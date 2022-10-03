package com.sun.org.apache.xerces.internal.xpointer;

import java.util.MissingResourceException;
import java.text.MessageFormat;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.util.ResourceBundle;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;

class XPointerMessageFormatter implements MessageFormatter
{
    public static final String XPOINTER_DOMAIN = "http://www.w3.org/TR/XPTR";
    private Locale fLocale;
    private ResourceBundle fResourceBundle;
    
    XPointerMessageFormatter() {
        this.fLocale = null;
        this.fResourceBundle = null;
    }
    
    @Override
    public String formatMessage(final Locale locale, final String key, final Object[] arguments) throws MissingResourceException {
        if (this.fResourceBundle == null || locale != this.fLocale) {
            if (locale != null) {
                this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages", locale);
                this.fLocale = locale;
            }
            if (this.fResourceBundle == null) {
                this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages");
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
            throw new MissingResourceException(msg, "com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages", key);
        }
        return msg;
    }
}
