package com.sun.xml.internal.ws.assembler.jaxws;

import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.TubeFactory;

public final class BasicTransportTubeFactory implements TubeFactory
{
    @Override
    public Tube createTube(final ClientTubelineAssemblyContext context) throws WebServiceException {
        return context.getWrappedContext().createTransportTube();
    }
    
    @Override
    public Tube createTube(final ServerTubelineAssemblyContext context) throws WebServiceException {
        return context.getTubelineHead();
    }
}
