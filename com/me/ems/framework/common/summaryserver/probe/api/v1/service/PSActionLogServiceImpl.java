package com.me.ems.framework.common.summaryserver.probe.api.v1.service;

import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import com.me.ems.framework.common.factory.ActionLogService;
import com.me.ems.framework.common.api.v1.service.ActionLogServiceImpl;

public class PSActionLogServiceImpl extends ActionLogServiceImpl implements ActionLogService
{
    @Override
    public void updateRetentionPeriod(final Map<String, String> noOfDaysMap, final User user, final HttpServletRequest httpServletRequest) throws APIException {
        final String isSSRequest = (String)httpServletRequest.getAttribute("summaryServerRequest");
        if (isSSRequest != null && isSSRequest.equalsIgnoreCase("true")) {
            super.updateRetentionPeriod(noOfDaysMap, user, httpServletRequest);
            return;
        }
        throw new APIException(Response.Status.BAD_REQUEST, "GENERIC0002", "ems.ss.common.request_not_allowed_in_probe");
    }
}
