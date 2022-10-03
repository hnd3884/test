package com.me.devicemanagement.onpremise.webclient.configurations;

import java.util.Hashtable;
import javax.servlet.http.Cookie;
import com.me.devicemanagement.framework.server.security.DMCookieUtil;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import java.util.Enumeration;
import com.me.devicemanagement.onpremise.start.servertroubleshooter.util.ServerTroubleshooterUtil;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import com.me.devicemanagement.onpremise.server.twofactor.TwoFactorAction;
import com.adventnet.i18n.I18N;
import java.io.File;
import java.util.HashMap;
import com.me.ems.onpremise.server.util.NotifyUpdatesUtil;
import com.me.devicemanagement.onpremise.server.util.UpdatesParamUtil;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.TreeMap;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.onpremise.webclient.admin.UserController;
import com.me.devicemanagement.framework.server.mobapp.MobileAppUtil;
import java.util.Iterator;
import java.util.Set;
import java.util.Locale;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import com.me.devicemanagement.framework.server.common.DMModuleHandler;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.cache.SessionAPI;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class LoginServlet extends HttpServlet
{
    Logger logger;
    String isSpicePlugin;
    SessionAPI sessionAPI;
    Properties localesProperties;
    
    public void init() throws ServletException {
        this.logger = Logger.getLogger(LoginServlet.class.getName());
        this.isSpicePlugin = "isSpicePlugin";
        this.sessionAPI = WebclientAPIFactoryProvider.getSessionAPI();
        this.localesProperties = SyMUtil.getLocalesProperties();
    }
    
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String isSpiceLogin = request.getParameter(this.isSpicePlugin);
        final boolean isSpicePluginLogin = isSpiceLogin != null && !isSpiceLogin.isEmpty() && isSpiceLogin.equalsIgnoreCase("true");
        final boolean isAWSLogin = Boolean.parseBoolean(SyMUtil.getServerParameter("IS_AWS_LOGIN"));
        final boolean isAwsDefaultPasswordChanged = this.checkAWSDefaultPasswordChanged(request, isAWSLogin);
        final boolean isDemoMode = ApiFactoryProvider.getDemoUtilAPI().isDemoMode();
        final Properties generalProperties = ProductUrlLoader.getInstance().getGeneralProperites();
        final Properties customerSpecificProperties = SyMUtil.getCustInstallSpecProps();
        final String isRestrictedLoginString = request.getParameter("restictedLogin");
        final boolean isRestrictedLogin = Boolean.parseBoolean(SyMUtil.getSyMParameter("SHOW_RESTICTED_LOGIN"));
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        final String productPromotion = request.getParameter("productPromotion");
        final String disableBanner = request.getParameter("disableBanner");
        final String selectedDB = PersistenceInitializer.getConfigurationValue("DBName");
        final ServletContext context = request.getServletContext();
        this.logger.log(Level.INFO, "isSpicePluginLogin " + isSpicePluginLogin);
        context.setAttribute("generalProperties", (Object)generalProperties);
        context.setAttribute("customerSpecificProps", (Object)customerSpecificProperties);
        this.localeHandling(request);
        this.mobileUserHandling(request);
        this.isPMP(context);
        this.isPasswordChanged(request);
        final boolean isProductSpecificHandlingSuccess = ApiFactoryProvider.getLoginHandler().productSpecificHandling(request, response, this.getServletConfig());
        this.addADLoginProps(request);
        if (!isProductSpecificHandlingSuccess) {
            return;
        }
        if (isRestrictedLoginString != null) {
            SyMUtil.updateSyMParameter("SHOW_RESTICTED_LOGIN", isRestrictedLoginString);
            final RequestDispatcher rd = request.getRequestDispatcher("/logout");
            rd.forward((ServletRequest)request, (ServletResponse)response);
            return;
        }
        if (productPromotion != null) {
            SyMUtil.updateSyMParameter("PRODUCT_PROMO", request.getParameter("productPromotion"));
            final RequestDispatcher rd = request.getRequestDispatcher("/logout");
            rd.forward((ServletRequest)request, (ServletResponse)response);
            return;
        }
        if (disableBanner != null) {
            SyMUtil.updateSyMParameter("DISABLE_PRODUCT_BANNER", String.valueOf(Boolean.parseBoolean(disableBanner)));
            final RequestDispatcher rd = request.getRequestDispatcher("/logout");
            rd.forward((ServletRequest)request, (ServletResponse)response);
            return;
        }
        this.defaultDomainHandling(request);
        SYMClientUtil.setProductInfoInSession(request);
        SYMClientUtil.setCopyRightProps(request);
        this.cacheClearMsgHandling(request);
        this.licenseHandlingDuringLogin(context);
        this.handlingLoginDID(request);
        context.setAttribute("selectedskin", (Object)SyMUtil.getInstance().getTheme());
        context.setAttribute("PROXY_DEFINED", (Object)SyMUtil.getSyMParameter("proxy_defined"));
        context.setAttribute("DEVICE_SCAN", (Object)SyMUtil.getSyMParameter("device_scan"));
        this.handlingDBFailure(request);
        this.setProductUpdateMsgInRequest(request);
        this.setFlashNewsShowStatusInRequest(request);
        this.setDemoModeMsgInRequest(request, context, isDemoMode);
        this.twoFactorHandling(request);
        this.localeForCurrentlyLoggedInUser(request, null);
        context.setAttribute("isOSDEnabled", (Object)DMModuleHandler.isOSDEnabled());
        context.setAttribute("selectedDB", (Object)selectedDB);
        this.updateCacheNumberInRequest(request, response);
        if (isDemoMode) {
            final RequestDispatcher rd = request.getRequestDispatcher(this.getInitParameter("loginDemo"));
            rd.forward((ServletRequest)request, (ServletResponse)response);
        }
        else if (isAWSLogin && !isAwsDefaultPasswordChanged) {
            this.logger.log(Level.INFO, "Redirecting to LOGIN AWS:" + isAWSLogin);
            final RequestDispatcher rd = request.getRequestDispatcher(this.getInitParameter("loginAmazon"));
            rd.forward((ServletRequest)request, (ServletResponse)response);
        }
        else if (isSpicePluginLogin) {
            final RequestDispatcher rd = request.getRequestDispatcher(this.getInitParameter("spicePluginLoginPage"));
            rd.forward((ServletRequest)request, (ServletResponse)response);
        }
        else if (isRestrictedLogin) {
            final RequestDispatcher rd = request.getRequestDispatcher(this.getInitParameter("restrictedLoginPage"));
            rd.forward((ServletRequest)request, (ServletResponse)response);
        }
        else if (isMsp) {
            this.writeStartupStatusToFile(request);
            final RequestDispatcher rd = request.getRequestDispatcher(this.getInitParameter("loginPageMSP"));
            rd.forward((ServletRequest)request, (ServletResponse)response);
        }
        else {
            this.writeStartupStatusToFile(request);
            final RequestDispatcher rd = request.getRequestDispatcher(this.getInitParameter("loginPage"));
            rd.forward((ServletRequest)request, (ServletResponse)response);
        }
    }
    
    private void addADLoginProps(final HttpServletRequest request) {
        String adAuthClass = "ADAuthenticator";
        try {
            final Properties webServerSettings = WebServerUtil.getWebServerSettings();
            final String adAuthClassFromConf = webServerSettings.getProperty("ad.auth.class");
            if (adAuthClassFromConf != null && !adAuthClassFromConf.trim().isEmpty()) {
                adAuthClass = adAuthClassFromConf;
                this.logger.log(Level.INFO, "Auth class has been taken from websettings.conf: " + adAuthClass);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in addADLoginProps", e);
        }
        request.setAttribute("adAuthClass", (Object)adAuthClass);
    }
    
    private boolean checkAWSDefaultPasswordChanged(final HttpServletRequest request, final boolean isAWSLogin) {
        boolean isAwsDefaultPasswordChanged = false;
        this.sessionAPI.addToSession(request, "isAwsInstance", (Object)isAWSLogin);
        this.logger.log(Level.INFO, "isAwsInstance :" + isAWSLogin);
        if (isAWSLogin) {
            isAwsDefaultPasswordChanged = Boolean.parseBoolean(SyMUtil.getServerParameter("IS_AMAZON_DEFAULT_PASSWORD_CHANGED"));
            this.sessionAPI.addToSession(request, "isAwsDefaultPasswordChanged", (Object)isAwsDefaultPasswordChanged);
            this.logger.log(Level.INFO, "isAwsDefaultPasswordChanged :" + isAwsDefaultPasswordChanged);
        }
        return isAwsDefaultPasswordChanged;
    }
    
    private void localeHandling(final HttpServletRequest request) {
        Locale browserLocale = request.getLocale();
        final String browserLocaleString = browserLocale.toString();
        String displayLocale = "en_US";
        final Set<String> localeKeys = this.localesProperties.stringPropertyNames();
        for (final String localeKey : localeKeys) {
            if (localeKey.equalsIgnoreCase(browserLocaleString) || localeKey.contains(browserLocaleString + "_")) {
                displayLocale = localeKey;
                break;
            }
        }
        browserLocale = new Locale(displayLocale, "");
        request.setAttribute("displayLocale", (Object)displayLocale);
        request.setAttribute("browserLocale", (Object)browserLocale);
    }
    
    private void mobileUserHandling(final HttpServletRequest request) {
        final String isAllUsersLoginMobApp = MobileAppUtil.getInstance().isAllUsersLogInMobileApp();
        if (isAllUsersLoginMobApp != null) {
            request.setAttribute("isAllUsersLogInMobApp", (Object)isAllUsersLoginMobApp);
            this.logger.log(Level.INFO, " isAllUsersLogInMobApp - {0}", isAllUsersLoginMobApp);
        }
    }
    
    private void isPMP(final ServletContext context) {
        final boolean isPMP = CustomerInfoUtil.isPMP();
        if (isPMP) {
            context.setAttribute("show_only_patch", (Object)"true");
        }
        else {
            context.setAttribute("show_only_patch", (Object)"false");
        }
    }
    
    private void isPasswordChanged(final HttpServletRequest request) {
        final String isPasswordChanged = SyMUtil.getSyMParameter("IS_PASSWORD_CHANGED");
        request.setAttribute("isPasswordChanged", (Object)isPasswordChanged);
        this.logger.log(Level.INFO, "isPasswordChanged :" + isPasswordChanged);
    }
    
    private void defaultDomainHandling(final HttpServletRequest request) {
        final TreeMap<String, String> domainList = UserController.getADDomainNamesForLoginPage();
        if (domainList != null) {
            final int loginUserCount = DMUserHandler.getUsersCountWithLogin();
            final int adUserCount = DMUserHandler.getADUserCount();
            final String defaultDomain = SyMUtil.getSyMParameter("DEFAULT_DOMAIN");
            request.setAttribute("loginDomainList", (Object)domainList);
            if (defaultDomain != null && !defaultDomain.equalsIgnoreCase("dcLocal")) {
                request.setAttribute("defaultDomainSelect", (Object)defaultDomain);
                return;
            }
            if (domainList.size() == 1 && loginUserCount == adUserCount) {
                request.setAttribute("defaultDomainSelect", (Object)domainList.lastKey());
            }
        }
    }
    
    private void cacheClearMsgHandling(final HttpServletRequest request) {
        String clearCacheBuildNum = SyMUtil.getSyMParameter("BROWSER_CLEAR_CACHE_BUILD_NUMBER");
        if (clearCacheBuildNum == null) {
            clearCacheBuildNum = "-1";
        }
        request.setAttribute("clearCacheBuildNum", (Object)clearCacheBuildNum);
    }
    
    private void licenseHandlingDuringLogin(final ServletContext context) {
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        final String productType = LicenseProvider.getInstance().getProductType();
        final String licenseVersion = LicenseProvider.getInstance().getLicenseVersion();
        context.setAttribute("licenseType", (Object)licenseType);
        if (licenseType != null && !licenseType.equals("")) {
            final String licenseTypeFromDB = SyMUtil.getSyMParameter("licenseType");
            if (licenseTypeFromDB == null || (licenseTypeFromDB != null && !licenseTypeFromDB.equalsIgnoreCase(licenseType))) {
                SyMUtil.updateSyMParameter("licenseType", licenseType);
            }
            if (licenseType.equalsIgnoreCase("F")) {
                this.logger.log(Level.INFO, "Product is running as free edition.");
                context.setAttribute("licenseType", (Object)"F");
            }
        }
        if (productType != null && !productType.equals("")) {
            SyMUtil.updateSyMParameter("productType", productType);
        }
        if (licenseVersion != null && !licenseVersion.equals("")) {
            final String licenseVersionFromDB = SyMUtil.getSyMParameter("licenseVersion");
            if (licenseVersionFromDB == null || (licenseVersionFromDB != null && !licenseVersionFromDB.equalsIgnoreCase(licenseVersion))) {
                SyMUtil.updateSyMParameter("licenseVersion", licenseVersion);
                context.setAttribute("licenseVersion", (Object)licenseVersion);
            }
        }
        this.logger.log(Level.INFO, "Product licenseType : " + context.getAttribute("licenseType") + " productType : " + context.getAttribute("productType") + " licenseVersion : " + context.getAttribute("licenseVersion"));
    }
    
    private void handlingLoginDID(final HttpServletRequest request) {
        String didValue = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING");
        if (didValue == null) {
            didValue = SyMUtil.getDIDValue();
            ApiFactoryProvider.getCacheAccessAPI().putCache("DID_STRING", (Object)didValue);
        }
        request.setAttribute("DID", (Object)didValue);
    }
    
    private void handlingDBFailure(final HttpServletRequest request) {
        final boolean mySQLServerRunning = DBUtil.checkDBStatus();
        this.logger.log(Level.WARNING, "Is MySQL Server Running : " + mySQLServerRunning);
        if (!mySQLServerRunning) {
            this.logger.log(Level.WARNING, "DB Connection Failure occurred. Restart Desktop Central");
            request.setAttribute("errorMessage", (Object)"Database Connection lost! Restart Endpoint Central service to proceed.");
        }
    }
    
    private void setProductUpdateMsgInRequest(final HttpServletRequest request) {
        final HashMap<String, String> updateProductMsg = UpdatesParamUtil.getInstance().getUpdateProductMsg();
        final String updateMsg = updateProductMsg.get("PRODUCT_UPDATE_MSG");
        final String updateURL = updateProductMsg.get("UPDATE_DOWNLOAD_URL");
        final String updateTitle = updateProductMsg.get("PRODUCT_UPDATE_MSG_TITLE");
        final boolean showVersionMsg = Boolean.parseBoolean(UpdatesParamUtil.getUpdParameter("showVersionMsg"));
        final boolean stopMsg = Boolean.parseBoolean(UpdatesParamUtil.getUpdParameter("STOP_VERSION_MESSAGE"));
        final boolean productUpdatesNotification = NotifyUpdatesUtil.getProductUpdatesNotificationSettings();
        final boolean hasCustomPPM = NotifyUpdatesUtil.hasCustomPPM();
        this.logger.log(Level.INFO, "updateMsg : " + updateMsg);
        this.logger.log(Level.INFO, "updateURL : " + updateURL);
        this.logger.log(Level.INFO, "updateTitle : " + updateTitle);
        this.logger.log(Level.INFO, "showVersionMsg : " + showVersionMsg);
        this.logger.log(Level.INFO, "productUpdatesNotification : " + productUpdatesNotification);
        if (!hasCustomPPM && showVersionMsg && !stopMsg && productUpdatesNotification) {
            if (updateMsg != null && !"".equals(updateMsg)) {
                request.setAttribute("updateMsg", (Object)updateMsg);
            }
            if (updateURL != null && !"".equals(updateURL)) {
                request.setAttribute("updateURL", (Object)updateURL);
            }
            if (updateTitle != null && !"".equals(updateTitle)) {
                request.setAttribute("updateTitle", (Object)updateTitle);
            }
        }
    }
    
    private void setFlashNewsShowStatusInRequest(final HttpServletRequest request) {
        try {
            final boolean stopFlashMessage = Boolean.parseBoolean(UpdatesParamUtil.getUpdParameter("STOP_FLASH_MESSAGE"));
            final boolean flashNewsDisable = Boolean.parseBoolean(UpdatesParamUtil.getUpdParameter("FLASH_NEWS_DISABLE"));
            final boolean flashMsgNotification = NotifyUpdatesUtil.getFlashMsgNotificationSettings();
            final String outFileName = SyMUtil.getInstallationDir() + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "images" + File.separator + "flashmsg" + File.separator + "flashMsg.html";
            final File flashFile = new File(outFileName);
            if (!flashNewsDisable && flashFile.exists() && !stopFlashMessage && flashMsgNotification) {
                request.setAttribute("flashShowStatus", (Object)Boolean.TRUE);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in setFlashNewsInRequest : ", ex);
        }
    }
    
    private void setDemoModeMsgInRequest(final HttpServletRequest request, final ServletContext context, final boolean isDemoMode) {
        try {
            context.setAttribute("isDemoMode", (Object)String.valueOf(isDemoMode));
            if (isDemoMode) {
                context.setAttribute("demoModeMessage", (Object)I18N.getMsg("desktopcentral.common.demo.Action_Not_Support", new Object[0]));
                request.setAttribute("LOCALES", (Object)SyMUtil.getLocalesProperties());
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in setDemoModeMsgInRequest : ", ex);
        }
    }
    
    private void twoFactorHandling(final HttpServletRequest request) {
        final boolean isTwoFactorEnabledGlobally = TwoFactorAction.isTwoFactorEnabledGlobaly();
        request.setAttribute("isTwoFactorEnabledGlobaly", (Object)isTwoFactorEnabledGlobally);
        final int otpTimeout = TwoFactorAction.getOtpTimeout();
        if (otpTimeout != 0) {
            request.setAttribute("otpTimeout", (Object)otpTimeout);
        }
    }
    
    private void writeStartupStatusToFile(final HttpServletRequest request) {
        try {
            final String confFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "METracking" + File.separator + "startupinfo.conf";
            final Properties props = StartupUtil.getProperties(confFilePath);
            final Properties loginProps = new Properties();
            ((Hashtable<String, String>)loginProps).put("WebconsoleOpened", "Yes");
            final boolean rFlag = this.isReload(request);
            if (!rFlag) {
                final Object loginStatus = request.getAttribute("login_status");
                if (loginStatus == null) {
                    if (props.containsKey("NullPointerException")) {
                        ((Hashtable<String, String>)loginProps).put("NullPointerException", Integer.toString(Integer.parseInt(((Hashtable<K, Object>)props).get("NullPointerException").toString()) + 1));
                    }
                    else {
                        ((Hashtable<String, String>)loginProps).put("NullPointerException", "1");
                    }
                }
                else {
                    String strLoginStatus = loginStatus.toString();
                    strLoginStatus = strLoginStatus.replaceAll(" ", "");
                    if (strLoginStatus.contains("Nosuchaccountconfigured")) {
                        strLoginStatus = "NoSuchAccountConfigured";
                    }
                    strLoginStatus = "LA_" + strLoginStatus;
                    if (props.containsKey(strLoginStatus)) {
                        ((Hashtable<String, String>)loginProps).put(strLoginStatus, Integer.toString(Integer.parseInt(props.getProperty(strLoginStatus)) + 1));
                    }
                    else {
                        ((Hashtable<String, String>)loginProps).put(strLoginStatus, "1");
                    }
                }
                if (props.containsKey("LA_Count")) {
                    ((Hashtable<String, String>)loginProps).put("LA_Count", Integer.toString(Integer.parseInt(props.getProperty("LA_Count")) + 1));
                }
                else {
                    ((Hashtable<String, String>)loginProps).put("LA_Count", "1");
                }
            }
            this.logger.log(Level.INFO, "Going to write file with properties in LoginServlet " + loginProps.toString());
            ServerTroubleshooterUtil.getInstance().writeStartupFailureInfoToConfFile(loginProps);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in writing  startupinfo conf file :", e);
        }
    }
    
    private boolean isReload(final HttpServletRequest request) {
        boolean flag = true;
        final Enumeration<String> e = request.getAttributeNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            if (name.contains("login_status")) {
                flag = false;
            }
        }
        return flag;
    }
    
    private void localeForCurrentlyLoggedInUser(final HttpServletRequest request, final Long userID) {
        final Locale locale = I18NUtil.getLocale(request.getLocale(), userID);
        this.sessionAPI.addToSession(request, "org.apache.struts.action.LOCALE", (Object)locale);
    }
    
    private void updateCacheNumberInRequest(final HttpServletRequest request, final HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        Cookie showRefMsgCookie = null;
        Cookie cacheNumCookie = null;
        for (final Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase("showRefMsg")) {
                showRefMsgCookie = cookie;
            }
            if (cookie.getName().equalsIgnoreCase("cacheNum")) {
                cacheNumCookie = cookie;
            }
        }
        final String closeRefMsg = request.getParameter("closeRefMsg");
        String currentCacheNumber = SyMUtil.getServerParameter("cachenumber");
        if (currentCacheNumber == null) {
            currentCacheNumber = "1";
            SyMUtil.updateServerParameter("cachenumber", currentCacheNumber);
        }
        if (closeRefMsg != null) {
            cacheNumCookie = DMCookieUtil.generateDMCookies(request, "cacheNum", currentCacheNumber);
            cacheNumCookie.setHttpOnly(true);
            showRefMsgCookie = DMCookieUtil.generateDMCookies(request, "showRefMsg", String.valueOf(false));
            showRefMsgCookie.setHttpOnly(true);
            showRefMsgCookie.setMaxAge(31536000);
            request.setAttribute("showRefMsg", (Object)false);
        }
        else if (showRefMsgCookie == null || showRefMsgCookie.getValue().equalsIgnoreCase(String.valueOf(Boolean.FALSE))) {
            if (cacheNumCookie == null) {
                cacheNumCookie = DMCookieUtil.generateDMCookies(request, "cacheNum", currentCacheNumber);
                cacheNumCookie.setHttpOnly(true);
                showRefMsgCookie = DMCookieUtil.generateDMCookies(request, "showRefMsg", String.valueOf(false));
                showRefMsgCookie.setHttpOnly(true);
                showRefMsgCookie.setMaxAge(31536000);
                request.setAttribute("showRefMsg", (Object)false);
            }
            else if (Integer.parseInt(currentCacheNumber) != Integer.parseInt(cacheNumCookie.getValue())) {
                cacheNumCookie = DMCookieUtil.generateDMCookies(request, "cacheNum", currentCacheNumber);
                cacheNumCookie.setHttpOnly(true);
                showRefMsgCookie = DMCookieUtil.generateDMCookies(request, "showRefMsg", String.valueOf(true));
                showRefMsgCookie.setHttpOnly(true);
                showRefMsgCookie.setMaxAge(31536000);
                request.setAttribute("showRefMsg", (Object)true);
            }
            else {
                showRefMsgCookie = DMCookieUtil.generateDMCookies(request, "showRefMsg", String.valueOf(false));
                showRefMsgCookie.setHttpOnly(true);
                showRefMsgCookie.setMaxAge(31536000);
                request.setAttribute("showRefMsg", (Object)false);
            }
        }
        else {
            showRefMsgCookie = DMCookieUtil.generateDMCookies(request, "showRefMsg", String.valueOf(true));
            showRefMsgCookie.setHttpOnly(true);
            showRefMsgCookie.setMaxAge(31536000);
            request.setAttribute("showRefMsg", (Object)true);
        }
        if (cacheNumCookie != null) {
            response.addCookie(cacheNumCookie);
        }
        if (showRefMsgCookie != null) {
            response.addCookie(showRefMsgCookie);
        }
    }
}
