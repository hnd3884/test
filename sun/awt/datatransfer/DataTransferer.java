package sun.awt.datatransfer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.Serializable;
import java.nio.charset.CharsetEncoder;
import java.io.BufferedReader;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.LinkedHashSet;
import java.awt.EventQueue;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import java.util.Stack;
import java.io.SequenceInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.ImageWriter;
import javax.imageio.ImageTypeSpecifier;
import java.awt.Graphics;
import sun.awt.image.ImageRepresentation;
import java.util.Hashtable;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import sun.awt.image.ToolkitImage;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageReader;
import javax.imageio.ImageIO;
import java.lang.reflect.Modifier;
import java.security.PrivilegedAction;
import java.lang.reflect.Constructor;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.security.Permission;
import java.io.FilePermission;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.ProtectionDomain;
import java.security.PrivilegedExceptionAction;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.SortedMap;
import java.awt.datatransfer.Transferable;
import java.util.Collections;
import java.util.List;
import java.awt.datatransfer.FlavorTable;
import java.awt.datatransfer.FlavorMap;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.Charset;
import java.awt.Toolkit;
import sun.awt.ComponentFactory;
import java.io.File;
import java.util.ArrayList;
import sun.util.logging.PlatformLogger;
import java.util.Set;
import java.util.Map;
import java.awt.datatransfer.DataFlavor;

public abstract class DataTransferer
{
    public static final DataFlavor plainTextStringFlavor;
    public static final DataFlavor javaTextEncodingFlavor;
    private static final Map textMIMESubtypeCharsetSupport;
    private static String defaultEncoding;
    private static final Set textNatives;
    private static final Map nativeCharsets;
    private static final Map nativeEOLNs;
    private static final Map nativeTerminators;
    private static final String DATA_CONVERTER_KEY = "DATA_CONVERTER_KEY";
    private static DataTransferer transferer;
    private static final PlatformLogger dtLog;
    private static final String[] DEPLOYMENT_CACHE_PROPERTIES;
    private static final ArrayList<File> deploymentCacheDirectoryList;
    
    public static synchronized DataTransferer getInstance() {
        return ((ComponentFactory)Toolkit.getDefaultToolkit()).getDataTransferer();
    }
    
    public static String canonicalName(final String s) {
        if (s == null) {
            return null;
        }
        try {
            return Charset.forName(s).name();
        }
        catch (final IllegalCharsetNameException ex) {
            return s;
        }
        catch (final UnsupportedCharsetException ex2) {
            return s;
        }
    }
    
    public static String getTextCharset(final DataFlavor dataFlavor) {
        if (!isFlavorCharsetTextType(dataFlavor)) {
            return null;
        }
        final String parameter = dataFlavor.getParameter("charset");
        return (parameter != null) ? parameter : getDefaultTextCharset();
    }
    
    public static String getDefaultTextCharset() {
        if (DataTransferer.defaultEncoding != null) {
            return DataTransferer.defaultEncoding;
        }
        return DataTransferer.defaultEncoding = Charset.defaultCharset().name();
    }
    
    public static boolean doesSubtypeSupportCharset(final DataFlavor dataFlavor) {
        if (DataTransferer.dtLog.isLoggable(PlatformLogger.Level.FINE) && !"text".equals(dataFlavor.getPrimaryType())) {
            DataTransferer.dtLog.fine("Assertion (\"text\".equals(flavor.getPrimaryType())) failed");
        }
        final String subType = dataFlavor.getSubType();
        if (subType == null) {
            return false;
        }
        final Object value = DataTransferer.textMIMESubtypeCharsetSupport.get(subType);
        if (value != null) {
            return value == Boolean.TRUE;
        }
        final boolean b = dataFlavor.getParameter("charset") != null;
        DataTransferer.textMIMESubtypeCharsetSupport.put(subType, b ? Boolean.TRUE : Boolean.FALSE);
        return b;
    }
    
    public static boolean doesSubtypeSupportCharset(final String s, final String s2) {
        final Boolean value = DataTransferer.textMIMESubtypeCharsetSupport.get(s);
        if (value != null) {
            return value == Boolean.TRUE;
        }
        final boolean b = s2 != null;
        DataTransferer.textMIMESubtypeCharsetSupport.put(s, b ? Boolean.TRUE : Boolean.FALSE);
        return b;
    }
    
    public static boolean isFlavorCharsetTextType(final DataFlavor dataFlavor) {
        if (DataFlavor.stringFlavor.equals(dataFlavor)) {
            return true;
        }
        if (!"text".equals(dataFlavor.getPrimaryType()) || !doesSubtypeSupportCharset(dataFlavor)) {
            return false;
        }
        final Class<?> representationClass = dataFlavor.getRepresentationClass();
        if (dataFlavor.isRepresentationClassReader() || String.class.equals(representationClass) || dataFlavor.isRepresentationClassCharBuffer() || char[].class.equals(representationClass)) {
            return true;
        }
        if (!dataFlavor.isRepresentationClassInputStream() && !dataFlavor.isRepresentationClassByteBuffer() && !byte[].class.equals(representationClass)) {
            return false;
        }
        final String parameter = dataFlavor.getParameter("charset");
        return parameter == null || isEncodingSupported(parameter);
    }
    
    public static boolean isFlavorNoncharsetTextType(final DataFlavor dataFlavor) {
        return "text".equals(dataFlavor.getPrimaryType()) && !doesSubtypeSupportCharset(dataFlavor) && (dataFlavor.isRepresentationClassInputStream() || dataFlavor.isRepresentationClassByteBuffer() || byte[].class.equals(dataFlavor.getRepresentationClass()));
    }
    
    public static boolean isEncodingSupported(final String s) {
        if (s == null) {
            return false;
        }
        try {
            return Charset.isSupported(s);
        }
        catch (final IllegalCharsetNameException ex) {
            return false;
        }
    }
    
    public static boolean isRemote(final Class<?> clazz) {
        return RMI.isRemote(clazz);
    }
    
    public static Set<String> standardEncodings() {
        return StandardEncodingsHolder.standardEncodings;
    }
    
    public static FlavorTable adaptFlavorMap(final FlavorMap flavorMap) {
        if (flavorMap instanceof FlavorTable) {
            return (FlavorTable)flavorMap;
        }
        return new FlavorTable() {
            @Override
            public Map getNativesForFlavors(final DataFlavor[] array) {
                return flavorMap.getNativesForFlavors(array);
            }
            
            @Override
            public Map getFlavorsForNatives(final String[] array) {
                return flavorMap.getFlavorsForNatives(array);
            }
            
            @Override
            public List getNativesForFlavor(final DataFlavor dataFlavor) {
                final String s = this.getNativesForFlavors(new DataFlavor[] { dataFlavor }).get(dataFlavor);
                if (s != null) {
                    final ArrayList list = new ArrayList(1);
                    list.add(s);
                    return list;
                }
                return Collections.EMPTY_LIST;
            }
            
            @Override
            public List getFlavorsForNative(final String s) {
                final DataFlavor dataFlavor = this.getFlavorsForNatives(new String[] { s }).get(s);
                if (dataFlavor != null) {
                    final ArrayList list = new ArrayList(1);
                    list.add(dataFlavor);
                    return list;
                }
                return Collections.EMPTY_LIST;
            }
        };
    }
    
    public abstract String getDefaultUnicodeEncoding();
    
    public void registerTextFlavorProperties(final String s, final String s2, final String s3, final String s4) {
        final Long formatForNativeAsLong = this.getFormatForNativeAsLong(s);
        DataTransferer.textNatives.add(formatForNativeAsLong);
        DataTransferer.nativeCharsets.put(formatForNativeAsLong, (s2 != null && s2.length() != 0) ? s2 : getDefaultTextCharset());
        if (s3 != null && s3.length() != 0 && !s3.equals("\n")) {
            DataTransferer.nativeEOLNs.put(formatForNativeAsLong, s3);
        }
        if (s4 != null && s4.length() != 0) {
            final Integer value = Integer.valueOf(s4);
            if (value > 0) {
                DataTransferer.nativeTerminators.put(formatForNativeAsLong, value);
            }
        }
    }
    
    protected boolean isTextFormat(final long n) {
        return DataTransferer.textNatives.contains(n);
    }
    
    protected String getCharsetForTextFormat(final Long n) {
        return DataTransferer.nativeCharsets.get(n);
    }
    
    public abstract boolean isLocaleDependentTextFormat(final long p0);
    
    public abstract boolean isFileFormat(final long p0);
    
    public abstract boolean isImageFormat(final long p0);
    
    protected boolean isURIListFormat(final long n) {
        return false;
    }
    
    public SortedMap<Long, DataFlavor> getFormatsForTransferable(final Transferable transferable, final FlavorTable flavorTable) {
        final DataFlavor[] transferDataFlavors = transferable.getTransferDataFlavors();
        if (transferDataFlavors == null) {
            return new TreeMap<Long, DataFlavor>();
        }
        return this.getFormatsForFlavors(transferDataFlavors, flavorTable);
    }
    
    public SortedMap getFormatsForFlavor(final DataFlavor dataFlavor, final FlavorTable flavorTable) {
        return this.getFormatsForFlavors(new DataFlavor[] { dataFlavor }, flavorTable);
    }
    
    public SortedMap<Long, DataFlavor> getFormatsForFlavors(final DataFlavor[] array, final FlavorTable flavorTable) {
        final HashMap hashMap = new HashMap(array.length);
        final HashMap hashMap2 = new HashMap(array.length);
        final HashMap hashMap3 = new HashMap(array.length);
        final HashMap hashMap4 = new HashMap(array.length);
        int n = 0;
        for (int i = array.length - 1; i >= 0; --i) {
            final DataFlavor dataFlavor = array[i];
            if (dataFlavor != null) {
                if (dataFlavor.isFlavorTextType() || dataFlavor.isFlavorJavaFileListType() || DataFlavor.imageFlavor.equals(dataFlavor) || dataFlavor.isRepresentationClassSerializable() || dataFlavor.isRepresentationClassInputStream() || dataFlavor.isRepresentationClassRemote()) {
                    final List<String> nativesForFlavor = flavorTable.getNativesForFlavor(dataFlavor);
                    int n2 = n + nativesForFlavor.size();
                    final Iterator iterator = nativesForFlavor.iterator();
                    while (iterator.hasNext()) {
                        final Long formatForNativeAsLong = this.getFormatForNativeAsLong((String)iterator.next());
                        final Integer value = n2--;
                        hashMap.put(formatForNativeAsLong, dataFlavor);
                        hashMap3.put(formatForNativeAsLong, value);
                        if (("text".equals(dataFlavor.getPrimaryType()) && "plain".equals(dataFlavor.getSubType())) || dataFlavor.equals(DataFlavor.stringFlavor)) {
                            hashMap2.put(formatForNativeAsLong, dataFlavor);
                            hashMap4.put(formatForNativeAsLong, value);
                        }
                    }
                    n = n2 + nativesForFlavor.size();
                }
            }
        }
        hashMap.putAll(hashMap2);
        hashMap3.putAll(hashMap4);
        final TreeMap treeMap = new TreeMap(new IndexOrderComparator(hashMap3, false));
        treeMap.putAll(hashMap);
        return (SortedMap<Long, DataFlavor>)treeMap;
    }
    
    public long[] getFormatsForTransferableAsArray(final Transferable transferable, final FlavorTable flavorTable) {
        return keysToLongArray(this.getFormatsForTransferable(transferable, flavorTable));
    }
    
    public long[] getFormatsForFlavorAsArray(final DataFlavor dataFlavor, final FlavorTable flavorTable) {
        return keysToLongArray(this.getFormatsForFlavor(dataFlavor, flavorTable));
    }
    
    public long[] getFormatsForFlavorsAsArray(final DataFlavor[] array, final FlavorTable flavorTable) {
        return keysToLongArray(this.getFormatsForFlavors(array, flavorTable));
    }
    
    public Map getFlavorsForFormat(final long n, final FlavorTable flavorTable) {
        return this.getFlavorsForFormats(new long[] { n }, flavorTable);
    }
    
    public Map getFlavorsForFormats(final long[] array, final FlavorTable flavorTable) {
        final HashMap hashMap = new HashMap(array.length);
        final HashSet set = new HashSet(array.length);
        final HashSet set2 = new HashSet(array.length);
        for (int i = 0; i < array.length; ++i) {
            final long n = array[i];
            for (final DataFlavor dataFlavor : flavorTable.getFlavorsForNative(this.getNativeForFormat(n))) {
                if (dataFlavor.isFlavorTextType() || dataFlavor.isFlavorJavaFileListType() || DataFlavor.imageFlavor.equals(dataFlavor) || dataFlavor.isRepresentationClassSerializable() || dataFlavor.isRepresentationClassInputStream() || dataFlavor.isRepresentationClassRemote()) {
                    final Long value = n;
                    final Object mapping = createMapping(value, dataFlavor);
                    hashMap.put(dataFlavor, value);
                    set.add(mapping);
                    set2.add(dataFlavor);
                }
            }
        }
        for (final DataFlavor dataFlavor2 : set2) {
            final Iterator<String> iterator3 = flavorTable.getNativesForFlavor(dataFlavor2).iterator();
            while (iterator3.hasNext()) {
                final Long formatForNativeAsLong = this.getFormatForNativeAsLong(iterator3.next());
                if (set.contains(createMapping(formatForNativeAsLong, dataFlavor2))) {
                    hashMap.put(dataFlavor2, formatForNativeAsLong);
                    break;
                }
            }
        }
        return hashMap;
    }
    
    public Set getFlavorsForFormatsAsSet(final long[] array, final FlavorTable flavorTable) {
        final HashSet set = new HashSet(array.length);
        for (int i = 0; i < array.length; ++i) {
            for (final DataFlavor dataFlavor : flavorTable.getFlavorsForNative(this.getNativeForFormat(array[i]))) {
                if (dataFlavor.isFlavorTextType() || dataFlavor.isFlavorJavaFileListType() || DataFlavor.imageFlavor.equals(dataFlavor) || dataFlavor.isRepresentationClassSerializable() || dataFlavor.isRepresentationClassInputStream() || dataFlavor.isRepresentationClassRemote()) {
                    set.add(dataFlavor);
                }
            }
        }
        return set;
    }
    
    public DataFlavor[] getFlavorsForFormatAsArray(final long n, final FlavorTable flavorTable) {
        return this.getFlavorsForFormatsAsArray(new long[] { n }, flavorTable);
    }
    
    public DataFlavor[] getFlavorsForFormatsAsArray(final long[] array, final FlavorTable flavorTable) {
        return setToSortedDataFlavorArray(this.getFlavorsForFormatsAsSet(array, flavorTable));
    }
    
    private static Object createMapping(final Object o, final Object o2) {
        return Arrays.asList(o, o2);
    }
    
    protected abstract Long getFormatForNativeAsLong(final String p0);
    
    protected abstract String getNativeForFormat(final long p0);
    
    private String getBestCharsetForTextFormat(final Long n, final Transferable transferable) throws IOException {
        String s = null;
        if (transferable != null && this.isLocaleDependentTextFormat(n) && transferable.isDataFlavorSupported(DataTransferer.javaTextEncodingFlavor)) {
            try {
                s = new String((byte[])transferable.getTransferData(DataTransferer.javaTextEncodingFlavor), "UTF-8");
            }
            catch (final UnsupportedFlavorException ex) {}
        }
        else {
            s = this.getCharsetForTextFormat(n);
        }
        if (s == null) {
            s = getDefaultTextCharset();
        }
        return s;
    }
    
    private byte[] translateTransferableString(String string, final long n) throws IOException {
        final Long value = n;
        final String bestCharsetForTextFormat = this.getBestCharsetForTextFormat(value, null);
        final String s = DataTransferer.nativeEOLNs.get(value);
        if (s != null) {
            final int length = string.length();
            final StringBuffer sb = new StringBuffer(length * 2);
            for (int i = 0; i < length; ++i) {
                if (string.startsWith(s, i)) {
                    sb.append(s);
                    i += s.length() - 1;
                }
                else {
                    final char char1 = string.charAt(i);
                    if (char1 == '\n') {
                        sb.append(s);
                    }
                    else {
                        sb.append(char1);
                    }
                }
            }
            string = sb.toString();
        }
        byte[] bytes = string.getBytes(bestCharsetForTextFormat);
        final Integer n2 = DataTransferer.nativeTerminators.get(value);
        if (n2 != null) {
            final byte[] array = new byte[bytes.length + n2];
            System.arraycopy(bytes, 0, array, 0, bytes.length);
            for (int j = bytes.length; j < array.length; ++j) {
                array[j] = 0;
            }
            bytes = array;
        }
        return bytes;
    }
    
    private String translateBytesToString(final byte[] array, final long n, final Transferable transferable) throws IOException {
        final Long value = n;
        final String bestCharsetForTextFormat = this.getBestCharsetForTextFormat(value, transferable);
        final String s = DataTransferer.nativeEOLNs.get(value);
        final Integer n2 = DataTransferer.nativeTerminators.get(value);
        int i;
        if (n2 != null) {
            final int intValue = n2;
            i = 0;
        Label_0061:
            while (i < array.length - intValue + 1) {
                for (int j = i; j < i + intValue; ++j) {
                    if (array[j] != 0) {
                        i += intValue;
                        continue Label_0061;
                    }
                }
                break;
            }
        }
        else {
            i = array.length;
        }
        String s2 = new String(array, 0, i, bestCharsetForTextFormat);
        if (s != null) {
            final char[] charArray = s2.toCharArray();
            final char[] charArray2 = s.toCharArray();
            int n3 = 0;
            int k = 0;
            while (k < charArray.length) {
                if (k + charArray2.length > charArray.length) {
                    charArray[n3++] = charArray[k++];
                }
                else {
                    boolean b = true;
                    for (int l = 0, n4 = k; l < charArray2.length; ++l, ++n4) {
                        if (charArray2[l] != charArray[n4]) {
                            b = false;
                            break;
                        }
                    }
                    if (b) {
                        charArray[n3++] = '\n';
                        k += charArray2.length;
                    }
                    else {
                        charArray[n3++] = charArray[k++];
                    }
                }
            }
            s2 = new String(charArray, 0, n3);
        }
        return s2;
    }
    
    public byte[] translateTransferable(final Transferable transferable, final DataFlavor dataFlavor, final long n) throws IOException {
        Object o;
        boolean b;
        try {
            o = transferable.getTransferData(dataFlavor);
            if (o == null) {
                return null;
            }
            if (dataFlavor.equals(DataFlavor.plainTextFlavor) && !(o instanceof InputStream)) {
                o = transferable.getTransferData(DataFlavor.stringFlavor);
                if (o == null) {
                    return null;
                }
                b = true;
            }
            else {
                b = false;
            }
        }
        catch (final UnsupportedFlavorException ex) {
            throw new IOException(ex.getMessage());
        }
        if (b || (String.class.equals(dataFlavor.getRepresentationClass()) && isFlavorCharsetTextType(dataFlavor) && this.isTextFormat(n))) {
            return this.translateTransferableString(this.removeSuspectedData(dataFlavor, transferable, (String)o), n);
        }
        if (dataFlavor.isRepresentationClassReader()) {
            if (!isFlavorCharsetTextType(dataFlavor) || !this.isTextFormat(n)) {
                throw new IOException("cannot transfer non-text data as Reader");
            }
            final StringBuffer sb = new StringBuffer();
            try (final Reader reader = (Reader)o) {
                int read;
                while ((read = reader.read()) != -1) {
                    sb.append((char)read);
                }
            }
            return this.translateTransferableString(sb.toString(), n);
        }
        else if (dataFlavor.isRepresentationClassCharBuffer()) {
            if (!isFlavorCharsetTextType(dataFlavor) || !this.isTextFormat(n)) {
                throw new IOException("cannot transfer non-text data as CharBuffer");
            }
            final CharBuffer charBuffer = (CharBuffer)o;
            final int remaining = charBuffer.remaining();
            final char[] array = new char[remaining];
            charBuffer.get(array, 0, remaining);
            return this.translateTransferableString(new String(array), n);
        }
        else if (char[].class.equals(dataFlavor.getRepresentationClass())) {
            if (!isFlavorCharsetTextType(dataFlavor) || !this.isTextFormat(n)) {
                throw new IOException("cannot transfer non-text data as char array");
            }
            return this.translateTransferableString(new String((char[])o), n);
        }
        else if (dataFlavor.isRepresentationClassByteBuffer()) {
            final ByteBuffer byteBuffer = (ByteBuffer)o;
            final int remaining2 = byteBuffer.remaining();
            final byte[] array2 = new byte[remaining2];
            byteBuffer.get(array2, 0, remaining2);
            if (isFlavorCharsetTextType(dataFlavor) && this.isTextFormat(n)) {
                return this.translateTransferableString(new String(array2, getTextCharset(dataFlavor)), n);
            }
            return array2;
        }
        else if (byte[].class.equals(dataFlavor.getRepresentationClass())) {
            final byte[] array3 = (byte[])o;
            if (isFlavorCharsetTextType(dataFlavor) && this.isTextFormat(n)) {
                return this.translateTransferableString(new String(array3, getTextCharset(dataFlavor)), n);
            }
            return array3;
        }
        else {
            if (!DataFlavor.imageFlavor.equals(dataFlavor)) {
                byte[] array4 = null;
                if (this.isFileFormat(n)) {
                    if (!DataFlavor.javaFileListFlavor.equals(dataFlavor)) {
                        throw new IOException("data translation failed");
                    }
                    try (final ByteArrayOutputStream convertFileListToBytes = this.convertFileListToBytes(this.castToFiles((List)o, getUserProtectionDomain(transferable)))) {
                        array4 = convertFileListToBytes.toByteArray();
                    }
                }
                else if (this.isURIListFormat(n)) {
                    if (!DataFlavor.javaFileListFlavor.equals(dataFlavor)) {
                        throw new IOException("data translation failed");
                    }
                    final String nativeForFormat = this.getNativeForFormat(n);
                    String parameter = null;
                    if (nativeForFormat != null) {
                        try {
                            parameter = new DataFlavor(nativeForFormat).getParameter("charset");
                        }
                        catch (final ClassNotFoundException ex2) {
                            throw new IOException(ex2);
                        }
                    }
                    if (parameter == null) {
                        parameter = "UTF-8";
                    }
                    final ArrayList<String> castToFiles = this.castToFiles((List)o, getUserProtectionDomain(transferable));
                    final ArrayList list = new ArrayList<String>(castToFiles.size());
                    final Iterator iterator = castToFiles.iterator();
                    while (iterator.hasNext()) {
                        final URI uri = new File((String)iterator.next()).toURI();
                        try {
                            list.add(new URI(uri.getScheme(), "", uri.getPath(), uri.getFragment()).toString());
                        }
                        catch (final URISyntaxException ex3) {
                            throw new IOException(ex3);
                        }
                    }
                    final byte[] bytes = "\r\n".getBytes(parameter);
                    try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                        for (int i = 0; i < list.size(); ++i) {
                            final byte[] bytes2 = list.get(i).getBytes(parameter);
                            byteArrayOutputStream.write(bytes2, 0, bytes2.length);
                            byteArrayOutputStream.write(bytes, 0, bytes.length);
                        }
                        array4 = byteArrayOutputStream.toByteArray();
                    }
                }
                else if (dataFlavor.isRepresentationClassInputStream()) {
                    if (!(o instanceof InputStream)) {
                        return new byte[0];
                    }
                    try (final ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream()) {
                        try (final InputStream inputStream = (InputStream)o) {
                            final int available = inputStream.available();
                            final byte[] array5 = new byte[(available > 8192) ? available : 8192];
                            boolean b2;
                            do {
                                final int read2;
                                if (!(b2 = ((read2 = inputStream.read(array5, 0, array5.length)) == -1))) {
                                    byteArrayOutputStream2.write(array5, 0, read2);
                                }
                            } while (!b2);
                        }
                        if (isFlavorCharsetTextType(dataFlavor) && this.isTextFormat(n)) {
                            return this.translateTransferableString(new String(byteArrayOutputStream2.toByteArray(), getTextCharset(dataFlavor)), n);
                        }
                        array4 = byteArrayOutputStream2.toByteArray();
                    }
                }
                else if (dataFlavor.isRepresentationClassRemote()) {
                    array4 = convertObjectToBytes(RMI.newMarshalledObject(o));
                }
                else {
                    if (!dataFlavor.isRepresentationClassSerializable()) {
                        throw new IOException("data translation failed");
                    }
                    array4 = convertObjectToBytes(o);
                }
                return array4;
            }
            if (!this.isImageFormat(n)) {
                throw new IOException("Data translation failed: not an image format");
            }
            final byte[] imageToPlatformBytes = this.imageToPlatformBytes((Image)o, n);
            if (imageToPlatformBytes == null) {
                throw new IOException("Data translation failed: cannot convert java image to native format");
            }
            return imageToPlatformBytes;
        }
    }
    
    private static byte[] convertObjectToBytes(final Object o) throws IOException {
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(o);
            return byteArrayOutputStream.toByteArray();
        }
    }
    
    protected abstract ByteArrayOutputStream convertFileListToBytes(final ArrayList<String> p0) throws IOException;
    
    private String removeSuspectedData(final DataFlavor dataFlavor, final Transferable transferable, final String s) throws IOException {
        if (null == System.getSecurityManager() || !dataFlavor.isMimeTypeEqual("text/uri-list")) {
            return s;
        }
        final ProtectionDomain userProtectionDomain = getUserProtectionDomain(transferable);
        String s2;
        try {
            s2 = AccessController.doPrivileged((PrivilegedExceptionAction<String>)new PrivilegedExceptionAction() {
                @Override
                public Object run() {
                    final StringBuffer sb = new StringBuffer(s.length());
                    for (final String s : s.split("(\\s)+")) {
                        final File file = new File(s);
                        if (file.exists() && !isFileInWebstartedCache(file) && !DataTransferer.this.isForbiddenToRead(file, userProtectionDomain)) {
                            if (0 != sb.length()) {
                                sb.append("\\r\\n");
                            }
                            sb.append(s);
                        }
                    }
                    return sb.toString();
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw new IOException(ex.getMessage(), ex);
        }
        return s2;
    }
    
    private static ProtectionDomain getUserProtectionDomain(final Transferable transferable) {
        return transferable.getClass().getProtectionDomain();
    }
    
    private boolean isForbiddenToRead(final File file, final ProtectionDomain protectionDomain) {
        if (null == protectionDomain) {
            return false;
        }
        try {
            if (protectionDomain.implies(new FilePermission(file.getCanonicalPath(), "read, delete"))) {
                return false;
            }
        }
        catch (final IOException ex) {}
        return true;
    }
    
    private ArrayList<String> castToFiles(final List list, final ProtectionDomain protectionDomain) throws IOException {
        final ArrayList list2 = new ArrayList();
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction() {
                @Override
                public Object run() throws IOException {
                    final Iterator iterator = list.iterator();
                    while (iterator.hasNext()) {
                        final File access$300 = DataTransferer.this.castToFile(iterator.next());
                        if (access$300 != null && (null == System.getSecurityManager() || (!isFileInWebstartedCache(access$300) && !DataTransferer.this.isForbiddenToRead(access$300, protectionDomain)))) {
                            list2.add(access$300.getCanonicalPath());
                        }
                    }
                    return null;
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw new IOException(ex.getMessage());
        }
        return list2;
    }
    
    private File castToFile(final Object o) throws IOException {
        String canonicalPath;
        if (o instanceof File) {
            canonicalPath = ((File)o).getCanonicalPath();
        }
        else {
            if (!(o instanceof String)) {
                return null;
            }
            canonicalPath = (String)o;
        }
        return new File(canonicalPath);
    }
    
    private static boolean isFileInWebstartedCache(final File file) {
        if (DataTransferer.deploymentCacheDirectoryList.isEmpty()) {
            final String[] deployment_CACHE_PROPERTIES = DataTransferer.DEPLOYMENT_CACHE_PROPERTIES;
            for (int length = deployment_CACHE_PROPERTIES.length, i = 0; i < length; ++i) {
                final String property = System.getProperty(deployment_CACHE_PROPERTIES[i]);
                if (property != null) {
                    try {
                        final File canonicalFile = new File(property).getCanonicalFile();
                        if (canonicalFile != null) {
                            DataTransferer.deploymentCacheDirectoryList.add(canonicalFile);
                        }
                    }
                    catch (final IOException ex) {}
                }
            }
        }
        for (final File file2 : DataTransferer.deploymentCacheDirectoryList) {
            for (File parentFile = file; parentFile != null; parentFile = parentFile.getParentFile()) {
                if (parentFile.equals(file2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Object translateBytes(byte[] bytes, final DataFlavor dataFlavor, final long n, final Transferable transferable) throws IOException {
        Object o = null;
        Label_1243: {
            if (this.isFileFormat(n)) {
                if (!DataFlavor.javaFileListFlavor.equals(dataFlavor)) {
                    throw new IOException("data translation failed");
                }
                final String[] dragQueryFile = this.dragQueryFile(bytes);
                if (dragQueryFile == null) {
                    return null;
                }
                final File[] array = new File[dragQueryFile.length];
                for (int i = 0; i < dragQueryFile.length; ++i) {
                    array[i] = new File(dragQueryFile[i]);
                }
                o = Arrays.asList(array);
            }
            else if (this.isURIListFormat(n) && DataFlavor.javaFileListFlavor.equals(dataFlavor)) {
                try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
                    final URI[] dragQueryURIs = this.dragQueryURIs(byteArrayInputStream, n, transferable);
                    if (dragQueryURIs == null) {
                        return null;
                    }
                    final ArrayList list = new ArrayList();
                    for (final URI uri : dragQueryURIs) {
                        try {
                            list.add(new File(uri));
                        }
                        catch (final IllegalArgumentException ex) {}
                    }
                    o = list;
                }
            }
            else if (String.class.equals(dataFlavor.getRepresentationClass()) && isFlavorCharsetTextType(dataFlavor) && this.isTextFormat(n)) {
                o = this.translateBytesToString(bytes, n, transferable);
            }
            else if (dataFlavor.isRepresentationClassReader()) {
                try (final ByteArrayInputStream byteArrayInputStream2 = new ByteArrayInputStream(bytes)) {
                    o = this.translateStream(byteArrayInputStream2, dataFlavor, n, transferable);
                }
            }
            else if (dataFlavor.isRepresentationClassCharBuffer()) {
                if (!isFlavorCharsetTextType(dataFlavor) || !this.isTextFormat(n)) {
                    throw new IOException("cannot transfer non-text data as CharBuffer");
                }
                o = this.constructFlavoredObject(CharBuffer.wrap(this.translateBytesToString(bytes, n, transferable)), dataFlavor, CharBuffer.class);
            }
            else if (char[].class.equals(dataFlavor.getRepresentationClass())) {
                if (!isFlavorCharsetTextType(dataFlavor) || !this.isTextFormat(n)) {
                    throw new IOException("cannot transfer non-text data as char array");
                }
                o = this.translateBytesToString(bytes, n, transferable).toCharArray();
            }
            else if (dataFlavor.isRepresentationClassByteBuffer()) {
                if (isFlavorCharsetTextType(dataFlavor) && this.isTextFormat(n)) {
                    bytes = this.translateBytesToString(bytes, n, transferable).getBytes(getTextCharset(dataFlavor));
                }
                o = this.constructFlavoredObject(ByteBuffer.wrap(bytes), dataFlavor, ByteBuffer.class);
            }
            else if (byte[].class.equals(dataFlavor.getRepresentationClass())) {
                if (isFlavorCharsetTextType(dataFlavor) && this.isTextFormat(n)) {
                    o = this.translateBytesToString(bytes, n, transferable).getBytes(getTextCharset(dataFlavor));
                }
                else {
                    o = bytes;
                }
            }
            else if (dataFlavor.isRepresentationClassInputStream()) {
                try (final ByteArrayInputStream byteArrayInputStream3 = new ByteArrayInputStream(bytes)) {
                    o = this.translateStream(byteArrayInputStream3, dataFlavor, n, transferable);
                }
            }
            else {
                if (dataFlavor.isRepresentationClassRemote()) {
                    try {
                        try (final ByteArrayInputStream byteArrayInputStream4 = new ByteArrayInputStream(bytes);
                             final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream4)) {
                            o = RMI.getMarshalledObject(objectInputStream.readObject());
                        }
                        break Label_1243;
                    }
                    catch (final Exception ex2) {
                        throw new IOException(ex2.getMessage());
                    }
                }
                if (dataFlavor.isRepresentationClassSerializable()) {
                    try (final ByteArrayInputStream byteArrayInputStream5 = new ByteArrayInputStream(bytes)) {
                        o = this.translateStream(byteArrayInputStream5, dataFlavor, n, transferable);
                    }
                }
                else if (DataFlavor.imageFlavor.equals(dataFlavor)) {
                    if (!this.isImageFormat(n)) {
                        throw new IOException("data translation failed");
                    }
                    o = this.platformImageBytesToImage(bytes, n);
                }
            }
        }
        if (o == null) {
            throw new IOException("data translation failed");
        }
        return o;
    }
    
    public Object translateStream(final InputStream inputStream, final DataFlavor dataFlavor, final long n, final Transferable transferable) throws IOException {
        Object o = null;
        Label_0647: {
            if (this.isURIListFormat(n) && DataFlavor.javaFileListFlavor.equals(dataFlavor)) {
                final URI[] dragQueryURIs = this.dragQueryURIs(inputStream, n, transferable);
                if (dragQueryURIs == null) {
                    return null;
                }
                final ArrayList list = new ArrayList();
                for (final URI uri : dragQueryURIs) {
                    try {
                        list.add(new File(uri));
                    }
                    catch (final IllegalArgumentException ex) {}
                }
                o = list;
            }
            else {
                if (String.class.equals(dataFlavor.getRepresentationClass()) && isFlavorCharsetTextType(dataFlavor) && this.isTextFormat(n)) {
                    return this.translateBytesToString(inputStreamToByteArray(inputStream), n, transferable);
                }
                if (DataFlavor.plainTextFlavor.equals(dataFlavor)) {
                    o = new StringReader(this.translateBytesToString(inputStreamToByteArray(inputStream), n, transferable));
                }
                else if (dataFlavor.isRepresentationClassInputStream()) {
                    o = this.translateStreamToInputStream(inputStream, dataFlavor, n, transferable);
                }
                else if (dataFlavor.isRepresentationClassReader()) {
                    if (!isFlavorCharsetTextType(dataFlavor) || !this.isTextFormat(n)) {
                        throw new IOException("cannot transfer non-text data as Reader");
                    }
                    o = this.constructFlavoredObject(new InputStreamReader((InputStream)this.translateStreamToInputStream(inputStream, DataFlavor.plainTextFlavor, n, transferable), getTextCharset(DataFlavor.plainTextFlavor)), dataFlavor, Reader.class);
                }
                else if (byte[].class.equals(dataFlavor.getRepresentationClass())) {
                    if (isFlavorCharsetTextType(dataFlavor) && this.isTextFormat(n)) {
                        o = this.translateBytesToString(inputStreamToByteArray(inputStream), n, transferable).getBytes(getTextCharset(dataFlavor));
                    }
                    else {
                        o = inputStreamToByteArray(inputStream);
                    }
                }
                else {
                    if (dataFlavor.isRepresentationClassRemote()) {
                        try {
                            try (final ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                                o = RMI.getMarshalledObject(objectInputStream.readObject());
                            }
                            break Label_0647;
                        }
                        catch (final Exception ex2) {
                            throw new IOException(ex2.getMessage());
                        }
                    }
                    if (dataFlavor.isRepresentationClassSerializable()) {
                        try {
                            try (final ObjectInputStream objectInputStream2 = new ObjectInputStream(inputStream)) {
                                o = objectInputStream2.readObject();
                            }
                            break Label_0647;
                        }
                        catch (final Exception ex3) {
                            throw new IOException(ex3.getMessage());
                        }
                    }
                    if (DataFlavor.imageFlavor.equals(dataFlavor)) {
                        if (!this.isImageFormat(n)) {
                            throw new IOException("data translation failed");
                        }
                        o = this.platformImageBytesToImage(inputStreamToByteArray(inputStream), n);
                    }
                }
            }
        }
        if (o == null) {
            throw new IOException("data translation failed");
        }
        return o;
    }
    
    private Object translateStreamToInputStream(InputStream inputStream, final DataFlavor dataFlavor, final long n, final Transferable transferable) throws IOException {
        if (isFlavorCharsetTextType(dataFlavor) && this.isTextFormat(n)) {
            inputStream = new ReencodingInputStream(inputStream, n, getTextCharset(dataFlavor), transferable);
        }
        return this.constructFlavoredObject(inputStream, dataFlavor, InputStream.class);
    }
    
    private Object constructFlavoredObject(final Object o, final DataFlavor dataFlavor, final Class clazz) throws IOException {
        final Class<?> representationClass = dataFlavor.getRepresentationClass();
        if (clazz.equals(representationClass)) {
            return o;
        }
        Constructor[] array;
        try {
            array = AccessController.doPrivileged((PrivilegedAction<Constructor[]>)new PrivilegedAction() {
                @Override
                public Object run() {
                    return representationClass.getConstructors();
                }
            });
        }
        catch (final SecurityException ex) {
            throw new IOException(ex.getMessage());
        }
        Constructor constructor = null;
        for (int i = 0; i < array.length; ++i) {
            if (Modifier.isPublic(array[i].getModifiers())) {
                final Class[] parameterTypes = array[i].getParameterTypes();
                if (parameterTypes != null && parameterTypes.length == 1 && clazz.equals(parameterTypes[0])) {
                    constructor = array[i];
                    break;
                }
            }
        }
        if (constructor == null) {
            throw new IOException("can't find <init>(L" + clazz + ";)V for class: " + representationClass.getName());
        }
        try {
            return constructor.newInstance(o);
        }
        catch (final Exception ex2) {
            throw new IOException(ex2.getMessage());
        }
    }
    
    protected abstract String[] dragQueryFile(final byte[] p0);
    
    protected URI[] dragQueryURIs(final InputStream inputStream, final long n, final Transferable transferable) throws IOException {
        throw new IOException(new UnsupportedOperationException("not implemented on this platform"));
    }
    
    protected abstract Image platformImageBytesToImage(final byte[] p0, final long p1) throws IOException;
    
    protected Image standardImageBytesToImage(final byte[] array, final String s) throws IOException {
        final Iterator<ImageReader> imageReadersByMIMEType = ImageIO.getImageReadersByMIMEType(s);
        if (!imageReadersByMIMEType.hasNext()) {
            throw new IOException("No registered service provider can decode  an image from " + s);
        }
        Object o = null;
        while (imageReadersByMIMEType.hasNext()) {
            final ImageReader imageReader = imageReadersByMIMEType.next();
            try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array)) {
                final ImageInputStream imageInputStream = ImageIO.createImageInputStream(byteArrayInputStream);
                try {
                    final ImageReadParam defaultReadParam = imageReader.getDefaultReadParam();
                    imageReader.setInput(imageInputStream, true, true);
                    final BufferedImage read = imageReader.read(imageReader.getMinIndex(), defaultReadParam);
                    if (read != null) {
                        return read;
                    }
                }
                finally {
                    imageInputStream.close();
                    imageReader.dispose();
                }
            }
            catch (final IOException ex) {
                o = ex;
            }
        }
        if (o == null) {
            o = new IOException("Registered service providers failed to decode an image from " + s);
        }
        throw o;
    }
    
    protected abstract byte[] imageToPlatformBytes(final Image p0, final long p1) throws IOException;
    
    protected byte[] imageToStandardBytes(final Image image, final String s) throws IOException {
        IOException ex = null;
        if (!ImageIO.getImageWritersByMIMEType(s).hasNext()) {
            throw new IOException("No registered service provider can encode  an image to " + s);
        }
        if (image instanceof RenderedImage) {
            try {
                return this.imageToStandardBytesImpl((RenderedImage)image, s);
            }
            catch (final IOException ex2) {
                ex = ex2;
            }
        }
        int n;
        int n2;
        if (image instanceof ToolkitImage) {
            final ImageRepresentation imageRep = ((ToolkitImage)image).getImageRep();
            imageRep.reconstruct(32);
            n = imageRep.getWidth();
            n2 = imageRep.getHeight();
        }
        else {
            n = image.getWidth(null);
            n2 = image.getHeight(null);
        }
        final ColorModel rgBdefault = ColorModel.getRGBdefault();
        final BufferedImage bufferedImage = new BufferedImage(rgBdefault, rgBdefault.createCompatibleWritableRaster(n, n2), rgBdefault.isAlphaPremultiplied(), null);
        final Graphics graphics = bufferedImage.getGraphics();
        try {
            graphics.drawImage(image, 0, 0, n, n2, null);
            graphics.dispose();
        }
        finally {
            graphics.dispose();
        }
        try {
            return this.imageToStandardBytesImpl(bufferedImage, s);
        }
        catch (final IOException ex3) {
            if (ex != null) {
                throw ex;
            }
        }
    }
    
    protected byte[] imageToStandardBytesImpl(final RenderedImage renderedImage, final String s) throws IOException {
        final Iterator<ImageWriter> imageWritersByMIMEType = ImageIO.getImageWritersByMIMEType(s);
        final ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(renderedImage);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Object o = null;
        while (imageWritersByMIMEType.hasNext()) {
            final ImageWriter imageWriter = imageWritersByMIMEType.next();
            if (!imageWriter.getOriginatingProvider().canEncodeImage(imageTypeSpecifier)) {
                continue;
            }
            try {
                final ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);
                try {
                    imageWriter.setOutput(imageOutputStream);
                    imageWriter.write(renderedImage);
                    imageOutputStream.flush();
                }
                finally {
                    imageOutputStream.close();
                }
            }
            catch (final IOException ex) {
                imageWriter.dispose();
                byteArrayOutputStream.reset();
                o = ex;
                continue;
            }
            imageWriter.dispose();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        }
        byteArrayOutputStream.close();
        if (o == null) {
            o = new IOException("Registered service providers failed to encode " + renderedImage + " to " + s);
        }
        throw o;
    }
    
    private Object concatData(final Object o, final Object o2) {
        InputStream inputStream;
        InputStream inputStream2;
        if (o instanceof byte[]) {
            final byte[] array = (byte[])o;
            if (o2 instanceof byte[]) {
                final byte[] array2 = (byte[])o2;
                final byte[] array3 = new byte[array.length + array2.length];
                System.arraycopy(array, 0, array3, 0, array.length);
                System.arraycopy(array2, 0, array3, array.length, array2.length);
                return array3;
            }
            inputStream = new ByteArrayInputStream(array);
            inputStream2 = (InputStream)o2;
        }
        else {
            inputStream = (InputStream)o;
            if (o2 instanceof byte[]) {
                inputStream2 = new ByteArrayInputStream((byte[])o2);
            }
            else {
                inputStream2 = (InputStream)o2;
            }
        }
        return new SequenceInputStream(inputStream, inputStream2);
    }
    
    public byte[] convertData(final Object o, final Transferable transferable, final long n, final Map map, final boolean b) throws IOException {
        byte[] translateTransferable = null;
        if (b) {
            try {
                final Stack stack = new Stack();
                final Runnable runnable = new Runnable() {
                    private boolean done = false;
                    
                    @Override
                    public void run() {
                        if (this.done) {
                            return;
                        }
                        byte[] translateTransferable = null;
                        try {
                            final DataFlavor dataFlavor = map.get(n);
                            if (dataFlavor != null) {
                                translateTransferable = DataTransferer.this.translateTransferable(transferable, dataFlavor, n);
                            }
                        }
                        catch (final Exception ex) {
                            ex.printStackTrace();
                            translateTransferable = null;
                        }
                        try {
                            DataTransferer.this.getToolkitThreadBlockedHandler().lock();
                            stack.push(translateTransferable);
                            DataTransferer.this.getToolkitThreadBlockedHandler().exit();
                        }
                        finally {
                            DataTransferer.this.getToolkitThreadBlockedHandler().unlock();
                            this.done = true;
                        }
                    }
                };
                final AppContext targetToAppContext = SunToolkit.targetToAppContext(o);
                this.getToolkitThreadBlockedHandler().lock();
                if (targetToAppContext != null) {
                    targetToAppContext.put("DATA_CONVERTER_KEY", runnable);
                }
                SunToolkit.executeOnEventHandlerThread(o, runnable);
                while (stack.empty()) {
                    this.getToolkitThreadBlockedHandler().enter();
                }
                if (targetToAppContext != null) {
                    targetToAppContext.remove("DATA_CONVERTER_KEY");
                }
                translateTransferable = (byte[])stack.pop();
            }
            finally {
                this.getToolkitThreadBlockedHandler().unlock();
            }
        }
        else {
            final DataFlavor dataFlavor = map.get(n);
            if (dataFlavor != null) {
                translateTransferable = this.translateTransferable(transferable, dataFlavor, n);
            }
        }
        return translateTransferable;
    }
    
    public void processDataConversionRequests() {
        if (EventQueue.isDispatchThread()) {
            final AppContext appContext = AppContext.getAppContext();
            this.getToolkitThreadBlockedHandler().lock();
            try {
                final Runnable runnable = (Runnable)appContext.get("DATA_CONVERTER_KEY");
                if (runnable != null) {
                    runnable.run();
                    appContext.remove("DATA_CONVERTER_KEY");
                }
            }
            finally {
                this.getToolkitThreadBlockedHandler().unlock();
            }
        }
    }
    
    public abstract ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler();
    
    public static long[] keysToLongArray(final SortedMap sortedMap) {
        final Set keySet = sortedMap.keySet();
        final long[] array = new long[keySet.size()];
        int n = 0;
        final Iterator iterator = keySet.iterator();
        while (iterator.hasNext()) {
            array[n] = (long)iterator.next();
            ++n;
        }
        return array;
    }
    
    public static DataFlavor[] setToSortedDataFlavorArray(final Set set) {
        final DataFlavor[] array = new DataFlavor[set.size()];
        set.toArray(array);
        Arrays.sort(array, new DataFlavorComparator(false));
        return array;
    }
    
    protected static byte[] inputStreamToByteArray(final InputStream inputStream) throws IOException {
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            final byte[] array = new byte[8192];
            int read;
            while ((read = inputStream.read(array)) != -1) {
                byteArrayOutputStream.write(array, 0, read);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
    
    public LinkedHashSet<DataFlavor> getPlatformMappingsForNative(final String s) {
        return new LinkedHashSet<DataFlavor>();
    }
    
    public LinkedHashSet<String> getPlatformMappingsForFlavor(final DataFlavor dataFlavor) {
        return new LinkedHashSet<String>();
    }
    
    static {
        textNatives = Collections.synchronizedSet(new HashSet<Object>());
        nativeCharsets = Collections.synchronizedMap(new HashMap<Object, Object>());
        nativeEOLNs = Collections.synchronizedMap(new HashMap<Object, Object>());
        nativeTerminators = Collections.synchronizedMap(new HashMap<Object, Object>());
        dtLog = PlatformLogger.getLogger("sun.awt.datatransfer.DataTransfer");
        DataFlavor plainTextStringFlavor2 = null;
        try {
            plainTextStringFlavor2 = new DataFlavor("text/plain;charset=Unicode;class=java.lang.String");
        }
        catch (final ClassNotFoundException ex) {}
        plainTextStringFlavor = plainTextStringFlavor2;
        DataFlavor javaTextEncodingFlavor2 = null;
        try {
            javaTextEncodingFlavor2 = new DataFlavor("application/x-java-text-encoding;class=\"[B\"");
        }
        catch (final ClassNotFoundException ex2) {}
        javaTextEncodingFlavor = javaTextEncodingFlavor2;
        final HashMap hashMap = new HashMap(17);
        hashMap.put("sgml", Boolean.TRUE);
        hashMap.put("xml", Boolean.TRUE);
        hashMap.put("html", Boolean.TRUE);
        hashMap.put("enriched", Boolean.TRUE);
        hashMap.put("richtext", Boolean.TRUE);
        hashMap.put("uri-list", Boolean.TRUE);
        hashMap.put("directory", Boolean.TRUE);
        hashMap.put("css", Boolean.TRUE);
        hashMap.put("calendar", Boolean.TRUE);
        hashMap.put("plain", Boolean.TRUE);
        hashMap.put("rtf", Boolean.FALSE);
        hashMap.put("tab-separated-values", Boolean.FALSE);
        hashMap.put("t140", Boolean.FALSE);
        hashMap.put("rfc822-headers", Boolean.FALSE);
        hashMap.put("parityfec", Boolean.FALSE);
        textMIMESubtypeCharsetSupport = Collections.synchronizedMap((Map<Object, Object>)hashMap);
        DEPLOYMENT_CACHE_PROPERTIES = new String[] { "deployment.system.cachedir", "deployment.user.cachedir", "deployment.javaws.cachedir", "deployment.javapi.cachedir" };
        deploymentCacheDirectoryList = new ArrayList<File>();
    }
    
    private static class StandardEncodingsHolder
    {
        private static final SortedSet<String> standardEncodings;
        
        private static SortedSet<String> load() {
            final TreeSet set = new TreeSet(new CharsetComparator(false));
            set.add("US-ASCII");
            set.add("ISO-8859-1");
            set.add("UTF-8");
            set.add("UTF-16BE");
            set.add("UTF-16LE");
            set.add("UTF-16");
            set.add(DataTransferer.getDefaultTextCharset());
            return Collections.unmodifiableSortedSet((SortedSet<String>)set);
        }
        
        static {
            standardEncodings = load();
        }
    }
    
    public class ReencodingInputStream extends InputStream
    {
        protected BufferedReader wrapped;
        protected final char[] in;
        protected byte[] out;
        protected CharsetEncoder encoder;
        protected CharBuffer inBuf;
        protected ByteBuffer outBuf;
        protected char[] eoln;
        protected int numTerminators;
        protected boolean eos;
        protected int index;
        protected int limit;
        
        public ReencodingInputStream(final InputStream inputStream, final long n, final String s, final Transferable transferable) throws IOException {
            this.in = new char[2];
            final Long value = n;
            String s2 = null;
            if (DataTransferer.this.isLocaleDependentTextFormat(n) && transferable != null && transferable.isDataFlavorSupported(DataTransferer.javaTextEncodingFlavor)) {
                try {
                    s2 = new String((byte[])transferable.getTransferData(DataTransferer.javaTextEncodingFlavor), "UTF-8");
                }
                catch (final UnsupportedFlavorException ex) {}
            }
            else {
                s2 = DataTransferer.this.getCharsetForTextFormat(value);
            }
            if (s2 == null) {
                s2 = DataTransferer.getDefaultTextCharset();
            }
            this.wrapped = new BufferedReader(new InputStreamReader(inputStream, s2));
            if (s == null) {
                throw new NullPointerException("null target encoding");
            }
            try {
                this.encoder = Charset.forName(s).newEncoder();
                this.out = new byte[(int)(this.encoder.maxBytesPerChar() * 2.0f + 0.5)];
                this.inBuf = CharBuffer.wrap(this.in);
                this.outBuf = ByteBuffer.wrap(this.out);
            }
            catch (final IllegalCharsetNameException ex2) {
                throw new IOException(ex2.toString());
            }
            catch (final UnsupportedCharsetException ex3) {
                throw new IOException(ex3.toString());
            }
            catch (final UnsupportedOperationException ex4) {
                throw new IOException(ex4.toString());
            }
            final String s3 = DataTransferer.nativeEOLNs.get(value);
            if (s3 != null) {
                this.eoln = s3.toCharArray();
            }
            final Integer n2 = DataTransferer.nativeTerminators.get(value);
            if (n2 != null) {
                this.numTerminators = n2;
            }
        }
        
        private int readChar() throws IOException {
            int read = this.wrapped.read();
            if (read == -1) {
                this.eos = true;
                return -1;
            }
            if (this.numTerminators > 0 && read == 0) {
                this.eos = true;
                return -1;
            }
            if (this.eoln != null && this.matchCharArray(this.eoln, read)) {
                read = 10;
            }
            return read;
        }
        
        @Override
        public int read() throws IOException {
            if (this.eos) {
                return -1;
            }
            if (this.index < this.limit) {
                return this.out[this.index++] & 0xFF;
            }
            final int char1 = this.readChar();
            if (char1 == -1) {
                return -1;
            }
            this.in[0] = (char)char1;
            this.in[1] = '\0';
            this.inBuf.limit(1);
            if (Character.isHighSurrogate((char)char1)) {
                final int char2 = this.readChar();
                if (char2 != -1) {
                    this.in[1] = (char)char2;
                    this.inBuf.limit(2);
                }
            }
            this.inBuf.rewind();
            this.outBuf.limit(this.out.length).rewind();
            this.encoder.encode(this.inBuf, this.outBuf, false);
            this.outBuf.flip();
            this.limit = this.outBuf.limit();
            this.index = 0;
            return this.read();
        }
        
        @Override
        public int available() throws IOException {
            return this.eos ? 0 : (this.limit - this.index);
        }
        
        @Override
        public void close() throws IOException {
            this.wrapped.close();
        }
        
        private boolean matchCharArray(final char[] array, int read) throws IOException {
            this.wrapped.mark(array.length);
            int i = 0;
            if ((char)read == array[0]) {
                for (i = 1; i < array.length; ++i) {
                    read = this.wrapped.read();
                    if (read == -1) {
                        break;
                    }
                    if ((char)read != array[i]) {
                        break;
                    }
                }
            }
            if (i == array.length) {
                return true;
            }
            this.wrapped.reset();
            return false;
        }
    }
    
    public abstract static class IndexedComparator implements Comparator
    {
        public static final boolean SELECT_BEST = true;
        public static final boolean SELECT_WORST = false;
        protected final boolean order;
        
        public IndexedComparator() {
            this(true);
        }
        
        public IndexedComparator(final boolean order) {
            this.order = order;
        }
        
        protected static int compareIndices(final Map map, final Object o, final Object o2, final Integer n) {
            Integer n2 = map.get(o);
            Integer n3 = map.get(o2);
            if (n2 == null) {
                n2 = n;
            }
            if (n3 == null) {
                n3 = n;
            }
            return n2.compareTo(n3);
        }
        
        protected static int compareLongs(final Map map, final Object o, final Object o2, final Long n) {
            Long n2 = map.get(o);
            Long n3 = map.get(o2);
            if (n2 == null) {
                n2 = n;
            }
            if (n3 == null) {
                n3 = n;
            }
            return n2.compareTo(n3);
        }
    }
    
    public static class CharsetComparator extends IndexedComparator
    {
        private static final Map charsets;
        private static String defaultEncoding;
        private static final Integer DEFAULT_CHARSET_INDEX;
        private static final Integer OTHER_CHARSET_INDEX;
        private static final Integer WORST_CHARSET_INDEX;
        private static final Integer UNSUPPORTED_CHARSET_INDEX;
        private static final String UNSUPPORTED_CHARSET = "UNSUPPORTED";
        
        public CharsetComparator() {
            this(true);
        }
        
        public CharsetComparator(final boolean b) {
            super(b);
        }
        
        @Override
        public int compare(final Object o, final Object o2) {
            String s;
            String s2;
            if (this.order) {
                s = (String)o;
                s2 = (String)o2;
            }
            else {
                s = (String)o2;
                s2 = (String)o;
            }
            return this.compareCharsets(s, s2);
        }
        
        protected int compareCharsets(String encoding, String encoding2) {
            encoding = getEncoding(encoding);
            encoding2 = getEncoding(encoding2);
            final int compareIndices = IndexedComparator.compareIndices(CharsetComparator.charsets, encoding, encoding2, CharsetComparator.OTHER_CHARSET_INDEX);
            if (compareIndices == 0) {
                return encoding2.compareTo(encoding);
            }
            return compareIndices;
        }
        
        protected static String getEncoding(final String s) {
            if (s == null) {
                return null;
            }
            if (!DataTransferer.isEncodingSupported(s)) {
                return "UNSUPPORTED";
            }
            final String canonicalName = DataTransferer.canonicalName(s);
            return CharsetComparator.charsets.containsKey(canonicalName) ? canonicalName : s;
        }
        
        static {
            DEFAULT_CHARSET_INDEX = 2;
            OTHER_CHARSET_INDEX = 1;
            WORST_CHARSET_INDEX = 0;
            UNSUPPORTED_CHARSET_INDEX = Integer.MIN_VALUE;
            final HashMap hashMap = new HashMap(8, 1.0f);
            hashMap.put(DataTransferer.canonicalName("UTF-16LE"), 4);
            hashMap.put(DataTransferer.canonicalName("UTF-16BE"), 5);
            hashMap.put(DataTransferer.canonicalName("UTF-8"), 6);
            hashMap.put(DataTransferer.canonicalName("UTF-16"), 7);
            hashMap.put(DataTransferer.canonicalName("US-ASCII"), CharsetComparator.WORST_CHARSET_INDEX);
            DataTransferer.canonicalName(DataTransferer.getDefaultTextCharset());
            if (hashMap.get(CharsetComparator.defaultEncoding) == null) {
                hashMap.put(CharsetComparator.defaultEncoding, CharsetComparator.DEFAULT_CHARSET_INDEX);
            }
            hashMap.put("UNSUPPORTED", CharsetComparator.UNSUPPORTED_CHARSET_INDEX);
            charsets = Collections.unmodifiableMap((Map<?, ?>)hashMap);
        }
    }
    
    public static class DataFlavorComparator extends IndexedComparator
    {
        private final CharsetComparator charsetComparator;
        private static final Map exactTypes;
        private static final Map primaryTypes;
        private static final Map nonTextRepresentations;
        private static final Map textTypes;
        private static final Map decodedTextRepresentations;
        private static final Map encodedTextRepresentations;
        private static final Integer UNKNOWN_OBJECT_LOSES;
        private static final Integer UNKNOWN_OBJECT_WINS;
        private static final Long UNKNOWN_OBJECT_LOSES_L;
        private static final Long UNKNOWN_OBJECT_WINS_L;
        
        public DataFlavorComparator() {
            this(true);
        }
        
        public DataFlavorComparator(final boolean b) {
            super(b);
            this.charsetComparator = new CharsetComparator(b);
        }
        
        @Override
        public int compare(final Object o, final Object o2) {
            DataFlavor dataFlavor;
            DataFlavor dataFlavor2;
            if (this.order) {
                dataFlavor = (DataFlavor)o;
                dataFlavor2 = (DataFlavor)o2;
            }
            else {
                dataFlavor = (DataFlavor)o2;
                dataFlavor2 = (DataFlavor)o;
            }
            if (dataFlavor.equals(dataFlavor2)) {
                return 0;
            }
            final String primaryType = dataFlavor.getPrimaryType();
            final String string = primaryType + "/" + dataFlavor.getSubType();
            final Class<?> representationClass = dataFlavor.getRepresentationClass();
            final String primaryType2 = dataFlavor2.getPrimaryType();
            final String string2 = primaryType2 + "/" + dataFlavor2.getSubType();
            final Class<?> representationClass2 = dataFlavor2.getRepresentationClass();
            if (dataFlavor.isFlavorTextType() && dataFlavor2.isFlavorTextType()) {
                final int compareIndices = IndexedComparator.compareIndices(DataFlavorComparator.textTypes, string, string2, DataFlavorComparator.UNKNOWN_OBJECT_LOSES);
                if (compareIndices != 0) {
                    return compareIndices;
                }
                if (DataTransferer.doesSubtypeSupportCharset(dataFlavor)) {
                    final int compareIndices2 = IndexedComparator.compareIndices(DataFlavorComparator.decodedTextRepresentations, representationClass, representationClass2, DataFlavorComparator.UNKNOWN_OBJECT_LOSES);
                    if (compareIndices2 != 0) {
                        return compareIndices2;
                    }
                    final int compareCharsets = this.charsetComparator.compareCharsets(DataTransferer.getTextCharset(dataFlavor), DataTransferer.getTextCharset(dataFlavor2));
                    if (compareCharsets != 0) {
                        return compareCharsets;
                    }
                }
                final int compareIndices3 = IndexedComparator.compareIndices(DataFlavorComparator.encodedTextRepresentations, representationClass, representationClass2, DataFlavorComparator.UNKNOWN_OBJECT_LOSES);
                if (compareIndices3 != 0) {
                    return compareIndices3;
                }
            }
            else {
                if (dataFlavor.isFlavorTextType()) {
                    return 1;
                }
                if (dataFlavor2.isFlavorTextType()) {
                    return -1;
                }
                final int compareIndices4 = IndexedComparator.compareIndices(DataFlavorComparator.primaryTypes, primaryType, primaryType2, DataFlavorComparator.UNKNOWN_OBJECT_LOSES);
                if (compareIndices4 != 0) {
                    return compareIndices4;
                }
                final int compareIndices5 = IndexedComparator.compareIndices(DataFlavorComparator.exactTypes, string, string2, DataFlavorComparator.UNKNOWN_OBJECT_WINS);
                if (compareIndices5 != 0) {
                    return compareIndices5;
                }
                final int compareIndices6 = IndexedComparator.compareIndices(DataFlavorComparator.nonTextRepresentations, representationClass, representationClass2, DataFlavorComparator.UNKNOWN_OBJECT_LOSES);
                if (compareIndices6 != 0) {
                    return compareIndices6;
                }
            }
            return dataFlavor.getMimeType().compareTo(dataFlavor2.getMimeType());
        }
        
        static {
            UNKNOWN_OBJECT_LOSES = Integer.MIN_VALUE;
            UNKNOWN_OBJECT_WINS = Integer.MAX_VALUE;
            UNKNOWN_OBJECT_LOSES_L = Long.MIN_VALUE;
            UNKNOWN_OBJECT_WINS_L = Long.MAX_VALUE;
            final HashMap hashMap = new HashMap(4, 1.0f);
            hashMap.put("application/x-java-file-list", 0);
            hashMap.put("application/x-java-serialized-object", 1);
            hashMap.put("application/x-java-jvm-local-objectref", 2);
            hashMap.put("application/x-java-remote-object", 3);
            exactTypes = Collections.unmodifiableMap((Map<?, ?>)hashMap);
            final HashMap hashMap2 = new HashMap(1, 1.0f);
            hashMap2.put("application", 0);
            primaryTypes = Collections.unmodifiableMap((Map<?, ?>)hashMap2);
            final HashMap hashMap3 = new HashMap(3, 1.0f);
            hashMap3.put(InputStream.class, 0);
            hashMap3.put(Serializable.class, 1);
            final Class<?> remoteClass = RMI.remoteClass();
            if (remoteClass != null) {
                hashMap3.put(remoteClass, 2);
            }
            nonTextRepresentations = Collections.unmodifiableMap((Map<?, ?>)hashMap3);
            final HashMap hashMap4 = new HashMap(16, 1.0f);
            hashMap4.put("text/plain", 0);
            hashMap4.put("application/x-java-serialized-object", 1);
            hashMap4.put("text/calendar", 2);
            hashMap4.put("text/css", 3);
            hashMap4.put("text/directory", 4);
            hashMap4.put("text/parityfec", 5);
            hashMap4.put("text/rfc822-headers", 6);
            hashMap4.put("text/t140", 7);
            hashMap4.put("text/tab-separated-values", 8);
            hashMap4.put("text/uri-list", 9);
            hashMap4.put("text/richtext", 10);
            hashMap4.put("text/enriched", 11);
            hashMap4.put("text/rtf", 12);
            hashMap4.put("text/html", 13);
            hashMap4.put("text/xml", 14);
            hashMap4.put("text/sgml", 15);
            textTypes = Collections.unmodifiableMap((Map<?, ?>)hashMap4);
            final HashMap hashMap5 = new HashMap(4, 1.0f);
            hashMap5.put(char[].class, 0);
            hashMap5.put(CharBuffer.class, 1);
            hashMap5.put(String.class, 2);
            hashMap5.put(Reader.class, 3);
            decodedTextRepresentations = Collections.unmodifiableMap((Map<?, ?>)hashMap5);
            final HashMap hashMap6 = new HashMap(3, 1.0f);
            hashMap6.put(byte[].class, 0);
            hashMap6.put(ByteBuffer.class, 1);
            hashMap6.put(InputStream.class, 2);
            encodedTextRepresentations = Collections.unmodifiableMap((Map<?, ?>)hashMap6);
        }
    }
    
    public static class IndexOrderComparator extends IndexedComparator
    {
        private final Map indexMap;
        private static final Integer FALLBACK_INDEX;
        
        public IndexOrderComparator(final Map indexMap) {
            super(true);
            this.indexMap = indexMap;
        }
        
        public IndexOrderComparator(final Map indexMap, final boolean b) {
            super(b);
            this.indexMap = indexMap;
        }
        
        @Override
        public int compare(final Object o, final Object o2) {
            if (!this.order) {
                return -IndexedComparator.compareIndices(this.indexMap, o, o2, IndexOrderComparator.FALLBACK_INDEX);
            }
            return IndexedComparator.compareIndices(this.indexMap, o, o2, IndexOrderComparator.FALLBACK_INDEX);
        }
        
        static {
            FALLBACK_INDEX = Integer.MIN_VALUE;
        }
    }
    
    private static class RMI
    {
        private static final Class<?> remoteClass;
        private static final Class<?> marshallObjectClass;
        private static final Constructor<?> marshallCtor;
        private static final Method marshallGet;
        
        private static Class<?> getClass(final String s) {
            try {
                return Class.forName(s, true, null);
            }
            catch (final ClassNotFoundException ex) {
                return null;
            }
        }
        
        private static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... array) {
            try {
                return (clazz == null) ? null : clazz.getDeclaredConstructor(array);
            }
            catch (final NoSuchMethodException ex) {
                throw new AssertionError((Object)ex);
            }
        }
        
        private static Method getMethod(final Class<?> clazz, final String s, final Class<?>... array) {
            try {
                return (clazz == null) ? null : clazz.getMethod(s, array);
            }
            catch (final NoSuchMethodException ex) {
                throw new AssertionError((Object)ex);
            }
        }
        
        static boolean isRemote(final Class<?> clazz) {
            return (RMI.remoteClass == null) ? null : Boolean.valueOf(RMI.remoteClass.isAssignableFrom(clazz));
        }
        
        static Class<?> remoteClass() {
            return RMI.remoteClass;
        }
        
        static Object newMarshalledObject(final Object o) throws IOException {
            try {
                return RMI.marshallCtor.newInstance(o);
            }
            catch (final InstantiationException ex) {
                throw new AssertionError((Object)ex);
            }
            catch (final IllegalAccessException ex2) {
                throw new AssertionError((Object)ex2);
            }
            catch (final InvocationTargetException ex3) {
                final Throwable cause = ex3.getCause();
                if (cause instanceof IOException) {
                    throw (IOException)cause;
                }
                throw new AssertionError((Object)ex3);
            }
        }
        
        static Object getMarshalledObject(final Object o) throws IOException, ClassNotFoundException {
            try {
                return RMI.marshallGet.invoke(o, new Object[0]);
            }
            catch (final IllegalAccessException ex) {
                throw new AssertionError((Object)ex);
            }
            catch (final InvocationTargetException ex2) {
                final Throwable cause = ex2.getCause();
                if (cause instanceof IOException) {
                    throw (IOException)cause;
                }
                if (cause instanceof ClassNotFoundException) {
                    throw (ClassNotFoundException)cause;
                }
                throw new AssertionError((Object)ex2);
            }
        }
        
        static {
            remoteClass = getClass("java.rmi.Remote");
            marshallObjectClass = getClass("java.rmi.MarshalledObject");
            marshallCtor = getConstructor(RMI.marshallObjectClass, Object.class);
            marshallGet = getMethod(RMI.marshallObjectClass, "get", (Class<?>[])new Class[0]);
        }
    }
}
