package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;

public class W3CWsaClientTube extends WsaClientTube
{
    public W3CWsaClientTube(final WSDLPort wsdlPort, final WSBinding binding, final Tube next) {
        super(wsdlPort, binding, next);
    }
    
    public W3CWsaClientTube(final WsaClientTube that, final TubeCloner cloner) {
        super(that, cloner);
    }
    
    @Override
    public W3CWsaClientTube copy(final TubeCloner cloner) {
        return new W3CWsaClientTube(this, cloner);
    }
    
    @Override
    protected void checkMandatoryHeaders(final Packet packet, final boolean foundAction, final boolean foundTo, final boolean foundReplyTo, final boolean foundFaultTo, final boolean foundMessageID, final boolean foundRelatesTo) {
        super.checkMandatoryHeaders(packet, foundAction, foundTo, foundReplyTo, foundFaultTo, foundMessageID, foundRelatesTo);
        if (this.expectReply && packet.getMessage() != null && !foundRelatesTo) {
            final String action = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
            if (!packet.getMessage().isFault() || !action.equals(this.addressingVersion.getDefaultFaultAction())) {
                throw new MissingAddressingHeaderException(this.addressingVersion.relatesToTag, packet);
            }
        }
    }
}
