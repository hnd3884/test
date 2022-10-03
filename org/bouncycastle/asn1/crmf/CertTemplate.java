package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CertTemplate extends ASN1Object
{
    private ASN1Sequence seq;
    private ASN1Integer version;
    private ASN1Integer serialNumber;
    private AlgorithmIdentifier signingAlg;
    private X500Name issuer;
    private OptionalValidity validity;
    private X500Name subject;
    private SubjectPublicKeyInfo publicKey;
    private DERBitString issuerUID;
    private DERBitString subjectUID;
    private Extensions extensions;
    
    private CertTemplate(final ASN1Sequence seq) {
        this.seq = seq;
        final Enumeration objects = seq.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject asn1TaggedObject = objects.nextElement();
            switch (asn1TaggedObject.getTagNo()) {
                case 0: {
                    this.version = ASN1Integer.getInstance(asn1TaggedObject, false);
                    continue;
                }
                case 1: {
                    this.serialNumber = ASN1Integer.getInstance(asn1TaggedObject, false);
                    continue;
                }
                case 2: {
                    this.signingAlg = AlgorithmIdentifier.getInstance(asn1TaggedObject, false);
                    continue;
                }
                case 3: {
                    this.issuer = X500Name.getInstance(asn1TaggedObject, true);
                    continue;
                }
                case 4: {
                    this.validity = OptionalValidity.getInstance(ASN1Sequence.getInstance(asn1TaggedObject, false));
                    continue;
                }
                case 5: {
                    this.subject = X500Name.getInstance(asn1TaggedObject, true);
                    continue;
                }
                case 6: {
                    this.publicKey = SubjectPublicKeyInfo.getInstance(asn1TaggedObject, false);
                    continue;
                }
                case 7: {
                    this.issuerUID = DERBitString.getInstance(asn1TaggedObject, false);
                    continue;
                }
                case 8: {
                    this.subjectUID = DERBitString.getInstance(asn1TaggedObject, false);
                    continue;
                }
                case 9: {
                    this.extensions = Extensions.getInstance(asn1TaggedObject, false);
                    continue;
                }
                default: {
                    throw new IllegalArgumentException("unknown tag: " + asn1TaggedObject.getTagNo());
                }
            }
        }
    }
    
    public static CertTemplate getInstance(final Object o) {
        if (o instanceof CertTemplate) {
            return (CertTemplate)o;
        }
        if (o != null) {
            return new CertTemplate(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public int getVersion() {
        return this.version.getValue().intValue();
    }
    
    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }
    
    public AlgorithmIdentifier getSigningAlg() {
        return this.signingAlg;
    }
    
    public X500Name getIssuer() {
        return this.issuer;
    }
    
    public OptionalValidity getValidity() {
        return this.validity;
    }
    
    public X500Name getSubject() {
        return this.subject;
    }
    
    public SubjectPublicKeyInfo getPublicKey() {
        return this.publicKey;
    }
    
    public DERBitString getIssuerUID() {
        return this.issuerUID;
    }
    
    public DERBitString getSubjectUID() {
        return this.subjectUID;
    }
    
    public Extensions getExtensions() {
        return this.extensions;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}
