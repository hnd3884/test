package com.adventnet.sym.server.mdm.inv;

import org.apache.commons.collections.CollectionUtils;
import java.util.Collection;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.csv.CSVProcessor;

public class MDCustomDetailsCSVProcessor extends CSVProcessor
{
    private static Logger logger;
    public static final String IS_IMEI_IN_CSV = "IsIMEIInCSV";
    public static final String IS_SERIAL_NUMBER_IN_CSV = "isSerialNoInCSV";
    public static final String IS_DEVICE_NAME_IN_CSV = "isDeviceNameInCSV";
    public static final String IS_DEVICE_DESCRIPTION_IN_CSV = "isDeviceDescriptionInCSV";
    public static final String IS_ASSET_TAG_IN_CSV = "isAssetTypeInCSV";
    public static final String IS_ASSET_OWNER_IN_CSV = "isAssetOwnerInCSV";
    public static final String OPERATION_LABEL = "MDCustomDetails";
    public static final String IS_OFFICE_IN_CSV = "isOfficeInCSV";
    public static final String IS_BRANCH_IN_CSV = "isBranchInCSV";
    public static final String IS_LOCATION_IN_CSV = "isLocationInCSV";
    public static final String IS_AREA_MANAGER_IN_CSV = "isAreaManagerInCSV";
    public static final String IS_PURCHASE_DATE_IN_CSV = "isPurchaseDateInCSV";
    public static final String IS_PURCHASE_ORDER_NUMBER_IN_CSV = "isPurchaseOrderNumberInCSV";
    public static final String IS_PURCHASE_PRICE_IN_CSV = "isPurchasePriceInCSV";
    public static final String IS_PURCHASE_TYPE_IN_CSV = "isPurchaseTypeInCSV";
    public static final String IS_WARRANTY_EXPIRATION_DATE_IN_CSV = "isWarrantyExpirationDateInCSV";
    public static final String IS_WARRANTY_NUMBER_IN_CSV = "isWarrantyNumberInCSV";
    public static final String IS_WARRANTY_TYPE_IN_CSV = "isWarrantyTypeInCSV";
    public static final String IS_APN_USER_NAME_IN_CSV = "isAPNUserNameinCSV";
    public static final String IS_APN_PASSWORD_IN_CSV = "isAPNPasswordinCSV";
    
    protected JSONObject generateTableDetails() throws Exception {
        try {
            final JSONObject tableDetails = new JSONObject();
            tableDetails.put((Object)"IMEI", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "IsIMEIInCSV"));
            tableDetails.put((Object)"SERIAL_NUMBER", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isSerialNoInCSV"));
            tableDetails.put((Object)"DEVICE_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "String", "isDeviceNameInCSV"));
            tableDetails.put((Object)"PERSONAL_RECOVERY_KEY", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "PERSONAL_RECOVERY_KEY"));
            tableDetails.putAll((Map)this.getCustomColumnDetails());
            return tableDetails;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    public JSONObject getCustomColumnDetails() throws Exception {
        final JSONObject tableDetails = new JSONObject();
        tableDetails.put((Object)"ASSET_TAG", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isAssetTypeInCSV"));
        tableDetails.put((Object)"ASSET_OWNER", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isAssetOwnerInCSV"));
        tableDetails.put((Object)"DESCRIPTION", (Object)this.generateColumnDetailsJSON(Integer.valueOf(1000), "String", "isDeviceDescriptionInCSV"));
        tableDetails.put((Object)"OFFICE", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isOfficeInCSV"));
        tableDetails.put((Object)"BRANCH", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isBranchInCSV"));
        tableDetails.put((Object)"LOCATION", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isLocationInCSV"));
        tableDetails.put((Object)"AREA_MANAGER", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isAreaManagerInCSV"));
        tableDetails.put((Object)"PURCHASE_DATE", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "Date", "isPurchaseDateInCSV"));
        tableDetails.put((Object)"PURCHASE_ORDER_NUMBER", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isPurchaseOrderNumberInCSV"));
        tableDetails.put((Object)"PURCHASE_PRICE", (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "Float", "isPurchasePriceInCSV"));
        tableDetails.put((Object)"PURCHASE_TYPE", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isPurchaseTypeInCSV"));
        tableDetails.put((Object)"WARRANTY_EXPIRATION_DATE", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "Date", "isWarrantyExpirationDateInCSV"));
        tableDetails.put((Object)"WARRANTY_NUMBER", (Object)this.generateColumnDetailsJSON(Integer.valueOf(1000), "String", "isWarrantyNumberInCSV"));
        tableDetails.put((Object)"WARRANTY_TYPE", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isWarrantyTypeInCSV"));
        tableDetails.put((Object)"APN_USER_NAME", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isAPNUserNameinCSV"));
        tableDetails.put((Object)"APN_PASSWORD", (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isAPNPasswordinCSV"));
        return tableDetails;
    }
    
    private JSONObject getCustomColumnDetailsForValidation() throws Exception {
        final JSONObject tableDetails = new JSONObject();
        tableDetails.put((Object)"ASSET_TAG".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isAssetTypeInCSV"));
        tableDetails.put((Object)"ASSET_OWNER".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isAssetOwnerInCSV"));
        tableDetails.put((Object)"DESCRIPTION".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(1000), "String", "isDeviceDescriptionInCSV"));
        tableDetails.put((Object)"OFFICE".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isOfficeInCSV"));
        tableDetails.put((Object)"BRANCH".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isBranchInCSV"));
        tableDetails.put((Object)"LOCATION".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isLocationInCSV"));
        tableDetails.put((Object)"AREA_MANAGER".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isAreaManagerInCSV"));
        tableDetails.put((Object)"PURCHASE_DATE".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "Date", "isPurchaseDateInCSV"));
        tableDetails.put((Object)"PURCHASE_ORDER_NUMBER".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isPurchaseOrderNumberInCSV"));
        tableDetails.put((Object)"PURCHASE_PRICE".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(100), "Float", "isPurchasePriceInCSV"));
        tableDetails.put((Object)"PURCHASE_TYPE".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isPurchaseTypeInCSV"));
        tableDetails.put((Object)"WARRANTY_EXPIRATION_DATE".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "Date", "isWarrantyExpirationDateInCSV"));
        tableDetails.put((Object)"WARRANTY_NUMBER".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(1000), "String", "isWarrantyNumberInCSV"));
        tableDetails.put((Object)"WARRANTY_TYPE".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isWarrantyTypeInCSV"));
        tableDetails.put((Object)"APN_USER_NAME".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isAPNUserNameinCSV"));
        tableDetails.put((Object)"APN_PASSWORD".replace("_", "").toLowerCase(), (Object)this.generateColumnDetailsJSON(Integer.valueOf(250), "String", "isAPNPasswordinCSV"));
        return tableDetails;
    }
    
    protected void validateCSVHeader(List<String> columnsInCSV) throws Exception {
        try {
            columnsInCSV = MDMUtil.getInstance().modifyHeadersInBulkCSVForValidation(columnsInCSV);
            if (!columnsInCSV.contains("IMEI".toLowerCase()) && !columnsInCSV.contains("SERIAL_NUMBER".replace("_", "").toLowerCase())) {
                MDCustomDetailsCSVProcessor.logger.log(Level.INFO, "IMEI or serial number is not available in the first row of csv file");
                throw new SyMException(13003, "dc.mdm.device_mgmt.error_no_pk", (Throwable)null);
            }
            if (columnsInCSV.contains("PERSONAL_RECOVERY_KEY".replace("_", "").toLowerCase())) {
                MDCustomDetailsCSVProcessor.logger.log(Level.INFO, "Filevault rotate is imported.Need to import via Filevault CSV Operation");
                throw new SyMException(51019, "dc.mdm.device_mgmt.filevault_import_not_allowed", (Throwable)null);
            }
            final List customColumnsInTemplate = new ArrayList(this.getCustomColumnDetailsForValidation().keySet());
            if (!columnsInCSV.contains("DEVICE_NAME".replace("_", "").toLowerCase()) && !CollectionUtils.containsAny((Collection)columnsInCSV, (Collection)customColumnsInTemplate)) {
                MDCustomDetailsCSVProcessor.logger.log(Level.INFO, "A field to modify is not available");
                throw new SyMException(13004, "dc.mdm.device_mgmt.error_no_editable_columns", (Throwable)null);
            }
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    protected String getOperationLabel() {
        return "MDCustomDetails";
    }
    
    static {
        MDCustomDetailsCSVProcessor.logger = Logger.getLogger("MDMEnrollment");
    }
}
