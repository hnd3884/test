package com.me.mdm.onpremise.webclient.integration;

import javax.net.ssl.SSLHandshakeException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.webclient.common.QuickLoadUtil;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserUtil;
import com.me.devicemanagement.framework.server.util.SoMADUtil;
import com.me.devicemanagement.onpremise.server.mesolutions.notification.SDPNotificationUtil;
import javax.servlet.ServletContext;
import java.util.Properties;
import java.security.Principal;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.authentication.PAM;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.mesolutions.util.SolutionUtil;
import javax.servlet.http.HttpSession;
import com.me.devicemanagement.onpremise.server.sdp.Ticket;
import com.me.devicemanagement.onpremise.server.sdp.DCCredentialHandler;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.webclient.sdp.DCAuthenticateRemote;

public class MDMIntegrationAuthenticateRemote extends DCAuthenticateRemote
{
    private static Logger logger;
    
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        final HttpServletResponse res = (HttpServletResponse)response;
        final String reqURI = req.getRequestURI();
        final String contextPath = req.getContextPath();
        req.setCharacterEncoding("UTF-8");
        Label_0127: {
            if (reqURI != null) {
                if (!reqURI.endsWith(".png") && !reqURI.endsWith(".jpg") && !reqURI.endsWith(".css") && !reqURI.endsWith(".js") && !reqURI.endsWith(".gif")) {
                    if (!reqURI.contains("ServerStatusServlet")) {
                        break Label_0127;
                    }
                }
                try {
                    this.getNext().invoke(request, response);
                }
                catch (final Exception e1) {
                    MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "Exception invoking next valve");
                    e1.printStackTrace();
                }
                return;
            }
        }
        MDMIntegrationAuthenticateRemote.logger.log(Level.FINE, "************************** invoke() Started ************************** ", reqURI);
        MDMIntegrationAuthenticateRemote.logger.log(Level.FINE, "invoke() The request URI is {0} ", reqURI);
        final String queryString = req.getQueryString();
        MDMIntegrationAuthenticateRemote.logger.log(Level.FINE, "The incoming request params are {0} ", queryString);
        MDMIntegrationAuthenticateRemote.logger.log(Level.FINE, "session exits pluginTicketToSessionMap : {0} ", MDMIntegrationAuthenticateRemote.pluginTicketToSessionMap.containsValue(req.getSession()));
        Ticket t = null;
        String ticket = req.getParameter("ticket");
        final String action = req.getParameter("action");
        MDMIntegrationAuthenticateRemote.logger.log(Level.FINEST, "action : {0}", action);
        if (ticket != null && req.getParameter("MDMPSDPIntegrationMode") != null) {
            final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
            if (isMSP) {
                final String sdpAccountName = req.getParameter("customerName");
                if (sdpAccountName != null) {
                    final Long customerId = CustomerInfoUtil.getInstance().getCustomerId(sdpAccountName.trim());
                    if (customerId != null) {
                        MSPWebClientUtil.setCustomerIDSummaryInCookie((HttpServletRequest)request, (HttpServletResponse)response, customerId.toString());
                    }
                }
            }
            req.getSession().setAttribute("isMDMPPluginLogin", (Object)"PLUGIN_LOGIN");
            req.getSession().setAttribute("sdpLoginEnabled", (Object)"true");
        }
        final String fromJumpTo = request.getParameter("jumpto");
        if (fromJumpTo != null && fromJumpTo.equalsIgnoreCase("true")) {
            req.getSession().setAttribute("jumpto", (Object)"true");
            req.getSession().setAttribute("baseurl", (Object)request.getParameter("baseurl"));
        }
        else if (fromJumpTo != null && fromJumpTo.equalsIgnoreCase("false")) {
            req.getSession().setAttribute("jumpto", (Object)"false");
        }
        final String isPluginLogin = (String)req.getSession().getAttribute("isMDMPPluginLogin");
        if (isPluginLogin == null || !isPluginLogin.equals("PLUGIN_LOGIN")) {
            MDMIntegrationAuthenticateRemote.logger.log(Level.FINE, "Login from DC hence invokeNext called");
            try {
                this.getNext().invoke(request, response);
            }
            catch (final Exception e2) {
                MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "Exception invoking next valve");
                e2.printStackTrace();
            }
            return;
        }
        final String localSessionKey = MDMIntegrationAuthenticateRemote.pluginSessionToTicketMap.get(req.getSession().getId());
        if (ticket == null) {
            ticket = localSessionKey;
        }
        if (ticket != null && localSessionKey != null && !ticket.equalsIgnoreCase(localSessionKey)) {
            MDMIntegrationAuthenticateRemote.pluginSessionToTicketMap.remove(req.getSession().getId());
        }
        else if (ticket != null && localSessionKey == null) {
            final Iterator ticketKeys = MDMIntegrationAuthenticateRemote.pluginSessionToTicketMap.keySet().iterator();
            while (ticketKeys.hasNext()) {
                final String oldSessionID = ticketKeys.next().toString();
                final String oldTicketKey = MDMIntegrationAuthenticateRemote.pluginSessionToTicketMap.get(oldSessionID);
                if (oldTicketKey.equalsIgnoreCase(ticket)) {
                    MDMIntegrationAuthenticateRemote.pluginSessionToTicketMap.remove(oldSessionID);
                    break;
                }
            }
        }
        if (DCCredentialHandler.localUserRoleMap == null || DCCredentialHandler.localUserRoleMap.size() <= 0) {
            DCCredentialHandler.init();
        }
        if (ticket != null) {
            t = MDMIntegrationAuthenticateRemote.pluginTicketCacheMap.get(ticket);
            if (t == null || (MDMIntegrationAuthenticateRemote.pluginTicketToSessionMap.containsKey(ticket) && !MDMIntegrationAuthenticateRemote.pluginTicketToSessionMap.get(ticket).equals(req.getSession()))) {
                if (req.getSession().getAttribute("isMDMPPluginLogin") != null) {
                    t = validateMDMPSDPTicket(ticket, req, res);
                    final String isSDPUIInteg = SolutionUtil.getInstance().getIntegrationParamsValue("SDP_MDM_UI_INTEGRATION");
                    if (isSDPUIInteg == null || (isSDPUIInteg != null && isSDPUIInteg.equalsIgnoreCase("false"))) {
                        SolutionUtil.getInstance().updateIntegrationParameter("SDP_MDM_UI_INTEGRATION", "true");
                    }
                }
                if (t != null) {
                    MDMIntegrationAuthenticateRemote.pluginTicketCacheMap.put(ticket, t);
                    MDMIntegrationAuthenticateRemote.pluginTicketToSessionMap.put(ticket, req.getSession());
                    MDMIntegrationAuthenticateRemote.pluginSessionToTicketMap.put(req.getSession().getId(), ticket);
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
            MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "no credential is present. Have to authenticate the request.");
            if (action != null && (action.equals("sdplogout") || action.equals("dcpluginlogout"))) {
                final HttpSession session = MDMIntegrationAuthenticateRemote.pluginTicketToSessionMap.remove(ticket);
                if (session != null) {
                    MDMIntegrationAuthenticateRemote.pluginSessionToTicketMap.remove(session.getId());
                    MDMIntegrationAuthenticateRemote.pluginTicketCacheMap.remove(ticket);
                    session.invalidate();
                }
            }
            this.getNext().invoke(request, response);
            return;
        }
        if (action != null && (action.equals("sdplogout") || action.equals("dcpluginlogout"))) {
            MDMIntegrationAuthenticateRemote.pluginTicketCacheMap.remove(ticket);
            final HttpSession session = MDMIntegrationAuthenticateRemote.pluginTicketToSessionMap.remove(ticket);
            session.invalidate();
            return;
        }
        req = (HttpServletRequest)DCCredentialHandler.handle(req, t);
        try {
            this.invokeMDMPValve(request, response, t, ticket, localSessionKey);
        }
        catch (final Exception e3) {
            e3.printStackTrace();
            this.getNext().invoke(request, response);
            return;
        }
        MDMIntegrationAuthenticateRemote.logger.log(Level.FINE, "************************** invoke() End here ************************** ", reqURI);
    }
    
    private void invokeMDMPValve(final Request request, final Response response, final Ticket t, final String ticket, final String localSessionKey) throws Exception {
        final HttpServletRequest req = (HttpServletRequest)request;
        final String appName = (String)req.getSession().getAttribute("appname");
        MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "invokeMDMPValve()   appName : {0} ", appName);
        if (t == null || MDMIntegrationAuthenticateRemote.principalMap.get(t.ticket) == null) {
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
                MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "invokeMDMPValve() User principal user Name : {0} domainName name : {1}  ", new Object[] { user, t.domainName });
                MDMIntegrationAuthenticateRemote.logger.log(Level.FINEST, "invokeMDMPValve() RequestURI   -----------  {0} {1}", new Object[] { req.getRequestURI(), req.getQueryString() });
                user = getDCMappedUserName(user, t.domainName);
                MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "invokeMDMPValve() After getting user Name user Name : {0} domainName name : {1}  ", new Object[] { user, t.domainName });
                p = PAM.login(user, "System", request);
                request.setUserPrincipal(p);
                MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "invokeMDMPValve() After Logged in  going to update general properties value user Name : {0} domainName name : {1}  ", new Object[] { user, t.domainName });
                final Properties generalProperties = ProductUrlLoader.getInstance().getGeneralProperites();
                final HttpSession session = req.getSession();
                final ServletContext context = session.getServletContext();
                context.setAttribute("generalProperties", (Object)generalProperties);
                MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "invokeMDMPValve() After Logged in  going to update Ddefault values to session user Name : {0} domainName name : {1}  ", new Object[] { user, t.domainName });
                SYMClientUtil.setDefalutVlauesToSession(session, (HttpServletRequest)request, user);
                MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "invokeMDMPValve()  Ddefault values has been updated to session for user login -> user Name : {0} domainName name : {1}  ", new Object[] { user, t.domainName });
            }
            catch (final Exception e) {
                e.printStackTrace();
                MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "invokeMDMPValve() Cannot create the user ----------in the valve  {0}", user);
                throw new Exception();
            }
            this.setInfoInCookie(request, response, ticket);
            if (ticket != null && request.getRequestedSessionId() != null) {
                MDMIntegrationAuthenticateRemote.principalMap.put(ticket, request.getRequestedSessionId());
            }
            if (ticket != null && p != null) {
                MDMIntegrationAuthenticateRemote.principalMap.put(ticket + "principal", p);
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
        MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "invokeMDMPValve()   ticket Value form Cookies ticket : {1} appName : {0}", new Object[] { appName, tic });
        if (tic == null) {
            tic = MDMIntegrationAuthenticateRemote.pluginSessionToTicketMap.get(req.getSession().getId());
            MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "invokeMDMPValve()   ticket Value form local memory ticket : {1} appName : {0}", new Object[] { appName, tic });
        }
        final String tt = t.ticket;
        if (tic != null && (tic.equalsIgnoreCase(localSessionKey) || tic.equals(tt) || tt.equalsIgnoreCase(localSessionKey))) {
            final String sessId = MDMIntegrationAuthenticateRemote.principalMap.get(t.ticket);
            request.setRequestedSessionId(sessId);
            final Principal pri = MDMIntegrationAuthenticateRemote.principalMap.get(t.ticket + "principal");
            request.setUserPrincipal(pri);
            MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "invokeMDMPValve()   ticket Value form local memory appName : {0} ticket object : {1}", new Object[] { appName, t });
            try {
                this.getNext().invoke(request, response);
            }
            catch (final Exception e2) {
                e2.printStackTrace();
                MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "Exception invoking next valve");
            }
            return;
        }
        MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "invokeMDMPValve()   ticket is not available AppName  : {0} ticket   : {1}", new Object[] { appName, tic });
        try {
            this.getNext().invoke(request, response);
        }
        catch (final Exception e) {
            e.printStackTrace();
            MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "Exception invoking next valve");
        }
    }
    
    public static Ticket validateMDMPSDPTicket(final String ticket, final HttpServletRequest request, final HttpServletResponse res) {
        Ticket t = null;
        try {
            final String servletStr = "api/v3/DCIntegrationServlet";
            final StringBuffer urlStr = new StringBuffer(servletStr);
            urlStr.append("?");
            urlStr.append("operation=validateticket");
            urlStr.append("&");
            urlStr.append("ticket").append("=").append(encode(ticket));
            final String baseServerType = request.getParameter("pname");
            final String appname = SolutionUtil.getInstance().getProductAppName(baseServerType);
            request.getSession().setAttribute("appname", (Object)appname);
            final String applicationServerUrl = SDPNotificationUtil.getApplicationServerURL(appname);
            final String postPath = applicationServerUrl + "/" + urlStr.toString();
            MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "validateMDMPSDPTicket() postPath : {0} ", postPath);
            Properties p = new Properties();
            final boolean isDemoMode = ApiFactoryProvider.getDemoUtilAPI().isDemoMode();
            if (isDemoMode) {
                MDMIntegrationAuthenticateRemote.logger.log(Level.INFO, "\n ***************************\n isDemoMode is {0} Hence going to set default property", isDemoMode);
                p = setDemoProp();
            }
            else {
                p = validateAndSetProp(postPath);
            }
            SolutionUtil.getInstance().updateIntegrationParameter("SDP_MDM_UI_INTEGRATION", "true");
            final boolean result = Boolean.valueOf(p.getProperty("RESULT"));
            if (!result) {
                MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "validateMDMPSDPTicket() The validation result is false");
                return null;
            }
            final String sdpBuildNumber = p.getProperty("SDP_BUILD_NUMBER");
            if (sdpBuildNumber != null) {
                request.getSession().setAttribute("SDP_BUILD_NUMBER", (Object)sdpBuildNumber);
                final String previousBuilNumber = SolutionUtil.getInstance().getIntegrationParamsValue("SDP_BUILD_NUMBER");
                MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "validateMDMPSDPTicket() previousBuilNumber : {0} sdpBuildNumber : {1}", new Object[] { previousBuilNumber, sdpBuildNumber });
                if (previousBuilNumber == null || !previousBuilNumber.equalsIgnoreCase(sdpBuildNumber)) {
                    SolutionUtil.getInstance().updateIntegrationParameter("SDP_BUILD_NUMBER", sdpBuildNumber);
                }
            }
            MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "validateMDMPSDPTicket() The validation result is true Properties : {0}", p);
            t = new Ticket();
            t.ticket = ticket;
            String pluginLoginName = p.getProperty("loginUser");
            t.principal = pluginLoginName;
            String domainName = p.getProperty("domainName");
            if (domainName != null && !domainName.equalsIgnoreCase("-")) {
                MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "validateMDMPSDPTicket() The validation pluginLoginName : {0} domainName {1} ", new Object[] { pluginLoginName, domainName });
                domainName = SoMADUtil.getInstance().getManagedDomain(domainName);
                MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "validateMDMPSDPTicket() The validation After Getting from DB pluginLoginName : {0} domainName {1} ", new Object[] { pluginLoginName, domainName });
                t.domainName = domainName.toLowerCase();
            }
            else {
                domainName = null;
            }
            final String role = p.getProperty("ROLES");
            final boolean userStatus = DMOnPremiseUserUtil.isActiveUser(pluginLoginName, domainName);
            if (!userStatus) {
                MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "validateMDMPSDPTicket() User is Disabled user");
                QuickLoadUtil.redirectURL("/jsp/admin/iframeUserMessage.jsp?username=" + pluginLoginName + "&domainname=" + domainName, request, res);
                return null;
            }
            if (role == null && domainName == null && pluginLoginName != null && pluginLoginName.equalsIgnoreCase("administrator")) {
                pluginLoginName = "admin";
            }
            final String mdmpRole = DMUserHandler.getRoleForUser(pluginLoginName);
            MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "validateMDMPSDPTicket() The validation roles : {0} ", mdmpRole);
            if (mdmpRole == null) {
                QuickLoadUtil.redirectURL("/jsp/admin/iframeUserMessage.jsp?username=" + pluginLoginName + "&domainname=" + domainName, request, res);
                return null;
            }
            MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "validateMDMPSDPTicket() DC roles from local cache::{0}", mdmpRole);
            t.roles = mdmpRole;
            t.ipaddress = request.getRemoteAddr();
            t.properties = p;
            MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "validateMDMPSDPTicket() The ticket upon validation is :{0}", t);
            return t;
        }
        catch (final SSLHandshakeException e) {
            MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "validateMDMPSDPTicket() Exception while validating the ticket:", e);
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
            MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "validateMDMPSDPTicket() Exception while validating the ticket:", e2);
        }
        finally {
            MDMIntegrationAuthenticateRemote.logger.log(Level.WARNING, "validateMDMPSDPTicket() Exit from validation");
        }
        return null;
    }
    
    static {
        MDMIntegrationAuthenticateRemote.logger = Logger.getLogger("SDPLogger");
    }
}
