package com.adventnet.sym.webclient.mdm.enroll;

import java.io.IOException;
import java.util.Properties;
import java.util.Locale;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class MDMI18NLocaleFilter implements Filter
{
    public Logger logger;
    private FilterConfig fc;
    
    public MDMI18NLocaleFilter() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public void init(final FilterConfig fc) throws ServletException {
        this.fc = fc;
    }
    
    public void doFilter(final ServletRequest sr, final ServletResponse sr1, final FilterChain fc) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest)sr;
        final Locale locale = request.getLocale();
        this.logger.log(Level.INFO, "## Device Locale display : {0}", locale.getDisplayLanguage());
        this.logger.log(Level.INFO, "## Device Locale : {0}", locale.toString());
        final Properties localeProps = MDMUtil.getLocalesProperties();
        final boolean isLangLicEnabled = LicenseProvider.getInstance().isLanguagePackEnabled();
        if (localeProps.containsKey(locale.toString()) && isLangLicEnabled) {
            I18N.setRequestLocale(locale);
        }
        else {
            I18N.setRequestLocale(new Locale("en", "US"));
        }
        try {
            fc.doFilter(sr, sr1);
        }
        finally {
            I18N.resetRequestLocale();
        }
    }
    
    public void destroy() {
    }
}
