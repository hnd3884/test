package javax.swing.plaf.nimbus;

import javax.swing.JInternalFrame;
import javax.swing.JComponent;

class InternalFrameWindowFocusedState extends State
{
    InternalFrameWindowFocusedState() {
        super("WindowFocused");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        return component instanceof JInternalFrame && ((JInternalFrame)component).isSelected();
    }
}
