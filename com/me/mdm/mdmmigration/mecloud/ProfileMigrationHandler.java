package com.me.mdm.mdmmigration.mecloud;

import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.certificate.SCEPFacade;
import com.me.mdm.server.certificate.ScepServerFacade;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.server.directory.DirectoryTemplateHandler;
import com.me.mdm.mdmmigration.APIServiceDataHandler;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFileVaultFacade;
import com.me.mdm.server.apps.AppPermissionFacade;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.webclips.WebClipsFacade;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.certificate.CertificateFacade;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONArray;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.List;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.Map;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ProfileMigrationHandler
{
    private static Logger logger;
    private static MECloudAPIRequestHandler meCloudAPIRequestHandler;
    
    public ProfileMigrationHandler(final MECloudAPIRequestHandler meCloudAPIRequestHandler) {
        ProfileMigrationHandler.meCloudAPIRequestHandler = meCloudAPIRequestHandler;
    }
    
    public void migrateMediaContent(final JSONObject payloadResponseJSON, final Map requestHeaderMap) {
        try {
            if (payloadResponseJSON.has("below_hdpi_wallpaper_path")) {
                ProfileMigrationHandler.logger.log(Level.INFO, "profile contains wallpaper media..{0}", payloadResponseJSON.toString());
                final String below_hdpi_wallpaper_path = payloadResponseJSON.remove("below_hdpi_wallpaper_path").toString();
                final String newFileId = ProfileMigrationHandler.meCloudAPIRequestHandler.fileUpload(below_hdpi_wallpaper_path, requestHeaderMap, 1);
                payloadResponseJSON.put("below_hdpi_wallpaper", (Object)newFileId);
                payloadResponseJSON.put("is_below_hdpi_wall_modified", true);
            }
            if (payloadResponseJSON.has("above_hdpi_wallpaper_path")) {
                ProfileMigrationHandler.logger.log(Level.INFO, "profile contains wallpaper media..{0}", payloadResponseJSON.toString());
                final String above_hdpi_wallpaper_path = payloadResponseJSON.remove("above_hdpi_wallpaper_path").toString();
                final String newFileId = ProfileMigrationHandler.meCloudAPIRequestHandler.fileUpload(above_hdpi_wallpaper_path, requestHeaderMap, 1);
                payloadResponseJSON.put("above_hdpi_wallpaper", (Object)newFileId);
                payloadResponseJSON.put("is_above_hdpi_wall_modified", true);
            }
            if (payloadResponseJSON.has("below_hdpi_lock_wallpaper")) {
                ProfileMigrationHandler.logger.log(Level.INFO, "profile contains ios media..{0}", payloadResponseJSON.toString());
                final String above_hdpi_wallpaper_path = payloadResponseJSON.remove("below_hdpi_lock_wallpaper").toString();
                final String newFileId = ProfileMigrationHandler.meCloudAPIRequestHandler.fileUpload(above_hdpi_wallpaper_path, requestHeaderMap, 1);
                payloadResponseJSON.put("below_hdpi_lock_wallpaper", (Object)newFileId);
                payloadResponseJSON.put("is_below_hdpi_lock_wall_modified", true);
            }
            if (payloadResponseJSON.has("below_hdpi_wallpaper") && requestHeaderMap.get("payload_name").equals("ioswallpaperpolicy")) {
                ProfileMigrationHandler.logger.log(Level.INFO, "profile contains ios media..{0}", payloadResponseJSON.toString());
                final String above_hdpi_wallpaper_path = payloadResponseJSON.remove("below_hdpi_wallpaper").toString();
                final String newFileId = ProfileMigrationHandler.meCloudAPIRequestHandler.fileUpload(above_hdpi_wallpaper_path, requestHeaderMap, 1);
                payloadResponseJSON.put("below_hdpi_wallpaper", (Object)newFileId);
                payloadResponseJSON.put("is_below_hdpi_wall_modified", true);
            }
            if (payloadResponseJSON.has("icon_file_name")) {
                ProfileMigrationHandler.logger.log(Level.INFO, "profile contains webclip media..{0}", payloadResponseJSON.toString());
                final String webclipsIconPath = payloadResponseJSON.remove("icon_file_name").toString();
                final String newIconId = ProfileMigrationHandler.meCloudAPIRequestHandler.fileUpload(webclipsIconPath, requestHeaderMap, 1);
                payloadResponseJSON.put("webclips_file_upload", (Object)newIconId);
                payloadResponseJSON.remove("webclip_policy_id");
            }
            if (payloadResponseJSON.has("wallpaper")) {
                ProfileMigrationHandler.logger.log(Level.INFO, "profile contains lockscreen media..{0}", payloadResponseJSON.toString());
                final String wallpaperId = payloadResponseJSON.remove("wallpaper").toString();
                final String newWallpaperId = ProfileMigrationHandler.meCloudAPIRequestHandler.fileUpload(wallpaperId, requestHeaderMap, 1);
                payloadResponseJSON.put("wallpaper", (Object)newWallpaperId);
            }
        }
        catch (final Exception e) {
            ProfileMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while migrating media {0} ", e);
            throw new APIHTTPException("MIG008", new Object[0]);
        }
    }
    
    public List getMigratedProfiles(final long config_id) {
        final List<String> profileList = new ArrayList<String>();
        try {
            final Criteria configCriteria = new Criteria(new Column("ProfileMigrationSummary", "CONFIG_ID"), (Object)config_id, 0);
            final Criteria migCriteria = new Criteria(new Column("ProfileMigrationSummary", "IS_MIGRATED"), (Object)Boolean.TRUE, 0);
            final DataObject migratedProfilesDO = MDMUtil.getPersistence().get("ProfileMigrationSummary", configCriteria.and(migCriteria));
            if (!migratedProfilesDO.isEmpty()) {
                final Iterator profileIterator = migratedProfilesDO.getRows("ProfileMigrationSummary");
                while (profileIterator.hasNext()) {
                    final Row profileRow = profileIterator.next();
                    final Long profileID = (Long)profileRow.get("SERVER_PROFILE_ID");
                    profileList.add(String.valueOf(profileID));
                }
            }
        }
        catch (final Exception e) {
            ProfileMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while fetching successfully migrated profile list {0} - {1}", new Object[] { config_id, e });
        }
        return profileList;
    }
    
    public int updateProfileMigrationSummary(final JSONArray migrationSummary, final Long config_id) {
        int migratedProfilesCount = 0;
        try {
            final DataObject migrationStatusDO = MDMUtil.getPersistence().get("ProfileMigrationSummary", new Criteria(new Column("ProfileMigrationSummary", "CONFIG_ID"), (Object)config_id, 0));
            for (int i = 0; i < migrationSummary.length(); ++i) {
                final JSONObject profile = migrationSummary.getJSONObject(i);
                final Long profile_id = Long.valueOf(profile.getString("server_profile_id"));
                Row summaryRow = migrationStatusDO.getRow("ProfileMigrationSummary", new Criteria(new Column("ProfileMigrationSummary", "SERVER_PROFILE_ID"), (Object)profile_id, 0));
                if (summaryRow != null) {
                    summaryRow.set("IS_MIGRATED", profile.get("is_migrated"));
                    summaryRow.set("REMARKS", profile.get("remarks"));
                    migrationStatusDO.updateRow(summaryRow);
                }
                else {
                    summaryRow = new Row("ProfileMigrationSummary");
                    summaryRow.set("CONFIG_ID", (Object)config_id);
                    summaryRow.set("PROFILE_ID", (Object)profile_id);
                    summaryRow.set("SERVER_PROFILE_ID", (Object)profile.getLong("server_profile_id"));
                    summaryRow.set("PROFILE_NAME", profile.get("profile_name"));
                    summaryRow.set("IS_MIGRATED", profile.get("is_migrated"));
                    summaryRow.set("REMARKS", profile.get("remarks"));
                    migrationStatusDO.addRow(summaryRow);
                }
                if (profile.getBoolean("is_migrated")) {
                    ++migratedProfilesCount;
                }
            }
            MDMUtil.getPersistence().update(migrationStatusDO);
        }
        catch (final Exception e) {
            ProfileMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while updating profile migration summary {0}", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return migratedProfilesCount;
    }
    
    public JSONObject createRequestJsonForProfile(final JSONObject requestBody, final Map requestHeaderMap) throws Exception {
        final JSONObject payloadCreateJson = new JSONObject();
        final String user_id = requestHeaderMap.get("user_id").toString();
        final Long login_id = DMUserHandler.getLoginIdForUserId(Long.valueOf(user_id));
        final String customer_id = requestHeaderMap.get("customer_id").toString();
        final String user_name = DMUserHandler.getUserNameFromUserID(Long.valueOf(user_id));
        payloadCreateJson.put("msg_body", (Object)requestBody);
        payloadCreateJson.put("msg_header", (Object)new JSONObject().put("filters", (Object)new JSONObject().put("user_id", (Object)user_id).put("customer_id", (Object)customer_id).put("login_id", (Object)login_id).put("user_name", (Object)user_name)));
        if (requestHeaderMap.containsKey("profile_id")) {
            payloadCreateJson.getJSONObject("msg_header").put("resource_identifier", (Object)new JSONObject().put("profile_id", (Object)requestHeaderMap.get("profile_id").toString()).put("payload_id", (Object)requestHeaderMap.get("payload_name").toString()).put("collection_id", requestHeaderMap.get("collection_id")));
        }
        return payloadCreateJson;
    }
    
    public JSONObject migrateCertificate(JSONObject certJSON, final Map requestHeaderMap) throws Exception {
        final String sourceMethod = "ProfileMigrationHandler::migrateCertificate";
        ProfileMigrationHandler.logger.log(Level.INFO, "{0} --> Going to start Certificate Migration..{1}", new Object[] { sourceMethod, certJSON.toString() });
        try {
            String certificate_file_name = certJSON.getString("certificate_file_name");
            certJSON.remove("certificate_file_name");
            if (certJSON.has("certificate_id")) {
                final String certificate_id = certJSON.remove("certificate_id").toString();
                final String[] file_name_splits = certificate_file_name.split("\\.");
                certificate_file_name = certificate_id + "." + file_name_splits[file_name_splits.length - 1];
            }
            final String new_certificate_id = ProfileMigrationHandler.meCloudAPIRequestHandler.fileUpload(certificate_file_name, requestHeaderMap, 3);
            certJSON.put("certificate_file_upload", (Object)new_certificate_id);
            certJSON.put("certificate_type", 0);
            final JSONObject requestJson = this.createRequestJsonForProfile(certJSON, requestHeaderMap);
            certJSON = new CertificateFacade().addCertificate(requestJson);
        }
        catch (final Exception ex) {
            ProfileMigrationHandler.logger.log(Level.SEVERE, "Error while mirating certificate {0}", ex);
            throw ex;
        }
        return certJSON;
    }
    
    public void migrateKioskProfile(final JSONObject payloadResponseJSON, final Map requestHeaderMap, final String platform_type, final String payloadName, final Long config_id) {
        try {
            if (payloadResponseJSON.has("allowed_apps")) {
                ProfileMigrationHandler.logger.log(Level.INFO, "profile contains kiosk app content..{0}", payloadResponseJSON.toString());
                final Long customer_id = Long.valueOf(requestHeaderMap.get("customer_id").toString());
                final JSONArray availableApps = payloadResponseJSON.getJSONArray("allowed_apps");
                final JSONArray newAppsWithNewAppID = new JSONArray();
                final AppMigrationHandler appMigrationHandler = new AppMigrationHandler(ProfileMigrationHandler.meCloudAPIRequestHandler);
                for (int apps_index = 0; apps_index < availableApps.length(); ++apps_index) {
                    final JSONObject appsDetails = availableApps.getJSONObject(apps_index);
                    final String group_display_name = appsDetails.getString("group_display_name");
                    final JSONObject searchJSON = appMigrationHandler.createRequestJsonForApp(null, requestHeaderMap);
                    searchJSON.getJSONObject("msg_header").getJSONObject("filters").put("platform", (Object)platform_type).put("search", (Object)group_display_name);
                    searchJSON.getJSONObject("msg_header").put("request_url", (Object)"");
                    JSONObject responseBodyObject = MDMRestAPIFactoryProvider.getAppFacade().getAppKioskPickList(searchJSON);
                    final JSONArray associatedApps = responseBodyObject.getJSONArray("apps");
                    if (associatedApps.length() > 0) {
                        final String newServerAppId = associatedApps.getJSONObject(0).getString("app_id");
                        appsDetails.put("app_id", (Object)newServerAppId);
                        newAppsWithNewAppID.put((Object)appsDetails);
                        ProfileMigrationHandler.logger.log(Level.INFO, "kiosk app already migrated..{0}", group_display_name);
                    }
                    else {
                        ProfileMigrationHandler.logger.log(Level.INFO, "going to migrate kiosk app..{0}", group_display_name);
                        final JSONObject parameters = new JSONObject();
                        parameters.put("platform", (Object)platform_type);
                        parameters.put("search", (Object)group_display_name);
                        final JSONObject responseObject = ProfileMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", ProfileMigrationHandler.meCloudAPIRequestHandler.apiServerURL + "api/v1/mdm/picklist/apps", parameters);
                        ProfileMigrationHandler.meCloudAPIRequestHandler.validateResponse(responseObject);
                        responseBodyObject = responseObject.getJSONObject("ResponseJson");
                        final JSONObject appDetails = responseBodyObject.getJSONArray("apps").getJSONObject(0);
                        final JSONObject appDataToPost = new JSONObject();
                        appDataToPost.put("bundle_identifier", (Object)appDetails.getString("identifier"));
                        appDataToPost.put("platform_type", (Object)platform_type);
                        appDataToPost.put("app_type", 1);
                        appDataToPost.put("supported_devices", 3);
                        appDataToPost.put("is_purchased_from_portal", (Object)Boolean.FALSE);
                        appDataToPost.put("is_paid_app", (Object)Boolean.FALSE);
                        appDataToPost.put("app_name", (Object)group_display_name);
                        final JSONObject appPostJSON = appMigrationHandler.createRequestJsonForApp(appDataToPost, requestHeaderMap);
                        try {
                            final JSONObject newAppDetails = new AppFacade().addApp(appPostJSON);
                            final String new_app_id = newAppDetails.getString("app_id");
                            requestHeaderMap.put("app_id", new_app_id);
                            final JSONObject migratedApp = new JSONObject();
                            migratedApp.put("server_app_id", (Object)appDetails.getString("app_id"));
                            migratedApp.put("bundle_identifier", (Object)newAppDetails.getString("bundle_identifier"));
                            migratedApp.put("profile_name", (Object)newAppDetails.getString("profile_name"));
                            migratedApp.put("platform_type", (Object)newAppDetails.getString("platform_type"));
                            migratedApp.put("app_id", (Object)newAppDetails.getString("app_id"));
                            migratedApp.put("is_migrated", (Object)Boolean.TRUE);
                            final StringBuilder release_labels = new StringBuilder();
                            final JSONArray new_release_labels = newAppDetails.getJSONArray("release_labels");
                            for (int j = 0; j < new_release_labels.length(); ++j) {
                                release_labels.append(new_release_labels.getJSONObject(j).getString("release_label_id")).append(",");
                            }
                            migratedApp.put("release_labels", (Object)release_labels.deleteCharAt(release_labels.length() - 1).toString());
                            migratedApp.put("remarks", (Object)"Migration completed successfully");
                            final Map appIdsMap = new HashMap();
                            appIdsMap.put(new_app_id, new_app_id);
                            final Map releaseLableInfo = new HashMap();
                            final String release_label_name = new_release_labels.getJSONObject(0).getString("release_label_name");
                            releaseLableInfo.put(release_label_name, appMigrationHandler.getReleaseLabelId(release_label_name, customer_id));
                            appMigrationHandler.associateAppsToGroup(appIdsMap, config_id, requestHeaderMap, releaseLableInfo);
                            appMigrationHandler.associateAppsToDevice(appIdsMap, config_id, releaseLableInfo);
                            final JSONArray migratedApps = new JSONArray();
                            migratedApps.put((Object)migratedApp);
                            appMigrationHandler.addOrUpdateAppMigrationSummary(migratedApps, config_id);
                            ProfileMigrationHandler.logger.log(Level.INFO, "kiosk app migration completed successfully..{0}", group_display_name);
                        }
                        catch (final APIHTTPException e) {
                            final JSONObject error = new JSONObject(e.toString());
                            if (!error.getString("error_description").contains("already exists as")) {
                                ProfileMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while migrating kiosk profile {0} ", e);
                                throw new APIHTTPException("MIG010", new Object[0]);
                            }
                        }
                    }
                }
                payloadResponseJSON.put("allowed_apps", (Object)newAppsWithNewAppID);
                payloadResponseJSON.put("payload_name", (Object)payloadName);
            }
            if (payloadResponseJSON.has("webclippolicies") && payloadResponseJSON.getJSONArray("webclippolicies").length() > 0) {
                final JSONObject webclippolicies = payloadResponseJSON.getJSONArray("webclippolicies").getJSONObject(0);
                String webclip_policy_id = webclippolicies.get("webclip_policy_id").toString();
                final JSONObject responseObject2 = ProfileMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", ProfileMigrationHandler.meCloudAPIRequestHandler.profilesUrl + "/webclips/" + webclip_policy_id, null);
                ProfileMigrationHandler.meCloudAPIRequestHandler.validateResponse(responseObject2);
                JSONObject responseBodyObject2 = responseObject2.getJSONObject("ResponseJson");
                String webclip_name = webclippolicies.getString("webclip_name");
                final Object customer_id2 = requestHeaderMap.get("customer_id");
                responseBodyObject2 = new ProfileMigrationHandler(ProfileMigrationHandler.meCloudAPIRequestHandler).createRequestJsonForProfile(responseBodyObject2, requestHeaderMap);
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WebClipPolicies"));
                selectQuery.addSelectColumn(new Column("WebClipPolicies", "WEBCLIP_POLICY_ID"));
                final Criteria webClipLabelCriteria = new Criteria(new Column("WebClipPolicies", "WEBCLIP_NAME"), (Object)webclip_name, 0, false);
                final Criteria customerIdCriteria = new Criteria(new Column("WebClipPolicies", "CUSTOMER_ID"), customer_id2, 0);
                selectQuery.setCriteria(webClipLabelCriteria.and(customerIdCriteria));
                final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Row row = dataObject.getFirstRow("WebClipPolicies");
                    webclip_policy_id = row.get("WEBCLIP_POLICY_ID").toString();
                }
                else {
                    responseBodyObject2 = new WebClipsFacade().addWebClipsPolicy(responseBodyObject2);
                    webclip_policy_id = responseBodyObject2.get("webclip_policy_id").toString();
                    webclip_name = responseBodyObject2.get("webclip_name").toString();
                }
                final JSONObject webclip = new JSONObject();
                webclip.put("webclip_policy_id", (Object)webclip_policy_id);
                webclip.put("webclip_name", (Object)webclip_name);
                final JSONArray webclipArray = new JSONArray();
                webclipArray.put((Object)webclip);
                payloadResponseJSON.put("webclippolicies", (Object)webclipArray);
            }
        }
        catch (final Exception e2) {
            ProfileMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while migrating kiosk profile {0} ", e2);
            throw new APIHTTPException("MIG010", new Object[0]);
        }
    }
    
    public void migrateMacSpecificContent(final JSONObject payloadResponseJSON, final Map requestHeaderMap) throws Exception {
        if (payloadResponseJSON.has("app_permission_config_id")) {
            try {
                ProfileMigrationHandler.logger.log(Level.INFO, "profile contains pppc policy content..{0}", payloadResponseJSON.toString());
                final String old_app_permission_config_id = payloadResponseJSON.getString("app_permission_config_id");
                final JSONObject responseObject = ProfileMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", ProfileMigrationHandler.meCloudAPIRequestHandler.apiServerURL + "api/v1/mdm/apppermissions/" + old_app_permission_config_id, null);
                ProfileMigrationHandler.meCloudAPIRequestHandler.validateResponse(responseObject);
                final JSONObject responseJSON = responseObject.getJSONObject("ResponseJson");
                responseJSON.remove("app_permission_config_id");
                responseJSON.remove("app_group_id");
                responseJSON.put("platform", 1);
                JSONObject requestJson = this.createRequestJsonForProfile(responseJSON, requestHeaderMap);
                requestJson = new AppPermissionFacade().addOrModifyAppPermission(requestJson);
                payloadResponseJSON.put("app_permission_config_id", (Object)requestJson.get("APP_PERMISSION_CONFIG_ID").toString());
            }
            catch (final Exception ex) {
                ProfileMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while migrating app permission config content {0} ", ex);
                throw new APIHTTPException("MIG013", new Object[0]);
            }
        }
        if (payloadResponseJSON.has("encryption_settings_id")) {
            try {
                final String old_encryption_settings_id = payloadResponseJSON.getString("encryption_settings_id");
                final JSONObject responseObject = ProfileMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", ProfileMigrationHandler.meCloudAPIRequestHandler.profilesUrl + "/filevaults/" + old_encryption_settings_id, null);
                ProfileMigrationHandler.meCloudAPIRequestHandler.validateResponse(responseObject);
                final JSONObject responseJSON = responseObject.getJSONObject("ResponseJson");
                final String settings_name = responseJSON.getJSONObject("mdmencryptionsettings").getString("settings_name");
                Object new_encryption_settings_id = "";
                final DataObject encrypDO = MDMUtil.getPersistence().get("MDMEncryptionSettings", new Criteria(new Column("MDMEncryptionSettings", "SETTINGS_NAME"), (Object)settings_name, 0));
                if (!encrypDO.isEmpty()) {
                    final Row encrypRow = encrypDO.getRow("MDMEncryptionSettings");
                    new_encryption_settings_id = encrypRow.get("ENCRYPTION_SETTINGS_ID");
                }
                else {
                    responseJSON.getJSONObject("mdmencryptionsettings").remove("encryption_settings_id");
                    JSONObject requestJson2 = this.createRequestJsonForProfile(responseJSON, requestHeaderMap);
                    requestJson2 = new MDMFileVaultFacade().addFileVault(requestJson2);
                    responseJSON.keySet().clear();
                    new_encryption_settings_id = requestJson2.getJSONObject("mdmencryptionsettings").get("encryption_settings_id");
                }
                payloadResponseJSON.put("encryption_settings_id", new_encryption_settings_id);
            }
            catch (final Exception ex) {
                ProfileMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while migrating encryption settings content {0} ", ex);
                throw new APIHTTPException("MIG014", new Object[0]);
            }
        }
        if (payloadResponseJSON.has("bind_policy_id")) {
            final String old_bind_policy_id = payloadResponseJSON.remove("bind_policy_id").toString();
            final JSONObject responseObject = ProfileMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", ProfileMigrationHandler.meCloudAPIRequestHandler.apiServerURL + "api/v1/mdm/directory/bindpolicytemplates/" + old_bind_policy_id, null);
            ProfileMigrationHandler.meCloudAPIRequestHandler.validateResponse(responseObject);
            JSONObject bindPolicyJSON = responseObject.getJSONObject("ResponseJson");
            final JSONObject domain = new JSONObject();
            final String domainName = bindPolicyJSON.getString("ad_domain_name");
            final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
            final Long customer_id = Long.valueOf(requestHeaderMap.get("customer_id").toString());
            final String domainID = apiServiceDataHandler.getDomainIDFromName(customer_id, domainName);
            if (domainID == null) {
                throw new APIHTTPException("MIG007", new Object[0]);
            }
            domain.put("domain_id", (Object)domainID);
            domain.put("type", bindPolicyJSON.get("type"));
            domain.put("create_ma_at_login", bindPolicyJSON.get("create_ma_at_login"));
            final JSONObject requestJson3 = this.createRequestJsonForProfile(domain, requestHeaderMap);
            bindPolicyJSON = new DirectoryTemplateHandler().createTemplate(requestJson3);
            payloadResponseJSON.put("bind_policy_id", bindPolicyJSON.get("bind_policy_id"));
        }
        if (payloadResponseJSON.has("extensions")) {
            final JSONArray extnArray = payloadResponseJSON.getJSONArray("extensions");
            for (int extIndex = 0; extIndex < extnArray.length(); ++extIndex) {
                extnArray.getJSONObject(extIndex).remove("prov_id");
            }
        }
    }
    
    public void migrateCertificateContent(final JSONObject payloadResponseJSON, final Map requestHeaderMap) {
        try {
            if (payloadResponseJSON.has("certificate_id")) {
                ProfileMigrationHandler.logger.log(Level.INFO, "profile contains certificate content..{0}", payloadResponseJSON.toString());
                final Long customer_id = Long.valueOf(requestHeaderMap.get("customer_id").toString());
                final String old_certificate_id = payloadResponseJSON.getString("certificate_id");
                final JSONObject responseObject = ProfileMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", ProfileMigrationHandler.meCloudAPIRequestHandler.apiServerURL + "api/v1/mdm/profiles/certificates/" + old_certificate_id, null);
                ProfileMigrationHandler.meCloudAPIRequestHandler.validateResponse(responseObject);
                JSONObject certpayloadResponseJSON = responseObject.getJSONObject("ResponseJson");
                final String ca_finger_print = certpayloadResponseJSON.getString("certificate_thumbprint");
                final DataObject certDataObject = this.getCertificateDataObj(customer_id, ca_finger_print);
                if (!certDataObject.isEmpty()) {
                    final Row certificateRow = certDataObject.getRow("CredentialCertificateInfo");
                    final String new_certificate_id = String.valueOf(certificateRow.get("CERTIFICATE_ID"));
                    payloadResponseJSON.put("certificate_id", (Object)new_certificate_id);
                    ProfileMigrationHandler.logger.log(Level.INFO, "certificate already migrated..{0}", new_certificate_id);
                }
                else {
                    certpayloadResponseJSON = this.migrateCertificate(certpayloadResponseJSON, requestHeaderMap);
                    payloadResponseJSON.put("certificate_id", (Object)certpayloadResponseJSON.getString("certificate_id"));
                }
            }
        }
        catch (final Exception e) {
            ProfileMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while migrating certificate contents {0} ", e);
            throw new APIHTTPException("MIG009", new Object[0]);
        }
    }
    
    public void associateProfilesToGroup(final Map profilesWithIds, final Long config_id, final Map requestHeaderMap) {
        ProfileMigrationHandler.logger.log(Level.FINEST, "Going to associate profile to group..");
        try {
            final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
            final DataObject migratedGroupsDO = MDMUtil.getPersistence().get("MigrationGroups", new Criteria(new Column("MigrationGroups", "CONFIG_ID"), (Object)config_id, 0, false));
            if (!migratedGroupsDO.isEmpty()) {
                final Iterator groupIterator = migratedGroupsDO.getRows("MigrationGroups");
                while (groupIterator.hasNext()) {
                    final Row groupRow = groupIterator.next();
                    final String group_name = (String)groupRow.get("GROUP_NAME");
                    final String old_groupId = (String)groupRow.get("MIGRATION_SERVER_GROUP_ID");
                    final Long customer_id = Long.valueOf(requestHeaderMap.get("customer_id").toString());
                    final Long new_group_id = apiServiceDataHandler.getGroupIDForGroupName(group_name, customer_id);
                    final JSONObject responseObject = ProfileMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", ProfileMigrationHandler.meCloudAPIRequestHandler.groupsUrl + "/" + old_groupId + "/" + "profiles", null);
                    final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
                    final JSONArray old_profile_ids_list = responseBodyObject.getJSONArray("profiles");
                    final JSONArray associatedProfileIds = new JSONArray();
                    for (int idIndex = 0; idIndex < old_profile_ids_list.length(); ++idIndex) {
                        final Object new_profile_id = profilesWithIds.get(old_profile_ids_list.getString(idIndex));
                        if (new_profile_id != null) {
                            associatedProfileIds.put((Object)new_profile_id.toString());
                        }
                    }
                    if (associatedProfileIds.length() > 0) {
                        JSONObject requestJson = new JSONObject();
                        requestJson = this.createRequestJsonForProfile(requestJson, requestHeaderMap);
                        requestJson.getJSONObject("msg_header").put("resource_identifier", (Object)new JSONObject().put("group_id", (Object)new_group_id));
                        requestJson.getJSONObject("msg_body").put("profile_ids", (Object)associatedProfileIds);
                        new ProfileFacade().associateProfilesToGroups(requestJson);
                    }
                }
            }
        }
        catch (final Exception e) {
            ProfileMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while associating profile to group {0}..", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void associateProfilesToDevice(final Map profilesWithIds, final Long config_id, final Map requestHeaderMap) {
        ProfileMigrationHandler.logger.log(Level.FINEST, "Going to associate profile to device..");
        try {
            final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
            final DataObject migratedGroupsDO = MDMUtil.getPersistence().get("MigrationDevices", new Criteria(new Column("MigrationDevices", "CONFIG_ID"), (Object)config_id, 0, false));
            if (!migratedGroupsDO.isEmpty()) {
                final DataObject deviceAssociationDO = MDMUtil.getPersistence().get("MigrationDeviceToProfile", (Criteria)null);
                final Iterator deviceIterator = migratedGroupsDO.getRows("MigrationDevices");
                while (deviceIterator.hasNext()) {
                    final Row deviceRow = deviceIterator.next();
                    final String udid = String.valueOf(deviceRow.get("UDID"));
                    final String old_deviceId = String.valueOf(deviceRow.get("MIGRATION_SERVER_DEVICE_ID"));
                    final JSONObject responseObject = ProfileMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", ProfileMigrationHandler.meCloudAPIRequestHandler.deviceUrl + "/" + old_deviceId + "/" + "profiles", null);
                    ProfileMigrationHandler.meCloudAPIRequestHandler.validateResponse(responseObject);
                    final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
                    final JSONArray old_profiles = responseBodyObject.getJSONArray("profiles");
                    for (int i = 0; i < old_profiles.length(); ++i) {
                        final Object new_profile_id = profilesWithIds.get(old_profiles.getJSONObject(i).getString("profile_id"));
                        if (new_profile_id != null) {
                            final Row profileRow = new Row("MigrationDeviceToProfile");
                            profileRow.set("UDID", (Object)udid);
                            profileRow.set("USER_ID", requestHeaderMap.get("user_id"));
                            profileRow.set("PROFILE_ID", new_profile_id);
                            deviceAssociationDO.addRow(profileRow);
                        }
                    }
                }
                MDMUtil.getPersistence().update(deviceAssociationDO);
            }
        }
        catch (final Exception e) {
            ProfileMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while associating profile to device .{0}.", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void migrateSCEPTemplate(final JSONObject payloadResponseJSON, final Map requestHeaderMap) throws Exception {
        try {
            String scep_cert_key = "";
            if (payloadResponseJSON.has("client_cert_id")) {
                scep_cert_key = "client_cert_id";
                ProfileMigrationHandler.logger.log(Level.INFO, "profile contains sso policy content..{0}", payloadResponseJSON.toString());
            }
            else {
                if (!payloadResponseJSON.has("scep_config_id")) {
                    return;
                }
                scep_cert_key = "scep_config_id";
                ProfileMigrationHandler.logger.log(Level.INFO, "profile sce sso policy content..{0}", payloadResponseJSON.toString());
            }
            JSONObject responseObject = new JSONObject();
            String new_server_id = "";
            String new_certificate_id = "";
            Long new_scep_config_id = 0L;
            String old_server_id = "";
            JSONObject requestJson = new JSONObject();
            JSONObject responseBodyObject = new JSONObject();
            final String sourceMethod = "ProfileMigrationHandler::migrateTemplate";
            ProfileMigrationHandler.logger.log(Level.INFO, "{0} --> Starts Template Migration..{1}", new Object[] { sourceMethod, payloadResponseJSON.toString() });
            final String scep_config_id = payloadResponseJSON.remove(scep_cert_key).toString();
            responseObject = ProfileMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", ProfileMigrationHandler.meCloudAPIRequestHandler.apiServerURL + "api/v1/mdm/profiles/scepsettings/" + scep_config_id, null);
            responseBodyObject = responseObject.getJSONObject("ResponseJson");
            final String template_name = responseBodyObject.getString("scep_configuration_name");
            final DataObject scepDO = MDMUtil.getPersistence().get("SCEPConfigurations", new Criteria(new Column("SCEPConfigurations", "SCEP_CONFIGURATION_NAME"), (Object)template_name, 0, false));
            if (!scepDO.isEmpty()) {
                final Row scepRow = scepDO.getRow("SCEPConfigurations");
                new_scep_config_id = (Long)scepRow.get("SCEP_CONFIG_ID");
                ProfileMigrationHandler.logger.log(Level.INFO, "Template already Migrated..{0}", new_scep_config_id);
            }
            else {
                ProfileMigrationHandler.logger.log(Level.INFO, "Going to start Template Migration..");
                final Long customer_id = requestHeaderMap.get("customer_id");
                final String ca_finger_print = responseBodyObject.getString("ca_finger_print");
                final String server_url = responseBodyObject.getString("url");
                JSONObject templateObject = new JSONObject();
                responseObject = ProfileMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", ProfileMigrationHandler.meCloudAPIRequestHandler.apiServerURL + "api/v1/mdm/scep/servers", null);
                responseBodyObject = responseObject.getJSONObject("ResponseJson");
                final JSONArray scep_servers = responseBodyObject.getJSONArray("scep_servers");
                for (int serverIndex = 0; serverIndex < scep_servers.length(); ++serverIndex) {
                    final JSONObject server = scep_servers.getJSONObject(serverIndex);
                    if (server.getString("url").equalsIgnoreCase(server_url)) {
                        old_server_id = server.getString("server_id");
                        final DataObject serverDataObject = this.getServerDataObj(customer_id, server_url);
                        if (!serverDataObject.isEmpty()) {
                            final Row certificateRow = serverDataObject.getRow("SCEPServers");
                            new_server_id = String.valueOf(certificateRow.get("SERVER_ID"));
                        }
                        else {
                            responseBodyObject.keySet().clear();
                            final DataObject certDataObject = this.getCertificateDataObj(customer_id, ca_finger_print);
                            if (!certDataObject.isEmpty()) {
                                final Row certificateRow2 = certDataObject.getRow("CredentialCertificateInfo");
                                new_certificate_id = String.valueOf(certificateRow2.get("CERTIFICATE_ID"));
                            }
                            else if (server.has("ca_certificate_id")) {
                                final String certificate_id = server.getString("ca_certificate_id");
                                responseObject = ProfileMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", ProfileMigrationHandler.meCloudAPIRequestHandler.apiServerURL + "api/v1/mdm/profiles/certificates/" + certificate_id, null);
                                responseBodyObject = responseObject.getJSONObject("ResponseJson");
                                responseBodyObject = this.migrateCertificate(responseBodyObject, requestHeaderMap);
                                new_certificate_id = String.valueOf(responseBodyObject.opt("certificate_id"));
                            }
                            server.put("ca_certificate_id", (Object)new_certificate_id);
                            requestJson = this.createRequestJsonForProfile(server, requestHeaderMap);
                            final Long customerId = APIUtil.getCustomerID(requestJson);
                            final Long loginId = APIUtil.getLoginID(requestJson);
                            new ScepServerFacade().addScepServer(customerId, loginId, requestJson);
                            new_server_id = String.valueOf(server.get("SERVER_ID"));
                        }
                        final String url = ProfileMigrationHandler.meCloudAPIRequestHandler.apiServerURL + "api/v1/mdm/scep/servers/" + old_server_id + "/templates/" + scep_config_id;
                        templateObject = ProfileMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", url, null);
                        templateObject = templateObject.getJSONObject("ResponseJson");
                    }
                    templateObject.remove("scep_config_id");
                    requestJson = this.createRequestJsonForProfile(templateObject, requestHeaderMap);
                    requestJson.getJSONObject("msg_header").put("request_url", (Object)"/scep/servers");
                    requestJson.getJSONObject("msg_header").getJSONObject("resource_identifier").put("server_id", (Object)new_server_id);
                    templateObject = new SCEPFacade().addSCEPConfiguration(requestJson);
                    new_scep_config_id = templateObject.getLong("scep_config_id");
                    ProfileMigrationHandler.logger.log(Level.INFO, "Template Migrated successfully..{0}", new_scep_config_id);
                }
            }
            payloadResponseJSON.put(scep_cert_key, (Object)new_scep_config_id);
        }
        catch (final Exception ex) {
            ProfileMigrationHandler.logger.log(Level.SEVERE, "Error while mirating template {0}", ex);
            throw new APIHTTPException("MIG011", new Object[0]);
        }
    }
    
    public void migrateCustomConfiguration(final JSONObject payloadResponseJSON, final Map requestHeaderMap) {
        try {
            if (payloadResponseJSON.has("syncml_commands")) {
                ProfileMigrationHandler.logger.log(Level.INFO, "profile contains custom profile content..{0}", payloadResponseJSON.toString());
                payloadResponseJSON.remove("custom_profile_file_id");
            }
            else if (payloadResponseJSON.has("custom_profile_id")) {
                ProfileMigrationHandler.logger.log(Level.INFO, "profile contains custom profile content..{0}", payloadResponseJSON.toString());
                final String custom_profile_id = payloadResponseJSON.remove("custom_profile_id").toString();
                final String custom_profile_file_id = ProfileMigrationHandler.meCloudAPIRequestHandler.fileUpload(custom_profile_id, requestHeaderMap, 4);
                payloadResponseJSON.keySet().clear();
                payloadResponseJSON.put("custom_profile_file_id", (Object)custom_profile_file_id);
                payloadResponseJSON.put("allow_existing_payload", (Object)Boolean.TRUE);
            }
        }
        catch (final Exception e) {
            ProfileMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while migrating custom config content {0} ", e);
            throw new APIHTTPException("MIG012", new Object[0]);
        }
    }
    
    public void revokeProfileMigration(final Map profilesMigrationMap, final JSONObject migratedProfile, final int status, final String errorMsg, final JSONObject profile, final String old_profile_id, final Long config_id, final Map requestHeaderMap) {
        final Long user_id = Long.valueOf(requestHeaderMap.get("user_id").toString());
        final Long customer_id = Long.valueOf(requestHeaderMap.get("customer_id").toString());
        final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
        profilesMigrationMap.remove(old_profile_id);
        migratedProfile.put("is_migrated", (Object)Boolean.FALSE);
        migratedProfile.put("remarks", (Object)errorMsg);
        apiServiceDataHandler.updateMigrationStatus(config_id, status, errorMsg, 1);
        apiServiceDataHandler.deleteMigratedProfiles(new JSONArray().put((Object)profile), user_id, customer_id);
    }
    
    public DataObject getCertificateDataObj(final Long customerId, final String certThumbPrint) throws DataAccessException {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CredentialCertificateInfo"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria customerIdCriteria = new Criteria(new Column("CredentialCertificateInfo", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria certificateIdCriteria = new Criteria(new Column("CredentialCertificateInfo", "CERTIFICATE_THUMBPRINT"), (Object)certThumbPrint, 0);
            selectQuery.setCriteria(customerIdCriteria.and(certificateIdCriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            return dataObject;
        }
        catch (final DataAccessException e) {
            ProfileMigrationHandler.logger.log(Level.SEVERE, "Exception in getting certificate details", (Throwable)e);
            throw e;
        }
    }
    
    public DataObject getServerDataObj(final Long customerId, final String server_url) throws DataAccessException {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SCEPServers"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria customerIdCriteria = new Criteria(new Column("SCEPServers", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria serverUrlCriteria = new Criteria(new Column("SCEPServers", "URL"), (Object)server_url, 0, false);
            selectQuery.setCriteria(customerIdCriteria.and(serverUrlCriteria));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            return dataObject;
        }
        catch (final DataAccessException e) {
            ProfileMigrationHandler.logger.log(Level.SEVERE, "Exception in getting server details", (Throwable)e);
            throw e;
        }
    }
    
    static {
        ProfileMigrationHandler.logger = Logger.getLogger("MDMMigrationLogger");
        ProfileMigrationHandler.meCloudAPIRequestHandler = null;
    }
}
