package org.verselstudios.voxel;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.verselstudios.math.Transform;

public class Chunk {

    public static final int CHUNK_SIZE = 16;

    private final String[][][] storage = new String[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
    private final Vector3i chunkPos;

    private final Transform transform;

    public Chunk(Vector3i chunkPos) {
        this.chunkPos = chunkPos;
        this.transform = new Transform(new Vector3d(chunkPos).mul(CHUNK_SIZE), new Quaterniond(), new Vector3d(1));

        for (int x=0; x<Chunk.CHUNK_SIZE; x++) {
            for (int y=0; y<Chunk.CHUNK_SIZE; y++) {
                for (int z=0; z<Chunk.CHUNK_SIZE; z++) {
                    storage[x][y][z] = "air";
                }
            }
        }
    }

    public Transform getTransform() {
        return transform;
    }

    public Vector3i getChunkPos() {
        return chunkPos;
    }

    public String[][][] getStorage() {
        return storage;
    }
}
