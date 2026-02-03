package org.verselstudios.render;

import org.verselstudios.Main;
import org.verselstudios.model.FontRenderSystem;
import org.verselstudios.math.Rectangle;
import org.verselstudios.math.Vector3d;
import org.verselstudios.render.font.Font;

public class TextWindow extends DraggableWindow {

    private String text;
    private FontRenderSystem system;

    public TextWindow(String title, String text) {
        this(title, text, new Rectangle(512, 512));
    }

    public TextWindow(String title, String text, Rectangle bounds) {
        super(bounds);
        this.text = text;
        setWindowName(title);
        system = Font.DEFAULT.createFontRenderSystem(this.text, Font.FontStyle.DEFAULT, 16);
    }

    @Override
    public void render() {
        super.render();
        Font.FontStyle style = system.style;
//        Font.DEFAULT.renderWrappedString(new Vector3d(getBounds().getPos().getX() + getBounds().getSize().getX()/16D, getBounds().getBound().getY() - style.size() - getBounds().getSize().getY()/8D, 0), text, getBounds().getSize().getX() - getBounds().getSize().getX()/8D, style);
        Font.renderFontSystem(system, new Vector3d(getBounds().getPos().getX() + getBounds().getSize().getX()/16D, getBounds().getBound().getY() - style.size() - getBounds().getSize().getY()/8D, 0), Main.getRenderManager().getRenderStack().getMatrixStack());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        system.destroy();
        system = Font.DEFAULT.createFontRenderSystem(text, Font.FontStyle.DEFAULT, 16);
    }
}
