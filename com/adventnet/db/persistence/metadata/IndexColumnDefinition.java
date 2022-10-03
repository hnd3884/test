package com.adventnet.db.persistence.metadata;

import java.util.Objects;
import org.json.JSONObject;
import java.util.StringJoiner;

public class IndexColumnDefinition implements Cloneable
{
    private String columnName;
    private int size;
    private Boolean isNullsFirst;
    private boolean isAscending;
    private ColumnDefinition columnDefinition;
    private String dataType;
    
    @Deprecated
    public IndexColumnDefinition(final String columnName) {
        this(columnName, null, -1);
    }
    
    public IndexColumnDefinition(final ColumnDefinition columnDefinition) {
        this(columnDefinition, true);
    }
    
    public IndexColumnDefinition(final ColumnDefinition columnDefinition, final boolean isAscending) {
        this(columnDefinition, isAscending, null);
    }
    
    public IndexColumnDefinition(final ColumnDefinition columnDefinition, final boolean isAscending, final Boolean isNullsFirst) {
        this(columnDefinition, -1, isAscending, isNullsFirst);
    }
    
    @Deprecated
    public IndexColumnDefinition(final String columnName, final String dataType, final int size) {
        this.size = -1;
        this.isNullsFirst = null;
        this.isAscending = true;
        this.dataType = null;
        (this.columnDefinition = new ColumnDefinition()).setColumnName(columnName);
        this.columnName = columnName;
        this.dataType = dataType;
        this.size = size;
    }
    
    public IndexColumnDefinition(final ColumnDefinition columnDefinition, final int size) {
        this(columnDefinition, size, true);
    }
    
    public IndexColumnDefinition(final ColumnDefinition columnDefinition, final int size, final boolean isAscending) {
        this(columnDefinition, size, isAscending, null);
    }
    
    public IndexColumnDefinition(final ColumnDefinition columnDefinition, final int size, final boolean isAscending, final Boolean isNullsFirst) {
        this.size = -1;
        this.isNullsFirst = null;
        this.isAscending = true;
        this.dataType = null;
        this.columnDefinition = columnDefinition;
        this.columnName = columnDefinition.getColumnName();
        this.dataType = columnDefinition.getDataType();
        this.size = size;
        this.isAscending = isAscending;
        this.isNullsFirst = isNullsFirst;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public Boolean isNullsFirst() {
        return this.isNullsFirst;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public String getDataType() {
        return this.dataType;
    }
    
    public boolean isAscending() {
        return this.isAscending;
    }
    
    public ColumnDefinition getColumnDefinition() {
        return this.columnDefinition;
    }
    
    @Override
    public String toString() {
        final StringJoiner indexColString = new StringJoiner("", "\n\t\t\t<IndexColumnDefinition>", "</IndexColumnDefinition>");
        indexColString.add(this.getColumnName());
        final int size = this.getSize();
        if (size != -1) {
            indexColString.add("(" + size + ")");
        }
        if (this.isAscending()) {
            indexColString.add(" ASC");
        }
        else {
            indexColString.add(" DESC");
        }
        if (this.isNullsFirst() != null) {
            if (this.isNullsFirst()) {
                indexColString.add(" NULLS FIRST");
            }
            else {
                indexColString.add(" NULLS LAST");
            }
        }
        return indexColString.toString();
    }
    
    public JSONObject toJSON() {
        final JSONObject jsonObj = new JSONObject();
        jsonObj.put("columndefinition", (Object)this.columnDefinition.toJSON());
        jsonObj.put("size", this.size);
        jsonObj.put("isascending", this.isAscending);
        jsonObj.put("isnullsfirst", (Object)this.isNullsFirst);
        return jsonObj;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final IndexColumnDefinition copy = (IndexColumnDefinition)super.clone();
        copy.columnDefinition = (ColumnDefinition)this.columnDefinition.clone();
        return copy;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof IndexColumnDefinition) {
            final IndexColumnDefinition icd = (IndexColumnDefinition)obj;
            return Objects.equals(this.columnDefinition, icd.columnDefinition) && Objects.equals(this.size, icd.size) && Objects.equals(this.isAscending, icd.isAscending) && Objects.equals(this.isNullsFirst, icd.isNullsFirst);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.columnDefinition, this.size, this.isAscending, this.isNullsFirst);
    }
}
