package com.me.ems.onpremise.summaryserver.summary.authentication.filter;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.start.util.AgentAuthenticationConstants;
import java.io.IOException;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.ems.onpremise.summaryserver.common.authentication.HSKeyValidator;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.logging.Level;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class SummaryServerAuthenticationFilter implements Filter
{
    private static Logger logger;
    private boolean isAuthEnabled;
    private static final boolean ENABLE_AUTH;
    public static String serverHome;
    
    public SummaryServerAuthenticationFilter() {
        this.isAuthEnabled = Boolean.TRUE;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        try {
            if (SummaryServerAuthenticationFilter.ENABLE_AUTH) {
                SummaryServerAuthenticationFilter.logger.log(Level.INFO, "Authentication in websettings.conf : " + SummaryServerAuthenticationFilter.ENABLE_AUTH);
            }
            else {
                this.isAuthEnabled = Boolean.FALSE;
            }
        }
        catch (final Exception e) {
            SummaryServerAuthenticationFilter.logger.log(Level.WARNING, "Error in reading WebSettings.conf file", e);
        }
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        boolean isValid = Boolean.FALSE;
        if (this.isAuthEnabled) {
            SummaryServerAuthenticationFilter.logger.log(Level.INFO, "Entered summary authentication filter");
            final HttpServletRequest servletRequest = (HttpServletRequest)request;
            final HttpServletResponse servletResponse = (HttpServletResponse)response;
            final String probeId = servletRequest.getHeader("probeId");
            final String hsKey = servletRequest.getHeader("hsKey");
            final String summaryKey = servletRequest.getHeader("SummaryAuthorization");
            if (summaryKey == null || hsKey == null || probeId == null) {
                SummaryServerAuthenticationFilter.logger.log(Level.WARNING, "Rejecting request due to Invalid Summary Authorization URI : " + servletRequest.getRequestURI());
                servletResponse.sendError(401, "UnAuthorized");
                return;
            }
            if (HSKeyValidator.getInstance().isValidHSKey(hsKey, Long.parseLong(probeId)) && ProbeMgmtFactoryProvider.getProbeDetailsAPI().isValidSummaryServerAuthKey(summaryKey, Long.valueOf(Long.parseLong(probeId)))) {
                ProbeMgmtFactoryProvider.getProbeDetailsAPI().updateIpAddr(Long.valueOf(Long.parseLong(probeId)), request.getRemoteAddr());
                isValid = Boolean.TRUE;
            }
            if (!isValid) {
                SummaryServerAuthenticationFilter.logger.log(Level.WARNING, "Rejecting request due to Invalid Summary Authorization URI : " + SecurityUtil.getNormalizedRequestURI(servletRequest));
                servletResponse.sendError(401, "UnAuthorized");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
    
    public void destroy() {
    }
    
    static {
        SummaryServerAuthenticationFilter.logger = Logger.getLogger("SummarySyncLogger");
        ENABLE_AUTH = AgentAuthenticationConstants.ENABLE_AUTH_VERIFY;
        SummaryServerAuthenticationFilter.serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
    }
}
