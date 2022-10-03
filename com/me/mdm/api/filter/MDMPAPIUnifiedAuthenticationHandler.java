package com.me.mdm.api.filter;

import com.me.devicemanagement.onpremise.server.authentication.APIKeyUtil;
import java.util.Iterator;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.List;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.authentication.APIKeyScopeUtil;
import com.adventnet.iam.security.ActionRule;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.me.ems.onpremise.common.authentication.APIUnifiedAuthenticationHandler;

public class MDMPAPIUnifiedAuthenticationHandler extends APIUnifiedAuthenticationHandler
{
    private JSONObject userDetails;
    Logger logger;
    
    public MDMPAPIUnifiedAuthenticationHandler() {
        this.userDetails = null;
        this.logger = Logger.getLogger(MDMPAPIUnifiedAuthenticationHandler.class.getName());
    }
    
    public boolean authentication(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.userDetails = null;
        return super.authentication(request, response);
    }
    
    public boolean authorization(final HttpServletRequest request, final HttpServletResponse response) {
        Boolean authorisedUser = Boolean.FALSE;
        try {
            final ActionRule actionrule = (ActionRule)request.getAttribute("urlrule");
            final String[] configuredRoles = actionrule.getRoles();
            final APIKeyScopeUtil apiKeyScopeUtil = APIKeyScopeUtil.getNewInstance();
            final List<String> userRoles = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
            List<Long> scopeRoles = new ArrayList<Long>();
            if (this.userDetails != null) {
                final List<Long> scopeIds = apiKeyScopeUtil.convertLongJSONArrayTOList(this.userDetails.getJSONArray("scope_ids"));
                scopeRoles = apiKeyScopeUtil.getRolesForScopes((List)scopeIds);
            }
            else if (userRoles != null && !userRoles.isEmpty()) {
                scopeRoles = DMUserHandler.getRoleIdsFromRoleName((List)userRoles);
            }
            if (configuredRoles != null && !scopeRoles.isEmpty()) {
                final List<String> temp = new ArrayList<String>();
                for (final String role : configuredRoles) {
                    temp.add(role.trim());
                }
                final List<Long> endpointRoles = DMUserHandler.getRoleIdsFromRoleName((List)temp);
                for (final Long role2 : endpointRoles) {
                    if (authorisedUser) {
                        break;
                    }
                    authorisedUser = scopeRoles.contains(role2);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in AuthorizationFlag while fetching loginID from Factory Provider", ex);
            return Boolean.FALSE;
        }
        return authorisedUser;
    }
    
    public Long getLoginIDIFValidAuthToken(final String authToken) {
        Long loginID = null;
        if (authToken == null || authToken.trim().equals("")) {
            return null;
        }
        try {
            final JSONObject properties = new JSONObject();
            properties.put("API_KEY", (Object)authToken);
            final JSONObject userDetails = APIKeyUtil.getNewInstance().getUserDetails(properties);
            if (String.valueOf(userDetails.get("status")).equals("success")) {
                this.userDetails = userDetails;
                final Long userId = userDetails.getLong("USER_ID");
                loginID = DMUserHandler.getLoginIdForUserId(userId);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred while fetching loginID from AuthToken", ex);
        }
        return loginID;
    }
}
