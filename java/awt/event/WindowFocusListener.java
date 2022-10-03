package java.awt.event;

import java.util.EventListener;

public interface WindowFocusListener extends EventListener
{
    void windowGainedFocus(final WindowEvent p0);
    
    void windowLostFocus(final WindowEvent p0);
}
