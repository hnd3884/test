package com.me.ems.onpremise.common.summaryserver.summary.api.v1.service;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.me.ems.onpremise.common.oauth.OauthDataHandler;
import org.json.JSONObject;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import com.me.ems.summaryserver.common.util.ProbePropertyUtil;
import java.util.HashMap;
import com.adventnet.ds.query.Criteria;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.service.ProbeDetailsService;
import java.util.Map;
import com.me.ems.onpremise.common.factory.SmtpService;
import com.me.ems.onpremise.common.api.v1.service.SmtpServiceImpl;

public class SSSmtpServiceImpl extends SmtpServiceImpl implements SmtpService
{
    @Override
    public Map getProbeSmtpConfiguredStatusList() {
        final ProbeDetailsService probeDetailsService = new ProbeDetailsService();
        final List<HashMap> details = ProbeDetailsService.getProbeDetails(null);
        final Map result = new HashMap();
        if (details != null && details.size() > 0) {
            for (final HashMap detail : details) {
                final Long probeID = detail.get("probeID");
                final String probeName = detail.get("probeName");
                final String val = ProbePropertyUtil.getProbeProperty("ps.server.mail_server_configured", probeID);
                final HashMap probeConfig = new HashMap();
                probeConfig.put("probeName", probeName);
                probeConfig.put("isConfigured", Boolean.valueOf(val));
                result.put(probeID + "", probeConfig);
            }
        }
        return result;
    }
    
    @Override
    public Response updateSmtpSettings(final Map<String, Object> smtpSettingsMap, final String userName, final HttpServletRequest httpServletRequest) throws APIException {
        final Response finalResponse = super.updateSmtpSettings(smtpSettingsMap, userName, httpServletRequest);
        final Boolean isPushToProbes = smtpSettingsMap.get("pushToProbes");
        if (isPushToProbes != null && isPushToProbes) {
            httpServletRequest.setAttribute("isProbeRequest", (Object)Boolean.TRUE);
            httpServletRequest.setAttribute("isReqdForNewProbe", (Object)true);
            httpServletRequest.setAttribute("eventID", (Object)950803);
            if (smtpSettingsMap.get("authType") == 1) {
                final JSONObject probeHandlerObject = new JSONObject();
                probeHandlerObject.put("tokens", (Object)OauthDataHandler.getInstance().getProbeHandlerObject());
                if (smtpSettingsMap.getOrDefault("proxyEnabled", false)) {
                    probeHandlerObject.put("proxyDetails", (Object)this.getProxyDetails());
                }
                httpServletRequest.setAttribute("probeHandlerObject", (Object)probeHandlerObject);
            }
            if (finalResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                final HashMap<String, Object> successResponse = new HashMap<String, Object>();
                String key = "ems.ss.admin.smtp.save_success_probe_repl_init";
                try {
                    key = I18N.getMsg(key, new Object[0]);
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, "Unable to translate i18n " + key);
                }
                successResponse.put("message", key);
                return Response.status(Response.Status.OK).entity((Object)successResponse).build();
            }
        }
        return finalResponse;
    }
    
    public JSONObject getProxyDetails() {
        final JSONObject jsonObject = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProxyConfiguration"));
        final Column column = Column.getColumn("ProxyConfiguration", "*");
        selectQuery.addSelectColumn(column);
        try {
            final DataObject dObj = SyMUtil.getPersistence().get(selectQuery);
            if (!dObj.isEmpty()) {
                final Row row = dObj.getRow("ProxyConfiguration");
                final List<Object> values = row.getValues();
                final List<String> columns = row.getColumns();
                for (int i = 0; i < values.size(); ++i) {
                    if (values.get(i) != null) {
                        jsonObject.put((String)columns.get(i), values.get(i));
                    }
                }
            }
            jsonObject.put("proxyType", (Object)SyMUtil.getSyMParameter("proxyType"));
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception occured in getProxyDetails");
        }
        return jsonObject;
    }
}
