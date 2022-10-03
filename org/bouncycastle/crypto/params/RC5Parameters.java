package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class RC5Parameters implements CipherParameters
{
    private byte[] key;
    private int rounds;
    
    public RC5Parameters(final byte[] array, final int rounds) {
        if (array.length > 255) {
            throw new IllegalArgumentException("RC5 key length can be no greater than 255");
        }
        this.key = new byte[array.length];
        this.rounds = rounds;
        System.arraycopy(array, 0, this.key, 0, array.length);
    }
    
    public byte[] getKey() {
        return this.key;
    }
    
    public int getRounds() {
        return this.rounds;
    }
}
