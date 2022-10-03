package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;

final class SOAPSourceDispatch extends DispatchImpl<Source>
{
    @Deprecated
    public SOAPSourceDispatch(final QName port, final Service.Mode mode, final WSServiceDelegate owner, final Tube pipe, final BindingImpl binding, final WSEndpointReference epr) {
        super(port, mode, owner, pipe, binding, epr);
        assert !DispatchImpl.isXMLHttp(binding);
    }
    
    public SOAPSourceDispatch(final WSPortInfo portInfo, final Service.Mode mode, final BindingImpl binding, final WSEndpointReference epr) {
        super(portInfo, mode, binding, epr);
        assert !DispatchImpl.isXMLHttp(binding);
    }
    
    @Override
    Source toReturnValue(final Packet response) {
        final Message msg = response.getMessage();
        switch (this.mode) {
            case PAYLOAD: {
                return msg.readPayloadAsSource();
            }
            case MESSAGE: {
                return msg.readEnvelopeAsSource();
            }
            default: {
                throw new WebServiceException("Unrecognized dispatch mode");
            }
        }
    }
    
    @Override
    Packet createPacket(final Source msg) {
        Message message = null;
        if (msg == null) {
            message = Messages.createEmpty(this.soapVersion);
        }
        else {
            switch (this.mode) {
                case PAYLOAD: {
                    message = new PayloadSourceMessage(null, msg, this.setOutboundAttachments(), this.soapVersion);
                    break;
                }
                case MESSAGE: {
                    message = Messages.create(msg, this.soapVersion);
                    break;
                }
                default: {
                    throw new WebServiceException("Unrecognized message mode");
                }
            }
        }
        return new Packet(message);
    }
}
