package com.adventnet.ds.query;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.logging.Logger;
import java.io.Serializable;

public class Table implements Serializable, Cloneable
{
    private static final long serialVersionUID = 8606752790505210237L;
    private static final transient Logger OUT;
    private String tableName;
    private String tableAlias;
    private static final Map<String, SoftReference<Table>> KEYVSTABLE;
    String key;
    int hashCode;
    
    public Table(final String tableName) {
        this(tableName, tableName);
    }
    
    public Table(final String tableName, final String tableAlias) {
        this.tableName = null;
        this.tableAlias = null;
        this.key = null;
        this.hashCode = -1;
        this.tableName = tableName;
        this.tableAlias = tableAlias;
    }
    
    public static Table getTable(final String tableName) {
        return getTable(tableName, null);
    }
    
    public static Table getTable(final String tableName, final String tableAlias) {
        if (tableName == null || tableName.equals("")) {
            throw new IllegalArgumentException("TableName cannot be specified as null/empty");
        }
        final String keyName = (tableName + ((tableAlias == null || tableAlias.equals(tableName)) ? "" : ("_" + tableAlias))).intern();
        final SoftReference<Table> sr = Table.KEYVSTABLE.get(keyName);
        Table retTable = null;
        if (sr != null) {
            retTable = sr.get();
            if (retTable == null) {
                Table.OUT.log(Level.FINER, "SoftReference object cleared but finalize method not invoked hence Null Table obtained from Cache for the tableName :: [${0}] tableAlias :: [${1}]", new Object[] { tableName, tableAlias });
                retTable = getNewTableInstanceFromCache(tableName, tableAlias, keyName);
            }
        }
        else {
            retTable = getNewTableInstanceFromCache(tableName, tableAlias, keyName);
        }
        return retTable;
    }
    
    private static Table getNewTableInstanceFromCache(final String tableName, final String tableAlias, final String keyName) {
        Table retTable = null;
        retTable = new Table(tableName, (tableAlias == null) ? tableName : tableAlias);
        final SoftReference<Table> sr = new SoftReference<Table>(retTable);
        Table.KEYVSTABLE.put(keyName, sr);
        retTable.key = keyName;
        return retTable;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public String getTableAlias() {
        return this.tableAlias;
    }
    
    @Override
    public boolean equals(final Object obj) {
        Table table = null;
        if (this == obj) {
            return true;
        }
        if (obj instanceof Table) {
            table = (Table)obj;
            return table.getTableName().equals(this.getTableName()) && table.getTableAlias().equals(this.getTableAlias());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = this.hashCode(this.tableName) + this.hashCode(this.tableAlias);
        }
        return this.hashCode;
    }
    
    private int hashCode(final Object obj) {
        return (obj == null) ? 0 : obj.hashCode();
    }
    
    @Override
    public String toString() {
        return this.tableName + " AS " + this.tableAlias;
    }
    
    public Object clone() {
        return new Table(this.tableName, this.tableAlias);
    }
    
    static {
        OUT = Logger.getLogger(Column.class.getName());
        KEYVSTABLE = new ConcurrentHashMap<String, SoftReference<Table>>(2000, 0.25f, 5);
    }
}
