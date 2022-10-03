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
public final class DeliverPasswordResetTokenExtendedRequest extends ExtendedRequest
{
    public static final String DELIVER_PW_RESET_TOKEN_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.45";
    private static final byte MESSAGE_SUBJECT_BER_TYPE = Byte.MIN_VALUE;
    private static final byte FULL_TEXT_BEFORE_TOKEN_BER_TYPE = -127;
    private static final byte FULL_TEXT_AFTER_TOKEN_BER_TYPE = -126;
    private static final byte COMPACT_TEXT_BEFORE_TOKEN_BER_TYPE = -125;
    private static final byte COMPACT_TEXT_AFTER_TOKEN_BER_TYPE = -124;
    private static final byte PREFERRED_DELIVERY_MECHANISM_BER_TYPE = -91;
    private static final long serialVersionUID = 7608072810737347230L;
    private final List<ObjectPair<String, String>> preferredDeliveryMechanisms;
    private final String compactTextAfterToken;
    private final String compactTextBeforeToken;
    private final String fullTextAfterToken;
    private final String fullTextBeforeToken;
    private final String messageSubject;
    private final String userDN;
    
    public DeliverPasswordResetTokenExtendedRequest(final String userDN, final String... preferredDeliveryMechanisms) {
        this(userDN, preferredMechanismsToList(preferredDeliveryMechanisms), new Control[0]);
    }
    
    public DeliverPasswordResetTokenExtendedRequest(final String userDN, final List<ObjectPair<String, String>> preferredDeliveryMechanisms, final Control... controls) {
        this(userDN, null, null, null, null, null, preferredDeliveryMechanisms, controls);
    }
    
    public DeliverPasswordResetTokenExtendedRequest(final String userDN, final String messageSubject, final String fullTextBeforeToken, final String fullTextAfterToken, final String compactTextBeforeToken, final String compactTextAfterToken, final List<ObjectPair<String, String>> preferredDeliveryMechanisms, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.45", encodeValue(userDN, messageSubject, fullTextBeforeToken, fullTextAfterToken, compactTextBeforeToken, compactTextAfterToken, preferredDeliveryMechanisms), controls);
        this.userDN = userDN;
        this.messageSubject = messageSubject;
        this.fullTextBeforeToken = fullTextBeforeToken;
        this.fullTextAfterToken = fullTextAfterToken;
        this.compactTextBeforeToken = compactTextBeforeToken;
        this.compactTextAfterToken = compactTextAfterToken;
        if (preferredDeliveryMechanisms == null) {
            this.preferredDeliveryMechanisms = Collections.emptyList();
        }
        else {
            this.preferredDeliveryMechanisms = Collections.unmodifiableList((List<? extends ObjectPair<String, String>>)new ArrayList<ObjectPair<String, String>>(preferredDeliveryMechanisms));
        }
    }
    
    public DeliverPasswordResetTokenExtendedRequest(final ExtendedRequest request) throws LDAPException {
        super(request);
        final ASN1OctetString value = request.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_PW_RESET_TOKEN_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.userDN = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            String subject = null;
            String fullBefore = null;
            String fullAfter = null;
            String compactBefore = null;
            String compactAfter = null;
            final ArrayList<ObjectPair<String, String>> pdmList = new ArrayList<ObjectPair<String, String>>(10);
            for (int i = 1; i < elements.length; ++i) {
                switch (elements[i].getType()) {
                    case Byte.MIN_VALUE: {
                        subject = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    case -127: {
                        fullBefore = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    case -126: {
                        fullAfter = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    case -125: {
                        compactBefore = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    case -124: {
                        compactAfter = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    case -91: {
                        final ASN1Element[] arr$;
                        final ASN1Element[] pdmElements = arr$ = ASN1Sequence.decodeAsSequence(elements[i]).elements();
                        for (final ASN1Element e : arr$) {
                            final ASN1Element[] mechElements = ASN1Sequence.decodeAsSequence(e).elements();
                            final String mech = ASN1OctetString.decodeAsOctetString(mechElements[0]).stringValue();
                            String recipientID;
                            if (mechElements.length > 1) {
                                recipientID = ASN1OctetString.decodeAsOctetString(mechElements[1]).stringValue();
                            }
                            else {
                                recipientID = null;
                            }
                            pdmList.add(new ObjectPair<String, String>(mech, recipientID));
                        }
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_PW_RESET_TOKEN_REQUEST_UNEXPECTED_TYPE.get(StaticUtils.toHex(elements[i].getType())));
                    }
                }
            }
            this.preferredDeliveryMechanisms = Collections.unmodifiableList((List<? extends ObjectPair<String, String>>)pdmList);
            this.messageSubject = subject;
            this.fullTextBeforeToken = fullBefore;
            this.fullTextAfterToken = fullAfter;
            this.compactTextBeforeToken = compactBefore;
            this.compactTextAfterToken = compactAfter;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_PW_RESET_TOKEN_REQUEST_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static List<ObjectPair<String, String>> preferredMechanismsToList(final String... preferredDeliveryMechanisms) {
        if (preferredDeliveryMechanisms == null) {
            return null;
        }
        final ArrayList<ObjectPair<String, String>> l = new ArrayList<ObjectPair<String, String>>(preferredDeliveryMechanisms.length);
        for (final String s : preferredDeliveryMechanisms) {
            l.add(new ObjectPair<String, String>(s, null));
        }
        return l;
    }
    
    private static ASN1OctetString encodeValue(final String userDN, final String messageSubject, final String fullTextBeforeToken, final String fullTextAfterToken, final String compactTextBeforeToken, final String compactTextAfterToken, final List<ObjectPair<String, String>> preferredDeliveryMechanisms) {
        Validator.ensureNotNull(userDN);
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(7);
        elements.add(new ASN1OctetString(userDN));
        if (messageSubject != null) {
            elements.add(new ASN1OctetString((byte)(-128), messageSubject));
        }
        if (fullTextBeforeToken != null) {
            elements.add(new ASN1OctetString((byte)(-127), fullTextBeforeToken));
        }
        if (fullTextAfterToken != null) {
            elements.add(new ASN1OctetString((byte)(-126), fullTextAfterToken));
        }
        if (compactTextBeforeToken != null) {
            elements.add(new ASN1OctetString((byte)(-125), compactTextBeforeToken));
        }
        if (compactTextAfterToken != null) {
            elements.add(new ASN1OctetString((byte)(-124), compactTextAfterToken));
        }
        if (preferredDeliveryMechanisms != null && !preferredDeliveryMechanisms.isEmpty()) {
            final ArrayList<ASN1Element> pdmElements = new ArrayList<ASN1Element>(preferredDeliveryMechanisms.size());
            for (final ObjectPair<String, String> p : preferredDeliveryMechanisms) {
                if (p.getSecond() == null) {
                    pdmElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(p.getFirst()) }));
                }
                else {
                    pdmElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(p.getFirst()), new ASN1OctetString(p.getSecond()) }));
                }
            }
            elements.add(new ASN1Sequence((byte)(-91), pdmElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getUserDN() {
        return this.userDN;
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
    
    public DeliverPasswordResetTokenExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new DeliverPasswordResetTokenExtendedResult(extendedResponse);
    }
    
    @Override
    public DeliverPasswordResetTokenExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public DeliverPasswordResetTokenExtendedRequest duplicate(final Control[] controls) {
        final DeliverPasswordResetTokenExtendedRequest r = new DeliverPasswordResetTokenExtendedRequest(this.userDN, this.messageSubject, this.fullTextBeforeToken, this.fullTextAfterToken, this.compactTextBeforeToken, this.compactTextAfterToken, this.preferredDeliveryMechanisms, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_EXTENDED_REQUEST_NAME_DELIVER_PW_RESET_TOKEN.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("DeliverPasswordResetTokenExtendedRequest(userDN='");
        buffer.append(this.userDN);
        buffer.append('\'');
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
