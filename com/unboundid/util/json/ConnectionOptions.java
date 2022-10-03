package com.unboundid.util.json;

import com.unboundid.util.ssl.SSLSocketVerifier;
import com.unboundid.util.ssl.HostNameSSLSocketVerifier;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class ConnectionOptions implements Serializable
{
    private static final String FIELD_CONNECT_TIMEOUT_MILLIS = "connect-timeout-millis";
    private static final String FIELD_DEFAULT_RESPONSE_TIMEOUT_MILLIS = "default-response-timeout-millis";
    private static final String FIELD_FOLLOW_REFERRALS = "follow-referrals";
    private static final String FIELD_USE_SCHEMA = "use-schema";
    private static final String FIELD_USE_SYNCHRONOUS_MODE = "use-synchronous-mode";
    private static final long serialVersionUID = 4615610794723107852L;
    private final boolean followReferrals;
    private final boolean useSchema;
    private final boolean useSynchronousMode;
    private final int connectTimeoutMillis;
    private final long defaultResponseTimeoutMillis;
    
    ConnectionOptions(final JSONObject connectionDetailsObject) throws LDAPException {
        boolean referrals = false;
        boolean schema = false;
        boolean synchronous = false;
        int connect = 60000;
        long response = 300000L;
        final JSONObject o = LDAPConnectionDetailsJSONSpecification.getObject(connectionDetailsObject, "connection-options");
        if (o != null) {
            LDAPConnectionDetailsJSONSpecification.validateAllowedFields(o, "connection-options", "connect-timeout-millis", "default-response-timeout-millis", "follow-referrals", "use-schema", "use-synchronous-mode");
            referrals = LDAPConnectionDetailsJSONSpecification.getBoolean(o, "follow-referrals", referrals);
            schema = LDAPConnectionDetailsJSONSpecification.getBoolean(o, "use-schema", schema);
            synchronous = LDAPConnectionDetailsJSONSpecification.getBoolean(o, "use-synchronous-mode", synchronous);
            connect = LDAPConnectionDetailsJSONSpecification.getInt(o, "connect-timeout-millis", connect, 0, null);
            response = LDAPConnectionDetailsJSONSpecification.getLong(o, "default-response-timeout-millis", response, 0L, null);
        }
        this.followReferrals = referrals;
        this.useSchema = schema;
        this.useSynchronousMode = synchronous;
        this.connectTimeoutMillis = connect;
        this.defaultResponseTimeoutMillis = response;
    }
    
    LDAPConnectionOptions createConnectionOptions(final SecurityOptions securityOptions) {
        final LDAPConnectionOptions options = new LDAPConnectionOptions();
        options.setFollowReferrals(this.followReferrals);
        options.setUseSchema(this.useSchema);
        options.setUseSynchronousMode(this.useSynchronousMode);
        options.setConnectTimeoutMillis(this.connectTimeoutMillis);
        options.setResponseTimeoutMillis(this.defaultResponseTimeoutMillis);
        if (securityOptions.verifyAddressInCertificate()) {
            options.setSSLSocketVerifier(new HostNameSSLSocketVerifier(true));
        }
        return options;
    }
}
