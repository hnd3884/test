package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Locale;
import java.util.ResourceBundle;

public class DOMMessageFormatter
{
    public static final String DOM_DOMAIN = "http://www.w3.org/dom/DOMTR";
    public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
    public static final String SERIALIZER_DOMAIN = "http://apache.org/xml/serializer";
    private static ResourceBundle domResourceBundle;
    private static ResourceBundle xmlResourceBundle;
    private static ResourceBundle serResourceBundle;
    private static Locale locale;
    
    DOMMessageFormatter() {
        DOMMessageFormatter.locale = Locale.getDefault();
    }
    
    public static String formatMessage(final String domain, final String key, final Object[] arguments) throws MissingResourceException {
        ResourceBundle resourceBundle = getResourceBundle(domain);
        if (resourceBundle == null) {
            init();
            resourceBundle = getResourceBundle(domain);
            if (resourceBundle == null) {
                throw new MissingResourceException("Unknown domain" + domain, null, key);
            }
        }
        String msg;
        try {
            msg = key + ": " + resourceBundle.getString(key);
            if (arguments != null) {
                try {
                    msg = MessageFormat.format(msg, arguments);
                }
                catch (final Exception e) {
                    msg = resourceBundle.getString("FormatFailed");
                    msg = msg + " " + resourceBundle.getString(key);
                }
            }
        }
        catch (final MissingResourceException e2) {
            msg = resourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(key, msg, key);
        }
        if (msg == null) {
            msg = key;
            if (arguments.length > 0) {
                final StringBuffer str = new StringBuffer(msg);
                str.append('?');
                for (int i = 0; i < arguments.length; ++i) {
                    if (i > 0) {
                        str.append('&');
                    }
                    str.append(String.valueOf(arguments[i]));
                }
            }
        }
        return msg;
    }
    
    static ResourceBundle getResourceBundle(final String domain) {
        if (domain == "http://www.w3.org/dom/DOMTR" || domain.equals("http://www.w3.org/dom/DOMTR")) {
            return DOMMessageFormatter.domResourceBundle;
        }
        if (domain == "http://www.w3.org/TR/1998/REC-xml-19980210" || domain.equals("http://www.w3.org/TR/1998/REC-xml-19980210")) {
            return DOMMessageFormatter.xmlResourceBundle;
        }
        if (domain == "http://apache.org/xml/serializer" || domain.equals("http://apache.org/xml/serializer")) {
            return DOMMessageFormatter.serResourceBundle;
        }
        return null;
    }
    
    public static void init() {
        if (DOMMessageFormatter.locale != null) {
            DOMMessageFormatter.domResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.DOMMessages", DOMMessageFormatter.locale);
            DOMMessageFormatter.serResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSerializerMessages", DOMMessageFormatter.locale);
            DOMMessageFormatter.xmlResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages", DOMMessageFormatter.locale);
        }
        else {
            DOMMessageFormatter.domResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.DOMMessages");
            DOMMessageFormatter.serResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSerializerMessages");
            DOMMessageFormatter.xmlResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages");
        }
    }
    
    public static void setLocale(final Locale dlocale) {
        DOMMessageFormatter.locale = dlocale;
    }
    
    static {
        DOMMessageFormatter.domResourceBundle = null;
        DOMMessageFormatter.xmlResourceBundle = null;
        DOMMessageFormatter.serResourceBundle = null;
        DOMMessageFormatter.locale = null;
    }
}
