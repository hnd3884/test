package sun.awt;

import java.awt.Component;

public interface RequestFocusController
{
    boolean acceptRequestFocus(final Component p0, final Component p1, final boolean p2, final boolean p3, final CausedFocusEvent.Cause p4);
}
