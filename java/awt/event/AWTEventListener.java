package java.awt.event;

import java.awt.AWTEvent;
import java.util.EventListener;

public interface AWTEventListener extends EventListener
{
    void eventDispatched(final AWTEvent p0);
}
