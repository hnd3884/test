package com.azul.crs.shared.models.metrics;

import com.azul.crs.shared.models.CVEImpact;
import java.util.Objects;
import java.util.List;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupComponentsCveMetric extends Metric
{
    private List<Group> groups;
    
    public List<Group> getGroups() {
        return this.groups;
    }
    
    public void setGroups(final List<Group> groups) {
        this.groups = groups;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final GroupComponentsCveMetric that = (GroupComponentsCveMetric)o;
        return Objects.equals(this.groups, that.groups);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.groups);
    }
    
    @Override
    public String toString() {
        return "GroupComponentsCveMetric{groups=" + this.groups + '}';
    }
    
    public static class Group
    {
        private Object groupKey;
        private List<Value> groupValues;
        
        public Object getGroupKey() {
            return this.groupKey;
        }
        
        public void setGroupKey(final Object groupKey) {
            this.groupKey = groupKey;
        }
        
        public List<Value> getGroupValues() {
            return this.groupValues;
        }
        
        public void setGroupValues(final List<Value> groupValues) {
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
            final Group group = (Group)o;
            return Objects.equals(this.groupKey, group.groupKey) && Objects.equals(this.groupValues, group.groupValues);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.groupKey, this.groupValues);
        }
        
        @Override
        public String toString() {
            return "Group{groupKey=" + this.groupKey + ", groupValues=" + this.groupValues + '}';
        }
        
        public static class Value
        {
            private String componentName;
            private List<CVEImpact.CVE> cves;
            
            public String getComponentName() {
                return this.componentName;
            }
            
            public void setComponentName(final String componentName) {
                this.componentName = componentName;
            }
            
            public List<CVEImpact.CVE> getCves() {
                return this.cves;
            }
            
            public void setCves(final List<CVEImpact.CVE> cves) {
                this.cves = cves;
            }
            
            @Override
            public boolean equals(final Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                final Value value = (Value)o;
                return Objects.equals(this.componentName, value.componentName) && Objects.equals(this.cves, value.cves);
            }
            
            @Override
            public int hashCode() {
                return Objects.hash(this.componentName, this.cves);
            }
            
            @Override
            public String toString() {
                return "Value{componentName='" + this.componentName + '\'' + ", cves=" + this.cves + '}';
            }
        }
    }
}
