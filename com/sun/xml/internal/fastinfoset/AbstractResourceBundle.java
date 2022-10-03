package com.sun.xml.internal.fastinfoset;

import java.util.Enumeration;
import java.util.Locale;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public abstract class AbstractResourceBundle extends ResourceBundle
{
    public static final String LOCALE = "com.sun.xml.internal.fastinfoset.locale";
    
    public String getString(final String key, final Object[] args) {
        final String pattern = this.getBundle().getString(key);
        return MessageFormat.format(pattern, args);
    }
    
    public static Locale parseLocale(final String localeString) {
        Locale locale = null;
        if (localeString == null) {
            locale = Locale.getDefault();
        }
        else {
            try {
                final String[] args = localeString.split("_");
                if (args.length == 1) {
                    locale = new Locale(args[0]);
                }
                else if (args.length == 2) {
                    locale = new Locale(args[0], args[1]);
                }
                else if (args.length == 3) {
                    locale = new Locale(args[0], args[1], args[2]);
                }
            }
            catch (final Throwable t) {
                locale = Locale.getDefault();
            }
        }
        return locale;
    }
    
    public abstract ResourceBundle getBundle();
    
    @Override
    protected Object handleGetObject(final String key) {
        return this.getBundle().getObject(key);
    }
    
    @Override
    public final Enumeration getKeys() {
        return this.getBundle().getKeys();
    }
}
