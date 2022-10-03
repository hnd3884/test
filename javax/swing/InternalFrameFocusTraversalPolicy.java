package javax.swing;

import java.awt.Container;
import java.awt.Component;
import java.awt.FocusTraversalPolicy;

public abstract class InternalFrameFocusTraversalPolicy extends FocusTraversalPolicy
{
    public Component getInitialComponent(final JInternalFrame internalFrame) {
        return this.getDefaultComponent(internalFrame);
    }
}
