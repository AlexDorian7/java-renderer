package org.verselstudios.shader.material;

import org.verselstudios.Image.Texture;

public record Material(Texture diffuse, Texture specular, double shininess) {
}
