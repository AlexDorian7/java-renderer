package org.verselstudios.render.font;

import org.lwjgl.opengl.GL20;
import org.verselstudios.Image.Image;
import org.verselstudios.Image.ImageUtils;
import org.verselstudios.gl.GLHelper;
import org.verselstudios.math.Vector3d;
import org.verselstudios.math.Vector4d;

public class Font {

    public static final Font DEFAULT = new Font("assets/textures/font/ascii.png");
    public static final Font EMOJI = new Font("assets/textures/font/emoji.png");

    private final int textureId;
    private final String fontResource;

    public Font(String fontResource) {
        this.fontResource = fontResource;

        Image image = ImageUtils.loadImageFromResource(fontResource);
        textureId = GLHelper.createGLTexture(image);
    }

    public void renderString(Vector3d position, String string) {
        renderString(position, string, FontStyle.DEFAULT);
    }

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

                GL20.glEnable(GL20.GL_BLEND);
                GL20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                GL20.glBindTexture(GL20.GL_TEXTURE_2D, textureId);
                GL20.glColor4d(shadowColor.getX(), shadowColor.getY(), shadowColor.getZ(), shadowColor.getW());
                GL20.glBegin(GL20.GL_QUADS);

                GL20.glTexCoord2d(x, y);
                GL20.glVertex3d(position.getX() + shadowPos*fontSize, position.getY() - fontSize/8D, position.getZ());
                GL20.glTexCoord2d(x, y1);
                GL20.glVertex3d(position.getX() + shadowPos*fontSize + (style.italic ? fontSize/4D : 0), position.getY() + fontSize - fontSize/8D, position.getZ());
                GL20.glTexCoord2d(x1, y1);
                GL20.glVertex3d(position.getX() + shadowPos*fontSize + fontSize + (style.italic ? fontSize/4D : 0), position.getY() + fontSize - fontSize/8D, position.getZ());
                GL20.glTexCoord2d(x1, y);
                GL20.glVertex3d(position.getX() + shadowPos*fontSize + fontSize, position.getY() - fontSize/8D, position.getZ());
                GL20.glEnd();

                GL20.glDisable(GL20.GL_BLEND);

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

            GL20.glEnable(GL20.GL_BLEND);
            GL20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            GL20.glBindTexture(GL20.GL_TEXTURE_2D, textureId);
            GL20.glColor4d(style.color().getX(), style.color().getY(), style.color().getZ(), style.color().getW());
            GL20.glBegin(GL20.GL_QUADS);

            GL20.glTexCoord2d(x, y);
            GL20.glVertex3d(position.getX() + pos*fontSize, position.getY(), position.getZ());
            GL20.glTexCoord2d(x, y1);
            GL20.glVertex3d(position.getX() + pos*fontSize + (style.italic ? fontSize/4D : 0), position.getY() + fontSize, position.getZ());
            GL20.glTexCoord2d(x1, y1);
            GL20.glVertex3d(position.getX() + pos*fontSize + fontSize + (style.italic ? fontSize/4D : 0), position.getY() + fontSize, position.getZ());
            GL20.glTexCoord2d(x1, y);
            GL20.glVertex3d(position.getX() + pos*fontSize + fontSize, position.getY(), position.getZ());
            GL20.glEnd();

            GL20.glDisable(GL20.GL_BLEND);

            pos++;
        }
    }

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


    public record FontStyle(double size, Vector4d color, Vector4d shadowColor, boolean italic, boolean shadow, boolean bold) {
        public static final FontStyle DEFAULT = new FontStyleBuilder().build();
    }

}
