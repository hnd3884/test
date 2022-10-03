package java.awt.datatransfer;

import java.lang.ref.SoftReference;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import sun.awt.datatransfer.DataTransferer;
import java.net.URL;
import java.awt.Toolkit;
import java.security.AccessController;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.File;
import java.security.PrivilegedAction;
import java.io.BufferedReader;
import java.util.HashSet;
import java.util.HashMap;
import sun.awt.AppContext;
import java.util.Collections;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;

public final class SystemFlavorMap implements FlavorMap, FlavorTable
{
    private static String JavaMIME;
    private static final Object FLAVOR_MAP_KEY;
    private static final String keyValueSeparators = "=: \t\r\n\f";
    private static final String strictKeyValueSeparators = "=:";
    private static final String whiteSpaceChars = " \t\r\n\f";
    private static final String[] UNICODE_TEXT_CLASSES;
    private static final String[] ENCODED_TEXT_CLASSES;
    private static final String TEXT_PLAIN_BASE_TYPE = "text/plain";
    private static final String HTML_TEXT_BASE_TYPE = "text/html";
    private final Map<String, LinkedHashSet<DataFlavor>> nativeToFlavor;
    private final Map<DataFlavor, LinkedHashSet<String>> flavorToNative;
    private Map<String, LinkedHashSet<String>> textTypeToNative;
    private boolean isMapInitialized;
    private final SoftCache<DataFlavor, String> nativesForFlavorCache;
    private final SoftCache<String, DataFlavor> flavorsForNativeCache;
    private Set<Object> disabledMappingGenerationKeys;
    private static final String[] htmlDocumntTypes;
    
    private Map<String, LinkedHashSet<DataFlavor>> getNativeToFlavor() {
        if (!this.isMapInitialized) {
            this.initSystemFlavorMap();
        }
        return this.nativeToFlavor;
    }
    
    private synchronized Map<DataFlavor, LinkedHashSet<String>> getFlavorToNative() {
        if (!this.isMapInitialized) {
            this.initSystemFlavorMap();
        }
        return this.flavorToNative;
    }
    
    private synchronized Map<String, LinkedHashSet<String>> getTextTypeToNative() {
        if (!this.isMapInitialized) {
            this.initSystemFlavorMap();
            this.textTypeToNative = Collections.unmodifiableMap((Map<? extends String, ? extends LinkedHashSet<String>>)this.textTypeToNative);
        }
        return this.textTypeToNative;
    }
    
    public static FlavorMap getDefaultFlavorMap() {
        final AppContext appContext = AppContext.getAppContext();
        FlavorMap flavorMap = (FlavorMap)appContext.get(SystemFlavorMap.FLAVOR_MAP_KEY);
        if (flavorMap == null) {
            flavorMap = new SystemFlavorMap();
            appContext.put(SystemFlavorMap.FLAVOR_MAP_KEY, flavorMap);
        }
        return flavorMap;
    }
    
    private SystemFlavorMap() {
        this.nativeToFlavor = new HashMap<String, LinkedHashSet<DataFlavor>>();
        this.flavorToNative = new HashMap<DataFlavor, LinkedHashSet<String>>();
        this.textTypeToNative = new HashMap<String, LinkedHashSet<String>>();
        this.isMapInitialized = false;
        this.nativesForFlavorCache = new SoftCache<DataFlavor, String>();
        this.flavorsForNativeCache = new SoftCache<String, DataFlavor>();
        this.disabledMappingGenerationKeys = new HashSet<Object>();
    }
    
    private void initSystemFlavorMap() {
        if (this.isMapInitialized) {
            return;
        }
        this.isMapInitialized = true;
        final BufferedReader bufferedReader = AccessController.doPrivileged((PrivilegedAction<BufferedReader>)new PrivilegedAction<BufferedReader>() {
            @Override
            public BufferedReader run() {
                final String string = System.getProperty("java.home") + File.separator + "lib" + File.separator + "flavormap.properties";
                try {
                    return new BufferedReader(new InputStreamReader(new File(string).toURI().toURL().openStream(), "ISO-8859-1"));
                }
                catch (final MalformedURLException ex) {
                    System.err.println("MalformedURLException:" + ex + " while loading default flavormap.properties file:" + string);
                }
                catch (final IOException ex2) {
                    System.err.println("IOException:" + ex2 + " while loading default flavormap.properties file:" + string);
                }
                return null;
            }
        });
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return Toolkit.getProperty("AWT.DnD.flavorMapFileURL", null);
            }
        });
        if (bufferedReader != null) {
            try {
                this.parseAndStoreReader(bufferedReader);
            }
            catch (final IOException ex) {
                System.err.println("IOException:" + ex + " while parsing default flavormap.properties file");
            }
        }
        BufferedReader bufferedReader2 = null;
        if (s != null) {
            try {
                bufferedReader2 = new BufferedReader(new InputStreamReader(new URL(s).openStream(), "ISO-8859-1"));
            }
            catch (final MalformedURLException ex2) {
                System.err.println("MalformedURLException:" + ex2 + " while reading AWT.DnD.flavorMapFileURL:" + s);
            }
            catch (final IOException ex3) {
                System.err.println("IOException:" + ex3 + " while reading AWT.DnD.flavorMapFileURL:" + s);
            }
            catch (final SecurityException ex4) {}
        }
        if (bufferedReader2 != null) {
            try {
                this.parseAndStoreReader(bufferedReader2);
            }
            catch (final IOException ex5) {
                System.err.println("IOException:" + ex5 + " while parsing AWT.DnD.flavorMapFileURL");
            }
        }
    }
    
    private void parseAndStoreReader(final BufferedReader bufferedReader) throws IOException {
        while (true) {
            String s = bufferedReader.readLine();
            if (s == null) {
                break;
            }
            if (s.length() <= 0) {
                continue;
            }
            final char char1 = s.charAt(0);
            if (char1 == '#' || char1 == '!') {
                continue;
            }
            while (this.continueLine(s)) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    line = "";
                }
                final String substring = s.substring(0, s.length() - 1);
                int n;
                for (n = 0; n < line.length() && " \t\r\n\f".indexOf(line.charAt(n)) != -1; ++n) {}
                s = substring + line.substring(n, line.length());
            }
            int length;
            int n2;
            for (length = s.length(), n2 = 0; n2 < length && " \t\r\n\f".indexOf(s.charAt(n2)) != -1; ++n2) {}
            if (n2 == length) {
                continue;
            }
            int i;
            for (i = n2; i < length; ++i) {
                final char char2 = s.charAt(i);
                if (char2 == '\\') {
                    ++i;
                }
                else if ("=: \t\r\n\f".indexOf(char2) != -1) {
                    break;
                }
            }
            int n3;
            for (n3 = i; n3 < length && " \t\r\n\f".indexOf(s.charAt(n3)) != -1; ++n3) {}
            if (n3 < length && "=:".indexOf(s.charAt(n3)) != -1) {
                ++n3;
            }
            while (n3 < length && " \t\r\n\f".indexOf(s.charAt(n3)) != -1) {
                ++n3;
            }
            final String substring2 = s.substring(n2, i);
            final String s2 = (i < length) ? s.substring(n3, length) : "";
            final String loadConvert = this.loadConvert(substring2);
            String s3 = this.loadConvert(s2);
            try {
                final MimeType mimeType = new MimeType(s3);
                if ("text".equals(mimeType.getPrimaryType())) {
                    final String parameter = mimeType.getParameter("charset");
                    if (DataTransferer.doesSubtypeSupportCharset(mimeType.getSubType(), parameter)) {
                        final DataTransferer instance = DataTransferer.getInstance();
                        if (instance != null) {
                            instance.registerTextFlavorProperties(loadConvert, parameter, mimeType.getParameter("eoln"), mimeType.getParameter("terminators"));
                        }
                    }
                    mimeType.removeParameter("charset");
                    mimeType.removeParameter("class");
                    mimeType.removeParameter("eoln");
                    mimeType.removeParameter("terminators");
                    s3 = mimeType.toString();
                }
            }
            catch (final MimeTypeParseException ex) {
                ex.printStackTrace();
                continue;
            }
            DataFlavor dataFlavor;
            try {
                dataFlavor = new DataFlavor(s3);
            }
            catch (final Exception ex2) {
                try {
                    dataFlavor = new DataFlavor(s3, null);
                }
                catch (final Exception ex3) {
                    ex3.printStackTrace();
                }
            }
            final LinkedHashSet set = new LinkedHashSet();
            set.add(dataFlavor);
            if ("text".equals(dataFlavor.getPrimaryType())) {
                set.addAll(convertMimeTypeToDataFlavors(s3));
                this.store(dataFlavor.mimeType.getBaseType(), loadConvert, this.getTextTypeToNative());
            }
            for (final DataFlavor dataFlavor2 : set) {
                this.store(dataFlavor2, loadConvert, this.getFlavorToNative());
                this.store(loadConvert, dataFlavor2, this.getNativeToFlavor());
            }
        }
    }
    
    private boolean continueLine(final String s) {
        int n = 0;
        int n2 = s.length() - 1;
        while (n2 >= 0 && s.charAt(n2--) == '\\') {
            ++n;
        }
        return n % 2 == 1;
    }
    
    private String loadConvert(final String s) {
        final int length = s.length();
        final StringBuilder sb = new StringBuilder(length);
        int i = 0;
        while (i < length) {
            final char char1 = s.charAt(i++);
            if (char1 == '\\') {
                char char2 = s.charAt(i++);
                if (char2 == 'u') {
                    int n = 0;
                    for (int j = 0; j < 4; ++j) {
                        final char char3 = s.charAt(i++);
                        switch (char3) {
                            case 48:
                            case 49:
                            case 50:
                            case 51:
                            case 52:
                            case 53:
                            case 54:
                            case 55:
                            case 56:
                            case 57: {
                                n = (n << 4) + char3 - 48;
                                break;
                            }
                            case 97:
                            case 98:
                            case 99:
                            case 100:
                            case 101:
                            case 102: {
                                n = (n << 4) + 10 + char3 - 97;
                                break;
                            }
                            case 65:
                            case 66:
                            case 67:
                            case 68:
                            case 69:
                            case 70: {
                                n = (n << 4) + 10 + char3 - 65;
                                break;
                            }
                            default: {
                                throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                            }
                        }
                    }
                    sb.append((char)n);
                }
                else {
                    if (char2 == 't') {
                        char2 = '\t';
                    }
                    else if (char2 == 'r') {
                        char2 = '\r';
                    }
                    else if (char2 == 'n') {
                        char2 = '\n';
                    }
                    else if (char2 == 'f') {
                        char2 = '\f';
                    }
                    sb.append(char2);
                }
            }
            else {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    private <H, L> void store(final H h, final L l, final Map<H, LinkedHashSet<L>> map) {
        LinkedHashSet set = map.get(h);
        if (set == null) {
            set = new LinkedHashSet(1);
            map.put(h, set);
        }
        if (!set.contains(l)) {
            set.add(l);
        }
    }
    
    private LinkedHashSet<DataFlavor> nativeToFlavorLookup(final String s) {
        LinkedHashSet<DataFlavor> set = this.getNativeToFlavor().get(s);
        if (s != null && !this.disabledMappingGenerationKeys.contains(s)) {
            final DataTransferer instance = DataTransferer.getInstance();
            if (instance != null) {
                final LinkedHashSet<DataFlavor> platformMappingsForNative = instance.getPlatformMappingsForNative(s);
                if (!platformMappingsForNative.isEmpty()) {
                    if (set != null) {
                        platformMappingsForNative.addAll(set);
                    }
                    set = platformMappingsForNative;
                }
            }
        }
        if (set == null && isJavaMIMEType(s)) {
            final String decodeJavaMIMEType = decodeJavaMIMEType(s);
            DataFlavor dataFlavor = null;
            try {
                dataFlavor = new DataFlavor(decodeJavaMIMEType);
            }
            catch (final Exception ex) {
                System.err.println("Exception \"" + ex.getClass().getName() + ": " + ex.getMessage() + "\"while constructing DataFlavor for: " + decodeJavaMIMEType);
            }
            if (dataFlavor != null) {
                set = new LinkedHashSet<DataFlavor>(1);
                this.getNativeToFlavor().put(s, set);
                set.add((Object)dataFlavor);
                this.flavorsForNativeCache.remove(s);
                LinkedHashSet<String> set2 = this.getFlavorToNative().get(dataFlavor);
                if (set2 == null) {
                    set2 = new LinkedHashSet<String>(1);
                    this.getFlavorToNative().put(dataFlavor, set2);
                }
                set2.add(s);
                this.nativesForFlavorCache.remove(dataFlavor);
            }
        }
        return (set != null) ? set : new LinkedHashSet<DataFlavor>(0);
    }
    
    private LinkedHashSet<String> flavorToNativeLookup(final DataFlavor dataFlavor, final boolean b) {
        LinkedHashSet<String> set = this.getFlavorToNative().get(dataFlavor);
        if (dataFlavor != null && !this.disabledMappingGenerationKeys.contains(dataFlavor)) {
            final DataTransferer instance = DataTransferer.getInstance();
            if (instance != null) {
                final LinkedHashSet<String> platformMappingsForFlavor = instance.getPlatformMappingsForFlavor(dataFlavor);
                if (!platformMappingsForFlavor.isEmpty()) {
                    if (set != null) {
                        platformMappingsForFlavor.addAll(set);
                    }
                    set = platformMappingsForFlavor;
                }
            }
        }
        if (set == null) {
            if (b) {
                final String encodeDataFlavor = encodeDataFlavor(dataFlavor);
                set = new LinkedHashSet<String>(1);
                this.getFlavorToNative().put(dataFlavor, set);
                set.add(encodeDataFlavor);
                LinkedHashSet<DataFlavor> set2 = this.getNativeToFlavor().get(encodeDataFlavor);
                if (set2 == null) {
                    set2 = new LinkedHashSet<DataFlavor>(1);
                    this.getNativeToFlavor().put(encodeDataFlavor, set2);
                }
                set2.add(dataFlavor);
                this.nativesForFlavorCache.remove(dataFlavor);
                this.flavorsForNativeCache.remove(encodeDataFlavor);
            }
            else {
                set = new LinkedHashSet<String>(0);
            }
        }
        return new LinkedHashSet<String>(set);
    }
    
    @Override
    public synchronized List<String> getNativesForFlavor(final DataFlavor dataFlavor) {
        final LinkedHashSet<String> check = this.nativesForFlavorCache.check(dataFlavor);
        if (check != null) {
            return new ArrayList<String>(check);
        }
        LinkedHashSet<String> set;
        if (dataFlavor == null) {
            set = new LinkedHashSet<String>(this.getNativeToFlavor().keySet());
        }
        else if (this.disabledMappingGenerationKeys.contains(dataFlavor)) {
            set = this.flavorToNativeLookup(dataFlavor, false);
        }
        else if (DataTransferer.isFlavorCharsetTextType(dataFlavor)) {
            set = new LinkedHashSet<String>(0);
            if ("text".equals(dataFlavor.getPrimaryType())) {
                final LinkedHashSet set2 = this.getTextTypeToNative().get(dataFlavor.mimeType.getBaseType());
                if (set2 != null) {
                    set.addAll((Collection<?>)set2);
                }
            }
            final LinkedHashSet set3 = this.getTextTypeToNative().get("text/plain");
            if (set3 != null) {
                set.addAll((Collection<?>)set3);
            }
            if (set.isEmpty()) {
                set = this.flavorToNativeLookup(dataFlavor, true);
            }
            else {
                set.addAll((Collection<?>)this.flavorToNativeLookup(dataFlavor, false));
            }
        }
        else if (DataTransferer.isFlavorNoncharsetTextType(dataFlavor)) {
            set = this.getTextTypeToNative().get(dataFlavor.mimeType.getBaseType());
            if (set == null || set.isEmpty()) {
                set = this.flavorToNativeLookup(dataFlavor, true);
            }
            else {
                set.addAll((Collection<?>)this.flavorToNativeLookup(dataFlavor, false));
            }
        }
        else {
            set = this.flavorToNativeLookup(dataFlavor, true);
        }
        this.nativesForFlavorCache.put(dataFlavor, set);
        return new ArrayList<String>(set);
    }
    
    @Override
    public synchronized List<DataFlavor> getFlavorsForNative(final String s) {
        final LinkedHashSet<DataFlavor> check = this.flavorsForNativeCache.check(s);
        if (check != null) {
            return new ArrayList<DataFlavor>(check);
        }
        final LinkedHashSet set = new LinkedHashSet();
        if (s == null) {
            final Iterator<String> iterator = this.getNativesForFlavor(null).iterator();
            while (iterator.hasNext()) {
                set.addAll(this.getFlavorsForNative(iterator.next()));
            }
        }
        else {
            final LinkedHashSet<DataFlavor> nativeToFlavorLookup = this.nativeToFlavorLookup(s);
            if (this.disabledMappingGenerationKeys.contains(s)) {
                return new ArrayList<DataFlavor>(nativeToFlavorLookup);
            }
            for (final DataFlavor dataFlavor : this.nativeToFlavorLookup(s)) {
                set.add(dataFlavor);
                if ("text".equals(dataFlavor.getPrimaryType())) {
                    set.addAll(convertMimeTypeToDataFlavors(dataFlavor.mimeType.getBaseType()));
                }
            }
        }
        this.flavorsForNativeCache.put(s, set);
        return new ArrayList<DataFlavor>(set);
    }
    
    private static Set<DataFlavor> convertMimeTypeToDataFlavors(final String s) {
        final LinkedHashSet set = new LinkedHashSet();
        String subType = null;
        try {
            subType = new MimeType(s).getSubType();
        }
        catch (final MimeTypeParseException ex) {}
        if (DataTransferer.doesSubtypeSupportCharset(subType, null)) {
            if ("text/plain".equals(s)) {
                set.add(DataFlavor.stringFlavor);
            }
            final String[] unicode_TEXT_CLASSES = SystemFlavorMap.UNICODE_TEXT_CLASSES;
            for (int length = unicode_TEXT_CLASSES.length, i = 0; i < length; ++i) {
                for (final String s2 : handleHtmlMimeTypes(s, s + ";charset=Unicode;class=" + unicode_TEXT_CLASSES[i])) {
                    Object o = null;
                    try {
                        o = new DataFlavor(s2);
                    }
                    catch (final ClassNotFoundException ex2) {}
                    set.add(o);
                }
            }
            for (final String s3 : DataTransferer.standardEncodings()) {
                final String[] encoded_TEXT_CLASSES = SystemFlavorMap.ENCODED_TEXT_CLASSES;
                for (int length2 = encoded_TEXT_CLASSES.length, j = 0; j < length2; ++j) {
                    for (final String s4 : handleHtmlMimeTypes(s, s + ";charset=" + s3 + ";class=" + encoded_TEXT_CLASSES[j])) {
                        DataFlavor plainTextFlavor = null;
                        try {
                            plainTextFlavor = new DataFlavor(s4);
                            if (plainTextFlavor.equals(DataFlavor.plainTextFlavor)) {
                                plainTextFlavor = DataFlavor.plainTextFlavor;
                            }
                        }
                        catch (final ClassNotFoundException ex3) {}
                        set.add(plainTextFlavor);
                    }
                }
            }
            if ("text/plain".equals(s)) {
                set.add(DataFlavor.plainTextFlavor);
            }
        }
        else {
            for (final String s5 : SystemFlavorMap.ENCODED_TEXT_CLASSES) {
                Object o2 = null;
                try {
                    o2 = new DataFlavor(s + ";class=" + s5);
                }
                catch (final ClassNotFoundException ex4) {}
                set.add(o2);
            }
        }
        return set;
    }
    
    private static LinkedHashSet<String> handleHtmlMimeTypes(final String s, final String s2) {
        final LinkedHashSet set = new LinkedHashSet();
        if ("text/html".equals(s)) {
            final String[] htmlDocumntTypes = SystemFlavorMap.htmlDocumntTypes;
            for (int length = htmlDocumntTypes.length, i = 0; i < length; ++i) {
                set.add(s2 + ";document=" + htmlDocumntTypes[i]);
            }
        }
        else {
            set.add(s2);
        }
        return set;
    }
    
    @Override
    public synchronized Map<DataFlavor, String> getNativesForFlavors(DataFlavor[] array) {
        if (array == null) {
            final List<DataFlavor> flavorsForNative = this.getFlavorsForNative(null);
            array = new DataFlavor[flavorsForNative.size()];
            flavorsForNative.toArray(array);
        }
        final HashMap hashMap = new HashMap(array.length, 1.0f);
        for (final DataFlavor dataFlavor : array) {
            final List<String> nativesForFlavor = this.getNativesForFlavor(dataFlavor);
            hashMap.put(dataFlavor, nativesForFlavor.isEmpty() ? null : ((String)nativesForFlavor.get(0)));
        }
        return hashMap;
    }
    
    @Override
    public synchronized Map<String, DataFlavor> getFlavorsForNatives(String[] array) {
        if (array == null) {
            final List<String> nativesForFlavor = this.getNativesForFlavor(null);
            array = new String[nativesForFlavor.size()];
            nativesForFlavor.toArray(array);
        }
        final HashMap hashMap = new HashMap(array.length, 1.0f);
        for (final String s : array) {
            final List<DataFlavor> flavorsForNative = this.getFlavorsForNative(s);
            hashMap.put(s, flavorsForNative.isEmpty() ? null : ((DataFlavor)flavorsForNative.get(0)));
        }
        return hashMap;
    }
    
    public synchronized void addUnencodedNativeForFlavor(final DataFlavor dataFlavor, final String s) {
        Objects.requireNonNull(s, "Null native not permitted");
        Objects.requireNonNull(dataFlavor, "Null flavor not permitted");
        LinkedHashSet set = this.getFlavorToNative().get(dataFlavor);
        if (set == null) {
            set = new LinkedHashSet(1);
            this.getFlavorToNative().put(dataFlavor, set);
        }
        set.add(s);
        this.nativesForFlavorCache.remove(dataFlavor);
    }
    
    public synchronized void setNativesForFlavor(final DataFlavor dataFlavor, final String[] array) {
        Objects.requireNonNull(array, "Null natives not permitted");
        Objects.requireNonNull(dataFlavor, "Null flavors not permitted");
        this.getFlavorToNative().remove(dataFlavor);
        for (int length = array.length, i = 0; i < length; ++i) {
            this.addUnencodedNativeForFlavor(dataFlavor, array[i]);
        }
        this.disabledMappingGenerationKeys.add(dataFlavor);
        this.nativesForFlavorCache.remove(dataFlavor);
    }
    
    public synchronized void addFlavorForUnencodedNative(final String s, final DataFlavor dataFlavor) {
        Objects.requireNonNull(s, "Null native not permitted");
        Objects.requireNonNull(dataFlavor, "Null flavor not permitted");
        LinkedHashSet set = this.getNativeToFlavor().get(s);
        if (set == null) {
            set = new LinkedHashSet(1);
            this.getNativeToFlavor().put(s, set);
        }
        set.add(dataFlavor);
        this.flavorsForNativeCache.remove(s);
    }
    
    public synchronized void setFlavorsForNative(final String s, final DataFlavor[] array) {
        Objects.requireNonNull(s, "Null native not permitted");
        Objects.requireNonNull(array, "Null flavors not permitted");
        this.getNativeToFlavor().remove(s);
        for (int length = array.length, i = 0; i < length; ++i) {
            this.addFlavorForUnencodedNative(s, array[i]);
        }
        this.disabledMappingGenerationKeys.add(s);
        this.flavorsForNativeCache.remove(s);
    }
    
    public static String encodeJavaMIMEType(final String s) {
        return (s != null) ? (SystemFlavorMap.JavaMIME + s) : null;
    }
    
    public static String encodeDataFlavor(final DataFlavor dataFlavor) {
        return (dataFlavor != null) ? encodeJavaMIMEType(dataFlavor.getMimeType()) : null;
    }
    
    public static boolean isJavaMIMEType(final String s) {
        return s != null && s.startsWith(SystemFlavorMap.JavaMIME, 0);
    }
    
    public static String decodeJavaMIMEType(final String s) {
        return isJavaMIMEType(s) ? s.substring(SystemFlavorMap.JavaMIME.length(), s.length()).trim() : null;
    }
    
    public static DataFlavor decodeDataFlavor(final String s) throws ClassNotFoundException {
        final String decodeJavaMIMEType = decodeJavaMIMEType(s);
        return (decodeJavaMIMEType != null) ? new DataFlavor(decodeJavaMIMEType) : null;
    }
    
    static {
        SystemFlavorMap.JavaMIME = "JAVA_DATAFLAVOR:";
        FLAVOR_MAP_KEY = new Object();
        UNICODE_TEXT_CLASSES = new String[] { "java.io.Reader", "java.lang.String", "java.nio.CharBuffer", "\"[C\"" };
        ENCODED_TEXT_CLASSES = new String[] { "java.io.InputStream", "java.nio.ByteBuffer", "\"[B\"" };
        htmlDocumntTypes = new String[] { "all", "selection", "fragment" };
    }
    
    private static final class SoftCache<K, V>
    {
        Map<K, SoftReference<LinkedHashSet<V>>> cache;
        
        public void put(final K k, final LinkedHashSet<V> set) {
            if (this.cache == null) {
                this.cache = new HashMap<K, SoftReference<LinkedHashSet<V>>>(1);
            }
            this.cache.put(k, new SoftReference<LinkedHashSet<V>>(set));
        }
        
        public void remove(final K k) {
            if (this.cache == null) {
                return;
            }
            this.cache.remove(null);
            this.cache.remove(k);
        }
        
        public LinkedHashSet<V> check(final K k) {
            if (this.cache == null) {
                return null;
            }
            final SoftReference softReference = this.cache.get(k);
            if (softReference != null) {
                return (LinkedHashSet<V>)softReference.get();
            }
            return null;
        }
    }
}
