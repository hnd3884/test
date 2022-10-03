package com.me.ems.onpremise.common.summaryserver.probe.api.v1.service;

import com.me.ems.summaryserver.probe.sync.factory.ProbeSyncAPI;
import com.me.ems.summaryserver.common.probeadministration.ProbeDetailsAPI;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.http.entity.InputStreamEntity;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.me.ems.onpremise.summaryserver.summary.probedistribution.ForwardToProbeUtil;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.onpremise.server.metrack.EvaluatorTrackerUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import java.util.Map;
import java.util.logging.Level;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.onpremise.common.factory.RequestDemoService;
import com.me.ems.onpremise.common.api.v1.service.RequestDemoServiceImpl;

public class PSRequestDemoServiceImpl extends RequestDemoServiceImpl implements RequestDemoService
{
    @Override
    public Response skipRequestDemoPage(final Long loginID, final HttpServletRequest httpServletRequest) {
        final String isSSRequest = (String)httpServletRequest.getAttribute("summaryServerRequest");
        if (isSSRequest != null && isSSRequest.equalsIgnoreCase("true")) {
            return super.skipRequestDemoPage(loginID, httpServletRequest);
        }
        if (this.pushDemoRequestToSummaryServer(loginID, httpServletRequest)) {
            return Response.ok().build();
        }
        PSRequestDemoServiceImpl.logger.log(Level.WARNING, "unable to contact SS server so updating the probe alone");
        return super.skipRequestDemoPage(loginID, httpServletRequest);
    }
    
    @Override
    public Response neverShowRequestDemoPageAgain(final Long loginID, final HttpServletRequest httpServletRequest) {
        final String isSSRequest = (String)httpServletRequest.getAttribute("summaryServerRequest");
        if (isSSRequest != null && isSSRequest.equalsIgnoreCase("true")) {
            return super.neverShowRequestDemoPageAgain(loginID, httpServletRequest);
        }
        if (this.pushDemoRequestToSummaryServer(loginID, httpServletRequest)) {
            return Response.ok().build();
        }
        PSRequestDemoServiceImpl.logger.log(Level.WARNING, "unable to contact SS server so updating the probe alone");
        return super.neverShowRequestDemoPageAgain(loginID, httpServletRequest);
    }
    
    @Override
    public Response registerRequestDemo(final Map<String, Object> detailsMap, final Long loginID, final String loginName, final HttpServletRequest httpServletRequest) throws APIException {
        final String isSSRequest = (String)httpServletRequest.getAttribute("summaryServerRequest");
        if (isSSRequest != null && isSSRequest.equalsIgnoreCase("true")) {
            DCEventLogUtil.getInstance().addEvent(121, loginName, (HashMap)null, "dm.request.demo.event.logger", (Object)loginName, false, CustomerInfoUtil.getInstance().getCustomerId());
            EvaluatorTrackerUtil.getInstance().addOrIncrementClickCountForTrialUsers("RequestDemoRegisteredUser", String.valueOf(loginID));
            return Response.ok().build();
        }
        if (this.pushDemoRequestToSummaryServer(loginID, httpServletRequest)) {
            return Response.ok().build();
        }
        PSRequestDemoServiceImpl.logger.log(Level.WARNING, "unable to contact SS server so updating the probe alone");
        return super.registerRequestDemo(detailsMap, loginID, loginName, httpServletRequest);
    }
    
    private boolean pushDemoRequestToSummaryServer(final Long loginID, final HttpServletRequest httpServletRequest) {
        try {
            DataOutputStream dataOutStream = null;
            final ProbeDetailsAPI probeDetailsAPI = ProbeMgmtFactoryProvider.getProbeDetailsAPI();
            final String baseURL = probeDetailsAPI.getSummaryServerBaseURL();
            if (baseURL != null) {
                final StringBuilder strUrl = new StringBuilder();
                strUrl.append(baseURL);
                strUrl.append(SecurityUtil.getNormalizedRequestURI(httpServletRequest));
                ForwardToProbeUtil.formatAndAddQueryParam(strUrl, httpServletRequest.getQueryString());
                final URL url = new URL(strUrl.toString());
                final ProbeSyncAPI probeSyncAPI = ProbeMgmtFactoryProvider.getProbeSyncAPI();
                final HttpURLConnection urlConnection = (HttpURLConnection)probeSyncAPI.createSummaryServerConnection(url, httpServletRequest.getMethod(), httpServletRequest.getContentType(), httpServletRequest.getHeader("Accept"), true, true);
                dataOutStream = new DataOutputStream(urlConnection.getOutputStream());
                new InputStreamEntity((InputStream)httpServletRequest.getInputStream()).writeTo((OutputStream)dataOutStream);
                dataOutStream.flush();
                final int httpResponseCode = urlConnection.getResponseCode();
                PSRequestDemoServiceImpl.logger.log(Level.INFO, "Push  status Update Response Code: " + httpResponseCode);
                return httpResponseCode == 200;
            }
            PSRequestDemoServiceImpl.logger.log(Level.WARNING, "Unable to get SS url, ignoring state update for");
        }
        catch (final Exception e) {
            PSRequestDemoServiceImpl.logger.log(Level.SEVERE, "Exception while pushing other PS Request Demo status to SS", e);
        }
        return false;
    }
}
