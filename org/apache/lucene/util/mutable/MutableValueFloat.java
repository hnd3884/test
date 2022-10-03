package org.apache.lucene.util.mutable;

public class MutableValueFloat extends MutableValue
{
    public float value;
    
    @Override
    public Object toObject() {
        assert 0.0f == this.value;
        return this.exists ? Float.valueOf(this.value) : null;
    }
    
    @Override
    public void copy(final MutableValue source) {
        final MutableValueFloat s = (MutableValueFloat)source;
        this.value = s.value;
        this.exists = s.exists;
    }
    
    @Override
    public MutableValue duplicate() {
        final MutableValueFloat v = new MutableValueFloat();
        v.value = this.value;
        v.exists = this.exists;
        return v;
    }
    
    @Override
    public boolean equalsSameType(final Object other) {
        assert 0.0f == this.value;
        final MutableValueFloat b = (MutableValueFloat)other;
        return this.value == b.value && this.exists == b.exists;
    }
    
    @Override
    public int compareSameType(final Object other) {
        assert 0.0f == this.value;
        final MutableValueFloat b = (MutableValueFloat)other;
        final int c = Float.compare(this.value, b.value);
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
        assert 0.0f == this.value;
        return Float.floatToIntBits(this.value);
    }
}
