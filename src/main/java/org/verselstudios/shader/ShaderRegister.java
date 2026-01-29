package org.verselstudios.shader;

import org.verselstudios.math.Matrix4d;

import java.io.InputStream;
import java.util.Scanner;

public class ShaderRegister {

    public static Matrix4d PROJECTION_MATRIX = new Matrix4d();

    public static final ShaderProgram CORE = loadProgram("core");
    public static final ShaderProgram LINE = loadProgram("line");

    public static ShaderProgram loadProgram(String name) {
        InputStream vertexStream = ShaderRegister.class.getClassLoader().getResourceAsStream("assets/shaders/" + name + ".vsh");
        InputStream fragmentStream = ShaderRegister.class.getClassLoader().getResourceAsStream("assets/shaders/" + name + ".fsh");
        String vertexCode = readString(vertexStream);
        String fragmentCode = readString(fragmentStream);

        return new ShaderProgram(vertexCode, fragmentCode);
    }

    private static String readString(InputStream stream) {
        Scanner s = new Scanner(stream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
