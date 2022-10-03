package sun.applet;

import java.util.EventListener;

public interface AppletListener extends EventListener
{
    void appletStateChanged(final AppletEvent p0);
}
