package sun.awt;

import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;
import sun.awt.windows.WFontConfiguration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.font.TrueTypeFont;
import sun.font.SunFontManager;

public final class Win32FontManager extends SunFontManager
{
    private static TrueTypeFont eudcFont;
    static String fontsForPrinting;
    
    private static native String getEUDCFontFile();
    
    @Override
    public TrueTypeFont getEUDCFont() {
        return Win32FontManager.eudcFont;
    }
    
    public Win32FontManager() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                Win32FontManager.this.registerJREFontsWithPlatform(SunFontManager.jreFontDirName);
                return null;
            }
        });
    }
    
    @Override
    protected boolean useAbsoluteFontFileNames() {
        return false;
    }
    
    @Override
    protected void registerFontFile(final String s, final String[] array, final int n, final boolean b) {
        if (this.registeredFontFiles.contains(s)) {
            return;
        }
        this.registeredFontFiles.add(s);
        int n2;
        if (this.getTrueTypeFilter().accept(null, s)) {
            n2 = 0;
        }
        else {
            if (!this.getType1Filter().accept(null, s)) {
                return;
            }
            n2 = 1;
        }
        if (this.fontPath == null) {
            this.fontPath = this.getPlatformFontPath(Win32FontManager.noType1Font);
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(Win32FontManager.jreFontDirName + File.pathSeparator + this.fontPath, File.pathSeparator);
        int n3 = 0;
        try {
            while (n3 == 0 && stringTokenizer.hasMoreTokens()) {
                final String nextToken = stringTokenizer.nextToken();
                final boolean equals = nextToken.equals(Win32FontManager.jreFontDirName);
                final File file = new File(nextToken, s);
                if (file.canRead()) {
                    n3 = 1;
                    final String absolutePath = file.getAbsolutePath();
                    if (b) {
                        this.registerDeferredFont(s, absolutePath, array, n2, equals, n);
                        break;
                    }
                    this.registerFontFile(absolutePath, array, n2, equals, n);
                    break;
                }
            }
        }
        catch (final NoSuchElementException ex) {
            System.err.println(ex);
        }
        if (n3 == 0) {
            this.addToMissingFontFileList(s);
        }
    }
    
    @Override
    protected FontConfiguration createFontConfiguration() {
        final WFontConfiguration wFontConfiguration = new WFontConfiguration(this);
        wFontConfiguration.init();
        return wFontConfiguration;
    }
    
    @Override
    public FontConfiguration createFontConfiguration(final boolean b, final boolean b2) {
        return new WFontConfiguration(this, b, b2);
    }
    
    @Override
    protected void populateFontFileNameMap(final HashMap<String, String> hashMap, final HashMap<String, String> hashMap2, final HashMap<String, ArrayList<String>> hashMap3, final Locale locale) {
        populateFontFileNameMap0(hashMap, hashMap2, hashMap3, locale);
    }
    
    private static native void populateFontFileNameMap0(final HashMap<String, String> p0, final HashMap<String, String> p1, final HashMap<String, ArrayList<String>> p2, final Locale p3);
    
    @Override
    protected synchronized native String getFontPath(final boolean p0);
    
    @Override
    protected String[] getDefaultPlatformFont() {
        final String[] array = { "Arial", "c:\\windows\\fonts" };
        final String[] platformFontDirs = this.getPlatformFontDirs(true);
        if (platformFontDirs.length > 1) {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
                @Override
                public Object run() {
                    for (int i = 0; i < platformFontDirs.length; ++i) {
                        if (new File(platformFontDirs[i] + File.separator + "arial.ttf").exists()) {
                            return platformFontDirs[i];
                        }
                    }
                    return null;
                }
            });
            if (s != null) {
                array[1] = s;
            }
        }
        else {
            array[1] = platformFontDirs[0];
        }
        array[1] = array[1] + File.separator + "arial.ttf";
        return array;
    }
    
    protected void registerJREFontsWithPlatform(final String fontsForPrinting) {
        Win32FontManager.fontsForPrinting = fontsForPrinting;
    }
    
    public static void registerJREFontsForPrinting() {
        final String fontsForPrinting;
        synchronized (Win32GraphicsEnvironment.class) {
            GraphicsEnvironment.getLocalGraphicsEnvironment();
            if (Win32FontManager.fontsForPrinting == null) {
                return;
            }
            fontsForPrinting = Win32FontManager.fontsForPrinting;
            Win32FontManager.fontsForPrinting = null;
        }
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                final File file = new File(fontsForPrinting);
                final String[] list = file.list(SunFontManager.getInstance().getTrueTypeFilter());
                if (list == null) {
                    return null;
                }
                for (int i = 0; i < list.length; ++i) {
                    Win32FontManager.registerFontWithPlatform(new File(file, list[i]).getAbsolutePath());
                }
                return null;
            }
        });
    }
    
    protected static native void registerFontWithPlatform(final String p0);
    
    protected static native void deRegisterFontWithPlatform(final String p0);
    
    @Override
    public HashMap<String, FamilyDescription> populateHardcodedFileNameMap() {
        final HashMap hashMap = new HashMap();
        final FamilyDescription familyDescription = new FamilyDescription();
        familyDescription.familyName = "Segoe UI";
        familyDescription.plainFullName = "Segoe UI";
        familyDescription.plainFileName = "segoeui.ttf";
        familyDescription.boldFullName = "Segoe UI Bold";
        familyDescription.boldFileName = "segoeuib.ttf";
        familyDescription.italicFullName = "Segoe UI Italic";
        familyDescription.italicFileName = "segoeuii.ttf";
        familyDescription.boldItalicFullName = "Segoe UI Bold Italic";
        familyDescription.boldItalicFileName = "segoeuiz.ttf";
        hashMap.put("segoe", familyDescription);
        final FamilyDescription familyDescription2 = new FamilyDescription();
        familyDescription2.familyName = "Tahoma";
        familyDescription2.plainFullName = "Tahoma";
        familyDescription2.plainFileName = "tahoma.ttf";
        familyDescription2.boldFullName = "Tahoma Bold";
        familyDescription2.boldFileName = "tahomabd.ttf";
        hashMap.put("tahoma", familyDescription2);
        final FamilyDescription familyDescription3 = new FamilyDescription();
        familyDescription3.familyName = "Verdana";
        familyDescription3.plainFullName = "Verdana";
        familyDescription3.plainFileName = "verdana.TTF";
        familyDescription3.boldFullName = "Verdana Bold";
        familyDescription3.boldFileName = "verdanab.TTF";
        familyDescription3.italicFullName = "Verdana Italic";
        familyDescription3.italicFileName = "verdanai.TTF";
        familyDescription3.boldItalicFullName = "Verdana Bold Italic";
        familyDescription3.boldItalicFileName = "verdanaz.TTF";
        hashMap.put("verdana", familyDescription3);
        final FamilyDescription familyDescription4 = new FamilyDescription();
        familyDescription4.familyName = "Arial";
        familyDescription4.plainFullName = "Arial";
        familyDescription4.plainFileName = "ARIAL.TTF";
        familyDescription4.boldFullName = "Arial Bold";
        familyDescription4.boldFileName = "ARIALBD.TTF";
        familyDescription4.italicFullName = "Arial Italic";
        familyDescription4.italicFileName = "ARIALI.TTF";
        familyDescription4.boldItalicFullName = "Arial Bold Italic";
        familyDescription4.boldItalicFileName = "ARIALBI.TTF";
        hashMap.put("arial", familyDescription4);
        final FamilyDescription familyDescription5 = new FamilyDescription();
        familyDescription5.familyName = "Symbol";
        familyDescription5.plainFullName = "Symbol";
        familyDescription5.plainFileName = "Symbol.TTF";
        hashMap.put("symbol", familyDescription5);
        final FamilyDescription familyDescription6 = new FamilyDescription();
        familyDescription6.familyName = "WingDings";
        familyDescription6.plainFullName = "WingDings";
        familyDescription6.plainFileName = "WINGDING.TTF";
        hashMap.put("wingdings", familyDescription6);
        return hashMap;
    }
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                final String access$000 = getEUDCFontFile();
                if (access$000 != null) {
                    try {
                        Win32FontManager.eudcFont = new TrueTypeFont(access$000, null, 0, true, false);
                    }
                    catch (final FontFormatException ex) {}
                }
                return null;
            }
        });
        Win32FontManager.fontsForPrinting = null;
    }
}
