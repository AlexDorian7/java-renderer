package org.verselstudios.shader;

import org.verselstudios.json.JsonRegistry;
import org.joml.Matrix4d;

import java.io.InputStream;
import java.util.HashMap;

import static org.verselstudios.util.Util.readString;

public class ShaderRegister {

    public static Matrix4d PROJECTION_MATRIX = new Matrix4d();

    private static final HashMap<String, ShaderProgram> SHADERS = new HashMap<>();

    public static ShaderProgram loadProgram(String name) {
        InputStream vertexStream = ShaderRegister.class.getClassLoader().getResourceAsStream("assets/shaders/" + name + ".vsh");
        InputStream fragmentStream = ShaderRegister.class.getClassLoader().getResourceAsStream("assets/shaders/" + name + ".fsh");
        InputStream vaoStream = ShaderRegister.class.getClassLoader().getResourceAsStream("assets/shaders/" + name + ".json");
        String vertexCode = readString(vertexStream);
        String fragmentCode = readString(fragmentStream);
        VaoBuilder builder = JsonRegistry.getGson().fromJson(readString(vaoStream), VaoBuilder.class);

        HashMap<Integer, String> attribs = new HashMap<>();

        int i=0;
        for (Vao vao : builder.getVAOs()) {
            attribs.put(i, vao.name());
            i++;
        }


        return new ShaderProgram(vertexCode, fragmentCode, attribs, builder);
    }

    public static ShaderPostProgram loadPostProgram(String name) {
        InputStream vertexStream = ShaderRegister.class.getClassLoader().getResourceAsStream("assets/shaders/postprocess.vsh");
        InputStream fragmentStream = ShaderRegister.class.getClassLoader().getResourceAsStream("assets/shaders/postProcess/" + name + ".glsl");
        InputStream vaoStream = ShaderRegister.class.getClassLoader().getResourceAsStream("assets/shaders/postprocess.json");
        String vertexCode = readString(vertexStream);
        String fragmentCode = readString(fragmentStream);
        VaoBuilder builder = JsonRegistry.getGson().fromJson(readString(vaoStream), VaoBuilder.class);

        HashMap<Integer, String> attribs = new HashMap<>();

        int i=0;
        for (Vao vao : builder.getVAOs()) {
            attribs.put(i, vao.name());
            i++;
        }


        return new ShaderPostProgram(vertexCode, fragmentCode, attribs, builder);
    }

    public static ShaderProgram getProgram(String name) {
        if (SHADERS.containsKey(name)) return SHADERS.get(name);
        try {
            ShaderProgram program = loadProgram(name);
            SHADERS.put(name, program);
            return program;
        } catch (Exception e) {
            throw new IllegalArgumentException("No shader found with name " + name, e);
        }
    }
}
