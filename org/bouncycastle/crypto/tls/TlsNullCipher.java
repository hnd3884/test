package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Arrays;
import java.io.IOException;
import org.bouncycastle.crypto.Digest;

public class TlsNullCipher implements TlsCipher
{
    protected TlsContext context;
    protected TlsMac writeMac;
    protected TlsMac readMac;
    
    public TlsNullCipher(final TlsContext context) {
        this.context = context;
        this.writeMac = null;
        this.readMac = null;
    }
    
    public TlsNullCipher(final TlsContext context, final Digest digest, final Digest digest2) throws IOException {
        if (digest == null != (digest2 == null)) {
            throw new TlsFatalAlert((short)80);
        }
        this.context = context;
        TlsMac tlsMac = null;
        TlsMac tlsMac2 = null;
        if (digest != null) {
            final int n = digest.getDigestSize() + digest2.getDigestSize();
            final byte[] calculateKeyBlock = TlsUtils.calculateKeyBlock(context, n);
            final int n2 = 0;
            tlsMac = new TlsMac(context, digest, calculateKeyBlock, n2, digest.getDigestSize());
            final int n3 = n2 + digest.getDigestSize();
            tlsMac2 = new TlsMac(context, digest2, calculateKeyBlock, n3, digest2.getDigestSize());
            if (n3 + digest2.getDigestSize() != n) {
                throw new TlsFatalAlert((short)80);
            }
        }
        if (context.isServer()) {
            this.writeMac = tlsMac2;
            this.readMac = tlsMac;
        }
        else {
            this.writeMac = tlsMac;
            this.readMac = tlsMac2;
        }
    }
    
    public int getPlaintextLimit(final int n) {
        int n2 = n;
        if (this.writeMac != null) {
            n2 -= this.writeMac.getSize();
        }
        return n2;
    }
    
    public byte[] encodePlaintext(final long n, final short n2, final byte[] array, final int n3, final int n4) throws IOException {
        if (this.writeMac == null) {
            return Arrays.copyOfRange(array, n3, n3 + n4);
        }
        final byte[] calculateMac = this.writeMac.calculateMac(n, n2, array, n3, n4);
        final byte[] array2 = new byte[n4 + calculateMac.length];
        System.arraycopy(array, n3, array2, 0, n4);
        System.arraycopy(calculateMac, 0, array2, n4, calculateMac.length);
        return array2;
    }
    
    public byte[] decodeCiphertext(final long n, final short n2, final byte[] array, final int n3, final int n4) throws IOException {
        if (this.readMac == null) {
            return Arrays.copyOfRange(array, n3, n3 + n4);
        }
        final int size = this.readMac.getSize();
        if (n4 < size) {
            throw new TlsFatalAlert((short)50);
        }
        final int n5 = n4 - size;
        if (!Arrays.constantTimeAreEqual(Arrays.copyOfRange(array, n3 + n5, n3 + n4), this.readMac.calculateMac(n, n2, array, n3, n5))) {
            throw new TlsFatalAlert((short)20);
        }
        return Arrays.copyOfRange(array, n3, n3 + n5);
    }
}
