package com.me.ems.framework.common.api.v1.service;

import com.me.ems.framework.common.api.utils.I18NForModulesUtil;
import com.adventnet.i18n.I18N;
import com.me.framework.server.core.TimezoneUtil;
import javax.ws.rs.core.Response;
import com.me.devicemanagement.framework.server.mobapp.MobileAppUtil;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Properties;
import com.me.ems.framework.common.core.LiveChatUtil;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;

public class CommonService
{
    private static Logger logger;
    public static final String KEY = "key";
    public static final String VALUE = "value";
    private static List<Map<String, String>> loadedLocalesList;
    
    public Map fetchProductMeta() {
        final Map productMeta = new HashMap();
        productMeta.put("productCode", ProductUrlLoader.getInstance().getValue("productcode"));
        productMeta.put("productEdition", LicenseProvider.getInstance().getProductCategoryString());
        productMeta.put("productTitle", ProductUrlLoader.getInstance().getValue("productname"));
        productMeta.put("webURL", ProductUrlLoader.getInstance().getValue("prodUrl"));
        productMeta.put("supportMail", ProductUrlLoader.getInstance().getValue("supportmailid"));
        productMeta.put("trackingCode", ProductUrlLoader.getInstance().getValue("trackingcode"));
        productMeta.put("did", ApiFactoryProvider.getCacheAccessAPI().getCache("DID_STRING", 2));
        productMeta.put("trackigQuickLink", ProductUrlLoader.getInstance().getValue("tracking-quicklinks"));
        productMeta.put("isDemoMode", ApiFactoryProvider.getDemoUtilAPI().isDemoMode());
        return productMeta;
    }
    
    public boolean isGettingStartedClosed(final Long userID, final String gettingStartedParam) {
        boolean isClosed = false;
        try {
            final String closeDB = SyMUtil.getUserParameter(userID, gettingStartedParam);
            if (closeDB != null) {
                isClosed = Boolean.valueOf(closeDB);
            }
        }
        catch (final Exception ex) {
            CommonService.logger.log(Level.SEVERE, "Exception while fetching getting started status for: " + gettingStartedParam, ex);
        }
        return isClosed;
    }
    
    public void closeGettingStarted(final Long userID, final String gettingStartedParam, final String isClosed) {
        try {
            SyMUtil.updateUserParameter(userID, gettingStartedParam, isClosed);
        }
        catch (final Exception ex) {
            CommonService.logger.log(Level.SEVERE, "Exception while updating getting started status for: " + gettingStartedParam, ex);
        }
    }
    
    public boolean getLeftTreeStatus(final Long userID) {
        boolean isLeftTreeEnabled = true;
        try {
            final String isLeftTreeEnabledStr = SyMUtil.getUserParameter(userID, "SHOW_TREE_NAVIGATION");
            isLeftTreeEnabled = (isLeftTreeEnabledStr == null || Boolean.valueOf(isLeftTreeEnabledStr));
        }
        catch (final Exception ex) {
            CommonService.logger.log(Level.SEVERE, "Exception while fetching leftTreeStatus", ex);
        }
        return isLeftTreeEnabled;
    }
    
    public void updateLeftTreeStatus(final Long userID, final boolean leftTreeStatus) throws APIException {
        try {
            SyMUtil.updateUserParameter(userID, "SHOW_TREE_NAVIGATION", leftTreeStatus + "");
        }
        catch (final Exception ex) {
            throw new APIException("GENERIC0002", ex.getMessage(), new String[0]);
        }
    }
    
    public Map<String, Object> getBuildVersionDetails() throws APIException {
        try {
            return ApiFactoryProvider.getBuildVersionAPI().getBuildVersionDetails();
        }
        catch (final Exception ex) {
            CommonService.logger.log(Level.SEVERE, "Exception while fetching versionDetails ", ex);
            throw new APIException("GENERIC0002", ex.getMessage(), new String[0]);
        }
    }
    
    public Map<String, Object> getLiveChatWidgetCode() throws APIException {
        try {
            final Map<String, Object> liveChatData = LiveChatUtil.getInstance().getLiveChatData();
            if (Boolean.TRUE.equals(liveChatData.get("isErrorOccurred"))) {
                throw new APIException("GENERIC0005", "An error occurred while fetching data", new String[0]);
            }
            return liveChatData;
        }
        catch (final APIException apiEx) {
            throw apiEx;
        }
        catch (final Exception ex) {
            CommonService.logger.log(Level.SEVERE, "Exception while fetching liveChat widget code ", ex);
            throw new APIException("GENERIC0002", ex.getMessage(), new String[0]);
        }
    }
    
    public List<Map<String, String>> getLocales() throws APIException {
        if (CommonService.loadedLocalesList == null) {
            try {
                final Properties localeProperties = SyMUtil.getLocalesProperties();
                if (localeProperties == null) {
                    throw new APIException("GENERIC0005", "An error occurred while fetching locales data", new String[0]);
                }
                CommonService.loadedLocalesList = loadPropsToList(localeProperties);
            }
            catch (final APIException apiEx) {
                throw apiEx;
            }
            catch (final Exception ex) {
                CommonService.logger.log(Level.SEVERE, "Exception while fetching locales ", ex);
                throw new APIException("GENERIC0002", ex.getMessage(), new String[0]);
            }
        }
        return CommonService.loadedLocalesList;
    }
    
    private static List<Map<String, String>> loadPropsToList(final Properties properties) {
        final List<Map<String, String>> returnList = new ArrayList<Map<String, String>>(properties.size());
        final Map<Object, Object> sortedMap = new TreeMap<Object, Object>(properties);
        for (final Map.Entry<Object, Object> entry : sortedMap.entrySet()) {
            final Map<String, String> objectMap = new HashMap<String, String>(3);
            final String key = entry.getKey();
            final String value = entry.getValue();
            objectMap.put("key", key);
            objectMap.put("value", value);
            returnList.add(objectMap);
        }
        return returnList;
    }
    
    public List<Map<String, String>> getTimeFormat() throws APIException {
        try {
            final Properties timeProperties = SyMUtil.getTimeProperties();
            if (timeProperties.isEmpty()) {
                throw new APIException("GENERIC0005", "An error occurred while fetching Time Format data", new String[0]);
            }
            return loadPropsToList(timeProperties);
        }
        catch (final APIException apiEx) {
            throw apiEx;
        }
        catch (final Exception ex) {
            CommonService.logger.log(Level.SEVERE, "Exception while fetching TimeFormat ", ex);
            throw new APIException("GENERIC0002", ex.getMessage(), new String[0]);
        }
    }
    
    public Map<String, Boolean> mobileAppUsedDetails() {
        final Map<String, Boolean> responseMap = new HashMap<String, Boolean>(3);
        final String appUser = MobileAppUtil.getInstance().updateMobileAppLoginUser();
        final String isIOSUser = "isIOSUser";
        final String isAndroidUser = "isAndroidUser";
        if (appUser == null) {
            responseMap.put(isIOSUser, Boolean.TRUE);
            responseMap.put(isAndroidUser, Boolean.TRUE);
        }
        else if (appUser.equals("None")) {
            responseMap.put(isIOSUser, Boolean.FALSE);
            responseMap.put(isAndroidUser, Boolean.FALSE);
        }
        else if (appUser.equals("iOS")) {
            responseMap.put(isIOSUser, Boolean.FALSE);
            responseMap.put(isAndroidUser, Boolean.TRUE);
        }
        else if (appUser.equals("Android")) {
            responseMap.put(isIOSUser, Boolean.TRUE);
            responseMap.put(isAndroidUser, Boolean.FALSE);
        }
        return responseMap;
    }
    
    public Map isScheduledTimeValid(final Map timeData) throws Exception {
        final Map validationStatus = new HashMap();
        try {
            if (!timeData.containsKey("onceTime") || !timeData.containsKey("taskTimeZone")) {
                throw new APIException(Response.Status.PRECONDITION_FAILED, "GENERIC0003", "time data for validation is missing");
            }
            final String scheduleTime = timeData.get("onceTime");
            final String scheduleTimeZone = timeData.get("taskTimeZone");
            validationStatus.put("isvalid", TimezoneUtil.ValidateTime(scheduleTime, scheduleTimeZone));
        }
        catch (final Exception ex) {
            CommonService.logger.log(Level.SEVERE, "Exception while validating time for different timezone", ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "dc.common.validation_failed");
        }
        return validationStatus;
    }
    
    public Map<String, Object> getProductLoaderProperties() throws APIException {
        try {
            final Map<String, Object> productProperties = new HashMap<String, Object>(4);
            productProperties.put("helpUrls", this.getHelpURLs());
            productProperties.put("productProperties", SyMUtil.getProductInfo());
            return productProperties;
        }
        catch (final Exception ex) {
            CommonService.logger.log(Level.SEVERE, "Exception while fetching productLoaderProperties ", ex);
            throw new APIException("GENERIC0005");
        }
    }
    
    public Map<String, Object> getHelpURLs() throws APIException {
        try {
            final Map<String, Object> helpUrls = new HashMap<String, Object>();
            helpUrls.put("did", SyMUtil.getDIDValue());
            helpUrls.put("supportMailId", this.getGeneralPropValue("supportmailid", true));
            helpUrls.put("privacyPolicyUrl", this.getGeneralPropValue("privacy_policy", false));
            helpUrls.put("requestDemoUrl", this.getGeneralPropValue("requestdemo", true));
            helpUrls.put("localizationUrl", this.getGeneralPropValue("localization_url", false));
            helpUrls.put("getQuoteUrl", this.getGeneralPropValue("get_quote", true));
            helpUrls.put("featureRequestUrl", this.getGeneralPropValue("featurerequest", true));
            helpUrls.put("requestSupportUrl", this.getGeneralPropValue("requestsupport", true));
            helpUrls.put("helpUrl", this.getGeneralPropValue("help", true));
            helpUrls.put("kbUrl", this.getGeneralPropValue("kb", true));
            helpUrls.put("faqUrl", this.getGeneralPropValue("faq", true));
            helpUrls.put("forumsUrl", this.getGeneralPropValue("forums_url", true));
            helpUrls.put("forumsNewTopicUrl", this.getGeneralPropValue("forums_newtopic", true));
            helpUrls.put("storeUrl", this.getGeneralPropValue("store_url", true));
            helpUrls.put("productDownloadUrl", this.getGeneralPropValue("download", true));
            helpUrls.put("desktopcentralTodayUrl", this.getGeneralPropValue("desktopcentral_today", true));
            helpUrls.put("secAddonDetailsUrl", this.getGeneralPropValue("sec_addon_details", false));
            helpUrls.entrySet().removeIf(entry -> null == entry.getValue());
            return helpUrls;
        }
        catch (final Exception ex) {
            throw new APIException("GENERIC0005");
        }
    }
    
    private String getGeneralPropValue(final String generalPropKey, final boolean isI18nKey) throws Exception {
        final String propValue = ProductUrlLoader.getInstance().getValue(generalPropKey);
        if (propValue != null && !propValue.isEmpty()) {
            return isI18nKey ? I18N.getMsg(propValue, new Object[0]) : propValue;
        }
        return null;
    }
    
    public Map<String, String> getI18NForModules(final List<String> modules, final Boolean isServer, final Boolean isClient) throws APIException {
        Map<String, String> i18NKeysForModules = new HashMap<String, String>();
        try {
            i18NKeysForModules = I18NForModulesUtil.getI18NForModules(modules, isServer, isClient);
        }
        catch (final Exception ex) {
            CommonService.logger.log(Level.INFO, "Exception while fetching I18N keys.", ex);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", ex.getMessage());
        }
        return i18NKeysForModules;
    }
    
    static {
        CommonService.logger = Logger.getLogger(CommonService.class.getName());
    }
}
