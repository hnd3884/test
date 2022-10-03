package com.zoho.security.wafad.instrument.pathtraversal;

import com.zoho.security.eventfw.config.EventConfigParser;
import com.zoho.security.eventfw.EventDataProcessor;
import java.util.Map;
import javax.servlet.DispatcherType;

public abstract class ReqDispatcherPathTraversalAttackDiscovery extends PathTraversalAttackDiscovery
{
    private static final String RQ_DISPATCHER_TYPE_PARAM_NAME = "RQ_DISPATCHER_TYPE";
    private final DispatcherType dispatcherType;
    
    public ReqDispatcherPathTraversalAttackDiscovery(final DispatcherType dispatcherType) {
        this.dispatcherType = dispatcherType;
    }
    
    @Override
    protected boolean matchesCondition(final Map<String, Object> params) {
        if (super.matchesCondition(params)) {
            params.put("RQ_DISPATCHER_TYPE", this.dispatcherType.name());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isInvalidCalleeClass(final String className) {
        final EventConfigParser parser = EventDataProcessor.getParser();
        return parser.getCalleeInfoExcludePattern().matcher(className).matches() || className.equals("org.apache.catalina.core.ApplicationDispatcher") || this.isInheritClassMatches(parser.getCalleeInfoInheritClassExcludes(), className);
    }
}
