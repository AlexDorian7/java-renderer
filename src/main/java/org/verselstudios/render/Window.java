package org.verselstudios.render;

import org.verselstudios.Image.Texture;
import org.verselstudios.Main;
import org.verselstudios.model.FontRenderSystem;
import org.verselstudios.model.QuadRenderSystem;
import org.verselstudios.model.RenderSystem;
import org.joml.Matrix4d;
import org.verselstudios.math.Rectangle;
import org.joml.Vector3d;
import org.verselstudios.render.font.Font;

import static org.lwjgl.opengl.GL45.*;

@Deprecated
/**
 * Does not work well in 3D.
 * A new 3D version will be made eventually
 */
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
            Matrix4d transform = bounds.getTransform().getModelMatrix();
            Main.getRenderManager().getRenderStack().getMatrixStack().push(transform);
            texture.bind(quad.getProgram());
//            glEnable(GL_DEPTH_TEST);
            quad.draw(Main.getRenderManager().getRenderStack().getMatrixStack());
//            glDisable(GL_DEPTH_TEST);
            if (titleSystem != null) {
                Font.renderFontSystem(titleSystem, new Vector3d(bounds.getPos().x + 4, bounds.getBound().y - titleSystem.style.size() - 4, 0), Main.getRenderManager().getRenderStack().getMatrixStack());
            }
            Main.getRenderManager().getRenderStack().getMatrixStack().pop();
            glDisable(GL_TEXTURE_2D);

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
