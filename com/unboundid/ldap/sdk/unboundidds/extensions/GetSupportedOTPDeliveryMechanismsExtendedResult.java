package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
import com.unboundid.ldap.sdk.Control;
import java.util.Collection;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1Boolean;
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
public final class GetSupportedOTPDeliveryMechanismsExtendedResult extends ExtendedResult
{
    public static final String GET_SUPPORTED_OTP_DELIVERY_MECHANISMS_RESULT_OID = "1.3.6.1.4.1.30221.2.6.48";
    private static final byte TYPE_DELIVERY_MECHANISM = Byte.MIN_VALUE;
    private static final byte TYPE_IS_SUPPORTED = -127;
    private static final byte TYPE_RECIPIENT_ID = -126;
    private static final long serialVersionUID = -1811121368502797059L;
    private final List<SupportedOTPDeliveryMechanismInfo> deliveryMechanismInfo;
    
    public GetSupportedOTPDeliveryMechanismsExtendedResult(final ExtendedResult result) throws LDAPException {
        super(result);
        final ASN1OctetString value = result.getValue();
        if (value == null) {
            this.deliveryMechanismInfo = Collections.emptyList();
        }
        else {
            try {
                final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(value.getValue()).elements();
                final ArrayList<SupportedOTPDeliveryMechanismInfo> mechInfo = new ArrayList<SupportedOTPDeliveryMechanismInfo>(elements.length);
                for (final ASN1Element e : elements) {
                    final ASN1Element[] infoElements = ASN1Sequence.decodeAsSequence(e).elements();
                    final String name = ASN1OctetString.decodeAsOctetString(infoElements[0]).stringValue();
                    Boolean isSupported = null;
                    String recipientID = null;
                    for (int i = 1; i < infoElements.length; ++i) {
                        switch (infoElements[i].getType()) {
                            case -127: {
                                isSupported = ASN1Boolean.decodeAsBoolean(infoElements[i]).booleanValue();
                                break;
                            }
                            case -126: {
                                recipientID = ASN1OctetString.decodeAsOctetString(infoElements[i]).stringValue();
                                break;
                            }
                            default: {
                                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_SUPPORTED_OTP_MECH_RESULT_UNKNOWN_ELEMENT.get(StaticUtils.toHex(infoElements[i].getType())));
                            }
                        }
                    }
                    mechInfo.add(new SupportedOTPDeliveryMechanismInfo(name, isSupported, recipientID));
                }
                this.deliveryMechanismInfo = Collections.unmodifiableList((List<? extends SupportedOTPDeliveryMechanismInfo>)mechInfo);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                throw le;
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_SUPPORTED_OTP_MECH_RESULT_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
            }
        }
    }
    
    public GetSupportedOTPDeliveryMechanismsExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final Collection<SupportedOTPDeliveryMechanismInfo> deliveryMechanismInfo, final Control... controls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, (resultCode == ResultCode.SUCCESS) ? "1.3.6.1.4.1.30221.2.6.48" : null, encodeValue(resultCode, deliveryMechanismInfo), controls);
        if (deliveryMechanismInfo == null || deliveryMechanismInfo.isEmpty()) {
            this.deliveryMechanismInfo = Collections.emptyList();
        }
        else {
            this.deliveryMechanismInfo = Collections.unmodifiableList((List<? extends SupportedOTPDeliveryMechanismInfo>)new ArrayList<SupportedOTPDeliveryMechanismInfo>(deliveryMechanismInfo));
        }
    }
    
    private static ASN1OctetString encodeValue(final ResultCode resultCode, final Collection<SupportedOTPDeliveryMechanismInfo> deliveryMechanismInfo) {
        if (resultCode != ResultCode.SUCCESS) {
            return null;
        }
        if (deliveryMechanismInfo == null || deliveryMechanismInfo.isEmpty()) {
            return new ASN1OctetString(new ASN1Sequence().encode());
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(deliveryMechanismInfo.size());
        for (final SupportedOTPDeliveryMechanismInfo i : deliveryMechanismInfo) {
            final ArrayList<ASN1Element> infoElements = new ArrayList<ASN1Element>(3);
            infoElements.add(new ASN1OctetString((byte)(-128), i.getDeliveryMechanism()));
            if (i.isSupported() != null) {
                infoElements.add(new ASN1Boolean((byte)(-127), i.isSupported()));
            }
            if (i.getRecipientID() != null) {
                infoElements.add(new ASN1OctetString((byte)(-126), i.getRecipientID()));
            }
            elements.add(new ASN1Sequence(infoElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public List<SupportedOTPDeliveryMechanismInfo> getDeliveryMechanismInfo() {
        return this.deliveryMechanismInfo;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_GET_SUPPORTED_OTP_MECH_RES_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetSupportedOTPDeliveryMechanismsExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        buffer.append("mechanismInfo={");
        final Iterator<SupportedOTPDeliveryMechanismInfo> mechIterator = this.deliveryMechanismInfo.iterator();
        while (mechIterator.hasNext()) {
            mechIterator.next().toString(buffer);
            if (mechIterator.hasNext()) {
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
