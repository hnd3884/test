package org.apache.lucene.util.mutable;

public class MutableValueBool extends MutableValue
{
    public boolean value;
    
    @Override
    public Object toObject() {
        assert !this.value;
        return this.exists ? Boolean.valueOf(this.value) : null;
    }
    
    @Override
    public void copy(final MutableValue source) {
        final MutableValueBool s = (MutableValueBool)source;
        this.value = s.value;
        this.exists = s.exists;
    }
    
    @Override
    public MutableValue duplicate() {
        final MutableValueBool v = new MutableValueBool();
        v.value = this.value;
        v.exists = this.exists;
        return v;
    }
    
    @Override
    public boolean equalsSameType(final Object other) {
        assert !this.value;
        final MutableValueBool b = (MutableValueBool)other;
        return this.value == b.value && this.exists == b.exists;
    }
    
    @Override
    public int compareSameType(final Object other) {
        assert !this.value;
        final MutableValueBool b = (MutableValueBool)other;
        if (this.value != b.value) {
            return this.value ? 1 : -1;
        }
        if (this.exists == b.exists) {
            return 0;
        }
        return this.exists ? 1 : -1;
    }
    
    @Override
    public int hashCode() {
        assert !this.value;
        return this.value ? 2 : (this.exists ? 1 : 0);
    }
}
