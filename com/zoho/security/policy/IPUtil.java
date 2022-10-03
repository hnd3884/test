package com.zoho.security.policy;

import java.net.UnknownHostException;
import java.util.logging.Level;
import java.net.InetAddress;
import java.math.BigInteger;
import java.util.logging.Logger;

public class IPUtil
{
    private static final Logger LOGGER;
    
    public static long ipV42Long(final String address) {
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
    
    public static BigInteger ipV62BigInt(final String ipv6Address) {
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
    
    public static String long2IPV4(final long ipL) {
        final byte[] dword = { (byte)(ipL >> 24 & 0xFFL), (byte)(ipL >> 16 & 0xFFL), (byte)(ipL >> 8 & 0xFFL), (byte)(ipL & 0xFFL) };
        try {
            return InetAddress.getByAddress(dword).getHostAddress();
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public static void checkIP(final String ipaddress) {
        if (isPrivateIP(ipaddress)) {
            throw new SecurityPolicyException("INTERNAL_IP");
        }
    }
    
    public static boolean isPrivateIP(final String ipaddress) {
        try {
            if (ipaddress == null) {
                return false;
            }
            final InetAddress inetAddress = InetAddress.getByName(ipaddress);
            return inetAddress.isSiteLocalAddress() || inetAddress.isLinkLocalAddress() || inetAddress.isLoopbackAddress();
        }
        catch (final UnknownHostException ex) {
            IPUtil.LOGGER.log(Level.SEVERE, "Unknown host :: {0}", ipaddress);
            return true;
        }
    }
    
    static {
        LOGGER = Logger.getLogger(IPUtil.class.getName());
    }
}
