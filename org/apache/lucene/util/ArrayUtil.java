package org.apache.lucene.util;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.Comparator;

public final class ArrayUtil
{
    public static final int MAX_ARRAY_LENGTH;
    private static final Comparator<?> NATURAL_COMPARATOR;
    
    private ArrayUtil() {
    }
    
    public static int parseInt(final char[] chars) throws NumberFormatException {
        return parseInt(chars, 0, chars.length, 10);
    }
    
    public static int parseInt(final char[] chars, final int offset, final int len) throws NumberFormatException {
        return parseInt(chars, offset, len, 10);
    }
    
    public static int parseInt(final char[] chars, int offset, int len, final int radix) throws NumberFormatException {
        if (chars == null || radix < 2 || radix > 36) {
            throw new NumberFormatException();
        }
        int i = 0;
        if (len == 0) {
            throw new NumberFormatException("chars length is 0");
        }
        final boolean negative = chars[offset + i] == '-';
        if (negative && ++i == len) {
            throw new NumberFormatException("can't convert to an int");
        }
        if (negative) {
            ++offset;
            --len;
        }
        return parse(chars, offset, len, radix, negative);
    }
    
    private static int parse(final char[] chars, final int offset, final int len, final int radix, final boolean negative) throws NumberFormatException {
        final int max = Integer.MIN_VALUE / radix;
        int result = 0;
        for (int i = 0; i < len; ++i) {
            final int digit = Character.digit(chars[i + offset], radix);
            if (digit == -1) {
                throw new NumberFormatException("Unable to parse");
            }
            if (max > result) {
                throw new NumberFormatException("Unable to parse");
            }
            final int next = result * radix - digit;
            if (next > result) {
                throw new NumberFormatException("Unable to parse");
            }
            result = next;
        }
        if (!negative) {
            result = -result;
            if (result < 0) {
                throw new NumberFormatException("Unable to parse");
            }
        }
        return result;
    }
    
    public static int oversize(final int minTargetSize, final int bytesPerElement) {
        if (minTargetSize < 0) {
            throw new IllegalArgumentException("invalid array size " + minTargetSize);
        }
        if (minTargetSize == 0) {
            return 0;
        }
        if (minTargetSize > ArrayUtil.MAX_ARRAY_LENGTH) {
            throw new IllegalArgumentException("requested array size " + minTargetSize + " exceeds maximum array in java (" + ArrayUtil.MAX_ARRAY_LENGTH + ")");
        }
        int extra = minTargetSize >> 3;
        if (extra < 3) {
            extra = 3;
        }
        final int newSize = minTargetSize + extra;
        if (newSize + 7 < 0 || newSize + 7 > ArrayUtil.MAX_ARRAY_LENGTH) {
            return ArrayUtil.MAX_ARRAY_LENGTH;
        }
        if (Constants.JRE_IS_64BIT) {
            switch (bytesPerElement) {
                case 4: {
                    return newSize + 1 & 0x7FFFFFFE;
                }
                case 2: {
                    return newSize + 3 & 0x7FFFFFFC;
                }
                case 1: {
                    return newSize + 7 & 0x7FFFFFF8;
                }
                default: {
                    return newSize;
                }
            }
        }
        else {
            switch (bytesPerElement) {
                case 2: {
                    return newSize + 1 & 0x7FFFFFFE;
                }
                case 1: {
                    return newSize + 3 & 0x7FFFFFFC;
                }
                default: {
                    return newSize;
                }
            }
        }
    }
    
    public static int getShrinkSize(final int currentSize, final int targetSize, final int bytesPerElement) {
        final int newSize = oversize(targetSize, bytesPerElement);
        if (newSize < currentSize / 2) {
            return newSize;
        }
        return currentSize;
    }
    
    public static <T> T[] grow(final T[] array, final int minSize) {
        assert minSize >= 0 : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            return Arrays.copyOf(array, oversize(minSize, RamUsageEstimator.NUM_BYTES_OBJECT_REF));
        }
        return array;
    }
    
    public static short[] grow(final short[] array, final int minSize) {
        assert minSize >= 0 : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            final short[] newArray = new short[oversize(minSize, 2)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }
    
    public static short[] grow(final short[] array) {
        return grow(array, 1 + array.length);
    }
    
    public static float[] grow(final float[] array, final int minSize) {
        assert minSize >= 0 : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            final float[] newArray = new float[oversize(minSize, 4)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }
    
    public static float[] grow(final float[] array) {
        return grow(array, 1 + array.length);
    }
    
    public static double[] grow(final double[] array, final int minSize) {
        assert minSize >= 0 : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            final double[] newArray = new double[oversize(minSize, 8)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }
    
    public static double[] grow(final double[] array) {
        return grow(array, 1 + array.length);
    }
    
    public static short[] shrink(final short[] array, final int targetSize) {
        assert targetSize >= 0 : "size must be positive (got " + targetSize + "): likely integer overflow?";
        final int newSize = getShrinkSize(array.length, targetSize, 2);
        if (newSize != array.length) {
            final short[] newArray = new short[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }
    
    public static int[] grow(final int[] array, final int minSize) {
        assert minSize >= 0 : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            final int[] newArray = new int[oversize(minSize, 4)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }
    
    public static int[] grow(final int[] array) {
        return grow(array, 1 + array.length);
    }
    
    public static int[] shrink(final int[] array, final int targetSize) {
        assert targetSize >= 0 : "size must be positive (got " + targetSize + "): likely integer overflow?";
        final int newSize = getShrinkSize(array.length, targetSize, 4);
        if (newSize != array.length) {
            final int[] newArray = new int[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }
    
    public static long[] grow(final long[] array, final int minSize) {
        assert minSize >= 0 : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            final long[] newArray = new long[oversize(minSize, 8)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }
    
    public static long[] grow(final long[] array) {
        return grow(array, 1 + array.length);
    }
    
    public static long[] shrink(final long[] array, final int targetSize) {
        assert targetSize >= 0 : "size must be positive (got " + targetSize + "): likely integer overflow?";
        final int newSize = getShrinkSize(array.length, targetSize, 8);
        if (newSize != array.length) {
            final long[] newArray = new long[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }
    
    public static byte[] grow(final byte[] array, final int minSize) {
        assert minSize >= 0 : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            final byte[] newArray = new byte[oversize(minSize, 1)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }
    
    public static byte[] grow(final byte[] array) {
        return grow(array, 1 + array.length);
    }
    
    public static byte[] shrink(final byte[] array, final int targetSize) {
        assert targetSize >= 0 : "size must be positive (got " + targetSize + "): likely integer overflow?";
        final int newSize = getShrinkSize(array.length, targetSize, 1);
        if (newSize != array.length) {
            final byte[] newArray = new byte[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }
    
    public static boolean[] grow(final boolean[] array, final int minSize) {
        assert minSize >= 0 : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            final boolean[] newArray = new boolean[oversize(minSize, 1)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }
    
    public static boolean[] grow(final boolean[] array) {
        return grow(array, 1 + array.length);
    }
    
    public static boolean[] shrink(final boolean[] array, final int targetSize) {
        assert targetSize >= 0 : "size must be positive (got " + targetSize + "): likely integer overflow?";
        final int newSize = getShrinkSize(array.length, targetSize, 1);
        if (newSize != array.length) {
            final boolean[] newArray = new boolean[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }
    
    public static char[] grow(final char[] array, final int minSize) {
        assert minSize >= 0 : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            final char[] newArray = new char[oversize(minSize, 2)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }
    
    public static char[] grow(final char[] array) {
        return grow(array, 1 + array.length);
    }
    
    public static char[] shrink(final char[] array, final int targetSize) {
        assert targetSize >= 0 : "size must be positive (got " + targetSize + "): likely integer overflow?";
        final int newSize = getShrinkSize(array.length, targetSize, 2);
        if (newSize != array.length) {
            final char[] newArray = new char[newSize];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }
    
    public static int[][] grow(final int[][] array, final int minSize) {
        assert minSize >= 0 : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            final int[][] newArray = new int[oversize(minSize, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }
    
    public static int[][] grow(final int[][] array) {
        return grow(array, 1 + array.length);
    }
    
    public static int[][] shrink(final int[][] array, final int targetSize) {
        assert targetSize >= 0 : "size must be positive (got " + targetSize + "): likely integer overflow?";
        final int newSize = getShrinkSize(array.length, targetSize, RamUsageEstimator.NUM_BYTES_OBJECT_REF);
        if (newSize != array.length) {
            final int[][] newArray = new int[newSize][];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }
    
    public static float[][] grow(final float[][] array, final int minSize) {
        assert minSize >= 0 : "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            final float[][] newArray = new float[oversize(minSize, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        }
        return array;
    }
    
    public static float[][] grow(final float[][] array) {
        return grow(array, 1 + array.length);
    }
    
    public static float[][] shrink(final float[][] array, final int targetSize) {
        assert targetSize >= 0 : "size must be positive (got " + targetSize + "): likely integer overflow?";
        final int newSize = getShrinkSize(array.length, targetSize, RamUsageEstimator.NUM_BYTES_OBJECT_REF);
        if (newSize != array.length) {
            final float[][] newArray = new float[newSize][];
            System.arraycopy(array, 0, newArray, 0, newSize);
            return newArray;
        }
        return array;
    }
    
    public static int hashCode(final char[] array, final int start, final int end) {
        int code = 0;
        for (int i = end - 1; i >= start; --i) {
            code = code * 31 + array[i];
        }
        return code;
    }
    
    public static int hashCode(final byte[] array, final int start, final int end) {
        int code = 0;
        for (int i = end - 1; i >= start; --i) {
            code = code * 31 + array[i];
        }
        return code;
    }
    
    public static boolean equals(final char[] left, final int offsetLeft, final char[] right, final int offsetRight, final int length) {
        if (offsetLeft + length <= left.length && offsetRight + length <= right.length) {
            for (int i = 0; i < length; ++i) {
                if (left[offsetLeft + i] != right[offsetRight + i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static boolean equals(final byte[] left, final int offsetLeft, final byte[] right, final int offsetRight, final int length) {
        if (offsetLeft + length <= left.length && offsetRight + length <= right.length) {
            for (int i = 0; i < length; ++i) {
                if (left[offsetLeft + i] != right[offsetRight + i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static boolean equals(final int[] left, final int offsetLeft, final int[] right, final int offsetRight, final int length) {
        if (offsetLeft + length <= left.length && offsetRight + length <= right.length) {
            for (int i = 0; i < length; ++i) {
                if (left[offsetLeft + i] != right[offsetRight + i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public static int[] toIntArray(final Collection<Integer> ints) {
        final int[] result = new int[ints.size()];
        int upto = 0;
        for (final int v : ints) {
            result[upto++] = v;
        }
        assert upto == result.length;
        return result;
    }
    
    public static <T extends Comparable<? super T>> Comparator<T> naturalComparator() {
        return (Comparator<T>)ArrayUtil.NATURAL_COMPARATOR;
    }
    
    public static <T> void swap(final T[] arr, final int i, final int j) {
        final T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
    
    public static <T> void introSort(final T[] a, final int fromIndex, final int toIndex, final Comparator<? super T> comp) {
        if (toIndex - fromIndex <= 1) {
            return;
        }
        new ArrayIntroSorter<Object>((Object[])a, (Comparator<?>)comp).sort(fromIndex, toIndex);
    }
    
    public static <T> void introSort(final T[] a, final Comparator<? super T> comp) {
        introSort(a, 0, a.length, comp);
    }
    
    public static <T extends Comparable<? super T>> void introSort(final T[] a, final int fromIndex, final int toIndex) {
        if (toIndex - fromIndex <= 1) {
            return;
        }
        introSort(a, fromIndex, toIndex, naturalComparator());
    }
    
    public static <T extends Comparable<? super T>> void introSort(final T[] a) {
        introSort(a, 0, a.length);
    }
    
    public static <T> void timSort(final T[] a, final int fromIndex, final int toIndex, final Comparator<? super T> comp) {
        if (toIndex - fromIndex <= 1) {
            return;
        }
        new ArrayTimSorter<Object>((Object[])a, (Comparator<?>)comp, a.length / 64).sort(fromIndex, toIndex);
    }
    
    public static <T> void timSort(final T[] a, final Comparator<? super T> comp) {
        timSort(a, 0, a.length, comp);
    }
    
    public static <T extends Comparable<? super T>> void timSort(final T[] a, final int fromIndex, final int toIndex) {
        if (toIndex - fromIndex <= 1) {
            return;
        }
        timSort(a, fromIndex, toIndex, naturalComparator());
    }
    
    public static <T extends Comparable<? super T>> void timSort(final T[] a) {
        timSort(a, 0, a.length);
    }
    
    static {
        MAX_ARRAY_LENGTH = Integer.MAX_VALUE - RamUsageEstimator.NUM_BYTES_ARRAY_HEADER;
        NATURAL_COMPARATOR = new NaturalComparator<Object>();
    }
    
    private static class NaturalComparator<T extends Comparable<? super T>> implements Comparator<T>
    {
        NaturalComparator() {
        }
        
        @Override
        public int compare(final T o1, final T o2) {
            return o1.compareTo(o2);
        }
    }
}
