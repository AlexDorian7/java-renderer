package org.verselstudios.render;

import org.verselstudios.Image.Texture;
import org.verselstudios.gl.FontRenderSystem;
import org.verselstudios.gl.QuadRenderSystem;
import org.verselstudios.gl.RenderSystem;
import org.verselstudios.math.Matrix4d;
import org.verselstudios.math.Rectangle;
import org.verselstudios.math.Vector3d;
import org.verselstudios.render.font.Font;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;

import static org.lwjgl.opengl.GL45.*;

public abstract class Window implements Renderer {

    private final RenderSystem quad;
    private boolean renderBorder = true;

    private String windowName = "Window";

    private Rectangle bounds;

    private final Texture texture;

    private FontRenderSystem titleSystem;

    protected Window(Rectangle bounds) {
        this.bounds = bounds;
        texture = new Texture("assets/textures/border.png");
        quad = QuadRenderSystem.makeQuad(new Rectangle(1,1));
    }

    @Override
    public void render() {
        if (renderBorder) {
            quad.getProgram().use();
            glEnable(GL_TEXTURE_2D);
            Matrix4d translation = Matrix4d.translation(bounds.getPos().getX(), bounds.getPos().getY(), 0);
            Matrix4d transform = translation.multiply(Matrix4d.scale(bounds.getSize().getX(), bounds.getSize().getY(), 1));
            RenderStack.getMatrixStack().push(transform);
            texture.bind(quad.getProgram());
            quad.draw(RenderStack.getMatrixStack());
            RenderStack.getMatrixStack().pop();
            glDisable(GL_TEXTURE_2D);

            if (titleSystem != null) {
                Font.renderFontSystem(titleSystem, new Vector3d(bounds.getPos().getX() + 4, bounds.getBound().getY() - titleSystem.style.size() - 4, 0), RenderStack.getMatrixStack());
            }

//            Font.DEFAULT.renderString(new Vector3d(bounds.getPos().getX() + 4, bounds.getBound().getY() - Font.FontStyle.DEFAULT.size() - 4, 0), windowName);
        }
    }

    public boolean isRenderBorder() {
        return renderBorder;
    }

    protected void setRenderBorder(boolean renderBorder) {
        this.renderBorder = renderBorder;
    }

    public String getWindowName() {
        return windowName;
    }

    protected void setWindowName(String windowName) {
        this.windowName = windowName;
        if (titleSystem != null) {
            titleSystem.destroy();
        }
        titleSystem = Font.DEFAULT.createFontRenderSystem(windowName);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    protected void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
