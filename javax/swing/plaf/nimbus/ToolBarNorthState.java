package javax.swing.plaf.nimbus;

import javax.swing.JToolBar;
import javax.swing.JComponent;

class ToolBarNorthState extends State
{
    ToolBarNorthState() {
        super("North");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        return component instanceof JToolBar && NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)component) == "North";
    }
}
