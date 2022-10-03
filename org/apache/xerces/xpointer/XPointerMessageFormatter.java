package org.apache.xerces.xpointer;

import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Locale;
import org.apache.xerces.util.MessageFormatter;

final class XPointerMessageFormatter implements MessageFormatter
{
    public static final String XPOINTER_DOMAIN = "http://www.w3.org/TR/XPTR";
    private Locale fLocale;
    private ResourceBundle fResourceBundle;
    
    XPointerMessageFormatter() {
        this.fLocale = null;
        this.fResourceBundle = null;
    }
    
    public String formatMessage(Locale default1, final String s, final Object[] array) throws MissingResourceException {
        if (default1 == null) {
            default1 = Locale.getDefault();
        }
        if (default1 != this.fLocale) {
            this.fResourceBundle = ResourceBundle.getBundle("org.apache.xerces.impl.msg.XPointerMessages", default1);
            this.fLocale = default1;
        }
        String s2 = this.fResourceBundle.getString(s);
        if (array != null) {
            try {
                s2 = MessageFormat.format(s2, array);
            }
            catch (final Exception ex) {
                s2 = this.fResourceBundle.getString("FormatFailed") + " " + this.fResourceBundle.getString(s);
            }
        }
        if (s2 == null) {
            throw new MissingResourceException(this.fResourceBundle.getString("BadMessageKey"), "org.apache.xerces.impl.msg.XPointerMessages", s);
        }
        return s2;
    }
}
