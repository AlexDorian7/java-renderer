package org.verselstudios.surface;

import org.verselstudios.math.SimplexNoise;
import org.joml.Vector2d;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;

public class NoiseHeightmap implements Heightmap {

    private static final ShaderProgram PROGRAM = ShaderRegister.getProgram("heightmap");

    public NoiseHeightmap() {

    }


    @Override
    public double get(Vector2d pos) {
        pos = pos.mul(0.25);
        return (SimplexNoise.noise(pos.x, pos.y)+1)/2D;
    }

    @Override
    public ShaderProgram getProgram() {
        return PROGRAM;
    }
}
