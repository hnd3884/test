package org.apache.lucene.util.mutable;

public class MutableValueDouble extends MutableValue
{
    public double value;
    
    public MutableValueDouble() {
        this.value = 0.0;
    }
    
    @Override
    public Object toObject() {
        assert 0.0 == this.value;
        return this.exists ? Double.valueOf(this.value) : null;
    }
    
    @Override
    public void copy(final MutableValue source) {
        final MutableValueDouble s = (MutableValueDouble)source;
        this.value = s.value;
        this.exists = s.exists;
    }
    
    @Override
    public MutableValue duplicate() {
        final MutableValueDouble v = new MutableValueDouble();
        v.value = this.value;
        v.exists = this.exists;
        return v;
    }
    
    @Override
    public boolean equalsSameType(final Object other) {
        assert 0.0 == this.value;
        final MutableValueDouble b = (MutableValueDouble)other;
        return this.value == b.value && this.exists == b.exists;
    }
    
    @Override
    public int compareSameType(final Object other) {
        assert 0.0 == this.value;
        final MutableValueDouble b = (MutableValueDouble)other;
        final int c = Double.compare(this.value, b.value);
        if (c != 0) {
            return c;
        }
        if (this.exists == b.exists) {
            return 0;
        }
        return this.exists ? 1 : -1;
    }
    
    @Override
    public int hashCode() {
        assert 0.0 == this.value;
        final long x = Double.doubleToLongBits(this.value);
        return (int)x + (int)(x >>> 32);
    }
}
