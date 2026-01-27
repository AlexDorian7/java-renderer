package org.verselstudios.math;

import java.util.ArrayList;

public class MatrixStack {

    private final ArrayList<Matrix4d> stack = new ArrayList<>();

    public MatrixStack() {
        stack.add(new Matrix4d());
    }

    public final Matrix4d matrix() {
        return stack.getLast();
    }

    public void push(Matrix4d matrix) {
        stack.add(matrix.multiply(matrix()));
    }

    public void pop() {
        if (stack.size() <= 1) {
            throw new IllegalStateException("Cannot pop the root matrix");
        }
        stack.removeLast();
    }

    public boolean isEmpty() {
        return stack.size() == 1;
    }
}
