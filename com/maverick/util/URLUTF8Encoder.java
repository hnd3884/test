package com.maverick.util;

public class URLUTF8Encoder
{
    static final String[] b;
    
    public static String encode(final String s, final boolean b) {
        final StringBuffer sb = new StringBuffer();
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '/' && !b) {
                sb.append(char1);
            }
            else if ('A' <= char1 && char1 <= 'Z') {
                sb.append(char1);
            }
            else if ('a' <= char1 && char1 <= 'z') {
                sb.append(char1);
            }
            else if ('0' <= char1 && char1 <= '9') {
                sb.append(char1);
            }
            else if (char1 == ' ') {
                sb.append("%20");
            }
            else if (char1 == '-' || char1 == '_' || char1 == '.' || char1 == '!' || char1 == '~' || char1 == '*' || char1 == '\'' || char1 == '(' || char1 == ')') {
                sb.append(char1);
            }
            else if (char1 <= '\u007f') {
                sb.append(URLUTF8Encoder.b[char1]);
            }
            else if (char1 <= '\u07ff') {
                sb.append(URLUTF8Encoder.b[0xC0 | char1 >> 6]);
                sb.append(URLUTF8Encoder.b[0x80 | (char1 & '?')]);
            }
            else {
                sb.append(URLUTF8Encoder.b[0xE0 | char1 >> 12]);
                sb.append(URLUTF8Encoder.b[0x80 | (char1 >> 6 & 0x3F)]);
                sb.append(URLUTF8Encoder.b[0x80 | (char1 & '?')]);
            }
        }
        return sb.toString();
    }
    
    public static String decode(final String s) {
        final StringBuffer sb = new StringBuffer();
        final int length = s.length();
        int n = 0;
        int i = 0;
        int n2 = -1;
        while (i < length) {
            final char char1;
            int n4 = 0;
            switch (char1 = s.charAt(i)) {
                case '%': {
                    final char char2 = s.charAt(++i);
                    final int n3 = (Character.isDigit(char2) ? (char2 - '0') : ('\n' + Character.toLowerCase(char2) - 97)) & 0xF;
                    final char char3 = s.charAt(++i);
                    n4 = (n3 << 4 | ((Character.isDigit(char3) ? (char3 - '0') : ('\n' + Character.toLowerCase(char3) - 97)) & 0xF));
                    break;
                }
                case '+': {
                    n4 = 32;
                    break;
                }
                default: {
                    n4 = char1;
                    break;
                }
            }
            if ((n4 & 0xC0) == 0x80) {
                n = (n << 6 | (n4 & 0x3F));
                if (--n2 == 0) {
                    sb.append((char)n);
                }
            }
            else if ((n4 & 0x80) == 0x0) {
                sb.append((char)n4);
            }
            else if ((n4 & 0xE0) == 0xC0) {
                n = (n4 & 0x1F);
                n2 = 1;
            }
            else if ((n4 & 0xF0) == 0xE0) {
                n = (n4 & 0xF);
                n2 = 2;
            }
            else if ((n4 & 0xF8) == 0xF0) {
                n = (n4 & 0x7);
                n2 = 3;
            }
            else if ((n4 & 0xFC) == 0xF8) {
                n = (n4 & 0x3);
                n2 = 4;
            }
            else {
                n = (n4 & 0x1);
                n2 = 5;
            }
            ++i;
        }
        return sb.toString();
    }
    
    static {
        b = new String[] { "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07", "%08", "%09", "%0A", "%0B", "%0C", "%0D", "%0E", "%0F", "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17", "%18", "%19", "%1A", "%1B", "%1C", "%1D", "%1E", "%1F", "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29", "%2A", "%2B", "%2C", "%2D", "%2E", "%2F", "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37", "%38", "%39", "%3A", "%3B", "%3C", "%3D", "%3E", "%3F", "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47", "%48", "%49", "%4A", "%4B", "%4C", "%4D", "%4E", "%4F", "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57", "%58", "%59", "%5A", "%5B", "%5C", "%5D", "%5E", "%5F", "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67", "%68", "%69", "%6A", "%6B", "%6C", "%6D", "%6E", "%6F", "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77", "%78", "%79", "%7A", "%7B", "%7C", "%7D", "%7E", "%7F", "%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87", "%88", "%89", "%8A", "%8B", "%8C", "%8D", "%8E", "%8F", "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97", "%98", "%99", "%9A", "%9B", "%9C", "%9D", "%9E", "%9F", "%A0", "%A1", "%A2", "%A3", "%A4", "%A5", "%A6", "%A7", "%A8", "%A9", "%AA", "%AB", "%AC", "%AD", "%AE", "%AF", "%B0", "%B1", "%B2", "%B3", "%B4", "%B5", "%B6", "%B7", "%B8", "%B9", "%BA", "%BB", "%BC", "%BD", "%BE", "%BF", "%C0", "%C1", "%C2", "%C3", "%C4", "%C5", "%C6", "%C7", "%C8", "%C9", "%CA", "%CB", "%CC", "%CD", "%CE", "%CF", "%D0", "%D1", "%D2", "%D3", "%D4", "%D5", "%D6", "%D7", "%D8", "%D9", "%DA", "%DB", "%DC", "%DD", "%DE", "%DF", "%E0", "%E1", "%E2", "%E3", "%E4", "%E5", "%E6", "%E7", "%E8", "%E9", "%EA", "%EB", "%EC", "%ED", "%EE", "%EF", "%F0", "%F1", "%F2", "%F3", "%F4", "%F5", "%F6", "%F7", "%F8", "%F9", "%FA", "%FB", "%FC", "%FD", "%FE", "%FF" };
    }
}
