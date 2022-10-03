package com.adventnet.sym.server.mdm.inv;

import java.util.Iterator;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.Map;
import java.util.logging.Logger;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.JSONUtil;

public class MDMInvDataHandler
{
    private static final String SECURITY_DETAILS = "SecurityDetails";
    private static final String DEVICE_DETAILS = "DeviceDetails";
    private static final String NETWORK_DETAILS = "NetworkDetails";
    private static final String NETWORK_USAGE_DETAILS = "NetworkUsageDetails";
    private static final String SOFTWARE_DETAILS = "SoftwareDetails";
    private static final String CERTIFICATE_DETAILS = "CertificateDetails";
    private static final String RESTRICTION_DETAILS = "Restriction";
    private static final String CUSTOM_FIELDS_DETAILS = "CustomFieldsDetails";
    private static final String SYSTEM_ACTICIY_DETAILS = "SystemActivityDetails";
    private static final String OS_UPDATE_DETAILS = "OsUpdateDetails";
    private static final String LOCATION_DETAILS = "Location";
    private static final String WORK_DATA_SECURITY_DETAILS = "WorkDataSecurityDetails";
    private static final String SECURITY_DETAILS_IMPL = "com.adventnet.sym.server.mdm.inv.android.SecurityDetailsMDMInventoryImpl";
    private static final String DEVICE_DETAILS_IMPL = "com.adventnet.sym.server.mdm.inv.android.DeviceDetailsMDMInventoryImpl";
    private static final String NETWORK_DETAILS_IMPL = "com.adventnet.sym.server.mdm.inv.android.NetworkDetailsMDMInventoryImpl";
    private static final String NETWORK_USAGE_DETAILS_IMPL = "com.adventnet.sym.server.mdm.inv.android.NetworkUsageDetailsMDMInventoryImpl";
    private static final String SOFTWARE_DETAILS_IMPL = "com.adventnet.sym.server.mdm.inv.android.SoftwareDetailsMDMInventoryImpl";
    private static final String CERTIFICATE_DETAILS_IMPL = "com.adventnet.sym.server.mdm.inv.android.CertificateDetailsMDMInventoryImpl";
    private static final String RESTRICTION_DETAILS_IMPL = "com.adventnet.sym.server.mdm.inv.MDMInvRestrictionDataHandler";
    private static final String CUSTOM_FIELDS_DETAILS_IMPL = "com.adventnet.sym.server.mdm.inv.chrome.CustomFieldsMDMInventoryImpl";
    private static final String SYSTEM_ACTICIY_DETAILS_IMPL = "com.adventnet.sym.server.mdm.inv.chrome.SystemActivityMDMInventoryImpl";
    private static final String OS_UPDATE_DETAILS_IMPL = "com.adventnet.sym.server.mdm.inv.android.OsUpdateDetailsMDMInventoryImpl";
    private static final String LOCATION_DETAILS_IMPL = "com.adventnet.sym.server.mdm.inv.android.LocationMDMInventoryImpl";
    private static final String WORK_DATA_SECURITY_DETAILS_IMPL = "com.adventnet.sym.server.mdm.inv.android.WorkDataSecurityMDMInventoryImpl";
    private JSONUtil jsonUtil;
    private final HashMap<String, String> instiantiateInventoryImpl;
    private static MDMInventory mdmInvDataPopulator;
    private Logger logger;
    
    public MDMInvDataHandler() {
        this.jsonUtil = JSONUtil.getInstance();
        this.instiantiateInventoryImpl = new HashMap<String, String>();
        this.logger = null;
        this.instiantiateInventoryImpl.put("SecurityDetails", "com.adventnet.sym.server.mdm.inv.android.SecurityDetailsMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("DeviceDetails", "com.adventnet.sym.server.mdm.inv.android.DeviceDetailsMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("NetworkDetails", "com.adventnet.sym.server.mdm.inv.android.NetworkDetailsMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("NetworkUsageDetails", "com.adventnet.sym.server.mdm.inv.android.NetworkUsageDetailsMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("SoftwareDetails", "com.adventnet.sym.server.mdm.inv.android.SoftwareDetailsMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("CertificateDetails", "com.adventnet.sym.server.mdm.inv.android.CertificateDetailsMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("Restriction", "com.adventnet.sym.server.mdm.inv.MDMInvRestrictionDataHandler");
        this.instiantiateInventoryImpl.put("CustomFieldsDetails", "com.adventnet.sym.server.mdm.inv.chrome.CustomFieldsMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("SystemActivityDetails", "com.adventnet.sym.server.mdm.inv.chrome.SystemActivityMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("OsUpdateDetails", "com.adventnet.sym.server.mdm.inv.android.OsUpdateDetailsMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("Location", "com.adventnet.sym.server.mdm.inv.android.LocationMDMInventoryImpl");
        this.instiantiateInventoryImpl.put("WorkDataSecurityDetails", "com.adventnet.sym.server.mdm.inv.android.WorkDataSecurityMDMInventoryImpl");
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public boolean mdmInventoryDataPopulator(final Long resourceId, final Map<String, String> parsedData, final Integer scope) {
        boolean dataPopulation = false;
        try {
            final String respondData = parsedData.get("ResponseData");
            this.jsonUtil = JSONUtil.getInstance();
            final JSONObject inventoryData = new JSONObject(respondData);
            final Iterator<String> iter = inventoryData.keys();
            while (iter.hasNext()) {
                final String key = iter.next();
                final String className = this.instiantiateInventoryImpl.get(key);
                final JSONObject invData = (JSONObject)inventoryData.get(key);
                final MDMInvdetails inventoryObject = new MDMInvdetails(resourceId, invData.toString(), scope);
                if (className != null) {
                    (MDMInvDataHandler.mdmInvDataPopulator = (MDMInventory)Class.forName(className).newInstance()).populateInventoryData(inventoryObject);
                }
            }
            dataPopulation = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred while handling inventory data.. {0}", exp);
            dataPopulation = false;
        }
        return dataPopulation;
    }
    
    static {
        MDMInvDataHandler.mdmInvDataPopulator = null;
    }
}
