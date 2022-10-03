package org.bouncycastle.crypto.params;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.DerivationParameters;

public class HKDFParameters implements DerivationParameters
{
    private final byte[] ikm;
    private final boolean skipExpand;
    private final byte[] salt;
    private final byte[] info;
    
    private HKDFParameters(final byte[] array, final boolean skipExpand, final byte[] array2, final byte[] array3) {
        if (array == null) {
            throw new IllegalArgumentException("IKM (input keying material) should not be null");
        }
        this.ikm = Arrays.clone(array);
        this.skipExpand = skipExpand;
        if (array2 == null || array2.length == 0) {
            this.salt = null;
        }
        else {
            this.salt = Arrays.clone(array2);
        }
        if (array3 == null) {
            this.info = new byte[0];
        }
        else {
            this.info = Arrays.clone(array3);
        }
    }
    
    public HKDFParameters(final byte[] array, final byte[] array2, final byte[] array3) {
        this(array, false, array2, array3);
    }
    
    public static HKDFParameters skipExtractParameters(final byte[] array, final byte[] array2) {
        return new HKDFParameters(array, true, null, array2);
    }
    
    public static HKDFParameters defaultParameters(final byte[] array) {
        return new HKDFParameters(array, false, null, null);
    }
    
    public byte[] getIKM() {
        return Arrays.clone(this.ikm);
    }
    
    public boolean skipExtract() {
        return this.skipExpand;
    }
    
    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }
    
    public byte[] getInfo() {
        return Arrays.clone(this.info);
    }
}
