package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.util.Base64;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ResumeWithTokenStartingPoint extends ChangelogBatchStartingPoint
{
    static final byte TYPE = Byte.MIN_VALUE;
    private static final long serialVersionUID = -101217605840282165L;
    private final ASN1OctetString resumeToken;
    
    public ResumeWithTokenStartingPoint(final ASN1OctetString resumeToken) {
        Validator.ensureNotNull(resumeToken);
        this.resumeToken = resumeToken;
    }
    
    public ASN1OctetString getResumeToken() {
        return this.resumeToken;
    }
    
    @Override
    public ASN1Element encode() {
        return new ASN1OctetString((byte)(-128), this.resumeToken.getValue());
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ResumeWithTokenStartingPoint(token='");
        Base64.encode(this.resumeToken.getValue(), buffer);
        buffer.append("')");
    }
}
