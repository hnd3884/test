package sun.nio.cs;

import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

abstract class UnicodeDecoder extends CharsetDecoder
{
    protected static final char BYTE_ORDER_MARK = '\ufeff';
    protected static final char REVERSED_MARK = '\ufffe';
    protected static final int NONE = 0;
    protected static final int BIG = 1;
    protected static final int LITTLE = 2;
    private final int expectedByteOrder;
    private int currentByteOrder;
    private int defaultByteOrder;
    
    public UnicodeDecoder(final Charset charset, final int n) {
        super(charset, 0.5f, 1.0f);
        this.defaultByteOrder = 1;
        this.currentByteOrder = n;
        this.expectedByteOrder = n;
    }
    
    public UnicodeDecoder(final Charset charset, final int n, final int defaultByteOrder) {
        this(charset, n);
        this.defaultByteOrder = defaultByteOrder;
    }
    
    private char decode(final int n, final int n2) {
        if (this.currentByteOrder == 1) {
            return (char)(n << 8 | n2);
        }
        return (char)(n2 << 8 | n);
    }
    
    @Override
    protected CoderResult decodeLoop(final ByteBuffer byteBuffer, final CharBuffer charBuffer) {
        int position = byteBuffer.position();
        try {
            while (byteBuffer.remaining() > 1) {
                final int n = byteBuffer.get() & 0xFF;
                final int n2 = byteBuffer.get() & 0xFF;
                if (this.currentByteOrder == 0) {
                    final char c = (char)(n << 8 | n2);
                    if (c == '\ufeff') {
                        this.currentByteOrder = 1;
                        position += 2;
                        continue;
                    }
                    if (c == '\ufffe') {
                        this.currentByteOrder = 2;
                        position += 2;
                        continue;
                    }
                    this.currentByteOrder = this.defaultByteOrder;
                }
                final char decode = this.decode(n, n2);
                if (decode == '\ufffe') {
                    return CoderResult.malformedForLength(2);
                }
                if (Character.isSurrogate(decode)) {
                    if (!Character.isHighSurrogate(decode)) {
                        return CoderResult.malformedForLength(2);
                    }
                    if (byteBuffer.remaining() < 2) {
                        return CoderResult.UNDERFLOW;
                    }
                    final char decode2 = this.decode(byteBuffer.get() & 0xFF, byteBuffer.get() & 0xFF);
                    if (!Character.isLowSurrogate(decode2)) {
                        return CoderResult.malformedForLength(4);
                    }
                    if (charBuffer.remaining() < 2) {
                        return CoderResult.OVERFLOW;
                    }
                    position += 4;
                    charBuffer.put(decode);
                    charBuffer.put(decode2);
                }
                else {
                    if (!charBuffer.hasRemaining()) {
                        return CoderResult.OVERFLOW;
                    }
                    position += 2;
                    charBuffer.put(decode);
                }
            }
            return CoderResult.UNDERFLOW;
        }
        finally {
            byteBuffer.position(position);
        }
    }
    
    @Override
    protected void implReset() {
        this.currentByteOrder = this.expectedByteOrder;
    }
}
