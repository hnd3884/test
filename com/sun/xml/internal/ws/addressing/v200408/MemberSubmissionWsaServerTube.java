package com.sun.xml.internal.ws.addressing.v200408;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;
import com.sun.xml.internal.ws.addressing.WsaServerTube;

public class MemberSubmissionWsaServerTube extends WsaServerTube
{
    private final MemberSubmissionAddressing.Validation validation;
    
    public MemberSubmissionWsaServerTube(final WSEndpoint endpoint, @NotNull final WSDLPort wsdlPort, final WSBinding binding, final Tube next) {
        super(endpoint, wsdlPort, binding, next);
        this.validation = binding.getFeature(MemberSubmissionAddressingFeature.class).getValidation();
    }
    
    public MemberSubmissionWsaServerTube(final MemberSubmissionWsaServerTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.validation = that.validation;
    }
    
    @Override
    public MemberSubmissionWsaServerTube copy(final TubeCloner cloner) {
        return new MemberSubmissionWsaServerTube(this, cloner);
    }
    
    @Override
    protected void checkMandatoryHeaders(final Packet packet, final boolean foundAction, final boolean foundTo, final boolean foundReplyTo, final boolean foundFaultTo, final boolean foundMessageId, final boolean foundRelatesTo) {
        super.checkMandatoryHeaders(packet, foundAction, foundTo, foundReplyTo, foundFaultTo, foundMessageId, foundRelatesTo);
        if (!foundTo) {
            throw new MissingAddressingHeaderException(this.addressingVersion.toTag, packet);
        }
        if (this.wsdlPort != null) {
            final WSDLBoundOperation wbo = this.getWSDLBoundOperation(packet);
            if (wbo != null && !wbo.getOperation().isOneWay() && !foundReplyTo) {
                throw new MissingAddressingHeaderException(this.addressingVersion.replyToTag, packet);
            }
        }
        if (!this.validation.equals(MemberSubmissionAddressing.Validation.LAX) && (foundReplyTo || foundFaultTo) && !foundMessageId) {
            throw new MissingAddressingHeaderException(this.addressingVersion.messageIDTag, packet);
        }
    }
}
