package sun.awt;

import java.awt.SecondaryLoop;

public interface FwDispatcher
{
    boolean isDispatchThread();
    
    void scheduleDispatch(final Runnable p0);
    
    SecondaryLoop createSecondaryLoop();
}
