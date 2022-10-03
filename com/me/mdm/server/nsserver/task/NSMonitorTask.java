package com.me.mdm.server.nsserver.task;

import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import com.me.devicemanagement.onpremise.start.util.NSStartUpUtil;
import com.me.mdm.server.nsserver.NSUtil;
import java.util.logging.Level;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class NSMonitorTask extends Thread
{
    private Logger logger;
    private Socket clientsocket;
    private int nsPortNumber;
    private String nsServerName;
    InputStream in;
    private int failureCount;
    private int maxFailureCount;
    private boolean suspendFlag;
    private static NSMonitorTask nsMonitorTask;
    private static boolean nsMonitoringState;
    private boolean nsSuspendTask;
    private long nsMonitoringStartTime;
    private Logger nsCrashLogger;
    
    public NSMonitorTask() {
        this.logger = Logger.getLogger("NSControllerLogger");
        this.clientsocket = null;
        this.nsPortNumber = 0;
        this.nsServerName = "localhost";
        this.in = null;
        this.failureCount = 0;
        this.maxFailureCount = 3;
        this.suspendFlag = false;
        this.nsSuspendTask = false;
        this.nsMonitoringStartTime = 0L;
        this.nsCrashLogger = Logger.getLogger("NSCrashLogger");
    }
    
    public static NSMonitorTask getInstance() {
        if (NSMonitorTask.nsMonitorTask == null || !NSMonitorTask.nsMonitoringState) {
            NSMonitorTask.nsMonitorTask = new NSMonitorTask();
        }
        return NSMonitorTask.nsMonitorTask;
    }
    
    @Override
    public void run() {
        this.logger.log(Level.INFO, "NSMonitorTask.run() has been invoked.");
        try {
            this.nsPortNumber = NSUtil.getInstance().getNSPort();
            this.nsSuspendTask = false;
            final long monitoringIntervalTime = NSUtil.getInstance().getNSMonitoringInterval();
            this.maxFailureCount = NSUtil.getInstance().getMaxNSFailureCount();
            this.reregister();
            this.nsMonitoringStartTime = System.currentTimeMillis();
            NSMonitorTask.nsMonitoringState = true;
            this.nsCrashLogger.log(Level.INFO, "BUILDNO \t CRASH_TIME \t FAILURE_COUNT \t REMARKS");
            this.nsCrashLogger.log(Level.INFO, "-------\t----------\t-------------\t-------");
            while (true) {
                if (this.failureCount >= this.maxFailureCount) {
                    if (NSMonitorTask.nsMonitoringState) {
                        break;
                    }
                }
                try {
                    this.monitorNSServer();
                }
                catch (final Exception exp) {
                    this.logger.log(Level.WARNING, "Exception occurred during the NSMonitoring {0}", exp);
                    if (this.nsSuspendTask) {
                        this.logger.log(Level.INFO, "Suspend flag is set ...");
                        break;
                    }
                    ++this.failureCount;
                    this.addorupdateNSCrashInfo();
                    this.logger.log(Level.INFO, "The failure count is given by {0}", new Integer(this.failureCount));
                    final long currentTime = System.currentTimeMillis();
                    if (currentTime - this.nsMonitoringStartTime <= monitoringIntervalTime) {
                        if (this.failureCount >= this.maxFailureCount) {
                            this.logger.log(Level.INFO, "Maximum failure Count has been reached , So we are going to switch to previous flow...");
                            NSUtil.getInstance().disableNS();
                            break;
                        }
                    }
                    else {
                        this.logger.log(Level.INFO, "Time difference has exceeded the monitoring interval time, reseting start time and failure count");
                        this.nsMonitoringStartTime = currentTime;
                        this.failureCount = 0;
                    }
                    Thread.sleep(30000L);
                    int exitcode = -1;
                    exitcode = NSStartUpUtil.startNSServer();
                    if (exitcode != 0) {
                        this.nsCrashLogger.log(Level.INFO, " -- \t -- \t -- \tExitCode : {0}", exitcode);
                    }
                    this.logger.log(Level.INFO, "Exit code for StartNSServer inside NSMonitorTask : {0}", exitcode);
                    this.reregister();
                }
            }
        }
        catch (final Exception exp2) {
            this.logger.log(Level.WARNING, "Exception occurred during the register of NSMonitoring{0}", exp2);
        }
    }
    
    public boolean getMonitoringState() {
        return NSMonitorTask.nsMonitoringState;
    }
    
    private void monitorNSServer() throws IOException, InterruptedException, Exception {
        this.in = new DataInputStream(new BufferedInputStream(this.clientsocket.getInputStream()));
        final byte[] readData = new byte[100];
        if (this.in == null) {
            return;
        }
        while (true) {
            this.logger.log(Level.INFO, "Inside the monitoring loop");
            try {
                this.in.read(readData, 0, 100);
            }
            catch (final Exception e) {
                try {
                    if (this.clientsocket != null) {
                        this.logger.log(Level.INFO, "Isclosed : {0} isConnected : {1} isOutputShutdown : {2}", new Object[] { this.clientsocket.isClosed(), this.clientsocket.isConnected(), this.clientsocket.isOutputShutdown() });
                    }
                }
                catch (final Exception e2) {
                    this.logger.log(Level.INFO, "Exception caught during clientsocket read :{0}", e2);
                }
                throw e;
            }
            Thread.sleep(500L);
        }
    }
    
    public void reregister() throws UnknownHostException, IOException, InterruptedException {
        try {
            this.register();
        }
        catch (final Exception exp1) {
            this.logger.log(Level.INFO, "Exception occurred during first attempt to re-register NSMonitoring {0}", exp1);
            Thread.sleep(10000L);
            try {
                this.register();
            }
            catch (final Exception exp2) {
                this.logger.log(Level.INFO, "Exception occurred during second attempt to re-register NSMonitoring {0}", exp2);
                Thread.sleep(10000L);
                this.register();
            }
        }
    }
    
    public void register() throws UnknownHostException, IOException {
        final String register = "/m";
        OutputStream out = null;
        this.nsPortNumber = NSUtil.getInstance().getNSPort();
        this.clientsocket = new Socket(this.nsServerName, this.nsPortNumber);
        this.in = null;
        out = this.clientsocket.getOutputStream();
        out.write(register.getBytes());
        out.flush();
    }
    
    public void suspendTask() {
        this.logger.log(Level.INFO, "Terminating the NS Monitor Task...");
        NSMonitorTask.nsMonitoringState = false;
        this.nsSuspendTask = true;
        try {
            if (this.clientsocket != null) {
                this.clientsocket.close();
                this.clientsocket = null;
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public boolean addorupdateNSCrashInfo() throws SyMException {
        try {
            final BackupRestoreUtil util = new BackupRestoreUtil();
            final int buildno = Integer.parseInt(util.getBuildNumber());
            final long currentCrashTime = new Long(System.currentTimeMillis());
            this.nsCrashLogger.log(Level.INFO, "{0}\t{1}\t{2}\t  -- ", new Object[] { buildno, currentCrashTime, this.failureCount });
            final Row r = new Row("NSCrashInfo");
            r.set(2, (Object)buildno);
            r.set(3, (Object)currentCrashTime);
            final DataObject dobj = (DataObject)new WritableDataObject();
            dobj.addRow(r);
            DataAccess.add(dobj);
            this.cleanOldTableEntry("NSCrashInfo");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occurred during Adding details to NSCrashInfo :  ", e);
            return false;
        }
        return true;
    }
    
    public void cleanOldTableEntry(final String tablename) {
        try {
            int row_count = 0;
            row_count = DBUtil.getRecordActualCount("NSCrashInfo", "BUILD_NUMBER", (Criteria)null);
            if (row_count > 100) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("NSCrashInfo"));
                selectQuery.addSelectColumn(new Column((String)null, "*"));
                final DataObject dobj1 = SyMUtil.getPersistence().get(selectQuery);
                final Iterator rows = dobj1.getRows("NSCrashInfo");
                if (rows.hasNext()) {
                    final Row row = rows.next();
                    dobj1.deleteRow(row);
                    SyMUtil.getPersistence().update(dobj1);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occurred during Cleaning process in NSCrashInfo Table :  ", e);
        }
    }
    
    static {
        NSMonitorTask.nsMonitorTask = null;
        NSMonitorTask.nsMonitoringState = false;
    }
}
