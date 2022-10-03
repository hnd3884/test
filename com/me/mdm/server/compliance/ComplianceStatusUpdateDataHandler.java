package com.me.mdm.server.compliance;

import java.util.List;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.HashMap;
import com.me.mdm.server.compliance.dbutil.ComplianceDBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import java.util.logging.Level;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ComplianceStatusUpdateDataHandler
{
    private static ComplianceStatusUpdateDataHandler complianceStatusUpdateDataHandler;
    private Logger logger;
    
    private ComplianceStatusUpdateDataHandler() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
    
    public static ComplianceStatusUpdateDataHandler getInstance() {
        if (ComplianceStatusUpdateDataHandler.complianceStatusUpdateDataHandler == null) {
            ComplianceStatusUpdateDataHandler.complianceStatusUpdateDataHandler = new ComplianceStatusUpdateDataHandler();
        }
        return ComplianceStatusUpdateDataHandler.complianceStatusUpdateDataHandler;
    }
    
    public JSONObject getAllDeviceComplianceSummary(final JSONObject requestJSON) throws Exception {
        try {
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final JSONArray deviceJSONArray = requestJSON.getJSONArray("device_ids");
            int nonCompliantCount = 0;
            int compliantCount = 0;
            int yetToEvaluateCount = 0;
            int notificationSent = 0;
            int notApplicableCount = 0;
            final JSONArray deviceArray = new JSONArray();
            for (int i = 0; i < deviceJSONArray.length(); ++i) {
                final Long deviceId = deviceJSONArray.getLong(i);
                final JSONObject deviceJSON = new JSONObject();
                if (requestJSON.has("filters")) {
                    deviceJSON.put("filters", (Object)String.valueOf(requestJSON.get("filters")));
                }
                deviceJSON.put("collection_id", (Object)collectionId);
                deviceJSON.put("resource_id", (Object)deviceId);
                deviceJSON.put("compliance_profile", (Object)requestJSON.getJSONObject("compliance_profile"));
                final JSONObject deviceSummaryJSON = this.getDeviceComplianceSummary(deviceJSON, 1901);
                final int state = deviceSummaryJSON.optInt("compliance_state", -1);
                deviceSummaryJSON.remove("compliance_profile");
                switch (state) {
                    case 901: {
                        ++yetToEvaluateCount;
                        deviceArray.put((Object)deviceSummaryJSON);
                        break;
                    }
                    case 902: {
                        ++compliantCount;
                        deviceArray.put((Object)deviceSummaryJSON);
                        break;
                    }
                    case 903: {
                        ++nonCompliantCount;
                        deviceArray.put((Object)deviceSummaryJSON);
                        break;
                    }
                    case 904: {
                        ++notApplicableCount;
                        deviceArray.put((Object)deviceSummaryJSON);
                        break;
                    }
                    case 905: {
                        ++notificationSent;
                        deviceArray.put((Object)deviceSummaryJSON);
                        break;
                    }
                    default: {
                        this.logger.log(Level.INFO, "invalid state  {0}", state);
                        break;
                    }
                }
            }
            requestJSON.put("total_device_count", deviceJSONArray.length());
            requestJSON.put("compliant_devices_count", compliantCount);
            requestJSON.put("non_compliant_devices_count", nonCompliantCount);
            requestJSON.put("yet_to_evaluate_devices_count", yetToEvaluateCount);
            requestJSON.put("notification_sent_count", notificationSent);
            requestJSON.put("not_applicable_count", notApplicableCount);
            requestJSON.put("devices", (Object)deviceArray);
            requestJSON.put("collection_id", (Object)collectionId);
            return requestJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- getAllDeviceComplianceSummary() >   Error ", e);
            throw e;
        }
    }
    
    private JSONObject getDeviceComplianceSummary(final JSONObject deviceJSON, final int params) throws Exception {
        try {
            JSONObject summaryJSON = new JSONObject();
            final Long resourceId = JSONUtil.optLongForUVH(deviceJSON, "resource_id", Long.valueOf(-1L));
            final HashMap userDeviceDetails = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(resourceId);
            final String userName = userDeviceDetails.get("FIRST_NAME");
            final Long userId = userDeviceDetails.get("MANAGED_USER_ID");
            final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
            final int platformType = deviceDetails.platform;
            final String platformName = MDMUtil.getInstance().getPlatformName(platformType);
            final String deviceName = deviceDetails.name;
            final String osVersion = deviceDetails.osVersion;
            deviceJSON.put("device_name", (Object)deviceName);
            deviceJSON.put("platform_type_id", platformType);
            deviceJSON.put("platform_type", (Object)platformName);
            summaryJSON = ComplianceDBUtil.getInstance().getComplianceToDeviceStatus(deviceJSON);
            summaryJSON.put("platform_type", (Object)platformName);
            summaryJSON.put("platform_type_id", platformType);
            summaryJSON.put("device_name", (Object)deviceName);
            summaryJSON.put("user_name", (Object)userName);
            summaryJSON.put("user_id", (Object)userId);
            summaryJSON.put("device_id", (Object)resourceId);
            summaryJSON.put("os_version", (Object)osVersion);
            return summaryJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- getDeviceComplianceSummary() >   Error ", e);
            throw e;
        }
    }
    
    public void removeComplianceToResourceSummary(final JSONObject requestJSON) throws JSONException, DataAccessException {
        try {
            final Long collectionId = JSONUtil.optLongForUVH(requestJSON, "collection_id", Long.valueOf(-1L));
            final JSONArray resourceList = requestJSON.getJSONArray("resource_list");
            final List resourceRemovalList = new ArrayList();
            for (int i = 0; i < resourceList.length(); ++i) {
                final Long resourceId = JSONUtil.optLongForUVH(resourceList, i, null);
                resourceRemovalList.add(resourceId);
            }
            final Criteria collectionCriteria = new Criteria(new Column("ComplianceToResource", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria resourceCriteria = new Criteria(new Column("ComplianceToResource", "RESOURCE_ID"), (Object)resourceRemovalList.toArray(), 8);
            MDMUtil.getPersistence().delete(collectionCriteria.and(resourceCriteria));
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- removeComplianceToResourceSummary() >   Error ", e);
            throw e;
        }
    }
    
    static {
        ComplianceStatusUpdateDataHandler.complianceStatusUpdateDataHandler = null;
    }
}
