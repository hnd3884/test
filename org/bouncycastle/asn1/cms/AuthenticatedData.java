package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class AuthenticatedData extends ASN1Object
{
    private ASN1Integer version;
    private OriginatorInfo originatorInfo;
    private ASN1Set recipientInfos;
    private AlgorithmIdentifier macAlgorithm;
    private AlgorithmIdentifier digestAlgorithm;
    private ContentInfo encapsulatedContentInfo;
    private ASN1Set authAttrs;
    private ASN1OctetString mac;
    private ASN1Set unauthAttrs;
    
    public AuthenticatedData(final OriginatorInfo originatorInfo, final ASN1Set recipientInfos, final AlgorithmIdentifier macAlgorithm, final AlgorithmIdentifier digestAlgorithm, final ContentInfo encapsulatedContentInfo, final ASN1Set authAttrs, final ASN1OctetString mac, final ASN1Set unauthAttrs) {
        if ((digestAlgorithm != null || authAttrs != null) && (digestAlgorithm == null || authAttrs == null)) {
            throw new IllegalArgumentException("digestAlgorithm and authAttrs must be set together");
        }
        this.version = new ASN1Integer(calculateVersion(originatorInfo));
        this.originatorInfo = originatorInfo;
        this.macAlgorithm = macAlgorithm;
        this.digestAlgorithm = digestAlgorithm;
        this.recipientInfos = recipientInfos;
        this.encapsulatedContentInfo = encapsulatedContentInfo;
        this.authAttrs = authAttrs;
        this.mac = mac;
        this.unauthAttrs = unauthAttrs;
    }
    
    private AuthenticatedData(final ASN1Sequence asn1Sequence) {
        int n = 0;
        this.version = (ASN1Integer)asn1Sequence.getObjectAt(n++);
        ASN1Encodable asn1Encodable = asn1Sequence.getObjectAt(n++);
        if (asn1Encodable instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject)asn1Encodable, false);
            asn1Encodable = asn1Sequence.getObjectAt(n++);
        }
        this.recipientInfos = ASN1Set.getInstance(asn1Encodable);
        this.macAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(n++));
        ASN1Encodable asn1Encodable2 = asn1Sequence.getObjectAt(n++);
        if (asn1Encodable2 instanceof ASN1TaggedObject) {
            this.digestAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject)asn1Encodable2, false);
            asn1Encodable2 = asn1Sequence.getObjectAt(n++);
        }
        this.encapsulatedContentInfo = ContentInfo.getInstance(asn1Encodable2);
        ASN1Encodable asn1Encodable3 = asn1Sequence.getObjectAt(n++);
        if (asn1Encodable3 instanceof ASN1TaggedObject) {
            this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject)asn1Encodable3, false);
            asn1Encodable3 = asn1Sequence.getObjectAt(n++);
        }
        this.mac = ASN1OctetString.getInstance(asn1Encodable3);
        if (asn1Sequence.size() > n) {
            this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(n), false);
        }
    }
    
    public static AuthenticatedData getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static AuthenticatedData getInstance(final Object o) {
        if (o instanceof AuthenticatedData) {
            return (AuthenticatedData)o;
        }
        if (o != null) {
            return new AuthenticatedData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public OriginatorInfo getOriginatorInfo() {
        return this.originatorInfo;
    }
    
    public ASN1Set getRecipientInfos() {
        return this.recipientInfos;
    }
    
    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlgorithm;
    }
    
    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }
    
    public ContentInfo getEncapsulatedContentInfo() {
        return this.encapsulatedContentInfo;
    }
    
    public ASN1Set getAuthAttrs() {
        return this.authAttrs;
    }
    
    public ASN1OctetString getMac() {
        return this.mac;
    }
    
    public ASN1Set getUnauthAttrs() {
        return this.unauthAttrs;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        if (this.originatorInfo != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.originatorInfo));
        }
        asn1EncodableVector.add(this.recipientInfos);
        asn1EncodableVector.add(this.macAlgorithm);
        if (this.digestAlgorithm != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.digestAlgorithm));
        }
        asn1EncodableVector.add(this.encapsulatedContentInfo);
        if (this.authAttrs != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 2, this.authAttrs));
        }
        asn1EncodableVector.add(this.mac);
        if (this.unauthAttrs != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 3, this.unauthAttrs));
        }
        return new BERSequence(asn1EncodableVector);
    }
    
    public static int calculateVersion(final OriginatorInfo originatorInfo) {
        if (originatorInfo == null) {
            return 0;
        }
        int n = 0;
        final Enumeration objects = originatorInfo.getCertificates().getObjects();
        while (objects.hasMoreElements()) {
            final Object nextElement = objects.nextElement();
            if (nextElement instanceof ASN1TaggedObject) {
                final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)nextElement;
                if (asn1TaggedObject.getTagNo() == 2) {
                    n = 1;
                }
                else {
                    if (asn1TaggedObject.getTagNo() == 3) {
                        n = 3;
                        break;
                    }
                    continue;
                }
            }
        }
        if (originatorInfo.getCRLs() != null) {
            final Enumeration objects2 = originatorInfo.getCRLs().getObjects();
            while (objects2.hasMoreElements()) {
                final Object nextElement2 = objects2.nextElement();
                if (nextElement2 instanceof ASN1TaggedObject && ((ASN1TaggedObject)nextElement2).getTagNo() == 1) {
                    n = 3;
                    break;
                }
            }
        }
        return n;
    }
}
