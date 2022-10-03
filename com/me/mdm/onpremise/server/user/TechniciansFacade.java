package com.me.mdm.onpremise.server.user;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.Hashtable;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.core.MDMUserHandler;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.me.mdm.server.dep.DEPTechnicianUserListener;
import com.adventnet.i18n.I18N;
import com.me.mdm.api.APIUtil;
import java.util.List;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.mdm.onpremise.server.authentication.MDMPUserHandler;
import com.me.devicemanagement.onpremise.server.twofactor.TwoFactorAction;
import com.me.devicemanagement.framework.server.authorization.RoleHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.Collection;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.role.RolesFacade;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import org.json.JSONObject;
import java.util.logging.Logger;

public class TechniciansFacade implements com.me.mdm.server.user.TechniciansFacade
{
    private static final Logger LOGGER;
    
    public void addTechnicians(final JSONObject message) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "create-failed";
        String userName = "";
        try {
            final JSONObject response = new JSONObject();
            final JSONObject body = message.getJSONObject("msg_body");
            final JSONObject addUserObj = new JSONObject();
            if (LicenseProvider.getInstance().isUserLimitReached()) {
                throw new APIHTTPException("COM0023", new Object[0]);
            }
            final JSONArray customGroupJSONArray = body.optJSONArray("group_ids");
            String role_ID = JSONUtil.optStringIgnoreKeyCase(body, "user_role_id");
            String roleName = "";
            try {
                roleName = (String)DBUtil.getValueFromDB("UMRole", "UM_ROLE_ID", (Object)role_ID, "UM_ROLE_NAME");
            }
            catch (final Exception ex) {
                TechniciansFacade.LOGGER.log(Level.WARNING, "Error while logging security details", ex);
            }
            secLog.put((Object)"ROLE", (Object)roleName);
            if (role_ID == null) {
                role_ID = DMUserHandler.getRoleID("Technician");
            }
            else if (!new RolesFacade().validateRole(role_ID)) {
                throw new APIHTTPException("USR001", new Object[] { role_ID });
            }
            final List roleIdsList = DMUserHandler.getRoleList(role_ID);
            final String domainName = body.optString("domain_name", "local");
            secLog.put((Object)"DOMAIN", (Object)domainName);
            if (!domainName.equalsIgnoreCase("local")) {
                addUserObj.put("domainName", (Object)domainName);
                addUserObj.put("password", (Object)String.valueOf(MDMUtil.getCurrentTimeInMillis()));
            }
            else {
                addUserObj.put("domainName", (Object)"-");
                addUserObj.put("password", body.get("password"));
                final String password = body.optString("password", "");
                if (password.equalsIgnoreCase("admin")) {
                    throw new APIHTTPException("USR013", new Object[0]);
                }
            }
            final JSONArray roleIdsListArray = new JSONArray((Collection)roleIdsList);
            final String sCustomerIDs = TechniciansHandler.getTechniciansInstance(message, addUserObj).validateCustomerId(message, role_ID);
            userName = String.valueOf(body.get("user_name"));
            userName = userName.toLowerCase().trim();
            secLog.put((Object)"NAME", (Object)userName);
            if (CustomerInfoUtil.getInstance().isMSP() && !MDMStringUtils.isEmpty(sCustomerIDs)) {
                secLog.put((Object)"CUSTOMER_IDs", (Object)sCustomerIDs);
            }
            final String userLocale = body.optString("user_locale", ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale().toString());
            addUserObj.put("userName", (Object)userName);
            addUserObj.put("loginName", (Object)userName);
            addUserObj.put("role_ID", (Object)role_ID);
            String userEmail = body.optString("email_id", "");
            if (userEmail.equalsIgnoreCase("")) {
                userEmail = body.optString("user_email", "");
            }
            addUserObj.put("USER_EMAIL_ID", (Object)userEmail);
            String phoneNumber = body.optString("phone_number", "");
            if (phoneNumber.equalsIgnoreCase("")) {
                phoneNumber = body.optString("user_landline_number", "");
            }
            addUserObj.put("USER_PH_NO", (Object)phoneNumber);
            addUserObj.put("USER_LOCALE", (Object)userLocale);
            addUserObj.put("sCustomerIDs", (Object)sCustomerIDs);
            final int mdmScope = new TechniciansHandler().validateScope(role_ID, customGroupJSONArray);
            if (customGroupJSONArray != null && customGroupJSONArray.length() > 0) {
                final String[] cgList = new String[customGroupJSONArray.length()];
                for (int i = 0; i < customGroupJSONArray.length(); ++i) {
                    cgList[i] = String.valueOf(JSONUtil.optLongForUVH(customGroupJSONArray, i, Long.valueOf(-1L)));
                }
                addUserObj.put("cgList", (Object)cgList);
            }
            addUserObj.put("mdmScope", mdmScope);
            if (mdmScope == 0) {
                roleIdsListArray.put((Object)new RoleHandler().getRoleID("All_Managed_Mobile_Devices"));
            }
            addUserObj.put("roleIdsList", (Object)roleIdsListArray);
            addUserObj.put("isTwoFactorEnabledGlobaly", TwoFactorAction.isTwoFactorEnabledGlobaly());
            addUserObj.put("summaryGroupID", DMUserHandler.getSummaryGroupID(String.valueOf(role_ID)));
            final Long id = MDMPUserHandler.getInstance().addUserForMDM(addUserObj);
            remarks = "create-success";
            DCEventLogUtil.getInstance().addEvent(701, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), (HashMap)null, "dc.admin.uac.USER_SUCCESSFULLY_ADDED", (Object)(domainName + "@@@" + userName), true);
            if (id != null && id != 0L) {
                response.put("status", 202);
            }
        }
        catch (final JSONException e) {
            TechniciansFacade.LOGGER.log(Level.SEVERE, " -- addTechnicians()  >   Error ", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception e2) {
            TechniciansFacade.LOGGER.log(Level.SEVERE, " -- addTechnicians()  >   Error ", e2);
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            if (!(e2 instanceof SyMException)) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            if (((SyMException)e2).getErrorCode() == 717) {
                throw new APIHTTPException("COM0010", new Object[] { userName });
            }
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            SecurityOneLineLogger.log("User_Management", "ADD_USER", secLog, Level.INFO);
        }
    }
    
    public void removeTechnicians(final JSONObject req) throws APIHTTPException {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        final org.json.simple.JSONArray removedUserNames = new org.json.simple.JSONArray();
        String remarks = "delete-failed";
        try {
            Long userId = APIUtil.getResourceID(req, "technician_id");
            JSONArray usersJSONArray = new JSONArray();
            if (userId == -1L) {
                usersJSONArray = req.getJSONObject("msg_body").getJSONArray("users");
            }
            else {
                usersJSONArray.put((Object)userId);
            }
            for (int i = 0; i < usersJSONArray.length(); ++i) {
                userId = JSONUtil.optLongForUVH(usersJSONArray, i, Long.valueOf(-1L));
                final Long loginId = DMUserHandler.getLoginIdForUserId(userId);
                if (loginId == null) {
                    throw new APIHTTPException("USR003", new Object[] { userId });
                }
                if (userId.equals(MDMUtil.getInstance().getCurrentlyLoggedOnUserID())) {
                    throw new APIHTTPException("USR008", new Object[] { DMUserHandler.getUserNameFromUserID(userId) });
                }
                String domain = DMUserHandler.getDCUserDomain(loginId);
                final String userName = DMUserHandler.getUserName(loginId);
                domain = (MDMStringUtils.isEmpty(domain) ? I18N.getMsg("desktopcentral.configurations.config.LOCAL", new Object[0]) : domain);
                final Long defaultAdminUVHLoginID = DBUtil.getUVHValue("AaaLogin:login_id:0");
                boolean userDeleted;
                if (loginId.equals(defaultAdminUVHLoginID)) {
                    new DEPTechnicianUserListener().deleteUserPersonalDataForDefaultAdmin(userId);
                    userDeleted = DMOnPremiseUserUtil.hideDefaultAdmin(loginId);
                    new MDMUserHandler().updateDeletedUserContact(userId, loginId);
                }
                else {
                    userDeleted = MDMPUserHandler.deleteUser(loginId, Boolean.FALSE);
                }
                if (userDeleted) {
                    removedUserNames.add((Object)(domain + "\\" + userName));
                    DCEventLogUtil.getInstance().addEvent(705, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), (HashMap)null, "dc.admin.uac.USER_SUCCESSFULLY_DEL", (Object)(domain + "@@@" + userName), true);
                }
                else {
                    TechniciansFacade.LOGGER.log(Level.WARNING, "Error while deleting the user: {0}\\{1}", new Object[] { domain, userName });
                }
            }
            remarks = "delete-success";
        }
        catch (final Exception e) {
            TechniciansFacade.LOGGER.log(Level.SEVERE, " -- removeTechnicians()  >   Error ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            secLog.put((Object)"REMOVED_USERS", (Object)removedUserNames);
            SecurityOneLineLogger.log("User_Management", "DELETE_USER", secLog, Level.INFO);
        }
    }
    
    public JSONObject getTechnicians(final JSONObject message) throws APIHTTPException {
        try {
            final Long userId = JSONUtil.optLongForUVH(message.getJSONObject("msg_header").getJSONObject("resource_identifier"), "technician_id", Long.valueOf(-1L));
            final String userName = APIUtil.getStringFilter(message, "search_user_name");
            final String userEmail = APIUtil.getStringFilter(message, "user_email");
            final JSONObject requestJSON = new JSONObject();
            if (userId != -1L) {
                requestJSON.put("user_id", (Object)userId);
            }
            if (userName != null) {
                requestJSON.put("search_user_name", (Object)userName);
            }
            if (userEmail != null) {
                requestJSON.put("user_email", (Object)userEmail);
            }
            return TechniciansHandler.getTechniciansInstance(message, requestJSON).getUserDetails(requestJSON);
        }
        catch (final Exception e) {
            TechniciansFacade.LOGGER.log(Level.SEVERE, "Exception while getting Technician details.", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void updateTechnicians(final JSONObject req) {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "update-failed";
        try {
            final JSONObject requestJSON = req.getJSONObject("msg_body");
            final String email = requestJSON.optString("user_email");
            final String domainName = requestJSON.optString("domain_name", "-");
            secLog.put((Object)"DOMAIN", (Object)domainName);
            Long userId = APIUtil.getResourceID(req, "technician_id");
            if (userId == -1L) {
                userId = JSONUtil.optLongForUVH(requestJSON, "user_id", Long.valueOf(-1L));
            }
            final Long roleId = JSONUtil.optLongForUVH(requestJSON, "user_role_id", Long.valueOf(-1L));
            String roleName = "";
            try {
                roleName = (String)DBUtil.getValueFromDB("UMRole", "UM_ROLE_ID", (Object)roleId, "UM_ROLE_NAME");
            }
            catch (final Exception ex) {
                TechniciansFacade.LOGGER.log(Level.WARNING, "Error while logging security details", ex);
            }
            secLog.put((Object)"ROLE", (Object)roleName);
            final String oldUserName = DMUserHandler.getUserNameFromUserID(userId);
            final Long loginId = DMUserHandler.getLoginIdForUserId(userId);
            final String oldDomainName = DMUserHandler.getDCUserDomain(loginId);
            if (roleId == -1L) {
                MDMPUserHandler.getInstance().addorUpdateContactEmail(userId, email);
            }
            else {
                final JSONArray customGroupJSONArray = requestJSON.optJSONArray("group_ids");
                if (DMUserHandler.getLoginIdForUserId(userId) == null) {
                    throw new APIHTTPException("USR003", new Object[] { userId });
                }
                final String phoneNumber = requestJSON.optString("user_landline_number");
                final String userEmailId = requestJSON.optString("user_email", "");
                String userName = requestJSON.optString("user_name", "");
                userName = userName.toLowerCase().trim();
                secLog.put((Object)"NAME", (Object)userName);
                final String userScope = requestJSON.optString("user_scope");
                secLog.put((Object)"SCOPE", (Object)userScope);
                int mdmScope = 0;
                if (userScope != null && userScope.equalsIgnoreCase("DeviceCG")) {
                    mdmScope = 1;
                }
                final List groupList = MDMGroupHandler.getAssignedCustomGroupsForUser(loginId);
                final String[] oldMappedList = new String[groupList.size()];
                for (int i = 0; i < groupList.size(); ++i) {
                    final Hashtable groupMap = groupList.get(i);
                    oldMappedList[i] = String.valueOf(groupMap.get("custom_gp_id"));
                }
                final String contactInfoId = String.valueOf(DMUserHandler.getContactInfoDO(userId).getRow("AaaContactInfo").get("CONTACTINFO_ID"));
                final String userLocale = requestJSON.optString("user_locale", "en_US");
                if (loginId.equals(DMUserHandler.getLoginIdForUserId(MDMUtil.getInstance().getCurrentlyLoggedOnUserID())) && !DMUserHandler.isDefaultAdministratorRole(roleId)) {
                    throw new APIHTTPException("USR007", new Object[0]);
                }
                if (roleId != -1L && !new RolesFacade().validateRole(String.valueOf(roleId))) {
                    throw new APIHTTPException("USR001", new Object[] { roleId });
                }
                final JSONObject modifyUserJSON = new JSONObject();
                final String customerId = TechniciansHandler.getTechniciansInstance(req, modifyUserJSON).validateCustomerId(req, String.valueOf(roleId));
                if (CustomerInfoUtil.getInstance().isMSP() && !MDMStringUtils.isEmpty(customerId)) {
                    secLog.put((Object)"CUSTOMER_IDs", (Object)customerId);
                }
                modifyUserJSON.put("loginID", (Object)loginId);
                modifyUserJSON.put("userName", (Object)userName);
                modifyUserJSON.put("role_ID", (Object)roleId);
                modifyUserJSON.put("mdmScope", mdmScope);
                modifyUserJSON.put("domainName", (Object)domainName);
                modifyUserJSON.put("oldMappedList", (Object)oldMappedList);
                modifyUserJSON.put("sCustomerIDs", (Object)customerId);
                modifyUserJSON.put("USER_EMAIL_ID", (Object)userEmailId);
                modifyUserJSON.put("USER_PH_NO", (Object)phoneNumber);
                modifyUserJSON.put("isTwoFactorEnabledGlobaly", TwoFactorAction.isTwoFactorEnabledGlobaly());
                modifyUserJSON.put("contactinfoID", (Object)contactInfoId);
                modifyUserJSON.put("USER_LOCALE", (Object)userLocale);
                if (DMUserHandler.isDefaultAdministratorRole(roleId) && mdmScope == 1) {
                    throw new APIHTTPException("USR002", new Object[0]);
                }
                String[] cgList = null;
                if (customGroupJSONArray != null && customGroupJSONArray.length() != 0) {
                    cgList = new String[customGroupJSONArray.length()];
                    for (int j = 0; j < customGroupJSONArray.length(); ++j) {
                        cgList[j] = String.valueOf(JSONUtil.optLongForUVH(customGroupJSONArray, j, Long.valueOf(-1L)));
                    }
                }
                modifyUserJSON.put("cgList", (Object)cgList);
                final ArrayList roleIdList = (ArrayList)DMUserHandler.getRoleList(String.valueOf(roleId));
                if (mdmScope == 0) {
                    roleIdList.add(new RoleHandler().getRoleID("All_Managed_Mobile_Devices"));
                }
                modifyUserJSON.put("roleIdsList", (Collection)roleIdList);
                modifyUserJSON.put("loggedOnUserId", (Object)APIUtil.getUserID(req));
                MDMPUserHandler.modifyUserForDC(modifyUserJSON);
                String args;
                String event;
                if (oldDomainName != null && !oldDomainName.equalsIgnoreCase(domainName)) {
                    args = oldDomainName + "@@@" + oldUserName + "@@@" + domainName + "@@@" + userName;
                    event = "dc.admin.uac.USER_DOMAIN_SUCCESSFULLY_MOD";
                }
                else {
                    args = oldDomainName + "@@@" + oldUserName;
                    event = "dc.admin.uac.USER_SUCCESSFULLY_MOD";
                }
                remarks = "update-success";
                DCEventLogUtil.getInstance().addEvent(703, ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName(), (HashMap)null, event, (Object)args, true);
            }
        }
        catch (final Exception e) {
            TechniciansFacade.LOGGER.log(Level.SEVERE, " -- updateTechnicians()  >   Error ", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        finally {
            secLog.put((Object)"REMARKS", (Object)remarks);
            SecurityOneLineLogger.log("User_Management", "MODIFY_USER", secLog, Level.INFO);
        }
    }
    
    public int getTotalTechniciansCount(final Long customerId) {
        int count = 0;
        try {
            final SelectQuery selectQuery = new TechniciansHandler().getUserQuery();
            final Criteria customerCriteria = new Criteria(Column.getColumn("LoginUserCustomerMapping", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria activeCriteria = new Criteria(Column.getColumn("AaaUserStatus", "STATUS"), (Object)"ACTIVE", 0);
            selectQuery.setCriteria(customerCriteria.and(activeCriteria));
            final JSONArray resultJSONArray = JSONUtil.getInstance().convertSimpleJSONarToJSONar(MDMUtil.executeSelectQuery(selectQuery));
            if (resultJSONArray.length() != 0) {
                count = resultJSONArray.length();
            }
        }
        catch (final Exception e) {
            TechniciansFacade.LOGGER.log(Level.SEVERE, " -- getTotalTechniciansCount()   >   Error, ", e);
        }
        return count;
    }
    
    public JSONObject getNotifyConfiguredForUserEmail(final JSONObject message) {
        try {
            final Long userId = APIUtil.getResourceID(message, "technician_id");
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("user_id", (Object)userId);
            final JSONObject userObject = TechniciansHandler.getTechniciansInstance(message, requestJSON).getUserDetails(requestJSON);
            final JSONObject resp = new MDMUserHandler().getNotifyConfiguredForEmail(String.valueOf(userObject.get("user_email")));
            return resp;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            TechniciansFacade.LOGGER.log(Level.WARNING, "Issue on checking if the user email is configured for any process {0}", e2);
            throw new APIHTTPException("COM0004", new Object[] { e2 });
        }
    }
    
    public boolean isEmailExistForOtherUser(final JSONObject apiRequest) {
        try {
            final Long userId = APIUtil.getResourceID(apiRequest, "technician_id");
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("user_id", (Object)userId);
            final JSONObject userObject = TechniciansHandler.getTechniciansInstance(apiRequest, requestJSON).getUserDetails(requestJSON);
            final boolean resp = new TechniciansHandler().isEmailExistForOtherUser(String.valueOf(userObject.get("user_email")), userId);
            return resp;
        }
        catch (final APIHTTPException e) {
            TechniciansFacade.LOGGER.log(Level.WARNING, "Issue on checking if the user email is configured for any process {0}", (Throwable)e);
            throw e;
        }
        catch (final Exception e2) {
            TechniciansFacade.LOGGER.log(Level.WARNING, "Issue on checking if the user email is configured for any process {0}", e2);
            throw new APIHTTPException("COM0004", new Object[] { e2 });
        }
    }
    
    static {
        LOGGER = Logger.getLogger("UserManagementLogger");
    }
}
