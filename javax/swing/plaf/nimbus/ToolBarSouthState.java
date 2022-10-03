package javax.swing.plaf.nimbus;

import javax.swing.JToolBar;
import javax.swing.JComponent;

class ToolBarSouthState extends State
{
    ToolBarSouthState() {
        super("South");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        return component instanceof JToolBar && NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)component) == "South";
    }
}
