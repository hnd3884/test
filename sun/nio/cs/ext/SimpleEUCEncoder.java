package sun.nio.cs.ext;

import java.nio.charset.CoderResult;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import sun.nio.cs.Surrogate;
import java.nio.charset.CharsetEncoder;

public abstract class SimpleEUCEncoder extends CharsetEncoder
{
    protected short[] index1;
    protected String index2;
    protected String index2a;
    protected String index2b;
    protected String index2c;
    protected int mask1;
    protected int mask2;
    protected int shift;
    private byte[] outputByte;
    private final Surrogate.Parser sgp;
    
    protected SimpleEUCEncoder(final Charset charset) {
        super(charset, 3.0f, 4.0f);
        this.outputByte = new byte[4];
        this.sgp = new Surrogate.Parser();
    }
    
    @Override
    public boolean canEncode(final char c) {
        int n = this.index1[(c & this.mask1) >> this.shift] + (c & this.mask2);
        String s;
        if (n < 7500) {
            s = this.index2;
        }
        else if (n < 15000) {
            n -= 7500;
            s = this.index2a;
        }
        else if (n < 22500) {
            n -= 15000;
            s = this.index2b;
        }
        else {
            n -= 22500;
            s = this.index2c;
        }
        return s.charAt(2 * n) != '\0' || s.charAt(2 * n + 1) != '\0' || c == '\0';
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
                boolean b = true;
                final char c = array[i];
                if (Character.isSurrogate(c)) {
                    if (this.sgp.parse(c, array, i, n2) < 0) {
                        return this.sgp.error();
                    }
                    return this.sgp.unmappableResult();
                }
                else {
                    if (c >= '\ufffe') {
                        return CoderResult.unmappableForLength(1);
                    }
                    int n6 = this.index1[(c & this.mask1) >> this.shift] + (c & this.mask2);
                    String s;
                    if (n6 < 7500) {
                        s = this.index2;
                    }
                    else if (n6 < 15000) {
                        n6 -= 7500;
                        s = this.index2a;
                    }
                    else if (n6 < 22500) {
                        n6 -= 15000;
                        s = this.index2b;
                    }
                    else {
                        n6 -= 22500;
                        s = this.index2c;
                    }
                    final char char1 = s.charAt(2 * n6);
                    this.outputByte[0] = (byte)((char1 & '\uff00') >> 8);
                    this.outputByte[1] = (byte)(char1 & '\u00ff');
                    final char char2 = s.charAt(2 * n6 + 1);
                    this.outputByte[2] = (byte)((char2 & '\uff00') >> 8);
                    this.outputByte[3] = (byte)(char2 & '\u00ff');
                    for (int j = 0; j < this.outputByte.length; ++j) {
                        if (this.outputByte[j] != 0) {
                            b = false;
                            break;
                        }
                    }
                    if (b && c != '\0') {
                        return CoderResult.unmappableForLength(1);
                    }
                    int n7;
                    int length;
                    for (n7 = 0, length = this.outputByte.length; length > 1 && this.outputByte[n7++] == 0; --length) {}
                    if (n5 + length > n4) {
                        return CoderResult.OVERFLOW;
                    }
                    for (int k = this.outputByte.length - length; k < this.outputByte.length; ++k) {
                        array2[n5++] = this.outputByte[k];
                    }
                    ++i;
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
                final char value = charBuffer.get();
                boolean b = true;
                if (Character.isSurrogate(value)) {
                    if (this.sgp.parse(value, charBuffer) < 0) {
                        return this.sgp.error();
                    }
                    return this.sgp.unmappableResult();
                }
                else {
                    if (value >= '\ufffe') {
                        return CoderResult.unmappableForLength(1);
                    }
                    int n = this.index1[(value & this.mask1) >> this.shift] + (value & this.mask2);
                    String s;
                    if (n < 7500) {
                        s = this.index2;
                    }
                    else if (n < 15000) {
                        n -= 7500;
                        s = this.index2a;
                    }
                    else if (n < 22500) {
                        n -= 15000;
                        s = this.index2b;
                    }
                    else {
                        n -= 22500;
                        s = this.index2c;
                    }
                    final char char1 = s.charAt(2 * n);
                    this.outputByte[0] = (byte)((char1 & '\uff00') >> 8);
                    this.outputByte[1] = (byte)(char1 & '\u00ff');
                    final char char2 = s.charAt(2 * n + 1);
                    this.outputByte[2] = (byte)((char2 & '\uff00') >> 8);
                    this.outputByte[3] = (byte)(char2 & '\u00ff');
                    for (int i = 0; i < this.outputByte.length; ++i) {
                        if (this.outputByte[i] != 0) {
                            b = false;
                            break;
                        }
                    }
                    if (b && value != '\0') {
                        return CoderResult.unmappableForLength(1);
                    }
                    int n2;
                    int length;
                    for (n2 = 0, length = this.outputByte.length; length > 1 && this.outputByte[n2++] == 0; --length) {}
                    if (byteBuffer.remaining() < length) {
                        return CoderResult.OVERFLOW;
                    }
                    for (int j = this.outputByte.length - length; j < this.outputByte.length; ++j) {
                        byteBuffer.put(this.outputByte[j]);
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
    
    public byte encode(final char c) {
        return (byte)this.index2.charAt(this.index1[(c & this.mask1) >> this.shift] + (c & this.mask2));
    }
}
