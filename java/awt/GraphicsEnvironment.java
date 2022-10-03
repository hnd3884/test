package java.awt;

import sun.java2d.SunGraphicsEnvironment;
import sun.font.FontManagerFactory;
import java.util.Locale;
import java.awt.image.BufferedImage;
import sun.java2d.HeadlessGraphicsEnvironment;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public abstract class GraphicsEnvironment
{
    private static GraphicsEnvironment localEnv;
    private static Boolean headless;
    private static Boolean defaultHeadless;
    
    protected GraphicsEnvironment() {
    }
    
    public static synchronized GraphicsEnvironment getLocalGraphicsEnvironment() {
        if (GraphicsEnvironment.localEnv == null) {
            GraphicsEnvironment.localEnv = createGE();
        }
        return GraphicsEnvironment.localEnv;
    }
    
    private static GraphicsEnvironment createGE() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.awt.graphicsenv", null));
        GraphicsEnvironment graphicsEnvironment;
        try {
            Class<?> clazz;
            try {
                clazz = Class.forName(s);
            }
            catch (final ClassNotFoundException ex) {
                clazz = Class.forName(s, true, ClassLoader.getSystemClassLoader());
            }
            graphicsEnvironment = (GraphicsEnvironment)clazz.newInstance();
            if (isHeadless()) {
                graphicsEnvironment = new HeadlessGraphicsEnvironment(graphicsEnvironment);
            }
        }
        catch (final ClassNotFoundException ex2) {
            throw new Error("Could not find class: " + s);
        }
        catch (final InstantiationException ex3) {
            throw new Error("Could not instantiate Graphics Environment: " + s);
        }
        catch (final IllegalAccessException ex4) {
            throw new Error("Could not access Graphics Environment: " + s);
        }
        return graphicsEnvironment;
    }
    
    public static boolean isHeadless() {
        return getHeadlessProperty();
    }
    
    static String getHeadlessMessage() {
        if (GraphicsEnvironment.headless == null) {
            getHeadlessProperty();
        }
        return (GraphicsEnvironment.defaultHeadless != Boolean.TRUE) ? null : "\nNo X11 DISPLAY variable was set, but this program performed an operation which requires it.";
    }
    
    private static boolean getHeadlessProperty() {
        if (GraphicsEnvironment.headless == null) {
            AccessController.doPrivileged(() -> {
                System.getProperty("java.awt.headless");
                final String s;
                if (s == null) {
                    if (System.getProperty("javaplugin.version") != null) {
                        final Boolean b = GraphicsEnvironment.headless = (GraphicsEnvironment.defaultHeadless = Boolean.FALSE);
                    }
                    else {
                        System.getProperty("os.name");
                        final String s2;
                        if (s2.contains("OS X") && "sun.awt.HToolkit".equals(System.getProperty("awt.toolkit"))) {
                            final Boolean b2 = GraphicsEnvironment.headless = (GraphicsEnvironment.defaultHeadless = Boolean.TRUE);
                        }
                        else {
                            System.getenv("DISPLAY");
                            final String s3;
                            final Boolean b3 = GraphicsEnvironment.headless = (GraphicsEnvironment.defaultHeadless = (("Linux".equals(s2) || "SunOS".equals(s2) || "FreeBSD".equals(s2) || "NetBSD".equals(s2) || "OpenBSD".equals(s2) || "AIX".equals(s2)) && (s3 == null || s3.trim().isEmpty())));
                        }
                    }
                }
                else {
                    GraphicsEnvironment.headless = Boolean.valueOf(s);
                }
                return null;
            });
        }
        return GraphicsEnvironment.headless;
    }
    
    static void checkHeadless() throws HeadlessException {
        if (isHeadless()) {
            throw new HeadlessException();
        }
    }
    
    public boolean isHeadlessInstance() {
        return getHeadlessProperty();
    }
    
    public abstract GraphicsDevice[] getScreenDevices() throws HeadlessException;
    
    public abstract GraphicsDevice getDefaultScreenDevice() throws HeadlessException;
    
    public abstract Graphics2D createGraphics(final BufferedImage p0);
    
    public abstract Font[] getAllFonts();
    
    public abstract String[] getAvailableFontFamilyNames();
    
    public abstract String[] getAvailableFontFamilyNames(final Locale p0);
    
    public boolean registerFont(final Font font) {
        if (font == null) {
            throw new NullPointerException("font cannot be null.");
        }
        return FontManagerFactory.getInstance().registerFont(font);
    }
    
    public void preferLocaleFonts() {
        FontManagerFactory.getInstance().preferLocaleFonts();
    }
    
    public void preferProportionalFonts() {
        FontManagerFactory.getInstance().preferProportionalFonts();
    }
    
    public Point getCenterPoint() throws HeadlessException {
        final Rectangle usableBounds = SunGraphicsEnvironment.getUsableBounds(this.getDefaultScreenDevice());
        return new Point(usableBounds.width / 2 + usableBounds.x, usableBounds.height / 2 + usableBounds.y);
    }
    
    public Rectangle getMaximumWindowBounds() throws HeadlessException {
        return SunGraphicsEnvironment.getUsableBounds(this.getDefaultScreenDevice());
    }
}
