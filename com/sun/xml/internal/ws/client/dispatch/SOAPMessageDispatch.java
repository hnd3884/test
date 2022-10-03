package com.sun.xml.internal.ws.client.dispatch;

import javax.xml.soap.SOAPException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import javax.xml.soap.MimeHeader;
import com.sun.xml.internal.ws.transport.Headers;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;

public class SOAPMessageDispatch extends DispatchImpl<SOAPMessage>
{
    @Deprecated
    public SOAPMessageDispatch(final QName port, final Service.Mode mode, final WSServiceDelegate owner, final Tube pipe, final BindingImpl binding, final WSEndpointReference epr) {
        super(port, mode, owner, pipe, binding, epr);
    }
    
    public SOAPMessageDispatch(final WSPortInfo portInfo, final Service.Mode mode, final BindingImpl binding, final WSEndpointReference epr) {
        super(portInfo, mode, binding, epr);
    }
    
    @Override
    Packet createPacket(final SOAPMessage arg) {
        final Iterator iter = arg.getMimeHeaders().getAllHeaders();
        final Headers ch = new Headers();
        while (iter.hasNext()) {
            final MimeHeader mh = iter.next();
            ch.add(mh.getName(), mh.getValue());
        }
        final Packet packet = new Packet(SAAJFactory.create(arg));
        packet.invocationProperties.put("javax.xml.ws.http.request.headers", ch);
        return packet;
    }
    
    @Override
    SOAPMessage toReturnValue(final Packet response) {
        try {
            if (response == null || response.getMessage() == null) {
                throw new WebServiceException(DispatchMessages.INVALID_RESPONSE());
            }
            return response.getMessage().readAsSOAPMessage();
        }
        catch (final SOAPException e) {
            throw new WebServiceException(e);
        }
    }
}
