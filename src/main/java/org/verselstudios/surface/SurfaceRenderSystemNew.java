package org.verselstudios.surface;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Matrix4d;
import org.verselstudios.math.MatrixStack;
import org.verselstudios.model.RenderSystem;
import org.verselstudios.shader.VaoBuilder;
import org.verselstudios.shader.Vertex;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL45.*;

public class SurfaceRenderSystemNew extends RenderSystem {

    private final Heightmap heightmap;
    private final double scale;

    static final double MAX_ERROR = 0.001;
    static final int MAX_DEPTH = 8;

    // Stores final emitted quads for LOD neighbor detection
    private final Map<Long, Integer> quadDepths = new HashMap<>();

    public SurfaceRenderSystemNew(Heightmap heightmap, int resolution, double scale) {
        super(RenderType.GL_TRIANGLES, heightmap.getProgram());
        this.heightmap = heightmap;
        this.scale = scale;

        double res = 1D/resolution;

        begin();
        for (double x=0; x<1; x+=res) {
            for (double y=0; y<1; y+=res) {
                emitQuad(x, y, res);
            }
        }
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

        return builder.getNewVertex().setData("position", (float) pos.x, (float) pos.y, (float) pos.z).setData("color", 1f, 1f, 1f, 1f)
                .setData("texCoord", (float) x, (float) z).setData("normal", (float) n.x, (float) n.y, (float) n.z)
                .setData("tangent", (float) t.x, (float) t.y, (float) t.z).setData("bitangent", (float) b.x, (float) b.y, (float) b.z);
    }

    private Vector3d samplePosition(double x, double z) {
        double y = heightmap.get(new Vector2d(x*scale, z*scale));
        return new Vector3d(x - 0.5, y, z - 0.5);
    }

    private Vector3d computeNormal(double x, double z, double step) {
        double hL = heightmap.get(new Vector2d(x - step, z).mul(scale));
        double hR = heightmap.get(new Vector2d(x + step, z).mul(scale));
        double hD = heightmap.get(new Vector2d(x, z - step).mul(scale));
        double hU = heightmap.get(new Vector2d(x, z + step).mul(scale));

        Vector3d n = new Vector3d(
                hL - hR,
                2.0 * step,
                hD - hU
        );
        return n.normalize();
    }

    private Vector3d computeTangent(Vector3d normal) {
        Vector3d t = new Vector3d(1, 0, 0);
        return t.sub(normal.mul(normal.dot(t))).normalize();
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
        glEnable(GL_DEPTH_TEST);
        matrixStack.push(new Matrix4d().scale(scale, 1, scale));
        super.draw(matrixStack);
        matrixStack.pop();
        glDisable(GL_DEPTH_TEST);
    }
}
