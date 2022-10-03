package sun.security.krb5.internal.util;

import java.io.IOException;
import sun.security.util.DerOutputStream;
import java.util.Arrays;
import sun.security.util.BitArray;

public class KerberosFlags
{
    BitArray bits;
    protected static final int BITS_PER_UNIT = 8;
    
    public KerberosFlags(final int n) throws IllegalArgumentException {
        this.bits = new BitArray(n);
    }
    
    public KerberosFlags(final int n, final byte[] array) throws IllegalArgumentException {
        this.bits = new BitArray(n, array);
        if (n != 32) {
            this.bits = new BitArray(Arrays.copyOf(this.bits.toBooleanArray(), 32));
        }
    }
    
    public KerberosFlags(final boolean[] array) {
        this.bits = new BitArray((array.length == 32) ? array : Arrays.copyOf(array, 32));
    }
    
    public void set(final int n, final boolean b) {
        this.bits.set(n, b);
    }
    
    public boolean get(final int n) {
        return this.bits.get(n);
    }
    
    public boolean[] toBooleanArray() {
        return this.bits.toBooleanArray();
    }
    
    public byte[] asn1Encode() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putUnalignedBitString(this.bits);
        return derOutputStream.toByteArray();
    }
    
    @Override
    public String toString() {
        return this.bits.toString();
    }
}
