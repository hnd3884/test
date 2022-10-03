package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.message.Headers;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.message.jaxb.JAXBDispatchMessage;
import javax.xml.transform.Source;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import javax.xml.bind.JAXBContext;

public class JAXBDispatch extends DispatchImpl<Object>
{
    private final JAXBContext jaxbcontext;
    private final boolean isContextSupported;
    
    @Deprecated
    public JAXBDispatch(final QName port, final JAXBContext jc, final Service.Mode mode, final WSServiceDelegate service, final Tube pipe, final BindingImpl binding, final WSEndpointReference epr) {
        super(port, mode, service, pipe, binding, epr);
        this.jaxbcontext = jc;
        this.isContextSupported = BindingContextFactory.isContextSupported(jc);
    }
    
    public JAXBDispatch(final WSPortInfo portInfo, final JAXBContext jc, final Service.Mode mode, final BindingImpl binding, final WSEndpointReference epr) {
        super(portInfo, mode, binding, epr);
        this.jaxbcontext = jc;
        this.isContextSupported = BindingContextFactory.isContextSupported(jc);
    }
    
    @Override
    Object toReturnValue(final Packet response) {
        try {
            final Unmarshaller unmarshaller = this.jaxbcontext.createUnmarshaller();
            final Message msg = response.getMessage();
            switch (this.mode) {
                case PAYLOAD: {
                    return msg.readPayloadAsJAXB(unmarshaller);
                }
                case MESSAGE: {
                    final Source result = msg.readEnvelopeAsSource();
                    return unmarshaller.unmarshal(result);
                }
                default: {
                    throw new WebServiceException("Unrecognized dispatch mode");
                }
            }
        }
        catch (final JAXBException e) {
            throw new WebServiceException(e);
        }
    }
    
    @Override
    Packet createPacket(final Object msg) {
        assert this.jaxbcontext != null;
        Message message;
        if (this.mode == Service.Mode.MESSAGE) {
            message = (this.isContextSupported ? new JAXBDispatchMessage(BindingContextFactory.create(this.jaxbcontext), msg, this.soapVersion) : new JAXBDispatchMessage(this.jaxbcontext, msg, this.soapVersion));
        }
        else if (msg == null) {
            message = Messages.createEmpty(this.soapVersion);
        }
        else {
            message = (this.isContextSupported ? Messages.create(this.jaxbcontext, msg, this.soapVersion) : Messages.createRaw(this.jaxbcontext, msg, this.soapVersion));
        }
        return new Packet(message);
    }
    
    @Override
    public void setOutboundHeaders(final Object... headers) {
        if (headers == null) {
            throw new IllegalArgumentException();
        }
        final Header[] hl = new Header[headers.length];
        for (int i = 0; i < hl.length; ++i) {
            if (headers[i] == null) {
                throw new IllegalArgumentException();
            }
            hl[i] = Headers.create(this.jaxbcontext, headers[i]);
        }
        super.setOutboundHeaders(hl);
    }
}
