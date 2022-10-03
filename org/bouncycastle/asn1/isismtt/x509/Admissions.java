package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1Object;

public class Admissions extends ASN1Object
{
    private GeneralName admissionAuthority;
    private NamingAuthority namingAuthority;
    private ASN1Sequence professionInfos;
    
    public static Admissions getInstance(final Object o) {
        if (o == null || o instanceof Admissions) {
            return (Admissions)o;
        }
        if (o instanceof ASN1Sequence) {
            return new Admissions((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    private Admissions(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        final Enumeration objects = asn1Sequence.getObjects();
        ASN1Encodable asn1Encodable = objects.nextElement();
        if (asn1Encodable instanceof ASN1TaggedObject) {
            switch (((ASN1TaggedObject)asn1Encodable).getTagNo()) {
                case 0: {
                    this.admissionAuthority = GeneralName.getInstance((ASN1TaggedObject)asn1Encodable, true);
                    break;
                }
                case 1: {
                    this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject)asn1Encodable, true);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject)asn1Encodable).getTagNo());
                }
            }
            asn1Encodable = objects.nextElement();
        }
        if (asn1Encodable instanceof ASN1TaggedObject) {
            switch (((ASN1TaggedObject)asn1Encodable).getTagNo()) {
                case 1: {
                    this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject)asn1Encodable, true);
                    asn1Encodable = objects.nextElement();
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject)asn1Encodable).getTagNo());
                }
            }
        }
        this.professionInfos = ASN1Sequence.getInstance(asn1Encodable);
        if (objects.hasMoreElements()) {
            throw new IllegalArgumentException("Bad object encountered: " + objects.nextElement().getClass());
        }
    }
    
    public Admissions(final GeneralName admissionAuthority, final NamingAuthority namingAuthority, final ProfessionInfo[] array) {
        this.admissionAuthority = admissionAuthority;
        this.namingAuthority = namingAuthority;
        this.professionInfos = new DERSequence(array);
    }
    
    public GeneralName getAdmissionAuthority() {
        return this.admissionAuthority;
    }
    
    public NamingAuthority getNamingAuthority() {
        return this.namingAuthority;
    }
    
    public ProfessionInfo[] getProfessionInfos() {
        final ProfessionInfo[] array = new ProfessionInfo[this.professionInfos.size()];
        int n = 0;
        final Enumeration objects = this.professionInfos.getObjects();
        while (objects.hasMoreElements()) {
            array[n++] = ProfessionInfo.getInstance(objects.nextElement());
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.admissionAuthority != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, this.admissionAuthority));
        }
        if (this.namingAuthority != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 1, this.namingAuthority));
        }
        asn1EncodableVector.add(this.professionInfos);
        return new DERSequence(asn1EncodableVector);
    }
}
