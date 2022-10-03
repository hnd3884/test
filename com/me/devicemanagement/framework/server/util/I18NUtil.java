package com.me.devicemanagement.framework.server.util;

import java.io.File;
import com.me.devicemanagement.framework.server.search.AdvSearchCommonUtil;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.MissingResourceException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Properties;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import java.util.Locale;
import java.util.logging.Logger;

public class I18NUtil
{
    private static Logger logger;
    public static final String I18N_OPENTAG = "[i18n]";
    public static final String I18N_CLOSETAG = "[/i18n]";
    public static final String I18N_ARGS_DELIMITER = "@@@";
    public static final String NO_I18N = "NO_I18N";
    
    private I18NUtil() {
    }
    
    public static String getString(final String key, final Object param, final Locale locale, final Long userID) {
        return getString(key, new Object[] { param }, locale, userID);
    }
    
    public static String getString(final String key, final Object[] params, final Locale locale, final Long userID) {
        String value = null;
        try {
            value = I18N.getMsg(key, params);
        }
        catch (final Exception e) {
            I18NUtil.logger.log(Level.WARNING, "Exception while getting i18n value for the key : " + key + " and locale : " + locale, e);
            value = key;
        }
        return value;
    }
    
    public static String getString(final String key, final Locale locale, final Long userID) {
        return getString(key, locale);
    }
    
    private static String getString(final String key, final Locale locale) {
        String value = null;
        try {
            value = I18N.getMsg(key, new Object[0]);
        }
        catch (final Exception ex) {
            I18NUtil.logger.log(Level.WARNING, "Exception while getting i18n value fot the key : " + key + " and locale : " + locale, ex);
            value = key;
        }
        return value;
    }
    
    public static Locale getLocale(final Locale browserLocale, final Long userID) {
        return Locale.US;
    }
    
    public static String[] parseI18NString(final String praseStr) {
        String i18NStr = praseStr;
        final String[] key_Argu = new String[2];
        I18NUtil.logger.log(Level.FINE, "Going to Parse this string: " + praseStr);
        try {
            if (i18NStr.contains("[i18n]")) {
                i18NStr = i18NStr.substring(i18NStr.indexOf("[i18n]") + "[i18n]".length(), i18NStr.length() - "[/i18n]".length());
                final int firstSep_Index = i18NStr.indexOf("@@@");
                if (firstSep_Index != -1) {
                    key_Argu[0] = i18NStr.substring(0, firstSep_Index);
                    key_Argu[1] = i18NStr.substring(firstSep_Index + "@@@".length());
                    I18NUtil.logger.log(Level.FINE, "Key_Args[0]: " + key_Argu[0] + "key_Argu[1] " + key_Argu[1]);
                }
                else {
                    key_Argu[0] = i18NStr;
                    key_Argu[1] = null;
                    I18NUtil.logger.log(Level.FINE, "Key_Args[0]: " + key_Argu[0]);
                }
            }
            else {
                key_Argu[0] = i18NStr;
                key_Argu[1] = "NO_I18N";
                I18NUtil.logger.log(Level.FINE, "Key_Args[0]: " + key_Argu[0]);
            }
        }
        catch (final Exception e) {
            I18NUtil.logger.log(Level.WARNING, "Exception in parseI18NString :: " + praseStr, e);
        }
        return key_Argu;
    }
    
    public static String parseI18NStringInEnglish(final String parseStr) {
        try {
            final String[] strArr = parseI18NString(parseStr);
            String args = null;
            if (strArr.length > 1) {
                args = strArr[1];
            }
            return transformRemarksInEnglish(strArr[0], args);
        }
        catch (final Exception e) {
            I18NUtil.logger.log(Level.WARNING, "Exception while parsing I18N String to English : " + e);
            return null;
        }
    }
    
    public static String transformRemarks(final String remarks, final String remarksArgs, final boolean toEnglish) {
        String transformedString = null;
        try {
            if (remarksArgs == null || remarksArgs.trim().length() == 0) {
                if (!toEnglish) {
                    transformedString = I18N.getMsg(remarks, new Object[0]);
                }
                else {
                    transformedString = getMsg(null, remarks, new Object[0]);
                }
            }
            else if (remarksArgs != null && remarksArgs.equalsIgnoreCase("NO_I18N")) {
                transformedString = remarks;
            }
            else if (remarksArgs != null) {
                final Object[] arguments = remarksArgs.split("@@@");
                if (!toEnglish) {
                    transformedString = I18N.getMsg(remarks, arguments);
                }
                else {
                    transformedString = getMsg(null, remarks, arguments);
                }
            }
        }
        catch (final Exception e) {
            I18NUtil.logger.log(Level.WARNING, "Exception while transforming remarks column to get localized String : " + e);
            transformedString = remarks;
            return transformedString;
        }
        return transformedString;
    }
    
    public static String transformRemarks(final String remarks, final String remarksArgs) {
        return transformRemarks(remarks, remarksArgs, false);
    }
    
    public static String transformRemarksInEnglish(final String remarks, final String remarksArgs) {
        return transformRemarks(remarks, remarksArgs, true);
    }
    
    public static String transformRemarksAndArguments(final String remarks, String remarksArgs) {
        String transformedArgs = null;
        while (remarksArgs.indexOf("@@@") > 0) {
            String arg = null;
            if (remarksArgs.startsWith("[i18n]")) {
                arg = remarksArgs.substring(remarksArgs.indexOf("[i18n]"), remarksArgs.indexOf("[/i18n]") + "[/i18n]".length());
                remarksArgs = remarksArgs.substring(remarksArgs.indexOf("[/i18n]") + "[/i18n]".length());
                if (remarksArgs.startsWith("@@@")) {
                    remarksArgs = remarksArgs.substring(remarksArgs.indexOf("@@@") + "@@@".length());
                }
                arg = transformArgumentRemarks(arg);
            }
            else {
                arg = remarksArgs.substring(0, remarksArgs.indexOf("@@@"));
                remarksArgs = remarksArgs.substring(remarksArgs.indexOf("@@@") + "@@@".length());
            }
            if (transformedArgs == null) {
                transformedArgs = arg;
            }
            else {
                transformedArgs = transformedArgs + "@@@" + arg;
            }
        }
        if (transformedArgs == null) {
            transformedArgs = remarksArgs;
        }
        else {
            transformedArgs = transformedArgs + "@@@" + remarksArgs;
        }
        I18NUtil.logger.log(Level.FINEST, "Transformed arguments : " + transformedArgs);
        return transformRemarks(remarks, transformedArgs, false);
    }
    
    public static Locale getLocaleEN() {
        return Locale.ENGLISH;
    }
    
    public static Locale getUserLocaleFromSession() {
        try {
            return ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
        }
        catch (final Exception ex) {
            Logger.getLogger(I18NUtil.class.getName()).log(Level.SEVERE, null, ex);
            return Locale.US;
        }
    }
    
    public static Locale getDefaultLocale() {
        String languageCode = "en";
        String countryCode = "US";
        final Properties localeProp = SyMUtil.getLocalesProperties();
        if (LicenseProvider.getInstance().isLanguagePackEnabled() && localeProp != null) {
            final Locale userLocale = Locale.getDefault();
            if (localeProp.containsKey(userLocale.toString())) {
                languageCode = userLocale.getLanguage();
                countryCode = userLocale.getCountry();
            }
        }
        return new Locale(languageCode, countryCode);
    }
    
    public static Locale getUserLocaleFromDB(final Long loginID) {
        String languageCode = "en";
        String countryCode = "US";
        final Properties localeProp = SyMUtil.getLocalesProperties();
        if (LicenseProvider.getInstance().isLanguagePackEnabled() && localeProp != null) {
            final Locale userLocale = Locale.getDefault();
            if (localeProp.containsKey(userLocale.toString())) {
                languageCode = userLocale.getLanguage();
                countryCode = userLocale.getCountry();
            }
        }
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AaaUserProfile"));
            query.addSelectColumn(new Column("AaaUserProfile", "*"));
            final Join userJoin = new Join("AaaUserProfile", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            final Join loginJoin = new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            query.addJoin(userJoin);
            query.addJoin(loginJoin);
            final Criteria criteria = new Criteria(Column.getColumn("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
            query.setCriteria(criteria);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            final Row userRow = dobj.getRow("AaaUserProfile");
            if (userRow != null) {
                languageCode = (String)userRow.get("LANGUAGE_CODE");
                countryCode = (String)userRow.get("COUNTRY_CODE");
            }
        }
        catch (final Exception e) {
            I18NUtil.logger.log(Level.WARNING, "Exception while getting user locale from db : ", e);
        }
        return new Locale(languageCode, countryCode);
    }
    
    public static boolean isUserLangIsEng() {
        final Locale userLocale = getUserLocaleFromSession();
        final String userLang = userLocale.getLanguage();
        return userLang.equalsIgnoreCase("en");
    }
    
    public static String getMsg(final Long loginID, final String msgKey, final Object... args) throws Exception {
        if (msgKey == null) {
            return null;
        }
        final Locale userLocale = getUserLocaleFromDB(loginID);
        ResourceBundle bundle = null;
        try {
            bundle = I18N.getBundle("ApplicationResources", userLocale);
        }
        catch (final Exception e) {
            I18NUtil.logger.log(Level.FINEST, "could not load properties file for locale {0} as well as en_US", userLocale);
            return msgKey;
        }
        String val = msgKey;
        if (bundle == null) {
            I18NUtil.logger.log(Level.FINEST, "could not load properties file for locale {0} as well as en_US", userLocale);
            return msgKey;
        }
        try {
            val = bundle.getString(msgKey);
        }
        catch (final MissingResourceException e2) {
            I18NUtil.logger.log(Level.FINEST, "key {0} has no entries in properties. Locale is {1}", new Object[] { msgKey, userLocale.toString() });
            return msgKey;
        }
        if (val != null && val.contains("{productName}")) {
            final String productName = ProductUrlLoader.getInstance().getGeneralProperites().getProperty("displayname");
            val = val.replaceAll("\\{productName}", productName);
        }
        return MessageFormat.format(val, args);
    }
    
    public static String getUsedLocales() {
        String usedLocales = "en";
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AaaUserProfile"));
            query.addSelectColumn(new Column("AaaUserProfile", "USER_ID"));
            query.addSelectColumn(new Column("AaaUserProfile", "LANGUAGE_CODE"));
            final Join userJoin = new Join("AaaUserProfile", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            final Join loginJoin = new Join("AaaUser", "AaaLogin", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2);
            final Criteria criteria = new Criteria(Column.getColumn("AaaUserProfile", "LANGUAGE_CODE"), (Object)usedLocales, 1);
            query.addJoin(userJoin);
            query.addJoin(loginJoin);
            query.setCriteria(criteria);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            if (!dobj.isEmpty()) {
                final Iterator userItr = dobj.getRows("AaaUserProfile");
                while (userItr.hasNext()) {
                    final Row row = userItr.next();
                    usedLocales = usedLocales + "," + (String)row.get("LANGUAGE_CODE");
                }
            }
        }
        catch (final Exception e) {
            I18NUtil.logger.log(Level.WARNING, "Exception while getting used locale from db : ", e);
        }
        return usedLocales;
    }
    
    public static String transformArgumentRemarks(String remarksArgs) {
        final String[] parsedArgs = parseI18NString(remarksArgs);
        String arguments = null;
        if (parsedArgs.length > 1) {
            final int size = parsedArgs.length - 1;
            arguments = "";
            for (int a = 1; a < size; ++a) {
                arguments = arguments + parsedArgs[a] + "@@@";
            }
            arguments += parsedArgs[size];
        }
        remarksArgs = transformRemarks(parsedArgs[0], arguments);
        return remarksArgs;
    }
    
    public static String getMsgFromLocale(final Locale userLocale, final String msgKey, final Object... args) throws Exception {
        if (msgKey == null) {
            return null;
        }
        ResourceBundle bundle = null;
        try {
            bundle = I18N.getBundle("ApplicationResources", userLocale);
        }
        catch (final Exception e) {
            I18NUtil.logger.log(Level.FINEST, "could not load properties file for locale {0} as well as en_US", userLocale);
            return msgKey;
        }
        String val = msgKey;
        if (bundle == null) {
            I18NUtil.logger.log(Level.FINEST, "could not load properties file for locale {0} as well as en_US", userLocale);
            return msgKey;
        }
        try {
            val = bundle.getString(msgKey);
        }
        catch (final MissingResourceException e2) {
            I18NUtil.logger.log(Level.FINEST, "key {0} has no entries in properties. Locale is {1}", new Object[] { msgKey, userLocale.toString() });
            return msgKey;
        }
        if (val != null && val.contains("{productName}")) {
            String productName = "Central Server";
            if (AdvSearchCommonUtil.productCode != null && !AdvSearchCommonUtil.productCode.isEmpty()) {
                final String genPropsFile = AdvSearchCommonUtil.SERVER_HOME + File.separator + "conf" + File.separator + "general_properties.conf";
                final Properties generalProperties = AdvSearchCommonUtil.getInstance().readGeneralProperties(genPropsFile, AdvSearchCommonUtil.productCode);
                productName = generalProperties.getProperty("displayname");
            }
            val = val.replaceAll("\\{productName}", productName);
        }
        return MessageFormat.format(val, args);
    }
    
    public static String getJSMsgFromLocale(final Locale userLocale, final String msgKey, final Object... args) throws Exception {
        if (msgKey == null) {
            return null;
        }
        ResourceBundle bundle = null;
        try {
            bundle = I18N.getBundle("JSApplicationResources", userLocale);
        }
        catch (final Exception e) {
            I18NUtil.logger.log(Level.FINEST, "could not load JSApplicationResources properties file for locale {0} as well as en_US", userLocale);
            return msgKey;
        }
        String val = msgKey;
        if (bundle == null) {
            I18NUtil.logger.log(Level.FINEST, "could not load JSApplicationResources properties file for locale {0} as well as en_US", userLocale);
            return msgKey;
        }
        try {
            val = bundle.getString(msgKey);
        }
        catch (final MissingResourceException e2) {
            I18NUtil.logger.log(Level.FINEST, "key {0} has no entries in JSApplicationResources properties. Locale is {1}", new Object[] { msgKey, userLocale.toString() });
            return msgKey;
        }
        if (val != null && val.contains("{productName}")) {
            String productName = "Central Server";
            if (AdvSearchCommonUtil.productCode != null && !AdvSearchCommonUtil.productCode.isEmpty()) {
                final String genPropsFile = AdvSearchCommonUtil.SERVER_HOME + File.separator + "conf" + File.separator + "general_properties.conf";
                final Properties generalProperties = AdvSearchCommonUtil.getInstance().readGeneralProperties(genPropsFile, AdvSearchCommonUtil.productCode);
                productName = generalProperties.getProperty("displayname");
            }
            val = val.replaceAll("\\{productName}", productName);
        }
        return MessageFormat.format(val, args);
    }
    
    static {
        I18NUtil.logger = Logger.getLogger(I18NUtil.class.getName());
    }
}
