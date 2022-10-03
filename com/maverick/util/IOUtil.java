package com.maverick.util;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class IOUtil
{
    public static int BUFFER_SIZE;
    
    public static void copy(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        copy(inputStream, outputStream, -1L);
    }
    
    public static void copy(final InputStream inputStream, final OutputStream outputStream, final long n) throws IOException {
        copy(inputStream, outputStream, n, IOUtil.BUFFER_SIZE);
    }
    
    public static void copy(final InputStream inputStream, final OutputStream outputStream, long n, final int n2) throws IOException {
        final byte[] array = new byte[n2];
        if (n >= 0L) {
            while (n > 0L) {
                int n3;
                if (n < n2) {
                    n3 = inputStream.read(array, 0, (int)n);
                }
                else {
                    n3 = inputStream.read(array, 0, n2);
                }
                if (n3 == -1) {
                    break;
                }
                n -= n3;
                outputStream.write(array, 0, n3);
            }
        }
        else {
            while (true) {
                final int read = inputStream.read(array, 0, n2);
                if (read < 0) {
                    break;
                }
                outputStream.write(array, 0, read);
            }
        }
    }
    
    public static boolean closeStream(final InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            return true;
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    public static boolean closeStream(final OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            return true;
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    public static boolean delTree(final File file) {
        if (file.isFile()) {
            return file.delete();
        }
        final String[] list = file.list();
        for (int i = 0; i < list.length; ++i) {
            if (!delTree(new File(file, list[i]))) {
                return false;
            }
        }
        return true;
    }
    
    public static void recurseDeleteDirectory(final File file) {
        final String[] list = file.list();
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.length; ++i) {
            final File file2 = new File(file, list[i]);
            if (file2.isDirectory()) {
                recurseDeleteDirectory(file2);
            }
            file2.delete();
        }
        file.delete();
    }
    
    public static void copyFile(final File file, File file2) throws IOException {
        if (file.isDirectory()) {
            if (!file2.exists()) {
                file2.mkdir();
            }
            final String[] list = file.list();
            for (int i = 0; i < list.length; ++i) {
                final File file3 = new File(file, list[i]);
                if (!file3.getName().equals(".")) {
                    if (!file3.getName().equals("..")) {
                        if (file3.isDirectory()) {
                            copyFile(file3, new File(file2, file3.getName()));
                        }
                        else {
                            copyFile(file3, file2);
                        }
                    }
                }
            }
        }
        else if (file.isFile() && (file2.isDirectory() || file2.isFile())) {
            if (file2.isDirectory()) {
                file2 = new File(file2, file.getName());
            }
            final FileInputStream fileInputStream = new FileInputStream(file);
            final FileOutputStream fileOutputStream = new FileOutputStream(file2);
            final byte[] array = new byte[32678];
            int read;
            while ((read = fileInputStream.read(array)) > -1) {
                fileOutputStream.write(array, 0, read);
            }
            closeStream(fileInputStream);
            closeStream(fileOutputStream);
        }
    }
    
    static {
        IOUtil.BUFFER_SIZE = 8192;
    }
}
