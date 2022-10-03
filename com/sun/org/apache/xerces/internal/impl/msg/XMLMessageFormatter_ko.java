package com.sun.org.apache.xerces.internal.impl.msg;

import java.util.MissingResourceException;
import java.text.MessageFormat;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.util.ResourceBundle;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;

public class XMLMessageFormatter_ko implements MessageFormatter
{
    public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
    public static final String XMLNS_DOMAIN = "http://www.w3.org/TR/1999/REC-xml-names-19990114";
    private Locale fLocale;
    private ResourceBundle fResourceBundle;
    
    public XMLMessageFormatter_ko() {
        this.fLocale = null;
        this.fResourceBundle = null;
    }
    
    @Override
    public String formatMessage(final Locale locale, final String key, final Object[] arguments) throws MissingResourceException {
        if (this.fResourceBundle == null || locale != this.fLocale) {
            if (locale != null) {
                this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages", locale);
                this.fLocale = locale;
            }
            if (this.fResourceBundle == null) {
                this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages");
            }
        }
        String msg;
        try {
            msg = this.fResourceBundle.getString(key);
            if (arguments != null) {
                try {
                    msg = MessageFormat.format(msg, arguments);
                }
                catch (final Exception e) {
                    msg = this.fResourceBundle.getString("FormatFailed");
                    msg = msg + " " + this.fResourceBundle.getString(key);
                }
            }
        }
        catch (final MissingResourceException e2) {
            msg = this.fResourceBundle.getString("BadMessageKey");
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
}
