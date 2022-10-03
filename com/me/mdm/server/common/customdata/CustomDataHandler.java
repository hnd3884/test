package com.me.mdm.server.common.customdata;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.dd.plist.NSDictionary;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.Collection;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.logging.Logger;

public class CustomDataHandler
{
    private Logger logger;
    public static final String CUSTOM_DATA = "CUSTOM_DATA";
    public static final int CUSTOM_CONFIG_STRING = 1;
    public static final int CUSTOM_CONFIG_INTEGER = 2;
    public static final int CUSTOM_CONFIG_BOOLEAN = 3;
    public static final int CUSTOM_CONFIG_LONG = 4;
    private static final String KEY_NAME = "CUSTOM_KEY";
    private static final String VALUE_NAME = "CUSTOM_VALUE";
    
    public CustomDataHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public List<Long> addCustomData(final List<JSONObject> customArray, final Long configDataID) {
        return this.addCustomData(new JSONArray((Collection)customArray), configDataID);
    }
    
    public List<Row> addCustomData(final List<JSONObject> customArray, final Object configDataID, final DataObject dataObject) {
        return this.addCustomData(new JSONArray((Collection)customArray), configDataID, dataObject);
    }
    
    public List<Long> addCustomData(final JSONArray customArray, final Long configDataID) {
        final List<Long> customDataIDs = new ArrayList<Long>();
        try {
            final DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
            final List<Row> rows = this.addCustomData(customArray, configDataID, dataObject);
            MDMUtil.getPersistence().add(dataObject);
            for (final Row row : rows) {
                customDataIDs.add((Long)row.get("CUSTOM_DATA_ID"));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Failed to add custom data", e);
        }
        return customDataIDs;
    }
    
    public List<Row> addCustomData(final JSONArray customArray, final Object configDataID, final DataObject dataObject) {
        final List<Row> rows = new ArrayList<Row>();
        try {
            MDMDBUtil.deleteRows(dataObject, "MDMConfigCustomData", new Object[][] { { "CONFIG_DATA_ITEM_ID", configDataID } });
            for (int i = 0; i < customArray.length(); ++i) {
                final JSONObject jsonObject = customArray.getJSONObject(i);
                final String keyName = jsonObject.getString("CUSTOM_KEY");
                if (!MDMStringUtils.isEmpty(keyName)) {
                    final Row row = MDMDBUtil.updateRow(dataObject, "MDMConfigCustomData", new Object[][] { { "KEY", jsonObject.getString("CUSTOM_KEY") }, { "VALUE", jsonObject.getString("CUSTOM_VALUE") }, { "CONFIG_DATA_ITEM_ID", configDataID }, { "DATA_TYPE", jsonObject.optInt("DATA_TYPE", 1) } });
                    rows.add(row);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Failed to add custom data", e);
        }
        return rows;
    }
    
    public List<JSONObject> getCustomData(final Long configDataID, final DataObject dataObject) {
        List<JSONObject> customData = new ArrayList<JSONObject>();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("MDMConfigCustomData", "CONFIG_DATA_ITEM_ID"), (Object)configDataID, 0);
            customData = this.formCustomDataArray(dataObject.getRows("MDMConfigCustomData", criteria));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Failed to fetch custom data", e);
        }
        return customData;
    }
    
    public List<JSONObject> getCustomData(final Long configDataItem) {
        List<JSONObject> customData = new ArrayList<JSONObject>();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("MDMConfigCustomData", "CONFIG_DATA_ITEM_ID"), (Object)configDataItem, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMConfigCustomData"));
            query.addSelectColumn(Column.getColumn("MDMConfigCustomData", "*"));
            query.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            customData = this.formCustomDataArray(dataObject.getRows("MDMConfigCustomData"));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Failed to fetch custom data", e);
        }
        return customData;
    }
    
    private List<JSONObject> formCustomDataArray(final Iterator<Row> iterator) throws Exception {
        final List<JSONObject> customData = new ArrayList<JSONObject>();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("CUSTOM_DATA_ID", row.get("CUSTOM_DATA_ID"));
            jsonObject.put("CUSTOM_KEY", row.get("KEY"));
            jsonObject.put("CUSTOM_VALUE", row.get("VALUE"));
            jsonObject.put("DATA_TYPE", row.get("DATA_TYPE"));
            customData.add(jsonObject);
        }
        return customData;
    }
    
    public NSDictionary getCustomDataAsNSDict(final Long configDataID, final DataObject dataObject) {
        final List<JSONObject> customDataFields = this.getCustomData(configDataID, dataObject);
        return this.formNSDictionary(customDataFields);
    }
    
    public NSDictionary getCustomDataAsNSDict(final Long configDataID) {
        final List<JSONObject> customDataFields = this.getCustomData(configDataID);
        return this.formNSDictionary(customDataFields);
    }
    
    private NSDictionary formNSDictionary(final List<JSONObject> customDataFields) {
        final NSDictionary customDict = new NSDictionary();
        for (final JSONObject jsonObject : customDataFields) {
            final String key = jsonObject.getString("CUSTOM_KEY");
            final int dataType = jsonObject.getInt("DATA_TYPE");
            if (MDMStringUtils.isEmpty(key)) {
                continue;
            }
            switch (dataType) {
                case 1: {
                    customDict.put(key, (Object)jsonObject.getString("CUSTOM_VALUE"));
                    continue;
                }
                case 2: {
                    customDict.put(key, (Object)jsonObject.getInt("CUSTOM_VALUE"));
                    continue;
                }
                case 3: {
                    customDict.put(key, (Object)jsonObject.getBoolean("CUSTOM_VALUE"));
                    continue;
                }
                case 4: {
                    customDict.put(key, (Object)jsonObject.getLong("CUSTOM_VALUE"));
                    continue;
                }
                default: {
                    customDict.put(key, jsonObject.get("CUSTOM_VALUE"));
                    continue;
                }
            }
        }
        return customDict;
    }
    
    public void deleteCustomData(final DataObject dataObject, final Object configDataID) {
        try {
            MDMDBUtil.deleteRows(dataObject, "MDMConfigCustomData", new Object[][] { { "CONFIG_DATA_ITEM_ID", configDataID } });
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Failed to remove custom data", e);
        }
    }
    
    public void deleteCustomData(final Long configDataID) {
        try {
            final DeleteQuery query = (DeleteQuery)new DeleteQueryImpl("MDMConfigCustomData");
            final Criteria criteria = new Criteria(Column.getColumn("MDMConfigCustomData", "CONFIG_DATA_ITEM_ID"), (Object)configDataID, 0);
            query.setCriteria(criteria);
            MDMUtil.getPersistence().delete(query);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Failed to remove custom data", e);
        }
    }
    
    public static Boolean hasCustomConfigData(final DataObject dataObject) throws DataAccessException {
        return dataObject.containsTable("MDMConfigCustomData");
    }
}
