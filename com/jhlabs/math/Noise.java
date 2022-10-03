package com.jhlabs.math;

import java.util.Random;

public class Noise implements Function1D, Function2D, Function3D
{
    private static Random randomGenerator;
    private static final int B = 256;
    private static final int BM = 255;
    private static final int N = 4096;
    static int[] p;
    static float[][] g3;
    static float[][] g2;
    static float[] g1;
    static boolean start;
    
    public float evaluate(final float x) {
        return noise1(x);
    }
    
    public float evaluate(final float x, final float y) {
        return noise2(x, y);
    }
    
    public float evaluate(final float x, final float y, final float z) {
        return noise3(x, y, z);
    }
    
    public static float turbulence2(final float x, final float y, final float octaves) {
        float t = 0.0f;
        for (float f = 1.0f; f <= octaves; f *= 2.0f) {
            t += Math.abs(noise2(f * x, f * y)) / f;
        }
        return t;
    }
    
    public static float turbulence3(final float x, final float y, final float z, final float octaves) {
        float t = 0.0f;
        for (float f = 1.0f; f <= octaves; f *= 2.0f) {
            t += Math.abs(noise3(f * x, f * y, f * z)) / f;
        }
        return t;
    }
    
    private static float sCurve(final float t) {
        return t * t * (3.0f - 2.0f * t);
    }
    
    public static float noise1(final float x) {
        if (Noise.start) {
            Noise.start = false;
            init();
        }
        final float t = x + 4096.0f;
        final int bx0 = (int)t & 0xFF;
        final int bx2 = bx0 + 1 & 0xFF;
        final float rx0 = t - (int)t;
        final float rx2 = rx0 - 1.0f;
        final float sx = sCurve(rx0);
        final float u = rx0 * Noise.g1[Noise.p[bx0]];
        final float v = rx2 * Noise.g1[Noise.p[bx2]];
        return 2.3f * lerp(sx, u, v);
    }
    
    public static float noise2(final float x, final float y) {
        if (Noise.start) {
            Noise.start = false;
            init();
        }
        float t = x + 4096.0f;
        final int bx0 = (int)t & 0xFF;
        final int bx2 = bx0 + 1 & 0xFF;
        final float rx0 = t - (int)t;
        final float rx2 = rx0 - 1.0f;
        t = y + 4096.0f;
        final int by0 = (int)t & 0xFF;
        final int by2 = by0 + 1 & 0xFF;
        final float ry0 = t - (int)t;
        final float ry2 = ry0 - 1.0f;
        final int i = Noise.p[bx0];
        final int j = Noise.p[bx2];
        final int b00 = Noise.p[i + by0];
        final int b2 = Noise.p[j + by0];
        final int b3 = Noise.p[i + by2];
        final int b4 = Noise.p[j + by2];
        final float sx = sCurve(rx0);
        final float sy = sCurve(ry0);
        float[] q = Noise.g2[b00];
        float u = rx0 * q[0] + ry0 * q[1];
        q = Noise.g2[b2];
        float v = rx2 * q[0] + ry0 * q[1];
        final float a = lerp(sx, u, v);
        q = Noise.g2[b3];
        u = rx0 * q[0] + ry2 * q[1];
        q = Noise.g2[b4];
        v = rx2 * q[0] + ry2 * q[1];
        final float b5 = lerp(sx, u, v);
        return 1.5f * lerp(sy, a, b5);
    }
    
    public static float noise3(final float x, final float y, final float z) {
        if (Noise.start) {
            Noise.start = false;
            init();
        }
        float t = x + 4096.0f;
        final int bx0 = (int)t & 0xFF;
        final int bx2 = bx0 + 1 & 0xFF;
        final float rx0 = t - (int)t;
        final float rx2 = rx0 - 1.0f;
        t = y + 4096.0f;
        final int by0 = (int)t & 0xFF;
        final int by2 = by0 + 1 & 0xFF;
        final float ry0 = t - (int)t;
        final float ry2 = ry0 - 1.0f;
        t = z + 4096.0f;
        final int bz0 = (int)t & 0xFF;
        final int bz2 = bz0 + 1 & 0xFF;
        final float rz0 = t - (int)t;
        final float rz2 = rz0 - 1.0f;
        final int i = Noise.p[bx0];
        final int j = Noise.p[bx2];
        final int b00 = Noise.p[i + by0];
        final int b2 = Noise.p[j + by0];
        final int b3 = Noise.p[i + by2];
        final int b4 = Noise.p[j + by2];
        t = sCurve(rx0);
        final float sy = sCurve(ry0);
        final float sz = sCurve(rz0);
        float[] q = Noise.g3[b00 + bz0];
        float u = rx0 * q[0] + ry0 * q[1] + rz0 * q[2];
        q = Noise.g3[b2 + bz0];
        float v = rx2 * q[0] + ry0 * q[1] + rz0 * q[2];
        float a = lerp(t, u, v);
        q = Noise.g3[b3 + bz0];
        u = rx0 * q[0] + ry2 * q[1] + rz0 * q[2];
        q = Noise.g3[b4 + bz0];
        v = rx2 * q[0] + ry2 * q[1] + rz0 * q[2];
        float b5 = lerp(t, u, v);
        final float c = lerp(sy, a, b5);
        q = Noise.g3[b00 + bz2];
        u = rx0 * q[0] + ry0 * q[1] + rz2 * q[2];
        q = Noise.g3[b2 + bz2];
        v = rx2 * q[0] + ry0 * q[1] + rz2 * q[2];
        a = lerp(t, u, v);
        q = Noise.g3[b3 + bz2];
        u = rx0 * q[0] + ry2 * q[1] + rz2 * q[2];
        q = Noise.g3[b4 + bz2];
        v = rx2 * q[0] + ry2 * q[1] + rz2 * q[2];
        b5 = lerp(t, u, v);
        final float d = lerp(sy, a, b5);
        return 1.5f * lerp(sz, c, d);
    }
    
    public static float lerp(final float t, final float a, final float b) {
        return a + t * (b - a);
    }
    
    private static void normalize2(final float[] v) {
        final float s = (float)Math.sqrt(v[0] * v[0] + v[1] * v[1]);
        v[0] /= s;
        v[1] /= s;
    }
    
    static void normalize3(final float[] v) {
        final float s = (float)Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        v[0] /= s;
        v[1] /= s;
        v[2] /= s;
    }
    
    private static int random() {
        return Noise.randomGenerator.nextInt() & Integer.MAX_VALUE;
    }
    
    private static void init() {
        for (int i = 0; i < 256; ++i) {
            Noise.p[i] = i;
            Noise.g1[i] = (random() % 512 - 256) / 256.0f;
            for (int j = 0; j < 2; ++j) {
                Noise.g2[i][j] = (random() % 512 - 256) / 256.0f;
            }
            normalize2(Noise.g2[i]);
            for (int j = 0; j < 3; ++j) {
                Noise.g3[i][j] = (random() % 512 - 256) / 256.0f;
            }
            normalize3(Noise.g3[i]);
        }
        for (int i = 255; i >= 0; --i) {
            final int k = Noise.p[i];
            final int j;
            Noise.p[i] = Noise.p[j = random() % 256];
            Noise.p[j] = k;
        }
        for (int i = 0; i < 258; ++i) {
            Noise.p[256 + i] = Noise.p[i];
            Noise.g1[256 + i] = Noise.g1[i];
            for (int j = 0; j < 2; ++j) {
                Noise.g2[256 + i][j] = Noise.g2[i][j];
            }
            for (int j = 0; j < 3; ++j) {
                Noise.g3[256 + i][j] = Noise.g3[i][j];
            }
        }
    }
    
    public static float[] findRange(final Function1D f, float[] minmax) {
        if (minmax == null) {
            minmax = new float[2];
        }
        float min = 0.0f;
        float max = 0.0f;
        for (float x = -100.0f; x < 100.0f; x += (float)1.27139) {
            final float n = f.evaluate(x);
            min = Math.min(min, n);
            max = Math.max(max, n);
        }
        minmax[0] = min;
        minmax[1] = max;
        return minmax;
    }
    
    public static float[] findRange(final Function2D f, float[] minmax) {
        if (minmax == null) {
            minmax = new float[2];
        }
        float min = 0.0f;
        float max = 0.0f;
        for (float y = -100.0f; y < 100.0f; y += (float)10.35173) {
            for (float x = -100.0f; x < 100.0f; x += (float)10.77139) {
                final float n = f.evaluate(x, y);
                min = Math.min(min, n);
                max = Math.max(max, n);
            }
        }
        minmax[0] = min;
        minmax[1] = max;
        return minmax;
    }
    
    static {
        Noise.randomGenerator = new Random();
        Noise.p = new int[514];
        Noise.g3 = new float[514][3];
        Noise.g2 = new float[514][2];
        Noise.g1 = new float[514];
        Noise.start = true;
    }
}
