package com.unboundid.ldap.sdk.experimental;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DraftChuLDAPLogSchema00BindEntry extends DraftChuLDAPLogSchema00Entry
{
    public static final String ATTR_BIND_METHOD = "reqMethod";
    public static final String ATTR_PROTOCOL_VERSION = "reqVersion";
    private static final long serialVersionUID = 864660009992589945L;
    private final int protocolVersion;
    private final String bindMethod;
    private final String saslMechanism;
    
    public DraftChuLDAPLogSchema00BindEntry(final Entry entry) throws LDAPException {
        super(entry, OperationType.BIND);
        final String versionString = entry.getAttributeValue("reqVersion");
        if (versionString == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqVersion"));
        }
        try {
            this.protocolVersion = Integer.parseInt(versionString);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_BIND_VERSION_ERROR.get(entry.getDN(), "reqVersion", versionString), e);
        }
        final String rawMethod = entry.getAttributeValue("reqMethod");
        if (rawMethod == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqMethod"));
        }
        final String lowerMethod = StaticUtils.toLowerCase(rawMethod);
        if (lowerMethod.equals("simple")) {
            this.bindMethod = "SIMPLE";
            this.saslMechanism = null;
        }
        else if (lowerMethod.startsWith("sasl/")) {
            this.bindMethod = "SASL";
            this.saslMechanism = rawMethod.substring(5);
        }
        else {
            this.bindMethod = rawMethod;
            this.saslMechanism = null;
        }
    }
    
    public int getProtocolVersion() {
        return this.protocolVersion;
    }
    
    public String getBindMethod() {
        return this.bindMethod;
    }
    
    public String getSASLMechanism() {
        return this.saslMechanism;
    }
}
