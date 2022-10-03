package sun.awt.windows;

import sun.awt.SunToolkit;
import java.awt.AWTEvent;
import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.MenuItem;
import java.awt.CheckboxMenuItem;
import java.awt.peer.CheckboxMenuItemPeer;

final class WCheckboxMenuItemPeer extends WMenuItemPeer implements CheckboxMenuItemPeer
{
    @Override
    public native void setState(final boolean p0);
    
    WCheckboxMenuItemPeer(final CheckboxMenuItem checkboxMenuItem) {
        super(checkboxMenuItem, true);
        this.setState(checkboxMenuItem.getState());
    }
    
    public void handleAction(final boolean b) {
        final CheckboxMenuItem checkboxMenuItem = (CheckboxMenuItem)this.target;
        SunToolkit.executeOnEventHandlerThread(checkboxMenuItem, new Runnable() {
            @Override
            public void run() {
                checkboxMenuItem.setState(b);
                WCheckboxMenuItemPeer.this.postEvent(new ItemEvent(checkboxMenuItem, 701, checkboxMenuItem.getLabel(), b ? 1 : 2));
            }
        });
    }
}
