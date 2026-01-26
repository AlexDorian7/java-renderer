package org.verselstudios.render;

import org.lwjgl.opengl.GL20;
import org.verselstudios.gl.GLHelper;
import org.verselstudios.Image.Image;
import org.verselstudios.Image.ImageUtils;
import org.verselstudios.math.Rectangle;
import org.verselstudios.math.Vector3d;
import org.verselstudios.render.font.Font;


public abstract class Window implements Renderer {

    private boolean renderBorder = true;

    private String windowName = "Window";

    private Rectangle bounds = new Rectangle(1, 1);

    private final int textureId;

    protected Window() {
        Image image = ImageUtils.loadImageFromResource("assets/textures/border.png");
        textureId = GLHelper.createGLTexture(image);
    }

    @Override
    public void render() {
        if (renderBorder) {
            GL20.glColor3d(1,1,1);
            GL20.glBindTexture(GL20.GL_TEXTURE_2D, textureId);
            GL20.glBegin(GL20.GL_QUADS);

            GL20.glTexCoord2d(0, 0);
            GL20.glVertex2d(bounds.getPos().getX(), bounds.getPos().getY());

            GL20.glTexCoord2d(0, 1);
            GL20.glVertex2d(bounds.getPos().getX(), bounds.getBound().getY());

            GL20.glTexCoord2d(1, 1);
            GL20.glVertex2d(bounds.getBound().getX(), bounds.getBound().getY());

            GL20.glTexCoord2d(1, 0);
            GL20.glVertex2d(bounds.getBound().getX(), bounds.getPos().getY());

            GL20.glEnd();

            Font.DEFAULT.renderString(new Vector3d(bounds.getPos().getX() + 4, bounds.getBound().getY() - Font.FontStyle.DEFAULT.size() - 4, 0), windowName);
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
    }

    public Rectangle getBounds() {
        return bounds;
    }

    protected void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
