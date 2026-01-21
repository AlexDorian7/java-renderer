package org.verselstudios.Image;

import java.nio.ByteBuffer;

// Assumed to be in RGBA Format
public record Image(int width, int height, ByteBuffer pixels) {
}
