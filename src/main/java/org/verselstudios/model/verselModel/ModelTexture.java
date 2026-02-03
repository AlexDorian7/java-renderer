package org.verselstudios.model.verselModel;

import org.verselstudios.Image.Texture;

public class ModelTexture {
    public String id;
    public String name;
    public int width;
    public int height;
    public String path;

    private Texture texture;

    public Texture getTexture(VerselModel model) {
        if (texture == null) {
            texture = new Texture("assets/textures/models/" + model.resource + "/" + name);
        }
        return texture;
    }

    public String getResource(VerselModel model) {
        return "assets/textures/models/" + model.resource + "/" + name;
    }

}
