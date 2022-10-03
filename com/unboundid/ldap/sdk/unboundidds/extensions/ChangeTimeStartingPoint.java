package com.unboundid.ldap.sdk.unboundidds.extensions;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ChangeTimeStartingPoint extends ChangelogBatchStartingPoint
{
    static final byte TYPE = -124;
    private static final long serialVersionUID = 920153185766534528L;
    private final long changeTime;
    private final String changeTimeString;
    
    public ChangeTimeStartingPoint(final long changeTime) {
        this.changeTime = changeTime;
        this.changeTimeString = StaticUtils.encodeGeneralizedTime(new Date(changeTime));
    }
    
    public long getChangeTime() {
        return this.changeTime;
    }
    
    @Override
    public ASN1Element encode() {
        return new ASN1OctetString((byte)(-124), this.changeTimeString);
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ChangeTimeStartingPoint(time='");
        buffer.append(this.changeTimeString);
        buffer.append("')");
    }
}
