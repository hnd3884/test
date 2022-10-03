package com.me.devicemanagement.onpremise.tools.servertroubleshooter.start;

import org.apache.juli.ClassLoaderLogManager;
import java.util.logging.LogManager;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.STSInterface;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.STSToolWorker;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.STSToolConstants;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.STSToolUtil;
import java.util.logging.Level;
import com.adventnet.mfw.logging.LoggerUtil;
import java.util.logging.Logger;

public class ServerTroubleShooterStarter
{
    private static final Logger LOGGER;
    private static boolean isShutdownInprogress;
    private static boolean isToolSpecificInvocation;
    private static boolean isToolExecutionCompleted;
    private static String toolName;
    
    public static void main(final String[] args) throws Exception {
        if (args.length > 0) {
            ServerTroubleShooterStarter.isToolSpecificInvocation = true;
            ServerTroubleShooterStarter.toolName = args[0];
        }
        if (ServerTroubleShooterStarter.isToolSpecificInvocation) {
            LoggerUtil.initLog("STS_" + ServerTroubleShooterStarter.toolName, true);
        }
        ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "====================");
        ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "Starting STS tool...");
        ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "====================");
        if (STSToolUtil.getSTSConfFileProps().getProperty("sts.tool.execution.allow").trim().equalsIgnoreCase(Boolean.FALSE.toString())) {
            ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "{0} value is set as false in {1} file. Going to stop...", new Object[] { "sts.tool.execution.allow", STSToolConstants.STS_CONF_FILE });
            stopSTSTool();
        }
        createLockFile();
        addShutdownHook();
        final ServerTroubleShooterStarter STSObj = new ServerTroubleShooterStarter();
        STSObj.addLockFileMonitorForShutdown();
        if (ServerTroubleShooterStarter.isToolSpecificInvocation) {
            ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "STS tool started to run {0} tool alone");
            startToolAlone(ServerTroubleShooterStarter.toolName);
            ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "{0} tool execution completed", args[0]);
            stopSTSTool();
        }
        STSToolWorker.getInstance().executeTools();
        ServerTroubleShooterStarter.isToolExecutionCompleted = true;
        stopSTSTool();
    }
    
    private static void startToolAlone(final String toolName) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
        final String className = STSToolUtil.getSTSConfFileProps().getProperty(toolName + ".class").trim();
        final STSInterface toolObj = (STSInterface)Class.forName(className).newInstance();
        toolObj.startTool();
    }
    
    private void addLockFileMonitorForShutdown() {
        ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "Starting Lock file monitor...");
        final LockFileMonitor lockFileMonitor = new LockFileMonitor();
        lockFileMonitor.start();
        ServerTroubleShooterStarter.LOGGER.log(Level.FINE, "Lock file monitor started");
    }
    
    private static void createLockFile() throws IOException {
        ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "Going to create lock file");
        final String lockFilePath = System.getProperty("ststool.home") + File.separator + STSToolConstants.LOCK_FILE;
        File lockFile = null;
        FileWriter fileWriter = null;
        try {
            lockFile = new File(lockFilePath);
            if (lockFile.exists()) {
                ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "{0} file already exists. Going to delete", lockFilePath);
                deleteLockFile();
            }
            fileWriter = new FileWriter(lockFile);
            final long pid = STSToolUtil.getJavaCurrentPid();
            fileWriter.write(String.valueOf(pid));
            ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "New {0} file created successfully. pid value : {1}", new Object[] { lockFilePath, pid });
        }
        catch (final IOException ex) {
            ServerTroubleShooterStarter.LOGGER.log(Level.WARNING, "Exception while creating lock file : ", ex);
            throw ex;
        }
        finally {
            if (fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
            }
        }
    }
    
    private static void addShutdownHook() {
        ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "Adding shutdown hook");
        final LogManager logManager = LogManager.getLogManager();
        if (logManager instanceof ClassLoaderLogManager) {
            ((ClassLoaderLogManager)logManager).setUseShutdownHook(false);
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "Shutdown is triggered. Stopping all the tools...");
                    if (!ServerTroubleShooterStarter.isShutdownInprogress) {
                        ServerTroubleShooterStarter.isShutdownInprogress = true;
                        deleteLockFile();
                        if (!ServerTroubleShooterStarter.isToolExecutionCompleted && !ServerTroubleShooterStarter.isToolSpecificInvocation) {
                            STSToolWorker.getInstance().stopExecution();
                        }
                    }
                    ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "===================");
                    ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "STS tool is stopped");
                    ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "===================");
                }
                finally {
                    final LogManager logManager = LogManager.getLogManager();
                    if (logManager instanceof ClassLoaderLogManager) {
                        ((ClassLoaderLogManager)logManager).shutdown();
                    }
                }
            }
        });
        ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "Shutdown hook is added");
    }
    
    private static void deleteLockFile() {
        final String lockFilePath = System.getProperty("ststool.home") + File.separator + STSToolConstants.LOCK_FILE;
        final File lockFile = new File(lockFilePath);
        ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "lock file deletion status : {0} ", lockFile.delete());
    }
    
    private static void stopSTSTool() {
        ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "Stopping...");
        if (!ServerTroubleShooterStarter.isShutdownInprogress) {
            ServerTroubleShooterStarter.isShutdownInprogress = true;
            deleteLockFile();
            System.exit(0);
        }
        else {
            ServerTroubleShooterStarter.LOGGER.log(Level.FINE, "Already shutdown in progress");
        }
    }
    
    static {
        LOGGER = Logger.getLogger("STSStarter");
        ServerTroubleShooterStarter.isShutdownInprogress = false;
        ServerTroubleShooterStarter.isToolSpecificInvocation = false;
        ServerTroubleShooterStarter.isToolExecutionCompleted = false;
        ServerTroubleShooterStarter.toolName = null;
    }
    
    private class LockFileMonitor extends Thread
    {
        @Override
        public void run() {
            final String lockFilePath = System.getProperty("ststool.home") + File.separator + STSToolConstants.LOCK_FILE;
            try {
                final File lockFile = new File(lockFilePath);
                long pollingIntevel = Long.parseLong(STSToolUtil.getSTSConfFileProps().getProperty("lockfile.check.interval").trim());
                pollingIntevel *= 1000L;
                do {
                    Thread.sleep(pollingIntevel);
                    if (!lockFile.exists()) {
                        ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "{0} file doest not exists. Triggering shutdown", lockFilePath);
                        stopSTSTool();
                    }
                } while (!ServerTroubleShooterStarter.isShutdownInprogress);
            }
            catch (final Exception ex) {
                ServerTroubleShooterStarter.LOGGER.log(Level.WARNING, "Caught exception while checking the presence of lock file : ", ex);
            }
            ServerTroubleShooterStarter.LOGGER.log(Level.INFO, "LockFile monitor stopped");
        }
    }
}
