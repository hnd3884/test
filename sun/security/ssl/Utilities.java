package sun.security.ssl;

import java.math.BigInteger;
import sun.security.action.GetPropertyAction;
import sun.net.util.IPAddressUtil;
import javax.net.ssl.SNIHostName;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import javax.net.ssl.SNIServerName;
import java.util.List;
import java.util.regex.Pattern;

final class Utilities
{
    static final char[] hexDigits;
    private static final String indent = "  ";
    private static final Pattern lineBreakPatern;
    
    static List<SNIServerName> addToSNIServerNameList(final List<SNIServerName> list, final String s) {
        final SNIHostName rawToSNIHostName = rawToSNIHostName(s);
        if (rawToSNIHostName == null) {
            return list;
        }
        final int size = list.size();
        final ArrayList list2 = (size != 0) ? new ArrayList(list) : new ArrayList(1);
        boolean b = false;
        for (int i = 0; i < size; ++i) {
            final SNIServerName sniServerName = (SNIServerName)list2.get(i);
            if (sniServerName.getType() == 0) {
                list2.set(i, (Object)rawToSNIHostName);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("the previous server name in SNI (" + sniServerName + ") was replaced with (" + rawToSNIHostName + ")", new Object[0]);
                }
                b = true;
                break;
            }
        }
        if (!b) {
            list2.add((Object)rawToSNIHostName);
        }
        return Collections.unmodifiableList((List<? extends SNIServerName>)list2);
    }
    
    private static SNIHostName rawToSNIHostName(final String s) {
        SNIHostName sniHostName = null;
        if (s != null && s.indexOf(46) > 0 && !s.endsWith(".") && !IPAddressUtil.isIPv4LiteralAddress(s) && !IPAddressUtil.isIPv6LiteralAddress(s)) {
            try {
                sniHostName = new SNIHostName(s);
            }
            catch (final IllegalArgumentException ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine(s + "\" is not a legal HostName for  server name indication", new Object[0]);
                }
            }
        }
        return sniHostName;
    }
    
    static boolean getBooleanProperty(final String s, final boolean b) {
        final String privilegedGetProperty = GetPropertyAction.privilegedGetProperty(s);
        if (privilegedGetProperty == null) {
            return b;
        }
        if (privilegedGetProperty.equalsIgnoreCase("false")) {
            return false;
        }
        if (privilegedGetProperty.equalsIgnoreCase("true")) {
            return true;
        }
        throw new RuntimeException("Value of " + s + " must either be 'true' or 'false'");
    }
    
    static String indent(final String s) {
        return indent(s, "  ");
    }
    
    static String indent(final String s, final String s2) {
        final StringBuilder sb = new StringBuilder();
        if (s == null) {
            sb.append("\n" + s2 + "<blank message>");
        }
        else {
            final String[] split = Utilities.lineBreakPatern.split(s);
            int n = 1;
            for (final String s3 : split) {
                if (n != 0) {
                    n = 0;
                }
                else {
                    sb.append("\n");
                }
                sb.append(s2).append(s3);
            }
        }
        return sb.toString();
    }
    
    static String toHexString(final byte b) {
        return String.valueOf(Utilities.hexDigits[b >> 4 & 0xF]) + String.valueOf(Utilities.hexDigits[b & 0xF]);
    }
    
    static String byte16HexString(final int n) {
        return "0x" + Utilities.hexDigits[n >> 12 & 0xF] + Utilities.hexDigits[n >> 8 & 0xF] + Utilities.hexDigits[n >> 4 & 0xF] + Utilities.hexDigits[n & 0xF];
    }
    
    static String toHexString(final byte[] array) {
        if (array == null || array.length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(array.length * 3);
        int n = 1;
        for (final byte b : array) {
            if (n != 0) {
                n = 0;
            }
            else {
                sb.append(' ');
            }
            sb.append(Utilities.hexDigits[b >> 4 & 0xF]);
            sb.append(Utilities.hexDigits[b & 0xF]);
        }
        return sb.toString();
    }
    
    static String toHexString(long n) {
        final StringBuilder sb = new StringBuilder(128);
        int n2 = 1;
        do {
            if (n2 != 0) {
                n2 = 0;
            }
            else {
                sb.append(' ');
            }
            sb.append(Utilities.hexDigits[(int)(n & 0xFL)]);
            n >>>= 4;
            sb.append(Utilities.hexDigits[(int)(n & 0xFL)]);
            n >>>= 4;
        } while (n != 0L);
        sb.reverse();
        return sb.toString();
    }
    
    static byte[] toByteArray(final BigInteger bigInteger) {
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length > 1 && byteArray[0] == 0) {
            final int n = byteArray.length - 1;
            final byte[] array = new byte[n];
            System.arraycopy(byteArray, 1, array, 0, n);
            byteArray = array;
        }
        return byteArray;
    }
    
    private static void rangeCheck(final int n, final int n2, final int n3) {
        if (n2 > n3) {
            throw new IllegalArgumentException("fromIndex(" + n2 + ") > toIndex(" + n3 + ")");
        }
        if (n2 < 0) {
            throw new ArrayIndexOutOfBoundsException(n2);
        }
        if (n3 > n) {
            throw new ArrayIndexOutOfBoundsException(n3);
        }
    }
    
    static boolean equals(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final int n4) {
        rangeCheck(array.length, n, n2);
        rangeCheck(array2.length, n3, n4);
        final int n5 = n2 - n;
        if (n5 != n4 - n3) {
            return false;
        }
        for (int i = 0; i < n5; ++i) {
            if (array[i + n] != array2[i + n3]) {
                return false;
            }
        }
        return true;
    }
    
    static {
        hexDigits = "0123456789ABCDEF".toCharArray();
        lineBreakPatern = Pattern.compile("\\r\\n|\\n|\\r");
    }
}
