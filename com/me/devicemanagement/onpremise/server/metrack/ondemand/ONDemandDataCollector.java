package com.me.devicemanagement.onpremise.server.metrack.ondemand;

import java.util.Hashtable;
import com.me.devicemanagement.onpremise.server.service.DCServerBuildHistoryProvider;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import org.json.simple.JSONValue;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import com.adventnet.persistence.PersistenceInitializer;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import java.util.ArrayList;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class ONDemandDataCollector implements ONDemandDataCollectorApi
{
    private static Logger logger;
    private static String sourceClass;
    
    @Override
    public ONDemandDataCollectorBean getAllOndemandProperties() {
        ONDemandDataCollectorBean onDemandDataCollectorBean = new ONDemandDataCollectorBean();
        try {
            SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getAllOndemandProperties", "**************************************************************************************");
            SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getAllOndemandProperties", "ONDemandDataCollector implementation starts...");
            final ONDemandDownloadHandler onDemandDownloadHandler = new ONDemandDownloadHandler();
            onDemandDataCollectorBean = onDemandDownloadHandler.updateTasksFromZC(onDemandDataCollectorBean);
            SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getAllOndemandProperties", "Going to process 'Using Query' Tasks.");
            onDemandDataCollectorBean = this.processTasksFromDB(onDemandDataCollectorBean, 1);
            SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getAllOndemandProperties", "Going to process 'Props/Conf File' Tasks.");
            onDemandDataCollectorBean = this.processTasksFromDB(onDemandDataCollectorBean, 2);
            SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getAllOndemandProperties", "Going to process 'Log File' Tasks.");
            onDemandDataCollectorBean = this.processTasksFromDB(onDemandDataCollectorBean, 3);
            SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getAllOndemandProperties", "ONDemandDataCollector implementation ends...");
            SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getAllOndemandProperties", "**************************************************************************************");
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getAllOndemandProperties", "Exception occurred : ", (Throwable)e);
        }
        return onDemandDataCollectorBean;
    }
    
    private ONDemandDataCollectorBean processTasksFromDB(ONDemandDataCollectorBean onDemandDataCollectorBean, final int criteriaValue) {
        try {
            final ArrayList<JSONObject> tasks = this.fetchTasksFromDB(criteriaValue);
            SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "processTasksFromDB", "Tasks Count : " + tasks.size());
            for (final JSONObject taskJson : tasks) {
                if (this.thisTaskExecutableForThisCustomer(taskJson)) {
                    JSONObject taskValue = new JSONObject();
                    switch (criteriaValue) {
                        case 1: {
                            taskValue = this.getValueFromQuery(taskJson);
                            break;
                        }
                        case 2: {
                            taskValue = this.getValueFromPropsORconf(taskJson);
                            break;
                        }
                        case 3: {
                            taskValue = this.getValueFromLog(taskJson);
                            break;
                        }
                    }
                    onDemandDataCollectorBean = this.addValueTOONDemandBean(onDemandDataCollectorBean, taskJson, String.valueOf(taskValue));
                    SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "processTasksFromDB", "*--------------------------------------------------------------------------------------------------*");
                    SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "processTasksFromDB", "| Task Id            | Column Name    | Task Value                                                 |");
                    SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "processTasksFromDB", "|--------------------------------------------------------------------------------------------------|");
                    SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "processTasksFromDB", "| " + taskJson.get("TASK_ID") + " | " + taskJson.get("ZC_COLUMN_NAME") + "           | " + taskValue + " |");
                    SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "processTasksFromDB", "*--------------------------------------------------------------------------------------------------*");
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "processTasksFromDB", "Exception occurred : ", (Throwable)e);
        }
        return onDemandDataCollectorBean;
    }
    
    private ArrayList<JSONObject> fetchTasksFromDB(final int criteriaValue) {
        Connection con = null;
        DataSet dataSet = null;
        final ArrayList<JSONObject> tasks = new ArrayList<JSONObject>();
        try {
            final SelectQuery selectQuery = this.getTasks(new Criteria(Column.getColumn("ONDemandDataCollectorTasks", "DATA_FROM"), (Object)criteriaValue, 0));
            con = RelationalAPI.getInstance().getConnection();
            dataSet = RelationalAPI.getInstance().executeQuery((Query)selectQuery, con);
            final int dataSetColumnCount = dataSet.getColumnCount() + 1;
            while (dataSet.next()) {
                final JSONObject taskJson = new JSONObject();
                for (int i = 1; i < dataSetColumnCount; ++i) {
                    taskJson.put(dataSet.getColumnName(i), dataSet.getValue(i));
                }
                tasks.add(taskJson);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "fetchTasksFromDB", "Exception occurred : ", (Throwable)e);
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (final Exception e) {
                SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "fetchTasksFromDB", "Exception occurred : ", (Throwable)e);
            }
        }
        finally {
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (final Exception e2) {
                SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "fetchTasksFromDB", "Exception occurred : ", (Throwable)e2);
            }
        }
        return tasks;
    }
    
    private JSONObject getValueFromQuery(final JSONObject taskJson) {
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        final JSONObject taskValue = new JSONObject();
        try {
            String sqlCommand = "";
            final JSONArray jsonArray = new JSONArray();
            final int sqlFor = taskJson.getInt("SQL_FOR");
            if (1 == sqlFor) {
                sqlCommand = taskJson.get("COMMON_QUERY").toString();
            }
            else {
                if (2 != sqlFor) {
                    taskValue.put("Status", (Object)"Invalid Task");
                    return taskValue;
                }
                final String currentDB = PersistenceInitializer.getConfigurationValue("DBName");
                sqlCommand = ("postgres".equalsIgnoreCase(currentDB) ? (taskJson.has("POSTGRES_QUERY") ? taskJson.get("POSTGRES_QUERY").toString() : "") : ("mssql".equalsIgnoreCase(currentDB) ? (taskJson.has("MSSQL_QUERY") ? taskJson.get("MSSQL_QUERY").toString() : "") : ""));
                if ("".equalsIgnoreCase(sqlCommand)) {
                    taskValue.put("Status", (Object)"Invalid Task");
                    return taskValue;
                }
            }
            final int index = sqlCommand.indexOf(" ");
            final String subString = (index != -1) ? sqlCommand.trim().substring(0, index) : null;
            if (subString == null || (!subString.equalsIgnoreCase("select") && !subString.equalsIgnoreCase("select*") && !subString.equalsIgnoreCase("show") && !subString.equalsIgnoreCase("desc"))) {
                taskValue.put("Status", (Object)"Invalid Task");
                return taskValue;
            }
            String status = "Success";
            int existsLimit = 0;
            con = RelationalAPI.getInstance().getConnection();
            statement = con.createStatement();
            resultSet = RelationalAPI.getInstance().executeQueryForSQL(sqlCommand, (Map)null, statement);
            final ResultSetMetaData rsmd = resultSet.getMetaData();
            final int dataSetColumnCount = rsmd.getColumnCount() + 1;
            while (resultSet.next()) {
                final JSONObject rowValues = new JSONObject();
                for (int i = 1; i < dataSetColumnCount; ++i) {
                    rowValues.put(rsmd.getColumnName(i), (Object)resultSet.getString(i));
                }
                final int currentRecordLength = rowValues.toString().length();
                if (existsLimit + currentRecordLength >= 20000) {
                    status = "Data Limit Reached";
                    SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getValueFromLog", "Data Limit Reached");
                    break;
                }
                jsonArray.add((Object)rowValues);
                existsLimit += currentRecordLength;
            }
            taskValue.put("Output", (Collection)jsonArray);
            taskValue.put("Status", (Object)status);
        }
        catch (final Exception e) {
            try {
                SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getValueFromQuery", "Exception occurred : ", (Throwable)e);
                taskValue.put("Status", (Object)("Exception : " + e.getMessage()));
            }
            catch (final Exception ee) {
                SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getValueFromQuery", "Exception occurred : ", (Throwable)ee);
            }
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (con != null) {
                    con.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e) {
                SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getValueFromQuery", "Exception occurred : ", (Throwable)e);
            }
        }
        finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (con != null) {
                    con.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e2) {
                SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getValueFromQuery", "Exception occurred : ", (Throwable)e2);
            }
        }
        return taskValue;
    }
    
    private JSONObject getValueFromPropsORconf(final JSONObject taskJson) {
        final JSONObject taskValue = new JSONObject();
        try {
            final String fileDirFromserverHome = taskJson.get("FILE_PATH").toString();
            final String[] keyNames = taskJson.get("KEY_NAMES").toString().split(",");
            final Properties properties = FileAccessUtil.readProperties(ONDemandDataCollectorUtil.getInstance().getFileFullDir(fileDirFromserverHome));
            for (final String keyName : keyNames) {
                if (properties.containsKey(keyName)) {
                    taskValue.put(keyName, (Object)((Hashtable<K, Object>)properties).get(keyName).toString());
                }
                else {
                    taskValue.put(keyName, (Object)"Key Not Found");
                    SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getValueFromPropsORconf", "Key Not Found : " + keyName);
                }
            }
            taskValue.put("Status", (Object)"Success");
        }
        catch (final Exception e) {
            try {
                SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getValueFromPropsORconf", "Exception occurred : ", (Throwable)e);
                taskValue.put("Status", (Object)("Exception : " + e.getMessage()));
            }
            catch (final Exception ee) {
                SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getValueFromPropsORconf", "Exception occurred : ", (Throwable)ee);
            }
        }
        return taskValue;
    }
    
    private JSONObject getValueFromLog(final JSONObject taskJson) {
        final JSONObject taskValue = new JSONObject();
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            String status = "No Matches Rows";
            final StringBuilder stringBuilder = new StringBuilder(20000);
            if (taskJson.has("STARTS_WITH") || taskJson.has("ENDS_WITH") || taskJson.has("MATCHES")) {
                final String fileDirFromserverHome = taskJson.get("FILE_PATH").toString();
                final String fileDir = ONDemandDataCollectorUtil.getInstance().getFileFullDir(fileDirFromserverHome);
                if (new File(fileDir).exists()) {
                    inputStream = ApiFactoryProvider.getFileAccessAPI().readFile(fileDir);
                    inputStreamReader = new InputStreamReader(inputStream);
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        line = line.trim();
                        if (line.length() > 0) {
                            final boolean startWith = !taskJson.has("STARTS_WITH") || line.startsWith(taskJson.get("STARTS_WITH").toString());
                            final boolean endWith = !taskJson.has("ENDS_WITH") || line.endsWith(taskJson.get("ENDS_WITH").toString());
                            final boolean matches = !taskJson.has("MATCHES") || line.matches(taskJson.get("MATCHES").toString());
                            if (!startWith || !endWith || !matches) {
                                continue;
                            }
                            if (stringBuilder.length() + line.length() >= 20000) {
                                status = "Data Limit Reached";
                                SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getValueFromLog", "Data Limit Reached");
                                final int availableSize = 20000 - stringBuilder.length() + line.length();
                                stringBuilder.append(line.substring(0, Math.min(0 + availableSize, line.length())));
                                break;
                            }
                            stringBuilder.append(line + "\n");
                            status = "Success";
                        }
                    }
                    taskValue.put("Output", (Object)stringBuilder.toString());
                    taskValue.put("Status", (Object)status);
                }
                else {
                    taskValue.put("Status", (Object)"File Not Found");
                }
            }
            else {
                taskValue.put("Status", (Object)"Invalid Task");
            }
        }
        catch (final Exception e) {
            try {
                SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getValueFromLog", "Exception occurred : ", (Throwable)e);
                taskValue.put("Status", (Object)("Exception : " + e.getMessage()));
            }
            catch (final Exception ee) {
                SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getValueFromLog", "Exception occurred : ", (Throwable)ee);
            }
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (final Exception e) {
                SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getValueFromLog", "Exception occurred : ", (Throwable)e);
            }
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (final Exception e2) {
                SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getValueFromLog", "Exception occurred : ", (Throwable)e2);
            }
        }
        return taskValue;
    }
    
    private ONDemandDataCollectorBean addValueTOONDemandBean(final ONDemandDataCollectorBean onDemandDataCollectorBean, final JSONObject taskJson, final String value) {
        try {
            if (1 == taskJson.getInt("DATA_POSTED_TO")) {
                final Properties properties = new Properties();
                ((Hashtable<String, String>)properties).put("taskid", taskJson.get("TASK_ID").toString());
                ((Hashtable<String, String>)properties).put(taskJson.get("ZC_COLUMN_NAME").toString(), value);
                onDemandDataCollectorBean.setDefaultFormRecords(taskJson.get("ZC_FORM_NAME").toString(), properties);
            }
            else if (2 == taskJson.getInt("DATA_POSTED_TO")) {
                onDemandDataCollectorBean.setNonDefaultFormRecords(taskJson.get("ZC_FORM_NAME").toString(), taskJson.get("ZC_COLUMN_NAME").toString(), value);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "addValueTOONDemandBean", "Exception occurred : ", (Throwable)e);
        }
        return onDemandDataCollectorBean;
    }
    
    private SelectQuery getTasks(final Criteria criteria) {
        try {
            final SelectQueryImpl selectQuery = new SelectQueryImpl(Table.getTable("ONDemandDataCollectorTasks"));
            selectQuery.addSelectColumn(Column.getColumn("ONDemandDataCollectorTasks", "*"));
            selectQuery.addSelectColumn(Column.getColumn("ONDemandDataCollectorTasksExtn", "*"));
            selectQuery.addJoin(new Join("ONDemandDataCollectorTasks", "ONDemandDataCollectorTasksExtn", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 1));
            selectQuery.setCriteria(criteria);
            return (SelectQuery)selectQuery;
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "getTasks", "Exception occurred : ", (Throwable)e);
            return null;
        }
    }
    
    private boolean thisTaskExecutableForThisCustomer(final JSONObject taskJson) {
        try {
            if (!this.buildNumberCheck(taskJson)) {
                SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "thisTaskExecutableForThisCustomer", "Build Number Not Applicable : " + taskJson);
                return false;
            }
            Label_0189: {
                if (taskJson.has("DB_TYPE")) {
                    if (!this.dbCheck(taskJson)) {
                        SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "thisTaskExecutableForThisCustomer", "DB Not Applicable : " + taskJson);
                        return false;
                    }
                }
                else if (taskJson.getInt("DATA_FROM") == 1 && taskJson.getInt("SQL_FOR") == 2) {
                    final String currentDB = PersistenceInitializer.getConfigurationValue("DBName");
                    if ("postgres".equalsIgnoreCase(currentDB)) {
                        if (taskJson.has("POSTGRES_QUERY")) {
                            break Label_0189;
                        }
                    }
                    else if ("mssql".equalsIgnoreCase(currentDB) && taskJson.has("MSSQL_QUERY")) {
                        break Label_0189;
                    }
                    SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "thisTaskExecutableForThisCustomer", "DB Not Applicable : " + taskJson);
                    return false;
                }
            }
            if (taskJson.has("LICENSE_TYPE") && !this.licenseTypeCheck(taskJson)) {
                SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "thisTaskExecutableForThisCustomer", "License Type Not Applicable : " + taskJson);
                return false;
            }
            if (taskJson.has("LICENSE_EDITION") && !this.licenseEditionCheck(taskJson)) {
                SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "thisTaskExecutableForThisCustomer", "License Edition Not Applicable : " + taskJson);
                return false;
            }
            if (taskJson.has("PRODUCT_ARCHITECTURE") && !this.productArchitectureCheck(taskJson)) {
                SyMLogger.info(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "thisTaskExecutableForThisCustomer", "Product Architecture Not Applicable : " + taskJson);
                return false;
            }
            return true;
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "thisTaskExecutableForThisCustomer", "Exception occurred : ", (Throwable)e);
            return false;
        }
    }
    
    private boolean productArchitectureCheck(final JSONObject taskJson) {
        try {
            final JSONArray applicableProductArchitecture = (JSONArray)JSONValue.parse(taskJson.get("PRODUCT_ARCHITECTURE").toString());
            if (StartupUtil.isDCProduct64bit()) {
                return applicableProductArchitecture.contains((Object)"64bit");
            }
            return applicableProductArchitecture.contains((Object)"32bit");
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "productArchitectureCheck", "Exception occurred : ", (Throwable)e);
            return false;
        }
    }
    
    private boolean licenseEditionCheck(final JSONObject taskJson) {
        try {
            final String currentLicenseEdition = LicenseProvider.getInstance().getProductType();
            final JSONArray applicableLicenseEdition = (JSONArray)JSONValue.parse(taskJson.get("LICENSE_EDITION").toString());
            return ONDemandDataCollectorUtil.getInstance().ifAValueExistsINJSONArray(applicableLicenseEdition, currentLicenseEdition);
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "licenseEditionCheck", "Exception occurred : ", (Throwable)e);
            return false;
        }
    }
    
    private boolean licenseTypeCheck(final JSONObject taskJson) {
        try {
            final String currentLicenseType = LicenseProvider.getInstance().getLicenseType();
            final JSONArray applicableLicenseTypes = (JSONArray)JSONValue.parse(taskJson.get("LICENSE_TYPE").toString());
            return ONDemandDataCollectorUtil.getInstance().ifAValueExistsINJSONArray(applicableLicenseTypes, currentLicenseType);
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "thisTaskExecutableForThisCustomer", "Exception occurred : ", (Throwable)e);
            return false;
        }
    }
    
    private boolean dbCheck(final JSONObject taskJson) {
        try {
            final String currentDB = PersistenceInitializer.getConfigurationValue("DBName");
            final JSONArray applicableDBNames = (JSONArray)JSONValue.parse(taskJson.get("DB_TYPE").toString());
            return ONDemandDataCollectorUtil.getInstance().ifAValueExistsINJSONArray(applicableDBNames, currentDB);
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "dbCheck", "Exception occurred : ", (Throwable)e);
            return false;
        }
    }
    
    private boolean buildNumberCheck(final JSONObject taskJson) {
        try {
            final Long currentBuildNumberFromDB = (Long)DCServerBuildHistoryProvider.getInstance().getCurrentBuildNumberFromDB();
            final Long minBuildNum = taskJson.has("MIN_BUILD_NUMBER") ? Long.valueOf(taskJson.get("MIN_BUILD_NUMBER").toString()) : -1L;
            return currentBuildNumberFromDB >= minBuildNum && (!taskJson.has("MAX_BUILD_NUMBER") || currentBuildNumberFromDB <= Long.valueOf(taskJson.get("MAX_BUILD_NUMBER").toString()));
        }
        catch (final Exception e) {
            SyMLogger.error(ONDemandDataCollector.logger, ONDemandDataCollector.sourceClass, "buildNumberCheck", "Exception occurred : ", (Throwable)e);
            return false;
        }
    }
    
    static {
        ONDemandDataCollector.logger = Logger.getLogger("METrackLog");
        ONDemandDataCollector.sourceClass = "ONDemandDataCollector";
    }
}
