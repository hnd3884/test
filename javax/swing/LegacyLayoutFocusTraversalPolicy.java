package javax.swing;

import java.awt.Component;
import java.util.Comparator;

final class LegacyLayoutFocusTraversalPolicy extends LayoutFocusTraversalPolicy
{
    LegacyLayoutFocusTraversalPolicy(final DefaultFocusManager defaultFocusManager) {
        super(new CompareTabOrderComparator(defaultFocusManager));
    }
}
