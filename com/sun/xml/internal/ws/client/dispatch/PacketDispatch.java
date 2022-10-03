package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.api.pipe.Fiber;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.api.client.ThrowableInPacketCompletionFeature;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import javax.xml.ws.Service;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.message.Packet;

public class PacketDispatch extends DispatchImpl<Packet>
{
    private final boolean isDeliverThrowableInPacket;
    
    @Deprecated
    public PacketDispatch(final QName port, final WSServiceDelegate owner, final Tube pipe, final BindingImpl binding, @Nullable final WSEndpointReference epr) {
        super(port, Service.Mode.MESSAGE, owner, pipe, binding, epr);
        this.isDeliverThrowableInPacket = this.calculateIsDeliverThrowableInPacket(binding);
    }
    
    public PacketDispatch(final WSPortInfo portInfo, final Tube pipe, final BindingImpl binding, final WSEndpointReference epr) {
        this(portInfo, pipe, binding, epr, true);
    }
    
    public PacketDispatch(final WSPortInfo portInfo, final Tube pipe, final BindingImpl binding, final WSEndpointReference epr, final boolean allowFaultResponseMsg) {
        super(portInfo, Service.Mode.MESSAGE, pipe, binding, epr, allowFaultResponseMsg);
        this.isDeliverThrowableInPacket = this.calculateIsDeliverThrowableInPacket(binding);
    }
    
    public PacketDispatch(final WSPortInfo portInfo, final BindingImpl binding, final WSEndpointReference epr) {
        super(portInfo, Service.Mode.MESSAGE, binding, epr, true);
        this.isDeliverThrowableInPacket = this.calculateIsDeliverThrowableInPacket(binding);
    }
    
    private boolean calculateIsDeliverThrowableInPacket(final BindingImpl binding) {
        return binding.isFeatureEnabled(ThrowableInPacketCompletionFeature.class);
    }
    
    @Override
    protected void configureFiber(final Fiber fiber) {
        fiber.setDeliverThrowableInPacket(this.isDeliverThrowableInPacket);
    }
    
    @Override
    Packet toReturnValue(final Packet response) {
        return response;
    }
    
    @Override
    Packet createPacket(final Packet request) {
        return request;
    }
}
