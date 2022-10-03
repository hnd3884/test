package com.azul.crs.shared.models;

import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

public class CVEImpactSummary
{
    private Map<Metric, Integer> metrics;
    
    public CVEImpactSummary() {
        this.metrics = new HashMap<Metric, Integer>();
    }
    
    public CVEImpactSummary put(final Metric metric, final Integer value) {
        this.metrics.put(metric, value);
        return this;
    }
    
    public CVEImpactSummary put(final String metric, final Integer value) {
        return this.put(Metric.valueOf(metric), value);
    }
    
    public Integer get(final Metric metric) {
        return this.metrics.get(metric);
    }
    
    public Integer get(final String metric) {
        return this.metrics.get(Metric.valueOf(metric));
    }
    
    @Override
    public String toString() {
        return this.metrics.toString();
    }
    
    public Map<String, Object> toMap() {
        return this.metrics.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().name(), e -> e.getValue()));
    }
    
    public enum Metric
    {
        impacted, 
        analyzed, 
        critical, 
        medium, 
        high, 
        low;
    }
}
