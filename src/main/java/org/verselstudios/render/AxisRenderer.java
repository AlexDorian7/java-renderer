package org.verselstudios.render;

import org.verselstudios.gl.RenderSystem;
import org.verselstudios.gl.VertexBuilder;
import org.verselstudios.math.Matrix4d;
import org.verselstudios.math.Vector2d;
import org.verselstudios.math.Vector3d;
import org.verselstudios.math.Vector4d;
import org.verselstudios.shader.ShaderRegister;

public class AxisRenderer implements Renderer {

    private final RenderSystem axisSystem;

    private static final Vector4d RED = new Vector4d(1,0,0,1);
    private static final Vector4d GREEN = new Vector4d(0,1,0,1);
    private static final Vector4d BLUE = new Vector4d(0,0,1,1);

    public AxisRenderer() {
        axisSystem = new RenderSystem(RenderSystem.RenderType.GL_LINES, ShaderRegister.LINE);
        axisSystem.begin();

        addLine(Vector3d.X, RED, axisSystem);
        addLine(Vector3d.Y, GREEN, axisSystem);
        addLine(Vector3d.Z, BLUE, axisSystem);

        axisSystem.end();
    }

    private static void addLine(Vector3d end, Vector4d color, RenderSystem system) {
        system.addVertex(new VertexBuilder().setPosition(Vector3d.ZERO).setColor(color).setNormal(Vector3d.ZERO).setTangent(Vector3d.ZERO).setTexCoord(Vector2d.ZERO).createVertex());
        system.addVertex(new VertexBuilder().setPosition(end).setColor(color).setNormal(Vector3d.ZERO).setTangent(Vector3d.ZERO).setTexCoord(Vector2d.ZERO).createVertex());
    }

    @Override
    public void render() {
//        RenderStack.getMatrixStack().push(Matrix4d.scale(200,200,200));
        axisSystem.draw(RenderStack.getMatrixStack());
//        RenderStack.getMatrixStack().pop();
    }
}
