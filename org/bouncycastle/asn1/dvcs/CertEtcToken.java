package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.smime.SMIMECapabilities;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.ocsp.CertStatus;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class CertEtcToken extends ASN1Object implements ASN1Choice
{
    public static final int TAG_CERTIFICATE = 0;
    public static final int TAG_ESSCERTID = 1;
    public static final int TAG_PKISTATUS = 2;
    public static final int TAG_ASSERTION = 3;
    public static final int TAG_CRL = 4;
    public static final int TAG_OCSPCERTSTATUS = 5;
    public static final int TAG_OCSPCERTID = 6;
    public static final int TAG_OCSPRESPONSE = 7;
    public static final int TAG_CAPABILITIES = 8;
    private static final boolean[] explicit;
    private int tagNo;
    private ASN1Encodable value;
    private Extension extension;
    
    public CertEtcToken(final int tagNo, final ASN1Encodable value) {
        this.tagNo = tagNo;
        this.value = value;
    }
    
    public CertEtcToken(final Extension extension) {
        this.tagNo = -1;
        this.extension = extension;
    }
    
    private CertEtcToken(final ASN1TaggedObject asn1TaggedObject) {
        switch (this.tagNo = asn1TaggedObject.getTagNo()) {
            case 0: {
                this.value = Certificate.getInstance(asn1TaggedObject, false);
                break;
            }
            case 1: {
                this.value = ESSCertID.getInstance(asn1TaggedObject.getObject());
                break;
            }
            case 2: {
                this.value = PKIStatusInfo.getInstance(asn1TaggedObject, false);
                break;
            }
            case 3: {
                this.value = ContentInfo.getInstance(asn1TaggedObject.getObject());
                break;
            }
            case 4: {
                this.value = CertificateList.getInstance(asn1TaggedObject, false);
                break;
            }
            case 5: {
                this.value = CertStatus.getInstance(asn1TaggedObject.getObject());
                break;
            }
            case 6: {
                this.value = CertID.getInstance(asn1TaggedObject, false);
                break;
            }
            case 7: {
                this.value = OCSPResponse.getInstance(asn1TaggedObject, false);
                break;
            }
            case 8: {
                this.value = SMIMECapabilities.getInstance(asn1TaggedObject.getObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown tag: " + this.tagNo);
            }
        }
    }
    
    public static CertEtcToken getInstance(final Object o) {
        if (o instanceof CertEtcToken) {
            return (CertEtcToken)o;
        }
        if (o instanceof ASN1TaggedObject) {
            return new CertEtcToken((ASN1TaggedObject)o);
        }
        if (o != null) {
            return new CertEtcToken(Extension.getInstance(o));
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.extension == null) {
            return new DERTaggedObject(CertEtcToken.explicit[this.tagNo], this.tagNo, this.value);
        }
        return this.extension.toASN1Primitive();
    }
    
    public int getTagNo() {
        return this.tagNo;
    }
    
    public ASN1Encodable getValue() {
        return this.value;
    }
    
    public Extension getExtension() {
        return this.extension;
    }
    
    @Override
    public String toString() {
        return "CertEtcToken {\n" + this.value + "}\n";
    }
    
    public static CertEtcToken[] arrayFromSequence(final ASN1Sequence asn1Sequence) {
        final CertEtcToken[] array = new CertEtcToken[asn1Sequence.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = getInstance(asn1Sequence.getObjectAt(i));
        }
        return array;
    }
    
    static {
        explicit = new boolean[] { false, true, false, true, false, true, false, false, true };
    }
}
