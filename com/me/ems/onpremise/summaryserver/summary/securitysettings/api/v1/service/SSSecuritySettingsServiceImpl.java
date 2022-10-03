package com.me.ems.onpremise.summaryserver.summary.securitysettings.api.v1.service;

import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Iterator;
import java.util.List;
import com.me.ems.summaryserver.common.probeadministration.ProbeDetailsAPI;
import com.me.ems.summaryserver.common.util.ProbePropertyUtil;
import java.util.HashMap;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.Map;
import com.me.ems.onpremise.summaryserver.summary.securitysettings.api.core.SSSecuritySettingsService;
import com.me.ems.framework.securitysettings.api.v1.service.SecuritySettingsServiceImpl;

public class SSSecuritySettingsServiceImpl extends SecuritySettingsServiceImpl implements SSSecuritySettingsService
{
    public Map getProbeSecurityConfiguredPercentageList() {
        final ProbeDetailsAPI probeDetail = ProbeMgmtFactoryProvider.getProbeDetailsAPI();
        final List<Map> details = probeDetail.getAllProbeDetails();
        final Map result = new HashMap();
        if (details != null && details.size() > 0) {
            for (final Map detail : details) {
                final Long probeID = detail.get("probeID");
                final String probeName = detail.get("probeName");
                String val = ProbePropertyUtil.getProbeProperty("ps.security.configured_percentage", probeID);
                val = ((val == null || val.isEmpty()) ? "0" : val);
                final HashMap probeConfig = new HashMap();
                probeConfig.put("probeName", probeName);
                probeConfig.put("percentage", Long.valueOf(val));
                result.put(probeID + "", probeConfig);
            }
        }
        return result;
    }
    
    public Map saveSecuritySettings(final Map saveSecuritySettingsDetails, final User user, final Long customerID, final HttpServletRequest httpServletRequest) throws APIException {
        if (saveSecuritySettingsDetails.containsKey("isShareLocked")) {
            SSSecuritySettingsServiceImpl.logger.log(Level.SEVERE, "parameters found which not allowed for SS while saving security settings");
            throw new APIException(Response.Status.PRECONDITION_FAILED, "IAM0006", "dc.rest.api_params_not_applicable_for_ss", new String[] { "isShareLocked" });
        }
        final Map result = super.saveSecuritySettings(saveSecuritySettingsDetails, user, customerID, httpServletRequest);
        httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
        httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
        httpServletRequest.setAttribute("eventID", (Object)950804);
        return result;
    }
}
