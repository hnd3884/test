package sun.swing;

import javax.swing.Icon;
import javax.swing.JMenuItem;

public interface MenuItemCheckIconFactory
{
    Icon getIcon(final JMenuItem p0);
    
    boolean isCompatible(final Object p0, final String p1);
}
