package sun.java2d.marlin;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public final class MarlinProperties
{
    private MarlinProperties() {
    }
    
    public static boolean isUseThreadLocal() {
        return getBoolean("sun.java2d.renderer.useThreadLocal", "true");
    }
    
    public static int getInitialEdges() {
        return align(getInteger("sun.java2d.renderer.edges", 4096, 64, 65536), 64);
    }
    
    public static int getInitialPixelWidth() {
        return align(getInteger("sun.java2d.renderer.pixelWidth", 4096, 64, 32768), 64);
    }
    
    public static int getInitialPixelHeight() {
        return align(getInteger("sun.java2d.renderer.pixelHeight", 2176, 64, 32768), 64);
    }
    
    public static int getSubPixel_Log2_X() {
        return getInteger("sun.java2d.renderer.subPixel_log2_X", 8, 0, 8);
    }
    
    public static int getSubPixel_Log2_Y() {
        return getInteger("sun.java2d.renderer.subPixel_log2_Y", 3, 0, 8);
    }
    
    public static int getTileSize_Log2() {
        return getInteger("sun.java2d.renderer.tileSize_log2", 5, 3, 10);
    }
    
    public static int getTileWidth_Log2() {
        return getInteger("sun.java2d.renderer.tileWidth_log2", 5, 3, 10);
    }
    
    public static int getBlockSize_Log2() {
        return getInteger("sun.java2d.renderer.blockSize_log2", 5, 3, 8);
    }
    
    public static boolean isForceRLE() {
        return getBoolean("sun.java2d.renderer.forceRLE", "false");
    }
    
    public static boolean isForceNoRLE() {
        return getBoolean("sun.java2d.renderer.forceNoRLE", "false");
    }
    
    public static boolean isUseTileFlags() {
        return getBoolean("sun.java2d.renderer.useTileFlags", "true");
    }
    
    public static boolean isUseTileFlagsWithHeuristics() {
        return isUseTileFlags() && getBoolean("sun.java2d.renderer.useTileFlags.useHeuristics", "true");
    }
    
    public static int getRLEMinWidth() {
        return getInteger("sun.java2d.renderer.rleMinWidth", 64, 0, Integer.MAX_VALUE);
    }
    
    public static boolean isUseSimplifier() {
        return getBoolean("sun.java2d.renderer.useSimplifier", "false");
    }
    
    public static boolean isUsePathSimplifier() {
        return getBoolean("sun.java2d.renderer.usePathSimplifier", "false");
    }
    
    public static float getPathSimplifierPixelTolerance() {
        return getFloat("sun.java2d.renderer.pathSimplifier.pixTol", 1.0f / MarlinConst.MIN_SUBPIXELS, 0.001f, 10.0f);
    }
    
    public static boolean isDoClip() {
        return getBoolean("sun.java2d.renderer.clip", "true");
    }
    
    public static boolean isDoClipRuntimeFlag() {
        return getBoolean("sun.java2d.renderer.clip.runtime.enable", "false");
    }
    
    public static boolean isDoClipAtRuntime() {
        return getBoolean("sun.java2d.renderer.clip.runtime", "true");
    }
    
    public static boolean isDoClipSubdivider() {
        return getBoolean("sun.java2d.renderer.clip.subdivider", "true");
    }
    
    public static float getSubdividerMinLength() {
        return getFloat("sun.java2d.renderer.clip.subdivider.minLength", 100.0f, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }
    
    public static boolean isDoStats() {
        return getBoolean("sun.java2d.renderer.doStats", "false");
    }
    
    public static boolean isDoMonitors() {
        return getBoolean("sun.java2d.renderer.doMonitors", "false");
    }
    
    public static boolean isDoChecks() {
        return getBoolean("sun.java2d.renderer.doChecks", "false");
    }
    
    public static boolean isLoggingEnabled() {
        return getBoolean("sun.java2d.renderer.log", "false");
    }
    
    public static boolean isUseLogger() {
        return getBoolean("sun.java2d.renderer.useLogger", "false");
    }
    
    public static boolean isLogCreateContext() {
        return getBoolean("sun.java2d.renderer.logCreateContext", "false");
    }
    
    public static boolean isLogUnsafeMalloc() {
        return getBoolean("sun.java2d.renderer.logUnsafeMalloc", "false");
    }
    
    public static float getCurveLengthError() {
        return getFloat("sun.java2d.renderer.curve_len_err", 0.01f, 1.0E-6f, 1.0f);
    }
    
    public static float getCubicDecD2() {
        return getFloat("sun.java2d.renderer.cubic_dec_d2", 1.0f, 1.0E-5f, 4.0f);
    }
    
    public static float getCubicIncD1() {
        return getFloat("sun.java2d.renderer.cubic_inc_d1", 0.2f, 1.0E-6f, 1.0f);
    }
    
    public static float getQuadDecD2() {
        return getFloat("sun.java2d.renderer.quad_dec_d2", 0.5f, 1.0E-5f, 4.0f);
    }
    
    static boolean getBoolean(final String s, final String s2) {
        return Boolean.valueOf(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(s, s2)));
    }
    
    static int getInteger(final String s, final int n, final int n2, final int n3) {
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(s));
        int intValue = n;
        if (s2 != null) {
            try {
                intValue = Integer.decode(s2);
            }
            catch (final NumberFormatException ex) {
                MarlinUtils.logInfo("Invalid integer value for " + s + " = " + s2);
            }
        }
        if (intValue < n2 || intValue > n3) {
            MarlinUtils.logInfo("Invalid value for " + s + " = " + intValue + "; expected value in range[" + n2 + ", " + n3 + "] !");
            intValue = n;
        }
        return intValue;
    }
    
    static int align(final int n, final int n2) {
        return FloatMath.ceil_int(n / (float)n2) * n2;
    }
    
    public static double getDouble(final String s, final double n, final double n2, final double n3) {
        double double1 = n;
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(s));
        if (s2 != null) {
            try {
                double1 = Double.parseDouble(s2);
            }
            catch (final NumberFormatException ex) {
                MarlinUtils.logInfo("Invalid value for " + s + " = " + s2 + " !");
            }
        }
        if (double1 < n2 || double1 > n3) {
            MarlinUtils.logInfo("Invalid value for " + s + " = " + double1 + "; expect value in range[" + n2 + ", " + n3 + "] !");
            double1 = n;
        }
        return double1;
    }
    
    public static float getFloat(final String s, final float n, final float n2, final float n3) {
        return (float)getDouble(s, n, n2, n3);
    }
}
