package com.adventnet.ds.query;

import java.util.Iterator;
import java.util.List;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.Map;
import com.adventnet.db.persistence.metadata.TableDefinition;

public class CreateTableLike
{
    private String tableName;
    private String tableToBeCloned;
    private TableDefinition mainTableDef;
    private SelectQuery selectQuery;
    private boolean cloneContraintWithName;
    private boolean regenerateCloneeDef;
    private TableDefinition cloneTableDefinition;
    private Map<String, ArchiveTable> relatedTableNameVsArchiveTable;
    private ArchiveTable arcTable;
    
    public CreateTableLike(final String existingTableName, final SelectQuery query, final Map<String, ArchiveTable> tableNameVsArchiveTable, final boolean cloneContraintWithExistingName) throws Exception {
        this.cloneContraintWithName = true;
        this.regenerateCloneeDef = true;
        this.relatedTableNameVsArchiveTable = null;
        this.relatedTableNameVsArchiveTable = tableNameVsArchiveTable;
        this.arcTable = this.relatedTableNameVsArchiveTable.get(existingTableName);
        this.tableName = this.arcTable.getInvisibleTableName();
        this.tableToBeCloned = this.arcTable.getTableName();
        this.mainTableDef = MetaDataUtil.getTableDefinitionByName(this.tableToBeCloned);
        this.selectQuery = query;
        this.cloneNewTableDefinition(this.cloneContraintWithName = cloneContraintWithExistingName);
        this.regenerateCloneeDef = false;
    }
    
    public void setAlternativeNamesForAllConstraints(final boolean setAlternativeNames) {
        this.regenerateCloneeDef = (this.cloneContraintWithName != setAlternativeNames);
        this.cloneContraintWithName = setAlternativeNames;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public String getTableNameToBeCloned() {
        return this.tableToBeCloned;
    }
    
    public TableDefinition getTableDefinition() {
        return this.mainTableDef;
    }
    
    public boolean isCloneConstraintWithActualName() {
        return this.cloneContraintWithName;
    }
    
    public void setSelectQuery(final SelectQuery sQuery) {
        this.selectQuery = sQuery;
    }
    
    public SelectQuery getSelectQuery() {
        return this.selectQuery;
    }
    
    private void cloneNewTableDefinition(final boolean copyConstraintsNamesToo) throws CloneNotSupportedException, QueryConstructionException {
        this.mainTableDef.setArchivalTable(true);
        final TableDefinition newTableDef = (TableDefinition)this.mainTableDef.clone();
        if (newTableDef == null) {
            throw new QueryConstructionException("Table definition cannot be null");
        }
        final ArchiveTable newTable = this.relatedTableNameVsArchiveTable.get(newTableDef.getTableName());
        if (newTable != null) {
            newTableDef.setTableName(newTable.getInvisibleTableName());
        }
        final PrimaryKeyDefinition pkDef = newTableDef.getPrimaryKey();
        if (!copyConstraintsNamesToo) {
            final String oldPKName = pkDef.getName();
            pkDef.setName(this.getNewConstraintName(oldPKName));
        }
        final List<ForeignKeyDefinition> fkList = newTableDef.getForeignKeyList();
        if (fkList != null) {
            for (final ForeignKeyDefinition fkDef : fkList) {
                if (!copyConstraintsNamesToo) {
                    final String oldFKName = fkDef.getName();
                    fkDef.setName(this.getNewConstraintName(oldFKName));
                }
                final ArchiveTable masterTable = this.relatedTableNameVsArchiveTable.get(fkDef.getMasterTableName());
                if (masterTable != null) {
                    fkDef.setMasterTableName(masterTable.getInvisibleTableName());
                }
            }
        }
        final List<UniqueKeyDefinition> ukList = newTableDef.getUniqueKeys();
        if (ukList != null) {
            for (final UniqueKeyDefinition ukDef : ukList) {
                if (!copyConstraintsNamesToo) {
                    final String oldUKName = ukDef.getName();
                    ukDef.setName(this.getNewConstraintName(oldUKName));
                }
            }
        }
        final List<IndexDefinition> idxDefList = newTableDef.getIndexes();
        if (idxDefList != null) {
            for (final IndexDefinition idxDef : idxDefList) {
                if (!copyConstraintsNamesToo) {
                    final String oldIdxName = idxDef.getName();
                    idxDef.setName(this.getNewConstraintName(oldIdxName));
                }
            }
        }
        this.setCloneTableDefinition(newTableDef);
    }
    
    private String getNewConstraintName(final String constraintName) {
        return "_" + constraintName;
    }
    
    public TableDefinition getCloneTableDefinition() throws Exception {
        if (!this.regenerateCloneeDef) {
            this.cloneNewTableDefinition(this.cloneContraintWithName);
        }
        return this.cloneTableDefinition;
    }
    
    private void setCloneTableDefinition(final TableDefinition cloneTableDefinition) {
        this.cloneTableDefinition = cloneTableDefinition;
    }
    
    public ArchiveTable getArchiveTable() {
        return this.arcTable;
    }
}
