package org.bouncycastle.jcajce;

import org.bouncycastle.crypto.CharToByteConverter;

public class PBKDF1Key implements PBKDFKey
{
    private final char[] password;
    private final CharToByteConverter converter;
    
    public PBKDF1Key(final char[] array, final CharToByteConverter converter) {
        this.password = new char[array.length];
        this.converter = converter;
        System.arraycopy(array, 0, this.password, 0, array.length);
    }
    
    public char[] getPassword() {
        return this.password;
    }
    
    public String getAlgorithm() {
        return "PBKDF1";
    }
    
    public String getFormat() {
        return this.converter.getType();
    }
    
    public byte[] getEncoded() {
        return this.converter.convert(this.password);
    }
}
