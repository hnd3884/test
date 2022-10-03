package com.me.devicemanagement.onpremise.tools.servertroubleshooter.difftool;

import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.schedule.ScheduledWorkerTask;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.schedule.ScheduledWorker;
import java.util.concurrent.TimeUnit;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.difftool.util.DiffGenerator;
import java.io.File;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.difftool.util.DiffToolUtil;
import java.util.Calendar;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.STSInterface;

public class DiffToolAction implements STSInterface
{
    private static final Logger LOGGER;
    private static int invocationType;
    private static Calendar invocationTime;
    
    private void loadConfValues() {
        try {
            final Properties diffToolProps = DiffToolUtil.getDiffToolProps();
            if (diffToolProps.getProperty("difftool.invocation.type") != null) {
                DiffToolAction.invocationType = Integer.parseInt(diffToolProps.getProperty("difftool.invocation.type").trim());
                if (DiffToolAction.invocationType == 1) {
                    final String scheduleTimeString = diffToolProps.getProperty("difftool.schedule.runtime").trim();
                    this.setScheduleTime(scheduleTimeString);
                }
            }
        }
        catch (final Exception ex) {
            DiffToolAction.LOGGER.log(Level.WARNING, "Caught exception while loading conf values ", ex);
        }
    }
    
    private void setScheduleTime(final String scheduleTime) {
        final String[] scheduleTimeString = scheduleTime.split(":");
        final int hours = Integer.parseInt(scheduleTimeString[0].trim());
        final int mins = Integer.parseInt(scheduleTimeString[1].trim());
        final int seconds = Integer.parseInt(scheduleTimeString[2].trim());
        (DiffToolAction.invocationTime = Calendar.getInstance()).set(11, hours);
        DiffToolAction.invocationTime.set(12, mins);
        DiffToolAction.invocationTime.set(13, seconds);
        if (new Date().compareTo(DiffToolAction.invocationTime.getTime()) >= 0) {
            DiffToolAction.invocationTime.add(5, 1);
        }
    }
    
    private boolean isUpdMgrInvoked() {
        boolean returnValue = false;
        File flagFile = null;
        try {
            DiffToolAction.LOGGER.log(Level.FINE, "Checking for {0} file to run DiffTool", "UpdmgrInvoked.flag");
            final String flagFilePath = System.getProperty("server.home") + File.separator + "bin" + File.separator + "UpdmgrInvoked.flag";
            flagFile = new File(flagFilePath);
            returnValue = flagFile.exists();
        }
        catch (final Exception ex) {
            DiffToolAction.LOGGER.log(Level.WARNING, "Caught exception in checking UpdMgr flag file: ", ex);
        }
        DiffToolAction.LOGGER.log(Level.INFO, "{0} file presence : {1}", new Object[] { "UpdmgrInvoked.flag", returnValue });
        return returnValue;
    }
    
    private void waitForDiffGeneration(final DiffGenerator diffGenerator) throws InterruptedException {
        DiffToolAction.LOGGER.log(Level.INFO, "Waiting Diff tool execution...");
        do {
            Thread.sleep(TimeUnit.MINUTES.toMillis(5L));
        } while (!DiffGenerator.isExecutionCompleted);
    }
    
    @Override
    public void startTool() {
        try {
            DiffToolAction.LOGGER.log(Level.INFO, "Inside Diff tool");
            if (this.isUpdMgrInvoked()) {
                this.loadConfValues();
                final DiffGenerator diffGenerator = new DiffGenerator();
                if (DiffToolAction.invocationType == 1) {
                    ScheduledWorker.getInstance().scheduleOnce(diffGenerator, DiffToolAction.invocationTime);
                    DiffToolAction.LOGGER.log(Level.INFO, "Diff tool has been scheduled on {0}. waiting for completion...", DiffToolAction.invocationTime.getTime());
                    this.waitForDiffGeneration(diffGenerator);
                }
                else {
                    diffGenerator.run();
                }
            }
            DiffToolAction.LOGGER.log(Level.INFO, "Exiting from Diff tool");
        }
        catch (final Exception ex) {
            DiffToolAction.LOGGER.log(Level.WARNING, "Caught exception in executing DiffTool: ", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("DiffToolLogger");
        DiffToolAction.invocationType = 2;
        DiffToolAction.invocationTime = null;
    }
}
