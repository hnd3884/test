package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ModifyDNForwardFailedAccessLogMessage extends ModifyDNRequestAccessLogMessage
{
    private static final long serialVersionUID = 3266864464529430335L;
    private final Integer resultCode;
    private final Integer targetPort;
    private final String message;
    private final String targetHost;
    private final String targetProtocol;
    
    public ModifyDNForwardFailedAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public ModifyDNForwardFailedAccessLogMessage(final LogMessage m) {
        super(m);
        this.targetHost = this.getNamedValue("targetHost");
        this.targetPort = this.getNamedValueAsInteger("targetPort");
        this.targetProtocol = this.getNamedValue("targetProtocol");
        this.resultCode = this.getNamedValueAsInteger("resultCode");
        this.message = this.getNamedValue("message");
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
    
    public Integer getResultCode() {
        return this.resultCode;
    }
    
    public String getDiagnosticMessage() {
        return this.message;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.FORWARD_FAILED;
    }
}
