package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.security.Permission;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.security.SecurityPermission;
import com.sun.org.slf4j.internal.Logger;

public final class JavaUtils
{
    private static final Logger LOG;
    private static final SecurityPermission REGISTER_PERMISSION;
    
    private JavaUtils() {
    }
    
    public static byte[] getBytesFromFile(final String s) throws FileNotFoundException, IOException {
        byte[] byteArray = null;
        try (final InputStream inputStream = Files.newInputStream(Paths.get(s, new String[0]), new OpenOption[0]);
             final UnsyncByteArrayOutputStream unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream()) {
            final byte[] array = new byte[1024];
            int read;
            while ((read = inputStream.read(array)) > 0) {
                unsyncByteArrayOutputStream.write(array, 0, read);
            }
            byteArray = unsyncByteArrayOutputStream.toByteArray();
        }
        return byteArray;
    }
    
    public static void writeBytesToFilename(final String s, final byte[] array) {
        if (s != null && array != null) {
            try (final OutputStream outputStream = Files.newOutputStream(Paths.get(s, new String[0]), new OpenOption[0])) {
                outputStream.write(array);
            }
            catch (final IOException ex) {
                JavaUtils.LOG.debug(ex.getMessage(), ex);
            }
        }
        else {
            JavaUtils.LOG.debug("writeBytesToFilename got null byte[] pointed");
        }
    }
    
    public static byte[] getBytesFromStream(final InputStream inputStream) throws IOException {
        try (final UnsyncByteArrayOutputStream unsyncByteArrayOutputStream = new UnsyncByteArrayOutputStream()) {
            final byte[] array = new byte[4096];
            int read;
            while ((read = inputStream.read(array)) > 0) {
                unsyncByteArrayOutputStream.write(array, 0, read);
            }
            return unsyncByteArrayOutputStream.toByteArray();
        }
    }
    
    public static byte[] convertDsaASN1toXMLDSIG(final byte[] array, final int n) throws IOException {
        if (array[0] != 48 || array[1] != array.length - 2 || array[2] != 2) {
            throw new IOException("Invalid ASN.1 format of DSA signature");
        }
        int n2;
        byte b;
        for (b = (byte)(n2 = array[3]); n2 > 0 && array[4 + b - n2] == 0; --n2) {}
        int n3;
        byte b2;
        for (b2 = (byte)(n3 = array[5 + b]); n3 > 0 && array[6 + b + b2 - n3] == 0; --n3) {}
        if (n2 > n || array[4 + b] != 2 || n3 > n) {
            throw new IOException("Invalid ASN.1 format of DSA signature");
        }
        final byte[] array2 = new byte[n * 2];
        System.arraycopy(array, 4 + b - n2, array2, n - n2, n2);
        System.arraycopy(array, 6 + b + b2 - n3, array2, n * 2 - n3, n3);
        return array2;
    }
    
    public static byte[] convertDsaXMLDSIGtoASN1(final byte[] array, final int n) throws IOException {
        final int n2 = n * 2;
        if (array.length != n2) {
            throw new IOException("Invalid XMLDSIG format of DSA signature");
        }
        int n3;
        for (n3 = n; n3 > 0 && array[n - n3] == 0; --n3) {}
        int n4 = n3;
        if (array[n - n3] < 0) {
            ++n4;
        }
        int n5;
        for (n5 = n; n5 > 0 && array[n2 - n5] == 0; --n5) {}
        int n6 = n5;
        if (array[n2 - n5] < 0) {
            ++n6;
        }
        final byte[] array2 = new byte[6 + n4 + n6];
        array2[0] = 48;
        array2[1] = (byte)(4 + n4 + n6);
        array2[2] = 2;
        array2[3] = (byte)n4;
        System.arraycopy(array, n - n3, array2, 4 + n4 - n3, n3);
        array2[4 + n4] = 2;
        array2[5 + n4] = (byte)n6;
        System.arraycopy(array, n2 - n5, array2, 6 + n4 + n6 - n5, n5);
        return array2;
    }
    
    public static void checkRegisterPermission() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(JavaUtils.REGISTER_PERMISSION);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(JavaUtils.class);
        REGISTER_PERMISSION = new SecurityPermission("com.sun.org.apache.xml.internal.security.register");
    }
}
