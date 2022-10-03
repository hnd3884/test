package eu.medsea.mimeutil;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collections;
import java.util.HashMap;
import java.util.zip.ZipException;
import eu.medsea.util.ZipJarUtil;
import java.util.LinkedHashMap;
import java.util.List;
import java.net.URL;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import eu.medsea.util.StringUtil;
import java.io.File;
import eu.medsea.mimeutil.detector.MimeDetector;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Set;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.regex.Pattern;

public class MimeUtil2
{
    private static final MimeLogger log;
    public static final MimeType DIRECTORY_MIME_TYPE;
    public static final MimeType UNKNOWN_MIME_TYPE;
    private static final Pattern mimeSplitter;
    private static Map mimeTypes;
    private static ByteOrder nativeByteOrder;
    private MimeDetectorRegistry mimeDetectorRegistry;
    
    public MimeUtil2() {
        this.mimeDetectorRegistry = new MimeDetectorRegistry();
    }
    
    public static void addKnownMimeType(final MimeType mimeType) {
        addKnownMimeType(mimeType.toString());
    }
    
    public static void addKnownMimeType(final String mimeType) {
        try {
            final String key = getMediaType(mimeType);
            Set s = MimeUtil2.mimeTypes.get(key);
            if (s == null) {
                s = new TreeSet();
            }
            s.add(getSubType(mimeType));
            MimeUtil2.mimeTypes.put(key, s);
        }
        catch (final MimeException ex) {}
    }
    
    public static Collection getKnownMimeTypes() {
        final Collection mimeTypes = new ArrayList();
        for (final String mediaType : MimeUtil2.mimeTypes.keySet()) {
            final Iterator it = MimeUtil2.mimeTypes.get(mediaType).iterator();
            while (it.hasNext()) {
                mimeTypes.add(mediaType + "/" + it.next());
            }
        }
        return mimeTypes;
    }
    
    public MimeDetector registerMimeDetector(final String mimeDetector) {
        return this.mimeDetectorRegistry.registerMimeDetector(mimeDetector);
    }
    
    public static String getExtension(final File file) {
        return getExtension(file.getName());
    }
    
    public static String getExtension(final String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return "";
        }
        final int index = fileName.indexOf(".");
        return (index < 0) ? "" : fileName.substring(index + 1);
    }
    
    public static MimeType getFirstMimeType(final String mimeTypes) {
        if (mimeTypes != null && mimeTypes.trim().length() != 0) {
            return new MimeType(mimeTypes.split(",")[0].trim());
        }
        return null;
    }
    
    public static String getMediaType(final String mimeType) throws MimeException {
        return new MimeType(mimeType).getMediaType();
    }
    
    public static double getMimeQuality(final String mimeType) throws MimeException {
        if (mimeType == null) {
            throw new MimeException("Invalid MimeType [" + mimeType + "].");
        }
        final String[] parts = MimeUtil2.mimeSplitter.split(mimeType);
        if (parts.length < 2) {
            throw new MimeException("Invalid MimeType [" + mimeType + "].");
        }
        if (parts.length > 2) {
            for (int i = 2; i < parts.length; ++i) {
                if (parts[i].trim().startsWith("q=")) {
                    try {
                        final double d = Double.parseDouble(parts[i].split("=")[1].trim());
                        return (d > 1.0) ? 1.0 : d;
                    }
                    catch (final NumberFormatException e) {
                        throw new MimeException("Invalid MIME quality indicator [" + parts[i].trim() + "]. Must be a valid double between 0 and 1");
                    }
                    catch (final Exception e2) {
                        throw new MimeException("Error parsing MIME quality indicator.", e2);
                    }
                }
            }
        }
        if (StringUtil.contains(parts[0], "*")) {
            return 0.01;
        }
        if (StringUtil.contains(parts[1], "*")) {
            return 0.02;
        }
        return 1.0;
    }
    
    public MimeDetector getMimeDetector(final String name) {
        return this.mimeDetectorRegistry.getMimeDetector(name);
    }
    
    public final Collection getMimeTypes(final byte[] data) throws MimeException {
        return this.getMimeTypes(data, MimeUtil2.UNKNOWN_MIME_TYPE);
    }
    
    public final Collection getMimeTypes(final byte[] data, final MimeType unknownMimeType) throws MimeException {
        final Collection mimeTypes = new MimeTypeHashSet();
        if (data == null) {
            MimeUtil2.log.error("byte array cannot be null.");
        }
        else {
            if (MimeUtil2.log.isDebugEnabled()) {
                try {
                    MimeUtil2.log.debug("Getting MIME types for byte array [" + StringUtil.getHexString(data) + "].");
                }
                catch (final UnsupportedEncodingException e) {
                    throw new MimeException(e);
                }
            }
            mimeTypes.addAll(this.mimeDetectorRegistry.getMimeTypes(data));
            mimeTypes.remove(unknownMimeType);
        }
        if (mimeTypes.isEmpty()) {
            mimeTypes.add(unknownMimeType);
        }
        if (MimeUtil2.log.isDebugEnabled()) {
            MimeUtil2.log.debug("Retrieved MIME types [" + mimeTypes.toString() + "]");
        }
        return mimeTypes;
    }
    
    public final Collection getMimeTypes(final File file) throws MimeException {
        return this.getMimeTypes(file, MimeUtil2.UNKNOWN_MIME_TYPE);
    }
    
    public final Collection getMimeTypes(final File file, final MimeType unknownMimeType) throws MimeException {
        final Collection mimeTypes = new MimeTypeHashSet();
        if (file == null) {
            MimeUtil2.log.error("File reference cannot be null.");
        }
        else {
            if (MimeUtil2.log.isDebugEnabled()) {
                MimeUtil2.log.debug("Getting MIME types for file [" + file.getAbsolutePath() + "].");
            }
            if (file.isDirectory()) {
                mimeTypes.add(MimeUtil2.DIRECTORY_MIME_TYPE);
            }
            else {
                mimeTypes.addAll(this.mimeDetectorRegistry.getMimeTypes(file));
                mimeTypes.remove(unknownMimeType);
            }
        }
        if (mimeTypes.isEmpty()) {
            mimeTypes.add(unknownMimeType);
        }
        if (MimeUtil2.log.isDebugEnabled()) {
            MimeUtil2.log.debug("Retrieved MIME types [" + mimeTypes.toString() + "]");
        }
        return mimeTypes;
    }
    
    public final Collection getMimeTypes(final InputStream in) throws MimeException {
        return this.getMimeTypes(in, MimeUtil2.UNKNOWN_MIME_TYPE);
    }
    
    public final Collection getMimeTypes(final InputStream in, final MimeType unknownMimeType) throws MimeException {
        final Collection mimeTypes = new MimeTypeHashSet();
        if (in == null) {
            MimeUtil2.log.error("InputStream reference cannot be null.");
        }
        else {
            if (!in.markSupported()) {
                throw new MimeException("InputStream must support the mark() and reset() methods.");
            }
            if (MimeUtil2.log.isDebugEnabled()) {
                MimeUtil2.log.debug("Getting MIME types for InputSteam [" + in + "].");
            }
            mimeTypes.addAll(this.mimeDetectorRegistry.getMimeTypes(in));
            mimeTypes.remove(unknownMimeType);
        }
        if (mimeTypes.isEmpty()) {
            mimeTypes.add(unknownMimeType);
        }
        if (MimeUtil2.log.isDebugEnabled()) {
            MimeUtil2.log.debug("Retrieved MIME types [" + mimeTypes.toString() + "]");
        }
        return mimeTypes;
    }
    
    public final Collection getMimeTypes(final String fileName) throws MimeException {
        return this.getMimeTypes(fileName, MimeUtil2.UNKNOWN_MIME_TYPE);
    }
    
    public final Collection getMimeTypes(final String fileName, final MimeType unknownMimeType) throws MimeException {
        final Collection mimeTypes = new MimeTypeHashSet();
        if (fileName == null) {
            MimeUtil2.log.error("fileName cannot be null.");
        }
        else {
            if (MimeUtil2.log.isDebugEnabled()) {
                MimeUtil2.log.debug("Getting MIME types for file name [" + fileName + "].");
            }
            final File file = new File(fileName);
            if (file.isDirectory()) {
                mimeTypes.add(MimeUtil2.DIRECTORY_MIME_TYPE);
            }
            else {
                mimeTypes.addAll(this.mimeDetectorRegistry.getMimeTypes(fileName));
                mimeTypes.remove(unknownMimeType);
            }
        }
        if (mimeTypes.isEmpty()) {
            mimeTypes.add(unknownMimeType);
        }
        if (MimeUtil2.log.isDebugEnabled()) {
            MimeUtil2.log.debug("Retrieved MIME types [" + mimeTypes.toString() + "]");
        }
        return mimeTypes;
    }
    
    public final Collection getMimeTypes(final URL url) throws MimeException {
        return this.getMimeTypes(url, MimeUtil2.UNKNOWN_MIME_TYPE);
    }
    
    public final Collection getMimeTypes(final URL url, final MimeType unknownMimeType) throws MimeException {
        final Collection mimeTypes = new MimeTypeHashSet();
        if (url == null) {
            MimeUtil2.log.error("URL reference cannot be null.");
        }
        else {
            if (MimeUtil2.log.isDebugEnabled()) {
                MimeUtil2.log.debug("Getting MIME types for URL [" + url + "].");
            }
            final File file = new File(url.getPath());
            if (file.isDirectory()) {
                mimeTypes.add(MimeUtil2.DIRECTORY_MIME_TYPE);
            }
            else {
                mimeTypes.addAll(this.mimeDetectorRegistry.getMimeTypes(url));
                mimeTypes.remove(unknownMimeType);
            }
        }
        if (mimeTypes.isEmpty()) {
            mimeTypes.add(unknownMimeType);
        }
        if (MimeUtil2.log.isDebugEnabled()) {
            MimeUtil2.log.debug("Retrieved MIME types [" + mimeTypes.toString() + "]");
        }
        return mimeTypes;
    }
    
    public static ByteOrder getNativeOrder() {
        return MimeUtil2.nativeByteOrder;
    }
    
    public static MimeType getPreferedMimeType(String accept, final String canProvide) {
        if (canProvide == null || canProvide.trim().length() == 0) {
            throw new MimeException("Must specify at least one MIME type that can be provided.");
        }
        if (accept == null || accept.trim().length() == 0) {
            accept = "*/*";
        }
        if (accept.indexOf(":") > 0) {
            accept = accept.substring(accept.indexOf(":") + 1);
        }
        accept = accept.replaceAll(" ", "");
        return getBestMatch(accept, getList(canProvide));
    }
    
    public static MimeType getMostSpecificMimeType(final Collection mimeTypes) {
        MimeType mimeType = null;
        int specificity = 0;
        for (final MimeType mt : mimeTypes) {
            if (mt.getSpecificity() > specificity) {
                mimeType = mt;
                specificity = mimeType.getSpecificity();
            }
        }
        return mimeType;
    }
    
    public static String getSubType(final String mimeType) throws MimeException {
        return new MimeType(mimeType).getSubType();
    }
    
    public static boolean isMimeTypeKnown(final MimeType mimeType) {
        try {
            final Set s = MimeUtil2.mimeTypes.get(mimeType.getMediaType());
            return s != null && s.contains(mimeType.getSubType());
        }
        catch (final MimeException e) {
            return false;
        }
    }
    
    public static boolean isMimeTypeKnown(final String mimeType) {
        return isMimeTypeKnown(new MimeType(mimeType));
    }
    
    public static boolean isTextMimeType(final MimeType mimeType) {
        return mimeType instanceof TextMimeType;
    }
    
    public MimeDetector unregisterMimeDetector(final MimeDetector mimeDetector) {
        return this.mimeDetectorRegistry.unregisterMimeDetector(mimeDetector);
    }
    
    public MimeDetector unregisterMimeDetector(final String mimeDetector) {
        return this.mimeDetectorRegistry.unregisterMimeDetector(mimeDetector);
    }
    
    public static double getQuality(final String mimeType) throws MimeException {
        return getMimeQuality(mimeType);
    }
    
    private static MimeType getBestMatch(final String accept, final List canProvideList) {
        if (canProvideList.size() == 1) {
            return new MimeType(canProvideList.get(0));
        }
        final Map wantedMap = normaliseWantedMap(accept, canProvideList);
        MimeType bestMatch = null;
        double qos = 0.0;
        final Iterator it = wantedMap.keySet().iterator();
        while (it.hasNext()) {
            final List wantedList = wantedMap.get(it.next());
            for (final String mimeType : wantedList) {
                final double q = getMimeQuality(mimeType);
                final String majorComponent = getMediaType(mimeType);
                final String minorComponent = getSubType(mimeType);
                if (q > qos) {
                    qos = q;
                    bestMatch = new MimeType(majorComponent + "/" + minorComponent);
                }
            }
        }
        return bestMatch;
    }
    
    private static List getList(final String options) {
        final List list = new ArrayList();
        final String[] array = options.split(",");
        for (int i = 0; i < array.length; ++i) {
            list.add(array[i].trim());
        }
        return list;
    }
    
    private static Map normaliseWantedMap(final String accept, final List canProvide) {
        final Map map = new LinkedHashMap();
        final String[] array = accept.split(",");
        for (int i = 0; i < array.length; ++i) {
            final String mimeType = array[i].trim();
            final String major = getMediaType(mimeType);
            final String minor = getSubType(mimeType);
            final double qos = getMimeQuality(mimeType);
            if (StringUtil.contains(major, "*")) {
                for (final String mt : canProvide) {
                    List list = map.get(getMediaType(mt));
                    if (list == null) {
                        list = new ArrayList();
                    }
                    list.add(mt + ";q=" + qos);
                    map.put(getMediaType(mt), list);
                }
            }
            else if (StringUtil.contains(minor, "*")) {
                for (final String mt : canProvide) {
                    if (getMediaType(mt).equals(major)) {
                        List list = map.get(major);
                        if (list == null) {
                            list = new ArrayList();
                        }
                        list.add(major + "/" + getSubType(mt) + ";q=" + qos);
                        map.put(major, list);
                    }
                }
            }
            else if (canProvide.contains(major + "/" + minor)) {
                List list2 = map.get(major);
                if (list2 == null) {
                    list2 = new ArrayList();
                }
                list2.add(major + "/" + minor + ";q=" + qos);
                map.put(major, list2);
            }
        }
        return map;
    }
    
    public static InputStream getInputStreamForURL(final URL url) throws Exception {
        try {
            return url.openStream();
        }
        catch (final ZipException e) {
            return ZipJarUtil.getInputStreamForURL(url);
        }
    }
    
    static {
        log = new MimeLogger(MimeUtil2.class.getName());
        DIRECTORY_MIME_TYPE = new MimeType("application/directory");
        UNKNOWN_MIME_TYPE = new MimeType("application/x-unknown-mime-type");
        mimeSplitter = Pattern.compile("[/;]++");
        MimeUtil2.mimeTypes = Collections.synchronizedMap(new HashMap<Object, Object>());
        MimeUtil2.nativeByteOrder = ByteOrder.nativeOrder();
    }
    
    public static class MimeLogger
    {
        Logger logger;
        
        public MimeLogger(final String loggerName) {
            this.logger = Logger.getLogger(loggerName);
        }
        
        public void debug(final String msg) {
            this.logger.fine(msg);
        }
        
        public void warn(final String msg) {
            this.logger.warning(msg);
        }
        
        public void error(final String msg) {
            this.logger.severe(msg);
        }
        
        public void error(final String msg, final Exception e) {
            this.logger.log(Level.SEVERE, msg, e);
        }
        
        public void warn(final String msg, final Exception e) {
            this.logger.log(Level.WARNING, msg, e);
        }
        
        public boolean isDebugEnabled() {
            return this.logger.isLoggable(Level.FINE);
        }
    }
}
