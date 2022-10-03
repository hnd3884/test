package com.adventnet.tools.update.installer;

import java.io.InputStream;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.net.URL;
import java.util.MissingResourceException;
import java.lang.reflect.Constructor;
import java.applet.Applet;
import java.util.ResourceBundle;

public class BuilderResourceBundle
{
    ResourceBundle localBundle;
    ResourceBundle parentBundle;
    String key;
    
    public BuilderResourceBundle(final ResourceBundle bundle) {
        this.localBundle = null;
        this.parentBundle = null;
        if (bundle != null) {
            this.localBundle = bundle;
        }
    }
    
    public BuilderResourceBundle(final String directory, String localfileArg, final String localeArg, final String parentClassArg, final Applet appletArg) {
        this.localBundle = null;
        this.parentBundle = null;
        this.key = directory + ":" + localfileArg + ":" + localeArg + ":" + parentClassArg;
        String resourceFile = "";
        if (directory != null) {
            localfileArg = directory + "/" + localfileArg;
        }
        if (localeArg != null) {
            resourceFile = localfileArg + "_" + localeArg + ".properties";
            this.localBundle = this.getPropertyResourceBundle(resourceFile, appletArg);
        }
        if (this.localBundle == null) {
            resourceFile = localfileArg + ".properties";
            this.localBundle = this.getPropertyResourceBundle(resourceFile, appletArg);
        }
        if (parentClassArg != null) {
            Object bundleObj = null;
            try {
                final Class parentBundleClass = Class.forName(parentClassArg);
                if (appletArg != null) {
                    try {
                        final Constructor appletConstructor = parentBundleClass.getConstructor(Applet.class);
                        bundleObj = appletConstructor.newInstance(appletArg);
                    }
                    catch (final NoSuchMethodException me) {
                        bundleObj = parentBundleClass.newInstance();
                    }
                }
                if (bundleObj != null) {
                    this.parentBundle = (ResourceBundle)bundleObj;
                }
            }
            catch (final Throwable th) {
                ConsoleOut.println("Unable to create an instance of parent class : " + parentClassArg + ". " + th.getClass().getName() + " occured.Message :" + th.getMessage());
            }
        }
    }
    
    public String getIdKey() {
        return this.key;
    }
    
    public String getString(final String key) {
        String finalValue = null;
        if (this.localBundle != null) {
            try {
                finalValue = this.localBundle.getString(key);
            }
            catch (final MissingResourceException ex) {}
        }
        if ((finalValue == null || finalValue.equals("")) && this.parentBundle != null) {
            try {
                finalValue = this.parentBundle.getString(key);
            }
            catch (final MissingResourceException mre) {
                ConsoleOut.println("MissingResourceException in parent for key :" + key + ".Reason : " + mre.getMessage());
            }
        }
        if (finalValue == null || finalValue.equals("")) {
            finalValue = key;
        }
        return finalValue;
    }
    
    private PropertyResourceBundle getPropertyResourceBundle(final URL url) {
        PropertyResourceBundle propResourceBundle = null;
        InputStream is = null;
        try {
            if (url != null) {
                is = url.openStream();
                propResourceBundle = new PropertyResourceBundle(is);
                return propResourceBundle;
            }
        }
        catch (final Exception ex) {}
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (final IOException ex2) {}
            }
        }
        return propResourceBundle;
    }
    
    private PropertyResourceBundle getPropertyResourceBundle(final String localfileArg, final Applet appletArg) {
        PropertyResourceBundle propResourceBundle = null;
        final String file = localfileArg;
        URL url = null;
        url = Utility.getURLFromGetResource("/" + file);
        propResourceBundle = this.getPropertyResourceBundle(url);
        if (propResourceBundle == null && appletArg == null) {
            url = Utility.getURLFromAbsolutePath(file);
            propResourceBundle = this.getPropertyResourceBundle(url);
        }
        if (appletArg != null) {
            if (propResourceBundle == null) {
                url = Utility.getURLFromDocumentBase(localfileArg, appletArg);
                propResourceBundle = this.getPropertyResourceBundle(url);
            }
            if (propResourceBundle == null) {
                url = Utility.getURLFromCodeBase(localfileArg, appletArg);
                propResourceBundle = this.getPropertyResourceBundle(url);
            }
            if (propResourceBundle == null) {
                url = Utility.getURLFromWebServerBase(localfileArg, appletArg);
                propResourceBundle = this.getPropertyResourceBundle(url);
            }
        }
        return propResourceBundle;
    }
}
