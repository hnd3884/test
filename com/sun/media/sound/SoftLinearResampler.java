package com.sun.media.sound;

public final class SoftLinearResampler extends SoftAbstractResampler
{
    @Override
    public int getPadding() {
        return 2;
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
                final float n9 = array[n7];
                array4[n6++] = n9 + (array[n7 + 1] - n9) * n8;
                n5 += n4;
            }
        }
        else {
            while (n5 < n && n6 < n3) {
                final int n10 = (int)n5;
                final float n11 = n5 - n10;
                final float n12 = array[n10];
                array4[n6++] = n12 + (array[n10 + 1] - n12) * n11;
                n5 += n4;
                n4 += n2;
            }
        }
        array2[0] = n5;
        array5[0] = n6;
        array3[0] = n4;
    }
}
