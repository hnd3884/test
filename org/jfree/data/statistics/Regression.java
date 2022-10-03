package org.jfree.data.statistics;

import org.jfree.data.xy.XYDataset;

public abstract class Regression
{
    public static double[] getOLSRegression(final double[][] data) {
        final int n = data.length;
        if (n < 2) {
            throw new IllegalArgumentException("Not enough data.");
        }
        double sumX = 0.0;
        double sumY = 0.0;
        double sumXX = 0.0;
        double sumXY = 0.0;
        for (int i = 0; i < n; ++i) {
            final double x = data[i][0];
            final double y = data[i][1];
            sumX += x;
            sumY += y;
            final double xx = x * x;
            sumXX += xx;
            final double xy = x * y;
            sumXY += xy;
        }
        final double sxx = sumXX - sumX * sumX / n;
        final double sxy = sumXY - sumX * sumY / n;
        final double xbar = sumX / n;
        final double ybar = sumY / n;
        final double[] result = { 0.0, sxy / sxx };
        result[0] = ybar - result[1] * xbar;
        return result;
    }
    
    public static double[] getOLSRegression(final XYDataset data, final int series) {
        final int n = data.getItemCount(series);
        if (n < 2) {
            throw new IllegalArgumentException("Not enough data.");
        }
        double sumX = 0.0;
        double sumY = 0.0;
        double sumXX = 0.0;
        double sumXY = 0.0;
        for (int i = 0; i < n; ++i) {
            final double x = data.getXValue(series, i);
            final double y = data.getYValue(series, i);
            sumX += x;
            sumY += y;
            final double xx = x * x;
            sumXX += xx;
            final double xy = x * y;
            sumXY += xy;
        }
        final double sxx = sumXX - sumX * sumX / n;
        final double sxy = sumXY - sumX * sumY / n;
        final double xbar = sumX / n;
        final double ybar = sumY / n;
        final double[] result = { 0.0, sxy / sxx };
        result[0] = ybar - result[1] * xbar;
        return result;
    }
    
    public static double[] getPowerRegression(final double[][] data) {
        final int n = data.length;
        if (n < 2) {
            throw new IllegalArgumentException("Not enough data.");
        }
        double sumX = 0.0;
        double sumY = 0.0;
        double sumXX = 0.0;
        double sumXY = 0.0;
        for (int i = 0; i < n; ++i) {
            final double x = Math.log(data[i][0]);
            final double y = Math.log(data[i][1]);
            sumX += x;
            sumY += y;
            final double xx = x * x;
            sumXX += xx;
            final double xy = x * y;
            sumXY += xy;
        }
        final double sxx = sumXX - sumX * sumX / n;
        final double sxy = sumXY - sumX * sumY / n;
        final double xbar = sumX / n;
        final double ybar = sumY / n;
        final double[] result = { 0.0, sxy / sxx };
        result[0] = Math.pow(Math.exp(1.0), ybar - result[1] * xbar);
        return result;
    }
    
    public static double[] getPowerRegression(final XYDataset data, final int series) {
        final int n = data.getItemCount(series);
        if (n < 2) {
            throw new IllegalArgumentException("Not enough data.");
        }
        double sumX = 0.0;
        double sumY = 0.0;
        double sumXX = 0.0;
        double sumXY = 0.0;
        for (int i = 0; i < n; ++i) {
            final double x = Math.log(data.getXValue(series, i));
            final double y = Math.log(data.getYValue(series, i));
            sumX += x;
            sumY += y;
            final double xx = x * x;
            sumXX += xx;
            final double xy = x * y;
            sumXY += xy;
        }
        final double sxx = sumXX - sumX * sumX / n;
        final double sxy = sumXY - sumX * sumY / n;
        final double xbar = sumX / n;
        final double ybar = sumY / n;
        final double[] result = { 0.0, sxy / sxx };
        result[0] = Math.pow(Math.exp(1.0), ybar - result[1] * xbar);
        return result;
    }
}
