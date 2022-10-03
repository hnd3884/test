package com.unboundid.ldap.sdk.experimental;

import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.OperationType;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DraftChuLDAPLogSchema00ExtendedEntry extends DraftChuLDAPLogSchema00Entry
{
    public static final String ATTR_REQUEST_VALUE = "reqData";
    private static final long serialVersionUID = 3767074068423424660L;
    private final ASN1OctetString requestValue;
    private final String requestOID;
    
    public DraftChuLDAPLogSchema00ExtendedEntry(final Entry entry) throws LDAPException {
        super(entry, OperationType.EXTENDED);
        final String requestType = entry.getAttributeValue("reqType");
        final String lowerRequestType = StaticUtils.toLowerCase(requestType);
        if (lowerRequestType.startsWith("extended") && lowerRequestType.length() > 8) {
            this.requestOID = requestType.substring(8);
            final byte[] requestValueBytes = entry.getAttributeValueBytes("reqData");
            if (requestValueBytes == null) {
                this.requestValue = null;
            }
            else {
                this.requestValue = new ASN1OctetString((byte)(-117), requestValueBytes);
            }
            return;
        }
        throw new LDAPException(ResultCode.DECODING_ERROR, ExperimentalMessages.ERR_LOGSCHEMA_DECODE_EXTENDED_MALFORMED_REQ_TYPE.get(entry.getDN(), "reqType", requestType));
    }
    
    public String getRequestOID() {
        return this.requestOID;
    }
    
    public ASN1OctetString getRequestValue() {
        return this.requestValue;
    }
    
    public ExtendedRequest toExtendedRequest() {
        return new ExtendedRequest(this.requestOID, this.requestValue, this.getRequestControlArray());
    }
}
