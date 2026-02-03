package org.verselstudios.model.verselModel;

import org.jetbrains.annotations.Nullable;

public class Face {
    public double[] uv;
    
    public Integer texture;

    @Nullable
    public String cullface; // can be null

    public Integer rotation;
    public Integer tint;
}

