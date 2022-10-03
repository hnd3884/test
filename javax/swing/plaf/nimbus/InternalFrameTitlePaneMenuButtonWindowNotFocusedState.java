package javax.swing.plaf.nimbus;

import java.awt.Container;
import javax.swing.JInternalFrame;
import javax.swing.JComponent;

class InternalFrameTitlePaneMenuButtonWindowNotFocusedState extends State
{
    InternalFrameTitlePaneMenuButtonWindowNotFocusedState() {
        super("WindowNotFocused");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        Container parent;
        for (parent = component; parent.getParent() != null && !(parent instanceof JInternalFrame); parent = parent.getParent()) {}
        return parent instanceof JInternalFrame && !((JInternalFrame)parent).isSelected();
    }
}
