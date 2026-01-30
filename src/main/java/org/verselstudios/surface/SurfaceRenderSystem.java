package org.verselstudios.surface;

import org.verselstudios.gl.RenderSystem;
import org.verselstudios.math.*;
import org.verselstudios.shader.VaoBuilder;
import org.verselstudios.shader.Vertex;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL45.*;

public class SurfaceRenderSystem extends RenderSystem {

    private final Heightmap heightmap;

    static final double MAX_ERROR = 0.001;
    static final int MAX_DEPTH = 8;

    // Stores final emitted quads for LOD neighbor detection
    private final Map<Long, Integer> quadDepths = new HashMap<>();

    public SurfaceRenderSystem(Heightmap heightmap) {
        super(RenderType.GL_TRIANGLES, heightmap.getProgram());
        this.heightmap = heightmap;

        begin();
        buildQuad(0.0, 0.0, 1.0, 0);
        end();
    }


    /* ============================================================
       Quadtree construction
       ============================================================ */

    private void buildQuad(double x, double z, double size, int depth) {
        if (shouldSubdivide(x, z, size, depth)) {
            double h = size * 0.5;
            buildQuad(x, z, h, depth + 1);
            buildQuad(x + h, z, h, depth + 1);
            buildQuad(x, z + h, h, depth + 1);
            buildQuad(x + h, z + h, h, depth + 1);
            return;
        }
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

        Vector3d p0 = samplePosition(x, z);
        Vector3d p1 = samplePosition(x, z + size);
        Vector3d p2 = samplePosition(x + size, z + size);
        Vector3d p3 = samplePosition(x + size, z);

        VaoBuilder builder = getProgram().getVaoBuilder();

        Vertex v0 = makeVertex(builder, p0, x, z);
        Vertex v1 = makeVertex(builder, p1, x, z + size);
        Vertex v2 = makeVertex(builder, p2, x + size, z + size);
        Vertex v3 = makeVertex(builder, p3, x + size, z);


        // Interior fan

        addVertex(v0).addVertex(v1).addVertex(v2); // for some reason I need to use CW winding here for the front face to point up
        addVertex(v0).addVertex(v2).addVertex(v3);


    }


    /* ============================================================
       Geometry helpers
       ============================================================ */

    private Vertex makeVertex(VaoBuilder builder, Vector3d pos, double x, double z) {
        Vector3d n = computeNormal(x, z, 0.001);
        Vector3d t = computeTangent(n);
        Vector3d b = n.cross(t).normalize();

        return builder.getNewVertex().setData("position", (float) pos.getX(), (float) pos.getY(), (float) pos.getZ()).setData("color", 1f, 1f, 1f, 1f)
                .setData("texCoord", (float) x, (float) z).setData("normal", (float) n.getX(), (float) n.getY(), (float) n.getZ())
                .setData("tangent", (float) t.getX(), (float) t.getY(), (float) t.getZ()).setData("bitangent", (float) b.getX(), (float) b.getY(), (float) b.getZ());
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
       Draw
       ============================================================ */

    @Override
    public void draw(MatrixStack matrixStack) {
//        glEnable(GL_DEPTH_TEST);
        matrixStack.push(Matrix4d.scale(5,5,5));
        super.draw(matrixStack);
        matrixStack.pop();
//        glDisable(GL_DEPTH_TEST);

//        RenderStack.getMatrixStack().pop();
    }
}
