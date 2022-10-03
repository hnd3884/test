package com.me.mdm.server.apps.businessstore.android;

import java.util.Iterator;
import java.util.Map;
import com.me.mdm.server.apps.businessstore.service.AndroidBusinessStoreService;
import java.util.Collection;
import com.me.mdm.server.apps.api.model.AppListModel;
import com.me.mdm.server.apps.AppTrashModeHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreAssetUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.SortColumn;
import com.me.mdm.server.apps.businessstore.model.android.AndroidStoreAppsSyncModel;
import com.me.mdm.server.apps.businessstore.model.android.AndroidStoreAppSyncDetailsModel;
import java.util.ArrayList;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.files.FileFacade;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.apps.android.afw.AFWAccountRegistrationHandler;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.me.mdm.server.apps.android.afw.AFWAccountStatusHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.api.APIUtil;
import java.util.Properties;
import java.util.List;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.mdm.server.apps.android.afw.appmgmt.GooglePlayBusinessAppHandler;
import org.json.JSONObject;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import java.util.logging.Logger;
import com.me.mdm.server.apps.businessstore.BaseStoreHandler;

public class AndroidStoreHandler extends BaseStoreHandler
{
    public static final String AFW_STORE_SYNC_PARAM = "afwFirstSyncPending";
    private Logger logger;
    
    public AndroidStoreHandler(final Long businessStoreID, final Long customerID) {
        super(businessStoreID, customerID);
        this.logger = Logger.getLogger("MDMBStoreLogger");
        this.platformType = 2;
        this.serviceType = BusinessStoreSyncConstants.BS_SERVICE_AFW;
        this.storeSyncKey = "afwFirstSyncPending";
    }
    
    @Override
    public JSONObject syncStore(final JSONObject jsonObject) throws Exception {
        final GooglePlayBusinessAppHandler handler = new GooglePlayBusinessAppHandler();
        handler.syncGooglePlay(this.customerID, 1, 1);
        final JSONObject response = new JSONObject();
        return response;
    }
    
    @Override
    public Object getSyncStoreStatus(final JSONObject jsonObject) throws Exception {
        final JSONObject syncStatus = new AndroidSyncAppsHandler().getStatus(this.customerID);
        final JSONObject afwDetails = GoogleForWorkSettings.getGoogleForWorkSettings(this.customerID, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        if (afwDetails.getBoolean("isConfigured")) {
            syncStatus.put("isConfigured", true);
            syncStatus.put("DomainName", afwDetails.get("MANAGED_DOMAIN_NAME"));
            syncStatus.put("DomainAdminAccount", afwDetails.get("DOMAIN_ADMIN_EMAIL_ID"));
            syncStatus.put("ServiceAccountId", afwDetails.get("ESA_EMAIL_ID"));
            syncStatus.put("EnterpriseID", afwDetails.get("ENTERPRISE_ID"));
            syncStatus.put("afwType", afwDetails.get("ENTERPRISE_TYPE"));
        }
        else {
            syncStatus.put("isConfigured", false);
        }
        super.getSyncStoreStatus(syncStatus);
        return syncStatus;
    }
    
    @Override
    public void validateAppToBusinessStoreProps(final List profileList, final Properties pkgToBusinessStore, final Properties appToBusinessProps) throws Exception {
    }
    
    @Override
    public JSONObject removeStoreDetails(final JSONObject jsonObject) throws Exception {
        final Integer serviceType = GoogleForWorkSettings.SERVICE_TYPE_AFW;
        final JSONObject resultJSON = new JSONObject();
        final JSONObject unenrollData = new JSONObject();
        final String userName = APIUtil.getUserName(jsonObject);
        final JSONObject googleSettings = GoogleForWorkSettings.getGoogleForWorkSettings(this.customerID, serviceType);
        final Boolean isConfigured = googleSettings.getBoolean("isConfigured");
        if (isConfigured) {
            unenrollData.put("EnterpriseId", googleSettings.get("ENTERPRISE_ID"));
            final String domainName = String.valueOf(googleSettings.get("MANAGED_DOMAIN_NAME"));
            JSONObject dataJSON = new JSONObject();
            dataJSON.put("Data", (Object)unenrollData);
            JSONObject responseJSON = new JSONObject();
            String status = "Success";
            if (serviceType.equals(GoogleForWorkSettings.SERVICE_TYPE_AFW)) {
                responseJSON = MDMApiFactoryProvider.getSecureKeyProviderAPI().unenrollESA(dataJSON);
                this.logger.log(Level.INFO, "Unenroll AFW {0} {1}", new Object[] { responseJSON.opt("Status"), responseJSON.opt("Remarks") });
                status = String.valueOf(responseJSON.optString("Status"));
            }
            if (status != null && status.equalsIgnoreCase("Success")) {
                GoogleForWorkSettings.resetSettings(this.customerID, serviceType);
                resultJSON.put("Status", (Object)"Success");
                resultJSON.put("success", true);
                MDMEventLogHandler.getInstance().MDMEventLogEntry(72502, null, userName, "mdm.afw.removed", "", this.customerID);
            }
            else {
                dataJSON = responseJSON.getJSONObject("Data");
                final String errorMsg = String.valueOf(dataJSON.optString("ErrorMsg"));
                resultJSON.put("Status", (Object)"Error");
                resultJSON.put("ErrorMessage", (Object)errorMsg);
                resultJSON.put("success", false);
            }
        }
        else {
            resultJSON.put("Status", (Object)"NotFound");
            resultJSON.put("ErrorMessage", (Object)"resource not configured to delete");
            resultJSON.put("success", false);
        }
        return resultJSON;
    }
    
    @Override
    public Object getStoreDetails(final JSONObject jsonObject) throws Exception {
        final JSONObject playstoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(this.customerID, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        final JSONObject response = new JSONObject();
        if (playstoreDetails != null && playstoreDetails.optBoolean("isConfigured")) {
            final Long businessStoreID = (Long)playstoreDetails.opt("BUSINESSSTORE_ID");
            response.put("BUSINESSSTORE_ID", (Object)businessStoreID);
            response.put("DOMAIN_ADMIN_EMAIL_ID", playstoreDetails.get("DOMAIN_ADMIN_EMAIL_ID"));
            response.put("ENTERPRISE_ID", playstoreDetails.get("ENTERPRISE_ID"));
            response.put("MANAGED_DOMAIN_NAME", playstoreDetails.get("MANAGED_DOMAIN_NAME"));
            final Integer enterpriseType = (Integer)playstoreDetails.get("ENTERPRISE_TYPE");
            response.put("ENTERPRISE_TYPE", (Object)enterpriseType);
            response.put("ESA_EMAIL_ID", playstoreDetails.get("ESA_EMAIL_ID"));
            if (enterpriseType.equals(GoogleForWorkSettings.ENTERPRISE_TYPE_EMM)) {
                response.put("managed_account_summary", (Object)new AFWAccountStatusHandler().getManagedAccountSummary(this.customerID));
            }
            final JSONObject syncStatus = MDBusinessStoreUtil.getBusinessStoreSyncDetails(businessStoreID);
            final Long lastSuccessfulSyncTime = syncStatus.optLong("LAST_SUCCESSFULL_SYNC_TIME", 0L);
            final String lastSyncTimeStr = (lastSuccessfulSyncTime != 0L) ? String.valueOf(lastSuccessfulSyncTime) : "--";
            response.put("PLAYSTORE_LAST_SYNC", (Object)lastSyncTimeStr);
            response.put("TotalPlaystoreApps", this.getPortalApprovedAppsCount(this.customerID, 2));
        }
        return response;
    }
    
    public int getPortalApprovedAppsCount(final Long customerId, final int platformType) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
        final Join appToGroupJoin = new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Criteria protalPurchasedCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)Boolean.TRUE, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
        sQuery.addJoin(appToGroupJoin);
        sQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID").count());
        sQuery.setCriteria(customerCriteria.and(protalPurchasedCriteria).and(platformCriteria));
        final int portalAppsCount = DBUtil.getRecordCount(sQuery, "MdAppGroupDetails", "APP_GROUP_ID");
        return portalAppsCount;
    }
    
    @Override
    public JSONObject getAllStoreDetails() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public JSONObject addStoreDetails(final JSONObject message) throws Exception {
        final String userName = APIUtil.getUserName(message);
        final Long userID = APIUtil.getUserID(message);
        final JSONObject jsonObject = message.getJSONObject("msg_body");
        if (!GoogleForWorkSettings.isAFWSettingsConfigured(this.customerID)) {
            JSONObject response = new JSONObject();
            final Integer enterpriseType = jsonObject.getInt("type");
            if (enterpriseType == GoogleForWorkSettings.ENTERPRISE_TYPE_EMM) {
                final String enterpriseToken = (String)jsonObject.get("enterprise_token");
                try {
                    final Long bstore_id = new AFWAccountRegistrationHandler().completeSignup(this.customerID, userID, enterpriseToken);
                    response.put("store_id", (Object)bstore_id);
                    MDMEventLogHandler.getInstance().MDMEventLogEntry(72501, null, userName, "mdm.afw.without.added", "", this.customerID);
                }
                catch (final RuntimeException e) {
                    throw new APIHTTPException("APP0003", new Object[] { e.getMessage() });
                }
            }
            else if (enterpriseType == GoogleForWorkSettings.ENTERPRISE_TYPE_GOOGLE) {
                JSONObject resultJSON = new JSONObject();
                final String domainName = String.valueOf(jsonObject.get("domain_name"));
                final String domainAdminAccount = String.valueOf(jsonObject.get("domain_admin_account"));
                final String bindingToken = String.valueOf(jsonObject.get("binding_token"));
                final Long fileId = Long.valueOf(jsonObject.get("eas_credential_json").toString());
                final String easJsonFileID = new FileFacade().validateIfExistsAndReturnFilePath(fileId, this.customerID);
                final JSONObject formData = new JSONObject();
                formData.put("ADMIN_ACCOUNT_ID", (Object)domainAdminAccount);
                formData.put("DOMAIN_NAME", (Object)domainName);
                formData.put("BINDING_TOKEN", (Object)bindingToken);
                formData.put("ESA_CREDENTIAL_JSON", (Object)easJsonFileID);
                formData.put("SERVICE_TYPE", (Object)GoogleForWorkSettings.SERVICE_TYPE_AFW);
                try {
                    resultJSON = GoogleForWorkSettings.persistSettings(this.customerID, ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), formData);
                    final String status = resultJSON.optString("Status");
                    if (resultJSON.optString("Status").equalsIgnoreCase("Success")) {
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(72501, null, userName, "mdm.afw.added", "", this.customerID);
                        new GooglePlayBusinessAppHandler().syncGooglePlay(this.customerID, 2, 3);
                    }
                }
                catch (final Exception ex) {
                    this.logger.log(Level.WARNING, "Exception while configuring Google Play Settings", ex);
                    resultJSON.put("Error", true);
                    resultJSON.put("Message", (Object)"Unknown Error! Contact Support with Logs");
                }
                finally {
                    ApiFactoryProvider.getFileAccessAPI().deleteFile(String.valueOf(formData.get("ESA_CREDENTIAL_JSON")));
                }
                response = resultJSON;
            }
            return response;
        }
        throw new APIHTTPException("APP0003", new Object[] { I18N.getMsg("mdm.api.error.store.config.error.reason", new Object[0]) });
    }
    
    @Override
    public JSONObject getStorePromoStatus(final JSONObject jsonObject) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("promo", false);
        final JSONObject storeDetails = (JSONObject)this.getStoreDetails(jsonObject);
        if (!storeDetails.has("BUSINESSSTORE_ID")) {
            responseJSON.put("promo", true);
        }
        return responseJSON;
    }
    
    @Override
    public void updateStoreSyncKey() throws Exception {
        final Long businessStoreID = MDBusinessStoreUtil.getBusinessStoreID(this.customerID, BusinessStoreSyncConstants.BS_SERVICE_AFW);
        MDBusinessStoreUtil.addOrUpdateBusinessStoreParam("afwFirstSyncPending", "true", businessStoreID);
    }
    
    @Override
    public Object getAppsFailureDetails(final JSONObject jsonObject) {
        final List<AndroidStoreAppSyncDetailsModel> failedAppsList = new ArrayList<AndroidStoreAppSyncDetailsModel>();
        final Long userID = jsonObject.optLong("user_id");
        final AndroidStoreAppsSyncModel androidStoreAppsSyncModel = new AndroidStoreAppsSyncModel();
        try {
            if (this.businessStoreID == null) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            this.logger.log(Level.INFO, "Going to fetch Managed Google play sync failed apps : {0}", this.businessStoreID);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdStoreAssetErrorDetails"));
            sQuery.addJoin(new Join("MdStoreAssetErrorDetails", "MdBusinessStoreToAssetRel", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            sQuery.addJoin(new Join("MdBusinessStoreToAssetRel", "MdAppGroupDetails", new String[] { "ASSET_IDENTIFIER" }, new String[] { "IDENTIFIER" }, 1));
            sQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
            sQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
            sQuery.addSelectColumn(Column.getColumn("MdStoreAssetErrorDetails", "STORE_ASSET_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdStoreAssetErrorDetails", "ERROR_CODE"));
            sQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToAssetRel", "STORE_ASSET_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToAssetRel", "ASSET_IDENTIFIER"));
            sQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
            sQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
            sQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
            sQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
            sQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
            sQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
            sQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"));
            sQuery.addSortColumn(new SortColumn(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (boolean)Boolean.TRUE));
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)this.businessStoreID, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)this.customerID, 0).or(new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)null, 0));
            sQuery.setCriteria(businessStoreCriteria.and(customerCriteria));
            final DMDataSetWrapper appDetailsDS = DMDataSetWrapper.executeQuery((Object)sQuery);
            final List tempAppList = new ArrayList();
            while (appDetailsDS.next()) {
                final Long storeAssetId = (Long)appDetailsDS.getValue("STORE_ASSET_ID");
                if (!tempAppList.contains(storeAssetId)) {
                    tempAppList.add(storeAssetId);
                    final String identifier = (String)appDetailsDS.getValue("ASSET_IDENTIFIER");
                    final String appName = (String)((appDetailsDS.getValue("GROUP_DISPLAY_NAME") != null) ? appDetailsDS.getValue("GROUP_DISPLAY_NAME") : identifier);
                    final String displayImgLoc = (String)((appDetailsDS.getValue("DISPLAY_IMAGE_LOC") != null) ? appDetailsDS.getValue("DISPLAY_IMAGE_LOC") : "--");
                    final Long appGroupId = (Long)((appDetailsDS.getValue("APP_GROUP_ID") != null) ? appDetailsDS.getValue("APP_GROUP_ID") : -1L);
                    final Long releaseLabelId = (Long)((appDetailsDS.getValue("RELEASE_LABEL_ID") != null) ? appDetailsDS.getValue("RELEASE_LABEL_ID") : -1L);
                    final Long packageId = (Long)((appDetailsDS.getValue("PACKAGE_ID") != null) ? appDetailsDS.getValue("PACKAGE_ID") : -1L);
                    final int errorCode = (int)((appDetailsDS.getValue("ERROR_CODE") != null) ? appDetailsDS.getValue("ERROR_CODE") : -1);
                    final AndroidStoreAppSyncDetailsModel androidStoreAppSyncDetailsModel = new AndroidStoreAppSyncDetailsModel();
                    androidStoreAppSyncDetailsModel.setAppName(appName);
                    androidStoreAppSyncDetailsModel.setIdentifier(identifier);
                    androidStoreAppSyncDetailsModel.setErrorCode(errorCode);
                    if (!MDMStringUtils.isEmpty(displayImgLoc)) {
                        androidStoreAppSyncDetailsModel.setDisplayImageLoc(displayImgLoc);
                    }
                    if (appGroupId != -1L) {
                        androidStoreAppSyncDetailsModel.setAppGroupId(appGroupId);
                    }
                    if (releaseLabelId != -1L) {
                        androidStoreAppSyncDetailsModel.setReleaseLabelId(releaseLabelId);
                    }
                    if (packageId != -1L) {
                        androidStoreAppSyncDetailsModel.setPackageId(packageId);
                    }
                    final String remarks = MDBusinessStoreAssetUtil.getSyncAppFailureRemarks(errorCode, appName, userID, androidStoreAppSyncDetailsModel);
                    androidStoreAppSyncDetailsModel.setRemarks(remarks);
                    failedAppsList.add(androidStoreAppSyncDetailsModel);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAppsFailureDetails()", ex);
        }
        androidStoreAppsSyncModel.setApps(failedAppsList);
        return androidStoreAppsSyncModel;
    }
    
    public void convertEnterpriseAppToPlaystoreApp(final JSONObject jsonObject, final List<Long> appIds) throws Exception {
        final Map<Long, String> appNames = AppsUtil.getInstance().getAppsNameFromPackageIds(appIds);
        final Map<Long, String> packageIdToIdentifier = AppsUtil.getInstance().getAppIdentifiersFromPackageIds(appIds);
        this.logger.log(Level.INFO, "Initiating enterprise app to playstore app conversion : {0}", appNames);
        final Long[] packageIDArr = appIds.toArray(new Long[0]);
        final String userName = jsonObject.optString("userName");
        new AppTrashModeHandler().softDeleteApps(packageIDArr, this.customerID, userName, false);
        final AppListModel appListModel = new AppListModel();
        appListModel.setIdentifierList(new ArrayList<String>(packageIdToIdentifier.values()));
        final AndroidBusinessStoreService bService = new AndroidBusinessStoreService();
        bService.addOrUpdateBusinessStoreApp(jsonObject.optLong("userID"), this.businessStoreID, this.customerID, appListModel);
        final String remarks = "mdm.apps.convert.enterprise_to_playstoreapp";
        String remarksArgs = "";
        for (final Long profileId : appNames.keySet()) {
            remarksArgs = appNames.get(profileId);
            MDMEventLogHandler.getInstance().MDMEventLogEntry(72509, null, userName, remarks, remarksArgs, this.customerID);
        }
    }
}
