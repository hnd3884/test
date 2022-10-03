package javax.swing;

import java.awt.Component;
import java.awt.ContainerOrderFocusTraversalPolicy;

class SwingContainerOrderFocusTraversalPolicy extends ContainerOrderFocusTraversalPolicy
{
    public boolean accept(final Component component) {
        return super.accept(component);
    }
}
