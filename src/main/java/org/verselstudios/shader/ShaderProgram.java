package org.verselstudios.shader;

import org.lwjgl.opengl.GL20;

import java.util.Map;


public class ShaderProgram {

    public ShaderProgram(String vertexShader, String fragmentShader, Map<Integer, String> attributes) {
        //compile the String source
        int vertex = compileShader(vertexShader, GL20.GL_VERTEX_SHADER);
        int fragment = compileShader(fragmentShader, GL20.GL_FRAGMENT_SHADER);

        //create the program
        int program = GL20.glCreateProgram();

        //attach the shaders
        GL20.glAttachShader(program, vertex);
        GL20.glAttachShader(program, fragment);

        //bind the attrib locations for GLSL 120
        if (attributes != null)
            for (Map.Entry<Integer, String> e : attributes.entrySet())
                GL20.glBindAttribLocation(program, e.getKey(), e.getValue());

        //link our program
        GL20.glLinkProgram(program);

        //grab our info log
        String infoLog = GL20.glGetProgramInfoLog(program, GL20.glGetProgrami(program, GL20.GL_INFO_LOG_LENGTH));
        //if some log exists, append it
        if (infoLog!=null && infoLog.trim().length()!=0)
            System.out.println(infoLog);

        //if the link failed, throw some sort of exception
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL20.GL_FALSE)
            throw new RuntimeException(
                    "Failure in linking program. Error log:\n" + infoLog);

        //detach and delete the shaders which are no longer needed
        GL20.glDetachShader(program, vertex);
        GL20.glDetachShader(program, fragment);
        GL20.glDeleteShader(vertex);
        GL20.glDeleteShader(fragment);
    }

    protected int compileShader(String source, int type) {
        //create a shader object
        int shader = GL20.glCreateShader(type);
        //pass the source string
        GL20.glShaderSource(shader, source);
        //compile the source
        GL20.glCompileShader(shader);

        //if info/warnings are found, append it to our shader log
        String infoLog = GL20.glGetShaderInfoLog(shader,
                GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH));

        //if the compiling was unsuccessful, throw an exception
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE)
            throw new RuntimeException("Failure in compiling. Error log:\n" + infoLog);

        return shader;
    }
}
