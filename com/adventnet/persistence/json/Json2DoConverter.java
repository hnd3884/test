package com.adventnet.persistence.json;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Set;
import java.util.Hashtable;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.persistence.xml.DynamicValueHandler;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.xml.SQLDynamicValueHandler;
import com.adventnet.persistence.xml.DVHandlerTemplate;
import com.adventnet.persistence.xml.XmlRowTransformer;
import java.util.Locale;
import com.adventnet.persistence.xml.XmlDoUtil;
import com.adventnet.persistence.xml.DynamicValueHandlerRepositry;
import java.sql.Timestamp;
import java.sql.Date;
import java.text.SimpleDateFormat;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONArray;
import java.io.BufferedReader;
import com.adventnet.persistence.xml.DynamicValueHandlingException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.util.Map;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class Json2DoConverter
{
    private static final String CLASS_NAME;
    private static final Logger LOGGER;
    private DataObject dataObject;
    private JSONObject jsonObject;
    private Map<String, Object> uvhMap;
    private MultiMap<String, ChildRow> uvhChildRowsMap;
    private boolean handleEnDecryption;
    
    public static DataObject transform(final String jsonFilepath, final boolean isTransformedJsonObject) throws IOException, JSONException, DataAccessException, DynamicValueHandlingException {
        if (jsonFilepath == null) {
            throw new IllegalArgumentException("jsonFilepath cannot be null");
        }
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(jsonFilepath);
            return transform(fileReader, isTransformedJsonObject);
        }
        finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
    }
    
    public static DataObject transform(final Reader jsonInputReader, final boolean isTransformedJsonObject) throws IOException, JSONException, DataAccessException, DynamicValueHandlingException {
        if (jsonInputReader == null) {
            throw new IllegalArgumentException("jsonInputReader cannot be null");
        }
        final char[] buff = new char[4096];
        final StringBuilder jsonInput = new StringBuilder();
        Reader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(jsonInputReader);
            int len;
            while ((len = bufferedReader.read(buff)) != -1) {
                jsonInput.append(buff, 0, len);
            }
        }
        finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        if (isTransformedJsonObject) {
            return getDataObjectFromTransformedJson(jsonInput.toString());
        }
        return getDataObjectFromSerializedJson(jsonInput.toString());
    }
    
    public static DataObject transform(final String jsonStr, final boolean isTransformedJsonObject, final boolean handleEnDecryption) throws IOException, JSONException, DataAccessException, DynamicValueHandlingException {
        final Json2DoConverter json2doConv = new Json2DoConverter(jsonStr, handleEnDecryption);
        if (isTransformedJsonObject) {
            json2doConv.init(jsonStr);
            return json2doConv.dataObject;
        }
        final Iterator<String> keys = json2doConv.jsonObject.keys();
        while (keys.hasNext()) {
            final String tableName = keys.next();
            if (json2doConv.jsonObject.get(tableName) instanceof JSONArray) {
                json2doConv.addRowsFromSerilizedJsonArray(tableName, (JSONArray)json2doConv.jsonObject.get(tableName));
            }
        }
        return json2doConv.dataObject;
    }
    
    public static DataObject transform(final JSONObject jsonobj, final boolean isTransformedJsonObject) throws IOException, JSONException, DataAccessException, DynamicValueHandlingException {
        if (isTransformedJsonObject) {
            return getDataObjectFromTransformedJson(jsonobj.toString());
        }
        return getDataObjectFromSerializedJson(jsonobj.toString());
    }
    
    public static DataObject transformJsonString(final String jsonString, final boolean isTransformedJsonObject) throws IOException, JSONException, DataAccessException, DynamicValueHandlingException {
        if (isTransformedJsonObject) {
            return getDataObjectFromTransformedJson(jsonString);
        }
        return getDataObjectFromSerializedJson(jsonString);
    }
    
    private static DataObject getDataObjectFromSerializedJson(final String jsonStr) throws JSONException, DataAccessException {
        final Json2DoConverter json2doConv = new Json2DoConverter(jsonStr);
        final Iterator<String> keys = json2doConv.jsonObject.keys();
        while (keys.hasNext()) {
            final String tableName = keys.next();
            if (json2doConv.jsonObject.get(tableName) instanceof JSONArray) {
                json2doConv.addRowsFromSerilizedJsonArray(tableName, (JSONArray)json2doConv.jsonObject.get(tableName));
            }
        }
        return json2doConv.dataObject;
    }
    
    private static DataObject getDataObjectFromTransformedJson(final String jsonStr) throws JSONException, DataAccessException, DynamicValueHandlingException {
        final Json2DoConverter json2doConv = new Json2DoConverter(jsonStr);
        json2doConv.init(jsonStr);
        return json2doConv.dataObject;
    }
    
    private Json2DoConverter(final String js) throws DataAccessException, JSONException {
        this(js, false);
    }
    
    private Json2DoConverter(final String js, final boolean handleEnDecryption) throws DataAccessException, JSONException {
        this.uvhMap = new HashMap<String, Object>();
        this.uvhChildRowsMap = new MultiMap<String, ChildRow>();
        this.dataObject = DataAccess.constructDataObject();
        this.jsonObject = new JSONObject(js);
        this.handleEnDecryption = handleEnDecryption;
    }
    
    private void init(final String jsonStr) throws JSONException, DataAccessException, DynamicValueHandlingException {
        final JSONObject1 jsonObj1 = new JSONObject1(new JSONObject(jsonStr));
        final Iterator<String> keys = jsonObj1.get().keys();
        while (keys.hasNext()) {
            final String tableName = keys.next();
            this.addRowsFromTransformedJsonArray(tableName, jsonObj1.get().getJSONArray(tableName), null);
        }
        this.doUVHhandling();
    }
    
    private void doUVHhandling() throws JSONException {
        for (final String uvhPattern : ((MultiMap<Object, Object>)this.uvhChildRowsMap).keySet()) {
            final List<ChildRow> rows = (List<ChildRow>)((MultiMap<Object, Object>)this.uvhChildRowsMap).getList(uvhPattern);
            final Object uvhValue = this.uvhMap.get(uvhPattern);
            if (uvhValue == null) {
                throw JsonUtil.createJsonExpception("No parent row available with id " + uvhPattern + " for these row(s) " + rows);
            }
            for (final ChildRow childRow : rows) {
                childRow.row.set(childRow.columnName, uvhValue);
                Json2DoConverter.LOGGER.log(Level.FINEST, "chrow; {0}", childRow.row);
            }
        }
    }
    
    private void addRowsFromSerilizedJsonArray(final String tableNameinLowerCase, final JSONArray rowsArr) throws JSONException, DataAccessException {
        try {
            final String tableName = MetaDataUtil.getDefinedTableName(tableNameinLowerCase);
            JsonUtil.assertTableName(tableName);
            for (int i = 0; i < rowsArr.length(); ++i) {
                final JSONObject rowJson = rowsArr.getJSONObject(i);
                final Row row = this.getRowFromSerializedJsonObject(tableName, rowsArr.getJSONObject(i));
                this.dataObject.addRow(row);
            }
        }
        catch (final MetaDataException ex) {
            ex.printStackTrace();
        }
    }
    
    private void addRowsFromTransformedJsonArray(final String displayName, final JSONArray rowsArr, final Row parentRow) throws JSONException, DataAccessException, DynamicValueHandlingException {
        final String tableName = this.getTableName(displayName);
        JsonUtil.assertTableName(tableName);
        for (int i = 0; i < rowsArr.length(); ++i) {
            final JSONObject1 rowJson = new JSONObject1(rowsArr.getJSONObject(i));
            final Row row = this.getRowFromTransformedJsonObject(tableName, rowsArr.getJSONObject(i), parentRow);
            this.dataObject.addRow(row);
            for (final String childTableName : rowJson.childTables()) {
                this.addRowsFromTransformedJsonArray(childTableName, rowJson.get().getJSONArray(childTableName), row);
            }
        }
    }
    
    private Row getRowFromSerializedJsonObject(final String tableName, final JSONObject rowJson) throws JSONException, DataAccessException {
        final Row row = new Row(tableName);
        final Iterator jsoncols = rowJson.keys();
        final List<ColumnDefinition> columnDefs = this.getColumnDefs(tableName);
        for (final ColumnDefinition columnDefn : columnDefs) {
            final String columnName = columnDefn.getColumnName();
            final String key = columnName.toLowerCase();
            final String datatype = columnDefn.getDataType();
            Object colValue = null;
            if (rowJson.has(key)) {
                Object value = rowJson.get(key);
                try {
                    final String dataType = columnDefn.getDataType();
                    if (this.handleEnDecryption && columnDefn.isEncryptedColumn() && !DataTypeUtil.isUDT(dataType)) {
                        value = JsonUtil.decryptValue(String.valueOf(value));
                    }
                    colValue = MetaDataUtil.convert(value.toString(), dataType);
                    row.set(columnName, colValue);
                }
                catch (final MetaDataException ex) {
                    try {
                        if (datatype.equals("DATE")) {
                            final SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy");
                            final java.util.Date parsedDate = sdf.parse(value.toString());
                            final Date sqlDate = (Date)(colValue = new Date(parsedDate.getTime()));
                            row.set(columnName, colValue);
                        }
                        else if (datatype.equals("DATETIME") || datatype.equals("TIMESTAMP")) {
                            final SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy");
                            final java.util.Date parsedDate = sdf.parse(value.toString());
                            final Timestamp timestamp = (Timestamp)(colValue = new Timestamp(parsedDate.getTime()));
                            row.set(columnName, colValue);
                        }
                        else {
                            row.set(columnName, value);
                            Json2DoConverter.LOGGER.log(Level.FINE, "Illegal column value {0} for datatype " + columnDefn.getDataType(), value);
                        }
                    }
                    catch (final Exception x) {
                        row.set(columnName, value);
                    }
                }
                catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    Json2DoConverter.LOGGER.log(Level.FINE, "Exception when decrypting encrypted value.");
                }
            }
        }
        return row;
    }
    
    private Row getRowFromTransformedJsonObject(final String tableName, final JSONObject rowJson, final Row parentRow) throws JSONException, DynamicValueHandlingException, DataAccessException {
        final HashMap colVsValue = new HashMap();
        final Iterator jsoncols = rowJson.keys();
        while (jsoncols.hasNext()) {
            final String colName = jsoncols.next();
            final Object colvalue = rowJson.get(colName);
            colVsValue.put(colName, colvalue);
        }
        final XmlRowTransformer rowProcessInstance = DynamicValueHandlerRepositry.getRowTransformer(tableName);
        if (rowProcessInstance != null) {
            rowProcessInstance.setColumnNames(tableName, colVsValue);
        }
        Json2DoConverter.LOGGER.log(Level.FINEST, "actual colum name vs column value :{0}", colVsValue);
        final Row row = new Row(tableName);
        final List<ColumnDefinition> columnDefs = this.getColumnDefs(tableName);
        for (final ColumnDefinition columnDefn : columnDefs) {
            final String columnName = columnDefn.getColumnName();
            Object value = null;
            if (colVsValue.containsKey(columnName.toLowerCase())) {
                value = colVsValue.get(columnName.toLowerCase());
            }
            if (XmlDoUtil.checkIfDynamicValueGeneratorExists(tableName + ":" + columnName) && value != null) {
                final Object dyvalue = this.getDynamicValue(tableName, columnName, value.toString());
                if (dyvalue != null) {
                    value = dyvalue;
                }
            }
            if (value == null) {
                if (parentRow != null && columnDefn.isChildOf(parentRow.getTableName())) {
                    value = parentRow.get(columnDefn.getParentColumn().getColumnName());
                }
                else {
                    value = columnDefn.getDefaultValue();
                }
            }
            if (value != null && value instanceof String) {
                final String valueStr = (String)value;
                if (columnDefn.getUniqueValueGeneration() != null) {
                    if (this.isUVHPattern(valueStr, columnDefn)) {
                        this.uvhMap.put(valueStr, row.get(columnDefn.getColumnName()));
                        continue;
                    }
                    if (valueStr.toString().startsWith("UVH@")) {
                        value = valueStr;
                    }
                }
                else if (columnDefn.getParentColumn() != null) {
                    if (this.isUVHPattern(valueStr, columnDefn.getParentColumn())) {
                        ((MultiMap<Object, Object>)this.uvhChildRowsMap).addToList(valueStr, new ChildRow(row, columnName));
                        Json2DoConverter.LOGGER.log(Level.FINEST, "uvhchild; {0}" + columnName, this.uvhChildRowsMap);
                    }
                    else if (valueStr.toString().startsWith("UVH@")) {
                        value = valueStr;
                    }
                }
            }
            Object colvalue2 = null;
            try {
                if (value != null) {
                    final String dataType = columnDefn.getDataType();
                    if (this.handleEnDecryption && columnDefn.isEncryptedColumn() && !DataTypeUtil.isUDT(dataType)) {
                        value = JsonUtil.decryptValue(String.valueOf(value));
                    }
                    colvalue2 = XmlDoUtil.convert(value.toString(), dataType);
                }
                if (!colVsValue.containsKey(columnName.toLowerCase(Locale.ENGLISH)) && colvalue2 == null) {
                    continue;
                }
                row.set(columnName, colvalue2);
            }
            catch (final Exception ex) {
                row.set(columnDefn.getColumnName(), value);
                Json2DoConverter.LOGGER.log(Level.FINE, "Illegal column value {0} for datatype " + columnDefn.getDataType(), value);
            }
        }
        return row;
    }
    
    private boolean isUVHPattern(final String input, final ColumnDefinition columnDefn) {
        return input.toLowerCase().startsWith((columnDefn.getTableName() + ':' + columnDefn.getColumnName() + ':').toLowerCase());
    }
    
    private Long getLongValue(final String valueStr, final ColumnDefinition column) throws JSONException {
        try {
            return Long.valueOf(valueStr);
        }
        catch (final NumberFormatException exp) {
            throw JsonUtil.createJsonExpception("Long value should be given for this column - " + column.getTableName() + '.' + column.getColumnName(), exp);
        }
    }
    
    private List<ColumnDefinition> getColumnDefs(final String tableName) {
        try {
            return MetaDataUtil.getTableDefinitionByName(tableName).getColumnList();
        }
        catch (final MetaDataException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private Object getDynamicValue(final String tableName, final String columnName, final String columnValue) throws DynamicValueHandlingException, DataAccessException, JSONException {
        Object returnValue = null;
        final Object temp = DynamicValueHandlerRepositry.dynamicHandlers.get(tableName + ":" + columnName);
        if (temp != null) {
            final DVHandlerTemplate dvTemplate = (DVHandlerTemplate)temp;
            final DynamicValueHandler handler = dvTemplate.getDynamicValueHandler();
            if (handler != null) {
                returnValue = handler.getColumnValue(dvTemplate.getTableName(), dvTemplate.getColumnName(), dvTemplate.getConfiguredAttributes(), columnValue);
                if (returnValue == null && handler instanceof SQLDynamicValueHandler) {
                    final String refTbName = dvTemplate.getConfiguredAttributes().getProperty("referred-table");
                    String reftableNodename = DynamicValueHandlerRepositry.getTableDisplayName(refTbName);
                    try {
                        if (reftableNodename == null) {
                            final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(refTbName);
                            reftableNodename = tableDef.getDisplayName();
                            if (reftableNodename == null) {
                                reftableNodename = refTbName;
                            }
                        }
                        reftableNodename = reftableNodename.toLowerCase(Locale.ENGLISH);
                    }
                    catch (final MetaDataException ex) {
                        ex.printStackTrace();
                    }
                    if (this.jsonObject.has(reftableNodename) && dvTemplate.getConfiguredAttributes() != null) {
                        final JSONArray jsonArr = this.jsonObject.getJSONArray(reftableNodename);
                        final String cricol = dvTemplate.getConfiguredAttributes().getProperty("criteria-column");
                        final DataObject tempDO = new WritableDataObject();
                        for (int i = 0; i < jsonArr.length(); ++i) {
                            final Row r = this.getRowFromTransformedJsonObject(refTbName, jsonArr.getJSONObject(i), null);
                            final Object jsonColValue = r.get(cricol);
                            if (jsonColValue != null && jsonColValue.equals(columnValue)) {
                                tempDO.addRow(r);
                                break;
                            }
                        }
                        if (tempDO.size(refTbName) > 0) {
                            try {
                                handler.set(tempDO);
                                returnValue = handler.getColumnValue(dvTemplate.getTableName(), dvTemplate.getColumnName(), dvTemplate.getConfiguredAttributes(), columnValue);
                            }
                            finally {
                                handler.set(null);
                            }
                        }
                    }
                }
            }
        }
        return returnValue;
    }
    
    private String getTableName(final String displayName) {
        String tableName = null;
        try {
            tableName = DynamicValueHandlerRepositry.getTableName(displayName);
            if (tableName == null) {
                tableName = MetaDataUtil.getDefinedTableNameByDisplayName(displayName);
                if (tableName == null) {
                    tableName = displayName;
                }
            }
            tableName = MetaDataUtil.getDefinedTableName(tableName);
            Json2DoConverter.LOGGER.log(Level.FINE, "defined tablename : {0}", tableName);
        }
        catch (final MetaDataException ex) {
            ex.printStackTrace();
        }
        return tableName;
    }
    
    static {
        CLASS_NAME = Json2DoConverter.class.getName();
        LOGGER = Logger.getLogger(Json2DoConverter.CLASS_NAME);
    }
    
    private static class MultiMap<K, V>
    {
        private Map<K, List<V>> map;
        
        private MultiMap() {
            this.map = new Hashtable<K, List<V>>();
        }
        
        private Set<K> keySet() {
            return this.map.keySet();
        }
        
        private List<V> getList(final K key) {
            return this.map.get(key);
        }
        
        private boolean addToList(final K key, final V value) {
            List<V> list = this.map.get(key);
            if (list == null) {
                list = new ArrayList<V>();
                this.map.put(key, list);
            }
            return list.add(value);
        }
    }
    
    private static class JSONObject1
    {
        private JSONObject jsonObj;
        private List<String> childTables;
        
        JSONObject1(final JSONObject jsonObj) throws JSONException {
            this.jsonObj = null;
            this.childTables = null;
            this.jsonObj = jsonObj;
            if (jsonObj.length() == 0) {
                this.childTables = Collections.EMPTY_LIST;
            }
            else {
                this.childTables = new ArrayList<String>();
                final Iterator<String> keys = jsonObj.keys();
                while (keys.hasNext()) {
                    final String key = keys.next();
                    if (jsonObj.get(key) instanceof JSONArray) {
                        this.childTables.add(key);
                    }
                }
            }
        }
        
        JSONObject get() {
            return this.jsonObj;
        }
        
        Object getValue(final String key) {
            try {
                return this.jsonObj.get(key);
            }
            catch (final JSONException ex) {
                return null;
            }
        }
        
        List<String> childTables() {
            return this.childTables;
        }
    }
    
    private class ChildRow
    {
        private Row row;
        private String columnName;
        
        ChildRow(final Row row, final String columnName) {
            this.row = row;
            this.columnName = columnName;
        }
    }
}
