package com.me.ems.onpremise.summaryserver.summary.webclient;

import org.glassfish.jersey.internal.util.Base64;
import com.me.ems.onpremise.summaryserver.summary.authentication.ProbeAuthUtil;
import com.me.devicemanagement.onpremise.summaryserver.summary.webclient.authentication.TicketUtil;
import com.me.ems.onpremise.summaryserver.summary.proberegistration.ProbeUtil;
import java.util.HashMap;
import java.util.Map;

public class SummaryProbeAuthenticationUtil
{
    protected static final String PROBE_DETAILS = "probeDetails";
    
    protected Map<String, Object> getProbeAuthDetails(final Long probeID) throws Exception {
        final Map<String, Object> probeAuthParams = new HashMap<String, Object>();
        final HashMap probeDetailMap = ProbeUtil.getInstance().getProbeDetail(probeID);
        if (probeDetailMap != null && !probeDetailMap.isEmpty()) {
            final String ticket = TicketUtil.createTicket();
            final String probeKey = ProbeAuthUtil.getInstance().getProbeAuthKey(probeID);
            final String encodedProbeKey = Base64.encodeAsString(probeKey);
            probeAuthParams.put("ticket", ticket);
            probeAuthParams.put("probeKey", encodedProbeKey);
            probeAuthParams.put("accessProbe", true);
            return probeAuthParams;
        }
        throw new Exception("Invalid ProbeID");
    }
}
