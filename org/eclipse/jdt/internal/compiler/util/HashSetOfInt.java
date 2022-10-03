package org.eclipse.jdt.internal.compiler.util;

public final class HashSetOfInt implements Cloneable
{
    public int[] set;
    public int elementSize;
    int threshold;
    
    public HashSetOfInt() {
        this(13);
    }
    
    public HashSetOfInt(final int size) {
        this.elementSize = 0;
        this.threshold = size;
        int extraRoom = (int)(size * 1.75f);
        if (this.threshold == extraRoom) {
            ++extraRoom;
        }
        this.set = new int[extraRoom];
    }
    
    public Object clone() throws CloneNotSupportedException {
        final HashSetOfInt result = (HashSetOfInt)super.clone();
        result.elementSize = this.elementSize;
        result.threshold = this.threshold;
        final int length = this.set.length;
        result.set = new int[length];
        System.arraycopy(this.set, 0, result.set, 0, length);
        return result;
    }
    
    public boolean contains(final int element) {
        final int length = this.set.length;
        int index = element % length;
        int currentElement;
        while ((currentElement = this.set[index]) != 0) {
            if (currentElement == element) {
                return true;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return false;
    }
    
    public int add(final int element) {
        final int length = this.set.length;
        int index = element % length;
        int currentElement;
        while ((currentElement = this.set[index]) != 0) {
            if (currentElement == element) {
                return this.set[index] = element;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        this.set[index] = element;
        if (++this.elementSize > this.threshold) {
            this.rehash();
        }
        return element;
    }
    
    public int remove(final int element) {
        final int length = this.set.length;
        int index = element % length;
        int currentElement;
        while ((currentElement = this.set[index]) != 0) {
            if (currentElement == element) {
                final int existing = this.set[index];
                --this.elementSize;
                this.set[index] = 0;
                this.rehash();
                return existing;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return 0;
    }
    
    private void rehash() {
        final HashSetOfInt newHashSet = new HashSetOfInt(this.elementSize * 2);
        int i = this.set.length;
        while (--i >= 0) {
            final int currentElement;
            if ((currentElement = this.set[i]) != 0) {
                newHashSet.add(currentElement);
            }
        }
        this.set = newHashSet.set;
        this.threshold = newHashSet.threshold;
    }
    
    public int size() {
        return this.elementSize;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0, length = this.set.length; i < length; ++i) {
            final int element;
            if ((element = this.set[i]) != 0) {
                buffer.append(element);
                if (i != length - 1) {
                    buffer.append('\n');
                }
            }
        }
        return buffer.toString();
    }
}
