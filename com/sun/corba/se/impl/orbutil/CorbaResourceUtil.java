package com.sun.corba.se.impl.orbutil;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class CorbaResourceUtil
{
    private static boolean resourcesInitialized;
    private static ResourceBundle resources;
    
    public static String getString(final String s) {
        if (!CorbaResourceUtil.resourcesInitialized) {
            initResources();
        }
        try {
            return CorbaResourceUtil.resources.getString(s);
        }
        catch (final MissingResourceException ex) {
            return null;
        }
    }
    
    public static String getText(final String s) {
        String s2 = getString(s);
        if (s2 == null) {
            s2 = "no text found: \"" + s + "\"";
        }
        return s2;
    }
    
    public static String getText(final String s, final int n) {
        return getText(s, Integer.toString(n), null, null);
    }
    
    public static String getText(final String s, final String s2) {
        return getText(s, s2, null, null);
    }
    
    public static String getText(final String s, final String s2, final String s3) {
        return getText(s, s2, s3, null);
    }
    
    public static String getText(final String s, final String s2, final String s3, final String s4) {
        String s5 = getString(s);
        if (s5 == null) {
            s5 = "no text found: key = \"" + s + "\", arguments = \"{0}\", \"{1}\", \"{2}\"";
        }
        return MessageFormat.format(s5, (s2 != null) ? s2.toString() : "null", (s3 != null) ? s3.toString() : "null", (s4 != null) ? s4.toString() : "null");
    }
    
    private static void initResources() {
        try {
            CorbaResourceUtil.resources = ResourceBundle.getBundle("com.sun.corba.se.impl.orbutil.resources.sunorb");
            CorbaResourceUtil.resourcesInitialized = true;
        }
        catch (final MissingResourceException ex) {
            throw new Error("fatal: missing resource bundle: " + ex.getClassName());
        }
    }
    
    static {
        CorbaResourceUtil.resourcesInitialized = false;
    }
}
