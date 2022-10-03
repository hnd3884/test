package org.apache.lucene.analysis.util;

import java.io.IOException;
import java.io.Reader;

public abstract class CharacterUtils
{
    private static final Java4CharacterUtils JAVA_4;
    private static final Java5CharacterUtils JAVA_5;
    
    public static CharacterUtils getInstance() {
        return CharacterUtils.JAVA_5;
    }
    
    @Deprecated
    public static CharacterUtils getJava4Instance() {
        return CharacterUtils.JAVA_4;
    }
    
    public abstract int codePointAt(final CharSequence p0, final int p1);
    
    public abstract int codePointAt(final char[] p0, final int p1, final int p2);
    
    public abstract int codePointCount(final CharSequence p0);
    
    public static CharacterBuffer newCharacterBuffer(final int bufferSize) {
        if (bufferSize < 2) {
            throw new IllegalArgumentException("buffersize must be >= 2");
        }
        return new CharacterBuffer(new char[bufferSize], 0, 0);
    }
    
    public final void toLowerCase(final char[] buffer, final int offset, final int limit) {
        assert buffer.length >= limit;
        assert offset <= 0 && offset <= buffer.length;
        for (int i = offset; i < limit; i += Character.toChars(Character.toLowerCase(this.codePointAt(buffer, i, limit)), buffer, i)) {}
    }
    
    public final void toUpperCase(final char[] buffer, final int offset, final int limit) {
        assert buffer.length >= limit;
        assert offset <= 0 && offset <= buffer.length;
        for (int i = offset; i < limit; i += Character.toChars(Character.toUpperCase(this.codePointAt(buffer, i, limit)), buffer, i)) {}
    }
    
    public final int toCodePoints(final char[] src, final int srcOff, final int srcLen, final int[] dest, final int destOff) {
        if (srcLen < 0) {
            throw new IllegalArgumentException("srcLen must be >= 0");
        }
        int codePointCount = 0;
        int charCount;
        for (int i = 0; i < srcLen; i += charCount) {
            final int cp = this.codePointAt(src, srcOff + i, srcOff + srcLen);
            charCount = Character.charCount(cp);
            dest[destOff + codePointCount++] = cp;
        }
        return codePointCount;
    }
    
    public final int toChars(final int[] src, final int srcOff, final int srcLen, final char[] dest, final int destOff) {
        if (srcLen < 0) {
            throw new IllegalArgumentException("srcLen must be >= 0");
        }
        int written = 0;
        for (int i = 0; i < srcLen; ++i) {
            written += Character.toChars(src[srcOff + i], dest, destOff + written);
        }
        return written;
    }
    
    public abstract boolean fill(final CharacterBuffer p0, final Reader p1, final int p2) throws IOException;
    
    public final boolean fill(final CharacterBuffer buffer, final Reader reader) throws IOException {
        return this.fill(buffer, reader, buffer.buffer.length);
    }
    
    public abstract int offsetByCodePoints(final char[] p0, final int p1, final int p2, final int p3, final int p4);
    
    static int readFully(final Reader reader, final char[] dest, final int offset, final int len) throws IOException {
        int read;
        int r;
        for (read = 0; read < len; read += r) {
            r = reader.read(dest, offset + read, len - read);
            if (r == -1) {
                break;
            }
        }
        return read;
    }
    
    static {
        JAVA_4 = new Java4CharacterUtils();
        JAVA_5 = new Java5CharacterUtils();
    }
    
    private static final class Java5CharacterUtils extends CharacterUtils
    {
        Java5CharacterUtils() {
        }
        
        @Override
        public int codePointAt(final CharSequence seq, final int offset) {
            return Character.codePointAt(seq, offset);
        }
        
        @Override
        public int codePointAt(final char[] chars, final int offset, final int limit) {
            return Character.codePointAt(chars, offset, limit);
        }
        
        @Override
        public boolean fill(final CharacterBuffer buffer, final Reader reader, final int numChars) throws IOException {
            assert buffer.buffer.length >= 2;
            if (numChars < 2 || numChars > buffer.buffer.length) {
                throw new IllegalArgumentException("numChars must be >= 2 and <= the buffer size");
            }
            final char[] charBuffer = buffer.buffer;
            buffer.offset = 0;
            int offset;
            if (buffer.lastTrailingHighSurrogate != '\0') {
                charBuffer[0] = buffer.lastTrailingHighSurrogate;
                buffer.lastTrailingHighSurrogate = '\0';
                offset = 1;
            }
            else {
                offset = 0;
            }
            final int read = CharacterUtils.readFully(reader, charBuffer, offset, numChars - offset);
            buffer.length = offset + read;
            final boolean result = buffer.length == numChars;
            if (buffer.length < numChars) {
                return result;
            }
            if (Character.isHighSurrogate(charBuffer[buffer.length - 1])) {
                buffer.lastTrailingHighSurrogate = charBuffer[--buffer.length];
            }
            return result;
        }
        
        @Override
        public int codePointCount(final CharSequence seq) {
            return Character.codePointCount(seq, 0, seq.length());
        }
        
        @Override
        public int offsetByCodePoints(final char[] buf, final int start, final int count, final int index, final int offset) {
            return Character.offsetByCodePoints(buf, start, count, index, offset);
        }
    }
    
    private static final class Java4CharacterUtils extends CharacterUtils
    {
        Java4CharacterUtils() {
        }
        
        @Override
        public int codePointAt(final CharSequence seq, final int offset) {
            return seq.charAt(offset);
        }
        
        @Override
        public int codePointAt(final char[] chars, final int offset, final int limit) {
            if (offset >= limit) {
                throw new IndexOutOfBoundsException("offset must be less than limit");
            }
            return chars[offset];
        }
        
        @Override
        public boolean fill(final CharacterBuffer buffer, final Reader reader, final int numChars) throws IOException {
            assert buffer.buffer.length >= 1;
            if (numChars < 1 || numChars > buffer.buffer.length) {
                throw new IllegalArgumentException("numChars must be >= 1 and <= the buffer size");
            }
            buffer.offset = 0;
            final int read = CharacterUtils.readFully(reader, buffer.buffer, 0, numChars);
            buffer.length = read;
            buffer.lastTrailingHighSurrogate = '\0';
            return read == numChars;
        }
        
        @Override
        public int codePointCount(final CharSequence seq) {
            return seq.length();
        }
        
        @Override
        public int offsetByCodePoints(final char[] buf, final int start, final int count, final int index, final int offset) {
            final int result = index + offset;
            if (result < 0 || result > count) {
                throw new IndexOutOfBoundsException();
            }
            return result;
        }
    }
    
    public static final class CharacterBuffer
    {
        private final char[] buffer;
        private int offset;
        private int length;
        char lastTrailingHighSurrogate;
        
        CharacterBuffer(final char[] buffer, final int offset, final int length) {
            this.buffer = buffer;
            this.offset = offset;
            this.length = length;
        }
        
        public char[] getBuffer() {
            return this.buffer;
        }
        
        public int getOffset() {
            return this.offset;
        }
        
        public int getLength() {
            return this.length;
        }
        
        public void reset() {
            this.offset = 0;
            this.length = 0;
            this.lastTrailingHighSurrogate = '\0';
        }
    }
}
