package sun.java2d.pisces;

import java.util.Arrays;

final class Helpers
{
    private Helpers() {
        throw new Error("This is a non instantiable class");
    }
    
    static boolean within(final float n, final float n2, final float n3) {
        final float n4 = n2 - n;
        return n4 <= n3 && n4 >= -n3;
    }
    
    static boolean within(final double n, final double n2, final double n3) {
        final double n4 = n2 - n;
        return n4 <= n3 && n4 >= -n3;
    }
    
    static int quadraticRoots(final float n, final float n2, final float n3, final float[] array, final int n4) {
        int n5 = n4;
        if (n != 0.0f) {
            final float n6 = n2 * n2 - 4.0f * n * n3;
            if (n6 > 0.0f) {
                final float n7 = (float)Math.sqrt(n6);
                if (n2 >= 0.0f) {
                    array[n5++] = 2.0f * n3 / (-n2 - n7);
                    array[n5++] = (-n2 - n7) / (2.0f * n);
                }
                else {
                    array[n5++] = (-n2 + n7) / (2.0f * n);
                    array[n5++] = 2.0f * n3 / (-n2 + n7);
                }
            }
            else if (n6 == 0.0f) {
                array[n5++] = -n2 / (2.0f * n);
            }
        }
        else if (n2 != 0.0f) {
            array[n5++] = -n3 / n2;
        }
        return n5 - n4;
    }
    
    static int cubicRootsInAB(final float n, float n2, float n3, float n4, final float[] array, final int n5, final float n6, final float n7) {
        if (n == 0.0f) {
            return filterOutNotInAB(array, n5, quadraticRoots(n2, n3, n4, array, n5), n6, n7) - n5;
        }
        n2 /= n;
        n3 /= n;
        n4 /= n;
        final double n8 = n2 * n2;
        final double n9 = 0.3333333333333333 * (-0.3333333333333333 * n8 + n3);
        final double n10 = 0.5 * (0.07407407407407407 * n2 * n8 - 0.3333333333333333 * n2 * n3 + n4);
        final double n11 = n9 * n9 * n9;
        final double n12 = n10 * n10 + n11;
        int n15;
        if (n12 < 0.0) {
            final double n13 = 0.3333333333333333 * Math.acos(-n10 / Math.sqrt(-n11));
            final double n14 = 2.0 * Math.sqrt(-n9);
            array[n5 + 0] = (float)(n14 * Math.cos(n13));
            array[n5 + 1] = (float)(-n14 * Math.cos(n13 + 1.0471975511965976));
            array[n5 + 2] = (float)(-n14 * Math.cos(n13 - 1.0471975511965976));
            n15 = 3;
        }
        else {
            final double sqrt = Math.sqrt(n12);
            array[n5] = (float)(Math.cbrt(sqrt - n10) + -Math.cbrt(sqrt + n10));
            n15 = 1;
            if (within(n12, 0.0, 1.0E-8)) {
                array[n5 + 1] = -(array[n5] / 2.0f);
                n15 = 2;
            }
        }
        final float n16 = 0.33333334f * n2;
        for (int i = 0; i < n15; ++i) {
            final int n17 = n5 + i;
            array[n17] -= n16;
        }
        return filterOutNotInAB(array, n5, n15, n6, n7) - n5;
    }
    
    static float[] widenArray(final float[] array, final int n, final int n2) {
        if (array.length >= n + n2) {
            return array;
        }
        return Arrays.copyOf(array, 2 * (n + n2));
    }
    
    static int[] widenArray(final int[] array, final int n, final int n2) {
        if (array.length >= n + n2) {
            return array;
        }
        return Arrays.copyOf(array, 2 * (n + n2));
    }
    
    static float evalCubic(final float n, final float n2, final float n3, final float n4, final float n5) {
        return n5 * (n5 * (n5 * n + n2) + n3) + n4;
    }
    
    static float evalQuad(final float n, final float n2, final float n3, final float n4) {
        return n4 * (n4 * n + n2) + n3;
    }
    
    static int filterOutNotInAB(final float[] array, final int n, final int n2, final float n3, final float n4) {
        int n5 = n;
        for (int i = n; i < n + n2; ++i) {
            if (array[i] >= n3 && array[i] < n4) {
                array[n5++] = array[i];
            }
        }
        return n5;
    }
    
    static float polyLineLength(final float[] array, final int n, final int n2) {
        assert n2 % 2 == 0 && array.length >= n + n2 : "";
        float n3 = 0.0f;
        for (int i = n + 2; i < n + n2; i += 2) {
            n3 += linelen(array[i], array[i + 1], array[i - 2], array[i - 1]);
        }
        return n3;
    }
    
    static float linelen(final float n, final float n2, final float n3, final float n4) {
        final float n5 = n3 - n;
        final float n6 = n4 - n2;
        return (float)Math.sqrt(n5 * n5 + n6 * n6);
    }
    
    static void subdivide(final float[] array, final int n, final float[] array2, final int n2, final float[] array3, final int n3, final int n4) {
        switch (n4) {
            case 6: {
                subdivideQuad(array, n, array2, n2, array3, n3);
                break;
            }
            case 8: {
                subdivideCubic(array, n, array2, n2, array3, n3);
                break;
            }
            default: {
                throw new InternalError("Unsupported curve type");
            }
        }
    }
    
    static void isort(final float[] array, final int n, final int n2) {
        for (int i = n + 1; i < n + n2; ++i) {
            float n3;
            int n4;
            for (n3 = array[i], n4 = i - 1; n4 >= n && array[n4] > n3; --n4) {
                array[n4 + 1] = array[n4];
            }
            array[n4 + 1] = n3;
        }
    }
    
    static void subdivideCubic(final float[] array, final int n, final float[] array2, final int n2, final float[] array3, final int n3) {
        final float n4 = array[n + 0];
        final float n5 = array[n + 1];
        final float n6 = array[n + 2];
        final float n7 = array[n + 3];
        final float n8 = array[n + 4];
        final float n9 = array[n + 5];
        final float n10 = array[n + 6];
        final float n11 = array[n + 7];
        if (array2 != null) {
            array2[n2 + 0] = n4;
            array2[n2 + 1] = n5;
        }
        if (array3 != null) {
            array3[n3 + 6] = n10;
            array3[n3 + 7] = n11;
        }
        final float n12 = (n4 + n6) / 2.0f;
        final float n13 = (n5 + n7) / 2.0f;
        final float n14 = (n10 + n8) / 2.0f;
        final float n15 = (n11 + n9) / 2.0f;
        final float n16 = (n6 + n8) / 2.0f;
        final float n17 = (n7 + n9) / 2.0f;
        final float n18 = (n12 + n16) / 2.0f;
        final float n19 = (n13 + n17) / 2.0f;
        final float n20 = (n14 + n16) / 2.0f;
        final float n21 = (n15 + n17) / 2.0f;
        final float n22 = (n18 + n20) / 2.0f;
        final float n23 = (n19 + n21) / 2.0f;
        if (array2 != null) {
            array2[n2 + 2] = n12;
            array2[n2 + 3] = n13;
            array2[n2 + 4] = n18;
            array2[n2 + 5] = n19;
            array2[n2 + 6] = n22;
            array2[n2 + 7] = n23;
        }
        if (array3 != null) {
            array3[n3 + 0] = n22;
            array3[n3 + 1] = n23;
            array3[n3 + 2] = n20;
            array3[n3 + 3] = n21;
            array3[n3 + 4] = n14;
            array3[n3 + 5] = n15;
        }
    }
    
    static void subdivideCubicAt(final float n, final float[] array, final int n2, final float[] array2, final int n3, final float[] array3, final int n4) {
        final float n5 = array[n2 + 0];
        final float n6 = array[n2 + 1];
        final float n7 = array[n2 + 2];
        final float n8 = array[n2 + 3];
        final float n9 = array[n2 + 4];
        final float n10 = array[n2 + 5];
        final float n11 = array[n2 + 6];
        final float n12 = array[n2 + 7];
        if (array2 != null) {
            array2[n3 + 0] = n5;
            array2[n3 + 1] = n6;
        }
        if (array3 != null) {
            array3[n4 + 6] = n11;
            array3[n4 + 7] = n12;
        }
        final float n13 = n5 + n * (n7 - n5);
        final float n14 = n6 + n * (n8 - n6);
        final float n15 = n9 + n * (n11 - n9);
        final float n16 = n10 + n * (n12 - n10);
        final float n17 = n7 + n * (n9 - n7);
        final float n18 = n8 + n * (n10 - n8);
        final float n19 = n13 + n * (n17 - n13);
        final float n20 = n14 + n * (n18 - n14);
        final float n21 = n17 + n * (n15 - n17);
        final float n22 = n18 + n * (n16 - n18);
        final float n23 = n19 + n * (n21 - n19);
        final float n24 = n20 + n * (n22 - n20);
        if (array2 != null) {
            array2[n3 + 2] = n13;
            array2[n3 + 3] = n14;
            array2[n3 + 4] = n19;
            array2[n3 + 5] = n20;
            array2[n3 + 6] = n23;
            array2[n3 + 7] = n24;
        }
        if (array3 != null) {
            array3[n4 + 0] = n23;
            array3[n4 + 1] = n24;
            array3[n4 + 2] = n21;
            array3[n4 + 3] = n22;
            array3[n4 + 4] = n15;
            array3[n4 + 5] = n16;
        }
    }
    
    static void subdivideQuad(final float[] array, final int n, final float[] array2, final int n2, final float[] array3, final int n3) {
        final float n4 = array[n + 0];
        final float n5 = array[n + 1];
        final float n6 = array[n + 2];
        final float n7 = array[n + 3];
        final float n8 = array[n + 4];
        final float n9 = array[n + 5];
        if (array2 != null) {
            array2[n2 + 0] = n4;
            array2[n2 + 1] = n5;
        }
        if (array3 != null) {
            array3[n3 + 4] = n8;
            array3[n3 + 5] = n9;
        }
        final float n10 = (n4 + n6) / 2.0f;
        final float n11 = (n5 + n7) / 2.0f;
        final float n12 = (n8 + n6) / 2.0f;
        final float n13 = (n9 + n7) / 2.0f;
        final float n14 = (n10 + n12) / 2.0f;
        final float n15 = (n11 + n13) / 2.0f;
        if (array2 != null) {
            array2[n2 + 2] = n10;
            array2[n2 + 3] = n11;
            array2[n2 + 4] = n14;
            array2[n2 + 5] = n15;
        }
        if (array3 != null) {
            array3[n3 + 0] = n14;
            array3[n3 + 1] = n15;
            array3[n3 + 2] = n12;
            array3[n3 + 3] = n13;
        }
    }
    
    static void subdivideQuadAt(final float n, final float[] array, final int n2, final float[] array2, final int n3, final float[] array3, final int n4) {
        final float n5 = array[n2 + 0];
        final float n6 = array[n2 + 1];
        final float n7 = array[n2 + 2];
        final float n8 = array[n2 + 3];
        final float n9 = array[n2 + 4];
        final float n10 = array[n2 + 5];
        if (array2 != null) {
            array2[n3 + 0] = n5;
            array2[n3 + 1] = n6;
        }
        if (array3 != null) {
            array3[n4 + 4] = n9;
            array3[n4 + 5] = n10;
        }
        final float n11 = n5 + n * (n7 - n5);
        final float n12 = n6 + n * (n8 - n6);
        final float n13 = n7 + n * (n9 - n7);
        final float n14 = n8 + n * (n10 - n8);
        final float n15 = n11 + n * (n13 - n11);
        final float n16 = n12 + n * (n14 - n12);
        if (array2 != null) {
            array2[n3 + 2] = n11;
            array2[n3 + 3] = n12;
            array2[n3 + 4] = n15;
            array2[n3 + 5] = n16;
        }
        if (array3 != null) {
            array3[n4 + 0] = n15;
            array3[n4 + 1] = n16;
            array3[n4 + 2] = n13;
            array3[n4 + 3] = n14;
        }
    }
    
    static void subdivideAt(final float n, final float[] array, final int n2, final float[] array2, final int n3, final float[] array3, final int n4, final int n5) {
        switch (n5) {
            case 8: {
                subdivideCubicAt(n, array, n2, array2, n3, array3, n4);
                break;
            }
            case 6: {
                subdivideQuadAt(n, array, n2, array2, n3, array3, n4);
                break;
            }
        }
    }
}
