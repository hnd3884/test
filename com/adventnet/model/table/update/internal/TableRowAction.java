package com.adventnet.model.table.update.internal;

import com.adventnet.model.table.internal.CVTableModelRow;
import java.io.Serializable;

public class TableRowAction implements Serializable
{
    public static final int INSERT = 0;
    public static final int UPDATE = 1;
    public static final int DELETE = 2;
    public static final int REFRESH = 3;
    public static final int INDICES_CHANGE = 4;
    private static final String[] STRING_TYPES;
    protected CVTableModelRow row;
    protected int rowIndex;
    protected int type;
    protected long startIndex;
    protected long endIndex;
    protected long total;
    
    public TableRowAction(final int type, final long startIndex, final long endIndex, final long total) {
        this.type = type;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.total = total;
    }
    
    public TableRowAction(final int type, final CVTableModelRow row, final int rowIndex, final long startIndex, final long endIndex, final long total) {
        this(type, startIndex, endIndex, total);
        this.row = row;
        this.rowIndex = rowIndex;
    }
    
    public int getType() {
        return this.type;
    }
    
    public CVTableModelRow getRow() {
        return this.row;
    }
    
    public int getRowIndex() {
        return this.rowIndex;
    }
    
    public long getStartIndex() {
        return this.startIndex;
    }
    
    public long getEndIndex() {
        return this.endIndex;
    }
    
    public long getTotal() {
        return this.total;
    }
    
    @Override
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        buff.append("\n<").append(TableRowAction.STRING_TYPES[this.type]);
        switch (this.type) {
            case 4: {
                this.addIndices(buff);
                buff.append(" />");
                break;
            }
            case 0:
            case 1:
            case 2: {
                buff.append(" @=").append(this.rowIndex);
                this.addIndices(buff);
                buff.append(" >");
                buff.append(this.row);
                buff.append("</").append(TableRowAction.STRING_TYPES[this.type]).append(">");
                break;
            }
        }
        return buff.toString();
    }
    
    private void addIndices(final StringBuffer buff) {
        buff.append(" S=").append(this.startIndex);
        buff.append(" E=").append(this.endIndex);
        buff.append(" T=").append(this.total);
    }
    
    static {
        STRING_TYPES = new String[] { "INSERT", "UPDATE", "DELETE", "REFRESH", "INDICES_CHANGE" };
    }
}
