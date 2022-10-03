package com.me.mdm.server.dep;

import java.util.Hashtable;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.persistence.Row;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.mdm.server.enrollment.notification.EnrollmentNotificationHandler;
import java.util.ArrayList;
import java.util.HashSet;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import org.json.JSONException;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.core.enrollment.OutOfBoxEnrollmentSettingsHandler;
import com.me.mdm.server.tracker.mics.MICSFeatureTrackerUtil;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import java.util.Properties;
import com.dd.plist.Base64;
import java.nio.charset.StandardCharsets;
import com.adventnet.sym.server.mdm.ios.payload.PayloadSigningFactory;
import com.me.mdm.server.enrollment.ios.IOSMobileConfigHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.ChecksumProvider;
import java.io.File;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.me.idps.core.util.DirectoryAttributeConstants;
import com.me.idps.core.util.DirectoryUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.me.mdm.core.ios.adep.ADEPAuthHandler;
import java.util.List;
import java.util.HashMap;
import java.util.Collection;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AdminEnrollmentHandler
{
    public static final Logger LOGGER;
    
    public JSONObject processMessage(final JSONObject requestJSON) throws Exception {
        final String messageType = String.valueOf(requestJSON.get("MsgRequestType"));
        if (messageType.equalsIgnoreCase("DEPDiscoverDomains")) {
            return this.processDiscoverMessage(requestJSON);
        }
        if (messageType.equalsIgnoreCase("DEPUserAuthenticate")) {
            return this.processAuthenticateMessage(requestJSON);
        }
        if (messageType.equalsIgnoreCase("DEPDeviceProvisioning")) {
            return this.processDeviceProvisioningMessage(requestJSON);
        }
        if (messageType.equalsIgnoreCase("DepWebViewSolicitation")) {
            return this.processAgentSolicitationMessage(requestJSON, 1);
        }
        if (messageType.equalsIgnoreCase("AdminEnrollAgentSolicitation")) {
            return this.processAgentSolicitationMessage(requestJSON, 2);
        }
        if (messageType.equalsIgnoreCase("ChromeEnrollAgentSolicitation")) {
            return this.processAgentSolicitationMessage(requestJSON, 4);
        }
        if (messageType.equalsIgnoreCase("WindowsAdminEnrollment")) {
            return this.processWindowsEnrollmentMessage(requestJSON);
        }
        throw new SyMException(15001, "UnSupported Message Type", (Throwable)null);
    }
    
    JSONObject processDiscoverMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("MsgResponseType", (Object)"DEPDiscoverDomainsResponse");
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final JSONObject msgResponseJSON = new JSONObject();
        final Long enrollmentRequestID = msgRequestJSON.optLong("EnrollmentRequestID", -1L);
        JSONArray domainArray = new JSONArray();
        if (enrollmentRequestID != -1L) {
            final HashMap userDetails = ManagedUserHandler.getInstance().getManagedUserDetailsForRequest(enrollmentRequestID);
            domainArray.put(userDetails.get("DOMAIN_NETBIOS_NAME"));
        }
        else {
            final List domainList = MDMEnrollmentUtil.getInstance().getDomainNames((Long)null);
            domainArray = new JSONArray((Collection)domainList);
        }
        msgResponseJSON.put("DomainNameList", (Object)domainArray);
        responseJSON.put("Status", (Object)"Acknowledged");
        responseJSON.put("MsgResponse", (Object)msgResponseJSON);
        return responseJSON;
    }
    
    JSONObject processAuthenticateMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("MsgResponseType", (Object)"DEPUserAuthenticateResponse");
        final JSONObject json = ADEPAuthHandler.getInstance().authenticate(msgRequestJSON);
        requestJSON.put("MsgRequest", (Object)msgRequestJSON);
        final String serialNum = String.valueOf(msgRequestJSON.get("SerialNumber"));
        final String imei = String.valueOf(msgRequestJSON.get("IMEI"));
        final String easID = msgRequestJSON.optString("EASID", "--");
        final JSONObject templateJSON = this.getDeviceEnrollmentInfo(msgRequestJSON, 1);
        final int templateType = templateJSON.optInt("TEMPLATE_TYPE", -1);
        final Long dfe = new DeviceForEnrollmentHandler().getDeviceForEnrollmentId(serialNum, imei, easID, templateType);
        if (dfe != null) {
            DataAccess.delete("DeviceEnrollmentToUser", new Criteria(Column.getColumn("DeviceEnrollmentToUser", "ENROLLMENT_DEVICE_ID"), (Object)dfe, 0));
        }
        if (templateJSON != null) {
            final Long enrollmentReqId = templateJSON.getLong("EnrollmentReqID");
            if (json != null && json.optString("Status", "Error").equalsIgnoreCase("Acknowledged")) {
                MDMEnrollmentRequestHandler.getInstance().updateEnrollmentStatusAndErrorCode(enrollmentReqId, 1, "dc.mdm.enroll.request_sent", -1);
                responseJSON.put("Status", (Object)"Acknowledged");
                return responseJSON;
            }
            final String sEventLogRemarks = "dc.mdm.enroll.AD_val_failed";
            MDMEnrollmentRequestHandler.getInstance().updateEnrollFailedStatus(enrollmentReqId, sEventLogRemarks, 12003);
        }
        responseJSON.put("Status", (Object)"Error");
        return responseJSON;
    }
    
    JSONObject processDeviceProvisioningMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("MsgResponseType", (Object)"DEPDeviceProvisioningResponse");
        final JSONObject msgResponseJSON = new JSONObject();
        final String platformStr = String.valueOf(requestJSON.get("DevicePlatform"));
        final int platform = this.getPlatformConstant(platformStr);
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final JSONObject templateJSON = this.getDeviceEnrollmentInfo(msgRequestJSON, 1);
        if (templateJSON == null) {
            responseJSON.put("Status", (Object)"Error");
            return responseJSON;
        }
        if (msgRequestJSON.has("UserName")) {
            final String userName = msgRequestJSON.getString("UserName");
            if (MDMUtil.getInstance().isValidEmail(userName)) {
                AdminEnrollmentHandler.LOGGER.log(Level.WARNING, "Fetching directory user properties for UPN since AD validation succeeded with UPN and password");
                final Long customerId = templateJSON.getLong("CustomerID");
                final Criteria custCri = new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerId, 0);
                final Criteria criteria = custCri.and(new Criteria(Column.getColumn("DirObjRegStrVal", "VALUE"), (Object)userName, 0, false)).and(new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)112L, 0));
                Properties properties = DirectoryUtil.getInstance().getObjectAttributes(criteria);
                AdminEnrollmentHandler.LOGGER.log(Level.INFO, "Obtained the following AD attr props : {0}", new Object[] { properties });
                if (properties != null && properties.containsKey("RESOURCE_ID")) {
                    final Long resID = Long.valueOf(String.valueOf(((Hashtable<K, Object>)properties).get("RESOURCE_ID")));
                    properties = DirectoryUtil.getInstance().getObjectAttributes(custCri.and(new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)resID, 0)));
                    msgRequestJSON.put("UserName", (Object)String.valueOf(((Hashtable<K, Object>)properties).get(DirectoryAttributeConstants.getAttrKey(Long.valueOf(2L)))));
                    requestJSON.put("MsgRequest", (Object)msgRequestJSON);
                }
                else {
                    AdminEnrollmentHandler.LOGGER.log(Level.WARNING, "Directory user not found for given UPN");
                }
            }
            this.assignUser(requestJSON, templateJSON);
        }
        int deviceType = 0;
        if (msgRequestJSON.opt("DeviceType") != null) {
            deviceType = Integer.parseInt(msgRequestJSON.opt("DeviceType").toString());
        }
        MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", templateJSON.getLong("CustomerID"));
        final Long enrollmentReqId = templateJSON.getLong("EnrollmentReqID");
        final Long customerId2 = templateJSON.getLong("CustomerID");
        if (platform == 2) {
            final String agentDirectory = DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "agent";
            final String apkFile = agentDirectory + File.separator + "MDMAndroidAgent.apk";
            final String checksum = ChecksumProvider.getInstance().GetMD5HashFromFile(apkFile);
            msgResponseJSON.put("Checksum", (Object)checksum);
            msgResponseJSON.put("DownloadURL", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getAgentDownloadUrl(2, 5));
            msgResponseJSON.put("AgentDownloadURL", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getAgentDownloadUrl(2, 2));
        }
        else if (platform == 1) {
            Logger.getLogger("MDMIosEnrollmentClientCertificateLogger").log(Level.INFO, "AdminEnrollmentHandler: Going to generate Mobile config for enrollment request Id: {0}", enrollmentReqId);
            byte[] mobileConfigBytes = IOSMobileConfigHandler.getInstance().generateMobileConfig(enrollmentReqId, deviceType, 5);
            mobileConfigBytes = PayloadSigningFactory.getInstance().signPayload(new String(mobileConfigBytes, StandardCharsets.UTF_8));
            final String encodeBytes = Base64.encodeBytes(mobileConfigBytes);
            msgResponseJSON.put("MobileConfigContent", (Object)encodeBytes);
        }
        responseJSON.put("Status", (Object)"Acknowledged");
        responseJSON.put("MsgResponse", (Object)msgResponseJSON);
        return responseJSON;
    }
    
    public JSONObject processDeviceProvisioningMessageForMacModernMgmt(final JSONObject requestJSON) throws Exception {
        return this.processDeviceProvisioningMessage(requestJSON);
    }
    
    public Long getEnrollmentRequest(final Long managedUserID, final JSONObject templateJSON, final int platform) {
        Long enrollmentRequestID = null;
        try {
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("MANAGED_USER_ID", managedUserID);
            ((Hashtable<String, Boolean>)properties).put("IS_SELF_ENROLLMENT", Boolean.FALSE);
            ((Hashtable<String, Integer>)properties).put("OWNED_BY", 1);
            ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", platform);
            ((Hashtable<String, Long>)properties).put("REQUESTED_TIME", System.currentTimeMillis());
            ((Hashtable<String, Integer>)properties).put("REQUEST_STATUS", 1);
            ((Hashtable<String, Long>)properties).put("USER_ID", templateJSON.getLong("ADDED_USER"));
            ((Hashtable<String, Integer>)properties).put("ENROLLMENT_TYPE", 3);
            ((Hashtable<String, Integer>)properties).put("AUTH_MODE", 4);
            enrollmentRequestID = MDMEnrollmentRequestHandler.getInstance().addOrUpdateEnrollmentRequest(properties);
            templateJSON.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
            new EnrollmentTemplateHandler().addOrUpdateEnrollmentRequestToTemplate(templateJSON);
        }
        catch (final Exception e) {
            AdminEnrollmentHandler.LOGGER.log(Level.SEVERE, "Exception while getEnrollmentRequest", e);
        }
        return enrollmentRequestID;
    }
    
    JSONObject constructAndroidMessage(final DeviceMessage deviceMsg) {
        final JSONObject response = new JSONObject();
        try {
            response.put("MessageType", (Object)deviceMsg.messageType);
            response.put("Status", (Object)deviceMsg.status);
            response.put("MessageResponse", (Object)deviceMsg.messageResponse);
        }
        catch (final Exception e) {
            AdminEnrollmentHandler.LOGGER.log(Level.SEVERE, "Exception while creating the response message", e);
        }
        return response;
    }
    
    private JSONObject constructIosMessage(final DeviceMessage deviceMsg) {
        final JSONObject response = new JSONObject();
        try {
            response.put("MessageType", (Object)deviceMsg.messageType);
            response.put("Status", (Object)deviceMsg.status);
            response.put("AutherzationURL", deviceMsg.messageResponse.get("AutherzationURL"));
        }
        catch (final Exception e) {
            AdminEnrollmentHandler.LOGGER.log(Level.SEVERE, "Exception while creating the response message", e);
        }
        return response;
    }
    
    private JSONObject processAgentSolicitationMessage(final JSONObject requestJSON, final int platform) throws Exception {
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("Message");
        final DeviceMessage responseMsg = new DeviceMessage();
        responseMsg.setMessageStatus("Acknowledged");
        responseMsg.setMessageType("AdminEnrollAgentSolicitation");
        MICSFeatureTrackerUtil.adminEnrollmentStart(msgRequestJSON, platform);
        JSONObject msgResponse;
        if (this.isAdminEnrollmentWithAD(msgRequestJSON)) {
            final Long deviceForEnrollId = OutOfBoxEnrollmentSettingsHandler.getInstance().getDeviceForEnrollmentId(requestJSON);
            msgRequestJSON.put("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollId);
            msgRequestJSON.put("PlatformType", platform);
            msgResponse = this.getEnrollAuthUrl(msgRequestJSON);
        }
        else {
            msgResponse = this.getDeviceEnrollmentInfo(msgRequestJSON, platform);
            if (msgResponse == null) {
                msgResponse = new JSONObject();
                responseMsg.setMessageStatus("Error");
                msgResponse.put("ErrorMsg", (Object)"Untrusted devices cannot be Enrolled");
                msgResponse.put("ErrorCode", 21001);
            }
            else {
                final JSONObject json = new JSONObject();
                if (msgResponse.has("EnrollmentReqID")) {
                    json.put("ENROLLMENT_REQUEST_ID", msgResponse.getLong("EnrollmentReqID"));
                    AdminEnrollmentHandler.LOGGER.log(Level.INFO, "Device already has an enrollment request ID");
                }
                json.put("decodeToken", true);
                if (requestJSON.optString("checkinURLVersion", (String)null) != null) {
                    json.put("checkinURLVersion", requestJSON.optInt("checkinURLVersion"));
                }
                final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(json);
                if (key != null && key.getVersion() == APIKey.VERSION_2_0) {
                    msgResponse.put("Services", (Object)key.toClientJSON());
                }
                MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", msgResponse.getLong("CustomerID"));
            }
        }
        responseMsg.setMessageResponseJSON(msgResponse);
        if (platform == 1) {
            return this.constructIosMessage(responseMsg);
        }
        return this.constructAndroidMessage(responseMsg);
    }
    
    private boolean isAdminEnrollmentWithAD(final JSONObject enrollJson) {
        boolean isADAuthEnabled = false;
        try {
            final String templateToken = String.valueOf(enrollJson.get("TemplateToken"));
            final EnrollmentTemplateHandler enrollmentTemplateHandler = new EnrollmentTemplateHandler();
            final JSONObject templateJSON = enrollmentTemplateHandler.getEnrollmentTemplateForTemplateToken(templateToken);
            if (templateJSON != null) {
                final int authMode = OutOfBoxEnrollmentSettingsHandler.getInstance().getAuthMode(templateJSON.getLong("TEMPLATE_ID"));
                if (authMode != 0) {
                    isADAuthEnabled = true;
                }
            }
        }
        catch (final JSONException exp) {
            AdminEnrollmentHandler.LOGGER.log(Level.SEVERE, "Excpetion occured while parsing JSON {0}", exp.toString());
        }
        return isADAuthEnabled;
    }
    
    private JSONObject getEnrollAuthUrl(final JSONObject enrollJSON) {
        JSONObject responseJSON = new JSONObject();
        try {
            final String templateToken = String.valueOf(enrollJSON.get("TemplateToken"));
            final Long deviceForEnrollId = enrollJSON.getLong("ENROLLMENT_DEVICE_ID");
            final int platform = enrollJSON.getInt("PlatformType");
            final EnrollmentTemplateHandler enrollmentTemplateHandler = new EnrollmentTemplateHandler();
            final JSONObject templateJSON = enrollmentTemplateHandler.getEnrollmentTemplateForTemplateToken(templateToken);
            if (templateJSON != null) {
                responseJSON = OutOfBoxEnrollmentSettingsHandler.getInstance().getAuthorizationUrl(templateJSON.getLong("TEMPLATE_ID"), deviceForEnrollId, platform);
            }
        }
        catch (final JSONException exp) {
            AdminEnrollmentHandler.LOGGER.log(Level.SEVERE, "Cannot fetch authorization URL ");
        }
        return responseJSON;
    }
    
    public JSONObject getDeviceEnrollmentInfo(final JSONObject jsonObject, final int platform) throws Exception {
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            final String imei = jsonObject.has("IMEI") ? String.valueOf(jsonObject.get("IMEI")) : null;
            final String serialNumber = String.valueOf(jsonObject.get("SerialNumber"));
            final String templateToken = String.valueOf(jsonObject.get("TemplateToken"));
            final String udid = jsonObject.optString("UDID", (String)null);
            final String easID = jsonObject.optString("EASID", "--");
            final Boolean allowDuplicateSerialNumber = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ALLOW_DUPLICATE_SERIAL_NUMBER");
            jsonObject.put("ALLOW_DUPLICATE_SERIAL_NUMBER", (Object)allowDuplicateSerialNumber);
            final Boolean isUntrustedDeviceAllowed = jsonObject.optBoolean("isUntrustedDeviceAllowed", true);
            JSONObject enrollJson = null;
            Long customerId = null;
            Long enrollmentRequestId = null;
            final EnrollmentTemplateHandler enrollmentTemplateHandler = new EnrollmentTemplateHandler();
            final JSONObject templateJSON = enrollmentTemplateHandler.getEnrollmentTemplateForTemplateToken(templateToken);
            if (templateJSON == null || templateJSON.length() == 0) {
                return enrollJson;
            }
            final int templateType = templateJSON.getInt("TEMPLATE_TYPE");
            jsonObject.put("template_type", templateType);
            final DeviceForEnrollmentHandler deviceForEnrollmentHandler = new DeviceForEnrollmentHandler();
            Long deviceForEnrollmentId = null;
            if (!MDMStringUtils.isEmpty(imei) || !MDMStringUtils.isEmpty(serialNumber) || !MDMStringUtils.isEmpty(udid) || !MDMStringUtils.isEmpty(easID)) {
                deviceForEnrollmentId = deviceForEnrollmentHandler.getDeviceForEnrollmentId(jsonObject);
            }
            if (deviceForEnrollmentId != null) {
                customerId = (Long)DBUtil.getValueFromDB("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId, "CUSTOMER_ID");
                final AdminEnrollmentHandler depHandler = new AdminEnrollmentHandler();
                enrollmentRequestId = deviceForEnrollmentHandler.getAssociatedEnrollmentRequestid(deviceForEnrollmentId);
                if (enrollmentRequestId == null) {
                    Long managedUserId = deviceForEnrollmentHandler.getAssociatedUserid(deviceForEnrollmentId);
                    if (managedUserId == null) {
                        managedUserId = this.getManagedUserID(templateJSON);
                    }
                    enrollmentRequestId = depHandler.getEnrollmentRequest(managedUserId, templateJSON, platform);
                    final HashSet<Long> groupIdList = new HashSet<Long>(deviceForEnrollmentHandler.getAssociatedGroupId(deviceForEnrollmentId));
                    groupIdList.addAll((Collection<?>)EnrollmentTemplateHandler.getDefaultGroupIDForTemplate(templateJSON.getLong("TEMPLATE_ID")));
                    if (groupIdList != null && !groupIdList.isEmpty()) {
                        new DeviceForEnrollmentHandler().addOrUpdateGroupForDevice(deviceForEnrollmentId, new ArrayList<Long>(groupIdList));
                    }
                }
                else {
                    final JSONObject enrollmentProps = new JSONObject();
                    enrollmentProps.put("UDID", (Object)"");
                    enrollmentProps.put("REQUEST_STATUS", 1);
                    enrollmentProps.put("REMARKS", (Object)"dc.mdm.enroll.request_sent");
                    if (templateType == 10) {
                        enrollmentProps.put("ENROLLMENT_TYPE", 3);
                    }
                    MDMEnrollmentRequestHandler.getInstance().updateEnrollmentRequestProperties(enrollmentRequestId, enrollmentProps);
                    EnrollmentNotificationHandler.getInstance().removeNotification(enrollmentRequestId);
                    EnrollmentTemplateHandler.updateUdidOnReEnroll(enrollmentRequestId, udid);
                }
                final HashMap userDetail = ManagedUserHandler.getInstance().getManagedUserDetailsForRequest(enrollmentRequestId);
                enrollJson = new JSONObject();
                enrollJson.put("EnrollmentReqID", (Object)enrollmentRequestId);
                enrollJson.put("CustomerID", (Object)customerId);
                enrollJson.put("UserName", userDetail.get("NAME"));
                enrollJson.put("EmailAddress", userDetail.get("EMAIL_ADDRESS"));
                enrollJson.put("ManagedUserId", userDetail.get("MANAGED_USER_ID"));
                final JSONObject enrollmentTemplateRequestJSON = new JSONObject();
                enrollmentTemplateRequestJSON.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestId);
                enrollmentTemplateRequestJSON.put("TEMPLATE_TOKEN", (Object)templateToken);
                enrollmentTemplateHandler.addOrUpdateEnrollmentRequestToTemplate(enrollmentTemplateRequestJSON);
            }
            else if (isUntrustedDeviceAllowed) {
                DMSecurityLogger.info(AdminEnrollmentHandler.LOGGER, "AdminEnrollmentHandler", "getDeviceEnrollmentInfo", "No entry in device for enrollment going to add entry for request : {0}", (Object)jsonObject);
                if (jsonObject.has("EnrolledUserDetails")) {
                    templateJSON.put("EnrolledUserDetails", (Object)jsonObject.getJSONObject("EnrolledUserDetails"));
                }
                final Long managedUserId2 = this.getManagedUserID(templateJSON);
                customerId = templateJSON.getLong("CUSTOMER_ID");
                final AdminEnrollmentHandler depHandler2 = new AdminEnrollmentHandler();
                enrollmentRequestId = depHandler2.getEnrollmentRequest(managedUserId2, templateJSON, platform);
                final HashMap userDetail2 = ManagedUserHandler.getInstance().getManagedUserDetails(managedUserId2);
                enrollJson = new JSONObject();
                enrollJson.put("EnrollmentReqID", (Object)enrollmentRequestId);
                enrollJson.put("CustomerID", (Object)customerId);
                enrollJson.put("UserName", userDetail2.get("NAME"));
                enrollJson.put("EmailAddress", userDetail2.get("EMAIL_ADDRESS"));
                enrollJson.put("ManagedUserId", (Object)managedUserId2);
                final JSONObject enrollmentTemplateRequestJSON2 = new JSONObject();
                enrollmentTemplateRequestJSON2.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestId);
                enrollmentTemplateRequestJSON2.put("TEMPLATE_TOKEN", (Object)templateToken);
                enrollmentTemplateHandler.addOrUpdateEnrollmentRequestToTemplate(enrollmentTemplateRequestJSON2);
                final JSONObject deviceJSON = new JSONObject();
                if (!MDMStringUtils.isEmpty(imei)) {
                    deviceJSON.put("IMEI", (Object)imei);
                }
                if (!MDMStringUtils.isEmpty(serialNumber)) {
                    deviceJSON.put("SerialNumber", (Object)serialNumber);
                }
                if (!MDMStringUtils.isEmpty(udid)) {
                    deviceJSON.put("UDID", (Object)udid);
                }
                if (!MDMStringUtils.isEmpty(easID)) {
                    deviceJSON.put("EASID", (Object)easID);
                }
                deviceJSON.put("CustomerId", (Object)customerId);
                deviceJSON.put("TEMPLATE_TOKEN", (Object)templateToken);
                deviceJSON.put("MANAGED_USER_ID", (Object)managedUserId2);
                deviceJSON.put("DeviceType", jsonObject.opt("DeviceType"));
                if (deviceJSON.has("IMEI") || deviceJSON.has("SerialNumber") || deviceJSON.has("UDID") || deviceJSON.has("EASID")) {
                    deviceForEnrollmentId = new DeviceForEnrollmentHandler().addDeviceForEnrollment(deviceJSON, templateJSON.getInt("TEMPLATE_TYPE"));
                }
                final HashSet<Long> groupIdList2 = new HashSet<Long>(deviceForEnrollmentHandler.getAssociatedGroupId(deviceForEnrollmentId));
                groupIdList2.addAll((Collection<?>)EnrollmentTemplateHandler.getDefaultGroupIDForTemplate(templateJSON.getLong("TEMPLATE_ID")));
                if (groupIdList2 != null && !groupIdList2.isEmpty() && deviceForEnrollmentId != null) {
                    new DeviceForEnrollmentHandler().addOrUpdateGroupForDevice(deviceForEnrollmentId, new ArrayList<Long>(groupIdList2));
                }
            }
            else {
                AdminEnrollmentHandler.LOGGER.log(Level.WARNING, "Untrusted device is not allowed for enrollment , following request is rejected to add entry in DeviceForEnrollment: {0}", jsonObject);
            }
            if (deviceForEnrollmentId != null && enrollmentRequestId != null) {
                new DeviceForEnrollmentHandler().addOrUpdateRequestForDevice(deviceForEnrollmentId, enrollmentRequestId);
                enrollJson.put("ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
            }
            enrollJson.put("TEMPLATE_ID", templateJSON.optLong("TEMPLATE_ID", -1L));
            return enrollJson;
        }
        catch (final Exception e) {
            AdminEnrollmentHandler.LOGGER.log(Level.SEVERE, "Exception in getDeviceEnrollmentInfo() ", e);
            throw e;
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
    }
    
    private int getPlatformConstant(final String platformValue) {
        int platform = -1;
        if (platformValue != null) {
            if (platformValue.equalsIgnoreCase("ios")) {
                platform = 1;
            }
            else if (platformValue.equalsIgnoreCase("android")) {
                platform = 2;
            }
            else if (platformValue.equalsIgnoreCase("windowsphone")) {
                platform = 3;
            }
        }
        return platform;
    }
    
    private Long getManagedUserID(final JSONObject templateJSON) throws Exception {
        Long managedUserId = null;
        JSONObject userDetails = null;
        final JSONObject enrolledUserDetails = templateJSON.optJSONObject("EnrolledUserDetails");
        String email = null;
        String domainName = null;
        if (enrolledUserDetails == null) {
            final Properties userInfo = DMUserHandler.getContactInfoProp((Long)templateJSON.get("ADDED_USER"));
            if (userInfo.containsKey("EMAIL_ID")) {
                email = userInfo.getProperty("EMAIL_ID");
            }
        }
        else {
            email = String.valueOf(enrolledUserDetails.get("EMAIL_ADDRESS"));
        }
        final JSONObject expectedUserJSON = new JSONObject();
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            expectedUserJSON.put("NAME", (Object)email.split("@")[0]);
            expectedUserJSON.put("USER_IDENTIFIER", (Object)"EMAIL_ADDRESS");
            expectedUserJSON.put("DOMAIN_NETBIOS_NAME", (Object)"MDM");
            domainName = "MDM";
        }
        else {
            if (enrolledUserDetails == null) {
                final Row row = DBUtil.getRowFromDB("AaaLogin", "USER_ID", (Object)templateJSON.get("ADDED_USER"));
                expectedUserJSON.put("NAME", row.get("NAME"));
                domainName = (String)row.get("DOMAINNAME");
            }
            else {
                expectedUserJSON.put("NAME", (Object)String.valueOf(enrolledUserDetails.get("NAME")));
                domainName = String.valueOf(enrolledUserDetails.get("DOMAIN_NETBIOS_NAME"));
            }
            domainName = (MDMUtil.getInstance().isEmpty(domainName) ? "MDM" : domainName);
            expectedUserJSON.put("DOMAIN_NETBIOS_NAME", (Object)domainName);
            expectedUserJSON.put("USER_IDENTIFIER", (Object)"NAME");
        }
        expectedUserJSON.put("EMAIL_ADDRESS", (Object)email);
        expectedUserJSON.put("CUSTOMER_ID", (Object)templateJSON.get("CUSTOMER_ID"));
        userDetails = ManagedUserHandler.getInstance().getManagedUserDetails(expectedUserJSON);
        if ((userDetails == null || userDetails.length() == 0) && !"MDM".equalsIgnoreCase(domainName)) {
            final List propList = new ArrayList();
            propList.add("sAMAccountName");
            propList.add("mail");
            Properties aduser = null;
            try {
                aduser = IdpsFactoryProvider.getIdpsAccessAPI(domainName, CustomerInfoUtil.getInstance().getCustomerId()).getThisADObjectProperties(domainName, 2, propList, String.valueOf(expectedUserJSON.get("NAME")), CustomerInfoUtil.getInstance().getDefaultCustomer());
            }
            catch (final Exception ex) {
                Logger.getLogger(ManagedUserHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (aduser != null) {
                if (aduser.contains("mail") && MDMUtil.getInstance().isValidEmail(aduser.getProperty("mail"))) {
                    expectedUserJSON.put("EMAIL_ADDRESS", (Object)aduser.getProperty("mail"));
                }
                if (aduser.contains("sAMAccountName") && !MDMStringUtils.isEmpty(aduser.getProperty("sAMAccountName"))) {
                    expectedUserJSON.put("NAME", (Object)aduser.getProperty("sAMAccountName"));
                }
                else if (aduser.contains("userPrincipalName") && !MDMStringUtils.isEmpty(aduser.getProperty("userPrincipalName"))) {
                    expectedUserJSON.put("NAME", (Object)aduser.getProperty("userPrincipalName"));
                }
                expectedUserJSON.put("DOMAIN_NETBIOS_NAME", (Object)domainName);
            }
        }
        if (userDetails != null && userDetails.length() != 0) {
            managedUserId = userDetails.getLong("MANAGED_USER_ID");
        }
        else {
            managedUserId = ManagedUserHandler.getInstance().addOrUpdateAndGetUserId(expectedUserJSON);
        }
        return managedUserId;
    }
    
    private void assignUser(final JSONObject requestJSON, final JSONObject templateJSON) throws Exception {
        final JSONObject assignJSON = new JSONObject();
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        assignJSON.put("IMEI", (Object)String.valueOf(msgRequestJSON.get("IMEI")));
        assignJSON.put("SerialNumber", (Object)String.valueOf(msgRequestJSON.get("SerialNumber")));
        assignJSON.put("UserName", (Object)String.valueOf(msgRequestJSON.get("UserName")));
        assignJSON.put("Password", (Object)String.valueOf(msgRequestJSON.get("Password")));
        assignJSON.put("DomainName", (Object)String.valueOf(msgRequestJSON.get("DomainName")));
        assignJSON.put("Email", (Object)"");
        final int templateType = templateJSON.optInt("TEMPLATE_TYPE", -1);
        final Long dfe = new DeviceForEnrollmentHandler().getDeviceForEnrollmentId(String.valueOf(msgRequestJSON.get("SerialNumber")), String.valueOf(msgRequestJSON.get("IMEI")), String.valueOf(msgRequestJSON.get("EASID")), templateType);
        ADEPAuthHandler.getInstance().getAuthenticatedUserDetails(assignJSON);
        final List<Long> defaultMDMGroupIdList = EnrollmentTemplateHandler.getDefaultGroupIDForTemplate(templateJSON.getLong("TEMPLATE_ID"));
        if (defaultMDMGroupIdList.size() == 0) {
            final Long gId = MDMGroupHandler.getInstance().getDefaultMDMSelfEnrollGroupId(templateJSON.getLong("CustomerID"), 1, 1);
            if (gId != null) {
                defaultMDMGroupIdList.add(gId);
            }
        }
        final Properties props = new Properties();
        ((Hashtable<String, Object>)props).put("NAME", assignJSON.get("UserName"));
        ((Hashtable<String, Object>)props).put("DOMAIN_NETBIOS_NAME", assignJSON.get("DomainName"));
        ((Hashtable<String, Long>)props).put("CUSTOMER_ID", templateJSON.getLong("CustomerID"));
        if (assignJSON.has("Email") && !MDMStringUtils.isEmpty(String.valueOf(assignJSON.get("Email")))) {
            ((Hashtable<String, Object>)props).put("EMAIL_ADDRESS", assignJSON.get("Email"));
        }
        else {
            ((Hashtable<String, String>)props).put("EMAIL_ADDRESS", "--");
        }
        if (assignJSON.has("FIRST_NAME") && !MDMStringUtils.isEmpty(String.valueOf(assignJSON.get("FIRST_NAME")))) {
            ((Hashtable<String, Object>)props).put("FIRST_NAME", assignJSON.get("FIRST_NAME"));
        }
        if (assignJSON.has("MIDDLE_NAME") && !MDMStringUtils.isEmpty(String.valueOf(assignJSON.get("MIDDLE_NAME")))) {
            ((Hashtable<String, Object>)props).put("MIDDLE_NAME", assignJSON.get("MIDDLE_NAME"));
        }
        if (assignJSON.has("LAST_NAME") && !MDMStringUtils.isEmpty(String.valueOf(assignJSON.get("LAST_NAME")))) {
            ((Hashtable<String, Object>)props).put("LAST_NAME", assignJSON.get("LAST_NAME"));
        }
        if (assignJSON.has("DISPLAY_NAME") && !MDMStringUtils.isEmpty(String.valueOf(assignJSON.get("DISPLAY_NAME")))) {
            ((Hashtable<String, Object>)props).put("DISPLAY_NAME", assignJSON.get("DISPLAY_NAME"));
        }
        final Long muid = ManagedUserHandler.getInstance().addOrUpdateAndGetUserId(props);
        final Long enrollmentReqId = templateJSON.getLong("EnrollmentReqID");
        final Long oldmuid = ManagedUserHandler.getInstance().getManagedUserDetailsForRequest(enrollmentReqId).get("MANAGED_USER_ID");
        final UpdateQuery uquery = (UpdateQuery)new UpdateQueryImpl("DeviceEnrollmentRequest");
        uquery.setUpdateColumn("MANAGED_USER_ID", (Object)muid);
        uquery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentReqId, 0));
        MDMUtil.getPersistence().update(uquery);
        new DeviceForEnrollmentHandler().addOrUpdateUserForDevice(dfe, muid);
        new DeviceForEnrollmentHandler().addOrUpdateGroupForDevice(dfe, defaultMDMGroupIdList);
    }
    
    private JSONObject processWindowsEnrollmentMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("MsgResponseType", (Object)"WindowsAdminEnrollment");
        responseJSON.put("Status", (Object)"Acknowledged");
        final JSONObject msgResponse = this.getDeviceEnrollmentInfo(msgRequestJSON, 3);
        MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", msgResponse.getLong("CustomerID"));
        if (msgResponse == null) {
            responseJSON.put("Status", (Object)"Error");
            msgResponse.put("ErrorMsg", (Object)"Untrusted devices cannot be Enrolled");
            msgResponse.put("ErrorCode", 21001);
        }
        else if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AutoAssignUserForAzureAD") && msgRequestJSON.optInt("TEMPLATE_TYPE", -1) == 32) {
            final Long managedUserID = (Long)msgResponse.opt("ManagedUserId");
            final Long defID = (Long)msgResponse.opt("ENROLLMENT_DEVICE_ID");
            if (managedUserID != null && defID != null) {
                new DeviceForEnrollmentHandler().addOrUpdateUserForDevice(defID, managedUserID);
            }
        }
        responseJSON.put("MsgResponse", (Object)msgResponse);
        return responseJSON;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMEnrollment");
    }
}
