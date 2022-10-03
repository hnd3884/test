package com.me.ems.summaryserver.factory;

import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.alerts.sms.SMSAPI;
import com.me.ems.summaryserver.common.probeadministration.ProbeResourceAPI;
import com.me.ems.summaryserver.common.probeadministration.util.ProbeCRUDAPI;
import com.me.ems.summaryserver.summary.sync.factory.SummaryServerSyncAPI;
import com.me.ems.summaryserver.common.ProbeActionAPI;
import com.me.ems.summaryserver.probe.sync.factory.ProbeSyncAPI;
import com.me.ems.summaryserver.common.probeadministration.ProbeDetailsAPI;
import com.me.ems.summaryserver.summary.probedistribution.ProbeDistributionInitializer;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class ProbeMgmtFactoryProvider extends ApiFactoryProvider
{
    private static final Logger LOGGER;
    private static ProbeDistributionInitializer probeDistributionInitializer;
    private static ProbeDetailsAPI probeDetailsAPI;
    private static ProbeSyncAPI probeSyncAPI;
    private static ProbeActionAPI probeActionAPI;
    private static SummaryServerSyncAPI summaryServerSyncAPI;
    private static ProbeCRUDAPI probeCRUDAPI;
    private static ProbeResourceAPI probeResourceAPI;
    private static SMSAPI smsapi;
    
    public static ProbeDetailsAPI getProbeDetailsAPI() {
        try {
            if (ProbeMgmtFactoryProvider.probeDetailsAPI == null && !SyMUtil.isStandaloneServer()) {
                ProbeMgmtFactoryProvider.probeDetailsAPI = (ProbeDetailsAPI)ApiFactoryProvider.getImplClassInstance("DM_PROBE_MGMT_IMPL_CLASS");
            }
        }
        catch (final Exception e) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "Exception  during Instantiation for ProbeDetails API", e);
        }
        return ProbeMgmtFactoryProvider.probeDetailsAPI;
    }
    
    public static ProbeSyncAPI getProbeSyncAPI() {
        try {
            if (ProbeMgmtFactoryProvider.probeSyncAPI == null && !SyMUtil.isStandaloneServer()) {
                ProbeMgmtFactoryProvider.probeSyncAPI = (ProbeSyncAPI)ApiFactoryProvider.getImplClassInstance("DM_PROBE_SYNC_IMPL_CLASS");
            }
        }
        catch (final Exception e) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "Exception  during Instantiation for ProbeSync API", e);
        }
        return ProbeMgmtFactoryProvider.probeSyncAPI;
    }
    
    public static ProbeActionAPI getProbeActionAPI() {
        try {
            if (ProbeMgmtFactoryProvider.probeActionAPI == null && !SyMUtil.isStandaloneServer()) {
                ProbeMgmtFactoryProvider.probeActionAPI = (ProbeActionAPI)ApiFactoryProvider.getImplClassInstance("DM_PROBE_ACTION_IMPL_CLASS");
            }
        }
        catch (final Exception e) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "Exception  during Instantiation for ProbeAction API", e);
        }
        return ProbeMgmtFactoryProvider.probeActionAPI;
    }
    
    public static SummaryServerSyncAPI getSummaryServerSyncAPI() {
        try {
            if (ProbeMgmtFactoryProvider.summaryServerSyncAPI == null && !SyMUtil.isStandaloneServer()) {
                ProbeMgmtFactoryProvider.summaryServerSyncAPI = (SummaryServerSyncAPI)ApiFactoryProvider.getImplClassInstance("DM_SS_SYNC_IMPL_CLASS");
            }
        }
        catch (final Exception e) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "Exception  during Instantiation for SummaryServerSync API", e);
        }
        return ProbeMgmtFactoryProvider.summaryServerSyncAPI;
    }
    
    public static ProbeDistributionInitializer getProbeDistributionInitializer() {
        try {
            if (ProbeMgmtFactoryProvider.probeDistributionInitializer == null && !SyMUtil.isStandaloneServer()) {
                ProbeMgmtFactoryProvider.probeDistributionInitializer = (ProbeDistributionInitializer)ApiFactoryProvider.getImplClassInstance("DM_PROBE_DISTRIBUTION_IMPL_CLASS");
            }
        }
        catch (final Exception e) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "Exception  during Instantiation for probe distribution initializer API", e);
        }
        return ProbeMgmtFactoryProvider.probeDistributionInitializer;
    }
    
    public static ProbeResourceAPI getProbeResourceAPI() {
        try {
            if (ProbeMgmtFactoryProvider.probeResourceAPI == null && !SyMUtil.isStandaloneServer() && !CustomerInfoUtil.isSAS()) {
                ProbeMgmtFactoryProvider.probeResourceAPI = (ProbeResourceAPI)Class.forName(ProbeMgmtFactoryConstants.opProbeResourceInstance).newInstance();
            }
        }
        catch (final ClassNotFoundException ce) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for probeResourceAPI", ce);
        }
        catch (final InstantiationException ie) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException  during Instantiation for  probeResourceAPI", ie);
        }
        catch (final IllegalAccessException ie2) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException  during Instantiation for probeResourceAPI", ie2);
        }
        catch (final Exception e) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "Exception  during Instantiation for probeResourceAPI", e);
        }
        return ProbeMgmtFactoryProvider.probeResourceAPI;
    }
    
    public static ProbeCRUDAPI getProbeCRUDAPI() {
        try {
            if (ProbeMgmtFactoryProvider.probeCRUDAPI == null) {
                if (SyMUtil.isSummaryServer()) {
                    ProbeMgmtFactoryProvider.probeCRUDAPI = (ProbeCRUDAPI)Class.forName(ProbeMgmtFactoryConstants.ssProbeCRUDClass).newInstance();
                }
                else if (SyMUtil.isProbeServer()) {
                    ProbeMgmtFactoryProvider.probeCRUDAPI = (ProbeCRUDAPI)Class.forName(ProbeMgmtFactoryConstants.probeProbeCRUDClass).newInstance();
                }
            }
        }
        catch (final ClassNotFoundException ce) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for probeCRUDAPI", ce);
        }
        catch (final InstantiationException ie) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException  during Instantiation for probeCRUDAPI", ie);
        }
        catch (final IllegalAccessException ie2) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException  during Instantiation for probeCRUDAPI", ie2);
        }
        catch (final Exception e) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "Exception  during Instantiation for probeCRUDAPI", e);
        }
        return ProbeMgmtFactoryProvider.probeCRUDAPI;
    }
    
    public static SMSAPI getSMSAPI() {
        try {
            if (ProbeMgmtFactoryProvider.smsapi == null && SyMUtil.isSummaryServer()) {
                final String classname = ProductClassLoader.getSingleImplProductClass("DM_SMSSETTINGS_CLASS");
                if (classname != null && classname.trim().length() != 0) {
                    return (SMSAPI)Class.forName(classname).newInstance();
                }
            }
        }
        catch (final ClassNotFoundException ce) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for SMSAPI", ce);
        }
        catch (final InstantiationException ie) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "InstantiationException  during Instantiation for SMSAPI", ie);
        }
        catch (final IllegalAccessException ie2) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException  during Instantiation for SMSAPI", ie2);
        }
        catch (final Exception e) {
            ProbeMgmtFactoryProvider.LOGGER.log(Level.SEVERE, "Exception  during Instantiation for SMSAPI", e);
        }
        return ProbeMgmtFactoryProvider.smsapi;
    }
    
    public static void resetObjects() {
        ProbeMgmtFactoryProvider.probeDistributionInitializer = null;
        ProbeMgmtFactoryProvider.probeDetailsAPI = null;
        ProbeMgmtFactoryProvider.probeSyncAPI = null;
        ProbeMgmtFactoryProvider.probeActionAPI = null;
        ProbeMgmtFactoryProvider.summaryServerSyncAPI = null;
        ProbeMgmtFactoryProvider.probeCRUDAPI = null;
        ProbeMgmtFactoryProvider.probeResourceAPI = null;
        ProbeMgmtFactoryProvider.smsapi = null;
    }
    
    static {
        LOGGER = Logger.getLogger(ProbeMgmtFactoryProvider.class.getName());
        ProbeMgmtFactoryProvider.probeDistributionInitializer = null;
        ProbeMgmtFactoryProvider.probeDetailsAPI = null;
        ProbeMgmtFactoryProvider.probeSyncAPI = null;
        ProbeMgmtFactoryProvider.probeActionAPI = null;
        ProbeMgmtFactoryProvider.summaryServerSyncAPI = null;
        ProbeMgmtFactoryProvider.probeCRUDAPI = null;
        ProbeMgmtFactoryProvider.probeResourceAPI = null;
        ProbeMgmtFactoryProvider.smsapi = null;
    }
}
