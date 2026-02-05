package org.verselstudios.model;

import org.verselstudios.math.Rectangle;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;
import org.verselstudios.shader.Vertex;
import org.joml.Vector3d;

public class QuadRenderSystem {

    public static RenderSystem makeQuad(Rectangle rectangle) {
        Vector3d pos = new Vector3d(rectangle.getPos(), 0);
        Vector3d bound = new Vector3d(rectangle.getBound(), 0);

        ShaderProgram program = ShaderRegister.getProgram("position_color_tex");
        RenderSystem rs = new RenderSystem(RenderSystem.RenderType.GL_TRIANGLE_STRIP, program).begin();

        // vertices
        Vertex v0 = program.getVaoBuilder().getNewVertex().setData("position", (float) pos.x, (float) pos.y, 0f).setData("color", 1f, 1f, 1f, 1f).setData("texCoord", 0f, 0f);
        Vertex v1 = program.getVaoBuilder().getNewVertex().setData("position", (float) pos.x, (float) bound.y, 0f).setData("color", 1f, 1f, 1f, 1f).setData("texCoord", 0f, 1f);
        Vertex v2 = program.getVaoBuilder().getNewVertex().setData("position", (float) bound.x, (float) bound.y, 0f).setData("color", 1f, 1f, 1f, 1f).setData("texCoord", 1f, 1f);
        Vertex v3 = program.getVaoBuilder().getNewVertex().setData("position", (float) bound.x, (float) pos.y, 0f).setData("color", 1f, 1f, 1f, 1f).setData("texCoord", 1f, 0f);

        // triangle strip
        rs.addVertex(v1).addVertex(v0).addVertex(v2).addVertex(v3);

        return rs.end();
    }

    public static RenderPostSystem makePostQuad() {
        Rectangle rectangle = new Rectangle(-1, -1, 2, 2);
        Vector3d pos = new Vector3d(rectangle.getPos(), 0);
        Vector3d bound = new Vector3d(rectangle.getBound(), 0);

        ShaderProgram program = ShaderRegister.getProgram("postprocess");
        RenderPostSystem rs = (RenderPostSystem) new RenderPostSystem(RenderSystem.RenderType.GL_TRIANGLE_STRIP, program).begin();

        // vertices
        Vertex v0 = program.getVaoBuilder().getNewVertex().setData("position", (float) pos.x, (float) pos.y, 0f).setData("texCoord", 0f, 0f);
        Vertex v1 = program.getVaoBuilder().getNewVertex().setData("position", (float) pos.x, (float) bound.y, 0f).setData("texCoord", 0f, 1f);
        Vertex v2 = program.getVaoBuilder().getNewVertex().setData("position", (float) bound.x, (float) bound.y, 0f).setData("texCoord", 1f, 1f);
        Vertex v3 = program.getVaoBuilder().getNewVertex().setData("position", (float) bound.x, (float) pos.y, 0f).setData("texCoord", 1f, 0f);

        // triangle strip
        rs.addVertex(v1).addVertex(v0).addVertex(v2).addVertex(v3);

        return (RenderPostSystem) rs.end();
    }

}
