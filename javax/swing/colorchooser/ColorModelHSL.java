package javax.swing.colorchooser;

final class ColorModelHSL extends ColorModel
{
    ColorModelHSL() {
        super("hsl", new String[] { "Hue", "Saturation", "Lightness", "Transparency" });
    }
    
    @Override
    void setColor(final int n, final float[] array) {
        super.setColor(n, array);
        RGBtoHSL(array, array);
        array[3] = 1.0f - array[3];
    }
    
    @Override
    int getColor(final float[] array) {
        array[3] = 1.0f - array[3];
        HSLtoRGB(array, array);
        return super.getColor(array);
    }
    
    @Override
    int getMaximum(final int n) {
        return (n == 0) ? 360 : 100;
    }
    
    @Override
    float getDefault(final int n) {
        return (n == 0) ? -1.0f : ((n == 2) ? 0.5f : 1.0f);
    }
    
    private static float[] HSLtoRGB(final float[] array, float[] array2) {
        if (array2 == null) {
            array2 = new float[3];
        }
        final float n = array[0];
        final float n2 = array[1];
        final float n3 = array[2];
        if (n2 > 0.0f) {
            final float n4 = (n < 1.0f) ? (n * 6.0f) : 0.0f;
            final float n5 = n3 + n2 * ((n3 > 0.5f) ? (1.0f - n3) : n3);
            final float n6 = 2.0f * n3 - n5;
            array2[0] = normalize(n5, n6, (n4 < 4.0f) ? (n4 + 2.0f) : (n4 - 4.0f));
            array2[1] = normalize(n5, n6, n4);
            array2[2] = normalize(n5, n6, (n4 < 2.0f) ? (n4 + 4.0f) : (n4 - 2.0f));
        }
        else {
            array2[0] = n3;
            array2[2] = (array2[1] = n3);
        }
        return array2;
    }
    
    private static float[] RGBtoHSL(final float[] array, float[] array2) {
        if (array2 == null) {
            array2 = new float[3];
        }
        final float max = max(array[0], array[1], array[2]);
        final float min = min(array[0], array[1], array[2]);
        final float n = max + min;
        float n2 = max - min;
        if (n2 > 0.0f) {
            n2 /= ((n > 1.0f) ? (2.0f - n) : n);
        }
        array2[0] = getHue(array[0], array[1], array[2], max, min);
        array2[1] = n2;
        array2[2] = n / 2.0f;
        return array2;
    }
    
    static float min(final float n, final float n2, final float n3) {
        final float n4 = (n < n2) ? n : n2;
        return (n4 < n3) ? n4 : n3;
    }
    
    static float max(final float n, final float n2, final float n3) {
        final float n4 = (n > n2) ? n : n2;
        return (n4 > n3) ? n4 : n3;
    }
    
    static float getHue(final float n, final float n2, final float n3, final float n4, final float n5) {
        float n6 = n4 - n5;
        if (n6 > 0.0f) {
            float n7;
            if (n4 == n) {
                n7 = (n2 - n3) / n6;
                if (n7 < 0.0f) {
                    n7 += 6.0f;
                }
            }
            else if (n4 == n2) {
                n7 = 2.0f + (n3 - n) / n6;
            }
            else {
                n7 = 4.0f + (n - n2) / n6;
            }
            n6 = n7 / 6.0f;
        }
        return n6;
    }
    
    private static float normalize(final float n, final float n2, final float n3) {
        if (n3 < 1.0f) {
            return n2 + (n - n2) * n3;
        }
        if (n3 < 3.0f) {
            return n;
        }
        if (n3 < 4.0f) {
            return n2 + (n - n2) * (4.0f - n3);
        }
        return n2;
    }
}
