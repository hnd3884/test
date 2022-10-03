package com.me.ems.onpremise.summaryserver.summary.probeadministration.util;

import com.adventnet.ds.query.UpdateQuery;
import com.me.ems.onpremise.summaryserver.common.probeadministration.util.ProbeSMSHandler;
import com.me.ems.onpremise.summaryserver.common.probeadministration.util.ProbeMailHandler;
import java.util.logging.Level;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.util.ProbeNotificationUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.ems.summaryserver.common.util.ProbePropertyUtil;
import java.util.Iterator;
import java.util.HashMap;
import com.me.ems.onpremise.summaryserver.summary.proberegistration.ProbeUtil;
import java.util.logging.Logger;

public class SSDetailsUpdateUtil
{
    public static Logger logger;
    
    public static void updateSSDetailsFromProbeProperty() {
        final HashMap probeDetails = ProbeUtil.getInstance().getAllProbeDetails();
        for (final Long probeId : probeDetails.keySet()) {
            updateProbeSpaceDetailsFromProbeProperty(probeId, "FREE_SPACE");
            updateProbeSpaceDetailsFromProbeProperty(probeId, "TOTAL_SPACE");
            updateProbeIpFromProbeProperty(probeId);
        }
    }
    
    private static void updateProbeSpaceDetailsFromProbeProperty(final Long probeId, final String param) {
        try {
            final String value = ProbePropertyUtil.getProbeProperty(param, probeId);
            if (value == null) {
                return;
            }
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ProbeDetailsExtn");
            updateQuery.setCriteria(new Criteria(Column.getColumn("ProbeDetailsExtn", "PROBE_ID"), (Object)probeId, 0));
            updateQuery.setUpdateColumn(param, (Object)Float.parseFloat(value));
            SyMUtil.getPersistence().update(updateQuery);
            final Long bytes = (long)(Float.valueOf(value) * 1024.0f * 1024.0f * 1024.0f);
            if (param.equalsIgnoreCase("FREE_SPACE") && ProbeDetailsUtil.isDiskSpaceLow(bytes)) {
                final boolean ismailServerConfigured = ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured();
                if (ismailServerConfigured && ProbeNotificationUtil.isMailEnabledForProbe(probeId)) {
                    SSDetailsUpdateUtil.logger.log(Level.INFO, "EMAIL TRIGGERING STARTED FOR DISK SPACE LOW FOR PROBE -> " + probeId);
                    ProbeMailHandler.sendMailForProbeEvent(probeId, "diskspace");
                }
                ProbeSMSHandler.triggerSMSForProbeEvent(probeId, "diskspace");
            }
        }
        catch (final Exception e) {
            SSDetailsUpdateUtil.logger.log(Level.SEVERE, "Exception while updating probe space details into db from probe properties", e);
        }
    }
    
    private static void updateProbeIpFromProbeProperty(final Long probeId) {
        try {
            final String ipAddress = ProbePropertyUtil.getProbeProperty("IPADDRESS", probeId);
            if (ipAddress == null) {
                return;
            }
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ProbeServerInfo");
            updateQuery.setCriteria(new Criteria(Column.getColumn("ProbeServerInfo", "PROBE_ID"), (Object)probeId, 0));
            updateQuery.setUpdateColumn("IPADDRESS", (Object)ipAddress);
            SyMUtil.getPersistence().update(updateQuery);
        }
        catch (final Exception e) {
            SSDetailsUpdateUtil.logger.log(Level.SEVERE, "Exception while updating probe ip details into db from probe properties", e);
        }
    }
    
    static {
        SSDetailsUpdateUtil.logger = Logger.getLogger("probeActionsLogger");
    }
}
