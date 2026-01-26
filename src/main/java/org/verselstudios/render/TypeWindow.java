package org.verselstudios.render;

import org.verselstudios.events.ActionType;
import org.verselstudios.events.CharacterEvent;
import org.verselstudios.events.KeyEvent;
import org.verselstudios.math.Rectangle;
import org.verselstudios.math.Vector3d;
import org.verselstudios.math.Vector4d;
import org.verselstudios.render.font.Font;
import org.verselstudios.render.font.FontStyleBuilder;

public class TypeWindow extends DraggableWindow {

    private String text;

    public TypeWindow(String title) {
        this(title, new Rectangle(512, 512));
    }

    public TypeWindow(String title, Rectangle bounds) {
        super();
        this.text = "";
        setWindowName(title);
        setBounds(bounds);
    }

    @Override
    public void render() {
        super.render();
        Font.FontStyle style = new FontStyleBuilder().setColor(new Vector4d(0,0,0,1)).setSize(16).build();
        Font.DEFAULT.renderWrappedString(new Vector3d(getBounds().getPos().getX() + getBounds().getSize().getX()/16D, getBounds().getBound().getY() - style.size() - getBounds().getSize().getY()/8D, 0), text, getBounds().getSize().getX() - getBounds().getSize().getX()/8D, style);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public ActionType onCharacter(CharacterEvent event) {
        text += event.character();
        return ActionType.CONSUME;
    }

    @Override
    public ActionType onKeyPress(KeyEvent event) {
        if (event.key() == 259 && event.isPressed() && !text.isEmpty())
            text = text.substring(0, text.length()-1);
        return super.onKeyPress(event);
    }
}
