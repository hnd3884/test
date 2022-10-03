package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.ws.soap.SOAPBinding;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.Message;

public abstract class ProviderArgumentsBuilder<T>
{
    protected abstract Message getResponseMessage(final Exception p0);
    
    protected Packet getResponse(final Packet request, final Exception e, final WSDLPort port, final WSBinding binding) {
        final Message message = this.getResponseMessage(e);
        final Packet response = request.createServerResponse(message, port, null, binding);
        return response;
    }
    
    public abstract T getParameter(final Packet p0);
    
    protected abstract Message getResponseMessage(final T p0);
    
    protected Packet getResponse(final Packet request, @Nullable final T returnValue, final WSDLPort port, final WSBinding binding) {
        Message message = null;
        if (returnValue != null) {
            message = this.getResponseMessage(returnValue);
        }
        final Packet response = request.createServerResponse(message, port, null, binding);
        return response;
    }
    
    public static ProviderArgumentsBuilder<?> create(final ProviderEndpointModel model, final WSBinding binding) {
        if (model.datatype == Packet.class) {
            return new PacketProviderArgumentsBuilder(binding.getSOAPVersion());
        }
        return (binding instanceof SOAPBinding) ? SOAPProviderArgumentBuilder.create(model, binding.getSOAPVersion()) : XMLProviderArgumentBuilder.createBuilder(model, binding);
    }
    
    private static class PacketProviderArgumentsBuilder extends ProviderArgumentsBuilder<Packet>
    {
        private final SOAPVersion soapVersion;
        
        public PacketProviderArgumentsBuilder(final SOAPVersion soapVersion) {
            this.soapVersion = soapVersion;
        }
        
        @Override
        protected Message getResponseMessage(final Exception e) {
            return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, e);
        }
        
        @Override
        public Packet getParameter(final Packet packet) {
            return packet;
        }
        
        @Override
        protected Message getResponseMessage(final Packet returnValue) {
            throw new IllegalStateException();
        }
        
        @Override
        protected Packet getResponse(final Packet request, @Nullable final Packet returnValue, final WSDLPort port, final WSBinding binding) {
            return returnValue;
        }
    }
}
