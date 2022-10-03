package com.me.ems.onpremise.summaryserver.probe.probeadministration.api.v1.service;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import java.util.logging.Logger;

public class SummaryServerDetailService
{
    public static Logger logger;
    
    public HashMap getSummaryServerDetails() {
        final HashMap summaryDetail = new HashMap();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SummaryServerInfo"));
            sq.addJoin(new Join(Table.getTable("SummaryServerInfo"), Table.getTable("SummaryServerLiveStatus"), new String[] { "SUMMARY_SERVER_ID" }, new String[] { "SUMMARY_SERVER_ID" }, 2));
            sq.addSelectColumn(new Column("SummaryServerInfo", "*"));
            sq.addSelectColumn(new Column("SummaryServerLiveStatus", "*"));
            final DataObject dObj = SyMUtil.getPersistence().get(sq);
            final String timeFormat = DMUserHandler.getUserDateTimeFormat("TIMEFORMAT", ApiFactoryProvider.getAuthUtilAccessAPI().getUserID());
            if (!dObj.isEmpty()) {
                final Row summaryServerInfo = dObj.getRow("SummaryServerInfo");
                final Row summaryLiveStatusInfo = dObj.getRow("SummaryServerLiveStatus");
                if (summaryLiveStatusInfo != null && summaryServerInfo != null) {
                    summaryDetail.put("host", summaryServerInfo.get("HOST"));
                    summaryDetail.put("port", summaryServerInfo.get("PORT").toString());
                    summaryDetail.put("protocol", summaryServerInfo.get("PROTOCOL"));
                    summaryDetail.put("ipaddress", summaryServerInfo.get("IPADDRESS"));
                    summaryDetail.put("version", summaryServerInfo.get("BUILD_NUMBER"));
                    summaryDetail.put("timeZone", SyMUtil.getFormattedTimeZone((String)summaryServerInfo.get("TIMEZONE")));
                    summaryDetail.put("serverStatus", summaryLiveStatusInfo.get("STATUS"));
                    summaryDetail.put("lastContactedTime", DateTimeUtil.longdateToString((long)summaryLiveStatusInfo.get("LAST_CONTACTED_TIME"), timeFormat));
                    summaryDetail.put("summaryServerAuthKey", ProbeMgmtFactoryProvider.getProbeDetailsAPI().getSummaryServerAPIKeyDetails().get("summaryServerAuthKey"));
                    if (ProbeDetailsUtil.getProbeAuthKey() != null) {
                        summaryDetail.put("probeAuthKey", ProbeDetailsUtil.getProbeAuthKey());
                    }
                    else {
                        summaryDetail.put("probeAuthKey", ProbeDetailsUtil.getProbeAuthKeyFromDB());
                    }
                }
            }
        }
        catch (final Exception e) {
            SummaryServerDetailService.logger.log(Level.SEVERE, "Exception while getting summary server details", e);
        }
        return summaryDetail;
    }
    
    public HashMap getSummaryServerUrl() {
        final HashMap summaryServerUrlInfo = new HashMap();
        summaryServerUrlInfo.put("summaryServerUrl", ProbeMgmtFactoryProvider.getProbeDetailsAPI().getSummaryServerBaseURL());
        return summaryServerUrlInfo;
    }
    
    static {
        SummaryServerDetailService.logger = Logger.getLogger("probeActionsLogger");
    }
}
