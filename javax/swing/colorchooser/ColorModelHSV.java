package javax.swing.colorchooser;

final class ColorModelHSV extends ColorModel
{
    ColorModelHSV() {
        super("hsv", new String[] { "Hue", "Saturation", "Value", "Transparency" });
    }
    
    @Override
    void setColor(final int n, final float[] array) {
        super.setColor(n, array);
        RGBtoHSV(array, array);
        array[3] = 1.0f - array[3];
    }
    
    @Override
    int getColor(final float[] array) {
        array[3] = 1.0f - array[3];
        HSVtoRGB(array, array);
        return super.getColor(array);
    }
    
    @Override
    int getMaximum(final int n) {
        return (n == 0) ? 360 : 100;
    }
    
    @Override
    float getDefault(final int n) {
        return (n == 0) ? -1.0f : 1.0f;
    }
    
    private static float[] HSVtoRGB(final float[] array, float[] array2) {
        if (array2 == null) {
            array2 = new float[3];
        }
        final float n = array[0];
        final float n2 = array[1];
        final float n3 = array[2];
        array2[0] = n3;
        array2[2] = (array2[1] = n3);
        if (n2 > 0.0f) {
            final float n4 = (n < 1.0f) ? (n * 6.0f) : 0.0f;
            final int n5 = (int)n4;
            final float n6 = n4 - n5;
            switch (n5) {
                case 0: {
                    final float[] array3 = array2;
                    final int n7 = 1;
                    array3[n7] *= 1.0f - n2 * (1.0f - n6);
                    final float[] array4 = array2;
                    final int n8 = 2;
                    array4[n8] *= 1.0f - n2;
                    break;
                }
                case 1: {
                    final float[] array5 = array2;
                    final int n9 = 0;
                    array5[n9] *= 1.0f - n2 * n6;
                    final float[] array6 = array2;
                    final int n10 = 2;
                    array6[n10] *= 1.0f - n2;
                    break;
                }
                case 2: {
                    final float[] array7 = array2;
                    final int n11 = 0;
                    array7[n11] *= 1.0f - n2;
                    final float[] array8 = array2;
                    final int n12 = 2;
                    array8[n12] *= 1.0f - n2 * (1.0f - n6);
                    break;
                }
                case 3: {
                    final float[] array9 = array2;
                    final int n13 = 0;
                    array9[n13] *= 1.0f - n2;
                    final float[] array10 = array2;
                    final int n14 = 1;
                    array10[n14] *= 1.0f - n2 * n6;
                    break;
                }
                case 4: {
                    final float[] array11 = array2;
                    final int n15 = 0;
                    array11[n15] *= 1.0f - n2 * (1.0f - n6);
                    final float[] array12 = array2;
                    final int n16 = 1;
                    array12[n16] *= 1.0f - n2;
                    break;
                }
                case 5: {
                    final float[] array13 = array2;
                    final int n17 = 1;
                    array13[n17] *= 1.0f - n2;
                    final float[] array14 = array2;
                    final int n18 = 2;
                    array14[n18] *= 1.0f - n2 * n6;
                    break;
                }
            }
        }
        return array2;
    }
    
    private static float[] RGBtoHSV(final float[] array, float[] array2) {
        if (array2 == null) {
            array2 = new float[3];
        }
        final float max = ColorModelHSL.max(array[0], array[1], array[2]);
        final float min = ColorModelHSL.min(array[0], array[1], array[2]);
        float n = max - min;
        if (n > 0.0f) {
            n /= max;
        }
        array2[0] = ColorModelHSL.getHue(array[0], array[1], array[2], max, min);
        array2[1] = n;
        array2[2] = max;
        return array2;
    }
}
