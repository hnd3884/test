package com.me.devicemanagement.onpremise.server.util;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import com.me.devicemanagement.onpremise.properties.util.GeneralPropertiesLoader;
import java.io.Reader;
import java.io.FileReader;
import org.json.simple.parser.JSONParser;
import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.Map;
import com.adventnet.ds.query.SelectQuery;
import java.io.File;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.components.table.web.TableUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.AlterTableQueryImpl;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.HashMap;
import org.json.simple.JSONObject;
import java.util.logging.Logger;

public class CustomColumnUtil
{
    private static Logger logger;
    private static JSONObject customColumnJSON;
    private static final String DETAILS = "Details";
    private static HashMap<String, String> tempFolders;
    private static int retryCount;
    
    public static void addCustomColumnInTable(final ColumnDefinition columnDefinition, final String tableName) throws Exception {
        try {
            CustomColumnUtil.logger.log(Level.INFO, "Inside addCustomColumnInTable...");
            final AlterTableQuery alterTableQuery = (AlterTableQuery)new AlterTableQueryImpl(tableName);
            alterTableQuery.addDynamicColumn(columnDefinition);
            DataAccess.alterTable(alterTableQuery);
        }
        catch (final Exception e) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while adding column in the table", e);
            throw e;
        }
    }
    
    public static void addCustomColumnInView(final String viewName, final ColumnDefinition columnDefinition, final Row columnConfigRow) throws Exception {
        addCustomColumnInView(viewName, columnDefinition, columnConfigRow, null);
    }
    
    public static void addCustomColumnInView(final String viewName, final ColumnDefinition columnDefinition, final Row columnConfigRow, final Row emberColumnConfigRow) throws Exception {
        try {
            CustomColumnUtil.logger.log(Level.INFO, "Inside addCustomColumnInView...");
            TableUtil.addDynamicColumnInView(viewName, columnDefinition, columnConfigRow);
        }
        catch (final Exception e) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while adding column in view", e);
            throw e;
        }
    }
    
    public static void modifyCustomColumn(final String tableName, final String columnName, final ColumnDefinition columnDefinition) throws Exception {
        try {
            CustomColumnUtil.logger.log(Level.INFO, "Inside modifyCustomColumn...");
            final AlterTableQuery atq = (AlterTableQuery)new AlterTableQueryImpl(tableName);
            atq.modifyDynamicColumn(columnName, columnDefinition);
            DataAccess.alterTable(atq);
        }
        catch (final Exception e) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while modifying custom column:", e);
            throw e;
        }
    }
    
    public static void enablePIIMask(final String tableName, final String columnName, final String baseView) {
        final String columnAlias = tableName + "." + columnName;
        if (!checkForPII(columnAlias, baseView)) {
            final Long originalViewId = WebViewAPI.getViewNameNo((Object)baseView);
            final WritableDataObject writableDataObject = new WritableDataObject();
            Row piiRow = new Row("PIIRedactConfig");
            piiRow.set("VIEWNAME", (Object)originalViewId);
            piiRow.set("COLUMNALIAS", (Object)columnAlias);
            piiRow.set("REDACT_TYPE", (Object)"MASK");
            try {
                writableDataObject.addRow(piiRow);
                MetaDataUtil.setAttribute(tableName, columnName, "pii", "MASK");
                final DataObject personalisedViewDO = SyMUtil.getPersonalisedViewsForViewName(baseView);
                if (personalisedViewDO != null) {
                    final Iterator iterator = personalisedViewDO.getRows("PersonalizedViewMap");
                    while (iterator.hasNext()) {
                        final Row personalisedView = iterator.next();
                        final Long persViewId = (Long)personalisedView.get("PERSVIEWNAME");
                        piiRow = new Row("PIIRedactConfig");
                        piiRow.set("VIEWNAME", (Object)persViewId);
                        piiRow.set("COLUMNALIAS", (Object)columnAlias);
                        piiRow.set("REDACT_TYPE", (Object)"MASK");
                        writableDataObject.addRow(piiRow);
                    }
                    DataAccess.add((DataObject)writableDataObject);
                }
            }
            catch (final Exception e) {
                CustomColumnUtil.logger.log(Level.SEVERE, "Exception while adding PII for Custom Column", e);
            }
        }
    }
    
    public static void deleteCustomColumnFromView(final String viewName, final String tableName, final String columnName) throws Exception {
        try {
            CustomColumnUtil.logger.log(Level.INFO, "Inside deleteCustomColumnFromView...");
            TableUtil.removeDynamicColumnFromView(viewName, tableName, tableName + "." + columnName);
        }
        catch (final Exception e) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while deleting custom column from view", e);
            throw e;
        }
    }
    
    private static boolean moveToTempFolder(final String tableName, final String columnName) {
        boolean success = true;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ColumnDetailsExtn"));
            selectQuery.addSelectColumn(new Column("ColumnDetailsExtn", "COLUMN_ID"));
            selectQuery.addSelectColumn(new Column("ColumnDetailsExtn", "EXTENDED_DATA_TYPE_NAME"));
            selectQuery.addSelectColumn(new Column("ColumnDetailsExtn", "ADDED_BY"));
            final Join join = new Join(Table.getTable("ColumnDetailsExtn"), Table.getTable("ColumnDetails"), new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
            final Join tableJoin = new Join(Table.getTable("ColumnDetails"), Table.getTable("TableDetails"), new String[] { "TABLE_ID" }, new String[] { "TABLE_ID" }, 2);
            selectQuery.addJoin(join);
            selectQuery.addJoin(tableJoin);
            final Criteria tableCriteria = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), (Object)tableName, 0, false);
            final Criteria columnCriteria = new Criteria(Column.getColumn("ColumnDetails", "COLUMN_NAME"), (Object)columnName, 0, false);
            selectQuery.setCriteria(tableCriteria.and(columnCriteria));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("ColumnDetailsExtn");
                if (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final String dataType = (String)row.get("EXTENDED_DATA_TYPE_NAME");
                    if (dataType.equalsIgnoreCase("FILE")) {
                        final String parentFolder = tableName.equalsIgnoreCase("ManagedComputerCustomFields") ? "Computer" : "Software";
                        final File customColumnFiles = new File(System.getProperty("server.home") + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "customColumnFiles");
                        if (customColumnFiles.exists()) {
                            final File[] listOfFiles = customColumnFiles.listFiles();
                            if (listOfFiles != null) {
                                for (int i = 0; i < listOfFiles.length; ++i) {
                                    if (listOfFiles[i].isDirectory()) {
                                        final String customerID = listOfFiles[i].getName();
                                        final String oldFilePath = System.getProperty("server.home") + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "customColumnFiles" + File.separator + customerID + File.separator + parentFolder + File.separator + columnName;
                                        final String newFilePath = System.getProperty("server.home") + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "customColumnFiles" + File.separator + customerID + File.separator + parentFolder + File.separator + SyMUtil.getCurrentTimeInMillis();
                                        if (new File(oldFilePath).exists()) {
                                            if (!renameFileWithRetryCount(new File(oldFilePath), new File(newFilePath), CustomColumnUtil.retryCount)) {
                                                final String exception = "Can't rename folder : " + oldFilePath + " : " + newFilePath;
                                                CustomColumnUtil.logger.log(Level.SEVERE, exception);
                                                throw new Exception(exception);
                                            }
                                            CustomColumnUtil.tempFolders.put(oldFilePath, newFilePath);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            CustomColumnUtil.logger.log(Level.SEVERE, ex.getMessage());
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception occurred while move files of " + tableName + "." + columnName + " column");
            success = false;
        }
        return success;
    }
    
    private static void resetTempFolders() {
        CustomColumnUtil.tempFolders = new HashMap<String, String>();
    }
    
    private static HashMap<String, String> getTempFolders() {
        return CustomColumnUtil.tempFolders;
    }
    
    private static void removeTempFolders() {
        final HashMap<String, String> folders = getTempFolders();
        for (final Map.Entry<String, String> folder : folders.entrySet()) {
            int i = 0;
            while (i < CustomColumnUtil.retryCount) {
                try {
                    final String tempFolder = folder.getValue();
                    ApiFactoryProvider.getFileAccessAPI().forceDeleteDirectory(tempFolder);
                }
                catch (final Exception ex1) {
                    CustomColumnUtil.logger.log(Level.INFO, "attempt : " + (i + 1) + " fail");
                    CustomColumnUtil.logger.log(Level.SEVERE, "Exception while remove folder : " + folder.getKey() + " : " + folder.getValue());
                    CustomColumnUtil.logger.log(Level.SEVERE, "Exception : " + ex1);
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (final Exception ex2) {
                        CustomColumnUtil.logger.log(Level.SEVERE, "Exception : " + ex2);
                    }
                    ++i;
                    continue;
                }
                break;
            }
        }
    }
    
    private static boolean renameFileWithRetryCount(final File oldFile, final File newFile, final int retryCount) {
        boolean isMoved = false;
        try {
            for (int i = 0; i < retryCount; ++i) {
                isMoved = oldFile.renameTo(newFile);
                if (isMoved) {
                    break;
                }
                CustomColumnUtil.logger.log(Level.INFO, "folder rename : Waiting for 2 seconds...");
                Thread.sleep(2000L);
            }
        }
        catch (final Exception ex) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while rename folder : " + ex);
        }
        return isMoved;
    }
    
    private static void revertRenamedFolders() {
        final HashMap<String, String> folders = getTempFolders();
        if (!folders.isEmpty()) {
            for (final Map.Entry<String, String> folder : folders.entrySet()) {
                File oldFolder = null;
                File newFolder = null;
                try {
                    oldFolder = new File(folder.getKey());
                    newFolder = new File(folder.getValue());
                    if (renameFileWithRetryCount(newFolder, oldFolder, CustomColumnUtil.retryCount)) {
                        continue;
                    }
                    CustomColumnUtil.logger.log(Level.SEVERE, "Can't rename : " + newFolder.getAbsolutePath() + " : " + oldFolder.getAbsolutePath());
                }
                catch (final Exception ex) {
                    if (newFolder != null) {
                        CustomColumnUtil.logger.log(Level.SEVERE, "Exception while revert renamed folder : " + newFolder.getAbsolutePath() + " : " + oldFolder.getAbsolutePath());
                    }
                    CustomColumnUtil.logger.log(Level.SEVERE, "Exception : " + ex);
                }
            }
        }
    }
    
    public static void deleteCustomColumnFromTable(final String tableName, final String columnName) throws Exception {
        try {
            CustomColumnUtil.logger.log(Level.INFO, "Inside deleteCustomColumnFromTable...");
            resetTempFolders();
            final boolean success = moveToTempFolder(tableName, columnName);
            if (!success) {
                throw new Exception("Exception While renaming folders of column : " + tableName + "." + columnName);
            }
            final AlterTableQuery atq = (AlterTableQuery)new AlterTableQueryImpl(tableName);
            atq.removeDynamicColumn(columnName);
            DataAccess.alterTable(atq);
            removeTempFolders();
        }
        catch (final Exception e) {
            revertRenamedFolders();
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while deleting custom column from table", e);
            throw e;
        }
    }
    
    public static void addCustomDataType(final DataTypeDefinition dataTypeDefinition) throws Exception {
        try {
            CustomColumnUtil.logger.log(Level.INFO, "Inside addCustomDataType...");
            SyMUtil.getPersistence().addDataType(dataTypeDefinition);
        }
        catch (final Exception e) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while adding data type", e);
            throw e;
        }
    }
    
    public static JSONObject getCustomColumnJSON() {
        if (CustomColumnUtil.customColumnJSON == null) {
            try {
                readFromFile();
            }
            catch (final Exception e) {
                CustomColumnUtil.logger.log(Level.SEVERE, "Exception while fetching customColumn details json object", e);
            }
        }
        return CustomColumnUtil.customColumnJSON;
    }
    
    public static void readFromFile() throws Exception {
        final String fileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "CustomColumns" + File.separator + "CustomColumn.json";
        final File custColDetailsFile = new File(fileName);
        final JSONParser jsonParser = new JSONParser();
        if (custColDetailsFile.exists()) {
            final FileReader fileReader = new FileReader(custColDetailsFile);
            CustomColumnUtil.customColumnJSON = (JSONObject)jsonParser.parse((Reader)fileReader);
            final String[] activeProducts = GeneralPropertiesLoader.getAllActiveProduct();
            CustomColumnUtil.customColumnJSON = constructJSONForProds(activeProducts);
        }
    }
    
    private static JSONObject constructJSONForProds(final String[] products) {
        final JSONObject details = (JSONObject)(CustomColumnUtil.customColumnJSON.containsKey((Object)"Details") ? CustomColumnUtil.customColumnJSON.remove((Object)"Details") : new JSONObject());
        final Object[] array;
        final Object[] customFields = array = details.keySet().toArray();
        for (final Object customField : array) {
            JSONObject customFieldJSONObj = (JSONObject)(details.containsKey(customField) ? details.remove(customField) : new JSONObject());
            customFieldJSONObj = constructCustomFieldJSONForProds(customFieldJSONObj, products);
            if (customFieldJSONObj != null) {
                details.put(customField, (Object)customFieldJSONObj);
            }
            else {
                final JSONArray displayOrder = (JSONArray)(CustomColumnUtil.customColumnJSON.containsKey((Object)"displayOrder") ? CustomColumnUtil.customColumnJSON.remove((Object)"displayOrder") : new JSONArray());
                displayOrder.remove(customField);
                CustomColumnUtil.customColumnJSON.put((Object)"displayOrder", (Object)displayOrder);
            }
        }
        if (!details.isEmpty()) {
            CustomColumnUtil.customColumnJSON.put((Object)"Details", (Object)details);
        }
        else {
            CustomColumnUtil.customColumnJSON = new JSONObject();
        }
        return CustomColumnUtil.customColumnJSON;
    }
    
    private static JSONObject constructCustomFieldJSONForProds(final JSONObject customFieldJSONObj, final String[] products) {
        final HashMap<String, JSONArray> allAssociatedViews = new HashMap<String, JSONArray>();
        final Object[] array;
        final Object[] customFieldKeys = array = customFieldJSONObj.keySet().toArray();
        for (final Object key : array) {
            if (key.toString().contains("associatedViews")) {
                allAssociatedViews.put(key.toString(), (JSONArray)customFieldJSONObj.remove(key));
            }
        }
        final JSONArray associatedViews = new JSONArray();
        boolean isAssViewsVisited = false;
        for (final String product : products) {
            if (allAssociatedViews.keySet().contains(product.toLowerCase() + "_" + "associatedViews")) {
                final JSONArray additionalAssociatedViews = allAssociatedViews.get(product.toLowerCase() + "_" + "associatedViews");
                for (final Object object : additionalAssociatedViews) {
                    if (!associatedViews.contains(object)) {
                        associatedViews.add((Object)object.toString());
                    }
                }
            }
            else if (!isAssViewsVisited) {
                final JSONArray additionalAssociatedViews = allAssociatedViews.get("associatedViews");
                for (final Object object : additionalAssociatedViews) {
                    if (!associatedViews.contains(object)) {
                        associatedViews.add((Object)object.toString());
                    }
                }
                isAssViewsVisited = true;
            }
        }
        customFieldJSONObj.put((Object)"associatedViews", (Object)associatedViews);
        final JSONObject viewDetails = (JSONObject)customFieldJSONObj.remove((Object)"viewDetails");
        if (viewDetails != null) {
            final Object[] array2;
            final Object[] views = array2 = viewDetails.keySet().toArray();
            for (final Object view : array2) {
                final JSONObject viewJSON = (JSONObject)viewDetails.remove(view);
                if (viewJSON != null && associatedViews.contains((Object)view.toString())) {
                    viewDetails.put((Object)view.toString(), (Object)viewJSON);
                }
            }
        }
        if (viewDetails != null && !viewDetails.isEmpty()) {
            customFieldJSONObj.put((Object)"viewDetails", (Object)viewDetails);
            return customFieldJSONObj;
        }
        return null;
    }
    
    public static JSONObject getCustomColumnDetails() {
        JSONObject customColumnDetails = new JSONObject();
        try {
            customColumnDetails = getCustomColumnJSON();
            customColumnDetails = (JSONObject)(customColumnDetails.isEmpty() ? customColumnDetails : customColumnDetails.get((Object)"Details"));
        }
        catch (final Exception e) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while fetching customColumn details json object", e);
        }
        return customColumnDetails;
    }
    
    public static String getBaseEntityWithViewName(final String viewName) {
        final JSONObject customColumnDetails = getCustomColumnDetails();
        try {
            for (final String baseEntityKey : customColumnDetails.keySet()) {
                final JSONObject baseEntityDetails = (JSONObject)customColumnDetails.get((Object)baseEntityKey);
                final JSONArray associatedViews = (JSONArray)baseEntityDetails.get((Object)"associatedViews");
                if (associatedViews.contains((Object)viewName)) {
                    return baseEntityKey;
                }
                final JSONObject viewDetails = (JSONObject)baseEntityDetails.get((Object)"viewDetails");
                if (viewDetails.containsKey((Object)viewName)) {
                    return baseEntityKey;
                }
            }
        }
        catch (final Exception ex) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while fetching baseEntityDetails with view name ", ex);
        }
        return null;
    }
    
    public static JSONArray getAssociatedViewsForBaseEntity(final String baseEntity) {
        final JSONObject customColumnDetails = getCustomColumnDetails();
        final JSONObject baseEntityDetails = (JSONObject)customColumnDetails.get((Object)baseEntity);
        return (baseEntityDetails != null) ? ((JSONArray)baseEntityDetails.get((Object)"associatedViews")) : null;
    }
    
    public static String getColumnNameAlias(final String baseEntity, final String viewName) {
        final JSONObject customColumnDetails = getCustomColumnDetails();
        try {
            final JSONObject baseEntityDetails = (JSONObject)customColumnDetails.get((Object)baseEntity);
            final JSONObject viewDetails = (JSONObject)baseEntityDetails.get((Object)"viewDetails");
            final JSONArray associatedViews = (JSONArray)baseEntityDetails.get((Object)"associatedViews");
            if (viewDetails.containsKey((Object)viewName)) {
                final JSONObject view = (JSONObject)viewDetails.get((Object)viewName);
                return view.get((Object)"columnAlias").toString();
            }
        }
        catch (final Exception ex) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while getting baseColumnAlias with view name ", ex);
        }
        return null;
    }
    
    public static boolean isCustomColumnEnabledForView(final String viewName) {
        final JSONObject customColumnDetails = getCustomColumnDetails();
        try {
            for (final String baseEntityKey : customColumnDetails.keySet()) {
                final JSONObject viewDetails = (JSONObject)customColumnDetails.get((Object)baseEntityKey);
                final JSONObject associatedViews = (JSONObject)viewDetails.get((Object)"viewDetails");
                if (associatedViews.containsKey((Object)viewName)) {
                    return true;
                }
            }
        }
        catch (final Exception ex) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while getting baseTableAlias with view name ", ex);
        }
        return false;
    }
    
    public static void deletePIIRedactConfig(final String columnAlias) {
        try {
            final Criteria piiRedactConfigCriteria = new Criteria(Column.getColumn("PIIRedactConfig", "COLUMNALIAS"), (Object)columnAlias, 0);
            SyMUtil.getPersistence().delete(piiRedactConfigCriteria);
            CustomColumnUtil.logger.log(Level.INFO, "Successfully cleared PII Redact Info for the views");
        }
        catch (final Exception e) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while deleting PII Redact information for the columnAlias", e);
        }
    }
    
    public static Boolean checkForPII(final String columnAlias, final String viewName) {
        Boolean isPII = false;
        try {
            final Long originalViewId = WebViewAPI.getViewNameNo((Object)viewName);
            Criteria columnAliasCriteria = new Criteria(Column.getColumn("PIIRedactConfig", "COLUMNALIAS"), (Object)columnAlias, 0);
            if (viewName != null) {
                final Criteria viewNoCriteria = new Criteria(Column.getColumn("PIIRedactConfig", "VIEWNAME"), (Object)originalViewId, 0);
                columnAliasCriteria = columnAliasCriteria.and(viewNoCriteria);
            }
            final DataObject isPIIDO = SyMUtil.getPersistence().get("PIIRedactConfig", columnAliasCriteria);
            if (isPIIDO != null && !isPIIDO.isEmpty()) {
                isPII = true;
            }
        }
        catch (final Exception e) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while checking isPII for the columnAlias", e);
        }
        return isPII;
    }
    
    public static Boolean isPIICustomColumn(final String columnAlias) {
        Boolean isPIICustomColumn = false;
        try {
            final String[] tableDetails = columnAlias.split("\\.");
            final Criteria tableCrit = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), (Object)tableDetails[0], 0, false);
            final Criteria columnCrit = new Criteria(Column.getColumn("ColumnDetails", "COLUMN_NAME"), (Object)tableDetails[1], 0, false);
            final DataObject columnDetailsDO = SyMUtil.getColumnDetailsForColumn(tableCrit.and(columnCrit));
            if (columnDetailsDO != null && !columnDetailsDO.isEmpty()) {
                final Long columnID = (Long)columnDetailsDO.getFirstValue("ColumnDetails", "COLUMN_ID");
                final Row columnDetailsRow = DBUtil.getRowFromDB("ColumnDetailsExtn", "COLUMN_ID", (Object)columnID);
                if (columnDetailsRow != null) {
                    isPIICustomColumn = (Boolean)columnDetailsRow.get("IS_PII");
                }
            }
        }
        catch (final Exception e) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while checking isPIICustomColumn for the columnAlias", e);
        }
        return isPIICustomColumn;
    }
    
    public static JSONObject getTableDetailsFromCustomColumnJSON(final String tableName) {
        JSONObject tableDetails = null;
        try {
            final JSONObject customColumnJSON = getCustomColumnJSON();
            final JSONObject customColumnDetails = (JSONObject)customColumnJSON.get((Object)"Details");
            tableDetails = (JSONObject)customColumnDetails.get((Object)tableName);
        }
        catch (final Exception e) {
            CustomColumnUtil.logger.log(Level.WARNING, "subModule for the corresponding table not found in CustomColumnJSON", e);
        }
        return tableDetails;
    }
    
    public static List<String> getFileDataTypeColumnNames(final String tableName) throws Exception {
        final List<String> fileTypeColumns = new ArrayList<String>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ColumnDetailsExtn"));
        selectQuery.addSelectColumn(new Column("ColumnDetailsExtn", "COLUMN_ID"));
        selectQuery.addSelectColumn(new Column("ColumnDetails", "COLUMN_ID"));
        selectQuery.addSelectColumn(new Column("ColumnDetails", "COLUMN_NAME"));
        final Join join = new Join(Table.getTable("ColumnDetailsExtn"), Table.getTable("ColumnDetails"), new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
        final Join tableJoin = new Join(Table.getTable("ColumnDetails"), Table.getTable("TableDetails"), new String[] { "TABLE_ID" }, new String[] { "TABLE_ID" }, 2);
        selectQuery.addJoin(join);
        selectQuery.addJoin(tableJoin);
        final Criteria tableCriteria = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), (Object)tableName, 0, false);
        final Criteria dynamicColumnCriteria = new Criteria(Column.getColumn("ColumnDetails", "IS_DYNAMIC"), (Object)true, 0);
        final Criteria columnCriteria = new Criteria(Column.getColumn("ColumnDetailsExtn", "EXTENDED_DATA_TYPE_NAME"), (Object)"FILE", 0, false);
        selectQuery.setCriteria(columnCriteria.and(dynamicColumnCriteria).and(tableCriteria));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator iterator1 = dataObject.getRows("ColumnDetails");
            while (iterator1.hasNext()) {
                final Row row = iterator1.next();
                final String column = (String)row.get("COLUMN_NAME");
                fileTypeColumns.add(column);
            }
        }
        return fileTypeColumns;
    }
    
    public static String getExtendedDataTypeName(final String columnName, final String tableName) {
        String extendedDataType = "";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ColumnDetailsExtn"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            selectQuery.addJoin(new Join("ColumnDetailsExtn", "ColumnDetails", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2));
            selectQuery.addJoin(new Join("ColumnDetails", "TableDetails", new String[] { "TABLE_ID" }, new String[] { "TABLE_ID" }, 2));
            final Criteria tableCriteria = new Criteria(new Column("TableDetails", "TABLE_NAME"), (Object)tableName, 0, false);
            final Criteria columnCriteria = new Criteria(new Column("ColumnDetails", "COLUMN_NAME"), (Object)columnName, 0, false);
            selectQuery.setCriteria(tableCriteria.and(columnCriteria));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                extendedDataType = String.valueOf(dataObject.getFirstRow("ColumnDetailsExtn").get("EXTENDED_DATA_TYPE_NAME"));
            }
        }
        catch (final Exception ex) {
            CustomColumnUtil.logger.log(Level.INFO, "EXception while getting Extended DataType Name" + ex);
            ex.printStackTrace();
        }
        return extendedDataType;
    }
    
    public static String getDisplayName(final String tableName, final String columnName) {
        String displayName = "";
        try {
            final Criteria tableCrit = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), (Object)tableName, 0, false);
            final Criteria columnCrit = new Criteria(Column.getColumn("ColumnDetails", "COLUMN_NAME"), (Object)columnName, 0, false);
            final DataObject columnDO = SyMUtil.getColumnDetailsForColumn(tableCrit.and(columnCrit));
            if (columnDO != null && !columnDO.isEmpty()) {
                displayName = String.valueOf(columnDO.getFirstRow("ColumnDetails").get("DISPLAY_NAME"));
            }
        }
        catch (final Exception e) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while checking getting DisplayName ", e);
        }
        return displayName;
    }
    
    public static String getCustomColumnTableName(final String viewName) {
        final JSONObject customColumnDetails = getCustomColumnDetails();
        for (final String customColumnTableName : customColumnDetails.keySet()) {
            final JSONObject viewDetails = (JSONObject)customColumnDetails.get((Object)customColumnTableName);
            final JSONObject associatedViews = (JSONObject)viewDetails.get((Object)"viewDetails");
            if (associatedViews.containsKey((Object)viewName)) {
                return customColumnTableName;
            }
        }
        return null;
    }
    
    public static List<String> getPIIEnabledCustomColumns(final String tableName) throws Exception {
        final List<String> piiEnabledCustomColumns = new ArrayList<String>();
        try {
            if (tableName != null) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("TableDetails"));
                selectQuery.addJoin(new Join("TableDetails", "ColumnDetails", new String[] { "TABLE_ID" }, new String[] { "TABLE_ID" }, 2));
                selectQuery.addJoin(new Join("ColumnDetails", "ColumnDetailsExtn", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 1));
                selectQuery.addSelectColumn(new Column((String)null, "*"));
                Criteria criteria = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), (Object)tableName, 0, (boolean)Boolean.FALSE);
                criteria = criteria.and(new Criteria(Column.getColumn("ColumnDetails", "COLUMN_NAME"), (Object)(tableName.equalsIgnoreCase("ManagedComputerCustomFields") ? "RESOURCE_ID" : "SOFTWARE_ID"), 1, (boolean)Boolean.FALSE));
                criteria = criteria.and(new Criteria(Column.getColumn("ColumnDetailsExtn", "IS_PII"), (Object)true, 0));
                selectQuery.setCriteria(criteria);
                final DataObject dataObject = DataAccess.get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("ColumnDetails");
                    while (iterator.hasNext()) {
                        final Row row = iterator.next();
                        final String column = (String)row.get("COLUMN_NAME");
                        piiEnabledCustomColumns.add(tableName + "." + column);
                    }
                }
            }
        }
        catch (final Exception ex) {
            CustomColumnUtil.logger.log(Level.SEVERE, "Exception while fetching PII Enabled CustomColumns..." + ex);
            ex.printStackTrace();
            throw ex;
        }
        return piiEnabledCustomColumns;
    }
    
    static {
        CustomColumnUtil.logger = Logger.getLogger(CustomColumnUtil.class.getName());
        CustomColumnUtil.customColumnJSON = null;
        CustomColumnUtil.tempFolders = new HashMap<String, String>();
        CustomColumnUtil.retryCount = 2;
    }
}
