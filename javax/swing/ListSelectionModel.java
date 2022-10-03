package javax.swing;

import javax.swing.event.ListSelectionListener;

public interface ListSelectionModel
{
    public static final int SINGLE_SELECTION = 0;
    public static final int SINGLE_INTERVAL_SELECTION = 1;
    public static final int MULTIPLE_INTERVAL_SELECTION = 2;
    
    void setSelectionInterval(final int p0, final int p1);
    
    void addSelectionInterval(final int p0, final int p1);
    
    void removeSelectionInterval(final int p0, final int p1);
    
    int getMinSelectionIndex();
    
    int getMaxSelectionIndex();
    
    boolean isSelectedIndex(final int p0);
    
    int getAnchorSelectionIndex();
    
    void setAnchorSelectionIndex(final int p0);
    
    int getLeadSelectionIndex();
    
    void setLeadSelectionIndex(final int p0);
    
    void clearSelection();
    
    boolean isSelectionEmpty();
    
    void insertIndexInterval(final int p0, final int p1, final boolean p2);
    
    void removeIndexInterval(final int p0, final int p1);
    
    void setValueIsAdjusting(final boolean p0);
    
    boolean getValueIsAdjusting();
    
    void setSelectionMode(final int p0);
    
    int getSelectionMode();
    
    void addListSelectionListener(final ListSelectionListener p0);
    
    void removeListSelectionListener(final ListSelectionListener p0);
}
