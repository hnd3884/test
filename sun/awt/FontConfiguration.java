package sun.awt;

import java.util.Properties;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;
import sun.font.CompositeFontDescriptor;
import java.nio.charset.CharsetEncoder;
import java.util.Vector;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.nio.charset.Charset;
import sun.font.FontUtilities;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.io.File;
import sun.font.SunFontManager;
import sun.util.logging.PlatformLogger;
import java.util.Hashtable;
import java.util.Locale;

public abstract class FontConfiguration
{
    protected static String osVersion;
    protected static String osName;
    protected static String encoding;
    protected static Locale startupLocale;
    protected static Hashtable localeMap;
    private static FontConfiguration fontConfig;
    private static PlatformLogger logger;
    protected static boolean isProperties;
    protected SunFontManager fontManager;
    protected boolean preferLocaleFonts;
    protected boolean preferPropFonts;
    private File fontConfigFile;
    private boolean foundOsSpecificFile;
    private boolean inited;
    private String javaLib;
    private static short stringIDNum;
    private static short[] stringIDs;
    private static StringBuilder stringTable;
    public static boolean verbose;
    private short initELC;
    private Locale initLocale;
    private String initEncoding;
    private String alphabeticSuffix;
    private short[][][] compFontNameIDs;
    private int[][][] compExclusions;
    private int[] compCoreNum;
    private Set<Short> coreFontNameIDs;
    private Set<Short> fallbackFontNameIDs;
    protected static final int NUM_FONTS = 5;
    protected static final int NUM_STYLES = 4;
    protected static final String[] fontNames;
    protected static final String[] publicFontNames;
    protected static final String[] styleNames;
    protected static String[] installedFallbackFontFiles;
    protected HashMap reorderMap;
    private Hashtable charsetRegistry;
    private FontDescriptor[][][] fontDescriptors;
    HashMap<String, Boolean> existsMap;
    private int numCoreFonts;
    private String[] componentFonts;
    HashMap<String, String> filenamesMap;
    HashSet<String> coreFontFileNames;
    private static final int HEAD_LENGTH = 20;
    private static final int INDEX_scriptIDs = 0;
    private static final int INDEX_scriptFonts = 1;
    private static final int INDEX_elcIDs = 2;
    private static final int INDEX_sequences = 3;
    private static final int INDEX_fontfileNameIDs = 4;
    private static final int INDEX_componentFontNameIDs = 5;
    private static final int INDEX_filenames = 6;
    private static final int INDEX_awtfontpaths = 7;
    private static final int INDEX_exclusions = 8;
    private static final int INDEX_proportionals = 9;
    private static final int INDEX_scriptFontsMotif = 10;
    private static final int INDEX_alphabeticSuffix = 11;
    private static final int INDEX_stringIDs = 12;
    private static final int INDEX_stringTable = 13;
    private static final int INDEX_TABLEEND = 14;
    private static final int INDEX_fallbackScripts = 15;
    private static final int INDEX_appendedfontpath = 16;
    private static final int INDEX_version = 17;
    private static short[] head;
    private static short[] table_scriptIDs;
    private static short[] table_scriptFonts;
    private static short[] table_elcIDs;
    private static short[] table_sequences;
    private static short[] table_fontfileNameIDs;
    private static short[] table_componentFontNameIDs;
    private static short[] table_filenames;
    protected static short[] table_awtfontpaths;
    private static short[] table_exclusions;
    private static short[] table_proportionals;
    private static short[] table_scriptFontsMotif;
    private static short[] table_alphabeticSuffix;
    private static short[] table_stringIDs;
    private static char[] table_stringTable;
    private HashMap<String, Short> reorderScripts;
    private static String[] stringCache;
    private static final int[] EMPTY_INT_ARRAY;
    private static final String[] EMPTY_STRING_ARRAY;
    private static final short[] EMPTY_SHORT_ARRAY;
    private static final String UNDEFINED_COMPONENT_FONT = "unknown";
    
    public FontConfiguration(final SunFontManager fontManager) {
        this.initELC = -1;
        this.compFontNameIDs = new short[5][4][];
        this.compExclusions = new int[5][][];
        this.compCoreNum = new int[5];
        this.coreFontNameIDs = new HashSet<Short>();
        this.fallbackFontNameIDs = new HashSet<Short>();
        this.reorderMap = null;
        this.charsetRegistry = new Hashtable(5);
        this.fontDescriptors = new FontDescriptor[5][4][];
        this.numCoreFonts = -1;
        this.componentFonts = null;
        this.filenamesMap = new HashMap<String, String>();
        this.coreFontFileNames = new HashSet<String>();
        if (FontUtilities.debugFonts()) {
            FontUtilities.getLogger().info("Creating standard Font Configuration");
        }
        if (FontUtilities.debugFonts() && FontConfiguration.logger == null) {
            FontConfiguration.logger = PlatformLogger.getLogger("sun.awt.FontConfiguration");
        }
        this.fontManager = fontManager;
        this.setOsNameAndVersion();
        this.setEncoding();
        this.findFontConfigFile();
    }
    
    public synchronized boolean init() {
        if (!this.inited) {
            this.preferLocaleFonts = false;
            this.preferPropFonts = false;
            this.setFontConfiguration();
            this.readFontConfigFile(this.fontConfigFile);
            this.initFontConfig();
            this.inited = true;
        }
        return true;
    }
    
    public FontConfiguration(final SunFontManager fontManager, final boolean preferLocaleFonts, final boolean preferPropFonts) {
        this.initELC = -1;
        this.compFontNameIDs = new short[5][4][];
        this.compExclusions = new int[5][][];
        this.compCoreNum = new int[5];
        this.coreFontNameIDs = new HashSet<Short>();
        this.fallbackFontNameIDs = new HashSet<Short>();
        this.reorderMap = null;
        this.charsetRegistry = new Hashtable(5);
        this.fontDescriptors = new FontDescriptor[5][4][];
        this.numCoreFonts = -1;
        this.componentFonts = null;
        this.filenamesMap = new HashMap<String, String>();
        this.coreFontFileNames = new HashSet<String>();
        this.fontManager = fontManager;
        if (FontUtilities.debugFonts()) {
            FontUtilities.getLogger().info("Creating alternate Font Configuration");
        }
        this.preferLocaleFonts = preferLocaleFonts;
        this.preferPropFonts = preferPropFonts;
        this.initFontConfig();
    }
    
    protected void setOsNameAndVersion() {
        FontConfiguration.osName = System.getProperty("os.name");
        FontConfiguration.osVersion = System.getProperty("os.version");
    }
    
    private void setEncoding() {
        FontConfiguration.encoding = Charset.defaultCharset().name();
        FontConfiguration.startupLocale = SunToolkit.getStartupLocale();
    }
    
    public boolean foundOsSpecificFile() {
        return this.foundOsSpecificFile;
    }
    
    public boolean fontFilesArePresent() {
        this.init();
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            final /* synthetic */ String val$fileName = FontConfiguration.this.mapFileName(getComponentFileName(getComponentFileID(FontConfiguration.this.compFontNameIDs[0][0][0])));
            
            @Override
            public Object run() {
                try {
                    return new File(this.val$fileName).exists();
                }
                catch (final Exception ex) {
                    return false;
                }
            }
        });
    }
    
    private void findFontConfigFile() {
        this.foundOsSpecificFile = true;
        final String property = System.getProperty("java.home");
        if (property == null) {
            throw new Error("java.home property not set");
        }
        this.javaLib = property + File.separator + "lib";
        final String property2 = System.getProperty("sun.awt.fontconfig");
        if (property2 != null) {
            this.fontConfigFile = new File(property2);
        }
        else {
            this.fontConfigFile = this.findFontConfigFile(this.javaLib);
        }
    }
    
    private void readFontConfigFile(final File file) {
        this.getInstalledFallbackFonts(this.javaLib);
        if (file != null) {
            try {
                final FileInputStream fileInputStream = new FileInputStream(file.getPath());
                if (FontConfiguration.isProperties) {
                    loadProperties(fileInputStream);
                }
                else {
                    loadBinary(fileInputStream);
                }
                fileInputStream.close();
                if (FontUtilities.debugFonts()) {
                    FontConfiguration.logger.config("Read logical font configuration from " + file);
                }
            }
            catch (final IOException ex) {
                if (FontUtilities.debugFonts()) {
                    FontConfiguration.logger.config("Failed to read logical font configuration from " + file);
                }
            }
        }
        final String version = this.getVersion();
        if (!"1".equals(version) && FontUtilities.debugFonts()) {
            FontConfiguration.logger.config("Unsupported fontconfig version: " + version);
        }
    }
    
    protected void getInstalledFallbackFonts(final String s) {
        final String string = s + File.separator + "fonts" + File.separator + "fallback";
        final File file = new File(string);
        if (file.exists() && file.isDirectory()) {
            final String[] list = file.list(this.fontManager.getTrueTypeFilter());
            final String[] list2 = file.list(this.fontManager.getType1Filter());
            final int n = (list == null) ? 0 : list.length;
            final int n2 = (list2 == null) ? 0 : list2.length;
            final int n3 = n + n2;
            if (n + n2 == 0) {
                return;
            }
            FontConfiguration.installedFallbackFontFiles = new String[n3];
            for (int i = 0; i < n; ++i) {
                FontConfiguration.installedFallbackFontFiles[i] = file + File.separator + list[i];
            }
            for (int j = 0; j < n2; ++j) {
                FontConfiguration.installedFallbackFontFiles[j + n] = file + File.separator + list2[j];
            }
            this.fontManager.registerFontsInDir(string);
        }
    }
    
    private File findImpl(final String s) {
        final File file = new File(s + ".properties");
        if (file.canRead()) {
            FontConfiguration.isProperties = true;
            return file;
        }
        final File file2 = new File(s + ".bfc");
        if (file2.canRead()) {
            FontConfiguration.isProperties = false;
            return file2;
        }
        return null;
    }
    
    private File findFontConfigFile(final String s) {
        final String string = s + File.separator + "fontconfig";
        String substring = null;
        if (FontConfiguration.osVersion != null && FontConfiguration.osName != null) {
            final File impl = this.findImpl(string + "." + FontConfiguration.osName + "." + FontConfiguration.osVersion);
            if (impl != null) {
                return impl;
            }
            if (FontConfiguration.osVersion.indexOf(".") != -1) {
                substring = FontConfiguration.osVersion.substring(0, FontConfiguration.osVersion.indexOf("."));
                final File impl2 = this.findImpl(string + "." + FontConfiguration.osName + "." + substring);
                if (impl2 != null) {
                    return impl2;
                }
            }
        }
        if (FontConfiguration.osName != null) {
            final File impl3 = this.findImpl(string + "." + FontConfiguration.osName);
            if (impl3 != null) {
                return impl3;
            }
        }
        if (FontConfiguration.osVersion != null) {
            final File impl4 = this.findImpl(string + "." + FontConfiguration.osVersion);
            if (impl4 != null) {
                return impl4;
            }
            if (substring != null) {
                final File impl5 = this.findImpl(string + "." + substring);
                if (impl5 != null) {
                    return impl5;
                }
            }
        }
        this.foundOsSpecificFile = false;
        final File impl6 = this.findImpl(string);
        if (impl6 != null) {
            return impl6;
        }
        return null;
    }
    
    public static void loadBinary(final InputStream inputStream) throws IOException {
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        FontConfiguration.head = readShortTable(dataInputStream, 20);
        final int[] array = new int[14];
        for (int i = 0; i < 14; ++i) {
            array[i] = FontConfiguration.head[i + 1] - FontConfiguration.head[i];
        }
        FontConfiguration.table_scriptIDs = readShortTable(dataInputStream, array[0]);
        FontConfiguration.table_scriptFonts = readShortTable(dataInputStream, array[1]);
        FontConfiguration.table_elcIDs = readShortTable(dataInputStream, array[2]);
        FontConfiguration.table_sequences = readShortTable(dataInputStream, array[3]);
        FontConfiguration.table_fontfileNameIDs = readShortTable(dataInputStream, array[4]);
        FontConfiguration.table_componentFontNameIDs = readShortTable(dataInputStream, array[5]);
        FontConfiguration.table_filenames = readShortTable(dataInputStream, array[6]);
        FontConfiguration.table_awtfontpaths = readShortTable(dataInputStream, array[7]);
        FontConfiguration.table_exclusions = readShortTable(dataInputStream, array[8]);
        FontConfiguration.table_proportionals = readShortTable(dataInputStream, array[9]);
        FontConfiguration.table_scriptFontsMotif = readShortTable(dataInputStream, array[10]);
        FontConfiguration.table_alphabeticSuffix = readShortTable(dataInputStream, array[11]);
        FontConfiguration.table_stringIDs = readShortTable(dataInputStream, array[12]);
        FontConfiguration.stringCache = new String[FontConfiguration.table_stringIDs.length + 1];
        final int n = array[13];
        final byte[] array2 = new byte[n * 2];
        FontConfiguration.table_stringTable = new char[n];
        dataInputStream.read(array2);
        for (int j = 0, n2 = 0; j < n; FontConfiguration.table_stringTable[j++] = (char)(array2[n2++] << 8 | (array2[n2++] & 0xFF))) {}
        if (FontConfiguration.verbose) {
            dump();
        }
    }
    
    public static void saveBinary(final OutputStream outputStream) throws IOException {
        sanityCheck();
        final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        writeShortTable(dataOutputStream, FontConfiguration.head);
        writeShortTable(dataOutputStream, FontConfiguration.table_scriptIDs);
        writeShortTable(dataOutputStream, FontConfiguration.table_scriptFonts);
        writeShortTable(dataOutputStream, FontConfiguration.table_elcIDs);
        writeShortTable(dataOutputStream, FontConfiguration.table_sequences);
        writeShortTable(dataOutputStream, FontConfiguration.table_fontfileNameIDs);
        writeShortTable(dataOutputStream, FontConfiguration.table_componentFontNameIDs);
        writeShortTable(dataOutputStream, FontConfiguration.table_filenames);
        writeShortTable(dataOutputStream, FontConfiguration.table_awtfontpaths);
        writeShortTable(dataOutputStream, FontConfiguration.table_exclusions);
        writeShortTable(dataOutputStream, FontConfiguration.table_proportionals);
        writeShortTable(dataOutputStream, FontConfiguration.table_scriptFontsMotif);
        writeShortTable(dataOutputStream, FontConfiguration.table_alphabeticSuffix);
        writeShortTable(dataOutputStream, FontConfiguration.table_stringIDs);
        dataOutputStream.writeChars(new String(FontConfiguration.table_stringTable));
        outputStream.close();
        if (FontConfiguration.verbose) {
            dump();
        }
    }
    
    public static void loadProperties(final InputStream inputStream) throws IOException {
        FontConfiguration.stringIDNum = 1;
        FontConfiguration.stringIDs = new short[1000];
        FontConfiguration.stringTable = new StringBuilder(4096);
        if (FontConfiguration.verbose && FontConfiguration.logger == null) {
            FontConfiguration.logger = PlatformLogger.getLogger("sun.awt.FontConfiguration");
        }
        new PropertiesHandler().load(inputStream);
        FontConfiguration.stringIDs = null;
        FontConfiguration.stringTable = null;
    }
    
    private void initFontConfig() {
        this.initLocale = FontConfiguration.startupLocale;
        this.initEncoding = FontConfiguration.encoding;
        if (this.preferLocaleFonts && !willReorderForStartupLocale()) {
            this.preferLocaleFonts = false;
        }
        this.initELC = this.getInitELC();
        this.initAllComponentFonts();
    }
    
    private short getInitELC() {
        if (this.initELC != -1) {
            return this.initELC;
        }
        final HashMap hashMap = new HashMap();
        for (int i = 0; i < FontConfiguration.table_elcIDs.length; ++i) {
            hashMap.put(getString(FontConfiguration.table_elcIDs[i]), i);
        }
        final String language = this.initLocale.getLanguage();
        String s;
        if (hashMap.containsKey(s = this.initEncoding + "." + language + "." + this.initLocale.getCountry()) || hashMap.containsKey(s = this.initEncoding + "." + language) || hashMap.containsKey(s = this.initEncoding)) {
            this.initELC = ((Integer)hashMap.get(s)).shortValue();
        }
        else {
            this.initELC = ((Integer)hashMap.get("NULL.NULL.NULL")).shortValue();
        }
        for (int j = 0; j < FontConfiguration.table_alphabeticSuffix.length; j += 2) {
            if (this.initELC == FontConfiguration.table_alphabeticSuffix[j]) {
                this.alphabeticSuffix = getString(FontConfiguration.table_alphabeticSuffix[j + 1]);
                return this.initELC;
            }
        }
        return this.initELC;
    }
    
    private void initAllComponentFonts() {
        final short[] fallbackScripts = getFallbackScripts();
        for (int i = 0; i < 5; ++i) {
            final short[] coreScripts = this.getCoreScripts(i);
            this.compCoreNum[i] = coreScripts.length;
            final int[][] array = new int[coreScripts.length][];
            for (int j = 0; j < coreScripts.length; ++j) {
                array[j] = getExclusionRanges(coreScripts[j]);
            }
            this.compExclusions[i] = array;
            for (int k = 0; k < 4; ++k) {
                short[] array2 = new short[coreScripts.length + fallbackScripts.length];
                int l;
                for (l = 0; l < coreScripts.length; ++l) {
                    array2[l] = getComponentFontID(coreScripts[l], i, k);
                    if (this.preferLocaleFonts && FontConfiguration.localeMap != null && this.fontManager.usingAlternateFontforJALocales()) {
                        array2[l] = this.remapLocaleMap(i, k, coreScripts[l], array2[l]);
                    }
                    if (this.preferPropFonts) {
                        array2[l] = this.remapProportional(i, array2[l]);
                    }
                    this.coreFontNameIDs.add(array2[l]);
                }
                for (int n = 0; n < fallbackScripts.length; ++n) {
                    short n2 = getComponentFontID(fallbackScripts[n], i, k);
                    if (this.preferLocaleFonts && FontConfiguration.localeMap != null && this.fontManager.usingAlternateFontforJALocales()) {
                        n2 = this.remapLocaleMap(i, k, fallbackScripts[n], n2);
                    }
                    if (this.preferPropFonts) {
                        n2 = this.remapProportional(i, n2);
                    }
                    if (!contains(array2, n2, l)) {
                        this.fallbackFontNameIDs.add(n2);
                        array2[l++] = n2;
                    }
                }
                if (l < array2.length) {
                    final short[] array3 = new short[l];
                    System.arraycopy(array2, 0, array3, 0, l);
                    array2 = array3;
                }
                this.compFontNameIDs[i][k] = array2;
            }
        }
    }
    
    private short remapLocaleMap(final int n, final int n2, final short n3, short n4) {
        final String string = getString(FontConfiguration.table_scriptIDs[n3]);
        String s = FontConfiguration.localeMap.get(string);
        if (s == null) {
            s = FontConfiguration.localeMap.get(FontConfiguration.fontNames[n] + "." + FontConfiguration.styleNames[n2] + "." + string);
        }
        if (s == null) {
            return n4;
        }
        for (int i = 0; i < FontConfiguration.table_componentFontNameIDs.length; ++i) {
            if (s.equalsIgnoreCase(getString(FontConfiguration.table_componentFontNameIDs[i]))) {
                n4 = (short)i;
                break;
            }
        }
        return n4;
    }
    
    public static boolean hasMonoToPropMap() {
        return FontConfiguration.table_proportionals != null && FontConfiguration.table_proportionals.length != 0;
    }
    
    private short remapProportional(final int n, final short n2) {
        if (this.preferPropFonts && FontConfiguration.table_proportionals.length != 0 && n != 2 && n != 4) {
            for (int i = 0; i < FontConfiguration.table_proportionals.length; i += 2) {
                if (FontConfiguration.table_proportionals[i] == n2) {
                    return FontConfiguration.table_proportionals[i + 1];
                }
            }
        }
        return n2;
    }
    
    public static boolean isLogicalFontFamilyName(final String s) {
        return isLogicalFontFamilyNameLC(s.toLowerCase(Locale.ENGLISH));
    }
    
    public static boolean isLogicalFontFamilyNameLC(final String s) {
        for (int i = 0; i < FontConfiguration.fontNames.length; ++i) {
            if (s.equals(FontConfiguration.fontNames[i])) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isLogicalFontStyleName(final String s) {
        for (int i = 0; i < FontConfiguration.styleNames.length; ++i) {
            if (s.equals(FontConfiguration.styleNames[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isLogicalFontFaceName(final String s) {
        return isLogicalFontFaceNameLC(s.toLowerCase(Locale.ENGLISH));
    }
    
    public static boolean isLogicalFontFaceNameLC(final String s) {
        final int index = s.indexOf(46);
        if (index >= 0) {
            final String substring = s.substring(0, index);
            final String substring2 = s.substring(index + 1);
            return isLogicalFontFamilyName(substring) && isLogicalFontStyleName(substring2);
        }
        return isLogicalFontFamilyName(s);
    }
    
    protected static int getFontIndex(final String s) {
        return getArrayIndex(FontConfiguration.fontNames, s);
    }
    
    protected static int getStyleIndex(final String s) {
        return getArrayIndex(FontConfiguration.styleNames, s);
    }
    
    private static int getArrayIndex(final String[] array, final String s) {
        for (int i = 0; i < array.length; ++i) {
            if (s.equals(array[i])) {
                return i;
            }
        }
        assert false;
        return 0;
    }
    
    protected static int getStyleIndex(final int n) {
        switch (n) {
            case 0: {
                return 0;
            }
            case 1: {
                return 1;
            }
            case 2: {
                return 2;
            }
            case 3: {
                return 3;
            }
            default: {
                return 0;
            }
        }
    }
    
    protected static String getFontName(final int n) {
        return FontConfiguration.fontNames[n];
    }
    
    protected static String getStyleName(final int n) {
        return FontConfiguration.styleNames[n];
    }
    
    public static String getLogicalFontFaceName(final String s, final int n) {
        assert isLogicalFontFamilyName(s);
        return s.toLowerCase(Locale.ENGLISH) + "." + getStyleString(n);
    }
    
    public static String getStyleString(final int n) {
        return getStyleName(getStyleIndex(n));
    }
    
    public abstract String getFallbackFamilyName(final String p0, final String p1);
    
    protected String getCompatibilityFamilyName(String lowerCase) {
        lowerCase = lowerCase.toLowerCase(Locale.ENGLISH);
        if (lowerCase.equals("timesroman")) {
            return "serif";
        }
        if (lowerCase.equals("helvetica")) {
            return "sansserif";
        }
        if (lowerCase.equals("courier")) {
            return "monospaced";
        }
        return null;
    }
    
    protected String mapFileName(final String s) {
        return s;
    }
    
    protected abstract void initReorderMap();
    
    private void shuffle(final String[] array, final int n, final int n2) {
        if (n2 >= n) {
            return;
        }
        final String s = array[n];
        for (int i = n; i > n2; --i) {
            array[i] = array[i - 1];
        }
        array[n2] = s;
    }
    
    public static boolean willReorderForStartupLocale() {
        return getReorderSequence() != null;
    }
    
    private static Object getReorderSequence() {
        if (FontConfiguration.fontConfig.reorderMap == null) {
            FontConfiguration.fontConfig.initReorderMap();
        }
        final HashMap reorderMap = FontConfiguration.fontConfig.reorderMap;
        final String language = FontConfiguration.startupLocale.getLanguage();
        Object o = reorderMap.get(FontConfiguration.encoding + "." + language + "." + FontConfiguration.startupLocale.getCountry());
        if (o == null) {
            o = reorderMap.get(FontConfiguration.encoding + "." + language);
        }
        if (o == null) {
            o = reorderMap.get(FontConfiguration.encoding);
        }
        return o;
    }
    
    private void reorderSequenceForLocale(final String[] array) {
        final Object reorderSequence = getReorderSequence();
        if (reorderSequence instanceof String) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i].equals(reorderSequence)) {
                    this.shuffle(array, i, 0);
                    return;
                }
            }
        }
        else if (reorderSequence instanceof String[]) {
            final String[] array2 = (String[])reorderSequence;
            for (int j = 0; j < array2.length; ++j) {
                for (int k = 0; k < array.length; ++k) {
                    if (array[k].equals(array2[j])) {
                        this.shuffle(array, k, j);
                    }
                }
            }
        }
    }
    
    private static Vector splitSequence(final String s) {
        final Vector vector = new Vector();
        int n;
        int index;
        for (n = 0; (index = s.indexOf(44, n)) >= 0; n = index + 1) {
            vector.add(s.substring(n, index));
        }
        if (s.length() > n) {
            vector.add(s.substring(n, s.length()));
        }
        return vector;
    }
    
    protected String[] split(final String s) {
        return splitSequence(s).toArray(new String[0]);
    }
    
    public FontDescriptor[] getFontDescriptors(String lowerCase, final int n) {
        assert isLogicalFontFamilyName(lowerCase);
        lowerCase = lowerCase.toLowerCase(Locale.ENGLISH);
        return this.getFontDescriptors(getFontIndex(lowerCase), getStyleIndex(n));
    }
    
    private FontDescriptor[] getFontDescriptors(final int n, final int n2) {
        FontDescriptor[] buildFontDescriptors = this.fontDescriptors[n][n2];
        if (buildFontDescriptors == null) {
            buildFontDescriptors = this.buildFontDescriptors(n, n2);
            this.fontDescriptors[n][n2] = buildFontDescriptors;
        }
        return buildFontDescriptors;
    }
    
    protected FontDescriptor[] buildFontDescriptors(final int n, final int n2) {
        final String s = FontConfiguration.fontNames[n];
        final String s2 = FontConfiguration.styleNames[n2];
        final short[] coreScripts = this.getCoreScripts(n);
        final short[] array = this.compFontNameIDs[n][n2];
        final String[] array2 = new String[coreScripts.length];
        final String[] array3 = new String[coreScripts.length];
        for (int i = 0; i < array2.length; ++i) {
            array3[i] = getComponentFontName(array[i]);
            array2[i] = getScriptName(coreScripts[i]);
            if (this.alphabeticSuffix != null && "alphabetic".equals(array2[i])) {
                array2[i] = array2[i] + "/" + this.alphabeticSuffix;
            }
        }
        final int[][] array4 = this.compExclusions[n];
        final FontDescriptor[] array5 = new FontDescriptor[array3.length];
        for (int j = 0; j < array3.length; ++j) {
            final String awtFontName = this.makeAWTFontName(array3[j], array2[j]);
            String encoding = this.getEncoding(array3[j], array2[j]);
            if (encoding == null) {
                encoding = "default";
            }
            array5[j] = new FontDescriptor(awtFontName, this.getFontCharsetEncoder(encoding.trim(), awtFontName), array4[j]);
        }
        return array5;
    }
    
    protected String makeAWTFontName(final String s, final String s2) {
        return s;
    }
    
    protected abstract String getEncoding(final String p0, final String p1);
    
    private CharsetEncoder getFontCharsetEncoder(final String s, final String s2) {
        Charset charset;
        if (s.equals("default")) {
            charset = this.charsetRegistry.get(s2);
        }
        else {
            charset = this.charsetRegistry.get(s);
        }
        if (charset != null) {
            return charset.newEncoder();
        }
        if (!s.startsWith("sun.awt.") && !s.equals("default")) {
            charset = Charset.forName(s);
        }
        else {
            final Class clazz = AccessController.doPrivileged((PrivilegedAction<Class>)new PrivilegedAction() {
                @Override
                public Object run() {
                    try {
                        return Class.forName(s, true, ClassLoader.getSystemClassLoader());
                    }
                    catch (final ClassNotFoundException ex) {
                        return null;
                    }
                }
            });
            if (clazz != null) {
                try {
                    charset = (Charset)clazz.newInstance();
                }
                catch (final Exception ex) {}
            }
        }
        if (charset == null) {
            charset = this.getDefaultFontCharset(s2);
        }
        if (s.equals("default")) {
            this.charsetRegistry.put(s2, charset);
        }
        else {
            this.charsetRegistry.put(s, charset);
        }
        return charset.newEncoder();
    }
    
    protected abstract Charset getDefaultFontCharset(final String p0);
    
    public HashSet<String> getAWTFontPathSet() {
        return null;
    }
    
    public CompositeFontDescriptor[] get2DCompositeFontInfo() {
        final CompositeFontDescriptor[] array = new CompositeFontDescriptor[20];
        final String defaultFontFile = this.fontManager.getDefaultFontFile();
        final String defaultFontFaceName = this.fontManager.getDefaultFontFaceName();
        for (int i = 0; i < 5; ++i) {
            final String s = FontConfiguration.publicFontNames[i];
            final int[][] array2 = this.compExclusions[i];
            int n = 0;
            for (int j = 0; j < array2.length; ++j) {
                n += array2[j].length;
            }
            final int[] array3 = new int[n];
            final int[] array4 = new int[array2.length];
            int n2 = 0;
            for (int k = 0; k < array2.length; ++k) {
                final int[] array5 = array2[k];
                for (int l = 0; l < array5.length; array3[n2++] = array5[l++], array3[n2++] = array5[l++]) {
                    final int n3 = array5[l];
                }
                array4[k] = n2;
            }
            for (int n4 = 0; n4 < 4; ++n4) {
                int length = this.compFontNameIDs[i][n4].length;
                int n5 = 0;
                if (FontConfiguration.installedFallbackFontFiles != null) {
                    length += FontConfiguration.installedFallbackFontFiles.length;
                }
                final String string = s + "." + FontConfiguration.styleNames[n4];
                String[] array6 = new String[length];
                String[] array7 = new String[length];
                int n6;
                for (n6 = 0; n6 < this.compFontNameIDs[i][n4].length; ++n6) {
                    final short n7 = this.compFontNameIDs[i][n4][n6];
                    final short componentFileID = getComponentFileID(n7);
                    array6[n6] = this.getFaceNameFromComponentFontName(getComponentFontName(n7));
                    array7[n6] = this.mapFileName(getComponentFileName(componentFileID));
                    if (array7[n6] == null || this.needToSearchForFile(array7[n6])) {
                        array7[n6] = this.getFileNameFromComponentFontName(getComponentFontName(n7));
                    }
                    if (n5 == 0 && defaultFontFile.equals(array7[n6])) {
                        n5 = 1;
                    }
                }
                if (n5 == 0) {
                    int length2 = 0;
                    if (FontConfiguration.installedFallbackFontFiles != null) {
                        length2 = FontConfiguration.installedFallbackFontFiles.length;
                    }
                    if (n6 + length2 == length) {
                        final String[] array8 = new String[length + 1];
                        System.arraycopy(array6, 0, array8, 0, n6);
                        array6 = array8;
                        final String[] array9 = new String[length + 1];
                        System.arraycopy(array7, 0, array9, 0, n6);
                        array7 = array9;
                    }
                    array6[n6] = defaultFontFaceName;
                    array7[n6] = defaultFontFile;
                    ++n6;
                }
                if (FontConfiguration.installedFallbackFontFiles != null) {
                    for (int n8 = 0; n8 < FontConfiguration.installedFallbackFontFiles.length; ++n8) {
                        array6[n6] = null;
                        array7[n6] = FontConfiguration.installedFallbackFontFiles[n8];
                        ++n6;
                    }
                }
                if (n6 < length) {
                    final String[] array10 = new String[n6];
                    System.arraycopy(array6, 0, array10, 0, n6);
                    array6 = array10;
                    final String[] array11 = new String[n6];
                    System.arraycopy(array7, 0, array11, 0, n6);
                    array7 = array11;
                }
                int[] array12 = array4;
                if (n6 != array12.length) {
                    final int length3 = array4.length;
                    array12 = new int[n6];
                    System.arraycopy(array4, 0, array12, 0, length3);
                    for (int n9 = length3; n9 < n6; ++n9) {
                        array12[n9] = array3.length;
                    }
                }
                array[i * 4 + n4] = new CompositeFontDescriptor(string, this.compCoreNum[i], array6, array7, array3, array12);
            }
        }
        return array;
    }
    
    protected abstract String getFaceNameFromComponentFontName(final String p0);
    
    protected abstract String getFileNameFromComponentFontName(final String p0);
    
    public boolean needToSearchForFile(final String s) {
        if (!FontUtilities.isLinux) {
            return false;
        }
        if (this.existsMap == null) {
            this.existsMap = new HashMap<String, Boolean>();
        }
        Boolean b = this.existsMap.get(s);
        if (b == null) {
            this.getNumberCoreFonts();
            if (!this.coreFontFileNames.contains(s)) {
                b = Boolean.TRUE;
            }
            else {
                b = new File(s).exists();
                this.existsMap.put(s, b);
                if (FontUtilities.debugFonts() && b == Boolean.FALSE) {
                    FontConfiguration.logger.warning("Couldn't locate font file " + s);
                }
            }
        }
        return b == Boolean.FALSE;
    }
    
    public int getNumberCoreFonts() {
        if (this.numCoreFonts == -1) {
            this.numCoreFonts = this.coreFontNameIDs.size();
            final Short[] array = new Short[0];
            final Short[] array2 = this.coreFontNameIDs.toArray(array);
            final Short[] array3 = this.fallbackFontNameIDs.toArray(array);
            int n = 0;
            for (int i = 0; i < array3.length; ++i) {
                if (this.coreFontNameIDs.contains(array3[i])) {
                    array3[i] = null;
                }
                else {
                    ++n;
                }
            }
            this.componentFonts = new String[this.numCoreFonts + n];
            int j;
            for (j = 0; j < array2.length; ++j) {
                final short shortValue = array2[j];
                final short componentFileID = getComponentFileID(shortValue);
                this.componentFonts[j] = getComponentFontName(shortValue);
                final String componentFileName = getComponentFileName(componentFileID);
                if (componentFileName != null) {
                    this.coreFontFileNames.add(componentFileName);
                }
                this.filenamesMap.put(this.componentFonts[j], this.mapFileName(componentFileName));
            }
            for (int k = 0; k < array3.length; ++k) {
                if (array3[k] != null) {
                    final short shortValue2 = array3[k];
                    final short componentFileID2 = getComponentFileID(shortValue2);
                    this.componentFonts[j] = getComponentFontName(shortValue2);
                    this.filenamesMap.put(this.componentFonts[j], this.mapFileName(getComponentFileName(componentFileID2)));
                    ++j;
                }
            }
        }
        return this.numCoreFonts;
    }
    
    public String[] getPlatformFontNames() {
        if (this.numCoreFonts == -1) {
            this.getNumberCoreFonts();
        }
        return this.componentFonts;
    }
    
    public String getFileNameFromPlatformName(final String s) {
        return this.filenamesMap.get(s);
    }
    
    public String getExtraFontPath() {
        return getString(FontConfiguration.head[16]);
    }
    
    public String getVersion() {
        return getString(FontConfiguration.head[17]);
    }
    
    protected static FontConfiguration getFontConfiguration() {
        return FontConfiguration.fontConfig;
    }
    
    protected void setFontConfiguration() {
        FontConfiguration.fontConfig = this;
    }
    
    private static void sanityCheck() {
        int n = 0;
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            @Override
            public Object run() {
                return System.getProperty("os.name");
            }
        });
        for (int i = 1; i < FontConfiguration.table_filenames.length; ++i) {
            if (FontConfiguration.table_filenames[i] == -1) {
                if (s.contains("Windows")) {
                    System.err.println("\n Error: <filename." + getString(FontConfiguration.table_componentFontNameIDs[i]) + "> entry is missing!!!");
                    ++n;
                }
                else if (FontConfiguration.verbose && !isEmpty(FontConfiguration.table_filenames)) {
                    System.err.println("\n Note: 'filename' entry is undefined for \"" + getString(FontConfiguration.table_componentFontNameIDs[i]) + "\"");
                }
            }
        }
        for (int j = 0; j < FontConfiguration.table_scriptIDs.length; ++j) {
            final short n2 = FontConfiguration.table_scriptFonts[j];
            if (n2 == 0) {
                System.out.println("\n Error: <allfonts." + getString(FontConfiguration.table_scriptIDs[j]) + "> entry is missing!!!");
                ++n;
            }
            else if (n2 < 0) {
                final short n3 = (short)(-n2);
                for (int k = 0; k < 5; ++k) {
                    for (int l = 0; l < 4; ++l) {
                        if (FontConfiguration.table_scriptFonts[n3 + (k * 4 + l)] == 0) {
                            System.err.println("\n Error: <" + getFontName(k) + "." + getStyleName(l) + "." + getString(FontConfiguration.table_scriptIDs[j]) + "> entry is missing!!!");
                            ++n;
                        }
                    }
                }
            }
        }
        if ("SunOS".equals(s)) {
            for (int n4 = 0; n4 < FontConfiguration.table_awtfontpaths.length; ++n4) {
                if (FontConfiguration.table_awtfontpaths[n4] == 0) {
                    final String string = getString(FontConfiguration.table_scriptIDs[n4]);
                    if (!string.contains("lucida") && !string.contains("dingbats")) {
                        if (!string.contains("symbol")) {
                            System.err.println("\nError: <awtfontpath." + string + "> entry is missing!!!");
                            ++n;
                        }
                    }
                }
            }
        }
        if (n != 0) {
            System.err.println("!!THERE ARE " + n + " ERROR(S) IN THE FONTCONFIG FILE, PLEASE CHECK ITS CONTENT!!\n");
            System.exit(1);
        }
    }
    
    private static boolean isEmpty(final short[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] != -1) {
                return false;
            }
        }
        return true;
    }
    
    private static void dump() {
        System.out.println("\n----Head Table------------");
        for (int i = 0; i < 20; ++i) {
            System.out.println("  " + i + " : " + FontConfiguration.head[i]);
        }
        System.out.println("\n----scriptIDs-------------");
        printTable(FontConfiguration.table_scriptIDs, 0);
        System.out.println("\n----scriptFonts----------------");
        for (int j = 0; j < FontConfiguration.table_scriptIDs.length; ++j) {
            final short n = FontConfiguration.table_scriptFonts[j];
            if (n >= 0) {
                System.out.println("  allfonts." + getString(FontConfiguration.table_scriptIDs[j]) + "=" + getString(FontConfiguration.table_componentFontNameIDs[n]));
            }
        }
        for (int k = 0; k < FontConfiguration.table_scriptIDs.length; ++k) {
            final short n2 = FontConfiguration.table_scriptFonts[k];
            if (n2 < 0) {
                final short n3 = (short)(-n2);
                for (int l = 0; l < 5; ++l) {
                    for (int n4 = 0; n4 < 4; ++n4) {
                        System.out.println("  " + getFontName(l) + "." + getStyleName(n4) + "." + getString(FontConfiguration.table_scriptIDs[k]) + "=" + getString(FontConfiguration.table_componentFontNameIDs[FontConfiguration.table_scriptFonts[n3 + (l * 4 + n4)]]));
                    }
                }
            }
        }
        System.out.println("\n----elcIDs----------------");
        printTable(FontConfiguration.table_elcIDs, 0);
        System.out.println("\n----sequences-------------");
        for (int n5 = 0; n5 < FontConfiguration.table_elcIDs.length; ++n5) {
            System.out.println("  " + n5 + "/" + getString(FontConfiguration.table_elcIDs[n5]));
            final short[] shortArray = getShortArray(FontConfiguration.table_sequences[n5 * 5 + 0]);
            for (int n6 = 0; n6 < shortArray.length; ++n6) {
                System.out.println("     " + getString(FontConfiguration.table_scriptIDs[shortArray[n6]]));
            }
        }
        System.out.println("\n----fontfileNameIDs-------");
        printTable(FontConfiguration.table_fontfileNameIDs, 0);
        System.out.println("\n----componentFontNameIDs--");
        printTable(FontConfiguration.table_componentFontNameIDs, 1);
        System.out.println("\n----filenames-------------");
        for (int n7 = 0; n7 < FontConfiguration.table_filenames.length; ++n7) {
            if (FontConfiguration.table_filenames[n7] == -1) {
                System.out.println("  " + n7 + " : null");
            }
            else {
                System.out.println("  " + n7 + " : " + getString(FontConfiguration.table_fontfileNameIDs[FontConfiguration.table_filenames[n7]]));
            }
        }
        System.out.println("\n----awtfontpaths---------");
        for (int n8 = 0; n8 < FontConfiguration.table_awtfontpaths.length; ++n8) {
            System.out.println("  " + getString(FontConfiguration.table_scriptIDs[n8]) + " : " + getString(FontConfiguration.table_awtfontpaths[n8]));
        }
        System.out.println("\n----proportionals--------");
        for (int n9 = 0; n9 < FontConfiguration.table_proportionals.length; ++n9) {
            System.out.println("  " + getString(FontConfiguration.table_componentFontNameIDs[FontConfiguration.table_proportionals[n9++]]) + " -> " + getString(FontConfiguration.table_componentFontNameIDs[FontConfiguration.table_proportionals[n9]]));
        }
        int n10 = 0;
        System.out.println("\n----alphabeticSuffix----");
        while (n10 < FontConfiguration.table_alphabeticSuffix.length) {
            System.out.println("    " + getString(FontConfiguration.table_elcIDs[FontConfiguration.table_alphabeticSuffix[n10++]]) + " -> " + getString(FontConfiguration.table_alphabeticSuffix[n10++]));
        }
        System.out.println("\n----String Table---------");
        System.out.println("    stringID:    Num =" + FontConfiguration.table_stringIDs.length);
        System.out.println("    stringTable: Size=" + FontConfiguration.table_stringTable.length * 2);
        System.out.println("\n----fallbackScriptIDs---");
        final short[] shortArray2 = getShortArray(FontConfiguration.head[15]);
        for (int n11 = 0; n11 < shortArray2.length; ++n11) {
            System.out.println("  " + getString(FontConfiguration.table_scriptIDs[shortArray2[n11]]));
        }
        System.out.println("\n----appendedfontpath-----");
        System.out.println("  " + getString(FontConfiguration.head[16]));
        System.out.println("\n----Version--------------");
        System.out.println("  " + getString(FontConfiguration.head[17]));
    }
    
    protected static short getComponentFontID(final short n, final int n2, final int n3) {
        final short n4 = FontConfiguration.table_scriptFonts[n];
        if (n4 >= 0) {
            return n4;
        }
        return FontConfiguration.table_scriptFonts[-n4 + n2 * 4 + n3];
    }
    
    protected static short getComponentFontIDMotif(final short n, final int n2, final int n3) {
        if (FontConfiguration.table_scriptFontsMotif.length == 0) {
            return 0;
        }
        final short n4 = FontConfiguration.table_scriptFontsMotif[n];
        if (n4 >= 0) {
            return n4;
        }
        return FontConfiguration.table_scriptFontsMotif[-n4 + n2 * 4 + n3];
    }
    
    private static int[] getExclusionRanges(final short n) {
        final short n2 = FontConfiguration.table_exclusions[n];
        if (n2 == 0) {
            return FontConfiguration.EMPTY_INT_ARRAY;
        }
        final char[] charArray = getString(n2).toCharArray();
        final int[] array = new int[charArray.length / 2];
        int n3 = 0;
        for (int i = 0; i < array.length; ++i) {
            array[i] = (charArray[n3++] << 16) + (charArray[n3++] & '\uffff');
        }
        return array;
    }
    
    private static boolean contains(final short[] array, final short n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            if (array[i] == n) {
                return true;
            }
        }
        return false;
    }
    
    protected static String getComponentFontName(final short n) {
        if (n < 0) {
            return null;
        }
        return getString(FontConfiguration.table_componentFontNameIDs[n]);
    }
    
    private static String getComponentFileName(final short n) {
        if (n < 0) {
            return null;
        }
        return getString(FontConfiguration.table_fontfileNameIDs[n]);
    }
    
    private static short getComponentFileID(final short n) {
        return FontConfiguration.table_filenames[n];
    }
    
    private static String getScriptName(final short n) {
        return getString(FontConfiguration.table_scriptIDs[n]);
    }
    
    protected short[] getCoreScripts(final int n) {
        final short[] shortArray = getShortArray(FontConfiguration.table_sequences[this.getInitELC() * 5 + n]);
        if (this.preferLocaleFonts) {
            if (this.reorderScripts == null) {
                this.reorderScripts = new HashMap<String, Short>();
            }
            final String[] array = new String[shortArray.length];
            for (int i = 0; i < array.length; ++i) {
                array[i] = getScriptName(shortArray[i]);
                this.reorderScripts.put(array[i], shortArray[i]);
            }
            this.reorderSequenceForLocale(array);
            for (int j = 0; j < array.length; ++j) {
                shortArray[j] = this.reorderScripts.get(array[j]);
            }
        }
        return shortArray;
    }
    
    private static short[] getFallbackScripts() {
        return getShortArray(FontConfiguration.head[15]);
    }
    
    private static void printTable(final short[] array, final int n) {
        for (int i = n; i < array.length; ++i) {
            System.out.println("  " + i + " : " + getString(array[i]));
        }
    }
    
    private static short[] readShortTable(final DataInputStream dataInputStream, final int n) throws IOException {
        if (n == 0) {
            return FontConfiguration.EMPTY_SHORT_ARRAY;
        }
        final short[] array = new short[n];
        final byte[] array2 = new byte[n * 2];
        dataInputStream.read(array2);
        for (int i = 0, n2 = 0; i < n; array[i++] = (short)(array2[n2++] << 8 | (array2[n2++] & 0xFF))) {}
        return array;
    }
    
    private static void writeShortTable(final DataOutputStream dataOutputStream, final short[] array) throws IOException {
        for (int length = array.length, i = 0; i < length; ++i) {
            dataOutputStream.writeShort(array[i]);
        }
    }
    
    private static short[] toList(final HashMap<String, Short> hashMap) {
        final short[] array = new short[hashMap.size()];
        Arrays.fill(array, (short)(-1));
        for (final Map.Entry entry : hashMap.entrySet()) {
            array[entry.getValue()] = getStringID((String)entry.getKey());
        }
        return array;
    }
    
    protected static String getString(final short n) {
        if (n == 0) {
            return null;
        }
        if (FontConfiguration.stringCache[n] == null) {
            FontConfiguration.stringCache[n] = new String(FontConfiguration.table_stringTable, FontConfiguration.table_stringIDs[n], FontConfiguration.table_stringIDs[n + 1] - FontConfiguration.table_stringIDs[n]);
        }
        return FontConfiguration.stringCache[n];
    }
    
    private static short[] getShortArray(final short n) {
        final char[] charArray = getString(n).toCharArray();
        final short[] array = new short[charArray.length];
        for (int i = 0; i < charArray.length; ++i) {
            array[i] = (short)(charArray[i] & '\uffff');
        }
        return array;
    }
    
    private static short getStringID(final String s) {
        if (s == null) {
            return 0;
        }
        final short n = (short)FontConfiguration.stringTable.length();
        FontConfiguration.stringTable.append(s);
        final short n2 = (short)FontConfiguration.stringTable.length();
        FontConfiguration.stringIDs[FontConfiguration.stringIDNum] = n;
        FontConfiguration.stringIDs[FontConfiguration.stringIDNum + 1] = n2;
        ++FontConfiguration.stringIDNum;
        if (FontConfiguration.stringIDNum + 1 >= FontConfiguration.stringIDs.length) {
            final short[] stringIDs = new short[FontConfiguration.stringIDNum + 1000];
            System.arraycopy(FontConfiguration.stringIDs, 0, stringIDs, 0, FontConfiguration.stringIDNum);
            FontConfiguration.stringIDs = stringIDs;
        }
        return (short)(FontConfiguration.stringIDNum - 1);
    }
    
    private static short getShortArrayID(final short[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (char)array[i];
        }
        return getStringID(new String(array2));
    }
    
    static {
        FontConfiguration.startupLocale = null;
        FontConfiguration.localeMap = null;
        FontConfiguration.isProperties = true;
        fontNames = new String[] { "serif", "sansserif", "monospaced", "dialog", "dialoginput" };
        publicFontNames = new String[] { "Serif", "SansSerif", "Monospaced", "Dialog", "DialogInput" };
        styleNames = new String[] { "plain", "bold", "italic", "bolditalic" };
        FontConfiguration.installedFallbackFontFiles = null;
        EMPTY_INT_ARRAY = new int[0];
        EMPTY_STRING_ARRAY = new String[0];
        EMPTY_SHORT_ARRAY = new short[0];
    }
    
    static class PropertiesHandler
    {
        private HashMap<String, Short> scriptIDs;
        private HashMap<String, Short> elcIDs;
        private HashMap<String, Short> componentFontNameIDs;
        private HashMap<String, Short> fontfileNameIDs;
        private HashMap<String, Integer> logicalFontIDs;
        private HashMap<String, Integer> fontStyleIDs;
        private HashMap<Short, Short> filenames;
        private HashMap<Short, short[]> sequences;
        private HashMap<Short, Short[]> scriptFonts;
        private HashMap<Short, Short> scriptAllfonts;
        private HashMap<Short, int[]> exclusions;
        private HashMap<Short, Short> awtfontpaths;
        private HashMap<Short, Short> proportionals;
        private HashMap<Short, Short> scriptAllfontsMotif;
        private HashMap<Short, Short[]> scriptFontsMotif;
        private HashMap<Short, Short> alphabeticSuffix;
        private short[] fallbackScriptIDs;
        private String version;
        private String appendedfontpath;
        
        public void load(final InputStream inputStream) throws IOException {
            this.initLogicalNameStyle();
            this.initHashMaps();
            new FontProperties().load(inputStream);
            this.initBinaryTable();
        }
        
        private void initBinaryTable() {
            FontConfiguration.head = new short[20];
            FontConfiguration.head[0] = 20;
            FontConfiguration.table_scriptIDs = toList(this.scriptIDs);
            FontConfiguration.head[1] = (short)(FontConfiguration.head[0] + FontConfiguration.table_scriptIDs.length);
            FontConfiguration.table_scriptFonts = new short[FontConfiguration.table_scriptIDs.length + this.scriptFonts.size() * 20];
            for (final Map.Entry entry : this.scriptAllfonts.entrySet()) {
                FontConfiguration.table_scriptFonts[entry.getKey()] = (short)entry.getValue();
            }
            int length = FontConfiguration.table_scriptIDs.length;
            for (final Map.Entry entry2 : this.scriptFonts.entrySet()) {
                FontConfiguration.table_scriptFonts[entry2.getKey()] = (short)(-length);
                final Short[] array = (Short[])entry2.getValue();
                for (int i = 0; i < 20; ++i) {
                    if (array[i] != null) {
                        FontConfiguration.table_scriptFonts[length++] = array[i];
                    }
                    else {
                        FontConfiguration.table_scriptFonts[length++] = 0;
                    }
                }
            }
            FontConfiguration.head[2] = (short)(FontConfiguration.head[1] + FontConfiguration.table_scriptFonts.length);
            FontConfiguration.table_elcIDs = toList(this.elcIDs);
            FontConfiguration.head[3] = (short)(FontConfiguration.head[2] + FontConfiguration.table_elcIDs.length);
            FontConfiguration.table_sequences = new short[this.elcIDs.size() * 5];
            for (final Map.Entry entry3 : this.sequences.entrySet()) {
                final int intValue = (int)entry3.getKey();
                final short[] array2 = (short[])entry3.getValue();
                if (array2.length == 1) {
                    for (int j = 0; j < 5; ++j) {
                        FontConfiguration.table_sequences[intValue * 5 + j] = array2[0];
                    }
                }
                else {
                    for (int k = 0; k < 5; ++k) {
                        FontConfiguration.table_sequences[intValue * 5 + k] = array2[k];
                    }
                }
            }
            FontConfiguration.head[4] = (short)(FontConfiguration.head[3] + FontConfiguration.table_sequences.length);
            FontConfiguration.table_fontfileNameIDs = toList(this.fontfileNameIDs);
            FontConfiguration.head[5] = (short)(FontConfiguration.head[4] + FontConfiguration.table_fontfileNameIDs.length);
            FontConfiguration.table_componentFontNameIDs = toList(this.componentFontNameIDs);
            FontConfiguration.head[6] = (short)(FontConfiguration.head[5] + FontConfiguration.table_componentFontNameIDs.length);
            FontConfiguration.table_filenames = new short[FontConfiguration.table_componentFontNameIDs.length];
            Arrays.fill(FontConfiguration.table_filenames, (short)(-1));
            for (final Map.Entry entry4 : this.filenames.entrySet()) {
                FontConfiguration.table_filenames[entry4.getKey()] = (short)entry4.getValue();
            }
            FontConfiguration.head[7] = (short)(FontConfiguration.head[6] + FontConfiguration.table_filenames.length);
            FontConfiguration.table_awtfontpaths = new short[FontConfiguration.table_scriptIDs.length];
            for (final Map.Entry entry5 : this.awtfontpaths.entrySet()) {
                FontConfiguration.table_awtfontpaths[entry5.getKey()] = (short)entry5.getValue();
            }
            FontConfiguration.head[8] = (short)(FontConfiguration.head[7] + FontConfiguration.table_awtfontpaths.length);
            FontConfiguration.table_exclusions = new short[this.scriptIDs.size()];
            for (final Map.Entry entry6 : this.exclusions.entrySet()) {
                final int[] array3 = (int[])entry6.getValue();
                final char[] array4 = new char[array3.length * 2];
                int n = 0;
                for (int l = 0; l < array3.length; ++l) {
                    array4[n++] = (char)(array3[l] >> 16);
                    array4[n++] = (char)(array3[l] & 0xFFFF);
                }
                FontConfiguration.table_exclusions[entry6.getKey()] = getStringID(new String(array4));
            }
            FontConfiguration.head[9] = (short)(FontConfiguration.head[8] + FontConfiguration.table_exclusions.length);
            FontConfiguration.table_proportionals = new short[this.proportionals.size() * 2];
            int n2 = 0;
            for (final Map.Entry entry7 : this.proportionals.entrySet()) {
                FontConfiguration.table_proportionals[n2++] = (short)entry7.getKey();
                FontConfiguration.table_proportionals[n2++] = (short)entry7.getValue();
            }
            FontConfiguration.head[10] = (short)(FontConfiguration.head[9] + FontConfiguration.table_proportionals.length);
            if (this.scriptAllfontsMotif.size() != 0 || this.scriptFontsMotif.size() != 0) {
                FontConfiguration.table_scriptFontsMotif = new short[FontConfiguration.table_scriptIDs.length + this.scriptFontsMotif.size() * 20];
                for (final Map.Entry entry8 : this.scriptAllfontsMotif.entrySet()) {
                    FontConfiguration.table_scriptFontsMotif[entry8.getKey()] = (short)entry8.getValue();
                }
                int length2 = FontConfiguration.table_scriptIDs.length;
                for (final Map.Entry entry9 : this.scriptFontsMotif.entrySet()) {
                    FontConfiguration.table_scriptFontsMotif[entry9.getKey()] = (short)(-length2);
                    final Short[] array5 = (Short[])entry9.getValue();
                    for (int n3 = 0; n3 < 20; ++n3) {
                        if (array5[n3] != null) {
                            FontConfiguration.table_scriptFontsMotif[length2++] = array5[n3];
                        }
                        else {
                            FontConfiguration.table_scriptFontsMotif[length2++] = 0;
                        }
                    }
                }
            }
            else {
                FontConfiguration.table_scriptFontsMotif = FontConfiguration.EMPTY_SHORT_ARRAY;
            }
            FontConfiguration.head[11] = (short)(FontConfiguration.head[10] + FontConfiguration.table_scriptFontsMotif.length);
            FontConfiguration.table_alphabeticSuffix = new short[this.alphabeticSuffix.size() * 2];
            int n4 = 0;
            for (final Map.Entry entry10 : this.alphabeticSuffix.entrySet()) {
                FontConfiguration.table_alphabeticSuffix[n4++] = (short)entry10.getKey();
                FontConfiguration.table_alphabeticSuffix[n4++] = (short)entry10.getValue();
            }
            FontConfiguration.head[15] = getShortArrayID(this.fallbackScriptIDs);
            FontConfiguration.head[16] = getStringID(this.appendedfontpath);
            FontConfiguration.head[17] = getStringID(this.version);
            FontConfiguration.head[12] = (short)(FontConfiguration.head[11] + FontConfiguration.table_alphabeticSuffix.length);
            FontConfiguration.table_stringIDs = new short[FontConfiguration.stringIDNum + 1];
            System.arraycopy(FontConfiguration.stringIDs, 0, FontConfiguration.table_stringIDs, 0, FontConfiguration.stringIDNum + 1);
            FontConfiguration.head[13] = (short)(FontConfiguration.head[12] + FontConfiguration.stringIDNum + 1);
            FontConfiguration.table_stringTable = FontConfiguration.stringTable.toString().toCharArray();
            FontConfiguration.head[14] = (short)(FontConfiguration.head[13] + FontConfiguration.stringTable.length());
            FontConfiguration.stringCache = new String[FontConfiguration.table_stringIDs.length];
        }
        
        private void initLogicalNameStyle() {
            this.logicalFontIDs = new HashMap<String, Integer>();
            this.fontStyleIDs = new HashMap<String, Integer>();
            this.logicalFontIDs.put("serif", 0);
            this.logicalFontIDs.put("sansserif", 1);
            this.logicalFontIDs.put("monospaced", 2);
            this.logicalFontIDs.put("dialog", 3);
            this.logicalFontIDs.put("dialoginput", 4);
            this.fontStyleIDs.put("plain", 0);
            this.fontStyleIDs.put("bold", 1);
            this.fontStyleIDs.put("italic", 2);
            this.fontStyleIDs.put("bolditalic", 3);
        }
        
        private void initHashMaps() {
            this.scriptIDs = new HashMap<String, Short>();
            this.elcIDs = new HashMap<String, Short>();
            (this.componentFontNameIDs = new HashMap<String, Short>()).put("", (Short)0);
            this.fontfileNameIDs = new HashMap<String, Short>();
            this.filenames = new HashMap<Short, Short>();
            this.sequences = new HashMap<Short, short[]>();
            this.scriptFonts = new HashMap<Short, Short[]>();
            this.scriptAllfonts = new HashMap<Short, Short>();
            this.exclusions = new HashMap<Short, int[]>();
            this.awtfontpaths = new HashMap<Short, Short>();
            this.proportionals = new HashMap<Short, Short>();
            this.scriptFontsMotif = new HashMap<Short, Short[]>();
            this.scriptAllfontsMotif = new HashMap<Short, Short>();
            this.alphabeticSuffix = new HashMap<Short, Short>();
            this.fallbackScriptIDs = FontConfiguration.EMPTY_SHORT_ARRAY;
        }
        
        private int[] parseExclusions(final String s, final String s2) {
            if (s2 == null) {
                return FontConfiguration.EMPTY_INT_ARRAY;
            }
            int n = 1;
            for (int index = 0; (index = s2.indexOf(44, index)) != -1; ++index) {
                ++n;
            }
            final int[] array = new int[n * 2];
            int n2 = 0;
            int int1;
            int int2;
            for (int i = 0; i < n * 2; array[i++] = int1, array[i++] = int2) {
                try {
                    final int index2 = s2.indexOf(45, n2);
                    final String substring = s2.substring(n2, index2);
                    final int n3 = index2 + 1;
                    int n4 = s2.indexOf(44, n3);
                    if (n4 == -1) {
                        n4 = s2.length();
                    }
                    final String substring2 = s2.substring(n3, n4);
                    n2 = n4 + 1;
                    final int length = substring.length();
                    final int length2 = substring2.length();
                    if ((length != 4 && length != 6) || (length2 != 4 && length2 != 6)) {
                        throw new Exception();
                    }
                    int1 = Integer.parseInt(substring, 16);
                    int2 = Integer.parseInt(substring2, 16);
                    if (int1 > int2) {
                        throw new Exception();
                    }
                }
                catch (final Exception ex) {
                    if (FontUtilities.debugFonts() && FontConfiguration.logger != null) {
                        FontConfiguration.logger.config("Failed parsing " + s + " property of font configuration.");
                    }
                    return FontConfiguration.EMPTY_INT_ARRAY;
                }
            }
            return array;
        }
        
        private Short getID(final HashMap<String, Short> hashMap, final String s) {
            final Short n = hashMap.get(s);
            if (n == null) {
                hashMap.put(s, (short)hashMap.size());
                return hashMap.get(s);
            }
            return n;
        }
        
        private void parseProperty(String s, final String s2) {
            if (s.startsWith("filename.")) {
                s = s.substring(9);
                if (!"MingLiU_HKSCS".equals(s)) {
                    s = s.replace('_', ' ');
                }
                this.filenames.put(this.getID(this.componentFontNameIDs, s), this.getID(this.fontfileNameIDs, s2));
            }
            else if (s.startsWith("exclusion.")) {
                s = s.substring(10);
                this.exclusions.put(this.getID(this.scriptIDs, s), this.parseExclusions(s, s2));
            }
            else if (s.startsWith("sequence.")) {
                s = s.substring(9);
                boolean b = false;
                boolean b2 = false;
                final String[] array = splitSequence(s2).toArray(FontConfiguration.EMPTY_STRING_ARRAY);
                final short[] fallbackScriptIDs = new short[array.length];
                for (int i = 0; i < array.length; ++i) {
                    if ("alphabetic/default".equals(array[i])) {
                        array[i] = "alphabetic";
                        b = true;
                    }
                    else if ("alphabetic/1252".equals(array[i])) {
                        array[i] = "alphabetic";
                        b2 = true;
                    }
                    fallbackScriptIDs[i] = this.getID(this.scriptIDs, array[i]);
                }
                final short access$1500 = getShortArrayID(fallbackScriptIDs);
                final int index = s.indexOf(46);
                Short n;
                if (index == -1) {
                    if ("fallback".equals(s)) {
                        this.fallbackScriptIDs = fallbackScriptIDs;
                        return;
                    }
                    if (!"allfonts".equals(s)) {
                        if (FontConfiguration.logger != null) {
                            FontConfiguration.logger.config("Error sequence def: <sequence." + s + ">");
                        }
                        return;
                    }
                    n = this.getID(this.elcIDs, "NULL.NULL.NULL");
                }
                else {
                    n = this.getID(this.elcIDs, s.substring(index + 1));
                    s = s.substring(0, index);
                }
                short[] array2;
                if ("allfonts".equals(s)) {
                    array2 = new short[] { access$1500 };
                }
                else {
                    array2 = this.sequences.get(n);
                    if (array2 == null) {
                        array2 = new short[5];
                    }
                    final Integer n2 = this.logicalFontIDs.get(s);
                    if (n2 == null) {
                        if (FontConfiguration.logger != null) {
                            FontConfiguration.logger.config("Unrecognizable logicfont name " + s);
                        }
                        return;
                    }
                    array2[n2] = access$1500;
                }
                this.sequences.put(n, array2);
                if (b) {
                    this.alphabeticSuffix.put(n, getStringID("default"));
                }
                else if (b2) {
                    this.alphabeticSuffix.put(n, getStringID("1252"));
                }
            }
            else if (s.startsWith("allfonts.")) {
                s = s.substring(9);
                if (s.endsWith(".motif")) {
                    s = s.substring(0, s.length() - 6);
                    this.scriptAllfontsMotif.put(this.getID(this.scriptIDs, s), this.getID(this.componentFontNameIDs, s2));
                }
                else {
                    this.scriptAllfonts.put(this.getID(this.scriptIDs, s), this.getID(this.componentFontNameIDs, s2));
                }
            }
            else if (s.startsWith("awtfontpath.")) {
                s = s.substring(12);
                this.awtfontpaths.put(this.getID(this.scriptIDs, s), getStringID(s2));
            }
            else if ("version".equals(s)) {
                this.version = s2;
            }
            else if ("appendedfontpath".equals(s)) {
                this.appendedfontpath = s2;
            }
            else if (s.startsWith("proportional.")) {
                s = s.substring(13).replace('_', ' ');
                this.proportionals.put(this.getID(this.componentFontNameIDs, s), this.getID(this.componentFontNameIDs, s2));
            }
            else {
                boolean b3 = false;
                final int index2 = s.indexOf(46);
                if (index2 == -1) {
                    if (FontConfiguration.logger != null) {
                        FontConfiguration.logger.config("Failed parsing " + s + " property of font configuration.");
                    }
                    return;
                }
                final int index3 = s.indexOf(46, index2 + 1);
                if (index3 == -1) {
                    if (FontConfiguration.logger != null) {
                        FontConfiguration.logger.config("Failed parsing " + s + " property of font configuration.");
                    }
                    return;
                }
                if (s.endsWith(".motif")) {
                    s = s.substring(0, s.length() - 6);
                    b3 = true;
                }
                final Integer n3 = this.logicalFontIDs.get(s.substring(0, index2));
                final Integer n4 = this.fontStyleIDs.get(s.substring(index2 + 1, index3));
                final Short id = this.getID(this.scriptIDs, s.substring(index3 + 1));
                if (n3 == null || n4 == null) {
                    if (FontConfiguration.logger != null) {
                        FontConfiguration.logger.config("unrecognizable logicfont name/style at " + s);
                    }
                    return;
                }
                Short[] array3;
                if (b3) {
                    array3 = this.scriptFontsMotif.get(id);
                }
                else {
                    array3 = this.scriptFonts.get(id);
                }
                if (array3 == null) {
                    array3 = new Short[20];
                }
                array3[n3 * 4 + n4] = this.getID(this.componentFontNameIDs, s2);
                if (b3) {
                    this.scriptFontsMotif.put(id, array3);
                }
                else {
                    this.scriptFonts.put(id, array3);
                }
            }
        }
        
        class FontProperties extends Properties
        {
            @Override
            public synchronized Object put(final Object o, final Object o2) {
                PropertiesHandler.this.parseProperty((String)o, (String)o2);
                return null;
            }
        }
    }
}
