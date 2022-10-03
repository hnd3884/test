package sun.font;

import java.text.Bidi;

public final class BidiUtils
{
    static final char NUMLEVELS = '>';
    
    public static void getLevels(final Bidi bidi, final byte[] array, final int n) {
        final int n2 = n + bidi.getLength();
        if (n < 0 || n2 > array.length) {
            throw new IndexOutOfBoundsException("levels.length = " + array.length + " start: " + n + " limit: " + n2);
        }
        final int runCount = bidi.getRunCount();
        int i = n;
        for (int j = 0; j < runCount; ++j) {
            final int n3 = n + bidi.getRunLimit(j);
            for (byte b = (byte)bidi.getRunLevel(j); i < n3; array[i++] = b) {}
        }
    }
    
    public static byte[] getLevels(final Bidi bidi) {
        final byte[] array = new byte[bidi.getLength()];
        getLevels(bidi, array, 0);
        return array;
    }
    
    public static int[] createVisualToLogicalMap(final byte[] array) {
        final int length = array.length;
        final int[] array2 = new int[length];
        byte b = 63;
        byte b2 = 0;
        for (int i = 0; i < length; ++i) {
            array2[i] = i;
            final byte b3 = array[i];
            if (b3 > b2) {
                b2 = b3;
            }
            if ((b3 & 0x1) != 0x0 && b3 < b) {
                b = b3;
            }
        }
        while (b2 >= b) {
            int n = 0;
            while (true) {
                if (n < length && array[n] < b2) {
                    ++n;
                }
                else {
                    int j = n++;
                    if (j == array.length) {
                        break;
                    }
                    while (n < length && array[n] >= b2) {
                        ++n;
                    }
                    for (int n2 = n - 1; j < n2; ++j, --n2) {
                        final int n3 = array2[j];
                        array2[j] = array2[n2];
                        array2[n2] = n3;
                    }
                }
            }
            --b2;
        }
        return array2;
    }
    
    public static int[] createInverseMap(final int[] array) {
        if (array == null) {
            return null;
        }
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[array[i]] = i;
        }
        return array2;
    }
    
    public static int[] createContiguousOrder(final int[] array) {
        if (array != null) {
            return computeContiguousOrder(array, 0, array.length);
        }
        return null;
    }
    
    private static int[] computeContiguousOrder(final int[] array, final int n, final int n2) {
        final int[] array2 = new int[n2 - n];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = i + n;
        }
        for (int j = 0; j < array2.length - 1; ++j) {
            int n3 = j;
            int n4 = array[array2[n3]];
            for (int k = j; k < array2.length; ++k) {
                if (array[array2[k]] < n4) {
                    n3 = k;
                    n4 = array[array2[n3]];
                }
            }
            final int n5 = array2[j];
            array2[j] = array2[n3];
            array2[n3] = n5;
        }
        if (n != 0) {
            for (int l = 0; l < array2.length; ++l) {
                final int[] array3 = array2;
                final int n6 = l;
                array3[n6] -= n;
            }
        }
        int n7;
        for (n7 = 0; n7 < array2.length && array2[n7] == n7; ++n7) {}
        if (n7 == array2.length) {
            return null;
        }
        return createInverseMap(array2);
    }
    
    public static int[] createNormalizedMap(final int[] array, final byte[] array2, final int n, final int n2) {
        if (array == null) {
            return null;
        }
        if (n == 0 && n2 == array.length) {
            return array;
        }
        byte b;
        int n3;
        int n4;
        if (array2 == null) {
            b = 0;
            n3 = 1;
            n4 = 1;
        }
        else if (array2[n] == array2[n2 - 1]) {
            b = array2[n];
            n4 = (((b & 0x1) == 0x0) ? 1 : 0);
            int n5;
            for (n5 = n; n5 < n2 && array2[n5] >= b; ++n5) {
                if (n4 != 0) {
                    n4 = ((array2[n5] == b) ? 1 : 0);
                }
            }
            n3 = ((n5 == n2) ? 1 : 0);
        }
        else {
            n3 = 0;
            b = 0;
            n4 = 0;
        }
        if (n3 == 0) {
            return computeContiguousOrder(array, n, n2);
        }
        if (n4 != 0) {
            return null;
        }
        final int[] array3 = new int[n2 - n];
        int n6;
        if ((b & 0x1) != 0x0) {
            n6 = array[n2 - 1];
        }
        else {
            n6 = array[n];
        }
        if (n6 == 0) {
            System.arraycopy(array, n, array3, 0, n2 - n);
        }
        else {
            for (int i = 0; i < array3.length; ++i) {
                array3[i] = array[i + n] - n6;
            }
        }
        return array3;
    }
    
    public static void reorderVisually(final byte[] array, final Object[] array2) {
        final int length = array.length;
        byte b = 63;
        byte b2 = 0;
        for (final byte b3 : array) {
            if (b3 > b2) {
                b2 = b3;
            }
            if ((b3 & 0x1) != 0x0 && b3 < b) {
                b = b3;
            }
        }
        while (b2 >= b) {
            int n = 0;
            while (true) {
                if (n < length && array[n] < b2) {
                    ++n;
                }
                else {
                    int j = n++;
                    if (j == array.length) {
                        break;
                    }
                    while (n < length && array[n] >= b2) {
                        ++n;
                    }
                    for (int n2 = n - 1; j < n2; ++j, --n2) {
                        final Object o = array2[j];
                        array2[j] = array2[n2];
                        array2[n2] = o;
                    }
                }
            }
            --b2;
        }
    }
}
