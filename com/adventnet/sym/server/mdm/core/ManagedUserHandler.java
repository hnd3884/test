package com.adventnet.sym.server.mdm.core;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.i18n.I18N;
import com.me.idps.core.util.DirectoryAttributeConstants;
import java.sql.Connection;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.UnionQuery;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.adventnet.ds.query.UnionQueryImpl;
import java.util.LinkedList;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.Set;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.HashSet;
import java.util.Arrays;
import com.adventnet.persistence.DataAccess;
import java.util.Collection;
import com.me.mdm.server.user.ManagedUserFacade;
import java.sql.SQLException;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import java.util.Map;
import java.util.Iterator;
import org.json.JSONException;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.me.mdm.webclient.i18n.MDMI18N;
import java.text.MessageFormat;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.net.URLDecoder;
import com.me.idps.core.util.DirectoryUtil;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.adventnet.ds.query.UpdateQuery;
import java.util.HashMap;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.resource.ResourceDataPopulator;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.resource.MDMResourceDataPopulator;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.persistence.DataObject;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ManagedUserHandler
{
    private static ManagedUserHandler managedUserHandlert;
    public static final int USER_ADDED = 1;
    public static final int USER_REMOVED = 2;
    public static final int USER_DETAILS_MODIFIED = 3;
    public static final int USER_TRASHED = 4;
    public Logger logger;
    public Logger assignUserLogger;
    String sourceClass;
    private List<ManagedUserListener> userListenerList;
    
    protected ManagedUserHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
        this.assignUserLogger = Logger.getLogger("MDMAssignUser");
        this.sourceClass = "ManagedUserHandler";
        this.userListenerList = new ArrayList<ManagedUserListener>();
    }
    
    public static synchronized ManagedUserHandler getInstance() {
        if (ManagedUserHandler.managedUserHandlert == null) {
            if (CustomerInfoUtil.isSAS) {
                try {
                    ManagedUserHandler.managedUserHandlert = (ManagedUserHandler)Class.forName("com.me.mdmcloud.server.enroll.ManagedUserHandlerCloudImpl").newInstance();
                }
                catch (final ClassNotFoundException ce) {
                    Logger.getLogger("MDMEnrollment").log(Level.SEVERE, "ClassNotFoundException  during Instantiation for ManagedUserHandler... ", ce);
                }
                catch (final InstantiationException ie) {
                    Logger.getLogger("MDMEnrollment").log(Level.SEVERE, "InstantiationException During Instantiation  for ManagedUserHandler...", ie);
                }
                catch (final IllegalAccessException ie2) {
                    Logger.getLogger("MDMEnrollment").log(Level.SEVERE, "IllegalAccessException During Instantiation  for ManagedUserHandler...", ie2);
                }
                catch (final Exception ex) {
                    Logger.getLogger("MDMEnrollment").log(Level.SEVERE, "Exception During Instantiation  for ManagedUserHandler...", ex);
                }
            }
            else {
                ManagedUserHandler.managedUserHandlert = new ManagedUserHandler();
            }
        }
        return ManagedUserHandler.managedUserHandlert;
    }
    
    public void addManagedUserListener(final ManagedUserListener userListener) {
        this.userListenerList.add(userListener);
    }
    
    public void invokeUserListeners(final UserEvent userEvent, final int operation) {
        final int l = this.userListenerList.size();
        this.logger.log(Level.INFO, "ManagedUserHandler: invokeUserListeners() called..");
        if (operation == 1) {
            this.logger.log(Level.INFO, "ManagedUserHandler: USER_ADDED event");
            for (int s = 0; s < l; ++s) {
                final ManagedUserListener listener = this.userListenerList.get(s);
                listener.userAdded(userEvent);
            }
        }
        else if (operation == 2) {
            this.logger.log(Level.INFO, "ManagedUserHandler: USER_REMOVED event");
            for (int s = 0; s < l; ++s) {
                final ManagedUserListener listener = this.userListenerList.get(s);
                listener.userDeleted(userEvent);
            }
        }
        else if (operation == 3) {
            this.logger.log(Level.INFO, "ManagedUserHandler: USER_MODIFIED event");
            for (int s = 0; s < l; ++s) {
                final ManagedUserListener listener = this.userListenerList.get(s);
                listener.userDetailsModified(userEvent);
            }
        }
        else if (operation == 4) {
            this.logger.log(Level.INFO, "ManagedUserHandler: USER_TRASHED event");
            for (int s = 0; s < l; ++s) {
                final ManagedUserListener listener = this.userListenerList.get(s);
                listener.userTrashed(userEvent);
            }
        }
    }
    
    public DataObject addOrUpdateManagedUser(final Properties properties) throws SyMException {
        final String methodName = "addOrUpdateManagedUser";
        DataObject dataObject = null;
        SyMLogger.info(this.logger, this.sourceClass, methodName, properties.toString());
        try {
            final String userName = ((Hashtable<K, String>)properties).get("NAME");
            final String domainNetbiosName = ((Hashtable<K, String>)properties).get("DOMAIN_NETBIOS_NAME");
            final Long customerID = ((Hashtable<K, Long>)properties).get("CUSTOMER_ID");
            Long managedUserID = ((Hashtable<K, Long>)properties).get("MANAGED_USER_ID");
            if (managedUserID == null) {
                managedUserID = this.getManagedUserId(userName, domainNetbiosName, customerID);
            }
            if (managedUserID == null) {
                if (userName != null && domainNetbiosName != null && customerID != null) {
                    properties.setProperty("RESOURCE_TYPE", String.valueOf(2));
                    final DataObject resourceDO = MDMResourceDataPopulator.addOrUpdateMDMResource(properties);
                    if (!resourceDO.isEmpty()) {
                        managedUserID = (Long)resourceDO.getFirstValue("Resource", "RESOURCE_ID");
                        dataObject = this.addOrUpdateManagedUser(managedUserID, properties);
                    }
                }
            }
            else {
                dataObject = this.addOrUpdateManagedUser(managedUserID, properties);
            }
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, methodName, "Exception occured during addition of Managed User !!!", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
        return dataObject;
    }
    
    public Long addOrUpdateAndGetUserId(final JSONObject json) throws Exception {
        final Properties props = new Properties();
        ((Hashtable<String, String>)props).put("NAME", String.valueOf(json.get("NAME")));
        ((Hashtable<String, String>)props).put("DISPLAY_NAME", String.valueOf(json.optString("DISPLAY_NAME", String.valueOf(json.get("NAME")))));
        ((Hashtable<String, String>)props).put("FIRST_NAME", String.valueOf(json.optString("FIRST_NAME", String.valueOf(json.get("NAME")))));
        ((Hashtable<String, String>)props).put("DOMAIN_NETBIOS_NAME", String.valueOf(json.get("DOMAIN_NETBIOS_NAME")));
        ((Hashtable<String, Long>)props).put("CUSTOMER_ID", json.getLong("CUSTOMER_ID"));
        ((Hashtable<String, String>)props).put("EMAIL_ADDRESS", String.valueOf(json.get("EMAIL_ADDRESS")));
        if (json.has("PHONE_NUMBER")) {
            if (!MDMUtil.isStringValid(String.valueOf(json.get("PHONE_NUMBER")))) {
                this.logger.log(Level.INFO, "ManagedUserHandler: PHONE_NUMBER:{0}", json.get("PHONE_NUMBER"));
            }
            else {
                ((Hashtable<String, String>)props).put("PHONE_NUMBER", String.valueOf(json.get("PHONE_NUMBER")));
            }
        }
        return this.addOrUpdateAndGetUserId(props);
    }
    
    public Long addOrUpdateAndGetUserId(final Properties properties) throws SyMException {
        DMSecurityLogger.info(this.logger, ManagedUserHandler.class.getName(), "addOrUpdateAndGetUserId", "Entered add/update user with properties : {0}", (Object)properties);
        final String methodName = "addOrUpdateManagedUser";
        DataObject dataObject = null;
        Long managedUserID = -1L;
        try {
            final String userName = ((Hashtable<K, String>)properties).get("NAME");
            final String domainNetbiosName = ((Hashtable<K, String>)properties).get("DOMAIN_NETBIOS_NAME");
            final Long customerID = Long.valueOf(((Hashtable<K, Object>)properties).get("CUSTOMER_ID").toString());
            if (userName != null && domainNetbiosName != null && customerID != null) {
                properties.setProperty("RESOURCE_TYPE", String.valueOf(2));
                final DataObject resourceDO = MDMResourceDataPopulator.addOrUpdateMDMResource(properties);
                if (!resourceDO.isEmpty()) {
                    managedUserID = (Long)resourceDO.getFirstValue("Resource", "RESOURCE_ID");
                    dataObject = this.addOrUpdateManagedUser(managedUserID, properties);
                }
                managedUserID = (Long)dataObject.getFirstValue("ManagedUser", "MANAGED_USER_ID");
            }
        }
        catch (final Exception exp) {
            SyMLogger.error(this.logger, this.sourceClass, methodName, "Exception occured during addition of Managed User !!!", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
        this.logger.log(Level.INFO, "Returning managed user id : {0}", managedUserID.toString());
        return managedUserID;
    }
    
    private void restrictUserNameModification(final Long managedUserID) throws Exception {
        final Criteria userCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"), (Object)managedUserID, 0);
        final Criteria managedCriteria = userCriteria.and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)3, 0));
        final int mdCount = MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCount(managedCriteria);
        if (mdCount != 0) {
            throw new SyMException(52101, "All the profiles which uses email account, will be re-distributed automatically with the modified email address. Example : Email, Exchange, CalDAV, etc,.", "dc.mdm.enroll.change_user_email.confirm.only_device", (Throwable)null);
        }
    }
    
    public void updateUserNameForManagedUser(final JSONObject json, final Long customerID) throws Exception {
        if (!json.has("USER_IDENTIFIER")) {
            json.put("USER_IDENTIFIER", (Object)"ENROLLMENT_REQUEST_ID");
        }
        final JSONObject managedUserDetails = this.getManagedUserDetails(json);
        final String userName = String.valueOf(json.get("NAME"));
        if (!userName.isEmpty() && !userName.equalsIgnoreCase("--")) {
            final String userNameInRepo = managedUserDetails.optString("NAME");
            if (!userNameInRepo.equalsIgnoreCase(userName)) {
                final HashMap map = getInstance().getManagedUserDetailsForUserName(userName, "MDM", customerID);
                if (!map.isEmpty() && map.containsKey("MANAGED_USER_ID")) {
                    final Object[] errorParams = { ProductUrlLoader.getInstance().getValue("mdmUrl") };
                    throw new SyMException(52103, "A user already exists for given email address", "dc.mdm.enroll.error.change_user_email_exists", errorParams, (Throwable)null);
                }
                if (!json.optBoolean("UPDATE_BLINDLY", false)) {
                    this.restrictUserNameModification(managedUserDetails.getLong("MANAGED_USER_ID"));
                }
                MDMResourceDataPopulator.updateDBUpdatedTime(managedUserDetails.getLong("MANAGED_USER_ID"));
                ResourceDataPopulator.renameResource(Long.valueOf(managedUserDetails.getLong("MANAGED_USER_ID")), userName, Boolean.FALSE);
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ManagedUser");
                updateQuery.setUpdateColumn("FIRST_NAME", (Object)userName);
                updateQuery.setUpdateColumn("DISPLAY_NAME", (Object)userName);
                updateQuery.setCriteria(new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)managedUserDetails.getLong("MANAGED_USER_ID"), 0));
                MDMUtil.getPersistenceLite().update(updateQuery);
                final UserEvent userEvent = new UserEvent(managedUserDetails.getLong("MANAGED_USER_ID"), customerID, ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
                final JSONObject additionalDetails = new JSONObject();
                final JSONArray modifiedFields = new JSONArray();
                modifiedFields.put((Object)"NAME");
                modifiedFields.put((Object)"DISPLAY_NAME");
                modifiedFields.put((Object)"FIRST_NAME");
                additionalDetails.put("MODIFIED_FIELDS", (Object)modifiedFields);
                userEvent.additionalDetails = additionalDetails;
                getInstance().invokeUserListeners(userEvent, 3);
                final String emailAddress = String.valueOf(managedUserDetails.get("EMAIL_ADDRESS"));
                final Object remarksArgs = userNameInRepo + "@@@" + userName + "@@@" + emailAddress + "@@@";
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), "dc.mdm.enroll.change_user_name_actionlog", remarksArgs, customerID);
                final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                logJSON.put((Object)"REMARKS", (Object)"update-success");
                logJSON.put((Object)"MANAGED_USER_ID", (Object)managedUserDetails.getLong("MANAGED_USER_ID"));
                MDMOneLineLogger.log(Level.INFO, "MODIFY_USER", logJSON);
            }
        }
    }
    
    protected void restrictEmailIDModification(final Long managedUserID) throws Exception {
        final Criteria userCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"), (Object)managedUserID, 0);
        final Criteria reqCriteria = userCriteria.and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)new Integer[] { 1, 0 }, 8));
        final Criteria managedCriteria = userCriteria.and(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)3, 0));
        final int reqCount = MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCount(reqCriteria);
        final int mdCount = MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCount(managedCriteria);
        if (reqCount != 0 && mdCount != 0) {
            throw new SyMException(52102, "Modifying the email address will impact the following: 1.>All the profiles which uses email account, will be re-distributed automatically with the modified email address - {0}. Example : Email, VPN, Exchange, Calendar, etc,. 2.If any devices are yet to be enrolled, then enrollment request will be re-sent to the email address.", "dc.mdm.enroll.change_user_email.confirm.device_and_invite", (Throwable)null);
        }
        if (reqCount != 0) {
            throw new SyMException(52100, "Enrollment request will be re-sent to the email address", "dc.mdm.enroll.change_user_email.confirm.only_invite", (Throwable)null);
        }
        if (mdCount != 0) {
            throw new SyMException(52101, "All the profiles which uses email account, will be re-distributed automatically with the modified email address. Example : Email, Exchange, CalDAV, etc,.", "dc.mdm.enroll.change_user_email.confirm.only_device", (Throwable)null);
        }
    }
    
    public void updateEmailAddressForManagedUser(final JSONObject json, final Long customerID) throws Exception {
        this.logger.log(Level.INFO, "ManagedUserHandler: updateEmailAddressForManagedUser() json = ", json.toString());
        final MDMUtil mdmUtil = MDMUtil.getInstance();
        HashMap<String, Object> hashMap = null;
        boolean phoneNumberUpdated = false;
        if (json.has("MANAGED_USER_ID")) {
            hashMap = this.getManagedUserDetails(json.getLong("MANAGED_USER_ID"));
        }
        else {
            if (!json.has("ENROLLMENT_REQUEST_ID")) {
                throw new Exception("Need Managed User ID / Enrollment Request ID");
            }
            hashMap = this.getManagedUserDetailsForRequest(json.getLong("ENROLLMENT_REQUEST_ID"));
        }
        json.put("CUSTOMER_ID", (Object)customerID);
        final String domainName = hashMap.get("DOMAIN_NETBIOS_NAME");
        final boolean isADManagedDomain = DMDomainDataHandler.getInstance().isADManagedDomain(domainName, customerID);
        if (isADManagedDomain) {
            DirectoryUtil.getInstance().syncDomain(domainName, customerID, Boolean.valueOf(false));
        }
        final String emailInRepo = hashMap.get("EMAIL_ADDRESS");
        String emailAddr = json.optString("EMAIL_ADDRESS");
        emailAddr = emailAddr.replaceAll("\\+", "%2b");
        emailAddr = URLDecoder.decode(emailAddr, "UTF-8");
        String phoneNumInRepo = "--";
        String phoneNum = "--";
        if (hashMap.containsKey("PHONE_NUMBER")) {
            phoneNumInRepo = hashMap.get("PHONE_NUMBER");
        }
        if (json.has("PHONE_NUMBER")) {
            phoneNum = String.valueOf(json.get("PHONE_NUMBER"));
        }
        if (isADManagedDomain || MDMUtil.getInstance().isValidEmail(emailAddr)) {
            if (!isADManagedDomain && MDMUtil.getInstance().isValidEmail(emailInRepo) && emailInRepo.equals(emailAddr)) {
                MDMEnrollmentUtil.getInstance();
                if ((!MDMEnrollmentUtil.isValidPhone(phoneNum) && !phoneNum.equalsIgnoreCase("")) || phoneNumInRepo.equalsIgnoreCase(phoneNum)) {
                    return;
                }
            }
            if (MDMUtil.getInstance().isValidEmail(emailInRepo) && !json.optBoolean("UPDATE_BLINDLY", false)) {
                this.restrictEmailIDModification(hashMap.get("MANAGED_USER_ID"));
            }
            final Properties props = new Properties();
            if (!MDMUtil.getInstance().isValidEmail(emailAddr)) {
                if (!MDMUtil.getInstance().isEmpty(emailInRepo)) {
                    ((Hashtable<String, String>)props).put("EMAIL_ADDRESS", emailInRepo);
                }
                else {
                    ((Hashtable<String, String>)props).put("EMAIL_ADDRESS", "--");
                }
            }
            else {
                ((Hashtable<String, String>)props).put("EMAIL_ADDRESS", emailAddr);
            }
            if (json.has("FIRST_NAME")) {
                ((Hashtable<String, String>)props).put("FIRST_NAME", String.valueOf(json.get("FIRST_NAME")));
            }
            if (json.has("MIDDLE_NAME")) {
                ((Hashtable<String, String>)props).put("MIDDLE_NAME", String.valueOf(json.get("MIDDLE_NAME")));
            }
            if (json.has("LAST_NAME")) {
                ((Hashtable<String, String>)props).put("LAST_NAME", String.valueOf(json.get("LAST_NAME")));
            }
            if (json.has("DISPLAY_NAME")) {
                ((Hashtable<String, String>)props).put("DISPLAY_NAME", String.valueOf(json.optString("DISPLAY_NAME")));
            }
            if (json.has("PHONE_NUMBER")) {
                ((Hashtable<String, String>)props).put("PHONE_NUMBER", String.valueOf(json.get("PHONE_NUMBER")));
                phoneNumberUpdated = true;
            }
            this.updateManagedUserEmail(hashMap.get("MANAGED_USER_ID"), props);
            MDMResourceDataPopulator.updateDBUpdatedTime(hashMap.get("MANAGED_USER_ID"));
            if (mdmUtil.isValidEmail(emailAddr) && (mdmUtil.isEmpty(emailInRepo) || !emailAddr.equalsIgnoreCase(emailInRepo))) {
                final UserEvent userEvent = new UserEvent(hashMap.get("MANAGED_USER_ID"), customerID, ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
                if (MDMUtil.getInstance().isValidEmail(emailAddr)) {
                    final JSONObject additionalDetails = new JSONObject();
                    final JSONArray modifiedFields = new JSONArray();
                    modifiedFields.put((Object)"EMAIL_ADDRESS");
                    if (phoneNumberUpdated) {
                        modifiedFields.put((Object)"PHONE_NUMBER");
                    }
                    additionalDetails.put("MODIFIED_FIELDS", (Object)modifiedFields);
                    userEvent.additionalDetails = additionalDetails;
                }
                getInstance().invokeUserListeners(userEvent, 3);
                final String userName = hashMap.get("NAME");
                final Object remarksArgs = emailInRepo + "@@@" + emailAddr + "@@@" + userName + "@@@";
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), "dc.mdm.enroll.change_user_email_actionlog", remarksArgs, customerID);
                final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                logJSON.put((Object)"REMARKS", (Object)"update-success");
                logJSON.put((Object)"MANAGED_USER_ID", (Object)userEvent.resourceID);
                MDMOneLineLogger.log(Level.INFO, "MODIFY_USER", logJSON);
            }
        }
    }
    
    public void invokeUserListeners(final String domainName, final Long resID, final org.json.simple.JSONObject userEvents, final Long customerID, final Long syncInitiatedBy, final String syncInitiatedByUserName, final int eventType) {
        try {
            final UserEvent userEvent = new UserEvent(resID, customerID, syncInitiatedBy);
            userEvent.additionalDetails = JSONUtil.getInstance().convertSimpleJSONtoJSON(userEvents);
            final org.json.simple.JSONArray modifiedFields = (org.json.simple.JSONArray)userEvents.get((Object)"MODIFIED_FIELDS");
            final String displayName = (String)DBUtil.getValueFromDB("ManagedUser", "MANAGED_USER_ID", (Object)resID, "DISPLAY_NAME");
            getInstance().invokeUserListeners(userEvent, eventType);
            final String i18nArgs = MessageFormat.format("{0}{1}{2}{3}{4}{5}", this.getModifiedFieldsStr(modifiedFields), "@@@", displayName, "@@@", domainName, "@@@");
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2001, null, syncInitiatedByUserName, "mdm.dir.attr.modify", i18nArgs, customerID);
            final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
            logJSON.put((Object)"REMARKS", (Object)"update-success");
            logJSON.put((Object)"MANAGED_USER_ID", (Object)userEvent.resourceID);
            MDMOneLineLogger.log(Level.INFO, "MODIFY_USER", logJSON);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private String getCurField(final String curField) {
        switch (curField) {
            case "LAST_NAME": {
                return MDMI18N.getI18Nmsg("mdm.ad.last.name");
            }
            case "NAME": {
                return MDMI18N.getI18Nmsg("dc.mdm.device_mgmt.username");
            }
            case "EMAIL_ADDRESS": {
                return MDMI18N.getI18Nmsg("dc.common.email");
            }
            case "FIRST_NAME": {
                return MDMI18N.getI18Nmsg("dc.common.FIRST_NAME");
            }
            case "MIDDLE_NAME": {
                return MDMI18N.getI18Nmsg("dc.common.MIDDLE_NAME");
            }
            case "PHONE_NUMBER": {
                return MDMI18N.getI18Nmsg("dc.mdm.inv.phone_number");
            }
            case "DISPLAY_NAME": {
                return MDMI18N.getI18Nmsg("mdm.deviceMgmt.DisplayName");
            }
            default: {
                return curField;
            }
        }
    }
    
    private String getModifiedFieldsStr(final org.json.simple.JSONArray modified_fields) {
        String str = "";
        for (int i = 0; modified_fields != null && i < modified_fields.size(); ++i) {
            final String curField = (String)modified_fields.get(i);
            str += this.getCurField(curField);
            if (i < modified_fields.size() - 1) {
                str += ", ";
            }
        }
        return str;
    }
    
    private DataObject addOrUpdateManagedUser(final Long managedUserID, final Properties properties) throws DataAccessException {
        final String emailID = ((Hashtable<K, String>)properties).get("EMAIL_ADDRESS");
        final Criteria criteria = new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)managedUserID, 0);
        final DataObject managedUserObject = MDMUtil.getPersistence().get("ManagedUser", criteria);
        Row managedUserRow = null;
        if (managedUserObject.isEmpty()) {
            managedUserRow = new Row("ManagedUser");
            managedUserRow.set("MANAGED_USER_ID", (Object)managedUserID);
            managedUserRow.set("EMAIL_ADDRESS", (Object)emailID);
            managedUserRow.set("FIRST_NAME", ((Hashtable<K, Object>)properties).get("FIRST_NAME"));
            managedUserRow.set("MIDDLE_NAME", ((Hashtable<K, Object>)properties).get("MIDDLE_NAME"));
            managedUserRow.set("LAST_NAME", ((Hashtable<K, Object>)properties).get("LAST_NAME"));
            managedUserRow.set("DISPLAY_NAME", ((Hashtable<K, Object>)properties).get("DISPLAY_NAME"));
            if (properties.containsKey("PHONE_NUMBER") && MDMUtil.isStringValid(((Hashtable<K, Object>)properties).get("PHONE_NUMBER").toString())) {
                managedUserRow.set("PHONE_NUMBER", ((Hashtable<K, Object>)properties).get("PHONE_NUMBER"));
            }
            managedUserObject.addRow(managedUserRow);
        }
        else {
            managedUserRow = managedUserObject.getFirstRow("ManagedUser");
            final String emailInRepo = managedUserRow.get("EMAIL_ADDRESS").toString();
            if (!MDMUtil.getInstance().isValidEmail(emailInRepo) && MDMUtil.getInstance().isValidEmail(emailID)) {
                managedUserRow.set("EMAIL_ADDRESS", (Object)emailID);
            }
            if (properties.containsKey("FIRST_NAME")) {
                managedUserRow.set("FIRST_NAME", ((Hashtable<K, Object>)properties).get("FIRST_NAME"));
            }
            if (properties.containsKey("MIDDLE_NAME")) {
                managedUserRow.set("MIDDLE_NAME", ((Hashtable<K, Object>)properties).get("MIDDLE_NAME"));
            }
            if (properties.containsKey("LAST_NAME")) {
                managedUserRow.set("LAST_NAME", ((Hashtable<K, Object>)properties).get("LAST_NAME"));
            }
            if (properties.containsKey("DISPLAY_NAME")) {
                managedUserRow.set("DISPLAY_NAME", ((Hashtable<K, Object>)properties).get("DISPLAY_NAME"));
            }
            if (properties.containsKey("PHONE_NUMBER") && MDMUtil.isStringValid(((Hashtable<K, Object>)properties).get("PHONE_NUMBER").toString())) {
                managedUserRow.set("PHONE_NUMBER", ((Hashtable<K, Object>)properties).get("PHONE_NUMBER"));
            }
            managedUserRow.set("STATUS", (Object)0);
            managedUserObject.updateRow(managedUserRow);
        }
        MDMUtil.getPersistence().update(managedUserObject);
        this.logger.log(Level.INFO, "Added Managed user ID : {0}", managedUserObject.getFirstRow("ManagedUser").get("MANAGED_USER_ID").toString());
        return managedUserObject;
    }
    
    private DataObject updateManagedUserEmail(final Long managedUserID, final Properties properties) throws DataAccessException {
        final String emailID = ((Hashtable<K, String>)properties).get("EMAIL_ADDRESS");
        final Criteria criteria = new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)managedUserID, 0);
        final DataObject managedUserObject = MDMUtil.getPersistence().get("ManagedUser", criteria);
        Row managedUserRow = null;
        if (!managedUserObject.isEmpty()) {
            managedUserRow = managedUserObject.getFirstRow("ManagedUser");
            managedUserRow.set("EMAIL_ADDRESS", (Object)emailID);
            if (properties.containsKey("FIRST_NAME")) {
                managedUserRow.set("FIRST_NAME", ((Hashtable<K, Object>)properties).get("FIRST_NAME"));
            }
            if (properties.containsKey("MIDDLE_NAME")) {
                managedUserRow.set("MIDDLE_NAME", ((Hashtable<K, Object>)properties).get("MIDDLE_NAME"));
            }
            if (properties.containsKey("LAST_NAME")) {
                managedUserRow.set("LAST_NAME", ((Hashtable<K, Object>)properties).get("LAST_NAME"));
            }
            if (properties.containsKey("DISPLAY_NAME")) {
                managedUserRow.set("DISPLAY_NAME", ((Hashtable<K, Object>)properties).get("DISPLAY_NAME"));
            }
            if (properties.containsKey("PHONE_NUMBER")) {
                String ph = ((Hashtable<K, String>)properties).get("PHONE_NUMBER");
                if (ph.equalsIgnoreCase("") || ph.equalsIgnoreCase("--")) {
                    ph = null;
                }
                managedUserRow.set("PHONE_NUMBER", (Object)ph);
            }
            managedUserObject.updateRow(managedUserRow);
        }
        MDMUtil.getPersistence().update(managedUserObject);
        return managedUserObject;
    }
    
    protected final SelectQuery getManagedUserQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        selectQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        return selectQuery;
    }
    
    public HashMap getManagedUserDetailsForRequest(final Long enrollmentRequestID) {
        try {
            final JSONObject json = new JSONObject();
            json.put("USER_IDENTIFIER", (Object)"ENROLLMENT_REQUEST_ID");
            json.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
            return JSONUtil.getInstance().ConvertToSameDataTypeHash(this.getManagedUserDetails(json));
        }
        catch (final JSONException ex) {
            Logger.getLogger(ManagedUserHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return new HashMap();
        }
    }
    
    private HashMap<Long, Long> getManagedUserERIDMapForRequests(final Long[] erids) {
        final HashMap<Long, Long> mUserEridMap = new HashMap<Long, Long>();
        try {
            final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            squery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            squery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"));
            squery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erids, 8));
            final DataObject DO = MDMUtil.getPersistence().get(squery);
            if (!DO.isEmpty()) {
                final Iterator<Row> iter = DO.getRows("DeviceEnrollmentRequest");
                while (iter.hasNext()) {
                    final Row row = iter.next();
                    mUserEridMap.put((Long)row.get("ENROLLMENT_REQUEST_ID"), (Long)row.get("MANAGED_USER_ID"));
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(ManagedUserHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mUserEridMap;
    }
    
    public void changeUser(final Long newUserId, final Long[] enrollReqIds) {
        this.changeUser(newUserId, enrollReqIds, true);
    }
    
    public void changeUser(final Long newUserId, final Long[] enrollReqIds, final boolean doNotSkipListeners) {
        this.changeUser(newUserId, enrollReqIds, doNotSkipListeners, null);
    }
    
    public void changeUser(final Long newUserId, final Long[] enrollReqIds, final boolean doNotSkipListeners, final Long technicianUserId) {
        try {
            this.assignUserLogger.log(Level.INFO, "Change User starts with newUserId:{0}, enrollReqIds:{1}, doNotSkipListeners:{2}, technicianUserId:{3}", new Object[] { newUserId, enrollReqIds.toString(), doNotSkipListeners, technicianUserId });
            final Long customerID = getInstance().getManagedUserDetails(newUserId).get("CUSTOMER_ID");
            final HashMap<Long, Long> oldEridUserMap = this.getManagedUserERIDMapForRequests(enrollReqIds);
            final Criteria enrollUserCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollReqIds, 8);
            final UpdateQuery enrollUserUpdateQuery = (UpdateQuery)new UpdateQueryImpl("DeviceEnrollmentRequest");
            enrollUserUpdateQuery.setCriteria(enrollUserCri);
            enrollUserUpdateQuery.setUpdateColumn("MANAGED_USER_ID", (Object)newUserId);
            MDMUtil.getPersistence().update(enrollUserUpdateQuery);
            final SelectQuery enrollDeviceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("EnrollmentRequestToDevice"));
            enrollDeviceQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"));
            enrollDeviceQuery.addSelectColumn(Column.getColumn("EnrollmentRequestToDevice", "MANAGED_DEVICE_ID"));
            enrollDeviceQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
            enrollDeviceQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"));
            final Join join = new Join("EnrollmentRequestToDevice", "ManagedUserToDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
            enrollDeviceQuery.addJoin(join);
            final Criteria enrollReqDeviceCri = new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)enrollReqIds, 8);
            enrollDeviceQuery.setCriteria(enrollReqDeviceCri);
            final DataObject dObj = MDMUtil.getPersistence().get(enrollDeviceQuery);
            if (!dObj.isEmpty()) {
                final Iterator itr = dObj.getRows("ManagedUserToDevice", enrollReqDeviceCri, join);
                Row row = null;
                while (itr.hasNext()) {
                    row = itr.next();
                    row.set("MANAGED_USER_ID", (Object)newUserId);
                    dObj.updateRow(row);
                }
                MDMUtil.getPersistence().update(dObj);
                for (final Long enrollmentId : enrollReqIds) {
                    final Long resId = ManagedDeviceHandler.getInstance().getManagedDeviceIDFromEnrollRequestID(enrollmentId);
                    final DeviceEvent deviceEvent = new DeviceEvent(resId, CustomerInfoUtil.getInstance().getCustomerIDForResID(resId));
                    deviceEvent.enrollmentRequestId = enrollmentId;
                    deviceEvent.platformType = MDMUtil.getInstance().getPlatformType(resId);
                    deviceEvent.udid = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resId);
                    deviceEvent.customerID = customerID;
                    if (deviceEvent.resourceJSON == null) {
                        deviceEvent.resourceJSON = new JSONObject();
                    }
                    deviceEvent.resourceJSON.put("oldUserId", (Object)oldEridUserMap.get(enrollmentId));
                    deviceEvent.resourceJSON.put("technicianUserId", (Object)technicianUserId);
                    if (doNotSkipListeners) {
                        this.assignUserLogger.log(Level.INFO, "Change user deviceListener invoked for {0}", deviceEvent.toString());
                        ManagedDeviceHandler.getInstance().invokeDeviceListeners(deviceEvent, 6);
                    }
                }
            }
        }
        catch (final Exception exp) {
            this.assignUserLogger.log(Level.WARNING, "Exception in changeUser", exp);
        }
    }
    
    public List getManagedUserListFromDB(final String domainName, final Criteria c) {
        List availableList = null;
        try {
            final SelectQuery sq = this.getManagedUserListQuery(domainName, c);
            sq.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            sq.addSelectColumn(Column.getColumn("Resource", "NAME"));
            sq.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
            sq.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
            availableList = new ArrayList();
            HashMap resourceDetailMap = null;
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(sq);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("Resource");
                while (iterator.hasNext()) {
                    final Row resourceRow = iterator.next();
                    resourceDetailMap = new HashMap();
                    final Long resourceId = (Long)resourceRow.get("RESOURCE_ID");
                    resourceDetailMap.put("NAME", resourceRow.get("NAME"));
                    resourceDetailMap.put("RESOURCE_ID", resourceId);
                    final Row managedUserRow = dataObject.getRow("ManagedUser", new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)resourceId, 0));
                    resourceDetailMap.put("EMAIL_ADDRESS", managedUserRow.get("EMAIL_ADDRESS"));
                    availableList.add(resourceDetailMap);
                }
            }
            this.logger.log(Level.INFO, "Available list obtained from db : ", availableList);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in getManagedUserListFromDB....", e);
        }
        return availableList;
    }
    
    public Properties getManagedUserDetailsForRequestAsProperties(final Long requestID) {
        final HashMap map = this.getManagedUserDetailsForRequest(requestID);
        final Properties properties = new Properties();
        properties.putAll(map);
        return properties;
    }
    
    public Boolean hasManagedUserInDomain(final Long dmDomainID) {
        try {
            final SelectQuery sq = this.getCanDeleteDomainSelectQuery(dmDomainID);
            final int count = DBUtil.getRecordCount(sq);
            if (count > 0) {
                return true;
            }
            return false;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in hasManagedUser....", e);
            return null;
        }
    }
    
    public SelectQuery getCanDeleteDomainSelectQuery(final Long dmDomainID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "DirResRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("DirResRel", "DMDomain", new String[] { "DM_DOMAIN_ID" }, new String[] { "DOMAIN_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        selectQuery.addJoin(new Join("ManagedUser", "DeviceEnrollmentToUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        selectQuery.addJoin(new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID").count());
        Criteria allCriteria;
        final Criteria typeCriteria = allCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Integer(2), 0);
        if (dmDomainID != null) {
            final Criteria domainCriteria = new Criteria(Column.getColumn("DMDomain", "DOMAIN_ID"), (Object)dmDomainID, 0);
            allCriteria = allCriteria.and(domainCriteria);
            final Criteria deviceEnrollToUserCri = new Criteria(Column.getColumn("DeviceEnrollmentToUser", "MANAGED_USER_ID"), (Object)null, 1);
            final Criteria deviceEnrollReqCri = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "MANAGED_USER_ID"), (Object)null, 1);
            final Criteria managedDevCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 1);
            final Criteria devStatusCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer[] { 7, 10, 11 }, 9);
            allCriteria = allCriteria.and(deviceEnrollReqCri.or(deviceEnrollToUserCri).or(managedDevCri)).and(devStatusCri);
            selectQuery.setCriteria(allCriteria);
            return selectQuery;
        }
        throw new Exception("invalid input : dmDomainID - " + String.valueOf(dmDomainID));
    }
    
    private SelectQuery getManagedUserListQuery(final String domainName, final Criteria c) {
        Criteria allCriteria;
        final Criteria typeCriteria = allCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Integer(2), 0);
        if (domainName != null) {
            final Criteria domainCriteria = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domainName, 0, false);
            allCriteria = allCriteria.and(domainCriteria);
        }
        if (c != null) {
            allCriteria = allCriteria.and(c);
        }
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        sq.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        sq.setCriteria(allCriteria);
        return sq;
    }
    
    private Long getManagedUserId(final String userName, final String domainName, final Long customerID) throws DataAccessException {
        final SelectQuery squery = this.getManagedUserQuery();
        squery.setCriteria(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domainName, 0, false).and(new Criteria(Column.getColumn("Resource", "NAME"), (Object)userName, 0, false)).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0)));
        squery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        final DataObject managedUserDO = MDMUtil.getPersistence().get(squery);
        if (!managedUserDO.isEmpty()) {
            return (Long)managedUserDO.getFirstValue("ManagedUser", "MANAGED_USER_ID");
        }
        return null;
    }
    
    public Long[] getAllUsersWhoCanBeCleaned(final Criteria c, final Boolean usersMappedToaRequest) throws Exception {
        final ArrayList<Long> removableUsers = new ArrayList<Long>();
        final SelectQuery squery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
        squery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        squery.addJoin(new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        squery.addJoin(new Join("ManagedUser", "AppleConfigRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "SHARED_USER_ID" }, 1));
        squery.addJoin(new Join("ManagedUser", "DeviceEnrollmentToUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        final Column requestCount = Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID").count();
        requestCount.setColumnAlias("ENROLL_REQ_COUNT");
        squery.addSelectColumn(requestCount);
        final Column acrequestCount = Column.getColumn("AppleConfigRequest", "APPLE_CONFIG_REQUEST_ID").count();
        acrequestCount.setColumnAlias("AC_REQ_COUNT");
        squery.addSelectColumn(acrequestCount);
        final Column userAssignmentCount = Column.getColumn("DeviceEnrollmentToUser", "MANAGED_USER_ID").count();
        userAssignmentCount.setColumnAlias("USER_ASSIGN_COUNT");
        squery.addSelectColumn(userAssignmentCount);
        squery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        final List<Column> groupByColumnList = new ArrayList<Column>();
        groupByColumnList.add(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        Criteria havingCriteria = new Criteria(acrequestCount, (Object)0, 0).and(new Criteria(userAssignmentCount, (Object)0, 0));
        if (usersMappedToaRequest) {
            havingCriteria = havingCriteria.and(new Criteria(requestCount, (Object)1, 6));
        }
        else {
            havingCriteria = havingCriteria.and(new Criteria(requestCount, (Object)0, 0));
        }
        final Criteria userInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 0);
        squery.setGroupByClause(new GroupByClause((List)groupByColumnList, havingCriteria));
        if (c != null) {
            squery.setCriteria(c.and(userInTrashCriteria));
        }
        else {
            squery.setCriteria(userInTrashCriteria);
        }
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)squery);
            while (ds.next()) {
                final int noOfAssociatedRequests = (int)ds.getValue("ENROLL_REQ_COUNT");
                final int noOfAssociatedAppleConfig = (int)ds.getValue("AC_REQ_COUNT");
                if (((usersMappedToaRequest && noOfAssociatedRequests == 1) || (!usersMappedToaRequest && noOfAssociatedRequests == 0)) && noOfAssociatedAppleConfig == 0) {
                    removableUsers.add((Long)ds.getValue("MANAGED_USER_ID"));
                }
            }
        }
        catch (final SQLException ex) {
            throw ex;
        }
        removableUsers.removeAll(new ManagedUserFacade().getListOfUsersWithProfilesAssociated(removableUsers));
        final Long[] removableUsersArr = new Long[removableUsers.size()];
        return removableUsers.toArray(removableUsersArr);
    }
    
    public Long[] getUsersWhoCanBeCleaned(final Long[] managedUserIDs, final Boolean usersMappedToaRequest) throws Exception {
        return this.getAllUsersWhoCanBeCleaned(new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)managedUserIDs, 8), usersMappedToaRequest);
    }
    
    public void removeUser(final Long[] managedUserIDs, final Long customerId, final Long technicianId) throws Exception {
        final Criteria cri = new Criteria(new Column("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)managedUserIDs, 8);
        final Criteria inTrashCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer[] { 11, 7, 10, 9 }, 8);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
        selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.setCriteria(cri);
        selectQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final ArrayList<Long> managedUserIdList = new ArrayList<Long>(Arrays.asList(managedUserIDs));
        final Set<Long> toTrashUserSet = new HashSet<Long>();
        final Iterator notInTrashIterator = dataObject.getRows("ManagedDevice", inTrashCriteria.negate());
        if (notInTrashIterator.hasNext()) {
            final JSONArray loggerJSONArray = new JSONArray();
            while (notInTrashIterator.hasNext()) {
                final Row managedDeviceRow = notInTrashIterator.next();
                final Long managedDeviceId = (Long)managedDeviceRow.get("RESOURCE_ID");
                final int managedStatus = (int)managedDeviceRow.get("MANAGED_STATUS");
                final Long managedUserId = (Long)dataObject.getValue("ManagedUserToDevice", "MANAGED_USER_ID", new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)managedDeviceId, 0));
                final JSONObject loggerJSON = new JSONObject();
                loggerJSON.put("MANAGED_DEVICE_ID", (Object)managedDeviceId);
                loggerJSON.put("MANAGED_USER_ID", (Object)managedUserId);
                loggerJSON.put("MANAGED_STATUS", managedStatus);
                loggerJSONArray.put((Object)loggerJSON);
            }
            this.logger.log(Level.INFO, "Following users have device linked to them with given status : {0}", loggerJSONArray.toString());
            throw new SyMException(52102, "User has device(s)", "mdm.enroll.remove_user_alert", (Throwable)null);
        }
        final HashMap userIdNameMap = (HashMap)this.getManagedUserMapForUserIds(managedUserIDs);
        final Iterator inTrashIterator = dataObject.getRows("ManagedDevice", inTrashCriteria);
        while (inTrashIterator.hasNext()) {
            final Row managedDeviceRow2 = inTrashIterator.next();
            final Long deviceId = (Long)managedDeviceRow2.get("RESOURCE_ID");
            final Iterator iterator = dataObject.getRows("ManagedUserToDevice", new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)deviceId, 0));
            while (iterator.hasNext()) {
                final Row managedUserToDeviceRow = iterator.next();
                final Long managedUserId2 = (Long)managedUserToDeviceRow.get("MANAGED_USER_ID");
                toTrashUserSet.add(managedUserId2);
                managedUserIdList.remove(managedUserId2);
            }
        }
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ManagedUser");
        updateQuery.setUpdateColumn("STATUS", (Object)11);
        updateQuery.setCriteria(new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)toTrashUserSet.toArray(), 8));
        MDMUtil.getPersistenceLite().update(updateQuery);
        final Iterator<Long> iterator3 = toTrashUserSet.iterator();
        while (iterator3.hasNext()) {
            final Long managedUserId = iterator3.next();
            final UserEvent userEvent = new UserEvent(managedUserId, customerId);
            getInstance().invokeUserListeners(userEvent, 4);
        }
        final String currentlyLoggedInUserName = DMUserHandler.getUserNameFromUserID(technicianId);
        final ArrayList trashUserNameList = new ArrayList();
        for (final Long trashUserId : toTrashUserSet) {
            trashUserNameList.add(userIdNameMap.get(trashUserId));
        }
        MDMEventLogHandler.getInstance().addEvent(72426, new ArrayList(toTrashUserSet), currentlyLoggedInUserName, "mdm.enroll.user_trashed", trashUserNameList, customerId, new Long(System.currentTimeMillis()));
        final Set<Long> toRemoveUserSet = new HashSet<Long>(managedUserIdList);
        for (final Long managedUserID : toRemoveUserSet) {
            final UserEvent userEvent2 = new UserEvent(managedUserID, customerId);
            getInstance().invokeUserListeners(userEvent2, 2);
        }
        final ArrayList deleteUserNameList = new ArrayList();
        for (final Long removeUserId : toRemoveUserSet) {
            deleteUserNameList.add(userIdNameMap.get(removeUserId));
        }
        String str = "Deleting managed users: ";
        final Criteria criteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)toRemoveUserSet.toArray(), 8).and(new Criteria(new Column("Resource", "RESOURCE_TYPE"), (Object)2, 0));
        final DataObject DO = DataAccess.get("Resource", criteria);
        if (!DO.isEmpty()) {
            final Iterator<Row> iterator2 = DO.getRows("Resource");
            while (iterator2.hasNext()) {
                final Row row = iterator2.next();
                str = str + row.get("NAME") + "[" + row.get("RESOURCE_ID") + "],";
            }
            this.logger.info(str);
        }
        MDMEventLogHandler.getInstance().addEvent(72426, new ArrayList(toRemoveUserSet), currentlyLoggedInUserName, "mdm.enroll.user_trashed", deleteUserNameList, customerId, new Long(System.currentTimeMillis()));
        DataAccess.delete("Resource", criteria);
    }
    
    public int getManagedUsersWithDevicesCount(final Long customerID) {
        int uniqueUser = 0;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("ManagedUser"));
            query.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            query.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addSelectColumn(new Column("ManagedUserToDevice", "MANAGED_USER_ID").distinct().count());
            final Criteria managedDevice = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)new Long(2L), 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            query.setCriteria(managedDevice.and(customerCriteria));
            uniqueUser = DBUtil.getRecordCount(query);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting unique user count", ex);
        }
        return uniqueUser;
    }
    
    public JSONObject getManagedUserDetailsForEmailAddress(final String emailAddress, final Long customerID) throws JSONException {
        if (CustomerInfoUtil.isSAS) {
            final JSONObject json = new JSONObject();
            json.put("EMAIL_ADDRESS", (Object)emailAddress);
            json.put("CUSTOMER_ID", (Object)customerID);
            json.put("USER_IDENTIFIER", (Object)"EMAIL_ADDRESS");
            final JSONObject userJSON = getInstance().getManagedUserDetails(json);
            return userJSON;
        }
        return new JSONObject();
    }
    
    public JSONObject getManagedUserDetails(final JSONObject json) {
        DMSecurityLogger.info(this.logger, ManagedUserHandler.class.getName(), "getManagedUserDetails", "Fetching user details for : {0}", (Object)json);
        final JSONObject managedUserInfo = new JSONObject();
        try {
            final SelectQuery selectQuery = this.getManagedUserQuery();
            Criteria criteria = null;
            if (!json.has("USER_IDENTIFIER")) {
                throw new SyMException(53000, "Managed User Identifier must be present", "dc.mdm.enroll.user_identifier_missing", (Throwable)null);
            }
            final String identifier = String.valueOf(json.get("USER_IDENTIFIER"));
            if (identifier.equalsIgnoreCase("MANAGED_USER_ID") && json.has("MANAGED_USER_ID")) {
                criteria = new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)JSONUtil.optLongForUVH(json, "MANAGED_USER_ID", (Long)null), 0);
            }
            else if (identifier.equalsIgnoreCase("NAME") && json.has("NAME") && json.has("DOMAIN_NETBIOS_NAME") && json.has("CUSTOMER_ID")) {
                criteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)String.valueOf(json.get("NAME")), 0, false).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)String.valueOf(json.get("DOMAIN_NETBIOS_NAME")), 0, false)).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)JSONUtil.optLongForUVH(json, "CUSTOMER_ID", (Long)null), 0));
            }
            else if (CustomerInfoUtil.isSAS && identifier.equalsIgnoreCase("EMAIL_ADDRESS") && json.has("EMAIL_ADDRESS") && json.has("CUSTOMER_ID")) {
                criteria = new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)String.valueOf(json.get("EMAIL_ADDRESS")), 0, false).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)JSONUtil.optLongForUVH(json, "CUSTOMER_ID", (Long)null), 0));
                if (json.has("DOMAIN_NETBIOS_NAME")) {
                    criteria = criteria.and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)String.valueOf(json.get("DOMAIN_NETBIOS_NAME")), 0, false));
                }
            }
            else if (identifier.equalsIgnoreCase("ENROLLMENT_REQUEST_ID") && json.has("ENROLLMENT_REQUEST_ID")) {
                selectQuery.addJoin(new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
                selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "*"));
                criteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)JSONUtil.optLongForUVH(json, "ENROLLMENT_REQUEST_ID", (Long)null), 0);
            }
            else if (identifier.equalsIgnoreCase("RESOURCE_ID") && json.has("RESOURCE_ID")) {
                selectQuery.addJoin(new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
                criteria = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)JSONUtil.optLongForUVH(json, "RESOURCE_ID", (Long)null), 0);
            }
            else if (identifier.equalsIgnoreCase("UDID") && json.has("UDID")) {
                selectQuery.addJoin(new Join("ManagedUser", "DeviceEnrollmentRequest", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
                selectQuery.addJoin(new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
                selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                selectQuery.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "*"));
                criteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)String.valueOf(json.get("UDID")), 0, false);
            }
            else {
                if (!identifier.equalsIgnoreCase("PHONE_NUMBER") || !json.has("PHONE_NUMBER")) {
                    throw new SyMException(53000, "Managed User Identifier must be present", "dc.mdm.enroll.user_identifier_missing", (Throwable)null);
                }
                criteria = new Criteria(Column.getColumn("ManagedUser", "PHONE_NUMBER"), (Object)String.valueOf(json.get("PHONE_NUMBER")), 0).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)JSONUtil.optLongForUVH(json, "CUSTOMER_ID", (Long)null), 0));
            }
            final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
            selectQuery.setCriteria(criteria.and(userNotInTrashCriteria));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "*"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "*"));
            DMSecurityLogger.info(this.logger, ManagedUserHandler.class.getName(), "getManagedUserDetails", "Fetching user details with query : {0}", (Object)RelationalAPI.getInstance().getSelectSQL((Query)selectQuery));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                managedUserInfo.put("NAME", dataObject.getValue("Resource", "NAME", (Criteria)null));
                managedUserInfo.put("DOMAIN_NETBIOS_NAME", dataObject.getValue("Resource", "DOMAIN_NETBIOS_NAME", (Criteria)null));
                managedUserInfo.put("CUSTOMER_ID", dataObject.getValue("Resource", "CUSTOMER_ID", (Criteria)null));
                managedUserInfo.put("EMAIL_ADDRESS", dataObject.getValue("ManagedUser", "EMAIL_ADDRESS", (Criteria)null));
                managedUserInfo.put("MANAGED_USER_ID", dataObject.getValue("ManagedUser", "MANAGED_USER_ID", (Criteria)null));
                managedUserInfo.put("FIRST_NAME", dataObject.getValue("ManagedUser", "FIRST_NAME", (Criteria)null));
                managedUserInfo.put("MIDDLE_NAME", dataObject.getValue("ManagedUser", "MIDDLE_NAME", (Criteria)null));
                managedUserInfo.put("LAST_NAME", dataObject.getValue("ManagedUser", "LAST_NAME", (Criteria)null));
                managedUserInfo.put("DISPLAY_NAME", dataObject.getValue("ManagedUser", "DISPLAY_NAME", (Criteria)null));
                managedUserInfo.put("PHONE_NUMBER", dataObject.getValue("ManagedUser", "PHONE_NUMBER", (Criteria)null));
                if (dataObject.containsTable("DeviceEnrollmentRequest")) {
                    final Iterator<Row> enrollIterator = dataObject.getRows("DeviceEnrollmentRequest");
                    if (enrollIterator.hasNext()) {
                        final Row deviceEnrollmentRequestRow = enrollIterator.next();
                        managedUserInfo.put("ENROLLMENT_REQUEST_ID", deviceEnrollmentRequestRow.get("ENROLLMENT_REQUEST_ID"));
                        managedUserInfo.put("PLATFORM_TYPE", deviceEnrollmentRequestRow.get("PLATFORM_TYPE"));
                    }
                }
            }
        }
        catch (final SyMException ex) {
            this.logger.log(Level.SEVERE, "Exception in getManagedUserDetails - {0}", ex.getMessage());
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in getManagedUserDetails", ex2);
        }
        DMSecurityLogger.info(this.logger, ManagedUserHandler.class.getName(), "getManagedUserDetails", "Fetched user details : {0}", (Object)managedUserInfo);
        return managedUserInfo;
    }
    
    public HashMap getManagedUserDetails(final Long managedUserID) {
        try {
            final JSONObject json = new JSONObject();
            json.put("USER_IDENTIFIER", (Object)"MANAGED_USER_ID");
            json.put("MANAGED_USER_ID", (Object)managedUserID);
            return JSONUtil.getInstance().ConvertToSameDataTypeHash(this.getManagedUserDetails(json));
        }
        catch (final JSONException ex) {
            Logger.getLogger(ManagedUserHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return new HashMap();
        }
    }
    
    public HashMap getManagedUserDetailsForDevice(final String deviceUDID) {
        try {
            final JSONObject json = new JSONObject();
            json.put("USER_IDENTIFIER", (Object)"UDID");
            json.put("UDID", (Object)deviceUDID);
            return JSONUtil.getInstance().ConvertToSameDataTypeHash(this.getManagedUserDetails(json));
        }
        catch (final JSONException ex) {
            Logger.getLogger(ManagedUserHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return new HashMap();
        }
    }
    
    public HashMap getManagedUserDetailsForDevice(final Long resourceID) {
        try {
            final JSONObject json = new JSONObject();
            json.put("USER_IDENTIFIER", (Object)"RESOURCE_ID");
            json.put("RESOURCE_ID", (Object)resourceID);
            return JSONUtil.getInstance().ConvertToSameDataTypeHash(this.getManagedUserDetails(json));
        }
        catch (final JSONException ex) {
            Logger.getLogger(ManagedUserHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return new HashMap();
        }
    }
    
    public HashMap getManagedUserDetailsForUserName(final String userName, final String domainName, final Long customerID) {
        try {
            final JSONObject json = new JSONObject();
            json.put("NAME", (Object)userName);
            json.put("DOMAIN_NETBIOS_NAME", (Object)domainName);
            json.put("CUSTOMER_ID", (Object)customerID);
            json.put("USER_IDENTIFIER", (Object)"NAME");
            this.getManagedUserDetails(json);
            return JSONUtil.getInstance().ConvertToSameDataTypeHash(this.getManagedUserDetails(json));
        }
        catch (final JSONException ex) {
            Logger.getLogger(ManagedUserHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return new HashMap();
        }
    }
    
    private SelectQuery getManagedUserQueryForSearch(final String tableName, final String columnName, final Long customerID, final String value) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("ManagedUser"));
        query.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("ManagedUser", "DirResRel", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 1));
        query.addSelectColumn(Column.getColumn("Resource", "NAME"));
        query.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "DISPLAY_NAME"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "PHONE_NUMBER"));
        Criteria criteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)new String[] { "", "-", "--", "---" }, 9, false).and(new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)null, 0));
        if (customerID != null) {
            criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
        }
        if (!MDMStringUtils.isEmpty(value)) {
            Criteria valCri;
            if (value.contains("%")) {
                valCri = new Criteria(Column.getColumn(tableName, columnName), (Object)value.replace("%", "*"), 2, false);
            }
            else {
                valCri = new Criteria(Column.getColumn(tableName, columnName), (Object)value, 10, false);
            }
            criteria = criteria.and(valCri);
        }
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        query.setCriteria(criteria.and(new Criteria(Column.getColumn("Resource", "NAME"), (Object)"--", 3)).and(userNotInTrashCriteria));
        query.addSortColumn(new SortColumn(Column.getColumn("Resource", "NAME"), true));
        query.setRange(new Range(0, 5));
        return query;
    }
    
    public List getUserListForSearch(final Long customerID, final String searchValue, final int range) {
        final List<Properties> users = new LinkedList<Properties>();
        if (range > 0) {
            try {
                final SelectQuery selectQuery = this.getManagedUserQueryForSearch("Resource", "NAME", customerID, searchValue);
                UnionQuery query = (UnionQuery)new UnionQueryImpl((Query)selectQuery, (Query)this.getManagedUserQueryForSearch("ManagedUser", "FIRST_NAME", customerID, searchValue), true);
                query = (UnionQuery)new UnionQueryImpl((Query)query, (Query)this.getManagedUserQueryForSearch("ManagedUser", "EMAIL_ADDRESS", customerID, searchValue), true);
                query = (UnionQuery)new UnionQueryImpl((Query)query, (Query)this.getManagedUserQueryForSearch("ManagedUser", "LAST_NAME", customerID, searchValue), true);
                query = (UnionQuery)new UnionQueryImpl((Query)query, (Query)this.getManagedUserQueryForSearch("ManagedUser", "PHONE_NUMBER", customerID, searchValue), true);
                query = (UnionQuery)new UnionQueryImpl((Query)query, (Query)this.getManagedUserQueryForSearch("ManagedUser", "MIDDLE_NAME", customerID, searchValue), true);
                DataSet ds = null;
                Connection c = null;
                final Set<Long> adUsersSet = new HashSet<Long>(range);
                try {
                    c = RelationalAPI.getInstance().getConnection();
                    ds = RelationalAPI.getInstance().executeQuery((Query)query, c);
                    while (ds.next()) {
                        if (!adUsersSet.contains(ds.getValue("MANAGED_USER_ID"))) {
                            final Properties dataProperty = new Properties();
                            ((Hashtable<String, Object>)dataProperty).put("dataId", ds.getValue("MANAGED_USER_ID"));
                            adUsersSet.add((Long)ds.getValue("MANAGED_USER_ID"));
                            final JSONObject json = new JSONObject();
                            json.put("NAME", (Object)ds.getValue("NAME"));
                            json.put("EMAIL_ADDRESS", (Object)ds.getValue("EMAIL_ADDRESS"));
                            json.put("DOMAIN_NETBIOS_NAME", (Object)ds.getValue("DOMAIN_NETBIOS_NAME"));
                            json.put("DISPLAY_NAME", (Object)ds.getValue("DISPLAY_NAME"));
                            json.put("PHONE_NUMBER", (Object)ds.getValue("PHONE_NUMBER"));
                            ((Hashtable<String, String>)dataProperty).put("dataValue", json.toString());
                            users.add(dataProperty);
                        }
                        if (adUsersSet.size() == range) {
                            break;
                        }
                    }
                }
                finally {
                    CustomGroupUtil.getInstance().closeConnection(c, ds);
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, null, ex);
            }
        }
        return users;
    }
    
    public JSONObject checkIfValidDomainUserName(final Long customerID, final JSONObject json) throws Exception {
        final String userName = URLDecoder.decode(String.valueOf(json.get("NAME")), "UTF-8");
        final String domainName = URLDecoder.decode(String.valueOf(json.get("DOMAIN_NETBIOS_NAME")), "UTF-8");
        final Long dmDomainID = ((Hashtable<K, Long>)DMDomainDataHandler.getInstance().getDomainProps(domainName, customerID)).get("DOMAIN_ID");
        final Criteria dmDomainCri = new Criteria(Column.getColumn("DirObjRegStrVal", "DM_DOMAIN_ID"), (Object)dmDomainID, 0);
        final Criteria resNameCri = new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)2L, 0, false).and(new Criteria(Column.getColumn("DirObjRegStrVal", "VALUE"), (Object)userName, 0, false));
        final Properties dirObjProps = DirectoryUtil.getInstance().getObjectAttributes(dmDomainCri.and(resNameCri));
        if (dirObjProps != null && !dirObjProps.isEmpty()) {
            json.put("NAME", (Object)dirObjProps.getProperty(DirectoryAttributeConstants.getAttrKey(Long.valueOf(2L))));
            json.put("EMAIL_ADDRESS", (Object)dirObjProps.getProperty("mail"));
            json.put("RESOURCE_ID", (Object)Long.valueOf(String.valueOf(((Hashtable<K, Object>)dirObjProps).get("RESOURCE_ID"))));
            return json;
        }
        throw new SyMException(12001, I18N.getMsg("dc.mdm.enroll.invalid_user_name", (Object[])new String[] { userName, domainName }), "dc.mdm.enroll.invalid_user_name", (Throwable)null);
    }
    
    public int getNoOfRequests(final Long muid, final int platformType) throws Exception {
        int count = 0;
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
        sq.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        sq.setCriteria(new Criteria(new Column("DeviceEnrollmentRequest", "MANAGED_USER_ID"), (Object)muid, 0));
        sq.setCriteria(new Criteria(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"), (Object)platformType, 0));
        sq.setCriteria(new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)2, 1));
        sq.setCriteria(new Criteria(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"), (Object)2, 1));
        DMDataSetWrapper ds = null;
        try {
            final Column requestCount = Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID").count();
            requestCount.setColumnAlias("ENROLL_REQ_COUNT");
            requestCount.setDataType("INTEGER");
            sq.addSelectColumn(requestCount);
            ds = DMDataSetWrapper.executeQuery((Object)sq);
            if (ds.next()) {
                count = (int)ds.getValue("ENROLL_REQ_COUNT");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Error in getNoOfRequests()   ", e);
        }
        return count;
    }
    
    public HashMap getPlatformBasedDeviceIdsForManagedUser(final List userList) {
        final HashMap map = new HashMap();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)userList.toArray(), 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
            selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            selectQuery.setDistinct(true);
            final Set ios = new HashSet();
            final Set android = new HashSet();
            final Set windows = new HashSet();
            final Set chrome = new HashSet();
            DMDataSetWrapper ds = null;
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final int platform = (int)ds.getValue("PLATFORM_TYPE");
                final Long resourceId = (Long)ds.getValue("MANAGED_DEVICE_ID");
                switch (platform) {
                    case 1: {
                        ios.add(resourceId);
                        continue;
                    }
                    case 2: {
                        android.add(resourceId);
                        continue;
                    }
                    case 3: {
                        windows.add(resourceId);
                        continue;
                    }
                    case 4: {
                        chrome.add(resourceId);
                        continue;
                    }
                }
            }
            map.put(1, ios);
            map.put(2, android);
            map.put(3, windows);
            map.put(4, chrome);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getPlatformBasedMemberIdForManagedUser", ex);
        }
        return map;
    }
    
    public List<Long> getManagedUserIDsForDeviceIDs(final List<Long> deviceIDs) {
        final List<Long> managedUserIDs = new ArrayList<Long>();
        final Set<Long> managedUserIdSet = new HashSet<Long>();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
            sq.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceIDs.toArray(), 8);
            sq.setCriteria(criteria);
            sq.addSelectColumn(Column.getColumn("ManagedUserToDevice", "*"));
            final DataObject dataObj = MDMUtil.getPersistence().get(sq);
            if (!dataObj.isEmpty()) {
                final Iterator<Row> it = dataObj.getRows("ManagedUserToDevice");
                Row row = null;
                while (it.hasNext()) {
                    row = it.next();
                    managedUserIdSet.add((Long)row.get("MANAGED_USER_ID"));
                }
                managedUserIDs.addAll(managedUserIdSet);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getResourceIDsForUDIDs.. ", ex);
        }
        return managedUserIDs;
    }
    
    public List<Long> getManagedDevicesListForManagedUsers(final List userList, final int deviceStatus) {
        final List<Long> list = new ArrayList<Long>();
        try {
            Criteria criteria = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)userList.toArray(), 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
            selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            if (deviceStatus != 0) {
                criteria = criteria.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)deviceStatus, 0));
            }
            selectQuery.setCriteria(criteria);
            Column managedDeviceColumn = Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID");
            managedDeviceColumn = managedDeviceColumn.distinct();
            managedDeviceColumn.setColumnAlias("MANAGED_DEVICE_ID");
            selectQuery.addSelectColumn(managedDeviceColumn);
            final org.json.simple.JSONArray deviceJSONArray = MDMUtil.executeSelectQuery(selectQuery);
            for (int i = 0; i < deviceJSONArray.size(); ++i) {
                final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)deviceJSONArray.get(i);
                list.add((Long)tempJSON.get((Object)"MANAGED_DEVICE_ID"));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getPlatformBasedMemberIdForManagedUser", ex);
        }
        return list;
    }
    
    public List<Long> getManagedDevicesListForManagedUsers(final List userList) {
        List<Long> list = new ArrayList<Long>();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)userList.toArray(), 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
            selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"));
            selectQuery.setDistinct(true);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                list = DBUtil.getColumnValuesAsList(dataObject.getRows("ManagedUserToDevice"), "MANAGED_DEVICE_ID");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getPlatformBasedMemberIdForManagedUser", ex);
        }
        return list;
    }
    
    public List<Long> getRBDAManagedDevicesListForManagedUsers(final List userList, final int deviceStatus) {
        final List<Long> list = new ArrayList<Long>();
        try {
            Criteria criteria = new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_USER_ID"), (Object)userList.toArray(), 8);
            SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUserToDevice"));
            selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            if (deviceStatus != 0) {
                criteria = criteria.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)deviceStatus, 0));
            }
            selectQuery.setCriteria(criteria);
            Column managedDeviceColumn = Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID");
            managedDeviceColumn = managedDeviceColumn.distinct();
            managedDeviceColumn.setColumnAlias("MANAGED_DEVICE_ID");
            selectQuery.addSelectColumn(managedDeviceColumn);
            final org.json.simple.JSONArray deviceJSONArray = MDMUtil.executeSelectQuery(selectQuery);
            for (int i = 0; i < deviceJSONArray.size(); ++i) {
                final org.json.simple.JSONObject tempJSON = (org.json.simple.JSONObject)deviceJSONArray.get(i);
                list.add((Long)tempJSON.get((Object)"MANAGED_DEVICE_ID"));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getPlatformBasedMemberIdForManagedUser", ex);
        }
        return list;
    }
    
    public JSONObject saveMangedUserDetails(final Properties properties, final boolean editAllowed) throws Exception {
        throw new UnsupportedOperationException("Method not supported");
    }
    
    public boolean isChangeUserAllowed(final int enrollmentType, final String deviceToken) throws Exception {
        return true;
    }
    
    public void deleteEmailOnUserDelete(final List<String> email) {
        try {
            final List<Long> erids = new ArrayList<Long>();
            final Long currentlyLoggedInManagedUserUserId = this.getManagedUserIdForCurrentlyloggedInUser();
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
            sq.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
            final Criteria userEmailCriteria = new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)email.toArray(), 8);
            sq.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            sq.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
            sq.setCriteria(userEmailCriteria);
            final DataObject DO = MDMUtil.getPersistenceLite().get(sq);
            if (!DO.isEmpty()) {
                final Iterator it = DO.getRows("DeviceEnrollmentRequest");
                while (it.hasNext()) {
                    final Row row = it.next();
                    final Object value = row.get("ENROLLMENT_REQUEST_ID");
                    if (value != null) {
                        erids.add((Long)value);
                    }
                }
                final Long[] eridArray = erids.toArray(new Long[erids.size()]);
                this.changeUser(currentlyLoggedInManagedUserUserId, eridArray);
            }
        }
        catch (final Exception exp) {
            this.assignUserLogger.log(Level.SEVERE, "Exception in deleteEmailOnUserDelete", exp);
        }
    }
    
    public Long getManagedUserIdForCurrentlyloggedInUser() throws Exception {
        String currentlyLoggedInUserEmail = MDMUtil.getInstance().getCurrentlyLoggedInUserEmail();
        if (currentlyLoggedInUserEmail == null) {
            currentlyLoggedInUserEmail = "";
        }
        Long currentlyLoggedInManagedUserUserId = (Long)DBUtil.getValueFromDB("ManagedUser", "EMAIL_ADDRESS", (Object)currentlyLoggedInUserEmail, "MANAGED_USER_ID");
        if (currentlyLoggedInManagedUserUserId == null) {
            final HashMap hash = MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo();
            final Properties props = new Properties();
            ((Hashtable<String, String>)props).put("EMAIL_ADDRESS", currentlyLoggedInUserEmail);
            ((Hashtable<String, String>)props).put("NAME", hash.get("UserName"));
            ((Hashtable<String, String>)props).put("DISPLAY_NAME", hash.get("UserName"));
            ((Hashtable<String, String>)props).put("FIRST_NAME", hash.get("UserName"));
            String domainname = hash.get("DomainName");
            domainname = (domainname.equalsIgnoreCase("-") ? "MDM" : domainname);
            ((Hashtable<String, String>)props).put("DOMAIN_NETBIOS_NAME", domainname);
            ((Hashtable<String, Long>)props).put("CUSTOMER_ID", CustomerInfoUtil.getInstance().getCustomerId());
            currentlyLoggedInManagedUserUserId = this.addOrUpdateAndGetUserId(props);
        }
        return currentlyLoggedInManagedUserUserId;
    }
    
    public void deletePIIInfo(final ArrayList<Long> deletedUsers) throws DataAccessException {
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ManagedUser");
        updateQuery.setCriteria(new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)deletedUsers.toArray(), 8));
        updateQuery.setUpdateColumn("DISPLAY_NAME", (Object)"--");
        updateQuery.setUpdateColumn("EMAIL_ADDRESS", (Object)"--");
        updateQuery.setUpdateColumn("FIRST_NAME", (Object)"--");
        updateQuery.setUpdateColumn("LAST_NAME", (Object)"--");
        updateQuery.setUpdateColumn("MIDDLE_NAME", (Object)"--");
        updateQuery.setUpdateColumn("DISPLAY_NAME", (Object)"--");
        updateQuery.setUpdateColumn("PHONE_NUMBER", (Object)"--");
        DataAccess.update(updateQuery);
    }
    
    public void handleUserDeletion(final ArrayList<Long> deletedUsers, final Long customerID, final Long userID, final String syncIntiatedByUsername) {
        try {
            new ManagedUserFacade().disassociateAllAppsAndProfilesFromUsers(deletedUsers, customerID, userID);
            final String erids = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestIdFromManagedUserIDs(deletedUsers.toArray(new Long[deletedUsers.size()]));
            MDMEnrollmentUtil.getInstance().removeDevice(erids, syncIntiatedByUsername, customerID);
            getInstance().deletePIIInfo(deletedUsers);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "exception in removing users handling", ex);
        }
    }
    
    public List getUserList(final List resourceList) {
        if (resourceList != null && resourceList.size() != 0) {
            List userList = null;
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            selectQuery.addJoin(new Join("Resource", "MDMResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0).and(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8)));
            try {
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                final Iterator itr = dataObject.getRows("Resource");
                while (itr.hasNext()) {
                    if (userList == null) {
                        userList = new ArrayList();
                    }
                    final Row row = itr.next();
                    userList.add(Long.valueOf(String.valueOf(row.get("RESOURCE_ID"))));
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception occurred...", e);
            }
            return userList;
        }
        return null;
    }
    
    public void modifyUser(final JSONObject jsonObject) throws Exception {
        final String emailAddr = String.valueOf(jsonObject.get("EMAIL_ADDRESS"));
        String phoneNum = String.valueOf(jsonObject.get("PHONE_NUMBER"));
        phoneNum = phoneNum.trim().replaceAll(" ", "");
        final Long muid = jsonObject.getLong("MANAGED_USER_ID");
        final Long customerID = jsonObject.getLong("CUSTOMER_ID");
        final String userName = jsonObject.getString("USER_NAME");
        final HashMap map = getInstance().getManagedUserDetails(muid);
        if (map.isEmpty()) {
            throw new SyMException(12001, "User not present!!", "mdm.enroll.user_not_present", (Throwable)null);
        }
        final String emailFromDb = map.get("EMAIL_ADDRESS");
        final String phoneNumFromDb = map.get("PHONE_NUMBER");
        final String userNameFromDb = map.get("NAME");
        final String domain = map.get("DOMAIN_NETBIOS_NAME");
        jsonObject.put("EMAIL_DB", (Object)emailFromDb);
        jsonObject.put("PHONE_DB", (Object)phoneNumFromDb);
        jsonObject.put("USERNAME_DB", (Object)userNameFromDb);
        jsonObject.put("DOMAIN_NETBIOS_NAME", (Object)domain);
        final boolean phoneOrEmailUpdated = this.validateModifyUserInput(jsonObject);
        final boolean userNameUpdated = this.validateModifyUserNameInput(jsonObject, customerID);
        if (phoneOrEmailUpdated) {
            final JSONObject updateDetails = new JSONObject();
            updateDetails.put("MANAGED_USER_ID", (Object)muid);
            if (!emailAddr.equalsIgnoreCase("--")) {
                updateDetails.put("EMAIL_ADDRESS", (Object)emailAddr);
            }
            else {
                updateDetails.put("EMAIL_ADDRESS", (Object)emailFromDb);
            }
            updateDetails.put("PHONE_NUMBER", (Object)phoneNum);
            updateDetails.put("DOMAIN_NETBIOS_NAME", map.get("DOMAIN_NETBIOS_NAME"));
            updateDetails.put("UPDATE_BLINDLY", true);
            this.updateEmailAddressForManagedUser(updateDetails, customerID);
        }
        if (userNameUpdated) {
            final JSONObject updateDetails = new JSONObject();
            updateDetails.put("USER_IDENTIFIER", (Object)"MANAGED_USER_ID");
            updateDetails.put("MANAGED_USER_ID", (Object)muid);
            updateDetails.put("DOMAIN_NETBIOS_NAME", map.get("DOMAIN_NETBIOS_NAME"));
            updateDetails.put("NAME", (Object)userName);
            updateDetails.put("UPDATE_BLINDLY", true);
            this.updateUserNameForManagedUser(updateDetails, customerID);
        }
    }
    
    public boolean validateModifyUserInput(final JSONObject jsonObject) throws Exception {
        boolean mailOrPhoneUpdated = false;
        final String emailAddr = String.valueOf(jsonObject.get("EMAIL_ADDRESS"));
        final String domainName = jsonObject.optString("DOMAIN_NETBIOS_NAME", "MDM");
        jsonObject.put("email", (Object)emailAddr);
        final String phoneNum = String.valueOf(jsonObject.get("PHONE_NUMBER"));
        final String emailAddrFromDb = String.valueOf(jsonObject.get("EMAIL_DB"));
        final String phoneNumFromDb = jsonObject.optString("PHONE_DB", "--");
        final Long muid = jsonObject.getLong("MANAGED_USER_ID");
        if (phoneNum.equalsIgnoreCase("--") && emailAddr.equalsIgnoreCase("--")) {
            throw new SyMException(51021, "Both email and phone number are empty!!", "mdm.enroll.empty_phone_email", (Throwable)null);
        }
        if (!emailAddr.equalsIgnoreCase("--")) {
            if (!MDMUtil.getInstance().isValidEmail(emailAddr)) {
                throw new SyMException(51022, "Email address is invalid!!", "dc.mdm.enroll.invalid_email", (Throwable)null);
            }
            if (!emailAddrFromDb.equalsIgnoreCase(emailAddr)) {
                mailOrPhoneUpdated = true;
            }
        }
        if (!phoneNum.equalsIgnoreCase("--")) {
            if (!MDMEnrollmentUtil.isValidPhone(phoneNum) && !phoneNum.trim().equalsIgnoreCase("")) {
                throw new SyMException(51023, "Phone number is invalid!!", "dc.mdm.inv.valid_phone_number", (Throwable)null);
            }
            if (!phoneNum.trim().equalsIgnoreCase("") && !phoneNumFromDb.equalsIgnoreCase(phoneNum)) {
                mailOrPhoneUpdated = true;
            }
        }
        if (!phoneNumFromDb.equalsIgnoreCase("--") && phoneNum.equalsIgnoreCase("")) {
            mailOrPhoneUpdated = true;
        }
        if (mailOrPhoneUpdated) {
            this.checkIfEmailAddressPhoneNumberAlreadyExist(emailAddr, phoneNum, muid, domainName);
        }
        return mailOrPhoneUpdated;
    }
    
    public boolean validateModifyUserNameInput(final JSONObject jsonObject, final Long customerID) throws Exception {
        final String userNameFromDB = jsonObject.getString("USERNAME_DB");
        final String userNameUpdate = jsonObject.getString("USER_NAME");
        final String domainName = jsonObject.getString("DOMAIN_NETBIOS_NAME");
        if (!userNameFromDB.equals(userNameUpdate)) {
            this.validateUserName(userNameUpdate, domainName, customerID);
            return true;
        }
        return false;
    }
    
    public void validateUserName(final String username, final String domainName, final Long customerID) throws Exception {
        final HashMap map = getInstance().getManagedUserDetailsForUserName(username, domainName, customerID);
        if (!map.isEmpty() && map.containsKey("MANAGED_USER_ID")) {
            throw new SyMException(52103, "A user already exists for given email address", I18N.getMsg("dc.mdm.enroll.error.change_user_email_exists", new Object[] { ProductUrlLoader.getInstance().getValue("mdmUrl") }), (Throwable)null);
        }
        if (!this.isValidStr(username)) {
            throw new SyMException(51014, "Enter a valid user name", "dc.mdm.enroll.valid_user", (Throwable)null);
        }
    }
    
    private boolean isValidStr(final String username) {
        final Pattern pattern = Pattern.compile("^[^*\"{};]+$");
        final Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
    
    public void checkIfEmailAddressPhoneNumberAlreadyExist(final String emailAddress, final String phoneNum, final Long muid, final String domainName) throws Exception {
    }
    
    public Map getManagedUserMapForUserIds(final Long[] userIds) {
        final Map<Long, String> managedUserDetailsMap = new HashMap<Long, String>();
        try {
            final Criteria resIdCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)userIds, 8);
            final DataObject dataObject = MDMUtil.getPersistence().get("Resource", resIdCriteria);
            final Iterator<Row> managedUserRows = dataObject.getRows("Resource");
            while (managedUserRows.hasNext()) {
                final Row managedUser = managedUserRows.next();
                managedUserDetailsMap.put((Long)managedUser.get("RESOURCE_ID"), (String)managedUser.get("NAME"));
            }
            this.logger.log(Level.INFO, "managedUserDetailsMap obtained from db : {0}", managedUserDetailsMap);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getManagedUserMapForUserIds : ", ex);
        }
        return managedUserDetailsMap;
    }
    
    public List getInvalidManagedUserIds(final Long[] muidArray) throws Exception {
        final List<Long> invalidList = new ArrayList<Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)muidArray, 8).and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 1)));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator itr = dataObject.getRows("Resource");
            while (itr.hasNext()) {
                final Row row = itr.next();
                invalidList.add((Long)row.get("RESOURCE_ID"));
            }
        }
        return invalidList;
    }
    
    public JSONObject getManagedUserIdAndAAAUserIdForAdmin(final Long customerId, final Boolean allowEmptyMailAddress) throws Exception {
        Long managedUserId = -1L;
        Long loggenInuserId = -1L;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaUser"));
        selectQuery.addJoin(new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("AaaUser", "AaaUserContactInfo", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addJoin(new Join("AaaUserContactInfo", "AaaContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
        selectQuery.addJoin(new Join("AaaLogin", "UsersRoleMapping", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        selectQuery.addJoin(new Join("UsersRoleMapping", "UMRole", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("UMRole", "UM_ROLE_NAME"), (Object)"Administrator", 0));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUser", "USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaLogin", "LOGIN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserContactInfo", "USER_ID", "AAAUSERCONTACTINFO.USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaUserContactInfo", "CONTACTINFO_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "CONTACTINFO_ID", "AAACONTACTINFO.CONTACTINFO_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "EMAILID"));
        selectQuery.addSelectColumn(Column.getColumn("UsersRoleMapping", "LOGIN_ID", "USERSROLEMAPPING.LOGIN_ID"));
        selectQuery.addSelectColumn(Column.getColumn("UsersRoleMapping", "UM_ROLE_ID", "USERSROLEMAPPING.UM_ROLE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("UMRole", "UM_ROLE_ID"));
        DMDataSetWrapper ds = null;
        ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final JSONObject properties = new JSONObject();
        while (ds.next()) {
            final String email = (String)ds.getValue("EMAILID");
            if (!MDMStringUtils.isEmpty(email) || allowEmptyMailAddress) {
                properties.put("NAME", (Object)ds.getValue("FIRST_NAME"));
                loggenInuserId = (Long)ds.getValue("USER_ID");
                properties.put("EMAIL_ADDRESS", (Object)email);
                break;
            }
        }
        if (properties.length() != 0 && customerId != -1L) {
            properties.put("DOMAIN_NETBIOS_NAME", (Object)"MDM");
            properties.put("CUSTOMER_ID", (Object)customerId);
            managedUserId = this.addOrUpdateAndGetUserId(properties);
        }
        final JSONObject jsonObject = new JSONObject();
        if (loggenInuserId != -1L && managedUserId != -1L) {
            jsonObject.put("MANAGED_USER_ID", (Object)managedUserId);
            jsonObject.put("USER_ID", (Object)loggenInuserId);
            jsonObject.put("CUSTOMER_ID", (Object)customerId);
        }
        return jsonObject;
    }
    
    private static SelectQuery constructManagedUserToDevicesQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        final Join managedUserToDeviceJoin = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join managedUserJoin = new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
        selectQuery.addJoin(managedUserToDeviceJoin);
        selectQuery.addJoin(managedUserJoin);
        selectQuery.addSelectColumn(new Column("ManagedUser", "*"));
        selectQuery.addSelectColumn(new Column("ManagedUserToDevice", "*"));
        return selectQuery;
    }
    
    public static Map<Long, Map<String, String>> getManagedUserDetailsForManagedDevices(final List<Long> devicesList) throws DataAccessException {
        final HashMap<Long, Map<String, String>> userDetailsMap = new HashMap<Long, Map<String, String>>();
        final SelectQuery managedUserToDevicesQuery = constructManagedUserToDevicesQuery();
        final Criteria devicesCriteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)devicesList.toArray(), 8);
        managedUserToDevicesQuery.setCriteria(devicesCriteria);
        final DataObject dataObject = SyMUtil.getPersistence().get(managedUserToDevicesQuery);
        if (!dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("ManagedUserToDevice");
            while (iterator.hasNext()) {
                final Row userToDeviceRow = iterator.next();
                final Long deviceID = (Long)userToDeviceRow.get("MANAGED_DEVICE_ID");
                final Long userID = (Long)userToDeviceRow.get("MANAGED_USER_ID");
                final Criteria managedUserIdCriteria = new Criteria(new Column("ManagedUser", "MANAGED_USER_ID"), (Object)userID, 0);
                final Row userRow = dataObject.getRow("ManagedUser", managedUserIdCriteria);
                final Map<String, String> userDetail = getManagedUserDetailFromRow(userRow);
                userDetailsMap.put(deviceID, userDetail);
            }
        }
        return userDetailsMap;
    }
    
    private static Map<String, String> getManagedUserDetailFromRow(final Row userRow) {
        final Map<String, String> userDetail = new HashMap<String, String>();
        userDetail.put("MANAGED_USER_ID", getValueFromRow(userRow, "MANAGED_USER_ID"));
        userDetail.put("EMAIL_ADDRESS", getValueFromRow(userRow, "EMAIL_ADDRESS"));
        userDetail.put("FIRST_NAME", getValueFromRow(userRow, "FIRST_NAME"));
        userDetail.put("MIDDLE_NAME", getValueFromRow(userRow, "MIDDLE_NAME"));
        userDetail.put("LAST_NAME", getValueFromRow(userRow, "LAST_NAME"));
        userDetail.put("DISPLAY_NAME", getValueFromRow(userRow, "DISPLAY_NAME"));
        userDetail.put("PHONE_NUMBER", getValueFromRow(userRow, "PHONE_NUMBER"));
        userDetail.put("STATUS", getValueFromRow(userRow, "STATUS"));
        return userDetail;
    }
    
    private static String getValueFromRow(final Row row, final String key) {
        if (row.get(key) != null) {
            return String.valueOf(row.get(key));
        }
        return "";
    }
    
    public boolean isUserManaged(final Long userId, final Long customerId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            selectQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
            selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)userId, 0).and(new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)null, 0)).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0)));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            return dataObject.isEmpty();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "Exception in checking if user is managed for id : " + n);
            return false;
        }
    }
    
    public List getManagedUserListFromResList(final List resList) {
        final List mdUserList = new ArrayList();
        try {
            final SelectQuery mdUserQuery = this.getManagedUserQuery();
            mdUserQuery.setCriteria(new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)resList.toArray(), 8));
            mdUserQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
            final DataObject mdUserDo = MDMUtil.getPersistence().get(mdUserQuery);
            if (mdUserDo != null && !mdUserDo.isEmpty()) {
                final Iterator iterator = mdUserDo.getRows("ManagedUser");
                while (iterator.hasNext()) {
                    final Row userRow = iterator.next();
                    final Long managedUserID = (Long)userRow.get("MANAGED_USER_ID");
                    mdUserList.add(managedUserID);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in getManagedUserListFromResList", e);
        }
        return mdUserList;
    }
    
    static {
        ManagedUserHandler.managedUserHandlert = null;
    }
}
