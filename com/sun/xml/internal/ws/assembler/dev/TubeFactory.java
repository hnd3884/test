package com.sun.xml.internal.ws.assembler.dev;

import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.pipe.Tube;

public interface TubeFactory
{
    Tube createTube(final ClientTubelineAssemblyContext p0) throws WebServiceException;
    
    Tube createTube(final ServerTubelineAssemblyContext p0) throws WebServiceException;
}
