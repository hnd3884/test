package java.util.zip;

import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import sun.nio.cs.ArrayEncoder;
import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import sun.nio.cs.ArrayDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

final class ZipCoder
{
    private Charset cs;
    private CharsetDecoder dec;
    private CharsetEncoder enc;
    private boolean isUTF8;
    private ZipCoder utf8;
    
    String toString(final byte[] array, final int n) {
        final CharsetDecoder reset = this.decoder().reset();
        final int n2 = (int)(n * reset.maxCharsPerByte());
        final char[] array2 = new char[n2];
        if (n2 == 0) {
            return new String(array2);
        }
        if (this.isUTF8 && reset instanceof ArrayDecoder) {
            final int decode = ((ArrayDecoder)reset).decode(array, 0, n, array2);
            if (decode == -1) {
                throw new IllegalArgumentException("MALFORMED");
            }
            return new String(array2, 0, decode);
        }
        else {
            final ByteBuffer wrap = ByteBuffer.wrap(array, 0, n);
            final CharBuffer wrap2 = CharBuffer.wrap(array2);
            final CoderResult decode2 = reset.decode(wrap, wrap2, true);
            if (!decode2.isUnderflow()) {
                throw new IllegalArgumentException(decode2.toString());
            }
            final CoderResult flush = reset.flush(wrap2);
            if (!flush.isUnderflow()) {
                throw new IllegalArgumentException(flush.toString());
            }
            return new String(array2, 0, wrap2.position());
        }
    }
    
    String toString(final byte[] array) {
        return this.toString(array, array.length);
    }
    
    byte[] getBytes(final String s) {
        final CharsetEncoder reset = this.encoder().reset();
        final char[] charArray = s.toCharArray();
        final int n = (int)(charArray.length * reset.maxBytesPerChar());
        final byte[] array = new byte[n];
        if (n == 0) {
            return array;
        }
        if (this.isUTF8 && reset instanceof ArrayEncoder) {
            final int encode = ((ArrayEncoder)reset).encode(charArray, 0, charArray.length, array);
            if (encode == -1) {
                throw new IllegalArgumentException("MALFORMED");
            }
            return Arrays.copyOf(array, encode);
        }
        else {
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            final CoderResult encode2 = reset.encode(CharBuffer.wrap(charArray), wrap, true);
            if (!encode2.isUnderflow()) {
                throw new IllegalArgumentException(encode2.toString());
            }
            final CoderResult flush = reset.flush(wrap);
            if (!flush.isUnderflow()) {
                throw new IllegalArgumentException(flush.toString());
            }
            if (wrap.position() == array.length) {
                return array;
            }
            return Arrays.copyOf(array, wrap.position());
        }
    }
    
    byte[] getBytesUTF8(final String s) {
        if (this.isUTF8) {
            return this.getBytes(s);
        }
        if (this.utf8 == null) {
            this.utf8 = new ZipCoder(StandardCharsets.UTF_8);
        }
        return this.utf8.getBytes(s);
    }
    
    String toStringUTF8(final byte[] array, final int n) {
        if (this.isUTF8) {
            return this.toString(array, n);
        }
        if (this.utf8 == null) {
            this.utf8 = new ZipCoder(StandardCharsets.UTF_8);
        }
        return this.utf8.toString(array, n);
    }
    
    boolean isUTF8() {
        return this.isUTF8;
    }
    
    private ZipCoder(final Charset cs) {
        this.cs = cs;
        this.isUTF8 = cs.name().equals(StandardCharsets.UTF_8.name());
    }
    
    static ZipCoder get(final Charset charset) {
        return new ZipCoder(charset);
    }
    
    private CharsetDecoder decoder() {
        if (this.dec == null) {
            this.dec = this.cs.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        }
        return this.dec;
    }
    
    private CharsetEncoder encoder() {
        if (this.enc == null) {
            this.enc = this.cs.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        }
        return this.enc;
    }
}
