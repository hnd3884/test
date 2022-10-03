package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DeliverOneTimePasswordExtendedResult extends ExtendedResult
{
    public static final String DELIVER_OTP_RESULT_OID = "1.3.6.1.4.1.30221.2.6.25";
    private static final byte TYPE_MECH = Byte.MIN_VALUE;
    private static final byte TYPE_RECIPIENT_DN = -127;
    private static final byte TYPE_RECIPIENT_ID = -126;
    private static final byte TYPE_MESSAGE = -125;
    private static final long serialVersionUID = 5077693879184160485L;
    private final String deliveryMechanism;
    private final String deliveryMessage;
    private final String recipientDN;
    private final String recipientID;
    
    public DeliverOneTimePasswordExtendedResult(final ExtendedResult extendedResult) throws LDAPException {
        super(extendedResult);
        final ASN1OctetString value = extendedResult.getValue();
        if (value == null) {
            this.deliveryMechanism = null;
            this.recipientDN = null;
            this.recipientID = null;
            this.deliveryMessage = null;
            return;
        }
        String mech = null;
        String dn = null;
        String id = null;
        String message = null;
        try {
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(value.getValue()).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        mech = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -127: {
                        dn = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -126: {
                        id = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -125: {
                        message = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_OTP_RES_UNEXPECTED_ELEMENT_TYPE.get(StaticUtils.toHex(e.getType())));
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
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_OTP_RES_ERROR_PARSING_VALUE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
        if (mech == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_OTP_RES_NO_MECH.get());
        }
        this.deliveryMechanism = mech;
        if (dn == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_OTP_RES_NO_RECIPIENT_DN.get());
        }
        this.recipientDN = dn;
        this.recipientID = id;
        this.deliveryMessage = message;
    }
    
    public DeliverOneTimePasswordExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final String deliveryMechanism, final String recipientDN, final String recipientID, final String deliveryMessage, final Control... responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, (deliveryMechanism == null) ? null : "1.3.6.1.4.1.30221.2.6.25", encodeValue(deliveryMechanism, recipientDN, recipientID, deliveryMessage), responseControls);
        this.deliveryMechanism = deliveryMechanism;
        this.recipientDN = recipientDN;
        this.recipientID = recipientID;
        this.deliveryMessage = deliveryMessage;
    }
    
    private static ASN1OctetString encodeValue(final String deliveryMechanism, final String recipientDN, final String recipientID, final String deliveryMessage) {
        if (deliveryMechanism == null) {
            Validator.ensureTrue(recipientID == null, "The delivery mechanism must be non-null if the recipient ID is non-null.");
            Validator.ensureTrue(deliveryMessage == null, "The delivery mechanism must be non-null if the delivery message is non-null.");
            return null;
        }
        Validator.ensureTrue(recipientDN != null, "If a delivery mechanism is provided, then a recipient DN must also be provided.");
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(4);
        elements.add(new ASN1OctetString((byte)(-128), deliveryMechanism));
        elements.add(new ASN1OctetString((byte)(-127), recipientDN));
        if (recipientID != null) {
            elements.add(new ASN1OctetString((byte)(-126), recipientID));
        }
        if (deliveryMessage != null) {
            elements.add(new ASN1OctetString((byte)(-125), deliveryMessage));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getDeliveryMechanism() {
        return this.deliveryMechanism;
    }
    
    public String getRecipientDN() {
        return this.recipientDN;
    }
    
    public String getRecipientID() {
        return this.recipientID;
    }
    
    public String getDeliveryMessage() {
        return this.deliveryMessage;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_DELIVER_OTP_RES_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("DeliverOneTimePasswordExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        if (this.deliveryMechanism != null) {
            buffer.append(", deliveryMechanism='");
            buffer.append(this.deliveryMechanism);
            buffer.append('\'');
        }
        if (this.recipientDN != null) {
            buffer.append(", recipientDN='");
            buffer.append(this.recipientDN);
            buffer.append('\'');
        }
        if (this.recipientID != null) {
            buffer.append(", recipientID='");
            buffer.append(this.recipientID);
            buffer.append('\'');
        }
        if (this.deliveryMessage != null) {
            buffer.append(", deliveryMessage='");
            buffer.append(this.deliveryMessage);
            buffer.append('\'');
        }
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
