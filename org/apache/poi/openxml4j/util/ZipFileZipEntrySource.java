package org.apache.poi.openxml4j.util;

import java.io.InputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import java.util.Enumeration;
import java.io.IOException;
import org.apache.commons.compress.archivers.zip.ZipFile;

public class ZipFileZipEntrySource implements ZipEntrySource
{
    private ZipFile zipArchive;
    
    public ZipFileZipEntrySource(final ZipFile zipFile) {
        this.zipArchive = zipFile;
    }
    
    @Override
    public void close() throws IOException {
        if (this.zipArchive != null) {
            this.zipArchive.close();
        }
        this.zipArchive = null;
    }
    
    @Override
    public boolean isClosed() {
        return this.zipArchive == null;
    }
    
    @Override
    public Enumeration<? extends ZipArchiveEntry> getEntries() {
        if (this.zipArchive == null) {
            throw new IllegalStateException("Zip File is closed");
        }
        return this.zipArchive.getEntries();
    }
    
    @Override
    public InputStream getInputStream(final ZipArchiveEntry entry) throws IOException {
        if (this.zipArchive == null) {
            throw new IllegalStateException("Zip File is closed");
        }
        return this.zipArchive.getInputStream(entry);
    }
    
    @Override
    public ZipArchiveEntry getEntry(final String path) {
        final String normalizedPath = path.replace('\\', '/');
        final ZipArchiveEntry entry = this.zipArchive.getEntry(normalizedPath);
        if (entry != null) {
            return entry;
        }
        final Enumeration<ZipArchiveEntry> zipArchiveEntryEnumeration = this.zipArchive.getEntries();
        while (zipArchiveEntryEnumeration.hasMoreElements()) {
            final ZipArchiveEntry ze = zipArchiveEntryEnumeration.nextElement();
            if (normalizedPath.equalsIgnoreCase(ze.getName().replace('\\', '/'))) {
                return ze;
            }
        }
        return null;
    }
}
