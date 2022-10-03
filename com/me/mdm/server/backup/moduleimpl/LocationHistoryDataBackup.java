package com.me.mdm.server.backup.moduleimpl;

import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.persistence.Row;
import com.me.mdm.server.util.CalendarUtil;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import java.util.List;
import com.me.mdm.server.backup.BackupFileData;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.backup.BackupFileDataHandler;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.GroupByClause;
import com.me.devicemanagement.framework.server.util.ReadOnlyDBUtil;
import com.adventnet.ds.query.Operation;
import com.me.mdm.server.settings.location.LocationSettingsDataHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;
import com.me.mdm.server.backup.MDMDataBackupRunner;

public class LocationHistoryDataBackup extends MDMDataBackupRunner
{
    private final String LOCATION_HISTORY_BACKUP = "LocationHistory";
    private Long backUpDate;
    public static int MAX_DAYS_OF_LOCATION_HISTORY;
    private int MAX_DAYS_OF_BACKUP_FILES;
    public static final Logger logger;
    
    public LocationHistoryDataBackup() {
        this.backUpDate = -1L;
        this.MAX_DAYS_OF_BACKUP_FILES = 88;
        this.moduleName = "LocationHistory";
        final Calendar c = Calendar.getInstance();
        c.set(5, 1);
        this.backUpDate = this.getBackupCriteriaTime();
    }
    
    @Override
    protected String getBackupFilePath() {
        final String basePath = this.backupProperties.optString("BACKUP_LOCATION", (String)null);
        String path = "";
        if (basePath != null) {
            String fileName = this.backupProperties.optString("FILE_NAME");
            final SimpleDateFormat e = new SimpleDateFormat("-yy-MM-dd-HH-mm-ss-SSS");
            fileName = fileName.replace("-date", e.format(new Date()));
            fileName = this.customerId + File.separator + fileName;
            this.fullFileName = fileName;
            path = System.getProperty("server.home") + File.separator + basePath + File.separator + fileName;
        }
        return path;
    }
    
    @Override
    protected SelectQuery constructBackupQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
        final Join customerResJoin = new Join("MdDeviceLocationDetails", "Resource", new String[] { "DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2);
        selectQuery.addJoin(customerResJoin);
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LATITUDE"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LONGITUDE"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
        final Criteria elapsedCriteria = new Criteria(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"), (Object)this.backUpDate, 7);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerId, 0);
        final Criteria criteria = elapsedCriteria.and(customerCriteria);
        selectQuery.setCriteria(criteria);
        final SortColumn sortColumn = new SortColumn(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"), true);
        selectQuery.addSortColumn(sortColumn);
        return selectQuery;
    }
    
    @Override
    protected void doCleanup() {
        this.deleteDataFromLocationHistoryDB();
        this.deleteDateExceededFiles();
    }
    
    private void deleteDataFromLocationHistoryDB() {
        try {
            final Long startTime = MDMUtil.getCurrentTimeInMillis();
            Logger.getLogger("MDMLocationLogger").log(Level.SEVERE, "Beginning to delete location history backup at {0}", startTime);
            final JSONObject locationSettingsJSON = LocationSettingsDataHandler.getInstance().getLocationSettingsJSON(this.customerId);
            final int days = locationSettingsJSON.optInt("LOCATION_HISTORY_DURATION", 30);
            final SelectQuery toBeCleanedUpQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            toBeCleanedUpQuery.addJoin(new Join("Resource", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            toBeCleanedUpQuery.addJoin(new Join("AgentContact", "MdDeviceLocationDetails", new String[] { "RESOURCE_ID" }, new String[] { "DEVICE_ID" }, 2));
            toBeCleanedUpQuery.addJoin(new Join("MdDeviceLocationDetails", "DeviceRecentLocation", new String[] { "DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            toBeCleanedUpQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID").distinct().count());
            final Column locatedTimeColumn = Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME");
            final Column agentContactTimeColumn = Column.getColumn("AgentContact", "LAST_CONTACT_TIME");
            final Column criteriaColumn = (Column)Column.createOperation(Operation.operationType.SUBTRACT, (Object)agentContactTimeColumn, (Object)locatedTimeColumn);
            criteriaColumn.setDataType("BIGINT");
            final Criteria elapsedCriteria = new Criteria(criteriaColumn, (Object)(days * 86400000L), 5);
            final Criteria recentLocationCri = new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"), (Object)Column.getColumn("DeviceRecentLocation", "LOCATION_DETAIL_ID"), 1);
            final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerId, 0);
            final Criteria criteria = elapsedCriteria.and(customerCriteria).and(recentLocationCri);
            toBeCleanedUpQuery.setCriteria(criteria);
            final int count = ReadOnlyDBUtil.getRecordCount(toBeCleanedUpQuery);
            toBeCleanedUpQuery.removeSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID").distinct().count());
            toBeCleanedUpQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            toBeCleanedUpQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            toBeCleanedUpQuery.addSelectColumn(Column.getColumn("AgentContact", "RESOURCE_ID", "AGENT_CONTACT_RESOURCE_ID"));
            toBeCleanedUpQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "DEVICE_ID"));
            toBeCleanedUpQuery.addSortColumn(new SortColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID", true));
            toBeCleanedUpQuery.setGroupByClause(new GroupByClause(toBeCleanedUpQuery.getSelectColumns()));
            for (int i = 0; i <= count / 500; ++i) {
                toBeCleanedUpQuery.setRange(new Range(0, 500));
                final DataObject dataObject = MDMUtil.getReadOnlyPersistence().get(toBeCleanedUpQuery);
                final Iterator iterator = dataObject.getRows("MdDeviceLocationDetails");
                dataObject.deleteRows("MdDeviceLocationDetails", new Criteria(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"), (Object)DBUtil.getColumnValuesAsList(iterator, "LOCATION_DETAIL_ID").toArray(), 8));
                MDMUtil.getPersistenceLite().update(dataObject);
            }
            final Long endTime = MDMUtil.getCurrentTimeInMillis();
            Logger.getLogger("MDMLocationLogger").log(Level.INFO, " ** Location history - deletion from DB completed at {0}", endTime);
            Logger.getLogger("MDMLocationLogger").log(Level.INFO, "Process completed in {0}  : milliseconds", endTime - startTime);
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMLocationLogger").log(Level.WARNING, "Exception occurred while deleteDataFromDB LocationHistoryDataBackup", ex);
        }
    }
    
    private void deleteDateExceededFiles() {
        try {
            final Long elapsedTime = this.getBackupFileCriteriaTime();
            final List<BackupFileData> backupFileList = new BackupFileDataHandler().getBackupFileInfoFromBelowCreatedTime(elapsedTime);
            if (backupFileList != null) {
                final FileAccessAPI fileAPI = ApiFactoryProvider.getFileAccessAPI();
                for (final BackupFileData backupFileData : backupFileList) {
                    final String basePath = this.backupProperties.optString("BACKUP_LOCATION", (String)null);
                    if (basePath != null) {
                        String path = basePath + File.separator + backupFileData.getFileName();
                        path = System.getProperty("server.home") + File.separator + path;
                        fileAPI.deleteFile(path);
                        LocationHistoryDataBackup.logger.log(Level.INFO, "Location History backup file deleted File : {0}", path);
                    }
                }
                this.deleteDataFromBackupInfoDB(elapsedTime);
            }
        }
        catch (final Exception ex) {
            LocationHistoryDataBackup.logger.log(Level.WARNING, "Exception occurred while deleteDateExceededFiles", ex);
        }
    }
    
    private void deleteDataFromBackupInfoDB(final Long elapsedTime) {
        final Criteria elapsedTimeCrit = new Criteria(Column.getColumn("DCCleanUpDataFiles", "CREATED_TIME"), (Object)elapsedTime, 7);
        new BackupFileDataHandler().deleteDataFromBackupDB(elapsedTimeCrit);
    }
    
    private Long getElapsedTime() {
        final Date referenceDate = new Date();
        final Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(2, -3);
        return c.getTimeInMillis();
    }
    
    private Long getBackupCriteriaTime() {
        Long backupCritTime = -1L;
        final Long firstLocAddedTime = this.getFirstLocationHistoryDataAddedTime();
        final Long curTime = System.currentTimeMillis();
        final CalendarUtil calendarUtil = CalendarUtil.getInstance();
        final int diffDays = calendarUtil.diffBetweenDays(firstLocAddedTime, curTime);
        if (diffDays > LocationHistoryDataBackup.MAX_DAYS_OF_LOCATION_HISTORY) {
            backupCritTime = calendarUtil.getStartTimeOfTheDay(calendarUtil.addDays(new Date(firstLocAddedTime), 1).getTime()).getTime();
        }
        return backupCritTime;
    }
    
    private Long getFirstLocationHistoryDataAddedTime() {
        Long addedTime = -1L;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceLocationDetails"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATION_DETAIL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "LOCATED_TIME"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"));
            selectQuery.setRange(new Range(1, 1));
            final SortColumn sortColumn = new SortColumn(Column.getColumn("MdDeviceLocationDetails", "ADDED_TIME"), true);
            selectQuery.addSortColumn(sortColumn);
            final DataObject locDataDO = MDMUtil.getPersistence().get(selectQuery);
            if (!locDataDO.isEmpty()) {
                final Row row = locDataDO.getFirstRow("MdDeviceLocationDetails");
                addedTime = (Long)row.get("ADDED_TIME");
            }
        }
        catch (final Exception ex) {
            LocationHistoryDataBackup.logger.log(Level.WARNING, "Exception occurred while getFirstLocationHistoryDataAddedTime", ex);
        }
        return addedTime;
    }
    
    private Long getFirstBackupFileDataAddedTime() {
        Long addedTime = -1L;
        try {
            final Criteria backupModuleCriteria = new Criteria(Column.getColumn("DCCleanUpDataFiles", "FEATURE_ID"), (Object)this.backupProperties.optLong("FEATURE_ID"), 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DCCleanUpDataFiles"));
            selectQuery.addSelectColumn(Column.getColumn("DCCleanUpDataFiles", "DATA_FILE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DCCleanUpDataFiles", "CREATED_TIME"));
            selectQuery.setRange(new Range(1, 1));
            final SortColumn sortColumn = new SortColumn(Column.getColumn("DCCleanUpDataFiles", "CREATED_TIME"), true);
            selectQuery.addSortColumn(sortColumn);
            selectQuery.setCriteria(backupModuleCriteria);
            final DataObject backupFileDO = MDMUtil.getPersistence().get(selectQuery);
            if (!backupFileDO.isEmpty()) {
                final Row row = backupFileDO.getFirstRow("DCCleanUpDataFiles");
                addedTime = (Long)row.get("CREATED_TIME");
            }
        }
        catch (final Exception ex) {
            LocationHistoryDataBackup.logger.log(Level.WARNING, "Exception occurred while getFirstBackupFileDataAddedTime", ex);
        }
        return addedTime;
    }
    
    private Long getBackupFileCriteriaTime() {
        Long backupCritTime = -1L;
        final Long firstLocAddedTime = this.getFirstBackupFileDataAddedTime();
        final Long curTime = System.currentTimeMillis();
        final CalendarUtil calendarUtil = CalendarUtil.getInstance();
        final int diffDays = calendarUtil.diffBetweenDays(firstLocAddedTime, curTime);
        if (diffDays > this.MAX_DAYS_OF_BACKUP_FILES) {
            backupCritTime = calendarUtil.getStartTimeOfTheDay(calendarUtil.addDays(new Date(firstLocAddedTime), 1).getTime()).getTime();
        }
        return backupCritTime;
    }
    
    @Override
    protected boolean isNeedBackup() {
        return MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowLocationHistoryBackup");
    }
    
    static {
        LocationHistoryDataBackup.MAX_DAYS_OF_LOCATION_HISTORY = 28;
        logger = Logger.getLogger("MDMLogger");
    }
}
