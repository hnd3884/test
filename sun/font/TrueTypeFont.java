package sun.font;

import java.nio.CharBuffer;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.nio.ShortBuffer;
import sun.awt.SunToolkit;
import sun.security.action.GetPropertyAction;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.security.AccessController;
import java.io.FileNotFoundException;
import java.security.PrivilegedAction;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.awt.GraphicsEnvironment;
import sun.java2d.DisposerRecord;
import sun.java2d.Disposer;
import java.awt.FontFormatException;
import java.util.Locale;
import java.util.Map;

public class TrueTypeFont extends FileFont
{
    public static final int cmapTag = 1668112752;
    public static final int glyfTag = 1735162214;
    public static final int headTag = 1751474532;
    public static final int hheaTag = 1751672161;
    public static final int hmtxTag = 1752003704;
    public static final int locaTag = 1819239265;
    public static final int maxpTag = 1835104368;
    public static final int nameTag = 1851878757;
    public static final int postTag = 1886352244;
    public static final int os_2Tag = 1330851634;
    public static final int GDEFTag = 1195656518;
    public static final int GPOSTag = 1196445523;
    public static final int GSUBTag = 1196643650;
    public static final int mortTag = 1836020340;
    public static final int morxTag = 1836020344;
    public static final int fdscTag = 1717859171;
    public static final int fvarTag = 1719034226;
    public static final int featTag = 1717920116;
    public static final int EBLCTag = 1161972803;
    public static final int gaspTag = 1734439792;
    public static final int ttcfTag = 1953784678;
    public static final int v1ttTag = 65536;
    public static final int trueTag = 1953658213;
    public static final int ottoTag = 1330926671;
    public static final int MS_PLATFORM_ID = 3;
    public static final short ENGLISH_LOCALE_ID = 1033;
    public static final int FAMILY_NAME_ID = 1;
    public static final int FULL_NAME_ID = 4;
    public static final int POSTSCRIPT_NAME_ID = 6;
    private static final short US_LCID = 1033;
    private static Map<String, Short> lcidMap;
    TTDisposerRecord disposerRecord;
    int fontIndex;
    int directoryCount;
    int directoryOffset;
    int numTables;
    DirectoryEntry[] tableDirectory;
    private boolean supportsJA;
    private boolean supportsCJK;
    private Locale nameLocale;
    private String localeFamilyName;
    private String localeFullName;
    int fontDataSize;
    private static final int TTCHEADERSIZE = 12;
    private static final int DIRECTORYHEADERSIZE = 12;
    private static final int DIRECTORYENTRYSIZE = 16;
    static final String[] encoding_mapping;
    private static final String[][] languages;
    private static final String[] codePages;
    private static String defaultCodePage;
    public static final int reserved_bits1 = Integer.MIN_VALUE;
    public static final int reserved_bits2 = 65535;
    private int fontWidth;
    private int fontWeight;
    private static final int fsSelectionItalicBit = 1;
    private static final int fsSelectionBoldBit = 32;
    private static final int fsSelectionRegularBit = 64;
    private float stSize;
    private float stPos;
    private float ulSize;
    private float ulPos;
    private char[] gaspTable;
    
    public TrueTypeFont(final String s, final Object o, final int n, final boolean b) throws FontFormatException {
        this(s, o, n, b, true);
    }
    
    public TrueTypeFont(final String s, final Object o, final int n, final boolean useJavaRasterizer, final boolean b) throws FontFormatException {
        super(s, o);
        this.disposerRecord = new TTDisposerRecord();
        this.fontIndex = 0;
        this.directoryCount = 1;
        this.fontWidth = 0;
        this.fontWeight = 0;
        this.useJavaRasterizer = useJavaRasterizer;
        this.fontRank = 3;
        try {
            this.verify(b);
            this.init(n);
            if (!b) {
                this.close();
            }
        }
        catch (final Throwable t) {
            this.close();
            if (t instanceof FontFormatException) {
                throw (FontFormatException)t;
            }
            throw new FontFormatException("Unexpected runtime exception.");
        }
        Disposer.addObjectRecord(this, this.disposerRecord);
    }
    
    @Override
    protected boolean checkUseNatives() {
        if (this.checkedNatives) {
            return this.useNatives;
        }
        if (!FontUtilities.isSolaris || this.useJavaRasterizer || FontUtilities.useT2K || this.nativeNames == null || this.getDirectoryEntry(1161972803) != null || GraphicsEnvironment.isHeadless()) {
            this.checkedNatives = true;
            return false;
        }
        if (this.nativeNames instanceof String) {
            final String s = (String)this.nativeNames;
            if (s.indexOf("8859") > 0) {
                this.checkedNatives = true;
                return false;
            }
            if (NativeFont.hasExternalBitmaps(s)) {
                this.nativeFonts = new NativeFont[1];
                try {
                    this.nativeFonts[0] = new NativeFont(s, true);
                    this.useNatives = true;
                }
                catch (final FontFormatException ex) {
                    this.nativeFonts = null;
                }
            }
        }
        else if (this.nativeNames instanceof String[]) {
            final String[] array = (String[])this.nativeNames;
            final int length = array.length;
            boolean b = false;
            for (int i = 0; i < length; ++i) {
                if (array[i].indexOf("8859") > 0) {
                    this.checkedNatives = true;
                    return false;
                }
                if (NativeFont.hasExternalBitmaps(array[i])) {
                    b = true;
                }
            }
            if (!b) {
                this.checkedNatives = true;
                return false;
            }
            this.useNatives = true;
            this.nativeFonts = new NativeFont[length];
            for (int j = 0; j < length; ++j) {
                try {
                    this.nativeFonts[j] = new NativeFont(array[j], true);
                }
                catch (final FontFormatException ex2) {
                    this.useNatives = false;
                    this.nativeFonts = null;
                }
            }
        }
        if (this.useNatives) {
            this.glyphToCharMap = new char[this.getMapper().getNumGlyphs()];
        }
        this.checkedNatives = true;
        return this.useNatives;
    }
    
    private synchronized FileChannel open() throws FontFormatException {
        return this.open(true);
    }
    
    private synchronized FileChannel open(final boolean b) throws FontFormatException {
        if (this.disposerRecord.channel == null) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().info("open TTF: " + this.platName);
            }
            try {
                this.disposerRecord.channel = AccessController.doPrivileged((PrivilegedAction<RandomAccessFile>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        try {
                            return new RandomAccessFile(TrueTypeFont.this.platName, "r");
                        }
                        catch (final FileNotFoundException ex) {
                            return null;
                        }
                    }
                }).getChannel();
                this.fileSize = (int)this.disposerRecord.channel.size();
                if (b) {
                    final FontManager instance = FontManagerFactory.getInstance();
                    if (instance instanceof SunFontManager) {
                        ((SunFontManager)instance).addToPool(this);
                    }
                }
            }
            catch (final NullPointerException ex) {
                this.close();
                throw new FontFormatException(ex.toString());
            }
            catch (final ClosedChannelException ex2) {
                Thread.interrupted();
                this.close();
                this.open();
            }
            catch (final IOException ex3) {
                this.close();
                throw new FontFormatException(ex3.toString());
            }
        }
        return this.disposerRecord.channel;
    }
    
    @Override
    protected synchronized void close() {
        this.disposerRecord.dispose();
    }
    
    int readBlock(final ByteBuffer byteBuffer, final int n, int n2) {
        int i = 0;
        try {
            synchronized (this) {
                if (this.disposerRecord.channel == null) {
                    this.open();
                }
                if (n + n2 > this.fileSize) {
                    if (n >= this.fileSize) {
                        if (FontUtilities.isLogging()) {
                            FontUtilities.getLogger().severe("Read offset is " + n + " file size is " + this.fileSize + " file is " + this.platName);
                        }
                        return -1;
                    }
                    n2 = this.fileSize - n;
                }
                byteBuffer.clear();
                this.disposerRecord.channel.position(n);
                while (i < n2) {
                    final int read = this.disposerRecord.channel.read(byteBuffer);
                    if (read == -1) {
                        String s = "Unexpected EOF " + this;
                        final int n3 = (int)this.disposerRecord.channel.size();
                        if (n3 != this.fileSize) {
                            s = s + " File size was " + this.fileSize + " and now is " + n3;
                        }
                        if (FontUtilities.isLogging()) {
                            FontUtilities.getLogger().severe(s);
                        }
                        if (i > n2 / 2 || i > 16384) {
                            byteBuffer.flip();
                            if (FontUtilities.isLogging()) {
                                s = "Returning " + i + " bytes instead of " + n2;
                                FontUtilities.getLogger().severe(s);
                            }
                        }
                        else {
                            i = -1;
                        }
                        throw new IOException(s);
                    }
                    i += read;
                }
                byteBuffer.flip();
                if (i > n2) {
                    i = n2;
                }
            }
        }
        catch (final FontFormatException ex) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().severe("While reading " + this.platName, ex);
            }
            i = -1;
            this.deregisterFontAndClearStrikeCache();
        }
        catch (final ClosedChannelException ex2) {
            Thread.interrupted();
            this.close();
            return this.readBlock(byteBuffer, n, n2);
        }
        catch (final IOException ex3) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().severe("While reading " + this.platName, ex3);
            }
            if (i == 0) {
                i = -1;
                this.deregisterFontAndClearStrikeCache();
            }
        }
        return i;
    }
    
    @Override
    ByteBuffer readBlock(final int n, final int n2) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(n2);
        try {
            synchronized (this) {
                if (this.disposerRecord.channel == null) {
                    this.open();
                }
                if (n + n2 > this.fileSize) {
                    if (n > this.fileSize) {
                        return null;
                    }
                    byteBuffer = ByteBuffer.allocate(this.fileSize - n);
                }
                this.disposerRecord.channel.position(n);
                this.disposerRecord.channel.read(byteBuffer);
                byteBuffer.flip();
            }
        }
        catch (final FontFormatException ex) {
            return null;
        }
        catch (final ClosedChannelException ex2) {
            Thread.interrupted();
            this.close();
            this.readBlock(byteBuffer, n, n2);
        }
        catch (final IOException ex3) {
            return null;
        }
        return byteBuffer;
    }
    
    byte[] readBytes(final int n, final int n2) {
        final ByteBuffer block = this.readBlock(n, n2);
        if (block.hasArray()) {
            return block.array();
        }
        final byte[] array = new byte[block.limit()];
        block.get(array);
        return array;
    }
    
    private void verify(final boolean b) throws FontFormatException {
        this.open(b);
    }
    
    protected void init(final int fontIndex) throws FontFormatException {
        int int1 = 0;
        final ByteBuffer block = this.readBlock(0, 12);
        try {
            switch (block.getInt()) {
                case 1953784678: {
                    block.getInt();
                    this.directoryCount = block.getInt();
                    if (fontIndex >= this.directoryCount) {
                        throw new FontFormatException("Bad collection index");
                    }
                    this.fontIndex = fontIndex;
                    int1 = this.readBlock(12 + 4 * fontIndex, 4).getInt();
                    this.fontDataSize = Math.max(0, this.fileSize - int1);
                    break;
                }
                case 65536:
                case 1330926671:
                case 1953658213: {
                    this.fontDataSize = this.fileSize;
                    break;
                }
                default: {
                    throw new FontFormatException("Unsupported sfnt " + this.getPublicFileName());
                }
            }
            this.numTables = this.readBlock(int1 + 4, 2).getShort();
            this.directoryOffset = int1 + 12;
            final IntBuffer intBuffer = this.readBlock(this.directoryOffset, this.numTables * 16).asIntBuffer();
            this.tableDirectory = new DirectoryEntry[this.numTables];
            for (int i = 0; i < this.numTables; ++i) {
                final DirectoryEntry directoryEntry = this.tableDirectory[i] = new DirectoryEntry();
                directoryEntry.tag = intBuffer.get();
                intBuffer.get();
                directoryEntry.offset = (intBuffer.get() & Integer.MAX_VALUE);
                directoryEntry.length = (intBuffer.get() & Integer.MAX_VALUE);
                if (directoryEntry.offset + directoryEntry.length > this.fileSize) {
                    throw new FontFormatException("bad table, tag=" + directoryEntry.tag);
                }
            }
            if (this.getDirectoryEntry(1751474532) == null) {
                throw new FontFormatException("missing head table");
            }
            if (this.getDirectoryEntry(1835104368) == null) {
                throw new FontFormatException("missing maxp table");
            }
            if (this.getDirectoryEntry(1752003704) != null && this.getDirectoryEntry(1751672161) == null) {
                throw new FontFormatException("missing hhea table");
            }
            this.initNames();
        }
        catch (final Exception ex) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().severe(ex.toString());
            }
            if (ex instanceof FontFormatException) {
                throw (FontFormatException)ex;
            }
            throw new FontFormatException(ex.toString());
        }
        if (this.familyName == null || this.fullName == null) {
            throw new FontFormatException("Font name not found");
        }
        final ByteBuffer tableBuffer = this.getTableBuffer(1330851634);
        this.setStyle(tableBuffer);
        this.setCJKSupport(tableBuffer);
    }
    
    static String getCodePage() {
        if (TrueTypeFont.defaultCodePage != null) {
            return TrueTypeFont.defaultCodePage;
        }
        if (FontUtilities.isWindows) {
            TrueTypeFont.defaultCodePage = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("file.encoding"));
        }
        else {
            if (TrueTypeFont.languages.length != TrueTypeFont.codePages.length) {
                throw new InternalError("wrong code pages array length");
            }
            final Locale startupLocale = SunToolkit.getStartupLocale();
            String s = startupLocale.getLanguage();
            if (s != null) {
                if (s.equals("zh")) {
                    final String country = startupLocale.getCountry();
                    if (country != null) {
                        s = s + "_" + country;
                    }
                }
                for (int i = 0; i < TrueTypeFont.languages.length; ++i) {
                    for (int j = 0; j < TrueTypeFont.languages[i].length; ++j) {
                        if (s.equals(TrueTypeFont.languages[i][j])) {
                            return TrueTypeFont.defaultCodePage = TrueTypeFont.codePages[i];
                        }
                    }
                }
            }
        }
        if (TrueTypeFont.defaultCodePage == null) {
            TrueTypeFont.defaultCodePage = "";
        }
        return TrueTypeFont.defaultCodePage;
    }
    
    @Override
    boolean supportsEncoding(String s) {
        if (s == null) {
            s = getCodePage();
        }
        if ("".equals(s)) {
            return false;
        }
        s = s.toLowerCase();
        if (s.equals("gb18030")) {
            s = "gbk";
        }
        else if (s.equals("ms950_hkscs")) {
            s = "ms950";
        }
        final ByteBuffer tableBuffer = this.getTableBuffer(1330851634);
        if (tableBuffer == null || tableBuffer.capacity() < 86) {
            return false;
        }
        final int int1 = tableBuffer.getInt(78);
        tableBuffer.getInt(82);
        for (int i = 0; i < TrueTypeFont.encoding_mapping.length; ++i) {
            if (TrueTypeFont.encoding_mapping[i].equals(s) && (1 << i & int1) != 0x0) {
                return true;
            }
        }
        return false;
    }
    
    private void setCJKSupport(final ByteBuffer byteBuffer) {
        if (byteBuffer == null || byteBuffer.capacity() < 50) {
            return;
        }
        final int int1 = byteBuffer.getInt(46);
        this.supportsCJK = ((int1 & 0x29BF0000) != 0x0);
        this.supportsJA = ((int1 & 0x60000) != 0x0);
    }
    
    boolean supportsJA() {
        return this.supportsJA;
    }
    
    ByteBuffer getTableBuffer(final int n) {
        DirectoryEntry directoryEntry = null;
        for (int i = 0; i < this.numTables; ++i) {
            if (this.tableDirectory[i].tag == n) {
                directoryEntry = this.tableDirectory[i];
                break;
            }
        }
        if (directoryEntry == null || directoryEntry.length == 0 || directoryEntry.offset + directoryEntry.length > this.fileSize) {
            return null;
        }
        final ByteBuffer allocate = ByteBuffer.allocate(directoryEntry.length);
        synchronized (this) {
            int read;
            try {
                if (this.disposerRecord.channel == null) {
                    this.open();
                }
                this.disposerRecord.channel.position(directoryEntry.offset);
                read = this.disposerRecord.channel.read(allocate);
                allocate.flip();
            }
            catch (final ClosedChannelException ex) {
                Thread.interrupted();
                this.close();
                return this.getTableBuffer(n);
            }
            catch (final IOException ex2) {
                return null;
            }
            catch (final FontFormatException ex3) {
                return null;
            }
            if (read < directoryEntry.length) {
                return null;
            }
            return allocate;
        }
    }
    
    @Override
    protected long getLayoutTableCache() {
        try {
            return this.getScaler().getLayoutTableCache();
        }
        catch (final FontScalerException ex) {
            return 0L;
        }
    }
    
    @Override
    protected byte[] getTableBytes(final int n) {
        final ByteBuffer tableBuffer = this.getTableBuffer(n);
        if (tableBuffer == null) {
            return null;
        }
        if (tableBuffer.hasArray()) {
            try {
                return tableBuffer.array();
            }
            catch (final Exception ex) {}
        }
        final byte[] array = new byte[this.getTableSize(n)];
        tableBuffer.get(array);
        return array;
    }
    
    int getTableSize(final int n) {
        for (int i = 0; i < this.numTables; ++i) {
            if (this.tableDirectory[i].tag == n) {
                return this.tableDirectory[i].length;
            }
        }
        return 0;
    }
    
    int getTableOffset(final int n) {
        for (int i = 0; i < this.numTables; ++i) {
            if (this.tableDirectory[i].tag == n) {
                return this.tableDirectory[i].offset;
            }
        }
        return 0;
    }
    
    DirectoryEntry getDirectoryEntry(final int n) {
        for (int i = 0; i < this.numTables; ++i) {
            if (this.tableDirectory[i].tag == n) {
                return this.tableDirectory[i];
            }
        }
        return null;
    }
    
    boolean useEmbeddedBitmapsForSize(final int n) {
        if (!this.supportsCJK) {
            return false;
        }
        if (this.getDirectoryEntry(1161972803) == null) {
            return false;
        }
        final ByteBuffer tableBuffer = this.getTableBuffer(1161972803);
        for (int int1 = tableBuffer.getInt(4), i = 0; i < int1; ++i) {
            if ((tableBuffer.get(8 + i * 48 + 45) & 0xFF) == n) {
                return true;
            }
        }
        return false;
    }
    
    public String getFullName() {
        return this.fullName;
    }
    
    @Override
    protected void setStyle() {
        this.setStyle(this.getTableBuffer(1330851634));
    }
    
    @Override
    public int getWidth() {
        return (this.fontWidth > 0) ? this.fontWidth : super.getWidth();
    }
    
    @Override
    public int getWeight() {
        return (this.fontWeight > 0) ? this.fontWeight : super.getWeight();
    }
    
    private void setStyle(final ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return;
        }
        if (byteBuffer.capacity() >= 8) {
            this.fontWeight = (byteBuffer.getChar(4) & '\uffff');
            this.fontWidth = (byteBuffer.getChar(6) & '\uffff');
        }
        if (byteBuffer.capacity() < 64) {
            super.setStyle();
            return;
        }
        final int n = byteBuffer.getChar(62) & '\uffff';
        final int n2 = n & 0x1;
        final int n3 = n & 0x20;
        final int n4 = n & 0x40;
        if (n4 != 0 && (n2 | n3) != 0x0) {
            super.setStyle();
            return;
        }
        if ((n4 | n2 | n3) == 0x0) {
            super.setStyle();
            return;
        }
        switch (n3 | n2) {
            case 1: {
                this.style = 2;
                break;
            }
            case 32: {
                if (FontUtilities.isSolaris && this.platName.endsWith("HG-GothicB.ttf")) {
                    this.style = 0;
                    break;
                }
                this.style = 1;
                break;
            }
            case 33: {
                this.style = 3;
                break;
            }
        }
    }
    
    private void setStrikethroughMetrics(final ByteBuffer byteBuffer, final int n) {
        if (byteBuffer == null || byteBuffer.capacity() < 30 || n < 0) {
            this.stSize = 0.05f;
            this.stPos = -0.4f;
            return;
        }
        final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        this.stSize = shortBuffer.get(13) / (float)n;
        this.stPos = -shortBuffer.get(14) / (float)n;
    }
    
    private void setUnderlineMetrics(final ByteBuffer byteBuffer, final int n) {
        if (byteBuffer == null || byteBuffer.capacity() < 12 || n < 0) {
            this.ulSize = 0.05f;
            this.ulPos = 0.1f;
            return;
        }
        final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        this.ulSize = shortBuffer.get(5) / (float)n;
        this.ulPos = -shortBuffer.get(4) / (float)n;
    }
    
    @Override
    public void getStyleMetrics(final float n, final float[] array, final int n2) {
        if (this.ulSize == 0.0f && this.ulPos == 0.0f) {
            final ByteBuffer tableBuffer = this.getTableBuffer(1751474532);
            int n3 = -1;
            if (tableBuffer != null && tableBuffer.capacity() >= 18) {
                n3 = (tableBuffer.asShortBuffer().get(9) & 0xFFFF);
                if (n3 < 16 || n3 > 16384) {
                    n3 = 2048;
                }
            }
            this.setStrikethroughMetrics(this.getTableBuffer(1330851634), n3);
            this.setUnderlineMetrics(this.getTableBuffer(1886352244), n3);
        }
        array[n2] = this.stPos * n;
        array[n2 + 1] = this.stSize * n;
        array[n2 + 2] = this.ulPos * n;
        array[n2 + 3] = this.ulSize * n;
    }
    
    private String makeString(byte[] array, int n, final short n2) {
        if (n2 >= 2 && n2 <= 6) {
            final byte[] array2 = array;
            final int n3 = n;
            array = new byte[n3];
            n = 0;
            for (int i = 0; i < n3; ++i) {
                if (array2[i] != 0) {
                    array[n++] = array2[i];
                }
            }
        }
        String s = null;
        switch (n2) {
            case 1: {
                s = "UTF-16";
                break;
            }
            case 0: {
                s = "UTF-16";
                break;
            }
            case 2: {
                s = "SJIS";
                break;
            }
            case 3: {
                s = "GBK";
                break;
            }
            case 4: {
                s = "MS950";
                break;
            }
            case 5: {
                s = "EUC_KR";
                break;
            }
            case 6: {
                s = "Johab";
                break;
            }
            default: {
                s = "UTF-16";
                break;
            }
        }
        try {
            return new String(array, 0, n, s);
        }
        catch (final UnsupportedEncodingException ex) {
            if (FontUtilities.isLogging()) {
                FontUtilities.getLogger().warning(ex + " EncodingID=" + n2);
            }
            return new String(array, 0, n);
        }
        catch (final Throwable t) {
            return null;
        }
    }
    
    protected void initNames() {
        final byte[] array = new byte[256];
        final ByteBuffer tableBuffer = this.getTableBuffer(1851878757);
        if (tableBuffer != null) {
            final ShortBuffer shortBuffer = tableBuffer.asShortBuffer();
            shortBuffer.get();
            final short value = shortBuffer.get();
            final int n = shortBuffer.get() & 0xFFFF;
            this.nameLocale = SunToolkit.getStartupLocale();
            final short lcidFromLocale = getLCIDFromLocale(this.nameLocale);
            for (short n2 = 0; n2 < value; ++n2) {
                if (shortBuffer.get() != 3) {
                    shortBuffer.position(shortBuffer.position() + 5);
                }
                else {
                    final short value2 = shortBuffer.get();
                    final short value3 = shortBuffer.get();
                    final short value4 = shortBuffer.get();
                    final int n3 = shortBuffer.get() & 0xFFFF;
                    final int n4 = (shortBuffer.get() & 0xFFFF) + n;
                    switch (value4) {
                        case 1: {
                            if (this.familyName != null && value3 != 1033 && value3 != lcidFromLocale) {
                                break;
                            }
                            tableBuffer.position(n4);
                            tableBuffer.get(array, 0, n3);
                            final String string = this.makeString(array, n3, value2);
                            if (this.familyName == null || value3 == 1033) {
                                this.familyName = string;
                            }
                            if (value3 == lcidFromLocale) {
                                this.localeFamilyName = string;
                                break;
                            }
                            break;
                        }
                        case 4: {
                            if (this.fullName != null && value3 != 1033 && value3 != lcidFromLocale) {
                                break;
                            }
                            tableBuffer.position(n4);
                            tableBuffer.get(array, 0, n3);
                            final String string2 = this.makeString(array, n3, value2);
                            if (this.fullName == null || value3 == 1033) {
                                this.fullName = string2;
                            }
                            if (value3 == lcidFromLocale) {
                                this.localeFullName = string2;
                                break;
                            }
                            break;
                        }
                    }
                }
            }
            if (this.localeFamilyName == null) {
                this.localeFamilyName = this.familyName;
            }
            if (this.localeFullName == null) {
                this.localeFullName = this.fullName;
            }
        }
    }
    
    protected String lookupName(final short n, final int n2) {
        String string = null;
        final byte[] array = new byte[1024];
        final ByteBuffer tableBuffer = this.getTableBuffer(1851878757);
        if (tableBuffer != null) {
            final ShortBuffer shortBuffer = tableBuffer.asShortBuffer();
            shortBuffer.get();
            final short value = shortBuffer.get();
            final int n3 = shortBuffer.get() & 0xFFFF;
            for (short n4 = 0; n4 < value; ++n4) {
                if (shortBuffer.get() != 3) {
                    shortBuffer.position(shortBuffer.position() + 5);
                }
                else {
                    final short value2 = shortBuffer.get();
                    final short value3 = shortBuffer.get();
                    final short value4 = shortBuffer.get();
                    final int n5 = shortBuffer.get() & 0xFFFF;
                    final int n6 = (shortBuffer.get() & 0xFFFF) + n3;
                    if (value4 == n2 && ((string == null && value3 == 1033) || value3 == n)) {
                        tableBuffer.position(n6);
                        tableBuffer.get(array, 0, n5);
                        string = this.makeString(array, n5, value2);
                        if (value3 == n) {
                            return string;
                        }
                    }
                }
            }
        }
        return string;
    }
    
    public int getFontCount() {
        return this.directoryCount;
    }
    
    @Override
    protected synchronized FontScaler getScaler() {
        if (this.scaler == null) {
            this.scaler = FontScaler.getScaler(this, this.fontIndex, this.supportsCJK, this.fileSize);
        }
        return this.scaler;
    }
    
    @Override
    public String getPostscriptName() {
        final String lookupName = this.lookupName((short)1033, 6);
        if (lookupName == null) {
            return this.fullName;
        }
        return lookupName;
    }
    
    @Override
    public String getFontName(final Locale locale) {
        if (locale == null) {
            return this.fullName;
        }
        if (locale.equals(this.nameLocale) && this.localeFullName != null) {
            return this.localeFullName;
        }
        final String lookupName = this.lookupName(getLCIDFromLocale(locale), 4);
        if (lookupName == null) {
            return this.fullName;
        }
        return lookupName;
    }
    
    private static void addLCIDMapEntry(final Map<String, Short> map, final String s, final short n) {
        map.put(s, n);
    }
    
    private static synchronized void createLCIDMap() {
        if (TrueTypeFont.lcidMap != null) {
            return;
        }
        final HashMap lcidMap = new HashMap(200);
        addLCIDMapEntry(lcidMap, "ar", (short)1025);
        addLCIDMapEntry(lcidMap, "bg", (short)1026);
        addLCIDMapEntry(lcidMap, "ca", (short)1027);
        addLCIDMapEntry(lcidMap, "zh", (short)1028);
        addLCIDMapEntry(lcidMap, "cs", (short)1029);
        addLCIDMapEntry(lcidMap, "da", (short)1030);
        addLCIDMapEntry(lcidMap, "de", (short)1031);
        addLCIDMapEntry(lcidMap, "el", (short)1032);
        addLCIDMapEntry(lcidMap, "es", (short)1034);
        addLCIDMapEntry(lcidMap, "fi", (short)1035);
        addLCIDMapEntry(lcidMap, "fr", (short)1036);
        addLCIDMapEntry(lcidMap, "iw", (short)1037);
        addLCIDMapEntry(lcidMap, "hu", (short)1038);
        addLCIDMapEntry(lcidMap, "is", (short)1039);
        addLCIDMapEntry(lcidMap, "it", (short)1040);
        addLCIDMapEntry(lcidMap, "ja", (short)1041);
        addLCIDMapEntry(lcidMap, "ko", (short)1042);
        addLCIDMapEntry(lcidMap, "nl", (short)1043);
        addLCIDMapEntry(lcidMap, "no", (short)1044);
        addLCIDMapEntry(lcidMap, "pl", (short)1045);
        addLCIDMapEntry(lcidMap, "pt", (short)1046);
        addLCIDMapEntry(lcidMap, "rm", (short)1047);
        addLCIDMapEntry(lcidMap, "ro", (short)1048);
        addLCIDMapEntry(lcidMap, "ru", (short)1049);
        addLCIDMapEntry(lcidMap, "hr", (short)1050);
        addLCIDMapEntry(lcidMap, "sk", (short)1051);
        addLCIDMapEntry(lcidMap, "sq", (short)1052);
        addLCIDMapEntry(lcidMap, "sv", (short)1053);
        addLCIDMapEntry(lcidMap, "th", (short)1054);
        addLCIDMapEntry(lcidMap, "tr", (short)1055);
        addLCIDMapEntry(lcidMap, "ur", (short)1056);
        addLCIDMapEntry(lcidMap, "in", (short)1057);
        addLCIDMapEntry(lcidMap, "uk", (short)1058);
        addLCIDMapEntry(lcidMap, "be", (short)1059);
        addLCIDMapEntry(lcidMap, "sl", (short)1060);
        addLCIDMapEntry(lcidMap, "et", (short)1061);
        addLCIDMapEntry(lcidMap, "lv", (short)1062);
        addLCIDMapEntry(lcidMap, "lt", (short)1063);
        addLCIDMapEntry(lcidMap, "fa", (short)1065);
        addLCIDMapEntry(lcidMap, "vi", (short)1066);
        addLCIDMapEntry(lcidMap, "hy", (short)1067);
        addLCIDMapEntry(lcidMap, "eu", (short)1069);
        addLCIDMapEntry(lcidMap, "mk", (short)1071);
        addLCIDMapEntry(lcidMap, "tn", (short)1074);
        addLCIDMapEntry(lcidMap, "xh", (short)1076);
        addLCIDMapEntry(lcidMap, "zu", (short)1077);
        addLCIDMapEntry(lcidMap, "af", (short)1078);
        addLCIDMapEntry(lcidMap, "ka", (short)1079);
        addLCIDMapEntry(lcidMap, "fo", (short)1080);
        addLCIDMapEntry(lcidMap, "hi", (short)1081);
        addLCIDMapEntry(lcidMap, "mt", (short)1082);
        addLCIDMapEntry(lcidMap, "se", (short)1083);
        addLCIDMapEntry(lcidMap, "gd", (short)1084);
        addLCIDMapEntry(lcidMap, "ms", (short)1086);
        addLCIDMapEntry(lcidMap, "kk", (short)1087);
        addLCIDMapEntry(lcidMap, "ky", (short)1088);
        addLCIDMapEntry(lcidMap, "sw", (short)1089);
        addLCIDMapEntry(lcidMap, "tt", (short)1092);
        addLCIDMapEntry(lcidMap, "bn", (short)1093);
        addLCIDMapEntry(lcidMap, "pa", (short)1094);
        addLCIDMapEntry(lcidMap, "gu", (short)1095);
        addLCIDMapEntry(lcidMap, "ta", (short)1097);
        addLCIDMapEntry(lcidMap, "te", (short)1098);
        addLCIDMapEntry(lcidMap, "kn", (short)1099);
        addLCIDMapEntry(lcidMap, "ml", (short)1100);
        addLCIDMapEntry(lcidMap, "mr", (short)1102);
        addLCIDMapEntry(lcidMap, "sa", (short)1103);
        addLCIDMapEntry(lcidMap, "mn", (short)1104);
        addLCIDMapEntry(lcidMap, "cy", (short)1106);
        addLCIDMapEntry(lcidMap, "gl", (short)1110);
        addLCIDMapEntry(lcidMap, "dv", (short)1125);
        addLCIDMapEntry(lcidMap, "qu", (short)1131);
        addLCIDMapEntry(lcidMap, "mi", (short)1153);
        addLCIDMapEntry(lcidMap, "ar_IQ", (short)2049);
        addLCIDMapEntry(lcidMap, "zh_CN", (short)2052);
        addLCIDMapEntry(lcidMap, "de_CH", (short)2055);
        addLCIDMapEntry(lcidMap, "en_GB", (short)2057);
        addLCIDMapEntry(lcidMap, "es_MX", (short)2058);
        addLCIDMapEntry(lcidMap, "fr_BE", (short)2060);
        addLCIDMapEntry(lcidMap, "it_CH", (short)2064);
        addLCIDMapEntry(lcidMap, "nl_BE", (short)2067);
        addLCIDMapEntry(lcidMap, "no_NO_NY", (short)2068);
        addLCIDMapEntry(lcidMap, "pt_PT", (short)2070);
        addLCIDMapEntry(lcidMap, "ro_MD", (short)2072);
        addLCIDMapEntry(lcidMap, "ru_MD", (short)2073);
        addLCIDMapEntry(lcidMap, "sr_CS", (short)2074);
        addLCIDMapEntry(lcidMap, "sv_FI", (short)2077);
        addLCIDMapEntry(lcidMap, "az_AZ", (short)2092);
        addLCIDMapEntry(lcidMap, "se_SE", (short)2107);
        addLCIDMapEntry(lcidMap, "ga_IE", (short)2108);
        addLCIDMapEntry(lcidMap, "ms_BN", (short)2110);
        addLCIDMapEntry(lcidMap, "uz_UZ", (short)2115);
        addLCIDMapEntry(lcidMap, "qu_EC", (short)2155);
        addLCIDMapEntry(lcidMap, "ar_EG", (short)3073);
        addLCIDMapEntry(lcidMap, "zh_HK", (short)3076);
        addLCIDMapEntry(lcidMap, "de_AT", (short)3079);
        addLCIDMapEntry(lcidMap, "en_AU", (short)3081);
        addLCIDMapEntry(lcidMap, "fr_CA", (short)3084);
        addLCIDMapEntry(lcidMap, "sr_CS", (short)3098);
        addLCIDMapEntry(lcidMap, "se_FI", (short)3131);
        addLCIDMapEntry(lcidMap, "qu_PE", (short)3179);
        addLCIDMapEntry(lcidMap, "ar_LY", (short)4097);
        addLCIDMapEntry(lcidMap, "zh_SG", (short)4100);
        addLCIDMapEntry(lcidMap, "de_LU", (short)4103);
        addLCIDMapEntry(lcidMap, "en_CA", (short)4105);
        addLCIDMapEntry(lcidMap, "es_GT", (short)4106);
        addLCIDMapEntry(lcidMap, "fr_CH", (short)4108);
        addLCIDMapEntry(lcidMap, "hr_BA", (short)4122);
        addLCIDMapEntry(lcidMap, "ar_DZ", (short)5121);
        addLCIDMapEntry(lcidMap, "zh_MO", (short)5124);
        addLCIDMapEntry(lcidMap, "de_LI", (short)5127);
        addLCIDMapEntry(lcidMap, "en_NZ", (short)5129);
        addLCIDMapEntry(lcidMap, "es_CR", (short)5130);
        addLCIDMapEntry(lcidMap, "fr_LU", (short)5132);
        addLCIDMapEntry(lcidMap, "bs_BA", (short)5146);
        addLCIDMapEntry(lcidMap, "ar_MA", (short)6145);
        addLCIDMapEntry(lcidMap, "en_IE", (short)6153);
        addLCIDMapEntry(lcidMap, "es_PA", (short)6154);
        addLCIDMapEntry(lcidMap, "fr_MC", (short)6156);
        addLCIDMapEntry(lcidMap, "sr_BA", (short)6170);
        addLCIDMapEntry(lcidMap, "ar_TN", (short)7169);
        addLCIDMapEntry(lcidMap, "en_ZA", (short)7177);
        addLCIDMapEntry(lcidMap, "es_DO", (short)7178);
        addLCIDMapEntry(lcidMap, "sr_BA", (short)7194);
        addLCIDMapEntry(lcidMap, "ar_OM", (short)8193);
        addLCIDMapEntry(lcidMap, "en_JM", (short)8201);
        addLCIDMapEntry(lcidMap, "es_VE", (short)8202);
        addLCIDMapEntry(lcidMap, "ar_YE", (short)9217);
        addLCIDMapEntry(lcidMap, "es_CO", (short)9226);
        addLCIDMapEntry(lcidMap, "ar_SY", (short)10241);
        addLCIDMapEntry(lcidMap, "en_BZ", (short)10249);
        addLCIDMapEntry(lcidMap, "es_PE", (short)10250);
        addLCIDMapEntry(lcidMap, "ar_JO", (short)11265);
        addLCIDMapEntry(lcidMap, "en_TT", (short)11273);
        addLCIDMapEntry(lcidMap, "es_AR", (short)11274);
        addLCIDMapEntry(lcidMap, "ar_LB", (short)12289);
        addLCIDMapEntry(lcidMap, "en_ZW", (short)12297);
        addLCIDMapEntry(lcidMap, "es_EC", (short)12298);
        addLCIDMapEntry(lcidMap, "ar_KW", (short)13313);
        addLCIDMapEntry(lcidMap, "en_PH", (short)13321);
        addLCIDMapEntry(lcidMap, "es_CL", (short)13322);
        addLCIDMapEntry(lcidMap, "ar_AE", (short)14337);
        addLCIDMapEntry(lcidMap, "es_UY", (short)14346);
        addLCIDMapEntry(lcidMap, "ar_BH", (short)15361);
        addLCIDMapEntry(lcidMap, "es_PY", (short)15370);
        addLCIDMapEntry(lcidMap, "ar_QA", (short)16385);
        addLCIDMapEntry(lcidMap, "es_BO", (short)16394);
        addLCIDMapEntry(lcidMap, "es_SV", (short)17418);
        addLCIDMapEntry(lcidMap, "es_HN", (short)18442);
        addLCIDMapEntry(lcidMap, "es_NI", (short)19466);
        addLCIDMapEntry(lcidMap, "es_PR", (short)20490);
        TrueTypeFont.lcidMap = lcidMap;
    }
    
    private static short getLCIDFromLocale(final Locale locale) {
        if (locale.equals(Locale.US)) {
            return 1033;
        }
        if (TrueTypeFont.lcidMap == null) {
            createLCIDMap();
        }
        int lastIndex;
        for (String s = locale.toString(); !"".equals(s); s = s.substring(0, lastIndex)) {
            final Short n = TrueTypeFont.lcidMap.get(s);
            if (n != null) {
                return n;
            }
            lastIndex = s.lastIndexOf(95);
            if (lastIndex < 1) {
                return 1033;
            }
        }
        return 1033;
    }
    
    @Override
    public String getFamilyName(final Locale locale) {
        if (locale == null) {
            return this.familyName;
        }
        if (locale.equals(this.nameLocale) && this.localeFamilyName != null) {
            return this.localeFamilyName;
        }
        final String lookupName = this.lookupName(getLCIDFromLocale(locale), 1);
        if (lookupName == null) {
            return this.familyName;
        }
        return lookupName;
    }
    
    public CharToGlyphMapper getMapper() {
        if (this.mapper == null) {
            this.mapper = new TrueTypeGlyphMapper(this);
        }
        return this.mapper;
    }
    
    protected void initAllNames(final int n, final HashSet set) {
        final byte[] array = new byte[256];
        final ByteBuffer tableBuffer = this.getTableBuffer(1851878757);
        if (tableBuffer != null) {
            final ShortBuffer shortBuffer = tableBuffer.asShortBuffer();
            shortBuffer.get();
            final short value = shortBuffer.get();
            final int n2 = shortBuffer.get() & 0xFFFF;
            for (short n3 = 0; n3 < value; ++n3) {
                if (shortBuffer.get() != 3) {
                    shortBuffer.position(shortBuffer.position() + 5);
                }
                else {
                    final short value2 = shortBuffer.get();
                    shortBuffer.get();
                    final short value3 = shortBuffer.get();
                    final int n4 = shortBuffer.get() & 0xFFFF;
                    final int n5 = (shortBuffer.get() & 0xFFFF) + n2;
                    if (value3 == n) {
                        tableBuffer.position(n5);
                        tableBuffer.get(array, 0, n4);
                        set.add(this.makeString(array, n4, value2));
                    }
                }
            }
        }
    }
    
    String[] getAllFamilyNames() {
        final HashSet set = new HashSet();
        try {
            this.initAllNames(1, set);
        }
        catch (final Exception ex) {}
        return (String[])set.toArray(new String[0]);
    }
    
    String[] getAllFullNames() {
        final HashSet set = new HashSet();
        try {
            this.initAllNames(4, set);
        }
        catch (final Exception ex) {}
        return (String[])set.toArray(new String[0]);
    }
    
    @Override
    Point2D.Float getGlyphPoint(final long n, final int n2, final int n3) {
        try {
            return this.getScaler().getGlyphPoint(n, n2, n3);
        }
        catch (final FontScalerException ex) {
            return null;
        }
    }
    
    private char[] getGaspTable() {
        if (this.gaspTable != null) {
            return this.gaspTable;
        }
        final ByteBuffer tableBuffer = this.getTableBuffer(1734439792);
        if (tableBuffer == null) {
            return this.gaspTable = new char[0];
        }
        final CharBuffer charBuffer = tableBuffer.asCharBuffer();
        if (charBuffer.get() > '\u0001') {
            return this.gaspTable = new char[0];
        }
        final char value = charBuffer.get();
        if (4 + value * '\u0004' > this.getTableSize(1734439792)) {
            return this.gaspTable = new char[0];
        }
        charBuffer.get(this.gaspTable = new char['\u0002' * value]);
        return this.gaspTable;
    }
    
    @Override
    public boolean useAAForPtSize(final int n) {
        final char[] gaspTable = this.getGaspTable();
        if (gaspTable.length > 0) {
            for (int i = 0; i < gaspTable.length; i += 2) {
                if (n <= gaspTable[i]) {
                    return (gaspTable[i + 1] & '\u0002') != 0x0;
                }
            }
            return true;
        }
        return this.style == 1 || n <= 8 || n >= 18;
    }
    
    @Override
    public boolean hasSupplementaryChars() {
        return ((TrueTypeGlyphMapper)this.getMapper()).hasSupplementaryChars();
    }
    
    @Override
    public String toString() {
        return "** TrueType Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + " fileName=" + this.getPublicFileName();
    }
    
    static {
        encoding_mapping = new String[] { "cp1252", "cp1250", "cp1251", "cp1253", "cp1254", "cp1255", "cp1256", "cp1257", "", "", "", "", "", "", "", "", "ms874", "ms932", "gbk", "ms949", "ms950", "ms1361", "", "", "", "", "", "", "", "", "", "" };
        languages = new String[][] { { "en", "ca", "da", "de", "es", "fi", "fr", "is", "it", "nl", "no", "pt", "sq", "sv" }, { "cs", "cz", "et", "hr", "hu", "nr", "pl", "ro", "sk", "sl", "sq", "sr" }, { "bg", "mk", "ru", "sh", "uk" }, { "el" }, { "tr" }, { "he" }, { "ar" }, { "et", "lt", "lv" }, { "th" }, { "ja" }, { "zh", "zh_CN" }, { "ko" }, { "zh_HK", "zh_TW" }, { "ko" } };
        codePages = new String[] { "cp1252", "cp1250", "cp1251", "cp1253", "cp1254", "cp1255", "cp1256", "cp1257", "ms874", "ms932", "gbk", "ms949", "ms950", "ms1361" };
        TrueTypeFont.defaultCodePage = null;
    }
    
    static class DirectoryEntry
    {
        int tag;
        int offset;
        int length;
    }
    
    private static class TTDisposerRecord implements DisposerRecord
    {
        FileChannel channel;
        
        private TTDisposerRecord() {
            this.channel = null;
        }
        
        @Override
        public synchronized void dispose() {
            try {
                if (this.channel != null) {
                    this.channel.close();
                }
            }
            catch (final IOException ex) {}
            finally {
                this.channel = null;
            }
        }
    }
}
