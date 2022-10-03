package jcifs.dcerpc;

public class UUID extends rpc.uuid_t
{
    static final char[] HEXCHARS;
    
    public static int hex_to_bin(final char[] arr, final int offset, final int length) {
        int value = 0;
        for (int count = 0, ai = offset; ai < arr.length && count < length; ++count, ++ai) {
            value <<= 4;
            switch (arr[ai]) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    value += arr[ai] - '0';
                    break;
                }
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F': {
                    value += 10 + (arr[ai] - 'A');
                    break;
                }
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f': {
                    value += 10 + (arr[ai] - 'a');
                    break;
                }
                default: {
                    throw new IllegalArgumentException(new String(arr, offset, length));
                }
            }
        }
        return value;
    }
    
    public static String bin_to_hex(int value, final int length) {
        final char[] arr = new char[length];
        int ai = arr.length;
        while (ai-- > 0) {
            arr[ai] = UUID.HEXCHARS[value & 0xF];
            value >>>= 4;
        }
        return new String(arr);
    }
    
    private static byte B(final int i) {
        return (byte)(i & 0xFF);
    }
    
    private static short S(final int i) {
        return (short)(i & 0xFFFF);
    }
    
    public UUID(final String str) {
        final char[] arr = str.toCharArray();
        this.time_low = hex_to_bin(arr, 0, 8);
        this.time_mid = S(hex_to_bin(arr, 9, 4));
        this.time_hi_and_version = S(hex_to_bin(arr, 14, 4));
        this.clock_seq_hi_and_reserved = B(hex_to_bin(arr, 19, 2));
        this.clock_seq_low = B(hex_to_bin(arr, 21, 2));
        (this.node = new byte[6])[0] = B(hex_to_bin(arr, 24, 2));
        this.node[1] = B(hex_to_bin(arr, 26, 2));
        this.node[2] = B(hex_to_bin(arr, 28, 2));
        this.node[3] = B(hex_to_bin(arr, 30, 2));
        this.node[4] = B(hex_to_bin(arr, 32, 2));
        this.node[5] = B(hex_to_bin(arr, 34, 2));
    }
    
    public String toString() {
        return bin_to_hex(this.time_low, 8) + '-' + bin_to_hex(this.time_mid, 4) + '-' + bin_to_hex(this.time_hi_and_version, 4) + '-' + bin_to_hex(this.clock_seq_hi_and_reserved, 2) + bin_to_hex(this.clock_seq_low, 2) + '-' + bin_to_hex(this.node[0], 2) + bin_to_hex(this.node[1], 2) + bin_to_hex(this.node[2], 2) + bin_to_hex(this.node[3], 2) + bin_to_hex(this.node[4], 2) + bin_to_hex(this.node[5], 2);
    }
    
    static {
        HEXCHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
