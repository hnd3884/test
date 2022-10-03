package com.adventnet.db.persistence.metadata;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class ForeignKeyDefinition implements Serializable, Cloneable
{
    private static final long serialVersionUID = 4572236700169239135L;
    private Long id;
    public static final int ON_DELETE_RESTRICT = 0;
    public static final int ON_DELETE_CASCADE = 1;
    public static final int ON_DELETE_SET_NULL = 2;
    public static final int ON_DELETE_SET_DEFAULT = 3;
    private String masterTableName;
    private String slaveTableName;
    private List<ForeignKeyColumnDefinition> foreignKeyColumns;
    private String name;
    private boolean bidirectional;
    private int constraints;
    private String description;
    private List<String> fkColumns;
    private List<String> fkRefColumns;
    
    public ForeignKeyDefinition() {
        this.id = null;
        this.foreignKeyColumns = new ArrayList<ForeignKeyColumnDefinition>(1);
        this.constraints = 0;
        this.fkColumns = new ArrayList<String>(2);
        this.fkRefColumns = new ArrayList<String>(2);
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
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String v) {
        this.description = v;
    }
    
    public String getMasterTableName() {
        return this.masterTableName;
    }
    
    public void setMasterTableName(final String masterTableName) {
        this.masterTableName = masterTableName;
    }
    
    public String getSlaveTableName() {
        return this.slaveTableName;
    }
    
    public void setSlaveTableName(final String slaveTableName) {
        this.slaveTableName = slaveTableName;
    }
    
    public List<ForeignKeyColumnDefinition> getForeignKeyColumns() {
        return Collections.unmodifiableList((List<? extends ForeignKeyColumnDefinition>)this.foreignKeyColumns);
    }
    
    public void addForeignKeyColumns(final ForeignKeyColumnDefinition fkcd) {
        this.foreignKeyColumns.add(fkcd);
        this.fkColumns.add(fkcd.getLocalColumnDefinition().getColumnName());
        this.fkRefColumns.add(fkcd.getReferencedColumnDefinition().getColumnName());
    }
    
    public List<String> getFkColumns() {
        return Collections.unmodifiableList((List<? extends String>)this.fkColumns);
    }
    
    public List<String> getFkRefColumns() {
        return Collections.unmodifiableList((List<? extends String>)this.fkRefColumns);
    }
    
    @Deprecated
    public void setForeignKeyColumns(final List<ForeignKeyColumnDefinition> foreignKeyColumns) {
        for (final ForeignKeyColumnDefinition fkcd : foreignKeyColumns) {
            this.addForeignKeyColumns(fkcd);
        }
    }
    
    public boolean isBidirectional() {
        return this.bidirectional;
    }
    
    public void setBidirectional(final boolean bidirectional) {
        this.bidirectional = bidirectional;
    }
    
    public int getConstraints() {
        return this.constraints;
    }
    
    public void setConstraints(final int constraints) {
        this.constraints = constraints;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("\n\t\t<Foreign-Key-Definition>");
        buffer.append("\n\t\t\t<name>" + this.name + "</name>");
        buffer.append("\n\t\t\t<description>" + this.description + "</description");
        buffer.append("\n\t\t\t<master-table-name>" + this.masterTableName + "</master-table-name>");
        buffer.append("\n\t\t\t<slave-table-name>" + this.slaveTableName + "</slave-table-name>");
        buffer.append("\n\t\t\t<foreign-key-columns>" + this.foreignKeyColumns + "\n\t\t\t</foreign-key-columns>");
        buffer.append("\n\t\t\t<bidirectional>" + this.bidirectional + "</bidirectional>");
        buffer.append("\n\t\t\t<foreign-key-constraint>" + this.constraints + "</constraints>");
        buffer.append("\n\t\t</Foreign-Key-Definition>");
        return buffer.toString();
    }
    
    public String parentTableName() {
        return this.getMasterTableName();
    }
    
    public String childTableName() {
        return this.getSlaveTableName();
    }
    
    public List<String> parentColumnNames() {
        return this.fkRefColumns;
    }
    
    public List<String> childColumnNames() {
        return this.fkColumns;
    }
    
    public void renameColumn(final String oldColumnName, final String newColumnName, final boolean isChildColumn) {
        final List<String> columns = isChildColumn ? this.fkColumns : this.fkRefColumns;
        final int columnIndex = columns.indexOf(oldColumnName);
        if (columnIndex > -1) {
            columns.set(columnIndex, newColumnName);
        }
    }
    
    public String getConstraintsAsString() {
        switch (this.constraints) {
            case 1: {
                return "ON-DELETE-CASCADE";
            }
            case 2: {
                return "ON-DELETE-SET-NULL";
            }
            case 3: {
                return "ON-DELETE-SET-DEFAULT";
            }
            default: {
                return "ON-DELETE-RESTRICT";
            }
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        final ForeignKeyDefinition copy = (ForeignKeyDefinition)super.clone();
        copy.fkColumns = new ArrayList<String>(this.fkColumns);
        copy.fkRefColumns = new ArrayList<String>(this.fkRefColumns);
        copy.foreignKeyColumns = new ArrayList<ForeignKeyColumnDefinition>();
        for (final ForeignKeyColumnDefinition fkcd : this.foreignKeyColumns) {
            copy.foreignKeyColumns.add((ForeignKeyColumnDefinition)fkcd.clone());
        }
        return copy;
    }
    
    private boolean isEquals(final Object o1, final Object o2) {
        if (o1 == null) {
            if (o2 != null) {
                return false;
            }
        }
        else if (!o1.equals(o2)) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ForeignKeyDefinition) {
            final ForeignKeyDefinition fk = (ForeignKeyDefinition)o;
            if (this.isEquals(fk.name, this.name) && this.isEquals(fk.slaveTableName, this.slaveTableName) && this.isEquals(fk.masterTableName, this.masterTableName) && this.isEquals(fk.foreignKeyColumns, this.foreignKeyColumns) && this.isEquals(fk.constraints, this.constraints) && this.isEquals(fk.description, this.description) && this.isEquals(fk.bidirectional, this.bidirectional)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("foreignkeyname", (Object)this.name);
        jsonObject.put("description", (Object)this.description);
        jsonObject.put("mastertablename", (Object)this.masterTableName);
        jsonObject.put("slavetablename", (Object)this.slaveTableName);
        final JSONArray jsonArray = new JSONArray();
        for (final ForeignKeyColumnDefinition fkcd : this.foreignKeyColumns) {
            jsonArray.put((Object)fkcd.toJSON());
        }
        jsonObject.put("foreignkeycolumns", (Object)jsonArray);
        jsonObject.put("bidirectional", this.bidirectional);
        jsonObject.put("constraints", (Object)this.getConstraintsAsString());
        return jsonObject;
    }
}
