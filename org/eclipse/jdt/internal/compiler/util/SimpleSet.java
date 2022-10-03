package org.eclipse.jdt.internal.compiler.util;

public final class SimpleSet implements Cloneable
{
    public Object[] values;
    public int elementSize;
    public int threshold;
    
    public SimpleSet() {
        this(13);
    }
    
    public SimpleSet(int size) {
        if (size < 3) {
            size = 3;
        }
        this.elementSize = 0;
        this.threshold = size + 1;
        this.values = new Object[2 * size + 1];
    }
    
    public Object add(final Object object) {
        final int length = this.values.length;
        int index = (object.hashCode() & Integer.MAX_VALUE) % length;
        Object current;
        while ((current = this.values[index]) != null) {
            if (current.equals(object)) {
                return this.values[index] = object;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        this.values[index] = object;
        if (++this.elementSize > this.threshold) {
            this.rehash();
        }
        return object;
    }
    
    public Object addIfNotIncluded(final Object object) {
        final int length = this.values.length;
        int index = (object.hashCode() & Integer.MAX_VALUE) % length;
        Object current;
        while ((current = this.values[index]) != null) {
            if (current.equals(object)) {
                return null;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        this.values[index] = object;
        if (++this.elementSize > this.threshold) {
            this.rehash();
        }
        return object;
    }
    
    public void asArray(final Object[] copy) {
        if (this.elementSize != copy.length) {
            throw new IllegalArgumentException();
        }
        for (int index = this.elementSize, i = 0, l = this.values.length; i < l && index > 0; ++i) {
            if (this.values[i] != null) {
                copy[--index] = this.values[i];
            }
        }
    }
    
    public void clear() {
        int i = this.values.length;
        while (--i >= 0) {
            this.values[i] = null;
        }
        this.elementSize = 0;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final SimpleSet result = (SimpleSet)super.clone();
        result.elementSize = this.elementSize;
        result.threshold = this.threshold;
        final int length = this.values.length;
        result.values = new Object[length];
        System.arraycopy(this.values, 0, result.values, 0, length);
        return result;
    }
    
    public boolean includes(final Object object) {
        final int length = this.values.length;
        int index = (object.hashCode() & Integer.MAX_VALUE) % length;
        Object current;
        while ((current = this.values[index]) != null) {
            if (current.equals(object)) {
                return true;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return false;
    }
    
    public Object remove(final Object object) {
        final int length = this.values.length;
        int index = (object.hashCode() & Integer.MAX_VALUE) % length;
        Object current;
        while ((current = this.values[index]) != null) {
            if (current.equals(object)) {
                --this.elementSize;
                final Object oldValue = this.values[index];
                this.values[index] = null;
                if (this.values[(index + 1 == length) ? 0 : (index + 1)] != null) {
                    this.rehash();
                }
                return oldValue;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return null;
    }
    
    private void rehash() {
        final SimpleSet newSet = new SimpleSet(this.elementSize * 2);
        int i = this.values.length;
        while (--i >= 0) {
            final Object current;
            if ((current = this.values[i]) != null) {
                newSet.add(current);
            }
        }
        this.values = newSet.values;
        this.elementSize = newSet.elementSize;
        this.threshold = newSet.threshold;
    }
    
    @Override
    public String toString() {
        String s = "";
        for (int i = 0, l = this.values.length; i < l; ++i) {
            final Object object;
            if ((object = this.values[i]) != null) {
                s = String.valueOf(s) + object.toString() + "\n";
            }
        }
        return s;
    }
}
