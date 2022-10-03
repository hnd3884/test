package com.me.ems.onpremise.security.certificate.api.constants;

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
    public static final String UPLOADED_CERT_FILE_NAME = "uploadedCertificateFileName";
    public static final String PRODUCT_DISPLAY_NAME = "productDisplayName";
    public static final String PFX_INTERMEDIATE_MANUAL_UPLOAD = "pfxIntermediateManualUplaod";
    public static final String JKS_UPLOAD = "jksUpload";
    public static final String KEYSTORE_UPLOAD = "keystoreUpload";
    public static final int SELF_SIGNED_CA_CONFIRM = 80027;
    public static final int SELF_SIGNED_CA_RESTRICT = 80029;
    public static final int ENCRYPTED_KEY_ERROR = 80030;
    public static final int CERTIFICATE_IMPORT_SUCCESS = 80000;
    public static final int CERTIFICATE_IMPORT_SUCCESS_WITH_SSL_HOST_NAME_MISMATCH = 80001;
    public static final Long CONFIRM_NAT_ADDRESS_MISMATCH_STATUS_CODE;
    public static final int TRUSTED_COMM_ENABLED_EPCA_CHECK = 80039;
    public static final int TRUSTED_COMM_ENABLED_SECONDIP_CHECK = 80040;
    
    static {
        CONFIRM_NAT_ADDRESS_MISMATCH_STATUS_CODE = 80002L;
    }
}
