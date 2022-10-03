package com.me.devicemanagement.onpremise.tools.dbmigration.metrack;

import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.me.devicemanagement.onpremise.tools.dbmigration.utils.DBMigrationUtils;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBMMETracker
{
    private static final Logger LOGGER;
    private static DBMMETracker dbmMETrackerInstance;
    public static final String DBM_START_TIME = "dbm_start_time";
    public static final int DBM_SUCCESS = 1;
    public static final int DBM_FAILURE = 2;
    private static final String DBM_METRACK_FILE_PATH;
    private static final int TRACKER_DETAILS_HISTORY_COUNT = 3;
    private static final String DBM_HISTORY_KEY = "dbm-history";
    private static final String LAST_MIGRATION_TIME_TAKEN_KEY = "last-dbm-dur";
    private static final String DBM_FAILURE_HISTORY_KEY = "dbm-failed-build";
    public static final String MYSQL = "mysql";
    public static final String PGSQL = "bundled postgres";
    public static final String MSSQL = "mssql";
    public static final String REMOTE_PGSQL = "remote postgres";
    private static final int MYSQL_TO_PGSQL = 1;
    private static final int MYSQL_TO_MSSQL = 4;
    private static final int PGSQL_TO_MSSQL = 2;
    private static final int PGSQL_TO_REMOTE_PGSQL = 5;
    private static final int MSSQL_TO_MSSQL = 3;
    private static final int MSSQL_TO_REMOTE_PGSQL = 6;
    private static final int MSSQL_TO_PGSQL = 7;
    private static final int REMOTE_PGSQL_TO_MSSQL = 8;
    private static final int REMOTE_PGSQL_TO_REMOTE_PGSQL = 9;
    private static final int REMOTE_PGSQL_TO_PGSQL = 10;
    String sourceDB;
    String destDB;
    
    private DBMMETracker() {
    }
    
    public static DBMMETracker getInstance() {
        if (DBMMETracker.dbmMETrackerInstance == null) {
            DBMMETracker.dbmMETrackerInstance = new DBMMETracker();
        }
        return DBMMETracker.dbmMETrackerInstance;
    }
    
    public void addDBMTracker(final int dbmStatus, final String sourceDB, final String destDB) {
        DBMMETracker.LOGGER.log(Level.INFO, "Going to write METracking details for DB migration");
        try {
            this.sourceDB = sourceDB.toLowerCase();
            this.destDB = destDB.toLowerCase();
            final JSONObject dbmMETrackerJSON = this.loadExistingTrackerJsonFile();
            if (dbmStatus == 1) {
                this.setDBMHistory(dbmMETrackerJSON);
                this.setDBMTimeTaken(dbmMETrackerJSON);
            }
            else {
                this.setDBMFailureHistory(dbmMETrackerJSON);
            }
            DBMigrationUtils.writeJSONFile(dbmMETrackerJSON, new File(DBMMETracker.DBM_METRACK_FILE_PATH));
        }
        catch (final Exception ex) {
            DBMMETracker.LOGGER.log(Level.WARNING, "Caught exception in writing DB Migration tracker details : ", ex);
        }
        DBMMETracker.LOGGER.log(Level.INFO, "METracking details for DB migration are updated successfully");
    }
    
    private void setDBMTimeTaken(final JSONObject dbmMETrackerJSON) throws JSONException {
        final long dbmStartTime = Long.parseLong(System.getProperty("dbm_start_time"));
        final long dbmTimeTaken = System.currentTimeMillis() - dbmStartTime;
        dbmMETrackerJSON.put("last-dbm-dur", dbmTimeTaken);
    }
    
    private void setDBMHistory(final JSONObject dbmMETrackerJSON) throws JSONException {
        JSONArray dbmHistory;
        if (dbmMETrackerJSON.has("dbm-history")) {
            dbmHistory = dbmMETrackerJSON.getJSONArray("dbm-history");
        }
        else {
            dbmHistory = new JSONArray();
        }
        int dbmType = 0;
        if (this.sourceDB.equalsIgnoreCase("mysql") && this.destDB.equalsIgnoreCase("bundled postgres")) {
            dbmType = 1;
        }
        else if (this.sourceDB.equalsIgnoreCase("mysql") && this.destDB.equalsIgnoreCase("mssql")) {
            dbmType = 4;
        }
        else if (this.sourceDB.equalsIgnoreCase("mssql") && this.destDB.equalsIgnoreCase("mssql")) {
            dbmType = 3;
        }
        else if (this.sourceDB.equalsIgnoreCase("bundled postgres") && this.destDB.equalsIgnoreCase("mssql")) {
            dbmType = 2;
        }
        else if (this.sourceDB.equalsIgnoreCase("bundled postgres") && this.destDB.equalsIgnoreCase("remote postgres")) {
            dbmType = 5;
        }
        else if (this.sourceDB.equalsIgnoreCase("mssql") && this.destDB.equalsIgnoreCase("remote postgres")) {
            dbmType = 6;
        }
        else if (this.sourceDB.equalsIgnoreCase("mssql") && this.destDB.equalsIgnoreCase("bundled postgres")) {
            dbmType = 7;
        }
        else if (this.sourceDB.equalsIgnoreCase("remote postgres") && this.destDB.equalsIgnoreCase("remote postgres")) {
            dbmType = 9;
        }
        else if (this.sourceDB.equalsIgnoreCase("remote postgres") && this.destDB.equalsIgnoreCase("bundled postgres")) {
            dbmType = 10;
        }
        else if (this.sourceDB.equalsIgnoreCase("remote postgres") && this.destDB.equalsIgnoreCase("mssql")) {
            dbmType = 8;
        }
        dbmHistory.put(dbmType);
        dbmMETrackerJSON.put("dbm-history", (Object)dbmHistory);
    }
    
    private void setDBMFailureHistory(final JSONObject trackerJSON) throws JSONException {
        JSONArray dbmFailureHistory;
        if (trackerJSON.has("dbm-failed-build")) {
            dbmFailureHistory = trackerJSON.getJSONArray("dbm-failed-build");
        }
        else {
            dbmFailureHistory = new JSONArray();
        }
        final String buildNumberString = BackupRestoreUtil.getInstance().getBuildNumber().trim();
        final int buildNumber = Integer.parseInt(buildNumberString);
        if (!dbmFailureHistory.toString().contains(buildNumberString)) {
            if (dbmFailureHistory.length() >= 3) {
                this.removeMinBuildNumber(dbmFailureHistory);
            }
            dbmFailureHistory.put(buildNumber);
        }
        trackerJSON.put("dbm-failed-build", (Object)dbmFailureHistory);
    }
    
    private void removeMinBuildNumber(final JSONArray buildArray) throws JSONException {
        int minBuildNumberIndex = 0;
        int minBuildNumber = buildArray.getInt(minBuildNumberIndex);
        for (int i = 1; i < buildArray.length(); ++i) {
            final int buildNumber = buildArray.getInt(i);
            if (minBuildNumber > buildNumber) {
                minBuildNumber = buildNumber;
                minBuildNumberIndex = i;
            }
        }
        buildArray.remove(minBuildNumberIndex);
    }
    
    private JSONObject loadExistingTrackerJsonFile() {
        JSONObject jsonObject = null;
        try {
            jsonObject = DBMigrationUtils.loadJSONFile(DBMMETracker.DBM_METRACK_FILE_PATH);
        }
        catch (final Exception ex) {
            DBMMETracker.LOGGER.log(Level.WARNING, "Caught exception is loading DBM tracker JSON file : ", ex);
        }
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        return jsonObject;
    }
    
    static {
        LOGGER = Logger.getLogger(DBMMETracker.class.getName());
        DBMMETracker.dbmMETrackerInstance = null;
        DBM_METRACK_FILE_PATH = System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "MeTrack" + File.separator + "DBMTrackingDetails.json";
    }
}
