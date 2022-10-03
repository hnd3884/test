package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.beans.ConstructorProperties;
import java.awt.color.ColorSpace;
import java.io.Serializable;

public class Color implements Paint, Serializable
{
    public static final Color white;
    public static final Color WHITE;
    public static final Color lightGray;
    public static final Color LIGHT_GRAY;
    public static final Color gray;
    public static final Color GRAY;
    public static final Color darkGray;
    public static final Color DARK_GRAY;
    public static final Color black;
    public static final Color BLACK;
    public static final Color red;
    public static final Color RED;
    public static final Color pink;
    public static final Color PINK;
    public static final Color orange;
    public static final Color ORANGE;
    public static final Color yellow;
    public static final Color YELLOW;
    public static final Color green;
    public static final Color GREEN;
    public static final Color magenta;
    public static final Color MAGENTA;
    public static final Color cyan;
    public static final Color CYAN;
    public static final Color blue;
    public static final Color BLUE;
    int value;
    private float[] frgbvalue;
    private float[] fvalue;
    private float falpha;
    private ColorSpace cs;
    private static final long serialVersionUID = 118526816881161077L;
    private static final double FACTOR = 0.7;
    
    private static native void initIDs();
    
    private static void testColorValueRange(final int n, final int n2, final int n3, final int n4) {
        int n5 = 0;
        String s = "";
        if (n4 < 0 || n4 > 255) {
            n5 = 1;
            s += " Alpha";
        }
        if (n < 0 || n > 255) {
            n5 = 1;
            s += " Red";
        }
        if (n2 < 0 || n2 > 255) {
            n5 = 1;
            s += " Green";
        }
        if (n3 < 0 || n3 > 255) {
            n5 = 1;
            s += " Blue";
        }
        if (n5 == 1) {
            throw new IllegalArgumentException("Color parameter outside of expected range:" + s);
        }
    }
    
    private static void testColorValueRange(final float n, final float n2, final float n3, final float n4) {
        int n5 = 0;
        String s = "";
        if (n4 < 0.0 || n4 > 1.0) {
            n5 = 1;
            s += " Alpha";
        }
        if (n < 0.0 || n > 1.0) {
            n5 = 1;
            s += " Red";
        }
        if (n2 < 0.0 || n2 > 1.0) {
            n5 = 1;
            s += " Green";
        }
        if (n3 < 0.0 || n3 > 1.0) {
            n5 = 1;
            s += " Blue";
        }
        if (n5 == 1) {
            throw new IllegalArgumentException("Color parameter outside of expected range:" + s);
        }
    }
    
    public Color(final int n, final int n2, final int n3) {
        this(n, n2, n3, 255);
    }
    
    @ConstructorProperties({ "red", "green", "blue", "alpha" })
    public Color(final int n, final int n2, final int n3, final int n4) {
        this.frgbvalue = null;
        this.fvalue = null;
        this.falpha = 0.0f;
        this.cs = null;
        this.value = ((n4 & 0xFF) << 24 | (n & 0xFF) << 16 | (n2 & 0xFF) << 8 | (n3 & 0xFF) << 0);
        testColorValueRange(n, n2, n3, n4);
    }
    
    public Color(final int n) {
        this.frgbvalue = null;
        this.fvalue = null;
        this.falpha = 0.0f;
        this.cs = null;
        this.value = (0xFF000000 | n);
    }
    
    public Color(final int value, final boolean b) {
        this.frgbvalue = null;
        this.fvalue = null;
        this.falpha = 0.0f;
        this.cs = null;
        if (b) {
            this.value = value;
        }
        else {
            this.value = (0xFF000000 | value);
        }
    }
    
    public Color(final float n, final float n2, final float n3) {
        this((int)(n * 255.0f + 0.5), (int)(n2 * 255.0f + 0.5), (int)(n3 * 255.0f + 0.5));
        testColorValueRange(n, n2, n3, 1.0f);
        (this.frgbvalue = new float[3])[0] = n;
        this.frgbvalue[1] = n2;
        this.frgbvalue[2] = n3;
        this.falpha = 1.0f;
        this.fvalue = this.frgbvalue;
    }
    
    public Color(final float n, final float n2, final float n3, final float falpha) {
        this((int)(n * 255.0f + 0.5), (int)(n2 * 255.0f + 0.5), (int)(n3 * 255.0f + 0.5), (int)(falpha * 255.0f + 0.5));
        (this.frgbvalue = new float[3])[0] = n;
        this.frgbvalue[1] = n2;
        this.frgbvalue[2] = n3;
        this.falpha = falpha;
        this.fvalue = this.frgbvalue;
    }
    
    public Color(final ColorSpace cs, final float[] array, final float falpha) {
        this.frgbvalue = null;
        this.fvalue = null;
        this.falpha = 0.0f;
        this.cs = null;
        boolean b = false;
        String s = "";
        final int numComponents = cs.getNumComponents();
        this.fvalue = new float[numComponents];
        for (int i = 0; i < numComponents; ++i) {
            if (array[i] < 0.0 || array[i] > 1.0) {
                b = true;
                s = s + "Component " + i + " ";
            }
            else {
                this.fvalue[i] = array[i];
            }
        }
        if (falpha < 0.0 || falpha > 1.0) {
            b = true;
            s += "Alpha";
        }
        else {
            this.falpha = falpha;
        }
        if (b) {
            throw new IllegalArgumentException("Color parameter outside of expected range: " + s);
        }
        this.frgbvalue = cs.toRGB(this.fvalue);
        this.cs = cs;
        this.value = (((int)(this.falpha * 255.0f) & 0xFF) << 24 | ((int)(this.frgbvalue[0] * 255.0f) & 0xFF) << 16 | ((int)(this.frgbvalue[1] * 255.0f) & 0xFF) << 8 | ((int)(this.frgbvalue[2] * 255.0f) & 0xFF) << 0);
    }
    
    public int getRed() {
        return this.getRGB() >> 16 & 0xFF;
    }
    
    public int getGreen() {
        return this.getRGB() >> 8 & 0xFF;
    }
    
    public int getBlue() {
        return this.getRGB() >> 0 & 0xFF;
    }
    
    public int getAlpha() {
        return this.getRGB() >> 24 & 0xFF;
    }
    
    public int getRGB() {
        return this.value;
    }
    
    public Color brighter() {
        int red = this.getRed();
        int green = this.getGreen();
        int blue = this.getBlue();
        final int alpha = this.getAlpha();
        final int n = 3;
        if (red == 0 && green == 0 && blue == 0) {
            return new Color(n, n, n, alpha);
        }
        if (red > 0 && red < n) {
            red = n;
        }
        if (green > 0 && green < n) {
            green = n;
        }
        if (blue > 0 && blue < n) {
            blue = n;
        }
        return new Color(Math.min((int)(red / 0.7), 255), Math.min((int)(green / 0.7), 255), Math.min((int)(blue / 0.7), 255), alpha);
    }
    
    public Color darker() {
        return new Color(Math.max((int)(this.getRed() * 0.7), 0), Math.max((int)(this.getGreen() * 0.7), 0), Math.max((int)(this.getBlue() * 0.7), 0), this.getAlpha());
    }
    
    @Override
    public int hashCode() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof Color && ((Color)o).getRGB() == this.getRGB();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[r=" + this.getRed() + ",g=" + this.getGreen() + ",b=" + this.getBlue() + "]";
    }
    
    public static Color decode(final String s) throws NumberFormatException {
        final int intValue = Integer.decode(s);
        return new Color(intValue >> 16 & 0xFF, intValue >> 8 & 0xFF, intValue & 0xFF);
    }
    
    public static Color getColor(final String s) {
        return getColor(s, null);
    }
    
    public static Color getColor(final String s, final Color color) {
        final Integer integer = Integer.getInteger(s);
        if (integer == null) {
            return color;
        }
        final int intValue = integer;
        return new Color(intValue >> 16 & 0xFF, intValue >> 8 & 0xFF, intValue & 0xFF);
    }
    
    public static Color getColor(final String s, final int n) {
        final Integer integer = Integer.getInteger(s);
        final int n2 = (integer != null) ? integer : n;
        return new Color(n2 >> 16 & 0xFF, n2 >> 8 & 0xFF, n2 >> 0 & 0xFF);
    }
    
    public static int HSBtoRGB(final float n, final float n2, final float n3) {
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        if (n2 == 0.0f) {
            n5 = (n4 = (n6 = (int)(n3 * 255.0f + 0.5f)));
        }
        else {
            final float n7 = (n - (float)Math.floor(n)) * 6.0f;
            final float n8 = n7 - (float)Math.floor(n7);
            final float n9 = n3 * (1.0f - n2);
            final float n10 = n3 * (1.0f - n2 * n8);
            final float n11 = n3 * (1.0f - n2 * (1.0f - n8));
            switch ((int)n7) {
                case 0: {
                    n4 = (int)(n3 * 255.0f + 0.5f);
                    n5 = (int)(n11 * 255.0f + 0.5f);
                    n6 = (int)(n9 * 255.0f + 0.5f);
                    break;
                }
                case 1: {
                    n4 = (int)(n10 * 255.0f + 0.5f);
                    n5 = (int)(n3 * 255.0f + 0.5f);
                    n6 = (int)(n9 * 255.0f + 0.5f);
                    break;
                }
                case 2: {
                    n4 = (int)(n9 * 255.0f + 0.5f);
                    n5 = (int)(n3 * 255.0f + 0.5f);
                    n6 = (int)(n11 * 255.0f + 0.5f);
                    break;
                }
                case 3: {
                    n4 = (int)(n9 * 255.0f + 0.5f);
                    n5 = (int)(n10 * 255.0f + 0.5f);
                    n6 = (int)(n3 * 255.0f + 0.5f);
                    break;
                }
                case 4: {
                    n4 = (int)(n11 * 255.0f + 0.5f);
                    n5 = (int)(n9 * 255.0f + 0.5f);
                    n6 = (int)(n3 * 255.0f + 0.5f);
                    break;
                }
                case 5: {
                    n4 = (int)(n3 * 255.0f + 0.5f);
                    n5 = (int)(n9 * 255.0f + 0.5f);
                    n6 = (int)(n10 * 255.0f + 0.5f);
                    break;
                }
            }
        }
        return 0xFF000000 | n4 << 16 | n5 << 8 | n6 << 0;
    }
    
    public static float[] RGBtoHSB(final int n, final int n2, final int n3, float[] array) {
        if (array == null) {
            array = new float[3];
        }
        int n4 = (n > n2) ? n : n2;
        if (n3 > n4) {
            n4 = n3;
        }
        int n5 = (n < n2) ? n : n2;
        if (n3 < n5) {
            n5 = n3;
        }
        final float n6 = n4 / 255.0f;
        float n7;
        if (n4 != 0) {
            n7 = (n4 - n5) / (float)n4;
        }
        else {
            n7 = 0.0f;
        }
        float n8;
        if (n7 == 0.0f) {
            n8 = 0.0f;
        }
        else {
            final float n9 = (n4 - n) / (float)(n4 - n5);
            final float n10 = (n4 - n2) / (float)(n4 - n5);
            final float n11 = (n4 - n3) / (float)(n4 - n5);
            float n12;
            if (n == n4) {
                n12 = n11 - n10;
            }
            else if (n2 == n4) {
                n12 = 2.0f + n9 - n11;
            }
            else {
                n12 = 4.0f + n10 - n9;
            }
            n8 = n12 / 6.0f;
            if (n8 < 0.0f) {
                ++n8;
            }
        }
        array[0] = n8;
        array[1] = n7;
        array[2] = n6;
        return array;
    }
    
    public static Color getHSBColor(final float n, final float n2, final float n3) {
        return new Color(HSBtoRGB(n, n2, n3));
    }
    
    public float[] getRGBComponents(final float[] array) {
        float[] array2;
        if (array == null) {
            array2 = new float[4];
        }
        else {
            array2 = array;
        }
        if (this.frgbvalue == null) {
            array2[0] = this.getRed() / 255.0f;
            array2[1] = this.getGreen() / 255.0f;
            array2[2] = this.getBlue() / 255.0f;
            array2[3] = this.getAlpha() / 255.0f;
        }
        else {
            array2[0] = this.frgbvalue[0];
            array2[1] = this.frgbvalue[1];
            array2[2] = this.frgbvalue[2];
            array2[3] = this.falpha;
        }
        return array2;
    }
    
    public float[] getRGBColorComponents(final float[] array) {
        float[] array2;
        if (array == null) {
            array2 = new float[3];
        }
        else {
            array2 = array;
        }
        if (this.frgbvalue == null) {
            array2[0] = this.getRed() / 255.0f;
            array2[1] = this.getGreen() / 255.0f;
            array2[2] = this.getBlue() / 255.0f;
        }
        else {
            array2[0] = this.frgbvalue[0];
            array2[1] = this.frgbvalue[1];
            array2[2] = this.frgbvalue[2];
        }
        return array2;
    }
    
    public float[] getComponents(final float[] array) {
        if (this.fvalue == null) {
            return this.getRGBComponents(array);
        }
        final int length = this.fvalue.length;
        float[] array2;
        if (array == null) {
            array2 = new float[length + 1];
        }
        else {
            array2 = array;
        }
        for (int i = 0; i < length; ++i) {
            array2[i] = this.fvalue[i];
        }
        array2[length] = this.falpha;
        return array2;
    }
    
    public float[] getColorComponents(final float[] array) {
        if (this.fvalue == null) {
            return this.getRGBColorComponents(array);
        }
        final int length = this.fvalue.length;
        float[] array2;
        if (array == null) {
            array2 = new float[length];
        }
        else {
            array2 = array;
        }
        for (int i = 0; i < length; ++i) {
            array2[i] = this.fvalue[i];
        }
        return array2;
    }
    
    public float[] getComponents(final ColorSpace colorSpace, float[] array) {
        if (this.cs == null) {
            this.cs = ColorSpace.getInstance(1000);
        }
        float[] fvalue;
        if (this.fvalue == null) {
            fvalue = new float[] { this.getRed() / 255.0f, this.getGreen() / 255.0f, this.getBlue() / 255.0f };
        }
        else {
            fvalue = this.fvalue;
        }
        final float[] fromCIEXYZ = colorSpace.fromCIEXYZ(this.cs.toCIEXYZ(fvalue));
        if (array == null) {
            array = new float[fromCIEXYZ.length + 1];
        }
        for (int i = 0; i < fromCIEXYZ.length; ++i) {
            array[i] = fromCIEXYZ[i];
        }
        if (this.fvalue == null) {
            array[fromCIEXYZ.length] = this.getAlpha() / 255.0f;
        }
        else {
            array[fromCIEXYZ.length] = this.falpha;
        }
        return array;
    }
    
    public float[] getColorComponents(final ColorSpace colorSpace, final float[] array) {
        if (this.cs == null) {
            this.cs = ColorSpace.getInstance(1000);
        }
        float[] fvalue;
        if (this.fvalue == null) {
            fvalue = new float[] { this.getRed() / 255.0f, this.getGreen() / 255.0f, this.getBlue() / 255.0f };
        }
        else {
            fvalue = this.fvalue;
        }
        final float[] fromCIEXYZ = colorSpace.fromCIEXYZ(this.cs.toCIEXYZ(fvalue));
        if (array == null) {
            return fromCIEXYZ;
        }
        for (int i = 0; i < fromCIEXYZ.length; ++i) {
            array[i] = fromCIEXYZ[i];
        }
        return array;
    }
    
    public ColorSpace getColorSpace() {
        if (this.cs == null) {
            this.cs = ColorSpace.getInstance(1000);
        }
        return this.cs;
    }
    
    @Override
    public synchronized PaintContext createContext(final ColorModel colorModel, final Rectangle rectangle, final Rectangle2D rectangle2D, final AffineTransform affineTransform, final RenderingHints renderingHints) {
        return new ColorPaintContext(this.getRGB(), colorModel);
    }
    
    @Override
    public int getTransparency() {
        final int alpha = this.getAlpha();
        if (alpha == 255) {
            return 1;
        }
        if (alpha == 0) {
            return 2;
        }
        return 3;
    }
    
    static {
        white = new Color(255, 255, 255);
        WHITE = Color.white;
        lightGray = new Color(192, 192, 192);
        LIGHT_GRAY = Color.lightGray;
        gray = new Color(128, 128, 128);
        GRAY = Color.gray;
        darkGray = new Color(64, 64, 64);
        DARK_GRAY = Color.darkGray;
        black = new Color(0, 0, 0);
        BLACK = Color.black;
        red = new Color(255, 0, 0);
        RED = Color.red;
        pink = new Color(255, 175, 175);
        PINK = Color.pink;
        orange = new Color(255, 200, 0);
        ORANGE = Color.orange;
        yellow = new Color(255, 255, 0);
        YELLOW = Color.yellow;
        green = new Color(0, 255, 0);
        GREEN = Color.green;
        magenta = new Color(255, 0, 255);
        MAGENTA = Color.magenta;
        cyan = new Color(0, 255, 255);
        CYAN = Color.cyan;
        blue = new Color(0, 0, 255);
        BLUE = Color.blue;
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }
}
