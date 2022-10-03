package sun.nio.cs.ext;

import java.nio.charset.CodingErrorAction;
import sun.nio.cs.Surrogate;
import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.US_ASCII;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class ISO2022_JP extends Charset implements HistoricallyNamedCharset
{
    private static final int ASCII = 0;
    private static final int JISX0201_1976 = 1;
    private static final int JISX0208_1978 = 2;
    private static final int JISX0208_1983 = 3;
    private static final int JISX0212_1990 = 4;
    private static final int JISX0201_1976_KANA = 5;
    private static final int SHIFTOUT = 6;
    private static final int ESC = 27;
    private static final int SO = 14;
    private static final int SI = 15;
    
    public ISO2022_JP() {
        super("ISO-2022-JP", ExtendedCharsets.aliasesFor("ISO-2022-JP"));
    }
    
    protected ISO2022_JP(final String s, final String[] array) {
        super(s, array);
    }
    
    @Override
    public String historicalName() {
        return "ISO2022JP";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset instanceof JIS_X_0201 || charset instanceof US_ASCII || charset instanceof JIS_X_0208 || charset instanceof ISO2022_JP;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder((Charset)this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder((Charset)this);
    }
    
    protected boolean doSBKANA() {
        return true;
    }
    
    static class Decoder extends CharsetDecoder implements DelegatableDecoder
    {
        static final DoubleByte.Decoder DEC0208;
        private int currentState;
        private int previousState;
        private DoubleByte.Decoder dec0208;
        private DoubleByte.Decoder dec0212;
        
        private Decoder(final Charset charset) {
            this(charset, Decoder.DEC0208, null);
        }
        
        protected Decoder(final Charset charset, final DoubleByte.Decoder dec0208, final DoubleByte.Decoder dec209) {
            super(charset, 0.5f, 1.0f);
            this.dec0208 = dec0208;
            this.dec0212 = dec209;
            this.currentState = 0;
            this.previousState = 0;
        }
        
        @Override
        public void implReset() {
            this.currentState = 0;
            this.previousState = 0;
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
                    if ((n6 & 0x80) != 0x0) {
                        return CoderResult.malformedForLength(n7);
                    }
                    if (n6 == 27 || n6 == 14 || n6 == 15) {
                        if (n6 == 27) {
                            if (i + n7 + 2 > n2) {
                                return CoderResult.UNDERFLOW;
                            }
                            final int n8 = array[i + n7++] & 0xFF;
                            if (n8 == 40) {
                                final int n9 = array[i + n7++] & 0xFF;
                                if (n9 == 66) {
                                    this.currentState = 0;
                                }
                                else if (n9 == 74) {
                                    this.currentState = 1;
                                }
                                else {
                                    if (n9 != 73) {
                                        return CoderResult.malformedForLength(n7);
                                    }
                                    this.currentState = 5;
                                }
                            }
                            else {
                                if (n8 != 36) {
                                    return CoderResult.malformedForLength(n7);
                                }
                                final int n10 = array[i + n7++] & 0xFF;
                                if (n10 == 64) {
                                    this.currentState = 2;
                                }
                                else if (n10 == 66) {
                                    this.currentState = 3;
                                }
                                else {
                                    if (n10 != 40 || this.dec0212 == null) {
                                        return CoderResult.malformedForLength(n7);
                                    }
                                    if (i + n7 + 1 > n2) {
                                        return CoderResult.UNDERFLOW;
                                    }
                                    if ((array[i + n7++] & 0xFF) != 0x44) {
                                        return CoderResult.malformedForLength(n7);
                                    }
                                    this.currentState = 4;
                                }
                            }
                        }
                        else if (n6 == 14) {
                            this.previousState = this.currentState;
                            this.currentState = 6;
                        }
                        else if (n6 == 15) {
                            this.currentState = this.previousState;
                        }
                        i += n7;
                    }
                    else {
                        if (n5 + 1 > n4) {
                            return CoderResult.OVERFLOW;
                        }
                        Label_1166: {
                            switch (this.currentState) {
                                case 0: {
                                    array2[n5++] = (char)(n6 & 0xFF);
                                    break;
                                }
                                case 1: {
                                    switch (n6) {
                                        case 92: {
                                            array2[n5++] = '¥';
                                            break Label_1166;
                                        }
                                        case 126: {
                                            array2[n5++] = '\u203e';
                                            break Label_1166;
                                        }
                                        default: {
                                            array2[n5++] = (char)n6;
                                            break Label_1166;
                                        }
                                    }
                                    break;
                                }
                                case 2:
                                case 3: {
                                    if (i + n7 + 1 > n2) {
                                        return CoderResult.UNDERFLOW;
                                    }
                                    final char decodeDouble = this.dec0208.decodeDouble(n6, array[i + n7++] & 0xFF);
                                    if (decodeDouble == '\ufffd') {
                                        return CoderResult.unmappableForLength(n7);
                                    }
                                    array2[n5++] = decodeDouble;
                                    break;
                                }
                                case 4: {
                                    if (i + n7 + 1 > n2) {
                                        return CoderResult.UNDERFLOW;
                                    }
                                    final char decodeDouble2 = this.dec0212.decodeDouble(n6, array[i + n7++] & 0xFF);
                                    if (decodeDouble2 == '\ufffd') {
                                        return CoderResult.unmappableForLength(n7);
                                    }
                                    array2[n5++] = decodeDouble2;
                                    break;
                                }
                                case 5:
                                case 6: {
                                    if (n6 > 95) {
                                        return CoderResult.malformedForLength(n7);
                                    }
                                    array2[n5++] = (char)(n6 + 65344);
                                    break;
                                }
                            }
                        }
                        i += n7;
                    }
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
                    final int n = byteBuffer.get() & 0xFF;
                    int n2 = 1;
                    if ((n & 0x80) != 0x0) {
                        return CoderResult.malformedForLength(n2);
                    }
                    if (n == 27 || n == 14 || n == 15) {
                        if (n == 27) {
                            if (byteBuffer.remaining() < 2) {
                                return CoderResult.UNDERFLOW;
                            }
                            final int n3 = byteBuffer.get() & 0xFF;
                            ++n2;
                            if (n3 == 40) {
                                final int n4 = byteBuffer.get() & 0xFF;
                                ++n2;
                                if (n4 == 66) {
                                    this.currentState = 0;
                                }
                                else if (n4 == 74) {
                                    this.currentState = 1;
                                }
                                else {
                                    if (n4 != 73) {
                                        return CoderResult.malformedForLength(n2);
                                    }
                                    this.currentState = 5;
                                }
                            }
                            else {
                                if (n3 != 36) {
                                    return CoderResult.malformedForLength(n2);
                                }
                                final int n5 = byteBuffer.get() & 0xFF;
                                ++n2;
                                if (n5 == 64) {
                                    this.currentState = 2;
                                }
                                else if (n5 == 66) {
                                    this.currentState = 3;
                                }
                                else {
                                    if (n5 != 40 || this.dec0212 == null) {
                                        return CoderResult.malformedForLength(n2);
                                    }
                                    if (!byteBuffer.hasRemaining()) {
                                        return CoderResult.UNDERFLOW;
                                    }
                                    final int n6 = byteBuffer.get() & 0xFF;
                                    ++n2;
                                    if (n6 != 68) {
                                        return CoderResult.malformedForLength(n2);
                                    }
                                    this.currentState = 4;
                                }
                            }
                        }
                        else if (n == 14) {
                            this.previousState = this.currentState;
                            this.currentState = 6;
                        }
                        else if (n == 15) {
                            this.currentState = this.previousState;
                        }
                        position += n2;
                    }
                    else {
                        if (!charBuffer.hasRemaining()) {
                            return CoderResult.OVERFLOW;
                        }
                        Label_0759: {
                            switch (this.currentState) {
                                case 0: {
                                    charBuffer.put((char)(n & 0xFF));
                                    break;
                                }
                                case 1: {
                                    switch (n) {
                                        case 92: {
                                            charBuffer.put('¥');
                                            break Label_0759;
                                        }
                                        case 126: {
                                            charBuffer.put('\u203e');
                                            break Label_0759;
                                        }
                                        default: {
                                            charBuffer.put((char)n);
                                            break Label_0759;
                                        }
                                    }
                                    break;
                                }
                                case 2:
                                case 3: {
                                    if (!byteBuffer.hasRemaining()) {
                                        return CoderResult.UNDERFLOW;
                                    }
                                    final int n7 = byteBuffer.get() & 0xFF;
                                    ++n2;
                                    final char decodeDouble = this.dec0208.decodeDouble(n, n7);
                                    if (decodeDouble == '\ufffd') {
                                        return CoderResult.unmappableForLength(n2);
                                    }
                                    charBuffer.put(decodeDouble);
                                    break;
                                }
                                case 4: {
                                    if (!byteBuffer.hasRemaining()) {
                                        return CoderResult.UNDERFLOW;
                                    }
                                    final int n8 = byteBuffer.get() & 0xFF;
                                    ++n2;
                                    final char decodeDouble2 = this.dec0212.decodeDouble(n, n8);
                                    if (decodeDouble2 == '\ufffd') {
                                        return CoderResult.unmappableForLength(n2);
                                    }
                                    charBuffer.put(decodeDouble2);
                                    break;
                                }
                                case 5:
                                case 6: {
                                    if (n > 95) {
                                        return CoderResult.malformedForLength(n2);
                                    }
                                    charBuffer.put((char)(n + 65344));
                                    break;
                                }
                            }
                        }
                        position += n2;
                    }
                }
                return CoderResult.UNDERFLOW;
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
        public CoderResult implFlush(final CharBuffer charBuffer) {
            return super.implFlush(charBuffer);
        }
        
        static {
            DEC0208 = (DoubleByte.Decoder)new JIS_X_0208().newDecoder();
        }
    }
    
    static class Encoder extends CharsetEncoder
    {
        static final DoubleByte.Encoder ENC0208;
        private static byte[] repl;
        private int currentMode;
        private int replaceMode;
        private DoubleByte.Encoder enc0208;
        private DoubleByte.Encoder enc0212;
        private boolean doSBKANA;
        private final Surrogate.Parser sgp;
        
        private Encoder(final Charset charset) {
            this(charset, Encoder.ENC0208, null, true);
        }
        
        Encoder(final Charset charset, final DoubleByte.Encoder enc0208, final DoubleByte.Encoder enc209, final boolean doSBKANA) {
            super(charset, 4.0f, (enc209 != null) ? 9.0f : 8.0f, Encoder.repl);
            this.currentMode = 0;
            this.replaceMode = 3;
            this.sgp = new Surrogate.Parser();
            this.enc0208 = enc0208;
            this.enc0212 = enc209;
            this.doSBKANA = doSBKANA;
        }
        
        protected int encodeSingle(final char c) {
            return -1;
        }
        
        @Override
        protected void implReset() {
            this.currentMode = 0;
        }
        
        @Override
        protected void implReplaceWith(final byte[] array) {
            if (array.length == 1) {
                this.replaceMode = 0;
            }
            else if (array.length == 2) {
                this.replaceMode = 3;
            }
        }
        
        @Override
        protected CoderResult implFlush(final ByteBuffer byteBuffer) {
            if (this.currentMode != 0) {
                if (byteBuffer.remaining() < 3) {
                    return CoderResult.OVERFLOW;
                }
                byteBuffer.put((byte)27);
                byteBuffer.put((byte)40);
                byteBuffer.put((byte)66);
                this.currentMode = 0;
            }
            return CoderResult.UNDERFLOW;
        }
        
        @Override
        public boolean canEncode(final char c) {
            return c <= '\u007f' || (c >= '\uff61' && c <= '\uff9f') || c == '¥' || c == '\u203e' || this.enc0208.canEncode(c) || (this.enc0212 != null && this.enc0212.canEncode(c));
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
            try {
                while (i < n2) {
                    final char c = array[i];
                    if (c <= '\u007f') {
                        if (this.currentMode != 0) {
                            if (n4 - n5 < 3) {
                                return CoderResult.OVERFLOW;
                            }
                            array2[n5++] = 27;
                            array2[n5++] = 40;
                            array2[n5++] = 66;
                            this.currentMode = 0;
                        }
                        if (n4 - n5 < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        array2[n5++] = (byte)c;
                    }
                    else if (c >= '\uff61' && c <= '\uff9f' && this.doSBKANA) {
                        if (this.currentMode != 5) {
                            if (n4 - n5 < 3) {
                                return CoderResult.OVERFLOW;
                            }
                            array2[n5++] = 27;
                            array2[n5++] = 40;
                            array2[n5++] = 73;
                            this.currentMode = 5;
                        }
                        if (n4 - n5 < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        array2[n5++] = (byte)(c - '\uff40');
                    }
                    else if (c == '¥' || c == '\u203e') {
                        if (this.currentMode != 1) {
                            if (n4 - n5 < 3) {
                                return CoderResult.OVERFLOW;
                            }
                            array2[n5++] = 27;
                            array2[n5++] = 40;
                            array2[n5++] = 74;
                            this.currentMode = 1;
                        }
                        if (n4 - n5 < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        array2[n5++] = (byte)((c == '¥') ? 92 : 126);
                    }
                    else {
                        final int encodeChar = this.enc0208.encodeChar(c);
                        if (encodeChar != 65533) {
                            if (this.currentMode != 3) {
                                if (n4 - n5 < 3) {
                                    return CoderResult.OVERFLOW;
                                }
                                array2[n5++] = 27;
                                array2[n5++] = 36;
                                array2[n5++] = 66;
                                this.currentMode = 3;
                            }
                            if (n4 - n5 < 2) {
                                return CoderResult.OVERFLOW;
                            }
                            array2[n5++] = (byte)(encodeChar >> 8);
                            array2[n5++] = (byte)(encodeChar & 0xFF);
                        }
                        else {
                            final int encodeChar2;
                            if (this.enc0212 != null && (encodeChar2 = this.enc0212.encodeChar(c)) != 65533) {
                                if (this.currentMode != 4) {
                                    if (n4 - n5 < 4) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    array2[n5++] = 27;
                                    array2[n5++] = 36;
                                    array2[n5++] = 40;
                                    array2[n5++] = 68;
                                    this.currentMode = 4;
                                }
                                if (n4 - n5 < 2) {
                                    return CoderResult.OVERFLOW;
                                }
                                array2[n5++] = (byte)(encodeChar2 >> 8);
                                array2[n5++] = (byte)(encodeChar2 & 0xFF);
                            }
                            else {
                                if (Character.isSurrogate(c) && this.sgp.parse(c, array, i, n2) < 0) {
                                    return this.sgp.error();
                                }
                                if (this.unmappableCharacterAction() == CodingErrorAction.REPLACE && this.currentMode != this.replaceMode) {
                                    if (n4 - n5 < 3) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    if (this.replaceMode == 0) {
                                        array2[n5++] = 27;
                                        array2[n5++] = 40;
                                        array2[n5++] = 66;
                                    }
                                    else {
                                        array2[n5++] = 27;
                                        array2[n5++] = 36;
                                        array2[n5++] = 66;
                                    }
                                    this.currentMode = this.replaceMode;
                                }
                                if (Character.isSurrogate(c)) {
                                    return this.sgp.unmappableResult();
                                }
                                return CoderResult.unmappableForLength(1);
                            }
                        }
                    }
                    ++i;
                }
                return CoderResult.UNDERFLOW;
            }
            finally {
                charBuffer.position(i - charBuffer.arrayOffset());
                byteBuffer.position(n5 - byteBuffer.arrayOffset());
            }
        }
        
        private CoderResult encodeBufferLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
            int position = charBuffer.position();
            try {
                while (charBuffer.hasRemaining()) {
                    final char value = charBuffer.get();
                    if (value <= '\u007f') {
                        if (this.currentMode != 0) {
                            if (byteBuffer.remaining() < 3) {
                                return CoderResult.OVERFLOW;
                            }
                            byteBuffer.put((byte)27);
                            byteBuffer.put((byte)40);
                            byteBuffer.put((byte)66);
                            this.currentMode = 0;
                        }
                        if (byteBuffer.remaining() < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        byteBuffer.put((byte)value);
                    }
                    else if (value >= '\uff61' && value <= '\uff9f' && this.doSBKANA) {
                        if (this.currentMode != 5) {
                            if (byteBuffer.remaining() < 3) {
                                return CoderResult.OVERFLOW;
                            }
                            byteBuffer.put((byte)27);
                            byteBuffer.put((byte)40);
                            byteBuffer.put((byte)73);
                            this.currentMode = 5;
                        }
                        if (byteBuffer.remaining() < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        byteBuffer.put((byte)(value - '\uff40'));
                    }
                    else if (value == '¥' || value == '\u203e') {
                        if (this.currentMode != 1) {
                            if (byteBuffer.remaining() < 3) {
                                return CoderResult.OVERFLOW;
                            }
                            byteBuffer.put((byte)27);
                            byteBuffer.put((byte)40);
                            byteBuffer.put((byte)74);
                            this.currentMode = 1;
                        }
                        if (byteBuffer.remaining() < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        byteBuffer.put((byte)((value == '¥') ? 92 : 126));
                    }
                    else {
                        final int encodeChar = this.enc0208.encodeChar(value);
                        if (encodeChar != 65533) {
                            if (this.currentMode != 3) {
                                if (byteBuffer.remaining() < 3) {
                                    return CoderResult.OVERFLOW;
                                }
                                byteBuffer.put((byte)27);
                                byteBuffer.put((byte)36);
                                byteBuffer.put((byte)66);
                                this.currentMode = 3;
                            }
                            if (byteBuffer.remaining() < 2) {
                                return CoderResult.OVERFLOW;
                            }
                            byteBuffer.put((byte)(encodeChar >> 8));
                            byteBuffer.put((byte)(encodeChar & 0xFF));
                        }
                        else {
                            final int encodeChar2;
                            if (this.enc0212 != null && (encodeChar2 = this.enc0212.encodeChar(value)) != 65533) {
                                if (this.currentMode != 4) {
                                    if (byteBuffer.remaining() < 4) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    byteBuffer.put((byte)27);
                                    byteBuffer.put((byte)36);
                                    byteBuffer.put((byte)40);
                                    byteBuffer.put((byte)68);
                                    this.currentMode = 4;
                                }
                                if (byteBuffer.remaining() < 2) {
                                    return CoderResult.OVERFLOW;
                                }
                                byteBuffer.put((byte)(encodeChar2 >> 8));
                                byteBuffer.put((byte)(encodeChar2 & 0xFF));
                            }
                            else {
                                if (Character.isSurrogate(value) && this.sgp.parse(value, charBuffer) < 0) {
                                    return this.sgp.error();
                                }
                                if (this.unmappableCharacterAction() == CodingErrorAction.REPLACE && this.currentMode != this.replaceMode) {
                                    if (byteBuffer.remaining() < 3) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    if (this.replaceMode == 0) {
                                        byteBuffer.put((byte)27);
                                        byteBuffer.put((byte)40);
                                        byteBuffer.put((byte)66);
                                    }
                                    else {
                                        byteBuffer.put((byte)27);
                                        byteBuffer.put((byte)36);
                                        byteBuffer.put((byte)66);
                                    }
                                    this.currentMode = this.replaceMode;
                                }
                                if (Character.isSurrogate(value)) {
                                    return this.sgp.unmappableResult();
                                }
                                return CoderResult.unmappableForLength(1);
                            }
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
        
        static {
            ENC0208 = (DoubleByte.Encoder)new JIS_X_0208().newEncoder();
            Encoder.repl = new byte[] { 33, 41 };
        }
    }
}
