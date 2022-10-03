package org.openjsse.sun.net.util;

import java.net.URL;
import java.util.Arrays;

public class IPAddressUtil
{
    private static final int INADDR4SZ = 4;
    private static final int INADDR16SZ = 16;
    private static final int INT16SZ = 2;
    private static final long L_IPV6_DELIMS = 0L;
    private static final long H_IPV6_DELIMS = 671088640L;
    private static final long L_GEN_DELIMS = -8935000888854970368L;
    private static final long H_GEN_DELIMS = 671088641L;
    private static final long L_AUTH_DELIMS = 288230376151711744L;
    private static final long H_AUTH_DELIMS = 671088641L;
    private static final long L_COLON = 288230376151711744L;
    private static final long H_COLON = 0L;
    private static final long L_SLASH = 140737488355328L;
    private static final long H_SLASH = 0L;
    private static final long L_BACKSLASH = 0L;
    private static final long H_BACKSLASH = 268435456L;
    private static final long L_NON_PRINTABLE = 4294967295L;
    private static final long H_NON_PRINTABLE = Long.MIN_VALUE;
    private static final long L_EXCLUDE = -8935000884560003073L;
    private static final long H_EXCLUDE = -9223372035915251711L;
    private static final char[] OTHERS;
    
    public static byte[] textToNumericFormatV4(final String src) {
        final byte[] res = new byte[4];
        long tmpValue = 0L;
        int currByte = 0;
        boolean newOctet = true;
        final int len = src.length();
        if (len == 0 || len > 15) {
            return null;
        }
        for (int i = 0; i < len; ++i) {
            final char c = src.charAt(i);
            if (c == '.') {
                if (newOctet || tmpValue < 0L || tmpValue > 255L || currByte == 3) {
                    return null;
                }
                res[currByte++] = (byte)(tmpValue & 0xFFL);
                tmpValue = 0L;
                newOctet = true;
            }
            else {
                final int digit = Character.digit(c, 10);
                if (digit < 0) {
                    return null;
                }
                tmpValue *= 10L;
                tmpValue += digit;
                newOctet = false;
            }
        }
        if (newOctet || tmpValue < 0L || tmpValue >= 1L << (4 - currByte) * 8) {
            return null;
        }
        switch (currByte) {
            case 0: {
                res[0] = (byte)(tmpValue >> 24 & 0xFFL);
            }
            case 1: {
                res[1] = (byte)(tmpValue >> 16 & 0xFFL);
            }
            case 2: {
                res[2] = (byte)(tmpValue >> 8 & 0xFFL);
            }
            case 3: {
                res[3] = (byte)(tmpValue >> 0 & 0xFFL);
                break;
            }
        }
        return res;
    }
    
    public static byte[] textToNumericFormatV6(final String src) {
        if (src.length() < 2) {
            return null;
        }
        final char[] srcb = src.toCharArray();
        final byte[] dst = new byte[16];
        int srcb_length = srcb.length;
        final int pc = src.indexOf(37);
        if (pc == srcb_length - 1) {
            return null;
        }
        if (pc != -1) {
            srcb_length = pc;
        }
        int colonp = -1;
        int i = 0;
        int j = 0;
        if (srcb[i] == ':' && srcb[++i] != ':') {
            return null;
        }
        int curtok = i;
        boolean saw_xdigit = false;
        int val = 0;
        while (i < srcb_length) {
            final char ch = srcb[i++];
            final int chval = Character.digit(ch, 16);
            if (chval != -1) {
                val <<= 4;
                val |= chval;
                if (val > 65535) {
                    return null;
                }
                saw_xdigit = true;
            }
            else if (ch == ':') {
                curtok = i;
                if (!saw_xdigit) {
                    if (colonp != -1) {
                        return null;
                    }
                    colonp = j;
                }
                else {
                    if (i == srcb_length) {
                        return null;
                    }
                    if (j + 2 > 16) {
                        return null;
                    }
                    dst[j++] = (byte)(val >> 8 & 0xFF);
                    dst[j++] = (byte)(val & 0xFF);
                    saw_xdigit = false;
                    val = 0;
                }
            }
            else {
                if (ch != '.' || j + 4 > 16) {
                    return null;
                }
                final String ia4 = src.substring(curtok, srcb_length);
                int dot_count = 0;
                for (int index = 0; (index = ia4.indexOf(46, index)) != -1; ++index) {
                    ++dot_count;
                }
                if (dot_count != 3) {
                    return null;
                }
                final byte[] v4addr = textToNumericFormatV4(ia4);
                if (v4addr == null) {
                    return null;
                }
                for (int k = 0; k < 4; ++k) {
                    dst[j++] = v4addr[k];
                }
                saw_xdigit = false;
                break;
            }
        }
        if (saw_xdigit) {
            if (j + 2 > 16) {
                return null;
            }
            dst[j++] = (byte)(val >> 8 & 0xFF);
            dst[j++] = (byte)(val & 0xFF);
        }
        if (colonp != -1) {
            final int n = j - colonp;
            if (j == 16) {
                return null;
            }
            for (i = 1; i <= n; ++i) {
                dst[16 - i] = dst[colonp + n - i];
                dst[colonp + n - i] = 0;
            }
            j = 16;
        }
        if (j != 16) {
            return null;
        }
        final byte[] newdst = convertFromIPv4MappedAddress(dst);
        if (newdst != null) {
            return newdst;
        }
        return dst;
    }
    
    public static boolean isIPv4LiteralAddress(final String src) {
        return textToNumericFormatV4(src) != null;
    }
    
    public static boolean isIPv6LiteralAddress(final String src) {
        return textToNumericFormatV6(src) != null;
    }
    
    public static byte[] convertFromIPv4MappedAddress(final byte[] addr) {
        if (isIPv4MappedAddress(addr)) {
            final byte[] newAddr = new byte[4];
            System.arraycopy(addr, 12, newAddr, 0, 4);
            return newAddr;
        }
        return null;
    }
    
    private static boolean isIPv4MappedAddress(final byte[] addr) {
        return addr.length >= 16 && (addr[0] == 0 && addr[1] == 0 && addr[2] == 0 && addr[3] == 0 && addr[4] == 0 && addr[5] == 0 && addr[6] == 0 && addr[7] == 0 && addr[8] == 0 && addr[9] == 0 && addr[10] == -1 && addr[11] == -1);
    }
    
    public static boolean match(final char c, final long lowMask, final long highMask) {
        if (c < '@') {
            return (1L << c & lowMask) != 0x0L;
        }
        return c < '\u0080' && (1L << c - '@' & highMask) != 0x0L;
    }
    
    public static int scan(final String s, final long lowMask, final long highMask) {
        int i = -1;
        final int len;
        if (s == null || (len = s.length()) == 0) {
            return -1;
        }
        boolean match = false;
        while (++i < len && !(match = match(s.charAt(i), lowMask, highMask))) {}
        if (match) {
            return i;
        }
        return -1;
    }
    
    public static int scan(final String s, final long lowMask, final long highMask, final char[] others) {
        int i = -1;
        final int len;
        if (s == null || (len = s.length()) == 0) {
            return -1;
        }
        boolean match = false;
        final char c0 = others[0];
        char c2;
        while (++i < len && !(match = match(c2 = s.charAt(i), lowMask, highMask))) {
            if (c2 >= c0 && Arrays.binarySearch(others, c2) > -1) {
                match = true;
                break;
            }
        }
        if (match) {
            return i;
        }
        return -1;
    }
    
    private static String describeChar(final char c) {
        if (c < ' ' || c == '\u007f') {
            if (c == '\n') {
                return "LF";
            }
            if (c == '\r') {
                return "CR";
            }
            return "control char (code=" + (int)c + ")";
        }
        else {
            if (c == '\\') {
                return "'\\'";
            }
            return "'" + c + "'";
        }
    }
    
    private static String checkUserInfo(final String str) {
        final int index = scan(str, -9223231260711714817L, -9223372035915251711L);
        if (index >= 0) {
            return "Illegal character found in user-info: " + describeChar(str.charAt(index));
        }
        return null;
    }
    
    private static String checkHost(String str) {
        if (str.startsWith("[") && str.endsWith("]")) {
            str = str.substring(1, str.length() - 1);
            if (isIPv6LiteralAddress(str)) {
                int index = str.indexOf(37);
                if (index >= 0) {
                    index = scan(str = str.substring(index), 4294967295L, -9223372036183687168L);
                    if (index >= 0) {
                        return "Illegal character found in IPv6 scoped address: " + describeChar(str.charAt(index));
                    }
                }
                return null;
            }
            return "Unrecognized IPv6 address format";
        }
        else {
            final int index = scan(str, -8935000884560003073L, -9223372035915251711L);
            if (index >= 0) {
                return "Illegal character found in host: " + describeChar(str.charAt(index));
            }
            return null;
        }
    }
    
    private static String checkAuth(final String str) {
        final int index = scan(str, -9223231260711714817L, -9223372036586340352L);
        if (index >= 0) {
            return "Illegal character found in authority: " + describeChar(str.charAt(index));
        }
        return null;
    }
    
    public static String checkAuthority(final URL url) {
        if (url == null) {
            return null;
        }
        final String u;
        String s;
        if ((s = checkUserInfo(u = url.getUserInfo())) != null) {
            return s;
        }
        final String h;
        if ((s = checkHost(h = url.getHost())) != null) {
            return s;
        }
        if (h == null && u == null) {
            return checkAuth(url.getAuthority());
        }
        return null;
    }
    
    public static String checkExternalForm(final URL url) {
        if (url == null) {
            return null;
        }
        String s;
        final int index = scan(s = url.getUserInfo(), 140741783322623L, Long.MIN_VALUE);
        if (index >= 0) {
            return "Illegal character found in authority: " + describeChar(s.charAt(index));
        }
        if ((s = checkHostString(url.getHost())) != null) {
            return s;
        }
        return null;
    }
    
    public static String checkHostString(final String host) {
        if (host == null) {
            return null;
        }
        final int index = scan(host, 140741783322623L, Long.MIN_VALUE, IPAddressUtil.OTHERS);
        if (index >= 0) {
            return "Illegal character found in host: " + describeChar(host.charAt(index));
        }
        return null;
    }
    
    static {
        OTHERS = new char[] { '\u2047', '\u2048', '\u2049', '\u2100', '\u2101', '\u2105', '\u2106', '\u2a74', '\ufe55', '\ufe56', '\ufe5f', '\ufe6b', '\uff03', '\uff0f', '\uff1a', '\uff1f', '\uff20' };
    }
}
