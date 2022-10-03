package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1Object;

public class AdmissionSyntax extends ASN1Object
{
    private GeneralName admissionAuthority;
    private ASN1Sequence contentsOfAdmissions;
    
    public static AdmissionSyntax getInstance(final Object o) {
        if (o == null || o instanceof AdmissionSyntax) {
            return (AdmissionSyntax)o;
        }
        if (o instanceof ASN1Sequence) {
            return new AdmissionSyntax((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + o.getClass().getName());
    }
    
    private AdmissionSyntax(final ASN1Sequence asn1Sequence) {
        switch (asn1Sequence.size()) {
            case 1: {
                this.contentsOfAdmissions = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(0));
                break;
            }
            case 2: {
                this.admissionAuthority = GeneralName.getInstance(asn1Sequence.getObjectAt(0));
                this.contentsOfAdmissions = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(1));
                break;
            }
            default: {
                throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
            }
        }
    }
    
    public AdmissionSyntax(final GeneralName admissionAuthority, final ASN1Sequence contentsOfAdmissions) {
        this.admissionAuthority = admissionAuthority;
        this.contentsOfAdmissions = contentsOfAdmissions;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.admissionAuthority != null) {
            asn1EncodableVector.add(this.admissionAuthority);
        }
        asn1EncodableVector.add(this.contentsOfAdmissions);
        return new DERSequence(asn1EncodableVector);
    }
    
    public GeneralName getAdmissionAuthority() {
        return this.admissionAuthority;
    }
    
    public Admissions[] getContentsOfAdmissions() {
        final Admissions[] array = new Admissions[this.contentsOfAdmissions.size()];
        int n = 0;
        final Enumeration objects = this.contentsOfAdmissions.getObjects();
        while (objects.hasMoreElements()) {
            array[n++] = Admissions.getInstance(objects.nextElement());
        }
        return array;
    }
}
