package org.verselstudios.shader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.lwjgl.BufferUtils;
import org.verselstudios.light.DirectionalLight;
import org.verselstudios.light.Light;
import org.verselstudios.light.PointLight;
import org.verselstudios.light.LightManager;
import org.verselstudios.shader.material.Material;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;


import static org.lwjgl.opengl.GL20.*;


public class ShaderProgram {

    private static final Logger LOGGER = LogManager.getLogger(ShaderProgram.class);

    protected static FloatBuffer buf16Pool;
    protected static FloatBuffer buf9Pool;
    /**
     * Makes the "default shader" (0) the active program. In GL 3.1+ core profile,
     * you may run into glErrors if you try rendering with the default shader.
     */
    public static void unbind() {
        glUseProgram(0);
    }

    public final int program;
    public final int vertex;
    public final int fragment;
    public final VaoBuilder vaoBuilder;

    public ShaderProgram(String vertexSource, String fragmentSource, VaoBuilder vaoBuilder) throws ShaderException {
        this(vertexSource, fragmentSource, null, vaoBuilder);
    }

    /**
     * Creates a new shader from vertex and fragment source, and with the given
     * map of <Integer, String> attrib locations
     * @param vertexShader the vertex shader source string
     * @param fragmentShader the fragment shader source string
     * @param attributes a map of attrib locations for GLSL 120
     * @throws ShaderException if the program could not be compiled and linked
     */
    public ShaderProgram(String vertexShader, String fragmentShader, @Nullable Map<Integer, String> attributes, VaoBuilder vaoBuilder) throws ShaderException {
        //compile the String source
        vertex = compileShader(vertexShader, GL_VERTEX_SHADER);
        fragment = compileShader(fragmentShader, GL_FRAGMENT_SHADER);
        this.vaoBuilder = vaoBuilder;

        //create the program
        program = glCreateProgram();

        //attach the shaders
        glAttachShader(program, vertex);
        glAttachShader(program, fragment);

        //bind the attrib locations for GLSL 120
        if (attributes != null)
            for (Map.Entry<Integer, String> e : attributes.entrySet())
                glBindAttribLocation(program, e.getKey(), e.getValue());

        //link our program
        glLinkProgram(program);

        //grab our info log
        String infoLog = glGetProgramInfoLog(program, glGetProgrami(program, GL_INFO_LOG_LENGTH));

        //if some log exists, append it
        if (infoLog!=null && !infoLog.trim().isEmpty())
            LOGGER.info(infoLog);

        //if the link failed, throw some sort of exception
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
            throw new ShaderException(
                    "Failure in linking program. Error log:\n" + infoLog);

        //detach and delete the shaders which are no longer needed
        glDetachShader(program, vertex);
        glDetachShader(program, fragment);
        glDeleteShader(vertex);
        glDeleteShader(fragment);
    }

    /** Compile the shader source as the given type and return the shader object ID. */
    protected int compileShader(String source, int type) throws ShaderException {
        //create a shader object
        int shader = glCreateShader(type);
        //pass the source string
        glShaderSource(shader, source);
        //compile the source
        glCompileShader(shader);

        //if info/warnings are found, append it to our shader log
        String infoLog = glGetShaderInfoLog(shader,
                glGetShaderi(shader, GL_INFO_LOG_LENGTH));
        if (infoLog!=null && !infoLog.trim().isEmpty())
            LOGGER.info(getName(type) +": "+infoLog);

        //if the compiling was unsuccessful, throw an exception
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
            throw new ShaderException("Failure in compiling " + getName(type)
                    + ". Error log:\n" + infoLog);

        return shader;
    }

    protected String getName(int shaderType) {
        if (shaderType == GL_VERTEX_SHADER)
            return "GL_VERTEX_SHADER";
        if (shaderType == GL_FRAGMENT_SHADER)
            return "GL_FRAGMENT_SHADER";
        else
            return "shader";
    }

    public VaoBuilder getVaoBuilder() {
        return vaoBuilder;
    }

    /**
     * Make this shader the active program.
     */
    public void use() {
        glUseProgram(program);
    }

    /**
     * Destroy this shader program.
     */
    public void destroy() {
        glDeleteProgram(program);
    }

    /**
     * Gets the location of the specified uniform name.
     * @param str the name of the uniform
     * @return the location of the uniform in this program
     */
    public int getUniformLocation(String str) {
        return glGetUniformLocation(program, str);
    }

    /* ------ UNIFORM SETTERS/GETTERS ------ */

    /**
     * Sets the uniform data at the specified location (the uniform type may be int, bool or sampler2D).
     * @param loc the location of the int/bool/sampler2D uniform
     * @param i the value to set
     */
    public void setUniformi(int loc, int i) {
        if (loc==-1) return;
        glUniform1i(loc, i);
    }

    public void setUniformf(int loc, float i) {
        if (loc==-1) return;
        glUniform1f(loc, i);
    }

    public void setUniform3v(int loc, Vector3d vec) {
        if (loc==-1) return;
        glUniform3f(loc, (float) vec.x, (float) vec.y, (float) vec.z);
    }

    public void setUniform4v(int loc, Vector4d vec) {
        if (loc==-1) return;
        glUniform4f(loc, (float) vec.x, (float) vec.y, (float) vec.z, (float) vec.w);
    }


    /**
     * Sends a 4x4 matrix to the shader program.
     * @param loc the location of the mat4 uniform
     * @param transposed whether the matrix should be transposed
     * @param mat the matrix to send
     */
    public void setUniformMatrix(int loc, boolean transposed, Matrix4d mat) {
        if (loc==-1) return;
        if (buf16Pool == null)
            buf16Pool = BufferUtils.createFloatBuffer(16);
        buf16Pool.clear();
        mat.get(buf16Pool);
        glUniformMatrix4fv(loc, transposed, buf16Pool);
    }

    /**
     * Sends a 3x3 matrix to the shader program.
     * @param loc the location of the mat4 uniform
     * @param transposed whether the matrix should be transposed
     * @param mat the matrix to send
     */
    public void setUniformMatrix(int loc, boolean transposed, Matrix3d mat) {
        if (loc==-1) return;
        if (buf9Pool == null)
            buf9Pool = BufferUtils.createFloatBuffer(9);
        buf9Pool.clear();
        mat.get(buf9Pool);
        glUniformMatrix4fv(loc, transposed, buf9Pool);
    }

    public void setModelMatrix(Matrix4d mat) {
        setUniformMatrix(getUniformLocation("model"), false, mat);
    }

    public void setViewMatrix(Matrix4d mat) {
        setUniformMatrix(getUniformLocation("view"), false, mat);
    }

    public void setProjectionMatrix(Matrix4d mat) {
        setUniformMatrix(getUniformLocation("projection"), false, mat);
    }

    public void genLight(Matrix4d modelMatrix) {
        Vector3d position = new Vector3d();
        modelMatrix.getTranslation(position);
        setLight("directionalLight", LightManager.getDirectionalLight());
        List<PointLight> lights = LightManager.getClosestLights(position);
        setUniformi(getUniformLocation("lightAmount"), lights.size());
        for (int i=0; i<lights.size(); i++) {
            setLight("lights["+i+"]", lights.get(i));
        }
    }

    public void setMaterial(String loc, Material material) {
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, material.diffuse().textureId());
        setUniformi(getUniformLocation(loc+".diffuse"), 1);
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, material.specular().textureId());
        setUniformi(getUniformLocation(loc+".specular"), 2);
        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, material.normal().textureId());
        setUniformi(getUniformLocation(loc+".normal"), 3);
        glActiveTexture(GL_TEXTURE4);
        glBindTexture(GL_TEXTURE_2D, material.height().textureId());
        setUniformi(getUniformLocation(loc+".height"), 3);
        setUniformf(getUniformLocation(loc+".shininess"), (float) material.shininess());
    }

    public void setLight(String loc, Light light) {
        if (light instanceof PointLight pointLight) {
            setUniform3v(getUniformLocation(loc+".position"), pointLight.position());
            setUniform3v(getUniformLocation(loc+".ambient"), pointLight.ambient());
            setUniform3v(getUniformLocation(loc+".diffuse"), pointLight.diffuse());
            setUniform3v(getUniformLocation(loc+".specular"), pointLight.specular());
            setUniform3v(getUniformLocation(loc+".attenuation"), pointLight.attenuation());
        } else if (light instanceof DirectionalLight directionalLight) {
            setUniform3v(getUniformLocation(loc+".direction"), directionalLight.direction());
            setUniform3v(getUniformLocation(loc+".ambient"), directionalLight.ambient());
            setUniform3v(getUniformLocation(loc+".diffuse"), directionalLight.diffuse());
            setUniform3v(getUniformLocation(loc+".specular"), directionalLight.specular());
        } else {
            throw new IllegalArgumentException("Unknown light type: " + light.getClass().getName());
        }
    }

}
