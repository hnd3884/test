package com.sun.xml.internal.messaging.saaj.util;

public class ParseUtil
{
    private static char unescape(final String s, final int i) {
        return (char)Integer.parseInt(s.substring(i + 1, i + 3), 16);
    }
    
    public static String decode(final String s) {
        final StringBuffer sb = new StringBuffer();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c != '%') {
                ++i;
            }
            else {
                try {
                    c = unescape(s, i);
                    i += 3;
                    if ((c & '\u0080') != 0x0) {
                        switch (c >> 4) {
                            case 12:
                            case 13: {
                                final char c2 = unescape(s, i);
                                i += 3;
                                c = (char)((c & '\u001f') << 6 | (c2 & '?'));
                                break;
                            }
                            case 14: {
                                final char c2 = unescape(s, i);
                                i += 3;
                                final char c3 = unescape(s, i);
                                i += 3;
                                c = (char)((c & '\u000f') << 12 | (c2 & '?') << 6 | (c3 & '?'));
                                break;
                            }
                            default: {
                                throw new IllegalArgumentException();
                            }
                        }
                    }
                }
                catch (final NumberFormatException e) {
                    throw new IllegalArgumentException();
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
