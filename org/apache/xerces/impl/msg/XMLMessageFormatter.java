package org.apache.xerces.impl.msg;

import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Locale;
import org.apache.xerces.util.MessageFormatter;

public class XMLMessageFormatter implements MessageFormatter
{
    public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
    public static final String XMLNS_DOMAIN = "http://www.w3.org/TR/1999/REC-xml-names-19990114";
    private Locale fLocale;
    private ResourceBundle fResourceBundle;
    
    public XMLMessageFormatter() {
        this.fLocale = null;
        this.fResourceBundle = null;
    }
    
    public String formatMessage(Locale default1, final String s, final Object[] array) throws MissingResourceException {
        if (default1 == null) {
            default1 = Locale.getDefault();
        }
        if (default1 != this.fLocale) {
            this.fResourceBundle = ResourceBundle.getBundle("org.apache.xerces.impl.msg.XMLMessages", default1);
            this.fLocale = default1;
        }
        String s2;
        try {
            s2 = this.fResourceBundle.getString(s);
            if (array != null) {
                try {
                    s2 = MessageFormat.format(s2, array);
                }
                catch (final Exception ex) {
                    s2 = this.fResourceBundle.getString("FormatFailed") + " " + this.fResourceBundle.getString(s);
                }
            }
        }
        catch (final MissingResourceException ex2) {
            throw new MissingResourceException(s, this.fResourceBundle.getString("BadMessageKey"), s);
        }
        if (s2 == null) {
            s2 = s;
            if (array.length > 0) {
                final StringBuffer sb = new StringBuffer(s2);
                sb.append('?');
                for (int i = 0; i < array.length; ++i) {
                    if (i > 0) {
                        sb.append('&');
                    }
                    sb.append(String.valueOf(array[i]));
                }
            }
        }
        return s2;
    }
}
