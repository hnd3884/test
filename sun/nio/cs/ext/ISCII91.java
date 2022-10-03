package sun.nio.cs.ext;

import sun.nio.cs.Surrogate;
import java.nio.ByteBuffer;
import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.Charset;

public class ISCII91 extends Charset implements HistoricallyNamedCharset
{
    private static final char NUKTA_CHAR = '\u093c';
    private static final char HALANT_CHAR = '\u094d';
    private static final byte NO_CHAR = -1;
    private static final char[] directMapTable;
    private static final byte[] encoderMappingTable;
    
    public ISCII91() {
        super("x-ISCII91", ExtendedCharsets.aliasesFor("x-ISCII91"));
    }
    
    @Override
    public String historicalName() {
        return "ISCII91";
    }
    
    @Override
    public boolean contains(final Charset charset) {
        return charset.name().equals("US-ASCII") || charset instanceof ISCII91;
    }
    
    @Override
    public CharsetDecoder newDecoder() {
        return new Decoder((Charset)this);
    }
    
    @Override
    public CharsetEncoder newEncoder() {
        return new Encoder((Charset)this);
    }
    
    static {
        directMapTable = new char[] { '\0', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\t', '\n', '\u000b', '\f', '\r', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\u007f', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff', '\u0901', '\u0902', '\u0903', '\u0905', '\u0906', '\u0907', '\u0908', '\u0909', '\u090a', '\u090b', '\u090e', '\u090f', '\u0910', '\u090d', '\u0912', '\u0913', '\u0914', '\u0911', '\u0915', '\u0916', '\u0917', '\u0918', '\u0919', '\u091a', '\u091b', '\u091c', '\u091d', '\u091e', '\u091f', '\u0920', '\u0921', '\u0922', '\u0923', '\u0924', '\u0925', '\u0926', '\u0927', '\u0928', '\u0929', '\u092a', '\u092b', '\u092c', '\u092d', '\u092e', '\u092f', '\u095f', '\u0930', '\u0931', '\u0932', '\u0933', '\u0934', '\u0935', '\u0936', '\u0937', '\u0938', '\u0939', '\u200d', '\u093e', '\u093f', '\u0940', '\u0941', '\u0942', '\u0943', '\u0946', '\u0947', '\u0948', '\u0945', '\u094a', '\u094b', '\u094c', '\u0949', '\u094d', '\u093c', '\u0964', '\uffff', '\uffff', '\uffff', '\uffff', '\ufffd', '\ufffd', '\u0966', '\u0967', '\u0968', '\u0969', '\u096a', '\u096b', '\u096c', '\u096d', '\u096e', '\u096f', '\uffff', '\uffff', '\uffff', '\uffff', '\uffff' };
        encoderMappingTable = new byte[] { -1, -1, -95, -1, -94, -1, -93, -1, -1, -1, -92, -1, -91, -1, -90, -1, -89, -1, -88, -1, -87, -1, -86, -1, -90, -23, -82, -1, -85, -1, -84, -1, -83, -1, -78, -1, -81, -1, -80, -1, -79, -1, -77, -1, -76, -1, -75, -1, -74, -1, -73, -1, -72, -1, -71, -1, -70, -1, -69, -1, -68, -1, -67, -1, -66, -1, -65, -1, -64, -1, -63, -1, -62, -1, -61, -1, -60, -1, -59, -1, -58, -1, -57, -1, -56, -1, -55, -1, -54, -1, -53, -1, -52, -1, -51, -1, -49, -1, -48, -1, -47, -1, -46, -1, -45, -1, -44, -1, -43, -1, -42, -1, -41, -1, -40, -1, -1, -1, -1, -1, -23, -1, -22, -23, -38, -1, -37, -1, -36, -1, -35, -1, -34, -1, -33, -1, -33, -23, -29, -1, -32, -1, -31, -1, -30, -1, -25, -1, -28, -1, -27, -1, -26, -1, -24, -1, -1, -1, -1, -1, -95, -23, -16, -75, -16, -72, -2, -1, -2, -1, -1, -1, -1, -1, -1, -1, -77, -23, -76, -23, -75, -23, -70, -23, -65, -23, -64, -23, -55, -23, -50, -1, -86, -23, -89, -23, -37, -23, -36, -23, -22, -1, -22, -22, -15, -1, -14, -1, -13, -1, -12, -1, -11, -1, -10, -1, -9, -1, -8, -1, -7, -1, -6, -1, -16, -65, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
    }
    
    private static class Decoder extends CharsetDecoder
    {
        private static final char ZWNJ_CHAR = '\u200c';
        private static final char ZWJ_CHAR = '\u200d';
        private static final char INVALID_CHAR = '\uffff';
        private char contextChar;
        private boolean needFlushing;
        
        private Decoder(final Charset charset) {
            super(charset, 1.0f, 1.0f);
            this.contextChar = '\uffff';
            this.needFlushing = false;
        }
        
        @Override
        protected CoderResult implFlush(final CharBuffer charBuffer) {
            if (this.needFlushing) {
                if (charBuffer.remaining() < 1) {
                    return CoderResult.OVERFLOW;
                }
                charBuffer.put(this.contextChar);
            }
            this.contextChar = '\uffff';
            this.needFlushing = false;
            return CoderResult.UNDERFLOW;
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
                    final byte b = array[i];
                    final char c = ISCII91.directMapTable[(b < 0) ? (b + 255) : b];
                    if (this.contextChar == '\ufffd') {
                        if (n4 - n5 < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        array2[n5++] = '\ufffd';
                        this.contextChar = '\uffff';
                        this.needFlushing = false;
                        ++i;
                    }
                    else {
                        Label_1059: {
                            switch (c) {
                                case 2305:
                                case 2311:
                                case 2312:
                                case 2315:
                                case 2367:
                                case 2368:
                                case 2371:
                                case 2404: {
                                    if (!this.needFlushing) {
                                        this.contextChar = c;
                                        this.needFlushing = true;
                                        ++i;
                                        continue;
                                    }
                                    if (n4 - n5 < 1) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    array2[n5++] = this.contextChar;
                                    this.contextChar = c;
                                    ++i;
                                    continue;
                                }
                                case 2364: {
                                    if (n4 - n5 < 1) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    switch (this.contextChar) {
                                        case '\u0901': {
                                            array2[n5++] = '\u0950';
                                            break Label_1059;
                                        }
                                        case '\u0907': {
                                            array2[n5++] = '\u090c';
                                            break Label_1059;
                                        }
                                        case '\u0908': {
                                            array2[n5++] = '\u0961';
                                            break Label_1059;
                                        }
                                        case '\u090b': {
                                            array2[n5++] = '\u0960';
                                            break Label_1059;
                                        }
                                        case '\u093f': {
                                            array2[n5++] = '\u0962';
                                            break Label_1059;
                                        }
                                        case '\u0940': {
                                            array2[n5++] = '\u0963';
                                            break Label_1059;
                                        }
                                        case '\u0943': {
                                            array2[n5++] = '\u0944';
                                            break Label_1059;
                                        }
                                        case '\u0964': {
                                            array2[n5++] = '\u093d';
                                            break Label_1059;
                                        }
                                        case '\u094d': {
                                            if (this.needFlushing) {
                                                array2[n5++] = this.contextChar;
                                                this.contextChar = c;
                                                ++i;
                                                continue;
                                            }
                                            array2[n5++] = '\u200d';
                                            break Label_1059;
                                        }
                                        default: {
                                            if (this.needFlushing) {
                                                array2[n5++] = this.contextChar;
                                                this.contextChar = c;
                                                ++i;
                                                continue;
                                            }
                                            array2[n5++] = '\u093c';
                                            break Label_1059;
                                        }
                                    }
                                    break;
                                }
                                case 2381: {
                                    if (n4 - n5 < 1) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    if (this.needFlushing) {
                                        array2[n5++] = this.contextChar;
                                        this.contextChar = c;
                                        ++i;
                                        continue;
                                    }
                                    if (this.contextChar == '\u094d') {
                                        array2[n5++] = '\u200c';
                                        break;
                                    }
                                    array2[n5++] = '\u094d';
                                    break;
                                }
                                case 65535: {
                                    if (!this.needFlushing) {
                                        return CoderResult.unmappableForLength(1);
                                    }
                                    if (n4 - n5 < 1) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    array2[n5++] = this.contextChar;
                                    this.contextChar = c;
                                    ++i;
                                    continue;
                                }
                                default: {
                                    if (n4 - n5 < 1) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    if (this.needFlushing) {
                                        array2[n5++] = this.contextChar;
                                        this.contextChar = c;
                                        ++i;
                                        continue;
                                    }
                                    array2[n5++] = c;
                                    break;
                                }
                            }
                        }
                        this.contextChar = c;
                        this.needFlushing = false;
                        ++i;
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
                    final byte value = byteBuffer.get();
                    final char c = ISCII91.directMapTable[(value < 0) ? (value + 255) : value];
                    if (this.contextChar == '\ufffd') {
                        if (charBuffer.remaining() < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        charBuffer.put('\ufffd');
                        this.contextChar = '\uffff';
                        this.needFlushing = false;
                        ++position;
                    }
                    else {
                        Label_0746: {
                            switch (c) {
                                case 2305:
                                case 2311:
                                case 2312:
                                case 2315:
                                case 2367:
                                case 2368:
                                case 2371:
                                case 2404: {
                                    if (!this.needFlushing) {
                                        this.contextChar = c;
                                        this.needFlushing = true;
                                        ++position;
                                        continue;
                                    }
                                    if (charBuffer.remaining() < 1) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    charBuffer.put(this.contextChar);
                                    this.contextChar = c;
                                    ++position;
                                    continue;
                                }
                                case 2364: {
                                    if (charBuffer.remaining() < 1) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    switch (this.contextChar) {
                                        case '\u0901': {
                                            charBuffer.put('\u0950');
                                            break Label_0746;
                                        }
                                        case '\u0907': {
                                            charBuffer.put('\u090c');
                                            break Label_0746;
                                        }
                                        case '\u0908': {
                                            charBuffer.put('\u0961');
                                            break Label_0746;
                                        }
                                        case '\u090b': {
                                            charBuffer.put('\u0960');
                                            break Label_0746;
                                        }
                                        case '\u093f': {
                                            charBuffer.put('\u0962');
                                            break Label_0746;
                                        }
                                        case '\u0940': {
                                            charBuffer.put('\u0963');
                                            break Label_0746;
                                        }
                                        case '\u0943': {
                                            charBuffer.put('\u0944');
                                            break Label_0746;
                                        }
                                        case '\u0964': {
                                            charBuffer.put('\u093d');
                                            break Label_0746;
                                        }
                                        case '\u094d': {
                                            if (this.needFlushing) {
                                                charBuffer.put(this.contextChar);
                                                this.contextChar = c;
                                                ++position;
                                                continue;
                                            }
                                            charBuffer.put('\u200d');
                                            break Label_0746;
                                        }
                                        default: {
                                            if (this.needFlushing) {
                                                charBuffer.put(this.contextChar);
                                                this.contextChar = c;
                                                ++position;
                                                continue;
                                            }
                                            charBuffer.put('\u093c');
                                            break Label_0746;
                                        }
                                    }
                                    break;
                                }
                                case 2381: {
                                    if (charBuffer.remaining() < 1) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    if (this.needFlushing) {
                                        charBuffer.put(this.contextChar);
                                        this.contextChar = c;
                                        ++position;
                                        continue;
                                    }
                                    if (this.contextChar == '\u094d') {
                                        charBuffer.put('\u200c');
                                        break;
                                    }
                                    charBuffer.put('\u094d');
                                    break;
                                }
                                case 65535: {
                                    if (!this.needFlushing) {
                                        return CoderResult.unmappableForLength(1);
                                    }
                                    if (charBuffer.remaining() < 1) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    charBuffer.put(this.contextChar);
                                    this.contextChar = c;
                                    ++position;
                                    continue;
                                }
                                default: {
                                    if (charBuffer.remaining() < 1) {
                                        return CoderResult.OVERFLOW;
                                    }
                                    if (this.needFlushing) {
                                        charBuffer.put(this.contextChar);
                                        this.contextChar = c;
                                        ++position;
                                        continue;
                                    }
                                    charBuffer.put(c);
                                    break;
                                }
                            }
                        }
                        this.contextChar = c;
                        this.needFlushing = false;
                        ++position;
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
    }
    
    private static class Encoder extends CharsetEncoder
    {
        private static final byte NO_CHAR = -1;
        private final Surrogate.Parser sgp;
        
        private Encoder(final Charset charset) {
            super(charset, 2.0f, 2.0f);
            this.sgp = new Surrogate.Parser();
        }
        
        @Override
        public boolean canEncode(final char c) {
            return (c >= '\u0900' && c <= '\u097f' && ISCII91.encoderMappingTable[2 * (c - '\u0900')] != -1) || c == '\u200d' || c == '\u200c' || c <= '\u007f';
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
                    int n6 = Integer.MIN_VALUE;
                    char c = array[i];
                    if (c >= '\0' && c <= '\u007f') {
                        if (n4 - n5 < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        array2[n5++] = (byte)c;
                        ++i;
                    }
                    else {
                        if (c == '\u200c') {
                            c = '\u094d';
                        }
                        else if (c == '\u200d') {
                            c = '\u093c';
                        }
                        if (c >= '\u0900' && c <= '\u097f') {
                            n6 = (c - '\u0900') * 2;
                        }
                        if (Character.isSurrogate(c)) {
                            if (this.sgp.parse(c, array, i, n2) < 0) {
                                return this.sgp.error();
                            }
                            return this.sgp.unmappableResult();
                        }
                        else {
                            if (n6 == Integer.MIN_VALUE || ISCII91.encoderMappingTable[n6] == -1) {
                                return CoderResult.unmappableForLength(1);
                            }
                            if (ISCII91.encoderMappingTable[n6 + 1] == -1) {
                                if (n4 - n5 < 1) {
                                    return CoderResult.OVERFLOW;
                                }
                                array2[n5++] = ISCII91.encoderMappingTable[n6];
                            }
                            else {
                                if (n4 - n5 < 2) {
                                    return CoderResult.OVERFLOW;
                                }
                                array2[n5++] = ISCII91.encoderMappingTable[n6];
                                array2[n5++] = ISCII91.encoderMappingTable[n6 + 1];
                            }
                            ++i;
                        }
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
            int position = charBuffer.position();
            try {
                while (charBuffer.hasRemaining()) {
                    int n = Integer.MIN_VALUE;
                    char value = charBuffer.get();
                    if (value >= '\0' && value <= '\u007f') {
                        if (byteBuffer.remaining() < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        byteBuffer.put((byte)value);
                        ++position;
                    }
                    else {
                        if (value == '\u200c') {
                            value = '\u094d';
                        }
                        else if (value == '\u200d') {
                            value = '\u093c';
                        }
                        if (value >= '\u0900' && value <= '\u097f') {
                            n = (value - '\u0900') * 2;
                        }
                        if (Character.isSurrogate(value)) {
                            if (this.sgp.parse(value, charBuffer) < 0) {
                                return this.sgp.error();
                            }
                            return this.sgp.unmappableResult();
                        }
                        else {
                            if (n == Integer.MIN_VALUE || ISCII91.encoderMappingTable[n] == -1) {
                                return CoderResult.unmappableForLength(1);
                            }
                            if (ISCII91.encoderMappingTable[n + 1] == -1) {
                                if (byteBuffer.remaining() < 1) {
                                    return CoderResult.OVERFLOW;
                                }
                                byteBuffer.put(ISCII91.encoderMappingTable[n]);
                            }
                            else {
                                if (byteBuffer.remaining() < 2) {
                                    return CoderResult.OVERFLOW;
                                }
                                byteBuffer.put(ISCII91.encoderMappingTable[n]);
                                byteBuffer.put(ISCII91.encoderMappingTable[n + 1]);
                            }
                            ++position;
                        }
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
