package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

public class DefaultFocusManager extends FocusManager
{
    final FocusTraversalPolicy gluePolicy;
    private final FocusTraversalPolicy layoutPolicy;
    private final LayoutComparator comparator;
    
    public DefaultFocusManager() {
        this.gluePolicy = new LegacyGlueFocusTraversalPolicy(this);
        this.layoutPolicy = new LegacyLayoutFocusTraversalPolicy(this);
        this.comparator = new LayoutComparator();
        this.setDefaultFocusTraversalPolicy(this.gluePolicy);
    }
    
    public Component getComponentAfter(final Container container, final Component component) {
        final Container container2 = container.isFocusCycleRoot() ? container : container.getFocusCycleRootAncestor();
        if (container2 == null) {
            return null;
        }
        final FocusTraversalPolicy focusTraversalPolicy = container2.getFocusTraversalPolicy();
        if (focusTraversalPolicy != this.gluePolicy) {
            return focusTraversalPolicy.getComponentAfter(container2, component);
        }
        this.comparator.setComponentOrientation(container2.getComponentOrientation());
        return this.layoutPolicy.getComponentAfter(container2, component);
    }
    
    public Component getComponentBefore(final Container container, final Component component) {
        final Container container2 = container.isFocusCycleRoot() ? container : container.getFocusCycleRootAncestor();
        if (container2 == null) {
            return null;
        }
        final FocusTraversalPolicy focusTraversalPolicy = container2.getFocusTraversalPolicy();
        if (focusTraversalPolicy != this.gluePolicy) {
            return focusTraversalPolicy.getComponentBefore(container2, component);
        }
        this.comparator.setComponentOrientation(container2.getComponentOrientation());
        return this.layoutPolicy.getComponentBefore(container2, component);
    }
    
    public Component getFirstComponent(final Container container) {
        final Container container2 = container.isFocusCycleRoot() ? container : container.getFocusCycleRootAncestor();
        if (container2 == null) {
            return null;
        }
        final FocusTraversalPolicy focusTraversalPolicy = container2.getFocusTraversalPolicy();
        if (focusTraversalPolicy != this.gluePolicy) {
            return focusTraversalPolicy.getFirstComponent(container2);
        }
        this.comparator.setComponentOrientation(container2.getComponentOrientation());
        return this.layoutPolicy.getFirstComponent(container2);
    }
    
    public Component getLastComponent(final Container container) {
        final Container container2 = container.isFocusCycleRoot() ? container : container.getFocusCycleRootAncestor();
        if (container2 == null) {
            return null;
        }
        final FocusTraversalPolicy focusTraversalPolicy = container2.getFocusTraversalPolicy();
        if (focusTraversalPolicy != this.gluePolicy) {
            return focusTraversalPolicy.getLastComponent(container2);
        }
        this.comparator.setComponentOrientation(container2.getComponentOrientation());
        return this.layoutPolicy.getLastComponent(container2);
    }
    
    public boolean compareTabOrder(final Component component, final Component component2) {
        return this.comparator.compare(component, component2) < 0;
    }
}
