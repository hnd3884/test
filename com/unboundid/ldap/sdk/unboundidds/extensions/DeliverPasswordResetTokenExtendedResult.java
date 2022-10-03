package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DeliverPasswordResetTokenExtendedResult extends ExtendedResult
{
    public static final String DELIVER_PW_RESET_TOKEN_RESULT_OID = "1.3.6.1.4.1.30221.2.6.46";
    private static final byte RECIPIENT_ID_BER_TYPE = Byte.MIN_VALUE;
    private static final byte DELIVERY_MESSAGE_BER_TYPE = -127;
    private static final long serialVersionUID = 576599499447071902L;
    private final String deliveryMechanism;
    private final String deliveryMessage;
    private final String recipientID;
    
    public DeliverPasswordResetTokenExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final String deliveryMechanism, final String recipientID, final String deliveryMessage, final Control... responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, (deliveryMechanism == null) ? null : "1.3.6.1.4.1.30221.2.6.46", encodeValue(deliveryMechanism, recipientID, deliveryMessage), responseControls);
        this.deliveryMechanism = deliveryMechanism;
        this.recipientID = recipientID;
        this.deliveryMessage = deliveryMessage;
    }
    
    public DeliverPasswordResetTokenExtendedResult(final ExtendedResult result) throws LDAPException {
        super(result);
        final ASN1OctetString value = result.getValue();
        if (value == null) {
            this.deliveryMechanism = null;
            this.recipientID = null;
            this.deliveryMessage = null;
            return;
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
            this.deliveryMechanism = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            String id = null;
            String msg = null;
            for (int i = 1; i < elements.length; ++i) {
                switch (elements[i].getType()) {
                    case Byte.MIN_VALUE: {
                        id = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    case -127: {
                        msg = ASN1OctetString.decodeAsOctetString(elements[i]).stringValue();
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_PW_RESET_TOKEN_RESULT_UNEXPECTED_TYPE.get(StaticUtils.toHex(elements[i].getType())));
                    }
                }
            }
            this.recipientID = id;
            this.deliveryMessage = msg;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_DELIVER_PW_RESET_TOKEN_RESULT_ERROR_DECODING_VALUE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final String deliveryMechanism, final String recipientID, final String deliveryMessage) {
        if (deliveryMechanism == null) {
            Validator.ensureTrue(recipientID == null, "The delivery mechanism must be non-null if the recipient ID is non-null.");
            Validator.ensureTrue(deliveryMessage == null, "The delivery mechanism must be non-null if the delivery message is non-null.");
            return null;
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        elements.add(new ASN1OctetString(deliveryMechanism));
        if (recipientID != null) {
            elements.add(new ASN1OctetString((byte)(-128), recipientID));
        }
        if (deliveryMessage != null) {
            elements.add(new ASN1OctetString((byte)(-127), deliveryMessage));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getDeliveryMechanism() {
        return this.deliveryMechanism;
    }
    
    public String getRecipientID() {
        return this.recipientID;
    }
    
    public String getDeliveryMessage() {
        return this.deliveryMessage;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_DELIVER_PW_RESET_TOKEN.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("DeliverPasswordResetTokenExtendedResult(resultCode=");
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
