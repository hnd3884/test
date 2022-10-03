package com.me.devicemanagement.onpremise.server.metrack.ondemand;

import java.util.Iterator;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import org.json.simple.JSONObject;
import java.util.logging.Logger;

public class ONDemandDownloadHandler
{
    private static Logger logger;
    private static String sourceClass;
    
    public ONDemandDataCollectorBean updateTasksFromZC(final ONDemandDataCollectorBean onDemandDataCollectorBean) {
        try {
            final Long zcFetchTime = System.currentTimeMillis();
            final JSONObject zcFailedRequests = new JSONObject();
            int fromIndex = 1;
            final int limit = 200;
            int failedRequestCount = 0;
            boolean updateTasksTODB = true;
            while (true) {
                final String theUrl = this.getZCTasksViewUrl(fromIndex, limit);
                final DownloadStatus downloadTasksStatus = DownloadManager.getInstance().getURLResponseWithoutCookie(theUrl, (String)null, new SSLValidationType[] { SSLValidationType.DEFAULT_SSL_VALIDATION });
                boolean currentDownloadIsSuccess = downloadTasksStatus.getStatus() == 0;
                if (currentDownloadIsSuccess) {
                    SyMLogger.info(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "updateTasksFromZC", "tasks downloaded(" + downloadTasksStatus.getStatus() + ")");
                    final JSONObject tasksJson = (JSONObject)JSONValue.parse(downloadTasksStatus.getUrlDataBuffer());
                    final Object response = tasksJson.get(tasksJson.keySet().iterator().next());
                    JSONArray tasks = null;
                    if (response instanceof JSONArray) {
                        tasks = (JSONArray)response;
                        SyMLogger.info(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "updateTasksFromZC", "Current Download Count : " + tasks.size());
                        if (!this.updateTasksTODB(tasks)) {
                            updateTasksTODB = false;
                        }
                        if (200 > tasks.size()) {
                            break;
                        }
                        fromIndex += limit;
                    }
                    else {
                        SyMLogger.warning(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "updateTasksFromZC", "Download failed : " + response);
                        currentDownloadIsSuccess = false;
                    }
                }
                else {
                    SyMLogger.warning(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "updateTasksFromZC", "Download Failed(" + downloadTasksStatus.getStatus() + ") : " + downloadTasksStatus.getErrorMessage());
                }
                if (!currentDownloadIsSuccess) {
                    zcFailedRequests.put((Object)(String.valueOf(fromIndex) + "-" + String.valueOf(limit)), (Object)downloadTasksStatus.getStatus());
                    if (failedRequestCount > 1) {
                        updateTasksTODB = false;
                        break;
                    }
                    ++failedRequestCount;
                }
                else {
                    failedRequestCount = 0;
                }
            }
            if (failedRequestCount == 0 && updateTasksTODB) {
                SyMLogger.info(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "updateTasksFromZC", "Going to update the tasks into db ...!");
                ONDemandDataCollectorUtil.getInstance().updateLastTasksGetTime(zcFetchTime);
            }
            SyMLogger.info(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "updateTasksFromZC", "zcFailedRequests : " + zcFailedRequests);
            onDemandDataCollectorBean.setZCFailedRequests(zcFailedRequests.toString());
            this.tasksCleanup();
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "updateTasksFromZC", "Exception occurred : ", (Throwable)e);
        }
        return onDemandDataCollectorBean;
    }
    
    private String getZCTasksViewUrl(final int fromIndex, final int limit) {
        String theUrl = "";
        try {
            theUrl = "https://creator.zoho.com/api/json/" + ONDemandDataCollectorUtil.getInstance().getZCApplicationName() + "/view/" + ONDemandDataCollectorUtil.getInstance().getTasksZCViewName();
            theUrl = theUrl + "?authtoken=" + ONDemandDataCollectorUtil.getInstance().getZCKey() + "&" + "raw=true&zc_ownername=" + ONDemandDataCollectorUtil.getInstance().getZCOwnerName() + "&scope=creatorapi&startindex=" + String.valueOf(fromIndex) + "&limit=" + String.valueOf(limit);
            final String lastTasksGetTime = ONDemandDataCollectorUtil.getInstance().getLastTasksGetTime();
            if (lastTasksGetTime != null) {
                theUrl = theUrl + "&criteria=(" + ONDemandDataCollectorUtil.getInstance().getZCCriteriaColumnName() + "%3E=%22" + lastTasksGetTime + "%22)";
            }
            theUrl = theUrl.replaceAll(" ", "%20");
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "getZCTasksViewUrl", "Exception occurred : ", (Throwable)e);
        }
        return theUrl;
    }
    
    private boolean updateTasksTODB(final JSONArray tasks) {
        boolean updateTasksTODB = true;
        try {
            DataObject dataObject = (DataObject)new WritableDataObject();
            for (int i = 0, len = tasks.size(); i < len; ++i) {
                final JSONObject taskJson = (JSONObject)tasks.get(i);
                final DataObject tempDataObject = this.processTask(dataObject, taskJson);
                if (tempDataObject == null) {
                    updateTasksTODB = false;
                }
                else {
                    dataObject = tempDataObject;
                }
            }
            DataAccess.update(dataObject);
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "updateTasksTODB", "Exception occurred : ", (Throwable)e);
        }
        return updateTasksTODB;
    }
    
    private DataObject processTask(final DataObject dataObject, final JSONObject taskJson) {
        try {
            if (taskJson.containsKey((Object)"ID")) {
                final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("ONDemandDataCollectorTasks"));
                selectQuery.addSelectColumn(Column.getColumn("ONDemandDataCollectorTasks", "*"));
                selectQuery.addSelectColumn(Column.getColumn("ONDemandDataCollectorTasksExtn", "*"));
                selectQuery.addJoin(new Join("ONDemandDataCollectorTasks", "ONDemandDataCollectorTasksExtn", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 1));
                selectQuery.setCriteria(new Criteria(Column.getColumn("ONDemandDataCollectorTasks", "TASK_ID"), (Object)Long.valueOf(taskJson.get((Object)"ID").toString()), 0));
                final DataObject taskFromDB = DataAccess.get((SelectQuery)selectQuery);
                final Hashtable<String, Row> rowSet = this.insertValues(taskJson);
                if (rowSet != null) {
                    if (!taskFromDB.isEmpty()) {
                        taskFromDB.updateRow((Row)rowSet.get("ONDemandDataCollectorTasks"));
                        taskFromDB.updateRow((Row)rowSet.get("ONDemandDataCollectorTasksExtn"));
                        dataObject.merge(taskFromDB);
                    }
                    else {
                        final DataObject writableDataObject = (DataObject)new WritableDataObject();
                        writableDataObject.addRow((Row)rowSet.get("ONDemandDataCollectorTasks"));
                        writableDataObject.addRow((Row)rowSet.get("ONDemandDataCollectorTasksExtn"));
                        dataObject.merge(writableDataObject);
                    }
                }
                else {
                    SyMLogger.info(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "processTask", "Invalid Task : " + taskJson);
                }
            }
            return dataObject;
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "processTask", "Exception occurred : ", (Throwable)e);
            return null;
        }
    }
    
    private Hashtable<String, Row> insertValues(final JSONObject taskJson) {
        final Hashtable<String, Row> rowSet = new Hashtable<String, Row>();
        try {
            final Row row = new Row("ONDemandDataCollectorTasks");
            final Row extnTableRow = new Row("ONDemandDataCollectorTasksExtn");
            final String emptyString = "";
            final String emptyJSONArray = "[]";
            row.set("TASK_ID", (Object)Long.valueOf(taskJson.get((Object)"ID").toString()));
            extnTableRow.set("TASK_ID", (Object)Long.valueOf(taskJson.get((Object)"ID").toString()));
            row.set("ZC_FORM_NAME", (Object)taskJson.get((Object)"ZCForm_Name").toString());
            row.set("ZC_COLUMN_NAME", (Object)taskJson.get((Object)"ZCColumn_Name").toString());
            row.set("MIN_BUILD_NUMBER", (Object)Integer.valueOf(taskJson.get((Object)"MINBuild_Num").toString()));
            final String dataPostedTo = taskJson.get((Object)"Data_Posted_To").toString();
            if ("Default Form".equalsIgnoreCase(dataPostedTo)) {
                row.set("DATA_POSTED_TO", (Object)1);
            }
            else {
                if (!"Not a Default Form".equalsIgnoreCase(dataPostedTo)) {
                    return null;
                }
                row.set("DATA_POSTED_TO", (Object)2);
            }
            final String taskReleasedTimeAsString = taskJson.get((Object)"Task_Released_Time").toString();
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
            row.set("TASK_RELEASED_TIME", (Object)simpleDateFormat.parse(taskReleasedTimeAsString).getTime());
            final String dataFrom = taskJson.get((Object)"Data_From").toString();
            row.set("EXPIRE_DAYS", (Object)Integer.valueOf(taskJson.get((Object)"Expire_Days").toString()));
            if (taskJson.containsKey((Object)"MAXBuild_Num")) {
                final String maxBuildNum = "".equals(taskJson.get((Object)"MAXBuild_Num").toString()) ? "" : taskJson.get((Object)"MAXBuild_Num").toString();
                if (!"".equals(maxBuildNum)) {
                    extnTableRow.set("MAX_BUILD_NUMBER", (Object)Integer.valueOf(maxBuildNum));
                }
            }
            if (taskJson.containsKey((Object)"License_Type.License_Type_Code")) {
                final String licenseTypeString = taskJson.get((Object)"License_Type.License_Type_Code").toString();
                final String licenseTypes = ("".equals(licenseTypeString) || "[]".equals(licenseTypeString)) ? "" : new org.json.JSONArray(licenseTypeString).toString();
                if (!"".equals(licenseTypes)) {
                    extnTableRow.set("LICENSE_TYPE", (Object)licenseTypes);
                }
            }
            if (taskJson.containsKey((Object)"License_Edition.License_Edition_Code")) {
                final String licenseEditionsString = taskJson.get((Object)"License_Edition.License_Edition_Code").toString();
                final String licenseEditions = ("".equals(licenseEditionsString) || "[]".equals(licenseEditionsString)) ? "" : new org.json.JSONArray(licenseEditionsString).toString();
                if (!"".equals(licenseEditions)) {
                    extnTableRow.set("LICENSE_EDITION", (Object)licenseEditions);
                }
            }
            if (taskJson.containsKey((Object)"Product_Architecture")) {
                final String productArchitectureString = taskJson.get((Object)"Product_Architecture").toString();
                final String productArchitecture = ("".equals(productArchitectureString) || "[]".equals(productArchitectureString)) ? "" : new org.json.JSONArray(productArchitectureString).toString();
                if (!"".equals(productArchitecture)) {
                    extnTableRow.set("PRODUCT_ARCHITECTURE", (Object)productArchitecture);
                }
            }
            if (taskJson.containsKey((Object)"DB_Type")) {
                final String dbTypesString = taskJson.get((Object)"DB_Type").toString();
                final String dbTypes = ("".equals(dbTypesString) || "[]".equals(dbTypesString)) ? "" : new org.json.JSONArray(dbTypesString).toString();
                if (!"".equals(dbTypes)) {
                    extnTableRow.set("DB_TYPE", (Object)dbTypes);
                }
            }
            if ("Using Query".equalsIgnoreCase(dataFrom)) {
                row.set("DATA_FROM", (Object)1);
                final String sqlFor = taskJson.get((Object)"Sql_For").toString();
                if ("Common [Across all DB]".equalsIgnoreCase(sqlFor)) {
                    extnTableRow.set("SQL_FOR", (Object)1);
                    extnTableRow.set("COMMON_QUERY", (Object)taskJson.get((Object)"Common_Query").toString());
                }
                else {
                    if (!"Unique for DB".equalsIgnoreCase(sqlFor)) {
                        return null;
                    }
                    extnTableRow.set("SQL_FOR", (Object)2);
                    final String pgsqlQuery = taskJson.get((Object)"Postgres_Query").toString();
                    final String mssqlQuery = taskJson.get((Object)"Mssql_Query").toString();
                    if ("".equals(pgsqlQuery) && "".equals(mssqlQuery)) {
                        return null;
                    }
                    if (!"".equals(pgsqlQuery)) {
                        extnTableRow.set("POSTGRES_QUERY", (Object)pgsqlQuery);
                    }
                    if (!"".equals(mssqlQuery)) {
                        extnTableRow.set("MSSQL_QUERY", (Object)mssqlQuery);
                    }
                }
            }
            else if ("From Props/Conf File".equalsIgnoreCase(dataFrom)) {
                row.set("DATA_FROM", (Object)2);
                final String filePath = taskJson.get((Object)"File_Path").toString();
                final String keyName = taskJson.get((Object)"Key_Names").toString();
                if ("".equals(filePath) || "".equals(keyName)) {
                    return null;
                }
                extnTableRow.set("FILE_PATH", (Object)filePath);
                extnTableRow.set("KEY_NAMES", (Object)keyName);
            }
            else {
                if (!"From File".equalsIgnoreCase(dataFrom)) {
                    return null;
                }
                row.set("DATA_FROM", (Object)3);
                final String filePath = taskJson.get((Object)"File_Path").toString();
                if ("".equals(filePath)) {
                    return null;
                }
                extnTableRow.set("FILE_PATH", (Object)filePath);
                final String startsWith = taskJson.containsKey((Object)"Starts_With") ? taskJson.get((Object)"Starts_With").toString() : "";
                final String endsWith = taskJson.containsKey((Object)"Ends_With") ? taskJson.get((Object)"Ends_With").toString() : "";
                final String matches = taskJson.containsKey((Object)"Matches") ? taskJson.get((Object)"Matches").toString() : "";
                if ("".equals(startsWith) && "".equals(endsWith) && "".equals(matches)) {
                    return null;
                }
                if (!"".equals(startsWith)) {
                    extnTableRow.set("STARTS_WITH", (Object)startsWith);
                }
                if (!"".equals(endsWith)) {
                    extnTableRow.set("ENDS_WITH", (Object)endsWith);
                }
                if (!"".equals(matches)) {
                    extnTableRow.set("MATCHES", (Object)matches);
                }
            }
            rowSet.put("ONDemandDataCollectorTasks", row);
            rowSet.put("ONDemandDataCollectorTasksExtn", extnTableRow);
            return rowSet;
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "insertValues", "Exception occurred : ", (Throwable)e);
            return null;
        }
    }
    
    private void tasksCleanup() {
        try {
            final DataObject dataObject = DataAccess.get("ONDemandDataCollectorTasks", (Criteria)null);
            final DataObject newDataObject = DataAccess.get("ONDemandDataCollectorTasks", (Criteria)null);
            final Iterator rows = dataObject.getRows("ONDemandDataCollectorTasks");
            while (rows.hasNext()) {
                final Row row = rows.next();
                final Long taskReleasedTime = Long.valueOf(row.get("TASK_RELEASED_TIME").toString());
                final Long expireDays = Long.valueOf(row.get("EXPIRE_DAYS").toString());
                final int daysAfterTaskReleased = (int)((System.currentTimeMillis() - taskReleasedTime) / 86400000L);
                if (daysAfterTaskReleased >= expireDays) {
                    newDataObject.deleteRow(row);
                    SyMLogger.info(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "tasksCleanup", "Cleanup the Task : " + row.toString());
                }
            }
            DataAccess.update(newDataObject);
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDownloadHandler.logger, ONDemandDownloadHandler.sourceClass, "tasksCleanup", "Exception occurred : ", (Throwable)e);
        }
    }
    
    static {
        ONDemandDownloadHandler.logger = Logger.getLogger("METrackLog");
        ONDemandDownloadHandler.sourceClass = "ONDemandDownloadHandler";
    }
}
