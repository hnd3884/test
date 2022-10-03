package com.azul.crs.shared.models.metrics;

import java.util.Map;
import java.util.Objects;
import java.util.List;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupUptimeCveMetric extends Metric
{
    private List<GroupUptimeCve> groupUptimeCve;
    
    public List<GroupUptimeCve> getGroupUptimeCve() {
        return this.groupUptimeCve;
    }
    
    public void setGroupUptimeCve(final List<GroupUptimeCve> groupUptimeCve) {
        this.groupUptimeCve = groupUptimeCve;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final GroupUptimeCveMetric metric = (GroupUptimeCveMetric)o;
        return Objects.equals(this.groupUptimeCve, metric.groupUptimeCve);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.groupUptimeCve);
    }
    
    @Override
    public String toString() {
        return "GroupUptimeCveMetric{groupUptimeCve=" + this.groupUptimeCve + '}';
    }
    
    public static class GroupUptimeCve
    {
        private Object groupKey;
        private List<GroupValue> groupValues;
        
        public Object getGroupKey() {
            return this.groupKey;
        }
        
        public void setGroupKey(final Object groupKey) {
            this.groupKey = groupKey;
        }
        
        public List<GroupValue> getGroupValues() {
            return this.groupValues;
        }
        
        public void setGroupValues(final List<GroupValue> groupValues) {
            this.groupValues = groupValues;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final GroupUptimeCve that = (GroupUptimeCve)o;
            return Objects.equals(this.groupKey, that.groupKey) && Objects.equals(this.groupValues, that.groupValues);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.groupKey, this.groupValues);
        }
        
        @Override
        public String toString() {
            return "GroupUptimeCve{groupKey=" + this.groupKey + ", groupValues=" + this.groupValues + '}';
        }
        
        public static class GroupValue
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
                final GroupValue value = (GroupValue)o;
                return Objects.equals(this.unitStart, value.unitStart) && Objects.equals(this.uptimes, value.uptimes);
            }
            
            @Override
            public int hashCode() {
                return Objects.hash(this.unitStart, this.uptimes);
            }
            
            @Override
            public String toString() {
                return "GroupValue{unitStart=" + this.unitStart + ", uptimes=" + this.uptimes + '}';
            }
        }
    }
}
