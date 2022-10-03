package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Long;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.ExtendedResult;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GetConnectionIDExtendedResult extends ExtendedResult
{
    private static final long serialVersionUID = -3161975076326146250L;
    private final long connectionID;
    
    public GetConnectionIDExtendedResult(final ExtendedResult extendedResult) throws LDAPException {
        super(extendedResult);
        final ASN1OctetString value = extendedResult.getValue();
        if (value == null) {
            this.connectionID = -1L;
            return;
        }
        try {
            final ASN1Element e = ASN1Element.decode(value.getValue());
            this.connectionID = ASN1Long.decodeAsLong(e).longValue();
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_GET_CONN_ID_RESPONSE_VALUE_NOT_INT.get(), e2);
        }
    }
    
    public GetConnectionIDExtendedResult(final int messageID, final ResultCode resultCode, final String diagnosticMessage, final String matchedDN, final String[] referralURLs, final Long connectionID, final Control[] responseControls) {
        super(messageID, resultCode, diagnosticMessage, matchedDN, referralURLs, null, encodeValue(connectionID), responseControls);
        if (connectionID == null) {
            this.connectionID = -1L;
        }
        else {
            this.connectionID = connectionID;
        }
    }
    
    private static ASN1OctetString encodeValue(final Long connectionID) {
        if (connectionID == null || connectionID < 0L) {
            return null;
        }
        return new ASN1OctetString(new ASN1Long(connectionID).encode());
    }
    
    public long getConnectionID() {
        return this.connectionID;
    }
    
    @Override
    public String getExtendedResultName() {
        return ExtOpMessages.INFO_EXTENDED_RESULT_NAME_GET_CONNECTION_ID.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetConnectionIDExtendedResult(connectionID=");
        buffer.append(this.connectionID);
        buffer.append(", resultCode=");
        buffer.append(this.getResultCode());
        final int messageID = this.getMessageID();
        if (messageID >= 0) {
            buffer.append(", messageID=");
            buffer.append(messageID);
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
        if (referralURLs != null && referralURLs.length > 0) {
            buffer.append(", referralURLs={ '");
            for (int i = 0; i < referralURLs.length; ++i) {
                if (i > 0) {
                    buffer.append("', '");
                }
                buffer.append(referralURLs[i]);
            }
            buffer.append("' }");
        }
        final Control[] controls = this.getResponseControls();
        if (controls.length > 0) {
            buffer.append(", controls={");
            for (int j = 0; j < controls.length; ++j) {
                if (j > 0) {
                    buffer.append(", ");
                }
                buffer.append(controls[j]);
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
}
