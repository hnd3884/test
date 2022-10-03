package javax.servlet.jsp.jstl.fmt;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.text.MessageFormat;
import javax.servlet.jsp.PageContext;

public class LocaleSupport
{
    public static String getLocalizedMessage(final PageContext pageContext, final String key) {
        return getLocalizedMessage(pageContext, key, null, null);
    }
    
    public static String getLocalizedMessage(final PageContext pageContext, final String key, final String basename) {
        return getLocalizedMessage(pageContext, key, null, basename);
    }
    
    public static String getLocalizedMessage(final PageContext pageContext, final String key, final Object[] args) {
        return getLocalizedMessage(pageContext, key, args, null);
    }
    
    public static String getLocalizedMessage(final PageContext pageContext, final String key, final Object[] args, final String basename) {
        LocalizationContext locCtxt = null;
        String message = "???" + key + "???";
        if (basename != null) {
            locCtxt = JakartaInline.getLocalizationContext(pageContext, basename);
        }
        else {
            locCtxt = JakartaInline.getLocalizationContext(pageContext);
        }
        if (locCtxt != null) {
            final ResourceBundle bundle = locCtxt.getResourceBundle();
            if (bundle != null) {
                try {
                    message = bundle.getString(key);
                    if (args != null) {
                        final MessageFormat formatter = new MessageFormat("");
                        if (locCtxt.getLocale() != null) {
                            formatter.setLocale(locCtxt.getLocale());
                        }
                        formatter.applyPattern(message);
                        message = formatter.format(args);
                    }
                }
                catch (final MissingResourceException ex) {}
            }
        }
        return message;
    }
}
