package com.adventnet.persistence.fos.filereplication;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.ArrayList;
import com.zoho.framework.utils.OSCheckUtil;
import com.zoho.conf.Configuration;
import com.adventnet.persistence.fos.FOSUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import com.adventnet.persistence.fos.IPHandler;
import java.util.concurrent.TimeUnit;
import java.util.Properties;
import java.io.IOException;
import com.adventnet.persistence.fos.FOSException;
import com.adventnet.persistence.fos.FOSErrorCode;
import java.util.logging.Level;
import com.zoho.framework.utils.FileUtils;
import java.io.File;
import com.adventnet.persistence.fos.FOSConfig;
import com.adventnet.persistence.fos.FOSErrorHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Logger;

public class DefaultFileReplicationHandler implements FileReplicationHandler
{
    private static final Logger OUT;
    private static boolean scheduleRunning;
    private ScheduledThreadPoolExecutor executor;
    private FOSErrorHandler replErrorHandler;
    private ReplicationTask replTask;
    private String masterIP;
    private long delay;
    private int waitForCompletionInSecs;
    
    public DefaultFileReplicationHandler() {
        this.replTask = null;
        this.delay = 0L;
    }
    
    @Override
    public void initialize(final FOSConfig config, final String confPath) throws FOSException {
        try {
            final Properties replProps = FileUtils.readPropertyFile(new File(confPath));
            DefaultFileReplicationHandler.OUT.log(Level.FINER, "Replication properties:{0}", new Object[] { replProps });
            this.delay = Long.parseLong(replProps.getProperty("repl.scheduledelay", "2"));
            this.waitForCompletionInSecs = Integer.parseInt(replProps.getProperty("repl.waitforcompletion", "120"));
            this.replErrorHandler = config.errorHandler();
            this.masterIP = config.getPeerIP();
            this.executor = new ScheduledThreadPoolExecutor(1);
            this.replTask = new ReplicationTask(this.masterIP, this.replErrorHandler, replProps);
        }
        catch (final IOException exp) {
            throw new FOSException(FOSErrorCode.ERROR_MISC, exp.getMessage());
        }
    }
    
    @Override
    public void startReplication() throws FOSException {
        DefaultFileReplicationHandler.OUT.info("Starting replication schedule");
        this.executor.scheduleWithFixedDelay(this.replTask, 0L, this.delay, TimeUnit.MINUTES);
        DefaultFileReplicationHandler.scheduleRunning = true;
    }
    
    @Override
    public void stopReplication() throws FOSException {
        try {
            DefaultFileReplicationHandler.OUT.info("Stopping File replication");
            this.executor.shutdownNow();
            int cnt;
            for (cnt = 0; !this.executor.isTerminated() && cnt < this.waitForCompletionInSecs; cnt += 2) {
                DefaultFileReplicationHandler.OUT.log(Level.FINER, " waiting for executor to shutdown");
                Thread.sleep(2000L);
            }
            if (cnt >= this.waitForCompletionInSecs) {
                this.replErrorHandler.handleError(new FOSException(FOSErrorCode.ERROR_IN_STOPPING_REPLICATION_SCHEDULE, "Replication schedule termination did not completein [" + this.waitForCompletionInSecs + "]. Try starting the build"));
            }
            else {
                DefaultFileReplicationHandler.OUT.finer("Scheduler is shutdown successfully");
                DefaultFileReplicationHandler.scheduleRunning = false;
            }
        }
        catch (final Exception exp) {
            throw new FOSException(FOSErrorCode.ERROR_MISC, exp.getMessage());
        }
    }
    
    @Override
    public boolean completePendingReplication() {
        DefaultFileReplicationHandler.OUT.info("Completing pending replication in slave");
        try {
            if (!IPHandler.ping(this.masterIP)) {
                throw new Exception("Remote host unreachable while trying to replicate pending files");
            }
        }
        catch (final Exception e) {
            this.replErrorHandler.handleError(new FOSException(FOSErrorCode.ERROR_PING_FAILURE_DURING_REPLICATION, e.getMessage()));
            return false;
        }
        this.replTask.replicateFiles(true);
        return true;
    }
    
    @Override
    public boolean replicateOnce(final FOSConfig config, final List<String> files) throws FOSException {
        if (DefaultFileReplicationHandler.scheduleRunning) {
            DefaultFileReplicationHandler.OUT.log(Level.SEVERE, "Replication cannot be done as replication schedule is already running.");
            return false;
        }
        try {
            final Criteria c = new Criteria(new Column("FOSNodeDetails", "IP"), config.ipaddr(), 1);
            final DataObject dobj = DataAccess.get("FOSNodeDetails", c);
            if (dobj == null) {
                throw new Exception("No other node is present in FOS");
            }
            final String peerIP = dobj.getRow("FOSNodeDetails").get("IP").toString();
            final Properties replProps = FileUtils.readPropertyFile(new File(config.replConf()));
            final ReplicationTask task = new ReplicationTask(peerIP, config.errorHandler(), replProps);
            for (final String entry : files) {
                task.invokeReplScript("MIRROR", entry, null, null, true);
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            throw new FOSException(FOSErrorCode.ERROR_MISC, exp.getMessage());
        }
        return true;
    }
    
    private void assertDir(final File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("[" + dir.getAbsolutePath() + "] is not a directory");
        }
    }
    
    static {
        OUT = Logger.getLogger(DefaultFileReplicationHandler.class.getName());
        DefaultFileReplicationHandler.scheduleRunning = false;
    }
    
    class ReplicationTask implements Runnable
    {
        private boolean useCredentials;
        private String shareName;
        private String userName;
        private String password;
        private String remoteInstallationDir;
        private String replLog;
        private String replScriptFile;
        private String masterIP;
        private Map<Integer, String> mirrorDirMap;
        private Map<Integer, String> copyDirMap;
        private Map<Integer, List<String>> mirror_excludeDirMap;
        private Map<Integer, List<String>> mirror_excludeFileMap;
        private Map<Integer, List<String>> copy_excludeDirMap;
        private Map<Integer, List<String>> copy_excludeFileMap;
        private FOSErrorHandler replErrorHandler;
        
        public ReplicationTask(final String masterIP, final FOSErrorHandler errHandler, final Properties replProps) throws FOSException {
            this.mirrorDirMap = new HashMap<Integer, String>();
            this.copyDirMap = new HashMap<Integer, String>();
            this.mirror_excludeDirMap = new HashMap<Integer, List<String>>();
            this.mirror_excludeFileMap = new HashMap<Integer, List<String>>();
            this.copy_excludeDirMap = new HashMap<Integer, List<String>>();
            this.copy_excludeFileMap = new HashMap<Integer, List<String>>();
            this.useCredentials = Boolean.valueOf(replProps.getProperty("repl.usecredentials", "false"));
            if (this.useCredentials) {
                DefaultFileReplicationHandler.OUT.finer("Use credentials enabled");
                this.shareName = FOSUtil.getValue(replProps, "repl.sharename");
                this.userName = FOSUtil.getValue(replProps, "repl.username");
                this.password = FOSUtil.getValue(replProps, "repl.password");
            }
            this.remoteInstallationDir = replProps.getProperty("repl.remoteinstallationDir", "default");
            this.replLog = replProps.getProperty("repl.log", "../logs/fileRepl.txt");
            this.replScriptFile = replProps.getProperty("repl.script", "ReplicateFiles.bat");
            final String prefix = "\\\\" + masterIP + "\\" + this.remoteInstallationDir + "\\";
            this.mirrorDirMap = FOSUtil.getIndexVsDirMap(replProps.getProperty("repl.mirrorlist", ""));
            this.mirror_excludeDirMap = FOSUtil.getIndexVsExcludeDirMap(prefix, this.mirrorDirMap, replProps.getProperty("repl.mirror.excludedir", ""));
            this.mirror_excludeFileMap = FOSUtil.getIndexVsExcludeDirMap(prefix, this.mirrorDirMap, replProps.getProperty("repl.mirror.excludefile", ""));
            this.copyDirMap = FOSUtil.getIndexVsDirMap(replProps.getProperty("repl.copylist", ""));
            this.copy_excludeDirMap = FOSUtil.getIndexVsExcludeDirMap(prefix, this.copyDirMap, replProps.getProperty("repl.copy.excludedir", ""));
            this.copy_excludeFileMap = FOSUtil.getIndexVsExcludeDirMap(prefix, this.copyDirMap, replProps.getProperty("repl.copy.excludefile", ""));
            this.masterIP = masterIP;
            this.replErrorHandler = errHandler;
            this.print_excludeMaps();
        }
        
        @Override
        public void run() {
            this.replicateFiles(false);
        }
        
        private void replicateFiles(final boolean onTakeOver) {
            for (final Map.Entry<Integer, String> entry : this.mirrorDirMap.entrySet()) {
                DefaultFileReplicationHandler.OUT.log(Level.FINER, "Mirroring directory :[ " + entry.getValue() + " ]");
                this.invokeReplScript("MIRROR", entry.getValue(), this.mirror_excludeDirMap.get(entry.getKey()), this.mirror_excludeFileMap.get(entry.getKey()), onTakeOver);
            }
            for (final Map.Entry<Integer, String> entry : this.copyDirMap.entrySet()) {
                DefaultFileReplicationHandler.OUT.log(Level.FINER, "Copying directory :[ " + entry.getValue() + " ]");
                this.invokeReplScript("COPY", entry.getValue(), this.copy_excludeDirMap.get(entry.getKey()), this.copy_excludeFileMap.get(entry.getKey()), onTakeOver);
            }
        }
        
        private void assertDirs() {
            for (final Map.Entry<Integer, String> entry : this.mirrorDirMap.entrySet()) {
                DefaultFileReplicationHandler.this.assertDir(new File(Configuration.getString("server.home") + File.separator + entry.getValue()));
            }
            for (final Map.Entry<Integer, String> entry : this.copyDirMap.entrySet()) {
                DefaultFileReplicationHandler.this.assertDir(new File(Configuration.getString("server.home") + File.separator + entry.getValue()));
            }
        }
        
        private void assertReplicationScript(final String scriptFile) {
            final File file = new File(Configuration.getString("server.home") + File.separator + "bin/" + scriptFile);
            if (file.exists() && file.canExecute()) {
                return;
            }
            throw new IllegalArgumentException("Invalid replication script :[" + scriptFile + "]. Either file not found or do not have execute permission");
        }
        
        private void invokeReplScript(final String operation, final String dir, final List<String> excludeDirList, final List<String> excludeFileList, final boolean onTakeOver) {
            Process p = null;
            int exitValue = -1;
            try {
                final boolean isWindows = OSCheckUtil.isWindows(OSCheckUtil.getOS());
                final List<String> commandList = new ArrayList<String>();
                this.assertReplicationScript(this.replScriptFile);
                if (isWindows) {
                    commandList.add("cmd");
                    commandList.add("/c");
                    commandList.add(this.replScriptFile);
                    commandList.add(this.masterIP);
                    if (this.useCredentials) {
                        commandList.add("netuse");
                        commandList.add(this.shareName);
                        commandList.add(this.userName);
                        commandList.add(this.password);
                    }
                    else {
                        commandList.add("shareEnabled");
                    }
                    commandList.add(this.remoteInstallationDir);
                    commandList.add(operation);
                    commandList.add(this.replLog);
                    commandList.add(dir);
                    if (excludeDirList != null && excludeDirList.size() > 0) {
                        commandList.add("/XD");
                        commandList.addAll(excludeDirList);
                    }
                    if (excludeFileList != null && excludeFileList.size() > 0) {
                        commandList.add("/XF");
                        commandList.addAll(excludeFileList);
                    }
                }
                else {
                    commandList.add("sh");
                    commandList.add("replScriptFile");
                }
                final ProcessBuilder pb = new ProcessBuilder(commandList);
                final File file = new File(Configuration.getString("server.home") + File.separator + "bin/");
                pb.directory(file);
                p = pb.start();
                if (isWindows && this.useCredentials) {
                    commandList.set(commandList.lastIndexOf(this.password), "password=*******");
                }
                DefaultFileReplicationHandler.OUT.log(Level.FINER, "Command to be executed {0} ", commandList);
                final ProcessWriter pw = new ProcessWriter(new BufferedReader(new InputStreamReader(p.getInputStream())), DefaultFileReplicationHandler.OUT);
                final ProcessWriter pw2 = new ProcessWriter(new BufferedReader(new InputStreamReader(p.getErrorStream())), DefaultFileReplicationHandler.OUT);
                pw2.start();
                pw.start();
                p.waitFor();
                exitValue = p.exitValue();
            }
            catch (final Exception exp) {
                this.replErrorHandler.handleError(new FOSException(FOSErrorCode.ERROR_FILE_REPLICATION, exp.getMessage()));
            }
            finally {
                p.destroy();
            }
            DefaultFileReplicationHandler.OUT.log(Level.INFO, "{0} task Completed for dir {1}. Process Return code :[ {2} ] process Error code :[ {3} ]", new Object[] { operation, dir, exitValue, onTakeOver });
            this.processErrorCode(operation, dir, exitValue, onTakeOver);
        }
        
        private void processErrorCode(final String operation, final String dir, final int exitValue, final boolean onTakeOver) {
            if (exitValue > 7 && exitValue < 16) {
                this.replErrorHandler.handleReplicationError(exitValue, dir, "Warning: Some of the files or directories could not be copied. Refer Replicaiton log", onTakeOver);
            }
            if (exitValue == 16) {
                this.replErrorHandler.handleReplicationError(exitValue, dir, "Error:Serious error. Robocopy did not copy any files.", onTakeOver);
            }
        }
        
        public void print_excludeMaps() {
            DefaultFileReplicationHandler.OUT.finer(this.mirror_excludeDirMap.toString());
            DefaultFileReplicationHandler.OUT.finer(this.mirror_excludeFileMap.toString());
            DefaultFileReplicationHandler.OUT.finer(this.mirrorDirMap.toString());
        }
    }
    
    private class ProcessWriter extends Thread
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
            String line = "";
            try {
                while ((line = this.br.readLine()) != null) {
                    this.log.finer(line);
                }
            }
            catch (final Exception exc) {
                DefaultFileReplicationHandler.OUT.severe(exc.getMessage());
            }
        }
    }
}
