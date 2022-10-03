package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SetNotificationSubscriptionExtendedRequest extends ExtendedRequest
{
    public static final String SET_NOTIFICATION_SUBSCRIPTION_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.38";
    private static final long serialVersionUID = -5822283773149091097L;
    private final List<ASN1OctetString> subscriptionDetails;
    private final String destinationID;
    private final String managerID;
    private final String subscriptionID;
    
    public SetNotificationSubscriptionExtendedRequest(final String managerID, final String destinationID, final String subscriptionID, final ASN1OctetString... subscriptionDetails) {
        this(managerID, destinationID, subscriptionID, StaticUtils.toList(subscriptionDetails), new Control[0]);
    }
    
    public SetNotificationSubscriptionExtendedRequest(final String managerID, final String destinationID, final String subscriptionID, final Collection<ASN1OctetString> subscriptionDetails, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.38", encodeValue(managerID, destinationID, subscriptionID, subscriptionDetails), controls);
        this.managerID = managerID;
        this.destinationID = destinationID;
        this.subscriptionID = subscriptionID;
        this.subscriptionDetails = Collections.unmodifiableList((List<? extends ASN1OctetString>)new ArrayList<ASN1OctetString>(subscriptionDetails));
    }
    
    public SetNotificationSubscriptionExtendedRequest(final ExtendedRequest extendedRequest) throws LDAPException {
        super(extendedRequest);
        final ASN1OctetString value = extendedRequest.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_SET_NOTIFICATION_SUB_REQ_DECODE_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.managerID = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            this.destinationID = ASN1OctetString.decodeAsOctetString(elements[1]).stringValue();
            this.subscriptionID = ASN1OctetString.decodeAsOctetString(elements[2]).stringValue();
            final ASN1Element[] detailElements = ASN1Sequence.decodeAsSequence(elements[3]).elements();
            final ArrayList<ASN1OctetString> detailList = new ArrayList<ASN1OctetString>(detailElements.length);
            for (final ASN1Element e : detailElements) {
                detailList.add(ASN1OctetString.decodeAsOctetString(e));
            }
            this.subscriptionDetails = Collections.unmodifiableList((List<? extends ASN1OctetString>)detailList);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_SET_NOTIFICATION_SUB_REQ_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static ASN1OctetString encodeValue(final String managerID, final String destinationID, final String subscriptionID, final Collection<ASN1OctetString> subscriptionDetails) {
        Validator.ensureNotNull(managerID);
        Validator.ensureNotNull(destinationID);
        Validator.ensureNotNull(subscriptionID);
        Validator.ensureNotNull(subscriptionDetails);
        Validator.ensureFalse(subscriptionDetails.isEmpty());
        final ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(managerID), new ASN1OctetString(destinationID), new ASN1OctetString(subscriptionID), new ASN1Sequence(new ArrayList<ASN1Element>(subscriptionDetails)) });
        return new ASN1OctetString(valueSequence.encode());
    }
    
    public String getManagerID() {
        return this.managerID;
    }
    
    public String getDestinationID() {
        return this.destinationID;
    }
    
    public String getSubscriptionID() {
        return this.subscriptionID;
    }
    
    public List<ASN1OctetString> getSubscriptionDetails() {
        return this.subscriptionDetails;
    }
    
    @Override
    public SetNotificationSubscriptionExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public SetNotificationSubscriptionExtendedRequest duplicate(final Control[] controls) {
        final SetNotificationSubscriptionExtendedRequest r = new SetNotificationSubscriptionExtendedRequest(this.managerID, this.destinationID, this.subscriptionID, this.subscriptionDetails, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_SET_NOTIFICATION_SUB.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SetNotificationSubscriptionExtendedRequest(managerID='");
        buffer.append(this.managerID);
        buffer.append("', destinationID='");
        buffer.append(this.destinationID);
        buffer.append("', subscriptionID='");
        buffer.append(this.subscriptionID);
        buffer.append("', subscriptionDetails=ASN1OctetString[");
        buffer.append(this.subscriptionDetails.size());
        buffer.append(']');
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
