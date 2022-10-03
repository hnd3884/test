package com.me.mdm.onpremise.webclient.filter;

import java.io.IOException;
import javax.servlet.http.HttpSession;
import com.me.mdm.onpremise.server.integration.MDMIntegrationUtil;
import com.me.mdm.onpremise.server.integration.sdp.MDMSDPIntegrationUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.io.File;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class MDMClickJackFilter implements Filter
{
    String xFrameOptionsValue;
    String xFrameOptionsKey;
    boolean isXFrameEnabled;
    private static final Logger SDPINTEGLOGGER;
    
    public MDMClickJackFilter() {
        this.xFrameOptionsValue = "SAMEORIGIN";
        this.xFrameOptionsKey = "tomcat.xframe.options";
        this.isXFrameEnabled = true;
    }
    
    public void init(final FilterConfig fc) throws ServletException {
        final String serverHome = System.getProperty("server.home");
        final File webSettingsConfFile = new File(serverHome + File.separator + "conf" + File.separator + "websettings.conf");
        if (webSettingsConfFile.exists()) {
            try {
                final Properties wsProps = WebServerUtil.getWebServerSettings();
                final String xFrameHeaderValFromWSProps = wsProps.getProperty(this.xFrameOptionsKey);
                if (null != xFrameHeaderValFromWSProps && !"".equalsIgnoreCase(xFrameHeaderValFromWSProps)) {
                    if ("disabled".equalsIgnoreCase(xFrameHeaderValFromWSProps)) {
                        this.isXFrameEnabled = false;
                    }
                    else {
                        this.xFrameOptionsValue = xFrameHeaderValFromWSProps;
                    }
                }
            }
            catch (final Exception ex) {
                Logger.getLogger(MDMClickJackFilter.class.getName()).log(Level.INFO, "Exception in getting webserver settings ", ex);
            }
        }
        Logger.getLogger(MDMClickJackFilter.class.getName()).log(Level.INFO, "Is XFrame Enabled {0}", this.isXFrameEnabled);
    }
    
    public void doFilter(final ServletRequest sr, final ServletResponse sr1, final FilterChain fc) throws IOException, ServletException {
        boolean isIframeLogin = false;
        final HttpServletResponse response = (HttpServletResponse)sr1;
        final HttpServletRequest request = (HttpServletRequest)sr;
        final HttpSession session = request.getSession();
        if (session.getAttribute("isMDMPPluginLogin") != null) {
            isIframeLogin = true;
        }
        if (!isIframeLogin) {
            response.setHeader("X-FRAME-OPTIONS", this.xFrameOptionsValue);
        }
        fc.doFilter((ServletRequest)request, (ServletResponse)response);
        final String reqURI = request.getRequestURI();
        final String queryString = request.getQueryString();
        if (request.getParameter("MDMPSDPIntegrationMode") != null) {
            MDMSDPIntegrationUtil.getInstance().handleSDPUIMETrack(reqURI, queryString);
        }
        if (request.getParameter("tracking_code") != null) {
            final String trackingCode = request.getParameter("tracking_code");
            MDMIntegrationUtil.getInstance().handleIntegrationMETrack(trackingCode, reqURI, queryString);
        }
    }
    
    public void destroy() {
    }
    
    static {
        SDPINTEGLOGGER = Logger.getLogger("MDMSDPIntegrationLog");
    }
}
