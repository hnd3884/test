package com.me.mdm.onpremise.server.authentication;

import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.db.api.RelationalAPI;
import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.sym.server.mdm.core.MDMUserHandler;
import java.util.HashMap;
import java.util.Iterator;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;
import java.util.TreeMap;
import com.me.devicemanagement.framework.server.authentication.UserListenerHandler;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import java.util.Collection;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.onpremise.webclient.admin.UserController;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.adventnet.authentication.util.AuthUtil;
import com.me.devicemanagement.framework.server.authentication.UserEvent;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserHandler;

public class MDMPUserHandler extends DMOnPremiseUserHandler
{
    private static Logger logger;
    private static MDMPUserHandler mdmpUserHandler;
    
    public static MDMPUserHandler getInstance() {
        if (MDMPUserHandler.mdmpUserHandler == null) {
            return new MDMPUserHandler();
        }
        return MDMPUserHandler.mdmpUserHandler;
    }
    
    public Long addUserForMDM(final JSONObject addUserJObj) throws Exception {
        final UserEvent userEvent = new UserEvent();
        try {
            final String userName = (String)addUserJObj.get("userName");
            final String roleID = (String)addUserJObj.get("role_ID");
            final String domainName = addUserJObj.optString("domainName", "-");
            final String[] cgList = (String[])addUserJObj.opt("cgList");
            final String[] roList = (String[])addUserJObj.opt("roList");
            final Integer computerScope = addUserJObj.optInt("scope", 0);
            final Integer mdmScope = addUserJObj.optInt("mdmScope", 0);
            final String sCustomerIDs = addUserJObj.optString("sCustomerIDs");
            final String locale = addUserJObj.optString("USER_LOCALE");
            final String domainNameForLog = addUserJObj.optString("domainName", "Local");
            final String computerScopeForLog = (computerScope == 0) ? "ALL_MANAGED_COMPUTERS" : ((computerScope == 1) ? "SCOPE_CG" : "SCOPE_RO");
            final String mdmScopeForLog = (mdmScope == 0) ? "ALL_MANAGED_MOBILE_DEVICES" : "SCOPE_CG";
            addUserJObj.put("service_id", (Object)AuthUtil.getServiceId("System"));
            addUserJObj.put("passwordProfileId", (Object)AuthUtil.getPasswordProfileId("Profile 2"));
            addUserJObj.put("accountprofile_id", (Object)AuthUtil.getAccountProfileId("Profile 2"));
            final boolean isPluginUser = addUserJObj.has("isPluginUser") && addUserJObj.getBoolean("isPluginUser");
            if (!isPluginUser && (!addUserJObj.has("domainName") || addUserJObj.get("domainName") == null || String.valueOf(addUserJObj.get("domainName")).equalsIgnoreCase("local") || String.valueOf(addUserJObj.get("domainName")).equalsIgnoreCase("-"))) {
                final Long passwdPolicyId = DMOnPremiseUserUtil.getConfiguredCustomPasswdPolicyID();
                final Long passwdProfileId = DMOnPremiseUserUtil.getConfiguredCustomPasswdProfileID();
                final Long accProfileId = DMOnPremiseUserUtil.getConfiguredCustomAccountProfileID();
                if (passwdPolicyId != null) {
                    addUserJObj.put("passwordPolicyId", (Object)passwdPolicyId);
                }
                if (passwdProfileId != null) {
                    addUserJObj.put("passwordProfileId", (Object)passwdProfileId);
                }
                if (accProfileId != null) {
                    addUserJObj.put("accountprofile_id", (Object)accProfileId);
                }
            }
            ArrayList<String> newList = new ArrayList<String>();
            if (!isUserAccountAvailable(userName, domainName)) {
                try {
                    SyMUtil.getUserTransaction().begin();
                    final String defaultDomainName = SyMUtil.getSyMParameter("DEFAULT_DOMAIN");
                    final UserController uc = new UserController();
                    final TreeMap domainList = UserController.getADDomainNamesForLoginPage();
                    if (domainList == null && domainName != null && !domainName.equalsIgnoreCase("-") && (defaultDomainName == null || !domainName.equalsIgnoreCase(defaultDomainName))) {
                        SyMUtil.updateSyMParameter("DEFAULT_DOMAIN", domainName);
                    }
                    final DataObject addUserDO = SyMUtil.getPersistence().constructDataObject();
                    this.addUser(addUserDO, addUserJObj);
                    final Long loginID = DMOnPremiseUserUtil.addUser(addUserDO, addUserJObj);
                    final Long userID = getDCUserID(loginID);
                    setRoleForUser(loginID, Long.valueOf(Long.parseLong(roleID)));
                    if (sCustomerIDs != null && !sCustomerIDs.equals("")) {
                        CustomerInfoUtil.getInstance().addCustomersToUserMapping(userID, sCustomerIDs);
                    }
                    addOrUpdateAPIKeyForLoginId(loginID);
                    final String[] langCountry = locale.split("_");
                    final String defaultTimeZoneID = SyMUtil.getDefaultTimeZoneID();
                    DMOnPremiseUserUtil.changeAAAProfile(userID, langCountry[0], langCountry[1], defaultTimeZoneID);
                    if (cgList != null && cgList.length > 0) {
                        newList = new ArrayList<String>(Arrays.asList(cgList));
                        setCustomGroupForUser(loginID, cgList);
                    }
                    SyMUtil.getUserTransaction().commit();
                    userEvent.loginID = loginID;
                }
                catch (final Exception e) {
                    SyMUtil.getUserTransaction().rollback();
                    throw e;
                }
                final String isPasswordChanged = SyMUtil.getSyMParameter("IS_PASSWORD_CHANGED");
                if (isPasswordChanged != null && isPasswordChanged.equals("false")) {
                    SyMUtil.updateSyMParameter("IS_PASSWORD_CHANGED", "true");
                }
            }
            else {
                final Long defaultAdminUVHLoginID = DBUtil.getUVHValue("AaaLogin:login_id:0");
                if (!userName.equalsIgnoreCase("admin") || (domainName != null && !domainName.equalsIgnoreCase("-") && !domainName.equalsIgnoreCase("local")) || !DMOnPremiseUserUtil.isDefaultAdminDisabled(defaultAdminUVHLoginID)) {
                    MDMPUserHandler.logger.log(Level.SEVERE, "Exception Occurred in MDMPUserhandler - addUser : UserExists");
                    throw new SyMException(717, I18N.getMsg("dc.admin.uac.USER_EXISTS", new Object[] { domainNameForLog, userName }), "dc.admin.uac.USER_EXISTS", (Throwable)null);
                }
                if (DMOnPremiseUserUtil.unHideDefaultAdmin(defaultAdminUVHLoginID)) {
                    modifyDefaultAdmin(addUserJObj);
                    userEvent.loginID = defaultAdminUVHLoginID;
                }
            }
            userEvent.userName = userName;
            userEvent.isAdminUser = isDefaultAdministratorRole(roleID);
            userEvent.isUserRoleChanged = Boolean.FALSE;
            userEvent.scope = computerScope;
            UserListenerHandler.getInstance().invokeUsedAddedListeners(userEvent);
        }
        catch (final SyMException symEx) {
            throw symEx;
        }
        catch (final Exception ex) {
            MDMPUserHandler.logger.log(Level.SEVERE, "Exception Occurred in MDMPUserhandler - userAdded : ", ex);
            throw ex;
        }
        return userEvent.loginID;
    }
    
    private static void modifyDefaultAdmin(final JSONObject addUserJObj) {
        try {
            final String loginName = (String)addUserJObj.get("userName");
            final String password = (String)addUserJObj.get("password");
            final String emailID = addUserJObj.optString("USER_EMAIL_ID");
            final String phNum = addUserJObj.optString("USER_PH_NO");
            final JSONArray roleIdsArray = (JSONArray)addUserJObj.opt("roleIdsList");
            final List roleIdsList = DMUserHandler.getIdsFromJsonArray(roleIdsArray);
            final Long accountProfileId = addUserJObj.getLong("accountprofile_id");
            final Long passwordProfileId = addUserJObj.getLong("passwordProfileId");
            final Long passwordPolicyId = addUserJObj.has("passwordPolicyId") ? Long.valueOf(addUserJObj.getLong("passwordPolicyId")) : null;
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaContactInfo"));
            final Criteria userCriteria = new Criteria(Column.getColumn("AaaUser", "FIRST_NAME"), (Object)loginName, 0);
            selectQuery.addJoin(new Join("AaaContactInfo", "AaaUserContactInfo", new String[] { "CONTACTINFO_ID" }, new String[] { "CONTACTINFO_ID" }, 2));
            selectQuery.addJoin(new Join("AaaUserContactInfo", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addJoin(new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addJoin(new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccount", "*"));
            selectQuery.addSelectColumn(Column.getColumn("AaaContactInfo", "*"));
            selectQuery.setCriteria(userCriteria);
            DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("AaaContactInfo");
                row.set("EMAILID", (Object)emailID);
                row.set("LANDLINE", (Object)phNum);
                dataObject.updateRow(row);
                final Row accountRow = dataObject.getFirstRow("AaaAccount");
                accountRow.set("ACCOUNTPROFILE_ID", (Object)accountProfileId);
                dataObject.updateRow(accountRow);
                SyMUtil.getPersistence().update(dataObject);
            }
            Row row = dataObject.getFirstRow("AaaAccount");
            final Long accountId = (Long)row.get("ACCOUNT_ID");
            Criteria criteria = new Criteria(Column.getColumn("AaaAuthorizedRole", "ACCOUNT_ID"), (Object)accountId, 0);
            SyMUtil.getPersistence().delete(criteria);
            DataObject dataObj = SyMUtil.getPersistence().get("AaaAuthorizedRole", (Criteria)null);
            for (int i = 0; i < roleIdsList.size(); ++i) {
                final Long id = roleIdsList.get(i);
                final Row accAuthRow = new Row("AaaAuthorizedRole");
                accAuthRow.set("ACCOUNT_ID", (Object)accountId);
                accAuthRow.set("ROLE_ID", (Object)id);
                dataObj.addRow(accAuthRow);
            }
            SyMUtil.getPersistence().update(dataObj);
            row = dataObject.getFirstRow("AaaAccount");
            final Long loginId = (Long)row.get("LOGIN_ID");
            dataObj = SyMUtil.getPersistence().get("AaaAuthorizedRole", (Criteria)null);
            criteria = new Criteria(Column.getColumn("AaaLoginExtn", "LOGIN_ID"), (Object)loginId, 0);
            SyMUtil.getPersistence().delete(criteria);
            final Row aaaLoginExtn = new Row("AaaLoginExtn");
            aaaLoginExtn.set("LOGIN_ID", (Object)loginId);
            aaaLoginExtn.set("CREATION_TIME", (Object)new Long(System.currentTimeMillis()));
            aaaLoginExtn.set("MODIFIED_TIME", (Object)new Long(System.currentTimeMillis()));
            aaaLoginExtn.set("SCOPE", (Object)0);
            aaaLoginExtn.set("MDM_SCOPE", (Object)0);
            dataObj.addRow(aaaLoginExtn);
            SyMUtil.getPersistence().update(dataObj);
            Criteria accountCriteria = new Criteria(Column.getColumn("AaaAccPassword", "ACCOUNT_ID"), (Object)accountId, 0);
            dataObject = SyMUtil.getPersistence().get("AaaAccPassword", accountCriteria);
            accountCriteria = new Criteria(Column.getColumn("AaaPassword", "PASSWORD_ID"), dataObject.getFirstValue("AaaAccPassword", "PASSWORD_ID"), 0);
            final DataObject passwordObj = SyMUtil.getPersistence().get("AaaPassword", accountCriteria);
            final Row passwordRow = passwordObj.getFirstRow("AaaPassword");
            passwordRow.set("PASSWDPROFILE_ID", (Object)passwordProfileId);
            if (passwordPolicyId != null) {
                passwordRow.set("PASSWDRULE_ID", (Object)passwordPolicyId);
            }
            passwordObj.updateRow(passwordRow);
            SYMClientUtil.changePassword(loginName, password);
        }
        catch (final Exception e) {
            MDMPUserHandler.logger.log(Level.WARNING, "Exception while modifying DefaultAdmin ", e);
        }
    }
    
    public static void modifyUserForDC(final JSONObject modifyUserJObj) throws Exception {
        try {
            final UserEvent userEvent = new UserEvent();
            final boolean flag = false;
            final Long loginID = (Long)modifyUserJObj.get("loginID");
            final String userName = (String)modifyUserJObj.get("userName");
            final String roleID = modifyUserJObj.optString("role_ID", (String)null);
            final Integer computerScope = modifyUserJObj.optInt("scope", 0);
            final Integer mdmScope = modifyUserJObj.optInt("mdmScope", 0);
            final String[] cgList = (String[])modifyUserJObj.opt("cgList");
            final String[] roList = (String[])modifyUserJObj.opt("roList");
            final String domainName = modifyUserJObj.optString("domainName", "-");
            final String[] oldMappedArray = (String[])modifyUserJObj.opt("oldMappedList");
            final String sCustomerIDs = modifyUserJObj.optString("sCustomerIDs");
            final JSONArray roleIDArrList = (JSONArray)modifyUserJObj.opt("roleIdsList");
            final Long loggedOnUserId = modifyUserJObj.optLong("loggedOnUserId");
            ArrayList<String> oldMappedList = new ArrayList<String>();
            if (oldMappedArray != null && oldMappedArray.length > 0) {
                oldMappedList = new ArrayList<String>(Arrays.asList(oldMappedArray));
            }
            final String domainNameForLog = modifyUserJObj.optString("domainName", "Local");
            final String computerScopeForLog = (computerScope == 0) ? "ALL_MANAGED_COMPUTERS" : ((computerScope == 1) ? "SCOPE_CG" : "SCOPE_RO");
            final String mdmScopeForLog = (mdmScope == 0) ? "ALL_MANAGED_MOBILE_DEVICES" : "SCOPE_CG";
            final String oldUserName = getDCUser(loginID);
            final Long oldUserID = getDCUserID(loginID);
            final Long oldRoleID = getRoleIdForUser(loginID);
            ArrayList<String> newList = new ArrayList<String>();
            if (cgList != null && cgList.length > 0) {
                newList = new ArrayList<String>(Arrays.asList(cgList));
            }
            else if (roList != null && roList.length > 0) {
                newList = new ArrayList<String>(Arrays.asList(roList));
            }
            try {
                MDMPUserHandler.logger.log(Level.INFO, "Going to modify {0} with Role id = {1} mapped CG/RO resources id= {2}", new Object[] { oldUserName, oldRoleID, oldMappedList });
                SyMUtil.getUserTransaction().begin();
                final String defaultDomainName = SyMUtil.getSyMParameter("DEFAULT_DOMAIN");
                final UserController uc = new UserController();
                final TreeMap domainList = UserController.getADDomainNamesForLoginPage();
                if (domainList == null && domainName != null && !domainName.equalsIgnoreCase("-") && (defaultDomainName == null || !domainName.equalsIgnoreCase(defaultDomainName))) {
                    SyMUtil.updateSyMParameter("DEFAULT_DOMAIN", domainName);
                }
                updateManagedUserDetailsForModifiedTechnician(modifyUserJObj);
                DMOnPremiseUserUtil.modifyUser(modifyUserJObj);
                boolean isPluginUser = false;
                if (modifyUserJObj.optString("IS_PLUGIN_USER").equalsIgnoreCase("true")) {
                    isPluginUser = true;
                }
                if (!isPluginUser && roleID != null) {
                    modifyAAAAuthorizedAccountForModifyUser(loginID, roleIDArrList);
                    setRoleForUser(loginID, Long.valueOf(Long.parseLong(roleID)));
                }
                if (!sCustomerIDs.equals("")) {
                    final Long userID = getDCUserID(loginID);
                    CustomerInfoUtil.getInstance().removeUserFromCustomerMapping(userID);
                    CustomerInfoUtil.getInstance().addCustomersToUserMapping(userID, sCustomerIDs);
                }
                addOrUpdateAPIKeyForLoginId(loginID);
                final Long userid = getDCUserID(loginID);
                if (!modifyUserJObj.optString("USER_LOCALE").isEmpty()) {
                    final String[] langCountry = ((String)modifyUserJObj.get("USER_LOCALE")).split("_");
                    String seletedTimeZone = null;
                    if (!modifyUserJObj.optString("PERSONALISE_TIME_ZONE").isEmpty()) {
                        seletedTimeZone = (String)modifyUserJObj.get("PERSONALISE_TIME_ZONE");
                    }
                    if (seletedTimeZone == null || seletedTimeZone.isEmpty()) {
                        seletedTimeZone = SyMUtil.getDefaultTimeZoneID();
                    }
                    DMOnPremiseUserUtil.changeAAAProfile(userid, langCountry[0], langCountry[1], seletedTimeZone);
                }
                if (cgList != null && cgList.length != 0) {
                    final List<Long> cgListData = new ArrayList<Long>();
                    for (final String cg : cgList) {
                        cgListData.add(Long.parseLong(cg));
                    }
                    MDMGroupHandler.getInstance().reassignDevicesToTechnicianCreatedGroupsOnScopeModification((long)loginID, (List)cgListData, loggedOnUserId);
                }
                deleteUserCGMapping(loginID);
                if (cgList != null && cgList.length != 0) {
                    setCustomGroupForUser(loginID, cgList);
                }
                if (roleID != null && oldRoleID != Long.valueOf(roleID)) {
                    final Criteria deleteDisplayOrderCri = new Criteria(new Column("HomePageSummaryDisplayOrder", "LOGIN_ID"), (Object)loginID, 0);
                    SyMUtil.getPersistence().delete(deleteDisplayOrderCri);
                }
                SyMUtil.getUserTransaction().commit();
                MDMPUserHandler.logger.log(Level.INFO, "{0} has been successfully modifed with username = {1} Role id ={2} domain = {3} computerscope = {4} MDM Scope={5} mapping list CG/RO resources id= {6}", new Object[] { oldUserName, userName, roleID, domainNameForLog, computerScopeForLog, mdmScopeForLog, newList });
            }
            catch (final Exception e) {
                SyMUtil.getUserTransaction().rollback();
                throw e;
            }
            final String isPasswordChanged = SyMUtil.getSyMParameter("IS_PASSWORD_CHANGED");
            if (isPasswordChanged != null && isPasswordChanged.equals("false")) {
                SyMUtil.updateSyMParameter("IS_PASSWORD_CHANGED", "true");
            }
            final Long userid2 = getDCUserID(loginID);
            userEvent.loginID = loginID;
            userEvent.isPreviouslyManagingAllComputers = isUserManagingAllComputers(loginID);
            userEvent.userID = oldUserID;
            userEvent.isUserRoleChanged = ((roleID == null) ? Boolean.FALSE : ((oldRoleID != Long.valueOf(roleID)) ? Boolean.TRUE : Boolean.FALSE));
            userEvent.userName = userName;
            userEvent.scope = computerScope;
            userEvent.isAdminUser = isDefaultAdministratorRole((roleID == null) ? String.valueOf(oldRoleID) : roleID);
            userEvent.isUserScopeChanged = (oldMappedList.containsAll(newList) ? (newList.containsAll(oldMappedList) ? Boolean.FALSE : Boolean.TRUE) : Boolean.TRUE);
            UserListenerHandler.getInstance().invokeuserModifiedListeners(userEvent);
        }
        catch (final Exception ex) {
            MDMPUserHandler.logger.log(Level.SEVERE, "Exception Occurred in MDMPUserhandler - ModifyUser : ", ex);
            throw ex;
        }
    }
    
    public static void setCustomGroupForUser(final Long loginId, final String[] cgList) {
        try {
            if (cgList != null && cgList.length != 0) {
                final WritableDataObject dobj = new WritableDataObject();
                for (int i = 0; i < cgList.length; ++i) {
                    final Row userCustomGroupMapping = new Row("UserCustomGroupMapping");
                    userCustomGroupMapping.set("LOGIN_ID", (Object)loginId);
                    userCustomGroupMapping.set("GROUP_RESOURCE_ID", (Object)cgList[i]);
                    dobj.addRow(userCustomGroupMapping);
                }
                final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("CustomGroup"));
                final Join customGroupJoin = new Join("CustomGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 2);
                sq.addJoin(customGroupJoin);
                final Criteria mdmCriteira = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)cgList, 8);
                final ArrayList groupsTypeList = (ArrayList)MDMGroupHandler.getMDMGroupType();
                final Criteria typeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupsTypeList.toArray(), 8);
                final Criteria criteria = mdmCriteira.and(typeCriteria);
                sq.setCriteria(criteria);
                sq.addSelectColumn(Column.getColumn((String)null, "*"));
                final DataObject cgDevicesDo = SyMUtil.getPersistence().get((SelectQuery)sq);
                if (!cgDevicesDo.isEmpty()) {
                    final Iterator ite = cgDevicesDo.getRows("CustomGroupMemberRel");
                    while (ite.hasNext()) {
                        final Row rowList = ite.next();
                        final Object resObj = rowList.get("MEMBER_RESOURCE_ID");
                        final Row userResourceMapping = new Row("UserDeviceMapping");
                        userResourceMapping.set("LOGIN_ID", (Object)loginId);
                        userResourceMapping.set("RESOURCE_ID", resObj);
                        final Row temp = dobj.getRow("UserDeviceMapping", userResourceMapping);
                        if (temp == null) {
                            dobj.addRow(userResourceMapping);
                        }
                    }
                }
                SyMUtil.getPersistence().update((DataObject)dobj);
            }
        }
        catch (final Exception e) {
            MDMPUserHandler.logger.log(Level.SEVERE, "Exception Occurred in CustomGroupMapping : ", e);
        }
    }
    
    private static void deleteUserCGMapping(final Long loginID) throws Exception {
        try {
            final Criteria cgMapCriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)loginID, 0);
            final Criteria deviceMapCriteria = new Criteria(Column.getColumn("UserDeviceMapping", "LOGIN_ID"), (Object)loginID, 0);
            SyMUtil.getPersistence().delete(cgMapCriteria);
            SyMUtil.getPersistence().delete(deviceMapCriteria);
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    public static boolean deleteUser(final Long loginID, final boolean isPluginUser) throws Exception {
        return deleteUser(loginID, isPluginUser, new HashMap());
    }
    
    public static boolean deleteUser(final Long loginID, final boolean isPluginUser, final HashMap userDelPII) {
        boolean status = false;
        try {
            final Long roleID = getRoleIdForUser(Long.valueOf((long)loginID));
            final Long userID = getDCUserID(Long.valueOf((long)loginID));
            final HashMap userContactInfo = DMUserHandler.getUserContactProps(userID, userDelPII);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DCAAALOGIN"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final Criteria criteria = new Criteria(Column.getColumn("DCAAALOGIN", "LOGIN_ID"), (Object)loginID, 0);
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (isPluginUser && dataObject.size("DCAAALOGIN") <= 0) {
                MDMPUserHandler.logger.log(Level.WARNING, "DELETE USER SDP == The user already created by DC. So we are not going to Delete this user from SDP : {0} ", loginID);
                return false;
            }
            synchronized (MDMPUserHandler.USER_HANDLING_LOCK) {
                try {
                    SyMUtil.getUserTransaction().begin();
                    final Criteria selectCriteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
                    SyMUtil.getPersistence().delete(selectCriteria);
                    final Criteria crtiteria = new Criteria(Column.getColumn("LoginUserCustomerMapping", "DC_USER_ID"), (Object)userID, 0);
                    SyMUtil.getPersistence().delete(crtiteria);
                    final Criteria twoFactorCriteria = new Criteria(new Column("AaaUserTwoFactorDetails", "USER_ID"), (Object)userID, 0);
                    SyMUtil.getPersistence().delete(twoFactorCriteria);
                    new MDMUserHandler().updateDeletedUserContact(userID, loginID);
                    SyMUtil.getUserTransaction().commit();
                }
                catch (final Exception e) {
                    SyMUtil.getUserTransaction().rollback();
                    throw e;
                }
                status = true;
            }
            final String defaultDomain = SyMUtil.getSyMParameter("DEFAULT_DOMAIN");
            if (defaultDomain != null && !defaultDomain.equals("dcLocal")) {
                final SelectQuery defaultDomainUsersSelect = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
                defaultDomainUsersSelect.addSelectColumn(new Column("AaaLogin", "DOMAINNAME"));
                defaultDomainUsersSelect.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
                final Criteria defaultDomainCri = new Criteria(new Column("AaaLogin", "DOMAINNAME"), (Object)defaultDomain, 0);
                defaultDomainUsersSelect.setCriteria(defaultDomainCri);
                final DataObject defaultDomainDataObject = SyMUtil.getPersistence().get(defaultDomainUsersSelect);
                if (defaultDomainDataObject != null) {
                    final Iterator defaultDomainUsers = defaultDomainDataObject.getRows("AaaLogin");
                    if (defaultDomainUsers == null || !defaultDomainUsers.hasNext()) {
                        SyMUtil.updateSyMParameter("DEFAULT_DOMAIN", "dcLocal");
                    }
                }
                else {
                    SyMUtil.updateSyMParameter("DEFAULT_DOMAIN", "dcLocal");
                }
            }
            final UserEvent userEvent = new UserEvent();
            userEvent.loginID = loginID;
            userEvent.userID = userID;
            userEvent.isAdminUser = isDefaultAdministratorRole(roleID);
            userEvent.userContactInfo = userContactInfo;
            UserListenerHandler.getInstance().invokeuserDeletedListeners(userEvent);
        }
        catch (final Exception e2) {
            status = false;
            MDMPUserHandler.logger.log(Level.SEVERE, "Error in deleting helpdesk user", e2);
        }
        return status;
    }
    
    public static void updateDeletedUserContact(final Long userID) {
        new MDMUserHandler().updateDeletedUserContact(userID, (Long)null);
    }
    
    public static Long getAAAAccountIdFromLoginId(final Long loginId) {
        Long aaaAccountID = null;
        try {
            if (loginId != null) {
                final Criteria logIdCriteria = new Criteria(Column.getColumn("AaaAccount", "LOGIN_ID"), (Object)loginId, 0);
                final DataObject aaaAccountDO = SyMUtil.getPersistence().get("AaaAccount", logIdCriteria);
                if (!aaaAccountDO.isEmpty()) {
                    final Row aaaAccountRow = aaaAccountDO.getFirstRow("AaaAccount");
                    aaaAccountID = (Long)aaaAccountRow.get("ACCOUNT_ID");
                }
            }
            MDMPUserHandler.logger.log(Level.INFO, "getAAAAccountIdFromLoginId loginId:{0}; aaaAccountId:{1}", new Object[] { loginId, aaaAccountID });
        }
        catch (final Exception ex) {
            MDMPUserHandler.logger.log(Level.WARNING, "Exception occurred while getAAAAccountIdFromLoginId", ex);
        }
        return aaaAccountID;
    }
    
    private static void modifyAAAAuthorizedAccountForModifyUser(final Long loginId, final JSONArray roleIdList) {
        try {
            if (loginId != null && roleIdList != null && roleIdList.length() > 0) {
                final Long aaaAccoutnId = getAAAAccountIdFromLoginId(loginId);
                if (aaaAccoutnId != null) {
                    final Criteria aaaAccountIdCriteria = new Criteria(Column.getColumn("AaaAuthorizedRole", "ACCOUNT_ID"), (Object)aaaAccoutnId, 0);
                    SyMUtil.getPersistence().delete(aaaAccountIdCriteria);
                    final DataObject aaaAuthorizedDO = SyMUtil.getPersistence().constructDataObject();
                    for (int i = 0; i < roleIdList.length(); ++i) {
                        final Long roleId = roleIdList.optLong(i);
                        if (roleId != null) {
                            final Row aaaAccountRow = new Row("AaaAuthorizedRole");
                            aaaAccountRow.set("ACCOUNT_ID", (Object)aaaAccoutnId);
                            aaaAccountRow.set("ROLE_ID", (Object)roleId);
                            aaaAuthorizedDO.addRow(aaaAccountRow);
                        }
                    }
                    SyMUtil.getPersistence().add(aaaAuthorizedDO);
                }
            }
        }
        catch (final Exception ex) {
            MDMPUserHandler.logger.log(Level.WARNING, "Exception occurred while modifyAAAAuthorizedAccountForModifyUser", ex);
        }
    }
    
    private static void updateManagedUserDetailsForModifiedTechnician(final JSONObject modifyUserJObj) {
        try {
            final String userName = (String)modifyUserJObj.get("userName");
            final String emailID = modifyUserJObj.optString("USER_EMAIL_ID");
            final String phNum = modifyUserJObj.optString("USER_PH_NO");
            String domainName = modifyUserJObj.optString("domainName", "MDM");
            domainName = ((domainName.equalsIgnoreCase("-") || domainName.trim().equalsIgnoreCase("")) ? "MDM" : domainName);
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ManagedUser");
            updateQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
            updateQuery.setCriteria(new Criteria(Column.getColumn("Resource", "NAME"), (Object)userName, 0).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)domainName, 0)).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)CustomerInfoUtil.getInstance().getCustomerId(), 0)));
            updateQuery.setUpdateColumn("EMAIL_ADDRESS", (Object)emailID);
            if (phNum != null && !phNum.trim().equalsIgnoreCase("") && !phNum.equalsIgnoreCase("--")) {
                updateQuery.setUpdateColumn("PHONE_NUMBER", (Object)phNum);
            }
            DataAccess.update(updateQuery);
        }
        catch (final Exception exp) {
            MDMPUserHandler.logger.log(Level.SEVERE, "Exception while updating manageduser details for technician", exp);
        }
    }
    
    public static DataObject getLoginDoForUserId(final Long userId) throws DataAccessException {
        try {
            final Criteria criteria = new Criteria(new Column("AaaLogin", "USER_ID"), (Object)userId, 0, false);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AaaUser"));
            selectQuery.addJoin(new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addSelectColumn(new Column("AaaLogin", "LOGIN_ID"));
            selectQuery.addSelectColumn(new Column("AaaLogin", "DOMAINNAME"));
            selectQuery.addSelectColumn(new Column("AaaUser", "FIRST_NAME"));
            selectQuery.addSelectColumn(new Column("AaaUser", "USER_ID"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            return dataObject;
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Error occured while checking user account status", e);
            throw e;
        }
    }
    
    public Hashtable<String, String> userLastLogonDetails() throws Exception {
        final Hashtable<String, String> userLogonHash = new Hashtable<String, String>();
        final RelationalAPI relApi = RelationalAPI.getInstance();
        Connection connection = null;
        DataSet dataSet = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaAccSession"));
            selectQuery.addJoin(new Join("AaaAccSession", "AaaAccount", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
            selectQuery.addJoin(new Join("AaaAccount", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            selectQuery.addJoin(new Join("AaaLogin", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("AaaAccSession", "OPENTIME").maximum());
            selectQuery.addSelectColumn(Column.getColumn("AaaUser", "FIRST_NAME"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("AaaAccSession", "STATUS"), (Object)"ACTIVE", 1, false));
            final List list = new ArrayList();
            list.add(Column.getColumn("AaaUser", "FIRST_NAME"));
            final GroupByClause groupBy = new GroupByClause(list);
            selectQuery.setGroupByClause(groupBy);
            connection = relApi.getConnection();
            dataSet = relApi.executeQuery((Query)selectQuery, connection);
            while (dataSet.next()) {
                userLogonHash.put(dataSet.getAsString(2), dataSet.getAsString(1));
            }
        }
        catch (final Exception e) {
            MDMPUserHandler.logger.log(Level.SEVERE, "Exception while getting user last logon details :", e);
            try {
                if (connection != null) {
                    connection.close();
                }
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception ex) {
                MDMPUserHandler.logger.log(Level.WARNING, "Exception while closing connection", ex);
            }
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception ex2) {
                MDMPUserHandler.logger.log(Level.WARNING, "Exception while closing connection", ex2);
            }
        }
        return userLogonHash;
    }
    
    static {
        MDMPUserHandler.logger = Logger.getLogger(MDMPUserHandler.class.getName());
        MDMPUserHandler.mdmpUserHandler = null;
    }
}
