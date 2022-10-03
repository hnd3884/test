package com.me.ems.onpremise.summaryserver.common.probeadministration.api.v1.service;

import com.adventnet.ds.query.DeleteQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.ems.summaryserver.listener.ProbeMgmtListenerUtil;
import com.me.ems.summaryserver.listener.ProbeMgmtEvent;
import com.me.ems.onpremise.summaryserver.summary.authentication.ProbeAuthUtil;
import com.me.ems.onpremise.summaryserver.summary.proberegistration.ProbeUtil;
import com.me.ems.summaryserver.common.probeadministration.util.ProbeCRUDAPI;
import java.util.logging.Level;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ProbeCRUDService
{
    public static Logger logger;
    
    public HashMap addProbe(final Map probeDetail) {
        HashMap probeAddedDetails = new HashMap();
        try {
            final ProbeCRUDAPI probeCRUDAPI = ProbeMgmtFactoryProvider.getProbeCRUDAPI();
            probeAddedDetails = probeCRUDAPI.addProbe(probeDetail);
        }
        catch (final Exception ex) {
            ProbeCRUDService.logger.log(Level.SEVERE, "Exception while adding Probe Details", ex);
        }
        return probeAddedDetails;
    }
    
    public HashMap updateProbeDetail(final Map probeDetail) {
        HashMap probeUpdatedDetails = new HashMap();
        try {
            final ProbeCRUDAPI probeCRUDAPI = ProbeMgmtFactoryProvider.getProbeCRUDAPI();
            probeUpdatedDetails = probeCRUDAPI.updateProbeDetail(probeDetail);
        }
        catch (final Exception ex) {
            ProbeCRUDService.logger.log(Level.SEVERE, "Exception while updating Probe Details", ex);
        }
        return probeUpdatedDetails;
    }
    
    public HashMap deleteProbe(final Long probeId) {
        final HashMap statusMap = new HashMap();
        String message = "FAILED";
        try {
            this.deleteProbeTable("ProbeDetails", "PROBE_ID", probeId);
            ProbeUtil.getAllProbeDetailsCache().remove(probeId);
            ProbeAuthUtil.getInstance();
            ProbeAuthUtil.getProbeKeysCache().remove(probeId);
            message = "SUCCESS";
            statusMap.put("status", message);
            final ProbeMgmtEvent probeMgmtEvent = new ProbeMgmtEvent(probeId);
            ProbeMgmtListenerUtil.getInstance().invokeProbeMgmtListeners(probeMgmtEvent, 4);
            ProbeCRUDService.logger.log(Level.INFO, "Probe Mgmt - Probe Deleted Event Listeners - Invoked");
            return statusMap;
        }
        catch (final Exception e) {
            statusMap.put("status", message);
            statusMap.put("errorMsg", e);
            ProbeCRUDService.logger.log(Level.SEVERE, "Exception while deleting  Probe Details", e);
            return statusMap;
        }
    }
    
    private void deleteProbeTable(final String tableName, final String columnName, final Long probeId) {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
            final Criteria criteria = new Criteria(new Column(tableName, columnName), (Object)probeId, 0);
            deleteQuery.setCriteria(criteria);
            SyMUtil.getPersistence().delete(deleteQuery);
            ProbeCRUDService.logger.log(Level.INFO, "PROBE " + probeId + " DELETED SUCCESSFULLY FROM " + tableName);
        }
        catch (final Exception ex) {
            ProbeCRUDService.logger.log(Level.SEVERE, "Exception while deleting " + tableName + " Probe Details", ex);
        }
    }
    
    static {
        ProbeCRUDService.logger = Logger.getLogger("probeActionsLogger");
    }
}
