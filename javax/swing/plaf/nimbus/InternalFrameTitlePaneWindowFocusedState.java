package javax.swing.plaf.nimbus;

import javax.swing.JInternalFrame;
import javax.swing.JComponent;

class InternalFrameTitlePaneWindowFocusedState extends State
{
    InternalFrameTitlePaneWindowFocusedState() {
        super("WindowFocused");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        return component instanceof JInternalFrame && ((JInternalFrame)component).isSelected();
    }
}
