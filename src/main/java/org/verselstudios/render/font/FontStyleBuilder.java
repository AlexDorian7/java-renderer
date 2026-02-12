package org.verselstudios.render.font;

import org.joml.Vector4d;

public class FontStyleBuilder {
    private double size = 1;
    private Vector4d color = new Vector4d(1);
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
        return new Font.FontStyle(size, color, italic, shadow, bold);
    }
}