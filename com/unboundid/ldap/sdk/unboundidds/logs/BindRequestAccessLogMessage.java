package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class BindRequestAccessLogMessage extends OperationRequestAccessLogMessage
{
    private static final long serialVersionUID = 8603928027823970L;
    private final BindRequestAuthenticationType authenticationType;
    private final String dn;
    private final String protocolVersion;
    private final String saslMechanismName;
    
    public BindRequestAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public BindRequestAccessLogMessage(final LogMessage m) {
        super(m);
        this.dn = this.getNamedValue("dn");
        this.saslMechanismName = this.getNamedValue("saslMechanism");
        this.protocolVersion = this.getNamedValue("version");
        BindRequestAuthenticationType authType = null;
        try {
            authType = BindRequestAuthenticationType.valueOf(this.getNamedValue("authType"));
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        this.authenticationType = authType;
    }
    
    public final BindRequestAuthenticationType getAuthenticationType() {
        return this.authenticationType;
    }
    
    public final String getDN() {
        return this.dn;
    }
    
    public final String getProtocolVersion() {
        return this.protocolVersion;
    }
    
    public final String getSASLMechanismName() {
        return this.saslMechanismName;
    }
    
    @Override
    public final AccessLogOperationType getOperationType() {
        return AccessLogOperationType.BIND;
    }
}
