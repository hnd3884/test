package sun.nio.cs.ext;

import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.CharsetMapping;
import java.nio.charset.Charset;

public class SJIS_0213 extends Charset
{
    static CharsetMapping mapping;
    
    public SJIS_0213() {
        super("x-SJIS_0213", ExtendedCharsets.aliasesFor("SJIS_0213"));
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof SJIS || charset instanceof SJIS_0213;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }
    
    static {
        SJIS_0213.mapping = AccessController.doPrivileged((PrivilegedAction<CharsetMapping>)new PrivilegedAction<CharsetMapping>() {
            @Override
            public CharsetMapping run() {
                return CharsetMapping.get(SJIS_0213.class.getResourceAsStream("sjis0213.dat"));
            }
        });
    }
    
    protected static class Decoder extends CharsetDecoder
    {
        protected static final char UNMAPPABLE = '\ufffd';
        private char[] cc;
        private CharsetMapping.Entry comp;
        
        protected Decoder(final Charset charset) {
            super(charset, 0.5f, 1.0f);
            this.cc = new char[2];
            this.comp = new CharsetMapping.Entry();
        }
        
        private CoderResult decodeArrayLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            final byte[] array = byteBuffer.array();
            int i = byteBuffer.arrayOffset() + byteBuffer.position();
            final int n = byteBuffer.arrayOffset() + byteBuffer.limit();
            final char[] array2 = charBuffer.array();
            int n2 = charBuffer.arrayOffset() + charBuffer.position();
            final int n3 = charBuffer.arrayOffset() + charBuffer.limit();
            try {
                while (i < n) {
                    final int n4 = array[i] & 0xFF;
                    char c = this.decodeSingle(n4);
                    int n5 = 1;
                    int n6 = 1;
                    char[] decodeDoubleEx = null;
                    if (c == '\ufffd') {
                        if (n - i < 2) {
                            return CoderResult.UNDERFLOW;
                        }
                        final int n7 = array[i + 1] & 0xFF;
                        c = this.decodeDouble(n4, n7);
                        ++n5;
                        if (c == '\ufffd') {
                            decodeDoubleEx = this.decodeDoubleEx(n4, n7);
                            if (decodeDoubleEx == null) {
                                if (this.decodeSingle(n7) == '\ufffd') {
                                    return CoderResult.unmappableForLength(2);
                                }
                                return CoderResult.unmappableForLength(1);
                            }
                            else {
                                ++n6;
                            }
                        }
                    }
                    if (n3 - n2 < n6) {
                        return CoderResult.OVERFLOW;
                    }
                    if (n6 == 2) {
                        array2[n2++] = decodeDoubleEx[0];
                        array2[n2++] = decodeDoubleEx[1];
                    }
                    else {
                        array2[n2++] = c;
                    }
                    i += n5;
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                byteBuffer.position(i - byteBuffer.arrayOffset());
                charBuffer.position(n2 - charBuffer.arrayOffset());
            }
        }
        
        private CoderResult decodeBufferLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            int position = byteBuffer.position();
            try {
                while (byteBuffer.hasRemaining()) {
                    char[] decodeDoubleEx = null;
                    final int n = byteBuffer.get() & 0xFF;
                    char c = this.decodeSingle(n);
                    int n2 = 1;
                    int n3 = 1;
                    if (c == '\ufffd') {
                        if (byteBuffer.remaining() < 1) {
                            return CoderResult.UNDERFLOW;
                        }
                        final int n4 = byteBuffer.get() & 0xFF;
                        ++n2;
                        c = this.decodeDouble(n, n4);
                        if (c == '\ufffd') {
                            decodeDoubleEx = this.decodeDoubleEx(n, n4);
                            if (decodeDoubleEx == null) {
                                if (this.decodeSingle(n4) == '\ufffd') {
                                    return CoderResult.unmappableForLength(2);
                                }
                                return CoderResult.unmappableForLength(1);
                            }
                            else {
                                ++n3;
                            }
                        }
                    }
                    if (charBuffer.remaining() < n3) {
                        return CoderResult.OVERFLOW;
                    }
                    if (n3 == 2) {
                        charBuffer.put(decodeDoubleEx[0]);
                        charBuffer.put(decodeDoubleEx[1]);
                    }
                    else {
                        charBuffer.put(c);
                    }
                    position += n2;
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                byteBuffer.position(position);
            }
        }
        
        @Override
        protected CoderResult decodeLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            if (byteBuffer.hasArray() && charBuffer.hasArray()) {
                return this.decodeArrayLoop(byteBuffer, charBuffer);
            }
            return this.decodeBufferLoop(byteBuffer, charBuffer);
        }
        
        protected char decodeSingle(final int n) {
            return SJIS_0213.mapping.decodeSingle(n);
        }
        
        protected char decodeDouble(final int n, final int n2) {
            return SJIS_0213.mapping.decodeDouble(n, n2);
        }
        
        protected char[] decodeDoubleEx(final int n, final int n2) {
            final int bs = n << 8 | n2;
            if (SJIS_0213.mapping.decodeSurrogate(bs, this.cc) != null) {
                return this.cc;
            }
            this.comp.bs = bs;
            if (SJIS_0213.mapping.decodeComposite(this.comp, this.cc) != null) {
                return this.cc;
            }
            return null;
        }
    }
    
    protected static class Encoder extends CharsetEncoder
    {
        protected static final int UNMAPPABLE = 65533;
        protected static final int MAX_SINGLEBYTE = 255;
        private CharsetMapping.Entry comp;
        char leftoverBase;
        
        protected Encoder(final Charset charset) {
            super(charset, 2.0f, 2.0f);
            this.comp = new CharsetMapping.Entry();
            this.leftoverBase = '\0';
        }
        
        @Override
        public boolean canEncode(final char c) {
            return this.encodeChar(c) != 65533;
        }
        
        protected int encodeChar(final char c) {
            return SJIS_0213.mapping.encodeChar(c);
        }
        
        protected int encodeSurrogate(final char c, final char c2) {
            return SJIS_0213.mapping.encodeSurrogate(c, c2);
        }
        
        protected int encodeComposite(final char cp, final char cp2) {
            this.comp.cp = cp;
            this.comp.cp2 = cp2;
            return SJIS_0213.mapping.encodeComposite(this.comp);
        }
        
        protected boolean isCompositeBase(final char cp) {
            this.comp.cp = cp;
            return SJIS_0213.mapping.isCompositeBase(this.comp);
        }
        
        protected CoderResult encodeArrayLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            final char[] array = charBuffer.array();
            int i = charBuffer.arrayOffset() + charBuffer.position();
            final int n = charBuffer.arrayOffset() + charBuffer.limit();
            final byte[] array2 = byteBuffer.array();
            int n2 = byteBuffer.arrayOffset() + byteBuffer.position();
            final int n3 = byteBuffer.arrayOffset() + byteBuffer.limit();
            try {
                while (i < n) {
                    final char leftoverBase = array[i];
                    if (this.leftoverBase != '\0') {
                        boolean b = false;
                        int n4 = this.encodeComposite(this.leftoverBase, leftoverBase);
                        if (n4 == 65533) {
                            n4 = this.encodeChar(this.leftoverBase);
                        }
                        else {
                            b = true;
                        }
                        if (n3 - n2 < 2) {
                            return CoderResult.OVERFLOW;
                        }
                        array2[n2++] = (byte)(n4 >> 8);
                        array2[n2++] = (byte)n4;
                        this.leftoverBase = '\0';
                        if (b) {
                            ++i;
                            continue;
                        }
                    }
                    if (this.isCompositeBase(leftoverBase)) {
                        this.leftoverBase = leftoverBase;
                    }
                    else {
                        final int encodeChar = this.encodeChar(leftoverBase);
                        if (encodeChar <= 255) {
                            if (n3 <= n2) {
                                return CoderResult.OVERFLOW;
                            }
                            array2[n2++] = (byte)encodeChar;
                        }
                        else if (encodeChar != 65533) {
                            if (n3 - n2 < 2) {
                                return CoderResult.OVERFLOW;
                            }
                            array2[n2++] = (byte)(encodeChar >> 8);
                            array2[n2++] = (byte)encodeChar;
                        }
                        else if (Character.isHighSurrogate(leftoverBase)) {
                            if (i + 1 == n) {
                                return CoderResult.UNDERFLOW;
                            }
                            final char c = array[i + 1];
                            if (!Character.isLowSurrogate(c)) {
                                return CoderResult.malformedForLength(1);
                            }
                            final int encodeSurrogate = this.encodeSurrogate(leftoverBase, c);
                            if (encodeSurrogate == 65533) {
                                return CoderResult.unmappableForLength(2);
                            }
                            if (n3 - n2 < 2) {
                                return CoderResult.OVERFLOW;
                            }
                            array2[n2++] = (byte)(encodeSurrogate >> 8);
                            array2[n2++] = (byte)encodeSurrogate;
                            ++i;
                        }
                        else {
                            if (Character.isLowSurrogate(leftoverBase)) {
                                return CoderResult.malformedForLength(1);
                            }
                            return CoderResult.unmappableForLength(1);
                        }
                    }
                    ++i;
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                charBuffer.position(i - charBuffer.arrayOffset());
                byteBuffer.position(n2 - byteBuffer.arrayOffset());
            }
        }
        
        protected CoderResult encodeBufferLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            int position = charBuffer.position();
            try {
                while (charBuffer.hasRemaining()) {
                    final char value = charBuffer.get();
                    if (this.leftoverBase != '\0') {
                        boolean b = false;
                        int n = this.encodeComposite(this.leftoverBase, value);
                        if (n == 65533) {
                            n = this.encodeChar(this.leftoverBase);
                        }
                        else {
                            b = true;
                        }
                        if (byteBuffer.remaining() < 2) {
                            return CoderResult.OVERFLOW;
                        }
                        byteBuffer.put((byte)(n >> 8));
                        byteBuffer.put((byte)n);
                        this.leftoverBase = '\0';
                        if (b) {
                            ++position;
                            continue;
                        }
                    }
                    if (this.isCompositeBase(value)) {
                        this.leftoverBase = value;
                    }
                    else {
                        final int encodeChar = this.encodeChar(value);
                        if (encodeChar <= 255) {
                            if (byteBuffer.remaining() < 1) {
                                return CoderResult.OVERFLOW;
                            }
                            byteBuffer.put((byte)encodeChar);
                        }
                        else if (encodeChar != 65533) {
                            if (byteBuffer.remaining() < 2) {
                                return CoderResult.OVERFLOW;
                            }
                            byteBuffer.put((byte)(encodeChar >> 8));
                            byteBuffer.put((byte)encodeChar);
                        }
                        else if (Character.isHighSurrogate(value)) {
                            if (!charBuffer.hasRemaining()) {
                                return CoderResult.UNDERFLOW;
                            }
                            final char value2 = charBuffer.get();
                            if (!Character.isLowSurrogate(value2)) {
                                return CoderResult.malformedForLength(1);
                            }
                            final int encodeSurrogate = this.encodeSurrogate(value, value2);
                            if (encodeSurrogate == 65533) {
                                return CoderResult.unmappableForLength(2);
                            }
                            if (byteBuffer.remaining() < 2) {
                                return CoderResult.OVERFLOW;
                            }
                            byteBuffer.put((byte)(encodeSurrogate >> 8));
                            byteBuffer.put((byte)encodeSurrogate);
                            ++position;
                        }
                        else {
                            if (Character.isLowSurrogate(value)) {
                                return CoderResult.malformedForLength(1);
                            }
                            return CoderResult.unmappableForLength(1);
                        }
                    }
                    ++position;
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                charBuffer.position(position);
            }
        }
        
        @Override
        protected CoderResult encodeLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            if (charBuffer.hasArray() && byteBuffer.hasArray()) {
                return this.encodeArrayLoop(charBuffer, byteBuffer);
            }
            return this.encodeBufferLoop(charBuffer, byteBuffer);
        }
        
        @Override
        protected CoderResult implFlush(final ByteBuffer byteBuffer) {
            if (this.leftoverBase > '\0') {
                if (byteBuffer.remaining() < 2) {
                    return CoderResult.OVERFLOW;
                }
                final int encodeChar = this.encodeChar(this.leftoverBase);
                byteBuffer.put((byte)(encodeChar >> 8));
                byteBuffer.put((byte)encodeChar);
                this.leftoverBase = '\0';
            }
            return CoderResult.UNDERFLOW;
        }
        
        @Override
        protected void implReset() {
            this.leftoverBase = '\0';
        }
    }
}
