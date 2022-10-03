package com.adventnet.sym.server.admin;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.onpremise.winaccess.WinAccessProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.util.SomTrackingParameters;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.text.SimpleDateFormat;
import java.util.Properties;
import com.adventnet.sym.server.util.SyMUtil;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.util.SoMADUtil;

public class SoMUtil extends SoMADUtil
{
    private static SoMUtil sUtil;
    private static Logger mstCreationLogger;
    private static Logger somLogger;
    private static String sourceClass;
    static final String SOM_TRACKING_SUMMARY = "SoMSummary";
    
    private SoMUtil() {
    }
    
    public static synchronized SoMUtil getInstance() {
        if (SoMUtil.sUtil == null) {
            SoMUtil.sUtil = new SoMUtil();
        }
        return SoMUtil.sUtil;
    }
    
    public void updateDCServerAgentInfo() throws Exception {
        SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo S T A R T #############");
        final String serverName = InetAddress.getLocalHost().getHostName();
        String hostAddress = "--";
        try {
            final InetAddress machine = InetAddress.getByName(serverName);
            hostAddress = machine.getHostAddress();
            SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo IP Address detected now :{0}", hostAddress);
        }
        catch (final Exception exp) {
            SoMUtil.somLogger.log(Level.INFO, "Exception occured while getting the server ipaddress in updateDCServerAgentInfo", exp);
        }
        final String detectedIP = SoMHandler.getInstance().getDCServerIPDetected();
        SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo detected IP in DB:{0}", detectedIP);
        final String serverIP = this.getServerIP();
        SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo Server IP in DB:{0}", serverIP);
        final String serverFQDN = InetAddress.getLocalHost().getCanonicalHostName();
        final Integer portNumber = new Integer(SyMUtil.getWebServerPort());
        final String agentVersion = SyMUtil.getProductProperty("agentversion");
        final String macagentVersion = SyMUtil.getProductProperty("macagentversion");
        final String linuxagentVersion = SyMUtil.getProductProperty("linuxagentversion");
        SoMUtil.mstCreationLogger.log(Level.INFO, "Server Name :{0}", serverName);
        SoMUtil.mstCreationLogger.log(Level.INFO, "Server Port :{0}", portNumber);
        SoMUtil.mstCreationLogger.log(Level.INFO, "Agent Version :{0}", agentVersion);
        SoMUtil.mstCreationLogger.log(Level.INFO, "IP Address :{0}", hostAddress);
        SoMUtil.mstCreationLogger.log(Level.INFO, "serverFQDN :{0}", serverFQDN);
        final Properties props = new Properties();
        ((Hashtable<String, String>)props).put("SERVER_MAC_NAME", serverName);
        ((Hashtable<String, Integer>)props).put("SERVER_PORT", portNumber);
        ((Hashtable<String, String>)props).put("SERVER_FQDN", serverFQDN);
        ((Hashtable<String, String>)props).put("AGENT_VERSION", agentVersion);
        ((Hashtable<String, String>)props).put("MAC_AGENT_VERSION", macagentVersion);
        ((Hashtable<String, String>)props).put("LINUX_AGENT_VERSION", linuxagentVersion);
        ((Hashtable<String, Integer>)props).put("HTTPS_PORT", SyMUtil.getSSLPort());
        ((Hashtable<String, String>)props).put("OS_NAME", SyMUtil.getServerOS());
        final Properties serverInfo = SyMUtil.getDCServerInfo();
        Long serverInstanceID = null;
        Long server_hash_id = null;
        if (serverInfo != null) {
            serverInstanceID = ((Hashtable<K, Long>)serverInfo).get("SERVER_INSTANCE_ID");
            server_hash_id = ((Hashtable<K, Long>)serverInfo).get("SERVER_HASH_ID");
        }
        if (serverInstanceID == null || serverInstanceID == -1L) {
            final String installationdateInLong = SyMUtil.getInstallationProperty("it");
            long installDate = -1L;
            if (installationdateInLong != null && !installationdateInLong.equals("")) {
                installDate = Long.parseLong(installationdateInLong);
            }
            else {
                installDate = System.currentTimeMillis();
            }
            ((Hashtable<String, Long>)props).put("SERVER_INSTANCE_ID", new Long(installDate / 1000L));
            final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
            final String installDateInReadableFormat = dateFormat.format(installDate);
            SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo SERVER_INSTANCE_ID is :{0} Going to set long value : {1} String value : {2}", new Object[] { serverInstanceID, installDate, installDateInReadableFormat });
        }
        final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
        final String mesuiPath = serverHome + File.separator + "lib" + File.separator + "mesui.dat";
        if (server_hash_id == null || server_hash_id == -1L) {
            server_hash_id = new Long(System.currentTimeMillis() / 1000L);
            ((Hashtable<String, Long>)props).put("SERVER_HASH_ID", server_hash_id);
            SoMUtil.somLogger.log(Level.INFO, "Creating mesui file..");
            ApiFactoryProvider.getFileAccessAPI().writeFile(mesuiPath, String.valueOf(server_hash_id).getBytes());
            SoMUtil.somLogger.log(Level.INFO, "Exit creating mesui file..");
        }
        else if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(mesuiPath)) {
            SoMUtil.somLogger.log(Level.SEVERE, "{0} file doesn''t exist, hence creating the file with agent server comm id {1}", new Object[] { mesuiPath, server_hash_id });
            ApiFactoryProvider.getFileAccessAPI().writeFile(mesuiPath, String.valueOf(server_hash_id).getBytes());
        }
        boolean ipMatch = false;
        try {
            final InetAddress[] inetAddr = InetAddress.getAllByName(serverName);
            SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo inetAddress arr length :{0}", inetAddr.length);
            for (int i = 0; i < inetAddr.length; ++i) {
                SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo inetAddr [{0}] :{1}", new Object[] { i, inetAddr[i] });
                final String ipAddr = inetAddr[i].getHostAddress();
                SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo ipAddr :{0}", ipAddr);
                if (serverIP != null && ipAddr.equals(serverIP)) {
                    ipMatch = true;
                }
            }
        }
        catch (final Exception exp2) {
            SoMUtil.mstCreationLogger.log(Level.WARNING, "Exception occured while checking the server ipaddress ", exp2);
        }
        if (!ipMatch) {
            final boolean autoSave = true;
            if (autoSave) {
                if (serverInfo != null) {
                    final String secIPAddr = ((Hashtable<K, String>)serverInfo).get("SERVER_SEC_IPADDR");
                    SoMUtil.mstCreationLogger.log(Level.INFO, "SECONDARY IP ADDRESS from DB :: {0}", secIPAddr);
                    SoMUtil.mstCreationLogger.log(Level.INFO, "PREVIOUS Primary IP ADDRESS :: {0}", serverIP);
                    if (serverIP != null && serverIP.equals(secIPAddr)) {
                        SoMUtil.mstCreationLogger.log(Level.INFO, "Secondary IP is same as previous primary IP. Hence, updating secondary IP with the new IP in DCServerInfo table");
                        ((Hashtable<String, String>)props).put("SERVER_SEC_IPADDR", hostAddress);
                    }
                }
                ((Hashtable<String, String>)props).put("SERVER_IPADDR", hostAddress);
                SyMUtil.addOrUpdateDCServerInfo(props);
                SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo DCServerInfo is updated!!!");
                if (detectedIP == null) {
                    SoMHandler.getInstance().addOrUpdateDCServerIPDetected(hostAddress);
                    SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo temp table for IP - DCServerIPDetected - updated!");
                }
            }
            else {
                SyMUtil.addOrUpdateDCServerInfo(props);
                SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo DCServerInfo is updated!!!");
                SoMHandler.getInstance().addOrUpdateDCServerIPDetected(hostAddress);
                SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo temp table for IP - DCServerIPDetected - updated ");
            }
        }
        else {
            ((Hashtable<String, String>)props).put("SERVER_IPADDR", hostAddress);
            SoMUtil.mstCreationLogger.log(Level.INFO, "################ Server Ip address {0} is added to DCServerAgentInfo", hostAddress);
            SyMUtil.addOrUpdateDCServerInfo(props);
            SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo DCServerInfo is updated!!!");
            SoMHandler.getInstance().addOrUpdateDCServerIPDetected(hostAddress);
            SoMUtil.mstCreationLogger.log(Level.INFO, "################ updateDCServerAgentInfo temp table for IP - DCServerIPDetected - updated ");
        }
    }
    
    public String getServerIP() throws SyMException, DataAccessException, Exception {
        final Table serverInfo = Table.getTable("DCServerInfo");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(serverInfo);
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        final DataObject dobj = SyMUtil.getPersistence().get(query);
        SoMUtil.somLogger.log(Level.FINE, "DataObject retrieved : {0}", dobj);
        if (!dobj.isEmpty()) {
            final Row row = dobj.getRow("DCServerInfo");
            final String strServerIP = (String)row.get("SERVER_MAC_IPADDR");
            return strServerIP;
        }
        return null;
    }
    
    public Criteria getScopeCriteria() {
        final Criteria cri = new Criteria(Column.getColumn("ManagedComputer", "MANAGED_STATUS"), (Object)new Integer(61), 0);
        return cri;
    }
    
    public static String getSoMPropertyForTracking() throws SyMException, Exception {
        final String sourceMethod = "getSoMPropertyForTracking";
        final SomTrackingParameters somTrackingParams = new SomTrackingParameters();
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "start of getSoMPropertyForTracking");
        final int totalComputersCount = 0;
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "Total SoM Computers : " + totalComputersCount);
        final int managedComputersCount = 0;
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "No. of managed Computers : " + managedComputersCount);
        final int managedDomainsCount = getInstance().getManagedDomainsCount(null);
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "No. of managed Domains :" + managedDomainsCount);
        final int adCount = getInstance().getADManagedDomainsCount();
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "No. of managed AD Domains :" + adCount);
        final int wgCount = 0;
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "No. of managed workgroups :" + wgCount);
        final boolean isSAS = CustomerInfoUtil.isSAS;
        if (!isSAS && WinAccessProvider.getInstance().getEnvironment(somTrackingParams)) {
            SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "Current Environment :" + somTrackingParams.environment);
            final String currentDomainName = WinAccessProvider.getInstance().getCurrentNetBIOSName();
            SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "currentDomainName :" + currentDomainName);
            if (somTrackingParams.environment == 3) {
                SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "Product is installed in AD environment");
            }
            else if (somTrackingParams.environment == 2) {
                SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "Product is installed in WG environment");
                somTrackingParams.wgComputerCount = 0;
                SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "wgCompCount :" + somTrackingParams.wgComputerCount);
            }
            else if (somTrackingParams.environment == 1) {
                SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "Product is installed in Not Joined environment");
            }
            else if (somTrackingParams.environment == 0) {
                SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "Product is installed in unknown environment");
            }
        }
        String errCode = "";
        if (!somTrackingParams.envErrorStatus.equals("0")) {
            errCode = "env" + String.valueOf(somTrackingParams.envErrorStatus);
        }
        if (!somTrackingParams.adCompCountErrorStatus.equals("0")) {
            errCode = errCode + "adc" + somTrackingParams.adCompCountErrorStatus;
        }
        if (!somTrackingParams.wgCompCountErrorStatus.equals("0")) {
            errCode = errCode + "wgc" + somTrackingParams.wgCompCountErrorStatus;
        }
        final Integer resourceType = new Integer(5);
        final Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)resourceType, 0);
        final int allDomainsCount = DBUtil.getRecordCount("Resource", "RESOURCE_ID", criteria);
        final int installed = 0;
        final int installFailed = 0;
        final String metrId = METrackerUtil.getMEDCTrackId();
        String som = null;
        if (!isSAS) {
            som = "env-" + somTrackingParams.getEnvironmentIdString() + "|adc-" + somTrackingParams.adComputerCount + "|wgc-" + somTrackingParams.wgComputerCount + "|dd-" + allDomainsCount + "|mad-" + adCount + "|mwg-" + wgCount + "|mc-" + managedComputersCount + "|tsc-" + totalComputersCount + "|ic-" + installed + "|ifc-" + installFailed + "|metrId-" + metrId;
            if (!errCode.isEmpty()) {
                som = som + "|sErr-" + errCode;
            }
        }
        else {
            som = "|dd-" + allDomainsCount + "|mad-" + adCount + "|mwg-" + wgCount + "|mc-" + managedComputersCount + "|ic-" + installed + "|ifc-" + installFailed;
        }
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "end of getSoMPropertyForTracking ");
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "Summary of Agent Install Status Update -- BEGIN");
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "      Total Systems Count in Scope : " + totalComputersCount);
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "      Managed Computer Count : " + managedComputersCount);
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "        Agent Yet To Install Count : 0");
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "  Agent Installation Success Count : " + installed);
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "  Agent Installation Failure Count : " + installFailed);
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "Agent UnInstallation Success Count : 0");
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "Agent UnInstallation Failure Count : 0");
        SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "Summary of Agent Install Status Update -- END");
        return som;
    }
    
    public int getADManagedDomainsCount() throws SyMException {
        final Criteria criteria = new Criteria(Column.getColumn("ManagedDomain", "IS_AD_DOMAIN"), (Object)Boolean.TRUE, 0);
        return this.getManagedDomainsCount(criteria);
    }
    
    public int getWGManagedDomainsCount() throws SyMException {
        final Criteria criteria = new Criteria(Column.getColumn("ManagedDomain", "IS_AD_DOMAIN"), (Object)Boolean.FALSE, 0);
        return this.getManagedDomainsCount(criteria);
    }
    
    public int getManagedDomainsCount(final Criteria criteria) throws SyMException {
        int recordCount = 0;
        final String baseTblName = "Resource";
        final Table baseTable = Table.getTable(baseTblName);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
        query.addJoin(new Join(baseTblName, "ManagedDomain", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        Criteria managedStatusCrit = new Criteria(Column.getColumn("ManagedDomain", "HAS_MANAGED_COMPUTERS"), (Object)Boolean.TRUE, 0);
        if (criteria != null) {
            managedStatusCrit = managedStatusCrit.and(criteria);
        }
        query.setCriteria(managedStatusCrit);
        Column selCol = new Column("Resource", "RESOURCE_ID");
        selCol = selCol.count();
        query.addSelectColumn(selCol);
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                if (value != null) {
                    recordCount = (int)value;
                }
            }
            ds.close();
        }
        catch (final QueryConstructionException ex) {
            SoMUtil.somLogger.log(Level.SEVERE, "Caught exception while retrieving managed domains count : ", (Throwable)ex);
            throw new SyMException(1001, "Exception occured while retrieving managed domains count", (Throwable)ex);
        }
        catch (final SQLException ex2) {
            SoMUtil.somLogger.log(Level.SEVERE, "Caught exception while retrieving managed domains count : ", ex2);
            throw new SyMException(1001, "Exception occured while retrieving managed domains count", (Throwable)ex2);
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final SQLException ex3) {
                throw new SyMException(1001, "Exception occured while getting managed domains count", (Throwable)ex3);
            }
        }
        return recordCount;
    }
    
    public String getSoMTrackingSummary() {
        String somSummary = null;
        try {
            somSummary = SyMUtil.getSyMParameter("SoMSummary");
        }
        catch (final Exception ex) {
            SoMUtil.somLogger.log(Level.SEVERE, "Caught exception while retrieving SoM tracking summary from DB.", ex);
        }
        return somSummary;
    }
    
    public static void writeSoMPropsInFile() throws SyMException, Exception {
        final Thread somPropsThread = new Thread("somPropsThread") {
            @Override
            public void run() {
                final String sourceMethod = "writeSoMPropsInFile";
                try {
                    SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "start of writeSoMPropsInFile ");
                    final Properties props = new Properties();
                    final String som = SoMUtil.getSoMPropertyForTracking();
                    props.setProperty("som", som);
                    SyMUtil.writeInstallProps(props);
                    SyMLogger.info(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "Had written the SoM Properties in install.conf");
                    SyMUtil.updateSyMParameter("SoMSummary", som);
                }
                catch (final Exception ex) {
                    SyMLogger.error(SoMUtil.somLogger, SoMUtil.sourceClass, sourceMethod, "Caught exception while populating som properties in install.conf. ", (Throwable)ex);
                }
            }
        };
        somPropsThread.start();
    }
    
    public List getManagedDomainNames(final Criteria criteria) throws SyMException {
        final List domainList = new ArrayList();
        DataObject resultDO = null;
        try {
            resultDO = this.getManagedDomainDO(criteria);
            if (!resultDO.isEmpty()) {
                final Iterator resRows = resultDO.getRows("Resource");
                while (resRows.hasNext()) {
                    final Properties props = new Properties();
                    final Row resRow = resRows.next();
                    ((Hashtable<String, Object>)props).put("RESOURCE_ID", resRow.get("RESOURCE_ID"));
                    ((Hashtable<String, Object>)props).put("DOMAIN_NETBIOS_NAME", resRow.get("DOMAIN_NETBIOS_NAME"));
                    domainList.add(props);
                }
            }
        }
        catch (final Exception ex) {
            SoMUtil.somLogger.log(Level.SEVERE, "Caught exception while retrieving managed domain names from DB: ", ex);
            throw new SyMException(1002, ex.getMessage(), ex.fillInStackTrace());
        }
        return domainList;
    }
    
    public DataObject getManagedDomainDO(final Criteria criteria) throws SyMException {
        return this.getManagedDomainsDO(criteria);
    }
    
    static {
        SoMUtil.sUtil = null;
        SoMUtil.mstCreationLogger = Logger.getLogger("MSTCreationLogger");
        SoMUtil.somLogger = Logger.getLogger("SoMLogger");
        SoMUtil.sourceClass = "SoMUtil";
    }
}
