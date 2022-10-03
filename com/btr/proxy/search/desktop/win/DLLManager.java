package com.btr.proxy.search.desktop.win;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileFilter;
import java.io.IOException;
import java.io.File;

final class DLLManager
{
    public static final String LIB_DIR_OVERRIDE = "proxy_vole_lib_dir";
    static final String TEMP_FILE_PREFIX = "proxy_vole";
    static final String DLL_EXTENSION = ".dll";
    static String LIB_NAME_BASE;
    static final String DEFAULT_LIB_FOLDER = "lib";
    
    static File findLibFile() throws IOException {
        final String libName = buildLibName();
        File libFile = getOverrideLibFile(libName);
        if (libFile == null || !libFile.exists()) {
            libFile = getDefaultLibFile(libName);
        }
        if (libFile == null || !libFile.exists()) {
            libFile = extractToTempFile(libName);
        }
        return libFile;
    }
    
    static void cleanupTempFiles() {
        final String tempFolder = System.getProperty("java.io.tmpdir");
        final File fldr = new File(tempFolder);
        final File[] arr$;
        final File[] oldFiles = arr$ = fldr.listFiles(new TempDLLFileFilter());
        for (final File tmp : arr$) {
            tmp.delete();
        }
    }
    
    private static File getDefaultLibFile(final String libName) {
        return new File("lib", libName);
    }
    
    private static File getOverrideLibFile(final String libName) {
        final String libDir = System.getProperty("proxy_vole_lib_dir");
        if (libDir == null || libDir.trim().length() == 0) {
            return null;
        }
        return new File(libDir, libName);
    }
    
    static File extractToTempFile(final String libName) throws IOException {
        final InputStream source = Win32ProxyUtils.class.getResourceAsStream("/lib/" + libName);
        final File tempFile = File.createTempFile("proxy_vole", ".dll");
        tempFile.deleteOnExit();
        final FileOutputStream destination = new FileOutputStream(tempFile);
        copy(source, destination);
        return tempFile;
    }
    
    private static void closeStream(final Closeable c) {
        try {
            c.close();
        }
        catch (final IOException ex) {}
    }
    
    static void copy(final InputStream source, final OutputStream dest) throws IOException {
        try {
            final byte[] buffer = new byte[1024];
            for (int read = 0; read >= 0; read = source.read(buffer)) {
                dest.write(buffer, 0, read);
            }
            dest.flush();
        }
        finally {
            closeStream(source);
            closeStream(dest);
        }
    }
    
    private static String buildLibName() {
        String arch = "w32";
        if (!System.getProperty("os.arch").equals("x86")) {
            arch = System.getProperty("os.arch");
        }
        return DLLManager.LIB_NAME_BASE + arch + ".dll";
    }
    
    static {
        DLLManager.LIB_NAME_BASE = "proxy_util_";
    }
    
    private static final class TempDLLFileFilter implements FileFilter
    {
        public boolean accept(final File pathname) {
            final String name = pathname.getName();
            return pathname.isFile() && name.startsWith("proxy_vole") && name.endsWith(".dll");
        }
    }
}
