package org.verselstudios.model;

import org.verselstudios.math.Vector3d;
import org.verselstudios.math.Vector4d;
import org.verselstudios.shader.ShaderRegister;
import org.verselstudios.shader.Vertex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ObjRenderSystem extends RenderSystem {

    public ObjRenderSystem(String resourcePath) {
        super(RenderType.GL_TRIANGLES, ShaderRegister.getProgram("position_color_tex"));
        loadObj(resourcePath);
    }

    // ============================================================
    // OBJ LOADER
    // ============================================================

    private void loadObj(String resourcePath) {

        List<Vector3d> positions = new ArrayList<>();
        List<Vector3d> normals   = new ArrayList<>();
        List<double[]> uvs       = new ArrayList<>();

        begin();

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is == null)
                throw new IllegalArgumentException("OBJ not found: " + resourcePath);

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("v ")) {
                    String[] s = line.split("\\s+");
                    positions.add(new Vector3d(
                            Double.parseDouble(s[1]),
                            Double.parseDouble(s[2]),
                            Double.parseDouble(s[3])
                    ));
                }
                else if (line.startsWith("vt ")) {
                    String[] s = line.split("\\s+");
                    double u = Double.parseDouble(s[1]);
                    double v = 1.0 - Double.parseDouble(s[2]); // flip
                    uvs.add(new double[]{u, v});
                }
                else if (line.startsWith("vn ")) {
                    String[] s = line.split("\\s+");
                    normals.add(new Vector3d(
                            Double.parseDouble(s[1]),
                            Double.parseDouble(s[2]),
                            Double.parseDouble(s[3])
                    ));
                }
                else if (line.startsWith("f ")) {
                    parseFace(line, positions, uvs);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load OBJ: " + resourcePath, e);
        }

        end();
    }

    // ============================================================
    // FACE PARSER
    // ============================================================

    private void parseFace(String line,
                           List<Vector3d> positions,
                           List<double[]> uvs) {

        String[] parts = line.split("\\s+");

        // Convert OBJ indices to vertex structs
        List<VertexData> verts = new ArrayList<>();

        for (int i = 1; i < parts.length; i++) {
            String[] idx = parts[i].split("/");

            int vIndex = Integer.parseInt(idx[0]) - 1;
            int vtIndex = idx.length > 1 && !idx[1].isEmpty()
                    ? Integer.parseInt(idx[1]) - 1
                    : -1;

            Vector3d pos = positions.get(vIndex);

            double u = 0, v = 0;
            if (vtIndex >= 0 && vtIndex < uvs.size()) {
                u = uvs.get(vtIndex)[0];
                v = uvs.get(vtIndex)[1];
            }

            verts.add(new VertexData(pos, u, v));
        }

        // Triangulate fan
        for (int i = 1; i < verts.size() - 1; i++) {
            addTri(verts.get(0), verts.get(i), verts.get(i + 1));
        }
    }

    private void addTri(VertexData a, VertexData b, VertexData c) {
        addVertex(makeVertex(a));
        addVertex(makeVertex(b));
        addVertex(makeVertex(c));
    }

    private Vertex makeVertex(VertexData vd) {
        return getProgram()
                .getVaoBuilder()
                .getNewVertex()
                .setData("position", vd.pos)
                .setData("color", Vector4d.ONE)
                .setData("texCoord", (float) vd.u, (float) vd.v);
    }

    // ============================================================
    // INTERNAL STRUCT
    // ============================================================

    private static class VertexData {
        Vector3d pos;
        double u, v;
        VertexData(Vector3d p, double u, double v) {
            this.pos = p;
            this.u = u;
            this.v = v;
        }
    }
}
