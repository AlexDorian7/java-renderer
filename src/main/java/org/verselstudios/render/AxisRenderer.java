package org.verselstudios.render;

import org.verselstudios.Main;
import org.verselstudios.model.RenderSystem;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.verselstudios.shader.ShaderRegister;

import static org.lwjgl.opengl.GL45.*;

public class AxisRenderer implements Renderer {

    private final RenderSystem axisSystem;

    private static final Vector4d RED = new Vector4d(1,0,0,1);
    private static final Vector4d GREEN = new Vector4d(0,1,0,1);
    private static final Vector4d BLUE = new Vector4d(0,0,1,1);
    private static final Vector4d YELLOW = new Vector4d(1,1,0,0.125);
    private static final Vector4d CYAN = new Vector4d(0,1,1,0.125);
    private static final Vector4d MAGENTA = new Vector4d(1,0,1,0.125);

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
        system.addVertex(system.getProgram().getVaoBuilder().getNewVertex().setData("position", 0f, 0f, 0f).setData("color", (float) color.x, (float) color.y, (float) color.z, (float) color.w));
        system.addVertex(system.getProgram().getVaoBuilder().getNewVertex().setData("position", (float) end.x, (float) end.y, (float) end.z).setData("color", (float) color.x, (float) color.y, (float) color.z, (float) color.w));
    }

    private static void addLine(Vector3d start, Vector3d end, Vector4d color, RenderSystem system) {
        system.addVertex(system.getProgram().getVaoBuilder().getNewVertex().setData("position", (float) start.x, (float) start.y, (float) start.z).setData("color", (float) color.x, (float) color.y, (float) color.z, (float) color.w));
        system.addVertex(system.getProgram().getVaoBuilder().getNewVertex().setData("position", (float) end.x, (float) end.y, (float) end.z).setData("color", (float) color.x, (float) color.y, (float) color.z, (float) color.w));
    }

    @Override
    public void render() {
        glLineWidth(4f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        axisSystem.draw(Main.getRenderManager().getRenderStack().getMatrixStack());
        glDisable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
    }
}
