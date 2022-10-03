package com.me.ems.summaryserver.summary.probedistribution;

import java.util.Map;
import org.json.JSONException;
import java.util.List;
import org.json.JSONObject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.servlet.http.HttpServletRequest;

public interface ProbeDistributionInitializer
{
    void addToProbeQueue(final HttpServletRequest p0, final int p1, final ContainerRequestContext p2, final int p3);
    
    void addToProbeQueue(final JSONObject p0, final List p1);
    
    JSONObject constructRequestJson(final String p0, final String p1, final String p2, final JSONObject p3, final Object p4);
    
    JSONObject storeHeaders(final HttpServletRequest p0) throws JSONException;
    
    void addRequestAuthProperties(final Map p0, final JSONObject p1);
}
