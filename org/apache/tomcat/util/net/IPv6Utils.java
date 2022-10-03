package org.apache.tomcat.util.net;

public class IPv6Utils
{
    private static final int MAX_NUMBER_OF_GROUPS = 8;
    private static final int MAX_GROUP_LENGTH = 4;
    
    public static String canonize(final String ipv6Address) throws IllegalArgumentException {
        if (ipv6Address == null) {
            return null;
        }
        if (!mayBeIPv6Address(ipv6Address)) {
            return ipv6Address;
        }
        int ipv6AddressLength = ipv6Address.length();
        if (ipv6Address.contains(".")) {
            final int lastColonPos = ipv6Address.lastIndexOf(58);
            final int lastColonsPos = ipv6Address.lastIndexOf("::");
            if (lastColonsPos >= 0 && lastColonPos == lastColonsPos + 1) {
                ipv6AddressLength = lastColonPos + 1;
            }
            else {
                ipv6AddressLength = lastColonPos;
            }
        }
        else if (ipv6Address.contains("%")) {
            ipv6AddressLength = ipv6Address.lastIndexOf(37);
        }
        final StringBuilder result = new StringBuilder();
        final char[][] groups = new char[8][4];
        int groupCounter = 0;
        int charInGroupCounter = 0;
        int zeroGroupIndex = -1;
        int zeroGroupLength = 0;
        int maxZeroGroupIndex = -1;
        int maxZeroGroupLength = 0;
        boolean isZero = true;
        boolean groupStart = true;
        final StringBuilder expanded = new StringBuilder(ipv6Address);
        final int colonsPos = ipv6Address.indexOf("::");
        int length = ipv6AddressLength;
        int change = 0;
        if (colonsPos >= 0 && colonsPos < ipv6AddressLength - 1) {
            int colonCounter = 0;
            for (int i = 0; i < ipv6AddressLength; ++i) {
                if (ipv6Address.charAt(i) == ':') {
                    ++colonCounter;
                }
            }
            if (colonsPos == 0) {
                expanded.insert(0, "0");
                ++change;
            }
            for (int i = 0; i < 8 - colonCounter; ++i) {
                expanded.insert(colonsPos + 1, "0:");
                change += 2;
            }
            if (colonsPos == ipv6AddressLength - 2) {
                expanded.setCharAt(colonsPos + change + 1, '0');
            }
            else {
                expanded.deleteCharAt(colonsPos + change + 1);
                --change;
            }
            length += change;
        }
        for (int charCounter = 0; charCounter < length; ++charCounter) {
            char c = expanded.charAt(charCounter);
            if (c >= 'A' && c <= 'F') {
                c += ' ';
            }
            if (c != ':') {
                groups[groupCounter][charInGroupCounter] = c;
                if (!groupStart || c != '0') {
                    ++charInGroupCounter;
                    groupStart = false;
                }
                if (c != '0') {
                    isZero = false;
                }
            }
            if (c == ':' || charCounter == length - 1) {
                if (isZero) {
                    ++zeroGroupLength;
                    if (zeroGroupIndex == -1) {
                        zeroGroupIndex = groupCounter;
                    }
                }
                if (!isZero || charCounter == length - 1) {
                    if (zeroGroupLength > maxZeroGroupLength) {
                        maxZeroGroupLength = zeroGroupLength;
                        maxZeroGroupIndex = zeroGroupIndex;
                    }
                    zeroGroupLength = 0;
                    zeroGroupIndex = -1;
                }
                ++groupCounter;
                charInGroupCounter = 0;
                isZero = true;
                groupStart = true;
            }
        }
        int numberOfGroups;
        for (numberOfGroups = groupCounter, groupCounter = 0; groupCounter < numberOfGroups; ++groupCounter) {
            if (maxZeroGroupLength <= 1 || groupCounter < maxZeroGroupIndex || groupCounter >= maxZeroGroupIndex + maxZeroGroupLength) {
                for (int j = 0; j < 4; ++j) {
                    if (groups[groupCounter][j] != '\0') {
                        result.append(groups[groupCounter][j]);
                    }
                }
                if (groupCounter < numberOfGroups - 1 && (groupCounter != maxZeroGroupIndex - 1 || maxZeroGroupLength <= 1)) {
                    result.append(':');
                }
            }
            else if (groupCounter == maxZeroGroupIndex) {
                result.append("::");
            }
        }
        final int resultLength = result.length();
        if (result.charAt(resultLength - 1) == ':' && ipv6AddressLength < ipv6Address.length() && ipv6Address.charAt(ipv6AddressLength) == ':') {
            result.delete(resultLength - 1, resultLength);
        }
        for (int k = ipv6AddressLength; k < ipv6Address.length(); ++k) {
            result.append(ipv6Address.charAt(k));
        }
        return result.toString();
    }
    
    static boolean mayBeIPv6Address(final String input) {
        if (input == null) {
            return false;
        }
        int colonsCounter = 0;
        for (int length = input.length(), i = 0; i < length; ++i) {
            final char c = input.charAt(i);
            if (c == '.') {
                break;
            }
            if (c == '%') {
                break;
            }
            if ((c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F') && c != ':') {
                return false;
            }
            if (c == ':') {
                ++colonsCounter;
            }
        }
        return colonsCounter >= 2;
    }
}
