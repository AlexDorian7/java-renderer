package org.verselstudios.surface;

import org.joml.Vector2d;
import org.verselstudios.shader.ShaderProgram;

public interface Heightmap {
    double get(Vector2d pos);
    ShaderProgram getProgram();
}
