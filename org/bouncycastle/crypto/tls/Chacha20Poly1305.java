package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.StreamCipher;
import java.io.IOException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.engines.ChaCha7539Engine;

public class Chacha20Poly1305 implements TlsCipher
{
    private static final byte[] ZEROES;
    protected TlsContext context;
    protected ChaCha7539Engine encryptCipher;
    protected ChaCha7539Engine decryptCipher;
    protected byte[] encryptIV;
    protected byte[] decryptIV;
    
    public Chacha20Poly1305(final TlsContext context) throws IOException {
        if (!TlsUtils.isTLSv12(context)) {
            throw new TlsFatalAlert((short)80);
        }
        this.context = context;
        final int n = 32;
        final int n2 = 12;
        final int n3 = 2 * n + 2 * n2;
        final byte[] calculateKeyBlock = TlsUtils.calculateKeyBlock(context, n3);
        final int n4 = 0;
        final KeyParameter keyParameter = new KeyParameter(calculateKeyBlock, n4, n);
        final int n5 = n4 + n;
        final KeyParameter keyParameter2 = new KeyParameter(calculateKeyBlock, n5, n);
        final int n6 = n5 + n;
        final byte[] copyOfRange = Arrays.copyOfRange(calculateKeyBlock, n6, n6 + n2);
        final int n7 = n6 + n2;
        final byte[] copyOfRange2 = Arrays.copyOfRange(calculateKeyBlock, n7, n7 + n2);
        if (n7 + n2 != n3) {
            throw new TlsFatalAlert((short)80);
        }
        this.encryptCipher = new ChaCha7539Engine();
        this.decryptCipher = new ChaCha7539Engine();
        KeyParameter keyParameter3;
        KeyParameter keyParameter4;
        if (context.isServer()) {
            keyParameter3 = keyParameter2;
            keyParameter4 = keyParameter;
            this.encryptIV = copyOfRange2;
            this.decryptIV = copyOfRange;
        }
        else {
            keyParameter3 = keyParameter;
            keyParameter4 = keyParameter2;
            this.encryptIV = copyOfRange;
            this.decryptIV = copyOfRange2;
        }
        this.encryptCipher.init(true, new ParametersWithIV(keyParameter3, this.encryptIV));
        this.decryptCipher.init(false, new ParametersWithIV(keyParameter4, this.decryptIV));
    }
    
    public int getPlaintextLimit(final int n) {
        return n - 16;
    }
    
    public byte[] encodePlaintext(final long n, final short n2, final byte[] array, final int n3, final int n4) throws IOException {
        final KeyParameter initRecord = this.initRecord(this.encryptCipher, true, n, this.encryptIV);
        final byte[] array2 = new byte[n4 + 16];
        this.encryptCipher.processBytes(array, n3, n4, array2, 0);
        final byte[] calculateRecordMAC = this.calculateRecordMAC(initRecord, this.getAdditionalData(n, n2, n4), array2, 0, n4);
        System.arraycopy(calculateRecordMAC, 0, array2, n4, calculateRecordMAC.length);
        return array2;
    }
    
    public byte[] decodeCiphertext(final long n, final short n2, final byte[] array, final int n3, final int n4) throws IOException {
        if (this.getPlaintextLimit(n4) < 0) {
            throw new TlsFatalAlert((short)50);
        }
        final KeyParameter initRecord = this.initRecord(this.decryptCipher, false, n, this.decryptIV);
        final int n5 = n4 - 16;
        if (!Arrays.constantTimeAreEqual(this.calculateRecordMAC(initRecord, this.getAdditionalData(n, n2, n5), array, n3, n5), Arrays.copyOfRange(array, n3 + n5, n3 + n4))) {
            throw new TlsFatalAlert((short)20);
        }
        final byte[] array2 = new byte[n5];
        this.decryptCipher.processBytes(array, n3, n5, array2, 0);
        return array2;
    }
    
    protected KeyParameter initRecord(final StreamCipher streamCipher, final boolean b, final long n, final byte[] array) {
        streamCipher.init(b, new ParametersWithIV(null, this.calculateNonce(n, array)));
        return this.generateRecordMACKey(streamCipher);
    }
    
    protected byte[] calculateNonce(final long n, final byte[] array) {
        final byte[] array2 = new byte[12];
        TlsUtils.writeUint64(n, array2, 4);
        for (int i = 0; i < 12; ++i) {
            final byte[] array3 = array2;
            final int n2 = i;
            array3[n2] ^= array[i];
        }
        return array2;
    }
    
    protected KeyParameter generateRecordMACKey(final StreamCipher streamCipher) {
        final byte[] array = new byte[64];
        streamCipher.processBytes(array, 0, array.length, array, 0);
        final KeyParameter keyParameter = new KeyParameter(array, 0, 32);
        Arrays.fill(array, (byte)0);
        return keyParameter;
    }
    
    protected byte[] calculateRecordMAC(final KeyParameter keyParameter, final byte[] array, final byte[] array2, final int n, final int n2) {
        final Poly1305 poly1305 = new Poly1305();
        poly1305.init(keyParameter);
        this.updateRecordMACText(poly1305, array, 0, array.length);
        this.updateRecordMACText(poly1305, array2, n, n2);
        this.updateRecordMACLength(poly1305, array.length);
        this.updateRecordMACLength(poly1305, n2);
        final byte[] array3 = new byte[poly1305.getMacSize()];
        poly1305.doFinal(array3, 0);
        return array3;
    }
    
    protected void updateRecordMACLength(final Mac mac, final int n) {
        final byte[] longToLittleEndian = Pack.longToLittleEndian((long)n & 0xFFFFFFFFL);
        mac.update(longToLittleEndian, 0, longToLittleEndian.length);
    }
    
    protected void updateRecordMACText(final Mac mac, final byte[] array, final int n, final int n2) {
        mac.update(array, n, n2);
        final int n3 = n2 % 16;
        if (n3 != 0) {
            mac.update(Chacha20Poly1305.ZEROES, 0, 16 - n3);
        }
    }
    
    protected byte[] getAdditionalData(final long n, final short n2, final int n3) throws IOException {
        final byte[] array = new byte[13];
        TlsUtils.writeUint64(n, array, 0);
        TlsUtils.writeUint8(n2, array, 8);
        TlsUtils.writeVersion(this.context.getServerVersion(), array, 9);
        TlsUtils.writeUint16(n3, array, 11);
        return array;
    }
    
    static {
        ZEROES = new byte[15];
    }
}
