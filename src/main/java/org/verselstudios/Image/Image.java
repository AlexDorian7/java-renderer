package org.verselstudios.Image;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

// Assumed to be in RGBA Format
public record Image(int width, int height, ByteBuffer pixels) {

    public static final Image ERROR;

    static {
        // 2x2 image, 4 bytes per pixel (RGBA)
        ByteBuffer buf = BufferUtils.createByteBuffer(2 * 2 * 4);

        // Fill pixels in row-major order (top-left to bottom-right)
        // Pixel order: top-left, top-right, bottom-left, bottom-right

        // Top-left: magenta
        buf.put((byte) 255); // R
        buf.put((byte) 0);   // G
        buf.put((byte) 255); // B
        buf.put((byte) 255); // A

        // Top-right: black
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 255);

        // Bottom-left: black
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 0);
        buf.put((byte) 255);

        // Bottom-right: magenta
        buf.put((byte) 255);
        buf.put((byte) 0);
        buf.put((byte) 255);
        buf.put((byte) 255);

        buf.flip(); // Reset position for reading

        ERROR = new Image(2, 2, buf);
    }
}
