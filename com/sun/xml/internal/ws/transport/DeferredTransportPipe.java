package com.sun.xml.internal.ws.transport;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.TransportTubeFactory;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.developer.HttpConfigFeature;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;

public final class DeferredTransportPipe extends AbstractTubeImpl
{
    private Tube transport;
    private EndpointAddress address;
    private final ClassLoader classLoader;
    private final ClientTubeAssemblerContext context;
    
    public DeferredTransportPipe(final ClassLoader classLoader, final ClientTubeAssemblerContext context) {
        this.classLoader = classLoader;
        this.context = context;
        if (context.getBinding().getFeature(HttpConfigFeature.class) == null) {
            context.getBinding().getFeatures().mergeFeatures(new WebServiceFeature[] { new HttpConfigFeature() }, false);
        }
        try {
            this.transport = TransportTubeFactory.create(classLoader, context);
            this.address = context.getAddress();
        }
        catch (final Exception ex) {}
    }
    
    public DeferredTransportPipe(final DeferredTransportPipe that, final TubeCloner cloner) {
        super(that, cloner);
        this.classLoader = that.classLoader;
        this.context = that.context;
        if (that.transport != null) {
            this.transport = cloner.copy(that.transport);
            this.address = that.address;
        }
    }
    
    @Override
    public NextAction processException(@NotNull final Throwable t) {
        return this.transport.processException(t);
    }
    
    @Override
    public NextAction processRequest(@NotNull final Packet request) {
        if (request.endpointAddress == this.address) {
            return this.transport.processRequest(request);
        }
        if (this.transport != null) {
            this.transport.preDestroy();
            this.transport = null;
            this.address = null;
        }
        final ClientTubeAssemblerContext newContext = new ClientTubeAssemblerContext(request.endpointAddress, this.context.getWsdlModel(), this.context.getBindingProvider(), this.context.getBinding(), this.context.getContainer(), this.context.getCodec().copy(), this.context.getSEIModel(), this.context.getSEI());
        this.address = request.endpointAddress;
        this.transport = TransportTubeFactory.create(this.classLoader, newContext);
        assert this.transport != null;
        return this.transport.processRequest(request);
    }
    
    @Override
    public NextAction processResponse(@NotNull final Packet response) {
        if (this.transport != null) {
            return this.transport.processResponse(response);
        }
        return this.doReturnWith(response);
    }
    
    @Override
    public void preDestroy() {
        if (this.transport != null) {
            this.transport.preDestroy();
            this.transport = null;
            this.address = null;
        }
    }
    
    @Override
    public DeferredTransportPipe copy(final TubeCloner cloner) {
        return new DeferredTransportPipe(this, cloner);
    }
}
