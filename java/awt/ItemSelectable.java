package java.awt;

import java.awt.event.ItemListener;

public interface ItemSelectable
{
    Object[] getSelectedObjects();
    
    void addItemListener(final ItemListener p0);
    
    void removeItemListener(final ItemListener p0);
}
