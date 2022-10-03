package org.apache.poi.openxml4j.util;

import java.io.ByteArrayInputStream;
import org.apache.poi.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

class ZipArchiveFakeEntry extends ZipArchiveEntry
{
    private final byte[] data;
    
    ZipArchiveFakeEntry(final ZipArchiveEntry entry, final InputStream inp) throws IOException {
        super(entry.getName());
        final long entrySize = entry.getSize();
        if (entrySize < -1L || entrySize >= 2147483647L) {
            throw new IOException("ZIP entry size is too large or invalid");
        }
        this.data = ((entrySize == -1L) ? IOUtils.toByteArray(inp) : IOUtils.toByteArray(inp, (int)entrySize));
    }
    
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.data);
    }
}
