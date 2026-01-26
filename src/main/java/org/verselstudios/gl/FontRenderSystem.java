package org.verselstudios.gl;

import org.verselstudios.render.font.Font;

public class FontRenderSystem extends RenderSystem {
    public final Font font;
    public final Font.FontStyle style;

    public FontRenderSystem(RenderType type, Font font, Font.FontStyle style) {
        super(type);
        this.font = font;
        this.style = style;
    }
}
