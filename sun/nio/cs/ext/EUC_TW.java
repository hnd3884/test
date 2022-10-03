package sun.nio.cs.ext;

import java.util.Arrays;
import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class EUC_TW extends Charset implements HistoricallyNamedCharset
{
    private static final int SS2 = 142;
    
    public EUC_TW() {
        super("x-EUC-TW", ExtendedCharsets.aliasesFor("x-EUC-TW"));
    }
    
    @Override
    public String historicalName() {
        return "EUC_TW";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof EUC_TW;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }
    
    public static class Decoder extends CharsetDecoder
    {
        char[] c1;
        char[] c2;
        static final String[] b2c;
        static final int b1Min = 161;
        static final int b1Max = 254;
        static final int b2Min = 161;
        static final int b2Max = 254;
        static final int dbSegSize = 94;
        static final byte[] b2cIsSupp;
        static final byte[] cnspToIndex;
        
        public Decoder(final Charset charset) {
            super(charset, 2.0f, 2.0f);
            this.c1 = new char[1];
            this.c2 = new char[2];
        }
        
        public char[] toUnicode(final int n, final int n2, final int n3) {
            return decode(n, n2, n3, this.c1, this.c2);
        }
        
        static boolean isLegalDB(final int n) {
            return n >= 161 && n <= 254;
        }
        
        static char[] decode(final int n, final int n2, final int n3, final char[] array, final char[] array2) {
            if (n < 161 || n > 254 || n2 < 161 || n2 > 254) {
                return null;
            }
            final int n4 = (n - 161) * 94 + n2 - 161;
            final char char1 = Decoder.b2c[n3].charAt(n4);
            if (char1 == '\ufffd') {
                return null;
            }
            if ((Decoder.b2cIsSupp[n4] & 1 << n3) == 0x0) {
                array[0] = char1;
                return array;
            }
            array2[0] = Character.highSurrogate(131072 + char1);
            array2[1] = Character.lowSurrogate(131072 + char1);
            return array2;
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
                    if (n4 == 142) {
                        if (n - i < 4) {
                            return CoderResult.UNDERFLOW;
                        }
                        final byte b = Decoder.cnspToIndex[array[i + 1] & 0xFF];
                        if (b < 0) {
                            return CoderResult.malformedForLength(2);
                        }
                        final int n5 = array[i + 2] & 0xFF;
                        final int n6 = array[i + 3] & 0xFF;
                        final char[] unicode = this.toUnicode(n5, n6, b);
                        if (unicode == null) {
                            if (!isLegalDB(n5) || !isLegalDB(n6)) {
                                return CoderResult.malformedForLength(4);
                            }
                            return CoderResult.unmappableForLength(4);
                        }
                        else {
                            if (n3 - n2 < unicode.length) {
                                return CoderResult.OVERFLOW;
                            }
                            if (unicode.length == 1) {
                                array2[n2++] = unicode[0];
                            }
                            else {
                                array2[n2++] = unicode[0];
                                array2[n2++] = unicode[1];
                            }
                            i += 4;
                        }
                    }
                    else if (n4 < 128) {
                        if (n3 - n2 < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        array2[n2++] = (char)n4;
                        ++i;
                    }
                    else {
                        if (n - i < 2) {
                            return CoderResult.UNDERFLOW;
                        }
                        final int n7 = array[i + 1] & 0xFF;
                        final char[] unicode2 = this.toUnicode(n4, n7, 0);
                        if (unicode2 == null) {
                            if (!isLegalDB(n4) || !isLegalDB(n7)) {
                                return CoderResult.malformedForLength(1);
                            }
                            return CoderResult.unmappableForLength(2);
                        }
                        else {
                            if (n3 - n2 < 1) {
                                return CoderResult.OVERFLOW;
                            }
                            array2[n2++] = unicode2[0];
                            i += 2;
                        }
                    }
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
                    final int n = byteBuffer.get() & 0xFF;
                    if (n == 142) {
                        if (byteBuffer.remaining() < 3) {
                            return CoderResult.UNDERFLOW;
                        }
                        final byte b = Decoder.cnspToIndex[byteBuffer.get() & 0xFF];
                        if (b < 0) {
                            return CoderResult.malformedForLength(2);
                        }
                        final int n2 = byteBuffer.get() & 0xFF;
                        final int n3 = byteBuffer.get() & 0xFF;
                        final char[] unicode = this.toUnicode(n2, n3, b);
                        if (unicode == null) {
                            if (!isLegalDB(n2) || !isLegalDB(n3)) {
                                return CoderResult.malformedForLength(4);
                            }
                            return CoderResult.unmappableForLength(4);
                        }
                        else {
                            if (charBuffer.remaining() < unicode.length) {
                                return CoderResult.OVERFLOW;
                            }
                            if (unicode.length == 1) {
                                charBuffer.put(unicode[0]);
                            }
                            else {
                                charBuffer.put(unicode[0]);
                                charBuffer.put(unicode[1]);
                            }
                            position += 4;
                        }
                    }
                    else if (n < 128) {
                        if (!charBuffer.hasRemaining()) {
                            return CoderResult.OVERFLOW;
                        }
                        charBuffer.put((char)n);
                        ++position;
                    }
                    else {
                        if (!byteBuffer.hasRemaining()) {
                            return CoderResult.UNDERFLOW;
                        }
                        final int n4 = byteBuffer.get() & 0xFF;
                        final char[] unicode2 = this.toUnicode(n, n4, 0);
                        if (unicode2 == null) {
                            if (!isLegalDB(n) || !isLegalDB(n4)) {
                                return CoderResult.malformedForLength(1);
                            }
                            return CoderResult.unmappableForLength(2);
                        }
                        else {
                            if (!charBuffer.hasRemaining()) {
                                return CoderResult.OVERFLOW;
                            }
                            charBuffer.put(unicode2[0]);
                            position += 2;
                        }
                    }
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
        
        static {
            b2c = EUC_TWMapping.b2c;
            Arrays.fill(cnspToIndex = new byte[256], (byte)(-1));
            Decoder.cnspToIndex[162] = 1;
            Decoder.cnspToIndex[163] = 2;
            Decoder.cnspToIndex[164] = 3;
            Decoder.cnspToIndex[165] = 4;
            Decoder.cnspToIndex[166] = 5;
            Decoder.cnspToIndex[167] = 6;
            Decoder.cnspToIndex[175] = 7;
            final String b2cIsSuppStr = EUC_TWMapping.b2cIsSuppStr;
            final byte[] b2cIsSupp2 = new byte[b2cIsSuppStr.length() << 1];
            int n = 0;
            for (int i = 0; i < b2cIsSuppStr.length(); ++i) {
                final char char1 = b2cIsSuppStr.charAt(i);
                b2cIsSupp2[n++] = (byte)(char1 >> 8);
                b2cIsSupp2[n++] = (byte)(char1 & '\u00ff');
            }
            b2cIsSupp = b2cIsSupp2;
        }
    }
    
    public static class Encoder extends CharsetEncoder
    {
        private byte[] bb;
        static final char[] c2b;
        static final char[] c2bIndex;
        static final char[] c2bSupp;
        static final char[] c2bSuppIndex;
        static final byte[] c2bPlane;
        
        public Encoder(final Charset charset) {
            super(charset, 4.0f, 4.0f);
            this.bb = new byte[4];
        }
        
        @Override
        public boolean canEncode(final char c) {
            return c <= '\u007f' || this.toEUC(c, this.bb) != -1;
        }
        
        @Override
        public boolean canEncode(final CharSequence charSequence) {
            int i = 0;
            while (i < charSequence.length()) {
                final char char1 = charSequence.charAt(i++);
                if (Character.isHighSurrogate(char1)) {
                    if (i == charSequence.length()) {
                        return false;
                    }
                    final char char2 = charSequence.charAt(i++);
                    if (!Character.isLowSurrogate(char2) || this.toEUC(char1, char2, this.bb) == -1) {
                        return false;
                    }
                    continue;
                }
                else {
                    if (!this.canEncode(char1)) {
                        return false;
                    }
                    continue;
                }
            }
            return true;
        }
        
        public int toEUC(final char c, final char c2, final byte[] array) {
            return encode(c, c2, array);
        }
        
        public int toEUC(final char c, final byte[] array) {
            return encode(c, array);
        }
        
        private CoderResult encodeArrayLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            final char[] array = charBuffer.array();
            int i = charBuffer.arrayOffset() + charBuffer.position();
            final int n = charBuffer.arrayOffset() + charBuffer.limit();
            final byte[] array2 = byteBuffer.array();
            int n2 = byteBuffer.arrayOffset() + byteBuffer.position();
            final int n3 = byteBuffer.arrayOffset() + byteBuffer.limit();
            try {
                while (i < n) {
                    final char c = array[i];
                    int n4 = 1;
                    int n5;
                    if (c < '\u0080') {
                        this.bb[0] = (byte)c;
                        n5 = 1;
                    }
                    else {
                        n5 = this.toEUC(c, this.bb);
                        if (n5 == -1) {
                            if (Character.isHighSurrogate(c)) {
                                if (i + 1 == n) {
                                    return CoderResult.UNDERFLOW;
                                }
                                if (!Character.isLowSurrogate(array[i + 1])) {
                                    return CoderResult.malformedForLength(1);
                                }
                                n5 = this.toEUC(c, array[i + 1], this.bb);
                                n4 = 2;
                            }
                            else if (Character.isLowSurrogate(c)) {
                                return CoderResult.malformedForLength(1);
                            }
                        }
                    }
                    if (n5 == -1) {
                        return CoderResult.unmappableForLength(n4);
                    }
                    if (n3 - n2 < n5) {
                        return CoderResult.OVERFLOW;
                    }
                    for (int j = 0; j < n5; ++j) {
                        array2[n2++] = this.bb[j];
                    }
                    i += n4;
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                charBuffer.position(i - charBuffer.arrayOffset());
                byteBuffer.position(n2 - byteBuffer.arrayOffset());
            }
        }
        
        private CoderResult encodeBufferLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            int position = charBuffer.position();
            try {
                while (charBuffer.hasRemaining()) {
                    int n = 1;
                    final char value = charBuffer.get();
                    int n2;
                    if (value < '\u0080') {
                        n2 = 1;
                        this.bb[0] = (byte)value;
                    }
                    else {
                        n2 = this.toEUC(value, this.bb);
                        if (n2 == -1) {
                            if (Character.isHighSurrogate(value)) {
                                if (!charBuffer.hasRemaining()) {
                                    return CoderResult.UNDERFLOW;
                                }
                                final char value2 = charBuffer.get();
                                if (!Character.isLowSurrogate(value2)) {
                                    return CoderResult.malformedForLength(1);
                                }
                                n2 = this.toEUC(value, value2, this.bb);
                                n = 2;
                            }
                            else if (Character.isLowSurrogate(value)) {
                                return CoderResult.malformedForLength(1);
                            }
                        }
                    }
                    if (n2 == -1) {
                        return CoderResult.unmappableForLength(n);
                    }
                    if (byteBuffer.remaining() < n2) {
                        return CoderResult.OVERFLOW;
                    }
                    for (int i = 0; i < n2; ++i) {
                        byteBuffer.put(this.bb[i]);
                    }
                    position += n;
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
        
        static int encode(final char c, final char c2, final byte[] array) {
            final int codePoint = Character.toCodePoint(c, c2);
            if ((codePoint & 0xF0000) != 0x20000) {
                return -1;
            }
            final int n = codePoint - 131072;
            final char c3 = Encoder.c2bSuppIndex[n >> 8];
            if (c3 == '\ufffd') {
                return -1;
            }
            final int n2 = c3 + (n & 0xFF);
            final char c4 = Encoder.c2bSupp[n2];
            if (c4 == '\ufffd') {
                return -1;
            }
            final int n3 = Encoder.c2bPlane[n2] >> 4 & 0xF;
            array[0] = -114;
            array[1] = (byte)(0xA0 | n3);
            array[2] = (byte)(c4 >> 8);
            array[3] = (byte)c4;
            return 4;
        }
        
        static int encode(final char c, final byte[] array) {
            final char c2 = Encoder.c2bIndex[c >> 8];
            if (c2 == '\ufffd') {
                return -1;
            }
            final int n = c2 + (c & '\u00ff');
            final char c3 = Encoder.c2b[n];
            if (c3 == '\ufffd') {
                return -1;
            }
            final int n2 = Encoder.c2bPlane[n] & 0xF;
            if (n2 == 0) {
                array[0] = (byte)(c3 >> 8);
                array[1] = (byte)c3;
                return 2;
            }
            array[0] = -114;
            array[1] = (byte)(0xA0 | n2);
            array[2] = (byte)(c3 >> 8);
            array[3] = (byte)c3;
            return 4;
        }
        
        static {
            final int n = 161;
            final int n2 = 254;
            final int n3 = 161;
            final int n4 = 254;
            final String[] b2c = Decoder.b2c;
            final byte[] b2cIsSupp = Decoder.b2cIsSupp;
            c2bIndex = EUC_TWMapping.c2bIndex;
            c2bSuppIndex = EUC_TWMapping.c2bSuppIndex;
            final char[] c2b2 = new char[31744];
            final char[] c2bSupp2 = new char[43520];
            final byte[] c2bPlane2 = new byte[Math.max(31744, 43520)];
            Arrays.fill(c2b2, '\ufffd');
            Arrays.fill(c2bSupp2, '\ufffd');
            for (int i = 0; i < b2c.length; ++i) {
                final String s = b2c[i];
                int n5 = i;
                if (n5 == 7) {
                    n5 = 15;
                }
                else if (n5 != 0) {
                    n5 = i + 1;
                }
                int n6 = 0;
                for (int j = n; j <= n2; ++j) {
                    for (int k = n3; k <= n4; ++k) {
                        final char char1 = s.charAt(n6);
                        if (char1 != '\ufffd') {
                            if ((b2cIsSupp[n6] & 1 << i) != 0x0) {
                                final int n7 = Encoder.c2bSuppIndex[char1 >> 8] + (char1 & '\u00ff');
                                c2bSupp2[n7] = (char)((j << 8) + k);
                                final byte[] array = c2bPlane2;
                                final int n8 = n7;
                                array[n8] |= (byte)(n5 << 4);
                            }
                            else {
                                final int n9 = Encoder.c2bIndex[char1 >> 8] + (char1 & '\u00ff');
                                c2b2[n9] = (char)((j << 8) + k);
                                final byte[] array2 = c2bPlane2;
                                final int n10 = n9;
                                array2[n10] |= (byte)n5;
                            }
                        }
                        ++n6;
                    }
                }
            }
            c2b = c2b2;
            c2bSupp = c2bSupp2;
            c2bPlane = c2bPlane2;
        }
    }
}
