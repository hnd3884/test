package javax.accessibility;

public interface AccessibleTable
{
    Accessible getAccessibleCaption();
    
    void setAccessibleCaption(final Accessible p0);
    
    Accessible getAccessibleSummary();
    
    void setAccessibleSummary(final Accessible p0);
    
    int getAccessibleRowCount();
    
    int getAccessibleColumnCount();
    
    Accessible getAccessibleAt(final int p0, final int p1);
    
    int getAccessibleRowExtentAt(final int p0, final int p1);
    
    int getAccessibleColumnExtentAt(final int p0, final int p1);
    
    AccessibleTable getAccessibleRowHeader();
    
    void setAccessibleRowHeader(final AccessibleTable p0);
    
    AccessibleTable getAccessibleColumnHeader();
    
    void setAccessibleColumnHeader(final AccessibleTable p0);
    
    Accessible getAccessibleRowDescription(final int p0);
    
    void setAccessibleRowDescription(final int p0, final Accessible p1);
    
    Accessible getAccessibleColumnDescription(final int p0);
    
    void setAccessibleColumnDescription(final int p0, final Accessible p1);
    
    boolean isAccessibleSelected(final int p0, final int p1);
    
    boolean isAccessibleRowSelected(final int p0);
    
    boolean isAccessibleColumnSelected(final int p0);
    
    int[] getSelectedAccessibleRows();
    
    int[] getSelectedAccessibleColumns();
}
