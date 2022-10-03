package org.bouncycastle.crypto.params;

import org.bouncycastle.util.Arrays;

public class DHValidationParameters
{
    private byte[] seed;
    private int counter;
    
    public DHValidationParameters(final byte[] seed, final int counter) {
        this.seed = seed;
        this.counter = counter;
    }
    
    public int getCounter() {
        return this.counter;
    }
    
    public byte[] getSeed() {
        return this.seed;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DHValidationParameters)) {
            return false;
        }
        final DHValidationParameters dhValidationParameters = (DHValidationParameters)o;
        return dhValidationParameters.counter == this.counter && Arrays.areEqual(this.seed, dhValidationParameters.seed);
    }
    
    @Override
    public int hashCode() {
        return this.counter ^ Arrays.hashCode(this.seed);
    }
}
