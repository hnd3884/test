package com.unboundid.ldap.sdk.unboundidds.extensions;

import java.util.Iterator;
import java.util.Collection;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.util.StaticUtils;
import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import java.util.List;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class StartInteractiveTransactionExtendedResult extends ExtendedResult
{
    private static final byte TYPE_TXN_ID = Byte.MIN_VALUE;
    private static final byte TYPE_BASE_DNS = -95;
    private static final long serialVersionUID = 4010094216900393866L;
    private final ASN1OctetString transactionID;
    private final List<String> baseDNs;
    
    public StartInteractiveTransactionExtendedResult(final ExtendedResult extendedResult) throws LDAPException {
        super(extendedResult);
        if (!extendedResult.hasValue()) {
            this.transactionID = null;
            this.baseDNs = null;
            return;
        }
        ASN1Sequence valueSequence;
        try {
            final ASN1Element valueElement = ASN1Element.decode(extendedResult.getValue().getValue());
            valueSequence = ASN1Sequence.decodeAsSequence(valueElement);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_START_INT_TXN_RESULT_VALUE_NOT_SEQUENCE.get(e.getMessage()), e);
        }
        ASN1OctetString txnID = null;
        List<String> baseDNList = null;
        for (final ASN1Element element : valueSequence.elements()) {
            switch (element.getType()) {
                case Byte.MIN_VALUE: {
                    txnID = ASN1OctetString.decodeAsOctetString(element);
                    break;
                }
                case -95: {
                    try {
                        final ASN1Sequence baseDNsSequence = ASN1Sequence.decodeAsSequence(element);
                        final ArrayList<String> dnList = new ArrayList<String>(baseDNsSequence.elements().length);
                        for (final ASN1Element e2 : baseDNsSequence.elements()) {
                            dnList.add(ASN1OctetString.decodeAsOctetString(e2).stringValue());
                        }
                        baseDNList = Collections.unmodifiableList((List<? extends String>)dnList);
                        break;
                    }
                    catch (final Exception e3) {
                        Debug.debugException(e3);
                        throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_START_INT_TXN_RESULT_BASE_DNS_NOT_SEQUENCE.get(e3.getMessage()), e3);
                    }
                    throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_START_INT_TXN_RESULT_INVALID_ELEMENT.get(StaticUtils.toHex(element.getType())));
                }
            }
        }
        this.transactionID = txnID;
        this.baseDNs = baseDNList;
        if (this.transactionID == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_START_INT_TXN_RESULT_NO_TXN_ID.get());
        }
    }
    
    public StartInteractiveTransactionExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final ASN1OctetString transactionID, final List<String> baseDNs, final Control[] responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, null, encodeValue(transactionID, baseDNs), responseControls);
        this.transactionID = transactionID;
        if (baseDNs == null) {
            this.baseDNs = null;
        }
        else {
            this.baseDNs = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(baseDNs));
        }
    }
    
    private static ASN1OctetString encodeValue(final ASN1OctetString transactionID, final List<String> baseDNs) {
        if (transactionID == null && baseDNs == null) {
            return null;
        }
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(2);
        if (transactionID != null) {
            elements.add(new ASN1OctetString((byte)(-128), transactionID.getValue()));
        }
        if (baseDNs != null && !baseDNs.isEmpty()) {
            final ArrayList<ASN1Element> baseDNElements = new ArrayList<ASN1Element>(baseDNs.size());
            for (final String s : baseDNs) {
                baseDNElements.add(new ASN1OctetString(s));
            }
            elements.add(new ASN1Sequence((byte)(-95), baseDNElements));
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public ASN1OctetString getTransactionID() {
        return this.transactionID;
    }
    
    public List<String> getBaseDNs() {
        return this.baseDNs;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_START_INTERACTIVE_TXN.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("StartInteractiveTransactionExtendedResult(resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
        }
        if (this.transactionID != null) {
            buffer.append(", transactionID='");
            buffer.append(this.transactionID.stringValue());
            buffer.append('\'');
        }
        if (this.baseDNs != null) {
            buffer.append(", baseDNs={");
            for (int i = 0; i < this.baseDNs.size(); ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(this.baseDNs.get(i));
                buffer.append('\'');
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
            for (int j = 0; j < referralURLs.length; ++j) {
                if (j > 0) {
                    buffer.append(", ");
                }
                buffer.append('\'');
                buffer.append(referralURLs[j]);
                buffer.append('\'');
            }
            buffer.append('}');
        }
        final Control[] responseControls = this.getResponseControls();
        if (responseControls.length > 0) {
            buffer.append(", responseControls={");
            for (int k = 0; k < responseControls.length; ++k) {
                if (k > 0) {
                    buffer.append(", ");
                }
                buffer.append(responseControls[k]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
