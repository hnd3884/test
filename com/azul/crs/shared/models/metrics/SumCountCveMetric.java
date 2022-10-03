package com.azul.crs.shared.models.metrics;

import java.util.Map;
import java.util.Objects;
import java.util.List;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SumCountCveMetric extends Metric
{
    private List<SumCountCve> sumCountCve;
    
    public List<SumCountCve> getSumCountCve() {
        return this.sumCountCve;
    }
    
    public void setSumCountCve(final List<SumCountCve> sumCountCve) {
        this.sumCountCve = sumCountCve;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SumCountCveMetric that = (SumCountCveMetric)o;
        return Objects.equals(this.sumCountCve, that.sumCountCve);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.sumCountCve);
    }
    
    @Override
    public String toString() {
        return "SumCountCveMetric{sumCountCve=" + this.sumCountCve + '}';
    }
    
    public static class SumCountCve
    {
        private Long unitStart;
        private Map<CveMaxScore, Long> counts;
        
        public Long getUnitStart() {
            return this.unitStart;
        }
        
        public void setUnitStart(final Long unitStart) {
            this.unitStart = unitStart;
        }
        
        public Map<CveMaxScore, Long> getCounts() {
            return this.counts;
        }
        
        public void setCounts(final Map<CveMaxScore, Long> counts) {
            this.counts = counts;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final SumCountCve value = (SumCountCve)o;
            return Objects.equals(this.unitStart, value.unitStart) && Objects.equals(this.counts, value.counts);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.unitStart, this.counts);
        }
        
        @Override
        public String toString() {
            return "SumCountCve{unitStart=" + this.unitStart + ", counts=" + this.counts + '}';
        }
    }
}
