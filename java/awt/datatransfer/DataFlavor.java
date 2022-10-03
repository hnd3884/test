package java.awt.datatransfer;

import java.io.OptionalDataException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.io.Serializable;
import java.util.Objects;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.io.CharArrayReader;
import java.nio.CharBuffer;
import java.io.StringReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import sun.awt.datatransfer.DataTransferer;
import java.security.Permission;
import sun.security.util.SecurityConstants;
import sun.reflect.misc.ReflectUtil;
import java.util.Comparator;
import java.io.InputStream;
import java.io.Externalizable;

public class DataFlavor implements Externalizable, Cloneable
{
    private static final long serialVersionUID = 8367026044764648243L;
    private static final Class<InputStream> ioInputStreamClass;
    public static final DataFlavor stringFlavor;
    public static final DataFlavor imageFlavor;
    @Deprecated
    public static final DataFlavor plainTextFlavor;
    public static final String javaSerializedObjectMimeType = "application/x-java-serialized-object";
    public static final DataFlavor javaFileListFlavor;
    public static final String javaJVMLocalObjectMimeType = "application/x-java-jvm-local-objectref";
    public static final String javaRemoteObjectMimeType = "application/x-java-remote-object";
    public static DataFlavor selectionHtmlFlavor;
    public static DataFlavor fragmentHtmlFlavor;
    public static DataFlavor allHtmlFlavor;
    private static Comparator<DataFlavor> textFlavorComparator;
    transient int atom;
    MimeType mimeType;
    private String humanPresentableName;
    private Class<?> representationClass;
    
    protected static final Class<?> tryToLoadClass(final String s, final ClassLoader classLoader) throws ClassNotFoundException {
        ReflectUtil.checkPackageAccess(s);
        try {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
            }
            final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            try {
                return Class.forName(s, true, systemClassLoader);
            }
            catch (final ClassNotFoundException ex) {
                final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                if (contextClassLoader != null) {
                    try {
                        return Class.forName(s, true, contextClassLoader);
                    }
                    catch (final ClassNotFoundException ex2) {}
                }
            }
        }
        catch (final SecurityException ex3) {}
        return Class.forName(s, true, classLoader);
    }
    
    private static DataFlavor createConstant(final Class<?> clazz, final String s) {
        try {
            return new DataFlavor(clazz, s);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    private static DataFlavor createConstant(final String s, final String s2) {
        try {
            return new DataFlavor(s, s2);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    private static DataFlavor initHtmlDataFlavor(final String s) {
        try {
            return new DataFlavor("text/html; class=java.lang.String;document=" + s + ";charset=Unicode");
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public DataFlavor() {
    }
    
    private DataFlavor(final String s, final String s2, MimeTypeParameterList list, final Class<?> representationClass, String humanPresentableName) {
        if (s == null) {
            throw new NullPointerException("primaryType");
        }
        if (s2 == null) {
            throw new NullPointerException("subType");
        }
        if (representationClass == null) {
            throw new NullPointerException("representationClass");
        }
        if (list == null) {
            list = new MimeTypeParameterList();
        }
        list.set("class", representationClass.getName());
        if (humanPresentableName == null) {
            humanPresentableName = list.get("humanPresentableName");
            if (humanPresentableName == null) {
                humanPresentableName = s + "/" + s2;
            }
        }
        try {
            this.mimeType = new MimeType(s, s2, list);
        }
        catch (final MimeTypeParseException ex) {
            throw new IllegalArgumentException("MimeType Parse Exception: " + ex.getMessage());
        }
        this.representationClass = representationClass;
        this.humanPresentableName = humanPresentableName;
        this.mimeType.removeParameter("humanPresentableName");
    }
    
    public DataFlavor(final Class<?> clazz, final String s) {
        this("application", "x-java-serialized-object", null, clazz, s);
        if (clazz == null) {
            throw new NullPointerException("representationClass");
        }
    }
    
    public DataFlavor(final String s, final String s2) {
        if (s == null) {
            throw new NullPointerException("mimeType");
        }
        try {
            this.initialize(s, s2, this.getClass().getClassLoader());
        }
        catch (final MimeTypeParseException ex) {
            throw new IllegalArgumentException("failed to parse:" + s);
        }
        catch (final ClassNotFoundException ex2) {
            throw new IllegalArgumentException("can't find specified class: " + ex2.getMessage());
        }
    }
    
    public DataFlavor(final String s, final String s2, final ClassLoader classLoader) throws ClassNotFoundException {
        if (s == null) {
            throw new NullPointerException("mimeType");
        }
        try {
            this.initialize(s, s2, classLoader);
        }
        catch (final MimeTypeParseException ex) {
            throw new IllegalArgumentException("failed to parse:" + s);
        }
    }
    
    public DataFlavor(final String s) throws ClassNotFoundException {
        if (s == null) {
            throw new NullPointerException("mimeType");
        }
        try {
            this.initialize(s, null, this.getClass().getClassLoader());
        }
        catch (final MimeTypeParseException ex) {
            throw new IllegalArgumentException("failed to parse:" + s);
        }
    }
    
    private void initialize(final String s, String humanPresentableName, final ClassLoader classLoader) throws MimeTypeParseException, ClassNotFoundException {
        if (s == null) {
            throw new NullPointerException("mimeType");
        }
        this.mimeType = new MimeType(s);
        final String parameter = this.getParameter("class");
        if (parameter == null) {
            if ("application/x-java-serialized-object".equals(this.mimeType.getBaseType())) {
                throw new IllegalArgumentException("no representation class specified for:" + s);
            }
            this.representationClass = InputStream.class;
        }
        else {
            this.representationClass = tryToLoadClass(parameter, classLoader);
        }
        this.mimeType.setParameter("class", this.representationClass.getName());
        if (humanPresentableName == null) {
            humanPresentableName = this.mimeType.getParameter("humanPresentableName");
            if (humanPresentableName == null) {
                humanPresentableName = this.mimeType.getPrimaryType() + "/" + this.mimeType.getSubType();
            }
        }
        this.humanPresentableName = humanPresentableName;
        this.mimeType.removeParameter("humanPresentableName");
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[" + this.paramString() + "]";
    }
    
    private String paramString() {
        final String string = "" + "mimetype=";
        String s;
        if (this.mimeType == null) {
            s = string + "null";
        }
        else {
            s = string + this.mimeType.getBaseType();
        }
        final String string2 = s + ";representationclass=";
        String s2;
        if (this.representationClass == null) {
            s2 = string2 + "null";
        }
        else {
            s2 = string2 + this.representationClass.getName();
        }
        if (DataTransferer.isFlavorCharsetTextType(this) && (this.isRepresentationClassInputStream() || this.isRepresentationClassByteBuffer() || byte[].class.equals(this.representationClass))) {
            s2 = s2 + ";charset=" + DataTransferer.getTextCharset(this);
        }
        return s2;
    }
    
    public static final DataFlavor getTextPlainUnicodeFlavor() {
        String defaultUnicodeEncoding = null;
        final DataTransferer instance = DataTransferer.getInstance();
        if (instance != null) {
            defaultUnicodeEncoding = instance.getDefaultUnicodeEncoding();
        }
        return new DataFlavor("text/plain;charset=" + defaultUnicodeEncoding + ";class=java.io.InputStream", "Plain Text");
    }
    
    public static final DataFlavor selectBestTextFlavor(final DataFlavor[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        if (DataFlavor.textFlavorComparator == null) {
            DataFlavor.textFlavorComparator = new TextFlavorComparator();
        }
        final DataFlavor dataFlavor = Collections.max((Collection<? extends DataFlavor>)Arrays.asList(array), (Comparator<? super DataFlavor>)DataFlavor.textFlavorComparator);
        if (!dataFlavor.isFlavorTextType()) {
            return null;
        }
        return dataFlavor;
    }
    
    public Reader getReaderForText(final Transferable transferable) throws UnsupportedFlavorException, IOException {
        final Object transferData = transferable.getTransferData(this);
        if (transferData == null) {
            throw new IllegalArgumentException("getTransferData() returned null");
        }
        if (transferData instanceof Reader) {
            return (Reader)transferData;
        }
        if (transferData instanceof String) {
            return new StringReader((String)transferData);
        }
        if (transferData instanceof CharBuffer) {
            final CharBuffer charBuffer = (CharBuffer)transferData;
            final int remaining = charBuffer.remaining();
            final char[] array = new char[remaining];
            charBuffer.get(array, 0, remaining);
            return new CharArrayReader(array);
        }
        if (transferData instanceof char[]) {
            return new CharArrayReader((char[])transferData);
        }
        InputStream inputStream = null;
        if (transferData instanceof InputStream) {
            inputStream = (InputStream)transferData;
        }
        else if (transferData instanceof ByteBuffer) {
            final ByteBuffer byteBuffer = (ByteBuffer)transferData;
            final int remaining2 = byteBuffer.remaining();
            final byte[] array2 = new byte[remaining2];
            byteBuffer.get(array2, 0, remaining2);
            inputStream = new ByteArrayInputStream(array2);
        }
        else if (transferData instanceof byte[]) {
            inputStream = new ByteArrayInputStream((byte[])transferData);
        }
        if (inputStream == null) {
            throw new IllegalArgumentException("transfer data is not Reader, String, CharBuffer, char array, InputStream, ByteBuffer, or byte array");
        }
        final String parameter = this.getParameter("charset");
        return (parameter == null) ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, parameter);
    }
    
    public String getMimeType() {
        return (this.mimeType != null) ? this.mimeType.toString() : null;
    }
    
    public Class<?> getRepresentationClass() {
        return this.representationClass;
    }
    
    public String getHumanPresentableName() {
        return this.humanPresentableName;
    }
    
    public String getPrimaryType() {
        return (this.mimeType != null) ? this.mimeType.getPrimaryType() : null;
    }
    
    public String getSubType() {
        return (this.mimeType != null) ? this.mimeType.getSubType() : null;
    }
    
    public String getParameter(final String s) {
        if (s.equals("humanPresentableName")) {
            return this.humanPresentableName;
        }
        return (this.mimeType != null) ? this.mimeType.getParameter(s) : null;
    }
    
    public void setHumanPresentableName(final String humanPresentableName) {
        this.humanPresentableName = humanPresentableName;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof DataFlavor && this.equals((DataFlavor)o);
    }
    
    public boolean equals(final DataFlavor dataFlavor) {
        if (dataFlavor == null) {
            return false;
        }
        if (this == dataFlavor) {
            return true;
        }
        if (!Objects.equals(this.getRepresentationClass(), dataFlavor.getRepresentationClass())) {
            return false;
        }
        if (this.mimeType == null) {
            if (dataFlavor.mimeType != null) {
                return false;
            }
        }
        else {
            if (!this.mimeType.match(dataFlavor.mimeType)) {
                return false;
            }
            if ("text".equals(this.getPrimaryType())) {
                if (DataTransferer.doesSubtypeSupportCharset(this) && this.representationClass != null && !this.isStandardTextRepresentationClass() && !Objects.equals(DataTransferer.canonicalName(this.getParameter("charset")), DataTransferer.canonicalName(dataFlavor.getParameter("charset")))) {
                    return false;
                }
                if ("html".equals(this.getSubType()) && !Objects.equals(this.getParameter("document"), dataFlavor.getParameter("document"))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Deprecated
    public boolean equals(final String s) {
        return s != null && this.mimeType != null && this.isMimeTypeEqual(s);
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        if (this.representationClass != null) {
            n += this.representationClass.hashCode();
        }
        if (this.mimeType != null) {
            final String primaryType = this.mimeType.getPrimaryType();
            if (primaryType != null) {
                n += primaryType.hashCode();
            }
            if ("text".equals(primaryType)) {
                if (DataTransferer.doesSubtypeSupportCharset(this) && this.representationClass != null && !this.isStandardTextRepresentationClass()) {
                    final String canonicalName = DataTransferer.canonicalName(this.getParameter("charset"));
                    if (canonicalName != null) {
                        n += canonicalName.hashCode();
                    }
                }
                if ("html".equals(this.getSubType())) {
                    final String parameter = this.getParameter("document");
                    if (parameter != null) {
                        n += parameter.hashCode();
                    }
                }
            }
        }
        return n;
    }
    
    public boolean match(final DataFlavor dataFlavor) {
        return this.equals(dataFlavor);
    }
    
    public boolean isMimeTypeEqual(final String s) {
        if (s == null) {
            throw new NullPointerException("mimeType");
        }
        if (this.mimeType == null) {
            return false;
        }
        try {
            return this.mimeType.match(new MimeType(s));
        }
        catch (final MimeTypeParseException ex) {
            return false;
        }
    }
    
    public final boolean isMimeTypeEqual(final DataFlavor dataFlavor) {
        return this.isMimeTypeEqual(dataFlavor.mimeType);
    }
    
    private boolean isMimeTypeEqual(final MimeType mimeType) {
        if (this.mimeType == null) {
            return mimeType == null;
        }
        return this.mimeType.match(mimeType);
    }
    
    private boolean isStandardTextRepresentationClass() {
        return this.isRepresentationClassReader() || String.class.equals(this.representationClass) || this.isRepresentationClassCharBuffer() || char[].class.equals(this.representationClass);
    }
    
    public boolean isMimeTypeSerializedObject() {
        return this.isMimeTypeEqual("application/x-java-serialized-object");
    }
    
    public final Class<?> getDefaultRepresentationClass() {
        return DataFlavor.ioInputStreamClass;
    }
    
    public final String getDefaultRepresentationClassAsString() {
        return this.getDefaultRepresentationClass().getName();
    }
    
    public boolean isRepresentationClassInputStream() {
        return DataFlavor.ioInputStreamClass.isAssignableFrom(this.representationClass);
    }
    
    public boolean isRepresentationClassReader() {
        return Reader.class.isAssignableFrom(this.representationClass);
    }
    
    public boolean isRepresentationClassCharBuffer() {
        return CharBuffer.class.isAssignableFrom(this.representationClass);
    }
    
    public boolean isRepresentationClassByteBuffer() {
        return ByteBuffer.class.isAssignableFrom(this.representationClass);
    }
    
    public boolean isRepresentationClassSerializable() {
        return Serializable.class.isAssignableFrom(this.representationClass);
    }
    
    public boolean isRepresentationClassRemote() {
        return DataTransferer.isRemote(this.representationClass);
    }
    
    public boolean isFlavorSerializedObjectType() {
        return this.isRepresentationClassSerializable() && this.isMimeTypeEqual("application/x-java-serialized-object");
    }
    
    public boolean isFlavorRemoteObjectType() {
        return this.isRepresentationClassRemote() && this.isRepresentationClassSerializable() && this.isMimeTypeEqual("application/x-java-remote-object");
    }
    
    public boolean isFlavorJavaFileListType() {
        return this.mimeType != null && this.representationClass != null && List.class.isAssignableFrom(this.representationClass) && this.mimeType.match(DataFlavor.javaFileListFlavor.mimeType);
    }
    
    public boolean isFlavorTextType() {
        return DataTransferer.isFlavorCharsetTextType(this) || DataTransferer.isFlavorNoncharsetTextType(this);
    }
    
    @Override
    public synchronized void writeExternal(final ObjectOutput objectOutput) throws IOException {
        if (this.mimeType != null) {
            this.mimeType.setParameter("humanPresentableName", this.humanPresentableName);
            objectOutput.writeObject(this.mimeType);
            this.mimeType.removeParameter("humanPresentableName");
        }
        else {
            objectOutput.writeObject(null);
        }
        objectOutput.writeObject(this.representationClass);
    }
    
    @Override
    public synchronized void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        String parameter = null;
        this.mimeType = (MimeType)objectInput.readObject();
        if (this.mimeType != null) {
            this.humanPresentableName = this.mimeType.getParameter("humanPresentableName");
            this.mimeType.removeParameter("humanPresentableName");
            parameter = this.mimeType.getParameter("class");
            if (parameter == null) {
                throw new IOException("no class parameter specified in: " + this.mimeType);
            }
        }
        try {
            this.representationClass = (Class)objectInput.readObject();
        }
        catch (final OptionalDataException ex) {
            if (!ex.eof || ex.length != 0) {
                throw ex;
            }
            if (parameter != null) {
                this.representationClass = tryToLoadClass(parameter, this.getClass().getClassLoader());
            }
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        final Object clone = super.clone();
        if (this.mimeType != null) {
            ((DataFlavor)clone).mimeType = (MimeType)this.mimeType.clone();
        }
        return clone;
    }
    
    @Deprecated
    protected String normalizeMimeTypeParameter(final String s, final String s2) {
        return s2;
    }
    
    @Deprecated
    protected String normalizeMimeType(final String s) {
        return s;
    }
    
    static {
        ioInputStreamClass = InputStream.class;
        stringFlavor = createConstant(String.class, "Unicode String");
        imageFlavor = createConstant("image/x-java-image; class=java.awt.Image", "Image");
        plainTextFlavor = createConstant("text/plain; charset=unicode; class=java.io.InputStream", "Plain Text");
        javaFileListFlavor = createConstant("application/x-java-file-list;class=java.util.List", null);
        DataFlavor.selectionHtmlFlavor = initHtmlDataFlavor("selection");
        DataFlavor.fragmentHtmlFlavor = initHtmlDataFlavor("fragment");
        DataFlavor.allHtmlFlavor = initHtmlDataFlavor("all");
    }
    
    static class TextFlavorComparator extends DataTransferer.DataFlavorComparator
    {
        @Override
        public int compare(final Object o, final Object o2) {
            final DataFlavor dataFlavor = (DataFlavor)o;
            final DataFlavor dataFlavor2 = (DataFlavor)o2;
            if (dataFlavor.isFlavorTextType()) {
                if (dataFlavor2.isFlavorTextType()) {
                    return super.compare(o, o2);
                }
                return 1;
            }
            else {
                if (dataFlavor2.isFlavorTextType()) {
                    return -1;
                }
                return 0;
            }
        }
    }
}
