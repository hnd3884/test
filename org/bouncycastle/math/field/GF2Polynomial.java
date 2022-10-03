package org.bouncycastle.math.field;

import org.bouncycastle.util.Arrays;

class GF2Polynomial implements Polynomial
{
    protected final int[] exponents;
    
    GF2Polynomial(final int[] array) {
        this.exponents = Arrays.clone(array);
    }
    
    public int getDegree() {
        return this.exponents[this.exponents.length - 1];
    }
    
    public int[] getExponentsPresent() {
        return Arrays.clone(this.exponents);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof GF2Polynomial && Arrays.areEqual(this.exponents, ((GF2Polynomial)o).exponents));
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.exponents);
    }
}
