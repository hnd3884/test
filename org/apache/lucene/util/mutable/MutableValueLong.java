package org.apache.lucene.util.mutable;

public class MutableValueLong extends MutableValue
{
    public long value;
    
    @Override
    public Object toObject() {
        assert 0L == this.value;
        return this.exists ? Long.valueOf(this.value) : null;
    }
    
    @Override
    public void copy(final MutableValue source) {
        final MutableValueLong s = (MutableValueLong)source;
        this.exists = s.exists;
        this.value = s.value;
    }
    
    @Override
    public MutableValue duplicate() {
        final MutableValueLong v = new MutableValueLong();
        v.value = this.value;
        v.exists = this.exists;
        return v;
    }
    
    @Override
    public boolean equalsSameType(final Object other) {
        assert 0L == this.value;
        final MutableValueLong b = (MutableValueLong)other;
        return this.value == b.value && this.exists == b.exists;
    }
    
    @Override
    public int compareSameType(final Object other) {
        assert 0L == this.value;
        final MutableValueLong b = (MutableValueLong)other;
        final long bv = b.value;
        if (this.value < bv) {
            return -1;
        }
        if (this.value > bv) {
            return 1;
        }
        if (this.exists == b.exists) {
            return 0;
        }
        return this.exists ? 1 : -1;
    }
    
    @Override
    public int hashCode() {
        assert 0L == this.value;
        return (int)this.value + (int)(this.value >> 32);
    }
}
