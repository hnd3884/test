package com.adventnet.db.adapter.mssql;

import com.adventnet.ds.query.UnionQuery;
import java.util.Set;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.util.QueryUtil;
import java.util.LinkedHashMap;
import com.adventnet.db.adapter.BulkInsertObject;
import com.adventnet.ds.query.BulkLoad;
import com.adventnet.ds.query.ArchiveTable;
import com.adventnet.ds.query.CreateTableLike;
import java.sql.SQLException;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.Function;
import com.adventnet.ds.query.Operation;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.zoho.conf.AppResources;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.ds.query.AlterTableQuery;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.Map;
import java.util.Collections;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.AlterOperation;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.db.adapter.Ansi92SQLGenerator;

public class MssqlSQLGenerator extends Ansi92SQLGenerator
{
    static final Logger OUT;
    protected String collation_string;
    protected String toggle_collation_string;
    protected boolean isDBCaseSensitive;
    protected boolean isDBMigrationRunning;
    private boolean treatCharAsNChar;
    private static boolean isTextDataTypeDeprecated;
    private boolean supportFilteredUniqueIndex;
    private Mssql2005SQLGenerator sql2005Gen;
    private boolean supportsEncryption;
    private String certificateName;
    private String symmKeyName;
    private String masterkeyPass;
    private String symmkeyAlgo;
    private String symmkeyAlgo_alt;
    private String certificateSub;
    private String identityValue;
    private String keySource;
    private boolean rowGuidEnable;
    
    void treatCharAsNChar() {
        this.treatCharAsNChar = true;
    }
    
    public boolean isCharTreatedAsNChar() {
        return this.treatCharAsNChar;
    }
    
    public void setCollation(final String collationStr) {
        this.collation_string = " COLLATE " + collationStr;
        MssqlSQLGenerator.OUT.log(Level.FINE, "collationString :: [{0}]", this.collation_string);
    }
    
    public void setIsDBCaseSensitive(final boolean isDBCaseSensitive) {
        this.isDBCaseSensitive = isDBCaseSensitive;
        MssqlSQLGenerator.OUT.log(Level.FINE, "isDBCaseSensitive :: [{0}]", this.isDBCaseSensitive);
    }
    
    public void setToggleCollation(final String toggleCollation) {
        this.toggle_collation_string = " COLLATE " + toggleCollation;
        MssqlSQLGenerator.OUT.log(Level.FINE, "toggle collation_string :: [{0}]", this.toggle_collation_string);
    }
    
    public MssqlSQLGenerator() {
        this.collation_string = " COLLATE SQL_Latin1_General_CP1_CI_AS";
        this.toggle_collation_string = "COLLATE SQL_Latin1_General_CP1_CS_AS";
        this.isDBCaseSensitive = true;
        this.isDBMigrationRunning = false;
        this.treatCharAsNChar = false;
        this.sql2005Gen = new Mssql2005SQLGenerator();
        this.supportsEncryption = false;
        this.certificateName = null;
        this.symmKeyName = null;
        this.masterkeyPass = null;
        this.symmkeyAlgo = null;
        this.symmkeyAlgo_alt = null;
        this.certificateSub = null;
        this.identityValue = null;
        this.keySource = null;
        this.rowGuidEnable = false;
        this.dbType = "mssql";
    }
    
    @Override
    public String getSQLForLock(final List tableList) throws QueryConstructionException {
        throw new UnsupportedOperationException("MssqlSQLGenerator: Lock SQL generation not supported for multiple tables");
    }
    
    @Override
    public String getSQLForLock(final String tableName) throws QueryConstructionException {
        throw new UnsupportedOperationException("FireBirdSQLGenerator: Lock SQL generation not supported in MsSQL");
    }
    
    private void throwIfInvalidMaxLength(final ColumnDefinition colDef) throws QueryConstructionException {
        final boolean keyOrUnique = colDef.isKey() || colDef.isUnique();
        final String dataType = colDef.getDataType();
        if (keyOrUnique && colDef.getMaxLength() > 4000 && (dataType.equals("CHAR") || dataType.equals("NCHAR") || dataType.equals("SCHAR"))) {
            throw new QueryConstructionException("Key or Unique Column size can not be more than 4000 characters");
        }
    }
    
    private String getSCHARDataTypeStr(final int maxLength, final boolean supportsEncryption) throws QueryConstructionException {
        if (supportsEncryption) {
            return "VARBINARY(MAX)";
        }
        if (maxLength > 4000) {
            return "TEXT";
        }
        return (maxLength == 0) ? "VARCHAR(50)" : ("VARCHAR(" + maxLength + ")");
    }
    
    private String getCHARDataTypeStr(final int maxLength) throws QueryConstructionException {
        if (maxLength <= 4000 && maxLength != -1) {
            return (maxLength == 0) ? "VARCHAR(50)" : ("VARCHAR(" + maxLength + ")");
        }
        if (MssqlSQLGenerator.isTextDataTypeDeprecated) {
            return "VARCHAR(MAX)";
        }
        return "TEXT";
    }
    
    private String getNCHARDataTypeStr(final int maxLength) throws QueryConstructionException {
        if (maxLength <= 4000 && maxLength != -1) {
            return (maxLength == 0) ? "NVARCHAR(50)" : ("NVARCHAR(" + maxLength + ")");
        }
        if (MssqlSQLGenerator.isTextDataTypeDeprecated) {
            return "NVARCHAR(MAX)";
        }
        return "NTEXT";
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
        if (dataType.equals("CHAR") || dataType.equals("NCHAR") || dataType.equals("SCHAR")) {
            this.throwIfInvalidMaxLength(colDef);
        }
        if ("CHAR".equals(dataType) && !this.treatCharAsNChar) {
            return this.getCHARDataTypeStr(maxLength);
        }
        if ("SCHAR".equals(dataType)) {
            return this.getSCHARDataTypeStr(maxLength, this.supportsEncryption);
        }
        if ("NCHAR".equals(dataType) || ("CHAR".equals(dataType) && this.treatCharAsNChar)) {
            return this.getNCHARDataTypeStr(maxLength);
        }
        if (dataType.equals("INTEGER")) {
            dataTypeStr = "INT";
        }
        else if (dataType.equals("BIGINT")) {
            dataTypeStr = "BIGINT";
        }
        else if (dataType.equals("TINYINT")) {
            dataTypeStr = "TINYINT";
        }
        else if (dataType.equals("FLOAT")) {
            if (maxLength == 0) {
                maxLength = 14;
            }
            dataTypeStr = "FLOAT (" + maxLength + ") ";
        }
        else if (dataType.equals("DOUBLE")) {
            dataTypeStr = "DOUBLE PRECISION";
        }
        else if (dataType.equals("DECIMAL")) {
            dataTypeStr = dataType + "(" + ((maxLength == 0) ? 16 : maxLength) + ", " + ((precision == 0) ? 4 : precision) + ")";
        }
        else if (dataType.equals("BLOB") || dataType.equals("SBLOB")) {
            dataTypeStr = "VARBINARY(MAX)";
        }
        else if (dataType.equals("DATE") || dataType.equals("TIMESTAMP") || dataType.equals("TIME") || dataType.equals("DATETIME")) {
            dataTypeStr = "DATETIME";
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
    
    private String getDefaultConstraintName(final String colName, final String constraintName) {
        if (null != constraintName) {
            return constraintName;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("DF_");
        sb.append((colName.length() > 4) ? colName.substring(0, 4) : colName);
        sb.append("_");
        sb.append(System.currentTimeMillis());
        return sb.toString();
    }
    
    String getAlterColumnQuery(final AlterOperation ao) throws QueryConstructionException {
        final ColumnDefinition colDef = (ColumnDefinition)ao.getAlterObject();
        final String tableName = ao.getTableName();
        final String colName = colDef.getColumnName();
        final String defValConsName = this.getDefaultConstraintName(colName, ao.getDefaultValueConstraintName());
        final StringBuilder query = new StringBuilder();
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException mde) {
            throw new QueryConstructionException("Error fetching oldColDef", mde);
        }
        final ColumnDefinition oldColDef = td.getColumnDefinitionByName(colName);
        final PrimaryKeyDefinition pk = td.getPrimaryKey();
        final boolean isPKColumn = pk.getColumnList().contains(colName);
        if (isPKColumn) {
            colDef.setNullable(false);
            query.append("ALTER TABLE ").append(this.getDBSpecificTableName(tableName)).append(" ");
            query.append("DROP CONSTRAINT ");
            query.append(this.getConstraintName((ao.getActualConstraintName() == null) ? pk.getName() : ao.getActualConstraintName()));
            query.append(';');
        }
        final List<UniqueKeyDefinition> uniqueKeys = new ArrayList<UniqueKeyDefinition>();
        if (td.getUniqueKeys() != null) {
            for (final UniqueKeyDefinition uk : td.getUniqueKeys()) {
                if (uk.getColumns().contains(colName)) {
                    uniqueKeys.add(uk);
                }
            }
        }
        for (final UniqueKeyDefinition uk : uniqueKeys) {
            query.append(this.getSQLForDropIndex(tableName, (ao.getActualConstraintName() == null) ? uk.getName() : ao.getActualConstraintName()));
            query.append(";");
        }
        final List<IndexDefinition> indexes = new ArrayList<IndexDefinition>();
        if (td.getIndexes() != null) {
            for (final IndexDefinition idx : td.getIndexes()) {
                if (idx.getColumns().contains(colName)) {
                    indexes.add(idx);
                }
            }
        }
        for (final IndexDefinition idx : indexes) {
            query.append(this.getSQLForDropIndex(tableName, idx.getName()));
            query.append(";");
        }
        final Object oldDefVal = oldColDef.getDefaultValue();
        if (null != oldDefVal || defValConsName.equals(ao.getDefaultValueConstraintName())) {
            query.append(this.getSQLForDropDefValConstraint(tableName, defValConsName));
            query.append(';');
        }
        query.append("ALTER TABLE ").append(this.getDBSpecificTableName(tableName)).append(" ");
        query.append(" ALTER COLUMN ");
        query.append(this.getDBSpecificColumnName(colName));
        query.append(" ");
        this.appendColumnAttributesForAlter(ao.getOperationType(), colDef, query);
        query.append(" ");
        final Object newDefVal = colDef.getDefaultValue();
        if (null != newDefVal) {
            query.append(';');
            query.append(this.getSQLForAddDefValConstraint(tableName, colName, defValConsName, colDef.getDataType(), newDefVal));
            query.append(' ');
        }
        if (!uniqueKeys.isEmpty()) {
            query.append(" ; ");
        }
        for (final UniqueKeyDefinition uk2 : uniqueKeys) {
            query.append(this.getSQLForCreateUniqueIndex(uk2.getName(), td, uk2, Collections.singletonList(colDef), null));
            query.append(";");
        }
        if (isPKColumn) {
            query.append(';');
            query.append("ALTER TABLE ").append(this.getDBSpecificTableName(tableName)).append(" ");
            query.append("ADD CONSTRAINT ");
            query.append(this.getConstraintName(pk.getName()));
            query.append(" PRIMARY KEY ");
            this.setColumnNamesFromList(pk.getColumnList(), query, null);
            query.append(' ');
        }
        for (final IndexDefinition idx2 : indexes) {
            query.append(this.getSQLForIndex(td.getTableName(), idx2));
            query.append(";");
        }
        return query.toString();
    }
    
    private void appendColumnAttributesForAlter(final int operationType, final ColumnDefinition colDef, final StringBuilder buffer) throws QueryConstructionException {
        buffer.append(this.getDBDataType(colDef));
        if (!colDef.isNullable()) {
            buffer.append(" NOT NULL");
        }
    }
    
    private boolean isNewQueryRequired(final AlterOperation oldAO, final AlterOperation newAO) {
        if (oldAO == null) {
            return true;
        }
        if (this.supportFilteredUniqueIndex && (oldAO.getOperationType() == 4 || oldAO.getOperationType() == 5)) {
            return true;
        }
        switch (newAO.getOperationType()) {
            case 2:
            case 4:
            case 5:
            case 10:
            case 11:
            case 12:
            case 13: {
                return true;
            }
            default: {
                final int oldOpType = oldAO.getOperationType();
                final int newOpType = newAO.getOperationType();
                return (!this.isAddOperation(oldOpType) || !this.isAddOperation(newOpType)) && (!this.isDeleteOperation(oldOpType) || !this.isDeleteOperation(newOpType));
            }
        }
    }
    
    @Override
    public String getIndexName(String name) {
        name = String.valueOf(name.hashCode()).replace("-", "_");
        return "IDX_" + name;
    }
    
    private boolean isAddOperation(final int operationType) {
        switch (operationType) {
            case 1:
            case 4:
            case 6:
            case 9: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isDeleteOperation(final int operationType) {
        switch (operationType) {
            case 3:
            case 5:
            case 7:
            case 8: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void appendAddDropQuery(final AlterOperation ao, final StringBuilder query) {
        final int oType = ao.getOperationType();
        if (this.isAddOperation(oType)) {
            query.append(" ADD ");
        }
        else if (this.isDeleteOperation(oType)) {
            query.append(" DROP ");
        }
    }
    
    @Override
    protected void appendSeperatorForAlterOperation(final StringBuilder query, final AlterOperation prevOperation, final AlterOperation currentOperation) throws QueryConstructionException {
        final boolean isNew = this.isNewQueryRequired(prevOperation, currentOperation);
        if (isNew && 0 != query.length()) {
            query.append(';');
        }
        switch (currentOperation.getOperationType()) {
            case 2:
            case 10:
            case 11:
            case 12:
            case 13:
            case 16: {
                return;
            }
            default: {
                if (this.supportFilteredUniqueIndex && (currentOperation.getOperationType() == 4 || currentOperation.getOperationType() == 5)) {
                    return;
                }
                if (isNew) {
                    final String tableName = currentOperation.getTableName();
                    query.append("ALTER TABLE ").append(this.getDBSpecificTableName(tableName)).append(' ');
                    this.appendAddDropQuery(currentOperation, query);
                }
                else {
                    query.append(',');
                }
            }
        }
    }
    
    protected AlterTableQuery getModifiedATQ(final AlterTableQuery aq) {
        final AlterTableQuery newAlterTableQuery = new AlterTableQueryImpl(aq.getTableName());
        for (final AlterOperation ao : aq.getAlterOperations()) {
            if (ao.getOperationType() == 15) {
                final UniqueKeyDefinition newUK = (UniqueKeyDefinition)ao.getAlterObject();
                final AlterOperation deleteUK = new AlterOperation(aq.getTableName(), 5, newUK.getName());
                newAlterTableQuery.getAlterOperations().add(deleteUK);
                final AlterOperation addUK = new AlterOperation(aq.getTableName(), 4, newUK);
                newAlterTableQuery.getAlterOperations().add(addUK);
            }
            else {
                newAlterTableQuery.getAlterOperations().add(ao);
            }
        }
        return newAlterTableQuery;
    }
    
    @Override
    public String getSQLForAlterTable(AlterTableQuery aq) throws QueryConstructionException {
        aq = this.getModifiedATQ(aq);
        final String tableName = aq.getTableName();
        final StringBuilder alterSQL = new StringBuilder();
        for (final AlterOperation ao : aq.getAlterOperations()) {
            if (ao.getOperationType() == 7) {
                final String fkName = (String)ao.getAlterObject();
                if (!ao.handleIndexForFK()) {
                    continue;
                }
                alterSQL.append(this.getSQLForDropIndex(ao.getTableName(), this.getIndexName(fkName)));
                alterSQL.append(";");
            }
        }
        final boolean isRevertOperation = aq.isRevert();
        AlterOperation prevAlterOperation = null;
        for (final AlterOperation currentOperation : aq.getAlterOperations()) {
            if (this.isPrePostHandlingAlone(currentOperation, aq)) {
                continue;
            }
            this.appendSeperatorForAlterOperation(alterSQL, prevAlterOperation, currentOperation);
            AlterOperation altOper = currentOperation;
            if (isRevertOperation) {
                altOper = this.getAlterOperationForRevert(currentOperation);
            }
            this.appendStringForAlterOperation(altOper, alterSQL);
            prevAlterOperation = currentOperation;
        }
        final List<AlterOperation> alterOperations = aq.getAlterOperations();
        for (final AlterOperation ao2 : alterOperations) {
            final int operation = ao2.getOperationType();
            ForeignKeyDefinition fkDef = null;
            IndexDefinition iDef = null;
            if (operation == 6) {
                fkDef = (ForeignKeyDefinition)ao2.getAlterObject();
                iDef = this.getIndexDefForFK(fkDef);
                if (!ao2.isIgnoreFKIndexCreation() || (ao2.isIgnoreFKIndexCreation() && !this.isDBMigrationRunning)) {
                    MssqlSQLGenerator.OUT.fine("AlterOperation.ignoreFKIndexCreation only supported during DBMigration, hence ignoring this configuration.");
                    alterSQL.append(";");
                    alterSQL.append(this.getSQLForIndex(tableName, iDef));
                }
                alterSQL.append(";");
            }
        }
        return alterSQL.toString();
    }
    
    @Override
    protected void appendColumnAttributes(final int operationType, final ColumnDefinition colDef, final StringBuilder buffer) throws QueryConstructionException {
        super.appendColumnAttributes(operationType, colDef, buffer);
        if (operationType == 1 && colDef.getDefaultValue() != null && colDef.isNullable()) {
            buffer.append(" WITH VALUES");
        }
    }
    
    @Override
    protected void appendStringForAlterOperation(final AlterOperation ao, final StringBuilder query) throws QueryConstructionException {
        final Object alterObject = ao.getAlterObject();
        ColumnDefinition colDef = null;
        final int operationType = ao.getOperationType();
        final String tableName = ao.getTableName();
        switch (operationType) {
            case 10: {
                query.append(this.getSQLForIndex(ao.getTableName(), (IndexDefinition)ao.getAlterObject()));
                break;
            }
            case 11: {
                query.append(this.getSQLForDropIndex(tableName, (ao.getActualConstraintName() == null) ? ((String)ao.getAlterObject()) : ao.getActualConstraintName()));
                break;
            }
            case 12: {
                query.append(this.getRenameColSql(ao));
                break;
            }
            case 13: {
                query.append(this.getRenameTableSql(ao));
                break;
            }
            case 2: {
                if (this.supportFilteredUniqueIndex) {
                    query.append(this.getAlterColumnQuery(ao));
                    break;
                }
                query.append(this.sql2005Gen.getAlterColumnQuery(ao));
                break;
            }
            case 1: {
                colDef = (ColumnDefinition)alterObject;
                query.append(this.getDBSpecificColumnName(colDef.getColumnName()));
                query.append(" ");
                this.appendColumnAttributes(operationType, colDef, query);
                query.append(" ");
                break;
            }
            case 4: {
                final UniqueKeyDefinition ukDef = (UniqueKeyDefinition)alterObject;
                TableDefinition td;
                try {
                    td = MetaDataUtil.getTableDefinitionByName(tableName);
                }
                catch (final MetaDataException e) {
                    throw new QueryConstructionException(e.getMessage(), e);
                }
                if (this.supportFilteredUniqueIndex) {
                    query.append(this.getSQLForCreateUniqueIndex(ukDef.getName(), td, ukDef, null, null));
                    break;
                }
                query.append(" CONSTRAINT ");
                query.append(this.getConstraintName(ukDef.getName()));
                query.append(" UNIQUE ");
                this.setColumnNamesFromList(ukDef.getColumns(), query, null);
                break;
            }
            case 5: {
                final String constraintName = (String)((ao.getActualConstraintName() == null) ? alterObject : ao.getActualConstraintName());
                if (this.supportFilteredUniqueIndex) {
                    query.append(this.getSQLForDropUniqueIndex(tableName, constraintName));
                    break;
                }
                query.append(" CONSTRAINT ");
                query.append(this.getConstraintName(constraintName));
                break;
            }
            case 9: {
                final PrimaryKeyDefinition pkDef = (PrimaryKeyDefinition)alterObject;
                query.append(" CONSTRAINT ");
                query.append(this.getConstraintName(pkDef.getName()));
                query.append(" PRIMARY KEY ");
                this.setColumnNamesFromList(pkDef.getColumnList(), query, null);
                break;
            }
            case 6: {
                final ForeignKeyDefinition fkDef = (ForeignKeyDefinition)alterObject;
                query.append(" CONSTRAINT ");
                query.append(this.getConstraintName(fkDef.getName()));
                query.append(" FOREIGN KEY ");
                this.setColumnNamesFromList(fkDef.getFkColumns(), query, null);
                query.append(" REFERENCES ");
                query.append(this.getDBSpecificTableName(fkDef.getMasterTableName()));
                this.setColumnNamesFromList(fkDef.getFkRefColumns(), query, null);
                break;
            }
            case 3: {
                this.appendStringForDefaultConstraintDrop(ao, query);
                query.append(" COLUMN ");
                query.append(this.getDBSpecificColumnName((String)alterObject));
                query.append(" ");
                break;
            }
            case 7:
            case 8: {
                query.append(" CONSTRAINT ");
                query.append(this.getConstraintName((ao.getActualConstraintName() == null) ? ((String)alterObject) : ao.getActualConstraintName()));
                break;
            }
            case 14: {
                final ForeignKeyDefinition fk = (ForeignKeyDefinition)alterObject;
                query.append(" DROP CONSTRAINT ");
                query.append(this.getConstraintName((ao.getActualConstraintName() == null) ? fk.getName() : ao.getActualConstraintName()));
                query.append(";");
                if (ao.handleIndexForFK()) {
                    query.append(this.getSQLForDropIndex(tableName, (ao.getActualConstraintName() == null) ? this.getIndexName(fk.getName()) : ao.getActualConstraintName()));
                    query.append(";");
                }
                final IndexDefinition idx = this.getIndexDefForFK(fk);
                query.append(this.getSQLForIndex(ao.getTableName(), idx));
                query.append(";ALTER TABLE ");
                query.append(this.getDBSpecificTableName(tableName));
                query.append(" ADD CONSTRAINT ");
                query.append(this.getConstraintName(fk.getName()));
                query.append(" FOREIGN KEY ");
                this.setColumnNamesFromList(fk.getFkColumns(), query, null);
                query.append(" REFERENCES ");
                query.append(this.getDBSpecificTableName(fk.getMasterTableName()));
                this.setColumnNamesFromList(fk.getFkRefColumns(), query, null);
                query.append(";");
                break;
            }
            case 16: {
                final IndexDefinition idxDef = (IndexDefinition)alterObject;
                query.append(this.getSQLForDropIndex(tableName, (ao.getActualConstraintName() == null) ? idxDef.getName() : ao.getActualConstraintName()));
                query.append(";");
                query.append(this.getSQLForIndex(ao.getTableName(), idxDef));
                break;
            }
            case 17: {
                final String oldPKName = ((Object[])alterObject)[0].toString();
                final PrimaryKeyDefinition pk = (PrimaryKeyDefinition)((Object[])alterObject)[1];
                query.append(" DROP CONSTRAINT ");
                query.append(this.getConstraintName((ao.getActualConstraintName() == null) ? oldPKName : ao.getActualConstraintName()));
                query.append(";ALTER TABLE ");
                query.append(this.getDBSpecificTableName(tableName));
                query.append(" ADD CONSTRAINT ");
                query.append(this.getConstraintName(pk.getName()));
                query.append(" PRIMARY KEY ");
                this.setColumnNamesFromList(pk.getColumnList(), query, null);
                break;
            }
            default: {
                super.appendStringForAlterOperation(ao, query);
                break;
            }
        }
    }
    
    private String getSQLForDropUniqueIndex(final String tableName, final String constraintName) throws QueryConstructionException {
        return this.getSQLForDropIndex(tableName, constraintName);
    }
    
    String getSQLForCreateUniqueIndex(final AlterTableQuery atq, final AlterOperation ao, final TableDefinition td, final UniqueKeyDefinition ukDef) {
        final List<String> ukCols = ukDef.getColumns();
        final List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
        for (final String colName : ukDef.getColumns()) {
            final ColumnDefinition cd = td.getColumnDefinitionByName(colName);
            if (cd == null) {
                for (final AlterOperation tempAo : atq.getAlterOperations()) {
                    if (tempAo.getOperationType() == 1) {
                        final ColumnDefinition tepColDef = (ColumnDefinition)tempAo.getAlterObject();
                        if (!ukCols.contains(tepColDef.getColumnName()) || !tepColDef.isNullable()) {
                            continue;
                        }
                        columnDefinitions.add(tepColDef);
                    }
                }
            }
        }
        return this.getSQLForCreateUniqueIndex(ukDef.getName(), td, ukDef, columnDefinitions, null);
    }
    
    private void appendStringForDefaultConstraintDrop(final AlterOperation ao, final StringBuilder curQuery) throws QueryConstructionException {
        final String consName = ao.getDefaultValueConstraintName();
        if (null == consName) {
            return;
        }
        curQuery.append(" CONSTRAINT ");
        curQuery.append(consName);
        curQuery.append(',');
    }
    
    private String getRenameColSql(final AlterOperation ao) throws QueryConstructionException {
        final StringBuilder renameColQuery = new StringBuilder();
        String tableName = ao.getTableName();
        final Object alterObject = ao.getAlterObject();
        String renColName_Old = ((String[])alterObject)[0];
        String renColName_New = ((String[])alterObject)[1];
        tableName = tableName.replaceAll("'", "''");
        renColName_Old = renColName_Old.replaceAll("'", "''");
        renColName_New = renColName_New.replaceAll("'", "''");
        renameColQuery.append("exec sp_rename '").append(tableName).append('.').append(renColName_Old).append("', '").append(renColName_New).append("', 'COLUMN'");
        return renameColQuery.toString();
    }
    
    private String getRenameTableSql(final AlterOperation ao) {
        final StringBuilder renameTableQuery = new StringBuilder();
        final String tableName = ao.getTableName();
        final Object alterObject = ao.getAlterObject();
        final String renTableName_New = (String)alterObject;
        renameTableQuery.append("exec sp_rename '").append(tableName).append("', '").append(renTableName_New).append('\'');
        return renameTableQuery.toString();
    }
    
    @Override
    protected String getConstraintName(final String constraintName) throws QueryConstructionException {
        if (constraintName.length() > 60) {
            throw new QueryConstructionException("ConstraintName should not exceed 60 characters");
        }
        return "\"" + constraintName + "\"";
    }
    
    @Override
    protected String getSimpleDateFormatStringFor(final int type) throws QueryConstructionException {
        switch (type) {
            case 91: {
                return "yyyyMMdd";
            }
            case 92: {
                return "HH:mm:ss.SSS";
            }
            case 93: {
                return "yyyyMMdd HH:mm:ss.SSS";
            }
            default: {
                throw new QueryConstructionException("Unknow type [" + type + "] received here");
            }
        }
    }
    
    @Override
    protected String processSelectQuery(final SelectQuery query) throws QueryConstructionException {
        final List<Column> caseSensitiveColumns = new ArrayList<Column>();
        final String selectClause = this.formSelectClause(query.getSelectColumns(), caseSensitiveColumns, query);
        final String whereClause = this.formWhereClause(query.getCriteria(), false, caseSensitiveColumns, query.getDerivedTables());
        final String orderByClause = this.getOrderByClause(query.getSortColumns(), query.getSelectColumns(), query.getDerivedTables(), caseSensitiveColumns);
        final String sqlString = this.getSQL(query, selectClause, whereClause, orderByClause);
        MssqlSQLGenerator.OUT.log(Level.FINE, "MssqlSQLGenerator.processSelectQuery(): SQL formed is : {0}", sqlString);
        return sqlString;
    }
    
    @Override
    protected String processPKDefn(final TableDefinition tabDefn) throws QueryConstructionException {
        final PrimaryKeyDefinition pkDefn = tabDefn.getPrimaryKey();
        if (pkDefn == null) {
            return null;
        }
        final StringBuilder pkBuffer = new StringBuilder(100);
        final String constraint = pkDefn.getName();
        final List colList = pkDefn.getColumnList();
        final int colSize = colList.size();
        if (colSize == 0) {
            return null;
        }
        final String constName = this.getConstraintName(constraint);
        if (constName != null) {
            pkBuffer.append("CONSTRAINT ").append(constName);
        }
        pkBuffer.append(" PRIMARY KEY CLUSTERED (");
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
    
    @Override
    public String getDBSpecificColumnName(String columnName) {
        if (columnName.startsWith("\"")) {
            return columnName;
        }
        columnName = columnName.replaceAll("\"", "\"\"");
        return "*".equals(columnName) ? columnName : ("\"" + columnName + "\"");
    }
    
    @Override
    public String getDBSpecificTableName(String tableName) {
        this.validateTableAlias(tableName);
        tableName = tableName.replaceAll("\"", "\"\"");
        return "\"" + tableName + "\"";
    }
    
    private void validateTableAlias(final String tableAlias) {
        if (AppResources.getBoolean("newValidateTableAlias", Boolean.valueOf(false)) && (tableAlias.contains("--") || tableAlias.contains(";") || tableAlias.contains("\\"))) {
            throw new IllegalArgumentException("Table alias cannot have characters ::( -- , ; , \\)");
        }
    }
    
    @Override
    protected String processUniqueKeys(final TableDefinition td) throws QueryConstructionException {
        if (this.supportFilteredUniqueIndex) {
            return "";
        }
        return this.sql2005Gen.processUniqueKeys(td);
    }
    
    @Override
    protected String processFKDefn(final TableDefinition td) throws QueryConstructionException {
        final List fkList = td.getForeignKeyList();
        if (fkList == null || fkList.isEmpty()) {
            return null;
        }
        final StringBuilder fkBuffer = new StringBuilder(200);
        final int fkSize = fkList.size();
        boolean appendOdc = false;
        if (fkSize == 1) {
            appendOdc = true;
        }
        for (int i = 0; i < fkSize; ++i) {
            final ForeignKeyDefinition fkDefn = fkList.get(i);
            if (i != 0) {
                fkBuffer.append(",");
            }
            final String singleFkStr = this.getSingleFKDefn(fkDefn);
            fkBuffer.append(singleFkStr);
        }
        return fkBuffer.toString();
    }
    
    @Override
    protected String getSingleFKDefn(final ForeignKeyDefinition fkDefn) throws QueryConstructionException {
        final String fkName = fkDefn.getName();
        final String masterTabName = fkDefn.getMasterTableName();
        final String slaveTabName = fkDefn.getSlaveTableName();
        final List fkCols = fkDefn.getForeignKeyColumns();
        final int constraints = fkDefn.getConstraints();
        if (fkCols == null || fkCols.isEmpty()) {
            throw new QueryConstructionException("Foreign Key columns not specified in the ForeignKeyDefinition");
        }
        final StringBuilder fkBuffer = new StringBuilder(100);
        final StringBuilder refBuffer = new StringBuilder(100);
        final String constName = this.getConstraintName(fkName);
        if (constName != null) {
            fkBuffer.append("CONSTRAINT ").append(constName);
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
        fkBuffer.append(") REFERENCES ").append(this.getDBSpecificTableName(masterTabName)).append(" (");
        fkBuffer.append(refBuffer.toString()).append(") ");
        return fkBuffer.toString();
    }
    
    public String getSQLForDeleteTrigger(final String triggerName) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("if exists (select * from sysobjects where type = 'TR' and name like '");
        buffer.append(triggerName);
        buffer.append("') drop trigger ");
        buffer.append(triggerName);
        return buffer.toString();
    }
    
    public String getSQLForCreateTrigger(final String tableName, final List fkDefns) {
        return this.getSQLForCreateTrigger(tableName, fkDefns, null);
    }
    
    String getSQLForCreateTrigger(final String tableName, final List fkDefns, final Map<String, String> oldVsNewTableName) {
        final String triggerName = this.getTriggerName(tableName);
        final StringBuilder buffer = new StringBuilder();
        final String dbTableName = this.getDBSpecificTableName(tableName);
        buffer.append("create trigger ");
        buffer.append(triggerName);
        buffer.append(" on ");
        buffer.append(dbTableName);
        buffer.append("\ninstead of delete");
        buffer.append("\nas");
        buffer.append("\nbegin");
        List pkCols = null;
        for (int i = 0; i < fkDefns.size(); ++i) {
            final ForeignKeyDefinition fkDefn = fkDefns.get(i);
            final String slaveTableName = fkDefn.getSlaveTableName();
            final String localTbl = (oldVsNewTableName != null && oldVsNewTableName.get(slaveTableName) != null) ? oldVsNewTableName.get(slaveTableName) : slaveTableName;
            final List fkCols = fkDefn.getFkColumns();
            final List fkRefCols = fkDefn.getFkRefColumns();
            final int size = fkCols.size();
            if (pkCols == null) {
                pkCols = fkRefCols;
            }
            if (fkDefn.getConstraints() == 1) {
                buffer.append("\ndelete from ");
                buffer.append(this.getDBSpecificTableName(localTbl));
            }
            else {
                if (fkDefn.getConstraints() != 2) {
                    continue;
                }
                buffer.append("\nupdate ");
                buffer.append(this.getDBSpecificTableName(localTbl));
                buffer.append(" set ");
                for (int k = 0; k < size; ++k) {
                    final String colName = fkCols.get(k);
                    buffer.append(this.getDBSpecificColumnName(colName));
                    buffer.append(" = null ");
                    if (k != size - 1) {
                        buffer.append(",");
                    }
                }
            }
            buffer.append(" where exists ( select * from DELETED where ");
            for (int j = 0; j < size; ++j) {
                final String colName = fkCols.get(j);
                final String refColName = fkRefCols.get(j);
                buffer.append("DELETED.");
                buffer.append(this.getDBSpecificColumnName(refColName));
                buffer.append(" = ");
                buffer.append(this.getDBSpecificTableName(localTbl) + ".");
                buffer.append(this.getDBSpecificColumnName(colName));
                if (j != size - 1) {
                    buffer.append(" and ");
                }
            }
            buffer.append(")");
        }
        buffer.append("\ndelete from ");
        buffer.append(dbTableName);
        buffer.append(" where exists ( select * from DELETED where ");
        for (int size2 = pkCols.size(), l = 0; l < size2; ++l) {
            buffer.append("DELETED.");
            final String colName2 = pkCols.get(l);
            buffer.append(this.getDBSpecificColumnName(colName2));
            buffer.append(" = ");
            buffer.append(dbTableName + ".");
            buffer.append(this.getDBSpecificColumnName(colName2));
            if (l != size2 - 1) {
                buffer.append(" and ");
            }
        }
        buffer.append(")");
        buffer.append("\nend");
        MssqlSQLGenerator.OUT.log(Level.FINER, "Trigger created for table {0} is {1}", new Object[] { tableName, buffer.toString() });
        return buffer.toString();
    }
    
    private List<String> getNullableColumns(final TableDefinition td, final UniqueKeyDefinition ukd, final List<ColumnDefinition> modifiedColDef, final Map<String, String> oldColVsNewCol) {
        final List<String> ukColumns = ukd.getColumns();
        final List<String> nullableColumnNames = new ArrayList<String>();
        final List<String> pkColumns = td.getPrimaryKey().getColumnList();
        for (String columnName : ukColumns) {
            final ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
            if (cd != null && cd.isNullable() && !pkColumns.contains(cd.getColumnName())) {
                if (oldColVsNewCol != null && oldColVsNewCol.size() > 0) {
                    columnName = oldColVsNewCol.getOrDefault(columnName, columnName);
                }
                nullableColumnNames.add(columnName);
            }
        }
        if (modifiedColDef != null) {
            for (final ColumnDefinition colDef : modifiedColDef) {
                final String modifiedColumnName = colDef.getColumnName();
                if (ukColumns.contains(modifiedColumnName)) {
                    if (!colDef.isNullable()) {
                        nullableColumnNames.remove(modifiedColumnName);
                        break;
                    }
                    if (!nullableColumnNames.contains(modifiedColumnName)) {
                        nullableColumnNames.add(modifiedColumnName);
                        break;
                    }
                    break;
                }
            }
        }
        return nullableColumnNames;
    }
    
    String getSQLForCreateUniqueIndex(final String indexName, final TableDefinition td, final UniqueKeyDefinition ukd, final List<ColumnDefinition> modifiedColumnDef, final Map<String, String> oldColVsNewCol) {
        final List<String> nullableColumnNames = this.getNullableColumns(td, ukd, modifiedColumnDef, oldColVsNewCol);
        final String tableName = td.getTableName();
        final List<String> columns = ukd.getColumns();
        final StringBuilder buf = new StringBuilder("CREATE UNIQUE INDEX ");
        try {
            buf.append(this.getConstraintName(indexName));
        }
        catch (final QueryConstructionException e) {
            throw new RuntimeException(e);
        }
        buf.append(" ON ");
        buf.append(this.getDBSpecificTableName(tableName) + "(");
        Iterator<String> itr = columns.iterator();
        while (itr.hasNext()) {
            String colName = itr.next();
            if (oldColVsNewCol != null && oldColVsNewCol.size() > 0) {
                colName = oldColVsNewCol.getOrDefault(colName, colName);
            }
            buf.append(this.getDBSpecificColumnName(colName));
            if (itr.hasNext()) {
                buf.append(",");
            }
        }
        buf.append(")");
        if (!nullableColumnNames.isEmpty()) {
            buf.append(" WHERE ");
            itr = nullableColumnNames.iterator();
            while (itr.hasNext()) {
                buf.append(this.getDBSpecificColumnName(itr.next()) + " IS NOT NULL");
                if (itr.hasNext()) {
                    buf.append(" AND ");
                }
            }
        }
        MssqlSQLGenerator.OUT.log(Level.FINE, "Unique Index created for table {0} column {1} is {2}", new Object[] { tableName, oldColVsNewCol, buf.toString() });
        return buf.toString();
    }
    
    @Override
    protected String processSimpleColumn(Column column, final boolean caseSensitive, final List<Table> derivedTableList, final Clause columnBelongsToClause) {
        final int type = column.getType();
        String retVal = null;
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
        final String columnName = column.getColumnName();
        if (tableName != null) {
            if (this.isAliasedColumnOfDerivedTable(derivedTableList, tableName, columnName)) {
                retVal = this.getDBSpecificTableName(tableName) + "." + this.getDBSpecificColumnAlias(columnName);
            }
            else {
                retVal = this.getDBSpecificTableName(tableName) + "." + this.getDBSpecificColumnName(columnName);
            }
        }
        else {
            retVal = this.getDBSpecificColumnName(columnName);
        }
        retVal = this.getDBSpecificDecryptionString(column, retVal);
        return retVal;
    }
    
    @Override
    protected String handleCaseSensitive(String valueStr, final boolean isCaseSensitive, final int type, final boolean isEncrypted, final Clause columnBelongsToClause) {
        if (DataTypeUtil.isUDT(type)) {
            return DataTypeManager.getDataTypeDefinition(type).getDTSQLGenerator(this.getDBType()).handleCaseSensitive(valueStr, isCaseSensitive, isEncrypted, columnBelongsToClause);
        }
        if (valueStr != null && (type == 1 || type == 12)) {
            final String append_Collation = (isCaseSensitive == this.isDBCaseSensitive) ? "" : this.toggle_collation_string;
            valueStr += append_Collation;
        }
        return valueStr;
    }
    
    @Override
    protected String lhsOfCriterion(final Column column, final boolean caseSensitive, final int comparator, final List<Table> derivedTableList) throws QueryConstructionException {
        String valueStr = super.lhsOfCriterion(column, caseSensitive, comparator, derivedTableList);
        valueStr = this.handleCaseSensitive(valueStr, caseSensitive, column.getType(), column.isEncrypted(), Clause.WHERE);
        return valueStr;
    }
    
    @Override
    protected String getValue(final Object value, final boolean isCaseSensitive) {
        return value.toString();
    }
    
    @Override
    protected String getValueString(String valueStr, final Column column, final int comparator, final boolean hasWildCard, final boolean isCaseSensitive) throws QueryConstructionException {
        final int type = column.getType();
        final String valString = super.getValueString(valueStr, column, comparator, hasWildCard, isCaseSensitive);
        if (type == 1 || type == 12) {
            final String dataType = column.getDataType();
            if ("NCHAR".equals(dataType) || "SCHAR".equals(dataType) || ("CHAR".equals(dataType) && this.treatCharAsNChar)) {
                valueStr = this.escapeSpecialCharacters(valueStr, type);
                return "N'" + valueStr + "'";
            }
        }
        return valString;
    }
    
    @Override
    public String getSQLForDelete(final DeleteQuery query) throws QueryConstructionException {
        final StringBuilder buff = new StringBuilder();
        final String tableName = query.getTableName();
        final String tabName = this.getDBSpecificTableName(tableName);
        final int numOfRows = query.getLimit();
        final Criteria criteria = query.getCriteria();
        final List<Join> joins = query.getJoins();
        if (!joins.isEmpty()) {
            if (numOfRows <= 0) {
                buff.append("DELETE " + tabName + " FROM ");
                buff.append(this.formJoinString(joins, Collections.emptyList())).append(" ");
                final String whereClause = this.formWhereClause(criteria, false, null);
                if (whereClause != null) {
                    buff.append(" WHERE ");
                    buff.append(whereClause);
                }
                return buff.toString();
            }
            throw new QueryConstructionException("ORDER BY and LIMIT cannot be used with JOIN");
        }
        else {
            if (numOfRows <= 0) {
                return this.getSQLForDelete(tableName, criteria);
            }
            final List sortcolumns = query.getSortColumns();
            if (!sortcolumns.isEmpty()) {
                final SelectQuery sq = new SelectQueryImpl(Table.getTable(tableName));
                sq.addSelectColumn(Column.getColumn(null, "*"));
                sq.setCriteria(criteria);
                final Range range = new Range(1, numOfRows);
                sq.setRange(range);
                sq.addSortColumns(sortcolumns);
                final String selectSQL = RelationalAPI.getInstance().getSelectSQL(sq);
                final String delSQL = selectSQL.replaceFirst("SELECT \\*", "DELETE ORG_QUERY");
                return delSQL;
            }
            throw new QueryConstructionException("Cannot construct range query without 'ORDER BY' clause");
        }
    }
    
    @Override
    public String getSQLForUpdate(final List tableList, final Map newValues, final Criteria criteria, final List joins) throws QueryConstructionException {
        final Table baseTable = tableList.get(0);
        final String tableName = baseTable.getTableName();
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
        updateBuffer.append(this.getDBSpecificTableName(tableName));
        updateBuffer.append(" SET ");
        updateBuffer.append(setSQL);
        updateBuffer.append(" FROM ");
        updateBuffer.append(this.formJoinString(joins, null));
        if (whereClause != null) {
            updateBuffer.append(" WHERE (");
            updateBuffer.append(whereClause);
            updateBuffer.append(")");
        }
        final String sql = updateBuffer.toString();
        MssqlSQLGenerator.OUT.log(Level.FINE, "MssqlSQLGEnerator.getSQLForUpdateQuery(): SQL formed is : {0}", sql);
        return sql;
    }
    
    @Override
    public String getSQLForDrop(final String tableName, final boolean cascade) throws QueryConstructionException {
        final String dropSQL = " DROP TABLE " + this.getDBSpecificTableName(tableName);
        MssqlSQLGenerator.OUT.log(Level.FINER, "Drop table for table {0} is {1}", new Object[] { tableName, dropSQL });
        return dropSQL;
    }
    
    void appendIfExistsForDropTable(final String tableName, final StringBuilder sql) {
        final String checkIfUserTbl = "IF Exists (select name from sysobjects where type='U' AND name like '" + tableName + "')";
        sql.insert(0, checkIfUserTbl);
    }
    
    @Override
    public String getSQLForDrop(final String tableName, final boolean ifExists, final boolean cascade) throws QueryConstructionException {
        final StringBuilder dropSQL = new StringBuilder();
        dropSQL.append(this.getSQLForDrop(tableName, true));
        this.appendIfExistsForDropTable(tableName, dropSQL);
        return dropSQL.toString();
    }
    
    public String getDefaultValue(final String dataType, final Object defVal) throws QueryConstructionException {
        if (!dataType.equals("SCHAR")) {
            return super.getDefaultValue(dataType, defVal);
        }
        if (defVal == null) {
            return null;
        }
        return "CONVERT(VARBINARY(255),'" + String.valueOf(defVal) + "')";
    }
    
    void setRowGuidEnable(final boolean flag) {
        this.rowGuidEnable = flag;
    }
    
    void setIdentityValue(final String identityValue) {
        this.identityValue = this.escapeSpecialCharacters(identityValue, 12);
    }
    
    void setKeySource(final String keySource) {
        this.keySource = this.escapeSpecialCharacters(keySource, 12);
    }
    
    void setCertificateName(final String certName) {
        this.certificateName = this.escapeSpecialCharacters(certName, 12);
    }
    
    void setSymmetricKeyName(final String symmKeyName) {
        this.symmKeyName = this.escapeSpecialCharacters(symmKeyName, 12);
    }
    
    void setMasterkeyPassword(final String masterkeyPass) {
        this.masterkeyPass = this.escapeSpecialCharacters(masterkeyPass, 12);
    }
    
    void setSymmetricKeyAlgorithm(final String symmkeyAlgo) {
        this.symmkeyAlgo = this.escapeSpecialCharacters(symmkeyAlgo, 12);
    }
    
    void setSymmetricKeyAltAlgorithm(final String symmkeyAlgo_alt) {
        this.symmkeyAlgo_alt = this.escapeSpecialCharacters(symmkeyAlgo_alt, 12);
    }
    
    void setCertificateSubject(final String certificateSub) {
        this.certificateSub = this.escapeSpecialCharacters(certificateSub, 12);
    }
    
    void setSupportsEncryption(final boolean flag) {
        this.supportsEncryption = flag;
    }
    
    void deprecateTextDataType() {
        MssqlSQLGenerator.isTextDataTypeDeprecated = true;
    }
    
    boolean isEncryptionDecryptionRequired(final Column column) {
        return column.isEncrypted() && this.supportsEncryption && column.getType() != 2004;
    }
    
    @Override
    public String getDBSpecificEncryptionString(final Column column, final String value) {
        if (!this.isEncryptionDecryptionRequired(column)) {
            return value;
        }
        if (DataTypeUtil.isUDT(column.getDataType())) {
            return DataTypeManager.getDataTypeDefinition(column.getDataType()).getDTSQLGenerator(this.getDBType()).getDTSpecificEncryptionString(column, value);
        }
        return "encryptbykey(key_guid('" + this.symmKeyName + "')," + value + ")";
    }
    
    @Override
    public String getDBSpecificDecryptionString(final Column column, final String value) {
        if (!this.isEncryptionDecryptionRequired(column)) {
            return value;
        }
        if (DataTypeUtil.isUDT(column.getDataType())) {
            return DataTypeManager.getDataTypeDefinition(column.getDataType()).getDTSQLGenerator(this.getDBType()).getDTSpecificDecryptionString(column, value);
        }
        return "convert(NVARCHAR(4000), " + this.getDecryptSQL(value, column.getDataType()) + " )";
    }
    
    @Override
    public String getDecryptSQL(final String value, final String dataType) {
        if (DataTypeManager.isDataTypeSupported(dataType) && DataTypeManager.getDataTypeDefinition(dataType).getBaseType() == null) {
            return DataTypeManager.getDataTypeDefinition(dataType).getDTSQLGenerator(this.dbType).getDTSpecificDecryptionString(null, value);
        }
        return " decryptbykeyautocert(cert_id('" + this.certificateName + "'),NULL," + value + ")";
    }
    
    protected String getSelectSQLForDefaultSymmetricKeys() {
        return "select name from sys.symmetric_keys where name = '##MS_DatabaseMasterKey##'";
    }
    
    protected String getSQLForCreateMasterKey() {
        return "create master key encryption by password='" + this.masterkeyPass + "'";
    }
    
    protected String getSQLForOpenMasterKey() {
        return "OPEN MASTER KEY DECRYPTION BY PASSWORD = '" + this.masterkeyPass + "'";
    }
    
    protected String getSQLToAssociateMKWithSMK() {
        return "ALTER MASTER KEY ADD ENCRYPTION BY SERVICE MASTER KEY";
    }
    
    protected String getSelectSQLForSymmetricKey() {
        return "select name from sys.symmetric_keys where name = '" + this.symmKeyName + "'";
    }
    
    protected String getSQLForCreateSymmetricKey(final boolean isAES) {
        final StringBuilder sb = new StringBuilder();
        sb.append("CREATE SYMMETRIC KEY ").append(this.symmKeyName);
        sb.append(" WITH ALGORITHM = ").append(isAES ? this.symmkeyAlgo : this.symmkeyAlgo_alt);
        sb.append(", KEY_SOURCE = '").append(this.keySource).append("'");
        sb.append(", IDENTITY_VALUE = '").append(this.identityValue).append("'");
        sb.append(" ENCRYPTION BY CERTIFICATE ").append(this.certificateName);
        return sb.toString();
    }
    
    protected String getSelectSQLForCertificate() {
        return "select name from sys.certificates where name = '" + this.certificateName + "'";
    }
    
    protected String getSQLForCreateCertificate() {
        return "CREATE CERTIFICATE " + this.certificateName + " WITH SUBJECT='" + this.certificateSub + "'";
    }
    
    protected String getSQLForOpenSymmetricKey() {
        return "OPEN SYMMETRIC KEY " + this.symmKeyName + " DECRYPTION BY CERTIFICATE " + this.certificateName;
    }
    
    protected String getSQLForCloseSymmetricKey() {
        return "CLOSE SYMMETRIC KEY " + this.symmKeyName;
    }
    
    protected String getSelectSQLForOpenSymmetricKey() {
        return "select * from sys.openkeys where database_name = DB_NAME() and key_name = '" + this.symmKeyName + "'";
    }
    
    protected String getSelectSQLForEncryptDecrytData() {
        return "select case when convert(varchar(50),DECRYPTBYKEY(encryptbykey(KEY_GUID('" + this.symmKeyName + "'),'mickey'))) = 'mickey' then 1 else 0 end";
    }
    
    @Override
    public String getSQLForCreateTable(final TableDefinition tabDefn, final String createTableOptions) throws QueryConstructionException {
        final StringBuilder createQuery = new StringBuilder(super.getSQLForCreateTable(tabDefn, createTableOptions));
        if (this.supportFilteredUniqueIndex) {
            this.appendCreateUniqueIndexForCreateTable(createQuery, tabDefn);
        }
        if (this.rowGuidEnable) {
            createQuery.insert(createQuery.indexOf("(") + 1, " ROWGUID UNIQUEIDENTIFIER ROWGUIDCOL UNIQUE DEFAULT NEWSEQUENTIALID(), ");
        }
        return createQuery.toString();
    }
    
    private void appendCreateUniqueIndexForCreateTable(final StringBuilder createQuery, final TableDefinition tabDefn) {
        final List<UniqueKeyDefinition> ukds = tabDefn.getUniqueKeys();
        if (ukds != null) {
            for (final UniqueKeyDefinition ukd : ukds) {
                createQuery.append("; ").append(this.getSQLForCreateUniqueIndex(ukd.getName(), tabDefn, ukd, null, null));
            }
        }
    }
    
    protected String getSQLToFetchDefValConstraintName(final String tableName, final String columnName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("SELECT constid, name, xtype, colid FROM sysconstraints c,sysobjects o WHERE o.id = c.constid AND c.id = (SELECT s.id FROM sysobjects s WHERE xtype = 'U' and name = '");
        sb.append(tableName);
        sb.append("') and xtype = 'D' and colid = (SELECT colid FROM syscolumns WHERE name = '");
        sb.append(columnName);
        sb.append("' and id = (SELECT id FROM sysobjects WHERE xtype = 'U' and name = '");
        sb.append(tableName);
        sb.append("'))");
        return sb.toString();
    }
    
    protected String getSQLForDropDefValConstraint(final String tableName, final String constraintName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ");
        sb.append(this.getDBSpecificTableName(tableName));
        sb.append(" DROP CONSTRAINT ");
        sb.append(this.getDBSpecificColumnName(constraintName));
        return sb.toString();
    }
    
    protected String getSQLForAddDefValConstraint(final String tableName, final String columnName, final String constraintName, final String dataType, final Object defVal) throws QueryConstructionException {
        final StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ");
        sb.append(this.getDBSpecificTableName(tableName));
        sb.append(" ADD CONSTRAINT ");
        sb.append(this.getConstraintName(constraintName));
        sb.append(" DEFAULT (");
        sb.append(this.getDefaultValue(dataType, defVal));
        sb.append(") FOR ");
        sb.append(this.getDBSpecificColumnName(columnName));
        return sb.toString();
    }
    
    String getSQLForColumnValueCopy(final ColumnDefinition srcColDef, final ColumnDefinition dstColDef) {
        final String tableName = dstColDef.getTableName();
        final String dColName = dstColDef.getColumnName();
        final String sColName = srcColDef.getColumnName();
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("UPDATE ");
        strBuff.append(this.getDBSpecificTableName(tableName));
        strBuff.append(" SET ");
        strBuff.append(this.getDBSpecificColumnName(dColName));
        strBuff.append(" = ");
        final Column column = new Column(tableName, sColName);
        column.setDefinition(srcColDef);
        if (srcColDef.isEncryptedColumn()) {
            column.setDefinition(srcColDef);
            strBuff.append(this.getDBSpecificDecryptionString(column, this.getDBSpecificColumnName(sColName)));
            column.setDefinition(dstColDef);
        }
        else {
            column.setDefinition(dstColDef);
            if (srcColDef.getDataType().equals("CHAR") && !this.treatCharAsNChar) {
                strBuff.append(this.getDBSpecificEncryptionString(column, "convert( NVARCHAR(MAX) ,  " + this.getDBSpecificColumnName(sColName) + ")"));
            }
            else {
                strBuff.append(this.getDBSpecificEncryptionString(column, this.getDBSpecificColumnName(sColName)));
            }
        }
        return strBuff.toString();
    }
    
    boolean isEncrptedModifyColumn(final AlterOperation ao) {
        if (2 == ao.getOperationType()) {
            final ColumnDefinition newColDef = (ColumnDefinition)ao.getAlterObject();
            final ColumnDefinition oldColDef = this.getColumnIgnoreException(ao.getTableName(), newColDef.getColumnName());
            final String newDataType = newColDef.getDataType();
            final String oldDataType = oldColDef.getDataType();
            return !newDataType.equals(oldDataType) && (newDataType.equals("SCHAR") || oldDataType.equals("SCHAR"));
        }
        return false;
    }
    
    boolean isNewDataTypeBinary(final AlterOperation ao) {
        if (2 == ao.getOperationType()) {
            final ColumnDefinition newColDef = (ColumnDefinition)ao.getAlterObject();
            final ColumnDefinition oldColDef = this.getColumnIgnoreException(ao.getTableName(), newColDef.getColumnName());
            final String newDataType = newColDef.getDataType();
            final String oldDataType = oldColDef.getDataType();
            return !newDataType.equals(oldDataType) && (newDataType.equals("BLOB") || newDataType.equals("SBLOB"));
        }
        return false;
    }
    
    boolean isPreAlterHandling(final AlterOperation ao) {
        return this.isEncrptedModifyColumn(ao);
    }
    
    boolean isPostAlterHandling(final AlterOperation ao, final AlterTableQuery completeQuery) {
        return this.isTriggerForUK(ao, completeQuery) || this.isAddColumnAndAddUK(ao, completeQuery);
    }
    
    boolean isAddColumnAndAddUK(final AlterOperation ao, final AlterTableQuery completeQuery) {
        try {
            if (this.supportFilteredUniqueIndex && ao.getOperationType() == 4) {
                final UniqueKeyDefinition ukDef = (UniqueKeyDefinition)ao.getAlterObject();
                final TableDefinition tdf = MetaDataUtil.getTableDefinitionByName(completeQuery.getTableName());
                for (final String colName : ukDef.getColumns()) {
                    final ColumnDefinition cd = tdf.getColumnDefinitionByName(colName);
                    if (cd == null) {
                        for (final AlterOperation tempAo : completeQuery.getAlterOperations()) {
                            if (tempAo.getOperationType() == 1) {
                                final ColumnDefinition tepColDef = (ColumnDefinition)tempAo.getAlterObject();
                                if (ukDef.getColumns().contains(tepColDef.getColumnName())) {
                                    return true;
                                }
                                continue;
                            }
                        }
                    }
                }
            }
            return false;
        }
        catch (final MetaDataException me) {
            throw new RuntimeException(me);
        }
    }
    
    public boolean isTriggerForUK(final AlterOperation alterOperation, final AlterTableQuery completeQuery) {
        return !this.supportFilteredUniqueIndex && this.sql2005Gen.isTriggerForUK(alterOperation, completeQuery);
    }
    
    boolean isPrePostHandlingAlone(final AlterOperation ao, final AlterTableQuery completeQuery) {
        return this.isPreAlterHandling(ao) || this.isPostAlterHandling(ao, completeQuery);
    }
    
    ColumnDefinition getColumnIgnoreException(final String tName, final String cName) {
        try {
            return MetaDataUtil.getTableDefinitionByName(tName).getColumnDefinitionByName(cName);
        }
        catch (final MetaDataException e) {
            return null;
        }
    }
    
    String getSQLForAddUVHColumn(final ColumnDefinition cd) throws QueryConstructionException {
        final StringBuilder buffer = new StringBuilder("ALTER TABLE ");
        buffer.append(this.getDBSpecificTableName(cd.getTableName()));
        buffer.append(" ADD ");
        buffer.append(this.getDBSpecificColumnName(cd.getColumnName()));
        buffer.append(" ");
        buffer.append(this.getDBDataType(cd));
        if (!cd.isNullable()) {
            buffer.append(" NOT NULL");
        }
        buffer.append(" IDENTITY");
        return buffer.toString();
    }
    
    String getSQLForUpdateColumn(final String tableName, final String oldColumnName, final String newColumnName) {
        final StringBuilder buffer = new StringBuilder("UPDATE ");
        buffer.append(this.getDBSpecificTableName(tableName));
        buffer.append(" SET ");
        buffer.append(this.getDBSpecificColumnName(oldColumnName));
        buffer.append(" = ");
        buffer.append(this.getDBSpecificColumnName(newColumnName));
        buffer.append(";");
        return buffer.toString();
    }
    
    public String getSQLForGetConstraintName(final AlterOperation alterOperation) throws SQLException {
        String sql = "";
        final int OperationType = alterOperation.getOperationType();
        switch (OperationType) {
            case 8:
            case 17: {
                sql = "select obj.name from sys.sysobjects obj inner join sys.sysobjects parent_obj on obj.parent_obj = parent_obj.id where parent_obj.name=? and obj.xtype='PK';";
                break;
            }
            case 7:
            case 14: {
                String fkConsName = null;
                if (OperationType == 14) {
                    fkConsName = ((ForeignKeyDefinition)alterOperation.getAlterObject()).getName();
                }
                else {
                    fkConsName = (String)alterOperation.getAlterObject();
                }
                final StringBuilder strBuff = new StringBuilder();
                strBuff.append("select o.name from sys.sysreferences fk inner join sys.sysobjects o on fk.constid = o.id");
                strBuff.append(" where fk.constid in (select c.constid from sys.sysconstraints c ");
                strBuff.append(" inner join sys.sysobjects o on c.constid = o.id where o.parent_obj =");
                strBuff.append(" (select id from sys.sysobjects where name='");
                strBuff.append(alterOperation.getTableName());
                strBuff.append("') and o.xtype = 'F') and ");
                try {
                    final int noOfCols = MetaDataUtil.getForeignKeyDefinitionByName(fkConsName).getFkColumns().size();
                    strBuff.append(" fk.keycnt = ");
                    strBuff.append(noOfCols);
                    for (int i = 1; i <= noOfCols; ++i) {
                        strBuff.append(" and ");
                        strBuff.append("fk.fkey");
                        strBuff.append(i);
                        strBuff.append(" = ? ");
                    }
                }
                catch (final MetaDataException ex) {
                    final SQLException sqle = new SQLException("Exception during fetch FK column list from FKDefinition");
                    sqle.initCause(ex);
                    throw sqle;
                }
                sql = strBuff.toString();
                break;
            }
            default: {
                throw new SQLException("Unknown operation specified to getConstraintName query");
            }
        }
        return sql;
    }
    
    public String getSQLForGetColIDFromSysObjectTable(final String tableName, final List<String> columnNames) {
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append("select col.name,col.colid from sys.sysobjects o inner join sys.syscolumns col on o.id = col.id ");
        strBuff.append("where o.name='");
        strBuff.append(tableName);
        strBuff.append("' and col.name IN (");
        boolean appendComma = false;
        for (final String columnName : columnNames) {
            if (appendComma) {
                strBuff.append(" , ");
            }
            strBuff.append("'");
            strBuff.append(columnName);
            strBuff.append("'");
            appendComma = true;
        }
        strBuff.append(")");
        return strBuff.toString();
    }
    
    @Override
    public String getSQLForCreateArchiveTable(final CreateTableLike cloneTdleDetails, final String createTableOptions, final boolean isPush) throws QueryConstructionException {
        final SelectQuery selectQry = cloneTdleDetails.getSelectQuery();
        final ArchiveTable tableToBeCloned = cloneTdleDetails.getArchiveTable();
        final List<ColumnDefinition> coldeflist = cloneTdleDetails.getTableDefinition().getColumnList();
        final StringBuilder buff = new StringBuilder();
        buff.append("SELECT ");
        boolean appendComma = false;
        for (final ColumnDefinition coldef : coldeflist) {
            if (appendComma) {
                buff.append(", ");
            }
            else {
                appendComma = true;
            }
            if (isPush) {
                buff.append(this.processSimpleColumn(Column.getColumn(coldef.getTableName(), coldef.getColumnName()), Clause.SELECT));
            }
            else {
                buff.append(this.processSimpleColumn(Column.getColumn(tableToBeCloned.getArchiveTableName(), coldef.getColumnName()), Clause.SELECT));
            }
        }
        buff.append(" INTO ");
        final String tableNameToBeCreated = isPush ? tableToBeCloned.getArchiveTableName() : tableToBeCloned.getInvisibleTableName();
        buff.append(this.getDBSpecificTableName(tableNameToBeCreated));
        buff.append(" FROM ");
        if (selectQry != null) {
            String selectSQL = this.getSQLForSelect(selectQry);
            selectSQL = selectSQL.substring(selectSQL.indexOf("FROM ") + 5, selectSQL.length());
            buff.append(selectSQL);
        }
        else {
            final String tableNameToBeQueried = isPush ? tableToBeCloned.getTableName() : tableToBeCloned.getArchiveTableName();
            buff.append(this.getDBSpecificTableName(tableNameToBeQueried));
            buff.append(" WHERE 1=2 ");
        }
        return buff.toString();
    }
    
    @Override
    protected String getDBSpecificColumnAlias(String columnAlias) {
        columnAlias = columnAlias.replaceAll("\"", "\"\"");
        return "\"" + columnAlias + "\"";
    }
    
    @Override
    protected void getAliasedColumn(final SelectQuery sq, final Column column, final StringBuilder selectClause) throws QueryConstructionException {
        final String columnAlias = column.getColumnAlias();
        final String columnName = column.getColumnName();
        final String tableAlias = column.getTableAlias();
        if ((columnAlias == null || columnName == null || columnName.equals(columnAlias)) && (column.getFunction() == 0 || columnAlias == null) && this.isEncryptionDecryptionRequired(column)) {
            selectClause.append(" AS ");
            selectClause.append(this.getDBSpecificColumnAlias(columnAlias));
        }
        else {
            super.getAliasedColumn(sq, column, selectClause);
        }
    }
    
    protected void setIsDBMigration(final boolean isDBMigrationRunning) {
        this.isDBMigrationRunning = isDBMigrationRunning;
    }
    
    @Override
    public String getBulkSql(final BulkLoad bulk, final BulkInsertObject bio) throws MetaDataException, SQLException, QueryConstructionException {
        final LinkedHashMap columnValues = new LinkedHashMap();
        String sql = "";
        if (null != MetaDataUtil.getTableDefinitionByName(bulk.getTableName())) {
            for (final String colName : bio.getColNames()) {
                final Column column = Column.getColumn(bulk.getTableName(), colName);
                final String dataType = bio.getColTypeNames().get(bio.getColNames().indexOf(column.getColumnName()));
                if (DataTypeUtil.isUDT(dataType) && !DataTypeManager.getDataTypeDefinition(dataType).getMeta().processInput()) {
                    continue;
                }
                column.setDataType(dataType);
                QueryUtil.setType(bulk.getTableName(), column);
                columnValues.put(column, QueryConstants.PREPARED_STMT_CONST);
            }
        }
        else {
            for (final String colName : bio.getColNames()) {
                final Column column = Column.getColumn(bulk.getTableName(), colName);
                column.setDataType(bio.getColTypeNames().get(bio.getColNames().indexOf(column.getColumnName())));
                columnValues.put(column, QueryConstants.PREPARED_STMT_CONST);
            }
        }
        if (bulk.isArchivedTable()) {
            sql = this.getInsertSQLForArchiveTable(bulk.getArchivedTableName(), columnValues, bulk.getTableName());
        }
        else {
            sql = this.getSQLForInsert(bulk.getTableName(), columnValues);
        }
        return sql;
    }
    
    private String getInsertSQLForArchiveTable(final String tableName, final Map values, final String liveTableName) throws QueryConstructionException {
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
                            MssqlSQLGenerator.OUT.log(Level.WARNING, "Column {0} type {1} and value \"{2}\" type {3} mismatch", new Object[] { column, Ansi92SQLGenerator.getSQLTypeAsString(type), value, value.getClass() });
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
            this.getDCSQLGeneratorForTable(liveTableName).getSQLForInsert(liveTableName, dyMap, valueBuffer, buffer);
        }
        buffer.append(") VALUES (");
        buffer.append(valueBuffer.toString());
        buffer.append(")");
        final String returnString = buffer.toString();
        MssqlSQLGenerator.OUT.log(Level.FINE, "getInsertSQLForArchiveTable(): SQL formed is : \n{0}", returnString);
        return returnString;
    }
    
    @Override
    protected void processSortColumn(final SortColumn sortColumn, final StringBuilder buffer, final List selectColumns, final List<SortColumn> sortColumns, final List<Table> derivedTableList) throws QueryConstructionException {
        if (sortColumn.isCaseSensitive() && (sortColumn.getColumn().getType() == 12 || sortColumn.getColumn().getType() == 1)) {
            final int sortColIdx = sortColumns.indexOf(sortColumn);
            if (sortColIdx != -1) {
                int csColumns = 0;
                for (int i = 0; i < sortColIdx; ++i) {
                    if (sortColumns.get(i).isCaseSensitive() && (sortColumns.get(i).getColumn().getType() == 12 || sortColumns.get(i).getColumn().getType() == 1)) {
                        ++csColumns;
                    }
                }
                buffer.append(selectColumns.size() + csColumns + 1);
            }
        }
        else {
            super.processSortColumn(sortColumn, buffer, selectColumns, derivedTableList);
        }
    }
    
    @Override
    protected String formSelectClause(final List columnsList, final List caseSensitiveColumns, final SelectQuery sq) throws QueryConstructionException {
        final StringBuilder buff = new StringBuilder(super.formSelectClause(columnsList, caseSensitiveColumns, sq));
        if (null != sq) {
            final List<SortColumn> sortColumns = sq.getSortColumns();
            for (final SortColumn sortColumn : sortColumns) {
                if (sortColumn.isCaseSensitive() && (sortColumn.getColumn().getType() == 12 || sortColumn.getColumn().getType() == 1)) {
                    buff.append(", CONVERT(VARBINARY(MAX), ").append(this.processSimpleColumn(sortColumn.getColumn(), false, null, null)).append(")");
                }
            }
            buff.append(" ");
        }
        return buff.toString();
    }
    
    protected String getUpdateSQLForColumn(final String tableName, final ColumnDefinition updateColumnDefinition, final List<String> pkColumns) throws QueryConstructionException {
        final String updateColumnName = updateColumnDefinition.getColumnName();
        if (tableName == null || updateColumnName == null) {
            throw new QueryConstructionException("Expected proper table name and column name to generate Update SQL");
        }
        final StringBuffer buffer = new StringBuffer();
        buffer.append("UPDATE ");
        buffer.append(this.getDBSpecificTableName(tableName));
        buffer.append("SET ");
        buffer.append(updateColumnName);
        buffer.append(" = ");
        final Column column = new Column(tableName, updateColumnName);
        column.setDefinition(updateColumnDefinition);
        buffer.append(this.getDBSpecificEncryptionString(column, "?"));
        if (pkColumns != null) {
            buffer.append(" WHERE ");
            int count = 0;
            for (final String pkColumn : pkColumns) {
                if (count != 0) {
                    buffer.append(" , ");
                }
                buffer.append(pkColumn + " = ?");
                ++count;
            }
        }
        return buffer.toString();
    }
    
    @Override
    public String getSchemaQuery() {
        return "SELECT SCHEMA_NAME()";
    }
    
    private Query getLeftMostQuery(Query leftQuery) {
        if (leftQuery instanceof UnionQuery) {
            final UnionQuery uq = (UnionQuery)leftQuery;
            leftQuery = uq.getLeftQuery();
            return this.getLeftMostQuery(leftQuery);
        }
        return leftQuery;
    }
    
    private void validateUnionQuery(Query leftQuery, final Query rightQuery) throws QueryConstructionException {
        leftQuery = this.getLeftMostQuery(leftQuery);
        if (leftQuery.getSortColumns() != null && leftQuery.getRange() != null && rightQuery.getSortColumns() != null && rightQuery.getRange() == null) {
            MssqlSQLGenerator.OUT.log(Level.SEVERE, "UNION operator must have an equal number of expressions in their target lists. Please check your right query!!! ");
            throw new QueryConstructionException("UNION operator must have an equal number of expressions in their target lists. Please check your right query!!! ");
        }
        if (leftQuery.getSortColumns() != null && leftQuery.getRange() == null && rightQuery.getSortColumns() != null && rightQuery.getRange() != null) {
            MssqlSQLGenerator.OUT.log(Level.SEVERE, "UNION operator must have an equal number of expressions in their target lists. Please check your left query!!! ");
            throw new QueryConstructionException("UNION operator must have an equal number of expressions in their target lists. Please check your left query!!! ");
        }
    }
    
    @Override
    protected String getUnionSQL(final Query query) throws QueryConstructionException {
        if (query instanceof UnionQuery) {
            final UnionQuery current = (UnionQuery)query;
            final Query leftQuery = current.getLeftQuery();
            final Query rightQuery = current.getRightQuery();
            this.validateUnionQuery(leftQuery, rightQuery);
            if (leftQuery.getSortColumns() != null && leftQuery.getRange() == null) {
                final List sort_cols = leftQuery.getSortColumns();
                for (int i = 0; i < sort_cols.size(); ++i) {
                    leftQuery.removeSortColumn(sort_cols.get(i));
                }
            }
            if (rightQuery.getSortColumns() != null && rightQuery.getRange() == null) {
                final List sort_cols = rightQuery.getSortColumns();
                for (int i = 0; i < sort_cols.size(); ++i) {
                    rightQuery.removeSortColumn(sort_cols.get(i));
                }
            }
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
                final String unionString = retainDuplicateRows ? " UNION ALL " : " UNION ";
                final String sqlString = leftSQL + unionString + rightSQL;
                MssqlSQLGenerator.OUT.log(Level.FINE, "Ansi92SQLGenerator.getSQLForSelect(): SQL formed is : {0}", sqlString);
                return sqlString;
            }
        }
        else if (query instanceof SelectQuery) {
            final SelectQuery sq = (SelectQuery)query;
            final Range range = sq.getRange();
            final String sqlString2 = this.getSQLForSelect(sq);
            return "(" + sqlString2 + ")";
        }
        return null;
    }
    
    @Override
    protected String getComparatorString(final Column column, final int comparator, final boolean isCaseSensitive) throws QueryConstructionException {
        if (column.getDefinition() != null) {
            String dataType = column.getDefinition().getDataType();
            if (DataTypeUtil.isEDT(dataType)) {
                dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
            }
            if (dataType.equalsIgnoreCase("CHAR") && column.getDefinition().getMaxLength() == -1) {
                if (comparator == 0) {
                    return super.getComparatorString(column, 2, isCaseSensitive);
                }
                if (comparator == 1) {
                    return super.getComparatorString(column, 3, isCaseSensitive);
                }
            }
        }
        return super.getComparatorString(column, comparator, isCaseSensitive);
    }
    
    @Override
    protected String escapeSquareBracket(String valueStr) throws QueryConstructionException {
        valueStr = valueStr.replace(String.valueOf('['), String.valueOf(this.getEscapeCharacter()) + '[');
        return valueStr;
    }
    
    @Override
    protected String processOrderByandRange(final String unionsql, final String orderbyclause, final Range range) throws QueryConstructionException {
        if (orderbyclause == null && range != null) {
            throw new QueryConstructionException("Range Query cannot construct without order by clause");
        }
        return super.processOrderByandRange(unionsql, orderbyclause, range);
    }
    
    protected String getSQLToGetDependentTableNamesFromTrigger(final String triggerName) {
        return "SELECT referenced_entity_name  FROM sys.sql_expression_dependencies AS sed INNER JOIN sys.objects AS o ON sed.referencing_id = o.object_id  WHERE referencing_id = OBJECT_ID(N'" + triggerName + "')";
    }
    
    public String getTriggerName(final String tableName) {
        return tableName + "_Trigger";
    }
    
    @Override
    protected void processCaseExpressionValue(final StringBuilder columnBuffer, final Boolean value) {
        columnBuffer.append(((boolean)value) ? 1 : 0);
    }
    
    public void setSupportFilteredUniqueIndex(final boolean b) {
        this.supportFilteredUniqueIndex = b;
    }
    
    public Mssql2005SQLGenerator getSql2005Generator() {
        return this.sql2005Gen;
    }
    
    static {
        OUT = Logger.getLogger(MssqlSQLGenerator.class.getName());
        MssqlSQLGenerator.isTextDataTypeDeprecated = false;
    }
    
    public class Mssql2005SQLGenerator
    {
        String getAlterColumnQuery(final AlterOperation ao) throws QueryConstructionException {
            final ColumnDefinition colDef = (ColumnDefinition)ao.getAlterObject();
            final String tableName = ao.getTableName();
            final String colName = colDef.getColumnName();
            final String defValConsName = MssqlSQLGenerator.this.getDefaultConstraintName(colName, ao.getDefaultValueConstraintName());
            final StringBuilder query = new StringBuilder();
            TableDefinition td = null;
            try {
                td = MetaDataUtil.getTableDefinitionByName(tableName);
            }
            catch (final MetaDataException mde) {
                throw new QueryConstructionException("Error fetching oldColDef", mde);
            }
            final ColumnDefinition oldColDef = td.getColumnDefinitionByName(colName);
            final PrimaryKeyDefinition pk = td.getPrimaryKey();
            final boolean isPKColumn = pk.getColumnList().contains(colName);
            if (isPKColumn) {
                colDef.setNullable(false);
                query.append("ALTER TABLE ").append(MssqlSQLGenerator.this.getDBSpecificTableName(tableName)).append(" ");
                query.append("DROP CONSTRAINT ");
                query.append(MssqlSQLGenerator.this.getConstraintName((ao.getActualConstraintName() == null) ? pk.getName() : ao.getActualConstraintName()));
                query.append(';');
            }
            final List<UniqueKeyDefinition> uniqueKeys = new ArrayList<UniqueKeyDefinition>();
            if (td.getUniqueKeys() != null) {
                for (final UniqueKeyDefinition uk : td.getUniqueKeys()) {
                    if (uk.getColumns().contains(colName)) {
                        uniqueKeys.add(uk);
                    }
                }
            }
            for (final UniqueKeyDefinition uk : uniqueKeys) {
                if (oldColDef.isNullable() && uk.getColumns().size() == 1) {
                    final String triggerName = tableName + "_" + uk.getName();
                    query.append(MssqlSQLGenerator.this.getSQLForDeleteTrigger(triggerName));
                    query.append(";");
                    query.append(MssqlSQLGenerator.this.getSQLForDropIndex(tableName, (ao.getActualConstraintName() == null) ? uk.getName() : ao.getActualConstraintName()));
                    query.append(";");
                }
                else {
                    query.append("ALTER TABLE ");
                    query.append(MssqlSQLGenerator.this.getDBSpecificTableName(tableName));
                    query.append(" DROP CONSTRAINT ");
                    query.append(MssqlSQLGenerator.this.getConstraintName((ao.getActualConstraintName() == null) ? uk.getName() : ao.getActualConstraintName()));
                    query.append(";");
                }
            }
            final List<IndexDefinition> indexes = new ArrayList<IndexDefinition>();
            if (td.getIndexes() != null) {
                for (final IndexDefinition idx : td.getIndexes()) {
                    if (idx.getColumns().contains(colName)) {
                        indexes.add(idx);
                    }
                }
            }
            for (final IndexDefinition idx : indexes) {
                query.append(MssqlSQLGenerator.this.getSQLForDropIndex(tableName, idx.getName()));
                query.append(";");
            }
            final Object oldDefVal = oldColDef.getDefaultValue();
            if (null != oldDefVal || defValConsName.equals(ao.getDefaultValueConstraintName())) {
                query.append(MssqlSQLGenerator.this.getSQLForDropDefValConstraint(tableName, defValConsName));
                query.append(';');
            }
            query.append("ALTER TABLE ").append(MssqlSQLGenerator.this.getDBSpecificTableName(tableName)).append(" ");
            query.append(" ALTER COLUMN ");
            query.append(MssqlSQLGenerator.this.getDBSpecificColumnName(colName));
            query.append(" ");
            MssqlSQLGenerator.this.appendColumnAttributesForAlter(ao.getOperationType(), colDef, query);
            query.append(" ");
            final Object newDefVal = colDef.getDefaultValue();
            if (null != newDefVal) {
                query.append(';');
                query.append(MssqlSQLGenerator.this.getSQLForAddDefValConstraint(tableName, colName, defValConsName, colDef.getDataType(), newDefVal));
                query.append(' ');
            }
            for (final UniqueKeyDefinition uk2 : uniqueKeys) {
                if (uk2.getColumns().size() > 1 || (uk2.getColumns().size() == 1 && !colDef.isNullable())) {
                    query.append(";ALTER TABLE ");
                    query.append(MssqlSQLGenerator.this.getDBSpecificTableName(tableName));
                    query.append(" ADD CONSTRAINT ");
                    query.append(MssqlSQLGenerator.this.getConstraintName(uk2.getName()));
                    query.append(" UNIQUE ");
                    Ansi92SQLGenerator.this.setColumnNamesFromList(uk2.getColumns(), query, null);
                    query.append(";");
                }
            }
            if (isPKColumn) {
                query.append(';');
                query.append("ALTER TABLE ").append(MssqlSQLGenerator.this.getDBSpecificTableName(tableName)).append(" ");
                query.append("ADD CONSTRAINT ");
                query.append(MssqlSQLGenerator.this.getConstraintName(pk.getName()));
                query.append(" PRIMARY KEY ");
                Ansi92SQLGenerator.this.setColumnNamesFromList(pk.getColumnList(), query, null);
                query.append(' ');
            }
            for (final IndexDefinition idx2 : indexes) {
                query.append(MssqlSQLGenerator.this.getSQLForIndex(td.getTableName(), idx2));
                query.append(";");
            }
            return query.toString();
        }
        
        String processUniqueKeys(final TableDefinition td) throws QueryConstructionException {
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
                if (!this.canCreateUniqueConstraint(td, ukd)) {
                    continue;
                }
                final List ukCols = ukd.getColumns();
                final int ukColSize = ukCols.size();
                final String ukColName = null;
                if (ukColSize == 1 && ukCols.get(0).equals(pkColumnName)) {
                    MssqlSQLGenerator.OUT.log(Level.WARNING, "A column cannot have a unique constraint as true, if it alone participates in the PKDefinition of that tableDefinition. TableName :: {0}, columnName :: {1}", new Object[] { pkDefn.getTableName(), pkColumnName });
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
                    buffer.append("CONSTRAINT ").append(MssqlSQLGenerator.this.getConstraintName(ukd.getName()));
                    buffer.append(" UNIQUE NONCLUSTERED ");
                    Ansi92SQLGenerator.this.addUniqueKeyName(buffer, ukd.getName());
                    buffer.append("(");
                    final Iterator cols = ukd.getColumns().iterator();
                    while (cols.hasNext()) {
                        final String name = cols.next();
                        buffer.append(MssqlSQLGenerator.this.getDBSpecificColumnName(name));
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
        
        public boolean isTriggerForUK(final AlterOperation alterOperation, final AlterTableQuery completeQuery) {
            if (4 != alterOperation.getOperationType() && 5 != alterOperation.getOperationType()) {
                return false;
            }
            final String tableName = alterOperation.getTableName();
            TableDefinition tDef = null;
            try {
                tDef = MetaDataUtil.getTableDefinitionByName(tableName);
            }
            catch (final MetaDataException ex) {
                throw new IllegalArgumentException(ex.getMessage(), ex);
            }
            UniqueKeyDefinition ukDef = null;
            if (alterOperation.getAlterObject() instanceof String) {
                final String ukName = (String)alterOperation.getAlterObject();
                ukDef = tDef.getUniqueKeyDefinitionByName(ukName);
            }
            else {
                ukDef = (UniqueKeyDefinition)alterOperation.getAlterObject();
            }
            if (ukDef == null || ukDef.getColumns().size() != 1) {
                return false;
            }
            ColumnDefinition cd = tDef.getColumnDefinitionByName(ukDef.getColumns().get(0));
            if (cd == null) {
                for (final AlterOperation ao : completeQuery.getAlterOperations()) {
                    if (ao.getOperationType() == 1) {
                        final ColumnDefinition colDef = (ColumnDefinition)ao.getAlterObject();
                        if (colDef.getColumnName().equals(ukDef.getColumns().get(0))) {
                            cd = colDef;
                            break;
                        }
                        continue;
                    }
                }
            }
            if (cd != null) {
                return cd.isNullable();
            }
            throw new IllegalArgumentException("Unknoun column name specified " + ukDef.getColumns().get(0) + " in UniqueKeyDefinition " + ukDef.getName());
        }
        
        public boolean canCreateUniqueConstraint(final TableDefinition td, final UniqueKeyDefinition ukd) {
            final List columns = ukd.getColumns();
            if (columns.size() > 1) {
                return true;
            }
            final String columnName = columns.get(0);
            final ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
            return cd != null && !cd.isNullable();
        }
        
        String getSQLCreateTriggerForUK(final String triggerName, final TableDefinition td, final UniqueKeyDefinition ukd, final boolean validate) {
            if (validate && this.canCreateUniqueConstraint(td, ukd)) {
                return null;
            }
            final String tableName = td.getTableName();
            final List columns = ukd.getColumns();
            final String columnName = columns.get(0);
            final ColumnDefinition cd = td.getColumnDefinitionByName(columnName);
            final StringBuilder buf = new StringBuilder("CREATE TRIGGER ");
            buf.append(triggerName);
            buf.append("\n");
            buf.append("\nON ");
            buf.append(tableName);
            buf.append("\n FOR UPDATE,INSERT ");
            buf.append("\nAS ");
            buf.append(" \nIF EXISTS");
            buf.append("\n(SELECT * ");
            buf.append("\nFROM Inserted AS I");
            buf.append("\nJOIN " + tableName + " AS S");
            buf.append("\nON I." + columnName + "= S." + columnName + " AND NOT(" + this.getPKCondition(td, "I", "S") + "))");
            buf.append("\nBEGIN");
            buf.append("\nROLLBACK TRAN");
            buf.append("\nRAISERROR('Duplicate values not permitted',16,1)");
            buf.append("\nEND");
            final String triggerStr = buf.toString();
            MssqlSQLGenerator.OUT.log(Level.FINER, "Trigger created for table {0} column {1} is {2}", new Object[] { tableName, columnName, triggerStr });
            return triggerStr;
        }
        
        private String getPKCondition(final TableDefinition td, final String lhsTableAlias, final String rhsTableAlias) {
            final PrimaryKeyDefinition pkd = td.getPrimaryKey();
            if (pkd == null) {
                return null;
            }
            final List columnNames = pkd.getColumnList();
            final StringBuilder pkCon = new StringBuilder();
            for (int i = 0; i < columnNames.size(); ++i) {
                final String columnName = columnNames.get(i);
                if (i > 0) {
                    pkCon.append(" AND ");
                }
                pkCon.append(" ( ");
                pkCon.append(lhsTableAlias);
                pkCon.append(".");
                pkCon.append(columnName);
                pkCon.append(" = ");
                pkCon.append(rhsTableAlias);
                pkCon.append(".");
                pkCon.append(columnName);
                pkCon.append(")");
            }
            return pkCon.toString();
        }
    }
}
