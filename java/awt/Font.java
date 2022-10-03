package java.awt;

import sun.font.FontAccess;
import sun.font.GlyphLayout;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import sun.font.StandardGlyphVector;
import sun.font.FontUtilities;
import java.awt.geom.Rectangle2D;
import java.awt.font.LineMetrics;
import sun.font.CoreMetrics;
import java.awt.geom.Point2D;
import java.awt.font.FontRenderContext;
import java.text.CharacterIterator;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Permission;
import java.io.FilePermission;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.awt.font.TextAttribute;
import sun.font.EAttribute;
import sun.font.AttributeMap;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.Locale;
import sun.font.CreatedFontTracker;
import java.io.File;
import sun.font.FontManager;
import sun.font.CompositeFont;
import sun.font.FontManagerFactory;
import sun.font.Font2D;
import sun.font.FontLineMetrics;
import java.lang.ref.SoftReference;
import java.awt.geom.AffineTransform;
import sun.font.AttributeValues;
import sun.font.Font2DHandle;
import java.awt.peer.FontPeer;
import java.util.Hashtable;
import java.io.Serializable;

public class Font implements Serializable
{
    private Hashtable<Object, Object> fRequestedAttributes;
    public static final String DIALOG = "Dialog";
    public static final String DIALOG_INPUT = "DialogInput";
    public static final String SANS_SERIF = "SansSerif";
    public static final String SERIF = "Serif";
    public static final String MONOSPACED = "Monospaced";
    public static final int PLAIN = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int ROMAN_BASELINE = 0;
    public static final int CENTER_BASELINE = 1;
    public static final int HANGING_BASELINE = 2;
    public static final int TRUETYPE_FONT = 0;
    public static final int TYPE1_FONT = 1;
    protected String name;
    protected int style;
    protected int size;
    protected float pointSize;
    private transient FontPeer peer;
    private transient long pData;
    private transient Font2DHandle font2DHandle;
    private transient AttributeValues values;
    private transient boolean hasLayoutAttributes;
    private transient boolean createdFont;
    private transient boolean nonIdentityTx;
    private static final AffineTransform identityTx;
    private static final long serialVersionUID = -4206021311591459213L;
    private static final int RECOGNIZED_MASK;
    private static final int PRIMARY_MASK;
    private static final int SECONDARY_MASK;
    private static final int LAYOUT_MASK;
    private static final int EXTRA_MASK;
    private static final float[] ssinfo;
    transient int hash;
    private int fontSerializedDataVersion;
    private transient SoftReference<FontLineMetrics> flmref;
    public static final int LAYOUT_LEFT_TO_RIGHT = 0;
    public static final int LAYOUT_RIGHT_TO_LEFT = 1;
    public static final int LAYOUT_NO_START_CONTEXT = 2;
    public static final int LAYOUT_NO_LIMIT_CONTEXT = 4;
    
    @Deprecated
    public FontPeer getPeer() {
        return this.getPeer_NoClientCode();
    }
    
    final FontPeer getPeer_NoClientCode() {
        if (this.peer == null) {
            this.peer = Toolkit.getDefaultToolkit().getFontPeer(this.name, this.style);
        }
        return this.peer;
    }
    
    private AttributeValues getAttributeValues() {
        if (this.values == null) {
            final AttributeValues values = new AttributeValues();
            values.setFamily(this.name);
            values.setSize(this.pointSize);
            if ((this.style & 0x1) != 0x0) {
                values.setWeight(2.0f);
            }
            if ((this.style & 0x2) != 0x0) {
                values.setPosture(0.2f);
            }
            values.defineAll(Font.PRIMARY_MASK);
            this.values = values;
        }
        return this.values;
    }
    
    private Font2D getFont2D() {
        final FontManager instance = FontManagerFactory.getInstance();
        if (instance.usingPerAppContextComposites() && this.font2DHandle != null && this.font2DHandle.font2D instanceof CompositeFont && ((CompositeFont)this.font2DHandle.font2D).isStdComposite()) {
            return instance.findFont2D(this.name, this.style, 2);
        }
        if (this.font2DHandle == null) {
            this.font2DHandle = instance.findFont2D(this.name, this.style, 2).handle;
        }
        return this.font2DHandle.font2D;
    }
    
    public Font(final String s, final int n, final int size) {
        this.createdFont = false;
        this.fontSerializedDataVersion = 1;
        this.name = ((s != null) ? s : "Default");
        this.style = (((n & 0xFFFFFFFC) == 0x0) ? n : 0);
        this.size = size;
        this.pointSize = (float)size;
    }
    
    private Font(final String s, final int n, final float pointSize) {
        this.createdFont = false;
        this.fontSerializedDataVersion = 1;
        this.name = ((s != null) ? s : "Default");
        this.style = (((n & 0xFFFFFFFC) == 0x0) ? n : 0);
        this.size = (int)(pointSize + 0.5);
        this.pointSize = pointSize;
    }
    
    private Font(final String s, final int n, final float n2, final boolean createdFont, final Font2DHandle font2DHandle) {
        this(s, n, n2);
        this.createdFont = createdFont;
        if (createdFont) {
            if (font2DHandle.font2D instanceof CompositeFont && font2DHandle.font2D.getStyle() != n) {
                this.font2DHandle = FontManagerFactory.getInstance().getNewComposite(null, n, font2DHandle);
            }
            else {
                this.font2DHandle = font2DHandle;
            }
        }
    }
    
    private Font(final File file, final int n, final boolean b, final CreatedFontTracker createdFontTracker) throws FontFormatException {
        this.createdFont = false;
        this.fontSerializedDataVersion = 1;
        this.createdFont = true;
        this.font2DHandle = FontManagerFactory.getInstance().createFont2D(file, n, b, createdFontTracker).handle;
        this.name = this.font2DHandle.font2D.getFontName(Locale.getDefault());
        this.style = 0;
        this.size = 1;
        this.pointSize = 1.0f;
    }
    
    private Font(final AttributeValues attributeValues, final String s, final int n, final boolean createdFont, final Font2DHandle font2DHandle) {
        this.createdFont = false;
        this.fontSerializedDataVersion = 1;
        this.createdFont = createdFont;
        if (createdFont) {
            this.font2DHandle = font2DHandle;
            String family = null;
            if (s != null) {
                family = attributeValues.getFamily();
                if (s.equals(family)) {
                    family = null;
                }
            }
            int n2 = 0;
            if (n == -1) {
                n2 = -1;
            }
            else {
                if (attributeValues.getWeight() >= 2.0f) {
                    n2 = 1;
                }
                if (attributeValues.getPosture() >= 0.2f) {
                    n2 |= 0x2;
                }
                if (n == n2) {
                    n2 = -1;
                }
            }
            if (font2DHandle.font2D instanceof CompositeFont) {
                if (n2 != -1 || family != null) {
                    this.font2DHandle = FontManagerFactory.getInstance().getNewComposite(family, n2, font2DHandle);
                }
            }
            else if (family != null) {
                this.createdFont = false;
                this.font2DHandle = null;
            }
        }
        this.initFromValues(attributeValues);
    }
    
    public Font(final Map<? extends AttributedCharacterIterator.Attribute, ?> map) {
        this.createdFont = false;
        this.fontSerializedDataVersion = 1;
        this.initFromValues(AttributeValues.fromMap(map, Font.RECOGNIZED_MASK));
    }
    
    protected Font(final Font font) {
        this.createdFont = false;
        this.fontSerializedDataVersion = 1;
        if (font.values != null) {
            this.initFromValues(font.getAttributeValues().clone());
        }
        else {
            this.name = font.name;
            this.style = font.style;
            this.size = font.size;
            this.pointSize = font.pointSize;
        }
        this.font2DHandle = font.font2DHandle;
        this.createdFont = font.createdFont;
    }
    
    private void initFromValues(final AttributeValues values) {
        (this.values = values).defineAll(Font.PRIMARY_MASK);
        this.name = values.getFamily();
        this.pointSize = values.getSize();
        this.size = (int)(values.getSize() + 0.5);
        if (values.getWeight() >= 2.0f) {
            this.style |= 0x1;
        }
        if (values.getPosture() >= 0.2f) {
            this.style |= 0x2;
        }
        this.nonIdentityTx = values.anyNonDefault(Font.EXTRA_MASK);
        this.hasLayoutAttributes = values.anyNonDefault(Font.LAYOUT_MASK);
    }
    
    public static Font getFont(final Map<? extends AttributedCharacterIterator.Attribute, ?> map) {
        if (map instanceof AttributeMap && ((AttributeMap)map).getValues() != null) {
            final AttributeValues values = ((AttributeMap)map).getValues();
            if (!values.isNonDefault(EAttribute.EFONT)) {
                return new Font(map);
            }
            final Font font = values.getFont();
            if (!values.anyDefined(Font.SECONDARY_MASK)) {
                return font;
            }
            final AttributeValues clone = font.getAttributeValues().clone();
            clone.merge(map, Font.SECONDARY_MASK);
            return new Font(clone, font.name, font.style, font.createdFont, font.font2DHandle);
        }
        else {
            final Font font2 = map.get(TextAttribute.FONT);
            if (font2 == null) {
                return new Font(map);
            }
            if (map.size() > 1) {
                final AttributeValues clone2 = font2.getAttributeValues().clone();
                clone2.merge(map, Font.SECONDARY_MASK);
                return new Font(clone2, font2.name, font2.style, font2.createdFont, font2.font2DHandle);
            }
            return font2;
        }
    }
    
    private static boolean hasTempPermission() {
        if (System.getSecurityManager() == null) {
            return true;
        }
        boolean b = false;
        try {
            Files.createTempFile("+~JT", ".tmp", (FileAttribute<?>[])new FileAttribute[0]).toFile().delete();
            b = true;
        }
        catch (final Throwable t) {}
        return b;
    }
    
    public static Font createFont(final int n, final InputStream inputStream) throws FontFormatException, IOException {
        if (hasTempPermission()) {
            return createFont0(n, inputStream, null);
        }
        final CreatedFontTracker tracker = CreatedFontTracker.getTracker();
        boolean acquirePermit = false;
        try {
            acquirePermit = tracker.acquirePermit();
            if (!acquirePermit) {
                throw new IOException("Timed out waiting for resources.");
            }
            return createFont0(n, inputStream, tracker);
        }
        catch (final InterruptedException ex) {
            throw new IOException("Problem reading font data.");
        }
        finally {
            if (acquirePermit) {
                tracker.releasePermit();
            }
        }
    }
    
    private static Font createFont0(final int n, final InputStream inputStream, final CreatedFontTracker createdFontTracker) throws FontFormatException, IOException {
        if (n != 0 && n != 1) {
            throw new IllegalArgumentException("font format not recognized");
        }
        boolean b = false;
        try {
            final File file = AccessController.doPrivileged((PrivilegedExceptionAction<File>)new PrivilegedExceptionAction<File>() {
                @Override
                public File run() throws IOException {
                    return Files.createTempFile("+~JF", ".tmp", (FileAttribute<?>[])new FileAttribute[0]).toFile();
                }
            });
            if (createdFontTracker != null) {
                createdFontTracker.add(file);
            }
            int n2 = 0;
            try {
                final OutputStream outputStream = AccessController.doPrivileged((PrivilegedExceptionAction<OutputStream>)new PrivilegedExceptionAction<OutputStream>() {
                    @Override
                    public OutputStream run() throws IOException {
                        return new FileOutputStream(file);
                    }
                });
                if (createdFontTracker != null) {
                    createdFontTracker.set(file, outputStream);
                }
                try {
                    final byte[] array = new byte[8192];
                    while (true) {
                        final int read = inputStream.read(array);
                        if (read < 0) {
                            break;
                        }
                        if (createdFontTracker != null) {
                            if (n2 + read > 33554432) {
                                throw new IOException("File too big.");
                            }
                            if (n2 + createdFontTracker.getNumBytes() > 335544320) {
                                throw new IOException("Total files too big.");
                            }
                            n2 += read;
                            createdFontTracker.addBytes(read);
                        }
                        outputStream.write(array, 0, read);
                    }
                }
                finally {
                    outputStream.close();
                }
                b = true;
                return new Font(file, n, true, createdFontTracker);
            }
            finally {
                if (createdFontTracker != null) {
                    createdFontTracker.remove(file);
                }
                if (!b) {
                    if (createdFontTracker != null) {
                        createdFontTracker.subBytes(n2);
                    }
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                        @Override
                        public Void run() {
                            file.delete();
                            return null;
                        }
                    });
                }
            }
        }
        catch (final Throwable t) {
            if (t instanceof FontFormatException) {
                throw (FontFormatException)t;
            }
            if (t instanceof IOException) {
                throw (IOException)t;
            }
            final Throwable cause = t.getCause();
            if (cause instanceof FontFormatException) {
                throw (FontFormatException)cause;
            }
            throw new IOException("Problem reading font data.");
        }
    }
    
    public static Font createFont(final int n, File file) throws FontFormatException, IOException {
        file = new File(file.getPath());
        if (n != 0 && n != 1) {
            throw new IllegalArgumentException("font format not recognized");
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new FilePermission(file.getPath(), "read"));
        }
        if (!file.canRead()) {
            throw new IOException("Can't read " + file);
        }
        return new Font(file, n, false, null);
    }
    
    public AffineTransform getTransform() {
        if (this.nonIdentityTx) {
            final AttributeValues attributeValues = this.getAttributeValues();
            final AffineTransform affineTransform = attributeValues.isNonDefault(EAttribute.ETRANSFORM) ? new AffineTransform(attributeValues.getTransform()) : new AffineTransform();
            if (attributeValues.getSuperscript() != 0) {
                final int superscript = attributeValues.getSuperscript();
                double n = 0.0;
                int n2 = 0;
                final boolean b = superscript > 0;
                int n5;
                for (int n3 = b ? -1 : 1, n4 = b ? superscript : (-superscript); (n4 & 0x7) > n2; n4 >>= 3, n3 = -n3, n2 = n5) {
                    n5 = (n4 & 0x7);
                    n += n3 * (Font.ssinfo[n5] - Font.ssinfo[n2]);
                }
                final double n6 = n * this.pointSize;
                final double pow = Math.pow(0.6666666666666666, n2);
                affineTransform.preConcatenate(AffineTransform.getTranslateInstance(0.0, n6));
                affineTransform.scale(pow, pow);
            }
            if (attributeValues.isNonDefault(EAttribute.EWIDTH)) {
                affineTransform.scale(attributeValues.getWidth(), 1.0);
            }
            return affineTransform;
        }
        return new AffineTransform();
    }
    
    public String getFamily() {
        return this.getFamily_NoClientCode();
    }
    
    final String getFamily_NoClientCode() {
        return this.getFamily(Locale.getDefault());
    }
    
    public String getFamily(final Locale locale) {
        if (locale == null) {
            throw new NullPointerException("null locale doesn't mean default");
        }
        return this.getFont2D().getFamilyName(locale);
    }
    
    public String getPSName() {
        return this.getFont2D().getPostscriptName();
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getFontName() {
        return this.getFontName(Locale.getDefault());
    }
    
    public String getFontName(final Locale locale) {
        if (locale == null) {
            throw new NullPointerException("null locale doesn't mean default");
        }
        return this.getFont2D().getFontName(locale);
    }
    
    public int getStyle() {
        return this.style;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public float getSize2D() {
        return this.pointSize;
    }
    
    public boolean isPlain() {
        return this.style == 0;
    }
    
    public boolean isBold() {
        return (this.style & 0x1) != 0x0;
    }
    
    public boolean isItalic() {
        return (this.style & 0x2) != 0x0;
    }
    
    public boolean isTransformed() {
        return this.nonIdentityTx;
    }
    
    public boolean hasLayoutAttributes() {
        return this.hasLayoutAttributes;
    }
    
    public static Font getFont(final String s) {
        return getFont(s, null);
    }
    
    public static Font decode(final String s) {
        int intValue = 12;
        int n = 0;
        if (s == null) {
            return new Font("Dialog", n, intValue);
        }
        final char c = (s.lastIndexOf(45) > s.lastIndexOf(32)) ? '-' : ' ';
        int lastIndex = s.lastIndexOf(c);
        int lastIndex2 = s.lastIndexOf(c, lastIndex - 1);
        final int length = s.length();
        if (lastIndex > 0 && lastIndex + 1 < length) {
            try {
                intValue = Integer.valueOf(s.substring(lastIndex + 1));
                if (intValue <= 0) {
                    intValue = 12;
                }
            }
            catch (final NumberFormatException ex) {
                lastIndex2 = lastIndex;
                lastIndex = length;
                if (s.charAt(lastIndex - 1) == c) {
                    --lastIndex;
                }
            }
        }
        String s2;
        if (lastIndex2 >= 0 && lastIndex2 + 1 < length) {
            final String lowerCase = s.substring(lastIndex2 + 1, lastIndex).toLowerCase(Locale.ENGLISH);
            if (lowerCase.equals("bolditalic")) {
                n = 3;
            }
            else if (lowerCase.equals("italic")) {
                n = 2;
            }
            else if (lowerCase.equals("bold")) {
                n = 1;
            }
            else if (lowerCase.equals("plain")) {
                n = 0;
            }
            else {
                lastIndex2 = lastIndex;
                if (s.charAt(lastIndex2 - 1) == c) {
                    --lastIndex2;
                }
            }
            s2 = s.substring(0, lastIndex2);
        }
        else {
            int n2 = length;
            if (lastIndex2 > 0) {
                n2 = lastIndex2;
            }
            else if (lastIndex > 0) {
                n2 = lastIndex;
            }
            if (n2 > 0 && s.charAt(n2 - 1) == c) {
                --n2;
            }
            s2 = s.substring(0, n2);
        }
        return new Font(s2, n, intValue);
    }
    
    public static Font getFont(final String s, final Font font) {
        String property = null;
        try {
            property = System.getProperty(s);
        }
        catch (final SecurityException ex) {}
        if (property == null) {
            return font;
        }
        return decode(property);
    }
    
    @Override
    public int hashCode() {
        if (this.hash == 0) {
            this.hash = (this.name.hashCode() ^ this.style ^ this.size);
            if (this.nonIdentityTx && this.values != null && this.values.getTransform() != null) {
                this.hash ^= this.values.getTransform().hashCode();
            }
        }
        return this.hash;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o != null) {
            try {
                final Font font = (Font)o;
                if (this.size == font.size && this.style == font.style && this.nonIdentityTx == font.nonIdentityTx && this.hasLayoutAttributes == font.hasLayoutAttributes && this.pointSize == font.pointSize && this.name.equals(font.name)) {
                    if (this.values == null) {
                        return font.values == null || this.getAttributeValues().equals(font.values);
                    }
                    return this.values.equals(font.getAttributeValues());
                }
            }
            catch (final ClassCastException ex) {}
        }
        return false;
    }
    
    @Override
    public String toString() {
        String s;
        if (this.isBold()) {
            s = (this.isItalic() ? "bolditalic" : "bold");
        }
        else {
            s = (this.isItalic() ? "italic" : "plain");
        }
        return this.getClass().getName() + "[family=" + this.getFamily() + ",name=" + this.name + ",style=" + s + ",size=" + this.size + "]";
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws ClassNotFoundException, IOException {
        if (this.values != null) {
            synchronized (this.values) {
                this.fRequestedAttributes = this.values.toSerializableHashtable();
                objectOutputStream.defaultWriteObject();
                this.fRequestedAttributes = null;
            }
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        if (this.pointSize == 0.0f) {
            this.pointSize = (float)this.size;
        }
        if (this.fRequestedAttributes != null) {
            try {
                this.values = this.getAttributeValues();
                final AttributeValues fromSerializableHashtable = AttributeValues.fromSerializableHashtable(this.fRequestedAttributes);
                if (!AttributeValues.is16Hashtable(this.fRequestedAttributes)) {
                    fromSerializableHashtable.unsetDefault();
                }
                this.values = this.getAttributeValues().merge(fromSerializableHashtable);
                this.nonIdentityTx = this.values.anyNonDefault(Font.EXTRA_MASK);
                this.hasLayoutAttributes = this.values.anyNonDefault(Font.LAYOUT_MASK);
            }
            catch (final Throwable t) {
                throw new IOException(t);
            }
            finally {
                this.fRequestedAttributes = null;
            }
        }
    }
    
    public int getNumGlyphs() {
        return this.getFont2D().getNumGlyphs();
    }
    
    public int getMissingGlyphCode() {
        return this.getFont2D().getMissingGlyphCode();
    }
    
    public byte getBaselineFor(final char c) {
        return this.getFont2D().getBaselineFor(c);
    }
    
    public Map<TextAttribute, ?> getAttributes() {
        return new AttributeMap(this.getAttributeValues());
    }
    
    public AttributedCharacterIterator.Attribute[] getAvailableAttributes() {
        return new AttributedCharacterIterator.Attribute[] { TextAttribute.FAMILY, TextAttribute.WEIGHT, TextAttribute.WIDTH, TextAttribute.POSTURE, TextAttribute.SIZE, TextAttribute.TRANSFORM, TextAttribute.SUPERSCRIPT, TextAttribute.CHAR_REPLACEMENT, TextAttribute.FOREGROUND, TextAttribute.BACKGROUND, TextAttribute.UNDERLINE, TextAttribute.STRIKETHROUGH, TextAttribute.RUN_DIRECTION, TextAttribute.BIDI_EMBEDDING, TextAttribute.JUSTIFICATION, TextAttribute.INPUT_METHOD_HIGHLIGHT, TextAttribute.INPUT_METHOD_UNDERLINE, TextAttribute.SWAP_COLORS, TextAttribute.NUMERIC_SHAPING, TextAttribute.KERNING, TextAttribute.LIGATURES, TextAttribute.TRACKING };
    }
    
    public Font deriveFont(final int n, final float size) {
        if (this.values == null) {
            return new Font(this.name, n, size, this.createdFont, this.font2DHandle);
        }
        final AttributeValues clone = this.getAttributeValues().clone();
        final int n2 = (this.style != n) ? this.style : -1;
        applyStyle(n, clone);
        clone.setSize(size);
        return new Font(clone, null, n2, this.createdFont, this.font2DHandle);
    }
    
    public Font deriveFont(final int n, final AffineTransform affineTransform) {
        final AttributeValues clone = this.getAttributeValues().clone();
        final int n2 = (this.style != n) ? this.style : -1;
        applyStyle(n, clone);
        applyTransform(affineTransform, clone);
        return new Font(clone, null, n2, this.createdFont, this.font2DHandle);
    }
    
    public Font deriveFont(final float size) {
        if (this.values == null) {
            return new Font(this.name, this.style, size, this.createdFont, this.font2DHandle);
        }
        final AttributeValues clone = this.getAttributeValues().clone();
        clone.setSize(size);
        return new Font(clone, null, -1, this.createdFont, this.font2DHandle);
    }
    
    public Font deriveFont(final AffineTransform affineTransform) {
        final AttributeValues clone = this.getAttributeValues().clone();
        applyTransform(affineTransform, clone);
        return new Font(clone, null, -1, this.createdFont, this.font2DHandle);
    }
    
    public Font deriveFont(final int n) {
        if (this.values == null) {
            return new Font(this.name, n, (float)this.size, this.createdFont, this.font2DHandle);
        }
        final AttributeValues clone = this.getAttributeValues().clone();
        final int n2 = (this.style != n) ? this.style : -1;
        applyStyle(n, clone);
        return new Font(clone, null, n2, this.createdFont, this.font2DHandle);
    }
    
    public Font deriveFont(final Map<? extends AttributedCharacterIterator.Attribute, ?> map) {
        if (map == null) {
            return this;
        }
        final AttributeValues clone = this.getAttributeValues().clone();
        clone.merge(map, Font.RECOGNIZED_MASK);
        return new Font(clone, this.name, this.style, this.createdFont, this.font2DHandle);
    }
    
    public boolean canDisplay(final char c) {
        return this.getFont2D().canDisplay(c);
    }
    
    public boolean canDisplay(final int n) {
        if (!Character.isValidCodePoint(n)) {
            throw new IllegalArgumentException("invalid code point: " + Integer.toHexString(n));
        }
        return this.getFont2D().canDisplay(n);
    }
    
    public int canDisplayUpTo(final String s) {
        final Font2D font2D = this.getFont2D();
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (!font2D.canDisplay(char1)) {
                if (!Character.isHighSurrogate(char1)) {
                    return i;
                }
                if (!font2D.canDisplay(s.codePointAt(i))) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }
    
    public int canDisplayUpTo(final char[] array, final int n, final int n2) {
        final Font2D font2D = this.getFont2D();
        for (int i = n; i < n2; ++i) {
            final char c = array[i];
            if (!font2D.canDisplay(c)) {
                if (!Character.isHighSurrogate(c)) {
                    return i;
                }
                if (!font2D.canDisplay(Character.codePointAt(array, i, n2))) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }
    
    public int canDisplayUpTo(final CharacterIterator characterIterator, final int index, final int n) {
        final Font2D font2D = this.getFont2D();
        char c = characterIterator.setIndex(index);
        for (int i = index; i < n; ++i, c = characterIterator.next()) {
            if (!font2D.canDisplay(c)) {
                if (!Character.isHighSurrogate(c)) {
                    return i;
                }
                final char next = characterIterator.next();
                if (!Character.isLowSurrogate(next)) {
                    return i;
                }
                if (!font2D.canDisplay(Character.toCodePoint(c, next))) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }
    
    public float getItalicAngle() {
        return this.getItalicAngle(null);
    }
    
    private float getItalicAngle(final FontRenderContext fontRenderContext) {
        Object o;
        Object o2;
        if (fontRenderContext == null) {
            o = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
            o2 = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
        }
        else {
            o = fontRenderContext.getAntiAliasingHint();
            o2 = fontRenderContext.getFractionalMetricsHint();
        }
        return this.getFont2D().getItalicAngle(this, Font.identityTx, o, o2);
    }
    
    public boolean hasUniformLineMetrics() {
        return false;
    }
    
    private FontLineMetrics defaultLineMetrics(final FontRenderContext fontRenderContext) {
        FontLineMetrics fontLineMetrics;
        if (this.flmref == null || (fontLineMetrics = this.flmref.get()) == null || !fontLineMetrics.frc.equals(fontRenderContext)) {
            final float[] array = new float[8];
            this.getFont2D().getFontMetrics(this, Font.identityTx, fontRenderContext.getAntiAliasingHint(), fontRenderContext.getFractionalMetricsHint(), array);
            float n = array[0];
            float n2 = array[1];
            final float n3 = array[2];
            float n4 = 0.0f;
            if (this.values != null && this.values.getSuperscript() != 0) {
                n4 = (float)this.getTransform().getTranslateY();
                n -= n4;
                n2 += n4;
            }
            final float n5 = n + n2 + n3;
            final int n6 = 0;
            final float[] array2 = { 0.0f, (n2 / 2.0f - n) / 2.0f, -n };
            float y = array[4];
            float y2 = array[5];
            float y3 = array[6];
            float y4 = array[7];
            final float italicAngle = this.getItalicAngle(fontRenderContext);
            if (this.isTransformed()) {
                final AffineTransform charTransform = this.values.getCharTransform();
                if (charTransform != null) {
                    final Point2D.Float float1 = new Point2D.Float();
                    float1.setLocation(0.0f, y);
                    charTransform.deltaTransform(float1, float1);
                    y = float1.y;
                    float1.setLocation(0.0f, y2);
                    charTransform.deltaTransform(float1, float1);
                    y2 = float1.y;
                    float1.setLocation(0.0f, y3);
                    charTransform.deltaTransform(float1, float1);
                    y3 = float1.y;
                    float1.setLocation(0.0f, y4);
                    charTransform.deltaTransform(float1, float1);
                    y4 = float1.y;
                }
            }
            fontLineMetrics = new FontLineMetrics(0, new CoreMetrics(n, n2, n3, n5, n6, array2, y + n4, y2, y3 + n4, y4, n4, italicAngle), fontRenderContext);
            this.flmref = new SoftReference<FontLineMetrics>(fontLineMetrics);
        }
        return (FontLineMetrics)fontLineMetrics.clone();
    }
    
    public LineMetrics getLineMetrics(final String s, final FontRenderContext fontRenderContext) {
        final FontLineMetrics defaultLineMetrics = this.defaultLineMetrics(fontRenderContext);
        defaultLineMetrics.numchars = s.length();
        return defaultLineMetrics;
    }
    
    public LineMetrics getLineMetrics(final String s, final int n, final int n2, final FontRenderContext fontRenderContext) {
        final FontLineMetrics defaultLineMetrics = this.defaultLineMetrics(fontRenderContext);
        final int n3 = n2 - n;
        defaultLineMetrics.numchars = ((n3 < 0) ? 0 : n3);
        return defaultLineMetrics;
    }
    
    public LineMetrics getLineMetrics(final char[] array, final int n, final int n2, final FontRenderContext fontRenderContext) {
        final FontLineMetrics defaultLineMetrics = this.defaultLineMetrics(fontRenderContext);
        final int n3 = n2 - n;
        defaultLineMetrics.numchars = ((n3 < 0) ? 0 : n3);
        return defaultLineMetrics;
    }
    
    public LineMetrics getLineMetrics(final CharacterIterator characterIterator, final int n, final int n2, final FontRenderContext fontRenderContext) {
        final FontLineMetrics defaultLineMetrics = this.defaultLineMetrics(fontRenderContext);
        final int n3 = n2 - n;
        defaultLineMetrics.numchars = ((n3 < 0) ? 0 : n3);
        return defaultLineMetrics;
    }
    
    public Rectangle2D getStringBounds(final String s, final FontRenderContext fontRenderContext) {
        final char[] charArray = s.toCharArray();
        return this.getStringBounds(charArray, 0, charArray.length, fontRenderContext);
    }
    
    public Rectangle2D getStringBounds(final String s, final int n, final int n2, final FontRenderContext fontRenderContext) {
        return this.getStringBounds(s.substring(n, n2), fontRenderContext);
    }
    
    public Rectangle2D getStringBounds(final char[] array, final int n, final int n2, final FontRenderContext fontRenderContext) {
        if (n < 0) {
            throw new IndexOutOfBoundsException("beginIndex: " + n);
        }
        if (n2 > array.length) {
            throw new IndexOutOfBoundsException("limit: " + n2);
        }
        if (n > n2) {
            throw new IndexOutOfBoundsException("range length: " + (n2 - n));
        }
        boolean b = this.values == null || (this.values.getKerning() == 0 && this.values.getLigatures() == 0 && this.values.getBaselineTransform() == null);
        if (b) {
            b = !FontUtilities.isComplexText(array, n, n2);
        }
        if (b) {
            return new StandardGlyphVector(this, array, n, n2 - n, fontRenderContext).getLogicalBounds();
        }
        final TextLayout textLayout = new TextLayout(new String(array, n, n2 - n), this, fontRenderContext);
        return new Rectangle2D.Float(0.0f, -textLayout.getAscent(), textLayout.getAdvance(), textLayout.getAscent() + textLayout.getDescent() + textLayout.getLeading());
    }
    
    public Rectangle2D getStringBounds(final CharacterIterator characterIterator, final int index, final int n, final FontRenderContext fontRenderContext) {
        final int beginIndex = characterIterator.getBeginIndex();
        final int endIndex = characterIterator.getEndIndex();
        if (index < beginIndex) {
            throw new IndexOutOfBoundsException("beginIndex: " + index);
        }
        if (n > endIndex) {
            throw new IndexOutOfBoundsException("limit: " + n);
        }
        if (index > n) {
            throw new IndexOutOfBoundsException("range length: " + (n - index));
        }
        final char[] array = new char[n - index];
        characterIterator.setIndex(index);
        for (int i = 0; i < array.length; ++i) {
            array[i] = characterIterator.current();
            characterIterator.next();
        }
        return this.getStringBounds(array, 0, array.length, fontRenderContext);
    }
    
    public Rectangle2D getMaxCharBounds(final FontRenderContext fontRenderContext) {
        final float[] array = new float[4];
        this.getFont2D().getFontMetrics(this, fontRenderContext, array);
        return new Rectangle2D.Float(0.0f, -array[0], array[3], array[0] + array[1] + array[2]);
    }
    
    public GlyphVector createGlyphVector(final FontRenderContext fontRenderContext, final String s) {
        return new StandardGlyphVector(this, s, fontRenderContext);
    }
    
    public GlyphVector createGlyphVector(final FontRenderContext fontRenderContext, final char[] array) {
        return new StandardGlyphVector(this, array, fontRenderContext);
    }
    
    public GlyphVector createGlyphVector(final FontRenderContext fontRenderContext, final CharacterIterator characterIterator) {
        return new StandardGlyphVector(this, characterIterator, fontRenderContext);
    }
    
    public GlyphVector createGlyphVector(final FontRenderContext fontRenderContext, final int[] array) {
        return new StandardGlyphVector(this, array, fontRenderContext);
    }
    
    public GlyphVector layoutGlyphVector(final FontRenderContext fontRenderContext, final char[] array, final int n, final int n2, final int n3) {
        final GlyphLayout value = GlyphLayout.get(null);
        final StandardGlyphVector layout = value.layout(this, fontRenderContext, array, n, n2 - n, n3, null);
        GlyphLayout.done(value);
        return layout;
    }
    
    private static void applyTransform(final AffineTransform transform, final AttributeValues attributeValues) {
        if (transform == null) {
            throw new IllegalArgumentException("transform must not be null");
        }
        attributeValues.setTransform(transform);
    }
    
    private static void applyStyle(final int n, final AttributeValues attributeValues) {
        attributeValues.setWeight(((n & 0x1) != 0x0) ? 2.0f : 1.0f);
        attributeValues.setPosture(((n & 0x2) != 0x0) ? 0.2f : 0.0f);
    }
    
    private static native void initIDs();
    
    static {
        Toolkit.loadLibraries();
        initIDs();
        FontAccess.setFontAccess(new FontAccessImpl());
        identityTx = new AffineTransform();
        RECOGNIZED_MASK = (AttributeValues.MASK_ALL & ~AttributeValues.getMask(EAttribute.EFONT));
        PRIMARY_MASK = AttributeValues.getMask(EAttribute.EFAMILY, EAttribute.EWEIGHT, EAttribute.EWIDTH, EAttribute.EPOSTURE, EAttribute.ESIZE, EAttribute.ETRANSFORM, EAttribute.ESUPERSCRIPT, EAttribute.ETRACKING);
        SECONDARY_MASK = (Font.RECOGNIZED_MASK & ~Font.PRIMARY_MASK);
        LAYOUT_MASK = AttributeValues.getMask(EAttribute.ECHAR_REPLACEMENT, EAttribute.EFOREGROUND, EAttribute.EBACKGROUND, EAttribute.EUNDERLINE, EAttribute.ESTRIKETHROUGH, EAttribute.ERUN_DIRECTION, EAttribute.EBIDI_EMBEDDING, EAttribute.EJUSTIFICATION, EAttribute.EINPUT_METHOD_HIGHLIGHT, EAttribute.EINPUT_METHOD_UNDERLINE, EAttribute.ESWAP_COLORS, EAttribute.ENUMERIC_SHAPING, EAttribute.EKERNING, EAttribute.ELIGATURES, EAttribute.ETRACKING, EAttribute.ESUPERSCRIPT);
        EXTRA_MASK = AttributeValues.getMask(EAttribute.ETRANSFORM, EAttribute.ESUPERSCRIPT, EAttribute.EWIDTH);
        ssinfo = new float[] { 0.0f, 0.375f, 0.625f, 0.7916667f, 0.9027778f, 0.9768519f, 1.0262346f, 1.0591564f };
    }
    
    private static class FontAccessImpl extends FontAccess
    {
        @Override
        public Font2D getFont2D(final Font font) {
            return font.getFont2D();
        }
        
        @Override
        public void setFont2D(final Font font, final Font2DHandle font2DHandle) {
            font.font2DHandle = font2DHandle;
        }
        
        @Override
        public void setCreatedFont(final Font font) {
            font.createdFont = true;
        }
        
        @Override
        public boolean isCreatedFont(final Font font) {
            return font.createdFont;
        }
    }
}
