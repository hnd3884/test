package com.sun.xml.internal.org.jvnet.fastinfoset;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface EncodingAlgorithm
{
    Object decodeFromBytes(final byte[] p0, final int p1, final int p2) throws EncodingAlgorithmException;
    
    Object decodeFromInputStream(final InputStream p0) throws EncodingAlgorithmException, IOException;
    
    void encodeToOutputStream(final Object p0, final OutputStream p1) throws EncodingAlgorithmException, IOException;
    
    Object convertFromCharacters(final char[] p0, final int p1, final int p2) throws EncodingAlgorithmException;
    
    void convertToCharacters(final Object p0, final StringBuffer p1) throws EncodingAlgorithmException;
}
