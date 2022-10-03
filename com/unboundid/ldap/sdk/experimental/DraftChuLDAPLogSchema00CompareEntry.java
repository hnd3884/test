package com.unboundid.ldap.sdk.experimental;

import com.unboundid.ldap.sdk.CompareRequest;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DraftChuLDAPLogSchema00CompareEntry extends DraftChuLDAPLogSchema00Entry
{
    public static final String ATTR_ENCODED_ASSERTION = "reqAssertion";
    private static final long serialVersionUID = 7968358177150902271L;
    private final ASN1OctetString assertionValue;
    private final String attributeName;
    
    public DraftChuLDAPLogSchema00CompareEntry(final Entry entry) throws LDAPException {
        super(entry, OperationType.COMPARE);
        final byte[] avaBytes = entry.getAttributeValueBytes("reqAssertion");
        if (avaBytes == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_MISSING_REQUIRED_ATTR.get(entry.getDN(), "reqAssertion"));
        }
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(avaBytes).elements();
            this.attributeName = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            this.assertionValue = ASN1OctetString.decodeAsOctetString(elements[1]);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_COMPARE_AVA_ERROR.get(entry.getDN(), "reqAssertion"), e);
        }
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public String getAssertionValueString() {
        return this.assertionValue.stringValue();
    }
    
    public byte[] getAssertionValueBytes() {
        return this.assertionValue.getValue();
    }
    
    public CompareRequest toCompareRequest() {
        return new CompareRequest(this.getTargetEntryDN(), this.attributeName, this.assertionValue.getValue(), this.getRequestControlArray());
    }
}
