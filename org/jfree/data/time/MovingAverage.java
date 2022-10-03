package org.jfree.data.time;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class MovingAverage
{
    public static TimeSeriesCollection createMovingAverage(final TimeSeriesCollection source, final String suffix, final int periodCount, final int skip) {
        if (source == null) {
            throw new IllegalArgumentException("MovingAverage.createMovingAverage() : null source.");
        }
        if (periodCount < 1) {
            throw new IllegalArgumentException("periodCount must be greater than or equal to 1.");
        }
        final TimeSeriesCollection result = new TimeSeriesCollection();
        for (int i = 0; i < source.getSeriesCount(); ++i) {
            final TimeSeries sourceSeries = source.getSeries(i);
            final TimeSeries maSeries = createMovingAverage(sourceSeries, sourceSeries.getKey() + suffix, periodCount, skip);
            result.addSeries(maSeries);
        }
        return result;
    }
    
    public static TimeSeries createMovingAverage(final TimeSeries source, final String name, final int periodCount, final int skip) {
        if (source == null) {
            throw new IllegalArgumentException("Null source.");
        }
        if (periodCount < 1) {
            throw new IllegalArgumentException("periodCount must be greater than or equal to 1.");
        }
        final TimeSeries result = new TimeSeries(name, source.getTimePeriodClass());
        if (source.getItemCount() > 0) {
            final long firstSerial = source.getDataItem(0).getPeriod().getSerialIndex() + skip;
            for (int i = source.getItemCount() - 1; i >= 0; --i) {
                final TimeSeriesDataItem current = source.getDataItem(i);
                final RegularTimePeriod period = current.getPeriod();
                final long serial = period.getSerialIndex();
                if (serial >= firstSerial) {
                    int n = 0;
                    double sum = 0.0;
                    final long serialLimit = period.getSerialIndex() - periodCount;
                    int offset = 0;
                    for (boolean finished = false; offset < periodCount && !finished; ++offset) {
                        if (i - offset >= 0) {
                            final TimeSeriesDataItem item = source.getDataItem(i - offset);
                            final RegularTimePeriod p = item.getPeriod();
                            final Number v = item.getValue();
                            final long currentIndex = p.getSerialIndex();
                            if (currentIndex > serialLimit) {
                                if (v != null) {
                                    sum += v.doubleValue();
                                    ++n;
                                }
                            }
                            else {
                                finished = true;
                            }
                        }
                    }
                    if (n > 0) {
                        result.add(period, sum / n);
                    }
                    else {
                        result.add(period, null);
                    }
                }
            }
        }
        return result;
    }
    
    public static TimeSeries createPointMovingAverage(final TimeSeries source, final String name, final int pointCount) {
        if (source == null) {
            throw new IllegalArgumentException("Null 'source'.");
        }
        if (pointCount < 2) {
            throw new IllegalArgumentException("periodCount must be greater than or equal to 2.");
        }
        final TimeSeries result = new TimeSeries(name, source.getTimePeriodClass());
        double rollingSumForPeriod = 0.0;
        for (int i = 0; i < source.getItemCount(); ++i) {
            final TimeSeriesDataItem current = source.getDataItem(i);
            final RegularTimePeriod period = current.getPeriod();
            rollingSumForPeriod += current.getValue().doubleValue();
            if (i > pointCount - 1) {
                final TimeSeriesDataItem startOfMovingAvg = source.getDataItem(i - pointCount);
                rollingSumForPeriod -= startOfMovingAvg.getValue().doubleValue();
                result.add(period, rollingSumForPeriod / pointCount);
            }
            else if (i == pointCount - 1) {
                result.add(period, rollingSumForPeriod / pointCount);
            }
        }
        return result;
    }
    
    public static XYDataset createMovingAverage(final XYDataset source, final String suffix, final long period, final long skip) {
        return createMovingAverage(source, suffix, (double)period, (double)skip);
    }
    
    public static XYDataset createMovingAverage(final XYDataset source, final String suffix, final double period, final double skip) {
        if (source == null) {
            throw new IllegalArgumentException("Null source (XYDataset).");
        }
        final XYSeriesCollection result = new XYSeriesCollection();
        for (int i = 0; i < source.getSeriesCount(); ++i) {
            final XYSeries s = createMovingAverage(source, i, source.getSeriesKey(i) + suffix, period, skip);
            result.addSeries(s);
        }
        return result;
    }
    
    public static XYSeries createMovingAverage(final XYDataset source, final int series, final String name, final double period, final double skip) {
        if (source == null) {
            throw new IllegalArgumentException("Null source (XYDataset).");
        }
        if (period < Double.MIN_VALUE) {
            throw new IllegalArgumentException("period must be positive.");
        }
        if (skip < 0.0) {
            throw new IllegalArgumentException("skip must be >= 0.0.");
        }
        final XYSeries result = new XYSeries(name);
        if (source.getItemCount(series) > 0) {
            final double first = source.getXValue(series, 0) + skip;
            for (int i = source.getItemCount(series) - 1; i >= 0; --i) {
                final double x = source.getXValue(series, i);
                if (x >= first) {
                    int n = 0;
                    double sum = 0.0;
                    final double limit = x - period;
                    int offset = 0;
                    boolean finished = false;
                    while (!finished) {
                        if (i - offset >= 0) {
                            final double xx = source.getXValue(series, i - offset);
                            final Number yy = source.getY(series, i - offset);
                            if (xx > limit) {
                                if (yy != null) {
                                    sum += yy.doubleValue();
                                    ++n;
                                }
                            }
                            else {
                                finished = true;
                            }
                        }
                        else {
                            finished = true;
                        }
                        ++offset;
                    }
                    if (n > 0) {
                        result.add(x, sum / n);
                    }
                    else {
                        result.add(x, null);
                    }
                }
            }
        }
        return result;
    }
}
