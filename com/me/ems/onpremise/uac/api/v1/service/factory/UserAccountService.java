package com.me.ems.onpremise.uac.api.v1.service.factory;

import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import org.json.simple.JSONObject;
import java.util.Map;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.onpremise.uac.api.v1.model.UserDetails;

public interface UserAccountService
{
    Response addUser(final UserDetails p0, final User p1, final HttpServletRequest p2) throws APIException;
    
    Response modifyUser(final UserDetails p0, final User p1, final HttpServletRequest p2) throws APIException;
    
    Response deleteUser(final Long p0, final User p1, final Map<String, Object> p2, final HttpServletRequest p3) throws APIException;
    
    Map<String, Object> getUserDetails(final Long p0) throws APIException;
    
    Map<String, Object> modifyUserContact(final Map<String, Object> p0, final User p1, final HttpServletRequest p2) throws APIException;
    
    Map<String, Object> getUserStartDetails(final User p0) throws APIException;
    
    Map<String, Object> addOrUpdatePassword(final Map p0, final HttpServletRequest p1) throws APIException;
    
    Map<String, Object> sendPasswordLink(final Long p0, final User p1, final Map<String, Object> p2, final HttpServletRequest p3) throws APIException;
    
    void sendPasswordLink(final Map<String, Object> p0, final HttpServletRequest p1) throws APIException;
    
    Map<String, Object> resendUserInvite(final Long p0, final User p1, final Map<String, Object> p2, final HttpServletRequest p3) throws APIException;
    
    default JSONObject getAddOrUpdateUserLogData(final UserDetails userDetails) {
        final JSONObject logData = new JSONObject();
        logData.put((Object)"username_new", (Object)userDetails.getUserName());
        logData.put((Object)"domain_name", (Object)userDetails.getDomainName());
        final int scopeID = userDetails.getComputerScopeType();
        try {
            logData.put((Object)"computer_scope", (Object)((scopeID == 0) ? "all_computers" : ((scopeID == 1) ? "computer_group" : ((scopeID == 2) ? "remote_office" : Integer.valueOf(scopeID)))));
        }
        catch (final Exception exception) {
            logData.put((Object)"computer_scope", (Object)scopeID);
        }
        final int deviceScope = userDetails.getDeviceScopeType();
        try {
            logData.put((Object)"device_scope", (Object)((deviceScope == 0) ? "all_devices" : ((deviceScope == 1) ? "device_group" : Integer.valueOf(deviceScope))));
        }
        catch (final Exception exception2) {
            logData.put((Object)"device_scope", (Object)deviceScope);
        }
        final String roleId = userDetails.getRoleID();
        try {
            logData.put((Object)"role_new", DBUtil.getValueFromDB("UMRole", "UM_ROLE_ID", (Object)roleId, "UM_ROLE_NAME"));
        }
        catch (final Exception exception3) {
            logData.put((Object)"role_new", (Object)roleId);
        }
        logData.put((Object)"auth_type", (Object)userDetails.getAuthType());
        logData.put((Object)"requested_time", (Object)SecurityOneLineLogger.formatTime(Long.valueOf(System.currentTimeMillis())));
        return logData;
    }
    
    Map<String, Object> getTokenDetails(final Long p0) throws APIException;
    
    Map<String, Object> getAddUserDetails(final User p0);
    
    Map<String, Object> checkMailId(final Map<String, Object> p0, final User p1) throws APIException;
    
    Map<String, Object> updateAdminPersonalizationDetails(final Map<String, Object> p0, final User p1, final HttpServletRequest p2) throws APIException;
    
    Map<String, Object> getUserTokenDetails(final Map p0, final HttpServletRequest p1) throws APIException;
}
