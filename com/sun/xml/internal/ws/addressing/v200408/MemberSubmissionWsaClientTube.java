package com.sun.xml.internal.ws.addressing.v200408;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;
import com.sun.xml.internal.ws.addressing.WsaClientTube;

public class MemberSubmissionWsaClientTube extends WsaClientTube
{
    private final MemberSubmissionAddressing.Validation validation;
    
    public MemberSubmissionWsaClientTube(final WSDLPort wsdlPort, final WSBinding binding, final Tube next) {
        super(wsdlPort, binding, next);
        this.validation = binding.getFeature(MemberSubmissionAddressingFeature.class).getValidation();
    }
    
    public MemberSubmissionWsaClientTube(final MemberSubmissionWsaClientTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.validation = that.validation;
    }
    
    @Override
    public MemberSubmissionWsaClientTube copy(final TubeCloner cloner) {
        return new MemberSubmissionWsaClientTube(this, cloner);
    }
    
    @Override
    protected void checkMandatoryHeaders(final Packet packet, final boolean foundAction, final boolean foundTo, final boolean foundReplyTo, final boolean foundFaultTo, final boolean foundMessageID, final boolean foundRelatesTo) {
        super.checkMandatoryHeaders(packet, foundAction, foundTo, foundReplyTo, foundFaultTo, foundMessageID, foundRelatesTo);
        if (!foundTo) {
            throw new MissingAddressingHeaderException(this.addressingVersion.toTag, packet);
        }
        if (!this.validation.equals(MemberSubmissionAddressing.Validation.LAX) && this.expectReply && packet.getMessage() != null && !foundRelatesTo) {
            final String action = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
            if (!packet.getMessage().isFault() || !action.equals(this.addressingVersion.getDefaultFaultAction())) {
                throw new MissingAddressingHeaderException(this.addressingVersion.relatesToTag, packet);
            }
        }
    }
}
