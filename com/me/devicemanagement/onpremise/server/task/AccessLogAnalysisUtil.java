package com.me.devicemanagement.onpremise.server.task;

import org.json.JSONException;
import java.util.Properties;
import java.util.Calendar;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.zoho.framework.utils.archive.SevenZipUtils;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.WritableDataObject;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import java.nio.file.Path;
import com.adventnet.ds.query.DataSet;
import java.util.List;
import java.io.IOException;
import com.adventnet.ds.query.QueryConstructionException;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.framework.server.util.CommonUpdatesUtil;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.io.Reader;
import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import com.adventnet.ds.query.BulkLoad;
import java.util.logging.Level;
import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

public class AccessLogAnalysisUtil
{
    private static AccessLogAnalysisUtil accessLogUtilInstance;
    private Logger logger;
    
    public AccessLogAnalysisUtil() {
        this.logger = Logger.getLogger("NginxAccessLog");
    }
    
    public static AccessLogAnalysisUtil getInstance() {
        if (AccessLogAnalysisUtil.accessLogUtilInstance == null) {
            AccessLogAnalysisUtil.accessLogUtilInstance = new AccessLogAnalysisUtil();
        }
        return AccessLogAnalysisUtil.accessLogUtilInstance;
    }
    
    public void rotateNginxLog(final String currentDay) {
        try {
            this.logger.info("Going to rotate logs with parameter =  " + currentDay);
            final ProcessBuilder builder = new ProcessBuilder(Arrays.asList("cmd.exe", "/C", "rotateNginxLogs.bat", currentDay));
            builder.directory(new File(System.getProperty("server.home") + File.separator + "bin" + File.separator));
            final Process process = builder.start();
            process.waitFor();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in rotateNginxLog ", e);
        }
    }
    
    public void populateDBWithData(final String fileLoc) {
        this.logger.info("Entering populateDBWithData for fileLocation: " + fileLoc);
        BulkLoad load = null;
        CSVReader reader = null;
        try {
            load = new BulkLoad("AccessLogAnalysis");
            load.setBufferSize(2);
            load.setIdleTimeOut(60);
            load.setAutoFillUVG(true);
            load.createTempTable(Boolean.valueOf(false));
            final SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
            reader = new CSVReader((Reader)new FileReader(fileLoc));
            String[] str;
            while ((str = reader.readNext()) != null) {
                load.setString("IPAddress", str[0]);
                load.setTimeStamp("time_stamp", new Timestamp(sdf.parse(str[1].split(" ")[0]).getTime()));
                load.setString("Request", str[2]);
                load.setString("Status_Code", str[3]);
                load.setString("Data_Size", str[4]);
                load.setFloat("Time_Taken", Float.valueOf(Float.parseFloat(str[5].split(" ")[0])));
                load.flush();
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in populateDBWithData ", e);
            try {
                if (load != null) {
                    load.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception in populateDBWithData finally", e);
            }
        }
        finally {
            try {
                if (load != null) {
                    load.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception in populateDBWithData finally", e2);
            }
        }
    }
    
    public void generateReport() {
        Connection conn = null;
        try {
            final byte[] encoded = Files.readAllBytes(Paths.get(System.getProperty("server.home") + File.separator + "conf" + File.separator + "accessLogParameters.json", new String[0]));
            JSONObject accessJSON = new JSONObject(new String(encoded));
            final String crsData = CommonUpdatesUtil.getInstance().getValue("NginxAccessLogAnalysisParameters");
            if (crsData != null) {
                final JSONObject crsJson = new JSONObject(crsData);
                accessJSON = this.mergeJSONObjects(crsJson, accessJSON);
            }
            conn = RelationalAPI.getInstance().getConnection();
            final Iterator<?> keys = accessJSON.keys();
            this.logger.info("############################ START OF DATA PROCESSING##################################");
            while (keys.hasNext()) {
                final String key = (String)keys.next();
                final String value = (String)accessJSON.get(key);
                final Criteria iterateCriteria = new Criteria(Column.getColumn("AccessLogAnalysis", "Request"), (Object)value, 2, false);
                this.insertDataIntoDataTable(conn, iterateCriteria, key);
            }
            this.insertDataIntoDataTable(conn, new Criteria(Column.getColumn("AccessLogAnalysis", "Status_Code"), (Object)"401", 0, false), "401Status");
            this.insertDataIntoDataTable(conn, null, "Overall");
            this.logger.info("############################ COMPLETED DATA PROCESSING ################################");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while generateReport", e);
            try {
                this.computeUnAuthorisedRequestMax(conn);
                RelationalAPI.getInstance().truncateTable("AccessLogAnalysis");
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final SQLException e2) {
                this.logger.log(Level.SEVERE, "Error while truncating table.", e2);
            }
        }
        finally {
            try {
                this.computeUnAuthorisedRequestMax(conn);
                RelationalAPI.getInstance().truncateTable("AccessLogAnalysis");
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final SQLException e3) {
                this.logger.log(Level.SEVERE, "Error while truncating table.", e3);
            }
        }
    }
    
    private void computeUnAuthorisedRequestMax(final Connection connection) throws SQLException {
        final List groupByColumnList = new ArrayList();
        DataSet dataSet = null;
        final Path filePathToStoreMaxCount = Paths.get(System.getProperty("server.home") + File.separator + "logs" + File.separator + "access_logs", new String[0]);
        final Path fileToStoreMaxCount = Paths.get(System.getProperty("server.home") + File.separator + "logs" + File.separator + "access_logs" + File.separator + "maxRequestFailure.props", new String[0]);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AccessLogAnalysis"));
        final Criteria criteria = new Criteria(Column.getColumn("AccessLogAnalysis", "Status_Code"), (Object)"401", 0, false);
        groupByColumnList.add(Column.getColumn("AccessLogAnalysis", "IPAddress"));
        final GroupByClause groupByClause = new GroupByClause(groupByColumnList);
        selectQuery.setCriteria(criteria);
        selectQuery.setGroupByClause(groupByClause);
        selectQuery.addSelectColumn(Column.getColumn("AccessLogAnalysis", "Status_Code").count());
        selectQuery.addSortColumn(new SortColumn(Column.getColumn("AccessLogAnalysis", "Status_Code").count(), false));
        try {
            dataSet = RelationalAPI.getInstance().executeQuery((Query)selectQuery, connection);
            if (dataSet.next()) {
                final Long maxUnAuthorizedCount = dataSet.getAsLong(1);
                if (!filePathToStoreMaxCount.toFile().exists()) {
                    Files.createDirectories(filePathToStoreMaxCount, (FileAttribute<?>[])new FileAttribute[0]);
                }
                Files.createFile(fileToStoreMaxCount, (FileAttribute<?>[])new FileAttribute[0]);
                Files.write(fileToStoreMaxCount, String.valueOf(maxUnAuthorizedCount).getBytes(), new OpenOption[0]);
            }
        }
        catch (final SQLException e) {
            this.logger.log(Level.WARNING, "Exception occurred in computeUnAuthorisedRequestMax() method ", e);
        }
        catch (final QueryConstructionException e2) {
            this.logger.log(Level.WARNING, "Exception occurred in computeUnAuthorisedRequestMax() method ", (Throwable)e2);
        }
        catch (final IOException e3) {
            this.logger.log(Level.WARNING, "Exception occurred in computeUnAuthorisedRequestMax() method ", e3);
        }
        finally {
            if (dataSet != null) {
                dataSet.close();
            }
        }
    }
    
    private long maxReqPerSec(final Connection conn, final Criteria criteria) throws SQLException {
        DataSet ds = null;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("AccessLogAnalysis"));
            sq.setCriteria(criteria);
            sq.addSelectColumn(Column.getColumn("AccessLogAnalysis", "Request").count());
            sq.addSelectColumn(Column.getColumn("AccessLogAnalysis", "time_stamp"));
            final List groupByColList = new ArrayList();
            groupByColList.add(Column.getColumn("AccessLogAnalysis", "time_stamp"));
            final GroupByClause groupByClause = new GroupByClause(groupByColList);
            sq.setGroupByClause(groupByClause);
            final List sortColumns = new ArrayList();
            sortColumns.add(new SortColumn(Column.getColumn("AccessLogAnalysis", "Request").count(), false));
            sq.addSortColumns(sortColumns);
            ds = RelationalAPI.getInstance().executeQuery((Query)sq, conn);
            return ds.getAsLong(1);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in maxReqPerSec: ", e);
        }
        finally {
            if (ds != null) {
                ds.close();
            }
        }
        return 0L;
    }
    
    private void insertDataIntoDataTable(final Connection conn, final Criteria criteria, final String type) throws Exception {
        DataSet ds = null;
        try {
            final SelectQuery accessLogData = (SelectQuery)new SelectQueryImpl(Table.getTable("AccessLogAnalysis"));
            accessLogData.setCriteria(criteria);
            final long topReqPerSecond = this.maxReqPerSec(conn, criteria);
            accessLogData.addSelectColumn(Column.getColumn("AccessLogAnalysis", "Request").count());
            accessLogData.addSelectColumn(Column.getColumn("AccessLogAnalysis", "Time_Taken").average());
            accessLogData.addSelectColumn(Column.getColumn("AccessLogAnalysis", "Time_Taken").maximum());
            accessLogData.addSelectColumn(Column.getColumn("AccessLogAnalysis", "Data_Size").summation());
            ds = RelationalAPI.getInstance().executeQuery((Query)accessLogData, conn);
            final Row dataRow = new Row("AccessLogData");
            long count = 0L;
            long errCount = 0L;
            long avg = 0L;
            long maximum = 0L;
            long totSize = 0L;
            if (ds.next()) {
                count = ds.getAsLong(1);
                avg = ds.getAsLong(2);
                maximum = ds.getAsLong(3);
                totSize = ds.getAsLong(4);
            }
            final Criteria success = new Criteria(Column.getColumn("AccessLogAnalysis", "Status_Code"), (Object)200, 1, false);
            final Criteria moved = new Criteria(Column.getColumn("AccessLogAnalysis", "Status_Code"), (Object)301, 1, false);
            final Criteria tempMoved = new Criteria(Column.getColumn("AccessLogAnalysis", "Status_Code"), (Object)302, 1, false);
            final Criteria notModified = new Criteria(Column.getColumn("AccessLogAnalysis", "Status_Code"), (Object)304, 1, false);
            final Criteria partialDownload = new Criteria(Column.getColumn("AccessLogAnalysis", "Status_Code"), (Object)206, 1, false);
            final Criteria totalCri = success.and(moved).and(notModified).and(partialDownload).and(tempMoved).and(criteria);
            accessLogData.setCriteria(totalCri);
            accessLogData.addSelectColumn(Column.getColumn("AccessLogAnalysis", "Request").count());
            ds = RelationalAPI.getInstance().executeQuery((Query)accessLogData, conn);
            if (ds.next()) {
                errCount = ds.getAsLong(1);
            }
            final String dataReadableFormat = FileUtils.byteCountToDisplaySize(totSize);
            this.logger.info("Values for " + type + " are = " + count + " " + avg + " " + maximum + " " + dataReadableFormat + " " + errCount + " " + topReqPerSecond);
            dataRow.set("Req_Time", (Object)new java.sql.Date(new Date().getTime()));
            dataRow.set("Type", (Object)type);
            dataRow.set("Max_Duration", (Object)maximum);
            dataRow.set("Average_Duration", (Object)avg);
            dataRow.set("Req_Count", (Object)count);
            dataRow.set("Error_Count", (Object)errCount);
            dataRow.set("Total_Size", (Object)(totSize / 1.0E9));
            dataRow.set("Total_Size_Readable", (Object)dataReadableFormat);
            dataRow.set("Reqps", (Object)topReqPerSecond);
            final DataObject dataObjectWriter = (DataObject)new WritableDataObject();
            dataObjectWriter.addRow(dataRow);
            SyMUtil.getPersistence().update(dataObjectWriter);
        }
        finally {
            if (ds != null) {
                ds.close();
            }
        }
    }
    
    public void deleteOldReports(final int tableRecordsToKeep) {
        try {
            final DeleteQuery delOldReps = (DeleteQuery)new DeleteQueryImpl("AccessLogData");
            final Criteria oldRecords = new Criteria(Column.getColumn("AccessLogData", "Req_Time"), (Object)new java.sql.Date(new Date().getTime() - tableRecordsToKeep * 24 * 3600 * 1000L), 7, false);
            delOldReps.setCriteria(oldRecords);
            SyMUtil.getPersistence().delete(delOldReps);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in deleteOldReports: ", e);
        }
    }
    
    public boolean zipLogsAndCleanDirectory(final String folderLocation, final List retainedFiles, final int numOfDays) throws IOException {
        final File dir = new File(folderLocation);
        Properties webSettingsProps = null;
        File zipDir;
        try {
            webSettingsProps = WebServerUtil.getWebServerSettings(Boolean.FALSE);
            if (webSettingsProps.containsKey("nginx.accesslogs.backup.location") && !webSettingsProps.getProperty("nginx.accesslogs.backup.location").isEmpty()) {
                zipDir = new File(webSettingsProps.getProperty("nginx.accesslogs.backup.location").replace("/", "\\"));
            }
            else {
                zipDir = new File(System.getProperty("server.home") + File.separator + "accesslogbackup");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in zipLogsAndCleanDirectory: ", e);
            zipDir = new File(System.getProperty("server.home") + File.separator + "accesslogbackup");
        }
        final File[] files = dir.listFiles();
        final SimpleDateFormat dtf_year_month = new SimpleDateFormat("yyyy-MM");
        final Date date = new Date();
        date.setTime(date.getTime() + (-numOfDays - 1) * 1000L * 60L * 60L * 24L);
        final String zipFileName = dtf_year_month.format(date) + ".zip";
        final List<String> filesToCompress = new ArrayList<String>();
        for (final File file : (files != null) ? files : new File[0]) {
            if (!retainedFiles.contains(file.getName())) {
                filesToCompress.add(file.getCanonicalPath());
            }
        }
        boolean executionStatus = true;
        try {
            System.setProperty("tools.7zip.win.path", System.getProperty("server.home") + File.separator + "bin" + File.separator + "7za.exe");
            if (filesToCompress.size() > 0) {
                if (!zipDir.exists()) {
                    zipDir.mkdir();
                }
                int exitCode;
                if (new File(zipDir.getCanonicalPath() + File.separator + zipFileName).exists()) {
                    exitCode = SevenZipUtils.appendInZip(zipDir.getCanonicalPath() + File.separator + zipFileName, (List)filesToCompress);
                }
                else {
                    exitCode = SevenZipUtils.zip(zipDir, zipFileName, dir, false, false, (List)filesToCompress, (List)null);
                    int monthPeriod;
                    if (webSettingsProps.containsKey("nginx.accesslogs.backup.period") && !webSettingsProps.getProperty("nginx.accesslogs.backup.period").isEmpty()) {
                        try {
                            monthPeriod = Integer.parseInt(webSettingsProps.getProperty("nginx.accesslogs.backup.period"));
                            if (monthPeriod > 12) {
                                monthPeriod = 12;
                            }
                            else if (monthPeriod < 1) {
                                monthPeriod = 1;
                            }
                        }
                        catch (final NumberFormatException e2) {
                            this.logger.log(Level.WARNING, "Month period entry for log backup is not a number: ", e2);
                            monthPeriod = Integer.parseInt(String.valueOf(FrameworkConfigurations.getSpecificPropertyIfExists("ACCESS_LOG", "BackupMonths", (Object)6)));
                        }
                    }
                    else {
                        monthPeriod = Integer.parseInt(String.valueOf(FrameworkConfigurations.getSpecificPropertyIfExists("ACCESS_LOG", "BackupMonths", (Object)6)));
                    }
                    final Calendar calendar = Calendar.getInstance();
                    calendar.add(2, -monthPeriod);
                    final int calMonth = calendar.get(2) + 1;
                    final String prevYearZip = calendar.get(1) + "-" + ((calMonth < 10) ? ("0" + calMonth) : Integer.valueOf(calMonth)) + ".zip";
                    if (new File(zipDir.getCanonicalPath() + File.separator + prevYearZip).exists()) {
                        FileUtils.forceDelete(new File(zipDir.getCanonicalPath() + File.separator + prevYearZip));
                    }
                }
                if (exitCode != 0) {
                    executionStatus = false;
                }
            }
        }
        catch (final Exception e3) {
            this.logger.log(Level.WARNING, "Zip task for access logs failed: ", e3);
            executionStatus = false;
        }
        if (executionStatus) {
            for (final String fileToDelete : filesToCompress) {
                FileUtils.forceDelete(new File(fileToDelete));
            }
        }
        return executionStatus;
    }
    
    public JSONObject mergeJSONObjects(final JSONObject json1, final JSONObject json2) {
        JSONObject mergedJSON = new JSONObject();
        try {
            mergedJSON = new JSONObject(json1, JSONObject.getNames(json1));
            for (final String key : JSONObject.getNames(json2)) {
                mergedJSON.put(key, json2.get(key));
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "error while mergeJSONObjects", (Throwable)e);
        }
        return mergedJSON;
    }
    
    static {
        AccessLogAnalysisUtil.accessLogUtilInstance = null;
    }
}
