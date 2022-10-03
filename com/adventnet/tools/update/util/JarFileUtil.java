package com.adventnet.tools.update.util;

import java.util.jar.Manifest;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.JarEntry;
import java.util.Enumeration;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;
import java.io.IOException;
import java.util.Hashtable;
import java.io.File;
import java.util.ArrayList;

public class JarFileUtil
{
    private static int BUFFER;
    
    public static void extractJarFile(final String jarFile, ArrayList filesToExtract, final File targetDir) throws Exception {
        Hashtable stdFileVsActFile = new Hashtable();
        File tempFile = new File(jarFile);
        if (!tempFile.exists()) {
            throw new IOException("The specified Jar file [ " + tempFile + " ] does not exist");
        }
        final JarFile jarToExtract = new JarFile(jarFile);
        if (!targetDir.exists()) {
            final boolean status = targetDir.mkdirs();
        }
        stdFileVsActFile = getStdFileVsActFile(jarToExtract, filesToExtract);
        filesToExtract = null;
        BufferedOutputStream boStream = null;
        BufferedInputStream biStream = null;
        try {
            final Enumeration en = stdFileVsActFile.keys();
            while (en.hasMoreElements()) {
                final String stdFile = en.nextElement();
                final String fileName = stdFileVsActFile.get(stdFile);
                final JarEntry zEntry = jarToExtract.getJarEntry(fileName);
                if (zEntry == null) {
                    error("The file [ " + fileName + " ] specified for extraction is not present in : " + jarFile);
                }
                else {
                    biStream = new BufferedInputStream(jarToExtract.getInputStream(zEntry));
                    tempFile = new File(targetDir, stdFile);
                    tempFile = tempFile.getParentFile();
                    if (tempFile != null && !tempFile.exists()) {
                        tempFile.mkdirs();
                    }
                    final FileOutputStream foStream = new FileOutputStream(new File(targetDir, stdFile));
                    boStream = new BufferedOutputStream(foStream, JarFileUtil.BUFFER);
                    int count = 0;
                    final byte[] data = new byte[JarFileUtil.BUFFER];
                    while ((count = biStream.read(data, 0, JarFileUtil.BUFFER)) != -1) {
                        boStream.write(data, 0, count);
                    }
                    boStream.flush();
                    foStream.close();
                }
            }
        }
        catch (final Exception e) {
            System.err.println("Exception while extracting the [ " + jarToExtract.getName() + " ] file.");
            e.printStackTrace();
            throw e;
        }
        finally {
            jarToExtract.close();
            if (boStream != null) {
                boStream.close();
            }
            if (biStream != null) {
                biStream.close();
            }
        }
    }
    
    public static void createJarFile(final String targetFileName, ArrayList filesToCompress, final File directory) throws Exception {
        boolean addHome = true;
        if (!directory.exists()) {
            throw new IOException("The specified directory [ " + directory + " ] does not exist");
        }
        if (filesToCompress == null) {
            filesToCompress = Utils.recurseAndGetFiles(directory);
            addHome = false;
        }
        createEmptyFile(targetFileName);
        BufferedInputStream biStream = null;
        FileOutputStream foStream = null;
        JarOutputStream zoStream = null;
        try {
            foStream = new FileOutputStream(targetFileName);
            zoStream = new JarOutputStream(new BufferedOutputStream(foStream));
            zoStream.setMethod(8);
            final byte[] data = new byte[JarFileUtil.BUFFER];
            for (int i = 0; i < filesToCompress.size(); ++i) {
                final String fileName = (String)filesToCompress.get(i);
                FileInputStream fiStream = null;
                if (addHome) {
                    fiStream = new FileInputStream(new File(directory, fileName));
                }
                else {
                    fiStream = new FileInputStream(fileName);
                }
                biStream = new BufferedInputStream(fiStream, JarFileUtil.BUFFER);
                final JarEntry zEntry = new JarEntry(fileName);
                zoStream.putNextEntry(zEntry);
                int count = 0;
                while ((count = biStream.read(data, 0, JarFileUtil.BUFFER)) != -1) {
                    zoStream.write(data, 0, count);
                }
                zoStream.flush();
                fiStream.close();
            }
        }
        catch (final Exception e) {
            System.err.println("Unable to compress the directory [ " + directory + " ]");
            e.printStackTrace();
            throw e;
        }
        finally {
            if (biStream != null) {
                biStream.close();
            }
            if (zoStream != null) {
                zoStream.close();
            }
            if (foStream != null) {
                foStream.close();
            }
        }
    }
    
    private static void createEmptyFile(final String fileName) throws Exception {
        final File file = new File(fileName);
        if (file.exists()) {
            return;
        }
        final File parentFile = file.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
        file.createNewFile();
    }
    
    private static void log(final String message) {
        System.out.println(message);
    }
    
    private static void error(final String errorMessage) {
        System.err.println("[JarFileUtil] : " + errorMessage);
    }
    
    private static Hashtable getStdFileVsActFile(final JarFile jarFile, ArrayList list) {
        final Hashtable hTable = new Hashtable();
        if (list == null) {
            final Enumeration en = jarFile.entries();
            while (en.hasMoreElements()) {
                final JarEntry entry = en.nextElement();
                if (!entry.isDirectory()) {
                    final String fileName = entry.getName();
                    hTable.put(Utils.getUnixFileName(fileName), fileName);
                }
            }
        }
        else {
            list = replaceBackSlash(list);
            final Enumeration en = jarFile.entries();
            while (en.hasMoreElements()) {
                final JarEntry entry = en.nextElement();
                if (!entry.isDirectory()) {
                    final String stdFileName = Utils.getUnixFileName(entry.getName());
                    if (!list.contains(stdFileName)) {
                        continue;
                    }
                    hTable.put(stdFileName, entry.getName());
                }
            }
        }
        return hTable;
    }
    
    private static ArrayList replaceBackSlash(final ArrayList list) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            final String fileName = list.get(i);
            list.set(i, Utils.getUnixFileName(fileName));
        }
        return list;
    }
    
    private static Manifest getManifest(final File homeDir, final ArrayList fileNames) throws IOException {
        final Manifest manifest = new Manifest();
        for (int size = fileNames.size(), i = 0; i < size; ++i) {
            final String fileName = fileNames.get(i).toUpperCase();
            if (fileName.equals("META-INF/MANIFEST.MF") || fileName.equals("META-INF\\MANIFEST.MF")) {
                fileNames.remove(i);
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(new File(homeDir, fileName));
                    return new Manifest(fis);
                }
                finally {
                    try {
                        fis.close();
                    }
                    catch (final Exception ex) {}
                }
            }
        }
        return manifest;
    }
    
    static {
        JarFileUtil.BUFFER = 8192;
    }
}
