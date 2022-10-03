package org.bouncycastle.jcajce;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.CharToByteConverter;
import javax.crypto.interfaces.PBEKey;

public class PBKDF2KeyWithParameters extends PBKDF2Key implements PBEKey
{
    private final byte[] salt;
    private final int iterationCount;
    
    public PBKDF2KeyWithParameters(final char[] array, final CharToByteConverter charToByteConverter, final byte[] array2, final int iterationCount) {
        super(array, charToByteConverter);
        this.salt = Arrays.clone(array2);
        this.iterationCount = iterationCount;
    }
    
    public byte[] getSalt() {
        return this.salt;
    }
    
    public int getIterationCount() {
        return this.iterationCount;
    }
}
