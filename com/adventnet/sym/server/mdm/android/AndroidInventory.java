package com.adventnet.sym.server.mdm.android;

import java.util.List;
import com.me.mdm.server.notification.NotificationHandler;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.inv.AppDataHandler;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.me.mdm.server.settings.location.MDMGeoLocationHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONException;
import java.util.HashMap;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.logging.Logger;

public class AndroidInventory
{
    public static final String SOFTWARE_DETAILS = "SOFTWARE_DETAILS";
    public static final String HARDWARE_DETAILS = "HARDWARE_DETAILS";
    public static final String NETWORK_DETAILS = "NETWORK_DETAILS ";
    public static final String DISKINFO = "DISKINFO";
    public static final String DEVICEINFO = "DEVICEINFO";
    public static final String MEMORYINFO = "MEMORYINFO";
    public static final String CPUINFO = "CPUINFO";
    public static final String BATTERYINFO = "BATTERYINFO";
    public static final String TELEPHONEINFO = "TELEPHONEINFO";
    public static final String WIFIINFO = "WIFIINFO";
    public static final String QUOTES = "\"";
    public static final String PHONE_MEMORY_TAG = "/data:";
    public static final String INTERNAL_MEMORY_TAG = "/mnt/sdcard:";
    public static final String EXTERNAL_MEMORY_TAG = "/mnt/sdcard/_ExternalSD:";
    public static final String MEMORY_END_INDEX_TAG = "(block size";
    private static String sourceClass;
    private static AndroidInventory androidInvTrans;
    private static Logger logger;
    private static final String POLICY_INFO = "PolicyInfo";
    private static final String SECURITY_INFO = "SecurityInfo";
    private static final String LOCATION_INFO = "Location";
    public static final String OSVERSION = "OSVersion";
    public static final String BUILDVERSION = "BuildVersion";
    public static final String SERIALNUMBER = "SerialNumber";
    public static final String BATTERYLEVEL = "BatteryLevel";
    public static final String CELLULARTECHNOLOGY = "CellularTechnology";
    public static final String IMEI = "IMEI";
    public static final String MEID = "MEID";
    public static final String MODEMFIRMWAREVERSION = "ModemFirmwareVersion";
    public static final String DEVICECAPACITY = "DeviceCapacity";
    public static final String AVAILABLEDEVICECAPACITY = "AvailableDeviceCapacity";
    public static final String MODELNAME = "ModelName";
    public static final String PRODUCTNAME = "ProductName";
    public static final String MODEL = "Model";
    public static final String MANUFACTURER = "Manufacture";
    public static final String MODELCODE = "ModelCode";
    private static final String FREE_INTERNAL_STORAGE = "freeInternalStorage";
    private static final String FREE_EXTERNAL_STORAGE = "freeExternalStorage";
    private static final String TOT_INTERNAL_STORAGE = "TotInternalStorage";
    private static final String TOT_EXTERNAL_STORAGE = "TotExternalStorage";
    public static final String IS_DEVICEOWNER = "IsDeviceOwner";
    public static final String DEVICE_TYPE = "DeviceType";
    public static final String GOOGLE_PLAY_SERVICE_ID = "GSFAndroidID";
    public static final String EAS_DEVICE_IDENTIFIER = "EASDeviceIdentifier";
    public static final String IS_PROFILEOWNER = "IsProfileOwner";
    public static final String ICCID = "ICCID";
    public static final String BLUETOOTHMAC = "BluetoothMAC";
    public static final String WIFIMAC = "WiFiMAC";
    public static final String CURRENTCARRIERNETWORK = "CurrentCarrierNetwork";
    public static final String SIMCARRIERNETWORK = "SIMCarrierNetwork";
    public static final String SUBSCRIBERCARRIERNETWORK = "SubscriberCarrier-Network";
    public static final String CARRIERSETTINGSVERSION = "CarrierSettingsVersion";
    public static final String PHONENUMBER = "PhoneNumber";
    public static final String VOICEROAMINGENABLED = "VoiceRoamingEnabled";
    public static final String DATAROAMINGENABLED = "DataRoamingEnabled";
    public static final String ISROAMING = "isRoaming";
    public static final String SUBSCRIBERMCC = "SubscriberMCC";
    public static final String SUBSCRIBERMNC = "SubscriberMNC";
    public static final String CURRENTMCC = "CurrentMCC";
    public static final String CURRENTMNC = "CurrentMNC";
    public static final String CHIPSET = "ChipSet";
    public static final String CORE_COUNT = "ProcessorCount";
    private static final String STORAGE_ENCRP = "StorageEncryption";
    private static final String PASSCODE_ENABLED = "PasscodeEnabled";
    private static final String PASSCODE_COMP_WITH_PROFILE = "PasscodeCompliantWithProfiles";
    private static final String DEVICE_ROOTED = "DeviceRooted";
    private static final String RESTRICTIONS = "Restrictions";
    private static final int ENABLED = 1;
    private JSONUtil jSONUtil;
    
    public static void processAndroidComplaince(final JSONObject json, final Long resourceID) throws Exception {
        final JSONObject joCompliance = json.getJSONObject("PolicyInfo");
        final JSONObject secJson = joCompliance.getJSONObject("SecurityInfo");
        final HashMap securityInfo = new HashMap();
        final int storageEncryp = secJson.getInt("StorageEncryption");
        if (storageEncryp == 1) {
            securityInfo.put("StorageEncryption", Boolean.toString(Boolean.TRUE));
        }
        else {
            securityInfo.put("StorageEncryption", Boolean.toString(Boolean.FALSE));
        }
        final JSONUtil jSONUtil = JSONUtil.getInstance();
        securityInfo.put("HardwareEncryptionCaps", secJson.optString("StorageEncryption", "-1"));
        securityInfo.put("PasscodePresent", secJson.optString("PasscodeEnabled", "false"));
        securityInfo.put("PasscodeCompliant", Boolean.toString(Boolean.TRUE));
        securityInfo.put("PasscodeCompliantWithProfiles", secJson.optString("PasscodeCompliantWithProfiles", "false"));
        try {
            securityInfo.put("DeviceRooted", secJson.get("DeviceRooted").toString());
        }
        catch (final JSONException ex) {
            securityInfo.put("DeviceRooted", Boolean.FALSE + "");
        }
        MDMInvDataPopulator.getInstance().addOrUpdateIOSSecurityInfo(resourceID, securityInfo);
        final JSONObject restriction = joCompliance.getJSONObject("Restrictions");
        final HashMap resHash = JSONUtil.getInstance().ConvertJSONObjectToHash(restriction);
        MDMInvDataPopulator.getInstance().addorUpdateAndroidRestriction(resourceID, resHash);
    }
    
    private AndroidInventory() {
        this.jSONUtil = null;
        final String sourceMethod = "AndroidEnrollment";
        SyMLogger.info(AndroidInventory.logger = Logger.getLogger("MDMLogger"), AndroidInventory.sourceClass, sourceMethod, "Creating instance...");
    }
    
    public static synchronized AndroidInventory getInstance() {
        if (AndroidInventory.androidInvTrans == null) {
            AndroidInventory.androidInvTrans = new AndroidInventory();
        }
        return AndroidInventory.androidInvTrans;
    }
    
    public void updateAndroidInventory(final String inventoryData, final Long resourceID) {
        try {
            AndroidInventory.logger.log(Level.INFO, "updateAndroidInventory() :  resourceID {0}", resourceID);
            populateAndroidInventory(inventoryData, resourceID);
        }
        catch (final Exception ex) {
            AndroidInventory.logger.log(Level.WARNING, "Exception ocurred while populating Inventory data for resource{0}\n The exception is given by {1}", new Object[] { resourceID, ex });
        }
    }
    
    private static void populateAndroidInventory(final String jsontext, final Long resourceID) throws Exception {
        try {
            AndroidInventory.logger.log(Level.INFO, "Inside populateAndroidInventory Method");
            final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(jsontext));
            final String responseData = hmap.get("ResponseData");
            if (responseData.startsWith("[")) {
                AndroidInventory.logger.log(Level.INFO, "The Given string is of JSON Array");
            }
            else {
                AndroidInventory.logger.log(Level.INFO, "The Given String is of JSON OBject");
            }
            final JSONArray jaReq = new JSONArray(responseData);
            final int size = jaReq.length();
            AndroidInventory.logger.log(Level.INFO, "Array Size : {0}", size);
            final ArrayList<JSONObject> arrays = new ArrayList<JSONObject>();
            for (int i = 0; i < size; ++i) {
                final JSONObject jsonObject = jaReq.getJSONObject(i);
                arrays.add(jsonObject);
            }
            final JSONObject[] jsons = new JSONObject[arrays.size()];
            arrays.toArray(jsons);
            JSONObject joSoftware = null;
            JSONObject joHardware = null;
            JSONObject joNetwork = null;
            JSONObject joCompliance = null;
            JSONObject joLocation = null;
            for (int j = 0; j < size; ++j) {
                final String str = jsons[j].toString();
                if (str.startsWith("{\"SOFTWARE_DETAILS")) {
                    joSoftware = jsons[j];
                }
                else if (str.startsWith("{\"HARDWARE_DETAILS")) {
                    joHardware = jsons[j];
                }
                else if (str.startsWith("{\"NETWORK_DETAILS ")) {
                    joNetwork = jsons[j];
                }
                else if (str.startsWith("{\"PolicyInfo")) {
                    joCompliance = jsons[j];
                }
                else if (str.startsWith("{\"Location")) {
                    joLocation = jsons[j];
                }
            }
            AndroidInventory.logger.log(Level.FINE, "Json Software Objects '{'0'}' {0}", joSoftware.toString());
            AndroidInventory.logger.log(Level.FINE, "Json Hardware Objects '{'0'}' {0}", joHardware.toString());
            AndroidInventory.logger.log(Level.FINE, "Json Network Objects '{'0'}' {0}", joNetwork.toString());
            processAndroidAppList(joSoftware, resourceID);
            processAndroidDeviceInfo(joHardware, joNetwork, resourceID);
            processAndroidComplaince(joCompliance, resourceID);
            processAndroidLocation(joLocation, resourceID);
        }
        catch (final Exception je) {
            AndroidInventory.logger.log(Level.WARNING, "Exception ocurred while parsing ..{0}", je);
        }
    }
    
    private static void processAndroidLocation(final JSONObject json, final Long resourceID) throws Exception {
        try {
            final String udid = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID);
            if (json != null) {
                final JSONObject joLocation = json.getJSONObject("Location");
                MDMGeoLocationHandler.getInstance().addOrUpdateDeviceLocationDetails(joLocation, udid);
            }
            else {
                AndroidInventory.logger.log(Level.INFO, "Location Details are not available");
            }
        }
        catch (final JSONException ex) {
            AndroidInventory.logger.log(Level.WARNING, "Exception ocurred while constructing Location", (Throwable)ex);
        }
    }
    
    public void processAndroidLocationCommand(final String strData, final String udid) throws Exception {
        try {
            final JSONObject json = new JSONObject(strData);
            if (json != null) {
                final JSONObject joLocation = json.getJSONObject("ResponseData");
                joLocation.put("LocationUpdationTime", System.currentTimeMillis());
                MDMGeoLocationHandler.getInstance().addOrUpdateDeviceLocationDetails(joLocation, udid);
            }
            else {
                AndroidInventory.logger.log(Level.INFO, "Location Details are not available");
            }
        }
        catch (final JSONException ex) {
            AndroidInventory.logger.log(Level.WARNING, "Exception ocurred while constructing Location", (Throwable)ex);
        }
    }
    
    private static void processAndroidDeviceInfo(final JSONObject joHardware, final JSONObject joNetwork, final Long resourceID) throws Exception {
        try {
            final HashMap<String, HashMap<String, String>> hmHardwareInfo = getHardwareInfoHash(joHardware);
            final HashMap<String, HashMap<String, String>> hmNetworkDetails = getNetworkInfoHash(joNetwork);
            final HashMap<String, String> hmAndroidDeviceInfo = new HashMap<String, String>();
            final HashMap<String, String> hmAndroidNetworkInfo = new HashMap<String, String>();
            final HashMap<String, String> hmAndroidSimInfo = new HashMap<String, String>();
            final HashMap<String, String> hmDeviceInfo = hmHardwareInfo.get("DEVICEINFO");
            final HashMap<String, String> hmDiskInfo = hmHardwareInfo.get("DISKINFO");
            final HashMap<String, String> hmBatteryInfo = hmHardwareInfo.get("BATTERYINFO");
            final HashMap<String, String> hmTelephoneInfo = hmNetworkDetails.get("TELEPHONEINFO");
            final HashMap<String, String> hmWifiInfo = hmNetworkDetails.get("WIFIINFO");
            final JSONUtil jsonUtil = JSONUtil.getInstance();
            hmAndroidDeviceInfo.put("OS_VERSION", jsonUtil.checkAndUpdateTheValue(hmDeviceInfo, "OSVersion", null));
            hmAndroidDeviceInfo.put("BUILD_VERSION", jsonUtil.checkAndUpdateTheValue(hmDeviceInfo, "BuildVersion", "--"));
            hmAndroidDeviceInfo.put("SERIAL_NUMBER", jsonUtil.checkAndUpdateTheValue(hmDeviceInfo, "SerialNumber", "--"));
            hmAndroidDeviceInfo.put("CELLULAR_TECHNOLOGY", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "CellularTechnology", "0"));
            hmAndroidDeviceInfo.put("IMEI", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "IMEI", null));
            hmAndroidDeviceInfo.put("BATTERY_LEVEL", jsonUtil.checkAndUpdateTheValue(hmBatteryInfo, "Battery Level", "-1"));
            hmAndroidDeviceInfo.put("MODEL_NAME", jsonUtil.checkAndUpdateTheValue(hmDeviceInfo, "ModelName", null));
            hmAndroidDeviceInfo.put("PRODUCT_NAME", jsonUtil.checkAndUpdateTheValue(hmDeviceInfo, "ProductName", "--"));
            hmAndroidDeviceInfo.put("MANUFACTURER", jsonUtil.checkAndUpdateTheValue(hmDeviceInfo, "Manufacture", "--"));
            hmAndroidDeviceInfo.put("MODEL_CODE", jsonUtil.checkAndUpdateTheValue(hmDeviceInfo, "ModelCode", "--"));
            hmAndroidDeviceInfo.put("MODEL", jsonUtil.checkAndUpdateTheValue(hmDeviceInfo, "Model", null));
            hmAndroidDeviceInfo.put("MODEL_TYPE", jsonUtil.checkAndUpdateTheValue(hmDeviceInfo, "ModelName", "0"));
            hmAndroidDeviceInfo.put("DEVICE_CAPACITY", jsonUtil.checkAndUpdateTheValue(hmDiskInfo, "DeviceCapacity", "0.0"));
            hmAndroidDeviceInfo.put("AVAILABLE_DEVICE_CAPACITY", jsonUtil.checkAndUpdateTheValue(hmDiskInfo, "AvailableDeviceCapacity", "0.0"));
            hmAndroidDeviceInfo.put("IS_SUPERVISED", jsonUtil.checkAndUpdateTheValue(hmDiskInfo, "IsDeviceOwner", "0"));
            hmAndroidDeviceInfo.put("IS_PROFILEOWNER", jsonUtil.checkAndUpdateTheValue(hmDiskInfo, "IsProfileOwner", "0"));
            hmAndroidNetworkInfo.put("VOICE_ROAMING_ENABLED", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "VoiceRoamingEnabled", "true"));
            hmAndroidNetworkInfo.put("DATA_ROAMING_ENABLED", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "DataRoamingEnabled", "true"));
            final HashMap privacyJson = new PrivacySettingsHandler().getPrivacySettingsForMdDevices(resourceID);
            final int fetchMac = Integer.parseInt(privacyJson.get("fetch_mac_address").toString());
            if (fetchMac != 2) {
                hmAndroidNetworkInfo.put("WIFI_MAC", jsonUtil.checkAndUpdateTheValue(hmWifiInfo, "WiFiMAC", null));
                hmAndroidNetworkInfo.put("BLUETOOTH_MAC", jsonUtil.checkAndUpdateTheValue(hmWifiInfo, "BluetoothMAC", null));
            }
            hmAndroidSimInfo.put("ICCID", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "ICCID", null));
            hmAndroidSimInfo.put("IMEI", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "IMEI", null));
            hmAndroidSimInfo.put("CURRENT_CARRIER_NETWORK", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "CurrentCarrierNetwork", null));
            hmAndroidSimInfo.put("SUBSCRIBER_CARRIER_NETWORK", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "SIMCarrierNetwork", null));
            hmAndroidSimInfo.put("PHONE_NUMBER", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "PhoneNumber", null));
            hmAndroidSimInfo.put("IS_ROAMING", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "isRoaming", "true"));
            hmAndroidSimInfo.put("SUBSCRIBER_MCC", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "SubscriberMCC", null));
            hmAndroidSimInfo.put("SUBSCRIBER_MNC", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "SubscriberMNC", null));
            hmAndroidSimInfo.put("CURRENT_MCC", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "CurrentMCC", null));
            hmAndroidSimInfo.put("CURRENT_MNC", jsonUtil.checkAndUpdateTheValue(hmTelephoneInfo, "CurrentMNC", null));
            final ArrayList simArrayList = new ArrayList();
            simArrayList.add(hmAndroidSimInfo);
            MDMInvDataPopulator.getInstance().addOrUpdateDeviceInfo(resourceID, hmAndroidDeviceInfo);
            MDMInvDataPopulator.getInstance().addOrUpdateNetworkInfo(resourceID, hmAndroidNetworkInfo);
            MDMInvDataPopulator.getInstance().addOrUpdateSimInfo(resourceID, simArrayList);
        }
        catch (final JSONException ex) {
            AndroidInventory.logger.log(Level.WARNING, "Exception ocurred while constructing hash", (Throwable)ex);
        }
    }
    
    private static void processAndroidAppList(final JSONObject joSoftware, final long resourceID) throws JSONException {
        final JSONArray jaSoftwareArray = joSoftware.getJSONArray("SOFTWARE_DETAILS");
        final AppDataHandler appHandler = new AppDataHandler();
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(resourceID));
        appHandler.processAndroidSoftwares(resourceID, customerId, jaSoftwareArray);
    }
    
    private static HashMap<String, HashMap<String, String>> getHardwareInfoHash(final JSONObject joHardware) throws JSONException {
        final JSONArray jaHardwareArray = joHardware.getJSONArray("HARDWARE_DETAILS");
        final int size = jaHardwareArray.length();
        AndroidInventory.logger.log(Level.INFO, "Array Size : {0}", size);
        final ArrayList<JSONObject> arrays = new ArrayList<JSONObject>();
        for (int i = 0; i < size; ++i) {
            final JSONObject jsonObject = jaHardwareArray.getJSONObject(i);
            arrays.add(jsonObject);
        }
        final JSONObject[] joHardWares = new JSONObject[arrays.size()];
        arrays.toArray(joHardWares);
        final HashMap<String, HashMap<String, String>> hmHardwareInfo = new HashMap<String, HashMap<String, String>>();
        JSONObject joCpu = null;
        JSONObject joMemory = null;
        JSONObject joBattery = null;
        JSONObject joDiskInfo = null;
        JSONObject joDeviceInfo = null;
        for (int j = 0; j < size; ++j) {
            final String str = joHardWares[j].toString();
            if (str.startsWith("{\"CPUINFO")) {
                joCpu = joHardWares[j];
            }
            else if (str.startsWith("{\"MEMORYINFO")) {
                joMemory = joHardWares[j];
            }
            else if (str.startsWith("{\"BATTERYINFO")) {
                joBattery = joHardWares[j];
            }
            else if (str.startsWith("{\"DISKINFO")) {
                joDiskInfo = joHardWares[j];
            }
            else if (str.startsWith("{\"DEVICEINFO")) {
                joDeviceInfo = joHardWares[j];
            }
        }
        if (joCpu != null) {
            AndroidInventory.logger.log(Level.INFO, joCpu.toString());
        }
        if (joMemory != null) {
            AndroidInventory.logger.log(Level.INFO, joMemory.toString());
            final HashMap<String, String> hmMemmoryInfo = getMemoryInfo(joMemory.toString());
            hmHardwareInfo.put("MEMORYINFO", hmMemmoryInfo);
        }
        if (joBattery != null) {
            AndroidInventory.logger.log(Level.INFO, joBattery.toString());
            final HashMap hmBattery = getBatteryDetails(joBattery);
            final String sBatteryLevel = hmBattery.get("Battery Level");
            final String sTech = hmBattery.get("Battery Technology");
            hmHardwareInfo.put("BATTERYINFO", hmBattery);
        }
        if (joDiskInfo != null) {
            AndroidInventory.logger.log(Level.INFO, joDiskInfo.toString());
            final HashMap hmDiskInfo = getDiskInfo(joDiskInfo.getJSONObject("DISKINFO"));
            final String sDeviceCapacity = hmDiskInfo.get("DeviceCapacity");
            final String sAvailableDeviceCapacity = hmDiskInfo.get("AvailableDeviceCapacity");
            final float fDeviceCapacity = Float.valueOf(sDeviceCapacity) / 1000.0f;
            final float fAvailableDeviceCapacity = Float.valueOf(sAvailableDeviceCapacity) / 1000.0f;
            hmDiskInfo.put("DeviceCapacity", Float.toString(fDeviceCapacity));
            hmDiskInfo.put("AvailableDeviceCapacity", Float.toString(fAvailableDeviceCapacity));
            hmHardwareInfo.put("DISKINFO", hmDiskInfo);
        }
        if (joDeviceInfo != null) {
            AndroidInventory.logger.log(Level.INFO, joDeviceInfo.toString());
            final HashMap<String, String> hmDeviceInfo = getDeviceInfo(joDeviceInfo);
            hmHardwareInfo.put("DEVICEINFO", hmDeviceInfo);
        }
        return hmHardwareInfo;
    }
    
    private static HashMap<String, HashMap<String, String>> getNetworkInfoHash(final JSONObject joNetwork) throws JSONException {
        final JSONArray jaNetworkArray = joNetwork.getJSONArray("NETWORK_DETAILS ");
        final int size = jaNetworkArray.length();
        AndroidInventory.logger.log(Level.INFO, "Array Size : {0}", size);
        final ArrayList<JSONObject> arrays = new ArrayList<JSONObject>();
        for (int i = 0; i < size; ++i) {
            final JSONObject jsonObject = jaNetworkArray.getJSONObject(i);
            arrays.add(jsonObject);
        }
        final JSONObject[] joNetworks = new JSONObject[arrays.size()];
        arrays.toArray(joNetworks);
        JSONObject joTelephone = null;
        JSONObject joWifi = null;
        for (int j = 0; j < size; ++j) {
            final String str = joNetworks[j].toString();
            if (str.startsWith("{\"TELEPHONEINFO")) {
                joTelephone = joNetworks[j];
            }
            else if (str.startsWith("{\"WIFIINFO")) {
                joWifi = joNetworks[j];
            }
        }
        AndroidInventory.logger.log(Level.INFO, joTelephone.toString());
        AndroidInventory.logger.log(Level.INFO, joWifi.toString());
        final HashMap hmTelephone = getTelephoneManagerData(joTelephone);
        AndroidInventory.logger.log(Level.INFO, "TelePhone Manager : {0}", hmTelephone.toString());
        final HashMap hmWifi = getWifiManager(joWifi);
        AndroidInventory.logger.log(Level.INFO, "Wifi Manager : {0}", hmWifi.toString());
        final HashMap<String, HashMap<String, String>> hmNetworkInfo = new HashMap<String, HashMap<String, String>>();
        hmNetworkInfo.put("TELEPHONEINFO", hmTelephone);
        hmNetworkInfo.put("WIFIINFO", hmWifi);
        return hmNetworkInfo;
    }
    
    private static HashMap<String, String> getDeviceInfo(final JSONObject joDeviceInfo) throws JSONException {
        return JSONUtil.getInstance().ConvertJSONObjectToHash(joDeviceInfo, "DEVICEINFO");
    }
    
    private static HashMap getBatteryDetails(final JSONObject joBattery) throws JSONException {
        return JSONUtil.getInstance().ConvertJSONObjectToHash(joBattery, "BATTERYINFO");
    }
    
    private static HashMap<String, String> getTelephoneManagerData(final JSONObject joTelephone) throws JSONException {
        return JSONUtil.getInstance().ConvertJSONObjectToHash(joTelephone, "TELEPHONEINFO");
    }
    
    private static HashMap getWifiManager(final JSONObject joWifi) throws JSONException {
        return JSONUtil.getInstance().ConvertJSONObjectToHash(joWifi, "WIFIINFO");
    }
    
    private static HashMap<String, String> getMemoryInfo(final String memoryInfo) {
        final int startIndex = memoryInfo.indexOf("MemTotal:");
        final int endIndex = memoryInfo.indexOf("kBnMemFree");
        final String substr = memoryInfo.substring(startIndex + "MemTotal:".length(), endIndex);
        final int ramMemory = Integer.parseInt(substr.trim());
        final HashMap<String, String> hmBattery = new HashMap<String, String>();
        hmBattery.put("Memmory", substr.trim());
        AndroidInventory.logger.log(Level.INFO, "Total Memory {0}", ramMemory);
        return hmBattery;
    }
    
    private static HashMap getDiskInfo(final JSONObject diskInfo) throws JSONException {
        final long TotalMemory = diskInfo.getLong("TotInternalStorage") + diskInfo.getLong("TotExternalStorage");
        final long TotalMemoryAvailable = diskInfo.getLong("freeInternalStorage") + diskInfo.getLong("freeExternalStorage");
        AndroidInventory.logger.log(Level.INFO, "TotalMemory : {0}", TotalMemory);
        AndroidInventory.logger.log(Level.INFO, "TotalMemoryAvailable : {0}", TotalMemoryAvailable);
        final HashMap deviceMemInfo = new HashMap();
        deviceMemInfo.put("DeviceCapacity", String.valueOf(TotalMemory));
        deviceMemInfo.put("AvailableDeviceCapacity", String.valueOf(TotalMemoryAvailable));
        return deviceMemInfo;
    }
    
    public void initSystemAppCommand(final Long resourceId) {
        try {
            DeviceCommandRepository.getInstance().addSystemAppCommand(resourceId);
            if (KnoxUtil.getInstance().doesContainerActive(resourceId)) {
                DeviceCommandRepository.getInstance().addSystemAppContainerCommand(resourceId);
            }
            final List resourceList = new ArrayList();
            resourceList.add(resourceId);
            NotificationHandler.getInstance().SendNotification(resourceList, 2);
        }
        catch (final Exception ex) {
            AndroidInventory.logger.log(Level.WARNING, "Exception occurred while initSystemAppCommand", ex);
        }
    }
    
    public void initSystemAppCommandForPPM(final Long resourceId) {
        try {
            DeviceCommandRepository.getInstance().addSystemAppCommand(resourceId);
            if (KnoxUtil.getInstance().doesContainerActive(resourceId)) {
                DeviceCommandRepository.getInstance().addSystemAppContainerCommand(resourceId);
                AndroidInventory.logger.log(Level.INFO, "Container System App Command Added for : {0}", resourceId);
            }
            AndroidInventory.logger.log(Level.INFO, "System App Command Added for : {0}", resourceId);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static {
        AndroidInventory.sourceClass = "AndroidInventory";
        AndroidInventory.androidInvTrans = null;
        AndroidInventory.logger = null;
    }
}
