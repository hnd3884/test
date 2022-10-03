package com.sun.media.sound;

public final class SoftSincResampler extends SoftAbstractResampler
{
    float[][][] sinc_table;
    int sinc_scale_size;
    int sinc_table_fsize;
    int sinc_table_size;
    int sinc_table_center;
    
    public SoftSincResampler() {
        this.sinc_scale_size = 100;
        this.sinc_table_fsize = 800;
        this.sinc_table_size = 30;
        this.sinc_table_center = this.sinc_table_size / 2;
        this.sinc_table = new float[this.sinc_scale_size][this.sinc_table_fsize][];
        for (int i = 0; i < this.sinc_scale_size; ++i) {
            final float n = (float)(1.0 / (1.0 + Math.pow(i, 1.1) / 10.0));
            for (int j = 0; j < this.sinc_table_fsize; ++j) {
                this.sinc_table[i][j] = sincTable(this.sinc_table_size, -j / (float)this.sinc_table_fsize, n);
            }
        }
    }
    
    public static double sinc(final double n) {
        return (n == 0.0) ? 1.0 : (Math.sin(3.141592653589793 * n) / (3.141592653589793 * n));
    }
    
    public static float[] wHanning(final int n, final float n2) {
        final float[] array = new float[n];
        for (int i = 0; i < n; ++i) {
            array[i] = (float)(-0.5 * Math.cos(6.283185307179586 * (i + n2) / n) + 0.5);
        }
        return array;
    }
    
    public static float[] sincTable(final int n, final float n2, final float n3) {
        final int n4 = n / 2;
        final float[] wHanning = wHanning(n, n2);
        for (int i = 0; i < n; ++i) {
            final float[] array = wHanning;
            final int n5 = i;
            array[n5] *= (float)(sinc((-n4 + i + n2) * n3) * n3);
        }
        return wHanning;
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
        final int n7 = this.sinc_scale_size - 1;
        if (n2 == 0.0f) {
            int n8 = (int)((n4 - 1.0f) * 10.0f);
            if (n8 < 0) {
                n8 = 0;
            }
            else if (n8 > n7) {
                n8 = n7;
            }
            final float[][] array6 = this.sinc_table[n8];
            while (n5 < n && n6 < n3) {
                final int n9 = (int)n5;
                final float[] array7 = array6[(int)((n5 - n9) * this.sinc_table_fsize)];
                int n10 = n9 - this.sinc_table_center;
                float n11 = 0.0f;
                for (int i = 0; i < this.sinc_table_size; ++i, ++n10) {
                    n11 += array[n10] * array7[i];
                }
                array4[n6++] = n11;
                n5 += n4;
            }
        }
        else {
            while (n5 < n && n6 < n3) {
                final int n12 = (int)n5;
                int n13 = (int)((n4 - 1.0f) * 10.0f);
                if (n13 < 0) {
                    n13 = 0;
                }
                else if (n13 > n7) {
                    n13 = n7;
                }
                final float[] array8 = this.sinc_table[n13][(int)((n5 - n12) * this.sinc_table_fsize)];
                int n14 = n12 - this.sinc_table_center;
                float n15 = 0.0f;
                for (int j = 0; j < this.sinc_table_size; ++j, ++n14) {
                    n15 += array[n14] * array8[j];
                }
                array4[n6++] = n15;
                n5 += n4;
                n4 += n2;
            }
        }
        array2[0] = n5;
        array5[0] = n6;
        array3[0] = n4;
    }
}
