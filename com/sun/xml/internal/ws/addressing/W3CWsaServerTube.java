package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import javax.xml.ws.soap.AddressingFeature;

public class W3CWsaServerTube extends WsaServerTube
{
    private final AddressingFeature af;
    
    public W3CWsaServerTube(final WSEndpoint endpoint, @NotNull final WSDLPort wsdlPort, final WSBinding binding, final Tube next) {
        super(endpoint, wsdlPort, binding, next);
        this.af = binding.getFeature(AddressingFeature.class);
    }
    
    public W3CWsaServerTube(final W3CWsaServerTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.af = that.af;
    }
    
    @Override
    public W3CWsaServerTube copy(final TubeCloner cloner) {
        return new W3CWsaServerTube(this, cloner);
    }
    
    @Override
    protected void checkMandatoryHeaders(final Packet packet, final boolean foundAction, final boolean foundTo, final boolean foundReplyTo, final boolean foundFaultTo, final boolean foundMessageId, final boolean foundRelatesTo) {
        super.checkMandatoryHeaders(packet, foundAction, foundTo, foundReplyTo, foundFaultTo, foundMessageId, foundRelatesTo);
        final WSDLBoundOperation wbo = this.getWSDLBoundOperation(packet);
        if (wbo != null && !wbo.getOperation().isOneWay() && !foundMessageId) {
            throw new MissingAddressingHeaderException(this.addressingVersion.messageIDTag, packet);
        }
    }
    
    @Override
    protected boolean isAnonymousRequired(@Nullable final WSDLBoundOperation wbo) {
        return this.getResponseRequirement(wbo) == WSDLBoundOperation.ANONYMOUS.required;
    }
    
    private WSDLBoundOperation.ANONYMOUS getResponseRequirement(@Nullable final WSDLBoundOperation wbo) {
        try {
            if (this.af.getResponses() == AddressingFeature.Responses.ANONYMOUS) {
                return WSDLBoundOperation.ANONYMOUS.required;
            }
            if (this.af.getResponses() == AddressingFeature.Responses.NON_ANONYMOUS) {
                return WSDLBoundOperation.ANONYMOUS.prohibited;
            }
        }
        catch (final NoSuchMethodError noSuchMethodError) {}
        return (wbo != null) ? wbo.getAnonymous() : WSDLBoundOperation.ANONYMOUS.optional;
    }
    
    @Override
    protected void checkAnonymousSemantics(final WSDLBoundOperation wbo, final WSEndpointReference replyTo, final WSEndpointReference faultTo) {
        String replyToValue = null;
        String faultToValue = null;
        if (replyTo != null) {
            replyToValue = replyTo.getAddress();
        }
        if (faultTo != null) {
            faultToValue = faultTo.getAddress();
        }
        final WSDLBoundOperation.ANONYMOUS responseRequirement = this.getResponseRequirement(wbo);
        switch (responseRequirement) {
            case prohibited: {
                if (replyToValue != null && replyToValue.equals(this.addressingVersion.anonymousUri)) {
                    throw new InvalidAddressingHeaderException(this.addressingVersion.replyToTag, W3CAddressingConstants.ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED);
                }
                if (faultToValue != null && faultToValue.equals(this.addressingVersion.anonymousUri)) {
                    throw new InvalidAddressingHeaderException(this.addressingVersion.faultToTag, W3CAddressingConstants.ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED);
                }
                break;
            }
            case required: {
                if (replyToValue != null && !replyToValue.equals(this.addressingVersion.anonymousUri)) {
                    throw new InvalidAddressingHeaderException(this.addressingVersion.replyToTag, W3CAddressingConstants.ONLY_ANONYMOUS_ADDRESS_SUPPORTED);
                }
                if (faultToValue != null && !faultToValue.equals(this.addressingVersion.anonymousUri)) {
                    throw new InvalidAddressingHeaderException(this.addressingVersion.faultToTag, W3CAddressingConstants.ONLY_ANONYMOUS_ADDRESS_SUPPORTED);
                }
                break;
            }
        }
    }
}
