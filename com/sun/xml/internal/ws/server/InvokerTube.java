package com.sun.xml.internal.ws.server;

import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceContext;
import com.sun.xml.internal.ws.api.server.AsyncProviderCallback;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.Invoker;

public abstract class InvokerTube<T> extends com.sun.xml.internal.ws.server.sei.InvokerTube<Invoker> implements EndpointAwareTube
{
    private WSEndpoint endpoint;
    private static final ThreadLocal<Packet> packets;
    private final Invoker wrapper;
    
    protected InvokerTube(final Invoker invoker) {
        super(invoker);
        this.wrapper = new Invoker() {
            @Override
            public Object invoke(final Packet p, final Method m, final Object... args) throws InvocationTargetException, IllegalAccessException {
                final Packet old = this.set(p);
                try {
                    return InvokerTube.this.invoker.invoke(p, m, args);
                }
                finally {
                    this.set(old);
                }
            }
            
            @Override
            public <T> T invokeProvider(final Packet p, final T arg) throws IllegalAccessException, InvocationTargetException {
                final Packet old = this.set(p);
                try {
                    return ((Invoker)InvokerTube.this.invoker).invokeProvider(p, arg);
                }
                finally {
                    this.set(old);
                }
            }
            
            @Override
            public <T> void invokeAsyncProvider(final Packet p, final T arg, final AsyncProviderCallback cbak, final WebServiceContext ctxt) throws IllegalAccessException, InvocationTargetException {
                final Packet old = this.set(p);
                try {
                    ((Invoker)InvokerTube.this.invoker).invokeAsyncProvider(p, arg, cbak, ctxt);
                }
                finally {
                    this.set(old);
                }
            }
            
            private Packet set(final Packet p) {
                final Packet old = InvokerTube.packets.get();
                InvokerTube.packets.set(p);
                return old;
            }
        };
    }
    
    @Override
    public void setEndpoint(final WSEndpoint endpoint) {
        this.endpoint = endpoint;
        final WSWebServiceContext webServiceContext = new AbstractWebServiceContext(endpoint) {
            @Nullable
            @Override
            public Packet getRequestPacket() {
                final Packet p = InvokerTube.packets.get();
                return p;
            }
        };
        ((Invoker)this.invoker).start(webServiceContext, endpoint);
    }
    
    protected WSEndpoint getEndpoint() {
        return this.endpoint;
    }
    
    @NotNull
    @Override
    public final Invoker getInvoker(final Packet request) {
        return this.wrapper;
    }
    
    @Override
    public final AbstractTubeImpl copy(final TubeCloner cloner) {
        cloner.add(this, this);
        return this;
    }
    
    @Override
    public void preDestroy() {
        ((Invoker)this.invoker).dispose();
    }
    
    @NotNull
    public static Packet getCurrentPacket() {
        final Packet packet = InvokerTube.packets.get();
        if (packet == null) {
            throw new WebServiceException(ServerMessages.NO_CURRENT_PACKET());
        }
        return packet;
    }
    
    static {
        packets = new ThreadLocal<Packet>();
    }
}
