package org.verselstudios.model;

import org.joml.Vector3d;
import org.verselstudios.Main;
import org.verselstudios.light.PointLight;
import org.verselstudios.math.MatrixStack;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;
import org.verselstudios.shader.material.Material;

public class LitRenderSystem extends RenderSystem {
    protected final Material material;

    private static final PointLight DEFAULT_LIGHT = new PointLight(new Vector3d(0, 0,0), new Vector3d(0.25), new Vector3d(1), new Vector3d(1), new Vector3d(1, 0.09, 0.032));

    public LitRenderSystem(RenderType type, Material material) {
        super(type, ShaderRegister.getProgram("lit"));
        this.material = material;
    }

    @Override
    public void draw(MatrixStack matrixStack) {

        getProgram().use();
        getProgram().genLight(matrixStack.matrix());
        getProgram().setUniform3v(getProgram().getUniformLocation("viewPos"), Main.getRenderManager().getRenderStack().getCamera().getTransform().getPosition());
        getProgram().setMaterial("material", material);
        super.draw(matrixStack);
    }
}
