package com.unboundid.ldap.sdk.unboundidds.controls;

import java.util.Iterator;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Collection;
import com.unboundid.asn1.ASN1OctetString;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RouteToBackendSetRequestControl extends Control
{
    public static final String ROUTE_TO_BACKEND_SET_REQUEST_OID = "1.3.6.1.4.1.30221.2.5.35";
    private static final long serialVersionUID = -2486448910813783450L;
    private final RouteToBackendSetRoutingType routingType;
    private final Set<String> absoluteBackendSetIDs;
    private final Set<String> routingHintFallbackSetIDs;
    private final Set<String> routingHintFirstGuessSetIDs;
    private final String entryBalancingRequestProcessorID;
    
    private RouteToBackendSetRequestControl(final boolean isCritical, final ASN1OctetString encodedValue, final String entryBalancingRequestProcessorID, final RouteToBackendSetRoutingType routingType, final Collection<String> absoluteBackendSetIDs, final Collection<String> routingHintFirstGuessSetIDs, final Collection<String> routingHintFallbackSetIDs) {
        super("1.3.6.1.4.1.30221.2.5.35", isCritical, encodedValue);
        this.entryBalancingRequestProcessorID = entryBalancingRequestProcessorID;
        this.routingType = routingType;
        if (absoluteBackendSetIDs == null) {
            this.absoluteBackendSetIDs = null;
        }
        else {
            this.absoluteBackendSetIDs = Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(absoluteBackendSetIDs));
        }
        if (routingHintFirstGuessSetIDs == null) {
            this.routingHintFirstGuessSetIDs = null;
        }
        else {
            this.routingHintFirstGuessSetIDs = Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(routingHintFirstGuessSetIDs));
        }
        if (routingHintFallbackSetIDs == null) {
            this.routingHintFallbackSetIDs = null;
        }
        else {
            this.routingHintFallbackSetIDs = Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(routingHintFallbackSetIDs));
        }
    }
    
    public RouteToBackendSetRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ROUTE_TO_BACKEND_SET_REQUEST_MISSING_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.entryBalancingRequestProcessorID = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            this.routingType = RouteToBackendSetRoutingType.valueOf(elements[1].getType());
            if (this.routingType == null) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ROUTE_TO_BACKEND_SET_REQUEST_UNKNOWN_ROUTING_TYPE.get(StaticUtils.toHex(elements[1].getType())));
            }
            if (this.routingType == RouteToBackendSetRoutingType.ABSOLUTE_ROUTING) {
                final ASN1Element[] arElements = ASN1Set.decodeAsSet(elements[1]).elements();
                final LinkedHashSet<String> arSet = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(arElements.length));
                for (final ASN1Element e : arElements) {
                    arSet.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
                }
                this.absoluteBackendSetIDs = Collections.unmodifiableSet((Set<? extends String>)arSet);
                if (this.absoluteBackendSetIDs.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ROUTE_TO_BACKEND_SET_REQUEST_ABSOLUTE_SET_EMPTY.get());
                }
                this.routingHintFirstGuessSetIDs = null;
                this.routingHintFallbackSetIDs = null;
            }
            else {
                final ASN1Element[] hintElements = ASN1Sequence.decodeAsSequence(elements[1]).elements();
                final ASN1Element[] firstGuessElements = ASN1Set.decodeAsSet(hintElements[0]).elements();
                final LinkedHashSet<String> firstGuessSet = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(firstGuessElements.length));
                for (final ASN1Element e2 : firstGuessElements) {
                    firstGuessSet.add(ASN1OctetString.decodeAsOctetString(e2).stringValue());
                }
                this.routingHintFirstGuessSetIDs = Collections.unmodifiableSet((Set<? extends String>)firstGuessSet);
                if (this.routingHintFirstGuessSetIDs.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ROUTE_TO_BACKEND_SET_REQUEST_HINT_FIRST_SET_EMPTY.get());
                }
                if (hintElements.length == 1) {
                    this.routingHintFallbackSetIDs = null;
                }
                else {
                    final ASN1Element[] fallbackElements = ASN1Set.decodeAsSet(hintElements[1]).elements();
                    final LinkedHashSet<String> fallbackSet = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(fallbackElements.length));
                    for (final ASN1Element e3 : fallbackElements) {
                        fallbackSet.add(ASN1OctetString.decodeAsOctetString(e3).stringValue());
                    }
                    this.routingHintFallbackSetIDs = Collections.unmodifiableSet((Set<? extends String>)fallbackSet);
                    if (this.routingHintFallbackSetIDs.isEmpty()) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ROUTE_TO_BACKEND_SET_REQUEST_HINT_FALLBACK_SET_EMPTY.get());
                    }
                }
                this.absoluteBackendSetIDs = null;
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e4) {
            Debug.debugException(e4);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ROUTE_TO_BACKEND_SET_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e4)), e4);
        }
    }
    
    public static RouteToBackendSetRequestControl createAbsoluteRoutingRequest(final boolean isCritical, final String entryBalancingRequestProcessorID, final String backendSetID) {
        return createAbsoluteRoutingRequest(isCritical, entryBalancingRequestProcessorID, Collections.singletonList(backendSetID));
    }
    
    public static RouteToBackendSetRequestControl createAbsoluteRoutingRequest(final boolean isCritical, final String entryBalancingRequestProcessorID, final Collection<String> backendSetIDs) {
        Validator.ensureNotNull(backendSetIDs);
        Validator.ensureFalse(backendSetIDs.isEmpty());
        final ArrayList<ASN1Element> backendSetIDElements = new ArrayList<ASN1Element>(backendSetIDs.size());
        for (final String s : backendSetIDs) {
            backendSetIDElements.add(new ASN1OctetString(s));
        }
        final RouteToBackendSetRoutingType routingType = RouteToBackendSetRoutingType.ABSOLUTE_ROUTING;
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(entryBalancingRequestProcessorID), new ASN1Set(routingType.getBERType(), backendSetIDElements) });
        return new RouteToBackendSetRequestControl(isCritical, new ASN1OctetString(valueSequence.encode()), entryBalancingRequestProcessorID, routingType, backendSetIDs, null, null);
    }
    
    public static RouteToBackendSetRequestControl createRoutingHintRequest(final boolean isCritical, final String entryBalancingRequestProcessorID, final String firstGuessSetID, final Collection<String> fallbackSetIDs) {
        return createRoutingHintRequest(isCritical, entryBalancingRequestProcessorID, Collections.singletonList(firstGuessSetID), fallbackSetIDs);
    }
    
    public static RouteToBackendSetRequestControl createRoutingHintRequest(final boolean isCritical, final String entryBalancingRequestProcessorID, final Collection<String> firstGuessSetIDs, final Collection<String> fallbackSetIDs) {
        Validator.ensureNotNull(firstGuessSetIDs);
        Validator.ensureFalse(firstGuessSetIDs.isEmpty());
        if (fallbackSetIDs != null) {
            Validator.ensureFalse(fallbackSetIDs.isEmpty());
        }
        final ArrayList<ASN1Element> backendSetsElements = new ArrayList<ASN1Element>(2);
        final ArrayList<ASN1Element> firstGuessElements = new ArrayList<ASN1Element>(firstGuessSetIDs.size());
        for (final String s : firstGuessSetIDs) {
            firstGuessElements.add(new ASN1OctetString(s));
        }
        backendSetsElements.add(new ASN1Set(firstGuessElements));
        if (fallbackSetIDs != null) {
            final ArrayList<ASN1Element> fallbackElements = new ArrayList<ASN1Element>(fallbackSetIDs.size());
            for (final String s2 : fallbackSetIDs) {
                fallbackElements.add(new ASN1OctetString(s2));
            }
            backendSetsElements.add(new ASN1Set(fallbackElements));
        }
        final RouteToBackendSetRoutingType routingType = RouteToBackendSetRoutingType.ROUTING_HINT;
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(entryBalancingRequestProcessorID), new ASN1Sequence(routingType.getBERType(), backendSetsElements) });
        return new RouteToBackendSetRequestControl(isCritical, new ASN1OctetString(valueSequence.encode()), entryBalancingRequestProcessorID, routingType, null, firstGuessSetIDs, fallbackSetIDs);
    }
    
    public String getEntryBalancingRequestProcessorID() {
        return this.entryBalancingRequestProcessorID;
    }
    
    public RouteToBackendSetRoutingType getRoutingType() {
        return this.routingType;
    }
    
    public Set<String> getAbsoluteBackendSetIDs() {
        return this.absoluteBackendSetIDs;
    }
    
    public Set<String> getRoutingHintFirstGuessSetIDs() {
        return this.routingHintFirstGuessSetIDs;
    }
    
    public Set<String> getRoutingHintFallbackSetIDs() {
        return this.routingHintFallbackSetIDs;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_ROUTE_TO_BACKEND_SET_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("RouteToBackendSetRequestControl(isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", entryBalancingRequestProcessorID='");
        buffer.append(this.entryBalancingRequestProcessorID);
        buffer.append("', routingType='");
        Iterator<String> iterator = null;
        switch (this.routingType) {
            case ABSOLUTE_ROUTING: {
                buffer.append("absolute', backendSetIDs={");
                iterator = this.absoluteBackendSetIDs.iterator();
                while (iterator.hasNext()) {
                    buffer.append('\'');
                    buffer.append(iterator.next());
                    buffer.append('\'');
                    if (iterator.hasNext()) {
                        buffer.append(", ");
                    }
                }
                buffer.append('}');
                break;
            }
            case ROUTING_HINT: {
                buffer.append("hint', firstGuessSetIDs={");
                iterator = this.routingHintFirstGuessSetIDs.iterator();
                while (iterator.hasNext()) {
                    buffer.append('\'');
                    buffer.append(iterator.next());
                    buffer.append('\'');
                    if (iterator.hasNext()) {
                        buffer.append(", ");
                    }
                }
                buffer.append('}');
                if (this.routingHintFallbackSetIDs != null) {
                    buffer.append(", fallbackSetIDs={");
                    iterator = this.routingHintFallbackSetIDs.iterator();
                    while (iterator.hasNext()) {
                        buffer.append('\'');
                        buffer.append(iterator.next());
                        buffer.append('\'');
                        if (iterator.hasNext()) {
                            buffer.append(", ");
                        }
                    }
                    buffer.append('}');
                    break;
                }
                break;
            }
        }
        buffer.append(')');
    }
}
