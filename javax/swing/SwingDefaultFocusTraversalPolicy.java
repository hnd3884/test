package javax.swing;

import java.awt.Component;
import java.awt.DefaultFocusTraversalPolicy;

class SwingDefaultFocusTraversalPolicy extends DefaultFocusTraversalPolicy
{
    public boolean accept(final Component component) {
        return super.accept(component);
    }
}
