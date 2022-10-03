package javax.swing.plaf.nimbus;

import javax.swing.JToolBar;
import javax.swing.JComponent;

class ToolBarEastState extends State
{
    ToolBarEastState() {
        super("East");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        return component instanceof JToolBar && NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)component) == "East";
    }
}
