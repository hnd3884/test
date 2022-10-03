package com.adventnet.i18n;

import java.util.Iterator;
import java.util.Enumeration;
import java.util.ArrayList;
import java.text.MessageFormat;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.MissingResourceException;
import java.util.logging.Level;
import com.adventnet.authentication.util.AuthUtil;
import java.util.logging.Logger;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Locale;

public class I18N
{
    private static boolean fromPropFile;
    private static Locale globalLocale;
    private static HashMap<String, ResourceBundle> rbmap;
    private static HashMap<String, String> scriptMap;
    private static Logger logger;
    private static ThreadLocal i18nVar;
    private static ThreadLocal reqLocale;
    private static String baseName;
    private static String js_baseName;
    
    public static void setI18N(final int value) {
        I18N.i18nVar.set(new Integer(value));
    }
    
    public static Integer getI18N() {
        final Integer val = I18N.i18nVar.get();
        if (val == null) {
            return new Integer(-1);
        }
        return val;
    }
    
    public static String getMsg(final String msgKey, final Object... args) {
        if (msgKey == null) {
            return null;
        }
        if (I18N.fromPropFile) {
            return getMsgFromPropFile(msgKey, args);
        }
        return msgKey;
    }
    
    public static Locale getLocale() {
        Locale locale = I18N.globalLocale;
        if (locale == null) {
            try {
                locale = AuthUtil.getUserCredential().getUserLocale();
            }
            catch (final Exception e) {
                locale = getRequestLocale();
            }
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }
    
    public static ResourceBundle getResourceBundleFromCache(final String basename, final Locale locale) throws Exception {
        final String key = basename + locale.toString();
        if (I18N.rbmap.containsKey(key)) {
            return I18N.rbmap.get(key);
        }
        final ResourceBundle bundle = ResourceBundle.getBundle(basename, locale, new MultiplePropertiesResourceBundleControl());
        I18N.rbmap.put(key, bundle);
        return bundle;
    }
    
    public static void clearResourceBundleCache() throws Exception {
        ResourceBundle.clearCache();
        I18N.rbmap = new HashMap<String, ResourceBundle>();
        final ResourceBundle bundle = ResourceBundle.getBundle(I18N.baseName, I18N.globalLocale, new MultiplePropertiesResourceBundleControl());
        String key = I18N.baseName + "_" + I18N.globalLocale.toString();
        I18N.rbmap.put(key, bundle);
        ResourceBundle.getBundle(I18N.js_baseName, I18N.globalLocale, new MultiplePropertiesResourceBundleControl());
        key = I18N.js_baseName + "_" + I18N.globalLocale.toString();
        I18N.rbmap.put(key, bundle);
    }
    
    public static ResourceBundle getBundle(final String basename, final Locale locale) throws Exception {
        ResourceBundle bundle = null;
        try {
            bundle = getResourceBundleFromCache(basename, locale);
        }
        catch (final Exception e) {
            I18N.logger.log(Level.FINEST, "Could not load resource {0} with following locale {1}", new Object[] { basename, locale.toString() });
            I18N.logger.log(Level.FINEST, "Trying to load  {0} with following locale en_US", basename);
            bundle = ResourceBundle.getBundle(basename, new Locale("en", "US"), new MultiplePropertiesResourceBundleControl());
            I18N.rbmap.put(basename + "_" + locale.toString(), bundle);
        }
        return bundle;
    }
    
    public static String getMsgFromPropFile(final String msgKey, final Object... args) {
        final Locale locale = getLocale();
        return getMsgFromPropFile(msgKey, locale, args);
    }
    
    public static String getMsgFromPropFile(final String msgKey, final Locale locale, final Object... args) {
        if (locale == null) {
            return msgKey;
        }
        ResourceBundle bundle = null;
        try {
            bundle = getBundle(I18N.baseName, locale);
        }
        catch (final Exception e) {
            I18N.logger.log(Level.FINEST, "could not load properties file for locale {0} as well as en_US", locale);
            I18N.rbmap.put(I18N.baseName + locale.toString(), null);
            return msgKey;
        }
        String val = msgKey;
        if (bundle == null) {
            I18N.logger.log(Level.FINEST, "could not load properties file for locale {0} as well as en_US", locale);
            return msgKey;
        }
        try {
            val = bundle.getString(msgKey);
        }
        catch (final MissingResourceException e2) {
            I18N.logger.log(Level.FINEST, "key {0} has no entries in properties. Locale is {1}", new Object[] { msgKey, locale.toString() });
            return msgKey;
        }
        if (val != null && val.contains("{productName}")) {
            final String productName = ProductUrlLoader.getInstance().getGeneralProperites().getProperty("displayname");
            val = val.replaceAll("\\{productName}", productName);
        }
        return MessageFormat.format(val, args);
    }
    
    public static String generateI18NScript() {
        if (I18N.scriptMap.containsKey(getLocale().toString())) {
            return I18N.scriptMap.get(getLocale().toString());
        }
        final String newline = System.getProperty("line.separator");
        final StringBuffer output = new StringBuffer();
        ResourceBundle bundle = null;
        try {
            bundle = getBundle(I18N.js_baseName, getLocale());
        }
        catch (final Exception e) {
            I18N.logger.log(Level.FINEST, " property file missing for locale {0} as well as en_US", getLocale());
            I18N.rbmap.put(I18N.baseName + getLocale().toString(), null);
        }
        if (bundle == null) {
            return "";
        }
        output.append("<script>var i18nJSON=({");
        boolean first = true;
        final ArrayList<String> keylist = new ArrayList<String>();
        final Enumeration e2 = bundle.getKeys();
        while (e2.hasMoreElements()) {
            keylist.add(e2.nextElement().toString());
        }
        for (final String key : keylist) {
            if (!first) {
                output.append(",");
            }
            first = false;
            String value = bundle.getString(key);
            value = getEscapedString(value);
            output.append("'").append(getEscapedString(key)).append("':'").append(value).append("'").append(newline);
        }
        output.append("});</script>");
        output.append(newline);
        I18N.scriptMap.put(getLocale().toString(), output.toString());
        return output.toString();
    }
    
    public static String getEscapedString(final Object textArg) {
        if (textArg == null) {
            return null;
        }
        final String text = textArg.toString();
        final StringBuffer charBuffer = new StringBuffer();
        for (int length = 0; length < text.length(); ++length) {
            final char ch = text.charAt(length);
            if (ch == '\r' || ch == '\n') {
                charBuffer.append('\\').append('n');
            }
            else if (ch == '\"' || ch == '\'' || ch == '/' || ch == '\\') {
                charBuffer.append('\\');
                charBuffer.append(ch);
            }
            else {
                charBuffer.append(ch);
            }
        }
        return charBuffer.toString();
    }
    
    public static void setRequestLocale(final Locale locale) {
        I18N.reqLocale.set(locale);
    }
    
    public static Locale getRequestLocale() {
        final Locale locale = I18N.reqLocale.get();
        return (locale == null) ? Locale.getDefault() : locale;
    }
    
    public static void resetRequestLocale() {
        I18N.reqLocale.remove();
    }
    
    static {
        I18N.fromPropFile = true;
        I18N.globalLocale = null;
        I18N.rbmap = new HashMap<String, ResourceBundle>();
        I18N.scriptMap = new HashMap<String, String>();
        I18N.logger = Logger.getLogger(I18N.class.getName());
        I18N.i18nVar = new ThreadLocal();
        I18N.reqLocale = new ThreadLocal();
        I18N.baseName = "ApplicationResources";
        I18N.js_baseName = "JSApplicationResources";
    }
}
