package com.me.ems.onpremise.uac.api.v1.service.summaryserver.summary;

import com.me.ems.framework.common.api.utils.APIException;
import java.util.List;
import com.me.devicemanagement.onpremise.server.authentication.summaryserver.summary.ProbeUsersUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.onpremise.uac.api.v1.service.factory.ChangePasswordService;
import com.me.ems.onpremise.uac.api.v1.service.ChangePasswordServiceImpl;

public class SSChangePasswordServiceImpl extends ChangePasswordServiceImpl implements ChangePasswordService
{
    @Override
    public Map<String, Object> changePassword(final Long userID, final Long loginID, final String loginName, final String oldPassword, final String newPassword, final HttpServletRequest httpServletRequest) throws APIException {
        final List<Long> targetProbes = ProbeUsersUtil.getProbeIdsForLoginId(DMUserHandler.getLoginIdForUser(loginName));
        httpServletRequest.setAttribute("targetProbes", (Object)targetProbes);
        httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
        if (ProbeUsersUtil.isUserManagingAllProbes(loginID)) {
            httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
            httpServletRequest.setAttribute("eventID", (Object)950710);
            httpServletRequest.setAttribute("eventUniqueID", (Object)loginID);
        }
        return super.changePassword(userID, loginID, loginName, oldPassword, newPassword, httpServletRequest);
    }
}
