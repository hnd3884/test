package cryptix.jce.provider.rsa;

import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchProviderException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.SecureRandom;
import java.security.Key;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import javax.crypto.CipherSpi;

public final class RSACipher_ECB_PKCS1 extends CipherSpi
{
    private BigInteger n;
    private BigInteger e;
    private BigInteger p;
    private BigInteger q;
    private BigInteger u;
    private boolean decrypt;
    
    protected final void engineSetMode(final String mode) throws NoSuchAlgorithmException {
        if (!mode.equalsIgnoreCase("ECB")) {
            throw new NoSuchAlgorithmException("Wrong mode type!");
        }
    }
    
    protected final void engineSetPadding(final String padding) throws NoSuchPaddingException {
        if (!padding.equalsIgnoreCase("PKCS1") && !padding.equalsIgnoreCase("PKCS#1") && !padding.equalsIgnoreCase("PKCS1Padding")) {
            throw new NoSuchPaddingException("Wrong padding scheme!");
        }
    }
    
    protected final int engineGetBlockSize() {
        return (this.n.bitLength() + 7) / 8;
    }
    
    protected final int engineGetOutputSize(final int inputLen) {
        return (inputLen < this.engineGetBlockSize() + 1) ? (this.engineGetBlockSize() + 1) : inputLen;
    }
    
    protected final byte[] engineGetIV() {
        return null;
    }
    
    protected final AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    protected final void engineInit(final int opmode, final Key key, final SecureRandom random) throws InvalidKeyException {
        if (!(key instanceof RSAPrivateKey) && !(key instanceof RSAPublicKey)) {
            throw new InvalidKeyException("Key must be instance of either RSAPublicKey or RSAPrivateKey!");
        }
        this.decrypt = (opmode == 2 || opmode == 6);
        if (this.decrypt) {
            this.n = ((RSAPrivateKey)key).getModulus();
            this.e = ((RSAPrivateKey)key).getPrivateExponent();
        }
        else {
            this.n = ((RSAPublicKey)key).getModulus();
            this.e = ((RSAPublicKey)key).getPublicExponent();
        }
        if (key instanceof RSAPrivateCrtKey) {
            this.p = ((RSAPrivateCrtKey)key).getPrimeP();
            this.q = ((RSAPrivateCrtKey)key).getPrimeQ();
            this.u = ((RSAPrivateCrtKey)key).getCrtCoefficient();
        }
        else {
            final BigInteger p = null;
            this.u = p;
            this.q = p;
            this.p = p;
        }
    }
    
    protected final void engineInit(final int opmode, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("This cipher do not support AlgorithmParameterSpecs");
    }
    
    protected final void engineInit(final int opmode, final Key key, final AlgorithmParameters params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("This cipher do not support AlgorithmParameters");
    }
    
    protected final byte[] engineUpdate(final byte[] input, final int inputOffset, final int inputLen) {
        throw new RuntimeException("You can't do an update when using PKCS1!");
    }
    
    protected final int engineUpdate(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws ShortBufferException {
        throw new RuntimeException("You can't do an update when using PKCS1!");
    }
    
    protected final byte[] engineDoFinal(final byte[] input, final int inputOffset, final int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        final byte[] o = new byte[this.engineGetOutputSize(inputLen)];
        int ret;
        try {
            ret = this.engineDoFinal(input, inputOffset, inputLen, o, 0);
            if (ret == o.length) {
                return o;
            }
        }
        catch (final ShortBufferException e) {
            throw new RuntimeException("PANIC: Should not happned!");
        }
        final byte[] r = new byte[ret];
        System.arraycopy(o, 0, r, 0, ret);
        return r;
    }
    
    protected final int engineDoFinal(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        if (output.length < this.engineGetOutputSize(inputLen)) {
            throw new ShortBufferException("Output buffer too small!");
        }
        final byte[] blub = new byte[inputLen];
        System.arraycopy(input, inputOffset, blub, 0, inputLen);
        if (this.decrypt) {
            final BigInteger bi = new BigInteger(1, blub);
            if (bi.compareTo(this.n) != -1) {
                throw new RuntimeException("TT");
            }
            final BigInteger res = RSAAlgorithm.rsa(bi, this.n, this.e, this.p, this.q, this.u);
            final byte[] b = res.toByteArray();
            return this.unpad(b, b.length, 0, output, outputOffset);
        }
        else {
            final BigInteger bi = new BigInteger(1, this.pad(blub, blub.length, 0, 2));
            if (bi.compareTo(this.n) != -1) {
                throw new RuntimeException("TT");
            }
            final BigInteger res = RSAAlgorithm.rsa(bi, this.n, this.e);
            if (res.compareTo(this.n) != -1) {
                throw new RuntimeException("TT");
            }
            final int blockSize = this.engineGetBlockSize();
            byte[] b = res.toByteArray();
            if (b.length - 1 > blockSize) {
                throw new RuntimeException("YY");
            }
            if (b.length > blockSize) {
                final byte[] t = new byte[blockSize];
                System.arraycopy(b, 1, t, 0, blockSize);
                b = t;
            }
            for (int i = 0; i < blockSize; ++i) {
                output[outputOffset + i] = 0;
            }
            final int bOff = blockSize - b.length;
            System.arraycopy(b, 0, output, outputOffset + bOff, b.length);
            return b.length + bOff;
        }
    }
    
    protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        final String format = key.getFormat();
        if (format == null || !format.equalsIgnoreCase("RAW")) {
            throw new InvalidKeyException("Wrong format on key!");
        }
        final byte[] buf = key.getEncoded();
        try {
            return this.engineDoFinal(buf, 0, buf.length);
        }
        catch (final BadPaddingException e) {
            throw new RuntimeException("PANIC: This should not happend!");
        }
    }
    
    protected Key engineUnwrap(final byte[] wrappedKey, final String wrappedKeyAlgorithm, final int wrappedKeyType) throws InvalidKeyException, NoSuchAlgorithmException {
        if (wrappedKeyType != 5) {
            throw new InvalidKeyException("Wrong keytype!");
        }
        try {
            KeyGenerator.getInstance(wrappedKeyAlgorithm, "Cryptix");
            final byte[] engineDoFinal = this.engineDoFinal(wrappedKey, 0, wrappedKey.length);
            final SecretKeySpec sks = new SecretKeySpec(engineDoFinal, 0, engineDoFinal.length, wrappedKeyAlgorithm);
            final SecretKeyFactory skf = SecretKeyFactory.getInstance(wrappedKeyAlgorithm);
            return skf.generateSecret(sks);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new NoSuchAlgorithmException("Algorithm not supported!");
        }
        catch (final NoSuchProviderException ex2) {
            throw new RuntimeException("PANIC: Should not happend!");
        }
        catch (final BadPaddingException ex3) {
            throw new RuntimeException("PANIC: This should not happend!");
        }
        catch (final IllegalBlockSizeException ex4) {
            throw new RuntimeException("PANIC: This should not happend!");
        }
        catch (final InvalidKeySpecException e) {
            throw new RuntimeException("PANIC: This should not happend!");
        }
    }
    
    private byte[] pad(final byte[] input, final int inputLen, final int offset, final int bt) throws BadPaddingException {
        final int k = (this.n.bitLength() + 7) / 8;
        if (inputLen > k - 11) {
            throw new BadPaddingException("Data to long for this modulus!");
        }
        final byte[] ed = new byte[k];
        final int padLen = k - 3 - inputLen;
        ed[0] = (ed[2 + padLen] = 0);
        switch (bt) {
            case 0: {
                for (int i = 1; i < 2 + padLen; ++i) {
                    ed[i] = 0;
                }
                break;
            }
            case 1: {
                ed[1] = 1;
                for (int i = 2; i < 2 + padLen; ++i) {
                    ed[i] = -1;
                }
                break;
            }
            case 2: {
                ed[1] = 2;
                final byte[] b = { 0 };
                final SecureRandom sr = new SecureRandom();
                for (int j = 2; j < 2 + padLen; ++j) {
                    b[0] = 0;
                    while (b[0] == 0) {
                        sr.nextBytes(b);
                    }
                    ed[j] = b[0];
                }
                break;
            }
            default: {
                throw new BadPaddingException("Wrong block type!");
            }
        }
        System.arraycopy(input, offset, ed, padLen + 3, inputLen);
        return ed;
    }
    
    private int unpad(final byte[] input, final int inputLen, final int inOffset, final byte[] output, final int outOffset) throws BadPaddingException {
        final int bt = input[inOffset];
        int padLen = 1;
        switch (bt) {
            case 0: {
                while (input[inOffset + padLen + 1] == 0) {
                    ++padLen;
                }
                break;
            }
            case 1:
            case 2: {
                while (input[inOffset + padLen] != 0) {
                    ++padLen;
                }
                break;
            }
            default: {
                throw new BadPaddingException("Wrong block type!");
            }
        }
        ++padLen;
        final int len = inputLen - inOffset - padLen;
        System.arraycopy(input, inOffset + padLen, output, outOffset, len);
        return len;
    }
}
