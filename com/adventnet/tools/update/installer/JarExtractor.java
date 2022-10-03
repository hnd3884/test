package com.adventnet.tools.update.installer;

import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.File;
import java.net.URL;

public class JarExtractor
{
    public static File extract(final URL u, final File dest, final boolean overwrite) throws Exception {
        String fileName = u.getFile();
        final int lastSlash = fileName.lastIndexOf("/");
        fileName = fileName.substring(lastSlash + 1);
        final File destAppFile = new File(dest, fileName);
        if (destAppFile.getCanonicalPath().startsWith(dest.getCanonicalPath())) {
            throw new IOException("URL is trying to reach outside of the target dir: " + fileName);
        }
        final HttpURLConnection hurlc = (HttpURLConnection)u.openConnection();
        final InputStream is = hurlc.getInputStream();
        final BufferedInputStream bis = new BufferedInputStream(is);
        if (destAppFile.exists()) {
            if (overwrite) {
                extract(bis, destAppFile);
            }
        }
        else {
            extract(bis, destAppFile);
        }
        is.close();
        bis.close();
        hurlc.disconnect();
        return destAppFile;
    }
    
    public static File extract(final File source, final File dest, final boolean overwrite) throws Exception {
        final File destAppFile = new File(dest, source.getName());
        if (destAppFile.exists()) {
            if (overwrite) {
                extract(source, destAppFile);
            }
        }
        else {
            extract(source, destAppFile);
        }
        return destAppFile;
    }
    
    public static void extract(final File src, final File dest) throws Exception {
        final FileInputStream fis = new FileInputStream(src);
        final BufferedInputStream bis = new BufferedInputStream(fis);
        extract(bis, dest);
        fis.close();
        bis.close();
    }
    
    public static void extract(final InputStream is, final File dest) throws Exception {
        final byte[] buffer = new byte[10000];
        final ZipInputStream zis = new ZipInputStream(is);
        for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
            String fileName = entry.getName();
            if (fileName.charAt(fileName.length() - 1) == '/') {
                fileName = fileName.substring(0, fileName.length() - 1);
            }
            if (fileName.charAt(0) == '/') {
                fileName = fileName.substring(1);
            }
            if (File.separatorChar != '/') {
                fileName = fileName.replace('/', File.separatorChar);
            }
            final File file = new File(dest, fileName);
            if (!file.getCanonicalPath().startsWith(dest.getCanonicalPath() + File.separator)) {
                throw new IOException("Entry is outside of the target dir: " + entry.getName());
            }
            if (entry.isDirectory()) {
                file.mkdirs();
                final long time = entry.getTime();
                file.setLastModified(time);
            }
            else if (isJar(fileName)) {
                extract(zis, file);
            }
            else {
                final File destin = new File(dest, fileName);
                if (!destin.getCanonicalPath().startsWith(dest.getCanonicalPath() + File.separator)) {
                    throw new IOException("Entry is outside of the target dir: " + entry.getName());
                }
                final File parent = destin.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file.getAbsolutePath()));
                int len = 0;
                while ((len = zis.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.close();
                final long time2 = entry.getTime();
                destin.setLastModified(time2);
                zis.closeEntry();
            }
        }
    }
    
    public static boolean isJar(final String name) {
        return name.endsWith(".ear") || name.endsWith(".war") || name.endsWith(".jar") || name.endsWith(".sar") || name.endsWith(".rar");
    }
    
    public static File[] extract(final File[] files, final File dest, final boolean overwrite) throws Exception {
        final int length = files.length;
        final File[] extractedLocations = new File[length];
        if (!dest.exists()) {
            dest.mkdirs();
        }
        for (int i = 0; i < length; ++i) {
            final File f = files[i];
            final File destAppFile = new File(dest, f.getName());
            if (destAppFile.exists()) {
                if (overwrite) {
                    extract(files[i], destAppFile);
                }
            }
            else {
                extract(files[i], destAppFile);
            }
            extractedLocations[i] = destAppFile;
        }
        return extractedLocations;
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 2) {
            ConsoleOut.println("Usage:java JarExtractor [EEAR to be extracted]  [destination directory]");
            System.exit(1);
        }
        final File f = new File(args[0]);
        final File dest = new File(args[1]);
        extract(f, dest);
    }
}
