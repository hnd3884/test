package com.adventnet.db.adapter.mysql;

import com.adventnet.ds.query.IndexHintClause;
import java.util.Arrays;
import com.adventnet.db.adapter.BulkInsertObject;
import com.adventnet.ds.query.BulkLoad;
import com.adventnet.ds.query.ArchiveTable;
import com.adventnet.ds.query.CreateTableLike;
import com.adventnet.ds.query.AlterTableQuery;
import com.zoho.conf.AppResources;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.ds.query.SortColumn;
import java.util.Locale;
import com.adventnet.ds.query.LocaleColumn;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.Operation;
import com.adventnet.ds.query.Function;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.adventnet.db.persistence.metadata.IndexColumnDefinition;
import java.util.StringJoiner;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.SelectQuery;
import java.util.Map;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.Collections;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Table;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.ds.query.DeleteQuery;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.db.adapter.Ansi92SQLGenerator;

public class MysqlSQLGenerator extends Ansi92SQLGenerator
{
    private static final Logger OUT;
    static String collationName;
    protected String collationString;
    protected String collationStringForEncryptedCols;
    protected boolean isDBCaseSensitive;
    protected String defaultCharacterSet;
    protected boolean hasCollation;
    
    public void setSupportsCollation(final boolean hasCollation) {
        this.hasCollation = hasCollation;
        MysqlSQLGenerator.OUT.log(Level.INFO, "hasCollation :: [{0}]", this.hasCollation);
    }
    
    public void setCollation(final String collationStr) {
        this.collationString = " COLLATE " + collationStr;
        MysqlSQLGenerator.OUT.log(Level.INFO, "collationString :: [{0}]", this.collationString);
    }
    
    public void setCollationForEncryptedCols(final String collationStr) {
        this.collationStringForEncryptedCols = " COLLATE " + collationStr;
        MysqlSQLGenerator.OUT.log(Level.INFO, "collationStringForEncryptedCols :: [{0}]", this.collationString);
    }
    
    public void setIsDBCaseSensitive(final boolean isDBCaseSensitive) {
        this.isDBCaseSensitive = isDBCaseSensitive;
        MysqlSQLGenerator.OUT.log(Level.INFO, "isDBCaseSensitive :: [{0}]", this.isDBCaseSensitive);
    }
    
    public void setDefaultCharacterSet(final String defaultCharacterSet) {
        this.defaultCharacterSet = defaultCharacterSet;
        MysqlSQLGenerator.OUT.log(Level.INFO, "defaultCharacterSet :: [{0}]", defaultCharacterSet);
    }
    
    public MysqlSQLGenerator() {
        this.collationString = "collate utf8_general_ci";
        this.collationStringForEncryptedCols = "collate utf8_general_ci";
        this.isDBCaseSensitive = true;
        this.defaultCharacterSet = null;
        this.hasCollation = true;
        this.dbType = "mysql";
    }
    
    @Override
    public String getSQLForDelete(final DeleteQuery query) throws QueryConstructionException {
        final String tableName = query.getTableName();
        final Criteria criteria = query.getCriteria();
        final List<Join> joins = query.getJoins();
        final int numOfRows = query.getLimit();
        final StringBuilder buff = new StringBuilder();
        buff.append("DELETE FROM ");
        buff.append(this.getDBSpecificTableName(tableName));
        if (joins.isEmpty()) {
            final String deleteSQL = this.formCriteriaForDelete(criteria);
            if (deleteSQL != null) {
                buff.append(deleteSQL);
            }
            if (numOfRows > 0) {
                final List sortcolumns = query.getSortColumns();
                final List selectCols = new ArrayList();
                final List iscaseSenCols = new ArrayList();
                final String orderbySQL = this.getOrderByClause(sortcolumns, selectCols, null, iscaseSenCols);
                if (orderbySQL != null) {
                    buff.append(orderbySQL);
                }
                buff.append(" LIMIT ");
                buff.append(numOfRows);
            }
            return buff.toString();
        }
        if (numOfRows < 0) {
            buff.append(" USING ");
            buff.append(this.formJoinString(joins, Collections.emptyList())).append(" ");
            if (criteria != null) {
                final String whereClause = this.formCriteriaForDelete(criteria);
                if (whereClause != null) {
                    buff.append(whereClause);
                }
            }
            return buff.toString();
        }
        throw new QueryConstructionException("ORDER BY and LIMIT cannot be used with JOIN");
    }
    
    @Override
    protected String getColumnNameForUpdateSetClause(final Column column) {
        final String tableName = column.getTableAlias();
        final String columnName = column.getColumnName();
        if (tableName != null) {
            return this.getDBSpecificTableName(tableName) + "." + this.getDBSpecificColumnName(columnName);
        }
        return this.getDBSpecificColumnName(columnName);
    }
    
    @Override
    public String getSQLForUpdate(final List tableList, final Map newValues, final Criteria criteria, final List joins) throws QueryConstructionException {
        final Table baseTable = tableList.get(0);
        final String tableName = baseTable.getTableName();
        return this.getSQLForUpdate(tableName, newValues, criteria, joins);
    }
    
    @Override
    public String getSQLForUpdate(final String tableName, final Map newValues, final Criteria criteria, final List joins) throws QueryConstructionException {
        if (tableName == null) {
            throw new QueryConstructionException("Table name can not be null");
        }
        if (joins == null || joins.isEmpty()) {
            return this.getSQLForUpdate(tableName, newValues, criteria);
        }
        final StringBuilder updateBuffer = new StringBuilder(100);
        final String setSQL = this.getUpdateColumnsSQL(tableName, newValues);
        final String whereClause = this.formWhereClause(criteria, false, null);
        updateBuffer.append("UPDATE ");
        updateBuffer.append(this.formJoinString(joins, null));
        updateBuffer.append(" SET ");
        updateBuffer.append(setSQL);
        if (whereClause != null) {
            updateBuffer.append(" WHERE (");
            updateBuffer.append(whereClause);
            updateBuffer.append(")");
        }
        final String sql = updateBuffer.toString();
        MysqlSQLGenerator.OUT.log(Level.FINE, "MysqlSQLGenerator.getSQLForUpdateQuery(): SQL formed is : {0}", sql);
        return sql;
    }
    
    @Override
    protected String getJoinString(final Join join) {
        if (join.isOptimized()) {
            return " STRAIGHT_JOIN ";
        }
        return super.getJoinString(join);
    }
    
    @Override
    protected String formSelectClause(final List columnsList, final List caseSensitiveColumns, final SelectQuery sq) throws QueryConstructionException {
        if (columnsList == null || columnsList.isEmpty()) {
            throw new QueryConstructionException("Select columns not given");
        }
        final StringBuilder selectClause = new StringBuilder(250);
        final int columnsLength = columnsList.size();
        final String columnString = null;
        for (int i = 0; i < columnsLength; ++i) {
            final int checkLength = i;
            final Column column = columnsList.get(i);
            final StringBuilder colBuffer = new StringBuilder(40);
            final boolean caseSensitive = false;
            this.processColumn(column, colBuffer, caseSensitiveColumns.contains(column), (sq == null) ? null : sq.getDerivedTables(), Clause.SELECT);
            selectClause.append(colBuffer.toString());
            this.getAliasedColumn(sq, column, selectClause);
            if (checkLength + 1 < columnsLength) {
                selectClause.append(",");
            }
        }
        return selectClause.toString();
    }
    
    @Override
    public String getSQLForLock(final List tableList) throws QueryConstructionException {
        String lockSQL = "LOCK TABLES";
        for (int tabSize = tableList.size(), i = 0; i < tabSize; ++i) {
            if (i != 0) {
                lockSQL += ",";
            }
            lockSQL = lockSQL + " " + this.getDBSpecificTableName(tableList.get(i)) + " READ";
        }
        return lockSQL;
    }
    
    @Override
    public String getSQLForLock(final String tableName) throws QueryConstructionException {
        return "LOCK TABLE " + this.getDBSpecificTableName(tableName) + " READ";
    }
    
    @Override
    protected void appendCreateIndexForCreateTable(final StringBuilder buffer, final TableDefinition tabDefn, final String createTableOptions) throws QueryConstructionException {
        final List fkList = tabDefn.getForeignKeyList();
        final List idxDefs = tabDefn.getIndexes();
        if (fkList == null && idxDefs == null) {
            buffer.append((createTableOptions == null) ? " ENGINE = InnoDB" : createTableOptions);
            return;
        }
        buffer.deleteCharAt(buffer.length() - 1);
        if (fkList != null) {
            for (int size = fkList.size(), i = 0; i < size; ++i) {
                final ForeignKeyDefinition fkDefn = fkList.get(i);
                final String fkIdxStr = this.getForeignKeyIndex(fkDefn, i);
                buffer.append(",");
                buffer.append(fkIdxStr);
            }
        }
        if (idxDefs != null) {
            for (int size = idxDefs.size(), i = 0; i < size; ++i) {
                final String idxStr = this.getStringForIndexDefinition(idxDefs.get(i), tabDefn);
                buffer.append(",").append(idxStr);
            }
        }
        buffer.append(")");
        buffer.append((createTableOptions == null) ? " ENGINE = InnoDB" : createTableOptions);
    }
    
    private String getStringForIndexDefinition(final IndexDefinition idxDef, final TableDefinition td) {
        final StringJoiner indexString = new StringJoiner(" ");
        final StringJoiner columnString = new StringJoiner(",", "(", ")");
        String indexProps = "";
        indexString.add("INDEX").add(this.getConstraintName(idxDef.getName()));
        for (final IndexColumnDefinition icd : idxDef.getColumnDefnitions()) {
            final String dataType = td.getColumnDefinitionByName(icd.getColumnName()).getDataType();
            final String columnName = icd.getColumnName();
            indexProps = this.getIndexPropString(icd.isAscending());
            final DataTypeDefinition udt = (dataType != null) ? DataTypeManager.getDataTypeDefinition(dataType) : null;
            if (udt != null && udt.getMeta() != null) {
                final String indexStr = udt.getDTSQLGenerator(this.getDBType()).getSQLForIndexColumn(icd);
                if (indexStr != null) {
                    columnString.add(indexStr);
                }
                else {
                    columnString.add(String.join("", this.getDBSpecificColumnName(columnName), indexProps));
                }
            }
            else {
                String size = "";
                if (icd.getSize() != -1) {
                    size = String.join("", "(", String.valueOf(idxDef.getSize(columnName)), ")");
                }
                columnString.add(String.join("", this.getDBSpecificColumnName(columnName), size, indexProps));
            }
        }
        indexString.add(columnString.toString());
        return indexString.toString();
    }
    
    @Override
    protected void addUniqueKeyName(final StringBuilder buffer, final String ukName) throws QueryConstructionException {
        buffer.append(" " + ukName);
    }
    
    @Override
    protected String getDefaultValue(String dataType, final Object defVal) {
        if (defVal == null) {
            return null;
        }
        if (DataTypeUtil.isEDT(dataType)) {
            dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
        }
        String retVal = "";
        if (dataType.equals("INTEGER") || dataType.equals("BIGINT") || dataType.equals("CHAR") || dataType.equals("SCHAR") || dataType.equals("NCHAR") || dataType.equals("DATE") || dataType.equals("DATETIME") || dataType.equals("TIMESTAMP") || dataType.equals("TIME") || dataType.equals("FLOAT") || dataType.equals("DOUBLE") || dataType.equals("DECIMAL") || dataType.equals("TINYINT")) {
            retVal = "'" + this.escapeSpecialCharacters(String.valueOf(defVal), 12) + "'";
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
        if (dataType.equals("BOOLEAN")) {
            return "TINYINT(1)";
        }
        if (dataType.equals("SCHAR")) {
            dataTypeStr = "BLOB";
        }
        else if (dataType.equals("CHAR") || dataType.equals("NCHAR")) {
            if (maxLength == 0) {
                maxLength = 50;
            }
            if (maxLength > 255 || maxLength == -1) {
                if (keyOrUnique) {
                    throw new QueryConstructionException("Key or Unique Column size can not be -1 or more than 255 characters");
                }
                if (maxLength > 65535) {
                    dataTypeStr = "LONGTEXT";
                }
                else {
                    dataTypeStr = "TEXT";
                }
            }
            else {
                dataTypeStr = "VARCHAR(" + maxLength + ")";
            }
        }
        else if (dataType.equals("INTEGER")) {
            if (maxLength <= 0 || maxLength > 10) {
                MysqlSQLGenerator.OUT.log(Level.WARNING, "maxLength of a INTEGER column [" + colDef.getTableName() + "." + colDef.getColumnName() + "] cannot be [" + maxLength + "], hence it is considered as [10].");
                maxLength = 10;
            }
            dataTypeStr = "INT(" + maxLength + ")";
        }
        else if (dataType.equals("BIGINT")) {
            if (maxLength <= 0 || maxLength > 19) {
                MysqlSQLGenerator.OUT.log(Level.WARNING, "maxLength of a BIGINT column [" + colDef.getTableName() + "." + colDef.getColumnName() + "] cannot be [" + maxLength + "], hence it is considered as [19].");
                maxLength = 19;
            }
            dataTypeStr = "BIGINT(" + maxLength + ")";
        }
        else if (dataType.equals("FLOAT") || dataType.equals("DOUBLE")) {
            dataTypeStr = dataType;
        }
        else if (dataType.equals("DECIMAL")) {
            dataTypeStr = dataType + "(" + ((maxLength == 0) ? 16 : maxLength) + ", " + ((precision == 0) ? 2 : precision) + ")";
        }
        else if (dataType.equals("TINYINT")) {
            if (maxLength == 0) {
                maxLength = 4;
            }
            dataTypeStr = "TINYINT(" + maxLength + ")";
        }
        else if (dataType.equals("BLOB") || dataType.equals("SBLOB")) {
            if (maxLength > 65535) {
                dataTypeStr = "LONGBLOB";
            }
            else {
                dataTypeStr = "BLOB";
            }
        }
        else if (DataTypeUtil.isUDT(dataType)) {
            if (DataTypeManager.getDataTypeDefinition(dataType).getDTSQLGenerator(this.getDBType()) != null) {
                dataTypeStr = DataTypeManager.getDataTypeDefinition(dataType).getDTSQLGenerator(this.getDBType()).getDBDataType(colDef);
            }
            else {
                dataTypeStr = super.getDBDataType(colDef);
            }
        }
        else {
            dataTypeStr = super.getDBDataType(colDef);
        }
        return dataTypeStr;
    }
    
    @Override
    protected void appendStringForAlterOperation(final AlterOperation ao, final StringBuilder buffer) throws QueryConstructionException {
        final String tableName = ao.getTableName();
        final int operationType = ao.getOperationType();
        final Object alterObject = ao.getAlterObject();
        switch (operationType) {
            case 12: {
                buffer.append(" CHANGE ");
                final String renColName_Old = ((String[])alterObject)[0];
                final String renColName_New = ((String[])alterObject)[1];
                buffer.append(this.getDBSpecificColumnName(renColName_Old));
                buffer.append(" ");
                buffer.append(this.getDBSpecificColumnName(renColName_New));
                buffer.append(" ");
                ColumnDefinition renColDef = null;
                try {
                    renColDef = MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(renColName_Old);
                }
                catch (final MetaDataException e) {
                    throw new QueryConstructionException("Cannot able to find suitable table definition for table [" + tableName + "]");
                }
                this.appendColumnAttributes(operationType, renColDef, buffer);
                break;
            }
            case 5: {
                buffer.append(" DROP INDEX ");
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? ((String)alterObject) : ao.getActualConstraintName()));
                break;
            }
            case 6: {
                final ForeignKeyDefinition fkDef = (ForeignKeyDefinition)alterObject;
                buffer.append("ADD INDEX ");
                buffer.append(this.getConstraintName(fkDef.getName() + "_IDX"));
                this.setColumnNamesFromList(fkDef.getFkColumns(), buffer, null);
                buffer.append(", ");
                buffer.append(super.getSQLForAddForeignKey(fkDef));
                break;
            }
            case 7: {
                final String delFKName = (String)alterObject;
                buffer.append(" DROP FOREIGN KEY ");
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? delFKName : ao.getActualConstraintName()));
                buffer.append(", DROP INDEX ");
                buffer.append(this.getConstraintName(((ao.getActualConstraintName() == null) ? delFKName : ao.getActualConstraintName()) + "_IDX"));
                break;
            }
            case 9: {
                final PrimaryKeyDefinition pkDef = (PrimaryKeyDefinition)alterObject;
                buffer.append(" ADD PRIMARY KEY ");
                buffer.append(this.getConstraintName(pkDef.getName()));
                this.setColumnNamesFromList(pkDef.getColumnList(), buffer, null);
                break;
            }
            case 8: {
                buffer.append(" DROP PRIMARY KEY");
                break;
            }
            case 13: {
                buffer.append(" RENAME ");
                buffer.append(this.getDBSpecificTableName((String)alterObject));
                break;
            }
            case 10: {
                final IndexDefinition idxDef = (IndexDefinition)alterObject;
                buffer.append("ADD INDEX ");
                buffer.append(this.getConstraintName(idxDef.getName()));
                buffer.append(" ");
                this.setColumnNamesFromList(idxDef.getColumns(), buffer, tableName);
                break;
            }
            case 14: {
                final ForeignKeyDefinition fk = (ForeignKeyDefinition)alterObject;
                buffer.append(" DROP FOREIGN KEY ");
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? fk.getName() : ao.getActualConstraintName()));
                buffer.append(", DROP INDEX ");
                buffer.append(this.getConstraintName(((ao.getActualConstraintName() == null) ? fk.getName() : ao.getActualConstraintName()) + "_IDX"));
                buffer.append("; ALTER TABLE ");
                buffer.append(this.getDBSpecificTableName(tableName));
                buffer.append(" ADD INDEX ");
                buffer.append(this.getConstraintName(fk.getName() + "_IDX"));
                this.setColumnNamesFromList(fk.getFkColumns(), buffer, null);
                buffer.append(",");
                buffer.append(super.getSQLForAddForeignKey(fk));
                break;
            }
            case 16: {
                final IndexDefinition idx = (IndexDefinition)alterObject;
                buffer.append(" DROP INDEX ");
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? idx.getName() : ao.getActualConstraintName()));
                buffer.append("; ALTER TABLE ");
                buffer.append(this.getDBSpecificTableName(tableName));
                buffer.append("ADD INDEX ");
                buffer.append(this.getConstraintName(idx.getName()));
                buffer.append(" ");
                this.setColumnNamesFromList(idx.getColumns(), buffer, tableName);
                break;
            }
            case 15: {
                final UniqueKeyDefinition uk = (UniqueKeyDefinition)alterObject;
                buffer.append(" DROP INDEX ");
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? uk.getName() : ao.getActualConstraintName()));
                buffer.append("; ALTER TABLE ");
                buffer.append(this.getDBSpecificTableName(tableName));
                buffer.append(super.getSQLForAddUniqueKey(uk));
                break;
            }
            case 17: {
                final PrimaryKeyDefinition pk = (PrimaryKeyDefinition)((Object[])alterObject)[1];
                buffer.append(" DROP PRIMARY KEY; ALTER TABLE ");
                buffer.append(this.getDBSpecificTableName(tableName));
                buffer.append(" ADD PRIMARY KEY ");
                buffer.append(this.getConstraintName(pk.getName()));
                this.setColumnNamesFromList(pk.getColumnList(), buffer, null);
                break;
            }
            case 1: {
                final ColumnDefinition colDef = (ColumnDefinition)alterObject;
                if (colDef.getUniqueValueGeneration() != null && ao.fillUVHValues()) {
                    buffer.append(" ADD ");
                    buffer.append(this.getDBSpecificColumnName(colDef.getColumnName()));
                    buffer.append(" ");
                    this.appendColumnAttributes(operationType, colDef, buffer);
                    buffer.append(" AUTO_INCREMENT UNIQUE");
                    break;
                }
                super.appendStringForAlterOperation(ao, buffer);
                break;
            }
            default: {
                super.appendStringForAlterOperation(ao, buffer);
                break;
            }
        }
    }
    
    private String getForeignKeyIndex(final ForeignKeyDefinition fkDefn, final int index) {
        final StringBuilder fkIdxBuffer = new StringBuilder();
        final String constraintName = this.getConstraintName(fkDefn.getName());
        fkIdxBuffer.append("INDEX ");
        fkIdxBuffer.append(constraintName.substring(0, constraintName.lastIndexOf("`")));
        fkIdxBuffer.append("_IDX`");
        fkIdxBuffer.append(" (");
        final List fkCols = fkDefn.getForeignKeyColumns();
        for (int size = fkCols.size(), i = 0; i < size; ++i) {
            if (i != 0) {
                fkIdxBuffer.append(",");
            }
            final ForeignKeyColumnDefinition fkColDefn = fkCols.get(i);
            final String colName = fkColDefn.getLocalColumnDefinition().getColumnName();
            fkIdxBuffer.append(this.getDBSpecificColumnName(colName));
        }
        fkIdxBuffer.append(")");
        return fkIdxBuffer.toString();
    }
    
    @Override
    protected String lhsOfCriterion(final Column column, final boolean caseSensitive, final int comparator, final List<Table> derivedTableList) throws QueryConstructionException {
        if (column instanceof Function || column.getFunction() > 0 || column instanceof Operation) {
            return super.lhsOfCriterion(column, caseSensitive, comparator, derivedTableList);
        }
        final StringBuilder colBuffer = new StringBuilder(40);
        if (column instanceof DerivedColumn) {
            super.processColumn(column, colBuffer, caseSensitive, derivedTableList, Clause.WHERE, true);
            return colBuffer.toString();
        }
        colBuffer.append(this.processSimpleColumn(column, false, null, null));
        return colBuffer.toString();
    }
    
    @Override
    protected String processSimpleColumn(final Column column, final Clause columnBelongsToClause) {
        return this.processSimpleColumn(column, true, null, columnBelongsToClause);
    }
    
    @Override
    protected String processSimpleColumn(Column column, final boolean caseSensitive, final List<Table> derivedTableList, final Clause columnBelongsToClause) {
        final int type = column.getType();
        String retVal = null;
        String tableName = column.getTableAlias();
        String columnName = column.getColumnName();
        if (column instanceof LocaleColumn) {
            tableName = ((LocaleColumn)column).getColumn().getTableAlias();
            columnName = ((LocaleColumn)column).getColumn().getColumnName();
        }
        if (column instanceof Operation) {
            final Operation opr = (Operation)column;
            if (opr.getLHSArgument() instanceof Column) {
                column = (Column)opr.getLHSArgument();
            }
            else if (opr.getRHSArgument() instanceof Column) {
                column = (Column)opr.getRHSArgument();
            }
        }
        if (column instanceof Function) {
            final Function func = (Function)column;
            final Object[] functionArguments;
            final Object[] obj = functionArguments = func.getFunctionArguments();
            for (final Object object : functionArguments) {
                if (object instanceof Column) {
                    column = (Column)object;
                }
            }
        }
        if (tableName != null) {
            if (this.isAliasedColumnOfDerivedTable(derivedTableList, tableName, columnName)) {
                retVal = this.getDBSpecificTableName(tableName) + "." + this.getDBSpecificColumnAlias(columnName);
            }
            else {
                retVal = this.getDBSpecificTableName(tableName) + "." + this.getDBSpecificColumnName(columnName);
            }
            if (column instanceof LocaleColumn) {
                final LocaleColumn lc = (LocaleColumn)column;
                if (null != getCollationString(lc.getLocale())) {
                    final String collationString = " COLLATE " + getCollationString(lc.getLocale());
                    retVal += collationString;
                }
            }
        }
        else {
            retVal = this.getDBSpecificColumnName(columnName);
            if (column instanceof LocaleColumn) {
                final LocaleColumn lc = (LocaleColumn)column;
                if (null != getCollationString(lc.getLocale())) {
                    final String collationString = " COLLATE " + getCollationString(lc.getLocale());
                    retVal += collationString;
                }
            }
        }
        retVal = this.getDBSpecificDecryptionString(column, retVal);
        final boolean isExpression = column instanceof Function || column instanceof Operation;
        if (!this.hasCollation && caseSensitive && (type == 12 || type == 1) && !isExpression) {
            return "BINARY " + retVal;
        }
        if (this.hasCollation && columnBelongsToClause == Clause.SELECT && !column.isEncrypted() && (type == 12 || type == 1) && !isExpression) {
            retVal = (caseSensitive ? (retVal + (this.isDBCaseSensitive ? "" : (this.collationString + (column.getColumnName().equals(column.getColumnAlias()) ? (" AS " + this.getDBSpecificColumnAlias(column.getColumnAlias())) : "")))) : (retVal + (this.isDBCaseSensitive ? (this.collationString + (column.getColumnName().equals(column.getColumnAlias()) ? (" AS " + this.getDBSpecificColumnAlias(column.getColumnAlias())) : "")) : "")));
            return retVal;
        }
        return retVal;
    }
    
    public static String getCollationString(final Locale locale) {
        final List<String> list = new ArrayList<String>();
        final String languageName = locale.getDisplayLanguage();
        list.add("swedish");
        list.add("polish");
        list.add("turkish");
        list.add("spanish");
        list.add("czech");
        list.add("danish");
        list.add("hungarian");
        list.add("lithuanian");
        list.add("icelandic");
        list.add("latvian");
        list.add("romanian");
        list.add("slovenian");
        list.add("estonian");
        list.add("slovak");
        list.add("spanish2");
        list.add("roman");
        list.add("persian");
        list.add("esperanto");
        list.add("sinhala");
        if (list.contains(languageName.toLowerCase(Locale.ENGLISH))) {
            return "utf8_" + languageName.toLowerCase(Locale.ENGLISH) + "_ci";
        }
        return null;
    }
    
    @Override
    protected String getOrderByClause(final List sortColumns, final List selectColumns, final List<Table> derivedTableList, final List<Column> isCaseSensitive) throws QueryConstructionException {
        if (sortColumns.isEmpty()) {
            return null;
        }
        final StringBuilder orderByBuffer = new StringBuilder(100);
        orderByBuffer.append(" ORDER BY ");
        for (int sortColsSize = sortColumns.size(), i = 0; i < sortColsSize; ++i) {
            final SortColumn sortCol = sortColumns.get(i);
            if (sortCol.isCaseSensitive()) {
                isCaseSensitive.add(sortCol.getColumn());
            }
            if (sortCol == SortColumn.NULL_COLUMN) {
                orderByBuffer.append(" NULL");
                if (i + 1 < sortColsSize) {
                    orderByBuffer.append(", ");
                }
            }
            else {
                this.processSortColumn(sortCol, orderByBuffer, selectColumns, derivedTableList);
                final boolean ascending = sortCol.isAscending();
                if (!ascending) {
                    orderByBuffer.append(" DESC");
                }
                if (i + 1 < sortColsSize) {
                    orderByBuffer.append(", ");
                }
            }
        }
        return orderByBuffer.toString();
    }
    
    @Override
    protected String getValueString(String valueStr, final Column column, final int comparator, final boolean hasWildCard, final boolean isCaseSensitive) throws QueryConstructionException {
        final int type = column.getType();
        valueStr = super.getValueString(valueStr, column, comparator, hasWildCard, isCaseSensitive);
        final String dataType = column.getDataType();
        if (dataType != null && DataTypeUtil.isUDT(dataType)) {
            return DataTypeManager.getDataTypeDefinition(dataType).getDTSQLGenerator(this.getDBType()).handleCaseSensitive(valueStr, isCaseSensitive, column.isEncrypted(), Clause.WHERE);
        }
        if (!this.isNumeric(type)) {
            if (!PersistenceInitializer.onSAS()) {
                final StringBuilder buffer = new StringBuilder();
                if (this.hasCollation) {
                    buffer.append(" _");
                    buffer.append(this.defaultCharacterSet);
                    buffer.append(" ");
                    buffer.append(valueStr);
                    buffer.append(" ");
                    if ((column.getType() == 1 || column.getType() == 12 || (column.getType() == 2004 && column.isEncrypted())) && this.isDBCaseSensitive != isCaseSensitive) {
                        buffer.append(this.collationString);
                    }
                    else if (column.getType() == 2004 && !column.isEncrypted() && !isCaseSensitive) {
                        buffer.append(this.collationString.replace("_bin", "_general_ci"));
                    }
                    else if (this.isDBCaseSensitive && isCaseSensitive && column.isEncrypted()) {
                        buffer.append(this.collationStringForEncryptedCols);
                    }
                    return buffer.toString();
                }
            }
            else if (isCaseSensitive && (type == 12 || type == 1)) {
                valueStr = " BINARY " + valueStr;
            }
        }
        return valueStr;
    }
    
    @Override
    protected String getEscapeStringForSlash() {
        return " ESCAPE '" + this.getEscapeStr() + "'";
    }
    
    @Override
    protected char getEscapeCharacter() {
        return '\\';
    }
    
    @Override
    protected String getEscapeStr() {
        return String.valueOf(this.getEscapeCharacter()) + String.valueOf(this.getEscapeCharacter());
    }
    
    @Override
    public String getDBSpecificColumnName(String columnName) {
        columnName = columnName.replaceAll("`", "``");
        return (columnName != null && columnName.equals("*")) ? columnName : ("`" + columnName + "`");
    }
    
    @Override
    protected String getDBSpecificColumnAlias(final String columnAlias) {
        return this.getDBSpecificColumnName(columnAlias);
    }
    
    @Override
    public String getDBSpecificTableName(String tableName) {
        this.validateTableAlias(tableName);
        tableName = tableName.replaceAll("`", "``");
        return "`" + tableName + "`";
    }
    
    private void validateTableAlias(final String tableAlias) {
        if (!AppResources.getBoolean("newValidateTableAlias", Boolean.valueOf(false))) {
            if (tableAlias.contains("\\")) {
                throw new IllegalArgumentException("Table alias cannot have characters ::( \\ )");
            }
        }
        else if (tableAlias.contains("--") || tableAlias.contains(";") || tableAlias.contains("\\")) {
            throw new IllegalArgumentException("Table alias cannot have characters ::( -- , ; , \\)");
        }
    }
    
    @Override
    protected String getConstraintName(final String constraintName) {
        return "`" + constraintName + "`";
    }
    
    @Override
    public String getDBSpecificEncryptionString(final Column column, final String value) {
        if (!column.isEncrypted()) {
            return value;
        }
        if (DataTypeUtil.isUDT(column.getDataType())) {
            return DataTypeManager.getDataTypeDefinition(column.getDataType()).getDTSQLGenerator(this.getDBType()).getDTSpecificEncryptionString(column, value);
        }
        return column.isEncrypted() ? ("AES_ENCRYPT(" + value + ", '" + this.getKey() + "')") : value;
    }
    
    @Override
    public String getDBSpecificDecryptionString(final Column column, final String value) {
        if (!column.isEncrypted()) {
            return value;
        }
        if (DataTypeManager.isDataTypeSupported(column.getDataType()) && DataTypeManager.getDataTypeDefinition(column.getDataType()).getBaseType() == null) {
            return DataTypeManager.getDataTypeDefinition(column.getDataType()).getDTSQLGenerator(this.dbType).getDTSpecificDecryptionString(column, value);
        }
        return "CONVERT( " + this.getDecryptSQL(value, column.getDataType()) + " USING " + this.defaultCharacterSet + ")";
    }
    
    @Override
    public String getDecryptSQL(final String value, final String dataType) {
        if (DataTypeManager.isDataTypeSupported(dataType) && DataTypeManager.getDataTypeDefinition(dataType).getBaseType() == null) {
            return DataTypeManager.getDataTypeDefinition(dataType).getDTSQLGenerator(this.dbType).getDTSpecificDecryptionString(null, value);
        }
        return "AES_DECRYPT(" + value + ", '" + this.getKey() + "')";
    }
    
    @Override
    protected String getValue(final Object value, final boolean isCaseSensitive) {
        return value.toString();
    }
    
    @Override
    public List<String> getUpdateSQLForModifyColumnDataEncryption(final AlterTableQuery alterTableQuery) throws QueryConstructionException {
        final List<String> updateQueries = new ArrayList<String>();
        try {
            final List<AlterOperation> alterOperations = alterTableQuery.getAlterOperations();
            for (final AlterOperation ao : alterOperations) {
                if (ao.getOperationType() == 2) {
                    StringBuilder strBuff = null;
                    final ColumnDefinition newColDef = (ColumnDefinition)ao.getAlterObject();
                    final String tableName = newColDef.getTableName();
                    final String columnName = newColDef.getColumnName();
                    final boolean isAlreadyEncryptedCol = this.isCurrentlyEncrypted(tableName, columnName);
                    if (newColDef.isEncryptedColumn() && isAlreadyEncryptedCol) {
                        continue;
                    }
                    if (newColDef.isEncryptedColumn() || isAlreadyEncryptedCol) {
                        strBuff = new StringBuilder();
                        strBuff.append("UPDATE ");
                        strBuff.append(this.getDBSpecificTableName(tableName));
                        strBuff.append(" SET ");
                        strBuff.append(this.getDBSpecificColumnName(columnName));
                        strBuff.append(" = ");
                    }
                    if (newColDef.isEncryptedColumn() && !isAlreadyEncryptedCol) {
                        final Column column = new Column(tableName, columnName);
                        column.setDefinition(newColDef);
                        strBuff.append(this.getDBSpecificEncryptionString(column, this.getDBSpecificColumnName(newColDef.getColumnName())));
                    }
                    else if (!newColDef.isEncryptedColumn() && isAlreadyEncryptedCol) {
                        final Column column = new Column(tableName, columnName);
                        column.setDefinition(MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(columnName));
                        strBuff.append(this.getDBSpecificDecryptionString(column, this.getDBSpecificColumnName(newColDef.getColumnName())));
                    }
                    if (strBuff == null) {
                        continue;
                    }
                    updateQueries.add(strBuff.toString());
                }
            }
        }
        catch (final MetaDataException ex) {
            throw new QueryConstructionException("Exception occured during appendStringForDataEncryption", ex);
        }
        return updateQueries;
    }
    
    @Override
    protected void appendCreateTableOptions(final StringBuilder buff, final String createTableOptions) {
        buff.append((createTableOptions == null) ? " ENGINE = InnoDB" : createTableOptions);
    }
    
    @Override
    public String getSQLForCreateArchiveTable(final CreateTableLike cloneTdleDetails, final String createTableOptions, final boolean isPush) throws QueryConstructionException {
        final SelectQuery selectQry = cloneTdleDetails.getSelectQuery();
        if (selectQry != null) {
            return super.getSQLForCreateArchiveTable(cloneTdleDetails, createTableOptions, isPush);
        }
        final ArchiveTable tableToBeCloned = cloneTdleDetails.getArchiveTable();
        final StringBuilder buff = new StringBuilder();
        buff.append("CREATE TABLE ");
        final String tableNameToBeCreated = isPush ? tableToBeCloned.getArchiveTableName() : tableToBeCloned.getInvisibleTableName();
        buff.append(this.getDBSpecificTableName(tableNameToBeCreated));
        buff.append(" LIKE ");
        buff.append(this.getDBSpecificTableName(tableToBeCloned.getArchiveTableName()));
        return buff.toString();
    }
    
    @Override
    public String getBulkSql(final BulkLoad bulk, final BulkInsertObject bio) throws MetaDataException {
        StringBuilder sql = new StringBuilder("");
        final StringBuilder setSql = new StringBuilder("");
        int noOfCols = 0;
        int i = 1;
        int scharCount = 0;
        sql.append("LOAD DATA LOCAL INFILE 'd:/file.dat' INTO TABLE ");
        sql.append(this.getDBSpecificTableName(bulk.isArchivedTable() ? bulk.getArchivedTableName() : bulk.getTableName()));
        sql.append(" FIELDS TERMINATED BY '\\t' LINES TERMINATED BY '\\r'");
        noOfCols = bio.getColNames().size();
        String hexString = "";
        String encryptStr = "";
        sql.append(" (");
        String columnDataType = "";
        for (final String colName : bio.getColNames()) {
            columnDataType = bio.getColTypeNames().get(i++ - 1);
            if (DataTypeUtil.isEDT(columnDataType)) {
                columnDataType = DataTypeManager.getDataTypeDefinition(columnDataType).getBaseType();
            }
            if (columnDataType.equalsIgnoreCase("BLOB") || columnDataType.equalsIgnoreCase("LONGBLOB") || columnDataType.equalsIgnoreCase("SBLOB") || columnDataType.equalsIgnoreCase("NCHAR") || columnDataType.equalsIgnoreCase("SCHAR") || columnDataType.equalsIgnoreCase("CHAR") || columnDataType.equalsIgnoreCase("VARCHAR") || columnDataType.equalsIgnoreCase("TEXT") || columnDataType.equalsIgnoreCase("NTEXT") || columnDataType.equalsIgnoreCase("LONGTEXT")) {
                if (scharCount++ > 0) {
                    setSql.append(", ");
                }
                sql.append("@");
                sql.append(colName);
                setSql.append(this.getDBSpecificColumnName(colName));
                setSql.append("=");
                hexString = "UNHEX(@" + colName + ")";
                if (columnDataType.equalsIgnoreCase("SBLOB") || columnDataType.equalsIgnoreCase("SCHAR")) {
                    MysqlSQLGenerator.OUT.log(Level.FINE, "Handling of Mickey Based Tables (Includes Encryption)");
                    final Column column = Column.getColumn(bulk.getTableName(), colName);
                    final ColumnDefinition colDef = MetaDataUtil.getTableDefinitionByName(bulk.getTableName()).getColumnDefinitionByName(colName);
                    column.setDefinition(colDef);
                    encryptStr = this.getDBSpecificEncryptionString(column, hexString);
                    setSql.append(encryptStr);
                }
                else {
                    setSql.append(hexString);
                }
            }
            else if (DataTypeUtil.isUDT(columnDataType)) {
                if (!DataTypeManager.getDataTypeDefinition(columnDataType).getMeta().processInput()) {
                    continue;
                }
                MysqlSQLGenerator.OUT.log(Level.FINE, "Handling of Mickey Based Tables (Includes Encryption for UDT)");
                if (scharCount++ > 0) {
                    setSql.append(", ");
                }
                sql.append("@");
                sql.append(colName);
                setSql.append(this.getDBSpecificColumnName(colName));
                setSql.append("=");
                hexString = "@" + colName;
                final Column column = Column.getColumn(bulk.getTableName(), colName);
                final ColumnDefinition colDef = MetaDataUtil.getTableDefinitionByName(bulk.getTableName()).getColumnDefinitionByName(colName);
                column.setDefinition(colDef);
                encryptStr = this.getDBSpecificEncryptionString(column, hexString);
                setSql.append(encryptStr);
            }
            else {
                sql.append(this.getDBSpecificColumnName(colName));
            }
            sql.append(", ");
        }
        final String formedSQL = sql.toString().trim();
        final int pos = formedSQL.lastIndexOf(",");
        final int length = formedSQL.length();
        if (formedSQL.endsWith(",") && pos == length - 1) {
            sql = sql.delete(sql.lastIndexOf(","), sql.length());
        }
        sql.append(") ");
        if (scharCount > 0) {
            sql.append("SET " + setSql.toString());
        }
        return sql.toString();
    }
    
    @Override
    protected void appendDeleteJoin(final StringBuilder buff, final Join join) throws QueryConstructionException {
        final List<Join> joins = Arrays.asList(join);
        buff.append(" USING ");
        buff.append(this.formJoinString(joins, Collections.emptyList())).append(" ");
    }
    
    @Override
    protected String formCountSqlForGroupBy(final String constructedSQL, final String countSqlWithoutGroupBy, final String groupByColumns) {
        return "select COUNT(DISTINCT " + groupByColumns + " ) " + countSqlWithoutGroupBy;
    }
    
    @Override
    public String getSQLWithHiddenEncryptionKey(String query) {
        final String key = this.getKey();
        int index = query.indexOf("AES_DECRYPT");
        if (index == -1) {
            index = query.indexOf("AES_ENCRYPT");
        }
        if (index != -1) {
            index = query.indexOf('\'' + key + '\'', index);
            if (index != -1) {
                query = query.replace('\'' + key + '\'', '\'' + new String(new char[key.length()]).replaceAll("\u0000", "*") + '\'');
            }
        }
        return query;
    }
    
    void resetCollation() {
        this.collationString = "collate ";
        this.collationString = this.collationString + this.defaultCharacterSet + (this.isDBCaseSensitive ? ((this.defaultCharacterSet.indexOf("_") >= 0) ? "_ci" : "_general_ci") : "_bin");
        this.collationStringForEncryptedCols = "collate " + MysqlSQLGenerator.collationName;
    }
    
    @Override
    public String getSQLForIndex(final String tableName, final IndexDefinition iDef) throws QueryConstructionException {
        final StringJoiner indexString = new StringJoiner(" ");
        final StringJoiner columnString = new StringJoiner(",", "(", ")");
        String indexProps = null;
        indexString.add("CREATE INDEX").add(this.getConstraintName(iDef.getName())).add("ON").add(this.getDBSpecificTableName(tableName));
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException e) {
            throw new QueryConstructionException(e.getMessage(), e);
        }
        for (final IndexColumnDefinition icd : iDef.getColumnDefnitions()) {
            final String dataType = td.getColumnDefinitionByName(icd.getColumnName()).getDataType();
            final String columnName = icd.getColumnName();
            indexProps = this.getIndexPropString(icd.isAscending());
            final DataTypeDefinition udt = (dataType != null) ? DataTypeManager.getDataTypeDefinition(dataType) : null;
            if (udt != null && udt.getMeta() != null) {
                final String indexStr = udt.getDTSQLGenerator(this.getDBType()).getSQLForIndexColumn(icd);
                if (indexStr != null) {
                    columnString.add(indexStr);
                }
                else {
                    columnString.add(String.join("", this.getDBSpecificColumnName(columnName), indexProps));
                }
            }
            else {
                String size = "";
                if (iDef.getSize(columnName) != -1) {
                    size = String.join("", "(", String.valueOf(iDef.getSize(columnName)), ")");
                }
                columnString.add(String.join("", this.getDBSpecificColumnName(columnName), size, indexProps));
            }
        }
        indexString.add(columnString.toString());
        return indexString.toString();
    }
    
    private void validataIndexHint(final String tableName, final List<IndexHintClause> hintList) throws QueryConstructionException {
        boolean isUseIndexIdentified = false;
        boolean isForceIndexIdentified = false;
        IndexHintClause idxHint = null;
        for (int i = 0; i < hintList.size(); ++i) {
            idxHint = hintList.get(i);
            if (idxHint.getIndexHint().equals("use")) {
                isUseIndexIdentified = true;
            }
            else if (idxHint.getIndexHint().equals("force")) {
                isForceIndexIdentified = true;
                if (idxHint.getIndexes() == null || idxHint.getIndexes().isEmpty()) {
                    throw new QueryConstructionException("Index name list has to be given for FORCE index:: " + idxHint);
                }
            }
            else if (idxHint.getIndexHint().equals("ignore") && (idxHint.getIndexes() == null || idxHint.getIndexes().isEmpty())) {
                throw new QueryConstructionException("Index name list has to be given for IGNORE index:: " + idxHint);
            }
        }
        if (isUseIndexIdentified && isForceIndexIdentified) {
            throw new QueryConstructionException("Force Index and Use Index cannot be given for the same table " + tableName);
        }
    }
    
    @Override
    protected String formIndexHintSQL(final Table table, final List<IndexHintClause> hintList) throws QueryConstructionException {
        final StringBuilder sb = new StringBuilder(300);
        if (hintList != null && !hintList.isEmpty()) {
            this.validataIndexHint(table.getTableName(), hintList);
            IndexHintClause idxHint = null;
            for (int i = 0; i < hintList.size(); ++i) {
                idxHint = hintList.get(i);
                this.constructSQLForIndexHint(idxHint, sb);
            }
            return sb.toString();
        }
        return null;
    }
    
    private String getIndexHintForClauseSQL(final SelectQuery.Clause idxFor) {
        if (idxFor == null) {
            return null;
        }
        if (idxFor.equals("join")) {
            return " FOR JOIN";
        }
        if (idxFor.equals("orderby")) {
            return " FOR ORDER BY";
        }
        if (idxFor.equals("groupby")) {
            return " FOR GROUP BY";
        }
        return null;
    }
    
    private void appendIndexesNames(final List<String> idxNames, final StringBuilder sb) {
        sb.append(" ( ");
        for (int i = 0; i < idxNames.size(); ++i) {
            if (i != 0) {
                sb.append(" , ");
            }
            sb.append("`" + idxNames.get(i) + "`");
        }
        sb.append(" ) ");
    }
    
    private String getIndexHint(final IndexHintClause idxHint) {
        String cons = null;
        if (idxHint.getIndexHint().equals("use")) {
            cons = " USE";
        }
        else if (idxHint.getIndexHint().equals("force")) {
            cons = " FORCE";
        }
        else if (idxHint.getIndexHint().equals("ignore")) {
            cons = " IGNORE";
        }
        if (idxHint.isKey()) {
            cons += " KEY";
        }
        else {
            cons += " INDEX";
        }
        return cons;
    }
    
    private void constructSQLForIndexHint(final IndexHintClause idxHint, final StringBuilder sb) {
        String idxHintForSQL = null;
        List<String> indexNames = new ArrayList<String>();
        sb.append(this.getIndexHint(idxHint));
        idxHintForSQL = this.getIndexHintForClauseSQL(idxHint.getIndexHintFor());
        if (idxHintForSQL != null) {
            sb.append(idxHintForSQL);
        }
        indexNames = idxHint.getIndexes();
        this.appendIndexesNames(indexNames, sb);
    }
    
    @Override
    protected void getAliasedColumn(final SelectQuery sq, final Column column, final StringBuilder buffer) throws QueryConstructionException {
        final String columnAlias = column.getColumnAlias();
        if (columnAlias != null) {
            this.validateColumnAlias(columnAlias);
        }
        super.getAliasedColumn(sq, column, buffer);
    }
    
    private void validateColumnAlias(final String columnAlias) throws QueryConstructionException {
        if (columnAlias.contains("\\")) {
            throw new QueryConstructionException("Column alias cannot have characters ::( \\ )");
        }
    }
    
    static {
        OUT = Logger.getLogger(MysqlSQLGenerator.class.getName());
        MysqlSQLGenerator.collationName = null;
    }
}
