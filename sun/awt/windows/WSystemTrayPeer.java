package sun.awt.windows;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.SystemTray;
import java.awt.peer.SystemTrayPeer;

final class WSystemTrayPeer extends WObjectPeer implements SystemTrayPeer
{
    WSystemTrayPeer(final SystemTray target) {
        this.target = target;
    }
    
    @Override
    public Dimension getTrayIconSize() {
        return new Dimension(16, 16);
    }
    
    public boolean isSupported() {
        return ((WToolkit)Toolkit.getDefaultToolkit()).isTraySupported();
    }
    
    @Override
    protected void disposeImpl() {
    }
}
