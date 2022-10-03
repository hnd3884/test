package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ResumeWithCSNStartingPoint extends ChangelogBatchStartingPoint
{
    static final byte TYPE = -127;
    private static final long serialVersionUID = -5205334877324505765L;
    private final String csn;
    
    public ResumeWithCSNStartingPoint(final String csn) {
        Validator.ensureNotNull(csn);
        this.csn = csn;
    }
    
    public String getCSN() {
        return this.csn;
    }
    
    @Override
    public ASN1Element encode() {
        return new ASN1OctetString((byte)(-127), this.csn);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ResumeWithCSNStartingPoint(csn='");
        buffer.append(this.csn);
        buffer.append("')");
    }
}
