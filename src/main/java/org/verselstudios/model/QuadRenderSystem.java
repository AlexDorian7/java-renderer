package org.verselstudios.model;

import org.verselstudios.math.Rectangle;
import org.verselstudios.math.Vector3d;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;
import org.verselstudios.shader.Vertex;

public class QuadRenderSystem {

    public static RenderSystem makeQuad(Rectangle rectangle) {
        Vector3d pos = new Vector3d(rectangle.getPos());
        Vector3d bound = new Vector3d(rectangle.getBound());

        ShaderProgram program = ShaderRegister.getProgram("position_color_tex");
        RenderSystem rs = new RenderSystem(RenderSystem.RenderType.GL_TRIANGLE_STRIP, program).begin();

        // vertices
        Vertex v0 = program.getVaoBuilder().getNewVertex().setData("position", (float) pos.getX(), (float) pos.getY(), 0f).setData("color", 1f, 1f, 1f, 1f).setData("texCoord", 0f, 0f);
        Vertex v1 = program.getVaoBuilder().getNewVertex().setData("position", (float) pos.getX(), (float) bound.getY(), 0f).setData("color", 1f, 1f, 1f, 1f).setData("texCoord", 0f, 1f);
        Vertex v2 = program.getVaoBuilder().getNewVertex().setData("position", (float) bound.getX(), (float) bound.getY(), 0f).setData("color", 1f, 1f, 1f, 1f).setData("texCoord", 1f, 1f);
        Vertex v3 = program.getVaoBuilder().getNewVertex().setData("position", (float) bound.getX(), (float) pos.getY(), 0f).setData("color", 1f, 1f, 1f, 1f).setData("texCoord", 1f, 0f);

        // triangle strip
        rs.addVertex(v1).addVertex(v0).addVertex(v2).addVertex(v3);

        return rs.end();
    }

    public static RenderPostSystem makePostQuad() {
        Rectangle rectangle = new Rectangle(-1, -1, 2, 2);
        Vector3d pos = new Vector3d(rectangle.getPos());
        Vector3d bound = new Vector3d(rectangle.getBound());

        ShaderProgram program = ShaderRegister.getProgram("postprocess");
        RenderPostSystem rs = (RenderPostSystem) new RenderPostSystem(RenderSystem.RenderType.GL_TRIANGLE_STRIP, program).begin();

        // vertices
        Vertex v0 = program.getVaoBuilder().getNewVertex().setData("position", (float) pos.getX(), (float) pos.getY(), 0f).setData("texCoord", 0f, 0f);
        Vertex v1 = program.getVaoBuilder().getNewVertex().setData("position", (float) pos.getX(), (float) bound.getY(), 0f).setData("texCoord", 0f, 1f);
        Vertex v2 = program.getVaoBuilder().getNewVertex().setData("position", (float) bound.getX(), (float) bound.getY(), 0f).setData("texCoord", 1f, 1f);
        Vertex v3 = program.getVaoBuilder().getNewVertex().setData("position", (float) bound.getX(), (float) pos.getY(), 0f).setData("texCoord", 1f, 0f);

        // triangle strip
        rs.addVertex(v1).addVertex(v0).addVertex(v2).addVertex(v3);

        return (RenderPostSystem) rs.end();
    }

}
