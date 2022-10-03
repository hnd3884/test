package com.adventnet.iam.security;

import org.xbill.DNS.Resolver;
import org.xbill.DNS.Cache;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.Lookup;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.net.InetAddress;
import java.util.logging.Logger;

public class IPUtil
{
    static final Logger LOGGER;
    public static final long ALL_ONES = 4294967295L;
    public static final long[] NON_ROUTABLE_IP_SUFFIX;
    public static final long[] NON_ROUTABLE_NWS;
    
    public static boolean isPrivateIP(String ipaddress) {
        try {
            if (ipaddress == null) {
                return false;
            }
            ipaddress = normalizeIpv6Address(ipaddress);
            final InetAddress inetAddress = InetAddress.getByName(ipaddress);
            if (inetAddress.isSiteLocalAddress() || inetAddress.isLinkLocalAddress() || inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress()) {
                return true;
            }
            if (ipaddress.contains(":")) {
                final String ipv6AddressRange = SecurityFilterProperties.getInstance(SecurityUtil.getCurrentRequest()).getAuthenticationProvider().getIAMAppSystemConfigurationValue("ipv6.address.range");
                final Pattern ipv6AddressPattern = Pattern.compile(ipv6AddressRange);
                return ipv6AddressPattern.matcher(ipaddress).matches();
            }
            return false;
        }
        catch (final UnknownHostException ex) {
            IPUtil.LOGGER.log(Level.SEVERE, "Unknown host :: {0}", ipaddress);
            throw new IAMSecurityException("UNKNOWN_HOST");
        }
    }
    
    private static String normalizeIpv6Address(final String ipaddress) {
        if (ipaddress.startsWith("[") && ipaddress.endsWith("]")) {
            return ipaddress.substring(1, ipaddress.length() - 1);
        }
        return ipaddress;
    }
    
    public static boolean isValidIPv4(final String ipAddress) {
        final String[] tokens = ipAddress.split("\\.");
        if (tokens.length != 4) {
            return false;
        }
        try {
            for (final String token : tokens) {
                final int i = Integer.parseInt(token);
                if (i < 0 || i > 255) {
                    return false;
                }
            }
        }
        catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public static long IP_LONG(final String address) {
        long laddr = 0L;
        final String[] st = address.split("\\.");
        final int[] addr = new int[4];
        for (int i = 0; i < 4; ++i) {
            addr[i] = Integer.parseInt(st[i]);
        }
        for (int i = 0; i < 4; ++i) {
            laddr |= (long)addr[i] << 8 * (3 - i);
        }
        return laddr;
    }
    
    public static BigInteger IPv6_TO_BIGINT(final String ipv6Address) {
        final String[] str = ipv6Address.split(":");
        final int[] addr = new int[8];
        for (int i = 0; i < 8; ++i) {
            addr[i] = Integer.parseInt(str[i], 16);
        }
        BigInteger bigInt = new BigInteger(String.valueOf(0));
        for (int j = 0; j < 8; ++j) {
            bigInt = bigInt.or(new BigInteger(String.valueOf(addr[j])).shiftLeft(16 * (7 - j)));
        }
        return bigInt;
    }
    
    public static String IP_STRING(final long ipL) {
        final byte[] dword = { (byte)(ipL >> 24 & 0xFFL), (byte)(ipL >> 16 & 0xFFL), (byte)(ipL >> 8 & 0xFFL), (byte)(ipL & 0xFFL) };
        try {
            return InetAddress.getByAddress(dword).getHostAddress();
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public static boolean IS_IP_IN_RANGE(final long ip, final long start, final long end) {
        return ip <= end && ip >= start;
    }
    
    public static boolean IS_IP_IN_RANGE(final String ipStr, final String startStr, final String endStr) {
        if (ipStr.contains(":")) {
            return IS_IPv6_IN_RANGE(ipStr, startStr, endStr);
        }
        return IS_IP_IN_RANGE(IP_LONG(ipStr), IP_LONG(startStr), IP_LONG(endStr));
    }
    
    public static boolean IS_IPv6_IN_RANGE(final String ipAddress, final String startRange, final String endRange) {
        final String formatIp = toIpv6FormattedAddress(ipAddress);
        final String formatStrt = toIpv6FormattedAddress(startRange);
        final String formatEnd = toIpv6FormattedAddress(endRange);
        return compareIpv6Address(formatStrt, formatIp) && compareIpv6Address(formatIp, formatEnd);
    }
    
    public static boolean IS_BLOCKED(final String ip, final String blockListServer, final String whiteListServer) {
        final String[] ipArr = ip.split("\\.");
        if (ipArr == null || ipArr.length < 4) {
            return false;
        }
        final StringBuilder builder = new StringBuilder(15).append(ipArr[3]).append(".").append(ipArr[2]).append(".").append(ipArr[1]).append(".").append(ipArr[0]).append(".");
        final String reverseIP = builder.toString();
        final String blackLookupStr = reverseIP + blockListServer;
        boolean result = LOOKUP(blackLookupStr);
        if (result) {
            final String whiteLookupStr = reverseIP + whiteListServer;
            result = !LOOKUP(whiteLookupStr);
        }
        return result;
    }
    
    public static boolean LOOKUP(final String lookupStr) {
        try {
            final Lookup lookup = new Lookup(lookupStr, 1);
            final Resolver resolver = (Resolver)new SimpleResolver();
            lookup.setResolver(resolver);
            lookup.setCache((Cache)null);
            lookup.run();
            if (lookup.getResult() == 0) {
                return true;
            }
            IPUtil.LOGGER.log(Level.FINE, "Black list lookup result is : \"{0}\"", lookup.getResult());
        }
        catch (final Exception e) {
            IPUtil.LOGGER.log(Level.WARNING, "", e);
        }
        return false;
    }
    
    public static void test_IS_BLOCKED(final String[] args) {
        if (args.length != 3) {
            IPUtil.LOGGER.info("<Usage : IPUtil ipAddress blackListServer whiteListServer");
            System.exit(0);
        }
        final boolean blocked = IS_BLOCKED(args[0], args[1], args[2]);
        IPUtil.LOGGER.info("Is IP Blocked  : " + blocked);
    }
    
    public static String toIpv6FormattedAddress(final String ipAddress) {
        final String[] parts = ipAddress.split(":");
        final String[] ipv6Address = new String[8];
        int index = 0;
        if (ipAddress.startsWith("::") || ipAddress.endsWith("::")) {
            for (int max = ipAddress.startsWith("::") ? 10 : 8, temp = parts.length; temp < max; ++temp) {
                if (max == 10) {
                    ipv6Address[index++] = "0000";
                }
                else {
                    ipv6Address[temp] = "0000";
                }
            }
            for (String part : parts) {
                for (int temp2 = part.length(); 0 < temp2 && temp2 < 4; ++temp2) {
                    part = "0" + part;
                }
                if (part.length() > 0) {
                    ipv6Address[index++] = part;
                }
            }
        }
        else if (parts.length != 8) {
            for (String part2 : parts) {
                if (part2.length() == 0) {
                    for (int temp3 = 0; temp3 < 9 - parts.length; ++temp3) {
                        ipv6Address[index++] = "0000";
                    }
                }
                else {
                    for (int temp3 = part2.length(); 0 < temp3 && temp3 < 4; ++temp3) {
                        part2 = "0" + part2;
                    }
                    ipv6Address[index++] = part2;
                }
            }
        }
        else {
            for (String part2 : parts) {
                if (part2.length() < 4) {
                    for (int temp3 = part2.length(); 0 < temp3 && temp3 < 4; ++temp3) {
                        part2 = "0" + part2;
                    }
                }
                ipv6Address[index++] = part2;
            }
        }
        String ipv6CompleteAddress = "";
        int i = 0;
        for (final String part3 : ipv6Address) {
            if (i == 0) {
                ipv6CompleteAddress += part3;
                i = 1;
            }
            else {
                ipv6CompleteAddress = ipv6CompleteAddress + ":" + part3;
            }
        }
        return ipv6CompleteAddress;
    }
    
    public static boolean compareIpv6Address(final String lowIp, final String highIP) {
        final String[] low = lowIp.split(":");
        final String[] high = highIP.split(":");
        for (int index = 0; index < low.length; ++index) {
            final int result = low[index].compareToIgnoreCase(high[index]);
            if (result >= 1) {
                return false;
            }
            if (result != 0) {
                break;
            }
        }
        return true;
    }
    
    public static boolean isSubnetMatches(final byte[] configuredAddress, final byte[] remoteAddress, final int cidr) {
        final int nMaskFullBytes = cidr / 8;
        final byte lastByte = (byte)(65280 >> (cidr & 0x7));
        return isSubnetMatches(configuredAddress, remoteAddress, nMaskFullBytes, lastByte);
    }
    
    public static boolean isSubnetMatches(final byte[] configuredAddress, final byte[] remoteAddress, final int nMaskFullBytes, final byte lastByte) {
        if (configuredAddress.length != remoteAddress.length) {
            return false;
        }
        for (int i = 0; i < nMaskFullBytes; ++i) {
            if (remoteAddress[i] != configuredAddress[i]) {
                return false;
            }
        }
        return lastByte == 0 || (remoteAddress[nMaskFullBytes] & lastByte) == (configuredAddress[nMaskFullBytes] & lastByte);
    }
    
    public static int GET_CIDR_VALUE(final String ipAddress) {
        if (ipAddress.indexOf(47) == -1) {
            return 32;
        }
        return Integer.parseInt(ipAddress.substring(ipAddress.indexOf(47) + 1).trim());
    }
    
    public static String REMOVE_CIDR(final String ipAddress) {
        if (ipAddress.indexOf(47) == -1) {
            return ipAddress;
        }
        return ipAddress.substring(0, ipAddress.indexOf(47)).trim();
    }
    
    public static long IPv4_TO_LONG(final byte[] addr) {
        long address = 0L;
        address = (address << 8 | (long)(addr[0] & 0xFF));
        address = (address << 8 | (long)(addr[1] & 0xFF));
        address = (address << 8 | (long)(addr[2] & 0xFF));
        address = (address << 8 | (long)(addr[3] & 0xFF));
        return address;
    }
    
    public static BigInteger IPv6_TO_BIGINT(final byte[] addr) {
        final BigInteger address = new BigInteger(1, addr);
        return address;
    }
    
    public static InetAddress parseAddress(final String address) {
        try {
            return InetAddress.getByName(address);
        }
        catch (final UnknownHostException e) {
            IPUtil.LOGGER.log(Level.SEVERE, "Unknown host :: {0}", address);
            throw new IAMSecurityException("UNKNOWN_HOST");
        }
    }
    
    static {
        LOGGER = Logger.getLogger(IPUtil.class.getName());
        NON_ROUTABLE_IP_SUFFIX = new long[] { 4503599626321920L, 281474976645120L, 72057594021150720L };
        NON_ROUTABLE_NWS = new long[] { 2886729728L, 3232235520L, 167772160L };
    }
}
