package com.adventnet.sym.server.fos;

import java.util.Hashtable;
import com.adventnet.tools.update.installer.ConsoleOut;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.zoho.framework.utils.crypto.EnDecrypt;
import com.adventnet.persistence.DataAccessException;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import java.io.FileOutputStream;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.security.CommonCryptoImpl;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.sym.webclient.admin.fos.FosTrialLicense;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.Map;
import java.io.RandomAccessFile;
import com.zoho.framework.utils.OSCheckUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.fos.FOS;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.onpremise.start.DCConsoleOut;
import com.adventnet.persistence.PersistenceInitializer;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.net.NetworkInterface;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class FailoverServerUtil
{
    private static Logger logger;
    public static Properties failoverUserProps;
    public static Properties failoverProps;
    public static final String REPLICATION_USE_CREDENTIAL = "repl.usecredentials";
    public static final String REPLICATION_SHARENAME = "repl.sharename";
    public static final String REPLICATION_USERNAME = "repl.username";
    public static final String REPLICATION_PASSWORD = "repl.password";
    public static final String REMOTE_INSTALLATION_DIRECTORY = "repl.remoteinstallationDir";
    
    public static List getStaticIPs() {
        final List<String> staticIPs = new ArrayList<String>();
        final ArrayList<String> hostAddresses = getHostAddresses();
        for (final String ip : hostAddresses) {
            if (isStaticIP(ip)) {
                staticIPs.add(ip);
            }
        }
        return staticIPs;
    }
    
    public static ArrayList getHostAddresses() {
        final ArrayList<String> hostAddresses = new ArrayList<String>();
        Enumeration<NetworkInterface> adapters = null;
        try {
            adapters = NetworkInterface.getNetworkInterfaces();
        }
        catch (final SocketException exp) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Cannot get Network Interface details:{0}", exp);
        }
        while (adapters != null && adapters.hasMoreElements()) {
            final NetworkInterface adapter = adapters.nextElement();
            final Enumeration<InetAddress> addrList = adapter.getInetAddresses();
            while (addrList.hasMoreElements()) {
                final InetAddress inetAddress = addrList.nextElement();
                final String ip = inetAddress.getHostAddress();
                hostAddresses.add(ip);
            }
        }
        return hostAddresses;
    }
    
    public static boolean isFosConfigured() {
        final String secondaryIP = FailoverServerUtil.failoverUserProps.getProperty("SecondaryServerIP");
        return secondaryIP != null && !"".equals(secondaryIP);
    }
    
    public static boolean isSecondaryStatic() {
        final String secondaryIP = FailoverServerUtil.failoverUserProps.getProperty("SecondaryServerIP");
        return isStaticIP(secondaryIP);
    }
    
    public static boolean isStaticIP(final String ip) {
        try {
            final Process process = new ProcessBuilder(new String[] { "CheckLocalIP.exe", ip }).start();
            final BufferedReader commandOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s = null;
            while ((s = commandOutput.readLine()) != null) {
                FailoverServerUtil.logger.log(Level.FINE, "Output{0}", s);
            }
            process.waitFor();
            final int exitCode = process.exitValue();
            if (exitCode == 0) {
                return true;
            }
        }
        catch (final IOException ex) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Exception while checking static IP {0}", ex);
        }
        catch (final InterruptedException ex2) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Interrupted Exception while checking static IP {0}", ex2);
        }
        return false;
    }
    
    public static long getFreeDiskSpace() {
        final String serverHome = System.getProperty("server.home");
        final File serverFile = new File(serverHome);
        return serverFile.getFreeSpace();
    }
    
    public static boolean isDiskSpaceEnough(final long freeDiskSpace) {
        final long finalDiskSpaceNeeded = 3221225472L;
        return freeDiskSpace >= finalDiskSpaceNeeded;
    }
    
    public static boolean isTimeDiff() {
        final String ip = FailoverServerUtil.failoverUserProps.getProperty("PrimaryServerIP");
        return isTimeDiff(ip, 10);
    }
    
    public static boolean isTimeDiff(final String ip, final int min) {
        try {
            final Process process = new ProcessBuilder(new String[] { "dcwinutil.exe", "-timediff", ip }).start();
            final BufferedReader commandOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s = null;
            while ((s = commandOutput.readLine()) != null) {
                FailoverServerUtil.logger.log(Level.FINE, "Output{0}", s);
            }
            process.waitFor();
            FailoverServerUtil.logger.log(Level.SEVERE, "Process exit value{0}", process.exitValue());
            final int exitCode = process.exitValue();
            if (exitCode / 60 > 10) {
                return true;
            }
        }
        catch (final IOException ex) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Exception while checking time difference {0}", ex);
        }
        catch (final InterruptedException ex2) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Interrupted Exception while checking time difference {0}", ex2);
        }
        return false;
    }
    
    public static void startDB() {
        try {
            PersistenceInitializer.initialize(System.getProperty("server.home") + File.separator + "conf");
        }
        catch (final Exception ex) {
            DCConsoleOut.println("Database cannot be initialized");
            FailoverServerUtil.logger.log(Level.SEVERE, "Exception in initializing persistence", ex);
            System.exit(1);
        }
    }
    
    public static void stopDB() {
        try {
            PersistenceInitializer.stopDB();
        }
        catch (final Exception ex) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Exception while stoppping database", ex);
        }
    }
    
    public static boolean isPrimaryAlive() {
        Boolean isAlive = Boolean.FALSE;
        try {
            startDB();
            final Criteria c = new Criteria(new Column("FOSNodeDetails", "STATUS"), (Object)"serving", 0);
            final DataObject d = DataAccess.get("FOSNodeDetails", c);
            if (!d.isEmpty()) {
                isAlive = Boolean.TRUE;
            }
            stopDB();
        }
        catch (final Exception ex) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Exception in checking primary status", ex);
        }
        return isAlive;
    }
    
    public static boolean isStaticIPMatches(final String ipAddress) {
        final List<String> staticIPs = getStaticIPs();
        return staticIPs.contains(ipAddress);
    }
    
    public static boolean isOtherServerInFosReachable() {
        Boolean isServerReachable = Boolean.FALSE;
        try {
            final FOS fos = new FOS();
            fos.initialize();
            final String peerIP = fos.getOtherNode();
            if (peerIP != null) {
                checkAndExecuteNetusePrefixCommand(peerIP);
                final String remoteInstallationDir = FailoverServerUtil.failoverProps.getProperty("repl.remoteinstallationDir", SyMUtil.getInstallationDirName());
                final String location = "\\\\" + peerIP + "\\" + remoteInstallationDir;
                isServerReachable = isOtherServerReachable(location);
                checkAndExecuteNetuseSuffixCommand(peerIP);
            }
        }
        catch (final Exception ex) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Exception while checking peer node reachability..", ex);
        }
        return isServerReachable;
    }
    
    public static void checkAndExecuteNetusePrefixCommand(final String peerIP) throws Exception {
        final Boolean useCredentials = Boolean.valueOf(FailoverServerUtil.failoverProps.getProperty("repl.usecredentials", "false"));
        String netusePrefix = null;
        if (useCredentials) {
            final String shareName = FailoverServerUtil.failoverProps.getProperty("repl.sharename");
            final String userName = FailoverServerUtil.failoverProps.getProperty("repl.username");
            final String password = FailoverServerUtil.failoverProps.getProperty("repl.password");
            netusePrefix = "net use \\\\" + peerIP + "\\" + shareName + " /user:" + userName + " " + password;
            executeNetUseCommand(netusePrefix);
        }
    }
    
    public static void checkAndExecuteNetuseSuffixCommand(final String peerIP) throws Exception {
        final Boolean useCredentials = Boolean.valueOf(FailoverServerUtil.failoverProps.getProperty("repl.usecredentials", "false"));
        String netuseSuffix = null;
        if (useCredentials) {
            final String shareName = FailoverServerUtil.failoverProps.getProperty("repl.sharename");
            netuseSuffix = "net use \\\\" + peerIP + "\\" + shareName + " /d";
            executeNetUseCommand(netuseSuffix);
        }
    }
    
    public static void executeNetUseCommand(final String command) throws Exception {
        Process p = null;
        try {
            final boolean isWindows = OSCheckUtil.isWindows(OSCheckUtil.getOS());
            if (isWindows) {
                final List<String> commandList = new ArrayList<String>();
                commandList.add("cmd");
                commandList.add("/c");
                commandList.add(command);
                FailoverServerUtil.logger.log(Level.INFO, "Command to be executed {0}", new Object[] { commandList });
                final File dirPath = new File(System.getProperty("server.home") + File.separator + "bin/");
                p = executeCommand(commandList, null, null);
                dump(p, FailoverServerUtil.logger);
                p.waitFor();
                final int exitValue = p.exitValue();
                FailoverServerUtil.logger.log(Level.INFO, "Net use Process completed with exit code: [{0}]", new Object[] { exitValue });
            }
            else {
                FailoverServerUtil.logger.info("FOS in not supported in Linux yet");
            }
        }
        catch (final Exception e) {
            throw e;
        }
        finally {
            p.destroy();
        }
    }
    
    public static boolean isOtherServerReachable(final String location) {
        boolean retVal = false;
        try {
            final File sharePathLocation = new File(location);
            final boolean direxists = sharePathLocation.isDirectory();
            FailoverServerUtil.logger.log(Level.INFO, "Share path Location {0} exists ? :: {1}", new Object[] { sharePathLocation, direxists });
            retVal = sharePathLocation.canWrite();
            FailoverServerUtil.logger.log(Level.INFO, "Is the Share has Write Access ? :: {0}", retVal);
        }
        catch (final Exception ex) {
            FailoverServerUtil.logger.log(Level.WARNING, "Caught exception while creating share access conf", ex);
        }
        return retVal;
    }
    
    public static void dump(final Process p, final Logger logger) {
        final ProcessWriter pw = new ProcessWriter(new BufferedReader(new InputStreamReader(p.getErrorStream())), logger);
        final ProcessWriter pw2 = new ProcessWriter(new BufferedReader(new InputStreamReader(p.getInputStream())), logger);
        pw.start();
        pw2.start();
    }
    
    public static Process executeCommand(final List<String> commandList, final Properties envProps, final File directoryPath) throws IOException {
        return executeCommand(commandList, envProps, directoryPath, false, true);
    }
    
    public static Process executeCommand(final List<String> commandList, final Properties envProps, final File directoryPath, final boolean writeToFile, final boolean executeCmd) throws IOException {
        final boolean isWindows = OSCheckUtil.isWindows(OSCheckUtil.getOS());
        if (!writeToFile || isWindows) {
            FailoverServerUtil.logger.log(Level.INFO, "Command to be executed ::: {0}", commandList);
            final ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            if (directoryPath != null) {
                processBuilder.directory(directoryPath);
            }
            setEnvProps(processBuilder, envProps);
            return processBuilder.start();
        }
        final File extFile = new File(new File(System.getProperty("server.home")).getAbsolutePath() + File.separator + "ext.sh");
        FailoverServerUtil.logger.log(Level.INFO, "Writing comman to ext.sh file ::: {0}", commandList);
        final RandomAccessFile f = new RandomAccessFile(extFile.getAbsolutePath(), "rw");
        if (extFile.length() != 0L) {
            f.seek(extFile.length());
            f.write(System.getProperty("line.separator").getBytes());
        }
        for (final String cmd : commandList) {
            f.write(cmd.toString().getBytes());
            f.write(" ".getBytes());
        }
        f.close();
        if (executeCmd) {
            FailoverServerUtil.logger.info("Executing all commands in ext.sh ");
            final List<String> extCmdList = new ArrayList<String>();
            extCmdList.add("sh");
            extCmdList.add(extFile.getAbsolutePath());
            FailoverServerUtil.logger.log(Level.INFO, "Command to be executed ::: {0}", extCmdList);
            final ProcessBuilder processBuilder2 = new ProcessBuilder(extCmdList);
            processBuilder2.directory(directoryPath);
            setEnvProps(processBuilder2, envProps);
            return processBuilder2.start();
        }
        return null;
    }
    
    public static void setEnvProps(final ProcessBuilder processBuilder, final Properties envVariables) {
        if (envVariables != null) {
            final Map<String, String> environment = processBuilder.environment();
            final Enumeration<Object> keys = ((Hashtable<Object, V>)envVariables).keys();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                environment.put(key, envVariables.getProperty(key));
            }
        }
    }
    
    public static String getDBName() throws Exception {
        String serverName = null;
        final String fname = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        final Properties dbProps = FileAccessUtil.readProperties(fname);
        if (dbProps != null) {
            final String URL = dbProps.getProperty("url");
            StringTokenizer stk = new StringTokenizer(URL, "//", false);
            stk.nextToken();
            String tok = stk.nextToken();
            stk = new StringTokenizer(tok, ";", false);
            tok = stk.nextToken();
            String hostName = null;
            if (tok.indexOf(":") < 0) {
                hostName = tok;
            }
            else {
                final StringTokenizer stk2 = new StringTokenizer(tok, ":", false);
                hostName = stk2.nextToken();
            }
            serverName = hostName;
        }
        FailoverServerUtil.logger.log(Level.INFO, "Database Name from database_params.conf: {0}", serverName);
        return serverName;
    }
    
    public static boolean hasRemoteDB() {
        boolean isRemoteDB = false;
        try {
            final String dbHostName = getDBName();
            if (dbHostName != null && !dbHostName.equalsIgnoreCase("localhost") && !dbHostName.equalsIgnoreCase("127.0.0.1") && !dbHostName.equalsIgnoreCase(InetAddress.getLocalHost().getHostName()) && !dbHostName.equalsIgnoreCase(InetAddress.getLocalHost().getHostAddress())) {
                isRemoteDB = true;
            }
        }
        catch (final UnknownHostException e) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Exception while checking remote database {0}", e);
        }
        catch (final Exception e2) {
            e2.printStackTrace();
        }
        return isRemoteDB;
    }
    
    public static boolean isDBInSameNetwork() {
        final Boolean isDBInSameNetwork = Boolean.TRUE;
        return isDBInSameNetwork;
    }
    
    public static String getMask(final InetAddress serverAddress) throws SocketException {
        final NetworkInterface networkInterface = NetworkInterface.getByInetAddress(serverAddress);
        final int prflen = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
        final int shft = -1 << 32 - prflen;
        final int oct1 = (byte)((shft & 0xFF000000) >> 24) & 0xFF;
        final int oct2 = (byte)((shft & 0xFF0000) >> 16) & 0xFF;
        final int oct3 = (byte)((shft & 0xFF00) >> 8) & 0xFF;
        final int oct4 = (byte)(shft & 0xFF) & 0xFF;
        final String submask = oct1 + "." + oct2 + "." + oct3 + "." + oct4;
        return submask;
    }
    
    public static boolean sameNetwork(final InetAddress ip1, final InetAddress ip2, final String mask) throws Exception {
        final byte[] a1 = ip1.getAddress();
        final byte[] a2 = ip2.getAddress();
        final byte[] m = InetAddress.getByName(mask).getAddress();
        for (int i = 0; i < a1.length; ++i) {
            if ((a1[i] & m[i]) != (a2[i] & m[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static void UpdateFOSLicenseMsgDetails() {
        FailoverServerUtil.logger.log(Level.INFO, "Inside MDMPFosUtil:addOrUpdateFOSMsgDetails()");
        final Boolean isFosEnabled = LicenseProvider.getInstance().isFosEnabled();
        String isFosTrialed = null;
        long fosTrialPeriod = -1L;
        try {
            final DataObject serverParams = getServerParamsDO("isFosTrialed");
            if (!serverParams.isEmpty()) {
                final Row fosRow = serverParams.getFirstRow("ServerParams");
                isFosTrialed = (String)fosRow.get("PARAM_VALUE");
            }
            if ("true".equalsIgnoreCase(isFosTrialed)) {
                fosTrialPeriod = FosTrialLicense.getFosTrialExpiryPeriod();
            }
            if (isFosEnabled || ("true".equalsIgnoreCase(isFosTrialed) && fosTrialPeriod > 0L)) {
                MessageProvider.getInstance().hideMessage("FOS_NOT_PURCHASED");
            }
            else {
                MessageProvider.getInstance().unhideMessage("FOS_NOT_PURCHASED");
            }
        }
        catch (final Exception ex) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Error in UpdateFOSLicenseMsgDetails", ex);
        }
    }
    
    public static void UpdateFOSDetails() {
        final Boolean hasRemoteDB = hasRemoteDB();
        final Boolean isDBInSameNetwork = isDBInSameNetwork();
        final Boolean isMailSettingsConfigured = ApiFactoryProvider.getMailSettingAPI().isMailServerConfigured();
        if (FosTrialLicense.isFosTrialFlagEnabled() && FosTrialLicense.getFosTrialExpiryPeriod() <= 0L) {
            cleanUpFosEntries();
        }
        if (hasRemoteDB && isDBInSameNetwork) {
            MessageProvider.getInstance().hideMessage("REMOTE_DB_NOT_CONFIGURED");
        }
        else {
            MessageProvider.getInstance().unhideMessage("REMOTE_DB_NOT_CONFIGURED");
        }
        if (getStaticIPs().isEmpty()) {
            MessageProvider.getInstance().unhideMessage("STATIC_IP_NOT_CONFIGURED");
        }
        else {
            MessageProvider.getInstance().hideMessage("STATIC_IP_NOT_CONFIGURED");
        }
        if (isMailSettingsConfigured) {
            MessageProvider.getInstance().hideMessage("MAIL_SERVER_NOT_CONFIGURED");
        }
        else {
            MessageProvider.getInstance().unhideMessage("MAIL_SERVER_NOT_CONFIGURED");
        }
        UpdateFOSLicenseMsgDetails();
    }
    
    public static Boolean getFosTrialLicense() {
        Boolean isFosTrialed = Boolean.FALSE;
        try {
            final DataObject serverParamsDO = getServerParamsDO("isFosTrialed");
            final String confFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
            if (new File(confFile).exists()) {
                final Properties fosProps = FileAccessUtil.readProperties(confFile);
                if (fosProps != null && fosProps.getProperty("FosTrialPeriod") != null && fosProps.getProperty("isFosTrialed") != null) {
                    final CommonCryptoImpl crypt = new CommonCryptoImpl();
                    isFosTrialed = Boolean.parseBoolean(crypt.decrypt(fosProps.getProperty("isFosTrialed")));
                }
            }
            if (serverParamsDO.isEmpty() && !isFosTrialed) {
                final Row rowIsFosTrialed = new Row("ServerParams");
                rowIsFosTrialed.set(2, (Object)"isFosTrialed");
                rowIsFosTrialed.set(3, (Object)"true");
                serverParamsDO.addRow(rowIsFosTrialed);
                com.me.devicemanagement.onpremise.server.util.SyMUtil.getPersistence().update(serverParamsDO);
                if (FosTrialLicense.getFosTrial()) {
                    final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
                    final String remarks = I18N.getMsg("dc.admin.fos.trail.optevent.msg", new Object[0]);
                    DCEventLogUtil.getInstance().addEvent(7003, userName, (HashMap)null, remarks, (Object)null, true);
                    MessageProvider.getInstance().unhideMessage("FOS_NOT_PURCHASED");
                }
                else {
                    FailoverServerUtil.logger.log(Level.INFO, "Trial cannot be enabled");
                }
            }
        }
        catch (final Exception exc) {
            Logger.getLogger(FailoverServerUtil.class.getName()).log(Level.WARNING, "Error in enabling FOS trail", exc);
        }
        return !isFosTrialed;
    }
    
    public static DataObject getServerParamsDO(final String paramName) throws Exception {
        final Column col = Column.getColumn("ServerParams", "PARAM_NAME");
        final Criteria crit = new Criteria(col, (Object)paramName, 2, false);
        final DataObject serverParamsDObj = com.me.devicemanagement.onpremise.server.util.SyMUtil.getPersistence().get("ServerParams", crit);
        return serverParamsDObj;
    }
    
    public static JSONObject getSlaveDetails(final JSONObject fosResponseJSON) {
        try {
            final Boolean isSlavePresent = Boolean.TRUE;
            Boolean replicationFailed = Boolean.FALSE;
            final FOS fos = new FOS();
            fos.initialize();
            final String currentIP = fos.getFOSConfig().ipaddr();
            final String otherIP = fos.getOtherNode();
            if (!isSlavePresent) {
                fosResponseJSON.put("isSlavePresent", (Object)isSlavePresent.toString());
            }
            fosResponseJSON.put("isSlavePresent", (Object)isSlavePresent.toString());
            final DataObject masterDO = DBUtil.getDataObjectFromDB("FosHealthDetails", "SERVER_IP", (Object)currentIP);
            final DataObject slaveDO = DBUtil.getDataObjectFromDB("FosHealthDetails", "SERVER_IP", (Object)otherIP);
            if (masterDO == null || slaveDO == null) {
                fosResponseJSON.put("isDataObjectNull", (Object)"true");
                return null;
            }
            fosResponseJSON.put("isSlaveReachable", masterDO.getFirstValue("FosHealthDetails", "IS_PEER_REACHABLE"));
            fosResponseJSON.put("isMasterReachable", slaveDO.getFirstValue("FosHealthDetails", "IS_PEER_REACHABLE"));
            fosResponseJSON.put("isMasterDiskFree", masterDO.getFirstValue("FosHealthDetails", "IS_DISKSPACE_ENOUGH"));
            fosResponseJSON.put("isSlaveDiskFree", slaveDO.getFirstValue("FosHealthDetails", "IS_DISKSPACE_ENOUGH"));
            fosResponseJSON.put("isMasterIPStatic", masterDO.getFirstValue("FosHealthDetails", "IS_STATIC"));
            fosResponseJSON.put("isSlaveIPStatic", slaveDO.getFirstValue("FosHealthDetails", "IS_STATIC"));
            fosResponseJSON.put("timeDiffExists", slaveDO.getFirstValue("FosHealthDetails", "IS_TIME_SAME"));
            final String serverHome = System.getProperty("server.home");
            final File serverFile = new File(serverHome);
            final double freeSpace = serverFile.getFreeSpace() / 1.073741824E9;
            final double totalSpace = serverFile.getTotalSpace() / 1.073741824E9;
            fosResponseJSON.put("MasterDiskSpace", Math.floor(freeSpace * 100.0) / 100.0);
            fosResponseJSON.put("MasterTotalSpace", Math.floor(totalSpace * 100.0) / 100.0);
            fosResponseJSON.put("MasterDiskSpacePercent", 100.0 - freeSpace / totalSpace * 100.0);
            final Long slaveDiskSpace = (Long)slaveDO.getFirstValue("FosHealthDetails", "DISKSPACE");
            final Long slaveTotalSpace = (Long)slaveDO.getFirstValue("FosHealthDetails", "TOTALDISKSPACE");
            final double slaveDiskSpaceInGB = slaveDiskSpace / 1.073741824E9;
            final double slaveTotalSpaceInGB = slaveTotalSpace / 1.073741824E9;
            fosResponseJSON.put("SlaveDiskSpace", Math.floor(slaveDiskSpaceInGB * 100.0) / 100.0);
            fosResponseJSON.put("SlaveTotalSpace", Math.floor(slaveTotalSpaceInGB * 100.0) / 100.0);
            fosResponseJSON.put("SlaveDiskSpacePercent", 100.0 - slaveDiskSpaceInGB / slaveTotalSpaceInGB * 100.0);
            final Long value = (Long)DBUtil.getValueFromDB("FosReplicationErrorCodes", "ERROR_CODE", (Object)"16", "LAST_ERROR_TIME");
            String date = null;
            if (value != null) {
                final Long TimeFromLastFailure = (System.currentTimeMillis() - value) / 60000L;
                if (TimeFromLastFailure < 5L) {
                    replicationFailed = Boolean.TRUE;
                    date = DateTimeUtil.longdateToString((long)value);
                }
            }
            fosResponseJSON.put("lastReplicationFailedAt", (Object)date);
            fosResponseJSON.put("isReplicationFailed", (Object)replicationFailed.toString());
        }
        catch (final Exception ex) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Error in getSlaveDetails", ex);
        }
        return fosResponseJSON;
    }
    
    public static Boolean isSlaveConfigured() {
        Boolean isSlaveConfigured = Boolean.TRUE;
        try {
            final FOS fos = new FOS();
            fos.initialize();
            final String otherIP = fos.getOtherNode();
            if (otherIP == null) {
                isSlaveConfigured = Boolean.FALSE;
            }
        }
        catch (final Exception ex) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Error in isSlaveConfigured", ex);
        }
        return isSlaveConfigured;
    }
    
    public static JSONObject getInputConfigurations(final JSONObject inputConfigurationJson) {
        try {
            final String fosUserConf = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
            final Properties fosProps = FileAccessUtil.readProperties(fosUserConf);
            final String isFosEnabled = fosProps.getProperty("EnableFos");
            if (isFosEnabled.equalsIgnoreCase("true")) {
                inputConfigurationJson.put("is_input_configured", (Object)Boolean.TRUE);
                inputConfigurationJson.put("email", (Object)fosProps.getProperty("EMAIL_ID"));
                inputConfigurationJson.put("primary_ip", (Object)fosProps.getProperty("PrimaryServerIP"));
                inputConfigurationJson.put("secondary_ip", (Object)fosProps.getProperty("SecondaryServerIP"));
                inputConfigurationJson.put("public_ip", (Object)fosProps.getProperty("PublicIP"));
            }
            else {
                inputConfigurationJson.put("is_input_configured", (Object)Boolean.FALSE);
            }
        }
        catch (final Exception ex) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Error in getInputConfigurations", ex);
        }
        return inputConfigurationJson;
    }
    
    public static Boolean saveSecondaryServer(final Map fosProperties) {
        Boolean result = Boolean.FALSE;
        String userName = "";
        String remarksText = "dc.admin.fos.enabled_success";
        try {
            userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            writeInstallationPath(com.me.devicemanagement.onpremise.server.util.SyMUtil.getInstallationDir());
            generateFosConf(fosProperties);
            updateEmailAlertInfo(fosProperties);
            FailoverServerUtil.logger.log(Level.INFO, "xxxxxxxxxxxxx FOS SERVER SETTINGS CONFIGURED!!  xxxxxxxxxxxxxxxxx");
            remarksText = I18N.getMsg(remarksText, new Object[] { fosProperties.get("PrimaryServerIP"), fosProperties.get("SecondaryServerIP"), fosProperties.get("PublicIP"), fosProperties.get("EMAIL_ID") });
            DCEventLogUtil.getInstance().addEvent(7001, userName, (HashMap)null, remarksText, (Object)null, true);
            result = Boolean.TRUE;
        }
        catch (final Exception e) {
            FailoverServerUtil.logger.log(Level.INFO, "Exception while saving fos server settings :: ", e);
            result = Boolean.FALSE;
        }
        return result;
    }
    
    public static void writeInstallationPath(final String serverHome) {
        try {
            final String fname = serverHome + File.separator + "conf" + File.separator + "fos_installation_path.conf";
            final StringBuffer sb = new StringBuffer(1000);
            sb.append("\ninstallation.dir=" + serverHome);
            writeFile(fname, sb.toString().getBytes());
        }
        catch (final IOException ex) {
            FailoverServerUtil.logger.log(Level.WARNING, "Exception occurred while writin fos_installation_path file", ex);
        }
    }
    
    public static void writeFile(final String fileName, final byte[] content) throws IOException {
        FileOutputStream fos = null;
        try {
            final File fname = new File(fileName).getParentFile();
            if (!fname.exists()) {
                fname.mkdirs();
            }
            fos = new FileOutputStream(fileName);
            fos.write(content);
        }
        catch (final IOException e) {
            FailoverServerUtil.logger.log(Level.WARNING, "Exception occurred while writing file", e);
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final Exception e2) {
                    FailoverServerUtil.logger.log(Level.WARNING, "Exception occurred while closing file output stream", e2);
                }
            }
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final Exception e3) {
                    FailoverServerUtil.logger.log(Level.WARNING, "Exception occurred while closing file output stream", e3);
                }
            }
        }
    }
    
    private static void generateFosConf(final Map modRewriteProps) throws Exception {
        final String fosConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
        final Properties fosTrialProps = FileAccessUtil.readProperties(fosConfFile);
        final String currentTime = com.me.devicemanagement.onpremise.server.util.SyMUtil.getCurrentTimeWithDate();
        final String comments = "Generated at " + currentTime;
        fosTrialProps.setProperty("EnableFos", "true");
        fosTrialProps.setProperty("SecondaryServerIP", modRewriteProps.get("SecondaryServerIP"));
        fosTrialProps.setProperty("PrimaryServerIP", modRewriteProps.get("PrimaryServerIP"));
        fosTrialProps.setProperty("PublicIP", modRewriteProps.get("PublicIP"));
        fosTrialProps.setProperty("EMAIL_ID", modRewriteProps.get("EMAIL_ID"));
        FileAccessUtil.storeProperties(fosTrialProps, fosConfFile, (boolean)Boolean.TRUE);
        FailoverServerUtil.logger.log(Level.INFO, "{0} file generated.", fosConfFile);
    }
    
    private static void updateEmailAlertInfo(final Map dynaForm) throws Exception {
        final String email = dynaForm.get("EMAIL_ID");
        com.me.devicemanagement.onpremise.server.util.SyMUtil.addOrUpdateEmailAddr("FailOver", (boolean)Boolean.TRUE, email);
    }
    
    public static void cleanUpFosEntries() {
        try {
            final EnDecrypt ed = (EnDecrypt)new EnDecryptAES256Impl();
            CryptoUtil.setEnDecryptInstance(ed);
            DataAccess.delete("FosParams", (Criteria)null);
            DataAccess.delete("FOSNodeDetails", (Criteria)null);
            DataAccess.delete("FosHealthDetails", (Criteria)null);
            final String fosUserConf = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
            final Properties userProps = new Properties();
            final Properties fosProps = FileAccessUtil.readProperties(fosUserConf);
            final String fosTrialValidity = fosProps.getProperty("FosTrialValidity");
            userProps.setProperty("EnableFos", "false");
            if (fosTrialValidity != null) {
                userProps.setProperty("FosTrialValidity", fosTrialValidity);
            }
            final String isFosTrialed = fosProps.getProperty("isFosTrialed");
            if (isFosTrialed != null) {
                userProps.setProperty("isFosTrialed", isFosTrialed);
            }
            else {
                userProps.setProperty("isFosTrialed", CryptoUtil.encrypt("false"));
            }
            final String fosTrialPeriod = fosProps.getProperty("FosTrialPeriod");
            if (fosTrialPeriod != null) {
                userProps.setProperty("FosTrialPeriod", fosTrialPeriod);
            }
            else {
                userProps.setProperty("FosTrialPeriod", CryptoUtil.encrypt("30"));
            }
            FileAccessUtil.storeProperties(userProps, fosUserConf, false);
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(FailoverServerUtil.class.getName()).log(Level.SEVERE, "Exception while deleting fos details", (Throwable)ex);
        }
        catch (final Exception ex2) {
            Logger.getLogger(FailoverServerUtil.class.getName()).log(Level.SEVERE, null, ex2);
        }
    }
    
    public static Boolean updateFOS(final JSONObject fosJson) {
        Boolean result = Boolean.FALSE;
        try {
            final String fosUserConf = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
            final Properties fosProps = FileAccessUtil.readProperties(fosUserConf);
            final String email = fosJson.optString("email");
            final Boolean revertFOS = fosJson.optBoolean("revertFOS");
            if (email != null) {
                addOrUpdateEmailAddr("FailOver", true, email);
                fosProps.setProperty("EMAIL_ID", email);
                FileAccessUtil.storeProperties(fosProps, fosUserConf, false);
                result = Boolean.TRUE;
            }
            if (revertFOS) {
                fosProps.setProperty("EnableFos", "false");
            }
        }
        catch (final Exception ex) {
            FailoverServerUtil.logger.log(Level.WARNING, "Exception occured : \n", ex);
        }
        return result;
    }
    
    public static synchronized void addOrUpdateEmailAddr(final String module, final boolean emailStatus, final String emailAddress) throws Exception {
        final String sourceMethod = "addOrUpdateEmailAddr";
        try {
            final DataObject mailDObj = getEmailAddDO(module);
            if (!mailDObj.isEmpty()) {
                final Row row = mailDObj.getRow("EMailAddr");
                row.set("SEND_MAIL", (Object)emailStatus);
                row.set("EMAIL_ADDR", (Object)emailAddress);
                mailDObj.updateRow(row);
            }
            else {
                final Row row = new Row("EMailAddr");
                row.set("SEND_MAIL", (Object)emailStatus);
                row.set("MODULE", (Object)module);
                row.set("EMAIL_ADDR", (Object)emailAddress);
                mailDObj.addRow(row);
            }
            com.me.devicemanagement.onpremise.server.util.SyMUtil.getPersistence().update(mailDObj);
        }
        catch (final Exception ex) {
            FailoverServerUtil.logger.log(Level.WARNING, "Exception occured : \n", ex);
        }
    }
    
    public static DataObject getEmailAddDO(final String module) throws Exception {
        final Column col = Column.getColumn("EMailAddr", "MODULE");
        final Criteria crit = new Criteria(col, (Object)module, 0, false);
        final DataObject mailDObj = com.me.devicemanagement.onpremise.server.util.SyMUtil.getPersistence().get("EMailAddr", crit);
        return mailDObj;
    }
    
    static {
        FailoverServerUtil.logger = Logger.getLogger(FailoverServerUtil.class.getName());
        FailoverServerUtil.failoverUserProps = new Properties();
        FailoverServerUtil.failoverProps = new Properties();
        try {
            final String fosPropsFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
            FailoverServerUtil.failoverUserProps = FileAccessUtil.readProperties(fosPropsFilePath);
            final String fosFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos.conf";
            FailoverServerUtil.failoverProps = FileAccessUtil.readProperties(fosFilePath);
            WebServerUtil.getWebServerSettings();
        }
        catch (final Exception ex) {
            FailoverServerUtil.logger.log(Level.SEVERE, "Exception while intializing failover properties", ex);
        }
    }
    
    private static class ProcessWriter extends Thread
    {
        BufferedReader br;
        Logger log;
        
        ProcessWriter(final BufferedReader br, final Logger logger) {
            this.log = null;
            this.br = br;
            this.log = logger;
        }
        
        @Override
        public void run() {
            try {
                String line;
                while ((line = this.br.readLine()) != null) {
                    if (this.log == null) {
                        ConsoleOut.print("\r" + line);
                    }
                    else {
                        this.log.info(line);
                    }
                }
            }
            catch (final Exception exc) {
                this.log.severe(exc.getMessage());
            }
        }
    }
}
