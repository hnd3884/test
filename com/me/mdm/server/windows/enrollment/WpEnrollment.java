package com.me.mdm.server.windows.enrollment;

import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.enrollment.MDMEnrollmentDeviceHandler;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.me.mdm.core.windows.SyncMLMessageParser;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.net.URLDecoder;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.HashMap;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;
import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.data.Location;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.requestcmds.ExecRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.List;
import java.util.ArrayList;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WpEnrollment
{
    Logger logger;
    private static WpEnrollment windowsEnroll;
    
    public WpEnrollment() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static synchronized WpEnrollment getInstance() {
        if (WpEnrollment.windowsEnroll == null) {
            WpEnrollment.windowsEnroll = new WpEnrollment();
        }
        return WpEnrollment.windowsEnroll;
    }
    
    public void enrollmentRequest(final SyncMLMessage requestSyncML, final SyncMLMessage responseSyncML) {
        try {
            final GetRequestCommand devInfoGet = new GetRequestCommand();
            devInfoGet.setRequestCmdId("Enrollment");
            final ArrayList items = new ArrayList();
            items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/DNSComputerName"));
            devInfoGet.setRequestItems(items);
            responseSyncML.getSyncBody().addRequestCmd(devInfoGet);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public void removeDeviceRequest(final SyncMLMessage requestSyncML, final SyncMLMessage responseSyncML) {
        try {
            final AtomicRequestCommand atomicCommand = new AtomicRequestCommand();
            atomicCommand.setRequestCmdId("CorporateWipe");
            final ExecRequestCommand execCommand = new ExecRequestCommand();
            execCommand.setRequestCmdId("CorporateWipe");
            execCommand.addRequestItem(this.createExecCommandItemTagElemet("./Vendor/MSFT/DMClient/Unenroll", "MEMDM"));
            atomicCommand.addRequestCmd(execCommand);
            responseSyncML.getSyncBody().addRequestCmd(atomicCommand);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public void remoteWipe(final SyncMLMessage requestSyncML, final SyncMLMessage responseSyncML, final JSONObject wipeOptionData) {
        try {
            final AtomicRequestCommand atomicCommand = new AtomicRequestCommand();
            atomicCommand.setRequestCmdId("EraseDevice");
            String locUri = "./Vendor/MSFT/RemoteWipe/doWipeProtected";
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("StopWindowsProtectedWipe") || !wipeOptionData.optBoolean("isRedstone2AboveDevice", (boolean)Boolean.FALSE)) {
                locUri = "./Vendor/MSFT/RemoteWipe/doWipe";
            }
            final ExecRequestCommand execCommand = new ExecRequestCommand();
            execCommand.setRequestCmdId("EraseDevice");
            if (wipeOptionData.optBoolean("RetainMDM", (boolean)Boolean.FALSE)) {
                final Boolean isRedstone4AboveDevice = wipeOptionData.optBoolean("isRedstone4AboveDevice", (boolean)Boolean.FALSE);
                final Boolean isPPKGEnrolledDevice = wipeOptionData.optBoolean("isPPKGEnrollment", (boolean)Boolean.FALSE);
                if (isPPKGEnrolledDevice) {
                    locUri = "./Vendor/MSFT/RemoteWipe/doWipePersistProvisionedData";
                    execCommand.addRequestItem(this.createTargetItemTagElement(locUri));
                    atomicCommand.addRequestCmd(execCommand);
                }
                else if (isRedstone4AboveDevice) {
                    locUri = "./Vendor/MSFT/RemoteWipe/AutomaticRedeployment/doAutomaticRedeployment";
                    final ReplaceRequestCommand replaceRequestCommand = new ReplaceRequestCommand();
                    replaceRequestCommand.setRequestCmdId("EraseDevice;Replace");
                    replaceRequestCommand.addRequestItem(this.createTargetItemTagElementWithData("./Vendor/MSFT/Policy/Config/CredentialProviders/DisableAutomaticReDeploymentCredentials", "0"));
                    execCommand.addRequestItem(this.createTargetItemTagElement(locUri));
                    final SequenceRequestCommand sequenceRequestCommand = new SequenceRequestCommand();
                    sequenceRequestCommand.addRequestCmd(replaceRequestCommand);
                    sequenceRequestCommand.addRequestCmd(execCommand);
                    atomicCommand.addRequestCmd(sequenceRequestCommand);
                }
                this.logger.log(Level.INFO, "Auto redep command was initiated {0}", wipeOptionData);
            }
            else {
                execCommand.addRequestItem(this.createTargetItemTagElement(locUri));
                atomicCommand.addRequestCmd(execCommand);
            }
            responseSyncML.getSyncBody().addRequestCmd(atomicCommand);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public void selectiveWipe(final SyncMLMessage requestSyncML, final SyncMLMessage responseSyncML) {
        try {
            final AtomicRequestCommand atomicCommand = new AtomicRequestCommand();
            atomicCommand.setRequestCmdId("WindowsSelectiveWipe");
            final ExecRequestCommand execCommand = new ExecRequestCommand();
            execCommand.setRequestCmdId("WindowsSelectiveWipe");
            execCommand.addRequestItem(this.createTargetItemTagElement("./Vendor/MSFT/RemoteWipe/doWipePersistProvisionedData"));
            atomicCommand.addRequestCmd(execCommand);
            responseSyncML.getSyncBody().addRequestCmd(atomicCommand);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    Item createReplaceCommandItemTagElement(final String locationUri, final String itemData) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        final Meta meta = new Meta();
        meta.setFormat("bool");
        item.setMeta(meta);
        item.setData(itemData);
        return item;
    }
    
    Item createExecCommandItemTagElemet(final String locationUri, final String itemData) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        final Meta meta = new Meta();
        meta.setFormat("chr");
        item.setMeta(meta);
        item.setData(itemData);
        return item;
    }
    
    Item createTargetItemTagElement(final String locationUri) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        return item;
    }
    
    Item createTargetItemTagElementWithData(final String locationUri, final String data) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        item.setData(data);
        final Meta meta = new Meta();
        meta.setFormat("int");
        item.setMeta(meta);
        return item;
    }
    
    public void updateWpEnrollmentResponse(final SyncMLMessage requestSyncML, final SyncMLResponseCommand response) throws SyMException {
        try {
            final HashMap deviceDetls = new HashMap();
            final List responseItems = response.getResponseItems();
            for (int j = 0; j < responseItems.size(); ++j) {
                final Item responseItem = responseItems.get(j);
                deviceDetls.put(responseItem.getSource().getLocUri(), responseItem.getData());
            }
            final String deviceName = deviceDetls.get("./DevDetail/Ext/Microsoft/DNSComputerName");
            final String osVersion = deviceDetls.get("./DevDetail/SwV");
            final String easDeviceIdentifier = deviceDetls.get("./Vendor/MSFT/DMClient/Provider/MEMDM/ExchangeID");
            final String sMan = deviceDetls.get("./DevInfo/Man");
            final String sMod = deviceDetls.get("./DevInfo/Mod");
            final String sDevType = deviceDetls.get("./DevDetail/DevTyp");
            final String imeiPrimary = deviceDetls.get("./Vendor/MSFT/DeviceInstanceService/Identity/Identity1/IMEI");
            final String imeiSecondary = deviceDetls.get("./Vendor/MSFT/DeviceInstanceService/Identity/Identity2/IMEI");
            final String sOSPlatform = deviceDetls.get("./DevDetail/Ext/Microsoft/OSPlatform");
            final String serverTargetUrl = requestSyncML.getSyncHeader().getTarget().getLocUri();
            final int lastIndex = serverTargetUrl.lastIndexOf("SerialNumber=");
            String serialNumber = deviceDetls.get("./DevDetail/Ext/Microsoft/SMBIOSSerialNumber");
            if (lastIndex != -1 && MDMStringUtils.isEmpty(serialNumber)) {
                serialNumber = URLDecoder.decode(serverTargetUrl.substring(lastIndex + "SerialNumber=".length()), "UTF-8");
            }
            String imei = "";
            if (imeiSecondary == null) {
                imei = imeiPrimary;
            }
            else if (imeiPrimary == null) {
                imei = imeiSecondary;
            }
            else {
                final DataObject assignedIMEIDO = SyMUtil.getPersistence().get("DeviceForEnrollment", new Criteria(Column.getColumn("DeviceForEnrollment", "IMEI"), (Object)new String[] { imeiPrimary, imeiSecondary }, 8));
                if (assignedIMEIDO.isEmpty()) {
                    imei = imeiPrimary;
                }
                else {
                    imei = (String)assignedIMEIDO.getFirstValue("DeviceForEnrollment", "IMEI");
                }
            }
            final SyncMLMessageParser parser = new SyncMLMessageParser();
            final JSONObject headerJSON = parser.parseSyncMLMessageHeader(requestSyncML);
            final Long enrollmentRequestId = Long.parseLong(headerJSON.optString("ENROLLMENT_REQUEST_ID"));
            final Long customerId = Long.parseLong(headerJSON.optString("CUSTOMER_ID"));
            final Long managedUserId = Long.parseLong(headerJSON.optString("MANAGED_USER_ID"));
            final String deviceUDID = headerJSON.optString("UDID");
            final JSONObject properties = new JSONObject();
            properties.put("CUSTOMER_ID", (Object)customerId);
            properties.put("UDID", (Object)deviceUDID);
            properties.put("NAME", (Object)deviceName);
            properties.put("DOMAIN_NETBIOS_NAME", (Object)"MDM");
            properties.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestId);
            properties.put("MANAGED_USER_ID", (Object)managedUserId);
            properties.put("MANAGED_STATUS", (Object)new Integer(2));
            properties.put("AGENT_TYPE", (Object)new Integer(4));
            properties.put("REMARKS", (Object)"dc.mdm.db.agent.enroll.agent_enroll_finished");
            properties.put("PLATFORM_TYPE", 3);
            properties.put("REQUEST_STATUS", 3);
            properties.put("isAppleConfig", false);
            properties.put("IMEI", (Object)imei);
            properties.put("EAS_DEVICE_IDENTIFIER", (Object)easDeviceIdentifier);
            if (!MDMStringUtils.isEmpty(serialNumber)) {
                properties.put("SerialNumber", (Object)serialNumber);
                properties.put("SERIAL_NUMBER", (Object)serialNumber);
            }
            final String genericID = new DeviceForEnrollmentHandler().getGenericIDFromERID(enrollmentRequestId, customerId);
            if (!MDMStringUtils.isEmpty(genericID)) {
                properties.put("GENERIC_IDENTIFIER", (Object)genericID);
            }
            final JSONObject modelAndDeviceInfo = new JSONObject();
            modelAndDeviceInfo.put("MODEL_NAME", (Object)sMan);
            modelAndDeviceInfo.put("PRODUCT_NAME", (Object)sMod);
            modelAndDeviceInfo.put("MODEL", (Object)sDevType);
            if (sOSPlatform.contains("Phone") || sOSPlatform.contains("Mobile")) {
                modelAndDeviceInfo.put("MODEL_TYPE", 1);
            }
            else {
                modelAndDeviceInfo.put("MODEL_TYPE", 3);
                properties.put("RESOURCE_TYPE", 121);
            }
            modelAndDeviceInfo.put("EAS_DEVICE_IDENTIFIER", (Object)easDeviceIdentifier);
            modelAndDeviceInfo.put("IMEI", (Object)imei);
            properties.put("MdModelInfo", (Object)modelAndDeviceInfo);
            final int enrollmentType = MDMEnrollmentRequestHandler.getInstance().getEnrollmentType(enrollmentRequestId);
            MDMEnrollmentDeviceHandler.getInstance(enrollmentType).enrollDevice(properties);
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
            this.updateTempOSDetails(resourceID, osVersion);
            final JSONObject deviceIdsJSON = new JSONObject();
            deviceIdsJSON.put("UDID", (Object)deviceUDID);
            ManagedDeviceHandler.getInstance().addOrUpdateManagedDeviceUniqueIdsRow(resourceID, deviceIdsJSON);
            final JSONObject deviceForEnrollmentJSON = new JSONObject();
            deviceForEnrollmentJSON.put("EAS_DEVICE_IDENTIFIER", (Object)easDeviceIdentifier);
            new DeviceForEnrollmentHandler().updateDeviceForEnrollmentProps("UDID", deviceUDID, deviceForEnrollmentJSON);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in updateWpEnrollmentResponse ", ex);
        }
    }
    
    private void updateTempOSDetails(final Long resourceID, final String osVersion) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("MdOSDetailsTemp", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject dataObject = MDMUtil.getPersistence().get("MdOSDetailsTemp", criteria);
            if (dataObject.isEmpty()) {
                final Row tempRow = new Row("MdOSDetailsTemp");
                tempRow.set("RESOURCE_ID", (Object)resourceID);
                tempRow.set("OS_VERSION", (Object)osVersion);
                dataObject.addRow(tempRow);
            }
            else {
                final Row tempRow = dataObject.getFirstRow("MdOSDetailsTemp");
                tempRow.set("OS_VERSION", (Object)osVersion);
                dataObject.updateRow(tempRow);
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, " Exception in updateTempOSDetails ", exp);
        }
    }
    
    static {
        WpEnrollment.windowsEnroll = null;
    }
}
