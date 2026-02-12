package org.verselstudios.gl;

import org.lwjgl.stb.STBImage;
import org.verselstudios.Image.Image;

import static org.lwjgl.opengl.GL45.*;

public class GLHelper {



    // This will free the image after upload.
    // The image will no longer be valid after upload
    public static int createGLTexture(Image image) {
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.width(), image.height(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image.pixels());
        STBImage.stbi_image_free(image.pixels()); // to prevent memory leak
        return textureId;
    }
}
