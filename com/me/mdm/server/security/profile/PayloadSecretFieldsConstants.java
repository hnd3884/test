package com.me.mdm.server.security.profile;

public class PayloadSecretFieldsConstants
{
    public static final String PAYLOAD_SECRET_FIELD = "secret_field";
    public static final String RETURN_PAYLOAD_SECRET_FIELD = "return_secret_field_value";
    public static final String SECRET_FIELD_REGEX = "%MDMPassword_(\\d+)%";
    public static final String ENCODED_SECRET_FIELD_REGEX = "%MDMPasswordEC_(\\d+)%";
    public static final String ENCODED_SECRET_FIELD_REPLACE_REGEX = "<string>%MDMPasswordEC_(\\d+)%</string>";
    public static final String CREDENTIAL_SECRET_FIELD_REGEX = "%Credential_(\\d+)%";
    public static final String CERTIFICATE_REGEX = "%MDMCertificate_(\\d+)%";
    public static final String CERT_PASSWORD_REGEX = "%MDMCertPassword_(\\d+)%";
    public static final String IOS_CERT_REGEX = "%IOSCertificate_(\\d+)%";
    public static final String FILEVAULT_CERT_REGEX = "%FileVaultCert_(\\d+)%";
    public static final String SSL_CERT_REGEX = "%MDMSSLCertificate_(\\d+)%";
    public static final String RECOVER_CERT_REGEX = "%RecoverCertificate_(\\d+)%";
    public static final String SCEP_CHALLENGE_REGEX = "%MDMSCEPChallenge_(\\d+)%";
}
