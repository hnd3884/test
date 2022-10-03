package org.bouncycastle.util;

public class IPAddress
{
    public static boolean isValid(final String s) {
        return isValidIPv4(s) || isValidIPv6(s);
    }
    
    public static boolean isValidWithNetMask(final String s) {
        return isValidIPv4WithNetmask(s) || isValidIPv6WithNetmask(s);
    }
    
    public static boolean isValidIPv4(final String s) {
        if (s.length() == 0) {
            return false;
        }
        int n = 0;
        final String string = s + ".";
        int index;
        for (int n2 = 0; n2 < string.length() && (index = string.indexOf(46, n2)) > n2; n2 = index + 1, ++n) {
            if (n == 4) {
                return false;
            }
            int int1;
            try {
                int1 = Integer.parseInt(string.substring(n2, index));
            }
            catch (final NumberFormatException ex) {
                return false;
            }
            if (int1 < 0 || int1 > 255) {
                return false;
            }
        }
        return n == 4;
    }
    
    public static boolean isValidIPv4WithNetmask(final String s) {
        final int index = s.indexOf("/");
        final String substring = s.substring(index + 1);
        return index > 0 && isValidIPv4(s.substring(0, index)) && (isValidIPv4(substring) || isMaskValue(substring, 32));
    }
    
    public static boolean isValidIPv6WithNetmask(final String s) {
        final int index = s.indexOf("/");
        final String substring = s.substring(index + 1);
        return index > 0 && isValidIPv6(s.substring(0, index)) && (isValidIPv6(substring) || isMaskValue(substring, 128));
    }
    
    private static boolean isMaskValue(final String s, final int n) {
        try {
            final int int1 = Integer.parseInt(s);
            return int1 >= 0 && int1 <= n;
        }
        catch (final NumberFormatException ex) {
            return false;
        }
    }
    
    public static boolean isValidIPv6(final String s) {
        if (s.length() == 0) {
            return false;
        }
        int n = 0;
        final String string = s + ":";
        int n2 = 0;
        int index;
        for (int n3 = 0; n3 < string.length() && (index = string.indexOf(58, n3)) >= n3; n3 = index + 1, ++n) {
            if (n == 8) {
                return false;
            }
            if (n3 != index) {
                final String substring = string.substring(n3, index);
                if (index == string.length() - 1 && substring.indexOf(46) > 0) {
                    if (!isValidIPv4(substring)) {
                        return false;
                    }
                    ++n;
                }
                else {
                    int int1;
                    try {
                        int1 = Integer.parseInt(string.substring(n3, index), 16);
                    }
                    catch (final NumberFormatException ex) {
                        return false;
                    }
                    if (int1 < 0 || int1 > 65535) {
                        return false;
                    }
                }
            }
            else {
                if (index != 1 && index != string.length() - 1 && n2 != 0) {
                    return false;
                }
                n2 = 1;
            }
        }
        return n == 8 || n2 != 0;
    }
}
