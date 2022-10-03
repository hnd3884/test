package org.jfree.util;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class TableOrder implements Serializable
{
    private static final long serialVersionUID = 525193294068177057L;
    public static final TableOrder BY_ROW;
    public static final TableOrder BY_COLUMN;
    private String name;
    
    static {
        BY_ROW = new TableOrder("TableOrder.BY_ROW");
        BY_COLUMN = new TableOrder("TableOrder.BY_COLUMN");
    }
    
    private TableOrder(final String name) {
        this.name = name;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TableOrder)) {
            return false;
        }
        final TableOrder that = (TableOrder)obj;
        return this.name.equals(that.name);
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(TableOrder.BY_ROW)) {
            return TableOrder.BY_ROW;
        }
        if (this.equals(TableOrder.BY_COLUMN)) {
            return TableOrder.BY_COLUMN;
        }
        return null;
    }
    
    public String toString() {
        return this.name;
    }
}
