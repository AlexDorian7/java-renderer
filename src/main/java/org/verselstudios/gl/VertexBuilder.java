package org.verselstudios.gl;

import org.verselstudios.math.Vector2d;
import org.verselstudios.math.Vector3d;
import org.verselstudios.math.Vector4d;

public class VertexBuilder {
    private Vector3d position;
    private Vector2d texCoord;
    private Vector4d color;
    private Vector3d normal;
    private Vector3d tangent;

    public VertexBuilder setPosition(Vector3d position) {
        this.position = position;
        return this;
    }

    public VertexBuilder setTexCoord(Vector2d texCoord) {
        this.texCoord = texCoord;
        return this;
    }

    public VertexBuilder setColor(Vector4d color) {
        this.color = color;
        return this;
    }

    public VertexBuilder setNormal(Vector3d normal) {
        this.normal = normal;
        return this;
    }

    public VertexBuilder setTangent(Vector3d tangent) {
        this.tangent = tangent;
        return this;
    }

    public RenderSystem.Vertex createVertex() {
        return new RenderSystem.Vertex(position, texCoord, color, normal, tangent, normal.cross(tangent));
    }
}