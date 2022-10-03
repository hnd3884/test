package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;

final class Histogram
{
    protected final int[][] matrix;
    protected final int totalWeight;
    protected final int[] values;
    protected final int[] counts;
    private static final long LOW32 = 4294967295L;
    private static double log2;
    private final BitMetric bitMetric;
    
    public Histogram(final int[] array) {
        this.bitMetric = new BitMetric() {
            @Override
            public double getBitLength(final int n) {
                return Histogram.this.getBitLength(n);
            }
        };
        final long[] computeHistogram2Col = computeHistogram2Col(maybeSort(array));
        final int[][] table = makeTable(computeHistogram2Col);
        this.values = table[0];
        this.counts = table[1];
        this.matrix = makeMatrix(computeHistogram2Col);
        this.totalWeight = array.length;
        assert this.assertWellFormed(array);
    }
    
    public Histogram(final int[] array, final int n, final int n2) {
        this(sortedSlice(array, n, n2));
    }
    
    public Histogram(int[][] normalizeMatrix) {
        this.bitMetric = new BitMetric() {
            @Override
            public double getBitLength(final int n) {
                return Histogram.this.getBitLength(n);
            }
        };
        normalizeMatrix = this.normalizeMatrix(normalizeMatrix);
        this.matrix = normalizeMatrix;
        int n = 0;
        int totalWeight = 0;
        for (int i = 0; i < normalizeMatrix.length; ++i) {
            final int n2 = normalizeMatrix[i].length - 1;
            n += n2;
            totalWeight += normalizeMatrix[i][0] * n2;
        }
        this.totalWeight = totalWeight;
        final long[] array = new long[n];
        int n3 = 0;
        for (int j = 0; j < normalizeMatrix.length; ++j) {
            for (int k = 1; k < normalizeMatrix[j].length; ++k) {
                array[n3++] = ((long)normalizeMatrix[j][k] << 32 | (0xFFFFFFFFL & (long)normalizeMatrix[j][0]));
            }
        }
        assert n3 == array.length;
        Arrays.sort(array);
        final int[][] table = makeTable(array);
        this.values = table[1];
        this.counts = table[0];
        assert this.assertWellFormed(null);
    }
    
    public int[][] getMatrix() {
        return this.matrix;
    }
    
    public int getRowCount() {
        return this.matrix.length;
    }
    
    public int getRowFrequency(final int n) {
        return this.matrix[n][0];
    }
    
    public int getRowLength(final int n) {
        return this.matrix[n].length - 1;
    }
    
    public int getRowValue(final int n, final int n2) {
        return this.matrix[n][n2 + 1];
    }
    
    public int getRowWeight(final int n) {
        return this.getRowFrequency(n) * this.getRowLength(n);
    }
    
    public int getTotalWeight() {
        return this.totalWeight;
    }
    
    public int getTotalLength() {
        return this.values.length;
    }
    
    public int[] getAllValues() {
        return this.values;
    }
    
    public int[] getAllFrequencies() {
        return this.counts;
    }
    
    public int getFrequency(final int n) {
        final int binarySearch = Arrays.binarySearch(this.values, n);
        if (binarySearch < 0) {
            return 0;
        }
        assert this.values[binarySearch] == n;
        return this.counts[binarySearch];
    }
    
    public double getBitLength(final int n) {
        return -Math.log(this.getFrequency(n) / (double)this.getTotalWeight()) / Histogram.log2;
    }
    
    public double getRowBitLength(final int n) {
        return -Math.log(this.getRowFrequency(n) / (double)this.getTotalWeight()) / Histogram.log2;
    }
    
    public BitMetric getBitMetric() {
        return this.bitMetric;
    }
    
    public double getBitLength() {
        double n = 0.0;
        for (int i = 0; i < this.matrix.length; ++i) {
            n += this.getRowBitLength(i) * this.getRowWeight(i);
        }
        assert 0.1 > Math.abs(n - this.getBitLength(this.bitMetric));
        return n;
    }
    
    public double getBitLength(final BitMetric bitMetric) {
        double n = 0.0;
        for (int i = 0; i < this.matrix.length; ++i) {
            for (int j = 1; j < this.matrix[i].length; ++j) {
                n += this.matrix[i][0] * bitMetric.getBitLength(this.matrix[i][j]);
            }
        }
        return n;
    }
    
    private static double round(final double n, final double n2) {
        return Math.round(n * n2) / n2;
    }
    
    public int[][] normalizeMatrix(int[][] array) {
        final long[] array2 = new long[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (array[i].length > 1) {
                final int n = array[i][0];
                if (n > 0) {
                    array2[i] = ((long)n << 32 | (long)i);
                }
            }
        }
        Arrays.sort(array2);
        final int[][] array3 = new int[array.length][];
        int n2 = -1;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        while (true) {
            Label_0451: {
                int[] array4;
                if (n5 < array.length) {
                    final long n6 = array2[array2.length - n5 - 1];
                    if (n6 == 0L) {
                        break Label_0451;
                    }
                    array4 = array[(int)n6];
                    assert n6 >>> 32 == array4[0];
                }
                else {
                    array4 = new int[] { -1 };
                }
                if (array4[0] != n2 && n4 > n3) {
                    int n7 = 0;
                    for (int j = n3; j < n4; ++j) {
                        final int[] array5 = array3[j];
                        assert array5[0] == n2;
                        n7 += array5.length - 1;
                    }
                    int[] array6 = new int[1 + n7];
                    array6[0] = n2;
                    int n8 = 1;
                    for (int k = n3; k < n4; ++k) {
                        final int[] array7 = array3[k];
                        assert array7[0] == n2;
                        System.arraycopy(array7, 1, array6, n8, array7.length - 1);
                        n8 += array7.length - 1;
                    }
                    if (!isSorted(array6, 1, true)) {
                        Arrays.sort(array6, 1, array6.length);
                        int n9 = 2;
                        for (int l = 2; l < array6.length; ++l) {
                            if (array6[l] != array6[l - 1]) {
                                array6[n9++] = array6[l];
                            }
                        }
                        if (n9 < array6.length) {
                            final int[] array8 = new int[n9];
                            System.arraycopy(array6, 0, array8, 0, n9);
                            array6 = array8;
                        }
                    }
                    array3[n3++] = array6;
                    n4 = n3;
                }
                if (n5 == array.length) {
                    assert n3 == n4;
                    array = array3;
                    if (n3 < array.length) {
                        final int[][] array9 = new int[n3][];
                        System.arraycopy(array, 0, array9, 0, n3);
                        array = array9;
                    }
                    return array;
                }
                else {
                    n2 = array4[0];
                    array3[n4++] = array4;
                }
            }
            ++n5;
        }
    }
    
    public String[] getRowTitles(final String s) {
        final int totalLength = this.getTotalLength();
        final int totalWeight = this.getTotalWeight();
        final String[] array = new String[this.matrix.length];
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < this.matrix.length; ++i) {
            final int rowFrequency = this.getRowFrequency(i);
            final int rowLength = this.getRowLength(i);
            n += this.getRowWeight(i);
            n2 += rowLength;
            final long n3 = (n * 100L + totalWeight / 2) / totalWeight;
            final long n4 = (n2 * 100L + totalLength / 2) / totalLength;
            final double rowBitLength = this.getRowBitLength(i);
            assert 0.1 > Math.abs(rowBitLength - this.getBitLength(this.matrix[i][1]));
            array[i] = s + "[" + i + "] len=" + round(rowBitLength, 10.0) + " (" + rowFrequency + "*[" + rowLength + "]) (" + n + ":" + n3 + "%) [" + n2 + ":" + n4 + "%]";
        }
        return array;
    }
    
    public void print(final PrintStream printStream) {
        this.print("hist", printStream);
    }
    
    public void print(final String s, final PrintStream printStream) {
        this.print(s, this.getRowTitles(s), printStream);
    }
    
    public void print(final String s, final String[] array, final PrintStream printStream) {
        final int totalLength = this.getTotalLength();
        final int totalWeight = this.getTotalWeight();
        final double bitLength = this.getBitLength();
        final String string = s + " len=" + round(bitLength, 10.0) + " avgLen=" + round(bitLength / totalWeight, 10.0) + " weight(" + totalWeight + ") unique[" + totalLength + "] avgWeight(" + round(totalWeight / (double)totalLength, 100.0) + ")";
        if (array == null) {
            printStream.println(string);
        }
        else {
            printStream.println(string + " {");
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < this.matrix.length; ++i) {
                sb.setLength(0);
                sb.append("  ").append(array[i]).append(" {");
                for (int j = 1; j < this.matrix[i].length; ++j) {
                    sb.append(" ").append(this.matrix[i][j]);
                }
                sb.append(" }");
                printStream.println(sb);
            }
            printStream.println("}");
        }
    }
    
    private static int[][] makeMatrix(final long[] array) {
        Arrays.sort(array);
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = (int)(array[i] >>> 32);
        }
        final long[] computeHistogram2Col = computeHistogram2Col(array2);
        final int[][] array3 = new int[computeHistogram2Col.length][];
        int n = 0;
        int n2 = 0;
        int length = array3.length;
        while (--length >= 0) {
            final long n3 = computeHistogram2Col[n2++];
            final int n4 = (int)n3;
            final int n5 = (int)(n3 >>> 32);
            final int[] array4 = new int[1 + n5];
            array4[0] = n4;
            for (int j = 0; j < n5; ++j) {
                final long n6 = array[n++];
                assert n6 >>> 32 == n4;
                array4[1 + j] = (int)n6;
            }
            array3[length] = array4;
        }
        assert n == array.length;
        return array3;
    }
    
    private static int[][] makeTable(final long[] array) {
        final int[][] array2 = new int[2][array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[0][i] = (int)array[i];
            array2[1][i] = (int)(array[i] >>> 32);
        }
        return array2;
    }
    
    private static long[] computeHistogram2Col(final int[] array) {
        switch (array.length) {
            case 0: {
                return new long[0];
            }
            case 1: {
                return new long[] { 0x100000000L | (0xFFFFFFFFL & (long)array[0]) };
            }
            default: {
                long[] array2 = null;
                int n = 1;
                while (true) {
                    int n2 = -1;
                    int n3 = ~array[0];
                    int n4 = 0;
                    for (int i = 0; i <= array.length; ++i) {
                        int n5;
                        if (i < array.length) {
                            n5 = array[i];
                        }
                        else {
                            n5 = ~n3;
                        }
                        if (n5 == n3) {
                            ++n4;
                        }
                        else {
                            if (n == 0 && n4 != 0) {
                                array2[n2] = ((long)n4 << 32 | (0xFFFFFFFFL & (long)n3));
                            }
                            n3 = n5;
                            n4 = 1;
                            ++n2;
                        }
                    }
                    if (n == 0) {
                        break;
                    }
                    array2 = new long[n2];
                    n = 0;
                }
                return array2;
            }
        }
    }
    
    private static int[][] regroupHistogram(final int[][] array, int[] array2) {
        long n = 0L;
        for (int i = 0; i < array.length; ++i) {
            n += array[i].length - 1;
        }
        long n2 = 0L;
        for (int j = 0; j < array2.length; ++j) {
            n2 += array2[j];
        }
        if (n2 > n) {
            final int length = array2.length;
            long n3 = n;
            for (int k = 0; k < array2.length; ++k) {
                if (n3 < array2[k]) {
                    final int[] array3 = new int[k + 1];
                    System.arraycopy(array2, 0, array3, 0, k + 1);
                    array2 = array3;
                    array2[k] = (int)n3;
                    break;
                }
                n3 -= array2[k];
            }
        }
        else {
            final long n4 = n - n2;
            final int[] array4 = new int[array2.length + 1];
            System.arraycopy(array2, 0, array4, 0, array2.length);
            array4[array2.length] = (int)n4;
            array2 = array4;
        }
        final int[][] array5 = new int[array2.length][];
        int n5 = 0;
        int l = 1;
        int n6 = array[n5].length;
        for (int n7 = 0; n7 < array2.length; ++n7) {
            final int n8 = array2[n7];
            final int[] array6 = new int[1 + n8];
            long n9 = 0L;
            array5[n7] = array6;
            int n11;
            for (int n10 = 1; n10 < array6.length; n10 += n11) {
                n11 = array6.length - n10;
                while (l == n6) {
                    l = 1;
                    n6 = array[++n5].length;
                }
                if (n11 > n6 - l) {
                    n11 = n6 - l;
                }
                n9 += array[n5][0] * (long)n11;
                System.arraycopy(array[n5], n6 - n11, array6, n10, n11);
                n6 -= n11;
            }
            Arrays.sort(array6, 1, array6.length);
            array6[0] = (int)((n9 + n8 / 2) / n8);
        }
        assert l == n6;
        assert n5 == array.length - 1;
        return array5;
    }
    
    public static Histogram makeByteHistogram(final InputStream inputStream) throws IOException {
        final byte[] array = new byte[4096];
        final int[] array2 = new int[256];
        int read;
        while ((read = inputStream.read(array)) > 0) {
            for (int i = 0; i < read; ++i) {
                final int[] array3 = array2;
                final int n = array[i] & 0xFF;
                ++array3[n];
            }
        }
        final int[][] array4 = new int[256][2];
        for (int j = 0; j < array2.length; ++j) {
            array4[j][0] = array2[j];
            array4[j][1] = j;
        }
        return new Histogram(array4);
    }
    
    private static int[] sortedSlice(final int[] array, final int n, final int n2) {
        if (n == 0 && n2 == array.length && isSorted(array, 0, false)) {
            return array;
        }
        final int[] array2 = new int[n2 - n];
        System.arraycopy(array, n, array2, 0, array2.length);
        Arrays.sort(array2);
        return array2;
    }
    
    private static boolean isSorted(final int[] array, final int n, final boolean b) {
        for (int i = n + 1; i < array.length; ++i) {
            if (b) {
                if (array[i - 1] >= array[i]) {
                    return false;
                }
            }
            else if (array[i - 1] > array[i]) {
                return false;
            }
        }
        return true;
    }
    
    private static int[] maybeSort(int[] array) {
        if (!isSorted(array, 0, false)) {
            array = array.clone();
            Arrays.sort(array);
        }
        return array;
    }
    
    private boolean assertWellFormed(final int[] array) {
        return true;
    }
    
    static {
        Histogram.log2 = Math.log(2.0);
    }
    
    public interface BitMetric
    {
        double getBitLength(final int p0);
    }
}
