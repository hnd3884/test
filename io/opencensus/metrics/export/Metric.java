package io.opencensus.metrics.export;

import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import io.opencensus.internal.Utils;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Metric
{
    Metric() {
    }
    
    public static Metric create(final MetricDescriptor metricDescriptor, final List<TimeSeries> timeSeriesList) {
        Utils.checkListElementNotNull((List<Object>)Utils.checkNotNull((List<T>)timeSeriesList, "timeSeriesList"), "timeSeries");
        return createInternal(metricDescriptor, Collections.unmodifiableList((List<? extends TimeSeries>)new ArrayList<TimeSeries>(timeSeriesList)));
    }
    
    public static Metric createWithOneTimeSeries(final MetricDescriptor metricDescriptor, final TimeSeries timeSeries) {
        return createInternal(metricDescriptor, Collections.singletonList((TimeSeries)Utils.checkNotNull((T)timeSeries, "timeSeries")));
    }
    
    private static Metric createInternal(final MetricDescriptor metricDescriptor, final List<TimeSeries> timeSeriesList) {
        Utils.checkNotNull(metricDescriptor, "metricDescriptor");
        checkTypeMatch(metricDescriptor.getType(), timeSeriesList);
        return new AutoValue_Metric(metricDescriptor, timeSeriesList);
    }
    
    public abstract MetricDescriptor getMetricDescriptor();
    
    public abstract List<TimeSeries> getTimeSeriesList();
    
    private static void checkTypeMatch(final MetricDescriptor.Type type, final List<TimeSeries> timeSeriesList) {
        for (final TimeSeries timeSeries : timeSeriesList) {
            for (final Point point : timeSeries.getPoints()) {
                final Value value = point.getValue();
                String valueClassName = "";
                if (value.getClass().getSuperclass() != null) {
                    valueClassName = value.getClass().getSuperclass().getSimpleName();
                }
                switch (type) {
                    case GAUGE_INT64:
                    case CUMULATIVE_INT64: {
                        Utils.checkArgument(value instanceof Value.ValueLong, "Type mismatch: %s, %s.", type, valueClassName);
                        continue;
                    }
                    case CUMULATIVE_DOUBLE:
                    case GAUGE_DOUBLE: {
                        Utils.checkArgument(value instanceof Value.ValueDouble, "Type mismatch: %s, %s.", type, valueClassName);
                        continue;
                    }
                    case GAUGE_DISTRIBUTION:
                    case CUMULATIVE_DISTRIBUTION: {
                        Utils.checkArgument(value instanceof Value.ValueDistribution, "Type mismatch: %s, %s.", type, valueClassName);
                        continue;
                    }
                    case SUMMARY: {
                        Utils.checkArgument(value instanceof Value.ValueSummary, "Type mismatch: %s, %s.", type, valueClassName);
                        continue;
                    }
                }
            }
        }
    }
}
