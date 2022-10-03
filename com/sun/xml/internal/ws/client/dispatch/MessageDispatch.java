package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import javax.xml.ws.Service;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.message.Message;

public class MessageDispatch extends DispatchImpl<Message>
{
    @Deprecated
    public MessageDispatch(final QName port, final WSServiceDelegate service, final Tube pipe, final BindingImpl binding, final WSEndpointReference epr) {
        super(port, Service.Mode.MESSAGE, service, pipe, binding, epr);
    }
    
    public MessageDispatch(final WSPortInfo portInfo, final BindingImpl binding, final WSEndpointReference epr) {
        super(portInfo, Service.Mode.MESSAGE, binding, epr, true);
    }
    
    @Override
    Message toReturnValue(final Packet response) {
        return response.getMessage();
    }
    
    @Override
    Packet createPacket(final Message msg) {
        return new Packet(msg);
    }
}
