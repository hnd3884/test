package com.sun.imageio.plugins.common;

import java.util.PropertyResourceBundle;

public class I18NImpl
{
    protected static final String getString(final String s, final String s2, final String s3) {
        PropertyResourceBundle propertyResourceBundle;
        try {
            propertyResourceBundle = new PropertyResourceBundle(Class.forName(s).getResourceAsStream(s2));
        }
        catch (final Throwable t) {
            throw new RuntimeException(t);
        }
        return (String)propertyResourceBundle.handleGetObject(s3);
    }
}
