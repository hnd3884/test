package com.adventnet.sym.webclient.mdm;

import java.util.Hashtable;
import com.me.mdm.server.enrollment.ios.MobileConfigUpgradeHandler;
import com.adventnet.sym.server.mdm.ios.APNSImpl;
import com.me.mdm.server.enrollment.ios.MDMProfileInstallationHandler;
import com.me.mdm.server.adep.DeviceConfiguredCommandHandler;
import java.util.HashMap;
import com.me.mdm.agent.servlets.ios.MacCheckInServlet;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.ArrayList;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.Properties;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.MDMEntrollment;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import java.util.Map;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.me.mdm.server.util.MDMSecurityLogger;
import java.util.logging.Level;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.DeviceAuthenticatedRequestServlet;

public class IOSCheckInServlet extends DeviceAuthenticatedRequestServlet
{
    public Logger logger;
    public Logger checkinLogger;
    protected String className;
    
    public IOSCheckInServlet() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.checkinLogger = Logger.getLogger("MDMCheckinLogger");
        this.className = "IOSCheckInServlet";
    }
    
    public void doPut(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.processRequest(request, response, deviceRequest);
    }
    
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response, DeviceRequest deviceRequest) {
        this.logger.log(Level.INFO, "{0} => (PUT) Received request from APPLE ", this.className);
        String strUDID = null;
        try {
            String strData = null;
            if (deviceRequest == null) {
                this.logger.log(Level.WARNING, "Device Request null in {0}", IOSCheckInServlet.class.getName());
                deviceRequest = this.prepareDeviceRequest(request, this.logger);
            }
            strData = (String)deviceRequest.deviceRequestData;
            this.logger.log(Level.INFO, "============================================================================");
            MDMSecurityLogger.info(this.logger, this.className, "processRequest", "IOSCheckInServlet Received Data: {0}", strData);
            this.logger.log(Level.INFO, "============================================================================");
            String responseData = null;
            final HashMap hashPlist = PlistWrapper.getInstance().getHashFromPlist(strData);
            final int serverVersion = MDMDeviceAPIKeyGenerator.getInstance().isClientVersion2_0(request.getServletPath()) ? APIKey.VERSION_2_0 : APIKey.VERSION_1_0;
            hashPlist.putAll(MDMDeviceAPIKeyGenerator.getInstance().fetchAPIKeyDetails(serverVersion, this.getParameterValueMap(request)));
            final String sEnrollmentRequestIDStr = request.getParameter("erid");
            hashPlist.put("ENROLLMENT_REQUEST_ID", sEnrollmentRequestIDStr);
            final Long enrollmentRequestId = Long.parseLong(sEnrollmentRequestIDStr);
            final String isAppleConfig = request.getParameter("isAppleConfig");
            final long customerIdFromRequestQueryParam = Long.parseLong(request.getParameter("customerId"));
            this.logger.log(Level.INFO, "Customer Id obtained from request query param is : {0}, Erid: {1}", new Object[] { customerIdFromRequestQueryParam, enrollmentRequestId });
            long customerId = customerIdFromRequestQueryParam;
            if (enrollmentRequestId != null) {
                customerId = MDMiOSEntrollmentUtil.getInstance().optCustomerIdForErid(enrollmentRequestId, customerIdFromRequestQueryParam);
            }
            this.logger.log(Level.INFO, "Customer Id obtained for erid {0} is : {1}", new Object[] { enrollmentRequestId, customerId });
            final String messageType = hashPlist.get("MessageType");
            if (messageType != null && messageType.equalsIgnoreCase("Authenticate")) {
                this.logger.log(Level.INFO, "MessageType: Authenticate");
                strUDID = hashPlist.get("UDID");
                this.checkinLogger.log(Level.INFO, "IOS MessageType:{0} Erid:{1} Udid:{2}", new Object[] { messageType, sEnrollmentRequestIDStr, strUDID });
                final int enrollStatus = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestStatus(enrollmentRequestId, strUDID);
                if (enrollStatus != 3) {
                    this.checkinLogger.log(Level.INFO, "IOS MessageType:{0}. Enrollment status:{1}. Adding details into IOS enrollment temp table.", new Object[] { messageType, enrollStatus });
                    MDMEntrollment.getInstance().enrolliOSDevice(hashPlist);
                }
                if (enrollmentRequestId != null) {
                    final JSONObject enrollmentProps = new JSONObject();
                    strUDID = hashPlist.get("UDID");
                    enrollmentProps.put("UDID", (Object)strUDID);
                    MDMEnrollmentRequestHandler.getInstance().updateEnrollmentRequestProperties(enrollmentRequestId, enrollmentProps);
                }
                MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", customerId);
                responseData = PlistWrapper.getInstance().getEmptyDict();
                response.setContentType("application/x-plist");
                response.getWriter().write(responseData);
                this.logger.log(Level.INFO, "Sending Empty plist as Response data for Authenticate : {0}", responseData);
            }
            else if (messageType != null && messageType.equalsIgnoreCase("TokenUpdate")) {
                this.handleTokenUpdateMessage(hashPlist, strData, enrollmentRequestId, isAppleConfig, messageType);
                responseData = PlistWrapper.getInstance().getEmptyDict();
                response.setContentType("application/x-plist");
                response.getWriter().write(responseData);
                this.logger.log(Level.INFO, "Sending Empty plist as response data for TokenUpdate: {0}", responseData);
            }
            else if (messageType != null && messageType.equalsIgnoreCase("CheckOut")) {
                this.logger.log(Level.INFO, "MessageType: CheckOut");
                strUDID = hashPlist.get("UDID");
                this.checkinLogger.log(Level.INFO, "IOS MessageType:{0} Udid:{1}", new Object[] { messageType, strUDID });
                final Properties properties = new Properties();
                String sRemarks = null;
                ((Hashtable<String, String>)properties).put("UDID", strUDID);
                final Integer status = ManagedDeviceHandler.getInstance().getManagedDeviceStatus(strUDID);
                final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
                final int ownedby = ManagedDeviceHandler.getInstance().getDeviceOwnership(resourceID);
                Boolean isDeviceUnmanaged = false;
                Integer managedstatus = 10;
                if (status == null || status != 4) {
                    sRemarks = "dc.mdm.profile.ios.remarks.removed_from_device";
                    managedstatus = 4;
                    isDeviceUnmanaged = true;
                }
                if (InventoryUtil.getInstance().isWipedFromServer(strUDID)) {
                    ((Hashtable<String, Boolean>)properties).put("WipeCmdFromServer", true);
                    if (ownedby == 1) {
                        sRemarks = "mdm.deprovision.old_remark";
                        managedstatus = 10;
                    }
                    else {
                        sRemarks = "mdm.deprovision.retire_remark";
                        managedstatus = 11;
                    }
                    final String cmdRemarks = "dc.mdm.general.command.succeeded";
                    final Long commandId = DeviceCommandRepository.getInstance().getCommandID("CorporateWipe");
                    final JSONObject statusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                    statusJSON.put("COMMAND_STATUS", 2);
                    statusJSON.put("REMARKS", (Object)cmdRemarks);
                    new CommandStatusHandler().populateCommandStatus(statusJSON);
                }
                ManagedDeviceHandler.getInstance().removeResourceAssociationsOnUnmanage(resourceID);
                int managedStatus = -1;
                String deprovisionRemarks = "";
                final JSONObject json = ManagedDeviceHandler.getInstance().getDeprovisiondetails(resourceID);
                if (json != null) {
                    managedStatus = json.optInt("MANAGED_STATUS", -1);
                    deprovisionRemarks = json.optString("REMARKS", "");
                }
                if (json != null && managedStatus != -1 && deprovisionRemarks != null && !deprovisionRemarks.equals("")) {
                    ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", managedStatus);
                    ((Hashtable<String, String>)properties).put("REMARKS", deprovisionRemarks);
                    ((Hashtable<String, Boolean>)properties).put("WipeCmdFromServer", true);
                }
                else {
                    ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", managedstatus);
                    ((Hashtable<String, String>)properties).put("REMARKS", sRemarks);
                }
                ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", 1);
                if (sEnrollmentRequestIDStr != null) {
                    ((Hashtable<String, Long>)properties).put("ENROLLMENT_REQUEST_ID", Long.valueOf(sEnrollmentRequestIDStr));
                }
                ((Hashtable<String, Long>)properties).put("UNREGISTERED_TIME", System.currentTimeMillis());
                if (ManagedDeviceHandler.getInstance().isDeviceRemoved(resourceID)) {
                    DeviceCommandRepository.getInstance().clearCommandFromDevice(strUDID, resourceID, "RemoveDevice", 1);
                    if (!DeviceCommandRepository.getInstance().hasDeviceCommandInCacheOrRepo(strUDID)) {
                        ManagedDeviceHandler.getInstance().removeDeviceInTrash(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID));
                    }
                }
                else {
                    ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(properties);
                    final JSONObject deprovisionJson = new JSONObject();
                    deprovisionJson.put("RESOURCE_ID", (Object)resourceID);
                    deprovisionJson.put("WIPE_PENDING", (Object)Boolean.FALSE);
                    ManagedDeviceHandler.getInstance().updatedeprovisionhistory(deprovisionJson);
                    if (isDeviceUnmanaged) {
                        final List remarksList = new ArrayList();
                        remarksList.add(ManagedDeviceHandler.getInstance().getDeviceName(resourceID));
                        MDMEventLogHandler.getInstance().addEvent(2001, null, "mdm.unmanage.user_revoke_management", remarksList, customerId, System.currentTimeMillis());
                        final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                        logJSON.put((Object)"REMARKS", (Object)"deprovision-success");
                        logJSON.put((Object)"RESOURCE_ID", (Object)resourceID);
                        logJSON.put((Object)"UDID", (Object)strUDID);
                        MDMOneLineLogger.log(Level.INFO, "DEVICE_UNMANAGED", logJSON);
                    }
                }
            }
            else if (messageType != null && (messageType.equalsIgnoreCase("GetBootstrapToken") || messageType.equalsIgnoreCase("SetBootstrapToken"))) {
                this.logger.log(Level.INFO, "MessageType={0}, customerID={1}, udid={2} ", new Object[] { messageType, customerId, hashPlist.get("UDID") });
                hashPlist.put("CUSTOMER_ID", customerId);
                new MacCheckInServlet().processMacRequest(response, strData, hashPlist);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> this.className + " => (PUT) Exception occured : {0}");
            try {
                response.sendError(404);
            }
            catch (final IOException e) {
                this.logger.log(Level.WARNING, "IOSCheckInServlet => (PUT) Exception occured while sending 404 : {0}", ex);
            }
        }
    }
    
    protected void handleTokenUpdateMessage(final HashMap hashPlist, final String strData, final Long enrollmentRequestId, final String isAppleConfig, final String messageType) throws Exception {
        final HashMap hsmap = new HashMap();
        final String strUDID = hashPlist.get("UDID");
        final String strTopic = hashPlist.get("Topic");
        final String strPushMagic = hashPlist.get("PushMagic");
        final String strToken = PlistWrapper.getInstance().getValueForKeyData("Token", strData);
        final String strUnLockToken = PlistWrapper.getInstance().getValueForKeyData("UnlockToken", strData);
        final String strDecodedToken = PlistWrapper.getInstance().getDecodedBase64HexValue(strToken);
        this.checkinLogger.log(Level.INFO, "{3} IOS MessageType:{0} UDID:{1} PushMagic:{2}", new Object[] { messageType, strUDID, strPushMagic, this.className });
        if (strUnLockToken == null) {
            this.logger.log(Level.INFO, "{0} => unlock Token is null", this.className);
        }
        hsmap.put("MessageType", "TokenUpdate");
        hsmap.put("UDID", strUDID);
        hsmap.put("Topic", strTopic);
        hsmap.put("DeviceToken", strDecodedToken);
        hsmap.put("PushMagic", strPushMagic);
        hsmap.put("UnlockToken", strUnLockToken);
        this.logger.log(Level.INFO, "MessageType: TokeUpdate");
        this.logger.log(Level.INFO, "{1} => UDID :{0}", new Object[] { strUDID, this.className });
        this.logger.log(Level.INFO, "{1} => Topic :{0}", new Object[] { strTopic, this.className });
        this.logger.log(Level.FINEST, "{1} => Token :{0}", new Object[] { strDecodedToken, this.className });
        this.logger.log(Level.INFO, "{1} => PushMagic :{0}", new Object[] { strPushMagic, this.className });
        this.logger.log(Level.FINEST, "{1} => UnLockToken :{0}", new Object[] { strUnLockToken, this.className });
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
        final int enrollmentStatus = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestStatus(enrollmentRequestId, strUDID);
        this.logger.log(Level.INFO, "{1} => enrollmentStatus :{0}", new Object[] { enrollmentStatus, this.className });
        if (resourceID != null && enrollmentStatus == 3) {
            MDMEntrollment.getInstance().updateEnrollmentDetails(resourceID, hsmap);
            this.logger.log(Level.WARNING, "Updated token update for resource : {0} ", resourceID);
        }
        else {
            MDMEntrollment.getInstance().enrolliOSDevice(hsmap);
            this.logger.log(Level.WARNING, "Added token update in temp for resource : {0} ", resourceID);
        }
        final boolean isEnrollmentCommandAllowed = this.isEnrollmentCommandAllowed(enrollmentRequestId, isAppleConfig, strUDID, enrollmentStatus);
        if (isEnrollmentCommandAllowed) {
            DeviceCommandRepository.getInstance().removeAllCommandsForResource(resourceID, strUDID);
            if (resourceID != null) {
                DeviceCommandRepository.getInstance().addEnrollmentCommand(resourceID);
            }
            else {
                DeviceCommandRepository.getInstance().addEnrollmentCommand(strUDID);
            }
            if (hashPlist.containsKey("AwaitingConfiguration")) {
                final boolean isAwaitingConfiguration = Boolean.parseBoolean(hashPlist.get("AwaitingConfiguration"));
                if (isAwaitingConfiguration) {
                    DeviceConfiguredCommandHandler.getInstance().addDeviceConfiguredCommand(strUDID);
                }
            }
            MDMProfileInstallationHandler.getInstance().addOrUpdateProfileInstallationStatus(enrollmentRequestId, Boolean.TRUE);
            final HashMap hsAdditionalParams = new HashMap();
            hsAdditionalParams.put("ENROLLMENT_REQUEST_ID", enrollmentRequestId);
            hsAdditionalParams.put("IS_SOURCE_TOKEN_UPDATE", true);
            hsAdditionalParams.put("RESOURCE_ID", resourceID);
            APNSImpl.getInstance().wakeUpDeviceWithERID(strDecodedToken, strPushMagic, strTopic, hsAdditionalParams);
        }
        MDMiOSEntrollmentUtil.getInstance().removeReenrollReq(enrollmentRequestId);
        MobileConfigUpgradeHandler.getInstance().removeMobileConfigUpgradeRequest(strUDID);
    }
    
    protected void checkAndAddInitialCmds(final Long resourceID, final String strUDID, final boolean isAwaitingConfiguration) {
        DeviceCommandRepository.getInstance().removeAllCommandsForResource(resourceID, strUDID);
        if (resourceID != null) {
            DeviceCommandRepository.getInstance().addEnrollmentCommand(resourceID);
        }
        else {
            DeviceCommandRepository.getInstance().addEnrollmentCommand(strUDID);
        }
        if (isAwaitingConfiguration) {
            if (resourceID != null) {
                DeviceConfiguredCommandHandler.getInstance().addDeviceConfiguredCommand(resourceID);
            }
            else {
                DeviceConfiguredCommandHandler.getInstance().addDeviceConfiguredCommand(strUDID);
            }
        }
    }
    
    protected int getManagedDeviceStatus(final String strUDID) {
        int managedDeviceStatus = -1;
        try {
            managedDeviceStatus = ManagedDeviceHandler.getInstance().getManagedDeviceStatus(strUDID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "Exception while getting managed device status for UDID: " + s);
        }
        return managedDeviceStatus;
    }
    
    protected boolean isEnrollmentCommandAllowed(final Long enrollmentRequestId, final String isAppleConfig, final String strUDID, final int enrollmentRequestStatus) throws Exception {
        final boolean isEnrollmentRequestYetToBeUsedOrFailed = enrollmentRequestStatus == 1 || enrollmentRequestStatus == 0;
        final boolean hasMobileConfigUpgradeRequest = MobileConfigUpgradeHandler.getInstance().hasMobileConfigUpgradeRequest(strUDID);
        final int managedStatus = this.getManagedDeviceStatus(strUDID);
        final boolean isDeviceReEstablishingCommunication = managedStatus != 2 && MDMiOSEntrollmentUtil.getInstance().getReenrollReq(enrollmentRequestId);
        final boolean isEnrollmentCommandAllowed = (isEnrollmentRequestYetToBeUsedOrFailed && (isAppleConfig == null || !hasMobileConfigUpgradeRequest)) || isDeviceReEstablishingCommunication;
        this.logger.log(Level.INFO, "Enrollment Command Allowed: {0} | Enrollment Request Status: {1} | Has Mobile Config Upgrade Request: {2} | Managed Status : {3} | Re-Establish Communication Allowed: {4}", new Object[] { isEnrollmentCommandAllowed, enrollmentRequestStatus, hasMobileConfigUpgradeRequest, managedStatus, isDeviceReEstablishingCommunication });
        return isEnrollmentCommandAllowed;
    }
}
