package com.me.ems.onpremise.security.securegatewayserver.constants;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;

public class SecureGatewayServerConstants
{
    public static final String PRODUCT_NAME = "ProductName";
    public static final String HTTP_PORT = "httpPort";
    public static final String HTTPS_PORT = "httpsPort";
    public static final String NS_PORT = "nsPort";
    public static final String WS_PORT = "wsPort";
    public static final String WSS_PORT = "wssPort";
    public static final String FTP_PORT = "ftpPort";
    public static final String HTTPS_PORT_MSP = "httpsPortMsp";
    public static final String NS_PORT_MSP = "nsPortMsp";
    public static final String WSS_PORT_MSP = "wssPortMsp";
    public static final String FTP_PORT_MSP = "ftpPortMsp";
    public static final String VALID_LICENSE = "isLicenseValid";
    public static final String SGS_REACHABLE = "isFSReachable";
    public static final String NAT_SAVED = "nat_saved";
    public static final String MAIL_ID = "support_mail";
    public static final String PRODUCT_CODE = "ProductCode";
    public static final String SGS_HELP_LINK = "fsHelpLink";
    public static final String CHECK_UPDATE_URL = "checkUpdateURL";
    public static final String LICENSE_ERROR = "licenseErrorMsg";
    public static final String IS_RECONFIGURE_WINDOWS_NEEDED = "reconfigureNeeded";
    public static final String IS_MSP = "isMsp";
    public static final String BUILD_NUMBER = "BuildNumber";
    public static final String SGS_AUTH_TOKEN = "SGS_AUTH_TOKEN";
    public static final String SGS_SETTINGS_FILE;
    public static final String SGS_SERVER = "FwServer";
    public static final String CERTIFICATE_SYNC_STATUS_CODE = "certificateSyncStatusCode";
    public static final String CERTIFICATE_SYNC_STATUS = "certificateSyncStatus";
    public static final int CERTIFICATE_SYNCHRONIZED_CODE = 1;
    public static final int CERTIFICATE_NOT_SYNCHRONIZED_CODE = 2;
    public static final String CERTIFICATE_SYNCHRONIZED = "Synchronous";
    public static final String CERTIFICATE_NOT_SYNCHRONIZED = "Asynchronous";
    public static final String ENABLE_EMAIL_NOTIFICATION = "enableEmailNotification";
    public static final String IS_EMAIL_NOTIFICATION_ENABLED = "isEmailNotificationEnabled";
    public static final String EMAL_ADDRESSES = "emailAddresses";
    public static final String SERVER_KEYSTORE_FILENAME = "server";
    public static final String SERVER_KEYSTORE_ALIAS = "tomcat";
    public static final String SERVER_KEYSTORE_PASSWORD = "changeit";
    public static final String INDEX_URI = "/";
    public static final String GET_STATUS = "/getStatus";
    public static final String GET_CERTIFICATE_URL = "/getCertificate";
    public static final String SYNC_DATA_URL = "/syncDataServlet";
    public static final String SGS_SERVER_DETAILS_FILE;
    
    static {
        SGS_SETTINGS_FILE = "conf" + File.separator + "fwsSettings.conf";
        SGS_SERVER_DETAILS_FILE = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "security" + File.separator + "sgsDetails.conf";
    }
}
