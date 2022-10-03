package javax.swing.colorchooser;

final class ColorModelCMYK extends ColorModel
{
    ColorModelCMYK() {
        super("cmyk", new String[] { "Cyan", "Magenta", "Yellow", "Black", "Alpha" });
    }
    
    @Override
    void setColor(final int n, final float[] array) {
        super.setColor(n, array);
        array[4] = array[3];
        RGBtoCMYK(array, array);
    }
    
    @Override
    int getColor(final float[] array) {
        CMYKtoRGB(array, array);
        array[3] = array[4];
        return super.getColor(array);
    }
    
    private static float[] CMYKtoRGB(final float[] array, float[] array2) {
        if (array2 == null) {
            array2 = new float[3];
        }
        array2[0] = 1.0f + array[0] * array[3] - array[3] - array[0];
        array2[1] = 1.0f + array[1] * array[3] - array[3] - array[1];
        array2[2] = 1.0f + array[2] * array[3] - array[3] - array[2];
        return array2;
    }
    
    private static float[] RGBtoCMYK(final float[] array, float[] array2) {
        if (array2 == null) {
            array2 = new float[4];
        }
        final float max = ColorModelHSL.max(array[0], array[1], array[2]);
        if (max > 0.0f) {
            array2[0] = 1.0f - array[0] / max;
            array2[1] = 1.0f - array[1] / max;
            array2[2] = 1.0f - array[2] / max;
        }
        else {
            array2[0] = 0.0f;
            array2[2] = (array2[1] = 0.0f);
        }
        array2[3] = 1.0f - max;
        return array2;
    }
}
