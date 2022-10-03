package com.sun.org.apache.xml.internal.security.utils;

import java.util.Locale;
import java.text.MessageFormat;
import com.sun.org.apache.xml.internal.security.Init;
import java.util.ResourceBundle;

public class I18n
{
    public static final String NOT_INITIALIZED_MSG = "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
    private static ResourceBundle resourceBundle;
    private static boolean alreadyInitialized;
    
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
                return "No message with ID \"" + s + "\" found in resource bundle \"" + "com.sun.org.apache.xml.internal.security/resource/xmlsecurity" + "\"";
            }
            return "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
        }
    }
    
    public static String getExceptionMessage(final String s, final Exception ex) {
        try {
            return MessageFormat.format(I18n.resourceBundle.getString(s), ex.getMessage());
        }
        catch (final Throwable t) {
            if (Init.isInitialized()) {
                return "No message with ID \"" + s + "\" found in resource bundle \"" + "com.sun.org.apache.xml.internal.security/resource/xmlsecurity" + "\". Original Exception was a " + ex.getClass().getName() + " and message " + ex.getMessage();
            }
            return "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
        }
    }
    
    public static String getExceptionMessage(final String s, final Object[] array) {
        try {
            return MessageFormat.format(I18n.resourceBundle.getString(s), array);
        }
        catch (final Throwable t) {
            if (Init.isInitialized()) {
                return "No message with ID \"" + s + "\" found in resource bundle \"" + "com.sun.org.apache.xml.internal.security/resource/xmlsecurity" + "\"";
            }
            return "You must initialize the xml-security library correctly before you use it. Call the static method \"com.sun.org.apache.xml.internal.security.Init.init();\" to do that before you use any functionality from that library.";
        }
    }
    
    public static synchronized void init(final String s, final String s2) {
        if (I18n.alreadyInitialized) {
            return;
        }
        I18n.resourceBundle = ResourceBundle.getBundle("com.sun.org.apache.xml.internal.security/resource/xmlsecurity", new Locale(s, s2));
        I18n.alreadyInitialized = true;
    }
    
    public static synchronized void init(final ResourceBundle resourceBundle) {
        if (I18n.alreadyInitialized) {
            return;
        }
        I18n.resourceBundle = resourceBundle;
        I18n.alreadyInitialized = true;
    }
    
    static {
        I18n.alreadyInitialized = false;
    }
}
