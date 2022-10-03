package org.apache.xml.security.utils;

import org.apache.commons.logging.LogFactory;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import org.apache.commons.logging.Log;

public class JavaUtils
{
    static Log log;
    
    private JavaUtils() {
    }
    
    public static byte[] getBytesFromFile(final String s) throws FileNotFoundException, IOException {
        byte[] byteArray = null;
        final FileInputStream fileInputStream = new FileInputStream(s);
        try {
            final UnsyncByteArrayOutputStream unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
            final byte[] array = new byte[1024];
            int read;
            while ((read = fileInputStream.read(array)) > 0) {
                unsyncByteArrayOutputStream.write(array, 0, read);
            }
            byteArray = unsyncByteArrayOutputStream.toByteArray();
        }
        finally {
            fileInputStream.close();
        }
        return byteArray;
    }
    
    public static void writeBytesToFilename(final String s, final byte[] array) {
        FileOutputStream fileOutputStream = null;
        try {
            if (s != null && array != null) {
                fileOutputStream = new FileOutputStream(new File(s));
                fileOutputStream.write(array);
                fileOutputStream.close();
            }
            else {
                JavaUtils.log.debug((Object)"writeBytesToFilename got null byte[] pointed");
            }
        }
        catch (final IOException ex) {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                }
                catch (final IOException ex2) {}
            }
        }
    }
    
    public static byte[] getBytesFromStream(final InputStream inputStream) throws IOException {
        final UnsyncByteArrayOutputStream unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream();
        final byte[] array = new byte[1024];
        int read;
        while ((read = inputStream.read(array)) > 0) {
            unsyncByteArrayOutputStream.write(array, 0, read);
        }
        return unsyncByteArrayOutputStream.toByteArray();
    }
    
    static {
        JavaUtils.log = LogFactory.getLog(JavaUtils.class.getName());
    }
}
