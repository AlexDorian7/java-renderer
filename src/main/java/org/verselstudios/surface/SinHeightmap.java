package org.verselstudios.surface;

import org.verselstudios.math.Vector2d;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;

public class SinHeightmap implements Heightmap {

    private static final ShaderProgram PROGRAM = ShaderRegister.loadProgram("heightmap");

    public SinHeightmap() {

    }


    @Override
    public double get(Vector2d pos) {
        pos = pos.multiply(Math.PI * 10);
        return (Math.sin(pos.getX()) + Math.sin(pos.getY()) + 2) / 4D;
    }

    @Override
    public ShaderProgram getProgram() {
        return PROGRAM;
    }
}
