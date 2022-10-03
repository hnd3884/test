package com.me.mdm.mdmmigration.target;

import com.me.mdm.server.apps.android.afw.AFWAccountStatusHandler;
import java.util.ArrayList;
import com.me.mdm.server.apps.usermgmt.StoreAccountManagementHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleManagedAccountHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.apps.api.model.AppListModel;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.apps.businessstore.service.AndroidBusinessStoreService;
import java.util.List;
import java.util.Arrays;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.server.apps.android.afw.appmgmt.GooglePlayBusinessAppHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import com.me.mdm.server.apps.businessstore.android.AndroidStoreHandler;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.certificate.CryptographyUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.mdmmigration.source.AFWRequestModel;
import org.json.JSONArray;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AFWMigrationDataUpdateManager
{
    private static Logger logger;
    
    public JSONObject updateAFWDetailsForMigration(final JSONObject request) throws Exception {
        final Long customerId = APIUtil.getCustomerID(request);
        final Long userId = APIUtil.getUserID(request);
        final JSONObject msgBody = request.getJSONObject("msg_body");
        final JSONArray requestData = msgBody.optJSONArray("data");
        if (requestData != null) {
            this.validateAndUpdateAFWDetails(requestData, customerId, userId, null);
        }
        final JSONObject response = new JSONObject();
        response.put("status", (Object)"success");
        return response;
    }
    
    public void AFWUpdateDetailsForMigration(final Long customerId, final Long userId, final AFWRequestModel afwRequestModel) throws Exception {
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MDMMigration")) {
            final JSONObject data = new CryptographyUtil().decrypt(afwRequestModel.getData(), afwRequestModel.getTag(), afwRequestModel.getCode(), 1);
            final JSONArray reqData = data.getJSONArray("data");
            final JSONArray bundleIdentifier = data.has("bundle_identifier") ? data.getJSONArray("bundle_identifier") : null;
            if (reqData != null) {
                this.validateAndUpdateAFWDetails(reqData, customerId, userId, bundleIdentifier);
            }
            return;
        }
        throw new APIHTTPException("COM0015", new Object[] { "Migration should be enabled for the server for this action. Contact support to enable it" });
    }
    
    private void validateAndUpdateAFWDetails(final JSONArray resourceWithData, final Long customerId, final Long userId, final JSONArray bundleIdentifier) throws Exception {
        final JSONObject afwDetails = resourceWithData.getJSONObject(0);
        if (GoogleForWorkSettings.isAFWSettingsConfigured(customerId)) {
            throw new APIHTTPException("COM0015", new Object[] { "AFW already configured" });
        }
        String bsIdentification;
        JSONObject credential;
        String esaId;
        String enterpriseId;
        String adminId;
        String enterpriseName;
        try {
            bsIdentification = afwDetails.getString("BUSINESSSTORE_IDENTIFICATION");
            credential = afwDetails.getJSONObject("credential.json");
            esaId = afwDetails.getString("ESA_EMAIL_ID");
            enterpriseId = afwDetails.getString("ENTERPRISE_ID");
            adminId = afwDetails.getString("DOMAIN_ADMIN_EMAIL_ID");
            enterpriseName = afwDetails.getString("MANAGED_DOMAIN_NAME");
        }
        catch (final JSONException e) {
            AFWMigrationDataUpdateManager.logger.log(Level.WARNING, "Necessary tables missing", (Throwable)e);
            throw new APIHTTPException("COM0005", new Object[] { "Necessary table or file details missing" });
        }
        final Long businessstore_id = new AndroidStoreHandler(null, customerId).addOrUpdateManagedStore(bsIdentification, userId);
        final String jsonPermanentPath = DCMetaDataUtil.getInstance().getClientDataParentDir() + "\\mdm\\android\\afw\\" + customerId + File.separator + businessstore_id + File.separator + "credential.json";
        ApiFactoryProvider.getFileAccessAPI().writeFile(jsonPermanentPath, credential.toString().getBytes());
        final JSONObject esaDataJSON = new JSONObject();
        esaDataJSON.put("BUSINESSSTORE_ID", (Object)businessstore_id);
        esaDataJSON.put("ESA_EMAIL_ID", (Object)esaId);
        esaDataJSON.put("ENTERPRISE_ID", (Object)enterpriseId);
        esaDataJSON.put("ESA_CREDENTIAL_JSON_PATH", (Object)jsonPermanentPath);
        esaDataJSON.put("DOMAIN_ADMIN_EMAIL_ID", (Object)adminId);
        esaDataJSON.put("MANAGED_DOMAIN_NAME", (Object)enterpriseName);
        esaDataJSON.put("ENTERPRISE_TYPE", (Object)GoogleForWorkSettings.ENTERPRISE_TYPE_EMM);
        esaDataJSON.put("OAUTH_TYPE", (Object)GoogleForWorkSettings.OAUTH_TYPE_ESA);
        GoogleForWorkSettings.addOrUpdateGoogleESADetails(esaDataJSON);
        AFWMigrationDataUpdateManager.logger.log(Level.INFO, "AFW without Gsuite configured :: Business store ID : {0} through migration", businessstore_id);
        new GooglePlayBusinessAppHandler().syncGooglePlay(customerId, 2, 3);
        if (bundleIdentifier != null && bundleIdentifier.length() > 0) {
            final AppFacade appFacade = new AppFacade();
            appFacade.validateBusinessStoreIDs(Arrays.asList(businessstore_id), customerId);
            final AndroidBusinessStoreService bService = new AndroidBusinessStoreService();
            final List bundlerIdentifierList = JSONUtil.convertJSONArrayToList(bundleIdentifier);
            final AppListModel appListModel = new AppListModel();
            appListModel.setIdentifierList(bundlerIdentifierList);
            bService.addOrUpdateBusinessStoreApp(userId, businessstore_id, customerId, appListModel);
        }
        MDMEventLogHandler.getInstance().MDMEventLogEntry(72501, null, "Migration", "mdm.afw.without.added", "", customerId);
    }
    
    public JSONObject updateAFWUsersAndAccounts(final JSONObject request) throws DataAccessException, JSONException {
        final Long customerId = APIUtil.getCustomerID(request);
        final JSONArray requestData = request.getJSONObject("msg_body").optJSONArray("data");
        if (requestData != null) {
            this.validateAndUpdateAFWDetails(requestData, customerId);
        }
        final JSONObject response = new JSONObject();
        response.put("status", (Object)"success");
        return response;
    }
    
    public void AFWUpdateUsersAndAccounts(final Long customerId, final AFWRequestModel afwRequestModel) throws Exception {
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MDMMigration")) {
            final JSONObject data = new CryptographyUtil().decrypt(afwRequestModel.getData(), afwRequestModel.getTag(), afwRequestModel.getCode(), 1);
            final JSONArray reqData = data.getJSONArray("data");
            if (reqData != null) {
                this.validateAndUpdateAFWDetails(reqData, customerId);
            }
            return;
        }
        throw new APIHTTPException("COM0015", new Object[] { "Migration should be enabled for the server for this action. Contact support to enable it" });
    }
    
    private void validateAndUpdateAFWDetails(final JSONArray afwUserData, final Long customerId) throws DataAccessException, JSONException {
        final JSONObject afwSetting = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        if (!afwSetting.getBoolean("isConfigured")) {
            AFWMigrationDataUpdateManager.logger.log(Level.WARNING, "AFW setting should be migrated first to migrate AFW accounts");
            throw new APIHTTPException("COM0015", new Object[] { "AFW setting should be migrated first to migrate AFW accounts" });
        }
        if (afwSetting.getInt("ENTERPRISE_TYPE") != GoogleForWorkSettings.ENTERPRISE_TYPE_EMM) {
            AFWMigrationDataUpdateManager.logger.log(Level.WARNING, "AFW users migration is supported only for AFW without GSuite");
            throw new APIHTTPException("COM0015", new Object[] { "AFW users migration is supported only for AFW without GSuite" });
        }
        int noOfAFWUserDevicesMigrated = 0;
        final DataObject dO = (DataObject)new WritableDataObject();
        final Long businessStoreId = afwSetting.getLong("BUSINESSSTORE_ID");
        for (int i = 0; i < afwUserData.length(); ++i) {
            try {
                final JSONObject userAccount = afwUserData.getJSONObject(i);
                final String udid = userAccount.getString("UDID");
                AFWMigrationDataUpdateManager.logger.log(Level.INFO, "Migrating udid {0}", udid);
                final int status = userAccount.getInt("ACCOUNT_STATUS");
                if (status == 2 || status == 1) {
                    final Long resIdForMigratedDevice = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid, customerId);
                    Boolean updatebsStoreUsers = false;
                    Boolean updateAccountData = false;
                    if (resIdForMigratedDevice != null) {
                        if (status == 2) {
                            updatebsStoreUsers = true;
                            updateAccountData = true;
                        }
                    }
                    else {
                        updatebsStoreUsers = true;
                    }
                    AFWMigrationDataUpdateManager.logger.log(Level.INFO, "Device ID in new server {0}", resIdForMigratedDevice);
                    if (updatebsStoreUsers) {
                        final Row newUser = new Row("BusinessStoreUsers");
                        final String userStoreId = userAccount.getString("BS_STORE_ID");
                        newUser.set("BUSINESSSTORE_ID", (Object)businessStoreId);
                        newUser.set("BS_STORE_ID", (Object)userStoreId);
                        newUser.set("BS_MDM_ID", (Object)new GoogleManagedAccountHandler().getUserNameForEMMAccountCreation(udid));
                        dO.addRow(newUser);
                        final Object bsUserId = newUser.get("BS_USER_ID");
                        ++noOfAFWUserDevicesMigrated;
                        if (updateAccountData) {
                            final Row newUserToDevice = new Row("BSUsersToManagedDevices");
                            newUserToDevice.set("BS_USER_ID", bsUserId);
                            newUserToDevice.set("MANAGED_DEVICE_ID", (Object)resIdForMigratedDevice);
                            newUserToDevice.set("ACCOUNT_STATUS", (Object)1);
                            dO.addRow(newUserToDevice);
                            final Object bsuserDeviceId = newUserToDevice.get("BS_USER_TO_DEVICE_ID");
                            final Row accStateRow = new Row("BSUserToManagedDeviceAccState");
                            accStateRow.set("ACCOUNT_STATE", (Object)1);
                            accStateRow.set("BS_USER_TO_DEVICE_ID", bsuserDeviceId);
                            dO.addRow(accStateRow);
                            final Row afwAccountStatus = new Row("AFWAccountStatus");
                            afwAccountStatus.set("RESOURCE_ID", (Object)resIdForMigratedDevice);
                            afwAccountStatus.set("ACCOUNT_STATUS", (Object)status);
                            afwAccountStatus.set("ERROR_CODE", (Object)(-1));
                            dO.addRow(afwAccountStatus);
                        }
                        else {
                            AFWMigrationDataUpdateManager.logger.log(Level.INFO, "Not migrating account details-status {0}", status);
                        }
                    }
                    else {
                        AFWMigrationDataUpdateManager.logger.log(Level.INFO, "Not migrating accounts and users-status is {0}", status);
                    }
                }
                else {
                    AFWMigrationDataUpdateManager.logger.log(Level.INFO, "No migration done.status {0}", status);
                }
            }
            catch (final JSONException e) {
                AFWMigrationDataUpdateManager.logger.log(Level.SEVERE, "JSON keys problem while migrating afw users", (Throwable)e);
                throw new APIHTTPException("COM0005", new Object[] { "Post JSON not properly constructed" });
            }
            catch (final Exception e2) {
                AFWMigrationDataUpdateManager.logger.log(Level.SEVERE, "Exception when migrating AFW users", e2);
                throw e2;
            }
        }
        AFWMigrationDataUpdateManager.logger.log(Level.INFO, "User count ready for migration {0}", noOfAFWUserDevicesMigrated);
        if (noOfAFWUserDevicesMigrated > 0) {
            MDMUtil.getPersistence().update(dO);
        }
    }
    
    public void handleMigradedDeviceAccount(final DeviceEvent userEvent) throws Exception {
        final String bsMdmId = new GoogleManagedAccountHandler().getUserNameForEMMAccountCreation(userEvent.udid);
        final Long bStoreId = GoogleForWorkSettings.getGoogleForWorkSettings(userEvent.customerID, GoogleForWorkSettings.SERVICE_TYPE_AFW).getLong("BUSINESSSTORE_ID");
        final Long bsUserId = new StoreAccountManagementHandler().getBSUserIdFromMDMId(bStoreId, bsMdmId);
        if (bsUserId != null) {
            final JSONObject mappingJSON = new JSONObject();
            mappingJSON.put("BS_USER_ID", (Object)bsUserId);
            mappingJSON.put("MANAGED_DEVICE_ID", (Object)userEvent.resourceID);
            mappingJSON.put("ACCOUNT_STATUS", 1);
            new StoreAccountManagementHandler().addOrUpdateStoreUserToManagedDevice(mappingJSON);
            new StoreAccountManagementHandler().addOrUpdateStoreUserToManagedDeviceAccState(mappingJSON, 1);
            final List resourceList = new ArrayList();
            resourceList.add(userEvent.resourceID);
            new AFWAccountStatusHandler().addOrUpdateAFWAccountStatus(resourceList, 2, -1);
            AFWMigrationDataUpdateManager.logger.log(Level.INFO, "Resource ID {0} added with account from previous server. user id {1}", new Object[] { userEvent.resourceID, bsUserId });
        }
        else {
            AFWMigrationDataUpdateManager.logger.log(Level.INFO, "Account and user data not migrated from previous server for resource id {0}. So initiating account now", userEvent.resourceID);
            new GoogleManagedAccountHandler().addAFWAccountAdditionCmd(userEvent.resourceID, userEvent.udid, userEvent.customerID);
        }
    }
    
    static {
        AFWMigrationDataUpdateManager.logger = Logger.getLogger("MDMMigrationLogger");
    }
}
