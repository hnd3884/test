package com.adventnet.ds.query;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.db.persistence.metadata.IndexColumnDefinition;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import com.adventnet.db.persistence.metadata.AllowedValues;
import java.util.logging.Level;
import java.util.Properties;
import java.util.Iterator;
import java.util.Collection;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.TableDefinition;
import java.util.List;
import java.util.logging.Logger;

public class AlterTableQueryImpl implements AlterTableQuery
{
    private static final Logger LOGGER;
    private boolean hasMultipleAlterations;
    private List<AlterOperation> alterOperations;
    private AlterOperation ao;
    private TableDefinition td;
    private String tableName;
    private boolean isExec;
    private boolean isValid;
    List<String> fkLocCols;
    List<String> fkRefCols;
    private boolean isRevert;
    
    public AlterTableQueryImpl(final String tableName) {
        this.hasMultipleAlterations = false;
        this.alterOperations = new ArrayList<AlterOperation>();
        this.ao = null;
        this.td = null;
        this.tableName = null;
        this.isExec = true;
        this.isValid = true;
        this.fkLocCols = new ArrayList<String>();
        this.fkRefCols = new ArrayList<String>();
        this.isRevert = false;
        this.tableName = tableName;
        this.hasMultipleAlterations = true;
    }
    
    @Deprecated
    public AlterTableQueryImpl(final String tableName, final int operationType) {
        this.hasMultipleAlterations = false;
        this.alterOperations = new ArrayList<AlterOperation>();
        this.ao = null;
        this.td = null;
        this.tableName = null;
        this.isExec = true;
        this.isValid = true;
        this.fkLocCols = new ArrayList<String>();
        this.fkRefCols = new ArrayList<String>();
        this.isRevert = false;
        this.tableName = tableName;
        this.alterOperations.add(new AlterOperation(tableName, operationType));
        this.ao = this.alterOperations.get(0);
        this.isValid = false;
    }
    
    @Deprecated
    public AlterTableQueryImpl(final String tableName, final String columnName, final int operationType) {
        this(tableName, operationType);
        this.setColumnName(columnName);
    }
    
    private void initTD(final String tableName) {
        try {
            final TableDefinition tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
            if (tabDef == null) {
                throw new IllegalArgumentException("No such tableName [" + tableName + "] defined");
            }
            this.td = tabDef;
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Exception occurred while fetching the TableDefinition for the tableName :: [" + tableName + "]", e);
        }
    }
    
    @Deprecated
    @Override
    public void setPrecision(final int decimalPlaces) {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                ((ColumnDefinition)this.ao.getAlterObject()).setPrecision(decimalPlaces);
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setPrecision(decimalPlaces) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public int getPrecision() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                return ((ColumnDefinition)this.ao.getAlterObject()).getPrecision();
            }
            default: {
                throw new IllegalArgumentException("getPrecision() method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Override
    public String getTableName() {
        return this.tableName;
    }
    
    @Deprecated
    @Override
    public int getOperationType() {
        this.checkForMultipleOperations();
        return this.alterOperations.get(0).getOperationType();
    }
    
    @Deprecated
    @Override
    public void setColumnName(final String columnName) {
        this.checkForMultipleOperations();
        checkString(columnName, "COLUMN_NAME", this.ao.getOperationType());
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                ((ColumnDefinition)this.ao.getAlterObject()).setColumnName(columnName);
                break;
            }
            case 3: {
                this.ao.alterObject = columnName;
                break;
            }
            case 12: {
                ((String[])this.ao.getAlterObject())[0] = columnName;
                break;
            }
            default: {
                throw new IllegalArgumentException("setColumnName(columnName) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
        this.isValid = false;
    }
    
    @Deprecated
    @Override
    public String getColumnName() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                return ((ColumnDefinition)this.ao.getAlterObject()).getColumnName();
            }
            case 3: {
                return (String)this.ao.getAlterObject();
            }
            case 12: {
                return ((String[])this.ao.getAlterObject())[0];
            }
            default: {
                throw new IllegalArgumentException("getColumnName() method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setNewColumnName(final String newColumnName) {
        this.checkForMultipleOperations();
        checkString(newColumnName, "NEW_COLUMN_NAME", this.ao.getOperationType());
        switch (this.ao.getOperationType()) {
            case 12: {
                ((String[])this.ao.getAlterObject())[1] = newColumnName;
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setNewColumnName(newColumnName) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public String getNewColumnName() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 12: {
                return ((String[])this.ao.getAlterObject())[1];
            }
            default: {
                throw new IllegalArgumentException("getNewColumnName(newColumnName) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setDataType(final String dataType) {
        this.checkForMultipleOperations();
        checkString(dataType, "DATA_TYPE", this.ao.getOperationType());
        MetaDataUtil.getJavaSQLType(dataType);
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                final ColumnDefinition cd = (ColumnDefinition)this.ao.getAlterObject();
                cd.setDataType(dataType);
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setDataType(dataType) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public String getDataType() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                return ((ColumnDefinition)this.ao.getAlterObject()).getDataType();
            }
            default: {
                throw new IllegalArgumentException("getDataType() method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setMaxLength(final int maxLength) {
        this.checkForMultipleOperations();
        validateMaxLength(maxLength);
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                ((ColumnDefinition)this.ao.getAlterObject()).setMaxLength(maxLength);
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setMaxLength(maxLength) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public int getMaxLength() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                return ((ColumnDefinition)this.ao.getAlterObject()).getMaxLength();
            }
            default: {
                throw new IllegalArgumentException("getMaxLength() method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setNullable(final boolean nullable) {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                ((ColumnDefinition)this.ao.getAlterObject()).setNullable(nullable);
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setNullable(nullable) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public boolean isNullable() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                return ((ColumnDefinition)this.ao.getAlterObject()).isNullable();
            }
            default: {
                throw new IllegalArgumentException("isNullable() method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setUnique(final boolean unique) {
        this.checkForMultipleOperations();
        if (unique) {
            switch (this.ao.getOperationType()) {
                case 2: {
                    throw new IllegalArgumentException("setUnique(true) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation, instead use modifyColumn(existingColName, newColumnDefinition, ukName) method.");
                }
                case 1: {
                    throw new IllegalArgumentException("setUnique(true) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation, instead use addColumn(newColumnDefinition, ukName) method.");
                }
            }
        }
    }
    
    @Deprecated
    @Override
    public boolean isUnique() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                return ((ColumnDefinition)this.ao.getAlterObject()).isUnique();
            }
            default: {
                throw new IllegalArgumentException("isUnique() method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setDefaultValue(final Object defaultValue) {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                Label_0104: {
                    try {
                        ((ColumnDefinition)this.ao.getAlterObject()).setDefaultValue(defaultValue);
                        break Label_0104;
                    }
                    catch (final MetaDataException mde) {
                        throw new IllegalArgumentException(mde);
                    }
                    break;
                }
                this.isValid = false;
                return;
            }
        }
        throw new IllegalArgumentException("setDefaultValue(defaultValue) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
    }
    
    @Deprecated
    @Override
    public Object getDefaultValue() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                return ((ColumnDefinition)this.ao.getAlterObject()).getDefaultValue();
            }
            default: {
                throw new IllegalArgumentException("getDefaultValue() method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setConstraintName(final String constraintName) {
        this.checkForMultipleOperations();
        checkString(constraintName, "CONSTRAINT_NAME", this.ao.getOperationType());
        switch (this.ao.getOperationType()) {
            case 9: {
                ((PrimaryKeyDefinition)this.ao.getAlterObject()).setName(constraintName);
                break;
            }
            case 6: {
                ((ForeignKeyDefinition)this.ao.getAlterObject()).setName(constraintName);
                break;
            }
            case 4: {
                ((UniqueKeyDefinition)this.ao.getAlterObject()).setName(constraintName);
                break;
            }
            case 10: {
                ((IndexDefinition)this.ao.getAlterObject()).setName(constraintName);
                break;
            }
            case 5:
            case 7:
            case 8:
            case 11: {
                this.ao.alterObject = constraintName;
                break;
            }
            case 1: {
                throw new UnsupportedOperationException("This API is no longer supported, Please use the alternate API addColumn(newColDef, constraintName)");
            }
            case 2: {
                throw new UnsupportedOperationException("This API is no longer supported, Please use the alternate API modifyColumn(columnName, newColDef, constraintName)");
            }
            default: {
                throw new IllegalArgumentException("setConstraintName cannot be used/invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
        this.isValid = false;
    }
    
    @Deprecated
    @Override
    public void setFKConstraint(final int fkConstraint) {
        this.checkForMultipleOperations();
        validateFKConstraint(fkConstraint);
        switch (this.ao.getOperationType()) {
            case 6: {
                ((ForeignKeyDefinition)this.ao.getAlterObject()).setConstraints(fkConstraint);
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setFKConstraints(fkConstraint) cannot be invoked/used for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    private void addFKColDefs() {
        if (this.fkLocCols.size() > 0 && this.fkLocCols.size() == this.fkRefCols.size()) {
            final ForeignKeyDefinition fkDef = (ForeignKeyDefinition)this.ao.getAlterObject();
            for (int i = 0; i < this.fkLocCols.size(); ++i) {
                final ColumnDefinition lc = new ColumnDefinition();
                lc.setColumnName(this.fkLocCols.get(i));
                final ColumnDefinition rc = new ColumnDefinition();
                rc.setColumnName(this.fkRefCols.get(i));
                final ForeignKeyColumnDefinition fkcd = new ForeignKeyColumnDefinition();
                fkcd.setLocalColumnDefinition(lc);
                fkcd.setReferencedColumnDefinition(rc);
                fkDef.addForeignKeyColumns(fkcd);
            }
            return;
        }
        throw new IllegalArgumentException("Either/Both FKLocalColumn [" + this.fkLocCols + "] and FKReferenceColumns [" + this.fkRefCols + "] is not set (or) the number of columns in both the lists are of different sizes.");
    }
    
    @Deprecated
    @Override
    public void setFKLocalColumns(final List<String> localColumnNames) {
        this.checkForMultipleOperations();
        validateColNames(localColumnNames, "FK_LOCAL_COLUMNS", this.ao.getOperationType());
        switch (this.ao.getOperationType()) {
            case 6: {
                if (this.fkLocCols.size() > 0) {
                    throw new IllegalArgumentException("FKLocalColumns has already been set as " + this.fkLocCols + ", hence cannot be reset");
                }
                this.fkLocCols.addAll(localColumnNames);
                if (this.fkRefCols.size() > 0) {
                    this.addFKColDefs();
                }
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setFKConstraints(fkConstraint) cannot be invoked/used for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setFKMasterTableName(final String masterTableName) {
        this.checkForMultipleOperations();
        checkString(masterTableName, "MASTER_TABLE_NAME", this.ao.getOperationType());
        switch (this.ao.getOperationType()) {
            case 6: {
                ((ForeignKeyDefinition)this.ao.getAlterObject()).setMasterTableName(masterTableName);
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setMasterTableName(masterTableName) cannot be invoked/used for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setFKReferenceColumns(final List<String> referenceColumnNames) {
        this.checkForMultipleOperations();
        validateColNames(referenceColumnNames, "FK_REFERENCE_COLUMNS", this.ao.getOperationType());
        switch (this.ao.getOperationType()) {
            case 6: {
                if (this.fkRefCols.size() > 0) {
                    throw new IllegalArgumentException("FKRefCols has already been set as " + this.fkRefCols + ", hence cannot be reset.");
                }
                this.fkRefCols.addAll(referenceColumnNames);
                if (this.fkLocCols.size() > 0) {
                    this.addFKColDefs();
                }
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setFKConstraints(fkConstraint) cannot be invoked/used for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    static void validateFKConstraint(final int fkConstraint) {
        switch (fkConstraint) {
            case 0:
            case 1:
            case 2:
            case 3: {
                return;
            }
            default: {
                throw new IllegalArgumentException("A ForeignKey constraint can be of only 4 types, check the javadoc of ForeignKeyDefinition");
            }
        }
    }
    
    static void validateColNames(final List<String> colNames, final String msg, final int operationType) {
        if (colNames == null || colNames.isEmpty()) {
            throw new IllegalArgumentException("The " + msg + " in the AlterOperation " + getOperationString(operationType) + " cannot be null or empty.");
        }
        for (final String cName : colNames) {
            if (cName == null || cName.trim().length() == 0) {
                throw new IllegalArgumentException("A Column name in the " + msg + " in the AlterOperation " + getOperationString(operationType) + " cannot be null or empty");
            }
        }
    }
    
    static void validateMaxLength(final int maxLength) {
        if (maxLength <= 0) {
            throw new IllegalArgumentException("MaxLength of a column cannot be less than or equal to zero");
        }
    }
    
    static void checkString(final String s, final String msg, final int operationType) {
        if (s == null || s.trim().length() == 0) {
            throw new IllegalArgumentException("Invalid value [" + s + "] set for [" + msg + "] in the alter operation [" + getOperationString(operationType) + "]");
        }
    }
    
    @Override
    public void validate() throws QueryConstructionException {
        if (this.alterOperations.isEmpty()) {
            throw new IllegalArgumentException("No AlterOperations has been performed in this AlterTableQuery object :: " + this.toString());
        }
        if (this.isValid) {
            return;
        }
        this.initTD(this.tableName);
        if (this.hasMultipleAlterations) {
            for (int index = 0; index < this.alterOperations.size(); ++index) {
                final AlterOperation alterOperation = this.alterOperations.get(index);
                alterOperation.validate();
                switch (alterOperation.getOperationType()) {
                    case 3: {
                        if (this.td != null && this.td.getColumnDefinitionByName((String)alterOperation.getAlterObject()) != null && this.td.getColumnDefinitionByName((String)alterOperation.getAlterObject()).isDynamic()) {
                            throw new IllegalArgumentException("Cannot delete dynamic columns via AlterTableQuery.DELETE_COLUMN");
                        }
                        final int addedAoperations = this.handleConstraints((String)alterOperation.getAlterObject(), alterOperation.isConstarintsNeedToBeDropped());
                        index += addedAoperations;
                        break;
                    }
                    case 20: {
                        if (this.td != null && this.td.getColumnDefinitionByName((String)alterOperation.getAlterObject()) != null && !this.td.getColumnDefinitionByName((String)alterOperation.getAlterObject()).isDynamic()) {
                            throw new IllegalArgumentException("Cannot delete normal columns via AlterTableQuery.DELETE_DYNAMIC_COLUMN");
                        }
                        final int addedAoperations = this.handleConstraints((String)alterOperation.getAlterObject(), alterOperation.isConstarintsNeedToBeDropped());
                        index += addedAoperations;
                        break;
                    }
                    case 4: {
                        this.checkForDuplicateUK((UniqueKeyDefinition)alterOperation.getAlterObject());
                        break;
                    }
                    case 7: {
                        this.validateForRemoveFK((String)alterOperation.getAlterObject());
                        break;
                    }
                    case 8: {
                        final String delPKName = (String)alterOperation.getAlterObject();
                        this.validateForDelPK(delPKName);
                        break;
                    }
                    case 9: {
                        this.validateForAddPK((PrimaryKeyDefinition)alterOperation.getAlterObject());
                        break;
                    }
                    case 1: {
                        if (((ColumnDefinition)alterOperation.getAlterObject()).isDynamic()) {
                            throw new IllegalArgumentException("Cannot add dynamic columns via AlterTableQuery.ADD_COLUMN");
                        }
                        this.validateIsUnique((ColumnDefinition)alterOperation.getAlterObject());
                        break;
                    }
                    case 19: {
                        ((ColumnDefinition)alterOperation.getAlterObject()).setDynamic(true);
                        this.validateIsUnique((ColumnDefinition)alterOperation.getAlterObject());
                        break;
                    }
                    case 14: {
                        final String fkName = ((ForeignKeyDefinition)alterOperation.getAlterObject()).getName();
                        if (this.td.getForeignKeyDefinitionByName(fkName) == null) {
                            throw new IllegalArgumentException("No foreign-key exists with name \"" + fkName + "\" to modify in table \"" + this.tableName + "\".");
                        }
                        break;
                    }
                    case 16: {
                        final String idxName = ((IndexDefinition)alterOperation.getAlterObject()).getName();
                        if (this.td.getIndexDefinitionByName(idxName) == null) {
                            throw new IllegalArgumentException("No Index exists with name \"" + idxName + "\" to modify in table \"" + this.tableName + "\".");
                        }
                        break;
                    }
                    case 15: {
                        final String ukName = ((UniqueKeyDefinition)alterOperation.getAlterObject()).getName();
                        if (this.td.getUniqueKeyDefinitionByName(ukName) == null) {
                            throw new IllegalArgumentException("No Unique-key exists with name \"" + ukName + "\" to modify in table \"" + this.tableName + "\".");
                        }
                        break;
                    }
                    case 2:
                    case 21: {
                        final ColumnDefinition modifiedColumnDefinition = (ColumnDefinition)alterOperation.getAlterObject();
                        final ColumnDefinition oldColumnDefinition = this.td.getColumnDefinitionByName(modifiedColumnDefinition.getColumnName());
                        if (oldColumnDefinition == null) {
                            throw new QueryConstructionException("No column with this name [" + modifiedColumnDefinition.getColumnName() + "] found in this table [" + this.td.getTableName() + "]");
                        }
                        this.validateModifyColumn(oldColumnDefinition, modifiedColumnDefinition, alterOperation);
                        break;
                    }
                    case 18: {
                        final Properties tableProp = (Properties)alterOperation.getAlterObject();
                        this.validateTableAttributeChange(tableProp);
                        break;
                    }
                }
            }
        }
        else {
            switch (this.ao.getOperationType()) {
                case 6:
                case 12: {
                    this.ao.validate();
                    break;
                }
                case 4: {
                    this.ao.validate();
                    this.checkForDuplicateUK((UniqueKeyDefinition)this.ao.getAlterObject());
                    break;
                }
                case 7: {
                    this.ao.validate();
                    this.validateForRemoveFK((String)this.ao.getAlterObject());
                    break;
                }
                case 3: {
                    this.ao.validate();
                    this.handleConstraints((String)this.ao.getAlterObject(), this.ao.isConstarintsNeedToBeDropped());
                    break;
                }
                case 1: {
                    this.ao.validate();
                    this.validateIsUnique((ColumnDefinition)this.ao.getAlterObject());
                    break;
                }
                case 2: {
                    this.ao.validate();
                    final ColumnDefinition modifiedColumnDefinition2 = (ColumnDefinition)this.ao.getAlterObject();
                    final ColumnDefinition oldColumnDefinition2 = this.td.getColumnDefinitionByName(modifiedColumnDefinition2.getColumnName());
                    if (oldColumnDefinition2 == null) {
                        throw new QueryConstructionException("No column with this name [" + modifiedColumnDefinition2.getColumnName() + "] found in this table [" + this.td.getTableName() + "]");
                    }
                    this.validateModifyColumn(oldColumnDefinition2, modifiedColumnDefinition2, this.ao);
                    break;
                }
                default: {
                    this.ao.validate();
                    break;
                }
            }
        }
        this.isValid = true;
    }
    
    private void validateModifyColumn(final ColumnDefinition oldColumnDefinition, final ColumnDefinition modifiedColumnDefinition, final AlterOperation ao) throws QueryConstructionException {
        if (!oldColumnDefinition.isUnique() && modifiedColumnDefinition.isUnique()) {
            this.validateIsUnique(modifiedColumnDefinition);
        }
        if (oldColumnDefinition.getUniqueValueGeneration() != null && modifiedColumnDefinition.getUniqueValueGeneration() != null) {
            final String oldGeneratorName = oldColumnDefinition.getUniqueValueGeneration().getGeneratorName();
            final String newGeneratorName = modifiedColumnDefinition.getUniqueValueGeneration().getGeneratorName();
            if (!oldGeneratorName.equals(newGeneratorName)) {
                ao.isGeneratorNameNeedToBeRenamed = true;
                checkString(newGeneratorName, "NEW_GENERATOR_NAME", ao.getOperationType());
            }
        }
    }
    
    private void validateIsUnique(final ColumnDefinition cd) throws QueryConstructionException {
        if (cd.getColumnName().equalsIgnoreCase("DYJSONCOL")) {
            throw new IllegalArgumentException("Column Name cannot be DYJSONCOL. This name is used for internal purpose");
        }
        if (cd.isUnique()) {
            cd.setUnique(false);
            final List<UniqueKeyDefinition> ukDefs = this.td.getUniqueKeys();
            int uks = (ukDefs == null) ? 0 : ukDefs.size();
            final UniqueKeyDefinition ukDef = new UniqueKeyDefinition();
            String ukName;
            for (ukName = this.tableName + "_UK"; this.td.getUniqueKeyDefinitionByName(ukName + uks) != null; ++uks) {}
            ukName += uks;
            ukDef.setName(ukName);
            ukDef.addColumn(cd.getColumnName());
            this.addUniqueKey(ukDef);
        }
    }
    
    private void validateTableAttributeChange(final Properties tableProp) throws QueryConstructionException {
        for (final String key : ((Hashtable<Object, V>)tableProp).keySet()) {
            if (!key.equals("description") && !key.equals("display-name") && !key.equals("createtable") && !key.equals("template") && !key.equals("modulename") && !key.equals("dc-type")) {
                throw new QueryConstructionException("Unknown attribute [\"" + key + "\"], is marked for modification in table properties.");
            }
        }
        if (tableProp.containsKey("createtable") || tableProp.containsKey("template") || tableProp.containsKey("dc-type")) {
            if (!this.isExec) {
                AlterTableQueryImpl.LOGGER.log(Level.SEVERE, "Changes in table''s {0} attribute requires DB level changes, but isExecutable attribute is set as false in the constructed AlterTable query.", tableProp.keySet());
            }
        }
        else if (this.isExec) {
            throw new QueryConstructionException("Since changes in table's " + tableProp.keySet() + " attribute won't require any DB level changes, set isExecutable attribute as false in the constructed AlterTable query.");
        }
    }
    
    @Deprecated
    @Override
    public String getConstraintName() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 9: {
                return ((PrimaryKeyDefinition)this.ao.getAlterObject()).getName();
            }
            case 8: {
                return (String)this.ao.getAlterObject();
            }
            case 6: {
                return ((ForeignKeyDefinition)this.ao.getAlterObject()).getName();
            }
            case 7: {
                return (String)this.ao.getAlterObject();
            }
            case 4: {
                return ((UniqueKeyDefinition)this.ao.getAlterObject()).getName();
            }
            case 5: {
                return (String)this.ao.getAlterObject();
            }
            case 10: {
                return ((IndexDefinition)this.ao.getAlterObject()).getName();
            }
            case 11: {
                return (String)this.ao.getAlterObject();
            }
            default: {
                throw new IllegalArgumentException("getConstraintName() cannot be used/invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public int getFKConstraint() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 6: {
                return ((ForeignKeyDefinition)this.ao.getAlterObject()).getConstraints();
            }
            default: {
                throw new IllegalArgumentException("getFKConstraint() cannot be used/invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public List<String> getFKLocalColumns() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 6: {
                return this.fkLocCols;
            }
            default: {
                throw new IllegalArgumentException("getFKLocalColumns() cannot be used/invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public List<String> getFKReferenceColumns() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 6: {
                return this.fkRefCols;
            }
            default: {
                throw new IllegalArgumentException("getFKReferenceColumns() cannot be used/invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public String getFKMasterTableName() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 6: {
                return ((ForeignKeyDefinition)this.ao.getAlterObject()).getMasterTableName();
            }
            default: {
                throw new IllegalArgumentException("getMasterTableName() cannot be used/invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public List<String> getUniqueCols() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 4: {
                return ((UniqueKeyDefinition)this.ao.getAlterObject()).getColumns();
            }
            default: {
                throw new IllegalArgumentException("getUniqueCols() cannot be used/invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setUniqueCols(final List<String> columnNames) {
        this.checkForMultipleOperations();
        validateColNames(columnNames, "UNIQUE_COLS", this.ao.getOperationType());
        switch (this.ao.getOperationType()) {
            case 4: {
                for (final String columnName : columnNames) {
                    ((UniqueKeyDefinition)this.ao.getAlterObject()).addColumn(columnName);
                }
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("getUniqueCols() cannot be used/invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Override
    public boolean isValid() {
        return this.isValid;
    }
    
    @Deprecated
    @Override
    public boolean isBidirectional() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 6: {
                return ((ForeignKeyDefinition)this.ao.getAlterObject()).isBidirectional();
            }
            default: {
                throw new IllegalArgumentException("isBidirectional() cannot be used/invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setBidirectional(final boolean isbidirectional) {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 6: {
                ((ForeignKeyDefinition)this.ao.getAlterObject()).setBidirectional(isbidirectional);
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setBidirectional() cannot be used/invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public AllowedValues getAllowedValues() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                return ((ColumnDefinition)this.ao.getAlterObject()).getAllowedValues();
            }
            default: {
                throw new IllegalArgumentException("getAllowedValues() method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setAllowedValues(final AllowedValues alValues) {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                ((ColumnDefinition)this.ao.getAlterObject()).setAllowedValues(alValues);
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setAllowedValues(allowedValues) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public String getDisplayName() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                return ((ColumnDefinition)this.ao.getAlterObject()).getDisplayName();
            }
            default: {
                throw new IllegalArgumentException("getDisplayName() method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setDisplayName(final String dispName) {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                ((ColumnDefinition)this.ao.getAlterObject()).setDisplayName(dispName);
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setDisplayName(dispName) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public UniqueValueGeneration getUniqueValueGeneration() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                return ((ColumnDefinition)this.ao.getAlterObject()).getUniqueValueGeneration();
            }
            default: {
                throw new IllegalArgumentException("getUniqueValueGeneration() method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setUniqueValueGeneration(final UniqueValueGeneration uvg) {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                ((ColumnDefinition)this.ao.getAlterObject()).setUniqueValueGeneration(uvg);
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setUniqueValueGeneration(uvg) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    public static String getOperationString(final int operationType) {
        switch (operationType) {
            case 1: {
                return "ADD COLUMN";
            }
            case 2: {
                return "MODIFY COLUMN";
            }
            case 3: {
                return "DELETE COLUMN";
            }
            case 6: {
                return "ADD FOREIGN KEY";
            }
            case 7: {
                return "DELETE FOREIGN KEY";
            }
            case 4: {
                return "ADD UNIQUE KEY";
            }
            case 5: {
                return "DELETE UNIQUE KEY";
            }
            case 9: {
                return "ADD PRIMARY KEY";
            }
            case 8: {
                return "DELETE PRIMARY KEY";
            }
            case 12: {
                return "RENAME COLUMN";
            }
            case 13: {
                return "RENAME TABLE";
            }
            case 10: {
                return "ADD INDEX";
            }
            case 11: {
                return "DELETE_INDEX";
            }
            case 14: {
                return "MODIFY FOREIGN KEY";
            }
            case 16: {
                return "MODIFY INDEX";
            }
            case 17: {
                return "MODIFY PRIMARY KEY";
            }
            case 15: {
                return "MODIFY UNIQUE KEY";
            }
            case 18: {
                return "MODIFY TABLE ATTRIBUTES";
            }
            case 19: {
                return "ADD DYNAMIC COLUMN";
            }
            case 21: {
                return "MODIFY DYNAMIC COLUMN";
            }
            case 20: {
                return "DELETE DYNAMIC COLUMN";
            }
            case 22: {
                return "RENAME DYNAMIC COLUMN";
            }
            default: {
                return "NONE";
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("\n<AlterTableQuery TABLE_NAME=\"");
        buffer.append(this.tableName);
        buffer.append("\">");
        int index = 1;
        for (final AlterOperation alterOperation : this.alterOperations) {
            buffer.append("\n\t<AlterOperation-");
            buffer.append(index++);
            buffer.append(" ");
            buffer.append(alterOperation);
            buffer.append("/>");
        }
        buffer.append("\n</AlterTableQuery>");
        return buffer.toString();
    }
    
    @Deprecated
    @Override
    public String getDescription() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                return ((ColumnDefinition)this.ao.getAlterObject()).getDescription();
            }
            default: {
                throw new IllegalArgumentException("getDescription() method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setDescription(final String descrip) {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 1:
            case 2: {
                ((ColumnDefinition)this.ao.getAlterObject()).setDescription(descrip);
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setDescription(descrip) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setIndexColumns(final List<String> columnNames) {
        this.checkForMultipleOperations();
        validateColNames(columnNames, "INDEX_COLUMNS", 10);
        switch (this.ao.getOperationType()) {
            case 10: {
                for (final String columnName : columnNames) {
                    final ColumnDefinition cd = new ColumnDefinition();
                    cd.setColumnName(columnName);
                    ((IndexDefinition)this.ao.getAlterObject()).addIndexColumnDefinition(new IndexColumnDefinition(cd));
                }
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setIndexColumns(columnNames) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public List<String> getIndexColumns() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 10: {
                return ((IndexDefinition)this.ao.getAlterObject()).getColumns();
            }
            default: {
                throw new IllegalArgumentException("getIndexColumns() method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public List<String> getPKColumns() {
        this.checkForMultipleOperations();
        switch (this.ao.getOperationType()) {
            case 9: {
                return ((PrimaryKeyDefinition)this.ao.getAlterObject()).getColumnList();
            }
            default: {
                throw new IllegalArgumentException("getPKColumns() method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Deprecated
    @Override
    public void setPKColumns(final List<String> columnNames) {
        this.checkForMultipleOperations();
        validateColNames(columnNames, "PK_COLUMN_NAMES", 9);
        switch (this.ao.getOperationType()) {
            case 9: {
                for (final String columnName : columnNames) {
                    ((PrimaryKeyDefinition)this.ao.getAlterObject()).addColumnName(columnName);
                }
                this.isValid = false;
                return;
            }
            default: {
                throw new IllegalArgumentException("setPKColumns(columnNames) method cannot be invoked for [" + getOperationString(this.ao.getOperationType()) + "] operation");
            }
        }
    }
    
    @Override
    public void setIsExecutable(final boolean isExecutable) {
        this.isExec = isExecutable;
    }
    
    @Override
    public boolean isExecutable() {
        return this.isExec;
    }
    
    @Override
    public void addColumn(final ColumnDefinition colDef) throws QueryConstructionException {
        this.checkIfSupported("addColumn(colDef)");
        if (colDef.isUnique()) {
            throw new IllegalArgumentException("To Add a unique column, use the alternate addColumn(colDef, uniqueKeyName) API");
        }
        colDef.setTableName(this.tableName);
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 1, colDef);
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void addColumn(final ColumnDefinition colDef, final String ukName) throws QueryConstructionException {
        this.checkIfSupported("addColumn(colDef, ukName)");
        if (colDef.isUnique()) {
            colDef.setUnique(false);
            this.addColumn(colDef);
            final UniqueKeyDefinition ukDef = new UniqueKeyDefinition();
            ukDef.setName(ukName);
            ukDef.addColumn(colDef.getColumnName());
            this.addUniqueKey(ukDef);
        }
        else {
            AlterTableQueryImpl.LOGGER.log(Level.WARNING, "To add a non-unique column, use the alternate addColumn(colDef) API.");
            this.addColumn(colDef);
        }
        this.isValid = false;
    }
    
    @Override
    public void renameColumn(final String existingColumnName, final String newColumnName) throws QueryConstructionException {
        this.checkIfSupported("renameColumn(existingColumnName, newColumnName)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 12, new String[] { existingColumnName, newColumnName });
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void renameDynamicColumn(final String existingColumnName, final String newColumnName) throws QueryConstructionException {
        this.checkIfSupported("renameColumn(existingColumnName, newColumnName)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 22, new String[] { existingColumnName, newColumnName });
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    private int handleConstraints(final String columnName, final boolean dropConstraints) throws QueryConstructionException {
        int extraOperations = 0;
        if (this.td.getPrimaryKey().getColumnList().contains(columnName)) {
            throw new UnsupportedOperationException("Column(s) [" + columnName + "] participating in a Primary key cannot be dropped.");
        }
        if (this.td.getIndexes() != null) {
            for (final IndexDefinition idx : this.td.getIndexes()) {
                if (idx.getColumns().contains(columnName)) {
                    if (!dropConstraints) {
                        throw new QueryConstructionException("An Index [" + idx + "] is defined based on this column. This column cannot be dropped unless its dependent constraints/indexes are dropped.");
                    }
                    this.dropIndex(idx.getName());
                    ++extraOperations;
                }
            }
        }
        for (final ForeignKeyDefinition fkd : this.td.getForeignKeyList()) {
            if (fkd.getFkColumns().contains(columnName)) {
                if (!dropConstraints) {
                    throw new QueryConstructionException("A ForeignKey [" + fkd + "] is defined based on this column. This column cannot be dropped unless its dependent constraints/indexes are dropped.");
                }
                this.removeForeignKey(fkd.getName());
                ++extraOperations;
            }
        }
        if (this.td.getUniqueKeys() != null) {
            for (final UniqueKeyDefinition uk : this.td.getUniqueKeys()) {
                if (uk.getColumns().contains(columnName)) {
                    if (!dropConstraints) {
                        throw new QueryConstructionException("An UniqueKey [" + uk + "] is defined based on this column. This column cannot be dropped unless its dependent constraints/indexes are dropped.");
                    }
                    this.removeUniqueKey(uk.getName());
                    ++extraOperations;
                }
            }
        }
        return extraOperations;
    }
    
    @Override
    public void removeColumn(final String columnName) throws QueryConstructionException {
        this.removeColumn(columnName, false);
    }
    
    @Override
    public void removeColumn(final String columnName, final boolean dropDependentConstraints) throws QueryConstructionException {
        this.checkIfSupported("removeColumn(columnName)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 3, columnName);
        alterOperation.setIsConstraintNeedToBeDropped(dropDependentConstraints);
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void modifyColumn(final String columnName, final ColumnDefinition newColDef) throws QueryConstructionException {
        this.checkIfSupported("modifyColumn(columnName, newColDef)");
        if (columnName != null && columnName.equals(newColDef.getColumnName())) {
            newColDef.setTableName(this.tableName);
            final AlterOperation alterOperation = new AlterOperation(this.tableName, 2, newColDef);
            this.alterOperations.add(alterOperation);
            this.isValid = false;
            return;
        }
        throw new IllegalArgumentException("Given columnName [" + columnName + "] and the columnName in newColumnDefinition [" + newColDef + "] should be same.");
    }
    
    @Override
    public void modifyColumn(final String columnName, final ColumnDefinition newColDef, final String ukName) throws QueryConstructionException {
        this.checkIfSupported("modifyColumn(columnName, newColDef, ukName)");
        if (columnName != null && columnName.equals(newColDef.getColumnName())) {
            this.modifyColumn(columnName, newColDef);
            if (newColDef.isUnique()) {
                final UniqueKeyDefinition ukDef = new UniqueKeyDefinition();
                ukDef.setName(ukName);
                ukDef.addColumn(columnName);
                this.addUniqueKey(ukDef);
            }
            else {
                AlterTableQueryImpl.LOGGER.log(Level.WARNING, "Since the uniqueness is set to false in newColumnDefinition [" + newColDef + "], the given ukName [" + ukName + "] is not used.");
            }
            this.isValid = false;
            return;
        }
        throw new IllegalArgumentException("Given columnName [" + columnName + "] and the columnName in newColumnDefinition [" + newColDef + "] should be same.");
    }
    
    private void validateForDelPK(final String delPKName) throws QueryConstructionException {
        if (!this.td.getPrimaryKey().getName().equals(delPKName)) {
            throw new QueryConstructionException("The constraint-name given in the modify PK :: [" + delPKName + "] should be same as that in the TableDefinition's PK :: [" + this.td.getPrimaryKey() + "]");
        }
        try {
            final List fks = MetaDataUtil.getReferringForeignKeyDefinitions(this.tableName);
            if (fks != null) {
                for (final Object o : fks) {
                    final ForeignKeyDefinition fk = (ForeignKeyDefinition)o;
                    for (final String colName : this.td.getPrimaryKey().getColumnList()) {
                        if (fk.getFkRefColumns().contains(colName)) {
                            throw new QueryConstructionException("This column [" + colName + "] is being referred by this FK [" + fk + "], hence this PK cannot be modified.");
                        }
                    }
                }
            }
        }
        catch (final MetaDataException mde) {
            throw new QueryConstructionException("Exception occurred while validating for modifyPrimaryKey");
        }
    }
    
    private void validateForAddPK(final PrimaryKeyDefinition pkDef) throws QueryConstructionException {
        if (!this.td.getPrimaryKey().getName().equals(pkDef.getName())) {
            throw new QueryConstructionException("The constraint-name given in the modify PK :: [" + pkDef.getName() + "] should be same as that in the TableDefinition's PK :: [" + this.td.getPrimaryKey() + "]");
        }
        validateColNames(pkDef.getColumnList(), "PRIMARY_KEY_COLUMN_NAMES", 8);
    }
    
    @Override
    public void addPrimaryKey(final PrimaryKeyDefinition pkDef) throws QueryConstructionException {
        this.checkIfSupported("addPrimaryKey(pkDef)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 9, pkDef);
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void removePrimaryKey(final String oldPKName) throws QueryConstructionException {
        this.checkIfSupported("removePrimaryKey(pkDef)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 8, oldPKName);
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void modifyPrimaryKey(final String oldPKName, final PrimaryKeyDefinition pkDef) throws QueryConstructionException {
        this.checkIfSupported("modifyPrimaryKey(pkDef)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 17, new Object[] { oldPKName, pkDef });
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void modifyForeignKey(final ForeignKeyDefinition fkDef) {
        this.checkIfSupported("modifyForeignKey(fkDef)");
        fkDef.setSlaveTableName(this.tableName);
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 14, fkDef);
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void modifyUniqueKey(final UniqueKeyDefinition ukDef) {
        this.checkIfSupported("modifyUniqueKey(ukDef)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 15, ukDef);
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void modifyIndex(final IndexDefinition idxDef) {
        this.checkIfSupported("modifyIndex(idxDef)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 16, idxDef);
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    private void checkForDuplicateUK(final UniqueKeyDefinition ukDef) throws QueryConstructionException {
        if (this.isSameElements(this.td.getPrimaryKey().getColumnList(), ukDef.getColumns())) {
            throw new QueryConstructionException("Primary Key is defined for the same set of columns, hence this UK cannot be added :: " + ukDef);
        }
        if (this.td.getUniqueKeys() != null) {
            for (final UniqueKeyDefinition uk : this.td.getUniqueKeys()) {
                if (this.isSameElements(uk.getColumns(), ukDef.getColumns())) {
                    throw new QueryConstructionException("Already a Unique Key [" + uk + "] is defined for the same set of columns, hence this UK cannot be added :: " + ukDef);
                }
            }
        }
    }
    
    private boolean isSameElements(final List<String> cols1, final List<String> cols2) {
        if (cols1.size() != cols2.size()) {
            return false;
        }
        for (int index = 0; index < cols1.size(); ++index) {
            if (!cols1.get(index).equals(cols2.get(index))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void addUniqueKey(final UniqueKeyDefinition ukDef) throws QueryConstructionException {
        this.checkIfSupported("addUniqueKey(ukDef)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 4, ukDef);
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void removeUniqueKey(final String ukName) throws QueryConstructionException {
        this.checkIfSupported("removeUniqueKey(ukName)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 5, ukName);
        this.alterOperations.add(0, alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void addForeignKey(final ForeignKeyDefinition fkDef) throws QueryConstructionException {
        this.checkIfSupported("addForeignKey(fkDef)");
        fkDef.setSlaveTableName(this.tableName);
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 6, fkDef);
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    private void validateForRemoveFK(final String fkName) throws QueryConstructionException {
        try {
            if (PersonalityConfigurationUtil.isFKPartOfPersonality(fkName)) {
                throw new QueryConstructionException("This foreign key cannot be removed, since the ForeignKey [" + fkName + "] of the table [" + this.tableName + "] is participating in the personality.");
            }
        }
        catch (final DataAccessException dae) {
            throw new QueryConstructionException("Exception occurred while check whether this foreignkey participates in any personality", dae);
        }
    }
    
    @Override
    public void removeForeignKey(final String fkName) throws QueryConstructionException {
        this.checkIfSupported("removeForeignKey(fkName)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 7, fkName);
        this.alterOperations.add(0, alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void addIndex(final IndexDefinition idxDef) throws QueryConstructionException {
        this.checkIfSupported("addIndex(idxDef)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 10, idxDef);
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void dropIndex(final String idxName) throws QueryConstructionException {
        this.checkIfSupported("dropIndex(idxName)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 11, idxName);
        this.alterOperations.add(0, alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void renameTable(final String newTableName) throws QueryConstructionException {
        this.checkIfSupported("renameTable(newTableName)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 13, newTableName);
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void setRevertFlag(final boolean revertFlag) {
        this.isRevert = revertFlag;
    }
    
    @Override
    public boolean isRevert() {
        return this.isRevert;
    }
    
    private void checkIfSupported(final String methodName) {
        if (!this.hasMultipleAlterations) {
            throw new UnsupportedOperationException("This " + methodName + " API cannot be used on a AlterTableQuery object which is constructed using a deprecated constructor. Please use the other constructor which has the tableName alone as the argument.");
        }
    }
    
    @Override
    public List<AlterOperation> getAlterOperations() {
        return this.alterOperations;
    }
    
    private void checkForMultipleOperations() {
        if (this.alterOperations.size() > 1) {
            throw new IllegalStateException("This AlterTableQuery object has multiple Alter Operations, hence old deprecated AlterTableQuery APIs are not allowed.");
        }
        this.ao = this.alterOperations.get(0);
    }
    
    @Override
    public void setTableDefinition(final TableDefinition tableDefinition) {
        this.td = tableDefinition;
        if (!this.tableName.equals(this.td.getTableName()) && this.tableName.equalsIgnoreCase(this.td.getTableName())) {
            this.tableName = this.td.getTableName();
        }
    }
    
    @Override
    public void modifyTableAttributes(final Properties tableProp) {
        this.checkIfSupported("modifyTableAttributes(tableProp)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 18, tableProp);
        this.alterOperations.add(0, alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void addDynamicColumn(final ColumnDefinition colDef) throws QueryConstructionException {
        this.checkIfSupported("addDynamicColumn(colDef)");
        if (this.alterOperations.size() != 0) {
            throw new QueryConstructionException("Multiple operations are not supported along with addDynamicColumn");
        }
        colDef.setTableName(this.tableName);
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 19, colDef);
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    @Override
    public void modifyDynamicColumn(final String columnName, final ColumnDefinition newColDef) throws QueryConstructionException {
        this.checkIfSupported("modifyDynamicColumn(columnName, newColDef)");
        if (columnName != null && columnName.equals(newColDef.getColumnName())) {
            newColDef.setTableName(this.tableName);
            final AlterOperation alterOperation = new AlterOperation(this.tableName, 21, newColDef);
            this.alterOperations.add(alterOperation);
            this.isValid = false;
            return;
        }
        throw new IllegalArgumentException("Given columnName [" + columnName + "] and the columnName in newColumnDefinition [" + newColDef + "] should be same.");
    }
    
    @Override
    public void removeDynamicColumn(final String columnName) throws QueryConstructionException {
        this.removeDynamicColumn(columnName, false);
    }
    
    @Override
    public void removeDynamicColumn(final String columnName, final boolean dropDependentConstraints) throws QueryConstructionException {
        this.checkIfSupported("removeDynamicColumn(columnName)");
        final AlterOperation alterOperation = new AlterOperation(this.tableName, 20, columnName);
        alterOperation.setIsConstraintNeedToBeDropped(dropDependentConstraints);
        this.alterOperations.add(alterOperation);
        this.isValid = false;
    }
    
    static void validateFKColums(final List fkColDefs) {
        for (final Object fkCol : fkColDefs) {
            final ForeignKeyColumnDefinition fkcoldef = (ForeignKeyColumnDefinition)fkCol;
            if (fkcoldef.getLocalColumnDefinition().isDynamic()) {
                throw new IllegalArgumentException("Local Column cannot be a dynamic column");
            }
            if (fkcoldef.getReferencedColumnDefinition().isDynamic()) {
                throw new IllegalArgumentException("Referenced Column cannot be a dynamic column");
            }
        }
    }
    
    @Override
    public TableDefinition getTableDefinition() {
        return this.td;
    }
    
    static {
        LOGGER = Logger.getLogger(AlterTableQueryImpl.class.getName());
    }
}
