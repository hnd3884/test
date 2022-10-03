package com.me.mdm.server.stageddevice;

import java.util.Iterator;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.Set;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.HashSet;
import org.json.JSONObject;
import com.me.mdm.server.stageddevice.mac.MacModernMgmtStagedDeviceFacade;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class ModernMgmtStagedDeviceFacade
{
    protected static Logger logger;
    private Map<String, String> apiKeyToColumnName;
    Integer platformType;
    
    protected ModernMgmtStagedDeviceFacade(final Integer platformType) {
        this.apiKeyToColumnName = new HashMap<String, String>() {
            {
                this.put("udid", "UDID");
                this.put("imei", "IMEI");
                this.put("serial_number", "SERIAL_NUMBER");
            }
        };
        this.platformType = null;
        this.platformType = platformType;
    }
    
    public static ModernMgmtStagedDeviceFacade getInstance(final Integer platformType) {
        ModernMgmtStagedDeviceFacade modernMgmtStagedDeviceFacade = null;
        if (platformType.equals(3)) {
            modernMgmtStagedDeviceFacade = new WindowsModernMgmtStagedDeviceFacade(platformType);
        }
        else if (platformType.equals(1)) {
            modernMgmtStagedDeviceFacade = new MacModernMgmtStagedDeviceFacade(platformType);
        }
        return modernMgmtStagedDeviceFacade;
    }
    
    public JSONObject getModernMgmtDeviceDetails(final JSONObject filterJSON) {
        return new JSONObject();
    }
    
    public JSONObject addOrUpdateDeviceStagedForModernMgmt(final JSONObject apiRequestJSON) throws Exception {
        final JSONObject apiRequestBodyJSON = apiRequestJSON.getJSONObject("msg_body");
        final JSONArray deviceJSONArray = apiRequestBodyJSON.getJSONArray("devices");
        final Boolean isDeviceAllowed = Boolean.FALSE;
        final Set<String> serialNumberSet = new HashSet<String>();
        final Set<String> ududSet = new HashSet<String>();
        final Set<String> genericSet = new HashSet<String>();
        for (int index = 0; index < deviceJSONArray.length(); ++index) {
            final JSONObject deviceJSON = deviceJSONArray.getJSONObject(index);
            final String serialNumber = deviceJSON.getJSONObject("device_unique_props").optString("serial_number", (String)null);
            final String udid = deviceJSON.getJSONObject("device_unique_props").optString("udid", (String)null);
            final String genericID = deviceJSON.getJSONObject("device_unique_props").optString("generic_id", (String)null);
            if (!MDMStringUtils.isEmpty(serialNumber)) {
                serialNumberSet.add(serialNumber);
            }
            if (!MDMStringUtils.isEmpty(udid)) {
                ududSet.add(udid);
            }
            if (!MDMStringUtils.isEmpty(genericID)) {
                genericSet.add(genericID);
            }
        }
        final JSONObject apiResponse = this.processDeviceAddRequest(deviceJSONArray, serialNumberSet, ududSet, genericSet);
        final JSONObject responseJSON = new JSONObject();
        if (apiResponse.optBoolean("isDeviceAllowed", (boolean)Boolean.FALSE)) {
            responseJSON.put("Status", (Object)"DevicesAddedSuccessfully");
            responseJSON.put("group_ro_details", (Object)responseJSON.optJSONObject("group_ro_details"));
            responseJSON.put("customer_id", deviceJSONArray.getJSONObject(0).getLong("customer_id"));
        }
        return responseJSON;
    }
    
    public JSONObject deleteDeviceStagedForModernMgmt(final JSONObject apiRequestJSON) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final Boolean isDeleteDevicesSuccess = this.processDevicesDeleteRequest(apiRequestJSON.getJSONObject("msg_body"));
        if (isDeleteDevicesSuccess) {
            ModernMgmtStagedDeviceFacade.logger.log(Level.INFO, "Successfully delete the device {0} from waiting for user assignment via modern mgmt\n", apiRequestJSON.getJSONObject("msg_body").getJSONArray("devices"));
        }
        responseJSON.put("Status", (Object)isDeleteDevicesSuccess.toString());
        return responseJSON;
    }
    
    public boolean deleteModernDeviceForEnrollment(final JSONObject devicesDeleteBodyJSON) throws Exception {
        final Map<String, List<String>> columnToValuesMapping = new HashMap<String, List<String>>();
        final Long userID = devicesDeleteBodyJSON.getLong("tech_user_id");
        final JSONArray deviceJSONArray = devicesDeleteBodyJSON.getJSONArray("devices");
        for (int index = 0; index < deviceJSONArray.length(); ++index) {
            final JSONObject deviceJSON = deviceJSONArray.getJSONObject(index);
            final JSONObject deviceUniquePropsJSON = deviceJSON.getJSONObject("device_unique_props");
            final Iterator<String> keyIter = deviceUniquePropsJSON.keys();
            while (keyIter.hasNext()) {
                final String key = keyIter.next();
                List<String> values = columnToValuesMapping.get(key);
                if (values == null) {
                    values = new ArrayList<String>();
                }
                values.add(String.valueOf(deviceUniquePropsJSON.get(key)));
                columnToValuesMapping.put(key, values);
            }
        }
        final Set<String> apiKeys = columnToValuesMapping.keySet();
        final Iterator<String> apiKeysIterator = apiKeys.iterator();
        Criteria finalCriteria = null;
        while (apiKeysIterator.hasNext()) {
            final String apiKey = apiKeysIterator.next();
            if (this.apiKeyToColumnName.get(apiKey) != null) {
                final String columnName = this.apiKeyToColumnName.get(apiKey);
                final Criteria columnCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", columnName), (Object)columnToValuesMapping.get(apiKey).toArray(new String[0]), 8);
                if (finalCriteria == null) {
                    finalCriteria = columnCriteria;
                }
                else {
                    finalCriteria = finalCriteria.or(columnCriteria);
                }
            }
        }
        if (finalCriteria != null) {
            if (this.platformType == 3) {
                final DeviceForEnrollmentHandler deviceForEnrollmentHandler = new DeviceForEnrollmentHandler();
                deviceForEnrollmentHandler.deleteOnEnrollment(finalCriteria);
            }
            else if (this.platformType == 1) {
                final DeviceForEnrollmentHandler deviceForEnrollmentHandler = new DeviceForEnrollmentHandler();
                deviceForEnrollmentHandler.deleteOnEnrollment(finalCriteria, "MacModernMgmtDeviceForEnrollment");
            }
        }
        else {
            ModernMgmtStagedDeviceFacade.logger.log(Level.WARNING, "Got empty delete request from DC , Not processing it to delete , input JSON- {0}", devicesDeleteBodyJSON);
        }
        return Boolean.TRUE;
    }
    
    protected abstract JSONObject processDeviceAddRequest(final JSONArray p0, final Set<String> p1, final Set<String> p2, final Set<String> p3) throws Exception;
    
    protected abstract Boolean processDevicesDeleteRequest(final JSONObject p0) throws Exception;
    
    static {
        ModernMgmtStagedDeviceFacade.logger = Logger.getLogger("MDMModernMgmtLogger");
    }
}
