package com.me.devicemanagement.framework.webclient.export;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ExportSettingsUtil
{
    public static final String EXPORT_REDACT_TYPE = "fw_export_redact_type";
    public static final String SCHEDULE_EXPORT_REDACT_TYPE = "fw_schedule_export_redact_type";
    public static final String EXPORT_PASSWORD_TYPE = "export_password_type";
    public static final String DEFAULT_EXPORT_PASSWORD = "default_export_password";
    public static final String SETTING_ENABLE = "enable";
    public static final String SETTING_DEFAULT = "default";
    public static final String SELECTED_TYPES = "selected_types";
    private static final String EXPORT_CONFIG_CACHE_NAME = "EXPORT_CONFIG_CACHE_NAME";
    public static Logger log;
    
    public static JSONObject getExportConfiguration() {
        JSONObject cacheObject = new JSONObject();
        final String cacheString = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("EXPORT_CONFIG_CACHE_NAME", 2);
        if (cacheString != null) {
            if (cacheString.length() >= 1) {
                try {
                    cacheObject = new JSONObject(cacheString);
                    ExportSettingsUtil.log.log(Level.INFO, "Loaded export settings" + cacheObject);
                }
                catch (final JSONException ex) {
                    ExportSettingsUtil.log.log(Level.INFO, "Exception while loading export settings", (Throwable)ex);
                }
                return cacheObject;
            }
        }
        try {
            ExportSettingsUtil.log.log(Level.INFO, "Going to Load export settings configurations");
            cacheObject = loadExportSettings();
            ExportSettingsUtil.log.log(Level.INFO, "Loaded export settings configurations" + cacheObject);
            ApiFactoryProvider.getCacheAccessAPI().putCache("EXPORT_CONFIG_CACHE_NAME", cacheObject.toString(), 2);
        }
        catch (final DataAccessException dataAccExcep) {
            ExportSettingsUtil.log.log(Level.INFO, " Exception while loading export settings", (Throwable)dataAccExcep);
        }
        catch (final JSONException jsonExcep) {
            ExportSettingsUtil.log.log(Level.INFO, "Exception while loading export settings", (Throwable)jsonExcep);
        }
        return cacheObject;
    }
    
    public static JSONObject loadExportSettings() throws DataAccessException, JSONException {
        final JSONObject settings = new JSONObject();
        final DataObject dataObject = SyMUtil.getPersistence().get("ExportSettings", (Criteria)null);
        final Iterator rows = dataObject.getRows("ExportSettings");
        while (rows.hasNext()) {
            final Row row = rows.next();
            final JSONObject settings_specifications = new JSONObject();
            settings_specifications.put("enable", row.get("REDACT_TYPE"));
            settings_specifications.put("default", row.get("DEFAULT_REDACT_TYPE"));
            String selectTypes = (String)row.get("SELECTED_REDACT_TYPES");
            if (selectTypes == null) {
                selectTypes = "";
            }
            settings_specifications.put("selected_types", (Object)selectTypes);
            settings_specifications.put("export_password_type", row.get("EXPORT_PASSWORD_TYPE"));
            String defaultExportPassword = (String)row.get("DEFAULT_EXPORT_PASSWORD");
            if (defaultExportPassword == null) {
                defaultExportPassword = "";
            }
            settings_specifications.put("default_export_password", (Object)defaultExportPassword);
            settings.put(row.get("REDACT_SETTING_NAME").toString(), (Object)settings_specifications);
        }
        return settings;
    }
    
    public static String getColumnName(final String jsonKey) {
        switch (jsonKey) {
            case "default": {
                return "DEFAULT_REDACT_TYPE";
            }
            case "selected_types": {
                return "SELECTED_REDACT_TYPES";
            }
            case "enable": {
                return "REDACT_TYPE";
            }
            default: {
                return null;
            }
        }
    }
    
    public static String getColumnType(final String columnName) {
        switch (columnName) {
            case "REDACT_TYPE":
            case "DEFAULT_REDACT_TYPE": {
                return "int";
            }
            case "SELECTED_REDACT_TYPES": {
                return "char";
            }
            default: {
                return null;
            }
        }
    }
    
    public static int addExportEnableSettings(final String settingsName, final String propertyName, final String value) throws DataAccessException {
        final Column column = new Column("ExportSettings", "REDACT_SETTING_NAME");
        final Criteria criteria = new Criteria(column, (Object)settingsName, 0);
        final DataObject dataObject = SyMUtil.getPersistence().get("ExportSettings", criteria);
        final String columnName = getColumnName(propertyName);
        if (columnName != null && dataObject != null) {
            final String columnType = getColumnType(columnName);
            final Row row = dataObject.getRow("ExportSettings", criteria);
            if (columnType != null && columnType.equalsIgnoreCase("int")) {
                row.set(columnName, (Object)Integer.parseInt(value));
            }
            else {
                row.set(columnName, (Object)value);
            }
            dataObject.updateRow(row);
            SyMUtil.getPersistence().update(dataObject);
        }
        try {
            final JSONObject cacheObject = loadExportSettings();
            ApiFactoryProvider.getCacheAccessAPI().putCache("EXPORT_CONFIG_CACHE_NAME", cacheObject.toString(), 2);
        }
        catch (final JSONException ex) {
            Logger.getLogger(ExportSettingsUtil.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return 3001;
    }
    
    private static DataObject getConsentActionLoggerDetailDO(final Criteria criteria) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("UserLevelRedactType"));
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        try {
            return SyMUtil.getPersistence().get(selectQuery);
        }
        catch (final DataAccessException dae) {
            ExportSettingsUtil.log.log(Level.INFO, "Exception while retrieving data from ConsentStatusDetail", (Throwable)dae);
            return null;
        }
    }
    
    public static String getUserSpecifiedRedactType(final Long user_id) {
        final Criteria criteria = new Criteria(new Column("UserLevelRedactType", "USER_ID"), (Object)user_id, 0);
        final DataObject dataObject = getConsentActionLoggerDetailDO(criteria);
        try {
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("UserLevelRedactType");
                return row.get("REDACT_LEVEL").toString();
            }
        }
        catch (final DataAccessException dae) {
            ExportSettingsUtil.log.log(Level.INFO, "Exception while getting redact type of user", (Throwable)dae);
        }
        return null;
    }
    
    public static int setRedactTypeForUser(final int redact_type, final Long user_id) throws DataAccessException {
        try {
            final Criteria criteria = new Criteria(new Column("UserLevelRedactType", "USER_ID"), (Object)user_id, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("UserLevelRedactType", criteria);
            if (dataObject.isEmpty()) {
                final Row row = new Row("UserLevelRedactType");
                row.set("USER_ID", (Object)user_id);
                row.set("REDACT_LEVEL", (Object)redact_type);
                dataObject.addRow(row);
                SyMUtil.getPersistence().add(dataObject);
            }
            else {
                final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("UserLevelRedactType");
                updateQuery.setUpdateColumn("REDACT_LEVEL", (Object)redact_type);
                SyMUtil.getPersistence().update(updateQuery);
            }
        }
        catch (final DataAccessException dae) {
            ExportSettingsUtil.log.log(Level.INFO, "Exception while setting redact type of user", (Throwable)dae);
            throw dae;
        }
        return 1000;
    }
    
    public static int setRedactTypeOfExportSettings(final Integer redactType) throws Exception {
        return setExportEnableSettings("fw_export_redact_type", "enable", redactType.toString());
    }
    
    public static int getRedactTypeOfExportSettings() throws Exception {
        final JSONObject exportRedactValues = (JSONObject)getExportConfiguration().get("fw_export_redact_type");
        return Integer.parseInt(exportRedactValues.get("enable").toString());
    }
    
    public static int getDefaultRedactTypeOfExportSettings() throws Exception {
        final JSONObject exportRedactValues = (JSONObject)getExportConfiguration().get("fw_export_redact_type");
        return Integer.parseInt(exportRedactValues.get("default").toString());
    }
    
    public static Boolean isAdminConfiguredExportSettings() throws Exception {
        if (getRedactTypeOfExportSettings() == 0) {
            return false;
        }
        return true;
    }
    
    public static Integer getExportPasswordType(final String exportSettings) throws Exception {
        final JSONObject config = (JSONObject)getExportConfiguration().get(exportSettings);
        return Integer.parseInt(config.get("export_password_type").toString());
    }
    
    public static Integer getExportPasswordType() throws Exception {
        final Iterator settings = getExportConfiguration().keys();
        while (settings.hasNext()) {
            final String key = settings.next().toString();
            final Integer exportRedactValues = getExportPasswordType(key);
            if (exportRedactValues == 1) {
                return 1;
            }
        }
        return 0;
    }
    
    public static int setExportPasswordSettings(final Integer redactType) throws Exception {
        return setExportEnableSettings("export_password_type", "enable", redactType.toString());
    }
    
    public static int setScheduleReportRedactType(final Integer redactType) throws Exception {
        return setExportEnableSettings("fw_schedule_export_redact_type", "enable", redactType.toString());
    }
    
    public static Integer getScheduleExportRedactType() throws Exception {
        final JSONObject exportRedactValues = (JSONObject)getExportConfiguration().get("fw_schedule_export_redact_type");
        return Integer.parseInt(exportRedactValues.get("enable").toString());
    }
    
    public static int setExportEnableSettings(final String settings_name, final String property_name, final String value) throws Exception {
        if (value != null && value.trim().length() >= 1) {
            final JSONObject enable = getExportConfiguration().getJSONObject(settings_name);
            enable.put(property_name, (Object)value);
            return addExportEnableSettings(settings_name, property_name, value);
        }
        return 3001;
    }
    
    private static String getAdminSelectedRedactTypesForUser(final String property) throws Exception {
        final JSONObject exportRedactValues = (JSONObject)getExportConfiguration().get(property);
        final String types = exportRedactValues.get("selected_types").toString();
        return types;
    }
    
    public static String getAdminSelectedExportRedactTypeForUser() throws Exception {
        return getAdminSelectedRedactTypesForUser("fw_export_redact_type");
    }
    
    public static String getAdminSelectedScheduleReportRedactTypeForUser() throws Exception {
        return getAdminSelectedRedactTypesForUser("fw_schedule_export_redact_type");
    }
    
    public static int setAdminSelectedExportRedactTypeForUser(final String types) throws Exception {
        if (types != null) {
            return setExportEnableSettings("fw_export_redact_type", "selected_types", types);
        }
        return 3001;
    }
    
    public static int setAdminSelectedScheduleRedactTypeForUser(final String types) throws Exception {
        if (types != null) {
            return setExportEnableSettings("fw_schedule_export_redact_type", "selected_types", types);
        }
        return 3001;
    }
    
    public static Boolean isPasswordMandatory() throws Exception {
        if (getExportPasswordType() == 1) {
            return true;
        }
        return false;
    }
    
    static {
        ExportSettingsUtil.log = Logger.getLogger(ExportSettingsUtil.class.getName());
    }
}
