package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.digests.LongDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;

public class TlsMac
{
    protected TlsContext context;
    protected byte[] secret;
    protected Mac mac;
    protected int digestBlockSize;
    protected int digestOverhead;
    protected int macLength;
    
    public TlsMac(final TlsContext context, final Digest digest, final byte[] array, final int n, final int n2) {
        this.context = context;
        final KeyParameter keyParameter = new KeyParameter(array, n, n2);
        this.secret = Arrays.clone(keyParameter.getKey());
        if (digest instanceof LongDigest) {
            this.digestBlockSize = 128;
            this.digestOverhead = 16;
        }
        else {
            this.digestBlockSize = 64;
            this.digestOverhead = 8;
        }
        if (TlsUtils.isSSL(context)) {
            this.mac = new SSL3Mac(digest);
            if (digest.getDigestSize() == 20) {
                this.digestOverhead = 4;
            }
        }
        else {
            this.mac = new HMac(digest);
        }
        this.mac.init(keyParameter);
        this.macLength = this.mac.getMacSize();
        if (context.getSecurityParameters().truncatedHMac) {
            this.macLength = Math.min(this.macLength, 10);
        }
    }
    
    public byte[] getMACSecret() {
        return this.secret;
    }
    
    public int getSize() {
        return this.macLength;
    }
    
    public byte[] calculateMac(final long n, final short n2, final byte[] array, final int n3, final int n4) {
        final ProtocolVersion serverVersion = this.context.getServerVersion();
        final boolean ssl = serverVersion.isSSL();
        final byte[] array2 = new byte[ssl ? 11 : 13];
        TlsUtils.writeUint64(n, array2, 0);
        TlsUtils.writeUint8(n2, array2, 8);
        if (!ssl) {
            TlsUtils.writeVersion(serverVersion, array2, 9);
        }
        TlsUtils.writeUint16(n4, array2, array2.length - 2);
        this.mac.update(array2, 0, array2.length);
        this.mac.update(array, n3, n4);
        final byte[] array3 = new byte[this.mac.getMacSize()];
        this.mac.doFinal(array3, 0);
        return this.truncate(array3);
    }
    
    public byte[] calculateMacConstantTime(final long n, final short n2, final byte[] array, final int n3, final int n4, final int n5, final byte[] array2) {
        final byte[] calculateMac = this.calculateMac(n, n2, array, n3, n4);
        final int n6 = TlsUtils.isSSL(this.context) ? 11 : 13;
        int n7 = this.getDigestBlockCount(n6 + n5) - this.getDigestBlockCount(n6 + n4);
        while (--n7 >= 0) {
            this.mac.update(array2, 0, this.digestBlockSize);
        }
        this.mac.update(array2[0]);
        this.mac.reset();
        return calculateMac;
    }
    
    protected int getDigestBlockCount(final int n) {
        return (n + this.digestOverhead) / this.digestBlockSize;
    }
    
    protected byte[] truncate(final byte[] array) {
        if (array.length <= this.macLength) {
            return array;
        }
        return Arrays.copyOf(array, this.macLength);
    }
}
