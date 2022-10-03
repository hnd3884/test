package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Null;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class BeginningOfChangelogStartingPoint extends ChangelogBatchStartingPoint
{
    static final byte TYPE = -126;
    private static final ASN1Null ENCODED_ELEMENT;
    private static final long serialVersionUID = 8048301335476843820L;
    
    @Override
    public ASN1Element encode() {
        return BeginningOfChangelogStartingPoint.ENCODED_ELEMENT;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("BeginningOfChangelogStartingPoint()");
    }
    
    static {
        ENCODED_ELEMENT = new ASN1Null((byte)(-126));
    }
}
