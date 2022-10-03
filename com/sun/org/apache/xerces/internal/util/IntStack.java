package com.sun.org.apache.xerces.internal.util;

public final class IntStack
{
    private int fDepth;
    private int[] fData;
    
    public int size() {
        return this.fDepth;
    }
    
    public void push(final int value) {
        this.ensureCapacity(this.fDepth + 1);
        this.fData[this.fDepth++] = value;
    }
    
    public int peek() {
        return this.fData[this.fDepth - 1];
    }
    
    public int elementAt(final int depth) {
        return this.fData[depth];
    }
    
    public int pop() {
        final int[] fData = this.fData;
        final int fDepth = this.fDepth - 1;
        this.fDepth = fDepth;
        return fData[fDepth];
    }
    
    public void clear() {
        this.fDepth = 0;
    }
    
    public void print() {
        System.out.print('(');
        System.out.print(this.fDepth);
        System.out.print(") {");
        for (int i = 0; i < this.fDepth; ++i) {
            if (i == 3) {
                System.out.print(" ...");
                break;
            }
            System.out.print(' ');
            System.out.print(this.fData[i]);
            if (i < this.fDepth - 1) {
                System.out.print(',');
            }
        }
        System.out.print(" }");
        System.out.println();
    }
    
    private void ensureCapacity(final int size) {
        if (this.fData == null) {
            this.fData = new int[32];
        }
        else if (this.fData.length <= size) {
            final int[] newdata = new int[this.fData.length * 2];
            System.arraycopy(this.fData, 0, newdata, 0, this.fData.length);
            this.fData = newdata;
        }
    }
}
