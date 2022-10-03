package com.sun.xml.internal.ws.assembler.dev;

import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.Pipe;

public interface TubelineAssemblyContext
{
    Pipe getAdaptedTubelineHead();
    
     <T> T getImplementation(final Class<T> p0);
    
    Tube getTubelineHead();
}
