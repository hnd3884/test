package com.adventnet.ds.query;

import java.io.Serializable;

public class ArchiveTable extends Table implements Serializable, Cloneable
{
    private String arcTableName;
    private String arcTableAlias;
    private String invTableName;
    private String invTableAlias;
    int hashCode;
    
    public ArchiveTable(final String tableName, final String archiveTableName, final String invisibleName) {
        this(tableName, tableName, archiveTableName, archiveTableName, invisibleName, invisibleName);
    }
    
    public ArchiveTable(final String tableName, final String tableNameAlias, final String archiveTableName, final String archiveTableAlias, final String invisibleName, final String invisibleNameAlias) {
        super(tableName);
        this.arcTableName = null;
        this.arcTableAlias = null;
        this.invTableName = null;
        this.invTableAlias = null;
        this.hashCode = -1;
        this.arcTableName = archiveTableName;
        this.arcTableAlias = archiveTableAlias;
        this.invTableName = invisibleName;
        this.invTableAlias = invisibleNameAlias;
    }
    
    public String getArchiveTableAlias() {
        return this.arcTableAlias;
    }
    
    public String getArchiveTableName() {
        return this.arcTableName;
    }
    
    public void setArcchiveTableAlias(final String arcTableAlias) {
        this.arcTableAlias = arcTableAlias;
    }
    
    public void setArchiveTableName(final String arcTableName) {
        this.arcTableName = arcTableName;
    }
    
    public String getInvisibleTableAlias() {
        return this.invTableAlias;
    }
    
    public String getInvisibleTableName() {
        return this.invTableName;
    }
    
    public void setInvisibleTableAlias(final String arcTableAlias) {
        this.invTableAlias = arcTableAlias;
    }
    
    public void setInvisibleTableName(final String arcTableName) {
        this.invTableName = arcTableName;
    }
    
    @Override
    public boolean equals(final Object obj) {
        ArchiveTable table = null;
        if (this == obj) {
            return true;
        }
        if (obj instanceof ArchiveTable) {
            table = (ArchiveTable)obj;
            return !super.equals(obj) || true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = super.hashCode();
        }
        return this.hashCode;
    }
    
    private int hashCode(final Object obj) {
        return (obj == null) ? 0 : obj.hashCode();
    }
    
    @Override
    public String toString() {
        return this.arcTableName + " AS " + this.arcTableAlias;
    }
    
    @Override
    public Object clone() {
        return new ArchiveTable(this.arcTableName, this.arcTableName, this.invTableName);
    }
}
