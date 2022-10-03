package org.eclipse.jdt.internal.compiler.util;

public final class ObjectVector
{
    static int INITIAL_SIZE;
    public int size;
    int maxSize;
    Object[] elements;
    
    static {
        ObjectVector.INITIAL_SIZE = 10;
    }
    
    public ObjectVector() {
        this(ObjectVector.INITIAL_SIZE);
    }
    
    public ObjectVector(final int initialSize) {
        this.maxSize = ((initialSize > 0) ? initialSize : ObjectVector.INITIAL_SIZE);
        this.size = 0;
        this.elements = new Object[this.maxSize];
    }
    
    public void add(final Object newElement) {
        if (this.size == this.maxSize) {
            final Object[] elements = this.elements;
            final int n = 0;
            final int maxSize = this.maxSize * 2;
            this.maxSize = maxSize;
            System.arraycopy(elements, n, this.elements = new Object[maxSize], 0, this.size);
        }
        this.elements[this.size++] = newElement;
    }
    
    public void addAll(final Object[] newElements) {
        if (this.size + newElements.length >= this.maxSize) {
            this.maxSize = this.size + newElements.length;
            System.arraycopy(this.elements, 0, this.elements = new Object[this.maxSize], 0, this.size);
        }
        System.arraycopy(newElements, 0, this.elements, this.size, newElements.length);
        this.size += newElements.length;
    }
    
    public void addAll(final ObjectVector newVector) {
        if (this.size + newVector.size >= this.maxSize) {
            this.maxSize = this.size + newVector.size;
            System.arraycopy(this.elements, 0, this.elements = new Object[this.maxSize], 0, this.size);
        }
        System.arraycopy(newVector.elements, 0, this.elements, this.size, newVector.size);
        this.size += newVector.size;
    }
    
    public boolean containsIdentical(final Object element) {
        int i = this.size;
        while (--i >= 0) {
            if (element == this.elements[i]) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(final Object element) {
        int i = this.size;
        while (--i >= 0) {
            if (element.equals(this.elements[i])) {
                return true;
            }
        }
        return false;
    }
    
    public void copyInto(final Object[] targetArray) {
        this.copyInto(targetArray, 0);
    }
    
    public void copyInto(final Object[] targetArray, final int index) {
        System.arraycopy(this.elements, 0, targetArray, index, this.size);
    }
    
    public Object elementAt(final int index) {
        return this.elements[index];
    }
    
    public Object find(final Object element) {
        int i = this.size;
        while (--i >= 0) {
            if (element.equals(this.elements[i])) {
                return this.elements[i];
            }
        }
        return null;
    }
    
    public Object remove(final Object element) {
        int i = this.size;
        while (--i >= 0) {
            if (element.equals(this.elements[i])) {
                System.arraycopy(this.elements, i + 1, this.elements, i, --this.size - i);
                this.elements[this.size] = null;
                return element;
            }
        }
        return null;
    }
    
    public void removeAll() {
        int i = this.size;
        while (--i >= 0) {
            this.elements[i] = null;
        }
        this.size = 0;
    }
    
    public int size() {
        return this.size;
    }
    
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < this.size; ++i) {
            s = String.valueOf(s) + this.elements[i].toString() + "\n";
        }
        return s;
    }
}
