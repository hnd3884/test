package com.me.ems.summaryserver.factory;

public class ProbeMgmtFactoryConstants
{
    public static final String DM_PROBE_MGMT_IMPL_CLASS = "DM_PROBE_MGMT_IMPL_CLASS";
    public static final String DM_PROBE_DISTRIBUTION_IMPL_CLASS = "DM_PROBE_DISTRIBUTION_IMPL_CLASS";
    public static final String DM_PROBE_ACTION_IMPL_CLASS = "DM_PROBE_ACTION_IMPL_CLASS";
    public static final String DM_PROBE_SYNC_IMPL_CLASS = "DM_PROBE_SYNC_IMPL_CLASS";
    public static final String DM_SS_SYNC_IMPL_CLASS = "DM_SS_SYNC_IMPL_CLASS";
    public static String opProbeResourceInstance;
    public static String ssProbeCRUDClass;
    public static String probeProbeCRUDClass;
    public static final String DM_SMSSETTINGS_CLASS = "DM_SMSSETTINGS_CLASS";
    
    static {
        ProbeMgmtFactoryConstants.opProbeResourceInstance = "com.me.dc.som.summaryserver.summary.core.ProbeResourceDetailsImpl";
        ProbeMgmtFactoryConstants.ssProbeCRUDClass = "com.me.ems.onpremise.summaryserver.summary.probeadministration.util.ProbeCRUDImpl";
        ProbeMgmtFactoryConstants.probeProbeCRUDClass = "com.me.ems.onpremise.summaryserver.probe.probeadministration.util.ProbeCRUDImpl";
    }
}
