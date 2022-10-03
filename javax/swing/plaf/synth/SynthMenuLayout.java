package javax.swing.plaf.synth;

import javax.swing.JPopupMenu;
import java.awt.Dimension;
import java.awt.Container;
import javax.swing.plaf.basic.DefaultMenuLayout;

class SynthMenuLayout extends DefaultMenuLayout
{
    public SynthMenuLayout(final Container container, final int n) {
        super(container, n);
    }
    
    @Override
    public Dimension preferredLayoutSize(final Container container) {
        if (container instanceof JPopupMenu) {
            ((JPopupMenu)container).putClientProperty(SynthMenuItemLayoutHelper.MAX_ACC_OR_ARROW_WIDTH, null);
        }
        return super.preferredLayoutSize(container);
    }
}
