package org.apache.commons.compress.archivers;

import java.util.Set;
import java.io.OutputStream;
import java.io.InputStream;

public interface ArchiveStreamProvider
{
    ArchiveInputStream createArchiveInputStream(final String p0, final InputStream p1, final String p2) throws ArchiveException;
    
    ArchiveOutputStream createArchiveOutputStream(final String p0, final OutputStream p1, final String p2) throws ArchiveException;
    
    Set<String> getInputStreamArchiveNames();
    
    Set<String> getOutputStreamArchiveNames();
}
