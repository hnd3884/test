package com.sun.media.sound;

public final class SoftCubicResampler extends SoftAbstractResampler
{
    @Override
    public int getPadding() {
        return 3;
    }
    
    @Override
    public void interpolate(final float[] array, final float[] array2, final float n, final float[] array3, final float n2, final float[] array4, final int[] array5, final int n3) {
        float n4 = array3[0];
        float n5 = array2[0];
        int n6 = array5[0];
        if (n2 == 0.0f) {
            while (n5 < n && n6 < n3) {
                final int n7 = (int)n5;
                final float n8 = n5 - n7;
                final float n9 = array[n7 - 1];
                final float n10 = array[n7];
                final float n11 = array[n7 + 1];
                final float n12 = array[n7 + 2] - n11 + n10 - n9;
                array4[n6++] = ((n12 * n8 + (n9 - n10 - n12)) * n8 + (n11 - n9)) * n8 + n10;
                n5 += n4;
            }
        }
        else {
            while (n5 < n && n6 < n3) {
                final int n13 = (int)n5;
                final float n14 = n5 - n13;
                final float n15 = array[n13 - 1];
                final float n16 = array[n13];
                final float n17 = array[n13 + 1];
                final float n18 = array[n13 + 2] - n17 + n16 - n15;
                array4[n6++] = ((n18 * n14 + (n15 - n16 - n18)) * n14 + (n17 - n15)) * n14 + n16;
                n5 += n4;
                n4 += n2;
            }
        }
        array2[0] = n5;
        array5[0] = n6;
        array3[0] = n4;
    }
}
