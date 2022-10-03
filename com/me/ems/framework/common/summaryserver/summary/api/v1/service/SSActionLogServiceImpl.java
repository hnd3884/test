package com.me.ems.framework.common.summaryserver.summary.api.v1.service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashMap;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.summaryserver.summary.core.SSActionLogDAOUtil;
import com.me.ems.framework.common.factory.ActionLogService;
import com.me.ems.framework.common.api.v1.service.ActionLogServiceImpl;

public class SSActionLogServiceImpl extends ActionLogServiceImpl implements ActionLogService
{
    SSActionLogDAOUtil actionLogDAOUtil;
    
    public SSActionLogServiceImpl() {
        this.actionLogDAOUtil = new SSActionLogDAOUtil();
    }
    
    @Override
    public Map<String, Object> getSettings(final User dcUser) throws APIException {
        final Map<String, Object> settingsMap = new HashMap<String, Object>();
        try {
            final boolean showRCView = this.isAllCustomersReasonBoxEnabled(dcUser);
            if (!CustomerInfoUtil.isSAS() && showRCView) {
                settingsMap.put("viewName", "RCEventView");
            }
            else {
                settingsMap.put("viewName", "AllEventView");
            }
            final List<Map<String, String>> modulesList = this.actionLogDAOUtil.getEventModuleList();
            settingsMap.put("modules", modulesList);
            final List<Map<String, String>> usersList = this.actionLogDAOUtil.getUsernamesList(dcUser);
            settingsMap.put("users", usersList);
        }
        catch (final Exception e) {
            throw new APIException("GENERIC0005");
        }
        return settingsMap;
    }
    
    @Override
    public void updateRetentionPeriod(final Map<String, String> noOfDaysMap, final User user, final HttpServletRequest httpServletRequest) throws APIException {
        super.updateRetentionPeriod(noOfDaysMap, user, httpServletRequest);
        httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
        httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
        httpServletRequest.setAttribute("eventID", (Object)950806);
    }
}
