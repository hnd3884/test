package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.api.server.ProviderInvokerTubeFactory;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.server.Invoker;
import javax.xml.ws.Provider;
import com.sun.xml.internal.ws.server.InvokerTube;

public abstract class ProviderInvokerTube<T> extends InvokerTube<Provider<T>>
{
    protected ProviderArgumentsBuilder<T> argsBuilder;
    
    ProviderInvokerTube(final Invoker invoker, final ProviderArgumentsBuilder<T> argsBuilder) {
        super(invoker);
        this.argsBuilder = argsBuilder;
    }
    
    public static <T> ProviderInvokerTube<T> create(final Class<T> implType, final WSBinding binding, final Invoker invoker, final Container container) {
        final ProviderEndpointModel<T> model = new ProviderEndpointModel<T>(implType, binding);
        final ProviderArgumentsBuilder<?> argsBuilder = ProviderArgumentsBuilder.create(model, binding);
        if (binding instanceof SOAPBindingImpl) {
            ((SOAPBindingImpl)binding).setMode(model.mode);
        }
        return ProviderInvokerTubeFactory.create(null, container, implType, invoker, argsBuilder, model.isAsync);
    }
}
