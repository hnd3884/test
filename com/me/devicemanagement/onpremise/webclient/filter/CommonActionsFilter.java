package com.me.devicemanagement.onpremise.webclient.filter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Locale;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import com.me.devicemanagement.framework.server.util.DCPluginUtil;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.EventLogThreadLocal;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.onpremise.server.util.ServerSessionUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class CommonActionsFilter implements Filter
{
    private static Logger logger;
    
    public void init(final FilterConfig filterConfig) {
        final String enableLiveChat = SyMUtil.getSyMParameter("ENABLE_LIVE_CHAT");
        filterConfig.getServletContext().setAttribute("enableLiveChat", (Object)((enableLiveChat == null) ? "true" : enableLiveChat.toLowerCase()));
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest servletRequest = (HttpServletRequest)request;
        final HttpSession session = servletRequest.getSession();
        String roleName = (String)session.getAttribute("roleName");
        try {
            final Locale locale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
            if (roleName == null) {
                final ServerSessionUtil sessionUtil = new ServerSessionUtil();
                sessionUtil.setDefaultSessionValues(session);
                final DCEventLogUtil eventUtil = DCEventLogUtil.getInstance();
                final boolean isDemoMode = ApiFactoryProvider.getDemoUtilAPI().isDemoMode();
                final String remoteAddr = request.getRemoteAddr();
                String remoteHost = request.getRemoteHost();
                CommonActionsFilter.logger.log(Level.INFO, "Remote Address : " + remoteAddr);
                final String licenseType = SyMUtil.getSyMParameter("licenseType");
                final String productType = SyMUtil.getSyMParameter("productType");
                final String licenseVersion = SyMUtil.getSyMParameter("licenseVersion");
                final long evalDays = LicenseProvider.getInstance().getEvaluationDays();
                CommonActionsFilter.logger.log(Level.INFO, "License Type     : " + licenseType);
                CommonActionsFilter.logger.log(Level.INFO, "Product Type     : " + productType);
                CommonActionsFilter.logger.log(Level.INFO, "License Version     : " + licenseVersion);
                session.setAttribute("licenseType", (Object)licenseType);
                session.setAttribute("productType", (Object)productType);
                session.setAttribute("licenseVersion", (Object)licenseVersion);
                session.setAttribute("licenseDays", (Object)evalDays);
                if (CustomerInfoUtil.getInstance().isRAP()) {
                    final String licenseUserType = SyMUtil.getSyMParameter("licenseusertype");
                    CommonActionsFilter.logger.log(Level.INFO, "License user type : " + licenseUserType);
                    session.setAttribute("licenseUserType", (Object)licenseUserType);
                }
                final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                final String loginUserName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
                roleName = DMUserHandler.getRoleForUser(loginUserName);
                final Long customerId = MSPWebClientUtil.getCustomerID(servletRequest);
                String i18n = "desktopcentral.webclient.filter.host_connected_msg";
                if (isDemoMode) {
                    remoteHost = "***:***:***:***";
                }
                EventLogThreadLocal.setSourceIpAddress(request.getRemoteAddr());
                EventLogThreadLocal.setSourceHostName(request.getRemoteHost());
                final Object remarksArgs = remoteHost + "@@@" + loginUserName + "@@@" + roleName;
                eventUtil.addEvent(718, loginUserName, (HashMap)null, i18n, remarksArgs, false, customerId);
                final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
                if (isMSP) {
                    final String techRoleScopeMsg = I18NUtil.getString("desktopcentral.webclient.filter.DCAuthorizationFilter.TechRoleScopeMessage_MSP", locale, userID);
                    session.setAttribute("TechRoleScopeMessage", (Object)techRoleScopeMsg);
                }
                else {
                    final String scope = I18N.getMsg("desktopcentral.webclient.admin.som.name", new Object[0]);
                    final String I18Ndefn = I18N.getMsg("desktopcentral.webclient.admin.som.notdefine.technician", new Object[] { scope });
                    session.setAttribute("TechRoleScopeMessage", (Object)I18Ndefn);
                }
                LicenseProvider.getInstance().setFreeEditionConfiguredStatus();
                SYMClientUtil.updateSOMDisplayName(servletRequest);
                session.setAttribute("VIEW_CONFIG_SELECTED_DOMAIN", (Object)"All");
                session.setAttribute("VIEW_CONFIG_SELECTED_TYPE", (Object)"All");
                CommonActionsFilter.logger.log(Level.INFO, loginUserName + " properties are loaded to session object");
                String showTree = SyMUtil.getUserParameter(userID, "SHOW_TREE_NAVIGATION");
                CommonActionsFilter.logger.log(Level.FINEST, loginUserName + " showTree :" + showTree);
                if (showTree == null) {
                    showTree = "true";
                }
                session.setAttribute("SHOW_TREE_NAVIGATION", (Object)showTree);
                session.setAttribute("isDemoMode", (Object)String.valueOf(isDemoMode));
                if (isDemoMode) {
                    i18n = I18NUtil.getString("dc.common.RUNNING_IN_RESTRICTED_MODE", locale, userID);
                    session.setAttribute("demoModeMessage", (Object)i18n);
                }
                session.setAttribute("isTestMode", (Object)SyMUtil.isTestMode());
                final String isRebranded = SyMUtil.getSyMParameter("IS_REBRANDED");
                if (isRebranded != null && isRebranded.equalsIgnoreCase("true")) {
                    final String url = SyMUtil.getSyMParameter("HOME_URL");
                    request.getServletContext().setAttribute("HOME_URL", (Object)url);
                    String escapedHomeUrl = "";
                    try {
                        escapedHomeUrl = url;
                        if (escapedHomeUrl != null && !escapedHomeUrl.isEmpty()) {
                            escapedHomeUrl = DMIAMEncoder.encodeURL(url).replace(DMIAMEncoder.encodeURL(":"), ":").replace(DMIAMEncoder.encodeURL("/"), "/").replace(DMIAMEncoder.encodeURL(";"), ";").replace(DMIAMEncoder.encodeURL("="), "=").replace(DMIAMEncoder.encodeURL("&"), "&").replace(DMIAMEncoder.encodeURL("?"), "?");
                        }
                    }
                    catch (final Exception ex) {}
                    request.getServletContext().setAttribute("ESCAPED_HOME_URL", (Object)escapedHomeUrl);
                    request.getServletContext().setAttribute("IS_REBRANDED", (Object)true);
                }
                else {
                    request.getServletContext().setAttribute("IS_REBRANDED", (Object)false);
                }
                session.setAttribute("IS_PLUGIN_MODE", (Object)DCPluginUtil.getInstance().isPlugin());
                session.setAttribute("selectcfgview", (Object)"myview");
            }
            final HttpServletResponse servletResponse = (HttpServletResponse)response;
            servletResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            servletResponse.setHeader("Pragma", "no-cache");
            servletResponse.setDateHeader("Expires", 0L);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        chain.doFilter(request, response);
    }
    
    public void destroy() {
    }
    
    static {
        CommonActionsFilter.logger = Logger.getLogger(CommonActionsFilter.class.getName());
    }
}
