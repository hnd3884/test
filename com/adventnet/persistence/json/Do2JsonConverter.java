package com.adventnet.persistence.json;

import java.util.Collections;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.persistence.xml.XmlRowTransformer;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.persistence.PersistenceException;
import java.util.Map;
import com.adventnet.persistence.xml.DynamicValueHandlerRepositry;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.logging.Level;
import com.adventnet.persistence.xml.DynamicValueHandlerUtil;
import java.util.Collection;
import com.adventnet.persistence.internal.SequenceGeneratorRepository;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.persistence.QueryConstructor;
import org.json.JSONArray;
import com.adventnet.persistence.Row;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.persistence.xml.XmlDoUtil;
import java.io.Writer;
import java.io.PrintWriter;
import com.adventnet.persistence.xml.DynamicValueHandlingException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.io.IOException;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.xml.ParentChildrenMap;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

public class Do2JsonConverter
{
    private static final String CLASS_NAME;
    private static final Logger LOGGER;
    private HashMap rowVsJsonObj;
    private List uvhPatternList;
    private boolean useuvh;
    private boolean handleEnDecryption;
    private ParentChildrenMap rootNode;
    private DataObject dataObject;
    private JSONObject jsonObject;
    
    public static void transform(final DataObject dataObject, final String filePath) throws IOException, JSONException, DataAccessException, DynamicValueHandlingException {
        transform(dataObject, filePath, false);
    }
    
    public static void transform(final DataObject dataObject, final String filePath, final boolean useuvh) throws IOException, JSONException, DataAccessException, DynamicValueHandlingException {
        final PrintWriter out = new PrintWriter(filePath);
        try {
            transform(dataObject, out, useuvh);
        }
        finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
    
    public static void transform(final DataObject dataObject, final Writer writer) throws IOException, JSONException, DataAccessException, DynamicValueHandlingException {
        transform(dataObject, writer, false);
    }
    
    public static void transform(final DataObject dataObject, final Writer writer, final boolean useuvh) throws IOException, JSONException, DataAccessException, DynamicValueHandlingException {
        if (writer == null) {
            throw new IllegalArgumentException("Writer param cannot be null");
        }
        writer.write(getJsonString(dataObject, useuvh));
    }
    
    public static void serialize(final DataObject dataObject, final String filePath) throws IOException, JSONException, DataAccessException {
        final PrintWriter out = new PrintWriter(filePath);
        try {
            serialize(dataObject, out);
        }
        finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
    
    public static void serialize(final DataObject dataObject, final Writer writer) throws IOException, JSONException, DataAccessException {
        if (writer == null) {
            throw new IllegalArgumentException("Writer param cannot be null");
        }
        writer.write(getSerializedJsonString(dataObject));
    }
    
    public static String getJsonString(final DataObject dataObject) throws JSONException, DataAccessException, DynamicValueHandlingException {
        return getJsonString(dataObject, false);
    }
    
    public static String getJsonString(final DataObject dataObject, final boolean useuvh) throws JSONException, DataAccessException, DynamicValueHandlingException {
        return getJsonObject(dataObject, useuvh).toString();
    }
    
    public static JSONObject getJsonObject(final DataObject dataObject) throws JSONException, DataAccessException, DynamicValueHandlingException {
        return getJsonObject(dataObject, false);
    }
    
    public static JSONObject getJsonObject(final DataObject dataObject, final boolean useuvh) throws JSONException, DataAccessException, DynamicValueHandlingException {
        Do2JsonConverter jsonConv = null;
        try {
            jsonConv = new Do2JsonConverter(XmlDoUtil.getPCM("root", dataObject), dataObject, useuvh);
        }
        catch (final Exception ex) {
            throw JsonUtil.createJsonExpception("Problem during [DO to JSON] conversion", ex);
        }
        jsonConv.initTransformation();
        return jsonConv.jsonObject;
    }
    
    public static JSONObject getJsonObject(final DataObject dataObject, final boolean useuvh, final boolean handleEnDecryption) throws Exception {
        Do2JsonConverter jsonConv = null;
        try {
            jsonConv = new Do2JsonConverter(XmlDoUtil.getPCM("root", dataObject), dataObject, useuvh, handleEnDecryption);
        }
        catch (final Exception ex) {
            throw JsonUtil.createJsonExpception("Problem during [DO to JSON] conversion", ex);
        }
        jsonConv.initTransformation();
        return jsonConv.jsonObject;
    }
    
    public static String getSerializedJsonString(final DataObject dataObject) throws JSONException, DataAccessException {
        return getSerializedJSONObject(dataObject).toString();
    }
    
    public static JSONObject getSerializedJSONObject(final DataObject dataObject) throws JSONException, DataAccessException {
        Do2JsonConverter jsonConv = null;
        try {
            jsonConv = new Do2JsonConverter(dataObject);
        }
        catch (final Exception ex) {
            throw JsonUtil.createJsonExpception("Problem during [DO to JSON] conversion", ex);
        }
        jsonConv.initSerialization();
        return jsonConv.jsonObject;
    }
    
    private Do2JsonConverter(final ParentChildrenMap rootNode, final DataObject dataObject, final boolean useUvhPattern) {
        this(rootNode, dataObject, useUvhPattern, false);
    }
    
    private Do2JsonConverter(final ParentChildrenMap rootNode, final DataObject dataObject, final boolean useUvhPattern, final boolean handleEnDecryption) {
        this.rowVsJsonObj = new HashMap();
        this.uvhPatternList = new ArrayList();
        this.rootNode = rootNode;
        this.dataObject = dataObject;
        this.jsonObject = new JSONObject();
        this.useuvh = useUvhPattern;
        this.handleEnDecryption = handleEnDecryption;
    }
    
    private Do2JsonConverter(final DataObject dataObject) {
        this.rowVsJsonObj = new HashMap();
        this.uvhPatternList = new ArrayList();
        this.dataObject = dataObject;
        this.jsonObject = new JSONObject();
    }
    
    private void initSerialization() throws DataAccessException, JSONException {
        final List<String> tableNames = this.dataObject.getTableNames();
        for (final String tableName : tableNames) {
            this.jsonObject.put(tableName.toLowerCase(), (Object)this.getSerializedJsonArray(tableName));
        }
    }
    
    private void initTransformation() throws DataAccessException, JSONException, DynamicValueHandlingException {
        final List<ParentChildrenMap> tableNodes = getChildNodes(this.rootNode);
        for (final ParentChildrenMap tableNode : tableNodes) {
            final String tableName = tableNode.getElementName();
            this.jsonObject.put(this.getDisplayName(tableName), (Object)this.getTableJson(tableNode, this.dataObject.getRows(tableName), false));
        }
    }
    
    private JSONArray getSerializedJsonArray(final String tablename) throws JSONException, DataAccessException {
        final JSONArray toRet = new JSONArray();
        final Iterator<Row> rows = this.dataObject.getRows(tablename);
        while (rows.hasNext()) {
            final Row row = rows.next();
            JSONObject rowJson = row.getAsJSON();
            if (rowJson == null) {
                rowJson = new JSONObject();
            }
            toRet.put((Object)rowJson);
        }
        return toRet;
    }
    
    private JSONArray getTableJson(final ParentChildrenMap node, final Iterator<Row> rowsIter, final Boolean isChildTable) throws JSONException, DataAccessException, DynamicValueHandlingException {
        JsonUtil.assertTableName(node.getElementName());
        final JSONArray toRet = new JSONArray();
        if (!isChildTable) {
            while (rowsIter.hasNext()) {
                final JSONObject rowJson = new JSONObject();
                final Row row = rowsIter.next();
                this.insertRow(node, row, rowJson, false);
                this.rowVsJsonObj.put(row, rowJson);
                toRet.put((Object)rowJson);
            }
        }
        else {
            final String childTableName = node.getElementName();
            final String childTableDisplayName = this.getDisplayName(childTableName);
            final String parentTableName = node.getMasterTableName();
            final Iterator<Row> childRows = this.dataObject.getRows(childTableName);
            final JSONArray rootArray = new JSONArray();
            while (childRows.hasNext()) {
                final JSONObject rowjson = new JSONObject();
                final Row row2 = childRows.next();
                final ForeignKeyDefinition fkdef = QueryConstructor.getSuitableFK(childTableName, parentTableName);
                final Join join = QueryConstructor.getJoin(fkdef);
                final Row parentRow = this.dataObject.getRow(parentTableName, row2, join);
                if (parentRow == null) {
                    this.insertRow(node, row2, rowjson, false);
                    this.rowVsJsonObj.put(row2, rowjson);
                    rootArray.put((Object)rowjson);
                }
                else {
                    this.insertRow(node, row2, rowjson, true);
                    this.rowVsJsonObj.put(row2, rowjson);
                    final JSONObject parentJson = this.rowVsJsonObj.get(parentRow);
                    if (parentJson.isNull(childTableDisplayName)) {
                        final JSONArray newchildArray = new JSONArray();
                        newchildArray.put((Object)rowjson);
                        parentJson.put(childTableDisplayName, (Object)newchildArray);
                    }
                    else {
                        final JSONArray childArray = parentJson.getJSONArray(childTableDisplayName);
                        childArray.put((Object)rowjson);
                        parentJson.put(childTableDisplayName, (Object)childArray);
                    }
                }
            }
            if (rootArray.length() > 0) {
                this.jsonObject.put(childTableDisplayName, (Object)rootArray);
            }
        }
        final List<ParentChildrenMap> childNodes = getChildNodes(node);
        for (final ParentChildrenMap childNode : childNodes) {
            this.getTableJson(childNode, this.dataObject.getRows(childNode.getElementName()), true);
        }
        return toRet;
    }
    
    private void insertRow(final ParentChildrenMap node, final Row row, final JSONObject rowJson, final Boolean isChildRow) throws JSONException, DynamicValueHandlingException {
        try {
            final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(row.getTableName());
            SequenceGeneratorRepository.initGeneratorValues(tableDef);
            final List<String> columnNames = new ArrayList<String>(tableDef.getColumnNames());
            final HashMap colVsvalue = new HashMap();
            final DynamicValueHandlerUtil util = new DynamicValueHandlerUtil();
            final HashMap dynamicValues = util.getDynamicValues(row.getTableName(), columnNames, row, this.dataObject);
            Do2JsonConverter.LOGGER.log(Level.FINEST, "row; {0}", row);
            final List<ColumnDefinition> columnDefs = tableDef.getColumnList();
            for (final ColumnDefinition columnDefn : columnDefs) {
                if (columnDefn.isChildOf(node.getMasterTableName()) && isChildRow) {
                    continue;
                }
                final String columnName = columnDefn.getColumnName();
                Object columnValue = row.get(columnName);
                final Object defaultValue = columnDefn.getDefaultValue();
                if (columnValue == null) {
                    continue;
                }
                if (columnValue.equals(defaultValue)) {
                    continue;
                }
                final ColumnDefinition parentcolmDefn = columnDefn.getParentColumn();
                if (parentcolmDefn != null && parentcolmDefn.getUniqueValueGeneration() != null && this.useuvh) {
                    final String tableName = parentcolmDefn.getTableName().toLowerCase();
                    final String parentcolumn = parentcolmDefn.getColumnName().toLowerCase();
                    final String template = tableName + ":" + parentcolumn;
                    final String rowValue = String.valueOf(columnValue);
                    final String key = template + ":" + rowValue;
                    if (this.uvhPatternList.contains(key)) {
                        columnValue = key;
                    }
                }
                Label_0382: {
                    if (columnDefn.getDataType().equals("DATE")) {
                        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        if (columnValue instanceof String) {
                            try {
                                columnValue = formatter.parseObject((String)columnValue);
                                break Label_0382;
                            }
                            catch (final Exception e) {
                                e.printStackTrace();
                                throw new DynamicValueHandlingException(e.getLocalizedMessage());
                            }
                        }
                        columnValue = formatter.format((Date)columnValue);
                    }
                }
                Label_0474: {
                    if (columnDefn.getDataType().equals("DATETIME") || columnDefn.getDataType().equals("TIMESTAMP")) {
                        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if (columnValue instanceof String) {
                            try {
                                columnValue = formatter.parseObject((String)columnValue);
                                break Label_0474;
                            }
                            catch (final Exception e) {
                                e.printStackTrace();
                                throw new DynamicValueHandlingException(e.getLocalizedMessage());
                            }
                        }
                        columnValue = formatter.format((Date)columnValue);
                    }
                }
                if (dynamicValues.containsKey(columnName)) {
                    columnValue = dynamicValues.get(columnName);
                    if (columnValue == null) {
                        continue;
                    }
                }
                final Object colval = this.getValue(columnValue, columnDefn);
                colVsvalue.put(columnName, colval);
            }
            final XmlRowTransformer rowProcessInstance = DynamicValueHandlerRepositry.getRowTransformer(row.getTableName());
            if (rowProcessInstance != null) {
                rowProcessInstance.setDisplayNames(row.getTableName(), colVsvalue);
            }
            Do2JsonConverter.LOGGER.log(Level.FINEST, "col name Vs value:{0} ", colVsvalue);
            for (final Map.Entry col : colVsvalue.entrySet()) {
                rowJson.put(col.getKey().toString().toLowerCase(), col.getValue());
            }
        }
        catch (final PersistenceException ex) {
            throw JsonUtil.createJsonExpception("Problem during [DO to JSON] conversion", ex);
        }
        catch (final MetaDataException ex2) {
            throw JsonUtil.createJsonExpception("Problem during [DO to JSON] conversion", ex2);
        }
    }
    
    private Object getValue(final Object colvalue, final ColumnDefinition coldef) {
        final String columnName = coldef.getColumnName();
        if (coldef.getUniqueValueGeneration() != null && this.useuvh) {
            final String tableName = coldef.getTableName().toLowerCase();
            final String template = tableName + ":" + columnName.toLowerCase();
            final String rowValue = String.valueOf(colvalue);
            final String key = template + ":" + rowValue;
            this.uvhPatternList.add(key);
            return key;
        }
        if (this.handleEnDecryption && coldef.isEncryptedColumn() && !DataTypeUtil.isUDT(coldef.getDataType())) {
            try {
                return JsonUtil.encryptValue(colvalue.toString());
            }
            catch (final Exception e) {
                Do2JsonConverter.LOGGER.log(Level.FINEST, "Exception in encrypting value - " + e.getMessage());
            }
        }
        return (colvalue instanceof Long) ? colvalue.toString() : colvalue;
    }
    
    private static List getChildNodes(final ParentChildrenMap node) {
        final List childs = node.getChildPCMs();
        return (childs != null) ? childs : Collections.emptyList();
    }
    
    private String getDisplayName(final String tableName) {
        String displayname = DynamicValueHandlerRepositry.getTableDisplayName(tableName);
        try {
            if (displayname == null) {
                final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
                displayname = tableDef.getDisplayName();
                if (displayname == null) {
                    displayname = tableName;
                }
            }
        }
        catch (final MetaDataException ex) {
            ex.printStackTrace();
        }
        return displayname.toLowerCase();
    }
    
    static {
        CLASS_NAME = Do2JsonConverter.class.getName();
        LOGGER = Logger.getLogger(Do2JsonConverter.CLASS_NAME);
    }
}
