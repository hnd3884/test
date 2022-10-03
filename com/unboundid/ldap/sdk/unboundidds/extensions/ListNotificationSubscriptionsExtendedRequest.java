package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.Iterator;
import com.unboundid.asn1.ASN1Set;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Collection;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.StaticUtils;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ListNotificationSubscriptionsExtendedRequest extends ExtendedRequest
{
    public static final String LIST_NOTIFICATION_SUBSCRIPTIONS_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.40";
    private static final long serialVersionUID = -8124073083247944273L;
    private final Set<String> destinationIDs;
    private final String managerID;
    
    public ListNotificationSubscriptionsExtendedRequest(final String managerID, final String... destinationIDs) {
        this(managerID, StaticUtils.toList(destinationIDs), new Control[0]);
    }
    
    public ListNotificationSubscriptionsExtendedRequest(final String managerID, final Collection<String> destinationIDs, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.40", encodeValue(managerID, destinationIDs), controls);
        this.managerID = managerID;
        if (destinationIDs == null) {
            this.destinationIDs = Collections.emptySet();
        }
        else {
            this.destinationIDs = Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(destinationIDs));
        }
    }
    
    public ListNotificationSubscriptionsExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_LIST_NOTIFICATION_SUBS_REQ_DECODE_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.managerID = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            if (elements.length > 1) {
                final ASN1Element[] destIDElements = ASN1Sequence.decodeAsSequence(elements[1]).elements();
                final LinkedHashSet<String> destIDs = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(destIDElements.length));
                for (final ASN1Element e : destIDElements) {
                    destIDs.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
                }
                this.destinationIDs = Collections.unmodifiableSet((Set<? extends String>)destIDs);
            }
            else {
                this.destinationIDs = Collections.emptySet();
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_LIST_NOTIFICATION_SUBS_REQ_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static ASN1OctetString encodeValue(final String managerID, final Collection<String> destinationIDs) {
        Validator.ensureNotNull(managerID);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(2);
        elements.add(new ASN1OctetString(managerID));
        if (destinationIDs != null && !destinationIDs.isEmpty()) {
            final LinkedHashSet<ASN1Element> destIDElements = new LinkedHashSet<ASN1Element>(StaticUtils.computeMapCapacity(destinationIDs.size()));
            for (final String destinationID : destinationIDs) {
                destIDElements.add(new ASN1OctetString(destinationID));
            }
            elements.add(new ASN1Set(destIDElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public ListNotificationSubscriptionsExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new ListNotificationSubscriptionsExtendedResult(extendedResponse);
    }
    
    public String getManagerID() {
        return this.managerID;
    }
    
    public Set<String> getDestinationIDs() {
        return this.destinationIDs;
    }
    
    @Override
    public ListNotificationSubscriptionsExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public ListNotificationSubscriptionsExtendedRequest duplicate(final Control[] controls) {
        final ListNotificationSubscriptionsExtendedRequest r = new ListNotificationSubscriptionsExtendedRequest(this.managerID, this.destinationIDs, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_LIST_NOTIFICATION_SUBS.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ListNotificationSubscriptionsExtendedRequest(managerID='");
        buffer.append(this.managerID);
        buffer.append('\'');
        if (!this.destinationIDs.isEmpty()) {
            buffer.append(", destinationIDs={");
            final Iterator<String> iterator = this.destinationIDs.iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append(iterator.next());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        final Control[] controls = this.getControls();
        if (controls.length > 0) {
            buffer.append(", controls={");
            for (int i = 0; i < controls.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[i]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
