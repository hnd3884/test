package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DisconnectAccessLogMessage extends AccessLogMessage
{
    private static final long serialVersionUID = -6224280874144845557L;
    private final String message;
    private final String reason;
    
    public DisconnectAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public DisconnectAccessLogMessage(final LogMessage m) {
        super(m);
        this.reason = this.getNamedValue("reason");
        this.message = this.getNamedValue("msg");
    }
    
    public String getDisconnectReason() {
        return this.reason;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.DISCONNECT;
    }
}
