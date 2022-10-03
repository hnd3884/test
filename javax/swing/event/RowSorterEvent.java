package javax.swing.event;

import javax.swing.RowSorter;
import java.util.EventObject;

public class RowSorterEvent extends EventObject
{
    private Type type;
    private int[] oldViewToModel;
    
    public RowSorterEvent(final RowSorter rowSorter) {
        this(rowSorter, Type.SORT_ORDER_CHANGED, null);
    }
    
    public RowSorterEvent(final RowSorter rowSorter, final Type type, final int[] oldViewToModel) {
        super(rowSorter);
        if (type == null) {
            throw new IllegalArgumentException("type must be non-null");
        }
        this.type = type;
        this.oldViewToModel = oldViewToModel;
    }
    
    @Override
    public RowSorter getSource() {
        return (RowSorter)super.getSource();
    }
    
    public Type getType() {
        return this.type;
    }
    
    public int convertPreviousRowIndexToModel(final int n) {
        if (this.oldViewToModel != null && n >= 0 && n < this.oldViewToModel.length) {
            return this.oldViewToModel[n];
        }
        return -1;
    }
    
    public int getPreviousRowCount() {
        return (this.oldViewToModel == null) ? 0 : this.oldViewToModel.length;
    }
    
    public enum Type
    {
        SORT_ORDER_CHANGED, 
        SORTED;
    }
}
