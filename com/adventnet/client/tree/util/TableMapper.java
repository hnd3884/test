package com.adventnet.client.tree.util;

import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.ArrayList;

public class TableMapper
{
    private String mapperTableName;
    private String parentTableName;
    private String childTableName;
    private ArrayList parent_fk_list;
    private ArrayList child_fk_list;
    
    public TableMapper(final String tableName) {
        this.mapperTableName = null;
        this.parentTableName = null;
        this.childTableName = null;
        this.parent_fk_list = null;
        this.child_fk_list = null;
        this.mapperTableName = tableName;
        this.parent_fk_list = new ArrayList();
        this.child_fk_list = new ArrayList();
    }
    
    public String getName() {
        return this.mapperTableName;
    }
    
    public void setParentTableName(final String tableName) {
        this.parentTableName = tableName;
    }
    
    public void setChildTableName(final String tableName) {
        this.childTableName = tableName;
    }
    
    public void addParentTableFK(final String fkName) {
        this.parent_fk_list.add(fkName);
    }
    
    public void addChildTableFK(final String fkName) {
        this.child_fk_list.add(fkName);
    }
    
    public String getParentTableName() {
        if (this.parentTableName == null && this.parent_fk_list.size() > 0) {
            try {
                final TableDefinition tdef = MetaDataUtil.getTableDefinitionByName(this.mapperTableName);
                final ForeignKeyDefinition fk_def = tdef.getForeignKeyDefinitionByName((String)this.parent_fk_list.get(0));
                this.parentTableName = fk_def.getMasterTableName();
            }
            catch (final MetaDataException e) {
                e.printStackTrace();
            }
        }
        return this.parentTableName;
    }
    
    public String getChildTableName() {
        if (this.childTableName == null && this.child_fk_list.size() > 0) {
            try {
                final TableDefinition tdef = MetaDataUtil.getTableDefinitionByName(this.mapperTableName);
                final ForeignKeyDefinition fk_def = tdef.getForeignKeyDefinitionByName((String)this.child_fk_list.get(0));
                this.childTableName = fk_def.getMasterTableName();
            }
            catch (final MetaDataException e) {
                e.printStackTrace();
            }
        }
        return this.childTableName;
    }
    
    public ArrayList getParentTableFKs() {
        return this.parent_fk_list;
    }
    
    public ArrayList getChildTableFKs() {
        return this.child_fk_list;
    }
}
