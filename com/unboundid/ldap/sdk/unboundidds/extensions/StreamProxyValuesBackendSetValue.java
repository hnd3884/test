package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class StreamProxyValuesBackendSetValue implements Serializable
{
    private static final long serialVersionUID = -799860937140238448L;
    private final ASN1OctetString backendSetID;
    private final ASN1OctetString value;
    
    public StreamProxyValuesBackendSetValue(final ASN1OctetString backendSetID, final ASN1OctetString value) {
        Validator.ensureNotNull(backendSetID, value);
        this.backendSetID = backendSetID;
        this.value = value;
    }
    
    public ASN1OctetString getBackendSetID() {
        return this.backendSetID;
    }
    
    public ASN1OctetString getValue() {
        return this.value;
    }
    
    public ASN1Element encode() {
        return new ASN1Sequence(new ASN1Element[] { this.backendSetID, this.value });
    }
    
    public static StreamProxyValuesBackendSetValue decode(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            return new StreamProxyValuesBackendSetValue(ASN1OctetString.decodeAsOctetString(elements[0]), ASN1OctetString.decodeAsOctetString(elements[1]));
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ExtOpMessages.ERR_STREAM_PROXY_VALUES_BACKEND_SET_VALUE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("StreamProxyValuesBackendSetValue(backendSetID=");
        this.backendSetID.toString(buffer);
        buffer.append(", value=");
        this.value.toString(buffer);
        buffer.append(')');
    }
}
