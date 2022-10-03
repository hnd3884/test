package javax.swing;

import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import java.util.List;
import javax.swing.event.EventListenerList;

public abstract class RowSorter<M>
{
    private EventListenerList listenerList;
    
    public RowSorter() {
        this.listenerList = new EventListenerList();
    }
    
    public abstract M getModel();
    
    public abstract void toggleSortOrder(final int p0);
    
    public abstract int convertRowIndexToModel(final int p0);
    
    public abstract int convertRowIndexToView(final int p0);
    
    public abstract void setSortKeys(final List<? extends SortKey> p0);
    
    public abstract List<? extends SortKey> getSortKeys();
    
    public abstract int getViewRowCount();
    
    public abstract int getModelRowCount();
    
    public abstract void modelStructureChanged();
    
    public abstract void allRowsChanged();
    
    public abstract void rowsInserted(final int p0, final int p1);
    
    public abstract void rowsDeleted(final int p0, final int p1);
    
    public abstract void rowsUpdated(final int p0, final int p1);
    
    public abstract void rowsUpdated(final int p0, final int p1, final int p2);
    
    public void addRowSorterListener(final RowSorterListener rowSorterListener) {
        this.listenerList.add(RowSorterListener.class, rowSorterListener);
    }
    
    public void removeRowSorterListener(final RowSorterListener rowSorterListener) {
        this.listenerList.remove(RowSorterListener.class, rowSorterListener);
    }
    
    protected void fireSortOrderChanged() {
        this.fireRowSorterChanged(new RowSorterEvent(this));
    }
    
    protected void fireRowSorterChanged(final int[] array) {
        this.fireRowSorterChanged(new RowSorterEvent(this, RowSorterEvent.Type.SORTED, array));
    }
    
    void fireRowSorterChanged(final RowSorterEvent rowSorterEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == RowSorterListener.class) {
                ((RowSorterListener)listenerList[i + 1]).sorterChanged(rowSorterEvent);
            }
        }
    }
    
    public static class SortKey
    {
        private int column;
        private SortOrder sortOrder;
        
        public SortKey(final int column, final SortOrder sortOrder) {
            if (sortOrder == null) {
                throw new IllegalArgumentException("sort order must be non-null");
            }
            this.column = column;
            this.sortOrder = sortOrder;
        }
        
        public final int getColumn() {
            return this.column;
        }
        
        public final SortOrder getSortOrder() {
            return this.sortOrder;
        }
        
        @Override
        public int hashCode() {
            return 37 * (37 * 17 + this.column) + this.sortOrder.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o == this || (o instanceof SortKey && ((SortKey)o).column == this.column && ((SortKey)o).sortOrder == this.sortOrder);
        }
    }
}
