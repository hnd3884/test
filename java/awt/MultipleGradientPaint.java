package java.awt;

import java.util.Arrays;
import java.lang.ref.SoftReference;
import java.awt.image.ColorModel;
import java.awt.geom.AffineTransform;

public abstract class MultipleGradientPaint implements Paint
{
    final int transparency;
    final float[] fractions;
    final Color[] colors;
    final AffineTransform gradientTransform;
    final CycleMethod cycleMethod;
    final ColorSpaceType colorSpace;
    ColorModel model;
    float[] normalizedIntervals;
    boolean isSimpleLookup;
    SoftReference<int[][]> gradients;
    SoftReference<int[]> gradient;
    int fastGradientArraySize;
    
    MultipleGradientPaint(final float[] array, final Color[] array2, final CycleMethod cycleMethod, final ColorSpaceType colorSpace, final AffineTransform affineTransform) {
        if (array == null) {
            throw new NullPointerException("Fractions array cannot be null");
        }
        if (array2 == null) {
            throw new NullPointerException("Colors array cannot be null");
        }
        if (cycleMethod == null) {
            throw new NullPointerException("Cycle method cannot be null");
        }
        if (colorSpace == null) {
            throw new NullPointerException("Color space cannot be null");
        }
        if (affineTransform == null) {
            throw new NullPointerException("Gradient transform cannot be null");
        }
        if (array.length != array2.length) {
            throw new IllegalArgumentException("Colors and fractions must have equal size");
        }
        if (array2.length < 2) {
            throw new IllegalArgumentException("User must specify at least 2 colors");
        }
        float n = -1.0f;
        for (final float n2 : array) {
            if (n2 < 0.0f || n2 > 1.0f) {
                throw new IllegalArgumentException("Fraction values must be in the range 0 to 1: " + n2);
            }
            if (n2 <= n) {
                throw new IllegalArgumentException("Keyframe fractions must be increasing: " + n2);
            }
            n = n2;
        }
        boolean b = false;
        boolean b2 = false;
        int length2 = array.length;
        int n3 = 0;
        if (array[0] != 0.0f) {
            b = true;
            ++length2;
            ++n3;
        }
        if (array[array.length - 1] != 1.0f) {
            b2 = true;
            ++length2;
        }
        System.arraycopy(array, 0, this.fractions = new float[length2], n3, array.length);
        System.arraycopy(array2, 0, this.colors = new Color[length2], n3, array2.length);
        if (b) {
            this.fractions[0] = 0.0f;
            this.colors[0] = array2[0];
        }
        if (b2) {
            this.fractions[length2 - 1] = 1.0f;
            this.colors[length2 - 1] = array2[array2.length - 1];
        }
        this.colorSpace = colorSpace;
        this.cycleMethod = cycleMethod;
        this.gradientTransform = new AffineTransform(affineTransform);
        boolean b3 = true;
        for (int j = 0; j < array2.length; ++j) {
            b3 = (b3 && array2[j].getAlpha() == 255);
        }
        this.transparency = (b3 ? 1 : 3);
    }
    
    public final float[] getFractions() {
        return Arrays.copyOf(this.fractions, this.fractions.length);
    }
    
    public final Color[] getColors() {
        return Arrays.copyOf(this.colors, this.colors.length);
    }
    
    public final CycleMethod getCycleMethod() {
        return this.cycleMethod;
    }
    
    public final ColorSpaceType getColorSpace() {
        return this.colorSpace;
    }
    
    public final AffineTransform getTransform() {
        return new AffineTransform(this.gradientTransform);
    }
    
    @Override
    public final int getTransparency() {
        return this.transparency;
    }
    
    public enum CycleMethod
    {
        NO_CYCLE, 
        REFLECT, 
        REPEAT;
    }
    
    public enum ColorSpaceType
    {
        SRGB, 
        LINEAR_RGB;
    }
}
