package com.me.emsalerts.sms.summaryserver.summary.api.v1.service;

import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Iterator;
import java.util.List;
import com.me.ems.summaryserver.common.util.ProbePropertyUtil;
import java.util.HashMap;
import com.adventnet.ds.query.Criteria;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.Map;
import com.me.emsalerts.sms.factory.SMSService;
import com.me.emsalerts.sms.api.v1.service.SMSServiceImpl;

public class SSSMSServiceImpl extends SMSServiceImpl implements SMSService
{
    @Override
    public Map getProbeSMSConfiguredStatusList() {
        final List<HashMap> details = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getProbeDetails((Criteria)null);
        final Map result = new HashMap();
        if (details != null && details.size() > 0) {
            for (final HashMap detail : details) {
                final Long probeID = detail.get("probeID");
                final String probeName = detail.get("probeName");
                final String val = ProbePropertyUtil.getProbeProperty("ps.ems.sms_server_configured", probeID);
                final HashMap probeConfig = new HashMap();
                probeConfig.put("probeName", probeName);
                probeConfig.put("isConfigured", Boolean.valueOf(val));
                result.put(probeID + "", probeConfig);
            }
        }
        return result;
    }
    
    @Override
    public Response updateSMSSettings(final User user, final Map smsConfigProperties, final HttpServletRequest httpServletRequest) {
        Response finalResponse = super.updateSMSSettings(user, smsConfigProperties, httpServletRequest);
        final Boolean isPushToProbes = smsConfigProperties.get("pushToProbes");
        if (isPushToProbes != null && isPushToProbes) {
            httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
            httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
            httpServletRequest.setAttribute("eventID", (Object)950805);
            if (finalResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                final HashMap<String, Object> successResponse = new HashMap<String, Object>();
                String key = "ems.ss.common.sms.save_success_probe_repl_init";
                try {
                    key = I18N.getMsg(key, new Object[0]);
                }
                catch (final Exception e) {
                    SSSMSServiceImpl.logger.log(Level.SEVERE, "Unable to translate i18n " + key);
                }
                successResponse.put("message", key);
                finalResponse = Response.status(Response.Status.OK).entity((Object)successResponse).build();
            }
        }
        return finalResponse;
    }
}
