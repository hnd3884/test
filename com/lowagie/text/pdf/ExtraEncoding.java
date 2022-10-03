package com.lowagie.text.pdf;

public interface ExtraEncoding
{
    byte[] charToByte(final String p0, final String p1);
    
    byte[] charToByte(final char p0, final String p1);
    
    String byteToChar(final byte[] p0, final String p1);
}
