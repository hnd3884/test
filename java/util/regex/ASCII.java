package java.util.regex;

final class ASCII
{
    static final int UPPER = 256;
    static final int LOWER = 512;
    static final int DIGIT = 1024;
    static final int SPACE = 2048;
    static final int PUNCT = 4096;
    static final int CNTRL = 8192;
    static final int BLANK = 16384;
    static final int HEX = 32768;
    static final int UNDER = 65536;
    static final int ASCII = 65280;
    static final int ALPHA = 768;
    static final int ALNUM = 1792;
    static final int GRAPH = 5888;
    static final int WORD = 67328;
    static final int XDIGIT = 32768;
    private static final int[] ctype;
    
    static int getType(final int n) {
        return ((n & 0xFFFFFF80) == 0x0) ? java.util.regex.ASCII.ctype[n] : 0;
    }
    
    static boolean isType(final int n, final int n2) {
        return (getType(n) & n2) != 0x0;
    }
    
    static boolean isAscii(final int n) {
        return (n & 0xFFFFFF80) == 0x0;
    }
    
    static boolean isAlpha(final int n) {
        return isType(n, 768);
    }
    
    static boolean isDigit(final int n) {
        return (n - 48 | 57 - n) >= 0;
    }
    
    static boolean isAlnum(final int n) {
        return isType(n, 1792);
    }
    
    static boolean isGraph(final int n) {
        return isType(n, 5888);
    }
    
    static boolean isPrint(final int n) {
        return (n - 32 | 126 - n) >= 0;
    }
    
    static boolean isPunct(final int n) {
        return isType(n, 4096);
    }
    
    static boolean isSpace(final int n) {
        return isType(n, 2048);
    }
    
    static boolean isHexDigit(final int n) {
        return isType(n, 32768);
    }
    
    static boolean isOctDigit(final int n) {
        return (n - 48 | 55 - n) >= 0;
    }
    
    static boolean isCntrl(final int n) {
        return isType(n, 8192);
    }
    
    static boolean isLower(final int n) {
        return (n - 97 | 122 - n) >= 0;
    }
    
    static boolean isUpper(final int n) {
        return (n - 65 | 90 - n) >= 0;
    }
    
    static boolean isWord(final int n) {
        return isType(n, 67328);
    }
    
    static int toDigit(final int n) {
        return java.util.regex.ASCII.ctype[n & 0x7F] & 0x3F;
    }
    
    static int toLower(final int n) {
        return isUpper(n) ? (n + 32) : n;
    }
    
    static int toUpper(final int n) {
        return isLower(n) ? (n - 32) : n;
    }
    
    static {
        ctype = new int[] { 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 26624, 10240, 10240, 10240, 10240, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 18432, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 33792, 33793, 33794, 33795, 33796, 33797, 33798, 33799, 33800, 33801, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 33034, 33035, 33036, 33037, 33038, 33039, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285, 286, 287, 288, 289, 290, 291, 4096, 4096, 4096, 4096, 69632, 4096, 33290, 33291, 33292, 33293, 33294, 33295, 528, 529, 530, 531, 532, 533, 534, 535, 536, 537, 538, 539, 540, 541, 542, 543, 544, 545, 546, 547, 4096, 4096, 4096, 4096, 8192 };
    }
}
