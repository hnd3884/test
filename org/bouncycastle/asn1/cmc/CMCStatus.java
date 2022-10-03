package org.bouncycastle.asn1.cmc;

import java.util.HashMap;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Integer;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Object;

public class CMCStatus extends ASN1Object
{
    public static final CMCStatus success;
    public static final CMCStatus failed;
    public static final CMCStatus pending;
    public static final CMCStatus noSupport;
    public static final CMCStatus confirmRequired;
    public static final CMCStatus popRequired;
    public static final CMCStatus partial;
    private static Map range;
    private final ASN1Integer value;
    
    private CMCStatus(final ASN1Integer value) {
        this.value = value;
    }
    
    public static CMCStatus getInstance(final Object o) {
        if (o instanceof CMCStatus) {
            return (CMCStatus)o;
        }
        if (o == null) {
            return null;
        }
        final CMCStatus cmcStatus = CMCStatus.range.get(ASN1Integer.getInstance(o));
        if (cmcStatus != null) {
            return cmcStatus;
        }
        throw new IllegalArgumentException("unknown object in getInstance(): " + o.getClass().getName());
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.value;
    }
    
    static {
        success = new CMCStatus(new ASN1Integer(0L));
        failed = new CMCStatus(new ASN1Integer(2L));
        pending = new CMCStatus(new ASN1Integer(3L));
        noSupport = new CMCStatus(new ASN1Integer(4L));
        confirmRequired = new CMCStatus(new ASN1Integer(5L));
        popRequired = new CMCStatus(new ASN1Integer(6L));
        partial = new CMCStatus(new ASN1Integer(7L));
        (CMCStatus.range = new HashMap()).put(CMCStatus.success.value, CMCStatus.success);
        CMCStatus.range.put(CMCStatus.failed.value, CMCStatus.failed);
        CMCStatus.range.put(CMCStatus.pending.value, CMCStatus.pending);
        CMCStatus.range.put(CMCStatus.noSupport.value, CMCStatus.noSupport);
        CMCStatus.range.put(CMCStatus.confirmRequired.value, CMCStatus.confirmRequired);
        CMCStatus.range.put(CMCStatus.popRequired.value, CMCStatus.popRequired);
        CMCStatus.range.put(CMCStatus.partial.value, CMCStatus.partial);
    }
}
