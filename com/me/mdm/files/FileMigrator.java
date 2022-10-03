package com.me.mdm.files;

import java.util.Hashtable;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import org.apache.commons.lang3.time.DurationFormatUtils;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.mdm.server.doc.DocMgmtConstants;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.io.File;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class FileMigrator implements SchedulerExecutionInterface
{
    private ArrayList<String> getFileNames(final ArrayList<String> fileNames) {
        final StringBuilder sb = new StringBuilder();
        final ArrayList<String> resFileNames = new ArrayList<String>();
        for (final String fileName : fileNames) {
            final String[] splitStr = fileName.split(Pattern.quote(File.separator));
            sb.append(splitStr[splitStr.length - 1]);
            sb.append(",");
            resFileNames.add(splitStr[splitStr.length - 1]);
        }
        SyMLogger.log("FileServletLog", Level.INFO, "fileNames : {0}", (Object[])new String[] { sb.toString() });
        return resFileNames;
    }
    
    public void executeTask(final Properties props) {
        final String taskName = String.valueOf(((Hashtable<K, Object>)props).get("taskName"));
        try {
            final String migrationFolder = MDMUtil.getSyMParameter(taskName);
            String newDirectory = null;
            String oldDirectory = null;
            SyMLogger.log("FileServletLog", Level.INFO, "taskName {0} : Migration folder {1}", (Object[])new String[] { taskName, migrationFolder });
            if (!MDMStringUtils.isEmpty(migrationFolder)) {
                if (migrationFolder.equalsIgnoreCase("docrepository")) {
                    newDirectory = DocMgmtConstants.DOC_BASE_DIRECTORY + DocMgmtConstants.DOC_FILE_DIRECTORY;
                    oldDirectory = DocMgmtConstants.OLD_DOC_BASE_DIRECTORY + DocMgmtConstants.DOC_FILE_DIRECTORY;
                }
                else if (migrationFolder.equalsIgnoreCase("apprepository")) {
                    newDirectory = AppMgmtConstants.APP_BASE_PATH + AppMgmtConstants.APP_FILE_DIRECTORY;
                    oldDirectory = AppMgmtConstants.OLD_APP_BASE_PATH + AppMgmtConstants.APP_FILE_DIRECTORY;
                }
                final ArrayList<String> oldDirFilesList = ApiFactoryProvider.getFileAccessAPI().getAllFilesList(oldDirectory, (String)null, (String)null);
                final Long copyFilesStartedAt = System.currentTimeMillis();
                SyMLogger.log("FileServletLog", Level.INFO, "starting files migration at : {0}", (Object[])new String[] { DateTimeUtil.longdateToString((long)copyFilesStartedAt) });
                ApiFactoryProvider.getFileAccessAPI().copyDirectory(oldDirectory, newDirectory);
                final Long copyFilesEndedAt = System.currentTimeMillis();
                SyMLogger.log("FileServletLog", Level.INFO, "starting files migration at : {0},duration {1}", (Object[])new String[] { DateTimeUtil.longdateToString((long)copyFilesStartedAt), DurationFormatUtils.formatDurationHMS(copyFilesEndedAt - copyFilesStartedAt) });
                final ArrayList<String> newDirFilesList = ApiFactoryProvider.getFileAccessAPI().getAllFilesList(newDirectory, (String)null, (String)null);
                boolean filesMigrated = false;
                if (oldDirFilesList != null && newDirFilesList != null) {
                    final ArrayList<String> newDirFileNames = this.getFileNames(newDirFilesList);
                    final ArrayList<String> oldDirFileNames = this.getFileNames(oldDirFilesList);
                    SyMLogger.log("FileServletLog", Level.INFO, "oldDirFilesList : {0}, newDirFilesList : {1}", (Object[])new String[] { String.valueOf(oldDirFilesList.size()), String.valueOf(newDirFilesList.size()) });
                    Collections.sort(oldDirFileNames);
                    Collections.sort(newDirFileNames);
                    filesMigrated = newDirFileNames.containsAll(oldDirFileNames);
                }
                else {
                    SyMLogger.log("FileServletLog", Level.INFO, "oldDirFilesList : {0}, newDirFilesList : {1}", new Object[] { oldDirFilesList, newDirFilesList });
                }
                SyMLogger.log("FileServletLog", Level.INFO, "filesMigrated : {0}", new Object[] { filesMigrated });
                if (filesMigrated) {
                    final Long deleteOldDirStartedAt = System.currentTimeMillis();
                    SyMLogger.log("FileServletLog", Level.INFO, "deleting old Dir at : {0}", (Object[])new String[] { DateTimeUtil.longdateToString((long)deleteOldDirStartedAt) });
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(oldDirectory);
                    final Long deleteOldDirEndedAt = System.currentTimeMillis();
                    SyMLogger.log("FileServletLog", Level.INFO, "deleted old Dir at : {0},duration {1}", (Object[])new String[] { DateTimeUtil.longdateToString((long)deleteOldDirEndedAt), DurationFormatUtils.formatDurationHMS(deleteOldDirEndedAt - deleteOldDirStartedAt) });
                    MDMUtil.deleteSyMParameter(taskName);
                    if (taskName.equalsIgnoreCase("migrateAppStaticFiles")) {
                        AppsUtil.getInstance().regenerateManifestFile();
                    }
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.log("FileServletLog", Level.SEVERE, "Exception in file migration  task", (Throwable)ex);
        }
    }
}
