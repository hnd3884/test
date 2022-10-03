package org.apache.xmlbeans.impl.util;

public final class Base64
{
    private static final int BASELENGTH = 255;
    private static final int LOOKUPLENGTH = 64;
    private static final int TWENTYFOURBITGROUP = 24;
    private static final int EIGHTBIT = 8;
    private static final int SIXTEENBIT = 16;
    private static final int FOURBYTE = 4;
    private static final int SIGN = -128;
    private static final byte PAD = 61;
    private static final boolean fDebug = false;
    private static byte[] base64Alphabet;
    private static byte[] lookUpBase64Alphabet;
    
    protected static boolean isWhiteSpace(final byte octect) {
        return octect == 32 || octect == 13 || octect == 10 || octect == 9;
    }
    
    protected static boolean isPad(final byte octect) {
        return octect == 61;
    }
    
    protected static boolean isData(final byte octect) {
        return Base64.base64Alphabet[octect] != -1;
    }
    
    protected static boolean isBase64(final byte octect) {
        return isWhiteSpace(octect) || isPad(octect) || isData(octect);
    }
    
    public static byte[] encode(final byte[] binaryData) {
        if (binaryData == null) {
            return null;
        }
        final int lengthDataBits = binaryData.length * 8;
        final int fewerThan24bits = lengthDataBits % 24;
        final int numberTriplets = lengthDataBits / 24;
        byte[] encodedData = null;
        if (fewerThan24bits != 0) {
            encodedData = new byte[(numberTriplets + 1) * 4];
        }
        else {
            encodedData = new byte[numberTriplets * 4];
        }
        byte k = 0;
        byte l = 0;
        byte b1 = 0;
        byte b2 = 0;
        byte b3 = 0;
        int encodedIndex = 0;
        int dataIndex = 0;
        int i;
        byte val1;
        byte val2;
        byte val3;
        for (i = 0, i = 0; i < numberTriplets; ++i) {
            dataIndex = i * 3;
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            b3 = binaryData[dataIndex + 2];
            l = (byte)(b2 & 0xF);
            k = (byte)(b1 & 0x3);
            encodedIndex = i * 4;
            val1 = (((b1 & 0xFFFFFF80) == 0x0) ? ((byte)(b1 >> 2)) : ((byte)(b1 >> 2 ^ 0xC0)));
            val2 = (((b2 & 0xFFFFFF80) == 0x0) ? ((byte)(b2 >> 4)) : ((byte)(b2 >> 4 ^ 0xF0)));
            val3 = (((b3 & 0xFFFFFF80) == 0x0) ? ((byte)(b3 >> 6)) : ((byte)(b3 >> 6 ^ 0xFC)));
            encodedData[encodedIndex] = Base64.lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = Base64.lookUpBase64Alphabet[val2 | k << 4];
            encodedData[encodedIndex + 2] = Base64.lookUpBase64Alphabet[l << 2 | val3];
            encodedData[encodedIndex + 3] = Base64.lookUpBase64Alphabet[b3 & 0x3F];
        }
        dataIndex = i * 3;
        encodedIndex = i * 4;
        if (fewerThan24bits == 8) {
            b1 = binaryData[dataIndex];
            k = (byte)(b1 & 0x3);
            val1 = (((b1 & 0xFFFFFF80) == 0x0) ? ((byte)(b1 >> 2)) : ((byte)(b1 >> 2 ^ 0xC0)));
            encodedData[encodedIndex] = Base64.lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = Base64.lookUpBase64Alphabet[k << 4];
            encodedData[encodedIndex + 3] = (encodedData[encodedIndex + 2] = 61);
        }
        else if (fewerThan24bits == 16) {
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            l = (byte)(b2 & 0xF);
            k = (byte)(b1 & 0x3);
            val1 = (((b1 & 0xFFFFFF80) == 0x0) ? ((byte)(b1 >> 2)) : ((byte)(b1 >> 2 ^ 0xC0)));
            val2 = (((b2 & 0xFFFFFF80) == 0x0) ? ((byte)(b2 >> 4)) : ((byte)(b2 >> 4 ^ 0xF0)));
            encodedData[encodedIndex] = Base64.lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = Base64.lookUpBase64Alphabet[val2 | k << 4];
            encodedData[encodedIndex + 2] = Base64.lookUpBase64Alphabet[l << 2];
            encodedData[encodedIndex + 3] = 61;
        }
        return encodedData;
    }
    
    public static byte[] decode(byte[] base64Data) {
        if (base64Data == null) {
            return null;
        }
        base64Data = removeWhiteSpace(base64Data);
        if (base64Data.length % 4 != 0) {
            return null;
        }
        final int numberQuadruple = base64Data.length / 4;
        if (numberQuadruple == 0) {
            return new byte[0];
        }
        byte[] decodedData = null;
        byte b1 = 0;
        byte b2 = 0;
        byte b3 = 0;
        byte b4 = 0;
        byte d1 = 0;
        byte d2 = 0;
        byte d3 = 0;
        byte d4 = 0;
        int i = 0;
        int encodedIndex = 0;
        int dataIndex = 0;
        decodedData = new byte[numberQuadruple * 3];
        while (i < numberQuadruple - 1) {
            if (!isData(d1 = base64Data[dataIndex++]) || !isData(d2 = base64Data[dataIndex++]) || !isData(d3 = base64Data[dataIndex++]) || !isData(d4 = base64Data[dataIndex++])) {
                return null;
            }
            b1 = Base64.base64Alphabet[d1];
            b2 = Base64.base64Alphabet[d2];
            b3 = Base64.base64Alphabet[d3];
            b4 = Base64.base64Alphabet[d4];
            decodedData[encodedIndex++] = (byte)(b1 << 2 | b2 >> 4);
            decodedData[encodedIndex++] = (byte)((b2 & 0xF) << 4 | (b3 >> 2 & 0xF));
            decodedData[encodedIndex++] = (byte)(b3 << 6 | b4);
            ++i;
        }
        if (!isData(d1 = base64Data[dataIndex++]) || !isData(d2 = base64Data[dataIndex++])) {
            return null;
        }
        b1 = Base64.base64Alphabet[d1];
        b2 = Base64.base64Alphabet[d2];
        d3 = base64Data[dataIndex++];
        d4 = base64Data[dataIndex++];
        if (isData(d3) && isData(d4)) {
            b3 = Base64.base64Alphabet[d3];
            b4 = Base64.base64Alphabet[d4];
            decodedData[encodedIndex++] = (byte)(b1 << 2 | b2 >> 4);
            decodedData[encodedIndex++] = (byte)((b2 & 0xF) << 4 | (b3 >> 2 & 0xF));
            decodedData[encodedIndex++] = (byte)(b3 << 6 | b4);
            return decodedData;
        }
        if (isPad(d3) && isPad(d4)) {
            if ((b2 & 0xF) != 0x0) {
                return null;
            }
            final byte[] tmp = new byte[i * 3 + 1];
            System.arraycopy(decodedData, 0, tmp, 0, i * 3);
            tmp[encodedIndex] = (byte)(b1 << 2 | b2 >> 4);
            return tmp;
        }
        else {
            if (isPad(d3) || !isPad(d4)) {
                return null;
            }
            b3 = Base64.base64Alphabet[d3];
            if ((b3 & 0x3) != 0x0) {
                return null;
            }
            final byte[] tmp = new byte[i * 3 + 2];
            System.arraycopy(decodedData, 0, tmp, 0, i * 3);
            tmp[encodedIndex++] = (byte)(b1 << 2 | b2 >> 4);
            tmp[encodedIndex] = (byte)((b2 & 0xF) << 4 | (b3 >> 2 & 0xF));
            return tmp;
        }
    }
    
    protected static byte[] removeWhiteSpace(final byte[] data) {
        if (data == null) {
            return null;
        }
        int newSize = 0;
        final int len = data.length;
        for (int i = 0; i < len; ++i) {
            if (!isWhiteSpace(data[i])) {
                ++newSize;
            }
        }
        if (newSize == len) {
            return data;
        }
        final byte[] newArray = new byte[newSize];
        int j = 0;
        for (int k = 0; k < len; ++k) {
            if (!isWhiteSpace(data[k])) {
                newArray[j++] = data[k];
            }
        }
        return newArray;
    }
    
    static {
        Base64.base64Alphabet = new byte[255];
        Base64.lookUpBase64Alphabet = new byte[64];
        for (int i = 0; i < 255; ++i) {
            Base64.base64Alphabet[i] = -1;
        }
        for (int i = 90; i >= 65; --i) {
            Base64.base64Alphabet[i] = (byte)(i - 65);
        }
        for (int i = 122; i >= 97; --i) {
            Base64.base64Alphabet[i] = (byte)(i - 97 + 26);
        }
        for (int i = 57; i >= 48; --i) {
            Base64.base64Alphabet[i] = (byte)(i - 48 + 52);
        }
        Base64.base64Alphabet[43] = 62;
        Base64.base64Alphabet[47] = 63;
        for (int i = 0; i <= 25; ++i) {
            Base64.lookUpBase64Alphabet[i] = (byte)(65 + i);
        }
        for (int i = 26, j = 0; i <= 51; ++i, ++j) {
            Base64.lookUpBase64Alphabet[i] = (byte)(97 + j);
        }
        for (int i = 52, j = 0; i <= 61; ++i, ++j) {
            Base64.lookUpBase64Alphabet[i] = (byte)(48 + j);
        }
        Base64.lookUpBase64Alphabet[62] = 43;
        Base64.lookUpBase64Alphabet[63] = 47;
    }
}
