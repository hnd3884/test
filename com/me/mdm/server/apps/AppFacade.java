package com.me.mdm.server.apps;

import java.util.Hashtable;
import com.me.mdm.server.acp.MDMAppCatalogHandler;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.me.mdm.server.tree.apidatahandler.ApiListViewDataHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.idps.core.util.IdpsUtil;
import com.me.mdm.server.apps.actionvalidator.ValidateRemoveAppHandler;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppMgmtHandler;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.WritableDataObject;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.me.mdm.server.apps.android.afw.GoogleAPIErrorHandler;
import com.me.mdm.server.apps.android.afw.GooglePlayEnterpriseBusinessStore;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import com.adventnet.sym.server.mdm.inv.AppDataHandler;
import com.me.mdm.server.apps.android.afw.appmgmt.GooglePlayBusinessAppHandler;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import com.me.mdm.files.upload.FileUploadManager;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import java.io.ByteArrayInputStream;
import org.apache.tika.Tika;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import java.io.FileInputStream;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.mdm.server.msp.sync.SyncConfigurationsUtil;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import com.adventnet.sym.server.mdm.apps.ios.IOSModifiedEnterpriseAppsUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.sql.SQLException;
import com.me.mdm.server.customgroup.GroupFacade;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.mdm.server.apps.businessstore.StoreInterface;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.mdm.server.deployment.DeplymentConfigHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.device.DeviceFacade;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.Properties;
import java.util.Set;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Map;
import com.me.mdm.api.paging.PagingUtil;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.List;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.tracker.mics.MICSFeatureTrackerUtil;
import org.apache.commons.lang.StringUtils;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import com.me.mdm.server.role.RBDAUtil;
import java.util.HashMap;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.tracker.mics.MICSAppRepositoryFeatureController;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.msp.sync.SyncConfigurationListeners;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONException;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.me.mdm.files.FileFacade;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppFacade
{
    protected static Logger logger;
    
    public JSONObject getApp(final JSONObject message) throws APIHTTPException {
        try {
            final Long packageId = APIUtil.getResourceID(message, "app_id");
            this.validateIfAppFound(packageId, APIUtil.getCustomerID(message));
            final Integer platform = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "PLATFORM_TYPE");
            final JSONObject idJSON = this.getInstance(platform, message).getAppDetails(message);
            final String include = APIUtil.optStringFilter(message, "include", "");
            if (!"".equals(include) && include.equals("migrationdetails")) {
                return this.getDetailedApps(idJSON, message);
            }
            return idJSON;
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.SEVERE, "Exception in get app ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addApp(final JSONObject message) throws APIHTTPException {
        String tempFilePathDMTemp = null;
        String dispFilePathDMTemp = null;
        String fulldisplayPathDMTemp = null;
        String vppCodeFilePathDMTemp = null;
        final FileFacade fileFacade = new FileFacade();
        final AppVersionDBUtil appVersionDBUtil = AppVersionDBUtil.getInstance();
        String sEventLogRemarks = null;
        final int eventLogConstant = 2031;
        final Long customerId = APIUtil.getCustomerID(message);
        final String userName = APIUtil.getUserName(message);
        String appName = null;
        String remarksArgs = null;
        String channelName = "--";
        try {
            JSONObject requestJSON;
            try {
                requestJSON = message.getJSONObject("msg_body");
            }
            catch (final JSONException e) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            this.validateAddAppRequest(requestJSON, false);
            final int appType = requestJSON.getInt("app_type");
            final int platform = requestJSON.getInt("platform_type");
            appName = String.valueOf(requestJSON.get("app_name"));
            final Long labelId = requestJSON.optLong("label_id", (long)appVersionDBUtil.getProductionAppReleaseLabelIDForCustomer(customerId));
            this.validateIfReleaseLabelFound(labelId, customerId);
            channelName = appVersionDBUtil.getChannelName(labelId);
            final Long userID = APIUtil.getUserID(message);
            requestJSON.put("customerID", (Object)customerId);
            requestJSON.put("userID", (Object)userID);
            requestJSON.put("userName", (Object)APIUtil.getUserName(message));
            final JSONObject syncJSON = new JSONObject(requestJSON.toString());
            JSONObject idJSON;
            if (appType == 2) {
                if (requestJSON.has("app_file")) {
                    final Long appFileId = Long.valueOf(requestJSON.get("app_file").toString());
                    final String tempFilePathDM = new FileFacade().validateIfExistsAndReturnFilePath(appFileId, customerId);
                    final Long uploadedFileSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(tempFilePathDM);
                    tempFilePathDMTemp = fileFacade.getTempLocation(tempFilePathDM);
                    AppFacade.logger.log(Level.INFO, "FILESIZELOG: AppFacade: addApp: ActualUploadedFileSize: {0}", uploadedFileSize);
                    new FileFacade().writeFile(tempFilePathDMTemp, ApiFactoryProvider.getFileAccessAPI().readFile(tempFilePathDM));
                    final Long tempFileSize = new File(tempFilePathDMTemp).length();
                    AppFacade.logger.log(Level.WARNING, "FILESIZELOG: AppFacade: addApp: TempFileSizeInServer ->: {0}", tempFileSize);
                    if (!uploadedFileSize.equals(tempFileSize)) {
                        AppFacade.logger.log(Level.WARNING, "FILESIZELOG: AppFacade: addApp: ***File Size Differs*** -> ActualUploadedFileSize: {0} TempFileSizeInServer: {1}", new Object[] { uploadedFileSize, tempFileSize });
                    }
                    requestJSON.put("app_file", (Object)tempFilePathDMTemp);
                    final Long metaFileId = this.getExtractedMetaFileId(appFileId);
                    if (metaFileId != null) {
                        AppFacade.logger.log(Level.INFO, "Getting details of already extracted apk from meta files");
                        try {
                            final String metaFilePath = new FileFacade().validateIfExistsAndReturnFilePath(metaFileId, customerId);
                            final JSONObject metaJSON = new JSONObject(new String(ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(metaFilePath)));
                            if (metaJSON.has("allowEditableFields") && metaJSON.getBoolean("allowEditableFields")) {
                                this.validateEditableAppInfoAddition(requestJSON);
                                metaJSON.put("PackageName", (Object)requestJSON.getString("bundle_identifier"));
                                metaJSON.put("VersionName", (Object)requestJSON.getString("app_version"));
                                metaJSON.put("version_code", (Object)requestJSON.getString("version_code"));
                                metaJSON.put("APP_NAME", (Object)requestJSON.getString("app_name"));
                                AppFacade.logger.log(Level.INFO, "metaJSON for allowEditableFields user input {0}", metaJSON);
                            }
                            requestJSON.put("app_info", (Object)metaJSON);
                            final Iterator metaInfo = metaJSON.keys();
                            if (metaInfo != null) {
                                while (metaInfo.hasNext()) {
                                    AppFacade.logger.log(Level.INFO, "metaJSON contains {0}", metaInfo.next());
                                }
                            }
                        }
                        catch (final Exception e2) {
                            AppFacade.logger.log(Level.WARNING, "Unable to get the meta file, not harmful for operation", e2);
                        }
                    }
                }
                if (requestJSON.has("display_image")) {
                    final Long displayImageId = Long.valueOf(requestJSON.get("display_image").toString());
                    final String dispFilePathDM = new FileFacade().validateIfExistsAndReturnFilePath(displayImageId, customerId);
                    dispFilePathDMTemp = fileFacade.getTempLocation(dispFilePathDM);
                    new FileFacade().writeFile(dispFilePathDMTemp, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(dispFilePathDM));
                    requestJSON.put("display_image", (Object)dispFilePathDMTemp);
                }
                if (requestJSON.has("full_image")) {
                    final Long fullImageId = Long.valueOf(requestJSON.get("full_image").toString());
                    final String dispFilePathDM = new FileFacade().validateIfExistsAndReturnFilePath(fullImageId, customerId);
                    fulldisplayPathDMTemp = fileFacade.getTempLocation(dispFilePathDM);
                    new FileFacade().writeFile(fulldisplayPathDMTemp, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(dispFilePathDM));
                    requestJSON.put("full_image", (Object)fulldisplayPathDMTemp);
                }
                idJSON = this.getInstance(platform, message).addEnterpriseApp(requestJSON);
                final Long app_id = idJSON.getLong("PACKAGE_ID");
                final Long label_id = idJSON.getLong("RELEASE_LABEL_ID");
                idJSON.put("app_id", (Object)app_id);
                idJSON.put("label_id", (Object)label_id);
                final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
                secLog.put((Object)"APP_ID", (Object)app_id);
                secLog.put((Object)"LABEL_ID", (Object)label_id);
                if (requestJSON.has("dependency_ids")) {
                    secLog.put((Object)"DEPENDENCY_IDs", requestJSON.opt("dependency_ids"));
                }
                secLog.put((Object)"REMARKS", (Object)"enterprise app addition success");
                MDMOneLineLogger.log(Level.INFO, "ADD_APP", secLog);
                if (platform != 4) {
                    final String versionName = idJSON.optString("APP_VERSION", "--");
                    remarksArgs = appName + "@@@" + versionName + "@@@" + channelName;
                    sEventLogRemarks = "dc.mdm.actionlog.appmgmt.new_add_success";
                }
                else {
                    remarksArgs = appName + "@@@" + channelName;
                    sEventLogRemarks = "dc.mdm.actionlog.appmgmt.add_store_app_success";
                }
                final String appUniqueIdentifier = idJSON.getString("BUNDLE_IDENTIFIER") + "@@@" + idJSON.getString("APP_VERSION") + "@@@" + idJSON.getString("APP_NAME_SHORT_VERSION");
                syncJSON.put("app_id", (Object)app_id);
                syncJSON.put("app_unique_identifier", (Object)appUniqueIdentifier);
                syncJSON.put("LOGIN_ID", (Object)APIUtil.getLoginID(message));
                SyncConfigurationListeners.invokeListeners(syncJSON, 201);
            }
            else {
                final String bundleIdentifier = requestJSON.optString("bundle_identifier");
                appName = requestJSON.optString("app_name");
                if (requestJSON.has("vpp_file_source")) {
                    final Long vppFileId = Long.valueOf(requestJSON.get("vpp_file_source").toString());
                    final String dispFilePathDM2 = new FileFacade().validateIfExistsAndReturnFilePath(vppFileId, customerId);
                    vppCodeFilePathDMTemp = fileFacade.getTempLocation(dispFilePathDM2);
                    new FileFacade().writeFile(vppCodeFilePathDMTemp, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(dispFilePathDM2));
                    requestJSON.put("vppFileSource", (Object)vppCodeFilePathDMTemp);
                }
                if (requestJSON.has("display_image")) {
                    final Long displayImageId2 = Long.valueOf(requestJSON.get("display_image").toString());
                    final String dispFilePathDM2 = new FileFacade().validateIfExistsAndReturnFilePath(displayImageId2, customerId);
                    dispFilePathDMTemp = fileFacade.getTempLocation(dispFilePathDM2);
                    new FileFacade().writeFile(dispFilePathDMTemp, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(dispFilePathDM2));
                    requestJSON.put("display_image", (Object)dispFilePathDMTemp);
                }
                final String oldAppName = AppsUtil.getInstance().getAppProfileName(bundleIdentifier, customerId, platform);
                if (!MDMStringUtils.isEmpty(oldAppName)) {
                    throw new APIHTTPException("APP0004", new Object[] { appName, oldAppName });
                }
                if (AppsUtil.getInstance().isAppExistsInPackage(bundleIdentifier, platform, customerId, true)) {
                    throw new APIHTTPException("APP0006", new Object[] { APIUtil.getPortalString(platform) });
                }
                if (!requestJSON.has("bundle_identifier")) {
                    throw new APIHTTPException("COM0009", new Object[] { "bundle_identifier" });
                }
                idJSON = this.getInstance(platform, message).addStoreApp(requestJSON);
                MICSAppRepositoryFeatureController.addTrackingData(platform, MICSAppRepositoryFeatureController.AppOperation.ADD_APP, false, false);
                remarksArgs = appName + "@@@" + channelName;
                sEventLogRemarks = "dc.mdm.actionlog.appmgmt.add_store_app_success";
            }
            final JSONObject headerJSON = message.getJSONObject("msg_header");
            headerJSON.put("resource_identifier", (Object)idJSON);
            headerJSON.put("filters", (Object)message.getJSONObject("msg_header").getJSONObject("filters"));
            message.put("msg_header", (Object)headerJSON);
            return this.getApp(message);
        }
        catch (final Exception e3) {
            AppFacade.logger.log(Level.SEVERE, "Exception in AddApp", e3);
            sEventLogRemarks = "dc.mdm.actionlog.appmgmt.new_add_failure";
            remarksArgs = appName + "@@@" + channelName;
            if (e3 instanceof APIHTTPException) {
                throw (APIHTTPException)e3;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            if (dispFilePathDMTemp != null) {
                fileFacade.deleteFile(new File(dispFilePathDMTemp).getParent());
            }
            if (tempFilePathDMTemp != null) {
                fileFacade.deleteFile(new File(tempFilePathDMTemp).getParent());
            }
            if (fulldisplayPathDMTemp != null) {
                fileFacade.deleteFile(new File(fulldisplayPathDMTemp).getParent());
            }
            if (vppCodeFilePathDMTemp != null) {
                fileFacade.deleteFile(new File(vppCodeFilePathDMTemp).getParent());
            }
            if (!MDMStringUtils.isEmpty(appName)) {
                MDMEventLogHandler.getInstance().MDMEventLogEntry(eventLogConstant, null, userName, sEventLogRemarks, remarksArgs, customerId);
            }
        }
    }
    
    private void validateEditableAppInfoAddition(final JSONObject requestJSON) {
        if (!requestJSON.has("bundle_identifier") || !requestJSON.has("app_version") || !requestJSON.has("app_category_id") || !requestJSON.has("app_name") || !requestJSON.has("supported_devices") || !requestJSON.has("version_code")) {
            throw new APIHTTPException("COM0009", new Object[0]);
        }
    }
    
    public JSONObject updateApp(final JSONObject message) throws APIHTTPException {
        final FileFacade fileFacade = new FileFacade();
        String tempFilePathDMTemp = null;
        String dispFilePathDMTemp = null;
        String fulldisplayPathDMTemp = null;
        String sEventLogRemarks = null;
        final int eventLogConstant = 2031;
        final Long customerId = APIUtil.getCustomerID(message);
        final String userName = APIUtil.getUserName(message);
        String appName = null;
        String remarksArgs = null;
        Long releaseLabelID = -1L;
        String channelName = "--";
        try {
            JSONObject requestJSON;
            try {
                requestJSON = message.getJSONObject("msg_body");
            }
            catch (final JSONException e) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final Long packageId = APIUtil.getResourceID(message, "app_id");
            releaseLabelID = APIUtil.getResourceID(message, "label_id");
            if (releaseLabelID == -1L) {
                releaseLabelID = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerId);
            }
            final HashMap alreadyExistingAppDetailsInGivenLabel = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageId, releaseLabelID);
            channelName = AppVersionDBUtil.getInstance().getChannelName(releaseLabelID);
            this.validateIfAppFound(packageId, customerId);
            this.validateIfReleaseLabelFound(releaseLabelID, customerId);
            final JSONObject appDetails = AppsUtil.getInstance().getAppPackageLevelDetails(packageId, null);
            final int platform = (int)appDetails.get("PLATFORM_TYPE");
            int appType = (int)appDetails.get("PACKAGE_TYPE");
            final String identifier = (String)appDetails.get("IDENTIFIER");
            final Long appGroupId = (Long)appDetails.get("APP_GROUP_ID");
            final int requestedAppType = requestJSON.optInt("app_type");
            requestJSON.put("platform_type", platform);
            requestJSON.put("app_type", appType);
            requestJSON.put("requested_app_type", requestedAppType);
            this.validateAddAppRequest(requestJSON, true);
            appName = requestJSON.optString("app_name");
            final Long userID = APIUtil.getUserID(message);
            requestJSON.put("customerID", (Object)customerId);
            requestJSON.put("userID", (Object)userID);
            requestJSON.put("userName", (Object)APIUtil.getUserName(message));
            JSONObject idJSON = new JSONObject();
            requestJSON.put("msg_header", (Object)message.getJSONObject("msg_header"));
            final JSONObject syncJSON = new JSONObject(requestJSON.toString());
            if (requestJSON.has("display_image")) {
                final Long displayImageId = Long.valueOf(requestJSON.get("display_image").toString());
                final String dispFilePathDM = new FileFacade().validateIfExistsAndReturnFilePath(displayImageId, customerId);
                dispFilePathDMTemp = fileFacade.getTempLocation(dispFilePathDM);
                new FileFacade().writeFile(dispFilePathDMTemp, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(dispFilePathDM));
                requestJSON.put("display_image", (Object)dispFilePathDMTemp);
            }
            if (requestJSON.has("full_image")) {
                final Long fullImageId = Long.valueOf(requestJSON.get("full_image").toString());
                final String dispFilePathDM = new FileFacade().validateIfExistsAndReturnFilePath(fullImageId, customerId);
                fulldisplayPathDMTemp = fileFacade.getTempLocation(dispFilePathDM);
                new FileFacade().writeFile(fulldisplayPathDMTemp, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(dispFilePathDM));
                requestJSON.put("full_image", (Object)fulldisplayPathDMTemp);
            }
            appType = AppsUtil.getInstance().validatePlayStoreToEnterpriseConversion(requestJSON, appType, requestedAppType, identifier, appGroupId);
            if (appType == 2) {
                if (requestJSON.has("app_file")) {
                    final Long appFileId = Long.valueOf(requestJSON.get("app_file").toString());
                    final String tempFilePathDM = new FileFacade().validateIfExistsAndReturnFilePath(appFileId, customerId);
                    final Long uploadedFileSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(tempFilePathDM);
                    AppFacade.logger.log(Level.INFO, "FILESIZELOG: AppFacade: updateApp: ActualUploadedFileSize: {0}", uploadedFileSize);
                    tempFilePathDMTemp = fileFacade.getTempLocation(tempFilePathDM);
                    new FileFacade().writeFile(tempFilePathDMTemp, ApiFactoryProvider.getFileAccessAPI().readFile(tempFilePathDM));
                    final Long tempFileSize = new File(tempFilePathDMTemp).length();
                    AppFacade.logger.log(Level.INFO, "FILESIZELOG: AppFacade: updateApp: TempFileSizeInServer ->{0}", tempFileSize);
                    if (!uploadedFileSize.equals(tempFileSize)) {
                        AppFacade.logger.log(Level.WARNING, "FILESIZELOG: AppFacade: updateApp: ***File Size Differs*** -> ActualUploadedFileSize: {0} TempFileSizeInServer: {1}", new Object[] { uploadedFileSize, tempFileSize });
                    }
                    requestJSON.put("app_file", (Object)tempFilePathDMTemp);
                    final Long metaFileId = this.getExtractedMetaFileId(appFileId);
                    if (metaFileId != null) {
                        try {
                            final String metaFilePath = new FileFacade().validateIfExistsAndReturnFilePath(metaFileId, customerId);
                            final JSONObject metaJSON = new JSONObject(new String(ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(metaFilePath)));
                            if (metaJSON.has("allowEditableFields") && metaJSON.getBoolean("allowEditableFields")) {
                                AppFacade.logger.log(Level.INFO, "Taking user input - allowEditableFields");
                                this.validateEditableAppInfoAddition(requestJSON);
                                metaJSON.put("PackageName", (Object)requestJSON.getString("bundle_identifier"));
                                metaJSON.put("VersionName", (Object)requestJSON.getString("app_version"));
                                metaJSON.put("version_code", (Object)requestJSON.getString("version_code"));
                                metaJSON.put("APP_NAME", (Object)requestJSON.getString("app_name"));
                                AppFacade.logger.log(Level.INFO, "metaJSON for allowEditableFields user input {0}", metaJSON);
                            }
                            requestJSON.put("app_info", (Object)metaJSON);
                        }
                        catch (final Exception e2) {
                            AppFacade.logger.log(Level.WARNING, "Unable to get the meta file, not harmful for operation", e2);
                        }
                    }
                }
                final JSONObject jsonObject = this.getInstance(platform, message).updateEnterpriseApp(requestJSON);
                final String versionName = jsonObject.optString("APP_VERSION", "--");
                final String appUniqueIdentifier = jsonObject.getString("BUNDLE_IDENTIFIER") + "@@@" + versionName + "@@@" + jsonObject.getString("APP_NAME_SHORT_VERSION");
                final String existingAppUniqueIdentifier = alreadyExistingAppDetailsInGivenLabel.get("IDENTIFIER") + "@@@" + alreadyExistingAppDetailsInGivenLabel.get("APP_VERSION") + "@@@" + alreadyExistingAppDetailsInGivenLabel.get("APP_NAME_SHORT_VERSION");
                syncJSON.put("existing_app_unique_identifier", (Object)existingAppUniqueIdentifier);
                syncJSON.put("app_unique_identifier", (Object)appUniqueIdentifier);
                syncJSON.put("LOGIN_ID", (Object)APIUtil.getLoginID(message));
                syncJSON.put("app_id", jsonObject.getLong("PACKAGE_ID"));
                SyncConfigurationListeners.invokeListeners(syncJSON, ((requestJSON.has("force_update_in_label") && requestJSON.optBoolean("force_update_in_label")) || !requestJSON.has("app_file")) ? 203 : 202);
                if (platform == 4) {
                    sEventLogRemarks = "dc.mdm.actionlog.appmgmt.update_store_app_success";
                    remarksArgs = appName + "@@@" + channelName;
                }
                else if (requestJSON.has("force_update_in_label") && requestJSON.getBoolean("force_update_in_label") == Boolean.FALSE) {
                    sEventLogRemarks = "mdm.actionlog.appmgmt.version_added_as_separate";
                    remarksArgs = appName + "@@@" + versionName + "@@@" + channelName;
                }
                else if (requestJSON.has("app_file")) {
                    sEventLogRemarks = "mdm.action.app_version_update_over_existing";
                    remarksArgs = appName + "@@@" + alreadyExistingAppDetailsInGivenLabel.get("APP_VERSION") + "@@@" + I18N.getMsg((String)alreadyExistingAppDetailsInGivenLabel.get("RELEASE_LABEL_DISPLAY_NAME"), new Object[0]) + "@@@" + appName + "@@@" + versionName + "@@@" + channelName;
                }
                else {
                    sEventLogRemarks = "dc.mdm.actionlog.appmgmt.new_update_success";
                    remarksArgs = appName + "@@@" + versionName + "@@@" + channelName;
                }
                final Long app_id = jsonObject.getLong("PACKAGE_ID");
                final Long label_id = jsonObject.getLong("RELEASE_LABEL_ID");
                idJSON.put("app_id", (Object)app_id);
                idJSON.put("label_id", (Object)label_id);
                final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
                secLog.put((Object)"APP_ID", (Object)app_id);
                secLog.put((Object)"LABEL_ID", (Object)label_id);
                secLog.put((Object)"REMARKS", (Object)"enterprise app update success");
                MDMOneLineLogger.log(Level.INFO, "UPDATE_APP", secLog);
                if (requestJSON.optBoolean("convertPlayStoreToEnterprise")) {
                    sEventLogRemarks = "mdm.apps.convert.play_to_enterprise";
                    remarksArgs = appName;
                }
            }
            else {
                message.put("msg_body", (Object)requestJSON);
                idJSON = this.getInstance(platform, message).updateStoreApp(message);
                sEventLogRemarks = "dc.mdm.actionlog.appmgmt.update_store_app_success";
                remarksArgs = appName + "@@@" + channelName;
            }
            final JSONObject headerJSON = message.getJSONObject("msg_header");
            headerJSON.put("resource_identifier", (Object)idJSON);
            headerJSON.put("filters", (Object)message.getJSONObject("msg_header").getJSONObject("filters"));
            message.put("msg_header", (Object)headerJSON);
            return this.getApp(message);
        }
        catch (final Exception e3) {
            AppFacade.logger.log(Level.SEVERE, "exception in updateApp", e3);
            remarksArgs = appName + "@@@" + channelName;
            sEventLogRemarks = "dc.mdm.actionlog.appmgmt.new_update_failure";
            if (e3 instanceof APIHTTPException) {
                throw (APIHTTPException)e3;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            if (dispFilePathDMTemp != null) {
                fileFacade.deleteFile(dispFilePathDMTemp);
            }
            if (tempFilePathDMTemp != null) {
                fileFacade.deleteFile(tempFilePathDMTemp);
            }
            if (fulldisplayPathDMTemp != null) {
                fileFacade.deleteFile(fulldisplayPathDMTemp);
            }
            if (!MDMStringUtils.isEmpty(appName)) {
                MDMEventLogHandler.getInstance().MDMEventLogEntry(eventLogConstant, null, userName, sEventLogRemarks, remarksArgs, customerId);
            }
        }
    }
    
    private void validateAddAppRequest(final JSONObject requestJSON, final boolean isUpdate) throws APIHTTPException {
        final StringBuilder errorString = new StringBuilder();
        if (!requestJSON.has("app_name") && !isUpdate) {
            errorString.append("app_name, ");
        }
        if (!requestJSON.has("platform_type")) {
            errorString.append("platform_type, ");
        }
        try {
            if (!isUpdate) {
                if (MDMStringUtils.isEmpty(requestJSON.optString("app_name", (String)null)) && errorString.indexOf("app_name") < 0) {
                    errorString.append("app_name, ");
                }
                if (requestJSON.has("app_type")) {
                    if (requestJSON.optInt("app_type", -1) == 2) {
                        if (!requestJSON.has("app_file") && requestJSON.getInt("platform_type") != 4) {
                            errorString.append("app_file, ");
                        }
                    }
                    else if (requestJSON.optInt("app_type", -1) != 1) {
                        errorString.append("app_type, ");
                    }
                }
                else {
                    errorString.append("app_type, ");
                }
            }
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
        if (requestJSON.has("app_configuration")) {
            errorString.append("app_configuration, ");
        }
        if (requestJSON.has("config_file")) {
            errorString.append("config_file, ");
        }
        if (errorString.length() != 0) {
            throw new APIHTTPException("COM0005", new Object[] { errorString.substring(0, errorString.length() - 2) });
        }
    }
    
    public void deleteApp(final JSONObject message) throws APIHTTPException {
        try {
            Long appID = APIUtil.getResourceID(message, "app_id");
            final Long loginId = APIUtil.getLoginID(message);
            final String userName = APIUtil.getUserName(message);
            final Boolean isMDMAdmin = RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginId, false);
            if (!isMDMAdmin) {
                throw new APIHTTPException("COM0015", new Object[] { I18N.getMsg("mdm.apps.trash_by_admin", new Object[0]) });
            }
            if (appID == -1L) {
                appID = null;
            }
            HashSet<Long> appSet;
            if (appID != null) {
                appSet = new HashSet<Long>(Arrays.asList(appID));
                this.validateIfAppFound(appID, APIUtil.getCustomerID(message));
            }
            else if (message.has("msg_body") && message.getJSONObject("msg_body").optJSONArray("app_ids") != null) {
                appSet = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("app_ids")));
            }
            else {
                if (MDMStringUtils.isEmpty(APIUtil.getStringFilter(message, "ids"))) {
                    throw new APIHTTPException("COM0009", new Object[] { "app_ids" });
                }
                final String[] ids = APIUtil.getStringFilter(message, "ids").split(",");
                appSet = new HashSet<Long>();
                for (final String id : ids) {
                    appSet.add(Long.valueOf(id));
                }
            }
            final Long[] packageIDALongArr = appSet.toArray(new Long[0]);
            final Long[] profileIds = AppsUtil.getInstance().getProfileIDS(packageIDALongArr);
            final Long customerId = APIUtil.getCustomerID(message);
            final String profileIdString = StringUtils.join((Object[])profileIds, ',');
            final boolean softDelete = APIUtil.getBooleanFilter(message, "softdelete", false);
            if (softDelete) {
                new AppTrashModeHandler().softDeleteApps(packageIDALongArr, customerId, userName, true);
            }
            else {
                new AppTrashModeHandler().moveAppsToTrash(profileIdString, customerId);
                MICSFeatureTrackerUtil.addAppRepositoryAppDelete(packageIDALongArr, customerId);
            }
            final JSONObject syncJSON = new JSONObject();
            syncJSON.put("app_ids", (Object)packageIDALongArr);
            syncJSON.put("softdelete", (Object)APIUtil.getBooleanFilter(message, "softdelete", false));
            syncJSON.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(message));
            syncJSON.put("PROFILE_TYPE", 2);
            syncJSON.put("LAST_MODIFIED_BY", (Object)APIUtil.getUserID(message));
            syncJSON.put("LOGIN_ID", (Object)APIUtil.getLoginID(message));
            SyncConfigurationListeners.invokeListeners(syncJSON, 209);
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "exception in delete", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void validateIfAppFound(final Long app, final Long customerID) throws APIHTTPException {
        if (app == null || app == -1L) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
            selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria criteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)app, 0).and(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator<Row> rows = dataObject.getRows("MdPackage");
            final ArrayList<Long> apps = new ArrayList<Long>();
            while (rows.hasNext()) {
                apps.add(Long.valueOf(String.valueOf(rows.next().get("PACKAGE_ID"))));
            }
            if (apps.size() == 0) {
                throw new APIHTTPException("COM0008", new Object[] { app });
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(AppsUtil.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void validateIfReleaseLabelFound(final Long releaseLabelID, final Long customerID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppReleaseLabel"));
        final Criteria customerIDCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria releaseLabelIDCriteria = new Criteria(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"), (Object)releaseLabelID, 0);
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        selectQuery.setCriteria(customerIDCriteria.and(releaseLabelIDCriteria));
        final DataObject dao = DataAccess.get(selectQuery);
        if (dao.isEmpty()) {
            throw new APIHTTPException("COM0008", new Object[] { releaseLabelID });
        }
    }
    
    public void validateIfAppsFound(Collection<Long> appset, final Long customerID) throws APIHTTPException {
        if (appset.isEmpty()) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        try {
            appset = new HashSet<Long>(appset);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
            selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria criteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)appset.toArray(), 8).and(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator<Row> rows = dataObject.getRows("MdPackage");
            final ArrayList<Long> apps = new ArrayList<Long>();
            while (rows.hasNext()) {
                apps.add(Long.valueOf(String.valueOf(rows.next().get("PACKAGE_ID"))));
            }
            appset.removeAll(apps);
            if (appset.size() > 0) {
                throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(appset) });
            }
        }
        catch (final DataAccessException e) {
            Logger.getLogger(AppsUtil.class.getName()).log(Level.SEVERE, null, (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Deprecated
    public void validateReleaseLabelName(final String labelName, final Long customerID) throws DataAccessException, APIHTTPException, Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppReleaseLabel"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("AppReleaseLabel", "CUSTOMER_ID"), (Object)customerID, 0));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        final DataObject releaseLabelDO = DataAccess.get(selectQuery);
        final Iterator<Row> releaseLabelRows = releaseLabelDO.getRows("AppReleaseLabel");
        while (releaseLabelRows.hasNext()) {
            final Row releaseLabelRow = releaseLabelRows.next();
            if (I18N.getMsg(String.valueOf(releaseLabelRow.get("RELEASE_LABEL_DISPLAY_NAME")), new Object[0]).equalsIgnoreCase(labelName)) {
                throw new APIHTTPException("APP0025", new Object[] { labelName });
            }
        }
    }
    
    public Object getRepositoryApps(final JSONObject request, final boolean isTrash) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        Connection conn = null;
        DataSet ds = null;
        try {
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(request);
            final Long customerId = APIUtil.getCustomerID(request);
            final JSONArray result = new JSONArray();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
            query.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            query.addJoin(new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            query.addJoin(new Join("MdPackageToAppData", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            query.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            query.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            query.addJoin(new Join("MdAppGroupDetails", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            query.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            query.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            query.addJoin(new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 2));
            query.addJoin(AppVersionDBUtil.getInstance().getAppReleaseLabelJoin());
            query.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_ID"));
            query.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_ADDED_TIME"));
            query.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_MODIFIED_TIME"));
            query.addSelectColumn(Column.getColumn("AppCategory", "APP_CATEGORY_NAME"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_TITLE"));
            query.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"));
            query.addSelectColumn(Column.getColumn("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
            query.addSelectColumn(Column.getColumn("MdPackageToAppData", "DESCRIPTION"));
            query.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_FILE_LOC"));
            query.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
            query.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            query.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
            query.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
            query.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_VERSION_STATUS"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
            final Criteria hiddenProfileCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)7, 1);
            Criteria finalCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)isTrash, 0).and(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            finalCriteria = finalCriteria.and(AppVersionDBUtil.getInstance().getCriteriaForCollectionIdWithMdAppToCollection()).and(hiddenProfileCriteria);
            final String search = APIUtil.getStringFilter(request, "search");
            Criteria searchCriteria = null;
            if (!MDMStringUtils.isEmpty(search)) {
                searchCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)search, 12, false);
            }
            if (searchCriteria != null) {
                finalCriteria = finalCriteria.and(searchCriteria);
            }
            final String platform = APIUtil.getStringFilter(request, "platform");
            final Criteria platformCri = null;
            if (platform != null) {
                final String[] platformTypes = platform.split(",");
                final ArrayList<Integer> values = new ArrayList<Integer>();
                for (int i = 0; i < platformTypes.length; ++i) {
                    final int temp = Integer.parseInt(platformTypes[i]);
                    if (temp == 2 || temp == 1 || temp == 3 || temp == 4) {
                        values.add(temp);
                    }
                }
                if (values.size() != 0) {
                    final Criteria deviceTypeCri = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)values.toArray(), 8);
                    finalCriteria = finalCriteria.and(deviceTypeCri);
                }
            }
            Criteria businessAppCri = null;
            final String isBussinessApps = APIUtil.getStringFilter(request, "isbusinessapp");
            if (isBussinessApps != null) {
                if (Boolean.parseBoolean(isBussinessApps)) {
                    businessAppCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
                }
                else {
                    businessAppCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)false, 0);
                }
                finalCriteria = finalCriteria.and(businessAppCri);
            }
            Criteria paidApp = null;
            final String paidApps = APIUtil.getStringFilter(request, "ispaidapp");
            if (paidApps != null) {
                if (Boolean.parseBoolean(paidApps)) {
                    paidApp = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"), (Object)true, 0);
                }
                else {
                    paidApp = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"), (Object)false, 0);
                }
                finalCriteria = finalCriteria.and(paidApp);
            }
            final String isEnterpriseApps = APIUtil.getStringFilter(request, "isenterpriseapp");
            Criteria isEnterpriseApp = null;
            if (isEnterpriseApps != null) {
                if (Boolean.parseBoolean(isEnterpriseApps)) {
                    isEnterpriseApp = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 0);
                }
                else {
                    isEnterpriseApp = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"), (Object)2, 1);
                }
                finalCriteria = finalCriteria.and(isEnterpriseApp);
            }
            query.setCriteria(finalCriteria);
            final SelectQuery cQuery = this.getRepositoryAppsCountQuery(finalCriteria);
            final int count = DBUtil.getRecordCount(cQuery);
            final JSONObject meta = new JSONObject();
            meta.put("total_record_count", count);
            response.put("metadata", (Object)meta);
            if (count != 0) {
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null) {
                    response.put("paging", (Object)pagingJSON);
                }
                final SelectQuery pagingQuery = cQuery;
                pagingQuery.removeSelectColumn(0);
                Column packageIdCol = new Column("MdPackage", "PACKAGE_ID");
                packageIdCol = packageIdCol.distinct();
                packageIdCol.setColumnAlias("PACKAGE_ID");
                pagingQuery.addSelectColumn(packageIdCol);
                pagingQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                pagingQuery.addSortColumn(new SortColumn("MdPackage", "PACKAGE_ID", false));
                final DerivedTable pagingTable = new DerivedTable("PAGING_TABLE", (Query)pagingQuery);
                query.addJoin(new Join(Table.getTable("MdPackage"), (Table)pagingTable, new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
                final RelationalAPI relapi = RelationalAPI.getInstance();
                conn = relapi.getConnection();
                ds = relapi.executeQuery((Query)query, conn);
                final JSONObject appCountJson = AppsUtil.getInstance().getDistributedAppCount(customerId, null);
                final Map<Long, Integer> packageIdToIdx = new HashMap<Long, Integer>();
                int currentIdx = 0;
                while (ds.next()) {
                    final Long packageId = (Long)ds.getValue("PACKAGE_ID");
                    String version = String.valueOf(ds.getValue("APP_VERSION"));
                    String versionCode = String.valueOf(ds.getValue("APP_NAME_SHORT_VERSION"));
                    final String releaseLabelId = String.valueOf(ds.getValue("RELEASE_LABEL_ID"));
                    final String releaseLabelName = String.valueOf(ds.getValue("RELEASE_LABEL_DISPLAY_NAME"));
                    final Integer appVersionStatus = (Integer)ds.getValue("APP_VERSION_STATUS");
                    final JSONObject releaseLabelDetails = new JSONObject().put("release_label_id", (Object)releaseLabelId).put("release_label_name", (Object)I18N.getMsg(releaseLabelName, new Object[0])).put("app_version", (Object)version).put("version_code", (Object)versionCode).put("is_approved", appVersionStatus != null);
                    final Integer platformType = (Integer)ds.getValue("PLATFORM_TYPE");
                    JSONArray releaseLabelTypeArray = null;
                    final Integer idx = packageIdToIdx.get(packageId);
                    if (idx != null) {
                        final JSONObject existingPackageEntry = result.getJSONObject((int)idx);
                        final String existingVersion = String.valueOf(existingPackageEntry.get("version"));
                        final String existingVersionCode = String.valueOf(existingPackageEntry.get("version_code"));
                        if (appVersionStatus == null) {
                            version = existingVersion;
                            versionCode = existingVersionCode;
                        }
                        final JSONArray existingReleaseLabels = existingPackageEntry.getJSONArray("release_labels");
                        final Boolean isExists = JSONUtil.checkValueExistsInJSONArray(existingReleaseLabels, releaseLabelId, "release_label_id");
                        if (!isExists) {
                            existingReleaseLabels.put((Object)releaseLabelDetails);
                        }
                        releaseLabelTypeArray = existingReleaseLabels;
                    }
                    else {
                        releaseLabelTypeArray = new JSONArray();
                        releaseLabelTypeArray.put((Object)releaseLabelDetails);
                    }
                    final JSONObject app = new JSONObject();
                    app.put("app_id", (Object)ds.getValue("PACKAGE_ID").toString());
                    app.put("APP_CATEGORY_NAME", (Object)String.valueOf(ds.getValue("APP_CATEGORY_NAME")));
                    app.put("version", (Object)version);
                    app.put("version_code", (Object)versionCode);
                    app.put("platform_type", (Object)String.valueOf(platformType));
                    app.put("app_type", (Object)String.valueOf(ds.getValue("PACKAGE_TYPE")));
                    app.put("description", (Object)String.valueOf(ds.getValue("DESCRIPTION")));
                    app.put("APP_NAME", (Object)String.valueOf(ds.getValue("PROFILE_NAME")));
                    app.put("added_time", (Object)String.valueOf(ds.getValue("PACKAGE_ADDED_TIME")));
                    app.put("modified_time", (Object)String.valueOf(ds.getValue("PACKAGE_MODIFIED_TIME")));
                    app.put("release_labels", (Object)releaseLabelTypeArray);
                    app.put("app_group_id", (Object)String.valueOf(ds.getValue("APP_GROUP_ID")));
                    app.put("bundle_identifier", (Object)String.valueOf(ds.getValue("IDENTIFIER")));
                    app.put("app_title", (Object)String.valueOf(ds.getValue("APP_TITLE")));
                    try {
                        final Criteria cr = new Criteria(Column.getColumn("MdAppCatalogToUser", "APP_GROUP_ID"), (Object)ds.getValue("APP_GROUP_ID").toString(), 0);
                        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToUser"));
                        selectQuery.setCriteria(cr);
                        final int user_count = MDMDBUtil.getCount(selectQuery, "MdAppCatalogToUser", "RESOURCE_ID");
                        app.put("distributed_user_count", user_count);
                        app.put("distributed_device_count", (Object)appCountJson.optString(String.valueOf(ds.getValue("APP_GROUP_ID"))));
                    }
                    catch (final Exception e) {
                        AppFacade.logger.log(Level.SEVERE, null, e);
                    }
                    final HashMap hm = new HashMap();
                    if (ds.getValue("DISPLAY_IMAGE_LOC") != null) {
                        final String displayImageLoc = String.valueOf(ds.getValue("DISPLAY_IMAGE_LOC"));
                        if (!displayImageLoc.startsWith("http")) {
                            app.put("icon", (Object)MDMRestAPIFactoryProvider.getAPIUtil().getFileURL(displayImageLoc));
                        }
                        else {
                            app.put("icon", (Object)displayImageLoc);
                        }
                    }
                    if (idx != null) {
                        result.put((int)idx, (Object)app);
                    }
                    else {
                        result.put(currentIdx, (Object)app);
                        packageIdToIdx.put(JSONUtil.optLongForUVH(app, "app_id", Long.valueOf(-1L)), currentIdx);
                        ++currentIdx;
                    }
                }
            }
            response.put("apps", (Object)result);
        }
        catch (final Exception e2) {
            AppFacade.logger.log(Level.SEVERE, "exception in getApps", e2);
        }
        finally {
            this.closeConnection(conn, ds);
        }
        return response;
    }
    
    private JSONObject getDetailedApps(final JSONObject app, final JSONObject request) {
        final JSONArray appVersions = new JSONArray();
        try {
            final Long app_id = app.getLong("app_id");
            final JSONArray releaseLabels = (JSONArray)app.remove("release_labels");
            if (releaseLabels.length() > 1) {
                for (int i = 0; i < releaseLabels.length(); ++i) {
                    String app_file = "";
                    final String icon = "";
                    final JSONObject fileJSON = new JSONObject();
                    final JSONObject currentVersion = new JSONObject();
                    final JSONObject currentRelease = releaseLabels.getJSONObject(i);
                    final Long label_id = Long.valueOf(currentRelease.getString("release_label_id"));
                    request.getJSONObject("msg_header").getJSONObject("resource_identifier").put("label_id", (Object)label_id);
                    final DataObject appDetailsDO = MDMAppMgmtHandler.getInstance().getAppRepositoryDetails(app_id, label_id);
                    if (!appDetailsDO.isEmpty()) {
                        final Row appPackageRow = appDetailsDO.getFirstRow("MdPackageToAppData");
                        currentRelease.put("description", appPackageRow.get("DESCRIPTION"));
                        if (!MDMStringUtils.isEmpty((String)appPackageRow.get("APP_FILE_LOC"))) {
                            final String appLoc = String.valueOf(appPackageRow.get("APP_FILE_LOC"));
                            if (!appLoc.equalsIgnoreCase("Not Available")) {
                                if (!appLoc.startsWith("http")) {
                                    app_file = MDMRestAPIFactoryProvider.getAPIUtil().getDownloadableFileURL(appLoc);
                                }
                                else {
                                    app_file = appLoc;
                                }
                            }
                        }
                        if (!MDMStringUtils.isEmpty((String)appPackageRow.get("DISPLAY_IMAGE_LOC"))) {
                            final String displayImageLoc = String.valueOf(appPackageRow.get("DISPLAY_IMAGE_LOC"));
                            if (!displayImageLoc.equalsIgnoreCase("Not Available")) {
                                if (!displayImageLoc.startsWith("http")) {
                                    fileJSON.put("icon", (Object)MDMRestAPIFactoryProvider.getAPIUtil().getFileURL(displayImageLoc));
                                }
                                else {
                                    fileJSON.put("icon", (Object)displayImageLoc);
                                }
                            }
                        }
                        if (!MDMStringUtils.isEmpty((String)appPackageRow.get("FULL_IMAGE_LOC"))) {
                            final String fullImageLoc = String.valueOf(appPackageRow.get("FULL_IMAGE_LOC"));
                            if (!fullImageLoc.equalsIgnoreCase("Not Available")) {
                                if (!fullImageLoc.startsWith("http")) {
                                    fileJSON.put("full_image_loc", (Object)MDMRestAPIFactoryProvider.getAPIUtil().getFileURL(fullImageLoc));
                                }
                                else {
                                    fileJSON.put("full_image_loc", (Object)fullImageLoc);
                                }
                            }
                        }
                    }
                    fileJSON.put("app_file", (Object)app_file);
                    currentVersion.put("release_label", (Object)currentRelease);
                    currentVersion.put("files", (Object)fileJSON);
                    currentVersion.put("configuration", (Object)this.getAppConfiguration(request));
                    currentVersion.put("permission", (Object)this.getPermissionsAssociatedWithApp(request));
                    appVersions.put((Object)currentVersion);
                }
            }
            else {
                final JSONObject currentVersion2 = new JSONObject();
                final JSONObject currentRelease2 = releaseLabels.getJSONObject(0);
                final Long label_id2 = Long.valueOf(currentRelease2.getString("release_label_id"));
                request.getJSONObject("msg_header").getJSONObject("resource_identifier").put("label_id", (Object)label_id2);
                final JSONObject fileJSON = new JSONObject();
                if (app.has("app_file")) {
                    fileJSON.put("app_file", (Object)app.getString("app_file"));
                }
                if (app.has("icon")) {
                    fileJSON.put("icon", (Object)app.getString("icon"));
                }
                if (app.has("FULL_IMAGE_LOC")) {
                    fileJSON.put("full_image_loc", (Object)app.getString("FULL_IMAGE_LOC"));
                }
                currentRelease2.put("description", (Object)app.getString("description"));
                currentVersion2.put("release_label", (Object)currentRelease2);
                currentVersion2.put("files", (Object)fileJSON);
                currentVersion2.put("configuration", (Object)this.getAppConfiguration(request));
                currentVersion2.put("permission", (Object)this.getPermissionsAssociatedWithApp(request));
                appVersions.put((Object)currentVersion2);
            }
            app.put("version_details", (Object)appVersions);
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.SEVERE, "exception in getDetailedApps", e);
        }
        return app;
    }
    
    private SelectQuery getRepositoryAppsCountQuery(final Criteria criteria) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        query.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        query.addJoin(new Join("MdPackage", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        query.addJoin(new Join("MdPackageToAppData", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        query.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        query.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        query.addJoin(new Join("MdAppGroupDetails", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        query.addJoin(new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 2));
        query.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        query.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        query.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        query.addJoin(AppVersionDBUtil.getInstance().getAppReleaseLabelJoin());
        Column selCol = new Column("MdPackage", "PACKAGE_ID");
        selCol = selCol.distinct();
        selCol = selCol.count();
        query.addSelectColumn(selCol);
        query.setCriteria(criteria);
        return query;
    }
    
    private void closeConnection(final Connection conn, final DataSet ds) {
        try {
            if (ds != null) {
                ds.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.WARNING, "Exception occoured in closeConnection....", ex);
        }
    }
    
    @Deprecated
    public Object getUsersForApp(final JSONObject message) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        try {
            final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(message);
            final Long customerId = APIUtil.getCustomerID(message);
            final Long packageID = APIUtil.getResourceID(message, "app_id");
            final Long approvedReleaseLabel = AppVersionDBUtil.getInstance().getApprovedReleaseLabelForGivePackage(packageID, customerId);
            final DataObject appDetailsDO = MDMAppMgmtHandler.getInstance().getAppRepositoryDetails(packageID, approvedReleaseLabel);
            final Long collectionId = (Long)appDetailsDO.getRow("AppGroupToCollection").get("COLLECTION_ID");
            final HashMap profileMap = MDMUtil.getInstance().getProfileDetailsForCollectionId(collectionId);
            final Long profileId = profileMap.get("PROFILE_ID");
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForMDMResource"));
            query.addJoin(new Join("RecentProfileForMDMResource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForMDMResource", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria notDeletedCriteria = new Criteria(Column.getColumn("RecentProfileForMDMResource", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
            query.setCriteria(profileCriteria.and(notDeletedCriteria));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.addSortColumn(new SortColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), true));
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(DBUtil.getRecordCount("RecentProfileForMDMResource", "RESOURCE_ID", new Criteria(Column.getColumn("RecentProfileForMDMResource", "PROFILE_ID"), (Object)profileId, 0)));
            if (pagingJSON != null) {
                response.put("paging", (Object)pagingJSON);
            }
            query.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
            query.addSortColumn(new SortColumn("ManagedUser", "FIRST_NAME", (boolean)Boolean.TRUE));
            final DataObject packageUserDO = SyMUtil.getPersistence().get(query);
            if (packageUserDO.isEmpty()) {
                final JSONArray result = new JSONArray();
                final JSONObject res = new JSONObject();
                res.put("users", (Object)result);
                return res;
            }
            final JSONArray result = new JSONArray();
            final Iterator<Row> rows = packageUserDO.getRows("RecentProfileForMDMResource");
            while (rows.hasNext()) {
                final Row row = rows.next();
                final JSONObject user = new JSONObject();
                final Long userID = Long.valueOf(String.valueOf(row.get("RESOURCE_ID")));
                final HashMap userMap = ManagedUserHandler.getInstance().getManagedUserDetails(userID);
                user.put("user_id", userMap.get("MANAGED_USER_ID"));
                user.put("user_name", userMap.get("NAME"));
                user.put("user_email", userMap.get("EMAIL_ADDRESS"));
                user.put("android_device_count", ManagedDeviceHandler.getInstance().getManagedDeviceCountForUser(userID, 2, new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0)));
                user.put("ios_device_count", ManagedDeviceHandler.getInstance().getManagedDeviceCountForUser(userID, 2, new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0)));
                user.put("windows_device_count", ManagedDeviceHandler.getInstance().getManagedDeviceCountForUser(userID, 2, new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)3, 0)));
                user.put("is_app_installed", this.getCollectionInstallationStatus((Long)row.get("COLLECTION_ID"), userMap.get("MANAGED_USER_ID")));
                final JSONArray groups = new JSONArray();
                final Criteria cr1 = new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)userID, 0);
                final DataObject groupsDO = SyMUtil.getPersistence().get("CustomGroupMemberRel", cr1);
                final Iterator<Row> gRows = groupsDO.getRows("CustomGroupMemberRel");
                while (gRows.hasNext()) {
                    final Long groupId = Long.valueOf(String.valueOf(gRows.next().get("GROUP_RESOURCE_ID")));
                    final Criteria cr2 = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupId, 0);
                    final Criteria cr3 = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
                    final DataObject groupDO = SyMUtil.getPersistence().get("RecentProfileForGroup", cr2.and(cr3));
                    if (!groupDO.isEmpty()) {
                        groups.put((Object)groupId);
                    }
                }
                user.put("associated_user_groups", (Object)groups);
                result.put((Object)user);
            }
            response.put("users", (Object)result);
            return response;
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.SEVERE, "exception in getApps", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private boolean getCollectionInstallationStatus(final Long collectionId, final Long userId) {
        try {
            final List devices = ManagedDeviceHandler.getInstance().getManagedDeviceIdForUserId(userId, null, false);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResource"));
            selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "INSTALLED_APP_ID" }, new String[] { "APP_ID" }, 2));
            final Criteria collectionCriteria = new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria deviceCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)devices.toArray(), 8);
            Column countCol = Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID").count();
            countCol = countCol.distinct();
            selectQuery.setCriteria(collectionCriteria.and(deviceCriteria));
            selectQuery.addSelectColumn(countCol);
            final int count = DBUtil.getRecordCount(selectQuery);
            return count != 0;
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.SEVERE, "error in getCollectionInstallationStatus()", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private Properties getDefaultBusinessStoreForIOSVppApps(final Set<Long> packageIDList, final Long customerID) throws DataAccessException, QueryConstructionException {
        final List packageArrayList = new ArrayList();
        packageArrayList.addAll(packageIDList);
        final List<List> splitPackageList = MDMUtil.getInstance().splitListIntoSubLists(packageArrayList, 1000);
        final Properties appToBusinessStoreProps = new Properties();
        for (final List tempList : splitPackageList) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppAsset", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedBusinessStore", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria packageCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)tempList.toArray(), 8);
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
            selectQuery.setCriteria(customerCriteria.and(packageCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iter = dataObject.getRows("MdPackageToAppGroup");
                final Row businessStoreRow = dataObject.getFirstRow("ManagedBusinessStore");
                final Long businessStoreID = (Long)businessStoreRow.get("BUSINESSSTORE_ID");
                while (iter.hasNext()) {
                    final Row packageRow = iter.next();
                    final Long packageID = (Long)packageRow.get("PACKAGE_ID");
                    ((Hashtable<Long, Long>)appToBusinessStoreProps).put(packageID, businessStoreID);
                }
            }
        }
        return appToBusinessStoreProps;
    }
    
    public void associateAppsToDevices(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "associate-failed";
        try {
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            final Long businessStoreID = MDBusinessStoreUtil.getBusinessStoreIDFromAPIBody(message);
            List<Long> resourceList;
            if (deviceId != null && deviceId != -1L) {
                resourceList = new ArrayList<Long>();
                resourceList.add(deviceId);
            }
            else {
                resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("device_ids"));
            }
            secLog.put((Object)"DEVICE_IDs", (Object)resourceList);
            final HashMap<Integer, ArrayList> platformDeviceMap = new DeviceFacade().validateIfDevicesExists(resourceList, APIUtil.getCustomerID(message));
            JSONArray appDetails = new JSONArray();
            if (platformDeviceMap.size() > 1) {
                throw new APIHTTPException("COM0015", new Object[] { "Devices are not with the unique platform type" });
            }
            final Integer platformType = platformDeviceMap.keySet().iterator().next();
            final Long customerID = APIUtil.getCustomerID(message);
            Properties pkgToBusinessStoreProps = new Properties();
            final Long packageId = APIUtil.getResourceID(message, "app_id");
            Map<Long, Set<Long>> releaseLabelToPackageId = new HashMap<Long, Set<Long>>();
            if (packageId != -1L) {
                final Set<Long> packageIds = new HashSet<Long>();
                Long releaseLabelId = APIUtil.getResourceID(message, "label_id");
                if (releaseLabelId.equals(-1L)) {
                    releaseLabelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(APIUtil.getCustomerID(message));
                }
                packageIds.add(packageId);
                releaseLabelToPackageId.put(releaseLabelId, packageIds);
                final JSONObject appDetail = new JSONObject();
                appDetail.put("app_id", (Object)packageId);
                if (businessStoreID != null && businessStoreID != -1L) {
                    appDetail.put("businessstore_id", (Object)businessStoreID);
                }
                appDetails.put((Object)appDetail);
            }
            else {
                appDetails = message.getJSONObject("msg_body").getJSONArray("app_details");
                releaseLabelToPackageId = this.convertAppDetailsArrayToHashMap(appDetails);
            }
            final boolean isAppUpgrade = message.getJSONObject("msg_body").optBoolean("isAppUpgrade");
            secLog.put((Object)"LABEL_TO_PACKAGE_IDs", (Object)releaseLabelToPackageId);
            pkgToBusinessStoreProps = this.checkAndSetBusinessStoreIDsInRequest(appDetails, customerID);
            final Map profileCollectionMap = this.validateAndGetAppDetails(releaseLabelToPackageId, APIUtil.getCustomerID(message), platformType);
            final Properties profileToBusinessStore = this.getProfileToBusinessStoreMap(pkgToBusinessStoreProps, customerID);
            AppFacade.logger.log(Level.INFO, "associate the app to device, device ids:{0} and releaseLabel->AppIdListMap:{1}", new Object[] { resourceList, releaseLabelToPackageId });
            final Collection<Set<Long>> appSets = releaseLabelToPackageId.values();
            final List<Long> packagesCheckForTrash = new ArrayList<Long>();
            for (final Set<Long> apps : appSets) {
                packagesCheckForTrash.addAll(apps);
            }
            new AppTrashModeHandler().validateIfPackageInTrash(packagesCheckForTrash);
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("customerId", APIUtil.getCustomerID(message));
            if (message.has("msg_body")) {
                this.setDeploymentConfigProperties(properties, message.getJSONObject("msg_body"));
            }
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, Map>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 120);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
            ((Hashtable<String, Boolean>)properties).put("isGroup", Boolean.FALSE);
            ((Hashtable<String, Boolean>)properties).put("isAppUpgrade", isAppUpgrade);
            if (Boolean.parseBoolean(String.valueOf(((Hashtable<K, Object>)properties).get("isAppDowngrade")))) {
                secLog.put((Object)"IS_DOWNGRADE", (Object)true);
            }
            if (!profileToBusinessStore.isEmpty()) {
                ((Hashtable<String, Properties>)properties).put("profileToBusinessStore", profileToBusinessStore);
            }
            this.updateDepPolicyAndAssociateAppToDevices(properties);
            remarks = "associate-success";
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ASSOCIATE_APP", secLog);
        }
    }
    
    public void updateDepPolicyAndAssociateAppToDevices(final Properties properties) {
        try {
            new DeplymentConfigHandler().updateDeploymentSettingsForApp(properties);
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.SEVERE, "Exception while updating deployment config", e);
        }
        ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
    }
    
    public void downgradeAppForDevices(final JSONObject requestJSON) throws APIHTTPException {
        try {
            AppFacade.logger.log(Level.INFO, "---------APP DOWNGRADE request fro device--------- {0} ", new Object[] { requestJSON });
            final Long packageId = APIUtil.getResourceID(requestJSON, "app_id");
            final Integer platformType = AppsUtil.getInstance().getPlatformTypeFromPackageID(packageId);
            if (platformType != 1) {
                throw new APIHTTPException("COM0015", new Object[0]);
            }
            JSONObject msgBody;
            if (requestJSON.has("msg_body")) {
                msgBody = requestJSON.getJSONObject("msg_body");
            }
            else {
                msgBody = new JSONObject();
            }
            msgBody.put("is_app_downgrade", (Object)Boolean.TRUE);
            requestJSON.put("msg_body", (Object)msgBody);
            this.associateAppsToDevices(requestJSON);
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in downgradeAppForDevices", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Properties validateAppToBusinessStoreProps(final Properties platformToPackageMap, final Properties pkgToBusinessStore, final Long customerID) throws Exception {
        final Properties appToBusinessStoreProp = new Properties();
        if (!pkgToBusinessStore.isEmpty()) {
            for (final int platformType : ((Hashtable<Object, V>)platformToPackageMap).keySet()) {
                final StoreInterface storeInterface = MDBusinessStoreUtil.getInstance(platformType, customerID);
                final List packageList = ((Hashtable<K, List>)platformToPackageMap).get(platformType);
                storeInterface.validateAppToBusinessStoreProps(packageList, pkgToBusinessStore, appToBusinessStoreProp);
            }
        }
        return appToBusinessStoreProp;
    }
    
    public void validateBusinessStoreIDs(final List businessStoreList, final Long customerID) {
        try {
            final List tempBusinessIDs = new ArrayList(businessStoreList);
            final List availableBusinessIDs = new ArrayList();
            final SelectQuery businessStoreQuery = MDBusinessStoreUtil.getBusinessStoreQuery();
            businessStoreQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreList.toArray(), 8);
            businessStoreQuery.setCriteria(businessStoreCriteria.and(customerCriteria));
            final DataObject businessDo = MDMUtil.getPersistence().get(businessStoreQuery);
            if (!businessDo.isEmpty()) {
                final Iterator iter = businessDo.getRows("ManagedBusinessStore");
                final Row bsRow = iter.next();
                final Long businessStoreID = (Long)bsRow.get("BUSINESSSTORE_ID");
                availableBusinessIDs.add(businessStoreID);
            }
            tempBusinessIDs.removeAll(availableBusinessIDs);
            if (!tempBusinessIDs.isEmpty()) {
                AppFacade.logger.log(Level.SEVERE, "These businessStoreIDs {0} doesn't belong to the customer {1}", new Object[] { tempBusinessIDs, customerID });
                throw new APIHTTPException("COM0008", new Object[] { "BusinessStoreIds: " + tempBusinessIDs.toString() });
            }
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.SEVERE, "Exception in validateBusinessStoreIDs", e);
        }
    }
    
    public Properties getProfileToBusinessStoreMap(final Properties pkgToBusinessStore, final Long customerID) {
        final List packageIDs = new ArrayList();
        packageIDs.addAll(pkgToBusinessStore.keySet());
        final List businessStoreIDs = new ArrayList();
        final Properties profileToBusinessStoreMap = new Properties();
        try {
            final Properties packageToProfileMap = ProfileUtil.getInstance().getPackageToProfileMap(packageIDs);
            for (int i = 0; i < packageIDs.size(); ++i) {
                final Long packageID = packageIDs.get(i);
                final Long profileID = ((Hashtable<K, Long>)packageToProfileMap).get(packageID);
                final Long businessStoreID = ((Hashtable<K, Long>)pkgToBusinessStore).get(packageID);
                ((Hashtable<Long, Long>)profileToBusinessStoreMap).put(profileID, businessStoreID);
                if (!businessStoreIDs.contains(businessStoreID)) {
                    businessStoreIDs.add(businessStoreID);
                }
            }
            this.validateBusinessStoreIDs(businessStoreIDs, customerID);
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.SEVERE, "Exception in getProfileToBusinessStoreMap", e);
        }
        return profileToBusinessStoreMap;
    }
    
    public Properties checkAndSetBusinessStoreIDsInRequest(final JSONArray appDetails, final long customerID) throws Exception {
        final Properties appToBusinessStoreProps = new Properties();
        final List packageIDs = new ArrayList();
        for (int i = 0; i < appDetails.length(); ++i) {
            if (!appDetails.getJSONObject(i).has("businessstore_id")) {
                packageIDs.add(appDetails.getJSONObject(i).getLong("app_id"));
            }
            else {
                ((Hashtable<Long, Long>)appToBusinessStoreProps).put(appDetails.getJSONObject(i).getLong("app_id"), appDetails.getJSONObject(i).getLong("businessstore_id"));
            }
        }
        if (!packageIDs.isEmpty()) {
            this.validateIfStoreAppsHasStoreID(packageIDs, customerID, appToBusinessStoreProps);
        }
        return appToBusinessStoreProps;
    }
    
    private void validateIfStoreAppsHasStoreID(final List packageIDList, final Long customerID, final Properties appToBusinessStoreProps) throws Exception {
        final List<List> splitPackageList = MDMUtil.getInstance().splitListIntoSubLists(packageIDList, 1000);
        final List pkgIDsWithOutBusinessStore = new ArrayList();
        for (final List tempList : splitPackageList) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria packageCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)tempList.toArray(), 8);
            final Criteria platformCriteria = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria purchasedFromPortalCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)Boolean.TRUE, 0);
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
            selectQuery.setCriteria(packageCriteria.and(platformCriteria).and(purchasedFromPortalCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iter = dataObject.getRows("MdPackageToAppGroup");
                while (iter.hasNext()) {
                    final Row packageRow = iter.next();
                    final Long packageID = (Long)packageRow.get("PACKAGE_ID");
                    pkgIDsWithOutBusinessStore.add(packageID);
                }
            }
            if (!pkgIDsWithOutBusinessStore.isEmpty()) {
                final List businessStoreIDs = MDBusinessStoreUtil.getBusinessStoreIDs(customerID, BusinessStoreSyncConstants.BS_SERVICE_VPP);
                if (businessStoreIDs != null && businessStoreIDs.size() == 1) {
                    final List tempPkgIDs = new ArrayList(pkgIDsWithOutBusinessStore);
                    for (final Long packageID2 : tempPkgIDs) {
                        ((Hashtable<Long, Long>)appToBusinessStoreProps).put(packageID2, businessStoreIDs.get(0));
                        pkgIDsWithOutBusinessStore.remove(packageID2);
                    }
                }
                if (!pkgIDsWithOutBusinessStore.isEmpty()) {
                    throw new Exception("BusinessStore apps found in request without BusinessStoreID : PkgIDs " + pkgIDsWithOutBusinessStore);
                }
                continue;
            }
        }
    }
    
    public void disassociateAppsToDevices(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "dissociate-failed";
        try {
            final Long packageId = APIUtil.getResourceID(message, "app_id");
            List<Long> packageIds;
            if (packageId != -1L) {
                packageIds = new ArrayList<Long>();
                packageIds.add(packageId);
            }
            else {
                packageIds = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("app_ids"));
            }
            secLog.put((Object)"PACKAGE_IDs", (Object)packageIds);
            Long deviceId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "device_id", (Long)null);
            if (deviceId == 0L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            List<Long> resourceList;
            if (deviceId != null && deviceId != 0L) {
                resourceList = new ArrayList<Long>();
                resourceList.add(deviceId);
            }
            else {
                resourceList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("device_ids"));
            }
            secLog.put((Object)"DEVICE_IDs", (Object)resourceList);
            AppFacade.logger.log(Level.INFO, "dissassociate the app from device device ids:{0} and app ids:{1}", new Object[] { resourceList, packageIds });
            new DeviceFacade().validateIfDevicesExists(resourceList, APIUtil.getCustomerID(message));
            final Map<Long, Long> profileCollectionMap = this.validateAndAppDetailsForDevice(packageIds, resourceList, APIUtil.getCustomerID(message));
            final Long customerID = APIUtil.getCustomerID(message);
            final Properties properties = new Properties();
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, Long>)properties).put("customerId", customerID);
            ((Hashtable<String, Map<Long, Long>>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 120);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
            ProfileAssociateHandler.getInstance().disAssociateCollectionForResource(properties);
            remarks = "dissociate-success";
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISSOCIATE_APP", secLog);
        }
    }
    
    public void associateAppsToManagedUsers(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "associate-failed";
        try {
            Long businessStoreID = null;
            JSONArray appDetails = new JSONArray();
            Properties pkgToBusinessStoreProps = new Properties();
            final Long customerID = APIUtil.getCustomerID(message);
            final Long packageId = APIUtil.getResourceID(message, "app_id");
            List<Long> packageIds;
            if (packageId != -1L) {
                this.validateIfAppFound(packageId, customerID);
                packageIds = new ArrayList<Long>();
                packageIds.add(packageId);
                businessStoreID = APIUtil.getResourceID(message, "businessstore_id");
                final JSONObject appDetail = new JSONObject();
                appDetail.put("app_id", (Object)packageId);
                if (businessStoreID != null && businessStoreID != -1L) {
                    appDetail.put("businessstore_id", (Object)businessStoreID);
                }
                appDetails.put((Object)appDetail);
            }
            else {
                packageIds = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("app_ids"));
                this.validateIfAppsFound(packageIds, APIUtil.getCustomerID(message));
                appDetails = this.getAppDetailsArray(message, packageIds);
            }
            secLog.put((Object)"PACKAGE_IDs", (Object)packageIds);
            pkgToBusinessStoreProps = this.checkAndSetBusinessStoreIDsInRequest(appDetails, customerID);
            final Properties profileToBusinessStore = this.getProfileToBusinessStoreMap(pkgToBusinessStoreProps, customerID);
            final Long userId = APIUtil.getResourceID(message, "user_id");
            List<Long> userList;
            if (userId != -1L) {
                userList = new ArrayList<Long>();
                userList.add(userId);
            }
            else {
                userList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("user_ids"));
            }
            secLog.put((Object)"USER_IDs", (Object)userList);
            final List<Long> invalidUserIds = new APIUtil().getInvalidManagedUserIds(userList);
            if (invalidUserIds.size() != 0) {
                throw new APIHTTPException("COM0008", new Object[] { "user_ids - " + APIUtil.getCommaSeperatedString(invalidUserIds) });
            }
            AppFacade.logger.log(Level.INFO, "associate app to the user, user ids: {0} and app ids:{1}", new Object[] { userList, packageIds });
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("customerId", APIUtil.getCustomerID(message));
            if (message.has("msg_body")) {
                this.setDeploymentConfigProperties(properties, message.getJSONObject("msg_body"));
            }
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            final HashMap<Long, Long> profileCollectionMap = this.getProfileCollectionMapForPackageIds(packageIds);
            ((Hashtable<String, HashMap<Long, Long>>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 2);
            ((Hashtable<String, Boolean>)properties).put("isGroup", false);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", userList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
            ((Hashtable<String, Integer>)properties).put("resourceType", 2);
            ((Hashtable<String, Properties>)properties).put("profileToBusinessStore", profileToBusinessStore);
            ((Hashtable<String, List<Long>>)properties).put("configSourceList", userList);
            try {
                new DeplymentConfigHandler().updateDeploymentSettingsForApp(properties);
            }
            catch (final Exception e) {
                AppFacade.logger.log(Level.SEVERE, "Exception while updating deployment config", e);
            }
            com.me.mdm.server.config.ProfileAssociateHandler.getInstance().associateCollectionToMDMResource(properties);
            remarks = "associate-success";
        }
        catch (final Exception e2) {
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ASSOCIATE_APP", secLog);
        }
    }
    
    private JSONArray getAppDetailsArray(final JSONObject message, final List packageIDs) {
        JSONArray appDetails = new JSONArray();
        if (message.getJSONObject("msg_body").has("app_details")) {
            appDetails = message.getJSONObject("msg_body").getJSONArray("app_details");
        }
        else if (packageIDs != null) {
            for (final Long packageID : packageIDs) {
                final JSONObject appDetail = new JSONObject();
                appDetail.put("app_id", (Object)packageID);
                appDetails.put((Object)appDetail);
            }
        }
        return appDetails;
    }
    
    public void disassociateAppsToManagedUsers(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "dissociate-failed";
        try {
            final Long packageId = APIUtil.getResourceID(message, "app_id");
            List<Long> packageIds;
            if (packageId != -1L) {
                this.validateIfAppFound(packageId, APIUtil.getCustomerID(message));
                packageIds = new ArrayList<Long>();
                packageIds.add(packageId);
            }
            else {
                packageIds = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("app_ids"));
                this.validateIfAppsFound(packageIds, APIUtil.getCustomerID(message));
            }
            secLog.put((Object)"PACKAGE_IDs", (Object)packageIds);
            final Long userId = APIUtil.getResourceID(message, "user_id");
            List<Long> userList;
            if (userId != -1L) {
                userList = new ArrayList<Long>();
                userList.add(userId);
            }
            else {
                userList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("user_ids"));
            }
            secLog.put((Object)"USER_IDs", (Object)userList);
            final List<Long> invalidUserIds = new APIUtil().getInvalidManagedUserIds(userList);
            if (invalidUserIds.size() != 0) {
                throw new APIHTTPException("COM0008", new Object[] { "user_ids - " + APIUtil.getCommaSeperatedString(invalidUserIds) });
            }
            AppFacade.logger.log(Level.INFO, "disassociate app to the user, user ids: {0} and app ids:{1}", new Object[] { userList, packageIds });
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("customerId", APIUtil.getCustomerID(message));
            if (message.has("msg_body")) {
                this.setDeploymentConfigProperties(properties, message.getJSONObject("msg_body"));
            }
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            final HashMap<Long, Long> profileCollectionMap = this.getProfileCollectionMapForPackageIds(packageIds);
            ((Hashtable<String, HashMap<Long, Long>>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 2);
            ((Hashtable<String, Boolean>)properties).put("isGroup", false);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", userList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
            ((Hashtable<String, Integer>)properties).put("resourceType", 2);
            ((Hashtable<String, List<Long>>)properties).put("configSourceList", userList);
            try {
                new DeplymentConfigHandler().updateDeploymentSettingsForApp(properties);
            }
            catch (final Exception e) {
                AppFacade.logger.log(Level.SEVERE, "Exception while updating deployment config", e);
            }
            com.me.mdm.server.config.ProfileAssociateHandler.getInstance().disassociateCollectionFromMDMResource(properties);
            remarks = "dissociate-success";
        }
        catch (final Exception e2) {
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISSOCIATE_APP", secLog);
        }
    }
    
    public void associateAppsToGroups(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "associate-failed";
        try {
            final Long packageId = APIUtil.getResourceID(message, "app_id");
            final Long businessStoreID = MDBusinessStoreUtil.getBusinessStoreIDFromAPIBody(message);
            final Long customerID = APIUtil.getCustomerID(message);
            Properties pkgToBusinessStoreProps = new Properties();
            Map<Long, Set<Long>> releaseLabelToPackageId = new HashMap<Long, Set<Long>>();
            JSONArray appDetails = new JSONArray();
            if (packageId != -1L) {
                final Set<Long> packageIds = new HashSet<Long>();
                Long releaseLabelId = APIUtil.getResourceID(message, "label_id");
                if (releaseLabelId.equals(-1L)) {
                    releaseLabelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(APIUtil.getCustomerID(message));
                }
                packageIds.add(packageId);
                releaseLabelToPackageId.put(releaseLabelId, packageIds);
                final JSONObject appDetail = new JSONObject();
                appDetail.put("app_id", (Object)packageId);
                if (businessStoreID != null && businessStoreID != -1L) {
                    appDetail.put("businessstore_id", (Object)businessStoreID);
                }
                appDetails.put((Object)appDetail);
            }
            else {
                appDetails = message.getJSONObject("msg_body").getJSONArray("app_details");
                releaseLabelToPackageId = this.convertAppDetailsArrayToHashMap(appDetails);
            }
            secLog.put((Object)"LABEL_TO_PACKAGE_IDs", (Object)releaseLabelToPackageId);
            final Map profileCollectionMap = this.validateAndGetAppDetails(releaseLabelToPackageId, APIUtil.getCustomerID(message), null);
            pkgToBusinessStoreProps = this.checkAndSetBusinessStoreIDsInRequest(appDetails, customerID);
            final Properties profileToBusinessStore = this.getProfileToBusinessStoreMap(pkgToBusinessStoreProps, customerID);
            final Long groupId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "group_id", (Long)null);
            List<Long> groupList;
            if (groupId != 0L) {
                groupList = new ArrayList<Long>();
                groupList.add(groupId);
            }
            else {
                groupList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("group_ids"));
            }
            secLog.put((Object)"GROUP_IDs", (Object)groupList);
            new GroupFacade().validateGroupsIfExists(groupList, APIUtil.getCustomerID(message));
            final Collection<Set<Long>> appSets = releaseLabelToPackageId.values();
            final List<Long> packagesCheckForTrash = new ArrayList<Long>();
            for (final Set<Long> apps : appSets) {
                packagesCheckForTrash.addAll(apps);
            }
            new AppTrashModeHandler().validateIfPackageInTrash(packagesCheckForTrash);
            AppFacade.logger.log(Level.INFO, "associate app to the group, group ids: {0} and releaseLabel->AppIdListMap:{1}", new Object[] { groupList, releaseLabelToPackageId });
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("customerId", customerID);
            if (message.has("msg_body")) {
                this.setDeploymentConfigProperties(properties, message.getJSONObject("msg_body"));
            }
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, Map>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
            ((Hashtable<String, Boolean>)properties).put("isGroup", true);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", groupList);
            ((Hashtable<String, Integer>)properties).put("groupType", 6);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ((Hashtable<String, Properties>)properties).put("profileToBusinessStore", profileToBusinessStore);
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
            if (Boolean.parseBoolean(String.valueOf(((Hashtable<K, Object>)properties).get("isAppDowngrade")))) {
                secLog.put((Object)"IS_DOWNGRADE", (Object)true);
            }
            try {
                new DeplymentConfigHandler().updateDeploymentSettingsForApp(properties);
            }
            catch (final Exception e) {
                AppFacade.logger.log(Level.SEVERE, "Exception while updating deployment config", e);
            }
            ProfileAssociateHandler.getInstance().associateCollectionForGroup(properties);
            remarks = "associate-success";
        }
        catch (final Exception e2) {
            AppFacade.logger.log(Level.SEVERE, "Exception while associating the profile to devices", e2);
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ASSOCIATE_APP", secLog);
        }
    }
    
    public void downgradeAppForGroups(final JSONObject message) throws APIHTTPException {
        try {
            AppFacade.logger.log(Level.INFO, "---------APP DOWNGRADE request fro group--------- {0} ", new Object[] { message });
            final Long packageID = APIUtil.getResourceID(message, "app_id");
            final Integer platform = AppsUtil.getInstance().getPlatformTypeFromPackageID(packageID);
            if (platform != 1) {
                throw new APIHTTPException("COM0015", new Object[0]);
            }
            JSONObject msgBody;
            if (message.has("msg_body")) {
                msgBody = message.getJSONObject("msg_body");
            }
            else {
                msgBody = new JSONObject();
            }
            msgBody.put("is_app_downgrade", (Object)Boolean.TRUE);
            message.put("msg_body", (Object)msgBody);
            this.associateAppsToGroups(message);
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in downgradeAppsForGroup", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void setDeploymentConfigProperties(final Properties properties, final JSONObject jsonObject) {
        final Long customerID = ((Hashtable<K, Long>)properties).get("customerId");
        final Properties appSettings = AppsUtil.getInstance().getAppSettings(customerID);
        final Boolean silentInstall = jsonObject.optBoolean("silent_install", (boolean)((Hashtable<K, Boolean>)appSettings).get("isSilentInstall"));
        ((Hashtable<String, Boolean>)properties).put("isSilentInstall", silentInstall);
        ((Hashtable<String, Boolean>)properties).put("isNotify", jsonObject.optBoolean("notify_user_via_email", (boolean)((Hashtable<K, Boolean>)appSettings).get("isNotify")));
        ((Hashtable<String, Boolean>)properties).put("sendEnrollmentRequest", jsonObject.optBoolean("invite_user", false));
        ((Hashtable<String, Boolean>)properties).put("doNotUninstall", jsonObject.optBoolean("do_not_uninstall", false));
        ((Hashtable<String, Boolean>)properties).put("isAppDowngrade", jsonObject.optBoolean("is_app_downgrade", (boolean)Boolean.FALSE));
        ((Hashtable<String, Boolean>)properties).put("doNotUninstall", jsonObject.optBoolean("do_not_uninstall", (boolean)silentInstall));
        ((Hashtable<String, Boolean>)properties).put("forceUpdate", jsonObject.optBoolean("force_update", (boolean)Boolean.FALSE));
    }
    
    public void disassociateAppsToGroups(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "dissociate-failed";
        try {
            final Long packageId = APIUtil.getResourceID(message, "app_id");
            List<Long> packageIds;
            if (packageId != -1L) {
                packageIds = new ArrayList<Long>();
                packageIds.add(packageId);
            }
            else {
                packageIds = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("app_ids"));
            }
            secLog.put((Object)"PACKAGE_IDs", (Object)packageIds);
            final Long groupId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "group_id", (Long)null);
            List<Long> groupList;
            if (groupId != 0L) {
                groupList = new ArrayList<Long>();
                groupList.add(groupId);
            }
            else {
                groupList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("group_ids"));
            }
            secLog.put((Object)"GROUP_IDs", (Object)groupList);
            new GroupFacade().validateGroupsIfExists(groupList, APIUtil.getCustomerID(message));
            final Map profileCollectionMap = this.validateAndAppDetailsForGroup(packageIds, groupList, APIUtil.getCustomerID(message));
            AppFacade.logger.log(Level.INFO, "disassociate app to the group, group ids: {0} and app ids:{1}", new Object[] { groupList, packageIds });
            final Long customerID = APIUtil.getCustomerID(message);
            final Properties properties = new Properties();
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, Long>)properties).put("customerId", customerID);
            ((Hashtable<String, Map>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
            ((Hashtable<String, Boolean>)properties).put("isGroup", true);
            ((Hashtable<String, Integer>)properties).put("groupType", 6);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", groupList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
            try {
                new DeplymentConfigHandler().updateDeploymentSettingsForApp(properties);
            }
            catch (final Exception e) {
                AppFacade.logger.log(Level.SEVERE, "Exception while updating deployment config", e);
            }
            ProfileAssociateHandler.getInstance().disAssociateCollectionForGroup(properties);
            remarks = "dissociate-success";
        }
        catch (final Exception e2) {
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISSOCIATE_APP", secLog);
        }
    }
    
    public void associateAppsToUserGroups(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "associate-failed";
        try {
            final Long packageId = APIUtil.getResourceID(message, "app_id");
            List<Long> packageIds;
            if (packageId != -1L) {
                this.validateIfAppFound(packageId, APIUtil.getCustomerID(message));
                packageIds = new ArrayList<Long>();
                packageIds.add(packageId);
            }
            else {
                packageIds = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("app_ids"));
                this.validateIfAppsFound(packageIds, APIUtil.getCustomerID(message));
            }
            secLog.put((Object)"PACKAGE_IDs", (Object)packageIds);
            final Long groupId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_group_id", (Long)null);
            List<Long> groupList;
            if (groupId != 0L) {
                groupList = new ArrayList<Long>();
                groupList.add(groupId);
            }
            else {
                groupList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("user_group_ids"));
            }
            secLog.put((Object)"USER_GROUP_IDs", (Object)groupList);
            new GroupFacade().validateGroupsIfExists(groupList, APIUtil.getCustomerID(message));
            AppFacade.logger.log(Level.INFO, "associate app to the user group, group ids: {0} and app ids:{1}", new Object[] { groupList, packageIds });
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("customerId", APIUtil.getCustomerID(message));
            if (message.has("msg_body")) {
                this.setDeploymentConfigProperties(properties, message.getJSONObject("msg_body"));
            }
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            final HashMap<Long, Long> profileCollectionMap = this.getProfileCollectionMapForPackageIds(packageIds);
            ((Hashtable<String, HashMap<Long, Long>>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 101);
            ((Hashtable<String, Integer>)properties).put("groupType", 7);
            ((Hashtable<String, Boolean>)properties).put("isGroup", true);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", groupList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
            try {
                new DeplymentConfigHandler().updateDeploymentSettingsForApp(properties);
            }
            catch (final Exception e) {
                AppFacade.logger.log(Level.SEVERE, "Exception while updating deployment config", e);
            }
            ProfileAssociateHandler.getInstance().associateCollectionForGroup(properties);
            remarks = "associate-success";
        }
        catch (final Exception e2) {
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "ASSOCIATE_APP", secLog);
        }
    }
    
    public void disassociateAppsToUserGroups(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "dissociate-failed";
        try {
            final Long packageId = APIUtil.getResourceID(message, "app_id");
            List<Long> packageIds;
            if (packageId != -1L) {
                this.validateIfAppFound(packageId, APIUtil.getCustomerID(message));
                packageIds = new ArrayList<Long>();
                packageIds.add(packageId);
            }
            else {
                packageIds = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("app_ids"));
                this.validateIfAppsFound(packageIds, APIUtil.getCustomerID(message));
            }
            secLog.put((Object)"PACKAGE_IDs", (Object)packageIds);
            final Long groupId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "user_group_id", (Long)null);
            List<Long> groupList;
            if (groupId != 0L) {
                groupList = new ArrayList<Long>();
                groupList.add(groupId);
            }
            else if (message.getJSONObject("msg_header").getJSONObject("filters").has("user_group_ids")) {
                groupList = new ArrayList<Long>();
                final String temp = String.valueOf(message.getJSONObject("msg_header").getJSONObject("filters").get("user_group_ids"));
                final String[] split;
                final String[] ids = split = temp.split(",");
                for (final String id : split) {
                    groupList.add(Long.valueOf(id));
                }
            }
            else {
                groupList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("user_group_ids"));
            }
            secLog.put((Object)"USER_GROUP_IDs", (Object)groupList);
            new GroupFacade().validateGroupsIfExists(groupList, APIUtil.getCustomerID(message));
            AppFacade.logger.log(Level.INFO, "disassociate app to the user group, group ids: {0} and app ids:{1}", new Object[] { groupList, packageIds });
            final Properties properties = new Properties();
            ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
            ((Hashtable<String, Long>)properties).put("customerId", APIUtil.getCustomerID(message));
            final HashMap<Long, Long> profileCollectionMap = this.getProfileCollectionMapForPackageIds(packageIds);
            ((Hashtable<String, HashMap<Long, Long>>)properties).put("profileCollectionMap", profileCollectionMap);
            ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
            ((Hashtable<String, Integer>)properties).put("profileOriginInt", 101);
            ((Hashtable<String, Boolean>)properties).put("isGroup", true);
            ((Hashtable<String, Integer>)properties).put("groupType", 7);
            ((Hashtable<String, List<Long>>)properties).put("resourceList", groupList);
            ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
            ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
            try {
                new DeplymentConfigHandler().updateDeploymentSettingsForApp(properties);
            }
            catch (final Exception e) {
                AppFacade.logger.log(Level.SEVERE, "Exception while updating deployment config", e);
            }
            ProfileAssociateHandler.getInstance().disAssociateCollectionForGroup(properties);
            remarks = "dissociate-success";
        }
        catch (final Exception e2) {
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            MDMOneLineLogger.log(Level.INFO, "DISSOCIATE_APP", secLog);
        }
    }
    
    @Deprecated
    private HashMap getProfileCollectionMapForPackageIds(final List<Long> packageIds) {
        final HashMap<Long, Long> profileCollectionMap = new HashMap<Long, Long>();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        query.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        query.addJoin(new Join("MdPackageToAppGroup", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        query.addJoin(new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        query.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        query.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        query.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        query.addJoin(new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        query.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_ID"));
        query.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        query.setCriteria(new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageIds.toArray(new Long[packageIds.size()]), 8));
        query.setCriteria(query.getCriteria().and(AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria()));
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                profileCollectionMap.put((Long)ds.getValue("PROFILE_ID"), (Long)ds.getValue("COLLECTION_ID"));
            }
            ds.close();
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final SQLException ex2) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        return profileCollectionMap;
    }
    
    public void unPublish(final JSONObject message) throws APIHTTPException {
        try {
            final Long packageId = APIUtil.getResourceID(message, "app_id");
            List<Long> packageIdList;
            if (packageId != -1L) {
                this.validateIfAppFound(packageId, APIUtil.getCustomerID(message));
                packageIdList = new ArrayList<Long>();
                packageIdList.add(packageId);
            }
            else {
                packageIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("app_ids"));
                this.validateIfAppsFound(packageIdList, APIUtil.getCustomerID(message));
            }
            for (final Long pId : packageIdList) {
                final List<Long> packageIds = new ArrayList<Long>();
                packageIds.add(pId);
                final HashMap<Long, Long> profileCollectionMap = this.getProfileCollectionMapForPackageIds(packageIds);
                List<Long> resourceList = this.getGroupsAppAssociatedTo(profileCollectionMap.keySet());
                final Properties properties = new Properties();
                ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
                ((Hashtable<String, Long>)properties).put("customerId", APIUtil.getCustomerID(message));
                ((Hashtable<String, HashMap<Long, Long>>)properties).put("profileCollectionMap", profileCollectionMap);
                ((Hashtable<String, Integer>)properties).put("profileOriginInt", 101);
                ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
                ((Hashtable<String, Boolean>)properties).put("isGroup", true);
                ((Hashtable<String, Integer>)properties).put("groupType", 6);
                ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
                ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
                if (resourceList != null) {
                    ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
                    ProfileAssociateHandler.getInstance().disAssociateCollectionForGroup(properties);
                    properties.remove("resourceList");
                    ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
                    ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
                }
                resourceList = this.getUserGroupAppAssociatedTo(profileCollectionMap.keySet());
                if (resourceList != null) {
                    ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
                    ((Hashtable<String, Integer>)properties).put("groupType", 7);
                    ProfileAssociateHandler.getInstance().disAssociateCollectionForGroup(properties);
                    properties.remove("resourceList");
                    ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
                    ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
                }
                properties.remove("groupType");
                ((Hashtable<String, Integer>)properties).put("profileOriginInt", 2);
                ((Hashtable<String, Boolean>)properties).put("isGroup", false);
                resourceList = this.getUsersAppAssociatedTo(profileCollectionMap.keySet());
                if (resourceList != null) {
                    ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
                    ((Hashtable<String, Integer>)properties).put("resourceType", 2);
                    ((Hashtable<String, Integer>)properties).put("profileOriginInt", 2);
                    ((Hashtable<String, List<Long>>)properties).put("configSourceList", resourceList);
                    com.me.mdm.server.config.ProfileAssociateHandler.getInstance().disassociateCollectionFromMDMResource(properties);
                    ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(APIUtil.getLoginID(message)));
                    ((Hashtable<String, Long>)properties).put("loggedOnUser", APIUtil.getUserID(message));
                }
                properties.remove("configSourceList");
                resourceList = this.getDevicesAppAssociatedTo(profileCollectionMap.keySet());
                if (resourceList != null) {
                    ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
                    ((Hashtable<String, Integer>)properties).put("profileOriginInt", 120);
                    ((Hashtable<String, Integer>)properties).put("resourceType", 120);
                    ((Hashtable<String, List<Long>>)properties).put("resourceList", resourceList);
                    ProfileAssociateHandler.getInstance().disAssociateCollectionForResource(properties);
                }
            }
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private List<Long> getGroupsAppAssociatedTo(final Set<Long> profileIds) throws APIHTTPException {
        Set<Long> groupList = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "CustomGroup", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileIds.toArray(new Long[profileIds.size()]), 8);
        final Criteria criteria2 = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)7, 1);
        selectQuery.setCriteria(criteria.and(criteria2));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        try {
            final DataObject recentProfileForGroupDO = SyMUtil.getPersistence().get(selectQuery);
            final Iterator<Row> rows = recentProfileForGroupDO.getRows("RecentProfileForGroup");
            while (rows.hasNext()) {
                if (groupList == null) {
                    groupList = new HashSet<Long>();
                }
                final Row row = rows.next();
                groupList.add((Long)row.get("GROUP_ID"));
            }
        }
        catch (final DataAccessException e) {
            AppFacade.logger.log(Level.SEVERE, null, (Throwable)e);
        }
        if (groupList == null) {
            return null;
        }
        return new ArrayList<Long>(groupList);
    }
    
    private List<Long> getUserGroupAppAssociatedTo(final Set<Long> profileIds) {
        Set<Long> groupList = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "CustomGroup", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"), (Object)profileIds.toArray(new Long[profileIds.size()]), 8);
        final Criteria criteria2 = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)7, 0);
        selectQuery.setCriteria(criteria.and(criteria2));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        try {
            final DataObject recentProfileForGroupDO = SyMUtil.getPersistence().get(selectQuery);
            final Iterator<Row> rows = recentProfileForGroupDO.getRows("RecentProfileForGroup");
            while (rows.hasNext()) {
                if (groupList == null) {
                    groupList = new HashSet<Long>();
                }
                final Row row = rows.next();
                groupList.add((Long)row.get("GROUP_ID"));
            }
        }
        catch (final DataAccessException e) {
            AppFacade.logger.log(Level.SEVERE, null, (Throwable)e);
        }
        if (groupList == null) {
            return null;
        }
        return new ArrayList<Long>(groupList);
    }
    
    private List<Long> getUsersAppAssociatedTo(final Set<Long> profileIds) {
        List<Long> userList = null;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForMDMResource"));
        query.addJoin(new Join("RecentProfileForMDMResource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("RecentProfileForMDMResource", "PROFILE_ID"), (Object)profileIds.toArray(new Long[profileIds.size()]), 8);
        query.setCriteria(criteria);
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        try {
            final DataObject recentProfileForUserDO = SyMUtil.getPersistence().get(query);
            final Iterator<Row> rows = recentProfileForUserDO.getRows("RecentProfileForMDMResource");
            while (rows.hasNext()) {
                if (userList == null) {
                    userList = new ArrayList<Long>();
                }
                final Row row = rows.next();
                userList.add((Long)row.get("RESOURCE_ID"));
            }
        }
        catch (final DataAccessException e) {
            AppFacade.logger.log(Level.SEVERE, null, (Throwable)e);
        }
        return userList;
    }
    
    private List<Long> getDevicesAppAssociatedTo(final Set<Long> profileIds) {
        List<Long> deviceList = null;
        final Criteria criteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileIds.toArray(new Long[profileIds.size()]), 8);
        try {
            final DataObject recentProfileForDeviceDO = SyMUtil.getPersistence().get("RecentProfileForResource", criteria);
            final Iterator<Row> rows = recentProfileForDeviceDO.getRows("RecentProfileForResource");
            while (rows.hasNext()) {
                if (deviceList == null) {
                    deviceList = new ArrayList<Long>();
                }
                final Row row = rows.next();
                deviceList.add((Long)row.get("RESOURCE_ID"));
            }
        }
        catch (final DataAccessException e) {
            AppFacade.logger.log(Level.SEVERE, null, (Throwable)e);
        }
        return deviceList;
    }
    
    public JSONObject getDevicesForApp(final JSONObject jsonObject) throws APIHTTPException {
        try {
            JSONUtil.optLongForUVH(jsonObject.getJSONObject("msg_header").getJSONObject("resource_identifier"), "app_id", Long.valueOf(-1L));
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return null;
    }
    
    public JSONObject getUploadedAppDetails(final JSONObject requestJSON) throws Exception {
        final JSONObject messageHeader = requestJSON.getJSONObject("msg_header");
        final JSONObject jsonObject = new JSONObject(requestJSON.toString());
        final FileFacade fileFacade = new FileFacade();
        if (!requestJSON.getJSONObject("msg_body").has("app_file")) {
            throw new APIHTTPException("COM0005", new Object[] { "app_file" });
        }
        final Long fileId = Long.valueOf(requestJSON.getJSONObject("msg_body").get("app_file").toString());
        final String fileName = new FileFacade().validateIfExistsAndReturnFilePath(fileId, APIUtil.getCustomerID(requestJSON));
        final Long fileSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(fileName);
        AppFacade.logger.log(Level.INFO, "FILESIZELOG: BLOB: ActualFileSize: {0}", fileSize);
        final String tempFileName = fileFacade.getModifiedTempLocation(fileName);
        new FileFacade().writeFile(tempFileName, ApiFactoryProvider.getFileAccessAPI().readFile(fileName));
        final Long tempFileSize = new File(tempFileName).length();
        AppFacade.logger.log(Level.INFO, "FILESIZELOG: BLOB: TempFileSize: {0}", tempFileSize);
        if (!fileSize.equals(tempFileSize)) {
            AppFacade.logger.log(Level.WARNING, "FILESIZELOG: BLOB: ***File Size Differs*** -> ActualFileSize: {0} TempFileSize: {1}", new Object[] { fileSize, tempFileSize });
        }
        requestJSON.put("file_path", (Object)tempFileName);
        Integer platform = null;
        if (fileName.toLowerCase().endsWith(".ipa")) {
            platform = 1;
        }
        else if (fileName.endsWith(".apk")) {
            platform = 2;
        }
        else if (fileName.toLowerCase().endsWith(".zip") || fileName.toLowerCase().endsWith(".appx") || fileName.toLowerCase().endsWith(".appxbundle") || fileName.toLowerCase().endsWith(".msi") || fileName.toLowerCase().endsWith(".msix")) {
            platform = 3;
        }
        else if (fileName.toLowerCase().endsWith(".pkg")) {
            platform = 6;
        }
        if (platform == null) {
            throw new APIHTTPException("COM0005", new Object[] { "File is not a valid package to extract" });
        }
        try {
            final AppDataHandlerInterface appDataHandlerInstance = this.getInstance(platform, requestJSON);
            final JSONObject basicAppInfo = appDataHandlerInstance.getAppDetailsFromAppFile(requestJSON);
            String bundleID = JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "packagename");
            if (platform == 1) {
                final String appName = (String)basicAppInfo.get("APP_NAME");
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS") && !MDMStringUtils.isEmpty(bundleID)) {
                    AppFacade.logger.log(Level.INFO, "AllowSameBundleIDStoreAndEnterpriseAppForIOS: feature enabled");
                    final JSONObject appMDMProp = IOSModifiedEnterpriseAppsUtil.getMDMPropsForApp(bundleID, appName, Boolean.TRUE, APIUtil.getCustomerID(jsonObject));
                    AppFacade.logger.log(Level.INFO, "appMDMProp: {0}", new Object[] { appMDMProp });
                    if (appMDMProp != null && appMDMProp.has("IDENTIFIER")) {
                        bundleID = (String)appMDMProp.get("IDENTIFIER");
                        basicAppInfo.put("PackageName", appMDMProp.get("IDENTIFIER"));
                    }
                    if (appMDMProp != null && appMDMProp.has("APP_NAME")) {
                        basicAppInfo.put("APP_NAME", appMDMProp.get("APP_NAME"));
                    }
                }
            }
            if (requestJSON.getJSONObject("msg_body").has("app_id")) {
                final Long appPackageId = Long.valueOf(requestJSON.getJSONObject("msg_body").get("app_id").toString());
                final String bundleIdentifierOfAppFromPackageId = AppsUtil.getInstance().getAppIdentifierFromPackageId(appPackageId);
                final String bundleIdentifierFromAppFile = JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "packagename");
                if (!MDMStringUtils.isEmpty(bundleIdentifierOfAppFromPackageId) && !MDMStringUtils.isEmpty(bundleIdentifierFromAppFile) && !appDataHandlerInstance.allowPackageUpdate(bundleIdentifierFromAppFile, bundleIdentifierOfAppFromPackageId)) {
                    final JSONObject customParamsJson = new JSONObject().put("app_name", (Object)JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "app_name", JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "package_label", JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "productname", "")))).put("version", (Object)JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "versionname")).put("bundle_identifier", (Object)bundleIdentifierFromAppFile);
                    throw new APIHTTPException(customParamsJson, "APP0001", new Object[] { I18N.getMsg("mdm.api.error.app.reject.bundleid", new Object[0]) });
                }
                if (requestJSON.getJSONObject("msg_body").has("label_id") && !basicAppInfo.has("error")) {
                    final Long label_id = Long.valueOf(requestJSON.getJSONObject("msg_body").get("label_id").toString());
                    final JSONObject validationJson = new JSONObject().put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(requestJSON)).put("PLATFORM_TYPE", (Object)platform);
                    validationJson.put("packagename", (Object)JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "packagename"));
                    validationJson.put("APP_VERSION", (Object)JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "versionname"));
                    validationJson.put("APP_NAME_SHORT_VERSION", (Object)JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "version_code", "--"));
                    final String appName2 = JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "app_name", JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "package_label", JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "productname", "")));
                    validationJson.put("app_name", (Object)appName2);
                    validationJson.put("RELEASE_LABEL_ID", (Object)label_id);
                    validationJson.put("PLATFORM_TYPE", (Object)platform);
                    validationJson.put("force_update_in_label", (Object)Boolean.TRUE);
                    validationJson.put("app_id", (Object)appPackageId);
                    AppVersionHandler.getInstance(platform).validateAppVersionForUploadWithReleaseLabel(validationJson);
                }
            }
            else {
                final Boolean isForAllCustomers = requestJSON.getJSONObject("msg_body").optBoolean("is_for_all_customers", (boolean)Boolean.FALSE);
                if (!MDMStringUtils.isEmpty(bundleID) && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers")) {
                    if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS") && bundleID.equalsIgnoreCase(IOSModifiedEnterpriseAppsUtil.getCustomBundleIDForEnterpriseApp(bundleID))) {
                        throw new APIHTTPException("APP0039", new Object[0]);
                    }
                    SyncConfigurationsUtil.validateAppScope(bundleID, platform, isForAllCustomers);
                }
            }
            final JSONObject uploadedAppDetails = new JSONObject().put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(requestJSON)).put("PLATFORM_TYPE", (Object)platform);
            uploadedAppDetails.put("packagename", (Object)JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "packagename"));
            uploadedAppDetails.put("versionname", (Object)JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "versionname"));
            uploadedAppDetails.put("versioncode", (Object)JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "version_code", "--"));
            final String appName3 = JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "app_name", JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "package_label", JSONUtil.optStringIgnoreKeyCase(basicAppInfo, "productname", "")));
            uploadedAppDetails.put("app_name", (Object)appName3);
            final Boolean isMsi = basicAppInfo.optBoolean("is_msi", (boolean)Boolean.FALSE);
            uploadedAppDetails.put("is_msi", (Object)isMsi);
            if (!basicAppInfo.has("error")) {
                AppVersionHandler.getInstance(platform).validateAppVersionForUpload(uploadedAppDetails);
            }
            if (!basicAppInfo.has("error") && (!isMsi || (isMsi && uploadedAppDetails.has("versionname")))) {
                final JSONObject validationJsonResult = AppVersionDBUtil.getInstance().getPossibleUpdatesForTheGivenVersionOfApp(uploadedAppDetails);
                basicAppInfo.put("is_prev_version_already_exists", validationJsonResult.get("isPreviousVersionOfAppAvailable"));
                if (validationJsonResult.has("existingAppPackageId")) {
                    basicAppInfo.put("existing_app_id", (Object)String.valueOf(validationJsonResult.get("existingAppPackageId")));
                    if (validationJsonResult.has("existingAppGroupId")) {
                        basicAppInfo.put("existing_app_group_id", (Object)String.valueOf(validationJsonResult.get("existingAppGroupId")));
                    }
                }
                if (validationJsonResult.has("possibleUpdates")) {
                    basicAppInfo.put("possible_updates", validationJsonResult.get("possibleUpdates"));
                }
                if (validationJsonResult.has("isSameVersionPresent")) {
                    basicAppInfo.put("is_same_version_present", validationJsonResult.get("isSameVersionPresent"));
                }
            }
            if (basicAppInfo.has("keytool_sign")) {
                if (!MDMStringUtils.isEmpty(String.valueOf(basicAppInfo.opt("existing_app_group_id")))) {
                    final JSONObject signObject = (JSONObject)basicAppInfo.get("keytool_sign");
                    final Long appGroupId = basicAppInfo.getLong("existing_app_group_id");
                    final Long appId = basicAppInfo.getLong("existing_app_id");
                    if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SkipValidateAPKSign")) {
                        basicAppInfo.put("signature_mismatch", (Object)Boolean.FALSE);
                    }
                    else {
                        final boolean isSignatureMismatch = MDMRestAPIFactoryProvider.getAppsUtilAPI().isSignatureMismatch(appGroupId, signObject, platform);
                        basicAppInfo.put("signature_mismatch", isSignatureMismatch);
                        if (isSignatureMismatch) {
                            final String currentSignatureMismatchUploads = CustomerParamsHandler.getInstance().getParameterValue("Signature_mismatch_Count", (long)APIUtil.getCustomerID(requestJSON));
                            CustomerParamsHandler.getInstance().addOrUpdateParameter("Signature_mismatch_Count", String.valueOf((currentSignatureMismatchUploads == null) ? 1 : (Integer.valueOf(currentSignatureMismatchUploads) + 1)), (long)APIUtil.getCustomerID(requestJSON));
                        }
                    }
                }
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SkipValidateAPKSign")) {
                    basicAppInfo.put("unsigned_apk", basicAppInfo.getJSONObject("keytool_sign").optBoolean("unsignedAPK", false));
                }
            }
            if (basicAppInfo.has("icon") && !basicAppInfo.has("error")) {
                final JSONArray iconArr = basicAppInfo.getJSONArray("icon");
                final JSONArray replacedIconArr = new JSONArray();
                final Set<String> iconPathSet = new HashSet<String>();
                for (int i = 0; i < iconArr.length(); ++i) {
                    JSONObject iconRequestJSON = new JSONObject(requestJSON, new String[] { "customerID", "msg_header" });
                    if (iconArr.getJSONObject(i).has("icon_path") && !MDMStringUtils.isEmpty(String.valueOf(iconArr.getJSONObject(i).get("icon_path")))) {
                        iconRequestJSON.put("file_name", (Object)new File(String.valueOf(iconArr.getJSONObject(i).get("icon_path"))).getName());
                        iconRequestJSON.put("content", (Object)IOUtils.toByteArray((InputStream)new FileInputStream(String.valueOf(iconArr.getJSONObject(i).get("icon_path")))));
                        final Tika tika = new Tika();
                        final String tikaContentType = tika.detect((byte[])iconRequestJSON.get("content"));
                        iconRequestJSON.put("content_type", (Object)tikaContentType);
                        iconRequestJSON.put("content_length", ((byte[])iconRequestJSON.get("content")).length);
                        iconRequestJSON.put("msg_header", (Object)messageHeader);
                        iconRequestJSON = new FileFacade().addFile(iconRequestJSON, new FileInputStream(String.valueOf(iconArr.getJSONObject(i).get("icon_path"))));
                        iconRequestJSON.remove("msg_header");
                        final String iconPath = String.valueOf(iconArr.getJSONObject(i).get("icon_path"));
                        replacedIconArr.put((Object)iconRequestJSON);
                        iconPathSet.add(iconPath);
                    }
                }
                for (final String iconPath2 : iconPathSet) {
                    fileFacade.deleteFile(iconPath2);
                }
                basicAppInfo.put("icon", (Object)replacedIconArr);
            }
            jsonObject.put("app_info", (Object)basicAppInfo);
            try {
                JSONObject metaDataJSON = new JSONObject(requestJSON, new String[] { "customerID", "msg_header" });
                metaDataJSON.put("file_name", (Object)"app_extracted_meta.json");
                metaDataJSON.put("content", (Object)basicAppInfo.toString().getBytes());
                metaDataJSON.put("content_type", (Object)"application/json");
                metaDataJSON.put("content_length", ((byte[])metaDataJSON.get("content")).length);
                metaDataJSON.put("msg_header", (Object)messageHeader);
                metaDataJSON = new FileFacade().addFile(metaDataJSON, new ByteArrayInputStream(basicAppInfo.toString().getBytes()));
                metaDataJSON.remove("msg_header");
                final Long metaFileId = Long.valueOf(String.valueOf(metaDataJSON.get("file_id")));
                this.persistExtractedetaDataFile(fileId, metaFileId);
            }
            catch (final Exception e) {
                AppFacade.logger.log(Level.WARNING, "Unable to save the extracted meta info, not harmfull since it is fail safe.", e);
            }
            final Boolean editableInfo = basicAppInfo.optBoolean("allowEditableFields", (boolean)Boolean.FALSE);
            if (editableInfo) {
                basicAppInfo.put("allowEditableFields", true);
                final String errorMsg = basicAppInfo.optString("errorMsg", I18N.getMsg("mdm.api.error.app.reject.file", new Object[0]));
                throw new APIHTTPException(basicAppInfo, "APP0001", new Object[] { errorMsg });
            }
            if (basicAppInfo.has("error")) {
                basicAppInfo.put("error", (Object)I18N.getMsg(String.valueOf(basicAppInfo.get("error")), new Object[0]));
            }
            if (basicAppInfo.has("SUPPORTED_DEVICES")) {
                final JSONObject jsonObject2 = basicAppInfo;
                final String s = "SUPPORTED_DEVICES";
                AppsUtil.getInstance();
                jsonObject2.put(s, AppsUtil.getAPISupportedDevicesValues(Integer.parseInt(basicAppInfo.get("SUPPORTED_DEVICES").toString()), platform));
            }
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowCustomizedAppURL")) {
                final String checkSum = SecurityUtil.getSHA256HashFromInputStream((InputStream)new FileInputStream(tempFileName));
                basicAppInfo.put("app_checksum", (Object)checkSum);
            }
            jsonObject.put("app_info", (Object)basicAppInfo);
        }
        catch (final Exception e2) {
            AppFacade.logger.log(Level.WARNING, "Exception while extracting app file ", e2);
            throw e2;
        }
        finally {
            fileFacade.deleteFile(new File(tempFileName).getParent());
        }
        jsonObject.remove("msg_header");
        jsonObject.remove("msg_body");
        return jsonObject;
    }
    
    public JSONObject addAppDependencies(final JSONObject message) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final FileFacade fileFacade = new FileFacade();
        String tempFilePathDMTemp = null;
        try {
            final String tempFilePathDM = String.valueOf(FileUploadManager.getFilePath(JSONUtil.toJSON("file_id", Long.valueOf(message.getJSONObject("msg_body").get("app_file").toString()))).get("file_path"));
            tempFilePathDMTemp = fileFacade.getTempLocation(tempFilePathDM);
            fileFacade.writeFile(tempFilePathDMTemp, ApiFactoryProvider.getFileAccessAPI().readFile(tempFilePathDM));
            final Long customerId = APIUtil.getCustomerID(message);
            final JSONArray dependencyArray = new AppDependencyHandler(3).validateAndUploadMultipleDependencies(tempFilePathDMTemp, customerId);
            jsonObject.put("dependencies", (Object)dependencyArray);
        }
        finally {
            if (tempFilePathDMTemp != null) {
                fileFacade.deleteFile(tempFilePathDMTemp);
            }
        }
        return jsonObject;
    }
    
    private AppDataHandlerInterface getInstance(final int platform, final JSONObject apiRequest) {
        AppDataHandlerInterface appDataHandlerInterface = null;
        try {
            if (!apiRequest.has("customerID")) {
                apiRequest.put("customerID", (Object)APIUtil.getCustomerID(apiRequest));
            }
            if (!apiRequest.has("userID")) {
                apiRequest.put("userID", (Object)APIUtil.getUserID(apiRequest));
            }
            if (!apiRequest.has("userName")) {
                apiRequest.put("userName", (Object)APIUtil.getUserName(apiRequest));
            }
            if (!apiRequest.has("app_type")) {
                final JSONObject msgBody = apiRequest.optJSONObject("msg_body");
                if (msgBody != null) {
                    final Integer apptype = msgBody.optInt("app_type", -1);
                    apiRequest.put("app_type", (Object)apptype);
                }
            }
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.WARNING, "failed to fetch customerId & userID");
        }
        if (platform == 3) {
            appDataHandlerInterface = new WindowsAppDataHandler(apiRequest);
        }
        else if (platform == 2) {
            appDataHandlerInterface = new AndroidAppDataHandler(apiRequest);
        }
        else if (platform == 1) {
            appDataHandlerInterface = new IOSAppDatahandler(apiRequest);
        }
        else if (platform == 4) {
            appDataHandlerInterface = new ChromeAppDataHandler(apiRequest);
        }
        else {
            if (platform != 6) {
                throw new APIHTTPException("COM0014", new Object[] { "Invalid platform" });
            }
            appDataHandlerInterface = new MacAppDataHandler(apiRequest);
        }
        return appDataHandlerInterface;
    }
    
    public JSONObject restoreAppsFromTrash(final JSONObject message) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final Long customerId = APIUtil.getCustomerID(message);
        final List packageIds = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("app_ids"));
        final HashMap appProfileDetails = AppsUtil.getInstance().getProfileIDFromPackageIdsForTrash(packageIds, customerId);
        final List profileIDs = appProfileDetails.get("profileIds");
        final List appGroupList = appProfileDetails.get("appGroupIds");
        final AppTrashModeHandler trashModeHandler = new AppTrashModeHandler();
        if (appGroupList == null || appGroupList.isEmpty()) {
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        if (!trashModeHandler.allowRestore(APIUtil.getCustomerID(message), appGroupList).optBoolean("allowRestore")) {
            throw new APIHTTPException("TRA0003", new Object[0]);
        }
        trashModeHandler.restoreAppFromTrash(profileIDs);
        jsonObject.put("app_ids", (Object)packageIds.toArray());
        final JSONObject syncJSON = new JSONObject();
        syncJSON.put("app_ids", (Object)packageIds.toArray(new Long[0]));
        syncJSON.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(message));
        syncJSON.put("PROFILE_TYPE", 2);
        syncJSON.put("LAST_MODIFIED_BY", (Object)APIUtil.getUserID(message));
        syncJSON.put("LOGIN_ID", (Object)APIUtil.getLoginID(message));
        SyncConfigurationListeners.invokeListeners(syncJSON, 211);
        return jsonObject;
    }
    
    public JSONObject deleteAppFromTrash(final JSONObject message) throws Exception {
        final List packageIds = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("app_ids"));
        final Long customerId = APIUtil.getCustomerID(message);
        final HashMap hashMap = AppsUtil.getInstance().getProfileIDFromPackageIdsForTrash(packageIds, customerId);
        hashMap.put("packageIds", packageIds);
        final String userName = APIUtil.getUserName(message);
        hashMap.put("CustomerID", customerId);
        final AppTrashModeHandler appTrashModeHandler = new AppTrashModeHandler();
        final List appGroupIdList = hashMap.get("appGroupIds");
        if (appGroupIdList == null || appGroupIdList.isEmpty()) {
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        final Boolean forceRemoval = APIUtil.getBooleanFilter(message, "forceremoval", Boolean.FALSE);
        final boolean hasVppApp = appTrashModeHandler.isAccountApp(appGroupIdList, 1L);
        final boolean hasAfwApp = appTrashModeHandler.isAccountApp(appGroupIdList, 2L);
        final boolean hasbstoreApp = appTrashModeHandler.isAccountApp(appGroupIdList, 3L);
        final String status = appTrashModeHandler.isAppAssociated(hashMap);
        final boolean isBstoreConfigured = new WpAppSettingsHandler().isBstoreConfigured(customerId);
        boolean accountApp = false;
        if (hasVppApp) {
            accountApp = true;
        }
        else if (hasbstoreApp) {
            accountApp = isBstoreConfigured;
        }
        boolean success = false;
        final JSONObject response = new JSONObject();
        if (accountApp) {
            final String errorMessage = I18N.getMsg("mdm.api.error.app.trash.error.account.app", new Object[0]);
            throw new APIHTTPException("TRA0002", new Object[] { errorMessage });
        }
        if (status.equalsIgnoreCase("me_mdm_not_safe")) {
            final String errorMessage = I18N.getMsg("mdm.app_mgmt.apps.me_mdm_not_safe", new Object[0]);
            throw new APIHTTPException("TRA0001", new Object[] { errorMessage });
        }
        if (!status.equalsIgnoreCase("success") && !forceRemoval) {
            final String errorMessage = I18N.getMsg("mdm.appmgmt.unable_to_delete_app", new Object[0]);
            throw new APIHTTPException("TRA0001", new Object[] { errorMessage });
        }
        final JSONObject syncJSON = new JSONObject();
        syncJSON.put("app_ids", (Object)packageIds.toArray(new Long[0]));
        syncJSON.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(message));
        syncJSON.put("PROFILE_TYPE", 2);
        syncJSON.put("LAST_MODIFIED_BY", (Object)APIUtil.getUserID(message));
        syncJSON.put("LOGIN_ID", (Object)APIUtil.getLoginID(message));
        SyncConfigurationListeners.invokeListeners(syncJSON, 210);
        appTrashModeHandler.deleteMultipleAppFromTrash(hashMap);
        success = true;
        if (hasAfwApp) {
            new GooglePlayBusinessAppHandler().syncGooglePlay(customerId, 2, 3);
        }
        if (forceRemoval) {
            appTrashModeHandler.updateForcedAppRemovalStatus(hashMap);
            final String remarks = "mdm.appmgmt.app_force_delete";
            final List appGroupIds = hashMap.get("appGroupIds");
            final Map<Long, String> appNames = appTrashModeHandler.getAppNames(appGroupIds);
            final List appGroupId = new ArrayList(appNames.keySet());
            for (final Object appGroup : appGroupId) {
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2037, null, userName, remarks, appNames.get(appGroup), customerId);
            }
        }
        response.put("success", success);
        return response;
    }
    
    public JSONObject getAppRepositoryStatus(final JSONObject message) throws Exception {
        final Long customerID = APIUtil.getCustomerID(message);
        final int updateCount = new AppDataHandler().getTheUpdateAvailableAppsCount(customerID, Boolean.FALSE);
        final int storeAppUpdateCount = new AppDataHandler().getTheUpdateAvailableAppsCount(customerID, Boolean.TRUE);
        int trashCount = new AppTrashModeHandler().getAppTrashCount(customerID);
        final Boolean isVppConfigured = VPPTokenDataHandler.getInstance().isVppTokenConfigured(customerID);
        final Boolean isPfwConfigured = GoogleForWorkSettings.isAFWSettingsConfigured(customerID);
        final JSONObject bStoreDetails = new JSONObject();
        WpAppSettingsHandler.getInstance().putBstoreData(bStoreDetails, customerID);
        final boolean bStoreConfigured = bStoreDetails.has("domain_name") || bStoreDetails.has("organisation");
        final JSONObject response = new JSONObject();
        response.put("update_count", updateCount);
        response.put("store_app_update_count", storeAppUpdateCount);
        response.put("bstore_configured", bStoreConfigured);
        response.put("pfw_configured", (Object)isPfwConfigured);
        response.put("vpp_configured", (Object)isVppConfigured);
        if (trashCount <= 0) {
            trashCount = 0;
        }
        response.put("trash_count", trashCount);
        return response;
    }
    
    public JSONObject getAppConfiguration(final JSONObject message) throws Exception {
        final Long customerID = APIUtil.getCustomerID(message);
        final Long packageId = APIUtil.getResourceID(message, "app_id");
        this.validateIfAppFound(packageId, customerID);
        final Integer platform = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "PLATFORM_TYPE");
        return this.getInstance(platform, message).getAppConfiguration(message);
    }
    
    public JSONObject addAppConfiguration(final JSONObject message) throws Exception {
        final Long packageId = APIUtil.getResourceID(message, "app_id");
        final Long customerID = APIUtil.getCustomerID(message);
        Long labelId = APIUtil.getResourceID(message, "label_id");
        if (labelId == -1L) {
            labelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerID);
        }
        this.validateIfAppFound(packageId, customerID);
        final Integer platform = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "PLATFORM_TYPE");
        final JSONObject syncJSON = new JSONObject(message.toString());
        final JSONObject response = this.getInstance(platform, message).addAppConfiguration(message);
        syncJSON.put("customerID", (Object)customerID);
        syncJSON.put("userID", (Object)APIUtil.getUserID(message));
        syncJSON.put("platform_type", (Object)platform);
        syncJSON.put("LOGIN_ID", (Object)APIUtil.getLoginID(message));
        syncJSON.put("app_id", (Object)packageId);
        syncJSON.put("app_unique_identifier", (Object)SyncConfigurationsUtil.getAppUniqueIdentifier(packageId, labelId));
        SyncConfigurationListeners.invokeListeners(syncJSON, 206);
        return response;
    }
    
    public JSONObject deleteAppConfiguration(final JSONObject message) throws Exception {
        final Long packageId = APIUtil.getResourceID(message, "app_id");
        final Long customerID = APIUtil.getCustomerID(message);
        Long labelId = APIUtil.getResourceID(message, "label_id");
        if (labelId == -1L) {
            labelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerID);
        }
        this.validateIfAppFound(packageId, customerID);
        final Integer platform = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "PLATFORM_TYPE");
        final JSONObject syncJSON = new JSONObject(message.toString());
        final JSONObject response = this.getInstance(platform, message).deleteAppConfiguration(message);
        syncJSON.put("customerID", (Object)customerID);
        syncJSON.put("userID", (Object)APIUtil.getUserID(message));
        syncJSON.put("platform_type", (Object)platform);
        syncJSON.put("LOGIN_ID", (Object)APIUtil.getLoginID(message));
        syncJSON.put("app_id", (Object)packageId);
        syncJSON.put("app_unique_identifier", (Object)SyncConfigurationsUtil.getAppUniqueIdentifier(packageId, labelId));
        SyncConfigurationListeners.invokeListeners(syncJSON, 208);
        return response;
    }
    
    public JSONObject addWindowsToken(final JSONObject message) throws Exception {
        final JSONObject tokenJSON = new JSONObject();
        final JSONObject msg_body = (JSONObject)message.get("msg_body");
        JSONObject response = new JSONObject();
        String tempFilePathDM = null;
        try {
            final Long customerID = APIUtil.getCustomerID(message);
            final Long fileId = Long.valueOf(msg_body.get("csc_file").toString());
            tempFilePathDM = new FileFacade().validateIfExistsAndReturnFilePath(fileId, customerID);
            tokenJSON.put("EMAIL_ADDRESS", (Object)msg_body.optString("expiry_email", ""));
            if (tempFilePathDM.contains(".aet")) {
                tokenJSON.put("AET_FILE_UPLOAD", (Object)tempFilePathDM);
                response = new WpAppSettingsHandler().handleAET(tokenJSON);
            }
            else {
                tokenJSON.put("CERT_FILE_UPLOAD", (Object)tempFilePathDM);
                response = new WpAppSettingsHandler().handleCodeSigning(tokenJSON, null);
            }
            if (response.has("errorMessage") && !MDMStringUtils.isEmpty(String.valueOf(response.get("errorMessage")))) {
                throw new APIHTTPException("COM0015", new Object[] { I18N.getMsg("mdm.api.upload.cert.expired", new Object[0]) });
            }
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.WARNING, "Issue on adding CSC", e);
            if (e instanceof APIHTTPException) {
                throw e;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        finally {
            if (tempFilePathDM != null) {
                FileFacade.getInstance().deleteFile(tempFilePathDM);
            }
        }
        MessageProvider.getInstance().hideMessage("AET_NOT_UPLOADED", APIUtil.getCustomerID(message));
        return response;
    }
    
    public JSONObject updateWindowsToken(final JSONObject message) throws Exception {
        final JSONObject tokenJSON = new JSONObject();
        final JSONObject msg_body = (JSONObject)message.get("msg_body");
        JSONObject response = new JSONObject();
        try {
            final Long customerId = APIUtil.getCustomerID(message);
            final Object cscFile = msg_body.opt("csc_file");
            String tempFilePathDM = null;
            if (cscFile != null) {
                final Long fileID = Long.valueOf(msg_body.get("csc_file").toString());
                tempFilePathDM = new FileFacade().validateIfExistsAndReturnFilePath(fileID, customerId);
            }
            tokenJSON.put("EMAIL_ADDRESS", (Object)msg_body.optString("expiry_email", ""));
            if (tempFilePathDM != null) {
                tokenJSON.put("CERT_FILE_UPLOAD", (Object)tempFilePathDM);
            }
            response = new WpAppSettingsHandler().handleCodeSigning(tokenJSON, null);
            if (response.has("errorMessage") && !MDMStringUtils.isEmpty(String.valueOf(response.get("errorMessage")))) {
                throw new APIHTTPException("COM0015", new Object[] { I18N.getMsg("mdm.api.upload.cert.expired", new Object[0]) });
            }
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.WARNING, "Issue on updating CSC", e);
            if (e instanceof APIHTTPException) {
                throw e;
            }
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        return response;
    }
    
    public JSONObject getWindowsToken(final JSONObject message) throws Exception {
        final JSONObject response = new JSONObject();
        final Long customerID = APIUtil.getCustomerID(message);
        final Properties wpAETNativeAppProp = WpAppSettingsHandler.getInstance().getWpAETDetails(customerID);
        if (wpAETNativeAppProp != null && (wpAETNativeAppProp.containsKey("ENTERPRISE_ID") || wpAETNativeAppProp.containsKey("CERT_FILE_PATH"))) {
            final String createdTimeStr = Utils.getEventTime(Long.valueOf(((Hashtable<K, Long>)wpAETNativeAppProp).get("CREATION_TIME")));
            final String expireTimeStr = Utils.getEventTime(Long.valueOf(((Hashtable<K, Long>)wpAETNativeAppProp).get("EXPIRE_TIME")));
            final String certExpireTimeStr = Utils.getEventTime(Long.valueOf(((Hashtable<K, Long>)wpAETNativeAppProp).get("CERT_EXPIRE_TIME")));
            final String aetPath = wpAETNativeAppProp.getProperty("AET_FILE_PATH");
            if (aetPath != null) {
                response.put("AET_FILE_PATH", (Object)aetPath);
                response.put("EXPIRE_TIME", (Object)expireTimeStr);
                response.put("CREATION_TIME", (Object)createdTimeStr);
                response.put("ENTERPRISE_ID", ((Hashtable<K, Object>)wpAETNativeAppProp).get("ENTERPRISE_ID"));
            }
            else {
                response.put("CREATION_TIME", (Object)createdTimeStr);
                response.put("CERT_EXPIRE_TIME", (Object)certExpireTimeStr);
                response.put("EMAIL_ADDRESS", ((Hashtable<K, Object>)wpAETNativeAppProp).get("EMAIL_ADDRESS"));
                response.put("CERT_SUBJECT", ((Hashtable<K, Object>)wpAETNativeAppProp).get("CERT_SUBJECT"));
            }
            final Long wpNativeAppId = ((Hashtable<K, Long>)wpAETNativeAppProp).get("APP_ID");
            if (wpNativeAppId != null && wpNativeAppId != -1L) {
                final HashMap appsMap = MDMUtil.getInstance().getAppDetails(wpNativeAppId);
                if (appsMap != null) {
                    response.put("APP_NAME", appsMap.get("APP_NAME"));
                    response.put("IDENTIFIER", appsMap.get("IDENTIFIER"));
                    response.put("APP_VERSION", appsMap.get("APP_VERSION"));
                }
            }
        }
        return response;
    }
    
    public JSONObject getWindowsToken(final Long customerID) throws Exception {
        final JSONObject response = new JSONObject();
        final Properties wpAETNativeAppProp = WpAppSettingsHandler.getInstance().getWpAETDetails(customerID);
        if (wpAETNativeAppProp != null && (wpAETNativeAppProp.containsKey("ENTERPRISE_ID") || wpAETNativeAppProp.containsKey("CERT_FILE_PATH"))) {
            final String createdTimeStr = Utils.getEventTime(Long.valueOf(((Hashtable<K, Long>)wpAETNativeAppProp).get("CREATION_TIME")));
            final String expireTimeStr = Utils.getEventTime(Long.valueOf(((Hashtable<K, Long>)wpAETNativeAppProp).get("EXPIRE_TIME")));
            final String certExpireTimeStr = Utils.getEventTime(Long.valueOf(((Hashtable<K, Long>)wpAETNativeAppProp).get("CERT_EXPIRE_TIME")));
            response.put("CREATION_TIME", (Object)createdTimeStr);
            response.put("EXPIRE_TIME", (Object)expireTimeStr);
            response.put("CERT_EXPIRE_TIME", (Object)certExpireTimeStr);
            response.put("EMAIL_ADDRESS", ((Hashtable<K, Object>)wpAETNativeAppProp).get("EMAIL_ADDRESS"));
            response.put("CERT_SUBJECT", ((Hashtable<K, Object>)wpAETNativeAppProp).get("CERT_SUBJECT"));
            final Long wpNativeAppId = ((Hashtable<K, Long>)wpAETNativeAppProp).get("APP_ID");
            if (wpNativeAppId != null && wpNativeAppId != -1L) {
                final HashMap appsMap = MDMUtil.getInstance().getAppDetails(wpNativeAppId);
                if (appsMap != null) {
                    response.put("APP_NAME", appsMap.get("APP_NAME"));
                    response.put("IDENTIFIER", appsMap.get("IDENTIFIER"));
                    response.put("APP_VERSION", appsMap.get("APP_VERSION"));
                }
            }
        }
        return response;
    }
    
    public JSONObject getPFWWebToken(final JSONObject options) {
        try {
            final JSONObject response = new JSONObject();
            final JSONObject postParams = options.getJSONObject("msg_body");
            final String parent = postParams.optString("parent", (String)null);
            final Boolean playSearch = postParams.optBoolean("play_search", (boolean)Boolean.FALSE);
            final Boolean playSearchAndApprove = postParams.optBoolean("play_search_approve_mode", (boolean)Boolean.FALSE);
            final Boolean webApps = postParams.optBoolean("web_apps", (boolean)Boolean.FALSE);
            final Boolean privateApps = postParams.optBoolean("private_apps", (boolean)Boolean.FALSE);
            final Boolean storeBuilder = postParams.optBoolean("store_builder", (boolean)Boolean.FALSE);
            final Long customerId = APIUtil.getCustomerID(options);
            final Boolean isPfwConfigured = GoogleForWorkSettings.isAFWSettingsConfigured(customerId);
            if (parent == null || parent.isEmpty()) {
                throw new APIHTTPException("COM0005", new Object[] { "Missing mandatory param : parent" });
            }
            if (!parent.startsWith("https://")) {
                throw new APIHTTPException("COM0005", new Object[] { "Field : parent, must start with https://" });
            }
            if (playSearch == Boolean.FALSE && playSearchAndApprove == Boolean.FALSE && webApps == Boolean.FALSE && privateApps == Boolean.FALSE && storeBuilder == Boolean.FALSE) {
                throw new APIHTTPException("COM0005", new Object[] { "Any of the mode must be set as enabled!" });
            }
            if (!isPfwConfigured) {
                throw new APIHTTPException("COM0015", new Object[] { "AFW Not Configured" });
            }
            final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW));
            response.put("web_token", (Object)ebs.generateWebToken(postParams));
            return response;
        }
        catch (final APIHTTPException ae) {
            throw ae;
        }
        catch (final GoogleJsonResponseException ex) {
            AppFacade.logger.log(Level.WARNING, "GoogleJsonResponseException while getting the web token ", (Throwable)ex);
            final JSONObject errorResponseJSON = GoogleAPIErrorHandler.getErrorResponseJSON(ex);
            final String apiErrorCode = errorResponseJSON.optString("apiErrorCode");
            if (apiErrorCode != null && apiErrorCode.equals("APP0026")) {
                throw new APIHTTPException(apiErrorCode, new Object[0]);
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.WARNING, "Exception while getting the web token ", e);
            throw new APIHTTPException("COM0004", new Object[] { e.getMessage() });
        }
    }
    
    public JSONObject getPermissionsAssociatedWithApp(final JSONObject jsonObject) throws Exception {
        final Long packageId = JSONUtil.optLongForUVH(jsonObject.getJSONObject("msg_header").getJSONObject("resource_identifier"), "app_id", (Long)null);
        final Integer platform = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "PLATFORM_TYPE");
        if (platform == null) {
            throw new APIHTTPException("COM0008", new Object[] { packageId });
        }
        return this.getInstance(platform, jsonObject).getAppPermission(jsonObject);
    }
    
    public JSONObject modifyAppPermissions(final JSONObject jsonObject) throws Exception {
        final Long packageId = JSONUtil.optLongForUVH(jsonObject.getJSONObject("msg_header").getJSONObject("resource_identifier"), "app_id", (Long)null);
        final Integer platform = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "PLATFORM_TYPE");
        if (platform == null) {
            throw new APIHTTPException("COM0008", new Object[] { packageId });
        }
        return this.getInstance(platform, jsonObject).modifyAppPermission(jsonObject);
    }
    
    private Criteria getFilterCriteria(final JSONObject request) {
        final String KEY = "key";
        final String VALUE = "value";
        final String OPERATOR = "operator";
        final Criteria combinedCriteria = null;
        Criteria platformCriteria = null;
        Criteria appCriteria = null;
        final Criteria licenseCriteria = null;
        try {
            final JSONObject jsonObject = request.optJSONObject("filters");
            JSONArray query = null;
            if (jsonObject != null) {
                query = jsonObject.optJSONArray("query");
            }
            if (query != null) {
                for (int i = 0; i < query.length(); ++i) {
                    final JSONObject curQuery = query.getJSONObject(i);
                    final String key = String.valueOf(curQuery.get(KEY));
                    final String value = String.valueOf(curQuery.get(VALUE));
                    final String operator = String.valueOf(curQuery.get(OPERATOR));
                    if (key.equals("platform")) {
                        final Criteria newCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)value, 0);
                        platformCriteria = this.combineCriteria(platformCriteria, newCriteria, 1);
                    }
                    else if (key.equals("app_type")) {
                        final Criteria newCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)value, 0);
                        appCriteria = this.combineCriteria(appCriteria, newCriteria, 1);
                    }
                    else if (key.equals("license")) {
                        final Criteria newCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"), (Object)value, 0);
                        this.combineCriteria(licenseCriteria, newCriteria, 1);
                    }
                }
                this.combineCriteria(combinedCriteria, platformCriteria, 2);
                this.combineCriteria(combinedCriteria, appCriteria, 2);
                this.combineCriteria(combinedCriteria, licenseCriteria, 2);
            }
        }
        catch (final JSONException e) {
            AppFacade.logger.log(Level.SEVERE, "filtering failed", (Throwable)e);
        }
        return combinedCriteria;
    }
    
    private Criteria combineCriteria(Criteria base, final Criteria criteriaToAdd, final int operator) {
        if (base == null) {
            base = criteriaToAdd;
            return base;
        }
        if (operator == 1) {
            base.or(criteriaToAdd);
        }
        else {
            base.and(criteriaToAdd);
        }
        return base;
    }
    
    public JSONObject getBstoreRedirectURL(final JSONObject request) throws Exception {
        final Long userID = APIUtil.getUserID(request);
        final Long customerID = APIUtil.getCustomerID(request);
        final JSONObject response = MDMApiFactoryProvider.getBusinessStoreAccess().getBusinessStoreRedirectURL(customerID, userID);
        String redirectURL = String.valueOf(response.get("redirect_url"));
        final List list = new ArrayList();
        list.add("serurl");
        list.add("state");
        redirectURL = MDMUtil.getInstance().removeParamsFromURL(redirectURL, list);
        response.put("redirect_url", (Object)redirectURL);
        return response;
    }
    
    public Object getCategoryCode(final JSONObject message) throws Exception {
        try {
            final JSONObject requestJSON = message;
            final int platform = APIUtil.getIntegerFilter(message, "platform");
            return this.getInstance(platform, requestJSON).getCategoryCode(message);
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0006", new Object[0]);
        }
    }
    
    public JSONObject getAppSuggestion(final JSONObject message) throws Exception {
        try {
            final int platform = APIUtil.getIntegerFilter(message, "platform");
            return this.getInstance(platform, message).getAppSuggestion(message);
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0006", new Object[0]);
        }
    }
    
    public Object getCountry(final JSONObject message) throws Exception {
        try {
            final JSONObject requestJSON = message;
            final int platform = 1;
            return this.getInstance(platform, requestJSON).getCountryCode(requestJSON);
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0006", new Object[0]);
        }
    }
    
    public JSONObject getPermissionsListforApp(final JSONObject jsonObject) throws Exception {
        final Long packageId = JSONUtil.optLongForUVH(jsonObject.getJSONObject("msg_header").getJSONObject("resource_identifier"), "app_id", (Long)null);
        final Integer platform = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "PLATFORM_TYPE");
        if (platform == null) {
            throw new APIHTTPException("COM0008", new Object[] { packageId });
        }
        return this.getInstance(platform, jsonObject).getAppPermissionList(jsonObject);
    }
    
    public void upgradeAppForAllDevices(final JSONObject message) {
        final Long customerId = APIUtil.getCustomerID(message);
        final List<Long> updateAppGroupList = new AppDataHandler().getListofAppsWithUpdate(customerId, false, false, true);
        try {
            JSONObject messageBody = message.optJSONObject("msg_body");
            if (messageBody == null) {
                messageBody = new JSONObject();
            }
            final Long appId = APIUtil.getResourceID(message, "app_id");
            final Integer platform = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)appId, "PLATFORM_TYPE");
            final Long appGroupId = AppsUtil.getInstance().getAppGroupId(appId);
            if (appGroupId == null || !updateAppGroupList.contains(appGroupId)) {
                throw new APIHTTPException("APP0007", new Object[] { appId });
            }
            message.put("msg_body", (Object)messageBody.put("appGroupId", (Object)appGroupId));
            this.getInstance(platform, message).updateAppsForAllDevices(message);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            AppFacade.logger.log(Level.SEVERE, "error in upgradeApp - AppFacade", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public boolean isNonAccountAppAvailable(final int platformType, final Long customerId) {
        final Criteria custCriteria = new Criteria(Column.getColumn("MdPackage", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria nonPortalcriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)false, 0);
        final Criteria platFormCriteria = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)platformType, 0);
        final Criteria enterpriseCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 0);
        try {
            return DBUtil.getRecordCount(this.getRepositoryAppsCountQuery(custCriteria.and(nonPortalcriteria.and(platFormCriteria.and(enterpriseCriteria.negate()))))) > 0;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
    }
    
    public JSONObject verifyTrashActivity(final JSONObject message) {
        JSONObject errorMessages = null;
        try {
            final JSONObject msgBody = message.optJSONObject("msg_body");
            if (msgBody == null || msgBody.optJSONArray("app_ids") == null || msgBody.getJSONArray("app_ids").length() <= 0) {
                throw new APIHTTPException("COM0009", new Object[] { "app_ids" });
            }
            final HashSet<Long> appSet = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("app_ids")));
            final Long[] packageIDALongArr = appSet.toArray(new Long[0]);
            final Long[] profileIds = AppsUtil.getInstance().getProfileIDS(packageIDALongArr);
            final Long customerId = APIUtil.getCustomerID(message);
            this.validateIfAppsFound(appSet, customerId);
            final List<Long> appGroupIds = AppsUtil.getInstance().getAppGroupDetails(new ArrayList(appSet), customerId);
            final int appsWithConfigPolicy = new AppTrashModeHandler().getAppConfigPolicyApps(appGroupIds, true);
            final HashMap params = new HashMap();
            params.put("appGroupIds", appGroupIds);
            params.put("CustomerID", customerId);
            params.put("profileIds", Arrays.asList(profileIds));
            params.put("packageIds", new ArrayList());
            final int mode = message.getJSONObject("msg_header").getJSONObject("filters").optInt("mode", -1);
            if (appGroupIds.size() == 0) {
                throw new APIHTTPException("COM0024", new Object[] { "appIds" });
            }
            final AppTrashModeHandler appTrashModeHandler = new AppTrashModeHandler();
            switch (mode) {
                case 1: {
                    if (appSet.size() != appTrashModeHandler.getAppGroupsMovedToTrash(appGroupIds, false)) {
                        throw new APIHTTPException("COM0024", new Object[] { "appIds" });
                    }
                    errorMessages = appTrashModeHandler.checkMoveAppsToTrashFesability(params);
                    if (errorMessages.length() == 0) {
                        errorMessages.put("allowTrash", true);
                        break;
                    }
                    errorMessages.put("allowTrash", false);
                    break;
                }
                case 2: {
                    if (appSet.size() != appTrashModeHandler.getAppGroupsMovedToTrash(appGroupIds, true)) {
                        throw new APIHTTPException("COM0024", new Object[] { "appIds" });
                    }
                    JSONArray errorMessage = new JSONArray();
                    final boolean hasVppApp = appTrashModeHandler.isAccountApp(appGroupIds, 1L);
                    final boolean hasbstoreApp = appTrashModeHandler.isAccountApp(appGroupIds, 3L);
                    final String status = appTrashModeHandler.isAppAssociated(params);
                    final boolean isBstoreConfigured = new WpAppSettingsHandler().isBstoreConfigured(customerId);
                    boolean accountApp = false;
                    boolean forceRemoval = Boolean.FALSE;
                    if (hasVppApp) {
                        accountApp = true;
                    }
                    else if (hasbstoreApp) {
                        accountApp = isBstoreConfigured;
                    }
                    if (accountApp) {
                        errorMessage.put((Object)I18N.getMsg("mdm.api.error.app.trash.error.account.app", new Object[0]));
                    }
                    else if (status.equalsIgnoreCase("me_mdm_not_safe")) {
                        errorMessage.put((Object)I18N.getMsg("mdm.app_mgmt.apps.me_mdm_not_safe", new Object[0]));
                    }
                    else if (appsWithConfigPolicy > 0) {
                        errorMessage.put((Object)I18N.getMsg("mdm.app.restrict.app_config_present", new Object[0]));
                    }
                    else if (status.equalsIgnoreCase("profile_passive_delete_safe")) {
                        errorMessage.put((Object)I18N.getMsg("mdm.app_mgmt.apps.delete_perm", new Object[0]));
                        forceRemoval = Boolean.TRUE;
                    }
                    else if (!status.equalsIgnoreCase("Success")) {
                        Integer inactive_period;
                        try {
                            final String inactive = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("inactive_period");
                            inactive_period = ((inactive == null) ? 7 : Integer.parseInt(inactive));
                        }
                        catch (final Exception var11) {
                            inactive_period = 7;
                        }
                        errorMessage.put((Object)I18N.getMsg("mdm.app_mgmt.apps.delete_perm_warning", new Object[] { inactive_period }));
                    }
                    else {
                        errorMessage = null;
                    }
                    errorMessages = new JSONObject();
                    if (forceRemoval && appsWithConfigPolicy == 0) {
                        errorMessages.put("allowDeletePermanently", true);
                        errorMessages.put("forceRemoval", true);
                        errorMessages.put("ErrorMessage", (Object)errorMessage);
                        break;
                    }
                    if (errorMessage != null) {
                        errorMessages.put("ErrorMessage", (Object)errorMessage);
                        errorMessages.put("allowDeletePermanently", false);
                        break;
                    }
                    errorMessages.put("allowDeletePermanently", true);
                    break;
                }
                case 3: {
                    if (appSet.size() != appTrashModeHandler.getAppGroupsMovedToTrash(appGroupIds, true)) {
                        throw new APIHTTPException("COM0024", new Object[] { "appIds" });
                    }
                    errorMessages = appTrashModeHandler.allowRestore(customerId, appGroupIds);
                    if (errorMessages.has("errorMsg") && errorMessages.getJSONArray("errorMsg").length() > 0) {
                        errorMessages.put("ErrorMessage", (Object)errorMessages.getJSONArray("errorMsg"));
                        errorMessages.remove("errorMsg");
                        break;
                    }
                    if (errorMessages.has("errorMsg") && errorMessages.getJSONArray("errorMsg").length() == 0) {
                        errorMessages.remove("errorMsg");
                        break;
                    }
                    break;
                }
                default: {
                    errorMessages = new JSONObject();
                    break;
                }
            }
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "exception in verifying trash", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return errorMessages;
    }
    
    public JSONObject getSignUpURL(final JSONObject message) {
        try {
            final int platform = APIUtil.getIntegerFilter(message, "platform");
            return this.getInstance(platform, message).generateSignUpURL(message);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
    }
    
    private void persistExtractedetaDataFile(final Long uploadedFileId, final Long metaFileId) {
        try {
            final Long existingmetaFileId = (Long)DBUtil.getValueFromDB("EnterpriseAppExtractionDetails", "ENTERPRISE_APP_FILE_ID", (Object)uploadedFileId, "EXTRACTED_META_FILE_ID");
            final Row row = new Row("EnterpriseAppExtractionDetails");
            row.set("ENTERPRISE_APP_FILE_ID", (Object)uploadedFileId);
            row.set("EXTRACTED_META_FILE_ID", (Object)metaFileId);
            final DataObject DO = (DataObject)new WritableDataObject();
            DO.addRow(row);
            if (existingmetaFileId == null) {
                MDMUtil.getPersistence().add(DO);
            }
            else {
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("EnterpriseAppExtractionDetails");
                final Criteria appFile = new Criteria(new Column("EnterpriseAppExtractionDetails", "ENTERPRISE_APP_FILE_ID"), (Object)uploadedFileId, 0);
                updateQuery.setCriteria(appFile);
                updateQuery.setUpdateColumn("EXTRACTED_META_FILE_ID", (Object)metaFileId);
                MDMUtil.getPersistence().update(updateQuery);
            }
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.WARNING, "Unable to save the extracted meta info, not harmfull since it is fail safe.", e);
        }
    }
    
    private Long getExtractedMetaFileId(final Long uploadedFileId) {
        try {
            return (Long)DBUtil.getValueFromDB("EnterpriseAppExtractionDetails", "ENTERPRISE_APP_FILE_ID", (Object)uploadedFileId, "EXTRACTED_META_FILE_ID");
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public JSONObject addAutoAppUpdateConfig(final JSONObject jsonObject) throws Exception {
        JSONObject responseObject = null;
        final String sEventLogRemarks = "dc.mdm.actionlog.appmgmt.autoupdate_added";
        final int eventLogConstant = 2031;
        try {
            final JSONObject messageBody = jsonObject.getJSONObject("msg_body");
            final Long customerId = APIUtil.getCustomerID(jsonObject);
            HashSet<Long> packageList = null;
            HashSet<Long> resourceList = null;
            this.checkForUpdatePolicyLimit(jsonObject);
            final JSONArray packageArr = messageBody.optJSONArray("app_ids");
            final JSONArray resourceArr = messageBody.optJSONArray("resource_ids");
            if (packageArr == null && !messageBody.optBoolean("all_apps", false)) {
                throw new APIHTTPException("COM0009", new Object[] { "app parameter" });
            }
            if (packageArr != null && packageArr.length() == 0) {
                throw new APIHTTPException("COM0009", new Object[] { "app parameter" });
            }
            if (resourceArr == null && !messageBody.optBoolean("all_resources", false)) {
                throw new APIHTTPException("COM0009", new Object[] { "resource parameter" });
            }
            if (resourceArr != null && resourceArr.length() == 0) {
                throw new APIHTTPException("COM0009", new Object[] { "resource parameter" });
            }
            Boolean invalidDevice = Boolean.FALSE;
            Boolean invalidGroup = Boolean.FALSE;
            if (messageBody.optBoolean("notify_user_via_email") && !MDMEnrollmentUtil.getInstance().isMailServerConfigured()) {
                throw new APIHTTPException("ENR0106", new Object[0]);
            }
            if (packageArr != null) {
                packageList = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(messageBody.getJSONArray("app_ids")));
                this.validateIfAppsFound(packageList, customerId);
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AutoUpdateForAllApps")) {
                    this.validateIfNonAccountAppsFound(packageList, customerId);
                }
            }
            if (resourceArr != null) {
                resourceList = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(messageBody.getJSONArray("resource_ids")));
                try {
                    new DeviceFacade().validateIfDevicesExists(resourceList, customerId);
                }
                catch (final APIHTTPException e) {
                    invalidDevice = Boolean.TRUE;
                }
                try {
                    new GroupFacade().validateGroupsIfExists(resourceList, customerId);
                }
                catch (final APIHTTPException e) {
                    invalidGroup = Boolean.TRUE;
                }
                if (invalidDevice && invalidGroup) {
                    throw new APIHTTPException("COM0008", new Object[0]);
                }
            }
            responseObject = this.getInstance(1, jsonObject).addAutoAppUpdateConfig(messageBody);
            final Properties taskProps = new Properties();
            ((Hashtable<String, Long>)taskProps).put("customerId", customerId);
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "InitialAppAutoUpdateTask");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.apps.autoupdate.task.InitialAppAutoUpdateTask", taskInfoMap, taskProps);
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in adding auto app update config", ex.getMessage());
            if (ex instanceof JSONException) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            throw ex;
        }
        final Long customerId2 = APIUtil.getCustomerID(jsonObject);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(eventLogConstant, null, APIUtil.getUserName(jsonObject), sEventLogRemarks, "", customerId2);
        return responseObject;
    }
    
    public void deleteAutoAppUpdateConfig(final JSONObject jsonObject) throws Exception {
        final String sEventLogRemarks = "dc.mdm.actionlog.appmgmt.autoupdate_deleted";
        final int eventLogConstant = 2031;
        try {
            final Long appUpdateConfId = APIUtil.getResourceID(jsonObject, "autoupdate_id");
            final Long customerId = APIUtil.getCustomerID(jsonObject);
            this.validateIfAppUpdateConfFound(appUpdateConfId, customerId);
            this.getInstance(1, jsonObject).deleteAutoAppUpdateConfig(JSONUtil.toJSON("appUpdateConfId", appUpdateConfId));
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in deleting auto app update config", ex);
            if (ex instanceof JSONException) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            throw ex;
        }
        final Long customerId2 = APIUtil.getCustomerID(jsonObject);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(eventLogConstant, null, APIUtil.getUserName(jsonObject), sEventLogRemarks, "", customerId2);
    }
    
    public JSONObject addProvProfileDetails(final JSONObject request, final int platform) throws Exception {
        return this.getInstance(platform, request).addProvProfileForApp(request);
    }
    
    public JSONObject getProvProfileDetailsFromAppId(final JSONObject request, final int platform) throws Exception {
        return this.getInstance(platform, request).getProvProfileDetailsFromAppId(request);
    }
    
    public JSONObject getProvProfileDetails(final JSONObject request, final int platform) throws Exception {
        return this.getInstance(platform, request).getProvProfileDetails(request);
    }
    
    public JSONObject getPrerequsiteForAddApp(final JSONObject requestJSON) throws Exception {
        try {
            final int platform = Integer.parseInt(requestJSON.getJSONObject("msg_header").getJSONObject("filters").get("platform_type").toString());
            return this.getInstance(platform, requestJSON).getPrerequsiteForAddApp(requestJSON);
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in getPrerequsiteForAddApp", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void updatePrerequsiteForAddApp(final JSONObject requestJSON) throws Exception {
        try {
            final int platform = (int)requestJSON.getJSONObject("msg_body").get("platform_type");
            this.getInstance(platform, requestJSON).updatePrerequsiteForAddApp(requestJSON);
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in updatePrerequsiteForAddApp", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject markAppAsStable(final JSONObject apiRequestJson) {
        final JSONObject apiResponseJson = new JSONObject();
        try {
            final Long packageId = APIUtil.getResourceID(apiRequestJson, "app_id");
            final Integer platformType = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "PLATFORM_TYPE");
            if (!apiRequestJson.has("msg_body") || (!apiRequestJson.getJSONObject("msg_body").has("silent_install") && !apiRequestJson.getJSONObject("msg_body").has("distribute_update")) || !apiRequestJson.getJSONObject("msg_body").has("notify_user_via_email")) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            final JSONObject responseJson = this.getInstance(platformType, apiRequestJson).markAppAsStable(apiRequestJson);
            if (!responseJson.has("status") || responseJson.getInt("status") != 200) {
                AppFacade.logger.log(Level.SEVERE, "Response from markAppAsStable method is {0}. Throwing APIHttpException as response does not contain 200 status.", responseJson);
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            AppFacade.logger.log(Level.SEVERE, "Exception in markAppAsStable method", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return apiResponseJson;
    }
    
    public JSONObject getChannelsToMerge(final JSONObject apiRequestJson) throws Exception {
        final Long packageId = APIUtil.getResourceID(apiRequestJson, "app_id");
        final Long customerID = APIUtil.getCustomerID(apiRequestJson);
        this.validateIfAppFound(packageId, customerID);
        final Integer platformType = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "PLATFORM_TYPE");
        final JSONObject responseJSON = this.getInstance(platformType, apiRequestJson).getChannelsToMerge(apiRequestJson);
        return responseJSON;
    }
    
    public JSONObject getAvailableChannels(final JSONObject message) throws APIHTTPException {
        try {
            final Long packageID = APIUtil.getResourceID(message, "app_id");
            final Long customerID = APIUtil.getCustomerID(message);
            this.validateIfAppFound(packageID, customerID);
            final Integer platform = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageID, "PLATFORM_TYPE");
            final JSONObject availableChannelJSON = this.getInstance(platform, message).getAvailableChannels(message);
            return availableChannelJSON;
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in getAvailableChannels method", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject addNewChannel(final JSONObject message) throws APIHTTPException {
        final String userName = APIUtil.getUserName(message);
        String channelName = "";
        try {
            final Long customerID = APIUtil.getCustomerID(message);
            final JSONObject requestJSON = message.has("msg_body") ? message.getJSONObject("msg_body") : new JSONObject();
            if (requestJSON.has("channel_name")) {
                channelName = (String)requestJSON.get("channel_name");
            }
            final Long releaseLabelID = AppVersionDBUtil.getInstance().addChannel(customerID, channelName);
            return new JSONObject().put("release_label_id", (Object)releaseLabelID);
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in addNewChannel method.", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getChannels(final JSONObject message) throws APIHTTPException {
        try {
            return AppVersionDBUtil.getInstance().getChannels(message);
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in getChannels method", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void updateChannel(final JSONObject message) throws APIHTTPException {
        try {
            final Long customerID = APIUtil.getCustomerID(message);
            final Long channelID = APIUtil.getResourceID(message, "label_id");
            this.validateIfReleaseLabelFound(channelID, customerID);
            String channelName;
            try {
                channelName = (String)message.getJSONObject("msg_body").get("channel_name");
            }
            catch (final JSONException ex) {
                throw new APIHTTPException("COM0006", new Object[0]);
            }
            AppVersionDBUtil.getInstance().updateChannel(channelID, channelName);
        }
        catch (final Exception ex2) {
            AppFacade.logger.log(Level.SEVERE, "Exception in updateChannel method", ex2);
            if (ex2 instanceof APIHTTPException) {
                throw (APIHTTPException)ex2;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getChannelDetails(final JSONObject message) {
        try {
            final Long customerID = APIUtil.getCustomerID(message);
            final Long channelID = APIUtil.getResourceID(message, "label_id");
            this.validateIfReleaseLabelFound(channelID, customerID);
            return AppVersionDBUtil.getInstance().getChannelDetails(message);
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in getChannelDetails", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Map validateAndAppDetailsForGroup(final List<Long> packageIds, final List<Long> groupIds, final Long customerId) throws Exception {
        if (packageIds.isEmpty() || groupIds.isEmpty()) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        AppFacade.logger.log(Level.INFO, "validata and get app details for the group with group id : {0} and package ids : {1}", new Object[] { groupIds, packageIds });
        final HashMap<Long, Long> groupProfileMap = new HashMap<Long, Long>();
        final SelectQuery selectQuery = this.getAppGroupQuery(groupIds, customerId);
        Criteria appCriteria = selectQuery.getCriteria();
        appCriteria = appCriteria.and(new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageIds.toArray(), 8));
        selectQuery.setCriteria(appCriteria);
        final DMDataSetWrapper dataSetValue = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final Map<Long, ArrayList<Long>> groupPackMap = new HashMap<Long, ArrayList<Long>>();
        while (dataSetValue.next()) {
            final Long groupId = (Long)dataSetValue.getValue("GROUP_ID");
            final Long groupProfileId = (Long)dataSetValue.getValue("PROFILE_ID");
            final Long groupProfileCollectionId = (Long)dataSetValue.getValue("COLLECTION_ID");
            groupProfileMap.put(groupProfileId, groupProfileCollectionId);
            final Long packageId = (Long)dataSetValue.getValue("PACKAGE_ID");
            if (!groupPackMap.containsKey(groupId)) {
                groupPackMap.put(groupId, new ArrayList<Long>());
            }
            groupPackMap.get(groupId).add(packageId);
        }
        boolean isValid = false;
        for (final Long groupId2 : groupIds) {
            final List<Long> validatePackageIds = new ArrayList<Long>(packageIds);
            final List<Long> availablePackageIds = (groupPackMap.get(groupId2) != null) ? ((List)groupPackMap.get(groupId2)) : new ArrayList<Long>();
            validatePackageIds.removeAll(availablePackageIds);
            if (!validatePackageIds.isEmpty()) {
                isValid = true;
                break;
            }
        }
        if (isValid) {
            throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(packageIds) });
        }
        return groupProfileMap;
    }
    
    public Map validateAndGetAppDetails(final Map<Long, Set<Long>> releaseLabelToPackageId, final Long customerId, final Integer platformType) throws APIHTTPException {
        AppFacade.logger.log(Level.INFO, "validate and get app details for package ids : {0}", releaseLabelToPackageId);
        final HashMap profileCollectionMap = new HashMap();
        final List validatePackageIds = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        final Join packageToCollectionJoin = new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        selectQuery.addJoin(packageToCollectionJoin);
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        Criteria appIdCriteria = null;
        final Set<Long> releaseLabelList = releaseLabelToPackageId.keySet();
        for (final Long releaseLabel : releaseLabelList) {
            final Set<Long> packageList = releaseLabelToPackageId.get(releaseLabel);
            final Criteria currentReleaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabel, 0);
            final Criteria currentPackageCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageList.toArray(new Long[0]), 8);
            if (appIdCriteria == null) {
                appIdCriteria = currentPackageCriteria.and(currentReleaseLabelCriteria);
            }
            else {
                appIdCriteria = appIdCriteria.or(currentPackageCriteria.and(currentReleaseLabelCriteria));
            }
            validatePackageIds.addAll(packageList);
        }
        appIdCriteria = appIdCriteria.and(new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0));
        if (platformType != null) {
            appIdCriteria = appIdCriteria.and(new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0));
        }
        selectQuery.setCriteria(appIdCriteria);
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final List<Long> availablePakageIds = new ArrayList<Long>();
            if (!dataObject.isEmpty()) {
                final Iterator<Row> profileCollectionRows = dataObject.getRows("ProfileToCollection");
                while (profileCollectionRows.hasNext()) {
                    final Row profileCollnRow = profileCollectionRows.next();
                    profileCollectionMap.put(profileCollnRow.get("PROFILE_ID"), profileCollnRow.get("COLLECTION_ID"));
                    final Row packageToAppGroupRow = dataObject.getRow("MdPackageToAppGroup", new Criteria(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), profileCollnRow.get("COLLECTION_ID"), 0), packageToCollectionJoin);
                    final Long packageId = (Long)packageToAppGroupRow.get("PACKAGE_ID");
                    availablePakageIds.add(packageId);
                }
            }
            validatePackageIds.removeAll(availablePakageIds);
        }
        catch (final DataAccessException ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception while fetching details of the app", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        if (!validatePackageIds.isEmpty()) {
            throw new APIHTTPException("COM0008", new Object[] { "App id: " + APIUtil.getCommaSeperatedString(validatePackageIds) });
        }
        return profileCollectionMap;
    }
    
    public JSONObject getGroupApps(final JSONObject message) throws APIHTTPException {
        try {
            final Long groupId = APIUtil.getResourceID(message, "group_id");
            final Long customerId = APIUtil.getCustomerID(message);
            new GroupFacade().validateAndGetGroupDetails(groupId, customerId);
            JSONArray appDetailArr = new JSONArray();
            final String include = APIUtil.optStringFilter(message, "include", "");
            if (include.equalsIgnoreCase("details")) {
                AppFacade.logger.log(Level.INFO, "Get group apps details {0}", groupId);
                final SelectQuery selectQuery = this.getGroupAppSelectQuery(groupId, customerId);
                final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
                while (dataSetWrapper.next()) {
                    final int executionVersion = (int)dataSetWrapper.getValue("PROFILE_VERSION");
                    final String executionVer = (String)dataSetWrapper.getValue("APP_VERSION");
                    final String executionVerCode = (String)dataSetWrapper.getValue("APP_NAME_SHORT_VERSION");
                    int latestVersion = executionVersion;
                    String latestVer = executionVer;
                    String latestVerCode = executionVerCode;
                    if (dataSetWrapper.getValue("LATESTPROFILECOLLN.PROFILE_VERSION") != null) {
                        latestVersion = (int)dataSetWrapper.getValue("LATESTPROFILECOLLN.PROFILE_VERSION");
                        latestVer = (String)dataSetWrapper.getValue("LATESTMDAPPDETAILS.APP_VERSION");
                        latestVerCode = (String)dataSetWrapper.getValue("LATESTMDAPPDETAILS.APP_NAME_SHORT_VERSION");
                    }
                    final String associatedByUserName = (String)dataSetWrapper.getValue("FIRST_NAME");
                    final Long associatedOn = (Long)dataSetWrapper.getValue("ASSOCIATED_TIME");
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("app_id", dataSetWrapper.getValue("PACKAGE_ID"));
                    jsonObject.put("app_name", dataSetWrapper.getValue("PROFILE_NAME"));
                    jsonObject.put("app_type", dataSetWrapper.getValue("PACKAGE_TYPE"));
                    jsonObject.put("platform_type", dataSetWrapper.getValue("PLATFORM_TYPE"));
                    jsonObject.put("app_category", dataSetWrapper.getValue("APP_CATEGORY_NAME"));
                    jsonObject.put("is_paid_app", dataSetWrapper.getValue("IS_PAID_APP"));
                    jsonObject.put("status", dataSetWrapper.getValue("STATUS"));
                    jsonObject.put("remarks", (Object)I18N.getMsg(String.valueOf(dataSetWrapper.getValue("REMARKS")), new Object[0]));
                    jsonObject.put("associated_by", (Object)associatedByUserName);
                    jsonObject.put("associated_on", (Object)associatedOn);
                    jsonObject.put("latest_version", (Object)latestVer);
                    jsonObject.put("executed_version", (Object)executionVer);
                    jsonObject.put("latest_version_code", (Object)latestVerCode);
                    jsonObject.put("executed_version_code", (Object)executionVerCode);
                    if (dataSetWrapper.getValue("DISPLAY_IMAGE_LOC") != null) {
                        final String displayImageLoc = String.valueOf(dataSetWrapper.getValue("DISPLAY_IMAGE_LOC"));
                        jsonObject.put("DisplayImageUrl", (Object)displayImageLoc);
                    }
                    if (executionVersion < latestVersion) {
                        jsonObject.put("isLatestVer", false);
                    }
                    else {
                        jsonObject.put("isLatestVer", true);
                    }
                    final JSONObject releaseLabelDetails = new JSONObject();
                    releaseLabelDetails.put("release_label_id", (Object)String.valueOf(dataSetWrapper.getValue("RELEASE_LABEL_ID")));
                    releaseLabelDetails.put("release_label_name", (Object)I18N.getMsg(String.valueOf(dataSetWrapper.getValue("RELEASE_LABEL_DISPLAY_NAME")), new Object[0]));
                    jsonObject.put("release_label_details", (Object)releaseLabelDetails);
                    appDetailArr.put((Object)jsonObject);
                }
                appDetailArr = MDMRestAPIFactoryProvider.getAppsUtilAPI().convertFilePath(appDetailArr);
            }
            else {
                AppFacade.logger.log(Level.INFO, "Get group apps group id {0}", groupId);
                final Map<Long, List<Map<String, Object>>> appDetailsMap = this.getAppDetailsForGroup(Arrays.asList(groupId), customerId);
                final List<Map<String, Object>> appDetailList = appDetailsMap.get(groupId);
                if (appDetailList != null) {
                    for (final Map<String, Object> appDetail : appDetailList) {
                        final JSONObject appDetailJSON = new JSONObject();
                        appDetailJSON.put("app_id", (Object)String.valueOf(appDetail.get("PACKAGE_ID")));
                        appDetailJSON.put("release_label_id", (Object)String.valueOf(appDetail.get("RELEASE_LABEL_ID")));
                        appDetailJSON.put("release_label_name", appDetail.get("RELEASE_LABEL_DISPLAY_NAME"));
                        appDetailArr.put((Object)appDetailJSON);
                    }
                }
            }
            final JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("app_details", (Object)appDetailArr);
            return jsonObject2;
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.SEVERE, "error in getGroupProfileDetail()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Map<Long, List<Map<String, Object>>> getAppDetailsForGroup(final List<Long> groupIds, final Long customerId) throws Exception {
        AppFacade.logger.log(Level.INFO, "get App details for the groups : {0}", groupIds);
        final SelectQuery selectQuery = this.getAppGroupQuery(groupIds, customerId);
        selectQuery.addJoin(new Join("RecentProfileForGroup", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
        selectQuery.addJoin(new Join("AppCollnToReleaseLabelHistory", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("AppReleaseLabel", "CUSTOMER_ID"), (Object)customerId, 0)));
        final List<Column> groupList = new ArrayList<Column>(selectQuery.getSelectColumns());
        final GroupByClause gr = new GroupByClause((List)groupList);
        selectQuery.setGroupByClause(gr);
        final DMDataSetWrapper dataSetValue = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final Map<Long, List<Map<String, Object>>> groupPackMap = new HashMap<Long, List<Map<String, Object>>>();
        while (dataSetValue.next()) {
            final Long groupId = (Long)dataSetValue.getValue("GROUP_ID");
            final Long packageId = (Long)dataSetValue.getValue("PACKAGE_ID");
            final Long releaseLabelId = (Long)dataSetValue.getValue("RELEASE_LABEL_ID");
            final String releaseLabelName = I18N.getMsg((String)dataSetValue.getValue("RELEASE_LABEL_DISPLAY_NAME"), new Object[0]);
            final Map<String, Object> appDetails = new HashMap<String, Object>();
            appDetails.put("PACKAGE_ID", packageId);
            appDetails.put("RELEASE_LABEL_ID", releaseLabelId);
            appDetails.put("RELEASE_LABEL_DISPLAY_NAME", releaseLabelName);
            if (!groupPackMap.containsKey(groupId)) {
                final List<Map<String, Object>> appDetailList = new ArrayList<Map<String, Object>>();
                groupPackMap.put(groupId, appDetailList);
            }
            final List<Map<String, Object>> appDetailList = groupPackMap.get(groupId);
            appDetailList.add(appDetails);
            groupPackMap.put(groupId, appDetailList);
        }
        return groupPackMap;
    }
    
    public JSONObject getGroupAppDetail(final JSONObject message) throws APIHTTPException {
        try {
            final Long groupId = APIUtil.getResourceID(message, "group_id");
            final Long customerId = APIUtil.getCustomerID(message);
            new GroupFacade().validateAndGetGroupDetails(groupId, customerId);
            final Long packageId = APIUtil.getResourceID(message, "app_id");
            AppFacade.logger.log(Level.INFO, "Get group app details group id {0} and app package id {1}", new Object[] { groupId, packageId });
            final JSONObject jsonObject = this.validateAndGetIfAppinGroup(packageId, groupId, customerId);
            return jsonObject;
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.SEVERE, "error in getGroupAppDetail()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public SelectQuery getAppGroupQuery(final List groupIds, final Long customerId) {
        AppFacade.logger.log(Level.INFO, "get App group query group ids: {0}", groupIds);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        Criteria appCriteria = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupIds.toArray(), 8);
        appCriteria = appCriteria.and(new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0));
        selectQuery.setCriteria(appCriteria);
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        return selectQuery;
    }
    
    public JSONObject validateAndGetIfAppinGroup(final Long packageId, final Long groupId, final Long customerId) throws Exception {
        if (packageId == null || packageId == -1L || groupId == null || groupId == -1L) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        AppFacade.logger.log(Level.INFO, "validate and get app avalable in group app id: {0} group id : {1}", new Object[] { packageId, groupId });
        final Long profileId = AppsUtil.getInstance().getProfileIdForPackage(packageId, customerId);
        final SelectQuery appGroupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        appGroupQuery.addJoin(new Join("RecentProfileForGroup", "ProfileToCollection", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 2));
        appGroupQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        appGroupQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        appGroupQuery.addJoin(new Join("RecentProfileForGroup", "CollnToResources", new String[] { "COLLECTION_ID", "GROUP_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdAppGroupDetails", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 2));
        appGroupQuery.addJoin(new Join("RecentProfileForGroup", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appGroupQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
        appGroupQuery.addJoin(new Join("AppCollnToReleaseLabelHistory", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), 0);
        final Criteria groupIDCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)groupId, 0);
        appGroupQuery.addJoin(new Join("RecentProfileForGroup", "MdAppCatalogToGroup", appGroupCriteria.and(groupIDCriteria), 2));
        appGroupQuery.addJoin(new Join("MdAppCatalogToGroup", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToGroup", "LatestAppColln", 2));
        appGroupQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "LatestAppColln", "LatestProf", 2));
        appGroupQuery.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, "LatestAppColln", "LatestApp", 2));
        appGroupQuery.addJoin(new Join("RecentProfileForGroup", "GroupToProfileHistory", new String[] { "GROUP_ID", "PROFILE_ID", "COLLECTION_ID" }, new String[] { "GROUP_ID", "PROFILE_ID", "COLLECTION_ID" }, 2));
        appGroupQuery.addJoin(new Join("GroupToProfileHistory", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, 2));
        final Criteria groupCriteria = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID"), (Object)groupId, 0);
        final Criteria profileIdCriteria = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileId, 0);
        appGroupQuery.setCriteria(groupCriteria.and(profileIdCriteria));
        appGroupQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
        appGroupQuery.addSelectColumn(Column.getColumn("AppCategory", "APP_CATEGORY_NAME"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "ASSOCIATED_BY"));
        appGroupQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "ASSOCIATED_TIME"));
        appGroupQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "LAST_MODIFIED_TIME"));
        appGroupQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME"));
        appGroupQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("CollnToResources", "STATUS"));
        appGroupQuery.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS"));
        appGroupQuery.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS_EN"));
        appGroupQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        appGroupQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        appGroupQuery.addSelectColumn(Column.getColumn("LatestApp", "APP_VERSION", "LATESTMDAPPDETAILS.APP_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("LatestApp", "APP_NAME_SHORT_VERSION", "LATESTMDAPPDETAILS.APP_NAME_SHORT_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("LatestProf", "PROFILE_VERSION", "LATESTPROFILECOLLN.PROFILE_VERSION"));
        final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)appGroupQuery);
        if (!dataSetWrapper.next()) {
            final String remark = "App Id : " + packageId.toString();
            throw new APIHTTPException("COM0008", new Object[] { remark });
        }
        final int latestVersion = (int)dataSetWrapper.getValue("LATESTPROFILECOLLN.PROFILE_VERSION");
        final int executionVersion = (int)dataSetWrapper.getValue("PROFILE_VERSION");
        final String latestVer = (String)dataSetWrapper.getValue("LATESTMDAPPDETAILS.APP_VERSION");
        final String executionVer = (String)dataSetWrapper.getValue("APP_VERSION");
        final String latestVerCode = (String)dataSetWrapper.getValue("LATESTMDAPPDETAILS.APP_NAME_SHORT_VERSION");
        final String executionVerCode = (String)dataSetWrapper.getValue("APP_NAME_SHORT_VERSION");
        final String associatedByUserName = (String)dataSetWrapper.getValue("FIRST_NAME");
        final Long associatedOn = (Long)dataSetWrapper.getValue("LAST_MODIFIED_TIME");
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("app_id", dataSetWrapper.getValue("PACKAGE_ID"));
        jsonObject.put("app_name", dataSetWrapper.getValue("PROFILE_NAME"));
        jsonObject.put("app_type", dataSetWrapper.getValue("PACKAGE_TYPE"));
        jsonObject.put("platform_type", dataSetWrapper.getValue("PLATFORM_TYPE"));
        jsonObject.put("app_category", dataSetWrapper.getValue("APP_CATEGORY_NAME"));
        jsonObject.put("is_paid_app", dataSetWrapper.getValue("IS_PAID_APP"));
        jsonObject.put("status", dataSetWrapper.getValue("STATUS"));
        jsonObject.put("remarks", (Object)I18N.getMsg(String.valueOf(dataSetWrapper.getValue("REMARKS")), new Object[0]));
        jsonObject.put("associated_by", (Object)associatedByUserName);
        jsonObject.put("associated_on", (Object)associatedOn);
        jsonObject.put("latest_version", (Object)latestVer);
        jsonObject.put("executed_version", (Object)executionVer);
        jsonObject.put("latest_version_code", (Object)latestVerCode);
        jsonObject.put("executed_version_code", (Object)executionVerCode);
        if (executionVersion < latestVersion) {
            jsonObject.put("isLatestVer", false);
        }
        else {
            jsonObject.put("isLatestVer", true);
        }
        final JSONObject releaseLabelDetails = new JSONObject();
        releaseLabelDetails.put("release_label_id", (Object)String.valueOf(dataSetWrapper.getValue("RELEASE_LABEL_ID")));
        releaseLabelDetails.put("release_label_name", (Object)I18N.getMsg(String.valueOf(dataSetWrapper.getValue("RELEASE_LABEL_DISPLAY_NAME")), new Object[0]));
        jsonObject.put("release_label_details", (Object)releaseLabelDetails);
        return jsonObject;
    }
    
    public Map validateAndAppDetailsForDevice(final List<Long> packageIds, final List<Long> deviceIds, final Long customerId) throws Exception {
        if (deviceIds.isEmpty() || packageIds.isEmpty()) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        AppFacade.logger.log(Level.INFO, "validate and get app details for devices packge ids {0} and device ids {1}", new Object[] { packageIds, deviceIds });
        final HashMap<Long, Long> deviceProfileMap = new HashMap<Long, Long>();
        final SelectQuery selectQuery = this.getAppDeviceQuery(deviceIds, customerId);
        Criteria appCriteria = selectQuery.getCriteria();
        appCriteria = appCriteria.and(new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageIds.toArray(), 8));
        selectQuery.setCriteria(appCriteria);
        final DMDataSetWrapper dataSetValue = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final Map<Long, ArrayList<Long>> devicePackMap = new HashMap<Long, ArrayList<Long>>();
        while (dataSetValue.next()) {
            final Long deviceId = (Long)dataSetValue.getValue("RESOURCE_ID");
            final Long deviceProfileId = (Long)dataSetValue.getValue("PROFILE_ID");
            final Long deviceProfileCollectionId = (Long)dataSetValue.getValue("COLLECTION_ID");
            deviceProfileMap.put(deviceProfileId, deviceProfileCollectionId);
            final Long packageId = (Long)dataSetValue.getValue("PACKAGE_ID");
            if (!devicePackMap.containsKey(deviceId)) {
                devicePackMap.put(deviceId, new ArrayList<Long>());
            }
            devicePackMap.get(deviceId).add(packageId);
        }
        for (final Long deviceId2 : deviceIds) {
            final List<Long> validatePackageIds = new ArrayList<Long>(packageIds);
            final List<Long> availablePackageIds = (devicePackMap.get(deviceId2) != null) ? ((List)devicePackMap.get(deviceId2)) : new ArrayList<Long>();
            validatePackageIds.removeAll(availablePackageIds);
            if (!validatePackageIds.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(validatePackageIds) });
            }
        }
        return deviceProfileMap;
    }
    
    public SelectQuery getAppDeviceQuery(final List deviceIds, final Long customerId) {
        AppFacade.logger.log(Level.INFO, "Method to get App device query with app ids {0}", deviceIds);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        Criteria appCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceIds.toArray(), 8);
        appCriteria = appCriteria.and(new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0));
        selectQuery.setCriteria(appCriteria);
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        return selectQuery;
    }
    
    public JSONObject getDeviceAppDetail(final JSONObject message) throws APIHTTPException {
        try {
            Long deviceId = APIUtil.getResourceID(message, "device_id");
            if (deviceId == -1L) {
                final String udid = APIUtil.getResourceIDString(message, "udid");
                deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            final Long customerId = APIUtil.getCustomerID(message);
            new DeviceFacade().validateIfDeviceExists(deviceId, customerId);
            final Long appId = APIUtil.getResourceID(message, "app_id");
            AppFacade.logger.log(Level.INFO, "Method to get Device App Details  device id :{0} and App Id: {1}", new Object[] { deviceId, appId });
            final JSONObject jsonObject = this.validateAndGetIfAppinDevice(appId, deviceId, customerId);
            return jsonObject;
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.SEVERE, "error in getDeviceAppDetail()", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject validateAndGetIfAppinDevice(final Long appId, final Long deviceId, final Long customerId) throws Exception {
        if (appId == null || appId == -1L || deviceId == null || deviceId == -1L) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        AppFacade.logger.log(Level.INFO, "validate and get if app:{0} in device :{1}", new Object[] { appId, deviceId });
        final SelectQuery appGroupQuery = this.getAppDeviceQuery(Arrays.asList(deviceId), customerId);
        appGroupQuery.addJoin(new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        appGroupQuery.addJoin(new Join("RecentProfileForResource", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appGroupQuery.addJoin(new Join("RecentProfileForResource", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appGroupQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
        appGroupQuery.addJoin(new Join("AppCollnToReleaseLabelHistory", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdAppGroupDetails", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), 0);
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)deviceId, 0);
        appGroupQuery.addJoin(new Join("RecentProfileForResource", "MdAppCatalogToResource", appGroupCriteria.and(resourceCriteria), 2));
        appGroupQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToResource", "LATESTMDAPPTOCOLLN", 2));
        appGroupQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "LATESTMDAPPTOCOLLN", "LATESTPROFILECOLLN", 2));
        appGroupQuery.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, "LATESTMDAPPTOCOLLN", "LATESTMDAPPDETAILS", 2));
        appGroupQuery.addJoin(new Join("RecentProfileForResource", "ResourceToProfileHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appGroupQuery.addJoin(new Join("ResourceToProfileHistory", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, 2));
        appGroupQuery.addJoin(new Join("RecentProfileForResource", "CollnToResources", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        appGroupQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_TYPE"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
        appGroupQuery.addSelectColumn(Column.getColumn("AppCategory", "APP_CATEGORY_NAME"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("LATESTMDAPPDETAILS", "APP_VERSION", "LATESTMDAPPDETAILS.APP_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("LATESTMDAPPDETAILS", "APP_NAME_SHORT_VERSION", "LATESTMDAPPDETAILS.APP_NAME_SHORT_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("ResourceToProfileHistory", "ASSOCIATED_BY"));
        appGroupQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME"));
        appGroupQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("LATESTPROFILECOLLN", "PROFILE_VERSION", "LATESTPROFILECOLLN.PROFILE_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("CollnToResources", "STATUS"));
        appGroupQuery.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS"));
        appGroupQuery.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS_EN"));
        appGroupQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        appGroupQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        appGroupQuery.setGroupByClause(new GroupByClause(appGroupQuery.getSelectColumns()));
        final Column assignedTimeColumn = Column.getColumn("ResourceToProfileHistory", "ASSOCIATED_TIME").maximum();
        assignedTimeColumn.setColumnAlias("ASSOCIATED_TIME");
        appGroupQuery.addSelectColumn(assignedTimeColumn);
        Criteria packageCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)appId, 0);
        packageCriteria = packageCriteria.and(appGroupQuery.getCriteria());
        appGroupQuery.setCriteria(packageCriteria);
        final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)appGroupQuery);
        if (!dataSetWrapper.next()) {
            final String remark = "App Id : " + appId.toString();
            throw new APIHTTPException("COM0008", new Object[] { remark });
        }
        final int latestVersion = (int)dataSetWrapper.getValue("LATESTPROFILECOLLN.PROFILE_VERSION");
        final int executionVersion = (int)dataSetWrapper.getValue("PROFILE_VERSION");
        final String latestVer = (String)dataSetWrapper.getValue("LATESTMDAPPDETAILS.APP_VERSION");
        final String executionVer = (String)dataSetWrapper.getValue("APP_VERSION");
        final String latestVerCode = (String)dataSetWrapper.getValue("LATESTMDAPPDETAILS.APP_NAME_SHORT_VERSION");
        final String executionVerCode = (String)dataSetWrapper.getValue("APP_NAME_SHORT_VERSION");
        final Long associatedByUser = (Long)dataSetWrapper.getValue("ASSOCIATED_BY");
        final String associatedByUserName = (String)dataSetWrapper.getValue("FIRST_NAME");
        final Long associatedOn = (Long)dataSetWrapper.getValue("ASSOCIATED_TIME");
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("app_id", dataSetWrapper.getValue("PACKAGE_ID"));
        jsonObject.put("app_name", dataSetWrapper.getValue("PROFILE_NAME"));
        jsonObject.put("app_type", dataSetWrapper.getValue("APP_TYPE"));
        jsonObject.put("platform_type", dataSetWrapper.getValue("PLATFORM_TYPE"));
        jsonObject.put("app_category", dataSetWrapper.getValue("APP_CATEGORY_NAME"));
        jsonObject.put("is_paid_app", dataSetWrapper.getValue("IS_PAID_APP"));
        jsonObject.put("associated_by", (Object)associatedByUserName);
        jsonObject.put("associated_on", (Object)associatedOn);
        jsonObject.put("latest_version", (Object)latestVer);
        jsonObject.put("executed_version", (Object)executionVer);
        jsonObject.put("latest_version_code", (Object)latestVerCode);
        jsonObject.put("executed_version_code", (Object)executionVerCode);
        jsonObject.put("status", dataSetWrapper.getValue("STATUS"));
        jsonObject.put("remark", (Object)MDMI18N.getMsg(String.valueOf(dataSetWrapper.getValue("REMARKS")), true));
        jsonObject.put("localized_remark", (Object)MDMI18N.getMsg(String.valueOf(dataSetWrapper.getValue("REMARKS_EN")), true));
        if (executionVersion < latestVersion) {
            jsonObject.put("isLatestVer", false);
        }
        else {
            jsonObject.put("isLatestVer", true);
        }
        final JSONObject releaseLabelDetails = new JSONObject();
        releaseLabelDetails.put("release_label_id", (Object)String.valueOf(dataSetWrapper.getValue("RELEASE_LABEL_ID")));
        releaseLabelDetails.put("release_label_name", (Object)I18N.getMsg(String.valueOf(dataSetWrapper.getValue("RELEASE_LABEL_DISPLAY_NAME")), new Object[0]));
        jsonObject.put("release_label_details", (Object)releaseLabelDetails);
        return jsonObject;
    }
    
    public JSONObject getAutoAppUpdateInfoForApp(final JSONObject message) throws Exception {
        try {
            final Long packageId = APIUtil.getResourceID(message, "app_id");
            final Integer platform = (Integer)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "PLATFORM_TYPE");
            if (platform == null || platform <= 0) {
                throw new APIHTTPException("COM0008", new Object[] { packageId });
            }
            final Long customerIdFromPackage = (Long)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "CUSTOMER_ID");
            final Long customerId = APIUtil.getCustomerID(message);
            if (!customerId.equals(customerIdFromPackage)) {
                throw new APIHTTPException("COM0008", new Object[] { packageId });
            }
            return this.getInstance(platform, message).getAutoAppUpdateInfoForApp(message);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
    }
    
    public JSONObject getAutoAppUpdateConfigList(final JSONObject jsonObject) throws Exception {
        try {
            final Long appUpdateConfId = APIUtil.getResourceID(jsonObject, "autoupdate_id");
            final Long customerId = APIUtil.getCustomerID(jsonObject);
            JSONObject responseJSON;
            if (appUpdateConfId != -1L) {
                this.validateIfAppUpdateConfFound(appUpdateConfId, customerId);
                final JSONObject message = JSONUtil.toJSON("appUpdateConfId", appUpdateConfId);
                responseJSON = this.getInstance(1, jsonObject).getAutoAppUpdateConfig(message);
            }
            else {
                responseJSON = this.getInstance(1, jsonObject).getAutoAppUpdateConfigList();
            }
            return responseJSON;
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in getting auto app update config", ex);
            if (ex instanceof JSONException) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            throw ex;
        }
    }
    
    private void validateIfNonAccountAppsFound(Collection<Long> appset, final Long customerID) throws APIHTTPException {
        if (appset.isEmpty()) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        try {
            appset = new HashSet<Long>(appset);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
            selectQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria criteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)appset.toArray(), 8).and(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria accountCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)Boolean.TRUE, 0);
            final Criteria iosStoreAppCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)1, 0).and(new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)new int[] { 0, 1 }, 8));
            selectQuery.setCriteria(criteria.and(accountCriteria.or(iosStoreAppCriteria)));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator<Row> rows = dataObject.getRows("MdPackage");
            final ArrayList<Long> apps = new ArrayList<Long>();
            while (rows.hasNext()) {
                apps.add(Long.valueOf(String.valueOf(rows.next().get("PACKAGE_ID"))));
            }
            appset.removeAll(apps);
            if (appset.size() > 0) {
                throw new APIHTTPException("APP0021", new Object[] { APIUtil.getCommaSeperatedString(appset) });
            }
        }
        catch (final DataAccessException e) {
            AppFacade.logger.log(Level.SEVERE, "Exception in validating Non account Apps", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void updateAutoAppUpdateConfig(final JSONObject jsonObject) throws Exception {
        final String sEventLogRemarks = "dc.mdm.actionlog.appmgmt.autoupdate_updated";
        final int eventLogConstant = 2031;
        try {
            final Long appUpdateConfId = APIUtil.getResourceID(jsonObject, "autoupdate_id");
            final Long customerId = APIUtil.getCustomerID(jsonObject);
            this.validateIfAppUpdateConfFound(appUpdateConfId, customerId);
            HashSet<Long> resourceList = null;
            HashSet<Long> packageList = null;
            final JSONObject messageBody = jsonObject.getJSONObject("msg_body");
            final JSONArray resourceArr = messageBody.optJSONArray("resource_ids");
            final JSONArray packageArr = messageBody.optJSONArray("app_ids");
            if (messageBody.optBoolean("notify_user_via_email") && !MDMEnrollmentUtil.getInstance().isMailServerConfigured()) {
                throw new APIHTTPException("ENR0106", new Object[0]);
            }
            if (resourceArr != null) {
                resourceList = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(messageBody.getJSONArray("resource_ids")));
                final List<Long> remainingList = this.validateGroupIds(resourceList, customerId);
                if (remainingList != null && !remainingList.isEmpty()) {
                    final List invalidIds = this.validateDeviceIds(new HashSet<Long>(remainingList), customerId);
                    if (invalidIds != null && !invalidIds.isEmpty()) {
                        throw new APIHTTPException("COM0008", invalidIds.toArray());
                    }
                }
            }
            if (packageArr != null) {
                packageList = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(messageBody.getJSONArray("app_ids")));
                this.validateIfAppsFound(packageList, customerId);
                if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AutoUpdateForAllApps")) {
                    this.validateIfNonAccountAppsFound(packageList, customerId);
                }
            }
            this.getInstance(1, jsonObject).updateAutoAppUpdateConfig(jsonObject.getJSONObject("msg_body").put("appUpdateConfId", (Object)appUpdateConfId));
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in updating auto app update config", ex);
            if (ex instanceof JSONException) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            throw ex;
        }
        final Long customerId2 = APIUtil.getCustomerID(jsonObject);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(eventLogConstant, null, APIUtil.getUserName(jsonObject), sEventLogRemarks, "", customerId2);
    }
    
    private boolean validateIfAppUpdateConfFound(final Long confId, final Long customerId) throws Exception {
        if (confId == null || confId == -1L) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdateConfigDetails"));
            selectQuery.addSelectColumn(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"));
            final Criteria custcriteria = new Criteria(new Column("AutoAppUpdateConfigDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria appUpdtConfCritiera = new Criteria(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"), (Object)confId, 0);
            selectQuery.setCriteria(custcriteria.and(appUpdtConfCritiera));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (dataObject.isEmpty()) {
                throw new APIHTTPException("COM0008", new Object[] { confId });
            }
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.SEVERE, "Exception in checking auto update config", e);
            throw e;
        }
        return true;
    }
    
    private void checkForUpdatePolicyLimit(final JSONObject jsonObject) throws Exception {
        final JSONObject configList = this.getInstance(1, jsonObject).getAutoAppUpdateConfigList();
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AutoUpdatePolicyUnLimit") && configList != null && configList.optJSONArray("config_list") != null && configList.getJSONArray("config_list").length() > 0) {
            AppFacade.logger.log(Level.SEVERE, "The no. of app update policy is restricted");
            throw new APIHTTPException("COM0015", new Object[] { "Unable to create a new policy! MDM supports only one app update policy." });
        }
    }
    
    public JSONObject getAppDistributionSettings(final JSONObject message) throws APIHTTPException {
        try {
            AppFacade.logger.log(Level.FINE, "Get App Distribution settings");
            final Integer platformtype = APIUtil.getIntegerFilter(message, "platform_type");
            final Long customerID = APIUtil.getCustomerID(message);
            final Properties properties = AppsUtil.getInstance().getAppSettings(customerID);
            final JSONObject jsonObject = new JSONObject();
            if (platformtype == 1) {
                final Integer distributionType = VPPAppMgmtHandler.getInstance().getVppGlobalAssignmentType(customerID);
                jsonObject.put("vpp_distribution_type", (Object)distributionType);
            }
            jsonObject.put("show_silent_install", (Object)this.showSilentInstallOption());
            jsonObject.put("is_silent_install", ((Hashtable<K, Object>)properties).get("isSilentInstall"));
            jsonObject.put("is_notify", ((Hashtable<K, Object>)properties).get("isNotify"));
            return jsonObject;
        }
        catch (final JSONException ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception while getting app distribution setting", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private Boolean showSilentInstallOption() {
        Boolean showSilentInstallOption = Boolean.TRUE;
        final Boolean checkIfSilentInstallAllowedForUser = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("checkIfSilentInstallAllowedForUser");
        if (checkIfSilentInstallAllowedForUser != null && checkIfSilentInstallAllowedForUser) {
            final Long loginId = DMUserHandler.getLoginId();
            if (!DMUserHandler.isUserInAdminRole(loginId) && !DMUserHandler.isUserInRole(loginId, "MDM_AppMgmt_Admin") && !DMUserHandler.isUserInRole(loginId, "ModernMgmt_AppMgmt_Admin")) {
                showSilentInstallOption = Boolean.FALSE;
            }
        }
        return showSilentInstallOption;
    }
    
    public JSONObject validateAppBeforeDelete(final JSONObject message) throws APIHTTPException {
        try {
            final Long customerId = APIUtil.getCustomerID(message);
            final JSONObject requestBody = message.getJSONObject("msg_body");
            final List packageIds = JSONUtil.getInstance().convertLongJSONArrayTOList(requestBody.getJSONArray("app_ids"));
            final Map<Long, Set<Long>> releaseLabelToPackageList = AppVersionDBUtil.getInstance().getApprovedReleaseLabelToPackageMap(packageIds);
            final Map profileCollectionMap = this.validateAndGetAppDetails(releaseLabelToPackageList, APIUtil.getCustomerID(message), null);
            AppFacade.logger.log(Level.INFO, "Validate app before delete with app id : {0}", packageIds);
            final List appCollectionIds = new ArrayList(profileCollectionMap.values());
            final JSONArray appCollectionArray = JSONUtil.getInstance().convertListToJSONArray(appCollectionIds);
            final JSONArray resourceIds = requestBody.getJSONArray("resource_ids");
            final Boolean isGroup = requestBody.optBoolean("is_group");
            final JSONObject data = new JSONObject();
            data.put("collectionIds", (Object)appCollectionArray);
            data.put("resourceIds", (Object)resourceIds);
            data.put("isGroup", (Object)isGroup);
            data.put("customerId", (Object)customerId);
            data.put("isAPI", true);
            final ValidateRemoveAppHandler handler = new ValidateRemoveAppHandler();
            final JSONObject responseData = handler.validateData(data);
            return responseData;
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.SEVERE, "Exception in getting device details", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Properties getPlatformToPackageList(final List packageIDs) {
        Properties platformToPackageIDs = new Properties();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            final Criteria packageCrit = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageIDs.toArray(), 8);
            selectQuery.setCriteria(packageCrit);
            selectQuery.addSelectColumn(Column.getColumn("MdPackage", "PACKAGE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackage", "PLATFORM_TYPE"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iter = dataObject.getRows("MdPackage");
            while (iter.hasNext()) {
                final Row packageRow = iter.next();
                final Long packageID = (Long)packageRow.get("PACKAGE_ID");
                final int platformType = (int)packageRow.get("PLATFORM_TYPE");
                if (platformToPackageIDs != null) {
                    List packageList = ((Hashtable<K, List>)platformToPackageIDs).get(platformType);
                    if (packageList == null) {
                        packageList = new ArrayList();
                    }
                    if (!packageList.contains(packageID)) {
                        packageList.add(packageID);
                    }
                    ((Hashtable<Integer, List>)platformToPackageIDs).put(platformType, packageList);
                }
                else {
                    platformToPackageIDs = new Properties();
                    final List packageList = new ArrayList();
                    packageList.add(packageID);
                    ((Hashtable<Integer, Long>)platformToPackageIDs).put(platformType, packageID);
                }
            }
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.SEVERE, "Exception in getPlatformToPackageList", e);
        }
        return platformToPackageIDs;
    }
    
    public JSONObject fillProfileCollectionIdForAppPackageReleaseLabel(final JSONObject apiRequestJson) throws JSONException, APIHTTPException {
        Long packageId = APIUtil.getResourceID(apiRequestJson, "app_id");
        if (packageId == -1L) {
            packageId = APIUtil.getResourceID(apiRequestJson, "distribute_app_id");
        }
        final Long customerId = APIUtil.getCustomerID(apiRequestJson);
        this.validateIfAppFound(packageId, customerId);
        final Long releaseLabelId = APIUtil.getResourceID(apiRequestJson, "label_id");
        try {
            final JSONObject profileCollnJSON = AppVersionDBUtil.getInstance().getCollectionForPackageAndReleaseLabel(new JSONObject().put("PACKAGE_ID", (Object)packageId).put("RELEASE_LABEL_ID", (Object)releaseLabelId));
            if (!profileCollnJSON.has("COLLECTION_ID")) {
                throw new APIHTTPException("COM0008", new Object[] { "app_id:" + packageId + ", label_id:" + releaseLabelId });
            }
            final Long profileId = profileCollnJSON.getLong("PROFILE_ID");
            final Long collectionId = profileCollnJSON.getLong("COLLECTION_ID");
            apiRequestJson.getJSONObject("msg_header").getJSONObject("resource_identifier").put("distribute_profile_id", (Object)profileId);
            apiRequestJson.getJSONObject("msg_header").getJSONObject("resource_identifier").put("collection_id", (Object)collectionId);
        }
        catch (final DataAccessException ex) {
            AppFacade.logger.log(Level.SEVERE, "DataAccessException in fillProfileCollectionIdForAppPackageReleaseLabel", (Throwable)ex);
        }
        return apiRequestJson;
    }
    
    public Map<Long, Set<Long>> convertAppDetailsArrayToHashMap(final JSONArray appDetails) throws JSONException {
        final Map<Long, Set<Long>> releaseLabelToPackageList = new HashMap<Long, Set<Long>>();
        final List<Long> allPackageList = new ArrayList<Long>();
        for (int idx = 0; idx < appDetails.length(); ++idx) {
            final JSONObject appDetail = appDetails.getJSONObject(idx);
            final Long appId = JSONUtil.optLongForUVH(appDetail, "app_id", Long.valueOf(-1L));
            final Long releaseLabelId = JSONUtil.optLongForUVH(appDetail, "release_label_id", Long.valueOf(-1L));
            if (!appId.equals(-1L) && !releaseLabelId.equals(-1L)) {
                Set<Long> packageList = releaseLabelToPackageList.get(releaseLabelId);
                if (packageList == null) {
                    packageList = new HashSet<Long>();
                }
                if (allPackageList.contains(appId)) {
                    throw new APIHTTPException("APP0016", new Object[] { String.valueOf(appId) });
                }
                allPackageList.add(appId);
                packageList.add(appId);
                releaseLabelToPackageList.put(releaseLabelId, packageList);
            }
        }
        return releaseLabelToPackageList;
    }
    
    public JSONObject getAppAccountRemovalMsg(final JSONObject apiRequest) throws Exception {
        final int platformType = APIUtil.getIntegerFilter(apiRequest, "platform");
        return this.getInstance(platformType, apiRequest).verifyAccountRemoval();
    }
    
    public JSONObject updateAppsSyncScheduler(final JSONObject apiRequest) throws APIHTTPException {
        final JSONObject msgBody = apiRequest.optJSONObject("msg_body");
        final Long customerId = APIUtil.getCustomerID(apiRequest);
        final Long userId = APIUtil.getUserID(apiRequest);
        final String userName = APIUtil.getUserName(apiRequest);
        final int eventLogConstant = 2040;
        if (msgBody == null || msgBody.length() <= 0) {
            throw new APIHTTPException("COM0009", new Object[0]);
        }
        JSONObject responseObject;
        try {
            msgBody.put("customerId", (Object)customerId);
            msgBody.put("userId", (Object)userId);
            msgBody.put("userName", (Object)userName);
            if (!GoogleForWorkSettings.isAFWSettingsConfigured(customerId) && !GoogleForWorkSettings.isGoogleForWorkSettingsConfigured(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT) && !new WpAppSettingsHandler().isBstoreConfigured(customerId) && !new VPPTokenDataHandler().isVppTokenConfigured(customerId)) {
                throw new APIHTTPException("APP0020", new Object[0]);
            }
            responseObject = AppsUtil.getInstance().updateAppsSyncScheduler(msgBody);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            AppFacade.logger.log(Level.SEVERE, "Exception on updating the scheduler", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        final String sEventLogRemarks = "mdm.actionlog.appmgmt.scheduler_update_success";
        MDMEventLogHandler.getInstance().MDMEventLogEntry(eventLogConstant, null, userName, sEventLogRemarks, "", customerId);
        return responseObject;
    }
    
    public JSONObject getAppsSyncScheduler(final JSONObject apiRequest) throws Exception {
        final Long customerId = APIUtil.getCustomerID(apiRequest);
        final JSONObject responseJSON = new JSONObject();
        String schedulerName = null;
        if (!IdpsUtil.getInstance().getSchedulerCustomizedTask(customerId, 7200).equals(-1L)) {
            schedulerName = "MDMAppSyncTaskCustomTemplate";
            Label_0095: {
                if (!CustomerInfoUtil.getInstance().isMSP()) {
                    CustomerInfoUtil.getInstance();
                    if (!CustomerInfoUtil.isSAS()) {
                        break Label_0095;
                    }
                }
                schedulerName = schedulerName + "__" + customerId;
            }
            final HashMap schedule = ApiFactoryProvider.getSchedulerAPI().getScheduledValues(schedulerName);
            MDMRestAPIFactoryProvider.getAPIUtil().formatSchedulerDetailsToJSON(responseJSON, schedule, schedulerName);
            responseJSON.put("custom_scheduler", (Object)Boolean.TRUE);
        }
        else {
            if (!GoogleForWorkSettings.isAFWSettingsConfigured(customerId) && !GoogleForWorkSettings.isGoogleForWorkSettingsConfigured(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT) && !new WpAppSettingsHandler().isBstoreConfigured(customerId) && !new VPPTokenDataHandler().isVppTokenConfigured(customerId)) {
                throw new APIHTTPException("APP0020", new Object[0]);
            }
            schedulerName = "MDMAppSyncTaskScheduler";
            final HashMap schedule = ApiFactoryProvider.getSchedulerAPI().getScheduledValues(schedulerName);
            MDMRestAPIFactoryProvider.getAPIUtil().formatSchedulerDetailsToJSON(responseJSON, schedule, schedulerName);
            responseJSON.put("custom_scheduler", (Object)Boolean.FALSE);
        }
        return responseJSON;
    }
    
    public void refreshAppStatusForAppGroup(final JSONObject apiRequest) throws Exception {
        try {
            final Long customerId = APIUtil.getCustomerID(apiRequest);
            final Long appGroupId = APIUtil.getResourceID(apiRequest, "app_id");
            final Integer status = APIUtil.getIntegerFilter(apiRequest, "status");
            final JSONArray appGroupIds = new JSONArray();
            appGroupIds.put((Object)appGroupId);
            final JSONArray deviceIds = apiRequest.getJSONObject("msg_body").optJSONArray("device_ids");
            final JSONObject appRefreshJSON = new JSONObject();
            appRefreshJSON.put("CUSTOMER_ID", (Object)customerId);
            appRefreshJSON.put("APP_IDS", (Object)appGroupIds);
            appRefreshJSON.put("DEVICE_IDS", (Object)deviceIds);
            if (status != -1) {
                appRefreshJSON.put("STATUS", (Object)status);
            }
            new AppStatusRefreshHandler().refreshAppStatusForAppGroup(appRefreshJSON);
        }
        catch (final JSONException e) {
            AppFacade.logger.log(Level.SEVERE, "Exception in refreshAppStatusForAppGroup", (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            AppFacade.logger.log(Level.SEVERE, "Exception in refreshAppStatusForAppGroup", e2);
            throw e2;
        }
    }
    
    public void refreshAppStatusForDevice(final JSONObject apiRequest) throws Exception {
        final Long customerId = APIUtil.getCustomerID(apiRequest);
        final Long deviceId = APIUtil.getResourceID(apiRequest, "device_id");
        final Integer status = APIUtil.getIntegerFilter(apiRequest, "status");
        final JSONArray deviceIds = new JSONArray();
        deviceIds.put((Object)deviceId);
        final JSONArray appGroupIds = apiRequest.getJSONObject("msg_body").optJSONArray("app_ids");
        final JSONObject appRefreshJSON = new JSONObject();
        appRefreshJSON.put("CUSTOMER_ID", (Object)customerId);
        appRefreshJSON.put("APP_IDS", (Object)appGroupIds);
        appRefreshJSON.put("DEVICE_IDS", (Object)deviceIds);
        if (status != -1) {
            appRefreshJSON.put("STATUS", (Object)status);
        }
        new AppStatusRefreshHandler().refreshAppStatusForResource(appRefreshJSON);
    }
    
    public JSONObject getAppKioskPickList(final JSONObject request) throws Exception {
        final Long customerID = APIUtil.getCustomerID(request);
        final Integer platform = APIUtil.getIntegerFilter(request, "platform");
        if (platform == -1) {
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        final JSONObject optionalParams = new JSONObject();
        final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(request);
        optionalParams.put("startIndex", pagingUtil.getStartIndex());
        optionalParams.put("noOfObj", pagingUtil.getLimit());
        optionalParams.put("selectAll", (Object)APIUtil.getBooleanFilter(request, "select_all", false));
        optionalParams.put("app_scope", (Object)APIUtil.getStringFilter(request, "app_scope"));
        optionalParams.put("platform", (Object)platform);
        optionalParams.put("searchValue", (Object)APIUtil.getStringFilter(request, "search"));
        optionalParams.put("is_app_purchased_from_portal", (Object)APIUtil.getBooleanFilter(request, "is_app_purchased_from_portal"));
        optionalParams.put("is_allowed_apps", (Object)APIUtil.getBooleanFilter(request, "is_allowed_apps", false));
        optionalParams.put("sort", (Object)APIUtil.getIntegerFilter(request, "sort"));
        optionalParams.put("customerId", (Object)APIUtil.getCustomerID(request));
        return ApiListViewDataHandler.getInstance(103).getFilterValues(optionalParams, pagingUtil);
    }
    
    public JSONObject getAppList(final JSONObject request) throws JSONException {
        final Long customerID = APIUtil.getCustomerID(request);
        final String filterChar = APIUtil.getStringFilter(request, "search");
        final List<String> applicationList = InventoryUtil.getInstance().getAllApplicationName(filterChar, customerID);
        final JSONObject response = new JSONObject();
        response.put("apps", (Collection)applicationList);
        return response;
    }
    
    private List<Long> validateGroupIds(final Set<Long> resourceIds, final Long customerID) {
        try {
            AppFacade.logger.log(Level.INFO, "Removing invalid group ids from resource ids: {0}", resourceIds);
            final Set groupList = new HashSet(resourceIds);
            SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
            selectQuery.addJoin(new Join("CustomGroup", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            final Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)groupList.toArray(), 8).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
            selectQuery.setCriteria(criteria);
            selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            final DataObject dataObject = DataAccess.get(selectQuery);
            final Iterator<Row> rows = dataObject.getRows("CustomGroup");
            final ArrayList<Long> groupIDS = new ArrayList<Long>();
            while (rows.hasNext()) {
                groupIDS.add(Long.valueOf(String.valueOf(rows.next().get("RESOURCE_ID"))));
            }
            groupList.removeAll(groupIDS);
            if (!groupList.isEmpty()) {
                return new ArrayList<Long>(groupList);
            }
        }
        catch (final DataAccessException ex) {
            AppFacade.logger.log(Level.SEVERE, "exception in validateGroupIds", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return null;
    }
    
    private List<Long> validateDeviceIds(Set<Long> deviceIDs, final Long customerID) throws APIHTTPException {
        try {
            deviceIDs = new HashSet<Long>(deviceIDs);
            final DataObject dataObject = DataAccess.get(new DeviceFacade().getDeviceValidationQuery(deviceIDs, customerID));
            final Iterator<Row> rows = dataObject.getRows("ManagedDevice");
            final ArrayList<Long> devices = new ArrayList<Long>();
            while (rows.hasNext()) {
                final Row managedDeviceRow = rows.next();
                final Long deviceId = Long.valueOf(String.valueOf(managedDeviceRow.get("RESOURCE_ID")));
                devices.add(deviceId);
            }
            deviceIDs.removeAll(devices);
            if (deviceIDs.size() > 0) {
                return new ArrayList<Long>(deviceIDs);
            }
        }
        catch (final DataAccessException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return null;
    }
    
    public JSONObject getLowerVersionApps(final JSONObject apiRequest) throws APIHTTPException {
        try {
            final Long packageId = APIUtil.getResourceID(apiRequest, "app_id");
            final Long releaseLabelId = APIUtil.getResourceID(apiRequest, "label_id");
            this.validateIfAppFound(packageId, APIUtil.getCustomerID(apiRequest));
            this.validateIfReleaseLabelFound(releaseLabelId, APIUtil.getCustomerID(apiRequest));
            final Integer platform = AppsUtil.getInstance().getPlatformTypeFromPackageID(packageId);
            return AppVersionHandler.getInstance(platform).getLowerVersionAppsThanGiveApp(apiRequest);
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception getting lower version apps", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getHigherVersionApps(final JSONObject apiRequest) throws APIHTTPException {
        try {
            final Long packageId = APIUtil.getResourceID(apiRequest, "app_id");
            final Long releaseLabelId = APIUtil.getResourceID(apiRequest, "label_id");
            this.validateIfAppFound(packageId, APIUtil.getCustomerID(apiRequest));
            this.validateIfReleaseLabelFound(releaseLabelId, APIUtil.getCustomerID(apiRequest));
            final Integer platform = AppsUtil.getInstance().getPlatformTypeFromPackageID(packageId);
            return AppVersionHandler.getInstance(platform).getHigherVersionAppsThanGivenApps(apiRequest);
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception getting higher version apps", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Boolean validateAppVersionIfApproved(final Long packageId, final Long releaseLabelId) throws DataAccessException, APIHTTPException {
        final SelectQuery selectQuery = AppsUtil.getAppAllLiveVersionQuery();
        final Criteria packageCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageId, 0);
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        selectQuery.setCriteria(packageCriteria.and(releaseLabelCriteria).and(AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria()));
        selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (dataObject.isEmpty()) {
            return false;
        }
        return true;
    }
    
    public void deleteSpecificAppVersionFromRepository(final JSONObject apiRequest) throws APIHTTPException {
        HashMap appDetailsMap = null;
        final Long customerID = APIUtil.getCustomerID(apiRequest);
        final String userName = APIUtil.getUserName(apiRequest);
        final int eventLogConstants = 72511;
        String eventLogRemarks = "mdm.appmgmt.app.version.deleted";
        String eventLogRemarksArgs = "";
        String appName = "";
        String appVersionName = "";
        String versionLabel = "";
        try {
            final Long packageId = APIUtil.getResourceID(apiRequest, "app_id");
            final Long releaseLabelId = APIUtil.getResourceID(apiRequest, "label_id");
            appDetailsMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageId, releaseLabelId);
            appName = appDetailsMap.get("PROFILE_NAME");
            appVersionName = appDetailsMap.get("APP_VERSION");
            versionLabel = I18N.getMsg((String)appDetailsMap.get("RELEASE_LABEL_DISPLAY_NAME"), new Object[0]);
            final Boolean isApproved = this.validateAppVersionIfApproved(packageId, releaseLabelId);
            if (isApproved) {
                throw new APIHTTPException("APP0028", new Object[0]);
            }
            this.validateIfAppFound(packageId, APIUtil.getCustomerID(apiRequest));
            this.validateIfReleaseLabelFound(releaseLabelId, APIUtil.getCustomerID(apiRequest));
            AppVersionDBUtil.getInstance().validateAppVersionForDelete(packageId, releaseLabelId);
            final Integer platform = AppsUtil.getInstance().getPlatformTypeFromPackageID(packageId);
            this.getInstance(platform, apiRequest).deleteSpecificAppVersion(apiRequest);
            final JSONObject syncJSON = new JSONObject(apiRequest.toString());
            syncJSON.put("customerID", (Object)APIUtil.getCustomerID(apiRequest));
            syncJSON.put("userID", (Object)APIUtil.getUserID(apiRequest));
            syncJSON.put("platform_type", (Object)platform);
            syncJSON.put("LOGIN_ID", (Object)APIUtil.getLoginID(apiRequest));
            final String appUniqueIdentifier = appDetailsMap.get("IDENTIFIER") + "@@@" + appDetailsMap.get("APP_VERSION") + "@@@" + appDetailsMap.get("APP_NAME_SHORT_VERSION");
            syncJSON.put("app_unique_identifier", (Object)appUniqueIdentifier);
            syncJSON.put("app_id", (Object)packageId);
            SyncConfigurationListeners.invokeListeners(syncJSON, 212);
            eventLogRemarksArgs = appName + "@@@" + appVersionName + "@@@" + versionLabel;
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in deleteSpecificAppVersionFromRepository", ex);
            eventLogRemarks = "mdm.appmgmt.app.version.deletion.failed";
            eventLogRemarksArgs = appName + "@@@" + appVersionName + "@@@" + versionLabel;
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            MDMEventLogHandler.getInstance().MDMEventLogEntry(eventLogConstants, null, userName, eventLogRemarks, eventLogRemarksArgs, customerID);
        }
    }
    
    public void approveApp(final JSONObject apiRequest) throws APIHTTPException {
        try {
            final Long packageId = APIUtil.getResourceID(apiRequest, "app_id");
            final Long releaseLabelId = APIUtil.getResourceID(apiRequest, "label_id");
            final Long customerId = APIUtil.getCustomerID(apiRequest);
            this.validateIfAppFound(packageId, customerId);
            this.validateIfReleaseLabelFound(releaseLabelId, customerId);
            final Boolean isApproved = this.validateAppVersionIfApproved(packageId, releaseLabelId);
            if (isApproved) {
                throw new APIHTTPException("APP0029", new Object[0]);
            }
            final JSONObject syncJSON = new JSONObject(apiRequest.toString());
            final Integer platform = AppsUtil.getInstance().getPlatformTypeFromPackageID(packageId);
            this.getInstance(platform, apiRequest).approveAppVersion(apiRequest);
            if (SyncConfigurationsUtil.checkIfAppIsForAllCustomers(packageId)) {
                syncJSON.put("customerID", (Object)APIUtil.getCustomerID(apiRequest));
                syncJSON.put("userName", (Object)APIUtil.getUserName(apiRequest));
                syncJSON.put("userID", (Object)APIUtil.getUserID(apiRequest));
                syncJSON.put("platform_type", (Object)platform);
                syncJSON.put("app_id", (Object)packageId);
                final HashMap appDetailsMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageId, releaseLabelId);
                final String appUniqueIdentifier = appDetailsMap.get("IDENTIFIER") + "@@@" + appDetailsMap.get("APP_VERSION") + "@@@" + appDetailsMap.get("APP_NAME_SHORT_VERSION");
                syncJSON.put("app_unique_identifier", (Object)appUniqueIdentifier);
                syncJSON.put("LOGIN_ID", (Object)APIUtil.getLoginID(apiRequest));
                SyncConfigurationListeners.invokeListeners(syncJSON, 204);
            }
        }
        catch (final Exception ex) {
            AppFacade.logger.log(Level.SEVERE, "Exception in approveApp", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONArray getAllNonPortalStoreApps(final Long customerId, final int platformType) throws DataAccessException, JSONException {
        final SelectQuery selectQuery = this.getPortalAppQuery(customerId, platformType);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final JSONArray appsArr = new JSONArray();
        if (!dataObject.isEmpty()) {
            final Iterator itr = dataObject.getRows("MdAppGroupDetails");
            while (itr.hasNext()) {
                final JSONObject app = new JSONObject();
                final Row row = itr.next();
                app.put("IDENTIFIER", row.get("IDENTIFIER"));
                app.put("APP_GROUP_ID", row.get("APP_GROUP_ID"));
                app.put("GROUP_DISPLAY_NAME", row.get("GROUP_DISPLAY_NAME"));
                appsArr.put((Object)app);
            }
        }
        return appsArr;
    }
    
    public List<String> getAllNonPortalStoreAppIdentifiers(final Long customerId, final int platformType) throws DataAccessException, JSONException {
        final SelectQuery selectQuery = this.getPortalAppQuery(customerId, platformType);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final List<String> appsArr = new ArrayList<String>();
        if (!dataObject.isEmpty()) {
            final Iterator itr = dataObject.getRows("MdAppGroupDetails");
            while (itr.hasNext()) {
                final Row row = itr.next();
                appsArr.add((String)row.get("IDENTIFIER"));
            }
        }
        return appsArr;
    }
    
    private SelectQuery getPortalAppQuery(final Long customerId, final int platformType) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
        final Join packageJoin = new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Join appGroupJoin = new Join("MdPackageToAppData", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        selectQuery.addJoin(packageJoin);
        selectQuery.addJoin(appGroupJoin);
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
        final Criteria portalCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)false, 0);
        final Criteria playstoreCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 1);
        selectQuery.setCriteria(portalCriteria.and(customerCriteria).and(platformCriteria).and(playstoreCriteria));
        return selectQuery;
    }
    
    private SelectQuery getGroupAppSelectQuery(final Long groupId, final Long customerId) throws Exception {
        final SelectQuery appGroupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        appGroupQuery.addJoin(new Join("RecentProfileForGroup", "GroupToProfileHistory", new String[] { "GROUP_ID", "PROFILE_ID", "COLLECTION_ID" }, new String[] { "GROUP_ID", "PROFILE_ID", "COLLECTION_ID" }, 2));
        appGroupQuery.addJoin(new Join("GroupToProfileHistory", "AaaUser", new String[] { "ASSOCIATED_BY" }, new String[] { "USER_ID" }, 2));
        appGroupQuery.addJoin(new Join("RecentProfileForGroup", "ProfileToCollection", new String[] { "PROFILE_ID", "COLLECTION_ID" }, new String[] { "PROFILE_ID", "COLLECTION_ID" }, 2));
        appGroupQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        appGroupQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        appGroupQuery.addJoin(new Join("RecentProfileForGroup", "CollnToResources", new String[] { "COLLECTION_ID", "GROUP_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdAppGroupDetails", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        appGroupQuery.addJoin(new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 2));
        appGroupQuery.addJoin(new Join("RecentProfileForGroup", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appGroupQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
        appGroupQuery.addJoin(new Join("AppCollnToReleaseLabelHistory", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), 0);
        final Criteria groupIDCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)groupId, 0);
        appGroupQuery.addJoin(new Join("RecentProfileForGroup", "MdAppCatalogToGroup", appGroupCriteria.and(groupIDCriteria), 2));
        appGroupQuery.addJoin(new Join("MdAppCatalogToGroup", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, "MdAppCatalogToGroup", "LatestAppColln", 2));
        appGroupQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, "LatestAppColln", "LatestProf", 2));
        appGroupQuery.addJoin(new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, "LatestAppColln", "LatestApp", 1));
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria groupCriteria = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID"), (Object)groupId, 0);
        appGroupQuery.setCriteria(customerCriteria.and(groupCriteria));
        appGroupQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
        appGroupQuery.addSelectColumn(Column.getColumn("AppCategory", "APP_CATEGORY_NAME"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "ASSOCIATED_BY"));
        appGroupQuery.addSelectColumn(Column.getColumn("GroupToProfileHistory", "ASSOCIATED_TIME"));
        appGroupQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME"));
        appGroupQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "PROFILE_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("CollnToResources", "STATUS"));
        appGroupQuery.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS"));
        appGroupQuery.addSelectColumn(Column.getColumn("CollnToResources", "REMARKS_EN"));
        appGroupQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
        appGroupQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
        appGroupQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
        appGroupQuery.addSelectColumn(Column.getColumn("LatestApp", "APP_VERSION", "LATESTMDAPPDETAILS.APP_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("LatestApp", "APP_NAME_SHORT_VERSION", "LATESTMDAPPDETAILS.APP_NAME_SHORT_VERSION"));
        appGroupQuery.addSelectColumn(Column.getColumn("LatestProf", "PROFILE_VERSION", "LATESTPROFILECOLLN.PROFILE_VERSION"));
        return appGroupQuery;
    }
    
    public JSONObject getAppCatalogForDevice(final JSONObject message) {
        Long deviceId = APIUtil.getResourceID(message, "device_id");
        final JSONObject response = new JSONObject();
        final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(message);
        if (deviceId == -1L) {
            final String udid = APIUtil.getResourceIDString(message, "udid");
            deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        }
        List<Long> resourceList = null;
        if (deviceId == null || deviceId == -1L) {
            AppFacade.logger.log(Level.INFO, "Device id/UDID not provided");
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        resourceList = new ArrayList<Long>();
        resourceList.add(deviceId);
        final String searchValue = APIUtil.getStringFilter(message, "search");
        final int statusFilter = APIUtil.getIntegerFilter(message, "status");
        new DeviceFacade().validateIfDevicesExists(resourceList, APIUtil.getCustomerID(message));
        try {
            final int appsCount = DBUtil.getRecordCount("MdAppCatalogToResource", "APP_GROUP_ID", new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)deviceId, 0));
            final JSONObject pagingJSON = pagingUtil.getPagingJSON(appsCount);
            if (pagingJSON != null) {
                response.put("paging", (Object)pagingJSON);
            }
            response.put("count", appsCount);
            response.put("device_id", (Object)deviceId);
            return response.put("apps", (Object)new MDMAppCatalogHandler().getAppCatalogForDevice(deviceId, searchValue, statusFilter, pagingUtil));
        }
        catch (final Exception e) {
            AppFacade.logger.log(Level.WARNING, "Cannot fetch app catalog for device", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONArray approveStoreApps(final int platformType, final JSONObject apiParams) throws Exception {
        if (!apiParams.has("appsList")) {
            AppFacade.logger.log(Level.SEVERE, "App list cannot be fetched from request");
            throw new APIHTTPException("COM0009", new Object[0]);
        }
        return this.getInstance(platformType, apiParams).approveStoreApps(apiParams);
    }
    
    static {
        AppFacade.logger = Logger.getLogger("MDMConfigLogger");
    }
}
