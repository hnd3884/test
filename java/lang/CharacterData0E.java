package java.lang;

class CharacterData0E extends CharacterData
{
    static final CharacterData instance;
    static final char[] X;
    static final char[] Y;
    static final int[] A;
    static final String A_DATA = "\u7800\u0000\u4800\u1010\u7800\u0000\u7800\u0000\u4800\u1010\u4800\u1010\u4000\u3006\u4000\u3006";
    static final char[] B;
    
    @Override
    int getProperties(final int n) {
        final char c = (char)n;
        return CharacterData0E.A[CharacterData0E.Y[CharacterData0E.X[c >> 5] | (c >> 1 & 0xF)] | (c & '\u0001')];
    }
    
    int getPropertiesEx(final int n) {
        final char c = (char)n;
        return CharacterData0E.B[CharacterData0E.Y[CharacterData0E.X[c >> 5] | (c >> 1 & 0xF)] | (c & '\u0001')];
    }
    
    @Override
    boolean isOtherLowercase(final int n) {
        return (this.getPropertiesEx(n) & 0x1) != 0x0;
    }
    
    @Override
    boolean isOtherUppercase(final int n) {
        return (this.getPropertiesEx(n) & 0x2) != 0x0;
    }
    
    @Override
    boolean isOtherAlphabetic(final int n) {
        return (this.getPropertiesEx(n) & 0x4) != 0x0;
    }
    
    @Override
    boolean isIdeographic(final int n) {
        return (this.getPropertiesEx(n) & 0x10) != 0x0;
    }
    
    @Override
    int getType(final int n) {
        return this.getProperties(n) & 0x1F;
    }
    
    @Override
    boolean isJavaIdentifierStart(final int n) {
        return (this.getProperties(n) & 0x7000) >= 20480;
    }
    
    @Override
    boolean isJavaIdentifierPart(final int n) {
        return (this.getProperties(n) & 0x3000) != 0x0;
    }
    
    @Override
    boolean isUnicodeIdentifierStart(final int n) {
        return (this.getProperties(n) & 0x7000) == 0x7000;
    }
    
    @Override
    boolean isUnicodeIdentifierPart(final int n) {
        return (this.getProperties(n) & 0x1000) != 0x0;
    }
    
    @Override
    boolean isIdentifierIgnorable(final int n) {
        return (this.getProperties(n) & 0x7000) == 0x1000;
    }
    
    @Override
    int toLowerCase(final int n) {
        int n2 = n;
        final int properties = this.getProperties(n);
        if ((properties & 0x20000) != 0x0) {
            n2 = n + (properties << 5 >> 23);
        }
        return n2;
    }
    
    @Override
    int toUpperCase(final int n) {
        int n2 = n;
        final int properties = this.getProperties(n);
        if ((properties & 0x10000) != 0x0) {
            n2 = n - (properties << 5 >> 23);
        }
        return n2;
    }
    
    @Override
    int toTitleCase(final int n) {
        int upperCase = n;
        final int properties = this.getProperties(n);
        if ((properties & 0x8000) != 0x0) {
            if ((properties & 0x10000) == 0x0) {
                upperCase = n + 1;
            }
            else if ((properties & 0x20000) == 0x0) {
                upperCase = n - 1;
            }
        }
        else if ((properties & 0x10000) != 0x0) {
            upperCase = this.toUpperCase(n);
        }
        return upperCase;
    }
    
    @Override
    int digit(final int n, final int n2) {
        int n3 = -1;
        if (n2 >= 2 && n2 <= 36) {
            final int properties = this.getProperties(n);
            if ((properties & 0x1F) == 0x9) {
                n3 = (n + ((properties & 0x3E0) >> 5) & 0x1F);
            }
            else if ((properties & 0xC00) == 0xC00) {
                n3 = (n + ((properties & 0x3E0) >> 5) & 0x1F) + 10;
            }
        }
        return (n3 < n2) ? n3 : -1;
    }
    
    @Override
    int getNumericValue(final int n) {
        final int properties = this.getProperties(n);
        int n2 = 0;
        switch (properties & 0xC00) {
            default: {
                n2 = -1;
                break;
            }
            case 1024: {
                n2 = (n + ((properties & 0x3E0) >> 5) & 0x1F);
                break;
            }
            case 2048: {
                n2 = -2;
                break;
            }
            case 3072: {
                n2 = (n + ((properties & 0x3E0) >> 5) & 0x1F) + 10;
                break;
            }
        }
        return n2;
    }
    
    @Override
    boolean isWhitespace(final int n) {
        return (this.getProperties(n) & 0x7000) == 0x4000;
    }
    
    @Override
    byte getDirectionality(final int n) {
        byte b = (byte)((this.getProperties(n) & 0x78000000) >> 27);
        if (b == 15) {
            b = -1;
        }
        return b;
    }
    
    @Override
    boolean isMirrored(final int n) {
        return (this.getProperties(n) & Integer.MIN_VALUE) != 0x0;
    }
    
    private CharacterData0E() {
    }
    
    static {
        instance = new CharacterData0E();
        X = "\u0000\u0010\u0010\u0010    0000000@                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                ".toCharArray();
        Y = "\u0000\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002".toCharArray();
        A = new int[8];
        B = "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000".toCharArray();
        final char[] charArray = "\u7800\u0000\u4800\u1010\u7800\u0000\u7800\u0000\u4800\u1010\u4800\u1010\u4000\u3006\u4000\u3006".toCharArray();
        assert charArray.length == 16;
        for (int i = 0, n = 0; i < 16; CharacterData0E.A[n++] = (charArray[i++] << 16 | charArray[i++])) {}
    }
}
