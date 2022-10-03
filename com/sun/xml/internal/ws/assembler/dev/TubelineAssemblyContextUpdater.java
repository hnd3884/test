package com.sun.xml.internal.ws.assembler.dev;

import javax.xml.ws.WebServiceException;

public interface TubelineAssemblyContextUpdater
{
    void prepareContext(final ClientTubelineAssemblyContext p0) throws WebServiceException;
    
    void prepareContext(final ServerTubelineAssemblyContext p0) throws WebServiceException;
}
