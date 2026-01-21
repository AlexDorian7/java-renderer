package org.verselstudios.render.font;

import org.verselstudios.math.Vector4d;

public class FontStyleBuilder {
    private double size = 32;
    private Vector4d color = new Vector4d(1, 1, 1, 1);
    private Vector4d shadowColor = color.divide(2);
    private boolean italic = false;
    private boolean shadow = false;
    private boolean bold = false;

    public FontStyleBuilder setSize(double size) {
        this.size = size;
        return this;
    }

    public FontStyleBuilder setColor(Vector4d color) {
        this.color = color;
        return this;
    }

    public FontStyleBuilder setShadowColor(Vector4d shadowColor) {
        this.shadowColor = shadowColor;
        return this;
    }

    public FontStyleBuilder setItalic(boolean italic) {
        this.italic = italic;
        return this;
    }

    public FontStyleBuilder setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public FontStyleBuilder setBold(boolean bold) {
        this.bold = bold;
        return this;
    }

    public Font.FontStyle build() {
        return new Font.FontStyle(size, color, shadowColor, italic, shadow, bold);
    }
}