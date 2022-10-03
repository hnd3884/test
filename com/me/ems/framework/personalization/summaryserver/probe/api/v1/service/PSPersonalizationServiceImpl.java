package com.me.ems.framework.personalization.summaryserver.probe.api.v1.service;

import com.me.ems.framework.common.api.utils.APIException;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import com.me.ems.framework.personalization.factory.PersonalizationService;
import com.me.ems.framework.personalization.api.v1.service.PersonalizationServiceImpl;

public class PSPersonalizationServiceImpl extends PersonalizationServiceImpl implements PersonalizationService
{
    @Override
    public Map<String, Object> updatePersonalizationDetails(final Map<String, Object> detailsMap, final User dcUser, final HttpServletRequest httpServletRequest) throws APIException {
        final String isSSRequest = (String)httpServletRequest.getAttribute("summaryServerRequest");
        if (isSSRequest != null && isSSRequest.equalsIgnoreCase("true")) {
            return super.updatePersonalizationDetails(detailsMap, dcUser, httpServletRequest);
        }
        throw new APIException("GENERIC0005");
    }
}
