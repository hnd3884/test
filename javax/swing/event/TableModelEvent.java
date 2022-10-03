package javax.swing.event;

import javax.swing.table.TableModel;
import java.util.EventObject;

public class TableModelEvent extends EventObject
{
    public static final int INSERT = 1;
    public static final int UPDATE = 0;
    public static final int DELETE = -1;
    public static final int HEADER_ROW = -1;
    public static final int ALL_COLUMNS = -1;
    protected int type;
    protected int firstRow;
    protected int lastRow;
    protected int column;
    
    public TableModelEvent(final TableModel tableModel) {
        this(tableModel, 0, Integer.MAX_VALUE, -1, 0);
    }
    
    public TableModelEvent(final TableModel tableModel, final int n) {
        this(tableModel, n, n, -1, 0);
    }
    
    public TableModelEvent(final TableModel tableModel, final int n, final int n2) {
        this(tableModel, n, n2, -1, 0);
    }
    
    public TableModelEvent(final TableModel tableModel, final int n, final int n2, final int n3) {
        this(tableModel, n, n2, n3, 0);
    }
    
    public TableModelEvent(final TableModel tableModel, final int firstRow, final int lastRow, final int column, final int type) {
        super(tableModel);
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.column = column;
        this.type = type;
    }
    
    public int getFirstRow() {
        return this.firstRow;
    }
    
    public int getLastRow() {
        return this.lastRow;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public int getType() {
        return this.type;
    }
}
