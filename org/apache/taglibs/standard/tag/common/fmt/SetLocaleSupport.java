package org.apache.taglibs.standard.tag.common.fmt;

import java.util.List;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.text.DateFormat;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.Config;
import org.apache.taglibs.standard.tag.common.core.Util;
import java.util.Locale;
import javax.servlet.jsp.tagext.TagSupport;

public abstract class SetLocaleSupport extends TagSupport
{
    protected Object value;
    protected String variant;
    private int scope;
    static Locale[] availableFormattingLocales;
    
    public SetLocaleSupport() {
        this.init();
    }
    
    private void init() {
        final String s = null;
        this.variant = s;
        this.value = s;
        this.scope = 1;
    }
    
    public void setScope(final String scope) {
        this.scope = Util.getScope(scope);
    }
    
    public int doEndTag() throws JspException {
        Locale locale;
        if (this.value instanceof Locale) {
            locale = (Locale)this.value;
        }
        else if (this.value instanceof String && !"".equals(((String)this.value).trim())) {
            locale = LocaleUtil.parseLocale((String)this.value, this.variant);
        }
        else {
            locale = Locale.getDefault();
        }
        Config.set(this.pageContext, "javax.servlet.jsp.jstl.fmt.locale", (Object)locale, this.scope);
        setResponseLocale(this.pageContext, locale);
        return 6;
    }
    
    public void release() {
        this.init();
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
    
    static Locale getFormattingLocale(final PageContext pc, final Tag fromTag, final boolean format, final Locale[] avail) {
        final Tag parent = findAncestorWithClass(fromTag, (Class)BundleSupport.class);
        if (parent != null) {
            final LocalizationContext locCtxt = ((BundleSupport)parent).getLocalizationContext();
            if (locCtxt.getLocale() != null) {
                if (format) {
                    setResponseLocale(pc, locCtxt.getLocale());
                }
                return locCtxt.getLocale();
            }
        }
        LocalizationContext locCtxt;
        if ((locCtxt = BundleSupport.getLocalizationContext(pc)) != null && locCtxt.getLocale() != null) {
            if (format) {
                setResponseLocale(pc, locCtxt.getLocale());
            }
            return locCtxt.getLocale();
        }
        Locale pref = getLocale(pc, "javax.servlet.jsp.jstl.fmt.locale");
        Locale match;
        if (pref != null) {
            match = findFormattingMatch(pref, avail);
        }
        else {
            match = findFormattingMatch(pc, avail);
        }
        if (match == null) {
            pref = getLocale(pc, "javax.servlet.jsp.jstl.fmt.fallbackLocale");
            if (pref != null) {
                match = findFormattingMatch(pref, avail);
            }
        }
        if (format && match != null) {
            setResponseLocale(pc, match);
        }
        return match;
    }
    
    static Locale getFormattingLocale(final PageContext pc) {
        Locale pref = getLocale(pc, "javax.servlet.jsp.jstl.fmt.locale");
        Locale match;
        if (pref != null) {
            match = findFormattingMatch(pref, SetLocaleSupport.availableFormattingLocales);
        }
        else {
            match = findFormattingMatch(pc, SetLocaleSupport.availableFormattingLocales);
        }
        if (match == null) {
            pref = getLocale(pc, "javax.servlet.jsp.jstl.fmt.fallbackLocale");
            if (pref != null) {
                match = findFormattingMatch(pref, SetLocaleSupport.availableFormattingLocales);
            }
        }
        if (match != null) {
            setResponseLocale(pc, match);
        }
        return match;
    }
    
    static Locale getLocale(final PageContext pageContext, final String name) {
        Locale loc = null;
        final Object obj = Config.find(pageContext, name);
        if (obj != null) {
            if (obj instanceof Locale) {
                loc = (Locale)obj;
            }
            else {
                loc = LocaleUtil.parseLocale((String)obj);
            }
        }
        return loc;
    }
    
    private static Locale findFormattingMatch(final PageContext pageContext, final Locale[] avail) {
        Locale match = null;
        final Enumeration enum_ = Util.getRequestLocales((HttpServletRequest)pageContext.getRequest());
        while (enum_.hasMoreElements()) {
            final Locale locale = enum_.nextElement();
            match = findFormattingMatch(locale, avail);
            if (match != null) {
                break;
            }
        }
        return match;
    }
    
    private static Locale findFormattingMatch(final Locale pref, final Locale[] avail) {
        Locale match = null;
        boolean langAndCountryMatch = false;
        for (final Locale locale : avail) {
            if (pref.equals(locale)) {
                match = locale;
                break;
            }
            if (!"".equals(pref.getVariant()) && "".equals(locale.getVariant()) && pref.getLanguage().equals(locale.getLanguage()) && pref.getCountry().equals(locale.getCountry())) {
                match = locale;
                langAndCountryMatch = true;
            }
            else if (!langAndCountryMatch && pref.getLanguage().equals(locale.getLanguage()) && "".equals(locale.getCountry()) && match == null) {
                match = locale;
            }
        }
        return match;
    }
    
    static {
        final Locale[] dateLocales = DateFormat.getAvailableLocales();
        final Locale[] numberLocales = NumberFormat.getAvailableLocales();
        final List<Locale> locales = new ArrayList<Locale>(dateLocales.length);
        for (final Locale dateLocale : dateLocales) {
            for (final Locale numberLocale : numberLocales) {
                if (dateLocale.equals(numberLocale)) {
                    locales.add(dateLocale);
                    break;
                }
            }
        }
        SetLocaleSupport.availableFormattingLocales = locales.toArray(new Locale[locales.size()]);
    }
}
