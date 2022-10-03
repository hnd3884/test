package sun.nio.cs;

import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public abstract class UnicodeEncoder extends CharsetEncoder
{
    protected static final char BYTE_ORDER_MARK = '\ufeff';
    protected static final char REVERSED_MARK = '\ufffe';
    protected static final int BIG = 0;
    protected static final int LITTLE = 1;
    private int byteOrder;
    private boolean usesMark;
    private boolean needsMark;
    private final Surrogate.Parser sgp;
    
    protected UnicodeEncoder(final Charset charset, final int byteOrder, final boolean b) {
        super(charset, 2.0f, b ? 4.0f : 2.0f, (byteOrder == 0) ? new byte[] { -1, -3 } : new byte[] { -3, -1 });
        this.sgp = new Surrogate.Parser();
        this.needsMark = b;
        this.usesMark = b;
        this.byteOrder = byteOrder;
    }
    
    private void put(final char c, final ByteBuffer byteBuffer) {
        if (this.byteOrder == 0) {
            byteBuffer.put((byte)(c >> 8));
            byteBuffer.put((byte)(c & '\u00ff'));
        }
        else {
            byteBuffer.put((byte)(c & '\u00ff'));
            byteBuffer.put((byte)(c >> 8));
        }
    }
    
    @Override
    protected CoderResult encodeLoop(final CharBuffer charBuffer, final ByteBuffer byteBuffer) {
        int position = charBuffer.position();
        if (this.needsMark && charBuffer.hasRemaining()) {
            if (byteBuffer.remaining() < 2) {
                return CoderResult.OVERFLOW;
            }
            this.put('\ufeff', byteBuffer);
            this.needsMark = false;
        }
        try {
            while (charBuffer.hasRemaining()) {
                final char value = charBuffer.get();
                if (!Character.isSurrogate(value)) {
                    if (byteBuffer.remaining() < 2) {
                        return CoderResult.OVERFLOW;
                    }
                    ++position;
                    this.put(value, byteBuffer);
                }
                else {
                    final int parse = this.sgp.parse(value, charBuffer);
                    if (parse < 0) {
                        return this.sgp.error();
                    }
                    if (byteBuffer.remaining() < 4) {
                        return CoderResult.OVERFLOW;
                    }
                    position += 2;
                    this.put(Character.highSurrogate(parse), byteBuffer);
                    this.put(Character.lowSurrogate(parse), byteBuffer);
                }
            }
            return CoderResult.UNDERFLOW;
        }
        finally {
            charBuffer.position(position);
        }
    }
    
    @Override
    protected void implReset() {
        this.needsMark = this.usesMark;
    }
    
    @Override
    public boolean canEncode(final char c) {
        return !Character.isSurrogate(c);
    }
}
