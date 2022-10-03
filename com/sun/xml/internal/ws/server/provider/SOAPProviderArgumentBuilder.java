package com.sun.xml.internal.ws.server.provider;

import java.util.Iterator;
import java.util.Map;
import javax.xml.soap.MimeHeaders;
import java.util.ArrayList;
import javax.xml.soap.MimeHeader;
import java.util.List;
import java.util.HashMap;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.istack.internal.Nullable;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Service;
import com.sun.xml.internal.ws.api.SOAPVersion;

abstract class SOAPProviderArgumentBuilder<T> extends ProviderArgumentsBuilder<T>
{
    protected final SOAPVersion soapVersion;
    
    private SOAPProviderArgumentBuilder(final SOAPVersion soapVersion) {
        this.soapVersion = soapVersion;
    }
    
    static ProviderArgumentsBuilder create(final ProviderEndpointModel model, final SOAPVersion soapVersion) {
        if (model.mode == Service.Mode.PAYLOAD) {
            return new PayloadSource(soapVersion);
        }
        if (model.datatype == Source.class) {
            return new MessageSource(soapVersion);
        }
        if (model.datatype == SOAPMessage.class) {
            return new SOAPMessageParameter(soapVersion);
        }
        if (model.datatype == Message.class) {
            return new MessageProviderArgumentBuilder(soapVersion);
        }
        throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(model.implClass, model.datatype));
    }
    
    private static final class PayloadSource extends SOAPProviderArgumentBuilder<Source>
    {
        PayloadSource(final SOAPVersion soapVersion) {
            super(soapVersion, null);
        }
        
        @Override
        public Source getParameter(final Packet packet) {
            return packet.getMessage().readPayloadAsSource();
        }
        
        @Override
        protected Message getResponseMessage(final Source source) {
            return Messages.createUsingPayload(source, this.soapVersion);
        }
        
        @Override
        protected Message getResponseMessage(final Exception e) {
            return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, e);
        }
    }
    
    private static final class MessageSource extends SOAPProviderArgumentBuilder<Source>
    {
        MessageSource(final SOAPVersion soapVersion) {
            super(soapVersion, null);
        }
        
        @Override
        public Source getParameter(final Packet packet) {
            return packet.getMessage().readEnvelopeAsSource();
        }
        
        @Override
        protected Message getResponseMessage(final Source source) {
            return Messages.create(source, this.soapVersion);
        }
        
        @Override
        protected Message getResponseMessage(final Exception e) {
            return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, e);
        }
    }
    
    private static final class SOAPMessageParameter extends SOAPProviderArgumentBuilder<SOAPMessage>
    {
        SOAPMessageParameter(final SOAPVersion soapVersion) {
            super(soapVersion, null);
        }
        
        @Override
        public SOAPMessage getParameter(final Packet packet) {
            try {
                return packet.getMessage().readAsSOAPMessage(packet, true);
            }
            catch (final SOAPException se) {
                throw new WebServiceException(se);
            }
        }
        
        @Override
        protected Message getResponseMessage(final SOAPMessage soapMsg) {
            return Messages.create(soapMsg);
        }
        
        @Override
        protected Message getResponseMessage(final Exception e) {
            return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, null, e);
        }
        
        @Override
        protected Packet getResponse(final Packet request, @Nullable final SOAPMessage returnValue, final WSDLPort port, final WSBinding binding) {
            final Packet response = super.getResponse(request, returnValue, port, binding);
            if (returnValue != null && response.supports("com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers")) {
                final MimeHeaders hdrs = returnValue.getMimeHeaders();
                final Map<String, List<String>> headers = new HashMap<String, List<String>>();
                final Iterator i = hdrs.getAllHeaders();
                while (i.hasNext()) {
                    final MimeHeader header = i.next();
                    if (header.getName().equalsIgnoreCase("SOAPAction")) {
                        continue;
                    }
                    List<String> list = headers.get(header.getName());
                    if (list == null) {
                        list = new ArrayList<String>();
                        headers.put(header.getName(), list);
                    }
                    list.add(header.getValue());
                }
                response.put("com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers", headers);
            }
            return response;
        }
    }
}
