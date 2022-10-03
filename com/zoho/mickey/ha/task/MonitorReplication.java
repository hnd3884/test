package com.zoho.mickey.ha.task;

import java.util.Properties;
import com.adventnet.persistence.PersistenceInitializer;
import com.zoho.framework.utils.OSCheckUtil;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.taskengine.Scheduler;
import java.util.List;
import java.util.logging.Level;
import java.util.ArrayList;
import com.zoho.framework.utils.FileUtils;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.taskengine.TaskExecutionException;
import com.adventnet.mfw.message.Messenger;
import com.zoho.mickey.ha.HAErrorCode;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.taskengine.TaskContext;
import com.zoho.mickey.ha.HAConfig;
import java.io.File;
import java.util.logging.Logger;
import com.adventnet.taskengine.Task;

public class MonitorReplication implements Task
{
    private static final Logger LOGGER;
    private static final boolean IS_WINDOWS;
    private static File pg_archivecleanup;
    private static File ha_wal_archive;
    private static HAConfig config;
    private static final String MONITOR_SCHEDULE = "PostgresReplicationMonitor";
    
    public void executeTask(final TaskContext taskContext) throws TaskExecutionException {
        final String monitorSQL = "select pg_walfile_name(replay_lsn) as replayedWAL from pg_stat_replication;";
        final RelationalAPI relApi = RelationalAPI.getInstance();
        try (final Connection conn = relApi.getConnection();
             final DataSet ds = relApi.executeQuery(monitorSQL, conn)) {
            if (ds.next()) {
                final String replayedWALFile = ds.getAsString(1);
                this.removeUnwantedWALFiles(replayedWALFile);
                this.removeUnwantedBackupFiles(replayedWALFile);
            }
            else {
                Messenger.publish("HATopic", (Object)HAErrorCode.PEER_NODE_DOWN);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new TaskExecutionException("Exception occurred while obtaining replication status the database :: " + e.getMessage());
        }
    }
    
    private void removeUnwantedBackupFiles(final String replayedWALFile) {
        final File[] listFiles;
        final File[] backupFiles = listFiles = FileUtils.listFiles(MonitorReplication.ha_wal_archive, (String)null, ".backup");
        for (final File backUpFile : listFiles) {
            final String backUpFileName = backUpFile.getName();
            final File WALFile = new File(MonitorReplication.ha_wal_archive.getAbsolutePath() + File.separator + backUpFileName.substring(0, backUpFileName.indexOf(".")));
            if (!WALFile.exists()) {
                MonitorReplication.LOGGER.info("going to delete :: " + backUpFile.getName() + "  :: delete status :: " + backUpFile.delete());
            }
        }
        MonitorReplication.LOGGER.info("deleted backup files");
    }
    
    private void removeUnwantedWALFiles(final String replayedWALFile) throws Exception {
        final List<String> commandList = new ArrayList<String>();
        commandList.add(MonitorReplication.pg_archivecleanup.getAbsolutePath());
        commandList.add(MonitorReplication.ha_wal_archive.getAbsolutePath());
        commandList.add(replayedWALFile);
        Process removeUnwantedWALFiles = null;
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            removeUnwantedWALFiles = processBuilder.start();
            RelationalAPI.getInstance().getDBAdapter().getDBInitializer().dump(removeUnwantedWALFiles, MonitorReplication.LOGGER);
            removeUnwantedWALFiles.waitFor();
            final int exitValue = removeUnwantedWALFiles.exitValue();
            if (exitValue != 0) {
                MonitorReplication.LOGGER.log(Level.SEVERE, "problem while deleting unwanted archived WAL files");
            }
        }
        catch (final Exception e) {
            MonitorReplication.LOGGER.log(Level.SEVERE, "Exception occurred while deleting unwanted archived WAL files" + e.getMessage());
            e.printStackTrace();
            Messenger.publish("HATopic", (Object)HAErrorCode.PROBLEM_DELETING_WALFILES);
        }
        finally {
            if (removeUnwantedWALFiles != null) {
                removeUnwantedWALFiles.destroy();
            }
        }
    }
    
    public void stopTask() throws TaskExecutionException {
    }
    
    public static void enableTask() throws Exception {
        MonitorReplication.LOGGER.log(Level.INFO, "Going to enable postgres monitor schedule");
        final Scheduler s = (Scheduler)BeanUtil.lookup("Scheduler");
        s.setScheduledTaskAdminStatus("PostgresReplicationMonitor", "PostgresReplicationMonitor", 3);
    }
    
    public static void disableTask() throws Exception {
        MonitorReplication.LOGGER.log(Level.INFO, "Going to disable postgres monitor schedule");
        final Scheduler s = (Scheduler)BeanUtil.lookup("Scheduler");
        s.setScheduledTaskAdminStatus("PostgresReplicationMonitor", "PostgresReplicationMonitor", 4);
    }
    
    static {
        LOGGER = Logger.getLogger(MonitorReplication.class.getName());
        IS_WINDOWS = OSCheckUtil.isWindows(OSCheckUtil.getOS());
        MonitorReplication.pg_archivecleanup = null;
        MonitorReplication.ha_wal_archive = new File(System.getProperty("db.home") + File.separator + "data" + File.separator + "ha_wal_archive");
        MonitorReplication.config = new HAConfig();
        MonitorReplication.pg_archivecleanup = new File(System.getProperty("db.home") + File.separator + "bin" + File.separator + (MonitorReplication.IS_WINDOWS ? "pg_archivecleanup.exe" : "pg_archivecleanup"));
        if (!MonitorReplication.pg_archivecleanup.exists()) {
            throw new RuntimeException("pg_archivecleanup binary does not exist. This binary is used to clean up archived WAL files, unwanted WAL files will exhaust memory with out it");
        }
        try {
            final String confFilePath = (PersistenceInitializer.getConfigurationValue("ha.conf.file") != null) ? (System.getProperty("server.home") + File.separator + PersistenceInitializer.getConfigurationValue("ha.conf.file")) : (System.getProperty("server.home") + File.separator + "conf" + File.separator + "ha.conf");
            final Properties props = FileUtils.readPropertyFile(new File(confFilePath));
            MonitorReplication.config.initialize(props);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception occurred while loading properties from configuration file :: " + e.getMessage());
        }
    }
}
