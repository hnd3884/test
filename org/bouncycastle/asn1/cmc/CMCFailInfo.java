package org.bouncycastle.asn1.cmc;

import java.util.HashMap;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Integer;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Object;

public class CMCFailInfo extends ASN1Object
{
    public static final CMCFailInfo badAlg;
    public static final CMCFailInfo badMessageCheck;
    public static final CMCFailInfo badRequest;
    public static final CMCFailInfo badTime;
    public static final CMCFailInfo badCertId;
    public static final CMCFailInfo unsupportedExt;
    public static final CMCFailInfo mustArchiveKeys;
    public static final CMCFailInfo badIdentity;
    public static final CMCFailInfo popRequired;
    public static final CMCFailInfo popFailed;
    public static final CMCFailInfo noKeyReuse;
    public static final CMCFailInfo internalCAError;
    public static final CMCFailInfo tryLater;
    public static final CMCFailInfo authDataFail;
    private static Map range;
    private final ASN1Integer value;
    
    private CMCFailInfo(final ASN1Integer value) {
        this.value = value;
    }
    
    public static CMCFailInfo getInstance(final Object o) {
        if (o instanceof CMCFailInfo) {
            return (CMCFailInfo)o;
        }
        if (o == null) {
            return null;
        }
        final CMCFailInfo cmcFailInfo = CMCFailInfo.range.get(ASN1Integer.getInstance(o));
        if (cmcFailInfo != null) {
            return cmcFailInfo;
        }
        throw new IllegalArgumentException("unknown object in getInstance(): " + o.getClass().getName());
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.value;
    }
    
    static {
        badAlg = new CMCFailInfo(new ASN1Integer(0L));
        badMessageCheck = new CMCFailInfo(new ASN1Integer(1L));
        badRequest = new CMCFailInfo(new ASN1Integer(2L));
        badTime = new CMCFailInfo(new ASN1Integer(3L));
        badCertId = new CMCFailInfo(new ASN1Integer(4L));
        unsupportedExt = new CMCFailInfo(new ASN1Integer(5L));
        mustArchiveKeys = new CMCFailInfo(new ASN1Integer(6L));
        badIdentity = new CMCFailInfo(new ASN1Integer(7L));
        popRequired = new CMCFailInfo(new ASN1Integer(8L));
        popFailed = new CMCFailInfo(new ASN1Integer(9L));
        noKeyReuse = new CMCFailInfo(new ASN1Integer(10L));
        internalCAError = new CMCFailInfo(new ASN1Integer(11L));
        tryLater = new CMCFailInfo(new ASN1Integer(12L));
        authDataFail = new CMCFailInfo(new ASN1Integer(13L));
        (CMCFailInfo.range = new HashMap()).put(CMCFailInfo.badAlg.value, CMCFailInfo.badAlg);
        CMCFailInfo.range.put(CMCFailInfo.badMessageCheck.value, CMCFailInfo.badMessageCheck);
        CMCFailInfo.range.put(CMCFailInfo.badRequest.value, CMCFailInfo.badRequest);
        CMCFailInfo.range.put(CMCFailInfo.badTime.value, CMCFailInfo.badTime);
        CMCFailInfo.range.put(CMCFailInfo.badCertId.value, CMCFailInfo.badCertId);
        CMCFailInfo.range.put(CMCFailInfo.popRequired.value, CMCFailInfo.popRequired);
        CMCFailInfo.range.put(CMCFailInfo.unsupportedExt.value, CMCFailInfo.unsupportedExt);
        CMCFailInfo.range.put(CMCFailInfo.mustArchiveKeys.value, CMCFailInfo.mustArchiveKeys);
        CMCFailInfo.range.put(CMCFailInfo.badIdentity.value, CMCFailInfo.badIdentity);
        CMCFailInfo.range.put(CMCFailInfo.popRequired.value, CMCFailInfo.popRequired);
        CMCFailInfo.range.put(CMCFailInfo.popFailed.value, CMCFailInfo.popFailed);
        CMCFailInfo.range.put(CMCFailInfo.badCertId.value, CMCFailInfo.badCertId);
        CMCFailInfo.range.put(CMCFailInfo.popRequired.value, CMCFailInfo.popRequired);
        CMCFailInfo.range.put(CMCFailInfo.noKeyReuse.value, CMCFailInfo.noKeyReuse);
        CMCFailInfo.range.put(CMCFailInfo.internalCAError.value, CMCFailInfo.internalCAError);
        CMCFailInfo.range.put(CMCFailInfo.tryLater.value, CMCFailInfo.tryLater);
        CMCFailInfo.range.put(CMCFailInfo.authDataFail.value, CMCFailInfo.authDataFail);
    }
}
