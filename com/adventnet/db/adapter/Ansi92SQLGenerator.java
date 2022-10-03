package com.adventnet.db.adapter;

import java.util.Hashtable;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.parser.ParseException;
import com.adventnet.ds.query1.DeleteSqlObject;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.util.DCManager;
import java.util.HashMap;
import java.util.Locale;
import java.util.Collections;
import java.sql.SQLException;
import com.adventnet.ds.query.BulkLoad;
import com.adventnet.ds.query.CreateTableLike;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.sql.Timestamp;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.util.Date;
import java.text.MessageFormat;
import com.zoho.conf.AppResources;
import com.adventnet.ds.query.CaseExpression;
import java.lang.reflect.Array;
import com.adventnet.ds.query.LocaleColumn;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.GroupByClause;
import java.util.Collection;
import com.adventnet.ds.query.ArchiveTable;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.IndexHintClause;
import com.adventnet.ds.query.Operation;
import com.adventnet.ds.query.Function;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import com.adventnet.db.persistence.metadata.IndexColumnDefinition;
import java.util.StringJoiner;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.UnionQuery;
import com.adventnet.ds.query.Query;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.Iterator;
import java.util.Set;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.ds.query.Column;
import java.util.LinkedHashMap;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.logging.Level;
import java.util.ArrayList;
import com.zoho.mickey.db.SQLModifier;
import java.util.Map;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class Ansi92SQLGenerator implements SQLGenerator
{
    private static final Logger OUT;
    static String stringWildCard;
    static String charWildCard;
    static final Pattern NUMBER_PATTERN;
    static final Pattern DATE_PATTERN;
    protected static boolean isAutoQuoteEnabled;
    protected Properties functionTemplates;
    protected List<String> reservedKeyWords;
    protected String dbType;
    private Map<String, DCSQLGenerator> dcTypeVsSQLGenerator;
    private SQLModifier sqlModifier;
    protected Boolean noAppendForVarchar;
    boolean isCEContainsSubQuery;
    String encrytionKey;
    
    public Ansi92SQLGenerator() {
        this.functionTemplates = new Properties();
        this.reservedKeyWords = new ArrayList<String>();
        this.dbType = null;
        this.dcTypeVsSQLGenerator = null;
        this.sqlModifier = null;
        this.noAppendForVarchar = Boolean.FALSE;
        this.isCEContainsSubQuery = false;
        this.encrytionKey = null;
    }
    
    protected void initializeSQLModifier(final SQLModifier sqlModi) {
        if (this.sqlModifier == null) {
            this.sqlModifier = sqlModi;
        }
        else {
            Ansi92SQLGenerator.OUT.log(Level.WARNING, "SQLModifier has been already initialized for this JVM.");
        }
    }
    
    @Override
    public String getSQLForInsert(final String tableName, final Map values) throws QueryConstructionException {
        if (tableName == null || tableName.trim().equals("")) {
            throw new QueryConstructionException("Table name should not be null");
        }
        if (values == null || values.isEmpty()) {
            throw new QueryConstructionException("Values to be inserted are not given");
        }
        final StringBuilder buffer = new StringBuilder(100);
        buffer.append("INSERT INTO ");
        buffer.append(this.getDBSpecificTableName(tableName));
        buffer.append(" (");
        final StringBuilder valueBuffer = new StringBuilder(30);
        final Set keys = values.keySet();
        final Map<Column, Object> dyMap = new LinkedHashMap<Column, Object>();
        final Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            final Column column = itr.next();
            final Object value = values.get(column);
            final String dataType = column.getDataType();
            Label_0259: {
                if (null != dataType) {
                    if (!DataTypeUtil.isUDT(dataType) || DataTypeManager.getDataTypeDefinition(dataType).getMeta().processInput()) {
                        break Label_0259;
                    }
                }
                else if (!DataTypeUtil.isUDT(column.getType()) || DataTypeManager.getDataTypeDefinition(column.getType()).getMeta().processInput()) {
                    break Label_0259;
                }
                if (!itr.hasNext()) {
                    buffer.deleteCharAt(buffer.lastIndexOf(","));
                    valueBuffer.deleteCharAt(valueBuffer.lastIndexOf(","));
                    continue;
                }
                continue;
            }
            if (column.getDefinition() != null && column.getDefinition().isDynamic()) {
                dyMap.put(column, value);
            }
            else {
                final String columnName = this.getDBSpecificColumnName(column.getColumnName());
                buffer.append(columnName);
                if (value == QueryConstants.PREPARED_STMT_CONST) {
                    valueBuffer.append(this.getDBSpecificEncryptionString(column, "?"));
                }
                else {
                    final int type = column.getType();
                    if (this.isNumeric(type)) {
                        if (!this.isNumeric(value)) {
                            Ansi92SQLGenerator.OUT.log(Level.WARNING, "Column {0} type {1} and value \"{2}\" type {3} mismatch", new Object[] { column, getSQLTypeAsString(type), value, value.getClass() });
                            throw new QueryConstructionException("Column Type and value doesn't match");
                        }
                        valueBuffer.append(value.toString());
                    }
                    else {
                        this.processNonNumericColumn(value, column, valueBuffer);
                    }
                }
                if (!itr.hasNext() && dyMap.isEmpty()) {
                    continue;
                }
                buffer.append(",");
                valueBuffer.append(",");
            }
        }
        if (!dyMap.isEmpty()) {
            this.getDCSQLGeneratorForTable(tableName).getSQLForInsert(tableName, dyMap, valueBuffer, buffer);
        }
        buffer.append(") VALUES (");
        buffer.append(valueBuffer.toString());
        buffer.append(")");
        final String returnString = buffer.toString();
        Ansi92SQLGenerator.OUT.log(Level.FINE, "Ansi92SQLGenerator.getSQLForInsert(): SQL formed is : \n{0}", returnString);
        return returnString;
    }
    
    protected void processNonNumericColumn(final Object value, final Column column, final StringBuilder valueBuffer) {
        if (value == null) {
            valueBuffer.append(" NULL ");
            return;
        }
        if (column.isEncrypted()) {
            valueBuffer.append(this.getDBSpecificEncryptionString(column, value.toString()));
        }
        else {
            valueBuffer.append("'");
            valueBuffer.append(value.toString());
            valueBuffer.append("'");
        }
    }
    
    @Override
    public String getDBSpecificEncryptionString(final Column column, final String value) {
        return value;
    }
    
    @Override
    public String getDBSpecificDecryptionString(final Column column, final String value) {
        return value;
    }
    
    @Override
    public String getDecryptSQL(final String value, final String dataType) {
        return value;
    }
    
    @Override
    public String getSQLForAlterTable(final AlterTableQuery alterTableQuery) throws QueryConstructionException {
        final boolean isRevertOperation = alterTableQuery.isRevert();
        final StringBuilder buffer = new StringBuilder("");
        AlterOperation prevAlterOperation = null;
        for (final AlterOperation currentOperation : alterTableQuery.getAlterOperations()) {
            this.appendSeperatorForAlterOperation(buffer, prevAlterOperation, currentOperation);
            AlterOperation altOper = currentOperation;
            if (isRevertOperation) {
                altOper = this.getAlterOperationForRevert(currentOperation);
            }
            this.appendStringForAlterOperation(altOper, buffer);
            prevAlterOperation = currentOperation;
        }
        return buffer.toString();
    }
    
    protected void setColumnNamesFromList(final List columnNames, final StringBuilder buffer, final String tableName) {
        DCSQLGenerator dcSqlGenerator = null;
        if (tableName != null) {
            dcSqlGenerator = this.getDCSQLGeneratorForTable(tableName);
        }
        buffer.append(" (");
        final Iterator<String> iterator = columnNames.iterator();
        while (iterator.hasNext()) {
            if (dcSqlGenerator != null) {
                if (!dcSqlGenerator.isUniqueKeySupported()) {
                    throw new UnsupportedOperationException("Unique Key is not supported for " + this.getDCTypeForTable(tableName));
                }
                buffer.append(dcSqlGenerator.getDCSpecificColumnName(tableName, iterator.next()));
            }
            else {
                buffer.append(this.getDBSpecificColumnName(iterator.next()));
            }
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append(")");
    }
    
    @Override
    public String getSQLForDelete(final String tableName, final Criteria criteria) throws QueryConstructionException {
        return this.getSQLForDelete(tableName, null, criteria);
    }
    
    @Override
    public String getSQLForDelete(final String tableName, final Join join, final Criteria criteria) throws QueryConstructionException {
        if (tableName == null) {
            throw new QueryConstructionException("Ansi92SQLGenerator.getSQLForDelete(): Table name passed is null");
        }
        final StringBuilder deleteBuffer = new StringBuilder(100);
        deleteBuffer.append("DELETE FROM ");
        deleteBuffer.append(this.getDBSpecificTableName(tableName));
        this.appendDeleteJoinAndCriteria(join, criteria, deleteBuffer);
        final String sql = deleteBuffer.toString();
        Ansi92SQLGenerator.OUT.log(Level.FINE, "Ansi92SQLGenerator.getSQLForDelete(): SQL formed is : {0}", sql);
        return sql;
    }
    
    protected String formCriteriaForDelete(final Criteria criteria) throws QueryConstructionException {
        QueryUtil.setTypeForCriteria(criteria);
        String whereClause = this.formWhereClause(criteria, false);
        if (whereClause != null) {
            whereClause = " WHERE " + whereClause;
        }
        return whereClause;
    }
    
    @Override
    public String getSQLForDelete(final DeleteQuery query) throws QueryConstructionException {
        throw new UnsupportedOperationException("Ansi92SQLGEnerator: Delete SQL creation not supported.");
    }
    
    protected String getColumnNameForUpdateSetClause(final Column column) {
        return this.getDBSpecificColumnName(column.getColumnName());
    }
    
    protected String getUpdateColumnsSQL(final String tableName, final Map newValues) throws QueryConstructionException {
        if (newValues == null || newValues.isEmpty()) {
            final String message = "Values to be updated are not given";
            throw new QueryConstructionException(message);
        }
        final Map values = new LinkedHashMap();
        try {
            final Map<Column, Object> dyVals = new LinkedHashMap<Column, Object>();
            for (final Column col : newValues.keySet()) {
                if (MetaDataUtil.getTableDefinitionByName(tableName) != null && MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(col.getColumnName()) != null && MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(col.getColumnName()).isDynamic()) {
                    dyVals.put(col, newValues.get(col));
                }
                else {
                    values.put(col, newValues.get(col));
                }
            }
            if (!dyVals.isEmpty()) {
                values.putAll(this.getDCSQLGeneratorForTable(tableName).getModifiedValueForUpdate(tableName, dyVals));
            }
        }
        catch (final MetaDataException mde) {
            throw new QueryConstructionException(mde.getMessage(), mde);
        }
        final StringBuilder updateColumnsBuffer = new StringBuilder(100);
        final Set keys = values.keySet();
        final Iterator itr = keys.iterator();
        while (itr.hasNext()) {
            final Column column = itr.next();
            final String columnName = column.getColumnName();
            if (tableName != null && !tableName.equals(column.getTableAlias())) {
                throw new QueryConstructionException("All Columns to be inserted should belong to a single table");
            }
            updateColumnsBuffer.append(this.getColumnNameForUpdateSetClause(column));
            updateColumnsBuffer.append("=");
            Object value = values.get(column);
            if (value == null) {
                updateColumnsBuffer.append("NULL");
            }
            else if (value == QueryConstants.PREPARED_STMT_CONST) {
                updateColumnsBuffer.append(this.getDBSpecificEncryptionString(column, "?"));
            }
            else if (value instanceof DerivedColumn) {
                final DerivedColumn dc = (DerivedColumn)value;
                final Query subQuery = dc.getSubQuery();
                updateColumnsBuffer.append("( ");
                ((SelectQueryImpl)subQuery).markAsSubQuery();
                final String queryStr = this.getSQLForSelect(subQuery);
                ((SelectQueryImpl)subQuery).clearSubQueryFlag();
                updateColumnsBuffer.append(queryStr);
                updateColumnsBuffer.append(") ");
            }
            else if (value instanceof Column) {
                final Column valueColumn = (Column)value;
                if (valueColumn.isEncrypted()) {
                    String retStr = null;
                    if (valueColumn.getTableAlias() != null) {
                        final String dbSpecificTableName = this.getDBSpecificTableName(valueColumn.getTableAlias());
                        if (valueColumn.getDefinition() != null && valueColumn.getDefinition().isDynamic()) {
                            final DCSQLGenerator dcSQLGenerator = this.getDCSQLGeneratorForTable(valueColumn.getTableAlias());
                            final String dcSpecificColumnName = dcSQLGenerator.getDCSpecificColumnName(valueColumn.getColumnAlias());
                            retStr = dcSQLGenerator.getSQLForCast(dbSpecificTableName + "." + dcSpecificColumnName, valueColumn);
                        }
                        else {
                            retStr = dbSpecificTableName + "." + this.getDBSpecificColumnName(valueColumn.getColumnAlias());
                        }
                    }
                    else if (valueColumn.getDefinition() != null && valueColumn.getDefinition().isDynamic()) {
                        final DCSQLGenerator dcSQLGenerator2 = this.getDCSQLGeneratorForTable(valueColumn.getDefinition().getTableName());
                        final String dcSpecificColumnName2 = dcSQLGenerator2.getDCSpecificColumnName(valueColumn.getColumnAlias());
                        retStr = dcSQLGenerator2.getSQLForCast(dcSpecificColumnName2, valueColumn);
                    }
                    else {
                        retStr = this.getDBSpecificColumnName(valueColumn.getColumnAlias());
                    }
                    updateColumnsBuffer.append(retStr);
                }
                else {
                    this.processColumn((Column)value, updateColumnsBuffer, null, Clause.WHERE);
                }
            }
            else {
                String retVal = null;
                final int type = column.getType();
                if (column.getDataType() != null && DataTypeUtil.isUDT(column.getDataType())) {
                    try {
                        value = DTTransformationUtil.transform(tableName, columnName, value, column.getDataType(), this.getDBType());
                    }
                    catch (final DataAccessException e) {
                        throw new QueryConstructionException("Exception while transforming data." + e);
                    }
                }
                if (type == 2004) {
                    throw new QueryConstructionException("Support for BLOB is not provided in UpdateQuery");
                }
                if (type == 16) {
                    retVal = this.getValueStringForBoolean(value.toString());
                }
                else if (this.isNumeric(type)) {
                    if (!this.isNumeric(value) && !(value instanceof String)) {
                        Ansi92SQLGenerator.OUT.log(Level.WARNING, "Column {0} type {1} and value \"{2}\" type {3} mismatch", new Object[] { column, getSQLTypeAsString(type), value, value.getClass() });
                        throw new QueryConstructionException("Column Type and value doesn't match");
                    }
                    retVal = value.toString();
                }
                else {
                    retVal = "'" + this.escapeSpecialCharacters(value.toString(), type) + "'";
                    retVal = retVal.replaceAll("\\\\", "\\\\\\\\");
                }
                updateColumnsBuffer.append(this.getDBSpecificEncryptionString(column, retVal));
            }
            if (!itr.hasNext()) {
                continue;
            }
            updateColumnsBuffer.append(", ");
        }
        return updateColumnsBuffer.toString();
    }
    
    @Override
    public String getSQLForUpdate(final String tableName, final Map newValues, final Criteria criteria, final List joins) throws QueryConstructionException {
        throw new UnsupportedOperationException("Ansi92SQLGEnerator: Update SQL creation with joins not supported.");
    }
    
    @Override
    public String getSQLForUpdate(final List tableName, final Map newValues, final Criteria criteria, final List joins) throws QueryConstructionException {
        throw new UnsupportedOperationException("Ansi92SQLGEnerator: Update SQL creation with joins not supported.");
    }
    
    @Override
    public String getSQLForUpdate(final String tableName, final Map newValues, final Criteria criteria) throws QueryConstructionException {
        if (tableName == null) {
            throw new QueryConstructionException("Table name can not be null");
        }
        final StringBuilder updateBuffer = new StringBuilder(100);
        final String setSQL = this.getUpdateColumnsSQL(tableName, newValues);
        final String whereClause = this.formWhereClause(criteria, false, null);
        updateBuffer.append("UPDATE ");
        updateBuffer.append(this.getDBSpecificTableName(tableName));
        updateBuffer.append(" SET ");
        updateBuffer.append(setSQL);
        if (whereClause != null) {
            updateBuffer.append(" WHERE (");
            updateBuffer.append(whereClause);
            updateBuffer.append(")");
        }
        final String sql = updateBuffer.toString();
        Ansi92SQLGenerator.OUT.log(Level.FINE, "Ansi92SQLGEnerator.getSQLForUpdate(): SQL formed is : {0}", sql);
        return sql;
    }
    
    @Override
    public String getSQLForSelect(final Query query) throws QueryConstructionException {
        if (query instanceof UnionQuery) {
            final UnionQuery current = (UnionQuery)query;
            if (!this.validateUnion(current, this.getSelectColumns(current))) {
                throw new QueryConstructionException("Columns that are part of select queries present in this Union Query have different data types. This is not supported");
            }
            String unionsql = this.getUnionSQL(query);
            final List sortColumns = current.getSortColumns();
            final List selectColumns = current.getSelectColumns();
            final List<Column> caseSensitiveColumns = new ArrayList<Column>();
            final String orderbyclause = this.getOrderByClause(sortColumns, selectColumns, null, caseSensitiveColumns);
            final Range range = current.getRange();
            unionsql = this.processOrderByandRange(unionsql, orderbyclause, range);
            Ansi92SQLGenerator.OUT.fine(unionsql);
            return unionsql;
        }
        else {
            if (query instanceof SelectQuery) {
                String sql = this.processSelectQuery((SelectQuery)query);
                final Range range2 = query.getRange();
                if (range2 != null) {
                    sql = this.sqlModifier.getSQLForSelectWithRange(sql, range2);
                }
                if (((SelectQuery)query).getLockStatus()) {
                    sql += " FOR UPDATE ";
                }
                Ansi92SQLGenerator.OUT.fine(sql);
                return sql;
            }
            return null;
        }
    }
    
    protected String processOrderByandRange(final String unionsql, final String orderbyclause, final Range range) throws QueryConstructionException {
        final StringBuilder buffer = new StringBuilder(unionsql);
        if (orderbyclause != null) {
            final int periodPosition = orderbyclause.indexOf(".");
            if (periodPosition != -1) {
                buffer.append(" ORDER BY " + orderbyclause.substring(periodPosition + 1));
            }
            else {
                buffer.append(orderbyclause);
            }
        }
        if (range != null) {
            final String rangeSQL = this.sqlModifier.getSQLForUnionWithRange(buffer.toString(), range);
            return rangeSQL;
        }
        return buffer.toString();
    }
    
    @Override
    public String getSQLForDrop(final String tableName, final boolean cascade) throws QueryConstructionException {
        String dropSQL = "DROP TABLE " + this.getDBSpecificTableName(tableName);
        if (cascade) {
            final String casecadeStr = this.getCascadeString();
            if (casecadeStr != null) {
                dropSQL = dropSQL + " " + casecadeStr;
            }
        }
        return dropSQL;
    }
    
    @Override
    public String getSQLForTruncate(final String tableName) throws QueryConstructionException {
        final String truncateSQL = "TRUNCATE TABLE " + this.getDBSpecificTableName(tableName);
        return truncateSQL;
    }
    
    @Override
    public String getSQLForDrop(final String tableName, final boolean ifExists, final boolean cascade) throws QueryConstructionException {
        String dropSQL = "DROP TABLE ";
        if (ifExists) {
            dropSQL += "IF EXISTS ";
        }
        dropSQL += this.getDBSpecificTableName(tableName);
        if (cascade) {
            final String casecadeStr = this.getCascadeString();
            if (casecadeStr != null) {
                dropSQL = dropSQL + " " + casecadeStr;
            }
        }
        return dropSQL;
    }
    
    @Override
    public String getSQLForLock(final List tableList) throws QueryConstructionException {
        throw new UnsupportedOperationException("Ansi92SQLGEnerator: Lock SQL creation not supported.");
    }
    
    @Override
    public String getSQLForLock(final String tableName) throws QueryConstructionException {
        throw new UnsupportedOperationException("Ansi92SQLGEnerator: Lock SQL creation not supported.");
    }
    
    @Override
    public String getSQLForCreateTable(final TableDefinition tabDefn) throws QueryConstructionException {
        return this.getSQLForCreateTable(tabDefn, null);
    }
    
    @Override
    public String getSQLForCreateTable(final TableDefinition tabDefn, final String createTableOptions) throws QueryConstructionException {
        final String tableName = tabDefn.getTableName();
        final List colDefnList = tabDefn.getColumnList();
        final List<ColumnDefinition> uniqueCols = new ArrayList<ColumnDefinition>();
        final int colSize = colDefnList.size();
        if (colSize == 0) {
            throw new QueryConstructionException("The given TableDefinition doesn't have column informations which is mandatory to form Create SQL.");
        }
        final StringBuilder createBuffer = new StringBuilder(600);
        createBuffer.append("CREATE TABLE ");
        createBuffer.append(this.getDBSpecificTableName(tableName));
        createBuffer.append(" (");
        for (int i = 0; i < colSize; ++i) {
            if (i > 0) {
                createBuffer.append(", ");
            }
            final ColumnDefinition colDefn = colDefnList.get(i);
            if (colDefn.isUnique()) {
                uniqueCols.add(colDefn);
            }
            final String colStr = this.processColumnDefn(colDefn);
            createBuffer.append(colStr);
        }
        final String primaryStr = this.processPKDefn(tabDefn);
        final String foreignKey = this.processFKDefn(tabDefn);
        final String uniqueKeyStr = this.processUniqueKeys(tabDefn);
        if (primaryStr != null) {
            createBuffer.append(", ");
            createBuffer.append(primaryStr);
        }
        if (foreignKey != null && foreignKey.trim().length() != 0) {
            createBuffer.append(", ");
            createBuffer.append(foreignKey);
        }
        if (uniqueKeyStr != null && !uniqueKeyStr.trim().equals("")) {
            createBuffer.append(", ");
            createBuffer.append(uniqueKeyStr);
        }
        createBuffer.append(")");
        this.appendCreateIndexForCreateTable(createBuffer, tabDefn, null);
        final String toReturn = createBuffer.toString();
        Ansi92SQLGenerator.OUT.log(Level.FINEST, "The create table string is {0} ", toReturn);
        return toReturn;
    }
    
    protected void appendCreateIndexForCreateTable(final StringBuilder buffer, final TableDefinition tabDefn, final String createTableOptions) throws QueryConstructionException {
        final String tableName = tabDefn.getTableName();
        final List<IndexDefinition> indexDefinitions = tabDefn.getIndexes();
        if (indexDefinitions != null) {
            for (final IndexDefinition idxDef : indexDefinitions) {
                buffer.append("; ");
                buffer.append(this.getSQLForIndex(tableName, idxDef));
            }
        }
        final List<ForeignKeyDefinition> fkDefList = tabDefn.getForeignKeyList();
        try {
            if (fkDefList != null) {
                for (final ForeignKeyDefinition fkDef : fkDefList) {
                    final IndexDefinition indxDef = this.getIndexDefForFK(fkDef);
                    buffer.append("; ");
                    buffer.append(this.getSQLForIndex(tableName, indxDef));
                }
            }
        }
        catch (final QueryConstructionException qce) {
            throw new QueryConstructionException(qce.getMessage());
        }
    }
    
    @Override
    public String getSQLForIndex(final String tableName, final IndexDefinition iDef) throws QueryConstructionException {
        final StringJoiner indexString = new StringJoiner(" ");
        final StringJoiner columnString = new StringJoiner(",", "(", ")");
        String indexProps = "";
        indexString.add("CREATE INDEX").add(this.getConstraintName(iDef.getName())).add("ON").add(this.getDBSpecificTableName(tableName));
        DCSQLGenerator dcSqlGenerator = null;
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException e) {
            throw new QueryConstructionException(e.getMessage(), e);
        }
        if (td != null && td.getDynamicColumnType() != null) {
            dcSqlGenerator = this.getDCSQLGenerator(td.getDynamicColumnType());
        }
        for (final IndexColumnDefinition icd : iDef.getColumnDefnitions()) {
            final String dataType = icd.getDataType();
            indexProps = this.getIndexPropString(icd.isAscending());
            final DataTypeDefinition udt = (dataType != null) ? DataTypeManager.getDataTypeDefinition(dataType) : null;
            if (udt != null && udt.getMeta() != null) {
                final String indexStr = udt.getDTSQLGenerator(this.getDBType()).getSQLForIndexColumn(icd);
                if (indexStr != null) {
                    columnString.add(indexStr);
                    continue;
                }
            }
            if (td != null && dcSqlGenerator != null) {
                columnString.add(String.join("", dcSqlGenerator.getDCSpecificColumnName(td.getTableName(), icd.getColumnName()), indexProps));
            }
            else {
                columnString.add(String.join("", this.getDBSpecificColumnName(icd.getColumnName()), indexProps));
            }
        }
        indexString.add(columnString.toString());
        return indexString.toString();
    }
    
    protected String getIndexPropString(final Boolean ascending) {
        if (!ascending) {
            return " desc";
        }
        return "";
    }
    
    protected String processUniqueKeys(final TableDefinition td) throws QueryConstructionException {
        final List uniqueKeys = td.getUniqueKeys();
        if (uniqueKeys == null || uniqueKeys.size() <= 0) {
            return null;
        }
        final PrimaryKeyDefinition pkDefn = td.getPrimaryKey();
        final Iterator it = uniqueKeys.iterator();
        boolean first_uniqueKey = true;
        StringBuilder buffer = new StringBuilder(200);
        final List pkCols = pkDefn.getColumnList();
        final int pkColSize = pkCols.size();
        String pkColumnName = null;
        if (pkColSize == 1) {
            pkColumnName = pkCols.get(0);
        }
        while (it.hasNext()) {
            final UniqueKeyDefinition ukd = it.next();
            final List ukCols = ukd.getColumns();
            final int ukColSize = ukCols.size();
            final String ukColName = null;
            if (ukColSize == 1 && ukCols.get(0).equals(pkColumnName)) {
                Ansi92SQLGenerator.OUT.log(Level.WARNING, "A column cannot have a unique constraint as true, if it alone participates in the PKDefinition of that tableDefinition. TableName :: {0}, columnName :: {1}", new Object[] { pkDefn.getTableName(), pkColumnName });
                String bufferStr = buffer.toString();
                if (!bufferStr.trim().endsWith(",")) {
                    continue;
                }
                bufferStr = bufferStr.trim();
                buffer = new StringBuilder(bufferStr.substring(0, bufferStr.length() - 1));
            }
            else {
                if (!first_uniqueKey) {
                    buffer.append(", ");
                }
                buffer.append("CONSTRAINT ");
                buffer.append(this.getConstraintName(ukd.getName()));
                buffer.append(" UNIQUE");
                this.addUniqueKeyName(buffer, this.getConstraintName(ukd.getName()));
                buffer.append("(");
                final Iterator cols = ukd.getColumns().iterator();
                while (cols.hasNext()) {
                    final String name = cols.next();
                    buffer.append(this.getDBSpecificColumnName(name));
                    if (cols.hasNext()) {
                        buffer.append(",");
                    }
                }
                buffer.append(")");
                first_uniqueKey = false;
            }
        }
        return buffer.toString();
    }
    
    protected void addUniqueKeyName(final StringBuilder buffer, final String ukName) throws QueryConstructionException {
    }
    
    protected String processColumnDefn(final ColumnDefinition colDefn) throws QueryConstructionException {
        final StringBuilder colBuffer = new StringBuilder(60);
        final String columnName = this.getDBSpecificColumnName(colDefn.getColumnName());
        final String dataType = colDefn.getDataType();
        final Object defaultVal = colDefn.getDefaultValue();
        final boolean nullable = colDefn.isNullable();
        colBuffer.append(columnName);
        colBuffer.append(" ");
        colBuffer.append(this.getDBDataType(colDefn));
        if (defaultVal != null) {
            colBuffer.append(" DEFAULT ").append(this.getDefaultValue(dataType, defaultVal));
        }
        if (!nullable) {
            colBuffer.append(" NOT NULL");
        }
        else {
            this.handleTimeStamp(colDefn, colBuffer);
        }
        return colBuffer.toString();
    }
    
    protected void handleTimeStamp(final ColumnDefinition colDefn, final StringBuilder colBuffer) {
    }
    
    @Override
    public String getDBSpecificColumnName(final String columnName) {
        return columnName;
    }
    
    @Override
    public String getDBSpecificTableName(final String tableName) {
        return tableName;
    }
    
    protected String getDefaultValue(String dataType, final Object defVal) throws QueryConstructionException {
        if (defVal == null) {
            return null;
        }
        if (DataTypeUtil.isEDT(dataType)) {
            dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
        }
        String retVal = "";
        if (dataType.equals("INTEGER") || dataType.equals("BIGINT") || dataType.equals("CHAR") || dataType.equals("SCHAR") || dataType.equals("NCHAR") || dataType.equals("DATE") || dataType.equals("DATETIME") || dataType.equals("TIMESTAMP") || dataType.equals("TIME") || dataType.equals("FLOAT") || dataType.equals("DOUBLE") || dataType.equals("TINYINT") || dataType.equals("DECIMAL")) {
            retVal = "'" + String.valueOf(defVal) + "'";
        }
        else if (dataType.equals("BOOLEAN")) {
            final String defaultValue = String.valueOf(defVal);
            if (defaultValue.equalsIgnoreCase("true") || defaultValue.equalsIgnoreCase("1") || defaultValue.equalsIgnoreCase("t") || defaultValue.equalsIgnoreCase("y") || defaultValue.equalsIgnoreCase("yes")) {
                retVal = "1";
            }
            else {
                retVal = "0";
            }
        }
        else if (DataTypeUtil.isUDT(dataType)) {
            retVal = DataTypeManager.getDataTypeDefinition(dataType).getDTSQLGenerator(this.getDBType()).getDefaultValue(defVal);
        }
        return retVal;
    }
    
    @Override
    public String getDBDataType(final ColumnDefinition colDef) throws QueryConstructionException {
        String dataType = colDef.getDataType();
        if (DataTypeManager.getDataTypeDefinition(dataType) != null) {
            final String baseType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
            if (baseType != null) {
                dataType = baseType;
            }
        }
        int maxLength = colDef.getMaxLength();
        final int precision = colDef.getPrecision();
        final boolean keyOrUnique = colDef.isKey() || colDef.isUnique();
        String dataTypeStr = null;
        if (dataType.equals("CHAR") || dataType.equals("SCHAR") || dataType.equals("NCHAR")) {
            if (maxLength == 0) {
                maxLength = 50;
            }
            if (maxLength == -1) {
                dataTypeStr = "TEXT";
            }
            else {
                dataTypeStr = "VARCHAR(" + maxLength + ")";
            }
        }
        else if (dataType.equals("BOOLEAN")) {
            dataTypeStr = "BIT";
        }
        else if (dataType.equals("DATE") || dataType.equals("DATETIME") || dataType.equals("TIME") || dataType.equals("TIMESTAMP") || dataType.equals("BLOB")) {
            dataTypeStr = dataType;
        }
        else if (dataType.equals("FLOAT") || dataType.equals("DOUBLE")) {
            dataTypeStr = dataType + ((maxLength != 0) ? ("(" + ((maxLength == 0) ? 10 : maxLength) + ", " + ((precision == 0) ? 2 : precision) + ")") : "");
        }
        else if (dataType.equals("DECIMAL")) {
            dataTypeStr = dataType + "(" + ((maxLength == 0) ? 16 : maxLength) + ", " + ((precision == 0) ? 4 : precision) + ")";
        }
        else if (dataType.equals("SBLOB")) {
            dataTypeStr = "BLOB";
        }
        else if (dataType.equals("TINYINT")) {
            dataTypeStr = "TINYINT";
        }
        else {
            if (!DataTypeUtil.isUDT(dataType)) {
                throw new QueryConstructionException("UnSupported data type " + dataType + " specified");
            }
            dataTypeStr = DataTypeManager.getDataTypeDefinition(dataType).getDTSQLGenerator(this.getDBType()).getDBDataType(colDef);
        }
        return dataTypeStr;
    }
    
    protected String processPKDefn(final TableDefinition tabDefn) throws QueryConstructionException {
        final PrimaryKeyDefinition pkDefn = tabDefn.getPrimaryKey();
        if (pkDefn == null) {
            return null;
        }
        final StringBuilder pkBuffer = new StringBuilder(100);
        final String constName = this.getConstraintName(pkDefn.getName());
        final List colList = pkDefn.getColumnList();
        final int colSize = colList.size();
        if (colSize == 0) {
            return null;
        }
        if (constName != null) {
            pkBuffer.append("CONSTRAINT ");
            pkBuffer.append(constName);
        }
        pkBuffer.append(" PRIMARY KEY (");
        for (int i = 0; i < colSize; ++i) {
            if (i > 0) {
                pkBuffer.append(", ");
            }
            final String colName = this.getDBSpecificColumnName(colList.get(i));
            pkBuffer.append(colName);
        }
        pkBuffer.append(")");
        return pkBuffer.toString();
    }
    
    protected String processFKDefn(final TableDefinition td) throws QueryConstructionException {
        final List fkList = td.getForeignKeyList();
        if (fkList == null || fkList.isEmpty()) {
            return null;
        }
        final StringBuilder fkBuffer = new StringBuilder(200);
        for (int fkSize = fkList.size(), i = 0; i < fkSize; ++i) {
            final ForeignKeyDefinition fkDefn = fkList.get(i);
            if (i != 0) {
                fkBuffer.append(",");
            }
            fkBuffer.append(this.getSingleFKDefn(fkDefn));
        }
        return fkBuffer.toString();
    }
    
    protected String getSingleFKDefn(final ForeignKeyDefinition fkDefn) throws QueryConstructionException {
        final String constName = this.getConstraintName(fkDefn.getName());
        final String masterTabName = fkDefn.getMasterTableName();
        final String slaveTabName = fkDefn.getSlaveTableName();
        final List fkCols = fkDefn.getForeignKeyColumns();
        final int constraints = fkDefn.getConstraints();
        if (fkCols == null || fkCols.isEmpty()) {
            throw new QueryConstructionException("Foreign Key columns not specified in the ForeignKeyDefinition");
        }
        final StringBuilder fkBuffer = new StringBuilder(100);
        final StringBuilder refBuffer = new StringBuilder(100);
        if (constName != null) {
            fkBuffer.append("CONSTRAINT ");
            fkBuffer.append(constName);
        }
        fkBuffer.append(" FOREIGN KEY (");
        for (int fkColSize = fkCols.size(), i = 0; i < fkColSize; ++i) {
            if (i != 0) {
                fkBuffer.append(",");
                refBuffer.append(",");
            }
            final ForeignKeyColumnDefinition fkColDefn = fkCols.get(i);
            final ColumnDefinition localDefn = fkColDefn.getLocalColumnDefinition();
            final ColumnDefinition refDefn = fkColDefn.getReferencedColumnDefinition();
            if (localDefn.getMaxLength() > 250) {
                throw new QueryConstructionException("Exception: FK Column size is greater than 250 for table " + slaveTabName);
            }
            fkBuffer.append(this.getDBSpecificColumnName(localDefn.getColumnName()));
            refBuffer.append(this.getDBSpecificColumnName(refDefn.getColumnName()));
        }
        fkBuffer.append(") REFERENCES ");
        fkBuffer.append(this.getDBSpecificTableName(masterTabName));
        fkBuffer.append(" (");
        fkBuffer.append(refBuffer.toString());
        fkBuffer.append(") ");
        fkBuffer.append(this.getStringConstraint(constraints));
        return fkBuffer.toString();
    }
    
    protected String getConstraintName(final String constraintName) throws QueryConstructionException {
        return constraintName;
    }
    
    protected String getStringConstraint(final int constraints) {
        String constraintName = null;
        switch (constraints) {
            case 0: {
                constraintName = "";
                break;
            }
            case 1: {
                constraintName = "ON DELETE CASCADE";
                break;
            }
            case 2: {
                constraintName = "ON DELETE SET NULL";
                break;
            }
            case 3: {
                constraintName = "ON DELETE SET DEFAULT";
                break;
            }
            default: {
                constraintName = "";
                break;
            }
        }
        return constraintName;
    }
    
    protected String getCascadeString() {
        return null;
    }
    
    protected String getUnionSQL(final Query query) throws QueryConstructionException {
        QueryUtil.setDataType(query);
        if (query instanceof UnionQuery) {
            final UnionQuery current = (UnionQuery)query;
            final Query leftQuery = current.getLeftQuery();
            final Query rightQuery = current.getRightQuery();
            final boolean retainDuplicateRows = current.isRetainDuplicateRows();
            String leftSQL = null;
            String rightSQL = null;
            if (leftQuery != null) {
                leftSQL = this.getUnionSQL(leftQuery);
            }
            if (rightQuery != null) {
                rightSQL = this.getUnionSQL(rightQuery);
            }
            if (leftSQL != null && rightSQL != null) {
                String sqlString = null;
                if (retainDuplicateRows) {
                    sqlString = leftSQL + " UNION ALL " + rightSQL;
                    Ansi92SQLGenerator.OUT.log(Level.FINE, "Ansi92SQLGenerator.getSQLForSelect(): SQL formed is : {0}", sqlString);
                    return sqlString;
                }
                sqlString = leftSQL + " UNION " + rightSQL;
                Ansi92SQLGenerator.OUT.log(Level.FINE, "Ansi92SQLGenerator.getSQLForSelect(): SQL formed is : {0}", sqlString);
                return sqlString;
            }
        }
        else if (query instanceof SelectQuery) {
            final String sqlString2 = this.getSQLForSelect(query);
            return "(" + sqlString2 + ")";
        }
        return null;
    }
    
    protected List getSelectColumns(final Query query) {
        if (query instanceof UnionQuery) {
            final UnionQuery union = (UnionQuery)query;
            return union.getSelectColumns();
        }
        if (query instanceof SelectQuery) {
            final SelectQuery select = (SelectQuery)query;
            return select.getSelectColumns();
        }
        return null;
    }
    
    protected String processSelectQuery(final SelectQuery query) throws QueryConstructionException {
        final List<Column> caseSensitiveColumns = new ArrayList<Column>();
        final String whereClause = this.formWhereClause(query.getCriteria(), false, caseSensitiveColumns, query.getDerivedTables());
        final String orderByClause = this.getOrderByClause(query.getSortColumns(), query.getSelectColumns(), query.getDerivedTables(), caseSensitiveColumns);
        final String selectClause = this.formSelectClause(query.getSelectColumns(), caseSensitiveColumns, query);
        final String sqlString = this.getSQL(query, selectClause, whereClause, orderByClause);
        Ansi92SQLGenerator.OUT.log(Level.FINE, "Ansi92SQLGenerator.processSelectQuery(): SQL formed is : {0}", sqlString);
        return sqlString;
    }
    
    protected boolean validateUnion(final UnionQuery query, final List selectCols) throws QueryConstructionException {
        final Query leftQuery = query.getLeftQuery();
        final Query rightQuery = query.getRightQuery();
        boolean leftTrue = false;
        boolean rightTrue = false;
        if (leftQuery instanceof UnionQuery) {
            leftTrue = this.validateUnion((UnionQuery)leftQuery, selectCols);
        }
        if (rightQuery instanceof UnionQuery) {
            rightTrue = this.validateUnion((UnionQuery)rightQuery, selectCols);
        }
        if (leftTrue && rightTrue) {
            return true;
        }
        if (leftQuery instanceof SelectQuery) {
            leftTrue = this.checkColsPresence((SelectQuery)leftQuery, selectCols);
        }
        if (rightQuery instanceof SelectQuery) {
            rightTrue = this.checkColsPresence((SelectQuery)rightQuery, selectCols);
        }
        return leftTrue && rightTrue;
    }
    
    private boolean checkColsPresence(final SelectQuery select, final List selectCols) throws QueryConstructionException {
        final List currentCols = select.getSelectColumns();
        if (currentCols.size() != selectCols.size()) {
            throw new QueryConstructionException("No. of Columns in each SelectQueries that are part of this Union query is different. This is not supported for forming Union query");
        }
        final int selSize = selectCols.size();
        for (int i = 0; i < selSize; ++i) {
            final Column selectCol = selectCols.get(i);
            final Column currentCol = currentCols.get(i);
            if (selectCol.getType() != currentCol.getType()) {
                return false;
            }
        }
        return selSize > 0;
    }
    
    protected void appendNoLock(final StringBuilder selectBuffer) {
    }
    
    protected String getSQL(final SelectQuery query, final String selectClause, String whereClause, String orderByClause) throws QueryConstructionException {
        final StringBuilder selectBuffer = new StringBuilder(500);
        selectBuffer.append("SELECT ");
        if (query.isDistinct()) {
            selectBuffer.append("DISTINCT ");
        }
        selectBuffer.append(selectClause);
        selectBuffer.append(" FROM ");
        final List tablesList = query.getTableList();
        final List joins = query.getJoins();
        final String fromString = this.getFromClause(tablesList, joins, query.getDerivedTables(), query.getIndexHintMap());
        if (fromString == null) {
            return null;
        }
        selectBuffer.append(fromString);
        whereClause = this.getWhereClause(whereClause, tablesList, joins);
        if (whereClause != null) {
            selectBuffer.append(" WHERE ");
            selectBuffer.append(whereClause);
        }
        String groupByClause = this.getGroupByClause(query.getGroupByColumns(), query.getDerivedTables());
        if (groupByClause != null || (groupByClause = this.getGroupByClause(query.getGroupByClause(), query.getDerivedTables())) != null) {
            final List groupByColumns = (query.getGroupByColumns().size() > 0) ? query.getGroupByColumns() : query.getGroupByClause().getGroupByColumns();
            this.validateGroupByColumns(query, groupByColumns);
            selectBuffer.append(" ");
            selectBuffer.append(groupByClause);
        }
        orderByClause = ((null != orderByClause) ? orderByClause : this.getOrderByClause(query.getSortColumns(), query.getSelectColumns(), null, null));
        if (orderByClause != null) {
            selectBuffer.append(orderByClause);
        }
        return selectBuffer.toString();
    }
    
    protected void validateGroupByColumns(final SelectQuery sq, final List grpByCols) throws QueryConstructionException {
        final List selectColumns = sq.getSelectColumns();
        final List groupByColumns = new ArrayList(grpByCols.size());
        for (int grpByColSize = grpByCols.size(), i = 0; i < grpByColSize; ++i) {
            Column c = null;
            if (grpByCols.get(i) instanceof GroupByColumn) {
                c = grpByCols.get(i).getGroupByColumn();
            }
            else if (grpByCols.get(i) instanceof Column) {
                c = grpByCols.get(i);
                if (c.getFunction() != 0) {
                    throw new QueryConstructionException("Aggregate functions are not allowed in GROUP BY clause");
                }
            }
            groupByColumns.add(c);
        }
        for (int selColSize = selectColumns.size(), j = 0; j < selColSize; ++j) {
            final Column c2 = selectColumns.get(j);
            if (c2.getFunction() == 0 && !groupByColumns.contains(c2) && !(c2 instanceof Function) && !(c2 instanceof Operation)) {
                throw new QueryConstructionException("column [" + c2 + "] must appear in the GROUP BY clause or be used in an aggregate function", 51);
            }
        }
    }
    
    protected String getJoinString(final Join join) {
        if (join.getJoinType() == 1) {
            return " LEFT JOIN ";
        }
        return " INNER JOIN ";
    }
    
    protected String formIndexHintSQL(final Table table, final List<IndexHintClause> indexHintList) throws QueryConstructionException {
        return null;
    }
    
    protected String getFromClause(final List tablesList, final List joins, final List<Table> derivedTableList, final Map<Table, List<IndexHintClause>> indexHintMap) throws QueryConstructionException {
        if (tablesList == null || tablesList.isEmpty()) {
            throw new QueryConstructionException("Tables from which the columns have to be selected is not given");
        }
        if (joins == null || joins.isEmpty()) {
            final StringBuilder fromBuffer = new StringBuilder(300);
            final Table mainTable = tablesList.get(0);
            if (mainTable instanceof DerivedTable) {
                final DerivedTable derivedTable = (DerivedTable)mainTable;
                fromBuffer.append("(");
                final String subQuery = this.getSQLForSelect(derivedTable.getSubQuery());
                fromBuffer.append(subQuery);
                fromBuffer.append(") ");
            }
            final String tableName = (mainTable instanceof ArchiveTable) ? ((ArchiveTable)mainTable).getArchiveTableName() : mainTable.getTableName();
            final String tableAlias = (mainTable instanceof ArchiveTable) ? ((ArchiveTable)mainTable).getArchiveTableAlias() : mainTable.getTableAlias();
            fromBuffer.append(this.getDBSpecificTableName(tableName));
            if (tableAlias != null && !tableName.equals(tableAlias)) {
                fromBuffer.append(" ");
                fromBuffer.append(this.getDBSpecificTableName(tableAlias));
            }
            if (indexHintMap != null) {
                final String indexHintSQL = this.formIndexHintSQL(mainTable, indexHintMap.get(mainTable));
                if (indexHintSQL != null) {
                    fromBuffer.append(indexHintSQL);
                }
            }
            return fromBuffer.toString();
        }
        return this.formJoinString(joins, derivedTableList, indexHintMap);
    }
    
    public String formJoinString(final List joins, final List<Table> derivedTableList, final Map<Table, List<IndexHintClause>> indexHintMap) throws QueryConstructionException {
        final StringBuilder fromBuffer = new StringBuilder(300);
        final Join baseJoin = joins.get(0);
        final Table t_base = baseJoin.getBaseTable();
        final String actualBaseTableName = (t_base instanceof ArchiveTable) ? ((ArchiveTable)t_base).getArchiveTableAlias() : baseJoin.getBaseTableAlias();
        final String baseTable1 = (t_base instanceof ArchiveTable) ? ((ArchiveTable)t_base).getInvisibleTableName() : baseJoin.getBaseTableName();
        final String baseTableAlias = (t_base instanceof ArchiveTable) ? ((ArchiveTable)t_base).getInvisibleTableAlias() : baseJoin.getBaseTableAlias();
        if (t_base instanceof DerivedTable) {
            final DerivedTable derivedTable = (DerivedTable)t_base;
            fromBuffer.append("(");
            final String subQuery = this.getSQLForSelect(derivedTable.getSubQuery());
            fromBuffer.append(subQuery);
            fromBuffer.append(") ");
            fromBuffer.append(this.getDBSpecificTableName(derivedTable.getTableAlias()));
        }
        else {
            fromBuffer.append(this.getDBSpecificTableName(baseTable1));
            if (baseTableAlias != null && !baseTable1.equals(baseTableAlias)) {
                fromBuffer.append(' ');
                fromBuffer.append(this.getDBSpecificTableName(baseTableAlias));
            }
        }
        if (indexHintMap != null) {
            final String indexHintSQL = this.formIndexHintSQL(t_base, indexHintMap.get(t_base));
            if (indexHintSQL != null) {
                fromBuffer.append(indexHintSQL);
            }
        }
        for (int i = 0; i < joins.size(); ++i) {
            final Join join = joins.get(i);
            fromBuffer.append(this.getJoinString(join));
            fromBuffer.append(this.getJoinReferencedTableString(join, indexHintMap));
            fromBuffer.append(" ON ");
            fromBuffer.append(this.getJoinCriteriaString(join, actualBaseTableName, derivedTableList));
        }
        return fromBuffer.toString();
    }
    
    @Override
    public String formJoinString(final List joins, final List<Table> derivedTableList) throws QueryConstructionException {
        return this.formJoinString(joins, derivedTableList, null);
    }
    
    protected String getJoinReferencedTableString(final Join join) throws QueryConstructionException {
        return this.getJoinReferencedTableString(join, null);
    }
    
    protected String getJoinReferencedTableString(final Join join, final Map<Table, List<IndexHintClause>> indexHintMap) throws QueryConstructionException {
        final StringBuilder buffer = new StringBuilder(300);
        final Table referencedTable = join.getReferencedTable();
        if (referencedTable instanceof DerivedTable) {
            final DerivedTable derivedTable = (DerivedTable)referencedTable;
            buffer.append("(");
            final String subQuery = this.getSQLForSelect(derivedTable.getSubQuery());
            buffer.append(subQuery);
            buffer.append(") ");
        }
        String referencedTableName = (referencedTable instanceof ArchiveTable) ? ((ArchiveTable)referencedTable).getArchiveTableName() : join.getReferencedTableName();
        final String referencedTableAlias = (referencedTable instanceof ArchiveTable) ? ((ArchiveTable)referencedTable).getArchiveTableAlias() : join.getReferencedTableAlias();
        buffer.append(this.getDBSpecificTableName(referencedTableName));
        if (referencedTableAlias != null && !referencedTableName.equals(referencedTableAlias)) {
            referencedTableName = referencedTableAlias;
            buffer.append(" ");
            buffer.append(this.getDBSpecificTableName(referencedTableName));
        }
        if (indexHintMap != null) {
            final String indexHintSQL = this.formIndexHintSQL(referencedTable, indexHintMap.get(referencedTable));
            if (indexHintSQL != null) {
                buffer.append(indexHintSQL);
            }
        }
        return buffer.toString();
    }
    
    protected String getJoinCriteriaString(final Join join, final List<Table> derivedTableList) throws QueryConstructionException {
        return this.getJoinCriteriaString(join, null, derivedTableList);
    }
    
    protected String getJoinCriteriaString(final Join join, final String actualBaseTableName, final List<Table> derivedTableList) throws QueryConstructionException {
        final StringBuilder buffer = new StringBuilder(300);
        final List<Table> tableList = new ArrayList<Table>();
        final List<Table> derivedTables = new ArrayList<Table>();
        if (derivedTableList != null) {
            tableList.addAll(derivedTableList);
            derivedTables.addAll(derivedTableList);
        }
        final Table baseTable = join.getBaseTable();
        String baseTableName = (baseTable instanceof ArchiveTable) ? ((ArchiveTable)baseTable).getArchiveTableName() : join.getBaseTableName();
        String baseTableNameAlias = (baseTable instanceof ArchiveTable) ? ((ArchiveTable)baseTable).getArchiveTableAlias() : join.getBaseTableAlias();
        if (actualBaseTableName != null && actualBaseTableName.equals(baseTableNameAlias)) {
            baseTableName = ((baseTable instanceof ArchiveTable) ? ((ArchiveTable)baseTable).getInvisibleTableName() : join.getBaseTableName());
            baseTableNameAlias = ((baseTable instanceof ArchiveTable) ? ((ArchiveTable)baseTable).getInvisibleTableAlias() : join.getBaseTableAlias());
        }
        final Table referencedTable = join.getReferencedTable();
        final String referencedTableName = (referencedTable instanceof ArchiveTable) ? ((ArchiveTable)referencedTable).getArchiveTableName() : join.getReferencedTableName();
        final String referencedTableAlias = (referencedTable instanceof ArchiveTable) ? ((ArchiveTable)referencedTable).getArchiveTableAlias() : join.getReferencedTableAlias();
        if (baseTable != null && baseTable instanceof DerivedTable && !derivedTables.contains(baseTable)) {
            derivedTables.add(baseTable);
        }
        if (referencedTable != null && referencedTable instanceof DerivedTable && !derivedTables.contains(referencedTable)) {
            derivedTables.add(referencedTable);
        }
        if (baseTable != null) {
            boolean toAdd = true;
            for (final Table table : tableList) {
                if (table.getTableAlias().equals(baseTableNameAlias)) {
                    toAdd = false;
                }
            }
            if (toAdd) {
                tableList.add(baseTable);
            }
        }
        if (referencedTable != null) {
            boolean toAdd = true;
            for (final Table table : tableList) {
                if (table.getTableAlias().equals(referencedTableAlias)) {
                    toAdd = false;
                }
            }
            if (toAdd) {
                tableList.add(referencedTable);
            }
        }
        if (join.getCriteria() == null) {
            for (int j = 0; j < join.getNumberOfColumns(); ++j) {
                if (j != 0) {
                    buffer.append(" AND ");
                }
                final Column baseTableColumn = new Column(baseTableNameAlias, join.getBaseTableColumn(j));
                if (!(baseTable instanceof ArchiveTable)) {
                    QueryUtil.setType(baseTableName, baseTableColumn, tableList);
                }
                this.processColumn(baseTableColumn, buffer, true, derivedTables, Clause.JOIN);
                buffer.append("=");
                final Column referencedTableColumn = new Column(referencedTableAlias, join.getReferencedTableColumn(j));
                if (!(baseTable instanceof ArchiveTable)) {
                    QueryUtil.setType(referencedTableName, referencedTableColumn, tableList);
                }
                this.processColumn(referencedTableColumn, buffer, true, derivedTables, Clause.JOIN);
            }
        }
        else {
            buffer.append(this.formWhereClause(join.getCriteria(), false, derivedTables));
        }
        return buffer.toString();
    }
    
    public String formJoinString(final List joins) throws QueryConstructionException {
        return this.formJoinString(joins, null);
    }
    
    @Override
    public String formGroupByString(final String[] columns, final Criteria havingcriteria) throws QueryConstructionException {
        if (columns == null || columns.length == 0) {
            Ansi92SQLGenerator.OUT.warning("Columns should not be empty in GroupByClause.");
            return null;
        }
        final StringBuilder buff = new StringBuilder(100);
        buff.append("GROUP BY ");
        for (int i = 0; i < columns.length; ++i) {
            buff.append(columns[i]);
            if (i + 1 < columns.length) {
                buff.append(",");
            }
        }
        if (havingcriteria != null) {
            buff.append(" HAVING ");
            buff.append(this.formWhereClause(havingcriteria, true, null));
        }
        return buff.toString();
    }
    
    @Override
    public String formOrderByString(final String[] columns, final boolean[] orders) throws QueryConstructionException {
        if (columns == null || columns.length == 0) {
            Ansi92SQLGenerator.OUT.warning("Columns should not be empty in OrderByClause");
            return null;
        }
        if (columns.length != orders.length) {
            Ansi92SQLGenerator.OUT.warning("Columns and orders length should be equal in OrderByClause");
            return null;
        }
        final StringBuilder buff = new StringBuilder(100);
        buff.append("ORDER BY ");
        for (int i = 0; i < columns.length; ++i) {
            String colName = columns[i];
            int colPos = -1;
            try {
                colPos = Integer.parseInt(colName);
            }
            catch (final NumberFormatException nfe) {
                Ansi92SQLGenerator.OUT.log(Level.FINEST, "column name is passed.Do nothing");
            }
            colName = ((colPos > 0) ? colName : this.getDBSpecificColumnName(colName));
            buff.append(colName);
            if (!orders[i]) {
                buff.append(" DESC");
            }
            final String nullsSortOrder = this.getNullsSortOrder(orders[i], null);
            if (nullsSortOrder != null) {
                buff.append(" ").append(nullsSortOrder).append(" ");
            }
            if (i + 1 < columns.length) {
                buff.append(",");
            }
        }
        return buff.toString();
    }
    
    protected String getNullsSortOrder(final boolean isAscending, final Boolean isNullsFirst) {
        return null;
    }
    
    protected String getWhereClause(final String whereClause, final List allTables, final List joins) throws QueryConstructionException {
        return whereClause;
    }
    
    protected String getGroupByClause(final List groupByColumns) throws QueryConstructionException {
        return this.getGroupByClause(groupByColumns, null);
    }
    
    protected String getGroupByClause(final List groupByColumns, final List<Table> derivedTableList) throws QueryConstructionException {
        if (groupByColumns == null || groupByColumns.isEmpty()) {
            return null;
        }
        final StringBuilder groupByBuffer = new StringBuilder(100);
        groupByBuffer.append(" GROUP BY ");
        for (int i = 0; i < groupByColumns.size(); ++i) {
            if (groupByColumns.get(i) instanceof GroupByColumn) {
                final GroupByColumn gbc = groupByColumns.get(i);
                final Column column = gbc.getGroupByColumn();
                final boolean casesensitive = gbc.isCaseSensitive();
                this.processColumn(column, groupByBuffer, casesensitive, derivedTableList, Clause.GROUPBY);
            }
            else if (groupByColumns.get(i) instanceof Column) {
                final Column column2 = groupByColumns.get(i);
                this.processColumn(column2, groupByBuffer, true, derivedTableList, Clause.GROUPBY);
            }
            if (i + 1 < groupByColumns.size()) {
                groupByBuffer.append(",");
            }
        }
        return groupByBuffer.toString();
    }
    
    protected String getGroupByClause(final GroupByClause groupByClause) throws QueryConstructionException {
        return this.getGroupByClause(groupByClause, null);
    }
    
    @Override
    public String getGroupByClause(final GroupByClause groupByClause, final List<Table> derivedTableList) throws QueryConstructionException {
        if (groupByClause == null) {
            return null;
        }
        final List groupByColumns = groupByClause.getGroupByColumns();
        if (groupByColumns.isEmpty()) {
            return null;
        }
        final StringBuilder buff = new StringBuilder(100);
        buff.append("GROUP BY ");
        for (int noOfGroupByColumns = groupByColumns.size(), i = 0; i < noOfGroupByColumns; ++i) {
            if (groupByColumns.get(i) instanceof GroupByColumn) {
                final GroupByColumn gbc = groupByColumns.get(i);
                final Column column = gbc.getGroupByColumn();
                final boolean casesensitive = gbc.isCaseSensitive();
                this.processColumn(column, buff, casesensitive, derivedTableList, Clause.GROUPBY);
            }
            else if (groupByColumns.get(i) instanceof Column) {
                final Column column2 = groupByColumns.get(i);
                this.processColumn(column2, buff, true, derivedTableList, Clause.GROUPBY);
            }
            if (i + 1 < noOfGroupByColumns) {
                buff.append(", ");
            }
        }
        final Criteria criteriaForHavingClause = groupByClause.getCriteriaForHavingClause();
        if (criteriaForHavingClause != null) {
            buff.append(" HAVING ");
            buff.append(this.formWhereClause(criteriaForHavingClause, true, null));
        }
        return buff.toString();
    }
    
    protected void processSortColumn(final SortColumn sortColumn, final StringBuilder buffer, final List selectColumns, final List<SortColumn> sortColumns, final List<Table> derivedTableList) throws QueryConstructionException {
        this.processSortColumn(sortColumn, buffer, selectColumns, derivedTableList);
    }
    
    protected void processSortColumn(final SortColumn sortColumn, final StringBuilder buffer, final List selectColumns, final List<Table> derivedTableList) throws QueryConstructionException {
        int sortColIdx = selectColumns.indexOf(sortColumn.getColumn());
        sortColIdx = ((sortColIdx != -1) ? (sortColIdx + 1) : sortColIdx);
        if (sortColIdx == -1) {
            final StringBuilder newBuffer = new StringBuilder();
            if (sortColumn.getColumn() instanceof Function) {
                this.noAppendForVarchar = Boolean.TRUE;
            }
            this.processColumn(sortColumn.getColumn(), newBuffer, sortColumn.isCaseSensitive(), derivedTableList, Clause.ORDERBY);
            buffer.append(" ").append(newBuffer.toString());
        }
        else {
            buffer.append(sortColIdx);
        }
    }
    
    protected String getOrderByClause(final List sortColumns, final List selectColumns, final List<Table> derivedTableList, final List<Column> isCaseSensitive) throws QueryConstructionException {
        if (sortColumns.isEmpty()) {
            return null;
        }
        StringBuilder orderByBuffer = new StringBuilder(100);
        for (int sortColsSize = sortColumns.size(), i = 0; i < sortColsSize; ++i) {
            final SortColumn sortCol = sortColumns.get(i);
            if (sortCol.isCaseSensitive()) {
                isCaseSensitive.add(sortCol.getColumn());
            }
            if (sortCol == SortColumn.NULL_COLUMN) {
                Ansi92SQLGenerator.OUT.log(Level.WARNING, "ORDER BY NULL is not supported in postgres/mssql DBs hence ignored");
            }
            else {
                this.processSortColumn(sortCol, orderByBuffer, selectColumns, sortColumns, derivedTableList);
                final boolean ascending = sortCol.isAscending();
                final Boolean isnullsFirst = sortCol.isNullsFirst();
                if (!ascending) {
                    orderByBuffer.append(" DESC");
                }
                final String nullsSortOrder = this.getNullsSortOrder(ascending, isnullsFirst);
                if (nullsSortOrder != null) {
                    orderByBuffer.append(" ").append(nullsSortOrder).append(" ");
                }
                if (i + 1 < sortColsSize) {
                    orderByBuffer.append(", ");
                }
            }
        }
        if (orderByBuffer.length() > 0) {
            orderByBuffer = new StringBuilder(" ORDER BY ").append((CharSequence)orderByBuffer);
            return orderByBuffer.toString();
        }
        return null;
    }
    
    protected StringBuilder appendNulls(final Column column, final List<Table> derivedTableList, final StringBuilder orderByBuffer, final String nullsSortOrder) throws QueryConstructionException {
        return orderByBuffer;
    }
    
    protected boolean isNumeric(final Object value) {
        return value instanceof Number;
    }
    
    protected boolean isNumeric(final int type) {
        return type != 12 && type != 1 && (type == 4 || type == -5 || type == 6 || type == 8 || type == 2 || type == 3 || type == -6);
    }
    
    @Override
    public String formWhereClause(final Criteria criteria) throws QueryConstructionException {
        if (criteria == null) {
            throw new QueryConstructionException("Criteria object is null");
        }
        return this.formWhereClause(criteria, false, null);
    }
    
    protected String formWhereClause(final Criteria criteria, final boolean groupByCriteria) throws QueryConstructionException {
        return this.formWhereClause(criteria, groupByCriteria, new ArrayList(1), null);
    }
    
    protected String formWhereClause(final Criteria criteria, final boolean groupByCriteria, final List<Table> derivedTableList) throws QueryConstructionException {
        return this.formWhereClause(criteria, groupByCriteria, new ArrayList(1), derivedTableList);
    }
    
    protected String formWhereClause(final Criteria criteria, final boolean groupByCriteria, final List caseSensitiveColumns, final List<Table> derivedTableList) throws QueryConstructionException {
        if (criteria == null) {
            return null;
        }
        final StringBuilder whereBuffer = new StringBuilder(200);
        this.formWhereClause(criteria, whereBuffer, groupByCriteria, caseSensitiveColumns, derivedTableList);
        final String whereClauseStr = String.valueOf(whereBuffer);
        if (whereClauseStr.equals("") || whereClauseStr.equals("()")) {
            return null;
        }
        return " ( " + whereClauseStr + " ) ";
    }
    
    private void formWhereClause(final Criteria criteria, final StringBuilder whereBuffer, final boolean groupByCriteria, final List<String> caseSensitiveColumns, final List<Table> derivedTableList) throws QueryConstructionException {
        Ansi92SQLGenerator.OUT.log(Level.FINE, "<ENTER>: Criteria={0}", criteria);
        if (criteria.isNegate()) {
            whereBuffer.append("not ( ");
        }
        final Criteria leftCriteria = criteria.getLeftCriteria();
        final Criteria rightCriteria = criteria.getRightCriteria();
        if (leftCriteria == null || rightCriteria == null) {
            Column column = criteria.getColumn();
            String lhString = null;
            if (!groupByCriteria) {
                if (!(column instanceof LocaleColumn) && column.getColumn() != null) {
                    if (column.getColumnAlias() == null) {
                        throw new QueryConstructionException("Cannot have aggregate columns in WHERE and JOIN clause :: " + column);
                    }
                    DerivedTable derivedTable = null;
                    if (derivedTableList != null && !derivedTableList.isEmpty()) {
                        for (final Table tab : derivedTableList) {
                            if (tab instanceof DerivedTable) {
                                final DerivedTable dt = (DerivedTable)tab;
                                if (dt.getSubQuery().getSelectColumns().contains(column)) {
                                    derivedTable = dt;
                                    break;
                                }
                                continue;
                            }
                        }
                    }
                    if (derivedTable == null) {
                        throw new QueryConstructionException("Cannot have aggregate columns in WHERE and JOIN clause :: " + column);
                    }
                }
                if (column instanceof LocaleColumn) {
                    column = column.getColumn();
                }
                lhString = this.lhsOfCriterion(column, criteria.isCaseSensitive(), criteria.getComparator(), derivedTableList);
            }
            else {
                if (column.getColumn() == null) {
                    Ansi92SQLGenerator.OUT.log(Level.WARNING, "Criter8ia based on Non-Aggregate Columns should be avoided in GroupByHaving Clause");
                }
                lhString = this.lhsOfCriterion_ForAggrColumn(column, derivedTableList, criteria.isCaseSensitive());
            }
            if (criteria.getValue() != null && criteria.getValue().getClass().isArray() && (criteria.getComparator() == 2 || criteria.getComparator() == 3)) {
                final int type = column.getType();
                final String mixed = this.expandMultiValuesForLike(column, type, criteria.getComparator(), criteria.getValue(), criteria.isCaseSensitive(), lhString, derivedTableList);
                whereBuffer.append(mixed);
            }
            else {
                final String rhString = this.rhsOfCriterion(column, criteria.getComparator(), criteria.getValue(), criteria.isCaseSensitive(), lhString, derivedTableList);
                whereBuffer.append(lhString);
                whereBuffer.append(rhString);
            }
        }
        else if (leftCriteria != null) {
            whereBuffer.append("( ");
            this.formWhereClause(leftCriteria, whereBuffer, groupByCriteria, caseSensitiveColumns, derivedTableList);
            whereBuffer.append(" )");
        }
        if (rightCriteria != null) {
            whereBuffer.append(criteria.getOperator());
            whereBuffer.append("( ");
            this.formWhereClause(rightCriteria, whereBuffer, groupByCriteria, caseSensitiveColumns, derivedTableList);
            whereBuffer.append(" )");
        }
        if (criteria.isNegate()) {
            whereBuffer.append(" )");
        }
    }
    
    protected String expandMultiValuesForLike(final Column column, final int type, final int comparator, final Object value, final boolean isCaseSensitive, final String lhString, final List<Table> derivedTableList) throws QueryConstructionException {
        final StringBuilder valStr = new StringBuilder("");
        final int len = Array.getLength(value);
        final String comparatorOpr = (comparator == 2) ? " OR " : " AND ";
        for (int i = 0; i < len; ++i) {
            if (!(value instanceof String[])) {
                throw new QueryConstructionException("Criteria " + this + ". Input array \" " + value.toString() + " \" received, which cannot be used with QueryConstants LIKE/NOT_LIKE");
            }
            final Object Value = Array.get(value, i);
            if (i != 0) {
                valStr.append(comparatorOpr);
            }
            final Object val = this.rhsOfCriterion(column, comparator, Value, isCaseSensitive, lhString, derivedTableList);
            valStr.append(lhString);
            valStr.append(val);
        }
        return valStr.toString();
    }
    
    @Override
    public String formSelectClause(final List columnsList) throws QueryConstructionException {
        return this.formSelectClause(columnsList, new ArrayList(1), null);
    }
    
    protected String formSelectClause(final List columnsList, final List caseSensitiveColumns, final SelectQuery sq) throws QueryConstructionException {
        if (columnsList == null || columnsList.isEmpty()) {
            throw new QueryConstructionException("Select columns not given");
        }
        final StringBuilder selectClause = new StringBuilder(250);
        for (int columnsLength = columnsList.size(), i = 0; i < columnsLength; ++i) {
            final int checkLength = i;
            final Column column = columnsList.get(i);
            final StringBuilder colBuffer = new StringBuilder(40);
            this.processColumn(column, colBuffer, caseSensitiveColumns.contains(column), (sq == null) ? null : sq.getDerivedTables(), (sq != null && ((SelectQueryImpl)sq).isSubQuery()) ? Clause.WHERE : Clause.SELECT);
            selectClause.append(colBuffer.toString());
            this.getAliasedColumn(sq, column, selectClause);
            if (checkLength + 1 < columnsLength) {
                selectClause.append(", ");
            }
        }
        return selectClause.toString();
    }
    
    protected void getAliasedColumn(final Column column, final StringBuilder buffer) throws QueryConstructionException {
        this.getAliasedColumn(null, column, buffer);
    }
    
    protected void getAliasedColumn(final SelectQuery sq, final Column column, final StringBuilder buffer) throws QueryConstructionException {
        final String columnAlias = column.getColumnAlias();
        if (columnAlias != null) {
            this.validateColumnAlias(columnAlias);
        }
        final String columnName = column.getColumnName();
        if ((columnAlias != null && columnName != null && !columnName.equals(columnAlias)) || (columnAlias != null && (column.getFunction() != 0 || column instanceof Function || column instanceof Operation)) || column.isEncrypted()) {
            buffer.append(" AS ");
            buffer.append(this.getDBSpecificColumnAlias(columnAlias));
        }
        else if (column.getDefinition() != null && column.getDefinition().isDynamic() && (columnAlias == null || columnName.equals(columnAlias))) {
            buffer.append(" AS ");
            buffer.append(this.getDBSpecificColumnAlias(columnAlias));
        }
    }
    
    private void validateColumnAlias(final String columnAlias) throws QueryConstructionException {
        if (columnAlias.contains("\\")) {
            throw new QueryConstructionException("Column alias cannot have characters ::( \\ )");
        }
    }
    
    protected boolean isAliasedColumnOfDerivedTable(final List<Table> derivedTableList, final String tableAlias, final String columnName) {
        final Table derivedTable = this.getDerivedTable(derivedTableList, tableAlias);
        return derivedTable != null && derivedTable instanceof DerivedTable && this.isAliasedColumnOfDerivedTable(((DerivedTable)derivedTable).getSubQuery(), tableAlias, columnName);
    }
    
    private boolean isAliasedColumnOfDerivedTable(final Query query, final String tableAlias, final String columnName) {
        List<Column> selectColumns = null;
        if (query instanceof SelectQuery) {
            final SelectQuery sQuery = (SelectQuery)query;
            selectColumns = sQuery.getSelectColumns();
        }
        else if (query instanceof UnionQuery) {
            final UnionQuery uQuery = (UnionQuery)query;
            selectColumns = uQuery.getSelectColumns();
        }
        for (final Column selectColumn : selectColumns) {
            if (selectColumn.getDefinition() != null && selectColumn.getDefinition().isDynamic() && ((selectColumn.getColumnAlias() == null && selectColumn.getColumnName().equals(columnName)) || (selectColumn.getColumnAlias() != null && selectColumn.getColumnAlias().equals(columnName)))) {
                Ansi92SQLGenerator.OUT.fine("isAliasedColumnOfDerivedTable returning true for dynamic column.");
                return true;
            }
            if (selectColumn.getColumnName() != null && !selectColumn.getColumnName().equals(selectColumn.getColumnAlias()) && selectColumn.getColumnAlias().equals(columnName)) {
                Ansi92SQLGenerator.OUT.fine("isAliasedColumnOfDerivedTable returning true.");
                return true;
            }
            if (selectColumn.getColumn() != null && selectColumn.getColumnAlias() != null && selectColumn.getColumnAlias().equals(columnName)) {
                Ansi92SQLGenerator.OUT.fine("isAliasedColumnOfDerivedTable returning true for DerivedTable aggregate column alias.");
                return true;
            }
            if (selectColumn.getColumnName() == null && selectColumn.getColumnAlias() != null && selectColumn.getColumnAlias().equals(columnName)) {
                Ansi92SQLGenerator.OUT.fine("isAliasedColumnOfDerivedTable returning true for DerivedTable function column alias");
                return true;
            }
            if (selectColumn.isEncrypted() && ((selectColumn.getColumnAlias() == null && selectColumn.getColumnName().equals(columnName)) || (selectColumn.getColumnAlias() != null && selectColumn.getColumnAlias().equals(columnName)))) {
                Ansi92SQLGenerator.OUT.fine("isAliasedColumnOfDerivedTable returning true for encrypted columns as decrypt function is applied in inner query and it has column alias with column name set");
                return true;
            }
        }
        Ansi92SQLGenerator.OUT.fine("isAliasedColumnOfDerivedTable returning false ....");
        return false;
    }
    
    private Table getDerivedTable(final List<Table> derivedTableList, final String tableAlias) {
        if (derivedTableList != null && !derivedTableList.isEmpty()) {
            for (final Table table : derivedTableList) {
                if (table.getTableAlias() != null && table.getTableAlias().equals(tableAlias)) {
                    return table;
                }
            }
        }
        return null;
    }
    
    protected String getDBSpecificColumnAlias(final String columnAlias) {
        return this.getDBSpecificColumnName(columnAlias);
    }
    
    protected String processSimpleColumn(final Column column, final Clause columnBelongsToClause) {
        final String tableName = column.getTableAlias();
        final String columnName = column.getColumnName();
        String retStr = null;
        if (tableName != null) {
            if (column.getDefinition() != null && column.getDefinition().isDynamic()) {
                retStr = this.getDCSQLGeneratorForTable(tableName).getSQLForCast(this.getDBSpecificTableName(tableName) + "." + this.getDCSQLGeneratorForTable(tableName).getDCSpecificColumnName(columnName), column);
            }
            else {
                retStr = this.getDBSpecificTableName(tableName) + "." + this.getDBSpecificColumnName(columnName);
            }
        }
        else if (column.getDefinition() != null && column.getDefinition().isDynamic()) {
            retStr = this.getDCSQLGeneratorForTable(column.getDefinition().getTableName()).getSQLForCast(this.getDCSQLGeneratorForTable(column.getDefinition().getTableName()).getDCSpecificColumnName(columnName), column);
        }
        else {
            retStr = this.getDBSpecificColumnName(columnName);
        }
        return this.getDBSpecificDecryptionString(column, retStr);
    }
    
    protected String processSimpleColumn(final Column column, final boolean caseSensitive, final List<Table> derivedTableList, final Clause columnBelongsToClause) {
        final int type = column.getType();
        String retVal = null;
        final String tableName = column.getTableAlias();
        final String columnName = column.getColumnName();
        if (tableName != null) {
            if (column.getDefinition() != null && column.getDefinition().isDynamic()) {
                if (column.getDefinition().getTableName().equals(tableName)) {
                    retVal = this.getDCSQLGeneratorForTable(tableName).getSQLForCast(this.getDBSpecificTableName(tableName) + "." + this.getDCSQLGeneratorForTable(tableName).getDCSpecificColumnName(columnName), column);
                }
                else {
                    retVal = this.getDCSQLGeneratorForTable(column.getDefinition().getTableName()).getSQLForCast(this.getDBSpecificTableName(tableName) + "." + this.getDCSQLGeneratorForTable(column.getDefinition().getTableName()).getDCSpecificColumnName(columnName), column);
                }
            }
            else {
                retVal = this.getDBSpecificTableName(tableName) + "." + this.getDBSpecificColumnName(columnName);
            }
        }
        else if (column.getDefinition() != null && column.getDefinition().isDynamic()) {
            retVal = this.getDCSQLGeneratorForTable(column.getDefinition().getTableName()).getSQLForCast(this.getDCSQLGeneratorForTable(column.getDefinition().getTableName()).getDCSpecificColumnName(columnName), column);
        }
        else {
            retVal = this.getDBSpecificColumnName(columnName);
        }
        retVal = this.getDBSpecificDecryptionString(column, retVal);
        if (column.getDataType() != null) {
            return this.handleCaseSensitive(retVal, caseSensitive, column.getDataType(), column.isEncrypted(), columnBelongsToClause);
        }
        return this.handleCaseSensitive(retVal, caseSensitive, type, column.isEncrypted(), columnBelongsToClause);
    }
    
    protected String handleCaseSensitive(final String retVal, final boolean isCaseSensitive, final int type, final boolean isEncrypted, final Clause columnBelongsToClause) {
        if (DataTypeUtil.isUDT(type)) {
            return DataTypeManager.getDataTypeDefinition(type).getDTSQLGenerator(this.getDBType()).handleCaseSensitive(retVal, isCaseSensitive, isEncrypted, columnBelongsToClause);
        }
        if (!isCaseSensitive && (type == 12 || type == 1)) {
            return "UPPER(" + retVal + ")";
        }
        return retVal;
    }
    
    protected String handleCaseSensitive(final String retVal, final boolean isCaseSensitive, final String dataType, final boolean isEncrypted, final Clause columnBelongsToClause) {
        if (DataTypeUtil.isUDT(dataType)) {
            return DataTypeManager.getDataTypeDefinition(dataType).getDTSQLGenerator(this.getDBType()).handleCaseSensitive(retVal, isCaseSensitive, isEncrypted, columnBelongsToClause);
        }
        return this.handleCaseSensitive(retVal, isCaseSensitive, MetaDataUtil.getJavaSQLType(dataType), isEncrypted, columnBelongsToClause);
    }
    
    protected void processColumn(final Column column, final StringBuilder columnBuffer, final List<Table> derivedTableList, final Clause columnBelongsToClause) throws QueryConstructionException {
        this.processColumn(column, columnBuffer, true, derivedTableList, columnBelongsToClause);
    }
    
    protected void processColumn(final Column column, final StringBuilder columnBuffer, final boolean caseSensitive, final List<Table> derivedTableList, final Clause columnBelongsToClause) throws QueryConstructionException {
        this.processColumn(column, columnBuffer, caseSensitive, derivedTableList, columnBelongsToClause, false);
    }
    
    protected void processColumn(final Column column, final StringBuilder columnBuffer, final boolean caseSensitive, final List<Table> derivedTableList, final Clause columnBelongsToClause, Boolean isSubQueryAliasNotNeeded) throws QueryConstructionException {
        if (column == null) {
            return;
        }
        if (column instanceof DerivedColumn) {
            if (columnBelongsToClause.equals(Clause.GROUPBY) || columnBelongsToClause.equals(Clause.WHERE)) {
                isSubQueryAliasNotNeeded = true;
            }
            final DerivedColumn dc = (DerivedColumn)column;
            final Query subQuery = dc.getSubQuery();
            columnBuffer.append("( ");
            ((SelectQueryImpl)subQuery).markAsSubQuery();
            final String queryStr = this.getSQLForSelect(subQuery);
            ((SelectQueryImpl)subQuery).clearSubQueryFlag();
            columnBuffer.append(queryStr);
            columnBuffer.append(") ");
            if (!isSubQueryAliasNotNeeded) {
                columnBuffer.append(this.getDBSpecificColumnName(dc.getColumnName()));
                columnBuffer.append(" ");
            }
            return;
        }
        if (column instanceof CaseExpression) {
            this.processCaseExpression((CaseExpression)column, columnBuffer);
            return;
        }
        if (column instanceof Function) {
            this.processFunction((Function)column, columnBuffer);
            return;
        }
        if (column instanceof Operation) {
            this.processOperation((Operation)column, columnBuffer);
            return;
        }
        final Column subColumn = column.getColumn();
        if (subColumn != null && !(column instanceof LocaleColumn)) {
            this.processColumn(subColumn, columnBuffer, caseSensitive, derivedTableList, columnBelongsToClause);
        }
        else {
            columnBuffer.append(this.processSimpleColumn(column, caseSensitive, derivedTableList, columnBelongsToClause));
        }
        final int function = column.getFunction();
        if (function != 0 && function == 1) {
            columnBuffer.insert(0, "DISTINCT(");
            columnBuffer.append(")");
        }
        else if (function != 0 && function == 2) {
            columnBuffer.insert(0, "COUNT(");
            columnBuffer.append(")");
        }
        else if (function != 0 && function == 3) {
            columnBuffer.insert(0, "MIN(");
            columnBuffer.append(")");
        }
        else if (function != 0 && function == 4) {
            columnBuffer.insert(0, "MAX(");
            columnBuffer.append(")");
        }
        else if (function != 0 && function == 5) {
            columnBuffer.insert(0, "SUM(");
            columnBuffer.append(")");
        }
        else if (function != 0 && function == 6) {
            columnBuffer.insert(0, "AVG(");
            columnBuffer.append(")");
        }
    }
    
    protected String getStringForOperation(final Operation.operationType operType) {
        if (operType == Operation.operationType.ADD) {
            return " + ";
        }
        if (operType == Operation.operationType.SUBTRACT) {
            return " - ";
        }
        if (operType == Operation.operationType.MULTIPLY) {
            return " * ";
        }
        if (operType == Operation.operationType.DIVIDE) {
            return " / ";
        }
        return " % ";
    }
    
    protected void processOperation(final Operation column, final StringBuilder sb) throws QueryConstructionException {
        this.processOperation(column, sb, true);
    }
    
    protected void processOperation(final Operation operation, final StringBuilder sb, final boolean caseSensitive) throws QueryConstructionException {
        if (!operation.isArgument() && operation.getType() == 1111) {
            throw new QueryConstructionException("Type must be set for the Expression columns and not set for this expression :: [" + operation + "]");
        }
        final Object lhsArg = operation.getLHSArgument();
        final Object rhsArg = operation.getRHSArgument();
        sb.append("(");
        final StringBuilder lhs = new StringBuilder();
        final StringBuilder rhs = new StringBuilder();
        this.processArgument(lhs, lhsArg, caseSensitive);
        sb.append((CharSequence)lhs);
        sb.append(this.getStringForOperation(operation.getOperation()));
        this.processArgument(rhs, rhsArg, caseSensitive);
        sb.append((CharSequence)rhs);
        sb.append(")");
    }
    
    protected void processFunction(final Function column, final StringBuilder sb) throws QueryConstructionException {
        this.processFunction(column, sb, true);
    }
    
    protected void processFunction(final Object columnObject, final StringBuilder sb, final List<Table> derivedTableList, final Clause columnBelongsToClause) throws QueryConstructionException {
        DerivedTable derivedTable = null;
        final String columnAlias = (columnObject instanceof Column && ((Column)columnObject).getColumn() != null) ? ((Column)columnObject).getColumnAlias() : ((columnObject instanceof Function) ? ((Function)columnObject).getColumnAlias() : null);
        if (derivedTableList != null) {
            for (final Table dt : derivedTableList) {
                if (((DerivedTable)dt).getSubQuery().getSelectColumns().contains(columnObject)) {
                    derivedTable = (DerivedTable)dt;
                    break;
                }
            }
        }
        if (columnObject instanceof Column && ((Column)columnObject).getColumn() != null) {
            final Column column = (Column)columnObject;
            if (derivedTable == null || columnAlias == null) {
                throw new QueryConstructionException("Cannot have aggregate columns in WHERE and JOIN clause :: " + column);
            }
            sb.append(this.getDBSpecificColumnAlias(columnAlias));
        }
        else if (columnObject instanceof Function) {
            final Function func = (Function)columnObject;
            if (derivedTable == null || columnAlias == null) {
                this.processFunction(func, sb);
            }
            else {
                sb.append(this.getDBSpecificColumnAlias(columnAlias));
            }
        }
    }
    
    protected void processFunction(final Function function, final StringBuilder sb, final boolean caseSensitive) throws QueryConstructionException {
        final String functionName = function.getFunctionName();
        final Object[] args = function.getFunctionArguments();
        final String template = this.functionTemplates.getProperty(functionName);
        final String returnType = this.functionTemplates.getProperty(functionName + ".TYPE");
        if (function.getType() == 1111 && returnType != null) {
            final int type = MetaDataUtil.getJavaSQLType(returnType);
            function.setType(type);
        }
        if (!function.isArgument() && function.getType() == 1111) {
            throw new QueryConstructionException("Type must be set for the Expression columns and not set for this expression :: [" + function + "]");
        }
        if (template == null) {
            sb.append(functionName);
            sb.append("(");
            for (int i = 0; i < args.length; ++i) {
                if (i > 0) {
                    sb.append(", ");
                }
                this.processArgument(sb, args[i], caseSensitive);
            }
            sb.append(")");
        }
        else if (template.contains("(") && Boolean.valueOf(AppResources.getString("function.append.no.extra.bracket", "false"))) {
            final Object[] strArgs = this.convertToStrObjectArray(args, caseSensitive);
            sb.append(MessageFormat.format(template, strArgs));
        }
        else {
            sb.append("(");
            final Object[] strArgs = this.convertToStrObjectArray(args, caseSensitive);
            sb.append(MessageFormat.format(template, strArgs));
            sb.append(")");
        }
    }
    
    protected Object[] convertToStrObjectArray(final Object[] args, final boolean caseSensitive) throws QueryConstructionException {
        final Object[] strArgs = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            final StringBuilder sb = new StringBuilder("");
            this.processArgument(sb, args[i], caseSensitive);
            strArgs[i] = sb.toString();
            this.noAppendForVarchar = Boolean.FALSE;
        }
        return strArgs;
    }
    
    protected void processArgument(final StringBuilder sb, final Object value, final boolean caseSensitive) throws QueryConstructionException {
        if (value instanceof Column) {
            this.processColumn((Column)value, sb, caseSensitive, null, ((boolean)this.noAppendForVarchar) ? null : Clause.WHERE, true);
        }
        else if (value instanceof Criteria) {
            this.formWhereClause((Criteria)value, sb, caseSensitive, new ArrayList<String>(), null);
        }
        else if (value instanceof Date) {
            sb.append(this.getDateString(93, value));
        }
        else if (value instanceof String) {
            sb.append("'");
            sb.append(this.escapeSpecialCharacters(value.toString(), 1));
            sb.append("'");
        }
        else if (value instanceof Boolean) {
            sb.append(value ? "1" : "0");
        }
        else if (value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double || value instanceof BigDecimal) {
            sb.append("(");
            sb.append(value.toString());
            sb.append(")");
        }
        else if (value == null) {
            sb.append(" NULL ");
        }
        else if (value instanceof Function.ReservedParameter) {
            final Function.ReservedParameter dfp = (Function.ReservedParameter)value;
            sb.append(dfp.getParamValue());
        }
        else {
            sb.append(value.toString());
        }
    }
    
    protected void processCaseExpression(final CaseExpression caseExp, final StringBuilder columnBuffer) throws QueryConstructionException {
        final String columnName = caseExp.getColumnName();
        final String tableAlias = caseExp.getTableAlias();
        columnBuffer.append("(CASE ");
        if (columnName != null) {
            if (!columnName.equals("<DUMMY_COLUMN>")) {
                if (caseExp.getDefinition() != null && caseExp.getDefinition().isDynamic()) {
                    final String tableName = caseExp.getDefinition().getTableName();
                    if (tableAlias != null) {
                        columnBuffer.append(this.getDCSQLGeneratorForTable(tableName).getSQLForCast(this.getDBSpecificTableName(tableAlias) + "." + this.getDCSQLGeneratorForTable(tableName).getDCSpecificColumnName(tableName, columnName), caseExp));
                    }
                    else {
                        columnBuffer.append(this.getDCSQLGeneratorForTable(tableName).getSQLForCast(this.getDCSQLGeneratorForTable(tableName).getDCSpecificColumnName(tableName, columnName), caseExp));
                    }
                }
                else {
                    if (tableAlias != null) {
                        columnBuffer.append(this.getDBSpecificTableName(tableAlias));
                        columnBuffer.append(".");
                    }
                    columnBuffer.append(this.getDBSpecificColumnName(columnName));
                }
            }
            else {
                final Criteria expr = caseExp.getExpr();
                if (expr != null) {
                    columnBuffer.append(this.formWhereClause(expr, false, null));
                }
            }
            final List<CaseExpression.WhenExpr> whenExpressions = caseExp.getWhenExpressions();
            for (final CaseExpression.WhenExpr whenExpr : whenExpressions) {
                columnBuffer.append(" WHEN ");
                final Object expr2 = whenExpr.getExpr();
                if (expr2 instanceof Criteria) {
                    columnBuffer.append(this.formWhereClause((Criteria)expr2, false, null));
                }
                else if (expr2 instanceof Column) {
                    boolean aliasNotNeeded = Boolean.FALSE;
                    if (expr2 instanceof DerivedColumn) {
                        aliasNotNeeded = Boolean.TRUE;
                    }
                    final Column modifiedColumn = (Column)expr2;
                    this.processColumn(modifiedColumn, columnBuffer, false, null, Clause.WHERE, aliasNotNeeded);
                }
                else if (expr2 instanceof String || expr2 instanceof Date) {
                    columnBuffer.append("'");
                    columnBuffer.append(expr2);
                    columnBuffer.append("'");
                }
                else if (expr2 instanceof Boolean) {
                    columnBuffer.append("'");
                    columnBuffer.append(((boolean)Boolean.valueOf(expr2.toString())) ? 1 : 0);
                    columnBuffer.append("'");
                }
                else if (this.isNumeric(expr2)) {
                    columnBuffer.append(expr2);
                }
                else {
                    columnBuffer.append(expr2);
                }
                columnBuffer.append(" THEN ");
                final Object value = whenExpr.getValue();
                if (value instanceof String) {
                    columnBuffer.append("'");
                    columnBuffer.append(value);
                    columnBuffer.append("'");
                }
                else if (value instanceof Column) {
                    boolean aliasNotNeeded2 = Boolean.FALSE;
                    if (value instanceof DerivedColumn) {
                        aliasNotNeeded2 = Boolean.TRUE;
                    }
                    final Column modifiedColumn2 = (Column)whenExpr.getValue();
                    this.processColumn(modifiedColumn2, columnBuffer, false, null, Clause.SELECT, aliasNotNeeded2);
                }
                else if (value instanceof Boolean) {
                    this.processCaseExpressionValue(columnBuffer, (Boolean)value);
                }
                else {
                    columnBuffer.append(value);
                }
            }
            final Object elseVal = caseExp.getElseVal();
            if (elseVal != null) {
                columnBuffer.append(" ELSE ");
                if (elseVal instanceof String) {
                    columnBuffer.append("'");
                    columnBuffer.append(elseVal);
                    columnBuffer.append("'");
                }
                else if (elseVal instanceof Column) {
                    boolean aliasNotNeeded3 = Boolean.FALSE;
                    if (elseVal instanceof DerivedColumn) {
                        aliasNotNeeded3 = Boolean.TRUE;
                    }
                    final Column modifiedColumn3 = (Column)elseVal;
                    this.processColumn(modifiedColumn3, columnBuffer, false, null, Clause.SELECT, aliasNotNeeded3);
                }
                else if (elseVal instanceof Boolean) {
                    this.processCaseExpressionValue(columnBuffer, (Boolean)elseVal);
                }
                else {
                    columnBuffer.append(elseVal);
                }
            }
            columnBuffer.append(" END ");
            columnBuffer.append(") ");
            return;
        }
        throw new QueryConstructionException("Null column can not be provided for CaseExpressions");
    }
    
    protected void processCaseExpressionValue(final StringBuilder columnBuffer, final Boolean value) {
        columnBuffer.append(value);
    }
    
    protected void handleCaseSensitive(final Function function, final StringBuilder colBuffer, final boolean caseSensitive) {
    }
    
    protected String lhsOfCriterion(final Column column, final boolean caseSensitive, final int comparator, final List<Table> derivedTableList) throws QueryConstructionException {
        final StringBuilder colBuffer = new StringBuilder(40);
        if (column instanceof Function) {
            this.processFunction(column, colBuffer, derivedTableList, Clause.WHERE);
            final Function func = (Function)column;
            this.handleCaseSensitive(func, colBuffer, caseSensitive);
        }
        else if (column.getColumn() != null) {
            this.processFunction(column, colBuffer, derivedTableList, Clause.WHERE);
        }
        else if (column instanceof Operation) {
            this.processOperation((Operation)column, colBuffer);
        }
        else if (column instanceof DerivedColumn) {
            this.processColumn(column, colBuffer, caseSensitive, derivedTableList, Clause.WHERE);
        }
        else {
            colBuffer.append(this.processSimpleColumn(column, caseSensitive, derivedTableList, Clause.WHERE));
        }
        return colBuffer.toString();
    }
    
    protected String lhsOfCriterion_ForAggrColumn(final Column column, final List<Table> derivedTableList, final boolean caseSensitive) throws QueryConstructionException {
        final StringBuilder colBuffer = new StringBuilder(40);
        if (column instanceof Function) {
            final Function func = (Function)column;
            this.processFunction(func, colBuffer);
            this.handleCaseSensitive(func, colBuffer, caseSensitive);
        }
        else if (column instanceof Operation) {
            this.processOperation((Operation)column, colBuffer);
        }
        else {
            this.processColumn(column, colBuffer, derivedTableList, Clause.WHERE);
        }
        return colBuffer.toString();
    }
    
    protected String rhsOfCriterion(final Column column, int comparator, final Object value, final boolean isCaseSensitive, final String lhString, final List<Table> derivedTableList) throws QueryConstructionException {
        final StringBuilder rhsBuffer = new StringBuilder();
        if (column.getType() == 6 && (comparator == 0 || comparator == 1)) {
            comparator = ((comparator == 0) ? 2 : 3);
        }
        final String comparatorString = this.getComparatorString(column, comparator, isCaseSensitive);
        if (value == QueryConstants.PREPARED_STMT_CONST) {
            rhsBuffer.append(comparatorString);
            rhsBuffer.append(this.handleCaseSensitive(this.getDBSpecificDecryptionString(column, "?"), isCaseSensitive, column.getType(), column.isEncrypted(), Clause.WHERE));
        }
        else {
            final int type = column.getType();
            final boolean isExpression = column instanceof Function || column instanceof Operation;
            final String valueStr = this.getValue(value, column, comparator, isCaseSensitive, lhString, derivedTableList, Clause.WHERE);
            if (!valueStr.trim().equals("IS NULL") && !valueStr.trim().equals("IS NOT NULL")) {
                rhsBuffer.append(comparatorString);
            }
            rhsBuffer.append(valueStr);
        }
        return rhsBuffer.toString();
    }
    
    protected String getSimpleDateFormatStringFor(final int type) throws QueryConstructionException {
        switch (type) {
            case 91: {
                return "yyyy-MM-dd";
            }
            case 92: {
                return "HH:mm:ss";
            }
            case 93: {
                return "yyyy-MM-dd HH:mm:ss";
            }
            default: {
                throw new QueryConstructionException("Unknow type [" + type + "] received here");
            }
        }
    }
    
    protected String getDateString(final int type, final Object value) throws QueryConstructionException {
        SimpleDateFormat sdf = null;
        if (value != null && value instanceof Date) {
            switch (type) {
                case 91: {
                    sdf = new SimpleDateFormat(this.getSimpleDateFormatStringFor(type));
                    return sdf.format((Date)value);
                }
                case 92: {
                    sdf = new SimpleDateFormat(this.getSimpleDateFormatStringFor(type));
                    return sdf.format((Date)value);
                }
                case 93: {
                    sdf = new SimpleDateFormat(this.getSimpleDateFormatStringFor(type));
                    return sdf.format((Date)value);
                }
            }
        }
        else if (value != null && value instanceof String) {
            try {
                final Date date = this.getDateValueFromStr((String)value, type);
                sdf = new SimpleDateFormat(this.getSimpleDateFormatStringFor(type));
                return sdf.format(date);
            }
            catch (final IllegalArgumentException e) {
                throw new QueryConstructionException("Improper value [" + value + "] set for " + getSQLTypeAsString(type) + " column", QueryConstructionException.getErrorCodeForType(type), e);
            }
        }
        throw new QueryConstructionException("Improper value [" + value + "] set for " + getSQLTypeAsString(type) + " column", QueryConstructionException.getErrorCodeForType(type));
    }
    
    protected Date getDateValueFromStr(final String value, final int type) {
        switch (type) {
            case 91: {
                return java.sql.Date.valueOf(value);
            }
            case 92: {
                return Time.valueOf(value);
            }
            case 93: {
                return Timestamp.valueOf(value);
            }
            default: {
                return null;
            }
        }
    }
    
    protected String getValue(final Object value, final boolean isCaseSensitive) {
        String valueStr = "";
        if (isCaseSensitive) {
            valueStr = value.toString();
        }
        else {
            valueStr = value.toString().toUpperCase();
        }
        return valueStr;
    }
    
    protected boolean validateDateWithWildcards(final int type, final int comparator, final String date) throws QueryConstructionException {
        final Matcher matcher = Ansi92SQLGenerator.DATE_PATTERN.matcher(date);
        if (!matcher.matches()) {
            throw new QueryConstructionException("Improper value [" + date + "] set for " + getSQLTypeAsString(type) + " column", QueryConstructionException.getErrorCodeForType(type));
        }
        return true;
    }
    
    protected String getValue(final Object value, final Column column, int comparator, final boolean isCaseSensitive, final String lhString, final List<Table> derivedTableList, final Clause columnBelongsToClause) throws QueryConstructionException {
        final int type = column.getType();
        String valueStr = null;
        if (value == null) {
            if (comparator == 0 || comparator == 2 || comparator == 12 || comparator == 10 || comparator == 11) {
                valueStr = " IS NULL";
            }
            else {
                if (comparator != 1 && comparator != 3 && comparator != 13) {
                    throw new QueryConstructionException("Comparator for criteria when the value is null should be any one of the following : EQUAL | NOT_EQUAL | LIKE | NOT_LIKE | CONTAINS | NOT_CONTAINS");
                }
                valueStr = " IS NOT NULL";
            }
            return valueStr;
        }
        if (value instanceof DerivedColumn) {
            final StringBuilder colBuffer = new StringBuilder();
            final DerivedColumn dc = (DerivedColumn)value;
            final Query subQuery = dc.getSubQuery();
            colBuffer.append("( ");
            if (isCaseSensitive) {
                ((SelectQueryImpl)subQuery).markAsSubQuery();
            }
            final String queryStr = this.getSQLForSelect(subQuery);
            ((SelectQueryImpl)subQuery).clearSubQueryFlag();
            colBuffer.append(queryStr);
            colBuffer.append(") ");
            return colBuffer.toString();
        }
        if (value instanceof Function) {
            final StringBuilder colBuffer = new StringBuilder();
            this.processFunction(value, colBuffer, derivedTableList, columnBelongsToClause);
            return colBuffer.toString();
        }
        if (value instanceof Operation) {
            final StringBuilder colBuffer = new StringBuilder();
            this.processOperation((Operation)value, colBuffer);
            return colBuffer.toString();
        }
        if (value instanceof CaseExpression) {
            final StringBuilder colBuffer = new StringBuilder();
            this.processCaseExpression((CaseExpression)value, colBuffer);
            return colBuffer.toString();
        }
        if (value instanceof Column) {
            final Column col = (Column)value;
            String retString = "";
            if (col.getColumn() != null) {
                final StringBuilder colBuffer2 = new StringBuilder();
                this.processFunction(col, colBuffer2, derivedTableList, columnBelongsToClause);
                return colBuffer2.toString();
            }
            retString = this.processSimpleColumn((Column)value, isCaseSensitive, derivedTableList, columnBelongsToClause);
            if (comparator == 8 || comparator == 9) {
                return "(" + retString + ")";
            }
            return retString;
        }
        else {
            if (comparator == 14 || comparator == 15) {
                final StringBuilder retString2 = new StringBuilder();
                Object from = Array.get(value, 0);
                Object to = Array.get(value, 1);
                if (from instanceof DerivedColumn) {
                    this.processColumn((Column)from, retString2, isCaseSensitive, derivedTableList, columnBelongsToClause);
                }
                else if (from instanceof Column) {
                    retString2.append(this.processSimpleColumn((Column)from, isCaseSensitive, null, columnBelongsToClause));
                }
                else if (DataTypeUtil.isUDT(column.getDataType())) {
                    final DataTypeDefinition dtd = DataTypeManager.getDataTypeDefinition(column.getDataType());
                    try {
                        final ColumnDefinition cd = column.getDefinition();
                        if (cd != null) {
                            from = dtd.getDTTransformer(this.getDBType()).transform(cd.getTableName(), cd.getColumnName(), from, cd.getDataType());
                        }
                    }
                    catch (final Exception e) {
                        throw new QueryConstructionException("Exception while transforming data.", e);
                    }
                    retString2.append(dtd.getDTSQLGenerator(this.getDBType()).getValue(from, comparator, isCaseSensitive));
                }
                else if (type == 91 || type == 93 || type == 92) {
                    retString2.append("'").append(this.getDateString(type, from)).append("'");
                }
                else if (type == 16) {
                    retString2.append("'").append(this.getValueStringForBoolean(from.toString())).append("'");
                }
                else {
                    retString2.append(this.getValueString(from.toString(), column, comparator, false, isCaseSensitive));
                }
                retString2.append(" AND ");
                if (to instanceof DerivedColumn) {
                    this.processColumn((Column)to, retString2, isCaseSensitive, derivedTableList, columnBelongsToClause);
                }
                else if (to instanceof Column) {
                    retString2.append(this.processSimpleColumn((Column)to, isCaseSensitive, null, columnBelongsToClause));
                }
                else if (DataTypeUtil.isUDT(column.getDataType())) {
                    final DataTypeDefinition dtd = DataTypeManager.getDataTypeDefinition(column.getDataType());
                    try {
                        final ColumnDefinition cd = column.getDefinition();
                        if (cd != null) {
                            to = dtd.getDTTransformer(this.getDBType()).transform(cd.getTableName(), cd.getColumnName(), to, cd.getDataType());
                        }
                    }
                    catch (final Exception e) {
                        throw new QueryConstructionException("Exception while transforming data.", e);
                    }
                    retString2.append(dtd.getDTSQLGenerator(this.getDBType()).getValue(to, comparator, isCaseSensitive));
                }
                else if (type == 91 || type == 93 || type == 92) {
                    retString2.append("'").append(this.getDateString(type, to)).append("'");
                }
                else if (type == 16) {
                    retString2.append("'").append(this.getValueStringForBoolean(to.toString())).append("'");
                }
                else {
                    retString2.append(this.getValueString(to.toString(), column, comparator, false, isCaseSensitive));
                }
                Ansi92SQLGenerator.OUT.fine("UUID : " + (Object)retString2);
                return retString2.toString();
            }
            if (type == 16 && comparator != 8 && comparator != 9) {
                return this.getValueStringForBoolean(value.toString());
            }
            Label_1203: {
                Label_1152: {
                    if (null != column.getDataType()) {
                        if (!DataTypeUtil.isUDT(column.getDataType())) {
                            break Label_1152;
                        }
                    }
                    else if (!DataTypeUtil.isUDT(column.getType())) {
                        break Label_1152;
                    }
                    final DataTypeDefinition dtd2 = DataTypeManager.getDataTypeDefinition(column.getDataType());
                    Object transformedVal = value;
                    try {
                        final ColumnDefinition cd2 = column.getDefinition();
                        if (cd2 != null) {
                            transformedVal = dtd2.getDTTransformer(this.getDBType()).transform(cd2.getTableName(), cd2.getColumnName(), value, cd2.getDataType());
                        }
                    }
                    catch (final Exception e2) {
                        throw new QueryConstructionException("Exception while transforming data.", e2);
                    }
                    valueStr = dtd2.getDTSQLGenerator(this.getDBType()).getValue(transformedVal, comparator, isCaseSensitive);
                    break Label_1203;
                }
                if (value instanceof Date && value != null) {
                    return "'" + this.getDateString(type, value) + "'";
                }
                valueStr = this.getValue(value, isCaseSensitive);
            }
            if (comparator == 12 || comparator == 13) {
                if (type == 91 || type == 93 || type == 92) {
                    this.validateDateWithWildcards(type, comparator, valueStr);
                }
                valueStr = "*" + valueStr + "*";
                comparator = 2;
            }
            else if (comparator == 0 || comparator == 1 || comparator == 5 || comparator == 4 || comparator == 7 || comparator == 6) {
                if (type == 91 || type == 93 || type == 92) {
                    valueStr = this.getDateString(type, valueStr);
                }
            }
            else if (comparator == 11) {
                if (type == 91 || type == 93 || type == 92) {
                    this.validateDateWithWildcards(type, comparator, valueStr);
                }
                valueStr = "*" + valueStr;
                comparator = 2;
            }
            else if (comparator == 10) {
                if (type == 91 || type == 93 || type == 92) {
                    this.validateDateWithWildcards(type, comparator, valueStr);
                }
                valueStr += "*";
                comparator = 2;
            }
            if (comparator != 2 && comparator != 3) {
                if (comparator == 8 || comparator == 9) {
                    valueStr = this.getValueForIn(value, column, comparator, isCaseSensitive, lhString);
                }
                else {
                    valueStr = this.getValueString(valueStr, column, comparator, false, isCaseSensitive);
                }
            }
            else {
                final boolean hasWildCard = valueStr.indexOf(Ansi92SQLGenerator.stringWildCard) != -1 || valueStr.indexOf(Ansi92SQLGenerator.charWildCard) != -1;
                final boolean hasDBWildCard = valueStr.indexOf(String.valueOf(this.getWildCardForString())) != -1 || valueStr.indexOf(String.valueOf(this.getWildCardForChar())) != -1;
                final boolean hasSquarebracket = valueStr.indexOf(String.valueOf('[')) != -1;
                if (hasDBWildCard) {
                    valueStr = this.escapeDBWildCard(valueStr, type, comparator);
                }
                if (hasWildCard) {
                    valueStr = this.replaceWildCards(valueStr, type);
                }
                if (hasSquarebracket) {
                    valueStr = this.escapeSquareBracket(valueStr);
                }
                if (type == 91 || type == 93 || type == 92) {
                    this.validateDateWithWildcards(type, comparator, valueStr);
                }
                valueStr = this.getValueString(valueStr, column, comparator, hasWildCard, isCaseSensitive);
                valueStr = this.appendEscapeString(valueStr, column, comparator);
            }
            return valueStr;
        }
    }
    
    protected String escapeSquareBracket(final String value) throws QueryConstructionException {
        return value;
    }
    
    protected String appendEscapeString(final String valueStr, final Column column, final int comparator) {
        if (valueStr.indexOf(this.getEscapeStr()) != -1) {
            final String escapeStr = this.getEscapeStringForSlash();
            if (escapeStr != null) {
                return valueStr + escapeStr;
            }
        }
        return valueStr;
    }
    
    protected String getEscapeStringForSlash() {
        return " ESCAPE '" + this.getEscapeStr() + "'";
    }
    
    protected String escapeDBWildCard(String valueStr, final int type, final int comparator) throws QueryConstructionException {
        if (!this.isWildCardSupportedForNumbers() && this.isNumeric(type)) {
            throw new QueryConstructionException("Wild card characters are not  allowed in numeric fields:" + valueStr);
        }
        valueStr = valueStr.replace(String.valueOf(this.getWildCardForString()), String.valueOf(this.getEscapeCharacter()) + this.getWildCardForString());
        valueStr = valueStr.replace(String.valueOf(this.getWildCardForChar()), String.valueOf(this.getEscapeCharacter()) + this.getWildCardForChar());
        return valueStr;
    }
    
    protected boolean hasSpecialCharacters(final String valueStr, final int type) throws QueryConstructionException {
        return (type == 12 || type == 1 || type == 2004) && valueStr.indexOf("'") >= 0;
    }
    
    @Override
    public String escapeSpecialCharacters(String valueStr, final int type) {
        if (type == 12 || type == 1 || type == 2004) {
            valueStr = valueStr.replaceAll("'", "''");
        }
        return valueStr;
    }
    
    protected char getEscapeCharacter() {
        return '\\';
    }
    
    protected String getEscapeStr() {
        return String.valueOf(this.getEscapeCharacter());
    }
    
    @Deprecated
    protected String getComparatorString(final Column column, final int comparator) throws QueryConstructionException {
        return this.getComparatorString(column, comparator, true);
    }
    
    protected String getComparatorString(final Column column, final int comparator, final boolean isCaseSensitive) throws QueryConstructionException {
        final int type = column.getType();
        switch (comparator) {
            case 0: {
                if (type == 6) {
                    return " LIKE ";
                }
                return " = ";
            }
            case 1: {
                if (type == 6) {
                    return " NOT LIKE ";
                }
                return " != ";
            }
            case 2:
            case 10:
            case 11:
            case 12: {
                if (type == 16) {
                    return " = ";
                }
                return " LIKE ";
            }
            case 3:
            case 13: {
                if (type == 16) {
                    return " != ";
                }
                return " NOT LIKE ";
            }
            case 4: {
                return " >= ";
            }
            case 5: {
                return " > ";
            }
            case 6: {
                return " <= ";
            }
            case 7: {
                return " < ";
            }
            case 8: {
                return " IN ";
            }
            case 9: {
                return " NOT IN ";
            }
            case 14: {
                return " BETWEEN ";
            }
            case 15: {
                return " NOT BETWEEN ";
            }
            default: {
                throw new QueryConstructionException("Ansi92SQLGenerator : Invalid comparator specified, Use constants specifiedin com.adventnet.ds.query.QueryConstants");
            }
        }
    }
    
    private static void out(final String message, final int logLevel) {
        Ansi92SQLGenerator.OUT.log(Level.FINE, message);
    }
    
    protected String getValueString(String valueStr, final Column column, final int comparator, final boolean hasWildCard, final boolean isCaseSensitive) throws QueryConstructionException {
        final int type = column.getType();
        String vStr = valueStr.trim();
        if (!this.isNumeric(type)) {
            valueStr = valueStr.replace(String.valueOf(this.getEscapeCharacter()), this.getEscapeStr());
            return "'" + this.escapeSpecialCharacters(valueStr, type) + "'";
        }
        if (vStr.startsWith("${") && vStr.endsWith("}")) {
            return vStr;
        }
        if (comparator == 2 || comparator == 3) {
            vStr = vStr.replaceAll("%", "");
            vStr = vStr.replaceAll("_", "");
            this.validateNumericString(vStr, type);
            valueStr = "'" + valueStr + "'";
        }
        else {
            this.validateNumericString(vStr, type);
        }
        return valueStr;
    }
    
    protected String getValueStringForBoolean(final String valueStr) throws QueryConstructionException {
        final String vStr = valueStr.trim();
        if ("true".equalsIgnoreCase(vStr) || "1".equals(vStr) || "t".equalsIgnoreCase(vStr)) {
            return "1";
        }
        if ("false".equalsIgnoreCase(vStr) || "0".equals(vStr) || "f".equalsIgnoreCase(vStr)) {
            return "0";
        }
        throw new QueryConstructionException("Improper value [" + valueStr + "] set for BOOLEAN column", 1);
    }
    
    private void validateNumericString(final String vStr, final int type) throws QueryConstructionException {
        try {
            switch (type) {
                case 4: {
                    Integer.parseInt(vStr);
                    break;
                }
                case -6: {
                    Integer.parseInt(vStr);
                    break;
                }
                case -5: {
                    Long.parseLong(vStr);
                    break;
                }
                case 3: {
                    new BigDecimal(vStr);
                    break;
                }
                case 6: {
                    Float.parseFloat(vStr);
                    break;
                }
                case 8: {
                    Double.parseDouble(vStr);
                    break;
                }
            }
        }
        catch (final NumberFormatException nfe) {
            throw new QueryConstructionException("Improper value [" + vStr + "] set for " + getSQLTypeAsString(type) + " column", QueryConstructionException.getErrorCodeForType(type), nfe);
        }
    }
    
    private String replaceWildCards(String valueStr, final int type) throws QueryConstructionException {
        if (!this.isWildCardSupportedForNumbers() && this.isNumeric(type)) {
            throw new QueryConstructionException("Wild card characters are not  allowed in numeric fields:" + valueStr);
        }
        valueStr = this.replaceAll(valueStr, Ansi92SQLGenerator.stringWildCard, this.getWildCardForString());
        valueStr = this.replaceAll(valueStr, Ansi92SQLGenerator.charWildCard, this.getWildCardForChar());
        valueStr = valueStr.replaceAll("\\\\\\" + Ansi92SQLGenerator.stringWildCard, Ansi92SQLGenerator.stringWildCard);
        valueStr = valueStr.replaceAll("\\\\\\" + Ansi92SQLGenerator.charWildCard, Ansi92SQLGenerator.charWildCard);
        return valueStr;
    }
    
    private String replaceAll(final String value, final String toBeReplaced, final char replacedBy) {
        final StringTokenizer tok = new StringTokenizer(value, toBeReplaced, true);
        final StringBuilder buffer = new StringBuilder("");
        while (tok.hasMoreTokens()) {
            String oneStr = tok.nextToken();
            if (oneStr.endsWith("\\") && this.countTrailingRepeatedChar(oneStr, "\\") % 2 != 0) {
                if (tok.hasMoreTokens()) {
                    oneStr += tok.nextToken();
                }
            }
            else if (oneStr.equals(toBeReplaced)) {
                oneStr = String.valueOf(replacedBy);
            }
            buffer.append(oneStr);
        }
        return buffer.toString();
    }
    
    private int countTrailingRepeatedChar(final String value, final String s) {
        final StringTokenizer stk = new StringTokenizer(value, s, true);
        int count = 0;
        while (stk.hasMoreTokens()) {
            final String tok = stk.nextToken();
            if (tok.equals(s)) {
                ++count;
            }
            else {
                count = 0;
            }
        }
        return count;
    }
    
    protected char getWildCardForChar() {
        return '_';
    }
    
    protected char getWildCardForString() {
        return '%';
    }
    
    protected boolean isWildCardSupportedForNumbers() {
        return true;
    }
    
    protected String getValueForIn(final Object value, final Column column, final int comparator, final boolean isCaseSensitive, final String lhString) throws QueryConstructionException {
        int valLength = 0;
        try {
            valLength = Array.getLength(value);
        }
        catch (final IllegalArgumentException excp) {
            throw new QueryConstructionException("Value for IN/NOT_IN comparator is not an array");
        }
        if (valLength > 0) {
            final int type = column.getType();
            StringBuilder buff = null;
            boolean firstTime = true;
            boolean containsNull = false;
            for (int i = 0; i < valLength; ++i) {
                final Object currVal = Array.get(value, i);
                if (currVal != null && (!(currVal instanceof String) || !((String)currVal).equalsIgnoreCase("null"))) {
                    if (firstTime) {
                        buff = new StringBuilder(75);
                        buff.append("(");
                        firstTime = false;
                    }
                    else {
                        buff.append(",");
                    }
                    if (DataTypeUtil.isUDT(column.getDataType())) {
                        final DataTypeDefinition dtd = DataTypeManager.getDataTypeDefinition(column.getDataType());
                        Object transformedVal = currVal;
                        try {
                            final ColumnDefinition cd = column.getDefinition();
                            if (cd != null) {
                                transformedVal = dtd.getDTTransformer(this.getDBType()).transform(cd.getTableName(), cd.getColumnName(), currVal, cd.getDataType());
                            }
                        }
                        catch (final Exception e) {
                            throw new QueryConstructionException("Exception while transforming data.", e);
                        }
                        buff.append(dtd.getDTSQLGenerator(this.getDBType()).getValue(transformedVal, comparator, isCaseSensitive));
                    }
                    else if (type == 91 || type == 93 || type == 92) {
                        buff.append("'").append(this.getDateString(type, currVal)).append("'");
                    }
                    else if (type == 16) {
                        buff.append(this.getValueStringForBoolean(currVal.toString().trim()));
                    }
                    else {
                        String strValue = this.getValueString(currVal.toString(), column, comparator, false, isCaseSensitive);
                        if (type == 1 || type == 12) {
                            strValue = this.getValue(strValue, isCaseSensitive);
                        }
                        buff.append(strValue);
                    }
                }
                else {
                    containsNull = true;
                }
            }
            if (buff != null) {
                buff.append(")");
            }
            if (containsNull) {
                Ansi92SQLGenerator.OUT.log(Level.WARNING, "NULL value detected in the list of values for IN/NOT_IN clause for column {0} with given values {1}. Therefore, restructuring the query", new Object[] { column, Arrays.asList((Object[])value) });
                if (buff != null) {
                    switch (comparator) {
                        case 9: {
                            buff.append(" AND ").append(lhString).append(" IS NOT NULL ");
                            break;
                        }
                        case 8: {
                            buff.append(" OR ").append(lhString).append(" IS NULL ");
                            break;
                        }
                    }
                }
                else {
                    valLength = 0;
                }
            }
            if (buff != null) {
                return buff.toString();
            }
        }
        if (valLength == 0) {
            switch (comparator) {
                case 9: {
                    return " IS NOT NULL ";
                }
                case 8: {
                    return " IS NULL ";
                }
            }
        }
        return null;
    }
    
    public static String getSQLTypeAsString(final int sqlTypeVal) throws IllegalArgumentException {
        switch (sqlTypeVal) {
            case 1:
            case 12: {
                return "CHAR";
            }
            case -6:
            case 2:
            case 4:
            case 5: {
                return "INTEGER";
            }
            case -5: {
                return "BIGINT";
            }
            case 16: {
                return "BOOLEAN";
            }
            case 3:
            case 6:
            case 7: {
                return "FLOAT";
            }
            case 8: {
                return "DOUBLE";
            }
            case 91: {
                return "DATE";
            }
            case 92: {
                return "TIME";
            }
            case 93: {
                return "TIMESTAMP";
            }
            case 2004: {
                return "BLOB";
            }
            default: {
                throw new IllegalArgumentException("Unknown type received: " + sqlTypeVal);
            }
        }
    }
    
    @Override
    public String getKey() {
        return this.encrytionKey;
    }
    
    @Override
    public void setKey(final String key) {
        this.encrytionKey = key;
    }
    
    @Override
    public void fillUserDataRange(final Map keyVsValues) throws QueryConstructionException {
    }
    
    protected boolean isWildCardSupportedComparator(final int comparator) {
        return comparator == 12 || comparator == 13 || comparator == 10 || comparator == 11 || comparator == 2 || comparator == 3;
    }
    
    protected String getStringForModifyColumn() {
        return " MODIFY ";
    }
    
    protected String getStringForDropColumn() {
        return " DROP COLUMN ";
    }
    
    protected void appendColumnAttributes(final int operationType, final ColumnDefinition colDef, final StringBuilder buffer) throws QueryConstructionException {
        buffer.append(this.getDBDataType(colDef));
        if (colDef.getDefaultValue() != null) {
            buffer.append(" DEFAULT ");
            buffer.append(this.getDefaultValue(colDef.getDataType(), colDef.getDefaultValue()));
        }
        if (!colDef.isNullable()) {
            buffer.append(" NOT NULL");
        }
        this.handleTimeStamp(colDef, buffer);
    }
    
    protected void appendStringForAlterOperation(final AlterOperation ao, final StringBuilder buffer) throws QueryConstructionException {
        final int operationType = ao.getOperationType();
        final Object alterObject = ao.getAlterObject();
        switch (operationType) {
            case 1:
            case 2:
            case 19:
            case 21: {
                final ColumnDefinition colDef = (ColumnDefinition)alterObject;
                buffer.append((operationType == 1 || operationType == 19) ? " ADD " : this.getStringForModifyColumn());
                buffer.append(this.getDBSpecificColumnName(colDef.getColumnName()));
                buffer.append(" ");
                this.appendColumnAttributes(operationType, colDef, buffer);
                break;
            }
            case 3:
            case 20: {
                buffer.append(this.getStringForDropColumn());
                buffer.append(this.getDBSpecificColumnName((String)alterObject));
                break;
            }
            case 4: {
                final UniqueKeyDefinition ukDef = (UniqueKeyDefinition)alterObject;
                buffer.append(this.getSQLForAddUniqueKey(ukDef));
                break;
            }
            case 6: {
                final ForeignKeyDefinition fkDef = (ForeignKeyDefinition)alterObject;
                buffer.append(this.getSQLForAddForeignKey(fkDef));
                break;
            }
            case 5:
            case 7: {
                buffer.append(" DROP CONSTRAINT ");
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? ((String)alterObject) : ao.getActualConstraintName()));
                break;
            }
            case 10: {
                final IndexDefinition idxDef = (IndexDefinition)alterObject;
                buffer.append(this.getSQLForAddIndex(idxDef, ao.getTableName()));
                break;
            }
            case 11: {
                buffer.append(" DROP INDEX ");
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? ((String)alterObject) : ao.getActualConstraintName()));
                break;
            }
            case 14: {
                final ForeignKeyDefinition fk = (ForeignKeyDefinition)alterObject;
                buffer.append(" DROP CONSTRAINT ");
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? fk.getName() : ao.getActualConstraintName()));
                buffer.append(";ALTER TABLE ");
                buffer.append(this.getDBSpecificTableName(ao.getTableName()));
                buffer.append(this.getSQLForAddForeignKey(fk));
                break;
            }
            case 16: {
                final IndexDefinition idx = (IndexDefinition)alterObject;
                buffer.append(" DROP INDEX ");
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? idx.getName() : ao.getActualConstraintName()));
                buffer.append(";ALTER TABLE ");
                buffer.append(this.getDBSpecificTableName(ao.getTableName()));
                buffer.append(this.getSQLForAddIndex(idx, ao.getTableName()));
                break;
            }
            case 15: {
                final UniqueKeyDefinition uk = (UniqueKeyDefinition)alterObject;
                buffer.append(" DROP CONSTRAINT ");
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? uk.getName() : ao.getActualConstraintName()));
                buffer.append(";ALTER TABLE ");
                buffer.append(this.getDBSpecificTableName(ao.getTableName()));
                buffer.append(this.getSQLForAddUniqueKey(uk));
                break;
            }
            case 18: {
                buffer.append(" SET");
                final Properties tableProp = (Properties)alterObject;
                final Iterator keys = ((Hashtable<Object, V>)tableProp).keySet().iterator();
                while (keys.hasNext()) {
                    final String key = keys.next();
                    if (key.equals("description")) {
                        final String newDesc = tableProp.getProperty(key);
                        buffer.append(" DESCRIPTION ");
                        buffer.append(newDesc.isEmpty() ? "NULL" : newDesc);
                    }
                    else if (key.equals("display-name")) {
                        final String newDisplayName = tableProp.getProperty(key);
                        buffer.append(" DISPLAYNAME ");
                        buffer.append(newDisplayName);
                    }
                    else if (key.equals("createtable")) {
                        final boolean createTable = Boolean.valueOf(tableProp.getProperty(key));
                        buffer.append(" CREATETABLE ");
                        buffer.append(createTable);
                    }
                    else if (key.equals("modulename")) {
                        final String moduleName = tableProp.getProperty(key);
                        buffer.append(" MODULENAME ");
                        buffer.append(moduleName);
                    }
                    else if (key.equals("dc-type")) {
                        final String newDcType = tableProp.getProperty(key);
                        buffer.append(" DC-TYPE ");
                        buffer.append(newDcType.isEmpty() ? "NULL" : newDcType);
                    }
                    if (keys.hasNext()) {
                        buffer.append(",");
                    }
                }
                break;
            }
            default: {
                throw new QueryConstructionException("Unknown/UnImplemented Operation Type : " + operationType);
            }
        }
    }
    
    public AlterOperation getAlterOperationForRevert(final AlterOperation ao) throws QueryConstructionException {
        final int operationType = ao.getOperationType();
        final Object alterObject = ao.getAlterObject();
        AlterOperation newAO = null;
        try {
            switch (operationType) {
                case 1: {
                    final ColumnDefinition addColDef = (ColumnDefinition)alterObject;
                    newAO = new AlterOperation(ao.getTableName(), 3, addColDef.getColumnName());
                    return newAO;
                }
                case 3: {
                    final String delColName = (String)alterObject;
                    final ColumnDefinition cdInMetaData = MetaDataUtil.getTableDefinitionByName(ao.getTableName()).getColumnDefinitionByName(delColName);
                    newAO = new AlterOperation(ao.getTableName(), 1, cdInMetaData);
                    return newAO;
                }
                case 2: {
                    final ColumnDefinition modColDef = (ColumnDefinition)alterObject;
                    final ColumnDefinition cdInMetaData = MetaDataUtil.getTableDefinitionByName(ao.getTableName()).getColumnDefinitionByName(modColDef.getColumnName());
                    newAO = new AlterOperation(ao.getTableName(), 2, cdInMetaData);
                    return newAO;
                }
                case 12: {
                    final String[] names = (String[])alterObject;
                    newAO = new AlterOperation(ao.getTableName(), 12, new String[] { names[1], names[0] });
                    return newAO;
                }
                case 4: {
                    final UniqueKeyDefinition ukDef = (UniqueKeyDefinition)alterObject;
                    newAO = new AlterOperation(ao.getTableName(), 5, ukDef.getName());
                    return newAO;
                }
                case 5: {
                    final String delUKName = (String)alterObject;
                    final UniqueKeyDefinition ukInMetaData = MetaDataUtil.getTableDefinitionByName(ao.getTableName()).getUniqueKeyDefinitionByName(delUKName);
                    newAO = new AlterOperation(ao.getTableName(), 4, ukInMetaData);
                    return newAO;
                }
                case 15: {
                    final UniqueKeyDefinition uk = (UniqueKeyDefinition)alterObject;
                    final UniqueKeyDefinition oldUK = MetaDataUtil.getTableDefinitionByName(ao.getTableName()).getUniqueKeyDefinitionByName(uk.getName());
                    newAO = new AlterOperation(ao.getTableName(), 15, oldUK);
                    return newAO;
                }
                case 6: {
                    final ForeignKeyDefinition fkDef = (ForeignKeyDefinition)alterObject;
                    newAO = new AlterOperation(ao.getTableName(), 7, fkDef.getName());
                    return newAO;
                }
                case 7: {
                    final String delFKName = (String)alterObject;
                    final ForeignKeyDefinition fkInMetaData = MetaDataUtil.getTableDefinitionByName(ao.getTableName()).getForeignKeyDefinitionByName(delFKName);
                    newAO = new AlterOperation(ao.getTableName(), 6, fkInMetaData);
                    return newAO;
                }
                case 14: {
                    final ForeignKeyDefinition fk = (ForeignKeyDefinition)alterObject;
                    final ForeignKeyDefinition oldFK = MetaDataUtil.getTableDefinitionByName(ao.getTableName()).getForeignKeyDefinitionByName(fk.getName());
                    newAO = new AlterOperation(ao.getTableName(), 14, oldFK);
                    return newAO;
                }
                case 10: {
                    final IndexDefinition idxDef = (IndexDefinition)alterObject;
                    newAO = new AlterOperation(ao.getTableName(), 11, idxDef.getName());
                    return newAO;
                }
                case 11: {
                    final String delIDXName = (String)alterObject;
                    final IndexDefinition idxInMetaData = MetaDataUtil.getTableDefinitionByName(ao.getTableName()).getIndexDefinitionByName(delIDXName);
                    newAO = new AlterOperation(ao.getTableName(), 10, idxInMetaData);
                    return newAO;
                }
                case 16: {
                    final IndexDefinition idx = (IndexDefinition)alterObject;
                    final IndexDefinition oldIdx = MetaDataUtil.getTableDefinitionByName(ao.getTableName()).getIndexDefinitionByName(idx.getName());
                    newAO = new AlterOperation(ao.getTableName(), 16, oldIdx);
                    return newAO;
                }
                case 9: {
                    newAO = new AlterOperation(ao.getTableName(), 9, MetaDataUtil.getTableDefinitionByName(ao.getTableName()).getPrimaryKey());
                    return newAO;
                }
                case 8: {
                    return ao;
                }
                case 17: {
                    final PrimaryKeyDefinition oldPK = MetaDataUtil.getTableDefinitionByName(ao.getTableName()).getPrimaryKey();
                    newAO = new AlterOperation(ao.getTableName(), 17, new Object[] { ((Object[])alterObject)[0], oldPK });
                    return newAO;
                }
                default: {
                    throw new QueryConstructionException("Unknown/UnImplemented Operation Type : " + operationType);
                }
            }
        }
        catch (final MetaDataException mde) {
            throw new QueryConstructionException("Exception occurred while fetching the MetaData Information for the alterOperation :: " + mde);
        }
    }
    
    protected void appendSeperatorForAlterOperation(final StringBuilder buffer, final AlterOperation prevOperation, final AlterOperation currentOperation) throws QueryConstructionException {
        if (buffer.length() == 0) {
            final String tableName = currentOperation.getTableName();
            buffer.append("ALTER TABLE ");
            buffer.append(this.getDBSpecificTableName(tableName));
            buffer.append(" ");
        }
        else {
            buffer.append(", ");
        }
    }
    
    @Override
    public String getSQLForDropIndex(final String tableName, final String indexName) throws QueryConstructionException {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("DROP INDEX ");
        buffer.append(this.getConstraintName(indexName));
        buffer.append(" ON ");
        buffer.append(this.getDBSpecificTableName(tableName));
        return buffer.toString();
    }
    
    protected String getSQLForAddForeignKey(final ForeignKeyDefinition fkDef) throws QueryConstructionException {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(" ADD CONSTRAINT ");
        buffer.append(this.getConstraintName(fkDef.getName()));
        buffer.append(" FOREIGN KEY ");
        this.setColumnNamesFromList(fkDef.getFkColumns(), buffer, null);
        buffer.append(" REFERENCES ");
        buffer.append(this.getDBSpecificTableName(fkDef.getMasterTableName()));
        this.setColumnNamesFromList(fkDef.getFkRefColumns(), buffer, null);
        buffer.append(" ");
        buffer.append(this.getStringConstraint(fkDef.getConstraints()));
        return buffer.toString();
    }
    
    protected String getSQLForAddIndex(final IndexDefinition idxDef, final String tableName) throws QueryConstructionException {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(" CREATE INDEX ");
        buffer.append(this.getConstraintName(idxDef.getName()));
        buffer.append(" ON ");
        buffer.append(this.getDBSpecificTableName(tableName));
        buffer.append(" ");
        this.setColumnNamesFromList(idxDef.getColumns(), buffer, tableName);
        return buffer.toString();
    }
    
    protected String getSQLForAddUniqueKey(final UniqueKeyDefinition ukDef) throws QueryConstructionException {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(" ADD CONSTRAINT ");
        buffer.append(this.getConstraintName(ukDef.getName()));
        buffer.append(" UNIQUE ");
        this.setColumnNamesFromList(ukDef.getColumns(), buffer, null);
        return buffer.toString();
    }
    
    @Override
    public String getIndexName(final String name) {
        return name + "_IDX";
    }
    
    public IndexDefinition getIndexDefForFK(final ForeignKeyDefinition fkDef) {
        final IndexDefinition indxDef = new IndexDefinition();
        indxDef.setName(this.getIndexName(fkDef.getName()));
        for (final ForeignKeyColumnDefinition fkcd : fkDef.getForeignKeyColumns()) {
            final IndexColumnDefinition icd = new IndexColumnDefinition(fkcd.getLocalColumnDefinition());
            indxDef.addIndexColumnDefinition(icd);
        }
        return indxDef;
    }
    
    protected boolean isCurrentlyEncrypted(final String tableName, final String columnName) throws MetaDataException {
        return MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(columnName).isEncryptedColumn();
    }
    
    @Override
    public List<String> getUpdateSQLForModifyColumnDataEncryption(final AlterTableQuery alterTableQuery) throws QueryConstructionException {
        throw new QueryConstructionException("No default implementation for MODIFY_COLUMN data encryption");
    }
    
    @Override
    public String getSQLForCreateArchiveTable(final CreateTableLike cloneTdleDetails, final String createTableOptions, final boolean isPush) throws QueryConstructionException {
        final ArchiveTable tableToBeCloned = cloneTdleDetails.getArchiveTable();
        final SelectQuery selectQry = cloneTdleDetails.getSelectQuery();
        final StringBuilder buff = new StringBuilder();
        buff.append("CREATE TABLE ");
        final String tableNameToBeCreated = isPush ? tableToBeCloned.getArchiveTableName() : tableToBeCloned.getInvisibleTableName();
        buff.append(this.getDBSpecificTableName(tableNameToBeCreated));
        this.appendCreateTableOptions(buff, createTableOptions);
        buff.append(" AS ");
        this.appendSelectQuery(buff, tableToBeCloned, selectQry, isPush);
        return buff.toString();
    }
    
    protected void appendCreateTableOptions(final StringBuilder buff, final String createTableOptions) {
        Ansi92SQLGenerator.OUT.fine("There is no default implementaion for append table options");
    }
    
    protected void appendSelectQuery(final StringBuilder buff, final ArchiveTable arcTable, final SelectQuery query, final boolean isPush) throws QueryConstructionException {
        buff.append(" ( ");
        if (query == null) {
            buff.append(" LIKE ");
            buff.append(this.getDBSpecificTableName(isPush ? arcTable.getTableName() : arcTable.getArchiveTableName()));
        }
        else {
            final String selectSQL = this.getSQLForSelect(query);
            buff.append(selectSQL);
        }
        buff.append(" ) ");
    }
    
    @Override
    public void setFunctionTemplates(final Properties functionTemplates) {
        this.functionTemplates = functionTemplates;
    }
    
    @Override
    public String getBulkSql(final BulkLoad bulk, final BulkInsertObject bio) throws MetaDataException, SQLException, QueryConstructionException {
        Ansi92SQLGenerator.OUT.log(Level.FINE, "getBulkSql is not supported !!!");
        return "";
    }
    
    @Override
    public String formBulkUpdateSql(final BulkLoad bulk) throws MetaDataException {
        Ansi92SQLGenerator.OUT.log(Level.FINE, "formBulkUpdateSql is not supported !!!");
        return "";
    }
    
    @Override
    public String createTempTableSQL(final String tableName) {
        Ansi92SQLGenerator.OUT.log(Level.FINE, "createTempTableSQL is not supported !!!");
        return "";
    }
    
    @Override
    public String insertSQLForTemp(final String tableName) {
        Ansi92SQLGenerator.OUT.log(Level.FINE, "insertSQLForTemp is not supported !!!");
        return "";
    }
    
    protected void appendDeleteJoin(final StringBuilder buff, final Join join) throws QueryConstructionException {
        final List<Join> joins = Arrays.asList(join);
        buff.append(" FROM ");
        buff.append(this.formJoinString(joins, Collections.emptyList())).append(" ");
    }
    
    protected void appendDeleteJoinAndCriteria(final Join join, final Criteria criteria, final StringBuilder deleteBuffer) throws QueryConstructionException {
        if (join != null) {
            this.appendDeleteJoin(deleteBuffer, join);
        }
        final String whereClause = this.formWhereClause(criteria, false, null);
        if (whereClause != null) {
            deleteBuffer.append(" WHERE ");
            deleteBuffer.append(whereClause);
        }
    }
    
    @Override
    public String formCountSQL(String constructedSQL, final boolean groupbyused) throws Exception {
        final String countSql = constructedSQL.toUpperCase(Locale.ENGLISH);
        final int firstIndex = countSql.indexOf(" FROM ");
        final int lastIndex = countSql.lastIndexOf(" FROM ");
        final int orderByIndex = constructedSQL.lastIndexOf("ORDER BY");
        if (orderByIndex != -1) {
            constructedSQL = constructedSQL.substring(0, orderByIndex);
        }
        if (groupbyused) {
            return "select count(*) from (" + constructedSQL + ") sel";
        }
        if (firstIndex != lastIndex) {
            return "select count(*) from (" + constructedSQL + ") sel";
        }
        String toReturn = "select count(*) " + constructedSQL.substring(lastIndex);
        final int groupByIndex = countSql.lastIndexOf("GROUP BY");
        if (groupByIndex != -1) {
            final String countSqlWithoutGroupBy = constructedSQL.substring(lastIndex, groupByIndex);
            final int till = (orderByIndex != -1) ? orderByIndex : constructedSQL.trim().length();
            final String groupByColumns = constructedSQL.substring(groupByIndex + 8, till);
            toReturn = this.formCountSqlForGroupBy(constructedSQL, countSqlWithoutGroupBy, groupByColumns);
        }
        return toReturn;
    }
    
    protected String formCountSqlForGroupBy(final String constructedSQL, final String countSqlWithoutGroupBy, final String groupByColumns) {
        if (groupByColumns.indexOf(",") != -1) {
            return "select count(*) from (" + constructedSQL + ") sel";
        }
        return "select COUNT(DISTINCT " + groupByColumns + " ) " + countSqlWithoutGroupBy;
    }
    
    @Override
    public String getSchemaQuery() {
        return null;
    }
    
    @Override
    public void initDCSQLGenerators() {
        this.dcTypeVsSQLGenerator = new HashMap<String, DCSQLGenerator>();
        final List<String> dcTypes = DCManager.getDCTypes();
        for (int i = 0; i < dcTypes.size(); ++i) {
            final Properties p = DCManager.getProps(dcTypes.get(i) + "." + this.getDBType());
            if (p != null && p.getProperty("dcsqlgenerator") != null) {
                DCSQLGenerator dcSqlGenerator = null;
                try {
                    dcSqlGenerator = (DCSQLGenerator)Thread.currentThread().getContextClassLoader().loadClass(p.getProperty("dcsqlgenerator")).newInstance();
                }
                catch (final ClassNotFoundException e) {
                    throw new IllegalArgumentException(e.getMessage(), e);
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                    Ansi92SQLGenerator.OUT.log(Level.SEVERE, "Error while trying to instantiate DCSQLGenerator for dc type :: {0} with exception {1}", new Object[] { dcTypes.get(i), ex });
                }
                dcSqlGenerator.initSQLGenerator(this);
                this.dcTypeVsSQLGenerator.put(dcTypes.get(i), dcSqlGenerator);
            }
        }
    }
    
    @Override
    public DCSQLGenerator getDCSQLGeneratorForTable(final String tableName) {
        return this.getDCSQLGenerator(this.getDCTypeForTable(tableName));
    }
    
    protected String getDCTypeForTable(final String tableName) {
        try {
            return MetaDataUtil.getTableDefinitionByName(tableName).getDynamicColumnType();
        }
        catch (final MetaDataException e) {
            throw new IllegalArgumentException("Problem while fetching dynamic column type for given table :: " + tableName, e);
        }
    }
    
    @Override
    public DCSQLGenerator getDCSQLGenerator(final String dcType) {
        return this.dcTypeVsSQLGenerator.get(dcType);
    }
    
    @Override
    public String getSQLForBatchInsert(final String tableName) throws MetaDataException, QueryConstructionException {
        Ansi92SQLGenerator.OUT.entering(Ansi92SQLGenerator.class.getName(), "getSQLForPreparedInsertStatement", tableName);
        String insertSQL;
        try {
            final LinkedHashMap insertColumnsMap = new LinkedHashMap();
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            if (td == null) {
                Ansi92SQLGenerator.OUT.log(Level.FINER, "Unknown table {0} specified for constructing SQL", tableName);
                throw new MetaDataException("Unknown table " + tableName);
            }
            final List columnNames = td.getColumnNames();
            final List encColNames = td.getEncryptedColumnNames();
            for (int colCount = columnNames.size(), i = 0; i < colCount; ++i) {
                final String columnName = columnNames.get(i);
                final Column column = Column.getColumn(tableName, columnName);
                final ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
                column.setDefinition(cd);
                insertColumnsMap.put(column, QueryConstants.PREPARED_STMT_CONST);
            }
            insertSQL = this.getSQLForInsert(tableName, insertColumnsMap);
        }
        catch (final MetaDataException mde) {
            throw new MetaDataException("Exception occured while getting the table definition for the table " + tableName, mde);
        }
        Ansi92SQLGenerator.OUT.exiting(Ansi92SQLGenerator.class.getName(), "getSQLForPreparedInsertStatement", insertSQL);
        return insertSQL;
    }
    
    @Override
    public String getSQLForBatchUpdate(final String tableName, final int[] changedIndexes) throws MetaDataException, QueryConstructionException {
        HashMap values = null;
        Criteria cri = null;
        try {
            Ansi92SQLGenerator.OUT.entering(Ansi92SQLGenerator.class.getName(), "getSQLForPreparedUpdateStatement", tableName);
            final List columns = MetaDataUtil.getTableDefinitionByName(tableName).getColumnList();
            if (changedIndexes == null) {
                Ansi92SQLGenerator.OUT.exiting(Ansi92SQLGenerator.class.getName(), "constructUpdateQuery", null);
                return null;
            }
            final int size = changedIndexes.length;
            values = new LinkedHashMap();
            final Map<Column, Object> dyVals = new LinkedHashMap<Column, Object>();
            for (final int index : changedIndexes) {
                final String columnName = columns.get(index - 1).getColumnName();
                final Column col = Column.getColumn(tableName, columnName);
                if (MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(col.getColumnName()).isDynamic()) {
                    dyVals.put(col, QueryConstants.PREPARED_STMT_CONST);
                }
                else {
                    values.put(col, QueryConstants.PREPARED_STMT_CONST);
                }
            }
            if (!dyVals.isEmpty()) {
                values.putAll(this.getDCSQLGeneratorForTable(tableName).getModifiedValueForUpdate(tableName, dyVals));
            }
            cri = getCriteriaForPreparedStatement(tableName);
            Ansi92SQLGenerator.OUT.log(Level.FINER, "For  UpdateSQL tablename {0} values {1}, criteria {2}", new Object[] { tableName, values, cri });
            final List tabList = new ArrayList();
            tabList.add(new Table(tableName));
            QueryUtil.setTypeForUpdateColumns(tabList, values, cri);
            Ansi92SQLGenerator.OUT.exiting(Ansi92SQLGenerator.class.getName(), "getSQLForPreparedUpdateStatement");
        }
        catch (final DataAccessException daexp) {
            throw new MetaDataException("Exception occured while getting the table definition for the table " + tableName, daexp);
        }
        return this.getSQLForUpdate(tableName, values, cri);
    }
    
    @Override
    public String getSQLForBatchUpdate(final UpdateQuery query) throws MetaDataException, QueryConstructionException {
        final String tableName = query.getTableName();
        final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
        final Map values = query.getUpdateColumns();
        final Map valuesForPS = new LinkedHashMap();
        for (final Column column : values.keySet()) {
            if (values.get(column) instanceof Column) {
                valuesForPS.put(column, values.get(column));
            }
            else {
                valuesForPS.put(column, QueryConstants.PREPARED_STMT_CONST);
            }
            if (td.getPhysicalColumns().contains(column.getColumnName())) {
                column.setType(QueryUtil.getJavaSQLType(this.getDCSQLGeneratorForTable(tableName).getDCDataType(column.getColumnName())));
            }
            else {
                final String dataType = td.getColumnDefinitionByName(column.getColumnName()).getDataType();
                column.setType(QueryUtil.getJavaSQLType(dataType));
            }
        }
        final Criteria criteria = query.getCriteria();
        final List tableList = query.getTableList();
        final List joins = query.getJoins();
        return this.getSQLForUpdate(tableList, valuesForPS, criteria, joins);
    }
    
    protected static Criteria getCriteriaForPreparedStatement(final String tableName) throws DataAccessException {
        Criteria criteria = null;
        try {
            final List<String> columnList = MetaDataUtil.getTableDefinitionByName(tableName).getPrimaryKey().getColumnList();
            for (final String col : columnList) {
                final Column column = new Column(tableName, col);
                final Object value = QueryConstants.PREPARED_STMT_CONST;
                if (criteria == null) {
                    criteria = new Criteria(column, value, 0);
                }
                else {
                    criteria = criteria.and(column, value, 0);
                }
            }
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Invalid tableName specified " + tableName, mde);
        }
        return criteria;
    }
    
    @Override
    public String getSQLWithHiddenEncryptionKey(final String query) {
        return query;
    }
    
    @Override
    public String getSQLForCount(final String tableName, final Criteria criteria) throws QueryConstructionException {
        final List<Table> tableList = new ArrayList<Table>();
        tableList.add(Table.getTable(tableName));
        QueryUtil.setTypeForCriteria(criteria, tableList);
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(*) FROM ");
        sb.append(this.getDBSpecificTableName(tableName));
        if (criteria != null) {
            sb.append(" WHERE ");
            sb.append(this.formWhereClause(criteria));
        }
        return sb.toString();
    }
    
    void addReservedKeyWords(final List<String> keyWords) {
        this.reservedKeyWords.addAll(keyWords);
    }
    
    protected boolean isKeyWord(final String identifier) {
        if (identifier != null && (identifier.trim().contains(" ") || identifier.contains("."))) {
            return true;
        }
        for (final String reservedkeywords : this.reservedKeyWords) {
            if (reservedkeywords.equalsIgnoreCase(identifier)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String processDeleteSQLString(final String deleteSqlStr) throws QueryConstructionException {
        if (!PersistenceInitializer.onSAS()) {
            return deleteSqlStr;
        }
        try {
            final DeleteSqlObject ds = new DeleteSqlObject(deleteSqlStr);
            final Map sasMap = this.postamble(ds.getTableNames());
            if (sasMap == null) {
                throw new QueryConstructionException("Problem while doing the SAS scoping. sasMap is null");
            }
            ds.setSasMap(sasMap);
            ds.doScoping();
            return ds.getSQL(PersistenceInitializer.getConfigurationValue("DBName"));
        }
        catch (final ParseException ex) {
            throw new QueryConstructionException("Problem while parsing the sql - " + deleteSqlStr, (Throwable)ex);
        }
        catch (final ConvertException ex2) {
            throw new QueryConstructionException("Problem while generating the scoped sql - " + deleteSqlStr, (Throwable)ex2);
        }
        catch (final DataAccessException ex3) {
            throw new QueryConstructionException("DataAcess Exception", ex3);
        }
    }
    
    @Override
    public Map postamble(final List<String> tableNames) throws QueryConstructionException {
        throw new UnsupportedOperationException("Not supported for non-SAS platforms");
    }
    
    @Override
    public String getDBType() {
        return this.dbType;
    }
    
    static {
        OUT = Logger.getLogger(Ansi92SQLGenerator.class.getName());
        Ansi92SQLGenerator.stringWildCard = "*";
        Ansi92SQLGenerator.charWildCard = "?";
        NUMBER_PATTERN = Pattern.compile("((-|\\+)?[0-9]+(\\.[0-9]+)?)+");
        DATE_PATTERN = Pattern.compile("[^a-zA-Z]+");
        Ansi92SQLGenerator.isAutoQuoteEnabled = false;
    }
    
    public enum Clause
    {
        SELECT, 
        JOIN, 
        WHERE, 
        GROUPBY, 
        ORDERBY;
    }
}
