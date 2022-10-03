package sun.font;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteOrder;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.io.FileNotFoundException;
import java.security.PrivilegedAction;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.ByteBuffer;
import sun.java2d.DisposerRecord;
import sun.java2d.Disposer;
import java.awt.FontFormatException;
import java.util.HashSet;
import java.util.HashMap;
import java.lang.ref.WeakReference;

public class Type1Font extends FileFont
{
    WeakReference bufferRef;
    private String psName;
    private static HashMap styleAbbreviationsMapping;
    private static HashSet styleNameTokes;
    private static final int PSEOFTOKEN = 0;
    private static final int PSNAMETOKEN = 1;
    private static final int PSSTRINGTOKEN = 2;
    
    public Type1Font(final String s, final Object o) throws FontFormatException {
        this(s, o, false);
    }
    
    public Type1Font(final String s, final Object o, final boolean b) throws FontFormatException {
        super(s, o);
        this.bufferRef = new WeakReference(null);
        this.psName = null;
        this.fontRank = 4;
        this.checkedNatives = true;
        try {
            this.verify();
        }
        catch (final Throwable t) {
            if (b) {
                Disposer.addObjectRecord(this.bufferRef, new T1DisposerRecord(s));
                this.bufferRef = null;
            }
            if (t instanceof FontFormatException) {
                throw (FontFormatException)t;
            }
            throw new FontFormatException("Unexpected runtime exception.");
        }
    }
    
    private synchronized ByteBuffer getBuffer() throws FontFormatException {
        MappedByteBuffer map = (MappedByteBuffer)this.bufferRef.get();
        if (map == null) {
            try {
                final FileChannel channel = AccessController.doPrivileged((PrivilegedAction<RandomAccessFile>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        try {
                            return new RandomAccessFile(Type1Font.this.platName, "r");
                        }
                        catch (final FileNotFoundException ex) {
                            return null;
                        }
                    }
                }).getChannel();
                this.fileSize = (int)channel.size();
                map = channel.map(FileChannel.MapMode.READ_ONLY, 0L, this.fileSize);
                map.position(0);
                this.bufferRef = new WeakReference(map);
                channel.close();
            }
            catch (final NullPointerException ex) {
                throw new FontFormatException(ex.toString());
            }
            catch (final ClosedChannelException ex2) {
                Thread.interrupted();
                return this.getBuffer();
            }
            catch (final IOException ex3) {
                throw new FontFormatException(ex3.toString());
            }
        }
        return map;
    }
    
    @Override
    protected void close() {
    }
    
    void readFile(final ByteBuffer byteBuffer) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = AccessController.doPrivileged((PrivilegedAction<RandomAccessFile>)new PrivilegedAction() {
                @Override
                public Object run() {
                    try {
                        return new RandomAccessFile(Type1Font.this.platName, "r");
                    }
                    catch (final FileNotFoundException ex) {
                        return null;
                    }
                }
            });
            final FileChannel channel = randomAccessFile.getChannel();
            while (byteBuffer.remaining() > 0 && channel.read(byteBuffer) != -1) {}
        }
        catch (final NullPointerException ex) {}
        catch (final ClosedChannelException ex2) {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                    randomAccessFile = null;
                }
            }
            catch (final IOException ex3) {}
            Thread.interrupted();
            this.readFile(byteBuffer);
        }
        catch (final IOException ex4) {}
        finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                }
                catch (final IOException ex5) {}
            }
        }
    }
    
    public synchronized ByteBuffer readBlock(int fileSize, final int n) {
        try {
            final ByteBuffer buffer = this.getBuffer();
            if (fileSize > this.fileSize) {
                fileSize = this.fileSize;
            }
            buffer.position(fileSize);
            return buffer.slice();
        }
        catch (final FontFormatException ex) {
            return null;
        }
    }
    
    private void verify() throws FontFormatException {
        final ByteBuffer buffer = this.getBuffer();
        if (buffer.capacity() < 6) {
            throw new FontFormatException("short file");
        }
        final int n = buffer.get(0) & 0xFF;
        if ((buffer.get(0) & 0xFF) == 0x80) {
            this.verifyPFB(buffer);
            buffer.position(6);
        }
        else {
            this.verifyPFA(buffer);
            buffer.position(0);
        }
        this.initNames(buffer);
        if (this.familyName == null || this.fullName == null) {
            throw new FontFormatException("Font name not found");
        }
        this.setStyle();
    }
    
    public int getFileSize() {
        if (this.fileSize == 0) {
            try {
                this.getBuffer();
            }
            catch (final FontFormatException ex) {}
        }
        return this.fileSize;
    }
    
    private void verifyPFA(final ByteBuffer byteBuffer) throws FontFormatException {
        if (byteBuffer.getShort() != 9505) {
            throw new FontFormatException("bad pfa font");
        }
    }
    
    private void verifyPFB(final ByteBuffer byteBuffer) throws FontFormatException {
        int n = 0;
        try {
            while (true) {
                final int n2 = byteBuffer.getShort(n) & 0xFFFF;
                if (n2 == 32769 || n2 == 32770) {
                    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    final int int1 = byteBuffer.getInt(n + 2);
                    byteBuffer.order(ByteOrder.BIG_ENDIAN);
                    if (int1 <= 0) {
                        throw new FontFormatException("bad segment length");
                    }
                    n += int1 + 6;
                }
                else {
                    if (n2 == 32771) {
                        return;
                    }
                    throw new FontFormatException("bad pfb file");
                }
            }
        }
        catch (final BufferUnderflowException ex) {
            throw new FontFormatException(ex.toString());
        }
        catch (final Exception ex2) {
            throw new FontFormatException(ex2.toString());
        }
    }
    
    private void initNames(final ByteBuffer byteBuffer) throws FontFormatException {
        int n = 0;
        Object o = null;
        try {
            while ((this.fullName == null || this.familyName == null || this.psName == null || o == null) && n == 0) {
                final int nextTokenType = this.nextTokenType(byteBuffer);
                if (nextTokenType == 1) {
                    if (byteBuffer.get(byteBuffer.position()) == 70) {
                        final String simpleToken = this.getSimpleToken(byteBuffer);
                        if ("FullName".equals(simpleToken)) {
                            if (this.nextTokenType(byteBuffer) != 2) {
                                continue;
                            }
                            this.fullName = this.getString(byteBuffer);
                        }
                        else if ("FamilyName".equals(simpleToken)) {
                            if (this.nextTokenType(byteBuffer) != 2) {
                                continue;
                            }
                            this.familyName = this.getString(byteBuffer);
                        }
                        else if ("FontName".equals(simpleToken)) {
                            if (this.nextTokenType(byteBuffer) != 1) {
                                continue;
                            }
                            this.psName = this.getSimpleToken(byteBuffer);
                        }
                        else {
                            if (!"FontType".equals(simpleToken)) {
                                continue;
                            }
                            final String simpleToken2 = this.getSimpleToken(byteBuffer);
                            if (!"def".equals(this.getSimpleToken(byteBuffer))) {
                                continue;
                            }
                            o = simpleToken2;
                        }
                    }
                    else {
                        while (byteBuffer.get() > 32) {}
                    }
                }
                else {
                    if (nextTokenType != 0) {
                        continue;
                    }
                    n = 1;
                }
            }
        }
        catch (final Exception ex) {
            throw new FontFormatException(ex.toString());
        }
        if (!"1".equals(o)) {
            throw new FontFormatException("Unsupported font type");
        }
        if (this.psName == null) {
            byteBuffer.position(0);
            if (byteBuffer.getShort() != 9505) {
                byteBuffer.position(8);
            }
            final String simpleToken3 = this.getSimpleToken(byteBuffer);
            if (!simpleToken3.startsWith("FontType1-") && !simpleToken3.startsWith("PS-AdobeFont-")) {
                throw new FontFormatException("Unsupported font format [" + simpleToken3 + "]");
            }
            this.psName = this.getSimpleToken(byteBuffer);
        }
        if (n != 0) {
            if (this.fullName != null) {
                this.familyName = this.fullName2FamilyName(this.fullName);
            }
            else if (this.familyName != null) {
                this.fullName = this.familyName;
            }
            else {
                this.fullName = this.psName2FullName(this.psName);
                this.familyName = this.psName2FamilyName(this.psName);
            }
        }
    }
    
    private String fullName2FamilyName(final String s) {
        int n;
        for (int i = s.length(); i > 0; i = n) {
            for (n = i - 1; n > 0 && s.charAt(n) != ' '; --n) {}
            if (!this.isStyleToken(s.substring(n + 1, i))) {
                return s.substring(0, i);
            }
        }
        return s;
    }
    
    private String expandAbbreviation(final String s) {
        if (Type1Font.styleAbbreviationsMapping.containsKey(s)) {
            return Type1Font.styleAbbreviationsMapping.get(s);
        }
        return s;
    }
    
    private boolean isStyleToken(final String s) {
        return Type1Font.styleNameTokes.contains(s);
    }
    
    private String psName2FullName(final String s) {
        final int index = s.indexOf("-");
        String s2;
        if (index >= 0) {
            s2 = this.expandName(s.substring(0, index), false) + " " + this.expandName(s.substring(index + 1), true);
        }
        else {
            s2 = this.expandName(s, false);
        }
        return s2;
    }
    
    private String psName2FamilyName(final String s) {
        String substring = s;
        if (substring.indexOf("-") > 0) {
            substring = substring.substring(0, substring.indexOf("-"));
        }
        return this.expandName(substring, false);
    }
    
    private int nextCapitalLetter(final String s, int n) {
        while (n >= 0 && n < s.length()) {
            if (s.charAt(n) >= 'A' && s.charAt(n) <= 'Z') {
                return n;
            }
            ++n;
        }
        return -1;
    }
    
    private String expandName(final String s, final boolean b) {
        final StringBuffer sb = new StringBuffer(s.length() + 10);
        int n;
        for (int i = 0; i < s.length(); i = n) {
            n = this.nextCapitalLetter(s, i + 1);
            if (n < 0) {
                n = s.length();
            }
            if (i != 0) {
                sb.append(" ");
            }
            if (b) {
                sb.append(this.expandAbbreviation(s.substring(i, n)));
            }
            else {
                sb.append(s.substring(i, n));
            }
        }
        return sb.toString();
    }
    
    private byte skip(final ByteBuffer byteBuffer) {
        byte b = byteBuffer.get();
        while (b == 37) {
            do {
                b = byteBuffer.get();
                if (b != 13) {
                    continue;
                }
                break;
            } while (b != 10);
        }
        while (b <= 32) {
            b = byteBuffer.get();
        }
        return b;
    }
    
    private int nextTokenType(final ByteBuffer byteBuffer) {
        try {
            byte b = this.skip(byteBuffer);
            while (b != 47) {
                if (b == 40) {
                    return 2;
                }
                if (b == 13 || b == 10) {
                    b = this.skip(byteBuffer);
                }
                else {
                    b = byteBuffer.get();
                }
            }
            return 1;
        }
        catch (final BufferUnderflowException ex) {
            return 0;
        }
    }
    
    private String getSimpleToken(final ByteBuffer byteBuffer) {
        while (byteBuffer.get() <= 32) {}
        final int n = byteBuffer.position() - 1;
        while (byteBuffer.get() > 32) {}
        final byte[] array = new byte[byteBuffer.position() - n - 1];
        byteBuffer.position(n);
        byteBuffer.get(array);
        try {
            return new String(array, "US-ASCII");
        }
        catch (final UnsupportedEncodingException ex) {
            return new String(array);
        }
    }
    
    private String getString(final ByteBuffer byteBuffer) {
        final int position = byteBuffer.position();
        while (byteBuffer.get() != 41) {}
        final byte[] array = new byte[byteBuffer.position() - position - 1];
        byteBuffer.position(position);
        byteBuffer.get(array);
        try {
            return new String(array, "US-ASCII");
        }
        catch (final UnsupportedEncodingException ex) {
            return new String(array);
        }
    }
    
    @Override
    public String getPostscriptName() {
        return this.psName;
    }
    
    @Override
    protected synchronized FontScaler getScaler() {
        if (this.scaler == null) {
            this.scaler = FontScaler.getScaler(this, 0, false, this.fileSize);
        }
        return this.scaler;
    }
    
    @Override
    CharToGlyphMapper getMapper() {
        if (this.mapper == null) {
            this.mapper = new Type1GlyphMapper(this);
        }
        return this.mapper;
    }
    
    @Override
    public int getNumGlyphs() {
        try {
            return this.getScaler().getNumGlyphs();
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            return this.getNumGlyphs();
        }
    }
    
    @Override
    public int getMissingGlyphCode() {
        try {
            return this.getScaler().getMissingGlyphCode();
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            return this.getMissingGlyphCode();
        }
    }
    
    public int getGlyphCode(final char c) {
        try {
            return this.getScaler().getGlyphCode(c);
        }
        catch (final FontScalerException ex) {
            this.scaler = FontScaler.getNullScaler();
            return this.getGlyphCode(c);
        }
    }
    
    @Override
    public String toString() {
        return "** Type1 Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + " fileName=" + this.getPublicFileName();
    }
    
    static {
        Type1Font.styleAbbreviationsMapping = new HashMap();
        Type1Font.styleNameTokes = new HashSet();
        final String[] array = { "Black", "Bold", "Book", "Demi", "Heavy", "Light", "Meduium", "Nord", "Poster", "Regular", "Super", "Thin", "Compressed", "Condensed", "Compact", "Extended", "Narrow", "Inclined", "Italic", "Kursiv", "Oblique", "Upright", "Sloped", "Semi", "Ultra", "Extra", "Alternate", "Alternate", "Deutsche Fraktur", "Expert", "Inline", "Ornaments", "Outline", "Roman", "Rounded", "Script", "Shaded", "Swash", "Titling", "Typewriter" };
        final String[] array2 = { "Blk", "Bd", "Bk", "Dm", "Hv", "Lt", "Md", "Nd", "Po", "Rg", "Su", "Th", "Cm", "Cn", "Ct", "Ex", "Nr", "Ic", "It", "Ks", "Obl", "Up", "Sl", "Sm", "Ult", "X", "A", "Alt", "Dfr", "Exp", "In", "Or", "Ou", "Rm", "Rd", "Scr", "Sh", "Sw", "Ti", "Typ" };
        final String[] array3 = { "Black", "Bold", "Book", "Demi", "Heavy", "Light", "Medium", "Nord", "Poster", "Regular", "Super", "Thin", "Compressed", "Condensed", "Compact", "Extended", "Narrow", "Inclined", "Italic", "Kursiv", "Oblique", "Upright", "Sloped", "Slanted", "Semi", "Ultra", "Extra" };
        for (int i = 0; i < array.length; ++i) {
            Type1Font.styleAbbreviationsMapping.put(array2[i], array[i]);
        }
        for (int j = 0; j < array3.length; ++j) {
            Type1Font.styleNameTokes.add(array3[j]);
        }
    }
    
    private static class T1DisposerRecord implements DisposerRecord
    {
        String fileName;
        
        T1DisposerRecord(final String fileName) {
            this.fileName = null;
            this.fileName = fileName;
        }
        
        @Override
        public synchronized void dispose() {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    if (T1DisposerRecord.this.fileName != null) {
                        new File(T1DisposerRecord.this.fileName).delete();
                    }
                    return null;
                }
            });
        }
    }
}
