package com.jhlabs.image;

public class Spectrum
{
    private static int adjust(final float color, final float factor, final float gamma) {
        if (color == 0.0) {
            return 0;
        }
        return (int)Math.round(255.0 * Math.pow(color * factor, gamma));
    }
    
    public static int wavelengthToRGB(final float wavelength) {
        final float gamma = 0.8f;
        final int w = (int)wavelength;
        float r;
        float g;
        float b;
        if (w < 380) {
            r = 0.0f;
            g = 0.0f;
            b = 0.0f;
        }
        else if (w < 440) {
            r = -(wavelength - 440.0f) / 60.0f;
            g = 0.0f;
            b = 1.0f;
        }
        else if (w < 490) {
            r = 0.0f;
            g = (wavelength - 440.0f) / 50.0f;
            b = 1.0f;
        }
        else if (w < 510) {
            r = 0.0f;
            g = 1.0f;
            b = -(wavelength - 510.0f) / 20.0f;
        }
        else if (w < 580) {
            r = (wavelength - 510.0f) / 70.0f;
            g = 1.0f;
            b = 0.0f;
        }
        else if (w < 645) {
            r = 1.0f;
            g = -(wavelength - 645.0f) / 65.0f;
            b = 0.0f;
        }
        else if (w <= 780) {
            r = 1.0f;
            g = 0.0f;
            b = 0.0f;
        }
        else {
            r = 0.0f;
            g = 0.0f;
            b = 0.0f;
        }
        float factor;
        if (380 <= w && w <= 419) {
            factor = 0.3f + 0.7f * (wavelength - 380.0f) / 40.0f;
        }
        else if (420 <= w && w <= 700) {
            factor = 1.0f;
        }
        else if (701 <= w && w <= 780) {
            factor = 0.3f + 0.7f * (780.0f - wavelength) / 80.0f;
        }
        else {
            factor = 0.0f;
        }
        final int ir = adjust(r, factor, gamma);
        final int ig = adjust(g, factor, gamma);
        final int ib = adjust(b, factor, gamma);
        return 0xFF000000 | ir << 16 | ig << 8 | ib;
    }
}
