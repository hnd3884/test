package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.LinkedHashSet;
import java.util.Collection;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ObjectPair;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DeliverOneTimePasswordExtendedRequest extends ExtendedRequest
{
    public static final String DELIVER_OTP_REQUEST_OID = "1.3.6.1.4.1.30221.2.6.24";
    private static final byte TYPE_AUTHN_ID = Byte.MIN_VALUE;
    private static final byte TYPE_PASSWORD = -127;
    private static final byte TYPE_PREFERRED_DELIVERY_MECHANISM_NAMES = -94;
    private static final byte TYPE_PREFERRED_DELIVERY_MECHANISM_NAMES_AND_IDS = -93;
    private static final byte MESSAGE_SUBJECT_BER_TYPE = -124;
    private static final byte FULL_TEXT_BEFORE_OTP_BER_TYPE = -123;
    private static final byte FULL_TEXT_AFTER_OTP_BER_TYPE = -122;
    private static final byte COMPACT_TEXT_BEFORE_OTP_BER_TYPE = -121;
    private static final byte COMPACT_TEXT_AFTER_OTP_BER_TYPE = -120;
    private static final long serialVersionUID = 1259250969726758847L;
    private final ASN1OctetString staticPassword;
    private final List<ObjectPair<String, String>> preferredDeliveryMechanisms;
    private final String authenticationID;
    private final String compactTextAfterOTP;
    private final String compactTextBeforeOTP;
    private final String fullTextAfterOTP;
    private final String fullTextBeforeOTP;
    private final String messageSubject;
    
    public DeliverOneTimePasswordExtendedRequest(final String authenticationID, final String staticPassword, final String... preferredDeliveryMechanisms) {
        this(authenticationID, staticPassword, StaticUtils.toList(preferredDeliveryMechanisms), new Control[0]);
    }
    
    public DeliverOneTimePasswordExtendedRequest(final String authenticationID, final byte[] staticPassword, final String... preferredDeliveryMechanisms) {
        this(authenticationID, staticPassword, StaticUtils.toList(preferredDeliveryMechanisms), new Control[0]);
    }
    
    public DeliverOneTimePasswordExtendedRequest(final String authenticationID, final String staticPassword, final List<String> preferredDeliveryMechanisms, final Control... controls) {
        this(authenticationID, (staticPassword == null) ? null : new ASN1OctetString((byte)(-127), staticPassword), preferredDeliveryMechanisms, controls);
    }
    
    public DeliverOneTimePasswordExtendedRequest(final String authenticationID, final byte[] staticPassword, final List<String> preferredDeliveryMechanisms, final Control... controls) {
        this(authenticationID, (staticPassword == null) ? null : new ASN1OctetString((byte)(-127), staticPassword), preferredDeliveryMechanisms, controls);
    }
    
    private DeliverOneTimePasswordExtendedRequest(final String authenticationID, final ASN1OctetString staticPassword, final List<String> preferredDeliveryMechanisms, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.24", encodeValue(authenticationID, staticPassword, preferredDeliveryMechanisms), controls);
        this.authenticationID = authenticationID;
        this.staticPassword = staticPassword;
        if (preferredDeliveryMechanisms == null || preferredDeliveryMechanisms.isEmpty()) {
            this.preferredDeliveryMechanisms = Collections.emptyList();
        }
        else {
            final ArrayList<ObjectPair<String, String>> l = new ArrayList<ObjectPair<String, String>>(preferredDeliveryMechanisms.size());
            for (final String s : preferredDeliveryMechanisms) {
                l.add(new ObjectPair<String, String>(s, null));
            }
            this.preferredDeliveryMechanisms = Collections.unmodifiableList((List<? extends ObjectPair<String, String>>)l);
        }
        this.messageSubject = null;
        this.fullTextBeforeOTP = null;
        this.fullTextAfterOTP = null;
        this.compactTextBeforeOTP = null;
        this.compactTextAfterOTP = null;
    }
    
    public DeliverOneTimePasswordExtendedRequest(final String authenticationID, final String staticPassword, final String messageSubject, final String fullTextBeforeOTP, final String fullTextAfterOTP, final String compactTextBeforeOTP, final String compactTextAfterOTP, final List<ObjectPair<String, String>> preferredDeliveryMechanisms, final Control... controls) {
        this(authenticationID, (staticPassword == null) ? null : new ASN1OctetString((byte)(-127), staticPassword), messageSubject, fullTextBeforeOTP, fullTextAfterOTP, compactTextBeforeOTP, compactTextAfterOTP, preferredDeliveryMechanisms, controls);
    }
    
    public DeliverOneTimePasswordExtendedRequest(final String authenticationID, final byte[] staticPassword, final String messageSubject, final String fullTextBeforeOTP, final String fullTextAfterOTP, final String compactTextBeforeOTP, final String compactTextAfterOTP, final List<ObjectPair<String, String>> preferredDeliveryMechanisms, final Control... controls) {
        this(authenticationID, (staticPassword == null) ? null : new ASN1OctetString((byte)(-127), staticPassword), messageSubject, fullTextBeforeOTP, fullTextAfterOTP, compactTextBeforeOTP, compactTextAfterOTP, preferredDeliveryMechanisms, controls);
    }
    
    private DeliverOneTimePasswordExtendedRequest(final String authenticationID, final ASN1OctetString staticPassword, final String messageSubject, final String fullTextBeforeOTP, final String fullTextAfterOTP, final String compactTextBeforeOTP, final String compactTextAfterOTP, final List<ObjectPair<String, String>> preferredDeliveryMechanisms, final Control... controls) {
        super("1.3.6.1.4.1.30221.2.6.24", encodeValue(authenticationID, staticPassword, messageSubject, fullTextBeforeOTP, fullTextAfterOTP, compactTextBeforeOTP, compactTextAfterOTP, preferredDeliveryMechanisms), controls);
        this.authenticationID = authenticationID;
        this.staticPassword = staticPassword;
        this.messageSubject = messageSubject;
        this.fullTextBeforeOTP = fullTextBeforeOTP;
        this.fullTextAfterOTP = fullTextAfterOTP;
        this.compactTextBeforeOTP = compactTextBeforeOTP;
        this.compactTextAfterOTP = compactTextAfterOTP;
        if (preferredDeliveryMechanisms == null || preferredDeliveryMechanisms.isEmpty()) {
            this.preferredDeliveryMechanisms = Collections.emptyList();
        }
        else {
            this.preferredDeliveryMechanisms = Collections.unmodifiableList((List<? extends ObjectPair<String, String>>)preferredDeliveryMechanisms);
        }
    }
    
    public DeliverOneTimePasswordExtendedRequest(final ExtendedRequest request) throws LDAPException {
        super(request);
        final ASN1OctetString value = request.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_OTP_REQ_NO_VALUE.get());
        }
        ASN1OctetString password = null;
        String authnID = null;
        String subject = null;
        String fullBefore = null;
        String fullAfter = null;
        String compactBefore = null;
        String compactAfter = null;
        final ArrayList<ObjectPair<String, String>> pdmList = new ArrayList<ObjectPair<String, String>>(10);
        try {
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(value.getValue()).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        authnID = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -127: {
                        password = ASN1OctetString.decodeAsOctetString(e);
                        break;
                    }
                    case -94: {
                        final ASN1Element[] arr$2;
                        final ASN1Element[] mechNameElements = arr$2 = ASN1Sequence.decodeAsSequence(e).elements();
                        for (final ASN1Element mechElement : arr$2) {
                            pdmList.add(new ObjectPair<String, String>(ASN1OctetString.decodeAsOctetString(mechElement).stringValue(), null));
                        }
                        break;
                    }
                    case -93: {
                        final ASN1Element[] arr$3;
                        final ASN1Element[] pdmElements = arr$3 = ASN1Sequence.decodeAsSequence(e).elements();
                        for (final ASN1Element pdmElement : arr$3) {
                            final ASN1Element[] mechElements = ASN1Sequence.decodeAsSequence(pdmElement).elements();
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
                    case -124: {
                        subject = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -123: {
                        fullBefore = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -122: {
                        fullAfter = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -121: {
                        compactBefore = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -120: {
                        compactAfter = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_OTP_REQ_UNEXPECTED_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_OTP_REQ_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        if (authnID == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_OTP_REQ_NO_AUTHN_ID.get());
        }
        this.authenticationID = authnID;
        this.staticPassword = password;
        this.messageSubject = subject;
        this.fullTextBeforeOTP = fullBefore;
        this.fullTextAfterOTP = fullAfter;
        this.compactTextBeforeOTP = compactBefore;
        this.compactTextAfterOTP = compactAfter;
        if (pdmList == null || pdmList.isEmpty()) {
            this.preferredDeliveryMechanisms = Collections.emptyList();
        }
        else {
            this.preferredDeliveryMechanisms = Collections.unmodifiableList((List<? extends ObjectPair<String, String>>)pdmList);
        }
    }
    
    private static ASN1OctetString encodeValue(final String authenticationID, final ASN1OctetString staticPassword, final List<String> preferredDeliveryMechanisms) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        elements.add(new ASN1OctetString((byte)(-128), authenticationID));
        if (staticPassword != null) {
            elements.add(staticPassword);
        }
        if (preferredDeliveryMechanisms != null && !preferredDeliveryMechanisms.isEmpty()) {
            final ArrayList<ASN1Element> dmElements = new ArrayList<ASN1Element>(preferredDeliveryMechanisms.size());
            for (final String s : preferredDeliveryMechanisms) {
                dmElements.add(new ASN1OctetString(s));
            }
            elements.add(new ASN1Sequence((byte)(-94), dmElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    private static ASN1OctetString encodeValue(final String authenticationID, final ASN1OctetString staticPassword, final String messageSubject, final String fullTextBeforeOTP, final String fullTextAfterOTP, final String compactTextBeforeOTP, final String compactTextAfterOTP, final List<ObjectPair<String, String>> preferredDeliveryMechanisms) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(8);
        elements.add(new ASN1OctetString((byte)(-128), authenticationID));
        if (staticPassword != null) {
            elements.add(staticPassword);
        }
        if (messageSubject != null) {
            elements.add(new ASN1OctetString((byte)(-124), messageSubject));
        }
        if (fullTextBeforeOTP != null) {
            elements.add(new ASN1OctetString((byte)(-123), fullTextBeforeOTP));
        }
        if (fullTextAfterOTP != null) {
            elements.add(new ASN1OctetString((byte)(-122), fullTextAfterOTP));
        }
        if (compactTextBeforeOTP != null) {
            elements.add(new ASN1OctetString((byte)(-121), compactTextBeforeOTP));
        }
        if (compactTextAfterOTP != null) {
            elements.add(new ASN1OctetString((byte)(-120), compactTextAfterOTP));
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
            elements.add(new ASN1Sequence((byte)(-93), pdmElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getAuthenticationID() {
        return this.authenticationID;
    }
    
    public ASN1OctetString getStaticPassword() {
        return this.staticPassword;
    }
    
    public List<String> getPreferredDeliveryMechanisms() {
        if (this.preferredDeliveryMechanisms.isEmpty()) {
            return null;
        }
        final LinkedHashSet<String> s = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(this.preferredDeliveryMechanisms.size()));
        for (final ObjectPair<String, String> p : this.preferredDeliveryMechanisms) {
            s.add(p.getFirst());
        }
        return Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(s));
    }
    
    public List<ObjectPair<String, String>> getPreferredDeliveryMechanismNamesAndIDs() {
        return this.preferredDeliveryMechanisms;
    }
    
    public String getMessageSubject() {
        return this.messageSubject;
    }
    
    public String getFullTextBeforeOTP() {
        return this.fullTextBeforeOTP;
    }
    
    public String getFullTextAfterOTP() {
        return this.fullTextAfterOTP;
    }
    
    public String getCompactTextBeforeOTP() {
        return this.compactTextBeforeOTP;
    }
    
    public String getCompactTextAfterOTP() {
        return this.compactTextAfterOTP;
    }
    
    public DeliverOneTimePasswordExtendedResult process(final LDAPConnection connection, final int depth) throws LDAPException {
        final ExtendedResult extendedResponse = super.process(connection, depth);
        return new DeliverOneTimePasswordExtendedResult(extendedResponse);
    }
    
    @Override
    public DeliverOneTimePasswordExtendedRequest duplicate() {
        return this.duplicate(this.getControls());
    }
    
    @Override
    public DeliverOneTimePasswordExtendedRequest duplicate(final Control[] controls) {
        final DeliverOneTimePasswordExtendedRequest r = new DeliverOneTimePasswordExtendedRequest(this.authenticationID, this.staticPassword, this.messageSubject, this.fullTextBeforeOTP, this.fullTextAfterOTP, this.compactTextBeforeOTP, this.compactTextAfterOTP, this.preferredDeliveryMechanisms, controls);
        r.setResponseTimeoutMillis(this.getResponseTimeoutMillis(null));
        return r;
    }
    
    @Override
    public String getExtendedRequestName() {
        return ExtOpMessages.INFO_DELIVER_OTP_REQ_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("DeliverOneTimePasswordExtendedRequest(authenticationID=");
        buffer.append(this.authenticationID);
        if (this.messageSubject != null) {
            buffer.append(", messageSubject='");
            buffer.append(this.messageSubject);
            buffer.append('\'');
        }
        if (this.fullTextBeforeOTP != null) {
            buffer.append(", fullTextBeforeOTP='");
            buffer.append(this.fullTextBeforeOTP);
            buffer.append('\'');
        }
        if (this.fullTextAfterOTP != null) {
            buffer.append(", fullTextAfterOTP='");
            buffer.append(this.fullTextAfterOTP);
            buffer.append('\'');
        }
        if (this.compactTextBeforeOTP != null) {
            buffer.append(", compactTextBeforeOTP='");
            buffer.append(this.compactTextBeforeOTP);
            buffer.append('\'');
        }
        if (this.compactTextAfterOTP != null) {
            buffer.append(", compactTextAfterOTP='");
            buffer.append(this.compactTextAfterOTP);
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
