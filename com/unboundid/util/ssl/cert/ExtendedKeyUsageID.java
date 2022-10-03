package com.unboundid.util.ssl.cert;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum ExtendedKeyUsageID
{
    TLS_SERVER_AUTHENTICATION("1.3.6.1.5.5.7.3.1", CertMessages.INFO_EXTENDED_KEY_USAGE_ID_TLS_SERVER_AUTHENTICATION.get()), 
    TLS_CLIENT_AUTHENTICATION("1.3.6.1.5.5.7.3.2", CertMessages.INFO_EXTENDED_KEY_USAGE_ID_TLS_CLIENT_AUTHENTICATION.get()), 
    CODE_SIGNING("1.3.6.1.5.5.7.3.3", CertMessages.INFO_EXTENDED_KEY_USAGE_ID_CODE_SIGNING.get()), 
    EMAIL_PROTECTION("1.3.6.1.5.5.7.3.4", CertMessages.INFO_EXTENDED_KEY_USAGE_ID_EMAIL_PROTECTION.get()), 
    TIME_STAMPING("1.3.6.1.5.5.7.3.8", CertMessages.INFO_EXTENDED_KEY_USAGE_ID_TIME_STAMPING.get()), 
    OCSP_SIGNING("1.3.6.1.5.5.7.3.9", CertMessages.INFO_EXTENDED_KEY_USAGE_ID_OCSP_SIGNING.get());
    
    private final OID oid;
    private final String name;
    
    private ExtendedKeyUsageID(final String oidString, final String name) {
        this.name = name;
        this.oid = new OID(oidString);
    }
    
    public OID getOID() {
        return this.oid;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static ExtendedKeyUsageID forOID(final OID oid) {
        for (final ExtendedKeyUsageID id : values()) {
            if (id.oid.equals(oid)) {
                return id;
            }
        }
        return null;
    }
    
    public static String getNameOrOID(final OID oid) {
        final ExtendedKeyUsageID id = forOID(oid);
        if (id == null) {
            return oid.toString();
        }
        return id.name;
    }
    
    public static ExtendedKeyUsageID forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "tlsserverauthentication":
            case "tls-server-authentication":
            case "tls_server_authentication":
            case "tls server authentication":
            case "serverauth":
            case "server-auth":
            case "server_auth":
            case "server auth": {
                return ExtendedKeyUsageID.TLS_SERVER_AUTHENTICATION;
            }
            case "tlsclientauthentication":
            case "tls-client-authentication":
            case "tls_client_authentication":
            case "tls client authentication":
            case "clientauth":
            case "client-auth":
            case "client_auth":
            case "client auth": {
                return ExtendedKeyUsageID.TLS_CLIENT_AUTHENTICATION;
            }
            case "codesigning":
            case "code-signing":
            case "code_signing":
            case "code signing": {
                return ExtendedKeyUsageID.CODE_SIGNING;
            }
            case "emailprotection":
            case "email-protection":
            case "email_protection":
            case "email protection": {
                return ExtendedKeyUsageID.EMAIL_PROTECTION;
            }
            case "timestamping":
            case "time-stamping":
            case "time_stamping":
            case "time stamping": {
                return ExtendedKeyUsageID.TIME_STAMPING;
            }
            case "ocspsigning":
            case "ocsp-signing":
            case "ocsp_signing":
            case "ocsp signing": {
                return ExtendedKeyUsageID.OCSP_SIGNING;
            }
            default: {
                return null;
            }
        }
    }
}
