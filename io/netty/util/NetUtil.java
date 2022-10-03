package io.netty.util;

import java.security.AccessController;
import java.io.FileReader;
import java.io.File;
import java.security.PrivilegedAction;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.SystemPropertyUtil;
import java.net.InetSocketAddress;
import io.netty.util.internal.StringUtil;
import java.net.UnknownHostException;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import io.netty.util.internal.logging.InternalLogger;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.net.Inet6Address;
import java.net.Inet4Address;

public final class NetUtil
{
    public static final Inet4Address LOCALHOST4;
    public static final Inet6Address LOCALHOST6;
    public static final InetAddress LOCALHOST;
    public static final NetworkInterface LOOPBACK_IF;
    public static final int SOMAXCONN;
    private static final int IPV6_WORD_COUNT = 8;
    private static final int IPV6_MAX_CHAR_COUNT = 39;
    private static final int IPV6_BYTE_COUNT = 16;
    private static final int IPV6_MAX_CHAR_BETWEEN_SEPARATOR = 4;
    private static final int IPV6_MIN_SEPARATORS = 2;
    private static final int IPV6_MAX_SEPARATORS = 8;
    private static final int IPV4_MAX_CHAR_BETWEEN_SEPARATOR = 3;
    private static final int IPV4_SEPARATORS = 3;
    private static final boolean IPV4_PREFERRED;
    private static final boolean IPV6_ADDRESSES_PREFERRED;
    private static final InternalLogger logger;
    
    private static Integer sysctlGetInt(final String sysctlKey) throws IOException {
        final Process process = new ProcessBuilder(new String[] { "sysctl", sysctlKey }).start();
        try {
            final InputStream is = process.getInputStream();
            final InputStreamReader isr = new InputStreamReader(is);
            final BufferedReader br = new BufferedReader(isr);
            try {
                final String line = br.readLine();
                if (line != null && line.startsWith(sysctlKey)) {
                    for (int i = line.length() - 1; i > sysctlKey.length(); --i) {
                        if (!Character.isDigit(line.charAt(i))) {
                            return Integer.valueOf(line.substring(i + 1));
                        }
                    }
                }
                return null;
            }
            finally {
                br.close();
            }
        }
        finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
    
    public static boolean isIpV4StackPreferred() {
        return NetUtil.IPV4_PREFERRED;
    }
    
    public static boolean isIpV6AddressesPreferred() {
        return NetUtil.IPV6_ADDRESSES_PREFERRED;
    }
    
    public static byte[] createByteArrayFromIpAddressString(String ipAddressString) {
        if (isValidIpV4Address(ipAddressString)) {
            return validIpV4ToBytes(ipAddressString);
        }
        if (isValidIpV6Address(ipAddressString)) {
            if (ipAddressString.charAt(0) == '[') {
                ipAddressString = ipAddressString.substring(1, ipAddressString.length() - 1);
            }
            final int percentPos = ipAddressString.indexOf(37);
            if (percentPos >= 0) {
                ipAddressString = ipAddressString.substring(0, percentPos);
            }
            return getIPv6ByName(ipAddressString, true);
        }
        return null;
    }
    
    private static int decimalDigit(final String str, final int pos) {
        return str.charAt(pos) - '0';
    }
    
    private static byte ipv4WordToByte(final String ip, int from, final int toExclusive) {
        int ret = decimalDigit(ip, from);
        if (++from == toExclusive) {
            return (byte)ret;
        }
        ret = ret * 10 + decimalDigit(ip, from);
        if (++from == toExclusive) {
            return (byte)ret;
        }
        return (byte)(ret * 10 + decimalDigit(ip, from));
    }
    
    static byte[] validIpV4ToBytes(final String ip) {
        int i;
        return new byte[] { ipv4WordToByte(ip, 0, i = ip.indexOf(46, 1)), ipv4WordToByte(ip, i + 1, i = ip.indexOf(46, i + 2)), ipv4WordToByte(ip, i + 1, i = ip.indexOf(46, i + 2)), ipv4WordToByte(ip, i + 1, ip.length()) };
    }
    
    public static int ipv4AddressToInt(final Inet4Address ipAddress) {
        final byte[] octets = ipAddress.getAddress();
        return (octets[0] & 0xFF) << 24 | (octets[1] & 0xFF) << 16 | (octets[2] & 0xFF) << 8 | (octets[3] & 0xFF);
    }
    
    public static String intToIpAddress(final int i) {
        final StringBuilder buf = new StringBuilder(15);
        buf.append(i >> 24 & 0xFF);
        buf.append('.');
        buf.append(i >> 16 & 0xFF);
        buf.append('.');
        buf.append(i >> 8 & 0xFF);
        buf.append('.');
        buf.append(i & 0xFF);
        return buf.toString();
    }
    
    public static String bytesToIpAddress(final byte[] bytes) {
        return bytesToIpAddress(bytes, 0, bytes.length);
    }
    
    public static String bytesToIpAddress(final byte[] bytes, final int offset, final int length) {
        switch (length) {
            case 4: {
                return new StringBuilder(15).append(bytes[offset] & 0xFF).append('.').append(bytes[offset + 1] & 0xFF).append('.').append(bytes[offset + 2] & 0xFF).append('.').append(bytes[offset + 3] & 0xFF).toString();
            }
            case 16: {
                return toAddressString(bytes, offset, false);
            }
            default: {
                throw new IllegalArgumentException("length: " + length + " (expected: 4 or 16)");
            }
        }
    }
    
    public static boolean isValidIpV6Address(final String ip) {
        return isValidIpV6Address((CharSequence)ip);
    }
    
    public static boolean isValidIpV6Address(final CharSequence ip) {
        int end = ip.length();
        if (end < 2) {
            return false;
        }
        char c = ip.charAt(0);
        int start;
        if (c == '[') {
            --end;
            if (ip.charAt(end) != ']') {
                return false;
            }
            start = 1;
            c = ip.charAt(1);
        }
        else {
            start = 0;
        }
        int colons;
        int compressBegin;
        if (c == ':') {
            if (ip.charAt(start + 1) != ':') {
                return false;
            }
            colons = 2;
            compressBegin = start;
            start += 2;
        }
        else {
            colons = 0;
            compressBegin = -1;
        }
        int wordLen = 0;
    Label_0421:
        for (int i = start; i < end; ++i) {
            c = ip.charAt(i);
            if (isValidHexChar(c)) {
                if (wordLen >= 4) {
                    return false;
                }
                ++wordLen;
            }
            else {
                switch (c) {
                    case ':': {
                        if (colons > 7) {
                            return false;
                        }
                        if (ip.charAt(i - 1) == ':') {
                            if (compressBegin >= 0) {
                                return false;
                            }
                            compressBegin = i - 1;
                        }
                        else {
                            wordLen = 0;
                        }
                        ++colons;
                        break;
                    }
                    case '.': {
                        if ((compressBegin < 0 && colons != 6) || (colons == 7 && compressBegin >= start) || colons > 7) {
                            return false;
                        }
                        final int ipv4Start = i - wordLen;
                        int j = ipv4Start - 2;
                        if (isValidIPv4MappedChar(ip.charAt(j))) {
                            if (!isValidIPv4MappedChar(ip.charAt(j - 1)) || !isValidIPv4MappedChar(ip.charAt(j - 2)) || !isValidIPv4MappedChar(ip.charAt(j - 3))) {
                                return false;
                            }
                            j -= 5;
                        }
                        while (j >= start) {
                            final char tmpChar = ip.charAt(j);
                            if (tmpChar != '0' && tmpChar != ':') {
                                return false;
                            }
                            --j;
                        }
                        int ipv4End = AsciiString.indexOf(ip, '%', ipv4Start + 7);
                        if (ipv4End < 0) {
                            ipv4End = end;
                        }
                        return isValidIpV4Address(ip, ipv4Start, ipv4End);
                    }
                    case '%': {
                        end = i;
                        break Label_0421;
                    }
                    default: {
                        return false;
                    }
                }
            }
        }
        if (compressBegin < 0) {
            return colons == 7 && wordLen > 0;
        }
        return compressBegin + 2 == end || (wordLen > 0 && (colons < 8 || compressBegin <= start));
    }
    
    private static boolean isValidIpV4Word(final CharSequence word, final int from, final int toExclusive) {
        final int len = toExclusive - from;
        final char c0;
        if (len < 1 || len > 3 || (c0 = word.charAt(from)) < '0') {
            return false;
        }
        if (len == 3) {
            final char c2;
            final char c3;
            return (c2 = word.charAt(from + 1)) >= '0' && (c3 = word.charAt(from + 2)) >= '0' && ((c0 <= '1' && c2 <= '9' && c3 <= '9') || (c0 == '2' && c2 <= '5' && (c3 <= '5' || (c2 < '5' && c3 <= '9'))));
        }
        return c0 <= '9' && (len == 1 || isValidNumericChar(word.charAt(from + 1)));
    }
    
    private static boolean isValidHexChar(final char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
    }
    
    private static boolean isValidNumericChar(final char c) {
        return c >= '0' && c <= '9';
    }
    
    private static boolean isValidIPv4MappedChar(final char c) {
        return c == 'f' || c == 'F';
    }
    
    private static boolean isValidIPv4MappedSeparators(final byte b0, final byte b1, final boolean mustBeZero) {
        return b0 == b1 && (b0 == 0 || (!mustBeZero && b1 == -1));
    }
    
    private static boolean isValidIPv4Mapped(final byte[] bytes, final int currentIndex, final int compressBegin, final int compressLength) {
        final boolean mustBeZero = compressBegin + compressLength >= 14;
        return currentIndex <= 12 && currentIndex >= 2 && (!mustBeZero || compressBegin < 12) && isValidIPv4MappedSeparators(bytes[currentIndex - 1], bytes[currentIndex - 2], mustBeZero) && PlatformDependent.isZero(bytes, 0, currentIndex - 3);
    }
    
    public static boolean isValidIpV4Address(final CharSequence ip) {
        return isValidIpV4Address(ip, 0, ip.length());
    }
    
    public static boolean isValidIpV4Address(final String ip) {
        return isValidIpV4Address(ip, 0, ip.length());
    }
    
    private static boolean isValidIpV4Address(final CharSequence ip, final int from, final int toExcluded) {
        return (ip instanceof String) ? isValidIpV4Address((String)ip, from, toExcluded) : ((ip instanceof AsciiString) ? isValidIpV4Address((AsciiString)ip, from, toExcluded) : isValidIpV4Address0(ip, from, toExcluded));
    }
    
    private static boolean isValidIpV4Address(final String ip, int from, final int toExcluded) {
        final int len = toExcluded - from;
        int i;
        return len <= 15 && len >= 7 && (i = ip.indexOf(46, from + 1)) > 0 && isValidIpV4Word(ip, from, i) && (i = ip.indexOf(46, from = i + 2)) > 0 && isValidIpV4Word(ip, from - 1, i) && (i = ip.indexOf(46, from = i + 2)) > 0 && isValidIpV4Word(ip, from - 1, i) && isValidIpV4Word(ip, i + 1, toExcluded);
    }
    
    private static boolean isValidIpV4Address(final AsciiString ip, int from, final int toExcluded) {
        final int len = toExcluded - from;
        int i;
        return len <= 15 && len >= 7 && (i = ip.indexOf('.', from + 1)) > 0 && isValidIpV4Word(ip, from, i) && (i = ip.indexOf('.', from = i + 2)) > 0 && isValidIpV4Word(ip, from - 1, i) && (i = ip.indexOf('.', from = i + 2)) > 0 && isValidIpV4Word(ip, from - 1, i) && isValidIpV4Word(ip, i + 1, toExcluded);
    }
    
    private static boolean isValidIpV4Address0(final CharSequence ip, int from, final int toExcluded) {
        final int len = toExcluded - from;
        int i;
        return len <= 15 && len >= 7 && (i = AsciiString.indexOf(ip, '.', from + 1)) > 0 && isValidIpV4Word(ip, from, i) && (i = AsciiString.indexOf(ip, '.', from = i + 2)) > 0 && isValidIpV4Word(ip, from - 1, i) && (i = AsciiString.indexOf(ip, '.', from = i + 2)) > 0 && isValidIpV4Word(ip, from - 1, i) && isValidIpV4Word(ip, i + 1, toExcluded);
    }
    
    public static Inet6Address getByName(final CharSequence ip) {
        return getByName(ip, true);
    }
    
    public static Inet6Address getByName(final CharSequence ip, final boolean ipv4Mapped) {
        final byte[] bytes = getIPv6ByName(ip, ipv4Mapped);
        if (bytes == null) {
            return null;
        }
        try {
            return Inet6Address.getByAddress(null, bytes, -1);
        }
        catch (final UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static byte[] getIPv6ByName(final CharSequence ip, final boolean ipv4Mapped) {
        final byte[] bytes = new byte[16];
        final int ipLength = ip.length();
        int compressBegin = 0;
        int compressLength = 0;
        int currentIndex = 0;
        int value = 0;
        int begin = -1;
        int i = 0;
        int ipv6Separators = 0;
        int ipv4Separators = 0;
        boolean needsShift = false;
        while (i < ipLength) {
            final char c = ip.charAt(i);
            switch (c) {
                case ':': {
                    ++ipv6Separators;
                    if (i - begin > 4 || ipv4Separators > 0 || ipv6Separators > 8 || currentIndex + 1 >= bytes.length) {
                        return null;
                    }
                    value <<= 4 - (i - begin) << 2;
                    if (compressLength > 0) {
                        compressLength -= 2;
                    }
                    bytes[currentIndex++] = (byte)((value & 0xF) << 4 | (value >> 4 & 0xF));
                    bytes[currentIndex++] = (byte)((value >> 8 & 0xF) << 4 | (value >> 12 & 0xF));
                    int tmp = i + 1;
                    if (tmp < ipLength && ip.charAt(tmp) == ':') {
                        ++tmp;
                        if (compressBegin != 0 || (tmp < ipLength && ip.charAt(tmp) == ':')) {
                            return null;
                        }
                        needsShift = (++ipv6Separators == 2 && value == 0);
                        compressBegin = currentIndex;
                        compressLength = bytes.length - compressBegin - 2;
                        ++i;
                    }
                    value = 0;
                    begin = -1;
                    break;
                }
                case '.': {
                    ++ipv4Separators;
                    final int tmp = i - begin;
                    if (tmp > 3 || begin < 0 || ipv4Separators > 3 || (ipv6Separators > 0 && currentIndex + compressLength < 12) || i + 1 >= ipLength || currentIndex >= bytes.length || (ipv4Separators == 1 && (!ipv4Mapped || (currentIndex != 0 && !isValidIPv4Mapped(bytes, currentIndex, compressBegin, compressLength)) || (tmp == 3 && (!isValidNumericChar(ip.charAt(i - 1)) || !isValidNumericChar(ip.charAt(i - 2)) || !isValidNumericChar(ip.charAt(i - 3)))) || (tmp == 2 && (!isValidNumericChar(ip.charAt(i - 1)) || !isValidNumericChar(ip.charAt(i - 2)))) || (tmp == 1 && !isValidNumericChar(ip.charAt(i - 1)))))) {
                        return null;
                    }
                    value <<= 3 - tmp << 2;
                    begin = (value & 0xF) * 100 + (value >> 4 & 0xF) * 10 + (value >> 8 & 0xF);
                    if (begin < 0 || begin > 255) {
                        return null;
                    }
                    bytes[currentIndex++] = (byte)begin;
                    value = 0;
                    begin = -1;
                    break;
                }
                default: {
                    if (!isValidHexChar(c) || (ipv4Separators > 0 && !isValidNumericChar(c))) {
                        return null;
                    }
                    if (begin < 0) {
                        begin = i;
                    }
                    else if (i - begin > 4) {
                        return null;
                    }
                    value += StringUtil.decodeHexNibble(c) << (i - begin << 2);
                    break;
                }
            }
            ++i;
        }
        final boolean isCompressed = compressBegin > 0;
        if (ipv4Separators > 0) {
            if ((begin > 0 && i - begin > 3) || ipv4Separators != 3 || currentIndex >= bytes.length) {
                return null;
            }
            if (ipv6Separators == 0) {
                compressLength = 12;
            }
            else {
                if (ipv6Separators < 2 || ((isCompressed || ipv6Separators != 6 || ip.charAt(0) == ':') && (!isCompressed || ipv6Separators >= 8 || (ip.charAt(0) == ':' && compressBegin > 2)))) {
                    return null;
                }
                compressLength -= 2;
            }
            value <<= 3 - (i - begin) << 2;
            begin = (value & 0xF) * 100 + (value >> 4 & 0xF) * 10 + (value >> 8 & 0xF);
            if (begin < 0 || begin > 255) {
                return null;
            }
            bytes[currentIndex++] = (byte)begin;
        }
        else {
            final int tmp = ipLength - 1;
            if ((begin > 0 && i - begin > 4) || ipv6Separators < 2 || (!isCompressed && (ipv6Separators + 1 != 8 || ip.charAt(0) == ':' || ip.charAt(tmp) == ':')) || (isCompressed && (ipv6Separators > 8 || (ipv6Separators == 8 && ((compressBegin <= 2 && ip.charAt(0) != ':') || (compressBegin >= 14 && ip.charAt(tmp) != ':'))))) || currentIndex + 1 >= bytes.length || (begin < 0 && ip.charAt(tmp - 1) != ':') || (compressBegin > 2 && ip.charAt(0) == ':')) {
                return null;
            }
            if (begin >= 0 && i - begin <= 4) {
                value <<= 4 - (i - begin) << 2;
            }
            bytes[currentIndex++] = (byte)((value & 0xF) << 4 | (value >> 4 & 0xF));
            bytes[currentIndex++] = (byte)((value >> 8 & 0xF) << 4 | (value >> 12 & 0xF));
        }
        i = currentIndex + compressLength;
        if (needsShift || i >= bytes.length) {
            if (i >= bytes.length) {
                ++compressBegin;
            }
            for (i = currentIndex; i < bytes.length; ++i) {
                for (begin = bytes.length - 1; begin >= compressBegin; --begin) {
                    bytes[begin] = bytes[begin - 1];
                }
                bytes[begin] = 0;
                ++compressBegin;
            }
        }
        else {
            for (i = 0; i < compressLength; ++i) {
                begin = i + compressBegin;
                currentIndex = begin + compressLength;
                if (currentIndex >= bytes.length) {
                    break;
                }
                bytes[currentIndex] = bytes[begin];
                bytes[begin] = 0;
            }
        }
        if (ipv4Separators > 0) {
            bytes[10] = (bytes[11] = -1);
        }
        return bytes;
    }
    
    public static String toSocketAddressString(final InetSocketAddress addr) {
        final String port = String.valueOf(addr.getPort());
        StringBuilder sb;
        if (addr.isUnresolved()) {
            final String hostname = getHostname(addr);
            sb = newSocketAddressStringBuilder(hostname, port, !isValidIpV6Address(hostname));
        }
        else {
            final InetAddress address = addr.getAddress();
            final String hostString = toAddressString(address);
            sb = newSocketAddressStringBuilder(hostString, port, address instanceof Inet4Address);
        }
        return sb.append(':').append(port).toString();
    }
    
    public static String toSocketAddressString(final String host, final int port) {
        final String portStr = String.valueOf(port);
        return newSocketAddressStringBuilder(host, portStr, !isValidIpV6Address(host)).append(':').append(portStr).toString();
    }
    
    private static StringBuilder newSocketAddressStringBuilder(final String host, final String port, final boolean ipv4) {
        final int hostLen = host.length();
        if (ipv4) {
            return new StringBuilder(hostLen + 1 + port.length()).append(host);
        }
        final StringBuilder stringBuilder = new StringBuilder(hostLen + 3 + port.length());
        if (hostLen > 1 && host.charAt(0) == '[' && host.charAt(hostLen - 1) == ']') {
            return stringBuilder.append(host);
        }
        return stringBuilder.append('[').append(host).append(']');
    }
    
    public static String toAddressString(final InetAddress ip) {
        return toAddressString(ip, false);
    }
    
    public static String toAddressString(final InetAddress ip, final boolean ipv4Mapped) {
        if (ip instanceof Inet4Address) {
            return ip.getHostAddress();
        }
        if (!(ip instanceof Inet6Address)) {
            throw new IllegalArgumentException("Unhandled type: " + ip);
        }
        return toAddressString(ip.getAddress(), 0, ipv4Mapped);
    }
    
    private static String toAddressString(final byte[] bytes, final int offset, final boolean ipv4Mapped) {
        final int[] words = new int[8];
        for (int end = offset + words.length, i = offset; i < end; ++i) {
            words[i] = ((bytes[i << 1] & 0xFF) << 8 | (bytes[(i << 1) + 1] & 0xFF));
        }
        int currentStart = -1;
        int shortestStart = -1;
        int shortestLength = 0;
        int i;
        for (i = 0; i < words.length; ++i) {
            if (words[i] == 0) {
                if (currentStart < 0) {
                    currentStart = i;
                }
            }
            else if (currentStart >= 0) {
                final int currentLength = i - currentStart;
                if (currentLength > shortestLength) {
                    shortestStart = currentStart;
                    shortestLength = currentLength;
                }
                currentStart = -1;
            }
        }
        if (currentStart >= 0) {
            final int currentLength = i - currentStart;
            if (currentLength > shortestLength) {
                shortestStart = currentStart;
                shortestLength = currentLength;
            }
        }
        if (shortestLength == 1) {
            shortestLength = 0;
            shortestStart = -1;
        }
        final int shortestEnd = shortestStart + shortestLength;
        final StringBuilder b = new StringBuilder(39);
        if (shortestEnd < 0) {
            b.append(Integer.toHexString(words[0]));
            for (i = 1; i < words.length; ++i) {
                b.append(':');
                b.append(Integer.toHexString(words[i]));
            }
        }
        else {
            boolean isIpv4Mapped;
            if (inRangeEndExclusive(0, shortestStart, shortestEnd)) {
                b.append("::");
                isIpv4Mapped = (ipv4Mapped && shortestEnd == 5 && words[5] == 65535);
            }
            else {
                b.append(Integer.toHexString(words[0]));
                isIpv4Mapped = false;
            }
            for (i = 1; i < words.length; ++i) {
                if (!inRangeEndExclusive(i, shortestStart, shortestEnd)) {
                    if (!inRangeEndExclusive(i - 1, shortestStart, shortestEnd)) {
                        if (!isIpv4Mapped || i == 6) {
                            b.append(':');
                        }
                        else {
                            b.append('.');
                        }
                    }
                    if (isIpv4Mapped && i > 5) {
                        b.append(words[i] >> 8);
                        b.append('.');
                        b.append(words[i] & 0xFF);
                    }
                    else {
                        b.append(Integer.toHexString(words[i]));
                    }
                }
                else if (!inRangeEndExclusive(i - 1, shortestStart, shortestEnd)) {
                    b.append("::");
                }
            }
        }
        return b.toString();
    }
    
    public static String getHostname(final InetSocketAddress addr) {
        return (PlatformDependent.javaVersion() >= 7) ? addr.getHostString() : addr.getHostName();
    }
    
    private static boolean inRangeEndExclusive(final int value, final int start, final int end) {
        return value >= start && value < end;
    }
    
    private NetUtil() {
    }
    
    static {
        IPV4_PREFERRED = SystemPropertyUtil.getBoolean("java.net.preferIPv4Stack", false);
        IPV6_ADDRESSES_PREFERRED = SystemPropertyUtil.getBoolean("java.net.preferIPv6Addresses", false);
        (logger = InternalLoggerFactory.getInstance(NetUtil.class)).debug("-Djava.net.preferIPv4Stack: {}", (Object)NetUtil.IPV4_PREFERRED);
        NetUtil.logger.debug("-Djava.net.preferIPv6Addresses: {}", (Object)NetUtil.IPV6_ADDRESSES_PREFERRED);
        LOCALHOST4 = NetUtilInitializations.createLocalhost4();
        LOCALHOST6 = NetUtilInitializations.createLocalhost6();
        final NetUtilInitializations.NetworkIfaceAndInetAddress loopback = NetUtilInitializations.determineLoopback(NetUtil.LOCALHOST4, NetUtil.LOCALHOST6);
        LOOPBACK_IF = loopback.iface();
        LOCALHOST = loopback.address();
        SOMAXCONN = AccessController.doPrivileged((PrivilegedAction<Integer>)new PrivilegedAction<Integer>() {
            @Override
            public Integer run() {
                int somaxconn = PlatformDependent.isWindows() ? 200 : 128;
                final File file = new File("/proc/sys/net/core/somaxconn");
                BufferedReader in = null;
                try {
                    if (file.exists()) {
                        in = new BufferedReader(new FileReader(file));
                        somaxconn = Integer.parseInt(in.readLine());
                        if (NetUtil.logger.isDebugEnabled()) {
                            NetUtil.logger.debug("{}: {}", file, somaxconn);
                        }
                    }
                    else {
                        Integer tmp = null;
                        if (SystemPropertyUtil.getBoolean("io.netty.net.somaxconn.trySysctl", false)) {
                            tmp = sysctlGetInt("kern.ipc.somaxconn");
                            if (tmp == null) {
                                tmp = sysctlGetInt("kern.ipc.soacceptqueue");
                                if (tmp != null) {
                                    somaxconn = tmp;
                                }
                            }
                            else {
                                somaxconn = tmp;
                            }
                        }
                        if (tmp == null) {
                            NetUtil.logger.debug("Failed to get SOMAXCONN from sysctl and file {}. Default: {}", file, somaxconn);
                        }
                    }
                }
                catch (final Exception e) {
                    if (NetUtil.logger.isDebugEnabled()) {
                        NetUtil.logger.debug("Failed to get SOMAXCONN from sysctl and file {}. Default: {}", file, somaxconn, e);
                    }
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (final Exception ex) {}
                    }
                }
                return somaxconn;
            }
        });
    }
}
