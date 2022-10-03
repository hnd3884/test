package com.me.ems.summaryserver.common.settings.service;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import com.me.ems.summaryserver.common.settings.util.SettingsStatusDBUtil;
import java.util.logging.Logger;

public class SettingsStatusService
{
    private final Logger logger;
    SettingsStatusDBUtil settingsStatusDBUtil;
    
    public SettingsStatusService() {
        this.logger = Logger.getLogger("DCAPILogger");
        this.settingsStatusDBUtil = SettingsStatusDBUtil.getInstance();
    }
    
    public Map<String, Object> getSSSettingsStatus(final Long settingsID) throws APIException {
        final Map<String, Object> statusMap = new HashMap<String, Object>();
        try {
            final Long status = this.settingsStatusDBUtil.getSSSettingsStatus(settingsID);
            statusMap.put("status", status);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while getting Summary Server Setting Status", e);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
        return statusMap;
    }
    
    public void saveSSSettingsStatus(final Long settingsID, final Long status, final HttpServletRequest servletRequest) throws APIException {
        try {
            final boolean isUpdated = this.settingsStatusDBUtil.updateSSSettingsStatus(settingsID, status);
            this.logger.log(Level.INFO, "Settings Status updated : " + isUpdated);
            if (isUpdated && SyMUtil.isSummaryServer()) {
                servletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
                servletRequest.setAttribute("eventID", (Object)950000);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while saving Summary Server Setting Status", e);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
}
