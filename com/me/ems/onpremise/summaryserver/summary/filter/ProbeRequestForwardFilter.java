package com.me.ems.onpremise.summaryserver.summary.filter;

import com.adventnet.i18n.I18N;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.Consts;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.ProbeReachabilityChecker;
import com.me.ems.summaryserver.common.api.response.DMSSAPIErrorCodes;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import javax.servlet.FilterConfig;
import com.me.ems.onpremise.summaryserver.summary.probedistribution.ForwardToProbeUtil;
import java.util.logging.Logger;
import javax.ws.rs.ext.Provider;
import javax.servlet.Filter;

@Provider
public class ProbeRequestForwardFilter implements Filter
{
    private static Logger logger;
    private boolean isDCSS;
    private ForwardToProbeUtil proxyClientUtil;
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.isDCSS = SyMUtil.isSummaryServer();
        if (this.isDCSS) {
            this.proxyClientUtil = new ForwardToProbeUtil("Probe Proxy", true);
        }
    }
    
    public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain filterChain) throws IOException, ServletException {
        if (this.isDCSS) {
            final HttpServletResponse response = (HttpServletResponse)resp;
            final HttpServletRequest request = (HttpServletRequest)req;
            String prbId = request.getHeader("X-ProbeID");
            prbId = ((prbId == null) ? this.getProbeIdFromQueryParam(request.getQueryString()) : prbId);
            if (prbId != null) {
                ProbeRequestForwardFilter.logger.log(Level.INFO, "Request with Probe ID " + prbId + " Url " + (Object)request.getRequestURL());
                if (!prbId.equals("0") && !prbId.equals("-1")) {
                    final Long probeId = Long.parseLong(prbId);
                    final int probeStatus = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getProbeLiveStatus(probeId);
                    try {
                        if (probeStatus == 1) {
                            ProbeRequestForwardFilter.logger.log(Level.INFO, "Going to Forward request to probe.....");
                            this.proxyClientUtil.proxyRequest(request, response, probeId, new Object[0]);
                            ProbeRequestForwardFilter.logger.log(Level.INFO, "serving response from probe");
                        }
                        else {
                            this.forwardUIErrorResponse(response, 503, DMSSAPIErrorCodes.PROBE_NOT_REACHABLE, "ems.ss.probe.probe_down", null);
                        }
                    }
                    catch (final UnknownHostException | ConnectException e) {
                        ProbeRequestForwardFilter.logger.log(Level.SEVERE, "Exception while handling ProbeRequestForward", e);
                        ProbeRequestForwardFilter.logger.log(Level.INFO, "{0} probe request {1} request Exception ", new String[] { prbId, request.getRequestURL().toString() });
                        this.forwardUIErrorResponse(response, 503, DMSSAPIErrorCodes.PROBE_NOT_REACHABLE, "ems.ss.requestforward.probe_not_reachable", e.getMessage());
                        ProbeReachabilityChecker.checkAndUpdateLiveStatus(probeId, null, true);
                    }
                    catch (final Exception e2) {
                        ProbeRequestForwardFilter.logger.log(Level.SEVERE, "Exception while handling ProbeRequestForward", e2);
                        ProbeRequestForwardFilter.logger.log(Level.INFO, "{0} probe request {1} request Exception ", new String[] { prbId, request.getRequestURL().toString() });
                        this.forwardUIErrorResponse(response, 503, "GENERIC0005", "ems.ss.requestforward.error_occur", e2.getMessage());
                    }
                    return;
                }
            }
        }
        filterChain.doFilter(req, resp);
    }
    
    public void destroy() {
        if (this.proxyClientUtil != null) {
            this.proxyClientUtil.destroy();
        }
    }
    
    private String getProbeIdFromQueryParam(final String queryString) {
        final List<NameValuePair> queryParams = URLEncodedUtils.parse(queryString, Consts.UTF_8);
        for (final NameValuePair queryParam : queryParams) {
            if (queryParam.getName().equals("X-ProbeID")) {
                return queryParam.getValue();
            }
        }
        return null;
    }
    
    private void forwardUIErrorResponse(final HttpServletResponse response, final int status, final Object errorCode, final String errorMsgI18n, final String exceptionMsg) throws IOException {
        final JSONObject errorOut = new JSONObject();
        String i18nVal = errorMsgI18n;
        try {
            i18nVal = I18N.getMsg(errorMsgI18n, new Object[0]);
        }
        catch (final Exception e) {
            ProbeRequestForwardFilter.logger.log(Level.SEVERE, "Error while get i18n key " + errorMsgI18n);
        }
        errorOut.put("errorCode", (Object)String.valueOf(errorCode));
        errorOut.put("errorMsg", (Object)i18nVal);
        if (exceptionMsg != null) {
            errorOut.put("exception", (Object)exceptionMsg);
        }
        response.setStatus(status);
        response.setContentType("application/json");
        response.getOutputStream().print(errorOut.toString());
        response.flushBuffer();
        response.getOutputStream().flush();
    }
    
    static {
        ProbeRequestForwardFilter.logger = Logger.getLogger("SummaryUIRequestForward");
    }
}
