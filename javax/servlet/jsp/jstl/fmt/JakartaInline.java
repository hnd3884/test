package javax.servlet.jsp.jstl.fmt;

import javax.servlet.ServletResponse;
import java.util.Vector;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.PageContext;
import java.util.Locale;

class JakartaInline
{
    static final String UNDEFINED_KEY = "???";
    private static final Locale EMPTY_LOCALE;
    private static final char HYPHEN = '-';
    private static final char UNDERSCORE = '_';
    static final String REQUEST_CHAR_SET = "javax.servlet.jsp.jstl.fmt.request.charset";
    
    static LocalizationContext getLocalizationContext(final PageContext pc) {
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
    
    static LocalizationContext getLocalizationContext(final PageContext pc, final String basename) {
        LocalizationContext locCtxt = null;
        ResourceBundle bundle = null;
        if (basename == null || basename.equals("")) {
            return new LocalizationContext();
        }
        Locale pref = getLocale(pc, "javax.servlet.jsp.jstl.fmt.locale");
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
            pref = getLocale(pc, "javax.servlet.jsp.jstl.fmt.fallbackLocale");
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
                bundle = ResourceBundle.getBundle(basename, JakartaInline.EMPTY_LOCALE, cl);
                if (bundle != null) {
                    locCtxt = new LocalizationContext(bundle, null);
                }
            }
            catch (final MissingResourceException ex) {}
        }
        if (locCtxt != null) {
            if (locCtxt.getLocale() != null) {
                setResponseLocale(pc, locCtxt.getLocale());
            }
        }
        else {
            locCtxt = new LocalizationContext();
        }
        return locCtxt;
    }
    
    private static LocalizationContext findMatch(final PageContext pageContext, final String basename) {
        LocalizationContext locCtxt = null;
        final Enumeration enum_ = getRequestLocales((HttpServletRequest)pageContext.getRequest());
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
    
    public static Enumeration getRequestLocales(final HttpServletRequest request) {
        final Enumeration values = request.getHeaders("accept-language");
        if (values == null) {
            return new Vector().elements();
        }
        if (values.hasMoreElements()) {
            return request.getLocales();
        }
        return values;
    }
    
    static void setResponseLocale(final PageContext pc, final Locale locale) {
        final ServletResponse response = pc.getResponse();
        response.setLocale(locale);
        if (pc.getSession() != null) {
            try {
                pc.setAttribute("javax.servlet.jsp.jstl.fmt.request.charset", (Object)response.getCharacterEncoding(), 3);
            }
            catch (final IllegalStateException ex) {}
        }
    }
    
    static Locale getLocale(final PageContext pageContext, final String name) {
        Locale loc = null;
        final Object obj = Config.find(pageContext, name);
        if (obj != null) {
            if (obj instanceof Locale) {
                loc = (Locale)obj;
            }
            else {
                loc = parseLocale((String)obj, null);
            }
        }
        return loc;
    }
    
    private static Locale parseLocale(final String locale, final String variant) {
        Locale ret = null;
        String language = locale;
        String country = null;
        int index = -1;
        if ((index = locale.indexOf(45)) > -1 || (index = locale.indexOf(95)) > -1) {
            language = locale.substring(0, index);
            country = locale.substring(index + 1);
        }
        if (language == null || language.length() == 0) {
            throw new IllegalArgumentException("Missing language component in 'value' attribute in &lt;setLocale&gt;");
        }
        if (country == null) {
            if (variant != null) {
                ret = new Locale(language, "", variant);
            }
            else {
                ret = new Locale(language, "");
            }
        }
        else {
            if (country.length() <= 0) {
                throw new IllegalArgumentException("Empty country component in 'value' attribute in &lt;setLocale&gt;");
            }
            if (variant != null) {
                ret = new Locale(language, country, variant);
            }
            else {
                ret = new Locale(language, country);
            }
        }
        return ret;
    }
    
    static {
        EMPTY_LOCALE = new Locale("", "");
    }
}
