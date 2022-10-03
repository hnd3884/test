package com.me.mdm.server.util;

import com.me.mdm.agent.servlets.modern.mac.ModernMacEnrollmentServlet;
import com.me.mdm.agent.servlets.windows.WpDiscoverServlet;
import com.me.mdm.agent.servlets.windows.WpCheckInServlet;
import com.me.mdm.webclient.filter.AuthenticationHandlerUtil;
import java.time.ZoneId;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.core.enrollment.settings.ModernMgmtUserAssignmentHandler;
import com.me.mdm.core.auth.APIKey;
import java.util.HashMap;
import com.me.mdm.uem.actionconstants.DeviceAction;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentConstants;
import org.json.JSONArray;
import com.me.mdm.core.enrollment.WindowsModernMgmtEnrollmentHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.mdm.server.ios.apns.APNsCertificateHandler;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import java.util.Properties;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.core.enrollment.WindowsLaptopEnrollmentHandler;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.core.enrollment.settings.UserAssignmentRuleHandler;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.ArrayList;
import org.json.JSONObject;
import com.me.mdm.server.factory.MDMUtilAPI;

public abstract class MDMUtilImpl implements MDMUtilAPI
{
    @Override
    public JSONObject addAutoUserAssignRule(final JSONObject jsonObject) {
        if (this.isModernMgmtCapable()) {
            try {
                List<Long> customerList = new ArrayList<Long>();
                Long customerID = jsonObject.optLong("CUSTOMER_ID", -1L);
                if (customerID == -1L) {
                    customerList = Arrays.asList(CustomerInfoUtil.getInstance().getCustomerIdsFromDB());
                }
                else {
                    customerList.add(customerID);
                }
                final Iterator iterator = customerList.iterator();
                while (iterator.hasNext()) {
                    customerID = iterator.next();
                    EnrollmentTemplateHandler.getModenMacMgmtEnrollmentTemplateDetailsForCustomer(customerID);
                    new UserAssignmentRuleHandler().createAdminUserRuleForTemplate(customerID, 10);
                    new UserAssignmentRuleHandler().createAdminUserRuleForTemplate(customerID, 12);
                }
            }
            catch (final Exception e) {
                Logger.getLogger("MDMEnrollment").log(Level.WARNING, "Creating user asignment rule in DC failed for mac", e);
            }
        }
        return null;
    }
    
    @Override
    public Criteria getManagedDeviceCountCriteriaForLicenseCheck() {
        if (this.isModernMgmtCapable()) {
            final Criteria mobileDeviceCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)120, 0);
            final Criteria chromePlatformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)4, 0);
            final Criteria mdmComputerCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)121, 0);
            return mobileDeviceCriteria.or(chromePlatformCriteria.and(mdmComputerCriteria));
        }
        return null;
    }
    
    @Override
    public Criteria getUemManagedDeviceCountCriteriaForLicenseCheck() {
        if (this.isModernMgmtCapable()) {
            final Criteria notMobileDeviceCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)120, 1);
            final Criteria macPlatformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria windowsPlatformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)3, 0);
            return notMobileDeviceCriteria.and(macPlatformCriteria.or(windowsPlatformCriteria));
        }
        return null;
    }
    
    @Override
    public void checkLicenseStateAndUpdateManagedDeviceStatus(final JSONObject deviceDetails) throws Exception {
        final boolean updateStatus = deviceDetails.optBoolean("UPDATE_STATUS", false);
        final long managedDeviceId = deviceDetails.optLong("RESOURCE_ID", -1L);
        if (updateStatus && managedDeviceId != -1L) {
            final DataObject managedDeviceDO = DBUtil.getDataObjectFromDB("ManagedDevice", "RESOURCE_ID", (Object)managedDeviceId);
            if (!managedDeviceDO.isEmpty()) {
                final Row managedDeviceRow = managedDeviceDO.getFirstRow("ManagedDevice");
                if (managedDeviceRow != null) {
                    final Integer managedDeviceStatus = (Integer)managedDeviceRow.get("MANAGED_STATUS");
                    final boolean isDeviceEnrolledSuccessfully = managedDeviceStatus.equals(2);
                    final long deviceForEnrollmentId = deviceDetails.optLong("ENROLLMENT_DEVICE_ID", -1L);
                    final boolean isDfeUserAssignOrEnrollSuccess = deviceForEnrollmentId != -1L && this.isDfeUserAssignOrEnrollSuccess(deviceForEnrollmentId);
                    if (isDeviceEnrolledSuccessfully || isDfeUserAssignOrEnrollSuccess) {
                        managedDeviceRow.set("MANAGED_STATUS", (Object)6);
                        managedDeviceDO.updateRow(managedDeviceRow);
                        MDMUtil.getPersistence().update(managedDeviceDO);
                        Logger.getLogger("MDMModernMgmtLogger").log(Level.INFO, "checkLicenseStateAndUpdateManagedDeviceStatus()  Managed status changed to WAITING_FOR_LICENSE for resourceID: {0} | Platform: {1}", new Object[] { managedDeviceRow.get("RESOURCE_ID"), managedDeviceRow.get("PLATFORM_TYPE") });
                    }
                }
            }
        }
    }
    
    private boolean isDfeUserAssignOrEnrollSuccess(final long deviceForEnrollmentId) throws Exception {
        boolean isDfeCriteriaSatisfied = false;
        final Row dfeRow = DBUtil.getRowFromDB("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollmentId);
        if (dfeRow != null) {
            final Integer dfeStatus = (Integer)dfeRow.get("STATUS");
            isDfeCriteriaSatisfied = (dfeStatus.equals(2) || dfeStatus.equals(0));
            Logger.getLogger("MDMModernMgmtLogger").log(Level.INFO, "isDfeUserAssignOrEnrollSuccess:  Qualifying whether the device needs to be moved to Waiting for license: Serial: {0} | DFE Status: {1}", new Object[] { dfeRow.get("SERIAL_NUMBER"), isDfeCriteriaSatisfied });
        }
        return isDfeCriteriaSatisfied;
    }
    
    @Override
    public Integer getWindowsLaptopEnrollmentUnassignedCount(final JSONObject criteriaValues, final WindowsLaptopEnrollmentHandler adminEnrollmentHandlerInstance) throws Exception {
        if (this.isModernMgmtCapable()) {
            int unassignedCount = 0;
            try {
                final Long customerID = criteriaValues.getLong("customerID");
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("WindowsLaptopDeviceForEnrollment"));
                sQuery.addJoin(new Join("WindowsLaptopDeviceForEnrollment", "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
                sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
                final Criteria customerCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerID, 0);
                final Criteria userAssignedNullCriteria = new Criteria(Column.getColumn("DeviceEnrollmentToUser", "MANAGED_USER_ID"), (Object)null, 0);
                sQuery.setCriteria(customerCriteria.and(userAssignedNullCriteria));
                unassignedCount = DBUtil.getRecordActualCount(sQuery, "WindowsLaptopDeviceForEnrollment", "ENROLLMENT_DEVICE_ID");
            }
            catch (final Exception exp) {
                Logger.getLogger("MDMEnrollment").log(Level.SEVERE, "Exception while obtaining unassignedCount for DCMDMUtilImpl.getWindowsLaptopEnrollmentUnassignedCount", exp);
            }
            return unassignedCount;
        }
        final Long customerID2 = criteriaValues.getLong("customerID");
        return adminEnrollmentHandlerInstance.callSuperUnassignedDeviceCount(customerID2);
    }
    
    @Override
    public JSONObject getComputerDeviceMappingTable(final JSONObject deviceDetails) throws Exception {
        return null;
    }
    
    @Override
    public String getAgentDownloadUrl(final Integer agentType, Integer downloadMode) {
        String url = "";
        if (agentType == 2) {
            if (downloadMode == null) {
                downloadMode = MDMAgentSettingsHandler.getInstance().getAndroidAgentDownloadMode();
            }
            if (downloadMode == 5 || downloadMode == 6) {
                url = "/agent/MDMAndroidAgent.apk";
            }
            else if (downloadMode == 2) {
                try {
                    url = this.getHttpsServerBaseUrl() + "/agent/" + "MDMAndroidAgent.apk";
                }
                catch (final Exception e) {
                    MDMUtilImpl.LOGGER.log(Level.SEVERE, "Exception while getting base Server URL", e);
                }
            }
            else if (downloadMode == 7) {
                try {
                    final Properties mdmApplicationProperties = MDMUtil.getInstance().getMDMApplicationProperties();
                    url = mdmApplicationProperties.getProperty("mdmcloudUrl") + "/agent/MDMAndroidAgent.apk";
                }
                catch (final Exception e) {
                    MDMUtilImpl.LOGGER.log(Level.SEVERE, "Exception Occurred while getting property \"mdmcloudUrl\" ", e);
                }
            }
            else {
                url = "https://play.google.com/store/apps/details?id=com.manageengine.mdm.android";
            }
        }
        return url;
    }
    
    @Override
    public String getOrgName(final Long customerId) {
        String companyName = "ManageEngine";
        try {
            companyName = MDMCustomerInfoUtil.getInstance().getCompanyName(customerId);
            if (!MDMUtil.isStringValid(companyName) || companyName.equals("--")) {
                companyName = APNsCertificateHandler.getInstance().getCSRInfo().optString("ORGANIZATION_NAME", "--");
            }
            if (!MDMUtil.isStringValid(companyName) || companyName.equals("--")) {
                companyName = "ManageEngine";
            }
        }
        catch (final Exception e) {
            companyName = "ManageEngine";
            Logger.getLogger("MDMEnrollment").log(Level.WARNING, "Exception handled.. Org name is not in DB so returning default value..");
        }
        return companyName;
    }
    
    @Override
    public boolean isFeatureAllowedForUser(final String actionKey) {
        try {
            final String value = MDMUtil.getInstance().getMDMApplicationProperties().getProperty(actionKey);
            if (value != null) {
                return value.trim().toLowerCase().equalsIgnoreCase("true");
            }
        }
        catch (final Exception e) {
            MDMUtilImpl.LOGGER.log(Level.WARNING, e, () -> "Exception Occurred while reading property " + s);
        }
        return Boolean.FALSE;
    }
    
    @Override
    public SelectQuery deepCloneQuery(final SelectQuery selectQuery) {
        throw new UnsupportedOperationException("deepCloneQuery was not allowed for on-premise");
    }
    
    @Override
    public JSONObject getUserAssignmentRules(final JSONObject jsonObject) throws Exception {
        JSONObject response = null;
        if (this.isModernMgmtCapable()) {
            response = new JSONObject();
            final Long customerID = jsonObject.getLong("CUSTOMER_ID");
            try {
                final JSONObject userRules = new UserAssignmentRuleHandler().getOrSetDefaultValues(customerID);
                final JSONObject userIdJson = ManagedUserHandler.getInstance().getManagedUserIdAndAAAUserIdForAdmin(customerID, Boolean.TRUE);
                String upn = userIdJson.optString("EMAIL_ADDRESS");
                final String serverBaseURL = MDMApiFactoryProvider.getMDMUtilAPI().getServerURLOnTomcatPortForClientAuthSetup();
                if (MDMStringUtils.isEmpty(upn)) {
                    upn = "Admin";
                }
                final HashMap details = new WindowsModernMgmtEnrollmentHandler().getModernMgmtEnrollmentDetails(customerID, userIdJson.getLong("USER_ID"));
                response.put("UPN", (Object)upn);
                final JSONArray rulesArray = userRules.getJSONArray("user_rules");
                final JSONObject domainRule = new JSONObject();
                final JSONArray domainExclude = new JSONArray();
                domainRule.put("MachineType", (Object)"Domain");
                domainRule.put("ExcludeList", (Object)domainExclude);
                domainRule.put("Force", (Object)Boolean.FALSE);
                final JSONObject workGroupRule = new JSONObject();
                final JSONArray workGroupExclude = new JSONArray();
                workGroupRule.put("ExcludeList", (Object)workGroupExclude);
                workGroupRule.put("MachineType", (Object)"Workgroup");
                workGroupRule.put("Force", (Object)Boolean.FALSE);
                for (int i = 0; i < rulesArray.length(); ++i) {
                    final JSONObject curRule = rulesArray.getJSONObject(i);
                    final Integer include = curRule.getInt("CRITERIA".toLowerCase());
                    final Integer type = curRule.getInt("RULE_TYPE".toLowerCase());
                    if (include != MDMEnrollmentConstants.UserAssignmentRules.UserRules.INCLUDE_RULE_CRITERIA) {
                        if (type.equals(MDMEnrollmentConstants.UserAssignmentRules.UserRules.FIRST_LOGGED_IN_USER_TYPE)) {
                            domainExclude.put(ManagedUserHandler.getInstance().getManagedUserDetails(curRule.getLong("MANAGED_USER_ID")).get("NAME"));
                        }
                        else if (type.equals(MDMEnrollmentConstants.UserAssignmentRules.UserRules.WORKGROUP_USER)) {
                            workGroupExclude.put(ManagedUserHandler.getInstance().getManagedUserDetails(curRule.getLong("MANAGED_USER_ID")).get("NAME"));
                        }
                    }
                    else if (type.equals(MDMEnrollmentConstants.UserAssignmentRules.UserRules.FIRST_LOGGED_IN_USER_TYPE)) {
                        domainRule.put("UserType", (Object)"FirstLoggedInDomainUser");
                    }
                    else if (type.equals(MDMEnrollmentConstants.UserAssignmentRules.UserRules.WORKGROUP_USER)) {
                        workGroupRule.put("UserType", (Object)"FirstLoggedInUser");
                    }
                }
                final JSONArray mdmRules = new JSONArray();
                mdmRules.put((Object)domainRule);
                mdmRules.put((Object)workGroupRule);
                response.put("MDMEnrollmentRules", (Object)mdmRules);
                response.put("WinApiKey", details.get("encapiKey"));
                response.put("WindowsTemplateToken", details.get("TEMPLATE_TOKEN"));
                response.put("NATUrl", (Object)serverBaseURL);
                if (MDMEnrollmentUtil.getInstance().isAPNsConfigured()) {
                    Logger.getLogger("MDMModernMgmtLogger").log(Level.INFO, "APNS configured for customer: {0}", new Object[] { customerID });
                    final JSONObject keyParams = new JSONObject();
                    keyParams.put("PURPOSE_KEY", 102);
                    keyParams.put("CUSTOMER_ID", (Object)customerID);
                    final APIKey key = MDMApiFactoryProvider.getMdmPurposeAPIKeyGenerator().generateAPIKey(keyParams);
                    response.put("MacApiKey", (Object)key.getKeyValue());
                    new EnrollmentTemplateHandler();
                    final JSONObject macTemplate = EnrollmentTemplateHandler.getModenMacMgmtEnrollmentTemplateDetailsForCustomer(customerID);
                    response.put("MacTemplateToken", macTemplate.get("TEMPLATE_TOKEN"));
                    final String mdmIosServerUrl = MDMEnrollmentUtil.getInstance().getServerBaseURL();
                    response.put("MdmIosServerUrl", (Object)mdmIosServerUrl);
                }
                jsonObject.put("rulesJSON", (Object)response);
                jsonObject.put("lastModifiedTime", System.currentTimeMillis());
                Logger.getLogger("MDMModernMgmtLogger").log(Level.INFO, "Sending User assignment rules to DC for customer: {0}", new Object[] { customerID });
                MDMApiFactoryProvider.getMDMModernMgmtAPI().deviceListener(DeviceAction.POST_USER_ASSIGNMENT_RULES, jsonObject);
            }
            catch (final Exception e) {
                response.put("Error", (Object)Boolean.TRUE);
                response.put("Message", (Object)"Unknown Error");
                Logger.getLogger("MDMEnrollment").log(Level.WARNING, "Error while getting default assign user settings ", e);
            }
        }
        return response;
    }
    
    @Override
    public JSONObject postDeviceUserDetails(final JSONObject jsonObject) {
        final JSONObject response = new JSONObject();
        if (this.isModernMgmtCapable()) {
            final Long customerID = (Long)jsonObject.get("CUSTOMER_ID");
            final JSONObject userJSON = jsonObject.getJSONObject("user_details");
            final JSONObject deviceJSON = jsonObject.getJSONObject("device_unique_props");
            try {
                new ModernMgmtUserAssignmentHandler().userRuleMatched(deviceJSON, userJSON, customerID);
                response.put("Status", (Object)"Success");
            }
            catch (final Exception e) {
                response.put("Status", (Object)"Error");
                Logger.getLogger("MDMEnrollment").log(Level.WARNING, "Error while assigning user through MDM ", e);
            }
        }
        return response;
    }
    
    @Override
    public boolean isModernMgmtCapable() {
        return false;
    }
    
    @Override
    public String getServerURLOnTomcatPortForClientAuthSetup() throws Exception {
        return this.getHttpsServerBaseUrl();
    }
    
    @Override
    public String getKeyToolPath() throws Exception {
        final String SERVER_HOME = SyMUtil.getInstallationDir();
        return SERVER_HOME + File.separator + "jre" + File.separator + "bin" + File.separator + "keytool " + "-printcert -jarfile ";
    }
    
    @Override
    public String getLicenseType() {
        Properties licenseEdition = null;
        final String version = LicenseProvider.getInstance().getEMSLicenseVersion();
        String propertyKey;
        if (!version.equalsIgnoreCase("11")) {
            licenseEdition = LicenseProvider.getInstance().getModuleProperties("MobileLicenseEdition");
            propertyKey = "MDMEdition";
        }
        else {
            licenseEdition = LicenseProvider.getInstance().getModuleProperties("MobileDevices");
            propertyKey = "Edition";
        }
        if (licenseEdition == null) {
            return "Professional";
        }
        final String edition = licenseEdition.getProperty(propertyKey);
        return edition;
    }
    
    @Override
    public ZoneId getZoneForCreatingSchedule() {
        return ZoneId.systemDefault();
    }
    
    @Override
    public String getCustomerDataParentPath() {
        final String baseDir = System.getProperty("server.home");
        return baseDir;
    }
    
    @Override
    public String getCustomerDataBasePath(final String dirName) {
        return this.getCustomerDataParentPath() + File.separator + "mdm" + File.separator + dirName;
    }
    
    @Override
    public Criteria getLicenseResolveCriteria(final List deviceIDs) {
        final Criteria enrolledCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria deviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceIDs.toArray(), 8);
        return enrolledCriteria.and(deviceCriteria.negate());
    }
    
    @Override
    public List<Integer> getAllowedPurpose(final String servletPath) {
        if (servletPath.equals(AuthenticationHandlerUtil.WP_CHECK_IN_V1) || servletPath.equals(AuthenticationHandlerUtil.WP_CHECK_IN_ADMIN_V1) || servletPath.equals(AuthenticationHandlerUtil.WP_CHECK_IN_ADMIN_V2)) {
            return WpCheckInServlet.PURPOSE;
        }
        if (AuthenticationHandlerUtil.WP_DISCOVER_SERVLET_PATTERN.matcher(servletPath).matches()) {
            return WpDiscoverServlet.PURPOSE;
        }
        if (AuthenticationHandlerUtil.MODERN_MAC_ENROLL_SERVLET_PATTERN.matcher(servletPath).matches()) {
            return ModernMacEnrollmentServlet.PURPOSE;
        }
        return new ArrayList<Integer>();
    }
}
