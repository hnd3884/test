package javax.swing.plaf.basic;

import javax.swing.JComponent;
import sun.swing.MenuItemLayoutHelper;
import javax.swing.JPopupMenu;
import java.awt.Dimension;
import java.awt.Container;
import javax.swing.plaf.UIResource;
import javax.swing.BoxLayout;

public class DefaultMenuLayout extends BoxLayout implements UIResource
{
    public DefaultMenuLayout(final Container container, final int n) {
        super(container, n);
    }
    
    @Override
    public Dimension preferredLayoutSize(final Container container) {
        if (container instanceof JPopupMenu) {
            final JPopupMenu popupMenu = (JPopupMenu)container;
            MenuItemLayoutHelper.clearUsedClientProperties(popupMenu);
            if (popupMenu.getComponentCount() == 0) {
                return new Dimension(0, 0);
            }
        }
        super.invalidateLayout(container);
        return super.preferredLayoutSize(container);
    }
}
