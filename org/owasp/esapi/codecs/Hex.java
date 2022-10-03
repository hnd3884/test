package org.owasp.esapi.codecs;

public class Hex
{
    public static String toHex(final byte[] b, final boolean leading0x) {
        final StringBuffer hexString = new StringBuffer();
        if (leading0x) {
            hexString.append("0x");
        }
        for (int i = 0; i < b.length; ++i) {
            final int j = b[i] & 0xFF;
            final String hex = Integer.toHexString(j);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    public static String encode(final byte[] b, final boolean leading0x) {
        return toHex(b, leading0x);
    }
    
    public static byte[] fromHex(final String hexStr) {
        String hexRep = hexStr;
        if (hexStr.startsWith("0x")) {
            hexRep = hexStr.substring(2);
        }
        final int len = hexRep.length() / 2;
        final byte[] rawBytes = new byte[len];
        for (int i = 0; i < len; ++i) {
            final String substr = hexRep.substring(i * 2, i * 2 + 2);
            rawBytes[i] = (byte)Integer.parseInt(substr, 16);
        }
        return rawBytes;
    }
    
    public static byte[] decode(final String hexStr) {
        return fromHex(hexStr);
    }
}
