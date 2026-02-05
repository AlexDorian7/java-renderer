package org.verselstudios.Image;

import org.verselstudios.gl.GLHelper;
import org.verselstudios.shader.ShaderProgram;

import java.util.HashMap;
import java.util.Objects;

import static org.lwjgl.opengl.GL20.*;

public class Texture {
    private static final HashMap<String, Integer> CACHE = new HashMap<>();

    protected final int textureId;
    protected final int width;
    protected final int height;
    protected String resourcePath;

    public Texture(String resourcePath) {
        this(resourcePath, false);
    }

    public Texture(String resource, boolean isWeb) {
        Image image;
        try {
            if (isWeb) {
                image = ImageUtils.loadImageFromUrl(resource);
            } else {
                image = ImageUtils.loadImageFromResource(resource);
            }
            this.resourcePath = resource;
        } catch (Exception e) {
            System.err.println("Failed to make texture");
            e.printStackTrace();
            this.resourcePath = "ERROR";
            image = Image.ERROR;
        }
        width = image.width();
        height = image.height();
        if (CACHE.containsKey(this.resourcePath)) {
            textureId = CACHE.get(this.resourcePath);
        } else {
            textureId = GLHelper.createGLTexture(image);
            CACHE.put(this.resourcePath, textureId);
        }
    }



    protected Texture(Image image) {
        width = image.width();
        height = image.height();
        String resourcePath = "GENERATED_" + image.hashCode();
        this.resourcePath = resourcePath;
        if (CACHE.containsKey(resourcePath)) {
            textureId = CACHE.get(resourcePath);
        } else {
            textureId = GLHelper.createGLTexture(image);
            CACHE.put(resourcePath, textureId);
        }
    }

    public int textureId() {
        return textureId;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public String resourcePath() {
        return resourcePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Texture) obj;
        return this.textureId == that.textureId &&
                this.width == that.width &&
                this.height == that.height &&
                Objects.equals(this.resourcePath, that.resourcePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textureId, width, height);
    }

    @Override
    public String toString() {
        return "Texture[" +
                "textureId=" + textureId + ", " +
                "width=" + width + ", " +
                "height=" + height + ", " +
                "resourcePath=" + resourcePath + ']';
    }

    public void bind(ShaderProgram program) {
        bind(program, "sampler0", 0);
    }

    public void bind(ShaderProgram program, String uniform, int unit) {
        glActiveTexture(GL_TEXTURE0 + unit);
        glBindTexture(GL_TEXTURE_2D, textureId);

        program.setUniformi(
                program.getUniformLocation(uniform),
                unit
        );
    }

}
