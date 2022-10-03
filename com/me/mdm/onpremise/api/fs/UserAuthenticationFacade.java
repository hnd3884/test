package com.me.mdm.onpremise.api.fs;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.me.devicemanagement.onpremise.webclient.authentication.ConfirmPasswordAction;
import java.util.Calendar;
import com.me.devicemanagement.onpremise.server.authentication.APIKeyUtil;
import com.me.devicemanagement.onpremise.server.authentication.IntegrationServiceUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.Collection;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.apache.commons.codec.binary.Base64;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;

public class UserAuthenticationFacade
{
    public Logger logger;
    private static String scope_name;
    private static String service_name;
    private static int valid_year;
    
    public UserAuthenticationFacade() {
        this.logger = Logger.getLogger(UserAuthenticationFacade.class.getName());
    }
    
    public JSONObject authenticate(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "Authenticate function started....");
            final JSONObject bodyJson = apiRequest.toJSONObject().getJSONObject("msg_body");
            final String loginName = bodyJson.optString("username", (String)null);
            String password = bodyJson.optString("password", (String)null);
            password = new String(Base64.decodeBase64(password + ""));
            final String domainName = bodyJson.optString("domainname", (String)null);
            final String authType = bodyJson.optString("auth_type", (String)null);
            final boolean isValidAdmin = this.isValidAdmin(loginName, password, domainName);
            if (isValidAdmin) {
                this.logger.log(Level.INFO, "Password authentication success");
                final DMUserHandler handler = new DMUserHandler();
                final long user_id = this.getUserIDFromAAALogin(loginName);
                final HashMap<String, HashMap> userMap = DMUserHandler.getLoginDataForUser(loginName, domainName);
                this.logger.log(Level.INFO, "Generating API key");
                final JSONObject APIKeyProperties = this.generateApiKey(user_id);
                final JSONObject Auth_Data = new JSONObject();
                Auth_Data.put("auth_token", APIKeyProperties.get("API_KEY"));
                final JSONObject Two_Factor_Data = new JSONObject();
                Two_Factor_Data.put("is_TwoFactor_Enabled", false);
                final JSONObject User_Data = new JSONObject();
                User_Data.put("auth_type", (Object)authType);
                User_Data.put("user_id", user_id);
                User_Data.put("user_name", (Object)loginName);
                User_Data.put("phone_number", (Object)"");
                User_Data.put("email", (Object)"");
                final JSONArray Read = new JSONArray((Collection)userMap.get("user_permissions").get("read"));
                final JSONArray Write = new JSONArray((Collection)userMap.get("user_permissions").get("write"));
                final JSONArray Admin = new JSONArray((Collection)userMap.get("user_permissions").get("admin"));
                final JSONObject User_Permissions = new JSONObject();
                User_Permissions.put("read", (Object)Read);
                User_Permissions.put("write", (Object)Write);
                User_Permissions.put("admin", (Object)Admin);
                final JSONObject Authentication = new JSONObject();
                Authentication.put("user_permissions", (Object)User_Permissions);
                Authentication.put("user_data", (Object)User_Data);
                Authentication.put("auth_data", (Object)Auth_Data);
                Authentication.put("two_factor_data", (Object)Two_Factor_Data);
                final JSONObject Message_Response = new JSONObject();
                Message_Response.put("authentication", (Object)Authentication);
                final JSONObject response = new JSONObject();
                response.put("message_type", (Object)"authentication");
                response.put("message_response", (Object)Message_Response);
                response.put("message_version", (Object)"1.0");
                response.put("status", (Object)"success");
                return response;
            }
            this.logger.log(Level.INFO, "Password Authentication failed");
            final JSONObject response2 = new JSONObject();
            response2.put("status", (Object)"Password Authentication failed");
            return response2;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in authenticate: ", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONObject createIntegrationService(final long user_id) {
        try {
            final JSONObject requiredproperties = new JSONObject();
            requiredproperties.put("NAME", (Object)UserAuthenticationFacade.service_name);
            requiredproperties.put("logged_in_user", user_id);
            final IntegrationServiceUtil serviceUtil = new IntegrationServiceUtil();
            return serviceUtil.createIntegrationService(requiredproperties);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in createIntegrationService: ", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONObject generateApiKey(final long userId) {
        try {
            final Long serviceID = this.getServiceID(UserAuthenticationFacade.service_name);
            if (serviceID != null) {
                final Long apiKeyID = this.getAPIKey(serviceID);
                if (apiKeyID != null) {
                    final JSONObject delApiKeyParams = new JSONObject().put("API_KEY_ID", (Object)apiKeyID);
                    APIKeyUtil.getNewInstance().deleteAPIKey(delApiKeyParams);
                }
                final JSONObject delIntegrationParams = new JSONObject().put("SERVICE_ID", (Object)serviceID);
                IntegrationServiceUtil.getNewInstance().deleteIntegrationService(delIntegrationParams);
            }
            final JSONObject integrationServiceProperties = this.createIntegrationService(userId);
            final JSONArray scopeIDs = new JSONArray();
            scopeIDs.put((Object)this.getScopeID(UserAuthenticationFacade.scope_name));
            final Calendar calendar = Calendar.getInstance();
            calendar.add(1, UserAuthenticationFacade.valid_year);
            final JSONObject requiredProperties = new JSONObject();
            requiredProperties.put("USER_ID", userId);
            requiredProperties.put("logged_in_user", userId);
            requiredProperties.put("SERVICE_ID", integrationServiceProperties.get("SERVICE_ID"));
            requiredProperties.put("VALIDITY", calendar.getTimeInMillis());
            requiredProperties.put("scope_ids", (Object)scopeIDs);
            return APIKeyUtil.getNewInstance().createAPIKey(requiredProperties);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in GenerateApikey: ", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private boolean isValidAdmin(final String loginName, final String password, final String domainName) {
        Boolean isValid = Boolean.FALSE;
        final ConfirmPasswordAction checker = new ConfirmPasswordAction();
        if (domainName != null && !domainName.equalsIgnoreCase("-")) {
            this.logger.log(Level.INFO, "User is an AD user");
            isValid = checker.validateADUser(loginName, domainName, password);
        }
        else {
            this.logger.log(Level.INFO, "User is an domain user");
            isValid = checker.validateDCUser(loginName, password);
            if (isValid) {
                final Long loginID = DMUserHandler.getLoginIdForUser(loginName, domainName);
                if (this.isDefaultAdmin(loginID)) {
                    isValid = !DMOnPremiseUserUtil.isDefaultAdminDisabled(loginID);
                }
            }
        }
        return isValid;
    }
    
    public boolean isDefaultAdmin(final Long loginId) {
        boolean isDefaultAdmin;
        try {
            final Long defaultAdminUVHLoginID = DBUtil.getUVHValue("AaaLogin:login_id:0");
            if (defaultAdminUVHLoginID == null || loginId == null) {
                this.logger.log(Level.INFO, "isDefaultAdmin: loginID is null. UVHValue: {0} and input: {1}", new Long[] { defaultAdminUVHLoginID, loginId });
                return false;
            }
            isDefaultAdmin = defaultAdminUVHLoginID.equals(loginId);
        }
        catch (final Exception dae) {
            this.logger.log(Level.INFO, "isDefaultAdmin: Exception getting UVHValue. ", dae);
            return false;
        }
        return isDefaultAdmin;
    }
    
    private Long getScopeID(final String scopename) throws SyMException {
        try {
            final Criteria cri = new Criteria(Column.getColumn("APIKeyScope", "SCOPE_NAME"), (Object)scopename, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("APIKeyScope"));
            sq.setCriteria(cri);
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = DataAccess.get(sq);
            final Long scopeID = (Long)dataObject.getFirstValue("APIKeyScope", "SCOPE_ID");
            return scopeID;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while getting scopeID for scopeName: ", e);
            throw new SyMException(1002, (Throwable)e);
        }
    }
    
    private Long getUserIDFromAAALogin(final String loginName) throws SyMException {
        try {
            final Criteria cri = new Criteria(Column.getColumn("AaaLogin", "NAME"), (Object)loginName, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("AaaLogin"));
            sq.setCriteria(cri);
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = DataAccess.get(sq);
            final Long userID = (Long)dataObject.getFirstValue("AaaLogin", "USER_ID");
            return userID;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, e, () -> "Exception while getting userID for loginName: " + loginName);
            throw new SyMException(1002, (Throwable)e);
        }
    }
    
    private Long getServiceID(final String serviceName) throws Exception {
        try {
            this.logger.log(Level.INFO, "Getting service ID for Service forwarding server");
            final Criteria cri = new Criteria(Column.getColumn("IntegrationService", "NAME"), (Object)serviceName, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("IntegrationService"));
            sq.setCriteria(cri);
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = DataAccess.get(sq);
            Long serviceID = null;
            if (!dataObject.isEmpty()) {
                serviceID = (Long)dataObject.getFirstValue("IntegrationService", "SERVICE_ID");
            }
            this.logger.log(Level.INFO, "service ID for Service forwarding server is {0}", serviceID);
            return serviceID;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, e, () -> "Exception while getting serviceID for serviceName: " + serviceName);
            throw e;
        }
    }
    
    private Long getAPIKey(final long serviceID) throws Exception {
        try {
            this.logger.log(Level.INFO, "Getting API key since it already exists..");
            final Criteria cri = new Criteria(Column.getColumn("APIKeyInfo", "SERVICE_ID"), (Object)serviceID, 0);
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("APIKeyInfo"));
            sq.setCriteria(cri);
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = DataAccess.get(sq);
            Long apiKeyID = null;
            if (!dataObject.isEmpty()) {
                apiKeyID = (Long)dataObject.getFirstValue("APIKeyInfo", "API_KEY_ID");
            }
            return apiKeyID;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, e, () -> "Exception while getting apikey for serviceID: " + serviceID);
            throw e;
        }
    }
    
    static {
        UserAuthenticationFacade.scope_name = "Settings (Read/Write)";
        UserAuthenticationFacade.service_name = "securegatewayserver";
        UserAuthenticationFacade.valid_year = 20;
    }
}
