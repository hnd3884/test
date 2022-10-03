package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.server.AbstractWebServiceContext;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import java.util.logging.Level;
import javax.xml.ws.WebServiceContext;
import com.sun.xml.internal.ws.api.server.AsyncProviderCallback;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.Invoker;
import java.util.logging.Logger;

public class AsyncProviderInvokerTube<T> extends ProviderInvokerTube<T>
{
    private static final Logger LOGGER;
    
    public AsyncProviderInvokerTube(final Invoker invoker, final ProviderArgumentsBuilder<T> argsBuilder) {
        super(invoker, argsBuilder);
    }
    
    @NotNull
    @Override
    public NextAction processRequest(@NotNull final Packet request) {
        final T param = this.argsBuilder.getParameter(request);
        final NoSuspendResumer resumer = new NoSuspendResumer();
        final AsyncProviderCallbackImpl callback = new AsyncProviderCallbackImpl(request, resumer);
        final AsyncWebServiceContext ctxt = new AsyncWebServiceContext(this.getEndpoint(), request);
        AsyncProviderInvokerTube.LOGGER.fine("Invoking AsyncProvider Endpoint");
        try {
            this.getInvoker(request).invokeAsyncProvider(request, param, callback, ctxt);
        }
        catch (final Throwable e) {
            AsyncProviderInvokerTube.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return this.doThrow(e);
        }
        synchronized (callback) {
            if (resumer.response != null) {
                final ThrowableContainerPropertySet tc = resumer.response.getSatellite(ThrowableContainerPropertySet.class);
                final Throwable t = (tc != null) ? tc.getThrowable() : null;
                return (t != null) ? this.doThrow(resumer.response, t) : this.doReturnWith(resumer.response);
            }
            callback.resumer = new FiberResumer();
            return this.doSuspend();
        }
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
        LOGGER = Logger.getLogger("com.sun.xml.internal.ws.server.AsyncProviderInvokerTube");
    }
    
    public class FiberResumer implements Resumer
    {
        private final Fiber fiber;
        
        public FiberResumer() {
            this.fiber = Fiber.current();
        }
        
        @Override
        public void onResume(final Packet response) {
            final ThrowableContainerPropertySet tc = response.getSatellite(ThrowableContainerPropertySet.class);
            final Throwable t = (tc != null) ? tc.getThrowable() : null;
            this.fiber.resume(t, response);
        }
    }
    
    private class NoSuspendResumer implements Resumer
    {
        protected Packet response;
        
        private NoSuspendResumer() {
            this.response = null;
        }
        
        @Override
        public void onResume(final Packet response) {
            this.response = response;
        }
    }
    
    public class AsyncProviderCallbackImpl implements AsyncProviderCallback<T>
    {
        private final Packet request;
        private Resumer resumer;
        
        public AsyncProviderCallbackImpl(final Packet request, final Resumer resumer) {
            this.request = request;
            this.resumer = resumer;
        }
        
        @Override
        public void send(@Nullable final T param) {
            if (param == null && this.request.transportBackChannel != null) {
                this.request.transportBackChannel.close();
            }
            final Packet packet = AsyncProviderInvokerTube.this.argsBuilder.getResponse(this.request, (T)param, InvokerTube.this.getEndpoint().getPort(), InvokerTube.this.getEndpoint().getBinding());
            synchronized (this) {
                this.resumer.onResume(packet);
            }
        }
        
        @Override
        public void sendError(@NotNull final Throwable t) {
            Exception e;
            if (t instanceof Exception) {
                e = (Exception)t;
            }
            else {
                e = new RuntimeException(t);
            }
            final Packet packet = AsyncProviderInvokerTube.this.argsBuilder.getResponse(this.request, e, InvokerTube.this.getEndpoint().getPort(), InvokerTube.this.getEndpoint().getBinding());
            synchronized (this) {
                this.resumer.onResume(packet);
            }
        }
    }
    
    public class AsyncWebServiceContext extends AbstractWebServiceContext
    {
        final Packet packet;
        
        public AsyncWebServiceContext(final WSEndpoint endpoint, final Packet packet) {
            super(endpoint);
            this.packet = packet;
        }
        
        @NotNull
        @Override
        public Packet getRequestPacket() {
            return this.packet;
        }
    }
    
    private interface Resumer
    {
        void onResume(final Packet p0);
    }
}
