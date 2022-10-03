package com.unboundid.ldap.sdk.extensions;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1OctetString;
import java.util.Map;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Exception;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.Control;
import java.util.TreeMap;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class EndTransactionExtendedResult extends ExtendedResult
{
    private static final long serialVersionUID = 1514265185948328221L;
    private final int failedOpMessageID;
    private final TreeMap<Integer, Control[]> opResponseControls;
    
    public EndTransactionExtendedResult(final ExtendedResult extendedResult) throws LDAPException {
        super(extendedResult);
        this.opResponseControls = new TreeMap<Integer, Control[]>();
        final ASN1OctetString value = extendedResult.getValue();
        if (value == null) {
            this.failedOpMessageID = -1;
            return;
        }
        ASN1Sequence valueSequence;
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            valueSequence = ASN1Sequence.decodeAsSequence(valueElement);
        }
        catch (final ASN1Exception ae) {
            Debug.debugException(ae);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_TXN_RESPONSE_VALUE_NOT_SEQUENCE.get(ae.getMessage()), ae);
        }
        final ASN1Element[] valueElements = valueSequence.elements();
        if (valueElements.length == 0) {
            this.failedOpMessageID = -1;
            return;
        }
        if (valueElements.length > 2) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_TXN_RESPONSE_INVALID_ELEMENT_COUNT.get(valueElements.length));
        }
        int msgID = -1;
        for (final ASN1Element e : valueElements) {
            Label_0282: {
                if (e.getType() == 2) {
                    try {
                        msgID = ASN1Integer.decodeAsInteger(e).intValue();
                        break Label_0282;
                    }
                    catch (final ASN1Exception ae2) {
                        Debug.debugException(ae2);
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_TXN_RESPONSE_CANNOT_DECODE_MSGID.get(ae2), ae2);
                    }
                }
                if (e.getType() != 48) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_TXN_RESPONSE_INVALID_TYPE.get(StaticUtils.toHex(e.getType())));
                }
                decodeOpControls(e, this.opResponseControls);
            }
        }
        this.failedOpMessageID = msgID;
    }
    
    public EndTransactionExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final Integer failedOpMessageID, final Map<Integer, Control[]> opResponseControls, final Control[] responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, null, encodeValue(failedOpMessageID, opResponseControls), responseControls);
        if (failedOpMessageID == null || failedOpMessageID <= 0) {
            this.failedOpMessageID = -1;
        }
        else {
            this.failedOpMessageID = failedOpMessageID;
        }
        if (opResponseControls == null) {
            this.opResponseControls = new TreeMap<Integer, Control[]>();
        }
        else {
            this.opResponseControls = new TreeMap<Integer, Control[]>(opResponseControls);
        }
    }
    
    private static void decodeOpControls(final ASN1Element element, final Map<Integer, Control[]> controlMap) throws LDAPException {
        ASN1Sequence ctlsSequence;
        try {
            ctlsSequence = ASN1Sequence.decodeAsSequence(element);
        }
        catch (final ASN1Exception ae) {
            Debug.debugException(ae);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_TXN_RESPONSE_CONTROLS_NOT_SEQUENCE.get(ae), ae);
        }
        for (final ASN1Element e : ctlsSequence.elements()) {
            ASN1Sequence ctlSequence;
            try {
                ctlSequence = ASN1Sequence.decodeAsSequence(e);
            }
            catch (final ASN1Exception ae2) {
                Debug.debugException(ae2);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_TXN_RESPONSE_CONTROL_NOT_SEQUENCE.get(ae2), ae2);
            }
            final ASN1Element[] ctlSequenceElements = ctlSequence.elements();
            if (ctlSequenceElements.length != 2) {
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_TXN_RESPONSE_CONTROL_INVALID_ELEMENT_COUNT.get(ctlSequenceElements.length));
            }
            int msgID;
            try {
                msgID = ASN1Integer.decodeAsInteger(ctlSequenceElements[0]).intValue();
            }
            catch (final ASN1Exception ae3) {
                Debug.debugException(ae3);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_TXN_RESPONSE_CONTROL_MSGID_NOT_INT.get(ae3), ae3);
            }
            ASN1Sequence controlsSequence;
            try {
                controlsSequence = ASN1Sequence.decodeAsSequence(ctlSequenceElements[1]);
            }
            catch (final ASN1Exception ae4) {
                Debug.debugException(ae4);
                throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_END_TXN_RESPONSE_CONTROLS_ELEMENT_NOT_SEQUENCE.get(ae4), ae4);
            }
            final Control[] controls = Control.decodeControls(controlsSequence);
            if (controls.length != 0) {
                controlMap.put(msgID, controls);
            }
        }
    }
    
    private static ASN1OctetString encodeValue(final Integer failedOpMessageID, final Map<Integer, Control[]> opResponseControls) {
        if (failedOpMessageID == null && opResponseControls == null) {
            return null;
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(2);
        if (failedOpMessageID != null) {
            elements.add(new ASN1Integer(failedOpMessageID));
        }
        if (opResponseControls != null && !opResponseControls.isEmpty()) {
            final ArrayList<ASN1Element> controlElements = new ArrayList<ASN1Element>(10);
            for (final Map.Entry<Integer, Control[]> e : opResponseControls.entrySet()) {
                final ASN1Element[] ctlElements = { new ASN1Integer(e.getKey()), Control.encodeControls(e.getValue()) };
                controlElements.add(new ASN1Sequence(ctlElements));
            }
            elements.add(new ASN1Sequence(controlElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public int getFailedOpMessageID() {
        return this.failedOpMessageID;
    }
    
    public Map<Integer, Control[]> getOperationResponseControls() {
        return this.opResponseControls;
    }
    
    public Control[] getOperationResponseControls(final int messageID) {
        return this.opResponseControls.get(messageID);
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_END_TXN.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("EndTransactionExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        if (this.failedOpMessageID > 0) {
            buffer.append(", failedOpMessageID=");
            buffer.append(this.failedOpMessageID);
        }
        if (!this.opResponseControls.isEmpty()) {
            buffer.append(", opResponseControls={");
            for (final int msgID : this.opResponseControls.keySet()) {
                buffer.append("opMsgID=");
                buffer.append(msgID);
                buffer.append(", opControls={");
                boolean first = true;
                for (final Control c : this.opResponseControls.get(msgID)) {
                    if (first) {
                        first = false;
                    }
                    else {
                        buffer.append(", ");
                    }
                    buffer.append(c);
                }
                buffer.append('}');
            }
            buffer.append('}');
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
