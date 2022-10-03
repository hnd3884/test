package com.sun.xml.internal.bind;

import javax.xml.bind.Marshaller;

public interface CycleRecoverable
{
    Object onCycleDetected(final Context p0);
    
    public interface Context
    {
        Marshaller getMarshaller();
    }
}
