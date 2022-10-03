package com.me.devicemanagement.onpremise.server.troubleshooter.postgres;

import java.util.Iterator;
import org.json.simple.JSONArray;
import java.io.Reader;
import java.io.FileReader;
import org.json.simple.parser.JSONParser;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.logging.Level;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import java.util.logging.Logger;

public class PostgresCorruptionJson
{
    private static PostgresCorruptionJson instance;
    private static final Logger LOGGER;
    private JSONObject json;
    private boolean disableCorruptionDetection;
    private boolean createLockFile;
    private boolean notifyCustomerthrMail;
    private Map<Long, JSONObject> pgSqlExceptionMap;
    private long lastModifiedTime;
    
    public static synchronized PostgresCorruptionJson getInstance() {
        if (PostgresCorruptionJson.instance == null) {
            PostgresCorruptionJson.instance = new PostgresCorruptionJson();
        }
        return PostgresCorruptionJson.instance;
    }
    
    private PostgresCorruptionJson() {
        this.pgSqlExceptionMap = new LinkedHashMap<Long, JSONObject>();
        try {
            this.json = this.getExceptionSorterJson();
        }
        catch (final Exception e) {
            this.disableCorruptionDetection = true;
            PostgresCorruptionJson.LOGGER.log(Level.WARNING, "Failed Load Postgres Corruption Configure File : ", e);
        }
    }
    
    private JSONObject getExceptionSorterJson() throws Exception {
        final File exceptionSorterFile = new File(PostgresCorruptionConstant.EXCEPTION_SORTER_FILE);
        if (!exceptionSorterFile.exists()) {
            throw new FileNotFoundException("File '" + exceptionSorterFile + "' does not exist");
        }
        if (!exceptionSorterFile.canRead()) {
            throw new IOException("File '" + exceptionSorterFile + "' cannot be read");
        }
        final JSONParser parser = new JSONParser();
        this.json = (JSONObject)parser.parse((Reader)new FileReader(exceptionSorterFile));
        this.lastModifiedTime = exceptionSorterFile.lastModified();
        if (!this.json.isEmpty()) {
            this.disableCorruptionDetection = Boolean.parseBoolean((String)this.json.get((Object)"DisableCorruptionDetection"));
            this.createLockFile = Boolean.parseBoolean((String)this.json.get((Object)"CreateLockFile"));
            this.notifyCustomerthrMail = Boolean.parseBoolean((String)this.json.get((Object)"notifyCustomerthrMail"));
            final JSONArray exceptionsArray = (JSONArray)this.json.get((Object)"Exceptions");
            for (final Object anExceptionsArray : exceptionsArray) {
                final JSONObject jsonObject = (JSONObject)anExceptionsArray;
                this.pgSqlExceptionMap.put(((Number)jsonObject.get((Object)"id")).longValue(), jsonObject);
            }
        }
        PostgresCorruptionJson.LOGGER.log(Level.INFO, "Exception Template Json : " + this.json.toJSONString());
        return this.json;
    }
    
    public JSONObject getJson() {
        return this.json;
    }
    
    public boolean isDisableCorruptionDetection() {
        return this.disableCorruptionDetection;
    }
    
    public boolean isCreateLockFile() {
        return this.createLockFile;
    }
    
    public boolean isNotifyCustomerthrMail() {
        return this.notifyCustomerthrMail;
    }
    
    public Map<Long, JSONObject> getPgSqlExceptionMap() {
        return this.pgSqlExceptionMap;
    }
    
    public long getLastModifiedTime() {
        return this.lastModifiedTime;
    }
    
    static {
        PostgresCorruptionJson.instance = null;
        LOGGER = Logger.getLogger(PostgresCorruptionJson.class.getName());
    }
}
