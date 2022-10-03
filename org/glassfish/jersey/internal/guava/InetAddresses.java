package org.glassfish.jersey.internal.guava;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public final class InetAddresses
{
    private static final int IPV4_PART_COUNT = 4;
    private static final int IPV6_PART_COUNT = 8;
    
    private InetAddresses() {
    }
    
    private static byte[] ipStringToBytes(String ipString) {
        boolean hasColon = false;
        boolean hasDot = false;
        for (int i = 0; i < ipString.length(); ++i) {
            final char c = ipString.charAt(i);
            if (c == '.') {
                hasDot = true;
            }
            else if (c == ':') {
                if (hasDot) {
                    return null;
                }
                hasColon = true;
            }
            else if (Character.digit(c, 16) == -1) {
                return null;
            }
        }
        if (hasColon) {
            if (hasDot) {
                ipString = convertDottedQuadToHex(ipString);
                if (ipString == null) {
                    return null;
                }
            }
            return textToNumericFormatV6(ipString);
        }
        if (hasDot) {
            return textToNumericFormatV4(ipString);
        }
        return null;
    }
    
    private static byte[] textToNumericFormatV4(final String ipString) {
        final String[] address = ipString.split("\\.", 5);
        if (address.length != 4) {
            return null;
        }
        final byte[] bytes = new byte[4];
        try {
            for (int i = 0; i < bytes.length; ++i) {
                bytes[i] = parseOctet(address[i]);
            }
        }
        catch (final NumberFormatException ex) {
            return null;
        }
        return bytes;
    }
    
    private static byte[] textToNumericFormatV6(final String ipString) {
        final String[] parts = ipString.split(":", 10);
        if (parts.length < 3 || parts.length > 9) {
            return null;
        }
        int skipIndex = -1;
        for (int i = 1; i < parts.length - 1; ++i) {
            if (parts[i].length() == 0) {
                if (skipIndex >= 0) {
                    return null;
                }
                skipIndex = i;
            }
        }
        int partsHi;
        int partsLo;
        if (skipIndex >= 0) {
            partsHi = skipIndex;
            partsLo = parts.length - skipIndex - 1;
            if (parts[0].length() == 0 && --partsHi != 0) {
                return null;
            }
            if (parts[parts.length - 1].length() == 0 && --partsLo != 0) {
                return null;
            }
        }
        else {
            partsHi = parts.length;
            partsLo = 0;
        }
        final int partsSkipped = 8 - (partsHi + partsLo);
        Label_0148: {
            if (skipIndex >= 0) {
                if (partsSkipped >= 1) {
                    break Label_0148;
                }
            }
            else if (partsSkipped == 0) {
                break Label_0148;
            }
            return null;
        }
        final ByteBuffer rawBytes = ByteBuffer.allocate(16);
        try {
            for (int j = 0; j < partsHi; ++j) {
                rawBytes.putShort(parseHextet(parts[j]));
            }
            for (int j = 0; j < partsSkipped; ++j) {
                rawBytes.putShort((short)0);
            }
            for (int j = partsLo; j > 0; --j) {
                rawBytes.putShort(parseHextet(parts[parts.length - j]));
            }
        }
        catch (final NumberFormatException ex) {
            return null;
        }
        return rawBytes.array();
    }
    
    private static String convertDottedQuadToHex(final String ipString) {
        final int lastColon = ipString.lastIndexOf(58);
        final String initialPart = ipString.substring(0, lastColon + 1);
        final String dottedQuad = ipString.substring(lastColon + 1);
        final byte[] quad = textToNumericFormatV4(dottedQuad);
        if (quad == null) {
            return null;
        }
        final String penultimate = Integer.toHexString((quad[0] & 0xFF) << 8 | (quad[1] & 0xFF));
        final String ultimate = Integer.toHexString((quad[2] & 0xFF) << 8 | (quad[3] & 0xFF));
        return initialPart + penultimate + ":" + ultimate;
    }
    
    private static byte parseOctet(final String ipPart) {
        final int octet = Integer.parseInt(ipPart);
        if (octet > 255 || (ipPart.startsWith("0") && ipPart.length() > 1)) {
            throw new NumberFormatException();
        }
        return (byte)octet;
    }
    
    private static short parseHextet(final String ipPart) {
        final int hextet = Integer.parseInt(ipPart, 16);
        if (hextet > 65535) {
            throw new NumberFormatException();
        }
        return (short)hextet;
    }
    
    private static InetAddress bytesToInetAddress(final byte[] addr) {
        try {
            return InetAddress.getByAddress(addr);
        }
        catch (final UnknownHostException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    private static InetAddress forUriStringNoThrow(final String hostAddr) {
        Preconditions.checkNotNull(hostAddr);
        String ipString;
        int expectBytes;
        if (hostAddr.startsWith("[") && hostAddr.endsWith("]")) {
            ipString = hostAddr.substring(1, hostAddr.length() - 1);
            expectBytes = 16;
        }
        else {
            ipString = hostAddr;
            expectBytes = 4;
        }
        final byte[] addr = ipStringToBytes(ipString);
        if (addr == null || addr.length != expectBytes) {
            return null;
        }
        return bytesToInetAddress(addr);
    }
    
    public static boolean isUriInetAddress(final String ipString) {
        return forUriStringNoThrow(ipString) != null;
    }
    
    public static boolean isMappedIPv4Address(final String ipString) {
        final byte[] bytes = ipStringToBytes(ipString);
        if (bytes != null && bytes.length == 16) {
            for (int i = 0; i < 10; ++i) {
                if (bytes[i] != 0) {
                    return false;
                }
            }
            for (int i = 10; i < 12; ++i) {
                if (bytes[i] != -1) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
