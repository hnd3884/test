package com.me.ems.onpremise.uac.summaryserver.summary.api.v1.service;

import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import com.me.ems.onpremise.uac.api.v1.service.factory.TFAService;
import com.me.ems.onpremise.uac.api.v1.service.TFAServiceImpl;

public class SSTFAServiceImpl extends TFAServiceImpl implements TFAService
{
    @Override
    public Response saveTwoFactorDetails(final Map twoFactorDetails, final User user, final HttpServletRequest httpServletRequest) throws APIException {
        httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
        httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
        httpServletRequest.setAttribute("eventID", (Object)950711);
        return super.saveTwoFactorDetails(twoFactorDetails, user, httpServletRequest);
    }
}
