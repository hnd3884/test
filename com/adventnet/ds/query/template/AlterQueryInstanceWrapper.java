package com.adventnet.ds.query.template;

import java.util.Properties;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.persistence.metadata.UniqueValueGeneration;
import com.adventnet.db.persistence.metadata.AllowedValues;
import java.util.List;
import com.adventnet.persistence.template.TemplateUtil;
import com.adventnet.ds.query.AlterTableQuery;

public class AlterQueryInstanceWrapper implements AlterTableQuery
{
    private AlterTableQuery template_aq;
    private String instanceId;
    
    public AlterQueryInstanceWrapper(final AlterTableQuery template_aq, final String instanceId) {
        this.template_aq = null;
        this.instanceId = null;
        this.template_aq = template_aq;
        if (instanceId == null) {
            throw new IllegalArgumentException("InstanceId cannot be NULL");
        }
        this.instanceId = instanceId;
    }
    
    @Override
    public int getOperationType() {
        return this.template_aq.getOperationType();
    }
    
    @Override
    public String getTableName() {
        return TemplateUtil.getTableName(this.template_aq.getTableName(), this.instanceId);
    }
    
    @Override
    public String getColumnName() {
        return this.template_aq.getColumnName();
    }
    
    @Override
    public String getNewColumnName() {
        return this.template_aq.getNewColumnName();
    }
    
    @Override
    public String getDescription() {
        return this.template_aq.getDescription();
    }
    
    @Override
    public String getDataType() {
        return this.template_aq.getDataType();
    }
    
    @Override
    public Object getDefaultValue() {
        return this.template_aq.getDefaultValue();
    }
    
    @Override
    public int getMaxLength() {
        return this.template_aq.getMaxLength();
    }
    
    @Override
    public boolean isNullable() {
        return this.template_aq.isNullable();
    }
    
    @Override
    public boolean isUnique() {
        return this.template_aq.isUnique();
    }
    
    @Override
    public boolean isValid() {
        return this.template_aq.isValid();
    }
    
    @Override
    public String getConstraintName() {
        final String constName = this.template_aq.getConstraintName();
        if (constName == null) {
            return constName;
        }
        switch (this.template_aq.getOperationType()) {
            case 6:
            case 7:
            case 14: {
                throw new UnsupportedOperationException("FK operations not supported");
            }
            case 10:
            case 11:
            case 16: {
                return TemplateUtil.getIndexDefnName(constName, this.instanceId);
            }
            case 4:
            case 5:
            case 15: {
                return TemplateUtil.getUKDefnName(constName, this.instanceId);
            }
            case 8:
            case 9:
            case 17: {
                return TemplateUtil.getPKDefnName(constName, this.instanceId);
            }
            default: {
                return constName;
            }
        }
    }
    
    @Override
    public int getFKConstraint() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public List getFKLocalColumns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public List getFKReferenceColumns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String getFKMasterTableName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public List getUniqueCols() {
        return this.template_aq.getUniqueCols();
    }
    
    @Override
    public boolean isBidirectional() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public List getIndexColumns() {
        return this.template_aq.getIndexColumns();
    }
    
    @Override
    public List getPKColumns() {
        return this.template_aq.getPKColumns();
    }
    
    @Override
    public AllowedValues getAllowedValues() {
        return this.template_aq.getAllowedValues();
    }
    
    @Override
    public String getDisplayName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public UniqueValueGeneration getUniqueValueGeneration() {
        return this.template_aq.getUniqueValueGeneration();
    }
    
    @Override
    public void setIndexColumns(final List columnNames) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setPKColumns(final List columnNames) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setColumnName(final String arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setNewColumnName(final String arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setDescription(final String arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setMaxLength(final int arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setDataType(final String arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setNullable(final boolean arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setAllowedValues(final AllowedValues arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setDisplayName(final String arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setUniqueValueGeneration(final UniqueValueGeneration arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setUnique(final boolean arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setDefaultValue(final Object arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setUniqueCols(final List columnNames) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setConstraintName(final String arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setFKConstraint(final int arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setFKLocalColumns(final List columnNames) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setFKReferenceColumns(final List columnNames) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setFKMasterTableName(final String arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setBidirectional(final boolean arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void validate() throws QueryConstructionException {
        this.template_aq.validate();
    }
    
    @Override
    public TableDefinition getTableDefinition() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setTableDefinition(final TableDefinition arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setIsExecutable(final boolean arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public boolean isExecutable() {
        return this.template_aq.isExecutable();
    }
    
    @Override
    public int getPrecision() {
        return this.template_aq.getPrecision();
    }
    
    @Override
    public void setPrecision(final int arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void addColumn(final ColumnDefinition colDef) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void renameColumn(final String columnName, final String newColumnName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void removeColumn(final String columnName) {
        this.removeColumn(columnName, false);
    }
    
    @Override
    public void removeColumn(final String columnName, final boolean dropDependentConstraints) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void modifyColumn(final String columnName, final ColumnDefinition newColDef) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void modifyPrimaryKey(final PrimaryKeyDefinition pkDef) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void modifyPrimaryKey(final String oldPKName, final PrimaryKeyDefinition pkDef) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void modifyForeignKey(final ForeignKeyDefinition fkDef) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void modifyIndex(final IndexDefinition idxDef) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void modifyUniqueKey(final UniqueKeyDefinition ukDef) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void addUniqueKey(final UniqueKeyDefinition ukDef) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void removeUniqueKey(final String ukName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void addForeignKey(final ForeignKeyDefinition fkDef) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void removeForeignKey(final String fkName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void addIndex(final IndexDefinition idxDef) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void dropIndex(final String idxName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void renameTable(final String newTableName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public List<AlterOperation> getAlterOperations() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void addColumn(final ColumnDefinition colDef, final String ukName) throws QueryConstructionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void modifyColumn(final String columnName, final ColumnDefinition newColDef, final String ukName) throws QueryConstructionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void setRevertFlag(final boolean revertFlag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public boolean isRevert() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void modifyTableAttributes(final Properties tableProp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void addPrimaryKey(final PrimaryKeyDefinition pkDef) throws QueryConstructionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void removePrimaryKey(final String oldPKName) throws QueryConstructionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void addDynamicColumn(final ColumnDefinition colDef) throws QueryConstructionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void removeDynamicColumn(final String columnName) throws QueryConstructionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void removeDynamicColumn(final String columnName, final boolean dropDependentConstraints) throws QueryConstructionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void modifyDynamicColumn(final String columnName, final ColumnDefinition newColDef) throws QueryConstructionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void renameDynamicColumn(final String existingColumnName, final String newColumnName) throws QueryConstructionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
