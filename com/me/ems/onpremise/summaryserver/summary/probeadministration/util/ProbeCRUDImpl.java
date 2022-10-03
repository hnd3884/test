package com.me.ems.onpremise.summaryserver.summary.probeadministration.util;

import com.adventnet.ds.query.SelectQuery;
import com.me.ems.onpremise.summaryserver.summary.proberegistration.ProbeUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.me.ems.summaryserver.listener.ProbeMgmtListenerUtil;
import com.me.ems.summaryserver.listener.ProbeMgmtEvent;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.ems.onpremise.summaryserver.summary.authentication.ProbeAuthUtil;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.api.v1.service.ProbeDetailsService;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import com.me.ems.summaryserver.common.probeadministration.util.ProbeCRUDAPI;

public class ProbeCRUDImpl implements ProbeCRUDAPI
{
    public static Logger logger;
    
    public HashMap addProbe(final Map probeDetail) {
        HashMap addedDetails = new HashMap();
        try {
            final ProbeDetailsService probeDetailsService = new ProbeDetailsService();
            final Map respMap = probeDetailsService.isProbeNameUnique(probeDetail.get("probeName"));
            if (!(boolean)(boolean)respMap.get("isProbeNameUnique")) {
                throw new APIException("PRBE9500304", "Probe Name already exists", new String[0]);
            }
            final Long probeId = this.addProbeDetail(probeDetail);
            addedDetails = ProbeAuthUtil.getInstance().generateProbeInstallationKeyDetails(probeId);
        }
        catch (final Exception e) {
            ProbeCRUDImpl.logger.log(Level.SEVERE, "Exception while adding Probe ", e);
        }
        return addedDetails;
    }
    
    public Long addProbeDetail(final Map probeDetail) {
        Long probeId = null;
        try {
            DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            Row probeDetailRow = new Row("ProbeDetails");
            probeDetailRow.set("PROBE_NAME", probeDetail.get("probeName"));
            probeDetailRow.set("PROBE_DESCRIPTION", probeDetail.get("probeDescription"));
            dataObject.addRow(probeDetailRow);
            dataObject = SyMUtil.getPersistence().add(dataObject);
            probeDetailRow = dataObject.getRow("ProbeDetails");
            probeId = (Long)probeDetailRow.get("PROBE_ID");
            ProbeCRUDImpl.logger.log(Level.INFO, "PROBE ADDED SUCCESSFULLY IN PROBEDETAILS");
            final ProbeMgmtEvent probeMgmtEvent = new ProbeMgmtEvent(probeId);
            ProbeMgmtListenerUtil.getInstance().invokeProbeMgmtListeners(probeMgmtEvent, 1);
            ProbeCRUDImpl.logger.log(Level.INFO, "Probe Mgmt - New Probe Added Event Listeners - Invoked");
        }
        catch (final DataAccessException ex) {
            ProbeCRUDImpl.logger.log(Level.SEVERE, "Exception while adding Probe  Details", (Throwable)ex);
        }
        return probeId;
    }
    
    public HashMap updateProbeDetail(final Map probeDetail) {
        final Long probeId = probeDetail.get("probeId");
        final HashMap statusMap = new HashMap();
        String message = "FAILED";
        boolean isProbeNameUnique = true;
        try {
            final ProbeDetailsService probeDetailsService = new ProbeDetailsService();
            if (!probeDetail.get("probeName").equals(new ProbeDetailsUtil().getProbeName(probeId))) {
                final Map respMap = probeDetailsService.isProbeNameUnique(probeDetail.get("probeName"));
                isProbeNameUnique = (boolean)(boolean)respMap.get("isProbeNameUnique");
            }
            if (isProbeNameUnique) {
                final Criteria criteria = new Criteria(new Column("ProbeDetails", "PROBE_ID"), (Object)probeId, 0);
                final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ProbeDetails"));
                sq.addSelectColumn(new Column("ProbeDetails", "*"));
                sq.setCriteria(criteria);
                final DataObject dataObject = SyMUtil.getPersistence().get(sq);
                if (!dataObject.isEmpty()) {
                    final Row probeRow = dataObject.getRow("ProbeDetails");
                    if (probeRow != null) {
                        probeRow.set("PROBE_NAME", probeDetail.get("probeName"));
                        probeRow.set("PROBE_DESCRIPTION", probeDetail.get("probeDescription"));
                        dataObject.updateRow(probeRow);
                        SyMUtil.getPersistence().update(dataObject);
                        ProbeCRUDImpl.logger.log(Level.INFO, "PROBE DETAILS UPDATED SUCCESSFULLY IN PROBEDETAILS");
                        message = "SUCCESS";
                        statusMap.put("status", message);
                        final HashMap probeDetailsMap = ProbeUtil.getAllProbeDetailsCache().get(probeId);
                        if (probeDetailsMap != null) {
                            probeDetailsMap.put("PROBE_NAME", probeDetail.get("probeName"));
                            ProbeUtil.getAllProbeDetailsCache().put(probeId, probeDetailsMap);
                            final ProbeMgmtEvent probeMgmtEvent = new ProbeMgmtEvent(probeId);
                            ProbeMgmtListenerUtil.getInstance().invokeProbeMgmtListeners(probeMgmtEvent, 3);
                            ProbeCRUDImpl.logger.log(Level.INFO, "Probe Mgmt - Probe Modified Event Listeners - Invoked");
                        }
                        return statusMap;
                    }
                }
                statusMap.put("status", message);
                statusMap.put("errorMsg", "No Such Probe Id");
                return statusMap;
            }
            throw new APIException("PRBE9500304", "Probe Name already exists", new String[0]);
        }
        catch (final DataAccessException | APIException ex) {
            ProbeCRUDImpl.logger.log(Level.SEVERE, "Exception while updating Probe Details", ex);
            statusMap.put("status", message);
            statusMap.put("errorMsg", ex);
            return statusMap;
        }
    }
    
    static {
        ProbeCRUDImpl.logger = Logger.getLogger("probeActionsLogger");
    }
}
