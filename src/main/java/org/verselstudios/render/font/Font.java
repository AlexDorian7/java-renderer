package org.verselstudios.render.font;

import org.verselstudios.Image.Texture;
import org.verselstudios.gl.FontRenderSystem;
import org.verselstudios.gl.RenderSystem;
import org.verselstudios.math.*;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;
import org.verselstudios.shader.Vertex;

import static org.lwjgl.opengl.GL45.*;

public class Font {

    public static final Font DEFAULT = new Font("assets/textures/font/ascii.png");
    public static final Font EMOJI = new Font("assets/textures/font/emoji.png");
    public static final Font PROGRESS = new Font("assets/textures/font/progress.png");

    private final Texture texture;
    private final String fontResource;

    public Font(String fontResource) {
        this.fontResource = fontResource;

        texture = new Texture(fontResource);
    }

    @Deprecated
    public void renderString(Vector3d position, String string) {
        renderString(position, string, FontStyle.DEFAULT);
    }

    @Deprecated
    public void renderString(Vector3d position, String string, FontStyle style) {
        int pos = 0;
        double fontSize = style.size;
        if (style.shadow) {
            Vector4d shadowColor = style.shadowColor;
            for (char c : string.toCharArray()) {
                double shadowPos = pos + 1/8D;
                if (c > 255) {
                    c = 0;
                }
                int yi = c/16;
                int xi = c%16;
                double x = xi/16D;
                double y  = 1D - (yi + 1) / 16D;
                double x1 = x + 1/16D;
                double y1 = y + 1/16D;

//                glEnable(GL_BLEND);
//                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//                glBindTexture(GL_TEXTURE_2D, texture.textureId());
//                glColor4d(shadowColor.getX(), shadowColor.getY(), shadowColor.getZ(), shadowColor.getW());
//                glBegin(GL_QUADS);
//
//                glTexCoord2d(x, y);
//                glVertex3d(position.getX() + shadowPos*fontSize, position.getY() - fontSize/8D, position.getZ());
//                glTexCoord2d(x, y1);
//                glVertex3d(position.getX() + shadowPos*fontSize + (style.italic ? fontSize/4D : 0), position.getY() + fontSize - fontSize/8D, position.getZ());
//                glTexCoord2d(x1, y1);
//                glVertex3d(position.getX() + shadowPos*fontSize + fontSize + (style.italic ? fontSize/4D : 0), position.getY() + fontSize - fontSize/8D, position.getZ());
//                glTexCoord2d(x1, y);
//                glVertex3d(position.getX() + shadowPos*fontSize + fontSize, position.getY() - fontSize/8D, position.getZ());
//                glEnd();
//
//                glDisable(GL_BLEND);

                pos++;
            }
        }
        pos = 0;
        for (char c : string.toCharArray()) {
            if (c > 255) {
                c = 0;
            }
            int yi = c/16;
            int xi = c%16;
            double x = xi/16D;
            double y  = 1D - (yi + 1) / 16D;
            double x1 = x + 1/16D;
            double y1 = y + 1/16D;

//            glEnable(GL_BLEND);
//            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//            glBindTexture(GL_TEXTURE_2D, texture.textureId());
//            glColor4d(style.color().getX(), style.color().getY(), style.color().getZ(), style.color().getW());
//            glBegin(GL_QUADS);
//
//            glTexCoord2d(x, y);
//            glVertex3d(position.getX() + pos*fontSize, position.getY(), position.getZ());
//            glTexCoord2d(x, y1);
//            glVertex3d(position.getX() + pos*fontSize + (style.italic ? fontSize/4D : 0), position.getY() + fontSize, position.getZ());
//            glTexCoord2d(x1, y1);
//            glVertex3d(position.getX() + pos*fontSize + fontSize + (style.italic ? fontSize/4D : 0), position.getY() + fontSize, position.getZ());
//            glTexCoord2d(x1, y);
//            glVertex3d(position.getX() + pos*fontSize + fontSize, position.getY(), position.getZ());
//            glEnd();
//
//            glDisable(GL_BLEND);

            pos++;
        }
    }

    @Deprecated
    public void renderWrappedString(
            Vector3d position,
            String text,
            double maxWidth,
            FontStyle style
    ) {
        double fontSize = style.size();
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        double cursorX = 0;
        double cursorY = 0;

        StringBuilder line = new StringBuilder();

        String[] words = text.split(" ");

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            double wordWidth = word.length() * fontSize;
            double spaceWidth = fontSize;

            boolean firstWord = line.isEmpty();

            // Would this word overflow the line?
            if ((!firstWord && cursorX + spaceWidth + wordWidth > maxWidth)) {
                // Render current line
                renderString(
                        new Vector3d(x, y - cursorY, z),
                        line.toString(),
                        style
                );

                // Start new line
                line.setLength(0);
                cursorX = 0;
                cursorY += fontSize;
                firstWord = true;
            }

            // Add space if needed
            if (!firstWord) {
                line.append(" ");
                cursorX += spaceWidth;
            }

            line.append(word);
            cursorX += wordWidth;
        }

        // ALWAYS render the final line
        if (!line.isEmpty()) {
            renderString(
                    new Vector3d(x, y - cursorY, z),
                    line.toString(),
                    style
            );
        }
    }

    public FontRenderSystem createFontRenderSystem(String string) {
        return createFontRenderSystem(string, FontStyle.DEFAULT);
    }

    public FontRenderSystem createFontRenderSystem(String string, FontStyle style) {
        if (string.isEmpty()) string = " "; // Should fix this later. this prevents empty RenderSystem
        ShaderProgram program = ShaderRegister.getProgram("pos_color_tex");
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

            Vertex vx0 = program.getVaoBuilder().getNewVertex().setData("position", (float) x, 0f, 0f).setData("texCoord", (float) u, (float) v).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());
            Vertex vx1 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x + italic), (float) y, 0f).setData("texCoord", (float) u, (float) v1).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());
            Vertex vx2 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x1 + italic), (float) y, 0f).setData("texCoord", (float) u1, (float) v1).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());
            Vertex vx3 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x1), 0f, 0f).setData("texCoord", (float) u1, (float) v).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());

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
        ShaderProgram program = ShaderRegister.getProgram("pos_color_tex");
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

                double x = posX;
                double y = posY;

                Vertex vx0 = program.getVaoBuilder().getNewVertex().setData("position", (float) x, (float) y, 0f).setData("texCoord", (float) u, (float) v).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());
                Vertex vx1 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x + (style.italic ? 0.25 : 0)), (float) (y+1), 0f).setData("texCoord", (float) u, (float) v1).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());
                Vertex vx2 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x + 1 + (style.italic ? 0.25 : 0)), (float) (y+1), 0f).setData("texCoord", (float) u1, (float) v1).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());
                Vertex vx3 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x+1), (float) y, 0f).setData("texCoord", (float) u1, (float) v).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());

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

        ShaderProgram program = ShaderRegister.getProgram("pos_color_tex");
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

            Vertex vx0 = program.getVaoBuilder().getNewVertex().setData("position", (float) x, 0f, 0f).setData("texCoord", (float) u, (float) v).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());
            Vertex vx1 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x + (style.italic ? 0.25 : 0)), 1f, 0f).setData("texCoord", (float) u, (float) v1).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());
            Vertex vx2 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x + 1 + (style.italic ? 0.25 : 0)), 1f, 0f).setData("texCoord", (float) u1, (float) v1).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());
            Vertex vx3 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x+1), 0f, 0f).setData("texCoord", (float) u1, (float) v).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());

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

            Vertex vx0 = program.getVaoBuilder().getNewVertex().setData("position", (float) x, 0f, 0f).setData("texCoord", (float) u, (float) v).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());
            Vertex vx1 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x + (style.italic ? 0.25 : 0)), 1f, 0f).setData("texCoord", (float) u, (float) v1).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());
            Vertex vx2 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x + 1 + (style.italic ? 0.25 : 0)), 1f, 0f).setData("texCoord", (float) u1, (float) v1).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());
            Vertex vx3 = program.getVaoBuilder().getNewVertex().setData("position", (float) (x+1), 0f, 0f).setData("texCoord", (float) u1, (float) v).setData("color", (float) style.color.getX(), (float) style.color.getY(), (float) style.color.getZ(), (float) style.color.getW());

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
        Matrix4d translate = Matrix4d.translation(position.getX(), position.getY(), position.getZ());
        Matrix4d transform = translate.multiply(system.style.getScaleMat());
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

    public record FontStyle(double size, Vector4d color, @Deprecated Vector4d shadowColor, boolean italic, @Deprecated boolean shadow, boolean bold) {
        public static final FontStyle DEFAULT = new FontStyleBuilder().setSize(1).build();

        public Matrix4d getScaleMat() {
            return Matrix4d.scale(size, size, size);
        }

    }

}
