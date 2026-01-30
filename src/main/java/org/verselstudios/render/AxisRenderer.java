package org.verselstudios.render;

import org.verselstudios.gl.RenderSystem;
import org.verselstudios.math.Vector3d;
import org.verselstudios.math.Vector4d;
import org.verselstudios.shader.ShaderRegister;

import static org.lwjgl.opengl.GL45.*;

public class AxisRenderer implements Renderer {

    private final RenderSystem axisSystem;

    private static final Vector4d RED = new Vector4d(1,0,0,1);
    private static final Vector4d GREEN = new Vector4d(0,1,0,1);
    private static final Vector4d BLUE = new Vector4d(0,0,1,1);

    public AxisRenderer() {
        axisSystem = new RenderSystem(RenderSystem.RenderType.GL_LINES, ShaderRegister.getProgram("line"));
        axisSystem.begin();

        addLine(Vector3d.X, RED, axisSystem);
        addLine(Vector3d.Y, GREEN, axisSystem);
        addLine(Vector3d.Z, BLUE, axisSystem);

        axisSystem.end();
    }

    private static void addLine(Vector3d end, Vector4d color, RenderSystem system) {
        system.addVertex(system.getProgram().getVaoBuilder().getNewVertex().setData("position", 0f, 0f, 0f).setData("color", (float) color.getX(), (float) color.getY(), (float) color.getZ(), (float) color.getW()));
        system.addVertex(system.getProgram().getVaoBuilder().getNewVertex().setData("position", (float) end.getX(), (float) end.getY(), (float) end.getZ()).setData("color", (float) color.getX(), (float) color.getY(), (float) color.getZ(), (float) color.getW()));
    }

    @Override
    public void render() {
        glLineWidth(4f);
        axisSystem.draw(RenderStack.getMatrixStack());
    }
}
