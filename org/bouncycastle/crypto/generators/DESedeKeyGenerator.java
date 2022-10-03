package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.params.DESParameters;
import org.bouncycastle.crypto.params.DESedeParameters;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class DESedeKeyGenerator extends DESKeyGenerator
{
    private static final int MAX_IT = 20;
    
    @Override
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        this.random = keyGenerationParameters.getRandom();
        this.strength = (keyGenerationParameters.getStrength() + 7) / 8;
        if (this.strength == 0 || this.strength == 21) {
            this.strength = 24;
        }
        else if (this.strength == 14) {
            this.strength = 16;
        }
        else if (this.strength != 24 && this.strength != 16) {
            throw new IllegalArgumentException("DESede key must be 192 or 128 bits long.");
        }
    }
    
    @Override
    public byte[] generateKey() {
        final byte[] oddParity = new byte[this.strength];
        int n = 0;
        do {
            this.random.nextBytes(oddParity);
            DESParameters.setOddParity(oddParity);
        } while (++n < 20 && (DESedeParameters.isWeakKey(oddParity, 0, oddParity.length) || !DESedeParameters.isRealEDEKey(oddParity, 0)));
        if (DESedeParameters.isWeakKey(oddParity, 0, oddParity.length) || !DESedeParameters.isRealEDEKey(oddParity, 0)) {
            throw new IllegalStateException("Unable to generate DES-EDE key");
        }
        return oddParity;
    }
}
