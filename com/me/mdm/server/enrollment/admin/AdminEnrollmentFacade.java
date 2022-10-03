package com.me.mdm.server.enrollment.admin;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.core.enrollment.settings.UserAssignmentRuleHandler;
import com.me.mdm.core.enrollment.OutOfBoxEnrollmentSettingsHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.Iterator;
import java.util.Set;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.List;
import com.me.mdm.server.resource.MDMResourceDataProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.core.enrollment.AdminDeviceHandler;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.chrome.ChromeOAuthHandler;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AdminEnrollmentFacade
{
    public static final Logger LOGGER;
    
    public JSONObject getChromeRedirectURL(final JSONObject requestJSON) throws Exception {
        final String adminId = String.valueOf(requestJSON.getJSONObject("msg_body").get("admin_email"));
        final String signupUrl = new ChromeOAuthHandler().generateChromeOAuthURL(adminId);
        final JSONObject response = new JSONObject();
        response.put("redirect_url", (Object)signupUrl);
        return response;
    }
    
    public JSONObject getChromeEnrollDetails(final JSONObject requestJSON) throws Exception {
        final JSONObject response = new JSONObject();
        final JSONObject googleSettings = GoogleForWorkSettings.getGoogleForWorkSettings(APIUtil.getCustomerID(requestJSON), GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
        if (googleSettings.optBoolean("isConfigured", (boolean)Boolean.FALSE)) {
            final String enterpriseID = googleSettings.getString("ENTERPRISE_ID");
            final JSONObject managedDomain = new JSONObject();
            managedDomain.put("DIR_INTEG", SyMUtil.isStringEmpty(enterpriseID));
            managedDomain.put("ESA_EMAIL_ID", googleSettings.get("ESA_EMAIL_ID"));
            managedDomain.put("MANAGED_DOMAIN_NAME", googleSettings.get("MANAGED_DOMAIN_NAME"));
            managedDomain.put("DOMAIN_ADMIN_EMAIL_ID", googleSettings.get("DOMAIN_ADMIN_EMAIL_ID"));
            final JSONArray managedDomains = new JSONArray();
            managedDomains.put((Object)managedDomain);
            response.put("managed_domains", (Object)managedDomains);
        }
        else {
            response.put("managed_domains", (Object)new JSONArray());
        }
        return response;
    }
    
    public JSONObject removeChromeIntegration(final JSONObject requestJSON) throws Exception {
        final JSONObject unenrollData = new JSONObject();
        final JSONObject googleSettings = GoogleForWorkSettings.getGoogleForWorkSettings(APIUtil.getCustomerID(requestJSON), GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
        final Boolean isConfigured = googleSettings.getBoolean("isConfigured");
        if (!isConfigured) {
            throw new APIHTTPException("COM0027", new Object[0]);
        }
        unenrollData.put("EnterpriseId", googleSettings.get("ENTERPRISE_ID"));
        final String domainName = String.valueOf(googleSettings.get("MANAGED_DOMAIN_NAME"));
        final JSONObject dataJSON = new JSONObject();
        dataJSON.put("Data", (Object)unenrollData);
        final JSONObject responseJSON = new JSONObject();
        GoogleForWorkSettings.resetSettings(APIUtil.getCustomerID(requestJSON), GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
        responseJSON.put("Status", (Object)"Success");
        MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo().get("UserName"), "dc.mdm.actionlog.afw.unenrolleddomain", domainName, APIUtil.getCustomerID(requestJSON));
        return responseJSON;
    }
    
    public JSONObject syncChromeDevice(final JSONObject requestJSON) throws Exception {
        final JSONObject resultJSON = new JSONObject();
        final Long customerID = APIUtil.getCustomerID(requestJSON);
        JSONObject msgBody = new JSONObject();
        if (requestJSON.has("msg_body")) {
            msgBody = requestJSON.getJSONObject("msg_body");
        }
        final Properties taskProps = new Properties();
        ((Hashtable<String, Long>)taskProps).put("CUSTOMER_ID", customerID);
        ((Hashtable<String, Boolean>)taskProps).put("RE_INTEG", msgBody.optBoolean("re_integ", false));
        final JSONObject googleESAJSON = GoogleForWorkSettings.getGoogleForWorkSettings(customerID, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
        final Boolean isConfigured = googleESAJSON.getBoolean("isConfigured");
        if (!isConfigured) {
            throw new APIHTTPException("COM0027", new Object[0]);
        }
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "SyncChromeDevices");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.chrome.agent.enrollment.ChromeDeviceSyncTask", taskInfoMap, taskProps);
        CustomerParamsHandler.getInstance().addOrUpdateParameter("ChromeDeviceSyncStatus", "Queued", (long)customerID);
        resultJSON.put("Status", (Object)"Success");
        return resultJSON;
    }
    
    public JSONObject getSyncChromeDeviceStatus(final JSONObject requestJSON) throws Exception {
        final JSONObject resultJSON = new JSONObject();
        final Long customerID = APIUtil.getCustomerID(requestJSON);
        final Properties taskProps = new Properties();
        ((Hashtable<String, Long>)taskProps).put("CUSTOMER_ID", customerID);
        final JSONObject googleESAJSON = GoogleForWorkSettings.getGoogleForWorkSettings(customerID, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
        final Boolean isConfigured = googleESAJSON.getBoolean("isConfigured");
        if (!isConfigured) {
            throw new APIHTTPException("COM0027", new Object[0]);
        }
        final String status = CustomerParamsHandler.getInstance().getParameterValue("ChromeDeviceSyncStatus", (long)customerID);
        if (MDMStringUtils.isEmpty(status)) {
            resultJSON.put("status", (Object)"NoStatus");
        }
        else {
            resultJSON.put("status", (Object)status);
        }
        return resultJSON;
    }
    
    public JSONObject addChromeEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        final Long userID = APIUtil.getUserID(requestJSON);
        final Long customerID = APIUtil.getCustomerID(requestJSON);
        final JSONObject msgBody = requestJSON.getJSONObject("msg_body");
        final JSONObject formData = new JSONObject();
        formData.put("DIR_INTEG", msgBody.optBoolean("dir_integ", false));
        formData.put("SERVICE_TYPE", (Object)GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
        formData.put("DOMAIN_NAME", (Object)String.valueOf(msgBody.get("domain")));
        formData.put("ADMIN_ACCOUNT_ID", (Object)String.valueOf(msgBody.get("admin_email")));
        formData.put("AUTHORIZATION_CODE", (Object)String.valueOf(msgBody.get("auth_code")));
        JSONObject resultJSON = GoogleForWorkSettings.persistSettings(customerID, userID, formData);
        if (String.valueOf(resultJSON.get("Status")).equalsIgnoreCase("success")) {
            boolean dirInteg = false;
            try {
                final JSONObject googleSettings = GoogleForWorkSettings.getGoogleForWorkSettings(customerID, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
                final String enterpriseID = googleSettings.getString("ENTERPRISE_ID");
                dirInteg = SyMUtil.isStringEmpty(enterpriseID);
            }
            catch (final Exception ex) {
                Logger.getLogger(AdminEnrollmentFacade.class.getName()).log(Level.FINE, null, ex);
            }
            resultJSON = new JSONObject();
            resultJSON.put("status", (Object)"success");
            resultJSON.put("dir_integ", dirInteg);
            resultJSON.put("domain", (Object)String.valueOf(msgBody.get("domain")));
            resultJSON.put("admin_email", (Object)String.valueOf(msgBody.get("admin_email")));
        }
        return resultJSON;
    }
    
    public JSONObject getAdminEnrollmentDetails(final JSONObject requestJSON) throws Exception {
        final Integer templateType = APIUtil.getResourceID(requestJSON, "template_id").intValue();
        final BaseAdminEnrollmentHandler baseAdminEnrollmentHandler = BaseAdminEnrollmentHandler.getInstance(templateType);
        if (baseAdminEnrollmentHandler == null) {
            throw new APIHTTPException("COM0005", new Object[] { "template_id" });
        }
        final JSONObject response = baseAdminEnrollmentHandler.getEnrollmentDetails(requestJSON);
        final Long userID = APIUtil.getUserID(requestJSON);
        response.put("TEMPLATE_ID", (Object)String.valueOf(EnrollmentTemplateHandler.getTemplateIdForTemplateType(templateType, userID, APIUtil.getCustomerID(requestJSON))));
        return response;
    }
    
    public JSONObject removeDevice(final JSONObject requestJSON) throws Exception {
        final JSONObject response = BaseAdminEnrollmentHandler.removeDevice(requestJSON);
        return response;
    }
    
    public JSONObject removeNFCAdminApp(final JSONObject requestJSON) throws Exception {
        final Long customerID = APIUtil.getCustomerID(requestJSON);
        final String udid = (String)requestJSON.getJSONObject("msg_body").opt("udid");
        if (udid == null) {
            throw new APIHTTPException("COM0009", new Object[] { "udid" });
        }
        final String modelName = new AdminDeviceHandler().getAdminDeviceModelName(udid);
        new AdminDeviceHandler().removeAdminDevice(udid);
        final Object remarksArgs = modelName + "@@@" + DMUserHandler.getDCUser(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID());
        final String i18n = "dc.mdm.adminagent.host_removed_msg";
        final Long currentlyLoggedInUserLoginId = APIUtil.getLoginID(requestJSON);
        final String userName = DMUserHandler.getDCUser(currentlyLoggedInUserLoginId);
        MDMEventLogHandler.getInstance().MDMEventLogEntry(121, null, userName, i18n, remarksArgs, customerID);
        return null;
    }
    
    public String getDownloadFile(final JSONObject requestJSON) throws Exception {
        final Integer template = APIUtil.getIntegerFilter(requestJSON, "template_id");
        if (template != 31 && template != 30) {
            throw new APIHTTPException("COM0024", new Object[] { "template_id" });
        }
        final Properties contactInfoProps = DMUserHandler.getContactInfoProp(APIUtil.getUserID(requestJSON));
        if (contactInfoProps.containsKey("EMAIL_ID") && ((Hashtable<K, String>)contactInfoProps).get("EMAIL_ID").trim().isEmpty()) {
            throw new APIHTTPException("COM0009", new Object[0]);
        }
        final AdminEnrollmentDownloadInterface adminEnrollmentDownloadInterface = (AdminEnrollmentDownloadInterface)BaseAdminEnrollmentHandler.getInstance(template);
        return adminEnrollmentDownloadInterface.getFileDownloadPath(requestJSON);
    }
    
    public JSONObject getStagedDeviceInfo(final JSONObject request) throws Exception {
        final JSONObject resposne = new JSONObject();
        final Long devID = APIUtil.getLongFilter(request, "device_id");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceForEnrollment"));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceEnrollmentToUser", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentProps", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "EnrollmentTemplateToDeviceEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "AppleDEPDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "AppleConfigDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "WinAzureADDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "WindowsLaptopDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "WinModernMgmtDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "WindowsICDDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "KNOXMobileDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "AndroidQRDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "AndroidNFCDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "AndroidZTDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "GSChromeDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "MacModernMgmtDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "MigrationDeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentProps", "*"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "*"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "*"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "*"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplateToDeviceEnrollment", "*"));
        selectQuery.addSelectColumn(Column.getColumn("AppleDEPDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppleDEPDeviceForEnrollment", "DEP_TOKEN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("WinAzureADDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppleConfigDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("WindowsLaptopDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("WinModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("WindowsICDDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("KNOXMobileDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AndroidNFCDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AndroidQRDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AndroidZTDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("GSChromeDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MacModernMgmtDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MigrationDeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        final Criteria devIDCri = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)devID, 0);
        final Criteria custCri = new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)APIUtil.getCustomerID(request), 0);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0));
        selectQuery.setCriteria(devIDCri.and(custCri).and(userNotInTrashCriteria));
        CustomerInfoThreadLocal.setSkipCustomerFilter("true");
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        CustomerInfoThreadLocal.setSkipCustomerFilter("false");
        if (dataObject.isEmpty()) {
            throw new APIHTTPException("COM0008", new Object[] { "-device_id : " + devID });
        }
        final Row deviceForEnrollmentRow = dataObject.getRow("DeviceForEnrollment");
        resposne.put("device_id", (Object)devID);
        resposne.put("serial_number", deviceForEnrollmentRow.get("SERIAL_NUMBER"));
        resposne.put("udid", deviceForEnrollmentRow.get("UDID"));
        resposne.put("imei", deviceForEnrollmentRow.get("IMEI"));
        final Row userRow = dataObject.getRow("ManagedUser");
        if (userRow != null) {
            final JSONObject userDetails = new JSONObject();
            userDetails.put("user_name", userRow.get(6));
            userDetails.put("user_email", userRow.get("EMAIL_ADDRESS"));
            final Row resRow = dataObject.getRow("Resource");
            userDetails.put("domain_name", resRow.get("DOMAIN_NETBIOS_NAME"));
            userDetails.put("user_id", userRow.get("MANAGED_USER_ID"));
            resposne.put("user", (Object)userDetails);
        }
        else {
            this.getEnrolledUserForAzure(resposne, devID);
        }
        Row row = dataObject.getRow("DeviceEnrollmentProps");
        if (row != null) {
            resposne.put("device_name", row.get("ASSIGNED_DEVICE_NAME"));
        }
        final JSONArray associatedGroupsJA = new JSONArray();
        final Set<Long> groupIdsSet = new HashSet<Long>(new DeviceForEnrollmentHandler().getAssociatedGroupId(devID));
        row = dataObject.getRow("EnrollmentTemplateToDeviceEnrollment");
        if (row != null) {
            groupIdsSet.addAll(EnrollmentTemplateHandler.getDefaultGroupIDForTemplate((Long)row.get("TEMPLATE_ID")));
        }
        if (groupIdsSet != null && groupIdsSet.size() > 0) {
            final HashMap groupIdVsNamesMap = MDMResourceDataProvider.getResourceNames(new ArrayList(groupIdsSet));
            for (final Long eachGrpId : groupIdsSet) {
                final JSONObject grpJO = new JSONObject();
                grpJO.put("group_id", (Object)eachGrpId);
                grpJO.put("name", groupIdVsNamesMap.get(eachGrpId));
                associatedGroupsJA.put((Object)grpJO);
            }
            resposne.put("group", (Object)associatedGroupsJA);
        }
        final Long knoxDfeId = (Long)(dataObject.containsTable("KNOXMobileDeviceForEnrollment") ? dataObject.getFirstRow("KNOXMobileDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
        final Long nfcDfeId = (Long)(dataObject.containsTable("AndroidNFCDeviceForEnrollment") ? dataObject.getFirstRow("AndroidNFCDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
        final Long appleConfigDfeId = (Long)(dataObject.containsTable("AppleConfigDeviceForEnrollment") ? dataObject.getFirstRow("AppleConfigDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
        final Long winIcdDfeId = (Long)(dataObject.containsTable("WindowsICDDeviceForEnrollment") ? dataObject.getFirstRow("WindowsICDDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
        final Long emmDfeId = (Long)(dataObject.containsTable("AndroidQRDeviceForEnrollment") ? dataObject.getFirstRow("AndroidQRDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
        final Long ztLapDfeId = (Long)(dataObject.containsTable("AndroidZTDeviceForEnrollment") ? dataObject.getFirstRow("AndroidZTDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
        final Long winAzureDfeId = (Long)(dataObject.containsTable("WinAzureADDeviceForEnrollment") ? dataObject.getFirstRow("WinAzureADDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
        final Long depDfeId = (Long)(dataObject.containsTable("AppleDEPDeviceForEnrollment") ? dataObject.getFirstRow("AppleDEPDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
        final Long winLapDfeId = (Long)(dataObject.containsTable("WindowsLaptopDeviceForEnrollment") ? dataObject.getFirstRow("WindowsLaptopDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
        final Long chromeBookDfeId = (Long)(dataObject.containsTable("GSChromeDeviceForEnrollment") ? dataObject.getFirstRow("GSChromeDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
        final Long macDfeId = (Long)(dataObject.containsTable("MacModernMgmtDeviceForEnrollment") ? dataObject.getFirstRow("MacModernMgmtDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
        final Long winmmDfeId = (Long)(dataObject.containsTable("WinModernMgmtDeviceForEnrollment") ? dataObject.getFirstRow("WinModernMgmtDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
        final Long migDfeId = (Long)(dataObject.containsTable("MigrationDeviceForEnrollment") ? dataObject.getFirstRow("MigrationDeviceForEnrollment").get("ENROLLMENT_DEVICE_ID") : null);
        Integer templateType = null;
        Integer platform = null;
        if (knoxDfeId != null || nfcDfeId != null || emmDfeId != null || ztLapDfeId != null) {
            templateType = ((knoxDfeId != null) ? 21 : ((nfcDfeId != null) ? 20 : ((emmDfeId != null) ? 22 : 23)));
            platform = 2;
        }
        else if (appleConfigDfeId != null || depDfeId != null || macDfeId != null) {
            templateType = ((appleConfigDfeId != null) ? 11 : ((depDfeId != null) ? 10 : 12));
            platform = 1;
        }
        else if (winAzureDfeId != null || winIcdDfeId != null || winLapDfeId != null || winmmDfeId != null) {
            templateType = ((winAzureDfeId != null) ? 32 : ((winIcdDfeId != null) ? 30 : ((winLapDfeId != null) ? 31 : 33)));
            platform = 3;
        }
        else if (chromeBookDfeId != null) {
            templateType = 40;
            platform = 4;
        }
        else if (migDfeId != null) {
            templateType = 50;
            platform = 0;
        }
        resposne.put("platform", (Object)platform);
        resposne.put("platform_type", (Object)MDMEnrollmentUtil.getPlatformString(platform));
        resposne.put("template_type", (Object)templateType);
        return resposne;
    }
    
    public JSONObject saveAdminEnrollSettings(final JSONObject requestJSON) throws Exception {
        final Long templateId = APIUtil.getResourceID(requestJSON, "setting_id");
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        if (!MDMEnrollmentUtil.getInstance().isValidTemplateId(templateId, customerId)) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        final JSONObject body = requestJSON.getJSONObject("msg_body");
        JSONObject responseJSON = new JSONObject();
        final String settingsType = String.valueOf(body.get("settings_type"));
        if (settingsType.equalsIgnoreCase("user_assignment_settings")) {
            responseJSON = this.saveAdminEnrollADAuthSettings(requestJSON);
        }
        return responseJSON;
    }
    
    private JSONObject saveAdminEnrollADAuthSettings(final JSONObject requestJSON) throws Exception {
        final Long templateId = APIUtil.getResourceID(requestJSON, "setting_id");
        final JSONObject body = requestJSON.getJSONObject("msg_body");
        body.put("template_id", (Object)templateId);
        body.put("user_id", (Object)APIUtil.getUserID(requestJSON));
        if (body.getInt("user_assignment_type") == 4 && !MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().isActiveDirectoryOrZohoAccountAuthApplicable(APIUtil.getCustomerID(requestJSON))) {
            throw new APIHTTPException("ENR0105", new Object[0]);
        }
        OutOfBoxEnrollmentSettingsHandler.getInstance().addOrUpdateSettingsTemplate(body);
        return new JSONObject();
    }
    
    public JSONObject getAdminEnrollADAuthSettings(final JSONObject requestJSON) throws Exception {
        final Long templateId = APIUtil.getResourceID(requestJSON, "setting_id");
        final Long customerId = APIUtil.getCustomerID(requestJSON);
        if (!MDMEnrollmentUtil.getInstance().isValidTemplateId(templateId, customerId)) {
            throw new APIHTTPException("ENR00105", new Object[0]);
        }
        return OutOfBoxEnrollmentSettingsHandler.getInstance().getAdminEnrollADAuthSettings(templateId, APIUtil.getUserID(requestJSON));
    }
    
    private void getEnrolledUserForAzure(final JSONObject resposne, final Long devID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceForEnrollment"));
        selectQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        selectQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        final Criteria templateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)32, 0);
        final Criteria devCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)devID, 0);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        selectQuery.setCriteria(devCriteria.and(templateCriteria).and(userNotInTrashCriteria));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "*"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "*"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row userRow = dataObject.getRow("ManagedUser");
            final JSONObject userDetails = new JSONObject();
            userDetails.put("user_name", userRow.get("DISPLAY_NAME"));
            userDetails.put("user_email", userRow.get("EMAIL_ADDRESS"));
            final Row resRow = dataObject.getRow("Resource");
            userDetails.put("domain_name", resRow.get("DOMAIN_NETBIOS_NAME"));
            userDetails.put("user_id", userRow.get("MANAGED_USER_ID"));
            resposne.put("user", (Object)userDetails);
        }
    }
    
    public JSONObject createAssignUserSettings(final JSONObject request) throws Exception {
        final JSONObject curSettings = this.getAssignUserSettings(request);
        final JSONArray jsonArray = curSettings.getJSONArray("user_settings");
        final List models = new ArrayList();
        final JSONArray givenArray = request.getJSONObject("msg_body").getJSONArray("device_model_rules");
        if (givenArray.length() == 0) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        for (int i = 0; i < givenArray.length(); ++i) {
            final JSONObject jsonObject = givenArray.getJSONObject(i);
            final int model = jsonObject.getInt("MODEL_TYPE".toLowerCase());
            models.add(model);
        }
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONObject jsonObject = jsonArray.getJSONObject(i);
            final JSONArray deviceModels = jsonObject.getJSONArray("device_model_rules");
            for (int j = 0; j < deviceModels.length(); ++j) {
                final JSONObject model2 = deviceModels.getJSONObject(j);
                final int modelVal = model2.getInt("MODEL_TYPE".toLowerCase());
                if (models.contains(modelVal)) {
                    AdminEnrollmentFacade.LOGGER.log(Level.INFO, "Rejecting request as a rule for the model exsists, {0}", modelVal);
                    throw new APIHTTPException("COM0010", new Object[0]);
                }
            }
        }
        final UserAssignmentRuleHandler userAssignmentRuleHandler = new UserAssignmentRuleHandler();
        final JSONObject msgRequest = request.getJSONObject("msg_body");
        final Long templateID = APIUtil.getResourceID(request, "template_id");
        final Integer templateType = EnrollmentTemplateHandler.getTemplateType(templateID);
        final Long customerID = EnrollmentTemplateHandler.getCustomerIDForTemplate(templateID);
        if (!customerID.equals(APIUtil.getCustomerID(request))) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        final Long userID = APIUtil.getUserID(request);
        if (templateType == -1) {
            throw new APIHTTPException("COM0005", new Object[] { "template_id" });
        }
        JSONObject ruleJSON = msgRequest.optJSONObject("user_assignment_rule");
        if (ruleJSON == null) {
            ruleJSON = new JSONObject();
            ruleJSON.put("RULE_NAME".toLowerCase(), (Object)("User Assignment Rule " + templateType));
        }
        ruleJSON.put("customer_id", (Object)customerID);
        ruleJSON.put("user_id", (Object)userID);
        final Long userAssignmentRuleID = userAssignmentRuleHandler.createAssignmentRule(ruleJSON);
        AdminEnrollmentFacade.LOGGER.log(Level.INFO, "User assignment Rule created {0}, ID : {1}", new Object[] { ruleJSON, userAssignmentRuleID });
        final JSONObject response = userAssignmentRuleHandler.persistRules(msgRequest, userAssignmentRuleID, customerID);
        AdminEnrollmentFacade.LOGGER.log(Level.INFO, "Persisted Rules for the ID {0}", response);
        response.put("on_board_rule_id", (Object)userAssignmentRuleID);
        this.addOrUpdateOnBoardingSettings(templateID, userAssignmentRuleID, customerID, templateType);
        AdminEnrollmentFacade.LOGGER.log(Level.INFO, "Mapping Updated for the template {0} and rule {1}", new Object[] { templateID, userAssignmentRuleID });
        return response;
    }
    
    public JSONObject updateAssignUserSettings(final JSONObject request) throws Exception {
        final JSONObject curSettings = this.getAssignUserSettings(request).getJSONArray("user_settings").getJSONObject(0);
        if (curSettings.length() == 0) {
            throw new APIHTTPException("COM0027", new Object[0]);
        }
        final UserAssignmentRuleHandler userAssignmentRuleHandler = new UserAssignmentRuleHandler();
        final JSONObject msgRequest = request.getJSONObject("msg_body");
        final Long templateID = APIUtil.getResourceID(request, "template_id");
        final Integer templateType = EnrollmentTemplateHandler.getTemplateType(templateID);
        final Long customerID = EnrollmentTemplateHandler.getCustomerIDForTemplate(templateID);
        final Long userID = APIUtil.getUserID(request);
        if (!customerID.equals(APIUtil.getCustomerID(request))) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        if (templateType == -1) {
            throw new APIHTTPException("COM0005", new Object[] { "template_id" });
        }
        JSONObject ruleJSON = msgRequest.optJSONObject("user_assignment_rule");
        if (ruleJSON == null) {
            ruleJSON = new JSONObject();
            ruleJSON.put("RULE_NAME".toLowerCase(), (Object)("User Assignment Rule " + templateType));
        }
        ruleJSON.put("customer_id", (Object)customerID);
        ruleJSON.put("user_id", (Object)userID);
        Long userAssignmentRuleID = null;
        userAssignmentRuleID = curSettings.getLong("on_board_rule_id");
        AdminEnrollmentFacade.LOGGER.log(Level.INFO, "User assignment Rule retrived {0}, ID : {1}", new Object[] { ruleJSON, userAssignmentRuleID });
        final JSONObject response = userAssignmentRuleHandler.persistRules(msgRequest, userAssignmentRuleID, customerID);
        AdminEnrollmentFacade.LOGGER.log(Level.INFO, "Persisted Rules for the ID {0}", response);
        response.put("on_board_rule_id", (Object)userAssignmentRuleID);
        this.addOrUpdateOnBoardingSettings(templateID, userAssignmentRuleID, customerID, templateType);
        AdminEnrollmentFacade.LOGGER.log(Level.INFO, "Mapping Updated for the template {0} and rule {1}", new Object[] { templateID, userAssignmentRuleID });
        return response;
    }
    
    public void deleteAssignUserSettings(final JSONObject request) throws Exception {
        final Long templateID = APIUtil.getResourceID(request, "template_id");
        final Long userSettingID = APIUtil.getResourceID(request, "user_settings");
        final Integer templateType = EnrollmentTemplateHandler.getTemplateType(templateID);
        final Long customerID = EnrollmentTemplateHandler.getCustomerIDForTemplate(templateID);
        if (templateType == -1) {
            throw new APIHTTPException("COM0005", new Object[] { "template_id" });
        }
        if (!customerID.equals(APIUtil.getCustomerID(request))) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        selectQuery.addJoin(new Join("EnrollmentTemplate", "OnBoardingSettings", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        selectQuery.addJoin(new Join("OnBoardingSettings", "OnBoardingRule", new String[] { "ON_BOARD_RULE_ID" }, new String[] { "ON_BOARD_RULE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("OnBoardingSettings", "*"));
        selectQuery.addSelectColumn(Column.getColumn("OnBoardingRule", "*"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"));
        final Criteria customerCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
        Criteria templateTypeCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0);
        Criteria templateIDCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)templateID, 0);
        if (userSettingID != -1L) {
            templateIDCriteria = templateIDCriteria.and(new Criteria(Column.getColumn("OnBoardingRule", "ON_BOARD_RULE_ID"), (Object)userSettingID, 0));
            templateTypeCriteria = templateTypeCriteria.and(new Criteria(Column.getColumn("OnBoardingRule", "ON_BOARD_RULE_ID"), (Object)userSettingID, 0));
        }
        if (UserAssignmentRuleHandler.serverAsTemplateList.contains(templateType) || MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotReplicateUserAssignSettings")) {
            selectQuery.setCriteria(customerCriteria.and(templateIDCriteria));
        }
        else {
            selectQuery.setCriteria(customerCriteria.and(templateTypeCriteria));
        }
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        dataObject.deleteRows("OnBoardingSettings", (Criteria)null);
        dataObject.deleteRows("OnBoardingRule", (Criteria)null);
        MDMUtil.getPersistenceLite().update(dataObject);
        AdminEnrollmentFacade.LOGGER.log(Level.INFO, "[not sql] delete user assignment rules for the request {0}", request);
    }
    
    public JSONObject getAssignUserSettings(final JSONObject request) throws Exception {
        final Long templateID = APIUtil.getResourceID(request, "template_id");
        final Long userSettingID = APIUtil.getResourceID(request, "usersetting_id");
        final Integer templateType = EnrollmentTemplateHandler.getTemplateType(templateID);
        final Long customerID = EnrollmentTemplateHandler.getCustomerIDForTemplate(templateID);
        if (templateType == -1) {
            throw new APIHTTPException("COM0005", new Object[] { "template_id" });
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        selectQuery.addJoin(new Join("EnrollmentTemplate", "OnBoardingSettings", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        selectQuery.addJoin(new Join("OnBoardingSettings", "OnBoardingRule", new String[] { "ON_BOARD_RULE_ID" }, new String[] { "ON_BOARD_RULE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("OnBoardingSettings", "*"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"));
        final Criteria customerCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
        Criteria templateTypeCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0);
        Criteria templateIDCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)templateID, 0);
        if (userSettingID != -1L) {
            templateIDCriteria = templateIDCriteria.and(new Criteria(Column.getColumn("OnBoardingRule", "ON_BOARD_RULE_ID"), (Object)userSettingID, 0));
            templateTypeCriteria = templateTypeCriteria.and(new Criteria(Column.getColumn("OnBoardingRule", "ON_BOARD_RULE_ID"), (Object)userSettingID, 0));
        }
        if (UserAssignmentRuleHandler.serverAsTemplateList.contains(templateType) || MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotReplicateUserAssignSettings")) {
            selectQuery.setCriteria(customerCriteria.and(templateIDCriteria));
        }
        else {
            selectQuery.setCriteria(customerCriteria.and(templateTypeCriteria));
        }
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final Iterator iterator = dataObject.getRows("OnBoardingSettings");
        final JSONArray responseArray = new JSONArray();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            responseArray.put((Object)new UserAssignmentRuleHandler().getUserAssignmentRule((Long)row.get("ON_BOARD_RULE_ID")));
        }
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_settings", (Object)responseArray);
        return jsonObject;
    }
    
    private void addOrUpdateOnBoardingSettings(final Long templateID, final Long userAssignmentRuleID, final Long customerID, final Integer templateType) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplate"));
        selectQuery.addJoin(new Join("EnrollmentTemplate", "OnBoardingSettings", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("OnBoardingSettings", "*"));
        selectQuery.addSelectColumn(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"));
        final Criteria customerCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria templateTypeCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)templateType, 0);
        final Criteria templateIDCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_ID"), (Object)templateID, 0);
        if (UserAssignmentRuleHandler.serverAsTemplateList.contains(templateType) || MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotReplicateUserAssignSettings")) {
            selectQuery.setCriteria(customerCriteria.and(templateIDCriteria));
        }
        else {
            selectQuery.setCriteria(customerCriteria.and(templateTypeCriteria));
        }
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        dataObject.deleteRows("OnBoardingSettings", (Criteria)null);
        final Iterator iterator = dataObject.getRows("EnrollmentTemplate");
        while (iterator.hasNext()) {
            final Row row = new Row("OnBoardingSettings");
            row.set("TEMPLATE_ID", iterator.next().get("TEMPLATE_ID"));
            row.set("ON_BOARD_RULE_ID", (Object)userAssignmentRuleID);
            dataObject.addRow(row);
        }
        MDMUtil.getPersistenceLite().update(dataObject);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMEnrollment");
    }
}
