package org.apache.xerces.dom;

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
    
    public static String formatMessage(final String s, final String s2, final Object[] array) throws MissingResourceException {
        ResourceBundle resourceBundle = getResourceBundle(s);
        if (resourceBundle == null) {
            init();
            resourceBundle = getResourceBundle(s);
            if (resourceBundle == null) {
                throw new MissingResourceException("Unknown domain" + s, null, s2);
            }
        }
        String s3;
        try {
            s3 = s2 + ": " + resourceBundle.getString(s2);
            if (array != null) {
                try {
                    s3 = MessageFormat.format(s3, array);
                }
                catch (final Exception ex) {
                    s3 = resourceBundle.getString("FormatFailed") + " " + resourceBundle.getString(s2);
                }
            }
        }
        catch (final MissingResourceException ex2) {
            throw new MissingResourceException(s2, resourceBundle.getString("BadMessageKey"), s2);
        }
        if (s3 == null) {
            s3 = s2;
            if (array.length > 0) {
                final StringBuffer sb = new StringBuffer(s3);
                sb.append('?');
                for (int i = 0; i < array.length; ++i) {
                    if (i > 0) {
                        sb.append('&');
                    }
                    sb.append(String.valueOf(array[i]));
                }
            }
        }
        return s3;
    }
    
    static ResourceBundle getResourceBundle(final String s) {
        if (s == "http://www.w3.org/dom/DOMTR" || s.equals("http://www.w3.org/dom/DOMTR")) {
            return DOMMessageFormatter.domResourceBundle;
        }
        if (s == "http://www.w3.org/TR/1998/REC-xml-19980210" || s.equals("http://www.w3.org/TR/1998/REC-xml-19980210")) {
            return DOMMessageFormatter.xmlResourceBundle;
        }
        if (s == "http://apache.org/xml/serializer" || s.equals("http://apache.org/xml/serializer")) {
            return DOMMessageFormatter.serResourceBundle;
        }
        return null;
    }
    
    public static void init() {
        Locale locale = DOMMessageFormatter.locale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        DOMMessageFormatter.domResourceBundle = ResourceBundle.getBundle("org.apache.xerces.impl.msg.DOMMessages", locale);
        DOMMessageFormatter.serResourceBundle = ResourceBundle.getBundle("org.apache.xerces.impl.msg.XMLSerializerMessages", locale);
        DOMMessageFormatter.xmlResourceBundle = ResourceBundle.getBundle("org.apache.xerces.impl.msg.XMLMessages", locale);
    }
    
    public static void setLocale(final Locale locale) {
        DOMMessageFormatter.locale = locale;
    }
    
    static {
        DOMMessageFormatter.domResourceBundle = null;
        DOMMessageFormatter.xmlResourceBundle = null;
        DOMMessageFormatter.serResourceBundle = null;
        DOMMessageFormatter.locale = null;
    }
}
