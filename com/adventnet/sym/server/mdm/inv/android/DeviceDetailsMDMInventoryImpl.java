package com.adventnet.sym.server.mdm.inv.android;

import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import java.util.logging.Level;
import org.json.JSONArray;
import com.me.mdm.server.settings.battery.MdDeviceBatteryDetailsDBHandler;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class DeviceDetailsMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
    private static final String UDID = "UDID";
    private static final String AVAILABLE_EXTERNAL_CAPACITY = "AvailableExternalCapacity";
    private static final String MODEL = "Model";
    private static final String AVAILABLE_DEVICE_CAPACITY = "AvailableDeviceCapacity";
    private static final String DEVICE_PROCESSOR_TYPE = "DeviceProcessorType";
    private static final String DEVICE_CAPACITY = "DeviceCapacity";
    private static final String OSVERSION = "OSVersion";
    private static final String EXTERNAL_CAPACITY = "ExternalCapacity";
    private static final String MODEL_TYPE = "ModelType";
    private static final String MANUFACTURER = "Manufacture";
    public static final String MODELNAME = "ModelName";
    private static final String TOTAL_RAM_MEMORY = "TotalRAMMemory";
    private static final String BUILD_VERSION = "BuildVersion";
    private static final String OSNAME = "OSName";
    private static final String AVAILABLE_RAM_MEMORY = "AvailableRAMMemory";
    private static final String USED_DEVICE_SAPCE = "UsedDeviceSpace";
    private static final String SERIAL_NUMBER = "SerialNumber";
    private static final String MODEM_FIRMWARE = "Modem_FirmWare";
    private static final String FIRMWARE_VERSION = "FirmwareVersion";
    private static final String IMEI = "IMEI";
    private static final String MEID = "MEID";
    private static final String MODEL_NAME = "ModelName";
    private static final String MODEL_CODE = "ModelCode";
    private static final String CELLULAR_TECHNOLOGY = "CellularTechnology";
    private static final String PRODUCT_NAME = "ProductName";
    private static final String DEVICE_PROCESSOR_SPEED = "DeviceProcessorSpeed";
    private static final String USED_EXTERNAL_SPACE = "UsedExternalSpace";
    private static final String BATTERY_LEVEL = "Battery_Level";
    public static final String IS_DEVICEOWNER = "IsDeviceOwner";
    public static final String IS_REALDEVICEOWNER = "IsRealDeviceOwner";
    public static final String IS_PROFILEOWNER = "IsProfileOwner";
    public static final String GOOGLE_PLAY_SERVICE_ID = "GSFAndroidID";
    
    public DeviceDetailsMDMInventoryImpl() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        final HashMap<String, String> deviceInfo = new HashMap<String, String>();
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject inventoryData = new JSONObject(inventoryObject.strData);
            deviceInfo.put("OS_VERSION", inventoryData.optString("OSVersion", "--"));
            deviceInfo.put("BUILD_VERSION", inventoryData.optString("BuildVersion", "--"));
            deviceInfo.put("SERIAL_NUMBER", inventoryData.optString("SerialNumber", (String)null));
            deviceInfo.put("MODEL_ID", inventoryData.optString("UDID", "-1"));
            deviceInfo.put("BATTERY_LEVEL", inventoryData.optString("Battery_Level", "-1"));
            try {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("BATTERY_LEVEL", (Object)inventoryData.optString("Battery_Level", "-1"));
                final Long deviceTime = (Long)inventoryData.get("DEVICE_LOCAL_TIME");
                final String deviceLocalTime = MdDeviceBatteryDetailsDBHandler.convertMillisecondsToDate(deviceTime);
                jsonObject.put("DEVICE_LOCAL_TIME", (Object)deviceLocalTime);
                MdDeviceBatteryDetailsDBHandler.addOrUpdateBatteryDetails(inventoryObject.resourceId, new JSONArray().put((Object)jsonObject));
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception while adding battery tracking details", e);
            }
            deviceInfo.put("CELLULAR_TECHNOLOGY", inventoryData.optString("CellularTechnology", "0"));
            deviceInfo.put("IMEI", inventoryData.optString("IMEI", (String)null));
            deviceInfo.put("MEID", inventoryData.optString("MEID", (String)null));
            deviceInfo.put("MODEM_FIRMWARE_VERSION", inventoryData.optString("Modem_FirmWare", (String)null));
            deviceInfo.put("FIRMWARE_VERSION", inventoryData.optString("FirmwareVersion", (String)null));
            deviceInfo.put("DEVICE_CAPACITY", inventoryData.optString("DeviceCapacity", "0.0"));
            deviceInfo.put("AVAILABLE_DEVICE_CAPACITY", inventoryData.optString("AvailableDeviceCapacity", "0.0"));
            deviceInfo.put("USED_DEVICE_SPACE", inventoryData.optString("UsedDeviceSpace", "0.0"));
            deviceInfo.put("EAS_DEVICE_IDENTIFIER", inventoryData.optString("EASDeviceIdentifier", (String)null));
            deviceInfo.put("PROCESSOR_NAME", inventoryData.optString("ChipSet", (String)null));
            deviceInfo.put("PROCESSOR_CORE_COUNT", inventoryData.optString("ProcessorCount", "0"));
            deviceInfo.put("EXTERNAL_CAPACITY", inventoryData.optString("ExternalCapacity", "0.0"));
            deviceInfo.put("USED_EXTERNAL_SPACE", inventoryData.optString("UsedExternalSpace", "0.0"));
            deviceInfo.put("AVAILABLE_EXTERNAL_CAPACITY", inventoryData.optString("AvailableExternalCapacity", "0.0"));
            deviceInfo.put("OS_NAME", inventoryData.optString("OSName", (String)null));
            deviceInfo.put("PROCESSOR_SPEED", inventoryData.optString("DeviceProcessorSpeed", (String)null));
            deviceInfo.put("PROCESSOR_TYPE", inventoryData.optString("DeviceProcessorType", (String)null));
            deviceInfo.put("AVAILABLE_RAM_MEMORY", inventoryData.optString("AvailableRAMMemory", "0.0"));
            deviceInfo.put("TOTAL_RAM_MEMORY", inventoryData.optString("TotalRAMMemory", "0.0"));
            deviceInfo.put("MODEL_NAME", inventoryData.optString("ModelName", (String)null));
            deviceInfo.put("PRODUCT_NAME", inventoryData.optString("ProductName", "--"));
            deviceInfo.put("MODEL", inventoryData.optString("Model", (String)null));
            deviceInfo.put("MODEL_TYPE", inventoryData.optString("ModelType", "0"));
            deviceInfo.put("MODEL_CODE", inventoryData.optString("ModelCode", (String)null));
            deviceInfo.put("MANUFACTURER", inventoryData.optString("Manufacture", "--"));
            deviceInfo.put("IS_SUPERVISED", inventoryData.optString("IsRealDeviceOwner", "0"));
            deviceInfo.put("IS_PROFILEOWNER", inventoryData.optString("IsProfileOwner", "0"));
            deviceInfo.put("GOOGLE_PLAY_SERVICE_ID", inventoryData.optString("GSFAndroidID", "--"));
            final MDMInvDataPopulator invDataPopulator = MDMInvDataPopulator.getInstance();
            invDataPopulator.addOrUpdateDeviceInfo(inventoryObject.resourceId, deviceInfo);
            isDataPopulationSuccess = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred on populating device details from response data..{0}", exp);
            isDataPopulationSuccess = false;
        }
        return isDataPopulationSuccess;
    }
}
