package org.owasp.esapi.util;

public class ByteConversionUtil
{
    public static byte[] fromShort(final short input) {
        final byte[] output = { (byte)(input >> 8), (byte)input };
        return output;
    }
    
    public static byte[] fromInt(final int input) {
        final byte[] output = { (byte)(input >> 24), (byte)(input >> 16), (byte)(input >> 8), (byte)input };
        return output;
    }
    
    public static byte[] fromLong(final long input) {
        final byte[] output = { (byte)(input >> 56), (byte)(input >> 48), (byte)(input >> 40), (byte)(input >> 32), (byte)(input >> 24), (byte)(input >> 16), (byte)(input >> 8), (byte)input };
        return output;
    }
    
    public static short toShort(final byte[] input) {
        assert input.length == 2 : "toShort(): Byte array length must be 2.";
        short output = 0;
        output = (short)((input[0] & 0xFF) << 8 | (input[1] & 0xFF));
        return output;
    }
    
    public static int toInt(final byte[] input) {
        assert input.length == 4 : "toInt(): Byte array length must be 4.";
        int output = 0;
        output = ((input[0] & 0xFF) << 24 | (input[1] & 0xFF) << 16 | (input[2] & 0xFF) << 8 | (input[3] & 0xFF));
        return output;
    }
    
    public static long toLong(final byte[] input) {
        assert input.length == 8 : "toLong(): Byte array length must be 8.";
        long output = 0L;
        output = (long)(input[0] & 0xFF) << 56;
        output |= (long)(input[1] & 0xFF) << 48;
        output |= (long)(input[2] & 0xFF) << 40;
        output |= (long)(input[3] & 0xFF) << 32;
        output |= (long)(input[4] & 0xFF) << 24;
        output |= (long)(input[5] & 0xFF) << 16;
        output |= (long)(input[6] & 0xFF) << 8;
        output |= (input[7] & 0xFF);
        return output;
    }
}
