package org.verselstudios.json;

import com.google.gson.Gson;

public class JsonRegistry {
    private static final Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }
}
