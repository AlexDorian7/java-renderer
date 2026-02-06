package org.verselstudios.model;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.verselstudios.math.MatrixStack;
import org.verselstudios.math.Transform;
import org.verselstudios.shader.ShaderRegister;

import static org.lwjgl.opengl.GL45.*;

public class TransformRenderSystem extends RenderSystem {

    private static final Vector4d RED = new Vector4d(1,0,0,1);
    private static final Vector4d GREEN = new Vector4d(0,1,0,1);
    private static final Vector4d BLUE = new Vector4d(0,0,1,1);
    private static final Vector4d YELLOW = new Vector4d(1,1,0,0.125);
    private static final Vector4d CYAN = new Vector4d(0,1,1,0.125);
    private static final Vector4d MAGENTA = new Vector4d(1,0,1,0.125);

    public TransformRenderSystem(Transform transform) {
        super(RenderType.GL_LINES, ShaderRegister.getProgram("line"));
        begin();

        addLine(new Vector3d(0,0,0), new Vector3d(transform.getPosition().x, 0, 0), RED, this);
        addLine(new Vector3d(transform.getPosition().x,0,0), new Vector3d(transform.getPosition().x, transform.getPosition().y, 0), GREEN, this);
        addLine(new Vector3d(transform.getPosition().x,transform.getPosition().y,0), new Vector3d(transform.getPosition()), BLUE, this);

        Matrix4d rot = new Matrix4d();

        transform.getRotation().get(rot);

        Vector3d v = new Vector3d();
        rot.getColumn(0, v);
        v.add(transform.getPosition());
        addLine(transform.getPosition(), v, RED, this);
        rot.getColumn(1, v);
        v.add(transform.getPosition());
        addLine(transform.getPosition(), v, GREEN, this);
        rot.getColumn(2, v);
        v.add(transform.getPosition());
        addLine(transform.getPosition(), v, BLUE, this);

        end();
    }

    private static void addLine(Vector3d start, Vector3d end, Vector4d color, RenderSystem system) {
        system.addVertex(system.getProgram().getVaoBuilder().getNewVertex().setData("position", (float) start.x, (float) start.y, (float) start.z).setData("color", (float) color.x, (float) color.y, (float) color.z, (float) color.w));
        system.addVertex(system.getProgram().getVaoBuilder().getNewVertex().setData("position", (float) end.x, (float) end.y, (float) end.z).setData("color", (float) color.x, (float) color.y, (float) color.z, (float) color.w));
    }

    @Override
    public void draw(MatrixStack matrixStack) {
        glLineWidth(4);
        super.draw(matrixStack);
    }
}
