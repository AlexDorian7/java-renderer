package org.verselstudios.util;

import java.io.InputStream;
import java.util.Scanner;

public class Util {
    public static String readString(InputStream stream) {
        Scanner s = new Scanner(stream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
