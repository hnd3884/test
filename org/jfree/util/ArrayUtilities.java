package org.jfree.util;

import java.util.Arrays;

public class ArrayUtilities
{
    private ArrayUtilities() {
    }
    
    public static float[][] clone(final float[][] array) {
        if (array == null) {
            return null;
        }
        final float[][] result = new float[array.length][];
        System.arraycopy(array, 0, result, 0, array.length);
        for (int i = 0; i < array.length; ++i) {
            final float[] child = array[i];
            final float[] copychild = new float[child.length];
            System.arraycopy(child, 0, copychild, 0, child.length);
            result[i] = copychild;
        }
        return result;
    }
    
    public static boolean equal(final float[][] array1, final float[][] array2) {
        if (array1 == null) {
            return array2 == null;
        }
        if (array2 == null) {
            return false;
        }
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; ++i) {
            if (!Arrays.equals(array1[i], array2[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean equalReferencesInArrays(final Object[] array1, final Object[] array2) {
        if (array1 == null) {
            return array2 == null;
        }
        if (array2 == null) {
            return false;
        }
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; ++i) {
            if (array1[i] == null && array2[i] != null) {
                return false;
            }
            if (array2[i] == null && array1[i] != null) {
                return false;
            }
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean hasDuplicateItems(final Object[] array) {
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < i; ++j) {
                final Object o1 = array[i];
                final Object o2 = array[j];
                if (o1 != null && o2 != null && o1.equals(o2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
