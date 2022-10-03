package sun.nio.cs.ext;

import java.nio.charset.CoderResult;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import sun.nio.cs.Surrogate;
import java.nio.charset.CharsetEncoder;

public abstract class DoubleByteEncoder extends CharsetEncoder
{
    private short[] index1;
    private String[] index2;
    private final Surrogate.Parser sgp;
    
    protected DoubleByteEncoder(final Charset charset, final short[] index1, final String[] index2) {
        super(charset, 2.0f, 2.0f);
        this.sgp = new Surrogate.Parser();
        this.index1 = index1;
        this.index2 = index2;
    }
    
    protected DoubleByteEncoder(final Charset charset, final short[] index1, final String[] index2, final float n, final float n2) {
        super(charset, n, n2);
        this.sgp = new Surrogate.Parser();
        this.index1 = index1;
        this.index2 = index2;
    }
    
    protected DoubleByteEncoder(final Charset charset, final short[] index1, final String[] index2, final byte[] array) {
        super(charset, 2.0f, 2.0f, array);
        this.sgp = new Surrogate.Parser();
        this.index1 = index1;
        this.index2 = index2;
    }
    
    protected DoubleByteEncoder(final Charset charset, final short[] index1, final String[] index2, final byte[] array, final float n, final float n2) {
        super(charset, n, n2, array);
        this.sgp = new Surrogate.Parser();
        this.index1 = index1;
        this.index2 = index2;
    }
    
    @Override
    public boolean canEncode(final char c) {
        return this.encodeSingle(c) != -1 || this.encodeDouble(c) != 0;
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
                if (Character.isSurrogate(c)) {
                    if (this.sgp.parse(c, array, i, n) < 0) {
                        return this.sgp.error();
                    }
                    if (n - i < 2) {
                        return CoderResult.UNDERFLOW;
                    }
                    final char c2 = array[i + 1];
                    final byte[] array3 = new byte[2];
                    final byte[] encodeSurrogate = this.encodeSurrogate(c, c2);
                    if (encodeSurrogate == null) {
                        return this.sgp.unmappableResult();
                    }
                    if (n3 - n2 < 2) {
                        return CoderResult.OVERFLOW;
                    }
                    array2[n2++] = encodeSurrogate[0];
                    array2[n2++] = encodeSurrogate[1];
                    i += 2;
                }
                else {
                    if (c >= '\ufffe') {
                        return CoderResult.unmappableForLength(1);
                    }
                    final int encodeSingle = this.encodeSingle(c);
                    if (encodeSingle != -1) {
                        if (n3 - n2 < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        array2[n2++] = (byte)encodeSingle;
                        ++i;
                    }
                    else {
                        final int encodeDouble = this.encodeDouble(c);
                        if (encodeDouble == 0 || c == '\0') {
                            return CoderResult.unmappableForLength(1);
                        }
                        if (n3 - n2 < 2) {
                            return CoderResult.OVERFLOW;
                        }
                        array2[n2++] = (byte)((encodeDouble & 0xFF00) >> 8);
                        array2[n2++] = (byte)(encodeDouble & 0xFF);
                        ++i;
                    }
                }
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
                final char value = charBuffer.get();
                if (Character.isSurrogate(value)) {
                    final int parse;
                    if ((parse = this.sgp.parse(value, charBuffer)) < 0) {
                        return this.sgp.error();
                    }
                    final char low = Surrogate.low(parse);
                    final byte[] array = new byte[2];
                    final byte[] encodeSurrogate = this.encodeSurrogate(value, low);
                    if (encodeSurrogate == null) {
                        return this.sgp.unmappableResult();
                    }
                    if (byteBuffer.remaining() < 2) {
                        return CoderResult.OVERFLOW;
                    }
                    position += 2;
                    byteBuffer.put(encodeSurrogate[0]);
                    byteBuffer.put(encodeSurrogate[1]);
                }
                else {
                    if (value >= '\ufffe') {
                        return CoderResult.unmappableForLength(1);
                    }
                    final int encodeSingle = this.encodeSingle(value);
                    if (encodeSingle != -1) {
                        if (byteBuffer.remaining() < 1) {
                            return CoderResult.OVERFLOW;
                        }
                        ++position;
                        byteBuffer.put((byte)encodeSingle);
                    }
                    else {
                        final int encodeDouble = this.encodeDouble(value);
                        if (encodeDouble == 0 || value == '\0') {
                            return CoderResult.unmappableForLength(1);
                        }
                        if (byteBuffer.remaining() < 2) {
                            return CoderResult.OVERFLOW;
                        }
                        ++position;
                        byteBuffer.put((byte)((encodeDouble & 0xFF00) >> 8));
                        byteBuffer.put((byte)encodeDouble);
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
    
    protected int encodeDouble(final char c) {
        final int n = this.index1[(c & '\uff00') >> 8] << 8;
        return this.index2[n >> 12].charAt((n & 0xFFF) + (c & '\u00ff'));
    }
    
    protected int encodeSingle(final char c) {
        if (c < '\u0080') {
            return (byte)c;
        }
        return -1;
    }
    
    protected byte[] encodeSurrogate(final char c, final char c2) {
        return null;
    }
}
