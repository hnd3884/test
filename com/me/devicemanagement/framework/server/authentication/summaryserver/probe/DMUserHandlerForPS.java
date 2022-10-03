package com.me.devicemanagement.framework.server.authentication.summaryserver.probe;

import java.util.Iterator;
import java.util.Date;
import com.adventnet.persistence.Row;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;

public class DMUserHandlerForPS extends DMUserHandler
{
    private static DMUserHandlerForPS dmUserHandlerForPS;
    
    public static DMUserHandlerForPS getInstance() {
        if (DMUserHandlerForPS.dmUserHandlerForPS == null) {
            DMUserHandlerForPS.dmUserHandlerForPS = new DMUserHandlerForPS();
        }
        return DMUserHandlerForPS.dmUserHandlerForPS;
    }
    
    public void addPSUser(final DataObject addUserDO, final JSONObject addUserJObj, final JSONObject probeHandlerObject) throws DataAccessException, JSONException {
        final String userName = (String)addUserJObj.get("userName");
        final String loginName = (String)addUserJObj.get("loginName");
        final String emailID = addUserJObj.optString("USER_EMAIL_ID");
        final String phNum = addUserJObj.optString("USER_PH_NO");
        final String domainName = addUserJObj.optString("domainName", "-");
        final String description = addUserJObj.optString("description", (String)null);
        final Long serviceId = (Long)addUserJObj.get("service_id");
        final Long accountProfileId = (Long)addUserJObj.get("accountprofile_id");
        final JSONArray roleIdsArray = (JSONArray)addUserJObj.opt("roleIdsList");
        final List<Long> roleIdsList = DMUserHandler.getIdsFromJsonArray(roleIdsArray);
        final Object summaryGroupID = addUserJObj.get("summaryGroupID");
        final Long userID = probeHandlerObject.getLong("userID");
        final Long loginID = probeHandlerObject.getLong("loginID");
        final Long accountID = probeHandlerObject.getLong("accountID");
        final Long contactInfoID = probeHandlerObject.getLong("contactInfoID");
        this.addPSUserInAAAUser(addUserDO, userName, description, userID);
        this.addPSUserInAAALogin(addUserDO, loginName, domainName, userID, loginID);
        this.addPSUserInAAAAccount(addUserDO, serviceId, accountProfileId, accountID, loginID);
        this.addPSUserInAAAAuthorizedRole(addUserDO, roleIdsList, accountID);
        this.addPSUserInAAAContactInfo(addUserDO, emailID, phNum, contactInfoID);
        this.addPSUserInAAAUserContactInfo(addUserDO, userID, contactInfoID);
        this.addPSUserInUserSummaryMapping(addUserDO, summaryGroupID, loginID);
    }
    
    public void addPSUserInAAAUser(final DataObject newUser, final String username, final String description, final Long userID) throws DataAccessException {
        final Row users = new Row("AaaUser");
        users.set("USER_ID", (Object)userID);
        users.set("FIRST_NAME", (Object)username);
        users.set("CREATEDTIME", (Object)new Date().getTime());
        if (description != null) {
            users.set("DESCRIPTION", (Object)description);
        }
        newUser.addRow(users);
    }
    
    public void addPSUserInAAALogin(final DataObject newUser, final String loginName, String domainName, final Long userID, final Long loginID) throws DataAccessException {
        final Row login = new Row("AaaLogin");
        login.set("NAME", (Object)loginName);
        login.set("USER_ID", (Object)userID);
        login.set("LOGIN_ID", (Object)loginID);
        domainName = ((domainName != null) ? domainName.toLowerCase() : "-");
        login.set("DOMAINNAME", (Object)domainName);
        newUser.addRow(login);
    }
    
    public void addPSUserInAAAAccount(final DataObject newUser, final Long serviceId, final Long accountProfileId, final Long accountId, final long loginId) throws DataAccessException {
        final Row accRow = new Row("AaaAccount");
        accRow.set("ACCOUNT_ID", (Object)accountId);
        accRow.set("LOGIN_ID", (Object)loginId);
        accRow.set("SERVICE_ID", (Object)serviceId);
        accRow.set("ACCOUNTPROFILE_ID", (Object)accountProfileId);
        accRow.set("CREATEDTIME", (Object)System.currentTimeMillis());
        newUser.addRow(accRow);
    }
    
    public void addPSUserInAAAAuthorizedRole(final DataObject newUser, final List<Long> roleIdsList, final Long accountID) throws DataAccessException {
        for (final Long roleID : roleIdsList) {
            final Row accAuthRow = new Row("AaaAuthorizedRole");
            accAuthRow.set("ACCOUNT_ID", (Object)accountID);
            accAuthRow.set("ROLE_ID", (Object)roleID);
            newUser.addRow(accAuthRow);
        }
    }
    
    public void addPSUserInAAAContactInfo(final DataObject newUser, final String emailID, final String phNum, final Long contactInfoID) throws DataAccessException {
        final Row accContactInfoRow = new Row("AaaContactInfo");
        accContactInfoRow.set("CONTACTINFO_ID", (Object)contactInfoID);
        accContactInfoRow.set("EMAILID", (Object)emailID);
        accContactInfoRow.set("LANDLINE", (Object)phNum);
        newUser.addRow(accContactInfoRow);
    }
    
    public void addPSUserInAAAUserContactInfo(final DataObject newUser, final Long userID, final Long contactInfoID) throws DataAccessException {
        final Row accUserContactInfoRow = new Row("AaaUserContactInfo");
        accUserContactInfoRow.set("USER_ID", (Object)userID);
        accUserContactInfoRow.set("CONTACTINFO_ID", (Object)contactInfoID);
        newUser.addRow(accUserContactInfoRow);
    }
    
    public DataObject addPSUserInUserSummaryMapping(final DataObject newUser, final Object summaryGroupID, final Long loginID) throws DataAccessException {
        final Row userSummaryMappingRow = new Row("UserSummaryMapping");
        userSummaryMappingRow.set("SUMMARYGROUP_ID", summaryGroupID);
        userSummaryMappingRow.set("LOGIN_ID", (Object)loginID);
        newUser.addRow(userSummaryMappingRow);
        return newUser;
    }
    
    public void updatePSUser(final JSONObject modifyUserJObj) throws Exception {
        final Long loginID = (Long)modifyUserJObj.get("loginID");
        final JSONArray roleIdsArray = (JSONArray)modifyUserJObj.opt("roleIdsList");
        final List<Long> roleIdsList = DMUserHandler.getIdsFromJsonArray(roleIdsArray);
        if (modifyUserJObj.optString("roleChanged").equalsIgnoreCase("true")) {
            this.updateAAAAuthorizedRole(loginID, roleIdsList);
            final Object summaryGroupID = modifyUserJObj.get("summaryGroupID");
            this.addOrUpdateUserInUserSummaryMapping(loginID, summaryGroupID);
        }
    }
    
    static {
        DMUserHandlerForPS.dmUserHandlerForPS = null;
    }
}
