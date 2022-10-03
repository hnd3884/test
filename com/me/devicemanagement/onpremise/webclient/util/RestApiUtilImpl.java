package com.me.devicemanagement.onpremise.webclient.util;

import com.me.devicemanagement.framework.server.admin.AuthenticationKeyUtil;
import com.me.devicemanagement.framework.webclient.api.util.DMApi;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.adventnet.iam.security.ActionRule;
import com.adventnet.iam.security.SecurityRequestWrapper;
import com.adventnet.iam.security.SecurityUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.DataAccessException;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.api.util.APIUtil;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.factory.RestAPIUtil;

public class RestApiUtilImpl implements RestAPIUtil
{
    public boolean authenticateRequest(final DataObject apiAuthDO, final Object entity) {
        final boolean skipAuthentication = APIUtil.getInstance().skipAuthenticationForOperation(entity.toString());
        return (apiAuthDO != null && !apiAuthDO.isEmpty()) || !skipAuthentication;
    }
    
    public HashMap fillLoginParams(HashMap parametersList, final DataObject apiAuthDO) throws DataAccessException {
        parametersList = APIUtil.getInstance().fillLoginParams(parametersList, apiAuthDO);
        return parametersList;
    }
    
    public boolean isCSRFAttackPresent(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse, final boolean isValidAuthToken, final Object entity) {
        final String queryString = servletRequest.getQueryString();
        final String fromsdp = servletRequest.getParameter("fromSDP");
        if (!SecurityUtil.isBrowserCookiesDisabled() && queryString != null && (servletRequest.getAttribute("ZSEC_CONTEXT_PATH") != null || servletRequest.getAttribute("ZSEC_API_CONTEXT_PATH") != null)) {
            final String queryParam = SecurityUtil.getCSRFParamName(servletRequest) + "=" + servletRequest.getParameter(SecurityUtil.getCSRFParamName(servletRequest));
            if (queryString.indexOf(queryParam) != -1) {
                throw new RuntimeException("CSRF Parameter should be sent only via  post request and it should not be present in the Query string.");
            }
        }
        if ((servletRequest.getHeader("Authorization") != null || !APIUtil.getInstance().skipAuthenticationForOperation(entity.toString())) && isValidAuthToken) {
            return false;
        }
        if (fromsdp != null && fromsdp.equalsIgnoreCase("true") && isValidAuthToken) {
            return false;
        }
        final SecurityRequestWrapper secureRequest = (SecurityRequestWrapper)servletRequest.getAttribute(SecurityRequestWrapper.class.getName());
        if (secureRequest != null) {
            final ActionRule actionrule = secureRequest.getURLActionRule();
            return !actionrule.validateCSRFToken(secureRequest, servletResponse);
        }
        return false;
    }
    
    public DataObject getAuthDO(final HttpServletRequest servletRequest) throws Exception {
        final String classname = ProductClassLoader.getSingleImplProductClass("DM_API_UTIL");
        DataObject apiAuthDO = null;
        if (classname != null && classname.trim().length() != 0) {
            final DMApi dmApi = (DMApi)Class.forName(classname).newInstance();
            apiAuthDO = dmApi.getAPIKeyDO(servletRequest);
        }
        else {
            final String apiKey = servletRequest.getHeader("Authorization");
            apiAuthDO = AuthenticationKeyUtil.getInstance().authenticateAPIKey(apiKey, "301");
        }
        return apiAuthDO;
    }
}
