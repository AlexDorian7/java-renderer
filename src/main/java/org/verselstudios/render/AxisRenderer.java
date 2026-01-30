package org.verselstudios.render;

import org.verselstudios.Main;
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
    private static final Vector4d YELLOW = new Vector4d(1,1,0,1);
    private static final Vector4d CYAN = new Vector4d(0,1,1,1);
    private static final Vector4d MAGENTA = new Vector4d(1,0,1,1);

    public AxisRenderer(boolean grid) {
        axisSystem = new RenderSystem(RenderSystem.RenderType.GL_LINES, ShaderRegister.getProgram("line"));
        axisSystem.begin();

        addLine(new Vector3d(-100, 0, 0), new Vector3d(100, 0, 0), RED, axisSystem);
        addLine(new Vector3d(0, -100, 0), new Vector3d(0, 100, 0), GREEN, axisSystem);
        addLine(new Vector3d(0, 0, -100), new Vector3d(0, 0, 100), BLUE, axisSystem);

        if (grid) {
            for (int i=-100; i<=100; i++) {
                if (i == 0) continue;
                addLine(new Vector3d(i, -100, 0), new Vector3d(i, 100, 0), YELLOW, axisSystem); // XY
                addLine(new Vector3d(-100, i, 0), new Vector3d(100, i, 0), YELLOW, axisSystem); // XY

                addLine(new Vector3d(i, 0, -100), new Vector3d(i, 0, 100), MAGENTA, axisSystem); // XZ
                addLine(new Vector3d(-100, 0, i), new Vector3d(100, 0, i), MAGENTA, axisSystem); // XZ

                addLine(new Vector3d(0, i, -100), new Vector3d(0, i, 100), CYAN, axisSystem); // YZ
                addLine(new Vector3d(0, -100, i), new Vector3d(0, 100, i), CYAN, axisSystem); // YZ
            }
        }

        axisSystem.end();
    }

    private static void addLine(Vector3d end, Vector4d color, RenderSystem system) {
        system.addVertex(system.getProgram().getVaoBuilder().getNewVertex().setData("position", 0f, 0f, 0f).setData("color", (float) color.getX(), (float) color.getY(), (float) color.getZ(), (float) color.getW()));
        system.addVertex(system.getProgram().getVaoBuilder().getNewVertex().setData("position", (float) end.getX(), (float) end.getY(), (float) end.getZ()).setData("color", (float) color.getX(), (float) color.getY(), (float) color.getZ(), (float) color.getW()));
    }

    private static void addLine(Vector3d start, Vector3d end, Vector4d color, RenderSystem system) {
        system.addVertex(system.getProgram().getVaoBuilder().getNewVertex().setData("position", (float) start.getX(), (float) start.getY(), (float) start.getZ()).setData("color", (float) color.getX(), (float) color.getY(), (float) color.getZ(), (float) color.getW()));
        system.addVertex(system.getProgram().getVaoBuilder().getNewVertex().setData("position", (float) end.getX(), (float) end.getY(), (float) end.getZ()).setData("color", (float) color.getX(), (float) color.getY(), (float) color.getZ(), (float) color.getW()));
    }

    @Override
    public void render() {
        glLineWidth(4f);
        glEnable(GL_DEPTH_TEST);
        axisSystem.draw(Main.getRenderManager().getRenderStack().getMatrixStack());
        glDisable(GL_DEPTH_TEST);
    }
}
