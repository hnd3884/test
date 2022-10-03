package com.me.ems.onpremise.common.summaryserver.summary.api.v1.service;

import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.onpremise.common.factory.RequestDemoService;
import com.me.ems.onpremise.common.api.v1.service.RequestDemoServiceImpl;

public class SSRequestDemoServiceImpl extends RequestDemoServiceImpl implements RequestDemoService
{
    @Override
    public Response skipRequestDemoPage(final Long loginID, final HttpServletRequest httpServletRequest) {
        final Response result = super.skipRequestDemoPage(loginID, httpServletRequest);
        if (result.getStatus() == Response.Status.OK.getStatusCode()) {
            httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
            httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
            httpServletRequest.setAttribute("eventID", (Object)950809);
        }
        return result;
    }
    
    @Override
    public Response neverShowRequestDemoPageAgain(final Long loginID, final HttpServletRequest httpServletRequest) {
        final Response result = super.neverShowRequestDemoPageAgain(loginID, httpServletRequest);
        if (result.getStatus() == Response.Status.OK.getStatusCode()) {
            httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
            httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
            httpServletRequest.setAttribute("eventID", (Object)950810);
        }
        return result;
    }
    
    @Override
    public Response registerRequestDemo(final Map<String, Object> detailsMap, final Long loginID, final String loginName, final HttpServletRequest httpServletRequest) throws APIException {
        final Response result = super.registerRequestDemo(detailsMap, loginID, loginName, httpServletRequest);
        if (result.getStatus() == Response.Status.OK.getStatusCode()) {
            httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
            httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
            httpServletRequest.setAttribute("eventID", (Object)950811);
        }
        return result;
    }
}
