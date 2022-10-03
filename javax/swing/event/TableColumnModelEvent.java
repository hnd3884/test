package javax.swing.event;

import javax.swing.table.TableColumnModel;
import java.util.EventObject;

public class TableColumnModelEvent extends EventObject
{
    protected int fromIndex;
    protected int toIndex;
    
    public TableColumnModelEvent(final TableColumnModel tableColumnModel, final int fromIndex, final int toIndex) {
        super(tableColumnModel);
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }
    
    public int getFromIndex() {
        return this.fromIndex;
    }
    
    public int getToIndex() {
        return this.toIndex;
    }
}
