package com.zoho.security.attackdiscovery;

import java.util.Map;
import com.zoho.security.instrumentation.WAFInstrumentClass;

public abstract class AttackDiscovery implements WAFInstrumentClass
{
    protected static final String MONITORING_CONDITION_PARAM_NAME = "MONITORING_CONDITION";
    private final AttackDiscoveryMetric metric;
    
    public AttackDiscovery() {
        this(null);
    }
    
    protected AttackDiscovery(final AttackDiscoveryMetric metric) {
        this.metric = metric;
    }
    
    public final void call(final String monitoringClass, final String monitoringMethod, final Map<String, Object> params) {
        if (this.metric != null) {
            final long startTime = System.currentTimeMillis();
            boolean discovered = false;
            if (this.matchesCondition(params)) {
                params.put("MONITORING_CONDITION", this.getMatchedCondition());
                discovered = true;
                this.doAction(params);
            }
            this.metric.record(this.getClass(), monitoringClass, monitoringMethod, System.currentTimeMillis() - startTime, discovered);
        }
        else if (this.matchesCondition(params)) {
            params.put("MONITORING_CONDITION", this.getMatchedCondition());
            this.doAction(params);
        }
    }
    
    protected abstract boolean matchesCondition(final Map<String, Object> p0);
    
    protected abstract void doAction(final Map<String, Object> p0);
    
    protected abstract String getMatchedCondition();
}
