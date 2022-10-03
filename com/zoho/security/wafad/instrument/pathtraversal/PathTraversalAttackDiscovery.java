package com.zoho.security.wafad.instrument.pathtraversal;

import java.util.Map;
import com.zoho.security.wafad.instrument.WAFAttackDiscoveryEvent;
import com.zoho.security.wafad.instrument.WAFAttackDiscoveryEventPush;

public class PathTraversalAttackDiscovery extends WAFAttackDiscoveryEventPush
{
    public static final String PATH_PAYLOAD = "PATH_PAYLOAD";
    private static final WAFAttackDiscoveryEvent PATH_TRAVERSAL_EVENT;
    
    public PathTraversalAttackDiscovery() {
        super(PathTraversalAttackDiscovery.PATH_TRAVERSAL_EVENT);
    }
    
    protected boolean matchesCondition(final Map<String, Object> params) {
        final String pathPayload = params.get("PATH_PAYLOAD");
        return pathPayload != null && (pathPayload.contains("/../") || pathPayload.contains("\\..\\"));
    }
    
    protected String getMatchedCondition() {
        return "[PATH_PAYLOAD] field data contains [..]";
    }
    
    @Override
    public boolean isInvalidCalleeClass(final String className) {
        return className.startsWith("java.io") || className.startsWith("java.nio");
    }
    
    static {
        PATH_TRAVERSAL_EVENT = new WAFAttackDiscoveryEvent("PATH_TRAVERSAL");
    }
}
