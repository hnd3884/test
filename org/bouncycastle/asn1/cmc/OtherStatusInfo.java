package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class OtherStatusInfo extends ASN1Object implements ASN1Choice
{
    private final CMCFailInfo failInfo;
    private final PendInfo pendInfo;
    private final ExtendedFailInfo extendedFailInfo;
    
    public static OtherStatusInfo getInstance(final Object o) {
        if (o instanceof OtherStatusInfo) {
            return (OtherStatusInfo)o;
        }
        if (o instanceof ASN1Encodable) {
            final ASN1Primitive asn1Primitive = ((ASN1Encodable)o).toASN1Primitive();
            if (asn1Primitive instanceof ASN1Integer) {
                return new OtherStatusInfo(CMCFailInfo.getInstance(asn1Primitive));
            }
            if (asn1Primitive instanceof ASN1Sequence) {
                if (((ASN1Sequence)asn1Primitive).getObjectAt(0) instanceof ASN1ObjectIdentifier) {
                    return new OtherStatusInfo(ExtendedFailInfo.getInstance(asn1Primitive));
                }
                return new OtherStatusInfo(PendInfo.getInstance(asn1Primitive));
            }
        }
        else if (o instanceof byte[]) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[])o));
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("parsing error: " + ex.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance(): " + o.getClass().getName());
    }
    
    OtherStatusInfo(final CMCFailInfo cmcFailInfo) {
        this(cmcFailInfo, null, null);
    }
    
    OtherStatusInfo(final PendInfo pendInfo) {
        this(null, pendInfo, null);
    }
    
    OtherStatusInfo(final ExtendedFailInfo extendedFailInfo) {
        this(null, null, extendedFailInfo);
    }
    
    private OtherStatusInfo(final CMCFailInfo failInfo, final PendInfo pendInfo, final ExtendedFailInfo extendedFailInfo) {
        this.failInfo = failInfo;
        this.pendInfo = pendInfo;
        this.extendedFailInfo = extendedFailInfo;
    }
    
    public boolean isPendingInfo() {
        return this.pendInfo != null;
    }
    
    public boolean isFailInfo() {
        return this.failInfo != null;
    }
    
    public boolean isExtendedFailInfo() {
        return this.extendedFailInfo != null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.pendInfo != null) {
            return this.pendInfo.toASN1Primitive();
        }
        if (this.failInfo != null) {
            return this.failInfo.toASN1Primitive();
        }
        return this.extendedFailInfo.toASN1Primitive();
    }
}
