package org.verselstudios.math;

import org.joml.Matrix4d;

import java.util.ArrayList;

public class MatrixStack {

    private final ArrayList<Matrix4d> stack = new ArrayList<>();

    public MatrixStack() {
    }

    public final Matrix4d matrix() {
        if (stack.isEmpty()) throw new IllegalStateException("Tried to get top matrix of empty stack!");
        return stack.getLast();
    }

    public void push(Matrix4d matrix) {
        Matrix4d mat = new Matrix4d();
        matrix().mul(matrix, mat);
        stack.add(mat);
    }

    public void pushView(Matrix4d viewMatrix) {
        if (!stack.isEmpty()) throw new IllegalStateException("Attempted to push view matrix to non empty stack!");
        stack.add(new Matrix4d(viewMatrix));
    }

    public void pop() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Cannot pop! Stack is empty");
        }
        stack.removeLast();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}
