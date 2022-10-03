package java.lang;

import sun.nio.cs.HistoricallyNamedCharset;
import java.nio.charset.CharsetEncoder;
import sun.nio.cs.ArrayEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharacterCodingException;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import sun.nio.cs.ArrayDecoder;
import java.nio.charset.CodingErrorAction;
import java.io.UnsupportedEncodingException;
import java.nio.charset.IllegalCharsetNameException;
import sun.misc.MessageUtils;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.nio.charset.Charset;
import java.lang.ref.SoftReference;

class StringCoding
{
    private static final ThreadLocal<SoftReference<StringDecoder>> decoder;
    private static final ThreadLocal<SoftReference<StringEncoder>> encoder;
    private static boolean warnUnsupportedCharset;
    
    private StringCoding() {
    }
    
    private static <T> T deref(final ThreadLocal<SoftReference<T>> threadLocal) {
        final SoftReference softReference = threadLocal.get();
        if (softReference == null) {
            return null;
        }
        return (T)softReference.get();
    }
    
    private static <T> void set(final ThreadLocal<SoftReference<T>> threadLocal, final T t) {
        threadLocal.set((SoftReference)new SoftReference(t));
    }
    
    private static byte[] safeTrim(final byte[] array, final int n, final Charset charset, final boolean b) {
        if (n == array.length && (b || System.getSecurityManager() == null)) {
            return array;
        }
        return Arrays.copyOf(array, n);
    }
    
    private static char[] safeTrim(final char[] array, final int n, final Charset charset, final boolean b) {
        if (n == array.length && (b || System.getSecurityManager() == null)) {
            return array;
        }
        return Arrays.copyOf(array, n);
    }
    
    private static int scale(final int n, final float n2) {
        return (int)(n * (double)n2);
    }
    
    private static Charset lookupCharset(final String s) {
        if (Charset.isSupported(s)) {
            try {
                return Charset.forName(s);
            }
            catch (final UnsupportedCharsetException ex) {
                throw new Error(ex);
            }
        }
        return null;
    }
    
    private static void warnUnsupportedCharset(final String s) {
        if (StringCoding.warnUnsupportedCharset) {
            MessageUtils.err("WARNING: Default charset " + s + " not supported, using ISO-8859-1 instead");
            StringCoding.warnUnsupportedCharset = false;
        }
    }
    
    static char[] decode(final String s, final byte[] array, final int n, final int n2) throws UnsupportedEncodingException {
        StringDecoder stringDecoder = deref(StringCoding.decoder);
        final String s2 = (s == null) ? "ISO-8859-1" : s;
        if (stringDecoder == null || (!s2.equals(stringDecoder.requestedCharsetName()) && !s2.equals(stringDecoder.charsetName()))) {
            stringDecoder = null;
            try {
                final Charset lookupCharset = lookupCharset(s2);
                if (lookupCharset != null) {
                    stringDecoder = new StringDecoder(lookupCharset, s2);
                }
            }
            catch (final IllegalCharsetNameException ex) {}
            if (stringDecoder == null) {
                throw new UnsupportedEncodingException(s2);
            }
            set(StringCoding.decoder, stringDecoder);
        }
        return stringDecoder.decode(array, n, n2);
    }
    
    static char[] decode(final Charset charset, byte[] copyOfRange, int n, final int n2) {
        final CharsetDecoder decoder = charset.newDecoder();
        final char[] array = new char[scale(n2, decoder.maxCharsPerByte())];
        if (n2 == 0) {
            return array;
        }
        boolean b = false;
        if (System.getSecurityManager() != null && !(b = (charset.getClass().getClassLoader0() == null))) {
            copyOfRange = Arrays.copyOfRange(copyOfRange, n, n + n2);
            n = 0;
        }
        decoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).reset();
        if (decoder instanceof ArrayDecoder) {
            return safeTrim(array, ((ArrayDecoder)decoder).decode(copyOfRange, n, n2, array), charset, b);
        }
        final ByteBuffer wrap = ByteBuffer.wrap(copyOfRange, n, n2);
        final CharBuffer wrap2 = CharBuffer.wrap(array);
        try {
            final CoderResult decode = decoder.decode(wrap, wrap2, true);
            if (!decode.isUnderflow()) {
                decode.throwException();
            }
            final CoderResult flush = decoder.flush(wrap2);
            if (!flush.isUnderflow()) {
                flush.throwException();
            }
        }
        catch (final CharacterCodingException ex) {
            throw new Error(ex);
        }
        return safeTrim(array, wrap2.position(), charset, b);
    }
    
    static char[] decode(final byte[] array, final int n, final int n2) {
        final String name = Charset.defaultCharset().name();
        try {
            return decode(name, array, n, n2);
        }
        catch (final UnsupportedEncodingException ex) {
            warnUnsupportedCharset(name);
            try {
                return decode("ISO-8859-1", array, n, n2);
            }
            catch (final UnsupportedEncodingException ex2) {
                MessageUtils.err("ISO-8859-1 charset not available: " + ex2.toString());
                System.exit(1);
                return null;
            }
        }
    }
    
    static byte[] encode(final String s, final char[] array, final int n, final int n2) throws UnsupportedEncodingException {
        StringEncoder stringEncoder = deref(StringCoding.encoder);
        final String s2 = (s == null) ? "ISO-8859-1" : s;
        if (stringEncoder == null || (!s2.equals(stringEncoder.requestedCharsetName()) && !s2.equals(stringEncoder.charsetName()))) {
            stringEncoder = null;
            try {
                final Charset lookupCharset = lookupCharset(s2);
                if (lookupCharset != null) {
                    stringEncoder = new StringEncoder(lookupCharset, s2);
                }
            }
            catch (final IllegalCharsetNameException ex) {}
            if (stringEncoder == null) {
                throw new UnsupportedEncodingException(s2);
            }
            set(StringCoding.encoder, stringEncoder);
        }
        return stringEncoder.encode(array, n, n2);
    }
    
    static byte[] encode(final Charset charset, char[] copyOfRange, int n, final int n2) {
        final CharsetEncoder encoder = charset.newEncoder();
        final byte[] array = new byte[scale(n2, encoder.maxBytesPerChar())];
        if (n2 == 0) {
            return array;
        }
        boolean b = false;
        if (System.getSecurityManager() != null && !(b = (charset.getClass().getClassLoader0() == null))) {
            copyOfRange = Arrays.copyOfRange(copyOfRange, n, n + n2);
            n = 0;
        }
        encoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).reset();
        if (encoder instanceof ArrayEncoder) {
            return safeTrim(array, ((ArrayEncoder)encoder).encode(copyOfRange, n, n2, array), charset, b);
        }
        final ByteBuffer wrap = ByteBuffer.wrap(array);
        final CharBuffer wrap2 = CharBuffer.wrap(copyOfRange, n, n2);
        try {
            final CoderResult encode = encoder.encode(wrap2, wrap, true);
            if (!encode.isUnderflow()) {
                encode.throwException();
            }
            final CoderResult flush = encoder.flush(wrap);
            if (!flush.isUnderflow()) {
                flush.throwException();
            }
        }
        catch (final CharacterCodingException ex) {
            throw new Error(ex);
        }
        return safeTrim(array, wrap.position(), charset, b);
    }
    
    static byte[] encode(final char[] array, final int n, final int n2) {
        final String name = Charset.defaultCharset().name();
        try {
            return encode(name, array, n, n2);
        }
        catch (final UnsupportedEncodingException ex) {
            warnUnsupportedCharset(name);
            try {
                return encode("ISO-8859-1", array, n, n2);
            }
            catch (final UnsupportedEncodingException ex2) {
                MessageUtils.err("ISO-8859-1 charset not available: " + ex2.toString());
                System.exit(1);
                return null;
            }
        }
    }
    
    static {
        decoder = new ThreadLocal<SoftReference<StringDecoder>>();
        encoder = new ThreadLocal<SoftReference<StringEncoder>>();
        StringCoding.warnUnsupportedCharset = true;
    }
    
    private static class StringDecoder
    {
        private final String requestedCharsetName;
        private final Charset cs;
        private final CharsetDecoder cd;
        private final boolean isTrusted;
        
        private StringDecoder(final Charset cs, final String requestedCharsetName) {
            this.requestedCharsetName = requestedCharsetName;
            this.cs = cs;
            this.cd = cs.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            this.isTrusted = (cs.getClass().getClassLoader0() == null);
        }
        
        String charsetName() {
            if (this.cs instanceof HistoricallyNamedCharset) {
                return ((HistoricallyNamedCharset)this.cs).historicalName();
            }
            return this.cs.name();
        }
        
        final String requestedCharsetName() {
            return this.requestedCharsetName;
        }
        
        char[] decode(final byte[] array, final int n, final int n2) {
            final char[] array2 = new char[scale(n2, this.cd.maxCharsPerByte())];
            if (n2 == 0) {
                return array2;
            }
            if (this.cd instanceof ArrayDecoder) {
                return safeTrim(array2, ((ArrayDecoder)this.cd).decode(array, n, n2, array2), this.cs, this.isTrusted);
            }
            this.cd.reset();
            final ByteBuffer wrap = ByteBuffer.wrap(array, n, n2);
            final CharBuffer wrap2 = CharBuffer.wrap(array2);
            try {
                final CoderResult decode = this.cd.decode(wrap, wrap2, true);
                if (!decode.isUnderflow()) {
                    decode.throwException();
                }
                final CoderResult flush = this.cd.flush(wrap2);
                if (!flush.isUnderflow()) {
                    flush.throwException();
                }
            }
            catch (final CharacterCodingException ex) {
                throw new Error(ex);
            }
            return safeTrim(array2, wrap2.position(), this.cs, this.isTrusted);
        }
    }
    
    private static class StringEncoder
    {
        private Charset cs;
        private CharsetEncoder ce;
        private final String requestedCharsetName;
        private final boolean isTrusted;
        
        private StringEncoder(final Charset cs, final String requestedCharsetName) {
            this.requestedCharsetName = requestedCharsetName;
            this.cs = cs;
            this.ce = cs.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            this.isTrusted = (cs.getClass().getClassLoader0() == null);
        }
        
        String charsetName() {
            if (this.cs instanceof HistoricallyNamedCharset) {
                return ((HistoricallyNamedCharset)this.cs).historicalName();
            }
            return this.cs.name();
        }
        
        final String requestedCharsetName() {
            return this.requestedCharsetName;
        }
        
        byte[] encode(final char[] array, final int n, final int n2) {
            final byte[] array2 = new byte[scale(n2, this.ce.maxBytesPerChar())];
            if (n2 == 0) {
                return array2;
            }
            if (this.ce instanceof ArrayEncoder) {
                return safeTrim(array2, ((ArrayEncoder)this.ce).encode(array, n, n2, array2), this.cs, this.isTrusted);
            }
            this.ce.reset();
            final ByteBuffer wrap = ByteBuffer.wrap(array2);
            final CharBuffer wrap2 = CharBuffer.wrap(array, n, n2);
            try {
                final CoderResult encode = this.ce.encode(wrap2, wrap, true);
                if (!encode.isUnderflow()) {
                    encode.throwException();
                }
                final CoderResult flush = this.ce.flush(wrap);
                if (!flush.isUnderflow()) {
                    flush.throwException();
                }
            }
            catch (final CharacterCodingException ex) {
                throw new Error(ex);
            }
            return safeTrim(array2, wrap.position(), this.cs, this.isTrusted);
        }
    }
}
