package sun.nio.cs;

import java.nio.CharBuffer;
import java.nio.charset.CoderResult;

public class Surrogate
{
    public static final char MIN_HIGH = '\ud800';
    public static final char MAX_HIGH = '\udbff';
    public static final char MIN_LOW = '\udc00';
    public static final char MAX_LOW = '\udfff';
    public static final char MIN = '\ud800';
    public static final char MAX = '\udfff';
    public static final int UCS4_MIN = 65536;
    public static final int UCS4_MAX = 1114111;
    
    private Surrogate() {
    }
    
    public static boolean isHigh(final int n) {
        return 55296 <= n && n <= 56319;
    }
    
    public static boolean isLow(final int n) {
        return 56320 <= n && n <= 57343;
    }
    
    public static boolean is(final int n) {
        return 55296 <= n && n <= 57343;
    }
    
    public static boolean neededFor(final int n) {
        return Character.isSupplementaryCodePoint(n);
    }
    
    public static char high(final int n) {
        assert Character.isSupplementaryCodePoint(n);
        return Character.highSurrogate(n);
    }
    
    public static char low(final int n) {
        assert Character.isSupplementaryCodePoint(n);
        return Character.lowSurrogate(n);
    }
    
    public static int toUCS4(final char c, final char c2) {
        assert Character.isHighSurrogate(c) && Character.isLowSurrogate(c2);
        return Character.toCodePoint(c, c2);
    }
    
    public static class Parser
    {
        private int character;
        private CoderResult error;
        private boolean isPair;
        
        public Parser() {
            this.error = CoderResult.UNDERFLOW;
        }
        
        public int character() {
            assert this.error == null;
            return this.character;
        }
        
        public boolean isPair() {
            assert this.error == null;
            return this.isPair;
        }
        
        public int increment() {
            assert this.error == null;
            return this.isPair ? 2 : 1;
        }
        
        public CoderResult error() {
            assert this.error != null;
            return this.error;
        }
        
        public CoderResult unmappableResult() {
            assert this.error == null;
            return CoderResult.unmappableForLength(this.isPair ? 2 : 1);
        }
        
        public int parse(final char character, final CharBuffer charBuffer) {
            if (Character.isHighSurrogate(character)) {
                if (!charBuffer.hasRemaining()) {
                    this.error = CoderResult.UNDERFLOW;
                    return -1;
                }
                final char value = charBuffer.get();
                if (Character.isLowSurrogate(value)) {
                    this.character = Character.toCodePoint(character, value);
                    this.isPair = true;
                    this.error = null;
                    return this.character;
                }
                this.error = CoderResult.malformedForLength(1);
                return -1;
            }
            else {
                if (Character.isLowSurrogate(character)) {
                    this.error = CoderResult.malformedForLength(1);
                    return -1;
                }
                this.character = character;
                this.isPair = false;
                this.error = null;
                return this.character;
            }
        }
        
        public int parse(final char character, final char[] array, final int n, final int n2) {
            assert array[n] == character;
            if (Character.isHighSurrogate(character)) {
                if (n2 - n < 2) {
                    this.error = CoderResult.UNDERFLOW;
                    return -1;
                }
                final char c = array[n + 1];
                if (Character.isLowSurrogate(c)) {
                    this.character = Character.toCodePoint(character, c);
                    this.isPair = true;
                    this.error = null;
                    return this.character;
                }
                this.error = CoderResult.malformedForLength(1);
                return -1;
            }
            else {
                if (Character.isLowSurrogate(character)) {
                    this.error = CoderResult.malformedForLength(1);
                    return -1;
                }
                this.character = character;
                this.isPair = false;
                this.error = null;
                return this.character;
            }
        }
    }
    
    public static class Generator
    {
        private CoderResult error;
        
        public Generator() {
            this.error = CoderResult.OVERFLOW;
        }
        
        public CoderResult error() {
            assert this.error != null;
            return this.error;
        }
        
        public int generate(final int n, final int n2, final CharBuffer charBuffer) {
            if (Character.isBmpCodePoint(n)) {
                final char c = (char)n;
                if (Character.isSurrogate(c)) {
                    this.error = CoderResult.malformedForLength(n2);
                    return -1;
                }
                if (charBuffer.remaining() < 1) {
                    this.error = CoderResult.OVERFLOW;
                    return -1;
                }
                charBuffer.put(c);
                this.error = null;
                return 1;
            }
            else {
                if (!Character.isValidCodePoint(n)) {
                    this.error = CoderResult.unmappableForLength(n2);
                    return -1;
                }
                if (charBuffer.remaining() < 2) {
                    this.error = CoderResult.OVERFLOW;
                    return -1;
                }
                charBuffer.put(Character.highSurrogate(n));
                charBuffer.put(Character.lowSurrogate(n));
                this.error = null;
                return 2;
            }
        }
        
        public int generate(final int n, final int n2, final char[] array, final int n3, final int n4) {
            if (Character.isBmpCodePoint(n)) {
                final char c = (char)n;
                if (Character.isSurrogate(c)) {
                    this.error = CoderResult.malformedForLength(n2);
                    return -1;
                }
                if (n4 - n3 < 1) {
                    this.error = CoderResult.OVERFLOW;
                    return -1;
                }
                array[n3] = c;
                this.error = null;
                return 1;
            }
            else {
                if (!Character.isValidCodePoint(n)) {
                    this.error = CoderResult.unmappableForLength(n2);
                    return -1;
                }
                if (n4 - n3 < 2) {
                    this.error = CoderResult.OVERFLOW;
                    return -1;
                }
                array[n3] = Character.highSurrogate(n);
                array[n3 + 1] = Character.lowSurrogate(n);
                this.error = null;
                return 2;
            }
        }
    }
}
