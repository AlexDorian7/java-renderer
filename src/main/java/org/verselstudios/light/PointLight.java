package org.verselstudios.light;

import org.joml.Vector3d;

public record PointLight(Vector3d position, Vector3d ambient, Vector3d diffuse, Vector3d specular, Vector3d attenuation) implements Light {
}
