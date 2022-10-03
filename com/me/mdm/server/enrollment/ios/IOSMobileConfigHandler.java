package com.me.mdm.server.enrollment.ios;

import javax.ws.rs.core.UriBuilder;
import java.security.cert.Certificate;
import java.io.InputStream;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import java.security.cert.X509Certificate;
import java.io.ByteArrayInputStream;
import com.me.mdm.certificate.CertificateHandler;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import java.util.Map;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.enrollment.MDMEnrollmentOTPHandler;
import com.adventnet.sym.server.mdm.ios.payload.IOSSCEPPayload;
import com.adventnet.sym.server.mdm.ios.payload.CertificatePayload;
import com.adventnet.sym.server.mdm.ios.payload.MDMPayload;
import com.adventnet.sym.server.mdm.ios.payload.transform.PayloadIdentifierConstants;
import com.dd.plist.NSArray;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.dd.plist.NSObject;
import java.util.ArrayList;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.adventnet.sym.server.mdm.ios.payload.ConfigurationPayload;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.mdm.server.apple.useraccount.AppleMultiUserUtils;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOSMobileConfigHandler
{
    public static Logger logger;
    private static IOSMobileConfigHandler mobileConfigHandler;
    
    public static IOSMobileConfigHandler getInstance() {
        if (IOSMobileConfigHandler.mobileConfigHandler == null) {
            IOSMobileConfigHandler.mobileConfigHandler = new IOSMobileConfigHandler();
        }
        return IOSMobileConfigHandler.mobileConfigHandler;
    }
    
    public String generateUpgradeMobileConfig(final long enrollmentReqId, final String strUDID, final String commandUUID, final String servletPath, final String queryParams, final Long resourceID) throws Exception {
        IOSMobileConfigHandler.logger.log(Level.INFO, "======================= UPGRADE MOBILE CONFIG CREATION BEGINS for ENROLLMENT REQ: {0} =====================", new Object[] { enrollmentReqId });
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-generateUpgradeMobileConfig: Creating upgrade Mobile config. DATA-IN: {0}, {1}, {2}, {3}", new Object[] { enrollmentReqId, strUDID, servletPath, commandUUID });
        MobileConfigUpgradeHandler.getInstance().addOrUpdateMobileConfigUpgradeRequest(strUDID);
        Integer accessRights = AppleAccessRightsHandler.getInstance().getAccessRightsForErid(enrollmentReqId);
        if (accessRights == null) {
            accessRights = (commandUUID.equalsIgnoreCase("InstallProfile;Collection=UpgradeMobileConfig4") ? 2047 : 8191);
        }
        final int deviceType = this.getDeviceTypeFromServletPath(servletPath);
        final int servletVersion = MDMDeviceAPIKeyGenerator.getInstance().isClientVersion2_0(servletPath) ? APIKey.VERSION_2_0 : APIKey.VERSION_1_0;
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-generateUpgradeMobileConfig: Device type: {0}, Api version: {1}, Access rights:{2}", new Object[] { deviceType, servletVersion, accessRights });
        final String serverBaseURL = MDMEnrollmentUtil.getInstance().getServerBaseURL();
        boolean isSharedDevice = false;
        if (deviceType == 2) {
            isSharedDevice = AppleMultiUserUtils.isSharediPadEnrollmentRequest(enrollmentReqId);
        }
        final String checkInURL = serverBaseURL + this.getCheckinUrlPath(deviceType, servletVersion, isSharedDevice) + "?" + queryParams;
        final String serverURL = serverBaseURL + this.getServerUrlPath(deviceType, servletVersion, isSharedDevice) + "?" + queryParams;
        byte[] iosMobileConfig = null;
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean isSkipCustomerFlagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                isSkipCustomerFlagSet = true;
            }
            final ConfigurationPayload cfgPayload = (ConfigurationPayload)this.createIosMobileConfigPayload(enrollmentReqId, serverURL, checkInURL, deviceType, accessRights, isSharedDevice);
            final String mobileConfig = cfgPayload.getPayloadDict().toXMLPropertyList();
            IOSMobileConfigHandler.logger.log(Level.FINE, "IOSMobileConfigHandler-generateUpgradeMobileConfig: Mobile config created. DATA-OUT: {0}", new Object[] { mobileConfig });
            iosMobileConfig = mobileConfig.getBytes();
        }
        catch (final Exception e) {
            IOSMobileConfigHandler.logger.log(Level.SEVERE, "Exception while creating upgrade mobile config for : {0}, {1}, {2}", new Object[] { enrollmentReqId, strUDID, resourceID });
        }
        finally {
            if (isSkipCustomerFlagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
        final IOSCommandPayload commandPayload = PayloadHandler.getInstance().createCommandPayload("InstallProfile");
        commandPayload.setCommandUUID(commandUUID.split(";")[1]);
        commandPayload.setPayload(iosMobileConfig);
        final String strQuery = commandPayload.getPayloadDict().toXMLPropertyList();
        final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
        final JSONObject commandStatusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
        commandStatusJSON.put("COMMAND_ID", (Object)commandId);
        commandStatusJSON.put("RESOURCE_ID", (Object)resourceID);
        commandStatusJSON.put("COMMAND_STATUS", 1);
        new CommandStatusHandler().populateCommandStatus(commandStatusJSON);
        IOSMobileConfigHandler.logger.log(Level.INFO, "======================= UPGRADE MOBILE CONFIG CREATION SUCCESSFUL for ENROLLMENT REQ: {0} =====================", new Object[] { enrollmentReqId });
        return strQuery;
    }
    
    public byte[] generateMobileConfig(final long enrollmentRequestId, final int deviceType, final int osVersion) {
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean isSkipCustomerFlagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                isSkipCustomerFlagSet = true;
            }
            IOSMobileConfigHandler.logger.log(Level.INFO, "======================= IOS MOBILE CONFIG CREATION BEGINS for ENROLLMENT REQ: {0} =====================", new Object[] { enrollmentRequestId });
            IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-generateMobileConfig: Creating Mobile config. DATA-IN: Erid = {0}, Device type =  {1}, osVersion = {2}", new Object[] { enrollmentRequestId, deviceType, osVersion });
            final APIKey apiKey = MDMiOSEntrollmentUtil.getInstance().checkAndGenerateApiKeyForEnrollmentRequest(enrollmentRequestId);
            final Long customerId = MDMiOSEntrollmentUtil.getInstance().getCustomerIdForErid(enrollmentRequestId);
            if (customerId == null) {
                IOSMobileConfigHandler.logger.log(Level.SEVERE, "No matching customer Id found for Enrollment request Id: {0}", new Object[] { enrollmentRequestId });
                throw new Exception("No matching customer Id found for Enrollment request Id: " + enrollmentRequestId);
            }
            final int accessRights = AppleAccessRightsHandler.getInstance().generateAccessRights(enrollmentRequestId, osVersion);
            IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-generateMobileConfig: Device type: {0}, Api version: {1}, Access rights:{2}", new Object[] { deviceType, apiKey.getVersion(), accessRights });
            boolean isSharedDevice = false;
            if (deviceType == 2) {
                isSharedDevice = AppleMultiUserUtils.isSharediPadEnrollmentRequest(enrollmentRequestId);
            }
            final String checkInUrlPath = this.getCheckinUrlPath(deviceType, apiKey.getVersion(), isSharedDevice);
            final String checkInURL = this.getUrlForEnrollmentRequest(checkInUrlPath, customerId, enrollmentRequestId, apiKey.getKeyValue());
            final String serverUrlPath = this.getServerUrlPath(deviceType, apiKey.getVersion(), isSharedDevice);
            final String serverURL = this.getUrlForEnrollmentRequest(serverUrlPath, customerId, enrollmentRequestId, apiKey.getKeyValue());
            final ConfigurationPayload cfgPayload = (ConfigurationPayload)this.createIosMobileConfigPayload(enrollmentRequestId, serverURL, checkInURL, deviceType, accessRights, isSharedDevice);
            final String mobileConfig = cfgPayload.getPayloadDict().toXMLPropertyList();
            IOSMobileConfigHandler.logger.log(Level.FINE, "IOSMobileConfigHandler-generateMobileConfig: Mobile config created. DATA-OUT: {0}", new Object[] { mobileConfig });
            IOSMobileConfigHandler.logger.log(Level.INFO, "======================= IOS MOBILE CONFIG CREATION SUCCESSFUL for ENROLLMENT REQ: {0} =====================", new Object[] { enrollmentRequestId });
            return mobileConfig.getBytes();
        }
        catch (final Exception e) {
            IOSMobileConfigHandler.logger.log(Level.SEVERE, e, () -> "IOSMobileConfigHandler-generateMobileConfig: Exception while creating mobile config for " + n);
            return new byte[0];
        }
        finally {
            if (isSkipCustomerFlagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
    }
    
    private IOSPayload createIosMobileConfigPayload(final long enrollmentRequestId, final String serverUrl, final String checkinUrl, final int deviceType, final int accessRights, final boolean sharedDevice) throws Exception {
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-createIosMobileConfigPayload: Enrollment Req Id: {0}", new Object[] { enrollmentRequestId });
        final Long customerId = MDMiOSEntrollmentUtil.getInstance().getCustomerIdForErid(enrollmentRequestId);
        if (customerId == null) {
            IOSMobileConfigHandler.logger.log(Level.SEVERE, "No matching customer Id found for Enrollment request Id: {0}", new Object[] { enrollmentRequestId });
            throw new Exception("No matching customer Id found for Enrollment request Id: " + enrollmentRequestId);
        }
        final String organizationName = MDMApiFactoryProvider.getMDMUtilAPI().getOrgName(customerId);
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-createIosMobileConfigPayload: Enrollment Req Id: {0}, Customer Id: {1}, Organization name: {2} ", new Object[] { enrollmentRequestId, customerId, organizationName });
        final ArrayList<NSObject> configurationPayloadsList = new ArrayList<NSObject>();
        final IOSPayload identityCertPayload = getIdentityCertPayload(organizationName, customerId, enrollmentRequestId);
        configurationPayloadsList.add((NSObject)identityCertPayload.getPayloadDict());
        IOSMobileConfigHandler.logger.log(Level.FINE, "IOSMobileConfigHandler-createIosMobileConfigPayload: Enrollment Id: {0}, Identity cert payload: {1}", new Object[] { enrollmentRequestId, identityCertPayload.getPayloadDict().toXMLPropertyList() });
        final String identityCertificateUUID = identityCertPayload.getPayloadDict().objectForKey("PayloadUUID").toString();
        final MDMPayload mdmPayload = this.getMdmPayload(organizationName, serverUrl, checkinUrl, deviceType, accessRights, identityCertificateUUID, sharedDevice);
        configurationPayloadsList.add((NSObject)mdmPayload.getPayloadDict());
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-createIosMobileConfigPayload: Enrollment Request Id: {0}, MDM payload: {1}", new Object[] { enrollmentRequestId, mdmPayload.getPayloadDict().toXMLPropertyList() });
        if (ApiFactoryProvider.getServerSettingsAPI().getCertificateType() != 2) {
            IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-createIosMobileConfigPayload: Server certificate is not issued by a globally trusted CA, So adding server cert. Enrollment req id: {0}", new Object[] { enrollmentRequestId });
            final CertificatePayload serverCertificatePayload = getServerCertificatePayload(organizationName);
            configurationPayloadsList.add((NSObject)serverCertificatePayload.getPayloadDict());
            IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-createIosMobileConfigPayload: Enrollment Request Id: {0}, Server certificate payload: {1}", new Object[] { enrollmentRequestId, serverCertificatePayload.getPayloadDict().toXMLPropertyList() });
        }
        final NSArray configurationPayloads = new NSArray((NSObject[])configurationPayloadsList.toArray(new NSObject[0]));
        final ConfigurationPayload configurationPayload = new ConfigurationPayload(1, organizationName, PayloadIdentifierConstants.MDM_INSTALATION_PROFILE_IDENTIFIER, "MDM Profile");
        configurationPayload.setPayloadContent(configurationPayloads);
        return configurationPayload;
    }
    
    private static IOSPayload getIdentityCertPayload(final String organizationName, final Long customerId, final Long enrollmentRequestId) throws Exception {
        final boolean isApnsConfigFeatureEnabled = MDMiOSEntrollmentUtil.getInstance().isApnsConfigFeatureEnabled();
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getIdentityCertPayload: Is Apns Config Feature Enabled: {0}", new Object[] { isApnsConfigFeatureEnabled });
        if (isApnsConfigFeatureEnabled) {
            return getApnsCertPayload(organizationName);
        }
        return getScepConfigurationPayload(organizationName, customerId, enrollmentRequestId);
    }
    
    private static IOSSCEPPayload getScepConfigurationPayload(final String organizationName, final Long customerID, final Long enrollmentRequestID) throws Exception {
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getScepConfigurationPayload: ScepConfig feature enabled. Enrollment req id: {0}, customer id: {1}, Organization name: {2}", new Object[] { enrollmentRequestID, customerID, organizationName });
        final String serverBaseURL = MDMEnrollmentUtil.getInstance().getServerBaseURL();
        final APIKey apiKey = MDMiOSEntrollmentUtil.getInstance().checkAndGenerateApiKeyForEnrollmentRequest(enrollmentRequestID);
        final String scepServerURL = MDMiOSEntrollmentUtil.getMdmScepServerUrl(serverBaseURL, customerID, enrollmentRequestID, apiKey.getKeyValue());
        final NSArray scepSubject = MDMiOSEntrollmentUtil.getScepSubject(enrollmentRequestID);
        final String scepEnrollmentPasscode = MDMEnrollmentOTPHandler.getInstance().generateMdmClientToken(enrollmentRequestID);
        final IOSSCEPPayload iosscepPayload = new IOSSCEPPayload(1, organizationName, "com.mdm.mobiledevice.identity.scep", "Enrollment Client Certificate Configuration");
        iosscepPayload.setURL(scepServerURL);
        iosscepPayload.setSubject(scepSubject);
        iosscepPayload.setChallenge(scepEnrollmentPasscode);
        iosscepPayload.setKeyType("RSA");
        iosscepPayload.setKeysize(2048L);
        iosscepPayload.setKeyUsage(5);
        iosscepPayload.setPayloadContent();
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler: IOS Scep payload constructed successfully. Enrollment req id: {0}, customer id: {1}, Organization name: {2}", new Object[] { enrollmentRequestID, customerID, organizationName });
        return iosscepPayload;
    }
    
    private static CertificatePayload getApnsCertPayload(final String organizationName) throws Exception {
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getApnsCertPayload: Getting Apns certificate details for organization: {0}", new Object[] { organizationName });
        final Map certificateDetails = MDMUtil.getAPNSCertificateDetails();
        final String certFileName = certificateDetails.get("CERTIFICATE_FILE_NAME");
        final String certPassword = certificateDetails.get("CERTIFICATE_PASSWORD");
        final String apnsCertificateFilePath = MDMUtil.getAPNsCertificateFolderPath() + File.separator + certFileName;
        final CertificatePayload apnsCertPayload = new CertificatePayload(1, organizationName, PayloadIdentifierConstants.MDM_INSTALATION_PROFILE_APNS_CERTIFICATE_IDENTIFIER, certificateDetails.get("CERTIFICATE_NAME"));
        apnsCertPayload.setPassword(certPassword);
        apnsCertPayload.setPayloadType("com.apple.security.pkcs12");
        apnsCertPayload.setPayloadContent(apnsCertificateFilePath);
        apnsCertPayload.getPayloadDict().put("PayloadDisplayName", certificateDetails.get("CERTIFICATE_NAME"));
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getApnsCertPayload: Apns Payload constructed for organization: {0}", new Object[] { organizationName });
        return apnsCertPayload;
    }
    
    private MDMPayload getMdmPayload(final String organizationName, final String serverURL, final String checkInURL, final int deviceType, final int accessRights, final String identityCertificateUUID, final boolean sharedDevice) {
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getMdmPayload: DATA-IN: Identity Certificate UUID: {0}, Organization name: {1}", new Object[] { identityCertificateUUID, organizationName });
        final String topic = MDMUtil.getAPNSCertificateDetails().get("TOPIC");
        final MDMPayload mdmPayload = new MDMPayload(1, organizationName, PayloadIdentifierConstants.MDM_INSTALATION_PROFILE_CONFIGURATION_IDENTIFIER, "MDM Profile");
        mdmPayload.setAccessRights(accessRights);
        mdmPayload.setCheckInURL(checkInURL);
        mdmPayload.setServerURL(serverURL);
        mdmPayload.setTopic(topic);
        mdmPayload.setCheckOutWhenRemoved(true);
        mdmPayload.setUseDevelopmentAPNS(false);
        mdmPayload.setSignMessage(true);
        mdmPayload.setIdentifyCertificateUUID(identityCertificateUUID);
        if (deviceType == 3 || deviceType == 4 || sharedDevice) {
            IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getMdmPayload: Device type is laptop/desktop, so adding additional capabilities Identity certificate UUIS: {0}, device type: {1}", new Object[] { identityCertificateUUID, deviceType });
            final NSArray caps = new NSArray(2);
            caps.setValue(0, (Object)"com.apple.mdm.per-user-connections");
            caps.setValue(1, (Object)"com.apple.mdm.bootstraptoken");
            mdmPayload.setServerCapabilities(caps);
        }
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getMdmPayload: MDM payload creation successful identity certificate uuid: {0}, Organization name: {1}", new Object[] { identityCertificateUUID, organizationName });
        return mdmPayload;
    }
    
    private static CertificatePayload getServerCertificatePayload(final String organizationName) throws Exception {
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getServerCertificatePayload: Getting server certificate payload for organization: {0}", new Object[] { organizationName });
        String serverCertificateFilePath = SSLCertificateUtil.getInstance().getServerCertificateFilePath();
        if (CertificateHandler.getInstance().isSANOrEnterpriseCACertificate()) {
            IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getServerCertificatePayload: Server certificate contains SAN: {0}", new Object[] { organizationName });
            serverCertificateFilePath = SSLCertificateUtil.getInstance().getServerCACertificateFilePath();
        }
        final byte[] bytes = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(serverCertificateFilePath);
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        final X509Certificate serverCertificate = (X509Certificate)CertificateUtil.convertInputStreamToX509CertificateChain(bais)[0];
        final String certificateName = CertificateUtil.getCommonNameFromCertificateSubject(serverCertificate);
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getServerCertificatePayload: Server certificate name: {0}", new Object[] { certificateName });
        final CertificatePayload certPayload = new CertificatePayload(1, organizationName, PayloadIdentifierConstants.MDM_INSTALATION_PROFILE_ROOT_CERTIFICATE_IDENTIFIER, certificateName);
        certPayload.setPayloadType("com.apple.security.root");
        certPayload.setPayloadContent(serverCertificateFilePath);
        certPayload.getPayloadDict().put("PayloadDisplayName", (Object)certificateName);
        return certPayload;
    }
    
    private String getUrlForEnrollmentRequest(final String path, final long customerId, final long enrollmentRequestId, final String encapiKey) throws Exception {
        final String serverBaseURL = MDMEnrollmentUtil.getInstance().getServerBaseURL();
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getUrlForEnrollmentRequest: Constructing URL for enrollment request. Enrollment request id: {0}, Path: {1}, Server base url: {2}, Customer id: {3}", new Object[] { enrollmentRequestId, path, serverBaseURL, customerId });
        final UriBuilder uri = UriBuilder.fromUri(serverBaseURL);
        uri.path(path);
        uri.queryParam("customerId", new Object[] { customerId });
        uri.queryParam("erid", new Object[] { enrollmentRequestId });
        uri.queryParam("encapiKey", new Object[] { encapiKey });
        return uri.build(new Object[0]).toURL().toString();
    }
    
    private String getCheckinUrlPath(final int deviceType, final int apiKeyVersion, final boolean sharedDevice) {
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getCheckinUrlPath: Device type: {0}, Api key version: {1}", new Object[] { deviceType, apiKeyVersion });
        if (deviceType == 5) {
            return (apiKeyVersion == APIKey.VERSION_2_0) ? "/mdm/client/v1/appletvcheckin" : "/ioscheckin";
        }
        if (deviceType == 3 || deviceType == 4) {
            return (apiKeyVersion == APIKey.VERSION_2_0) ? "/mdm/client/v1/maccheckin" : "/ioscheckin";
        }
        if (deviceType == 2 && sharedDevice) {
            return (apiKeyVersion == APIKey.VERSION_2_0) ? "/mdm/client/v1/sharedipadcheckin" : "/ioscheckin";
        }
        return (apiKeyVersion == APIKey.VERSION_2_0) ? "/mdm/client/v1/ioscheckin" : "/ioscheckin";
    }
    
    private String getServerUrlPath(final int deviceType, final int apiKeyVersion, final boolean sharedDevice) {
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getServerUrlPath: Device type: {0}, Api key version: {1}", new Object[] { deviceType, apiKeyVersion });
        if (deviceType == 5) {
            return (apiKeyVersion == APIKey.VERSION_2_0) ? "/mdm/client/v1/appletvserver" : "/iosserver";
        }
        if (deviceType == 3 || deviceType == 4) {
            return (apiKeyVersion == APIKey.VERSION_2_0) ? "/mdm/client/v1/macserver" : "/iosserver";
        }
        if (deviceType == 2 && sharedDevice) {
            return (apiKeyVersion == APIKey.VERSION_2_0) ? "/mdm/client/v1/sharedipadserver" : "/iosserver";
        }
        return (apiKeyVersion == APIKey.VERSION_2_0) ? "/mdm/client/v1/iosserver" : "/iosserver";
    }
    
    public int getDeviceTypeFromServletPath(final String servletPath) {
        IOSMobileConfigHandler.logger.log(Level.INFO, "IOSMobileConfigHandler-getDeviceTypeFromServletPath: Servlet path: {0}", new Object[] { servletPath });
        if (servletPath.contains("appletv")) {
            return 5;
        }
        if (servletPath.contains("mac")) {
            return 3;
        }
        return 1;
    }
    
    static {
        IOSMobileConfigHandler.logger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
        IOSMobileConfigHandler.mobileConfigHandler = null;
    }
}
