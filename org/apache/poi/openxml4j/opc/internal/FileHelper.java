package org.apache.poi.openxml4j.opc.internal;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;

public final class FileHelper
{
    public static File getDirectory(final File f) {
        if (f != null) {
            final String path = f.getPath();
            int num2 = path.length();
            while (--num2 >= 0) {
                final char ch1 = path.charAt(num2);
                if (ch1 == File.separatorChar) {
                    return new File(path.substring(0, num2));
                }
            }
        }
        return null;
    }
    
    public static void copyFile(final File in, final File out) throws IOException {
        try (final FileInputStream fis = new FileInputStream(in);
             final FileOutputStream fos = new FileOutputStream(out);
             final FileChannel sourceChannel = fis.getChannel();
             final FileChannel destinationChannel = fos.getChannel()) {
            sourceChannel.transferTo(0L, sourceChannel.size(), destinationChannel);
        }
    }
    
    public static String getFilename(final File file) {
        if (file != null) {
            final String path = file.getPath();
            int num2;
            final int len = num2 = path.length();
            while (--num2 >= 0) {
                final char ch1 = path.charAt(num2);
                if (ch1 == File.separatorChar) {
                    return path.substring(num2 + 1, len);
                }
            }
        }
        return "";
    }
}
