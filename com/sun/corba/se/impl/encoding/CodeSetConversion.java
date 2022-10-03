package com.sun.corba.se.impl.encoding;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.UnmappableCharacterException;
import java.nio.charset.MalformedInputException;
import java.nio.CharBuffer;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public class CodeSetConversion
{
    private static CodeSetConversion implementation;
    private static final int FALLBACK_CODESET = 0;
    private CodeSetCache cache;
    
    public CTBConverter getCTBConverter(final OSFCodeSetRegistry.Entry entry) {
        return new JavaCTBConverter(entry, entry.isFixedWidth() ? entry.getMaxBytesPerChar() : 1);
    }
    
    public CTBConverter getCTBConverter(final OSFCodeSetRegistry.Entry entry, final boolean b, final boolean b2) {
        if (entry == OSFCodeSetRegistry.UCS_2) {
            return new UTF16CTBConverter(b);
        }
        if (entry != OSFCodeSetRegistry.UTF_16) {
            return new JavaCTBConverter(entry, entry.isFixedWidth() ? entry.getMaxBytesPerChar() : 1);
        }
        if (b2) {
            return new UTF16CTBConverter();
        }
        return new UTF16CTBConverter(b);
    }
    
    public BTCConverter getBTCConverter(final OSFCodeSetRegistry.Entry entry) {
        return new JavaBTCConverter(entry);
    }
    
    public BTCConverter getBTCConverter(final OSFCodeSetRegistry.Entry entry, final boolean b) {
        if (entry == OSFCodeSetRegistry.UTF_16 || entry == OSFCodeSetRegistry.UCS_2) {
            return new UTF16BTCConverter(b);
        }
        return new JavaBTCConverter(entry);
    }
    
    private int selectEncoding(final CodeSetComponentInfo.CodeSetComponent codeSetComponent, final CodeSetComponentInfo.CodeSetComponent codeSetComponent2) {
        int nativeCodeSet = codeSetComponent2.nativeCodeSet;
        if (nativeCodeSet == 0) {
            if (codeSetComponent2.conversionCodeSets.length <= 0) {
                return 0;
            }
            nativeCodeSet = codeSetComponent2.conversionCodeSets[0];
        }
        if (codeSetComponent.nativeCodeSet == nativeCodeSet) {
            return nativeCodeSet;
        }
        for (int i = 0; i < codeSetComponent.conversionCodeSets.length; ++i) {
            if (nativeCodeSet == codeSetComponent.conversionCodeSets[i]) {
                return nativeCodeSet;
            }
        }
        for (int j = 0; j < codeSetComponent2.conversionCodeSets.length; ++j) {
            if (codeSetComponent.nativeCodeSet == codeSetComponent2.conversionCodeSets[j]) {
                return codeSetComponent.nativeCodeSet;
            }
        }
        for (int k = 0; k < codeSetComponent2.conversionCodeSets.length; ++k) {
            for (int l = 0; l < codeSetComponent.conversionCodeSets.length; ++l) {
                if (codeSetComponent2.conversionCodeSets[k] == codeSetComponent.conversionCodeSets[l]) {
                    return codeSetComponent2.conversionCodeSets[k];
                }
            }
        }
        return 0;
    }
    
    public CodeSetComponentInfo.CodeSetContext negotiate(final CodeSetComponentInfo codeSetComponentInfo, final CodeSetComponentInfo codeSetComponentInfo2) {
        int n = this.selectEncoding(codeSetComponentInfo.getCharComponent(), codeSetComponentInfo2.getCharComponent());
        if (n == 0) {
            n = OSFCodeSetRegistry.UTF_8.getNumber();
        }
        int n2 = this.selectEncoding(codeSetComponentInfo.getWCharComponent(), codeSetComponentInfo2.getWCharComponent());
        if (n2 == 0) {
            n2 = OSFCodeSetRegistry.UTF_16.getNumber();
        }
        return new CodeSetComponentInfo.CodeSetContext(n, n2);
    }
    
    private CodeSetConversion() {
        this.cache = new CodeSetCache();
    }
    
    public static final CodeSetConversion impl() {
        return CodeSetConversionHolder.csc;
    }
    
    public abstract static class CTBConverter
    {
        public abstract void convert(final char p0);
        
        public abstract void convert(final String p0);
        
        public abstract int getNumBytes();
        
        public abstract float getMaxBytesPerChar();
        
        public abstract boolean isFixedWidthEncoding();
        
        public abstract int getAlignment();
        
        public abstract byte[] getBytes();
    }
    
    public abstract static class BTCConverter
    {
        public abstract boolean isFixedWidthEncoding();
        
        public abstract int getFixedCharWidth();
        
        public abstract int getNumChars();
        
        public abstract char[] getChars(final byte[] p0, final int p1, final int p2);
    }
    
    private class JavaCTBConverter extends CTBConverter
    {
        private ORBUtilSystemException wrapper;
        private OMGSystemException omgWrapper;
        private CharsetEncoder ctb;
        private int alignment;
        private char[] chars;
        private int numBytes;
        private int numChars;
        private ByteBuffer buffer;
        private OSFCodeSetRegistry.Entry codeset;
        
        public JavaCTBConverter(final OSFCodeSetRegistry.Entry codeset, final int alignment) {
            this.wrapper = ORBUtilSystemException.get("rpc.encoding");
            this.omgWrapper = OMGSystemException.get("rpc.encoding");
            this.chars = null;
            this.numBytes = 0;
            this.numChars = 0;
            try {
                this.ctb = CodeSetConversion.this.cache.getCharToByteConverter(codeset.getName());
                if (this.ctb == null) {
                    this.ctb = Charset.forName(codeset.getName()).newEncoder();
                    CodeSetConversion.this.cache.setConverter(codeset.getName(), this.ctb);
                }
            }
            catch (final IllegalCharsetNameException ex) {
                throw this.wrapper.invalidCtbConverterName(ex, codeset.getName());
            }
            catch (final UnsupportedCharsetException ex2) {
                throw this.wrapper.invalidCtbConverterName(ex2, codeset.getName());
            }
            this.codeset = codeset;
            this.alignment = alignment;
        }
        
        @Override
        public final float getMaxBytesPerChar() {
            return this.ctb.maxBytesPerChar();
        }
        
        @Override
        public void convert(final char c) {
            if (this.chars == null) {
                this.chars = new char[1];
            }
            this.chars[0] = c;
            this.numChars = 1;
            this.convertCharArray();
        }
        
        @Override
        public void convert(final String s) {
            if (this.chars == null || this.chars.length < s.length()) {
                this.chars = new char[s.length()];
            }
            s.getChars(0, this.numChars = s.length(), this.chars, 0);
            this.convertCharArray();
        }
        
        @Override
        public final int getNumBytes() {
            return this.numBytes;
        }
        
        @Override
        public final int getAlignment() {
            return this.alignment;
        }
        
        @Override
        public final boolean isFixedWidthEncoding() {
            return this.codeset.isFixedWidth();
        }
        
        @Override
        public byte[] getBytes() {
            return this.buffer.array();
        }
        
        private void convertCharArray() {
            try {
                this.buffer = this.ctb.encode(CharBuffer.wrap(this.chars, 0, this.numChars));
                this.numBytes = this.buffer.limit();
            }
            catch (final IllegalStateException ex) {
                throw this.wrapper.ctbConverterFailure(ex);
            }
            catch (final MalformedInputException ex2) {
                throw this.wrapper.badUnicodePair(ex2);
            }
            catch (final UnmappableCharacterException ex3) {
                throw this.omgWrapper.charNotInCodeset(ex3);
            }
            catch (final CharacterCodingException ex4) {
                throw this.wrapper.ctbConverterFailure(ex4);
            }
        }
    }
    
    private class UTF16CTBConverter extends JavaCTBConverter
    {
        public UTF16CTBConverter() {
            super(OSFCodeSetRegistry.UTF_16, 2);
        }
        
        public UTF16CTBConverter(final boolean b) {
            super(b ? OSFCodeSetRegistry.UTF_16LE : OSFCodeSetRegistry.UTF_16BE, 2);
        }
    }
    
    private class JavaBTCConverter extends BTCConverter
    {
        private ORBUtilSystemException wrapper;
        private OMGSystemException omgWrapper;
        protected CharsetDecoder btc;
        private char[] buffer;
        private int resultingNumChars;
        private OSFCodeSetRegistry.Entry codeset;
        
        public JavaBTCConverter(final OSFCodeSetRegistry.Entry codeset) {
            this.wrapper = ORBUtilSystemException.get("rpc.encoding");
            this.omgWrapper = OMGSystemException.get("rpc.encoding");
            this.btc = this.getConverter(codeset.getName());
            this.codeset = codeset;
        }
        
        @Override
        public final boolean isFixedWidthEncoding() {
            return this.codeset.isFixedWidth();
        }
        
        @Override
        public final int getFixedCharWidth() {
            return this.codeset.getMaxBytesPerChar();
        }
        
        @Override
        public final int getNumChars() {
            return this.resultingNumChars;
        }
        
        @Override
        public char[] getChars(final byte[] array, final int n, final int n2) {
            try {
                final CharBuffer decode = this.btc.decode(ByteBuffer.wrap(array, n, n2));
                this.resultingNumChars = decode.limit();
                if (decode.limit() == decode.capacity()) {
                    this.buffer = decode.array();
                }
                else {
                    this.buffer = new char[decode.limit()];
                    decode.get(this.buffer, 0, decode.limit()).position(0);
                }
                return this.buffer;
            }
            catch (final IllegalStateException ex) {
                throw this.wrapper.btcConverterFailure(ex);
            }
            catch (final MalformedInputException ex2) {
                throw this.wrapper.badUnicodePair(ex2);
            }
            catch (final UnmappableCharacterException ex3) {
                throw this.omgWrapper.charNotInCodeset(ex3);
            }
            catch (final CharacterCodingException ex4) {
                throw this.wrapper.btcConverterFailure(ex4);
            }
        }
        
        protected CharsetDecoder getConverter(final String s) {
            CharsetDecoder charsetDecoder;
            try {
                charsetDecoder = CodeSetConversion.this.cache.getByteToCharConverter(s);
                if (charsetDecoder == null) {
                    charsetDecoder = Charset.forName(s).newDecoder();
                    CodeSetConversion.this.cache.setConverter(s, charsetDecoder);
                }
            }
            catch (final IllegalCharsetNameException ex) {
                throw this.wrapper.invalidBtcConverterName(ex, s);
            }
            return charsetDecoder;
        }
    }
    
    private class UTF16BTCConverter extends JavaBTCConverter
    {
        private boolean defaultToLittleEndian;
        private boolean converterUsesBOM;
        private static final char UTF16_BE_MARKER = '\ufeff';
        private static final char UTF16_LE_MARKER = '\ufffe';
        
        public UTF16BTCConverter(final boolean defaultToLittleEndian) {
            super(OSFCodeSetRegistry.UTF_16);
            this.converterUsesBOM = true;
            this.defaultToLittleEndian = defaultToLittleEndian;
        }
        
        @Override
        public char[] getChars(final byte[] array, final int n, final int n2) {
            if (this.hasUTF16ByteOrderMarker(array, n, n2)) {
                if (!this.converterUsesBOM) {
                    this.switchToConverter(OSFCodeSetRegistry.UTF_16);
                }
                this.converterUsesBOM = true;
                return super.getChars(array, n, n2);
            }
            if (this.converterUsesBOM) {
                if (this.defaultToLittleEndian) {
                    this.switchToConverter(OSFCodeSetRegistry.UTF_16LE);
                }
                else {
                    this.switchToConverter(OSFCodeSetRegistry.UTF_16BE);
                }
                this.converterUsesBOM = false;
            }
            return super.getChars(array, n, n2);
        }
        
        private boolean hasUTF16ByteOrderMarker(final byte[] array, final int n, final int n2) {
            if (n2 >= 4) {
                final char c = (char)((array[n] & 0xFF) << 8 | (array[n + 1] & 0xFF) << 0);
                return c == '\ufeff' || c == '\ufffe';
            }
            return false;
        }
        
        private void switchToConverter(final OSFCodeSetRegistry.Entry entry) {
            this.btc = super.getConverter(entry.getName());
        }
    }
    
    private static class CodeSetConversionHolder
    {
        static final CodeSetConversion csc;
        
        static {
            csc = new CodeSetConversion(null);
        }
    }
}
