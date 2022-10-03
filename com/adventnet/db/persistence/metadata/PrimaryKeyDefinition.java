package com.adventnet.db.persistence.metadata;

import org.json.JSONException;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class PrimaryKeyDefinition implements Serializable, Cloneable
{
    private static final long serialVersionUID = -5103814665197522316L;
    private Long id;
    private String name;
    private List<String> columnList;
    private String tableName;
    
    public PrimaryKeyDefinition() {
        this.id = null;
        this.columnList = new ArrayList<String>(1);
    }
    
    @Deprecated
    public Long getID() {
        return this.id;
    }
    
    public void setID(final Long id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final PrimaryKeyDefinition copy = (PrimaryKeyDefinition)super.clone();
        copy.columnList = new ArrayList<String>(this.columnList);
        return copy;
    }
    
    public List<String> getColumnList() {
        return Collections.unmodifiableList((List<? extends String>)this.columnList);
    }
    
    public void addColumnName(final String columnName) {
        this.columnList.add(columnName);
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("\n\t\t<PrimaryKeyDefinition>");
        buffer.append("\n\t\t\t<primary-key-name>" + this.name + "</primary-key-name>");
        buffer.append("\n\t\t\t<table-name>" + this.tableName + "</table-name>");
        buffer.append("\n\t\t\t<primary-key-columns>" + this.columnList + "</primary-key-columns>");
        buffer.append("\n\t\t</PrimaryKeyDefinition>");
        return buffer.toString();
    }
    
    protected void renameColumn(final String oldColumnName, final String newColumnName) {
        final int columnIndex = this.columnList.indexOf(oldColumnName);
        if (columnIndex > -1) {
            this.columnList.set(columnIndex, newColumnName);
        }
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("primarykeyname", (Object)this.name);
        jsonObject.put("tablename", (Object)this.tableName);
        final JSONArray jsonArray = new JSONArray();
        for (final String column : this.columnList) {
            jsonArray.put((Object)column);
        }
        jsonObject.put("primarykeycolumns", (Object)jsonArray);
        return jsonObject;
    }
}
