package org.verselstudios.render.font;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.verselstudios.Image.Texture;
import org.verselstudios.math.MatrixStack;
import org.verselstudios.model.FontRenderSystem;
import org.verselstudios.model.RenderSystem;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;
import org.verselstudios.shader.Vertex;

import static org.lwjgl.opengl.GL45.*;

public class Font {

    private static final Logger LOGGER = LogManager.getLogger(Font.class);

    public static final Font DEFAULT = new Font("assets/textures/font/ascii.png");
    public static final Font EMOJI = new Font("assets/textures/font/emoji.png");
    public static final Font PROGRESS = new Font("assets/textures/font/progress.png");

    private final Texture texture;
    private final String fontResource;

    public Font(String fontResource) {
        this.fontResource = fontResource;

        texture = new Texture(fontResource);
    }

    public FontRenderSystem createFontRenderSystem(String string) {
        return createFontRenderSystem(string, FontStyle.DEFAULT);
    }

    public FontRenderSystem createFontRenderSystem(String string, FontStyle style) {
//        LOGGER.debug("Creating Font Render System for: " + string);
        if (string.isEmpty()) string = " "; // Should fix this later. this prevents empty RenderSystem
        ShaderProgram program = ShaderRegister.getProgram("font");
        FontRenderSystem system = new FontRenderSystem(RenderSystem.RenderType.GL_TRIANGLES, program, this, style);
        system.begin();
        int pos = 0;
        for (char c : string.toCharArray()) {
            if (c > 255) {
                c = 0;
            }
            int vi = c/16;
            int ui = c%16;
            double u = ui/16D;
            double v  = 1D - (vi + 1) / 16D;
            double u1 = u + 1/16D;
            double v1 = v + 1/16D;

            double x = pos * style.size;
            double x1 = x + style.size;
            double y = style.size;
            double italic = style.italic ? style.size * 0.25 : 0;

            Vertex vx0 = program.getVaoBuilder().getNewVertex().setData("position", (float) x, 0f, 0f).setData("texCoord", (float) u, (float) v).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);
            Vertex vx1 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x + italic), (float) y, 0f).setData("texCoord", (float) u, (float) v1).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);
            Vertex vx2 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x1 + italic), (float) y, 0f).setData("texCoord", (float) u1, (float) v1).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);
            Vertex vx3 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x1), 0f, 0f).setData("texCoord", (float) u1, (float) v).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);

            // tri 1
            system.addVertex(vx0).addVertex(vx2).addVertex(vx1);
            // tri 2
            system.addVertex(vx0).addVertex(vx3).addVertex(vx2);

            pos++;
        }
        system.end();
        return system;
    }

    public FontRenderSystem createFontRenderSystem(String string, FontStyle style, int maxWidth) {
        if (string.isEmpty()) string = " ";
        ShaderProgram program = ShaderRegister.getProgram("position_color_tex");
        FontRenderSystem system = new FontRenderSystem(RenderSystem.RenderType.GL_TRIANGLES, program, this, style);
        system.begin();

        int posX = 0;
        int posY = 0;

        // Split input into words while keeping line feeds
        int i = 0;
        while (i < string.length()) {
            // Handle line feed immediately
            if (string.charAt(i) == '\n') {
                posX = 0;
                posY -= 1;
                i++;
                continue;
            }

            // Find next word or space
            int wordEnd = i;
            while (wordEnd < string.length() && string.charAt(wordEnd) != ' ' && string.charAt(wordEnd) != '\n') {
                wordEnd++;
            }
            int wordLength = wordEnd - i;

            // Wrap word if it doesn't fit
            if (posX + wordLength > maxWidth) {
                posX = 0;
                posY -= 1;
            }

            // Render each character in the word
            for (int j = i; j < wordEnd; j++) {
                char c = string.charAt(j);
                if (c > 255) c = 0;

                int vi = c / 16;
                int ui = c % 16;
                double u = ui / 16D;
                double v  = 1D - (vi + 1) / 16D;
                double u1 = u + 1 / 16D;
                double v1 = v + 1 / 16D;

                double x = posX * style.size;
                double x1 = x + style.size;
                double y = posY * style.size;
                double y1 = y + style.size;
                double italic = style.italic ? style.size * 0.25 : 0;

                Vertex vx0 = program.getVaoBuilder().getNewVertex().setData("position", (float) x, (float) y, 0f).setData("texCoord", (float) u, (float) v).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);
                Vertex vx1 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x + italic), (float) y1, 0f).setData("texCoord", (float) u, (float) v1).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);
                Vertex vx2 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x1 + italic), (float) y1, 0f).setData("texCoord", (float) u1, (float) v1).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);
                Vertex vx3 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x1), (float) y, 0f).setData("texCoord", (float) u1, (float) v).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);

                // tri 1
                system.addVertex(vx0).addVertex(vx2).addVertex(vx1);
                // tri 2
                system.addVertex(vx0).addVertex(vx3).addVertex(vx2);

                posX++;
            }

            // Skip space after word
            if (wordEnd < string.length() && string.charAt(wordEnd) == ' ') {
                posX++;
            }

            i = wordEnd + 1;
        }

        system.end();
        return system;
    }


    public static FontRenderSystem createProgressBar(int width, double value, Vector4d progressColor) {

        if (width < 2) width = 2;

        Font font = PROGRESS;
        FontStyle style = FontStyle.DEFAULT;

        StringBuilder borderBuilder = new StringBuilder();

        borderBuilder.append('\u0000');
        for (int i=0; i< width-2; i++) {
            borderBuilder.append('\u0001');
        }
        borderBuilder.append('\u0002');

        StringBuilder progressBuilder = new StringBuilder();
        double val1 = value*width;
        int i = (int) val1;
        double f = val1%1;
        if (f < 0.00001) f = 0;
        for (int j=0; j<i; j++) {
            progressBuilder.append('\u000F');
        }
        if (f != 0) {
            int k = 8+((int) (f*8));
            progressBuilder.append(8+k);
        }

        ShaderProgram program = ShaderRegister.getProgram("position_color_tex");
        FontRenderSystem system = new FontRenderSystem(RenderSystem.RenderType.GL_TRIANGLES, program, font, style);
        system.begin();
        int pos = 0;
        for (char c : progressBuilder.toString().toCharArray()) {
            if (c > 255) {
                c = 0;
            }
            int vi = c/16;
            int ui = c%16;
            double u = ui/16D;
            double v  = 1D - (vi + 1) / 16D;
            double u1 = u + 1/16D;
            double v1 = v + 1/16D;

            double x = pos;

            Vertex vx0 = program.getVaoBuilder().getNewVertex().setData("position", (float) x, 0f, 0f).setData("texCoord", (float) u, (float) v).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);
            Vertex vx1 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x + (style.italic ? 0.25 : 0)), 1f, 0f).setData("texCoord", (float) u, (float) v1).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);
            Vertex vx2 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x + 1 + (style.italic ? 0.25 : 0)), 1f, 0f).setData("texCoord", (float) u1, (float) v1).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);
            Vertex vx3 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x+1), 0f, 0f).setData("texCoord", (float) u1, (float) v).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);

            // tri 1
            system.addVertex(vx0).addVertex(vx2).addVertex(vx1);
            // tri 2
            system.addVertex(vx0).addVertex(vx3).addVertex(vx2);

            pos++;
        }

        pos = 0;
        for (char c : borderBuilder.toString().toCharArray()) {
            if (c > 255) {
                c = 0;
            }
            int vi = c/16;
            int ui = c%16;
            double u = ui/16D;
            double v  = 1D - (vi + 1) / 16D;
            double u1 = u + 1/16D;
            double v1 = v + 1/16D;

            double x = pos;

            Vertex vx0 = program.getVaoBuilder().getNewVertex().setData("position", (float) x, 0f, 0f).setData("texCoord", (float) u, (float) v).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);
            Vertex vx1 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x + (style.italic ? 0.25 : 0)), 1f, 0f).setData("texCoord", (float) u, (float) v1).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);
            Vertex vx2 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x + 1 + (style.italic ? 0.25 : 0)), 1f, 0f).setData("texCoord", (float) u1, (float) v1).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);
            Vertex vx3 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x+1), 0f, 0f).setData("texCoord", (float) u1, (float) v).setData("color", (float) style.color.x, (float) style.color.y, (float) style.color.z, (float) style.color.w);

            // tri 1
            system.addVertex(vx0).addVertex(vx2).addVertex(vx1);
            // tri 2
            system.addVertex(vx0).addVertex(vx3).addVertex(vx2);

            pos++;
        }
        system.end();
        return system;
    }


    @Deprecated
    public static void renderFontSystem(FontRenderSystem system, Vector3d position, MatrixStack matrixStack) {
        system.getProgram().use();
        Matrix4d translate = new Matrix4d().translate(position.x, position.y, position.z);
        Matrix4d transform = translate.mul(system.style.getScaleMat());
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D);
        system.font.texture.bind(system.getProgram());
        matrixStack.push(transform);
        system.draw(matrixStack);
        matrixStack.pop();
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }

    public static void renderFontSystem(FontRenderSystem system, MatrixStack matrixStack) {
        system.getProgram().use();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        system.font.texture.bind(system.getProgram());
        system.draw(matrixStack);
        glDisable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_TEXTURE_2D);
    }

    public record FontStyle(double size, Vector4d color, boolean italic, @Deprecated boolean shadow, boolean bold) {
        public static final FontStyle DEFAULT = new FontStyleBuilder().setSize(1).build();

        public Matrix4d getScaleMat() {
            return new Matrix4d().scale(size);
        }

    }

}
