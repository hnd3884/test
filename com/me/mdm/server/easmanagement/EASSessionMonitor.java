package com.me.mdm.server.easmanagement;

import java.util.Iterator;
import com.me.mdm.server.easmanagement.pss.PSSMgmt;
import java.util.HashSet;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.MemoryOnlyDCQueueDataProcessor;

public class EASSessionMonitor extends MemoryOnlyDCQueueDataProcessor
{
    private boolean monitor;
    private int sessionMonitorThreadCount;
    private static EASSessionMonitor easSessionProcessor;
    
    public EASSessionMonitor() {
        this.monitor = false;
        this.sessionMonitorThreadCount = 0;
    }
    
    public static EASSessionMonitor getInstance() {
        if (EASSessionMonitor.easSessionProcessor == null) {
            EASSessionMonitor.easSessionProcessor = new EASSessionMonitor();
        }
        return EASSessionMonitor.easSessionProcessor;
    }
    
    public synchronized int getSessionMonitorThreadCount() {
        final EASSessionMonitor tempSessionMonitor = getInstance();
        EASMgmt.logger.log(Level.INFO, " sessionMonitorThread count = {0}", tempSessionMonitor.sessionMonitorThreadCount);
        return tempSessionMonitor.sessionMonitorThreadCount;
    }
    
    public synchronized void increaseSessionMonitorThreadCount() {
        final EASSessionMonitor instance;
        final EASSessionMonitor tempSessionMonitor = instance = getInstance();
        ++instance.sessionMonitorThreadCount;
        EASMgmt.logger.log(Level.INFO, " sessionMonitorThread count incremented = {0}", tempSessionMonitor.sessionMonitorThreadCount);
    }
    
    private void decrementSessionMonitorThreadCount() {
        final EASSessionMonitor tempSessionMonitor = getInstance();
        if (tempSessionMonitor.sessionMonitorThreadCount > 0) {
            final EASSessionMonitor easSessionMonitor = tempSessionMonitor;
            --easSessionMonitor.sessionMonitorThreadCount;
        }
        EASMgmt.logger.log(Level.INFO, " sessionMonitorThread count decremented = {0}", tempSessionMonitor.sessionMonitorThreadCount);
    }
    
    public void processData(final DCQueueData qData) {
        final EASSessionMonitor tempSessionMonitor = getInstance();
        try {
            tempSessionMonitor.monitor = true;
            while (tempSessionMonitor.monitor) {
                tempSessionMonitor.monitor = false;
                final HashSet<Long> serverSessionMap = (HashSet<Long>)PSSMgmt.getInstance().getPSSSessionSet().clone();
                if (serverSessionMap != null) {
                    final Iterator<Long> iterator = serverSessionMap.iterator();
                    while (iterator.hasNext() && !tempSessionMonitor.monitor) {
                        Long easServerID = null;
                        easServerID = iterator.next();
                        if (easServerID != null) {
                            final Integer pssState = PSSMgmt.getInstance().getPSSstate(easServerID);
                            EASMgmt.logger.log(Level.FINE, "finding out about {0}  {1}", new Object[] { easServerID, pssState });
                            if (pssState == 1) {
                                if (PSSMgmt.getInstance().isSessionInactive(easServerID)) {
                                    EASMgmt.logger.log(Level.INFO, "closing the session for {0} not forcibly", easServerID);
                                    PSSMgmt.getInstance().closeSession(easServerID);
                                    iterator.remove();
                                }
                                else {
                                    tempSessionMonitor.monitor = true;
                                }
                            }
                            else if (pssState == 0 && PSSMgmt.getInstance().getPendingTaskCount(easServerID) > 0) {
                                EASMgmt.logger.log(Level.SEVERE, "starting a session from session monitor!!!!!!!!!!!!!!!!!!.. should not have started from here");
                            }
                            else {
                                if (pssState == 1 || PSSMgmt.getInstance().getPendingTaskCount(easServerID) != 0) {
                                    continue;
                                }
                                EASMgmt.logger.log(Level.INFO, "thread executed.. but no pending tasks for the server {0} session dead anyway", easServerID);
                                if (!PSSMgmt.getInstance().isSessionInactive(easServerID)) {
                                    continue;
                                }
                                PSSMgmt.getInstance().closeSession(easServerID);
                                iterator.remove();
                            }
                        }
                    }
                }
                Thread.sleep(3000L);
            }
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.SEVERE, null, ex);
        }
        tempSessionMonitor.decrementSessionMonitorThreadCount();
    }
    
    static {
        EASSessionMonitor.easSessionProcessor = null;
    }
}
