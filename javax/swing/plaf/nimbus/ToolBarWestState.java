package javax.swing.plaf.nimbus;

import javax.swing.JToolBar;
import javax.swing.JComponent;

class ToolBarWestState extends State
{
    ToolBarWestState() {
        super("West");
    }
    
    @Override
    protected boolean isInState(final JComponent component) {
        return component instanceof JToolBar && NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)component) == "West";
    }
}
