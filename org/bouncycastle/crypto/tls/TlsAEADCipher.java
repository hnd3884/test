package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.KeyParameter;
import java.io.IOException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;

public class TlsAEADCipher implements TlsCipher
{
    public static final int NONCE_RFC5288 = 1;
    static final int NONCE_DRAFT_CHACHA20_POLY1305 = 2;
    protected TlsContext context;
    protected int macSize;
    protected int record_iv_length;
    protected AEADBlockCipher encryptCipher;
    protected AEADBlockCipher decryptCipher;
    protected byte[] encryptImplicitNonce;
    protected byte[] decryptImplicitNonce;
    protected int nonceMode;
    
    public TlsAEADCipher(final TlsContext tlsContext, final AEADBlockCipher aeadBlockCipher, final AEADBlockCipher aeadBlockCipher2, final int n, final int n2) throws IOException {
        this(tlsContext, aeadBlockCipher, aeadBlockCipher2, n, n2, 1);
    }
    
    TlsAEADCipher(final TlsContext context, final AEADBlockCipher aeadBlockCipher, final AEADBlockCipher aeadBlockCipher2, final int n, final int macSize, final int nonceMode) throws IOException {
        if (!TlsUtils.isTLSv12(context)) {
            throw new TlsFatalAlert((short)80);
        }
        int n2 = 0;
        switch (this.nonceMode = nonceMode) {
            case 1: {
                n2 = 4;
                this.record_iv_length = 8;
                break;
            }
            case 2: {
                n2 = 12;
                this.record_iv_length = 0;
                break;
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
        this.context = context;
        this.macSize = macSize;
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
        KeyParameter keyParameter3;
        KeyParameter keyParameter4;
        if (context.isServer()) {
            this.encryptCipher = aeadBlockCipher2;
            this.decryptCipher = aeadBlockCipher;
            this.encryptImplicitNonce = copyOfRange2;
            this.decryptImplicitNonce = copyOfRange;
            keyParameter3 = keyParameter2;
            keyParameter4 = keyParameter;
        }
        else {
            this.encryptCipher = aeadBlockCipher;
            this.decryptCipher = aeadBlockCipher2;
            this.encryptImplicitNonce = copyOfRange;
            this.decryptImplicitNonce = copyOfRange2;
            keyParameter3 = keyParameter;
            keyParameter4 = keyParameter2;
        }
        final byte[] array = new byte[n2 + this.record_iv_length];
        this.encryptCipher.init(true, new AEADParameters(keyParameter3, 8 * macSize, array));
        this.decryptCipher.init(false, new AEADParameters(keyParameter4, 8 * macSize, array));
    }
    
    public int getPlaintextLimit(final int n) {
        return n - this.macSize - this.record_iv_length;
    }
    
    public byte[] encodePlaintext(final long n, final short n2, final byte[] array, final int n3, final int n4) throws IOException {
        final byte[] array2 = new byte[this.encryptImplicitNonce.length + this.record_iv_length];
        switch (this.nonceMode) {
            case 1: {
                System.arraycopy(this.encryptImplicitNonce, 0, array2, 0, this.encryptImplicitNonce.length);
                TlsUtils.writeUint64(n, array2, this.encryptImplicitNonce.length);
                break;
            }
            case 2: {
                TlsUtils.writeUint64(n, array2, array2.length - 8);
                for (int i = 0; i < this.encryptImplicitNonce.length; ++i) {
                    final byte[] array3 = array2;
                    final int n5 = i;
                    array3[n5] ^= this.encryptImplicitNonce[i];
                }
                break;
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
        final byte[] array4 = new byte[this.record_iv_length + this.encryptCipher.getOutputSize(n4)];
        if (this.record_iv_length != 0) {
            System.arraycopy(array2, array2.length - this.record_iv_length, array4, 0, this.record_iv_length);
        }
        final int record_iv_length = this.record_iv_length;
        final AEADParameters aeadParameters = new AEADParameters(null, 8 * this.macSize, array2, this.getAdditionalData(n, n2, n4));
        int n7;
        try {
            this.encryptCipher.init(true, aeadParameters);
            final int n6 = record_iv_length + this.encryptCipher.processBytes(array, n3, n4, array4, record_iv_length);
            n7 = n6 + this.encryptCipher.doFinal(array4, n6);
        }
        catch (final Exception ex) {
            throw new TlsFatalAlert((short)80, ex);
        }
        if (n7 != array4.length) {
            throw new TlsFatalAlert((short)80);
        }
        return array4;
    }
    
    public byte[] decodeCiphertext(final long n, final short n2, final byte[] array, final int n3, final int n4) throws IOException {
        if (this.getPlaintextLimit(n4) < 0) {
            throw new TlsFatalAlert((short)50);
        }
        final byte[] array2 = new byte[this.decryptImplicitNonce.length + this.record_iv_length];
        switch (this.nonceMode) {
            case 1: {
                System.arraycopy(this.decryptImplicitNonce, 0, array2, 0, this.decryptImplicitNonce.length);
                System.arraycopy(array, n3, array2, array2.length - this.record_iv_length, this.record_iv_length);
                break;
            }
            case 2: {
                TlsUtils.writeUint64(n, array2, array2.length - 8);
                for (int i = 0; i < this.decryptImplicitNonce.length; ++i) {
                    final byte[] array3 = array2;
                    final int n5 = i;
                    array3[n5] ^= this.decryptImplicitNonce[i];
                }
                break;
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
        final int n6 = n3 + this.record_iv_length;
        final int n7 = n4 - this.record_iv_length;
        final int outputSize = this.decryptCipher.getOutputSize(n7);
        final byte[] array4 = new byte[outputSize];
        final int n8 = 0;
        final AEADParameters aeadParameters = new AEADParameters(null, 8 * this.macSize, array2, this.getAdditionalData(n, n2, outputSize));
        int n10;
        try {
            this.decryptCipher.init(false, aeadParameters);
            final int n9 = n8 + this.decryptCipher.processBytes(array, n6, n7, array4, n8);
            n10 = n9 + this.decryptCipher.doFinal(array4, n9);
        }
        catch (final Exception ex) {
            throw new TlsFatalAlert((short)20, ex);
        }
        if (n10 != array4.length) {
            throw new TlsFatalAlert((short)80);
        }
        return array4;
    }
    
    protected byte[] getAdditionalData(final long n, final short n2, final int n3) throws IOException {
        final byte[] array = new byte[13];
        TlsUtils.writeUint64(n, array, 0);
        TlsUtils.writeUint8(n2, array, 8);
        TlsUtils.writeVersion(this.context.getServerVersion(), array, 9);
        TlsUtils.writeUint16(n3, array, 11);
        return array;
    }
}
