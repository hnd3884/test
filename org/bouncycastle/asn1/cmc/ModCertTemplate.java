package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.ASN1Object;

public class ModCertTemplate extends ASN1Object
{
    private final BodyPartPath pkiDataReference;
    private final BodyPartList certReferences;
    private final boolean replace;
    private final CertTemplate certTemplate;
    
    public ModCertTemplate(final BodyPartPath pkiDataReference, final BodyPartList certReferences, final boolean replace, final CertTemplate certTemplate) {
        this.pkiDataReference = pkiDataReference;
        this.certReferences = certReferences;
        this.replace = replace;
        this.certTemplate = certTemplate;
    }
    
    private ModCertTemplate(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 4 && asn1Sequence.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.pkiDataReference = BodyPartPath.getInstance(asn1Sequence.getObjectAt(0));
        this.certReferences = BodyPartList.getInstance(asn1Sequence.getObjectAt(1));
        if (asn1Sequence.size() == 4) {
            this.replace = ASN1Boolean.getInstance(asn1Sequence.getObjectAt(2)).isTrue();
            this.certTemplate = CertTemplate.getInstance(asn1Sequence.getObjectAt(3));
        }
        else {
            this.replace = true;
            this.certTemplate = CertTemplate.getInstance(asn1Sequence.getObjectAt(2));
        }
    }
    
    public static ModCertTemplate getInstance(final Object o) {
        if (o instanceof ModCertTemplate) {
            return (ModCertTemplate)o;
        }
        if (o != null) {
            return new ModCertTemplate(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public BodyPartPath getPkiDataReference() {
        return this.pkiDataReference;
    }
    
    public BodyPartList getCertReferences() {
        return this.certReferences;
    }
    
    public boolean isReplacingFields() {
        return this.replace;
    }
    
    public CertTemplate getCertTemplate() {
        return this.certTemplate;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.pkiDataReference);
        asn1EncodableVector.add(this.certReferences);
        if (!this.replace) {
            asn1EncodableVector.add(ASN1Boolean.getInstance(this.replace));
        }
        asn1EncodableVector.add(this.certTemplate);
        return new DERSequence(asn1EncodableVector);
    }
}
