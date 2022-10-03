package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ClientCertificateAccessLogMessage extends AccessLogMessage
{
    private static final long serialVersionUID = -2585979292882352926L;
    private final String issuerSubject;
    private final String peerSubject;
    
    public ClientCertificateAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public ClientCertificateAccessLogMessage(final LogMessage m) {
        super(m);
        this.peerSubject = this.getNamedValue("peerSubject");
        this.issuerSubject = this.getNamedValue("issuerSubject");
    }
    
    public String getPeerSubject() {
        return this.peerSubject;
    }
    
    public String getIssuerSubject() {
        return this.issuerSubject;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.CLIENT_CERTIFICATE;
    }
}
