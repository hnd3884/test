package org.apache.tomcat.util.buf;

public class Asn1Writer
{
    public static byte[] writeSequence(final byte[]... components) {
        int len = 0;
        for (final byte[] component : components) {
            len += component.length;
        }
        final byte[] combined = new byte[len];
        int pos = 0;
        for (final byte[] component2 : components) {
            System.arraycopy(component2, 0, combined, pos, component2.length);
            pos += component2.length;
        }
        return writeTag((byte)48, combined);
    }
    
    public static byte[] writeInteger(int value) {
        int valueSize;
        for (valueSize = 1; value >> valueSize * 8 > 0; ++valueSize) {}
        final byte[] valueBytes = new byte[valueSize];
        for (int i = 0; valueSize > 0; --valueSize, ++i) {
            valueBytes[i] = (byte)(value >> 8 * (valueSize - 1));
            value >>= 8;
        }
        return writeTag((byte)2, valueBytes);
    }
    
    public static byte[] writeOctetString(final byte[] data) {
        return writeTag((byte)4, data);
    }
    
    public static byte[] writeTag(final byte tagId, final byte[] data) {
        int dataSize = data.length;
        int lengthSize = 1;
        if (dataSize > 127) {
            do {
                ++lengthSize;
            } while (dataSize >> lengthSize * 8 > 0);
        }
        final byte[] result = new byte[1 + lengthSize + dataSize];
        result[0] = tagId;
        if (dataSize < 128) {
            result[1] = (byte)dataSize;
        }
        else {
            result[1] = (byte)(127 + lengthSize);
            for (int i = lengthSize; dataSize > 0; dataSize >>= 8, --i) {
                result[i] = (byte)(dataSize & 0xFF);
            }
        }
        System.arraycopy(data, 0, result, 1 + lengthSize, data.length);
        return result;
    }
}
