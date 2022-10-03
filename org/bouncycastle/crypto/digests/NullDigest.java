package org.bouncycastle.crypto.digests;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.Digest;

public class NullDigest implements Digest
{
    private ByteArrayOutputStream bOut;
    
    public NullDigest() {
        this.bOut = new ByteArrayOutputStream();
    }
    
    public String getAlgorithmName() {
        return "NULL";
    }
    
    public int getDigestSize() {
        return this.bOut.size();
    }
    
    public void update(final byte b) {
        this.bOut.write(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.bOut.write(array, n, n2);
    }
    
    public int doFinal(final byte[] array, final int n) {
        final byte[] byteArray = this.bOut.toByteArray();
        System.arraycopy(byteArray, 0, array, n, byteArray.length);
        this.reset();
        return byteArray.length;
    }
    
    public void reset() {
        this.bOut.reset();
    }
}
