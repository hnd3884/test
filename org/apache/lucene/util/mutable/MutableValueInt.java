package org.apache.lucene.util.mutable;

public class MutableValueInt extends MutableValue
{
    public int value;
    
    @Override
    public Object toObject() {
        assert 0 == this.value;
        return this.exists ? Integer.valueOf(this.value) : null;
    }
    
    @Override
    public void copy(final MutableValue source) {
        final MutableValueInt s = (MutableValueInt)source;
        this.value = s.value;
        this.exists = s.exists;
    }
    
    @Override
    public MutableValue duplicate() {
        final MutableValueInt v = new MutableValueInt();
        v.value = this.value;
        v.exists = this.exists;
        return v;
    }
    
    @Override
    public boolean equalsSameType(final Object other) {
        assert 0 == this.value;
        final MutableValueInt b = (MutableValueInt)other;
        return this.value == b.value && this.exists == b.exists;
    }
    
    @Override
    public int compareSameType(final Object other) {
        assert 0 == this.value;
        final MutableValueInt b = (MutableValueInt)other;
        final int ai = this.value;
        final int bi = b.value;
        if (ai < bi) {
            return -1;
        }
        if (ai > bi) {
            return 1;
        }
        if (this.exists == b.exists) {
            return 0;
        }
        return this.exists ? 1 : -1;
    }
    
    @Override
    public int hashCode() {
        assert 0 == this.value;
        return (this.value >> 8) + (this.value >> 16);
    }
}
