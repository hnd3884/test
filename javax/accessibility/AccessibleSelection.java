package javax.accessibility;

public interface AccessibleSelection
{
    int getAccessibleSelectionCount();
    
    Accessible getAccessibleSelection(final int p0);
    
    boolean isAccessibleChildSelected(final int p0);
    
    void addAccessibleSelection(final int p0);
    
    void removeAccessibleSelection(final int p0);
    
    void clearAccessibleSelection();
    
    void selectAllAccessibleSelection();
}
