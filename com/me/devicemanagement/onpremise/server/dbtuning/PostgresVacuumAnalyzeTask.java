package com.me.devicemanagement.onpremise.server.dbtuning;

import org.apache.commons.io.FileUtils;
import java.io.File;
import org.json.JSONObject;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.Calendar;
import java.util.Map;
import com.adventnet.db.api.RelationalAPI;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.onpremise.server.util.CommonUpdatesUtil;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class PostgresVacuumAnalyzeTask implements SchedulerExecutionInterface
{
    private Logger logger;
    private static Float thresholdCleanupTime;
    
    public PostgresVacuumAnalyzeTask() {
        this.logger = Logger.getLogger("DatabaseMaintenanceLogger");
        if (PostgresVacuumAnalyzeTask.thresholdCleanupTime == null || PostgresVacuumAnalyzeTask.thresholdCleanupTime == 0.0f) {
            this.getPgVacuumCleanupThresholdTime();
        }
    }
    
    public void executeTask(final Properties props) {
        String globalRun = CommonUpdatesUtil.getInstance().getValue("runPostgresCleanUp");
        if (globalRun == null) {
            globalRun = "true";
        }
        if (!DBUtil.getActiveDBName().equalsIgnoreCase("postgres") || globalRun.equalsIgnoreCase("false")) {
            this.logger.info("PostgresVacuumAnalyzeTask is disabled");
            return;
        }
        final long startTime = System.currentTimeMillis();
        this.logger.info("Inside PostgresVacuumAnalyzeTask ");
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        int tablesProcessed = 0;
        final HashMap<String, Float> longestExecutingTableMap = new HashMap<String, Float>();
        try {
            conn = RelationalAPI.getInstance().getConnection();
            statement = conn.createStatement();
            rs = RelationalAPI.getInstance().executeQueryForSQL("select relname,last_autovacuum,last_vacuum,last_autoanalyze,n_dead_tup,n_mod_since_analyze from pg_stat_all_tables where (relname NOT LIKE 'pg%' and relname not like 'sql_%') order by n_dead_tup desc ;", (Map)null, statement);
            final Calendar cal = Calendar.getInstance();
            final Date currentDate = cal.getTime();
            while (rs.next()) {
                final String tableName = rs.getString("relname");
                final Date lastAutoVacDate = rs.getDate("last_autovacuum");
                final Date lastVacDate = rs.getDate("last_vacuum");
                final long deadRows = rs.getLong("n_dead_tup");
                final long modifiedRows = rs.getLong("n_mod_since_analyze");
                String sqlQuery = "";
                if (modifiedRows > 1000L) {
                    sqlQuery = "analyze " + tableName + " ;";
                }
                if (deadRows > 5000L) {
                    sqlQuery = "vacuum analyze " + tableName + " ;";
                }
                if (lastVacDate != null) {
                    if (lastAutoVacDate == null) {
                        if (lastVacDate.getTime() - currentDate.getTime() > 2592000000L) {
                            sqlQuery = "vacuum analyze " + tableName + " ;";
                        }
                    }
                    else if (lastVacDate.getTime() - currentDate.getTime() > 2592000000L && lastAutoVacDate.getTime() - currentDate.getTime() > 2592000000L) {
                        sqlQuery = "vacuum analyze " + tableName + " ;";
                    }
                }
                else if (lastAutoVacDate != null) {
                    if (lastAutoVacDate.getTime() - currentDate.getTime() > 2592000000L) {
                        sqlQuery = "vacuum analyze " + tableName + " ;";
                    }
                }
                else {
                    sqlQuery = "vacuum analyze " + tableName + " ;";
                }
                if (!sqlQuery.isEmpty()) {
                    ++tablesProcessed;
                    try {
                        final long tableStartTime = System.currentTimeMillis();
                        RelationalAPI.getInstance().execute(conn, sqlQuery);
                        final long tableEndTime = System.currentTimeMillis();
                        final float execTime = (tableEndTime - tableStartTime) / 1000.0f / 60.0f;
                        if (execTime >= PostgresVacuumAnalyzeTask.thresholdCleanupTime) {
                            longestExecutingTableMap.put(tableName, execTime);
                        }
                        if (execTime <= 10.0f) {
                            continue;
                        }
                        this.logger.info("query " + sqlQuery + " has taken " + execTime + " minutes to execute.");
                    }
                    catch (final Exception ex) {
                        this.logger.log(Level.SEVERE, "Error while executing the query \"" + sqlQuery + "\"" + ex.getMessage());
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error in PostgresVacuumAnalyzeTask " + e.getMessage());
            try {
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Error in finally block  " + e.getMessage());
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Error in finally block  " + e2.getMessage());
            }
        }
        final long endTime = System.currentTimeMillis();
        final float execTime2 = (endTime - startTime) / 1000.0f / 60.0f;
        this.logger.info("Execution time =  " + execTime2 + " minutes");
        this.logger.info("Number of tables processed = " + tablesProcessed);
        this.logger.info("Exiting PostgresVacuumAnalyzeTask");
        this.setMeTrackData(execTime2, tablesProcessed, longestExecutingTableMap);
        this.rotatePgLogs();
    }
    
    private void setMeTrackData(final Float execTime, final int tablesProcessed, final HashMap<String, Float> longestExecutingTableMap) {
        SyMUtil.getInstance();
        com.me.devicemanagement.framework.server.util.SyMUtil.updateServerParameter("CleaningTime", Float.toString(execTime));
        SyMUtil.getInstance();
        com.me.devicemanagement.framework.server.util.SyMUtil.updateServerParameter("TablesCleaned", Integer.toString(tablesProcessed));
        SyMUtil.getInstance();
        com.me.devicemanagement.framework.server.util.SyMUtil.updateServerParameter("LongestTableCleanUpTime", new JSONObject((Map)longestExecutingTableMap).toString());
    }
    
    private void getPgVacuumCleanupThresholdTime() {
        final File jsonFile = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "DeviceManagementFramework" + File.separator + "configurations" + File.separator + "framework_settings_dc_1.json");
        try {
            final JSONObject settingsJson = new JSONObject(FileUtils.readFileToString(jsonFile, "UTF-8"));
            if (settingsJson.keySet().contains("pg_vacuum_analyse_task") && settingsJson.getJSONObject("pg_vacuum_analyse_task").keySet().contains("pg_vacuum_cleanup_time_threshold_metrack")) {
                PostgresVacuumAnalyzeTask.thresholdCleanupTime = settingsJson.getJSONObject("pg_vacuum_analyse_task").getFloat("pg_vacuum_cleanup_time_threshold_metrack");
            }
            else {
                this.logger.log(Level.SEVERE, "The required key for thresholdTime is not present in " + jsonFile.getAbsolutePath());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error while reading [" + jsonFile.getAbsolutePath() + "] file for getting pg_vacuum_cleanup_time_threshold_metrack", e);
        }
        finally {
            if (PostgresVacuumAnalyzeTask.thresholdCleanupTime == 0.0f) {
                PostgresVacuumAnalyzeTask.thresholdCleanupTime = 1.0f;
                this.logger.log(Level.INFO, "Since thresholdTime is not set from file, default value of 1 minute is set.");
            }
        }
    }
    
    private void rotatePgLogs() {
        try {
            final String destDir = System.getProperty("server.home") + File.separator + "logs" + File.separator + "postgres" + File.separator + "pg_log";
            final String srcDir = System.getProperty("server.home") + File.separator + "pgsql" + File.separator + "data" + File.separator + "pg_log";
            FileUtils.copyDirectory(new File(srcDir), new File(destDir));
            this.logger.log(Level.INFO, "Successfully moved the postgresql logs directory");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in rotatePgLogs: ", ex);
        }
    }
    
    static {
        PostgresVacuumAnalyzeTask.thresholdCleanupTime = 0.0f;
    }
}
