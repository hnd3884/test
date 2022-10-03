package io.opencensus.metrics.export;

import java.util.List;

final class AutoValue_Metric extends Metric
{
    private final MetricDescriptor metricDescriptor;
    private final List<TimeSeries> timeSeriesList;
    
    AutoValue_Metric(final MetricDescriptor metricDescriptor, final List<TimeSeries> timeSeriesList) {
        if (metricDescriptor == null) {
            throw new NullPointerException("Null metricDescriptor");
        }
        this.metricDescriptor = metricDescriptor;
        if (timeSeriesList == null) {
            throw new NullPointerException("Null timeSeriesList");
        }
        this.timeSeriesList = timeSeriesList;
    }
    
    @Override
    public MetricDescriptor getMetricDescriptor() {
        return this.metricDescriptor;
    }
    
    @Override
    public List<TimeSeries> getTimeSeriesList() {
        return this.timeSeriesList;
    }
    
    @Override
    public String toString() {
        return "Metric{metricDescriptor=" + this.metricDescriptor + ", timeSeriesList=" + this.timeSeriesList + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Metric) {
            final Metric that = (Metric)o;
            return this.metricDescriptor.equals(that.getMetricDescriptor()) && this.timeSeriesList.equals(that.getTimeSeriesList());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.metricDescriptor.hashCode();
        h *= 1000003;
        h ^= this.timeSeriesList.hashCode();
        return h;
    }
}
