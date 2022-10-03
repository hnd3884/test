package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Arrays;
import java.util.Map;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.LinkedHashMap;
import io.netty.util.NetUtil;
import io.netty.util.internal.logging.InternalLogger;

public final class MacAddressUtil
{
    private static final InternalLogger logger;
    private static final int EUI64_MAC_ADDRESS_LENGTH = 8;
    private static final int EUI48_MAC_ADDRESS_LENGTH = 6;
    
    public static byte[] bestAvailableMac() {
        byte[] bestMacAddr = EmptyArrays.EMPTY_BYTES;
        InetAddress bestInetAddr = NetUtil.LOCALHOST4;
        final Map<NetworkInterface, InetAddress> ifaces = new LinkedHashMap<NetworkInterface, InetAddress>();
        try {
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    final NetworkInterface iface = interfaces.nextElement();
                    final Enumeration<InetAddress> addrs = SocketUtils.addressesFromNetworkInterface(iface);
                    if (addrs.hasMoreElements()) {
                        final InetAddress a = addrs.nextElement();
                        if (a.isLoopbackAddress()) {
                            continue;
                        }
                        ifaces.put(iface, a);
                    }
                }
            }
        }
        catch (final SocketException e) {
            MacAddressUtil.logger.warn("Failed to retrieve the list of available network interfaces", e);
        }
        for (final Map.Entry<NetworkInterface, InetAddress> entry : ifaces.entrySet()) {
            final NetworkInterface iface2 = entry.getKey();
            final InetAddress inetAddr = entry.getValue();
            if (iface2.isVirtual()) {
                continue;
            }
            byte[] macAddr;
            try {
                macAddr = SocketUtils.hardwareAddressFromNetworkInterface(iface2);
            }
            catch (final SocketException e2) {
                MacAddressUtil.logger.debug("Failed to get the hardware address of a network interface: {}", iface2, e2);
                continue;
            }
            boolean replace = false;
            int res = compareAddresses(bestMacAddr, macAddr);
            if (res < 0) {
                replace = true;
            }
            else if (res == 0) {
                res = compareAddresses(bestInetAddr, inetAddr);
                if (res < 0) {
                    replace = true;
                }
                else if (res == 0 && bestMacAddr.length < macAddr.length) {
                    replace = true;
                }
            }
            if (!replace) {
                continue;
            }
            bestMacAddr = macAddr;
            bestInetAddr = inetAddr;
        }
        if (bestMacAddr == EmptyArrays.EMPTY_BYTES) {
            return null;
        }
        switch (bestMacAddr.length) {
            case 6: {
                final byte[] newAddr = new byte[8];
                System.arraycopy(bestMacAddr, 0, newAddr, 0, 3);
                newAddr[3] = -1;
                newAddr[4] = -2;
                System.arraycopy(bestMacAddr, 3, newAddr, 5, 3);
                bestMacAddr = newAddr;
                break;
            }
            default: {
                bestMacAddr = Arrays.copyOf(bestMacAddr, 8);
                break;
            }
        }
        return bestMacAddr;
    }
    
    public static byte[] defaultMachineId() {
        byte[] bestMacAddr = bestAvailableMac();
        if (bestMacAddr == null) {
            bestMacAddr = new byte[8];
            PlatformDependent.threadLocalRandom().nextBytes(bestMacAddr);
            MacAddressUtil.logger.warn("Failed to find a usable hardware address from the network interfaces; using random bytes: {}", formatAddress(bestMacAddr));
        }
        return bestMacAddr;
    }
    
    public static byte[] parseMAC(final String value) {
        char separator = '\0';
        byte[] machineId = null;
        switch (value.length()) {
            case 17: {
                separator = value.charAt(2);
                validateMacSeparator(separator);
                machineId = new byte[6];
                break;
            }
            case 23: {
                separator = value.charAt(2);
                validateMacSeparator(separator);
                machineId = new byte[8];
                break;
            }
            default: {
                throw new IllegalArgumentException("value is not supported [MAC-48, EUI-48, EUI-64]");
            }
        }
        final int end = machineId.length - 1;
        int j = 0;
        for (int i = 0; i < end; ++i, j += 3) {
            final int sIndex = j + 2;
            machineId[i] = StringUtil.decodeHexByte(value, j);
            if (value.charAt(sIndex) != separator) {
                throw new IllegalArgumentException("expected separator '" + separator + " but got '" + value.charAt(sIndex) + "' at index: " + sIndex);
            }
        }
        machineId[end] = StringUtil.decodeHexByte(value, j);
        return machineId;
    }
    
    private static void validateMacSeparator(final char separator) {
        if (separator != ':' && separator != '-') {
            throw new IllegalArgumentException("unsupported separator: " + separator + " (expected: [:-])");
        }
    }
    
    public static String formatAddress(final byte[] addr) {
        final StringBuilder buf = new StringBuilder(24);
        for (final byte b : addr) {
            buf.append(String.format("%02x:", b & 0xFF));
        }
        return buf.substring(0, buf.length() - 1);
    }
    
    static int compareAddresses(final byte[] current, final byte[] candidate) {
        if (candidate == null || candidate.length < 6) {
            return 1;
        }
        boolean onlyZeroAndOne = true;
        for (final byte b : candidate) {
            if (b != 0 && b != 1) {
                onlyZeroAndOne = false;
                break;
            }
        }
        if (onlyZeroAndOne) {
            return 1;
        }
        if ((candidate[0] & 0x1) != 0x0) {
            return 1;
        }
        if ((candidate[0] & 0x2) == 0x0) {
            if (current.length != 0 && (current[0] & 0x2) == 0x0) {
                return 0;
            }
            return -1;
        }
        else {
            if (current.length != 0 && (current[0] & 0x2) == 0x0) {
                return 1;
            }
            return 0;
        }
    }
    
    private static int compareAddresses(final InetAddress current, final InetAddress candidate) {
        return scoreAddress(current) - scoreAddress(candidate);
    }
    
    private static int scoreAddress(final InetAddress addr) {
        if (addr.isAnyLocalAddress() || addr.isLoopbackAddress()) {
            return 0;
        }
        if (addr.isMulticastAddress()) {
            return 1;
        }
        if (addr.isLinkLocalAddress()) {
            return 2;
        }
        if (addr.isSiteLocalAddress()) {
            return 3;
        }
        return 4;
    }
    
    private MacAddressUtil() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(MacAddressUtil.class);
    }
}
