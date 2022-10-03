package com.jhlabs.image;

public class GrayscaleColormap implements Colormap
{
    public int getColor(final float v) {
        int n = (int)(v * 255.0f);
        if (n < 0) {
            n = 0;
        }
        else if (n > 255) {
            n = 255;
        }
        return 0xFF000000 | n << 16 | n << 8 | n;
    }
}
