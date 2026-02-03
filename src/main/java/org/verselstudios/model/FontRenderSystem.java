package org.verselstudios.model;

import org.verselstudios.render.font.Font;
import org.verselstudios.shader.ShaderProgram;

public class FontRenderSystem extends RenderSystem {
    public final Font font;
    public final Font.FontStyle style;

    public FontRenderSystem(RenderType type, ShaderProgram program, Font font, Font.FontStyle style) {
        super(type, program);
        this.font = font;
        this.style = style;
    }
}
