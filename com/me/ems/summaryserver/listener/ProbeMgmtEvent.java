package com.me.ems.summaryserver.listener;

import java.util.Properties;

public class ProbeMgmtEvent
{
    public static final int PROBE_ADDED = 1;
    public static final int PROBE_INSTALLED = 2;
    public static final int PROBE_MODIFIED = 3;
    public static final int PROBE_DELETED = 4;
    public static final int PROBE_BACK_TO_LIVE = 5;
    private Long probeID;
    private Long customerID;
    private Properties probeProperties;
    
    public Long getProbeID() {
        return this.probeID;
    }
    
    public void setProbeID(final Long probeID) {
        this.probeID = probeID;
    }
    
    public ProbeMgmtEvent(final Long probeID) {
        this.probeID = null;
        this.customerID = null;
        this.probeProperties = null;
        this.probeID = probeID;
    }
    
    public ProbeMgmtEvent(final Long probeID, final Long customerID) {
        this.probeID = null;
        this.customerID = null;
        this.probeProperties = null;
        this.probeID = probeID;
        this.customerID = customerID;
    }
    
    public ProbeMgmtEvent(final Long probeID, final Long customerID, final Properties probeProperties) {
        this.probeID = null;
        this.customerID = null;
        this.probeProperties = null;
        this.probeID = probeID;
        this.customerID = customerID;
        this.probeProperties = probeProperties;
    }
}
