package com.sun.xml.internal.fastinfoset;

import java.util.ResourceBundle;
import java.util.Locale;

public class CommonResourceBundle extends AbstractResourceBundle
{
    public static final String BASE_NAME = "com.sun.xml.internal.fastinfoset.resources.ResourceBundle";
    private static volatile CommonResourceBundle instance;
    private static Locale locale;
    private ResourceBundle bundle;
    
    protected CommonResourceBundle() {
        this.bundle = null;
        this.bundle = ResourceBundle.getBundle("com.sun.xml.internal.fastinfoset.resources.ResourceBundle");
    }
    
    protected CommonResourceBundle(final Locale locale) {
        this.bundle = null;
        this.bundle = ResourceBundle.getBundle("com.sun.xml.internal.fastinfoset.resources.ResourceBundle", locale);
    }
    
    public static CommonResourceBundle getInstance() {
        if (CommonResourceBundle.instance == null) {
            synchronized (CommonResourceBundle.class) {
                CommonResourceBundle.instance = new CommonResourceBundle();
                CommonResourceBundle.locale = AbstractResourceBundle.parseLocale(null);
            }
        }
        return CommonResourceBundle.instance;
    }
    
    public static CommonResourceBundle getInstance(final Locale locale) {
        if (CommonResourceBundle.instance == null) {
            synchronized (CommonResourceBundle.class) {
                CommonResourceBundle.instance = new CommonResourceBundle(locale);
            }
        }
        else {
            synchronized (CommonResourceBundle.class) {
                if (CommonResourceBundle.locale != locale) {
                    CommonResourceBundle.instance = new CommonResourceBundle(locale);
                }
            }
        }
        return CommonResourceBundle.instance;
    }
    
    @Override
    public ResourceBundle getBundle() {
        return this.bundle;
    }
    
    public ResourceBundle getBundle(final Locale locale) {
        return ResourceBundle.getBundle("com.sun.xml.internal.fastinfoset.resources.ResourceBundle", locale);
    }
    
    static {
        CommonResourceBundle.instance = null;
        CommonResourceBundle.locale = null;
    }
}
