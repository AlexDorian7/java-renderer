package org.verselstudios.shader.material;

import org.joml.Vector3d;
import org.verselstudios.Image.Texture;

public record Material(Vector3d ambient, Texture diffuse, Texture specular, double shininess) {
}
