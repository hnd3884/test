package com.jhlabs.math;

public class FFT
{
    protected float[] w1;
    protected float[] w2;
    protected float[] w3;
    
    public FFT(final int logN) {
        this.w1 = new float[logN];
        this.w2 = new float[logN];
        this.w3 = new float[logN];
        int N = 1;
        for (int k = 0; k < logN; ++k) {
            N <<= 1;
            final double angle = -6.283185307179586 / N;
            this.w1[k] = (float)Math.sin(0.5 * angle);
            this.w2[k] = -2.0f * this.w1[k] * this.w1[k];
            this.w3[k] = (float)Math.sin(angle);
        }
    }
    
    private void scramble(final int n, final float[] real, final float[] imag) {
        int j = 0;
        for (int i = 0; i < n; ++i) {
            if (i > j) {
                float t = real[j];
                real[j] = real[i];
                real[i] = t;
                t = imag[j];
                imag[j] = imag[i];
                imag[i] = t;
            }
            int m;
            for (m = n >> 1; j >= m && m >= 2; j -= m, m >>= 1) {}
            j += m;
        }
    }
    
    private void butterflies(final int n, final int logN, final int direction, final float[] real, final float[] imag) {
        int N = 1;
        for (int k = 0; k < logN; ++k) {
            final int half_N = N;
            N <<= 1;
            float wt = direction * this.w1[k];
            final float wp_re = this.w2[k];
            final float wp_im = direction * this.w3[k];
            float w_re = 1.0f;
            float w_im = 0.0f;
            for (int offset = 0; offset < half_N; ++offset) {
                for (int i = offset; i < n; i += N) {
                    final int j = i + half_N;
                    final float re = real[j];
                    final float im = imag[j];
                    final float temp_re = w_re * re - w_im * im;
                    final float temp_im = w_im * re + w_re * im;
                    real[j] = real[i] - temp_re;
                    final int n2 = i;
                    real[n2] += temp_re;
                    imag[j] = imag[i] - temp_im;
                    final int n3 = i;
                    imag[n3] += temp_im;
                }
                wt = w_re;
                w_re += wt * wp_re - w_im * wp_im;
                w_im += w_im * wp_re + wt * wp_im;
            }
        }
        if (direction == -1) {
            final float nr = 1.0f / n;
            for (int l = 0; l < n; ++l) {
                final int n4 = l;
                real[n4] *= nr;
                final int n5 = l;
                imag[n5] *= nr;
            }
        }
    }
    
    public void transform1D(final float[] real, final float[] imag, final int logN, final int n, final boolean forward) {
        this.scramble(n, real, imag);
        this.butterflies(n, logN, forward ? 1 : -1, real, imag);
    }
    
    public void transform2D(final float[] real, final float[] imag, final int cols, final int rows, final boolean forward) {
        final int log2cols = this.log2(cols);
        final int log2rows = this.log2(rows);
        final int n = Math.max(rows, cols);
        final float[] rtemp = new float[n];
        final float[] itemp = new float[n];
        for (int y = 0; y < rows; ++y) {
            final int offset = y * cols;
            System.arraycopy(real, offset, rtemp, 0, cols);
            System.arraycopy(imag, offset, itemp, 0, cols);
            this.transform1D(rtemp, itemp, log2cols, cols, forward);
            System.arraycopy(rtemp, 0, real, offset, cols);
            System.arraycopy(itemp, 0, imag, offset, cols);
        }
        for (int x = 0; x < cols; ++x) {
            int index = x;
            for (int y2 = 0; y2 < rows; ++y2) {
                rtemp[y2] = real[index];
                itemp[y2] = imag[index];
                index += cols;
            }
            this.transform1D(rtemp, itemp, log2rows, rows, forward);
            index = x;
            for (int y2 = 0; y2 < rows; ++y2) {
                real[index] = rtemp[y2];
                imag[index] = itemp[y2];
                index += cols;
            }
        }
    }
    
    private int log2(final int n) {
        int m;
        int log2n;
        for (m = 1, log2n = 0; m < n; m *= 2, ++log2n) {}
        return (m == n) ? log2n : -1;
    }
}
