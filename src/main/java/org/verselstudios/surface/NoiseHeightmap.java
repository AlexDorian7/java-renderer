package org.verselstudios.surface;

import org.verselstudios.math.SimplexNoise;
import org.verselstudios.math.Vector2d;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;

public class NoiseHeightmap implements Heightmap {

    private static final ShaderProgram PROGRAM = ShaderRegister.getProgram("heightmap");

    public NoiseHeightmap() {

    }


    @Override
    public double get(Vector2d pos) {
        pos = pos.multiply(0.25);
        return (SimplexNoise.noise(pos.getX(), pos.getY())+1)/2D;
    }

    @Override
    public ShaderProgram getProgram() {
        return PROGRAM;
    }
}
