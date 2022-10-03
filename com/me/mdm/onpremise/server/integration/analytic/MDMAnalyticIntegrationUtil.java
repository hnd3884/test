package com.me.mdm.onpremise.server.integration.analytic;

import java.util.Hashtable;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import java.util.Properties;
import com.me.mdm.onpremise.server.integration.MDMIntegrationUtil;
import com.me.mdm.onpremise.server.integration.sdp.MDMSDPIntegrationUtil;
import org.json.JSONObject;
import com.me.mdm.api.reports.integ.MDMAnalyticRequestHandler;

public class MDMAnalyticIntegrationUtil implements MDMAnalyticRequestHandler
{
    public static final String ANALYTIC_APPNAME = "AnalyticPlus";
    public static final String ANALYTIC_DB_ID = "analytic_db_id";
    public static final String SERVER = "server";
    public static final String PORT = "port";
    public static final String PROTOCOL = "protocol";
    
    public JSONObject getSettings() throws JSONException {
        final Properties analyticServerProp = MDMSDPIntegrationUtil.getInstance().getServerSettings("AnalyticPlus");
        if (analyticServerProp == null) {
            return null;
        }
        final JSONObject responseJSONBody = new JSONObject();
        responseJSONBody.put("server", (Object)analyticServerProp.getProperty("SERVER"));
        responseJSONBody.put("port", (Object)analyticServerProp.getProperty("PORT"));
        responseJSONBody.put("protocol", (Object)analyticServerProp.getProperty("PROTOCOL"));
        final StringBuilder urlFormation = new StringBuilder(analyticServerProp.getProperty("PROTOCOL").toLowerCase()).append("://").append(analyticServerProp.getProperty("SERVER")).append(":").append(analyticServerProp.getProperty("PORT"));
        final String analticDbId = MDMIntegrationUtil.getInstance().getIntegrationParamValue("analytic_db_id");
        if (analticDbId != null && !analticDbId.isEmpty()) {
            responseJSONBody.put("analytic_db_id", (Object)analticDbId);
            urlFormation.append("/workspace/").append(analticDbId);
        }
        responseJSONBody.put("url", (Object)urlFormation.toString());
        return responseJSONBody;
    }
    
    public void deleteSettings() throws SyMException {
        MDMSDPIntegrationUtil.getInstance().deleteServerSettings("AnalyticPlus");
    }
    
    public void addOrUpdateSettings(final JSONObject requestJson) throws SyMException {
        final Properties analyticServerProp = new Properties();
        this.checkPostParams(requestJson);
        ((Hashtable<String, String>)analyticServerProp).put("SERVER", requestJson.getString("server"));
        ((Hashtable<String, String>)analyticServerProp).put("PORT", String.valueOf(requestJson.get("port")));
        ((Hashtable<String, String>)analyticServerProp).put("PROTOCOL", requestJson.getString("protocol"));
        MDMSDPIntegrationUtil.getInstance().addOrUpdateServerSettings("AnalyticPlus", analyticServerProp);
        String analyticDBId = "";
        if (requestJson.has("analytic_db_id")) {
            analyticDBId = String.valueOf(requestJson.get("analytic_db_id"));
        }
        MDMSDPIntegrationUtil.getInstance().updateIntegrationParameter("analytic_db_id", analyticDBId);
    }
    
    public void checkPostParams(final JSONObject obj) {
        if (!obj.has("server")) {
            throw new APIHTTPException("COM0005", new Object[] { "server" });
        }
        if (!obj.has("port")) {
            throw new APIHTTPException("COM0005", new Object[] { "port" });
        }
        if (!obj.has("protocol")) {
            throw new APIHTTPException("COM0005", new Object[] { "protocol" });
        }
    }
}
