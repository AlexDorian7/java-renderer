package org.verselstudios.world.objects;

import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector4d;
import org.verselstudios.model.LitRenderSystem;
import org.verselstudios.model.RenderSystem;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.Vertex;
import org.verselstudios.shader.material.Material;
import org.verselstudios.voxel.Chunk;
import org.verselstudios.world.WorldObject;

import static org.lwjgl.opengl.GL45.*;

public class ChunkRenderer extends WorldObject {
    public ChunkRenderer(Chunk chunk, Material material) {
        super(chunk.getTransform(), makeRenderSystem(chunk, material));
    }

    private static LitRenderSystem makeRenderSystem(Chunk chunk, Material material) {
        LitRenderSystem litRenderSystem = new LitRenderSystem(RenderSystem.RenderType.GL_TRIANGLES, material);
        
        litRenderSystem.begin();

        for (int x=0; x<Chunk.CHUNK_SIZE; x++) {
            for (int y=0; y<Chunk.CHUNK_SIZE; y++) {
                for (int z=0; z<Chunk.CHUNK_SIZE; z++) {
                    if (!chunk.getStorage()[x][y][z].equals("air")) {
                        addCube(litRenderSystem, new Vector3i(x, y, z));
                    }
                }
            }
        }

        litRenderSystem.end();
        
        return litRenderSystem;
    }
    
    private static void addCube(RenderSystem renderSystem, Vector3i pos) {
        Vector4d WHITE = new Vector4d(1);

        Vector3d p000 = new Vector3d(pos.x, pos.y, pos.z);
        Vector3d p001 = new Vector3d(pos.x, pos.y,  pos.z+1);
        Vector3d p010 = new Vector3d(pos.x,  pos.y+1, pos.z);
        Vector3d p011 = new Vector3d(pos.x,  pos.y+1,  pos.z+1);
        Vector3d p100 = new Vector3d( pos.x+1, pos.y, pos.z);
        Vector3d p101 = new Vector3d( pos.x+1, pos.y,  pos.z+1);
        Vector3d p110 = new Vector3d( pos.x+1,  pos.y+1, pos.z);
        Vector3d p111 = new Vector3d( pos.x+1,  pos.y+1,  pos.z+1);
        // ---------------- SOUTH (+Z) ----------------
        renderSystem.addVertex(v(renderSystem.getProgram(), p001, WHITE, 0,0,  0,0,1,  1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p101, WHITE, 1,0,  0,0,1,  1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p111, WHITE, 1,1,  0,0,1,  1,0,0));

        renderSystem.addVertex(v(renderSystem.getProgram(), p001, WHITE, 0,0,  0,0,1,  1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p111, WHITE, 1,1,  0,0,1,  1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p011, WHITE, 0,1,  0,0,1,  1,0,0));


        // ---------------- NORTH (-Z) ----------------
        renderSystem.addVertex(v(renderSystem.getProgram(), p100, WHITE, 0,0,  0,0,-1, -1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p000, WHITE, 1,0,  0,0,-1, -1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p010, WHITE, 1,1,  0,0,-1, -1,0,0));

        renderSystem.addVertex(v(renderSystem.getProgram(), p100, WHITE, 0,0,  0,0,-1, -1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p010, WHITE, 1,1,  0,0,-1, -1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p110, WHITE, 0,1,  0,0,-1, -1,0,0));


        // ---------------- EAST (+X) ----------------
        renderSystem.addVertex(v(renderSystem.getProgram(), p101, WHITE, 0,0,  1,0,0,  0,0,-1));
        renderSystem.addVertex(v(renderSystem.getProgram(), p100, WHITE, 1,0,  1,0,0,  0,0,-1));
        renderSystem.addVertex(v(renderSystem.getProgram(), p110, WHITE, 1,1,  1,0,0,  0,0,-1));

        renderSystem.addVertex(v(renderSystem.getProgram(), p101, WHITE, 0,0,  1,0,0,  0,0,-1));
        renderSystem.addVertex(v(renderSystem.getProgram(), p110, WHITE, 1,1,  1,0,0,  0,0,-1));
        renderSystem.addVertex(v(renderSystem.getProgram(), p111, WHITE, 0,1,  1,0,0,  0,0,-1));


        // ---------------- WEST (-X) ----------------
        renderSystem.addVertex(v(renderSystem.getProgram(), p000, WHITE, 0,0,  -1,0,0, 0,0,1));
        renderSystem.addVertex(v(renderSystem.getProgram(), p001, WHITE, 1,0,  -1,0,0, 0,0,1));
        renderSystem.addVertex(v(renderSystem.getProgram(), p011, WHITE, 1,1,  -1,0,0, 0,0,1));

        renderSystem.addVertex(v(renderSystem.getProgram(), p000, WHITE, 0,0,  -1,0,0, 0,0,1));
        renderSystem.addVertex(v(renderSystem.getProgram(), p011, WHITE, 1,1,  -1,0,0, 0,0,1));
        renderSystem.addVertex(v(renderSystem.getProgram(), p010, WHITE, 0,1,  -1,0,0, 0,0,1));


        // ---------------- UP (+Y) ----------------
        renderSystem.addVertex(v(renderSystem.getProgram(), p011, WHITE, 0,0,  0,1,0,  1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p111, WHITE, 1,0,  0,1,0,  1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p110, WHITE, 1,1,  0,1,0,  1,0,0));

        renderSystem.addVertex(v(renderSystem.getProgram(), p011, WHITE, 0,0,  0,1,0,  1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p110, WHITE, 1,1,  0,1,0,  1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p010, WHITE, 0,1,  0,1,0,  1,0,0));


        // ---------------- DOWN (-Y) ----------------
        renderSystem.addVertex(v(renderSystem.getProgram(), p000, WHITE, 0,0,  0,-1,0, 1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p100, WHITE, 1,0,  0,-1,0, 1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p101, WHITE, 1,1,  0,-1,0, 1,0,0));

        renderSystem.addVertex(v(renderSystem.getProgram(), p000, WHITE, 0,0,  0,-1,0, 1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p101, WHITE, 1,1,  0,-1,0, 1,0,0));
        renderSystem.addVertex(v(renderSystem.getProgram(), p001, WHITE, 0,1,  0,-1,0, 1,0,0));

    }

    private static Vertex v(ShaderProgram program, Vector3d pos, Vector4d col,
                            float u, float v,
                            float nx, float ny, float nz,
                            float tx, float ty, float tz) {

        return program.getVaoBuilder().getNewVertex()
                .setData("position", pos)
                .setData("color", col)
                .setData("texCoord", u, v)
                .setData("normal", nx, ny, nz)
                .setData("tangent", tx, ty, tz);
    }

    @Override
    protected void preRender() {
        super.preRender();
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    protected void postRender() {
        super.postRender();
        glDisable(GL_DEPTH_TEST);
    }
}
