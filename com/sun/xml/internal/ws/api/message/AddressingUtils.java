package com.sun.xml.internal.ws.api.message;

import com.sun.xml.internal.ws.message.RelatesToHeader;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.api.addressing.AddressingPropertySet;
import com.sun.xml.internal.ws.api.addressing.OneWayFeature;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.api.model.SEIModel;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.message.StringHeader;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;

public class AddressingUtils
{
    public static void fillRequestAddressingHeaders(final MessageHeaders headers, final Packet packet, final AddressingVersion av, final SOAPVersion sv, final boolean oneway, final String action) {
        fillRequestAddressingHeaders(headers, packet, av, sv, oneway, action, false);
    }
    
    public static void fillRequestAddressingHeaders(final MessageHeaders headers, final Packet packet, final AddressingVersion av, final SOAPVersion sv, final boolean oneway, final String action, final boolean mustUnderstand) {
        fillCommonAddressingHeaders(headers, packet, av, sv, action, mustUnderstand);
        if (!oneway) {
            final WSEndpointReference epr = av.anonymousEpr;
            if (headers.get(av.replyToTag, false) == null) {
                headers.add(epr.createHeader(av.replyToTag));
            }
            if (headers.get(av.faultToTag, false) == null) {
                headers.add(epr.createHeader(av.faultToTag));
            }
            if (packet.getMessage().getHeaders().get(av.messageIDTag, false) == null && headers.get(av.messageIDTag, false) == null) {
                final Header h = new StringHeader(av.messageIDTag, Message.generateMessageID());
                headers.add(h);
            }
        }
    }
    
    public static void fillRequestAddressingHeaders(final MessageHeaders headers, final WSDLPort wsdlPort, final WSBinding binding, final Packet packet) {
        if (binding == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_BINDING());
        }
        if (binding.isFeatureEnabled(SuppressAutomaticWSARequestHeadersFeature.class)) {
            return;
        }
        final MessageHeaders hl = packet.getMessage().getHeaders();
        final String action = getAction(hl, binding.getAddressingVersion(), binding.getSOAPVersion());
        if (action != null) {
            return;
        }
        final AddressingVersion addressingVersion = binding.getAddressingVersion();
        final WsaTubeHelper wsaHelper = addressingVersion.getWsaHelper(wsdlPort, null, binding);
        final String effectiveInputAction = wsaHelper.getEffectiveInputAction(packet);
        if (effectiveInputAction == null || (effectiveInputAction.equals("") && binding.getSOAPVersion() == SOAPVersion.SOAP_11)) {
            throw new WebServiceException(ClientMessages.INVALID_SOAP_ACTION());
        }
        final boolean oneway = !packet.expectReply;
        if (wsdlPort != null && !oneway && packet.getMessage() != null && packet.getWSDLOperation() != null) {
            final WSDLBoundOperation wbo = wsdlPort.getBinding().get(packet.getWSDLOperation());
            if (wbo != null && wbo.getAnonymous() == WSDLBoundOperation.ANONYMOUS.prohibited) {
                throw new WebServiceException(AddressingMessages.WSAW_ANONYMOUS_PROHIBITED());
            }
        }
        OneWayFeature oneWayFeature = binding.getFeature(OneWayFeature.class);
        final AddressingPropertySet addressingPropertySet = packet.getSatellite(AddressingPropertySet.class);
        oneWayFeature = ((addressingPropertySet == null) ? oneWayFeature : new OneWayFeature(addressingPropertySet, addressingVersion));
        if (oneWayFeature == null || !oneWayFeature.isEnabled()) {
            fillRequestAddressingHeaders(headers, packet, addressingVersion, binding.getSOAPVersion(), oneway, effectiveInputAction, AddressingVersion.isRequired(binding));
        }
        else {
            fillRequestAddressingHeaders(headers, packet, addressingVersion, binding.getSOAPVersion(), oneWayFeature, oneway, effectiveInputAction);
        }
    }
    
    public static String getAction(@NotNull final MessageHeaders headers, @NotNull final AddressingVersion av, @NotNull final SOAPVersion sv) {
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        String action = null;
        final Header h = getFirstHeader(headers, av.actionTag, true, sv);
        if (h != null) {
            action = h.getStringContent();
        }
        return action;
    }
    
    public static WSEndpointReference getFaultTo(@NotNull final MessageHeaders headers, @NotNull final AddressingVersion av, @NotNull final SOAPVersion sv) {
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        final Header h = getFirstHeader(headers, av.faultToTag, true, sv);
        WSEndpointReference faultTo = null;
        if (h != null) {
            try {
                faultTo = h.readAsEPR(av);
            }
            catch (final XMLStreamException e) {
                throw new WebServiceException(AddressingMessages.FAULT_TO_CANNOT_PARSE(), e);
            }
        }
        return faultTo;
    }
    
    public static String getMessageID(@NotNull final MessageHeaders headers, @NotNull final AddressingVersion av, @NotNull final SOAPVersion sv) {
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        final Header h = getFirstHeader(headers, av.messageIDTag, true, sv);
        String messageId = null;
        if (h != null) {
            messageId = h.getStringContent();
        }
        return messageId;
    }
    
    public static String getRelatesTo(@NotNull final MessageHeaders headers, @NotNull final AddressingVersion av, @NotNull final SOAPVersion sv) {
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        final Header h = getFirstHeader(headers, av.relatesToTag, true, sv);
        String relatesTo = null;
        if (h != null) {
            relatesTo = h.getStringContent();
        }
        return relatesTo;
    }
    
    public static WSEndpointReference getReplyTo(@NotNull final MessageHeaders headers, @NotNull final AddressingVersion av, @NotNull final SOAPVersion sv) {
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        final Header h = getFirstHeader(headers, av.replyToTag, true, sv);
        if (h != null) {
            try {
                final WSEndpointReference replyTo = h.readAsEPR(av);
                return replyTo;
            }
            catch (final XMLStreamException e) {
                throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), e);
            }
        }
        final WSEndpointReference replyTo = av.anonymousEpr;
        return replyTo;
    }
    
    public static String getTo(final MessageHeaders headers, final AddressingVersion av, final SOAPVersion sv) {
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        final Header h = getFirstHeader(headers, av.toTag, true, sv);
        String to;
        if (h != null) {
            to = h.getStringContent();
        }
        else {
            to = av.anonymousUri;
        }
        return to;
    }
    
    public static Header getFirstHeader(final MessageHeaders headers, final QName name, final boolean markUnderstood, final SOAPVersion sv) {
        if (sv == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_SOAP_VERSION());
        }
        final Iterator<Header> iter = headers.getHeaders(name.getNamespaceURI(), name.getLocalPart(), markUnderstood);
        while (iter.hasNext()) {
            final Header h = iter.next();
            if (h.getRole(sv).equals(sv.implicitRole)) {
                return h;
            }
        }
        return null;
    }
    
    private static void fillRequestAddressingHeaders(@NotNull final MessageHeaders headers, @NotNull final Packet packet, @NotNull final AddressingVersion av, @NotNull final SOAPVersion sv, @NotNull final OneWayFeature oneWayFeature, final boolean oneway, @NotNull final String action) {
        if (!oneway && !oneWayFeature.isUseAsyncWithSyncInvoke() && Boolean.TRUE.equals(packet.isSynchronousMEP)) {
            fillRequestAddressingHeaders(headers, packet, av, sv, oneway, action);
        }
        else {
            fillCommonAddressingHeaders(headers, packet, av, sv, action, false);
            boolean isMessageIdAdded = false;
            if (headers.get(av.replyToTag, false) == null) {
                final WSEndpointReference replyToEpr = oneWayFeature.getReplyTo();
                if (replyToEpr != null) {
                    headers.add(replyToEpr.createHeader(av.replyToTag));
                    if (packet.getMessage().getHeaders().get(av.messageIDTag, false) == null) {
                        final String newID = (oneWayFeature.getMessageId() == null) ? Message.generateMessageID() : oneWayFeature.getMessageId();
                        headers.add(new StringHeader(av.messageIDTag, newID));
                        isMessageIdAdded = true;
                    }
                }
            }
            final String messageId = oneWayFeature.getMessageId();
            if (!isMessageIdAdded && messageId != null) {
                headers.add(new StringHeader(av.messageIDTag, messageId));
            }
            if (headers.get(av.faultToTag, false) == null) {
                final WSEndpointReference faultToEpr = oneWayFeature.getFaultTo();
                if (faultToEpr != null) {
                    headers.add(faultToEpr.createHeader(av.faultToTag));
                    if (headers.get(av.messageIDTag, false) == null) {
                        headers.add(new StringHeader(av.messageIDTag, Message.generateMessageID()));
                    }
                }
            }
            if (oneWayFeature.getFrom() != null) {
                headers.addOrReplace(oneWayFeature.getFrom().createHeader(av.fromTag));
            }
            if (oneWayFeature.getRelatesToID() != null) {
                headers.addOrReplace(new RelatesToHeader(av.relatesToTag, oneWayFeature.getRelatesToID()));
            }
        }
    }
    
    private static void fillCommonAddressingHeaders(final MessageHeaders headers, final Packet packet, @NotNull final AddressingVersion av, @NotNull final SOAPVersion sv, @NotNull final String action, final boolean mustUnderstand) {
        if (packet == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_PACKET());
        }
        if (av == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
        }
        if (sv == null) {
            throw new IllegalArgumentException(AddressingMessages.NULL_SOAP_VERSION());
        }
        if (action == null && !sv.httpBindingId.equals("http://www.w3.org/2003/05/soap/bindings/HTTP/")) {
            throw new IllegalArgumentException(AddressingMessages.NULL_ACTION());
        }
        if (headers.get(av.toTag, false) == null) {
            final StringHeader h = new StringHeader(av.toTag, packet.endpointAddress.toString());
            headers.add(h);
        }
        if (action != null) {
            packet.soapAction = action;
            if (headers.get(av.actionTag, false) == null) {
                final StringHeader h = new StringHeader(av.actionTag, action, sv, mustUnderstand);
                headers.add(h);
            }
        }
    }
}
