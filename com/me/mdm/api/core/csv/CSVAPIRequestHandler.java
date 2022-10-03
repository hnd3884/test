package com.me.mdm.api.core.csv;

import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.i18n.I18N;
import com.me.mdm.api.error.APIError;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.me.mdm.files.FileFacade;
import com.me.mdm.files.upload.FileUploadManager;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.easmanagement.EASMgmt;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.config.WebContentFilterStatusHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONArray;
import com.me.mdm.server.profiles.ProfileFacade;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.adminenroll.UserAssignmentStatusHandler;
import com.me.mdm.server.enrollment.admin.BaseAdminEnrollmentHandler;
import com.me.devicemanagement.framework.server.csv.CSVImportStatusHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class CSVAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public CSVAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject request = apiRequest.toJSONObject();
            final Long customerId = APIUtil.getCustomerID(request);
            final APIUtil apiUtil = new APIUtil();
            String operation = APIUtil.getStringFilter(request, "operation");
            if (operation == null || operation.length() == 0) {
                throw new APIHTTPException("COM0024", new Object[] { "operation" });
            }
            final String[] operations = operation.split("-");
            operation = operations[0];
            CSVImportStatusHandler csvImportStatusHandler = CSVImportStatusHandler.getInstance();
            final String s = operation;
            switch (s) {
                case "device_details_bulk_update": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Inventory_Write", "MDM_Inventory_Admin" })) {
                        operation = "MDCustomDetails";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "web_content_filter": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Configurations_Write", "MDM_Configurations_Admin,ModernMgmt_Configurations_Write,ModernMgmt_Configurations_Admin" })) {
                        operation = "WebContentFilter";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "admin_enroll_bulk_assign": {
                    if (!apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Enrollment_Write", "MDM_Enrollment_Admin" })) {
                        throw new APIHTTPException("COM0013", new Object[0]);
                    }
                    if (operations.length == 1) {
                        operation = BaseAdminEnrollmentHandler.getInstance(-1).getOperationLabelForTemplate();
                        csvImportStatusHandler = UserAssignmentStatusHandler.getInstance();
                        break;
                    }
                    if (Integer.parseInt(operations[1]) == 10) {
                        operation = "BulkDEP";
                        csvImportStatusHandler = CSVImportStatusHandler.getInstance();
                        break;
                    }
                    operation = BaseAdminEnrollmentHandler.getInstance(Integer.parseInt(operations[1])).getOperationLabelForTemplate();
                    csvImportStatusHandler = UserAssignmentStatusHandler.getInstance();
                    break;
                }
                case "import_devices_to_group_csv": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Configurations_Write", "MDM_Configurations_Admin", "MDM_AppMgmt_Write", "MDM_AppMgmt_Admin", "MDM_ContentMgmt_Write", "MDM_ContentMgmt_Admin" })) {
                        operation = "CustomGroupImport";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "filevault_recovery_key_import": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_EncryptionMgmt_Admin", "ModernMgmt_EncryptionMgmt_Admin" })) {
                        operation = "MacFilevaultKeyImport";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "bulk_enrollment_csv": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Enrollment_Write", "MDM_Enrollment_Admin" })) {
                        operation = "BulkEnroll";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "bulk_deprovision_csv": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Enrollment_Write", "MDM_Enrollment_Admin" })) {
                        operation = "BulkDeprovision";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "user_import_csv": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Enrollment_Write", "MDM_Enrollment_Admin" })) {
                        operation = "ManagedUserImport";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "eas_policy_csv": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Settings_Write", "MDM_Settings_Admin" })) {
                        operation = "EASPolicy_BulkAssignUser";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
            }
            final org.json.simple.JSONObject props = csvImportStatusHandler.getImportStatus(customerId, operation);
            final JSONObject temp = new JSONObject(props.toJSONString());
            final JSONObject result = new JSONObject();
            JSONObject tempdetails = null;
            final String value = String.valueOf(temp.get("STATUS"));
            switch (value) {
                case "NO_HISTORY": {
                    result.put("status_id", 0);
                    break;
                }
                case "IN_PROGRESS": {
                    tempdetails = temp.getJSONObject("DETAILS");
                    result.put("status_id", 1);
                    result.put("completed", tempdetails.get("COMPLETED"));
                    result.put("total", tempdetails.get("TOTAL"));
                    break;
                }
                case "COMPLETED": {
                    tempdetails = temp.getJSONObject("DETAILS");
                    result.put("status_id", 2);
                    result.put("success", tempdetails.get("SUCCESS"));
                    result.put("failure", tempdetails.get("FAILURE"));
                    final String s2 = operation;
                    switch (s2) {
                        case "MDCustomDetails": {
                            result.put("error_csv", (Object)"/deviceCustomColumnErrorDetails.csv?fileName=ErrorReport");
                            break;
                        }
                        case "CustomGroupImport": {
                            result.put("error_csv", (Object)"/deviceToCustomGroupImportErrorDetails.csv?fileName=ErrorReport");
                            break;
                        }
                        case "BulkDeprovision": {
                            result.put("IOSDeviceCount", (Object)CustomerParamsHandler.getInstance().getParameterValue("BulkDeprovision_IOSDeviceCount", (long)customerId));
                            result.put("error_csv", (Object)"/deprovisionErrorDetails.csv?fileName=ErrorReport");
                            break;
                        }
                        case "BulkEnroll": {
                            result.put("error_csv", (Object)"/bulkEnrollmentErrorDetails.csv?fileName=ErrorReport");
                            break;
                        }
                        case "ManagedUserImport": {
                            result.put("error_csv", (Object)"/managedUserEmberImportErrorDetails.csv?fileName=ErrorReport");
                            break;
                        }
                        case "WebContentFilter": {
                            result.put("error_csv", (Object)"/webContentFilterUrlImportErrorDetails.csv?fileName=ErrorReport");
                            break;
                        }
                        case "EASPolicy_BulkAssignUser": {
                            result.put("error_csv", (Object)"/easPolicyBulkUserAssignErrorDetails.csv?fileName=ErrorReport");
                            break;
                        }
                    }
                    if (!operations[0].equalsIgnoreCase("admin_enroll_bulk_assign")) {
                        break;
                    }
                    if (operations.length == 1) {
                        result.put("error_csv", (Object)"/assignUserErrorDetails.csv?fileName=ErrorReport");
                        break;
                    }
                    if (Integer.parseInt(operations[1]) == 10) {
                        result.put("error_csv", (Object)"/bulkDEPErrorDetails.csv?fileName=ErrorReport");
                        break;
                    }
                    result.put("error_csv", (Object)("/assignUserErrorDetails.csv?fileName=ErrorReport&enrollmentTemplate=" + Integer.parseInt(operations[1])));
                    break;
                }
            }
            final JSONObject response = new JSONObject();
            response.put("RESPONSE", (Object)result);
            response.put("status", 200);
            return response;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "exception occurred in doGet", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        this.processCSVPostRequest(apiRequest.toJSONObject());
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 202);
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "JSONException", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    void processCSVPostRequest(final JSONObject request) throws APIHTTPException {
        try {
            final JSONObject body = request.getJSONObject("msg_body");
            if (!body.has("csv_file") && (!body.has("is_url_change") || body.getBoolean("is_url_change"))) {
                throw new APIHTTPException("COM0005", new Object[] { "csv_file" });
            }
            if (!body.has("operation")) {
                throw new APIHTTPException("COM0005", new Object[] { "operation" });
            }
            final APIUtil apiUtil = new APIUtil();
            String operation = String.valueOf(body.get("operation"));
            final String[] operations = operation.split("-");
            operation = operations[0];
            final org.json.simple.JSONObject inputJSON = new org.json.simple.JSONObject();
            CSVImportStatusHandler csvImportStatusHandler = CSVImportStatusHandler.getInstance();
            final String s = operation;
            switch (s) {
                case "device_details_bulk_update": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Inventory_Write", "MDM_Inventory_Admin" })) {
                        operation = "MDCustomDetails";
                        csvImportStatusHandler = CSVImportStatusHandler.getInstance();
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "web_content_filter": {
                    if (!apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Configurations_Write", "MDM_Configurations_Admin,ModernMgmt_Configurations_Write,ModernMgmt_Configurations_Admin" })) {
                        throw new APIHTTPException("COM0013", new Object[0]);
                    }
                    operation = "WebContentFilter";
                    final ProfileFacade profileFacade = new ProfileFacade();
                    final long profileID = body.getLong("profile_id");
                    final long configDataItemID = body.optLong("config_data_item_id", -1L);
                    final String payloadName = body.getString("payload_name");
                    final Long collectionID = profileFacade.validateProfilePayloadForModification(profileID, configDataItemID, APIUtil.getCustomerID(request), payloadName);
                    if (body.has("is_url_change") && !body.getBoolean("is_url_change")) {
                        final JSONObject jsonObject = new JSONObject();
                        jsonObject.put("collection_id", (Object)collectionID);
                        jsonObject.put("profile_id", profileID);
                        jsonObject.put("filter_type", 1);
                        jsonObject.put("payload_name", (Object)payloadName);
                        jsonObject.put("CONFIG_DATA_ITEM_ID", configDataItemID);
                        jsonObject.put("malicious_content_filter", body.optBoolean("malicious_content_filter"));
                        jsonObject.put("url_filter_type", body.getBoolean("url_filter_type"));
                        jsonObject.put("create_bookmarks", body.optBoolean("create_bookmarks", false));
                        jsonObject.put("enable_auto_filter", body.optBoolean("enable_auto_filter", false));
                        jsonObject.put("is_url_change", body.optBoolean("is_url_change", false));
                        jsonObject.put("url_details", (Object)new JSONArray());
                        jsonObject.put("upload_type", 2);
                        if (body.has("permitted_urls")) {
                            jsonObject.put("permitted_urls", (Object)body.getJSONArray("permitted_urls"));
                        }
                        jsonObject.put("PROFILE_COLLECTION_STATUS", (int)DBUtil.getValueFromDB("CollectionStatus", "COLLECTION_ID", (Object)collectionID, "PROFILE_COLLECTION_STATUS"));
                        jsonObject.put("PLATFORM_TYPE", new ProfileUtil().getPlatformType(profileID));
                        profileFacade.addOrModifyConfigDataItem(jsonObject, collectionID, APIUtil.getCustomerID(request), APIUtil.getUserID(request));
                        return;
                    }
                    inputJSON.put((Object)"collection_id", (Object)collectionID);
                    inputJSON.put((Object)"profile_id", (Object)profileID);
                    inputJSON.put((Object)"payload_name", (Object)payloadName);
                    inputJSON.put((Object)"config_data_item_id", (Object)configDataItemID);
                    inputJSON.put((Object)"malicious_content_filter", (Object)body.optBoolean("malicious_content_filter"));
                    inputJSON.put((Object)"filter_type", (Object)1);
                    inputJSON.put((Object)"url_filter_type", (Object)body.getBoolean("url_filter_type"));
                    inputJSON.put((Object)"create_bookmarks", (Object)body.optBoolean("create_bookmarks", false));
                    inputJSON.put((Object)"enable_auto_filter", (Object)body.optBoolean("enable_auto_filter", false));
                    if (body.has("permitted_urls")) {
                        inputJSON.put((Object)"permitted_urls", (Object)body.getJSONArray("permitted_urls"));
                    }
                    csvImportStatusHandler = WebContentFilterStatusHandler.getInstance();
                    break;
                }
                case "import_devices_to_group_csv": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Configurations_Write", "MDM_Configurations_Admin", "MDM_AppMgmt_Write", "MDM_AppMgmt_Admin", "MDM_ContentMgmt_Admin", "MDM_ContentMgmt_Write" })) {
                        operation = "CustomGroupImport";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "admin_enroll_bulk_assign": {
                    if (!apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Enrollment_Write", "MDM_Enrollment_Admin" })) {
                        throw new APIHTTPException("COM0013", new Object[0]);
                    }
                    if (operations.length == 1) {
                        operation = BaseAdminEnrollmentHandler.getInstance(-1).getOperationLabelForTemplate();
                        inputJSON.put((Object)"EnrollmentTemplate", (Object)(-1));
                        csvImportStatusHandler = UserAssignmentStatusHandler.getInstance();
                        break;
                    }
                    if (Integer.parseInt(operations[1]) == 10) {
                        operation = "BulkDEP";
                        inputJSON.put((Object)"EnrollmentTemplate", (Object)Integer.parseInt(operations[1]));
                        csvImportStatusHandler = CSVImportStatusHandler.getInstance();
                        break;
                    }
                    operation = BaseAdminEnrollmentHandler.getInstance(Integer.parseInt(operations[1])).getOperationLabelForTemplate();
                    inputJSON.put((Object)"EnrollmentTemplate", (Object)Integer.parseInt(operations[1]));
                    csvImportStatusHandler = UserAssignmentStatusHandler.getInstance();
                    break;
                }
                case "bulk_enrollment_csv": {
                    if (!apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Enrollment_Write", "MDM_Enrollment_Admin" })) {
                        throw new APIHTTPException("COM0013", new Object[0]);
                    }
                    operation = "BulkEnroll";
                    if (ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured()) {
                        inputJSON.put((Object)"sendInvitation", (Object)true);
                        inputJSON.put((Object)"sendEmail", (Object)true);
                    }
                    if (MDMApiFactoryProvider.getSMSAPI().isSMSSettingsConfigured()) {
                        inputJSON.put((Object)"sendSMS", (Object)true);
                        break;
                    }
                    break;
                }
                case "bulk_deprovision_csv": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Enrollment_Write", "MDM_Enrollment_Admin" })) {
                        operation = "BulkDeprovision";
                        inputJSON.put((Object)"COMMENT", (Object)body.optString("other_reason"));
                        inputJSON.put((Object)"DEPROVISION_REASON", (Object)body.optInt("wipe_reason"));
                        inputJSON.put((Object)"DEPROVISION_TYPE", (Object)body.optInt("wipe_type"));
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "user_import_csv": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Enrollment_Write", "MDM_Enrollment_Admin" })) {
                        operation = "ManagedUserImport";
                        final boolean sendInvite = body.optBoolean("send_invite", false);
                        if (ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured() && sendInvite) {
                            inputJSON.put((Object)"send_invite", (Object)sendInvite);
                        }
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "eas_policy_csv": {
                    if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("bulkAssignEasUsers")) {
                        this.logger.log(Level.WARNING, "Feature Param not enabled to upload eas CSV file upload");
                        throw new APIHTTPException("COM0013", new Object[0]);
                    }
                    if (!apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Settings_Write", "MDM_Settings_Admin" })) {
                        throw new APIHTTPException("COM0013", new Object[0]);
                    }
                    final org.json.simple.JSONObject ceaDetailsReq = new org.json.simple.JSONObject();
                    ceaDetailsReq.put((Object)"EASServerDetails", (Object)"true");
                    final org.json.simple.JSONObject ceaDetails = EASMgmt.getInstance().getCEAdetails(ceaDetailsReq);
                    if (ceaDetails == null || ceaDetails.isEmpty()) {
                        throw new APIHTTPException("CEA0001", new Object[0]);
                    }
                    final Long easServerId = (Long)ceaDetails.get((Object)"EAS_SERVER_ID");
                    inputJSON.put((Object)"GRACE_DAYS", (Object)body.getInt("grace_days"));
                    inputJSON.put((Object)"APPLIED_FOR", (Object)body.getInt("applied_for"));
                    inputJSON.put((Object)"UPDATE_POLICY_SELECTION", (Object)String.valueOf(Boolean.TRUE));
                    inputJSON.put((Object)"UPDATED_BY", (Object)MDMUtil.getInstance().getCurrentlyLoggedOnUserID());
                    inputJSON.put((Object)"SEND_NOTIF_MAIL", (Object)body.getInt("SEND_NOTIF_MAIL".toLowerCase()));
                    inputJSON.put((Object)"ROLLBACK_BLOCKED_DEVICES", (Object)String.valueOf(body.get("rollback_blocked_devices")));
                    if (!inputJSON.containsKey((Object)"APPLIED_FOR") && (int)inputJSON.get((Object)"APPLIED_FOR") != 2) {
                        throw new APIHTTPException("CEA0002", new Object[0]);
                    }
                    operation = "EASPolicy_BulkAssignUser";
                    inputJSON.put((Object)"EAS_SERVER_ID", (Object)easServerId);
                    break;
                }
                case "filevault_recovery_key_import": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "ModernMgmt_EncryptionMgmt_Admin", "MDM_EncryptionMgmt_Admin" })) {
                        operation = "MacFilevaultKeyImport";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
            }
            final Long csvFileID = Long.valueOf(String.valueOf(body.optString("csv_file")));
            final String csvFile = String.valueOf(FileUploadManager.getFilePath(JSONUtil.toJSON("file_id", csvFileID)).get("file_path"));
            new FileFacade().writeFile(csvFile, ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(csvFile));
            final Long customerID = APIUtil.getCustomerID(request);
            final boolean bomPresent = MDMEnrollmentRequestHandler.getInstance().isBOMPresent(ApiFactoryProvider.getFileAccessAPI().readFile(csvFile));
            final boolean isUTFencoding = MDMEnrollmentRequestHandler.getInstance().isUTFencoding(ApiFactoryProvider.getFileAccessAPI().readFile(csvFile));
            if (!isUTFencoding || (isUTFencoding && bomPresent)) {
                throw new APIHTTPException("CSV0001", new Object[0]);
            }
            final org.json.simple.JSONObject props = csvImportStatusHandler.processCSVFile(ApiFactoryProvider.getFileAccessAPI().readFile(csvFile), inputJSON, customerID, operation);
            if (props.containsKey((Object)"STATUS") && props.get((Object)"STATUS").equals("FAILURE")) {
                if (props.containsKey((Object)"CODE")) {
                    final APIError error = new APIError();
                    if (props.containsKey((Object)"CODE")) {
                        error.setErrorCode(String.valueOf(props.get((Object)"CODE")));
                    }
                    else {
                        error.setErrorCode("CSV0002");
                    }
                    error.setErrorCode(String.valueOf(props.get((Object)"CODE")));
                    error.setI18nKey(String.valueOf(props.get((Object)"CAUSE")));
                    error.setErrorMsg(I18N.getLocale());
                    error.setHttpStatus(400);
                    throw new APIHTTPException(error);
                }
                throw new APIHTTPException("CSV0003", new Object[0]);
            }
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception occurred in processCSVPostRequest", e);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final Long customerId = APIUtil.getCustomerID(requestJSON);
            String operation = APIUtil.getStringFilter(requestJSON, "operation");
            if (operation == null || operation.length() == 0) {
                throw new APIHTTPException("COM0024", new Object[] { "operation" });
            }
            final APIUtil apiUtil = APIUtil.getNewInstance();
            final String[] operations = operation.split("-");
            final String s;
            operation = (s = operations[0]);
            switch (s) {
                case "device_details_bulk_update": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Inventory_Write", "MDM_Inventory_Admin" })) {
                        operation = "MDCustomDetails";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "admin_enroll_bulk_assign": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Enrollment_Write", "MDM_Enrollment_Admin" })) {
                        if (operations.length == 1) {
                            operation = BaseAdminEnrollmentHandler.getInstance(-1).getOperationLabelForTemplate();
                        }
                        else if (Integer.parseInt(operations[1]) == 10) {
                            operation = "BulkDEP";
                        }
                        else {
                            operation = BaseAdminEnrollmentHandler.getInstance(Integer.parseInt(operations[1])).getOperationLabelForTemplate();
                        }
                        MDMEnrollmentUtil.getInstance().clearAssignUserImportInfoDetails(customerId);
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "bulk_enrollment_csv": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Enrollment_Write", "MDM_Enrollment_Admin" })) {
                        operation = "BulkEnroll";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "bulk_deprovision_csv": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Enrollment_Write", "MDM_Enrollment_Admin" })) {
                        operation = "BulkDeprovision";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "user_import_csv": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Enrollment_Write", "MDM_Enrollment_Admin" })) {
                        operation = "ManagedUserImport";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                case "import_devices_to_group_csv": {
                    if (apiUtil.checkRolesForCurrentUser(new String[] { "MDM_Configurations_Write", "MDM_Configurations_Admin", "MDM_AppMgmt_Write", "MDM_AppMgmt_Admin" })) {
                        operation = "CustomGroupImport";
                        break;
                    }
                    throw new APIHTTPException("COM0013", new Object[0]);
                }
                default: {
                    throw new APIHTTPException("COM0024", new Object[] { "operation" });
                }
            }
            CSVImportStatusHandler.getInstance().clearImportStatus(customerId, operation);
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            return response;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception in clear csv status", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
