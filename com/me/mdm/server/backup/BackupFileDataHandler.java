package com.me.mdm.server.backup;

import com.adventnet.persistence.DataAccess;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackupFileDataHandler
{
    public static final Logger logger;
    
    public void addBackupDataFilesInfo(final BackupFileData backupFileData) {
        try {
            BackupFileDataHandler.logger.log(Level.INFO, "Backup file data update initiated");
            final DataObject cleanUpDO = MDMUtil.getPersistence().constructDataObject();
            final Row backupFileRow = new Row("DCCleanUpDataFiles");
            backupFileRow.set("FEATURE_ID", (Object)backupFileData.getFeatureId());
            backupFileRow.set("FILE_NAME", (Object)backupFileData.getFileName());
            backupFileRow.set("CREATED_TIME", (Object)backupFileData.getCreatedTime());
            backupFileRow.set("FILE_SIZE", (Object)backupFileData.getFileSize());
            cleanUpDO.addRow(backupFileRow);
            MDMUtil.getPersistence().add(cleanUpDO);
            BackupFileDataHandler.logger.log(Level.INFO, "Backup file data update completed", backupFileData.toString());
        }
        catch (final Exception ex) {
            BackupFileDataHandler.logger.log(Level.WARNING, "Exception occurred while addCleanupDataFiles", ex);
        }
    }
    
    public List<BackupFileData> getBackupFileInfoFromBelowCreatedTime(final Long elapsedTime) {
        List<BackupFileData> backupFileList = null;
        try {
            final Criteria elapsedTimeCrit = new Criteria(Column.getColumn("DCCleanUpDataFiles", "CREATED_TIME"), (Object)elapsedTime, 7);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCCleanUpDataFiles"));
            selectQuery.setCriteria(elapsedTimeCrit);
            selectQuery.addSelectColumn(Column.getColumn("DCCleanUpDataFiles", "DATA_FILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DCCleanUpDataFiles", "FEATURE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DCCleanUpDataFiles", "FILE_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("DCCleanUpDataFiles", "FILE_SIZE"));
            selectQuery.addSelectColumn(Column.getColumn("DCCleanUpDataFiles", "CREATED_TIME"));
            final DataObject cleanupFileDO = MDMUtil.getReadOnlyPersistence().get(selectQuery);
            if (!cleanupFileDO.isEmpty()) {
                backupFileList = new ArrayList<BackupFileData>();
                final Iterator cleanUpIterator = cleanupFileDO.getRows("DCCleanUpDataFiles");
                while (cleanUpIterator.hasNext()) {
                    final Row cleanUpFilerow = cleanUpIterator.next();
                    final BackupFileData backupFileData = new BackupFileData();
                    backupFileData.addFeatureId((Long)cleanUpFilerow.get("FEATURE_ID"));
                    backupFileData.addFileName((String)cleanUpFilerow.get("FILE_NAME"));
                    backupFileData.addFileSize((Long)cleanUpFilerow.get("FILE_SIZE"));
                    backupFileData.addCreatedTime((Long)cleanUpFilerow.get("CREATED_TIME"));
                    backupFileList.add(backupFileData);
                }
            }
        }
        catch (final Exception ex) {
            BackupFileDataHandler.logger.log(Level.WARNING, "Exception occurred while getBackupFileInfoFromBelowCreatedTime", ex);
        }
        return backupFileList;
    }
    
    public void deleteDataFromBackupDB(final Criteria criteria) {
        try {
            DataAccess.delete("DCCleanUpDataFiles", criteria);
        }
        catch (final Exception ex) {
            BackupFileDataHandler.logger.log(Level.WARNING, "Exception occurred while deleteDataFromBackupDB", ex);
        }
    }
    
    static {
        logger = Logger.getLogger("MDMLogger");
    }
}
