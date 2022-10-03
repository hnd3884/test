package com.me.mdm.server.drp;

import java.util.logging.Level;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import java.util.List;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import org.json.JSONException;
import com.me.mdm.server.enrollment.MDMAgentUpdateHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.enrollment.MDMEnrollmentOTPHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.core.auth.MDMDeviceTokenGenerator;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONObject;
import java.util.logging.Logger;

public class WpMDMRegistrationHandler extends MDMRegistrationHandler
{
    private static final String MSG_REQUEST = "MsgRequest";
    private static final String MSG_RESPONSE = "MsgResponse";
    private static final String EMAIL_ADDRESS = "EmailAddress";
    private static final String MSG_REQUEST_TYPE = "MsgRequestType";
    private static final String MSG_RESPONSE_TYPE = "MsgResponseType";
    private static final String IS_MDM_CLIENT_INSTALLED = "IsMdmClientInstalled";
    private static final String MDM_CLIENT_TOKEN = "MDMClientToken";
    private static final String SERVER_PATH = "ServerPath";
    private static final String MDM_CLIENT_DETAILS = "MDMClientDetails";
    public Logger checkinLogger;
    
    public WpMDMRegistrationHandler() {
        this.checkinLogger = Logger.getLogger("MDMCheckinLogger");
    }
    
    @Override
    protected JSONObject processAppAuthenticateMessage(final JSONObject requestJSON) throws Exception {
        final JSONObject response = super.processAppAuthenticateMessage(requestJSON);
        final String status = response.optString("Status");
        if (!MDMStringUtils.isEmpty(status) && status.equals("Acknowledged")) {
            final JSONObject msgResponse = response.getJSONObject("MsgResponse");
            final Long erid = msgResponse.getLong("EnrollmentRequestID");
            msgResponse.put("encapiKey", (Object)MDMDeviceTokenGenerator.getInstance().getDeviceToken(erid));
            response.put("MsgResponse", (Object)msgResponse);
        }
        return response;
    }
    
    @Override
    protected JSONObject processDiscoverMessage(final JSONObject requestJSON) throws Exception {
        JSONObject responseJSON = null;
        final JSONObject msgRequest = requestJSON.getJSONObject("MsgRequest");
        final String emailAddress = msgRequest.optString("EmailAddress", (String)null);
        final DataObject iosNativeAppDO = this.getIOSNativeAppAuthDO(emailAddress, 3);
        if (iosNativeAppDO.isEmpty()) {
            responseJSON = super.processDiscoverMessage(requestJSON);
            responseJSON.getJSONObject("MsgResponse").put("IsMdmClientInstalled", (Object)Boolean.FALSE);
        }
        else {
            final Row iosNativeAppRow = iosNativeAppDO.getFirstRow("IOSNativeAppAuthentication");
            responseJSON = new JSONObject();
            responseJSON.put("MsgResponseType", (Object)"DiscoverResponse");
            responseJSON.put("Status", (Object)"Acknowledged");
            final JSONObject messageResponseJSON = new JSONObject();
            messageResponseJSON.put("ManagedDeviceID", iosNativeAppRow.get("MANAGED_DEVICE_ID"));
            messageResponseJSON.put("IsLanguagePackEnabled", LicenseProvider.getInstance().isLanguagePackEnabled());
            messageResponseJSON.put("AuthMode", (Object)this.getAuthenticationModeString(1));
            messageResponseJSON.put("IsMdmClientInstalled", (Object)Boolean.TRUE);
            responseJSON.put("MsgResponse", (Object)messageResponseJSON);
        }
        return responseJSON;
    }
    
    @Override
    protected JSONObject processDeviceAuthenticateMessage(final JSONObject requestJSON) throws Exception {
        JSONObject responseJSON = super.processDeviceAuthenticateMessage(requestJSON);
        final JSONObject msgResponseJSON = responseJSON.getJSONObject("MsgResponse");
        if (String.valueOf(requestJSON.getJSONObject("MsgRequest").get("ClientType")).equalsIgnoreCase("WindowsPhoneApp")) {
            final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
            if (evaluatorApi != null) {
                evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Windows_App_Auth_Page_Count");
            }
        }
        if (String.valueOf(responseJSON.get("Status")).equalsIgnoreCase("Acknowledged")) {
            final Boolean isAppBasedEnrollment = Boolean.valueOf(MDMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"));
            if (String.valueOf(requestJSON.getJSONObject("MsgRequest").get("ClientType")).equalsIgnoreCase("WindowsPhoneApp")) {
                final String clientToken = MDMEnrollmentOTPHandler.getInstance().generateMdmClientToken(msgResponseJSON.getLong("EnrollmentRequestID"));
                msgResponseJSON.put("MDMClientToken", (Object)clientToken);
                final Long customerId = msgResponseJSON.getLong("CustomerID");
                final String serverBasePath = MDMEnrollmentUtil.getInstance().getServerBaseURL();
                final String discoverUrl = serverBasePath + "/mdm/client/v1/wpdiscover/" + customerId;
                msgResponseJSON.put("ServerPath", (Object)discoverUrl);
            }
            else if (String.valueOf(requestJSON.getJSONObject("MsgRequest").get("ClientType")).equalsIgnoreCase("WindowsPhoneWorkplace") && isAppBasedEnrollment) {
                responseJSON.put("Status", (Object)"Error");
                msgResponseJSON.put("ErrorMsg", (Object)"Settings are in App Based Enrollment");
                msgResponseJSON.put("ErrorCode", 12013);
                msgResponseJSON.put("ErrorKey", (Object)"dc.mdm.enroll.windows.app_based_enrollment");
            }
        }
        else {
            final String clientToken2 = requestJSON.getJSONObject("MsgRequest").optString("OTPPassword", (String)null);
            if (clientToken2 != null) {
                final Row mdmClientTokenRow = DBUtil.getRowFromDB("MdmClientToken", "CLIENT_TOKEN", (Object)clientToken2);
                if (mdmClientTokenRow != null) {
                    responseJSON = new JSONObject();
                    responseJSON.put("Status", (Object)"Acknowledged");
                    responseJSON.put("MsgResponseType", (Object)"AuthenticateResponse");
                    final JSONObject messageResponseJSON = new JSONObject();
                    messageResponseJSON.put("EnrollmentRequestID", mdmClientTokenRow.get("ENROLLMENT_REQUEST_ID"));
                    responseJSON.put("MsgResponse", (Object)messageResponseJSON);
                }
            }
        }
        return responseJSON;
    }
    
    @Override
    protected void processPostAppRegistration(final JSONObject requestJSON) throws JSONException {
        super.processPostAppRegistration(requestJSON);
        final String udid = String.valueOf(requestJSON.getJSONObject("MsgRequest").get("UDID"));
        final String agentVersion = requestJSON.getJSONObject("MsgRequest").optString("AgentVersion", (String)null);
        if (agentVersion != null) {
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            MDMAgentUpdateHandler.getInstance().updateAppAgentVersion(resourceID, agentVersion, null);
        }
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        final Double messageVersion = requestJSON.optDouble("MsgVersion", 1.0);
        this.addAgentInitialCommands(resourceID, messageVersion);
    }
    
    private void addAgentInitialCommands(final Long resourceID, final Double messageVersion) {
        if (ManagedDeviceHandler.getInstance().isWindows81OrAboveDevice(resourceID) && messageVersion == 2.0) {
            DeviceCommandRepository.getInstance().addNativeAppChannelUriCommand(Arrays.asList(resourceID));
            DeviceCommandRepository.getInstance().addSyncAppCatalogCommand(Arrays.asList(resourceID));
            DeviceCommandRepository.getInstance().addAppCatalogStatusSummaryCommand(Arrays.asList(resourceID));
            if (LocationSettingsDataHandler.getInstance().isLocationTrackingEnabledforDevice(resourceID) && !ManagedDeviceHandler.getInstance().isOsVersionGreaterThanForResource(resourceID, 10.0f)) {
                DeviceCommandRepository.getInstance().addLocationCommand(Arrays.asList(resourceID), 2);
            }
        }
    }
    
    private DataObject getIOSNativeAppAuthDO(final String emailAddress, final int platformType) throws DataAccessException {
        final SelectQuery managedDeviceIdQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedUser"));
        managedDeviceIdQuery.addJoin(new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        managedDeviceIdQuery.addJoin(new Join("ManagedUserToDevice", "IOSNativeAppAuthentication", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        managedDeviceIdQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria emailIdCri = new Criteria(new Column("ManagedUser", "EMAIL_ADDRESS"), (Object)emailAddress, 0, (boolean)Boolean.FALSE);
        final Criteria platformTypeCri = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)platformType, 0);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        managedDeviceIdQuery.setCriteria(emailIdCri.and(platformTypeCri).and(userNotInTrashCriteria));
        managedDeviceIdQuery.addSelectColumn(new Column("IOSNativeAppAuthentication", "MANAGED_DEVICE_ID"));
        final DataObject iosNativeAppDO = MDMUtil.getPersistence().get(managedDeviceIdQuery);
        return iosNativeAppDO;
    }
    
    @Override
    protected JSONObject processRegistrationStatusUpdateMessage(final JSONObject requestJSON) throws Exception {
        final String messageType = String.valueOf(requestJSON.get("MsgRequestType"));
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final String registrationType = msgRequestJSON.optString("RegistrationType", "MDMRegistration");
        final String deviceUDID = msgRequestJSON.optString("UDID");
        final JSONObject enrollDetails = ManagedDeviceHandler.getInstance().getEnrollmentDetailsForDevice(deviceUDID);
        this.checkinLogger.log(Level.INFO, "Windows MessageType:{0} RegistrationType:{1} Erid:{2} Udid:{3}", new Object[] { messageType, registrationType, enrollDetails.opt("ENROLLMENT_REQUEST_ID"), deviceUDID });
        return super.processRegistrationStatusUpdateMessage(requestJSON);
    }
}
