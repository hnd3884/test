package org.bouncycastle.jcajce.spec;

import org.bouncycastle.util.Arrays;
import javax.crypto.spec.IvParameterSpec;

public class AEADParameterSpec extends IvParameterSpec
{
    private final byte[] associatedData;
    private final int macSizeInBits;
    
    public AEADParameterSpec(final byte[] array, final int n) {
        this(array, n, null);
    }
    
    public AEADParameterSpec(final byte[] array, final int macSizeInBits, final byte[] array2) {
        super(array);
        this.macSizeInBits = macSizeInBits;
        this.associatedData = Arrays.clone(array2);
    }
    
    public int getMacSizeInBits() {
        return this.macSizeInBits;
    }
    
    public byte[] getAssociatedData() {
        return Arrays.clone(this.associatedData);
    }
    
    public byte[] getNonce() {
        return this.getIV();
    }
}
