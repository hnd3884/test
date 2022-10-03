package com.jhlabs.math;

public class RidgedFBM implements Function2D
{
    public float evaluate(final float x, final float y) {
        return 1.0f - Math.abs(Noise.noise2(x, y));
    }
}
