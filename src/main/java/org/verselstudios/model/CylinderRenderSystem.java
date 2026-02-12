package org.verselstudios.model;

import org.joml.Vector3d;
import org.joml.Vector4d;
import org.verselstudios.shader.ShaderRegister;
import org.verselstudios.shader.Vertex;
import org.verselstudios.shader.material.Material;

import java.util.ArrayList;

public class CylinderRenderSystem extends LitRenderSystem {
    public CylinderRenderSystem(double radius, double halfHeight, int steps, Material material) {
        super(RenderType.GL_TRIANGLES, ShaderRegister.getProgram("lit"), material);

        Vector4d WHITE = new Vector4d(1);

        ArrayList<Vector3d> positions = new ArrayList<>();

        double tauSteps = Math.TAU/steps;

        for (int i=0; i<steps; i++) {
            double x = Math.cos(i*tauSteps)*radius;
            double z = Math.sin(i*tauSteps)*radius;

            positions.add(new Vector3d(x, -halfHeight, z));
            positions.add(new Vector3d(x, halfHeight, z));
        }

        begin();

        Vector3d topCenter = new Vector3d(0,  halfHeight, 0);
        Vector3d botCenter = new Vector3d(0, -halfHeight, 0);

        double edgeWidth = 2 * radius * Math.tan(Math.PI / steps);
        double height =  halfHeight*2;

        double factor = 0.125 / Math.max(edgeWidth, height); // make fit in a 0.25 x 0.25 square
        edgeWidth *= factor;
        height *= factor;


        for (int i = 0; i < steps; i++) {

            int next = (i + 1) % steps;

            Vector3d b0 = positions.get(i * 2);
            Vector3d t0 = positions.get(i * 2 + 1);
            Vector3d b1 = positions.get(next * 2);
            Vector3d t1 = positions.get(next * 2 + 1);

            // ----- SIDE NORMAL -----
            Vector3d edge = new Vector3d(b0.x, 0, b0.z).normalize();
            float nx = (float) edge.x;
            float ny = 0f;
            float nz = (float) edge.z;

            // Tangent = rotate normal 90 degrees around Y
            float tx = -nz;
            float ty = 0f;
            float tz = nx;

            float u0 = (float) i / steps;
            float u1 = (float) next / steps;

            // =====================================
            // SIDE QUAD (2 TRIANGLES)
            // =====================================

            addVertex(v(b0, WHITE, 0, 1, nx, ny, nz, tx, ty, tz));
            addVertex(v(t0, WHITE, 0, (float) (1-height), nx, ny, nz, tx, ty, tz));
            addVertex(v(t1, WHITE, (float) edgeWidth, (float) (1-height), nx, ny, nz, tx, ty, tz));

            addVertex(v(b0, WHITE, 0, 1, nx, ny, nz, tx, ty, tz));
            addVertex(v(t1, WHITE, (float) edgeWidth, (float) (1-height), nx, ny, nz, tx, ty, tz));
            addVertex(v(b1, WHITE, (float) edgeWidth, 1, nx, ny, nz, tx, ty, tz));

            // =====================================
            // TOP CAP
            // Normal = (0,1,0)
            // =====================================

            float x0 = (float) (t0.x/radius/2+0.5);
            float x1 = (float) (t1.x/radius/2+0.5);
            float z0 = (float) (t0.z/radius/2+0.5);
            float z1 = (float) (t1.z/radius/2+0.5);

            addVertex(v(topCenter, WHITE, 0.5f, 0.5f, 0, 1, 0, 1, 0, 0));
            addVertex(v(t1, WHITE, x1, z1, 0, 1, 0, 1, 0, 0));
            addVertex(v(t0, WHITE, x0, z0, 0, 1, 0, 1, 0, 0));

            // =====================================
            // BOTTOM CAP
            // Normal = (0,-1,0)
            // Reverse winding!
            // =====================================

            addVertex(v(botCenter, WHITE, 0.5f, 0.5f, 0, -1, 0, 1, 0, 0));
            addVertex(v(b0, WHITE, x0, z0, 0, -1, 0, 1, 0, 0));
            addVertex(v(b1, WHITE, x1, z1, 0, -1, 0, 1, 0, 0));
        }

        end();
    }

    private Vertex v(Vector3d pos, Vector4d col,
                     float u, float v,
                     float nx, float ny, float nz,
                     float tx, float ty, float tz) {

        return getProgram().getVaoBuilder().getNewVertex()
                .setData("position", pos)
                .setData("color", col)
                .setData("texCoord", u, v)
                .setData("normal", nx, ny, nz)
                .setData("tangent", tx, ty, tz);
    }
}
