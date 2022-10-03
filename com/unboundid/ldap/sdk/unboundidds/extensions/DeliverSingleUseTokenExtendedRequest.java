package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.Iterator;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Long;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.ObjectPair;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DeliverSingleUseTokenExtendedRequest extends ExtendedRequest
{
    public static final String DELIVER_SINGLE_USE_TOKEN_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.49";
    private static final byte VALIDITY_DURATION_MILLIS_BER_TYPE = Byte.MIN_VALUE;
    private static final byte MESSAGE_SUBJECT_BER_TYPE = -127;
    private static final byte FULL_TEXT_BEFORE_TOKEN_BER_TYPE = -126;
    private static final byte FULL_TEXT_AFTER_TOKEN_BER_TYPE = -125;
    private static final byte COMPACT_TEXT_BEFORE_TOKEN_BER_TYPE = -124;
    private static final byte COMPACT_TEXT_AFTER_TOKEN_BER_TYPE = -123;
    private static final byte PREFERRED_DELIVERY_MECHANISM_BER_TYPE = -90;
    private static final byte DELIVER_IF_PASSWORD_EXPIRED_TYPE = -121;
    private static final byte DELIVER_IF_ACCOUNT_LOCKED_TYPE = -120;
    private static final byte DELIVER_IF_ACCOUNT_DISABLED_TYPE = -119;
    private static final byte DELIVER_IF_ACCOUNT_EXPIRED_TYPE = -118;
    private static final long serialVersionUID = -4158226639899928825L;
    private final boolean deliverIfAccountDisabled;
    private final boolean deliverIfAccountExpired;
    private final boolean deliverIfAccountLocked;
    private final boolean deliverIfPasswordExpired;
    private final List<ObjectPair<String, String>> preferredDeliveryMechanisms;
    private final Long validityDurationMillis;
    private final String compactTextAfterToken;
    private final String compactTextBeforeToken;
    private final String fullTextAfterToken;
    private final String fullTextBeforeToken;
    private final String messageSubject;
    private final String tokenID;
    private final String userDN;
    
    public DeliverSingleUseTokenExtendedRequest(final String userDN, final String tokenID, final Long validityDurationMillis, final String messageSubject, final String fullTextBeforeToken, final String fullTextAfterToken, final String compactTextBeforeToken, final String compactTextAfterToken, final List<ObjectPair<String, String>> preferredDeliveryMechanisms, final boolean deliverIfPasswordExpired, final boolean deliverIfAccountLocked, final boolean deliverIfAccountDisabled, final boolean deliverIfAccountExpired, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.49", encodeValue(userDN, tokenID, validityDurationMillis, messageSubject, fullTextBeforeToken, fullTextAfterToken, compactTextBeforeToken, compactTextAfterToken, preferredDeliveryMechanisms, deliverIfPasswordExpired, deliverIfAccountLocked, deliverIfAccountDisabled, deliverIfAccountExpired), controls);
        this.userDN = userDN;
        this.tokenID = tokenID;
        this.validityDurationMillis = validityDurationMillis;
        this.messageSubject = messageSubject;
        this.fullTextBeforeToken = fullTextBeforeToken;
        this.fullTextAfterToken = fullTextAfterToken;
        this.compactTextBeforeToken = compactTextBeforeToken;
        this.compactTextAfterToken = compactTextAfterToken;
        this.deliverIfPasswordExpired = deliverIfPasswordExpired;
        this.deliverIfAccountLocked = deliverIfAccountLocked;
        this.deliverIfAccountDisabled = deliverIfAccountDisabled;
        this.deliverIfAccountExpired = deliverIfAccountExpired;
        if (preferredDeliveryMechanisms == null) {
            this.preferredDeliveryMechanisms = Collections.emptyList();
        }
        else {
            this.preferredDeliveryMechanisms = Collections.unmodifiableList((List<? extends ObjectPair<String, String>>)new ArrayList<ObjectPair<String, String>>(preferredDeliveryMechanisms));
        }
    }
    
    public DeliverSingleUseTokenExtendedRequest(final ExtendedRequest request) throws LDAPException {
        super(request);
        final ASN1OctetString value = request.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_SINGLE_USE_TOKEN_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.userDN = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            this.tokenID = ASN1OctetString.decodeAsOctetString(elements[1]).stringValue();
            Long validityDuration = null;
            String subject = null;
            String fullBefore = null;
            String fullAfter = null;
            String compactBefore = null;
            String compactAfter = null;
            final ArrayList<ObjectPair<String, String>> pdmList = new ArrayList<ObjectPair<String, String>>(10);
            boolean ifPasswordExpired = false;
            boolean ifAccountLocked = false;
            boolean ifAccountDisabled = false;
            boolean ifAccountExpired = false;
            for (int i = 2; i < elements.length; ++i) {
                switch (elements[i].getType()) {
                    case Byte.MIN_VALUE: {
                        validityDuration = ASN1Long.decodeAsLong(elements[i]).longValue();
                        break;
                    }
                    case -127: {
                        subject = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    case -126: {
                        fullBefore = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    case -125: {
                        fullAfter = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    case -124: {
                        compactBefore = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    case -123: {
                        compactAfter = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    case -90: {
                        for (final ASN1Element pdmElement : ASN1Sequence.decodeAsSequence(elements[i]).elements()) {
                            final ASN1Element[] dmElements = ASN1Sequence.decodeAsSequence(pdmElement).elements();
                            final String name = ASN1OctetString.decodeAsOctetString(dmElements[0]).stringValue();
                            String recipientID;
                            if (dmElements.length > 1) {
                                recipientID = ASN1OctetString.decodeAsOctetString(dmElements[1]).stringValue();
                            }
                            else {
                                recipientID = null;
                            }
                            pdmList.add(new ObjectPair<String, String>(name, recipientID));
                        }
                        break;
                    }
                    case -121: {
                        ifPasswordExpired = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    case -120: {
                        ifAccountLocked = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    case -119: {
                        ifAccountDisabled = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    case -118: {
                        ifAccountExpired = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_SINGLE_USE_TOKEN_REQUEST_UNKNOWN_ELEMENT.get(StaticUtils.toHex(elements[i].getType())));
                    }
                }
            }
            this.validityDurationMillis = validityDuration;
            this.messageSubject = subject;
            this.fullTextBeforeToken = fullBefore;
            this.fullTextAfterToken = fullAfter;
            this.compactTextBeforeToken = compactBefore;
            this.compactTextAfterToken = compactAfter;
            this.preferredDeliveryMechanisms = Collections.unmodifiableList((List<? extends ObjectPair<String, String>>)pdmList);
            this.deliverIfPasswordExpired = ifPasswordExpired;
            this.deliverIfAccountLocked = ifAccountLocked;
            this.deliverIfAccountDisabled = ifAccountDisabled;
            this.deliverIfAccountExpired = ifAccountExpired;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_SINGLE_USE_TOKEN_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final String userDN, final String tokenID, final Long validityDurationMillis, final String messageSubject, final String fullTextBeforeToken, final String fullTextAfterToken, final String compactTextBeforeToken, final String compactTextAfterToken, final List<ObjectPair<String, String>> preferredDeliveryMechanisms, final boolean deliverIfPasswordExpired, final boolean deliverIfAccountLocked, final boolean deliverIfAccountDisabled, final boolean deliverIfAccountExpired) {
        Validator.ensureNotNull(userDN);
        Validator.ensureNotNull(tokenID);
        if (validityDurationMillis != null) {
            Validator.ensureTrue(validityDurationMillis > 0L);
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(13);
        elements.add(new ASN1OctetString(userDN));
        elements.add(new ASN1OctetString(tokenID));
        if (validityDurationMillis != null) {
            elements.add(new ASN1Long((byte)(-128), validityDurationMillis));
        }
        if (messageSubject != null) {
            elements.add(new ASN1OctetString((byte)(-127), messageSubject));
        }
        if (fullTextBeforeToken != null) {
            elements.add(new ASN1OctetString((byte)(-126), fullTextBeforeToken));
        }
        if (fullTextAfterToken != null) {
            elements.add(new ASN1OctetString((byte)(-125), fullTextAfterToken));
        }
        if (compactTextBeforeToken != null) {
            elements.add(new ASN1OctetString((byte)(-124), compactTextBeforeToken));
        }
        if (compactTextAfterToken != null) {
            elements.add(new ASN1OctetString((byte)(-123), compactTextAfterToken));
        }
        if (preferredDeliveryMechanisms != null && !preferredDeliveryMechanisms.isEmpty()) {
            final ArrayList<ASN1Element> pdmElements = new ArrayList<ASN1Element>(preferredDeliveryMechanisms.size());
            for (final ObjectPair<String, String> p : preferredDeliveryMechanisms) {
                final ArrayList<ASN1Element> l = new ArrayList<ASN1Element>(2);
                l.add(new ASN1OctetString(p.getFirst()));
                if (p.getSecond() != null) {
                    l.add(new ASN1OctetString(p.getSecond()));
                }
                pdmElements.add(new ASN1Sequence(l));
            }
            elements.add(new ASN1Sequence((byte)(-90), pdmElements));
        }
        if (deliverIfPasswordExpired) {
            elements.add(new ASN1Boolean((byte)(-121), true));
        }
        if (deliverIfAccountLocked) {
            elements.add(new ASN1Boolean((byte)(-120), true));
        }
        if (deliverIfAccountDisabled) {
            elements.add(new ASN1Boolean((byte)(-119), true));
        }
        if (deliverIfAccountExpired) {
            elements.add(new ASN1Boolean((byte)(-118), true));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getUserDN() {
        return this.userDN;
    }
    
    public String getTokenID() {
        return this.tokenID;
    }
    
    public Long getValidityDurationMillis() {
        return this.validityDurationMillis;
    }
    
    public String getMessageSubject() {
        return this.messageSubject;
    }
    
    public String getFullTextBeforeToken() {
        return this.fullTextBeforeToken;
    }
    
    public String getFullTextAfterToken() {
        return this.fullTextAfterToken;
    }
    
    public String getCompactTextBeforeToken() {
        return this.compactTextBeforeToken;
    }
    
    public String getCompactTextAfterToken() {
        return this.compactTextAfterToken;
    }
    
    public List<ObjectPair<String, String>> getPreferredDeliveryMechanisms() {
        return this.preferredDeliveryMechanisms;
    }
    
    public boolean deliverIfPasswordExpired() {
        return this.deliverIfPasswordExpired;
    }
    
    public boolean deliverIfAccountLocked() {
        return this.deliverIfAccountLocked;
    }
    
    public boolean deliverIfAccountDisabled() {
        return this.deliverIfAccountDisabled;
    }
    
    public boolean deliverIfAccountExpired() {
        return this.deliverIfAccountExpired;
    }
    
    public DeliverSingleUseTokenExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new DeliverSingleUseTokenExtendedResult(extendedResponse);
    }
    
    @Override
    public DeliverSingleUseTokenExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public DeliverSingleUseTokenExtendedRequest duplicate(final Control[] controls) {
        final DeliverSingleUseTokenExtendedRequest r = new DeliverSingleUseTokenExtendedRequest(this.userDN, this.tokenID, this.validityDurationMillis, this.messageSubject, this.fullTextBeforeToken, this.fullTextAfterToken, this.compactTextBeforeToken, this.compactTextAfterToken, this.preferredDeliveryMechanisms, this.deliverIfPasswordExpired, this.deliverIfAccountLocked, this.deliverIfAccountDisabled, this.deliverIfAccountExpired, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_DELIVER_SINGLE_USE_TOKEN.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("DeliverSingleUseTokenExtendedRequest(userDN='");
        buffer.append(this.userDN);
        buffer.append("', tokenID='");
        buffer.append(this.tokenID);
        buffer.append('\'');
        if (this.validityDurationMillis != null) {
            buffer.append(", validityDurationMillis=");
            buffer.append(this.validityDurationMillis);
        }
        if (this.messageSubject != null) {
            buffer.append(", messageSubject='");
            buffer.append(this.messageSubject);
            buffer.append('\'');
        }
        if (this.fullTextBeforeToken != null) {
            buffer.append(", fullTextBeforeToken='");
            buffer.append(this.fullTextBeforeToken);
            buffer.append('\'');
        }
        if (this.fullTextAfterToken != null) {
            buffer.append(", fullTextAfterToken='");
            buffer.append(this.fullTextAfterToken);
            buffer.append('\'');
        }
        if (this.compactTextBeforeToken != null) {
            buffer.append(", compactTextBeforeToken='");
            buffer.append(this.compactTextBeforeToken);
            buffer.append('\'');
        }
        if (this.compactTextAfterToken != null) {
            buffer.append(", compactTextAfterToken='");
            buffer.append(this.compactTextAfterToken);
            buffer.append('\'');
        }
        if (this.preferredDeliveryMechanisms != null) {
            buffer.append(", preferredDeliveryMechanisms={");
            final Iterator<ObjectPair<String, String>> iterator = this.preferredDeliveryMechanisms.iterator();
            while (iterator.hasNext()) {
                final ObjectPair<String, String> p = iterator.next();
                buffer.append('\'');
                buffer.append(p.getFirst());
                if (p.getSecond() != null) {
                    buffer.append('(');
                    buffer.append(p.getSecond());
                    buffer.append(')');
                }
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(',');
                }
            }
        }
        buffer.append(", deliverIfPasswordExpired=");
        buffer.append(this.deliverIfPasswordExpired);
        buffer.append(", deliverIfAccountLocked=");
        buffer.append(this.deliverIfAccountLocked);
        buffer.append(", deliverIfAccountDisabled=");
        buffer.append(this.deliverIfAccountDisabled);
        buffer.append(", deliverIfAccountExpired=");
        buffer.append(this.deliverIfAccountExpired);
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
