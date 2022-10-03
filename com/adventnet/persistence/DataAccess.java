package com.adventnet.persistence;

import java.util.Hashtable;
import java.io.InputStream;
import java.io.FileInputStream;
import com.zoho.conf.Configuration;
import java.util.Properties;
import java.net.URISyntaxException;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import com.adventnet.persistence.template.TemplateUtil;
import com.adventnet.db.persistence.metadata.MetaDataPreChangeEvent;
import com.adventnet.db.persistence.metadata.MetaDataChangeEvent;
import com.adventnet.db.persistence.metadata.MetaDataChangeListener;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.util.TemplateMetaHandler;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.ds.query.AlterTableQuery;
import javax.transaction.Transaction;
import java.util.Locale;
import com.adventnet.db.persistence.metadata.DataDictionary;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.ds.query.Join;
import com.adventnet.db.persistence.SequenceGenerator;
import com.adventnet.persistence.internal.SequenceGeneratorRepository;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.persistence.internal.GetUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.db.persistence.metadata.AllowedValues;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.Function;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.Column;
import com.zoho.conf.AppResources;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import com.adventnet.ds.query.Criteria;
import java.util.Stack;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import java.sql.PreparedStatement;
import java.sql.BatchUpdateException;
import com.adventnet.db.archive.TableArchiverUtil;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.adapter.DTTransformationUtil;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedHashMap;
import com.adventnet.db.api.RelationalAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.util.Iterator;
import java.sql.SQLException;
import com.adventnet.persistence.internal.Operation;
import java.util.logging.Level;
import java.util.List;
import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Map;
import javax.transaction.TransactionManager;
import java.util.logging.Logger;

public class DataAccess
{
    private static final Logger OUT;
    private static final String CLASS_NAME;
    private static TransactionManager tm;
    private static String transactionMgrJNDIName;
    private static Map<String, HashSet<String>> convertableDataTypes;
    private static final String TABLE_NAME = "TABLE_NAME";
    private static DataSource dataSource;
    private static boolean readOnlyMode;
    private static List listenerList;
    private static final String ENABLE_CBH = "enable.callbackhandlers";
    private static boolean isCallbackHandlersEnabled;
    private static ThreadLocal<Boolean> invokeCBH;
    private static Map<String, MigrationCallbackHandler> tbNameVsCBH;
    
    private DataAccess() {
    }
    
    public static void setDataSource(final DataSource ds) {
        if (DataAccess.dataSource != null) {
            final Exception exc = new Exception("Stack Trace");
            DataAccess.OUT.log(Level.WARNING, "Somebody is trying to change the data source. Ignoring.", exc);
        }
        else {
            DataAccess.dataSource = ds;
            if (PersistenceInitializer.onSAS()) {
                initializeMigrationCallbackHandlerMap();
            }
        }
    }
    
    public static void setTransactionManager(final TransactionManager transMgr) {
        if (DataAccess.tm != null) {
            final Exception exc = new Exception("Transaction Manager is already set");
            DataAccess.OUT.log(Level.WARNING, "Somebody is trying to change the Transaction Manager. Ignoring.", exc);
        }
        else {
            DataAccess.tm = transMgr;
        }
    }
    
    public static TransactionManager getTransactionManager() {
        return DataAccess.tm;
    }
    
    public static DataObject constructDataObject() throws DataAccessException {
        final DataObject dob = new WritableDataObject();
        return dob;
    }
    
    public static DataObject add(final DataObject dataObject) throws DataAccessException {
        return add(dataObject, true);
    }
    
    static DataObject add(final DataObject dataObject, final boolean checkSystem) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "add", dataObject);
        if (!dataObject.isValidated()) {
            dataObject.validate();
        }
        if (PersistenceInitializer.onSAS()) {
            invokeCallbackHandlersFor(dataObject);
        }
        final List tableNames = dataObject.getTableNames();
        if (tableNames == null || tableNames.isEmpty()) {
            DataAccess.OUT.exiting(DataAccess.CLASS_NAME, "add", "No rows found for insertion");
            return dataObject;
        }
        if (checkSystem) {
            for (final String tableName : tableNames) {
                checkForSystemTable(tableName);
            }
        }
        final List sortedTableNames = PersistenceUtil.sortTables(tableNames);
        Connection conn = null;
        try {
            conn = getRelationalAPI().getConnection();
            final int size = sortedTableNames.size();
            PersistenceUtil.handlePreExec(sortedTableNames, conn, dataObject);
            for (int i = 0; i < size; ++i) {
                final String tableName2 = sortedTableNames.get(i);
                Iterator iterator = dataObject.getRows(tableName2);
                try {
                    while (iterator.hasNext()) {
                        final Row thisRow = iterator.next();
                        fillGenValues(thisRow);
                        Operation.addRow(1, thisRow);
                        dataObject.updateRow(thisRow);
                    }
                    iterator = dataObject.getRows(tableName2);
                    insertRows(tableName2, iterator, conn);
                }
                catch (final DataAccessException dae) {
                    final int[] err = dae.getUpdateCounts();
                    if (err != null) {
                        dae.setErrRows(dataObject.getRows(tableName2));
                    }
                    DataAccess.OUT.log(Level.FINE, "\nException occured while inserting the following row(s) in the table [" + tableName2 + "]: \n\n" + dae.getErrRowsAsString() + "\n\nMessage : " + dae.getMessage() + "\n\nError code : " + dae.getErrorCode() + "\n\n");
                    throw dae;
                }
            }
        }
        catch (final SQLException sqle) {
            DataAccess.OUT.log(Level.WARNING, "SQL Exception while fetching connection ", sqle);
            throw createDataAccessException(sqle);
        }
        finally {
            try {
                if (conn != null) {
                    PersistenceUtil.handlePostExec(sortedTableNames, conn, dataObject);
                }
            }
            finally {
                safeClose(conn);
            }
        }
        clearAndStartRecording(dataObject);
        DataAccess.OUT.exiting(DataAccess.CLASS_NAME, "add", dataObject);
        return dataObject;
    }
    
    private static void processInsertRows(final List sortedTableList, final HashMap tableVsActionInfo, final Connection conn, final DataObject writDataObject) throws DataAccessException {
        final int size = sortedTableList.size();
        final RelationalAPI relAPI = getRelationalAPI();
        PersistenceUtil.handlePreExec(sortedTableList, conn, writDataObject);
        try {
            for (int i = 0; i < size; ++i) {
                final String tableName = sortedTableList.get(i);
                final ArrayList insertActionsList = tableVsActionInfo.get(tableName);
                final int insertListSize = insertActionsList.size();
                if (insertListSize > 0) {
                    final Iterator itr = insertActionsList.iterator();
                    final ArrayList rowList = new ArrayList(insertListSize);
                    while (itr.hasNext()) {
                        final ActionInfo insertActInfo = itr.next();
                        final Row thisRow = insertActInfo.getValue();
                        fillGenValues(thisRow);
                        Operation.addRow(1, thisRow);
                        rowList.add(insertActInfo.getValue());
                    }
                    try {
                        insertRows(tableName, rowList.iterator(), conn);
                    }
                    catch (final DataAccessException dae) {
                        final int[] err = dae.getUpdateCounts();
                        if (err != null) {
                            dae.setErrRows(rowList.iterator());
                        }
                        DataAccess.OUT.log(Level.FINE, "\nException occured while inserting the following row(s) in the table [" + tableName + "]: \n\n" + dae.getErrRowsAsString() + "\n\nMessage : " + dae.getMessage() + "\n\nError code : " + dae.getErrorCode() + "\n\n");
                        throw dae;
                    }
                }
            }
        }
        finally {
            PersistenceUtil.handlePostExec(sortedTableList, conn, writDataObject);
        }
    }
    
    private static void processUpdateRows(final List sortedTableList, final HashMap tableVsActionInfo, final Connection conn, final DataObject writDataObject) throws DataAccessException, MetaDataException, QueryConstructionException {
        final int size = sortedTableList.size();
        final RelationalAPI relAPI = getRelationalAPI();
        PersistenceUtil.handlePreExec(sortedTableList, conn, writDataObject);
        final Map<String, Object> dyValues = new LinkedHashMap<String, Object>();
        final Map<String, Integer> dyColVsSQLTypes = new LinkedHashMap<String, Integer>();
        try {
            for (int k = 0; k < size; ++k) {
                final ArrayList actionInfosList = tableVsActionInfo.get(sortedTableList.get(k));
                final int tabActSize = actionInfosList.size();
                int[] changedColumnIndices = null;
                PreparedStatement ps = null;
                final List rows = new ArrayList(tabActSize);
                try {
                    for (int i = 0; i < tabActSize; ++i) {
                        final ActionInfo info = actionInfosList.get(i);
                        final Row thisRow = info.getValue();
                        rows.add(thisRow);
                        if (changedColumnIndices == null) {
                            ps = relAPI.createUpdateStatement(thisRow.getOriginalTableName(), thisRow.getChangedColumnIndex(), conn);
                            if (ps == null) {
                                continue;
                            }
                        }
                        else if (!Arrays.equals(changedColumnIndices, thisRow.getChangedColumnIndex())) {
                            try {
                                getRelationalAPI().executeBatch(ps);
                            }
                            catch (final SQLException sqle) {
                                DataAccess.OUT.log(Level.WARNING, "SQLException while updating the Row {0}", thisRow);
                                throw createDataAccessException(sqle.getMessage(), sqle, thisRow.getTableName());
                            }
                            finally {
                                safeClose(ps);
                            }
                            ps = relAPI.createUpdateStatement(thisRow.getOriginalTableName(), thisRow.getChangedColumnIndex(), conn);
                            if (ps == null) {
                                continue;
                            }
                        }
                        changedColumnIndices = thisRow.getChangedColumnIndex();
                        final int dcSize = changedColumnIndices.length;
                        int actIndexForKey = 0;
                        int dyIndex = -1;
                        final String dcType = MetaDataUtil.getTableDefinitionByName(thisRow.getOriginalTableName()).getDynamicColumnType();
                        dyValues.clear();
                        dyColVsSQLTypes.clear();
                        for (int j = 0; j < dcSize; ++j) {
                            final int changedIndex = changedColumnIndices[j];
                            Object value = thisRow.get(changedIndex);
                            if (value instanceof UniqueValueHolder) {
                                value = generateValue(thisRow.getTableName(), (UniqueValueHolder)value);
                                thisRow.setBlindly(changedIndex, value, false);
                            }
                            final String datatype = thisRow.getColumnType(changedIndex);
                            int javaSQLType = thisRow.getSQLType(changedIndex);
                            final String database = relAPI.getDBAdapter().getDBType();
                            if (DataTypeUtil.isUDT(datatype)) {
                                final DataTypeDefinition dataDef = DataTypeManager.getDataTypeDefinition(datatype);
                                try {
                                    value = DTTransformationUtil.transform(thisRow.getTableName(), thisRow.getColumns().get(changedIndex - 1), value, datatype, database);
                                }
                                catch (final Exception e) {
                                    throw new SQLException("Exception while transforming data." + e);
                                }
                                if (dataDef.getDTAdapter(database) == null) {
                                    throw new IllegalArgumentException("DTAdapter not defined for type :: " + datatype);
                                }
                                javaSQLType = dataDef.getDTAdapter(database).getJavaSQLType();
                            }
                            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(thisRow.getOriginalTableName());
                            if (td.getColumnList().get(changedIndex - 1).isDynamic()) {
                                if (dyValues.size() == 0) {
                                    dyIndex = j;
                                }
                                dyValues.put(td.getColumnList().get(changedIndex - 1).getColumnName(), value);
                                dyColVsSQLTypes.put(td.getColumnList().get(changedIndex - 1).getColumnName(), javaSQLType);
                            }
                            else {
                                relAPI.setValue(ps, j + 1, javaSQLType, value);
                                ++actIndexForKey;
                            }
                        }
                        if (dyValues != null && dyValues.size() != 0) {
                            relAPI.setValue(ps, dyIndex + 1, dyColVsSQLTypes, dyValues, dcType);
                            actIndexForKey = ps.getParameterMetaData().getParameterCount() - thisRow.getKeyIndices().length;
                        }
                        final int[] keyIndices = thisRow.getKeyIndices();
                        for (int l = 0; l < keyIndices.length; ++l) {
                            final int changedIndex2 = keyIndices[l];
                            Object value2 = thisRow.getOriginalValue(changedIndex2);
                            if (value2 == null || value2 instanceof UniqueValueHolder) {
                                value2 = thisRow.get(changedIndex2);
                            }
                            final String datatype2 = thisRow.getColumnType(changedIndex2);
                            int javaSQLType2 = thisRow.getSQLType(changedIndex2);
                            final String database2 = relAPI.getDBAdapter().getDBType();
                            final DataTypeDefinition dataDef2 = DataTypeManager.getDataTypeDefinition(datatype2);
                            if (dataDef2 != null && dataDef2.getMeta() != null) {
                                try {
                                    value2 = DTTransformationUtil.transform(thisRow.getTableName(), thisRow.getColumns().get(changedIndex2 - 1), value2, datatype2, database2);
                                }
                                catch (final Exception e2) {
                                    throw new SQLException("Exception while transforming data." + e2);
                                }
                                if (dataDef2.getDTAdapter(database2) == null) {
                                    throw new IllegalArgumentException("DTAdapter not defined for type :: " + datatype2);
                                }
                                javaSQLType2 = dataDef2.getDTAdapter(database2).getJavaSQLType();
                            }
                            relAPI.setValue(ps, l + actIndexForKey + 1, javaSQLType2, value2);
                        }
                        ps.addBatch();
                        Operation.addRow(2, thisRow);
                    }
                    if (ps != null) {
                        final int[] updatedRowCount = getRelationalAPI().executeBatch(ps);
                        if (!DataAccess.readOnlyMode) {
                            for (int m = 0; m < updatedRowCount.length; ++m) {
                                try {
                                    if (updatedRowCount[m] == 0 && MetaDataUtil.getTableDefinitionByName(rows.get(m).getOriginalTableName()).isDirtyWriteCheckColumnsDefined()) {
                                        throw new DataAccessException("Batch update returns '0' as no. of row updated, it should not be happened for this dirty-write protected table: [ " + rows.get(m).getOriginalTableName() + " ]");
                                    }
                                }
                                catch (final MetaDataException mde) {
                                    throw new DataAccessException("Invalid tableName specified " + mde);
                                }
                            }
                        }
                    }
                }
                catch (final BatchUpdateException bae) {
                    final String tableName = sortedTableList.get(k);
                    try {
                        if (TableArchiverUtil.isArchiveEnabled()) {
                            getRelationalAPI().getArchiveAdapter().restoreUnArchivedInvisibleTable(tableName, conn, bae);
                        }
                    }
                    catch (final Exception e3) {
                        DataAccess.OUT.warning("Exception while handling archive(restoreUnArchivedInvisibleTable) inside catch!");
                        e3.printStackTrace();
                    }
                    final DataAccessException dae = createDataAccessException(bae.getMessage(), bae, tableName);
                    dae.setUpdateCounts(bae.getUpdateCounts());
                    dae.setErrRows(rows.iterator());
                    DataAccess.OUT.log(Level.WARNING, "\nException occured while updating the following row(s) in the table [" + tableName + "]: \n\n" + dae.getErrRowsAsString() + "\n\nMessage : " + dae.getMessage() + "\n\nError code : " + dae.getErrorCode() + "\n\n");
                    throw dae;
                }
                catch (final SQLException sqle2) {
                    DataAccess.OUT.log(Level.WARNING, "SQLException while updating the Rows of table {0}", sortedTableList.get(k));
                    throw createDataAccessException(sqle2.getMessage(), sqle2, sortedTableList.get(k));
                }
                finally {
                    safeClose(ps);
                }
            }
        }
        finally {
            PersistenceUtil.handlePostExec(sortedTableList, conn, writDataObject);
        }
    }
    
    private static void processDeleteRows(final List sortedTableList, final HashMap tableVsActionInfo, final Connection conn, final boolean checkSystem) throws DataAccessException {
        final int size = sortedTableList.size();
        final RelationalAPI relAPI = getRelationalAPI();
        for (int i = size - 1; i >= 0; --i) {
            final ArrayList deleteActionInfo = tableVsActionInfo.get(sortedTableList.get(i));
            for (int delListSize = deleteActionInfo.size(), k = 0; k < delListSize; ++k) {
                final ActionInfo info = deleteActionInfo.get(k);
                final Row thisRow = info.getValue();
                final Criteria criteria = QueryConstructor.formCriteria(thisRow);
                DeleteUtil.setBdfkStack(new Stack());
                try {
                    DeleteUtil.delete(thisRow.getOriginalTableName(), criteria, checkSystem);
                }
                catch (final DataAccessException dae) {
                    DataAccess.OUT.log(Level.WARNING, "Exception occured while deleting the Row {0}", thisRow);
                    throw dae;
                }
            }
        }
    }
    
    private static void checkForSystemTable(final WritableDataObject dataObject) throws DataAccessException {
        final Map map = dataObject.getOperationTables();
        final Collection values = map.values();
        for (final WritableDataObject.OperationTables op : values) {
            final HashMap tableVsActionInfo = op.getActions();
            for (final String tableName : tableVsActionInfo.keySet()) {
                checkForSystemTable(tableName, dataObject);
            }
        }
    }
    
    private static void checkForSystemTable(final String tableName, final WritableDataObject data) throws DataAccessException {
        TableDefinition tabDef = null;
        try {
            tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
            if (tabDef == null) {
                final String orgTableName = data.getOrigTableName(tableName);
                tabDef = MetaDataUtil.getTableDefinitionByName(orgTableName);
            }
            if (tabDef == null) {
                DataAccess.OUT.log(Level.WARNING, "Operation exists for an unknown table :: {0}", tableName);
            }
            else if (tabDef.isSystem()) {
                throw new DataAccessException("Data cannot be added/upated/deleted in system Table : " + tableName);
            }
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Invalid tableName specified " + tableName, mde);
        }
    }
    
    private static void checkForSystemTable(final String tableName) throws DataAccessException {
        TableDefinition tabDef = null;
        try {
            tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Invalid tableName specified " + tableName, mde);
        }
        if (tabDef == null) {
            DataAccess.OUT.log(Level.WARNING, "Operation exists for an unknown table :: {0}", tableName);
        }
        else if (tabDef.isSystem()) {
            throw new DataAccessException("Data cannot be added/upated/deleted in system Table : " + tableName);
        }
    }
    
    private static void checkForSystemTable(final SelectQuery query) throws DataAccessException {
        final List tableList = query.getTableList();
        for (final Table table : tableList) {
            final String tableName = table.getTableName();
            try {
                checkForSystemTable(tableName);
            }
            catch (final Exception e) {
                throw new DataAccessException("System table cannot be fetched : " + tableName);
            }
        }
    }
    
    public static DataObject update(final DataObject dataObject) throws DataAccessException {
        return update(dataObject, true);
    }
    
    static DataObject update(final DataObject dataObject, final boolean checkSystem) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "update", dataObject);
        if (!dataObject.isValidated()) {
            dataObject.validate();
        }
        Connection conn = null;
        try {
            final WritableDataObject writDataObject = (WritableDataObject)dataObject;
            if (checkSystem) {
                checkForSystemTable(writDataObject);
            }
            conn = getRelationalAPI().getConnection();
            if (PersistenceInitializer.onSAS()) {
                invokeCallbackHandlersFor(dataObject);
            }
            List involvedTables = writDataObject.getModifiedTablesFor("insert");
            if (involvedTables != null) {
                final List insertSortedTables = PersistenceUtil.sortTables(writDataObject, involvedTables);
                processInsertRows(insertSortedTables, writDataObject.getActionsFor("insert"), conn, writDataObject);
            }
            involvedTables = writDataObject.getModifiedTablesFor("update");
            if (involvedTables != null) {
                final List updateSortedTables = PersistenceUtil.sortTables(writDataObject, involvedTables);
                processUpdateRows(updateSortedTables, writDataObject.getActionsFor("update"), conn, writDataObject);
            }
            involvedTables = writDataObject.getModifiedTablesFor("delete");
            if (involvedTables != null) {
                final List deleteSortedTables = PersistenceUtil.sortTables(writDataObject, involvedTables);
                processDeleteRows(deleteSortedTables, writDataObject.getActionsFor("delete"), conn, false);
            }
        }
        catch (final SQLException sqle) {
            DataAccess.OUT.log(Level.WARNING, "SQL Exception while getting connection", sqle);
            throw createDataAccessException(sqle);
        }
        catch (final MetaDataException mde) {
            DataAccess.OUT.log(Level.WARNING, "Exception while getting table definition", mde);
            throw new DataAccessException("Exception while getting table definition", mde);
        }
        catch (final QueryConstructionException qce) {
            DataAccess.OUT.log(Level.WARNING, "Exception while constructing query for update", qce);
            throw new DataAccessException("Exception while constructing query for update", qce);
        }
        finally {
            safeClose(conn);
        }
        clearAndStartRecording(dataObject);
        DataAccess.OUT.exiting(DataAccess.CLASS_NAME, "update", dataObject);
        return dataObject;
    }
    
    public static int update(final UpdateQuery updateQuery) throws DataAccessException {
        return update(updateQuery, true);
    }
    
    private static int update(final UpdateQuery updateQuery, final boolean checkSystem) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "update", updateQuery);
        if (PersistenceInitializer.onSAS()) {
            if (AppResources.getBoolean("validate.updatequery", Boolean.valueOf(false))) {
                validateUpdateQuery(updateQuery);
            }
        }
        else {
            validateUpdateQuery(updateQuery);
        }
        Connection conn = null;
        PreparedStatement ps = null;
        String tableName = null;
        final List<String> tables = new ArrayList<String>(1);
        try {
            tableName = updateQuery.getTableName();
            if (checkSystem) {
                checkForSystemTable(tableName);
            }
            invokeCallbackHandlersFor(updateQuery, tableName);
            conn = getRelationalAPI().getConnection();
            tables.add(tableName);
            PersistenceUtil.handlePreExec(tables, conn, null);
            ps = getRelationalAPI().createUpdateStatement(updateQuery, conn);
            int index = 1;
            final Map values = updateQuery.getUpdateColumns();
            final Iterator keyItr = values.keySet().iterator();
            Map<String, Object> dyValues = null;
            Map<String, Integer> dyColVsSQLTypes = null;
            int dyIndex = -1;
            final String dcType = MetaDataUtil.getTableDefinitionByName(tableName).getDynamicColumnType();
            while (keyItr.hasNext()) {
                final Column column = keyItr.next();
                if (values.get(column) instanceof Column) {
                    continue;
                }
                Object value = values.get(column);
                final String datatype = column.getDataType();
                int javaSQLType = column.getType();
                final String database = getRelationalAPI().getDBAdapter().getDBType();
                if (DataTypeUtil.isUDT(datatype)) {
                    final DataTypeDefinition dataDef = DataTypeManager.getDataTypeDefinition(datatype);
                    try {
                        value = DTTransformationUtil.transform(tableName, column.getColumnName(), value, datatype, database);
                    }
                    catch (final Exception e) {
                        throw new SQLException("Exception while transforming data." + e);
                    }
                    if (dataDef.getDTAdapter(database) == null) {
                        throw new IllegalArgumentException("DTAdapter not defined for type :: " + datatype);
                    }
                    javaSQLType = dataDef.getDTAdapter(database).getJavaSQLType();
                }
                if (MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(column.getColumnName()).isDynamic()) {
                    if (dyValues == null) {
                        dyValues = new LinkedHashMap<String, Object>();
                        dyColVsSQLTypes = new LinkedHashMap<String, Integer>();
                        dyIndex = index;
                    }
                    dyValues.put(column.getColumnName(), value);
                    dyColVsSQLTypes.put(column.getColumnName(), javaSQLType);
                }
                else {
                    getRelationalAPI().setValue(ps, index, javaSQLType, value);
                    ++index;
                }
            }
            if (dyValues != null) {
                getRelationalAPI().setValue(ps, dyIndex, dyColVsSQLTypes, dyValues, dcType);
            }
            return getRelationalAPI().executeUpdate(ps);
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Exception occured while forming update sql as preparedStatement for the table " + tableName, mde);
        }
        catch (final QueryConstructionException qce) {
            throw new DataAccessException("exception occured while forming update sql for the table ", qce);
        }
        catch (final SQLException sqle) {
            try {
                if (TableArchiverUtil.isArchiveEnabled()) {
                    getRelationalAPI().getArchiveAdapter().restoreUnArchivedInvisibleTable(tableName, conn, sqle);
                }
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
            throw createDataAccessException("Exception occured while executing the update sql ", sqle, tableName);
        }
        finally {
            if (conn != null) {
                PersistenceUtil.handlePostExec(tables, conn, null);
            }
            safeClose(ps);
            safeClose(conn);
        }
    }
    
    private static void validateUpdateQuery(final UpdateQuery updateQuery) {
        final String tableName = updateQuery.getTableName();
        final List<Table> tableList = updateQuery.getTableList();
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException e) {
            throw new IllegalArgumentException(e);
        }
        if (td == null) {
            throw new IllegalArgumentException("No table exists with name " + tableName);
        }
        final Map values = updateQuery.getUpdateColumns();
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Table cannot be updated without update Columns.");
        }
        for (final Column col : values.keySet()) {
            final String columnName = col.getColumnName();
            final ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
            if (cd == null) {
                throw new IllegalArgumentException("No column exists with name " + columnName + " in table " + tableName);
            }
            final String updateColumnDatatype = cd.getDataType();
            final Object updateValue = values.get(col);
            if (updateValue == null) {
                if (!cd.isNullable() && cd.getPhysicalColumn() != null) {
                    DataAccess.OUT.log(Level.WARNING, "Cannot allow null update for a not nullable column(JSON column cannot have not-null constraint check). ");
                }
                else {
                    if (!cd.isNullable() || cd.isKey()) {
                        throw new IllegalArgumentException("Cannot update Non-nullable column " + tableName + "." + columnName + " with NULL value.");
                    }
                    continue;
                }
            }
            else if (updateValue instanceof Column) {
                if (updateValue instanceof CaseExpression) {
                    final CaseExpression ce = (CaseExpression)updateValue;
                    for (final CaseExpression.WhenExpr expr : ce.getWhenExpressions()) {
                        final Object value = expr.getValue();
                        if (value instanceof Column) {
                            validateUpdateColumnDataType(tableList, (Column)value, updateColumnDatatype);
                        }
                        else {
                            validateValue(cd, value);
                        }
                    }
                    final Object elseVal = ce.getElseVal();
                    if (elseVal != null) {
                        if (elseVal instanceof Column) {
                            validateUpdateColumnDataType(tableList, (Column)elseVal, updateColumnDatatype);
                        }
                        else {
                            ((CaseExpression)updateValue).elseVal(validateValue(cd, ce.getElseVal()));
                        }
                    }
                    values.put(col, updateValue);
                }
                else {
                    if (!(updateValue instanceof Column)) {
                        continue;
                    }
                    validateUpdateColumnDataType(tableList, (Column)updateValue, updateColumnDatatype);
                }
            }
            else {
                values.put(col, validateValue(cd, updateValue));
            }
        }
    }
    
    private static void validateUpdateColumnDataType(final List<Table> tabList, final Column column, final String columnDatatype) throws IllegalArgumentException {
        if (column instanceof com.adventnet.ds.query.Operation || column instanceof Function || column instanceof DerivedColumn) {
            return;
        }
        final String tableAlias = column.getTableAlias();
        final String columnAlias = column.getColumnAlias();
        String columnName = column.getColumnName();
        final Table table = getTable(tableAlias, tabList);
        if (table == null) {
            throw new IllegalArgumentException("Unknow column [" + column.getTableAlias() + "." + column.getColumnName() + "] is specified for update value.");
        }
        String tableName = table.getTableName();
        if (table instanceof DerivedTable) {
            final SelectQuery sq = (SelectQuery)((DerivedTable)table).getSubQuery();
            final List selectCols = sq.getSelectColumns();
            for (int size = selectCols.size(), i = 0; i < size; ++i) {
                if (columnAlias.equals(selectCols.get(i).getColumnAlias())) {
                    final Column derivedTabColumn = selectCols.get(i);
                    if (derivedTabColumn instanceof com.adventnet.ds.query.Operation || derivedTabColumn instanceof Function || derivedTabColumn instanceof DerivedColumn) {
                        return;
                    }
                    columnName = derivedTabColumn.getColumnName();
                    final Table derivedtable = getTable(derivedTabColumn.getTableAlias(), sq.getTableList());
                    tableName = derivedtable.getTableName();
                }
            }
        }
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException e) {
            throw new IllegalArgumentException(e);
        }
        if (td == null) {
            throw new IllegalArgumentException("Unknow table " + tableName + " column is specified for update value.");
        }
        final ColumnDefinition existingColumn = td.getColumnDefinitionByName(columnName);
        if (existingColumn == null) {
            throw new IllegalArgumentException("Unknow column " + column.getColumnName() + " is specified for update value.");
        }
        if (!existingColumn.getDataType().equals(columnDatatype)) {
            throw new IllegalArgumentException("Different dataType column has been given for Update.");
        }
    }
    
    private static Table getTable(final String tableAlias, final List tableList) {
        for (int size = tableList.size(), i = 0; i < size; ++i) {
            if (tableAlias != null && tableAlias.equals(tableList.get(i).getTableAlias())) {
                return tableList.get(i);
            }
        }
        return null;
    }
    
    private static Object validateValue(final ColumnDefinition cd, Object updateValue) {
        final String dataType = cd.getDataType();
        Label_0161: {
            if (updateValue instanceof String) {
                if (dataType.equals("CHAR") || dataType.equals("SCHAR") || dataType.equals("NCHAR")) {
                    final String value = (String)updateValue;
                    if (cd.getMaxLength() > 0 && cd.getMaxLength() < 256 && value.length() > cd.getMaxLength()) {
                        throw new IllegalArgumentException("The size of the value " + value + " is greater than the maxLength(" + cd.getMaxLength() + ") of the Column.");
                    }
                    break Label_0161;
                }
                else {
                    try {
                        updateValue = MetaDataUtil.convert((String)updateValue, dataType);
                        break Label_0161;
                    }
                    catch (final MetaDataException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            }
            try {
                MetaDataUtil.validate(updateValue, dataType);
            }
            catch (final MetaDataException e) {
                throw new IllegalArgumentException(e);
            }
        }
        final AllowedValues av = cd.getAllowedValues();
        if (av != null) {
            av.validateValue(updateValue);
        }
        return updateValue;
    }
    
    public static void update(final List updateQueries) throws DataAccessException {
        update(updateQueries, true);
    }
    
    private static void update(final List updateQueries, final boolean checkSystem) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "update", updateQueries);
        if (checkSystem) {
            for (int i = 0; i < updateQueries.size(); ++i) {
                final UpdateQuery updateQuery = updateQueries.get(i);
                final String tableName = updateQuery.getTableName();
                checkForSystemTable(tableName);
            }
        }
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DataAccess.dataSource.getConnection();
            stmt = conn.createStatement();
            for (int j = 0; j < updateQueries.size(); ++j) {
                final UpdateQuery updateQuery2 = updateQueries.get(j);
                if (PersistenceInitializer.onSAS()) {
                    if (AppResources.getBoolean("validate.updatequery", Boolean.valueOf(false))) {
                        validateUpdateQuery(updateQuery2);
                    }
                }
                else {
                    validateUpdateQuery(updateQuery2);
                }
                final String tableName2 = updateQuery2.getTableName();
                final Criteria criteria = updateQuery2.getCriteria();
                final Map values = updateQuery2.getUpdateColumns();
                final String updatesql = getRelationalAPI().getUpdateSQL(tableName2, values, criteria);
                DataAccess.OUT.log(Level.FINEST, "update query ", updatesql);
                if (updatesql != null) {
                    stmt.addBatch(updatesql);
                }
            }
            getRelationalAPI().executeBatch(stmt);
        }
        catch (final QueryConstructionException qce) {
            throw new DataAccessException("exception occured while forming update sql for the table ", qce);
        }
        catch (final SQLException sqle) {
            throw createDataAccessException("Exception occured while executing the update sql ", sqle);
        }
        finally {
            safeClose(stmt);
            safeClose(conn);
        }
    }
    
    public static void delete(final Row row) throws DataAccessException {
        delete(row, true);
    }
    
    private static void delete(final Row row, final boolean checkSystem) throws DataAccessException {
        Operation.clear();
        final Criteria criteria = QueryConstructor.formCriteria(row);
        DeleteUtil.setBdfkStack(new Stack());
        DeleteUtil.delete(row.getOriginalTableName(), criteria, checkSystem);
        Operation.clear();
    }
    
    public static void delete(final Criteria condition) throws DataAccessException {
        delete(condition, true);
    }
    
    private static void delete(final Criteria condition, final boolean checkSystem) throws DataAccessException {
        final String tableName = getTableName(condition);
        DeleteUtil.setBdfkStack(new Stack());
        DeleteUtil.delete(tableName, condition, checkSystem);
    }
    
    private static String getTableName(Criteria criteria) {
        while (criteria.getLeftCriteria() != null) {
            criteria = criteria.getLeftCriteria();
        }
        if (criteria.getColumn() instanceof Function) {
            final Function func = (Function)criteria.getColumn();
            final Object[] functionArguments;
            final Object[] obj = functionArguments = func.getFunctionArguments();
            for (final Object object : functionArguments) {
                if (object instanceof Column) {
                    return ((Column)object).getTableAlias();
                }
            }
        }
        if (criteria.getColumn() instanceof com.adventnet.ds.query.Operation) {
            final com.adventnet.ds.query.Operation oper = (com.adventnet.ds.query.Operation)criteria.getColumn();
            if (oper.getLHSArgument() instanceof Column) {
                return ((Column)oper.getLHSArgument()).getTableAlias();
            }
            if (oper.getRHSArgument() instanceof Column) {
                return ((Column)oper.getRHSArgument()).getTableAlias();
            }
        }
        return criteria.getColumn().getTableAlias();
    }
    
    public static void delete(final String tableName, final Row condition) throws DataAccessException {
        delete(tableName, condition, true);
    }
    
    private static void delete(final String tableName, final Row condition, final boolean checkSystem) throws DataAccessException {
        try {
            if (tableName == null || MetaDataUtil.getTableDefinitionByName(tableName) == null) {
                throw new DataAccessException("Invalid tableName specified " + tableName);
            }
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Invalid tableName specified " + tableName, mde);
        }
        if (condition != null) {
            if (!tableName.equals(condition.getTableName())) {
                throw new DataAccessException("The From tableName and condition tableName does not match");
            }
            final Criteria ct = QueryConstructor.formCriteria(condition);
            delete(tableName, ct, checkSystem);
        }
    }
    
    public static void delete(final String tableName, final Criteria criteria) throws DataAccessException {
        delete(tableName, criteria, true);
    }
    
    private static void delete(final String tableName, final Criteria criteria, final boolean checkSystem) throws DataAccessException {
        if (Operation.getContextInfo(criteria) == null) {
            DeleteUtil.setBdfkStack(new Stack());
            if (PersistenceInitializer.onSAS()) {
                invokeCallbackHandlersFor(criteria, tableName);
            }
            DeleteUtil.delete(tableName, criteria, checkSystem);
        }
        else {
            final List criteriaList = Operation.getDeleteCriteriaList();
            for (int i = 0; i < criteriaList.size(); ++i) {
                final Criteria delCriteria = criteriaList.get(i);
                final String delCriTableName = getTableName(delCriteria);
                if (PersistenceInitializer.onSAS()) {
                    invokeCallbackHandlersFor(delCriteria, delCriTableName);
                }
                DeleteUtil.executeDelete(delCriTableName, delCriteria);
            }
            Operation.clearCriteriaList();
        }
    }
    
    public static int delete(final DeleteQuery delQry) throws DataAccessException {
        int noOfRowsDeleted = 0;
        if (Operation.getContextInfo(delQry) == null) {
            DeleteUtil.setBdfkStack(new Stack());
            if (PersistenceInitializer.onSAS()) {
                invokeCallbackHandlersFor(delQry, delQry.getTableName());
            }
            noOfRowsDeleted = DeleteUtil.delete(delQry, true);
        }
        else {
            if (PersistenceInitializer.onSAS()) {
                invokeCallbackHandlersFor(delQry, delQry.getTableName());
            }
            noOfRowsDeleted = DeleteUtil.executeDelete(delQry);
            final List criteriaList = Operation.getDeleteCriteriaList();
            for (int i = 0; i < criteriaList.size(); ++i) {
                final Criteria delCriteria = criteriaList.get(i);
                final String delCriTableName = getTableName(delCriteria);
                if (PersistenceInitializer.onSAS()) {
                    invokeCallbackHandlersFor(delCriteria, delCriTableName);
                }
                DeleteUtil.executeDelete(delCriTableName, delCriteria);
            }
            Operation.clearCriteriaList();
        }
        return noOfRowsDeleted;
    }
    
    public static DataObject get(final String tableName, final Row instance) throws DataAccessException {
        return get(tableName, instance, true);
    }
    
    static DataObject get(final String tableName, final Row instance, final boolean checkSystem) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableName, instance);
        return get(sq, checkSystem);
    }
    
    public static DataObject get(final String tableName, final List instances) throws DataAccessException {
        return get(tableName, instances, true);
    }
    
    static DataObject get(final String tableName, final List instances, final boolean checkSystem) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableName, instances);
        return get(sq, checkSystem);
    }
    
    public static DataObject get(final String tableName, final Criteria condition) throws DataAccessException {
        return get(tableName, condition, true);
    }
    
    static DataObject get(final String tableName, final Criteria condition, final boolean checkSystem) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableName, condition);
        return get(sq, checkSystem);
    }
    
    public static DataObject get(final List tableNames, final Row instance) throws DataAccessException {
        return get(tableNames, instance, true);
    }
    
    static DataObject get(final List tableNames, final Row instance, final boolean checkSystem) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableNames, instance);
        return get(sq, checkSystem);
    }
    
    public static DataObject get(final List tableNames, final List optionalTableNames, final Criteria condition) throws DataAccessException {
        return get(tableNames, optionalTableNames, condition, true);
    }
    
    static DataObject get(final List tableNames, final List optionalTableNames, final Criteria condition, final boolean checkSystem) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableNames, optionalTableNames, condition);
        return get(sq, checkSystem);
    }
    
    public static DataObject get(final List tableNames, final List instances) throws DataAccessException {
        return get(tableNames, instances, true);
    }
    
    static DataObject get(final List tableNames, final List instances, final boolean checkSystem) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableNames, instances);
        return get(sq, checkSystem);
    }
    
    public static DataObject get(final List tableNames, final Criteria condition) throws DataAccessException {
        return get(tableNames, condition, true);
    }
    
    static DataObject get(final List tableNames, final Criteria condition, final boolean checkSystem) throws DataAccessException {
        final SelectQuery sq = QueryConstructor.get(tableNames, condition);
        return get(sq, checkSystem);
    }
    
    public static DataObject get(final SelectQuery query) throws DataAccessException {
        return get(query, true);
    }
    
    static DataObject get(final SelectQuery query, final boolean checkSystem) throws DataAccessException {
        if (checkSystem) {
            checkForSystemTable(query);
        }
        return GetUtil.get(query);
    }
    
    public static DataObject getPrimaryKeys(final String tableName, final Criteria condition) throws DataAccessException {
        throw new DataAccessException("Not yet supported");
    }
    
    public static DataObject getForPersonality(final String personalityName, final Row instance) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonality(personalityName, instance);
        return get(query);
    }
    
    public static DataObject getForPersonality(final String personalityName, final List instances) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonality(personalityName, instances);
        return get(query);
    }
    
    public static DataObject getForPersonality(final String personalityName, final Criteria condition) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonality(personalityName, condition);
        return get(query);
    }
    
    public static DataObject getForPersonalities(final List personalities, final Row instance) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonalities(personalities, instance);
        return get(query);
    }
    
    public static DataObject getForPersonalities(final List personalities, final List instances) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonalities(personalities, instances);
        return get(query);
    }
    
    public static DataObject getForPersonalities(final List personalities, final Criteria condition) throws DataAccessException {
        final SelectQuery query = QueryConstructor.getForPersonalities(personalities, condition);
        return get(query);
    }
    
    public static DataObject getForPersonalities(final List personalities, final List deepRetrievedPersonalities, final Row instance) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "getForPersonalities", new Object[] { personalities, deepRetrievedPersonalities, instance });
        final Criteria condition = QueryConstructor.formCriteria(instance);
        return getForPersonalities(personalities, deepRetrievedPersonalities, condition);
    }
    
    public static DataObject getForPersonalities(final List personalities, final List deepRetrievedPersonalities, final List instances) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "getForPersonalities", new Object[] { personalities, deepRetrievedPersonalities, instances });
        final Criteria condition = QueryConstructor.formCriteria(instances);
        return getForPersonalities(personalities, deepRetrievedPersonalities, condition);
    }
    
    public static DataObject getForPersonalities(final List personalities, final List deepRetrievedPersonalities, final Criteria condition) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "getForPersonalities", new Object[] { personalities, deepRetrievedPersonalities, condition });
        if (personalities == null || personalities.contains(null)) {
            throw new DataAccessException("Personality cannot be null or contain null");
        }
        if (deepRetrievedPersonalities != null && deepRetrievedPersonalities.contains(null)) {
            throw new DataAccessException("Deep Retrieved Personalities cannot contain null");
        }
        if (deepRetrievedPersonalities != null && !personalities.containsAll(deepRetrievedPersonalities)) {
            DataAccess.OUT.log(Level.WARNING, "Not all the personalities requested for deep retrieval {0} are listed in the personalities {1}", new Object[] { deepRetrievedPersonalities, personalities });
            throw new DataAccessException("Not all the personalities requested for deep retrieval are listed in the personalities");
        }
        DataObject dObj;
        if (PersonalityConfigurationUtil.areAllPersonalitiesNotIndexed(deepRetrievedPersonalities)) {
            DataAccess.OUT.log(Level.FINEST, "All personalities in given list {0} are NOT indexed - optimizing", deepRetrievedPersonalities);
            final SelectQuery sq = QueryConstructor.getForPersonalities(personalities, deepRetrievedPersonalities, condition);
            dObj = get(sq);
            DataAccess.OUT.log(Level.FINEST, "DataObject returned from deep fetch : {0} ", dObj);
        }
        else {
            DataAccess.OUT.log(Level.FINEST, "list {0} contains indexed personality", deepRetrievedPersonalities);
            dObj = getForPersonalities(personalities, condition);
            fillDeepRetrievedData(dObj, deepRetrievedPersonalities);
        }
        DataAccess.OUT.exiting(DataAccess.CLASS_NAME, "getForPersonalities", dObj);
        return dObj;
    }
    
    private static void fillDeepRetrievedData(final DataObject dob, final List deepRetrievedPersonalities) throws DataAccessException {
        DataAccess.OUT.log(Level.FINEST, "fillDeepRetrievedData :: {0}", deepRetrievedPersonalities);
        final WritableDataObject dObj = (WritableDataObject)dob;
        final List deepRetDominantTables = new ArrayList();
        for (int deepRetPerSize = deepRetrievedPersonalities.size(), i = 0; i < deepRetPerSize; ++i) {
            final String personality = deepRetrievedPersonalities.get(i);
            final String dominantTable = PersonalityConfigurationUtil.getDominantTableForPersonality(personality);
            if (!deepRetDominantTables.contains(dominantTable)) {
                deepRetDominantTables.add(dominantTable);
            }
        }
        for (int deepRetDomTableSize = deepRetDominantTables.size(), j = 0; j < deepRetDomTableSize; ++j) {
            final String dominantTable = deepRetDominantTables.get(j);
            DataAccess.OUT.log(Level.FINEST, "Deep retrieving for the dominant table {0}", dominantTable);
            final Iterator itr = dObj.getRows(dominantTable);
            while (itr.hasNext()) {
                final Row row = itr.next();
                DataAccess.OUT.log(Level.FINE, "Deep retrieving for the row {0}", row);
                final DataObject deepRetDObj = get(QueryConstructor.getCompleteQuery(row));
                DataAccess.OUT.log(Level.FINE, "Merging the deep retrieved DataObject.");
                dObj.merge(deepRetDObj);
            }
        }
        DataAccess.OUT.exiting(DataAccess.CLASS_NAME, "get", dObj);
    }
    
    public static DataObject getCompleteData(final Row row) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "getCompleteData", new Object[] { row });
        final String tableName = row.getTableName();
        final String idxTableName = PersistenceUtil.getIndexTableName(tableName);
        final boolean isIndexed = PersonalityConfigurationUtil.isIndexed(tableName);
        if (idxTableName == null && isIndexed) {
            final String msg = "Table " + tableName + " is not indexed";
            DataAccess.OUT.log(Level.FINEST, msg);
            throw new DataAccessException(msg);
        }
        final List containingTables = PersistenceUtil.getContainingTables(row);
        if (containingTables.isEmpty()) {
            return constructDataObject();
        }
        final List sortedContainingTables = PersistenceUtil.sortTables(containingTables);
        DataAccess.OUT.log(Level.FINEST, "Sorted tables are {0}", sortedContainingTables);
        final boolean[] isLeftJoins = new boolean[sortedContainingTables.size()];
        Arrays.fill(isLeftJoins, true);
        final Criteria criteria = QueryConstructor.formCriteria(row);
        final SelectQuery query = QueryConstructor.get(sortedContainingTables, isLeftJoins, criteria);
        final DataObject deepRetDObj = get(query);
        DataAccess.OUT.exiting(DataAccess.CLASS_NAME, "get", deepRetDObj);
        return deepRetDObj;
    }
    
    public static boolean isInstanceOf(final Row instance, final String personalityName) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "isInstanceOf", instance);
        final List personalitiesFromDB = getPersonalities(instance);
        final boolean isInstanceOf = personalitiesFromDB.contains(personalityName);
        DataAccess.OUT.exiting(DataAccess.CLASS_NAME, "isInstanceOf", isInstanceOf);
        return isInstanceOf;
    }
    
    public static boolean isInstanceOf(final Row instance, final List personalities) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "isInstanceOf", instance);
        final List personalitiesFromDB = getPersonalities(instance);
        final boolean isInstanceOf = personalitiesFromDB.containsAll(personalities);
        DataAccess.OUT.exiting(DataAccess.CLASS_NAME, "isInstanceOf", isInstanceOf);
        return isInstanceOf;
    }
    
    public static List getPersonalities(final Row instance) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "getPersonalities", instance);
        final List tableList = PersistenceUtil.getContainingTables(instance);
        final List personalities = PersonalityConfigurationUtil.getPersonalities(tableList);
        DataAccess.OUT.exiting(DataAccess.CLASS_NAME, "getPersonalities", personalities);
        return personalities;
    }
    
    public static List getDominantPersonalities(final Row instance) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "getDominantPersonalities", instance);
        final List tableList = PersistenceUtil.getContainingTables(instance);
        final List personalities = PersonalityConfigurationUtil.getDominantPersonalities(tableList);
        DataAccess.OUT.exiting(DataAccess.CLASS_NAME, "getDominantPersonalities", personalities);
        return personalities;
    }
    
    private static boolean isPresent(final Row instance) throws DataAccessException {
        boolean found = false;
        final String tableName = instance.getTableName();
        Connection conn = null;
        DataSet ds = null;
        SelectQuery sq = null;
        try {
            final Table table = Table.getTable(tableName);
            sq = new SelectQueryImpl(table);
            final List pkColumns = instance.getPKColumns();
            sq.addSelectColumn(Column.getColumn(tableName, pkColumns.get(0)));
            final Criteria criteria = QueryConstructor.formCriteria(instance);
            sq.setCriteria(criteria);
            conn = getRelationalAPI().getConnection();
            ds = getRelationalAPI().executeQuery(sq, conn);
            found = ds.next();
        }
        catch (final QueryConstructionException qce) {
            DataAccess.OUT.log(Level.FINER, "Exception occured when constructing SQL corresponding to the SelectQuery {0}", sq);
            DataAccess.OUT.log(Level.FINER, "QueryConstructionException thrown", qce);
            final String mess = "SQLException occured while constructing SQL to check if a row is present in the table " + tableName;
            throw new DataAccessException(mess, qce);
        }
        catch (final SQLException sqle) {
            DataAccess.OUT.log(Level.FINER, "SQLException occured while executing query to check if the row {0} is present in the database", instance);
            DataAccess.OUT.log(Level.FINER, "SQLException thrown", sqle);
            final String mess = "SQLException occured while executing query to check if a row is present in the table " + tableName;
            throw createDataAccessException(mess, sqle, tableName);
        }
        finally {
            safeClose(ds);
            safeClose(conn);
        }
        DataAccess.OUT.exiting(DataAccess.CLASS_NAME, "isPresent", found);
        return found;
    }
    
    private static void clearAndStartRecording(final DataObject dataObj) {
        final WritableDataObject dataObject = (WritableDataObject)dataObj;
        dataObject.clearOperations();
        dataObject.clearIndices();
    }
    
    private static String constructInsertQuery(final Row thisRow) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "constructInsertQuery", thisRow);
        String insertSQL = null;
        try {
            final String tableName = thisRow.getTableName();
            final List columns = thisRow.getColumns();
            final int size = columns.size();
            final HashMap values = new LinkedHashMap();
            for (int i = 0; i < size; ++i) {
                values.put(Column.getColumn(tableName, columns.get(i)), thisRow.get(i + 1));
            }
            insertSQL = getRelationalAPI().getInsertSQL(tableName, values);
        }
        catch (final QueryConstructionException qce) {
            throw new DataAccessException("Exception occured while forming InsertSQL for the table " + thisRow.getTableName(), qce);
        }
        DataAccess.OUT.exiting(DataAccess.CLASS_NAME, "constructInsertQuery", insertSQL);
        return insertSQL;
    }
    
    static String[] getGeneratorNames(final String tableName) throws DataAccessException {
        String[] generatorNames;
        try {
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            final List columnDefns = td.getColumnList();
            final int size = columnDefns.size();
            generatorNames = new String[size];
            for (int i = 0; i < size; ++i) {
                final ColumnDefinition cd = columnDefns.get(i);
                final UniqueValueGeneration uvg = cd.getUniqueValueGeneration();
                if (uvg != null) {
                    generatorNames[i] = uvg.getGeneratorName();
                }
                else {
                    generatorNames[i] = null;
                }
            }
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Exception occured while identifying the data types for the table " + tableName, mde);
        }
        return generatorNames;
    }
    
    public static boolean generateValues(final Row row) throws DataAccessException {
        final int size = row.getColumns().size();
        boolean hasUVH = false;
        for (int i = 0; i < size; ++i) {
            final Object holderObj = row.get(i + 1);
            if (holderObj instanceof UniqueValueHolder) {
                final UniqueValueHolder holder = (UniqueValueHolder)holderObj;
                final Object value = generateValue(holder.getTableName(), holder);
                DataAccess.OUT.log(Level.FINEST, "The unique value generated for the column index {0} for the row {1} is {2}", new Object[] { new Integer(i + 1), row, value });
                row.setBlindly(i + 1, value);
                hasUVH = true;
            }
        }
        return hasUVH;
    }
    
    static Object generateValue(final String tableName, final UniqueValueHolder holder) throws DataAccessException {
        try {
            Operation.suspend();
            final String generatorName = holder.getGeneratorName();
            DataAccess.OUT.log(Level.FINE, "Generating value for the generator {0} for table {1} for UVH {2}", new Object[] { generatorName, tableName, holder });
            SequenceGenerator gen = SequenceGeneratorRepository.get(generatorName);
            if (gen == null) {
                try {
                    final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
                    String templateInstanceId = null;
                    if (tabDef.isTemplate() && !tabDef.getTableName().equals(tableName)) {
                        templateInstanceId = tableName.substring(tabDef.getTableName().length() + 1, tableName.length());
                    }
                    SequenceGeneratorRepository.initGeneratorValues(tabDef, templateInstanceId);
                    gen = SequenceGeneratorRepository.get(generatorName);
                }
                catch (final Exception e) {
                    throw new DataAccessException("Problem in initializing the SequenceGenerator for the tableName : " + tableName, e);
                }
            }
            Object value = null;
            if (!holder.isGenerated()) {
                value = gen.nextValue();
                holder.setValue(value);
                holder.setGenerated(true);
            }
            else {
                value = holder.getValue();
                gen.setValue(value);
            }
            DataAccess.OUT.log(Level.FINEST, "The generated value for UVH {0} is {1}", new Object[] { holder, value });
            return value;
        }
        finally {
            Operation.resume();
        }
    }
    
    private static void setValueForUVHColumn(final String tableName, final String columnName, final Object value) throws DataAccessException {
        DataAccess.OUT.log(Level.FINE, "setValueForUVHColumn called with table {0} column {1} value {2}", new Object[] { tableName, columnName, value });
        if (value instanceof UniqueValueHolder) {
            return;
        }
        try {
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            String templateInstanceId = null;
            final ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
            final UniqueValueGeneration uvg = cd.getUniqueValueGeneration();
            if (uvg != null) {
                DataAccess.OUT.log(Level.FINE, "uvg is not null");
                String generatorName;
                if (uvg.isInstanceSpecificSequenceGeneratorEnabled()) {
                    generatorName = uvg.getGeneratorNameForTemplateInstance(tableName, cd.getColumnName());
                }
                else {
                    generatorName = uvg.getGeneratorName();
                }
                SequenceGenerator gen = SequenceGeneratorRepository.get(generatorName);
                if (gen == null) {
                    if (td.isTemplate() && !tableName.equalsIgnoreCase(td.getTableName())) {
                        templateInstanceId = tableName.substring(td.getTableName().length() + 1, tableName.length());
                    }
                    SequenceGeneratorRepository.initGeneratorValues(td, templateInstanceId);
                    gen = SequenceGeneratorRepository.get(generatorName);
                }
                DataAccess.OUT.log(Level.FINE, "Setting value {0} to the SequenceGenerator", value);
                gen.setValue(value);
            }
        }
        catch (final Exception mde) {
            DataAccess.OUT.log(Level.SEVERE, "Exception during UVH value reset for column [" + columnName + "] in tableName [" + tableName + "] ", mde);
            throw new DataAccessException(mde.getMessage(), mde);
        }
    }
    
    private static RelationalAPI getRelationalAPI() {
        return RelationalAPI.getInstance();
    }
    
    private static void insertRows(final String tableName, final Iterator rowIterator, final Connection conn) throws DataAccessException {
        String idxTableName = null;
        boolean indexed = false;
        final String dominantTableName = PersonalityConfigurationUtil.getDominantTable(tableName);
        if (dominantTableName != null) {
            try {
                idxTableName = ((MetaDataUtil.getTableDefinitionByName(dominantTableName + "_PIDX") != null) ? (dominantTableName + "_PIDX") : null);
            }
            catch (final MetaDataException mde) {
                mde.printStackTrace();
            }
        }
        indexed = (idxTableName != null);
        String[] keyColumnNames = null;
        boolean isDominantTable = false;
        boolean sharesSamePK = false;
        if (indexed) {
            try {
                final Join joinWithDominantTable = PersistenceUtil.getJoinWithDominantTable(tableName);
                isDominantTable = (joinWithDominantTable == null);
                if (joinWithDominantTable != null) {
                    DataAccess.OUT.log(Level.FINER, " Not a Dominant Table!! {0}", new Object[] { tableName });
                    keyColumnNames = PersistenceUtil.getColumns(joinWithDominantTable, false);
                    sharesSamePK = isPKEquals(tableName, keyColumnNames);
                }
                else {
                    DataAccess.OUT.log(Level.FINER, " Is a Dominant Table!! {0}", new Object[] { tableName });
                    final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                    final List columnList = td.getPrimaryKey().getColumnList();
                    final int size = columnList.size();
                    keyColumnNames = columnList.toArray(new String[size]);
                    sharesSamePK = true;
                }
                DataAccess.OUT.log(Level.FINER, " index:::: keys: {0} SharesSamePK: {1}", new Object[] { Arrays.asList(keyColumnNames), sharesSamePK });
            }
            catch (final MetaDataException mde2) {
                final String mess = "Exception occured while getting the definition for the table " + idxTableName;
                DataAccess.OUT.log(Level.FINER, mess, mde2);
                throw new DataAccessException(mess, mde2);
            }
        }
        PreparedStatement ps = null;
        PreparedStatement indexPS = null;
        try {
            ps = getRelationalAPI().createInsertStatement(tableName, conn);
            if (indexed) {
                indexPS = getRelationalAPI().createInsertStatement(idxTableName, conn);
            }
            final List indexTableRows = new ArrayList();
            while (rowIterator.hasNext()) {
                final Row thisRow = rowIterator.next();
                setValues(ps, thisRow);
                ps.addBatch();
                if (indexed) {
                    final Row indexRow = constructIndexRow(idxTableName, thisRow, keyColumnNames);
                    if (!isDominantTable && !sharesSamePK) {
                        if (indexTableRows.contains(indexRow.getPKValues())) {
                            continue;
                        }
                        if (isPresent(indexRow)) {
                            continue;
                        }
                        indexTableRows.add(indexRow.getPKValues());
                    }
                    setValues(indexPS, indexRow);
                    indexPS.addBatch();
                }
            }
            getRelationalAPI().executeBatch(ps);
            if (indexed) {
                try {
                    getRelationalAPI().executeBatch(indexPS);
                }
                catch (final SQLException sqle) {
                    DataAccess.OUT.log(Level.FINE, "Exception occured while inserting rows for the table {0}", idxTableName);
                    DataAccess.OUT.log(Level.FINE, "", sqle);
                    throw createDataAccessException("Exception occured while inserting rows ", sqle, idxTableName);
                }
            }
        }
        catch (final BatchUpdateException bae) {
            try {
                if (TableArchiverUtil.isArchiveEnabled()) {
                    getRelationalAPI().getArchiveAdapter().restoreUnArchivedInvisibleTable(tableName, conn, bae);
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            final DataAccessException dae = createDataAccessException(bae.getMessage(), bae, tableName);
            dae.setUpdateCounts(bae.getUpdateCounts());
            throw dae;
        }
        catch (final SQLException sqle2) {
            DataAccess.OUT.log(Level.FINE, "Exception occured while inserting rows for the table {0}", tableName);
            DataAccess.OUT.log(Level.FINE, "", sqle2);
            throw createDataAccessException("Exception occured while inserting rows ", sqle2, tableName);
        }
        catch (final QueryConstructionException qexp) {
            DataAccess.OUT.log(Level.FINE, "Exception occured while constructing insert query for the table {0} ", tableName);
            throw new DataAccessException("Exception occured while constructing insert query for the " + tableName, qexp);
        }
        catch (final MetaDataException mexp) {
            DataAccess.OUT.log(Level.FINE, "Exception occured while getting table definition for the table {0} ", tableName);
            throw new DataAccessException("Exception occured while getting table definition for the " + tableName, mexp);
        }
        finally {
            safeClose(indexPS);
            safeClose(ps);
        }
    }
    
    private static Row constructIndexRow(final String idxTableName, final Row thisRow, final String[] keys) {
        final Row idxRow = new Row(idxTableName);
        for (int size = keys.length, i = 0; i < size; ++i) {
            final Object value = thisRow.get(keys[i]);
            idxRow.set(i + 1, value);
        }
        idxRow.set("TABLE_NAME", thisRow.getTableName());
        DataAccess.OUT.log(Level.FINER, " Returning index Row {0} ", new Object[] { idxRow });
        return idxRow;
    }
    
    private static boolean isPKEquals(final String tableName, final String[] columnNames) throws DataAccessException {
        try {
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            if (td == null) {
                final String mess = "Exception occured while getting the definition for the table " + tableName;
                DataAccess.OUT.log(Level.FINER, mess);
                throw new DataAccessException(mess);
            }
            final PrimaryKeyDefinition pkDef = td.getPrimaryKey();
            final List colList = pkDef.getColumnList();
            final String[] pkColumns = colList.toArray(new String[colList.size()]);
            return Arrays.equals(columnNames, pkColumns);
        }
        catch (final MetaDataException mde) {
            final String mess = "Exception occured while getting the definition for the table " + tableName;
            DataAccess.OUT.log(Level.FINER, mess, mde);
            throw new DataAccessException(mess, mde);
        }
    }
    
    private static void fillGenValues(final Row row) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "fillGenValues", new Object[] { row });
        final List columnNames = row.getColumns();
        final String tableName = row.getTableName();
        final int size = columnNames.size();
        String columnName = null;
        for (int i = 1; i <= size; ++i) {
            Object value = row.get(i);
            columnName = columnNames.get(i - 1);
            if (value instanceof UniqueValueHolder) {
                final UniqueValueHolder holder = (UniqueValueHolder)value;
                value = holder.getValue();
                if (value == null || !holder.isGenerated()) {
                    value = generateValue(holder.getTableName(), holder);
                }
                row.setBlindly(i, value);
            }
            setValueForUVHColumn(tableName, columnName, value);
        }
    }
    
    private static void setValues(final PreparedStatement ps, final Row row) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "setValues", new Object[] { ps, row });
        final List columnNames = row.getColumns();
        final String tableName = row.getTableName();
        final int size = columnNames.size();
        String columnName = null;
        String dcType = null;
        Map<String, Object> dyValues = null;
        Map<String, Integer> dyColVsSQLTypes = null;
        int parameterIndex = 1;
        int dyIndex = -1;
        String dataType = null;
        try {
            for (int i = 1; i <= size; ++i) {
                Object value = row.get(i);
                columnName = columnNames.get(i - 1);
                dataType = row.getColumnType(i);
                if (!DataTypeUtil.isUDT(dataType) || DataTypeManager.getDataTypeDefinition(dataType).getMeta().processInput()) {
                    if (value instanceof UniqueValueHolder) {
                        final UniqueValueHolder holder = (UniqueValueHolder)value;
                        value = holder.getValue();
                        if (value == null || !holder.isGenerated()) {
                            value = generateValue(holder.getTableName(), holder);
                        }
                        row.setBlindly(i, value);
                    }
                    setValueForUVHColumn(tableName, columnName, value);
                    int javaSQLType = row.getSQLType(i);
                    final String database = getRelationalAPI().getDBAdapter().getDBType();
                    if (DataTypeUtil.isUDT(dataType)) {
                        final DataTypeDefinition dataDef = DataTypeManager.getDataTypeDefinition(dataType);
                        try {
                            value = DTTransformationUtil.transform(tableName, columnName, value, dataType, database);
                        }
                        catch (final Exception e) {
                            throw new SQLException("Exception while transforming data." + e);
                        }
                        if (dataDef.getDTAdapter(database) == null) {
                            throw new IllegalArgumentException("DTAdapter not defined for type :: " + dataType);
                        }
                        javaSQLType = dataDef.getDTAdapter(database).getJavaSQLType();
                    }
                    try {
                        dcType = MetaDataUtil.getTableDefinitionByName(tableName).getDynamicColumnType();
                        if (MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(columnName).isDynamic()) {
                            if (dyValues == null) {
                                dyValues = new LinkedHashMap<String, Object>();
                                dyColVsSQLTypes = new LinkedHashMap<String, Integer>();
                                dyIndex = i;
                            }
                            dyValues.put(columnName, value);
                            dyColVsSQLTypes.put(columnName, new Integer(javaSQLType));
                        }
                        else {
                            getRelationalAPI().setValue(ps, parameterIndex++, javaSQLType, value);
                        }
                    }
                    catch (final MetaDataException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            if (dyValues != null) {
                getRelationalAPI().setValue(ps, parameterIndex, dyColVsSQLTypes, dyValues, dcType);
            }
        }
        catch (final SQLException sqle) {
            final DataAccessException dae = createDataAccessException("Exception occured while setting value in PreparedStatement for the Column [" + columnName + "]\nCheck whether the datatype for this column is given as specified in the data-dictionary\n", sqle, row.getTableName());
            dae.addErrRow(row);
            throw dae;
        }
        catch (final ClassCastException cce) {
            throw new DataAccessException("Invalid Type specified", cce);
        }
    }
    
    private static void index(final PreparedStatement idxPS, final String idxTableName, final Row row) throws DataAccessException {
        DataAccess.OUT.entering(DataAccess.CLASS_NAME, "index", new Object[] { idxPS, idxTableName, row });
        try {
            final String tableName = row.getTableName();
            final Join joinWithDominantTable = PersistenceUtil.getJoinWithDominantTable(tableName);
            final Row idxRow = new Row(idxTableName);
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(idxTableName);
            final List columnList = td.getColumnList();
            final int size = columnList.size();
            if (joinWithDominantTable != null) {
                final String[] dominantColumns = PersistenceUtil.getColumns(joinWithDominantTable, true);
                final String[] slaveColumns = PersistenceUtil.getColumns(joinWithDominantTable, false);
                for (int i = 0; i < size - 1; ++i) {
                    final ColumnDefinition cd = columnList.get(i);
                    final String pkColumnName = cd.getColumnName();
                    final int index = PersistenceUtil.indexOf(dominantColumns, pkColumnName);
                    final String columnName = joinWithDominantTable.getReferencedTableColumn(index);
                    final Object value = row.get(columnName);
                    idxRow.set(i + 1, value);
                }
            }
            else {
                for (int j = 0; j < size - 1; ++j) {
                    final ColumnDefinition cd2 = columnList.get(j);
                    final String columnName2 = cd2.getColumnName();
                    final Object value2 = row.get(columnName2);
                    idxRow.set(j + 1, value2);
                }
            }
            idxRow.set(size, row.getTableName());
            final boolean present = isPresent(idxRow);
            if (!present) {
                setValues(idxPS, idxRow);
                idxPS.executeUpdate();
            }
        }
        catch (final SQLException sqle) {
            DataAccess.OUT.log(Level.FINER, "SQLException occured while trying to insert value in to the index table {0}", idxTableName);
            DataAccess.OUT.log(Level.FINER, "Exception Stack trace:", sqle);
            final String mess = "Exception occured while trying to insert value in to the index table " + idxTableName;
            throw createDataAccessException(mess, sqle);
        }
        catch (final MetaDataException mde) {
            final String mess = "Exception occured while getting the definition for the table " + idxTableName;
            DataAccess.OUT.log(Level.FINER, mess, mde);
            throw new DataAccessException(mess, mde);
        }
    }
    
    private static void safeClose(final Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception exc) {
            exc.printStackTrace();
        }
    }
    
    private static void safeClose(final Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (final Exception exc) {
            exc.printStackTrace();
        }
    }
    
    private static void safeClose(final DataSet ds) {
        try {
            if (ds != null) {
                ds.close();
            }
        }
        catch (final Exception exc) {
            DataAccess.OUT.log(Level.WARNING, "Exception occured while closing DataSet {0}", ds);
            DataAccess.OUT.log(Level.WARNING, "Exception Stack trace:", exc);
        }
    }
    
    public static DataObject fillGeneratedValues(final DataObject dObj) throws DataAccessException {
        final List tableNames = ((WritableDataObject)dObj).getModifiedTables();
        if (tableNames == null) {
            DataAccess.OUT.log(Level.WARNING, "There are no modified rows in the given dataObject. So, fillGeneratedValues is skipped.");
            return dObj;
        }
        final List sortedTableNames = PersistenceUtil.sortTables((WritableDataObject)dObj, tableNames);
        for (int size = sortedTableNames.size(), i = 0; i < size; ++i) {
            final String tableName = sortedTableNames.get(i);
            final Iterator iterator = dObj.getRows(tableName);
            while (iterator.hasNext()) {
                final Row thisRow = iterator.next();
                if (generateValues(thisRow)) {
                    ((WritableDataObject)dObj).indexRow(thisRow);
                }
            }
        }
        return dObj;
    }
    
    private static boolean setUniqueValue(final Row row) throws DataAccessException {
        final List columnNames = row.getColumns();
        final int size = columnNames.size();
        boolean changed = false;
        for (int i = 1; i <= size; ++i) {
            Object value = row.get(i);
            if (value instanceof UniqueValueHolder) {
                final UniqueValueHolder holder = (UniqueValueHolder)value;
                value = holder.getValue();
                row.setBlindly(i, value);
                changed = true;
            }
        }
        return changed;
    }
    
    private static DataAccessException createDataAccessException(final SQLException sqle) {
        return createDataAccessException(sqle.getMessage(), sqle, null);
    }
    
    private static DataAccessException createDataAccessException(final String message, final SQLException sqle) {
        return createDataAccessException(message, sqle, null);
    }
    
    private static DataAccessException createDataAccessException(final String message, final SQLException sqle, final String tableName) {
        final DataAccessException dae = new DataAccessException(message, sqle);
        dae.setErrorCode(sqle.getErrorCode());
        dae.setTableName(tableName);
        return dae;
    }
    
    public static void addDataDictionary(final DataDictionary dd) throws DataAccessException {
        try {
            DataObject data = SchemaBrowserUtil.getDOForDataDictionary(dd);
            SchemaBrowserUtil.fillTableOrderInDO(data);
            if (!PersistenceInitializer.onSAS()) {
                data = add(data, false);
            }
        }
        catch (final MetaDataException me) {
            throw new DataAccessException(me.getMessage(), me);
        }
        catch (final Exception dae) {
            throw new DataAccessException(dae.getMessage(), dae);
        }
    }
    
    private static List getAllSlaveTables(final String tableName) throws MetaDataException {
        final List relatedTables = new ArrayList();
        final List slaveTables = MetaDataUtil.getAllRelatedTableDefinitions(tableName);
        if (slaveTables != null) {
            for (int i = 0; i < slaveTables.size(); ++i) {
                final TableDefinition slaveTD = slaveTables.get(i);
                final String slaveTable = slaveTD.getTableName();
                if (!tableName.equals(slaveTable)) {
                    relatedTables.add(slaveTable);
                }
            }
        }
        DataAccess.OUT.log(Level.FINEST, "Related tables to the table getting dropped {0}", relatedTables);
        return relatedTables;
    }
    
    private static List getTables(final String schemaName) throws SQLException {
        final List tablesPresent = RelationalAPI.getInstance().getTables(schemaName);
        final List tables = new ArrayList();
        for (int size = tablesPresent.size(), i = 0; i < size; ++i) {
            tables.add(tablesPresent.get(i).toLowerCase(Locale.ENGLISH));
        }
        DataAccess.OUT.log(Level.FINEST, "Tables present in the database are {0}", tablesPresent);
        return tables;
    }
    
    public static void createTables(final String ddName) throws DataAccessException, SQLException {
        final String schemaName = null;
        DataDictionary dd = null;
        try {
            dd = MetaDataUtil.getDataDictionary(ddName);
        }
        catch (final MetaDataException mde) {
            final String mess = "Exception occured while getting DataDictionary for the module " + ddName;
            DataAccess.OUT.log(Level.FINER, mess, mde);
            throw new DataAccessException(mess, mde);
        }
        if (dd == null) {
            DataAccess.OUT.log(Level.SEVERE, "Table creation requested for unknown module {0}", ddName);
            throw new DataAccessException("Table creation requested for unknown module or module which doesnot have datadictionary" + ddName);
        }
        final List tableDefns = dd.getTableDefinitions();
        createTables(schemaName, tableDefns);
    }
    
    public static void createTables(final List tableNames) throws DataAccessException, SQLException {
        if (tableNames == null || tableNames.isEmpty()) {
            return;
        }
        final String schemaName = null;
        final List tableDefns = new ArrayList();
        final List tablesPresent = null;
        String tableName = null;
        try {
            final Iterator iterator = tableNames.iterator();
            while (iterator.hasNext()) {
                tableName = iterator.next();
                final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
                tableDefns.add(tabDef);
            }
        }
        catch (final MetaDataException mde) {
            final String mess = "No table eith the name  " + tableName;
            DataAccess.OUT.log(Level.FINER, mess, mde);
            throw new DataAccessException(mess, mde);
        }
        createTables(schemaName, tableDefns);
    }
    
    private static void createTables(final String schemaName, final List tableDefns) throws DataAccessException, SQLException {
        Transaction oldTransaction = null;
        try {
            final RelationalAPI relapi = RelationalAPI.getInstance();
            oldTransaction = suspendTransaction();
            relapi.createTables(schemaName, tableDefns, null);
        }
        catch (final SQLException sqe) {
            throw sqe;
        }
        catch (final RuntimeException run) {
            throw run;
        }
        catch (final DataAccessException dae) {
            throw dae;
        }
        catch (final Exception sqex) {
            DataAccess.OUT.log(Level.WARNING, "Exception in creating table. ", sqex);
            throw new DataAccessException("Exception in creating tables ", sqex);
        }
        finally {
            if (oldTransaction != null) {
                DataAccess.OUT.log(Level.FINEST, "Resuming the suspended Transaction {0}", oldTransaction);
            }
            resumeTransaction(oldTransaction);
        }
        notifyAllListeners(4, tableDefns);
    }
    
    public static void dropTables(final String moduleName) throws DataAccessException, SQLException {
        Transaction oldTransaction = null;
        List tableDefns = null;
        Criteria criteria = null;
        try {
            criteria = new Criteria(Column.getColumn("SB_Applications", "APPL_NAME"), moduleName, 2);
            delete("SB_Applications", criteria, false);
            oldTransaction = suspendTransaction();
            final DataDictionary dd = MetaDataUtil.getDataDictionary(moduleName);
            if (dd == null) {
                throw new DataAccessException("No such module with the name : " + moduleName);
            }
            tableDefns = dd.getTableDefinitions();
            final List existingTables = getTables(null);
            final int size = tableDefns.size();
            for (int i = size - 1; i >= 0; --i) {
                final TableDefinition td = tableDefns.get(i);
                DataAccess.OUT.log(Level.FINEST, "****************************  TableDefinition ************ {0}", td.getTableName());
                SequenceGeneratorRepository.removeGeneratorValues(td);
                final String tableName = td.getTableName();
                if (existingTables.contains(tableName.toLowerCase(Locale.ENGLISH))) {
                    RelationalAPI.getInstance().dropTable(tableName, false, null);
                }
                else {
                    DataAccess.OUT.log(Level.INFO, "Table Not exists in DB.");
                }
                MetaDataUtil.removeTableDefinition(tableName);
            }
            MetaDataUtil.removeDataDictionaryConfiguration(moduleName);
        }
        catch (final SQLException sqe) {
            throw sqe;
        }
        catch (final RuntimeException run) {
            throw run;
        }
        catch (final DataAccessException dae) {
            throw dae;
        }
        catch (final Exception sqex) {
            DataAccess.OUT.log(Level.WARNING, "Exception in creating table. ", sqex);
            throw new DataAccessException("Exception in creating tables ", sqex);
        }
        finally {
            if (oldTransaction != null) {
                DataAccess.OUT.log(Level.FINEST, "Resuming the suspended Transaction {0}", oldTransaction);
            }
            resumeTransaction(oldTransaction);
        }
        notifyAllListeners(5, tableDefns);
    }
    
    public static void createTable(final String moduleName, final TableDefinition tableDefinition) throws DataAccessException, SQLException {
        createTable(moduleName, tableDefinition, (DataObject)null);
    }
    
    public static void createTable(final String moduleName, final TableDefinition tableDefinition, final String createTableOptions) throws DataAccessException, SQLException {
        createTable(moduleName, tableDefinition, createTableOptions, null);
    }
    
    public static void createTable(final String moduleName, final DataObject tableDetails) throws DataAccessException, SQLException, MetaDataException {
        createTable(moduleName, SchemaBrowserUtil.getTableDefinition(tableDetails, tableDetails.getRow("TableDetails")), tableDetails);
    }
    
    public static void createTable(final String moduleName, final TableDefinition tableDefinition, final DataObject tableDetails) throws DataAccessException, SQLException {
        createTable(moduleName, tableDefinition, null, tableDetails);
    }
    
    public static void createTable(final String moduleName, final TableDefinition tableDefinition, final String createTableOptions, final DataObject tableDetails) throws DataAccessException, SQLException {
        try {
            if (!MetaDataUtil.getAllModuleNames().contains(moduleName)) {
                throw new DataAccessException("No such module exists :: " + moduleName);
            }
            final List columnList = tableDefinition.getColumnList();
            MetaDataUtil.validateTableDefinition(tableDefinition);
            if (tableDefinition.getPrimaryKey() == null || columnList == null || columnList.isEmpty()) {
                throw new DataAccessException("Table cannot be created without PrimaryKey or without columns");
            }
            if (tableDefinition.getDynamicColumnList() != null && !tableDefinition.getDynamicColumnList().isEmpty()) {
                throw new DataAccessException("Table cannot have dynamic columns during table creation");
            }
            createTheTable(moduleName, tableDefinition, createTableOptions, tableDetails);
            MetaDataUtil.addTableDefinition(moduleName, tableDefinition);
        }
        catch (final MetaDataException me) {
            throw new DataAccessException(me.getMessage(), me);
        }
        notifyAllListeners(1, tableDefinition.getTableName());
    }
    
    private static void createTheTable(final String ddName, final TableDefinition tableDefinition, final String createTableOptions, DataObject data) throws DataAccessException, SQLException {
        DataObject addedData = null;
        if (data == null) {
            try {
                data = constructDataObject();
                SchemaBrowserUtil.addTableDefinitionInDO(ddName, tableDefinition, data);
            }
            catch (final MetaDataException mae) {
                throw new DataAccessException(mae.getMessage(), mae);
            }
        }
        Transaction oldTransaction = null;
        try {
            oldTransaction = suspendTransaction();
            getTransactionManager().begin();
            try {
                SchemaBrowserUtil.fillTableOrderInDO(data);
                addedData = add(data, false);
                if (!tableDefinition.isTemplate()) {
                    RelationalAPI.getInstance().createTable(tableDefinition, createTableOptions, null);
                }
                getTransactionManager().commit();
            }
            catch (final Exception e) {
                getTransactionManager().rollback();
                throw e;
            }
        }
        catch (final SQLException sqe) {
            throw sqe;
        }
        catch (final RuntimeException run) {
            throw run;
        }
        catch (final DataAccessException dae) {
            throw dae;
        }
        catch (final Exception e) {
            throw new DataAccessException(e.getMessage(), e);
        }
        finally {
            if (oldTransaction != null) {
                DataAccess.OUT.log(Level.FINEST, "Resuming the suspended Transaction {0}", oldTransaction);
            }
            resumeTransaction(oldTransaction);
        }
    }
    
    public static void alterTable(final AlterTableQuery alterTableQuery) throws DataAccessException, SQLException {
        alterTable(alterTableQuery, true);
    }
    
    public static void alterTable(final AlterTableQuery alterTableQuery, final boolean validate) throws DataAccessException, SQLException {
        final RelationalAPI relapi = RelationalAPI.getInstance();
        DataObject alterDO = null;
        Transaction oldTransac1 = null;
        boolean isTemplate;
        try {
            final String tableName = alterTableQuery.getTableName();
            final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(tableName);
            if (tableDefinition == null) {
                throw new DataAccessException("No such table exists :: " + tableName);
            }
            isTemplate = tableDefinition.isTemplate();
            if (isTemplate && !tableName.equals(tableDefinition.getTableName())) {
                throw new UnsupportedOperationException("Not Applicable.[" + tableName + "] is a template-instance");
            }
            if (isTemplate) {
                final TemplateMetaHandler templateMeta = MetaDataUtil.getTemplateHandler(tableDefinition.getModuleName());
                templateMeta.alterTemplate(alterTableQuery);
            }
            alterTableQuery.setTableDefinition(tableDefinition);
            if (TableArchiverUtil.isParticipatedInArchiveProcess(tableName)) {
                for (final AlterOperation ao : alterTableQuery.getAlterOperations()) {
                    final int alterType = ao.getOperationType();
                    String actualName = null;
                    switch (alterType) {
                        case 5:
                        case 7:
                        case 8:
                        case 11: {
                            actualName = SchemaBrowserUtil.getOriginalContraintName((String)ao.getAlterObject());
                            DataAccess.OUT.log(Level.INFO, "table participated in archive process... \n actual constarint name of [{0}] is [{1}]", new Object[] { ao.getAlterObject(), actualName });
                            ao.setActualConstraintName(actualName);
                            DataAccess.OUT.log(Level.INFO, "actual name in ao :::: {0}", ao.getActualConstraintName());
                            continue;
                        }
                        case 14: {
                            actualName = SchemaBrowserUtil.getOriginalContraintName(((ForeignKeyDefinition)ao.getAlterObject()).getName());
                            ao.setActualConstraintName(actualName);
                            continue;
                        }
                        case 16: {
                            actualName = SchemaBrowserUtil.getOriginalContraintName(((IndexDefinition)ao.getAlterObject()).getName());
                            ao.setActualConstraintName(actualName);
                            continue;
                        }
                        case 17: {
                            actualName = SchemaBrowserUtil.getOriginalContraintName(((PrimaryKeyDefinition)ao.getAlterObject()).getName());
                            ao.setActualConstraintName(actualName);
                            continue;
                        }
                        case 15: {
                            actualName = SchemaBrowserUtil.getOriginalContraintName(((UniqueKeyDefinition)ao.getAlterObject()).getName());
                            ao.setActualConstraintName(actualName);
                            continue;
                        }
                        case 2:
                        case 21: {
                            final String columnName = ((ColumnDefinition)ao.getAlterObject()).getColumnName();
                            final PrimaryKeyDefinition pkDef = tableDefinition.getPrimaryKey();
                            if (pkDef.getColumnList().contains(columnName)) {
                                actualName = SchemaBrowserUtil.getOriginalContraintName(pkDef.getName());
                            }
                            final List<UniqueKeyDefinition> ukDefList = tableDefinition.getUniqueKeys();
                            if (ukDefList != null) {
                                for (final UniqueKeyDefinition ukDef : ukDefList) {
                                    if (ukDef.getColumns().contains(columnName)) {
                                        actualName = SchemaBrowserUtil.getOriginalContraintName(pkDef.getName());
                                    }
                                }
                            }
                            ao.setActualConstraintName(actualName);
                            continue;
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            throw new DataAccessException(e.getMessage(), e);
        }
        try {
            try {
                if (!alterTableQuery.isValid()) {
                    alterTableQuery.validate();
                }
                if (validate) {
                    MetaDataUtil.validateAlterTableQuery(alterTableQuery);
                }
            }
            catch (final QueryConstructionException qce) {
                throw new DataAccessException("Exception occurred while validating the tableDefinition for the tableName :: [" + alterTableQuery.getTableName() + "]", qce);
            }
            preNotifyAllListeners(2, alterTableQuery);
            oldTransac1 = suspendTransaction();
            if (alterTableQuery.isExecutable() && !isTemplate) {
                relapi.alterTable(alterTableQuery);
            }
            final TableDefinition td_beforeUpdate = MetaDataUtil.getTableDefinitionByName(alterTableQuery.getTableName());
            for (final AlterOperation ao2 : alterTableQuery.getAlterOperations()) {
                if (ao2.getOperationType() == 3) {
                    final String columnName2 = (String)ao2.getAlterObject();
                    final ColumnDefinition colDef = td_beforeUpdate.getColumnDefinitionByName(columnName2);
                    if (colDef.getUniqueValueGeneration() == null) {
                        continue;
                    }
                    final Criteria cri = new Criteria(Column.getColumn("SeqGenState", "SEQNAME"), colDef.getUniqueValueGeneration().getGeneratorName(), 0);
                    delete(cri);
                    SequenceGeneratorRepository.remove(colDef.getUniqueValueGeneration().getGeneratorName());
                }
                else {
                    if (ao2.getOperationType() != 2) {
                        continue;
                    }
                    final ColumnDefinition newColDef = (ColumnDefinition)ao2.getAlterObject();
                    final ColumnDefinition oldColDef = td_beforeUpdate.getColumnDefinitionByName(newColDef.getColumnName());
                    if (oldColDef.getUniqueValueGeneration() != null && oldColDef.getUniqueValueGeneration().getGeneratorClass() == null && newColDef.getUniqueValueGeneration() != null && newColDef.getUniqueValueGeneration().getGeneratorClass() != null) {
                        final Object maxVal = getMaxValue(alterTableQuery.getTableName(), newColDef.getColumnName());
                        if (maxVal != null && Long.parseLong(maxVal.toString()) > 0L) {
                            final Long currentSize = Long.parseLong(maxVal.toString());
                            final Row row = new Row("SeqGenState");
                            row.set("SEQNAME", newColDef.getUniqueValueGeneration().getGeneratorName());
                            final DataObject dobj = get("SeqGenState", row);
                            if (!dobj.isEmpty()) {
                                final Row currow = dobj.getFirstRow("SeqGenState");
                                currow.set("CURRENTBATCHEND", currentSize);
                                dobj.updateRow(currow);
                                update(dobj);
                            }
                        }
                        SequenceGeneratorRepository.remove(oldColDef.getUniqueValueGeneration().getGeneratorName());
                    }
                    if (!ao2.isGeneratorNameNeedToBeRenamed()) {
                        continue;
                    }
                    final String oldGenName = oldColDef.getUniqueValueGeneration().getGeneratorName();
                    final String newGenName = newColDef.getUniqueValueGeneration().getGeneratorName();
                    SequenceGeneratorRepository.rename(oldGenName, newGenName, newColDef.getDataType(), newColDef.getUniqueValueGeneration().getGeneratorClass());
                }
            }
            alterDO = SchemaBrowserUtil.alterTableDefinition(alterTableQuery);
            Label_1202: {
                if (!alterDO.isEmpty()) {
                    getTransactionManager().begin();
                    try {
                        fillGeneratedValues(alterDO);
                        DataAccess.OUT.log(Level.FINE, "alterDO :: {0}", alterDO);
                        final DataObject upData = update(alterDO, false);
                        MetaDataUtil.alterTableDefinition(alterTableQuery);
                        getTransactionManager().commit();
                        break Label_1202;
                    }
                    catch (final Exception e2) {
                        getTransactionManager().rollback();
                        throw e2;
                    }
                }
                DataAccess.OUT.log(Level.SEVERE, "MetaData Information not found in the DB, hence this change [{0}] in the MetaData will not reflect in next startup, unless the respective data-dictionary.xml is changed accordingly.", alterTableQuery);
                MetaDataUtil.alterTableDefinition(alterTableQuery);
            }
            for (final AlterOperation ao2 : alterTableQuery.getAlterOperations()) {
                if (ao2.getOperationType() == 1) {
                    final ColumnDefinition cd = (ColumnDefinition)ao2.getAlterObject();
                    if (!cd.getDataType().equals("BIGINT") && !cd.getDataType().equals("INTEGER")) {
                        continue;
                    }
                    final UniqueValueGeneration uvg = cd.getUniqueValueGeneration();
                    if (uvg == null) {
                        continue;
                    }
                    final Object maxVal = getMaxValue(alterTableQuery.getTableName(), cd.getColumnName());
                    if (maxVal == null || Long.parseLong(maxVal.toString()) <= 0L) {
                        continue;
                    }
                    long currentSize2;
                    if (uvg.getGeneratorClass() != null) {
                        currentSize2 = Long.parseLong(maxVal.toString());
                    }
                    else {
                        currentSize2 = (Long.parseLong(maxVal.toString()) / 300L + 1L) * 300L;
                    }
                    final Row row2 = new Row("SeqGenState");
                    row2.set("SEQNAME", uvg.getGeneratorName());
                    row2.set("CURRENTBATCHEND", currentSize2);
                    final DataObject dobj2 = new WritableDataObject();
                    dobj2.addRow(row2);
                    add(dobj2);
                }
                else {
                    if (ao2.getOperationType() != 19) {
                        continue;
                    }
                    final ColumnDefinition cd = (ColumnDefinition)ao2.getAlterObject();
                    if (cd.getDefaultValue() == null) {
                        continue;
                    }
                    final UpdateQuery uq = new UpdateQueryImpl(cd.getTableName());
                    uq.setUpdateColumn(cd.getColumnName(), cd.getDefaultValue());
                    update(uq);
                }
            }
        }
        catch (final RuntimeException run) {
            throw run;
        }
        catch (final DataAccessException dae) {
            throw dae;
        }
        catch (final Exception e) {
            throw new DataAccessException(e.getMessage(), e);
        }
        finally {
            if (oldTransac1 != null) {
                DataAccess.OUT.log(Level.FINEST, "Resuming the suspended Transaction {0}", oldTransac1);
            }
            resumeTransaction(oldTransac1);
        }
        notifyAllListeners(2, alterTableQuery);
    }
    
    private static Object getMaxValue(final String tabName, final String colName) throws SQLException, QueryConstructionException {
        final RelationalAPI relapi = RelationalAPI.getInstance();
        final Table table = Table.getTable(tabName);
        final SelectQuery sq = new SelectQueryImpl(table);
        sq.addSelectColumn(Column.getColumn(table.getTableName(), colName).maximum());
        Connection c = null;
        DataSet ds = null;
        Object maxVal = null;
        try {
            c = relapi.getConnection();
            ds = relapi.executeQuery(sq, c);
            while (ds.next()) {
                maxVal = ds.getValue(1);
            }
        }
        finally {
            safeClose(ds);
            safeClose(c);
        }
        return maxVal;
    }
    
    public static void dropTable(final String tableName) throws DataAccessException, SQLException {
        dropTable(tableName, true);
    }
    
    static void dropTable(final String tableName, final boolean validate) throws DataAccessException, SQLException {
        final TableDefinition td = _getTableDefinitionByName(tableName);
        if (td.isTemplate() && !tableName.equals(td.getTableName())) {
            throw new UnsupportedOperationException("Not Applicable.[" + tableName + "] is a template-instance");
        }
        if (validate) {
            List slaveTables = null;
            try {
                slaveTables = getAllSlaveTables(tableName);
                if (slaveTables != null && slaveTables.size() > 0) {
                    throw new DataAccessException("This table [" + tableName + "] cannot be dropped because it has these " + slaveTables + " tables dependent on it.");
                }
                if (PersonalityConfigurationUtil.isPartOfPersonality(tableName)) {
                    throw new DataAccessException("This table [" + tableName + "] cannot be dropped, since it participates in some personalities");
                }
            }
            catch (final MetaDataException me) {
                throw new DataAccessException(me.getMessage(), me);
            }
        }
        if (td.isTemplate()) {
            try {
                final TemplateMetaHandler templateMeta = MetaDataUtil.getTemplateHandler(td.getModuleName());
                templateMeta.removeTemplate(tableName);
            }
            catch (final MetaDataException me2) {
                throw new DataAccessException(me2.getMessage(), me2);
            }
        }
        Transaction oldTransaction = null;
        try {
            oldTransaction = suspendTransaction();
            getTransactionManager().begin();
            try {
                final Long tableId = MetaDataUtil.getTableDefinitionByName(tableName).getTableID();
                Criteria criteria;
                if (tableId != null) {
                    criteria = new Criteria(Column.getColumn("TableDetails", "TABLE_ID"), tableId, 0);
                }
                else {
                    criteria = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), tableName, 0);
                }
                delete("TableDetails", criteria, false);
                if (!td.isTemplate()) {
                    RelationalAPI.getInstance().dropTable(tableName, false, null);
                }
                getTransactionManager().commit();
            }
            catch (final Exception e) {
                getTransactionManager().rollback();
                throw e;
            }
        }
        catch (final SQLException sqe) {
            throw sqe;
        }
        catch (final RuntimeException run) {
            throw run;
        }
        catch (final DataAccessException dae) {
            throw dae;
        }
        catch (final Exception e) {
            throw new DataAccessException(e.getMessage(), e);
        }
        finally {
            if (oldTransaction != null) {
                DataAccess.OUT.log(Level.FINEST, "Resuming the suspended Transaction {0}", oldTransaction);
            }
            resumeTransaction(oldTransaction);
        }
        try {
            SequenceGeneratorRepository.removeGeneratorValues(td);
            MetaDataUtil.removeTableDefinition(tableName);
        }
        catch (final PersistenceException pe) {
            throw new DataAccessException(pe.getMessage(), pe);
        }
        catch (final MetaDataException me) {
            throw new DataAccessException(me.getMessage(), me);
        }
        notifyAllListeners(3, td);
    }
    
    private static long[] getFirstRowFromDB(final String tableName, final String columnName) throws SQLException {
        final SelectQuery sq = new SelectQueryImpl(Table.getTable(tableName));
        sq.addSelectColumn(Column.getColumn(null, "*").count());
        if (columnName != null) {
            sq.addSelectColumn(Column.getColumn(tableName, columnName).distinct().count());
            sq.addSelectColumn(Column.getColumn(tableName, columnName).count());
        }
        final RelationalAPI relAPI = RelationalAPI.getInstance();
        Connection connection = null;
        DataSet ds = null;
        try {
            connection = relAPI.getConnection();
            try {
                ds = relAPI.executeQuery(sq, connection);
            }
            catch (final QueryConstructionException qce) {
                throw new SQLException(qce.getMessage());
            }
            final int size = ds.getColumnCount();
            final long[] returnObject = new long[size];
            ds.next();
            for (int i = 1; i <= size; ++i) {
                final Object val = ds.getValue(i);
                DataAccess.OUT.log(Level.FINEST, "Val :: " + val);
                DataAccess.OUT.log(Level.FINEST, "Instance of :: " + val.getClass().getName());
                returnObject[i - 1] = (long)((val != null) ? val : 0L);
            }
            return returnObject;
        }
        finally {
            safeClose(ds);
            safeClose(connection);
        }
    }
    
    private static boolean participatesInUKorFK(final String tableName, final String columnName) throws MetaDataException {
        final TableDefinition tableDefinition = MetaDataUtil.getTableDefinitionByName(tableName);
        final List unqList = tableDefinition.getUniqueKeys();
        if (unqList != null) {
            for (final UniqueKeyDefinition ukd : unqList) {
                final Iterator colIterator = ukd.getColumns().iterator();
                while (colIterator.hasNext()) {
                    if (colIterator.next().equals(columnName)) {
                        return true;
                    }
                }
            }
        }
        final List fkList = tableDefinition.getForeignKeyList();
        if (fkList != null) {
            for (final ForeignKeyDefinition fkd : fkList) {
                for (final ForeignKeyColumnDefinition fkcd : fkd.getForeignKeyColumns()) {
                    if (fkcd.getLocalColumnDefinition().getColumnName().equals(columnName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static void registerForMetaDataChanges(final MetaDataChangeListener listener) {
        DataAccess.listenerList.add(listener);
    }
    
    public static void unRegisterMetaDataChanges(final MetaDataChangeListener listener) {
        DataAccess.listenerList.remove(listener);
    }
    
    private static void notifyAllListeners(final int operationType, final Object notifyObject) {
        final Iterator iterator = DataAccess.listenerList.iterator();
        final MetaDataChangeEvent event = new MetaDataChangeEvent(notifyObject, operationType);
        while (iterator.hasNext()) {
            try {
                final MetaDataChangeListener listener = iterator.next();
                listener.metaDataChanged(event);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void preNotifyAllListeners(final int operationType, final Object notifyObject) {
        final Iterator iterator = DataAccess.listenerList.iterator();
        final MetaDataPreChangeEvent preEvent = new MetaDataPreChangeEvent(notifyObject, operationType);
        while (iterator.hasNext()) {
            try {
                final MetaDataChangeListener listener = iterator.next();
                listener.preMetaDataChange(preEvent);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private static Transaction suspendTransaction() throws DataAccessException {
        try {
            final Transaction tx = DataAccess.tm.getTransaction();
            return DataAccess.tm.suspend();
        }
        catch (final Exception exc) {
            DataAccess.OUT.log(Level.WARNING, exc.getMessage(), exc);
            throw new DataAccessException(exc.getMessage());
        }
    }
    
    private static void resumeTransaction(final Transaction oldTransaction) throws DataAccessException {
        if (oldTransaction != null) {
            try {
                DataAccess.tm.resume(oldTransaction);
            }
            catch (final Exception exc) {
                DataAccess.OUT.log(Level.WARNING, exc.getMessage(), exc);
                throw new DataAccessException(exc.getMessage());
            }
        }
    }
    
    private static TableDefinition _getTableDefinitionByName(final String tableName) {
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException exp) {
            throw new RuntimeException(exp);
        }
        if (td == null) {
            throw new IllegalArgumentException("The specified table [" + tableName + "] doesn't exists");
        }
        return td;
    }
    
    public static void createTableInstance(final String templateTableName, final String instanceId, final String createTableOptions) throws DataAccessException, SQLException {
        try {
            final TableDefinition template_td = _getTableDefinitionByName(templateTableName);
            final TableDefinition new_td = TemplateUtil.createTableDefnForTemplateInstance(template_td, instanceId);
            invokeCallbackHandlersFor(templateTableName, new_td);
            RelationalAPI.getInstance().createTable(new_td, createTableOptions, null);
            MetaDataUtil.addTemplateInstance(templateTableName, instanceId);
        }
        catch (final SQLException exp) {
            throw exp;
        }
        catch (final Exception exp2) {
            throw new DataAccessException(exp2.getMessage(), exp2);
        }
        notifyAllListeners(6, new Object[] { templateTableName, instanceId });
    }
    
    public static void dropTableInstance(final String templateTableName, final String instanceId) throws DataAccessException, SQLException {
        final TableDefinition td = _getTableDefinitionByName(templateTableName);
        try {
            if (!td.isTemplate()) {
                throw new IllegalArgumentException("Specified table [" + templateTableName + "] is NOT a template-table");
            }
            final String tableName = TemplateUtil.getTableName(templateTableName, instanceId);
            RelationalAPI.getInstance().dropTable(tableName, false, null);
            MetaDataUtil.removeTemplateInstance(templateTableName, instanceId);
        }
        catch (final MetaDataException me) {
            throw new DataAccessException(me.getMessage(), me);
        }
        try {
            SequenceGeneratorRepository.removeGeneratorValues(td, instanceId);
        }
        catch (final PersistenceException pe) {
            throw new DataAccessException(pe.getMessage(), pe);
        }
        notifyAllListeners(7, new Object[] { templateTableName, instanceId });
    }
    
    public static void addDataType(final DataTypeDefinition udtDefinition) throws DataAccessException {
        try {
            if (!udtDefinition.isValidated()) {
                udtDefinition.validate();
            }
            DataTypeManager.addDataType(udtDefinition.getDataType(), udtDefinition);
        }
        catch (final IOException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }
    
    @Deprecated
    public static void loadErrorCodes(final URL errorCodesURL) throws DataAccessException {
        try {
            PersistenceInitializer.loadErrorCodes(new File(errorCodesURL.toURI()).getAbsolutePath());
        }
        catch (final URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
    private static void initializeMigrationCallbackHandlerMap() throws RuntimeException {
        if (!(DataAccess.isCallbackHandlersEnabled = AppResources.getBoolean("enable.callbackhandlers"))) {
            return;
        }
        final Properties cbhClassNameVsTbNames = new Properties();
        InputStream fis = null;
        try {
            fis = new FileInputStream(new File(Configuration.getString("app.home") + "/conf/callback_handlers.props"));
            cbhClassNameVsTbNames.load(fis);
            for (final String className : ((Hashtable<Object, V>)cbhClassNameVsTbNames).keySet()) {
                final MigrationCallbackHandler mcbh = (MigrationCallbackHandler)Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
                final List<String> tbNames = new ArrayList<String>();
                String tableNameStr = cbhClassNameVsTbNames.getProperty(className);
                tableNameStr = tableNameStr.replaceAll(" ", "");
                final String[] split;
                final String[] tableNames = split = tableNameStr.split(",");
                for (final String tableName : split) {
                    if (DataAccess.tbNameVsCBH.containsKey(tableName)) {
                        throw new IllegalArgumentException("One table can have only one Callback Handler, but tableName :: [" + tableName + "]  is having more than one.");
                    }
                    DataAccess.tbNameVsCBH.put(tableName, mcbh);
                    tbNames.add(tableName);
                }
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final Exception e2) {
                    DataAccess.OUT.log(Level.WARNING, e2.getMessage(), e2);
                }
            }
        }
    }
    
    private static void invokeCallbackHandlersFor(final UpdateQuery updateQuery, final String tableName) throws DataAccessException {
        if (!DataAccess.isCallbackHandlersEnabled || DataAccess.invokeCBH.get() != null || DataAccess.tbNameVsCBH.get(tableName) == null) {
            return;
        }
        DataAccess.invokeCBH.set(Boolean.FALSE);
        try {
            DataAccess.tbNameVsCBH.get(tableName).handleMigrationForUpdate(updateQuery);
        }
        finally {
            DataAccess.invokeCBH.set(null);
        }
    }
    
    private static void invokeCallbackHandlersFor(final DeleteQuery deleteQuery, final String tableName) throws DataAccessException {
        if (!DataAccess.isCallbackHandlersEnabled || DataAccess.invokeCBH.get() != null || DataAccess.tbNameVsCBH.get(tableName) == null) {
            return;
        }
        DataAccess.invokeCBH.set(Boolean.FALSE);
        try {
            DataAccess.tbNameVsCBH.get(tableName).handleMigrationForDelete(deleteQuery);
        }
        finally {
            DataAccess.invokeCBH.set(null);
        }
    }
    
    private static void invokeCallbackHandlersFor(final Criteria criteria, final String tableName) throws DataAccessException {
        if (!DataAccess.isCallbackHandlersEnabled || DataAccess.invokeCBH.get() != null || DataAccess.tbNameVsCBH.get(tableName) == null) {
            return;
        }
        DataAccess.invokeCBH.set(Boolean.FALSE);
        try {
            DataAccess.tbNameVsCBH.get(tableName).handleMigrationForDelete(criteria);
        }
        finally {
            DataAccess.invokeCBH.set(null);
        }
    }
    
    private static void invokeCallbackHandlersFor(final DataObject data) throws DataAccessException {
        if (!DataAccess.isCallbackHandlersEnabled || DataAccess.invokeCBH.get() != null) {
            return;
        }
        final List<String> tableNames = data.getTableNames();
        final Map<MigrationCallbackHandler, List<String>> map = new HashMap<MigrationCallbackHandler, List<String>>();
        for (final String tableName : tableNames) {
            final MigrationCallbackHandler mcbh = DataAccess.tbNameVsCBH.get(tableName);
            if (mcbh != null) {
                List<String> tbNames = map.get(mcbh);
                if (tbNames == null) {
                    tbNames = new ArrayList<String>();
                    map.put(mcbh, tbNames);
                }
                tbNames.add(tableName);
            }
        }
        if (map.size() == 0) {
            return;
        }
        DataAccess.invokeCBH.set(Boolean.FALSE);
        try {
            for (final MigrationCallbackHandler mcbh2 : map.keySet()) {
                mcbh2.handleMigration(data, map.get(mcbh2));
            }
        }
        finally {
            DataAccess.invokeCBH.set(null);
        }
    }
    
    private static void invokeCallbackHandlersFor(final String templateTableName, final TableDefinition templateInstanceTD) throws DataAccessException {
        if (!DataAccess.isCallbackHandlersEnabled || DataAccess.invokeCBH.get() != null || DataAccess.tbNameVsCBH.get(templateTableName) == null) {
            return;
        }
        DataAccess.invokeCBH.set(Boolean.FALSE);
        try {
            DataAccess.tbNameVsCBH.get(templateTableName).handleMigrationForTemplateInstanceCreation(templateTableName, templateInstanceTD);
        }
        finally {
            DataAccess.invokeCBH.set(null);
        }
    }
    
    static {
        OUT = Logger.getLogger(DataAccess.class.getName());
        CLASS_NAME = DataAccess.class.getName();
        DataAccess.transactionMgrJNDIName = "java:/TransactionManager";
        DataAccess.convertableDataTypes = new HashMap<String, HashSet<String>>();
        HashSet<String> set = new HashSet<String>();
        set.add("BIGINT");
        set.add("FLOAT");
        set.add("DOUBLE");
        DataAccess.convertableDataTypes.put("INTEGER", set);
        set = new HashSet<String>();
        set.add("DOUBLE");
        DataAccess.convertableDataTypes.put("BIGINT", set);
        DataAccess.convertableDataTypes.put("FLOAT", set);
        set = new HashSet<String>();
        set.add("VARBINARY");
        set.add("BLOB");
        set.add("NCHAR");
        DataAccess.convertableDataTypes.put("CHAR", set);
        set = new HashSet<String>();
        set.add("BLOB");
        DataAccess.convertableDataTypes.put("VARBINARY", set);
        set = new HashSet<String>();
        set.add("DATETIME");
        set.add("TIMESTAMP");
        DataAccess.convertableDataTypes.put("DATE", set);
        set = new HashSet<String>();
        set.add("TIMESTAMP");
        DataAccess.convertableDataTypes.put("DATETIME", set);
        set = new HashSet<String>();
        set.add("DATETIME");
        DataAccess.convertableDataTypes.put("TIMESTAMP", set);
        DataAccess.dataSource = null;
        DataAccess.readOnlyMode = Boolean.getBoolean("app.readonly.mode");
        DataAccess.listenerList = new ArrayList();
        DataAccess.isCallbackHandlersEnabled = false;
        DataAccess.invokeCBH = new ThreadLocal<Boolean>();
        DataAccess.tbNameVsCBH = new HashMap<String, MigrationCallbackHandler>();
    }
}
