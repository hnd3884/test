package com.adventnet.tools.update.installer;

import com.adventnet.tools.update.XmlData;

public interface HotSwapHandler
{
    void preInvoke();
    
    void postInvoke();
    
    void revertPreInvoke(final Throwable p0);
    
    void revertPostInvoke(final Throwable p0);
    
    boolean allowPatchUpgrade(final XmlData p0);
}
