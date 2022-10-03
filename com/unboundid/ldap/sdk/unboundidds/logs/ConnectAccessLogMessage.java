package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ConnectAccessLogMessage extends AccessLogMessage
{
    private static final long serialVersionUID = 4254346309071273212L;
    private final String clientConnectionPolicy;
    private final String protocolName;
    private final String sourceAddress;
    private final String targetAddress;
    
    public ConnectAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public ConnectAccessLogMessage(final LogMessage m) {
        super(m);
        this.sourceAddress = this.getNamedValue("from");
        this.targetAddress = this.getNamedValue("to");
        this.protocolName = this.getNamedValue("protocol");
        this.clientConnectionPolicy = this.getNamedValue("clientConnectionPolicy");
    }
    
    public String getSourceAddress() {
        return this.sourceAddress;
    }
    
    public String getTargetAddress() {
        return this.targetAddress;
    }
    
    public String getProtocolName() {
        return this.protocolName;
    }
    
    public String getClientConnectionPolicy() {
        return this.clientConnectionPolicy;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.CONNECT;
    }
}
