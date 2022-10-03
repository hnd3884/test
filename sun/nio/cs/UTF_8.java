package sun.nio.cs;

import java.nio.charset.CodingErrorAction;
import java.nio.CharBuffer;
import java.nio.charset.CoderResult;
import java.nio.ByteBuffer;
import java.nio.Buffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

class UTF_8 extends Unicode
{
    public UTF_8() {
        super("UTF-8", StandardCharsets.aliases_UTF_8);
    }
    
    @Override
    public String historicalName() {
        return "UTF8";
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder((Charset)this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder((Charset)this);
    }
    
    private static final void updatePositions(final Buffer buffer, final int n, final Buffer buffer2, final int n2) {
        buffer.position(n - buffer.arrayOffset());
        buffer2.position(n2 - buffer2.arrayOffset());
    }
    
    private static class Decoder extends CharsetDecoder implements ArrayDecoder
    {
        private Decoder(final Charset charset) {
            super(charset, 1.0f, 1.0f);
        }
        
        private static boolean isNotContinuation(final int n) {
            return (n & 0xC0) != 0x80;
        }
        
        private static boolean isMalformed3(final int n, final int n2, final int n3) {
            return (n == -32 && (n2 & 0xE0) == 0x80) || (n2 & 0xC0) != 0x80 || (n3 & 0xC0) != 0x80;
        }
        
        private static boolean isMalformed3_2(final int n, final int n2) {
            return (n == -32 && (n2 & 0xE0) == 0x80) || (n2 & 0xC0) != 0x80;
        }
        
        private static boolean isMalformed4(final int n, final int n2, final int n3) {
            return (n & 0xC0) != 0x80 || (n2 & 0xC0) != 0x80 || (n3 & 0xC0) != 0x80;
        }
        
        private static boolean isMalformed4_2(final int n, final int n2) {
            return (n == 240 && (n2 < 144 || n2 > 191)) || (n == 244 && (n2 & 0xF0) != 0x80) || (n2 & 0xC0) != 0x80;
        }
        
        private static boolean isMalformed4_3(final int n) {
            return (n & 0xC0) != 0x80;
        }
        
        private static CoderResult lookupN(final ByteBuffer byteBuffer, final int n) {
            for (int i = 1; i < n; ++i) {
                if (isNotContinuation(byteBuffer.get())) {
                    return CoderResult.malformedForLength(i);
                }
            }
            return CoderResult.malformedForLength(n);
        }
        
        private static CoderResult malformedN(final ByteBuffer byteBuffer, final int n) {
            switch (n) {
                case 1:
                case 2: {
                    return CoderResult.malformedForLength(1);
                }
                case 3: {
                    final byte value = byteBuffer.get();
                    final byte value2 = byteBuffer.get();
                    return CoderResult.malformedForLength(((value == -32 && (value2 & 0xE0) == 0x80) || isNotContinuation(value2)) ? 1 : 2);
                }
                case 4: {
                    final int n2 = byteBuffer.get() & 0xFF;
                    final int n3 = byteBuffer.get() & 0xFF;
                    if (n2 > 244 || (n2 == 240 && (n3 < 144 || n3 > 191)) || (n2 == 244 && (n3 & 0xF0) != 0x80) || isNotContinuation(n3)) {
                        return CoderResult.malformedForLength(1);
                    }
                    if (isNotContinuation(byteBuffer.get())) {
                        return CoderResult.malformedForLength(2);
                    }
                    return CoderResult.malformedForLength(3);
                }
                default: {
                    assert false;
                    return null;
                }
            }
        }
        
        private static CoderResult malformed(final ByteBuffer byteBuffer, final int n, final CharBuffer charBuffer, final int n2, final int n3) {
            byteBuffer.position(n - byteBuffer.arrayOffset());
            final CoderResult malformedN = malformedN(byteBuffer, n3);
            updatePositions(byteBuffer, n, charBuffer, n2);
            return malformedN;
        }
        
        private static CoderResult malformed(final ByteBuffer byteBuffer, final int n, final int n2) {
            byteBuffer.position(n);
            final CoderResult malformedN = malformedN(byteBuffer, n2);
            byteBuffer.position(n);
            return malformedN;
        }
        
        private static CoderResult malformedForLength(final ByteBuffer byteBuffer, final int n, final CharBuffer charBuffer, final int n2, final int n3) {
            updatePositions(byteBuffer, n, charBuffer, n2);
            return CoderResult.malformedForLength(n3);
        }
        
        private static CoderResult malformedForLength(final ByteBuffer byteBuffer, final int n, final int n2) {
            byteBuffer.position(n);
            return CoderResult.malformedForLength(n2);
        }
        
        private static CoderResult xflow(final Buffer buffer, final int n, final int n2, final Buffer buffer2, final int n3, final int n4) {
            updatePositions(buffer, n, buffer2, n3);
            return (n4 == 0 || n2 - n < n4) ? CoderResult.UNDERFLOW : CoderResult.OVERFLOW;
        }
        
        private static CoderResult xflow(final Buffer buffer, final int n, final int n2) {
            buffer.position(n);
            return (n2 == 0 || buffer.remaining() < n2) ? CoderResult.UNDERFLOW : CoderResult.OVERFLOW;
        }
        
        private CoderResult decodeArrayLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            byte[] array;
            int i;
            int n;
            char[] array2;
            int n2;
            int n3;
            for (array = byteBuffer.array(), i = byteBuffer.arrayOffset() + byteBuffer.position(), n = byteBuffer.arrayOffset() + byteBuffer.limit(), array2 = charBuffer.array(), n2 = charBuffer.arrayOffset() + charBuffer.position(), n3 = charBuffer.arrayOffset() + charBuffer.limit(); n2 < n2 + Math.min(n - i, n3 - n2) && array[i] >= 0; array2[n2++] = (char)array[i++]) {}
            while (i < n) {
                final byte b = array[i];
                if (b >= 0) {
                    if (n2 >= n3) {
                        return xflow(byteBuffer, i, n, charBuffer, n2, 1);
                    }
                    array2[n2++] = (char)b;
                    ++i;
                }
                else if (b >> 5 == -2 && (b & 0x1E) != 0x0) {
                    if (n - i < 2 || n2 >= n3) {
                        return xflow(byteBuffer, i, n, charBuffer, n2, 2);
                    }
                    final byte b2 = array[i + 1];
                    if (isNotContinuation(b2)) {
                        return malformedForLength(byteBuffer, i, charBuffer, n2, 1);
                    }
                    array2[n2++] = (char)(b << 6 ^ b2 ^ 0xF80);
                    i += 2;
                }
                else if (b >> 4 == -2) {
                    final int n4 = n - i;
                    if (n4 < 3 || n2 >= n3) {
                        if (n4 > 1 && isMalformed3_2(b, array[i + 1])) {
                            return malformedForLength(byteBuffer, i, charBuffer, n2, 1);
                        }
                        return xflow(byteBuffer, i, n, charBuffer, n2, 3);
                    }
                    else {
                        final byte b3 = array[i + 1];
                        final byte b4 = array[i + 2];
                        if (isMalformed3(b, b3, b4)) {
                            return malformed(byteBuffer, i, charBuffer, n2, 3);
                        }
                        final char c = (char)(b << 12 ^ b3 << 6 ^ (b4 ^ 0xFFFE1F80));
                        if (Character.isSurrogate(c)) {
                            return malformedForLength(byteBuffer, i, charBuffer, n2, 3);
                        }
                        array2[n2++] = c;
                        i += 3;
                    }
                }
                else {
                    if (b >> 3 != -2) {
                        return malformed(byteBuffer, i, charBuffer, n2, 1);
                    }
                    final int n5 = n - i;
                    if (n5 < 4 || n3 - n2 < 2) {
                        final int n6 = b & 0xFF;
                        if (n6 > 244 || (n5 > 1 && isMalformed4_2(n6, array[i + 1] & 0xFF))) {
                            return malformedForLength(byteBuffer, i, charBuffer, n2, 1);
                        }
                        if (n5 > 2 && isMalformed4_3(array[i + 2])) {
                            return malformedForLength(byteBuffer, i, charBuffer, n2, 2);
                        }
                        return xflow(byteBuffer, i, n, charBuffer, n2, 4);
                    }
                    else {
                        final byte b5 = array[i + 1];
                        final byte b6 = array[i + 2];
                        final byte b7 = array[i + 3];
                        final int n7 = b << 18 ^ b5 << 12 ^ b6 << 6 ^ (b7 ^ 0x381F80);
                        if (isMalformed4(b5, b6, b7) || !Character.isSupplementaryCodePoint(n7)) {
                            return malformed(byteBuffer, i, charBuffer, n2, 4);
                        }
                        array2[n2++] = Character.highSurrogate(n7);
                        array2[n2++] = Character.lowSurrogate(n7);
                        i += 4;
                    }
                }
            }
            return xflow(byteBuffer, i, n, charBuffer, n2, 0);
        }
        
        private CoderResult decodeBufferLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            int i = byteBuffer.position();
            final int limit = byteBuffer.limit();
            while (i < limit) {
                final byte value = byteBuffer.get();
                if (value >= 0) {
                    if (charBuffer.remaining() < 1) {
                        return xflow(byteBuffer, i, 1);
                    }
                    charBuffer.put((char)value);
                    ++i;
                }
                else if (value >> 5 == -2 && (value & 0x1E) != 0x0) {
                    if (limit - i < 2 || charBuffer.remaining() < 1) {
                        return xflow(byteBuffer, i, 2);
                    }
                    final byte value2 = byteBuffer.get();
                    if (isNotContinuation(value2)) {
                        return malformedForLength(byteBuffer, i, 1);
                    }
                    charBuffer.put((char)(value << 6 ^ value2 ^ 0xF80));
                    i += 2;
                }
                else if (value >> 4 == -2) {
                    final int n = limit - i;
                    if (n < 3 || charBuffer.remaining() < 1) {
                        if (n > 1 && isMalformed3_2(value, byteBuffer.get())) {
                            return malformedForLength(byteBuffer, i, 1);
                        }
                        return xflow(byteBuffer, i, 3);
                    }
                    else {
                        final byte value3 = byteBuffer.get();
                        final byte value4 = byteBuffer.get();
                        if (isMalformed3(value, value3, value4)) {
                            return malformed(byteBuffer, i, 3);
                        }
                        final char c = (char)(value << 12 ^ value3 << 6 ^ (value4 ^ 0xFFFE1F80));
                        if (Character.isSurrogate(c)) {
                            return malformedForLength(byteBuffer, i, 3);
                        }
                        charBuffer.put(c);
                        i += 3;
                    }
                }
                else {
                    if (value >> 3 != -2) {
                        return malformed(byteBuffer, i, 1);
                    }
                    final int n2 = limit - i;
                    if (n2 < 4 || charBuffer.remaining() < 2) {
                        final int n3 = value & 0xFF;
                        if (n3 > 244 || (n2 > 1 && isMalformed4_2(n3, byteBuffer.get() & 0xFF))) {
                            return malformedForLength(byteBuffer, i, 1);
                        }
                        if (n2 > 2 && isMalformed4_3(byteBuffer.get())) {
                            return malformedForLength(byteBuffer, i, 2);
                        }
                        return xflow(byteBuffer, i, 4);
                    }
                    else {
                        final byte value5 = byteBuffer.get();
                        final byte value6 = byteBuffer.get();
                        final byte value7 = byteBuffer.get();
                        final int n4 = value << 18 ^ value5 << 12 ^ value6 << 6 ^ (value7 ^ 0x381F80);
                        if (isMalformed4(value5, value6, value7) || !Character.isSupplementaryCodePoint(n4)) {
                            return malformed(byteBuffer, i, 4);
                        }
                        charBuffer.put(Character.highSurrogate(n4));
                        charBuffer.put(Character.lowSurrogate(n4));
                        i += 4;
                    }
                }
            }
            return xflow(byteBuffer, i, 0);
        }
        
        @Override
        protected CoderResult decodeLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            if (byteBuffer.hasArray() && charBuffer.hasArray()) {
                return this.decodeArrayLoop(byteBuffer, charBuffer);
            }
            return this.decodeBufferLoop(byteBuffer, charBuffer);
        }
        
        private static ByteBuffer getByteBuffer(ByteBuffer wrap, final byte[] array, final int n) {
            if (wrap == null) {
                wrap = ByteBuffer.wrap(array);
            }
            wrap.position(n);
            return wrap;
        }
        
        @Override
        public int decode(final byte[] array, int i, final int n, final char[] array2) {
            final int n2 = i + n;
            int n3 = 0;
            final int min = Math.min(n, array2.length);
            ByteBuffer byteBuffer = null;
            while (n3 < min && array[i] >= 0) {
                array2[n3++] = (char)array[i++];
            }
            while (i < n2) {
                final byte b = array[i++];
                if (b >= 0) {
                    array2[n3++] = (char)b;
                }
                else if (b >> 5 == -2 && (b & 0x1E) != 0x0) {
                    if (i < n2) {
                        final byte b2 = array[i++];
                        if (isNotContinuation(b2)) {
                            if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                                return -1;
                            }
                            array2[n3++] = this.replacement().charAt(0);
                            --i;
                        }
                        else {
                            array2[n3++] = (char)(b << 6 ^ b2 ^ 0xF80);
                        }
                    }
                    else {
                        if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                            return -1;
                        }
                        array2[n3++] = this.replacement().charAt(0);
                        return n3;
                    }
                }
                else if (b >> 4 == -2) {
                    if (i + 1 < n2) {
                        final byte b3 = array[i++];
                        final byte b4 = array[i++];
                        if (isMalformed3(b, b3, b4)) {
                            if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                                return -1;
                            }
                            array2[n3++] = this.replacement().charAt(0);
                            i -= 3;
                            byteBuffer = getByteBuffer(byteBuffer, array, i);
                            i += malformedN(byteBuffer, 3).length();
                        }
                        else {
                            final char c = (char)(b << 12 ^ b3 << 6 ^ (b4 ^ 0xFFFE1F80));
                            if (Character.isSurrogate(c)) {
                                if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                                    return -1;
                                }
                                array2[n3++] = this.replacement().charAt(0);
                            }
                            else {
                                array2[n3++] = c;
                            }
                        }
                    }
                    else {
                        if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                            return -1;
                        }
                        if (i >= n2 || !isMalformed3_2(b, array[i])) {
                            array2[n3++] = this.replacement().charAt(0);
                            return n3;
                        }
                        array2[n3++] = this.replacement().charAt(0);
                    }
                }
                else if (b >> 3 == -2) {
                    if (i + 2 < n2) {
                        final byte b5 = array[i++];
                        final byte b6 = array[i++];
                        final byte b7 = array[i++];
                        final int n4 = b << 18 ^ b5 << 12 ^ b6 << 6 ^ (b7 ^ 0x381F80);
                        if (isMalformed4(b5, b6, b7) || !Character.isSupplementaryCodePoint(n4)) {
                            if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                                return -1;
                            }
                            array2[n3++] = this.replacement().charAt(0);
                            i -= 4;
                            byteBuffer = getByteBuffer(byteBuffer, array, i);
                            i += malformedN(byteBuffer, 4).length();
                        }
                        else {
                            array2[n3++] = Character.highSurrogate(n4);
                            array2[n3++] = Character.lowSurrogate(n4);
                        }
                    }
                    else {
                        if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                            return -1;
                        }
                        final int n5 = b & 0xFF;
                        if (n5 > 244 || (i < n2 && isMalformed4_2(n5, array[i] & 0xFF))) {
                            array2[n3++] = this.replacement().charAt(0);
                        }
                        else {
                            if (++i >= n2 || !isMalformed4_3(array[i])) {
                                array2[n3++] = this.replacement().charAt(0);
                                return n3;
                            }
                            array2[n3++] = this.replacement().charAt(0);
                        }
                    }
                }
                else {
                    if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                        return -1;
                    }
                    array2[n3++] = this.replacement().charAt(0);
                }
            }
            return n3;
        }
    }
    
    private static final class Encoder extends CharsetEncoder implements ArrayEncoder
    {
        private Surrogate.Parser sgp;
        private byte repl;
        
        private Encoder(final Charset charset) {
            super(charset, 1.1f, 3.0f);
            this.repl = 63;
        }
        
        @Override
        public boolean canEncode(final char c) {
            return !Character.isSurrogate(c);
        }
        
        @Override
        public boolean isLegalReplacement(final byte[] array) {
            return (array.length == 1 && array[0] >= 0) || super.isLegalReplacement(array);
        }
        
        private static CoderResult overflow(final CharBuffer charBuffer, final int n, final ByteBuffer byteBuffer, final int n2) {
            updatePositions(charBuffer, n, byteBuffer, n2);
            return CoderResult.OVERFLOW;
        }
        
        private static CoderResult overflow(final CharBuffer charBuffer, final int n) {
            charBuffer.position(n);
            return CoderResult.OVERFLOW;
        }
        
        private CoderResult encodeArrayLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            char[] array;
            int i;
            int n;
            byte[] array2;
            int n2;
            int n3;
            for (array = charBuffer.array(), i = charBuffer.arrayOffset() + charBuffer.position(), n = charBuffer.arrayOffset() + charBuffer.limit(), array2 = byteBuffer.array(), n2 = byteBuffer.arrayOffset() + byteBuffer.position(), n3 = byteBuffer.arrayOffset() + byteBuffer.limit(); n2 < n2 + Math.min(n - i, n3 - n2) && array[i] < '\u0080'; array2[n2++] = (byte)array[i++]) {}
            while (i < n) {
                final char c = array[i];
                if (c < '\u0080') {
                    if (n2 >= n3) {
                        return overflow(charBuffer, i, byteBuffer, n2);
                    }
                    array2[n2++] = (byte)c;
                }
                else if (c < '\u0800') {
                    if (n3 - n2 < 2) {
                        return overflow(charBuffer, i, byteBuffer, n2);
                    }
                    array2[n2++] = (byte)(0xC0 | c >> 6);
                    array2[n2++] = (byte)(0x80 | (c & '?'));
                }
                else if (Character.isSurrogate(c)) {
                    if (this.sgp == null) {
                        this.sgp = new Surrogate.Parser();
                    }
                    final int parse = this.sgp.parse(c, array, i, n);
                    if (parse < 0) {
                        updatePositions(charBuffer, i, byteBuffer, n2);
                        return this.sgp.error();
                    }
                    if (n3 - n2 < 4) {
                        return overflow(charBuffer, i, byteBuffer, n2);
                    }
                    array2[n2++] = (byte)(0xF0 | parse >> 18);
                    array2[n2++] = (byte)(0x80 | (parse >> 12 & 0x3F));
                    array2[n2++] = (byte)(0x80 | (parse >> 6 & 0x3F));
                    array2[n2++] = (byte)(0x80 | (parse & 0x3F));
                    ++i;
                }
                else {
                    if (n3 - n2 < 3) {
                        return overflow(charBuffer, i, byteBuffer, n2);
                    }
                    array2[n2++] = (byte)(0xE0 | c >> 12);
                    array2[n2++] = (byte)(0x80 | (c >> 6 & 0x3F));
                    array2[n2++] = (byte)(0x80 | (c & '?'));
                }
                ++i;
            }
            updatePositions(charBuffer, i, byteBuffer, n2);
            return CoderResult.UNDERFLOW;
        }
        
        private CoderResult encodeBufferLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            int position = charBuffer.position();
            while (charBuffer.hasRemaining()) {
                final char value = charBuffer.get();
                if (value < '\u0080') {
                    if (!byteBuffer.hasRemaining()) {
                        return overflow(charBuffer, position);
                    }
                    byteBuffer.put((byte)value);
                }
                else if (value < '\u0800') {
                    if (byteBuffer.remaining() < 2) {
                        return overflow(charBuffer, position);
                    }
                    byteBuffer.put((byte)(0xC0 | value >> 6));
                    byteBuffer.put((byte)(0x80 | (value & '?')));
                }
                else if (Character.isSurrogate(value)) {
                    if (this.sgp == null) {
                        this.sgp = new Surrogate.Parser();
                    }
                    final int parse = this.sgp.parse(value, charBuffer);
                    if (parse < 0) {
                        charBuffer.position(position);
                        return this.sgp.error();
                    }
                    if (byteBuffer.remaining() < 4) {
                        return overflow(charBuffer, position);
                    }
                    byteBuffer.put((byte)(0xF0 | parse >> 18));
                    byteBuffer.put((byte)(0x80 | (parse >> 12 & 0x3F)));
                    byteBuffer.put((byte)(0x80 | (parse >> 6 & 0x3F)));
                    byteBuffer.put((byte)(0x80 | (parse & 0x3F)));
                    ++position;
                }
                else {
                    if (byteBuffer.remaining() < 3) {
                        return overflow(charBuffer, position);
                    }
                    byteBuffer.put((byte)(0xE0 | value >> 12));
                    byteBuffer.put((byte)(0x80 | (value >> 6 & 0x3F)));
                    byteBuffer.put((byte)(0x80 | (value & '?')));
                }
                ++position;
            }
            charBuffer.position(position);
            return CoderResult.UNDERFLOW;
        }
        
        @Override
        protected final CoderResult encodeLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            if (charBuffer.hasArray() && byteBuffer.hasArray()) {
                return this.encodeArrayLoop(charBuffer, byteBuffer);
            }
            return this.encodeBufferLoop(charBuffer, byteBuffer);
        }
        
        @Override
        protected void implReplaceWith(final byte[] array) {
            this.repl = array[0];
        }
        
        @Override
        public int encode(final char[] array, int i, final int n, final byte[] array2) {
            final int n2 = i + n;
            int n3;
            for (n3 = 0; n3 < n3 + Math.min(n, array2.length) && array[i] < '\u0080'; array2[n3++] = (byte)array[i++]) {}
            while (i < n2) {
                final char c = array[i++];
                if (c < '\u0080') {
                    array2[n3++] = (byte)c;
                }
                else if (c < '\u0800') {
                    array2[n3++] = (byte)(0xC0 | c >> 6);
                    array2[n3++] = (byte)(0x80 | (c & '?'));
                }
                else if (Character.isSurrogate(c)) {
                    if (this.sgp == null) {
                        this.sgp = new Surrogate.Parser();
                    }
                    final int parse = this.sgp.parse(c, array, i - 1, n2);
                    if (parse < 0) {
                        if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                            return -1;
                        }
                        array2[n3++] = this.repl;
                    }
                    else {
                        array2[n3++] = (byte)(0xF0 | parse >> 18);
                        array2[n3++] = (byte)(0x80 | (parse >> 12 & 0x3F));
                        array2[n3++] = (byte)(0x80 | (parse >> 6 & 0x3F));
                        array2[n3++] = (byte)(0x80 | (parse & 0x3F));
                        ++i;
                    }
                }
                else {
                    array2[n3++] = (byte)(0xE0 | c >> 12);
                    array2[n3++] = (byte)(0x80 | (c >> 6 & 0x3F));
                    array2[n3++] = (byte)(0x80 | (c & '?'));
                }
            }
            return n3;
        }
    }
}
