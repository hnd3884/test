package com.adventnet.ds.query;

import java.util.Properties;
import java.util.List;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.persistence.PersistenceInitializer;

public class AlterOperation
{
    Object alterObject;
    private int operationType;
    private String tableName;
    private boolean fillUVHValues;
    private boolean needToDropConstraint;
    private boolean avoidMaxSizeReduction;
    public boolean handleIndexForFK;
    private String defValConsName;
    private String actualConstraintName;
    private boolean disableTriggerCreation;
    private boolean ignoreFKIndexCreation;
    boolean isGeneratorNameNeedToBeRenamed;
    
    public boolean handleIndexForFK() {
        return this.handleIndexForFK;
    }
    
    public void setFillUVHValue(final boolean condition) {
        this.fillUVHValues = condition;
    }
    
    public boolean fillUVHValues() {
        return this.fillUVHValues;
    }
    
    public void setDefaultValueConstraintName(final String defValConsName) {
        this.defValConsName = defValConsName;
    }
    
    public String getDefaultValueConstraintName() {
        return this.defValConsName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public int getOperationType() {
        return this.operationType;
    }
    
    public Object getAlterObject() {
        return this.alterObject;
    }
    
    public AlterOperation(final String tableName, final int operationType, final Object altObject) {
        this.alterObject = null;
        this.operationType = 0;
        this.tableName = null;
        this.fillUVHValues = false;
        this.needToDropConstraint = false;
        this.avoidMaxSizeReduction = false;
        this.handleIndexForFK = true;
        this.defValConsName = null;
        this.actualConstraintName = null;
        this.disableTriggerCreation = false;
        this.ignoreFKIndexCreation = false;
        this.isGeneratorNameNeedToBeRenamed = false;
        this.tableName = tableName;
        this.operationType = operationType;
        this.alterObject = altObject;
        if (PersistenceInitializer.onSAS()) {
            this.validate();
        }
    }
    
    @Deprecated
    public AlterOperation(final String tableName, final int operationType) {
        this.alterObject = null;
        this.operationType = 0;
        this.tableName = null;
        this.fillUVHValues = false;
        this.needToDropConstraint = false;
        this.avoidMaxSizeReduction = false;
        this.handleIndexForFK = true;
        this.defValConsName = null;
        this.actualConstraintName = null;
        this.disableTriggerCreation = false;
        this.ignoreFKIndexCreation = false;
        this.isGeneratorNameNeedToBeRenamed = false;
        this.operationType = operationType;
        this.tableName = tableName;
        switch (this.operationType) {
            case 1:
            case 2: {
                final ColumnDefinition colDef = new ColumnDefinition();
                colDef.setTableName(tableName);
                this.alterObject = colDef;
                break;
            }
            case 12: {
                this.alterObject = new String[2];
                break;
            }
            case 3:
            case 5:
            case 7:
            case 8:
            case 11:
            case 13: {
                this.alterObject = "";
                break;
            }
            case 17: {
                this.alterObject = new Object[2];
                break;
            }
            case 9: {
                this.alterObject = new PrimaryKeyDefinition();
                break;
            }
            case 4:
            case 15: {
                this.alterObject = new UniqueKeyDefinition();
                break;
            }
            case 6:
            case 14: {
                final ForeignKeyDefinition fkDef = new ForeignKeyDefinition();
                fkDef.setSlaveTableName(tableName);
                this.alterObject = fkDef;
                break;
            }
            case 10:
            case 16: {
                this.alterObject = new IndexDefinition();
                break;
            }
        }
    }
    
    public void validate() {
        switch (this.operationType) {
            case 1:
            case 2:
            case 19:
            case 21: {
                final ColumnDefinition colDef = (ColumnDefinition)this.alterObject;
                AlterTableQueryImpl.checkString(colDef.getColumnName(), "COLUMN_NAME", this.operationType);
                AlterTableQueryImpl.checkString(colDef.getDataType(), "DATA_TYPE", this.operationType);
                if (colDef.getDataType().equals("DECIMAL") && colDef.getMaxLength() - colDef.getPrecision() < 2) {
                    throw new IllegalArgumentException("Decimal column [" + colDef.getColumnName() + "] cannot have the maxLength as [" + colDef.getMaxLength() + "] and precision as [" + colDef.getPrecision() + "]");
                }
                break;
            }
            case 3:
            case 20: {
                AlterTableQueryImpl.checkString((String)this.alterObject, "COLUMN_NAME", this.operationType);
                break;
            }
            case 12:
            case 22: {
                final String[] renColStrs = (String[])this.alterObject;
                AlterTableQueryImpl.checkString(renColStrs[0], "OLD_COLUMN_NAME", this.operationType);
                AlterTableQueryImpl.checkString(renColStrs[1], "NEW_COLUMN_NAME", this.operationType);
                break;
            }
            case 5:
            case 7:
            case 8:
            case 11: {
                AlterTableQueryImpl.checkString((String)this.alterObject, "CONSTRAINT_NAME", this.operationType);
                break;
            }
            case 9:
            case 17: {
                PrimaryKeyDefinition pkDef;
                if (this.operationType == 9) {
                    pkDef = (PrimaryKeyDefinition)this.alterObject;
                }
                else {
                    pkDef = (PrimaryKeyDefinition)((Object[])this.alterObject)[1];
                }
                AlterTableQueryImpl.checkString(pkDef.getName(), "CONSTRAINT_NAME", this.operationType);
                AlterTableQueryImpl.validateColNames(pkDef.getColumnList(), "PK_COLUMNS", this.operationType);
                break;
            }
            case 4:
            case 15: {
                final UniqueKeyDefinition ukDef = (UniqueKeyDefinition)this.alterObject;
                AlterTableQueryImpl.checkString(ukDef.getName(), "CONSTRAINT_NAME", this.operationType);
                AlterTableQueryImpl.validateColNames(ukDef.getColumns(), "UNIQUE_COLUMNS", this.operationType);
                break;
            }
            case 6:
            case 14: {
                final ForeignKeyDefinition fkDef = (ForeignKeyDefinition)this.alterObject;
                AlterTableQueryImpl.checkString(fkDef.getName(), "CONSTRAINT_NAME", this.operationType);
                AlterTableQueryImpl.validateColNames(fkDef.getFkColumns(), "FK_LOCAL_COLUMNS", this.operationType);
                AlterTableQueryImpl.validateColNames(fkDef.getFkRefColumns(), "FK_REFERENCE_COLUMNS", this.operationType);
                AlterTableQueryImpl.checkString(fkDef.getMasterTableName(), "MASTER_TABLE_NAME", this.operationType);
                AlterTableQueryImpl.validateFKConstraint(fkDef.getConstraints());
                AlterTableQueryImpl.validateFKColums(fkDef.getForeignKeyColumns());
                break;
            }
            case 10:
            case 16: {
                final IndexDefinition idxDef = (IndexDefinition)this.alterObject;
                AlterTableQueryImpl.checkString(idxDef.getName(), "CONSTRAINT_NAME", this.operationType);
                AlterTableQueryImpl.validateColNames(idxDef.getColumns(), "INDEX_COLUMNS", this.operationType);
                break;
            }
            case 13: {
                final String newTbName = (String)this.alterObject;
                AlterTableQueryImpl.checkString(newTbName, "NEW_TABLE_NAME", this.operationType);
                break;
            }
            case 18: {
                final Properties tableProp = (Properties)this.alterObject;
                if (tableProp.isEmpty()) {
                    throw new IllegalArgumentException("No Properties were set to modify the attributes of table \"" + this.tableName + "\".");
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Received an unknown operationType [" + this.operationType + "]");
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("operationType=[");
        sb.append(AlterTableQueryImpl.getOperationString(this.operationType));
        switch (this.operationType) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 13:
            case 14:
            case 15:
            case 16:
            case 19:
            case 20:
            case 21: {
                sb.append("] alterObject=[");
                sb.append(this.alterObject);
                sb.append("]");
                break;
            }
            case 17: {
                sb.append("] oldPKName=[");
                sb.append(((Object[])this.alterObject)[0]);
                sb.append("] alterObject=[");
                sb.append(((Object[])this.alterObject)[1]);
                sb.append("]");
                break;
            }
            case 12:
            case 22: {
                sb.append("] oldName=[");
                sb.append(((String[])this.alterObject)[0]);
                sb.append("] newName=[");
                sb.append(((String[])this.alterObject)[1]);
                sb.append("]");
                break;
            }
        }
        return sb.toString();
    }
    
    public String getActualConstraintName() {
        return this.actualConstraintName;
    }
    
    public void setActualConstraintName(final String actualConsName) {
        this.actualConstraintName = actualConsName;
    }
    
    void setIsConstraintNeedToBeDropped(final boolean needToDropConstraint) {
        this.needToDropConstraint = needToDropConstraint;
    }
    
    boolean isConstarintsNeedToBeDropped() {
        return this.needToDropConstraint;
    }
    
    public void ignoreMaxSizeReduction() {
        this.avoidMaxSizeReduction = true;
    }
    
    public boolean isMaxSizeReductionIgnored() {
        return this.avoidMaxSizeReduction;
    }
    
    public boolean isDisableTriggerCreation() {
        return this.disableTriggerCreation;
    }
    
    public void setDisableTriggerCreation(final boolean disableTriggerCreation) {
        this.disableTriggerCreation = disableTriggerCreation;
    }
    
    public boolean isIgnoreFKIndexCreation() {
        return this.ignoreFKIndexCreation;
    }
    
    public void setIgnoreFKIndexCreation(final boolean ignoreFKIndexCreation) {
        this.ignoreFKIndexCreation = ignoreFKIndexCreation;
    }
    
    public boolean isGeneratorNameNeedToBeRenamed() {
        return this.isGeneratorNameNeedToBeRenamed;
    }
}
