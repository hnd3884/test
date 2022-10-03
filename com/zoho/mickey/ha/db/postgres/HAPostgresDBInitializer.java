package com.zoho.mickey.ha.db.postgres;

import java.util.Hashtable;
import java.sql.Connection;
import com.adventnet.db.adapter.postgres.PostgresConfUtil;
import java.util.concurrent.TimeUnit;
import java.io.InputStream;
import java.io.FileInputStream;
import com.zoho.mickey.ha.HASyncUtil;
import java.util.stream.Stream;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.nio.file.LinkOption;
import java.nio.file.FileVisitOption;
import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import com.adventnet.persistence.PersistenceUtil;
import java.nio.file.Paths;
import java.nio.file.Path;
import com.zoho.mickey.ha.HAException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.Properties;
import com.zoho.mickey.ha.db.DataBaseHAUtil;
import java.util.logging.Level;
import com.adventnet.db.api.RelationalAPI;
import com.zoho.mickey.ha.HAConfig;
import com.zoho.framework.utils.FileUtils;
import com.adventnet.persistence.PersistenceInitializer;
import java.io.IOException;
import com.zoho.mickey.ha.HAUtil;
import com.zoho.conf.Configuration;
import java.io.File;
import java.util.logging.Logger;
import com.adventnet.db.adapter.postgres.DefaultPostgresDBInitializer;

public class HAPostgresDBInitializer extends DefaultPostgresDBInitializer
{
    private static final Logger LOGGER;
    private static boolean hasCurrentDBServedasLastMaster;
    private File configureHA;
    private File ha_wal_archive_dir;
    
    public HAPostgresDBInitializer() {
        this.configureHA = new File(Configuration.getString("db.home") + File.separator + "data" + File.separator + "ha_configured");
        this.ha_wal_archive_dir = new File(Configuration.getString("server.home") + File.separator + "pgsql" + File.separator + "data" + File.separator + "ha_wal_archive");
    }
    
    public boolean preDBServerStartUp() throws IOException {
        if (!super.preDBServerStartUp()) {
            return false;
        }
        try {
            if (!HAUtil.isDataBaseHAEnabled()) {
                return true;
            }
            final String dbhome = Configuration.getString("db.home");
            final String version = this.getVersion();
            final String[] versionIndex = version.split("\\.");
            final String shortVersion = versionIndex[0] + "." + versionIndex[1];
            final Float dbVer = new Float(shortVersion);
            if (dbVer < new Float("10")) {
                throw new IOException("HA is not supported for versions less than postgres 10 version");
            }
            final File recoveryConf = new File(dbhome + File.separator + "data" + File.separator + "recovery.conf");
            final String confFilePath = (PersistenceInitializer.getConfigurationValue("ha.conf.file") != null) ? (Configuration.getString("server.home") + File.separator + PersistenceInitializer.getConfigurationValue("ha.conf.file")) : (Configuration.getString("server.home") + File.separator + "conf" + File.separator + "ha.conf");
            final Properties props = FileUtils.readPropertyFile(new File(confFilePath));
            final HAConfig config = new HAConfig();
            config.initialize(props);
            if (config.getPeerIP() == null) {
                FileUtils.deleteFile(recoveryConf);
                this.createTouchFileForHA();
                this.create_HA_archive_dir();
                return true;
            }
            if (config.getPeerDBPort() == null) {
                throw new IllegalArgumentException("DB port of PeerIP is not defined ");
            }
            final Properties dbProps = RelationalAPI.getInstance().getDBAdapter().getDBProps();
            final boolean isOtherNodeRunning = this.checkServerStatus(config.getPeerIP(), Integer.parseInt(config.getPeerDBPort()), dbProps.getProperty("username"));
            HAPostgresDBInitializer.LOGGER.log(Level.INFO, "is peer node running :: " + isOtherNodeRunning);
            this.syncConfigFiles(config, dbProps);
            if (!isOtherNodeRunning) {
                HAPostgresDBInitializer.hasCurrentDBServedasLastMaster = DataBaseHAUtil.hasCurrentDBServedAsLastMaster(config);
                HAPostgresDBInitializer.LOGGER.log(Level.INFO, "hasCurrentDBServedasLastMaster :: " + HAPostgresDBInitializer.hasCurrentDBServedasLastMaster);
                if (HAPostgresDBInitializer.hasCurrentDBServedasLastMaster) {
                    FileUtils.deleteFile(recoveryConf);
                }
                else {
                    this.createRecoveryConf(config, dbProps);
                }
                this.createTouchFileForHA();
                this.create_HA_archive_dir();
            }
            if (recoveryConf.exists()) {
                DataBaseHAUtil.syncDB(config);
                this.pullWALFiles(config);
            }
            else if (!recoveryConf.exists() && isOtherNodeRunning) {
                DataBaseHAUtil.syncDB(config);
                this.pullWALFiles(config);
                this.createRecoveryConf(config, dbProps);
                FileUtils.deleteFile(this.configureHA);
                this.deleteTouchFiles();
            }
            else if (!recoveryConf.exists() && !isOtherNodeRunning && !HAPostgresDBInitializer.hasCurrentDBServedasLastMaster) {
                DataBaseHAUtil.syncDB(config);
                this.pullWALFiles(config);
            }
        }
        catch (final Exception e) {
            throw new IOException(e.getMessage(), e);
        }
        return true;
    }
    
    private void create_HA_archive_dir() throws IOException, HAException {
        if (!this.ha_wal_archive_dir.exists()) {
            final Path path = Files.createDirectory(this.ha_wal_archive_dir.toPath(), (FileAttribute<?>[])new FileAttribute[0]);
            if (path == null || !path.toFile().exists()) {
                throw new HAException("unable to create " + this.ha_wal_archive_dir + " directory");
            }
            HAPostgresDBInitializer.LOGGER.info("successfully created " + this.ha_wal_archive_dir + " directory");
        }
    }
    
    private void createTouchFileForHA() throws IOException, HAException {
        if (!this.configureHA.exists()) {
            final Path path = Files.createFile(this.configureHA.toPath(), (FileAttribute<?>[])new FileAttribute[0]);
            if (path == null || !path.toFile().exists()) {
                throw new HAException("unable to create " + this.configureHA + " file");
            }
            HAPostgresDBInitializer.LOGGER.info("successfully created " + this.configureHA + " file");
        }
    }
    
    private void deleteTouchFiles() {
        final String dataPath = Paths.get(Configuration.getString("db.home"), "data").toString();
        final File fullBackUp = new File(dataPath + File.separator + "full_backup");
        final File incBackUp = new File(dataPath + File.separator + "incremental_backup");
        FileUtils.deleteFile(fullBackUp);
        FileUtils.deleteFile(incBackUp);
    }
    
    private void createRecoveryConf(final HAConfig config, final Properties dbProps) throws Exception {
        final String dbhome = Configuration.getString("db.home");
        final File recoveryConf = new File(dbhome + File.separator + "data" + File.separator + "recovery.conf");
        if (recoveryConf.exists()) {
            return;
        }
        try {
            final Properties properties = new Properties();
            ((Hashtable<String, String>)properties).put("standby_mode", "'on'");
            ((Hashtable<String, String>)properties).put("primary_conninfo", "'host=" + config.getPeerIP() + " port=" + config.getPeerDBPort() + " user=" + dbProps.getProperty("repl.username") + " password=" + PersistenceUtil.getDBPasswordProvider("postgres").getPassword((Object)dbProps.getProperty("repl.password")) + "'");
            ((Hashtable<String, String>)properties).put("trigger_file", "'pg_promote'");
            ((Hashtable<String, String>)properties).put("recovery_target_timeline", "'latest'");
            ((Hashtable<String, String>)properties).put("restore_command", "'if exist ha_wal_archive\\%f (copy ha_wal_archive\\%f %p)'");
            ((Hashtable<String, String>)properties).put("archive_cleanup_command", "'..\\bin\\pg_archivecleanup -d ..\\data\\ha_wal_archive %r'");
            FileUtils.writeToFile(recoveryConf, properties, "recovery props");
        }
        catch (final Exception e) {
            throw new Exception("Exception occurred while constructing recovery conf", e);
        }
    }
    
    private void pullWALFiles(final HAConfig config) throws Exception {
        if (DataBaseHAUtil.getDBServingTime(config.ipaddr()) == null) {
            return;
        }
        final Path pg_wal_path = Paths.get("pgsql", "data", "pg_wal");
        final Path ha_wal_archive_path = Paths.get("pgsql", "data", "ha_wal_archive");
        final Path full_pg_wal_path = Paths.get(Configuration.getString("server.home"), pg_wal_path.toString());
        final Path full_ha_wal_archive_path = Paths.get(Configuration.getString("server.home"), ha_wal_archive_path.toString());
        final List<String> files_in_PGWAL = this.getFiles(full_pg_wal_path);
        List<String> files_in_HA_WAL_ARCHIVE = new ArrayList<String>();
        if (full_ha_wal_archive_path.toFile().exists()) {
            files_in_HA_WAL_ARCHIVE = this.getFiles(full_ha_wal_archive_path);
        }
        try {
            HAPostgresDBInitializer.LOGGER.log(Level.INFO, "going to copy pg_wal directory from master database");
            DataBaseHAUtil.getFileReplicationHandler(config).replicateDirs(config, Arrays.asList(pg_wal_path.toString()), Arrays.asList("archive_status"), null, "COPY");
            HAPostgresDBInitializer.LOGGER.log(Level.INFO, "going to copy WAL files from master database");
            DataBaseHAUtil.getFileReplicationHandler(config).replicateDirs(config, Arrays.asList(ha_wal_archive_path.toString()), "COPY");
        }
        catch (final Exception e) {
            HAPostgresDBInitializer.LOGGER.log(Level.SEVERE, "Exception occurred while replicating WAL files from primary to secondary server :: " + e.getMessage());
            throw e;
        }
        finally {
            final List<String> current_files_in_PGWAL = this.getFiles(full_pg_wal_path);
            final List<String> current_files_in_HA_WAL_ARCHIVE = this.getFiles(full_ha_wal_archive_path);
            this.listChanges("pg_wal", files_in_PGWAL, current_files_in_PGWAL);
            this.listChanges("ha_wal_archive", files_in_HA_WAL_ARCHIVE, current_files_in_HA_WAL_ARCHIVE);
        }
    }
    
    private void listChanges(final String folder, final List<String> files, final List<String> current_files) {
        final ArrayList<String> clonedList = new ArrayList<String>(current_files);
        clonedList.removeAll(files);
        files.removeAll(current_files);
        HAPostgresDBInitializer.LOGGER.log(Level.INFO, "added files in {0} :: {1}", new Object[] { folder, clonedList });
        HAPostgresDBInitializer.LOGGER.log(Level.INFO, "deleted files in {0} :: {1}", new Object[] { folder, files });
    }
    
    private List<String> getFiles(final Path dirPath) throws IOException {
        try (final Stream<Path> paths = Files.walk(dirPath, new FileVisitOption[0])) {
            return (List<String>)paths.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).map(p -> p.getFileName().toString()).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList());
        }
    }
    
    private void syncConfigFiles(final HAConfig config, final Properties dbProps) {
        HAPostgresDBInitializer.LOGGER.log(Level.FINER, "Checking for sync patch dir file");
        final File syncPatch = new File(Configuration.getString("server.home") + File.separator + "sync_patch_dir");
        if (syncPatch.exists()) {
            HAPostgresDBInitializer.LOGGER.log(Level.INFO, "Syncing database_params.conf from other machine to update latest configurations");
            final File dbParamsFile = Paths.get(Configuration.getString("server.home"), "conf", "database_params.conf").toFile();
            String url = null;
            final Properties properties = new Properties();
            url = dbProps.getProperty("url");
            HASyncUtil.syncConfigFiles(config);
            try (final FileInputStream fis = new FileInputStream(dbParamsFile)) {
                properties.load(fis);
                PersistenceUtil.removeKeyInDBConf("url");
                PersistenceUtil.addKeyInDBConf("url", url);
                final String newPassword = PersistenceUtil.getDBPasswordProvider("postgres").getPassword((Object)properties.getProperty("password"));
                dbProps.setProperty("password", newPassword);
                final String newReplPassword = PersistenceUtil.getDBPasswordProvider("postgres").getPassword((Object)properties.getProperty("repl.password"));
                dbProps.setProperty("repl.password", newReplPassword);
                Configuration.setString("ha.dbparams.updated", "true");
                DataBaseHAUtil.syncDB(config);
            }
            catch (final Exception exp) {
                exp.printStackTrace();
                throw new RuntimeException("ERROR_IN_SYNCING_DBPARAMS_AFTER_PPM " + exp.getMessage());
            }
        }
    }
    
    public boolean postDBServerStartUp() throws IOException {
        if (!super.postDBServerStartUp()) {
            return false;
        }
        try {
            if (!HAUtil.isDataBaseHAEnabled()) {
                return true;
            }
            final String dbhome = Configuration.getString("db.home");
            final File recoveryConf = new File(dbhome + File.separator + "data" + File.separator + "recovery.conf");
            final String confFilePath = (PersistenceInitializer.getConfigurationValue("ha.conf.file") != null) ? (Configuration.getString("server.home") + File.separator + PersistenceInitializer.getConfigurationValue("ha.conf.file")) : (Configuration.getString("server.home") + File.separator + "conf" + File.separator + "ha.conf");
            final Properties props = FileUtils.readPropertyFile(new File(confFilePath));
            final HAConfig config = new HAConfig();
            config.initialize(props);
            if (config.getPeerIP() == null) {
                return true;
            }
            Properties dbProps = RelationalAPI.getInstance().getDBAdapter().getDBProps();
            final boolean isOtherNodeRunning = this.checkServerStatus(config.getPeerIP(), Integer.parseInt(config.getPeerDBPort()), dbProps.getProperty("username"));
            if (recoveryConf.exists() && !isOtherNodeRunning && !HAPostgresDBInitializer.hasCurrentDBServedasLastMaster) {
                DataBaseHAUtil.promoteDB(config);
            }
            if (recoveryConf.exists() || (!recoveryConf.exists() && isOtherNodeRunning) || (!recoveryConf.exists() && !isOtherNodeRunning && !HAPostgresDBInitializer.hasCurrentDBServedasLastMaster)) {
                DataBaseHAUtil.syncDB(config);
            }
            if (recoveryConf.exists() && isOtherNodeRunning) {
                if (!DataBaseHAUtil.isTimeInSync(config, dbProps)) {
                    throw new HAException("Timing of slave and master servers should be in sync. Please sync the time before proceeding.");
                }
                final long dbSyncWaitTime = TimeUnit.MINUTES.toSeconds(config.dbSyncWaitTime());
                for (int time = 0; time <= dbSyncWaitTime && !DataBaseHAUtil.isDataSyncedWithPrimary(); time += 2) {
                    HAPostgresDBInitializer.LOGGER.log(Level.INFO, "waiting for data to sync with primary database");
                    Thread.sleep(2000L);
                }
                HAPostgresDBInitializer.LOGGER.log(Level.INFO, "Data is successfully synced with primary database");
            }
            dbProps = (Properties)dbProps.clone();
            boolean isReadOnly = false;
            try (final Connection connection = DataBaseHAUtil.getConnection(dbProps)) {
                isReadOnly = RelationalAPI.getInstance().getDBAdapter().isReadOnly(connection);
            }
            if (!isReadOnly) {
                HAPostgresDBInitializer.LOGGER.log(Level.INFO, "updating serving time of DB");
                DataBaseHAUtil.updateTime(config.ipaddr(), dbProps);
            }
            if (isReadOnly && DataBaseHAUtil.getDBServingTime(config.ipaddr()) == null) {
                DataBaseHAUtil.getFileReplicationHandler(config).replicateFiles(config, Arrays.asList(Paths.get("conf", "ha", "ha.json").toString()));
                DataBaseHAUtil.updateDBServingTime(config.ipaddr(), Integer.toString(0));
                DataBaseHAUtil.pushFile(config, Paths.get("conf", "ha", "ha.json").toString());
            }
            HAPostgresDBInitializer.LOGGER.log(Level.INFO, "Adding/Updating database identifier if not present");
            DataBaseHAUtil.updateDatabaseIdentifier(config.ipaddr(), PostgresConfUtil.getSystemIdentifier(new File(Configuration.getString("db.home"))));
        }
        catch (final Exception e) {
            throw new IOException(e.getMessage(), e);
        }
        return true;
    }
    
    public boolean isDBReadyToAcceptConnection(final int port, final String host, final String userName, final String passwd) throws IOException {
        return super.isDBReadyToAcceptConnection(port, host, userName, RelationalAPI.getInstance().getDBAdapter().getDBProps().getProperty("password"));
    }
    
    static {
        LOGGER = Logger.getLogger(HAPostgresDBInitializer.class.getName());
        HAPostgresDBInitializer.hasCurrentDBServedasLastMaster = false;
    }
}
