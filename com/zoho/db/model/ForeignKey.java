package com.zoho.db.model;

import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class ForeignKey
{
    private String name;
    private String tableName;
    private String parentTableName;
    private String dbLable;
    private List<ForeignKeyColumn> fkColumn;
    private Map<MetaDataLabel, String> others;
    private int deleteRule;
    private static String format;
    
    public ForeignKey() {
        this.fkColumn = new ArrayList<ForeignKeyColumn>();
        this.others = new HashMap<MetaDataLabel, String>();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public List<ForeignKeyColumn> getFkColumns() {
        return this.fkColumn;
    }
    
    public void addFkColumn(final String columnName, final String parentColumnName, final int keySeq) {
        this.fkColumn.add(new ForeignKeyColumn(columnName, parentColumnName, keySeq));
        Collections.sort(this.getFkColumns());
    }
    
    public Map<MetaDataLabel, String> getOthers() {
        return this.others;
    }
    
    public void setProperties(final Map<MetaDataLabel, String> others) {
        this.others = others;
    }
    
    public String getParentTableName() {
        return this.parentTableName;
    }
    
    public void setParentTableName(final String parentTableName) {
        this.parentTableName = parentTableName;
    }
    
    public boolean isCascadeOnDelete() {
        return this.getDeleteRule() == 0;
    }
    
    public boolean isRestrictDelete() {
        return this.getDeleteRule() == 3 || this.getDeleteRule() == 1;
    }
    
    public boolean isNullOnDelete() {
        return this.getDeleteRule() == 2;
    }
    
    public String getConstraint() {
        String constraintName = null;
        switch (this.getDeleteRule()) {
            case 1:
            case 3: {
                constraintName = "";
                break;
            }
            case 0: {
                constraintName = "ON DELETE CASCADE";
                break;
            }
            case 2: {
                constraintName = "ON DELETE SET NULL";
                break;
            }
            case 4: {
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
    
    public int getDeleteRule() {
        return this.deleteRule;
    }
    
    public void setDeleteRule(final int deleteRule) {
        this.deleteRule = deleteRule;
    }
    
    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();
        buff.append(String.format(ForeignKey.format, this.getName()));
        String ccolumns = "";
        String pcolumns = "";
        for (final ForeignKeyColumn fkColumn : this.getFkColumns()) {
            if (!ccolumns.isEmpty() || !pcolumns.isEmpty()) {
                ccolumns += ",";
                pcolumns += ",";
            }
            ccolumns += fkColumn.getColumnName();
            pcolumns += fkColumn.getParentColumnName();
        }
        buff.append("(").append(ccolumns).append(")");
        buff.append(" REFERENCES ").append(this.getParentTableName());
        buff.append(" (").append(pcolumns).append(") ");
        buff.append(this.getConstraint()).append("\n");
        return buff.toString();
    }
    
    public String getDbLable() {
        return this.dbLable;
    }
    
    public void setDbLable(final String dbLable) {
        this.dbLable = dbLable;
    }
    
    static {
        ForeignKey.format = "%s FOREIGN KEY ";
    }
    
    public enum MetaDataLabel
    {
        PKTABLE_CAT, 
        PKTABLE_SCHEM, 
        PKTABLE_NAME, 
        PKCOLUMN_NAME, 
        FKTABLE_CAT, 
        FKTABLE_SCHEM, 
        FKTABLE_NAME, 
        FKCOLUMN_NAME, 
        KEY_SEQ, 
        UPDATE_RULE, 
        DELETE_RULE, 
        FK_NAME, 
        PK_NAME, 
        DEFERRABILITY;
    }
    
    public class ForeignKeyColumn implements Comparable<ForeignKeyColumn>
    {
        private String columnName;
        private String parentColumnName;
        private Integer keySeq;
        
        public ForeignKeyColumn(final String columnName, final String parentColumnName, final int keySeq) {
            this.keySeq = 0;
            this.columnName = columnName;
            this.parentColumnName = parentColumnName;
            this.keySeq = keySeq;
        }
        
        public String getColumnName() {
            return this.columnName;
        }
        
        public String getParentColumnName() {
            return this.parentColumnName;
        }
        
        public int getKeySeq() {
            return this.keySeq;
        }
        
        @Override
        public int compareTo(final ForeignKeyColumn o) {
            return this.keySeq.compareTo(o.getKeySeq());
        }
    }
}
