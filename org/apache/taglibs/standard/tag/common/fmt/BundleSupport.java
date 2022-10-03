package org.apache.taglibs.standard.tag.common.fmt;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import org.apache.taglibs.standard.tag.common.core.Util;
import javax.servlet.http.HttpServletRequest;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import java.util.Locale;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class BundleSupport extends BodyTagSupport
{
    private static final Locale EMPTY_LOCALE;
    protected String basename;
    protected String prefix;
    private LocalizationContext locCtxt;
    
    public BundleSupport() {
        this.init();
    }
    
    private void init() {
        final String s = null;
        this.prefix = s;
        this.basename = s;
        this.locCtxt = null;
    }
    
    public LocalizationContext getLocalizationContext() {
        return this.locCtxt;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public int doStartTag() throws JspException {
        this.locCtxt = getLocalizationContext(this.pageContext, this.basename);
        return 1;
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    public static LocalizationContext getLocalizationContext(final PageContext pc) {
        LocalizationContext locCtxt = null;
        final Object obj = Config.find(pc, "javax.servlet.jsp.jstl.fmt.localizationContext");
        if (obj == null) {
            return null;
        }
        if (obj instanceof LocalizationContext) {
            locCtxt = (LocalizationContext)obj;
        }
        else {
            locCtxt = getLocalizationContext(pc, (String)obj);
        }
        return locCtxt;
    }
    
    public static LocalizationContext getLocalizationContext(final PageContext pc, final String basename) {
        LocalizationContext locCtxt = null;
        ResourceBundle bundle = null;
        if (basename == null || basename.equals("")) {
            return new LocalizationContext();
        }
        Locale pref = SetLocaleSupport.getLocale(pc, "javax.servlet.jsp.jstl.fmt.locale");
        if (pref != null) {
            bundle = findMatch(basename, pref);
            if (bundle != null) {
                locCtxt = new LocalizationContext(bundle, pref);
            }
        }
        else {
            locCtxt = findMatch(pc, basename);
        }
        if (locCtxt == null) {
            pref = SetLocaleSupport.getLocale(pc, "javax.servlet.jsp.jstl.fmt.fallbackLocale");
            if (pref != null) {
                bundle = findMatch(basename, pref);
                if (bundle != null) {
                    locCtxt = new LocalizationContext(bundle, pref);
                }
            }
        }
        if (locCtxt == null) {
            try {
                final ClassLoader cl = getClassLoaderCheckingPrivilege();
                bundle = ResourceBundle.getBundle(basename, BundleSupport.EMPTY_LOCALE, cl);
                if (bundle != null) {
                    locCtxt = new LocalizationContext(bundle, (Locale)null);
                }
            }
            catch (final MissingResourceException ex) {}
        }
        if (locCtxt != null) {
            if (locCtxt.getLocale() != null) {
                SetLocaleSupport.setResponseLocale(pc, locCtxt.getLocale());
            }
        }
        else {
            locCtxt = new LocalizationContext();
        }
        return locCtxt;
    }
    
    private static LocalizationContext findMatch(final PageContext pageContext, final String basename) {
        LocalizationContext locCtxt = null;
        final Enumeration enum_ = Util.getRequestLocales((HttpServletRequest)pageContext.getRequest());
        while (enum_.hasMoreElements()) {
            final Locale pref = enum_.nextElement();
            final ResourceBundle match = findMatch(basename, pref);
            if (match != null) {
                locCtxt = new LocalizationContext(match, pref);
                break;
            }
        }
        return locCtxt;
    }
    
    private static ResourceBundle findMatch(final String basename, final Locale pref) {
        ResourceBundle match = null;
        try {
            final ClassLoader cl = getClassLoaderCheckingPrivilege();
            final ResourceBundle bundle = ResourceBundle.getBundle(basename, pref, cl);
            final Locale avail = bundle.getLocale();
            if (pref.equals(avail)) {
                match = bundle;
            }
            else if (pref.getLanguage().equals(avail.getLanguage()) && ("".equals(avail.getCountry()) || pref.getCountry().equals(avail.getCountry()))) {
                match = bundle;
            }
        }
        catch (final MissingResourceException ex) {}
        return match;
    }
    
    private static ClassLoader getClassLoaderCheckingPrivilege() {
        final SecurityManager sm = System.getSecurityManager();
        ClassLoader cl;
        if (sm == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        else {
            cl = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
                public ClassLoader run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            });
        }
        return cl;
    }
    
    static {
        EMPTY_LOCALE = new Locale("", "");
    }
}
