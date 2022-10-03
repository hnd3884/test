package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.addressing.model.ActionNotSupportedException;
import com.sun.xml.internal.ws.api.addressing.NonAnonymousResponseProcessor;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.EndpointAddress;
import java.net.URI;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.istack.internal.Nullable;
import javax.xml.soap.SOAPFault;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.message.FaultDetailHeader;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.message.Message;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.server.WSEndpoint;

public class WsaServerTube extends WsaTube
{
    private WSEndpoint endpoint;
    private WSEndpointReference replyTo;
    private WSEndpointReference faultTo;
    private boolean isAnonymousRequired;
    protected boolean isEarlyBackchannelCloseAllowed;
    private WSDLBoundOperation wbo;
    @Deprecated
    public static final String REQUEST_MESSAGE_ID = "com.sun.xml.internal.ws.addressing.request.messageID";
    private static final Logger LOGGER;
    
    public WsaServerTube(final WSEndpoint endpoint, @NotNull final WSDLPort wsdlPort, final WSBinding binding, final Tube next) {
        super(wsdlPort, binding, next);
        this.isAnonymousRequired = false;
        this.isEarlyBackchannelCloseAllowed = true;
        this.endpoint = endpoint;
    }
    
    public WsaServerTube(final WsaServerTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.isAnonymousRequired = false;
        this.isEarlyBackchannelCloseAllowed = true;
        this.endpoint = that.endpoint;
    }
    
    @Override
    public WsaServerTube copy(final TubeCloner cloner) {
        return new WsaServerTube(this, cloner);
    }
    
    @NotNull
    @Override
    public NextAction processRequest(final Packet request) {
        final Message msg = request.getMessage();
        if (msg == null) {
            return this.doInvoke(this.next, request);
        }
        request.addSatellite(new WsaPropertyBag(this.addressingVersion, this.soapVersion, request));
        final MessageHeaders hl = request.getMessage().getHeaders();
        String msgId;
        try {
            this.replyTo = AddressingUtils.getReplyTo(hl, this.addressingVersion, this.soapVersion);
            this.faultTo = AddressingUtils.getFaultTo(hl, this.addressingVersion, this.soapVersion);
            msgId = AddressingUtils.getMessageID(hl, this.addressingVersion, this.soapVersion);
        }
        catch (final InvalidAddressingHeaderException e) {
            WsaServerTube.LOGGER.log(Level.WARNING, this.addressingVersion.getInvalidMapText() + ", Problem header:" + e.getProblemHeader() + ", Reason: " + e.getSubsubcode(), e);
            hl.remove(e.getProblemHeader());
            final SOAPFault soapFault = this.helper.createInvalidAddressingHeaderFault(e, this.addressingVersion);
            if (this.wsdlPort != null && request.getMessage().isOneWay(this.wsdlPort)) {
                final Packet response = request.createServerResponse(null, this.wsdlPort, null, this.binding);
                return this.doReturnWith(response);
            }
            final Message m = Messages.create(soapFault);
            if (this.soapVersion == SOAPVersion.SOAP_11) {
                final FaultDetailHeader s11FaultDetailHeader = new FaultDetailHeader(this.addressingVersion, this.addressingVersion.problemHeaderQNameTag.getLocalPart(), e.getProblemHeader());
                m.getHeaders().add(s11FaultDetailHeader);
            }
            final Packet response2 = request.createServerResponse(m, this.wsdlPort, null, this.binding);
            return this.doReturnWith(response2);
        }
        if (this.replyTo == null) {
            this.replyTo = this.addressingVersion.anonymousEpr;
        }
        if (this.faultTo == null) {
            this.faultTo = this.replyTo;
        }
        request.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.ReplyToFromRequest", this.replyTo);
        request.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.FaultToFromRequest", this.faultTo);
        request.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.MessageIdFromRequest", msgId);
        this.wbo = this.getWSDLBoundOperation(request);
        this.isAnonymousRequired = this.isAnonymousRequired(this.wbo);
        final Packet p = this.validateInboundHeaders(request);
        if (p.getMessage() == null) {
            return this.doReturnWith(p);
        }
        if (p.getMessage().isFault()) {
            if (this.isEarlyBackchannelCloseAllowed && !this.isAnonymousRequired && !this.faultTo.isAnonymous() && request.transportBackChannel != null) {
                request.transportBackChannel.close();
            }
            return this.processResponse(p);
        }
        if (this.isEarlyBackchannelCloseAllowed && !this.isAnonymousRequired && !this.replyTo.isAnonymous() && !this.faultTo.isAnonymous() && request.transportBackChannel != null) {
            request.transportBackChannel.close();
        }
        return this.doInvoke(this.next, p);
    }
    
    protected boolean isAnonymousRequired(@Nullable final WSDLBoundOperation wbo) {
        return false;
    }
    
    protected void checkAnonymousSemantics(final WSDLBoundOperation wbo, final WSEndpointReference replyTo, final WSEndpointReference faultTo) {
    }
    
    @NotNull
    @Override
    public NextAction processException(final Throwable t) {
        final Packet response = Fiber.current().getPacket();
        ThrowableContainerPropertySet tc = response.getSatellite(ThrowableContainerPropertySet.class);
        if (tc == null) {
            tc = new ThrowableContainerPropertySet(t);
            response.addSatellite(tc);
        }
        else if (t != tc.getThrowable()) {
            tc.setThrowable(t);
        }
        return this.processResponse(response.endpoint.createServiceResponseForException(tc, response, this.soapVersion, this.wsdlPort, response.endpoint.getSEIModel(), this.binding));
    }
    
    @NotNull
    @Override
    public NextAction processResponse(final Packet response) {
        final Message msg = response.getMessage();
        if (msg == null) {
            return this.doReturnWith(response);
        }
        final String to = AddressingUtils.getTo(msg.getHeaders(), this.addressingVersion, this.soapVersion);
        if (to != null) {
            final WSEndpointReference wsEndpointReference = new WSEndpointReference(to, this.addressingVersion);
            this.faultTo = wsEndpointReference;
            this.replyTo = wsEndpointReference;
        }
        if (this.replyTo == null) {
            this.replyTo = (WSEndpointReference)response.get("com.sun.xml.internal.ws.addressing.WsaPropertyBag.ReplyToFromRequest");
        }
        if (this.faultTo == null) {
            this.faultTo = (WSEndpointReference)response.get("com.sun.xml.internal.ws.addressing.WsaPropertyBag.FaultToFromRequest");
        }
        WSEndpointReference target = msg.isFault() ? this.faultTo : this.replyTo;
        if (target == null && response.proxy instanceof Stub) {
            target = ((Stub)response.proxy).getWSEndpointReference();
        }
        if (target == null || target.isAnonymous() || this.isAnonymousRequired) {
            return this.doReturnWith(response);
        }
        if (target.isNone()) {
            response.setMessage(null);
            return this.doReturnWith(response);
        }
        if (this.wsdlPort != null && response.getMessage().isOneWay(this.wsdlPort)) {
            WsaServerTube.LOGGER.fine(AddressingMessages.NON_ANONYMOUS_RESPONSE_ONEWAY());
            return this.doReturnWith(response);
        }
        if (this.wbo != null || response.soapAction == null) {
            final String action = response.getMessage().isFault() ? this.helper.getFaultAction(this.wbo, response) : this.helper.getOutputAction(this.wbo);
            if (response.soapAction == null || (action != null && !action.equals("http://jax-ws.dev.java.net/addressing/output-action-not-set"))) {
                response.soapAction = action;
            }
        }
        response.expectReply = false;
        EndpointAddress adrs;
        try {
            adrs = new EndpointAddress(URI.create(target.getAddress()));
        }
        catch (final NullPointerException e) {
            throw new WebServiceException(e);
        }
        catch (final IllegalArgumentException e2) {
            throw new WebServiceException(e2);
        }
        response.endpointAddress = adrs;
        if (response.isAdapterDeliversNonAnonymousResponse) {
            return this.doReturnWith(response);
        }
        return this.doReturnWith(NonAnonymousResponseProcessor.getDefault().process(response));
    }
    
    @Override
    protected void validateAction(final Packet packet) {
        final WSDLBoundOperation wsdlBoundOperation = this.getWSDLBoundOperation(packet);
        if (wsdlBoundOperation == null) {
            return;
        }
        final String gotA = AddressingUtils.getAction(packet.getMessage().getHeaders(), this.addressingVersion, this.soapVersion);
        if (gotA == null) {
            throw new WebServiceException(AddressingMessages.VALIDATION_SERVER_NULL_ACTION());
        }
        String expected = this.helper.getInputAction(packet);
        final String soapAction = this.helper.getSOAPAction(packet);
        if (this.helper.isInputActionDefault(packet) && soapAction != null && !soapAction.equals("")) {
            expected = soapAction;
        }
        if (expected != null && !gotA.equals(expected)) {
            throw new ActionNotSupportedException(gotA);
        }
    }
    
    @Override
    protected void checkMessageAddressingProperties(final Packet packet) {
        super.checkMessageAddressingProperties(packet);
        final WSDLBoundOperation wsdlBoundOperation = this.getWSDLBoundOperation(packet);
        this.checkAnonymousSemantics(wsdlBoundOperation, this.replyTo, this.faultTo);
        this.checkNonAnonymousAddresses(this.replyTo, this.faultTo);
    }
    
    private void checkNonAnonymousAddresses(final WSEndpointReference replyTo, final WSEndpointReference faultTo) {
        if (!replyTo.isAnonymous()) {
            try {
                new EndpointAddress(URI.create(replyTo.getAddress()));
            }
            catch (final Exception e) {
                throw new InvalidAddressingHeaderException(this.addressingVersion.replyToTag, this.addressingVersion.invalidAddressTag);
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(WsaServerTube.class.getName());
    }
}
