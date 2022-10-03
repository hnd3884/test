package com.adventnet.db.persistence.metadata;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;

public class ForeignKeyColumnDefinition implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1177248734327750840L;
    private ColumnDefinition localColumnDefinition;
    private ColumnDefinition referencedColumnDefinition;
    
    public ColumnDefinition getLocalColumnDefinition() {
        return this.localColumnDefinition;
    }
    
    public void setLocalColumnDefinition(final ColumnDefinition localColumnDefinition) {
        this.localColumnDefinition = localColumnDefinition;
    }
    
    public ColumnDefinition getReferencedColumnDefinition() {
        return this.referencedColumnDefinition;
    }
    
    public void setReferencedColumnDefinition(final ColumnDefinition referencedColumnDefinition) {
        this.referencedColumnDefinition = referencedColumnDefinition;
        this.localColumnDefinition.setParentColumn(referencedColumnDefinition);
        this.referencedColumnDefinition.setChildColumn(this.localColumnDefinition);
        ColumnDefinition rootCol = referencedColumnDefinition;
        final ColumnDefinition refRoot = referencedColumnDefinition.getRootColumn();
        if (refRoot != null) {
            rootCol = refRoot;
        }
        this.localColumnDefinition.setRootColumn(rootCol);
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("<\n\t\t\t\t<Foreign-Key-Column-Definition>");
        buffer.append("\n\t\t\t\t\t<local-column-name>").append(this.localColumnDefinition.getColumnName()).append("</local-column-name>");
        buffer.append("\n\t\t\t\t\t<referenced-column-name>").append(this.referencedColumnDefinition.getColumnName()).append("</referenced-column-name>");
        buffer.append("\n\t\t\t\t</Foreign-Key-Column-Definition>");
        return buffer.toString();
    }
    
    public Object clone() throws CloneNotSupportedException {
        final ForeignKeyColumnDefinition copy = (ForeignKeyColumnDefinition)super.clone();
        copy.localColumnDefinition = (ColumnDefinition)this.localColumnDefinition.clone();
        copy.referencedColumnDefinition = (ColumnDefinition)this.referencedColumnDefinition.clone();
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
        if (o instanceof ForeignKeyColumnDefinition) {
            final ForeignKeyColumnDefinition fkColDef = (ForeignKeyColumnDefinition)o;
            if (this.isEquals(fkColDef.localColumnDefinition, this.localColumnDefinition) && this.isEquals(fkColDef.referencedColumnDefinition, this.referencedColumnDefinition)) {
                return true;
            }
        }
        return false;
    }
    
    public JSONObject toJSON() throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("localcolumnname", (Object)this.localColumnDefinition.getColumnName());
        jsonObject.put("referencedcolumnname", (Object)this.referencedColumnDefinition.getColumnName());
        return jsonObject;
    }
}
