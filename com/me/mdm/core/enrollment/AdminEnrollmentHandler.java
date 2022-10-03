package com.me.mdm.core.enrollment;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;
import com.me.devicemanagement.framework.server.resource.ResourceDataProvider;
import java.util.Collections;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.inv.MDCustomDetailsRequestHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.Arrays;
import com.me.mdm.api.user.UserFacade;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Collection;
import com.adventnet.persistence.DataAccess;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.util.MDMTransactionManager;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.adventnet.sym.server.mdm.core.DeviceEventForQueue;
import java.util.ArrayList;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.TreeMap;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import java.util.Map;
import com.me.idps.core.util.ADSyncDataHandler;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;

public abstract class AdminEnrollmentHandler
{
    private static final String MDM_ENROLLMENT_ROLE = "MDM_Enrollment_Write";
    protected final int templateType;
    protected final String deviceForEnrollmentTableName;
    protected final String adminEnrollmentTemplateTableName;
    public static Logger logger;
    public static Logger assignUserLogger;
    
    private static void addDeviceForEnrollmentProperties(final Long deviceForEnrollId, final Long userId, final List<Long> groupIdsList, final String deviceName) throws Exception {
        final DeviceForEnrollmentHandler deviceForEnrollmentHandler = new DeviceForEnrollmentHandler();
        deviceForEnrollmentHandler.addOrUpdateUserForDevice(deviceForEnrollId, userId);
        deviceForEnrollmentHandler.addOrUpdateGroupForDevice(deviceForEnrollId, groupIdsList);
        if (deviceName != null && !deviceName.equals("")) {
            final JSONObject props = new JSONObject();
            props.put("ASSIGNED_DEVICE_NAME", (Object)deviceName);
            deviceForEnrollmentHandler.addOrUpdatePropsForDevice(deviceForEnrollId, props);
        }
    }
    
    private AdminEnrollmentHandler() {
        this.templateType = -1;
        this.deviceForEnrollmentTableName = null;
        this.adminEnrollmentTemplateTableName = null;
    }
    
    private static void handleNewUserAlreadyExists(final JSONObject data) throws Exception {
        final String domainName = data.optString("DomainName", "MDM");
        final String userName = data.optString("UserName");
        final String emailAddr = data.optString("EmailAddr");
        final Long customerId = data.getLong("CustomerId");
        final JSONObject userjson = new JSONObject();
        userjson.put("USER_IDENTIFIER", (Object)"NAME");
        userjson.put("NAME", (Object)userName);
        userjson.put("DOMAIN_NETBIOS_NAME", (Object)domainName);
        userjson.put("CUSTOMER_ID", (Object)customerId);
        final JSONObject newuserDetailsJson = ManagedUserHandler.getInstance().getManagedUserDetails(userjson);
        if (newuserDetailsJson != null && newuserDetailsJson.has("NAME") && !String.valueOf(newuserDetailsJson.get("EMAIL_ADDRESS")).equalsIgnoreCase(emailAddr)) {
            final Object[] errorParams = { ProductUrlLoader.getInstance().getValue("mdmUrl") };
            throw new SyMException(52103, "User name already exists", "dc.mdm.enroll.error.change_user_email_exists", errorParams, (Throwable)null);
        }
    }
    
    protected AdminEnrollmentHandler(final int templateType, final String deviceForEnrollmentTableName, final String adminEnrollmentTemplateTableName) {
        this.templateType = templateType;
        this.deviceForEnrollmentTableName = deviceForEnrollmentTableName;
        this.adminEnrollmentTemplateTableName = adminEnrollmentTemplateTableName;
    }
    
    public static Long validateAndAddUser(final JSONObject data, final boolean validateEmail) throws Exception {
        String domainName = data.optString("DomainName", "MDM");
        if (domainName == null) {
            domainName = "MDM";
        }
        String userName = data.optString("UserName");
        String emailAddr = data.optString("EmailAddr");
        final Long customerId = data.getLong("CustomerId");
        final String phoneNumber = data.optString("PHONE_NUMBER", (String)null);
        if ((userName == null || userName.isEmpty()) && (emailAddr == null || emailAddr.isEmpty())) {
            throw new SyMException(51200, I18N.getMsg("dc.admin.addUser.Enter_vaild_UserNameAndEmailId", new Object[0]), "dc.admin.addUser.Enter_vaild_UserNameAndEmailId", (Throwable)null);
        }
        if (!data.has("UserName") && !MDMStringUtils.isEmpty(emailAddr)) {
            final int index = emailAddr.indexOf(64);
            if (index != -1) {
                userName = emailAddr.substring(0, index);
            }
        }
        if (userName == null || userName.isEmpty() || userName.equalsIgnoreCase("--")) {
            throw new SyMException(14010, I18N.getMsg("dc.admin.addUser.Enter_vaild_UserName", new Object[0]), "dc.admin.addUser.Enter_vaild_UserName", (Throwable)null);
        }
        if ((emailAddr == null || emailAddr.isEmpty() || emailAddr.equalsIgnoreCase("--")) && validateEmail) {
            throw new SyMException(14003, I18N.getMsg("dc.admin.addUser.Enter_vaild_Email", new Object[0]), "dc.admin.addUser.Enter_vaild_Email", (Throwable)null);
        }
        final Properties userProp = new Properties();
        ((Hashtable<String, Long>)userProp).put("CUSTOMER_ID", data.getLong("CustomerId"));
        ((Hashtable<String, String>)userProp).put("NAME", userName);
        ((Hashtable<String, String>)userProp).put("DOMAIN_NETBIOS_NAME", domainName);
        ((Hashtable<String, String>)userProp).put("EMAIL_ADDRESS", emailAddr);
        if (MDMUtil.isStringValid(phoneNumber)) {
            ((Hashtable<String, String>)userProp).put("PHONE_NUMBER", phoneNumber);
        }
        if (domainName.equals("MDM")) {
            ((Hashtable<String, String>)userProp).put("FIRST_NAME", userName);
            ((Hashtable<String, String>)userProp).put("DISPLAY_NAME", userName);
        }
        else if (DMDomainDataHandler.getInstance().isADManagedDomain(domainName, customerId)) {
            validateDomainAndUser(domainName, userName, customerId);
            final Properties userPropsFromADTemp = ADSyncDataHandler.getInstance().getDirUserProps(customerId, domainName, userName, new Integer[] { 1 }, (Integer[])null);
            if (userPropsFromADTemp != null && !userPropsFromADTemp.isEmpty()) {
                userProp.putAll(userPropsFromADTemp);
            }
            else {
                final JSONObject userValidationJSON = new JSONObject();
                userValidationJSON.put("DOMAIN_NETBIOS_NAME", (Object)domainName);
                userValidationJSON.put("NAME", (Object)userName);
                userValidationJSON.put("CustomerId", (Object)customerId);
                final JSONObject userDataJSON = MDMEnrollmentRequestHandler.getInstance().validateUserName(userValidationJSON);
                if (userDataJSON.has("USER_NAME") && !MDMUtil.getInstance().isEmpty(String.valueOf(userDataJSON.get("USER_NAME")))) {
                    userName = String.valueOf(userDataJSON.get("USER_NAME"));
                    ((Hashtable<String, String>)userProp).put("NAME", userName);
                }
                if (userDataJSON.has("EMAIL_ADDRESS") && !MDMUtil.getInstance().isEmpty(String.valueOf(userDataJSON.get("EMAIL_ADDRESS")))) {
                    emailAddr = String.valueOf(userDataJSON.get("EMAIL_ADDRESS"));
                    ((Hashtable<String, String>)userProp).put("EMAIL_ADDRESS", emailAddr);
                }
                if (userDataJSON.has("FIRST_NAME") && !MDMUtil.getInstance().isEmpty(String.valueOf(userDataJSON.get("FIRST_NAME")))) {
                    ((Hashtable<String, String>)userProp).put("FIRST_NAME", String.valueOf(userDataJSON.get("FIRST_NAME")));
                }
                if (userDataJSON.has("MIDDLE_NAME") && !MDMUtil.getInstance().isEmpty(String.valueOf(userDataJSON.get("MIDDLE_NAME")))) {
                    ((Hashtable<String, String>)userProp).put("MIDDLE_NAME", String.valueOf(userDataJSON.get("MIDDLE_NAME")));
                }
                if (userDataJSON.has("LAST_NAME") && !MDMUtil.getInstance().isEmpty(String.valueOf(userDataJSON.get("LAST_NAME")))) {
                    ((Hashtable<String, String>)userProp).put("LAST_NAME", String.valueOf(userDataJSON.get("LAST_NAME")));
                }
                if (userDataJSON.has("DISPLAY_NAME") && !MDMUtil.getInstance().isEmpty(String.valueOf(userDataJSON.get("DISPLAY_NAME")))) {
                    ((Hashtable<String, String>)userProp).put("DISPLAY_NAME", String.valueOf(userDataJSON.get("DISPLAY_NAME")));
                }
            }
        }
        if (validateEmail) {
            validateEmailAddress(emailAddr);
        }
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            handleNewUserAlreadyExists(data);
        }
        return ManagedUserHandler.getInstance().addOrUpdateAndGetUserId(userProp);
    }
    
    private static void validateDomainAndUser(final String domainName, final String userName, final Long customerID) throws Exception {
        final List domainList = DMDomainDataHandler.getInstance().getAllDMManagedProps(customerID);
        final TreeMap domainListMap = MDMEnrollmentUtil.getInstance().getDomainListAsTreeMap(domainList);
        if (domainListMap == null || !domainListMap.containsValue(domainName.toUpperCase())) {
            throw new SyMException(14002, I18N.getMsg("dc.mdm.enroll.invalid_domain_name", new Object[0]), "dc.mdm.enroll.invalid_domain_name", (Throwable)null);
        }
        final Properties userProps = ADSyncDataHandler.getInstance().getDirUserProps(customerID, domainName, userName, new Integer[] { 1 }, (Integer[])null);
        if (userProps.isEmpty()) {
            throw new SyMException(14010, I18N.getMsg("dc.mdm.enroll.invalid_user_name", (Object[])new String[] { userName, domainName }), "dc.mdm.enroll.invalid_user_name", (Throwable)null);
        }
    }
    
    private static void validateEmailAddress(final String email) throws Exception {
        if (!MDMUtil.getInstance().isValidEmail(email)) {
            throw new SyMException(14003, I18N.getMsg("dc.mdm.enroll.invalid_email", new Object[0]), "dc.mdm.enroll.invalid_email", (Throwable)null);
        }
    }
    
    public void addAdminEnrollmentTemplateForAllUsers() {
        try {
            final Long[] customerList = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            if (customerList != null) {
                for (int i = 0; i < customerList.length; ++i) {
                    this.addAdminEnrollmentTemplateForAllUsers(customerList[i], getAdminEnrollmentTemplateData(customerList[i]));
                }
            }
        }
        catch (final Exception ex) {
            AdminEnrollmentHandler.logger.log(Level.SEVERE, "Exception in addAdminEnrollmentTemplateForAllUsers()", ex);
        }
    }
    
    public static HashMap<Long, JSONObject> getAdminEnrollmentTemplateData(final Long customerId) {
        final HashMap<Long, JSONObject> templateData = new HashMap<Long, JSONObject>();
        try {
            final List loginIdList = DMUserHandler.getLoginIDsForAAARoleName("MDM_Enrollment_Write", customerId);
            for (int i = 0; i < loginIdList.size(); ++i) {
                final Long loginId = loginIdList.get(i);
                final Long userId = DMUserHandler.getDCUserID(loginId);
                final JSONObject enrollmentTemplateJSON = new JSONObject();
                final String domainName = (String)DBUtil.getValueFromDB("AaaLogin", "USER_ID", (Object)userId, "DOMAINNAME");
                final Properties userInfo = DMUserHandler.getContactInfoProp(userId);
                final String email = userInfo.getProperty("EMAIL_ID");
                if (CustomerInfoUtil.isSAS()) {
                    final String loginEmail = DMUserHandler.getDCUser(loginId);
                    final JSONObject userDetails = ManagedUserHandler.getInstance().getManagedUserDetailsForEmailAddress(loginEmail, customerId);
                    if (userDetails == null || userDetails.length() == 0) {
                        enrollmentTemplateJSON.put("NAME", (Object)loginEmail.split("@")[0]);
                        enrollmentTemplateJSON.put("DOMAIN_NETBIOS_NAME", (Object)((domainName != null) ? domainName : "Zoho Directory"));
                    }
                    else {
                        enrollmentTemplateJSON.put("DOMAIN_NETBIOS_NAME", userDetails.get("DOMAIN_NETBIOS_NAME"));
                        enrollmentTemplateJSON.put("NAME", userDetails.get("NAME"));
                    }
                }
                else {
                    enrollmentTemplateJSON.put("NAME", (Object)DMUserHandler.getDCUser(loginId));
                    enrollmentTemplateJSON.put("DOMAIN_NETBIOS_NAME", (Object)((domainName != null) ? domainName : "MDM"));
                }
                enrollmentTemplateJSON.put("ADDED_USER", (Object)userId);
                enrollmentTemplateJSON.put("LOGIN_ID", (Object)loginId);
                enrollmentTemplateJSON.put("EMAIL_ADDRESS", (Object)email);
                enrollmentTemplateJSON.put("CUSTOMER_ID", (Object)customerId);
                templateData.put(loginId, enrollmentTemplateJSON);
            }
        }
        catch (final Exception ex) {
            AdminEnrollmentHandler.logger.log(Level.SEVERE, "Exception in addAdminEnrollmentTemplateForAllUsers(customerID) {0}", ex);
        }
        return templateData;
    }
    
    public void addAdminEnrollmentTemplateForAllUsers(final Long customerId, final HashMap<Long, JSONObject> templateData) {
        try {
            for (final Long loginId : templateData.keySet()) {
                if (!this.isAdminEnrollmentTemplateAvailable(loginId, customerId)) {
                    this.addorUpdateAdminEnrollmentTemplate(templateData.get(loginId));
                }
            }
        }
        catch (final Exception ex) {
            AdminEnrollmentHandler.logger.log(Level.SEVERE, "Exception in addAdminEnrollmentTemplateForAllUsers(customerID) {0}", ex);
        }
    }
    
    public abstract void addorUpdateAdminEnrollmentTemplate(final JSONObject p0) throws Exception;
    
    public abstract boolean isValidEnrollmentTemplate(final Long p0) throws Exception;
    
    public boolean isAdminEnrollmentTemplateAvailable(final Long loginId, final Long customerID) throws Exception {
        if (this.adminEnrollmentTemplateTableName != null) {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table(this.adminEnrollmentTemplateTableName));
            final Join loginJoin = new Join(this.adminEnrollmentTemplateTableName, "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
            final Join userJoin = new Join("AaaLogin", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            final Join templateJoin = new Join(this.adminEnrollmentTemplateTableName, "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2);
            sQuery.addJoin(loginJoin);
            sQuery.addJoin(userJoin);
            sQuery.addJoin(templateJoin);
            final Criteria loginIdCriteria = new Criteria(new Column(this.adminEnrollmentTemplateTableName, "LOGIN_ID"), (Object)loginId, 0);
            final Criteria cusIdCriteria = new Criteria(new Column("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
            sQuery.setCriteria(loginIdCriteria.and(cusIdCriteria));
            final int count = DBUtil.getRecordActualCount(sQuery, this.adminEnrollmentTemplateTableName, "TEMPLATE_ID");
            if (count > 0) {
                return true;
            }
        }
        return false;
    }
    
    public static JSONObject assignUser(final List<JSONObject> listToAssign, final Long technicianUserId, final Integer templateType, String primaryKeyLabel, final int platform) throws Exception {
        DMSecurityLogger.info(AdminEnrollmentHandler.assignUserLogger, AdminEnrollmentHandler.class.getName(), "assignUser", "Assign User Input : {0}", (Object)listToAssign.toString());
        final JSONObject result = new JSONObject();
        final JSONArray successArray = new JSONArray();
        final JSONArray failedArray = new JSONArray();
        final ArrayList<DeviceEventForQueue> invokeListenerList = new ArrayList<DeviceEventForQueue>();
        final ArrayList<DeviceEvent> awaitingDeviceActivationListenerList = new ArrayList<DeviceEvent>();
        Long customerId = null;
        for (final JSONObject data : listToAssign) {
            data.put("platform", platform);
            if (data.has("user_id")) {
                primaryKeyLabel = "user_id";
            }
            final Long pkValue = data.getLong(primaryKeyLabel);
            boolean isRowSuccess = false;
            final MDMTransactionManager mdmTransactionManager = new MDMTransactionManager();
            try {
                mdmTransactionManager.begin();
                customerId = data.getLong("CustomerId");
                checkIfAssignUserAllowed(customerId, templateType);
                final String imei = data.optString("IMEI", (String)null);
                final String serialNo = data.optString("SerialNumber", (String)null);
                final String udid = data.optString("UDID", (String)null);
                final String easId = data.optString("EASID", (String)null);
                final String deviceName = data.optString("DeviceName");
                final String userName = data.optString("UserName");
                final String genericID = data.optString("GENERIC_ID");
                AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "Assign user starts for this device with IMEI:{0}, Serial number:{1}, UDID:{2}, EAS ID:{3}, CustomerId:{4}, Device Name:{5}", new Object[] { imei, serialNo, udid, easId, customerId, deviceName });
                if (MDMStringUtils.isEmpty(imei) && MDMStringUtils.isEmpty(serialNo) && MDMStringUtils.isEmpty(udid) && MDMStringUtils.isEmpty(easId) && MDMStringUtils.isEmpty(genericID)) {
                    throw new SyMException(14020, "Either of IMEI / Serial number / UDID / EAS ID must be specified", "dc.mdm.msg.inv.bulk_edit.no_imei_slno", (Throwable)null);
                }
                final HashMap newManagedUserDetails = getManagedUserDetailsForUserAssignment(data);
                final Long userId = newManagedUserDetails.get("MANAGED_USER_ID");
                data.put("user_id", (Object)userId);
                performUserAssignment(data, newManagedUserDetails, templateType, technicianUserId, awaitingDeviceActivationListenerList, invokeListenerList);
                successArray.put((Object)pkValue);
                final String remarksArg = ((imei == null) ? "--" : imei) + "@@@" + ((serialNo == null) ? "--" : serialNo) + "@@@" + ((udid == null) ? "--" : udid) + "@@@" + userName;
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(technicianUserId)), "dc.mdm.actionlog.enrollment.user_assignment", remarksArg, customerId);
                mdmTransactionManager.commit();
                isRowSuccess = true;
                AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "Assign user done successfully for this device with IMEI:{0}, Serial number:{1}, UDID:{2}, EAS ID:{3}, CustomerId:{4}, Device Name:{5}, ManagedUserId:{6}", new Object[] { imei, serialNo, udid, easId, customerId, deviceName, userId });
            }
            catch (final DataAccessException ex) {
                AdminEnrollmentHandler.assignUserLogger.log(Level.WARNING, "Problem assigning user for input " + data.toString(), (Throwable)ex);
                failedArray.put((Object)getFailedJSON(primaryKeyLabel, pkValue, "Internal Server Error"));
            }
            catch (final SyMException ex2) {
                AdminEnrollmentHandler.assignUserLogger.log(Level.WARNING, "Problem assigning user for input " + data.toString(), (Throwable)ex2);
                failedArray.put((Object)getFailedJSON(primaryKeyLabel, pkValue, ex2.getMessage()));
            }
            catch (final JSONException ex3) {
                AdminEnrollmentHandler.assignUserLogger.log(Level.WARNING, "Problem assigning user for input " + data.toString(), (Throwable)ex3);
                failedArray.put((Object)getFailedJSON(primaryKeyLabel, pkValue, "dc.mdm.enroll.admin.csv.mandatory_fields_missing"));
            }
            catch (final Exception ex4) {
                AdminEnrollmentHandler.assignUserLogger.log(Level.WARNING, "Problem assigning user for input " + data.toString(), ex4);
                failedArray.put((Object)getFailedJSON(primaryKeyLabel, pkValue, "Internal Server Error"));
            }
            if (!isRowSuccess) {
                try {
                    AdminEnrollmentHandler.assignUserLogger.log(Level.WARNING, "There is something wrong so RollBack");
                    AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "Assign user stopped because of rollback for this device details with IMEI:{0}, Serial number:{1}, UDID:{2}", new Object[] { data.optString("IMEI", (String)null), data.optString("SerialNumber", (String)null), data.optString("UDID", (String)null) });
                    mdmTransactionManager.rollBack();
                }
                catch (final Exception ex4) {
                    AdminEnrollmentHandler.assignUserLogger.log(Level.WARNING, "Problem on rollback" + data.toString(), ex4);
                }
            }
        }
        final Iterator it = listToAssign.iterator();
        if (it.hasNext()) {
            final JSONObject data = it.next();
            customerId = data.getLong("CustomerId");
            MDMMessageHandler.getInstance().messageAction("NO_DEVICE_ENROLLED", customerId);
        }
        invokePostUserAssignmentListenersFromList(invokeListenerList);
        invokeDevicePreAssignedListenersFromList(awaitingDeviceActivationListenerList);
        result.put("SuccessList", (Object)successArray);
        result.put("FailedList", (Object)failedArray);
        AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "Assign User Output : {0}", result.toString());
        return result;
    }
    
    private static void updateManagedDeviceStatus(final Long resourceID) throws DataAccessException {
        if (resourceID != null) {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("ManagedDevice"));
            query.addSelectColumn(new Column("ManagedDevice", "*"));
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
            query.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
            if (!dataObject.isEmpty()) {
                AdminEnrollmentHandler.logger.log(Level.INFO, "Updating managed status to enrolled on assign user for manageddevice id: {0}", resourceID);
                final Row managedDeviceRow = dataObject.getFirstRow("ManagedDevice");
                managedDeviceRow.set("MANAGED_STATUS", (Object)2);
                managedDeviceRow.set("REGISTERED_TIME", (Object)System.currentTimeMillis());
                managedDeviceRow.set("REMARKS", (Object)"mdm.enroll.assign_in_progess_remarks");
                dataObject.updateRow(managedDeviceRow);
                MDMUtil.getPersistence().update(dataObject);
            }
            else {
                AdminEnrollmentHandler.logger.log(Level.INFO, "dataObject empty on changing managed status to enrolled on assign user for manageddevice id: {0}", resourceID);
            }
        }
    }
    
    protected int getAdminEnrollRequestCount(final Long customerID, Criteria criteria) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentTemplate"));
        sQuery.addJoin(new Join("EnrollmentTemplate", "EnrollmentTemplateToRequest", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentTemplateToRequest", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        sQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        sQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        sQuery.addJoin(new Join("EnrollmentTemplate", "AaaUser", new String[] { "ADDED_USER" }, new String[] { "USER_ID" }, 1));
        final Criteria customerCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria templateCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)this.templateType, 0);
        if (criteria == null) {
            criteria = customerCriteria.and(templateCriteria);
        }
        else {
            criteria = criteria.and(customerCriteria).and(templateCriteria);
        }
        sQuery.setCriteria(criteria);
        RBDAUtil.getInstance().modifyRBDAQueryByTechnician(sQuery);
        return DBUtil.getRecordActualCount(sQuery, "DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
    }
    
    protected int getDeviceForEnrollmentRequestCount(final Long customerID, Criteria criteria) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(this.deviceForEnrollmentTableName));
        sQuery.addJoin(new Join(this.deviceForEnrollmentTableName, "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
        sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        sQuery.addJoin(new Join("DeviceEnrollmentToUser", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        sQuery.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        sQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        sQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        sQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        sQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        sQuery.addJoin(new Join("EnrollmentTemplate", "AaaUser", new String[] { "ADDED_USER" }, new String[] { "USER_ID" }, 1));
        if (customerID != null && customerID != -1L) {
            final Criteria customerCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerID, 0);
            if (criteria == null) {
                criteria = customerCriteria;
            }
            else {
                criteria = criteria.and(customerCriteria);
            }
        }
        sQuery.setCriteria(criteria);
        RBDAUtil.getInstance().modifyRBDAQueryByTechnician(sQuery);
        return DBUtil.getRecordActualCount(sQuery, "DeviceForEnrollment", "ENROLLMENT_DEVICE_ID");
    }
    
    public int getAdminEnrolledDeviceCount(final Criteria cri) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
        sQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        sQuery.addJoin(new Join("EnrollmentTemplate", this.adminEnrollmentTemplateTableName, new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2));
        sQuery.addJoin(new Join("DeviceEnrollmentRequest", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        sQuery.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        sQuery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        sQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 1));
        sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        sQuery.addJoin(new Join("EnrollmentTemplate", "AaaUser", new String[] { "ADDED_USER" }, new String[] { "USER_ID" }, 1));
        final Criteria noDfeCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 0);
        final Criteria enrollmentFailed = new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)null, 1).and(new Criteria(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"), (Object)null, 1)).and(new Criteria(Column.getColumn("DeviceEnrollmentToUser", "ENROLLMENT_DEVICE_ID"), (Object)null, 1));
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0));
        Criteria enrolledDevicesCountCri = noDfeCriteria.or(enrollmentFailed);
        if (cri != null) {
            enrolledDevicesCountCri = enrolledDevicesCountCri.and(cri);
        }
        sQuery.setCriteria(enrolledDevicesCountCri.and(userNotInTrashCriteria));
        RBDAUtil.getInstance().modifyRBDAQueryByTechnician(sQuery);
        return DBUtil.getRecordActualCount(sQuery, "DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID");
    }
    
    public int getAdminEnrolledDeviceCount(final Long customerID) throws Exception {
        return this.getAdminEnrolledDeviceCount(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
    }
    
    public int getUnassignedDeviceCount(final Long customerID) throws Exception {
        final Criteria enrollmentToUserC = new Criteria(Column.getColumn("DeviceEnrollmentToUser", "ENROLLMENT_DEVICE_ID"), (Object)null, 0);
        final Criteria deviceEnrollRegC = new Criteria(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"), (Object)null, 1);
        return this.getDeviceForEnrollmentRequestCount(customerID, enrollmentToUserC.and(deviceEnrollRegC));
    }
    
    public int getUnEnrolledDeviceCount(final Long customerID) throws Exception {
        return this.getDeviceForEnrollmentRequestCount(customerID, new Criteria(Column.getColumn("DeviceEnrollmentToUser", "ENROLLMENT_DEVICE_ID"), (Object)null, 1).and(new Criteria(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"), (Object)null, 0)));
    }
    
    public int getUnEnrolledAndUnassignedDeviceCount(final Long customerID) throws Exception {
        return this.getDeviceForEnrollmentRequestCount(customerID, new Criteria(Column.getColumn("DeviceEnrollmentToUser", "ENROLLMENT_DEVICE_ID"), (Object)null, 0).and(new Criteria(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"), (Object)null, 0)));
    }
    
    public static Boolean removeDevice(final List<Long> deviceForEnrollmentList, final String userName, final Long customerID) throws Exception {
        StringBuffer enrollmentIds = null;
        final List<Long> userIDs = new ArrayList<Long>();
        if (!deviceForEnrollmentList.isEmpty()) {
            DataObject DO = MDMUtil.getPersistence().get("DeviceEnrollmentToUser", new Criteria(Column.getColumn("DeviceEnrollmentToUser", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentList.toArray(), 8));
            if (!DO.isEmpty()) {
                final Iterator<Row> iter = DO.getRows("DeviceEnrollmentToUser");
                while (iter.hasNext()) {
                    userIDs.add((Long)iter.next().get("MANAGED_USER_ID"));
                }
            }
            DO = MDMUtil.getPersistence().get("DeviceEnrollmentToRequest", new Criteria(Column.getColumn("DeviceEnrollmentToRequest", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentList.toArray(), 8));
            if (!DO.isEmpty()) {
                final Iterator<Row> iter = DO.getRows("DeviceEnrollmentToRequest");
                while (iter.hasNext()) {
                    if (enrollmentIds == null) {
                        enrollmentIds = new StringBuffer(String.valueOf(iter.next().get("ENROLLMENT_REQUEST_ID")));
                    }
                    else {
                        enrollmentIds.append(",").append(String.valueOf(iter.next().get("ENROLLMENT_REQUEST_ID")));
                    }
                }
            }
            AdminEnrollmentHandler.logger.log(Level.INFO, "Deleting deviceForEnrollment entry for {0}", deviceForEnrollmentList.toString());
            DataAccess.delete("DeviceForEnrollment", new Criteria(new Column("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollmentList.toArray(), 8));
            if (enrollmentIds != null) {
                MDMEnrollmentUtil.getInstance().removeDevice(enrollmentIds.toString(), userName, customerID);
            }
        }
        return true;
    }
    
    private static JSONObject getFailedJSON(final String pkLabel, final Long pk, final String errorMsg) throws JSONException {
        final JSONObject failedJSON = new JSONObject();
        failedJSON.put(pkLabel, (Object)pk);
        failedJSON.put("ErrorMsg", (Object)errorMsg);
        return failedJSON;
    }
    
    public int getDeviceForEnrollmentRequestCount(final Long customerID, Criteria criteria, final Join enrollmentType) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AndroidNFCDeviceForEnrollment"));
        sQuery.addJoin(enrollmentType);
        sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        sQuery.addJoin(new Join("DeviceEnrollmentToUser", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        sQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        sQuery.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        sQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        sQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        final Criteria customerCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerID, 0);
        if (criteria == null) {
            criteria = customerCriteria;
        }
        else {
            criteria = criteria.and(customerCriteria);
        }
        sQuery.setCriteria(criteria);
        return DBUtil.getRecordActualCount(sQuery, "DeviceForEnrollment", "ENROLLMENT_DEVICE_ID");
    }
    
    public Properties removeUnwantedMessages(final Properties messageProps) {
        return messageProps;
    }
    
    public static Long getCustomerIdFromDeviceForEnrollId(final Long deviceForEnrollId) throws Exception {
        Long customerID = -1L;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollId, 0));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceForEnrollment", "CUSTOMER_ID"));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            customerID = (Long)dataObject.getFirstValue("DeviceForEnrollment", "CUSTOMER_ID");
        }
        return customerID;
    }
    
    public static JSONArray getAlreadyManagedDevicesList(final List<JSONObject> deviceList) throws Exception {
        try {
            final JSONArray managedDeviceList = new JSONArray();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            selectQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            final ArrayList<String> serialNumberList = new ArrayList<String>();
            final ArrayList<String> imeiList = new ArrayList<String>();
            final ArrayList<String> udidList = new ArrayList<String>();
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IMEI"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
            for (final JSONObject deviceJSON : deviceList) {
                final String serialNumber = deviceJSON.optString("SerialNumber");
                final String imei = deviceJSON.optString("IMEI");
                final String udid = deviceJSON.optString("UDID");
                if (!MDMUtil.getInstance().isEmpty(serialNumber)) {
                    serialNumberList.add(serialNumber);
                }
                if (!MDMUtil.getInstance().isEmpty(imei)) {
                    imeiList.add(imei);
                }
                if (!MDMUtil.getInstance().isEmpty(udid)) {
                    udidList.add(udid);
                }
            }
            final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria serialNumberCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"), (Object)serialNumberList.toArray(), 8);
            final Criteria imeiCriteria = new Criteria(Column.getColumn("MdDeviceInfo", "IMEI"), (Object)imeiList.toArray(), 8);
            final Criteria udidCriteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udidList.toArray(), 8);
            selectQuery.setCriteria(managedCriteria.and(serialNumberCriteria.or(imeiCriteria).or(udidCriteria)));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            final ArrayList<JSONObject> tempList = new ArrayList<JSONObject>(deviceList);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ManagedDevice");
                while (iterator.hasNext()) {
                    final Row managedDeviceRow = iterator.next();
                    final Long resourceId = (Long)managedDeviceRow.get("RESOURCE_ID");
                    final Row mdDeviceInfoRow = dataObject.getRow("MdDeviceInfo", new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0));
                    final String imei2 = (String)mdDeviceInfoRow.get("IMEI");
                    final String serialNumber2 = (String)mdDeviceInfoRow.get("SERIAL_NUMBER");
                    final String udid2 = (String)managedDeviceRow.get("UDID");
                    final Row enrollmentRequestRow = dataObject.getRow("EnrollmentRequestToDevice", new Criteria(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"), (Object)resourceId, 0));
                    final Long erid = (Long)enrollmentRequestRow.get("ENROLLMENT_REQUEST_ID");
                    for (final JSONObject deviceJSON2 : tempList) {
                        final String imeiCSV = deviceJSON2.optString("IMEI");
                        final String serialNumberCSV = deviceJSON2.optString("SerialNumber");
                        final String udidCSV = deviceJSON2.optString("UDID");
                        if ((!MDMUtil.getInstance().isEmpty(imei2) && !MDMUtil.getInstance().isEmpty(imeiCSV) && imei2.equalsIgnoreCase(imeiCSV)) || (!MDMUtil.getInstance().isEmpty(serialNumber2) && !MDMUtil.getInstance().isEmpty(serialNumberCSV) && serialNumber2.equalsIgnoreCase(serialNumberCSV)) || (!MDMUtil.getInstance().isEmpty(udid2) && !MDMUtil.getInstance().isEmpty(udidCSV) && udid2.equalsIgnoreCase(udidCSV))) {
                            deviceList.remove(deviceJSON2);
                            deviceJSON2.put("ENROLLMENT_REQUEST_ID", (Object)erid);
                            managedDeviceList.put((Object)deviceJSON2);
                        }
                    }
                }
            }
            return managedDeviceList;
        }
        catch (final Exception e) {
            AdminEnrollmentHandler.logger.log(Level.SEVERE, "Exception in getAlreadyManagedDevicesList()  ", e);
            throw e;
        }
    }
    
    public static JSONObject changeUserFromCSV(final JSONArray reAssignUserJSONArray) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final JSONArray successList = new JSONArray();
        final JSONArray failedList = new JSONArray();
        for (int i = 0; i < reAssignUserJSONArray.length(); ++i) {
            final JSONObject deviceJSON = reAssignUserJSONArray.getJSONObject(i);
            try {
                final Long userId = validateAndAddUser(deviceJSON, Boolean.TRUE);
                final Long erid = JSONUtil.optLongForUVH(deviceJSON, "ENROLLMENT_REQUEST_ID", Long.valueOf(-1L));
                ManagedUserHandler.getInstance().changeUser(userId, new Long[] { erid }, false);
                successList.put((Object)JSONUtil.optLongForUVH(deviceJSON, "ASSIGN_USER_ID", Long.valueOf(-1L)));
            }
            catch (final Exception e) {
                AdminEnrollmentHandler.assignUserLogger.log(Level.WARNING, e, () -> "Problem assigin user for input " + jsonObject.toString());
                failedList.put((Object)getFailedJSON("ASSIGN_USER_ID", JSONUtil.optLongForUVH(deviceJSON, "ASSIGN_USER_ID", Long.valueOf(-1L)), "Internal Server Error"));
            }
        }
        responseJSON.put("SuccessList", (Object)successList);
        responseJSON.put("FailedList", (Object)failedList);
        return responseJSON;
    }
    
    private static HashMap getManagedUserDetailsForUserAssignment(final JSONObject dataJSON) throws Exception {
        DMSecurityLogger.info(AdminEnrollmentHandler.assignUserLogger, AdminEnrollmentHandler.class.getName(), "getManagedUserDetailsForUserAssignment()", "Fetching user details to proceed with user assignment : " + dataJSON.toString(), (Object)null);
        JSONObject managedUserJSON = new JSONObject();
        final Long customerId = dataJSON.getLong("CustomerId");
        final String userName = dataJSON.optString("UserName");
        final Long toAssignUserId = JSONUtil.optLongForUVH(dataJSON, "user_id", Long.valueOf(-1L));
        Long userId;
        if (toAssignUserId != -1L) {
            AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "Assigning user with user ID picked from picklist user API : {0}", toAssignUserId);
            new UserFacade().validateIfUsersExists(Arrays.asList(toAssignUserId), customerId);
            if (!ManagedUserHandler.getInstance().isUserManaged(toAssignUserId, customerId)) {
                AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "Given Domain user is not managed user hence adding the same to Managed user");
                final Properties domainUserProps = ADSyncDataHandler.getInstance().getDirUserProps(toAssignUserId, customerId);
                ((Hashtable<String, Long>)domainUserProps).put("MANAGED_USER_ID", toAssignUserId);
                if (domainUserProps != null && !domainUserProps.isEmpty()) {
                    DMSecurityLogger.info(AdminEnrollmentHandler.assignUserLogger, AdminEnrollmentHandler.class.getName(), "assignUser()", "Fetched domain user props for given user id : " + toAssignUserId + " props : " + domainUserProps.toString(), (Object)null);
                    ManagedUserHandler.getInstance().addOrUpdateManagedUser(domainUserProps);
                }
            }
            userId = toAssignUserId;
        }
        else {
            AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "Assigning user with user name, email and domain as input");
            final JSONObject userJSON = new JSONObject();
            userJSON.put("USER_IDENTIFIER", (Object)"NAME");
            userJSON.put("NAME", (Object)userName);
            userJSON.put("DOMAIN_NETBIOS_NAME", (Object)dataJSON.optString("DomainName"));
            userJSON.put("CUSTOMER_ID", (Object)customerId);
            managedUserJSON = ManagedUserHandler.getInstance().getManagedUserDetails(dataJSON);
            userId = JSONUtil.optLongForUVH(managedUserJSON, "MANAGED_USER_ID", Long.valueOf(-1L));
            if (userId == -1L) {
                userId = validateAndAddUser(dataJSON, !dataJSON.optBoolean("skip_user_validation", (boolean)Boolean.FALSE));
            }
        }
        AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "Assigning user with managedUserId:{0}", userId);
        HashMap newManagedUserDetails = new HashMap();
        if (managedUserJSON.has("MANAGED_USER_ID")) {
            newManagedUserDetails = JSONUtil.getInstance().ConvertToSameDataTypeHash(managedUserJSON);
        }
        else {
            newManagedUserDetails = ManagedUserHandler.getInstance().getManagedUserDetails(userId);
        }
        newManagedUserDetails.put("MANAGED_USER_ID", userId);
        return newManagedUserDetails;
    }
    
    private static void checkIfAssignUserAllowed(final Long customerId, final Integer templateType) throws SyMException {
        AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "Checking if user assignment is allowed for customer: {0}", new Object[] { customerId });
        final boolean isLicenseLimitReached = MDMEnrollmentUtil.getInstance().isLicenseLimitReached(customerId);
        final boolean isLaptopDevice = templateType == 12 || templateType == 31 || templateType == 32 || templateType == 30 || templateType == 33;
        if (isLicenseLimitReached && !isLaptopDevice) {
            throw new SyMException(12012, "Unable to assign user due to insufficient license available.", "dc.mdm.enroll.UNABLE_TO_ASSIGN_USER_INSUFFICIENT_LICENSE", (Throwable)null);
        }
        AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "User assignment is allowed for customer: {0}", new Object[] { customerId });
    }
    
    private static SelectQuery getDeviceForEnrollmentQuery(final JSONObject data, final int templateType) throws SyMException {
        final SelectQuery deviceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceForEnrollment"));
        final String imei = data.optString("IMEI", (String)null);
        final String serialNo = data.optString("SerialNumber", (String)null);
        final String udid = data.optString("UDID", (String)null);
        final String easId = data.optString("EASID", (String)null);
        final Long customerId = data.getLong("CustomerId");
        final String genericID = data.optString("GENERIC_ID");
        final Boolean allowDuplicateSerialNumber = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ALLOW_DUPLICATE_SERIAL_NUMBER");
        Criteria criteria = null;
        if (!MDMStringUtils.isEmpty(serialNo) && (templateType == 10 || !allowDuplicateSerialNumber)) {
            criteria = new Criteria(new Column("DeviceForEnrollment", "SERIAL_NUMBER"), (Object)serialNo, 0, false);
        }
        if (!MDMStringUtils.isEmpty(imei)) {
            if (criteria == null) {
                criteria = new Criteria(new Column("DeviceForEnrollment", "IMEI"), (Object)imei, 0, false);
            }
            else {
                criteria = criteria.or(new Criteria(new Column("DeviceForEnrollment", "IMEI"), (Object)imei, 0, false));
            }
        }
        if (!MDMStringUtils.isEmpty(genericID)) {
            if (criteria == null) {
                criteria = new Criteria(new Column("DeviceForEnrollment", "GENERIC_IDENTIFIER"), (Object)genericID, 0, false);
            }
            else {
                criteria = criteria.or(new Criteria(new Column("DeviceForEnrollment", "GENERIC_IDENTIFIER"), (Object)genericID, 0, false));
            }
        }
        if (!MDMStringUtils.isEmpty(udid)) {
            final Criteria udidCriteria = new Criteria(Column.getColumn("DeviceForEnrollment", "UDID"), (Object)udid, 0, false);
            if (criteria == null) {
                criteria = udidCriteria;
            }
            else {
                criteria = criteria.or(udidCriteria);
            }
        }
        if (!MDMStringUtils.isEmpty(easId)) {
            if (criteria == null) {
                criteria = new Criteria(new Column("DeviceForEnrollment", "EAS_DEVICE_IDENTIFIER"), (Object)easId, 0, false);
            }
            else {
                criteria = criteria.or(new Criteria(new Column("DeviceForEnrollment", "EAS_DEVICE_IDENTIFIER"), (Object)easId, 0, false));
            }
        }
        if (criteria == null) {
            AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "No entry for device in DeviceForEnrollment, so Assign user stops");
            throw new SyMException(14011, "Device is not present", "mdm.enroll.device_not_found", (Throwable)null);
        }
        criteria = criteria.and(new Criteria(new Column("DeviceForEnrollment", "CUSTOMER_ID"), (Object)customerId, 0));
        deviceQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToRequest", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        deviceQuery.addJoin(new Join("DeviceForEnrollment", "DeviceEnrollmentToUser", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        deviceQuery.addJoin(new Join("DeviceEnrollmentToRequest", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        deviceQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        deviceQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        deviceQuery.addJoin(new Join("DeviceForEnrollment", "EnrollmentTemplateToDeviceEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 1));
        deviceQuery.addSelectColumn(new Column((String)null, "*"));
        deviceQuery.setCriteria(criteria);
        return deviceQuery;
    }
    
    private static void performUserAssignment(final JSONObject deviceJSON, final HashMap managedUserDetails, final Integer templateType, final Long technicianUserId, final ArrayList<DeviceEvent> awaitingDeviceActivationListenerList, final ArrayList<DeviceEventForQueue> invokeListenerList) throws Exception {
        DMSecurityLogger.info(AdminEnrollmentHandler.assignUserLogger, AdminEnrollmentHandler.class.getName(), "performUserAssignment()", "Performing user assignment with device data : " + deviceJSON.toString() + " and user details : " + managedUserDetails.toString(), (Object)null);
        final Long userId = managedUserDetails.get("MANAGED_USER_ID");
        final SelectQuery deviceQuery = getDeviceForEnrollmentQuery(deviceJSON, templateType);
        final DataObject dO = DataAccess.get(deviceQuery);
        final Iterator<Row> iterator = dO.getRows("DeviceEnrollmentToRequest");
        final String deviceName = deviceJSON.optString("DeviceName");
        final Long customerId = deviceJSON.getLong("CustomerId");
        String serialNo = deviceJSON.optString("SerialNumber", (String)null);
        String udid = deviceJSON.optString("UDID", (String)null);
        int platform = deviceJSON.getInt("platform");
        deviceJSON.put("technician_id", (Object)technicianUserId);
        final List<Long> groupIdsList = getGroupIdsListForUserAssignment(deviceJSON);
        final List<Long> oldUserList = new ArrayList<Long>();
        final DeviceForEnrollmentHandler deviceForEnrollmentHandler = new DeviceForEnrollmentHandler();
        Long deviceForEnrollId;
        if (iterator.hasNext()) {
            Row row = iterator.next();
            deviceForEnrollId = (Long)row.get("ENROLLMENT_DEVICE_ID");
            final Long enrollmentRequestId = (Long)row.get("ENROLLMENT_REQUEST_ID");
            ManagedUserHandler.getInstance().changeUser(userId, new Long[] { enrollmentRequestId }, true, technicianUserId);
            addDeviceForEnrollmentProperties(deviceForEnrollId, userId, groupIdsList, deviceName);
            MDMEnrollmentRequestHandler.getInstance().addEnrollmentToGroupEntries(enrollmentRequestId, groupIdsList);
            final Iterator<Row> iter = dO.getRows("ManagedDevice");
            if (iter.hasNext()) {
                row = iter.next();
                final Long resourceId = (Long)row.get("RESOURCE_ID");
                if (deviceName != null && !deviceName.equals("")) {
                    final org.json.simple.JSONObject resourceJSON = new org.json.simple.JSONObject();
                    resourceJSON.put((Object)"MANAGED_DEVICE_ID", (Object)resourceId);
                    resourceJSON.put((Object)"NAME", (Object)deviceName);
                    resourceJSON.put((Object)"IS_MODIFIED", (Object)true);
                    resourceJSON.put((Object)"USER_ID", (Object)technicianUserId);
                    resourceJSON.put((Object)"DESCRIPTION", (Object)"");
                    resourceJSON.put((Object)"ENROLLMENT_REQUEST_ID", (Object)deviceForEnrollId);
                    MDCustomDetailsRequestHandler.getInstance().addOrUpdateCustomDeviceDetails(resourceJSON);
                }
                final int managed_status = ManagedDeviceHandler.getInstance().getManagedDeviceStatus(resourceId);
                if (managed_status == 5 || managed_status == 6) {
                    if (managed_status == 5 && !MDMEnrollmentUtil.getInstance().isLicenseLimitReached(customerId)) {
                        updateManagedDeviceStatus(resourceId);
                    }
                    else {
                        ManagedDeviceHandler.getInstance().updateManagedDeviceStatus(Collections.singletonList(resourceId), 6);
                    }
                    platform = (int)((platform == -1) ? row.get("PLATFORM_TYPE") : platform);
                    final DeviceEventForQueue deviceEvent = new DeviceEventForQueue(resourceId, customerId);
                    deviceEvent.udid = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceId);
                    deviceEvent.enrollmentRequestId = enrollmentRequestId;
                    deviceEvent.platformType = platform;
                    final JSONObject resourceJSON2 = new JSONObject();
                    final Row dfeRow = dO.getFirstRow("DeviceForEnrollment");
                    serialNo = (String)dfeRow.get("SERIAL_NUMBER");
                    udid = (String)dfeRow.get("UDID");
                    resourceJSON2.put("SERIAL_NUMBER", (Object)serialNo);
                    resourceJSON2.put("GENERIC_IDENTIFIER", (Object)dfeRow.get("GENERIC_IDENTIFIER"));
                    resourceJSON2.put("IMEI", (Object)dfeRow.get("IMEI"));
                    resourceJSON2.put("UDID", (Object)udid);
                    resourceJSON2.put("EAS_DEVICE_IDENTIFIER", (Object)dfeRow.get("EAS_DEVICE_IDENTIFIER"));
                    resourceJSON2.put("PLATFORM_TYPE", platform);
                    resourceJSON2.put("RESOURCE_ID", (Object)resourceId);
                    resourceJSON2.put("SerialNumber", (Object)((serialNo != null) ? serialNo : ""));
                    resourceJSON2.put("RESOURCE_TYPE", ResourceDataProvider.getResourceType(resourceId));
                    resourceJSON2.put("addedUserID", (Object)technicianUserId);
                    deviceEvent.resourceJSON = resourceJSON2.toString();
                    AdminEnrollmentHandler.logger.log(Level.INFO, "Adding device data for post user assignment task invocation : {0}", resourceJSON2.toString());
                    invokeListenerList.add(deviceEvent);
                }
            }
        }
        else {
            deviceJSON.put("SkipUserAssignmentAutomation", (Object)Boolean.TRUE);
            deviceForEnrollId = deviceForEnrollmentHandler.addDeviceForEnrollment(deviceJSON, templateType);
            final Long oldUserID = new DeviceForEnrollmentHandler().getAssociatedUserid(deviceForEnrollId);
            addDeviceForEnrollmentProperties(deviceForEnrollId, userId, groupIdsList, deviceName);
            if (oldUserID != null && userId != oldUserID) {
                oldUserList.add(oldUserID);
            }
            if (templateType != null && templateType.equals(31)) {
                final DeviceEvent deviceEvent2 = new DeviceEvent();
                deviceEvent2.customerID = customerId;
                deviceEvent2.platformType = platform;
                final JSONObject resourceDetailsJSON = new JSONObject();
                resourceDetailsJSON.put("SERIAL_NUMBER", (Object)deviceJSON.optString("SerialNumber", ""));
                resourceDetailsJSON.put("UDID", (Object)deviceJSON.optString("UDID", ""));
                resourceDetailsJSON.put("IMEI", (Object)deviceJSON.optString("IMEI", ""));
                resourceDetailsJSON.put("TEMPLATE_TYPE", (Object)templateType);
                resourceDetailsJSON.put("MANAGED_USER_DETAILS", (Object)new JSONObject((Map)managedUserDetails));
                resourceDetailsJSON.put("TEMPLATE_TOKEN", (Object)new EnrollmentTemplateHandler().getTemplateTokenForUserId(technicianUserId, 31, customerId));
                String userApiKey = null;
                if (templateType != null && templateType.equals(31)) {
                    final JSONObject loginInfo = new JSONObject();
                    loginInfo.put("LOGIN_ID", (Object)DMUserHandler.getLoginIdForUserId(technicianUserId));
                    userApiKey = MDMUserAPIKeyGenerator.getInstance().generateAPIKey(loginInfo).getKeyValue();
                }
                resourceDetailsJSON.put("ZAPIKEY", (Object)userApiKey);
                deviceEvent2.resourceJSON = resourceDetailsJSON;
                AdminEnrollmentHandler.logger.log(Level.INFO, "Adding pre user assigned device data for post user assignment task invocation : {0}", resourceDetailsJSON.toString());
                awaitingDeviceActivationListenerList.add(deviceEvent2);
            }
            if (templateType != null && templateType.equals(12)) {
                final DeviceEvent deviceEvent2 = new DeviceEvent();
                deviceEvent2.customerID = customerId;
                if (platform == -1) {
                    platform = 1;
                }
                deviceEvent2.platformType = platform;
                final JSONObject resourceDetailsJSON = new JSONObject();
                resourceDetailsJSON.put("SERIAL_NUMBER", (Object)deviceJSON.optString("SerialNumber", ""));
                resourceDetailsJSON.put("UDID", (Object)deviceJSON.optString("UDID", ""));
                resourceDetailsJSON.put("IMEI", (Object)deviceJSON.optString("IMEI", ""));
                resourceDetailsJSON.put("TEMPLATE_TYPE", (Object)templateType);
                resourceDetailsJSON.put("MANAGED_USER_DETAILS", (Object)new JSONObject((Map)managedUserDetails));
                final JSONObject templateDetails = EnrollmentTemplateHandler.getModenMacMgmtEnrollmentTemplateDetailsForCustomer(customerId);
                JSONUtil.putAll(resourceDetailsJSON, templateDetails);
                deviceEvent2.resourceJSON = resourceDetailsJSON;
                awaitingDeviceActivationListenerList.add(deviceEvent2);
            }
        }
        final Row deviceForEnrollmentRow = dO.getRow("DeviceForEnrollment", new Criteria(Column.getColumn("DeviceForEnrollment", "ENROLLMENT_DEVICE_ID"), (Object)deviceForEnrollId, 0));
        if (deviceForEnrollmentRow != null) {
            deviceForEnrollmentRow.set("REMARKS", (Object)"mdm.db.user_assignment_completed");
            deviceForEnrollmentRow.set("STATUS", (Object)2);
            dO.updateRow(deviceForEnrollmentRow);
            MDMUtil.getPersistence().update(dO);
        }
        AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "performUserAssignment() operation completed");
        final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
        logJSON.put((Object)"REMARKS", (Object)"assign-success");
        logJSON.put((Object)"SERIAL_NUMBER", (Object)serialNo);
        logJSON.put((Object)"UDID", (Object)udid);
        logJSON.put((Object)"NAME", (Object)deviceName);
        logJSON.put((Object)"ENROLLMENT_DEVICE_ID", (Object)deviceForEnrollId);
        logJSON.put((Object)"MANAGED_USER_ID", (Object)userId);
        MDMOneLineLogger.log(Level.INFO, "DEVICE_USER_ASSIGNED", logJSON);
    }
    
    private static List<Long> getGroupIdsListForUserAssignment(final JSONObject data) throws SyMException {
        List<Long> groupIdsList = data.has("GroupId") ? JSONUtil.getInstance().convertLongJSONArrayTOList(data.getJSONArray("GroupId")) : null;
        final String groupName = data.optString("GroupName");
        final Long customerId = data.getLong("CustomerId");
        final Long technicianUserID = JSONUtil.optLongForUVH(data, "technician_id", Long.valueOf(-1L));
        if (groupIdsList == null) {
            if (groupName != null && !groupName.isEmpty() && !groupName.equals("--")) {
                try {
                    groupIdsList = MDMGroupHandler.getCustomGroupDetailsForMultipleGroups(groupName, technicianUserID, customerId);
                    if (groupIdsList == null || groupIdsList.size() == 0) {
                        throw new SyMException(14006, "Group not found", "dc.mdm.enroll.group_not_available", (Throwable)null);
                    }
                    return groupIdsList;
                }
                catch (final Exception ex) {
                    throw new SyMException(14006, "Group not found", "dc.mdm.enroll.group_not_available", (Throwable)null);
                }
            }
            groupIdsList = new ArrayList<Long>();
        }
        return groupIdsList;
    }
    
    private static void invokePostUserAssignmentListenersFromList(final ArrayList<DeviceEventForQueue> invokeListenerList) throws Exception {
        if (invokeListenerList.size() > 0) {
            final String qFileName = "assign-user-" + System.currentTimeMillis() + ".txt";
            final DCQueueData queueData = new DCQueueData();
            queueData.fileName = qFileName;
            queueData.postTime = System.currentTimeMillis();
            queueData.queueData = invokeListenerList;
            final String separator = "\t";
            AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "QueueName : {0}{1}AddingToQueue{2}{3}{4}{5}", new Object[] { "assign-user", separator, separator, queueData.fileName, separator, String.valueOf(System.currentTimeMillis()) });
            final DCQueue queue = DCQueueHandler.getQueue("assign-user");
            AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "Queue data added - FileName : {0}\t QueuDataType : {1}", new Object[] { queueData.fileName, queueData.queueDataType });
            queue.addToQueue(queueData);
        }
    }
    
    private static void invokeDevicePreAssignedListenersFromList(final ArrayList<DeviceEvent> awaitingDeviceActivationListenerList) {
        AdminEnrollmentHandler.assignUserLogger.log(Level.INFO, "Invoking device preassigned listener list");
        for (final DeviceEvent deviceEvent : awaitingDeviceActivationListenerList) {
            DMSecurityLogger.info(AdminEnrollmentHandler.assignUserLogger, AdminEnrollmentHandler.class.getName(), "invokeDevicePreAssignedListenersFromList()", "invoking device pre user assigned listener for data : " + deviceEvent.toString(), (Object)null);
            ManagedDeviceHandler.getInstance().invokeDeviceListeners(deviceEvent, 8);
        }
    }
    
    static {
        AdminEnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
        AdminEnrollmentHandler.assignUserLogger = Logger.getLogger("MDMAssignUser");
    }
}
