package org.verselstudios.light;

import org.joml.Vector3d;

public record DirectionalLight(Vector3d direction, Vector3d ambient, Vector3d diffuse, Vector3d specular) implements Light {
}
