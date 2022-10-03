package org.bouncycastle.crypto.tls;

import java.security.SecureRandom;
import java.io.IOException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.BlockCipher;

public class TlsBlockCipher implements TlsCipher
{
    protected TlsContext context;
    protected byte[] randomData;
    protected boolean useExplicitIV;
    protected boolean encryptThenMAC;
    protected BlockCipher encryptCipher;
    protected BlockCipher decryptCipher;
    protected TlsMac writeMac;
    protected TlsMac readMac;
    
    public TlsMac getWriteMac() {
        return this.writeMac;
    }
    
    public TlsMac getReadMac() {
        return this.readMac;
    }
    
    public TlsBlockCipher(final TlsContext context, final BlockCipher blockCipher, final BlockCipher blockCipher2, final Digest digest, final Digest digest2, final int n) throws IOException {
        this.context = context;
        this.randomData = new byte[256];
        context.getNonceRandomGenerator().nextBytes(this.randomData);
        this.useExplicitIV = TlsUtils.isTLSv11(context);
        this.encryptThenMAC = context.getSecurityParameters().encryptThenMAC;
        int n2 = 2 * n + digest.getDigestSize() + digest2.getDigestSize();
        if (!this.useExplicitIV) {
            n2 += blockCipher.getBlockSize() + blockCipher2.getBlockSize();
        }
        final byte[] calculateKeyBlock = TlsUtils.calculateKeyBlock(context, n2);
        final int n3 = 0;
        final TlsMac tlsMac = new TlsMac(context, digest, calculateKeyBlock, n3, digest.getDigestSize());
        final int n4 = n3 + digest.getDigestSize();
        final TlsMac tlsMac2 = new TlsMac(context, digest2, calculateKeyBlock, n4, digest2.getDigestSize());
        final int n5 = n4 + digest2.getDigestSize();
        final KeyParameter keyParameter = new KeyParameter(calculateKeyBlock, n5, n);
        final int n6 = n5 + n;
        final KeyParameter keyParameter2 = new KeyParameter(calculateKeyBlock, n6, n);
        int n7 = n6 + n;
        byte[] copyOfRange;
        byte[] copyOfRange2;
        if (this.useExplicitIV) {
            copyOfRange = new byte[blockCipher.getBlockSize()];
            copyOfRange2 = new byte[blockCipher2.getBlockSize()];
        }
        else {
            copyOfRange = Arrays.copyOfRange(calculateKeyBlock, n7, n7 + blockCipher.getBlockSize());
            final int n8 = n7 + blockCipher.getBlockSize();
            copyOfRange2 = Arrays.copyOfRange(calculateKeyBlock, n8, n8 + blockCipher2.getBlockSize());
            n7 = n8 + blockCipher2.getBlockSize();
        }
        if (n7 != n2) {
            throw new TlsFatalAlert((short)80);
        }
        ParametersWithIV parametersWithIV;
        ParametersWithIV parametersWithIV2;
        if (context.isServer()) {
            this.writeMac = tlsMac2;
            this.readMac = tlsMac;
            this.encryptCipher = blockCipher2;
            this.decryptCipher = blockCipher;
            parametersWithIV = new ParametersWithIV(keyParameter2, copyOfRange2);
            parametersWithIV2 = new ParametersWithIV(keyParameter, copyOfRange);
        }
        else {
            this.writeMac = tlsMac;
            this.readMac = tlsMac2;
            this.encryptCipher = blockCipher;
            this.decryptCipher = blockCipher2;
            parametersWithIV = new ParametersWithIV(keyParameter, copyOfRange);
            parametersWithIV2 = new ParametersWithIV(keyParameter2, copyOfRange2);
        }
        this.encryptCipher.init(true, parametersWithIV);
        this.decryptCipher.init(false, parametersWithIV2);
    }
    
    public int getPlaintextLimit(final int n) {
        final int blockSize = this.encryptCipher.getBlockSize();
        final int size = this.writeMac.getSize();
        int n2 = n;
        if (this.useExplicitIV) {
            n2 -= blockSize;
        }
        int n4;
        if (this.encryptThenMAC) {
            final int n3 = n2 - size;
            n4 = n3 - n3 % blockSize;
        }
        else {
            n4 = n2 - n2 % blockSize - size;
        }
        return --n4;
    }
    
    public byte[] encodePlaintext(final long n, final short n2, final byte[] array, final int n3, final int n4) {
        final int blockSize = this.encryptCipher.getBlockSize();
        final int size = this.writeMac.getSize();
        final ProtocolVersion serverVersion = this.context.getServerVersion();
        int n5 = n4;
        if (!this.encryptThenMAC) {
            n5 += size;
        }
        int n6 = blockSize - 1 - n5 % blockSize;
        if ((this.encryptThenMAC || !this.context.getSecurityParameters().truncatedHMac) && !serverVersion.isDTLS() && !serverVersion.isSSL()) {
            n6 += this.chooseExtraPadBlocks(this.context.getSecureRandom(), (255 - n6) / blockSize) * blockSize;
        }
        int n7 = n4 + size + n6 + 1;
        if (this.useExplicitIV) {
            n7 += blockSize;
        }
        final byte[] array2 = new byte[n7];
        int n8 = 0;
        if (this.useExplicitIV) {
            final byte[] array3 = new byte[blockSize];
            this.context.getNonceRandomGenerator().nextBytes(array3);
            this.encryptCipher.init(true, new ParametersWithIV(null, array3));
            System.arraycopy(array3, 0, array2, n8, blockSize);
            n8 += blockSize;
        }
        final int n9 = n8;
        System.arraycopy(array, n3, array2, n8, n4);
        int n10 = n8 + n4;
        if (!this.encryptThenMAC) {
            final byte[] calculateMac = this.writeMac.calculateMac(n, n2, array, n3, n4);
            System.arraycopy(calculateMac, 0, array2, n10, calculateMac.length);
            n10 += calculateMac.length;
        }
        for (int i = 0; i <= n6; ++i) {
            array2[n10++] = (byte)n6;
        }
        for (int j = n9; j < n10; j += blockSize) {
            this.encryptCipher.processBlock(array2, j, array2, j);
        }
        if (this.encryptThenMAC) {
            final byte[] calculateMac2 = this.writeMac.calculateMac(n, n2, array2, 0, n10);
            System.arraycopy(calculateMac2, 0, array2, n10, calculateMac2.length);
            final int n11 = n10 + calculateMac2.length;
        }
        return array2;
    }
    
    public byte[] decodeCiphertext(final long n, final short n2, final byte[] array, int n3, final int n4) throws IOException {
        final int blockSize = this.decryptCipher.getBlockSize();
        final int size = this.readMac.getSize();
        final int n5 = blockSize;
        int max;
        if (this.encryptThenMAC) {
            max = n5 + size;
        }
        else {
            max = Math.max(n5, size + 1);
        }
        if (this.useExplicitIV) {
            max += blockSize;
        }
        if (n4 < max) {
            throw new TlsFatalAlert((short)50);
        }
        int n6 = n4;
        if (this.encryptThenMAC) {
            n6 -= size;
        }
        if (n6 % blockSize != 0) {
            throw new TlsFatalAlert((short)21);
        }
        if (this.encryptThenMAC) {
            final int n7 = n3 + n4;
            if (!Arrays.constantTimeAreEqual(this.readMac.calculateMac(n, n2, array, n3, n4 - size), Arrays.copyOfRange(array, n7 - size, n7))) {
                throw new TlsFatalAlert((short)20);
            }
        }
        if (this.useExplicitIV) {
            this.decryptCipher.init(false, new ParametersWithIV(null, array, n3, blockSize));
            n3 += blockSize;
            n6 -= blockSize;
        }
        for (int i = 0; i < n6; i += blockSize) {
            this.decryptCipher.processBlock(array, n3 + i, array, n3 + i);
        }
        final int checkPaddingConstantTime = this.checkPaddingConstantTime(array, n3, n6, blockSize, this.encryptThenMAC ? 0 : size);
        boolean b = checkPaddingConstantTime == 0;
        int n8 = n6 - checkPaddingConstantTime;
        if (!this.encryptThenMAC) {
            final int n9;
            n8 = (n9 = n8 - size);
            final int n10 = n3 + n9;
            b |= !Arrays.constantTimeAreEqual(this.readMac.calculateMacConstantTime(n, n2, array, n3, n9, n6 - size, this.randomData), Arrays.copyOfRange(array, n10, n10 + size));
        }
        if (b) {
            throw new TlsFatalAlert((short)20);
        }
        return Arrays.copyOfRange(array, n3, n3 + n8);
    }
    
    protected int checkPaddingConstantTime(final byte[] array, final int n, final int n2, final int n3, final int n4) {
        final int n5 = n + n2;
        final byte b = array[n5 - 1];
        int n6 = (b & 0xFF) + 1;
        int i = 0;
        byte b2 = 0;
        if ((TlsUtils.isSSL(this.context) && n6 > n3) || n4 + n6 > n2) {
            n6 = 0;
        }
        else {
            int j = n5 - n6;
            do {
                b2 |= (byte)(array[j++] ^ b);
            } while (j < n5);
            i = n6;
            if (b2 != 0) {
                n6 = 0;
            }
        }
        byte[] randomData;
        for (randomData = this.randomData; i < 256; b2 |= (byte)(randomData[i++] ^ b)) {}
        final byte[] array2 = randomData;
        final int n7 = 0;
        array2[n7] ^= b2;
        return n6;
    }
    
    protected int chooseExtraPadBlocks(final SecureRandom secureRandom, final int n) {
        return Math.min(this.lowestBitSet(secureRandom.nextInt()), n);
    }
    
    protected int lowestBitSet(int n) {
        if (n == 0) {
            return 32;
        }
        int n2 = 0;
        while ((n & 0x1) == 0x0) {
            ++n2;
            n >>= 1;
        }
        return n2;
    }
}
