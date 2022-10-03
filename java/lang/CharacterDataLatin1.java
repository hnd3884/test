package java.lang;

class CharacterDataLatin1 extends CharacterData
{
    static char[] sharpsMap;
    static final CharacterDataLatin1 instance;
    static final int[] A;
    static final String A_DATA = "\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u5800\u400f\u5000\u400f\u5800\u400f\u6000\u400f\u5000\u400f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u5000\u400f\u5000\u400f\u5000\u400f\u5800\u400f\u6000\u400c\u6800\u0018\u6800\u0018\u2800\u0018\u2800\u601a\u2800\u0018\u6800\u0018\u6800\u0018\ue800\u0015\ue800\u0016\u6800\u0018\u2000\u0019\u3800\u0018\u2000\u0014\u3800\u0018\u3800\u0018\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u3800\u0018\u6800\u0018\ue800\u0019\u6800\u0019\ue800\u0019\u6800\u0018\u6800\u0018\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\ue800\u0015\u6800\u0018\ue800\u0016\u6800\u001b\u6800\u5017\u6800\u001b\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\ue800\u0015\u6800\u0019\ue800\u0016\u6800\u0019\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u5000\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u3800\f\u6800\u0018\u2800\u601a\u2800\u601a\u2800\u601a\u2800\u601a\u6800\u001c\u6800\u0018\u6800\u001b\u6800\u001c\u0000\u7005\ue800\u001d\u6800\u0019\u4800\u1010\u6800\u001c\u6800\u001b\u2800\u001c\u2800\u0019\u1800\u060b\u1800\u060b\u6800\u001b\u07fd\u7002\u6800\u0018\u6800\u0018\u6800\u001b\u1800\u050b\u0000\u7005\ue800\u001e\u6800\u080b\u6800\u080b\u6800\u080b\u6800\u0018\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u6800\u0019\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u07fd\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u6800\u0019\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u061d\u7002";
    static final char[] B;
    
    @Override
    int getProperties(final int n) {
        return CharacterDataLatin1.A[(char)n];
    }
    
    int getPropertiesEx(final int n) {
        return CharacterDataLatin1.B[(char)n];
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
        if ((properties & 0x20000) != 0x0 && (properties & 0x7FC0000) != 0x7FC0000) {
            n2 = n + (properties << 5 >> 23);
        }
        return n2;
    }
    
    @Override
    int toUpperCase(final int n) {
        int n2 = n;
        final int properties = this.getProperties(n);
        if ((properties & 0x10000) != 0x0) {
            if ((properties & 0x7FC0000) != 0x7FC0000) {
                n2 = n - (properties << 5 >> 23);
            }
            else if (n == 181) {
                n2 = 924;
            }
        }
        return n2;
    }
    
    @Override
    int toTitleCase(final int n) {
        return this.toUpperCase(n);
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
    
    @Override
    int toUpperCaseEx(final int n) {
        int n2 = n;
        final int properties = this.getProperties(n);
        if ((properties & 0x10000) != 0x0) {
            if ((properties & 0x7FC0000) != 0x7FC0000) {
                n2 = n - (properties << 5 >> 23);
            }
            else {
                switch (n) {
                    case 181: {
                        n2 = 924;
                        break;
                    }
                    default: {
                        n2 = -1;
                        break;
                    }
                }
            }
        }
        return n2;
    }
    
    @Override
    char[] toUpperCaseCharArray(final int n) {
        char[] sharpsMap = { (char)n };
        if (n == 223) {
            sharpsMap = CharacterDataLatin1.sharpsMap;
        }
        return sharpsMap;
    }
    
    private CharacterDataLatin1() {
    }
    
    static {
        CharacterDataLatin1.sharpsMap = new char[] { 'S', 'S' };
        instance = new CharacterDataLatin1();
        A = new int[256];
        B = "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0001\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000".toCharArray();
        final char[] charArray = "\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u5800\u400f\u5000\u400f\u5800\u400f\u6000\u400f\u5000\u400f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u5000\u400f\u5000\u400f\u5000\u400f\u5800\u400f\u6000\u400c\u6800\u0018\u6800\u0018\u2800\u0018\u2800\u601a\u2800\u0018\u6800\u0018\u6800\u0018\ue800\u0015\ue800\u0016\u6800\u0018\u2000\u0019\u3800\u0018\u2000\u0014\u3800\u0018\u3800\u0018\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u1800\u3609\u3800\u0018\u6800\u0018\ue800\u0019\u6800\u0019\ue800\u0019\u6800\u0018\u6800\u0018\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\u0082\u7fe1\ue800\u0015\u6800\u0018\ue800\u0016\u6800\u001b\u6800\u5017\u6800\u001b\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\u0081\u7fe2\ue800\u0015\u6800\u0019\ue800\u0016\u6800\u0019\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u5000\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u4800\u100f\u3800\f\u6800\u0018\u2800\u601a\u2800\u601a\u2800\u601a\u2800\u601a\u6800\u001c\u6800\u0018\u6800\u001b\u6800\u001c\u0000\u7005\ue800\u001d\u6800\u0019\u4800\u1010\u6800\u001c\u6800\u001b\u2800\u001c\u2800\u0019\u1800\u060b\u1800\u060b\u6800\u001b\u07fd\u7002\u6800\u0018\u6800\u0018\u6800\u001b\u1800\u050b\u0000\u7005\ue800\u001e\u6800\u080b\u6800\u080b\u6800\u080b\u6800\u0018\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u6800\u0019\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u0082\u7001\u07fd\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u6800\u0019\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u0081\u7002\u061d\u7002".toCharArray();
        assert charArray.length == 512;
        for (int i = 0, n = 0; i < 512; CharacterDataLatin1.A[n++] = (charArray[i++] << 16 | charArray[i++])) {}
    }
}
