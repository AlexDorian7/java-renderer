package org.verselstudios.render;

import org.verselstudios.events.ActionType;
import org.verselstudios.events.CharacterEvent;
import org.verselstudios.events.KeyEvent;
import org.verselstudios.gl.FontRenderSystem;
import org.verselstudios.math.Rectangle;
import org.verselstudios.math.Vector3d;
import org.verselstudios.math.Vector4d;
import org.verselstudios.render.font.Font;
import org.verselstudios.render.font.FontStyleBuilder;
import org.verselstudios.shader.ShaderRegister;

import javax.swing.text.Style;

public class TypeWindow extends DraggableWindow {

    private String text;
    private FontRenderSystem system;

    public TypeWindow(String title) {
        this(title, new Rectangle(512, 512));
    }

    public TypeWindow(String title, Rectangle bounds) {
        super(bounds);
        this.text = "";
        setWindowName(title);
        system = Font.DEFAULT.createFontRenderSystem("");
    }

    @Override
    public void render() {
        super.render();
        Font.FontStyle style = system.style;
        //Font.DEFAULT.renderWrappedString(new Vector3d(getBounds().getPos().getX() + getBounds().getSize().getX()/16D, getBounds().getBound().getY() - style.size() - getBounds().getSize().getY()/8D, 0), text, getBounds().getSize().getX() - getBounds().getSize().getX()/8D, style);
        Font.renderFontSystem(system, new Vector3d(getBounds().getPos().getX() + getBounds().getSize().getX()/16D, getBounds().getBound().getY() - style.size() - getBounds().getSize().getY()/8D, 0), ShaderRegister.CORE);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        system.destroy();
        system = Font.DEFAULT.createFontRenderSystem(text);
    }

    @Override
    public ActionType onCharacter(CharacterEvent event) {
        text += event.character();
        system.destroy();
        system = Font.DEFAULT.createFontRenderSystem(text);
        return ActionType.CONSUME;
    }

    @Override
    public ActionType onKeyPress(KeyEvent event) {
        if (event.key() == 259 && event.isPressed() && !text.isEmpty()) {
            text = text.substring(0, text.length() - 1);
            system.destroy();
            system = Font.DEFAULT.createFontRenderSystem(text);
        }
        return super.onKeyPress(event);
    }
}
