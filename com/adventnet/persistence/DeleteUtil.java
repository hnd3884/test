package com.adventnet.persistence;

import com.adventnet.ds.query.QueryConstants;
import java.util.HashSet;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.sql.PreparedStatement;
import java.sql.Statement;
import com.adventnet.db.archive.TableArchiverUtil;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.Function;
import java.util.Iterator;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Query;
import java.util.logging.Level;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.util.Stack;
import java.util.HashMap;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.internal.Operation;
import com.adventnet.ds.query.Criteria;
import java.util.Properties;
import java.util.logging.Logger;
import com.adventnet.db.api.RelationalAPI;

public class DeleteUtil
{
    private static final String CLASS_NAME;
    private static RelationalAPI relAPI;
    private static final Logger OUT;
    private static Logger err;
    private static Properties operationHandlerProps;
    private static ThreadLocal readOnly;
    private static ThreadLocal bdfkStack;
    
    public static OperationInfo getDeleteInfo(final String tableName, final Criteria criteria) throws DataAccessException {
        DeleteUtil.readOnly.set(Boolean.TRUE);
        deleteData(tableName, criteria);
        final WritableDataObject wdo = (WritableDataObject)Operation.getDataObject();
        if (wdo != null) {
            wdo.cleanupOnDeleteCascadeOperations();
        }
        final OperationInfo delInfo = Operation.getOperationInfo();
        DeleteUtil.readOnly.set(null);
        Operation.clearPKs();
        return delInfo;
    }
    
    public static OperationInfo getDeleteInfo(final DeleteQuery query) throws DataAccessException {
        DeleteUtil.readOnly.set(Boolean.TRUE);
        deleteData(query);
        final WritableDataObject wdo = (WritableDataObject)Operation.getDataObject();
        if (wdo != null) {
            wdo.cleanupOnDeleteCascadeOperations();
        }
        final OperationInfo delInfo = Operation.getOperationInfo();
        DeleteUtil.readOnly.set(null);
        Operation.clearPKs();
        return delInfo;
    }
    
    private static void delete(final String tableName, final Criteria criteria) throws DataAccessException {
    }
    
    private static void checkForSystemTable(final TableDefinition tabDef) throws DataAccessException {
        if (tabDef.isSystem()) {
            throw new DataAccessException("Data cannot be added/upated/deleted in system Table : " + tabDef.getTableName());
        }
    }
    
    static void delete(String tableName, Criteria criteria, final boolean checkSystem) throws DataAccessException {
        DeleteUtil.OUT.entering(DeleteUtil.CLASS_NAME, "delete", new Object[] { tableName, criteria });
        do {
            TableDefinition td = null;
            try {
                td = MetaDataUtil.getTableDefinitionByName(tableName);
                if (td == null) {
                    throw new DataAccessException("No table exists with name " + tableName);
                }
            }
            catch (final MetaDataException mde) {
                throw new DataAccessException(mde.getMessage(), mde);
            }
            if (checkSystem) {
                checkForSystemTable(td);
            }
            if (td.hasBDFK() || td.hasBDFKImpact() || PersonalityConfigurationUtil.isPartOfIndexedPersonality(tableName)) {
                deleteData(tableName, criteria);
            }
            executeDelete(tableName, criteria);
            HashMap map;
            for (criteria = null; criteria == null && !isBdfkStackEmpty(); criteria = getCondition(map)) {
                map = popBdfkStack();
            }
            if (criteria != null) {
                tableName = getTableName(criteria);
            }
        } while (criteria != null);
        DeleteUtil.OUT.exiting(DeleteUtil.CLASS_NAME, "delete");
    }
    
    static int delete(final DeleteQuery query, final boolean checkSystem) throws DataAccessException {
        int noOfRowsDeleted = 0;
        String tableName = query.getTableName();
        Criteria criteria = query.getCriteria();
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException(mde.getMessage(), mde);
        }
        if (checkSystem) {
            checkForSystemTable(td);
        }
        if (td.hasBDFK() || td.hasBDFKImpact()) {
            deleteData(query);
        }
        noOfRowsDeleted = executeDelete(query);
        HashMap map;
        for (criteria = null; criteria == null && !isBdfkStackEmpty(); criteria = getCondition(map)) {
            map = popBdfkStack();
        }
        if (criteria != null) {
            tableName = getTableName(criteria);
            delete(tableName, criteria, checkSystem);
        }
        DeleteUtil.OUT.exiting(DeleteUtil.CLASS_NAME, "delete");
        return noOfRowsDeleted;
    }
    
    private static Stack getBdfkStack() {
        return DeleteUtil.bdfkStack.get();
    }
    
    public static boolean isBdfkStackEmpty() {
        return getBdfkStack() == null || getBdfkStack().empty();
    }
    
    public static void setBdfkStack(final Stack stack) {
        DeleteUtil.bdfkStack.set(stack);
    }
    
    public static HashMap popBdfkStack() {
        return getBdfkStack().pop();
    }
    
    private static void pushBdfkStack(final HashMap map) {
        getBdfkStack().push(map);
    }
    
    public static Criteria getCondition(final HashMap map) throws DataAccessException {
        DeleteUtil.OUT.entering(DeleteUtil.CLASS_NAME, "getCondition", map);
        try {
            final ForeignKeyDefinition fk = map.get("ForeignKeyDefinition");
            final String slaveTableName = fk.getSlaveTableName();
            final Table slaveTable = Table.getTable(slaveTableName);
            final SelectQuery query = new SelectQueryImpl(slaveTable);
            final Column column = Column.getColumn(null, "*");
            query.addSelectColumn(column.count());
            final String masterTableName = fk.getMasterTableName();
            final TableDefinition masterTableDef = MetaDataUtil.getTableDefinitionByName(masterTableName);
            final List fkCols = fk.getForeignKeyColumns();
            final int colCount = fkCols.size();
            final int[] masterColPositions = new int[colCount];
            final Column[] masterCols = new Column[colCount];
            final List<String> columnNames = masterTableDef.getColumnNames();
            final String[] baseTableColumns = new String[colCount];
            final String[] referencedTableColumns = new String[colCount];
            for (int i = 0; i < colCount; ++i) {
                final ForeignKeyColumnDefinition fkCol = fkCols.get(i);
                final String masterColumnName = fkCol.getReferencedColumnDefinition().getColumnName();
                masterColPositions[i] = columnNames.indexOf(masterColumnName);
                masterCols[i] = Column.getColumn(masterTableName, masterColumnName);
                baseTableColumns[i] = fkCol.getLocalColumnDefinition().getColumnName();
                referencedTableColumns[i] = masterColumnName;
            }
            final Join join = new Join(slaveTableName, masterTableName, baseTableColumns, referencedTableColumns, 2);
            query.addJoin(join);
            final ArrayList candidateRows = map.get("CandidateRows");
            final int rowSize = candidateRows.size();
            final List pkColumns = masterTableDef.getPrimaryKey().getColumnList();
            final Object[] values = new Object[rowSize];
            Criteria returnedCriteria = null;
            final ArrayList addedCriteria = new ArrayList();
            for (int j = 0; j < rowSize; ++j) {
                final Row row = candidateRows.get(j);
                final Criteria criteria = getOneCriteria(row, masterColPositions, masterCols);
                query.setCriteria(criteria);
                DeleteUtil.OUT.log(Level.FINEST, "SQL formed to fetch the slave table rows is:{0}", query);
                Connection conn = null;
                DataSet ds = null;
                try {
                    conn = DeleteUtil.relAPI.getConnection();
                    ds = DeleteUtil.relAPI.executeQuery(query, conn);
                    ds.next();
                    if ((int)ds.getValue(1) == 0) {
                        DeleteUtil.OUT.log(Level.FINEST, "No Rows found from the slave table {0}.", slaveTableName);
                        final int pkColCount = pkColumns.size();
                        Criteria rowcriteria = null;
                        for (int k = 0; k < pkColCount; ++k) {
                            final String pkColumnName = pkColumns.get(k);
                            final Object pkColumnValue = row.get(pkColumnName);
                            if (rowcriteria == null) {
                                rowcriteria = new Criteria(Column.getColumn(masterTableName, pkColumnName), pkColumnValue, 0);
                            }
                            else {
                                rowcriteria = rowcriteria.and(new Criteria(Column.getColumn(masterTableName, pkColumnName), pkColumnValue, 0));
                            }
                        }
                        DeleteUtil.OUT.log(Level.FINEST, "Criteria formed for the row {0} is {1}.", new Object[] { row, rowcriteria });
                        if (!addedCriteria.contains(rowcriteria)) {
                            if (pkColumns.size() == 1) {
                                values[j] = rowcriteria.getValue();
                            }
                            else if (returnedCriteria == null) {
                                returnedCriteria = rowcriteria;
                            }
                            else {
                                returnedCriteria = returnedCriteria.or(rowcriteria);
                            }
                            addedCriteria.add(rowcriteria);
                        }
                    }
                    if (pkColumns.size() == 1) {
                        returnedCriteria = new Criteria(Column.getColumn(masterTableName, pkColumns.get(0)), values, 8);
                    }
                }
                catch (final QueryConstructionException qce) {
                    final String message = "QueryConstructionException occured while constructing SQL to retrieve rows.";
                    DeleteUtil.err.log(Level.FINE, message, qce);
                    DeleteUtil.err.log(Level.FINE, "SelectQuery being used, when the exception was thrown, is {0}", query);
                    throw new DataAccessException(message, qce);
                }
                catch (final SQLException sqle) {
                    final String message = "SQLException occured while retrieving candidate rows from the master table " + masterTableName;
                    DeleteUtil.err.log(Level.FINE, message, sqle);
                    DeleteUtil.err.log(Level.FINE, "SelectQuery being executed, when the exception was thrown, is {0}", query);
                    throw new DataAccessException(message, sqle);
                }
                finally {
                    safeClose(ds);
                    safeClose(conn);
                }
            }
            DeleteUtil.OUT.exiting(DeleteUtil.CLASS_NAME, "getCondition", returnedCriteria);
            return returnedCriteria;
        }
        catch (final MetaDataException exc) {
            throw new DataAccessException(exc);
        }
    }
    
    private static void deleteData(final String tableName, final Criteria criteria) throws DataAccessException {
        DeleteUtil.OUT.entering(DeleteUtil.CLASS_NAME, "deleteData", new Object[] { tableName, criteria });
        if (DeleteUtil.readOnly.get() != null) {
            Operation.addDeleteCriteria(criteria);
        }
        final SelectQuery sq = new SelectQueryImpl(Table.getTable(tableName));
        sq.setCriteria(criteria);
        fetchRows(sq, tableName, null, 1);
        DeleteUtil.OUT.log(Level.FINEST, "Completed deleteData for the table {0} and the criteria {1}.", new Object[] { tableName, criteria });
        DeleteUtil.OUT.exiting(DeleteUtil.CLASS_NAME, "deleteData");
    }
    
    private static void deleteData(final DeleteQuery query) throws DataAccessException {
        DeleteUtil.OUT.entering(DeleteUtil.CLASS_NAME, "deleteData by query", query);
        final String tableName = query.getTableName();
        final Criteria criteria = query.getCriteria();
        final List sortColumns = query.getSortColumns();
        final List<Join> joins = query.getJoins();
        final int limit = query.getLimit();
        if (DeleteUtil.readOnly.get() != null) {
            Operation.addDeleteQuery(query);
        }
        final SelectQuery sq = new SelectQueryImpl(Table.getTable(tableName));
        sq.setCriteria(criteria);
        for (final Join join : joins) {
            sq.addJoin(join);
        }
        if (limit > 0) {
            final String dbName = PersistenceInitializer.getConfigurationValue("DBName");
            final Range range = new Range(1, limit);
            sq.setRange(range);
            if (!sortColumns.isEmpty()) {
                sq.addSortColumns(sortColumns);
            }
            else if (dbName != null && dbName.equals("mssql")) {
                throw new DataAccessException("In mssql, Cannot construct range query without 'ORDER BY' clause");
            }
        }
        fetchRows(sq, tableName, null, 1);
        DeleteUtil.OUT.log(Level.FINEST, "Completed deleteData for the Query {0}.", query);
        DeleteUtil.OUT.exiting(DeleteUtil.CLASS_NAME, "deleteData by query");
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
        else if (criteria.getColumn() instanceof com.adventnet.ds.query.Operation) {
            final com.adventnet.ds.query.Operation oper = (com.adventnet.ds.query.Operation)criteria.getColumn();
            Object obj2 = oper.getLHSArgument();
            Column col = null;
            while (obj2 != null) {
                if (!(obj2 instanceof Operation)) {
                    col = (Column)obj2;
                    break;
                }
                obj2 = oper.getLHSArgument();
            }
            if (oper.getRHSArgument() instanceof Column) {
                col = (Column)oper.getRHSArgument();
            }
            return col.getTableAlias();
        }
        return criteria.getColumn().getTableAlias();
    }
    
    public static void executeDelete(final String tableName, final Criteria criteria) throws DataAccessException {
        Connection conn = null;
        final String deleteSQL = null;
        final List<String> tables = new ArrayList<String>(1);
        tables.add(tableName);
        try {
            if (criteria != null) {
                final List<Table> tableList = new ArrayList<Table>();
                tableList.add(Table.getTable(tableName));
                QueryUtil.setTypeForCriteria(criteria, tableList);
                criteria.validateInput();
            }
            conn = DeleteUtil.relAPI.getConnection();
            PersistenceUtil.handlePreExec(tables, conn, null);
            DeleteUtil.relAPI.executeDelete(tableName, criteria, conn);
        }
        catch (final QueryConstructionException qce) {
            DeleteUtil.err.log(Level.FINE, "QueryConstructionException occured, while constructing SQL to delete rows from table {0}. Criteria being used is {1}", new Object[] { tableName, criteria });
            DeleteUtil.err.log(Level.FINE, "QueryConstructionException is thrown", qce);
            throw new DataAccessException("QueryConstructionException is thrown, while generating delete SQL", qce);
        }
        catch (final SQLException sqle) {
            final String message = "Exception occured while executing SQL to delete rows";
            DeleteUtil.err.log(Level.FINE, "{0}. SQL executed: {1} ", new Object[] { message, deleteSQL });
            DeleteUtil.err.log(Level.FINE, "", sqle);
            try {
                if (TableArchiverUtil.isArchiveEnabled()) {
                    DeleteUtil.relAPI.getArchiveAdapter().restoreUnArchivedInvisibleTable(tableName, conn, sqle);
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            throw new DataAccessException(message, sqle);
        }
        finally {
            if (conn != null) {
                PersistenceUtil.handlePostExec(tables, conn, null);
            }
            safeClose(conn);
        }
    }
    
    public static int executeDelete(final DeleteQuery query) throws DataAccessException {
        Connection conn = null;
        String deleteSQL = null;
        PreparedStatement pstmt = null;
        final List<String> tables = new ArrayList<String>(1);
        tables.add(query.getTableName());
        try {
            conn = DeleteUtil.relAPI.getConnection();
            PersistenceUtil.handlePreExec(tables, conn, null);
            deleteSQL = DeleteUtil.relAPI.getDeleteSQL(query);
            DeleteUtil.OUT.log(Level.FINEST, "Executing delete SQL: {0}", deleteSQL);
            pstmt = conn.prepareStatement(deleteSQL);
            return DeleteUtil.relAPI.executeUpdate(pstmt);
        }
        catch (final QueryConstructionException qce) {
            DeleteUtil.err.log(Level.FINE, "QueryConstructionException is thrown", qce);
            throw new DataAccessException("QueryConstructionException while generating delete SQL ::" + qce.getMessage(), qce);
        }
        catch (final SQLException sqle) {
            final String message = "Exception occured while executing SQL to delete rows";
            DeleteUtil.err.log(Level.FINE, "{0}. SQL executed: {1} ", new Object[] { message, deleteSQL });
            DeleteUtil.err.log(Level.FINE, "", sqle);
            throw new DataAccessException(message, sqle);
        }
        finally {
            safeClose(pstmt);
            if (conn != null) {
                PersistenceUtil.handlePostExec(tables, conn, null);
            }
            safeClose(conn);
        }
    }
    
    private static void fetchRows(final SelectQuery sq, final String tableAlias, final String[] fkCols, final int foreignKeyConstraint) throws DataAccessException {
        final int maxRowsPerTable = Integer.parseInt(DeleteUtil.operationHandlerProps.getProperty("max-rows-per-table", "50"));
        final Range range = new Range(0, maxRowsPerTable + 1);
        sq.setRange(range);
        if (sq.getSortColumns().isEmpty()) {
            try {
                final String tableName = sq.getTableNameForTableAlias(tableAlias);
                final List<String> pkColumns = MetaDataUtil.getTableDefinitionByName(tableName).getPrimaryKey().getColumnList();
                final List<SortColumn> sortColumns = new ArrayList<SortColumn>();
                pkColumns.forEach(pkColumnName -> sortColumns.add(new SortColumn(tableAlias, pkColumnName, true)));
                sq.addSortColumns(sortColumns);
            }
            catch (final Exception e) {
                DeleteUtil.OUT.log(Level.INFO, "Exception while getting table definition in delete operation - " + e.getMessage());
            }
        }
        DeleteUtil.OUT.entering(DeleteUtil.CLASS_NAME, "fetchRows", new Object[] { sq, tableAlias });
        final ArrayList rowsList = new ArrayList();
        final String tableName2 = getTableName(sq, tableAlias);
        try {
            final TableDefinition tabDefn = MetaDataUtil.getTableDefinitionByName(tableName2);
            sq.addSelectColumn(Column.getColumn(tableName2, "*"));
            final DataObject dobj = DataAccess.get(sq);
            Iterator rows = dobj.getRows(tableName2);
            while (rows.hasNext()) {
                final Row row = rows.next();
                if (DeleteUtil.readOnly.get() != null) {
                    if (foreignKeyConstraint == 3 && fkCols != null) {
                        for (final String fkCol : fkCols) {
                            final Object defaultValue = tabDefn.getColumnDefinitionByName(fkCol).getDefaultValue();
                            row.set(fkCol, defaultValue);
                        }
                    }
                    else if (foreignKeyConstraint == 2 && fkCols != null) {
                        for (final String fkCol : fkCols) {
                            row.set(fkCol, null);
                        }
                    }
                    Operation.addRow(3, row, foreignKeyConstraint);
                }
                rowsList.add(row);
            }
            if (dobj.size(tableName2) <= 0) {
                DeleteUtil.OUT.log(Level.FINEST, "No rows have to be deleted from the table {0}. Returning.", tableName2);
                DeleteUtil.OUT.exiting(DeleteUtil.CLASS_NAME, "fetchRows");
                return;
            }
            if (tabDefn.hasBDFK()) {
                loadCandidateMasterRows(tableName2, rowsList);
            }
            if (DeleteUtil.readOnly.get() != null) {
                Operation.addSelectQuery((SelectQuery)sq.clone());
            }
            cleanUpIndex(tableName2, rowsList);
            rows = null;
        }
        catch (final MetaDataException excp) {
            final String message = "MetaDataException while getting information from DataDictionary";
            DeleteUtil.err.log(Level.FINE, message, excp);
            throw new DataAccessException(message, excp);
        }
        try {
            final List referringFKs = MetaDataUtil.getReferringForeignKeyDefinitions(tableName2);
            if (referringFKs != null) {
                for (int size = referringFKs.size(), i = 0; i < size; ++i) {
                    final ForeignKeyDefinition fk = referringFKs.get(i);
                    final TableDefinition td = MetaDataUtil.getTableDefinitionByName(fk.getSlaveTableName());
                    if (td == null || !td.isTemplate()) {
                        fetchRowsForSlave(sq, tableAlias, fk);
                    }
                }
            }
        }
        catch (final MetaDataException excp) {
            final String message = "MetaDataException while getting information from DataDictionary";
            DeleteUtil.err.log(Level.FINE, message, excp);
            throw new DataAccessException(message, excp);
        }
        DeleteUtil.OUT.exiting(DeleteUtil.CLASS_NAME, "fetchRows");
    }
    
    private static void loadCandidateMasterRows(final String slaveTableName, final ArrayList rows) throws DataAccessException {
        DeleteUtil.OUT.entering(DeleteUtil.CLASS_NAME, "loadCandidateMasterRows");
        try {
            final TableDefinition slaveTableDef = MetaDataUtil.getTableDefinitionByName(slaveTableName);
            final List fkList = slaveTableDef.getForeignKeyList();
            if (fkList == null) {
                DeleteUtil.OUT.log(Level.FINEST, "No foreign key definition for the {0} table. Skipping loadCandidateMasterRows.", slaveTableName);
                DeleteUtil.OUT.exiting(DeleteUtil.CLASS_NAME, "loadCandidateMasterRows");
                return;
            }
            final List<String> columnNames = slaveTableDef.getColumnNames();
            for (int size = fkList.size(), i = 0; i < size; ++i) {
                final ForeignKeyDefinition fk = fkList.get(i);
                if (!fk.isBidirectional()) {
                    DeleteUtil.OUT.log(Level.FINEST, "The foreign key definition {0} is not defined as bi-directional. Skipping loadCandidateMasterRows.", fk.getName());
                }
                else {
                    final String masterTableName = fk.getMasterTableName();
                    final SelectQuery query = new SelectQueryImpl(Table.getTable(masterTableName));
                    final TableDefinition masterTableDef = MetaDataUtil.getTableDefinitionByName(masterTableName);
                    final List pkColumns = masterTableDef.getPrimaryKey().getColumnList();
                    for (final String pkColumnName : pkColumns) {
                        final Column column = Column.getColumn(masterTableName, pkColumnName);
                        query.addSelectColumn(column);
                    }
                    final List fkCols = fk.getForeignKeyColumns();
                    final int colCount = fkCols.size();
                    final int[] slaveColPositions = new int[colCount];
                    final Column[] slaveCols = new Column[colCount];
                    final String[] baseTableColumns = new String[colCount];
                    final String[] referencedTableColumns = new String[colCount];
                    for (int j = 0; j < colCount; ++j) {
                        final ForeignKeyColumnDefinition fkCol = fkCols.get(j);
                        final String slaveColumnName = fkCol.getLocalColumnDefinition().getColumnName();
                        slaveColPositions[j] = columnNames.indexOf(slaveColumnName);
                        slaveCols[j] = Column.getColumn(slaveTableName, slaveColumnName);
                        baseTableColumns[j] = fkCol.getReferencedColumnDefinition().getColumnName();
                        referencedTableColumns[j] = slaveColumnName;
                    }
                    final Criteria criteria = getCriteria(rows, slaveColPositions, slaveCols);
                    query.setCriteria(criteria);
                    final Join join = new Join(masterTableName, slaveTableName, baseTableColumns, referencedTableColumns, 2);
                    query.addJoin(join);
                    DeleteUtil.OUT.log(Level.FINEST, "SQL formed to fetch the master table candidate rows is:{0}", query);
                    Connection conn = null;
                    DataSet ds = null;
                    try {
                        conn = DeleteUtil.relAPI.getConnection();
                        ds = DeleteUtil.relAPI.executeQuery(query, conn);
                        final List<String> masterColumnNames = masterTableDef.getColumnNames();
                        final int pkColCount = pkColumns.size();
                        final ArrayList candidateRows = new ArrayList();
                        while (ds.next()) {
                            final Row candidateRow = new Row(masterTableName);
                            for (int k = 0; k < pkColCount; ++k) {
                                candidateRow.set(pkColumns.get(k), ds.getValue(k + 1));
                            }
                            if (!candidateRows.contains(candidateRow)) {
                                candidateRows.add(candidateRow);
                            }
                        }
                        DeleteUtil.OUT.log(Level.FINEST, "Rows retrieved from the master table {0} are {1}", new Object[] { masterTableName, candidateRows });
                        if (!candidateRows.isEmpty()) {
                            final HashMap map = new HashMap();
                            map.put("ForeignKeyDefinition", fk);
                            map.put("CandidateRows", candidateRows);
                            pushBdfkStack(map);
                        }
                    }
                    catch (final QueryConstructionException qce) {
                        final String message = "QueryConstructionException occured while constructing SQL to retrieve rows.";
                        DeleteUtil.err.log(Level.FINE, message, qce);
                        DeleteUtil.err.log(Level.FINE, "SelectQuery being used, when the exception was thrown, is {0}", query);
                        throw new DataAccessException(message, qce);
                    }
                    catch (final SQLException sqle) {
                        final String message = "SQLException occured while retrieving candidate rows from the master table " + masterTableName;
                        DeleteUtil.err.log(Level.FINE, message, sqle);
                        DeleteUtil.err.log(Level.FINE, "SelectQuery being executed, when the exception was thrown, is {0}", query);
                        throw new DataAccessException(message, sqle);
                    }
                    finally {
                        safeClose(ds);
                        safeClose(conn);
                    }
                }
            }
        }
        catch (final DataAccessException exc) {
            throw exc;
        }
        catch (final Exception exc2) {
            final String message2 = "Exception while loading candidate rows of the master tables.";
            DeleteUtil.err.log(Level.FINE, message2, exc2);
            throw new DataAccessException(message2, exc2);
        }
        finally {
            DeleteUtil.OUT.exiting(DeleteUtil.CLASS_NAME, "loadCandidateMasterRows");
        }
    }
    
    private static List getColumnNames(final String tableName) throws DataAccessException {
        final List columnNames = new ArrayList();
        try {
            final TableDefinition tableDefn = MetaDataUtil.getTableDefinitionByName(tableName);
            final List colDefns = tableDefn.getColumnList();
            for (int i = 0; i < colDefns.size(); ++i) {
                final String columnName = colDefns.get(i).getColumnName();
                columnNames.add(columnName);
            }
        }
        catch (final MetaDataException excp) {
            final String message = "MetaDataException while getting information from DataDictionary";
            DeleteUtil.err.log(Level.FINE, message, excp);
            throw new DataAccessException(message, excp);
        }
        return columnNames;
    }
    
    private static void fetchRowsForSlave(final SelectQuery masterSQ, final String masterTableAlias, final ForeignKeyDefinition fk) throws DataAccessException {
        DeleteUtil.OUT.entering(DeleteUtil.CLASS_NAME, "fetchRowsForSlave", new Object[] { masterSQ, masterTableAlias, fk.getName() });
        final SelectQuery sq = (SelectQuery)masterSQ.clone();
        final List oldSelectColList = sq.getSelectColumns();
        if (oldSelectColList != null) {
            for (int i = oldSelectColList.size() - 1; i >= 0; --i) {
                sq.removeSelectColumn(i);
            }
        }
        String slaveTableAlias;
        final String slaveTableName = slaveTableAlias = fk.getSlaveTableName();
        final List fkCols = fk.getForeignKeyColumns();
        final int size = fkCols.size();
        final String[] masterCols = new String[size];
        final String[] slaveCols = new String[size];
        for (int j = 0; j < size; ++j) {
            final ForeignKeyColumnDefinition fkCol = fkCols.get(j);
            slaveCols[j] = fkCol.getLocalColumnDefinition().getColumnName();
            masterCols[j] = fkCol.getReferencedColumnDefinition().getColumnName();
        }
        for (int counter = 0; containsAlias(sq, slaveTableAlias); slaveTableAlias = "t" + counter) {
            ++counter;
        }
        final Join join = new Join(fk.getMasterTableName(), slaveTableName, masterCols, slaveCols, masterTableAlias, slaveTableAlias, 2);
        sq.addJoin(join);
        switch (fk.getConstraints()) {
            case 1: {
                fetchRows(sq, slaveTableAlias, slaveCols, 1);
                break;
            }
            case 2: {
                fetchRows(sq, slaveTableAlias, slaveCols, 2);
                break;
            }
            case 3: {
                fetchRows(sq, slaveTableAlias, slaveCols, 3);
                break;
            }
        }
        DeleteUtil.OUT.exiting(DeleteUtil.CLASS_NAME, "fetchRowsForSlave");
    }
    
    private static boolean containsAlias(final SelectQuery sq, final String tableAlias) {
        final List tables = sq.getTableList();
        for (int i = 0; i < tables.size(); ++i) {
            final Table table = tables.get(i);
            if (tableAlias.equals(table.getTableAlias())) {
                return true;
            }
        }
        return false;
    }
    
    private static Criteria getCriteria(final List rows, final int[] colPositions, final Column[] critCols) {
        DeleteUtil.OUT.entering(DeleteUtil.CLASS_NAME, "getCriteria", rows);
        Criteria returnCriteria = null;
        final int size = rows.size();
        final int colCount = colPositions.length;
        if (colCount == 1) {
            if (size == 0) {
                return null;
            }
            Object[] values = new Object[size];
            for (int i = 0; i < size; ++i) {
                final Row row = rows.get(i);
                values[i] = row.getValues().get(colPositions[0]);
            }
            values = removeRedundentValues(values);
            returnCriteria = new Criteria(critCols[0], values, 8);
        }
        else {
            for (int j = 0; j < size; ++j) {
                final Row row2 = rows.get(j);
                final Criteria oneCriteria = getOneCriteria(row2, colPositions, critCols);
                if (returnCriteria == null) {
                    returnCriteria = oneCriteria;
                }
                else {
                    returnCriteria = returnCriteria.or(oneCriteria);
                }
            }
        }
        DeleteUtil.OUT.exiting(DeleteUtil.CLASS_NAME, "getCriteria", returnCriteria);
        return returnCriteria;
    }
    
    private static Object[] removeRedundentValues(final Object[] values) {
        final int length = values.length;
        final HashSet set = new HashSet();
        for (int i = 0; i < length; ++i) {
            set.add(values[i]);
        }
        return set.toArray();
    }
    
    private static Criteria getOneCriteria(final Row row, final int[] colPositions, final Column[] columns) {
        Criteria returnCriteria = null;
        for (int i = 0; i < colPositions.length; ++i) {
            final List values = row.getValues();
            final Object value = values.get(colPositions[i]);
            final Criteria criteria = new Criteria(columns[i], value, 0);
            if (returnCriteria == null) {
                returnCriteria = criteria;
            }
            else {
                returnCriteria = returnCriteria.and(criteria);
            }
        }
        return returnCriteria;
    }
    
    private static void cleanUpIndex(final String tableName, final List deletedRows) throws DataAccessException {
        DeleteUtil.OUT.log(Level.FINEST, "cleanUpIndex :: tableName :: {0},   deletedRows :: {1}", new Object[] { tableName, deletedRows });
        String idxTableName = null;
        if (PersonalityConfigurationUtil.isPartOfPersonality(tableName)) {
            idxTableName = PersistenceUtil.getIndexTableName(tableName);
        }
        if (idxTableName == null) {
            DeleteUtil.OUT.log(Level.FINEST, "Table {0} is not indexed", tableName);
            return;
        }
        final String dominantTable = PersistenceUtil.getDominantTable(tableName);
        if (tableName.equals(dominantTable)) {
            DeleteUtil.OUT.log(Level.FINEST, "Table {0} is the dominant table itself. Avoiding Index cleanup", tableName);
            return;
        }
        final Join joinWithDominantTable = PersistenceUtil.getJoinWithDominantTable(tableName);
        String[] dominantColumns = null;
        String[] slaveColumns = null;
        if (joinWithDominantTable != null) {
            dominantColumns = PersistenceUtil.getColumns(joinWithDominantTable, true);
            slaveColumns = PersistenceUtil.getColumns(joinWithDominantTable, false);
        }
        Criteria criteria = null;
        final TableDefinition td = QueryConstructor.getTableDefinition(idxTableName);
        final List columnList = td.getColumnList();
        final int size = columnList.size();
        for (int i = 0; i < size - 1; ++i) {
            final ColumnDefinition cd = columnList.get(i);
            String columnName = null;
            if (joinWithDominantTable == null) {
                columnName = cd.getColumnName();
            }
            else {
                final String pkColumnName = cd.getColumnName();
                final int index = PersistenceUtil.indexOf(dominantColumns, pkColumnName);
                columnName = joinWithDominantTable.getReferencedTableColumn(index);
            }
            final Column column = Column.getColumn(tableName, columnName);
            final Criteria oneCriteria = new Criteria(column, QueryConstants.PREPARED_STMT_CONST, 0);
            if (criteria == null) {
                criteria = oneCriteria;
            }
            else {
                criteria = criteria.and(oneCriteria);
            }
        }
        DeleteUtil.OUT.log(Level.FINEST, "Criteria formed to check if more rows present with dominant PK is {0}", criteria);
        Connection conn = null;
        PreparedStatement ps = null;
        String sqlToCheckPresence = null;
        SelectQuery sq = null;
        try {
            conn = DeleteUtil.relAPI.getConnection();
            sq = new SelectQueryImpl(Table.getTable(tableName));
            final Column cntColumn = Column.getColumn(null, "*").count();
            cntColumn.setColumnAlias("CNT");
            sq.addSelectColumn(cntColumn);
            sq.setCriteria(criteria);
            sqlToCheckPresence = DeleteUtil.relAPI.getSelectSQL(sq);
            DeleteUtil.OUT.log(Level.FINEST, "SQL formed to check if more rows present with dominant PK is {0}", sqlToCheckPresence);
            ps = conn.prepareStatement(sqlToCheckPresence);
            for (int rowCount = deletedRows.size(), j = 0; j < rowCount; ++j) {
                final Row row = deletedRows.get(j);
                DataSet ds = null;
                try {
                    for (int k = 0; k < size - 1; ++k) {
                        final ColumnDefinition cd2 = columnList.get(k);
                        String columnName2 = null;
                        if (joinWithDominantTable == null) {
                            columnName2 = cd2.getColumnName();
                        }
                        else {
                            final String pkColumnName2 = cd2.getColumnName();
                            final int index2 = PersistenceUtil.indexOf(dominantColumns, pkColumnName2);
                            columnName2 = joinWithDominantTable.getReferencedTableColumn(index2);
                        }
                        final Object value = row.get(columnName2);
                        ps.setObject(k + 1, value);
                    }
                    ds = DeleteUtil.relAPI.executeQuery(ps, sq);
                    if (ds.next() && new Long(String.valueOf(ds.getValue(1))) > 1L) {
                        DeleteUtil.OUT.log(Level.FINEST, "More rows with dominant PK found corresponding to row {0}", row);
                    }
                    else {
                        DeleteUtil.OUT.log(Level.FINEST, "No extra rows with dominant PK found corresponding to row {0}. Hence, index corresponding to this row will be deleted.", row);
                        final Row idxRow = new Row(idxTableName);
                        for (int l = 0; l < size - 1; ++l) {
                            final ColumnDefinition cd3 = columnList.get(l);
                            String columnName3 = null;
                            if (joinWithDominantTable == null) {
                                columnName3 = cd3.getColumnName();
                            }
                            else {
                                final String pkColumnName3 = cd3.getColumnName();
                                final int index3 = PersistenceUtil.indexOf(dominantColumns, pkColumnName3);
                                columnName3 = joinWithDominantTable.getReferencedTableColumn(index3);
                            }
                            final Object value2 = row.get(columnName3);
                            idxRow.set(l + 1, value2);
                        }
                        idxRow.set(size, tableName);
                        final Criteria idxCriteria = QueryConstructor.formCriteria(idxRow);
                        if (DeleteUtil.readOnly.get() != null) {
                            Operation.addDeleteCriteria(idxCriteria);
                        }
                        else {
                            executeDelete(idxTableName, idxCriteria);
                        }
                    }
                }
                catch (final SQLException sqle) {
                    final String message = "SQLException occured while cleaning up rows that are to be deleted from the index table " + idxTableName;
                    DeleteUtil.err.log(Level.FINER, message, sqle);
                    DeleteUtil.err.log(Level.FINER, "SelectQuery being executed, when the exception was thrown, is {0}", sqlToCheckPresence);
                    throw new DataAccessException(message, sqle);
                }
                finally {
                    safeClose(ds);
                }
            }
        }
        catch (final SQLException sqle2) {
            final String message2 = "SQLException occured while cleaning up rows that are to be deleted from the index table " + idxTableName;
            DeleteUtil.err.log(Level.FINER, message2, sqle2);
            DeleteUtil.err.log(Level.FINER, "SQL being executed, when the exception was thrown, is {0}", sqlToCheckPresence);
            throw new DataAccessException(message2, sqle2);
        }
        catch (final QueryConstructionException qce) {
            DeleteUtil.err.log(Level.FINE, "QueryConstructionException occured, while constructing SQL to delete rows from index for the table {0}. Criteria being used is {1}", new Object[] { tableName, criteria });
            DeleteUtil.err.log(Level.FINE, "QueryConstructionException is thrown", qce);
            throw new DataAccessException(qce);
        }
        finally {
            safeClose(ps);
            safeClose(conn);
        }
    }
    
    private static String getTableName(final SelectQuery sq, final String tableAlias) throws DataAccessException {
        final List tableList = sq.getTableList();
        for (int size = tableList.size(), i = 0; i < size; ++i) {
            final Table table = tableList.get(i);
            if (table.getTableAlias().equals(tableAlias)) {
                return table.getTableName();
            }
        }
        DeleteUtil.OUT.log(Level.FINER, "Table alias {0} is not found the in the passed SelectQuery: {1}", new Object[] { tableAlias, sq });
        throw new DataAccessException("Table alias " + tableAlias + " is not found the in the passed SelectQuery");
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
            DeleteUtil.OUT.log(Level.FINEST, "Exception occured while closing DataSet {0}", ds);
            DeleteUtil.OUT.log(Level.FINEST, "Exception Stack trace:", exc);
        }
    }
    
    static {
        CLASS_NAME = DeleteUtil.class.getName();
        DeleteUtil.relAPI = RelationalAPI.getInstance();
        OUT = Logger.getLogger(DeleteUtil.CLASS_NAME);
        DeleteUtil.err = Logger.getLogger(DeleteUtil.CLASS_NAME);
        DeleteUtil.operationHandlerProps = PersistenceInitializer.getConfigurationProps("OperationHandler");
        DeleteUtil.readOnly = new ThreadLocal();
        DeleteUtil.bdfkStack = new ThreadLocal();
    }
}
