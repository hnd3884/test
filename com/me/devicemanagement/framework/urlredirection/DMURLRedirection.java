package com.me.devicemanagement.framework.urlredirection;

import org.json.JSONObject;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.me.devicemanagement.framework.server.cache.CacheAccessAPI;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.ds.query.Criteria;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class DMURLRedirection
{
    private static String did;
    private static Logger logger;
    private static ResourceBundle fullUrlBundle;
    private static ResourceBundle relativeURLBundle_US;
    private static ResourceBundle relativeURLBundle_locale;
    private static String language;
    private static String country;
    private static String variant;
    private static String category;
    public static Properties foundKeys;
    public static String redirectURLFolderName;
    public static String resourceBundleRootFolderName;
    public static boolean redirectURLFindFromMemory;
    public static String redirectURLDefaultLanguage;
    public static String redirectURLDefaultCountry;
    public static String redirectURLDefaultVariant;
    public static boolean redirectURLExceptionLogEnabled;
    
    public static void setLocaleAndRelativeURL() throws DMURLRedirectionException {
        setLocale();
        setURLBundle();
    }
    
    public static void setLocale() throws DMURLRedirectionException {
        try {
            final Locale currentLocale = Locale.getDefault();
            final String lang = currentLocale.getLanguage();
            final String installedCountry = currentLocale.getCountry();
            final String varian = currentLocale.getVariant();
            DMURLRedirection.language = lang;
            DMURLRedirection.country = installedCountry;
            DMURLRedirection.variant = varian;
        }
        catch (final Exception ex) {
            DMURLRedirection.language = "";
            DMURLRedirection.country = "";
            DMURLRedirection.variant = "";
            printExceptionTrace("Unable to set the Locale due to the Exception : ", ex, 4001);
        }
    }
    
    public static void setLocaleOnDemand(final String lang, final String countr, final String varian) throws DMURLRedirectionException {
        try {
            DMURLRedirection.language = lang;
            DMURLRedirection.country = countr;
            if (varian == null || varian.trim().length() < 1) {
                DMURLRedirection.variant = "";
            }
            else {
                DMURLRedirection.variant = varian;
            }
        }
        catch (final Exception ex) {
            DMURLRedirection.language = "";
            DMURLRedirection.country = "";
            DMURLRedirection.variant = "";
            printExceptionTrace("Unable to set the Locale due to the Exception : ", ex, 4001);
        }
    }
    
    public static void setURLBundle() throws DMURLRedirectionException {
        try {
            final Locale defaultLocale = new Locale(DMURLRedirection.redirectURLDefaultLanguage, DMURLRedirection.redirectURLDefaultCountry);
            Locale locale = Locale.US;
            if (DMURLRedirection.language != null && DMURLRedirection.language.trim().length() > 0) {
                if (DMURLRedirection.country != null && DMURLRedirection.country.trim().length() > 0) {
                    if (DMURLRedirection.variant != null && DMURLRedirection.variant.trim().length() > 0) {
                        locale = new Locale(DMURLRedirection.language, DMURLRedirection.country, DMURLRedirection.variant);
                    }
                    else {
                        locale = new Locale(DMURLRedirection.language, DMURLRedirection.country);
                    }
                }
                else {
                    locale = new Locale(DMURLRedirection.language);
                }
            }
            final String fullURLBundleName = getBundleName(DMURLRedirection.redirectURLFolderName + ".fullURL", locale);
            String fullURLBundleFile = "";
            if (DMURLRedirection.resourceBundleRootFolderName != null && DMURLRedirection.resourceBundleRootFolderName.trim().length() > 0) {
                fullURLBundleFile = System.getProperty("server.home") + File.separator + DMURLRedirection.resourceBundleRootFolderName;
            }
            else {
                fullURLBundleFile = ApiFactoryProvider.getServerSettingsAPI().getResourceBundleRootDirectory() + File.separator + fullURLBundleName.replaceAll("\\.", "\\\\") + ".properties";
            }
            if (new File(fullURLBundleFile).exists()) {
                DMURLRedirection.fullUrlBundle = ResourceBundle.getBundle(DMURLRedirection.redirectURLFolderName + ".fullURL", locale);
            }
            else {
                DMURLRedirection.fullUrlBundle = null;
            }
            DMURLRedirection.relativeURLBundle_US = ResourceBundle.getBundle(DMURLRedirection.redirectURLFolderName + ".relativeURL", defaultLocale);
            DMURLRedirection.relativeURLBundle_locale = ResourceBundle.getBundle(DMURLRedirection.redirectURLFolderName + ".relativeURL", locale);
        }
        catch (final Exception ex) {
            printExceptionTrace("Unable to setURLBundle due to the Exception : ", ex, 4002);
        }
    }
    
    public static String getURL(final String key) throws DMURLRedirectionException {
        if (DMURLRedirection.language == null || DMURLRedirection.language.trim().length() < 1) {
            setLocaleAndRelativeURL();
        }
        if (DMURLRedirection.relativeURLBundle_locale == null) {
            setURLBundle();
        }
        return getURLValue(key);
    }
    
    public static String getURLOnDemand(final String lang, final String countr, final String varian, final String key) throws DMURLRedirectionException {
        setLocaleOnDemand(lang, countr, varian);
        setURLBundle();
        return getURLValue(key);
    }
    
    public static String getURLValue(final String key) throws DMURLRedirectionException {
        if (DMURLRedirection.redirectURLFindFromMemory) {
            return getURLValueFromMemory(key);
        }
        return getvalue(key);
    }
    
    public static String getURLValueFromMemory(final String key) throws DMURLRedirectionException {
        if (DMURLRedirection.foundKeys == null) {
            DMURLRedirection.foundKeys = new Properties();
        }
        if (DMURLRedirection.foundKeys.size() > 0 && DMURLRedirection.foundKeys.containsKey(key + "_" + DMURLRedirection.language)) {
            return DMURLRedirection.foundKeys.getProperty(key + "_" + DMURLRedirection.language);
        }
        final String value = getvalue(key);
        DMURLRedirection.foundKeys.setProperty(key + "_" + DMURLRedirection.language, value);
        return value;
    }
    
    private static String getvalue(final String key) throws DMURLRedirectionException {
        String value = null;
        if (DMURLRedirection.fullUrlBundle != null && DMURLRedirection.fullUrlBundle.containsKey(key)) {
            value = appendTrackCode(DMURLRedirection.fullUrlBundle.getString(key));
        }
        else if (DMURLRedirection.relativeURLBundle_locale.containsKey(key)) {
            value = replaceUrl(DMURLRedirection.relativeURLBundle_locale.getString(key), Boolean.FALSE);
        }
        else if (DMURLRedirection.relativeURLBundle_US.containsKey(key)) {
            value = replaceUrl(DMURLRedirection.relativeURLBundle_US.getString(key), Boolean.TRUE);
        }
        else {
            printExceptionTrace("Unable find the key from proreties ", new Exception("UNABLE TO FIND THE GIVEN KEY FROM URL PROPERTY FILES : " + key), 4006);
        }
        return value;
    }
    
    public static List getIdentityKeys() throws DMURLRedirectionException {
        final List availableKeys = new ArrayList();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("RedirectUrl"));
            query.addSelectColumn(new Column("RedirectUrl", "RedirectUrl_ID"));
            query.addSelectColumn(new Column("RedirectUrl", "IDENDITY_KEY"));
            final DataObject resultDO = DataAccess.get(query);
            final Iterator iterator = resultDO.getRows("RedirectUrl");
            while (iterator.hasNext()) {
                final Row resRow = iterator.next();
                final String idendityKeyName = (String)resRow.get("IDENDITY_KEY");
                if (!availableKeys.contains(idendityKeyName)) {
                    availableKeys.add(idendityKeyName);
                }
            }
        }
        catch (final Exception ex) {
            printExceptionTrace("Unable to get Identity Keys from DB due to the Exception : ", ex, 4003);
        }
        return availableKeys;
    }
    
    public static String replaceUrl(final String url, final boolean isDefaultURL) throws DMURLRedirectionException {
        String urlvalue = "";
        String resultUrl = appendTrackCode(url);
        final List availableKeys = getIdentityKeys();
        if (availableKeys.size() > 0) {
            for (int i = 0; i < availableKeys.size(); ++i) {
                if (url.contains(availableKeys.get(i).toString())) {
                    final String key = "$(" + availableKeys.get(i).toString() + ")";
                    if (isDefaultURL) {
                        urlvalue = getDefaultURL(availableKeys.get(i).toString());
                    }
                    else {
                        urlvalue = getLocaleURL(availableKeys.get(i).toString());
                    }
                    resultUrl = resultUrl.replace(key, urlvalue);
                    return resultUrl;
                }
            }
        }
        if (resultUrl.contains("$(")) {
            printExceptionTrace("Unable to get replace the key/place holder : ", new Exception("UNABLE TO GENERATE THE URL SINCE KEY WAS NOT AVAILABLE FROM DB"), 4005);
        }
        return resultUrl;
    }
    
    public static String getLocaleURL(final String identifyKey) throws DMURLRedirectionException {
        String countryValue = null;
        String variantValue = null;
        if (DMURLRedirection.country != null && !DMURLRedirection.country.equals("")) {
            countryValue = DMURLRedirection.country;
        }
        else {
            countryValue = "--";
        }
        if (DMURLRedirection.variant != null && !DMURLRedirection.variant.equals("")) {
            variantValue = DMURLRedirection.variant;
        }
        else {
            variantValue = "--";
        }
        return getURLFromDB(DMURLRedirection.language, countryValue, variantValue, identifyKey);
    }
    
    public static String getDefaultURL(final String identifyKey) throws DMURLRedirectionException {
        return getURLFromDB(DMURLRedirection.redirectURLDefaultLanguage, DMURLRedirection.redirectURLDefaultCountry, DMURLRedirection.redirectURLDefaultVariant, identifyKey);
    }
    
    public static String getURLFromDB(final String language, final String country, final String variant, final String idendityKey) throws DMURLRedirectionException {
        String url = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("RedirectUrl"));
            query.addSelectColumn(new Column("RedirectUrl", "*"));
            final Criteria cri = new Criteria(Column.getColumn("RedirectUrl", "SUPPORTED_LANGUAGE"), (Object)language, 0);
            cri.and(new Criteria(Column.getColumn("RedirectUrl", "SUPPORTED_COUNTRY"), (Object)country, 0));
            cri.and(new Criteria(Column.getColumn("RedirectUrl", "SUPPORTED_VARIANT"), (Object)variant, 0));
            cri.and(new Criteria(Column.getColumn("RedirectUrl", "IDENDITY_KEY"), (Object)idendityKey, 0));
            query.setCriteria(cri);
            final DataObject resultDO = DataAccess.get(query);
            final Row row = resultDO.getRow("RedirectUrl");
            if (row != null) {
                url = (String)row.get("SUPPORTED_URL");
            }
        }
        catch (final Exception ex) {
            printExceptionTrace("Unable to get URL from DB due to the Exception : ", ex, 4004);
        }
        return url;
    }
    
    public static String appendTrackCode(final String url) {
        setDidValue();
        String resultUrl = url;
        final String trackingCodeValue = ProductUrlLoader.getInstance().getValue("trackingcode");
        if (url.contains("traceurl")) {
            resultUrl = url.replace("$(traceurl)", "?" + trackingCodeValue + DMURLRedirection.did);
        }
        else if (url.contains("html?")) {
            resultUrl = url.replace("html?", "html?" + trackingCodeValue + DMURLRedirection.did);
        }
        else if (url.contains("html")) {
            resultUrl = url.replace("html", "html?" + trackingCodeValue + DMURLRedirection.did);
        }
        return resultUrl;
    }
    
    public static void printExceptionTrace(final String warningMessage, final Exception e, final int errorCode) throws DMURLRedirectionException {
        if (DMURLRedirection.redirectURLExceptionLogEnabled) {
            DMURLRedirection.logger.log(Level.WARNING, warningMessage, e);
        }
        throw new DMURLRedirectionException(e, errorCode);
    }
    
    public static void setDidValue() {
        String didValue = null;
        try {
            final String className = ProductClassLoader.getSingleImplProductClass("DM_CACHE_ACCESS_API_CLASS");
            if (className != null && className.trim().length() > 0) {
                final CacheAccessAPI cacheAPI = (CacheAccessAPI)Class.forName(className).newInstance();
                didValue = (String)cacheAPI.getCache("DID_STRING", 2);
            }
        }
        catch (final Exception ex) {
            DMURLRedirection.logger.log(Level.WARNING, "Unable to set DID value due to the exception : ", ex);
        }
        DMURLRedirection.did = ((didValue != null) ? ("&did=" + didValue) : "&did=");
    }
    
    public static String getBundleName(final String baseName, final Locale locale) {
        if (locale == Locale.ROOT) {
            return baseName;
        }
        final String language = locale.getLanguage();
        final String script = locale.getScript();
        final String country = locale.getCountry();
        final String variant = locale.getVariant();
        if (language == "" && country == "" && variant == "") {
            return baseName;
        }
        final StringBuilder sb = new StringBuilder(baseName);
        sb.append('_');
        if (script != "") {
            if (variant != "") {
                sb.append(language).append('_').append(script).append('_').append(country).append('_').append(variant);
            }
            else if (country != "") {
                sb.append(language).append('_').append(script).append('_').append(country);
            }
            else {
                sb.append(language).append('_').append(script);
            }
        }
        else if (variant != "") {
            sb.append(language).append('_').append(country).append('_').append(variant);
        }
        else if (country != "") {
            sb.append(language).append('_').append(country);
        }
        else {
            sb.append(language);
        }
        return sb.toString();
    }
    
    static {
        DMURLRedirection.logger = Logger.getLogger(DMURLRedirection.class.getName());
        DMURLRedirection.category = "urlredirection";
        try {
            final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
            DMURLRedirection.redirectURLFolderName = String.valueOf(((JSONObject)frameworkConfigurations.get(DMURLRedirection.category)).get("redirect.url_folder.name"));
            DMURLRedirection.resourceBundleRootFolderName = String.valueOf(((JSONObject)frameworkConfigurations.get(DMURLRedirection.category)).get("resourcebundle.root_folder_name"));
            DMURLRedirection.redirectURLFindFromMemory = Boolean.parseBoolean(String.valueOf(((JSONObject)frameworkConfigurations.get(DMURLRedirection.category)).get("redirect.findurl_from_memory")));
            DMURLRedirection.redirectURLDefaultLanguage = String.valueOf(((JSONObject)frameworkConfigurations.get(DMURLRedirection.category)).get("redirect.default_language"));
            DMURLRedirection.redirectURLDefaultCountry = String.valueOf(((JSONObject)frameworkConfigurations.get(DMURLRedirection.category)).get("redirect.default_country"));
            DMURLRedirection.redirectURLDefaultVariant = String.valueOf(((JSONObject)frameworkConfigurations.get(DMURLRedirection.category)).get("redirect.default_variant"));
            DMURLRedirection.redirectURLExceptionLogEnabled = Boolean.parseBoolean(String.valueOf(((JSONObject)frameworkConfigurations.get(DMURLRedirection.category)).get("redirect.exception_trace_enabled")));
        }
        catch (final Exception ex) {
            DMURLRedirection.logger.log(Level.WARNING, "Exception while retrieving data from framework configuration. ", ex);
        }
    }
}
