package java.awt.event;

import java.util.EventListener;

public interface FocusListener extends EventListener
{
    void focusGained(final FocusEvent p0);
    
    void focusLost(final FocusEvent p0);
}
