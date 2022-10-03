package org.apache.xerces.impl.xs;

import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Locale;
import org.apache.xerces.util.MessageFormatter;

public class XSMessageFormatter implements MessageFormatter
{
    public static final String SCHEMA_DOMAIN = "http://www.w3.org/TR/xml-schema-1";
    private Locale fLocale;
    private ResourceBundle fResourceBundle;
    
    public XSMessageFormatter() {
        this.fLocale = null;
        this.fResourceBundle = null;
    }
    
    public String formatMessage(Locale default1, final String s, final Object[] array) throws MissingResourceException {
        if (default1 == null) {
            default1 = Locale.getDefault();
        }
        if (default1 != this.fLocale) {
            this.fResourceBundle = ResourceBundle.getBundle("org.apache.xerces.impl.msg.XMLSchemaMessages", default1);
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
            throw new MissingResourceException(this.fResourceBundle.getString("BadMessageKey"), "org.apache.xerces.impl.msg.SchemaMessages", s);
        }
        return s2;
    }
}
