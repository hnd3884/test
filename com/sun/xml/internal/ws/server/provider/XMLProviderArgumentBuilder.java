package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.encoding.xml.XMLMessage;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.ServerMessages;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import javax.xml.ws.http.HTTPException;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.message.Packet;

abstract class XMLProviderArgumentBuilder<T> extends ProviderArgumentsBuilder<T>
{
    @Override
    protected Packet getResponse(final Packet request, final Exception e, final WSDLPort port, final WSBinding binding) {
        final Packet response = super.getResponse(request, e, port, binding);
        if (e instanceof HTTPException && response.supports("javax.xml.ws.http.response.code")) {
            response.put("javax.xml.ws.http.response.code", ((HTTPException)e).getStatusCode());
        }
        return response;
    }
    
    static XMLProviderArgumentBuilder createBuilder(final ProviderEndpointModel model, final WSBinding binding) {
        if (model.mode == Service.Mode.PAYLOAD) {
            return new PayloadSource();
        }
        if (model.datatype == Source.class) {
            return new PayloadSource();
        }
        if (model.datatype == DataSource.class) {
            return new DataSourceParameter(binding);
        }
        throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(model.implClass, model.datatype));
    }
    
    private static final class PayloadSource extends XMLProviderArgumentBuilder<Source>
    {
        @Override
        public Source getParameter(final Packet packet) {
            return packet.getMessage().readPayloadAsSource();
        }
        
        public Message getResponseMessage(final Source source) {
            return Messages.createUsingPayload(source, SOAPVersion.SOAP_11);
        }
        
        @Override
        protected Message getResponseMessage(final Exception e) {
            return XMLMessage.create(e);
        }
    }
    
    private static final class DataSourceParameter extends XMLProviderArgumentBuilder<DataSource>
    {
        private final WSBinding binding;
        
        DataSourceParameter(final WSBinding binding) {
            this.binding = binding;
        }
        
        @Override
        public DataSource getParameter(final Packet packet) {
            final Message msg = packet.getInternalMessage();
            return (msg instanceof XMLMessage.MessageDataSource) ? ((XMLMessage.MessageDataSource)msg).getDataSource() : XMLMessage.getDataSource(msg, this.binding.getFeatures());
        }
        
        public Message getResponseMessage(final DataSource ds) {
            return XMLMessage.create(ds, this.binding.getFeatures());
        }
        
        @Override
        protected Message getResponseMessage(final Exception e) {
            return XMLMessage.create(e);
        }
    }
}
