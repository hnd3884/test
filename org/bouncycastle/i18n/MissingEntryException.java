package org.bouncycastle.i18n;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;

public class MissingEntryException extends RuntimeException
{
    protected final String resource;
    protected final String key;
    protected final ClassLoader loader;
    protected final Locale locale;
    private String debugMsg;
    
    public MissingEntryException(final String s, final String resource, final String key, final Locale locale, final ClassLoader loader) {
        super(s);
        this.resource = resource;
        this.key = key;
        this.locale = locale;
        this.loader = loader;
    }
    
    public MissingEntryException(final String s, final Throwable t, final String resource, final String key, final Locale locale, final ClassLoader loader) {
        super(s, t);
        this.resource = resource;
        this.key = key;
        this.locale = locale;
        this.loader = loader;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public String getResource() {
        return this.resource;
    }
    
    public ClassLoader getClassLoader() {
        return this.loader;
    }
    
    public Locale getLocale() {
        return this.locale;
    }
    
    public String getDebugMsg() {
        if (this.debugMsg == null) {
            this.debugMsg = "Can not find entry " + this.key + " in resource file " + this.resource + " for the locale " + this.locale + ".";
            if (this.loader instanceof URLClassLoader) {
                final URL[] urLs = ((URLClassLoader)this.loader).getURLs();
                this.debugMsg += " The following entries in the classpath were searched: ";
                for (int i = 0; i != urLs.length; ++i) {
                    this.debugMsg = this.debugMsg + urLs[i] + " ";
                }
            }
        }
        return this.debugMsg;
    }
}
