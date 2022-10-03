package com.sun.media.sound;

public final class SoftLanczosResampler extends SoftAbstractResampler
{
    float[][] sinc_table;
    int sinc_table_fsize;
    int sinc_table_size;
    int sinc_table_center;
    
    public SoftLanczosResampler() {
        this.sinc_table_fsize = 2000;
        this.sinc_table_size = 5;
        this.sinc_table_center = this.sinc_table_size / 2;
        this.sinc_table = new float[this.sinc_table_fsize][];
        for (int i = 0; i < this.sinc_table_fsize; ++i) {
            this.sinc_table[i] = sincTable(this.sinc_table_size, -i / (float)this.sinc_table_fsize);
        }
    }
    
    public static double sinc(final double n) {
        return (n == 0.0) ? 1.0 : (Math.sin(3.141592653589793 * n) / (3.141592653589793 * n));
    }
    
    public static float[] sincTable(final int n, final float n2) {
        final int n3 = n / 2;
        final float[] array = new float[n];
        for (int i = 0; i < n; ++i) {
            final float n4 = -n3 + i + n2;
            if (n4 < -2.0f || n4 > 2.0f) {
                array[i] = 0.0f;
            }
            else if (n4 == 0.0f) {
                array[i] = 1.0f;
            }
            else {
                array[i] = (float)(2.0 * Math.sin(3.141592653589793 * n4) * Math.sin(3.141592653589793 * n4 / 2.0) / (3.141592653589793 * n4 * (3.141592653589793 * n4)));
            }
        }
        return array;
    }
    
    @Override
    public int getPadding() {
        return this.sinc_table_size / 2 + 2;
    }
    
    @Override
    public void interpolate(final float[] array, final float[] array2, final float n, final float[] array3, final float n2, final float[] array4, final int[] array5, final int n3) {
        float n4 = array3[0];
        float n5 = array2[0];
        int n6 = array5[0];
        if (n2 == 0.0f) {
            while (n5 < n && n6 < n3) {
                final int n7 = (int)n5;
                final float[] array6 = this.sinc_table[(int)((n5 - n7) * this.sinc_table_fsize)];
                int n8 = n7 - this.sinc_table_center;
                float n9 = 0.0f;
                for (int i = 0; i < this.sinc_table_size; ++i, ++n8) {
                    n9 += array[n8] * array6[i];
                }
                array4[n6++] = n9;
                n5 += n4;
            }
        }
        else {
            while (n5 < n && n6 < n3) {
                final int n10 = (int)n5;
                final float[] array7 = this.sinc_table[(int)((n5 - n10) * this.sinc_table_fsize)];
                int n11 = n10 - this.sinc_table_center;
                float n12 = 0.0f;
                for (int j = 0; j < this.sinc_table_size; ++j, ++n11) {
                    n12 += array[n11] * array7[j];
                }
                array4[n6++] = n12;
                n5 += n4;
                n4 += n2;
            }
        }
        array2[0] = n5;
        array5[0] = n6;
        array3[0] = n4;
    }
}
