package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.CipherKeyGenerator;

public class Poly1305KeyGenerator extends CipherKeyGenerator
{
    private static final byte R_MASK_LOW_2 = -4;
    private static final byte R_MASK_HIGH_4 = 15;
    
    @Override
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        super.init(new KeyGenerationParameters(keyGenerationParameters.getRandom(), 256));
    }
    
    @Override
    public byte[] generateKey() {
        final byte[] generateKey = super.generateKey();
        clamp(generateKey);
        return generateKey;
    }
    
    public static void clamp(final byte[] array) {
        if (array.length != 32) {
            throw new IllegalArgumentException("Poly1305 key must be 256 bits.");
        }
        final int n = 3;
        array[n] &= 0xF;
        final int n2 = 7;
        array[n2] &= 0xF;
        final int n3 = 11;
        array[n3] &= 0xF;
        final int n4 = 15;
        array[n4] &= 0xF;
        final int n5 = 4;
        array[n5] &= 0xFFFFFFFC;
        final int n6 = 8;
        array[n6] &= 0xFFFFFFFC;
        final int n7 = 12;
        array[n7] &= 0xFFFFFFFC;
    }
    
    public static void checkKey(final byte[] array) {
        if (array.length != 32) {
            throw new IllegalArgumentException("Poly1305 key must be 256 bits.");
        }
        checkMask(array[3], (byte)15);
        checkMask(array[7], (byte)15);
        checkMask(array[11], (byte)15);
        checkMask(array[15], (byte)15);
        checkMask(array[4], (byte)(-4));
        checkMask(array[8], (byte)(-4));
        checkMask(array[12], (byte)(-4));
    }
    
    private static void checkMask(final byte b, final byte b2) {
        if ((b & ~b2) != 0x0) {
            throw new IllegalArgumentException("Invalid format for r portion of Poly1305 key.");
        }
    }
}
