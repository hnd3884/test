package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.namespace.QName;
import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import javax.xml.soap.SOAPFault;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.message.FaultDetailHeader;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.api.model.SEIModel;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;

abstract class WsaTube extends AbstractFilterTubeImpl
{
    @NotNull
    protected final WSDLPort wsdlPort;
    protected final WSBinding binding;
    final WsaTubeHelper helper;
    @NotNull
    protected final AddressingVersion addressingVersion;
    protected final SOAPVersion soapVersion;
    private final boolean addressingRequired;
    private static final Logger LOGGER;
    
    public WsaTube(final WSDLPort wsdlPort, final WSBinding binding, final Tube next) {
        super(next);
        this.wsdlPort = wsdlPort;
        this.addKnownHeadersToBinding(this.binding = binding);
        this.addressingVersion = binding.getAddressingVersion();
        this.soapVersion = binding.getSOAPVersion();
        this.helper = this.getTubeHelper();
        this.addressingRequired = AddressingVersion.isRequired(binding);
    }
    
    public WsaTube(final WsaTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.wsdlPort = that.wsdlPort;
        this.binding = that.binding;
        this.helper = that.helper;
        this.addressingVersion = that.addressingVersion;
        this.soapVersion = that.soapVersion;
        this.addressingRequired = that.addressingRequired;
    }
    
    private void addKnownHeadersToBinding(final WSBinding binding) {
        for (final AddressingVersion addrVersion : AddressingVersion.values()) {
            binding.addKnownHeader(addrVersion.actionTag);
            binding.addKnownHeader(addrVersion.faultDetailTag);
            binding.addKnownHeader(addrVersion.faultToTag);
            binding.addKnownHeader(addrVersion.fromTag);
            binding.addKnownHeader(addrVersion.messageIDTag);
            binding.addKnownHeader(addrVersion.relatesToTag);
            binding.addKnownHeader(addrVersion.replyToTag);
            binding.addKnownHeader(addrVersion.toTag);
        }
    }
    
    @NotNull
    @Override
    public NextAction processException(final Throwable t) {
        return super.processException(t);
    }
    
    protected WsaTubeHelper getTubeHelper() {
        if (this.binding.isFeatureEnabled(AddressingFeature.class)) {
            return new WsaTubeHelperImpl(this.wsdlPort, null, this.binding);
        }
        if (this.binding.isFeatureEnabled(MemberSubmissionAddressingFeature.class)) {
            return new com.sun.xml.internal.ws.addressing.v200408.WsaTubeHelperImpl(this.wsdlPort, null, this.binding);
        }
        throw new WebServiceException(AddressingMessages.ADDRESSING_NOT_ENABLED(this.getClass().getSimpleName()));
    }
    
    protected Packet validateInboundHeaders(final Packet packet) {
        SOAPFault soapFault;
        FaultDetailHeader s11FaultDetailHeader;
        try {
            this.checkMessageAddressingProperties(packet);
            return packet;
        }
        catch (final InvalidAddressingHeaderException e) {
            WsaTube.LOGGER.log(Level.WARNING, this.addressingVersion.getInvalidMapText() + ", Problem header:" + e.getProblemHeader() + ", Reason: " + e.getSubsubcode(), e);
            soapFault = this.helper.createInvalidAddressingHeaderFault(e, this.addressingVersion);
            s11FaultDetailHeader = new FaultDetailHeader(this.addressingVersion, this.addressingVersion.problemHeaderQNameTag.getLocalPart(), e.getProblemHeader());
        }
        catch (final MissingAddressingHeaderException e2) {
            WsaTube.LOGGER.log(Level.WARNING, this.addressingVersion.getMapRequiredText() + ", Problem header:" + e2.getMissingHeaderQName(), e2);
            soapFault = this.helper.newMapRequiredFault(e2);
            s11FaultDetailHeader = new FaultDetailHeader(this.addressingVersion, this.addressingVersion.problemHeaderQNameTag.getLocalPart(), e2.getMissingHeaderQName());
        }
        if (soapFault == null) {
            return packet;
        }
        if (this.wsdlPort != null && packet.getMessage().isOneWay(this.wsdlPort)) {
            return packet.createServerResponse(null, this.wsdlPort, null, this.binding);
        }
        final Message m = Messages.create(soapFault);
        if (this.soapVersion == SOAPVersion.SOAP_11) {
            m.getHeaders().add(s11FaultDetailHeader);
        }
        return packet.createServerResponse(m, this.wsdlPort, null, this.binding);
    }
    
    protected void checkMessageAddressingProperties(final Packet packet) {
        this.checkCardinality(packet);
    }
    
    final boolean isAddressingEngagedOrRequired(final Packet packet, final WSBinding binding) {
        if (AddressingVersion.isRequired(binding)) {
            return true;
        }
        if (packet == null) {
            return false;
        }
        if (packet.getMessage() == null) {
            return false;
        }
        if (packet.getMessage().getHeaders() != null) {
            return false;
        }
        final String action = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
        return action != null || true;
    }
    
    protected void checkCardinality(final Packet packet) {
        final Message message = packet.getMessage();
        if (message == null) {
            if (this.addressingRequired) {
                throw new WebServiceException(AddressingMessages.NULL_MESSAGE());
            }
        }
        else {
            final Iterator<Header> hIter = message.getHeaders().getHeaders(this.addressingVersion.nsUri, true);
            if (!hIter.hasNext()) {
                if (this.addressingRequired) {
                    throw new MissingAddressingHeaderException(this.addressingVersion.actionTag, packet);
                }
            }
            else {
                boolean foundFrom = false;
                boolean foundTo = false;
                boolean foundReplyTo = false;
                boolean foundFaultTo = false;
                boolean foundAction = false;
                boolean foundMessageId = false;
                boolean foundRelatesTo = false;
                QName duplicateHeader = null;
                while (hIter.hasNext()) {
                    final Header h = hIter.next();
                    if (!this.isInCurrentRole(h, this.binding)) {
                        continue;
                    }
                    final String local = h.getLocalPart();
                    if (local.equals(this.addressingVersion.fromTag.getLocalPart())) {
                        if (foundFrom) {
                            duplicateHeader = this.addressingVersion.fromTag;
                            break;
                        }
                        foundFrom = true;
                    }
                    else if (local.equals(this.addressingVersion.toTag.getLocalPart())) {
                        if (foundTo) {
                            duplicateHeader = this.addressingVersion.toTag;
                            break;
                        }
                        foundTo = true;
                    }
                    else {
                        if (local.equals(this.addressingVersion.replyToTag.getLocalPart())) {
                            if (foundReplyTo) {
                                duplicateHeader = this.addressingVersion.replyToTag;
                                break;
                            }
                            foundReplyTo = true;
                            try {
                                h.readAsEPR(this.addressingVersion);
                                continue;
                            }
                            catch (final XMLStreamException e) {
                                throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), e);
                            }
                        }
                        if (local.equals(this.addressingVersion.faultToTag.getLocalPart())) {
                            if (foundFaultTo) {
                                duplicateHeader = this.addressingVersion.faultToTag;
                                break;
                            }
                            foundFaultTo = true;
                            try {
                                h.readAsEPR(this.addressingVersion);
                                continue;
                            }
                            catch (final XMLStreamException e) {
                                throw new WebServiceException(AddressingMessages.FAULT_TO_CANNOT_PARSE(), e);
                            }
                        }
                        if (local.equals(this.addressingVersion.actionTag.getLocalPart())) {
                            if (foundAction) {
                                duplicateHeader = this.addressingVersion.actionTag;
                                break;
                            }
                            foundAction = true;
                        }
                        else if (local.equals(this.addressingVersion.messageIDTag.getLocalPart())) {
                            if (foundMessageId) {
                                duplicateHeader = this.addressingVersion.messageIDTag;
                                break;
                            }
                            foundMessageId = true;
                        }
                        else if (local.equals(this.addressingVersion.relatesToTag.getLocalPart())) {
                            foundRelatesTo = true;
                        }
                        else {
                            if (local.equals(this.addressingVersion.faultDetailTag.getLocalPart())) {
                                continue;
                            }
                            System.err.println(AddressingMessages.UNKNOWN_WSA_HEADER());
                        }
                    }
                }
                if (duplicateHeader != null) {
                    throw new InvalidAddressingHeaderException(duplicateHeader, this.addressingVersion.invalidCardinalityTag);
                }
                final boolean engaged = foundAction;
                if (engaged || this.addressingRequired) {
                    this.checkMandatoryHeaders(packet, foundAction, foundTo, foundReplyTo, foundFaultTo, foundMessageId, foundRelatesTo);
                }
            }
        }
    }
    
    final boolean isInCurrentRole(final Header header, final WSBinding binding) {
        return binding == null || ((SOAPBinding)binding).getRoles().contains(header.getRole(this.soapVersion));
    }
    
    protected final WSDLBoundOperation getWSDLBoundOperation(final Packet packet) {
        if (this.wsdlPort == null) {
            return null;
        }
        final QName opName = packet.getWSDLOperation();
        if (opName != null) {
            return this.wsdlPort.getBinding().get(opName);
        }
        return null;
    }
    
    protected void validateSOAPAction(final Packet packet) {
        final String gotA = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
        if (gotA == null) {
            throw new WebServiceException(AddressingMessages.VALIDATION_SERVER_NULL_ACTION());
        }
        if (packet.soapAction != null && !packet.soapAction.equals("\"\"") && !packet.soapAction.equals("\"" + gotA + "\"")) {
            throw new InvalidAddressingHeaderException(this.addressingVersion.actionTag, this.addressingVersion.actionMismatchTag);
        }
    }
    
    protected abstract void validateAction(final Packet p0);
    
    protected void checkMandatoryHeaders(final Packet packet, final boolean foundAction, final boolean foundTo, final boolean foundReplyTo, final boolean foundFaultTo, final boolean foundMessageId, final boolean foundRelatesTo) {
        if (!foundAction) {
            throw new MissingAddressingHeaderException(this.addressingVersion.actionTag, packet);
        }
        this.validateSOAPAction(packet);
    }
    
    static {
        LOGGER = Logger.getLogger(WsaTube.class.getName());
    }
}
