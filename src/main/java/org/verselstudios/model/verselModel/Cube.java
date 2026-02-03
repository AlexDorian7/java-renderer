package org.verselstudios.model.verselModel;

import com.google.gson.annotations.SerializedName;
import org.verselstudios.math.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cube {
    public String name;

    private double[] from;
    private double[] to;
    private double[] origin;
    private double[] rotation;


    public double inflate;

    @SerializedName("uv_offset")
    public double[] uvOffset;

    public Map<Direction, Face> faces;

    public Vector3d getFrom() {
        if (from.length != 3) throw new IllegalStateException("From does not have 3 values for cube " + name);
        return new Vector3d(from[0], from[1], from[2]);
    }

    public Vector3d getTo() {
        if (to.length != 3) throw new IllegalStateException("To does not have 3 values for cube " + name);
        return new Vector3d(to[0], to[1], to[2]);
    }

    public Vector3d getOrigin() {
        if (origin.length != 3) throw new IllegalStateException("Origin does not have 3 values for cube " + name);
        return new Vector3d(origin[0], origin[1], origin[2]);
    }

    public Vector3d getRotation() {
        if (rotation.length != 3) throw new IllegalStateException("Rotation does not have 3 values for cube " + name);
        return new Vector3d(rotation[0], rotation[1], rotation[2]);
    }

    public Map<Direction, List<Vector3d>> getVerticies() {
        HashMap<Direction, List<Vector3d>> map = new HashMap<>();

        Vector3d from = getFrom();
        Vector3d to = getTo();
        Vector3d origin = getOrigin();

        Vector3d p000 = new Vector3d(from.getX(), from.getY(), from.getZ()).subtract(origin).divide(16);
        Vector3d p001 = new Vector3d(from.getX(), from.getY(), to.getZ()).subtract(origin).divide(16);
        Vector3d p010 = new Vector3d(from.getX(), to.getY(), from.getZ()).subtract(origin).divide(16);
        Vector3d p011 = new Vector3d(from.getX(), to.getY(), to.getZ()).subtract(origin).divide(16);
        Vector3d p100 = new Vector3d(to.getX(), from.getY(), from.getZ()).subtract(origin).divide(16);
        Vector3d p101 = new Vector3d(to.getX(), from.getY(), to.getZ()).subtract(origin).divide(16);
        Vector3d p110 = new Vector3d(to.getX(), to.getY(), from.getZ()).subtract(origin).divide(16);
        Vector3d p111 = new Vector3d(to.getX(), to.getY(), to.getZ()).subtract(origin).divide(16);


        // NORTH
        map.put(Direction.north, List.of(p100, p000, p010, p110));

        // SOUTH
        map.put(Direction.south, List.of(p001, p101, p111, p011));

        // WEST
        map.put(Direction.west, List.of(p000, p001, p011, p010));

        // EAST
        map.put(Direction.east, List.of(p101, p100, p110, p111));

        // DOWN
        map.put(Direction.down, List.of(p000, p100, p101, p001));

        // UP
        map.put(Direction.up, List.of(p011, p111, p110, p010));

        return map;
    }
}
