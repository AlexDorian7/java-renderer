package org.verselstudios.shader;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import java.util.ArrayList;

public class Vertex {
    private final VaoBuilder vaoBuilder;

    private final ArrayList<Float> storage = new ArrayList<>();

    public Vertex(VaoBuilder vaoBuilder) {
        this.vaoBuilder = vaoBuilder;
        int total = 0;
        for (Vao vao : vaoBuilder.getVAOs()) {
            total += vao.amount();
        }
        for (int i = 0; i < total; i++) {
            storage.add(0.0f);
        }
    }

    public Vertex setData(String vaoName, Float... data) {
        int pos = 0;
        for (Vao vao : vaoBuilder.getVAOs()) {
            if (vao.name().equals(vaoName)) {
                if (data.length != vao.amount()) throw new IllegalArgumentException("Expected " + vao.amount() + " of floats, but got " + data.length + " when setting vertex data for " + vao.name());
                for (int i=0; i<data.length; i++) {
                    storage.set(pos+i, data[i]);
                }
                return this;
            }
            pos += vao.amount();
        }
        throw new IllegalArgumentException("Vao with name " + vaoName + " not found");
    }

    public Vertex setData(String name, Vector2d vector) {
        return setData(name, (float) vector.x, (float) vector.y);
    }

    public Vertex setData(String name, Vector3d vector) {
        return setData(name, (float) vector.x, (float) vector.y, (float) vector.z);
    }

    public Vertex setData(String name, Vector4d vector) {
        return setData(name, (float) vector.x, (float) vector.y, (float) vector.z, (float) vector.w);
    }

    public VaoBuilder getVaoBuilder() {
        return vaoBuilder;
    }

    public final ArrayList<Float> getData() {
        return storage;
    }
}
