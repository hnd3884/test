package org.apache.jasper.compiler;

import org.apache.jasper.runtime.ExceptionUtils;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Localizer
{
    private static ResourceBundle bundle;
    
    public static String getMessage(final String errCode) {
        String errMsg = errCode;
        try {
            if (Localizer.bundle != null) {
                errMsg = Localizer.bundle.getString(errCode);
            }
        }
        catch (final MissingResourceException ex) {}
        return errMsg;
    }
    
    public static String getMessage(final String errCode, final Object... args) {
        String errMsg = getMessage(errCode);
        if (args != null && args.length > 0) {
            final MessageFormat formatter = new MessageFormat(errMsg);
            errMsg = formatter.format(args);
        }
        return errMsg;
    }
    
    static {
        try {
            Localizer.bundle = ResourceBundle.getBundle("org.apache.jasper.resources.LocalStrings");
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
    }
}
