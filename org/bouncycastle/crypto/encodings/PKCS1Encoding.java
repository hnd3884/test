package org.bouncycastle.crypto.encodings;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;

public class PKCS1Encoding implements AsymmetricBlockCipher
{
    @Deprecated
    public static final String STRICT_LENGTH_ENABLED_PROPERTY = "org.bouncycastle.pkcs1.strict";
    public static final String NOT_STRICT_LENGTH_ENABLED_PROPERTY = "org.bouncycastle.pkcs1.not_strict";
    private static final int HEADER_LENGTH = 10;
    private SecureRandom random;
    private AsymmetricBlockCipher engine;
    private boolean forEncryption;
    private boolean forPrivateKey;
    private boolean useStrictLength;
    private int pLen;
    private byte[] fallback;
    private byte[] blockBuffer;
    
    public PKCS1Encoding(final AsymmetricBlockCipher engine) {
        this.pLen = -1;
        this.fallback = null;
        this.engine = engine;
        this.useStrictLength = this.useStrict();
    }
    
    public PKCS1Encoding(final AsymmetricBlockCipher engine, final int pLen) {
        this.pLen = -1;
        this.fallback = null;
        this.engine = engine;
        this.useStrictLength = this.useStrict();
        this.pLen = pLen;
    }
    
    public PKCS1Encoding(final AsymmetricBlockCipher engine, final byte[] fallback) {
        this.pLen = -1;
        this.fallback = null;
        this.engine = engine;
        this.useStrictLength = this.useStrict();
        this.fallback = fallback;
        this.pLen = fallback.length;
    }
    
    private boolean useStrict() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            public Object run() {
                return System.getProperty("org.bouncycastle.pkcs1.strict");
            }
        });
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            public Object run() {
                return System.getProperty("org.bouncycastle.pkcs1.not_strict");
            }
        });
        if (s2 != null) {
            return !s2.equals("true");
        }
        return s == null || s.equals("true");
    }
    
    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        AsymmetricKeyParameter asymmetricKeyParameter;
        if (cipherParameters instanceof ParametersWithRandom) {
            final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            this.random = parametersWithRandom.getRandom();
            asymmetricKeyParameter = (AsymmetricKeyParameter)parametersWithRandom.getParameters();
        }
        else {
            asymmetricKeyParameter = (AsymmetricKeyParameter)cipherParameters;
            if (!asymmetricKeyParameter.isPrivate() && forEncryption) {
                this.random = new SecureRandom();
            }
        }
        this.engine.init(forEncryption, cipherParameters);
        this.forPrivateKey = asymmetricKeyParameter.isPrivate();
        this.forEncryption = forEncryption;
        this.blockBuffer = new byte[this.engine.getOutputBlockSize()];
        if (this.pLen > 0 && this.fallback == null && this.random == null) {
            throw new IllegalArgumentException("encoder requires random");
        }
    }
    
    public int getInputBlockSize() {
        final int inputBlockSize = this.engine.getInputBlockSize();
        if (this.forEncryption) {
            return inputBlockSize - 10;
        }
        return inputBlockSize;
    }
    
    public int getOutputBlockSize() {
        final int outputBlockSize = this.engine.getOutputBlockSize();
        if (this.forEncryption) {
            return outputBlockSize;
        }
        return outputBlockSize - 10;
    }
    
    public byte[] processBlock(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return this.encodeBlock(array, n, n2);
        }
        return this.decodeBlock(array, n, n2);
    }
    
    private byte[] encodeBlock(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (n2 > this.getInputBlockSize()) {
            throw new IllegalArgumentException("input data too large");
        }
        final byte[] array2 = new byte[this.engine.getInputBlockSize()];
        if (this.forPrivateKey) {
            array2[0] = 1;
            for (int i = 1; i != array2.length - n2 - 1; ++i) {
                array2[i] = -1;
            }
        }
        else {
            this.random.nextBytes(array2);
            array2[0] = 2;
            for (int j = 1; j != array2.length - n2 - 1; ++j) {
                while (array2[j] == 0) {
                    array2[j] = (byte)this.random.nextInt();
                }
            }
        }
        array2[array2.length - n2 - 1] = 0;
        System.arraycopy(array, n, array2, array2.length - n2, n2);
        return this.engine.processBlock(array2, 0, array2.length);
    }
    
    private static int checkPkcs1Encoding(final byte[] array, final int n) {
        int n2 = 0x0 | (array[0] ^ 0x2);
        for (int n3 = array.length - (n + 1), i = 1; i < n3; ++i) {
            final byte b = array[i];
            final int n4 = b | b >> 1;
            final int n5 = n4 | n4 >> 2;
            n2 |= ((n5 | n5 >> 4) & 0x1) - 1;
        }
        final int n6 = n2 | array[array.length - (n + 1)];
        final int n7 = n6 | n6 >> 1;
        final int n8 = n7 | n7 >> 2;
        return ~(((n8 | n8 >> 4) & 0x1) - 1);
    }
    
    private byte[] decodeBlockOrRandom(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (!this.forPrivateKey) {
            throw new InvalidCipherTextException("sorry, this method is only for decryption, not for signing");
        }
        final byte[] processBlock = this.engine.processBlock(array, n, n2);
        byte[] fallback;
        if (this.fallback == null) {
            fallback = new byte[this.pLen];
            this.random.nextBytes(fallback);
        }
        else {
            fallback = this.fallback;
        }
        final byte[] array2 = (this.useStrictLength & processBlock.length != this.engine.getOutputBlockSize()) ? this.blockBuffer : processBlock;
        final int checkPkcs1Encoding = checkPkcs1Encoding(array2, this.pLen);
        final byte[] array3 = new byte[this.pLen];
        for (int i = 0; i < this.pLen; ++i) {
            array3[i] = (byte)((array2[i + (array2.length - this.pLen)] & ~checkPkcs1Encoding) | (fallback[i] & checkPkcs1Encoding));
        }
        Arrays.fill(array2, (byte)0);
        return array3;
    }
    
    private byte[] decodeBlock(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (this.pLen != -1) {
            return this.decodeBlockOrRandom(array, n, n2);
        }
        final byte[] processBlock = this.engine.processBlock(array, n, n2);
        final boolean b = this.useStrictLength & processBlock.length != this.engine.getOutputBlockSize();
        byte[] blockBuffer;
        if (processBlock.length < this.getOutputBlockSize()) {
            blockBuffer = this.blockBuffer;
        }
        else {
            blockBuffer = processBlock;
        }
        final byte b2 = blockBuffer[0];
        boolean b3;
        if (this.forPrivateKey) {
            b3 = (b2 != 2);
        }
        else {
            b3 = (b2 != 1);
        }
        int start = this.findStart(b2, blockBuffer);
        ++start;
        if (b3 | start < 10) {
            Arrays.fill(blockBuffer, (byte)0);
            throw new InvalidCipherTextException("block incorrect");
        }
        if (b) {
            Arrays.fill(blockBuffer, (byte)0);
            throw new InvalidCipherTextException("block incorrect size");
        }
        final byte[] array2 = new byte[blockBuffer.length - start];
        System.arraycopy(blockBuffer, start, array2, 0, array2.length);
        return array2;
    }
    
    private int findStart(final byte b, final byte[] array) throws InvalidCipherTextException {
        int n = -1;
        boolean b2 = false;
        for (int i = 1; i != array.length; ++i) {
            final byte b3 = array[i];
            if (b3 == 0 & n < 0) {
                n = i;
            }
            b2 |= (b == 1 & n < 0 & b3 != -1);
        }
        if (b2) {
            return -1;
        }
        return n;
    }
}
