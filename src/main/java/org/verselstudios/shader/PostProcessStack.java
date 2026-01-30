package org.verselstudios.shader;

import java.util.ArrayList;

public class PostProcessStack {

    private final ArrayList<ShaderPostProgram> shaders = new ArrayList<>();

    public void push(ShaderPostProgram shader) {
        shaders.add(shader);
    }

    public void pop() {
        shaders.removeLast();
    }

    public final ArrayList<ShaderPostProgram> getShaders() {
        return shaders;
    }
}
