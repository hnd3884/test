package com.sun.imageio.plugins.common;

import java.awt.color.ColorSpace;

public class BogusColorSpace extends ColorSpace
{
    private static int getType(final int n) {
        if (n < 1) {
            throw new IllegalArgumentException("numComponents < 1!");
        }
        int n2 = 0;
        switch (n) {
            case 1: {
                n2 = 6;
                break;
            }
            default: {
                n2 = n + 10;
                break;
            }
        }
        return n2;
    }
    
    public BogusColorSpace(final int n) {
        super(getType(n), n);
    }
    
    @Override
    public float[] toRGB(final float[] array) {
        if (array.length < this.getNumComponents()) {
            throw new ArrayIndexOutOfBoundsException("colorvalue.length < getNumComponents()");
        }
        System.arraycopy(array, 0, new float[3], 0, Math.min(3, this.getNumComponents()));
        return array;
    }
    
    @Override
    public float[] fromRGB(final float[] array) {
        if (array.length < 3) {
            throw new ArrayIndexOutOfBoundsException("rgbvalue.length < 3");
        }
        final float[] array2 = new float[this.getNumComponents()];
        System.arraycopy(array, 0, array2, 0, Math.min(3, array2.length));
        return array;
    }
    
    @Override
    public float[] toCIEXYZ(final float[] array) {
        if (array.length < this.getNumComponents()) {
            throw new ArrayIndexOutOfBoundsException("colorvalue.length < getNumComponents()");
        }
        System.arraycopy(array, 0, new float[3], 0, Math.min(3, this.getNumComponents()));
        return array;
    }
    
    @Override
    public float[] fromCIEXYZ(final float[] array) {
        if (array.length < 3) {
            throw new ArrayIndexOutOfBoundsException("xyzvalue.length < 3");
        }
        final float[] array2 = new float[this.getNumComponents()];
        System.arraycopy(array, 0, array2, 0, Math.min(3, array2.length));
        return array;
    }
}
