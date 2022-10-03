package com.me.ems.framework.common.api.filters;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.admin.AuthenticationKeyUtil;
import java.io.IOException;
import javax.ws.rs.core.SecurityContext;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.ws.rs.core.Response;
import com.me.ems.summaryserver.common.probeadministration.ProbeDetailsAPI;
import java.lang.reflect.Method;
import java.util.logging.Level;
import com.me.ems.framework.common.api.security.APISecurityContext;
import com.me.ems.framework.uac.api.v1.service.CoreUserService;
import com.me.ems.framework.common.api.response.APIResponse;
import java.lang.annotation.Annotation;
import javax.annotation.security.PermitAll;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ResourceInfo;
import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.container.ContainerRequestFilter;

@Provider
@Priority(1000)
public class RestAuthenticationFilter implements ContainerRequestFilter
{
    @Context
    private ResourceInfo resourceInfo;
    @Context
    private HttpServletRequest request;
    private static Logger logger;
    
    public void filter(final ContainerRequestContext containerRequestContext) throws IOException {
        final String authToken = containerRequestContext.getHeaderString("Authorization");
        try {
            final Method method = this.resourceInfo.getResourceMethod();
            Long loginID = this.getLoginIDIFValidAuthToken(authToken);
            final Long loginIDFromMickey = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (loginID == null) {
                final ProbeDetailsAPI probeDetailsAPI = ProbeMgmtFactoryProvider.getProbeDetailsAPI();
                if (probeDetailsAPI != null && probeDetailsAPI.isValidProbeAuthKey(containerRequestContext)) {
                    loginID = loginIDFromMickey;
                }
            }
            if (!method.isAnnotationPresent((Class<? extends Annotation>)PermitAll.class) && loginIDFromMickey == null && loginID == null) {
                final Response unAuthenticated = APIResponse.errorResponse("USER0001");
                containerRequestContext.abortWith(unAuthenticated);
            }
            else if (!method.isAnnotationPresent((Class<? extends Annotation>)PermitAll.class)) {
                boolean isAPILogin = false;
                if (loginID == null) {
                    loginID = loginIDFromMickey;
                }
                else {
                    containerRequestContext.setProperty("isAPILogin", (Object)true);
                    isAPILogin = true;
                }
                final User user = CoreUserService.getInstance().getLoginDataForUser(loginID);
                final SecurityContext dcSecurityContext = (SecurityContext)new APISecurityContext(user, "BASIC");
                containerRequestContext.setSecurityContext(dcSecurityContext);
                if (isAPILogin) {
                    ApiFactoryProvider.getAuthUtilAccessAPI().setUserCredential(user.getName(), "system", user.getDomainName(), user.getUserID());
                }
            }
        }
        catch (final Exception ex) {
            RestAuthenticationFilter.logger.log(Level.SEVERE, "Exception occurred while fetching loginID from Factory Provider", ex);
            final Response unAuthenticated2 = APIResponse.errorResponse("USER0004");
            containerRequestContext.abortWith(unAuthenticated2);
        }
    }
    
    private Long getLoginIDIFValidAuthToken(final String authToken) {
        Long loginID = null;
        if (authToken == null || authToken.trim().equals("")) {
            return null;
        }
        final DataObject authDO = AuthenticationKeyUtil.getInstance().authenticateAPIKey(authToken, "301");
        try {
            if (authDO != null && !authDO.isEmpty()) {
                final Row authRow = authDO.getRow("APIKeyDetails");
                loginID = (Long)authRow.get("LOGIN_ID");
            }
        }
        catch (final Exception ex) {
            RestAuthenticationFilter.logger.log(Level.SEVERE, "Exception occurred while fetching loginID from AuthToken", ex);
        }
        return loginID;
    }
    
    static {
        RestAuthenticationFilter.logger = Logger.getLogger(RestAuthenticationFilter.class.getName());
    }
}
