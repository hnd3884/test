package com.azul.crs.shared.models.metrics;

import java.util.Map;
import java.util.Objects;
import java.util.List;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SumUptimeCveMetric extends Metric
{
    private List<SumUptimeCve> sumUptimeCve;
    
    public List<SumUptimeCve> getSumUptimeCve() {
        return this.sumUptimeCve;
    }
    
    public void setSumUptimeCve(final List<SumUptimeCve> sumUptimeCve) {
        this.sumUptimeCve = sumUptimeCve;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SumUptimeCveMetric that = (SumUptimeCveMetric)o;
        return Objects.equals(this.sumUptimeCve, that.sumUptimeCve);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.sumUptimeCve);
    }
    
    @Override
    public String toString() {
        return "SumUptimeCveMetric{sumUptimeCve=" + this.sumUptimeCve + '}';
    }
    
    public static class SumUptimeCve
    {
        private Long unitStart;
        private Map<CveMaxScore, Long> uptimes;
        
        public Long getUnitStart() {
            return this.unitStart;
        }
        
        public void setUnitStart(final Long unitStart) {
            this.unitStart = unitStart;
        }
        
        public Map<CveMaxScore, Long> getUptimes() {
            return this.uptimes;
        }
        
        public void setUptimes(final Map<CveMaxScore, Long> uptimes) {
            this.uptimes = uptimes;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final SumUptimeCve value = (SumUptimeCve)o;
            return Objects.equals(this.unitStart, value.unitStart) && Objects.equals(this.uptimes, value.uptimes);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.unitStart, this.uptimes);
        }
        
        @Override
        public String toString() {
            return "SumUptimeCve{unitStart=" + this.unitStart + ", uptimes=" + this.uptimes + '}';
        }
    }
}
