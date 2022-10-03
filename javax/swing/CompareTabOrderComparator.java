package javax.swing;

import java.awt.Component;
import java.util.Comparator;

final class CompareTabOrderComparator implements Comparator<Component>
{
    private final DefaultFocusManager defaultFocusManager;
    
    CompareTabOrderComparator(final DefaultFocusManager defaultFocusManager) {
        this.defaultFocusManager = defaultFocusManager;
    }
    
    @Override
    public int compare(final Component component, final Component component2) {
        if (component == component2) {
            return 0;
        }
        return this.defaultFocusManager.compareTabOrder(component, component2) ? -1 : 1;
    }
}
