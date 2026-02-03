package org.verselstudios.Image;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.ReadableByteChannel;

public class ImageUtils {

    static {
        STBImage.stbi_set_flip_vertically_on_load(true);
    }

    private static ByteBuffer readResourceToBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer = BufferUtils.createByteBuffer(bufferSize);

        try (InputStream is =
                     Thread.currentThread()
                             .getContextClassLoader()
                             .getResourceAsStream(resource)) {

            if (is == null) {
                throw new IOException("Resource not found: " + resource);
            }

            byte[] tmp = new byte[8192];
            while (true) {
                int bytes = is.read(tmp);
                if (bytes == -1) break;

                if (buffer.remaining() < bytes) {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                }

                buffer.put(tmp, 0, bytes);
            }
        }

        buffer.flip();
        return buffer;
    }

    private static ByteBuffer readUrlToBuffer(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);

        try (InputStream source = connection.getInputStream();
            ReadableByteChannel rbc = Channels.newChannel(source)) {

            ByteBuffer buffer = BufferUtils.createByteBuffer(8 * 1024);

            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1)
                    break;

                if (buffer.remaining() == 0) {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                }
            }

            buffer.flip();
            return buffer;
        }
    }


    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }


    public static Image loadImageFromResource(String resourcePath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            ByteBuffer imageBuffer = readResourceToBuffer(resourcePath, 8 * 1024);

            IntBuffer width  = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer comp   = stack.mallocInt(1);

            ByteBuffer pixels = STBImage.stbi_load_from_memory(
                    imageBuffer,
                    width,
                    height,
                    comp,
                    4 // force RGBA
            );

            if (pixels == null) {
                throw new RuntimeException("Failed to load image: " +
                        STBImage.stbi_failure_reason());
            }

            return new Image(width.get(0), height.get(0), pixels);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource: " + resourcePath, e);
        }
    }

    public static Image loadImageFromUrl(String urlString) {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            ByteBuffer imageBuffer = readUrlToBuffer(urlString);

            IntBuffer width  = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer comp   = stack.mallocInt(1);

            ByteBuffer pixels = STBImage.stbi_load_from_memory(
                    imageBuffer,
                    width,
                    height,
                    comp,
                    4 // force RGBA
            );

            if (pixels == null) {
                throw new RuntimeException("Failed to load image: " +
                        STBImage.stbi_failure_reason());
            }

            return new Image(width.get(0), height.get(0), pixels);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read URL: " + urlString, e);
        }
    }


}
