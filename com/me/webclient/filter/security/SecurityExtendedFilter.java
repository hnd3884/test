package com.me.webclient.filter.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.iam.security.ActionRule;
import com.adventnet.iam.security.IAMSecurityException;
import com.me.mdm.api.error.APIError;
import javax.servlet.http.HttpServletResponse;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.ArrayList;
import java.util.List;
import com.me.mdm.onpremise.server.authentication.MDMPUserHandler;
import com.me.devicemanagement.onpremise.server.authentication.APIKeyUtil;
import com.me.devicemanagement.onpremise.server.authentication.APIKeyScopeUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.regex.Pattern;
import com.adventnet.iam.security.SecurityUtil;
import com.adventnet.iam.security.SecurityRequestWrapper;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class SecurityExtendedFilter implements Filter
{
    private static Logger logger;
    private String authorisationEnabled;
    private static final String APIREGEX = "^(\\/api\\/v\\d+\\/mdm\\/.*$)";
    private static final String FSAPIREGEX = "^(\\/api\\/1.2\\/fwserver\\/.*$)";
    private static final String MDMAPIJERSEYREGEX = "^(\\/api\\/mdm\\/.*$)";
    private static final String EMSAPIREGEX = "^(\\/emsapi\\/.*$)";
    
    public SecurityExtendedFilter() {
        this.authorisationEnabled = "true";
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        final String authorisationParam = filterConfig.getInitParameter("authorisation");
        if (authorisationParam != null) {
            this.authorisationEnabled = authorisationParam;
        }
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final SecurityRequestWrapper secureRequest = (SecurityRequestWrapper)servletRequest;
        if (secureRequest != null) {
            final ActionRule actionrule = secureRequest.getURLActionRule();
            final Boolean uiOnly = actionrule.getCustomAttribute("ui-only") != null && actionrule.getCustomAttribute("ui-only").equals("true");
            final Boolean apiOnly = actionrule.getCustomAttribute("api-only") != null && actionrule.getCustomAttribute("api-only").equals("true");
            final String authenticationMode = actionrule.getCustomAttribute("authentication");
            if (this.authorisationEnabled.equalsIgnoreCase("true") && authenticationMode != null && !authenticationMode.equalsIgnoreCase("public")) {
                final String requestURI = SecurityUtil.getNormalizedURI(secureRequest.getRequestURI());
                final boolean isAPI = Pattern.matches("^(\\/emsapi\\/.*$)", requestURI) || Pattern.matches("^(\\/api\\/v\\d+\\/mdm\\/.*$)", requestURI) || Pattern.matches("^(\\/api\\/1.2\\/fwserver\\/.*$)", requestURI) || Pattern.matches("^(\\/api\\/mdm\\/.*$)", requestURI);
                boolean authorisedUser = false;
                final String[] configuredRoles = actionrule.getRoles();
                String userName = null;
                try {
                    userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
                }
                catch (final Exception e) {
                    SecurityExtendedFilter.logger.log(Level.SEVERE, "exception while getting userName", e);
                }
                if (isAPI && (userName == null || apiOnly) && !uiOnly) {
                    boolean authenticatedUser = false;
                    try {
                        final String apiKey = secureRequest.getHeader("Authorization");
                        if (apiKey != null) {
                            final JSONObject properties = new JSONObject();
                            final APIKeyScopeUtil apiKeyScopeUtil = APIKeyScopeUtil.getNewInstance();
                            properties.put("API_KEY", (Object)apiKey);
                            final JSONObject userDetails = APIKeyUtil.getNewInstance().getUserDetails(properties);
                            if (String.valueOf(userDetails.get("status")).equals("success")) {
                                authenticatedUser = true;
                                final List<Long> scopeIds = apiKeyScopeUtil.convertLongJSONArrayTOList(userDetails.getJSONArray("scope_ids"));
                                final Long userId = userDetails.getLong("USER_ID");
                                final DataObject loginDo = MDMPUserHandler.getLoginDoForUserId(userId);
                                String domainName = null;
                                if (!loginDo.isEmpty()) {
                                    final Row loginRow = loginDo.getRow("AaaLogin");
                                    domainName = (String)loginRow.get("DOMAINNAME");
                                    domainName = ((domainName == null || domainName.isEmpty() || domainName.equalsIgnoreCase("-")) ? null : domainName);
                                    final Row userRow = loginDo.getRow("AaaUser");
                                    userName = (String)userRow.get("FIRST_NAME");
                                }
                                final List<Long> scopeRoles = apiKeyScopeUtil.getRolesForScopes((List)scopeIds);
                                if (configuredRoles != null) {
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
                                else {
                                    authorisedUser = true;
                                }
                                if (authorisedUser) {
                                    ApiFactoryProvider.getAuthUtilAccessAPI().setUserCredential(userName, "System", domainName, userId);
                                }
                            }
                        }
                    }
                    catch (final Exception e2) {
                        SecurityExtendedFilter.logger.log(Level.SEVERE, "Exception while validating api key", e2);
                    }
                    if (!authenticatedUser && authenticationMode.equalsIgnoreCase("required")) {
                        final HttpServletResponse response = (HttpServletResponse)servletResponse;
                        response.setStatus(401);
                        response.setHeader("Content-Type", "application/json;charset=UTF-8");
                        final PrintWriter pout = response.getWriter();
                        pout.print(new APIError("COM0028").toJSONObject().toString());
                        pout.close();
                        return;
                    }
                    if (!authorisedUser && authenticationMode.equalsIgnoreCase("required")) {
                        final HttpServletResponse response = (HttpServletResponse)servletResponse;
                        response.setStatus(403);
                        response.setHeader("Content-Type", "application/json;charset=UTF-8");
                        final PrintWriter pout = response.getWriter();
                        pout.print(new APIError("COM0013").toJSONObject().toString());
                        pout.close();
                        return;
                    }
                }
                else if (configuredRoles != null) {
                    try {
                        for (final String role3 : configuredRoles) {
                            if (authorisedUser) {
                                break;
                            }
                            authorisedUser = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles().contains(role3.trim());
                        }
                    }
                    catch (final Exception e) {
                        SecurityExtendedFilter.logger.log(Level.SEVERE, "Exception while getting roles : {0}", e);
                    }
                }
                else {
                    authorisedUser = true;
                }
                if (!authorisedUser && authenticationMode.equalsIgnoreCase("required")) {
                    if (isAPI) {
                        final HttpServletResponse response2 = (HttpServletResponse)servletResponse;
                        response2.setStatus(403);
                        response2.setHeader("Content-Type", "application/json;charset=UTF-8");
                        final PrintWriter pout2 = response2.getWriter();
                        pout2.print(new APIError("COM0013").toJSONObject().toString());
                        pout2.close();
                        return;
                    }
                    throw new IAMSecurityException("UNAUTHORISED ENTRY FOR THE URL" + actionrule.getPath());
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
    
    public void destroy() {
    }
    
    static {
        SecurityExtendedFilter.logger = Logger.getLogger(SecurityExtendedFilter.class.getName());
    }
}
