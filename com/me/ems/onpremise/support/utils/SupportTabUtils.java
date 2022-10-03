package com.me.ems.onpremise.support.utils;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.UnionQuery;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.db.api.RelationalAPI;
import java.util.LinkedList;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.UnionQueryImpl;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import java.util.Properties;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.adventnet.devicemanagement.silentmigration.SilentUpdation;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.IOException;
import java.io.File;
import java.util.TimeZone;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserHandler;
import java.net.UnknownHostException;
import java.net.InetAddress;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.devicemanagement.onpremise.server.status.SysStatusHandler;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SupportTabUtils
{
    private static Logger logger;
    
    public Map getSupportTabDetails() throws APIException {
        final Map supportTabDetails = new HashMap();
        this.supportPageMessageHandling();
        try {
            supportTabDetails.put("heapInfo", this.loadHeapInfo());
            SupportTabUtils.logger.log(Level.FINEST, "HeapInfo", supportTabDetails.get("heapInfo"));
            supportTabDetails.put("systemInfo", this.loadSystemInfo());
            SupportTabUtils.logger.log(Level.FINEST, "sysInfo", supportTabDetails.get("systemInfo"));
            supportTabDetails.put("serverInfo", this.loadServerInfo());
            supportTabDetails.put("productInfo", this.loadProductInfo());
            supportTabDetails.put("databaseInfo", this.loadDatabaseInfo());
            supportTabDetails.put("lastStatusTime", Utils.getEventTime(new Long(SysStatusHandler.getLastStatusCalTime())));
            supportTabDetails.put("serverName", SysStatusHandler.getServerName());
            final String osname = ApiFactoryProvider.getSupportAPI().getServerOS();
            SupportTabUtils.logger.log(Level.INFO, "osName" + osname);
            supportTabDetails.put("osName", osname);
            supportTabDetails.put("serverPort", String.valueOf(SyMUtil.getWebServerPort()));
            supportTabDetails.put("serverUpTime", Utils.getEventTime(new Long(SysStatusHandler.getServerStartTime())));
            supportTabDetails.put("isDemoMode", ApiFactoryProvider.getDemoUtilAPI().isDemoMode());
            supportTabDetails.put("prodUrl", ProductUrlLoader.getInstance().getValue("prodUrl"));
        }
        catch (final Exception ex) {
            SupportTabUtils.logger.log(Level.WARNING, "Exception while getting support tab info ", ex);
            throw new APIException("GENERIC0005");
        }
        return supportTabDetails;
    }
    
    private Map loadDatabaseInfo() {
        final Map dbServerProperties = DBUtil.getDBServerProperties();
        final Map datadaseInfo = new HashMap();
        datadaseInfo.put("dbName", dbServerProperties.get("db.name").toString());
        datadaseInfo.put("dbHostname", dbServerProperties.get("db.host.name").toString());
        datadaseInfo.put("dbUsername", dbServerProperties.get("db.user.name").toString());
        datadaseInfo.put("dbVersion", dbServerProperties.get("db.version").toString());
        final String dbArch = String.valueOf(dbServerProperties.get("db.arch"));
        if (dbArch != null && !dbArch.isEmpty()) {
            datadaseInfo.put("dbArchitecture", dbArch);
        }
        final String dbEdition = String.valueOf(dbServerProperties.get("db.edition"));
        if (dbEdition != null && !dbEdition.isEmpty() && !dbEdition.equalsIgnoreCase("null")) {
            datadaseInfo.put("dbEdition", dbEdition);
        }
        final String dbInstanceName = String.valueOf(dbServerProperties.get("db.instance.name"));
        if (dbInstanceName != null && !dbInstanceName.isEmpty() && !dbInstanceName.equalsIgnoreCase("null")) {
            datadaseInfo.put("dbInstanceName", dbInstanceName);
        }
        final String dbProductName = String.valueOf(dbServerProperties.get("db.product.name"));
        if (dbProductName != null && !dbProductName.isEmpty() && !dbProductName.equalsIgnoreCase("null")) {
            datadaseInfo.put("dbProductName", dbProductName);
        }
        return datadaseInfo;
    }
    
    private Map loadSystemInfo() {
        final Map systemInfo = new HashMap();
        try {
            final InetAddress add = InetAddress.getLocalHost();
            systemInfo.put("hostName", add.getHostName());
        }
        catch (final UnknownHostException e) {
            SupportTabUtils.logger.log(Level.WARNING, "Exception while getting host name in support tab", e);
        }
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        final String osArchitecture = SyMUtil.getDCOSArchitecture();
        if (osArchitecture != null) {
            systemInfo.put("osArchitecture", osArchitecture);
        }
        systemInfo.put("systemTime", Utils.getTime(Long.valueOf(System.currentTimeMillis()), DMOnPremiseUserHandler.getUserTimeFormat()));
        systemInfo.put("hostedAt", getServerHostedAt());
        systemInfo.put("hostTimezone", TimeZone.getDefault().getDisplayName());
        systemInfo.put("hostTimezoneId", TimeZone.getDefault().getID());
        return systemInfo;
    }
    
    private Map loadHeapInfo() {
        final Map heapInfo = new HashMap();
        final Runtime heap = Runtime.getRuntime();
        long total = heap.totalMemory();
        total /= 1048576L;
        long free = heap.freeMemory();
        free /= 1048576L;
        final long used = total - free;
        heapInfo.put("totalMemory", new Long(total).toString());
        heapInfo.put("usedMemory", new Long(used).toString());
        heapInfo.put("freeMemory", new Long(free).toString());
        return heapInfo;
    }
    
    private Map loadServerInfo() throws IOException {
        final Map serverInfo = new HashMap();
        serverInfo.put("javaVersion", System.getProperty("java.vm.version"));
        serverInfo.put("serverStartTime", Utils.getEventTime(new Long(SysStatusHandler.getServerStartTime())));
        serverInfo.put("port", ApiFactoryProvider.getUtilAccessAPI().getWebServerPort());
        try {
            final File f = new File(".");
            String path = f.getCanonicalPath();
            final File f2 = new File(path);
            path = f2.getParent();
            final int length = path.length();
            String originalName = "";
            if (length > 33) {
                final String[] resultNames = path.split("\\\\");
                String temp = "";
                for (int i = 0; i < resultNames.length; ++i) {
                    if ((temp + resultNames[i]).length() > 33) {
                        originalName = originalName + temp + File.separator;
                        temp = resultNames[i];
                    }
                    else if (temp.equals("")) {
                        temp = resultNames[i];
                    }
                    else {
                        temp = temp + File.separator + resultNames[i];
                    }
                }
                originalName += temp;
            }
            else {
                originalName = path;
            }
            serverInfo.put("workingDirectory", originalName);
        }
        catch (final Exception e) {
            SupportTabUtils.logger.log(Level.WARNING, "Exception while finding working directory in support tab", e);
            throw e;
        }
        SupportTabUtils.logger.log(Level.FINEST, "ServerInfo", serverInfo);
        return serverInfo;
    }
    
    private Map loadProductInfo() throws Exception {
        final Map productInfo = new HashMap();
        productInfo.put("productName", ProductUrlLoader.getInstance().getValue("productname"));
        productInfo.put("productID", ProductUrlLoader.getInstance().getValue("productcode"));
        productInfo.put("productVersion", SyMUtil.getProductProperty("productversion"));
        String dcProductArch = StartupUtil.dcProductArch();
        if (dcProductArch == null) {
            dcProductArch = "--";
        }
        productInfo.put("productArchitecture", dcProductArch);
        productInfo.put("vendor", "ZOHO Corp.");
        productInfo.put("vendorUrl", I18N.getMsg("dc.vendor.website.zohocorp", new Object[0]));
        productInfo.put("buildNumber", SyMUtil.getProductProperty("buildnumber"));
        productInfo.put("agentVersion", SyMUtil.getProductProperty("agentversion"));
        productInfo.put("macAgentVersion", SyMUtil.getProductProperty("macagentversion"));
        productInfo.put("linuxAgentVersion", SyMUtil.getProductProperty("linuxagentversion"));
        if (!CustomerInfoUtil.getInstance().isRAP()) {
            productInfo.put("dsVersion", SyMUtil.getProductProperty("distributionserversion"));
        }
        final String installationDateInLong = SyMUtil.getInstallationProperty("it");
        if (installationDateInLong != null && !installationDateInLong.isEmpty()) {
            final long instDate = new Long(installationDateInLong);
            productInfo.put("buildDate", Utils.getEventTime(Long.valueOf(instDate)));
        }
        else {
            productInfo.put("buildDate", "--");
        }
        return productInfo;
    }
    
    public static String getServerHostedAt() {
        String hostedAt = SyMUtil.getServerParameter("SYSTEM_HW_TYPE");
        if (hostedAt != null) {
            hostedAt = (hostedAt.equalsIgnoreCase("azure_virtual") ? "Azure Virtual Machine" : (hostedAt.equalsIgnoreCase("amazon_virtual") ? "Amazon Virtual Machine" : "On-premise"));
        }
        return hostedAt;
    }
    
    private void supportPageMessageHandling() {
        try {
            final String serverHome = System.getProperty("server.home");
            final String propertyFile = serverHome + File.separator + SilentUpdation.QUICK_FIX_HISTORY;
            if (new File(propertyFile).exists()) {
                final Properties fixHistory = FileAccessUtil.readProperties(propertyFile);
                if (fixHistory.getProperty("ChangesAdapted") != null && fixHistory.getProperty("ChangesAdapted").equalsIgnoreCase("FALSE")) {
                    if (fixHistory.getProperty("LastQuickFixerStatus") != null && fixHistory.getProperty("LastQuickFixerStatus").equalsIgnoreCase("failed")) {
                        MessageProvider.getInstance().unhideMessage("QPM_INSTALL_FAILED");
                    }
                    else {
                        MessageProvider.getInstance().hideMessage("QPM_INSTALL_FAILED");
                        MessageProvider.getInstance().hideMessage("QUICKFIXER_INCOMPATIBLE_FILE");
                    }
                    fixHistory.setProperty("ChangesAdapted", "TRUE");
                    FileAccessUtil.storeProperties(fixHistory, propertyFile, true);
                }
                if (fixHistory.getProperty("DifferentFileMoved") != null && fixHistory.getProperty("DifferentFileMoved").equalsIgnoreCase("TRUE")) {
                    MessageProvider.getInstance().unhideMessage("QUICKFIXER_INCOMPATIBLE_FILE");
                    fixHistory.setProperty("DifferentFileMoved", "FALSE");
                    FileAccessUtil.storeProperties(fixHistory, propertyFile, true);
                }
            }
        }
        catch (final Exception e) {
            SupportTabUtils.logger.log(Level.SEVERE, "Exception occured " + e);
        }
    }
    
    public List getBuildHistoryDetails() throws Exception {
        final SelectQuery buildHistorySelectQuery = (SelectQuery)new SelectQueryImpl(new Table("DCServerBuildHistory"));
        buildHistorySelectQuery.addSelectColumn(Column.getColumn("DCServerBuildHistory", "BUILD_NUMBER"));
        buildHistorySelectQuery.addSelectColumn(Column.getColumn("DCServerBuildHistory", "BUILD_TYPE"));
        buildHistorySelectQuery.addSelectColumn(Column.getColumn("DCServerBuildHistory", "BUILD_DETECTED_AT"));
        buildHistorySelectQuery.addSelectColumn(Column.getColumn("DCServerBuildHistory", "REMARKS"));
        final SelectQuery dmJarBuildHistorySelectQuery = (SelectQuery)new SelectQueryImpl(new Table("DMJarBuildHistory"));
        dmJarBuildHistorySelectQuery.addSelectColumn(Column.getColumn("DMJarBuildHistory", "BUILD_NUMBER"));
        dmJarBuildHistorySelectQuery.addSelectColumn(Column.getColumn("DMJarBuildHistory", "BUILD_TYPE"));
        dmJarBuildHistorySelectQuery.addSelectColumn(Column.getColumn("DMJarBuildHistory", "BUILD_DETECTED_AT"));
        dmJarBuildHistorySelectQuery.addSelectColumn(Column.getColumn("DMJarBuildHistory", "REMARKS"));
        final UnionQuery unionQuery = (UnionQuery)new UnionQueryImpl((Query)buildHistorySelectQuery, (Query)dmJarBuildHistorySelectQuery, false);
        final Column timeCol = Column.getColumn("DCServerBuildHistory", "BUILD_NUMBER");
        final SortColumn sortCol = new SortColumn(timeCol, false);
        unionQuery.addSortColumn(sortCol);
        final List buildHistoryList = new LinkedList();
        final RelationalAPI relationalAPI = RelationalAPI.getInstance();
        Connection connection = null;
        DataSet dataSet = null;
        try {
            connection = relationalAPI.getConnection();
            dataSet = relationalAPI.executeQuery((Query)unionQuery, connection);
            while (dataSet.next()) {
                final Map buildHistoryDetails = new HashMap();
                buildHistoryDetails.put("buildNumber", dataSet.getValue("BUILD_NUMBER"));
                buildHistoryDetails.put("buildDetectedAt", dataSet.getValue("BUILD_DETECTED_AT"));
                buildHistoryDetails.put("buildType", dataSet.getValue("BUILD_TYPE"));
                buildHistoryDetails.put("remarks", dataSet.getValue("REMARKS"));
                this.transformBuildHistoryDetails(buildHistoryDetails);
                buildHistoryList.add(buildHistoryDetails);
            }
        }
        catch (final Exception ex) {
            SupportTabUtils.logger.log(Level.WARNING, "Exception in constructing getBuildHistoryDetails Response");
            throw ex;
        }
        finally {
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex2) {
                SupportTabUtils.logger.log(Level.WARNING, "Exception in closing Data Set & Connection", ex2);
            }
        }
        return buildHistoryList;
    }
    
    private void transformBuildHistoryDetails(final Map buildHistoryDetails) throws Exception {
        String buildType = buildHistoryDetails.get("buildType").toString();
        final String buildNumber = buildHistoryDetails.get("buildNumber").toString();
        String remarks = buildHistoryDetails.get("remarks").toString();
        final String buildDetectedAt = buildHistoryDetails.get("buildDetectedAt").toString();
        switch (Integer.parseInt(buildType)) {
            case 1: {
                buildType = I18N.getMsg("dm.support.transformer.freshinstallation", new Object[0]);
                break;
            }
            case 2: {
                final int buildNum = Integer.valueOf(buildNumber);
                final int version = buildNum % 1000;
                if (version <= 20) {
                    buildType = I18N.getMsg("dm.support.transformer.servicepack", new Object[0]);
                    break;
                }
                buildType = I18N.getMsg("dm.support.transformer.hotfix", new Object[0]);
                break;
            }
            case 3: {
                remarks = remarks.substring(remarks.indexOf("PatchDescription") + 17, remarks.indexOf("type=JarPPM") - 2);
                buildType = I18N.getMsg("dm.support.transformer.quickfix", new Object[0]);
                buildHistoryDetails.put("description", remarks);
                break;
            }
        }
        buildHistoryDetails.put("buildType", buildType);
        if (buildDetectedAt != null && !buildDetectedAt.isEmpty()) {
            buildHistoryDetails.put("buildDetectedAt", Utils.longdateToString(Long.parseLong(buildDetectedAt), DMOnPremiseUserHandler.getUserTimeFormat()));
        }
    }
    
    public String getLogonUserEmailID() {
        String emailID = "";
        try {
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final Properties prop = DMUserHandler.getContactInfoProp(userID);
            if (prop.get("EMAIL_ID") != null) {
                emailID = ((Hashtable<K, String>)prop).get("EMAIL_ID");
                if (emailID.contains("adventnet")) {
                    SupportTabUtils.logger.log(Level.INFO, "In getLogonUserEmailID method, its Adventnet, emailID : " + emailID);
                    emailID = "";
                }
                else if (emailID.contains("zohocorp")) {
                    SupportTabUtils.logger.log(Level.INFO, "In getLogonUserEmailID method, its ZOHO Corp, emailID : " + emailID);
                    emailID = "";
                }
            }
        }
        catch (final Exception ex) {
            SupportTabUtils.logger.log(Level.WARNING, "Exception in setLogonUserEmailID", ex);
        }
        return emailID;
    }
    
    static {
        SupportTabUtils.logger = Logger.getLogger(SupportTabUtils.class.getName());
    }
}
