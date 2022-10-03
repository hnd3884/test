package com.adventnet.db.adapter.postgres;

import com.adventnet.db.adapter.DCSQLGenerator;
import com.adventnet.db.persistence.metadata.IndexColumnDefinition;
import java.util.StringJoiner;
import java.util.Collection;
import java.util.Set;
import java.sql.SQLException;
import com.adventnet.db.adapter.BulkInsertObject;
import com.adventnet.ds.query.BulkLoad;
import com.adventnet.ds.query.DerivedTable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import com.adventnet.ds.query.Operation;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.Function;
import com.adventnet.ds.query.ArchiveTable;
import java.util.ArrayList;
import com.adventnet.ds.query.CreateTableLike;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.DataTypeDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import java.util.Map;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import java.util.List;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.DeleteQuery;
import java.util.logging.Logger;
import com.adventnet.db.adapter.Ansi92SQLGenerator;

public class PostgresSQLGenerator extends Ansi92SQLGenerator
{
    private static final Logger OUT;
    private String encryptionAlgo;
    private String encryptionS2kMode;
    
    public PostgresSQLGenerator() {
        this.encryptionAlgo = null;
        this.encryptionS2kMode = null;
        this.dbType = "postgres";
    }
    
    @Override
    protected String getCascadeString() {
        return "CASCADE";
    }
    
    @Override
    public String getSQLForDelete(final DeleteQuery query) throws QueryConstructionException {
        final String tableName = query.getTableName();
        final StringBuilder buff = new StringBuilder();
        try {
            final int numOfRows = query.getLimit();
            final Criteria criteria = query.getCriteria();
            final List<Join> joins = query.getJoins();
            if (!joins.isEmpty() || numOfRows > 0) {
                buff.append("DELETE FROM ");
                buff.append(this.getDBSpecificTableName(tableName));
                final List sortcolumns = query.getSortColumns();
                final StringBuilder pkBuffer = new StringBuilder(100);
                final List pkcolList = MetaDataUtil.getTableDefinitionByName(tableName).getPrimaryKey().getColumnList();
                final int colSize = pkcolList.size();
                final SelectQuery sQuery = new SelectQueryImpl(Table.getTable(tableName));
                String columnName = null;
                for (int i = 0; i < colSize; ++i) {
                    if (i > 0) {
                        pkBuffer.append(", ");
                    }
                    columnName = pkcolList.get(i);
                    final Column column = Column.getColumn(tableName, columnName);
                    sQuery.addSelectColumn(column);
                    final String colName = this.getDBSpecificColumnName(columnName);
                    pkBuffer.append(colName);
                }
                final String pkcols = pkBuffer.toString();
                sQuery.setCriteria(criteria);
                if (!joins.isEmpty()) {
                    if (numOfRows > 0) {
                        throw new QueryConstructionException("ORDER BY and LIMIT cannot be used with JOIN");
                    }
                    for (final Join join : joins) {
                        sQuery.addJoin(join);
                    }
                }
                if (numOfRows > 0) {
                    sQuery.addSortColumns(sortcolumns);
                    final Range range = new Range(1, numOfRows);
                    sQuery.setRange(range);
                }
                buff.append(" WHERE (" + pkcols + ") IN (");
                buff.append(this.getSQLForSelect(sQuery));
                buff.append(")");
                return buff.toString();
            }
            return this.getSQLForDelete(tableName, criteria);
        }
        catch (final MetaDataException mde) {
            throw new QueryConstructionException("Exception occurred while fetching the MetaData Information for constructing delete sql :: " + mde);
        }
    }
    
    @Override
    public String getSQLForUpdate(final List tableList, final Map newValues, final Criteria criteria, final List joins) throws QueryConstructionException {
        final Table targetTable = tableList.get(0);
        final String targetTableName = targetTable.getTableName();
        return this.getSQLForUpdate(targetTableName, newValues, criteria, joins);
    }
    
    @Override
    public String getSQLForUpdate(final String tableName, final Map newValues, final Criteria criteria, final List joins) throws QueryConstructionException {
        final String targetTableName = tableName;
        if (targetTableName == null) {
            throw new QueryConstructionException("Table name can not be null");
        }
        if (joins == null || joins.isEmpty()) {
            return this.getSQLForUpdate(targetTableName, newValues, criteria);
        }
        final StringBuilder updateBuffer = new StringBuilder(100);
        final String setSQL = this.getUpdateColumnsSQL(targetTableName, newValues);
        updateBuffer.append("UPDATE ");
        updateBuffer.append(this.getDBSpecificTableName(targetTableName));
        updateBuffer.append(" SET ");
        updateBuffer.append(setSQL);
        final StringBuilder fromBuffer = new StringBuilder(300);
        final StringBuilder whereBuffer = new StringBuilder(300);
        fromBuffer.append(" FROM ");
        for (int i = 0; i < joins.size(); ++i) {
            final Join join = joins.get(i);
            String baseTableName = join.getBaseTableName();
            final String baseTableNameAlias = join.getBaseTableAlias();
            if (baseTableNameAlias != null && !baseTableName.equals(baseTableNameAlias)) {
                baseTableName = baseTableNameAlias;
            }
            final String joinCriteria = this.getJoinCriteriaString(join, null, null);
            if (baseTableName.equalsIgnoreCase(targetTableName)) {
                if (i > 0) {
                    fromBuffer.append(" , ");
                    whereBuffer.append(" AND ");
                }
                fromBuffer.append(this.getJoinReferencedTableString(join));
                whereBuffer.append(joinCriteria);
            }
            else {
                fromBuffer.append(this.getJoinString(join));
                fromBuffer.append(this.getJoinReferencedTableString(join));
                fromBuffer.append(" ON ");
                fromBuffer.append(joinCriteria);
            }
        }
        updateBuffer.append(fromBuffer.toString());
        final String updateCriteria = this.formWhereClause(criteria, false, null);
        updateBuffer.append(" WHERE (");
        if (updateCriteria != null) {
            updateBuffer.append(whereBuffer.toString());
            updateBuffer.append(" AND ");
            updateBuffer.append(updateCriteria);
        }
        else {
            updateBuffer.append(whereBuffer.toString());
        }
        updateBuffer.append(")");
        final String sql = updateBuffer.toString();
        return sql;
    }
    
    @Override
    public String getSQLForLock(final List tableList) throws QueryConstructionException {
        String lockSQL = "LOCK TABLES";
        for (int tabSize = tableList.size(), i = 0; i < tabSize; ++i) {
            if (i != 0) {
                lockSQL += ",";
            }
            lockSQL = lockSQL + " " + tableList.get(i) + " READ";
        }
        return lockSQL;
    }
    
    @Override
    public String getSQLForLock(final String tableName) throws QueryConstructionException {
        return "LOCK TABLE " + tableName + " READ";
    }
    
    @Override
    protected String getDefaultValue(String dataType, final Object defVal) throws QueryConstructionException {
        if (DataTypeUtil.isEDT(dataType)) {
            dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
        }
        if (defVal == null) {
            return null;
        }
        final String defaultValue = String.valueOf(defVal);
        String retVal = "";
        if (dataType.equals("BOOLEAN")) {
            retVal = Boolean.valueOf(defaultValue).toString();
            return retVal;
        }
        return super.getDefaultValue(dataType, defVal);
    }
    
    @Override
    protected String processColumnDefn(final ColumnDefinition colDefn) throws QueryConstructionException {
        String retString = super.processColumnDefn(colDefn);
        String dataType = colDefn.getDataType();
        if (DataTypeManager.getDataTypeDefinition(dataType) != null) {
            final String baseType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
            if (baseType != null) {
                dataType = baseType;
            }
        }
        int maxLength = colDefn.getMaxLength();
        final DataTypeDefinition dataDef = DataTypeManager.getDataTypeDefinition(dataType);
        if (("CHAR".equals(dataType) || "NCHAR".equals(dataType) || (DataTypeUtil.isUDT(dataType) && dataDef.getMeta().processCheckConstraint())) && maxLength != -1) {
            final String tableName = colDefn.getTableName();
            final String columnName = colDefn.getColumnName();
            if (maxLength == 0) {
                maxLength = 50;
            }
            retString += this.getSQLForMaxSizeCheckConstraint(tableName, columnName, maxLength);
        }
        return retString;
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
        String dataTypeStr = null;
        if (dataType.equals("CHAR") || dataType.equals("NCHAR")) {
            dataTypeStr = "CITEXT";
        }
        else if (dataType.equals("SCHAR")) {
            dataTypeStr = "BYTEA";
        }
        else if (dataType.equals("INTEGER")) {
            if (maxLength == 0) {
                maxLength = 10;
            }
            dataTypeStr = "INT";
        }
        else if (dataType.equals("BIGINT")) {
            if (maxLength == 0) {
                maxLength = 19;
            }
            dataTypeStr = "BIGINT";
        }
        else if (dataType.equals("TINYINT")) {
            dataTypeStr = "SMALLINT";
        }
        else if (dataType.equals("BLOB") || dataType.equals("SBLOB")) {
            dataTypeStr = "BYTEA";
        }
        else if (dataType.equals("BOOLEAN")) {
            dataTypeStr = "BOOLEAN";
        }
        else if (dataType.equals("DOUBLE")) {
            dataTypeStr = "DOUBLE PRECISION";
        }
        else if (dataType.equals("FLOAT")) {
            dataTypeStr = "REAL";
        }
        else if (dataType.equals("DECIMAL")) {
            dataTypeStr = dataType + "(" + ((maxLength == 0) ? 16 : maxLength) + ", " + ((precision == 0) ? 4 : precision) + ")";
        }
        else if (dataType.equals("DATETIME")) {
            dataTypeStr = "TIMESTAMP";
        }
        else if (dataType.equals("DCJSON")) {
            dataTypeStr = "JSONB";
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
    
    private static TableDefinition getTableDefinition(final String tableName) throws QueryConstructionException {
        try {
            return MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException mde) {
            throw new QueryConstructionException(mde.getMessage());
        }
    }
    
    @Override
    protected void appendStringForAlterOperation(final AlterOperation ao, final StringBuilder buffer) throws QueryConstructionException {
        final int operation = ao.getOperationType();
        if (operation == 10 || operation == 11) {
            return;
        }
        final Object alterObject = ao.getAlterObject();
        switch (operation) {
            case 1: {
                final ColumnDefinition colDef = (ColumnDefinition)alterObject;
                buffer.append(" ADD ");
                buffer.append(this.getDBSpecificColumnName(colDef.getColumnName()));
                buffer.append(" ");
                this.appendColumnAttributes(operation, colDef, buffer);
                String dataType = colDef.getDataType();
                if (DataTypeUtil.isEDT(dataType)) {
                    dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
                }
                final DataTypeDefinition dataDef = DataTypeManager.getDataTypeDefinition(dataType);
                if (("CHAR".equals(dataType) || "NCHAR".equals(dataType) || (DataTypeManager.isDataTypeSupported(dataType) && dataDef.getMeta() != null && dataDef.getMeta().processCheckConstraint())) && colDef.getMaxLength() != -1) {
                    final String tableName = ao.getTableName();
                    int maxLength = colDef.getMaxLength();
                    if (maxLength == 0) {
                        maxLength = 50;
                    }
                    buffer.append(", ADD");
                    buffer.append(this.getSQLForMaxSizeCheckConstraint(tableName, colDef.getColumnName(), maxLength));
                    break;
                }
                break;
            }
            case 2: {
                final ColumnDefinition colDef = (ColumnDefinition)alterObject;
                buffer.append(" ALTER ");
                buffer.append(" ");
                buffer.append(this.getDBSpecificColumnName(colDef.getColumnName()));
                buffer.append(" TYPE ");
                buffer.append(this.getDBDataType(colDef));
                this.appendStringForDataTypeConvert(colDef, buffer);
                this.appendColumnAttributes(operation, colDef, buffer);
                break;
            }
            case 3: {
                buffer.append(" DROP COLUMN ");
                buffer.append(this.getDBSpecificColumnName((String)alterObject));
                break;
            }
            case 12: {
                buffer.append(" RENAME COLUMN ");
                final String renColName_Old = ((String[])alterObject)[0];
                final String renColName_New = ((String[])alterObject)[1];
                buffer.append(this.getDBSpecificColumnName(renColName_Old));
                buffer.append(" TO ");
                buffer.append(this.getDBSpecificColumnName(renColName_New));
                break;
            }
            case 13: {
                final String newTableName = (String)alterObject;
                buffer.append(" RENAME TO ");
                buffer.append(this.getDBSpecificTableName(newTableName));
                break;
            }
            case 6: {
                final ForeignKeyDefinition fkDef = (ForeignKeyDefinition)alterObject;
                buffer.append(" ADD CONSTRAINT ");
                buffer.append(this.getConstraintName(fkDef.getName()));
                buffer.append(" FOREIGN KEY ");
                this.setColumnNamesFromList(fkDef.getFkColumns(), buffer, null);
                buffer.append(" REFERENCES ");
                buffer.append(this.getDBSpecificTableName(fkDef.getMasterTableName()));
                this.setColumnNamesFromList(fkDef.getFkRefColumns(), buffer, null);
                buffer.append(" ");
                buffer.append(this.getStringConstraint(fkDef.getConstraints()));
                break;
            }
            case 5:
            case 7:
            case 8: {
                final String delConstraintName = (String)alterObject;
                buffer.append(" DROP CONSTRAINT ");
                if (operation == 5) {
                    buffer.append("IF EXISTS ");
                }
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? delConstraintName : ao.getActualConstraintName()));
                break;
            }
            case 9: {
                final PrimaryKeyDefinition pkDef = (PrimaryKeyDefinition)alterObject;
                buffer.append(" ADD CONSTRAINT ");
                buffer.append(this.getConstraintName(pkDef.getName()));
                buffer.append(" PRIMARY KEY ");
                this.setColumnNamesFromList(pkDef.getColumnList(), buffer, null);
                break;
            }
            case 10:
            case 11: {
                break;
            }
            case 14: {
                final ForeignKeyDefinition fk = (ForeignKeyDefinition)alterObject;
                buffer.append(" DROP CONSTRAINT ");
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? fk.getName() : ao.getActualConstraintName()));
                final IndexDefinition indxDef = this.getIndexDefForFK(fk);
                buffer.append("; ");
                buffer.append(this.getSQLForDropIndex(ao.getTableName(), indxDef.getName()));
                buffer.append("; ");
                buffer.append(this.getSQLForIndex(ao.getTableName(), indxDef));
                buffer.append(";ALTER TABLE ");
                buffer.append(this.getDBSpecificTableName(ao.getTableName()));
                buffer.append(" ADD CONSTRAINT ");
                buffer.append(this.getConstraintName(fk.getName()));
                buffer.append(" FOREIGN KEY ");
                this.setColumnNamesFromList(fk.getFkColumns(), buffer, null);
                buffer.append(" REFERENCES ");
                buffer.append(this.getDBSpecificTableName(fk.getMasterTableName()));
                this.setColumnNamesFromList(fk.getFkRefColumns(), buffer, null);
                buffer.append(" ");
                buffer.append(this.getStringConstraint(fk.getConstraints()));
                buffer.append(";");
                break;
            }
            case 16: {
                final IndexDefinition idx = (IndexDefinition)alterObject;
                buffer.append(this.getSQLForDropIndex(ao.getTableName(), (ao.getActualConstraintName() == null) ? idx.getName() : ao.getActualConstraintName()));
                buffer.append(";");
                buffer.append(this.getSQLForIndex(ao.getTableName(), idx));
                buffer.append(";");
                break;
            }
            case 17: {
                final String oldPKName = ((Object[])alterObject)[0].toString();
                final PrimaryKeyDefinition pk = (PrimaryKeyDefinition)((Object[])alterObject)[1];
                buffer.append(" DROP CONSTRAINT ");
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? oldPKName : ao.getActualConstraintName()));
                buffer.append(";ALTER TABLE ");
                buffer.append(this.getDBSpecificTableName(ao.getTableName()));
                buffer.append(" ADD CONSTRAINT ");
                buffer.append(this.getConstraintName(pk.getName()));
                buffer.append(" PRIMARY KEY ");
                this.setColumnNamesFromList(pk.getColumnList(), buffer, null);
                break;
            }
            case 15: {
                final UniqueKeyDefinition uk = (UniqueKeyDefinition)alterObject;
                buffer.append(" DROP CONSTRAINT ");
                buffer.append("IF EXISTS ");
                buffer.append(this.getConstraintName((ao.getActualConstraintName() == null) ? uk.getName() : ao.getActualConstraintName()));
                buffer.append(";ALTER TABLE ");
                buffer.append(this.getDBSpecificTableName(ao.getTableName()));
                buffer.append(super.getSQLForAddUniqueKey(uk));
                break;
            }
            default: {
                super.appendStringForAlterOperation(ao, buffer);
                break;
            }
        }
    }
    
    @Override
    public String getSQLForAlterTable(final AlterTableQuery alterTableQuery) throws QueryConstructionException {
        final StringBuilder buffer = new StringBuilder();
        final List<AlterOperation> alterOperations = alterTableQuery.getAlterOperations();
        final boolean isRevert = alterTableQuery.isRevert();
        for (final AlterOperation ao : alterOperations) {
            if ((ao.getOperationType() == 11 && !isRevert) || (ao.getOperationType() == 10 && isRevert)) {
                final String indexName = (String)(isRevert ? this.getAlterOperationForRevert(ao).getAlterObject() : ((String)ao.getAlterObject()));
                buffer.append(this.getSQLForDropIndex(ao.getTableName(), indexName));
                buffer.append("; ");
            }
            else {
                if (ao.getOperationType() != 7) {
                    continue;
                }
                final String fkName = (String)ao.getAlterObject();
                final IndexDefinition indxDef = new IndexDefinition();
                indxDef.setName(this.getIndexName(fkName));
                this.appendStringForFKIndex(buffer, alterTableQuery.getTableName(), indxDef, ao.getOperationType(), isRevert);
                buffer.append("; ");
            }
        }
        final String returnedQuery = super.getSQLForAlterTable(alterTableQuery);
        buffer.append(returnedQuery);
        for (final AlterOperation ao2 : alterOperations) {
            final int operation = ao2.getOperationType();
            final String tableName = ao2.getTableName();
            if ((operation == 10 && !isRevert) || (operation == 11 && isRevert)) {
                final IndexDefinition indexDef = (IndexDefinition)(isRevert ? this.getAlterOperationForRevert(ao2).getAlterObject() : ((IndexDefinition)ao2.getAlterObject()));
                buffer.append("; ");
                buffer.append(this.getSQLForIndex(tableName, indexDef));
            }
            else if (operation == 6) {
                final ForeignKeyDefinition fkDef = (ForeignKeyDefinition)ao2.getAlterObject();
                final IndexDefinition indxDef2 = this.getIndexDefForFK(fkDef);
                buffer.append("; ");
                this.appendStringForFKIndex(buffer, alterTableQuery.getTableName(), indxDef2, operation, alterTableQuery.isRevert());
            }
            else {
                if (operation != 1) {
                    continue;
                }
                final ColumnDefinition colDef = (ColumnDefinition)ao2.getAlterObject();
                if (colDef.getUniqueValueGeneration() == null || !ao2.fillUVHValues()) {
                    continue;
                }
                buffer.append("; ");
                buffer.append(this.getSQLForCreateSequence(tableName));
                buffer.append("; ALTER TABLE ");
                buffer.append(this.getDBSpecificTableName(tableName));
                buffer.append(" ADD ");
                buffer.append(this.getDBSpecificColumnName("_" + colDef.getColumnName() + "_"));
                buffer.append(" ");
                buffer.append(this.getDBDataType(colDef));
                buffer.append(" DEFAULT NEXTVAL('");
                buffer.append(tableName);
                buffer.append("_SEQ'); UPDATE ");
                buffer.append(this.getDBSpecificTableName(tableName));
                buffer.append(" SET ");
                buffer.append(this.getDBSpecificColumnName(colDef.getColumnName()));
                buffer.append("=");
                buffer.append(this.getDBSpecificColumnName("_" + colDef.getColumnName() + "_"));
                buffer.append("; ALTER TABLE ");
                buffer.append(this.getDBSpecificTableName(tableName));
                buffer.append(" DROP COLUMN ");
                buffer.append(this.getDBSpecificColumnName("_" + colDef.getColumnName() + "_"));
                buffer.append(";");
                buffer.append(this.getSQLForDropSequence(tableName));
            }
        }
        return buffer.toString();
    }
    
    private String getSQLForCreateSequence(final String tableName) {
        final String sequenceName = tableName + "_SEQ";
        final StringBuilder buffer = new StringBuilder();
        buffer.append("CREATE SEQUENCE ");
        buffer.append(sequenceName);
        return buffer.toString();
    }
    
    private String getSQLForDropSequence(final String tableName) {
        final String sequenceName = tableName + "_SEQ";
        final StringBuilder buffer = new StringBuilder();
        buffer.append("DROP SEQUENCE ");
        buffer.append(sequenceName);
        return buffer.toString();
    }
    
    @Override
    public String getDBSpecificColumnName(String columnName) {
        if (columnName.equals("*")) {
            return columnName;
        }
        columnName = columnName.replaceAll("\"", "\"\"");
        if (PostgresSQLGenerator.isAutoQuoteEnabled || columnName.contains("\"") || columnName.contains("--") || this.isKeyWord(columnName)) {
            return "\"" + columnName + "\"";
        }
        return columnName;
    }
    
    @Override
    public String getDBSpecificTableName(String tableName) {
        this.validateTableAlias(tableName);
        if (this.isKeyWord(tableName)) {
            if (PersistenceInitializer.isMDS()) {
                final int index = tableName.indexOf("__");
                if (index > 0) {
                    tableName = tableName.substring(index + 2);
                }
            }
            return "\"" + tableName + "\"";
        }
        if (PostgresSQLGenerator.isAutoQuoteEnabled || (!PostgresSQLGenerator.isAutoQuoteEnabled && this.isKeyWord(tableName))) {
            return "\"" + tableName + "\"";
        }
        return tableName;
    }
    
    private void validateTableAlias(final String tableAlias) {
        if (tableAlias.contains("--") || tableAlias.contains(";") || tableAlias.contains("\\")) {
            throw new IllegalArgumentException("Table alias cannot have characters ::( -- , ; , \\)");
        }
    }
    
    @Override
    public String getDBSpecificEncryptionString(final Column column, final String value) {
        if (!column.isEncrypted()) {
            return value;
        }
        String dataType = column.getDefinition().getDataType();
        if (DataTypeUtil.isUDT(dataType)) {
            return DataTypeManager.getDataTypeDefinition(dataType).getDTSQLGenerator(this.getDBType()).getDTSpecificEncryptionString(column, value);
        }
        if (DataTypeUtil.isEDT(dataType)) {
            dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
        }
        final String encryptFunctionName = dataType.equals("SBLOB") ? "pgp_sym_encrypt_bytea" : "pgp_sym_encrypt";
        return encryptFunctionName + "(" + value + ",'" + this.getKey() + "','s2k-mode=" + this.getEncryptionS2kMode() + ", cipher-algo=" + this.getEncryptionAlgorithm() + "')";
    }
    
    @Override
    public String getDBSpecificDecryptionString(final Column column, final String value) {
        if (!column.isEncrypted()) {
            return value;
        }
        String dataType = column.getDefinition().getDataType();
        if (DataTypeUtil.isUDT(dataType)) {
            return DataTypeManager.getDataTypeDefinition(dataType).getDTSQLGenerator(this.getDBType()).getDTSpecificDecryptionString(column, value);
        }
        if (DataTypeUtil.isEDT(dataType)) {
            dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
        }
        return this.getDecryptSQL(value, dataType);
    }
    
    @Override
    public String getDecryptSQL(final String value, final String dataType) {
        if (DataTypeUtil.isUDT(dataType)) {
            return DataTypeManager.getDataTypeDefinition(dataType).getDTSQLGenerator(this.dbType).getDTSpecificDecryptionString(null, value);
        }
        final String encryptFunctionName = dataType.equals("SBLOB") ? "pgp_sym_decrypt_bytea" : "pgp_sym_decrypt";
        return encryptFunctionName + "(" + value + ",'" + this.getKey() + "','cipher-algo=" + this.getEncryptionAlgorithm() + "')";
    }
    
    @Override
    public String getSQLForCreateArchiveTable(final CreateTableLike cloneTdleDetails, final String createTableOptions, final boolean isPush) throws QueryConstructionException {
        final TableDefinition tb = cloneTdleDetails.getTableDefinition();
        final ArchiveTable tableToBeCloned = cloneTdleDetails.getArchiveTable();
        final String tableNameToBeCreated = isPush ? tableToBeCloned.getArchiveTableName() : tableToBeCloned.getInvisibleTableName();
        final StringBuilder buff = new StringBuilder();
        buff.append(super.getSQLForCreateArchiveTable(cloneTdleDetails, createTableOptions, isPush));
        final List<ColumnDefinition> dyColDefs = new ArrayList<ColumnDefinition>();
        if (!isPush) {
            for (final ColumnDefinition cd : tb.getColumnList()) {
                if ((cd.getDataType().equals("CHAR") || cd.getDataType().equals("NCHAR")) && cd.getMaxLength() != -1) {
                    if (cd.isDynamic()) {
                        dyColDefs.add(cd);
                    }
                    else {
                        buff.append("; ALTER TABLE ");
                        buff.append(this.getDBSpecificTableName(tableNameToBeCreated));
                        buff.append(" ADD ");
                        buff.append(this.getSQLForMaxSizeCheckConstraint(tableToBeCloned.getTableName(), cd.getColumnName(), cd.getMaxLength()));
                    }
                }
            }
            if (!dyColDefs.isEmpty()) {
                final String dcSql = this.getDCSQLGenerator(tb.getDynamicColumnType()).getSQLForArchiveTableCheckConstraint(tableNameToBeCreated, dyColDefs);
                if (dcSql != null) {
                    buff.append(dcSql);
                }
            }
        }
        return buff.toString();
    }
    
    protected String getSQLForAlterMaxSizeCheckConstraint(final AlterTableQuery alterTableQuery, final Map columnNameVsconstraintName) throws QueryConstructionException {
        final String tableName = alterTableQuery.getTableName();
        final TableDefinition td = getTableDefinition(tableName);
        final StringBuilder buffer = new StringBuilder();
        if (td != null) {
            final List<AlterOperation> alterOperations = alterTableQuery.getAlterOperations();
            for (final AlterOperation ao : alterOperations) {
                final int operation = ao.getOperationType();
                final Object alterObject = ao.getAlterObject();
                if (operation == 13) {
                    final String newTableName = (String)alterObject;
                    for (final ColumnDefinition cd : td.getColumnList()) {
                        if ((cd.getDataType().equals("CHAR") || cd.getDataType().equals("NCHAR")) && cd.getMaxLength() != -1) {
                            String dbConstraintName = null;
                            if (columnNameVsconstraintName.containsKey(cd.getColumnName().toLowerCase())) {
                                dbConstraintName = columnNameVsconstraintName.get(cd.getColumnName().toLowerCase()).toString();
                            }
                            final String newCheckConstraintName = this.getMaxSizeCheckConstraintName(newTableName, cd.getColumnName());
                            if (dbConstraintName == null || dbConstraintName.equalsIgnoreCase(newCheckConstraintName)) {
                                continue;
                            }
                            this.appendStringForRenameCheckConstraint(buffer, newTableName, dbConstraintName, newCheckConstraintName);
                        }
                    }
                }
                if (operation == 12) {
                    final String renColName_Old = ((String[])alterObject)[0];
                    final String renColName_New = ((String[])alterObject)[1];
                    final ColumnDefinition colDef = td.getColumnDefinitionByName(renColName_Old);
                    if (colDef != null && (colDef.getDataType().equals("CHAR") || colDef.getDataType().equals("NCHAR")) && colDef != null && colDef.getMaxLength() != -1) {
                        String dbConstraintName = null;
                        if (columnNameVsconstraintName.containsKey(renColName_Old.toLowerCase())) {
                            dbConstraintName = columnNameVsconstraintName.get(renColName_Old.toLowerCase()).toString();
                        }
                        final String newCheckConstraintName = this.getMaxSizeCheckConstraintName(tableName, renColName_New);
                        if (dbConstraintName != null && !dbConstraintName.equalsIgnoreCase(newCheckConstraintName)) {
                            this.appendStringForRenameCheckConstraint(buffer, tableName, dbConstraintName, newCheckConstraintName);
                        }
                    }
                }
                if (operation == 2) {
                    final ColumnDefinition colDef2 = (ColumnDefinition)alterObject;
                    final ColumnDefinition oldCD = td.getColumnDefinitionByName(colDef2.getColumnName());
                    String newDataType = colDef2.getDataType();
                    if (DataTypeUtil.isEDT(newDataType)) {
                        newDataType = DataTypeManager.getDataTypeDefinition(newDataType).getBaseType();
                    }
                    String oldDataType = oldCD.getDataType();
                    if (DataTypeUtil.isEDT(oldDataType)) {
                        oldDataType = DataTypeManager.getDataTypeDefinition(oldDataType).getBaseType();
                    }
                    if ("CHAR".equals(oldDataType) || "NCHAR".equals(oldDataType)) {
                        final boolean isNewTypeCHAR = "CHAR".equals(newDataType) || "NCHAR".equals(newDataType);
                        final boolean isNewTypeCHAR_with_maxlen_changed = isNewTypeCHAR && colDef2.getMaxLength() != oldCD.getMaxLength();
                        if (!isNewTypeCHAR || isNewTypeCHAR_with_maxlen_changed) {
                            String dbConstraintName2 = null;
                            if (columnNameVsconstraintName.containsKey(colDef2.getColumnName().toLowerCase())) {
                                dbConstraintName2 = columnNameVsconstraintName.get(colDef2.getColumnName().toLowerCase()).toString();
                            }
                            if (dbConstraintName2 != null) {
                                buffer.append(" , DROP CONSTRAINT IF EXISTS ");
                                buffer.append(dbConstraintName2);
                            }
                        }
                        if (!isNewTypeCHAR_with_maxlen_changed || colDef2.getMaxLength() == -1) {
                            continue;
                        }
                        buffer.append(" , ADD");
                        buffer.append(this.getSQLForMaxSizeCheckConstraint(td.getTableName(), colDef2.getColumnName(), colDef2.getMaxLength()));
                    }
                    else {
                        if ((!"CHAR".equals(newDataType) && !"NCHAR".equals(newDataType)) || colDef2.getMaxLength() == -1) {
                            continue;
                        }
                        int maxLength = colDef2.getMaxLength();
                        if (maxLength == 0) {
                            maxLength = 50;
                        }
                        buffer.append(" , ADD");
                        buffer.append(this.getSQLForMaxSizeCheckConstraint(td.getTableName(), colDef2.getColumnName(), maxLength));
                    }
                }
            }
        }
        return buffer.toString();
    }
    
    private void appendStringForRenameCheckConstraint(final StringBuilder buffer, final String tableName, final String oldConstraintName, final String newConstraintName) {
        buffer.append(" ; ALTER TABLE ");
        buffer.append(this.getDBSpecificTableName(tableName));
        buffer.append(" RENAME CONSTRAINT ");
        buffer.append(oldConstraintName);
        buffer.append(" TO ");
        buffer.append(newConstraintName);
    }
    
    @Override
    protected String getConstraintName(final String constraintName) {
        return (PostgresSQLGenerator.isAutoQuoteEnabled || (!PostgresSQLGenerator.isAutoQuoteEnabled && this.isKeyWord(constraintName))) ? ("\"" + constraintName + "\"") : constraintName;
    }
    
    protected String getMaxSizeCheckConstraintName(final String tableName, final String columnName) {
        final String columnHashcode = String.valueOf(columnName.hashCode()).replace("-", "");
        final String checkConstraintName = tableName + "_" + columnHashcode + "_C";
        return (checkConstraintName.trim().contains(" ") && (PostgresSQLGenerator.isAutoQuoteEnabled || this.isKeyWord(tableName) || this.isKeyWord(columnName))) ? ("\"" + checkConstraintName + "\"") : checkConstraintName;
    }
    
    protected String getSQLForMaxSizeCheckConstraint(final String tableName, final String columnName, final int maxSize) {
        final StringBuilder sb = new StringBuilder();
        sb.append(" CONSTRAINT ");
        sb.append(this.getMaxSizeCheckConstraintName(tableName, columnName));
        sb.append(" CHECK (LENGTH(");
        sb.append(this.getDBSpecificColumnName(columnName));
        sb.append(") <= ");
        sb.append(maxSize);
        sb.append(")");
        return sb.toString();
    }
    
    @Override
    protected String getSimpleDateFormatStringFor(final int type) throws QueryConstructionException {
        switch (type) {
            case 91: {
                return "yyyy-MM-dd";
            }
            case 92: {
                return "HH:mm:ss.SSS";
            }
            case 93: {
                return "yyyy-MM-dd HH:mm:ss.SSS";
            }
            default: {
                throw new QueryConstructionException("Unknow type [" + type + "] received here");
            }
        }
    }
    
    @Override
    protected void handleCaseSensitive(final Function function, final StringBuilder colBuffer, final boolean caseSensitive) {
        if ((function.getType() == 12 || function.getType() == 1) && caseSensitive) {
            colBuffer.append(" ::TEXT");
        }
    }
    
    @Override
    protected String getValueString(String valueStr, final Column column, final int comparator, final boolean hasWildCard, final boolean isCaseSensitive) throws QueryConstructionException {
        final int type = column.getType();
        final String retValue = "'" + this.escapeSpecialCharacters(valueStr, type) + "'";
        final String dataType = column.getDataType();
        if (dataType != null && DataTypeUtil.isUDT(dataType)) {
            return DataTypeManager.getDataTypeDefinition(dataType).getDTSQLGenerator(this.getDBType()).handleCaseSensitive(retValue, isCaseSensitive, column.isEncrypted(), Clause.WHERE);
        }
        if (this.isNumeric(type) && (comparator == 2 || comparator == 3)) {
            final StringBuilder retVal = new StringBuilder();
            retVal.append("'").append(valueStr).append("'");
            if (type == 3 || type == 6 || type == 8) {
                final char decimalPt = this.getDecimalSeparator();
                valueStr = valueStr.replace(decimalPt, '.');
            }
            return super.getValueString(valueStr, column, comparator, hasWildCard, isCaseSensitive);
        }
        if ((type == 1 || type == 12 || type == 2004 || comparator == 2 || comparator == 3) && (comparator == 0 || comparator == 1 || comparator == 14 || comparator == 15 || comparator == 8 || comparator == 9 || comparator == 4 || comparator == 5 || comparator == 6 || comparator == 7)) {
            if (isCaseSensitive && !column.isEncrypted()) {
                return "'" + this.escapeSpecialCharacters(valueStr, type) + "'::TEXT";
            }
            if ((column.isEncrypted() || (!column.isEncrypted() && type == 2004)) && !isCaseSensitive) {
                return "UPPER('" + this.escapeSpecialCharacters(valueStr, type) + "')";
            }
            return "'" + this.escapeSpecialCharacters(valueStr, type) + "'";
        }
        else {
            if (comparator == 2 || comparator == 3) {
                valueStr = this.escapeSpecialCharacters(valueStr, type);
                valueStr = "'" + valueStr + "'";
                if (!isCaseSensitive && type != 2004 && (column.getDataType() == null || (!column.getDataType().equals("CHAR") && !column.getDataType().equals("NCHAR")))) {
                    valueStr = "UPPER(" + valueStr + ")";
                }
                return valueStr;
            }
            return super.getValueString(valueStr, column, comparator, hasWildCard, isCaseSensitive);
        }
    }
    
    @Override
    protected String getValue(final Object value, final boolean isCaseSensitive) {
        return value.toString();
    }
    
    @Override
    protected String getValueStringForBoolean(final String valueStr) throws QueryConstructionException {
        final String vStr = valueStr.trim();
        if ("true".equalsIgnoreCase(vStr) || "1".equals(vStr) || "t".equalsIgnoreCase(vStr)) {
            return "true";
        }
        if ("false".equalsIgnoreCase(vStr) || "0".equals(vStr) || "f".equalsIgnoreCase(vStr)) {
            return "false";
        }
        throw new QueryConstructionException("Improper value [" + valueStr + "] set for BOOLEAN column", 1);
    }
    
    @Override
    protected String lhsOfCriterion(final Column column, final boolean caseSensitive, final int comparator, final List<Table> derivedTableList) throws QueryConstructionException {
        String retVal = super.lhsOfCriterion(column, caseSensitive, comparator, derivedTableList);
        final int type = column.getType();
        if (((this.isDate(type) || this.isNumeric(type)) && this.isWildCardSupportedComparator(comparator)) || (type == 6 && (comparator == 0 || comparator == 1))) {
            return this.stringFunction(column, null, caseSensitive);
        }
        if (column.getType() == 2004 && !this.isWildCardSupportedComparator(comparator)) {
            retVal = "convert_from(" + retVal + ", pg_client_encoding())";
        }
        return retVal;
    }
    
    @Override
    protected String rhsOfCriterion(final Column column, int comparator, final Object value, final boolean isCaseSensitive, final String lhString, final List<Table> derivedTableList) throws QueryConstructionException {
        final StringBuilder rhsBuffer = new StringBuilder();
        boolean convertToString = false;
        if (column.getType() == 6 && (comparator == 0 || comparator == 1)) {
            comparator = ((comparator == 0) ? 2 : 3);
            convertToString = true;
        }
        final String comparatorString = this.getComparatorString(column, comparator, true);
        if (value == QueryConstants.PREPARED_STMT_CONST) {
            rhsBuffer.append(comparatorString);
            if (convertToString && value instanceof Column) {
                rhsBuffer.append(this.stringFunction(column, this.getDBSpecificDecryptionString(column, "?"), isCaseSensitive));
            }
            else {
                rhsBuffer.append(this.getDBSpecificDecryptionString(column, "?"));
            }
        }
        else {
            final int type = column.getType();
            final boolean isExpression = column instanceof Function || column instanceof Operation;
            final String valueStr = this.getValue(value, column, comparator, isCaseSensitive && !isExpression, lhString, derivedTableList, Clause.WHERE);
            if (!valueStr.trim().equals("IS NULL") && !valueStr.trim().equals("IS NOT NULL")) {
                rhsBuffer.append(comparatorString);
            }
            if (convertToString && value instanceof Column) {
                rhsBuffer.append(this.stringFunction(column, valueStr, isCaseSensitive));
            }
            else {
                rhsBuffer.append(valueStr);
            }
        }
        return rhsBuffer.toString();
    }
    
    @Override
    protected String appendEscapeString(final String valueStr, final Column column, final int comparator) {
        if (valueStr.indexOf(this.getEscapeStr()) != -1 && column.getType() != 2004 && comparator != 2 && comparator != 3) {
            final String escapeStr = this.getEscapeStringForSlash();
            if (escapeStr != null) {
                return valueStr + escapeStr;
            }
        }
        return valueStr;
    }
    
    @Override
    protected String escapeDBWildCard(String valueStr, final int type, final int comparator) throws QueryConstructionException {
        if (!this.isWildCardSupportedForNumbers() && this.isNumeric(type)) {
            throw new QueryConstructionException("Wild card characters are not  allowed in numeric fields:" + valueStr);
        }
        if (type == 2004 && (comparator == 2 || comparator == 3)) {
            valueStr = valueStr.replace(String.valueOf(this.getWildCardForChar()), String.valueOf(this.getEscapeCharacter()) + String.valueOf(this.getEscapeCharacter()) + this.getWildCardForChar());
        }
        else {
            valueStr = valueStr.replace(String.valueOf(this.getWildCardForChar()), String.valueOf(this.getEscapeCharacter()) + this.getWildCardForChar());
        }
        valueStr = valueStr.replace(String.valueOf(this.getWildCardForString()), String.valueOf(this.getEscapeCharacter()) + this.getWildCardForString());
        return valueStr;
    }
    
    private String stringFunction(final Column column, final String sqlString, final boolean caseSensitive) throws QueryConstructionException {
        final int type = column.getType();
        if (this.isDate(type) || this.isNumeric(type)) {
            final ColumnDefinition colDef = column.getDefinition();
            final StringBuilder strBuff = new StringBuilder();
            int maxLength = 0;
            int precision = 0;
            strBuff.append("TO_CHAR(");
            if (sqlString == null) {
                strBuff.append(this.processSimpleColumn(column, caseSensitive, null, Clause.WHERE));
            }
            else {
                strBuff.append(sqlString);
            }
            strBuff.append(",'");
            if (this.isNumeric(type) && colDef != null) {
                strBuff.append("FM");
                maxLength = colDef.getMaxLength();
                precision = colDef.getPrecision();
            }
            if (maxLength == 0) {
                strBuff.append(this.getGeneratedPattern(type));
            }
            else {
                strBuff.append(this.generatePattern(maxLength, precision));
            }
            strBuff.append("')");
            return strBuff.toString();
        }
        throw new QueryConstructionException("String function not supported for other than Numeric/Date");
    }
    
    private String generatePattern(final int max, final int precision) {
        final StringBuilder strBuff = new StringBuilder();
        for (int i = 0; i < max; ++i) {
            strBuff.append("9");
        }
        if (precision == 0) {
            return strBuff.toString();
        }
        strBuff.append(".");
        for (int i = 0; i < precision; ++i) {
            strBuff.append("9");
        }
        return strBuff.toString();
    }
    
    private String getGeneratedPattern(final int type) throws QueryConstructionException {
        switch (type) {
            case 4: {
                return "9999999999";
            }
            case -6: {
                return "99999";
            }
            case -5: {
                return "9999999999999999999";
            }
            case 3:
            case 6:
            case 8: {
                return "999999999999999.9999";
            }
            case 91: {
                return "YYYY-MM-DD";
            }
            case 92: {
                return "HH24:MI:SS.MS";
            }
            case 93: {
                return "YYYY-MM-DD HH24:MI:SS.MS";
            }
            default: {
                PostgresSQLGenerator.OUT.log(Level.WARNING, "No pattern available for given datatype");
                throw new QueryConstructionException("Template pattern not available for given DataType");
            }
        }
    }
    
    private char getDecimalSeparator() {
        final NumberFormat format = NumberFormat.getInstance();
        final char seperator = ((DecimalFormat)format).getDecimalFormatSymbols().getDecimalSeparator();
        return seperator;
    }
    
    private boolean isDate(final int type) {
        return type == 91 || type == 92 || type == 93;
    }
    
    @Override
    protected void appendColumnAttributes(final int operation, final ColumnDefinition colDef, final StringBuilder buffer) throws QueryConstructionException {
        if (operation == 1) {
            super.appendColumnAttributes(operation, colDef, buffer);
        }
        else if (operation == 2 || operation == 21) {
            buffer.append(", ALTER ");
            if (colDef.getDefaultValue() != null) {
                buffer.append(this.getDBSpecificColumnName(colDef.getColumnName()));
                buffer.append(" SET DEFAULT ");
                buffer.append(this.getDefaultValue(colDef.getDataType(), colDef.getDefaultValue()));
            }
            else {
                buffer.append(this.getDBSpecificColumnName(colDef.getColumnName()));
                buffer.append(" DROP DEFAULT ");
            }
            buffer.append(", ALTER ");
            if (colDef.isNullable() && !colDef.isKey()) {
                buffer.append(this.getDBSpecificColumnName(colDef.getColumnName()));
                buffer.append(" DROP NOT NULL ");
            }
            else {
                buffer.append(this.getDBSpecificColumnName(colDef.getColumnName()));
                buffer.append(" SET NOT NULL ");
            }
        }
    }
    
    private void appendStringForFKIndex(final StringBuilder buffer, final String tableName, final IndexDefinition indexDef, final int operation, final boolean isRevert) throws QueryConstructionException {
        if (operation == 6 || (operation == 7 && isRevert)) {
            buffer.append(this.getSQLForIndex(tableName, indexDef));
        }
        else if (operation == 7 || (operation == 6 && isRevert)) {
            buffer.append(this.getSQLForDropIndex(tableName, indexDef.getName()));
        }
    }
    
    void setEncryptionAlgorithm(final String algoName) {
        PostgresSQLGenerator.OUT.log(Level.FINE, "Encryption algorithm used :: [{0}]", algoName);
        this.encryptionAlgo = algoName;
    }
    
    String getEncryptionAlgorithm() {
        return this.encryptionAlgo;
    }
    
    void setEncryptionS2kMode(final String mode) {
        PostgresSQLGenerator.OUT.log(Level.FINE, "Encryption s2k-mode used :: [{0}]", mode);
        this.encryptionS2kMode = mode;
    }
    
    String getEncryptionS2kMode() {
        return this.encryptionS2kMode;
    }
    
    @Override
    public String getSQLForDropIndex(final String tableName, final String indexName) throws QueryConstructionException {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("DROP INDEX ");
        buffer.append(this.getConstraintName(indexName));
        return buffer.toString();
    }
    
    @Override
    protected void appendSeperatorForAlterOperation(final StringBuilder buffer, final AlterOperation prevOperation, final AlterOperation ao) throws QueryConstructionException {
        final int operation = ao.getOperationType();
        if (buffer.length() == 0 && operation != 10 && operation != 11 && operation != 16) {
            final String tableName = ao.getTableName();
            buffer.append("ALTER TABLE ");
            buffer.append(this.getDBSpecificTableName(tableName));
            buffer.append(" ");
        }
        else if (operation != 10 && operation != 11 && operation != 16) {
            buffer.append(", ");
        }
        else if (buffer.length() != 0) {
            return;
        }
    }
    
    protected void appendStringForDataTypeConvert(final ColumnDefinition newColDef, final StringBuilder sb) throws QueryConstructionException {
        try {
            final String tableName = newColDef.getTableName();
            final String columnName = newColDef.getColumnName();
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            if (td == null || td.getColumnDefinitionByName(columnName) == null) {
                return;
            }
            final ColumnDefinition oldColDef = td.getColumnDefinitionByName(columnName);
            final String newDataType = newColDef.getDataType();
            final String oldDataType = oldColDef.getDataType();
            final boolean isAlreadyEncryptedCol = this.isCurrentlyEncrypted(tableName, columnName);
            final Column column = new Column(tableName, columnName);
            if (newColDef.isEncryptedColumn() && !isAlreadyEncryptedCol) {
                column.setDefinition(newColDef);
                sb.append(" USING ");
                String dbSpecificColumnName = this.getDBSpecificColumnName(newColDef.getColumnName());
                if (newColDef.getDataType().equals("SBLOB")) {
                    dbSpecificColumnName += "::BYTEA";
                }
                sb.append(this.getDBSpecificEncryptionString(column, dbSpecificColumnName));
                return;
            }
            if (!newColDef.isEncryptedColumn() && isAlreadyEncryptedCol) {
                column.setDefinition(oldColDef);
                sb.append(" USING ");
                sb.append(this.getDBSpecificDecryptionString(column, this.getDBSpecificColumnName(newColDef.getColumnName())));
                if (newColDef.getDataType().equals("BLOB")) {
                    sb.append(":: BYTEA");
                }
                return;
            }
            if (!oldDataType.equals(newDataType) && !oldDataType.equals("SCHAR") && !oldDataType.equals("SBLOB") && newDataType.equals("BLOB")) {
                sb.append(" USING ");
                sb.append(this.getDBSpecificColumnName(columnName));
                sb.append("::BYTEA");
            }
        }
        catch (final MetaDataException ex) {
            throw new QueryConstructionException("Exception occured during appendStringForDataEncryption", ex);
        }
    }
    
    @Override
    protected void appendSelectQuery(final StringBuilder buff, final ArchiveTable arcTable, final SelectQuery query, final boolean isPush) throws QueryConstructionException {
        if (query == null) {
            buff.append(" ( ");
            buff.append("TABLE ");
            buff.append(this.getDBSpecificTableName(isPush ? arcTable.getTableName() : arcTable.getArchiveTableName()));
            buff.append(" )");
            buff.append(" WITH NO DATA ");
        }
        else {
            super.appendSelectQuery(buff, arcTable, query, isPush);
        }
    }
    
    @Override
    protected void processColumn(final Column column, final StringBuilder columnBuffer, final boolean caseSensitive, final List<Table> derivedTableList, final Clause columnBelongsToClause) throws QueryConstructionException {
        if (!PostgresSQLGenerator.isAutoQuoteEnabled) {
            final String processColString = this.getDBSpecificColumnString(derivedTableList, column);
            if (processColString != null) {
                columnBuffer.append(this.handleCaseSensitive(this.getDBSpecificDecryptionString(column, processColString), caseSensitive, column.getType(), column.isEncrypted(), columnBelongsToClause));
                return;
            }
        }
        super.processColumn(column, columnBuffer, caseSensitive, derivedTableList, columnBelongsToClause);
    }
    
    @Override
    protected String processSimpleColumn(Column column, final boolean caseSensitive, final List<Table> derivedTableList, final Clause columnBelongsToClause) {
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
        final String tableName = column.getTableAlias();
        if (tableName != null) {
            String retVal = this.getDBSpecificColumnString(derivedTableList, column);
            if (retVal != null) {
                retVal = this.getDBSpecificDecryptionString(column, retVal);
                return this.handleCaseSensitive(retVal, caseSensitive, column.getType(), column.isEncrypted(), columnBelongsToClause);
            }
        }
        return super.processSimpleColumn(column, caseSensitive, derivedTableList, columnBelongsToClause);
    }
    
    @Override
    protected String handleCaseSensitive(final String retVal, final boolean isCaseSensitive, final int type, final boolean isEncrypted, final Clause columnBelongsToClause) {
        if (DataTypeUtil.isUDT(type)) {
            return DataTypeManager.getDataTypeDefinition(type).getDTSQLGenerator(this.getDBType()).handleCaseSensitive(retVal, isCaseSensitive, isEncrypted, columnBelongsToClause);
        }
        if ((columnBelongsToClause == Clause.WHERE || columnBelongsToClause == Clause.SELECT) && isCaseSensitive && (type == 12 || type == 1) && !isEncrypted) {
            return retVal + "::TEXT";
        }
        if (columnBelongsToClause == Clause.WHERE && !isCaseSensitive && isEncrypted && type != 2004) {
            return "UPPER(" + retVal + ")";
        }
        return retVal;
    }
    
    private String getDBSpecificColumnString(final List<Table> derivedTableList, final Column column) {
        if (!PostgresSQLGenerator.isAutoQuoteEnabled && this.isAliasedColumnOfDerivedTable(derivedTableList, column.getTableAlias(), column.getColumnName())) {
            final String columnName = column.getColumnName();
            return this.getDBSpecificTableName(column.getTableAlias()) + "." + this.getDBSpecificColumnAlias(columnName);
        }
        return null;
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
    
    @Override
    protected String getDBSpecificColumnAlias(String columnAlias) {
        columnAlias = columnAlias.replaceAll("\"", "\"\"");
        return "\"" + columnAlias + "\"";
    }
    
    @Override
    protected String getNullsSortOrder(final boolean isascending, final Boolean isNullsFirst) {
        if (isNullsFirst == null) {
            return null;
        }
        if (isascending == isNullsFirst) {
            return isNullsFirst ? "NULLS FIRST" : "NULLS LAST";
        }
        return null;
    }
    
    @Override
    protected StringBuilder appendNulls(final Column col, final List<Table> derivedTableList, final StringBuilder orderByBuffer, final String nullsSortOrder) throws QueryConstructionException {
        final DerivedTable dertable = (DerivedTable)this.getDerivedTable(derivedTableList, col.getTableAlias());
        if (dertable != null) {
            final SelectQuery sq = (SelectQuery)dertable.getSubQuery();
            final List<Column> columnList = sq.getSelectColumns();
            Column sortColumn = null;
            for (final Column c : columnList) {
                if (c.getColumnAlias().equals(col.getColumnName())) {
                    sortColumn = (Column)c.clone();
                    break;
                }
            }
            if (sortColumn == null) {
                throw new QueryConstructionException("Sort Column is not selected in the inner query");
            }
            this.appendNulls(sortColumn, sq.getDerivedTables(), orderByBuffer, nullsSortOrder);
        }
        else if (col.getDefinition() != null && (!col.getDefinition().isNullable() || col.getDefinition().isKey())) {
            orderByBuffer.append(" ");
        }
        else {
            orderByBuffer.append(" ").append(nullsSortOrder).append(" ");
        }
        return orderByBuffer;
    }
    
    public String getSQLForGetCitextColumnLength() {
        return "select t.relname as tablename, c.attname as columnname, cons.conname as constraintname, cons.consrc as constraintstring, ltrim(rtrim(split_part(cons.consrc,'<=', 2), ')'),' ') as maxsize from pg_class t inner join pg_attribute c on t.oid = c.attrelid inner join pg_constraint cons on c.attnum = ANY(cons.conkey) and t.oid = cons.conrelid where cons.contype = 'c' and t.relname ilike ?;";
    }
    
    @Override
    public String createTempTableSQL(final String tableName) {
        return "create temporary table " + tableName + "_temp (like " + tableName + ")";
    }
    
    @Override
    public String insertSQLForTemp(final String tableName) {
        return "insert into " + tableName + " (select * from " + tableName + "_temp)";
    }
    
    @Override
    public String getBulkSql(final BulkLoad bulk, final BulkInsertObject bio) throws MetaDataException, SQLException, QueryConstructionException {
        final StringBuilder sql = new StringBuilder("");
        sql.append("COPY ");
        sql.append(this.getDBSpecificTableName(bulk.isArchivedTable() ? bulk.getArchivedTableName() : bulk.getTableName()));
        if (bulk.createTempTable) {
            sql.append("_temp");
        }
        sql.append(" (");
        String columnDataType = null;
        int skipCount = 0;
        for (int i = 0; i < bio.getColNames().size(); ++i) {
            columnDataType = bio.getColTypeNames().get(i);
            if (DataTypeUtil.isEDT(columnDataType)) {
                columnDataType = DataTypeManager.getDataTypeDefinition(columnDataType).getBaseType();
            }
            if (DataTypeUtil.isUDT(columnDataType) && !DataTypeManager.getDataTypeDefinition(columnDataType).getMeta().processInput()) {
                ++skipCount;
            }
            else {
                sql.append(this.getDBSpecificColumnName(bio.getColNames().get(i)));
                if (i < bio.getColNames().size() - skipCount) {
                    sql.append(",");
                }
            }
        }
        if (sql.toString().endsWith(",")) {
            sql.deleteCharAt(sql.toString().lastIndexOf(","));
        }
        sql.append(") FROM STDIN DELIMITER E'\\t'");
        return sql.toString();
    }
    
    @Override
    public String formBulkUpdateSql(final BulkLoad bulk) throws MetaDataException {
        final StringBuilder updateSql = new StringBuilder("");
        int i = 0;
        try {
            if (MetaDataUtil.getTableDefinitionByName(bulk.getTableName()) != null) {
                for (String colName : bulk.getBulkInsertObject().getColNames()) {
                    if (MetaDataUtil.getTableDefinitionByName(bulk.getTableName()).getColumnDefinitionByName(colName) != null && MetaDataUtil.getTableDefinitionByName(bulk.getTableName()).getColumnDefinitionByName(colName).isEncryptedColumn()) {
                        updateSql.append(" UPDATE ");
                        updateSql.append(this.getDBSpecificTableName(bulk.isArchivedTable() ? bulk.getArchivedTableName() : bulk.getTableName()));
                        if (bulk.createTempTable) {
                            updateSql.append("_temp");
                        }
                        updateSql.append(" SET ");
                        updateSql.append(this.getDBSpecificColumnName(colName));
                        updateSql.append(" = ");
                        final Column column = Column.getColumn(bulk.getTableName(), colName);
                        final ColumnDefinition colDef = MetaDataUtil.getTableDefinitionByName(bulk.getTableName()).getColumnDefinitionByName(colName);
                        String dataType = bulk.getBulkInsertObject().getColTypeNames().get(i);
                        if (DataTypeUtil.isEDT(dataType)) {
                            dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
                        }
                        if (dataType.equalsIgnoreCase("SCHAR")) {
                            colDef.setDataType("SCHAR");
                            colName = "convert_from(" + colName + ",'utf8')";
                        }
                        column.setDefinition(colDef);
                        final String encryptStr = this.getDBSpecificEncryptionString(column, colName);
                        updateSql.append(encryptStr);
                        updateSql.append("; ");
                    }
                    ++i;
                }
            }
        }
        catch (final MetaDataException e) {
            PostgresSQLGenerator.OUT.log(Level.INFO, "Meta Data Exception ::", e);
            throw e;
        }
        return updateSql.toString();
    }
    
    @Override
    protected void appendDeleteJoinAndCriteria(final Join join, final Criteria criteria, final StringBuilder deleteBuffer) throws QueryConstructionException {
        try {
            if (join != null) {
                final SelectQuery sQuery = new SelectQueryImpl(Table.getTable(join.getBaseTableName(), join.getBaseTableAlias()));
                final List<String> columnList = MetaDataUtil.getTableDefinitionByName(join.getBaseTableName()).getPrimaryKey().getColumnList();
                Criteria cri = null;
                for (final String columnName : columnList) {
                    final Column column = Column.getColumn(join.getBaseTableName(), columnName);
                    sQuery.addSelectColumn(column);
                    cri = ((cri == null) ? new Criteria(column, Column.getColumn("sq", columnName), 0) : cri.and(column, Column.getColumn("sq", columnName), 0));
                }
                sQuery.setCriteria(criteria);
                sQuery.addJoin(join);
                deleteBuffer.append(" USING (");
                deleteBuffer.append(this.getSQLForSelect(sQuery));
                deleteBuffer.append(") AS ").append(this.getDBSpecificTableName(" sq "));
                final String whereClause = this.formWhereClause(cri, false, null);
                if (whereClause != null) {
                    deleteBuffer.append(" WHERE ");
                    deleteBuffer.append(whereClause);
                }
            }
            else {
                final String whereClause2 = this.formWhereClause(criteria, false, null);
                if (whereClause2 != null) {
                    deleteBuffer.append(" WHERE ");
                    deleteBuffer.append(whereClause2);
                }
            }
        }
        catch (final MetaDataException e) {
            throw new QueryConstructionException("Exception while fetching metadata for table :: " + join.getBaseTableName());
        }
    }
    
    void setAutoQuote(final boolean enable) {
        PostgresSQLGenerator.isAutoQuoteEnabled = enable;
    }
    
    void addReservedKeyWords(final Set<String> keyWords) {
        this.reservedKeyWords.addAll(keyWords);
    }
    
    @Override
    public String getSchemaQuery() {
        return "SELECT CURRENT_SCHEMA()";
    }
    
    @Override
    public String getSQLWithHiddenEncryptionKey(String query) {
        final String key = String.valueOf(this.getKey());
        int index = query.indexOf("pgp_sym_encrypt");
        if (index == -1) {
            index = query.indexOf("pgp_sym_decrypt");
        }
        if (index != -1) {
            index = query.indexOf('\'' + key + '\'', index);
            if (index != -1) {
                query = query.replace('\'' + key + '\'', '\'' + new String(new char[key.length()]).replaceAll("\u0000", "*") + '\'');
            }
        }
        return query;
    }
    
    @Override
    public String getSQLForIndex(final String tableName, final IndexDefinition iDef) throws QueryConstructionException {
        final StringJoiner indexString = new StringJoiner(" ");
        final StringJoiner columnString = new StringJoiner(",", "(", ")");
        indexString.add("CREATE INDEX").add(this.getConstraintName(iDef.getName())).add("ON").add(this.getDBSpecificTableName(tableName));
        String indexProps = null;
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
            indexProps = this.getIndexPropString(icd.isAscending(), icd.isNullsFirst());
            if (td != null && dcSqlGenerator != null) {
                columnString.add(String.join("", "(", dcSqlGenerator.getDCSpecificColumnName(td.getTableName(), icd.getColumnName()), ")", indexProps));
            }
            else {
                columnString.add(this.getDBSpecificColumnName(icd.getColumnName()) + indexProps);
            }
        }
        indexString.add(columnString.toString());
        return indexString.toString();
    }
    
    private String getIndexPropString(final Boolean ascending, final Boolean nullsFirst) {
        final StringJoiner indexProps = new StringJoiner(" ", " ", " ");
        if (!ascending) {
            indexProps.add("desc");
        }
        if (nullsFirst == null) {
            indexProps.add("");
        }
        else if (ascending && nullsFirst) {
            indexProps.add("nulls first");
        }
        else if (!ascending && !nullsFirst) {
            indexProps.add("nulls last");
        }
        return indexProps.toString();
    }
    
    @Override
    public String getSQLForCreateTable(final TableDefinition tabDefn, final String createTableOptions) throws QueryConstructionException {
        final String tableName = tabDefn.getTableName();
        List colDefnList = new ArrayList();
        if (tabDefn.getPhysicalColumns() != null && !tabDefn.getPhysicalColumns().isEmpty()) {
            for (final Object cd : tabDefn.getColumnList()) {
                if (!((ColumnDefinition)cd).isDynamic()) {
                    colDefnList.add(cd);
                }
            }
        }
        else {
            colDefnList = tabDefn.getColumnList();
        }
        final List uniqueCols = new ArrayList();
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
        PostgresSQLGenerator.OUT.log(Level.FINEST, "The create table string is {0} ", toReturn);
        return toReturn;
    }
    
    String maskSensitiveMessages(final String message) {
        if (message == null) {
            return null;
        }
        return message.replaceAll("pgp_sym_[^(]*\\(('[^']*'|[^,]*),('[^']*')(,('[^']*'))?\\)", "******************");
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
    
    @Override
    protected String formSelectClause(final List columnsList, final List caseSensitiveColumns, final SelectQuery sq) throws QueryConstructionException {
        if (columnsList == null || columnsList.size() == 0) {
            throw new QueryConstructionException("Select columns not given");
        }
        final StringBuilder selectClause = new StringBuilder(250);
        for (int columnsLength = columnsList.size(), i = 0; i < columnsLength; ++i) {
            final int checkLength = i;
            final Column column = columnsList.get(i);
            if (sq.getLockStatus() && column.getFunction() != 0) {
                throw new QueryConstructionException("SELECT FOR UPDATE cannot be given for select query having aggregate function");
            }
            final StringBuilder colBuffer = new StringBuilder(40);
            this.processColumn(column, colBuffer, caseSensitiveColumns.contains(column), (sq == null) ? null : sq.getDerivedTables(), (sq != null && ((SelectQueryImpl)sq).isSubQuery()) ? Clause.WHERE : Clause.SELECT);
            selectClause.append(colBuffer.toString());
            this.getAliasedColumn(column, selectClause);
            if (checkLength + 1 < columnsLength) {
                selectClause.append(", ");
            }
        }
        return selectClause.toString();
    }
    
    @Override
    protected String getSQL(final SelectQuery query, final String selectClause, String whereClause, String orderByClause) throws QueryConstructionException {
        final StringBuilder selectBuffer = new StringBuilder(500);
        selectBuffer.append("SELECT ");
        if (query.isDistinct()) {
            if (query.getLockStatus()) {
                throw new QueryConstructionException("SELECT FOR UPDATE cannot be given for select query having DISTINCT keyword");
            }
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
            if (query.getLockStatus()) {
                throw new QueryConstructionException("SELECT FOR UPDATE cannot be given for select query having GROUP BY clause");
            }
            selectBuffer.append(' ');
            selectBuffer.append(groupByClause);
        }
        orderByClause = ((null != orderByClause) ? orderByClause : this.getOrderByClause(query.getSortColumns(), query.getSelectColumns(), null, null));
        if (orderByClause != null) {
            selectBuffer.append(orderByClause);
        }
        return selectBuffer.toString();
    }
    
    static {
        OUT = Logger.getLogger(PostgresSQLGenerator.class.getName());
    }
}
