package com.sun.xml.internal.ws.dump;

import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.TubeFactory;

public final class MessageDumpingTubeFactory implements TubeFactory
{
    @Override
    public Tube createTube(final ClientTubelineAssemblyContext context) throws WebServiceException {
        final MessageDumpingFeature messageDumpingFeature = context.getBinding().getFeature(MessageDumpingFeature.class);
        if (messageDumpingFeature != null) {
            return new MessageDumpingTube(context.getTubelineHead(), messageDumpingFeature);
        }
        return context.getTubelineHead();
    }
    
    @Override
    public Tube createTube(final ServerTubelineAssemblyContext context) throws WebServiceException {
        final MessageDumpingFeature messageDumpingFeature = context.getEndpoint().getBinding().getFeature(MessageDumpingFeature.class);
        if (messageDumpingFeature != null) {
            return new MessageDumpingTube(context.getTubelineHead(), messageDumpingFeature);
        }
        return context.getTubelineHead();
    }
}
