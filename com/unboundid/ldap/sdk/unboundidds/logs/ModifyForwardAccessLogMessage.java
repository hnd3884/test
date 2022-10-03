package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ModifyForwardAccessLogMessage extends ModifyRequestAccessLogMessage
{
    private static final long serialVersionUID = 7933843749321013693L;
    private final Integer targetPort;
    private final String targetHost;
    private final String targetProtocol;
    
    public ModifyForwardAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public ModifyForwardAccessLogMessage(final LogMessage m) {
        super(m);
        this.targetHost = this.getNamedValue("targetHost");
        this.targetPort = this.getNamedValueAsInteger("targetPort");
        this.targetProtocol = this.getNamedValue("targetProtocol");
    }
    
    public String getTargetHost() {
        return this.targetHost;
    }
    
    public Integer getTargetPort() {
        return this.targetPort;
    }
    
    public String getTargetProtocol() {
        return this.targetProtocol;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.FORWARD;
    }
}
