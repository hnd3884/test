package com.me.mdm.agent.servlets.ios;

import com.adventnet.sym.webclient.mdm.IOSServerServlet;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSData;
import com.me.mdm.server.enrollment.ios.MacBootstrapTokenHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.enrollment.ios.MobileConfigUpgradeHandler;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.adventnet.sym.server.mdm.ios.APNSImpl;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.MDMEntrollment;
import com.me.mdm.server.enrollment.ios.MDMProfileInstallationHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.PlistWrapper;
import java.util.HashMap;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.sym.webclient.mdm.IOSCheckInServlet;

public class MacCheckInServlet extends IOSCheckInServlet
{
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.logger.log(Level.INFO, "MacCheckInServlet => (POST) Received request from APPLE ");
        this.checkinLogger.log(Level.INFO, "MacCheckInServlet => (POST) Received request from APPLE ");
        super.doPost(request, response, deviceRequest);
    }
    
    @Override
    public void doPut(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.logger.log(Level.INFO, "MacCheckInServlet => (PUT) Received request from APPLE ");
        this.checkinLogger.log(Level.INFO, "MacCheckInServlet => (PUT) Received request from APPLE ");
        super.doPut(request, response, deviceRequest);
    }
    
    @Override
    protected void handleTokenUpdateMessage(final HashMap hashPlist, final String strData, final Long enrollmentRequestId, final String isAppleConfig, final String messageType) throws Exception {
        this.className = "MacCheckInServlet";
        this.handleMultiUserTokenUpdate(hashPlist, strData, enrollmentRequestId, isAppleConfig, messageType);
    }
    
    protected void handleMultiUserTokenUpdate(final HashMap hashPlist, final String strData, final Long enrollmentRequestId, final String isAppleConfig, final String messageType) throws Exception {
        final HashMap hsmap = new HashMap();
        final String strUserID = hashPlist.get("UserID");
        if (strUserID != null) {
            this.logger.log(Level.INFO, "{0}: Dropping TokenUpdate from user (possibly Mac)", this.className);
            this.logger.log(Level.INFO, "{0} {1} {2}", new Object[] { strUserID, hashPlist.get("UserLongName"), hashPlist.get("UserShortName") });
        }
        else {
            final String strUDID = hashPlist.get("UDID");
            final String strTopic = hashPlist.get("Topic");
            final String strPushMagic = hashPlist.get("PushMagic");
            final String strToken = PlistWrapper.getInstance().getValueForKeyData("Token", strData);
            final String strUnLockToken = null;
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
            if (enrollmentRequestId != null) {
                MDMProfileInstallationHandler.getInstance().addOrUpdateProfileInstallationStatus(enrollmentRequestId, Boolean.TRUE);
            }
            MDMEntrollment.getInstance().enrolliOSDevice(hsmap);
            if (resourceID != null) {
                MDMEntrollment.getInstance().updateEnrollmentDetails(resourceID, hsmap);
                this.logger.log(Level.WARNING, "Updated token update for resource : {0} ", resourceID);
            }
            final int enrollmentStatus = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestStatus(enrollmentRequestId, strUDID);
            this.logger.log(Level.INFO, "{1} => enrollmentStatus :{0}", new Object[] { enrollmentStatus, this.className });
            final boolean isEnrollmentCommandAllowed = this.isEnrollmentCommandAllowed(enrollmentRequestId, isAppleConfig, strUDID, enrollmentStatus);
            if (isEnrollmentCommandAllowed) {
                Boolean isAwaitingConfiguration = Boolean.FALSE;
                if (hashPlist.containsKey("AwaitingConfiguration")) {
                    isAwaitingConfiguration = Boolean.parseBoolean(hashPlist.get("AwaitingConfiguration"));
                }
                this.checkAndAddInitialCmds(resourceID, strUDID, isAwaitingConfiguration);
                final HashMap hsAdditionalParams = new HashMap();
                if (enrollmentRequestId != null) {
                    hsAdditionalParams.put("ENROLLMENT_REQUEST_ID", enrollmentRequestId);
                }
                hsAdditionalParams.put("IS_SOURCE_TOKEN_UPDATE", true);
                hsAdditionalParams.put("RESOURCE_ID", resourceID);
                APNSImpl.getInstance().wakeUpDeviceWithERID(strDecodedToken, strPushMagic, strTopic, hsAdditionalParams);
            }
            MDMiOSEntrollmentUtil.getInstance().removeReenrollReq(enrollmentRequestId);
            MobileConfigUpgradeHandler.getInstance().removeMobileConfigUpgradeRequest(strUDID);
        }
    }
    
    @Override
    protected void checkAndAddInitialCmds(final Long resourceID, final String strUDID, final boolean isAwaitingConfiguration) {
        DeviceCommandRepository.getInstance().removeAllCommandsForResource(resourceID, strUDID);
        if (resourceID != null) {
            DeviceCommandRepository.getInstance().addEnrollmentCommand(resourceID);
        }
        else {
            DeviceCommandRepository.getInstance().addEnrollmentCommand(strUDID);
        }
    }
    
    public void processMacRequest(final HttpServletResponse response, final String strData, final HashMap hashPlist) throws Exception {
        final String messageType = hashPlist.get("MessageType");
        final String udid = hashPlist.get("UDID");
        String responseData = null;
        final Long customerId = hashPlist.get("CUSTOMER_ID");
        if (messageType.equalsIgnoreCase("GetBootstrapToken")) {
            final String bootstrapToken = MacBootstrapTokenHandler.getInstance().getMacBootstrapToken(udid, customerId);
            if (bootstrapToken != null) {
                final NSData nsData = new NSData(bootstrapToken);
                final NSDictionary nsDictionary = new NSDictionary();
                nsDictionary.put("BootstrapToken", (NSObject)nsData);
                responseData = nsDictionary.toXMLPropertyList();
            }
            else {
                responseData = PlistWrapper.getInstance().getEmptyDict();
                this.checkinLogger.log(Level.INFO, "Empty plist for SetBootstrapToken...");
            }
            response.setContentType("application/x-plist");
            response.getWriter().write(responseData);
            this.checkinLogger.log(Level.INFO, "response sent for GetBootstrapToken...");
        }
        else if (messageType.equalsIgnoreCase("SetBootstrapToken")) {
            final NSDictionary bootstrapData = (NSDictionary)DMSecurityUtil.parsePropertyList(strData.getBytes("UTF-8"));
            bootstrapData.put("ENROLLMENT_REQUEST_ID", hashPlist.get("ENROLLMENT_REQUEST_ID"));
            new IOSServerServlet().addToQueue(customerId, udid, bootstrapData.toXMLPropertyList(), 108);
            responseData = PlistWrapper.getInstance().getEmptyDict();
            response.setContentType("application/x-plist");
            response.getWriter().write(responseData);
            this.checkinLogger.log(Level.INFO, "Empty plist sent for SetBootstrapToken...");
        }
    }
}
