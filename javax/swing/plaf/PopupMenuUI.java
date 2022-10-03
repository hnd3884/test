package javax.swing.plaf;

import java.awt.Component;
import javax.swing.PopupFactory;
import javax.swing.Popup;
import javax.swing.JPopupMenu;
import java.awt.event.MouseEvent;

public abstract class PopupMenuUI extends ComponentUI
{
    public boolean isPopupTrigger(final MouseEvent mouseEvent) {
        return mouseEvent.isPopupTrigger();
    }
    
    public Popup getPopup(final JPopupMenu popupMenu, final int n, final int n2) {
        return PopupFactory.getSharedInstance().getPopup(popupMenu.getInvoker(), popupMenu, n, n2);
    }
}
