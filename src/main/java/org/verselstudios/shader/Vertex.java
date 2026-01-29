package org.verselstudios.shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Vertex {
    private final VaoBuilder vaoBuilder;

    private final HashMap<Vao, ArrayList<Float>> storage = new HashMap<>();

    public Vertex(VaoBuilder vaoBuilder) {
        this.vaoBuilder = vaoBuilder;
        for (Vao vao : vaoBuilder.getVAOs()) {
            storage.put(vao, new ArrayList<>());
        }

    }

    public void addData(String vaoName, Float... data) {
        for (Vao vao : storage.keySet()) {
            if (vao.name().equals(vaoName)) {
                if (data.length != vao.amount()) throw new IllegalArgumentException("Expected " + vao.amount() + " of floats, but got " + data.length + " when setting vertex data for " + vao.name());
                storage.get(vao).addAll(List.of(data));
                return;
            }
        }
        throw new IllegalArgumentException("Vao with name " + vaoName + " not found");
    }

    public VaoBuilder getVaoBuilder() {
        return vaoBuilder;
    }
}
