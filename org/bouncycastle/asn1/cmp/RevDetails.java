package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.ASN1Object;

public class RevDetails extends ASN1Object
{
    private CertTemplate certDetails;
    private Extensions crlEntryDetails;
    
    private RevDetails(final ASN1Sequence asn1Sequence) {
        this.certDetails = CertTemplate.getInstance(asn1Sequence.getObjectAt(0));
        if (asn1Sequence.size() > 1) {
            this.crlEntryDetails = Extensions.getInstance(asn1Sequence.getObjectAt(1));
        }
    }
    
    public static RevDetails getInstance(final Object o) {
        if (o instanceof RevDetails) {
            return (RevDetails)o;
        }
        if (o != null) {
            return new RevDetails(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public RevDetails(final CertTemplate certDetails) {
        this.certDetails = certDetails;
    }
    
    @Deprecated
    public RevDetails(final CertTemplate certDetails, final X509Extensions x509Extensions) {
        this.certDetails = certDetails;
        this.crlEntryDetails = Extensions.getInstance(x509Extensions.toASN1Primitive());
    }
    
    public RevDetails(final CertTemplate certDetails, final Extensions crlEntryDetails) {
        this.certDetails = certDetails;
        this.crlEntryDetails = crlEntryDetails;
    }
    
    public CertTemplate getCertDetails() {
        return this.certDetails;
    }
    
    public Extensions getCrlEntryDetails() {
        return this.crlEntryDetails;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.certDetails);
        if (this.crlEntryDetails != null) {
            asn1EncodableVector.add(this.crlEntryDetails);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
