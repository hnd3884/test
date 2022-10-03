package sun.awt.windows;

import java.awt.MenuBar;
import java.awt.Menu;
import java.awt.peer.MenuBarPeer;

final class WMenuBarPeer extends WMenuPeer implements MenuBarPeer
{
    final WFramePeer framePeer;
    
    @Override
    public native void addMenu(final Menu p0);
    
    @Override
    public native void delMenu(final int p0);
    
    @Override
    public void addHelpMenu(final Menu menu) {
        this.addMenu(menu);
    }
    
    WMenuBarPeer(final MenuBar target) {
        this.target = target;
        this.framePeer = (WFramePeer)WToolkit.targetToPeer(target.getParent());
        if (this.framePeer != null) {
            this.framePeer.addChildPeer(this);
        }
        this.create(this.framePeer);
        this.checkMenuCreation();
    }
    
    native void create(final WFramePeer p0);
}
