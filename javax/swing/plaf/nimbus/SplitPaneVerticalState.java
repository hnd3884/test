package javax.swing.plaf.nimbus;

import javax.swing.JSplitPane;
import javax.swing.JComponent;

class SplitPaneVerticalState extends State
{
    SplitPaneVerticalState() {
        super("Vertical");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        return component instanceof JSplitPane && ((JSplitPane)component).getOrientation() == 1;
    }
}
