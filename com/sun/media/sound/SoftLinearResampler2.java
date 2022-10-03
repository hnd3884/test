package com.sun.media.sound;

public final class SoftLinearResampler2 extends SoftAbstractResampler
{
    @Override
    public int getPadding() {
        return 2;
    }
    
    @Override
    public void interpolate(final float[] array, final float[] array2, final float n, final float[] array3, float n2, final float[] array4, final int[] array5, final int n3) {
        final float n4 = array3[0];
        float n5 = array2[0];
        int i = array5[0];
        int n6 = n3;
        if (n5 >= n || i >= n6) {
            return;
        }
        int n7 = (int)(n5 * 32768.0f);
        final int n8 = (int)(n * 32768.0f);
        int n9 = (int)(n4 * 32768.0f);
        float n10 = n9 * 3.0517578E-5f;
        if (n2 == 0.0f) {
            int n11 = n8 - n7;
            final int n12 = n11 % n9;
            if (n12 != 0) {
                n11 += n9 - n12;
            }
            final int n13 = i + n11 / n9;
            if (n13 < n6) {
                n6 = n13;
            }
            while (i < n6) {
                final int n14 = n7 >> 15;
                final float n15 = n5 - n14;
                final float n16 = array[n14];
                array4[i++] = n16 + (array[n14 + 1] - n16) * n15;
                n7 += n9;
                n5 += n10;
            }
        }
        else {
            int n17;
            int n18;
            float n19;
            float n20;
            for (n17 = (int)(n2 * 32768.0f), n2 = n17 * 3.0517578E-5f; n7 < n8 && i < n6; array4[i++] = n20 + (array[n18 + 1] - n20) * n19, n5 += n10, n7 += n9, n10 += n2, n9 += n17) {
                n18 = n7 >> 15;
                n19 = n5 - n18;
                n20 = array[n18];
            }
        }
        array2[0] = n5;
        array5[0] = i;
        array3[0] = n10;
    }
}
