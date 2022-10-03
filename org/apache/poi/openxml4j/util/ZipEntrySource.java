package org.apache.poi.openxml4j.util;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import java.util.Enumeration;
import java.io.Closeable;

public interface ZipEntrySource extends Closeable
{
    Enumeration<? extends ZipArchiveEntry> getEntries();
    
    ZipArchiveEntry getEntry(final String p0);
    
    InputStream getInputStream(final ZipArchiveEntry p0) throws IOException;
    
    void close() throws IOException;
    
    boolean isClosed();
}
