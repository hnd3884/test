package org.apache.taglibs.standard.resources;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Resources
{
    private static final ResourceBundle rb;
    
    public static String getMessage(final String name) throws MissingResourceException {
        return Resources.rb.getString(name);
    }
    
    public static String getMessage(final String name, final Object... a) throws MissingResourceException {
        final String res = Resources.rb.getString(name);
        return MessageFormat.format(res, a);
    }
    
    static {
        rb = ResourceBundle.getBundle(Resources.class.getName());
    }
}
