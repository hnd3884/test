package com.me.devicemanagement.onpremise.webclient.sdp;

import java.util.HashMap;
import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.io.OutputStreamWriter;
import com.me.devicemanagement.framework.server.util.Encoder;
import java.net.URL;
import javax.servlet.http.Cookie;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.me.devicemanagement.framework.server.security.DMCookieUtil;
import javax.servlet.ServletContext;
import java.security.Principal;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.authentication.PAM;
import java.net.URLEncoder;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.admin.AuthenticationKeyUtil;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import org.apache.commons.codec.binary.Base64;
import java.util.ArrayList;
import javax.net.ssl.SSLHandshakeException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.webclient.common.QuickLoadUtil;
import com.me.devicemanagement.framework.server.util.SoMADUtil;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.mesolutions.notification.SDPNotificationUtil;
import com.me.devicemanagement.onpremise.server.mesolutions.util.SolutionUtil;
import com.me.devicemanagement.framework.webclient.api.util.DMApi;
import org.json.JSONObject;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.me.devicemanagement.onpremise.server.alerts.AlertConstants;
import com.me.devicemanagement.framework.server.alerts.AlertsUtil;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;
import com.me.ems.onpremise.common.authentication.factory.AuthenticationFactoryProvider;
import com.me.devicemanagement.onpremise.server.sdp.DCCredentialHandler;
import javax.servlet.http.HttpServletResponse;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.admin.DMJiraAPI;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;
import java.util.Hashtable;
import javax.servlet.http.HttpSession;
import com.me.devicemanagement.onpremise.server.sdp.Ticket;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.catalina.valves.ValveBase;

public class DCAuthenticateRemote extends ValveBase
{
    private static Logger logger;
    public static Map<String, Ticket> pluginTicketCacheMap;
    public static Map<String, HttpSession> pluginTicketToSessionMap;
    public static Map<String, String> pluginSessionToTicketMap;
    public static Hashtable principalMap;
    private static String isXFrameEnabled;
    private static String xFrameOptionsValue;
    private static final String ENCODING = "UTF-8";
    
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        final HttpServletResponse res = (HttpServletResponse)response;
        final String reqURI = SecurityUtil.getNormalizedRequestURI(req);
        req.setCharacterEncoding("UTF-8");
        this.setIfRequestFromTomcat((HttpServletRequest)request);
        Label_0122: {
            if (reqURI != null) {
                if (!reqURI.endsWith(".png") && !reqURI.endsWith(".jpg") && !reqURI.endsWith(".css") && !reqURI.endsWith(".js") && !reqURI.endsWith(".gif")) {
                    if (!reqURI.contains("ServerStatusServlet")) {
                        break Label_0122;
                    }
                }
                try {
                    this.getNext().invoke(request, response);
                }
                catch (final Exception e1) {
                    DCAuthenticateRemote.logger.log(Level.WARNING, "Exception invoking next valve");
                    e1.printStackTrace();
                }
                return;
            }
        }
        DCAuthenticateRemote.logger.log(Level.FINE, "************************** invoke() Started ************************** ", reqURI);
        DCAuthenticateRemote.logger.log(Level.FINE, "invoke() The request URI is {0} ", reqURI);
        final String queryString = req.getQueryString();
        DCAuthenticateRemote.logger.log(Level.FINE, "The incoming request params are {0} ", queryString);
        DCAuthenticateRemote.logger.log(Level.FINE, "session exits pluginTicketToSessionMap : {0} ", DCAuthenticateRemote.pluginTicketToSessionMap.containsValue(req.getSession()));
        Ticket t = null;
        String ticket = req.getParameter("ticket");
        final String action = req.getParameter("action");
        DCAuthenticateRemote.logger.log(Level.FINEST, "action : " + action);
        if (ticket != null) {
            if (req.getSession().getAttribute("isJiraLogin") == null && req.getParameter("isJiraLogin") != null) {
                if (req.getParameter("iframe") != null && req.getParameter("iframe").equalsIgnoreCase("true")) {
                    String[] classNames = null;
                    try {
                        classNames = ProductClassLoader.getMultiImplProductClass("DM_JIRA_CLASS");
                        DMJiraAPI dmJiraAPI = null;
                        if (classNames.length != 0) {
                            for (final String className : classNames) {
                                dmJiraAPI = (DMJiraAPI)Class.forName(className).newInstance();
                                dmJiraAPI.addMETrackingForJiraIframe();
                            }
                        }
                    }
                    catch (final Exception e2) {
                        e2.printStackTrace();
                    }
                }
                req.getSession().setAttribute("isJiraLogin", (Object)"PLUGIN_LOGIN");
                ApiFactoryProvider.getCacheAccessAPI().putCache("isZendeskLogin", (Object)"PLUGIN_LOGIN");
            }
            if (req.getSession().getAttribute("isZendeskLogin") == null && req.getParameter("isZendeskLogin") != null) {
                req.getSession().setAttribute("isZendeskLogin", (Object)"PLUGIN_LOGIN");
                ApiFactoryProvider.getCacheAccessAPI().putCache("isZendeskLogin", (Object)"PLUGIN_LOGIN");
            }
            if (req.getSession().getAttribute("isPuginLogin") == null && req.getParameter("integrationMode") != null) {
                req.getSession().setAttribute("isPuginLogin", (Object)"PLUGIN_LOGIN");
                ApiFactoryProvider.getCacheAccessAPI().putCache("isPuginLogin", (Object)"PLUGIN_LOGIN");
                req.getSession().setAttribute("sdpLoginEnabled", (Object)"true");
                ApiFactoryProvider.getCacheAccessAPI().putCache("sdpLoginEnabled", (Object)"true");
            }
            if (req.getParameter("accessProbe") != null) {
                if (req.getSession().getAttribute("isSSLogin") == null) {
                    req.getSession().setAttribute("isSSLogin", (Object)"true");
                }
                ApiFactoryProvider.getCacheAccessAPI().putCache("isSSLogin", (Object)"true");
            }
            if (req.getParameter("MDMPSDPIntegrationMode") != null) {
                req.getSession().setAttribute("isPuginLogin", (Object)"PLUGIN_LOGIN");
                ApiFactoryProvider.getCacheAccessAPI().putCache("sdpLoginEnabled", (Object)"true");
                req.getSession().setAttribute("sdpLoginEnabled", (Object)"true");
                ApiFactoryProvider.getCacheAccessAPI().putCache("isPuginLogin", (Object)"PLUGIN_LOGIN");
            }
        }
        final String fromJumpTo = request.getParameter("jumpto");
        if (fromJumpTo != null && fromJumpTo.equalsIgnoreCase("true")) {
            req.getSession().setAttribute("jumpto", (Object)"true");
            ApiFactoryProvider.getCacheAccessAPI().putCache("jumpto", (Object)"true");
            String baseUrl = request.getParameter("baseurl");
            if (baseUrl != null) {
                baseUrl = baseUrl.replace("servlet/SDAjaxServlet", "");
            }
            req.getSession().setAttribute("baseurl", (Object)baseUrl);
            ApiFactoryProvider.getCacheAccessAPI().putCache("baseurl", (Object)baseUrl);
        }
        else if (fromJumpTo != null && fromJumpTo.equalsIgnoreCase("false")) {
            req.getSession().setAttribute("jumpto", (Object)"false");
            ApiFactoryProvider.getCacheAccessAPI().putCache("jumpto", (Object)"false");
        }
        else {
            String isJumpTo = (String)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute((HttpServletRequest)request, "jumpto");
            if (isJumpTo == null) {
                isJumpTo = "false";
            }
            else if (isJumpTo.equalsIgnoreCase("true")) {
                isJumpTo = "true";
            }
            ApiFactoryProvider.getCacheAccessAPI().putCache("jumpto", (Object)isJumpTo);
        }
        final String isPluginLogin = (String)req.getSession().getAttribute("isPuginLogin");
        if ((isPluginLogin == null || !isPluginLogin.equals("PLUGIN_LOGIN")) && req.getSession().getAttribute("isZendeskLogin") == null && req.getSession().getAttribute("isJiraLogin") == null && req.getSession().getAttribute("isSSLogin") == null) {
            DCAuthenticateRemote.logger.log(Level.FINE, "Login from DC hence invokeNext called");
            try {
                addXFrameHeader((HttpServletRequest)request, (HttpServletResponse)response);
                this.getNext().invoke(request, response);
                final String username = request.getParameter("j_username");
                this.addUserAlerts((HttpServletRequest)request, username);
            }
            catch (final Exception e3) {
                DCAuthenticateRemote.logger.log(Level.WARNING, "Exception invoking next valve");
                e3.printStackTrace();
            }
            return;
        }
        final String localSessionKey = DCAuthenticateRemote.pluginSessionToTicketMap.get(req.getSession().getId());
        if (ticket == null) {
            ticket = localSessionKey;
        }
        if (ticket != null && localSessionKey != null && !ticket.equalsIgnoreCase(localSessionKey)) {
            DCAuthenticateRemote.pluginSessionToTicketMap.remove(req.getSession().getId());
        }
        else if (ticket != null && localSessionKey == null) {
            final Iterator ticketKeys = DCAuthenticateRemote.pluginSessionToTicketMap.keySet().iterator();
            while (ticketKeys.hasNext()) {
                final String oldSessionID = ticketKeys.next().toString();
                final String oldTicketKey = DCAuthenticateRemote.pluginSessionToTicketMap.get(oldSessionID);
                if (oldTicketKey.equalsIgnoreCase(ticket)) {
                    DCAuthenticateRemote.pluginSessionToTicketMap.remove(oldSessionID);
                    break;
                }
            }
        }
        if (DCCredentialHandler.localUserRoleMap == null || DCCredentialHandler.localUserRoleMap.size() <= 0) {
            DCCredentialHandler.init();
        }
        if (ticket != null) {
            t = DCAuthenticateRemote.pluginTicketCacheMap.get(ticket);
            if (t == null || (DCAuthenticateRemote.pluginTicketToSessionMap.containsKey(ticket) && !DCAuthenticateRemote.pluginTicketToSessionMap.get(ticket).equals(req.getSession()))) {
                if (req.getSession().getAttribute("isJiraLogin") != null) {
                    t = validateJiraSession(ticket, req, res);
                }
                if (req.getSession().getAttribute("isZendeskLogin") != null) {
                    t = validateZendeskKey(ticket, req, res);
                }
                if (req.getSession().getAttribute("isPuginLogin") != null) {
                    t = validateTicket(ticket, req, res);
                }
                if (req.getSession().getAttribute("isSSLogin") != null) {
                    t = AuthenticationFactoryProvider.getWebclientAuthenticationImpl().validateTicketForSSO(ticket, req, res, false);
                }
                if (t != null) {
                    DCAuthenticateRemote.pluginTicketCacheMap.put(ticket, t);
                    DCAuthenticateRemote.pluginTicketToSessionMap.put(ticket, req.getSession());
                    DCAuthenticateRemote.pluginSessionToTicketMap.put(req.getSession().getId(), ticket);
                    if (req.getSession().getAttribute("isDemoMode") == null) {
                        final boolean isDemoMode = ApiFactoryProvider.getDemoUtilAPI().isDemoMode();
                        req.getSession().setAttribute("isDemoMode", (Object)String.valueOf(isDemoMode));
                        if (isDemoMode) {
                            req.getSession().setAttribute("demoModeMessage", (Object)"Running in restricted mode");
                        }
                    }
                }
            }
        }
        if (t == null) {
            DCAuthenticateRemote.logger.log(Level.WARNING, "no credential is present. Have to authenticate the request.");
            if (action != null && (action.equals("sdplogout") || action.equals("dcpluginlogout"))) {
                final HttpSession session = DCAuthenticateRemote.pluginTicketToSessionMap.remove(ticket);
                if (session != null) {
                    DCAuthenticateRemote.pluginSessionToTicketMap.remove(session.getId());
                    DCAuthenticateRemote.pluginTicketCacheMap.remove(ticket);
                    session.invalidate();
                }
            }
            this.getNext().invoke(request, response);
            return;
        }
        if (action != null && (action.equals("sdplogout") || action.equals("dcpluginlogout"))) {
            DCAuthenticateRemote.pluginTicketCacheMap.remove(ticket);
            final HttpSession session = DCAuthenticateRemote.pluginTicketToSessionMap.remove(ticket);
            session.invalidate();
            return;
        }
        req = (HttpServletRequest)DCCredentialHandler.handle(req, t);
        try {
            this.invokeDCValve(request, response, t, ticket, localSessionKey);
        }
        catch (final Exception e4) {
            e4.printStackTrace();
            this.getNext().invoke(request, response);
            return;
        }
        DCAuthenticateRemote.logger.log(Level.FINE, "************************** invoke() End here ************************** ", reqURI);
    }
    
    private void addUserAlerts(final HttpServletRequest hreq, final String username) {
        final String contextPath = hreq.getContextPath();
        final String requestURI = SecurityUtil.getNormalizedRequestURI(hreq);
        final boolean loginAction = requestURI.startsWith(contextPath) && (requestURI.endsWith("/j_security_check") || requestURI.endsWith("/two_fact_auth"));
        if (loginAction) {
            String loginStatus = (String)hreq.getAttribute("login_status");
            if (loginStatus == null) {
                loginStatus = (String)hreq.getSession().getAttribute("login_status");
            }
            loginStatus = ((loginStatus != null) ? loginStatus.toLowerCase() : "");
            DCAuthenticateRemote.logger.log(Level.INFO, "login status: {0}", loginStatus);
            if (loginStatus.startsWith("no such account configured")) {
                AlertsUtil.getInstance().addAlert(AlertConstants.UNKNOWN_USER_ALERT, "ems.user.security.unknown_user_login", (Object)username);
            }
            else if (loginStatus.startsWith("no rows found for the table") || loginStatus.contains("exception") || loginStatus.startsWith("invalid loginname/password")) {
                AlertsUtil.getInstance().addAlert(AlertConstants.USER_INCORRECT_PASSWORD_ALERT, "ems.user.security.user_password_incorrect", (Object)username);
                final Integer loginAttemptLeft = (Integer)hreq.getAttribute("login_attempts_left");
                if (loginAttemptLeft == 1) {
                    JSONObject passwordPolicyDetails = DMOnPremiseUserUtil.getPasswordPolicyDetails();
                    if (passwordPolicyDetails == null || passwordPolicyDetails.length() <= 0) {
                        passwordPolicyDetails = DMOnPremiseUserUtil.getDefaultPasswordPolicy();
                    }
                    String lockOutDuration = "temporarily";
                    if (passwordPolicyDetails.getBoolean("ENABLE_LOGIN_RESTRICTION")) {
                        final Integer lockOutTime = passwordPolicyDetails.getInt("LOCK_PERIOD");
                        lockOutDuration = lockOutTime + " minutes";
                    }
                    final String remarks = username + "@@@" + lockOutDuration;
                    AlertsUtil.getInstance().addAlert(AlertConstants.USER_ACCOUNT_LOCKED_ALERT, "ems.user.security.user_locked_to_login", (Object)remarks);
                }
                else if (loginAttemptLeft < 1) {
                    AlertsUtil.getInstance().addAlert(AlertConstants.USER_ACCOUNT_LOCKED_ALERT, "ems.user.security.user_blocked_to_login", (Object)username);
                }
            }
            else if (loginStatus.indexOf("badlogin") > -1) {
                AlertsUtil.getInstance().addAlert(AlertConstants.USER_ACCOUNT_LOCKED_ALERT, "ems.user.security.user_blocked_to_login", (Object)username);
            }
        }
    }
    
    public static Ticket validateTicket(final String ticket, final HttpServletRequest request, final HttpServletResponse res) {
        Ticket t = null;
        try {
            final String classname = ProductClassLoader.getSingleImplProductClass("DM_API_UTIL");
            String servletStr = "";
            if (classname != null && classname.trim().length() != 0) {
                final DMApi dmApi = (DMApi)Class.forName(classname).newInstance();
                servletStr = dmApi.urltoValidateSDPticket();
            }
            final StringBuffer urlStr = new StringBuffer(servletStr);
            urlStr.append("?");
            if (servletStr != null && servletStr.contains("/DCIntegrationServlet")) {
                urlStr.append("operation=validateticket");
            }
            else {
                urlStr.append("action=validateticket");
            }
            urlStr.append("&");
            urlStr.append("ticket").append("=").append(encode(ticket));
            final String baseServerType = request.getParameter("pname");
            final String appname = SolutionUtil.getInstance().getProductAppName(baseServerType);
            request.getSession().setAttribute("appname", (Object)appname);
            final String applicationServerUrl = SDPNotificationUtil.getApplicationServerURL(appname);
            final String postPath = applicationServerUrl + "/" + urlStr.toString();
            Properties p = new Properties();
            final boolean isDemoMode = ApiFactoryProvider.getDemoUtilAPI().isDemoMode();
            if (isDemoMode) {
                DCAuthenticateRemote.logger.log(Level.INFO, "\n ***************************\n isDemoMode is " + isDemoMode + " Hence going to set default property");
                p = setDemoProp();
            }
            else {
                p = validateAndSetProp(postPath);
            }
            final boolean result = Boolean.valueOf(p.getProperty("RESULT"));
            if (!result) {
                DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() The validation result is false");
                return null;
            }
            final String sdpBuildNumber = p.getProperty("SDP_BUILD_NUMBER");
            if (sdpBuildNumber != null) {
                request.getSession().setAttribute("SDP_BUILD_NUMBER", (Object)sdpBuildNumber);
                final String previousBuilNumber = SolutionUtil.getInstance().getIntegrationParamsValue("SDP_BUILD_NUMBER");
                DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() previousBuilNumber : " + previousBuilNumber + " sdpBuildNumber : " + sdpBuildNumber);
                if (previousBuilNumber == null || !previousBuilNumber.equalsIgnoreCase(sdpBuildNumber)) {
                    SolutionUtil.getInstance().updateIntegrationParameter("SDP_BUILD_NUMBER", sdpBuildNumber);
                }
            }
            DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() The validation result is true Properties : " + p);
            t = new Ticket();
            t.ticket = ticket;
            final String pluginLoginName = p.getProperty("loginUser");
            String domainName = p.getProperty("domainName");
            t.principal = pluginLoginName;
            if (domainName != null && !domainName.equalsIgnoreCase("-")) {
                DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() The validation pluginLoginName : {0} domainName {1} ", new Object[] { pluginLoginName, domainName });
                domainName = SoMADUtil.getInstance().getManagedDomain(domainName);
                DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() The validation After Getting from DB pluginLoginName : {0} domainName {1} ", new Object[] { pluginLoginName, domainName });
                t.domainName = domainName.toLowerCase();
            }
            else {
                domainName = null;
            }
            final String role = p.getProperty("ROLES");
            final String reqURI = SecurityUtil.getNormalizedRequestURI(request);
            final boolean userStatus = DMOnPremiseUserUtil.isActiveUser(pluginLoginName, domainName, reqURI);
            if (!userStatus) {
                DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() User is Disabled user");
                QuickLoadUtil.redirectURL("/jsp/admin/iframeUserMessage.jsp?username=" + pluginLoginName + "&domainname=" + domainName, request, res);
                return null;
            }
            final ArrayList roles = getDCRoles(pluginLoginName, domainName);
            DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() The validation roles : {0} ", roles);
            if (roles == null) {
                QuickLoadUtil.redirectURL("/jsp/admin/iframeUserMessage.jsp?username=" + pluginLoginName + "&domainname=" + domainName, request, res);
                return null;
            }
            final StringBuffer tempString = new StringBuffer();
            for (int i = 0; i < roles.size(); ++i) {
                if (i == 0) {
                    tempString.append(role + ";" + roles.get(i));
                }
                else {
                    tempString.append(";" + roles.get(i));
                }
            }
            DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() DC roles from local cache::" + tempString.toString());
            t.roles = tempString.toString();
            t.ipaddress = request.getRemoteAddr();
            t.properties = p;
            DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() The ticket upon validation is :" + t);
            return t;
        }
        catch (final SSLHandshakeException e) {
            DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() Exception while validating the ticket:", e);
            try {
                final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
                if (!isMSP) {
                    QuickLoadUtil.redirectURL("/jsp/admin/iframeHttpsMessage.jsp", request, res);
                }
            }
            catch (final Exception ee) {
                ee.printStackTrace();
            }
        }
        catch (final Exception e2) {
            e2.printStackTrace();
            DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() Exception while validating the ticket:" + e2);
        }
        finally {
            DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() Exit from validation");
        }
        return null;
    }
    
    protected static String getDCMappedUserName(String pluginLoginName, final String domainName) {
        final ArrayList roles = DCCredentialHandler.getUserRole(pluginLoginName, domainName);
        DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() The validation roles : {0} ", roles);
        if (roles == null && domainName == null && pluginLoginName != null && pluginLoginName.equalsIgnoreCase("administrator")) {
            pluginLoginName = "admin";
        }
        return pluginLoginName;
    }
    
    private static Ticket validateZendeskKey(final String ticket, final HttpServletRequest req, final HttpServletResponse res) {
        final Ticket t = new Ticket();
        try {
            final String zendeskUserId = req.getParameter("zendesk_user_id");
            final int serviceTypeForRestAPI = 301;
            final Properties dcLoginProps = ApiFactoryProvider.getZendeskAPI().getUserMappedToZendesk(Long.valueOf(zendeskUserId));
            if (dcLoginProps == null || dcLoginProps.getProperty("loginName") == null) {
                DCAuthenticateRemote.logger.log(Level.WARNING, "DCLOGINPROPS - " + dcLoginProps + " DCLOGINPROPS-PROPERTY:" + dcLoginProps.getProperty("LoginName"));
                return null;
            }
            final String loginId = String.valueOf(((Hashtable<K, Object>)dcLoginProps).get("userLoginId"));
            final String apiKey = ApiFactoryProvider.getZendeskAPI().fetchAPIKeyForTech(Long.valueOf(loginId), serviceTypeForRestAPI);
            final String encodedKeyVal = Base64.encodeBase64String(ticket.getBytes());
            if (apiKey.equals(encodedKeyVal)) {
                final String loginName = dcLoginProps.getProperty("loginName");
                String domainName = null;
                t.ticket = ticket;
                t.principal = dcLoginProps.getProperty("loginName");
                if (dcLoginProps.getProperty("domainName") != null && !dcLoginProps.getProperty("domainName").equalsIgnoreCase("")) {
                    t.domainName = dcLoginProps.getProperty("domainName");
                    domainName = dcLoginProps.getProperty("domainName");
                }
                ArrayList roles = DCCredentialHandler.getUserRole(loginName, domainName);
                final String role = "";
                final StringBuffer tempString = new StringBuffer();
                if (roles == null) {
                    DCCredentialHandler.init();
                    roles = DCCredentialHandler.getUserRole(loginName, domainName);
                }
                for (int i = 0; i < roles.size(); ++i) {
                    if (i == 0) {
                        tempString.append(role + ";" + roles.get(i));
                    }
                    else {
                        tempString.append(";" + roles.get(i));
                    }
                }
                DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() DC roles from local cache::" + tempString.toString());
                t.roles = tempString.toString();
                t.ipaddress = req.getRemoteAddr();
                return t;
            }
            DCAuthenticateRemote.logger.log(Level.WARNING, "API Key Mismatch while logging from Zendesk");
            return null;
        }
        catch (final Exception e) {
            DCAuthenticateRemote.logger.log(Level.WARNING, "Exception occured while authenticating Zendesk Token: " + e);
            return null;
        }
    }
    
    private static Ticket validateJiraSession(final String ticket, final HttpServletRequest req, final HttpServletResponse res) {
        final Ticket t = new Ticket();
        final String DM_JIRA_API = "DM_JIRA_CLASS";
        try {
            boolean isJiraConfigured = true;
            final String serverName = ((Hashtable<K, String>)SyMUtil.getDCServerInfo()).get("SERVER_MAC_NAME");
            int port = SyMUtil.getWebServerPort();
            String http_type = "http";
            final String enableHttps = SyMUtil.getSyMParameter("ENABLE_HTTPS");
            if (enableHttps != null && enableHttps.equals("true")) {
                http_type = "https";
                port = WebServerUtil.getHttpsPort();
            }
            String dcUrl = "";
            if (port == 0) {
                dcUrl = new String(http_type + "://" + serverName);
            }
            else {
                dcUrl = new String(http_type + "://" + serverName + ":" + port);
            }
            String jiraServerUrl = "";
            String username = "";
            String password = "";
            int installation_type = 0;
            final String[] classNames = ProductClassLoader.getMultiImplProductClass(DM_JIRA_API);
            DMJiraAPI dmJiraAPI = null;
            final Properties credentialProps = new Properties();
            for (final String className : classNames) {
                dmJiraAPI = (DMJiraAPI)Class.forName(className).newInstance();
                final Properties credSubProps = dmJiraAPI.getJiraCredentials();
                credentialProps.putAll(credSubProps);
            }
            if (!credentialProps.isEmpty()) {
                jiraServerUrl = credentialProps.getProperty("serverUrl");
                username = credentialProps.getProperty("userName");
                password = credentialProps.getProperty("password");
                installation_type = Integer.parseInt(credentialProps.getProperty("installationType"));
            }
            else {
                isJiraConfigured = false;
            }
            String jiraAccountId = req.getParameter("jiraAccountId");
            if (installation_type == 0 && jiraAccountId != null) {
                isJiraConfigured = false;
            }
            else if (installation_type == 1 && jiraAccountId == null) {
                isJiraConfigured = false;
            }
            if (!isJiraConfigured) {
                QuickLoadUtil.redirectURL("/jsp/admin/jiraMessagePage.jsp?dcUrl=" + dcUrl + "&jiraServerUrl=" + jiraServerUrl, req, res);
                return null;
            }
            String jiraVerificationUrl = "";
            final String dcUserId = req.getParameter("userId");
            if (installation_type == 0) {
                final String param = "dcJiraSessionId=" + ticket;
                jiraVerificationUrl = jiraServerUrl + "/rest/dcintegration/1.0/dcsession?" + param;
            }
            else {
                jiraAccountId = req.getParameter("jiraAccountId");
                jiraVerificationUrl = jiraServerUrl + "/rest/api/3/user/properties/ticket?accountId=" + jiraAccountId;
            }
            final String authKey = username + ":" + password;
            final String encodedAuthKey = "Basic " + Base64.encodeBase64String(authKey.getBytes());
            final int code = dmJiraAPI.checkSessionWithJiraServer(jiraVerificationUrl, encodedAuthKey, jiraAccountId, ticket, dcUserId);
            if (code == 401 || code == 403) {
                QuickLoadUtil.redirectURL("/jsp/admin/jiraMessagePage.jsp?dcUrl=" + dcUrl + "&changeCred=true&jiraServerUrl=" + jiraServerUrl, req, res);
                return null;
            }
            if (code == 10001) {
                QuickLoadUtil.redirectURL("/jsp/admin/jiraMessagePage.jsp?dcUrl=" + dcUrl + "&changeServer=true&jiraServerUrl=" + jiraServerUrl, req, res);
                return null;
            }
            DataObject apiAuthDO = null;
            if (installation_type == 0) {
                final String apiKeyForAuth = req.getParameter("dcJiraKeyValue");
                apiAuthDO = AuthenticationKeyUtil.getInstance().authenticateAPIKey(apiKeyForAuth, "301");
            }
            else {
                apiAuthDO = authenticateUser(dcUserId);
            }
            if (apiAuthDO == null || apiAuthDO.isEmpty()) {
                QuickLoadUtil.redirectURL("/jsp/admin/jiraMessagePage.jsp?dcUrl=" + dcUrl + "&errorPage=true&jiraServerUrl=" + jiraServerUrl, req, res);
                return null;
            }
            return dmJiraAPI.setValuesAfterJiraLogin(apiAuthDO, ticket, req);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return t;
        }
    }
    
    private static DataObject authenticateUser(final String dc_user_id) {
        DataObject keyTableDO = null;
        try {
            if (dc_user_id == null || "".equals(dc_user_id) || "null".equalsIgnoreCase(dc_user_id)) {
                DCAuthenticateRemote.logger.log(Level.INFO, "User ID is null or empty. Unable to authenticate.");
                return null;
            }
            keyTableDO = getUserLoginInfo(dc_user_id);
            if (keyTableDO != null && !keyTableDO.isEmpty()) {
                try {
                    final Row techRow = keyTableDO.getFirstRow("APIKeyDetails");
                    final String keyStatus = (String)techRow.get("STATUS");
                    final String authKeyInDO = (String)techRow.get("APIKEY");
                    if (keyStatus.equalsIgnoreCase("active")) {
                        final Long validity = (Long)techRow.get("VALIDITY");
                        if (validity > System.currentTimeMillis() || validity == -1L) {
                            return keyTableDO;
                        }
                    }
                    else {
                        DCAuthenticateRemote.logger.log(Level.INFO, "Key " + authKeyInDO + " is not active. Cannot perform operation");
                    }
                    return keyTableDO;
                }
                catch (final Exception ex) {
                    DCAuthenticateRemote.logger.log(Level.SEVERE, "Exception when trying to get user info from DO or when updating API key info - " + ex.getMessage(), ex);
                    throw ex;
                }
            }
            DCAuthenticateRemote.logger.log(Level.INFO, "Key not present for any user - auth fail - return false");
        }
        catch (final Exception exp) {
            DCAuthenticateRemote.logger.log(Level.WARNING, "Exception while authenticating user");
        }
        return keyTableDO;
    }
    
    private static DataObject getUserLoginInfo(final String dc_user_id) throws Exception {
        DataObject infoDO = null;
        try {
            final SelectQueryImpl sql = new SelectQueryImpl(Table.getTable("APIKeyDetails"));
            final String[] joinCol1 = { "LOGIN_ID" };
            final String[] joinCol2 = { "LOGIN_ID" };
            final Join sqlJoin = new Join("APIKeyDetails", "AaaLogin", joinCol1, joinCol2, 1);
            sql.addJoin(sqlJoin);
            sql.addSelectColumn(Column.getColumn("APIKeyDetails", "*"));
            sql.addSelectColumn(Column.getColumn("AaaLogin", "*"));
            final DataObject returnDO = DataAccess.get((SelectQuery)sql);
            final ArrayList<String> tableNames = new ArrayList<String>(2);
            tableNames.add("APIKeyDetails");
            tableNames.add("AaaLogin");
            final Criteria crit = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)dc_user_id, 0);
            final Row inputRow = returnDO.getRow("AaaLogin", crit);
            if (inputRow != null) {
                infoDO = returnDO.getDataObject((List)tableNames, inputRow);
            }
        }
        catch (final Exception e) {
            DCAuthenticateRemote.logger.log(Level.SEVERE, "Exception when getting user info for Key", e);
            throw e;
        }
        return infoDO;
    }
    
    public static String encode(final String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return s;
        }
    }
    
    private static ArrayList getDCRoles(String pluginLoginName, final String domainName) {
        DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() The validation roles : {0} domainName {1} ", new Object[] { pluginLoginName, domainName });
        ArrayList roles = DCCredentialHandler.getUserRole(pluginLoginName, domainName);
        DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() The validation roles : {0} domainName {1} roles {2}", new Object[] { pluginLoginName, domainName, roles });
        if (roles == null) {
            pluginLoginName = getDCMappedUserName(pluginLoginName, domainName);
            DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() pluginLoginName Name changed pluginLoginName : {0} domainName {1} ", new Object[] { pluginLoginName, domainName });
            roles = DCCredentialHandler.getUserRole(pluginLoginName, domainName);
        }
        DCAuthenticateRemote.logger.log(Level.WARNING, "validateTicket() The validation roles : {0} ", roles);
        return roles;
    }
    
    protected void invokeDCValve(final Request request, final Response response, final Ticket t, final String ticket, final String localSessionKey) throws Exception {
        final HttpServletRequest req = (HttpServletRequest)request;
        final String appName = (String)req.getSession().getAttribute("appname");
        DCAuthenticateRemote.logger.log(Level.WARNING, "invokeDCValve()   appName : {0} ", appName);
        if (appName != null) {
            final boolean isEnabled = SolutionUtil.getInstance().getLeftTreeOption(appName, false);
            DCAuthenticateRemote.logger.log(Level.WARNING, "invokeDCValve()   appName : {0} isEnabled : {1}", new Object[] { appName, isEnabled });
            request.getSession().setAttribute("isLeftTreeEnable", (Object)isEnabled);
        }
        if (t == null || DCAuthenticateRemote.principalMap.get(t.ticket) == null) {
            Principal p = null;
            String user = null;
            try {
                if (req.getParameter("userPrincipal") != null) {
                    user = req.getParameter("userPrincipal");
                }
                if (t.principal != null) {
                    user = t.principal;
                }
                if (t.domainName != null) {
                    req.setAttribute("domainName", (Object)t.domainName);
                }
                DCAuthenticateRemote.logger.log(Level.WARNING, "invokeDCValve() User principal user Name : {0} domainName name : {1}  ", new Object[] { user, t.domainName });
                DCAuthenticateRemote.logger.log(Level.FINEST, "invokeDCValve() RequestURI   -----------  " + SecurityUtil.getNormalizedRequestURI(req) + " " + req.getQueryString());
                user = getDCMappedUserName(user, t.domainName);
                DCAuthenticateRemote.logger.log(Level.WARNING, "invokeDCValve() After getting user Name user Name : {0} domainName name : {1}  ", new Object[] { user, t.domainName });
                p = PAM.login(user, "System", request);
                request.setUserPrincipal(p);
                DCAuthenticateRemote.logger.log(Level.WARNING, "invokeDCValve() After Logged in  going to update general properties value user Name : {0} domainName name : {1}  ", new Object[] { user, t.domainName });
                final Properties generalProperties = ProductUrlLoader.getInstance().getGeneralProperites();
                final HttpSession session = req.getSession();
                final ServletContext context = session.getServletContext();
                context.setAttribute("producturlmaps", (Object)generalProperties);
                session.setAttribute("generalProperties", (Object)generalProperties);
                DCAuthenticateRemote.logger.log(Level.WARNING, "invokeDCValve() After Logged in  going to update Ddefault values to session user Name : {0} domainName name : {1}  ", new Object[] { user, t.domainName });
                SYMClientUtil.setDefalutVlauesToSession(session, (HttpServletRequest)request, user);
                DCAuthenticateRemote.logger.log(Level.WARNING, "invokeDCValve()  Ddefault values has been updated to session for user login -> user Name : {0} domainName name : {1}  ", new Object[] { user, t.domainName });
            }
            catch (final Exception e) {
                e.printStackTrace();
                DCAuthenticateRemote.logger.log(Level.WARNING, "invokeDCValve() Cannot create the user ----------in the valve  " + user);
                throw new Exception();
            }
            this.setInfoInCookie(request, response, ticket);
            if (ticket != null && request.getRequestedSessionId() != null) {
                DCAuthenticateRemote.principalMap.put(ticket, request.getRequestedSessionId());
            }
            if (ticket != null && p != null) {
                DCAuthenticateRemote.principalMap.put(ticket + "principal", p);
            }
            try {
                this.getNext().invoke(request, response);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            return;
        }
        String tic = this.getFromCookie(request, response);
        DCAuthenticateRemote.logger.log(Level.WARNING, "invokeDCValve()   ticket Value form Cookies ticket : {1} appName : {0}", new Object[] { appName, tic });
        if (tic == null) {
            tic = DCAuthenticateRemote.pluginSessionToTicketMap.get(req.getSession().getId());
            DCAuthenticateRemote.logger.log(Level.WARNING, "invokeDCValve()   ticket Value form local memory ticket : {1} appName : {0}", new Object[] { appName, tic });
        }
        final String tt = t.ticket;
        if (tic != null && (tic.equalsIgnoreCase(localSessionKey) || tic.equals(tt) || tt.equalsIgnoreCase(localSessionKey))) {
            final String sessId = DCAuthenticateRemote.principalMap.get(t.ticket);
            request.setRequestedSessionId(sessId);
            final Principal pri = DCAuthenticateRemote.principalMap.get(t.ticket + "principal");
            request.setUserPrincipal(pri);
            DCAuthenticateRemote.logger.log(Level.WARNING, "invokeDCValve()   ticket Value form local memory appName : {0} ticket object : {1}", new Object[] { appName, t });
            try {
                this.getNext().invoke(request, response);
            }
            catch (final Exception e2) {
                e2.printStackTrace();
                DCAuthenticateRemote.logger.log(Level.WARNING, "Exception invoking next valve");
            }
            return;
        }
        DCAuthenticateRemote.logger.log(Level.WARNING, "invokeDCValve()   ticket is not available AppName  : {0} ticket   : {1}", new Object[] { appName, tic });
        try {
            this.getNext().invoke(request, response);
        }
        catch (final Exception e) {
            e.printStackTrace();
            DCAuthenticateRemote.logger.log(Level.WARNING, "Exception invoking next valve");
        }
    }
    
    public void setCustomerFilter(final Request request, final Response response) {
        final String accountName = request.getParameter("accountName");
        if (accountName != null) {
            try {
                final Properties customerInfo = CustomerInfoUtil.getInstance().getCustomerInfo(accountName);
                final Long customerID = ((Hashtable<K, Long>)customerInfo).get("CUSTOMER_ID");
                final Cookie custCookie = DMCookieUtil.generateDMCookies((HttpServletRequest)request, "dc_customerid", "" + customerID);
                final HttpServletResponse resp = (HttpServletResponse)response;
                custCookie.setSecure(true);
                custCookie.setHttpOnly(true);
                resp.addCookie(custCookie);
                CustomerInfoThreadLocal.setCustomerId(customerID.toString());
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void setInfoInCookie(final Request request, final Response response, final String ticket) {
        final Cookie c = DMCookieUtil.generateDMCookies((HttpServletRequest)request, "DCSDPCOOKIE", ticket);
        c.setHttpOnly(true);
        final HttpServletResponse resp = (HttpServletResponse)response;
        resp.addCookie(c);
    }
    
    public String getFromCookie(final Request request, final Response response) {
        final HttpServletRequest req = (HttpServletRequest)request;
        final Cookie[] cookiesList = req.getCookies();
        if (cookiesList == null) {
            return null;
        }
        for (int i = 0; i < cookiesList.length; ++i) {
            if (cookiesList[i].getName() != null && cookiesList[i].getName().equals("DCSDPCOOKIE")) {
                return cookiesList[i].getValue().toString();
            }
        }
        return null;
    }
    
    public static void destorySessionValues(final String sessId) {
        ApiFactoryProvider.getCacheAccessAPI().removeCache("isZendeskLogin");
        ApiFactoryProvider.getCacheAccessAPI().removeCache("isPuginLogin");
        ApiFactoryProvider.getCacheAccessAPI().removeCache("sdpLoginEnabled");
        ApiFactoryProvider.getCacheAccessAPI().removeCache("isSSLogin");
        ApiFactoryProvider.getCacheAccessAPI().removeCache("jumpto");
        ApiFactoryProvider.getCacheAccessAPI().removeCache("isPuginLogin");
        ApiFactoryProvider.getCacheAccessAPI().removeCache("baseurl");
        if (SolutionUtil.getInstance().isIframeIntegrationModeEnabled() || isProbeSSOSession(sessId)) {
            DCAuthenticateRemote.logger.log(Level.FINEST, "sessionDestroyed() sessId : {0}", sessId);
            final String ticketId = DCAuthenticateRemote.pluginSessionToTicketMap.get(sessId);
            DCAuthenticateRemote.logger.log(Level.FINEST, "sessionDestroyed() ticketId : {0} ", ticketId);
            if (ticketId != null) {
                DCAuthenticateRemote.logger.log(Level.FINEST, "sessionDestroyed() ticketId : {0} ", ticketId);
                try {
                    DCAuthenticateRemote.pluginTicketCacheMap.remove(ticketId);
                    DCAuthenticateRemote.pluginTicketToSessionMap.remove(ticketId);
                    DCAuthenticateRemote.principalMap.remove(ticketId);
                    DCAuthenticateRemote.principalMap.remove(ticketId + "principal");
                    DCAuthenticateRemote.pluginSessionToTicketMap.remove(sessId);
                }
                catch (final Exception e) {
                    DCAuthenticateRemote.logger.log(Level.SEVERE, "Exception in sessionDestroyed() of DCSessionListener : {0} ", e.getMessage());
                    DCAuthenticateRemote.logger.log(Level.SEVERE, "Exception in sessionDestroyed() of Exception : ", e);
                }
            }
        }
    }
    
    protected static Properties setDemoProp() {
        final Properties demoProp = new Properties();
        ((Hashtable<String, String>)demoProp).put("loginUser", "administrator");
        ((Hashtable<String, String>)demoProp).put("domainName", "-");
        ((Hashtable<String, String>)demoProp).put("SDP_VERSION", "9.0");
        ((Hashtable<String, String>)demoProp).put("SDP_BUILD_NUMBER", "9003");
        ((Hashtable<String, String>)demoProp).put("RESULT", "true");
        return demoProp;
    }
    
    protected static Properties validateAndSetProp(final String postPath) {
        final Properties prop = new Properties();
        try {
            final URL url = new URL(postPath);
            final String contentType = "text/xml; charset=\"UTF-8\"";
            final URLConnection uc = SDPNotificationUtil.getInstance().createSDPURLConnection(url, contentType, true, true, true);
            if (postPath != null && postPath.contains("/DCIntegrationServlet")) {
                final String authenticationKey = SolutionUtil.getInstance().getServerSettings("HelpDesk").getProperty("AUTHENDICATION_KEY");
                if (authenticationKey != null) {
                    uc.setRequestProperty("AUTHTOKEN", Encoder.convertFromBase(authenticationKey));
                }
                final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
            }
            final InputStream ic = uc.getInputStream();
            prop.load(ic);
            ic.close();
        }
        catch (final Exception ex) {
            DCAuthenticateRemote.logger.log(Level.SEVERE, "Exception in Validating request: ", ex);
        }
        return prop;
    }
    
    private static boolean isProbeSSOSession(final String sessionId) {
        return SyMUtil.isProbeServer() && DCAuthenticateRemote.pluginSessionToTicketMap.containsKey(sessionId);
    }
    
    public static void addXFrameHeader(final HttpServletRequest request, final HttpServletResponse response) {
        final String xFrameOptionsKey = "tomcat.xframe.options";
        final String serverHome = System.getProperty("server.home");
        final String statusEnabled = "enabled";
        final String statusDisabled = "disabled";
        final File webSettingsConfFile = new File(serverHome + File.separator + "conf" + File.separator + "websettings.conf");
        if (DCAuthenticateRemote.isXFrameEnabled == null && webSettingsConfFile.exists()) {
            try {
                final Properties wsProps = WebServerUtil.getWebServerSettings();
                final String xFrameHeaderValFromWSProps = wsProps.getProperty(xFrameOptionsKey);
                if (null != xFrameHeaderValFromWSProps && !"".equalsIgnoreCase(xFrameHeaderValFromWSProps)) {
                    if (statusDisabled.equalsIgnoreCase(xFrameHeaderValFromWSProps)) {
                        DCAuthenticateRemote.isXFrameEnabled = statusDisabled;
                    }
                    else {
                        DCAuthenticateRemote.isXFrameEnabled = statusEnabled;
                        DCAuthenticateRemote.xFrameOptionsValue = xFrameHeaderValFromWSProps;
                    }
                }
                else {
                    DCAuthenticateRemote.isXFrameEnabled = statusEnabled;
                }
            }
            catch (final Exception ex) {
                DCAuthenticateRemote.logger.log(Level.INFO, "Is XFrame Enabled " + DCAuthenticateRemote.isXFrameEnabled);
            }
        }
        if (DCAuthenticateRemote.isXFrameEnabled == null || DCAuthenticateRemote.isXFrameEnabled.equalsIgnoreCase(statusEnabled)) {
            response.addHeader("X-FRAME-OPTIONS", DCAuthenticateRemote.xFrameOptionsValue);
        }
    }
    
    private void setIfRequestFromTomcat(final HttpServletRequest req) {
        try {
            final Properties webSettings = WebServerUtil.getWebServerSettings();
            final int wsPort = Integer.parseInt(webSettings.getProperty("ws.port"));
            final int wssPort = Integer.parseInt(webSettings.getProperty("wss.port"));
            final int port = req.getLocalPort();
            if (wsPort == port || wssPort == port) {
                req.setAttribute("directAccessToTomcatPort", (Object)"true");
            }
        }
        catch (final Exception ex) {
            DCAuthenticateRemote.logger.log(Level.INFO, "Exception while checking whether the port is tomcat port." + ex.getMessage());
            DCAuthenticateRemote.logger.log(Level.FINE, "Exception while checking whether the port is tomcat port.", ex);
        }
    }
    
    static {
        DCAuthenticateRemote.logger = Logger.getLogger("SDPLogger");
        DCAuthenticateRemote.pluginTicketCacheMap = new HashMap<String, Ticket>();
        DCAuthenticateRemote.pluginTicketToSessionMap = new HashMap<String, HttpSession>();
        DCAuthenticateRemote.pluginSessionToTicketMap = new HashMap<String, String>();
        DCAuthenticateRemote.principalMap = new Hashtable();
        DCAuthenticateRemote.isXFrameEnabled = null;
        DCAuthenticateRemote.xFrameOptionsValue = "SAMEORIGIN";
    }
}
