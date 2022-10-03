package org.apache.commons.compress.utils;

import java.io.File;

public class FileNameUtils
{
    public static String getExtension(final String filename) {
        if (filename == null) {
            return null;
        }
        final String name = new File(filename).getName();
        final int extensionPosition = name.lastIndexOf(46);
        if (extensionPosition < 0) {
            return "";
        }
        return name.substring(extensionPosition + 1);
    }
    
    public static String getBaseName(final String filename) {
        if (filename == null) {
            return null;
        }
        final String name = new File(filename).getName();
        final int extensionPosition = name.lastIndexOf(46);
        if (extensionPosition < 0) {
            return name;
        }
        return name.substring(0, extensionPosition);
    }
}
