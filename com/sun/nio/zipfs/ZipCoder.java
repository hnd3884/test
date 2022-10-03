package com.sun.nio.zipfs;

import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.nio.charset.CoderResult;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

final class ZipCoder
{
    private Charset cs;
    private boolean isutf8;
    private ZipCoder utf8;
    private final ThreadLocal<CharsetDecoder> decTL;
    private final ThreadLocal<CharsetEncoder> encTL;
    
    String toString(final byte[] array, final int n) {
        final CharsetDecoder reset = this.decoder().reset();
        final int n2 = (int)(n * reset.maxCharsPerByte());
        final char[] array2 = new char[n2];
        if (n2 == 0) {
            return new String(array2);
        }
        final ByteBuffer wrap = ByteBuffer.wrap(array, 0, n);
        final CharBuffer wrap2 = CharBuffer.wrap(array2);
        final CoderResult decode = reset.decode(wrap, wrap2, true);
        if (!decode.isUnderflow()) {
            throw new IllegalArgumentException(decode.toString());
        }
        final CoderResult flush = reset.flush(wrap2);
        if (!flush.isUnderflow()) {
            throw new IllegalArgumentException(flush.toString());
        }
        return new String(array2, 0, wrap2.position());
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
        final ByteBuffer wrap = ByteBuffer.wrap(array);
        final CoderResult encode = reset.encode(CharBuffer.wrap(charArray), wrap, true);
        if (!encode.isUnderflow()) {
            throw new IllegalArgumentException(encode.toString());
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
    
    byte[] getBytesUTF8(final String s) {
        if (this.isutf8) {
            return this.getBytes(s);
        }
        if (this.utf8 == null) {
            this.utf8 = new ZipCoder(Charset.forName("UTF-8"));
        }
        return this.utf8.getBytes(s);
    }
    
    String toStringUTF8(final byte[] array, final int n) {
        if (this.isutf8) {
            return this.toString(array, n);
        }
        if (this.utf8 == null) {
            this.utf8 = new ZipCoder(Charset.forName("UTF-8"));
        }
        return this.utf8.toString(array, n);
    }
    
    boolean isUTF8() {
        return this.isutf8;
    }
    
    private ZipCoder(final Charset cs) {
        this.decTL = new ThreadLocal<CharsetDecoder>();
        this.encTL = new ThreadLocal<CharsetEncoder>();
        this.cs = cs;
        this.isutf8 = cs.name().equals("UTF-8");
    }
    
    static ZipCoder get(final Charset charset) {
        return new ZipCoder(charset);
    }
    
    static ZipCoder get(final String s) {
        try {
            return new ZipCoder(Charset.forName(s));
        }
        catch (final Throwable t) {
            t.printStackTrace();
            return new ZipCoder(Charset.defaultCharset());
        }
    }
    
    private CharsetDecoder decoder() {
        CharsetDecoder onUnmappableCharacter = this.decTL.get();
        if (onUnmappableCharacter == null) {
            onUnmappableCharacter = this.cs.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
            this.decTL.set(onUnmappableCharacter);
        }
        return onUnmappableCharacter;
    }
    
    private CharsetEncoder encoder() {
        CharsetEncoder onUnmappableCharacter = this.encTL.get();
        if (onUnmappableCharacter == null) {
            onUnmappableCharacter = this.cs.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
            this.encTL.set(onUnmappableCharacter);
        }
        return onUnmappableCharacter;
    }
}
