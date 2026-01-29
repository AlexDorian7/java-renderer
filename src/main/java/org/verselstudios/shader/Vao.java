package org.verselstudios.shader;

import org.lwjgl.opengl.GL45;

public record Vao(String name, int amount, boolean normalized) {

    /**
     * Maybe this will be supported later. For now, I will only support floats
     */
    public enum Types {
        GL_BYTE(GL45.GL_BYTE, Byte.BYTES),
        GL_UNSIGNED_BYTE(GL45.GL_UNSIGNED_BYTE, Byte.BYTES),
        GL_SHORT(GL45.GL_SHORT, Short.BYTES),
        GL_UNSIGNED_SHORT(GL45.GL_UNSIGNED_SHORT, Short.BYTES),
        GL_INT(GL45.GL_INT, Integer.BYTES),
        GL_UNSIGNED_INT(GL45.GL_UNSIGNED_INT, Integer.BYTES),
        GL_FLOAT(GL45.GL_FLOAT, Float.BYTES),
        GL_2_BYTES(GL45.GL_2_BYTES, 2),
        GL_3_BYTES(GL45.GL_3_BYTES, 3),
        GL_4_BYTES(GL45.GL_4_BYTES, 4),
        GL_DOUBLE(GL45.GL_DOUBLE, Double.BYTES);

        private final int type;
        private final int size;

        Types(int type, int size) {
            this.type = type;
            this.size = size;
        }

        public int getType() {
            return type;
        }

        public int getSize() {
            return size;
        }
    }
}
