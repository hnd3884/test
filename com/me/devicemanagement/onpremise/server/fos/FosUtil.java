package com.me.devicemanagement.onpremise.server.fos;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import com.zoho.framework.utils.FileUtils;
import java.util.Arrays;
import com.adventnet.persistence.fos.FOS;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.tools.prevalent.Wield;
import com.me.devicemanagement.onpremise.start.DCStarter;
import java.io.IOException;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.SocketException;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Properties;
import java.net.InetAddress;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import com.adventnet.db.api.RelationalAPI;
import java.util.logging.Logger;
import com.adventnet.persistence.fos.DBUtil;

public class FosUtil extends DBUtil
{
    private static Logger logger;
    
    public static void updateFosParam(final String paramValue) {
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection con = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;
        try {
            con = relapi.getConnection();
            final String servingIP = DBUtil.getServingNodeIP();
            final String updateSql = "UPDATE FosParams SET IS_REPLICATION_PENDING=? WHERE SERVER_IP=?";
            final String insertSql = "insert into FosParams values(?,?,'0','0','0','false')";
            updateStmt = con.prepareStatement(updateSql);
            updateStmt.setBoolean(1, Boolean.valueOf(paramValue));
            updateStmt.setString(2, servingIP);
            final int rows = updateStmt.executeUpdate();
            if (rows == 0) {
                insertStmt = con.prepareStatement(insertSql);
                insertStmt.setString(1, servingIP);
                insertStmt.setBoolean(2, Boolean.valueOf(paramValue));
                insertStmt.execute();
            }
        }
        catch (final Exception ex) {
            FosUtil.logger.log(Level.SEVERE, "Sql Exception while updating FosParams table..", ex);
            if (updateStmt != null) {
                try {
                    updateStmt.close();
                }
                catch (final SQLException ex2) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex2);
                }
            }
            if (insertStmt != null) {
                try {
                    insertStmt.close();
                }
                catch (final SQLException ex2) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex2);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final SQLException e) {
                    FosUtil.logger.log(Level.SEVERE, " Exception while Closing connection..", e);
                }
            }
        }
        finally {
            if (updateStmt != null) {
                try {
                    updateStmt.close();
                }
                catch (final SQLException ex3) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex3);
                }
            }
            if (insertStmt != null) {
                try {
                    insertStmt.close();
                }
                catch (final SQLException ex3) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex3);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final SQLException e2) {
                    FosUtil.logger.log(Level.SEVERE, " Exception while Closing connection..", e2);
                }
            }
        }
    }
    
    public static void incrementReplicationErrorFrequency(final int errorCode) {
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection con = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;
        try {
            con = relapi.getConnection();
            final String updateSql = "update FosReplicationErrorCodes set ERROR_FREQUENCY=ERROR_FREQUENCY+1, LAST_ERROR_TIME=? where ERROR_CODE=?";
            final String insertSql = "insert into FosReplicationErrorCodes values(?,'1',?)";
            updateStmt = con.prepareStatement(updateSql);
            updateStmt.setLong(1, System.currentTimeMillis());
            updateStmt.setInt(2, errorCode);
            final int rows = updateStmt.executeUpdate();
            if (rows == 0) {
                insertStmt = con.prepareStatement(insertSql);
                insertStmt.setInt(1, errorCode);
                insertStmt.setLong(2, System.currentTimeMillis());
                insertStmt.execute();
            }
        }
        catch (final SQLException ex) {
            Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while incrementing replication error frequency", ex);
            if (updateStmt != null) {
                try {
                    updateStmt.close();
                }
                catch (final SQLException ex) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex);
                }
            }
            if (insertStmt != null) {
                try {
                    insertStmt.close();
                }
                catch (final SQLException ex) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final Exception e) {
                    FosUtil.logger.log(Level.SEVERE, " Exception while Closing connection..", e);
                }
            }
        }
        finally {
            if (updateStmt != null) {
                try {
                    updateStmt.close();
                }
                catch (final SQLException ex2) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex2);
                }
            }
            if (insertStmt != null) {
                try {
                    insertStmt.close();
                }
                catch (final SQLException ex2) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex2);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final Exception e2) {
                    FosUtil.logger.log(Level.SEVERE, " Exception while Closing connection..", e2);
                }
            }
        }
    }
    
    public static void incrementTakeOverCount() {
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection con = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;
        try {
            con = relapi.getConnection();
            final String servingIP = DBUtil.getServingNodeIP();
            final String updateSql = "update FosParams set TAKEOVER_COUNT=TAKEOVER_COUNT+1, LAST_TAKEOVER_TIME=?, SEND_TAKEOVER_MAIL='true' WHERE SERVER_IP=?";
            final String insertSql = "insert into FosParams values(?,'false','1',?,'0','true')";
            updateStmt = con.prepareStatement(updateSql);
            updateStmt.setLong(1, System.currentTimeMillis());
            updateStmt.setString(2, servingIP);
            final int rows = updateStmt.executeUpdate();
            if (rows == 0) {
                insertStmt = con.prepareStatement(insertSql);
                insertStmt.setString(1, servingIP);
                insertStmt.setLong(2, System.currentTimeMillis());
                insertStmt.execute();
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while incrementing TakeOver count", ex);
            if (updateStmt != null) {
                try {
                    updateStmt.close();
                }
                catch (final SQLException ex2) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex2);
                }
            }
            if (insertStmt != null) {
                try {
                    insertStmt.close();
                }
                catch (final SQLException ex2) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex2);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final Exception e) {
                    FosUtil.logger.log(Level.SEVERE, " Exception while Closing connection..", e);
                }
            }
        }
        finally {
            if (updateStmt != null) {
                try {
                    updateStmt.close();
                }
                catch (final SQLException ex3) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex3);
                }
            }
            if (insertStmt != null) {
                try {
                    insertStmt.close();
                }
                catch (final SQLException ex3) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex3);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final Exception e2) {
                    FosUtil.logger.log(Level.SEVERE, " Exception while Closing connection..", e2);
                }
            }
        }
    }
    
    public static Boolean getFOSParameter(final String paramKey) {
        Boolean IsPending = Boolean.FALSE;
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = relapi.getConnection();
            final String Sql = "SELECT " + paramKey + " from FosParams Where SERVER_IP=?";
            ps = con.prepareStatement(Sql);
            ps.setString(1, DBUtil.getServingNodeIP());
            rs = ps.executeQuery();
            while (rs.next()) {
                IsPending = rs.getBoolean("IS_REPLICATION_PENDING");
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while getting the replication pending flag", ex);
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (final SQLException ex2) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex2);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final SQLException ex2) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing Resultset", ex2);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final Exception e) {
                    FosUtil.logger.log(Level.SEVERE, " Exception while Closing connection..", e);
                }
            }
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (final SQLException ex3) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex3);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final SQLException ex3) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing Resultset", ex3);
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (final Exception e2) {
                    FosUtil.logger.log(Level.SEVERE, " Exception while Closing connection..", e2);
                }
            }
        }
        return IsPending;
    }
    
    public static boolean isFOSInSameNetwork() {
        Boolean isFOSInSameNetwork = Boolean.FALSE;
        try {
            final String FosUserConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos_user.conf";
            final Properties Props = StartupUtil.getProperties(FosUserConfFile);
            final String primaryIp = Props.getProperty("PrimaryServerIP");
            final String secondaryIp = Props.getProperty("SecondaryServerIP");
            final InetAddress secondaryAddress = InetAddress.getByName(secondaryIp);
            final InetAddress primaryAddress = InetAddress.getByName(primaryIp);
            final InetAddress localIpaddr = InetAddress.getLocalHost();
            final String mask = getMask(localIpaddr);
            isFOSInSameNetwork = sameNetwork(secondaryAddress, primaryAddress, mask);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return isFOSInSameNetwork;
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
    
    public static void replaceFileContent(final String filePath, final String stringToBeReplaced, final String replacementString) {
        final File fileToBeModified = new File(filePath);
        String oldContent = "";
        BufferedReader reader = null;
        FileWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(fileToBeModified));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                oldContent = oldContent + line + System.lineSeparator();
            }
            final String newContent = oldContent.replaceAll(stringToBeReplaced, replacementString);
            writer = new FileWriter(fileToBeModified);
            writer.write(newContent);
        }
        catch (final IOException e) {
            FosUtil.logger.log(Level.SEVERE, "Exception occured while Replacing the content from the file" + e);
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }
            catch (final IOException e) {
                FosUtil.logger.log(Level.SEVERE, "Exception while closing the reader or writer object" + e);
            }
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }
            catch (final IOException e2) {
                FosUtil.logger.log(Level.SEVERE, "Exception while closing the reader or writer object" + e2);
            }
        }
    }
    
    public static Properties getCurrentAndPeerIP() {
        final Properties ipProps = new Properties();
        final String serverHome = System.getProperty("server.home");
        final String FosUserConfFile = serverHome + File.separator + "conf" + File.separator + "fos_user.conf";
        final Properties Props = StartupUtil.getProperties(FosUserConfFile);
        final String primaryIp = Props.getProperty("PrimaryServerIP");
        final String secondaryIp = Props.getProperty("SecondaryServerIP");
        if (DCStarter.isIPPresent(primaryIp)) {
            ipProps.setProperty("currentIP", primaryIp);
            ipProps.setProperty("peerIP", secondaryIp);
        }
        else if (DCStarter.isIPPresent(secondaryIp)) {
            ipProps.setProperty("currentIP", secondaryIp);
            ipProps.setProperty("peerIP", primaryIp);
        }
        return ipProps;
    }
    
    public static boolean checkValidLicenseForFos() {
        final String homeDir = System.getProperty("server.home");
        final Wield w = Wield.getInstance();
        w.validateInvoke("License Agreement", homeDir, false, "lib", true);
        Boolean isValidLicenseForFos = Boolean.FALSE;
        try {
            if (w.getUserType().equalsIgnoreCase("T")) {
                isValidLicenseForFos = Boolean.TRUE;
            }
            else {
                Properties fosProp = w.getModuleProperties("FailOverService");
                if (fosProp == null) {
                    fosProp = w.getModuleProperties("AddOnModules");
                }
                if (fosProp == null) {
                    isValidLicenseForFos = Boolean.FALSE;
                }
                else {
                    isValidLicenseForFos = Boolean.parseBoolean(fosProp.getProperty("FOSEnabled"));
                    if (!isValidLicenseForFos && DCStarter.isFosTrialFlagEnabled()) {
                        final long expiryPeriod = DCStarter.getFosTrialExpiryPeriod();
                        if (expiryPeriod > 0L) {
                            isValidLicenseForFos = Boolean.TRUE;
                            FosUtil.logger.log(Level.INFO, "Trial license active");
                        }
                        else {
                            isValidLicenseForFos = Boolean.FALSE;
                            FosUtil.logger.log(Level.INFO, "Trial license expired");
                        }
                    }
                }
            }
            if (!w.isBare()) {
                FosUtil.logger.log(Level.INFO, "Invalid/Corrupted License detected, The server might be starting from standby mode , Cannot start in FOS mode");
                SyMUtil.triggerServerRestart("Server starting from Standby mode with expired license");
            }
        }
        catch (final Exception ex) {
            FosUtil.logger.log(Level.SEVERE, "Fos License File Exception", ex);
            isValidLicenseForFos = Boolean.FALSE;
            try {
                if (FOS.isEnabled()) {
                    FosUtil.logger.log(Level.INFO, "This server starting from StandBy mode, but License Expired, so trigerring restart");
                    SyMUtil.triggerServerRestart("Server starting from Standby mode with expired license");
                }
            }
            catch (final Exception exc) {
                FosUtil.logger.log(Level.SEVERE, "Exception while checking FOS enabled status", exc);
            }
        }
        FosUtil.logger.log(Level.INFO, "License Valid For Fail Over Service : {0}", isValidLicenseForFos);
        return isValidLicenseForFos;
    }
    
    public static Boolean replicateLicenseFiles(final String peerIp, final String serverHome) {
        Process p = null;
        final List<String> filesToBeReplicated = Arrays.asList("AdventNetLicense.xml", "petinfo.dat", "product.dat");
        final String logFile = serverHome + File.separator + "logs" + File.separator + "LicenseReplication.log";
        final List<String> opts = Arrays.asList("/z", "/R:3", "/W:5", "/tee", "/log:" + logFile);
        final String defaultLocation = new File(serverHome).getName();
        final String confPath = serverHome + File.separator + "conf" + File.separator + "fos.conf";
        try {
            final Properties replProps = FileUtils.readPropertyFile(new File(confPath));
            final String remoteInstallationDir = replProps.getProperty("repl.remoteinstallationDir", defaultLocation);
            final String sourceFolder = File.separator + File.separator + peerIp + File.separator + remoteInstallationDir + File.separator + "lib ";
            final String destinationFolder = System.getProperty("server.home") + File.separator + "lib ";
            final List<String> cmd = new ArrayList<String>();
            cmd.add("Robocopy");
            cmd.add(sourceFolder);
            cmd.add(destinationFolder);
            cmd.addAll(filesToBeReplicated);
            cmd.addAll(opts);
            final ProcessBuilder builder = new ProcessBuilder(cmd);
            p = builder.start();
            p.waitFor();
            final int replicationStatus = p.exitValue();
            FosUtil.logger.log(Level.INFO, "Replication completed with status : " + replicationStatus);
            if (replicationStatus < 8) {
                return true;
            }
            return false;
        }
        catch (final Exception exp) {
            FosUtil.logger.log(Level.SEVERE, " Exception occurred during replication");
        }
        finally {
            p.destroy();
        }
        return false;
    }
    
    static {
        FosUtil.logger = Logger.getLogger(FosUtil.class.getName());
    }
}
