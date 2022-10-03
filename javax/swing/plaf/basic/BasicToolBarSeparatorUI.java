package javax.swing.plaf.basic;

import java.awt.Graphics;
import javax.swing.UIManager;
import java.awt.Dimension;
import javax.swing.plaf.UIResource;
import javax.swing.JToolBar;
import javax.swing.JSeparator;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class BasicToolBarSeparatorUI extends BasicSeparatorUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new BasicToolBarSeparatorUI();
    }
    
    @Override
    protected void installDefaults(final JSeparator separator) {
        final Dimension separatorSize = ((JToolBar.Separator)separator).getSeparatorSize();
        if (separatorSize == null || separatorSize instanceof UIResource) {
            final JToolBar.Separator separator2 = (JToolBar.Separator)separator;
            Dimension separatorSize2 = (Dimension)UIManager.get("ToolBar.separatorSize");
            if (separatorSize2 != null) {
                if (separator2.getOrientation() == 0) {
                    separatorSize2 = new Dimension(separatorSize2.height, separatorSize2.width);
                }
                separator2.setSeparatorSize(separatorSize2);
            }
        }
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Dimension separatorSize = ((JToolBar.Separator)component).getSeparatorSize();
        if (separatorSize != null) {
            return separatorSize.getSize();
        }
        return null;
    }
}
