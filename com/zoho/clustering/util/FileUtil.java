package com.zoho.clustering.util;

import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.io.Closeable;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.io.File;

public class FileUtil
{
    public static void assertFile(final String filePath) {
        assertFile(new File(filePath));
    }
    
    public static void assertFile(final File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("No such File [" + file.getAbsolutePath() + "] is present");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Specified file [" + file.getAbsolutePath() + "] is not a valid file");
        }
    }
    
    public static void assertDir(final String dirPath) {
        assertDir(new File(dirPath));
    }
    
    public static void assertDir(final File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("[" + dir.getAbsolutePath() + "] is not a directory");
        }
    }
    
    public static void assertOrCreateDir(final String dirPath) {
        assertOrCreateDir(new File(dirPath));
    }
    
    public static void assertOrCreateDir(final File dirObj) {
        if (!dirObj.exists()) {
            if (!dirObj.mkdirs()) {
                throw new RuntimeException("Couldn't create directory [" + dirObj.getAbsolutePath() + "]");
            }
        }
        else if (dirObj.isFile()) {
            throw new IllegalArgumentException("[" + dirObj.getAbsolutePath() + "] is not a directory");
        }
    }
    
    public static void createParentDirectories(final File file) {
        final File parent = file.getParentFile();
        if (parent != null && !parent.isDirectory()) {
            final boolean created = parent.mkdirs();
            if (!created) {
                throw new RuntimeException("Not able to create the directory [" + parent.getAbsolutePath() + "]");
            }
        }
    }
    
    public static boolean deleteDirectoryContents(final File dirObj) {
        return deleteDirectoryInternal(dirObj, false);
    }
    
    public static boolean deleteDirectory(final File dirObj) {
        return deleteDirectoryInternal(dirObj, true);
    }
    
    private static boolean deleteDirectoryInternal(final File dirObj, final boolean deleteDir) {
        if (dirObj == null || !dirObj.isDirectory()) {
            return false;
        }
        boolean errorFlag = false;
        final File[] listFiles;
        final File[] files = listFiles = dirObj.listFiles();
        for (final File file : listFiles) {
            if (file.isDirectory()) {
                if (!deleteDirectory(file)) {
                    errorFlag = true;
                }
            }
            else if (!file.delete()) {
                errorFlag = true;
            }
        }
        if (deleteDir) {
            return !errorFlag && dirObj.delete();
        }
        return errorFlag;
    }
    
    public static void unzip(final String zipFile, final String outputDir) {
        unzip(new File(zipFile), new File(outputDir));
    }
    
    public static void unzip(final File zipFile, final File outputDir) {
        assertFile(zipFile);
        assertOrCreateDir(outputDir);
        ZipInputStream zin = null;
        try {
            zin = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry entry = null;
            while ((entry = zin.getNextEntry()) != null) {
                final String fileName = entry.getName();
                if ("~".equals(fileName)) {
                    continue;
                }
                final File target = new File(outputDir, fileName);
                createParentDirectories(target);
                final String canonicalDestinationFile = target.getCanonicalPath();
                if (!canonicalDestinationFile.startsWith(outputDir.getCanonicalPath() + File.separator)) {
                    throw new RuntimeException("Entry is outside of the target directory: " + entry.getName());
                }
                final OutputStream fout = new BufferedOutputStream(new FileOutputStream(target));
                try {
                    copy(zin, fout);
                }
                finally {
                    Close(fout);
                }
            }
        }
        catch (final IOException exp) {
            throw new RuntimeException(exp);
        }
        finally {
            Close(zin);
        }
    }
    
    public static void zipDirectory(final String dir, final String outputZipFilename) {
        final File dirObj = new File(dir);
        assertDir(dirObj);
        ZipOutputStream zout = null;
        try {
            zout = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputZipFilename)));
            if (dirObj.list().length == 0) {
                zout.putNextEntry(new ZipEntry("~"));
                zout.closeEntry();
            }
            else {
                addDirToZip(dirObj, "", zout);
            }
        }
        catch (final IOException exp) {
            throw new RuntimeException(exp);
        }
        finally {
            Close(zout);
        }
    }
    
    private static void addDirToZip(final File dir, final String prefix, final ZipOutputStream zout) throws IOException {
        final File[] listFiles;
        final File[] files = listFiles = dir.listFiles();
        for (final File file : listFiles) {
            final String fileName = prefix + File.separator + file.getName();
            if (file.exists()) {
                if (file.isDirectory()) {
                    addDirToZip(file, fileName, zout);
                }
                else {
                    zout.putNextEntry(new ZipEntry(fileName));
                    copyFromFile(file, zout);
                    zout.closeEntry();
                    zout.flush();
                }
            }
        }
    }
    
    public static void copyFromFile(final File file, final OutputStream out) {
        BufferedInputStream bin = null;
        try {
            bin = new BufferedInputStream(new FileInputStream(file));
        }
        catch (final FileNotFoundException exp) {
            throw new IllegalArgumentException(exp);
        }
        try {
            copy(bin, out);
        }
        finally {
            Close(bin);
        }
    }
    
    public static void copyToFile(final InputStream in, final File file) {
        BufferedOutputStream bout = null;
        try {
            bout = new BufferedOutputStream(new FileOutputStream(file));
        }
        catch (final FileNotFoundException exp) {
            throw new IllegalArgumentException(exp);
        }
        try {
            copy(in, bout);
        }
        finally {
            Close(bout);
        }
    }
    
    private static void copy(final InputStream in, final OutputStream out) {
        final byte[] buffer = new byte[1024];
        try {
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        catch (final IOException exp) {
            throw new RuntimeException(exp);
        }
    }
    
    public static void Close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (final IOException exp) {
                exp.printStackTrace();
            }
        }
    }
}
