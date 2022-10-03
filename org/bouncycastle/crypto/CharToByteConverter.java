package org.bouncycastle.crypto;

public interface CharToByteConverter
{
    String getType();
    
    byte[] convert(final char[] p0);
}
