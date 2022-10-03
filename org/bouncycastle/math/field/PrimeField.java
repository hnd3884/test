package org.bouncycastle.math.field;

import java.math.BigInteger;

class PrimeField implements FiniteField
{
    protected final BigInteger characteristic;
    
    PrimeField(final BigInteger characteristic) {
        this.characteristic = characteristic;
    }
    
    public BigInteger getCharacteristic() {
        return this.characteristic;
    }
    
    public int getDimension() {
        return 1;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof PrimeField && this.characteristic.equals(((PrimeField)o).characteristic));
    }
    
    @Override
    public int hashCode() {
        return this.characteristic.hashCode();
    }
}
