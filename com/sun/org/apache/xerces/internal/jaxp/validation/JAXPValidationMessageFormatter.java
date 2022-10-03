package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.text.MessageFormat;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.util.Locale;

final class JAXPValidationMessageFormatter
{
    public static String formatMessage(final Locale locale, final String key, final Object[] arguments) throws MissingResourceException {
        ResourceBundle resourceBundle = null;
        if (locale != null) {
            resourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.JAXPValidationMessages", locale);
        }
        else {
            resourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.JAXPValidationMessages");
        }
        String msg;
        try {
            msg = resourceBundle.getString(key);
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
}
