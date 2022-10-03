package org.apache.poi.openxml4j.util;

import java.io.InputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import java.io.IOException;
import java.io.File;
import org.apache.commons.compress.archivers.zip.ZipFile;

public class ZipSecureFile extends ZipFile
{
    static double MIN_INFLATE_RATIO;
    static long MAX_ENTRY_SIZE;
    private static long MAX_TEXT_SIZE;
    private final String fileName;
    
    public static void setMinInflateRatio(final double ratio) {
        ZipSecureFile.MIN_INFLATE_RATIO = ratio;
    }
    
    public static double getMinInflateRatio() {
        return ZipSecureFile.MIN_INFLATE_RATIO;
    }
    
    public static void setMaxEntrySize(final long maxEntrySize) {
        if (maxEntrySize < 0L || maxEntrySize > 4294967295L) {
            throw new IllegalArgumentException("Max entry size is bounded [0-4GB], but had " + maxEntrySize);
        }
        ZipSecureFile.MAX_ENTRY_SIZE = maxEntrySize;
    }
    
    public static long getMaxEntrySize() {
        return ZipSecureFile.MAX_ENTRY_SIZE;
    }
    
    public static void setMaxTextSize(final long maxTextSize) {
        if (maxTextSize < 0L || maxTextSize > 4294967295L) {
            throw new IllegalArgumentException("Max text size is bounded [0-4GB], but had " + maxTextSize);
        }
        ZipSecureFile.MAX_TEXT_SIZE = maxTextSize;
    }
    
    public static long getMaxTextSize() {
        return ZipSecureFile.MAX_TEXT_SIZE;
    }
    
    public ZipSecureFile(final File file) throws IOException {
        super(file);
        this.fileName = file.getAbsolutePath();
    }
    
    public ZipSecureFile(final String name) throws IOException {
        super(name);
        this.fileName = new File(name).getAbsolutePath();
    }
    
    public ZipArchiveThresholdInputStream getInputStream(final ZipArchiveEntry entry) throws IOException {
        final ZipArchiveThresholdInputStream zatis = new ZipArchiveThresholdInputStream(super.getInputStream(entry));
        zatis.setEntry(entry);
        return zatis;
    }
    
    public String getName() {
        return this.fileName;
    }
    
    static {
        ZipSecureFile.MIN_INFLATE_RATIO = 0.01;
        ZipSecureFile.MAX_ENTRY_SIZE = 4294967295L;
        ZipSecureFile.MAX_TEXT_SIZE = 10485760L;
    }
}
