package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import java.util.Collection;
import com.unboundid.asn1.ASN1OctetString;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Sequence;
import java.util.Collections;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ListNotificationSubscriptionsExtendedResult extends ExtendedResult
{
    public static final String LIST_NOTIFICATION_SUBSCRIPTIONS_RESULT_OID = "1.3.6.1.4.1.30221.2.6.41";
    private static final long serialVersionUID = 8876370324325619149L;
    private final List<NotificationDestinationDetails> destinations;
    
    public ListNotificationSubscriptionsExtendedResult(final ExtendedResult extendedResult) throws LDAPException {
        super(extendedResult);
        final ASN1OctetString value = extendedResult.getValue();
        if (value == null) {
            this.destinations = Collections.emptyList();
            return;
        }
        try {
            final ASN1Element[] destsElements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            final ArrayList<NotificationDestinationDetails> destList = new ArrayList<NotificationDestinationDetails>(destsElements.length);
            for (final ASN1Element destElement : destsElements) {
                final ASN1Element[] destElements = ASN1Sequence.decodeAsSequence(destElement).elements();
                final String destID = ASN1OctetString.decodeAsOctetString(destElements[0]).stringValue();
                final ASN1Element[] destDetailsElements = ASN1Sequence.decodeAsSequence(destElements[1]).elements();
                final ArrayList<ASN1OctetString> destDetailsList = new ArrayList<ASN1OctetString>(destDetailsElements.length);
                for (final ASN1Element e : destDetailsElements) {
                    destDetailsList.add(ASN1OctetString.decodeAsOctetString(e));
                }
                final ASN1Element[] subElements = ASN1Sequence.decodeAsSequence(destElements[2]).elements();
                final ArrayList<NotificationSubscriptionDetails> subscriptions = new ArrayList<NotificationSubscriptionDetails>(subElements.length);
                for (final ASN1Element e2 : subElements) {
                    final ASN1Element[] sElements = ASN1Sequence.decodeAsSequence(e2).elements();
                    final String subID = ASN1OctetString.decodeAsOctetString(sElements[0]).stringValue();
                    final ASN1Element[] subDetailsElements = ASN1Sequence.decodeAsSequence(sElements[1]).elements();
                    final ArrayList<ASN1OctetString> subDetails = new ArrayList<ASN1OctetString>(subDetailsElements.length);
                    for (final ASN1Element sde : subDetailsElements) {
                        subDetails.add(ASN1OctetString.decodeAsOctetString(sde));
                    }
                    subscriptions.add(new NotificationSubscriptionDetails(subID, subDetails));
                }
                destList.add(new NotificationDestinationDetails(destID, destDetailsList, subscriptions));
            }
            this.destinations = Collections.unmodifiableList((List<? extends NotificationDestinationDetails>)destList);
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_LIST_NOTIFICATION_SUBS_RESULT_CANNOT_DECODE_VALUE.get(StaticUtils.getExceptionMessage(e3)), e3);
        }
    }
    
    public ListNotificationSubscriptionsExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final Collection<NotificationDestinationDetails> destinations, final Control... controls) throws LDAPException {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, "1.3.6.1.4.1.30221.2.6.41", encodeValue(destinations), controls);
        if (destinations == null) {
            this.destinations = Collections.emptyList();
        }
        else {
            this.destinations = Collections.unmodifiableList((List<? extends NotificationDestinationDetails>)new ArrayList<NotificationDestinationDetails>(destinations));
        }
    }
    
    private static ASN1OctetString encodeValue(final Collection<NotificationDestinationDetails> destinations) {
        if (destinations == null || destinations.isEmpty()) {
            return null;
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(destinations.size());
        for (final NotificationDestinationDetails destDetails : destinations) {
            final ArrayList<ASN1Element> destElements = new ArrayList<ASN1Element>(3);
            destElements.add(new ASN1OctetString(destDetails.getID()));
            destElements.add(new ASN1Sequence(destDetails.getDetails()));
            final ArrayList<ASN1Element> subElements = new ArrayList<ASN1Element>(destDetails.getSubscriptions().size());
            for (final NotificationSubscriptionDetails subDetails : destDetails.getSubscriptions()) {
                subElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(subDetails.getID()), new ASN1Sequence(subDetails.getDetails()) }));
            }
            destElements.add(new ASN1Sequence(subElements));
            elements.add(new ASN1Sequence(destElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public List<NotificationDestinationDetails> getDestinations() {
        return this.destinations;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_LIST_NOTIFICATION_SUBS.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ListNotificationSubscriptionsExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        buffer.append(", notificationDestinations={");
        final Iterator<NotificationDestinationDetails> destIterator = this.destinations.iterator();
        while (destIterator.hasNext()) {
            destIterator.next().toString(buffer);
            if (destIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append('}');
        final String diagnosticMessage = this.getDiagnosticMessage();
        if (diagnosticMessage != null) {
            buffer.append(", diagnosticMessage='");
            buffer.append(diagnosticMessage);
            buffer.append('\'');
        }
        final String matchedDN = this.getMatchedDN();
        if (matchedDN != null) {
            buffer.append(", matchedDN='");
            buffer.append(matchedDN);
            buffer.append('\'');
        }
        final String[] referralURLs = this.getReferralURLs();
        if (referralURLs.length > 0) {
            buffer.append(", referralURLs={");
            for (int i = 0; i < referralURLs.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(referralURLs[i]);
                buffer.append('\'');
            }
            buffer.append('}');
        }
        final Control[] responseControls = this.getResponseControls();
        if (responseControls.length > 0) {
            buffer.append(", responseControls={");
            for (int j = 0; j < responseControls.length; ++j) {
                if (j > 0) {
                    buffer.append(", ");
                }
                buffer.append(responseControls[j]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
