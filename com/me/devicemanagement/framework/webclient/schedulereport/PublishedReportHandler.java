package com.me.devicemanagement.framework.webclient.schedulereport;

import org.json.JSONException;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import com.me.devicemanagement.framework.server.util.FrameworkStatusCodes;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import org.apache.commons.lang3.RandomStringUtils;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Logger;

public class PublishedReportHandler
{
    public static Logger log;
    public static final String SEPARATE_SCHEDULE_BACKUP_HISTORY_PERIOD = "separate_schedule_backup_history_period";
    public static Boolean separateScheduleBackupHistoryPeriodStatus;
    
    public static HashMap<String, String> addReportRelatedDetails(final ArrayList<String> report_paths, final boolean isUpdateCurrentTime) {
        return addReportRelatedDetails(report_paths, isUpdateCurrentTime, null);
    }
    
    public static HashMap<String, String> addReportRelatedDetails(final ArrayList<String> report_paths, final boolean isUpdateCurrentTime, final Long task_id) {
        final HashMap<String, String> reports_id = new HashMap<String, String>();
        try {
            final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            for (final String report_path : report_paths) {
                final String report_id = getUniqueReportId();
                final Row row = getRow(report_path, report_id, isUpdateCurrentTime, task_id);
                reports_id.put(report_id, report_path);
                dataObject.addRow(row);
            }
            SyMUtil.getPersistence().update(dataObject);
            return reports_id;
        }
        catch (final DataAccessException dae) {
            PublishedReportHandler.log.log(Level.INFO, "Exception while saving consents" + dae);
            return null;
        }
    }
    
    public static String getUniqueReportId() {
        String report_id = RandomStringUtils.randomAlphanumeric(15);
        for (DataObject reportIDDO = getPublishedReportDetailsDO(report_id); reportIDDO != null && !reportIDDO.isEmpty(); reportIDDO = getPublishedReportDetailsDO(report_id)) {
            report_id = RandomStringUtils.randomAlphanumeric(15);
        }
        return report_id;
    }
    
    public static Row getRow(final String report_path, final String report_id, final Boolean isUpdateCurrentTime) {
        return getRow(report_path, report_id, isUpdateCurrentTime, null);
    }
    
    private static Row getRow(final String report_path, final String report_id, final Boolean isUpdateCurrentTime, final Long task_id) {
        final Row row = new Row("PublishedReportDetails");
        row.set("REPORT_ID", (Object)report_id);
        row.set("REPORT_PATH", (Object)report_path);
        if (!isUpdateCurrentTime) {
            final File file = new File(report_path);
            row.set("GENERATED_TIME", (Object)file.lastModified());
        }
        else {
            row.set("GENERATED_TIME", (Object)System.currentTimeMillis());
        }
        Long user_id = null;
        try {
            user_id = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        }
        catch (final Exception e) {
            PublishedReportHandler.log.log(Level.INFO, "User name not found" + e);
        }
        final Long customer_id = CustomerInfoUtil.getInstance().getCustomerId();
        String scheduleBackupIdString;
        if (PublishedReportHandler.separateScheduleBackupHistoryPeriodStatus) {
            if (task_id == null) {
                scheduleBackupIdString = ScheduleReportUtil.getScheduleBackupDetails(user_id, customer_id).getProperty("schedule_backup_status_id");
            }
            else {
                scheduleBackupIdString = ScheduleReportUtil.getScheduleBackupDetails(task_id, user_id, customer_id).getProperty("schedule_backup_status_id");
            }
        }
        else {
            scheduleBackupIdString = ScheduleReportUtil.getScheduleBackupDetails().getProperty("schedule_backup_status_id");
        }
        final Long scheduleBackUpId = (scheduleBackupIdString == null) ? null : Long.valueOf(Long.parseLong(scheduleBackupIdString));
        row.set("SCHEDULE_BACKUP_STATUS_ID", (Object)scheduleBackUpId);
        return row;
    }
    
    public static String addReportRelatedDetails(final String reportPath, final boolean isUpdateCurrentTime) {
        return addReportRelatedDetails(reportPath, isUpdateCurrentTime, null);
    }
    
    public static String addReportRelatedDetails(final String reportPath, final boolean isUpdateCurrentTime, final Long task_id) {
        try {
            final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            final String report_id = getUniqueReportId();
            final Row row = getRow(reportPath, report_id, isUpdateCurrentTime, task_id);
            dataObject.addRow(row);
            SyMUtil.getPersistence().update(dataObject);
            return report_id;
        }
        catch (final DataAccessException dae) {
            PublishedReportHandler.log.log(Level.INFO, "Exception while saving consents" + dae);
            return null;
        }
    }
    
    public static DataObject getPublishedReportDetailsDO(final Criteria criteria) {
        final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("PublishedReportDetails"));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        try {
            return SyMUtil.getPersistence().get((SelectQuery)selectQuery);
        }
        catch (final DataAccessException dae) {
            PublishedReportHandler.log.log(Level.INFO, " Exception while retrieving PublishedExportDetails " + dae);
            return null;
        }
    }
    
    public static DataObject getPublishedReportDetailsDO(final ArrayList<String> report_paths) {
        final Criteria criteria = new Criteria(new Column("PublishedReportDetails", "REPORT_PATH"), (Object)report_paths, 8);
        return getPublishedReportDetailsDO(criteria);
    }
    
    public static DataObject getPublishedReportDetailsDO(final String report_id) {
        final Criteria criteria = new Criteria(new Column("PublishedReportDetails", "REPORT_ID"), (Object)report_id, 0);
        return getPublishedReportDetailsDO(criteria);
    }
    
    public static String getReportPath(final String report_id) {
        final DataObject dataObject = getPublishedReportDetailsDO(report_id);
        try {
            final Row row = dataObject.getFirstRow("PublishedReportDetails");
            final String path = row.get("REPORT_PATH").toString();
            return path;
        }
        catch (final DataAccessException dae) {
            PublishedReportHandler.log.log(Level.INFO, "Exception while retrieving report path " + dae);
            return null;
        }
    }
    
    public static int deletePublishedReportDetailsByGeneratedTime() {
        final DataObject dataObject = ScheduleReportUtil.getInstance().getScheduleScheduleBackupStatusDO();
        try {
            final Iterator rows = dataObject.getRows("ScheduleBackupStatus");
            while (rows.hasNext()) {
                final Row row = rows.next();
                final Long no_of_days = ScheduleReportUtil.getScheduleReportBackupPeriod(row);
                final Long scheduleBackupPeriodId = (Long)row.get("SCHEDULE_BACKUP_STATUS_ID");
                deleteCriteria(no_of_days, scheduleBackupPeriodId);
            }
            final Row defaultPeriodRow = dataObject.getRow("ScheduleBackupStatus", new Criteria(new Column("ScheduleBackupStatus", "TASK_ID"), (Object)null, 0));
            final Long no_of_days = ScheduleReportUtil.getScheduleReportBackupPeriod(defaultPeriodRow);
            return deletePublishedReportDetailsByGeneratedTime(no_of_days);
        }
        catch (final DataAccessException e) {
            PublishedReportHandler.log.log(Level.INFO, "Exception while deleting schedule report backed up changes" + e);
            return 1001;
        }
    }
    
    public static int deletePublishedReportDetailsByGeneratedTime(final Long generatedTime) {
        try {
            final Criteria deleteCriteria = new Criteria(new Column("PublishedReportDetails", "GENERATED_TIME"), (Object)generatedTime, 7);
            SyMUtil.getPersistence().delete(deleteCriteria);
            return FrameworkStatusCodes.SUCCESS_RESPONSE_CODE;
        }
        catch (final DataAccessException dae) {
            PublishedReportHandler.log.log(Level.INFO, "Exception while deleting the published report details" + dae);
            return 1001;
        }
    }
    
    private static int deleteCriteria(final Long generatedTime, final Long scheduleBackupPeriodId) {
        try {
            final Criteria timeCriteria = new Criteria(new Column("PublishedReportDetails", "GENERATED_TIME"), (Object)generatedTime, 7);
            final Criteria scheduleReportIdCriteria = new Criteria(new Column("PublishedReportDetails", "SCHEDULE_BACKUP_STATUS_ID"), (Object)scheduleBackupPeriodId, 0);
            final Criteria deleteCriteria = timeCriteria.and(scheduleReportIdCriteria);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("PublishedReportDetails"));
            query.setCriteria(deleteCriteria);
            final DataObject data = SyMUtil.getPersistence().get(query);
            final Iterator rows = data.getRows("PublishedReportDetails");
            while (rows.hasNext()) {
                final Row row = rows.next();
                String file_path = row.get("REPORT_PATH").toString();
                file_path = file_path.replaceAll("\\\\", "\\\\\\\\");
                final boolean isDeleted = ((FileAccessAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_FILE_ACCESS_API_CLASS")).newInstance()).deleteDirectory(file_path);
                if (isDeleted) {
                    PublishedReportHandler.log.log(Level.FINEST, "file: " + file_path + " deleted successfully");
                }
            }
            SyMUtil.getPersistence().delete(deleteCriteria);
            return FrameworkStatusCodes.SUCCESS_RESPONSE_CODE;
        }
        catch (final DataAccessException dae) {
            PublishedReportHandler.log.log(Level.INFO, "Exception while deleting the published report details" + dae);
            return 1001;
        }
        catch (final Exception e) {
            PublishedReportHandler.log.log(Level.INFO, "Exception while deleting the published report directory" + e);
            return 3002;
        }
    }
    
    static {
        PublishedReportHandler.log = Logger.getLogger(PublishedReportHandler.class.getName());
        PublishedReportHandler.separateScheduleBackupHistoryPeriodStatus = false;
        try {
            final JSONObject separate_schedule_backup = (JSONObject)FrameworkConfigurations.getFrameworkConfigurations().get("separate_schedule_backup_history_period");
            PublishedReportHandler.separateScheduleBackupHistoryPeriodStatus = separate_schedule_backup.getBoolean("enable");
        }
        catch (final JSONException jsonExce) {
            PublishedReportHandler.log.log(Level.WARNING, " Exception while loading SEPARATE_SCHEDULE_BACKUP_HISTORY_PERIOD " + jsonExce);
            jsonExce.printStackTrace();
        }
    }
}
