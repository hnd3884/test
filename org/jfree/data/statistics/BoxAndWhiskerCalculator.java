package org.jfree.data.statistics;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class BoxAndWhiskerCalculator
{
    public static BoxAndWhiskerItem calculateBoxAndWhiskerStatistics(final List values) {
        Collections.sort((List<Comparable>)values);
        final double mean = Statistics.calculateMean(values);
        final double median = Statistics.calculateMedian(values, false);
        final double q1 = calculateQ1(values);
        final double q2 = calculateQ3(values);
        final double interQuartileRange = q2 - q1;
        final double upperOutlierThreshold = q2 + interQuartileRange * 1.5;
        final double lowerOutlierThreshold = q1 - interQuartileRange * 1.5;
        final double upperFaroutThreshold = q2 + interQuartileRange * 2.0;
        final double lowerFaroutThreshold = q1 - interQuartileRange * 2.0;
        double minRegularValue = Double.POSITIVE_INFINITY;
        double maxRegularValue = Double.NEGATIVE_INFINITY;
        double minOutlier = Double.POSITIVE_INFINITY;
        double maxOutlier = Double.NEGATIVE_INFINITY;
        final List outliers = new ArrayList();
        final Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            final Object object = iterator.next();
            if (object != null && object instanceof Number) {
                final Number number = (Number)object;
                final double value = number.doubleValue();
                if (value > upperOutlierThreshold) {
                    outliers.add(number);
                    if (value <= maxOutlier || value > upperFaroutThreshold) {
                        continue;
                    }
                    maxOutlier = value;
                }
                else if (value < lowerOutlierThreshold) {
                    outliers.add(number);
                    if (value >= minOutlier || value < lowerFaroutThreshold) {
                        continue;
                    }
                    minOutlier = value;
                }
                else {
                    if (minRegularValue == Double.NaN) {
                        minRegularValue = value;
                    }
                    else {
                        minRegularValue = Math.min(minRegularValue, value);
                    }
                    if (maxRegularValue == Double.NaN) {
                        maxRegularValue = value;
                    }
                    else {
                        maxRegularValue = Math.max(maxRegularValue, value);
                    }
                }
            }
        }
        minOutlier = Math.min(minOutlier, minRegularValue);
        maxOutlier = Math.max(maxOutlier, maxRegularValue);
        return new BoxAndWhiskerItem(new Double(mean), new Double(median), new Double(q1), new Double(q2), new Double(minRegularValue), new Double(maxRegularValue), new Double(minOutlier), new Double(maxOutlier), outliers);
    }
    
    public static double calculateQ1(final List values) {
        double result = Double.NaN;
        final int count = values.size();
        if (count > 0) {
            if (count % 2 == 1) {
                if (count > 1) {
                    result = Statistics.calculateMedian(values, 0, count / 2);
                }
                else {
                    result = Statistics.calculateMedian(values, 0, 0);
                }
            }
            else {
                result = Statistics.calculateMedian(values, 0, count / 2 - 1);
            }
        }
        return result;
    }
    
    public static double calculateQ3(final List values) {
        double result = Double.NaN;
        final int count = values.size();
        if (count > 0) {
            if (count % 2 == 1) {
                if (count > 1) {
                    result = Statistics.calculateMedian(values, count / 2, count - 1);
                }
                else {
                    result = Statistics.calculateMedian(values, 0, 0);
                }
            }
            else {
                result = Statistics.calculateMedian(values, count / 2, count - 1);
            }
        }
        return result;
    }
}
