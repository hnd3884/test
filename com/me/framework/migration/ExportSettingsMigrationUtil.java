package com.me.framework.migration;

import java.util.HashMap;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.utils.JsonUtils;
import java.util.logging.Level;
import java.io.File;
import java.util.logging.Logger;

public class ExportSettingsMigrationUtil
{
    private static Logger logger;
    
    public static void populateDbForExistingJsonFile(final String filePath) throws JSONException, DataAccessException {
        final File file = new File(filePath);
        if (!file.isFile()) {
            ExportSettingsMigrationUtil.logger.log(Level.INFO, filePath + " file does not exist");
            return;
        }
        final JSONObject exportSettingsJsonObj = JsonUtils.loadJsonFile(file);
        DataObject dataObject = DataAccess.get("ExportSettings", (Criteria)null);
        boolean isDataObjectEmpty = false;
        if (dataObject.isEmpty()) {
            isDataObjectEmpty = true;
        }
        final Iterator<String> itr = exportSettingsJsonObj.keys();
        String redactType = null;
        while (itr.hasNext()) {
            try {
                final String redactTypeFromFile;
                redactType = (redactTypeFromFile = itr.next());
                if (redactType.equals("export_redact_type")) {
                    redactType = "fw_export_redact_type";
                }
                else if (redactType.equals("export_password_type")) {
                    redactType = "export_password_type";
                }
                else if (redactType.equals("schedule_export_redact_type")) {
                    redactType = "fw_schedule_export_redact_type";
                }
                final Column col = Column.getColumn("ExportSettings", "REDACT_SETTING_NAME");
                final Row row = setRow(redactType, (JSONObject)exportSettingsJsonObj.get(redactTypeFromFile));
                if (!isDataObjectEmpty) {
                    final Criteria c = new Criteria(col, (Object)redactType, 0);
                    final Row tempRow = dataObject.getRow("ExportSettings", c);
                    if (tempRow != null && tempRow.get("REDACT_TYPE").toString().equals(row.get("REDACT_TYPE").toString()) && tempRow.get("DEFAULT_REDACT_TYPE").toString().equals(row.get("DEFAULT_REDACT_TYPE").toString())) {
                        ExportSettingsMigrationUtil.logger.log(Level.INFO, redactType, " Type already exists in db !");
                        continue;
                    }
                    if (tempRow != null) {
                        dataObject.deleteRow(tempRow);
                        dataObject = DataAccess.update(dataObject);
                    }
                }
                dataObject.addRow(row);
            }
            catch (final Exception ex) {
                ExportSettingsMigrationUtil.logger.log(Level.INFO, redactType, "value not found in file");
            }
        }
        if (isDataObjectEmpty) {
            DataAccess.add(dataObject);
        }
        else {
            DataAccess.update(dataObject);
        }
    }
    
    private static Row setRow(final String settingName, final JSONObject jsonObject) throws JSONException {
        final Row row = new Row("ExportSettings");
        row.set("REDACT_SETTING_NAME", (Object)settingName);
        final Iterator<String> itr = jsonObject.keys();
        String key = null;
        final HashMap<String, String> keyValueMap = getDbKeyValueMap();
        while (itr.hasNext()) {
            key = itr.next();
            row.set((String)keyValueMap.get(key), jsonObject.get(key));
        }
        return row;
    }
    
    private static HashMap getDbKeyValueMap() {
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("default", "DEFAULT_REDACT_TYPE");
        hashMap.put("enable", "REDACT_TYPE");
        hashMap.put("selected_types", "SELECTED_REDACT_TYPES");
        return hashMap;
    }
    
    static {
        ExportSettingsMigrationUtil.logger = Logger.getLogger(ExportSettingsMigrationUtil.class.getName());
    }
}
