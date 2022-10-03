package org.apache.poi.openxml4j.util;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.io.IOException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Map;

public class ZipInputStreamZipEntrySource implements ZipEntrySource
{
    private final Map<String, ZipArchiveFakeEntry> zipEntries;
    private InputStream streamToClose;
    
    public ZipInputStreamZipEntrySource(final ZipArchiveThresholdInputStream inp) throws IOException {
        this.zipEntries = new HashMap<String, ZipArchiveFakeEntry>();
        while (true) {
            final ZipArchiveEntry zipEntry = inp.getNextEntry();
            if (zipEntry == null) {
                break;
            }
            this.zipEntries.put(zipEntry.getName(), new ZipArchiveFakeEntry(zipEntry, inp));
        }
        this.streamToClose = inp;
    }
    
    @Override
    public Enumeration<? extends ZipArchiveEntry> getEntries() {
        return Collections.enumeration((Collection<? extends ZipArchiveEntry>)this.zipEntries.values());
    }
    
    @Override
    public InputStream getInputStream(final ZipArchiveEntry zipEntry) {
        assert zipEntry instanceof ZipArchiveFakeEntry;
        return ((ZipArchiveFakeEntry)zipEntry).getInputStream();
    }
    
    @Override
    public void close() throws IOException {
        this.zipEntries.clear();
        this.streamToClose.close();
    }
    
    @Override
    public boolean isClosed() {
        return this.zipEntries.isEmpty();
    }
    
    @Override
    public ZipArchiveEntry getEntry(final String path) {
        final String normalizedPath = path.replace('\\', '/');
        final ZipArchiveEntry ze = this.zipEntries.get(normalizedPath);
        if (ze != null) {
            return ze;
        }
        for (final Map.Entry<String, ZipArchiveFakeEntry> fze : this.zipEntries.entrySet()) {
            if (normalizedPath.equalsIgnoreCase(fze.getKey())) {
                return fze.getValue();
            }
        }
        return null;
    }
}
