package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SecurityNegotiationAccessLogMessage extends AccessLogMessage
{
    private static final long serialVersionUID = -8588250275523891216L;
    private final String cipher;
    private final String protocol;
    
    public SecurityNegotiationAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public SecurityNegotiationAccessLogMessage(final LogMessage m) {
        super(m);
        this.protocol = this.getNamedValue("protocol");
        this.cipher = this.getNamedValue("cipher");
    }
    
    public String getProtocol() {
        return this.protocol;
    }
    
    public String getCipher() {
        return this.cipher;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.SECURITY_NEGOTIATION;
    }
}
