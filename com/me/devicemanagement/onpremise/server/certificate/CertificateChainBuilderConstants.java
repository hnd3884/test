package com.me.devicemanagement.onpremise.server.certificate;

public class CertificateChainBuilderConstants
{
    public static final String PFX = "pfx";
    public static final String DER = "der";
    public static final String CRT = "crt";
    public static final String CER = "cer";
    public static final String KEY = "key";
    public static final String JKS = "jks";
    public static final String KEYSTORE = "keystore";
    public static final String ERROR = "error";
    public static final String SUCCESS = "success";
    public static final String CONFIRM = "confirm";
    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_MSG = "errorMsg";
    public static final String ERROR_KEY = "errorKey";
    public static final String STATUS = "status";
    public static final String IS_ENTERPRISE_CA_SINGED_SERVER_CERT = "IsEnterpriseCASignedServerCert";
    public static final String SERVER_CERTIFICATE_THUMBPRINT = "server_certificate_thumbprint";
    public static final String ROOT_CERTIFICATE_THUMBPRINT = "root_certificate_thumbprint";
    public static final String CONFIRMED_CHANGE_IN_NAT_SETTINGS = "confirmedChangeInNatSettings";
    public static final String CONFIRMED_SELF_SIGNED_CA = "confirmedSelfSignedCA";
    public static final String IS_AUTO_DOWNLOAD_INTERMEDIATE_CERTS = "isAutoDownloadIntermediateCerts";
    public static final String UPLOADED_CERT_FILE_PATH = "uploadedCertificateFilePath";
    public static final String PFX_INTERMEDIATE_MANUAL_UPLOAD = "pfxIntermediateManualUplaod";
    public static final String JKS_UPLOAD = "jksUpload";
    public static final String KEYSTORE_UPLOAD = "keystoreUpload";
    public static final int INITIALIZE_FAILED = 80000;
    public static final int CERTIFICATE_EXPIRED = 80001;
    public static final int CERTIFICATE_ALGORITHM_NOT_SUPPORTED = 80002;
    public static final int PFX_CERTIFICATE_VALIDATION_FAILED = 80003;
    public static final int UPLOADED_CERTIFICATE_PATH_NULL = 80004;
    public static final int NAT_CHANGE_CONFIRMATION_REQUIRED = 80005;
    public static final int UNSUPPORTED_PROTOCOL_IN_DOWNLOAD_URL = 80006;
    public static final int PROXY_CONFIGURATION_NEEDED = 80007;
    public static final int SERVER_CERTIFICATE_NULL = 80008;
    public static final int KEY_NULL = 80009;
    public static final int CERT_CHAIN_NULL = 80010;
    public static final int CA_MANDATORY = 80011;
    public static final int NOT_END_ENTITY_CERT = 80012;
    public static final int CERT_KEY_MISMATCH = 80013;
    public static final int CERTIFICATE_EXTENSION_NOT_VALID = 80014;
    public static final int KEY_EXTENSION_NOT_VALID = 80015;
    public static final int BACKUP_FAILED = 80016;
    public static final int FILE_MOVE_FAILED = 80017;
    public static final int CONF_FILE_UPDATION_FAILED = 80018;
    public static final int ENTERPRISE_CA_CERT_CONFIRMATION_REQUIRED = 80019;
    public static final int CERTIFICATE_DOWNLOAD_DOMAIN_NOT_RESOLVED = 80020;
    public static final int CERTIFICATE_PKIX_PATH_BUILD_FAILED = 80021;
    public static final int PFX_PASSWORD_WRONG = 80022;
    public static final int PFX_CERTIFICATE_TAMPERED = 80023;
    public static final int SELF_SIGNED_CERT_NOT_ALLOWED = 80024;
    public static final int GENERIC_CERTIFICATE_EXCEPTION = 80025;
    public static final int NO_PERMISSION_FOR_FILE_WRITE = 80026;
    public static final int SELF_SIGNED_CA_CONFIRM = 80027;
    public static final int SELF_SIGNED_CA_PROCEED = 80028;
    public static final int SELF_SIGNED_CA_RESTRICT = 80029;
    public static final int ENCRYPTED_KEY_ERROR = 80030;
    public static final int KEYSTORE_NOT_CREATED = 80031;
    public static final int KEYSTORE_PASSWORD_WRONG = 80032;
    public static final int UNSUPPORTED_HEADER_IN_RESPONSE = 80033;
    public static final int LDAP_URL_INTERMEDIATE_CRT = 80034;
    public static final int INVALID_PUBLIC_KEY = 80035;
    public static final int INVALID_EXPIRY_DATE = 80036;
    public static final int SHA1_HASH_UNSUPPORTED = 80037;
    public static final int ENTERPRISE_CERTIFICATE_WITH_TRUSTED_COMMUNICATION = 80039;
}