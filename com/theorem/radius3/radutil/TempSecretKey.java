package com.theorem.radius3.radutil;

import java.security.Key;
import javax.crypto.SecretKey;

public class TempSecretKey implements SecretKey
{
    private byte[] a;
    private int b;
    
    public TempSecretKey(final byte[] array) {
        System.arraycopy(array, this.b = 0, this.a = new byte[array.length], 0, array.length);
    }
    
    public final String getAlgorithm() {
        return "None";
    }
    
    public final byte[] getEncoded() {
        return this.a;
    }
    
    public final String getFormat() {
        return "RAW";
    }
    
    public final boolean equals(final Object o) {
        return ((Key)o).getEncoded().equals(this.getEncoded());
    }
    
    public final int hashCode() {
        if (this.b == 0) {
            this.b = new String(this.a).hashCode();
        }
        return this.b;
    }
    
    public final String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("TempSecretKey@").append(this.hashCode()).append(": ");
        sb.append(Util.toHexString(this.getEncoded()));
        sb.append(" Format:").append(this.getFormat());
        sb.append(" Algorithm:").append(this.getAlgorithm());
        return sb.toString();
    }
}
