package org.verselstudios.surface;

import org.verselstudios.gl.RenderSystem;
import org.verselstudios.gl.VertexBuilder;
import org.verselstudios.math.*;
import org.verselstudios.render.RenderStack;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;

import java.util.HashMap;
import java.util.Map;

public class SurfaceRenderSystem extends RenderSystem {

    private final Heightmap heightmap;

    static final double MAX_ERROR = 0.001;
    static final int MAX_DEPTH = 8;

    // Stores final emitted quads for LOD neighbor detection
    private final Map<Long, Integer> quadDepths = new HashMap<>();

    public SurfaceRenderSystem(Heightmap heightmap, int resolution) {
        super(RenderType.GL_TRIANGLES);
        this.heightmap = heightmap;

        begin();
        buildQuad(0.0, 0.0, 1.0, 0);
        end();
    }

    private boolean neighborTooCoarse(double nx, double nz, int depth) {
        for (int d = depth + 2; d <= MAX_DEPTH; d++) {
            double size = 1.0 / (1 << d);
            if (quadDepths.containsKey(quadKey(nx, nz, size))) {
                return true;
            }
        }
        return false;
    }


    /* ============================================================
       Quadtree construction
       ============================================================ */

    private void buildQuad(double x, double z, double size, int depth) {
        if (neighborTooCoarse(x - size, z, depth) ||
                neighborTooCoarse(x + size, z, depth) ||
                neighborTooCoarse(x, z - size, depth) ||
                neighborTooCoarse(x, z + size, depth) ||
                shouldSubdivide(x, z, size, depth)) {
            double h = size * 0.5;
            buildQuad(x, z, h, depth + 1);
            buildQuad(x + h, z, h, depth + 1);
            buildQuad(x, z + h, h, depth + 1);
            buildQuad(x + h, z + h, h, depth + 1);
            return;
        }

        quadDepths.put(quadKey(x, z, size), depth);
        emitQuad(x, z, size);
    }

    private boolean shouldSubdivide(double x, double z, double size, int depth) {
        if (depth >= MAX_DEPTH) return false;
        return computeError(x, z, size) > MAX_ERROR;
    }

    /* ============================================================
       Edge stitching
       ============================================================ */

    private void emitQuad(double x, double z, double size) {

        boolean stitchL = neighborIsCoarser(x - size, z, size);
        boolean stitchR = neighborIsCoarser(x + size, z, size);
        boolean stitchB = neighborIsCoarser(x, z - size, size);
        boolean stitchT = neighborIsCoarser(x, z + size, size);

        Vector3d p0 = samplePosition(x, z);
        Vector3d p1 = samplePosition(x, z + size);
        Vector3d p2 = samplePosition(x + size, z + size);
        Vector3d p3 = samplePosition(x + size, z);

        Vector3d pmL = samplePosition(x, z + size * 0.5);
        Vector3d pmR = samplePosition(x + size, z + size * 0.5);
        Vector3d pmB = samplePosition(x + size * 0.5, z);
        Vector3d pmT = samplePosition(x + size * 0.5, z + size);

        Vertex v0 = makeVertex(p0, x, z);
        Vertex v1 = makeVertex(p1, x, z + size);
        Vertex v2 = makeVertex(p2, x + size, z + size);
        Vertex v3 = makeVertex(p3, x + size, z);

        Vertex vL = makeVertex(pmL, x, z + size * 0.5);
        Vertex vR = makeVertex(pmR, x + size, z + size * 0.5);
        Vertex vB = makeVertex(pmB, x + size * 0.5, z);
        Vertex vT = makeVertex(pmT, x + size * 0.5, z + size);

        // Edge stitches
        if (stitchB) addVertex(v0).addVertex(v3).addVertex(vB);
        if (stitchT) addVertex(v1).addVertex(vT).addVertex(v2);
        if (stitchL) addVertex(v0).addVertex(vL).addVertex(v1);
        if (stitchR) addVertex(v3).addVertex(v2).addVertex(vR);

        // Interior fan
        addVertex(vB).addVertex(v3).addVertex(vR);
        addVertex(vB).addVertex(vR).addVertex(vT);
        addVertex(vB).addVertex(vT).addVertex(vL);
        addVertex(vB).addVertex(vL).addVertex(v0);
        addVertex(vL).addVertex(vT).addVertex(v1);
        addVertex(vT).addVertex(vR).addVertex(v2);
    }

    private boolean neighborIsCoarser(double nx, double nz, double size) {
        if (quadDepths.containsKey(quadKey(nx, nz, size))) return false;

        double parentSize = size * 2.0;
        double px = Math.floor(nx / parentSize) * parentSize;
        double pz = Math.floor(nz / parentSize) * parentSize;

        return quadDepths.containsKey(quadKey(px, pz, parentSize));
    }

    /* ============================================================
       Geometry helpers
       ============================================================ */

    private Vertex makeVertex(Vector3d pos, double x, double z) {
        Vector3d n = computeNormal(x, z, 0.001);
        Vector3d t = computeTangent(n);

        return new VertexBuilder()
                .setPosition(pos)
                .setTexCoord(new Vector2d(x, z))
                .setColor(Vector4d.ONE)
                .setNormal(n)
                .setTangent(t)
                .createVertex();
    }

    private Vector3d samplePosition(double x, double z) {
        double y = heightmap.get(new Vector2d(x, z));
        return new Vector3d(x - 0.5, y, z - 0.5);
    }

    private Vector3d computeNormal(double x, double z, double step) {
        double hL = heightmap.get(new Vector2d(x - step, z));
        double hR = heightmap.get(new Vector2d(x + step, z));
        double hD = heightmap.get(new Vector2d(x, z - step));
        double hU = heightmap.get(new Vector2d(x, z + step));

        Vector3d n = new Vector3d(
                hL - hR,
                2.0 * step,
                hD - hU
        );
        return n.normalize();
    }

    private Vector3d computeTangent(Vector3d normal) {
        Vector3d t = new Vector3d(1, 0, 0);
        return t.subtract(normal.multiply(normal.dot(t))).normalize();
    }

    private double computeError(double x, double z, double size) {
        double h00 = heightmap.get(new Vector2d(x, z));
        double h01 = heightmap.get(new Vector2d(x, z + size));
        double h11 = heightmap.get(new Vector2d(x + size, z + size));
        double h10 = heightmap.get(new Vector2d(x + size, z));

        double hc = heightmap.get(new Vector2d(x + size * 0.5, z + size * 0.5));
        return Math.abs(hc - (h00 + h01 + h11 + h10) * 0.25);
    }

    /* ============================================================
       Quad key
       ============================================================ */

    private long quadKey(double x, double z, double size) {
        long ix = Math.round(x / size);
        long iz = Math.round(z / size);
        long is = Math.round(1.0 / size);
        return ix | (iz << 21) | (is << 42);
    }

    /* ============================================================
       Draw
       ============================================================ */

    public void draw(MatrixStack matrixStack) {
        draw(heightmap.getProgram(), matrixStack);
    }

    @Override
    public void draw(ShaderProgram program, MatrixStack matrixStack) {
        program.use();
        Matrix4d transform =
                Matrix4d.rotationX(-Math.PI / 2)
                        .multiply(Matrix4d.scale(500, 1, 500));
        RenderStack.getMatrixStack().push(transform);
        super.draw(program, matrixStack);
        RenderStack.getMatrixStack().pop();
    }
}
