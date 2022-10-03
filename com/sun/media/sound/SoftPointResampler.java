package com.sun.media.sound;

public final class SoftPointResampler extends SoftAbstractResampler
{
    @Override
    public int getPadding() {
        return 100;
    }
    
    @Override
    public void interpolate(final float[] array, final float[] array2, final float n, final float[] array3, final float n2, final float[] array4, final int[] array5, final int n3) {
        float n4 = array3[0];
        float n5 = array2[0];
        int n6 = array5[0];
        final float n7 = (float)n3;
        if (n2 == 0.0f) {
            while (n5 < n && n6 < n7) {
                array4[n6++] = array[(int)n5];
                n5 += n4;
            }
        }
        else {
            while (n5 < n && n6 < n7) {
                array4[n6++] = array[(int)n5];
                n5 += n4;
                n4 += n2;
            }
        }
        array2[0] = n5;
        array5[0] = n6;
        array3[0] = n4;
    }
}
