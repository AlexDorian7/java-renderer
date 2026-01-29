package org.verselstudios.surface;

import org.verselstudios.math.Vector2d;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;

public class FlatHeightMap implements Heightmap {

    private final double height;
    private static final ShaderProgram PROGRAM = ShaderRegister.getProgram("heightmap");

    public FlatHeightMap() {
        this(0);
    }

    public FlatHeightMap(double height) {
        this.height = height;
    }

    @Override
    public double get(Vector2d pos) {
        return height;
    }

    @Override
    public ShaderProgram getProgram() {
        return PROGRAM;
    }
}
