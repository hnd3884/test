package sun.awt.windows;

import java.awt.Point;
import java.awt.Container;
import java.awt.Event;
import java.awt.MenuContainer;
import sun.awt.SunToolkit;
import java.awt.Component;
import java.awt.MenuComponent;
import sun.awt.AWTAccessor;
import java.awt.PopupMenu;
import java.awt.peer.PopupMenuPeer;

final class WPopupMenuPeer extends WMenuPeer implements PopupMenuPeer
{
    WPopupMenuPeer(final PopupMenu target) {
        this.target = target;
        MenuContainer menuContainer;
        if (AWTAccessor.getPopupMenuAccessor().isTrayIconPopup(target)) {
            menuContainer = AWTAccessor.getMenuComponentAccessor().getParent(target);
        }
        else {
            menuContainer = target.getParent();
        }
        if (menuContainer instanceof Component) {
            WComponentPeer wComponentPeer = (WComponentPeer)WToolkit.targetToPeer(menuContainer);
            if (wComponentPeer == null) {
                wComponentPeer = (WComponentPeer)WToolkit.targetToPeer(SunToolkit.getNativeContainer((Component)menuContainer));
            }
            wComponentPeer.addChildPeer(this);
            this.createMenu(wComponentPeer);
            this.checkMenuCreation();
            return;
        }
        throw new IllegalArgumentException("illegal popup menu container class");
    }
    
    private native void createMenu(final WComponentPeer p0);
    
    @Override
    public void show(final Event event) {
        final Component component = (Component)event.target;
        if (WToolkit.targetToPeer(component) == null) {
            final Container nativeContainer = SunToolkit.getNativeContainer(component);
            event.target = nativeContainer;
            for (Component parent = component; parent != nativeContainer; parent = parent.getParent()) {
                final Point location = parent.getLocation();
                event.x += location.x;
                event.y += location.y;
            }
        }
        this._show(event);
    }
    
    void show(final Component component, final Point point) {
        final WComponentPeer wComponentPeer = (WComponentPeer)WToolkit.targetToPeer(component);
        final Event event = new Event(component, 0L, 501, point.x, point.y, 0, 0);
        if (wComponentPeer == null) {
            event.target = SunToolkit.getNativeContainer(component);
        }
        event.x = point.x;
        event.y = point.y;
        this._show(event);
    }
    
    private native void _show(final Event p0);
}
