package java.awt.color;

import sun.java2d.cmm.CMSManager;
import java.io.Serializable;

public abstract class ColorSpace implements Serializable
{
    static final long serialVersionUID = -409452704308689724L;
    private int type;
    private int numComponents;
    private transient String[] compName;
    private static ColorSpace sRGBspace;
    private static ColorSpace XYZspace;
    private static ColorSpace PYCCspace;
    private static ColorSpace GRAYspace;
    private static ColorSpace LINEAR_RGBspace;
    public static final int TYPE_XYZ = 0;
    public static final int TYPE_Lab = 1;
    public static final int TYPE_Luv = 2;
    public static final int TYPE_YCbCr = 3;
    public static final int TYPE_Yxy = 4;
    public static final int TYPE_RGB = 5;
    public static final int TYPE_GRAY = 6;
    public static final int TYPE_HSV = 7;
    public static final int TYPE_HLS = 8;
    public static final int TYPE_CMYK = 9;
    public static final int TYPE_CMY = 11;
    public static final int TYPE_2CLR = 12;
    public static final int TYPE_3CLR = 13;
    public static final int TYPE_4CLR = 14;
    public static final int TYPE_5CLR = 15;
    public static final int TYPE_6CLR = 16;
    public static final int TYPE_7CLR = 17;
    public static final int TYPE_8CLR = 18;
    public static final int TYPE_9CLR = 19;
    public static final int TYPE_ACLR = 20;
    public static final int TYPE_BCLR = 21;
    public static final int TYPE_CCLR = 22;
    public static final int TYPE_DCLR = 23;
    public static final int TYPE_ECLR = 24;
    public static final int TYPE_FCLR = 25;
    public static final int CS_sRGB = 1000;
    public static final int CS_LINEAR_RGB = 1004;
    public static final int CS_CIEXYZ = 1001;
    public static final int CS_PYCC = 1002;
    public static final int CS_GRAY = 1003;
    
    protected ColorSpace(final int type, final int numComponents) {
        this.compName = null;
        this.type = type;
        this.numComponents = numComponents;
    }
    
    public static ColorSpace getInstance(final int n) {
        ColorSpace colorSpace = null;
        switch (n) {
            case 1000: {
                synchronized (ColorSpace.class) {
                    if (ColorSpace.sRGBspace == null) {
                        ColorSpace.sRGBspace = new ICC_ColorSpace(ICC_Profile.getInstance(1000));
                    }
                    colorSpace = ColorSpace.sRGBspace;
                }
                break;
            }
            case 1001: {
                synchronized (ColorSpace.class) {
                    if (ColorSpace.XYZspace == null) {
                        ColorSpace.XYZspace = new ICC_ColorSpace(ICC_Profile.getInstance(1001));
                    }
                    colorSpace = ColorSpace.XYZspace;
                }
                break;
            }
            case 1002: {
                synchronized (ColorSpace.class) {
                    if (ColorSpace.PYCCspace == null) {
                        ColorSpace.PYCCspace = new ICC_ColorSpace(ICC_Profile.getInstance(1002));
                    }
                    colorSpace = ColorSpace.PYCCspace;
                }
                break;
            }
            case 1003: {
                synchronized (ColorSpace.class) {
                    if (ColorSpace.GRAYspace == null) {
                        ColorSpace.GRAYspace = new ICC_ColorSpace(ICC_Profile.getInstance(1003));
                        CMSManager.GRAYspace = ColorSpace.GRAYspace;
                    }
                    colorSpace = ColorSpace.GRAYspace;
                }
                break;
            }
            case 1004: {
                synchronized (ColorSpace.class) {
                    if (ColorSpace.LINEAR_RGBspace == null) {
                        ColorSpace.LINEAR_RGBspace = new ICC_ColorSpace(ICC_Profile.getInstance(1004));
                        CMSManager.LINEAR_RGBspace = ColorSpace.LINEAR_RGBspace;
                    }
                    colorSpace = ColorSpace.LINEAR_RGBspace;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown color space");
            }
        }
        return colorSpace;
    }
    
    public boolean isCS_sRGB() {
        return this == ColorSpace.sRGBspace;
    }
    
    public abstract float[] toRGB(final float[] p0);
    
    public abstract float[] fromRGB(final float[] p0);
    
    public abstract float[] toCIEXYZ(final float[] p0);
    
    public abstract float[] fromCIEXYZ(final float[] p0);
    
    public int getType() {
        return this.type;
    }
    
    public int getNumComponents() {
        return this.numComponents;
    }
    
    public String getName(final int n) {
        if (n < 0 || n > this.numComponents - 1) {
            throw new IllegalArgumentException("Component index out of range: " + n);
        }
        if (this.compName == null) {
            switch (this.type) {
                case 0: {
                    this.compName = new String[] { "X", "Y", "Z" };
                    break;
                }
                case 1: {
                    this.compName = new String[] { "L", "a", "b" };
                    break;
                }
                case 2: {
                    this.compName = new String[] { "L", "u", "v" };
                    break;
                }
                case 3: {
                    this.compName = new String[] { "Y", "Cb", "Cr" };
                    break;
                }
                case 4: {
                    this.compName = new String[] { "Y", "x", "y" };
                    break;
                }
                case 5: {
                    this.compName = new String[] { "Red", "Green", "Blue" };
                    break;
                }
                case 6: {
                    this.compName = new String[] { "Gray" };
                    break;
                }
                case 7: {
                    this.compName = new String[] { "Hue", "Saturation", "Value" };
                    break;
                }
                case 8: {
                    this.compName = new String[] { "Hue", "Lightness", "Saturation" };
                    break;
                }
                case 9: {
                    this.compName = new String[] { "Cyan", "Magenta", "Yellow", "Black" };
                    break;
                }
                case 11: {
                    this.compName = new String[] { "Cyan", "Magenta", "Yellow" };
                    break;
                }
                default: {
                    final String[] compName = new String[this.numComponents];
                    for (int i = 0; i < compName.length; ++i) {
                        compName[i] = "Unnamed color component(" + i + ")";
                    }
                    this.compName = compName;
                    break;
                }
            }
        }
        return this.compName[n];
    }
    
    public float getMinValue(final int n) {
        if (n < 0 || n > this.numComponents - 1) {
            throw new IllegalArgumentException("Component index out of range: " + n);
        }
        return 0.0f;
    }
    
    public float getMaxValue(final int n) {
        if (n < 0 || n > this.numComponents - 1) {
            throw new IllegalArgumentException("Component index out of range: " + n);
        }
        return 1.0f;
    }
    
    static boolean isCS_CIEXYZ(final ColorSpace colorSpace) {
        return colorSpace == ColorSpace.XYZspace;
    }
}
