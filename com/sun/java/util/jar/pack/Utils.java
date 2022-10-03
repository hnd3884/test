package com.sun.java.util.jar.pack;

import java.io.FilterOutputStream;
import sun.util.logging.PlatformLogger;
import java.io.BufferedInputStream;
import java.util.Date;
import java.io.File;
import java.io.BufferedOutputStream;
import java.util.Iterator;
import java.util.Collections;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.jar.JarInputStream;
import java.io.IOException;
import java.util.jar.JarOutputStream;
import java.util.TimeZone;

class Utils
{
    static final String COM_PREFIX = "com.sun.java.util.jar.pack.";
    static final String METAINF = "META-INF";
    static final String DEBUG_VERBOSE = "com.sun.java.util.jar.pack.verbose";
    static final String DEBUG_DISABLE_NATIVE = "com.sun.java.util.jar.pack.disable.native";
    static final String PACK_DEFAULT_TIMEZONE = "com.sun.java.util.jar.pack.default.timezone";
    static final String UNPACK_MODIFICATION_TIME = "com.sun.java.util.jar.pack.unpack.modification.time";
    static final String UNPACK_STRIP_DEBUG = "com.sun.java.util.jar.pack.unpack.strip.debug";
    static final String UNPACK_REMOVE_PACKFILE = "com.sun.java.util.jar.pack.unpack.remove.packfile";
    static final String NOW = "now";
    static final String PACK_KEEP_CLASS_ORDER = "com.sun.java.util.jar.pack.keep.class.order";
    static final String PACK_ZIP_ARCHIVE_MARKER_COMMENT = "PACK200";
    static final String CLASS_FORMAT_ERROR = "com.sun.java.util.jar.pack.class.format.error";
    static final ThreadLocal<TLGlobals> currentInstance;
    private static TimeZone tz;
    private static int workingPackerCount;
    static final boolean nolog;
    static final boolean SORT_MEMBERS_DESCR_MAJOR;
    static final boolean SORT_HANDLES_KIND_MAJOR;
    static final boolean SORT_INDY_BSS_MAJOR;
    static final boolean SORT_BSS_BSM_MAJOR;
    static final Pack200Logger log;
    
    static TLGlobals getTLGlobals() {
        return Utils.currentInstance.get();
    }
    
    static PropMap currentPropMap() {
        final UnpackerImpl value = Utils.currentInstance.get();
        if (value instanceof PackerImpl) {
            return ((PackerImpl)value).props;
        }
        if (value instanceof UnpackerImpl) {
            return value.props;
        }
        return null;
    }
    
    static synchronized void changeDefaultTimeZoneToUtc() {
        if (Utils.workingPackerCount++ == 0) {
            Utils.tz = TimeZone.getDefault();
            TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        }
    }
    
    static synchronized void restoreDefaultTimeZone() {
        if (--Utils.workingPackerCount == 0) {
            if (Utils.tz != null) {
                TimeZone.setDefault(Utils.tz);
            }
            Utils.tz = null;
        }
    }
    
    static String getVersionString() {
        return "Pack200, Vendor: " + System.getProperty("java.vendor") + ", Version: " + Constants.MAX_PACKAGE_VERSION;
    }
    
    static void markJarFile(final JarOutputStream jarOutputStream) throws IOException {
        jarOutputStream.setComment("PACK200");
    }
    
    static void copyJarFile(final JarInputStream jarInputStream, final JarOutputStream jarOutputStream) throws IOException {
        if (jarInputStream.getManifest() != null) {
            jarOutputStream.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
            jarInputStream.getManifest().write(jarOutputStream);
            jarOutputStream.closeEntry();
        }
        final byte[] array = new byte[16384];
        JarEntry nextJarEntry;
        while ((nextJarEntry = jarInputStream.getNextJarEntry()) != null) {
            jarOutputStream.putNextEntry(nextJarEntry);
            int read;
            while (0 < (read = jarInputStream.read(array))) {
                jarOutputStream.write(array, 0, read);
            }
        }
        jarInputStream.close();
        markJarFile(jarOutputStream);
    }
    
    static void copyJarFile(final JarFile jarFile, final JarOutputStream jarOutputStream) throws IOException {
        final byte[] array = new byte[16384];
        for (final JarEntry jarEntry : Collections.list(jarFile.entries())) {
            jarOutputStream.putNextEntry(jarEntry);
            int read;
            while (0 < (read = jarFile.getInputStream(jarEntry).read(array))) {
                jarOutputStream.write(array, 0, read);
            }
        }
        jarFile.close();
        markJarFile(jarOutputStream);
    }
    
    static void copyJarFile(final JarInputStream jarInputStream, final OutputStream outputStream) throws IOException {
        try (final JarOutputStream jarOutputStream = new JarOutputStream(new NonCloser(new BufferedOutputStream(outputStream)))) {
            copyJarFile(jarInputStream, jarOutputStream);
        }
    }
    
    static void copyJarFile(final JarFile jarFile, final OutputStream outputStream) throws IOException {
        try (final JarOutputStream jarOutputStream = new JarOutputStream(new NonCloser(new BufferedOutputStream(outputStream)))) {
            copyJarFile(jarFile, jarOutputStream);
        }
    }
    
    static String getJarEntryName(final String s) {
        if (s == null) {
            return null;
        }
        return s.replace(File.separatorChar, '/');
    }
    
    static String zeString(final ZipEntry zipEntry) {
        return zipEntry.getSize() + "\t" + zipEntry.getMethod() + "\t" + zipEntry.getCompressedSize() + "\t" + ((zipEntry.getCompressedSize() > 0L) ? ((int)((1.0 - zipEntry.getCompressedSize() / (double)zipEntry.getSize()) * 100.0)) : 0) + "%\t" + new Date(zipEntry.getTime()) + "\t" + Long.toHexString(zipEntry.getCrc()) + "\t" + zipEntry.getName();
    }
    
    static byte[] readMagic(final BufferedInputStream bufferedInputStream) throws IOException {
        bufferedInputStream.mark(4);
        final byte[] array = new byte[4];
        for (int n = 0; n < array.length && 1 == bufferedInputStream.read(array, n, 1); ++n) {}
        bufferedInputStream.reset();
        return array;
    }
    
    static boolean isJarMagic(final byte[] array) {
        return array[0] == 80 && array[1] == 75 && array[2] >= 1 && array[2] < 8 && array[3] == array[2] + 1;
    }
    
    static boolean isPackMagic(final byte[] array) {
        return array[0] == -54 && array[1] == -2 && array[2] == -48 && array[3] == 13;
    }
    
    static boolean isGZIPMagic(final byte[] array) {
        return array[0] == 31 && array[1] == -117 && array[2] == 8;
    }
    
    private Utils() {
    }
    
    static {
        currentInstance = new ThreadLocal<TLGlobals>();
        Utils.workingPackerCount = 0;
        nolog = Boolean.getBoolean("com.sun.java.util.jar.pack.nolog");
        SORT_MEMBERS_DESCR_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.members.descr.major");
        SORT_HANDLES_KIND_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.handles.kind.major");
        SORT_INDY_BSS_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.indy.bss.major");
        SORT_BSS_BSM_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.bss.bsm.major");
        log = new Pack200Logger("java.util.jar.Pack200");
    }
    
    static class Pack200Logger
    {
        private final String name;
        private PlatformLogger log;
        
        Pack200Logger(final String name) {
            this.name = name;
        }
        
        private synchronized PlatformLogger getLogger() {
            if (this.log == null) {
                this.log = PlatformLogger.getLogger(this.name);
            }
            return this.log;
        }
        
        public void warning(final String s, final Object o) {
            this.getLogger().warning(s, o);
        }
        
        public void warning(final String s) {
            this.warning(s, null);
        }
        
        public void info(final String s) {
            if (Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose") > 0) {
                if (Utils.nolog) {
                    System.out.println(s);
                }
                else {
                    this.getLogger().info(s);
                }
            }
        }
        
        public void fine(final String s) {
            if (Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose") > 0) {
                System.out.println(s);
            }
        }
    }
    
    private static class NonCloser extends FilterOutputStream
    {
        NonCloser(final OutputStream outputStream) {
            super(outputStream);
        }
        
        @Override
        public void close() throws IOException {
            this.flush();
        }
    }
}
