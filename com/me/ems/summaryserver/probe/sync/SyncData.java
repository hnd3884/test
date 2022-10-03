package com.me.ems.summaryserver.probe.sync;

import java.util.HashMap;
import java.util.logging.Level;
import com.me.ems.summaryserver.common.sync.utils.SummarySyncParamsDAOUtil;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.logging.Logger;
import java.io.Serializable;

public class SyncData implements Serializable, Cloneable
{
    public Long syncId;
    public int syncType;
    public Long syncTime;
    public Object syncData;
    public String tableName;
    public String fileName;
    public String fileLocation;
    public int fileType;
    public Long sqlId;
    public Long customerID;
    public String zaaid;
    public boolean priority;
    public boolean isCompressed;
    public Long moduleID;
    public boolean isOnDemandSync;
    public Long probeID;
    public String probeName;
    public boolean isConflictData;
    private static final Logger LOGGER;
    
    public SyncData() {
        this.syncId = null;
        this.syncType = -1;
        this.syncTime = null;
        this.syncData = null;
        this.tableName = null;
        this.fileName = null;
        this.fileLocation = null;
        this.fileType = -1;
        this.sqlId = null;
        this.customerID = null;
        this.zaaid = null;
        this.priority = false;
        this.isCompressed = false;
        this.moduleID = -1L;
        this.isOnDemandSync = false;
        this.probeID = -1L;
        this.probeName = null;
        this.isConflictData = false;
        try {
            final HashMap currentProbeDetails = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeServerDetail();
            if (!currentProbeDetails.isEmpty()) {
                final long probeID = Long.parseLong(String.valueOf(currentProbeDetails.get("PROBE_ID")));
                final String probeName = String.valueOf(currentProbeDetails.get("PROBE_NAME"));
                this.probeID = probeID;
                this.probeName = probeName;
                final SummarySyncParamsDAOUtil summarySyncParamsDAOUtil = new SummarySyncParamsDAOUtil();
                this.isCompressed = Boolean.parseBoolean(summarySyncParamsDAOUtil.getSummarySyncParams("IS_COMPRESSED_FILE_POST"));
            }
        }
        catch (final Exception e) {
            SyncData.LOGGER.log(Level.INFO, "Exception while initializing SyncData with probe details", e);
        }
    }
    
    @Override
    public String toString() {
        return "syncType=" + this.syncType + "&syncTime=" + this.syncTime + "&fileName=" + this.fileName + "&fileType=" + this.fileType + "&priority=" + this.priority + "&isCompressed=" + this.isCompressed + "&moduleID=" + this.moduleID + "&isOnDemandSync=" + this.isOnDemandSync + "&probeID=" + this.probeID + "&probeName=" + this.probeName + "&isConflictData=" + this.isConflictData;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    static {
        LOGGER = Logger.getLogger("ProbeSyncLogger");
    }
}
