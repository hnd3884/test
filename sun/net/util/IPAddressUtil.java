package sun.net.util;

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
    
    public static byte[] textToNumericFormatV4(final String s) {
        final byte[] array = new byte[4];
        long n = 0L;
        int n2 = 0;
        int n3 = 1;
        final int length = s.length();
        if (length == 0 || length > 15) {
            return null;
        }
        for (int i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '.') {
                if (n3 != 0 || n < 0L || n > 255L || n2 == 3) {
                    return null;
                }
                array[n2++] = (byte)(n & 0xFFL);
                n = 0L;
                n3 = 1;
            }
            else {
                final int digit = Character.digit(char1, 10);
                if (digit < 0) {
                    return null;
                }
                n = n * 10L + digit;
                n3 = 0;
            }
        }
        if (n3 != 0 || n < 0L || n >= 1L << (4 - n2) * 8) {
            return null;
        }
        switch (n2) {
            case 0: {
                array[0] = (byte)(n >> 24 & 0xFFL);
            }
            case 1: {
                array[1] = (byte)(n >> 16 & 0xFFL);
            }
            case 2: {
                array[2] = (byte)(n >> 8 & 0xFFL);
            }
            case 3: {
                array[3] = (byte)(n >> 0 & 0xFFL);
                break;
            }
        }
        return array;
    }
    
    public static byte[] textToNumericFormatV6(final String s) {
        if (s.length() < 2) {
            return null;
        }
        final char[] charArray = s.toCharArray();
        final byte[] array = new byte[16];
        int length = charArray.length;
        final int index = s.indexOf("%");
        if (index == length - 1) {
            return null;
        }
        if (index != -1) {
            length = index;
        }
        int n = -1;
        int i = 0;
        int n2 = 0;
        if (charArray[i] == ':' && charArray[++i] != ':') {
            return null;
        }
        int n3 = i;
        int n4 = 0;
        int n5 = 0;
        while (i < length) {
            final char c = charArray[i++];
            final int digit = Character.digit(c, 16);
            if (digit != -1) {
                n5 = (n5 << 4 | digit);
                if (n5 > 65535) {
                    return null;
                }
                n4 = 1;
            }
            else if (c == ':') {
                n3 = i;
                if (n4 == 0) {
                    if (n != -1) {
                        return null;
                    }
                    n = n2;
                }
                else {
                    if (i == length) {
                        return null;
                    }
                    if (n2 + 2 > 16) {
                        return null;
                    }
                    array[n2++] = (byte)(n5 >> 8 & 0xFF);
                    array[n2++] = (byte)(n5 & 0xFF);
                    n4 = 0;
                    n5 = 0;
                }
            }
            else {
                if (c != '.' || n2 + 4 > 16) {
                    return null;
                }
                final String substring = s.substring(n3, length);
                int n6 = 0;
                for (int index2 = 0; (index2 = substring.indexOf(46, index2)) != -1; ++index2) {
                    ++n6;
                }
                if (n6 != 3) {
                    return null;
                }
                final byte[] textToNumericFormatV4 = textToNumericFormatV4(substring);
                if (textToNumericFormatV4 == null) {
                    return null;
                }
                for (int j = 0; j < 4; ++j) {
                    array[n2++] = textToNumericFormatV4[j];
                }
                n4 = 0;
                break;
            }
        }
        if (n4 != 0) {
            if (n2 + 2 > 16) {
                return null;
            }
            array[n2++] = (byte)(n5 >> 8 & 0xFF);
            array[n2++] = (byte)(n5 & 0xFF);
        }
        if (n != -1) {
            final int n7 = n2 - n;
            if (n2 == 16) {
                return null;
            }
            for (int k = 1; k <= n7; ++k) {
                array[16 - k] = array[n + n7 - k];
                array[n + n7 - k] = 0;
            }
            n2 = 16;
        }
        if (n2 != 16) {
            return null;
        }
        final byte[] convertFromIPv4MappedAddress = convertFromIPv4MappedAddress(array);
        if (convertFromIPv4MappedAddress != null) {
            return convertFromIPv4MappedAddress;
        }
        return array;
    }
    
    public static boolean isIPv4LiteralAddress(final String s) {
        return textToNumericFormatV4(s) != null;
    }
    
    public static boolean isIPv6LiteralAddress(final String s) {
        return textToNumericFormatV6(s) != null;
    }
    
    public static byte[] convertFromIPv4MappedAddress(final byte[] array) {
        if (isIPv4MappedAddress(array)) {
            final byte[] array2 = new byte[4];
            System.arraycopy(array, 12, array2, 0, 4);
            return array2;
        }
        return null;
    }
    
    private static boolean isIPv4MappedAddress(final byte[] array) {
        return array.length >= 16 && (array[0] == 0 && array[1] == 0 && array[2] == 0 && array[3] == 0 && array[4] == 0 && array[5] == 0 && array[6] == 0 && array[7] == 0 && array[8] == 0 && array[9] == 0 && array[10] == -1 && array[11] == -1);
    }
    
    public static boolean match(final char c, final long n, final long n2) {
        if (c < '@') {
            return (1L << c & n) != 0x0L;
        }
        return c < '\u0080' && (1L << c - '@' & n2) != 0x0L;
    }
    
    public static int scan(final String s, final long n, final long n2) {
        int n3 = -1;
        final int length;
        if (s == null || (length = s.length()) == 0) {
            return -1;
        }
        boolean match = false;
        while (++n3 < length && !(match = match(s.charAt(n3), n, n2))) {}
        if (match) {
            return n3;
        }
        return -1;
    }
    
    public static int scan(final String s, final long n, final long n2, final char[] array) {
        int n3 = -1;
        final int length;
        if (s == null || (length = s.length()) == 0) {
            return -1;
        }
        boolean match = false;
        final char c = array[0];
        char char1;
        while (++n3 < length && !(match = match(char1 = s.charAt(n3), n, n2))) {
            if (char1 >= c && Arrays.binarySearch(array, char1) > -1) {
                match = true;
                break;
            }
        }
        if (match) {
            return n3;
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
    
    private static String checkUserInfo(final String s) {
        final int scan = scan(s, -9223231260711714817L, -9223372035915251711L);
        if (scan >= 0) {
            return "Illegal character found in user-info: " + describeChar(s.charAt(scan));
        }
        return null;
    }
    
    private static String checkHost(String s) {
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length() - 1);
            if (isIPv6LiteralAddress(s)) {
                final int index = s.indexOf(37);
                if (index >= 0) {
                    final int scan = scan(s = s.substring(index), 4294967295L, -9223372036183687168L);
                    if (scan >= 0) {
                        return "Illegal character found in IPv6 scoped address: " + describeChar(s.charAt(scan));
                    }
                }
                return null;
            }
            return "Unrecognized IPv6 address format";
        }
        else {
            final int scan2 = scan(s, -8935000884560003073L, -9223372035915251711L);
            if (scan2 >= 0) {
                return "Illegal character found in host: " + describeChar(s.charAt(scan2));
            }
            return null;
        }
    }
    
    private static String checkAuth(final String s) {
        final int scan = scan(s, -9223231260711714817L, -9223372036586340352L);
        if (scan >= 0) {
            return "Illegal character found in authority: " + describeChar(s.charAt(scan));
        }
        return null;
    }
    
    public static String checkAuthority(final URL url) {
        if (url == null) {
            return null;
        }
        final String userInfo;
        final String checkUserInfo;
        if ((checkUserInfo = checkUserInfo(userInfo = url.getUserInfo())) != null) {
            return checkUserInfo;
        }
        final String host;
        final String checkHost;
        if ((checkHost = checkHost(host = url.getHost())) != null) {
            return checkHost;
        }
        if (host == null && userInfo == null) {
            return checkAuth(url.getAuthority());
        }
        return null;
    }
    
    public static String checkExternalForm(final URL url) {
        if (url == null) {
            return null;
        }
        final String userInfo;
        final int scan = scan(userInfo = url.getUserInfo(), 140741783322623L, Long.MIN_VALUE);
        if (scan >= 0) {
            return "Illegal character found in authority: " + describeChar(userInfo.charAt(scan));
        }
        final String checkHostString;
        if ((checkHostString = checkHostString(url.getHost())) != null) {
            return checkHostString;
        }
        return null;
    }
    
    public static String checkHostString(final String s) {
        if (s == null) {
            return null;
        }
        final int scan = scan(s, 140741783322623L, Long.MIN_VALUE, IPAddressUtil.OTHERS);
        if (scan >= 0) {
            return "Illegal character found in host: " + describeChar(s.charAt(scan));
        }
        return null;
    }
    
    static {
        OTHERS = new char[] { '\u2047', '\u2048', '\u2049', '\u2100', '\u2101', '\u2105', '\u2106', '\u2a74', '\ufe55', '\ufe56', '\ufe5f', '\ufe6b', '\uff03', '\uff0f', '\uff1a', '\uff1f', '\uff20' };
    }
}
