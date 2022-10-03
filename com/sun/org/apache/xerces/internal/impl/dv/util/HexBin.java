package com.sun.org.apache.xerces.internal.impl.dv.util;

public final class HexBin
{
    private static final int BASELENGTH = 128;
    private static final int LOOKUPLENGTH = 16;
    private static final byte[] hexNumberTable;
    private static final char[] lookUpHexAlphabet;
    
    public static String encode(final byte[] binaryData) {
        if (binaryData == null) {
            return null;
        }
        final int lengthData = binaryData.length;
        final int lengthEncode = lengthData * 2;
        final char[] encodedData = new char[lengthEncode];
        for (int i = 0; i < lengthData; ++i) {
            int temp = binaryData[i];
            if (temp < 0) {
                temp += 256;
            }
            encodedData[i * 2] = HexBin.lookUpHexAlphabet[temp >> 4];
            encodedData[i * 2 + 1] = HexBin.lookUpHexAlphabet[temp & 0xF];
        }
        return new String(encodedData);
    }
    
    public static byte[] decode(final String encoded) {
        if (encoded == null) {
            return null;
        }
        final int lengthData = encoded.length();
        if (lengthData % 2 != 0) {
            return null;
        }
        final char[] binaryData = encoded.toCharArray();
        final int lengthDecode = lengthData / 2;
        final byte[] decodedData = new byte[lengthDecode];
        for (int i = 0; i < lengthDecode; ++i) {
            char tempChar = binaryData[i * 2];
            final byte temp1 = (byte)((tempChar < '\u0080') ? HexBin.hexNumberTable[tempChar] : -1);
            if (temp1 == -1) {
                return null;
            }
            tempChar = binaryData[i * 2 + 1];
            final byte temp2 = (byte)((tempChar < '\u0080') ? HexBin.hexNumberTable[tempChar] : -1);
            if (temp2 == -1) {
                return null;
            }
            decodedData[i] = (byte)(temp1 << 4 | temp2);
        }
        return decodedData;
    }
    
    static {
        hexNumberTable = new byte[128];
        lookUpHexAlphabet = new char[16];
        for (int i = 0; i < 128; ++i) {
            HexBin.hexNumberTable[i] = -1;
        }
        for (int i = 57; i >= 48; --i) {
            HexBin.hexNumberTable[i] = (byte)(i - 48);
        }
        for (int i = 70; i >= 65; --i) {
            HexBin.hexNumberTable[i] = (byte)(i - 65 + 10);
        }
        for (int i = 102; i >= 97; --i) {
            HexBin.hexNumberTable[i] = (byte)(i - 97 + 10);
        }
        for (int i = 0; i < 10; ++i) {
            HexBin.lookUpHexAlphabet[i] = (char)(48 + i);
        }
        for (int i = 10; i <= 15; ++i) {
            HexBin.lookUpHexAlphabet[i] = (char)(65 + i - 10);
        }
    }
}
