package com.me.devicemanagement.onpremise.server.scheduler;

import java.util.regex.Matcher;
import java.io.FileWriter;
import java.util.regex.Pattern;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Collection;
import java.util.Properties;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

public class SchedulerTuning
{
    private static SchedulerTuning schedulerTuningObject;
    private static Logger schedulelogger;
    
    public static SchedulerTuning getInstance() {
        if (SchedulerTuning.schedulerTuningObject == null) {
            SchedulerTuning.schedulerTuningObject = new SchedulerTuning();
        }
        return SchedulerTuning.schedulerTuningObject;
    }
    
    public void checkChangesInThreadPoolsConf() {
        final LinkedHashMap threadPoolNewFileMap = new LinkedHashMap();
        final LinkedHashMap threadPoolNewDBMap = new LinkedHashMap();
        Boolean threadPoolUserTobeChanged = false;
        Boolean threadPoolUserCreated = false;
        try {
            final String threadPoolFileName = System.getProperty("server.home") + File.separator + SchedulerTuningUtil.THREAD_POOL_FILENAME;
            final String threadPoolUserFileName = System.getProperty("server.home") + File.separator + SchedulerTuningUtil.THREAD_POOL_USER_FILENAME;
            if (SchedulerTuningUtil.getInstance().isThreadPoolFilesChanged()) {
                SchedulerTuning.schedulelogger.log(Level.INFO, "ThreadPool settings are found to be changed.");
                final LinkedHashMap threadPoolBaseHMap = SchedulerTuningUtil.getInstance().getThreadPoolBaseHMap();
                final Properties threadPoolUserProps = SchedulerTuningUtil.getInstance().getThreadPoolUserProps();
                if (threadPoolBaseHMap != null && !threadPoolBaseHMap.isEmpty()) {
                    if (threadPoolUserProps == null || threadPoolUserProps.isEmpty()) {
                        threadPoolUserCreated = true;
                        SchedulerTuning.schedulelogger.log(Level.INFO, threadPoolUserFileName + " ,does not exist. This may be the first time of server restart, so we create a new threadpools-user.conf File");
                    }
                    final Collection collectLinkedHMap = threadPoolBaseHMap.keySet();
                    for (final String theardPoolBaseName : collectLinkedHMap) {
                        final Long threadPoolBaseSize = Long.valueOf(String.valueOf(threadPoolBaseHMap.get(theardPoolBaseName)));
                        final Long threadPoolUserSize = Long.valueOf(String.valueOf(threadPoolUserProps.getProperty(theardPoolBaseName, "0")));
                        if (threadPoolBaseSize > threadPoolUserSize) {
                            threadPoolUserTobeChanged = true;
                        }
                        final Long threadPoolNewSize = (threadPoolBaseSize > threadPoolUserSize) ? threadPoolBaseSize : threadPoolUserSize;
                        threadPoolNewFileMap.put(theardPoolBaseName + ".*=.*", theardPoolBaseName + "=" + threadPoolNewSize);
                        threadPoolNewDBMap.put(theardPoolBaseName, threadPoolNewSize);
                    }
                    if (threadPoolUserTobeChanged) {
                        if (threadPoolUserCreated && !ApiFactoryProvider.getFileAccessAPI().isFileExists(threadPoolUserFileName)) {
                            SchedulerTuning.schedulelogger.log(Level.INFO, "threadpools-User.conf File does not exist, so a new file is created: " + threadPoolUserFileName);
                            ApiFactoryProvider.getFileAccessAPI().createNewFile(threadPoolUserFileName);
                        }
                        this.findAndReplaceStringInFile(threadPoolUserFileName, threadPoolNewFileMap);
                        final Properties threadPoolLMTProps = SchedulerTuningUtil.getInstance().getThreadPoolFilesCurrentLMTProperty();
                        SchedulerTuningUtil.getInstance().saveThreadPoolFileLastModifiedTimeProps(threadPoolLMTProps);
                        SchedulerTuning.schedulelogger.log(Level.INFO, "Last Modified Time of threadpools-User.conf File is updated in the sc.modtime");
                    }
                    final Boolean threadPoolDBChanged = SchedulerTuningUtil.getInstance().updateThreadPoolTable(threadPoolNewDBMap);
                    if (threadPoolDBChanged) {
                        SchedulerTuning.schedulelogger.log(Level.INFO, "###################### Need to Restart the Desktop Central Service since Mickey Scheduler ThreaPool Table has been Modified ########################");
                    }
                    else {
                        SchedulerTuning.schedulelogger.log(Level.INFO, " No Change Exists between the Values of the ThreadPool Database Table and ThreadPool-Users.conf File");
                    }
                }
                else {
                    SchedulerTuning.schedulelogger.log(Level.INFO, threadPoolFileName + " ,does not exist. So, tuning of Mickey Scheduler cannot be performed");
                }
            }
            else {
                SchedulerTuning.schedulelogger.log(Level.INFO, "ThreadPool settings are found not be changed. No need to regenerate the conf files or update the DB...");
            }
        }
        catch (final Exception ex) {
            SchedulerTuning.schedulelogger.log(Level.WARNING, "Caught exception while checking whether ThreadPools.xml or ThreadPools-User.conf is modified ?", ex);
        }
    }
    
    private void findAndReplaceStringInFile(final String fileName, final LinkedHashMap linkedHMap) throws Exception {
        String findStr = null;
        String replaceStr = null;
        FileReader filereader = null;
        FileWriter filewriter = null;
        try {
            filereader = new FileReader(fileName);
            int read = 0;
            final char[] chBuf = new char[500];
            final StringBuilder strBuilder = new StringBuilder();
            while ((read = filereader.read(chBuf)) > -1) {
                strBuilder.append(chBuf, 0, read);
            }
            filereader.close();
            String finalStr = strBuilder.toString();
            final Collection collectLinkedHMap = linkedHMap.keySet();
            final Iterator iterate = collectLinkedHMap.iterator();
            while (iterate.hasNext()) {
                findStr = iterate.next();
                replaceStr = linkedHMap.get(findStr);
                final Pattern findStrPattern = Pattern.compile(findStr);
                final Matcher matcher = findStrPattern.matcher(finalStr);
                if (matcher.find()) {
                    finalStr = finalStr.replaceAll(findStr, replaceStr);
                }
                else {
                    finalStr = finalStr.concat("\n\n" + replaceStr);
                }
            }
            filewriter = new FileWriter(fileName, false);
            filewriter.write(finalStr, 0, finalStr.length());
        }
        catch (final Exception ex) {
            SchedulerTuning.schedulelogger.log(Level.WARNING, "Caught exception in findAndReplaceStringInFile() with fileName: " + fileName + " with Properties" + linkedHMap, ex);
            throw ex;
        }
        finally {
            if (filereader != null) {
                filereader.close();
            }
            if (filewriter != null) {
                filewriter.close();
            }
        }
    }
    
    static {
        SchedulerTuning.schedulerTuningObject = null;
        SchedulerTuning.schedulelogger = Logger.getLogger(SchedulerTuning.class.getName());
    }
}
