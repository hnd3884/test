package org.openjsse.com.sun.crypto.provider;

import java.io.ByteArrayOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteOrder;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.AEADBadTagException;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import javax.crypto.ShortBufferException;
import java.security.MessageDigest;
import java.nio.ByteBuffer;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import org.openjsse.javax.crypto.spec.ChaCha20ParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Key;
import java.io.IOException;
import sun.security.util.DerValue;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.CipherSpi;

abstract class ChaCha20Cipher extends CipherSpi
{
    private static final int MODE_NONE = 0;
    private static final int MODE_AEAD = 1;
    private static final int STATE_CONST_0 = 1634760805;
    private static final int STATE_CONST_1 = 857760878;
    private static final int STATE_CONST_2 = 2036477234;
    private static final int STATE_CONST_3 = 1797285236;
    private static final int KEYSTREAM_SIZE = 64;
    private static final int KS_SIZE_INTS = 16;
    private static final int CIPHERBUF_BASE = 1024;
    private boolean initialized;
    protected int mode;
    private int direction;
    private boolean aadDone;
    private byte[] keyBytes;
    private byte[] nonce;
    private static final long MAX_UINT32 = 4294967295L;
    private long finalCounterValue;
    private long counter;
    private final int[] startState;
    private final byte[] keyStream;
    private int keyStrOffset;
    private static final int TAG_LENGTH = 16;
    private long aadLen;
    private long dataLen;
    private static final byte[] padBuf;
    private final byte[] lenBuf;
    protected String authAlgName;
    private Poly1305 authenticator;
    private ChaChaEngine engine;
    
    protected ChaCha20Cipher() {
        this.aadDone = false;
        this.startState = new int[16];
        this.keyStream = new byte[64];
        this.lenBuf = new byte[16];
    }
    
    @Override
    protected void engineSetMode(final String mode) throws NoSuchAlgorithmException {
        if (!mode.equalsIgnoreCase("None")) {
            throw new NoSuchAlgorithmException("Mode must be None");
        }
    }
    
    @Override
    protected void engineSetPadding(final String padding) throws NoSuchPaddingException {
        if (!padding.equalsIgnoreCase("NoPadding")) {
            throw new NoSuchPaddingException("Padding must be NoPadding");
        }
    }
    
    @Override
    protected int engineGetBlockSize() {
        return 0;
    }
    
    @Override
    protected int engineGetOutputSize(final int inputLen) {
        return this.engine.getOutputSize(inputLen, true);
    }
    
    @Override
    protected byte[] engineGetIV() {
        return this.nonce.clone();
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        AlgorithmParameters params = null;
        if (this.mode == 1) {
            try {
                params = AlgorithmParameters.getInstance("ChaCha20-Poly1305");
                params.init(new DerValue((byte)4, this.nonce).toByteArray());
            }
            catch (final NoSuchAlgorithmException | IOException exc) {
                throw new RuntimeException(exc);
            }
        }
        return params;
    }
    
    @Override
    protected void engineInit(final int opmode, final Key key, final SecureRandom random) throws InvalidKeyException {
        if (opmode != 2) {
            final byte[] newNonce = this.createRandomNonce(random);
            this.counter = 1L;
            this.init(opmode, key, newNonce);
            return;
        }
        throw new InvalidKeyException("Default parameter generation disallowed in DECRYPT and UNWRAP modes");
    }
    
    @Override
    protected void engineInit(final int opmode, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (params == null) {
            this.engineInit(opmode, key, random);
            return;
        }
        byte[] newNonce = null;
        switch (this.mode) {
            case 0: {
                if (!(params instanceof ChaCha20ParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("ChaCha20 algorithm requires ChaCha20ParameterSpec");
                }
                final ChaCha20ParameterSpec chaParams = (ChaCha20ParameterSpec)params;
                newNonce = chaParams.getNonce();
                this.counter = ((long)chaParams.getCounter() & 0xFFFFFFFFL);
                break;
            }
            case 1: {
                if (!(params instanceof IvParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("ChaCha20-Poly1305 requires IvParameterSpec");
                }
                final IvParameterSpec ivParams = (IvParameterSpec)params;
                newNonce = ivParams.getIV();
                if (newNonce.length != 12) {
                    throw new InvalidAlgorithmParameterException("ChaCha20-Poly1305 nonce must be 12 bytes in length");
                }
                break;
            }
            default: {
                throw new RuntimeException("ChaCha20 in unsupported mode");
            }
        }
        this.init(opmode, key, newNonce);
    }
    
    @Override
    protected void engineInit(final int opmode, final Key key, final AlgorithmParameters params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (params == null) {
            this.engineInit(opmode, key, random);
            return;
        }
        byte[] newNonce = null;
        switch (this.mode) {
            case 0: {
                throw new InvalidAlgorithmParameterException("AlgorithmParameters not supported");
            }
            case 1: {
                final String paramAlg = params.getAlgorithm();
                if (!paramAlg.equalsIgnoreCase("ChaCha20-Poly1305")) {
                    throw new InvalidAlgorithmParameterException("Invalid parameter type: " + paramAlg);
                }
                Label_0185: {
                    try {
                        final DerValue dv = new DerValue(params.getEncoded());
                        newNonce = dv.getOctetString();
                        if (newNonce.length != 12) {
                            throw new InvalidAlgorithmParameterException("ChaCha20-Poly1305 nonce must be 12 bytes in length");
                        }
                        break Label_0185;
                    }
                    catch (final IOException ioe) {
                        throw new InvalidAlgorithmParameterException(ioe);
                    }
                    break;
                }
                if (newNonce == null) {
                    newNonce = this.createRandomNonce(random);
                }
                this.init(opmode, key, newNonce);
                return;
            }
        }
        throw new RuntimeException("Invalid mode: " + this.mode);
    }
    
    @Override
    protected void engineUpdateAAD(final byte[] src, final int offset, final int len) {
        if (!this.initialized) {
            throw new IllegalStateException("Attempted to update AAD on uninitialized Cipher");
        }
        if (this.aadDone) {
            throw new IllegalStateException("Attempted to update AAD on Cipher after plaintext/ciphertext update");
        }
        if (this.mode != 1) {
            throw new IllegalStateException("Cipher is running in non-AEAD mode");
        }
        try {
            this.aadLen = Math.addExact(this.aadLen, len);
            this.authUpdate(src, offset, len);
        }
        catch (final ArithmeticException ae) {
            throw new IllegalStateException("AAD overflow", ae);
        }
    }
    
    @Override
    protected void engineUpdateAAD(final ByteBuffer src) {
        if (!this.initialized) {
            throw new IllegalStateException("Attempted to update AAD on uninitialized Cipher");
        }
        if (this.aadDone) {
            throw new IllegalStateException("Attempted to update AAD on Cipher after plaintext/ciphertext update");
        }
        if (this.mode != 1) {
            throw new IllegalStateException("Cipher is running in non-AEAD mode");
        }
        try {
            this.aadLen = Math.addExact(this.aadLen, src.limit() - src.position());
            this.authenticator.engineUpdate(src);
        }
        catch (final ArithmeticException ae) {
            throw new IllegalStateException("AAD overflow", ae);
        }
    }
    
    private byte[] createRandomNonce(final SecureRandom random) {
        final byte[] newNonce = new byte[12];
        final SecureRandom rand = (random != null) ? random : new SecureRandom();
        rand.nextBytes(newNonce);
        return newNonce;
    }
    
    private void init(final int opmode, final Key key, final byte[] newNonce) throws InvalidKeyException {
        if (opmode == 3 || opmode == 4) {
            throw new UnsupportedOperationException("WRAP_MODE and UNWRAP_MODE are not currently supported");
        }
        if (opmode != 1 && opmode != 2) {
            throw new InvalidKeyException("Unknown opmode: " + opmode);
        }
        final byte[] newKeyBytes = getEncodedKey(key);
        this.checkKeyAndNonce(newKeyBytes, newNonce);
        this.keyBytes = newKeyBytes;
        this.nonce = newNonce;
        this.setInitialState();
        if (this.mode == 0) {
            this.engine = new EngineStreamOnly();
        }
        else if (this.mode == 1) {
            if (opmode == 1) {
                this.engine = new EngineAEADEnc();
            }
            else {
                if (opmode != 2) {
                    throw new InvalidKeyException("Not encrypt or decrypt mode");
                }
                this.engine = new EngineAEADDec();
            }
        }
        this.finalCounterValue = this.counter + 4294967295L;
        this.generateKeystream();
        this.direction = opmode;
        this.aadDone = false;
        this.keyStrOffset = 0;
        this.initialized = true;
    }
    
    private void checkKeyAndNonce(final byte[] newKeyBytes, final byte[] newNonce) throws InvalidKeyException {
        if (MessageDigest.isEqual(newKeyBytes, this.keyBytes) && MessageDigest.isEqual(newNonce, this.nonce)) {
            throw new InvalidKeyException("Matching key and nonce from previous initialization");
        }
    }
    
    private static byte[] getEncodedKey(final Key key) throws InvalidKeyException {
        if (!"RAW".equals(key.getFormat())) {
            throw new InvalidKeyException("Key encoding format must be RAW");
        }
        final byte[] encodedKey = key.getEncoded();
        if (encodedKey == null || encodedKey.length != 32) {
            throw new InvalidKeyException("Key length must be 256 bits");
        }
        return encodedKey;
    }
    
    @Override
    protected byte[] engineUpdate(final byte[] in, final int inOfs, final int inLen) {
        final byte[] out = new byte[this.engine.getOutputSize(inLen, false)];
        try {
            this.engine.doUpdate(in, inOfs, inLen, out, 0);
        }
        catch (final ShortBufferException | KeyException exc) {
            throw new RuntimeException(exc);
        }
        return out;
    }
    
    @Override
    protected int engineUpdate(final byte[] in, final int inOfs, final int inLen, final byte[] out, final int outOfs) throws ShortBufferException {
        int bytesUpdated = 0;
        try {
            bytesUpdated = this.engine.doUpdate(in, inOfs, inLen, out, outOfs);
        }
        catch (final KeyException ke) {
            throw new RuntimeException(ke);
        }
        return bytesUpdated;
    }
    
    @Override
    protected byte[] engineDoFinal(final byte[] in, final int inOfs, final int inLen) throws AEADBadTagException {
        final byte[] output = new byte[this.engine.getOutputSize(inLen, true)];
        try {
            this.engine.doFinal(in, inOfs, inLen, output, 0);
        }
        catch (final ShortBufferException | KeyException exc) {
            throw new RuntimeException(exc);
        }
        finally {
            this.initialized = false;
        }
        return output;
    }
    
    @Override
    protected int engineDoFinal(final byte[] in, final int inOfs, final int inLen, final byte[] out, final int outOfs) throws ShortBufferException, AEADBadTagException {
        int bytesUpdated = 0;
        try {
            bytesUpdated = this.engine.doFinal(in, inOfs, inLen, out, outOfs);
        }
        catch (final KeyException ke) {
            throw new RuntimeException(ke);
        }
        finally {
            this.initialized = false;
        }
        return bytesUpdated;
    }
    
    @Override
    protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        throw new UnsupportedOperationException("Wrap operations are not supported");
    }
    
    @Override
    protected Key engineUnwrap(final byte[] wrappedKey, final String algorithm, final int type) throws InvalidKeyException, NoSuchAlgorithmException {
        throw new UnsupportedOperationException("Unwrap operations are not supported");
    }
    
    @Override
    protected int engineGetKeySize(final Key key) throws InvalidKeyException {
        final byte[] encodedKey = getEncodedKey(key);
        return encodedKey.length << 3;
    }
    
    private void setInitialState() throws InvalidKeyException {
        this.startState[0] = 1634760805;
        this.startState[1] = 857760878;
        this.startState[2] = 2036477234;
        this.startState[3] = 1797285236;
        for (int i = 0; i < 32; i += 4) {
            this.startState[i / 4 + 4] = ((this.keyBytes[i] & 0xFF) | (this.keyBytes[i + 1] << 8 & 0xFF00) | (this.keyBytes[i + 2] << 16 & 0xFF0000) | (this.keyBytes[i + 3] << 24 & 0xFF000000));
        }
        this.startState[12] = 0;
        for (int i = 0; i < 12; i += 4) {
            this.startState[i / 4 + 13] = ((this.nonce[i] & 0xFF) | (this.nonce[i + 1] << 8 & 0xFF00) | (this.nonce[i + 2] << 16 & 0xFF0000) | (this.nonce[i + 3] << 24 & 0xFF000000));
        }
    }
    
    private void generateKeystream() {
        chaCha20Block(this.startState, this.counter, this.keyStream);
        ++this.counter;
    }
    
    private static void chaCha20Block(final int[] initState, final long counter, final byte[] result) {
        int ws00 = 1634760805;
        int ws2 = 857760878;
        int ws3 = 2036477234;
        int ws4 = 1797285236;
        int ws5 = initState[4];
        int ws6 = initState[5];
        int ws7 = initState[6];
        int ws8 = initState[7];
        int ws9 = initState[8];
        int ws10 = initState[9];
        int ws11 = initState[10];
        int ws12 = initState[11];
        int ws13 = (int)counter;
        int ws14 = initState[13];
        int ws15 = initState[14];
        int ws16 = initState[15];
        for (int round = 0; round < 10; ++round) {
            ws00 += ws5;
            ws13 = Integer.rotateLeft(ws13 ^ ws00, 16);
            ws9 += ws13;
            ws5 = Integer.rotateLeft(ws5 ^ ws9, 12);
            ws00 += ws5;
            ws13 = Integer.rotateLeft(ws13 ^ ws00, 8);
            ws9 += ws13;
            ws5 = Integer.rotateLeft(ws5 ^ ws9, 7);
            ws2 += ws6;
            ws14 = Integer.rotateLeft(ws14 ^ ws2, 16);
            ws10 += ws14;
            ws6 = Integer.rotateLeft(ws6 ^ ws10, 12);
            ws2 += ws6;
            ws14 = Integer.rotateLeft(ws14 ^ ws2, 8);
            ws10 += ws14;
            ws6 = Integer.rotateLeft(ws6 ^ ws10, 7);
            ws3 += ws7;
            ws15 = Integer.rotateLeft(ws15 ^ ws3, 16);
            ws11 += ws15;
            ws7 = Integer.rotateLeft(ws7 ^ ws11, 12);
            ws3 += ws7;
            ws15 = Integer.rotateLeft(ws15 ^ ws3, 8);
            ws11 += ws15;
            ws7 = Integer.rotateLeft(ws7 ^ ws11, 7);
            ws4 += ws8;
            ws16 = Integer.rotateLeft(ws16 ^ ws4, 16);
            ws12 += ws16;
            ws8 = Integer.rotateLeft(ws8 ^ ws12, 12);
            ws4 += ws8;
            ws16 = Integer.rotateLeft(ws16 ^ ws4, 8);
            ws12 += ws16;
            ws8 = Integer.rotateLeft(ws8 ^ ws12, 7);
            ws00 += ws6;
            ws16 = Integer.rotateLeft(ws16 ^ ws00, 16);
            ws11 += ws16;
            ws6 = Integer.rotateLeft(ws6 ^ ws11, 12);
            ws00 += ws6;
            ws16 = Integer.rotateLeft(ws16 ^ ws00, 8);
            ws11 += ws16;
            ws6 = Integer.rotateLeft(ws6 ^ ws11, 7);
            ws2 += ws7;
            ws13 = Integer.rotateLeft(ws13 ^ ws2, 16);
            ws12 += ws13;
            ws7 = Integer.rotateLeft(ws7 ^ ws12, 12);
            ws2 += ws7;
            ws13 = Integer.rotateLeft(ws13 ^ ws2, 8);
            ws12 += ws13;
            ws7 = Integer.rotateLeft(ws7 ^ ws12, 7);
            ws3 += ws8;
            ws14 = Integer.rotateLeft(ws14 ^ ws3, 16);
            ws9 += ws14;
            ws8 = Integer.rotateLeft(ws8 ^ ws9, 12);
            ws3 += ws8;
            ws14 = Integer.rotateLeft(ws14 ^ ws3, 8);
            ws9 += ws14;
            ws8 = Integer.rotateLeft(ws8 ^ ws9, 7);
            ws4 += ws5;
            ws15 = Integer.rotateLeft(ws15 ^ ws4, 16);
            ws10 += ws15;
            ws5 = Integer.rotateLeft(ws5 ^ ws10, 12);
            ws4 += ws5;
            ws15 = Integer.rotateLeft(ws15 ^ ws4, 8);
            ws10 += ws15;
            ws5 = Integer.rotateLeft(ws5 ^ ws10, 7);
        }
        final ByteBuffer bb = ByteBuffer.allocate(64);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(ws00 + 1634760805);
        bb.putInt(ws2 + 857760878);
        bb.putInt(ws3 + 2036477234);
        bb.putInt(ws4 + 1797285236);
        bb.putInt(ws5 + initState[4]);
        bb.putInt(ws6 + initState[5]);
        bb.putInt(ws7 + initState[6]);
        bb.putInt(ws8 + initState[7]);
        bb.putInt(ws9 + initState[8]);
        bb.putInt(ws10 + initState[9]);
        bb.putInt(ws11 + initState[10]);
        bb.putInt(ws12 + initState[11]);
        bb.putInt(ws13 + (int)counter);
        bb.putInt(ws14 + initState[13]);
        bb.putInt(ws15 + initState[14]);
        bb.putInt(ws16 + initState[15]);
        bb.rewind();
        bb.get(result);
    }
    
    private void chaCha20Transform(final byte[] in, int inOff, final int inLen, final byte[] out, int outOff) throws KeyException {
        int xformLen;
        for (int remainingData = inLen; remainingData > 0; remainingData -= xformLen) {
            int ksRemain = this.keyStream.length - this.keyStrOffset;
            if (ksRemain <= 0) {
                if (this.counter > this.finalCounterValue) {
                    throw new KeyException("Counter exhausted.  Reinitialize with new key and/or nonce");
                }
                this.generateKeystream();
                this.keyStrOffset = 0;
                ksRemain = this.keyStream.length;
            }
            xformLen = Math.min(remainingData, ksRemain);
            xor(this.keyStream, this.keyStrOffset, in, inOff, out, outOff, xformLen);
            outOff += xformLen;
            inOff += xformLen;
            this.keyStrOffset += xformLen;
        }
    }
    
    private static void xor(final byte[] in1, int off1, final byte[] in2, int off2, final byte[] out, int outOff, int len) {
        final ByteBuffer bb = ByteBuffer.allocate(16);
        bb.order(ByteOrder.nativeOrder());
        while (len >= 8) {
            bb.rewind();
            bb.put(in1, off1, 8);
            bb.put(in2, off2, 8);
            final long v1 = bb.getLong(0);
            final long v2 = bb.getLong(8);
            bb.putLong(0, v1 ^ v2);
            bb.rewind();
            bb.get(out, outOff, 8);
            off1 += 8;
            off2 += 8;
            outOff += 8;
            len -= 8;
        }
        while (len > 0) {
            out[outOff] = (byte)(in1[off1] ^ in2[off2]);
            ++off1;
            ++off2;
            ++outOff;
            --len;
        }
    }
    
    private void initAuthenticator() throws InvalidKeyException {
        this.authenticator = new Poly1305();
        final byte[] serializedKey = new byte[64];
        chaCha20Block(this.startState, 0L, serializedKey);
        this.authenticator.engineInit(new SecretKeySpec(serializedKey, 0, 32, this.authAlgName), null);
        this.aadLen = 0L;
        this.dataLen = 0L;
    }
    
    private int authUpdate(final byte[] data, final int offset, final int length) {
        this.checkFromIndexSize(offset, length, data.length);
        this.authenticator.engineUpdate(data, offset, length);
        return length;
    }
    
    private void authFinalizeData(final byte[] data, final int dataOff, final int length, final byte[] out, final int outOff) throws ShortBufferException {
        if (data != null) {
            this.dataLen += this.authUpdate(data, dataOff, length);
        }
        this.authPad16(this.dataLen);
        this.authWriteLengths(this.aadLen, this.dataLen, this.lenBuf);
        this.authenticator.engineUpdate(this.lenBuf, 0, this.lenBuf.length);
        final byte[] tag = this.authenticator.engineDoFinal();
        this.checkFromIndexSize(outOff, tag.length, out.length);
        System.arraycopy(tag, 0, out, outOff, tag.length);
        this.aadLen = 0L;
        this.dataLen = 0L;
    }
    
    private void authPad16(final long dataLen) {
        this.authenticator.engineUpdate(ChaCha20Cipher.padBuf, 0, 16 - ((int)dataLen & 0xF) & 0xF);
    }
    
    private void authWriteLengths(final long aLen, final long dLen, final byte[] buf) {
        final ByteBuffer bb = ByteBuffer.allocate(16);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putLong(aLen);
        bb.putLong(dLen);
        bb.rewind();
        bb.get(buf, 0, 16);
    }
    
    private int checkFromIndexSize(final int fromIndex, final int size, final int length) throws IndexOutOfBoundsException {
        if ((length | fromIndex | size) < 0 || size > length - fromIndex) {
            throw new IndexOutOfBoundsException();
        }
        return fromIndex;
    }
    
    static {
        padBuf = new byte[16];
    }
    
    private final class EngineStreamOnly implements ChaChaEngine
    {
        @Override
        public int getOutputSize(final int inLength, final boolean isFinal) {
            return inLength;
        }
        
        @Override
        public int doUpdate(final byte[] in, final int inOff, final int inLen, final byte[] out, final int outOff) throws ShortBufferException, KeyException {
            if (ChaCha20Cipher.this.initialized) {
                try {
                    if (out == null) {
                        throw new ShortBufferException("Output buffer too small");
                    }
                    ChaCha20Cipher.this.checkFromIndexSize(outOff, inLen, out.length);
                }
                catch (final IndexOutOfBoundsException iobe) {
                    throw new ShortBufferException("Output buffer too small");
                }
                if (in != null) {
                    ChaCha20Cipher.this.checkFromIndexSize(inOff, inLen, in.length);
                    ChaCha20Cipher.this.chaCha20Transform(in, inOff, inLen, out, outOff);
                }
                return inLen;
            }
            throw new IllegalStateException("Must use either a different key or iv.");
        }
        
        @Override
        public int doFinal(final byte[] in, final int inOff, final int inLen, final byte[] out, final int outOff) throws ShortBufferException, KeyException {
            return this.doUpdate(in, inOff, inLen, out, outOff);
        }
    }
    
    private final class EngineAEADEnc implements ChaChaEngine
    {
        @Override
        public int getOutputSize(final int inLength, final boolean isFinal) {
            return isFinal ? Math.addExact(inLength, 16) : inLength;
        }
        
        private EngineAEADEnc() throws InvalidKeyException {
            ChaCha20Cipher.this.initAuthenticator();
            ChaCha20Cipher.this.counter = 1L;
        }
        
        @Override
        public int doUpdate(final byte[] in, final int inOff, final int inLen, final byte[] out, final int outOff) throws ShortBufferException, KeyException {
            if (ChaCha20Cipher.this.initialized) {
                if (!ChaCha20Cipher.this.aadDone) {
                    ChaCha20Cipher.this.authPad16(ChaCha20Cipher.this.aadLen);
                    ChaCha20Cipher.this.aadDone = true;
                }
                try {
                    if (out == null) {
                        throw new ShortBufferException("Output buffer too small");
                    }
                    ChaCha20Cipher.this.checkFromIndexSize(outOff, inLen, out.length);
                }
                catch (final IndexOutOfBoundsException iobe) {
                    throw new ShortBufferException("Output buffer too small");
                }
                if (in != null) {
                    ChaCha20Cipher.this.checkFromIndexSize(inOff, inLen, in.length);
                    ChaCha20Cipher.this.chaCha20Transform(in, inOff, inLen, out, outOff);
                    ChaCha20Cipher.this.dataLen += ChaCha20Cipher.this.authUpdate(out, outOff, inLen);
                }
                return inLen;
            }
            throw new IllegalStateException("Must use either a different key or iv.");
        }
        
        @Override
        public int doFinal(final byte[] in, final int inOff, final int inLen, final byte[] out, final int outOff) throws ShortBufferException, KeyException {
            if (inLen + 16 > out.length - outOff) {
                throw new ShortBufferException("Output buffer too small");
            }
            this.doUpdate(in, inOff, inLen, out, outOff);
            ChaCha20Cipher.this.authFinalizeData(null, 0, 0, out, outOff + inLen);
            ChaCha20Cipher.this.aadDone = false;
            return inLen + 16;
        }
    }
    
    private final class EngineAEADDec implements ChaChaEngine
    {
        private final ByteArrayOutputStream cipherBuf;
        private final byte[] tag;
        
        @Override
        public int getOutputSize(final int inLen, final boolean isFinal) {
            return isFinal ? Integer.max(Math.addExact(inLen - 16, this.cipherBuf.size()), 0) : 0;
        }
        
        private EngineAEADDec() throws InvalidKeyException {
            ChaCha20Cipher.this.initAuthenticator();
            ChaCha20Cipher.this.counter = 1L;
            this.cipherBuf = new ByteArrayOutputStream(1024);
            this.tag = new byte[16];
        }
        
        @Override
        public int doUpdate(final byte[] in, final int inOff, final int inLen, final byte[] out, final int outOff) {
            if (ChaCha20Cipher.this.initialized) {
                if (!ChaCha20Cipher.this.aadDone) {
                    ChaCha20Cipher.this.authPad16(ChaCha20Cipher.this.aadLen);
                    ChaCha20Cipher.this.aadDone = true;
                }
                if (in != null) {
                    ChaCha20Cipher.this.checkFromIndexSize(inOff, inLen, in.length);
                    this.cipherBuf.write(in, inOff, inLen);
                }
                return 0;
            }
            throw new IllegalStateException("Must use either a different key or iv.");
        }
        
        @Override
        public int doFinal(final byte[] in, final int inOff, final int inLen, final byte[] out, final int outOff) throws ShortBufferException, AEADBadTagException, KeyException {
            byte[] ctPlusTag;
            int ctPlusTagLen;
            if (this.cipherBuf.size() == 0 && inOff == 0) {
                this.doUpdate(null, inOff, inLen, out, outOff);
                ctPlusTag = in;
                ctPlusTagLen = inLen;
            }
            else {
                this.doUpdate(in, inOff, inLen, out, outOff);
                ctPlusTag = this.cipherBuf.toByteArray();
                ctPlusTagLen = ctPlusTag.length;
            }
            this.cipherBuf.reset();
            if (ctPlusTagLen < 16) {
                throw new AEADBadTagException("Input too short - need tag");
            }
            final int ctLen = ctPlusTagLen - 16;
            try {
                ChaCha20Cipher.this.checkFromIndexSize(outOff, ctLen, out.length);
            }
            catch (final IndexOutOfBoundsException ioobe) {
                throw new ShortBufferException("Output buffer too small");
            }
            ChaCha20Cipher.this.authFinalizeData(ctPlusTag, 0, ctLen, this.tag, 0);
            final ByteBuffer bb = ByteBuffer.allocate(32);
            bb.order(ByteOrder.nativeOrder());
            bb.put(ctPlusTag, ctLen, 16);
            bb.put(this.tag, 0, 16);
            final long tagCompare = (bb.getLong(0) ^ bb.getLong(16)) | (bb.getLong(8) ^ bb.getLong(24));
            if (tagCompare != 0L) {
                throw new AEADBadTagException("Tag mismatch");
            }
            ChaCha20Cipher.this.chaCha20Transform(ctPlusTag, 0, ctLen, out, outOff);
            ChaCha20Cipher.this.aadDone = false;
            return ctLen;
        }
    }
    
    public static final class ChaCha20Only extends ChaCha20Cipher
    {
        public ChaCha20Only() {
            this.mode = 0;
        }
    }
    
    public static final class ChaCha20Poly1305 extends ChaCha20Cipher
    {
        public ChaCha20Poly1305() {
            this.mode = 1;
            this.authAlgName = "Poly1305";
        }
    }
    
    interface ChaChaEngine
    {
        int getOutputSize(final int p0, final boolean p1);
        
        int doUpdate(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4) throws ShortBufferException, KeyException;
        
        int doFinal(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4) throws ShortBufferException, AEADBadTagException, KeyException;
    }
}
