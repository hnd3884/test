package sun.font;

import javax.swing.plaf.FontUIResource;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import sun.applet.AppletSecurity;
import sun.misc.ThreadGroupUtils;
import sun.awt.AppContext;
import java.util.TreeMap;
import sun.awt.SunToolkit;
import java.util.Collection;
import java.util.Map;
import java.awt.FontFormatException;
import java.security.AccessController;
import sun.util.logging.PlatformLogger;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Hashtable;
import java.io.File;
import java.util.Vector;
import java.util.Locale;
import java.awt.Font;
import java.io.FilenameFilter;
import sun.awt.FontConfiguration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import sun.java2d.FontSupport;

public abstract class SunFontManager implements FontSupport, FontManagerForSGE
{
    public static final int FONTFORMAT_NONE = -1;
    public static final int FONTFORMAT_TRUETYPE = 0;
    public static final int FONTFORMAT_TYPE1 = 1;
    public static final int FONTFORMAT_T2K = 2;
    public static final int FONTFORMAT_TTC = 3;
    public static final int FONTFORMAT_COMPOSITE = 4;
    public static final int FONTFORMAT_NATIVE = 5;
    protected static final int CHANNELPOOLSIZE = 20;
    protected FileFont[] fontFileCache;
    private int lastPoolIndex;
    private int maxCompFont;
    private CompositeFont[] compFonts;
    private ConcurrentHashMap<String, CompositeFont> compositeFonts;
    private ConcurrentHashMap<String, PhysicalFont> physicalFonts;
    private ConcurrentHashMap<String, PhysicalFont> registeredFonts;
    protected ConcurrentHashMap<String, Font2D> fullNameToFont;
    private HashMap<String, TrueTypeFont> localeFullNamesToFont;
    private PhysicalFont defaultPhysicalFont;
    static boolean longAddresses;
    private boolean loaded1dot0Fonts;
    boolean loadedAllFonts;
    boolean loadedAllFontFiles;
    HashMap<String, String> jreFontMap;
    HashSet<String> jreLucidaFontFiles;
    String[] jreOtherFontFiles;
    boolean noOtherJREFontFiles;
    public static final String lucidaFontName = "Lucida Sans Regular";
    public static String jreLibDirName;
    public static String jreFontDirName;
    private static HashSet<String> missingFontFiles;
    private String defaultFontName;
    private String defaultFontFileName;
    protected HashSet registeredFontFiles;
    private ArrayList badFonts;
    protected String fontPath;
    private FontConfiguration fontConfig;
    private boolean discoveredAllFonts;
    private static final FilenameFilter ttFilter;
    private static final FilenameFilter t1Filter;
    private Font[] allFonts;
    private String[] allFamilies;
    private Locale lastDefaultLocale;
    public static boolean noType1Font;
    private static String[] STR_ARRAY;
    private boolean usePlatformFontMetrics;
    private static int maxSoftRefCnt;
    private final ConcurrentHashMap<String, FontRegistrationInfo> deferredFontFiles;
    private final ConcurrentHashMap<String, Font2DHandle> initialisedFonts;
    private HashMap<String, String> fontToFileMap;
    private HashMap<String, String> fontToFamilyNameMap;
    private HashMap<String, ArrayList<String>> familyToFontListMap;
    private String[] pathDirs;
    private boolean haveCheckedUnreferencedFontFiles;
    static HashMap<String, FamilyDescription> platformFontMap;
    private ConcurrentHashMap<String, Font2D> fontNameCache;
    protected Thread fileCloser;
    Vector<File> tmpFontFiles;
    private int createdFontCount;
    private static final Object altJAFontKey;
    private static final Object localeFontKey;
    private static final Object proportionalFontKey;
    private boolean _usingPerAppContextComposites;
    private boolean _usingAlternateComposites;
    private static boolean gAltJAFont;
    private boolean gLocalePref;
    private boolean gPropPref;
    private static HashSet<String> installedNames;
    private static final Object regFamilyKey;
    private static final Object regFullNameKey;
    private Hashtable<String, FontFamily> createdByFamilyName;
    private Hashtable<String, Font2D> createdByFullName;
    private boolean fontsAreRegistered;
    private boolean fontsAreRegisteredPerAppContext;
    private static Locale systemLocale;
    
    public static SunFontManager getInstance() {
        return (SunFontManager)FontManagerFactory.getInstance();
    }
    
    public FilenameFilter getTrueTypeFilter() {
        return SunFontManager.ttFilter;
    }
    
    public FilenameFilter getType1Filter() {
        return SunFontManager.t1Filter;
    }
    
    @Override
    public boolean usingPerAppContextComposites() {
        return this._usingPerAppContextComposites;
    }
    
    private void initJREFontMap() {
        this.jreFontMap = new HashMap<String, String>();
        this.jreLucidaFontFiles = new HashSet<String>();
        if (isOpenJDK()) {
            return;
        }
        this.jreFontMap.put("lucida sans0", "LucidaSansRegular.ttf");
        this.jreFontMap.put("lucida sans1", "LucidaSansDemiBold.ttf");
        this.jreFontMap.put("lucida sans regular0", "LucidaSansRegular.ttf");
        this.jreFontMap.put("lucida sans regular1", "LucidaSansDemiBold.ttf");
        this.jreFontMap.put("lucida sans bold1", "LucidaSansDemiBold.ttf");
        this.jreFontMap.put("lucida sans demibold1", "LucidaSansDemiBold.ttf");
        this.jreFontMap.put("lucida sans typewriter0", "LucidaTypewriterRegular.ttf");
        this.jreFontMap.put("lucida sans typewriter1", "LucidaTypewriterBold.ttf");
        this.jreFontMap.put("lucida sans typewriter regular0", "LucidaTypewriter.ttf");
        this.jreFontMap.put("lucida sans typewriter regular1", "LucidaTypewriterBold.ttf");
        this.jreFontMap.put("lucida sans typewriter bold1", "LucidaTypewriterBold.ttf");
        this.jreFontMap.put("lucida sans typewriter demibold1", "LucidaTypewriterBold.ttf");
        this.jreFontMap.put("lucida bright0", "LucidaBrightRegular.ttf");
        this.jreFontMap.put("lucida bright1", "LucidaBrightDemiBold.ttf");
        this.jreFontMap.put("lucida bright2", "LucidaBrightItalic.ttf");
        this.jreFontMap.put("lucida bright3", "LucidaBrightDemiItalic.ttf");
        this.jreFontMap.put("lucida bright regular0", "LucidaBrightRegular.ttf");
        this.jreFontMap.put("lucida bright regular1", "LucidaBrightDemiBold.ttf");
        this.jreFontMap.put("lucida bright regular2", "LucidaBrightItalic.ttf");
        this.jreFontMap.put("lucida bright regular3", "LucidaBrightDemiItalic.ttf");
        this.jreFontMap.put("lucida bright bold1", "LucidaBrightDemiBold.ttf");
        this.jreFontMap.put("lucida bright bold3", "LucidaBrightDemiItalic.ttf");
        this.jreFontMap.put("lucida bright demibold1", "LucidaBrightDemiBold.ttf");
        this.jreFontMap.put("lucida bright demibold3", "LucidaBrightDemiItalic.ttf");
        this.jreFontMap.put("lucida bright italic2", "LucidaBrightItalic.ttf");
        this.jreFontMap.put("lucida bright italic3", "LucidaBrightDemiItalic.ttf");
        this.jreFontMap.put("lucida bright bold italic3", "LucidaBrightDemiItalic.ttf");
        this.jreFontMap.put("lucida bright demibold italic3", "LucidaBrightDemiItalic.ttf");
        final Iterator<String> iterator = this.jreFontMap.values().iterator();
        while (iterator.hasNext()) {
            this.jreLucidaFontFiles.add(iterator.next());
        }
    }
    
    public TrueTypeFont getEUDCFont() {
        return null;
    }
    
    private static native void initIDs();
    
    protected SunFontManager() {
        this.fontFileCache = new FileFont[20];
        this.lastPoolIndex = 0;
        this.maxCompFont = 0;
        this.compFonts = new CompositeFont[20];
        this.compositeFonts = new ConcurrentHashMap<String, CompositeFont>();
        this.physicalFonts = new ConcurrentHashMap<String, PhysicalFont>();
        this.registeredFonts = new ConcurrentHashMap<String, PhysicalFont>();
        this.fullNameToFont = new ConcurrentHashMap<String, Font2D>();
        this.loaded1dot0Fonts = false;
        this.loadedAllFonts = false;
        this.loadedAllFontFiles = false;
        this.noOtherJREFontFiles = false;
        this.registeredFontFiles = new HashSet();
        this.discoveredAllFonts = false;
        this.usePlatformFontMetrics = false;
        this.deferredFontFiles = new ConcurrentHashMap<String, FontRegistrationInfo>();
        this.initialisedFonts = new ConcurrentHashMap<String, Font2DHandle>();
        this.fontToFileMap = null;
        this.fontToFamilyNameMap = null;
        this.familyToFontListMap = null;
        this.pathDirs = null;
        this.fontNameCache = new ConcurrentHashMap<String, Font2D>();
        this.fileCloser = null;
        this.tmpFontFiles = null;
        this.createdFontCount = 0;
        this._usingPerAppContextComposites = false;
        this._usingAlternateComposites = false;
        this.gLocalePref = false;
        this.gPropPref = false;
        this.fontsAreRegistered = false;
        this.fontsAreRegisteredPerAppContext = false;
        this.initJREFontMap();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                final File file = new File(SunFontManager.jreFontDirName + File.separator + "badfonts.txt");
                if (file.exists()) {
                    InputStream inputStream = null;
                    try {
                        SunFontManager.this.badFonts = new ArrayList();
                        inputStream = new FileInputStream(file);
                        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        while (true) {
                            final String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            if (FontUtilities.debugFonts()) {
                                FontUtilities.getLogger().warning("read bad font: " + line);
                            }
                            SunFontManager.this.badFonts.add(line);
                        }
                    }
                    catch (final IOException ex) {
                        try {
                            if (inputStream != null) {
                                ((FileInputStream)inputStream).close();
                            }
                        }
                        catch (final IOException ex2) {}
                    }
                }
                if (FontUtilities.isLinux) {
                    SunFontManager.this.registerFontDir(SunFontManager.jreFontDirName);
                }
                SunFontManager.this.registerFontsInDir(SunFontManager.jreFontDirName, true, 2, true, false);
                SunFontManager.this.fontConfig = SunFontManager.this.createFontConfiguration();
                if (SunFontManager.isOpenJDK()) {
                    final String[] defaultPlatformFont = SunFontManager.this.getDefaultPlatformFont();
                    SunFontManager.this.defaultFontName = defaultPlatformFont[0];
                    SunFontManager.this.defaultFontFileName = defaultPlatformFont[1];
                }
                final String extraFontPath = SunFontManager.this.fontConfig.getExtraFontPath();
                boolean b = false;
                boolean b2 = false;
                String fontPath = System.getProperty("sun.java2d.fontpath");
                if (fontPath != null) {
                    if (fontPath.startsWith("prepend:")) {
                        b = true;
                        fontPath = fontPath.substring("prepend:".length());
                    }
                    else if (fontPath.startsWith("append:")) {
                        b2 = true;
                        fontPath = fontPath.substring("append:".length());
                    }
                }
                if (FontUtilities.debugFonts()) {
                    final PlatformLogger logger = FontUtilities.getLogger();
                    logger.info("JRE font directory: " + SunFontManager.jreFontDirName);
                    logger.info("Extra font path: " + extraFontPath);
                    logger.info("Debug font path: " + fontPath);
                }
                if (fontPath != null) {
                    SunFontManager.this.fontPath = SunFontManager.this.getPlatformFontPath(SunFontManager.noType1Font);
                    if (extraFontPath != null) {
                        SunFontManager.this.fontPath = extraFontPath + File.pathSeparator + SunFontManager.this.fontPath;
                    }
                    if (b2) {
                        SunFontManager.this.fontPath = SunFontManager.this.fontPath + File.pathSeparator + fontPath;
                    }
                    else if (b) {
                        SunFontManager.this.fontPath = fontPath + File.pathSeparator + SunFontManager.this.fontPath;
                    }
                    else {
                        SunFontManager.this.fontPath = fontPath;
                    }
                    SunFontManager.this.registerFontDirs(SunFontManager.this.fontPath);
                }
                else if (extraFontPath != null) {
                    SunFontManager.this.registerFontDirs(extraFontPath);
                }
                if (FontUtilities.isSolaris && Locale.JAPAN.equals(Locale.getDefault())) {
                    SunFontManager.this.registerFontDir("/usr/openwin/lib/locale/ja/X11/fonts/TT");
                }
                SunFontManager.this.initCompositeFonts(SunFontManager.this.fontConfig, null);
                return null;
            }
        });
        if (AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                final String property = System.getProperty("java2d.font.usePlatformFont");
                final String getenv = System.getenv("JAVA2D_USEPLATFORMFONT");
                return "true".equals(property) || getenv != null;
            }
        })) {
            this.usePlatformFontMetrics = true;
            System.out.println("Enabling platform font metrics for win32. This is an unsupported option.");
            System.out.println("This yields incorrect composite font metrics as reported by 1.1.x releases.");
            System.out.println("It is appropriate only for use by applications which do not use any Java 2");
            System.out.println("functionality. This property will be removed in a later release.");
        }
    }
    
    @Override
    public Font2DHandle getNewComposite(String familyName, int style, final Font2DHandle font2DHandle) {
        if (!(font2DHandle.font2D instanceof CompositeFont)) {
            return font2DHandle;
        }
        final CompositeFont compositeFont = (CompositeFont)font2DHandle.font2D;
        final PhysicalFont slotFont = compositeFont.getSlotFont(0);
        if (familyName == null) {
            familyName = slotFont.getFamilyName(null);
        }
        if (style == -1) {
            style = compositeFont.getStyle();
        }
        Font2D font2D = this.findFont2D(familyName, style, 0);
        if (!(font2D instanceof PhysicalFont)) {
            font2D = slotFont;
        }
        final PhysicalFont physicalFont = (PhysicalFont)font2D;
        final CompositeFont compositeFont2 = (CompositeFont)this.findFont2D("dialog", style, 0);
        if (compositeFont2 == null) {
            return font2DHandle;
        }
        return new Font2DHandle(new CompositeFont(physicalFont, compositeFont2));
    }
    
    protected void registerCompositeFont(final String s, final String[] array, final String[] array2, final int n, final int[] array3, final int[] array4, final boolean b) {
        final CompositeFont compositeFont = new CompositeFont(s, array, array2, n, array3, array4, b, this);
        this.addCompositeToFontList(compositeFont, 2);
        synchronized (this.compFonts) {
            this.compFonts[this.maxCompFont++] = compositeFont;
        }
    }
    
    protected static void registerCompositeFont(final String s, final String[] array, final String[] array2, final int n, final int[] array3, final int[] array4, final boolean b, final ConcurrentHashMap<String, Font2D> concurrentHashMap) {
        final CompositeFont font2D = new CompositeFont(s, array, array2, n, array3, array4, b, getInstance());
        final Font2D font2D2 = concurrentHashMap.get(s.toLowerCase(Locale.ENGLISH));
        if (font2D2 instanceof CompositeFont) {
            font2D2.handle.font2D = font2D;
        }
        concurrentHashMap.put(s.toLowerCase(Locale.ENGLISH), font2D);
    }
    
    private void addCompositeToFontList(final CompositeFont compositeFont, final int rank) {
        if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("Add to Family " + compositeFont.familyName + ", Font " + compositeFont.fullName + " rank=" + rank);
        }
        compositeFont.setRank(rank);
        this.compositeFonts.put(compositeFont.fullName, compositeFont);
        this.fullNameToFont.put(compositeFont.fullName.toLowerCase(Locale.ENGLISH), compositeFont);
        FontFamily family = FontFamily.getFamily(compositeFont.familyName);
        if (family == null) {
            family = new FontFamily(compositeFont.familyName, true, rank);
        }
        family.setFont(compositeFont, compositeFont.style);
    }
    
    protected PhysicalFont addToFontList(final PhysicalFont physicalFont, final int rank) {
        final String fullName = physicalFont.fullName;
        final String familyName = physicalFont.familyName;
        if (fullName == null || "".equals(fullName)) {
            return null;
        }
        if (this.compositeFonts.containsKey(fullName)) {
            return null;
        }
        physicalFont.setRank(rank);
        if (!this.physicalFonts.containsKey(fullName)) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().info("Add to Family " + familyName + ", Font " + fullName + " rank=" + rank);
            }
            this.physicalFonts.put(fullName, physicalFont);
            final FontFamily family = FontFamily.getFamily(familyName);
            if (family == null) {
                new FontFamily(familyName, false, rank).setFont(physicalFont, physicalFont.style);
            }
            else {
                family.setFont(physicalFont, physicalFont.style);
            }
            this.fullNameToFont.put(fullName.toLowerCase(Locale.ENGLISH), physicalFont);
            return physicalFont;
        }
        final PhysicalFont physicalFont2 = this.physicalFonts.get(fullName);
        if (physicalFont2 == null) {
            return null;
        }
        if (physicalFont2.getRank() < rank) {
            return physicalFont2;
        }
        if (physicalFont2.mapper != null && rank > 2) {
            return physicalFont2;
        }
        if (physicalFont2.getRank() == rank) {
            if (!(physicalFont2 instanceof TrueTypeFont) || !(physicalFont instanceof TrueTypeFont)) {
                return physicalFont2;
            }
            if (((TrueTypeFont)physicalFont2).fileSize >= ((TrueTypeFont)physicalFont).fileSize) {
                return physicalFont2;
            }
        }
        if (physicalFont2.platName.startsWith(SunFontManager.jreFontDirName)) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().warning("Unexpected attempt to replace a JRE  font " + fullName + " from " + physicalFont2.platName + " with " + physicalFont.platName);
            }
            return physicalFont2;
        }
        if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("Replace in Family " + familyName + ",Font " + fullName + " new rank=" + rank + " from " + physicalFont2.platName + " with " + physicalFont.platName);
        }
        this.replaceFont(physicalFont2, physicalFont);
        this.physicalFonts.put(fullName, physicalFont);
        this.fullNameToFont.put(fullName.toLowerCase(Locale.ENGLISH), physicalFont);
        final FontFamily family2 = FontFamily.getFamily(familyName);
        if (family2 == null) {
            new FontFamily(familyName, false, rank).setFont(physicalFont, physicalFont.style);
        }
        else {
            family2.setFont(physicalFont, physicalFont.style);
        }
        return physicalFont;
    }
    
    public Font2D[] getRegisteredFonts() {
        final PhysicalFont[] physicalFonts = this.getPhysicalFonts();
        final int maxCompFont = this.maxCompFont;
        final Font2D[] array = new Font2D[physicalFonts.length + maxCompFont];
        System.arraycopy(this.compFonts, 0, array, 0, maxCompFont);
        System.arraycopy(physicalFonts, 0, array, maxCompFont, physicalFonts.length);
        return array;
    }
    
    protected PhysicalFont[] getPhysicalFonts() {
        return this.physicalFonts.values().toArray(new PhysicalFont[0]);
    }
    
    protected synchronized void initialiseDeferredFonts() {
        final Iterator<String> iterator = this.deferredFontFiles.keySet().iterator();
        while (iterator.hasNext()) {
            this.initialiseDeferredFont(iterator.next());
        }
    }
    
    protected synchronized void registerDeferredJREFonts(final String s) {
        for (final FontRegistrationInfo fontRegistrationInfo : this.deferredFontFiles.values()) {
            if (fontRegistrationInfo.fontFilePath != null && fontRegistrationInfo.fontFilePath.startsWith(s)) {
                this.initialiseDeferredFont(fontRegistrationInfo.fontFilePath);
            }
        }
    }
    
    public boolean isDeferredFont(final String s) {
        return this.deferredFontFiles.containsKey(s);
    }
    
    public PhysicalFont findJREDeferredFont(final String s, final int n) {
        final String s2 = this.jreFontMap.get(s.toLowerCase(Locale.ENGLISH) + n);
        if (s2 != null) {
            final String string = SunFontManager.jreFontDirName + File.separator + s2;
            if (this.deferredFontFiles.get(string) != null) {
                final PhysicalFont initialiseDeferredFont = this.initialiseDeferredFont(string);
                if (initialiseDeferredFont != null && (initialiseDeferredFont.getFontName(null).equalsIgnoreCase(s) || initialiseDeferredFont.getFamilyName(null).equalsIgnoreCase(s)) && initialiseDeferredFont.style == n) {
                    return initialiseDeferredFont;
                }
            }
        }
        if (this.noOtherJREFontFiles) {
            return null;
        }
        synchronized (this.jreLucidaFontFiles) {
            if (this.jreOtherFontFiles == null) {
                final HashSet<String> set = new HashSet<String>();
                for (final String s3 : this.deferredFontFiles.keySet()) {
                    final File file = new File(s3);
                    final String parent = file.getParent();
                    final String name = file.getName();
                    if (parent != null && parent.equals(SunFontManager.jreFontDirName)) {
                        if (this.jreLucidaFontFiles.contains(name)) {
                            continue;
                        }
                        set.add(s3);
                    }
                }
                this.jreOtherFontFiles = set.toArray(SunFontManager.STR_ARRAY);
                if (this.jreOtherFontFiles.length == 0) {
                    this.noOtherJREFontFiles = true;
                }
            }
            for (int i = 0; i < this.jreOtherFontFiles.length; ++i) {
                final String s4 = this.jreOtherFontFiles[i];
                if (s4 != null) {
                    this.jreOtherFontFiles[i] = null;
                    final PhysicalFont initialiseDeferredFont2 = this.initialiseDeferredFont(s4);
                    if (initialiseDeferredFont2 != null && (initialiseDeferredFont2.getFontName(null).equalsIgnoreCase(s) || initialiseDeferredFont2.getFamilyName(null).equalsIgnoreCase(s)) && initialiseDeferredFont2.style == n) {
                        return initialiseDeferredFont2;
                    }
                }
            }
        }
        return null;
    }
    
    private PhysicalFont findOtherDeferredFont(final String s, final int n) {
        for (final String s2 : this.deferredFontFiles.keySet()) {
            final File file = new File(s2);
            final String parent = file.getParent();
            final String name = file.getName();
            if (parent != null && parent.equals(SunFontManager.jreFontDirName) && this.jreLucidaFontFiles.contains(name)) {
                continue;
            }
            final PhysicalFont initialiseDeferredFont = this.initialiseDeferredFont(s2);
            if (initialiseDeferredFont != null && (initialiseDeferredFont.getFontName(null).equalsIgnoreCase(s) || initialiseDeferredFont.getFamilyName(null).equalsIgnoreCase(s)) && initialiseDeferredFont.style == n) {
                return initialiseDeferredFont;
            }
        }
        return null;
    }
    
    private PhysicalFont findDeferredFont(final String s, final int n) {
        final PhysicalFont jreDeferredFont = this.findJREDeferredFont(s, n);
        if (jreDeferredFont != null) {
            return jreDeferredFont;
        }
        return this.findOtherDeferredFont(s, n);
    }
    
    public void registerDeferredFont(final String s, final String s2, final String[] array, final int n, final boolean b, final int n2) {
        this.deferredFontFiles.put(s, new FontRegistrationInfo(s2, array, n, b, n2));
    }
    
    public synchronized PhysicalFont initialiseDeferredFont(final String s) {
        if (s == null) {
            return null;
        }
        if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("Opening deferred font file " + s);
        }
        final FontRegistrationInfo fontRegistrationInfo = this.deferredFontFiles.get(s);
        PhysicalFont physicalFont;
        if (fontRegistrationInfo != null) {
            this.deferredFontFiles.remove(s);
            physicalFont = this.registerFontFile(fontRegistrationInfo.fontFilePath, fontRegistrationInfo.nativeNames, fontRegistrationInfo.fontFormat, fontRegistrationInfo.javaRasterizer, fontRegistrationInfo.fontRank);
            if (physicalFont != null) {
                this.initialisedFonts.put(s, physicalFont.handle);
            }
            else {
                this.initialisedFonts.put(s, this.getDefaultPhysicalFont().handle);
            }
        }
        else {
            final Font2DHandle font2DHandle = this.initialisedFonts.get(s);
            if (font2DHandle == null) {
                physicalFont = this.getDefaultPhysicalFont();
            }
            else {
                physicalFont = (PhysicalFont)font2DHandle.font2D;
            }
        }
        return physicalFont;
    }
    
    public boolean isRegisteredFontFile(final String s) {
        return this.registeredFonts.containsKey(s);
    }
    
    public PhysicalFont getRegisteredFontFile(final String s) {
        return this.registeredFonts.get(s);
    }
    
    public PhysicalFont registerFontFile(final String s, final String[] array, final int n, final boolean b, final int n2) {
        final PhysicalFont physicalFont = this.registeredFonts.get(s);
        if (physicalFont != null) {
            return physicalFont;
        }
        PhysicalFont physicalFont2 = null;
        try {
            switch (n) {
                case 0: {
                    int i = 0;
                    TrueTypeFont trueTypeFont;
                    do {
                        trueTypeFont = new TrueTypeFont(s, array, i++, b);
                        final PhysicalFont addToFontList = this.addToFontList(trueTypeFont, n2);
                        if (physicalFont2 == null) {
                            physicalFont2 = addToFontList;
                        }
                    } while (i < trueTypeFont.getFontCount());
                    break;
                }
                case 1: {
                    physicalFont2 = this.addToFontList(new Type1Font(s, array), n2);
                    break;
                }
                case 5: {
                    physicalFont2 = this.addToFontList(new NativeFont(s, false), n2);
                    break;
                }
            }
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().info("Registered file " + s + " as font " + physicalFont2 + " rank=" + n2);
            }
        }
        catch (final FontFormatException ex) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().warning("Unusable font: " + s + " " + ex.toString());
            }
        }
        if (physicalFont2 != null && n != 5) {
            this.registeredFonts.put(s, physicalFont2);
        }
        return physicalFont2;
    }
    
    public void registerFonts(final String[] array, final String[][] array2, final int n, final int n2, final boolean b, final int n3, final boolean b2) {
        for (int i = 0; i < n; ++i) {
            if (b2) {
                this.registerDeferredFont(array[i], array[i], array2[i], n2, b, n3);
            }
            else {
                this.registerFontFile(array[i], array2[i], n2, b, n3);
            }
        }
    }
    
    public PhysicalFont getDefaultPhysicalFont() {
        if (this.defaultPhysicalFont == null) {
            this.defaultPhysicalFont = (PhysicalFont)this.findFont2D("Lucida Sans Regular", 0, 0);
            if (this.defaultPhysicalFont == null) {
                this.defaultPhysicalFont = (PhysicalFont)this.findFont2D("Arial", 0, 0);
            }
            if (this.defaultPhysicalFont == null) {
                final Iterator<PhysicalFont> iterator = this.physicalFonts.values().iterator();
                if (!iterator.hasNext()) {
                    throw new Error("Probable fatal error:No fonts found.");
                }
                this.defaultPhysicalFont = iterator.next();
            }
        }
        return this.defaultPhysicalFont;
    }
    
    public Font2D getDefaultLogicalFont(final int n) {
        return this.findFont2D("dialog", n, 0);
    }
    
    private static String dotStyleStr(final int n) {
        switch (n) {
            case 1: {
                return ".bold";
            }
            case 2: {
                return ".italic";
            }
            case 3: {
                return ".bolditalic";
            }
            default: {
                return ".plain";
            }
        }
    }
    
    protected void populateFontFileNameMap(final HashMap<String, String> hashMap, final HashMap<String, String> hashMap2, final HashMap<String, ArrayList<String>> hashMap3, final Locale locale) {
    }
    
    private String[] getFontFilesFromPath(final boolean b) {
        FilenameFilter ttFilter;
        if (b) {
            ttFilter = SunFontManager.ttFilter;
        }
        else {
            ttFilter = new TTorT1Filter();
        }
        return AccessController.doPrivileged((PrivilegedAction<String[]>)new PrivilegedAction() {
            @Override
            public Object run() {
                if (SunFontManager.this.pathDirs.length != 1) {
                    final ArrayList list = new ArrayList();
                    for (int i = 0; i < SunFontManager.this.pathDirs.length; ++i) {
                        final String[] list2 = new File(SunFontManager.this.pathDirs[i]).list(ttFilter);
                        if (list2 != null) {
                            for (int j = 0; j < list2.length; ++j) {
                                list.add(list2[j].toLowerCase());
                            }
                        }
                    }
                    return list.toArray(SunFontManager.STR_ARRAY);
                }
                final String[] list3 = new File(SunFontManager.this.pathDirs[0]).list(ttFilter);
                if (list3 == null) {
                    return new String[0];
                }
                for (int k = 0; k < list3.length; ++k) {
                    list3[k] = list3[k].toLowerCase();
                }
                return list3;
            }
        });
    }
    
    private void resolveWindowsFonts() {
        ArrayList<String> list = null;
        for (final String s : this.fontToFamilyNameMap.keySet()) {
            if (this.fontToFileMap.get(s) == null) {
                if (s.indexOf("  ") > 0) {
                    final String replaceFirst = s.replaceFirst("  ", " ");
                    final String s2 = this.fontToFileMap.get(replaceFirst);
                    if (s2 == null || this.fontToFamilyNameMap.containsKey(replaceFirst)) {
                        continue;
                    }
                    this.fontToFileMap.remove(replaceFirst);
                    this.fontToFileMap.put(s, s2);
                }
                else if (s.equals("marlett")) {
                    this.fontToFileMap.put(s, "marlett.ttf");
                }
                else if (s.equals("david")) {
                    final String s3 = this.fontToFileMap.get("david regular");
                    if (s3 == null) {
                        continue;
                    }
                    this.fontToFileMap.remove("david regular");
                    this.fontToFileMap.put("david", s3);
                }
                else {
                    if (list == null) {
                        list = new ArrayList<String>();
                    }
                    list.add(s);
                }
            }
        }
        if (list != null) {
            final HashSet<String> set = new HashSet<String>();
            final HashMap hashMap = (HashMap)this.fontToFileMap.clone();
            final Iterator<String> iterator2 = this.fontToFamilyNameMap.keySet().iterator();
            while (iterator2.hasNext()) {
                hashMap.remove(iterator2.next());
            }
            for (final String s4 : hashMap.keySet()) {
                set.add((String)hashMap.get(s4));
                this.fontToFileMap.remove(s4);
            }
            this.resolveFontFiles(set, list);
            if (list.size() > 0) {
                final ArrayList<String> list2 = new ArrayList<String>();
                final Iterator<String> iterator4 = this.fontToFileMap.values().iterator();
                while (iterator4.hasNext()) {
                    list2.add(iterator4.next().toLowerCase());
                }
                for (final String s5 : this.getFontFilesFromPath(true)) {
                    if (!list2.contains(s5)) {
                        set.add(s5);
                    }
                }
                this.resolveFontFiles(set, list);
            }
            if (list.size() > 0) {
                for (int size = list.size(), j = 0; j < size; ++j) {
                    final String s6 = list.get(j);
                    final String s7 = this.fontToFamilyNameMap.get(s6);
                    if (s7 != null) {
                        final ArrayList list3 = this.familyToFontListMap.get(s7);
                        if (list3 != null && list3.size() <= 1) {
                            this.familyToFontListMap.remove(s7);
                        }
                    }
                    this.fontToFamilyNameMap.remove(s6);
                    if (FontUtilities.isLogging()) {
                        FontUtilities.getLogger().info("No file for font:" + s6);
                    }
                }
            }
        }
    }
    
    private synchronized void checkForUnreferencedFontFiles() {
        if (this.haveCheckedUnreferencedFontFiles) {
            return;
        }
        this.haveCheckedUnreferencedFontFiles = true;
        if (!FontUtilities.isWindows) {
            return;
        }
        final ArrayList list = new ArrayList();
        final Iterator<String> iterator = this.fontToFileMap.values().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next().toLowerCase());
        }
        HashMap<String, String> fontToFileMap = null;
        HashMap<String, String> fontToFamilyNameMap = null;
        HashMap<String, ArrayList<String>> familyToFontListMap = null;
        for (final String s : this.getFontFilesFromPath(false)) {
            if (!list.contains(s)) {
                if (FontUtilities.isLogging()) {
                    FontUtilities.getLogger().info("Found non-registry file : " + s);
                }
                final PhysicalFont registerFontFile = this.registerFontFile(this.getPathName(s));
                if (registerFontFile != null) {
                    if (fontToFileMap == null) {
                        fontToFileMap = new HashMap<String, String>(this.fontToFileMap);
                        fontToFamilyNameMap = new HashMap<String, String>(this.fontToFamilyNameMap);
                        familyToFontListMap = new HashMap<String, ArrayList<String>>(this.familyToFontListMap);
                    }
                    final String fontName = registerFontFile.getFontName(null);
                    final String familyName = registerFontFile.getFamilyName(null);
                    final String lowerCase = familyName.toLowerCase();
                    fontToFamilyNameMap.put(fontName, familyName);
                    fontToFileMap.put(fontName, s);
                    final ArrayList list2 = familyToFontListMap.get(lowerCase);
                    ArrayList list3;
                    if (list2 == null) {
                        list3 = new ArrayList();
                    }
                    else {
                        list3 = new ArrayList(list2);
                    }
                    list3.add(fontName);
                    familyToFontListMap.put(lowerCase, list3);
                }
            }
        }
        if (fontToFileMap != null) {
            this.fontToFileMap = fontToFileMap;
            this.familyToFontListMap = familyToFontListMap;
            this.fontToFamilyNameMap = fontToFamilyNameMap;
        }
    }
    
    private void resolveFontFiles(final HashSet<String> set, final ArrayList<String> list) {
        final Locale startupLocale = SunToolkit.getStartupLocale();
        for (final String s : set) {
            try {
                int i = 0;
                final String pathName = this.getPathName(s);
                if (FontUtilities.isLogging()) {
                    FontUtilities.getLogger().info("Trying to resolve file " + pathName);
                }
                TrueTypeFont trueTypeFont;
                do {
                    trueTypeFont = new TrueTypeFont(pathName, null, i++, false);
                    final String lowerCase = trueTypeFont.getFontName(startupLocale).toLowerCase();
                    if (list.contains(lowerCase)) {
                        this.fontToFileMap.put(lowerCase, s);
                        list.remove(lowerCase);
                        if (!FontUtilities.isLogging()) {
                            continue;
                        }
                        FontUtilities.getLogger().info("Resolved absent registry entry for " + lowerCase + " located in " + pathName);
                    }
                } while (i < trueTypeFont.getFontCount());
            }
            catch (final Exception ex) {}
        }
    }
    
    public HashMap<String, FamilyDescription> populateHardcodedFileNameMap() {
        return new HashMap<String, FamilyDescription>(0);
    }
    
    Font2D findFontFromPlatformMap(final String s, int n) {
        if (SunFontManager.platformFontMap == null) {
            SunFontManager.platformFontMap = this.populateHardcodedFileNameMap();
        }
        if (SunFontManager.platformFontMap == null || SunFontManager.platformFontMap.size() == 0) {
            return null;
        }
        final int index = s.indexOf(32);
        String substring = s;
        if (index > 0) {
            substring = s.substring(0, index);
        }
        final FamilyDescription familyDescription = SunFontManager.platformFontMap.get(substring);
        if (familyDescription == null) {
            return null;
        }
        int n2 = -1;
        if (s.equalsIgnoreCase(familyDescription.plainFullName)) {
            n2 = 0;
        }
        else if (s.equalsIgnoreCase(familyDescription.boldFullName)) {
            n2 = 1;
        }
        else if (s.equalsIgnoreCase(familyDescription.italicFullName)) {
            n2 = 2;
        }
        else if (s.equalsIgnoreCase(familyDescription.boldItalicFullName)) {
            n2 = 3;
        }
        if (n2 == -1 && !s.equalsIgnoreCase(familyDescription.familyName)) {
            return null;
        }
        String pathName = null;
        String pathName2 = null;
        String pathName3 = null;
        String pathName4 = null;
        boolean b = false;
        this.getPlatformFontDirs(SunFontManager.noType1Font);
        if (familyDescription.plainFileName != null) {
            pathName = this.getPathName(familyDescription.plainFileName);
            if (pathName == null) {
                b = true;
            }
        }
        if (familyDescription.boldFileName != null) {
            pathName2 = this.getPathName(familyDescription.boldFileName);
            if (pathName2 == null) {
                b = true;
            }
        }
        if (familyDescription.italicFileName != null) {
            pathName3 = this.getPathName(familyDescription.italicFileName);
            if (pathName3 == null) {
                b = true;
            }
        }
        if (familyDescription.boldItalicFileName != null) {
            pathName4 = this.getPathName(familyDescription.boldItalicFileName);
            if (pathName4 == null) {
                b = true;
            }
        }
        if (b) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().info("Hardcoded file missing looking for " + s);
            }
            SunFontManager.platformFontMap.remove(substring);
            return null;
        }
        final String[] array = { pathName, pathName2, pathName3, pathName4 };
        if (AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                for (int i = 0; i < array.length; ++i) {
                    if (array[i] != null) {
                        if (!new File(array[i]).exists()) {
                            return Boolean.TRUE;
                        }
                    }
                }
                return Boolean.FALSE;
            }
        })) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().info("Hardcoded file missing looking for " + s);
            }
            SunFontManager.platformFontMap.remove(substring);
            return null;
        }
        Font2D font2D = null;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                final PhysicalFont registerFontFile = this.registerFontFile(array[i], null, 0, false, 3);
                if (i == n2) {
                    font2D = registerFontFile;
                }
            }
        }
        final FontFamily family = FontFamily.getFamily(familyDescription.familyName);
        if (family != null) {
            if (font2D == null) {
                font2D = family.getFont(n);
                if (font2D == null) {
                    font2D = family.getClosestStyle(n);
                }
            }
            else if (n > 0 && n != font2D.style) {
                n |= font2D.style;
                font2D = family.getFont(n);
                if (font2D == null) {
                    font2D = family.getClosestStyle(n);
                }
            }
        }
        return font2D;
    }
    
    private synchronized HashMap<String, String> getFullNameToFileMap() {
        if (this.fontToFileMap == null) {
            this.pathDirs = this.getPlatformFontDirs(SunFontManager.noType1Font);
            this.fontToFileMap = new HashMap<String, String>(100);
            this.fontToFamilyNameMap = new HashMap<String, String>(100);
            this.familyToFontListMap = new HashMap<String, ArrayList<String>>(50);
            this.populateFontFileNameMap(this.fontToFileMap, this.fontToFamilyNameMap, this.familyToFontListMap, Locale.ENGLISH);
            if (FontUtilities.isWindows) {
                this.resolveWindowsFonts();
            }
            if (FontUtilities.isLogging()) {
                this.logPlatformFontInfo();
            }
        }
        return this.fontToFileMap;
    }
    
    private void logPlatformFontInfo() {
        final PlatformLogger logger = FontUtilities.getLogger();
        for (int i = 0; i < this.pathDirs.length; ++i) {
            logger.info("fontdir=" + this.pathDirs[i]);
        }
        for (final String s : this.fontToFileMap.keySet()) {
            logger.info("font=" + s + " file=" + this.fontToFileMap.get(s));
        }
        for (final String s2 : this.fontToFamilyNameMap.keySet()) {
            logger.info("font=" + s2 + " family=" + this.fontToFamilyNameMap.get(s2));
        }
        for (final String s3 : this.familyToFontListMap.keySet()) {
            logger.info("family=" + s3 + " fonts=" + this.familyToFontListMap.get(s3));
        }
    }
    
    protected String[] getFontNamesFromPlatform() {
        if (this.getFullNameToFileMap().size() == 0) {
            return null;
        }
        this.checkForUnreferencedFontFiles();
        final ArrayList list = new ArrayList();
        final Iterator<ArrayList<String>> iterator = this.familyToFontListMap.values().iterator();
        while (iterator.hasNext()) {
            final Iterator iterator2 = iterator.next().iterator();
            while (iterator2.hasNext()) {
                list.add(iterator2.next());
            }
        }
        return list.toArray(SunFontManager.STR_ARRAY);
    }
    
    public boolean gotFontsFromPlatform() {
        return this.getFullNameToFileMap().size() != 0;
    }
    
    public String getFileNameForFontName(final String s) {
        return this.fontToFileMap.get(s.toLowerCase(Locale.ENGLISH));
    }
    
    private PhysicalFont registerFontFile(final String s) {
        if (!new File(s).isAbsolute() || this.registeredFonts.contains(s)) {
            return null;
        }
        int n = -1;
        int n2 = 6;
        if (SunFontManager.ttFilter.accept(null, s)) {
            n = 0;
            n2 = 3;
        }
        else if (SunFontManager.t1Filter.accept(null, s)) {
            n = 1;
            n2 = 4;
        }
        if (n == -1) {
            return null;
        }
        return this.registerFontFile(s, null, n, false, n2);
    }
    
    protected void registerOtherFontFiles(final HashSet set) {
        if (this.getFullNameToFileMap().size() == 0) {
            return;
        }
        final Iterator<String> iterator = this.fontToFileMap.values().iterator();
        while (iterator.hasNext()) {
            this.registerFontFile(iterator.next());
        }
    }
    
    public boolean getFamilyNamesFromPlatform(final TreeMap<String, String> treeMap, final Locale locale) {
        if (this.getFullNameToFileMap().size() == 0) {
            return false;
        }
        this.checkForUnreferencedFontFiles();
        for (final String s : this.fontToFamilyNameMap.values()) {
            treeMap.put(s.toLowerCase(locale), s);
        }
        return true;
    }
    
    private String getPathName(final String s) {
        if (new File(s).isAbsolute()) {
            return s;
        }
        if (this.pathDirs.length == 1) {
            return this.pathDirs[0] + File.separator + s;
        }
        final String s2 = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                for (int i = 0; i < SunFontManager.this.pathDirs.length; ++i) {
                    final File file = new File(SunFontManager.this.pathDirs[i] + File.separator + s);
                    if (file.exists()) {
                        return file.getAbsolutePath();
                    }
                }
                return null;
            }
        });
        if (s2 != null) {
            return s2;
        }
        return s;
    }
    
    private Font2D findFontFromPlatform(final String s, int n) {
        if (this.getFullNameToFileMap().size() == 0) {
            return null;
        }
        String s2 = null;
        String s3 = this.fontToFamilyNameMap.get(s);
        ArrayList list;
        if (s3 != null) {
            s2 = this.fontToFileMap.get(s);
            list = this.familyToFontListMap.get(s3.toLowerCase(Locale.ENGLISH));
        }
        else {
            list = this.familyToFontListMap.get(s);
            if (list != null && list.size() > 0) {
                final String lowerCase = ((String)list.get(0)).toLowerCase(Locale.ENGLISH);
                if (lowerCase != null) {
                    s3 = this.fontToFamilyNameMap.get(lowerCase);
                }
            }
        }
        if (list == null || s3 == null) {
            return null;
        }
        final String[] array = (String[])list.toArray(SunFontManager.STR_ARRAY);
        if (array.length == 0) {
            return null;
        }
        for (int i = 0; i < array.length; ++i) {
            if (this.fontToFileMap.get(array[i].toLowerCase(Locale.ENGLISH)) == null) {
                if (FontUtilities.isLogging()) {
                    FontUtilities.getLogger().info("Platform lookup : No file for font " + array[i] + " in family " + s3);
                }
                return null;
            }
        }
        PhysicalFont registerFontFile = null;
        if (s2 != null) {
            registerFontFile = this.registerFontFile(this.getPathName(s2), null, 0, false, 3);
        }
        for (int j = 0; j < array.length; ++j) {
            final String s4 = this.fontToFileMap.get(array[j].toLowerCase(Locale.ENGLISH));
            if (s2 == null || !s2.equals(s4)) {
                this.registerFontFile(this.getPathName(s4), null, 0, false, 3);
            }
        }
        Font2D font2D = null;
        final FontFamily family = FontFamily.getFamily(s3);
        if (registerFontFile != null) {
            n |= registerFontFile.style;
        }
        if (family != null) {
            font2D = family.getFont(n);
            if (font2D == null) {
                font2D = family.getClosestStyle(n);
            }
        }
        return font2D;
    }
    
    @Override
    public Font2D findFont2D(String s, final int n, final int n2) {
        final String lowerCase = s.toLowerCase(Locale.ENGLISH);
        final String string = lowerCase + dotStyleStr(n);
        Font2D font2D;
        if (this._usingPerAppContextComposites) {
            final ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap)AppContext.getAppContext().get(CompositeFont.class);
            if (concurrentHashMap != null) {
                font2D = concurrentHashMap.get(string);
            }
            else {
                font2D = null;
            }
        }
        else {
            font2D = this.fontNameCache.get(string);
        }
        if (font2D != null) {
            return font2D;
        }
        if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("Search for font: " + s);
        }
        if (FontUtilities.isWindows) {
            if (lowerCase.equals("ms sans serif")) {
                s = "sansserif";
            }
            else if (lowerCase.equals("ms serif")) {
                s = "serif";
            }
        }
        if (lowerCase.equals("default")) {
            s = "dialog";
        }
        final FontFamily family = FontFamily.getFamily(s);
        if (family != null) {
            Font2D font2D2 = family.getFontWithExactStyleMatch(n);
            if (font2D2 == null) {
                font2D2 = this.findDeferredFont(s, n);
            }
            if (font2D2 == null) {
                font2D2 = family.getFont(n);
            }
            if (font2D2 == null) {
                font2D2 = family.getClosestStyle(n);
            }
            if (font2D2 != null) {
                this.fontNameCache.put(string, font2D2);
                return font2D2;
            }
        }
        final Font2D font2D3 = this.fullNameToFont.get(lowerCase);
        if (font2D3 != null) {
            if (font2D3.style == n || n == 0) {
                this.fontNameCache.put(string, font2D3);
                return font2D3;
            }
            final FontFamily family2 = FontFamily.getFamily(font2D3.getFamilyName(null));
            if (family2 != null) {
                final Font2D font = family2.getFont(n | font2D3.style);
                if (font != null) {
                    this.fontNameCache.put(string, font);
                    return font;
                }
                final Font2D closestStyle = family2.getClosestStyle(n | font2D3.style);
                if (closestStyle != null && closestStyle.canDoStyle(n | font2D3.style)) {
                    this.fontNameCache.put(string, closestStyle);
                    return closestStyle;
                }
            }
        }
        if (FontUtilities.isWindows) {
            final Font2D fontFromPlatformMap = this.findFontFromPlatformMap(lowerCase, n);
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().info("findFontFromPlatformMap returned " + fontFromPlatformMap);
            }
            if (fontFromPlatformMap != null) {
                this.fontNameCache.put(string, fontFromPlatformMap);
                return fontFromPlatformMap;
            }
            if (this.deferredFontFiles.size() > 0) {
                final PhysicalFont jreDeferredFont = this.findJREDeferredFont(lowerCase, n);
                if (jreDeferredFont != null) {
                    this.fontNameCache.put(string, jreDeferredFont);
                    return jreDeferredFont;
                }
            }
            final Font2D fontFromPlatform = this.findFontFromPlatform(lowerCase, n);
            if (fontFromPlatform != null) {
                if (FontUtilities.isLogging()) {
                    FontUtilities.getLogger().info("Found font via platform API for request:\"" + s + "\":, style=" + n + " found font: " + fontFromPlatform);
                }
                this.fontNameCache.put(string, fontFromPlatform);
                return fontFromPlatform;
            }
        }
        if (this.deferredFontFiles.size() > 0) {
            final PhysicalFont deferredFont = this.findDeferredFont(s, n);
            if (deferredFont != null) {
                this.fontNameCache.put(string, deferredFont);
                return deferredFont;
            }
        }
        if (FontUtilities.isSolaris && !this.loaded1dot0Fonts) {
            if (lowerCase.equals("timesroman")) {
                this.fontNameCache.put(string, this.findFont2D("serif", n, n2));
            }
            this.register1dot0Fonts();
            this.loaded1dot0Fonts = true;
            return this.findFont2D(s, n, n2);
        }
        if (this.fontsAreRegistered || this.fontsAreRegisteredPerAppContext) {
            Hashtable<String, FontFamily> createdByFamilyName;
            Hashtable<String, Font2D> createdByFullName;
            if (this.fontsAreRegistered) {
                createdByFamilyName = this.createdByFamilyName;
                createdByFullName = this.createdByFullName;
            }
            else {
                final AppContext appContext = AppContext.getAppContext();
                createdByFamilyName = (Hashtable)appContext.get(SunFontManager.regFamilyKey);
                createdByFullName = (Hashtable)appContext.get(SunFontManager.regFullNameKey);
            }
            final FontFamily fontFamily = createdByFamilyName.get(lowerCase);
            if (fontFamily != null) {
                Font2D font2D4 = fontFamily.getFontWithExactStyleMatch(n);
                if (font2D4 == null) {
                    font2D4 = fontFamily.getFont(n);
                }
                if (font2D4 == null) {
                    font2D4 = fontFamily.getClosestStyle(n);
                }
                if (font2D4 != null) {
                    if (this.fontsAreRegistered) {
                        this.fontNameCache.put(string, font2D4);
                    }
                    return font2D4;
                }
            }
            final Font2D font2D5 = createdByFullName.get(lowerCase);
            if (font2D5 != null) {
                if (this.fontsAreRegistered) {
                    this.fontNameCache.put(string, font2D5);
                }
                return font2D5;
            }
        }
        if (!this.loadedAllFonts) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().info("Load fonts looking for:" + s);
            }
            this.loadFonts();
            this.loadedAllFonts = true;
            return this.findFont2D(s, n, n2);
        }
        if (!this.loadedAllFontFiles) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().info("Load font files looking for:" + s);
            }
            this.loadFontFiles();
            this.loadedAllFontFiles = true;
            return this.findFont2D(s, n, n2);
        }
        final Font2D font2DAllLocales;
        if ((font2DAllLocales = this.findFont2DAllLocales(s, n)) != null) {
            this.fontNameCache.put(string, font2DAllLocales);
            return font2DAllLocales;
        }
        if (FontUtilities.isWindows) {
            final String fallbackFamilyName = this.getFontConfiguration().getFallbackFamilyName(s, null);
            if (fallbackFamilyName != null) {
                final Font2D font2D6 = this.findFont2D(fallbackFamilyName, n, n2);
                this.fontNameCache.put(string, font2D6);
                return font2D6;
            }
        }
        else {
            if (lowerCase.equals("timesroman")) {
                final Font2D font2D7 = this.findFont2D("serif", n, n2);
                this.fontNameCache.put(string, font2D7);
                return font2D7;
            }
            if (lowerCase.equals("helvetica")) {
                final Font2D font2D8 = this.findFont2D("sansserif", n, n2);
                this.fontNameCache.put(string, font2D8);
                return font2D8;
            }
            if (lowerCase.equals("courier")) {
                final Font2D font2D9 = this.findFont2D("monospaced", n, n2);
                this.fontNameCache.put(string, font2D9);
                return font2D9;
            }
        }
        if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("No font found for:" + s);
        }
        switch (n2) {
            case 1: {
                return this.getDefaultPhysicalFont();
            }
            case 2: {
                return this.getDefaultLogicalFont(n);
            }
            default: {
                return null;
            }
        }
    }
    
    public boolean usePlatformFontMetrics() {
        return this.usePlatformFontMetrics;
    }
    
    public int getNumFonts() {
        return this.physicalFonts.size() + this.maxCompFont;
    }
    
    private static boolean fontSupportsEncoding(final Font font, final String s) {
        return FontUtilities.getFont2D(font).supportsEncoding(s);
    }
    
    protected abstract String getFontPath(final boolean p0);
    
    @Override
    public Font2D createFont2D(final File file, final int n, final boolean b, final CreatedFontTracker createdFontTracker) throws FontFormatException {
        final String path = file.getPath();
        boolean b2 = false;
        int n2 = 0;
        synchronized (this) {
            if (this.createdFontCount < SunFontManager.maxSoftRefCnt) {
                ++this.createdFontCount;
            }
            else {
                b2 = true;
                n2 = 10;
            }
        }
        FileFont fileFont = null;
        try {
            switch (n) {
                case 0: {
                    fileFont = new TrueTypeFont(path, null, 0, true);
                    fileFont.setUseWeakRefs(b2, n2);
                    break;
                }
                case 1: {
                    fileFont = new Type1Font(path, null, b);
                    fileFont.setUseWeakRefs(b2, n2);
                    break;
                }
                default: {
                    throw new FontFormatException("Unrecognised Font Format");
                }
            }
        }
        catch (final FontFormatException ex) {
            if (b) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        if (createdFontTracker != null) {
                            createdFontTracker.subBytes((int)file.length());
                        }
                        file.delete();
                        return null;
                    }
                });
            }
            throw ex;
        }
        if (b) {
            fileFont.setFileToRemove(file, createdFontTracker);
            synchronized (FontManager.class) {
                if (this.tmpFontFiles == null) {
                    this.tmpFontFiles = new Vector<File>();
                }
                this.tmpFontFiles.add(file);
                if (this.fileCloser == null) {
                    AccessController.doPrivileged(() -> {
                        final Object o = new Runnable() {
                            @Override
                            public void run() {
                                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                                    @Override
                                    public Object run() {
                                        for (int i = 0; i < 20; ++i) {
                                            if (SunFontManager.this.fontFileCache[i] != null) {
                                                try {
                                                    SunFontManager.this.fontFileCache[i].close();
                                                }
                                                catch (final Exception ex) {}
                                            }
                                        }
                                        if (SunFontManager.this.tmpFontFiles != null) {
                                            final File[] array = SunFontManager.this.tmpFontFiles.toArray(new File[SunFontManager.this.tmpFontFiles.size()]);
                                            for (int j = 0; j < array.length; ++j) {
                                                try {
                                                    array[j].delete();
                                                }
                                                catch (final Exception ex2) {}
                                            }
                                        }
                                        return null;
                                    }
                                });
                            }
                        };
                        (this.fileCloser = new Thread(ThreadGroupUtils.getRootThreadGroup(), runnable)).setContextClassLoader(null);
                        Runtime.getRuntime().addShutdownHook(this.fileCloser);
                        return null;
                    });
                }
            }
        }
        return fileFont;
    }
    
    public synchronized String getFullNameByFileName(final String s) {
        final PhysicalFont[] physicalFonts = this.getPhysicalFonts();
        for (int i = 0; i < physicalFonts.length; ++i) {
            if (physicalFonts[i].platName.equals(s)) {
                return physicalFonts[i].getFontName(null);
            }
        }
        return null;
    }
    
    @Override
    public synchronized void deRegisterBadFont(final Font2D font2D) {
        if (!(font2D instanceof PhysicalFont)) {
            return;
        }
        if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().severe("Deregister bad font: " + font2D);
        }
        this.replaceFont((PhysicalFont)font2D, this.getDefaultPhysicalFont());
    }
    
    public synchronized void replaceFont(final PhysicalFont physicalFont, PhysicalFont physicalFont2) {
        if (physicalFont.handle.font2D != physicalFont) {
            return;
        }
        if (physicalFont == physicalFont2) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().severe("Can't replace bad font with itself " + physicalFont);
            }
            final PhysicalFont[] physicalFonts = this.getPhysicalFonts();
            for (int i = 0; i < physicalFonts.length; ++i) {
                if (physicalFonts[i] != physicalFont2) {
                    physicalFont2 = physicalFonts[i];
                    break;
                }
            }
            if (physicalFont == physicalFont2) {
                if (FontUtilities.isLogging()) {
                    FontUtilities.getLogger().severe("This is bad. No good physicalFonts found.");
                }
                return;
            }
        }
        physicalFont.handle.font2D = physicalFont2;
        this.physicalFonts.remove(physicalFont.fullName);
        this.fullNameToFont.remove(physicalFont.fullName.toLowerCase(Locale.ENGLISH));
        FontFamily.remove(physicalFont);
        if (this.localeFullNamesToFont != null) {
            final Map.Entry[] array = this.localeFullNamesToFont.entrySet().toArray(new Map.Entry[0]);
            for (int j = 0; j < array.length; ++j) {
                if (array[j].getValue() == physicalFont) {
                    try {
                        array[j].setValue(physicalFont2);
                    }
                    catch (final Exception ex) {
                        this.localeFullNamesToFont.remove(array[j].getKey());
                    }
                }
            }
        }
        for (int k = 0; k < this.maxCompFont; ++k) {
            if (physicalFont2.getRank() > 2) {
                this.compFonts[k].replaceComponentFont(physicalFont, physicalFont2);
            }
        }
    }
    
    private synchronized void loadLocaleNames() {
        if (this.localeFullNamesToFont != null) {
            return;
        }
        this.localeFullNamesToFont = new HashMap<String, TrueTypeFont>();
        final Font2D[] registeredFonts = this.getRegisteredFonts();
        for (int i = 0; i < registeredFonts.length; ++i) {
            if (registeredFonts[i] instanceof TrueTypeFont) {
                final TrueTypeFont trueTypeFont = (TrueTypeFont)registeredFonts[i];
                final String[] allFullNames = trueTypeFont.getAllFullNames();
                for (int j = 0; j < allFullNames.length; ++j) {
                    this.localeFullNamesToFont.put(allFullNames[j], trueTypeFont);
                }
                final FontFamily family = FontFamily.getFamily(trueTypeFont.familyName);
                if (family != null) {
                    FontFamily.addLocaleNames(family, trueTypeFont.getAllFamilyNames());
                }
            }
        }
    }
    
    private Font2D findFont2DAllLocales(final String s, final int n) {
        if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("Searching localised font names for:" + s);
        }
        if (this.localeFullNamesToFont == null) {
            this.loadLocaleNames();
        }
        final String lowerCase = s.toLowerCase();
        Font2D font2D = null;
        final FontFamily localeFamily = FontFamily.getLocaleFamily(lowerCase);
        if (localeFamily != null) {
            font2D = localeFamily.getFont(n);
            if (font2D == null) {
                font2D = localeFamily.getClosestStyle(n);
            }
            if (font2D != null) {
                return font2D;
            }
        }
        synchronized (this) {
            font2D = this.localeFullNamesToFont.get(s);
        }
        if (font2D != null) {
            if (font2D.style == n || n == 0) {
                return font2D;
            }
            final FontFamily family = FontFamily.getFamily(font2D.getFamilyName(null));
            if (family != null) {
                final Font2D font = family.getFont(n);
                if (font != null) {
                    return font;
                }
                Font2D closestStyle = family.getClosestStyle(n);
                if (closestStyle != null) {
                    if (!closestStyle.canDoStyle(n)) {
                        closestStyle = null;
                    }
                    return closestStyle;
                }
            }
        }
        return font2D;
    }
    
    public boolean maybeUsingAlternateCompositeFonts() {
        return this._usingAlternateComposites || this._usingPerAppContextComposites;
    }
    
    public boolean usingAlternateCompositeFonts() {
        return this._usingAlternateComposites || (this._usingPerAppContextComposites && AppContext.getAppContext().get(CompositeFont.class) != null);
    }
    
    private static boolean maybeMultiAppContext() {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            @Override
            public Object run() {
                return new Boolean(System.getSecurityManager() instanceof AppletSecurity);
            }
        });
    }
    
    @Override
    public synchronized void useAlternateFontforJALocales() {
        if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("Entered useAlternateFontforJALocales().");
        }
        if (!FontUtilities.isWindows) {
            return;
        }
        if (!maybeMultiAppContext()) {
            SunFontManager.gAltJAFont = true;
        }
        else {
            AppContext.getAppContext().put(SunFontManager.altJAFontKey, SunFontManager.altJAFontKey);
        }
    }
    
    public boolean usingAlternateFontforJALocales() {
        if (!maybeMultiAppContext()) {
            return SunFontManager.gAltJAFont;
        }
        return AppContext.getAppContext().get(SunFontManager.altJAFontKey) == SunFontManager.altJAFontKey;
    }
    
    @Override
    public synchronized void preferLocaleFonts() {
        if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("Entered preferLocaleFonts().");
        }
        if (!FontConfiguration.willReorderForStartupLocale()) {
            return;
        }
        if (!maybeMultiAppContext()) {
            if (this.gLocalePref) {
                return;
            }
            this.gLocalePref = true;
            this.createCompositeFonts(this.fontNameCache, this.gLocalePref, this.gPropPref);
            this._usingAlternateComposites = true;
        }
        else {
            final AppContext appContext = AppContext.getAppContext();
            if (appContext.get(SunFontManager.localeFontKey) == SunFontManager.localeFontKey) {
                return;
            }
            appContext.put(SunFontManager.localeFontKey, SunFontManager.localeFontKey);
            final boolean b = appContext.get(SunFontManager.proportionalFontKey) == SunFontManager.proportionalFontKey;
            final ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
            appContext.put(CompositeFont.class, concurrentHashMap);
            this.createCompositeFonts(concurrentHashMap, this._usingPerAppContextComposites = true, b);
        }
    }
    
    @Override
    public synchronized void preferProportionalFonts() {
        if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("Entered preferProportionalFonts().");
        }
        if (!FontConfiguration.hasMonoToPropMap()) {
            return;
        }
        if (!maybeMultiAppContext()) {
            if (this.gPropPref) {
                return;
            }
            this.gPropPref = true;
            this.createCompositeFonts(this.fontNameCache, this.gLocalePref, this.gPropPref);
            this._usingAlternateComposites = true;
        }
        else {
            final AppContext appContext = AppContext.getAppContext();
            if (appContext.get(SunFontManager.proportionalFontKey) == SunFontManager.proportionalFontKey) {
                return;
            }
            appContext.put(SunFontManager.proportionalFontKey, SunFontManager.proportionalFontKey);
            final boolean b = appContext.get(SunFontManager.localeFontKey) == SunFontManager.localeFontKey;
            final ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
            appContext.put(CompositeFont.class, concurrentHashMap);
            this.createCompositeFonts(concurrentHashMap, b, this._usingPerAppContextComposites = true);
        }
    }
    
    private static HashSet<String> getInstalledNames() {
        if (SunFontManager.installedNames == null) {
            final Locale systemStartupLocale = getSystemStartupLocale();
            final SunFontManager instance = getInstance();
            final String[] installedFontFamilyNames = instance.getInstalledFontFamilyNames(systemStartupLocale);
            final Font[] allInstalledFonts = instance.getAllInstalledFonts();
            final HashSet installedNames = new HashSet();
            for (int i = 0; i < installedFontFamilyNames.length; ++i) {
                installedNames.add(installedFontFamilyNames[i].toLowerCase(systemStartupLocale));
            }
            for (int j = 0; j < allInstalledFonts.length; ++j) {
                installedNames.add(allInstalledFonts[j].getFontName(systemStartupLocale).toLowerCase(systemStartupLocale));
            }
            SunFontManager.installedNames = installedNames;
        }
        return SunFontManager.installedNames;
    }
    
    @Override
    public boolean registerFont(final Font font) {
        if (font == null) {
            return false;
        }
        synchronized (SunFontManager.regFamilyKey) {
            if (this.createdByFamilyName == null) {
                this.createdByFamilyName = new Hashtable<String, FontFamily>();
                this.createdByFullName = new Hashtable<String, Font2D>();
            }
        }
        if (!FontAccess.getFontAccess().isCreatedFont(font)) {
            return false;
        }
        final HashSet<String> installedNames = getInstalledNames();
        final Locale systemStartupLocale = getSystemStartupLocale();
        final String lowerCase = font.getFamily(systemStartupLocale).toLowerCase();
        final String lowerCase2 = font.getFontName(systemStartupLocale).toLowerCase();
        if (installedNames.contains(lowerCase) || installedNames.contains(lowerCase2)) {
            return false;
        }
        Hashtable<String, FontFamily> createdByFamilyName;
        Hashtable<String, Font2D> createdByFullName;
        if (!maybeMultiAppContext()) {
            createdByFamilyName = this.createdByFamilyName;
            createdByFullName = this.createdByFullName;
            this.fontsAreRegistered = true;
        }
        else {
            final AppContext appContext = AppContext.getAppContext();
            createdByFamilyName = (Hashtable)appContext.get(SunFontManager.regFamilyKey);
            createdByFullName = (Hashtable)appContext.get(SunFontManager.regFullNameKey);
            if (createdByFamilyName == null) {
                createdByFamilyName = new Hashtable<String, FontFamily>();
                createdByFullName = new Hashtable<String, Font2D>();
                appContext.put(SunFontManager.regFamilyKey, createdByFamilyName);
                appContext.put(SunFontManager.regFullNameKey, createdByFullName);
            }
            this.fontsAreRegisteredPerAppContext = true;
        }
        final Font2D font2D = FontUtilities.getFont2D(font);
        final int style = font2D.getStyle();
        FontFamily fontFamily = createdByFamilyName.get(lowerCase);
        if (fontFamily == null) {
            fontFamily = new FontFamily(font.getFamily(systemStartupLocale));
            createdByFamilyName.put(lowerCase, fontFamily);
        }
        if (this.fontsAreRegistered) {
            this.removeFromCache(fontFamily.getFont(0));
            this.removeFromCache(fontFamily.getFont(1));
            this.removeFromCache(fontFamily.getFont(2));
            this.removeFromCache(fontFamily.getFont(3));
            this.removeFromCache(createdByFullName.get(lowerCase2));
        }
        fontFamily.setFont(font2D, style);
        createdByFullName.put(lowerCase2, font2D);
        return true;
    }
    
    private void removeFromCache(final Font2D font2D) {
        if (font2D == null) {
            return;
        }
        final String[] array = this.fontNameCache.keySet().toArray(SunFontManager.STR_ARRAY);
        for (int i = 0; i < array.length; ++i) {
            if (this.fontNameCache.get(array[i]) == font2D) {
                this.fontNameCache.remove(array[i]);
            }
        }
    }
    
    @Override
    public TreeMap<String, String> getCreatedFontFamilyNames() {
        Hashtable<String, FontFamily> createdByFamilyName;
        if (this.fontsAreRegistered) {
            createdByFamilyName = this.createdByFamilyName;
        }
        else {
            if (!this.fontsAreRegisteredPerAppContext) {
                return null;
            }
            createdByFamilyName = (Hashtable)AppContext.getAppContext().get(SunFontManager.regFamilyKey);
        }
        final Locale systemStartupLocale = getSystemStartupLocale();
        synchronized (createdByFamilyName) {
            final TreeMap<String, String> treeMap = new TreeMap<String, String>();
            for (final FontFamily fontFamily : createdByFamilyName.values()) {
                Font2D font2D = fontFamily.getFont(0);
                if (font2D == null) {
                    font2D = fontFamily.getClosestStyle(0);
                }
                final String familyName = font2D.getFamilyName(systemStartupLocale);
                treeMap.put(familyName.toLowerCase(systemStartupLocale), familyName);
            }
            return treeMap;
        }
    }
    
    @Override
    public Font[] getCreatedFonts() {
        Hashtable<String, Font2D> createdByFullName;
        if (this.fontsAreRegistered) {
            createdByFullName = this.createdByFullName;
        }
        else {
            if (!this.fontsAreRegisteredPerAppContext) {
                return null;
            }
            createdByFullName = (Hashtable)AppContext.getAppContext().get(SunFontManager.regFullNameKey);
        }
        final Locale systemStartupLocale = getSystemStartupLocale();
        synchronized (createdByFullName) {
            final Font[] array = new Font[createdByFullName.size()];
            int n = 0;
            final Iterator iterator = createdByFullName.values().iterator();
            while (iterator.hasNext()) {
                array[n++] = new Font(((Font2D)iterator.next()).getFontName(systemStartupLocale), 0, 1);
            }
            return array;
        }
    }
    
    protected String[] getPlatformFontDirs(final boolean b) {
        if (this.pathDirs != null) {
            return this.pathDirs;
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(this.getPlatformFontPath(b), File.pathSeparator);
        final ArrayList list = new ArrayList();
        try {
            while (stringTokenizer.hasMoreTokens()) {
                list.add(stringTokenizer.nextToken());
            }
        }
        catch (final NoSuchElementException ex) {}
        return this.pathDirs = list.toArray(new String[0]);
    }
    
    protected abstract String[] getDefaultPlatformFont();
    
    private void addDirFonts(final String s, final File file, final FilenameFilter filenameFilter, final int n, final boolean b, final int n2, final boolean b2, final boolean b3) {
        final String[] list = file.list(filenameFilter);
        if (list == null || list.length == 0) {
            return;
        }
        final String[] array = new String[list.length];
        final String[][] array2 = new String[list.length][];
        int n3 = 0;
        for (int i = 0; i < list.length; ++i) {
            final File file2 = new File(file, list[i]);
            String s2 = null;
            if (b3) {
                try {
                    s2 = file2.getCanonicalPath();
                }
                catch (final IOException ex) {}
            }
            if (s2 == null) {
                s2 = s + File.separator + list[i];
            }
            if (!this.registeredFontFiles.contains(s2)) {
                if (this.badFonts != null && this.badFonts.contains(s2)) {
                    if (FontUtilities.debugFonts()) {
                        FontUtilities.getLogger().warning("skip bad font " + s2);
                    }
                }
                else {
                    this.registeredFontFiles.add(s2);
                    if (FontUtilities.debugFonts() && FontUtilities.getLogger().isLoggable(PlatformLogger.Level.INFO)) {
                        final String string = "Registering font " + s2;
                        final String[] nativeNames = this.getNativeNames(s2, null);
                        String s3;
                        if (nativeNames == null) {
                            s3 = string + " with no native name";
                        }
                        else {
                            s3 = string + " with native name(s) " + nativeNames[0];
                            for (int j = 1; j < nativeNames.length; ++j) {
                                s3 = s3 + ", " + nativeNames[j];
                            }
                        }
                        FontUtilities.getLogger().info(s3);
                    }
                    array[n3] = s2;
                    array2[n3++] = this.getNativeNames(s2, null);
                }
            }
        }
        this.registerFonts(array, array2, n3, n, b, n2, b2);
    }
    
    protected String[] getNativeNames(final String s, final String s2) {
        return null;
    }
    
    protected String getFileNameFromPlatformName(final String s) {
        return this.fontConfig.getFileNameFromPlatformName(s);
    }
    
    @Override
    public FontConfiguration getFontConfiguration() {
        return this.fontConfig;
    }
    
    public String getPlatformFontPath(final boolean b) {
        if (this.fontPath == null) {
            this.fontPath = this.getFontPath(b);
        }
        return this.fontPath;
    }
    
    public static boolean isOpenJDK() {
        return FontUtilities.isOpenJDK;
    }
    
    protected void loadFonts() {
        if (this.discoveredAllFonts) {
            return;
        }
        synchronized (this) {
            if (FontUtilities.debugFonts()) {
                Thread.dumpStack();
                FontUtilities.getLogger().info("SunGraphicsEnvironment.loadFonts() called");
            }
            this.initialiseDeferredFonts();
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    if (SunFontManager.this.fontPath == null) {
                        SunFontManager.this.fontPath = SunFontManager.this.getPlatformFontPath(SunFontManager.noType1Font);
                        SunFontManager.this.registerFontDirs(SunFontManager.this.fontPath);
                    }
                    if (SunFontManager.this.fontPath != null && !SunFontManager.this.gotFontsFromPlatform()) {
                        SunFontManager.this.registerFontsOnPath(SunFontManager.this.fontPath, false, 6, false, true);
                        SunFontManager.this.loadedAllFontFiles = true;
                    }
                    SunFontManager.this.registerOtherFontFiles(SunFontManager.this.registeredFontFiles);
                    SunFontManager.this.discoveredAllFonts = true;
                    return null;
                }
            });
        }
    }
    
    protected void registerFontDirs(final String s) {
    }
    
    private void registerFontsOnPath(final String s, final boolean b, final int n, final boolean b2, final boolean b3) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s, File.pathSeparator);
        try {
            while (stringTokenizer.hasMoreTokens()) {
                this.registerFontsInDir(stringTokenizer.nextToken(), b, n, b2, b3);
            }
        }
        catch (final NoSuchElementException ex) {}
    }
    
    public void registerFontsInDir(final String s) {
        this.registerFontsInDir(s, true, 2, true, false);
    }
    
    protected void registerFontsInDir(final String s, final boolean b, final int n, final boolean b2, final boolean b3) {
        final File file = new File(s);
        this.addDirFonts(s, file, SunFontManager.ttFilter, 0, b, (n == 6) ? 3 : n, b2, b3);
        this.addDirFonts(s, file, SunFontManager.t1Filter, 1, b, (n == 6) ? 4 : n, b2, b3);
    }
    
    protected void registerFontDir(final String s) {
    }
    
    public synchronized String getDefaultFontFile() {
        if (this.defaultFontFileName == null) {
            this.initDefaultFonts();
        }
        return this.defaultFontFileName;
    }
    
    private void initDefaultFonts() {
        if (!isOpenJDK()) {
            this.defaultFontName = "Lucida Sans Regular";
            if (this.useAbsoluteFontFileNames()) {
                this.defaultFontFileName = SunFontManager.jreFontDirName + File.separator + "LucidaSansRegular.ttf";
            }
            else {
                this.defaultFontFileName = "LucidaSansRegular.ttf";
            }
        }
    }
    
    protected boolean useAbsoluteFontFileNames() {
        return true;
    }
    
    protected abstract FontConfiguration createFontConfiguration();
    
    public abstract FontConfiguration createFontConfiguration(final boolean p0, final boolean p1);
    
    public synchronized String getDefaultFontFaceName() {
        if (this.defaultFontName == null) {
            this.initDefaultFonts();
        }
        return this.defaultFontName;
    }
    
    public void loadFontFiles() {
        this.loadFonts();
        if (this.loadedAllFontFiles) {
            return;
        }
        synchronized (this) {
            if (FontUtilities.debugFonts()) {
                Thread.dumpStack();
                FontUtilities.getLogger().info("loadAllFontFiles() called");
            }
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    if (SunFontManager.this.fontPath == null) {
                        SunFontManager.this.fontPath = SunFontManager.this.getPlatformFontPath(SunFontManager.noType1Font);
                    }
                    if (SunFontManager.this.fontPath != null) {
                        SunFontManager.this.registerFontsOnPath(SunFontManager.this.fontPath, false, 6, false, true);
                    }
                    SunFontManager.this.loadedAllFontFiles = true;
                    return null;
                }
            });
        }
    }
    
    private void initCompositeFonts(final FontConfiguration fontConfiguration, final ConcurrentHashMap<String, Font2D> concurrentHashMap) {
        if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().info("Initialising composite fonts");
        }
        final int numberCoreFonts = fontConfiguration.getNumberCoreFonts();
        final String[] platformFontNames = fontConfiguration.getPlatformFontNames();
        for (int i = 0; i < platformFontNames.length; ++i) {
            final String s = platformFontNames[i];
            String fileNameFromPlatformName = this.getFileNameFromPlatformName(s);
            String[] nativeNames = null;
            if (fileNameFromPlatformName == null || fileNameFromPlatformName.equals(s)) {
                fileNameFromPlatformName = s;
            }
            else {
                if (i < numberCoreFonts) {
                    this.addFontToPlatformFontPath(s);
                }
                nativeNames = this.getNativeNames(fileNameFromPlatformName, s);
            }
            this.registerFontFile(fileNameFromPlatformName, nativeNames, 2, true);
        }
        this.registerPlatformFontsUsedByFontConfiguration();
        final CompositeFontDescriptor[] get2DCompositeFontInfo = fontConfiguration.get2DCompositeFontInfo();
        for (int j = 0; j < get2DCompositeFontInfo.length; ++j) {
            final CompositeFontDescriptor compositeFontDescriptor = get2DCompositeFontInfo[j];
            final String[] componentFileNames = compositeFontDescriptor.getComponentFileNames();
            final String[] componentFaceNames = compositeFontDescriptor.getComponentFaceNames();
            if (SunFontManager.missingFontFiles != null) {
                for (int k = 0; k < componentFileNames.length; ++k) {
                    if (SunFontManager.missingFontFiles.contains(componentFileNames[k])) {
                        componentFileNames[k] = this.getDefaultFontFile();
                        componentFaceNames[k] = this.getDefaultFontFaceName();
                    }
                }
            }
            if (concurrentHashMap != null) {
                registerCompositeFont(compositeFontDescriptor.getFaceName(), componentFileNames, componentFaceNames, compositeFontDescriptor.getCoreComponentCount(), compositeFontDescriptor.getExclusionRanges(), compositeFontDescriptor.getExclusionRangeLimits(), true, concurrentHashMap);
            }
            else {
                this.registerCompositeFont(compositeFontDescriptor.getFaceName(), componentFileNames, componentFaceNames, compositeFontDescriptor.getCoreComponentCount(), compositeFontDescriptor.getExclusionRanges(), compositeFontDescriptor.getExclusionRangeLimits(), true);
            }
            if (FontUtilities.debugFonts()) {
                FontUtilities.getLogger().info("registered " + compositeFontDescriptor.getFaceName());
            }
        }
    }
    
    protected void addFontToPlatformFontPath(final String s) {
    }
    
    protected void registerFontFile(final String s, final String[] array, final int n, final boolean b) {
        if (this.registeredFontFiles.contains(s)) {
            return;
        }
        int n2;
        if (SunFontManager.ttFilter.accept(null, s)) {
            n2 = 0;
        }
        else if (SunFontManager.t1Filter.accept(null, s)) {
            n2 = 1;
        }
        else {
            n2 = 5;
        }
        this.registeredFontFiles.add(s);
        if (b) {
            this.registerDeferredFont(s, s, array, n2, false, n);
        }
        else {
            this.registerFontFile(s, array, n2, false, n);
        }
    }
    
    protected void registerPlatformFontsUsedByFontConfiguration() {
    }
    
    protected void addToMissingFontFileList(final String s) {
        if (SunFontManager.missingFontFiles == null) {
            SunFontManager.missingFontFiles = new HashSet<String>();
        }
        SunFontManager.missingFontFiles.add(s);
    }
    
    private boolean isNameForRegisteredFile(final String s) {
        final String fileNameForFontName = this.getFileNameForFontName(s);
        return fileNameForFontName != null && this.registeredFontFiles.contains(fileNameForFontName);
    }
    
    public void createCompositeFonts(final ConcurrentHashMap<String, Font2D> concurrentHashMap, final boolean b, final boolean b2) {
        this.initCompositeFonts(this.createFontConfiguration(b, b2), concurrentHashMap);
    }
    
    @Override
    public Font[] getAllInstalledFonts() {
        if (this.allFonts == null) {
            this.loadFonts();
            final TreeMap treeMap = new TreeMap();
            final Font2D[] registeredFonts = this.getRegisteredFonts();
            for (int i = 0; i < registeredFonts.length; ++i) {
                if (!(registeredFonts[i] instanceof NativeFont)) {
                    treeMap.put(registeredFonts[i].getFontName(null), registeredFonts[i]);
                }
            }
            final String[] fontNamesFromPlatform = this.getFontNamesFromPlatform();
            if (fontNamesFromPlatform != null) {
                for (int j = 0; j < fontNamesFromPlatform.length; ++j) {
                    if (!this.isNameForRegisteredFile(fontNamesFromPlatform[j])) {
                        treeMap.put(fontNamesFromPlatform[j], null);
                    }
                }
            }
            String[] array = null;
            if (treeMap.size() > 0) {
                array = new String[treeMap.size()];
                final Object[] array2 = treeMap.keySet().toArray();
                for (int k = 0; k < array2.length; ++k) {
                    array[k] = (String)array2[k];
                }
            }
            final Font[] allFonts = new Font[array.length];
            for (int l = 0; l < array.length; ++l) {
                allFonts[l] = new Font(array[l], 0, 1);
                final Font2D font2D = treeMap.get(array[l]);
                if (font2D != null) {
                    FontAccess.getFontAccess().setFont2D(allFonts[l], font2D.handle);
                }
            }
            this.allFonts = allFonts;
        }
        final Font[] array3 = new Font[this.allFonts.length];
        System.arraycopy(this.allFonts, 0, array3, 0, this.allFonts.length);
        return array3;
    }
    
    @Override
    public String[] getInstalledFontFamilyNames(Locale default1) {
        if (default1 == null) {
            default1 = Locale.getDefault();
        }
        if (this.allFamilies != null && this.lastDefaultLocale != null && default1.equals(this.lastDefaultLocale)) {
            final String[] array = new String[this.allFamilies.length];
            System.arraycopy(this.allFamilies, 0, array, 0, this.allFamilies.length);
            return array;
        }
        final TreeMap<String, String> treeMap = new TreeMap<String, String>();
        final String s = "Serif";
        treeMap.put(s.toLowerCase(), s);
        final String s2 = "SansSerif";
        treeMap.put(s2.toLowerCase(), s2);
        final String s3 = "Monospaced";
        treeMap.put(s3.toLowerCase(), s3);
        final String s4 = "Dialog";
        treeMap.put(s4.toLowerCase(), s4);
        final String s5 = "DialogInput";
        treeMap.put(s5.toLowerCase(), s5);
        if (default1.equals(getSystemStartupLocale()) && this.getFamilyNamesFromPlatform(treeMap, default1)) {
            this.getJREFontFamilyNames(treeMap, default1);
        }
        else {
            this.loadFontFiles();
            final PhysicalFont[] physicalFonts = this.getPhysicalFonts();
            for (int i = 0; i < physicalFonts.length; ++i) {
                if (!(physicalFonts[i] instanceof NativeFont)) {
                    final String familyName = physicalFonts[i].getFamilyName(default1);
                    treeMap.put(familyName.toLowerCase(default1), familyName);
                }
            }
        }
        this.addNativeFontFamilyNames(treeMap, default1);
        final String[] array2 = new String[treeMap.size()];
        final Object[] array3 = treeMap.keySet().toArray();
        for (int j = 0; j < array3.length; ++j) {
            array2[j] = treeMap.get(array3[j]);
        }
        if (default1.equals(Locale.getDefault())) {
            this.lastDefaultLocale = default1;
            System.arraycopy(array2, 0, this.allFamilies = new String[array2.length], 0, this.allFamilies.length);
        }
        return array2;
    }
    
    protected void addNativeFontFamilyNames(final TreeMap<String, String> treeMap, final Locale locale) {
    }
    
    public void register1dot0Fonts() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                SunFontManager.this.registerFontsInDir("/usr/openwin/lib/X11/fonts/Type1", true, 4, false, false);
                return null;
            }
        });
    }
    
    protected void getJREFontFamilyNames(final TreeMap<String, String> treeMap, final Locale locale) {
        this.registerDeferredJREFonts(SunFontManager.jreFontDirName);
        final PhysicalFont[] physicalFonts = this.getPhysicalFonts();
        for (int i = 0; i < physicalFonts.length; ++i) {
            if (!(physicalFonts[i] instanceof NativeFont)) {
                final String familyName = physicalFonts[i].getFamilyName(locale);
                treeMap.put(familyName.toLowerCase(locale), familyName);
            }
        }
    }
    
    private static Locale getSystemStartupLocale() {
        if (SunFontManager.systemLocale == null) {
            SunFontManager.systemLocale = AccessController.doPrivileged((PrivilegedAction<Locale>)new PrivilegedAction() {
                @Override
                public Object run() {
                    final String property = System.getProperty("file.encoding", "");
                    final String property2 = System.getProperty("sun.jnu.encoding");
                    if (property2 != null && !property2.equals(property)) {
                        return Locale.ROOT;
                    }
                    return new Locale(System.getProperty("user.language", "en"), System.getProperty("user.country", ""), System.getProperty("user.variant", ""));
                }
            });
        }
        return SunFontManager.systemLocale;
    }
    
    void addToPool(final FileFont fileFont) {
        FileFont fileFont2 = null;
        int n = -1;
        synchronized (this.fontFileCache) {
            for (int i = 0; i < 20; ++i) {
                if (this.fontFileCache[i] == fileFont) {
                    return;
                }
                if (this.fontFileCache[i] == null && n < 0) {
                    n = i;
                }
            }
            if (n >= 0) {
                this.fontFileCache[n] = fileFont;
                return;
            }
            fileFont2 = this.fontFileCache[this.lastPoolIndex];
            this.fontFileCache[this.lastPoolIndex] = fileFont;
            this.lastPoolIndex = (this.lastPoolIndex + 1) % 20;
        }
        if (fileFont2 != null) {
            fileFont2.close();
        }
    }
    
    protected FontUIResource getFontConfigFUIR(final String s, final int n, final int n2) {
        return new FontUIResource(s, n, n2);
    }
    
    static {
        SunFontManager.missingFontFiles = null;
        ttFilter = new TTFilter();
        t1Filter = new T1Filter();
        SunFontManager.STR_ARRAY = new String[0];
        SunFontManager.maxSoftRefCnt = 10;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                FontManagerNativeLibrary.load();
                initIDs();
                switch (StrikeCache.nativeAddressSize) {
                    case 8: {
                        SunFontManager.longAddresses = true;
                        break;
                    }
                    case 4: {
                        SunFontManager.longAddresses = false;
                        break;
                    }
                    default: {
                        throw new RuntimeException("Unexpected address size");
                    }
                }
                SunFontManager.noType1Font = "true".equals(System.getProperty("sun.java2d.noType1Font"));
                SunFontManager.jreLibDirName = System.getProperty("java.home", "") + File.separator + "lib";
                SunFontManager.jreFontDirName = SunFontManager.jreLibDirName + File.separator + "fonts";
                final File file = new File(SunFontManager.jreFontDirName + File.separator + "LucidaSansRegular.ttf");
                SunFontManager.maxSoftRefCnt = Integer.getInteger("sun.java2d.font.maxSoftRefs", 10);
                return null;
            }
        });
        altJAFontKey = new Object();
        localeFontKey = new Object();
        proportionalFontKey = new Object();
        SunFontManager.gAltJAFont = false;
        SunFontManager.installedNames = null;
        regFamilyKey = new Object();
        regFullNameKey = new Object();
        SunFontManager.systemLocale = null;
    }
    
    private static class TTFilter implements FilenameFilter
    {
        @Override
        public boolean accept(final File file, final String s) {
            final int n = s.length() - 4;
            return n > 0 && (s.startsWith(".ttf", n) || s.startsWith(".TTF", n) || s.startsWith(".ttc", n) || s.startsWith(".TTC", n) || s.startsWith(".otf", n) || s.startsWith(".OTF", n));
        }
    }
    
    private static class T1Filter implements FilenameFilter
    {
        @Override
        public boolean accept(final File file, final String s) {
            if (SunFontManager.noType1Font) {
                return false;
            }
            final int n = s.length() - 4;
            return n > 0 && (s.startsWith(".pfa", n) || s.startsWith(".pfb", n) || s.startsWith(".PFA", n) || s.startsWith(".PFB", n));
        }
    }
    
    private static class TTorT1Filter implements FilenameFilter
    {
        @Override
        public boolean accept(final File file, final String s) {
            final int n = s.length() - 4;
            return n > 0 && (s.startsWith(".ttf", n) || s.startsWith(".TTF", n) || s.startsWith(".ttc", n) || s.startsWith(".TTC", n) || s.startsWith(".otf", n) || s.startsWith(".OTF", n) || (!SunFontManager.noType1Font && (s.startsWith(".pfa", n) || s.startsWith(".pfb", n) || s.startsWith(".PFA", n) || s.startsWith(".PFB", n))));
        }
    }
    
    private static final class FontRegistrationInfo
    {
        String fontFilePath;
        String[] nativeNames;
        int fontFormat;
        boolean javaRasterizer;
        int fontRank;
        
        FontRegistrationInfo(final String fontFilePath, final String[] nativeNames, final int fontFormat, final boolean javaRasterizer, final int fontRank) {
            this.fontFilePath = fontFilePath;
            this.nativeNames = nativeNames;
            this.fontFormat = fontFormat;
            this.javaRasterizer = javaRasterizer;
            this.fontRank = fontRank;
        }
    }
    
    public static class FamilyDescription
    {
        public String familyName;
        public String plainFullName;
        public String boldFullName;
        public String italicFullName;
        public String boldItalicFullName;
        public String plainFileName;
        public String boldFileName;
        public String italicFileName;
        public String boldItalicFileName;
    }
}
