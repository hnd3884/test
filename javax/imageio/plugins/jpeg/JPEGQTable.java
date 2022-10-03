package javax.imageio.plugins.jpeg;

import java.util.Arrays;

public class JPEGQTable
{
    private static final int[] k1;
    private static final int[] k1div2;
    private static final int[] k2;
    private static final int[] k2div2;
    public static final JPEGQTable K1Luminance;
    public static final JPEGQTable K1Div2Luminance;
    public static final JPEGQTable K2Chrominance;
    public static final JPEGQTable K2Div2Chrominance;
    private int[] qTable;
    
    private JPEGQTable(final int[] array, final boolean b) {
        this.qTable = (b ? Arrays.copyOf(array, array.length) : array);
    }
    
    public JPEGQTable(final int[] array) {
        if (array == null) {
            throw new IllegalArgumentException("table must not be null.");
        }
        if (array.length != 64) {
            throw new IllegalArgumentException("table.length != 64");
        }
        this.qTable = Arrays.copyOf(array, array.length);
    }
    
    public int[] getTable() {
        return Arrays.copyOf(this.qTable, this.qTable.length);
    }
    
    public JPEGQTable getScaledInstance(final float n, final boolean b) {
        final int n2 = b ? 255 : 32767;
        final int[] array = new int[this.qTable.length];
        for (int i = 0; i < this.qTable.length; ++i) {
            int n3 = (int)(this.qTable[i] * n + 0.5f);
            if (n3 < 1) {
                n3 = 1;
            }
            if (n3 > n2) {
                n3 = n2;
            }
            array[i] = n3;
        }
        return new JPEGQTable(array);
    }
    
    @Override
    public String toString() {
        final String property = System.getProperty("line.separator", "\n");
        final StringBuilder sb = new StringBuilder("JPEGQTable:" + property);
        for (int i = 0; i < this.qTable.length; ++i) {
            if (i % 8 == 0) {
                sb.append('\t');
            }
            sb.append(this.qTable[i]);
            sb.append((i % 8 == 7) ? property : Character.valueOf(' '));
        }
        return sb.toString();
    }
    
    static {
        k1 = new int[] { 16, 11, 10, 16, 24, 40, 51, 61, 12, 12, 14, 19, 26, 58, 60, 55, 14, 13, 16, 24, 40, 57, 69, 56, 14, 17, 22, 29, 51, 87, 80, 62, 18, 22, 37, 56, 68, 109, 103, 77, 24, 35, 55, 64, 81, 104, 113, 92, 49, 64, 78, 87, 103, 121, 120, 101, 72, 92, 95, 98, 112, 100, 103, 99 };
        k1div2 = new int[] { 8, 6, 5, 8, 12, 20, 26, 31, 6, 6, 7, 10, 13, 29, 30, 28, 7, 7, 8, 12, 20, 29, 35, 28, 7, 9, 11, 15, 26, 44, 40, 31, 9, 11, 19, 28, 34, 55, 52, 39, 12, 18, 28, 32, 41, 52, 57, 46, 25, 32, 39, 44, 52, 61, 60, 51, 36, 46, 48, 49, 56, 50, 52, 50 };
        k2 = new int[] { 17, 18, 24, 47, 99, 99, 99, 99, 18, 21, 26, 66, 99, 99, 99, 99, 24, 26, 56, 99, 99, 99, 99, 99, 47, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99 };
        k2div2 = new int[] { 9, 9, 12, 24, 50, 50, 50, 50, 9, 11, 13, 33, 50, 50, 50, 50, 12, 13, 28, 50, 50, 50, 50, 50, 24, 33, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50 };
        K1Luminance = new JPEGQTable(JPEGQTable.k1, false);
        K1Div2Luminance = new JPEGQTable(JPEGQTable.k1div2, false);
        K2Chrominance = new JPEGQTable(JPEGQTable.k2, false);
        K2Div2Chrominance = new JPEGQTable(JPEGQTable.k2div2, false);
    }
}
