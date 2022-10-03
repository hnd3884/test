package com.me.devicemanagement.framework.server.certificate;

import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;

public class CertificateConstants
{
    public static final String CERTIFICATE_ID = "CERTIFICATE_ID";
    public static final String CERTIFICATE_NAME = "CERTIFICATE_NAME";
    public static final String IDENTIFY = "IDENTIFY";
    public static final String CERTIFICATE_TYPE = "CERTIFICATE_TYPE";
    public static final String CERTIFICATE_VERSION = "CERTIFICATE_VERSION";
    public static final String CERTIFICATE_SERIAL_NUMBER = "CERTIFICATE_SERIAL_NUMBER";
    public static final String SIGNATURE_ALGORITHM_OID = "SIGNATURE_ALGORITHM_OID";
    public static final String SIGNATURE_ALGORITHM_NAME = "SIGNATURE_ALGORITHM_NAME";
    public static final String CERTIFICATE_SIGNATURE = "CERTIFICATE_SIGNATURE";
    public static final String CERTIFICATE_EXPIRE = "CERTIFICATE_EXPIRE";
    public static final String CERTIFICATE_ISSUER_DN = "CERTIFICATE_ISSUER_DN";
    public static final String CERTIFICATE_SUBJECT_DN = "CERTIFICATE_SUBJECT_DN";
    public static final int SELF_SIGNED_CERT = 1;
    public static final int THIRD_PARTY_SSL_CERT = 2;
    public static final int SELF_SIGNED_CA_CERT = 3;
    public static final int SELF_SIGNED_SAN_CERT = 4;
    private static final String SYSTEM_HOME;
    private static final String SERVER_DATA_DIR;
    private static final String CLIENT_DATA_DIR;
    private static final String ROOT_CERT = "DMRootCA.crt";
    private static final String ROOT_KEY = "DMRootCA.key";
    private static final String LEAF_CERT = "server.crt";
    private static final String LEAF_KEY = "server.key";
    private static final String NGINX_CONF;
    private static final String APACHE_CONF;
    public static final String ROOT_CERT_PATH_APACHE;
    public static final String ROOT_KEY_PATH_APACHE;
    public static final String LEAF_CERT_PATH_APACHE;
    public static final String LEAF_KEY_PATH_APACHE;
    public static final String ROOT_CERT_PATH_NGINX;
    public static final String ROOT_KEY_PATH_NGINX;
    public static final String LEAF_CERT_PATH_NGINX;
    public static final String LEAF_KEY_PATH_NGINX;
    public static final String SERVER_ROOT_CLIENT_DATA_PATH;
    
    public static String getCxRootCertServerDataPath(final long customerID, final boolean isOnlyDirectoryPath) {
        if (isOnlyDirectoryPath) {
            return CertificateConstants.SERVER_DATA_DIR + customerID + File.separator + "server-certificates";
        }
        return CertificateConstants.SERVER_DATA_DIR + customerID + File.separator + "server-certificates" + File.separator + "DMRootCA.crt";
    }
    
    public static String getCxRootKeyServerDataPath(final long customerID, final boolean isOnlyDirectoryPath) {
        if (isOnlyDirectoryPath) {
            return CertificateConstants.SERVER_DATA_DIR + customerID + File.separator + "server-certificates";
        }
        return CertificateConstants.SERVER_DATA_DIR + customerID + File.separator + "server-certificates" + File.separator + "DMRootCA.key";
    }
    
    public static String getCxRootCertClientDataPath(final long customerID, final boolean isOnlyDirectoryPath) {
        if (isOnlyDirectoryPath) {
            return CertificateConstants.CLIENT_DATA_DIR + customerID + File.separator + "server-certificates";
        }
        return CertificateConstants.CLIENT_DATA_DIR + customerID + File.separator + "server-certificates" + File.separator + "DMRootCA.crt";
    }
    
    public static String getDSLeafCertServerDataPath(final long customerID, final long remoteOfficeID, final boolean isOnlyDirectoryPath) {
        if (isOnlyDirectoryPath) {
            return CertificateConstants.SERVER_DATA_DIR + "ds-certs" + File.separator + customerID + File.separator + remoteOfficeID;
        }
        return CertificateConstants.SERVER_DATA_DIR + "ds-certs" + File.separator + customerID + File.separator + remoteOfficeID + File.separator + "server.crt";
    }
    
    public static String getDSLeafKeyServerDataPath(final long customerID, final long remoteOfficeID, final boolean isOnlyDirectoryPath) {
        if (isOnlyDirectoryPath) {
            return CertificateConstants.SERVER_DATA_DIR + "ds-certs" + File.separator + customerID + File.separator + remoteOfficeID;
        }
        return CertificateConstants.SERVER_DATA_DIR + "ds-certs" + File.separator + customerID + File.separator + remoteOfficeID + File.separator + "server.key";
    }
    
    static {
        SYSTEM_HOME = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator;
        SERVER_DATA_DIR = DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "server-data" + File.separator;
        CLIENT_DATA_DIR = DCMetaDataUtil.getInstance().getClientDataDir() + File.separator;
        NGINX_CONF = CertificateConstants.SYSTEM_HOME + File.separator + "nginx" + File.separator + "conf" + File.separator;
        APACHE_CONF = CertificateConstants.SYSTEM_HOME + File.separator + "apache" + File.separator + "conf" + File.separator;
        ROOT_CERT_PATH_APACHE = CertificateConstants.APACHE_CONF + "DMRootCA.crt";
        ROOT_KEY_PATH_APACHE = CertificateConstants.APACHE_CONF + "DMRootCA.key";
        LEAF_CERT_PATH_APACHE = CertificateConstants.APACHE_CONF + "server.crt";
        LEAF_KEY_PATH_APACHE = CertificateConstants.APACHE_CONF + "server.key";
        ROOT_CERT_PATH_NGINX = CertificateConstants.NGINX_CONF + "DMRootCA.crt";
        ROOT_KEY_PATH_NGINX = CertificateConstants.NGINX_CONF + "DMRootCA.key";
        LEAF_CERT_PATH_NGINX = CertificateConstants.NGINX_CONF + "server.crt";
        LEAF_KEY_PATH_NGINX = CertificateConstants.NGINX_CONF + "server.key";
        SERVER_ROOT_CLIENT_DATA_PATH = CertificateConstants.CLIENT_DATA_DIR + "server-certificates" + File.separator + "DMRootCA.crt";
    }
}
