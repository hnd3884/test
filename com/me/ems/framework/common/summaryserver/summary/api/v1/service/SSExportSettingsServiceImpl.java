package com.me.ems.framework.common.summaryserver.summary.api.v1.service;

import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.utils.APIException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import com.me.ems.framework.common.factory.ExportSettingsService;
import com.me.ems.framework.common.api.v1.service.ExportSettingsServiceImpl;

public class SSExportSettingsServiceImpl extends ExportSettingsServiceImpl implements ExportSettingsService
{
    @Override
    public boolean saveExportSettings(final Map exportSettingsDetails, final Long customerID, final HttpServletRequest httpServletRequest) throws APIException {
        final boolean exportSaveStatus = super.saveExportSettings(exportSettingsDetails, customerID, httpServletRequest);
        if (exportSaveStatus) {
            httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
            httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
            httpServletRequest.setAttribute("eventID", (Object)950807);
        }
        return exportSaveStatus;
    }
    
    @Override
    public void userChosenRedactLevel(final User user, final Map redactLevelDetails, final HttpServletRequest httpServletRequest) throws APIException {
        super.userChosenRedactLevel(user, redactLevelDetails, httpServletRequest);
        httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
        httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
        httpServletRequest.setAttribute("eventID", (Object)950808);
    }
}
