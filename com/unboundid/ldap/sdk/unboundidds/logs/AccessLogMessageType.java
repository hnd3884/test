package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum AccessLogMessageType
{
    ASSURANCE_COMPLETE("ASSURANCE-COMPLETE"), 
    CLIENT_CERTIFICATE("CLIENT-CERTIFICATE"), 
    CONNECT("CONNECT"), 
    DISCONNECT("DISCONNECT"), 
    ENTRY("ENTRY"), 
    ENTRY_REBALANCING_REQUEST("ENTRY-REBALANCING-REQUEST"), 
    ENTRY_REBALANCING_RESULT("ENTRY-REBALANCING-RESULT"), 
    FORWARD("FORWARD"), 
    FORWARD_FAILED("FORWARD-FAILED"), 
    INTERMEDIATE_RESPONSE("INTERMEDIATE-RESPONSE"), 
    REFERENCE("REFERENCE"), 
    REQUEST("REQUEST"), 
    RESULT("RESULT"), 
    SECURITY_NEGOTIATION("SECURITY-NEGOTIATION");
    
    private final String logIdentifier;
    
    private AccessLogMessageType(final String logIdentifier) {
        this.logIdentifier = logIdentifier;
    }
    
    public String getLogIdentifier() {
        return this.logIdentifier;
    }
    
    public static AccessLogMessageType forName(final String logIdentifier) {
        for (final AccessLogMessageType t : values()) {
            if (t.logIdentifier.equals(logIdentifier)) {
                return t;
            }
        }
        return null;
    }
}
