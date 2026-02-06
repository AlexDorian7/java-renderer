package org.verselstudios.model;

import org.joml.Vector3d;
import org.joml.Vector4d;
import org.verselstudios.shader.ShaderRegister;
import org.verselstudios.shader.Vertex;

public class BoxRenderSystem extends RenderSystem {
    public BoxRenderSystem(Vector3d radius) {
        super(RenderType.GL_TRIANGLES, ShaderRegister.getProgram("position_color_tex_normal_tangent"));

        Vector4d WHITE = new Vector4d(1);

        Vector3d p000 = new Vector3d(-radius.x, -radius.y, -radius.z);
        Vector3d p001 = new Vector3d(-radius.x, -radius.y,  radius.z);
        Vector3d p010 = new Vector3d(-radius.x,  radius.y, -radius.z);
        Vector3d p011 = new Vector3d(-radius.x,  radius.y,  radius.z);
        Vector3d p100 = new Vector3d( radius.x, -radius.y, -radius.z);
        Vector3d p101 = new Vector3d( radius.x, -radius.y,  radius.z);
        Vector3d p110 = new Vector3d( radius.x,  radius.y, -radius.z);
        Vector3d p111 = new Vector3d( radius.x,  radius.y,  radius.z);

        begin();

        // ---------------- SOUTH (+Z) ----------------
        addVertex(v(p001, WHITE, 0,0,  0,0,1,  1,0,0));
        addVertex(v(p101, WHITE, 1,0,  0,0,1,  1,0,0));
        addVertex(v(p111, WHITE, 1,1,  0,0,1,  1,0,0));

        addVertex(v(p001, WHITE, 0,0,  0,0,1,  1,0,0));
        addVertex(v(p111, WHITE, 1,1,  0,0,1,  1,0,0));
        addVertex(v(p011, WHITE, 0,1,  0,0,1,  1,0,0));


        // ---------------- NORTH (-Z) ----------------
        addVertex(v(p100, WHITE, 0,0,  0,0,-1, -1,0,0));
        addVertex(v(p000, WHITE, 1,0,  0,0,-1, -1,0,0));
        addVertex(v(p010, WHITE, 1,1,  0,0,-1, -1,0,0));

        addVertex(v(p100, WHITE, 0,0,  0,0,-1, -1,0,0));
        addVertex(v(p010, WHITE, 1,1,  0,0,-1, -1,0,0));
        addVertex(v(p110, WHITE, 0,1,  0,0,-1, -1,0,0));


        // ---------------- EAST (+X) ----------------
        addVertex(v(p101, WHITE, 0,0,  1,0,0,  0,0,-1));
        addVertex(v(p100, WHITE, 1,0,  1,0,0,  0,0,-1));
        addVertex(v(p110, WHITE, 1,1,  1,0,0,  0,0,-1));

        addVertex(v(p101, WHITE, 0,0,  1,0,0,  0,0,-1));
        addVertex(v(p110, WHITE, 1,1,  1,0,0,  0,0,-1));
        addVertex(v(p111, WHITE, 0,1,  1,0,0,  0,0,-1));


        // ---------------- WEST (-X) ----------------
        addVertex(v(p000, WHITE, 0,0,  -1,0,0, 0,0,1));
        addVertex(v(p001, WHITE, 1,0,  -1,0,0, 0,0,1));
        addVertex(v(p011, WHITE, 1,1,  -1,0,0, 0,0,1));

        addVertex(v(p000, WHITE, 0,0,  -1,0,0, 0,0,1));
        addVertex(v(p011, WHITE, 1,1,  -1,0,0, 0,0,1));
        addVertex(v(p010, WHITE, 0,1,  -1,0,0, 0,0,1));


        // ---------------- UP (+Y) ----------------
        addVertex(v(p011, WHITE, 0,0,  0,1,0,  1,0,0));
        addVertex(v(p111, WHITE, 1,0,  0,1,0,  1,0,0));
        addVertex(v(p110, WHITE, 1,1,  0,1,0,  1,0,0));

        addVertex(v(p011, WHITE, 0,0,  0,1,0,  1,0,0));
        addVertex(v(p110, WHITE, 1,1,  0,1,0,  1,0,0));
        addVertex(v(p010, WHITE, 0,1,  0,1,0,  1,0,0));


        // ---------------- DOWN (-Y) ----------------
        addVertex(v(p000, WHITE, 0,0,  0,-1,0, 1,0,0));
        addVertex(v(p100, WHITE, 1,0,  0,-1,0, 1,0,0));
        addVertex(v(p101, WHITE, 1,1,  0,-1,0, 1,0,0));

        addVertex(v(p000, WHITE, 0,0,  0,-1,0, 1,0,0));
        addVertex(v(p101, WHITE, 1,1,  0,-1,0, 1,0,0));
        addVertex(v(p001, WHITE, 0,1,  0,-1,0, 1,0,0));

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
