package java.awt.peer;

import java.awt.Menu;

public interface MenuBarPeer extends MenuComponentPeer
{
    void addMenu(final Menu p0);
    
    void delMenu(final int p0);
    
    void addHelpMenu(final Menu p0);
}
