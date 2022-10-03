package com.me.mdm.server.windows.asset;

import java.util.Map;
import com.me.mdm.core.windows.commands.WpDeviceInformationCommand;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import java.util.Set;
import com.me.mdm.server.inv.InventoryCertificateDataHandler;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.List;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import java.util.Collections;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import com.me.mdm.server.settings.battery.MdDeviceBatteryDetailsDBHandler;
import java.util.Collection;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.responsecmds.ResultsResponseCommand;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;
import java.util.ArrayList;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import org.json.JSONObject;

public class WpInventory
{
    public void updateWpInvData(final JSONObject jsonObject, final SyncMLMessage syncMLMessage) throws Exception {
        final Long postTime = jsonObject.getLong("DEVICE_LOCAL_TIME");
        final Long resourceID = jsonObject.getLong("RESOURCE_ID");
        final Long customerID = JSONUtil.optLongForUVH(jsonObject, "CUSTOMER_ID", (Long)null);
        final String deviceUDID = String.valueOf(jsonObject.get("UDID"));
        final HashMap deviceDetls = new HashMap();
        final HashMap inNodeDataMap = new HashMap();
        final List inNodeDataNodes = new ArrayList();
        inNodeDataNodes.add("^\\.\\/Vendor\\/MSFT\\/DeviceStatus\\/CellularIdentities\\/[0-9]{15}(\\/.*)?");
        final List responseCmds = syncMLMessage.getSyncBody().getResponseCmds();
        for (int i = 0; i < responseCmds.size(); ++i) {
            final SyncMLResponseCommand response = responseCmds.get(i);
            if (response instanceof ResultsResponseCommand) {
                final List responseItems = response.getResponseItems();
                for (int j = 0; j < responseItems.size(); ++j) {
                    final Item responseItem = responseItems.get(j);
                    final String localUri = responseItem.getSource().getLocUri();
                    final String data = (String)responseItem.getData();
                    deviceDetls.put(localUri, data);
                    final String regExMatch = MDMStringUtils.isValueMatchinList(localUri, inNodeDataNodes);
                    if (regExMatch != null) {
                        this.processinvalueNodeData(regExMatch, inNodeDataMap, localUri, data);
                    }
                }
            }
        }
        final String sDevType = deviceDetls.get("./DevDetail/DevTyp");
        final String sOEM = deviceDetls.get("./DevDetail/OEM");
        final String sFwV = deviceDetls.get("./DevDetail/FwV");
        final String sSwV = deviceDetls.get("./DevDetail/SwV");
        final String sHwV = deviceDetls.get("./DevDetail/HwV");
        final String sLrgObj = deviceDetls.get("./DevDetail/LrgObj");
        final String sMaxDepth = deviceDetls.get("./DevDetail/URI/MaxDepth");
        final String sMaxTotLen = deviceDetls.get("./DevDetail/URI/MaxTotLen");
        final String sMaxSegLen = deviceDetls.get("./DevDetail/URI/MaxSegLen");
        final String sMobileID = deviceDetls.get("./DevDetail/Ext/Microsoft/MobileID");
        final String sLocalTime = deviceDetls.get("./DevDetail/Ext/Microsoft/LocalTime");
        String sOSPlatform = deviceDetls.get("./DevDetail/Ext/Microsoft/OSPlatform");
        final String sProcessorType = deviceDetls.get("./DevDetail/Ext/Microsoft/ProcessorType");
        final String sRadioSwV = deviceDetls.get("./DevDetail/Ext/Microsoft/RadioSwV");
        final String sResolution = deviceDetls.get("./DevDetail/Ext/Microsoft/Resolution");
        String sCommercializationOperator_1 = deviceDetls.get("./DevDetail/Ext/Microsoft/CommercializationOperator");
        final String sProcessorArchitecture = deviceDetls.get("./DevDetail/Ext/Microsoft/ProcessorArchitecture");
        final String sDeviceName = deviceDetls.get("./DevDetail/Ext/Microsoft/DNSComputerName");
        final String sWLANMACAddress = deviceDetls.get("./DevDetail/Ext/WLANMACAddress");
        final String sWLANIPV4Address = deviceDetls.get("./DevDetail/Ext/WlanIPv4Address");
        final String sWLANIPV6Address = deviceDetls.get("./DevDetail/Ext/WlanIPv6Address");
        final String serialNumber = deviceDetls.get("./DevDetail/Ext/Microsoft/SMBIOSSerialNumber");
        final String sEncryption = deviceDetls.get("./Vendor/MSFT/BitLocker/Status/DeviceEncryptionStatus");
        final String sPasscodePresent = (deviceDetls.get("./Vendor/MSFT/DeviceLock/DeviceValue/DevicePasswordEnabled") == null) ? ((deviceDetls.get("./Vendor/MSFT/PolicyManager/Device/DeviceLock/DevicePasswordEnabled") == null) ? deviceDetls.get("./Vendor/MSFT/Policy/Result/DeviceLock/DevicePasswordEnabled") : deviceDetls.get("./Vendor/MSFT/PolicyManager/Device/DeviceLock/DevicePasswordEnabled")) : deviceDetls.get("./Vendor/MSFT/DeviceLock/DeviceValue/DevicePasswordEnabled");
        final String sMan = deviceDetls.get("./DevInfo/Man");
        final String sMod = deviceDetls.get("./DevInfo/Mod");
        String isRoaming = deviceDetls.get("./Vendor/MSFT/DeviceInstanceService/Identity/Identity1/Roaming");
        final String phoneNo = deviceDetls.get("./Vendor/MSFT/DeviceInstanceService/Identity/Identity1/PhoneNumber");
        String imsi = deviceDetls.get("./Vendor/MSFT/DeviceInstanceService/Identity/Identity1/IMSI");
        String imei = deviceDetls.get("./Vendor/MSFT/DeviceInstanceService/Identity/Identity1/IMEI");
        String isRoaming_2 = deviceDetls.get("./Vendor/MSFT/DeviceInstanceService/Identity/Identity2/Roaming");
        final String phoneNo_2 = deviceDetls.get("./Vendor/MSFT/DeviceInstanceService/Identity/Identity2/PhoneNumber");
        String imsi_2 = deviceDetls.get("./Vendor/MSFT/DeviceInstanceService/Identity/Identity2/IMSI");
        String imei_2 = deviceDetls.get("./Vendor/MSFT/DeviceInstanceService/Identity/Identity2/IMEI");
        String sCommercializationOperator_2 = sCommercializationOperator_1;
        if (ManagedDeviceHandler.getInstance().isWin11OrAboveOSVersion(sSwV)) {
            sOSPlatform = sOSPlatform.replace("10", "11");
        }
        final HashMap imeiNode = inNodeDataMap.get(inNodeDataNodes.get(0).split("\\(")[0].substring(1));
        if (imeiNode != null) {
            final List keySet = new ArrayList(imeiNode.keySet());
            imei = keySet.get(0);
            final HashMap imei1Map = imeiNode.get(imei);
            imsi = imei1Map.get("IMSI");
            sCommercializationOperator_1 = imei1Map.get("CommercializationOperator");
            isRoaming = imei1Map.get("RoamingStatus");
            if (keySet.size() > 1) {
                imei_2 = keySet.get(1);
                final HashMap imei2Map = imeiNode.get(imei_2);
                imsi_2 = imei2Map.get("IMSI");
                sCommercializationOperator_2 = imei2Map.get("CommercializationOperator");
                isRoaming_2 = imei2Map.get("RoamingStatus");
            }
        }
        final String easDeviceIdentifier = deviceDetls.get("./Vendor/MSFT/DMClient/Provider/MEMDM/ExchangeID");
        String deviceMemory = deviceDetls.get("./DevDetail/Ext/Microsoft/TotalStorage");
        final String deviceRAM = deviceDetls.get("./DevDetail/Ext/Microsoft/TotalRAM");
        if (deviceMemory != null && !deviceMemory.trim().isEmpty()) {
            final Float devMem = Float.parseFloat(deviceMemory) / 1024.0f;
            deviceMemory = devMem.toString();
        }
        final String batteryLevel = deviceDetls.get("./Vendor/MSFT/DeviceStatus/Battery/EstimatedChargeRemaining");
        final HashMap deviceInfoHash = new HashMap();
        deviceInfoHash.put("OS_NAME", sOSPlatform);
        deviceInfoHash.put("OS_VERSION", sSwV);
        deviceInfoHash.put("BUILD_VERSION", "--");
        deviceInfoHash.put("PROCESSOR_TYPE", sProcessorType);
        deviceInfoHash.put("PROCESSOR_ARCHITECTURE", this.processArchitectureType(sProcessorArchitecture));
        deviceInfoHash.put("MODEM_FIRMWARE_VERSION", sFwV);
        if (imei != null && !MDMStringUtils.isEmpty(imei)) {
            deviceInfoHash.put("IMEI", imei);
        }
        deviceInfoHash.put("EAS_DEVICE_IDENTIFIER", easDeviceIdentifier);
        deviceInfoHash.put("DEVICE_CAPACITY", deviceMemory);
        deviceInfoHash.put("TOTAL_RAM_MEMORY", deviceRAM);
        deviceInfoHash.put("BATTERY_LEVEL", batteryLevel);
        try {
            final JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("BATTERY_LEVEL", (Object)batteryLevel);
            final String deviceTime = MdDeviceBatteryDetailsDBHandler.convertMillisecondsToDate(postTime);
            jsonObject2.put("DEVICE_LOCAL_TIME", (Object)deviceTime);
            MdDeviceBatteryDetailsDBHandler.addOrUpdateBatteryDetails(resourceID, new JSONArray().put((Object)jsonObject2));
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.INFO, "Exception while adding battery details");
        }
        if (!MDMStringUtils.isEmpty(serialNumber)) {
            deviceInfoHash.put("SERIAL_NUMBER", serialNumber);
        }
        deviceInfoHash.put("MODEL_NAME", sMod);
        deviceInfoHash.put("PRODUCT_NAME", sMan);
        if (sOSPlatform.contains("Phone") || sOSPlatform.contains("Mobile")) {
            deviceInfoHash.put("MODEL_TYPE", "1");
        }
        else {
            deviceInfoHash.put("MODEL_TYPE", "3");
        }
        deviceInfoHash.put("MODEL", sDevType);
        final HashMap networkInfoHash = new HashMap();
        networkInfoHash.put("WIFI_MAC", sWLANMACAddress);
        String wifiIP = null;
        if (sWLANIPV4Address != null && !sWLANIPV4Address.trim().isEmpty()) {
            wifiIP = sWLANIPV4Address;
        }
        if (sWLANIPV6Address != null && !sWLANIPV6Address.trim().isEmpty()) {
            if (wifiIP != null) {
                wifiIP = wifiIP + "," + sWLANIPV6Address.split("/")[0];
            }
            else {
                wifiIP = sWLANIPV6Address.split("/")[0];
            }
        }
        networkInfoHash.put("WIFI_IP", wifiIP);
        networkInfoHash.put("BLUETOOTH_MAC", "--");
        final ArrayList simArrayList = new ArrayList();
        final HashMap simInfoHash = new HashMap();
        simInfoHash.put("IMSI", imsi);
        simInfoHash.put("IMEI", imei);
        simInfoHash.put("PHONE_NUMBER", phoneNo);
        simInfoHash.put("IS_ROAMING", isRoaming);
        simInfoHash.put("CURRENT_CARRIER_NETWORK", sCommercializationOperator_1);
        if (imei != null) {
            simInfoHash.put("SLOT", 1);
        }
        simArrayList.add(simInfoHash);
        final HashMap simInfoHash_2 = new HashMap();
        simInfoHash_2.put("IMSI", imsi_2);
        simInfoHash_2.put("IMEI", imei_2);
        simInfoHash_2.put("PHONE_NUMBER", phoneNo_2);
        simInfoHash_2.put("IS_ROAMING", isRoaming_2);
        simInfoHash_2.put("CURRENT_CARRIER_NETWORK", sCommercializationOperator_2);
        if (imei != null) {
            simInfoHash.put("SLOT", 2);
        }
        simArrayList.add(simInfoHash_2);
        final HashMap securityInfoHash = new HashMap();
        securityInfoHash.put("STORAGE_ENCRYPTION", sEncryption);
        if (sPasscodePresent != null) {
            if (sPasscodePresent.equalsIgnoreCase("0")) {
                securityInfoHash.put("PasscodePresent", Boolean.toString(Boolean.TRUE));
            }
            else if (sPasscodePresent.equalsIgnoreCase("1")) {
                securityInfoHash.put("PasscodePresent", Boolean.toString(Boolean.FALSE));
            }
        }
        final HashMap deviceNameUpdate = new HashMap();
        deviceNameUpdate.put("DeviceName", sDeviceName);
        deviceNameUpdate.put("UDID", deviceUDID);
        if (sEncryption != null) {
            if (sEncryption.equalsIgnoreCase("0")) {
                final Criteria remarkStatusCriteria = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)8, 1);
                final Criteria configIdCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)613, 0);
                ProfileUtil.getInstance().updateMdmConfigStatus(Collections.singletonList(resourceID), 6, "mdm.bitlocker.turned_on", remarkStatusCriteria.and(configIdCriteria));
                securityInfoHash.put("StorageEncryption", Boolean.toString(Boolean.TRUE));
            }
            else {
                securityInfoHash.put("StorageEncryption", Boolean.toString(Boolean.FALSE));
            }
        }
        final JSONObject devOSJSON = new JSONObject();
        devOSJSON.put("RESOURCE_ID", (Object)resourceID);
        devOSJSON.put("CUSTOMER_ID", (Object)customerID);
        devOSJSON.put("OS_VERSION", (Object)sSwV);
        MDMInvDataPopulator.getInstance().addOrUpdateSimInfo(resourceID, simArrayList);
        this.deviceOSVersionUpgradeHandler(devOSJSON);
        MDMInvDataPopulator.getInstance().addOrUpdateDeviceInfo(resourceID, deviceInfoHash);
        MDMInvDataPopulator.getInstance().addOrUpdateNetworkInfo(resourceID, networkInfoHash);
        MDMInvDataPopulator.getInstance().addOrUpdateIOSSecurityInfo(resourceID, securityInfoHash);
        MDMInvDataPopulator.getInstance().updateDeviceName(resourceID, deviceNameUpdate, 3);
        final JSONObject deviceIdsJSON = new JSONObject();
        if (!MDMStringUtils.isEmpty(sWLANMACAddress)) {
            deviceIdsJSON.put("WIFI_MAC", (Object)sWLANMACAddress);
        }
        if (!MDMStringUtils.isEmpty(imei)) {
            deviceIdsJSON.put("IMEI", (Object)imei);
        }
        if (!MDMStringUtils.isEmpty(imei_2)) {
            String imeiString = deviceIdsJSON.optString("IMEI");
            if (!MDMStringUtils.isEmpty(imeiString)) {
                imeiString = imeiString + "," + imei_2;
            }
            else {
                imeiString = imei_2;
            }
            deviceIdsJSON.put("IMEI", (Object)imeiString);
        }
        if (!MDMStringUtils.isEmpty(easDeviceIdentifier)) {
            deviceIdsJSON.put("EAS_DEVICE_IDENTIFIER", (Object)easDeviceIdentifier);
        }
        if (deviceIdsJSON.length() != 0) {
            deviceIdsJSON.put("UDID", (Object)deviceUDID);
            ManagedDeviceHandler.getInstance().addOrUpdateManagedDeviceUniqueIdsRow(resourceID, deviceIdsJSON);
        }
        this.processCertificateDetails(deviceDetls, resourceID);
        this.processRestrictionDetails(deviceDetls, resourceID);
        final DeviceEvent deviceEvent = new DeviceEvent(resourceID, customerID);
        ManagedDeviceHandler.getInstance().invokeDeviceListeners(deviceEvent, 10);
    }
    
    private void deviceOSVersionUpgradeHandler(final JSONObject jsonObject) throws Exception {
        final Long resourceID = jsonObject.getLong("RESOURCE_ID");
        final Long customerID = JSONUtil.optLongForUVH(jsonObject, "CUSTOMER_ID", (Long)null);
        final String sSwV = (String)jsonObject.get("OS_VERSION");
        final String oldOSVersion = MDMUtil.getInstance().getMDMDeviceProperties(resourceID).get("OS_VERSION");
        if (ManagedDeviceHandler.getInstance().isWindows81OrAboveDevice(sSwV) && !ManagedDeviceHandler.getInstance().isWindows81OrAboveDevice(oldOSVersion) && MDMAgentSettingsHandler.getInstance().getNotificaitonServiceType(3, customerID) == 1) {
            final ArrayList deviceList = new ArrayList();
            deviceList.add(resourceID);
            Logger.getLogger("MDMLogger").log(Level.INFO, "adding device communication push command for resource id {0} as OS is upgraded to 8.1 and above", resourceID);
            DeviceCommandRepository.getInstance().addDeviceCommunicationCommand(deviceList);
        }
        if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(sSwV, 10.0f) && oldOSVersion != null && !oldOSVersion.equalsIgnoreCase(sSwV)) {
            Logger.getLogger("MDMLogger").log(Level.INFO, "adding system apps query command for resource id {0} as OS is upgraded to {1} to {2}", new Object[] { resourceID, oldOSVersion, sSwV });
            DeviceCommandRepository.getInstance().addWindowsCommand(Arrays.asList(resourceID), "PreloadedAppsInfo");
        }
    }
    
    private void processCertificateDetails(final HashMap deviceDetails, final Long resourceId) throws Exception {
        final Logger logger = Logger.getLogger("MDMLogger");
        logger.log(Level.INFO, "inside WpInventory.processCertificateDetails() -> resourceID {0}", resourceId);
        final Set keys = deviceDetails.keySet();
        final JSONObject certificateObject = new JSONObject();
        final JSONArray certificateList = new JSONArray();
        for (final Object key : keys) {
            if (key.toString().contains("EncodedCertificate")) {
                try {
                    final JSONObject certificateInfo = CertificateUtils.parseX509Certificate(deviceDetails.get(key).toString());
                    certificateInfo.put("CERTIFICATE_CONTENT", (Object)deviceDetails.get(key).toString());
                    certificateList.put((Object)certificateInfo);
                }
                catch (final Exception e) {
                    logger.log(Level.WARNING, "Exception while parsing cert : {0}", key);
                }
            }
        }
        certificateObject.put("CertificateList", (Object)certificateList);
        final MDMInvdetails inventoryObject = new MDMInvdetails(resourceId, certificateObject.toString(), 0);
        final InventoryCertificateDataHandler certificateProcessor = new InventoryCertificateDataHandler();
        certificateProcessor.populateInventoryData(inventoryObject);
    }
    
    private void processCertificate(final String encodedCertificate, final DataObject dataObject, final Long resourceId) throws Exception {
        final JSONObject certificateDetails = CertificateUtils.parseX509Certificate(encodedCertificate);
        MDMInvDataPopulator.getInstance().addOrUpdateCertificatesInfo(resourceId, certificateDetails, dataObject);
    }
    
    private Boolean processRestrictionDetails(final HashMap restrictionDetails, final Long resourceID) {
        boolean isSuccessful = Boolean.TRUE;
        Integer value = null;
        final HashMap<String, String> keyToDBColumnName = new WpDeviceInformationCommand().getKeyToDBColumnNameMap();
        final HashMap<String, Integer> deviceDetailsMap = new HashMap<String, Integer>();
        final Set keySet = restrictionDetails.keySet();
        for (final Object key : keySet) {
            if (key.toString().endsWith("Storage/Disable")) {
                if (restrictionDetails.get(key.toString()).equals("true")) {
                    value = 0;
                }
                else {
                    value = 1;
                }
                deviceDetailsMap.put(keyToDBColumnName.get(key.toString()), new Integer(value));
            }
            if (key.toString().contains("PolicyManager") || key.toString().endsWith("Status/DeviceEncryptionStatus") || key.toString().contains("Policy/")) {
                value = Integer.parseInt(restrictionDetails.get(key).toString());
                deviceDetailsMap.put(keyToDBColumnName.get(key.toString()), new Integer(value));
            }
        }
        try {
            MDMInvDataPopulator.getInstance().addOrUpdateWindowsRestriction(resourceID, deviceDetailsMap);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            isSuccessful = Boolean.FALSE;
        }
        return isSuccessful;
    }
    
    private String processArchitectureType(final String code) {
        String architecture = code;
        if (code != null && code.matches("\\d")) {
            if (code.equals("0")) {
                architecture = "X86";
            }
            else if (code.equals("1")) {
                architecture = "MIPS";
            }
            else if (code.equals("2")) {
                architecture = "Alpha";
            }
            else if (code.equals("3")) {
                architecture = "PowerPC";
            }
            else if (code.equals("5")) {
                architecture = "ARM";
            }
            else if (code.equals("6")) {
                architecture = "ia64";
            }
            else if (code.equals("9")) {
                architecture = "X64";
            }
        }
        return architecture;
    }
    
    private void processinvalueNodeData(final String regEx, final HashMap inNodeDataMap, final String localUri, final String data) {
        final String baseRegex = regEx.split("\\(")[0].substring(1);
        HashMap valueMap = inNodeDataMap.get(baseRegex);
        if (valueMap == null) {
            valueMap = new HashMap();
        }
        final String[] splits = localUri.split("/");
        if (localUri.matches(baseRegex)) {
            final String key = splits[splits.length - 1];
            HashMap dataMap = valueMap.get(key);
            if (dataMap == null) {
                dataMap = new HashMap();
            }
            valueMap.put(key, dataMap);
        }
        else {
            final String baseKey = splits[splits.length - 2];
            final String key2 = splits[splits.length - 1];
            HashMap dataMap2 = valueMap.get(baseKey);
            if (dataMap2 == null) {
                dataMap2 = new HashMap();
            }
            dataMap2.put(key2, data);
            valueMap.put(baseKey, dataMap2);
        }
        inNodeDataMap.put(baseRegex, valueMap);
    }
}
