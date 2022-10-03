package com.adventnet.tools.update.installer;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;

public class JarCompressor
{
    public static void compress(final File sourceDir, final File destFile) throws Exception {
        if (!sourceDir.isDirectory()) {
            return;
        }
        if (destFile.exists() && destFile.isDirectory()) {
            return;
        }
        compressFile(sourceDir, destFile);
    }
    
    public static void compressFile(File sourceDir, File destFile) throws Exception {
        destFile = destFile.getCanonicalFile();
        sourceDir = sourceDir.getCanonicalFile();
        final File[] files = sourceDir.listFiles();
        final FileOutputStream fos = new FileOutputStream(destFile);
        final BufferedOutputStream bos = new BufferedOutputStream(fos);
        final byte[] buffer = new byte[1024];
        final ZipOutputStream zos = new ZipOutputStream(bos);
        for (final File file : files) {
            if (!file.equals(destFile)) {
                final String fileName = file.getName();
                if (file.isDirectory() && isJar(file.getName())) {
                    final ZipEntry entry = new ZipEntry(fileName);
                    zos.putNextEntry(entry);
                    final String tempDir = System.getProperty("java.io.tmpdir");
                    final File tempDirFile = new File(tempDir);
                    final File tempFile = new File(tempDirFile, file.getName());
                    compressFile(file, tempFile);
                    final FileInputStream in = new FileInputStream(tempFile);
                    final BufferedInputStream bis = new BufferedInputStream(in);
                    int len;
                    while ((len = bis.read(buffer, 0, buffer.length)) != -1) {
                        zos.write(buffer, 0, len);
                    }
                    in.close();
                    bis.close();
                    zos.closeEntry();
                    tempFile.delete();
                }
                else if (file.isDirectory()) {
                    final ZipEntry entry = new ZipEntry(fileName + "/");
                    zos.putNextEntry(entry);
                    zos.closeEntry();
                    processDirectory(zos, file, fileName);
                }
                else if (file.isFile()) {
                    final ZipEntry entry = new ZipEntry(file.getName());
                    zos.putNextEntry(entry);
                    final FileInputStream in2 = new FileInputStream(file);
                    final BufferedInputStream bis2 = new BufferedInputStream(in2);
                    int len2;
                    while ((len2 = bis2.read(buffer, 0, buffer.length)) != -1) {
                        zos.write(buffer, 0, len2);
                    }
                    in2.close();
                    bis2.close();
                    zos.closeEntry();
                }
            }
        }
        zos.close();
        fos.close();
        bos.close();
    }
    
    private static void processDirectory(final ZipOutputStream zos, final File dir, String prefix) throws Exception {
        if (!prefix.endsWith("/")) {
            prefix += "/";
        }
        for (final File file : dir.listFiles()) {
            final String fileName = file.getName();
            if (file.isDirectory() && isJar(file.getName())) {
                final byte[] buffer = new byte[1024];
                final ZipEntry entry = new ZipEntry(prefix + fileName);
                zos.putNextEntry(entry);
                final String tempDir = System.getProperty("java.io.tmpdir");
                final File tempDirFile = new File(tempDir);
                final File tempFile = new File(tempDirFile, file.getName());
                compressFile(file, tempFile);
                final FileInputStream in = new FileInputStream(tempFile);
                final BufferedInputStream bis = new BufferedInputStream(in);
                int len;
                while ((len = bis.read(buffer, 0, buffer.length)) != -1) {
                    zos.write(buffer, 0, len);
                }
                in.close();
                bis.close();
                zos.closeEntry();
                tempFile.delete();
            }
            else if (file.isDirectory()) {
                final ZipEntry entry2 = new ZipEntry(prefix + fileName + "/");
                zos.putNextEntry(entry2);
                zos.closeEntry();
                processDirectory(zos, file, prefix + fileName);
            }
            else if (file.isFile()) {
                final byte[] buffer = new byte[1024];
                final ZipEntry entry = new ZipEntry(prefix + fileName);
                zos.putNextEntry(entry);
                final FileInputStream in2 = new FileInputStream(file);
                final BufferedInputStream bis2 = new BufferedInputStream(in2);
                int len2;
                while ((len2 = bis2.read(buffer, 0, buffer.length)) != -1) {
                    zos.write(buffer, 0, len2);
                }
                in2.close();
                bis2.close();
                zos.closeEntry();
            }
        }
    }
    
    private static boolean isJar(final String name) {
        return name.endsWith(".ear") || name.endsWith(".war") || name.endsWith(".jar") || name.endsWith(".sar") || name.endsWith(".rar");
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 2) {
            ConsoleOut.println("Usage:java JarCompressor [directory to be compressed]  [EEAR file name]");
            System.exit(1);
        }
        final File f = new File(args[0]);
        final File dest = new File(args[1]);
        compress(f, dest);
    }
}
