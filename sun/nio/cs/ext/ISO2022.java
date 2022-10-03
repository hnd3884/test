package sun.nio.cs.ext;

import sun.nio.cs.Surrogate;
import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

abstract class ISO2022 extends Charset
{
    private static final byte ISO_ESC = 27;
    private static final byte ISO_SI = 15;
    private static final byte ISO_SO = 14;
    private static final byte ISO_SS2_7 = 78;
    private static final byte ISO_SS3_7 = 79;
    private static final byte MSB = Byte.MIN_VALUE;
    private static final char REPLACE_CHAR = '\ufffd';
    private static final byte minDesignatorLength = 3;
    
    public ISO2022(final String s, final String[] array) {
        super(s, array);
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder(this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder(this);
    }
    
    protected static class Decoder extends CharsetDecoder
    {
        protected byte[][] SODesig;
        protected byte[][] SS2Desig;
        protected byte[][] SS3Desig;
        protected CharsetDecoder[] SODecoder;
        protected CharsetDecoder[] SS2Decoder;
        protected CharsetDecoder[] SS3Decoder;
        private static final byte SOFlag = 0;
        private static final byte SS2Flag = 1;
        private static final byte SS3Flag = 2;
        private int curSODes;
        private int curSS2Des;
        private int curSS3Des;
        private boolean shiftout;
        private CharsetDecoder[] tmpDecoder;
        
        protected Decoder(final Charset charset) {
            super(charset, 1.0f, 1.0f);
            this.SS2Desig = null;
            this.SS3Desig = null;
            this.SS2Decoder = null;
            this.SS3Decoder = null;
        }
        
        @Override
        protected void implReset() {
            this.curSODes = 0;
            this.curSS2Des = 0;
            this.curSS3Des = 0;
            this.shiftout = false;
        }
        
        private char decode(final byte b, final byte b2, final byte b3) {
            final byte[] array = { (byte)(b | 0xFFFFFF80), (byte)(b2 | 0xFFFFFF80) };
            final char[] array2 = { '\0' };
            int n = 0;
            switch (b3) {
                case 0: {
                    n = this.curSODes;
                    this.tmpDecoder = this.SODecoder;
                    break;
                }
                case 1: {
                    n = this.curSS2Des;
                    this.tmpDecoder = this.SS2Decoder;
                    break;
                }
                case 2: {
                    n = this.curSS3Des;
                    this.tmpDecoder = this.SS3Decoder;
                    break;
                }
            }
            if (this.tmpDecoder != null) {
                for (int i = 0; i < this.tmpDecoder.length; ++i) {
                    if (n == i) {
                        try {
                            final ByteBuffer wrap = ByteBuffer.wrap(array, 0, 2);
                            final CharBuffer wrap2 = CharBuffer.wrap(array2, 0, 1);
                            this.tmpDecoder[i].decode(wrap, wrap2, true);
                            wrap2.flip();
                            return wrap2.get();
                        }
                        catch (final Exception ex) {}
                    }
                }
            }
            return '\ufffd';
        }
        
        private int findDesig(final byte[] array, final int n, final int n2, final byte[][] array2) {
            if (array2 == null) {
                return -1;
            }
            for (int i = 0; i < array2.length; ++i) {
                if (array2[i] != null && n2 - n >= array2[i].length) {
                    int n3;
                    for (n3 = 0; n3 < array2[i].length && array[n + n3] == array2[i][n3]; ++n3) {}
                    if (n3 == array2[i].length) {
                        return i;
                    }
                }
            }
            return -1;
        }
        
        private int findDesigBuf(final ByteBuffer byteBuffer, final byte[][] array) {
            if (array == null) {
                return -1;
            }
            for (int i = 0; i < array.length; ++i) {
                if (array[i] != null && byteBuffer.remaining() >= array[i].length) {
                    int n = 0;
                    byteBuffer.mark();
                    while (n < array[i].length && byteBuffer.get() == array[i][n]) {
                        ++n;
                    }
                    if (n == array[i].length) {
                        return i;
                    }
                    byteBuffer.reset();
                }
            }
            return -1;
        }
        
        private CoderResult decodeArrayLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            final byte[] array = byteBuffer.array();
            final int n = byteBuffer.arrayOffset() + byteBuffer.position();
            final int n2 = byteBuffer.arrayOffset() + byteBuffer.limit();
            assert n <= n2;
            int i = (n <= n2) ? n : n2;
            final char[] array2 = charBuffer.array();
            final int n3 = charBuffer.arrayOffset() + charBuffer.position();
            final int n4 = charBuffer.arrayOffset() + charBuffer.limit();
            assert n3 <= n4;
            int n5 = (n3 <= n4) ? n3 : n4;
            try {
                while (i < n2) {
                    final int n6 = array[i] & 0xFF;
                    int n7 = 1;
                    Label_0935: {
                        switch (n6) {
                            case 14: {
                                this.shiftout = true;
                                n7 = 1;
                                break;
                            }
                            case 15: {
                                this.shiftout = false;
                                n7 = 1;
                                break;
                            }
                            case 27: {
                                if (n2 - i - 1 < 3) {
                                    return CoderResult.UNDERFLOW;
                                }
                                final int desig = this.findDesig(array, i + 1, n2, this.SODesig);
                                if (desig != -1) {
                                    this.curSODes = desig;
                                    n7 = this.SODesig[desig].length + 1;
                                    break;
                                }
                                final int desig2 = this.findDesig(array, i + 1, n2, this.SS2Desig);
                                if (desig2 != -1) {
                                    this.curSS2Des = desig2;
                                    n7 = this.SS2Desig[desig2].length + 1;
                                    break;
                                }
                                final int desig3 = this.findDesig(array, i + 1, n2, this.SS3Desig);
                                if (desig3 != -1) {
                                    this.curSS3Des = desig3;
                                    n7 = this.SS3Desig[desig3].length + 1;
                                    break;
                                }
                                if (n2 - i < 2) {
                                    return CoderResult.UNDERFLOW;
                                }
                                switch (array[i + 1]) {
                                    case 78: {
                                        if (n2 - i < 4) {
                                            return CoderResult.UNDERFLOW;
                                        }
                                        final byte b = array[i + 2];
                                        final byte b2 = array[i + 3];
                                        if (n4 - n5 < 1) {
                                            return CoderResult.OVERFLOW;
                                        }
                                        array2[n5] = this.decode(b, b2, (byte)1);
                                        ++n5;
                                        n7 = 4;
                                        break Label_0935;
                                    }
                                    case 79: {
                                        if (n2 - i < 4) {
                                            return CoderResult.UNDERFLOW;
                                        }
                                        final byte b3 = array[i + 2];
                                        final byte b4 = array[i + 3];
                                        if (n4 - n5 < 1) {
                                            return CoderResult.OVERFLOW;
                                        }
                                        array2[n5] = this.decode(b3, b4, (byte)2);
                                        ++n5;
                                        n7 = 4;
                                        break Label_0935;
                                    }
                                    default: {
                                        return CoderResult.malformedForLength(2);
                                    }
                                }
                                break;
                            }
                            default: {
                                if (n4 - n5 < 1) {
                                    return CoderResult.OVERFLOW;
                                }
                                if (!this.shiftout) {
                                    array2[n5++] = (char)(array[i] & 0xFF);
                                    break;
                                }
                                if (n4 - n5 < 1) {
                                    return CoderResult.OVERFLOW;
                                }
                                if (n2 - i < 2) {
                                    return CoderResult.UNDERFLOW;
                                }
                                array2[n5++] = this.decode((byte)n6, (byte)(array[i + 1] & 0xFF), (byte)0);
                                n7 = 2;
                                break;
                            }
                        }
                    }
                    i += n7;
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                byteBuffer.position(i - byteBuffer.arrayOffset());
                charBuffer.position(n5 - charBuffer.arrayOffset());
            }
        }
        
        private CoderResult decodeBufferLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
            int position = byteBuffer.position();
            try {
                while (byteBuffer.hasRemaining()) {
                    final byte value = byteBuffer.get();
                    int n = 1;
                    Label_0564: {
                        switch (value) {
                            case 14: {
                                this.shiftout = true;
                                break;
                            }
                            case 15: {
                                this.shiftout = false;
                                break;
                            }
                            case 27: {
                                if (byteBuffer.remaining() < 3) {
                                    return CoderResult.UNDERFLOW;
                                }
                                final int desigBuf = this.findDesigBuf(byteBuffer, this.SODesig);
                                if (desigBuf != -1) {
                                    this.curSODes = desigBuf;
                                    n = this.SODesig[desigBuf].length + 1;
                                    break;
                                }
                                final int desigBuf2 = this.findDesigBuf(byteBuffer, this.SS2Desig);
                                if (desigBuf2 != -1) {
                                    this.curSS2Des = desigBuf2;
                                    n = this.SS2Desig[desigBuf2].length + 1;
                                    break;
                                }
                                final int desigBuf3 = this.findDesigBuf(byteBuffer, this.SS3Desig);
                                if (desigBuf3 != -1) {
                                    this.curSS3Des = desigBuf3;
                                    n = this.SS3Desig[desigBuf3].length + 1;
                                    break;
                                }
                                if (byteBuffer.remaining() < 1) {
                                    return CoderResult.UNDERFLOW;
                                }
                                switch (byteBuffer.get()) {
                                    case 78: {
                                        if (byteBuffer.remaining() < 2) {
                                            return CoderResult.UNDERFLOW;
                                        }
                                        final byte value2 = byteBuffer.get();
                                        final byte value3 = byteBuffer.get();
                                        if (charBuffer.remaining() < 1) {
                                            return CoderResult.OVERFLOW;
                                        }
                                        charBuffer.put(this.decode(value2, value3, (byte)1));
                                        n = 4;
                                        break Label_0564;
                                    }
                                    case 79: {
                                        if (byteBuffer.remaining() < 2) {
                                            return CoderResult.UNDERFLOW;
                                        }
                                        final byte value4 = byteBuffer.get();
                                        final byte value5 = byteBuffer.get();
                                        if (charBuffer.remaining() < 1) {
                                            return CoderResult.OVERFLOW;
                                        }
                                        charBuffer.put(this.decode(value4, value5, (byte)2));
                                        n = 4;
                                        break Label_0564;
                                    }
                                    default: {
                                        return CoderResult.malformedForLength(2);
                                    }
                                }
                                break;
                            }
                            default: {
                                if (charBuffer.remaining() < 1) {
                                    return CoderResult.OVERFLOW;
                                }
                                if (!this.shiftout) {
                                    charBuffer.put((char)(value & 0xFF));
                                    break;
                                }
                                if (charBuffer.remaining() < 1) {
                                    return CoderResult.OVERFLOW;
                                }
                                if (byteBuffer.remaining() < 1) {
                                    return CoderResult.UNDERFLOW;
                                }
                                charBuffer.put(this.decode(value, (byte)(byteBuffer.get() & 0xFF), (byte)0));
                                n = 2;
                                break;
                            }
                        }
                    }
                    position += n;
                }
                return CoderResult.UNDERFLOW;
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                return CoderResult.OVERFLOW;
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
    }
    
    protected static class Encoder extends CharsetEncoder
    {
        private final Surrogate.Parser sgp;
        public static final byte SS2 = -114;
        public static final byte PLANE2 = -94;
        public static final byte PLANE3 = -93;
        private final byte MSB = Byte.MIN_VALUE;
        protected final byte maximumDesignatorLength = 4;
        protected String SODesig;
        protected String SS2Desig;
        protected String SS3Desig;
        protected CharsetEncoder ISOEncoder;
        private boolean shiftout;
        private boolean SODesDefined;
        private boolean SS2DesDefined;
        private boolean SS3DesDefined;
        private boolean newshiftout;
        private boolean newSODesDefined;
        private boolean newSS2DesDefined;
        private boolean newSS3DesDefined;
        
        protected Encoder(final Charset charset) {
            super(charset, 4.0f, 8.0f);
            this.sgp = new Surrogate.Parser();
            this.SS2Desig = null;
            this.SS3Desig = null;
            this.shiftout = false;
            this.SODesDefined = false;
            this.SS2DesDefined = false;
            this.SS3DesDefined = false;
            this.newshiftout = false;
            this.newSODesDefined = false;
            this.newSS2DesDefined = false;
            this.newSS3DesDefined = false;
        }
        
        @Override
        public boolean canEncode(final char c) {
            return this.ISOEncoder.canEncode(c);
        }
        
        @Override
        protected void implReset() {
            this.shiftout = false;
            this.SODesDefined = false;
            this.SS2DesDefined = false;
            this.SS3DesDefined = false;
        }
        
        private int unicodeToNative(final char c, final byte[] array) {
            int n = 0;
            final char[] array2 = { c };
            final byte[] array3 = new byte[4];
            int remaining;
            try {
                final CharBuffer wrap = CharBuffer.wrap(array2);
                final ByteBuffer allocate = ByteBuffer.allocate(4);
                this.ISOEncoder.encode(wrap, allocate, true);
                allocate.flip();
                remaining = allocate.remaining();
                allocate.get(array3, 0, remaining);
            }
            catch (final Exception ex) {
                return -1;
            }
            if (remaining == 2) {
                if (!this.SODesDefined) {
                    this.newSODesDefined = true;
                    array[0] = 27;
                    final byte[] bytes = this.SODesig.getBytes();
                    System.arraycopy(bytes, 0, array, 1, bytes.length);
                    n = bytes.length + 1;
                }
                if (!this.shiftout) {
                    this.newshiftout = true;
                    array[n++] = 14;
                }
                array[n++] = (byte)(array3[0] & 0x7F);
                array[n++] = (byte)(array3[1] & 0x7F);
            }
            else if (array3[0] == -114) {
                if (array3[1] == -94) {
                    if (!this.SS2DesDefined) {
                        this.newSS2DesDefined = true;
                        array[0] = 27;
                        final byte[] bytes2 = this.SS2Desig.getBytes();
                        System.arraycopy(bytes2, 0, array, 1, bytes2.length);
                        n = bytes2.length + 1;
                    }
                    array[n++] = 27;
                    array[n++] = 78;
                    array[n++] = (byte)(array3[2] & 0x7F);
                    array[n++] = (byte)(array3[3] & 0x7F);
                }
                else if (array3[1] == -93) {
                    if (!this.SS3DesDefined) {
                        this.newSS3DesDefined = true;
                        array[0] = 27;
                        final byte[] bytes3 = this.SS3Desig.getBytes();
                        System.arraycopy(bytes3, 0, array, 1, bytes3.length);
                        n = bytes3.length + 1;
                    }
                    array[n++] = 27;
                    array[n++] = 79;
                    array[n++] = (byte)(array3[2] & 0x7F);
                    array[n++] = (byte)(array3[3] & 0x7F);
                }
            }
            return n;
        }
        
        private CoderResult encodeArrayLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            final char[] array = charBuffer.array();
            final int n = charBuffer.arrayOffset() + charBuffer.position();
            final int n2 = charBuffer.arrayOffset() + charBuffer.limit();
            assert n <= n2;
            int i = (n <= n2) ? n : n2;
            final byte[] array2 = byteBuffer.array();
            final int n3 = byteBuffer.arrayOffset() + byteBuffer.position();
            final int n4 = byteBuffer.arrayOffset() + byteBuffer.limit();
            assert n3 <= n4;
            int n5 = (n3 <= n4) ? n3 : n4;
            final byte[] array3 = new byte[8];
            this.newshiftout = this.shiftout;
            this.newSODesDefined = this.SODesDefined;
            this.newSS2DesDefined = this.SS2DesDefined;
            this.newSS3DesDefined = this.SS3DesDefined;
            try {
                while (i < n2) {
                    final char c = array[i];
                    if (Character.isSurrogate(c)) {
                        if (this.sgp.parse(c, array, i, n2) < 0) {
                            return this.sgp.error();
                        }
                        return this.sgp.unmappableResult();
                    }
                    else {
                        int unicodeToNative;
                        if (c < '\u0080') {
                            if (this.shiftout) {
                                this.newshiftout = false;
                                unicodeToNative = 2;
                                array3[0] = 15;
                                array3[1] = (byte)(c & '\u007f');
                            }
                            else {
                                unicodeToNative = 1;
                                array3[0] = (byte)(c & '\u007f');
                            }
                            if (array[i] == '\n') {
                                this.newSODesDefined = false;
                                this.newSS2DesDefined = false;
                                this.newSS3DesDefined = false;
                            }
                        }
                        else {
                            unicodeToNative = this.unicodeToNative(c, array3);
                            if (unicodeToNative == 0) {
                                return CoderResult.unmappableForLength(1);
                            }
                        }
                        if (n4 - n5 < unicodeToNative) {
                            return CoderResult.OVERFLOW;
                        }
                        for (int j = 0; j < unicodeToNative; ++j) {
                            array2[n5++] = array3[j];
                        }
                        ++i;
                        this.shiftout = this.newshiftout;
                        this.SODesDefined = this.newSODesDefined;
                        this.SS2DesDefined = this.newSS2DesDefined;
                        this.SS3DesDefined = this.newSS3DesDefined;
                    }
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                charBuffer.position(i - charBuffer.arrayOffset());
                byteBuffer.position(n5 - byteBuffer.arrayOffset());
            }
        }
        
        private CoderResult encodeBufferLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            final byte[] array = new byte[8];
            this.newshiftout = this.shiftout;
            this.newSODesDefined = this.SODesDefined;
            this.newSS2DesDefined = this.SS2DesDefined;
            this.newSS3DesDefined = this.SS3DesDefined;
            int position = charBuffer.position();
            try {
                while (charBuffer.hasRemaining()) {
                    final char value = charBuffer.get();
                    if (Character.isSurrogate(value)) {
                        if (this.sgp.parse(value, charBuffer) < 0) {
                            return this.sgp.error();
                        }
                        return this.sgp.unmappableResult();
                    }
                    else {
                        int unicodeToNative;
                        if (value < '\u0080') {
                            if (this.shiftout) {
                                this.newshiftout = false;
                                unicodeToNative = 2;
                                array[0] = 15;
                                array[1] = (byte)(value & '\u007f');
                            }
                            else {
                                unicodeToNative = 1;
                                array[0] = (byte)(value & '\u007f');
                            }
                            if (value == '\n') {
                                this.newSODesDefined = false;
                                this.newSS2DesDefined = false;
                                this.newSS3DesDefined = false;
                            }
                        }
                        else {
                            unicodeToNative = this.unicodeToNative(value, array);
                            if (unicodeToNative == 0) {
                                return CoderResult.unmappableForLength(1);
                            }
                        }
                        if (byteBuffer.remaining() < unicodeToNative) {
                            return CoderResult.OVERFLOW;
                        }
                        for (int i = 0; i < unicodeToNative; ++i) {
                            byteBuffer.put(array[i]);
                        }
                        ++position;
                        this.shiftout = this.newshiftout;
                        this.SODesDefined = this.newSODesDefined;
                        this.SS2DesDefined = this.newSS2DesDefined;
                        this.SS3DesDefined = this.newSS3DesDefined;
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
    }
}
