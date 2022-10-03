package org.bouncycastle.crypto.encodings;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricBlockCipher;

public class ISO9796d1Encoding implements AsymmetricBlockCipher
{
    private static final BigInteger SIXTEEN;
    private static final BigInteger SIX;
    private static byte[] shadows;
    private static byte[] inverse;
    private AsymmetricBlockCipher engine;
    private boolean forEncryption;
    private int bitSize;
    private int padBits;
    private BigInteger modulus;
    
    public ISO9796d1Encoding(final AsymmetricBlockCipher engine) {
        this.padBits = 0;
        this.engine = engine;
    }
    
    public AsymmetricBlockCipher getUnderlyingCipher() {
        return this.engine;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        RSAKeyParameters rsaKeyParameters;
        if (cipherParameters instanceof ParametersWithRandom) {
            rsaKeyParameters = (RSAKeyParameters)((ParametersWithRandom)cipherParameters).getParameters();
        }
        else {
            rsaKeyParameters = (RSAKeyParameters)cipherParameters;
        }
        this.engine.init(forEncryption, cipherParameters);
        this.modulus = rsaKeyParameters.getModulus();
        this.bitSize = this.modulus.bitLength();
        this.forEncryption = forEncryption;
    }
    
    public int getInputBlockSize() {
        final int inputBlockSize = this.engine.getInputBlockSize();
        if (this.forEncryption) {
            return (inputBlockSize + 1) / 2;
        }
        return inputBlockSize;
    }
    
    public int getOutputBlockSize() {
        final int outputBlockSize = this.engine.getOutputBlockSize();
        if (this.forEncryption) {
            return outputBlockSize;
        }
        return (outputBlockSize + 1) / 2;
    }
    
    public void setPadBits(final int padBits) {
        if (padBits > 7) {
            throw new IllegalArgumentException("padBits > 7");
        }
        this.padBits = padBits;
    }
    
    public int getPadBits() {
        return this.padBits;
    }
    
    public byte[] processBlock(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return this.encodeBlock(array, n, n2);
        }
        return this.decodeBlock(array, n, n2);
    }
    
    private byte[] encodeBlock(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        final byte[] array2 = new byte[(this.bitSize + 7) / 8];
        final int n3 = this.padBits + 1;
        final int n4 = (this.bitSize + 13) / 16;
        for (int i = 0; i < n4; i += n2) {
            if (i > n4 - n2) {
                System.arraycopy(array, n + n2 - (n4 - i), array2, array2.length - n4, n4 - i);
            }
            else {
                System.arraycopy(array, n, array2, array2.length - (i + n2), n2);
            }
        }
        for (int j = array2.length - 2 * n4; j != array2.length; j += 2) {
            final byte b = array2[array2.length - n4 + j / 2];
            array2[j] = (byte)(ISO9796d1Encoding.shadows[(b & 0xFF) >>> 4] << 4 | ISO9796d1Encoding.shadows[b & 0xF]);
            array2[j + 1] = b;
        }
        final byte[] array3 = array2;
        final int n5 = array2.length - 2 * n2;
        array3[n5] ^= (byte)n3;
        array2[array2.length - 1] = (byte)(array2[array2.length - 1] << 4 | 0x6);
        final int n6 = 8 - (this.bitSize - 1) % 8;
        int n7 = 0;
        if (n6 != 8) {
            final byte[] array4 = array2;
            final int n8 = 0;
            array4[n8] &= (byte)(255 >>> n6);
            final byte[] array5 = array2;
            final int n9 = 0;
            array5[n9] |= (byte)(128 >>> n6);
        }
        else {
            array2[0] = 0;
            final byte[] array6 = array2;
            final int n10 = 1;
            array6[n10] |= (byte)128;
            n7 = 1;
        }
        return this.engine.processBlock(array2, n7, array2.length - n7);
    }
    
    private byte[] decodeBlock(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        final byte[] processBlock = this.engine.processBlock(array, n, n2);
        int n3 = 1;
        final int n4 = (this.bitSize + 13) / 16;
        final BigInteger bigInteger = new BigInteger(1, processBlock);
        BigInteger subtract;
        if (bigInteger.mod(ISO9796d1Encoding.SIXTEEN).equals(ISO9796d1Encoding.SIX)) {
            subtract = bigInteger;
        }
        else {
            if (!this.modulus.subtract(bigInteger).mod(ISO9796d1Encoding.SIXTEEN).equals(ISO9796d1Encoding.SIX)) {
                throw new InvalidCipherTextException("resulting integer iS or (modulus - iS) is not congruent to 6 mod 16");
            }
            subtract = this.modulus.subtract(bigInteger);
        }
        final byte[] convertOutputDecryptOnly = convertOutputDecryptOnly(subtract);
        if ((convertOutputDecryptOnly[convertOutputDecryptOnly.length - 1] & 0xF) != 0x6) {
            throw new InvalidCipherTextException("invalid forcing byte in block");
        }
        convertOutputDecryptOnly[convertOutputDecryptOnly.length - 1] = (byte)((convertOutputDecryptOnly[convertOutputDecryptOnly.length - 1] & 0xFF) >>> 4 | ISO9796d1Encoding.inverse[(convertOutputDecryptOnly[convertOutputDecryptOnly.length - 2] & 0xFF) >> 4] << 4);
        convertOutputDecryptOnly[0] = (byte)(ISO9796d1Encoding.shadows[(convertOutputDecryptOnly[1] & 0xFF) >>> 4] << 4 | ISO9796d1Encoding.shadows[convertOutputDecryptOnly[1] & 0xF]);
        int n5 = 0;
        int n6 = 0;
        for (int i = convertOutputDecryptOnly.length - 1; i >= convertOutputDecryptOnly.length - 2 * n4; i -= 2) {
            final int n7 = ISO9796d1Encoding.shadows[(convertOutputDecryptOnly[i] & 0xFF) >>> 4] << 4 | ISO9796d1Encoding.shadows[convertOutputDecryptOnly[i] & 0xF];
            if (((convertOutputDecryptOnly[i - 1] ^ n7) & 0xFF) != 0x0) {
                if (n5 != 0) {
                    throw new InvalidCipherTextException("invalid tsums in block");
                }
                n5 = 1;
                n3 = ((convertOutputDecryptOnly[i - 1] ^ n7) & 0xFF);
                n6 = i - 1;
            }
        }
        convertOutputDecryptOnly[n6] = 0;
        final byte[] array2 = new byte[(convertOutputDecryptOnly.length - n6) / 2];
        for (int j = 0; j < array2.length; ++j) {
            array2[j] = convertOutputDecryptOnly[2 * j + n6 + 1];
        }
        this.padBits = n3 - 1;
        return array2;
    }
    
    private static byte[] convertOutputDecryptOnly(final BigInteger bigInteger) {
        final byte[] byteArray = bigInteger.toByteArray();
        if (byteArray[0] == 0) {
            final byte[] array = new byte[byteArray.length - 1];
            System.arraycopy(byteArray, 1, array, 0, array.length);
            return array;
        }
        return byteArray;
    }
    
    static {
        SIXTEEN = BigInteger.valueOf(16L);
        SIX = BigInteger.valueOf(6L);
        ISO9796d1Encoding.shadows = new byte[] { 14, 3, 5, 8, 9, 4, 2, 15, 0, 13, 11, 6, 7, 10, 12, 1 };
        ISO9796d1Encoding.inverse = new byte[] { 8, 15, 6, 1, 5, 2, 11, 12, 3, 4, 13, 10, 14, 9, 0, 7 };
    }
}
