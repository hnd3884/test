package com.me.mdm.webclient.filter;

import java.io.IOException;
import com.me.devicemanagement.framework.server.license.MDMLicenseAPI;
import java.io.PrintWriter;
import com.adventnet.iam.security.ActionRule;
import com.adventnet.iam.security.IAMSecurityException;
import com.me.mdm.api.error.APIError;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.iam.security.SecurityRequestWrapper;
import com.adventnet.iam.security.SecurityUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.servlet.Filter;

public class MDMLicenseEditionFilter implements Filter
{
    protected Pattern skipPattern;
    private Logger LOGGER;
    private static final String APIREGEX = "^(\\/api\\/v\\d+\\/mdm\\/.*$)";
    private static final String VIEWAPIREGEX = "^(\\/.+.ec$)";
    
    public MDMLicenseEditionFilter() {
        this.skipPattern = null;
        this.LOGGER = Logger.getLogger(MDMLicenseEditionFilter.class.getName());
    }
    
    public void init(final FilterConfig fc) throws ServletException {
        final String skip = fc.getInitParameter("skip");
        if (skip != null && skip.length() != 0) {
            this.skipPattern = Pattern.compile(skip);
        }
    }
    
    public void doFilter(final ServletRequest sr, final ServletResponse sr1, final FilterChain fc) throws IOException, ServletException {
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("enrollment.debug.logs")) {
            this.LOGGER.log(Level.INFO, "Entered {0} Filter", MDMLicenseEditionFilter.class.getName());
        }
        final HttpServletRequest hreq = (HttpServletRequest)sr;
        final HttpServletResponse hres = (HttpServletResponse)sr1;
        if (this.skipPattern == null || this.skipPattern.matcher(SecurityUtil.getRequestPath(hreq)).matches()) {
            fc.doFilter(sr, sr1);
            return;
        }
        final SecurityRequestWrapper secureRequest = SecurityRequestWrapper.getInstance(hreq);
        if (secureRequest != null) {
            final ActionRule actionrule = secureRequest.getURLActionRule();
            boolean freeEditionFwdRequired = false;
            boolean freeEdition = false;
            try {
                final String licenseType = LicenseProvider.getInstance().getLicenseType();
                freeEdition = (licenseType != null && licenseType.equals("F"));
            }
            catch (final Exception ex) {
                Logger.getLogger(MDMLicenseEditionFilter.class.getName()).log(Level.WARNING, "Exception while fetching license details", ex);
            }
            if (freeEdition) {
                freeEditionFwdRequired = LicenseProvider.getInstance().isFreeEditionForwardRequired();
            }
            if (!freeEditionFwdRequired) {
                freeEditionFwdRequired = (ApiFactoryProvider.getCacheAccessAPI().getCache("FREE_LICENSE_NOT_CONFIGURED", 2) != null && (boolean)ApiFactoryProvider.getCacheAccessAPI().getCache("FREE_LICENSE_NOT_CONFIGURED", 2));
            }
            if (actionrule != null && freeEditionFwdRequired) {
                final boolean isAPI = Pattern.matches("^(\\/api\\/v\\d+\\/mdm\\/.*$)", secureRequest.getRequestURI()) || Pattern.matches("^(\\/.+.ec$)", secureRequest.getRequestURI());
                if (isAPI) {
                    final Boolean expiredUsage = actionrule.getCustomAttribute("on-expired-allow") != null && actionrule.getCustomAttribute("on-expired-allow").equals("true");
                    if (!expiredUsage) {
                        final HttpServletResponse response = (HttpServletResponse)sr1;
                        response.setStatus(410);
                        response.setHeader("Content-Type", "application/json;charset=UTF-8");
                        response.setCharacterEncoding("UTF-8");
                        final PrintWriter pout = response.getWriter();
                        pout.print(new APIError("LIC0005").toJSONObject().toString());
                        pout.close();
                        return;
                    }
                }
            }
            final String edition = actionrule.getCustomAttribute("MDMEdition");
            if (edition != null) {
                final MDMLicenseAPI licenseAPI = LicenseProvider.getInstance().getMDMLicenseAPI();
                if (edition.equalsIgnoreCase("Professional") && !licenseAPI.isProfessionalLicenseEdition()) {
                    if (Pattern.matches("^(\\/api\\/v\\d+\\/mdm\\/.*$)", secureRequest.getRequestURI()) || Pattern.matches("^(\\/api\\/mdm\\/.*$)", secureRequest.getRequestURI())) {
                        final HttpServletResponse response = (HttpServletResponse)sr1;
                        response.setStatus(401);
                        response.setHeader("Content-Type", "application/json;charset=UTF-8");
                        response.setCharacterEncoding("UTF-8");
                        final PrintWriter pout = response.getWriter();
                        pout.print(new APIError("COM0013").toJSONObject().toString());
                        pout.close();
                        throw new IAMSecurityException("UNAUTHORISED");
                    }
                    sr.setAttribute(IAMSecurityException.class.getName(), (Object)new IAMSecurityException("LICENSE_EDITION_ERROR"));
                    hres.sendError(500);
                }
                else if (edition.equalsIgnoreCase("Enterprise") && !licenseAPI.isEnterpriseLicenseEdition()) {
                    sr.setAttribute(IAMSecurityException.class.getName(), (Object)new IAMSecurityException("LICENSE_EDITION_ERROR"));
                    hres.sendError(500);
                }
            }
        }
        fc.doFilter(sr, sr1);
    }
    
    public void destroy() {
    }
}
