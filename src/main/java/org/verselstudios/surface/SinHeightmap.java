package org.verselstudios.surface;

import org.joml.Vector2d;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;

public class SinHeightmap implements Heightmap {

    private static final ShaderProgram PROGRAM = ShaderRegister.getProgram("heightmap");

    public SinHeightmap() {

    }


    @Override
    public double get(Vector2d pos) {
        pos = pos.mul(Math.PI * 10);
        return (Math.sin(pos.x) + Math.sin(pos.y) + 2) / 4D;
    }

    @Override
    public ShaderProgram getProgram() {
        return PROGRAM;
    }
}
