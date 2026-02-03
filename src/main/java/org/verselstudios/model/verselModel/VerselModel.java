package org.verselstudios.model.verselModel;

import org.verselstudios.Image.AtlasTexture;
import org.verselstudios.Image.Texture;
import org.verselstudios.json.JsonRegistry;
import org.verselstudios.math.*;
import org.verselstudios.model.RenderSystem;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;
import org.verselstudios.util.Triple;

import java.io.InputStream;
import java.util.*;

import static org.lwjgl.opengl.GL45.*;

import static org.verselstudios.util.Util.readString;

/**
 * Please note this class does not make models properly and can in some cases cause access violations
 */
@Deprecated
public class VerselModel {

    private static final ShaderProgram program = ShaderRegister.getProgram("position_color_tex");

    public Meta meta;
    public List<ModelTexture> textures;
    public List<Cube> cubes;

    public String resource;
//    public ArrayList<Triple<RenderSystem, Texture, Transform>> systems;

    private RenderSystem system;
    private AtlasTexture atlas;

    private VerselModel() {}

    private void makeSystems() {

        ArrayList<String> textures = new ArrayList<>();
        for (ModelTexture texture : this.textures) {
            textures.add(texture.getResource(this));
        }

        atlas = new AtlasTexture(textures);

        system = new RenderSystem(RenderSystem.RenderType.GL_TRIANGLES, program);
        system.begin();

        for (Cube cube : cubes) {
            Map<Direction, List<Vector3d>> verts = cube.getVerticies();
            Vector3d origin = cube.getOrigin().divide(16);
            for (Direction direction : Direction.values()) {
                if (cube.faces.containsKey(direction)) {
                    Face face = cube.faces.get(direction);
                    if (face.texture == null || face.texture < 0 || face.texture >= textures.size()) {
//                        System.out.println("Face " + direction.name() + " on cube " + cube.name + " does not have a valid texture index!");
                        continue;
                    }
                    if (face.uv.length != 4) throw new IllegalStateException("Face " + direction.name() + " on cube " + cube.name + " does not have a valid texture index!");
                    Rectangle region = new Rectangle(atlas.getUV(textures.get(face.texture)));
                    double u = face.uv[0]/16;
                    double v = 1-face.uv[3]/16;
                    double uf = face.uv[2]/16;
                    double vf = 1-face.uv[1]/16;
                    Rectangle uv = new Rectangle(u, v, uf-u, vf-v);
                    uv.setSize(uv.getSize().multiply(region.getSize()));
                    uv.setPos(uv.getPos().add(region.getPos()));
                    Vector2d uv0 = uv.getPos();
                    Vector2d uv1 = uv.getBound();
                    uv0 = uv0.rotate(-face.rotation*Math.PI/2);
                    uv1 = uv1.rotate(-face.rotation*Math.PI/2);
                    float u0 = (float) uv0.getX();
                    float v0 = (float) uv0.getY();
                    float u1 = (float) uv1.getX();
                    float v1 = (float) uv1.getY();
                    Transform transform = new Transform(origin, cube.getRotation(), Vector3d.ONE.multiply(cube.inflate+1));
                    List<Vector3d> faceVerts = verts.get(direction);
                    Vector3d vt0 = faceVerts.get(0).transform(transform.getModelMatrix());
                    Vector3d vt1 = faceVerts.get(1).transform(transform.getModelMatrix());
                    Vector3d vt2 = faceVerts.get(2).transform(transform.getModelMatrix());
                    Vector3d vt3 = faceVerts.get(3).transform(transform.getModelMatrix());
                    // Tri 1
                    system.addVertex(program.getVaoBuilder().getNewVertex().setData("position", vt1).setData("color", Vector4d.ONE).setData("texCoord", u1, v0));
                    system.addVertex(program.getVaoBuilder().getNewVertex().setData("position", vt2).setData("color", Vector4d.ONE).setData("texCoord", u1, v1));
                    system.addVertex(program.getVaoBuilder().getNewVertex().setData("position", vt0).setData("color", Vector4d.ONE).setData("texCoord", u0, v0));
                    // Tri 2
                    system.addVertex(program.getVaoBuilder().getNewVertex().setData("position", vt0).setData("color", Vector4d.ONE).setData("texCoord", u0, v0));
                    system.addVertex(program.getVaoBuilder().getNewVertex().setData("position", vt2).setData("color", Vector4d.ONE).setData("texCoord", u1, v1));
                    system.addVertex(program.getVaoBuilder().getNewVertex().setData("position", vt3).setData("color", Vector4d.ONE).setData("texCoord", u0, v1));
                }
            }
        }

        system.end();

    }

    public static VerselModel load(String resource) {
        InputStream stream = VerselModel.class.getClassLoader().getResourceAsStream("assets/models/" + resource + ".versel");
        VerselModel model = JsonRegistry.getGson().fromJson(readString(stream), VerselModel.class);
        String[] split = resource.split("/");
        List<String> sp = List.of(split);
        sp = sp.subList(0, split.length-1);
        if (split.length == 1) {
            model.resource = "";
        } else {
            model.resource = String.join("/", sp);
        }
        model.makeSystems();
        return model;
    }




    public void draw(MatrixStack matrixStack) {
        if (system == null)
            makeSystems();
        glEnable(GL_DEPTH_TEST);
        program.use();
        atlas.bind(program);
        system.draw(matrixStack);
        glDisable(GL_DEPTH_TEST);
    }
}

