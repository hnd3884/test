package org.jfree.data;

public abstract class DataUtilities
{
    public static double calculateColumnTotal(final Values2D data, final int column) {
        double total = 0.0;
        for (int rowCount = data.getRowCount(), r = 0; r < rowCount; ++r) {
            final Number n = data.getValue(r, column);
            if (n != null) {
                total += n.doubleValue();
            }
        }
        return total;
    }
    
    public static double calculateRowTotal(final Values2D data, final int row) {
        double total = 0.0;
        for (int columnCount = data.getColumnCount(), c = 0; c < columnCount; ++c) {
            final Number n = data.getValue(row, c);
            if (n != null) {
                total += n.doubleValue();
            }
        }
        return total;
    }
    
    public static Number[] createNumberArray(final double[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        final Number[] result = new Number[data.length];
        for (int i = 0; i < data.length; ++i) {
            result[i] = new Double(data[i]);
        }
        return result;
    }
    
    public static Number[][] createNumberArray2D(final double[][] data) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        final int l1 = data.length;
        final Number[][] result = new Number[l1][];
        for (int i = 0; i < l1; ++i) {
            result[i] = createNumberArray(data[i]);
        }
        return result;
    }
    
    public static KeyedValues getCumulativePercentages(final KeyedValues data) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        final DefaultKeyedValues result = new DefaultKeyedValues();
        double total = 0.0;
        for (int i = 0; i < data.getItemCount(); ++i) {
            final Number v = data.getValue(i);
            if (v != null) {
                total += v.doubleValue();
            }
        }
        double runningTotal = 0.0;
        for (int j = 0; j < data.getItemCount(); ++j) {
            final Number v2 = data.getValue(j);
            if (v2 != null) {
                runningTotal += v2.doubleValue();
            }
            result.addValue(data.getKey(j), new Double(runningTotal / total));
        }
        return result;
    }
}
