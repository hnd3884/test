package org.jfree.data.statistics;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;

public abstract class Statistics
{
    public static double calculateMean(final Number[] values) {
        double result = Double.NaN;
        if (values != null && values.length > 0) {
            double sum = 0.0;
            int counter;
            for (counter = 0; counter < values.length; ++counter) {
                sum += values[counter].doubleValue();
            }
            result = sum / counter;
        }
        return result;
    }
    
    public static double calculateMean(final Collection values) {
        double result = Double.NaN;
        int count = 0;
        double total = 0.0;
        final Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object != null && object instanceof Number) {
                final Number number = (Number)object;
                total += number.doubleValue();
                ++count;
            }
        }
        if (count > 0) {
            result = total / count;
        }
        return result;
    }
    
    public static double calculateMedian(final List values) {
        return calculateMedian(values, true);
    }
    
    public static double calculateMedian(List values, final boolean copyAndSort) {
        double result = Double.NaN;
        if (values != null) {
            if (copyAndSort) {
                final int itemCount = values.size();
                final List copy = new ArrayList(itemCount);
                for (int i = 0; i < itemCount; ++i) {
                    copy.add(i, values.get(i));
                }
                Collections.sort((List<Comparable>)copy);
                values = copy;
            }
            final int count = values.size();
            if (count > 0) {
                if (count % 2 == 1) {
                    if (count > 1) {
                        final Number value = (Number)values.get((count - 1) / 2);
                        result = value.doubleValue();
                    }
                    else {
                        final Number value = (Number)values.get(0);
                        result = value.doubleValue();
                    }
                }
                else {
                    final Number value2 = (Number)values.get(count / 2 - 1);
                    final Number value3 = (Number)values.get(count / 2);
                    result = (value2.doubleValue() + value3.doubleValue()) / 2.0;
                }
            }
        }
        return result;
    }
    
    public static double calculateMedian(final List values, final int start, final int end) {
        return calculateMedian(values, start, end, true);
    }
    
    public static double calculateMedian(final List values, final int start, final int end, final boolean copyAndSort) {
        double result = Double.NaN;
        if (copyAndSort) {
            final List working = new ArrayList(end - start + 1);
            for (int i = start; i <= end; ++i) {
                working.add(values.get(i));
            }
            Collections.sort((List<Comparable>)working);
            result = calculateMedian(working, false);
        }
        else {
            final int count = end - start + 1;
            if (count > 0) {
                if (count % 2 == 1) {
                    if (count > 1) {
                        final Number value = values.get(start + (count - 1) / 2);
                        result = value.doubleValue();
                    }
                    else {
                        final Number value = values.get(start);
                        result = value.doubleValue();
                    }
                }
                else {
                    final Number value2 = values.get(start + count / 2 - 1);
                    final Number value3 = values.get(start + count / 2);
                    result = (value2.doubleValue() + value3.doubleValue()) / 2.0;
                }
            }
        }
        return result;
    }
    
    public static double getStdDev(final Number[] data) {
        final double avg = calculateMean(data);
        double sum = 0.0;
        for (int counter = 0; counter < data.length; ++counter) {
            final double diff = data[counter].doubleValue() - avg;
            sum += diff * diff;
        }
        return Math.sqrt(sum / (data.length - 1));
    }
    
    public static double[] getLinearFit(final Number[] xData, final Number[] yData) {
        if (xData.length != yData.length) {
            throw new IllegalArgumentException("Statistics.getLinearFit(): array lengths must be equal.");
        }
        final double[] result = { 0.0, getSlope(xData, yData) };
        result[0] = calculateMean(yData) - result[1] * calculateMean(xData);
        return result;
    }
    
    public static double getSlope(final Number[] xData, final Number[] yData) {
        if (xData.length != yData.length) {
            throw new IllegalArgumentException("Array lengths must be equal.");
        }
        double sx = 0.0;
        double sxx = 0.0;
        double sxy = 0.0;
        double sy = 0.0;
        int counter;
        for (counter = 0; counter < xData.length; ++counter) {
            sx += xData[counter].doubleValue();
            sxx += Math.pow(xData[counter].doubleValue(), 2.0);
            sxy += yData[counter].doubleValue() * xData[counter].doubleValue();
            sy += yData[counter].doubleValue();
        }
        return (sxy - sx * sy / counter) / (sxx - sx * sx / counter);
    }
    
    public static double getCorrelation(final Number[] data1, final Number[] data2) {
        if (data1 == null) {
            throw new IllegalArgumentException("Null 'data1' argument.");
        }
        if (data2 == null) {
            throw new IllegalArgumentException("Null 'data2' argument.");
        }
        if (data1.length != data2.length) {
            throw new IllegalArgumentException("'data1' and 'data2' arrays must have same length.");
        }
        final int n = data1.length;
        double sumX = 0.0;
        double sumY = 0.0;
        double sumX2 = 0.0;
        double sumY2 = 0.0;
        double sumXY = 0.0;
        for (int i = 0; i < n; ++i) {
            double x = 0.0;
            if (data1[i] != null) {
                x = data1[i].doubleValue();
            }
            double y = 0.0;
            if (data2[i] != null) {
                y = data2[i].doubleValue();
            }
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
            sumY2 += y * y;
        }
        return (n * sumXY - sumX * sumY) / Math.pow((n * sumX2 - sumX * sumX) * (n * sumY2 - sumY * sumY), 0.5);
    }
    
    public static double[][] getMovingAverage(final Number[] xData, final Number[] yData, final int period) {
        if (xData.length != yData.length) {
            throw new IllegalArgumentException("Array lengths must be equal.");
        }
        if (period > xData.length) {
            throw new IllegalArgumentException("Period can't be longer than dataset.");
        }
        final double[][] result = new double[xData.length - period][2];
        for (int i = 0; i < result.length; ++i) {
            result[i][0] = xData[i + period].doubleValue();
            double sum = 0.0;
            for (int j = 0; j < period; ++j) {
                sum += yData[i + j].doubleValue();
            }
            sum /= period;
            result[i][1] = sum;
        }
        return result;
    }
}
