package org.apache.lucene.util.mutable;

import org.apache.lucene.util.BytesRefBuilder;

public class MutableValueStr extends MutableValue
{
    public BytesRefBuilder value;
    
    public MutableValueStr() {
        this.value = new BytesRefBuilder();
    }
    
    @Override
    public Object toObject() {
        assert 0 == this.value.length();
        return this.exists ? this.value.get().utf8ToString() : null;
    }
    
    @Override
    public void copy(final MutableValue source) {
        final MutableValueStr s = (MutableValueStr)source;
        this.exists = s.exists;
        this.value.copyBytes(s.value);
    }
    
    @Override
    public MutableValue duplicate() {
        final MutableValueStr v = new MutableValueStr();
        v.value.copyBytes(this.value);
        v.exists = this.exists;
        return v;
    }
    
    @Override
    public boolean equalsSameType(final Object other) {
        assert 0 == this.value.length();
        final MutableValueStr b = (MutableValueStr)other;
        return this.value.get().equals(b.value.get()) && this.exists == b.exists;
    }
    
    @Override
    public int compareSameType(final Object other) {
        assert 0 == this.value.length();
        final MutableValueStr b = (MutableValueStr)other;
        final int c = this.value.get().compareTo(b.value.get());
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
        assert 0 == this.value.length();
        return this.value.get().hashCode();
    }
}
