package org.apache.lucene.util.mutable;

public abstract class MutableValue implements Comparable<MutableValue>
{
    public boolean exists;
    
    public MutableValue() {
        this.exists = true;
    }
    
    public abstract void copy(final MutableValue p0);
    
    public abstract MutableValue duplicate();
    
    public abstract boolean equalsSameType(final Object p0);
    
    public abstract int compareSameType(final Object p0);
    
    public abstract Object toObject();
    
    public boolean exists() {
        return this.exists;
    }
    
    @Override
    public int compareTo(final MutableValue other) {
        final Class<? extends MutableValue> c1 = this.getClass();
        final Class<? extends MutableValue> c2 = other.getClass();
        if (c1 != c2) {
            int c3 = c1.hashCode() - c2.hashCode();
            if (c3 == 0) {
                c3 = c1.getCanonicalName().compareTo(c2.getCanonicalName());
            }
            return c3;
        }
        return this.compareSameType(other);
    }
    
    @Override
    public boolean equals(final Object other) {
        return this.getClass() == other.getClass() && this.equalsSameType(other);
    }
    
    @Override
    public abstract int hashCode();
    
    @Override
    public String toString() {
        return this.exists() ? this.toObject().toString() : "(null)";
    }
}
