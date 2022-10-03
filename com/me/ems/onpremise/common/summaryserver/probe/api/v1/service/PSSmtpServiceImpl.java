package com.me.ems.onpremise.common.summaryserver.probe.api.v1.service;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.ems.framework.common.api.utils.APIException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.HashMap;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import com.me.ems.onpremise.common.factory.SmtpService;
import com.me.ems.onpremise.common.api.v1.service.SmtpServiceImpl;

public class PSSmtpServiceImpl extends SmtpServiceImpl implements SmtpService
{
    @Override
    public Response updateSmtpSettings(final Map<String, Object> smtpSettingsMap, final String userName, final HttpServletRequest httpServletRequest) throws APIException {
        final boolean isOauth = smtpSettingsMap.get("authType") == 1;
        if (isOauth && smtpSettingsMap.containsKey("probeHandlerObject")) {
            final HashMap probeHandlerObject = smtpSettingsMap.get("probeHandlerObject");
            final HashMap tokenData = probeHandlerObject.get("tokens");
            smtpSettingsMap.put("smtpPassword", tokenData.get("ACCESS_TOKEN"));
            smtpSettingsMap.put("REFRESH_TOKEN", tokenData.get("REFRESH_TOKEN"));
            smtpSettingsMap.put("EXPIRES_AT", tokenData.get("EXPIRES_AT"));
            final HashMap proxyDetails = probeHandlerObject.get("proxyDetails");
            try {
                this.putProxyDetails(proxyDetails);
            }
            catch (final DataAccessException e) {
                this.logger.log(Level.SEVERE, "Exception in putProxyDetails", (Throwable)e);
            }
        }
        return super.updateSmtpSettings(smtpSettingsMap, userName, httpServletRequest);
    }
    
    public void putProxyDetails(final HashMap proxyDetails) throws DataAccessException {
        if (proxyDetails != null) {
            final DownloadManager downloadManager = DownloadManager.getInstance();
            downloadManager.setProxyType((int)proxyDetails.get("proxyType"));
            SyMUtil.updateSyMParameter("proxyType", Integer.toString(proxyDetails.get("proxyType")));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ProxyConfiguration"));
            selectQuery.addSelectColumn(new Column("ProxyConfiguration", "*"));
            final Persistence persistence = SyMUtil.getPersistence();
            final DataObject dObj = persistence.get(selectQuery);
            if (proxyDetails.size() > 1) {
                Row row = null;
                boolean addRow = false;
                if (dObj.isEmpty()) {
                    row = new Row("ProxyConfiguration");
                    addRow = true;
                }
                else {
                    row = dObj.getFirstRow("ProxyConfiguration");
                }
                row.set("PCID", proxyDetails.get("PCID"));
                row.set("HTTPPROXYPORT", proxyDetails.get("HTTPPROXYPORT"));
                row.set("HTTPPROXYPASSWORD", proxyDetails.get("HTTPPROXYPASSWORD"));
                row.set("HTTPPROXYUSER", proxyDetails.get("HTTPPROXYUSER"));
                row.set("HTTPPROXYHOST", proxyDetails.get("HTTPPROXYHOST"));
                row.set("FTPPROXYPORT", proxyDetails.get("FTPPROXYPORT"));
                row.set("PROXYSCRIPT", proxyDetails.get("PROXYSCRIPT"));
                row.set("PROXYSCRIPT_ENABLED", proxyDetails.get("PROXYSCRIPT_ENABLED"));
                if (addRow) {
                    dObj.addRow(row);
                    persistence.add(dObj);
                }
                else {
                    dObj.updateRow(row);
                    persistence.update(dObj);
                }
            }
            else if (!dObj.isEmpty()) {
                persistence.delete(dObj.getRow("ProxyConfiguration"));
            }
        }
    }
}
