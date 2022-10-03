package com.zoho.security.wafad.instrument;

import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.EventDataProcessor;
import java.util.Map;
import com.zoho.security.wafad.WAFAttackDiscoveryMetricRecorder;
import com.zoho.security.eventfw.EventCallerInferrer;
import com.zoho.security.attackdiscovery.AttackDiscovery;

public abstract class WAFAttackDiscoveryEventPush extends AttackDiscovery implements EventCallerInferrer.CalleeInfoStackWalk
{
    private static final String INSTRUMENT_LOOKUP_CLASS_NAME = "com.zoho.security.instrumentation.WAFInstrumentClassLookup";
    private final WAFAttackDiscoveryEvent attackDiscoveryEvent;
    
    public WAFAttackDiscoveryEventPush(final WAFAttackDiscoveryEvent attackDiscoveryEvent) {
        super(WAFAttackDiscoveryMetricRecorder.wafAttackDiscoveryMetric);
        this.attackDiscoveryEvent = attackDiscoveryEvent;
    }
    
    protected void doAction(final Map<String, Object> params) {
        EventDataProcessor.pushData(this.attackDiscoveryEvent.getEvent(), (Map)this.attackDiscoveryEvent.toEventData(params), EventCallerInferrer.inferClass(this.attackDiscoveryEvent.getEvent(), (EventCallerInferrer.CalleeInfoStackWalk)this, (String)null, (String)null), (ExecutionTimer)null);
    }
    
    public final int getStackTraceStartIndex(final StackTraceElement[] traces) {
        for (int traceIndex = 0; traceIndex < traces.length; ++traceIndex) {
            if (traces[traceIndex].getClassName().equals("com.zoho.security.instrumentation.WAFInstrumentClassLookup")) {
                return (traces.length > traceIndex + 2) ? (traceIndex + 1) : -1;
            }
        }
        return -1;
    }
    
    public boolean isInvalidCalleeClass(final String className) {
        return false;
    }
}
