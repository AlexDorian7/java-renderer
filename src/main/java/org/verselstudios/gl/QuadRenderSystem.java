package org.verselstudios.gl;

import org.verselstudios.math.Rectangle;
import org.verselstudios.math.Vector2d;
import org.verselstudios.math.Vector3d;
import org.verselstudios.math.Vector4d;
import org.verselstudios.shader.ShaderProgram;

public class QuadRenderSystem {

    public static RenderSystem makeQuad(ShaderProgram program, Rectangle rectangle) {
        Vector3d pos = new Vector3d(rectangle.getPos());
        Vector3d bound = new Vector3d(rectangle.getBound());

        VertexBuilder vb = new VertexBuilder();
        RenderSystem rs = new RenderSystem(RenderSystem.RenderType.GL_TRIANGLE_STRIP, program).begin();

        // vertices
        RenderSystem.Vertex v0 = vb.setPosition(new Vector3d(pos.getX(), pos.getY(), 0)).setTexCoord(new Vector2d(0, 0)).setColor(Vector4d.ONE)
                .setNormal(new Vector3d(0, 0, 1)).setTangent(new Vector3d(1, 0, 0)).createVertex();
        RenderSystem.Vertex v1 = vb.setPosition(new Vector3d(pos.getX(), bound.getY(), 0)).setTexCoord(new Vector2d(0, 1)).setColor(Vector4d.ONE)
                .setNormal(new Vector3d(0, 0, 1)).setTangent(new Vector3d(1, 0, 0)).createVertex();
        RenderSystem.Vertex v2 = vb.setPosition(new Vector3d(bound.getX(), bound.getY(), 0)).setTexCoord(new Vector2d(1, 1)).setColor(Vector4d.ONE)
                .setNormal(new Vector3d(0, 0, 1)).setTangent(new Vector3d(1, 0, 0)).createVertex();
        RenderSystem.Vertex v3 = vb.setPosition(new Vector3d(bound.getX(), pos.getY(), 0)).setTexCoord(new Vector2d(1, 0)).setColor(Vector4d.ONE)
                .setNormal(new Vector3d(0, 0, 1)).setTangent(new Vector3d(1, 0, 0)).createVertex();

        // triangle strip
        rs.addVertex(v1).addVertex(v0).addVertex(v2).addVertex(v3);

        return rs.end();
    }

}
