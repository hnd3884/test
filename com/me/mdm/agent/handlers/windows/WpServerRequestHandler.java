package com.me.mdm.agent.handlers.windows;

import java.util.Hashtable;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AddRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ExecRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.DeleteRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.me.mdm.framework.syncml.core.SyncBodyMessage;
import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.SyncHeaderMessage;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.me.mdm.server.windows.asset.WMIQueryResponseProcessor;
import com.me.mdm.server.windows.profile.WpSCEPResponseProcessor;
import com.me.mdm.server.settings.location.MDMGeoLocationHandler;
import com.adventnet.sym.server.mdm.security.ResetPasscodeHandler;
import com.me.mdm.server.windows.notification.WpDeviceChannelUri;
import com.me.devicemanagement.framework.server.config.ConfigUtil;
import com.me.mdm.server.profiles.MDMProfileResponseListenerHandler;
import com.me.mdm.server.config.MDMConfigUtil;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.apps.MDDeviceInstalledAppsHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.me.mdm.server.windows.asset.WpInventory;
import com.adventnet.sym.server.mdm.inv.AppDataHandler;
import com.me.mdm.core.windows.commands.WinDesktopInstalledAppListCommand;
import com.me.mdm.core.windows.commands.WinMobileInstalledAppListCommand;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.framework.syncml.xml.SyncMLMessage2XMLConverterException;
import org.json.JSONException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.me.mdm.core.windows.commands.WinROBOCommand;
import com.me.mdm.core.windows.commands.WinGetEnrollmentTypeQuery;
import com.me.mdm.core.windows.commands.WinBlacklistAppCommand;
import com.me.mdm.server.apps.blacklist.windows.WindowsBlacklistProcessor;
import com.me.mdm.server.windows.enrollment.WindowsLegacyAgentInstallHandler;
import com.me.mdm.uem.actionconstants.DeviceAction;
import com.me.mdm.core.windows.commands.WindowsAppConfigCommand;
import com.me.mdm.core.windows.commands.WpAppStatusCommand;
import com.me.mdm.core.windows.commands.WpSideLoadCommand;
import org.json.JSONArray;
import java.util.Collection;
import com.me.mdm.core.windows.commands.WpSCEPStatusCheck;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.config.MDMConfigQueryUtil;
import com.adventnet.sym.server.mdm.config.MDMConfigQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.core.windows.commands.WpRestartDeviceCommand;
import com.me.mdm.server.windows.apps.nativeapp.WindowsNativeAppHandler;
import com.me.mdm.core.windows.commands.WindowsNativeAppConfigurationCommand;
import com.me.mdm.core.windows.commands.WpDeviceServerUrlCommand;
import com.me.mdm.core.windows.commands.WpUserInfoUpdateCommand;
import com.me.mdm.core.windows.commands.WpRemoteLocateCommand;
import com.me.mdm.core.windows.commands.WpWnsChannelUriCommand;
import com.me.mdm.core.windows.commands.WpWnsRegistrationCommand;
import com.me.mdm.core.windows.commands.WpDMClientCommand;
import com.me.mdm.core.windows.commands.WpEnterpriseAppManagementCommand;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.net.URLDecoder;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.apache.commons.lang.StringEscapeUtils;
import java.net.URLEncoder;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.zerotrust.ZeroTrustAPIHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.ThirdPartyCAUtil;
import com.adventnet.sym.server.mdm.command.DynamicVariableHandler;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.idps.core.util.DirectoryUtil;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.mdm.files.MDMFileUtil;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.io.File;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.core.windows.commands.WpResetPasscodeCommand;
import com.me.mdm.core.windows.commands.WpDeviceRingCommand;
import com.me.mdm.core.windows.commands.WpDeviceLockCommand;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.adventnet.sym.server.mdm.security.RemoteWipeHandler;
import com.me.mdm.core.windows.commands.WpInstalledApplicationListCommand;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.api.MdmInvDataProcessor;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.me.mdm.core.windows.commands.WpDeviceInformationCommand;
import com.me.mdm.server.seqcommands.SequentialSubCommand;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.HashMap;
import com.me.mdm.framework.syncml.xml.SyncMLMessage2XMLConverter;
import com.me.mdm.framework.syncml.responsecmds.StatusResponseCommand;
import com.me.mdm.server.seqcommands.windows.WindowsSeqCmdUtil;
import com.me.mdm.server.seqcommands.SeqCmdDBUtil;
import com.me.mdm.server.seqcommands.SeqCmdUtils;
import com.me.mdm.core.windows.wmi.WMIQueryHandler;
import com.me.mdm.server.windows.apps.WpCompanyHubAppHandler;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import java.util.Properties;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.enrollment.EnrollmentFacade;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.me.mdm.server.windows.enrollment.WpEnrollment;
import com.me.mdm.framework.syncml.responsecmds.ResultsResponseCommand;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;
import com.me.mdm.server.enrollment.task.InactiveDevicePolicyTask;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import org.json.JSONObject;
import com.me.mdm.core.windows.commands.WpDeviceEnrollmentCommand;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.Map;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.core.windows.SyncMLMessageParser;
import com.me.mdm.framework.syncml.xml.XML2SyncMLMessageConverter;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;

public class WpServerRequestHandler extends BaseProcessDeviceRequestHandler
{
    private Logger logger;
    public Logger seqlogger;
    private Logger accesslogger;
    public Logger checkinLogger;
    private String separator;
    public static final String WIN_8_OSVERSION_STR = "WindowsPhone8";
    public static final String WIN_8_1_OSVERSION_STR = "WindowsPhone81";
    public static final String WIN_10_OSVERSION_STR = "Windows10Mobile";
    List<String> dynamicPayloadStrings;
    
    public WpServerRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.seqlogger = Logger.getLogger("MDMSequentialCommandsLogger");
        this.accesslogger = Logger.getLogger("MDMCommandsLogger");
        this.checkinLogger = Logger.getLogger("MDMCheckinLogger");
        this.separator = "\t";
        this.dynamicPayloadStrings = new ArrayList<String>() {
            {
                this.add("%restriction_payload_xml%");
                this.add("%passcode_payload_xml%");
                this.add("%restriction_payload_xml_nonAtomicDelete%");
                this.add("%certificate_add_payload_xml%");
                this.add("%certificate_payload_xml%");
                this.add("%certificate_payload_xml_nonAtomicDelete%");
                this.add("%scep_payload_xml%");
                this.add("%scep_exec_payload_xml%");
                this.add("%scep_payload_xml_nonAtomicDelete%");
                this.add("%scep_add_payload_xml%");
                this.add("%activesync_payload_xml%");
                this.add("%email_payload_xml%");
                this.add("%app_add_payload_xml%");
                this.add("%app_exec_payload_xml%");
                this.add("%app_nonAtomicDelete_payload_xml%");
                this.add("%app_payload_xml%");
                this.add("%app_replace_payload_xml%");
                this.add("%lockdown_replace_payload_xml%");
                this.add("%lockdown_payload_xml%");
            }
        };
    }
    
    @Override
    public String processRequest(final DeviceRequest request) {
        String responseBuffer = null;
        try {
            final String receivedBuffer = (String)request.deviceRequestData;
            final HashMap parameterMap = request.requestMap;
            final XML2SyncMLMessageConverter converter = new XML2SyncMLMessageConverter();
            final SyncMLMessage requestSyncML = converter.transform(receivedBuffer);
            final SyncMLMessage responseSyncML = this.createResponseMessage(requestSyncML);
            final SyncMLMessageParser parser = new SyncMLMessageParser();
            final JSONObject syncMLHeader = parser.parseSyncMLMessageHeader(requestSyncML);
            syncMLHeader.put("ServletPath", (Object)parameterMap.get("ServletPath"));
            final int serverVersion = MDMDeviceAPIKeyGenerator.getInstance().isClientVersion2_0(parameterMap.get("ServletPath")) ? APIKey.VERSION_2_0 : APIKey.VERSION_1_0;
            final JSONObject newJSON = JSONUtil.mapToJSON(MDMDeviceAPIKeyGenerator.getInstance().fetchAPIKeyDetails(serverVersion, parameterMap));
            JSONUtil.getInstance();
            JSONUtil.putAll(syncMLHeader, newJSON);
            final String deviceUDID = String.valueOf(syncMLHeader.get("UDID"));
            final String enrollmentRequestID = String.valueOf(syncMLHeader.get("ENROLLMENT_REQUEST_ID"));
            Boolean responseContainsCommand = Boolean.FALSE;
            syncMLHeader.put("ENROLLMENT_REQUEST_ID", (Object)Long.valueOf(enrollmentRequestID));
            request.customerID = JSONUtil.optLongForUVH(syncMLHeader, "CUSTOMER_ID", (Long)null);
            request.deviceUDID = deviceUDID;
            Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
            if (requestSyncML.getSyncHeader().getMsgID().equalsIgnoreCase("1")) {
                WindowsMigrationUtil.getInstance().checkAndAddMigrationCommand(resourceID, syncMLHeader.optString("TARGET"));
            }
            if (requestSyncML.getSyncBody().getRequestCmds() != null && !this.containsOnlyAADUserTokenAlertRequestCmd(requestSyncML)) {
                final Integer managedDeviceStatus = ManagedDeviceHandler.getInstance().getManagedDeviceStatus(deviceUDID);
                final Integer enrollReqStatus = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestStatus(Long.valueOf(enrollmentRequestID));
                final boolean isNewRequest = this.isNewEnrollRequest(resourceID, Long.valueOf(enrollmentRequestID));
                if (((managedDeviceStatus != 2 && managedDeviceStatus != 4 && managedDeviceStatus != 6 && managedDeviceStatus != 7 && managedDeviceStatus != 5 && managedDeviceStatus != 9 && managedDeviceStatus != 11 && managedDeviceStatus != 10) || isNewRequest || (managedDeviceStatus == 5 && enrollReqStatus == 1)) && enrollReqStatus != -1) {
                    final WpDeviceEnrollmentCommand deviceEnrollment = new WpDeviceEnrollmentCommand();
                    deviceEnrollment.processRequest(responseSyncML, null);
                    DeviceCommandRepository.getInstance().removeAllCommandsForResource(resourceID, deviceUDID);
                }
                else {
                    this.accesslogger.log(Level.INFO, "DEVICE-IN: IdleRequestReceived{0}{1}{2}{3}{4}IdleReceived{5}{6}", new Object[] { this.separator, resourceID, this.separator, deviceUDID, this.separator, this.separator, MDMUtil.getCurrentTimeInMillis() });
                    MDMUtil.getInstance();
                    MDMUtil.addOrupdateAgentLastContact(resourceID, new Long(System.currentTimeMillis()), null, null, new Long(0L), "");
                    new InactiveDevicePolicyTask().updateInactiveDeviceRemarksAfterContact(resourceID);
                    DeviceCommandRepository.getInstance().loadCommandsForDevice(deviceUDID, 1);
                }
                this.handleUnEnrollmentRequest(requestSyncML, deviceUDID);
            }
            else if (requestSyncML.getSyncBody().getResponseCmds() != null) {
                final List responseCmds = requestSyncML.getSyncBody().getResponseCmds();
                boolean isResponseProcessedInMemory = false;
                boolean isWMIQueryResponse = false;
                for (int i = 0; i < responseCmds.size(); ++i) {
                    final SyncMLResponseCommand response = responseCmds.get(i);
                    final String cmdRef = response.getCmdRef();
                    if (response instanceof ResultsResponseCommand) {
                        if (cmdRef.equalsIgnoreCase("Enrollment")) {
                            final WpEnrollment wpEnrollment = new WpEnrollment();
                            wpEnrollment.updateWpEnrollmentResponse(requestSyncML, response);
                            resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
                            final DeviceDetails deviceDetails = new DeviceDetails(resourceID);
                            DeviceCommandRepository.getInstance().addDeviceClientSettingsCommand(resourceID);
                            final Long userId = new EnrollmentFacade().getUserIdForEnrollmentRequestToDevice(resourceID);
                            DeviceCommandRepository.getInstance().addDeviceScanCommand(deviceDetails, userId);
                            final String osVersion = DBUtil.getValueFromDB("MdOSDetailsTemp", "RESOURCE_ID", (Object)resourceID, "OS_VERSION").toString();
                            if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.0f)) {
                                DeviceCommandRepository.getInstance().addWindowsCommand(Arrays.asList(resourceID), "PreloadedAppsInfo");
                                Properties aetproperties = new Properties();
                                aetproperties = WpAppSettingsHandler.getInstance().getWpAETDetails(new Long(String.valueOf(syncMLHeader.get("CUSTOMER_ID"))));
                                if (aetproperties != null && aetproperties.get("CERT_FILE_PATH") != null) {
                                    DeviceCommandRepository.getInstance().addWindowsCommand(Arrays.asList(resourceID), "AppEnrollmentToken");
                                }
                            }
                            this.checkinLogger.log(Level.INFO, "Windows MessageType:{0} Erid:{1} Udid:{2}", new Object[] { "Enrollment", enrollmentRequestID, deviceUDID });
                            try {
                                if (syncMLHeader.optString("TARGET", "").contains("SerialNumber")) {
                                    MEMDMTrackParamManager.getInstance().incrementTrackValue(deviceDetails.customerId, "Windows_UEM_Module", "Uem_Device_Enrolled");
                                }
                            }
                            catch (final Exception e) {
                                this.logger.log(Level.WARNING, "UEM tracking not tracked", e);
                            }
                            if (ManagedDeviceHandler.getInstance().isWindows81OrAboveDevice(osVersion) && MDMAgentSettingsHandler.getInstance().getNotificaitonServiceType(3, request.customerID) == 1) {
                                DeviceCommandRepository.getInstance().addDeviceCommunicationCommand(Arrays.asList(resourceID));
                            }
                            isResponseProcessedInMemory = true;
                            final Properties windowsAETProp = WpAppSettingsHandler.getInstance().getWpAETDetails(deviceDetails.customerId);
                            final Boolean isAppBasedEnrollment = Boolean.valueOf(MDMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"));
                            if (windowsAETProp != null && !isAppBasedEnrollment) {
                                final Object appIdObj = ((Hashtable<K, Object>)windowsAETProp).get("APP_ID");
                                if (appIdObj != null) {
                                    final ArrayList resourceList = new ArrayList();
                                    resourceList.add(resourceID);
                                    WpCompanyHubAppHandler.getInstance().sendWPCompanyHubAppMail(resourceList, new Long(1L), deviceDetails.customerId, 1);
                                }
                            }
                        }
                        else if (cmdRef.contains("WmiQuery")) {
                            String accessMessage = "DATA-IN: " + cmdRef + this.separator + resourceID + this.separator + deviceUDID + this.separator + 200 + this.separator + MDMUtil.getCurrentTimeInMillis() + this.separator + 0;
                            this.accesslogger.log(Level.INFO, accessMessage);
                            WMIQueryHandler.getInstance().getWMIInstancePropertiesQuery(responseSyncML, (ResultsResponseCommand)response);
                            isResponseProcessedInMemory = true;
                            isWMIQueryResponse = true;
                            responseContainsCommand = Boolean.TRUE;
                            accessMessage = "DATA-OUT: WmiInstancePropsQuery" + cmdRef.substring(cmdRef.indexOf(";")) + this.separator + resourceID + this.separator + deviceUDID + this.separator + "Command-Sent" + this.separator + MDMUtil.getCurrentTimeInMillis();
                            this.accesslogger.log(Level.INFO, accessMessage);
                        }
                        else if (response instanceof ResultsResponseCommand && cmdRef.equalsIgnoreCase("CorporateWipe")) {
                            DeviceCommandRepository.getInstance().deleteResourceCommand(cmdRef, deviceUDID, 1);
                            if (!DeviceCommandRepository.getInstance().hasDeviceCommandInCacheOrRepo(deviceUDID)) {
                                try {
                                    ManagedDeviceHandler.getInstance().removeDeviceInTrash(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID));
                                }
                                catch (final Exception ex) {
                                    Logger.getLogger(WpServerRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                    final SequentialSubCommand sequentialSubCommand = SeqCmdUtils.getInstance().getIfSequentialCommandResponse(resourceID, cmdRef);
                    if (DeviceCommandRepository.getInstance().getCommandID(cmdRef) != null && sequentialSubCommand != null) {
                        SeqCmdDBUtil.getInstance().addorUpdateSeqcmdStatusAndQueue(resourceID, sequentialSubCommand.order, sequentialSubCommand.SequentialCommandID, 120);
                        if (sequentialSubCommand.isImmidiate) {
                            this.seqlogger.log(Level.INFO, "Processing Sequential command in thread resource : {0} , Seqcommand :  {1}", new Object[] { resourceID, sequentialSubCommand.SequentialCommandID });
                            if (WindowsSeqCmdUtil.getInstance().isImmediateProcessingSubCommand(cmdRef)) {
                                final JSONObject params = new JSONObject();
                                params.put("status", 200);
                                params.put("resourceID", (Object)resourceID);
                                params.put("statusMap", (Object)new JSONObject());
                                WindowsSeqCmdUtil.getInstance().processWinSeqCmd(cmdRef, params);
                            }
                        }
                    }
                }
                if (!isResponseProcessedInMemory) {
                    this.addResponseToQueue(request, receivedBuffer, 103);
                }
                if (isWMIQueryResponse) {
                    this.deleteWMIQueryFromMdCommandsToDevice(resourceID, requestSyncML);
                }
            }
            if (resourceID != null) {
                syncMLHeader.put("RESOURCE_ID", (Object)resourceID);
            }
            if (!responseContainsCommand) {
                this.getNextSyncMLCommand(syncMLHeader, requestSyncML, responseSyncML);
            }
            final List requestCmds = responseSyncML.getSyncBody().getRequestCmds();
            if (requestCmds == null) {
                final StatusResponseCommand syncHeaderStatus = responseSyncML.getSyncBody().getResponseCmds().get(0);
                syncHeaderStatus.setData(String.valueOf(200));
                responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
                this.accesslogger.log(Level.INFO, "DEVICE-OUT: TerminatingSession{0}{1}{2}{3}{4}TerminateSession{5}{6}", new Object[] { this.separator, resourceID, this.separator, deviceUDID, this.separator, this.separator, MDMUtil.getCurrentTimeInMillis() });
            }
            final SyncMLMessage2XMLConverter convert = new SyncMLMessage2XMLConverter();
            responseBuffer = convert.transform(responseSyncML);
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in processRequestForDEPToken of WpSyncMLProcessor", ex2);
        }
        return responseBuffer;
    }
    
    public List getNextSyncMLCommand(final JSONObject jsonObject, final SyncMLMessage requestSyncML, final SyncMLMessage responseSyncML) throws JSONException, SyncMLMessage2XMLConverterException, SyMException, Exception {
        final Long resourceID = jsonObject.optLong("RESOURCE_ID", -1L);
        final String deviceUDID = String.valueOf(jsonObject.get("UDID"));
        final Long customerID = JSONUtil.optLongForUVH(jsonObject, "CUSTOMER_ID", (Long)null);
        final DeviceCommand deviceCommand = DeviceCommandRepository.getInstance().getDeviceCommandFromCache(deviceUDID, 1);
        if (deviceCommand != null) {
            final String accessMessage = "DATA-OUT: " + deviceCommand.commandType + this.separator + resourceID + this.separator + deviceUDID + this.separator + "Command-Sent" + this.separator + MDMUtil.getCurrentTimeInMillis();
            this.accesslogger.log(Level.INFO, accessMessage);
            final String command = deviceCommand.commandType;
            if (command.equals("DeviceInformation")) {
                final WpDeviceInformationCommand deviceinformation = new WpDeviceInformationCommand();
                Object os_version = DBUtil.getValueFromDB("MdOSDetailsTemp", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
                if (os_version == null) {
                    os_version = DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
                }
                final HashMap deviceInfo = MDMUtil.getInstance().getMDMDeviceProperties(resourceID);
                final HashMap privacyJson = new PrivacySettingsHandler().getPrivacySettingsForMdDevices(resourceID);
                Boolean fetchDeviceName = true;
                Boolean fetchPhonenum = true;
                Boolean fetchMacAddr = true;
                final int fetchPhone = Integer.parseInt(privacyJson.get("fetch_phone_number").toString());
                final int fetchDevice = Integer.parseInt(privacyJson.get("fetch_device_name").toString());
                final int fetchMac = Integer.parseInt(privacyJson.get("fetch_mac_address").toString());
                if (fetchPhone == 2) {
                    fetchPhonenum = false;
                    final HashMap simHash = new HashMap();
                    MdmInvDataProcessor.getInstance().getSimDetails(resourceID, simHash);
                    final String imei = simHash.get("IMEI");
                    if (!MDMStringUtils.isEmpty(imei)) {
                        jsonObject.put("IMEI", (Object)imei);
                    }
                }
                if (fetchDevice == 2) {
                    fetchDeviceName = false;
                }
                if (fetchMac == 2) {
                    fetchMacAddr = false;
                }
                jsonObject.put("isWindows81OrAbove", ManagedDeviceHandler.getInstance().isWindows81OrAboveDevice(os_version.toString()));
                jsonObject.put("isWindows10OrAbove", ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(os_version.toString(), 10.0f));
                jsonObject.put("isWindows10RedstoneOrAbove", (Object)ManagedDeviceHandler.getInstance().isWin10RedstoneOrAboveOSVersion((String)os_version));
                jsonObject.put("fetchDeviceName", (Object)fetchDeviceName);
                jsonObject.put("fetchPhonenum", (Object)fetchPhonenum);
                jsonObject.put("fetchMacAddr", (Object)fetchMacAddr);
                deviceinformation.getInstance(deviceInfo).processRequest(responseSyncML, jsonObject);
            }
            else if (command.equals("InstalledApplicationList") || command.equalsIgnoreCase("PreloadedAppsInfo")) {
                final JSONObject tokenObject = new JSONObject();
                Properties properties = new Properties();
                properties = WpAppSettingsHandler.getInstance().getWpAETDetails(customerID);
                if (properties != null && properties.getProperty("ENTERPRISE_ID", null) != null) {
                    tokenObject.put("APP_ENROLLMENT_TOKEN", (Object)properties.getProperty("APP_ENROLLMENT_TOKEN"));
                    tokenObject.put("ENTERPRISE_ID", (Object)properties.getProperty("ENTERPRISE_ID"));
                }
                tokenObject.put("IsAppBasedEnrollmentForWindowsPhone", (Object)MDMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"));
                tokenObject.put("OS_VERSION", (Object)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceID, "OS_VERSION"));
                if (tokenObject.optString("OS_VERSION", (String)null) == null) {
                    tokenObject.put("OS_VERSION", (Object)DBUtil.getValueFromDB("MdOSDetailsTemp", "RESOURCE_ID", (Object)resourceID, "OS_VERSION"));
                }
                final DeviceDetails deviceDetails = new DeviceDetails(resourceID);
                tokenObject.put("MODEL_TYPE", deviceDetails.modelType);
                tokenObject.put("isWinMSIAppsEnabled", (Object)Boolean.TRUE);
                tokenObject.put("COMMAND_UUID", (Object)command);
                if (command.equalsIgnoreCase("PreloadedAppsInfo")) {
                    tokenObject.put("APP_INSTALLED_SOURCE", (Object)"System");
                }
                final HashMap privacyJson = new PrivacySettingsHandler().getPrivacySettingsForMdDevices(resourceID);
                final int fetchapps = Integer.parseInt(privacyJson.get("fetch_installed_app").toString());
                if (fetchapps == 2) {
                    final HashMap appsList = WpAppSettingsHandler.getInstance().getAppsInstalledForResource(resourceID);
                    tokenObject.put("storeApps", appsList.get("StoreApps"));
                    tokenObject.put("nonStoreApps", appsList.get("NonStoreApps"));
                    tokenObject.put("isPrivacy", true);
                }
                final WpInstalledApplicationListCommand applicationList = WpInstalledApplicationListCommand.getInstance(tokenObject);
                applicationList.processRequest(responseSyncML, tokenObject);
            }
            else if (command.equals("CorporateWipe") || command.equals("RemoveDevice")) {
                final WpEnrollment wpEnrollment = new WpEnrollment();
                wpEnrollment.removeDeviceRequest(requestSyncML, responseSyncML);
                if (command.equals("CorporateWipe")) {
                    DeviceCommandRepository.getInstance().deleteResourceCommand(command, resourceID);
                    final String sRemarks = "mdm.deprovision.old_remark";
                    final Properties properties2 = new Properties();
                    ((Hashtable<String, String>)properties2).put("UDID", deviceUDID);
                    final JSONObject json = ManagedDeviceHandler.getInstance().getDeprovisiondetails(resourceID);
                    String deprovisionRemarks = "";
                    int managedStatus = -1;
                    if (json != null) {
                        managedStatus = json.optInt("MANAGED_STATUS", -1);
                        deprovisionRemarks = json.optString("REMARKS", "");
                    }
                    if (json != null && managedStatus != -1 && deprovisionRemarks != null && deprovisionRemarks != "") {
                        ((Hashtable<String, Integer>)properties2).put("MANAGED_STATUS", managedStatus);
                        ((Hashtable<String, String>)properties2).put("REMARKS", deprovisionRemarks);
                    }
                    else {
                        ((Hashtable<String, Integer>)properties2).put("MANAGED_STATUS", new Integer(10));
                        ((Hashtable<String, String>)properties2).put("REMARKS", sRemarks);
                    }
                    ((Hashtable<String, Integer>)properties2).put("PLATFORM_TYPE", 3);
                    ((Hashtable<String, Boolean>)properties2).put("WipeCmdFromServer", true);
                    ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(properties2);
                    final JSONObject deprovisionJson = new JSONObject();
                    deprovisionJson.put("RESOURCE_ID", (Object)resourceID);
                    deprovisionJson.put("WIPE_PENDING", (Object)Boolean.FALSE);
                    ManagedDeviceHandler.getInstance().updatedeprovisionhistory(deprovisionJson);
                }
                else {
                    DeviceCommandRepository.getInstance().deleteResourceCommand(command, deviceUDID, 1);
                    if (!DeviceCommandRepository.getInstance().hasDeviceCommandInCacheOrRepo(deviceUDID) && ManagedDeviceHandler.getInstance().removeDeviceInTrash(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID))) {
                        return responseSyncML.getSyncBody().getRequestCmds();
                    }
                }
                ManagedDeviceHandler.getInstance().removeResourceAssociationsOnUnmanage(resourceID);
            }
            else if (command.equals("EraseDevice")) {
                final RemoteWipeHandler remoteWipeHandler = new RemoteWipeHandler();
                final JSONObject wipeOptionData = remoteWipeHandler.getWipeOptionData(resourceID);
                final String osVersion = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
                if (wipeOptionData.optBoolean("RetainMDM", (boolean)Boolean.FALSE)) {
                    wipeOptionData.put("isRedstone4AboveDevice", (Object)ManagedDeviceHandler.getInstance().isWin10Redstone5OrAboveOSVersion(osVersion));
                    wipeOptionData.put("isPPKGEnrollment", new EnrollmentTemplateHandler().getEnrollmentTemplateTypeForErid(MDMEnrollmentUtil.getInstance().getEnrollRequestIDFromManagedDeviceID(resourceID)) == 30);
                }
                if (osVersion != null) {
                    wipeOptionData.put("isRedstone2AboveDevice", (Object)ManagedDeviceHandler.getInstance().isWin10Redstone2OrAboveOSVersion(osVersion));
                }
                final WpEnrollment wpEnrollment2 = new WpEnrollment();
                wpEnrollment2.remoteWipe(requestSyncML, responseSyncML, wipeOptionData);
                DeviceCommandRepository.getInstance().deleteResourceCommand(command, resourceID);
                remoteWipeHandler.deleteWipeOptionForResource(resourceID);
                final int ownedby = ManagedDeviceHandler.getInstance().getDeviceOwnership(resourceID);
                String sRemarks2 = "";
                if (ownedby == 1) {
                    sRemarks2 = "mdm.deprovision.old_remark";
                }
                else {
                    sRemarks2 = "mdm.deprovision.retire_remark";
                }
                final Properties properties3 = new Properties();
                final JSONObject json2 = ManagedDeviceHandler.getInstance().getDeprovisiondetails(resourceID);
                int managedStatus2 = -1;
                String deprovisionRemarks2 = "";
                if (json2 != null) {
                    managedStatus2 = json2.optInt("MANAGED_STATUS", -1);
                    deprovisionRemarks2 = json2.optString("REMARKS", "");
                }
                if (json2 != null && managedStatus2 != -1 && deprovisionRemarks2 != null && deprovisionRemarks2 != "") {
                    ((Hashtable<String, Integer>)properties3).put("MANAGED_STATUS", managedStatus2);
                    ((Hashtable<String, String>)properties3).put("REMARKS", deprovisionRemarks2);
                }
                else {
                    if (ownedby == 1) {
                        ((Hashtable<String, Integer>)properties3).put("MANAGED_STATUS", new Integer(10));
                    }
                    else {
                        ((Hashtable<String, Integer>)properties3).put("MANAGED_STATUS", new Integer(11));
                    }
                    ((Hashtable<String, String>)properties3).put("REMARKS", sRemarks2);
                }
                ((Hashtable<String, String>)properties3).put("UDID", deviceUDID);
                ((Hashtable<String, Integer>)properties3).put("PLATFORM_TYPE", 3);
                ((Hashtable<String, Boolean>)properties3).put("WipeCmdFromServer", true);
                ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(properties3);
                final JSONObject deprovisionJson2 = new JSONObject();
                deprovisionJson2.put("RESOURCE_ID", (Object)resourceID);
                deprovisionJson2.put("WIPE_PENDING", (Object)Boolean.FALSE);
                ManagedDeviceHandler.getInstance().updatedeprovisionhistory(deprovisionJson2);
                ManagedDeviceHandler.getInstance().removeResourceAssociationsOnUnmanage(resourceID);
            }
            else if (command.equals("WindowsSelectiveWipe")) {
                new WpEnrollment().selectiveWipe(requestSyncML, responseSyncML);
            }
            else if (command.equals("DeviceLock")) {
                final WpDeviceLockCommand deviceLockCmd = new WpDeviceLockCommand();
                deviceLockCmd.processRequest(responseSyncML);
            }
            else if (command.equals("DeviceRing")) {
                final WpDeviceRingCommand deviceRingCmd = new WpDeviceRingCommand();
                deviceRingCmd.processRequest(responseSyncML);
            }
            else if (command.equals("ResetPasscode")) {
                final HashMap privacyJson2 = new PrivacySettingsHandler().getPrivacySettingsForMdDevices(resourceID);
                final int clearPasscodePrivacy = Integer.parseInt(privacyJson2.get("disable_clear_passcode").toString());
                if (clearPasscodePrivacy == 0) {
                    final WpResetPasscodeCommand resetPwdCmd = new WpResetPasscodeCommand();
                    resetPwdCmd.processRequest(responseSyncML);
                }
                else {
                    this.logger.log(Level.INFO, "getNextSyncMLCommand: No access to perform {0} on Resource {1}.. ", new Object[] { command, resourceID });
                    DeviceCommandRepository.getInstance().deleteResourceCommand("ResetPasscode", resourceID);
                }
            }
            else if (command.equals("InstallProfile") || command.equals("RemoveProfile") || command.equals("InstallApplication") || command.equalsIgnoreCase("RemoveApplication") || command.equalsIgnoreCase("UpdateApplication")) {
                Boolean isProfileApplicable = Boolean.TRUE;
                final Long collectionID = Long.valueOf(deviceCommand.commandUUID.substring(deviceCommand.commandUUID.lastIndexOf("=") + 1));
                final JSONObject cmdRequiredcheck = this.checkCollectionProfileNotApplicableForResource(resourceID, deviceCommand.commandUUID);
                final boolean doNotSendCmd = cmdRequiredcheck.optBoolean("doNotSendCmd", false);
                if (!doNotSendCmd) {
                    final String clientDataParentDir = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
                    final String profileFullPath = clientDataParentDir + File.separator + deviceCommand.commandFilePath;
                    String payloadContent = null;
                    this.logger.log(Level.INFO, "generateQuery command: profileFullPath{0}: ", profileFullPath);
                    if (command.equals("InstallProfile")) {
                        final String cacheName = MDMMetaDataUtil.getInstance().getFileCanonicalPath(deviceCommand.commandFilePath);
                        payloadContent = (String)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName, 2);
                        if (payloadContent == null) {
                            payloadContent = PayloadHandler.getInstance().readProfileFromFile(profileFullPath);
                            payloadContent = PayloadSecretFieldsHandler.getInstance().replaceAllPayloadSecrets(payloadContent, customerID);
                            if (payloadContent.length() <= MDMFileUtil.fileSizeCacheThreshold) {
                                ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, (Object)payloadContent, 2, (int)MDMFileUtil.fileCacheTimeTTL);
                                this.logger.log(Level.INFO, "File added in cache - {0}", profileFullPath);
                            }
                            else {
                                this.logger.log(Level.INFO, "File size greater than threshold - {0}", profileFullPath);
                            }
                        }
                        else {
                            this.logger.log(Level.INFO, "{0} file read from cache - {1}", new Object[] { command, profileFullPath });
                        }
                    }
                    else {
                        payloadContent = PayloadHandler.getInstance().readProfileFromFile(profileFullPath);
                    }
                    final XML2SyncMLMessageConverter convert = new XML2SyncMLMessageConverter();
                    payloadContent = this.appendDeviceSpecificPayload(payloadContent, resourceID, command, profileFullPath);
                    if (deviceCommand.dynamicVariable == Boolean.TRUE) {
                        final HashMap managedUserInfo = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceUDID);
                        final String userName = managedUserInfo.get("NAME");
                        final String domainName = managedUserInfo.get("DOMAIN_NETBIOS_NAME");
                        final String email = managedUserInfo.get("EMAIL_ADDRESS");
                        final Long managedUserID = Long.valueOf(String.valueOf(managedUserInfo.get("MANAGED_USER_ID")));
                        final MDMStringUtils mdmStringUtils = new MDMStringUtils();
                        payloadContent = mdmStringUtils.replaceAndEscapeMetaCharacters(payloadContent, "%username%", userName);
                        if (payloadContent.contains("%upn%")) {
                            String upn = null;
                            try {
                                upn = DirectoryUtil.getInstance().getFirstDirObjAttrValue(managedUserID, Long.valueOf(112L));
                            }
                            catch (final Exception ex) {
                                this.logger.log(Level.SEVERE, "exception in retrieving upn for {0} ex {1}", new Object[] { String.valueOf(managedUserID), ex });
                            }
                            upn = ((upn == null) ? userName : upn);
                            payloadContent = mdmStringUtils.replaceAndEscapeMetaCharacters(payloadContent, "%upn%", upn);
                            final int clientID = ((Hashtable<K, Integer>)DMDomainDataHandler.getInstance().getDomainProps(domainName, customerID)).get("CLIENT_ID");
                            if (clientID == 3 || clientID == 4) {
                                payloadContent = mdmStringUtils.replaceAndEscapeMetaCharacters(payloadContent, "%kioskDynamic%", "AzureAD\\\\");
                            }
                            else {
                                payloadContent = mdmStringUtils.replaceAndEscapeMetaCharacters(payloadContent, "%kioskDynamic%", "");
                            }
                        }
                        payloadContent = mdmStringUtils.replaceAndEscapeMetaCharacters(payloadContent, "%email%", email);
                        if (domainName == null || domainName.equalsIgnoreCase("MDM")) {
                            payloadContent = mdmStringUtils.replaceAndEscapeMetaCharacters(payloadContent, "%domainname%", "");
                        }
                        else {
                            final String actualDomainName = DirectoryUtil.getInstance().getDomainName(resourceID, domainName);
                            payloadContent = mdmStringUtils.replaceAndEscapeMetaCharacters(payloadContent, "%domainname%", actualDomainName);
                        }
                        if (payloadContent.contains("%PackageFullName%")) {
                            final Long commandID = DeviceCommandRepository.getInstance().getCommandID(deviceCommand.commandUUID);
                            final String packageFamilyName = WpCompanyHubAppHandler.getInstance().getPackageFullName(commandID, resourceID);
                            if (packageFamilyName != null) {
                                payloadContent = payloadContent.replaceAll("%PackageFullName%", packageFamilyName);
                            }
                        }
                        payloadContent = payloadContent.replaceAll("%ServerName%", ((Hashtable<K, String>)MDMUtil.getMDMServerInfo()).get("MDM_SERVER_NAME"));
                        payloadContent = payloadContent.replaceAll("%ServerPort%", Integer.toString(((Hashtable<K, Integer>)MDMUtil.getMDMServerInfo()).get("HTTPS_PORT")));
                        payloadContent = DynamicVariableHandler.replaceDynamicVariables(payloadContent, deviceUDID);
                        payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, "%profileId%", collectionID.toString());
                        if (payloadContent.contains("%challenge_password%")) {
                            payloadContent = ThirdPartyCAUtil.replaceScepChallengePasswords(payloadContent, resourceID);
                        }
                        else if (payloadContent.contains("%zerotrust_password%")) {
                            this.logger.log(Level.INFO, "Zerotrust SCEP profile: Getting san and password");
                            final Long profileID = new ProfileHandler().getProfileIDFromCollectionID(collectionID);
                            this.logger.log(Level.INFO, "Zerotrust SCEP profile: Collection id: {0}", new Object[] { collectionID });
                            final JSONObject associatedUserJSON = ProfileUtil.getInstance().getAssociatedUserForProfile(profileID);
                            this.logger.log(Level.INFO, "Zerotrust SCEP profile: Profile id: {0}", new Object[] { profileID });
                            final Long associatedUserID = (Long)associatedUserJSON.get("UserID");
                            if (associatedUserID != null) {
                                this.logger.log(Level.INFO, "Zerotrust SCEP profile: Associated user id : {0}", associatedUserID);
                                final JSONObject scepDetails = ZeroTrustAPIHandler.getInstance().getSANandPasswordFromZeroTrust(resourceID, associatedUserID);
                                if (scepDetails.getInt("http_response_code") == 200) {
                                    payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, "%zerotrust_password%", scepDetails.getString("ZEROTRUST_PASSWORD"));
                                    payloadContent = DynamicVariableHandler.replaceDynamicVariable(payloadContent, "%zerotrust_san%", scepDetails.getString("ZEROTRUST_SAN"));
                                }
                                else {
                                    this.logger.log(Level.INFO, "Zerotrust SCEP profile: Failed to get san and password");
                                }
                            }
                            else {
                                this.logger.log(Level.INFO, "Zerotrust SCEP profile: Associated user not found");
                            }
                        }
                    }
                    if (command.equals("InstallApplication") || command.equalsIgnoreCase("UpdateApplication")) {
                        final JSONObject urlResponse = WpAppSettingsHandler.getInstance().getAppInstalltionURL(cmdRequiredcheck, customerID);
                        if (urlResponse.getBoolean("success")) {
                            final HashMap requestMap = JSONUtil.getInstance().ConvertToSameDataTypeHash(jsonObject);
                            final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().getAPIKeyFromMap(requestMap);
                            String url = null;
                            url = String.valueOf(urlResponse.get("url"));
                            if (key != null) {
                                String encApiKey = key.getKeyValue();
                                if (!urlResponse.optBoolean("isMSI", (boolean)Boolean.FALSE)) {
                                    encApiKey = URLEncoder.encode(encApiKey, "UTF-8");
                                }
                                if (url.contains("getmFile.do") || url.contains("api/v1/mdm/getmfiles") || url.contains("getmFile.do")) {
                                    url = url.replaceAll("%authtoken%", StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeHtml("&" + key.getKeyName() + "=" + encApiKey)));
                                    payloadContent = payloadContent.replaceAll("%authtoken%", StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeHtml("&" + key.getKeyName() + "=" + encApiKey)));
                                    url = url.replaceAll("%deviceudid%", StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeHtml("&udid=" + deviceUDID)));
                                    payloadContent = payloadContent.replaceAll("%deviceudid%", StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeHtml("&udid=" + deviceUDID)));
                                }
                                else {
                                    url = url.replaceAll("%authtoken%", StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeHtml("?" + key.getKeyName() + "=" + encApiKey)));
                                    payloadContent = payloadContent.replaceAll("%authtoken%", StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeHtml("?" + key.getKeyName() + "=" + encApiKey)));
                                    url = url.replaceAll("%deviceudid%", StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeHtml("&udid=" + deviceUDID)));
                                    payloadContent = payloadContent.replaceAll("%deviceudid%", StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeHtml("&udid=" + deviceUDID)));
                                }
                            }
                            else {
                                url = url.replaceAll("%authtoken%", "");
                                payloadContent = payloadContent.replaceAll("%authtoken%", "");
                                url = url.replaceAll("%deviceudid%", "");
                                payloadContent = payloadContent.replaceAll("%deviceudid%", "");
                            }
                            url = url.replaceAll("%ServerName%", ((Hashtable<K, String>)MDMUtil.getMDMServerInfo()).get("MDM_SERVER_NAME"));
                            url = url.replaceAll("%ServerPort%", Integer.toString(((Hashtable<K, Integer>)MDMUtil.getMDMServerInfo()).get("HTTPS_PORT")));
                            final String customizedAppURL = urlResponse.optString("CUSTOMIZED_APP_URL");
                            if (!MDMStringUtils.isEmpty(customizedAppURL)) {
                                url = customizedAppURL;
                            }
                            payloadContent = payloadContent.replaceAll("%DownloadURL%", url);
                            payloadContent = payloadContent.replaceAll("%DependencySection%", StringEscapeUtils.escapeHtml(urlResponse.optString("dependency", "")));
                            payloadContent = WpAppSettingsHandler.getInstance().replacePayloadContentWithLicense(payloadContent, urlResponse.optString("LicenseID"), urlResponse.optString("LicenseBlob"));
                            payloadContent = MDMApiFactoryProvider.getUploadDownloadAPI().replaceDeviceAPIKeyPlaceHolderForUDWindows(payloadContent, key, true);
                            payloadContent = MDMDeviceAPIKeyGenerator.getInstance().replaceDeviceUDIDPlaceHolder(payloadContent, deviceUDID, false);
                            if (!urlResponse.optBoolean("isMSI", (boolean)Boolean.FALSE) || MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ForceURLDecode")) {
                                payloadContent = URLDecoder.decode(payloadContent, "UTF-8");
                            }
                        }
                        else {
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionID.toString(), 7, String.valueOf(urlResponse.get("remarks")));
                            DeviceCommandRepository.getInstance().deleteResourceCommand(deviceCommand.commandUUID, resourceID);
                            isProfileApplicable = Boolean.FALSE;
                        }
                    }
                    if (isProfileApplicable) {
                        final SyncMLMessage syncMLMsg = convert.transform(payloadContent);
                        final List<SyncMLRequestCommand> syncMLreqCmd = syncMLMsg.getSyncBody().getRequestCmds();
                        SyncMLRequestCommand requestCmd = syncMLreqCmd.get(0);
                        if (cmdRequiredcheck.optBoolean("isLTSB2016", (boolean)Boolean.FALSE)) {
                            requestCmd = new VersionCompatibilityHandler(VersionCompatibilityHandler.LTSB_CLASS).removeNotApplicablePayloads(requestCmd);
                        }
                        if (cmdRequiredcheck.optBoolean("isWindows11", (boolean)Boolean.FALSE)) {
                            requestCmd = new VersionCompatibilityHandler(VersionCompatibilityHandler.WIN11_CLASS).removeNotApplicablePayloads(requestCmd);
                        }
                        responseSyncML.getSyncBody().addRequestCmd(requestCmd);
                        responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
                        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(Arrays.asList(resourceID), collectionID, 3);
                        if (deviceCommand.commandUUID.contains("InstallApplication")) {
                            final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionID);
                            final Long appId = cmdRequiredcheck.getLong("appID");
                            final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
                            handler.updateAppInstallationDetailsFromDevice(resourceID, appGroupId, appId, 1, "dc.db.mdm.apps.status.Installing", 0);
                        }
                    }
                }
                else {
                    this.logger.log(Level.INFO, "Profile payloads are not applicable for the device type. Marking and NOT_APPLICABLE and updating remarks for resourceID {0} and commandUUID {1}", new Object[] { resourceID, deviceCommand.commandUUID });
                    final String accessLogNotApplicableMessage = "DATA-OUT: " + deviceCommand.commandType + this.separator + resourceID + this.separator + deviceUDID + this.separator + "Command-Not-Applicable" + this.separator + MDMUtil.getCurrentTimeInMillis();
                    this.accesslogger.log(Level.INFO, accessLogNotApplicableMessage);
                    String commandName = deviceCommand.commandUUID;
                    final String remarks = String.valueOf(cmdRequiredcheck.get("remarks"));
                    if (command.contains("InstallProfile") || command.contains("RemoveApplication")) {
                        commandName = MDMStringUtils.matchFirstOccurenceOfPattern(deviceCommand.commandUUID, "InstallProfile;Collection=[0-9]*");
                    }
                    if (command.contains("RemoveApplication")) {
                        ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, collectionID);
                    }
                    MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionID.toString(), 8, remarks);
                    DeviceCommandRepository.getInstance().deleteResourceCommand(commandName, resourceID);
                }
            }
            else if (command.equalsIgnoreCase("AppEnrollmentToken")) {
                final JSONObject tokenObject = new JSONObject();
                Properties properties = new Properties();
                properties = WpAppSettingsHandler.getInstance().getWpAETDetails(customerID);
                final HashMap deviceMap = MDMUtil.getInstance().getMDMDeviceProperties(resourceID);
                final String osVersion2 = deviceMap.get("OS_VERSION");
                if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion2, 10.0f)) {
                    tokenObject.put("Type", 2);
                    final int modelType = deviceMap.get("MODEL_TYPE");
                    tokenObject.put("ModelType", modelType);
                    tokenObject.put("CERT_FILE_PATH", (Object)properties.getProperty("CERT_FILE_PATH"));
                    final WpEnterpriseAppManagementCommand appEnrollmentCommand = new WpEnterpriseAppManagementCommand();
                    appEnrollmentCommand.processRequest(responseSyncML, tokenObject);
                }
                else {
                    tokenObject.put("Type", 1);
                    tokenObject.put("APP_ENROLLMENT_TOKEN", (Object)properties.getProperty("APP_ENROLLMENT_TOKEN"));
                    tokenObject.put("ENTERPRISE_ID", (Object)properties.getProperty("ENTERPRISE_ID"));
                    final WpEnterpriseAppManagementCommand appEnrollmentCommand2 = new WpEnterpriseAppManagementCommand();
                    appEnrollmentCommand2.processRequest(responseSyncML, tokenObject);
                }
            }
            else if (command.equalsIgnoreCase("DeviceClientSettings")) {
                final JSONObject deviceDetailsObject = new JSONObject();
                deviceDetailsObject.put("RESOURCE_ID", (Object)resourceID);
                final WpDMClientCommand clientCommand = new WpDMClientCommand();
                final int managedDeviceStatus = ManagedDeviceHandler.getInstance().getManagedDeviceStatus(resourceID);
                final boolean isWPUserUnEnroll = MDMAgentSettingsHandler.getInstance().isWPUserUnEnroll(customerID);
                final boolean isAdminEnrolledDevice = MDMEnrollmentRequestHandler.getInstance().getEnrollmentType(Long.valueOf(String.valueOf(jsonObject.get("ENROLLMENT_REQUEST_ID")))) == 3;
                deviceDetailsObject.put("MANAGED_STATUS", managedDeviceStatus);
                deviceDetailsObject.put("USER_UNENROLL", isWPUserUnEnroll);
                deviceDetailsObject.put("isAdminEnrolledDevice", isAdminEnrolledDevice);
                String osVersion3 = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
                if (osVersion3 == null) {
                    osVersion3 = (String)DBUtil.getValueFromDB("MdOSDetailsTemp", "RESOURCE_ID", (Object)resourceID, "OS_VERSION");
                }
                deviceDetailsObject.put("osVersion", (Object)osVersion3);
                clientCommand.processRequest(responseSyncML, deviceDetailsObject);
            }
            else if (command.equalsIgnoreCase("DeviceCommunicationPush")) {
                final JSONObject deviceDetailsObject = new JSONObject();
                deviceDetailsObject.put("RESOURCE_ID", (Object)resourceID);
                final WpWnsRegistrationCommand wnsRegistrationCommand = new WpWnsRegistrationCommand();
                wnsRegistrationCommand.processRequest(responseSyncML, deviceDetailsObject);
            }
            else if (command.equalsIgnoreCase("GetChannelUri")) {
                final WpWnsChannelUriCommand wpChannelUriCommand = new WpWnsChannelUriCommand();
                wpChannelUriCommand.processRequest(responseSyncML, null);
            }
            else if (command.equalsIgnoreCase("GetLocation")) {
                final WpRemoteLocateCommand wpRemoteLocateCommand = new WpRemoteLocateCommand();
                wpRemoteLocateCommand.processRequest(responseSyncML, jsonObject);
            }
            else if (command.equalsIgnoreCase("UpdateUserInfo") || command.equalsIgnoreCase("DeviceName")) {
                final String osName = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceID, "OS_NAME");
                final HashMap managedUserDetails = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceUDID);
                final JSONObject managedUserJson = new JSONObject((Map)managedUserDetails);
                jsonObject.put("managedUserJson", (Object)managedUserJson);
                jsonObject.put("ManagedDeviceExtn.NAME", (Object)ManagedDeviceHandler.getInstance().getDeviceName(resourceID));
                jsonObject.put("commandName", (Object)command);
                final int templateType = new EnrollmentTemplateHandler().getEnrollmentTemplateTypeForErid(Long.valueOf(String.valueOf(jsonObject.get("ENROLLMENT_REQUEST_ID"))));
                jsonObject.put("adminEnrollmentTemplateType", templateType);
                Boolean isWindowsPhone = Boolean.TRUE;
                if (!osName.toLowerCase().contains("phone")) {
                    isWindowsPhone = Boolean.FALSE;
                }
                jsonObject.put("isWindowsPhone", (Object)isWindowsPhone);
                final WpUserInfoUpdateCommand wpUserInfoUpdateCommand = new WpUserInfoUpdateCommand();
                wpUserInfoUpdateCommand.processRequest(responseSyncML, jsonObject);
            }
            else if (command.equalsIgnoreCase("ServerURLReplace")) {
                final JSONObject params = new JSONObject();
                params.put("commandName", (Object)command);
                final JSONObject json3 = new JSONObject();
                final Long erid = (Long)DBUtil.getValueFromDB("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID", (Object)resourceID, "ENROLLMENT_REQUEST_ID");
                json3.put("ENROLLMENT_REQUEST_ID", (Object)erid);
                final APIKey key2 = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(json3);
                if (key2 != null && key2.getVersion() == APIKey.VERSION_2_0) {
                    final HashMap managedUserDetails2 = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceUDID);
                    params.put("encapiKey", (Object)key2.getKeyValue());
                    params.put("ENROLLMENT_REQUEST_ID", (Object)erid);
                    params.put("CUSTOMER_ID", (Object)customerID);
                    params.put("MANAGED_USER_ID", managedUserDetails2.get("MANAGED_USER_ID"));
                    final WpDeviceServerUrlCommand serverUrlReplaceCommand = new WpDeviceServerUrlCommand();
                    serverUrlReplaceCommand.processRequest(responseSyncML, params);
                    this.logger.log(Level.INFO, "replaced URL sent to the device : resource ID {0}", resourceID);
                    WindowsMigrationUtil.getInstance().migrationInitated(resourceID, 1);
                }
                else {
                    this.logger.log(Level.INFO, "[Migration][Mailer] : could not generate token for a resource that had migration command : resource ID {0}", resourceID);
                    WindowsMigrationUtil.getInstance().migrationFailed(resourceID, 1);
                }
            }
            else if (command.equalsIgnoreCase("WindowsNativeAppConfig")) {
                final WindowsNativeAppConfigurationCommand windowsNativeAppConfigurationCommand = new WindowsNativeAppConfigurationCommand();
                final JSONObject config = WindowsNativeAppHandler.getInstance().getNativeAppConfigurationJSON().getJSONObject(0);
                String configkey = config.getString("value");
                configkey = DynamicVariableHandler.replaceDynamicVariables(configkey, deviceUDID);
                final HashMap requestMap2 = JSONUtil.getInstance().ConvertToSameDataTypeHash(jsonObject);
                final APIKey key3 = MDMDeviceAPIKeyGenerator.getInstance().getAPIKeyFromMap(requestMap2);
                final String encApiKey2 = key3.getKeyValue();
                configkey = configkey.replaceAll("%authtoken%", encApiKey2);
                config.put("value", (Object)configkey);
                windowsNativeAppConfigurationCommand.processRequest(responseSyncML, config);
                this.logger.log(Level.INFO, "App Config sent to the device for migration : resource ID {0}", resourceID);
                WindowsMigrationUtil.getInstance().migrationInitated(resourceID, 2);
            }
            else if (command.equalsIgnoreCase("RestartDevice")) {
                final WpRestartDeviceCommand rebootCommand = new WpRestartDeviceCommand();
                rebootCommand.processRequest(responseSyncML);
            }
            else if (command.startsWith("RestartDevice")) {
                final WpRestartDeviceCommand rebootCommand = new WpRestartDeviceCommand();
                rebootCommand.processRequest(responseSyncML);
            }
            else if (command.contains("ScepStatusCheck")) {
                final String collectionId = command.split(";")[1].split("=")[1];
                final List<Integer> configList = new ArrayList<Integer>();
                configList.add(606);
                final Criteria scepCollectionCriteria = new Criteria(Column.getColumn("CfgDataToCollection", "COLLECTION_ID"), (Object)Long.valueOf(collectionId), 0);
                final Criteria scepConfigIDCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)606, 0);
                final MDMConfigQuery configQueryObject = new MDMConfigQuery(configList, scepCollectionCriteria.and(scepConfigIDCriteria));
                final List<Column> columnList = new ArrayList<Column>();
                columnList.add(Column.getColumn("SCEPConfigurations", "SCEP_CONFIG_ID"));
                columnList.add(Column.getColumn("SCEPConfigurations", "SCEP_CONFIGURATION_NAME"));
                configQueryObject.setConfigColumns(columnList);
                final DataObject scepConfigurationsData = MDMConfigQueryUtil.getConfigDataObject(configQueryObject);
                final Iterator scepConfigDataIterator = scepConfigurationsData.getRows("SCEPConfigurations");
                final List<String> scepConfigNamesForCollection = new ArrayList<String>();
                while (scepConfigDataIterator.hasNext()) {
                    final Row scepConfigRow = scepConfigDataIterator.next();
                    final String scepConfgName = (String)scepConfigRow.get("SCEP_CONFIGURATION_NAME");
                    if (scepConfgName != null && !scepConfgName.trim().isEmpty()) {
                        scepConfigNamesForCollection.add(scepConfgName);
                    }
                }
                if (!scepConfigNamesForCollection.isEmpty()) {
                    final WpSCEPStatusCheck wpSCEPStatusCheck = new WpSCEPStatusCheck();
                    jsonObject.put("scepConfigNames", (Collection)scepConfigNamesForCollection);
                    jsonObject.put("isWindows10OrAbove", ManagedDeviceHandler.getInstance().isOsVersionGreaterThanForResource(resourceID, 10.0f));
                    jsonObject.put("COLLECTION_ID", (Object)collectionId);
                    wpSCEPStatusCheck.processRequest(responseSyncML, jsonObject);
                }
            }
            else if (command.contains("WmiQuery")) {
                final String[] wmiCommandNames = command.substring(command.indexOf(";")).split(";");
                final List<String> wmiComandNamesList = new ArrayList<String>(Arrays.asList(wmiCommandNames));
                final HashMap privacyJson3 = new PrivacySettingsHandler().getPrivacySettingsForMdDevices(resourceID);
                final int fetchMac2 = Integer.parseInt(privacyJson3.get("fetch_mac_address").toString());
                if (fetchMac2 == 2) {
                    wmiComandNamesList.remove("NetworkAdapterConfig");
                }
                jsonObject.put("WmiClasses", (Object)new JSONArray((Collection)wmiComandNamesList));
                jsonObject.put("commandName", (Object)command);
                WMIQueryHandler.getInstance().getWMIInstanceQuery(responseSyncML, jsonObject);
            }
            else if (command.contains("DisableSideloadApps") || command.contains("SideloadNotConfigured") || command.contains("EnableSideloadApps")) {
                final JSONObject cmdParams = new JSONObject();
                cmdParams.put("CommandUUID", (Object)command);
                final WpSideLoadCommand wpSideLoadCommand = new WpSideLoadCommand();
                wpSideLoadCommand.processRequest(responseSyncML, cmdParams);
            }
            else if (command.contains("WinAppInstallStatusQuery")) {
                final JSONObject seqCmdParams = SeqCmdDBUtil.getInstance().getParams(resourceID);
                final String PFN = (String)seqCmdParams.optJSONObject("cmdScopeParams").get("PackageFamilyName");
                final String installAppCommandID = String.valueOf(seqCmdParams.optJSONObject("cmdScopeParams").get("InstallAppCommandID"));
                final JSONObject seqCmdInitialParams = seqCmdParams.optJSONObject("initialParams");
                Boolean isMSI = Boolean.FALSE;
                if (seqCmdInitialParams != null && seqCmdInitialParams.has("isMSIJson")) {
                    isMSI = seqCmdInitialParams.getJSONObject("isMSIJson").optBoolean(installAppCommandID);
                }
                final JSONObject cmdParams2 = new JSONObject();
                cmdParams2.put("PackageFamilyName", (Object)PFN);
                cmdParams2.put("ResourceID", (Object)resourceID);
                cmdParams2.put("isMSI", (Object)isMSI);
                final DeviceDetails deviceDetails2 = new DeviceDetails(resourceID);
                cmdParams2.put("IsDesktop", (Object)ManagedDeviceHandler.getInstance().isWindowsDesktopOSDevice(deviceDetails2.modelType));
                final WpAppStatusCommand wpAppStatusCommand = new WpAppStatusCommand();
                wpAppStatusCommand.processRequest(responseSyncML, cmdParams2);
            }
            else if (command.contains("ApplicationConfiguration")) {
                final WindowsAppConfigCommand windowsAppConfigCommand = new WindowsAppConfigCommand();
                final JSONObject params2 = new JSONObject();
                final String clientDataParentDir2 = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
                final String profileFullPath2 = clientDataParentDir2 + File.separator + deviceCommand.commandFilePath;
                String payloadContent2 = PayloadHandler.getInstance().readProfileFromFile(profileFullPath2);
                if (!MDMStringUtils.isEmpty(payloadContent2)) {
                    payloadContent2 = DynamicVariableHandler.replaceDynamicVariables(payloadContent2, deviceUDID);
                    final HashMap requestMap3 = JSONUtil.getInstance().ConvertToSameDataTypeHash(jsonObject);
                    final APIKey key4 = MDMDeviceAPIKeyGenerator.getInstance().getAPIKeyFromMap(requestMap3);
                    final String encApiKey3 = key4.getKeyValue();
                    payloadContent2 = payloadContent2.replaceAll("%authtoken%", encApiKey3);
                    params2.put("payloadContent", (Object)payloadContent2);
                    params2.put("commandName", (Object)"ApplicationConfiguration");
                    windowsAppConfigCommand.processRequest(responseSyncML, params2);
                }
            }
            else if (command.contains("InstallLegacyAgent")) {
                final JSONObject commandJSON = new JSONObject();
                commandJSON.put("CUSTOMER_ID", (Object)customerID);
                commandJSON.put("uemPlatformType", 3);
                final JSONObject agentDetails = MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.GETLEGACY_AGENT_DETAILS, commandJSON);
                if (agentDetails != null && agentDetails.has("AgentDownloadUrl")) {
                    jsonObject.put("agentDetails", (Object)agentDetails);
                    WindowsLegacyAgentInstallHandler.getInstance().processLegacyAgentInstallation(responseSyncML, jsonObject);
                    final Long commandID2 = DeviceCommandRepository.getInstance().getCommandID(command);
                    DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandID2, resourceID, 1, 3);
                }
            }
            else if (command.contains("BlacklistAppInDevice") || command.contains("RemoveBlacklistAppInDevice")) {
                final WindowsBlacklistProcessor windowsBlacklistProcessor = new WindowsBlacklistProcessor();
                final HashMap hashMap = new HashMap();
                hashMap.put("RESOURCE_ID", resourceID);
                final HashMap blacklistMap = (HashMap)windowsBlacklistProcessor.processBlackListRequest(hashMap);
                final WinBlacklistAppCommand winBlacklistAppCommand = new WinBlacklistAppCommand();
                final JSONObject params3 = new JSONObject();
                params3.put("data", (Object)blacklistMap);
                params3.put("requestType", (Object)"BlacklistAppInDevice");
                winBlacklistAppCommand.processRequest(responseSyncML, params3);
            }
            else if (command.contains("EnrollmentTypeQuery")) {
                final WinGetEnrollmentTypeQuery winGetEnrollmentTypeQuery = new WinGetEnrollmentTypeQuery();
                winGetEnrollmentTypeQuery.processRequest(responseSyncML, new JSONObject());
            }
            else if (command.contains("TriggerROBO")) {
                final WinROBOCommand winROBOCommand = new WinROBOCommand();
            }
        }
        return responseSyncML.getSyncBody().getRequestCmds();
    }
    
    public void processWpResponseFromQueue(final DCQueueData qData) {
        String remarks = null;
        final String receivedBuffer = (String)qData.queueData;
        try {
            final XML2SyncMLMessageConverter converter = new XML2SyncMLMessageConverter();
            final SyncMLMessage requestSyncML = converter.transform(receivedBuffer);
            final SyncMLMessageParser parser = new SyncMLMessageParser();
            final JSONObject jsonObject = parser.parseSyncMLMessageHeader(requestSyncML);
            final String deviceUDID = String.valueOf(jsonObject.get("UDID"));
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
            jsonObject.put("RESOURCE_ID", (Object)resourceID);
            final Long customerID = JSONUtil.optLongForUVH(jsonObject, "CUSTOMER_ID", (Long)null);
            final JSONObject commandStatusObject = parser.parseCommandStatusMessage(requestSyncML);
            final JSONObject statusMapJson = commandStatusObject.getJSONObject("statusMap");
            this.logger.log(Level.INFO, "processWpResponseFromQueue CommandStatusMessage : {0}", commandStatusObject);
            final String commandUUID = JSONUtil.getString(commandStatusObject, "CommandUUID", null);
            final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
            if (commandUUID != null) {
                String sDeviceName = "";
                final int errorCode = commandStatusObject.optInt("ErrorCode", -1);
                int status = commandStatusObject.optInt("Status", -1);
                final JSONObject seqParams = new JSONObject();
                final String accessMessage = "DATA-IN: " + commandUUID + this.separator + resourceID + this.separator + deviceUDID + this.separator + status + this.separator + qData.postTime + this.separator + (MDMUtil.getCurrentTimeInMillis() - qData.postTime);
                this.accesslogger.log(Level.INFO, accessMessage);
                if (commandUUID.equalsIgnoreCase("InstalledApplicationList") || commandUUID.equalsIgnoreCase("PreloadedAppsInfo")) {
                    final JSONObject appDataJson = new JSONObject();
                    JSONObject appListJson = new JSONObject();
                    Boolean isErrorStatus = Boolean.FALSE;
                    final DeviceDetails deviceDetails = new DeviceDetails(resourceID);
                    if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThanForResource(resourceID, 10.0f)) {
                        WinMobileInstalledAppListCommand winMobileInstallAppCmd = new WinMobileInstalledAppListCommand();
                        if (deviceDetails.modelType == 4 || deviceDetails.modelType == 2 || deviceDetails.modelType == 3) {
                            winMobileInstallAppCmd = new WinDesktopInstalledAppListCommand();
                        }
                        appListJson = winMobileInstallAppCmd.processResponse(requestSyncML);
                        if (statusMapJson.optInt(commandUUID + ";Replace") / 100 != 2) {
                            this.logger.log(Level.WARNING, "Replace command for EnterpriseModernAppManagement/AppManagement/InventoryQuery key is failing. Need to check.");
                        }
                        if (statusMapJson.optInt(commandUUID + ";Get") / 100 != 2) {
                            isErrorStatus = Boolean.TRUE;
                            this.logger.log(Level.SEVERE, "InstalledApplicationList command has failed for Windows 10 and Above device. Need to check the same.");
                        }
                    }
                    else {
                        final SyncMLMessageParser messageParser = new SyncMLMessageParser();
                        appListJson = messageParser.parseInstalledApplicationList(requestSyncML);
                        if (statusMapJson.optInt(commandUUID + ";Get", 400) / 100 != 2 && statusMapJson.optInt("EnterpriseApps/Inventory", 404) / 100 == 4) {
                            isErrorStatus = Boolean.TRUE;
                        }
                    }
                    if (!isErrorStatus) {
                        final AppDataHandler handler = new AppDataHandler();
                        if (commandUUID.equalsIgnoreCase("PreloadedAppsInfo")) {
                            appDataJson.put("appType", 2);
                        }
                        appDataJson.put("appListJson", (Object)appListJson);
                        handler.processWindowsSoftwares(resourceID, customerID, appDataJson);
                        DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
                    }
                    this.logger.log(Level.INFO, "processWpResponseFromQueue InstalledApplicationList : {0}", appListJson);
                }
                else if (commandUUID.equalsIgnoreCase("DeviceInformation")) {
                    final WpInventory wpInv = new WpInventory();
                    MDMInvDataPopulator.getInstance().updateDeviceScanStus(resourceID, 4, "dc.common.SCANNING_IN_PROGRESS");
                    jsonObject.put("DEVICE_LOCAL_TIME", qData.postTime);
                    wpInv.updateWpInvData(jsonObject, requestSyncML);
                    MDMInvDataPopulator.getInstance().deleteDeviceScanToErrCode(resourceID);
                    remarks = "mdm.scan.scanning_successful";
                    MDMInvDataPopulator.getInstance().updateDeviceScanStus(resourceID, 2, remarks);
                    DeviceCommandRepository.getInstance().deleteResourceCommand("DeviceInformation", resourceID);
                    sDeviceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
                    final JSONObject commandStatusJSON = new CommandStatusHandler().getRecentCommandInfo(resourceID, commandId);
                    if (commandStatusJSON.has("ADDED_BY")) {
                        commandStatusJSON.put("COMMAND_STATUS", 2);
                        commandStatusJSON.put("RESOURCE_ID", (Object)resourceID);
                        commandStatusJSON.put("COMMAND_ID", (Object)commandId);
                        new CommandStatusHandler().populateCommandStatus(commandStatusJSON);
                        final String userName = DMUserHandler.getUserNameFromUserID(JSONUtil.optLongForUVH(commandStatusJSON, "ADDED_BY", Long.valueOf(-1L)));
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(2041, null, userName, "dc.mdm.actionlog.inv.device_scan_success", sDeviceName, customerID);
                    }
                    final DeviceDetails deviceDetails2 = new DeviceDetails(resourceID);
                    if (deviceDetails2.modelType == 1) {
                        CustomerInfoUtil.getInstance();
                        if (!CustomerInfoUtil.isSAS()) {
                            MDMApiFactoryProvider.getSDPIntegrationAPI().postMDMDataToSDP(qData, 20);
                        }
                    }
                }
                else if (commandUUID.equalsIgnoreCase("CorporateWipe") || commandUUID.equalsIgnoreCase("EraseDevice") || commandUUID.equalsIgnoreCase("DeviceRing") || commandUUID.equalsIgnoreCase("DeviceLock") || commandUUID.startsWith("RestartDevice")) {
                    MDMUtil.getInstance().updateSecurityCommandsStatus(deviceUDID, commandUUID, status, customerID, null, null);
                    DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
                }
                else if (commandUUID.contains("InstallApplication") || commandUUID.contains("UpdateApplication") || commandUUID.contains("RemoveApplication")) {
                    final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
                    final Long appGroupID = MDMUtil.getInstance().getAppGroupIDFromCollection(Long.parseLong(collectionId));
                    Long appID = AppsUtil.getInstance().getCompatibleAppForResource(new Long(collectionId), resourceID);
                    if (appID == null) {
                        appID = WpAppSettingsHandler.getInstance().getAppIDIfMSIApp(new Long(collectionId));
                    }
                    Boolean addAppScanCommands = Boolean.FALSE;
                    int appCatalogStatus = 0;
                    if (status == 200) {
                        if (commandUUID.contains("InstallApplication") || commandUUID.contains("UpdateApplication")) {
                            appCatalogStatus = 2;
                            if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThanForResource(resourceID, 10.0f)) {
                                appCatalogStatus = 1;
                                remarks = "dc.db.mdm.apps.status.Installing";
                                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 3, remarks);
                            }
                            else {
                                remarks = "dc.db.mdm.collection.Successfully_installed_the_app";
                                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                            }
                        }
                        else if (commandUUID.contains("RemoveApplication")) {
                            if (ProfileAssociateHandler.getInstance().isCollectionDeleteSafe(resourceID, Long.parseLong(collectionId))) {
                                appCatalogStatus = 3;
                                remarks = "dc.db.mdm.collection.Successfully_removed_the_app";
                                AppsUtil.getInstance().deleteAppResourceRel(resourceID, appGroupID);
                                new MDDeviceInstalledAppsHandler().removeInstalledAppResourceRelation(resourceID, appGroupID);
                                ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, Long.valueOf(collectionId));
                                ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
                                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                            }
                            else {
                                new MDDeviceInstalledAppsHandler().removeInstalledAppResourceRelation(resourceID, appGroupID);
                                AppsUtil.getInstance().revertInstalledAppStatus(appGroupID, resourceID);
                            }
                        }
                        if (commandUUID.contains("InstallApplication")) {
                            WpCompanyHubAppHandler.getInstance().sendWPCompanyHubAppSilentInstallMail(customerID, resourceID, appID);
                        }
                        if (commandUUID.contains("UpdateApplication") && appID.equals(WpCompanyHubAppHandler.getInstance().getWPCompanyHubAppId(customerID))) {
                            final String appVersion = (String)DBUtil.getValueFromDB("MdAppDetails", "APP_ID", (Object)appID, "APP_VERSION");
                            if (appVersion.startsWith("9.2.")) {
                                final List resourceIdList = Arrays.asList(resourceID);
                                DeviceCommandRepository.getInstance().addNativeAppChannelUriCommand(resourceIdList);
                                NotificationHandler.getInstance().SendNotification(resourceIdList, 3);
                                addAppScanCommands = Boolean.TRUE;
                            }
                        }
                    }
                    else {
                        remarks = JSONUtil.getString(commandStatusObject, "Remarks", null);
                        if (errorCode == 418 || status == 418 || WpCompanyHubAppHandler.getInstance().isMSIAlreadyInstalledStatus(commandStatusObject)) {
                            remarks = "dc.db.mdm.collection.Successfully_installed_the_app";
                            appCatalogStatus = 2;
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                        }
                        else if (commandUUID.contains("RemoveApplication") && (errorCode == 507 || status == 507)) {
                            remarks = "dc.db.mdm.collection.App_already_removed";
                            appCatalogStatus = 3;
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                            AppsUtil.getInstance().deleteAppResourceRel(resourceID, appGroupID);
                            new MDDeviceInstalledAppsHandler().removeInstalledAppResourceRelation(resourceID, appGroupID);
                            ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, Long.valueOf(collectionId));
                            ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
                        }
                        else if (errorCode == 405 || errorCode == 406 || errorCode == 501 || errorCode == 404) {
                            remarks = "mdm.profiles.incompatible_edition_or_version";
                            MDMCollectionStatusUpdate.getInstance().updateCollnToResErrorCode(resourceID, Long.valueOf(collectionId), 30007);
                        }
                        else {
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 7, remarks);
                        }
                    }
                    final String commandName = commandUUID.split(";")[0];
                    DeviceCommandRepository.getInstance().deleteResourceCommand(MDMStringUtils.matchFirstOccurenceOfPattern(commandUUID, commandName + ";Collection=[0-9]*"), resourceID);
                    final AppInstallationStatusHandler handler2 = new AppInstallationStatusHandler();
                    handler2.updateAppInstallationDetailsFromDevice(resourceID, appGroupID, appID, appCatalogStatus, remarks, 0);
                    if (commandUUID.contains("RemoveApplication")) {
                        AppsUtil.getInstance().deleteAppResourceRel(resourceID, appGroupID);
                        new MDDeviceInstalledAppsHandler().removeInstalledAppResourceRelation(resourceID, appGroupID);
                        ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, Long.parseLong(collectionId));
                    }
                    ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
                    final List resourceList = Arrays.asList(resourceID);
                    if (addAppScanCommands || WpCompanyHubAppHandler.getInstance().hasLocationSupportCompatibleAgent(resourceID)) {
                        DeviceCommandRepository.getInstance().addSyncAppCatalogCommand(resourceList);
                        DeviceCommandRepository.getInstance().addAppCatalogStatusSummaryCommand(resourceList);
                        NotificationHandler.getInstance().SendNotification(resourceList, 303);
                    }
                }
                else if (commandUUID.contains("InstallProfile")) {
                    final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
                    if (status == 200) {
                        remarks = "dc.db.mdm.collection.Successfully_applied_policy";
                        final JSONObject configuredPolicyData = MDMConfigUtil.getConfiuguredPolicyInfo(Long.valueOf(collectionId));
                        if (configuredPolicyData.has(String.valueOf(606))) {
                            final DeviceDetails deviceDetails2 = new DeviceDetails(resourceID);
                            if (ManagedDeviceHandler.getInstance().isWindowsDesktopOSDevice(deviceDetails2.modelType) && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SendAccountProfileToLaptops")) {
                                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, "dc.mdm.windows.profile_payloads_not_applied");
                            }
                            else {
                                final Properties taskProps = new Properties();
                                ((Hashtable<String, String>)taskProps).put("COLLECTION_ID", collectionId);
                                final long scheduleIn = 120000L;
                                MDMUtil.getInstance().scheduleMDMCommand(resourceID, "ScepStatusCheck", MDMUtil.getCurrentTimeInMillis() + scheduleIn, taskProps);
                            }
                        }
                        else if (configuredPolicyData.has(String.valueOf(607)) && ProfileUtil.getInstance().isClientCertificateProfile(Long.valueOf(collectionId), 607, Arrays.asList("pfx")) && ManagedDeviceHandler.getInstance().getDevicesEqualOrAboveOsVersion(Arrays.asList(resourceID), "10").size() == 0) {
                            remarks = "dc.db.mdm.collection.Cert_payload_not_compatible";
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                        }
                        else if (configuredPolicyData.has(String.valueOf(603)) || configuredPolicyData.has(String.valueOf(602))) {
                            final DeviceDetails deviceDetails2 = new DeviceDetails(resourceID);
                            if (ManagedDeviceHandler.getInstance().isWindowsDesktopOSDevice(deviceDetails2.modelType) && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SendAccountProfileToLaptops")) {
                                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, "dc.mdm.windows.profile_payloads_not_applied");
                            }
                            else {
                                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                            }
                        }
                        else if (configuredPolicyData.has(String.valueOf(608))) {
                            final DeviceDetails deviceDetails2 = new DeviceDetails(resourceID);
                            if (deviceDetails2.modelType == 1) {
                                remarks = "mdm.profile.windows.kiosk_restart";
                            }
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                        }
                        else if (configuredPolicyData.has(String.valueOf(613))) {
                            remarks = "mdm.bitlocker.user_prompted";
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                        }
                        else {
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                        }
                        final JSONObject params = new JSONObject();
                        params.put("collectionId", (Object)collectionId);
                        params.put("resourceId", (Object)resourceID);
                        params.put("handler", 1);
                        params.put("additionalParams", (Object)new JSONObject());
                        params.put("platformType", 3);
                        params.put("customerId", (Object)customerID);
                        MDMProfileResponseListenerHandler.getInstance().invokeProfileListener(params);
                    }
                    else {
                        final JSONObject configuredPolicyData = MDMConfigUtil.getConfiuguredPolicyInfo(Long.valueOf(collectionId));
                        if (status == 400) {
                            remarks = "dc.db.mdm.collection.failed_incomplete";
                        }
                        else if (configuredPolicyData.has(String.valueOf(612))) {
                            remarks = JSONUtil.getString(commandStatusObject, "Remarks", null);
                        }
                        else if (errorCode == 405 || errorCode == 406 || errorCode == 501 || errorCode == 404) {
                            remarks = "mdm.profiles.incompatible_edition_or_version";
                            MDMCollectionStatusUpdate.getInstance().updateCollnToResErrorCode(resourceID, Long.valueOf(collectionId), 30007);
                        }
                        else if (configuredPolicyData.has(String.valueOf(611))) {
                            if (statusMapJson.has("AssignedAccess/Configuration") && statusMapJson.optInt("AssignedAccess/Configuration", 200) != 200) {
                                remarks = "mdm.profile.windows.kiosk_generic_failure";
                                MDMCollectionStatusUpdate.getInstance().updateCollnToResErrorCode(resourceID, Long.valueOf(collectionId), 30008);
                            }
                        }
                        else if (configuredPolicyData.has(String.valueOf(607)) && ProfileUtil.getInstance().isClientCertificateProfile(Long.valueOf(collectionId), 607, Arrays.asList("pfx")) && ManagedDeviceHandler.getInstance().getDevicesEqualOrAboveOsVersion(Arrays.asList(resourceID), "10").size() == 0) {
                            remarks = "dc.db.mdm.collection.Cert_payload_not_compatible";
                        }
                        else if (configuredPolicyData.has(String.valueOf(603)) || configuredPolicyData.has(String.valueOf(602)) || configuredPolicyData.has(String.valueOf(606))) {
                            remarks = "mdm.profile.windows.not_applicable_for_user";
                        }
                        else {
                            remarks = JSONUtil.getString(commandStatusObject, "Remarks", null);
                        }
                        MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 7, remarks);
                    }
                    DeviceCommandRepository.getInstance().deleteResourceCommand(MDMStringUtils.matchFirstOccurenceOfPattern(commandUUID, "InstallProfile;Collection=[0-9]*"), resourceID);
                }
                else if (commandUUID.contains("RemoveProfile")) {
                    final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
                    if (MDMCollectionStatusUpdate.getInstance().isCollectionPresent(Long.parseLong(collectionId))) {
                        final JSONObject params2 = new JSONObject();
                        params2.put("collectionId", (Object)collectionId);
                        params2.put("resourceId", (Object)resourceID);
                        params2.put("additionalParams", (Object)new JSONObject());
                        params2.put("platformType", 3);
                        if (status == 200) {
                            final List configList = ConfigUtil.getConfigIds(Long.valueOf(Long.parseLong(collectionId)));
                            final HashMap deviceInfo = MDMUtil.getInstance().getMDMDeviceProperties(resourceID);
                            final String osVersion = deviceInfo.get("OS_VERSION");
                            if (configList.contains(608) && deviceInfo.get("MODEL_TYPE") == 1 && ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.0f)) {
                                final Long wipeCommand = DeviceCommandRepository.getInstance().addCommand("WindowsSelectiveWipe");
                                DeviceCommandRepository.getInstance().assignCommandToDevice(wipeCommand, ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID));
                                NotificationHandler.getInstance().SendNotification(Arrays.asList(resourceID), 303);
                                remarks = "mdm.windows.selective_wipe_remark";
                            }
                            else {
                                remarks = "dc.db.mdm.collection.Successfully_removed_the_policy";
                            }
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                            params2.put("handler", 1);
                        }
                        else if (status == -1) {
                            final List configList = ConfigUtil.getConfigIds(Long.valueOf(Long.parseLong(collectionId)));
                            if (configList.contains(612) && configList.size() == 1) {
                                remarks = "dc.db.mdm.collection.Successfully_removed_the_policy";
                                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                                params2.put("handler", 1);
                            }
                            else {
                                remarks = JSONUtil.getString(commandStatusObject, "Remarks", null);
                                MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 7, remarks);
                                params2.put("handler", 2);
                            }
                        }
                        else {
                            remarks = JSONUtil.getString(commandStatusObject, "Remarks", null);
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 7, remarks);
                            params2.put("handler", 2);
                        }
                        ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, Long.valueOf(collectionId));
                        MDMProfileResponseListenerHandler.getInstance().invokeRemoveProfileListener(params2);
                    }
                    DeviceCommandRepository.getInstance().deleteResourceCommand(MDMStringUtils.matchFirstOccurenceOfPattern(commandUUID, "RemoveProfile;Collection=[0-9]*"), resourceID);
                    ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
                }
                else if (commandUUID.contains("AppEnrollmentToken")) {
                    DeviceCommandRepository.getInstance().deleteResourceCommand("AppEnrollmentToken", resourceID);
                }
                else if (commandUUID.contains("DeviceClientSettings")) {
                    DeviceCommandRepository.getInstance().deleteResourceCommand("DeviceClientSettings", resourceID);
                }
                else if (commandUUID.contains("DeviceCommunicationPush")) {
                    final WpDeviceChannelUri wpDevChannelUri = new WpDeviceChannelUri();
                    this.checkinLogger.log(Level.INFO, "Windows MessageType:Device Communication Push Udid:{0} Channel URI: {1}", new Object[] { deviceUDID, wpDevChannelUri.getChannelUri(requestSyncML) });
                    final boolean channelUriStatus = wpDevChannelUri.updateChannelUri(jsonObject, requestSyncML);
                    if (channelUriStatus) {
                        DeviceCommandRepository.getInstance().deleteResourceCommand("DeviceCommunicationPush", resourceID);
                    }
                    else {
                        final Long commandID = DeviceCommandRepository.getInstance().getCommandID(commandUUID);
                        DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandID, resourceID, 1, 12);
                    }
                }
                else if (commandUUID.contains("GetChannelUri")) {
                    final WpDeviceChannelUri wpDevChannelUri = new WpDeviceChannelUri();
                    wpDevChannelUri.updateChannelUri(jsonObject, requestSyncML);
                    DeviceCommandRepository.getInstance().deleteResourceCommand("GetChannelUri", resourceID);
                }
                else if (commandUUID.equalsIgnoreCase("ResetPasscode")) {
                    final SyncMLMessageParser messageParser2 = new SyncMLMessageParser();
                    final JSONObject resetPasscodeJson = messageParser2.parseResetPasscodeSyncML(requestSyncML);
                    final String newPasscode = resetPasscodeJson.optString("PASSCODE", (String)null);
                    final HashMap resetPasscodeMap = new HashMap();
                    boolean isPasscodeReset = false;
                    if (status == 200 && !newPasscode.equals("")) {
                        isPasscodeReset = true;
                    }
                    else {
                        status = 500;
                    }
                    resetPasscodeMap.put("PASSCODE", newPasscode);
                    resetPasscodeMap.put("isPasscodeReset", isPasscodeReset);
                    resetPasscodeMap.put("RESOURCE_ID", resourceID);
                    resetPasscodeMap.put("CUSTOMER_ID", customerID);
                    ResetPasscodeHandler.getInstance().handleResetPasscode(resetPasscodeMap);
                    MDMUtil.getInstance().updateSecurityCommandsStatus(deviceUDID, commandUUID, status, customerID, null, null);
                    DeviceCommandRepository.getInstance().deleteResourceCommand("ResetPasscode", resourceID);
                }
                else if (commandUUID.equalsIgnoreCase("GetLocation")) {
                    final JSONObject remoteLocationObject = parser.parseRemoteLocationSyncML(requestSyncML);
                    String remarksKey = "";
                    String englishRemarks = "";
                    if (remoteLocationObject.has("Latitude")) {
                        remoteLocationObject.put("LocationUpdationTime", System.currentTimeMillis());
                        MDMGeoLocationHandler.getInstance().deleteDeviceLocationErrorCode(resourceID);
                        MDMGeoLocationHandler.getInstance().addOrUpdateDeviceLocationDetails(remoteLocationObject, deviceUDID);
                    }
                    else {
                        MDMGeoLocationHandler.getInstance().addorUpdateDeviceLocationErrorCode(resourceID, 12137);
                        status = 12137;
                        remarksKey = "dc.mdm.db.agent.location.error_msg.location_service_disabled_client";
                        englishRemarks = "Location service is not enabled in the managed device";
                    }
                    MDMUtil.getInstance().updateSecurityCommandsStatus(deviceUDID, commandUUID, status, customerID, remarksKey, englishRemarks);
                    if (remoteLocationObject.has("Latitude")) {
                        DeviceCommandRepository.getInstance().deleteResourceCommand("GetLocation", resourceID);
                    }
                }
                else if (commandUUID.equalsIgnoreCase("UpdateUserInfo") || commandUUID.equalsIgnoreCase("DeviceName")) {
                    if (status == 200) {
                        DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
                    }
                }
                else if (commandUUID.equalsIgnoreCase("ServerURLReplace")) {
                    if (status == 200) {
                        this.logger.log(Level.INFO, "Server url changed for resource ID {0} for replacing to new URL", resourceID);
                        DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
                    }
                }
                else if (commandUUID.equalsIgnoreCase("WindowsNativeAppConfig")) {
                    if (status == 200) {
                        this.logger.log(Level.INFO, "App config pushed for  resource ID {0} for replacing encapikey", resourceID);
                        DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
                    }
                }
                else if (commandUUID.contains("ScepStatusCheck")) {
                    final String collectionId = commandUUID.split(";")[1].split("=")[1];
                    final WpSCEPStatusCheck wpSCEPStatusCheck = new WpSCEPStatusCheck();
                    final JSONObject scepStatusCheckJSON = wpSCEPStatusCheck.processResponse(requestSyncML);
                    jsonObject.put("scepStatusCheckJSON", (Object)scepStatusCheckJSON);
                    jsonObject.put("collectionID", (Object)collectionId);
                    WpSCEPResponseProcessor.getInstance().processSCEPStatusCheckResponse(jsonObject);
                    DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
                }
                else if (commandUUID.contains("WmiInstancePropsQuery")) {
                    WMIQueryResponseProcessor.getInstance().processResponse(requestSyncML, resourceID);
                    DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
                }
                else if (commandUUID.contains("WinAppInstallStatusQuery")) {
                    final WpAppStatusCommand wpAppStatusCommand = new WpAppStatusCommand();
                    final JSONObject appStatusJSON = wpAppStatusCommand.processResponse(requestSyncML);
                    seqParams.put("installStatus", (Object)appStatusJSON);
                    DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
                }
                else if (commandUUID.contains("EnableSideloadApps") || commandUUID.contains("DisableSideloadApps") || commandUUID.contains("SideloadNotConfigured")) {
                    DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
                }
                else if (commandUUID.contains("ApplicationConfiguration")) {
                    DeviceCommandRepository.getInstance().deleteResourceCommand(MDMStringUtils.matchFirstOccurenceOfPattern(commandUUID, "RemoveProfile;Collection=[0-9]*"), resourceID);
                    final String collectionId = MDMUtil.getInstance().getCollectionIdFromCommandUUID(commandUUID);
                    if (status == 200) {
                        final String identifer = AppsUtil.getInstance().getAppIdentifierFromCollection(Long.parseLong(collectionId));
                        if (identifer.equalsIgnoreCase("ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2")) {
                            IosNativeAppHandler.getInstance().addorUpdateIOSAgentInstallationStatus(resourceID, 1);
                        }
                    }
                    if (status != 200) {
                        remarks = "mdm.apps.status.windows.app_config_failed";
                        MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId, 6, remarks);
                        this.logger.log(Level.WARNING, "App configuration command failed for Device : {0}  command : {1} Status : {2}", new Object[] { resourceID, commandUUID, status });
                    }
                }
                else if (commandUUID.contains("WindowsSelectiveWipe")) {
                    DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
                }
                else if (commandUUID.contains("InstallLegacyAgent")) {
                    final DeviceDetails deviceDetails3 = new DeviceDetails(resourceID);
                    if (status == 200 || deviceDetails3.modelType == 1) {
                        DeviceCommandRepository.getInstance().deleteResourceCommand("InstallLegacyAgent", resourceID);
                    }
                }
                else if (commandUUID.contains("BlacklistAppInDevice") || commandUUID.contains("RemoveBlacklistAppInDevice")) {
                    final WindowsBlacklistProcessor windowsBlacklistProcessor = new WindowsBlacklistProcessor();
                    final JSONObject resp = new JSONObject();
                    Boolean success = true;
                    if (status != 200) {
                        success = false;
                    }
                    resp.put("success", (Object)success);
                    resp.put("RESOURCE_ID", (Object)resourceID);
                    windowsBlacklistProcessor.processResponse(resp);
                    DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
                }
                else if (commandUUID.contains("TriggerROBO")) {
                    if (status != 200) {
                        this.logger.log(Level.WARNING, "[Windows][Enrollment][Autopilot] : A ROBO Command was sent to the device but was not accepted by the device {0}", status);
                    }
                    else {
                        this.logger.log(Level.INFO, "[Windows][Enrollment][Autopilot] :  ROBO request initated for device ");
                    }
                    DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
                }
                else if (commandUUID.contains("EnrollmentTypeQuery")) {
                    final JSONObject parsedResponse = new WinGetEnrollmentTypeQuery().processResponse(requestSyncML);
                    final String enrolType = parsedResponse.optString("EnrollmentType", "");
                    if (enrolType.equals("Device")) {
                        this.logger.log(Level.INFO, "[Windows][Enrollment][Autopilot] : Full enrollment device found adding ROBO trigger {0}", resourceID);
                        final Long commandID = DeviceCommandRepository.getInstance().addCommand("TriggerROBO");
                        final List resList = new ArrayList();
                        resList.add(resourceID);
                    }
                    DeviceCommandRepository.getInstance().deleteResourceCommand(commandUUID, resourceID);
                }
                final String cmdUUID = WindowsSeqCmdUtil.getInstance().removeTypeFromUUID(commandUUID);
                if (SeqCmdUtils.getInstance().isSequentialCommandResponse(resourceID, cmdUUID)) {
                    seqParams.put("status", status);
                    seqParams.put("resourceID", (Object)resourceID);
                    seqParams.put("statusMap", (Object)commandStatusObject.getJSONObject("statusMap"));
                    WindowsSeqCmdUtil.getInstance().processWinSeqCmd(cmdUUID, seqParams);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in processResponse of WpSyncMLProcessor", ex);
        }
    }
    
    SyncMLMessage createResponseMessage(final SyncMLMessage requestSyncML) {
        SyncMLMessage responseSyncML = null;
        if (requestSyncML != null) {
            responseSyncML = new SyncMLMessage();
            responseSyncML.setSyncHeader(this.getResponseSyncHeaderMessage(requestSyncML));
            responseSyncML.setSyncBody(this.getResponseSyncBodyMessage(requestSyncML));
        }
        return responseSyncML;
    }
    
    SyncHeaderMessage getResponseSyncHeaderMessage(final SyncMLMessage requestSyncML) {
        SyncHeaderMessage responseHeader = null;
        if (requestSyncML != null) {
            responseHeader = new SyncHeaderMessage();
            final SyncHeaderMessage deviceSyncHeader = requestSyncML.getSyncHeader();
            responseHeader.setVerDTD(deviceSyncHeader.getVerDTD());
            responseHeader.setVerProto(deviceSyncHeader.getVerProto());
            responseHeader.setSessionID(deviceSyncHeader.getSessionID());
            responseHeader.setTarget(deviceSyncHeader.getSource());
            final Location sourceLocation = deviceSyncHeader.getTarget();
            sourceLocation.setLocName("MEMDM");
            responseHeader.setSource(sourceLocation);
            responseHeader.setMsgID(deviceSyncHeader.getMsgID());
        }
        return responseHeader;
    }
    
    SyncBodyMessage getResponseSyncBodyMessage(final SyncMLMessage requestSyncML) {
        SyncBodyMessage responseBody = null;
        if (requestSyncML != null) {
            responseBody = new SyncBodyMessage();
            final long currentTime = System.currentTimeMillis();
            final String messageReference = requestSyncML.getSyncHeader().getMsgID();
            final String targetReference = requestSyncML.getSyncHeader().getTarget().getLocUri();
            final String sourceReference = requestSyncML.getSyncHeader().getSource().getLocUri();
            final StatusResponseCommand syncHeaderStatus = new StatusResponseCommand();
            syncHeaderStatus.setCmdId(Long.toString(currentTime));
            syncHeaderStatus.setMsgRef(messageReference);
            syncHeaderStatus.setCmd("SyncHdr");
            syncHeaderStatus.setCmdRef("0");
            syncHeaderStatus.setTargetRef(targetReference);
            syncHeaderStatus.setSourceRef(sourceReference);
            syncHeaderStatus.setData(String.valueOf(212));
            responseBody.addResponseCmd(syncHeaderStatus);
            final List requestCmds = requestSyncML.getSyncBody().getRequestCmds();
            if (requestCmds != null) {
                for (int i = 0; i < requestCmds.size(); ++i) {
                    final SyncMLRequestCommand requestCmd = requestCmds.get(i);
                    final StatusResponseCommand requestStatus = new StatusResponseCommand();
                    requestStatus.setCmdId(requestCmd.getRequestCmdId());
                    requestStatus.setMsgRef(messageReference);
                    requestStatus.setCmdRef(requestCmd.getRequestCmdId());
                    requestStatus.setCmd(requestCmd.getSyncMLCommandName());
                    requestStatus.setData(String.valueOf(200));
                    responseBody.addResponseCmd(requestStatus);
                }
            }
        }
        return responseBody;
    }
    
    public boolean isNewEnrollRequest(final Long deviceID, final Long enrollRequestID) {
        boolean isNewRequest = true;
        try {
            final Long existingEnrollRequestID = MDMEnrollmentUtil.getInstance().getEnrollRequestIDFromManagedDeviceID(deviceID);
            if (existingEnrollRequestID != null && enrollRequestID.equals(existingEnrollRequestID)) {
                isNewRequest = false;
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return isNewRequest;
    }
    
    private void handleUnEnrollmentRequest(final SyncMLMessage requestSyncML, final String deviceUDID) {
        try {
            final SyncMLMessageParser messageParser = new SyncMLMessageParser();
            final JSONArray jsonAlertValues = messageParser.parseSyncMLAlertRequest(requestSyncML);
            for (int i = 0; i < jsonAlertValues.length(); ++i) {
                final JSONObject alert = jsonAlertValues.getJSONObject(i);
                if (!alert.isNull("AlertItemMetaType") && !alert.isNull("AlertItemDataValue")) {
                    final String alertItemMetaType = String.valueOf(alert.get("AlertItemMetaType"));
                    final String alertItemDataValue = String.valueOf(alert.get("AlertItemDataValue"));
                    if (alertItemMetaType.equalsIgnoreCase("com.microsoft:mdm.unenrollment.userrequest") && alertItemDataValue.equalsIgnoreCase("1")) {
                        this.checkinLogger.log(Level.INFO, "Windows MessageType:CheckOut Udid:{0}", new Object[] { deviceUDID });
                        String sRemarks = "dc.mdm.profile.ios.remarks.removed_from_device";
                        Boolean isDeviceUnmanaged = false;
                        final Properties properties = new Properties();
                        ((Hashtable<String, String>)properties).put("UDID", deviceUDID);
                        if (InventoryUtil.getInstance().isWipedFromServer(deviceUDID)) {
                            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
                            final int ownedby = ManagedDeviceHandler.getInstance().getDeviceOwnership(resourceID);
                            sRemarks = "mdm.deprovision.old_remark";
                            ((Hashtable<String, Boolean>)properties).put("WipeCmdFromServer", true);
                            final JSONObject json = ManagedDeviceHandler.getInstance().getDeprovisiondetails(resourceID);
                            int managedStatus = -1;
                            String deprovisionRemarks = "";
                            if (json != null) {
                                managedStatus = json.optInt("MANAGED_STATUS", -1);
                                deprovisionRemarks = json.optString("REMARKS", "");
                            }
                            if (json != null && managedStatus != -1 && deprovisionRemarks != null && deprovisionRemarks != "") {
                                ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", managedStatus);
                                ((Hashtable<String, String>)properties).put("REMARKS", deprovisionRemarks);
                            }
                            else if (ownedby == 1) {
                                sRemarks = "mdm.deprovision.old_remark";
                                managedStatus = 10;
                                ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", managedStatus);
                                ((Hashtable<String, String>)properties).put("REMARKS", sRemarks);
                            }
                            else {
                                sRemarks = "mdm.deprovision.retire_remark";
                                managedStatus = 11;
                                ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", managedStatus);
                                ((Hashtable<String, String>)properties).put("REMARKS", sRemarks);
                            }
                        }
                        else {
                            ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", new Integer(4));
                            ((Hashtable<String, String>)properties).put("REMARKS", sRemarks);
                            isDeviceUnmanaged = true;
                        }
                        ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", 3);
                        final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
                        if (IosNativeAppHandler.getInstance().isIOSNativeAgentInstalled(resourceId)) {
                            DeviceCommandRepository.getInstance().addCorporateWipeCommand(deviceUDID);
                            NotificationHandler.getInstance().SendNotification(Arrays.asList(resourceId), 303);
                            IosNativeAppHandler.getInstance().addorUpdateIOSAgentInstallationStatus(resourceId, 0);
                        }
                        final Long resourceID2 = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
                        DeviceCommandRepository.getInstance().deleteResourceCommand("RemoveDevice", deviceUDID, 1);
                        if (!DeviceCommandRepository.getInstance().hasDeviceCommandInCacheOrRepo(deviceUDID) && ManagedDeviceHandler.getInstance().removeDeviceInTrash(resourceID2)) {
                            return;
                        }
                        if (ManagedDeviceHandler.getInstance().isDeviceRemoved(resourceID2)) {
                            DeviceCommandRepository.getInstance().clearCommandFromDevice(deviceUDID, resourceID2, "RemoveDevice", 1);
                            if (!DeviceCommandRepository.getInstance().hasDeviceCommandInCacheOrRepo(deviceUDID)) {
                                ManagedDeviceHandler.getInstance().removeDeviceInTrash(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID));
                            }
                        }
                        else {
                            ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(properties);
                            if (isDeviceUnmanaged) {
                                final List remarksList = new ArrayList();
                                remarksList.add(ManagedDeviceHandler.getInstance().getDeviceName(resourceID2));
                                MDMEventLogHandler.getInstance().addEvent(2001, null, "mdm.unmanage.user_revoke_management", remarksList, CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID2), System.currentTimeMillis());
                                final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                                logJSON.put((Object)"REMARKS", (Object)"deprovision-success");
                                logJSON.put((Object)"RESOURCE_ID", (Object)resourceID2);
                                logJSON.put((Object)"UDID", (Object)deviceUDID);
                                MDMOneLineLogger.log(Level.INFO, "DEVICE_UNMANAGED", logJSON);
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private String appendDeviceSpecificPayload(String payloadContent, final Long resourceID, final String command, final String profileFullPath) {
        String type = null;
        final String osVersion = null;
        final HashMap deviceMap = MDMUtil.getInstance().getMDMDeviceProperties(resourceID);
        if (command.equalsIgnoreCase("InstallProfile") || command.equalsIgnoreCase("InstallApplication")) {
            type = "install";
        }
        else if (command.equalsIgnoreCase("RemoveProfile") || command.equalsIgnoreCase("RemoveApplication")) {
            type = "remove";
        }
        else if (command.equalsIgnoreCase("UpdateApplication")) {
            type = "update";
        }
        payloadContent = this.getReplacedContent(payloadContent, deviceMap, type, profileFullPath, command);
        return payloadContent;
    }
    
    private String getReplacedContent(String payloadContent, final HashMap devDetailsMap, final String commandType, final String profileFullPath, final String command) {
        for (final String placeHolder : this.dynamicPayloadStrings) {
            if (payloadContent.contains(placeHolder)) {
                final String filePrefixName = this.getFileNamePrefixForOSVersionAndPlaceHolder(devDetailsMap, placeHolder);
                String fileName = profileFullPath.replace(commandType + "_profile.xml", filePrefixName + "_" + commandType + ".xml");
                if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(fileName) && placeHolder.contains("restriction")) {
                    fileName = profileFullPath.replace(commandType + "_profile.xml", "WindowsPhone81_" + commandType + ".xml");
                }
                payloadContent = this.getReplacedContentForPlaceholder(payloadContent, fileName, placeHolder, command);
            }
        }
        return payloadContent;
    }
    
    private String getFileNamePrefixForOSVersionAndPlaceHolder(final HashMap devDetailsMap, final String placeHolder) {
        String retVal = null;
        final String osVersion = devDetailsMap.get("OS_VERSION");
        final int modelType = devDetailsMap.get("MODEL_TYPE");
        if (placeHolder.contains("restriction")) {
            if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.0f)) {
                if (ManagedDeviceHandler.getInstance().isWin10RedstoneOrAboveOSVersion(osVersion)) {
                    if (modelType != 1) {
                        retVal = "Windows10Desktop";
                    }
                    else {
                        retVal = "Windows10Mobile";
                    }
                }
                else {
                    retVal = "WindowsPhone81";
                }
            }
            else if (ManagedDeviceHandler.getInstance().isWindows81OrAboveDevice(osVersion)) {
                retVal = "WindowsPhone81";
            }
            else {
                retVal = "WindowsPhone8";
            }
        }
        else if (placeHolder.contains("passcode")) {
            if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.0f)) {
                retVal = "Windows10MobilePasscode";
            }
            else {
                retVal = "WindowsPhone81Passcode";
            }
        }
        else if (placeHolder.contains("certificate")) {
            if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.0f)) {
                retVal = "Windows10MobileCertificate";
            }
            else {
                retVal = "WindowsPhone81Certificate";
            }
        }
        else if (placeHolder.contains("scep")) {
            if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.0f)) {
                retVal = "Windows10MobileScep";
            }
            else if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 8.1f)) {
                retVal = "WindowsPhone81Scep";
            }
        }
        else if (placeHolder.contains("activesync")) {
            retVal = "WindowsActiveSync";
        }
        else if (placeHolder.contains("email")) {
            retVal = "WindowsEmail";
        }
        else if (placeHolder.contains("app")) {
            if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.0f)) {
                if (modelType == 1) {
                    retVal = "Windows10MobileApp";
                }
                else {
                    retVal = "Windows10DesktopApp";
                }
            }
            else if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 8.1f)) {
                retVal = "WindowsPhone81App";
            }
        }
        else if (placeHolder.contains("lockdown")) {
            if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.0f)) {
                if (modelType == 1) {
                    retVal = "Windows10MobileLockdown";
                }
                else {
                    retVal = "Windows10DesktopLockdown";
                }
            }
            else if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 8.1f)) {
                retVal = "WindowsPhone81Lockdown";
            }
        }
        return retVal;
    }
    
    private String getReplacedContentForPlaceholder(String payloadContent, final String fileFullPath, final String placeHolderValueToReplace, final String command) {
        try {
            String replacementString = "";
            String wpProfileContent;
            if (command.equals("InstallProfile")) {
                final String cacheName = MDMMetaDataUtil.getInstance().getFileCanonicalPath(fileFullPath);
                wpProfileContent = (String)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName, 2);
                if (wpProfileContent == null) {
                    wpProfileContent = PayloadHandler.getInstance().readProfileFromFile(fileFullPath);
                    if (wpProfileContent.length() <= MDMFileUtil.fileSizeCacheThreshold) {
                        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
                        wpProfileContent = PayloadSecretFieldsHandler.getInstance().replaceAllPayloadSecrets(wpProfileContent, customerID);
                        ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, (Object)wpProfileContent, 2, (int)MDMFileUtil.fileCacheTimeTTL);
                        this.logger.log(Level.INFO, "File added in cache - {0}", fileFullPath);
                    }
                    else {
                        this.logger.log(Level.INFO, "File size greater than threshold - {0}", fileFullPath);
                    }
                }
                else {
                    this.logger.log(Level.INFO, "{0} file read from cache - {1}", new Object[] { command, fileFullPath });
                }
            }
            else {
                wpProfileContent = PayloadHandler.getInstance().readProfileFromFile(fileFullPath);
            }
            if (wpProfileContent != null && !wpProfileContent.trim().equalsIgnoreCase("")) {
                final XML2SyncMLMessageConverter converter = new XML2SyncMLMessageConverter();
                final SyncMLMessage win8SyncMLMsg = converter.transform(wpProfileContent);
                final SyncBodyMessage win8SyncBodyMsg = win8SyncMLMsg.getSyncBody();
                final List win8SyncBodyRequests = win8SyncBodyMsg.getRequestCmds();
                AtomicRequestCommand reqCmd = null;
                DeleteRequestCommand nonAtomicDeleteRequestCommand = null;
                if (win8SyncBodyRequests.get(0) instanceof SequenceRequestCommand) {
                    final SequenceRequestCommand seqCmd = win8SyncBodyRequests.get(0);
                    final List<SyncMLRequestCommand> requestCmds = seqCmd.getRequestCmds();
                    for (final SyncMLRequestCommand requestCmd : requestCmds) {
                        if (requestCmd instanceof AtomicRequestCommand) {
                            reqCmd = (AtomicRequestCommand)requestCmd;
                        }
                        else {
                            if (!(requestCmd instanceof DeleteRequestCommand)) {
                                continue;
                            }
                            nonAtomicDeleteRequestCommand = (DeleteRequestCommand)requestCmd;
                        }
                    }
                }
                else {
                    reqCmd = win8SyncBodyRequests.get(0);
                }
                if (reqCmd.getRequestCmds() != null) {
                    SyncMLRequestCommand innerReqCmd = null;
                    if (placeHolderValueToReplace.contains("nonAtomicDelete")) {
                        if (nonAtomicDeleteRequestCommand == null) {
                            payloadContent = payloadContent.replaceAll("<Delete><CmdID>[A-Za-z0-9-;=.]*</CmdID><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item></Delete>", "");
                            payloadContent = payloadContent.replaceAll("<Delete><Item><Target><LocURI>" + placeHolderValueToReplace + "<[/]LocURI><[/]Target><[/]Item><CmdID>[A-Za-z0-9-;=.]*<[/]CmdID><[/]Delete>", "");
                            payloadContent = payloadContent.replaceAll("<Item><Target><LocURI>" + placeHolderValueToReplace + "<[/]LocURI><[/]Target><[/]Item>", "");
                        }
                        else {
                            innerReqCmd = nonAtomicDeleteRequestCommand;
                        }
                    }
                    else {
                        final List<SyncMLRequestCommand> win8ReqCmds = reqCmd.getRequestCmds();
                        final List<Class> instanceCheckClazz = new ArrayList<Class>();
                        if (placeHolderValueToReplace.contains("exec")) {
                            instanceCheckClazz.add(ExecRequestCommand.class);
                        }
                        else if (placeHolderValueToReplace.contains("add")) {
                            instanceCheckClazz.add(AddRequestCommand.class);
                        }
                        else {
                            instanceCheckClazz.add(ReplaceRequestCommand.class);
                            instanceCheckClazz.add(DeleteRequestCommand.class);
                        }
                        for (final SyncMLRequestCommand innerCmd : reqCmd.getRequestCmds()) {
                            for (final Class checkClazz : instanceCheckClazz) {
                                if (checkClazz.isInstance(innerCmd)) {
                                    innerReqCmd = innerCmd;
                                }
                            }
                        }
                    }
                    if (innerReqCmd != null) {
                        final List itemList = innerReqCmd.getRequestItems();
                        replacementString = this.createReplacementStringFromItemList(itemList);
                        payloadContent = payloadContent.replaceAll("<Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item>", replacementString);
                    }
                    else {
                        payloadContent = payloadContent.replaceAll("<Add><CmdID>[A-Za-z0-9-;=.]*</CmdID><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item></Add>", "");
                        payloadContent = payloadContent.replaceAll("<Add><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item><CmdID>[A-Za-z0-9-;=.]*</CmdID></Add>", "");
                        payloadContent = payloadContent.replaceAll("<Replace><CmdID>[A-Za-z0-9-;=.]*</CmdID><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item></Replace>", "");
                        payloadContent = payloadContent.replaceAll("<Replace><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item><CmdID>[A-Za-z0-9-;=.]*</CmdID></Replace>", "");
                        payloadContent = payloadContent.replaceAll("<Delete><CmdID>[A-Za-z0-9-;=.]*</CmdID><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item></Delete>", "");
                        payloadContent = payloadContent.replaceAll("<Delete><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item><CmdID>[A-Za-z0-9-;=.]*</CmdID></Delete>", "");
                        payloadContent = payloadContent.replaceAll("<Exec><CmdID>[A-Za-z0-9-;=.]*</CmdID><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item></Exec>", "");
                        payloadContent = payloadContent.replaceAll("<Exec><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item><CmdID>[A-Za-z0-9-;=.]*</CmdID></Exec>", "");
                        payloadContent = payloadContent.replaceAll("<Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item>", "");
                    }
                }
                else {
                    payloadContent = payloadContent.replaceAll("<Replace><CmdID>.*<[/]CmdID><Item><Target><LocURI>%restriction_payload_xml%<[/]LocURI><[/]Target><[/]Item><[/]Replace>", replacementString);
                }
            }
            else {
                payloadContent = this.removePlaceholderFromPayload(payloadContent, placeHolderValueToReplace);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getReplacedContent of WpServerRequestHandler {0}", ex);
        }
        return payloadContent;
    }
    
    private String removePlaceholderFromPayload(String payloadContent, final String placeHolderValueToReplace) {
        payloadContent = payloadContent.replaceAll("<Delete><CmdID>[A-Za-z0-9-;=.]*</CmdID><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item></Delete>", "");
        payloadContent = payloadContent.replaceAll("<Delete><Item><Target><LocURI>" + placeHolderValueToReplace + "<[/]LocURI><[/]Target><[/]Item><CmdID>[A-Za-z0-9-;=.]*<[/]CmdID><[/]Delete>", "");
        payloadContent = payloadContent.replaceAll("<Add><CmdID>[A-Za-z0-9-;=.]*</CmdID><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item></Add>", "");
        payloadContent = payloadContent.replaceAll("<Add><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item><CmdID>[A-Za-z0-9-;=.]*</CmdID></Add>", "");
        payloadContent = payloadContent.replaceAll("<Replace><CmdID>[A-Za-z0-9-;=.]*</CmdID><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item></Replace>", "");
        payloadContent = payloadContent.replaceAll("<Replace><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item><CmdID>[A-Za-z0-9-;=.]*</CmdID></Replace>", "");
        payloadContent = payloadContent.replaceAll("<Exec><CmdID>[A-Za-z0-9-;=.]*</CmdID><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item></Exec>", "");
        payloadContent = payloadContent.replaceAll("<Exec><Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item><CmdID>[A-Za-z0-9-;=.]*</CmdID></Exec>", "");
        payloadContent = payloadContent.replaceAll("<Item><Target><LocURI>" + placeHolderValueToReplace + "<[/]LocURI><[/]Target><[/]Item>", "");
        payloadContent = payloadContent.replaceAll("<Item><Target><LocURI>" + placeHolderValueToReplace + "</LocURI></Target></Item>", "");
        return payloadContent;
    }
    
    private String createReplacementStringFromItemList(final List itemList) {
        String replacementString = "";
        for (final Item item : itemList) {
            replacementString += item.transform();
        }
        return replacementString;
    }
    
    private void deleteWMIQueryFromMdCommandsToDevice(final Long resourceID, final SyncMLMessage requestSyncML) {
        final List responseCmds = requestSyncML.getSyncBody().getResponseCmds();
        String cmdRef = null;
        for (int i = 0; i < responseCmds.size(); ++i) {
            final SyncMLResponseCommand response = responseCmds.get(i);
            if (response instanceof StatusResponseCommand && response.getCmd().equalsIgnoreCase("Sequence")) {
                cmdRef = response.getCmdRef();
                break;
            }
        }
        if (cmdRef != null) {
            DeviceCommandRepository.getInstance().deleteResourceCommand(cmdRef, resourceID);
        }
    }
    
    private boolean hasOnlyAccountPayload(final JSONObject configIDs) {
        Boolean hasOnlyAccountPayload = Boolean.FALSE;
        final String[] onlyAccountProfiles = { "603", "602", "606" };
        int accountProfileCnt = 0;
        for (final String profile : onlyAccountProfiles) {
            if (configIDs.has(profile)) {
                ++accountProfileCnt;
            }
        }
        if (configIDs.length() == accountProfileCnt) {
            hasOnlyAccountPayload = Boolean.TRUE;
        }
        return hasOnlyAccountPayload;
    }
    
    private JSONObject checkCollectionProfileNotApplicableForResource(final Long resourceID, final String commandUUID) throws Exception {
        final JSONObject response = new JSONObject();
        String remarks = "";
        Boolean doNotSendCmd = Boolean.FALSE;
        if (commandUUID.contains("InstallApplication") || commandUUID.contains("UpdateApplication") || commandUUID.contains("RemoveApplication")) {
            final HashMap deviceMap = MDMUtil.getInstance().getMDMDeviceProperties(resourceID);
            final Long collectionID = Long.valueOf(commandUUID.substring(commandUUID.lastIndexOf("=") + 1));
            Long appID = AppsUtil.getInstance().getCompatibleAppForResource(collectionID, deviceMap);
            if (appID == null) {
                appID = WpAppSettingsHandler.getInstance().getAppIDIfMSIApp(collectionID);
                if (appID == null) {
                    doNotSendCmd = true;
                    remarks = "mdm.windows.app_payloads_not_applicable_type";
                }
                else {
                    doNotSendCmd = false;
                    response.put("appID", (Object)appID);
                }
            }
            else {
                doNotSendCmd = false;
                response.put("appID", (Object)appID);
            }
            int supportedDevice = 24;
            try {
                supportedDevice = MDMUtil.getInstance().getSupportedDevice(appID);
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "exception in getting supported device");
            }
            final int modelType = deviceMap.get("MODEL_TYPE");
            response.put("MODEL_TYPE", modelType);
            response.put("PROCESSOR_ARCHITECTURE", deviceMap.get("PROCESSOR_ARCHITECTURE"));
            response.put("OS_VERSION", deviceMap.get("OS_VERSION"));
            if (supportedDevice == 8 && modelType == 1 && !doNotSendCmd) {
                final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
                if (!ManagedDeviceHandler.getInstance().isOsVersionGreaterThanForResource(resourceID, 10.0f)) {
                    if (!WpAppSettingsHandler.getInstance().isAETUploaded(customerID)) {
                        doNotSendCmd = true;
                        remarks = "mdm.windows.aet_not_uploaded";
                    }
                }
                else {
                    final String clientDataParentDir = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
                    final String profileInstallDirectory = MDMMetaDataUtil.getInstance().getMdmProfileFolderPath(customerID, "profiles", collectionID);
                    final String profileFile = clientDataParentDir + File.separator + profileInstallDirectory + File.separator + "Windows10MobileApp";
                    if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(profileFile + "_install.xml")) {
                        doNotSendCmd = true;
                        try {
                            final Long releaseLabel = AppVersionDBUtil.getInstance().getReleaseLabelIdForAppCollectionId(collectionID);
                            final Long packageID = AppsUtil.getInstance().getAppPackageId(appID);
                            final String redirectURL = "#/uems/mdm/manage/appRepo/apps/windows/enterprise?appId=" + packageID + "&labelId=" + releaseLabel;
                            remarks = "mdm.windows.save_profile_again@@@" + redirectURL;
                        }
                        catch (final Exception e2) {
                            this.logger.log(Level.WARNING, "cannot fetch profile ID while checing if profile applicable for windows");
                        }
                    }
                }
            }
        }
        else if (commandUUID.contains("InstallProfile") || commandUUID.contains("RemoveProfile")) {
            final Long collectionID2 = Long.valueOf(commandUUID.substring(commandUUID.lastIndexOf("=") + 1));
            final JSONObject configIDs = MDMConfigUtil.getConfiuguredPolicyInfo(collectionID2);
            final Boolean hasOnlyAccountPayload = this.hasOnlyAccountPayload(configIDs);
            Boolean doNotSendAccountPayload = Boolean.FALSE;
            final HashMap deviceMap2 = MDMUtil.getInstance().getMDMDeviceProperties(resourceID);
            if (hasOnlyAccountPayload && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SendAccountProfileToLaptops")) {
                final int modelType2 = deviceMap2.get("MODEL_TYPE");
                if (ManagedDeviceHandler.getInstance().isWindowsDesktopOSDevice(modelType2)) {
                    doNotSendAccountPayload = Boolean.TRUE;
                }
            }
            final String osName = deviceMap2.get("OS_NAME");
            if (osName != null) {
                response.put("isLTSB2016", osName.toLowerCase().contains("ltsb") && osName.toLowerCase().contains("2016"));
            }
            final String osversion = deviceMap2.get("OS_VERSION");
            if (osversion != null && ManagedDeviceHandler.getInstance().isWin11OrAboveOSVersion(osversion)) {
                response.put("isWindows11", (Object)Boolean.TRUE);
            }
            doNotSendCmd = doNotSendAccountPayload;
            remarks = "dc.mdm.windows.profile_payloads_not_applicable";
        }
        try {
            response.put("doNotSendCmd", (Object)doNotSendCmd);
            response.put("remarks", (Object)remarks);
        }
        catch (final JSONException e3) {
            this.logger.log(Level.WARNING, "error in checking if command applicable");
        }
        return response;
    }
    
    private boolean containsOnlyAADUserTokenAlertRequestCmd(final SyncMLMessage requestSyncML) {
        Boolean hasOnlyAADUserTokenAlert = Boolean.FALSE;
        final List<SyncMLRequestCommand> requestCmds = requestSyncML.getSyncBody().getRequestCmds();
        if (requestCmds.size() == 1) {
            final Item requestItem = requestCmds.get(0).getRequestItems().get(0);
            hasOnlyAADUserTokenAlert = requestItem.getMeta().getType().equalsIgnoreCase("com.microsoft/MDM/AADUserToken");
        }
        return hasOnlyAADUserTokenAlert;
    }
}
