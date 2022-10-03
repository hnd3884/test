package com.me.mdm.server.apps.businessstore.windows;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.Row;
import com.me.mdm.server.apps.windows.BusinessStoreAPIAccess;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import org.json.JSONObject;
import com.me.mdm.server.apps.businessstore.BaseSyncAppsHandler;

public class WindowsSyncAppsHandler extends BaseSyncAppsHandler
{
    public static final String TOTAL_APPS = "TotalBStoreApps";
    public static final String SUCCESS_APPS = "SuccuessBStoreApps";
    public static final String FAILURE_APPS = "FailureBStoreApps";
    public static final String BSTORE_SYNC_STATUS = "BstoreSyncStatus";
    public static final String BSTORE_LAST_SYNC = "BSTORE_LAST_SYNC";
    public static final String BSTORE_NEXT_SYNC = "BSTORE_NEXT_SYNC";
    
    public WindowsSyncAppsHandler(final Long businessStoreID, final Long customerID) {
        super(businessStoreID, customerID);
    }
    
    @Override
    public void syncApps(final JSONObject params) throws Exception {
        params.put("PlatformType", 3);
        final long customerId = params.getLong("CustomerID");
        CustomerParamsHandler.getInstance().addOrUpdateParameter("BstoreSyncStatus", "download", customerId);
        try {
            MessageProvider.getInstance().unhideMessage("WP_APP_NOT_PURCHASED", Long.valueOf(customerId));
            super.syncApps(params);
        }
        catch (final Exception e) {
            CustomerParamsHandler.getInstance().addOrUpdateParameter("BstoreSyncStatus", "Error", params.getLong("CustomerID"));
            this.logger.log(Level.WARNING, "error while Syncing Bstore", e);
            throw e;
        }
    }
    
    @Override
    public void handlePostAddOperationsForApp(final JSONObject jsonObject) throws DataAccessException, JSONException {
        this.addOrUpdateWindowsAppsDetails(jsonObject);
    }
    
    @Override
    public void handlePostSyncOperations(final HashMap params) throws Exception {
        this.moveUnManagedAppsToTrash();
        final Long customerID = params.get("CustomerID");
        try {
            this.updateSyncData(customerID);
            CustomerParamsHandler.getInstance().addOrUpdateParameter("bstoreFirstSyncPending", "false", (long)customerID);
            super.handlePostSyncOperations(params);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "error in updating final sync status ", e);
        }
    }
    
    @Override
    public void handlePreSyncOperations(final HashMap params) throws Exception {
        this.updateSyncData(params);
    }
    
    public void addOrUpdateWindowsAppsDetails(final JSONObject params) throws DataAccessException, JSONException {
        final JSONArray appIDArray = params.optJSONArray("AppIDList");
        final HashMap hashMap = new HashMap();
        hashMap.put("CustomerID", params.get("CUSTOMER_ID"));
        try {
            if (appIDArray != null) {
                final List appIdList = new ArrayList();
                for (int i = 0; i < appIDArray.length(); ++i) {
                    final Long appId = appIDArray.getLong(i);
                    appIdList.add(appId);
                }
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WindowsAppDetails"));
                selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "*"));
                final Criteria criteria = new Criteria(Column.getColumn("WindowsAppDetails", "APP_ID"), (Object)appIdList.toArray(), 8);
                selectQuery.setCriteria(criteria);
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                final JSONArray MDAppsArray = params.getJSONArray("MDAPPS");
                for (int j = 0; j < appIdList.size(); ++j) {
                    final Long appId2 = appIdList.get(j);
                    final JSONObject currentApps = (JSONObject)MDAppsArray.get(j);
                    final JSONObject windowsAppData = currentApps.getJSONObject("WindowsAppData");
                    final String productID = (String)windowsAppData.get("ProductID");
                    final String skuid = (String)windowsAppData.get("SKUID");
                    final String imgBG = (String)windowsAppData.get("IMG_BG");
                    final String pkgID = (String)windowsAppData.get("packageId");
                    final String licenseID = (String)windowsAppData.opt("contentId");
                    final String licenseBlob = (String)windowsAppData.opt("licenseBlob");
                    final Boolean isOffline = windowsAppData.optBoolean("offline", true);
                    final String fileHash = windowsAppData.optString("fileHash");
                    final String aumid = windowsAppData.optString("AUMID");
                    final JSONObject apiparams = new JSONObject();
                    apiparams.put("Type", (Object)"PackageIDQuery");
                    apiparams.put("StoreID", (Object)productID);
                    String phoneProductID = "";
                    try {
                        final JSONObject jsonObject = new BusinessStoreAPIAccess().getDataFromBusinessStore(apiparams);
                        phoneProductID = jsonObject.optString("windowsPhoneLegacyId", "");
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.WARNING, "Exception is adding windos store apps. Phone product ID is not sycned", e);
                    }
                    final Row winAppRow = dataObject.getRow("WindowsAppDetails", new Criteria(Column.getColumn("WindowsAppDetails", "APP_ID"), (Object)appId2, 0));
                    if (winAppRow == null) {
                        final Row row = new Row("WindowsAppDetails");
                        row.set("APP_ID", (Object)appId2);
                        row.set("IMG_BG", (Object)imgBG);
                        row.set("PACKAGE_ID", (Object)pkgID);
                        row.set("PRODUCT_ID", (Object)productID);
                        row.set("SKU_ID", (Object)skuid);
                        row.set("LICENSE_ID", (Object)licenseID);
                        row.set("LICENSE_CONTENT", (Object)licenseBlob);
                        row.set("IS_OFFLINE_APP", (Object)isOffline);
                        row.set("FILE_HASH", (Object)fileHash);
                        row.set("PHONE_PRODUCT_ID", (Object)phoneProductID);
                        row.set("AUMID", (Object)aumid);
                        dataObject.addRow(row);
                    }
                    else {
                        final Row row = winAppRow;
                        row.set("IMG_BG", (Object)imgBG);
                        row.set("PACKAGE_ID", (Object)pkgID);
                        row.set("PRODUCT_ID", (Object)productID);
                        row.set("SKU_ID", (Object)skuid);
                        row.set("LICENSE_ID", (Object)licenseID);
                        row.set("LICENSE_CONTENT", (Object)licenseBlob);
                        row.set("IS_OFFLINE_APP", (Object)isOffline);
                        row.set("FILE_HASH", (Object)fileHash);
                        row.set("PHONE_PRODUCT_ID", (Object)phoneProductID);
                        row.set("AUMID", (Object)aumid);
                        dataObject.updateRow(row);
                    }
                }
                MDMUtil.getPersistence().update(dataObject);
                hashMap.put("SuccuessBStoreApps", 1L);
                final Long collectionID = params.optLong("COLLECTION_ID", -1L);
                if (collectionID != -1L && appIdList != null && appIdList.size() > 0) {
                    final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdAppToCollection");
                    final Criteria collnCriteria = new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
                    final Criteria appIDCriteria = new Criteria(Column.getColumn("MdAppToCollection", "APP_ID"), (Object)appIdList.toArray(), 9);
                    deleteQuery.setCriteria(appIDCriteria.and(collnCriteria));
                    MDMUtil.getPersistenceLite().delete(deleteQuery);
                }
            }
            else {
                final String reason = params.optString("reason", "");
                if (!MDMStringUtils.isEmpty(reason) && reason.equalsIgnoreCase("SubscriptionSynced")) {
                    CustomerParamsHandler.getInstance().addOrUpdateParameter("bstoreSubscriptionUser", "true", (long)params.get("CUSTOMER_ID"));
                }
                hashMap.put("FailureBStoreApps", 1L);
            }
            this.updateSyncData(hashMap);
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Error Updating Status", e2);
        }
    }
    
    private void moveUnManagedAppsToTrash() {
    }
    
    private void updateSyncData(final HashMap hashMap) throws Exception {
        final Integer totalApps = hashMap.get("TotalAppsToSync");
        final Long successApps = hashMap.get("SuccuessBStoreApps");
        final Long failureApps = hashMap.get("FailureBStoreApps");
        final Long customerID = hashMap.get("CustomerID");
        if (totalApps != null) {
            final org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
            jsonObject.put((Object)"TotalBStoreApps", (Object)("" + totalApps));
            jsonObject.put((Object)"SuccuessBStoreApps", (Object)"0");
            jsonObject.put((Object)"FailureBStoreApps", (Object)"0");
            jsonObject.put((Object)"BstoreSyncStatus", (Object)"sync");
            CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObject, (long)customerID);
        }
        if (successApps != null) {
            final Long curCount = new Long(CustomerParamsHandler.getInstance().getParameterValue("SuccuessBStoreApps", (long)customerID)) + successApps;
            CustomerParamsHandler.getInstance().addOrUpdateParameter("SuccuessBStoreApps", "" + curCount, (long)customerID);
        }
        if (failureApps != null) {
            final Long curCount = new Long(CustomerParamsHandler.getInstance().getParameterValue("FailureBStoreApps", (long)customerID)) + failureApps;
            CustomerParamsHandler.getInstance().addOrUpdateParameter("FailureBStoreApps", "" + curCount, (long)customerID);
        }
    }
    
    public void updateSyncData(final Long customerID) throws Exception {
        final org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
        jsonObject.put((Object)"BSTORE_LAST_SYNC", (Object)("" + System.currentTimeMillis()));
        jsonObject.put((Object)"BSTORE_NEXT_SYNC", (Object)("" + (System.currentTimeMillis() + 86400000L)));
        jsonObject.put((Object)"BstoreSyncStatus", (Object)"completed");
        CustomerParamsHandler.getInstance().addOrUpdateParameters(jsonObject, (long)customerID);
    }
}
