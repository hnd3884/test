package com.adventnet.sym.server.mdm.apps;

import java.util.logging.Level;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.adventnet.persistence.DataObject;

public class AppLicenseHandler
{
    private DataObject finalDO;
    private DataObject existingDO;
    Logger logger;
    private static AppLicenseHandler appLicenseHandler;
    public static final int VPP_REDEMPTION_ASSIGNMENT = 1;
    public static final int VPP_APP_ASSIGNMENT = 2;
    public static final int STORE_APP_LICENSE = 3;
    public static final int LICENSE_MIGRATED = 0;
    
    public AppLicenseHandler() {
        this.finalDO = null;
        this.existingDO = null;
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    public void processStoreAppsLicense(final Long appGroupId, final Long customerId, final JSONObject licenseJSON) throws DataAccessException, JSONException {
        final AppLicense appLicense = this.getAppLicense(licenseJSON);
        appLicense.customerID = customerId;
        appLicense.licenseType = 3;
        appLicense.appGroupId = appGroupId;
        appLicense.isMigrated = true;
        appLicense.businessStoreId = licenseJSON.getLong("BUSINESSSTORE_ID");
        this.processAppLicenseDetails(appLicense);
    }
    
    private void processAppLicenseDetails(final AppLicense appLicense) throws DataAccessException {
        this.finalDO = MDMUtil.getPersistence().constructDataObject();
        this.getExistingDO(appLicense.appGroupId);
        final Row mdLicenceRow = this.addorUpdateMdLicense(appLicense);
        this.addOrUpdateMdLicenseToAppGroupRel(appLicense, mdLicenceRow);
        if (appLicense.licenseType == 3) {
            this.addorUpdateStoreAppsLicenseSummary(appLicense, mdLicenceRow);
        }
        MDMUtil.getPersistence().update(this.finalDO);
    }
    
    private Row addorUpdateMdLicense(final AppLicense appLicense) throws DataAccessException {
        DataObject DO = MDMUtil.getPersistence().constructDataObject();
        Row licenseRow = null;
        if (this.existingDO.isEmpty()) {
            licenseRow = new Row("MdLicense");
            licenseRow.set("LICENSED_TYPE", (Object)appLicense.licenseType);
            licenseRow.set("CUSTOMER_ID", (Object)appLicense.customerID);
            licenseRow.set("IS_MIGRATED", (Object)appLicense.isMigrated);
            DO.addRow(licenseRow);
        }
        else {
            licenseRow = this.existingDO.getFirstRow("MdLicense");
            if (licenseRow == null) {
                licenseRow = new Row("MdLicense");
                licenseRow.set("LICENSED_TYPE", (Object)appLicense.licenseType);
                licenseRow.set("CUSTOMER_ID", (Object)appLicense.customerID);
                licenseRow.set("IS_MIGRATED", (Object)appLicense.isMigrated);
                DO.addRow(licenseRow);
            }
            else if (appLicense.isMigrated) {
                final Criteria criteria = new Criteria(Column.getColumn("MdLicense", "LICENSE_ID"), licenseRow.get("LICENSE_ID"), 0);
                DO = MDMUtil.getPersistence().get("MdLicense", criteria);
                licenseRow.set("LICENSED_TYPE", (Object)appLicense.licenseType);
                licenseRow.set("CUSTOMER_ID", (Object)appLicense.customerID);
                licenseRow.set("IS_MIGRATED", (Object)appLicense.isMigrated);
                DO.updateRow(licenseRow);
            }
        }
        this.finalDO.merge(DO);
        return licenseRow;
    }
    
    private Row addOrUpdateMdLicenseToAppGroupRel(final AppLicense appLicense, final Row mdLicenseRow) throws DataAccessException {
        final DataObject DO = MDMUtil.getPersistence().constructDataObject();
        Row licenseRelRow = null;
        if (this.existingDO.isEmpty()) {
            licenseRelRow = new Row("MdLicenseToAppGroupRel");
            licenseRelRow.set("LICENSE_ID", mdLicenseRow.get("LICENSE_ID"));
            licenseRelRow.set("APP_GROUP_ID", (Object)appLicense.appGroupId);
            DO.addRow(licenseRelRow);
        }
        else {
            licenseRelRow = this.existingDO.getFirstRow("MdLicenseToAppGroupRel");
            if (licenseRelRow == null) {
                licenseRelRow = new Row("MdLicenseToAppGroupRel");
                licenseRelRow.set("LICENSE_ID", mdLicenseRow.get("LICENSE_ID"));
                licenseRelRow.set("APP_GROUP_ID", (Object)appLicense.appGroupId);
                DO.addRow(licenseRelRow);
            }
        }
        this.finalDO.merge(DO);
        return licenseRelRow;
    }
    
    private void addorUpdateStoreAppsLicenseSummary(final AppLicense appLicense, final Row mdLicenseDetailsRow) throws DataAccessException {
        Criteria licenseCriteria = null;
        licenseCriteria = new Criteria(Column.getColumn("StoreAppsLicenseSummary", "LICENSE_ID"), mdLicenseDetailsRow.get("LICENSE_ID"), 0);
        Row appLicenseRow = null;
        appLicenseRow = this.existingDO.getRow("StoreAppsLicenseSummary", licenseCriteria);
        if (appLicenseRow == null) {
            appLicenseRow = new Row("StoreAppsLicenseSummary");
            appLicenseRow.set("LICENSE_ID", mdLicenseDetailsRow.get("LICENSE_ID"));
            appLicenseRow.set("BUSINESSSTORE_ID", (Object)appLicense.businessStoreId);
            appLicenseRow.set("PURCHASED_COUNT", (Object)appLicense.licenseCount);
            appLicenseRow.set("PROVISIONED_COUNT", (Object)appLicense.licenseAlreadyUsedCount);
            this.finalDO.addRow(appLicenseRow);
        }
        else {
            appLicenseRow.set("PURCHASED_COUNT", (Object)appLicense.licenseCount);
            appLicenseRow.set("PROVISIONED_COUNT", (Object)appLicense.licenseAlreadyUsedCount);
            this.finalDO.updateBlindly(appLicenseRow);
        }
    }
    
    private void getExistingDO(final Long appGroupId) throws DataAccessException {
        this.getExistingDO(new Long[] { appGroupId });
    }
    
    private void getExistingDO(final Long[] appGroupId) throws DataAccessException {
        final Criteria appCriteria = new Criteria(Column.getColumn("MdLicenseToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 8);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdLicenseToAppGroupRel"));
        final Join licenseDetails = new Join("MdLicenseToAppGroupRel", "MdLicense", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 1);
        final Join licenseAppRel = new Join("MdLicense", "MdLicenseDetails", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 1);
        final Join appLicenseDetails = new Join("MdLicenseDetails", "MdAppLicenseInfo", new String[] { "LICENSE_DETAILS_ID" }, new String[] { "LICENSE_DETAILS_ID" }, 1);
        final Join licenseCodeDetails = new Join("MdLicenseToAppGroupRel", "MdLicenseCodes", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
        final Join deviceResRelDetails = new Join("MdLicenseCodes", "MdAppLicenseToResources", new String[] { "LICENSE_CODE_ID" }, new String[] { "LICENSE_CODE_ID" }, 1);
        final Join storeAppsLicenseSummary = new Join("MdLicense", "StoreAppsLicenseSummary", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 1);
        selectQuery.addJoin(licenseDetails);
        selectQuery.addJoin(licenseCodeDetails);
        selectQuery.addJoin(licenseAppRel);
        selectQuery.addJoin(appLicenseDetails);
        selectQuery.addJoin(deviceResRelDetails);
        selectQuery.addJoin(storeAppsLicenseSummary);
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.setCriteria(appCriteria);
        this.existingDO = MDMUtil.getPersistence().get(selectQuery);
    }
    
    private AppLicense getAppLicense(final JSONObject licenseJSON) throws JSONException {
        final AppLicense appLicense = new AppLicense();
        final int totalPurchasedCount = licenseJSON.getInt("TOTAL_APP_COUNT");
        final int availableCount = licenseJSON.getInt("AVAILABLE_APP_COUNT");
        final int provisionedCount = licenseJSON.getInt("ASSIGNED_APP_COUNT");
        appLicense.licenseRemainingCount = availableCount;
        appLicense.licenseAlreadyUsedCount = provisionedCount;
        appLicense.licenseCount = totalPurchasedCount;
        return appLicense;
    }
    
    public void deleteLicenseDetails(final Long appGroupID) throws DataAccessException {
        this.getExistingDO(appGroupID);
        if (this.existingDO != null && !this.existingDO.isEmpty()) {
            final Row licenseRow = this.existingDO.getFirstRow("MdLicense");
            if (licenseRow != null) {
                this.existingDO.deleteRow(licenseRow);
                MDMUtil.getPersistence().update(this.existingDO);
            }
        }
    }
    
    public void updateLicenseStatus(final Long resourceId, final Long appGroupId, final int licenseStatus) {
        try {
            this.getExistingDO(appGroupId);
            if (this.existingDO != null && !this.existingDO.isEmpty()) {
                final Criteria resCri = new Criteria(Column.getColumn("MdAppLicenseToResources", "RESOURCE_ID"), (Object)resourceId, 0);
                final Criteria appCri = new Criteria(Column.getColumn("MdAppLicenseToResources", "APP_GROUP_ID"), (Object)appGroupId, 0);
                final Row licRow = this.existingDO.getRow("MdAppLicenseToResources", resCri.and(appCri));
                if (licRow != null) {
                    licRow.set("LICENSE_CODE_STATUS", (Object)licenseStatus);
                    licRow.set("APPLIED_TIME", (Object)System.currentTimeMillis());
                    this.existingDO.updateRow(licRow);
                    MDMUtil.getPersistence().update(this.existingDO);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in updateLicenseStatus...", ex);
        }
    }
    
    static {
        AppLicenseHandler.appLicenseHandler = null;
    }
}
