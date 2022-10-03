package com.me.devicemanagement.onpremise.server.troubleshooter.postgres;

import java.util.Hashtable;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Collections;
import java.util.Set;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.io.Reader;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;
import java.io.File;
import org.json.simple.JSONObject;
import java.util.logging.Level;
import org.json.simple.JSONArray;
import java.util.logging.Logger;

public class PostgresCorruptionDetectionUtil
{
    private static PostgresCorruptionDetectionUtil pgsqlCorrDetection;
    private static final Logger LOGGER;
    
    private PostgresCorruptionDetectionUtil() {
    }
    
    public static synchronized PostgresCorruptionDetectionUtil getInstance() {
        if (PostgresCorruptionDetectionUtil.pgsqlCorrDetection == null) {
            PostgresCorruptionDetectionUtil.pgsqlCorrDetection = new PostgresCorruptionDetectionUtil();
        }
        return PostgresCorruptionDetectionUtil.pgsqlCorrDetection;
    }
    
    public boolean checkForCorruption(final String exceptionMsg) {
        boolean isCorruptionPresent = false;
        boolean disableCorruptionDetection = false;
        JSONArray latestInfoJsonArray = new JSONArray();
        JSONArray corruptionHistory = new JSONArray();
        try {
            final PostgresCorruptionJson configuration = PostgresCorruptionJson.getInstance();
            disableCorruptionDetection = configuration.isDisableCorruptionDetection();
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Is Corruption Detection is required ? " + !disableCorruptionDetection);
            if (disableCorruptionDetection) {
                return false;
            }
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Since Corruption Detection is Enabled, going to check if the corruption is present");
            final Map<Long, JSONObject> pgSqlExceptionMap = configuration.getPgSqlExceptionMap();
            for (final Long exceptionId : pgSqlExceptionMap.keySet()) {
                final JSONObject exceptionJson = pgSqlExceptionMap.get(exceptionId);
                final String regex = (String)exceptionJson.get((Object)"regex");
                if (exceptionMsg.matches(regex)) {
                    isCorruptionPresent = true;
                    PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Since the exception msg: " + exceptionMsg + " matches regex: " + regex + ", corruption is detected. @ ID : " + exceptionId);
                    final File corruptionFile = new File(PostgresCorruptionConstant.CORRUPTION_FILE);
                    final JSONObject corruptionFileDataJSON = this.getJsonFromFile(corruptionFile);
                    PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Corruption Data present in corruptionInfo.json file : " + corruptionFileDataJSON);
                    if (corruptionFileDataJSON.get((Object)"history") != null) {
                        corruptionHistory = (JSONArray)corruptionFileDataJSON.get((Object)"history");
                    }
                    final JSONObject newCorruptionJson = this.getNewCorruptionJson(exceptionJson, exceptionMsg);
                    boolean isExceptionUnique;
                    if (corruptionFileDataJSON.get((Object)"current") != null) {
                        latestInfoJsonArray = (JSONArray)corruptionFileDataJSON.get((Object)"current");
                        PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Latest Corruption Info from file:  " + latestInfoJsonArray);
                        isExceptionUnique = !this.updateCurrentCorruption(latestInfoJsonArray, newCorruptionJson);
                    }
                    else {
                        isExceptionUnique = true;
                    }
                    PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "is Exception Unique :  " + isExceptionUnique);
                    if (isExceptionUnique) {
                        PostgresCorruptionDetectionUtil.LOGGER.log(Level.FINE, "Since exception is unique, going to add exception msg to json object");
                        PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Going to update the latest corruption info");
                        PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "New unique corruption Json : " + newCorruptionJson.toJSONString());
                        PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Check from specific props, Create Lock file ? " + configuration.isCreateLockFile());
                        if (configuration.isCreateLockFile()) {
                            this.createCorruptionLockFile(exceptionMsg, configuration.isNotifyCustomerthrMail());
                        }
                        newCorruptionJson.remove((Object)"msg");
                        latestInfoJsonArray.add((Object)newCorruptionJson);
                    }
                    corruptionFileDataJSON.put((Object)"current", (Object)latestInfoJsonArray);
                    corruptionFileDataJSON.put((Object)"history", (Object)corruptionHistory);
                    this.writeFile(corruptionFile, corruptionFileDataJSON.toJSONString(), false);
                    break;
                }
            }
        }
        catch (final Exception ex) {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.SEVERE, "Exception caught while writing corruption info into a file: ", ex);
        }
        if (!disableCorruptionDetection) {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Is Corruption Present ? " + isCorruptionPresent);
        }
        return isCorruptionPresent;
    }
    
    private JSONObject getJsonFromFile(final File file) throws IOException, ParseException {
        JSONObject jsonObject = null;
        if (!file.exists() || file.length() <= 0L) {
            file.createNewFile();
            this.writeFile(file, "{}", false);
        }
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            final JSONParser parser = new JSONParser();
            jsonObject = (JSONObject)parser.parse((Reader)fileReader);
        }
        catch (final Exception ex) {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.WARNING, "Failed Load the JSON File : " + file);
            return new JSONObject();
        }
        finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
        return jsonObject;
    }
    
    private void writeFile(final File file, final String content, final boolean append) throws IOException {
        PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Going to write \" " + content + " \" in file : " + file.getCanonicalPath());
        FileWriter fileWriter = null;
        BufferedWriter writer = null;
        try {
            fileWriter = new FileWriter(file, append);
            writer = new BufferedWriter(fileWriter);
            writer.write(content);
            writer.flush();
        }
        catch (final Exception e) {
            throw new IOException("Unable write JSON File :" + file.getAbsolutePath(), e);
        }
        finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    public boolean updateBackupTypeStatus(final String type, final boolean isTaken) {
        final File file = new File(PostgresCorruptionConstant.CORRUPTION_FILE);
        if (!file.exists()) {
            return false;
        }
        try {
            final JSONObject jsonObject = this.getJsonFromFile(file);
            if (type.equals("binary")) {
                jsonObject.put((Object)"lastBinary", (Object)isTaken);
            }
            if (type.equals("dump")) {
                jsonObject.put((Object)"lastDump", (Object)isTaken);
            }
            this.writeFile(file, jsonObject.toJSONString(), false);
        }
        catch (final Exception e) {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.WARNING, "Failed update the backup status the JSON File : " + file, e);
            return false;
        }
        return true;
    }
    
    private void createCorruptionLockFile(final String exceptionMsg, final boolean notifyCustomerthrMail) {
        try {
            final File lockFile = new File(PostgresCorruptionConstant.CORRUPTION_LOCK_FILE);
            if (lockFile.exists()) {
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "{0} file already exists. Is deleted ? ", lockFile.delete());
            }
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DBBackupInfo"));
            query.addSelectColumn(Column.getColumn("DBBackupInfo", "BACKUP_DIR"));
            final DataObject data = SyMUtil.getPersistence().get(query);
            final Row dcBackupInfoRow = data.getRow("DBBackupInfo");
            String dbBackupFileLocation = null;
            if (dcBackupInfoRow != null) {
                dbBackupFileLocation = (String)dcBackupInfoRow.get("BACKUP_DIR");
            }
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.WARNING, "DB Backup Location from DBBACKUPINFO table: " + dbBackupFileLocation);
            if (dbBackupFileLocation == null || dbBackupFileLocation.equals("") || dbBackupFileLocation.equals("--") || dbBackupFileLocation.startsWith("..")) {
                dbBackupFileLocation = SyMUtil.getInstallationDir() + File.separator + "ScheduledDBBackup";
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.WARNING, "Default Backup Location: " + dbBackupFileLocation);
            }
            final Properties corruptionProps = new Properties();
            corruptionProps.setProperty("notify.cust.thru.mail", String.valueOf(notifyCustomerthrMail));
            corruptionProps.setProperty("db.backup.location", dbBackupFileLocation);
            corruptionProps.setProperty("msg", exceptionMsg);
            FileAccessUtil.storeProperties(corruptionProps, lockFile.getAbsolutePath(), false);
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "corruption.lock file created successfully");
        }
        catch (final Exception e) {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.SEVERE, "Exception caught while creating lock file: ", e);
        }
    }
    
    private JSONObject getNewCorruptionJson(final JSONObject exceptionJson, final String exceptionMsg) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put((Object)"id", exceptionJson.get((Object)"id"));
        jsonObject.put((Object)"msg", (Object)exceptionMsg);
        jsonObject.put((Object)"detected", (Object)System.currentTimeMillis());
        jsonObject.put((Object)"recent", (Object)System.currentTimeMillis());
        jsonObject.put((Object)"count", (Object)1L);
        if (Boolean.parseBoolean((String)exceptionJson.get((Object)"TableInfoRequired")) && Boolean.parseBoolean((String)exceptionJson.get((Object)"using_relfilenode"))) {
            final String corruptedTableName = this.getCorruptedTableName(exceptionJson, exceptionMsg);
            if (corruptedTableName != null) {
                jsonObject.put((Object)"table", (Object)corruptedTableName);
            }
        }
        return jsonObject;
    }
    
    private String getCorruptedTableName(final JSONObject exceptionJson, final String exceptionMsg) {
        String corruptedTableName = null;
        final int relfilenode = this.getRelfilenodeFromExceptionMsg(exceptionMsg);
        if (relfilenode > -1) {
            String sqlQueryforCorruptedTable = (String)exceptionJson.get((Object)"query");
            sqlQueryforCorruptedTable = sqlQueryforCorruptedTable + relfilenode + ";";
            corruptedTableName = this.getCorruptedTableName(sqlQueryforCorruptedTable);
        }
        return corruptedTableName;
    }
    
    private int getRelfilenodeFromExceptionMsg(final String exceptionMsg) {
        int relfilenode = -1;
        final Pattern pattern = Pattern.compile("(.*)(base/\\d*/\\d*)(.*)");
        try {
            final Matcher matcher = pattern.matcher(exceptionMsg);
            if (matcher.find()) {
                final String patternString = matcher.group(2);
                relfilenode = Integer.parseInt(patternString.split("/")[2]);
            }
        }
        catch (final Exception e) {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.SEVERE, "Exception caught while extracting relfilenode from SQLExceptionMsg: " + e);
        }
        return relfilenode;
    }
    
    private String getCorruptedTableName(final String query) {
        String tableName = null;
        Connection conn = null;
        DataSet dataSet = null;
        try {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Going to get the name of corrupted table");
            conn = RelationalAPI.getInstance().getConnection();
            dataSet = RelationalAPI.getInstance().executeQuery(query, conn);
            while (dataSet.next()) {
                tableName = dataSet.getAsString("CORRUPTED_TABLE_NAME").trim();
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Corrupted Table Name: " + tableName);
            }
        }
        catch (final Exception ex) {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, " Exception message " + ex.getMessage());
            if (ex.getMessage().equalsIgnoreCase("No results were returned by the query")) {
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Query executed, No rows found.");
            }
            else {
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.WARNING, "Unable to get Index Corrupted tables due to the Exception : ", ex);
            }
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception e) {
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.WARNING, "Exception while closing dataset : ", e);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.WARNING, "Exception while closing connection : ", e);
            }
        }
        finally {
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception e2) {
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.WARNING, "Exception while closing dataset : ", e2);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e2) {
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.WARNING, "Exception while closing connection : ", e2);
            }
        }
        return tableName;
    }
    
    public void moveCorruptionPropstoHistory() {
        try {
            final String corruptionLock = System.getProperty("server.home") + File.separator + "bin" + File.separator + "corruption.lock";
            final Properties properties = FileAccessUtil.readProperties(corruptionLock);
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Going to fetch properties from corruption.lock .. : " + properties);
            final boolean prevRestoreSuccess = Boolean.parseBoolean(((Hashtable<K, String>)properties).get("restore.successful"));
            if (prevRestoreSuccess) {
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.FINE, "Since previous restore is successful, going to check if corruption props have to be cleared..");
                final boolean clearCorrProps = Boolean.parseBoolean(((Hashtable<K, String>)properties).get("clear.corruption.props"));
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "clear corruption props: " + clearCorrProps);
                if (clearCorrProps) {
                    this.clearLatestCorruptionProps();
                }
            }
        }
        catch (final Exception e) {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.WARNING, "Exception caught while moving latest corruption info to corruption history", e);
        }
    }
    
    public void clearLatestCorruptionProps() {
        try {
            final File corruptionFile = new File(PostgresCorruptionConstant.CORRUPTION_FILE);
            final JSONObject corruptionFileDataJSON = this.getJsonFromFile(corruptionFile);
            final JSONArray corruptionProps = (JSONArray)corruptionFileDataJSON.get((Object)"current");
            final JSONArray corruptionHistory = (JSONArray)corruptionFileDataJSON.get((Object)"history");
            if (corruptionProps != null) {
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Going to add LatestCorruptionInfo into Corruption History Obj..");
                final JSONObject jsonObject = this.moveToHistory(corruptionFileDataJSON);
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "corruptionFileDataJSON : " + corruptionFileDataJSON.toJSONString());
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "After moved to History : " + jsonObject.toJSONString());
                this.writeFile(corruptionFile, jsonObject.toJSONString(), false);
            }
            final File corruptionLockFile = new File(PostgresCorruptionConstant.CORRUPTION_LOCK_FILE);
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Going to delete corruption.lock file.. " + corruptionLockFile.delete());
        }
        catch (final Exception e) {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.WARNING, "Exception while clearing corruption props", e);
        }
    }
    
    public void clearIndexCorruptionProps() {
        try {
            final File corruptionFile = new File(PostgresCorruptionConstant.CORRUPTION_FILE);
            final JSONObject corruptionFileDataJSON = this.getJsonFromFile(corruptionFile);
            final JSONArray corruptionProps = (JSONArray)corruptionFileDataJSON.get((Object)"current");
            for (int i = 0; i < corruptionProps.size(); ++i) {
                final JSONObject current = (JSONObject)corruptionProps.get(i);
                if (corruptionProps != null && (Integer.parseInt(current.get((Object)"id").toString()) == 8 || Integer.parseInt(current.get((Object)"id").toString()) == 1)) {
                    PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Going to add LatestCorruptionInfo into Corruption History Obj..");
                    JSONObject jsonObject = this.moveToHistory(8L, corruptionFileDataJSON);
                    jsonObject = this.moveToHistory(1L, jsonObject);
                    PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "corruptionFileDataJSON : " + corruptionFileDataJSON.toJSONString());
                    PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "After moved to History : " + jsonObject.toJSONString());
                    this.writeFile(corruptionFile, jsonObject.toJSONString(), false);
                    break;
                }
            }
        }
        catch (final Exception e) {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.WARNING, "Exception while clearing corruption props", e);
        }
    }
    
    private boolean updateCurrentCorruption(final JSONArray exceptionInfo, final JSONObject newCorruptionJson) {
        boolean isUpdate = false;
        try {
            final long id = (long)((newCorruptionJson.get((Object)"id") != null) ? newCorruptionJson.get((Object)"id") : 0L);
            final String table = (String)newCorruptionJson.get((Object)"table");
            for (int i = 0; i < exceptionInfo.size(); ++i) {
                final JSONObject json = (JSONObject)exceptionInfo.get(i);
                final String currentTable = (String)json.get((Object)"table");
                final long currentID = (long)((json.get((Object)"id") != null) ? json.get((Object)"id") : 0L);
                long currentCount = (long)((json.get((Object)"count") != null) ? json.get((Object)"count") : 0L);
                if (table != null && currentTable != null && currentTable.trim().equals(table)) {
                    if (id != 0L && currentID != 0L && currentID == id) {
                        if (currentID == 3L) {
                            final String errorMessage2 = (String)((json.get((Object)"errorMessage1") != null) ? json.get((Object)"errorMessage1") : "no data");
                            final String errorMessage3 = (String)newCorruptionJson.get((Object)"msg");
                            json.put((Object)"errorMessage1", (Object)errorMessage3);
                            json.put((Object)"errorMessage2", (Object)errorMessage2);
                        }
                        json.put((Object)"count", (Object)(++currentCount));
                        json.put((Object)"recent", (Object)System.currentTimeMillis());
                        isUpdate = true;
                        break;
                    }
                }
                else if (table == null && currentTable == null && id != 0L && currentID != 0L && currentID == id) {
                    if (currentID == 3L) {
                        final String errorMessage2 = (String)((json.get((Object)"errorMessage1") != null) ? json.get((Object)"errorMessage1") : "no data");
                        final String errorMessage3 = (String)newCorruptionJson.get((Object)"msg");
                        json.put((Object)"errorMessage1", (Object)errorMessage3);
                        json.put((Object)"errorMessage2", (Object)errorMessage2);
                    }
                    json.put((Object)"count", (Object)(++currentCount));
                    json.put((Object)"recent", (Object)System.currentTimeMillis());
                    isUpdate = true;
                    break;
                }
            }
        }
        catch (final Exception ex) {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.SEVERE, "Exception while trying to update current corruption", ex);
            isUpdate = false;
        }
        return isUpdate;
    }
    
    private JSONObject moveToHistory(final Long id, JSONObject json) {
        final JSONArray currentArray = (JSONArray)((json.get((Object)"current") != null) ? json.get((Object)"current") : new JSONArray());
        final Iterator iterator = currentArray.iterator();
        final JSONArray tempCurrentArray = new JSONArray();
        int indexToRemove = -1;
        while (iterator.hasNext()) {
            final JSONObject jsonObject = iterator.next();
            final Long tempId = (Long)jsonObject.get((Object)"id");
            ++indexToRemove;
            if (id == tempId) {
                tempCurrentArray.add((Object)jsonObject);
                break;
            }
        }
        json.put((Object)"current", (Object)tempCurrentArray);
        json = new PostgresCorruptionDetectionUtil().moveToHistory(json);
        currentArray.remove(indexToRemove);
        json.put((Object)"current", (Object)currentArray);
        return json;
    }
    
    private JSONObject moveToHistory(final JSONObject json) {
        final JSONArray current = (JSONArray)((json.get((Object)"current") != null) ? json.get((Object)"current") : new JSONArray());
        final JSONArray history = (JSONArray)((json.get((Object)"history") != null) ? json.get((Object)"history") : new JSONArray());
        try {
            for (int i = 0; i < current.size(); ++i) {
                boolean isUpdate = false;
                final JSONObject currentCorruption = (JSONObject)current.get(i);
                final long currentCorruptionID = (long)((currentCorruption.get((Object)"id") != null) ? currentCorruption.get((Object)"id") : 0L);
                final long currentCorruptionCount = (long)((currentCorruption.get((Object)"count") != null) ? currentCorruption.get((Object)"count") : 0L);
                final String currentTable = (String)currentCorruption.get((Object)"table");
                for (int j = 0; j < history.size(); ++j) {
                    final JSONObject historyCorruption = (JSONObject)history.get(j);
                    final String historyTable = (String)historyCorruption.get((Object)"table");
                    final long historyID = (long)((historyCorruption.get((Object)"id") != null) ? historyCorruption.get((Object)"id") : 0L);
                    JSONArray jsonArray = (JSONArray)historyCorruption.get((Object)"count");
                    if (jsonArray == null) {
                        jsonArray = new JSONArray();
                    }
                    if (currentTable != null && historyTable != null && historyTable.trim().equals(currentTable)) {
                        if (currentCorruptionID != 0L && historyID != 0L && historyID == currentCorruptionID) {
                            this.updateHistoryCount(currentCorruption, historyCorruption, jsonArray);
                            isUpdate = true;
                            break;
                        }
                    }
                    else if (currentTable == null && historyTable == null && currentCorruptionID != 0L && historyID != 0L && historyID == currentCorruptionID) {
                        this.updateHistoryCount(currentCorruption, historyCorruption, jsonArray);
                        isUpdate = true;
                        break;
                    }
                }
                if (!isUpdate) {
                    final JSONArray jsonArray2 = new JSONArray();
                    jsonArray2.add((Object)currentCorruptionCount);
                    currentCorruption.put((Object)"count", (Object)jsonArray2);
                    currentCorruption.remove((Object)"detected");
                    history.add((Object)currentCorruption);
                }
            }
            final JSONObject newJSON = new JSONObject();
            newJSON.put((Object)"current", (Object)new JSONArray());
            newJSON.put((Object)"history", (Object)history);
            return newJSON;
        }
        catch (final Exception ex) {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.SEVERE, "Exception thrown while trying to move current to history", ex);
            return json;
        }
    }
    
    private void updateHistoryCount(final JSONObject currentCorruption, final JSONObject historyCorruption, final JSONArray jsonArray) {
        final long currentCorruptionCount = (long)((currentCorruption.get((Object)"count") != null) ? currentCorruption.get((Object)"count") : 0L);
        final int ARRAY_LIMIT = 10;
        final int size = jsonArray.size();
        if (size > 9) {
            for (int k = 0; k < size - 9; ++k) {
                jsonArray.remove(0);
            }
        }
        jsonArray.add((Object)currentCorruptionCount);
        historyCorruption.put((Object)"count", (Object)jsonArray);
        historyCorruption.put((Object)"recent", (Object)this.getRecent(currentCorruption));
    }
    
    public JSONObject getMeTrackData(final int limit) {
        final JSONObject metrackJson = new JSONObject();
        PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Processing MeTrack Data for Corruption");
        try {
            final File file = new File(PostgresCorruptionConstant.CORRUPTION_FILE);
            if (file.exists()) {
                final JSONObject jsonObject = this.getJsonFromFile(file);
                final JSONArray currentArray = (JSONArray)jsonObject.get((Object)"current");
                final JSONArray historyArray = (JSONArray)jsonObject.get((Object)"history");
                final Map<Long, Set<JSONObject>> currentFreqMap = this.generateFreqMap(currentArray);
                final Map<Long, Set<JSONObject>> historyFreqMap = this.generateFreqMap(historyArray);
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Current Array: " + currentArray.toJSONString());
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "History Array: " + historyArray.toJSONString());
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "Current Freq Map : " + currentFreqMap.toString());
                PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "History Freq Map : " + historyFreqMap.toString());
                metrackJson.put((Object)"current", (Object)this.getTrimJSONArray(currentFreqMap, limit, false));
                metrackJson.put((Object)"history", (Object)this.getTrimJSONArray(historyFreqMap, limit, true));
                metrackJson.put((Object)"totalCurrent", (Object)currentArray.size());
                metrackJson.put((Object)"totalHistory", (Object)historyArray.size());
                if (jsonObject.containsKey((Object)"lastDump")) {
                    metrackJson.put((Object)"lastDump", jsonObject.get((Object)"lastDump"));
                }
                if (jsonObject.containsKey((Object)"lastBinary")) {
                    metrackJson.put((Object)"lastBinary", jsonObject.get((Object)"lastBinary"));
                }
            }
            metrackJson.put((Object)"isEnable", (Object)!PostgresCorruptionJson.getInstance().isDisableCorruptionDetection());
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.INFO, "MeTrack Data : " + metrackJson.toJSONString());
        }
        catch (final Exception ex) {
            PostgresCorruptionDetectionUtil.LOGGER.log(Level.SEVERE, "Exception thrown while getting MeTrack data from corruption", ex);
        }
        return metrackJson;
    }
    
    private Map<Long, Set<JSONObject>> generateFreqMap(final JSONArray jsonArray) {
        final Map<Long, Set<JSONObject>> map = new TreeMap<Long, Set<JSONObject>>(Collections.reverseOrder());
        if (jsonArray == null) {
            return map;
        }
        for (final JSONObject json : jsonArray) {
            final long key = this.getCount(json.get((Object)"count"));
            Set<JSONObject> valueSet = map.get(key);
            if (valueSet == null) {
                valueSet = this.createRecentJsonSet();
            }
            valueSet.add(json);
            map.put(key, valueSet);
        }
        return map;
    }
    
    private Set<JSONObject> createRecentJsonSet() {
        final Set<JSONObject> recentJsonObj = new TreeSet<JSONObject>(new Comparator<JSONObject>() {
            @Override
            public int compare(final JSONObject json1, final JSONObject json2) {
                return Long.compare(PostgresCorruptionDetectionUtil.this.getRecent(json2), PostgresCorruptionDetectionUtil.this.getRecent(json1));
            }
        });
        return recentJsonObj;
    }
    
    private long getRecent(final JSONObject json) {
        final Object object = json.get((Object)"recent");
        return (object instanceof Number) ? ((Number)object).longValue() : Long.parseLong((String)object);
    }
    
    private JSONArray getTrimJSONArray(final Map<Long, Set<JSONObject>> map, final int limit, final boolean removeRecent) {
        final JSONArray jsonArray = new JSONArray();
        int size = 1;
        for (final Map.Entry<Long, Set<JSONObject>> entry : map.entrySet()) {
            final Set<JSONObject> set = entry.getValue();
            for (final JSONObject json : set) {
                if (size > limit) {
                    return jsonArray;
                }
                if (removeRecent) {
                    json.remove((Object)"recent");
                }
                jsonArray.add((Object)json);
                ++size;
            }
        }
        return jsonArray;
    }
    
    private long getCount(final Object object) {
        long count = 0L;
        if (object instanceof JSONArray) {
            final JSONArray array = (JSONArray)object;
            for (final Object obj : array) {
                final Long number = this.getCount(obj);
                count += number;
            }
            return count;
        }
        if (object instanceof Number) {
            return ((Number)object).longValue();
        }
        return Long.parseLong((String)object);
    }
    
    static {
        PostgresCorruptionDetectionUtil.pgsqlCorrDetection = null;
        LOGGER = Logger.getLogger(PostgresCorruptionDetectionUtil.class.getName());
    }
}
