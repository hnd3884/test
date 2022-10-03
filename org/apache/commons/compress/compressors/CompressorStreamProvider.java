package org.apache.commons.compress.compressors;

import java.util.Set;
import java.io.OutputStream;
import java.io.InputStream;

public interface CompressorStreamProvider
{
    CompressorInputStream createCompressorInputStream(final String p0, final InputStream p1, final boolean p2) throws CompressorException;
    
    CompressorOutputStream createCompressorOutputStream(final String p0, final OutputStream p1) throws CompressorException;
    
    Set<String> getInputStreamCompressorNames();
    
    Set<String> getOutputStreamCompressorNames();
}
