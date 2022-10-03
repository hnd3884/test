package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.addressing.model.ActionNotSupportedException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;

public class WsaClientTube extends WsaTube
{
    protected boolean expectReply;
    
    public WsaClientTube(final WSDLPort wsdlPort, final WSBinding binding, final Tube next) {
        super(wsdlPort, binding, next);
        this.expectReply = true;
    }
    
    public WsaClientTube(final WsaClientTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.expectReply = true;
    }
    
    @Override
    public WsaClientTube copy(final TubeCloner cloner) {
        return new WsaClientTube(this, cloner);
    }
    
    @NotNull
    @Override
    public NextAction processRequest(final Packet request) {
        this.expectReply = request.expectReply;
        return this.doInvoke(this.next, request);
    }
    
    @NotNull
    @Override
    public NextAction processResponse(Packet response) {
        if (response.getMessage() != null) {
            response = this.validateInboundHeaders(response);
            response.addSatellite(new WsaPropertyBag(this.addressingVersion, this.soapVersion, response));
            final String msgId = AddressingUtils.getMessageID(response.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
            response.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.MessageIdFromRequest", msgId);
        }
        return this.doReturnWith(response);
    }
    
    @Override
    protected void validateAction(final Packet packet) {
        final WSDLBoundOperation wbo = this.getWSDLBoundOperation(packet);
        if (wbo == null) {
            return;
        }
        final String gotA = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
        if (gotA == null) {
            throw new WebServiceException(AddressingMessages.VALIDATION_CLIENT_NULL_ACTION());
        }
        final String expected = this.helper.getOutputAction(packet);
        if (expected != null && !gotA.equals(expected)) {
            throw new ActionNotSupportedException(gotA);
        }
    }
}
