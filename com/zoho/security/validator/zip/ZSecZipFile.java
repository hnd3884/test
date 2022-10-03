package com.zoho.security.validator.zip;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.HashMap;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.zip.ZipFile;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

public class ZSecZipFile
{
    private SevenZFile sevenZFile;
    private ZipFile zipFile;
    boolean is7Zip;
    private String filePath;
    private Map<ZSecZipEntry, File> sevenZEntryVsTmpFile;
    
    public ZSecZipFile(final File inputFile) throws IOException {
        this(inputFile, false);
    }
    
    public ZSecZipFile(final File inputFile, final boolean is7ZipFile) throws IOException {
        this(inputFile, 1, is7ZipFile);
    }
    
    public ZSecZipFile(final File inputFile, final int mode, final boolean is7ZipFile) throws IOException {
        this(inputFile, mode, StandardCharsets.UTF_8, is7ZipFile);
    }
    
    public ZSecZipFile(final File inputFile, final Charset charset, final boolean is7ZipFile) throws IOException {
        this(inputFile, 1, charset, is7ZipFile);
    }
    
    public ZSecZipFile(final File inputFile, final int mode, final Charset charset, final boolean is7ZipFile) throws IOException {
        this.sevenZEntryVsTmpFile = new HashMap<ZSecZipEntry, File>();
        if (is7ZipFile) {
            this.sevenZFile = new SevenZFile(inputFile);
            this.filePath = inputFile.getPath();
            this.is7Zip = true;
        }
        else {
            this.zipFile = new ZipFile(inputFile, mode, charset);
        }
    }
    
    public ZipFile getZipFile() {
        return this.zipFile;
    }
    
    public SevenZFile get7ZipFile() {
        return this.sevenZFile;
    }
    
    public boolean is7ZipFile() {
        return this.is7Zip;
    }
    
    public String getName() {
        return this.is7Zip ? this.filePath : this.zipFile.getName();
    }
    
    public void close() throws IOException {
        if (this.is7Zip) {
            for (final File tempEntryFile : this.sevenZEntryVsTmpFile.values()) {
                tempEntryFile.delete();
            }
            this.sevenZFile.close();
        }
        else {
            this.zipFile.close();
        }
    }
    
    public Enumeration entries() {
        if (this.is7Zip) {
            return new IterableEnumeration(this.sevenZFile.getEntries().iterator());
        }
        return this.zipFile.entries();
    }
    
    public InputStream getInputStream(final ZSecZipEntry entry) throws IOException {
        if (this.is7Zip) {
            if (!this.sevenZEntryVsTmpFile.containsKey(entry)) {
                final File tmpFile = File.createTempFile("SFZEW_ZS_7ze_", ".tmp");
                final InputStream entryStream = this.sevenZFile.getInputStream(entry.getSevenZipEntry());
                this.writeStreamToFile(entryStream, tmpFile);
                this.sevenZEntryVsTmpFile.put(entry, tmpFile);
            }
            return new FileInputStream(this.sevenZEntryVsTmpFile.get(entry));
        }
        return this.zipFile.getInputStream(entry.getZipEntry());
    }
    
    protected void closeEntry(final ZSecZipEntry entry) {
        if (!this.is7Zip) {
            return;
        }
        final File tmpEntryFile = this.sevenZEntryVsTmpFile.get(entry);
        if (tmpEntryFile != null) {
            tmpEntryFile.delete();
            this.sevenZEntryVsTmpFile.remove(entry);
        }
    }
    
    private void writeStreamToFile(final InputStream inputStream, final File destFile) throws IOException {
        if (inputStream == null || destFile == null) {
            return;
        }
        final FileOutputStream outStream = new FileOutputStream(destFile);
        final byte[] buffer = new byte[8192];
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outStream != null) {
                outStream.close();
            }
        }
    }
    
    private class IterableEnumeration implements Enumeration
    {
        private final Iterator iter;
        
        public IterableEnumeration(final Iterator iter) {
            this.iter = iter;
        }
        
        @Override
        public boolean hasMoreElements() {
            return this.iter.hasNext();
        }
        
        @Override
        public Object nextElement() {
            return this.iter.next();
        }
    }
}
