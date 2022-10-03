package com.me.devicemanagement.onpremise.server.fos;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.ArrayList;
import com.zoho.framework.utils.OSCheckUtil;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.fos.FOSUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
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
import com.adventnet.persistence.fos.filereplication.FileReplicationHandler;
import com.adventnet.persistence.fos.filereplication.DefaultFileReplicationHandler;

public class DCFosFileReplicationHandler extends DefaultFileReplicationHandler implements FileReplicationHandler
{
    private static final Logger OUT;
    private ScheduledThreadPoolExecutor executor;
    private FOSErrorHandler replErrorHandler;
    private ReplicationTask replTask;
    private String masterIP;
    private long delay;
    private int waitForCompletionInSecs;
    
    public DCFosFileReplicationHandler() {
        this.delay = 0L;
    }
    
    public void initialize(final FOSConfig config, final String confPath) throws FOSException {
        try {
            final Properties replProps = FileUtils.readPropertyFile(new File(confPath));
            DCFosFileReplicationHandler.OUT.log(Level.FINER, "Replication properties:{0}", new Object[] { replProps });
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
        super.initialize(config, confPath);
    }
    
    public void startReplication() throws FOSException {
        final Boolean CompletePending = FosUtil.getFOSParameter("IS_REPLICATION_PENDING");
        if (CompletePending) {
            DCFosFileReplicationHandler.OUT.info("pushing latest changes to master before replicating since complete pending replication failed during last takeover");
            this.replTask.replicateFiles(false);
            FosUtil.updateFosParam("false");
        }
        super.startReplication();
    }
    
    public void stopReplication() throws FOSException {
        super.stopReplication();
    }
    
    public boolean completePendingReplication() {
        final long completeRepStartTime = System.currentTimeMillis();
        final boolean result = super.completePendingReplication();
        final long completeRepEndTime = System.currentTimeMillis();
        final int completeRepTimeInMin = (int)(completeRepEndTime - completeRepStartTime) / 1000;
        System.setProperty("completePendingRepEndTime", completeRepEndTime + "");
        System.setProperty("completePendingRepCompletedTime", completeRepTimeInMin + "");
        return result;
    }
    
    public boolean replicateOnce(final FOSConfig config, final List<String> files) throws FOSException {
        return super.replicateOnce(config, (List)files);
    }
    
    private void assertDir(final File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("[" + dir.getAbsolutePath() + "] is not a directory");
        }
    }
    
    static {
        OUT = Logger.getLogger(DCFosFileReplicationHandler.class.getName());
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
        private String slaveIP;
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
                DCFosFileReplicationHandler.OUT.finer("Use credentials enabled");
                this.shareName = FOSUtil.getValue(replProps, "repl.sharename");
                this.userName = FOSUtil.getValue(replProps, "repl.username");
                this.password = FOSUtil.getValue(replProps, "repl.password");
            }
            this.remoteInstallationDir = replProps.getProperty("repl.remoteinstallationDir", "default");
            this.replLog = replProps.getProperty("repl.log", "../logs/fileRepl.txt");
            this.replScriptFile = replProps.getProperty("repl.script", "ReplicateFiles.bat");
            String prefix = null;
            try {
                prefix = SyMUtil.getInstallationDir() + "\\";
            }
            catch (final Exception ex) {
                Logger.getLogger(DCFosFileReplicationHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.mirrorDirMap = FOSUtil.getIndexVsDirMap(replProps.getProperty("repl.mirrorlist", ""));
            this.mirror_excludeDirMap = FOSUtil.getIndexVsExcludeDirMap(prefix, (Map)this.mirrorDirMap, replProps.getProperty("repl.mirror.excludedir", ""));
            this.mirror_excludeFileMap = FOSUtil.getIndexVsExcludeDirMap(prefix, (Map)this.mirrorDirMap, replProps.getProperty("repl.mirror.excludefile", ""));
            this.copyDirMap = FOSUtil.getIndexVsDirMap(replProps.getProperty("repl.copylist", ""));
            this.copy_excludeDirMap = FOSUtil.getIndexVsExcludeDirMap(prefix, (Map)this.copyDirMap, replProps.getProperty("repl.copy.excludedir", ""));
            this.copy_excludeFileMap = FOSUtil.getIndexVsExcludeDirMap(prefix, (Map)this.copyDirMap, replProps.getProperty("repl.copy.excludefile", ""));
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
                DCFosFileReplicationHandler.OUT.log(Level.FINER, "Mirroring directory :[ " + entry.getValue() + " ]");
                this.invokeReplScript("MIRROR", entry.getValue(), this.mirror_excludeDirMap.get(entry.getKey()), this.mirror_excludeFileMap.get(entry.getKey()), onTakeOver);
            }
            for (final Map.Entry<Integer, String> entry : this.copyDirMap.entrySet()) {
                DCFosFileReplicationHandler.OUT.log(Level.FINER, "Copying directory :[ " + entry.getValue() + " ]");
                this.invokeReplScript("COPY", entry.getValue(), this.copy_excludeDirMap.get(entry.getKey()), this.copy_excludeFileMap.get(entry.getKey()), onTakeOver);
            }
        }
        
        private void assertDirs() {
            for (final Map.Entry<Integer, String> entry : this.mirrorDirMap.entrySet()) {
                DCFosFileReplicationHandler.this.assertDir(new File(System.getProperty("server.home") + File.separator + entry.getValue()));
            }
            for (final Map.Entry<Integer, String> entry : this.copyDirMap.entrySet()) {
                DCFosFileReplicationHandler.this.assertDir(new File(System.getProperty("server.home") + File.separator + entry.getValue()));
            }
        }
        
        private void assertReplicationScript(final String scriptFile) {
            final File file = new File(System.getProperty("server.home") + File.separator + "bin/" + scriptFile);
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
                    commandList.add("latest");
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
                    commandList.add("/XO");
                }
                else {
                    commandList.add("sh");
                    commandList.add("replScriptFile");
                }
                final ProcessBuilder pb = new ProcessBuilder(commandList);
                final File file = new File(System.getProperty("server.home") + File.separator + "bin/");
                pb.directory(file);
                p = pb.start();
                final ProcessWriter pw = new ProcessWriter(new BufferedReader(new InputStreamReader(p.getInputStream())), DCFosFileReplicationHandler.OUT);
                final ProcessWriter pw2 = new ProcessWriter(new BufferedReader(new InputStreamReader(p.getErrorStream())), DCFosFileReplicationHandler.OUT);
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
            DCFosFileReplicationHandler.OUT.log(Level.INFO, "{0} task Completed for dir {1}. Process Return code :[ {2} ] process Error code :[ {3} ]", new Object[] { operation, dir, exitValue, onTakeOver });
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
            DCFosFileReplicationHandler.OUT.finer(this.mirror_excludeDirMap.toString());
            DCFosFileReplicationHandler.OUT.finer(this.mirror_excludeFileMap.toString());
            DCFosFileReplicationHandler.OUT.finer(this.mirrorDirMap.toString());
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
                DCFosFileReplicationHandler.OUT.severe(exc.getMessage());
            }
        }
    }
}
