package com.me.mdm.onpremise.server.admin;

import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.SomTrackingParameters;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class DomainUtil
{
    private static DomainUtil domainUtil;
    private static String sourceClass;
    private static final Logger LOGGER;
    static final String SOM_TRACKING_SUMMARY = "SoMSummary";
    
    private DomainUtil() {
    }
    
    public static synchronized DomainUtil getInstance() {
        if (DomainUtil.domainUtil == null) {
            DomainUtil.domainUtil = new DomainUtil();
        }
        return DomainUtil.domainUtil;
    }
    
    public static void writeSoMPropsInFile() throws SyMException, Exception {
        final Thread somPropsThread = new Thread("somPropsThread") {
            @Override
            public void run() {
                final String sourceMethod = "writeSoMPropsInFile";
                try {
                    SyMLogger.info(DomainUtil.LOGGER, DomainUtil.sourceClass, sourceMethod, "start of writeSoMPropsInFile ");
                    final Properties props = new Properties();
                    final String som = DomainUtil.getDomainPropertyForTracking();
                    props.setProperty("som", som);
                    SyMUtil.writeInstallProps(props);
                    SyMLogger.info(DomainUtil.LOGGER, DomainUtil.sourceClass, sourceMethod, "Had written the Domain Properties in install.conf");
                    SyMUtil.updateSyMParameter("SoMSummary", som);
                }
                catch (final Exception ex) {
                    SyMLogger.error(DomainUtil.LOGGER, DomainUtil.sourceClass, sourceMethod, "Caught exception while populating som properties in install.conf. ", (Throwable)ex);
                }
            }
        };
        somPropsThread.start();
    }
    
    public static String getDomainPropertyForTracking() throws SyMException, Exception {
        final String sourceMethod = "getDomainPropertyForTracking";
        final SomTrackingParameters somTrackingParams = new SomTrackingParameters();
        final boolean isSAS = CustomerInfoUtil.isSAS;
        if (!isSAS && WinAccessProvider.getInstance().getEnvironment(somTrackingParams)) {
            SyMLogger.info(DomainUtil.LOGGER, DomainUtil.sourceClass, sourceMethod, "Current Environment :" + somTrackingParams.environment);
            final String currentDomainName = WinAccessProvider.getInstance().getCurrentNetBIOSName();
            SyMLogger.info(DomainUtil.LOGGER, DomainUtil.sourceClass, sourceMethod, "currentDomainName :" + currentDomainName);
            if (somTrackingParams.environment == 3) {
                SyMLogger.info(DomainUtil.LOGGER, DomainUtil.sourceClass, sourceMethod, "Product is installed in AD environment");
                WinAccessProvider.getInstance().getADComputerCount(somTrackingParams);
                SyMLogger.info(DomainUtil.LOGGER, DomainUtil.sourceClass, sourceMethod, "adCompCount :" + somTrackingParams.adComputerCount);
            }
            else if (somTrackingParams.environment == 1) {
                SyMLogger.info(DomainUtil.LOGGER, DomainUtil.sourceClass, sourceMethod, "Product is installed in Not Joined environment");
            }
            else if (somTrackingParams.environment == 0) {
                SyMLogger.info(DomainUtil.LOGGER, DomainUtil.sourceClass, sourceMethod, "Product is installed in unknown environment");
            }
        }
        String errCode = "";
        if (!somTrackingParams.envErrorStatus.equals("0")) {
            errCode = "env" + String.valueOf(somTrackingParams.envErrorStatus);
        }
        if (!somTrackingParams.adCompCountErrorStatus.equals("0")) {
            errCode = errCode + "adc" + somTrackingParams.adCompCountErrorStatus;
        }
        final Integer resourceType = new Integer(5);
        final Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)resourceType, 0);
        final int allDomainsCount = DBUtil.getRecordCount("Resource", "RESOURCE_ID", criteria);
        final String metrId = METrackerUtil.getMEDCTrackId();
        String som = null;
        if (!isSAS) {
            som = "env-" + somTrackingParams.getEnvironmentIdString() + "|adc-" + somTrackingParams.adComputerCount + "|dd-" + allDomainsCount + "|metrId-" + metrId;
            if (!errCode.isEmpty()) {
                som = som + "|sErr-" + errCode;
            }
        }
        else {
            som = "|dd-" + allDomainsCount;
        }
        SyMLogger.info(DomainUtil.LOGGER, DomainUtil.sourceClass, sourceMethod, "end of getSoMPropertyForTracking ");
        return som;
    }
    
    static {
        DomainUtil.domainUtil = null;
        DomainUtil.sourceClass = "DomainUtil";
        LOGGER = Logger.getLogger(DomainUtil.class.getName());
    }
}
