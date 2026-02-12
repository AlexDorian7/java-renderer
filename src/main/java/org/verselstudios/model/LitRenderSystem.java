package org.verselstudios.model;

import org.joml.Matrix3d;
import org.joml.Vector3d;
import org.verselstudios.Main;
import org.verselstudios.light.Light;
import org.verselstudios.math.MatrixStack;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.material.Material;

public class LitRenderSystem extends RenderSystem {
    protected final Material material;

    private static final Light DEFAULT_LIGHT = new Light(new Vector3d(0, 0,0), new Vector3d(0.25), new Vector3d(1), new Vector3d(1));

    public LitRenderSystem(RenderType type, ShaderProgram program, Material material) {
        super(type, program);
        this.material = material;
    }

    @Override
    public void draw(MatrixStack matrixStack) {

        // matrixStack.matrix() gets current model to world matrix
//        Matrix3d normalMatrix = new Matrix3d();
//        matrixStack.matrix().normal(normalMatrix);
        getProgram().use();
        getProgram().genLight();
//        getProgram().setUniformMatrix(getProgram().getUniformLocation("normalMatrix"), false, normalMatrix);
        getProgram().setUniform3v(getProgram().getUniformLocation("viewPos"), Main.getRenderManager().getRenderStack().getCamera().getTransform().getPosition());
        getProgram().setMaterial("material", material);
        getProgram().setLight("light", DEFAULT_LIGHT);
        super.draw(matrixStack);
    }
}
