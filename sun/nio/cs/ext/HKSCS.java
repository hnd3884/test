package sun.nio.cs.ext;

import java.util.Arrays;
import sun.nio.cs.Surrogate;
import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class HKSCS
{
    public static class Decoder extends DoubleByte.Decoder
    {
        static int b2Min;
        static int b2Max;
        private char[][] b2cBmp;
        private char[][] b2cSupp;
        private DoubleByte.Decoder big5Dec;
        
        protected Decoder(final Charset charset, final DoubleByte.Decoder big5Dec, final char[][] b2cBmp, final char[][] b2cSupp) {
            super(charset, 0.5f, 1.0f, null, null, 0, 0);
            this.big5Dec = big5Dec;
            this.b2cBmp = b2cBmp;
            this.b2cSupp = b2cSupp;
        }
        
        @Override
        public char decodeSingle(final int n) {
            return this.big5Dec.decodeSingle(n);
        }
        
        public char decodeBig5(final int n, final int n2) {
            return this.big5Dec.decodeDouble(n, n2);
        }
        
        @Override
        public char decodeDouble(final int n, final int n2) {
            return this.b2cBmp[n][n2 - Decoder.b2Min];
        }
        
        public char decodeDoubleEx(final int n, final int n2) {
            return this.b2cSupp[n][n2 - Decoder.b2Min];
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
                    char c = this.decodeSingle(n4);
                    int n5 = 1;
                    int n6 = 1;
                    if (c == '\ufffd') {
                        if (n - i < 2) {
                            return CoderResult.UNDERFLOW;
                        }
                        final int n7 = array[i + 1] & 0xFF;
                        ++n5;
                        if (n7 < Decoder.b2Min || n7 > Decoder.b2Max) {
                            return CoderResult.unmappableForLength(2);
                        }
                        c = this.decodeDouble(n4, n7);
                        if (c == '\ufffd') {
                            c = this.decodeDoubleEx(n4, n7);
                            if (c == '\ufffd') {
                                c = this.decodeBig5(n4, n7);
                                if (c == '\ufffd') {
                                    return CoderResult.unmappableForLength(2);
                                }
                            }
                            else {
                                n6 = 2;
                            }
                        }
                    }
                    if (n3 - n2 < n6) {
                        return CoderResult.OVERFLOW;
                    }
                    if (n6 == 2) {
                        array2[n2++] = Surrogate.high(131072 + c);
                        array2[n2++] = Surrogate.low(131072 + c);
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
        
        @Override
        protected CoderResult decodeBufferLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            int position = byteBuffer.position();
            try {
                while (byteBuffer.hasRemaining()) {
                    final int n = byteBuffer.get() & 0xFF;
                    int n2 = 1;
                    int n3 = 1;
                    char c = this.decodeSingle(n);
                    if (c == '\ufffd') {
                        if (byteBuffer.remaining() < 1) {
                            return CoderResult.UNDERFLOW;
                        }
                        final int n4 = byteBuffer.get() & 0xFF;
                        ++n2;
                        if (n4 < Decoder.b2Min || n4 > Decoder.b2Max) {
                            return CoderResult.unmappableForLength(2);
                        }
                        c = this.decodeDouble(n, n4);
                        if (c == '\ufffd') {
                            c = this.decodeDoubleEx(n, n4);
                            if (c == '\ufffd') {
                                c = this.decodeBig5(n, n4);
                                if (c == '\ufffd') {
                                    return CoderResult.unmappableForLength(2);
                                }
                            }
                            else {
                                n3 = 2;
                            }
                        }
                    }
                    if (charBuffer.remaining() < n3) {
                        return CoderResult.OVERFLOW;
                    }
                    if (n3 == 2) {
                        charBuffer.put(Surrogate.high(131072 + c));
                        charBuffer.put(Surrogate.low(131072 + c));
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
        public int decode(final byte[] array, int i, final int n, final char[] array2) {
            int n2 = 0;
            final int n3 = i + n;
            final char char1 = this.replacement().charAt(0);
            while (i < n3) {
                final int n4 = array[i++] & 0xFF;
                char c = this.decodeSingle(n4);
                if (c == '\ufffd') {
                    if (n3 == i) {
                        c = char1;
                    }
                    else {
                        final int n5 = array[i++] & 0xFF;
                        if (n5 < Decoder.b2Min || n5 > Decoder.b2Max) {
                            c = char1;
                        }
                        else if ((c = this.decodeDouble(n4, n5)) == '\ufffd') {
                            final char decodeDoubleEx = this.decodeDoubleEx(n4, n5);
                            if (decodeDoubleEx != '\ufffd') {
                                array2[n2++] = Surrogate.high(131072 + decodeDoubleEx);
                                array2[n2++] = Surrogate.low(131072 + decodeDoubleEx);
                                continue;
                            }
                            c = this.decodeBig5(n4, n5);
                            if (c == '\ufffd') {
                                c = char1;
                            }
                        }
                    }
                }
                array2[n2++] = c;
            }
            return n2;
        }
        
        @Override
        public CoderResult decodeLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            if (byteBuffer.hasArray() && charBuffer.hasArray()) {
                return this.decodeArrayLoop(byteBuffer, charBuffer);
            }
            return this.decodeBufferLoop(byteBuffer, charBuffer);
        }
        
        static void initb2c(final char[][] array, final String[] array2) {
            for (int i = 0; i < array2.length; ++i) {
                if (array2[i] == null) {
                    array[i] = DoubleByte.B2C_UNMAPPABLE;
                }
                else {
                    array[i] = array2[i].toCharArray();
                }
            }
        }
        
        static {
            Decoder.b2Min = 64;
            Decoder.b2Max = 254;
        }
    }
    
    public static class Encoder extends DoubleByte.Encoder
    {
        private DoubleByte.Encoder big5Enc;
        private char[][] c2bBmp;
        private char[][] c2bSupp;
        private byte[] repl;
        static char[] C2B_UNMAPPABLE;
        
        protected Encoder(final Charset charset, final DoubleByte.Encoder big5Enc, final char[][] c2bBmp, final char[][] c2bSupp) {
            super(charset, null, null);
            this.repl = this.replacement();
            this.big5Enc = big5Enc;
            this.c2bBmp = c2bBmp;
            this.c2bSupp = c2bSupp;
        }
        
        public int encodeBig5(final char c) {
            return this.big5Enc.encodeChar(c);
        }
        
        @Override
        public int encodeChar(final char c) {
            final char c2 = this.c2bBmp[c >> 8][c & '\u00ff'];
            if (c2 == '\ufffd') {
                return this.encodeBig5(c);
            }
            return c2;
        }
        
        public int encodeSupp(final int n) {
            if ((n & 0xF0000) != 0x20000) {
                return 65533;
            }
            return this.c2bSupp[n >> 8 & 0xFF][n & 0xFF];
        }
        
        @Override
        public boolean canEncode(final char c) {
            return this.encodeChar(c) != 65533;
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
                    int n4 = 1;
                    int n5 = this.encodeChar(c);
                    if (n5 == 65533) {
                        if (!Character.isSurrogate(c)) {
                            return CoderResult.unmappableForLength(1);
                        }
                        final int parse;
                        if ((parse = this.sgp().parse(c, array, i, n)) < 0) {
                            return this.sgp.error();
                        }
                        n5 = this.encodeSupp(parse);
                        if (n5 == 65533) {
                            return CoderResult.unmappableForLength(2);
                        }
                        n4 = 2;
                    }
                    if (n5 > 255) {
                        if (n3 - n2 < 2) {
                            return CoderResult.OVERFLOW;
                        }
                        array2[n2++] = (byte)(n5 >> 8);
                        array2[n2++] = (byte)n5;
                    }
                    else {
                        if (n3 - n2 < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        array2[n2++] = (byte)n5;
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
        
        @Override
        protected CoderResult encodeBufferLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            int position = charBuffer.position();
            try {
                while (charBuffer.hasRemaining()) {
                    int n = 1;
                    final char value = charBuffer.get();
                    int n2 = this.encodeChar(value);
                    if (n2 == 65533) {
                        if (!Character.isSurrogate(value)) {
                            return CoderResult.unmappableForLength(1);
                        }
                        final int parse;
                        if ((parse = this.sgp().parse(value, charBuffer)) < 0) {
                            return this.sgp.error();
                        }
                        n2 = this.encodeSupp(parse);
                        if (n2 == 65533) {
                            return CoderResult.unmappableForLength(2);
                        }
                        n = 2;
                    }
                    if (n2 > 255) {
                        if (byteBuffer.remaining() < 2) {
                            return CoderResult.OVERFLOW;
                        }
                        byteBuffer.put((byte)(n2 >> 8));
                        byteBuffer.put((byte)n2);
                    }
                    else {
                        if (byteBuffer.remaining() < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        byteBuffer.put((byte)n2);
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
        
        @Override
        protected void implReplaceWith(final byte[] repl) {
            this.repl = repl;
        }
        
        @Override
        public int encode(final char[] array, int i, final int n, final byte[] array2) {
            int n2 = 0;
            final int n3 = i + n;
            while (i < n3) {
                final char c = array[i++];
                int n4 = this.encodeChar(c);
                if (n4 == 65533 && (!Character.isHighSurrogate(c) || i == n3 || !Character.isLowSurrogate(array[i]) || (n4 = this.encodeSupp(Character.toCodePoint(c, array[i++]))) == 65533)) {
                    array2[n2++] = this.repl[0];
                    if (this.repl.length <= 1) {
                        continue;
                    }
                    array2[n2++] = this.repl[1];
                }
                else if (n4 > 255) {
                    array2[n2++] = (byte)(n4 >> 8);
                    array2[n2++] = (byte)n4;
                }
                else {
                    array2[n2++] = (byte)n4;
                }
            }
            return n2;
        }
        
        static void initc2b(final char[][] array, final String[] array2, final String s) {
            final int n = 64;
            Arrays.fill(array, Encoder.C2B_UNMAPPABLE);
            for (int i = 0; i < 256; ++i) {
                final String s2 = array2[i];
                if (s2 != null) {
                    for (int j = 0; j < s2.length(); ++j) {
                        final char char1 = s2.charAt(j);
                        final int n2 = char1 >> 8;
                        if (array[n2] == Encoder.C2B_UNMAPPABLE) {
                            Arrays.fill(array[n2] = new char[256], '\ufffd');
                        }
                        array[n2][char1 & '\u00ff'] = (char)(i << 8 | j + n);
                    }
                }
            }
            if (s != null) {
                int n3 = 57344;
                for (int k = 0; k < s.length(); ++k) {
                    final char char2 = s.charAt(k);
                    if (char2 != '\ufffd') {
                        final int n4 = n3 >> 8;
                        if (array[n4] == Encoder.C2B_UNMAPPABLE) {
                            Arrays.fill(array[n4] = new char[256], '\ufffd');
                        }
                        array[n4][n3 & 0xFF] = char2;
                    }
                    n3 = (char)(n3 + 1);
                }
            }
        }
        
        static {
            Arrays.fill(Encoder.C2B_UNMAPPABLE = new char[256], '\ufffd');
        }
    }
}
