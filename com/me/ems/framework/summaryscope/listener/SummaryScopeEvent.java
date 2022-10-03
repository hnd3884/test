package com.me.ems.framework.summaryscope.listener;

import java.util.Properties;
import java.util.List;

public class SummaryScopeEvent
{
    private Long summaryScopeID;
    private Integer summaryScopeType;
    private List<Long> valueID;
    private Properties scopeProps;
    
    public SummaryScopeEvent(final Long summaryScopeID, final Integer summaryScopeType, final List<Long> valueID) {
        this.summaryScopeID = summaryScopeID;
        this.summaryScopeType = summaryScopeType;
        this.valueID = valueID;
    }
    
    public SummaryScopeEvent(final Long summaryScopeID, final Integer summaryScopeType, final List<Long> valueID, final Properties scopeProps) {
        this.summaryScopeID = summaryScopeID;
        this.summaryScopeType = summaryScopeType;
        this.valueID = valueID;
        this.scopeProps = scopeProps;
    }
    
    public Long getSummaryScopeID() {
        return this.summaryScopeID;
    }
    
    public void setSummaryScopeID(final Long summaryScopeID) {
        this.summaryScopeID = summaryScopeID;
    }
    
    public Integer getSummaryScopeType() {
        return this.summaryScopeType;
    }
    
    public void setSummaryScopeType(final Integer summaryScopeType) {
        this.summaryScopeType = summaryScopeType;
    }
    
    public List<Long> getValueID() {
        return this.valueID;
    }
    
    public void setValueID(final List<Long> valueID) {
        this.valueID = valueID;
    }
    
    public Properties getScopeProps() {
        return this.scopeProps;
    }
    
    public void setScopeProps(final Properties scopeProps) {
        this.scopeProps = scopeProps;
    }
}
