package org.bouncycastle.jcajce;

import org.bouncycastle.util.Arrays;
import javax.crypto.interfaces.PBEKey;

public class PKCS12KeyWithParameters extends PKCS12Key implements PBEKey
{
    private final byte[] salt;
    private final int iterationCount;
    
    public PKCS12KeyWithParameters(final char[] array, final byte[] array2, final int iterationCount) {
        super(array);
        this.salt = Arrays.clone(array2);
        this.iterationCount = iterationCount;
    }
    
    public PKCS12KeyWithParameters(final char[] array, final boolean b, final byte[] array2, final int iterationCount) {
        super(array, b);
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
