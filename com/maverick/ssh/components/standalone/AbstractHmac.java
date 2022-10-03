package com.maverick.ssh.components.standalone;

import java.io.IOException;
import com.maverick.ssh.SshException;
import com.maverick.crypto.digests.HMac;
import com.maverick.ssh.components.SshHmac;

public class AbstractHmac implements SshHmac
{
    HMac c;
    String b;
    
    public AbstractHmac(final String b, final HMac c) {
        this.b = b;
        this.c = c;
    }
    
    public String getAlgorithm() {
        return this.b;
    }
    
    public int getMacLength() {
        return this.c.getOutputSize();
    }
    
    public void generate(final long n, final byte[] array, final int n2, final int n3, final byte[] array2, final int n4) {
        final byte[] array3 = new byte[this.c.getMacSize()];
        final byte[] array4 = { (byte)(n >> 24), (byte)(n >> 16), (byte)(n >> 8), (byte)(n >> 0) };
        this.c.update(array4, 0, array4.length);
        this.c.update(array, n2, n3);
        this.c.doFinal(array3, 0);
        System.arraycopy(array3, 0, array2, n4, this.c.getOutputSize());
    }
    
    public void init(final byte[] array) throws SshException {
        final byte[] array2 = new byte[this.c.getMacSize()];
        System.arraycopy(array, 0, array2, 0, array2.length);
        try {
            this.c.init(array2);
        }
        catch (final IOException ex) {
            throw new SshException(ex, 5);
        }
    }
    
    public boolean verify(final long n, final byte[] array, final int n2, final int n3, final byte[] array2, final int n4) {
        if (array.length < n2 + n3) {
            throw new RuntimeException("Not enough data for message and mac!");
        }
        if (array2.length - n4 < this.getMacLength()) {
            throw new RuntimeException(String.valueOf(this.getMacLength()) + " bytes of MAC data required!");
        }
        final byte[] array3 = new byte[this.c.getOutputSize()];
        this.generate(n, array, n2, n3, array3, 0);
        for (int i = 0; i < array3.length; ++i) {
            if (array3[i] != array2[n4 + i]) {
                return false;
            }
        }
        return true;
    }
    
    public void update(final byte[] array) {
        this.c.update(array, 0, array.length);
    }
    
    public byte[] doFinal() {
        final byte[] array = new byte[this.getMacLength()];
        this.c.doFinal(array, 0);
        return array;
    }
}
