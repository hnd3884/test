package com.adventnet.sym.server.fos;

import com.adventnet.persistence.fos.FOS;
import java.util.LinkedHashMap;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import com.me.devicemanagement.onpremise.server.fos.FosUtil;
import com.adventnet.db.api.RelationalAPI;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Level;
import com.adventnet.persistence.fos.FOSUtil;
import com.zoho.framework.utils.FileUtils;
import java.io.File;
import java.util.Timer;
import java.util.Properties;
import java.util.logging.Logger;
import com.adventnet.mfw.modulestartup.ModuleStartStopProcessor;

public class MonitorFailover implements ModuleStartStopProcessor
{
    private static final Logger LOG;
    private static String ipaddr;
    private static Properties fosProps;
    private Timer timer;
    private static final String IS_STATIC = "IS_STATIC";
    private static final String IS_DISKSPACE_ENOUGH = "IS_DISKSPACE_ENOUGH";
    private static final String DISKSPACE = "DISKSPACE";
    private static final String TOTALDISKSPACE = "TOTALDISKSPACE";
    private static final String IS_TIME_SAME = "IS_TIME_SAME";
    private static final String IS_PEER_REACHABLE = "IS_PEER_REACHABLE";
    private static int interval;
    
    public void initialize() throws Exception {
        final String fosConfFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "fos.conf";
        MonitorFailover.fosProps = FileUtils.readPropertyFile(new File(fosConfFilePath));
        final String publicIP = MonitorFailover.fosProps.getProperty("publicIP");
        MonitorFailover.ipaddr = MonitorFailover.fosProps.getProperty("ipaddr", FOSUtil.getIPAddr(publicIP));
        MonitorFailover.LOG.log(Level.SEVERE, "Failover Health Monitoring Task initialized");
    }
    
    public void preStartProcess() throws Exception {
        MonitorFailover.LOG.log(Level.SEVERE, "Failover Health Monitoring Task called");
        final TimerTask task = new updateTask();
        (this.timer = new Timer(true)).scheduleAtFixedRate(task, 0L, MonitorFailover.interval * 60 * 1000);
    }
    
    public void postStartProcess() throws Exception {
    }
    
    public void stopProcess() throws Exception {
    }
    
    private void update(final Map healthprops) {
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection con = null;
        final String updateSql = "UPDATE FosHealthDetails SET IS_STATIC=?,IS_SW_REPO_REMOTE=?,IS_SW_REPO_REACHABLE=?,IS_PATCH_STORE_REMOTE=?,IS_PATCH_STORE_REACHABLE=?,IS_DISKSPACE_ENOUGH=?,DISKSPACE=?,TOTALDISKSPACE=?,IS_TIME_SAME=?,IS_PEER_REACHABLE=? WHERE SERVER_IP=?";
        final String insertSql = "INSERT into FosHealthDetails values(?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement updateStatement = null;
        PreparedStatement insertStatement = null;
        try {
            con = relapi.getConnection();
            updateStatement = con.prepareStatement(updateSql);
            updateStatement.setBoolean(1, healthprops.get("IS_STATIC"));
            updateStatement.setBoolean(2, true);
            updateStatement.setBoolean(3, true);
            updateStatement.setBoolean(4, true);
            updateStatement.setBoolean(5, true);
            updateStatement.setBoolean(6, healthprops.get("IS_DISKSPACE_ENOUGH"));
            updateStatement.setLong(7, Long.valueOf(healthprops.get("DISKSPACE").toString()));
            updateStatement.setLong(8, Long.valueOf(healthprops.get("TOTALDISKSPACE").toString()));
            updateStatement.setBoolean(9, healthprops.get("IS_TIME_SAME"));
            updateStatement.setBoolean(10, healthprops.get("IS_PEER_REACHABLE"));
            updateStatement.setString(11, MonitorFailover.ipaddr);
            final int rows = updateStatement.executeUpdate();
            if (rows == 0) {
                insertStatement = con.prepareStatement(insertSql);
                insertStatement.setString(1, MonitorFailover.ipaddr);
                insertStatement.setBoolean(2, healthprops.get("IS_STATIC"));
                insertStatement.setBoolean(3, true);
                insertStatement.setBoolean(4, true);
                insertStatement.setBoolean(5, true);
                insertStatement.setBoolean(6, true);
                insertStatement.setBoolean(7, healthprops.get("IS_DISKSPACE_ENOUGH"));
                insertStatement.setLong(8, Long.valueOf(healthprops.get("DISKSPACE").toString()));
                insertStatement.setLong(9, Long.valueOf(healthprops.get("TOTALDISKSPACE").toString()));
                insertStatement.setBoolean(10, healthprops.get("IS_TIME_SAME"));
                insertStatement.setBoolean(11, healthprops.get("IS_PEER_REACHABLE"));
                insertStatement.execute();
            }
        }
        catch (final Exception ex) {
            MonitorFailover.LOG.log(Level.SEVERE, "Sql Exception while updating FosHealthDetails table..", ex);
            if (updateStatement != null) {
                try {
                    updateStatement.close();
                }
                catch (final SQLException ex2) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex2);
                }
            }
            if (insertStatement != null) {
                try {
                    insertStatement.close();
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
                    MonitorFailover.LOG.log(Level.SEVERE, " Exception while Closing connection..", e);
                }
            }
        }
        finally {
            if (updateStatement != null) {
                try {
                    updateStatement.close();
                }
                catch (final SQLException ex3) {
                    Logger.getLogger(FosUtil.class.getName()).log(Level.SEVERE, "Exception while closing statement", ex3);
                }
            }
            if (insertStatement != null) {
                try {
                    insertStatement.close();
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
                    MonitorFailover.LOG.log(Level.SEVERE, " Exception while Closing connection..", e2);
                }
            }
        }
    }
    
    static {
        LOG = Logger.getLogger("FailoverMonitorLogger");
        MonitorFailover.ipaddr = null;
        MonitorFailover.fosProps = new Properties();
        MonitorFailover.interval = 60;
    }
    
    public class updateTask extends TimerTask
    {
        @Override
        public void run() {
            final Map healthProps = new LinkedHashMap();
            final Long diskSpace = FailoverServerUtil.getFreeDiskSpace();
            healthProps.put("DISKSPACE", diskSpace);
            final Boolean isDiskSpaceEnough = FailoverServerUtil.isDiskSpaceEnough(diskSpace);
            healthProps.put("IS_DISKSPACE_ENOUGH", isDiskSpaceEnough);
            final File serverHome = new File(System.getProperty("server.home"));
            final Long totalSpace = serverHome.getTotalSpace();
            healthProps.put("TOTALDISKSPACE", totalSpace);
            MonitorFailover.LOG.log(Level.INFO, "Health Status for : {0}", MonitorFailover.ipaddr);
            MonitorFailover.LOG.log(Level.INFO, "----------------------------------");
            MonitorFailover.LOG.log(Level.INFO, " Disk Space enough : {0}", isDiskSpaceEnough.toString());
            final Boolean isStaticIP = FailoverServerUtil.isStaticIP(MonitorFailover.ipaddr);
            MonitorFailover.LOG.log(Level.INFO, " Static IP :{0}", isStaticIP.toString());
            healthProps.put("IS_STATIC", isStaticIP);
            Boolean isTimeSame = Boolean.TRUE;
            final Boolean isOtherServerReachable = FailoverServerUtil.isOtherServerInFosReachable();
            final FOS fosObject = new FOS();
            try {
                fosObject.initialize();
                final String peerIP = fosObject.getOtherNode();
                MonitorFailover.LOG.log(Level.SEVERE, " Peer IP : {0}", peerIP);
                if (peerIP != null) {
                    isTimeSame = !FailoverServerUtil.isTimeDiff(peerIP, 10);
                }
            }
            catch (final Exception ex) {
                Logger.getLogger(MonitorFailover.class.getName()).log(Level.SEVERE, "Exception while getting peer IP", ex);
            }
            MonitorFailover.LOG.log(Level.INFO, " Time Difference Exists : {0}", isTimeSame.toString());
            healthProps.put("IS_TIME_SAME", isTimeSame);
            MonitorFailover.LOG.log(Level.INFO, " Peer Reachable : {0}", isOtherServerReachable.toString());
            healthProps.put("IS_PEER_REACHABLE", isOtherServerReachable);
            MonitorFailover.this.update(healthProps);
        }
    }
}
