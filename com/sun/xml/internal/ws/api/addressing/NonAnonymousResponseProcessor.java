package com.sun.xml.internal.ws.api.addressing;

import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.message.Packet;

public class NonAnonymousResponseProcessor
{
    private static final NonAnonymousResponseProcessor DEFAULT;
    
    public static NonAnonymousResponseProcessor getDefault() {
        return NonAnonymousResponseProcessor.DEFAULT;
    }
    
    protected NonAnonymousResponseProcessor() {
    }
    
    public Packet process(final Packet packet) {
        Fiber.CompletionCallback fiberCallback = null;
        final Fiber currentFiber = Fiber.getCurrentIfSet();
        if (currentFiber != null) {
            final Fiber.CompletionCallback currentFiberCallback = currentFiber.getCompletionCallback();
            if (currentFiberCallback != null) {
                fiberCallback = new Fiber.CompletionCallback() {
                    @Override
                    public void onCompletion(@NotNull final Packet response) {
                        currentFiberCallback.onCompletion(response);
                    }
                    
                    @Override
                    public void onCompletion(@NotNull final Throwable error) {
                        currentFiberCallback.onCompletion(error);
                    }
                };
                currentFiber.setCompletionCallback(null);
            }
        }
        final WSEndpoint<?> endpoint = packet.endpoint;
        final WSBinding binding = endpoint.getBinding();
        final Tube transport = TransportTubeFactory.create(Thread.currentThread().getContextClassLoader(), new ClientTubeAssemblerContext(packet.endpointAddress, endpoint.getPort(), (WSService)null, binding, endpoint.getContainer(), ((BindingImpl)binding).createCodec(), null, null));
        final Fiber fiber = endpoint.getEngine().createFiber();
        fiber.start(transport, packet, fiberCallback);
        final Packet copy = packet.copy(false);
        copy.endpointAddress = null;
        return copy;
    }
    
    static {
        DEFAULT = new NonAnonymousResponseProcessor();
    }
}
