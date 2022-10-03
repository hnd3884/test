package java.awt.peer;

import java.awt.MenuItem;

public interface MenuPeer extends MenuItemPeer
{
    void addSeparator();
    
    void addItem(final MenuItem p0);
    
    void delItem(final int p0);
}
