package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Arrays;
import java.io.IOException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.StreamCipher;

public class TlsStreamCipher implements TlsCipher
{
    protected TlsContext context;
    protected StreamCipher encryptCipher;
    protected StreamCipher decryptCipher;
    protected TlsMac writeMac;
    protected TlsMac readMac;
    protected boolean usesNonce;
    
    public TlsStreamCipher(final TlsContext context, final StreamCipher encryptCipher, final StreamCipher decryptCipher, final Digest digest, final Digest digest2, final int n, final boolean usesNonce) throws IOException {
        final boolean server = context.isServer();
        this.context = context;
        this.usesNonce = usesNonce;
        this.encryptCipher = encryptCipher;
        this.decryptCipher = decryptCipher;
        final int n2 = 2 * n + digest.getDigestSize() + digest2.getDigestSize();
        final byte[] calculateKeyBlock = TlsUtils.calculateKeyBlock(context, n2);
        final int n3 = 0;
        final TlsMac tlsMac = new TlsMac(context, digest, calculateKeyBlock, n3, digest.getDigestSize());
        final int n4 = n3 + digest.getDigestSize();
        final TlsMac tlsMac2 = new TlsMac(context, digest2, calculateKeyBlock, n4, digest2.getDigestSize());
        final int n5 = n4 + digest2.getDigestSize();
        final KeyParameter keyParameter = new KeyParameter(calculateKeyBlock, n5, n);
        final int n6 = n5 + n;
        final KeyParameter keyParameter2 = new KeyParameter(calculateKeyBlock, n6, n);
        if (n6 + n != n2) {
            throw new TlsFatalAlert((short)80);
        }
        CipherParameters cipherParameters;
        CipherParameters cipherParameters2;
        if (server) {
            this.writeMac = tlsMac2;
            this.readMac = tlsMac;
            this.encryptCipher = decryptCipher;
            this.decryptCipher = encryptCipher;
            cipherParameters = keyParameter2;
            cipherParameters2 = keyParameter;
        }
        else {
            this.writeMac = tlsMac;
            this.readMac = tlsMac2;
            this.encryptCipher = encryptCipher;
            this.decryptCipher = decryptCipher;
            cipherParameters = keyParameter;
            cipherParameters2 = keyParameter2;
        }
        if (usesNonce) {
            final byte[] array = new byte[8];
            cipherParameters = new ParametersWithIV(cipherParameters, array);
            cipherParameters2 = new ParametersWithIV(cipherParameters2, array);
        }
        this.encryptCipher.init(true, cipherParameters);
        this.decryptCipher.init(false, cipherParameters2);
    }
    
    public int getPlaintextLimit(final int n) {
        return n - this.writeMac.getSize();
    }
    
    public byte[] encodePlaintext(final long n, final short n2, final byte[] array, final int n3, final int n4) {
        if (this.usesNonce) {
            this.updateIV(this.encryptCipher, true, n);
        }
        final byte[] array2 = new byte[n4 + this.writeMac.getSize()];
        this.encryptCipher.processBytes(array, n3, n4, array2, 0);
        final byte[] calculateMac = this.writeMac.calculateMac(n, n2, array, n3, n4);
        this.encryptCipher.processBytes(calculateMac, 0, calculateMac.length, array2, n4);
        return array2;
    }
    
    public byte[] decodeCiphertext(final long n, final short n2, final byte[] array, final int n3, final int n4) throws IOException {
        if (this.usesNonce) {
            this.updateIV(this.decryptCipher, false, n);
        }
        final int size = this.readMac.getSize();
        if (n4 < size) {
            throw new TlsFatalAlert((short)50);
        }
        final int n5 = n4 - size;
        final byte[] array2 = new byte[n4];
        this.decryptCipher.processBytes(array, n3, n4, array2, 0);
        this.checkMAC(n, n2, array2, n5, n4, array2, 0, n5);
        return Arrays.copyOfRange(array2, 0, n5);
    }
    
    protected void checkMAC(final long n, final short n2, final byte[] array, final int n3, final int n4, final byte[] array2, final int n5, final int n6) throws IOException {
        if (!Arrays.constantTimeAreEqual(Arrays.copyOfRange(array, n3, n4), this.readMac.calculateMac(n, n2, array2, n5, n6))) {
            throw new TlsFatalAlert((short)20);
        }
    }
    
    protected void updateIV(final StreamCipher streamCipher, final boolean b, final long n) {
        final byte[] array = new byte[8];
        TlsUtils.writeUint64(n, array, 0);
        streamCipher.init(b, new ParametersWithIV(null, array));
    }
}
