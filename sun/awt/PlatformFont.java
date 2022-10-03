package sun.awt;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Vector;
import java.util.Locale;
import sun.java2d.FontSupport;
import sun.font.SunFontManager;
import java.awt.peer.FontPeer;

public abstract class PlatformFont implements FontPeer
{
    protected FontDescriptor[] componentFonts;
    protected char defaultChar;
    protected FontConfiguration fontConfig;
    protected FontDescriptor defaultFont;
    protected String familyName;
    private Object[] fontCache;
    protected static int FONTCACHESIZE;
    protected static int FONTCACHEMASK;
    protected static String osVersion;
    
    public PlatformFont(final String s, final int n) {
        final SunFontManager instance = SunFontManager.getInstance();
        if (instance instanceof FontSupport) {
            this.fontConfig = instance.getFontConfiguration();
        }
        if (this.fontConfig == null) {
            return;
        }
        this.familyName = s.toLowerCase(Locale.ENGLISH);
        if (!FontConfiguration.isLogicalFontFamilyName(this.familyName)) {
            this.familyName = this.fontConfig.getFallbackFamilyName(this.familyName, "sansserif");
        }
        this.componentFonts = this.fontConfig.getFontDescriptors(this.familyName, n);
        final char missingGlyphCharacter = this.getMissingGlyphCharacter();
        this.defaultChar = '?';
        if (this.componentFonts.length > 0) {
            this.defaultFont = this.componentFonts[0];
        }
        for (int i = 0; i < this.componentFonts.length; ++i) {
            if (!this.componentFonts[i].isExcluded(missingGlyphCharacter)) {
                if (this.componentFonts[i].encoder.canEncode(missingGlyphCharacter)) {
                    this.defaultFont = this.componentFonts[i];
                    this.defaultChar = missingGlyphCharacter;
                    break;
                }
            }
        }
    }
    
    protected abstract char getMissingGlyphCharacter();
    
    public CharsetString[] makeMultiCharsetString(final String s) {
        return this.makeMultiCharsetString(s.toCharArray(), 0, s.length(), true);
    }
    
    public CharsetString[] makeMultiCharsetString(final String s, final boolean b) {
        return this.makeMultiCharsetString(s.toCharArray(), 0, s.length(), b);
    }
    
    public CharsetString[] makeMultiCharsetString(final char[] array, final int n, final int n2) {
        return this.makeMultiCharsetString(array, n, n2, true);
    }
    
    public CharsetString[] makeMultiCharsetString(final char[] array, final int n, final int n2, final boolean b) {
        if (n2 < 1) {
            return new CharsetString[0];
        }
        Vector<CharsetString> vector = null;
        final char[] array2 = new char[n2];
        char defaultChar = this.defaultChar;
        boolean b2 = false;
        FontDescriptor defaultFont = this.defaultFont;
        for (int i = 0; i < this.componentFonts.length; ++i) {
            if (!this.componentFonts[i].isExcluded(array[n])) {
                if (this.componentFonts[i].encoder.canEncode(array[n])) {
                    defaultFont = this.componentFonts[i];
                    defaultChar = array[n];
                    b2 = true;
                    break;
                }
            }
        }
        if (!b && !b2) {
            return null;
        }
        array2[0] = defaultChar;
        int n3 = 0;
        for (int j = 1; j < n2; ++j) {
            final char c = array[n + j];
            FontDescriptor defaultFont2 = this.defaultFont;
            char defaultChar2 = this.defaultChar;
            boolean b3 = false;
            for (int k = 0; k < this.componentFonts.length; ++k) {
                if (!this.componentFonts[k].isExcluded(c)) {
                    if (this.componentFonts[k].encoder.canEncode(c)) {
                        defaultFont2 = this.componentFonts[k];
                        defaultChar2 = c;
                        b3 = true;
                        break;
                    }
                }
            }
            if (!b && !b3) {
                return null;
            }
            array2[j] = defaultChar2;
            if (defaultFont != defaultFont2) {
                if (vector == null) {
                    vector = new Vector<CharsetString>(3);
                }
                vector.addElement(new CharsetString(array2, n3, j - n3, defaultFont));
                defaultFont = defaultFont2;
                final FontDescriptor defaultFont3 = this.defaultFont;
                n3 = j;
            }
        }
        final CharsetString charsetString = new CharsetString(array2, n3, n2 - n3, defaultFont);
        CharsetString[] array3;
        if (vector == null) {
            array3 = new CharsetString[] { charsetString };
        }
        else {
            vector.addElement(charsetString);
            array3 = new CharsetString[vector.size()];
            for (int l = 0; l < vector.size(); ++l) {
                array3[l] = vector.elementAt(l);
            }
        }
        return array3;
    }
    
    public boolean mightHaveMultiFontMetrics() {
        return this.fontConfig != null;
    }
    
    public Object[] makeConvertedMultiFontString(final String s) {
        return this.makeConvertedMultiFontChars(s.toCharArray(), 0, s.length());
    }
    
    public Object[] makeConvertedMultiFontChars(final char[] array, final int n, final int n2) {
        Object[] array2 = new Object[2];
        Object o = null;
        int i = n;
        int n3 = 0;
        int n4 = 0;
        FontDescriptor fontDescriptor = null;
        final int n5 = n + n2;
        if (n < 0 || n5 > array.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (i >= n5) {
            return null;
        }
        while (i < n5) {
            final char c = array[i];
            final int n6 = c & PlatformFont.FONTCACHEMASK;
            Object o2 = this.getFontCache()[n6];
            if (o2 == null || ((PlatformFontCache)o2).uniChar != c) {
                FontDescriptor defaultFont = this.defaultFont;
                char defaultChar = this.defaultChar;
                final char c2 = array[i];
                for (int length = this.componentFonts.length, j = 0; j < length; ++j) {
                    final FontDescriptor fontDescriptor2 = this.componentFonts[j];
                    fontDescriptor2.encoder.reset();
                    if (!fontDescriptor2.isExcluded(c2)) {
                        if (fontDescriptor2.encoder.canEncode(c2)) {
                            defaultFont = fontDescriptor2;
                            defaultChar = c2;
                            break;
                        }
                    }
                }
                try {
                    final char[] array3 = { defaultChar };
                    o2 = new PlatformFontCache();
                    if (defaultFont.useUnicode()) {
                        if (FontDescriptor.isLE) {
                            ((PlatformFontCache)o2).bb.put((byte)(array3[0] & '\u00ff'));
                            ((PlatformFontCache)o2).bb.put((byte)(array3[0] >> 8));
                        }
                        else {
                            ((PlatformFontCache)o2).bb.put((byte)(array3[0] >> 8));
                            ((PlatformFontCache)o2).bb.put((byte)(array3[0] & '\u00ff'));
                        }
                    }
                    else {
                        defaultFont.encoder.encode(CharBuffer.wrap(array3), ((PlatformFontCache)o2).bb, true);
                    }
                    ((PlatformFontCache)o2).fontDescriptor = defaultFont;
                    ((PlatformFontCache)o2).uniChar = array[i];
                    this.getFontCache()[n6] = o2;
                }
                catch (final Exception ex) {
                    System.err.println(ex);
                    ex.printStackTrace();
                    return null;
                }
            }
            if (fontDescriptor != ((PlatformFontCache)o2).fontDescriptor) {
                if (fontDescriptor != null) {
                    array2[n4++] = fontDescriptor;
                    if ((array2[n4++] = o) != null) {
                        n3 -= 4;
                        o[0] = (byte)(n3 >> 24);
                        o[1] = (byte)(n3 >> 16);
                        o[2] = (byte)(n3 >> 8);
                        o[3] = (byte)n3;
                    }
                    if (n4 >= array2.length) {
                        final Object[] array4 = new Object[array2.length * 2];
                        System.arraycopy(array2, 0, array4, 0, array2.length);
                        array2 = array4;
                    }
                }
                if (((PlatformFontCache)o2).fontDescriptor.useUnicode()) {
                    o = new byte[(n5 - i + 1) * (int)((PlatformFontCache)o2).fontDescriptor.unicodeEncoder.maxBytesPerChar() + 4];
                }
                else {
                    o = new byte[(n5 - i + 1) * (int)((PlatformFontCache)o2).fontDescriptor.encoder.maxBytesPerChar() + 4];
                }
                n3 = 4;
                fontDescriptor = ((PlatformFontCache)o2).fontDescriptor;
            }
            final byte[] array5 = ((PlatformFontCache)o2).bb.array();
            final int position = ((PlatformFontCache)o2).bb.position();
            if (position == 1) {
                o[n3++] = array5[0];
            }
            else if (position == 2) {
                o[n3++] = array5[0];
                o[n3++] = array5[1];
            }
            else if (position == 3) {
                o[n3++] = array5[0];
                o[n3++] = array5[1];
                o[n3++] = array5[2];
            }
            else if (position == 4) {
                o[n3++] = array5[0];
                o[n3++] = array5[1];
                o[n3++] = array5[2];
                o[n3++] = array5[3];
            }
            ++i;
        }
        array2[n4++] = fontDescriptor;
        if ((array2[n4] = o) != null) {
            n3 -= 4;
            o[0] = (byte)(n3 >> 24);
            o[1] = (byte)(n3 >> 16);
            o[2] = (byte)(n3 >> 8);
            o[3] = (byte)n3;
        }
        return array2;
    }
    
    protected final Object[] getFontCache() {
        if (this.fontCache == null) {
            this.fontCache = new Object[PlatformFont.FONTCACHESIZE];
        }
        return this.fontCache;
    }
    
    private static native void initIDs();
    
    static {
        NativeLibLoader.loadLibraries();
        initIDs();
        PlatformFont.FONTCACHESIZE = 256;
        PlatformFont.FONTCACHEMASK = PlatformFont.FONTCACHESIZE - 1;
    }
    
    class PlatformFontCache
    {
        char uniChar;
        FontDescriptor fontDescriptor;
        ByteBuffer bb;
        
        PlatformFontCache() {
            this.bb = ByteBuffer.allocate(4);
        }
    }
}
