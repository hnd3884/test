package org.bouncycastle.jcajce;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.CharToByteConverter;

public class PBKDF2Key implements PBKDFKey
{
    private final char[] password;
    private final CharToByteConverter converter;
    
    public PBKDF2Key(final char[] array, final CharToByteConverter converter) {
        this.password = Arrays.clone(array);
        this.converter = converter;
    }
    
    public char[] getPassword() {
        return this.password;
    }
    
    public String getAlgorithm() {
        return "PBKDF2";
    }
    
    public String getFormat() {
        return this.converter.getType();
    }
    
    public byte[] getEncoded() {
        return this.converter.convert(this.password);
    }
}
