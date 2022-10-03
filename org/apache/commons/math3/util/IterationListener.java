package org.apache.commons.math3.util;

import java.util.EventListener;

public interface IterationListener extends EventListener
{
    void initializationPerformed(final IterationEvent p0);
    
    void iterationPerformed(final IterationEvent p0);
    
    void iterationStarted(final IterationEvent p0);
    
    void terminationPerformed(final IterationEvent p0);
}
