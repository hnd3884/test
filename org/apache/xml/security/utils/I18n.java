package org.apache.xml.security.utils;

import java.util.Locale;
import java.text.MessageFormat;
import org.apache.xml.security.Init;
import java.util.ResourceBundle;

public class I18n
{
    public static final String NOT_INITIALIZED_MSG = "You must initialize the xml-security library correctly before you use it. Call the static method \"org.apache.xml.security.Init.init();\" to do that before you use any functionality from that library.";
    static String defaultLanguageCode;
    static String defaultCountryCode;
    static ResourceBundle resourceBundle;
    static boolean alreadyInitialized;
    static String _languageCode;
    static String _countryCode;
    
    private I18n() {
    }
    
    public static String translate(final String s, final Object[] array) {
        return getExceptionMessage(s, array);
    }
    
    public static String translate(final String s) {
        return getExceptionMessage(s);
    }
    
    public static String getExceptionMessage(final String s) {
        try {
            return I18n.resourceBundle.getString(s);
        }
        catch (final Throwable t) {
            if (Init.isInitialized()) {
                return "No message with ID \"" + s + "\" found in resource bundle \"" + "org/apache/xml/security/resource/xmlsecurity" + "\"";
            }
            return "You must initialize the xml-security library correctly before you use it. Call the static method \"org.apache.xml.security.Init.init();\" to do that before you use any functionality from that library.";
        }
    }
    
    public static String getExceptionMessage(final String s, final Exception ex) {
        try {
            return MessageFormat.format(I18n.resourceBundle.getString(s), ex.getMessage());
        }
        catch (final Throwable t) {
            if (Init.isInitialized()) {
                return "No message with ID \"" + s + "\" found in resource bundle \"" + "org/apache/xml/security/resource/xmlsecurity" + "\". Original Exception was a " + ex.getClass().getName() + " and message " + ex.getMessage();
            }
            return "You must initialize the xml-security library correctly before you use it. Call the static method \"org.apache.xml.security.Init.init();\" to do that before you use any functionality from that library.";
        }
    }
    
    public static String getExceptionMessage(final String s, final Object[] array) {
        try {
            return MessageFormat.format(I18n.resourceBundle.getString(s), array);
        }
        catch (final Throwable t) {
            if (Init.isInitialized()) {
                return "No message with ID \"" + s + "\" found in resource bundle \"" + "org/apache/xml/security/resource/xmlsecurity" + "\"";
            }
            return "You must initialize the xml-security library correctly before you use it. Call the static method \"org.apache.xml.security.Init.init();\" to do that before you use any functionality from that library.";
        }
    }
    
    public static void init(final String defaultLanguageCode, final String defaultCountryCode) {
        I18n.defaultLanguageCode = defaultLanguageCode;
        if (I18n.defaultLanguageCode == null) {
            I18n.defaultLanguageCode = Locale.getDefault().getLanguage();
        }
        I18n.defaultCountryCode = defaultCountryCode;
        if (I18n.defaultCountryCode == null) {
            I18n.defaultCountryCode = Locale.getDefault().getCountry();
        }
        initLocale(I18n.defaultLanguageCode, I18n.defaultCountryCode);
    }
    
    public static void initLocale(final String languageCode, final String countryCode) {
        if (I18n.alreadyInitialized && languageCode.equals(I18n._languageCode) && countryCode.equals(I18n._countryCode)) {
            return;
        }
        if (languageCode != null && countryCode != null && languageCode.length() > 0 && countryCode.length() > 0) {
            I18n._languageCode = languageCode;
            I18n._countryCode = countryCode;
        }
        else {
            I18n._countryCode = I18n.defaultCountryCode;
            I18n._languageCode = I18n.defaultLanguageCode;
        }
        I18n.resourceBundle = ResourceBundle.getBundle("org/apache/xml/security/resource/xmlsecurity", new Locale(I18n._languageCode, I18n._countryCode));
    }
    
    static {
        I18n.alreadyInitialized = false;
        I18n._languageCode = null;
        I18n._countryCode = null;
    }
}
