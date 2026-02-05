package org.verselstudios.Image;

import org.joml.Vector4d;
import org.verselstudios.math.Rectangle;

import java.nio.ByteBuffer;
import java.util.*;

public class AtlasTexture extends Texture {

    private final Map<String, Rectangle> regions = new HashMap<>();

    public AtlasTexture(String... resourcePaths) {
        super(buildAtlasImage(List.of(resourcePaths), null));
        // regions will be filled by buildAtlasImage via static temp store
        regions.putAll(tempRegions);
        tempRegions.clear();
    }

    public AtlasTexture(List<String> resourcePaths) {
        super(buildAtlasImage(resourcePaths, null));
        // regions will be filled by buildAtlasImage via static temp store
        regions.putAll(tempRegions);
        tempRegions.clear();
    }

    // ------------------------------------------------------------
    // Temporary region store during construction
    // (because super(...) must be first statement)
    // ------------------------------------------------------------
    private static final Map<String, Rectangle> tempRegions = new HashMap<>();

    // ============================================================
    // Build Atlas Image
    // ============================================================

    private static Image buildAtlasImage(List<String> paths, Object ignored) {

        class Entry {
            String path;
            Image image;
            Entry(String p, Image i) { path = p; image = i; }
        }

        ArrayList<Entry> entries = new ArrayList<>();
        int totalArea = 0;
        int maxWidth = 0;

        for (String path : paths) {
            Image img = ImageUtils.loadImageFromResource(path);
            entries.add(new Entry(path, img));
            totalArea += img.width() * img.height();
            maxWidth = Math.max(maxWidth, img.width());
        }

        int size = nextPowerOfTwo((int)Math.ceil(Math.sqrt(totalArea)));
        size = Math.max(size, maxWidth);

        int atlasW = size;
        int atlasH = size;

        ByteBuffer atlasPixels = ByteBuffer.allocateDirect(atlasW * atlasH * 4);

        int x = 0;
        int y = 0;
        int rowHeight = 0;

        tempRegions.clear();

        for (Entry e : entries) {
            Image img = e.image;

            if (x + img.width() > atlasW) {
                x = 0;
                y += rowHeight;
                rowHeight = 0;
            }

            if (y + img.height() > atlasH) {
                throw new IllegalStateException("Atlas overflow");
            }

            copyImage(img, atlasPixels, atlasW, x, y);

            tempRegions.put(
                    e.path,
                    new Rectangle(x, y, img.width(), img.height())
            );

            rowHeight = Math.max(rowHeight, img.height());
            x += img.width();
        }

        atlasPixels.flip();
        return new Image(atlasW, atlasH, atlasPixels);
    }

    // ============================================================
    // Pixel Copy
    // ============================================================

    private static void copyImage(Image src, ByteBuffer dst,
                                  int dstWidth, int dstX, int dstY) {

        int srcWidth = src.width();
        int srcHeight = src.height();
        ByteBuffer srcBuf = src.pixels();

        for (int y = 0; y < srcHeight; y++) {
            for (int x = 0; x < srcWidth; x++) {

                int srcIndex = (y * srcWidth + x) * 4;
                int dstIndex = ((dstY + y) * dstWidth + (dstX + x)) * 4;

                dst.put(dstIndex,     srcBuf.get(srcIndex));
                dst.put(dstIndex + 1, srcBuf.get(srcIndex + 1));
                dst.put(dstIndex + 2, srcBuf.get(srcIndex + 2));
                dst.put(dstIndex + 3, srcBuf.get(srcIndex + 3));
            }
        }
    }

    // ============================================================
    // API
    // ============================================================

    public Rectangle getRegion(String path) {
        Rectangle r = regions.get(path);
        if (r == null)
            throw new IllegalArgumentException("Texture not in atlas: " + path);
        return r;
    }

    public Vector4d getUV(String path) {
        Rectangle r = getRegion(path);

        return new Vector4d(
                r.getPos().x / width,
                r.getPos().y / height,
                r.getBound().x / width,
                r.getBound().y / height
        );
    }

    // ============================================================
    // Helpers
    // ============================================================

    private static int nextPowerOfTwo(int v) {
        int p = 1;
        while (p < v) p <<= 1;
        return p;
    }
}
