package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import javax.crypto.ShortBufferException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import sun.misc.HexDumpEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Hashtable;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;

final class CipherBox
{
    static final CipherBox NULL;
    private static final Debug debug;
    private final ProtocolVersion protocolVersion;
    private final Cipher cipher;
    private SecureRandom random;
    private final byte[] fixedIv;
    private final Key key;
    private final int mode;
    private final int tagSize;
    private final int recordIvSize;
    private final CipherSuite.CipherType cipherType;
    private static Hashtable<Integer, IvParameterSpec> masks;
    
    private CipherBox() {
        this.protocolVersion = ProtocolVersion.DEFAULT;
        this.cipher = null;
        this.cipherType = CipherSuite.CipherType.STREAM_CIPHER;
        this.fixedIv = new byte[0];
        this.key = null;
        this.mode = 1;
        this.random = null;
        this.tagSize = 0;
        this.recordIvSize = 0;
    }
    
    private CipherBox(final ProtocolVersion protocolVersion, final CipherSuite.BulkCipher bulkCipher, final SecretKey key, IvParameterSpec iv, SecureRandom random, final boolean encrypt) throws NoSuchAlgorithmException {
        try {
            this.protocolVersion = protocolVersion;
            this.cipher = JsseJce.getCipher(bulkCipher.transformation);
            this.mode = (encrypt ? 1 : 2);
            if (random == null) {
                random = JsseJce.getSecureRandom();
            }
            this.random = random;
            this.cipherType = bulkCipher.cipherType;
            if (iv == null && bulkCipher.ivSize != 0 && this.mode == 2 && protocolVersion.v >= ProtocolVersion.TLS11.v) {
                iv = getFixedMask(bulkCipher.ivSize);
            }
            if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
                bulkCipher.getClass();
                this.tagSize = 16;
                this.key = key;
                this.fixedIv = iv.getIV();
                if (this.fixedIv == null || this.fixedIv.length != bulkCipher.fixedIvSize) {
                    throw new RuntimeException("Improper fixed IV for AEAD");
                }
                this.recordIvSize = bulkCipher.ivSize - bulkCipher.fixedIvSize;
            }
            else {
                this.tagSize = 0;
                this.fixedIv = new byte[0];
                this.recordIvSize = 0;
                this.key = null;
                this.cipher.init(this.mode, key, iv, random);
            }
        }
        catch (final NoSuchAlgorithmException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new NoSuchAlgorithmException("Could not create cipher " + bulkCipher, e2);
        }
        catch (final ExceptionInInitializerError e3) {
            throw new NoSuchAlgorithmException("Could not create cipher " + bulkCipher, e3);
        }
    }
    
    static CipherBox newCipherBox(final ProtocolVersion version, final CipherSuite.BulkCipher cipher, final SecretKey key, final IvParameterSpec iv, final SecureRandom random, final boolean encrypt) throws NoSuchAlgorithmException {
        if (!cipher.allowed) {
            throw new NoSuchAlgorithmException("Unsupported cipher " + cipher);
        }
        if (cipher == CipherSuite.B_NULL) {
            return CipherBox.NULL;
        }
        return new CipherBox(version, cipher, key, iv, random, encrypt);
    }
    
    private static IvParameterSpec getFixedMask(final int ivSize) {
        if (CipherBox.masks == null) {
            CipherBox.masks = new Hashtable<Integer, IvParameterSpec>(5);
        }
        IvParameterSpec iv = CipherBox.masks.get(ivSize);
        if (iv == null) {
            iv = new IvParameterSpec(new byte[ivSize]);
            CipherBox.masks.put(ivSize, iv);
        }
        return iv;
    }
    
    int encrypt(final byte[] buf, final int offset, int len) {
        if (this.cipher == null) {
            return len;
        }
        try {
            final int blockSize = this.cipher.getBlockSize();
            if (this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER) {
                len = addPadding(buf, offset, len, blockSize);
            }
            if (CipherBox.debug != null && Debug.isOn("plaintext")) {
                try {
                    final HexDumpEncoder hd = new HexDumpEncoder();
                    System.out.println("Padded plaintext before ENCRYPTION:  len = " + len);
                    hd.encodeBuffer(new ByteArrayInputStream(buf, offset, len), System.out);
                }
                catch (final IOException ex) {}
            }
            if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
                try {
                    return this.cipher.doFinal(buf, offset, len, buf, offset);
                }
                catch (final IllegalBlockSizeException | BadPaddingException ibe) {
                    throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher.getProvider().getName(), ibe);
                }
            }
            final int newLen = this.cipher.update(buf, offset, len, buf, offset);
            if (newLen != len) {
                throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
            }
            return newLen;
        }
        catch (final ShortBufferException e) {
            throw new ArrayIndexOutOfBoundsException(e.toString());
        }
    }
    
    int encrypt(final ByteBuffer bb, final int outLimit) {
        int len = bb.remaining();
        if (this.cipher == null) {
            bb.position(bb.limit());
            return len;
        }
        final int pos = bb.position();
        final int blockSize = this.cipher.getBlockSize();
        if (this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER) {
            len = addPadding(bb, blockSize);
            bb.position(pos);
        }
        if (CipherBox.debug != null && Debug.isOn("plaintext")) {
            try {
                final HexDumpEncoder hd = new HexDumpEncoder();
                System.out.println("Padded plaintext before ENCRYPTION:  len = " + len);
                hd.encodeBuffer(bb.duplicate(), System.out);
            }
            catch (final IOException ex) {}
        }
        final ByteBuffer dup = bb.duplicate();
        if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
            try {
                final int outputSize = this.cipher.getOutputSize(dup.remaining());
                if (outputSize > bb.remaining()) {
                    if (outLimit < pos + outputSize) {
                        throw new ShortBufferException("need more space in output buffer");
                    }
                    bb.limit(pos + outputSize);
                }
                final int newLen = this.cipher.doFinal(dup, bb);
                if (newLen != outputSize) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
                }
                return newLen;
            }
            catch (final IllegalBlockSizeException | BadPaddingException | ShortBufferException ibse) {
                throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher.getProvider().getName(), ibse);
            }
        }
        int newLen2;
        try {
            newLen2 = this.cipher.update(dup, bb);
        }
        catch (final ShortBufferException sbe) {
            throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
        }
        if (bb.position() != dup.position()) {
            throw new RuntimeException("bytebuffer padding error");
        }
        if (newLen2 != len) {
            throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
        }
        return newLen2;
    }
    
    int decrypt(final byte[] buf, final int offset, final int len, final int tagLen) throws BadPaddingException {
        if (this.cipher == null) {
            return len;
        }
        try {
            int newLen = 0;
            Label_0132: {
                if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
                    try {
                        newLen = this.cipher.doFinal(buf, offset, len, buf, offset);
                        break Label_0132;
                    }
                    catch (final IllegalBlockSizeException ibse) {
                        throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher.getProvider().getName(), ibse);
                    }
                }
                newLen = this.cipher.update(buf, offset, len, buf, offset);
                if (newLen != len) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
                }
            }
            if (CipherBox.debug != null && Debug.isOn("plaintext")) {
                try {
                    final HexDumpEncoder hd = new HexDumpEncoder();
                    System.out.println("Padded plaintext after DECRYPTION:  len = " + newLen);
                    hd.encodeBuffer(new ByteArrayInputStream(buf, offset, newLen), System.out);
                }
                catch (final IOException ex) {}
            }
            if (this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER) {
                final int blockSize = this.cipher.getBlockSize();
                newLen = removePadding(buf, offset, newLen, tagLen, blockSize, this.protocolVersion);
                if (this.protocolVersion.v >= ProtocolVersion.TLS11.v && newLen < blockSize) {
                    throw new BadPaddingException("The length after padding removal (" + newLen + ") should be larger than <" + blockSize + "> since explicit IV used");
                }
            }
            return newLen;
        }
        catch (final ShortBufferException e) {
            throw new ArrayIndexOutOfBoundsException(e.toString());
        }
    }
    
    int decrypt(final ByteBuffer bb, final int tagLen) throws BadPaddingException {
        final int len = bb.remaining();
        if (this.cipher == null) {
            bb.position(bb.limit());
            return len;
        }
        try {
            final int pos = bb.position();
            final ByteBuffer dup = bb.duplicate();
            int newLen = 0;
            Label_0165: {
                if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
                    try {
                        newLen = this.cipher.doFinal(dup, bb);
                        break Label_0165;
                    }
                    catch (final IllegalBlockSizeException ibse) {
                        throw new RuntimeException("Cipher error in AEAD mode \"" + ibse.getMessage() + " \"in JCE provider " + this.cipher.getProvider().getName());
                    }
                }
                newLen = this.cipher.update(dup, bb);
                if (newLen != len) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
                }
            }
            bb.limit(pos + newLen);
            if (CipherBox.debug != null && Debug.isOn("plaintext")) {
                try {
                    final HexDumpEncoder hd = new HexDumpEncoder();
                    System.out.println("Padded plaintext after DECRYPTION:  len = " + newLen);
                    hd.encodeBuffer((ByteBuffer)bb.duplicate().position(pos), System.out);
                }
                catch (final IOException ex) {}
            }
            if (this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER) {
                final int blockSize = this.cipher.getBlockSize();
                bb.position(pos);
                newLen = removePadding(bb, tagLen, blockSize, this.protocolVersion);
                if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
                    if (newLen < blockSize) {
                        throw new BadPaddingException("The length after padding removal (" + newLen + ") should be larger than <" + blockSize + "> since explicit IV used");
                    }
                    bb.position(bb.limit());
                }
            }
            return newLen;
        }
        catch (final ShortBufferException e) {
            throw new ArrayIndexOutOfBoundsException(e.toString());
        }
    }
    
    private static int addPadding(final byte[] buf, int offset, final int len, final int blockSize) {
        int newlen = len + 1;
        if (newlen % blockSize != 0) {
            newlen += blockSize - 1;
            newlen -= newlen % blockSize;
        }
        final byte pad = (byte)(newlen - len);
        if (buf.length < newlen + offset) {
            throw new IllegalArgumentException("no space to pad buffer");
        }
        int i = 0;
        offset += len;
        while (i < pad) {
            buf[offset++] = (byte)(pad - 1);
            ++i;
        }
        return newlen;
    }
    
    private static int addPadding(final ByteBuffer bb, final int blockSize) {
        final int len = bb.remaining();
        int offset = bb.position();
        int newlen = len + 1;
        if (newlen % blockSize != 0) {
            newlen += blockSize - 1;
            newlen -= newlen % blockSize;
        }
        final byte pad = (byte)(newlen - len);
        bb.limit(newlen + offset);
        int i = 0;
        offset += len;
        while (i < pad) {
            bb.put(offset++, (byte)(pad - 1));
            ++i;
        }
        bb.position(offset);
        bb.limit(offset);
        return newlen;
    }
    
    private static int[] checkPadding(final byte[] buf, final int offset, final int len, final byte pad) {
        if (len <= 0) {
            throw new RuntimeException("padding len must be positive");
        }
        final int[] results = { 0, 0 };
        int i = 0;
        while (i <= 256) {
            for (int j = 0; j < len && i <= 256; ++j, ++i) {
                if (buf[offset + j] != pad) {
                    final int[] array = results;
                    final int n = 0;
                    ++array[n];
                }
                else {
                    final int[] array2 = results;
                    final int n2 = 1;
                    ++array2[n2];
                }
            }
        }
        return results;
    }
    
    private static int[] checkPadding(final ByteBuffer bb, final byte pad) {
        if (!bb.hasRemaining()) {
            throw new RuntimeException("hasRemaining() must be positive");
        }
        final int[] results = { 0, 0 };
        bb.mark();
        int i = 0;
        while (i <= 256) {
            while (bb.hasRemaining() && i <= 256) {
                if (bb.get() != pad) {
                    final int[] array = results;
                    final int n = 0;
                    ++array[n];
                }
                else {
                    final int[] array2 = results;
                    final int n2 = 1;
                    ++array2[n2];
                }
                ++i;
            }
            bb.reset();
        }
        return results;
    }
    
    private static int removePadding(final byte[] buf, final int offset, final int len, final int tagLen, final int blockSize, final ProtocolVersion protocolVersion) throws BadPaddingException {
        final int padOffset = offset + len - 1;
        final int padLen = buf[padOffset] & 0xFF;
        final int newLen = len - (padLen + 1);
        if (newLen - tagLen < 0) {
            checkPadding(buf, offset, len, (byte)(padLen & 0xFF));
            throw new BadPaddingException("Invalid Padding length: " + padLen);
        }
        final int[] results = checkPadding(buf, offset + newLen, padLen + 1, (byte)(padLen & 0xFF));
        if (protocolVersion.v >= ProtocolVersion.TLS10.v) {
            if (results[0] != 0) {
                throw new BadPaddingException("Invalid TLS padding data");
            }
        }
        else if (padLen > blockSize) {
            throw new BadPaddingException("Padding length (" + padLen + ") of SSLv3 message should not be bigger than the block size (" + blockSize + ")");
        }
        return newLen;
    }
    
    private static int removePadding(final ByteBuffer bb, final int tagLen, final int blockSize, final ProtocolVersion protocolVersion) throws BadPaddingException {
        final int len = bb.remaining();
        final int offset = bb.position();
        final int padOffset = offset + len - 1;
        final int padLen = bb.get(padOffset) & 0xFF;
        final int newLen = len - (padLen + 1);
        if (newLen - tagLen < 0) {
            checkPadding(bb.duplicate(), (byte)(padLen & 0xFF));
            throw new BadPaddingException("Invalid Padding length: " + padLen);
        }
        final int[] results = checkPadding((ByteBuffer)bb.duplicate().position(offset + newLen), (byte)(padLen & 0xFF));
        if (protocolVersion.v >= ProtocolVersion.TLS10.v) {
            if (results[0] != 0) {
                throw new BadPaddingException("Invalid TLS padding data");
            }
        }
        else if (padLen > blockSize) {
            throw new BadPaddingException("Padding length (" + padLen + ") of SSLv3 message should not be bigger than the block size (" + blockSize + ")");
        }
        bb.position(offset + newLen);
        bb.limit(offset + newLen);
        return newLen;
    }
    
    void dispose() {
        try {
            if (this.cipher != null) {
                this.cipher.doFinal();
            }
        }
        catch (final Exception ex) {}
    }
    
    boolean isCBCMode() {
        return this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER;
    }
    
    boolean isAEADMode() {
        return this.cipherType == CipherSuite.CipherType.AEAD_CIPHER;
    }
    
    boolean isNullCipher() {
        return this.cipher == null;
    }
    
    int getExplicitNonceSize() {
        switch (this.cipherType) {
            case BLOCK_CIPHER: {
                if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
                    return this.cipher.getBlockSize();
                }
                break;
            }
            case AEAD_CIPHER: {
                return this.recordIvSize;
            }
        }
        return 0;
    }
    
    int applyExplicitNonce(final Authenticator authenticator, final byte contentType, final ByteBuffer bb) throws BadPaddingException {
        switch (this.cipherType) {
            case BLOCK_CIPHER: {
                final int tagLen = (authenticator instanceof MAC) ? ((MAC)authenticator).MAClen() : 0;
                if (tagLen != 0 && !this.sanityCheck(tagLen, bb.remaining())) {
                    throw new BadPaddingException("ciphertext sanity check failed");
                }
                if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
                    return this.cipher.getBlockSize();
                }
                break;
            }
            case AEAD_CIPHER: {
                if (bb.remaining() < this.recordIvSize + this.tagSize) {
                    throw new BadPaddingException("Insufficient buffer remaining for AEAD cipher fragment (" + bb.remaining() + "). Needs to be more than or equal to IV size (" + this.recordIvSize + ") + tag size (" + this.tagSize + ")");
                }
                final byte[] iv = Arrays.copyOf(this.fixedIv, this.fixedIv.length + this.recordIvSize);
                bb.get(iv, this.fixedIv.length, this.recordIvSize);
                bb.position(bb.position() - this.recordIvSize);
                final GCMParameterSpec spec = new GCMParameterSpec(this.tagSize * 8, iv);
                try {
                    this.cipher.init(this.mode, this.key, spec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ikae) {
                    throw new RuntimeException("invalid key or spec in GCM mode", ikae);
                }
                final byte[] aad = authenticator.acquireAuthenticationBytes(contentType, bb.remaining() - this.recordIvSize - this.tagSize);
                this.cipher.updateAAD(aad);
                return this.recordIvSize;
            }
        }
        return 0;
    }
    
    int applyExplicitNonce(final Authenticator authenticator, final byte contentType, final byte[] buf, final int offset, final int cipheredLength) throws BadPaddingException {
        final ByteBuffer bb = ByteBuffer.wrap(buf, offset, cipheredLength);
        return this.applyExplicitNonce(authenticator, contentType, bb);
    }
    
    byte[] createExplicitNonce(final Authenticator authenticator, final byte contentType, final int fragmentLength) {
        byte[] nonce = new byte[0];
        switch (this.cipherType) {
            case BLOCK_CIPHER: {
                if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
                    nonce = new byte[this.cipher.getBlockSize()];
                    this.random.nextBytes(nonce);
                    break;
                }
                break;
            }
            case AEAD_CIPHER: {
                nonce = authenticator.sequenceNumber();
                final byte[] iv = Arrays.copyOf(this.fixedIv, this.fixedIv.length + nonce.length);
                System.arraycopy(nonce, 0, iv, this.fixedIv.length, nonce.length);
                final GCMParameterSpec spec = new GCMParameterSpec(this.tagSize * 8, iv);
                try {
                    this.cipher.init(this.mode, this.key, spec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ikae) {
                    throw new RuntimeException("invalid key or spec in GCM mode", ikae);
                }
                final byte[] aad = authenticator.acquireAuthenticationBytes(contentType, fragmentLength);
                this.cipher.updateAAD(aad);
                break;
            }
        }
        return nonce;
    }
    
    private boolean sanityCheck(final int tagLen, final int fragmentLen) {
        if (!this.isCBCMode()) {
            return fragmentLen >= tagLen;
        }
        final int blockSize = this.cipher.getBlockSize();
        if (fragmentLen % blockSize == 0) {
            int minimal = tagLen + 1;
            minimal = ((minimal >= blockSize) ? minimal : blockSize);
            if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
                minimal += blockSize;
            }
            return fragmentLen >= minimal;
        }
        return false;
    }
    
    static {
        NULL = new CipherBox();
        debug = Debug.getInstance("ssl");
    }
}
