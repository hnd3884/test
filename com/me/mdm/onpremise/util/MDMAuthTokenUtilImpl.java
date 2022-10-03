package com.me.mdm.onpremise.util;

import java.util.Hashtable;
import java.io.File;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import java.util.Map;
import com.me.mdm.onpremise.server.enrollment.InvitationQRCodeEnrollmentHander;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.onpremise.server.admin.MDMAuthenticationKeyHandlerImpl;
import java.util.HashMap;
import java.util.Properties;
import com.me.mdm.server.factory.MDMAuthTokenUtilAPI;

public class MDMAuthTokenUtilImpl implements MDMAuthTokenUtilAPI
{
    public Properties createAuthToken(final String displayName) {
        final Properties properties = new Properties();
        ((Hashtable<String, String>)properties).put("device_authtoken", "");
        ((Hashtable<String, String>)properties).put("device_SCOPE", "");
        return properties;
    }
    
    public String getURLWithAuthToken(final HashMap hashMap) {
        final String fileURL = hashMap.get("path");
        return fileURL;
    }
    
    public String getURLWithAuthTokenAndUDID(final HashMap hashMap) {
        final boolean isServer = hashMap.get("IS_SERVER");
        String fileURL = hashMap.get("path");
        if (!isServer) {
            fileURL = fileURL + "%authtoken%" + "%deviceudid%";
        }
        return fileURL;
    }
    
    public String replaceAuthTokenPlaceHolder(final String placeHolderURL, final String authTokenValue, final String scope, final boolean encodeURL) {
        return placeHolderURL.replaceAll("%authtoken%", "");
    }
    
    public Boolean authenticateDevice(final Long erid, final String udid) {
        return Boolean.TRUE;
    }
    
    public Long authenticateUser(final String authToken) throws SecurityException, Exception {
        final DataObject authDO = MDMAuthenticationKeyHandlerImpl.getInstance().authenticateAPIKey(authToken, Integer.valueOf(201));
        if (authDO == null) {
            Logger.getLogger("MDMEnrollment").log(Level.INFO, "--------------------------------------------------------------------------------");
            Logger.getLogger("MDMEnrollment").log(Level.INFO, "Invalid Auth token Used by Admin App. Auth Token is not associated to any user.");
            Logger.getLogger("MDMEnrollment").log(Level.INFO, "---------------------------------------------------------------------------------");
            throw new SecurityException("Invalid Auth token Used by Admin App. Auth Token is not associated to any user.");
        }
        final Row aaaLoginRow = authDO.getFirstRow("AaaLogin");
        final Long loginId = (Long)aaaLoginRow.get("LOGIN_ID");
        return loginId;
    }
    
    public String getOrAddAuthToken(final Properties prop) throws DataAccessException, SyMException {
        prop.setProperty("SERVICE_TYPE", Integer.toString(201));
        return MDMAuthenticationKeyHandlerImpl.getInstance().getOrAddAuthToken(prop);
    }
    
    public Properties appendEnrollmentPropertiesForEnrollemntInMail(final Long enrollmentRequestID, final Integer platformType, final Properties enrollRequestProperties, final Properties enrollMailProperties) throws Exception {
        final Long customerId = ((Hashtable<K, Long>)enrollRequestProperties).get("CUSTOMER_ID");
        final String domainName = ((Hashtable<K, String>)enrollRequestProperties).get("DOMAIN_NETBIOS_NAME");
        final String sServerBaseURL = MDMEnrollmentUtil.getInstance().getServerBaseURL();
        final String url = sServerBaseURL + "/mdm/enroll" + "/" + String.valueOf(enrollmentRequestID);
        final String helpURL = ProductUrlLoader.getInstance().getValue("mdmUrl") + "/help/enrollment/enroll_android_devices.html#Enrollment_Process_in_mobile_device_for_Android_devices";
        String discoverURL = sServerBaseURL + "/mdm/client/v1/wpdiscover/" + customerId;
        final JSONObject json = new JSONObject();
        json.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
        discoverURL = MDMDeviceAPIKeyGenerator.getInstance().getAPIKey(json).appendAsURLParams(discoverURL) + "&erid=" + enrollmentRequestID;
        final Properties serverProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
        ((Hashtable<String, String>)enrollMailProperties).put("$link_to_enroll$", url);
        ((Hashtable<String, String>)enrollMailProperties).put("$domain_name$", domainName);
        ((Hashtable<String, Object>)enrollMailProperties).put("$server_name$", ((Hashtable<K, Object>)serverProps).get("NAT_ADDRESS"));
        ((Hashtable<String, Object>)enrollMailProperties).put("$server_port$", ((Hashtable<K, Object>)serverProps).get("NAT_HTTPS_PORT"));
        ((Hashtable<String, String>)enrollMailProperties).put("$help_link$", helpURL);
        ((Hashtable<String, String>)enrollMailProperties).put("$discover_url$", discoverURL);
        ((Hashtable<String, String>)enrollMailProperties).put("$complete_server_url$", MDMEnrollmentUtil.getInstance().getServerBaseURL());
        ((Hashtable<String, String>)enrollMailProperties).put("$organisation_name$", MDMApiFactoryProvider.getMDMUtilAPI().getOrgName(customerId));
        ((Hashtable<String, String>)enrollMailProperties).put("$organisation_logo_src$", "cid:OrgLogo");
        if (platformType == 0) {
            ((Hashtable<String, String>)enrollMailProperties).put("$qrimage$", this.getNeutralQRImageTag(enrollmentRequestID, enrollMailProperties.getProperty("$passcode$")));
        }
        else {
            ((Hashtable<String, String>)enrollMailProperties).put("$qrimage$", this.getQRImageTag(enrollmentRequestID, enrollMailProperties.getProperty("$passcode$")));
        }
        final HashMap<String, String> mailImages = new HashMap<String, String>();
        mailImages.put("OrgLogo", CustomerInfoUtil.getInstance().getLogoPath(customerId));
        if (platformType != 3) {
            mailImages.put("QRImage", InvitationQRCodeEnrollmentHander.getQRImagefileNameForEnrollmentRequest(enrollmentRequestID));
        }
        if (platformType == 0) {
            ((Hashtable<String, String>)enrollMailProperties).put("$ios_logo$", "cid:iOSLogo");
            ((Hashtable<String, String>)enrollMailProperties).put("$android_logo$", "cid:AndroidLogo");
            ((Hashtable<String, String>)enrollMailProperties).put("$windows_logo$", "cid:WindowsLogo");
            mailImages.put("iOSLogo", this.getPlatformLogoPath(1));
            mailImages.put("AndroidLogo", this.getPlatformLogoPath(2));
            mailImages.put("WindowsLogo", this.getPlatformLogoPath(3));
        }
        JSONObject obj = ((Hashtable<K, JSONObject>)enrollMailProperties).get("additionalParams");
        if (obj == null) {
            obj = new JSONObject();
        }
        obj.put("InlineImages", (Map)mailImages);
        ((Hashtable<String, JSONObject>)enrollMailProperties).put("additionalParams", obj);
        return enrollMailProperties;
    }
    
    public JSONObject appendEnrollmentPropertiesForEnrollmentInUI(final Long enrollmentRequestID, final Integer platformType, final JSONObject enrollmentDetails) throws Exception {
        final String sServerBaseURL = MDMEnrollmentUtil.getInstance().getServerBaseURL();
        final String url = sServerBaseURL + "/mdm/enroll/" + String.valueOf(enrollmentRequestID);
        final JSONObject json = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestProperties(enrollmentRequestID);
        final Long customerId = json.optLong("Resource.CUSTOMER_ID");
        final String otpPassword = String.valueOf(enrollmentDetails.get("otp_password"));
        final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
        String serverIP;
        String serverPort;
        if (natProps.size() > 0) {
            serverIP = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
            serverPort = String.valueOf(((Hashtable<K, Object>)natProps).get("NAT_HTTPS_PORT"));
        }
        else {
            final Properties serverProp = MDMUtil.getDCServerInfo();
            serverIP = ((Hashtable<K, String>)serverProp).get("SERVER_MAC_NAME");
            serverPort = String.valueOf(((Hashtable<K, Object>)serverProp).get("SERVER_PORT"));
        }
        enrollmentDetails.put("server_name", (Object)serverIP);
        enrollmentDetails.put("server_port", (Object)serverPort);
        if (platformType == 3) {
            final Boolean isAppBasedEnrollment = Boolean.valueOf(MDMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"));
            if (!isAppBasedEnrollment) {
                String discoverURL = MDMApiFactoryProvider.getMDMUtilAPI().getServerURLOnTomcatPortForClientAuthSetup() + "/mdm/client/v1/wpdiscover/" + customerId;
                discoverURL = discoverURL + "?erid=" + enrollmentRequestID;
                final JSONObject apiKeyJson = new JSONObject();
                apiKeyJson.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
                discoverURL = MDMDeviceAPIKeyGenerator.getInstance().getAPIKey(apiKeyJson).appendAsURLParams(discoverURL);
                enrollmentDetails.put("discover_url", (Object)discoverURL);
            }
            enrollmentDetails.put("is_app_based_enrollment_for_windows_phone", (Object)isAppBasedEnrollment);
        }
        enrollmentDetails.put("enrollment_url", (Object)url);
        enrollmentDetails.put("qr_url", (Object)InvitationQRCodeEnrollmentHander.getQREnrollmentURLFromContextPath(enrollmentRequestID, otpPassword, 2));
        return enrollmentDetails;
    }
    
    public Properties appendProductSpecificSMSProperties(final Long enrollmentRequestID, final Integer platformType, final Properties enrollRequestProperties, final Properties smsProperties) throws Exception {
        return smsProperties;
    }
    
    public Properties appendManagedUserProperties(final Properties properties) throws Exception {
        return properties;
    }
    
    public String getCustomerPhoneNumber(final String email) {
        return null;
    }
    
    private String getQRImageTag(final Long erid, final String otppassword) {
        try {
            return I18N.getMsg("mdm.enroll.mailtemplate.allplatform.qr_imgage", new Object[] { "cid:QRImage", InvitationQRCodeEnrollmentHander.getQREnrollmentImageURL(erid, otppassword, 3) });
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMEnrollmentRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private String getNeutralQRImageTag(final Long erid, final String otppassword) {
        try {
            return I18N.getMsg("mdm.enroll.mailtemplate.neutral.qr_imgage", new Object[] { "cid:QRImage", InvitationQRCodeEnrollmentHander.getQREnrollmentImageURL(erid, otppassword, 3) });
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMEnrollmentRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public String getAdminEnrollAuthenticationUrl(final JSONObject jsonObject) throws Exception {
        final String deviceForEnrollmentId = String.valueOf(jsonObject.get("ENROLLMENT_DEVICE_ID"));
        return MDMEnrollmentUtil.getInstance().getServerBaseURL() + "/mdm/enroll?actionToCall=showAdminEnrollAuthPage&deviceForEnrollmentId=" + deviceForEnrollmentId;
    }
    
    public Long getAuthenticatedUserId(final Long deviceForEnrollmentId) throws Exception {
        return -1L;
    }
    
    public Boolean isActiveDirectoryOrZohoAccountAuthApplicable(final Long customerId) throws Exception {
        if (MDMEnrollmentUtil.getInstance().getADCount(customerId) > 0) {
            return true;
        }
        return false;
    }
    
    public String getPlatformLogoPath(final int platformType) {
        String logoPath = "";
        switch (platformType) {
            case 1: {
                logoPath = DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "images" + File.separator + "platformNeutralMailInviteIcons" + File.separator + "ios.png";
                break;
            }
            case 2: {
                logoPath = DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "images" + File.separator + "platformNeutralMailInviteIcons" + File.separator + "android.png";
                break;
            }
            case 3: {
                logoPath = DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "images" + File.separator + "platformNeutralMailInviteIcons" + File.separator + "windows.png";
                break;
            }
        }
        return logoPath;
    }
}
