package org.apache.poi.xdgf.util;

public class Util
{
    public static int countLines(final String str) {
        int lines = 1;
        int pos = 0;
        while ((pos = str.indexOf("\n", pos) + 1) != 0) {
            ++lines;
        }
        return lines;
    }
    
    public static String sanitizeFilename(final String name) {
        return name.replaceAll("[:\\\\/*\"?|<>]", "_");
    }
}
