package sun.nio.cs.ext;

import sun.nio.cs.Surrogate;
import sun.nio.cs.ArrayEncoder;
import java.nio.charset.CharsetEncoder;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CoderResult;
import sun.nio.cs.ArrayDecoder;
import java.nio.charset.CharsetDecoder;
import java.util.Arrays;

public class DoubleByte
{
    public static final char[] B2C_UNMAPPABLE;
    
    static {
        Arrays.fill(B2C_UNMAPPABLE = new char[256], '\ufffd');
    }
    
    public static class Decoder extends CharsetDecoder implements DelegatableDecoder, ArrayDecoder
    {
        final char[][] b2c;
        final char[] b2cSB;
        final int b2Min;
        final int b2Max;
        
        protected CoderResult crMalformedOrUnderFlow(final int n) {
            return CoderResult.UNDERFLOW;
        }
        
        protected CoderResult crMalformedOrUnmappable(final int n, final int n2) {
            if (this.b2c[n] == DoubleByte.B2C_UNMAPPABLE || this.b2c[n2] != DoubleByte.B2C_UNMAPPABLE || this.decodeSingle(n2) != '\ufffd') {
                return CoderResult.malformedForLength(1);
            }
            return CoderResult.unmappableForLength(2);
        }
        
        Decoder(final Charset charset, final float n, final float n2, final char[][] b2c, final char[] b2cSB, final int b2Min, final int b2Max) {
            super(charset, n, n2);
            this.b2c = b2c;
            this.b2cSB = b2cSB;
            this.b2Min = b2Min;
            this.b2Max = b2Max;
        }
        
        Decoder(final Charset charset, final char[][] array, final char[] array2, final int n, final int n2) {
            this(charset, 0.5f, 1.0f, array, array2, n, n2);
        }
        
        protected CoderResult decodeArrayLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            final byte[] array = byteBuffer.array();
            int n = byteBuffer.arrayOffset() + byteBuffer.position();
            final int n2 = byteBuffer.arrayOffset() + byteBuffer.limit();
            final char[] array2 = charBuffer.array();
            int n3 = charBuffer.arrayOffset() + charBuffer.position();
            final int n4 = charBuffer.arrayOffset() + charBuffer.limit();
            try {
                while (n < n2 && n3 < n4) {
                    int n5 = 1;
                    final int n6 = array[n] & 0xFF;
                    char c = this.b2cSB[n6];
                    if (c == '\ufffd') {
                        if (n2 - n < 2) {
                            return this.crMalformedOrUnderFlow(n6);
                        }
                        final int n7 = array[n + 1] & 0xFF;
                        if (n7 < this.b2Min || n7 > this.b2Max || (c = this.b2c[n6][n7 - this.b2Min]) == '\ufffd') {
                            return this.crMalformedOrUnmappable(n6, n7);
                        }
                        ++n5;
                    }
                    array2[n3++] = c;
                    n += n5;
                }
                return (n >= n2) ? CoderResult.UNDERFLOW : CoderResult.OVERFLOW;
            }
            finally {
                byteBuffer.position(n - byteBuffer.arrayOffset());
                charBuffer.position(n3 - charBuffer.arrayOffset());
            }
        }
        
        protected CoderResult decodeBufferLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            int position = byteBuffer.position();
            try {
                while (byteBuffer.hasRemaining() && charBuffer.hasRemaining()) {
                    final int n = byteBuffer.get() & 0xFF;
                    char c = this.b2cSB[n];
                    int n2 = 1;
                    if (c == '\ufffd') {
                        if (byteBuffer.remaining() < 1) {
                            return this.crMalformedOrUnderFlow(n);
                        }
                        final int n3 = byteBuffer.get() & 0xFF;
                        if (n3 < this.b2Min || n3 > this.b2Max || (c = this.b2c[n][n3 - this.b2Min]) == '\ufffd') {
                            return this.crMalformedOrUnmappable(n, n3);
                        }
                        ++n2;
                    }
                    charBuffer.put(c);
                    position += n2;
                }
                return byteBuffer.hasRemaining() ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
            }
            finally {
                byteBuffer.position(position);
            }
        }
        
        @Override
        public CoderResult decodeLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            if (byteBuffer.hasArray() && charBuffer.hasArray()) {
                return this.decodeArrayLoop(byteBuffer, charBuffer);
            }
            return this.decodeBufferLoop(byteBuffer, charBuffer);
        }
        
        @Override
        public int decode(final byte[] array, int i, final int n, final char[] array2) {
            int n2 = 0;
            final int n3 = i + n;
            final char char1 = this.replacement().charAt(0);
            while (i < n3) {
                final int n4 = array[i++] & 0xFF;
                char c = this.b2cSB[n4];
                if (c == '\ufffd') {
                    if (i < n3) {
                        final int n5 = array[i++] & 0xFF;
                        if ((n5 < this.b2Min || n5 > this.b2Max || (c = this.b2c[n4][n5 - this.b2Min]) == '\ufffd') && (this.b2c[n4] == DoubleByte.B2C_UNMAPPABLE || this.b2c[n5] != DoubleByte.B2C_UNMAPPABLE || this.decodeSingle(n5) != '\ufffd')) {
                            --i;
                        }
                    }
                    if (c == '\ufffd') {
                        c = char1;
                    }
                }
                array2[n2++] = c;
            }
            return n2;
        }
        
        @Override
        public void implReset() {
            super.implReset();
        }
        
        @Override
        public CoderResult implFlush(final CharBuffer charBuffer) {
            return super.implFlush(charBuffer);
        }
        
        public char decodeSingle(final int n) {
            return this.b2cSB[n];
        }
        
        public char decodeDouble(final int n, final int n2) {
            if (n < 0 || n > this.b2c.length || n2 < this.b2Min || n2 > this.b2Max) {
                return '\ufffd';
            }
            return this.b2c[n][n2 - this.b2Min];
        }
    }
    
    public static class Decoder_EBCDIC extends Decoder
    {
        private static final int SBCS = 0;
        private static final int DBCS = 1;
        private static final int SO = 14;
        private static final int SI = 15;
        private int currentState;
        
        Decoder_EBCDIC(final Charset charset, final char[][] array, final char[] array2, final int n, final int n2) {
            super(charset, array, array2, n, n2);
        }
        
        @Override
        public void implReset() {
            this.currentState = 0;
        }
        
        private static boolean isDoubleByte(final int n, final int n2) {
            return (65 <= n && n <= 254 && 65 <= n2 && n2 <= 254) || (n == 64 && n2 == 64);
        }
        
        @Override
        protected CoderResult decodeArrayLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            final byte[] array = byteBuffer.array();
            int i = byteBuffer.arrayOffset() + byteBuffer.position();
            final int n = byteBuffer.arrayOffset() + byteBuffer.limit();
            final char[] array2 = charBuffer.array();
            int n2 = charBuffer.arrayOffset() + charBuffer.position();
            final int n3 = charBuffer.arrayOffset() + charBuffer.limit();
            try {
                while (i < n) {
                    final int n4 = array[i] & 0xFF;
                    int n5 = 1;
                    if (n4 == 14) {
                        if (this.currentState != 0) {
                            return CoderResult.malformedForLength(1);
                        }
                        this.currentState = 1;
                    }
                    else if (n4 == 15) {
                        if (this.currentState != 1) {
                            return CoderResult.malformedForLength(1);
                        }
                        this.currentState = 0;
                    }
                    else {
                        char c;
                        if (this.currentState == 0) {
                            c = this.b2cSB[n4];
                            if (c == '\ufffd') {
                                return CoderResult.unmappableForLength(1);
                            }
                        }
                        else {
                            if (n - i < 2) {
                                return CoderResult.UNDERFLOW;
                            }
                            final int n6 = array[i + 1] & 0xFF;
                            if (n6 < this.b2Min || n6 > this.b2Max || (c = this.b2c[n4][n6 - this.b2Min]) == '\ufffd') {
                                if (!isDoubleByte(n4, n6)) {
                                    return CoderResult.malformedForLength(2);
                                }
                                return CoderResult.unmappableForLength(2);
                            }
                            else {
                                ++n5;
                            }
                        }
                        if (n3 - n2 < 1) {
                            return CoderResult.OVERFLOW;
                        }
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
        
        @Override
        protected CoderResult decodeBufferLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            int position = byteBuffer.position();
            try {
                while (byteBuffer.hasRemaining()) {
                    final int n = byteBuffer.get() & 0xFF;
                    int n2 = 1;
                    if (n == 14) {
                        if (this.currentState != 0) {
                            return CoderResult.malformedForLength(1);
                        }
                        this.currentState = 1;
                    }
                    else if (n == 15) {
                        if (this.currentState != 1) {
                            return CoderResult.malformedForLength(1);
                        }
                        this.currentState = 0;
                    }
                    else {
                        char c;
                        if (this.currentState == 0) {
                            c = this.b2cSB[n];
                            if (c == '\ufffd') {
                                return CoderResult.unmappableForLength(1);
                            }
                        }
                        else {
                            if (byteBuffer.remaining() < 1) {
                                return CoderResult.UNDERFLOW;
                            }
                            final int n3 = byteBuffer.get() & 0xFF;
                            if (n3 < this.b2Min || n3 > this.b2Max || (c = this.b2c[n][n3 - this.b2Min]) == '\ufffd') {
                                if (!isDoubleByte(n, n3)) {
                                    return CoderResult.malformedForLength(2);
                                }
                                return CoderResult.unmappableForLength(2);
                            }
                            else {
                                ++n2;
                            }
                        }
                        if (charBuffer.remaining() < 1) {
                            return CoderResult.OVERFLOW;
                        }
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
        public int decode(final byte[] array, int i, final int n, final char[] array2) {
            int n2 = 0;
            final int n3 = i + n;
            this.currentState = 0;
            final char char1 = this.replacement().charAt(0);
            while (i < n3) {
                final int n4 = array[i++] & 0xFF;
                if (n4 == 14) {
                    if (this.currentState != 0) {
                        array2[n2++] = char1;
                    }
                    else {
                        this.currentState = 1;
                    }
                }
                else if (n4 == 15) {
                    if (this.currentState != 1) {
                        array2[n2++] = char1;
                    }
                    else {
                        this.currentState = 0;
                    }
                }
                else {
                    char c;
                    if (this.currentState == 0) {
                        c = this.b2cSB[n4];
                        if (c == '\ufffd') {
                            c = char1;
                        }
                    }
                    else if (n3 == i) {
                        c = char1;
                    }
                    else {
                        final int n5 = array[i++] & 0xFF;
                        if (n5 < this.b2Min || n5 > this.b2Max || (c = this.b2c[n4][n5 - this.b2Min]) == '\ufffd') {
                            c = char1;
                        }
                    }
                    array2[n2++] = c;
                }
            }
            return n2;
        }
    }
    
    public static class Decoder_DBCSONLY extends Decoder
    {
        static final char[] b2cSB_UNMAPPABLE;
        
        Decoder_DBCSONLY(final Charset charset, final char[][] array, final char[] array2, final int n, final int n2) {
            super(charset, 0.5f, 1.0f, array, Decoder_DBCSONLY.b2cSB_UNMAPPABLE, n, n2);
        }
        
        static {
            Arrays.fill(b2cSB_UNMAPPABLE = new char[256], '\ufffd');
        }
    }
    
    public static class Decoder_EUC_SIM extends Decoder
    {
        private final int SS2 = 142;
        private final int SS3 = 143;
        
        Decoder_EUC_SIM(final Charset charset, final char[][] array, final char[] array2, final int n, final int n2) {
            super(charset, array, array2, n, n2);
        }
        
        @Override
        protected CoderResult crMalformedOrUnderFlow(final int n) {
            if (n == 142 || n == 143) {
                return CoderResult.malformedForLength(1);
            }
            return CoderResult.UNDERFLOW;
        }
        
        @Override
        protected CoderResult crMalformedOrUnmappable(final int n, final int n2) {
            if (n == 142 || n == 143) {
                return CoderResult.malformedForLength(1);
            }
            return CoderResult.unmappableForLength(2);
        }
        
        @Override
        public int decode(final byte[] array, int i, final int n, final char[] array2) {
            int n2 = 0;
            final int n3 = i + n;
            final char char1 = this.replacement().charAt(0);
            while (i < n3) {
                final int n4 = array[i++] & 0xFF;
                char c = this.b2cSB[n4];
                if (c == '\ufffd') {
                    if (i < n3) {
                        final int n5 = array[i++] & 0xFF;
                        if (n5 < this.b2Min || n5 > this.b2Max || (c = this.b2c[n4][n5 - this.b2Min]) == '\ufffd') {
                            if (n4 == 142 || n4 == 143) {
                                --i;
                            }
                            c = char1;
                        }
                    }
                    else {
                        c = char1;
                    }
                }
                array2[n2++] = c;
            }
            return n2;
        }
    }
    
    public static class Encoder extends CharsetEncoder implements ArrayEncoder
    {
        final int MAX_SINGLEBYTE = 255;
        private final char[] c2b;
        private final char[] c2bIndex;
        Surrogate.Parser sgp;
        protected byte[] repl;
        
        protected Encoder(final Charset charset, final char[] c2b, final char[] c2bIndex) {
            super(charset, 2.0f, 2.0f);
            this.repl = this.replacement();
            this.c2b = c2b;
            this.c2bIndex = c2bIndex;
        }
        
        Encoder(final Charset charset, final float n, final float n2, final byte[] array, final char[] c2b, final char[] c2bIndex) {
            super(charset, n, n2, array);
            this.repl = this.replacement();
            this.c2b = c2b;
            this.c2bIndex = c2bIndex;
        }
        
        @Override
        public boolean canEncode(final char c) {
            return this.encodeChar(c) != 65533;
        }
        
        Surrogate.Parser sgp() {
            if (this.sgp == null) {
                this.sgp = new Surrogate.Parser();
            }
            return this.sgp;
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
                    final char c = array[i];
                    final int encodeChar = this.encodeChar(c);
                    if (encodeChar == 65533) {
                        if (!Character.isSurrogate(c)) {
                            return CoderResult.unmappableForLength(1);
                        }
                        if (this.sgp().parse(c, array, i, n) < 0) {
                            return this.sgp.error();
                        }
                        return this.sgp.unmappableResult();
                    }
                    else {
                        if (encodeChar > 255) {
                            if (n3 - n2 < 2) {
                                return CoderResult.OVERFLOW;
                            }
                            array2[n2++] = (byte)(encodeChar >> 8);
                            array2[n2++] = (byte)encodeChar;
                        }
                        else {
                            if (n3 - n2 < 1) {
                                return CoderResult.OVERFLOW;
                            }
                            array2[n2++] = (byte)encodeChar;
                        }
                        ++i;
                    }
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
                    final int encodeChar = this.encodeChar(value);
                    if (encodeChar == 65533) {
                        if (!Character.isSurrogate(value)) {
                            return CoderResult.unmappableForLength(1);
                        }
                        if (this.sgp().parse(value, charBuffer) < 0) {
                            return this.sgp.error();
                        }
                        return this.sgp.unmappableResult();
                    }
                    else {
                        if (encodeChar > 255) {
                            if (byteBuffer.remaining() < 2) {
                                return CoderResult.OVERFLOW;
                            }
                            byteBuffer.put((byte)(encodeChar >> 8));
                            byteBuffer.put((byte)encodeChar);
                        }
                        else {
                            if (byteBuffer.remaining() < 1) {
                                return CoderResult.OVERFLOW;
                            }
                            byteBuffer.put((byte)encodeChar);
                        }
                        ++position;
                    }
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
        protected void implReplaceWith(final byte[] repl) {
            this.repl = repl;
        }
        
        @Override
        public int encode(final char[] array, int i, final int n, final byte[] array2) {
            int n2 = 0;
            final int n3 = i + n;
            final int length = array2.length;
            while (i < n3) {
                final char c = array[i++];
                final int encodeChar = this.encodeChar(c);
                if (encodeChar == 65533) {
                    if (Character.isHighSurrogate(c) && i < n3 && Character.isLowSurrogate(array[i])) {
                        ++i;
                    }
                    array2[n2++] = this.repl[0];
                    if (this.repl.length <= 1) {
                        continue;
                    }
                    array2[n2++] = this.repl[1];
                }
                else if (encodeChar > 255) {
                    array2[n2++] = (byte)(encodeChar >> 8);
                    array2[n2++] = (byte)encodeChar;
                }
                else {
                    array2[n2++] = (byte)encodeChar;
                }
            }
            return n2;
        }
        
        public int encodeChar(final char c) {
            return this.c2b[this.c2bIndex[c >> 8] + (c & '\u00ff')];
        }
        
        static void initC2B(final String[] array, final String s, final String s2, final String s3, final int n, final int n2, final char[] array2, final char[] array3) {
            Arrays.fill(array2, '\ufffd');
            int n3 = 256;
            final char[][] array4 = new char[array.length][];
            char[] charArray = null;
            if (s != null) {
                charArray = s.toCharArray();
            }
            for (int i = 0; i < array.length; ++i) {
                if (array[i] != null) {
                    array4[i] = array[i].toCharArray();
                }
            }
            if (s2 != null) {
                int j = 0;
                while (j < s2.length()) {
                    final char char1 = s2.charAt(j++);
                    final char char2 = s2.charAt(j++);
                    if (char1 < '\u0100' && charArray != null) {
                        if (charArray[char1] != char2) {
                            continue;
                        }
                        charArray[char1] = '\ufffd';
                    }
                    else {
                        if (array4[char1 >> 8][(char1 & '\u00ff') - n] != char2) {
                            continue;
                        }
                        array4[char1 >> 8][(char1 & '\u00ff') - n] = '\ufffd';
                    }
                }
            }
            if (charArray != null) {
                for (int k = 0; k < charArray.length; ++k) {
                    final char c = charArray[k];
                    if (c != '\ufffd') {
                        int n4 = array3[c >> 8];
                        if (n4 == 0) {
                            n4 = n3;
                            n3 += 256;
                            array3[c >> 8] = (char)n4;
                        }
                        array2[n4 + (c & '\u00ff')] = (char)k;
                    }
                }
            }
            for (int l = 0; l < array.length; ++l) {
                final char[] array5 = array4[l];
                if (array5 != null) {
                    for (int n5 = n; n5 <= n2; ++n5) {
                        final char c2 = array5[n5 - n];
                        if (c2 != '\ufffd') {
                            int n6 = array3[c2 >> 8];
                            if (n6 == 0) {
                                n6 = n3;
                                n3 += 256;
                                array3[c2 >> 8] = (char)n6;
                            }
                            array2[n6 + (c2 & '\u00ff')] = (char)(l << 8 | n5);
                        }
                    }
                }
            }
            if (s3 != null) {
                for (int n7 = 0; n7 < s3.length(); n7 += 2) {
                    final char char3 = s3.charAt(n7);
                    final char char4 = s3.charAt(n7 + 1);
                    final int n8 = char4 >> 8;
                    if (array3[n8] == '\0') {
                        array3[n8] = (char)n3;
                        n3 += 256;
                    }
                    array2[array3[n8] + (char4 & '\u00ff')] = char3;
                }
            }
        }
    }
    
    public static class Encoder_DBCSONLY extends Encoder
    {
        Encoder_DBCSONLY(final Charset charset, final byte[] array, final char[] array2, final char[] array3) {
            super(charset, 2.0f, 2.0f, array, array2, array3);
        }
        
        @Override
        public int encodeChar(final char c) {
            final int encodeChar = super.encodeChar(c);
            if (encodeChar <= 255) {
                return 65533;
            }
            return encodeChar;
        }
    }
    
    public static class Encoder_EBCDIC extends Encoder
    {
        static final int SBCS = 0;
        static final int DBCS = 1;
        static final byte SO = 14;
        static final byte SI = 15;
        protected int currentState;
        
        Encoder_EBCDIC(final Charset charset, final char[] array, final char[] array2) {
            super(charset, 4.0f, 5.0f, new byte[] { 111 }, array, array2);
            this.currentState = 0;
        }
        
        @Override
        protected void implReset() {
            this.currentState = 0;
        }
        
        @Override
        protected CoderResult implFlush(final ByteBuffer byteBuffer) {
            if (this.currentState == 1) {
                if (byteBuffer.remaining() < 1) {
                    return CoderResult.OVERFLOW;
                }
                byteBuffer.put((byte)15);
            }
            this.implReset();
            return CoderResult.UNDERFLOW;
        }
        
        @Override
        protected CoderResult encodeArrayLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            final char[] array = charBuffer.array();
            int i = charBuffer.arrayOffset() + charBuffer.position();
            final int n = charBuffer.arrayOffset() + charBuffer.limit();
            final byte[] array2 = byteBuffer.array();
            int n2 = byteBuffer.arrayOffset() + byteBuffer.position();
            final int n3 = byteBuffer.arrayOffset() + byteBuffer.limit();
            try {
                while (i < n) {
                    final char c = array[i];
                    final int encodeChar = this.encodeChar(c);
                    if (encodeChar == 65533) {
                        if (!Character.isSurrogate(c)) {
                            return CoderResult.unmappableForLength(1);
                        }
                        if (this.sgp().parse(c, array, i, n) < 0) {
                            return this.sgp.error();
                        }
                        return this.sgp.unmappableResult();
                    }
                    else {
                        if (encodeChar > 255) {
                            if (this.currentState == 0) {
                                if (n3 - n2 < 1) {
                                    return CoderResult.OVERFLOW;
                                }
                                this.currentState = 1;
                                array2[n2++] = 14;
                            }
                            if (n3 - n2 < 2) {
                                return CoderResult.OVERFLOW;
                            }
                            array2[n2++] = (byte)(encodeChar >> 8);
                            array2[n2++] = (byte)encodeChar;
                        }
                        else {
                            if (this.currentState == 1) {
                                if (n3 - n2 < 1) {
                                    return CoderResult.OVERFLOW;
                                }
                                this.currentState = 0;
                                array2[n2++] = 15;
                            }
                            if (n3 - n2 < 1) {
                                return CoderResult.OVERFLOW;
                            }
                            array2[n2++] = (byte)encodeChar;
                        }
                        ++i;
                    }
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                charBuffer.position(i - charBuffer.arrayOffset());
                byteBuffer.position(n2 - byteBuffer.arrayOffset());
            }
        }
        
        @Override
        protected CoderResult encodeBufferLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            int position = charBuffer.position();
            try {
                while (charBuffer.hasRemaining()) {
                    final char value = charBuffer.get();
                    final int encodeChar = this.encodeChar(value);
                    if (encodeChar == 65533) {
                        if (!Character.isSurrogate(value)) {
                            return CoderResult.unmappableForLength(1);
                        }
                        if (this.sgp().parse(value, charBuffer) < 0) {
                            return this.sgp.error();
                        }
                        return this.sgp.unmappableResult();
                    }
                    else {
                        if (encodeChar > 255) {
                            if (this.currentState == 0) {
                                if (byteBuffer.remaining() < 1) {
                                    return CoderResult.OVERFLOW;
                                }
                                this.currentState = 1;
                                byteBuffer.put((byte)14);
                            }
                            if (byteBuffer.remaining() < 2) {
                                return CoderResult.OVERFLOW;
                            }
                            byteBuffer.put((byte)(encodeChar >> 8));
                            byteBuffer.put((byte)encodeChar);
                        }
                        else {
                            if (this.currentState == 1) {
                                if (byteBuffer.remaining() < 1) {
                                    return CoderResult.OVERFLOW;
                                }
                                this.currentState = 0;
                                byteBuffer.put((byte)15);
                            }
                            if (byteBuffer.remaining() < 1) {
                                return CoderResult.OVERFLOW;
                            }
                            byteBuffer.put((byte)encodeChar);
                        }
                        ++position;
                    }
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                charBuffer.position(position);
            }
        }
        
        @Override
        public int encode(final char[] array, int i, final int n, final byte[] array2) {
            int n2 = 0;
            final int n3 = i + n;
            while (i < n3) {
                final char c = array[i++];
                final int encodeChar = this.encodeChar(c);
                if (encodeChar == 65533) {
                    if (Character.isHighSurrogate(c) && i < n3 && Character.isLowSurrogate(array[i])) {
                        ++i;
                    }
                    array2[n2++] = this.repl[0];
                    if (this.repl.length <= 1) {
                        continue;
                    }
                    array2[n2++] = this.repl[1];
                }
                else if (encodeChar > 255) {
                    if (this.currentState == 0) {
                        this.currentState = 1;
                        array2[n2++] = 14;
                    }
                    array2[n2++] = (byte)(encodeChar >> 8);
                    array2[n2++] = (byte)encodeChar;
                }
                else {
                    if (this.currentState == 1) {
                        this.currentState = 0;
                        array2[n2++] = 15;
                    }
                    array2[n2++] = (byte)encodeChar;
                }
            }
            if (this.currentState == 1) {
                this.currentState = 0;
                array2[n2++] = 15;
            }
            return n2;
        }
    }
    
    public static class Encoder_EUC_SIM extends Encoder
    {
        Encoder_EUC_SIM(final Charset charset, final char[] array, final char[] array2) {
            super(charset, array, array2);
        }
    }
}
