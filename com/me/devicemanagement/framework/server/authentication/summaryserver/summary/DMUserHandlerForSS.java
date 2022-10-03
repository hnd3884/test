package com.me.devicemanagement.framework.server.authentication.summaryserver.summary;

import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;

public class DMUserHandlerForSS extends DMUserHandler
{
    private static DMUserHandlerForSS dmUserHandlerForSS;
    
    public static DMUserHandlerForSS getInstance() {
        if (DMUserHandlerForSS.dmUserHandlerForSS == null) {
            DMUserHandlerForSS.dmUserHandlerForSS = new DMUserHandlerForSS();
        }
        return DMUserHandlerForSS.dmUserHandlerForSS;
    }
    
    public void addSSUser(final DataObject addUserDO, final JSONObject addUserJObj) throws DataAccessException, JSONException {
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
        this.addUserInAAAUser(addUserDO, userName, description);
        this.addUserInAAALogin(addUserDO, loginName, domainName);
        this.addUserInAAAAccount(addUserDO, serviceId, accountProfileId);
        this.addUserInAAAAuthorizedRole(addUserDO, roleIdsList);
        this.addUserInAAAContactInfo(addUserDO, emailID, phNum);
        this.addUserInAAAUserContactInfo(addUserDO);
        this.addUserInUserSummarymapping(addUserDO, summaryGroupID);
    }
    
    public void updateSSUser(final JSONObject modifyUserJObj) throws Exception {
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
        DMUserHandlerForSS.dmUserHandlerForSS = null;
    }
}
