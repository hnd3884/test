package com.sun.xml.internal.ws.server.provider;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.Invoker;
import java.util.logging.Logger;

public class SyncProviderInvokerTube<T> extends ProviderInvokerTube<T>
{
    private static final Logger LOGGER;
    
    public SyncProviderInvokerTube(final Invoker invoker, final ProviderArgumentsBuilder<T> argsBuilder) {
        super(invoker, argsBuilder);
    }
    
    @Override
    public NextAction processRequest(final Packet request) {
        final WSDLPort port = this.getEndpoint().getPort();
        final WSBinding binding = this.getEndpoint().getBinding();
        final T param = this.argsBuilder.getParameter(request);
        SyncProviderInvokerTube.LOGGER.fine("Invoking Provider Endpoint");
        T returnValue;
        try {
            returnValue = this.getInvoker(request).invokeProvider(request, param);
        }
        catch (final Exception e) {
            SyncProviderInvokerTube.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            final Packet response = this.argsBuilder.getResponse(request, e, port, binding);
            return this.doReturnWith(response);
        }
        if (returnValue == null && request.transportBackChannel != null) {
            request.transportBackChannel.close();
        }
        final Packet response2 = this.argsBuilder.getResponse(request, returnValue, port, binding);
        final ThrowableContainerPropertySet tc = response2.getSatellite(ThrowableContainerPropertySet.class);
        final Throwable t = (tc != null) ? tc.getThrowable() : null;
        return (t != null) ? this.doThrow(response2, t) : this.doReturnWith(response2);
    }
    
    @NotNull
    @Override
    public NextAction processResponse(@NotNull final Packet response) {
        return this.doReturnWith(response);
    }
    
    @NotNull
    @Override
    public NextAction processException(@NotNull final Throwable t) {
        return this.doThrow(t);
    }
    
    static {
        LOGGER = Logger.getLogger("com.sun.xml.internal.ws.server.SyncProviderInvokerTube");
    }
}
