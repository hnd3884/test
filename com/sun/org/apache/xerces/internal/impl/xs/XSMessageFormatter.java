package com.sun.org.apache.xerces.internal.impl.xs;

import java.util.MissingResourceException;
import java.text.MessageFormat;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.util.ResourceBundle;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;

public class XSMessageFormatter implements MessageFormatter
{
    public static final String SCHEMA_DOMAIN = "http://www.w3.org/TR/xml-schema-1";
    private Locale fLocale;
    private ResourceBundle fResourceBundle;
    
    public XSMessageFormatter() {
        this.fLocale = null;
        this.fResourceBundle = null;
    }
    
    @Override
    public String formatMessage(final Locale locale, final String key, final Object[] arguments) throws MissingResourceException {
        if (this.fResourceBundle == null || locale != this.fLocale) {
            if (locale != null) {
                this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages", locale);
                this.fLocale = locale;
            }
            if (this.fResourceBundle == null) {
                this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages");
            }
        }
        String msg = this.fResourceBundle.getString(key);
        if (arguments != null) {
            try {
                msg = MessageFormat.format(msg, arguments);
            }
            catch (final Exception e) {
                msg = this.fResourceBundle.getString("FormatFailed");
                msg = msg + " " + this.fResourceBundle.getString(key);
            }
        }
        if (msg == null) {
            msg = this.fResourceBundle.getString("BadMessageKey");
            throw new MissingResourceException(msg, "com.sun.org.apache.xerces.internal.impl.msg.SchemaMessages", key);
        }
        return msg;
    }
}
