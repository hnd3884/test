package com.me.devicemanagement.onpremise.tools.backuprestore.action;

import java.util.List;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.FileUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.handler.BackUpHandler;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.io.InputStream;
import java.util.Properties;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import java.util.logging.Level;
import org.w3c.dom.Document;
import java.util.logging.Logger;

public class DMFileBackup
{
    private static final Logger LOGGER;
    private static DMFileBackup dmFileBackupObj;
    private String serverHome;
    
    public static DMFileBackup getInstance() {
        if (DMFileBackup.dmFileBackupObj == null) {
            DMFileBackup.dmFileBackupObj = new DMFileBackup();
        }
        return DMFileBackup.dmFileBackupObj;
    }
    
    public DMFileBackup() {
        this.serverHome = System.getProperty("server.home");
    }
    
    public Document getDoc() throws Exception {
        DMFileBackup.LOGGER.log(Level.INFO, "XML LOCATION\t::\t{0}", "/conf/backup-files.xml");
        final Document doc = BackupRestoreUtil.getInstance().parseXML(this.getBackupXMLInputStream());
        return doc;
    }
    
    public HashMap<Integer, Properties> getBackupList() throws Exception {
        final HashMap<Integer, Properties> backupList = BackupRestoreUtil.getInstance().getFileListFromXML(this.getDoc());
        DMFileBackup.LOGGER.log(Level.FINE, "Backup List parsed from XML :: {0}", backupList);
        return backupList;
    }
    
    private InputStream getBackupXMLInputStream() {
        return this.getClass().getResourceAsStream("/conf/backup-files.xml");
    }
    
    public void takeFileBackupList(final HashMap<Integer, Properties> backupList, final String destination) throws Exception {
        for (final Map.Entry<Integer, Properties> entry : backupList.entrySet()) {
            final Properties backupProps = entry.getValue();
            this.backupFile(backupProps, destination);
        }
        DMFileBackup.LOGGER.log(Level.INFO, "*********************************************************************************************");
    }
    
    private void backupFile(final Properties backupProps, final String destination) throws Exception {
        final String handlerClass = backupProps.getProperty("handler_class");
        final String filePath = backupProps.getProperty("file_path");
        final String backupTypes = backupProps.getProperty("backup_types");
        final boolean ignoreError = backupProps.getProperty("ignore_error").equalsIgnoreCase("true");
        long size = 0L;
        File completeFilePath = null;
        final File actualPath = new File(this.serverHome, filePath);
        DMFileBackup.LOGGER.log(Level.INFO, "****************** [ {0} ] ******************", filePath);
        if (!actualPath.exists() && !ignoreError) {
            DMFileBackup.LOGGER.log(Level.WARNING, "File {0} does not exist! ignore_error is false. Hence returning.", filePath);
            final String displayName = new BackupRestoreUtil().getValueFromGenProps("displayname");
            throw BackupRestoreUtil.createException(-13, new Object[] { displayName }, null);
        }
        if (handlerClass == null && backupTypes.equalsIgnoreCase("copy")) {
            backupProps.setProperty("backup_type", "copy");
            completeFilePath = actualPath;
        }
        else {
            DMFileBackup.LOGGER.log(Level.INFO, "\tHandler Class\t::\t{0}", handlerClass);
            if (BackUpHandler.class.isAssignableFrom(Class.forName(handlerClass))) {
                backupProps.remove("handler_class");
                final BackUpHandler obj = (BackUpHandler)Class.forName(handlerClass).newInstance();
                List<String> filesToBeMoved = null;
                try {
                    filesToBeMoved = obj.backUpFile(destination);
                    if (!filesToBeMoved.isEmpty()) {
                        final StringBuilder sb = new StringBuilder();
                        final Iterator it = filesToBeMoved.iterator();
                        if (it.hasNext()) {
                            sb.append(it.next());
                            while (it.hasNext()) {
                                sb.append(',').append(it.next());
                            }
                        }
                        backupProps.setProperty("backup_type", "copy");
                        backupProps.setProperty("file_path", sb.toString());
                        DMFileBackup.LOGGER.info("Certificate Files moved.." + filesToBeMoved.toString());
                        completeFilePath = new File(this.serverHome + File.separator + filesToBeMoved.get(0));
                    }
                    else {
                        DMFileBackup.LOGGER.info("No certificates found to back up..");
                    }
                }
                catch (final Exception ex) {
                    DMFileBackup.LOGGER.log(Level.SEVERE, "Exception in backing up the certificates..", ex);
                }
            }
        }
        DMFileBackup.LOGGER.log(Level.INFO, "\tComplete path\t::\t{0}", completeFilePath);
        size = FileUtil.getFileOrFolderSize(completeFilePath);
        DMFileBackup.LOGGER.log(Level.INFO, "\tSize\t\t::\t{0}", size);
        backupProps.setProperty("size", "" + size);
    }
    
    static {
        LOGGER = Logger.getLogger("ScheduleDBBackup");
        DMFileBackup.dmFileBackupObj = null;
    }
}
