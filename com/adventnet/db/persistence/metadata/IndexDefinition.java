package com.adventnet.db.persistence.metadata;

import java.util.Objects;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class IndexDefinition implements Cloneable, Serializable
{
    private Long id;
    private List<IndexColumnDefinition> indexColumns;
    private String name;
    
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
    
    public IndexDefinition() {
        this.id = null;
        this.indexColumns = new ArrayList<IndexColumnDefinition>(1);
    }
    
    @Deprecated
    public int getSize(final String column) {
        if (!this.indexColumns.isEmpty()) {
            for (final IndexColumnDefinition icd : this.indexColumns) {
                if (icd.getColumnName().equals(column)) {
                    return icd.getSize();
                }
            }
        }
        return -1;
    }
    
    @Deprecated
    public void setSize(final String column, final int size) throws MetaDataException {
        if (column == null) {
            throw new MetaDataException("column name cannot be null");
        }
        if (!this.indexColumns.isEmpty()) {
            for (final IndexColumnDefinition icd : this.indexColumns) {
                if (icd.getColumnName().equals(column)) {
                    final IndexColumnDefinition newDef = new IndexColumnDefinition(icd.getColumnDefinition(), size);
                    final int position = this.indexColumns.indexOf(icd);
                    this.indexColumns.remove(icd);
                    this.indexColumns.add(position, newDef);
                    break;
                }
            }
        }
    }
    
    public boolean isPartialIndex() {
        if (!this.indexColumns.isEmpty()) {
            for (final IndexColumnDefinition icd : this.indexColumns) {
                if (icd.getSize() != -1) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final IndexDefinition copy = (IndexDefinition)super.clone();
        copy.indexColumns = new ArrayList<IndexColumnDefinition>(this.indexColumns);
        return copy;
    }
    
    @Deprecated
    public List<String> getColumns() {
        final List<String> cols = new ArrayList<String>();
        for (final IndexColumnDefinition icd : this.indexColumns) {
            cols.add(icd.getColumnName());
        }
        return cols;
    }
    
    public List<IndexColumnDefinition> getColumnDefnitions() {
        return Collections.unmodifiableList((List<? extends IndexColumnDefinition>)this.indexColumns);
    }
    
    @Deprecated
    public void addColumn(final String columnName) {
        final ColumnDefinition cd = new ColumnDefinition();
        cd.setColumnName(columnName);
        final IndexColumnDefinition icd = new IndexColumnDefinition(cd);
        this.indexColumns.add(icd);
    }
    
    @Deprecated
    public void addColumn(final String columnName, final int size) throws MetaDataException {
        if (columnName == null) {
            throw new MetaDataException("column name cannot be null");
        }
        if (this.isColumnAlreadyAdded(columnName)) {
            throw new MetaDataException("column already present in the column list");
        }
        final ColumnDefinition cd = new ColumnDefinition();
        cd.setColumnName(columnName);
        final IndexColumnDefinition icd = new IndexColumnDefinition(cd, size);
        this.indexColumns.add(icd);
    }
    
    private boolean isColumnAlreadyAdded(final String columnName) {
        if (!this.indexColumns.isEmpty()) {
            for (final IndexColumnDefinition col : this.indexColumns) {
                if (col.getColumnName().equals(columnName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void addIndexColumnDefinition(final IndexColumnDefinition icd) {
        this.indexColumns.add(icd);
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("\n\t\t<IndexDefinition>\n\t\t\t<index-key-name>");
        buffer.append(this.name + "</index-key-name>\n\t\t\t<index-key-id>");
        buffer.append(this.getID() + "</index-key-id>");
        for (final IndexColumnDefinition icd : this.indexColumns) {
            buffer.append(String.join("", icd.toString()));
        }
        buffer.append("\n\t\t</IndexDefinition>");
        return buffer.toString();
    }
    
    void renameColumn(final String oldColName, final String newColName) {
        for (final IndexColumnDefinition icd : this.indexColumns) {
            if (icd.getColumnName().equals(oldColName)) {
                final IndexColumnDefinition newDef = new IndexColumnDefinition(icd.getColumnDefinition(), icd.getSize());
                final int position = this.indexColumns.indexOf(icd);
                this.indexColumns.remove(icd);
                this.indexColumns.add(position, newDef);
                break;
            }
        }
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("indexname", (Object)this.name);
        final JSONArray jsonArray = new JSONArray();
        this.getColumnDefnitions().forEach(col -> jsonArray.put((Object)col.toJSON()));
        jsonObject.put("indexcolumns", (Object)jsonArray);
        return jsonObject;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof IndexDefinition) {
            final IndexDefinition idx = (IndexDefinition)obj;
            return Objects.equals(this.name, idx.name) && Objects.equals(this.indexColumns, idx.indexColumns);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.indexColumns);
    }
}
