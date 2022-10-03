package java.awt;

import java.awt.peer.ComponentPeer;

public class DefaultFocusTraversalPolicy extends ContainerOrderFocusTraversalPolicy
{
    private static final long serialVersionUID = 8876966522510157497L;
    
    @Override
    protected boolean accept(final Component component) {
        if (!component.isVisible() || !component.isDisplayable() || !component.isEnabled()) {
            return false;
        }
        if (!(component instanceof Window)) {
            for (Container container = component.getParent(); container != null; container = container.getParent()) {
                if (!container.isEnabled() && !container.isLightweight()) {
                    return false;
                }
                if (container instanceof Window) {
                    break;
                }
            }
        }
        final boolean focusable = component.isFocusable();
        if (component.isFocusTraversableOverridden()) {
            return focusable;
        }
        final ComponentPeer peer = component.getPeer();
        return peer != null && peer.isFocusable();
    }
}
