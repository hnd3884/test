package org.apache.commons.compress.harmony.pack200;

import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.LogManager;
import java.util.Iterator;
import java.util.Collections;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.jar.JarOutputStream;
import java.io.OutputStream;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;
import java.util.logging.FileHandler;

public class PackingUtils
{
    private static PackingLogger packingLogger;
    
    public static void config(final PackingOptions options) throws IOException {
        final String logFileName = options.getLogFile();
        if (logFileName != null) {
            final FileHandler fileHandler = new FileHandler(logFileName, false);
            fileHandler.setFormatter(new SimpleFormatter());
            PackingUtils.packingLogger.addHandler(fileHandler);
            PackingUtils.packingLogger.setUseParentHandlers(false);
        }
        PackingUtils.packingLogger.setVerbose(options.isVerbose());
    }
    
    public static void log(final String message) {
        PackingUtils.packingLogger.log(Level.INFO, message);
    }
    
    public static void copyThroughJar(final JarInputStream jarInputStream, final OutputStream outputStream) throws IOException {
        final Manifest manifest = jarInputStream.getManifest();
        final JarOutputStream jarOutputStream = new JarOutputStream(outputStream, manifest);
        jarOutputStream.setComment("PACK200");
        log("Packed META-INF/MANIFEST.MF");
        final byte[] bytes = new byte[16384];
        JarEntry jarEntry;
        while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
            jarOutputStream.putNextEntry(jarEntry);
            int bytesRead;
            while ((bytesRead = jarInputStream.read(bytes)) != -1) {
                jarOutputStream.write(bytes, 0, bytesRead);
            }
            log("Packed " + jarEntry.getName());
        }
        jarInputStream.close();
        jarOutputStream.close();
    }
    
    public static void copyThroughJar(final JarFile jarFile, final OutputStream outputStream) throws IOException {
        final JarOutputStream jarOutputStream = new JarOutputStream(outputStream);
        jarOutputStream.setComment("PACK200");
        final byte[] bytes = new byte[16384];
        final Enumeration entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            final JarEntry jarEntry = entries.nextElement();
            jarOutputStream.putNextEntry(jarEntry);
            final InputStream inputStream = jarFile.getInputStream(jarEntry);
            int bytesRead;
            while ((bytesRead = inputStream.read(bytes)) != -1) {
                jarOutputStream.write(bytes, 0, bytesRead);
            }
            jarOutputStream.closeEntry();
            log("Packed " + jarEntry.getName());
        }
        jarFile.close();
        jarOutputStream.close();
    }
    
    public static List getPackingFileListFromJar(final JarInputStream jarInputStream, final boolean keepFileOrder) throws IOException {
        final List packingFileList = new ArrayList();
        final Manifest manifest = jarInputStream.getManifest();
        if (manifest != null) {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            manifest.write(baos);
            packingFileList.add(new Archive.PackingFile("META-INF/MANIFEST.MF", baos.toByteArray(), 0L));
        }
        JarEntry jarEntry;
        while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
            final byte[] bytes = readJarEntry(jarEntry, new BufferedInputStream(jarInputStream));
            packingFileList.add(new Archive.PackingFile(bytes, jarEntry));
        }
        if (!keepFileOrder) {
            reorderPackingFiles(packingFileList);
        }
        return packingFileList;
    }
    
    public static List getPackingFileListFromJar(final JarFile jarFile, final boolean keepFileOrder) throws IOException {
        final List packingFileList = new ArrayList();
        final Enumeration jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            final JarEntry jarEntry = jarEntries.nextElement();
            final byte[] bytes = readJarEntry(jarEntry, new BufferedInputStream(jarFile.getInputStream(jarEntry)));
            packingFileList.add(new Archive.PackingFile(bytes, jarEntry));
        }
        if (!keepFileOrder) {
            reorderPackingFiles(packingFileList);
        }
        return packingFileList;
    }
    
    private static byte[] readJarEntry(final JarEntry jarEntry, final InputStream inputStream) throws IOException {
        long size = jarEntry.getSize();
        if (size > 2147483647L) {
            throw new RuntimeException("Large Class!");
        }
        if (size < 0L) {
            size = 0L;
        }
        final byte[] bytes = new byte[(int)size];
        if (inputStream.read(bytes) != size) {
            throw new RuntimeException("Error reading from stream");
        }
        return bytes;
    }
    
    private static void reorderPackingFiles(final List packingFileList) {
        final Iterator iterator = packingFileList.iterator();
        while (iterator.hasNext()) {
            final Archive.PackingFile packingFile = iterator.next();
            if (packingFile.isDirectory()) {
                iterator.remove();
            }
        }
        Collections.sort((List<Object>)packingFileList, (arg0, arg1) -> {
            if (arg0 instanceof Archive.PackingFile && arg1 instanceof Archive.PackingFile) {
                final String fileName0 = ((Archive.PackingFile)arg0).getName();
                final String fileName2 = ((Archive.PackingFile)arg1).getName();
                if (fileName0.equals(fileName2)) {
                    return 0;
                }
                else if ("META-INF/MANIFEST.MF".equals(fileName0)) {
                    return -1;
                }
                else if ("META-INF/MANIFEST.MF".equals(fileName2)) {
                    return 1;
                }
                else {
                    return fileName0.compareTo(fileName2);
                }
            }
            else {
                throw new IllegalArgumentException();
            }
        });
    }
    
    static {
        PackingUtils.packingLogger = new PackingLogger("org.harmony.apache.pack200", null);
        LogManager.getLogManager().addLogger(PackingUtils.packingLogger);
    }
    
    private static class PackingLogger extends Logger
    {
        private boolean verbose;
        
        protected PackingLogger(final String name, final String resourceBundleName) {
            super(name, resourceBundleName);
            this.verbose = false;
        }
        
        @Override
        public void log(final LogRecord logRecord) {
            if (this.verbose) {
                super.log(logRecord);
            }
        }
        
        public void setVerbose(final boolean isVerbose) {
            this.verbose = isVerbose;
        }
    }
}
