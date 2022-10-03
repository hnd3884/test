package sun.java2d.windows;

import sun.awt.windows.WToolkit;
import java.security.AccessController;
import sun.java2d.opengl.WGLGraphicsConfig;
import java.security.PrivilegedAction;

public class WindowsFlags
{
    private static boolean gdiBlitEnabled;
    private static boolean d3dEnabled;
    private static boolean d3dVerbose;
    private static boolean d3dSet;
    private static boolean d3dOnScreenEnabled;
    private static boolean oglEnabled;
    private static boolean oglVerbose;
    private static boolean offscreenSharingEnabled;
    private static boolean accelReset;
    private static boolean checkRegistry;
    private static boolean disableRegistry;
    private static boolean magPresent;
    private static boolean setHighDPIAware;
    private static String javaVersion;
    
    private static native boolean initNativeFlags();
    
    public static void initFlags() {
    }
    
    private static boolean getBooleanProp(final String s, final boolean b) {
        final String property = System.getProperty(s);
        boolean b2 = b;
        if (property != null) {
            if (property.equals("true") || property.equals("t") || property.equals("True") || property.equals("T") || property.equals("")) {
                b2 = true;
            }
            else if (property.equals("false") || property.equals("f") || property.equals("False") || property.equals("F")) {
                b2 = false;
            }
        }
        return b2;
    }
    
    private static boolean isBooleanPropTrueVerbose(final String s) {
        final String property = System.getProperty(s);
        return property != null && (property.equals("True") || property.equals("T"));
    }
    
    private static int getIntProp(final String s, final int n) {
        final String property = System.getProperty(s);
        int int1 = n;
        if (property != null) {
            try {
                int1 = Integer.parseInt(property);
            }
            catch (final NumberFormatException ex) {}
        }
        return int1;
    }
    
    private static boolean getPropertySet(final String s) {
        return System.getProperty(s) != null;
    }
    
    private static void initJavaFlags() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                WindowsFlags.magPresent = getBooleanProp("javax.accessibility.screen_magnifier_present", false);
                final boolean b = !getBooleanProp("sun.java2d.noddraw", WindowsFlags.magPresent);
                final boolean access$100 = getBooleanProp("sun.java2d.ddoffscreen", b);
                WindowsFlags.d3dEnabled = getBooleanProp("sun.java2d.d3d", b && access$100);
                WindowsFlags.d3dOnScreenEnabled = getBooleanProp("sun.java2d.d3d.onscreen", WindowsFlags.d3dEnabled);
                WindowsFlags.oglEnabled = getBooleanProp("sun.java2d.opengl", false);
                if (WindowsFlags.oglEnabled) {
                    WindowsFlags.oglVerbose = isBooleanPropTrueVerbose("sun.java2d.opengl");
                    if (WGLGraphicsConfig.isWGLAvailable()) {
                        WindowsFlags.d3dEnabled = false;
                    }
                    else {
                        if (WindowsFlags.oglVerbose) {
                            System.out.println("Could not enable OpenGL pipeline (WGL not available)");
                        }
                        WindowsFlags.oglEnabled = false;
                    }
                }
                WindowsFlags.gdiBlitEnabled = getBooleanProp("sun.java2d.gdiBlit", true);
                WindowsFlags.d3dSet = getPropertySet("sun.java2d.d3d");
                if (WindowsFlags.d3dSet) {
                    WindowsFlags.d3dVerbose = isBooleanPropTrueVerbose("sun.java2d.d3d");
                }
                WindowsFlags.offscreenSharingEnabled = getBooleanProp("sun.java2d.offscreenSharing", false);
                WindowsFlags.accelReset = getBooleanProp("sun.java2d.accelReset", false);
                WindowsFlags.checkRegistry = getBooleanProp("sun.java2d.checkRegistry", false);
                WindowsFlags.disableRegistry = getBooleanProp("sun.java2d.disableRegistry", false);
                WindowsFlags.javaVersion = System.getProperty("java.version");
                if (WindowsFlags.javaVersion == null) {
                    WindowsFlags.javaVersion = "default";
                }
                else {
                    final int index = WindowsFlags.javaVersion.indexOf(45);
                    if (index >= 0) {
                        WindowsFlags.javaVersion = WindowsFlags.javaVersion.substring(0, index);
                    }
                }
                final String property = System.getProperty("sun.java2d.dpiaware");
                if (property != null) {
                    WindowsFlags.setHighDPIAware = property.equalsIgnoreCase("true");
                }
                else {
                    WindowsFlags.setHighDPIAware = System.getProperty("sun.java.launcher", "unknown").equalsIgnoreCase("SUN_STANDARD");
                }
                return null;
            }
        });
    }
    
    public static boolean isD3DEnabled() {
        return WindowsFlags.d3dEnabled;
    }
    
    public static boolean isD3DSet() {
        return WindowsFlags.d3dSet;
    }
    
    public static boolean isD3DOnScreenEnabled() {
        return WindowsFlags.d3dOnScreenEnabled;
    }
    
    public static boolean isD3DVerbose() {
        return WindowsFlags.d3dVerbose;
    }
    
    public static boolean isGdiBlitEnabled() {
        return WindowsFlags.gdiBlitEnabled;
    }
    
    public static boolean isTranslucentAccelerationEnabled() {
        return WindowsFlags.d3dEnabled;
    }
    
    public static boolean isOffscreenSharingEnabled() {
        return WindowsFlags.offscreenSharingEnabled;
    }
    
    public static boolean isMagPresent() {
        return WindowsFlags.magPresent;
    }
    
    public static boolean isOGLEnabled() {
        return WindowsFlags.oglEnabled;
    }
    
    public static boolean isOGLVerbose() {
        return WindowsFlags.oglVerbose;
    }
    
    static {
        WToolkit.loadLibraries();
        initJavaFlags();
        initNativeFlags();
    }
}
