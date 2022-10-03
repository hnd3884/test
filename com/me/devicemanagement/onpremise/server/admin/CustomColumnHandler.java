package com.me.devicemanagement.onpremise.server.admin;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.Hashtable;
import org.json.JSONException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import com.adventnet.db.persistence.metadata.AllowedValues;
import com.adventnet.persistence.WritableDataObject;
import java.util.Iterator;
import com.adventnet.db.persistence.SequenceGenerator;
import com.adventnet.persistence.internal.SequenceGeneratorRepository;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.io.OutputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import com.adventnet.iam.security.UploadedFileItem;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.onpremise.server.util.CustomColumnUtil;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import org.json.simple.JSONObject;
import java.util.List;
import java.util.logging.Logger;

public class CustomColumnHandler
{
    private static Logger logger;
    private static List<CustomColumnListener> customColumnListenerList;
    
    public static void addCustomColumn(final JSONObject addCustomColumnJObj) throws Exception {
        try {
            CustomColumnHandler.logger.log(Level.INFO, "Inside addCustomColumn...");
            SyMUtil.getUserTransaction().begin();
            final ColumnDefinition columnDefinition = addCustomColumnInTable(addCustomColumnJObj);
            addCustomColumnInView(columnDefinition, addCustomColumnJObj);
            updateColumnDetailsExtn(addCustomColumnJObj);
            invokeListenerForCustomColumnAddition(addCustomColumnJObj);
            SyMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            SyMUtil.getUserTransaction().rollback();
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while adding custom column:", e);
            throw e;
        }
    }
    
    public static void modifyCustomColumn(final JSONObject modifyCustomColumnJObj) throws Exception {
        try {
            CustomColumnHandler.logger.log(Level.INFO, "Inside modifyCustomColumn...");
            final String tableName = (String)modifyCustomColumnJObj.get((Object)"tableName");
            final String actualColumnName = (String)modifyCustomColumnJObj.get((Object)"actualColumnName");
            String dataType = (String)modifyCustomColumnJObj.get((Object)"dataType");
            final Integer maxSize = (Integer)modifyCustomColumnJObj.get((Object)"maxSize");
            final String defaultValue = (String)modifyCustomColumnJObj.get((Object)"defaultValue");
            final String description = (String)modifyCustomColumnJObj.get((Object)"description");
            final Boolean isPII = (Boolean)modifyCustomColumnJObj.get((Object)"isPII");
            final String columnName = (String)modifyCustomColumnJObj.get((Object)"columnName");
            final ColumnDefinition columnDefinition = new ColumnDefinition();
            dataType = (dataType.equalsIgnoreCase("File") ? "CHAR" : dataType);
            columnDefinition.setTableName(tableName);
            columnDefinition.setColumnName(actualColumnName);
            columnDefinition.setDataType(dataType);
            columnDefinition.setMaxLength((int)maxSize);
            columnDefinition.setDisplayName(columnName);
            columnDefinition.setDynamic(true);
            if (defaultValue != null && !defaultValue.trim().equalsIgnoreCase("")) {
                columnDefinition.setDefaultValue((Object)defaultValue);
            }
            if (description != null && !description.trim().equalsIgnoreCase("")) {
                columnDefinition.setDescription(description);
            }
            updateColumnDetailsExtn(modifyCustomColumnJObj);
            invokeListenerForCustomColumnModification(modifyCustomColumnJObj);
            updateDetailsAndPIIForViews(tableName, actualColumnName, columnName, isPII, defaultValue);
            CustomColumnUtil.modifyCustomColumn(tableName, actualColumnName, columnDefinition);
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while modifying custom column:", e);
            throw e;
        }
    }
    
    public static void deleteCustomColumn(final JSONObject deleteCustomColumnJObj) throws Exception {
        try {
            CustomColumnHandler.logger.log(Level.INFO, "Inside deleteCustomColumn...");
            SyMUtil.getUserTransaction().begin();
            invokeListenerForCustomColumnDeletion(deleteCustomColumnJObj);
            deleteCustomColumnFromView(deleteCustomColumnJObj);
            deleteCustomColumnFromTable(deleteCustomColumnJObj);
            SyMUtil.getUserTransaction().commit();
        }
        catch (final Exception e) {
            SyMUtil.getUserTransaction().rollback();
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while deleting custom column:", e);
            throw e;
        }
    }
    
    public static void modifyCustomColumnValue(final JSONObject customColumnJObj) throws Exception {
        try {
            CustomColumnHandler.logger.log(Level.INFO, "Inside modifyCFValue...");
            final String columnName = (String)customColumnJObj.get((Object)"columnName");
            final String tableName = (String)customColumnJObj.get((Object)"tableName");
            final boolean isInputFormatAsFileType = Boolean.parseBoolean((String)customColumnJObj.get((Object)"IsInputFormatAsFileType"));
            final String extendedDataType = CustomColumnUtil.getExtendedDataTypeName(columnName, tableName);
            if (extendedDataType.equalsIgnoreCase("FILE") != isInputFormatAsFileType) {
                throw new Exception("Invalid ColumnName Present :: TableName - " + tableName + " ColumnName - " + columnName + " ExtendedDataTypeName - " + extendedDataType);
            }
            final String resourceName = (String)customColumnJObj.get((Object)"resourceName");
            final String[] selectedResources = (String[])customColumnJObj.get((Object)"selectedResources");
            String customColumnValue = (String)customColumnJObj.get((Object)"customColumnValue");
            if (customColumnValue != null) {
                customColumnValue = customColumnValue.trim();
            }
            if (tableName != null) {
                final DataObject dataObject = SyMUtil.getPersistence().get(tableName, (Criteria)null);
                for (int resource = 0; resource < selectedResources.length; ++resource) {
                    final Criteria criteria = new Criteria(new Column(tableName, resourceName), (Object)selectedResources[resource], 0);
                    Row customColumnRow = dataObject.getRow(tableName, criteria);
                    if (customColumnRow == null) {
                        customColumnRow = new Row(tableName);
                        customColumnRow.set(resourceName, (Object)new Long(selectedResources[resource]));
                        customColumnRow.set(columnName, (Object)customColumnValue);
                        dataObject.addRow(customColumnRow);
                    }
                    else {
                        customColumnRow.set(columnName, (Object)customColumnValue);
                        dataObject.updateRow(customColumnRow);
                    }
                }
                SyMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while modifying custom column value:", e);
            throw e;
        }
    }
    
    public static void modifyCustomColumnFile(final JSONObject customColumnJObj, final UploadedFileItem formFile) throws Exception {
        try {
            CustomColumnHandler.logger.log(Level.INFO, "Inside modifyCFValue...");
            final String filePath = (String)customColumnJObj.get((Object)"filePath");
            final String[] array;
            final String[] selectedResourcesArray = array = (String[])customColumnJObj.get((Object)"selectedResources");
            for (final String fileName : array) {
                storeFile(fileName, formFile, filePath);
            }
            modifyCustomColumnValue(customColumnJObj);
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while modifying custom file value:", e);
            throw e;
        }
    }
    
    private static void storeFile(final String fileName, final UploadedFileItem formFile, final String filePath) throws Exception {
        FileOutputStream fout = null;
        File saveFile = null;
        try {
            final InputStream fileInput = new FileInputStream(formFile.getUploadedFile());
            final byte[] file = new byte[fileInput.available()];
            fileInput.read(file);
            final File saveDir = new File(filePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            saveFile = new File(filePath + File.separator + fileName);
            fout = new FileOutputStream(saveFile);
            fout.write(file);
            fout.close();
        }
        catch (final FileNotFoundException ex) {
            CustomColumnHandler.logger.log(Level.WARNING, "Exception while storing the licence file under directory ", ex);
            throw ex;
        }
        catch (final IOException ex2) {
            CustomColumnHandler.logger.log(Level.WARNING, "Exception while storing the licence file under directory ", ex2);
            throw ex2;
        }
        finally {
            fout.close();
        }
    }
    
    public static void downloadFile(final JSONObject downloadFilesObject, final HttpServletResponse response) throws Exception {
        OutputStream os = null;
        InputStream is = null;
        try {
            final String fileName = (String)downloadFilesObject.get((Object)"fileName");
            final String filePath = (String)downloadFilesObject.get((Object)"filePath");
            if (fileName == null || filePath == null) {
                CustomColumnHandler.logger.info("Requested File Not Found in the Directory");
                throw new IOException("Requested File " + fileName + "Not Found");
            }
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            is = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
            int read = 0;
            final byte[] bytes = new byte[4096];
            os = (OutputStream)response.getOutputStream();
            while ((read = is.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
            os.flush();
        }
        catch (final Exception ex) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while downloading File {0}", ex);
        }
        finally {
            if (os != null) {
                os.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }
    
    public static DataObject getColumnDO(final String tableName, final String columnname) throws Exception {
        DataObject dataObject = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ColumnDetails"));
            Criteria columnCriteria = new Criteria(new Column("ColumnDetails", "COLUMN_NAME"), (Object)columnname, 0, false);
            final Criteria tableCriteria = new Criteria(new Column("TableDetails", "TABLE_NAME"), (Object)tableName, 0, false);
            columnCriteria = columnCriteria.and(tableCriteria);
            final Join roleJoin = new Join("ColumnDetails", "TableDetails", new String[] { "TABLE_ID" }, new String[] { "TABLE_ID" }, 2);
            selectQuery.addJoin(roleJoin);
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            selectQuery.setCriteria(columnCriteria);
            dataObject = SyMUtil.getPersistence().get(selectQuery);
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while checking isCOlumnAvailable", e);
            throw e;
        }
        return dataObject;
    }
    
    public static Long getColumnID(final String tableName, final String columnName) throws Exception {
        Long columnID = null;
        try {
            final DataObject dataObject = getColumnDO(tableName, columnName);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row columnRow = dataObject.getRow("ColumnDetails");
                columnID = (Long)columnRow.get("COLUMN_ID");
            }
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while checking isCOlumnAvailable", e);
            throw e;
        }
        return columnID;
    }
    
    public static boolean isColumnAvailable(final String tableName, final String columnname) {
        boolean isAvailable = false;
        try {
            final DataObject dataObject = getColumnDO(tableName, columnname);
            if (dataObject != null && !dataObject.isEmpty()) {
                isAvailable = true;
            }
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while checking isCOlumnAvailable", e);
        }
        return isAvailable;
    }
    
    public static boolean isDataTypeAvailable(final String dataType) {
        boolean isAvailable = false;
        try {
            final Criteria criteria = new Criteria(new Column("CustomDataType", "DATATYPE"), (Object)dataType, 0, false);
            final DataObject dataObject = SyMUtil.getPersistence().get("CustomDataType", criteria);
            if (!dataObject.isEmpty()) {
                isAvailable = true;
            }
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while checking data type available", e);
        }
        return isAvailable;
    }
    
    public static ColumnDefinition addCustomColumnInTable(final JSONObject addCustomColumnJObj) throws Exception {
        CustomColumnHandler.logger.log(Level.INFO, "Inside addCustomColumnInTable...");
        final ColumnDefinition columnDefinition = new ColumnDefinition();
        final String tableName = (String)addCustomColumnJObj.get((Object)"tableName");
        final String columnName = (String)addCustomColumnJObj.get((Object)"columnName");
        final String dataType = (String)addCustomColumnJObj.get((Object)"dataType");
        final Integer maxSize = (Integer)addCustomColumnJObj.get((Object)"maxSize");
        final String defaultValue = (String)addCustomColumnJObj.get((Object)"defaultValue");
        final String description = (String)addCustomColumnJObj.get((Object)"description");
        final SequenceGenerator seq = SequenceGeneratorRepository.getOrCreate(tableName, "BIGINT");
        final String actualColumnName = "COL" + seq.nextValue();
        addCustomColumnJObj.put((Object)"actualColumnName", (Object)actualColumnName);
        try {
            if (isColumnAvailable(tableName, actualColumnName)) {
                CustomColumnHandler.logger.log(Level.SEVERE, "Exception Occurred :column exist");
                throw new Exception("Column already Exist");
            }
            columnDefinition.setTableName(tableName);
            columnDefinition.setColumnName(actualColumnName);
            columnDefinition.setDataType(dataType);
            columnDefinition.setMaxLength((int)maxSize);
            columnDefinition.setDisplayName(columnName);
            if (defaultValue != null && !defaultValue.trim().equalsIgnoreCase("")) {
                columnDefinition.setDefaultValue((Object)defaultValue);
            }
            if (description != null && !description.trim().equalsIgnoreCase("")) {
                columnDefinition.setDescription(description);
            }
            CustomColumnUtil.addCustomColumnInTable(columnDefinition, tableName);
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while adding column in the table", e);
            throw e;
        }
        return columnDefinition;
    }
    
    public static void addCustomColumnInView(final ColumnDefinition columnDefinition, final JSONObject addCustomColumnJObj) throws Exception {
        try {
            CustomColumnHandler.logger.log(Level.INFO, "Inside addCustomColumnInView...");
            final String tableName = (String)addCustomColumnJObj.get((Object)"tableName");
            final String columnName = (String)addCustomColumnJObj.get((Object)"columnName");
            final String actualColumnName = (String)addCustomColumnJObj.get((Object)"actualColumnName");
            final String tableAlias = (String)addCustomColumnJObj.get((Object)"tableAlias");
            final Boolean sortEnabled = (Boolean)addCustomColumnJObj.get((Object)"sortEnabled");
            final Boolean searchEnabled = (Boolean)addCustomColumnJObj.get((Object)"searchEnabled");
            final Integer trimLength = (Integer)addCustomColumnJObj.get((Object)"trimLength");
            final String trimMsgLink = (String)addCustomColumnJObj.get((Object)"trimMsgLink");
            final String defaultText = (String)addCustomColumnJObj.get((Object)"defaultText");
            final String actionName = (String)addCustomColumnJObj.get((Object)"actionName");
            final String transformer = (String)addCustomColumnJObj.get((Object)"transformer");
            final String emberProperties = (String)addCustomColumnJObj.get((Object)"emberProperties");
            final Boolean isPII = (Boolean)addCustomColumnJObj.get((Object)"isPII");
            final JSONObject viewArray = (JSONObject)addCustomColumnJObj.get((Object)"viewName");
            for (final String viewName : viewArray.keySet()) {
                final Boolean visibility = (Boolean)viewArray.get((Object)viewName);
                final Row columnConfigRow = new Row("ACColumnConfiguration");
                columnConfigRow.set("COLUMNALIAS", (Object)(tableName + "." + actualColumnName));
                columnConfigRow.set("TABLEALIAS", (Object)tableName);
                columnConfigRow.set("DISPLAYNAME", (Object)columnName);
                columnConfigRow.set("SORTENABLED", (Object)sortEnabled);
                columnConfigRow.set("SEARCHENABLED", (Object)searchEnabled);
                columnConfigRow.set("TRIM_LENGTH", (Object)trimLength);
                columnConfigRow.set("TRIM_MSG_LINK", (Object)trimMsgLink);
                columnConfigRow.set("VISIBLE", (Object)visibility);
                columnConfigRow.set("DEFAULT_TEXT", (Object)defaultText);
                columnConfigRow.set("ACTIONNAME", (Object)actionName);
                columnConfigRow.set("TRANSFORMER", (Object)transformer);
                final Row emberColumnConfigRow = new Row("ACColumnConfiguration");
                emberColumnConfigRow.set("COLUMNALIAS", (Object)(tableName + "." + actualColumnName));
                emberColumnConfigRow.set("PROPERTIES", (Object)emberProperties);
                CustomColumnUtil.addCustomColumnInView(viewName, columnDefinition, columnConfigRow, emberColumnConfigRow);
                if (isPII) {
                    CustomColumnUtil.enablePIIMask(tableName, actualColumnName, viewName);
                }
            }
            clearCacheAndPIIForAssociatedViews(tableName, columnName, isPII);
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while adding column in view", e);
            throw e;
        }
    }
    
    public static void updateColumnDetailsExtn(final JSONObject addCustomColumnJObj) throws Exception {
        try {
            CustomColumnHandler.logger.log(Level.INFO, "Inside updateColumnDetailsExtn...");
            final String tableName = (String)addCustomColumnJObj.get((Object)"tableName");
            final String actualColumnName = (String)addCustomColumnJObj.get((Object)"actualColumnName");
            final String ExtendedDataTypeName = (String)addCustomColumnJObj.get((Object)"ExtendedDataTypeName");
            final Long userID = (Long)addCustomColumnJObj.get((Object)"userID");
            final Boolean isPII = (Boolean)addCustomColumnJObj.get((Object)"isPII");
            final Long columnID = getColumnID(tableName, actualColumnName);
            final Criteria columnIDcrit = new Criteria(Column.getColumn("ColumnDetailsExtn", "COLUMN_ID"), (Object)columnID, 0);
            final DataObject columnDetailsObj = SyMUtil.getPersistence().get("ColumnDetailsExtn", columnIDcrit);
            if (columnDetailsObj == null || columnDetailsObj.isEmpty()) {
                final WritableDataObject dataObject = new WritableDataObject();
                final Row columnDetailsExtnRow = new Row("ColumnDetailsExtn");
                columnDetailsExtnRow.set("COLUMN_ID", (Object)columnID);
                columnDetailsExtnRow.set("EXTENDED_DATA_TYPE_NAME", (Object)ExtendedDataTypeName);
                columnDetailsExtnRow.set("ADDED_BY", (Object)userID);
                columnDetailsExtnRow.set("ADDED_TIME", (Object)System.currentTimeMillis());
                columnDetailsExtnRow.set("IS_PII", (Object)isPII);
                dataObject.addRow(columnDetailsExtnRow);
                SyMUtil.getPersistence().add((DataObject)dataObject);
            }
            else {
                final Row columnDetailsExtnRow2 = columnDetailsObj.getRow("ColumnDetailsExtn");
                columnDetailsExtnRow2.set("MODIFIED_BY", (Object)userID);
                columnDetailsExtnRow2.set("MODIFIED_TIME", (Object)System.currentTimeMillis());
                columnDetailsExtnRow2.set("IS_PII", (Object)isPII);
                columnDetailsObj.updateRow(columnDetailsExtnRow2);
                SyMUtil.getPersistence().update(columnDetailsObj);
            }
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while adding data type", e);
            throw e;
        }
    }
    
    public static void addCustomDataType(final JSONObject customDataTypeJObj) throws Exception {
        try {
            CustomColumnHandler.logger.log(Level.INFO, "Inside addCustomDataType...");
            final String dataTypeName = (String)customDataTypeJObj.get((Object)"dataTypeName");
            if (isDataTypeAvailable(dataTypeName)) {
                CustomColumnHandler.logger.log(Level.SEVERE, "Exception Occurred  : data Type exist");
                throw new Exception("data Type already Exist");
            }
            final String baseType = (String)customDataTypeJObj.get((Object)"baseType");
            final String dataTypeSize = (String)customDataTypeJObj.get((Object)"dataTypeSize");
            final String dataTypeDefaultValue = (String)customDataTypeJObj.get((Object)"dataTypeDefaultValue");
            final AllowedValues allowedValues = new AllowedValues();
            final Object[] dataTypeAllowedValue = (Object[])customDataTypeJObj.get((Object)"dataTypeAllowedValue");
            for (int i = 0; i < dataTypeAllowedValue.length; ++i) {
                allowedValues.addValue(dataTypeAllowedValue[i]);
            }
            final DataTypeDefinition dataTypeDefinition = new DataTypeDefinition(dataTypeName, baseType, Integer.parseInt(dataTypeSize), 0, allowedValues, (Object)dataTypeDefaultValue);
            CustomColumnUtil.addCustomDataType(dataTypeDefinition);
            final WritableDataObject dataObject = new WritableDataObject();
            final Row customDataTypeRow = new Row("CustomDataType");
            customDataTypeRow.set("DATATYPE", (Object)dataTypeName);
            dataObject.addRow(customDataTypeRow);
            SyMUtil.getPersistence().add((DataObject)dataObject);
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while adding data type", e);
            throw e;
        }
    }
    
    public static void deleteCustomColumnFromView(final JSONObject deleteCustomColumnJObj) throws Exception {
        try {
            CustomColumnHandler.logger.log(Level.INFO, "Inside deleteCustomColumnFromView...");
            final String tableName = (String)deleteCustomColumnJObj.get((Object)"tableName");
            final String actualColumnName = (String)deleteCustomColumnJObj.get((Object)"actualColumnName");
            final JSONArray viewList = (JSONArray)deleteCustomColumnJObj.get((Object)"viewName");
            for (int i = 0; i < viewList.size(); ++i) {
                final String viewName = (String)viewList.get(i);
                CustomColumnUtil.deleteCustomColumnFromView(viewName, tableName, actualColumnName);
            }
            CustomColumnUtil.deletePIIRedactConfig(tableName + "." + actualColumnName);
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while deleting custom column from view", e);
            throw e;
        }
    }
    
    public static void deleteCustomColumnFromTable(final JSONObject deleteCustomColumnJObj) throws Exception {
        try {
            CustomColumnHandler.logger.log(Level.INFO, "Inside deleteCustomColumnFromTable...");
            final String tableName = (String)deleteCustomColumnJObj.get((Object)"tableName");
            final String actualColumnName = (String)deleteCustomColumnJObj.get((Object)"actualColumnName");
            CustomColumnUtil.deleteCustomColumnFromTable(tableName, actualColumnName);
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while deleting custom column from table", e);
            throw e;
        }
    }
    
    public static JSONArray getDataTypes(final String tableName, final String columnName) {
        final JSONArray dataTypeArr = new JSONArray();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table(tableName));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows(tableName);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final String dataType = (String)row.get(columnName);
                    dataTypeArr.add((Object)dataType);
                }
            }
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Error occured in  getdataTypes  method", e);
        }
        return dataTypeArr;
    }
    
    private static ArrayList<Long> getIdsFromJsonArray(final JSONArray jsonArray) throws JSONException {
        final ArrayList<Long> arrayList = new ArrayList<Long>();
        try {
            for (int size = jsonArray.size(), i = 0; i < size; ++i) {
                final Long oldList = (Long)jsonArray.get(i);
                arrayList.add(oldList);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
        return arrayList;
    }
    
    public static Row setValuesInExtnTableRow(final Row row, final Hashtable resourceDetails) {
        for (final Object key : resourceDetails.keySet()) {
            try {
                row.set((String)key, resourceDetails.get(key));
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return row;
    }
    
    public static List getColumnsOfTable(final String tableName) {
        final List roleList = new ArrayList();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("ColumnDetails"));
            final Join join = new Join("ColumnDetails", "TableDetails", new String[] { "TABLE_ID" }, new String[] { "TABLE_ID" }, 2);
            sq.addJoin(join);
            sq.addSelectColumn(new Column((String)null, "*"));
            Criteria c = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), (Object)tableName, 0);
            c = c.and(new Criteria(Column.getColumn("ColumnDetails", "IS_DYNAMIC"), (Object)true, 0));
            sq.setCriteria(c);
            final DataObject dataObj = SyMUtil.getPersistence().get(sq);
            if (dataObj != null) {
                final Iterator ite = dataObj.getRows("ColumnDetails");
                while (ite.hasNext()) {
                    final Row r = ite.next();
                    roleList.add(r.get("COLUMN_NAME"));
                }
            }
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.WARNING, "Exception while get  the RoleList :", e);
        }
        return roleList;
    }
    
    public static void addCustomColumnListener(final CustomColumnListener listener) {
        CustomColumnHandler.customColumnListenerList.add(listener);
    }
    
    public static void removeCustomColumnListener(final CustomColumnListener listener) {
        CustomColumnHandler.customColumnListenerList.remove(listener);
    }
    
    private static void invokeListenerForCustomColumnAddition(final JSONObject customColumnObj) {
        for (int i = 0; i < CustomColumnHandler.customColumnListenerList.size(); ++i) {
            final CustomColumnListener customColumnListener = CustomColumnHandler.customColumnListenerList.get(i);
            customColumnListener.customColumnAdded(customColumnObj);
        }
    }
    
    private static void invokeListenerForCustomColumnModification(final JSONObject customColumnObj) {
        for (int i = 0; i < CustomColumnHandler.customColumnListenerList.size(); ++i) {
            final CustomColumnListener customColumnListener = CustomColumnHandler.customColumnListenerList.get(i);
            customColumnListener.customColumnModified(customColumnObj);
        }
    }
    
    private static void invokeListenerForCustomColumnDeletion(final JSONObject customColumnObj) {
        for (int i = 0; i < CustomColumnHandler.customColumnListenerList.size(); ++i) {
            final CustomColumnListener customColumnListener = CustomColumnHandler.customColumnListenerList.get(i);
            customColumnListener.customColumnDeleted(customColumnObj);
        }
    }
    
    private static void updateDetailsAndPIIForViews(final String tableName, final String columnName, final String displayName, final Boolean isPII, final String defaultText) throws Exception {
        try {
            UpdateQuery displayNameQuery = (UpdateQuery)new UpdateQueryImpl("ACColumnConfiguration");
            Criteria columnAliasCriteria = new Criteria(new Column("ACColumnConfiguration", "COLUMNALIAS"), (Object)(tableName + "." + columnName), 0);
            displayNameQuery.setCriteria(columnAliasCriteria);
            displayNameQuery.setUpdateColumn("DISPLAYNAME", (Object)displayName);
            displayNameQuery.setUpdateColumn("DEFAULT_TEXT", (Object)defaultText);
            SyMUtil.getPersistence().update(displayNameQuery);
            displayNameQuery = (UpdateQuery)new UpdateQueryImpl("ACTableColumns");
            columnAliasCriteria = new Criteria(new Column("ACTableColumns", "COLUMNALIAS"), (Object)(tableName + "." + columnName), 0);
            displayNameQuery.setCriteria(columnAliasCriteria);
            displayNameQuery.setUpdateColumn("DISPLAYNAME", (Object)displayName);
            SyMUtil.getPersistence().update(displayNameQuery);
            clearCacheAndPIIForAssociatedViews(tableName, columnName, isPII);
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while updating displayName for views with custom Column", e);
            throw e;
        }
    }
    
    private static void clearCacheAndPIIForAssociatedViews(final String tableName, final String columnName, final Boolean isPII) {
        try {
            final JSONArray viewsToClear = CustomColumnUtil.getAssociatedViewsForBaseEntity(tableName);
            if (viewsToClear != null) {
                for (int i = 0; i < viewsToClear.size(); ++i) {
                    final String viewName = (String)viewsToClear.get(i);
                    SyMUtil.getInstance().clearCacheForView(viewName);
                    if (isPII) {
                        CustomColumnUtil.enablePIIMask(tableName, columnName, viewName);
                    }
                    else {
                        CustomColumnUtil.deletePIIRedactConfig(tableName + "." + columnName);
                    }
                    final DataObject personalisedViewDO = SyMUtil.getPersonalisedViewsForViewName(viewName);
                    if (personalisedViewDO != null) {
                        final Iterator iterator = personalisedViewDO.getRows("PersonalizedViewMap");
                        while (iterator.hasNext()) {
                            final Row personalisedView = iterator.next();
                            final Long personalisedViewID = (Long)personalisedView.get("PERSVIEWNAME");
                            final Criteria viewIDCriteria = new Criteria(new Column("ViewConfiguration", "VIEWNAME_NO"), (Object)personalisedViewID, 0);
                            final Row viewConfigurationRow = personalisedViewDO.getRow("ViewConfiguration", viewIDCriteria);
                            if (viewConfigurationRow != null) {
                                final String personalisedViewName = (String)viewConfigurationRow.get("VIEWNAME");
                                SyMUtil.getInstance().clearCacheForView(personalisedViewName);
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while clearing cached Views", e);
        }
    }
    
    public static Boolean isDisplayNameAvailable(final String tableName, final String columnName) {
        Boolean displayNameAvailable = false;
        try {
            final Criteria tableCrit = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), (Object)tableName, 0, false);
            final Criteria displayCrit = new Criteria(Column.getColumn("ColumnDetails", "DISPLAY_NAME"), (Object)columnName, 0, false);
            final DataObject columnDO = SyMUtil.getColumnDetailsForColumn(tableCrit.and(displayCrit));
            if (columnDO != null && !columnDO.isEmpty()) {
                displayNameAvailable = true;
            }
        }
        catch (final Exception e) {
            CustomColumnHandler.logger.log(Level.SEVERE, "Exception while checking Display Name Availability ", e);
        }
        return displayNameAvailable;
    }
    
    public static String getCustomColumns(final String customColumnBaseTableName) {
        final String columnList = getColumnsOfTable(customColumnBaseTableName).stream().map(c -> s + "." + c + " AS \"" + s + "." + c + "\"").collect(Collectors.toList()).toString();
        return columnList.substring(1, columnList.length() - 1);
    }
    
    static {
        CustomColumnHandler.logger = Logger.getLogger(CustomColumnHandler.class.getName());
        CustomColumnHandler.customColumnListenerList = new ArrayList<CustomColumnListener>();
    }
}
