package sun.awt.windows;

import java.awt.MenuContainer;
import java.awt.MenuBar;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.peer.MenuPeer;

class WMenuPeer extends WMenuItemPeer implements MenuPeer
{
    @Override
    public native void addSeparator();
    
    @Override
    public void addItem(final MenuItem menuItem) {
        final WMenuItemPeer wMenuItemPeer = (WMenuItemPeer)WToolkit.targetToPeer(menuItem);
    }
    
    @Override
    public native void delItem(final int p0);
    
    WMenuPeer() {
    }
    
    WMenuPeer(final Menu target) {
        this.target = target;
        final MenuContainer parent = target.getParent();
        if (parent instanceof MenuBar) {
            final WMenuBarPeer parent2 = (WMenuBarPeer)WToolkit.targetToPeer(parent);
            (this.parent = parent2).addChildPeer(this);
            this.createMenu(parent2);
        }
        else {
            if (!(parent instanceof Menu)) {
                throw new IllegalArgumentException("unknown menu container class");
            }
            (this.parent = (WMenuPeer)WToolkit.targetToPeer(parent)).addChildPeer(this);
            this.createSubMenu(this.parent);
        }
        this.checkMenuCreation();
    }
    
    native void createMenu(final WMenuBarPeer p0);
    
    native void createSubMenu(final WMenuPeer p0);
}
