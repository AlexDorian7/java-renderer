package org.verselstudios.shader;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ShaderPostProgram extends ShaderProgram {
    public ShaderPostProgram(String vertexShader, String fragmentShader, @Nullable Map<Integer, String> attributes, VaoBuilder vaoBuilder) throws ShaderException {
        super(vertexShader, fragmentShader, attributes, vaoBuilder);
    }
}
