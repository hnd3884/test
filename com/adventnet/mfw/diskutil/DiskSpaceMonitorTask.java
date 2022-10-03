package com.adventnet.mfw.diskutil;

import com.zoho.conf.Configuration;
import java.io.File;
import java.util.Iterator;
import java.util.Date;
import java.io.IOException;
import com.zoho.net.handshake.HandShakeUtil;
import com.adventnet.mfw.Starter;
import com.adventnet.mfw.ConsoleOut;
import java.util.logging.Level;
import java.util.ArrayList;
import com.adventnet.mfw.ServerInterface;
import java.util.List;
import java.util.logging.Logger;
import java.util.TimerTask;

public class DiskSpaceMonitorTask extends TimerTask
{
    static final Logger LOGGER;
    private long lowerLimit;
    private long upperLimit;
    private List<String> monitorPathsList;
    private ServerInterface serverInstance;
    private DiskSpaceMonitorHandler handler;
    private static String server_home;
    
    public DiskSpaceMonitorTask(final long lowerLimit, final long upperLimit, final String path, final DiskSpaceMonitorHandler handler) {
        this.lowerLimit = -1L;
        this.upperLimit = -1L;
        this.monitorPathsList = new ArrayList<String>();
        this.serverInstance = null;
        try {
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
            this.handler = handler;
            this.serverInstance = this.getServerInstance();
            this.parseInputPath((path != null) ? path : DiskSpaceMonitorTask.server_home);
        }
        catch (final Exception ex) {
            DiskSpaceMonitorTask.LOGGER.log(Level.INFO, "Exception occured while initializing diskSpace Monitoring task ");
            ex.printStackTrace();
        }
    }
    
    private boolean isDiskMonitoringHandlerEnabled() {
        return this.handler != null;
    }
    
    private void parseInputPath(final String path) {
        final String[] split;
        final String[] tokens = split = path.split(",");
        for (final String pathToMonitor : split) {
            if (pathToMonitor.length() > 0) {
                this.monitorPathsList.add(pathToMonitor);
            }
        }
    }
    
    private void shutDownServer(final boolean normal, final DiskSpaceStatus status) throws Exception {
        if (this.serverInstance != null) {
            ConsoleOut.println("DiskSpaceMonitor :: disk space is too low [Shutting down server]..free up disk and start again");
            DiskSpaceMonitorTask.LOGGER.log(Level.SEVERE, "Disk space is too low [Shutting down Server] :: " + status.toString());
            this.serverInstance.shutDown(normal);
        }
        else {
            this.shutDownDbServer(status);
        }
    }
    
    private void shutDownDbServer(final DiskSpaceStatus status) throws Exception {
        if (this.serverInstance != null) {
            DiskSpaceMonitorTask.LOGGER.log(Level.FINE, "Trying to shutdown DB Server......");
            this.serverInstance.stopDB();
        }
        ConsoleOut.println("DiskSpaceMonitor :: disk space is too low [Shutting down JVM]..free up disk and start again");
        DiskSpaceMonitorTask.LOGGER.log(Level.SEVERE, "Disk space is too low [ Shutting down JVM ] :: " + status.toString());
        Runtime.getRuntime().exit(-1);
    }
    
    private void processExitOperations(final DiskSpaceStatus status) throws Exception {
        if (Starter.isStarted()) {
            this.shutDownServer(true, status);
        }
        else if (HandShakeUtil.isServerListening()) {
            this.shutDownServer(false, status);
        }
        else {
            this.shutDownDbServer(status);
        }
    }
    
    private void processHandlerOperations(final DiskSpaceStatus status) throws IOException, SecurityException {
        if (this.isDiskMonitoringHandlerEnabled()) {
            this.handler.monitoredDiskUsage(status);
            if (status.getDiskSpaceStatusConstant().equals(DiskSpaceMonitorConstants.DISKSPACE_STATUS.CRITICAL_LIMIT_EXCEEDED)) {
                final DiskSpaceMonitorConstants.DISKSPACE_STATUS revalidateDiskSpace = this.handler.preInvokeServerShutDown();
                if (revalidateDiskSpace != null && revalidateDiskSpace.equals(DiskSpaceMonitorConstants.DISKSPACE_STATUS.REVALIDATE)) {
                    status.setMessage(this.checkDiskSpaceLimit(status.getMonitoringPath(), this.lowerLimit, this.upperLimit));
                }
            }
        }
    }
    
    @Override
    public void run() {
        try {
            for (final String monitoringPath : this.monitorPathsList) {
                final DiskSpaceStatus status = new DiskSpaceStatus(monitoringPath, new Date().getTime(), this.lowerLimit, this.upperLimit, this.checkDiskSpaceLimit(monitoringPath, this.lowerLimit, this.upperLimit));
                this.processHandlerOperations(status);
                if (status.getDiskSpaceStatusConstant().equals(DiskSpaceMonitorConstants.DISKSPACE_STATUS.CRITICAL_LIMIT_REACHED)) {
                    DiskSpaceMonitorTask.LOGGER.log(Level.WARNING, "Low disk space [Free Up Disk] :: " + status.toString());
                }
                else {
                    if (!status.getDiskSpaceStatusConstant().equals(DiskSpaceMonitorConstants.DISKSPACE_STATUS.CRITICAL_LIMIT_EXCEEDED)) {
                        continue;
                    }
                    this.cancel();
                    this.processExitOperations(status);
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    private ServerInterface getServerInstance() throws Exception {
        if (this.serverInstance == null) {
            this.serverInstance = Starter.getServerInstance();
            this.serverInstance = ((this.serverInstance != null) ? this.serverInstance : Starter.getNewServerClassInstance());
        }
        return this.serverInstance;
    }
    
    public DiskSpaceMonitorConstants.DISKSPACE_STATUS checkDiskSpaceLimit(final String pathToCheck, final long lowerLimit, final long upperLimit) throws IOException, SecurityException {
        final long freeSpace = new File(pathToCheck).getFreeSpace();
        if (freeSpace > lowerLimit && freeSpace < upperLimit) {
            return DiskSpaceMonitorConstants.DISKSPACE_STATUS.CRITICAL_LIMIT_REACHED;
        }
        if (freeSpace < lowerLimit) {
            return DiskSpaceMonitorConstants.DISKSPACE_STATUS.CRITICAL_LIMIT_EXCEEDED;
        }
        return DiskSpaceMonitorConstants.DISKSPACE_STATUS.THRESHOLD_NOT_REACHED;
    }
    
    static {
        LOGGER = Logger.getLogger(DiskSpaceMonitorTask.class.getName());
        DiskSpaceMonitorTask.server_home = ((Configuration.getString("server.home", ".") != null) ? Configuration.getString("server.home", ".") : Configuration.getString("app.home", "."));
    }
}
