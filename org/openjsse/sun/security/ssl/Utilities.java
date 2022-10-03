package org.openjsse.sun.security.ssl;

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
    
    static List<SNIServerName> addToSNIServerNameList(final List<SNIServerName> serverNames, final String hostname) {
        final SNIHostName sniHostName = rawToSNIHostName(hostname);
        if (sniHostName == null) {
            return serverNames;
        }
        final int size = serverNames.size();
        final List<SNIServerName> sniList = (size != 0) ? new ArrayList<SNIServerName>(serverNames) : new ArrayList<SNIServerName>(1);
        boolean reset = false;
        for (int i = 0; i < size; ++i) {
            final SNIServerName serverName = sniList.get(i);
            if (serverName.getType() == 0) {
                sniList.set(i, sniHostName);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("the previous server name in SNI (" + serverName + ") was replaced with (" + sniHostName + ")", new Object[0]);
                }
                reset = true;
                break;
            }
        }
        if (!reset) {
            sniList.add(sniHostName);
        }
        return Collections.unmodifiableList((List<? extends SNIServerName>)sniList);
    }
    
    private static SNIHostName rawToSNIHostName(final String hostname) {
        SNIHostName sniHostName = null;
        if (hostname != null && hostname.indexOf(46) > 0 && !hostname.endsWith(".") && !IPAddressUtil.isIPv4LiteralAddress(hostname) && !IPAddressUtil.isIPv6LiteralAddress(hostname)) {
            try {
                sniHostName = new SNIHostName(hostname);
            }
            catch (final IllegalArgumentException iae) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine(hostname + "\" is not a legal HostName for  server name indication", new Object[0]);
                }
            }
        }
        return sniHostName;
    }
    
    static boolean getBooleanProperty(final String propName, final boolean defaultValue) {
        final String b = GetPropertyAction.privilegedGetProperty(propName);
        if (b == null) {
            return defaultValue;
        }
        if (b.equalsIgnoreCase("false")) {
            return false;
        }
        if (b.equalsIgnoreCase("true")) {
            return true;
        }
        throw new RuntimeException("Value of " + propName + " must either be 'true' or 'false'");
    }
    
    static int getUIntProperty(final String propName, final int defaultValue) {
        final String val = GetPropertyAction.privilegedGetProperty(propName);
        int value = defaultValue;
        if (val != null && !val.isEmpty()) {
            try {
                value = Integer.parseUnsignedInt(val);
            }
            catch (final NumberFormatException e) {
                throw new RuntimeException("Value of " + propName + " must be unsigned integer");
            }
        }
        return value;
    }
    
    static String indent(final String source) {
        return indent(source, "  ");
    }
    
    static String indent(final String source, final String prefix) {
        final StringBuilder builder = new StringBuilder();
        if (source == null) {
            builder.append("\n" + prefix + "<blank message>");
        }
        else {
            final String[] lines = Utilities.lineBreakPatern.split(source);
            boolean isFirst = true;
            for (final String line : lines) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    builder.append("\n");
                }
                builder.append(prefix).append(line);
            }
        }
        return builder.toString();
    }
    
    static String toHexString(final byte b) {
        return String.valueOf(Utilities.hexDigits[b >> 4 & 0xF]) + String.valueOf(Utilities.hexDigits[b & 0xF]);
    }
    
    static String byte16HexString(final int id) {
        return "0x" + Utilities.hexDigits[id >> 12 & 0xF] + Utilities.hexDigits[id >> 8 & 0xF] + Utilities.hexDigits[id >> 4 & 0xF] + Utilities.hexDigits[id & 0xF];
    }
    
    static String toHexString(final byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        final StringBuilder builder = new StringBuilder(bytes.length * 3);
        boolean isFirst = true;
        for (final byte b : bytes) {
            if (isFirst) {
                isFirst = false;
            }
            else {
                builder.append(' ');
            }
            builder.append(Utilities.hexDigits[b >> 4 & 0xF]);
            builder.append(Utilities.hexDigits[b & 0xF]);
        }
        return builder.toString();
    }
    
    static String toHexString(long lv) {
        final StringBuilder builder = new StringBuilder(128);
        boolean isFirst = true;
        do {
            if (isFirst) {
                isFirst = false;
            }
            else {
                builder.append(' ');
            }
            builder.append(Utilities.hexDigits[(int)(lv & 0xFL)]);
            lv >>>= 4;
            builder.append(Utilities.hexDigits[(int)(lv & 0xFL)]);
            lv >>>= 4;
        } while (lv != 0L);
        builder.reverse();
        return builder.toString();
    }
    
    static byte[] toByteArray(final BigInteger bi) {
        byte[] b = bi.toByteArray();
        if (b.length > 1 && b[0] == 0) {
            final int n = b.length - 1;
            final byte[] newarray = new byte[n];
            System.arraycopy(b, 1, newarray, 0, n);
            b = newarray;
        }
        return b;
    }
    
    static boolean equals(final byte[] arr1, final int st1, final int end1, final byte[] arr2, final int st2, final int end2) {
        for (int i = st1, j = st2; i < end1 && j < end2; ++i, ++j) {
            if (arr1[i] != arr2[j]) {
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
