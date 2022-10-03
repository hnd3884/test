package sun.font;

import java.security.AccessController;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.security.PrivilegedAction;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.ref.SoftReference;
import sun.util.logging.PlatformLogger;

public final class FontUtilities
{
    public static boolean isSolaris;
    public static boolean isLinux;
    public static boolean isMacOSX;
    public static boolean isMacOSX14;
    public static boolean isSolaris8;
    public static boolean isSolaris9;
    public static boolean isOpenSolaris;
    public static boolean useT2K;
    public static boolean isWindows;
    public static boolean isOpenJDK;
    static final String LUCIDA_FILE_NAME = "LucidaSansRegular.ttf";
    private static boolean debugFonts;
    private static PlatformLogger logger;
    private static boolean logging;
    public static final int MIN_LAYOUT_CHARCODE = 768;
    public static final int MAX_LAYOUT_CHARCODE = 8303;
    private static volatile SoftReference<ConcurrentHashMap<PhysicalFont, CompositeFont>> compMapRef;
    private static final String[][] nameMap;
    
    public static Font2D getFont2D(final Font font) {
        return FontAccess.getFontAccess().getFont2D(font);
    }
    
    public static boolean isComplexText(final char[] array, final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            if (array[i] >= '\u0300') {
                if (isNonSimpleChar(array[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean isNonSimpleChar(final char c) {
        return isComplexCharCode(c) || (c >= '\ud800' && c <= '\udfff');
    }
    
    public static boolean isComplexCharCode(final int n) {
        return n >= 768 && n <= 8303 && (n <= 879 || (n >= 1424 && (n <= 1791 || (n >= 2304 && (n <= 3711 || (n >= 3840 && (n <= 4095 || (n >= 4352 && (n < 4607 || (n >= 6016 && (n <= 6143 || (n >= 8204 && (n <= 8205 || (n >= 8234 && n <= 8238) || (n >= 8298 && n <= 8303))))))))))))));
    }
    
    public static PlatformLogger getLogger() {
        return FontUtilities.logger;
    }
    
    public static boolean isLogging() {
        return FontUtilities.logging;
    }
    
    public static boolean debugFonts() {
        return FontUtilities.debugFonts;
    }
    
    public static boolean fontSupportsDefaultEncoding(final Font font) {
        return getFont2D(font) instanceof CompositeFont;
    }
    
    public static FontUIResource getCompositeFontUIResource(final Font font) {
        final FontUIResource createdFont = new FontUIResource(font);
        final Font2D font2D = getFont2D(font);
        if (!(font2D instanceof PhysicalFont)) {
            return createdFont;
        }
        final Font2D font2D2 = FontManagerFactory.getInstance().findFont2D("dialog", font.getStyle(), 0);
        if (font2D2 == null || !(font2D2 instanceof CompositeFont)) {
            return createdFont;
        }
        final CompositeFont compositeFont = (CompositeFont)font2D2;
        final PhysicalFont physicalFont = (PhysicalFont)font2D;
        ConcurrentHashMap concurrentHashMap = FontUtilities.compMapRef.get();
        if (concurrentHashMap == null) {
            concurrentHashMap = new ConcurrentHashMap();
            FontUtilities.compMapRef = new SoftReference<ConcurrentHashMap<PhysicalFont, CompositeFont>>(concurrentHashMap);
        }
        CompositeFont compositeFont2 = (CompositeFont)concurrentHashMap.get(physicalFont);
        if (compositeFont2 == null) {
            compositeFont2 = new CompositeFont(physicalFont, compositeFont);
            concurrentHashMap.put(physicalFont, compositeFont2);
        }
        FontAccess.getFontAccess().setFont2D(createdFont, compositeFont2.handle);
        FontAccess.getFontAccess().setCreatedFont(createdFont);
        return createdFont;
    }
    
    public static String mapFcName(final String s) {
        for (int i = 0; i < FontUtilities.nameMap.length; ++i) {
            if (s.equals(FontUtilities.nameMap[i][0])) {
                return FontUtilities.nameMap[i][1];
            }
        }
        return null;
    }
    
    public static FontUIResource getFontConfigFUIR(final String s, final int n, final int n2) {
        String mapFcName = mapFcName(s);
        if (mapFcName == null) {
            mapFcName = "sansserif";
        }
        final FontManager instance = FontManagerFactory.getInstance();
        FontUIResource fontConfigFUIR;
        if (instance instanceof SunFontManager) {
            fontConfigFUIR = ((SunFontManager)instance).getFontConfigFUIR(mapFcName, n, n2);
        }
        else {
            fontConfigFUIR = new FontUIResource(mapFcName, n, n2);
        }
        return fontConfigFUIR;
    }
    
    public static boolean textLayoutIsCompatible(final Font font) {
        final Font2D font2D = getFont2D(font);
        if (font2D instanceof TrueTypeFont) {
            final TrueTypeFont trueTypeFont = (TrueTypeFont)font2D;
            return trueTypeFont.getDirectoryEntry(1196643650) == null || trueTypeFont.getDirectoryEntry(1196445523) != null;
        }
        return false;
    }
    
    static {
        FontUtilities.debugFonts = false;
        FontUtilities.logger = null;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                final String property = System.getProperty("os.name", "unknownOS");
                FontUtilities.isSolaris = property.startsWith("SunOS");
                FontUtilities.isLinux = property.startsWith("Linux");
                FontUtilities.isMacOSX = property.contains("OS X");
                if (FontUtilities.isMacOSX) {
                    FontUtilities.isMacOSX14 = true;
                    final String property2 = System.getProperty("os.version", "");
                    if (property2.startsWith("10.")) {
                        String s = property2.substring(3);
                        final int index = s.indexOf(46);
                        if (index != -1) {
                            s = s.substring(0, index);
                        }
                        try {
                            FontUtilities.isMacOSX14 = (Integer.parseInt(s) >= 14);
                        }
                        catch (final NumberFormatException ex) {}
                    }
                }
                final String property3 = System.getProperty("sun.java2d.font.scaler");
                if (property3 != null) {
                    FontUtilities.useT2K = "t2k".equals(property3);
                }
                else {
                    FontUtilities.useT2K = false;
                }
                if (FontUtilities.isSolaris) {
                    final String property4 = System.getProperty("os.version", "0.0");
                    FontUtilities.isSolaris8 = property4.startsWith("5.8");
                    FontUtilities.isSolaris9 = property4.startsWith("5.9");
                    if (Float.parseFloat(property4) > 5.1f) {
                        final File file = new File("/etc/release");
                        String line = null;
                        try {
                            final FileInputStream fileInputStream = new FileInputStream(file);
                            line = new BufferedReader(new InputStreamReader(fileInputStream, "ISO-8859-1")).readLine();
                            fileInputStream.close();
                        }
                        catch (final Exception ex2) {}
                        if (line != null && line.indexOf("OpenSolaris") >= 0) {
                            FontUtilities.isOpenSolaris = true;
                        }
                        else {
                            FontUtilities.isOpenSolaris = false;
                        }
                    }
                    else {
                        FontUtilities.isOpenSolaris = false;
                    }
                }
                else {
                    FontUtilities.isSolaris8 = false;
                    FontUtilities.isSolaris9 = false;
                    FontUtilities.isOpenSolaris = false;
                }
                FontUtilities.isWindows = property.startsWith("Windows");
                FontUtilities.isOpenJDK = !new File(System.getProperty("java.home", "") + File.separator + "lib" + File.separator + "fonts" + File.separator + "LucidaSansRegular.ttf").exists();
                final String property5 = System.getProperty("sun.java2d.debugfonts");
                if (property5 != null && !property5.equals("false")) {
                    FontUtilities.debugFonts = true;
                    FontUtilities.logger = PlatformLogger.getLogger("sun.java2d");
                    if (property5.equals("warning")) {
                        FontUtilities.logger.setLevel(PlatformLogger.Level.WARNING);
                    }
                    else if (property5.equals("severe")) {
                        FontUtilities.logger.setLevel(PlatformLogger.Level.SEVERE);
                    }
                }
                if (FontUtilities.debugFonts) {
                    FontUtilities.logger = PlatformLogger.getLogger("sun.java2d");
                    FontUtilities.logging = FontUtilities.logger.isEnabled();
                }
                return null;
            }
        });
        FontUtilities.compMapRef = new SoftReference<ConcurrentHashMap<PhysicalFont, CompositeFont>>(null);
        nameMap = new String[][] { { "sans", "sansserif" }, { "sans-serif", "sansserif" }, { "serif", "serif" }, { "monospace", "monospaced" } };
    }
}
