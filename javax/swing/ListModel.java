package javax.swing;

import javax.swing.event.ListDataListener;

public interface ListModel<E>
{
    int getSize();
    
    E getElementAt(final int p0);
    
    void addListDataListener(final ListDataListener p0);
    
    void removeListDataListener(final ListDataListener p0);
}
