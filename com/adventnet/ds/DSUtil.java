package com.adventnet.ds;

import com.adventnet.persistence.PersistenceInitializer;
import java.util.Collections;
import com.zoho.cp.ConnectionCreator;
import java.util.Map;
import com.zoho.mickey.cp.ConnectionInfoFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DSUtil
{
    static DataSourcePlugIn dsPlugIn;
    private static final Logger LOGGER;
    
    public static boolean setDSPlugIn(final DataSourcePlugIn newDsPlugIn) {
        if (DSUtil.dsPlugIn == null) {
            DSUtil.dsPlugIn = newDsPlugIn;
            return true;
        }
        DSUtil.LOGGER.log(Level.SEVERE, "Modifying DataSourcePlugIn is not allowed");
        return false;
    }
    
    public static String getDataSourcePlugInImplClass() throws Exception {
        if (DSUtil.dsPlugIn != null) {
            return DSUtil.dsPlugIn.getDataSourcePlugInImplClass();
        }
        return null;
    }
    
    public static void dumpInUseConnections() {
        if (isMwsr()) {
            DSUtil.LOGGER.log(Level.SEVERE, "dumpInUseConnections is not implemented for MWSR.");
        }
        else {
            ConnectionInfoFactory.dumpInUseConnections();
        }
    }
    
    public static int getInUseConnectionCount(final long timeInSeconds) throws Exception {
        if (isMwsr()) {
            DSUtil.LOGGER.log(Level.SEVERE, "getInUseConnectionCount is not implemented for MWSR.");
            return -1;
        }
        return ConnectionInfoFactory.getInUseConnectionCount(timeInSeconds);
    }
    
    public static Map<String, ConnectionCreator.ConnectionInfo> getInUseConnectionInfo(final long timeInSeconds) throws Exception {
        if (isMwsr()) {
            DSUtil.LOGGER.log(Level.SEVERE, "dumpInUseConnections is not implemented for MWSR.");
            return Collections.emptyMap();
        }
        return ConnectionInfoFactory.getInUseConnectionInfo(timeInSeconds);
    }
    
    public static void abortAllConnections(final boolean forceAbort) {
        if (null != DSUtil.dsPlugIn) {
            DSUtil.dsPlugIn.abortAllConnections(true);
        }
    }
    
    private static boolean isMwsr() {
        return PersistenceInitializer.getConfigurationValue("EnableMWSR") != null && PersistenceInitializer.getConfigurationValue("EnableMWSR").equals("true");
    }
    
    static {
        DSUtil.dsPlugIn = null;
        LOGGER = Logger.getLogger(DSUtil.class.getName());
    }
}
