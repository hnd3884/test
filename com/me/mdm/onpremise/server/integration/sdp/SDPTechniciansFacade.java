package com.me.mdm.onpremise.server.integration.sdp;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.authentication.util.AuthUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import com.me.mdm.onpremise.server.authentication.MDMPUserHandler;
import com.me.devicemanagement.onpremise.winaccess.ADAccessProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.api.APIUtil;
import java.util.Collection;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.user.TechniciansFacade;

public class SDPTechniciansFacade implements TechniciansFacade
{
    private Logger logger;
    
    public SDPTechniciansFacade() {
        this.logger = Logger.getLogger(SDPTechniciansFacade.class.getName());
    }
    
    public void addTechnicians(final JSONObject message) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            final JSONObject body = message.getJSONObject("msg_body");
            final JSONObject addUserObj = new JSONObject();
            if (LicenseProvider.getInstance().isUserLimitReached()) {
                throw new APIHTTPException("COM0023", new Object[0]);
            }
            final String password = body.optString("password", "");
            if (password.equalsIgnoreCase("admin")) {
                throw new APIHTTPException("USR013", new Object[0]);
            }
            final String role = body.optString("role", "Technician");
            final List<Long> roleIdsList = DMUserHandler.getRoleList(DMUserHandler.getRoleID(role));
            final String role_ID = DMUserHandler.getRoleID(role);
            final JSONArray roleIdsListArray = new JSONArray((Collection)roleIdsList);
            final String sCustomerIDs = String.valueOf(APIUtil.getCustomerID(message));
            String userName = (String)body.get("user_name");
            userName = userName.toLowerCase().trim();
            addUserObj.put("userName", (Object)userName);
            addUserObj.put("loginName", (Object)userName);
            addUserObj.put("password", body.get("password"));
            addUserObj.put("role_ID", (Object)role_ID);
            addUserObj.put("USER_EMAIL_ID", (Object)body.optString("email_id", ""));
            addUserObj.put("USER_PH_NO", (Object)body.optString("phone_number", ""));
            addUserObj.put("USER_LOCALE", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale());
            addUserObj.put("sCustomerIDs", (Object)sCustomerIDs);
            final String domainName = body.optString("ad_domain", "-");
            final boolean isValid = ADAccessProvider.getInstance().isValidADObjectName(domainName, userName, 2);
            final Long loginId = DMUserHandler.getLoginIdForUser(userName, domainName);
            if (isValid || (loginId != null && loginId != 0L)) {
                addUserObj.put("domainName", (Object)domainName);
            }
            addUserObj.put("mdmScope", 0);
            final String[] cgList = new String[0];
            addUserObj.put("cgList", (Object)cgList);
            addUserObj.put("roleIdsList", (Object)roleIdsListArray);
            addUserObj.put("isTwoFactorEnabledGlobaly", false);
            addUserObj.put("summaryGroupID", DMUserHandler.getSummaryGroupID(role_ID));
            final Long id = MDMPUserHandler.getInstance().addUserForMDM(addUserObj);
            this.updateLoginPwd(id, String.valueOf(body.get("password")), String.valueOf(body.get("salt")), String.valueOf(System.currentTimeMillis()));
            if (id != null && id != 0L) {
                response.put("status", 202);
            }
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final Exception e2) {
            if (e2 instanceof APIHTTPException) {
                throw (APIHTTPException)e2;
            }
            if (!(e2 instanceof SyMException)) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            if (((SyMException)e2).getErrorCode() == 717) {
                throw new APIHTTPException("COM0010", new Object[0]);
            }
        }
    }
    
    public void removeTechnicians(final JSONObject req) throws APIHTTPException {
        try {
            final Long loginId = JSONUtil.optLongForUVH(req.getJSONObject("msg_body"), "login_id", Long.valueOf(0L));
            final Long userId = JSONUtil.optLongForUVH(req.getJSONObject("msg_body"), "user_id", Long.valueOf(0L));
            MDMPUserHandler.getInstance().deleteUser(loginId, userId);
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getTechnicians(final JSONObject message) throws APIHTTPException {
        return null;
    }
    
    public void updateTechnicians(final JSONObject req) {
        try {
            final String email = req.getJSONObject("msg_body").optString("email_id", "");
            final Long userId = JSONUtil.optLongForUVH(req.getJSONObject("msg_body"), "user_id", Long.valueOf(0L));
            MDMPUserHandler.getInstance().addorUpdateContactEmail(userId, email);
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public int getTotalTechniciansCount(final Long customerId) {
        return 0;
    }
    
    private void updateLoginPwd(final Long loginID, final String pswd, final String salt, final String createdTime) {
        try {
            final SelectQueryImpl sq = new SelectQueryImpl(Table.getTable("AaaLogin"));
            final Join join = new Join("AaaLogin", "AaaAccount", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
            final Join join2 = new Join("AaaAccount", "AaaAccPassword", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2);
            final Join join3 = new Join("AaaAccPassword", "AaaPassword", new String[] { "PASSWORD_ID" }, new String[] { "PASSWORD_ID" }, 2);
            final Criteria crit = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            sq.addJoin(join);
            sq.addJoin(join2);
            sq.addJoin(join3);
            sq.setCriteria(crit);
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject userNameDO = MDMUtil.getPersistence().get((SelectQuery)sq);
            if (userNameDO.size("AaaAccPassword") > 0) {
                final Row passRow = userNameDO.getRow("AaaPassword");
                passRow.set("PASSWORD", (Object)pswd);
                passRow.set("ALGORITHM", (Object)"MD5");
                passRow.set("SALT", (Object)salt);
                passRow.set("CREATEDTIME", (Object)createdTime);
                passRow.set("PASSWDPROFILE_ID", (Object)AuthUtil.getAccountProfileId("Profile 2"));
                userNameDO.updateRow(passRow);
                MDMUtil.getPersistence().update(userNameDO);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Expection in updateLoginPswd", e);
        }
    }
    
    public JSONObject getNotifyConfiguredForUserEmail(final JSONObject message) {
        return null;
    }
}
