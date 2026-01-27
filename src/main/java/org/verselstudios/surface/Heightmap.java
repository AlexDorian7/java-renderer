package org.verselstudios.surface;

import org.verselstudios.math.Vector2d;
import org.verselstudios.shader.ShaderProgram;

public interface Heightmap {
    double get(Vector2d pos);
    ShaderProgram getProgram();
}
