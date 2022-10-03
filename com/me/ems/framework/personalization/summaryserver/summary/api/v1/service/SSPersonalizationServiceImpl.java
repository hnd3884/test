package com.me.ems.framework.personalization.summaryserver.summary.api.v1.service;

import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import com.me.ems.framework.personalization.factory.PersonalizationService;
import com.me.ems.framework.personalization.api.v1.service.PersonalizationServiceImpl;

public class SSPersonalizationServiceImpl extends PersonalizationServiceImpl implements PersonalizationService
{
    @Override
    public Map<String, Object> updatePersonalizationDetails(final Map<String, Object> detailsMap, final User dcUser, final HttpServletRequest httpServletRequest) throws APIException {
        final Map<String, Object> result = super.updatePersonalizationDetails(detailsMap, dcUser, httpServletRequest);
        httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
        final Long loginId = dcUser.getLoginID();
        if (DMUserHandler.isUserInRole(loginId, "All_Managed_Probes")) {
            httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
            httpServletRequest.setAttribute("eventID", (Object)950802);
            httpServletRequest.setAttribute("eventUniqueID", (Object)loginId);
        }
        return result;
    }
}
