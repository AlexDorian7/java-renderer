package org.verselstudios.shader;

import java.util.ArrayList;

public class VaoBuilder {
    public VaoBuilder() {

    }

    private final ArrayList<Vao> VAOs = new ArrayList<>();
    private final boolean lit = false;

    public ArrayList<Vao> getVAOs() {
        return VAOs;
    }

    public Vertex getNewVertex() {
        return new Vertex(this);
    }

    public int getStride() {
        int total = 0;
        for (Vao vao : VAOs) {
            total += vao.amount();
        }
        return total;

    }

    public boolean isLit() {
        return lit;
    }
}
