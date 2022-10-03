package org.apache.lucene.util;

public class SloppyMath
{
    public static final double TO_RADIANS = 0.017453292519943295;
    public static final double TO_DEGREES = 57.29577951308232;
    private static final double ONE_DIV_F2 = 0.5;
    private static final double ONE_DIV_F3 = 0.16666666666666666;
    private static final double ONE_DIV_F4 = 0.041666666666666664;
    public static final double PIO2 = 1.5707963267948966;
    private static final double PIO2_HI;
    private static final double PIO2_LO;
    private static final double TWOPI_HI;
    private static final double TWOPI_LO;
    private static final int SIN_COS_TABS_SIZE = 2049;
    private static final double SIN_COS_DELTA_HI;
    private static final double SIN_COS_DELTA_LO;
    private static final double SIN_COS_INDEXER;
    private static final double[] sinTab;
    private static final double[] cosTab;
    static final double SIN_COS_MAX_VALUE_FOR_INT_MODULO;
    private static final double ASIN_MAX_VALUE_FOR_TABS;
    private static final int ASIN_TABS_SIZE = 8193;
    private static final double ASIN_DELTA;
    private static final double ASIN_INDEXER;
    private static final double[] asinTab;
    private static final double[] asinDer1DivF1Tab;
    private static final double[] asinDer2DivF2Tab;
    private static final double[] asinDer3DivF3Tab;
    private static final double[] asinDer4DivF4Tab;
    private static final double ASIN_PIO2_HI;
    private static final double ASIN_PIO2_LO;
    private static final double ASIN_PS0;
    private static final double ASIN_PS1;
    private static final double ASIN_PS2;
    private static final double ASIN_PS3;
    private static final double ASIN_PS4;
    private static final double ASIN_PS5;
    private static final double ASIN_QS1;
    private static final double ASIN_QS2;
    private static final double ASIN_QS3;
    private static final double ASIN_QS4;
    private static final int RADIUS_TABS_SIZE = 1025;
    private static final double RADIUS_DELTA = 0.0015339807878856412;
    private static final double RADIUS_INDEXER = 651.8986469044033;
    private static final double[] earthDiameterPerLatitude;
    
    public static double haversin(final double lat1, final double lon1, final double lat2, final double lon2) {
        final double x1 = lat1 * 0.017453292519943295;
        final double x2 = lat2 * 0.017453292519943295;
        final double h1 = 1.0 - cos(x1 - x2);
        final double h2 = 1.0 - cos((lon1 - lon2) * 0.017453292519943295);
        final double h3 = (h1 + cos(x1) * cos(x2) * h2) / 2.0;
        final double avgLat = (x1 + x2) / 2.0;
        final double diameter = earthDiameter(avgLat);
        return diameter * asin(Math.min(1.0, Math.sqrt(h3)));
    }
    
    public static double cos(double a) {
        if (a < 0.0) {
            a = -a;
        }
        if (a > SloppyMath.SIN_COS_MAX_VALUE_FOR_INT_MODULO) {
            return Math.cos(a);
        }
        int index = (int)(a * SloppyMath.SIN_COS_INDEXER + 0.5);
        final double delta = a - index * SloppyMath.SIN_COS_DELTA_HI - index * SloppyMath.SIN_COS_DELTA_LO;
        index &= 0x7FF;
        final double indexCos = SloppyMath.cosTab[index];
        final double indexSin = SloppyMath.sinTab[index];
        return indexCos + delta * (-indexSin + delta * (-indexCos * 0.5 + delta * (indexSin * 0.16666666666666666 + delta * indexCos * 0.041666666666666664)));
    }
    
    public static double sin(final double a) {
        return cos(a - 1.5707963267948966);
    }
    
    public static double tan(final double a) {
        return sin(a) / cos(a);
    }
    
    public static double asin(double a) {
        boolean negateResult;
        if (a < 0.0) {
            a = -a;
            negateResult = true;
        }
        else {
            negateResult = false;
        }
        if (a <= SloppyMath.ASIN_MAX_VALUE_FOR_TABS) {
            final int index = (int)(a * SloppyMath.ASIN_INDEXER + 0.5);
            final double delta = a - index * SloppyMath.ASIN_DELTA;
            final double result = SloppyMath.asinTab[index] + delta * (SloppyMath.asinDer1DivF1Tab[index] + delta * (SloppyMath.asinDer2DivF2Tab[index] + delta * (SloppyMath.asinDer3DivF3Tab[index] + delta * SloppyMath.asinDer4DivF4Tab[index])));
            return negateResult ? (-result) : result;
        }
        if (a < 1.0) {
            final double t = (1.0 - a) * 0.5;
            final double p = t * (SloppyMath.ASIN_PS0 + t * (SloppyMath.ASIN_PS1 + t * (SloppyMath.ASIN_PS2 + t * (SloppyMath.ASIN_PS3 + t * (SloppyMath.ASIN_PS4 + t * SloppyMath.ASIN_PS5)))));
            final double q = 1.0 + t * (SloppyMath.ASIN_QS1 + t * (SloppyMath.ASIN_QS2 + t * (SloppyMath.ASIN_QS3 + t * SloppyMath.ASIN_QS4)));
            final double s = Math.sqrt(t);
            final double z = s + s * (p / q);
            final double result2 = SloppyMath.ASIN_PIO2_HI - (z + z - SloppyMath.ASIN_PIO2_LO);
            return negateResult ? (-result2) : result2;
        }
        if (a == 1.0) {
            return negateResult ? -1.5707963267948966 : 1.5707963267948966;
        }
        return Double.NaN;
    }
    
    public static double earthDiameter(final double latitude) {
        final int index = (int)(Math.abs(latitude) * 651.8986469044033 + 0.5) % SloppyMath.earthDiameterPerLatitude.length;
        return SloppyMath.earthDiameterPerLatitude[index];
    }
    
    static {
        PIO2_HI = Double.longBitsToDouble(4609753056924401664L);
        PIO2_LO = Double.longBitsToDouble(4454258360616903473L);
        TWOPI_HI = 4.0 * SloppyMath.PIO2_HI;
        TWOPI_LO = 4.0 * SloppyMath.PIO2_LO;
        SIN_COS_DELTA_HI = SloppyMath.TWOPI_HI / 2048.0;
        SIN_COS_DELTA_LO = SloppyMath.TWOPI_LO / 2048.0;
        SIN_COS_INDEXER = 1.0 / (SloppyMath.SIN_COS_DELTA_HI + SloppyMath.SIN_COS_DELTA_LO);
        sinTab = new double[2049];
        cosTab = new double[2049];
        SIN_COS_MAX_VALUE_FOR_INT_MODULO = 4194303.0 / SloppyMath.SIN_COS_INDEXER * 0.99;
        ASIN_MAX_VALUE_FOR_TABS = StrictMath.sin(Math.toRadians(73.0));
        ASIN_DELTA = SloppyMath.ASIN_MAX_VALUE_FOR_TABS / 8192.0;
        ASIN_INDEXER = 1.0 / SloppyMath.ASIN_DELTA;
        asinTab = new double[8193];
        asinDer1DivF1Tab = new double[8193];
        asinDer2DivF2Tab = new double[8193];
        asinDer3DivF3Tab = new double[8193];
        asinDer4DivF4Tab = new double[8193];
        ASIN_PIO2_HI = Double.longBitsToDouble(4609753056924675352L);
        ASIN_PIO2_LO = Double.longBitsToDouble(4364452196894661639L);
        ASIN_PS0 = Double.longBitsToDouble(4595172819793696085L);
        ASIN_PS1 = Double.longBitsToDouble(-4623835544539140227L);
        ASIN_PS2 = Double.longBitsToDouble(4596417465768494165L);
        ASIN_PS3 = Double.longBitsToDouble(-4637438604930937029L);
        ASIN_PS4 = Double.longBitsToDouble(4560439845004096136L);
        ASIN_PS5 = Double.longBitsToDouble(4540259411154564873L);
        ASIN_QS1 = Double.longBitsToDouble(-4610777653840302773L);
        ASIN_QS2 = Double.longBitsToDouble(4611733184086379208L);
        ASIN_QS3 = Double.longBitsToDouble(-4618997306433404583L);
        ASIN_QS4 = Double.longBitsToDouble(4590215604441354882L);
        earthDiameterPerLatitude = new double[1025];
        final int SIN_COS_PI_INDEX = 1024;
        final int SIN_COS_PI_MUL_2_INDEX = 2048;
        final int SIN_COS_PI_MUL_0_5_INDEX = 512;
        final int SIN_COS_PI_MUL_1_5_INDEX = 1536;
        for (int i = 0; i < 2049; ++i) {
            final double angle = i * SloppyMath.SIN_COS_DELTA_HI + i * SloppyMath.SIN_COS_DELTA_LO;
            double sinAngle = StrictMath.sin(angle);
            double cosAngle = StrictMath.cos(angle);
            if (i == 1024) {
                sinAngle = 0.0;
            }
            else if (i == 2048) {
                sinAngle = 0.0;
            }
            else if (i == 512) {
                cosAngle = 0.0;
            }
            else if (i == 1536) {
                cosAngle = 0.0;
            }
            SloppyMath.sinTab[i] = sinAngle;
            SloppyMath.cosTab[i] = cosAngle;
        }
        for (int i = 0; i < 8193; ++i) {
            final double x = i * SloppyMath.ASIN_DELTA;
            SloppyMath.asinTab[i] = StrictMath.asin(x);
            final double oneMinusXSqInv = 1.0 / (1.0 - x * x);
            final double oneMinusXSqInv0_5 = StrictMath.sqrt(oneMinusXSqInv);
            final double oneMinusXSqInv1_5 = oneMinusXSqInv0_5 * oneMinusXSqInv;
            final double oneMinusXSqInv2_5 = oneMinusXSqInv1_5 * oneMinusXSqInv;
            final double oneMinusXSqInv3_5 = oneMinusXSqInv2_5 * oneMinusXSqInv;
            SloppyMath.asinDer1DivF1Tab[i] = oneMinusXSqInv0_5;
            SloppyMath.asinDer2DivF2Tab[i] = x * oneMinusXSqInv1_5 * 0.5;
            SloppyMath.asinDer3DivF3Tab[i] = (1.0 + 2.0 * x * x) * oneMinusXSqInv2_5 * 0.16666666666666666;
            SloppyMath.asinDer4DivF4Tab[i] = (5.0 + 2.0 * x * (2.0 + x * (5.0 - 2.0 * x))) * oneMinusXSqInv3_5 * 0.041666666666666664;
        }
        final double a = 6378137.0;
        final double b = 6356752.3142;
        final double a2 = 4.0680631590769E13;
        final double b2 = 4.0408299984087055E13;
        SloppyMath.earthDiameterPerLatitude[0] = 12756.274;
        SloppyMath.earthDiameterPerLatitude[1024] = 12713.5046284;
        for (int j = 1; j < 1024; ++j) {
            final double lat = 3.141592653589793 * j / 2049.0;
            final double one = StrictMath.pow(4.0680631590769E13 * StrictMath.cos(lat), 2.0);
            final double two = StrictMath.pow(4.0408299984087055E13 * StrictMath.sin(lat), 2.0);
            final double three = StrictMath.pow(6378137.0 * StrictMath.cos(lat), 2.0);
            final double four = StrictMath.pow(6356752.3142 * StrictMath.sin(lat), 2.0);
            final double radius = StrictMath.sqrt((one + two) / (three + four));
            SloppyMath.earthDiameterPerLatitude[j] = 2.0 * radius / 1000.0;
        }
    }
}
