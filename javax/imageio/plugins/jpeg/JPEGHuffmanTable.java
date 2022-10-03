package javax.imageio.plugins.jpeg;

import java.util.Arrays;

public class JPEGHuffmanTable
{
    private static final short[] StdDCLuminanceLengths;
    private static final short[] StdDCLuminanceValues;
    private static final short[] StdDCChrominanceLengths;
    private static final short[] StdDCChrominanceValues;
    private static final short[] StdACLuminanceLengths;
    private static final short[] StdACLuminanceValues;
    private static final short[] StdACChrominanceLengths;
    private static final short[] StdACChrominanceValues;
    public static final JPEGHuffmanTable StdDCLuminance;
    public static final JPEGHuffmanTable StdDCChrominance;
    public static final JPEGHuffmanTable StdACLuminance;
    public static final JPEGHuffmanTable StdACChrominance;
    private short[] lengths;
    private short[] values;
    
    public JPEGHuffmanTable(final short[] array, final short[] array2) {
        if (array == null || array2 == null || array.length == 0 || array2.length == 0 || array.length > 16 || array2.length > 256) {
            throw new IllegalArgumentException("Illegal lengths or values");
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] < 0) {
                throw new IllegalArgumentException("lengths[" + i + "] < 0");
            }
        }
        for (int j = 0; j < array2.length; ++j) {
            if (array2[j] < 0) {
                throw new IllegalArgumentException("values[" + j + "] < 0");
            }
        }
        this.lengths = Arrays.copyOf(array, array.length);
        this.values = Arrays.copyOf(array2, array2.length);
        this.validate();
    }
    
    private void validate() {
        int n = 0;
        for (int i = 0; i < this.lengths.length; ++i) {
            n += this.lengths[i];
        }
        if (n != this.values.length) {
            throw new IllegalArgumentException("lengths do not correspond to length of value table");
        }
    }
    
    private JPEGHuffmanTable(final short[] lengths, final short[] values, final boolean b) {
        if (b) {
            this.lengths = Arrays.copyOf(lengths, lengths.length);
            this.values = Arrays.copyOf(values, values.length);
        }
        else {
            this.lengths = lengths;
            this.values = values;
        }
    }
    
    public short[] getLengths() {
        return Arrays.copyOf(this.lengths, this.lengths.length);
    }
    
    public short[] getValues() {
        return Arrays.copyOf(this.values, this.values.length);
    }
    
    @Override
    public String toString() {
        final String property = System.getProperty("line.separator", "\n");
        final StringBuilder sb = new StringBuilder("JPEGHuffmanTable");
        sb.append(property).append("lengths:");
        for (int i = 0; i < this.lengths.length; ++i) {
            sb.append(" ").append(this.lengths[i]);
        }
        sb.append(property).append("values:");
        for (int j = 0; j < this.values.length; ++j) {
            sb.append(" ").append(this.values[j]);
        }
        return sb.toString();
    }
    
    static {
        StdDCLuminanceLengths = new short[] { 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 };
        StdDCLuminanceValues = new short[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
        StdDCChrominanceLengths = new short[] { 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0 };
        StdDCChrominanceValues = new short[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
        StdACLuminanceLengths = new short[] { 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125 };
        StdACLuminanceValues = new short[] { 1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, 129, 145, 161, 8, 35, 66, 177, 193, 21, 82, 209, 240, 36, 51, 98, 114, 130, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147, 148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167, 168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194, 195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214, 215, 216, 217, 218, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250 };
        StdACChrominanceLengths = new short[] { 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119 };
        StdACChrominanceValues = new short[] { 0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, 129, 8, 20, 66, 145, 161, 177, 193, 9, 35, 51, 82, 240, 21, 98, 114, 209, 10, 22, 36, 52, 225, 37, 241, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, 130, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147, 148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167, 168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194, 195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214, 215, 216, 217, 218, 226, 227, 228, 229, 230, 231, 232, 233, 234, 242, 243, 244, 245, 246, 247, 248, 249, 250 };
        StdDCLuminance = new JPEGHuffmanTable(JPEGHuffmanTable.StdDCLuminanceLengths, JPEGHuffmanTable.StdDCLuminanceValues, false);
        StdDCChrominance = new JPEGHuffmanTable(JPEGHuffmanTable.StdDCChrominanceLengths, JPEGHuffmanTable.StdDCChrominanceValues, false);
        StdACLuminance = new JPEGHuffmanTable(JPEGHuffmanTable.StdACLuminanceLengths, JPEGHuffmanTable.StdACLuminanceValues, false);
        StdACChrominance = new JPEGHuffmanTable(JPEGHuffmanTable.StdACChrominanceLengths, JPEGHuffmanTable.StdACChrominanceValues, false);
    }
}
