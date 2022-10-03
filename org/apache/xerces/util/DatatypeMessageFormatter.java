package org.apache.xerces.util;

import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Locale;

public class DatatypeMessageFormatter
{
    private static final String BASE_NAME = "org.apache.xerces.impl.msg.DatatypeMessages";
    
    public static String formatMessage(Locale default1, final String s, final Object[] array) throws MissingResourceException {
        if (default1 == null) {
            default1 = Locale.getDefault();
        }
        final ResourceBundle bundle = ResourceBundle.getBundle("org.apache.xerces.impl.msg.DatatypeMessages", default1);
        String s2;
        try {
            s2 = bundle.getString(s);
            if (array != null) {
                try {
                    s2 = MessageFormat.format(s2, array);
                }
                catch (final Exception ex) {
                    s2 = bundle.getString("FormatFailed") + " " + bundle.getString(s);
                }
            }
        }
        catch (final MissingResourceException ex2) {
            throw new MissingResourceException(s, bundle.getString("BadMessageKey"), s);
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
