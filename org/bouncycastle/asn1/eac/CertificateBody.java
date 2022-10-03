package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1ApplicationSpecific;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;

public class CertificateBody extends ASN1Object
{
    ASN1InputStream seq;
    private DERApplicationSpecific certificateProfileIdentifier;
    private DERApplicationSpecific certificationAuthorityReference;
    private PublicKeyDataObject publicKey;
    private DERApplicationSpecific certificateHolderReference;
    private CertificateHolderAuthorization certificateHolderAuthorization;
    private DERApplicationSpecific certificateEffectiveDate;
    private DERApplicationSpecific certificateExpirationDate;
    private int certificateType;
    private static final int CPI = 1;
    private static final int CAR = 2;
    private static final int PK = 4;
    private static final int CHR = 8;
    private static final int CHA = 16;
    private static final int CEfD = 32;
    private static final int CExD = 64;
    public static final int profileType = 127;
    public static final int requestType = 13;
    
    private void setIso7816CertificateBody(final ASN1ApplicationSpecific asn1ApplicationSpecific) throws IOException {
        if (asn1ApplicationSpecific.getApplicationTag() == 78) {
            final ASN1InputStream asn1InputStream = new ASN1InputStream(asn1ApplicationSpecific.getContents());
            ASN1Primitive object;
            while ((object = asn1InputStream.readObject()) != null) {
                if (!(object instanceof DERApplicationSpecific)) {
                    throw new IOException("Not a valid iso7816 content : not a DERApplicationSpecific Object :" + EACTags.encodeTag(asn1ApplicationSpecific) + ((DERApplicationSpecific)object).getClass());
                }
                final DERApplicationSpecific certificateExpirationDate = (DERApplicationSpecific)object;
                switch (certificateExpirationDate.getApplicationTag()) {
                    case 41: {
                        this.setCertificateProfileIdentifier(certificateExpirationDate);
                        continue;
                    }
                    case 2: {
                        this.setCertificationAuthorityReference(certificateExpirationDate);
                        continue;
                    }
                    case 73: {
                        this.setPublicKey(PublicKeyDataObject.getInstance(certificateExpirationDate.getObject(16)));
                        continue;
                    }
                    case 32: {
                        this.setCertificateHolderReference(certificateExpirationDate);
                        continue;
                    }
                    case 76: {
                        this.setCertificateHolderAuthorization(new CertificateHolderAuthorization(certificateExpirationDate));
                        continue;
                    }
                    case 37: {
                        this.setCertificateEffectiveDate(certificateExpirationDate);
                        continue;
                    }
                    case 36: {
                        this.setCertificateExpirationDate(certificateExpirationDate);
                        continue;
                    }
                    default: {
                        this.certificateType = 0;
                        throw new IOException("Not a valid iso7816 DERApplicationSpecific tag " + certificateExpirationDate.getApplicationTag());
                    }
                }
            }
            asn1InputStream.close();
            return;
        }
        throw new IOException("Bad tag : not an iso7816 CERTIFICATE_CONTENT_TEMPLATE");
    }
    
    public CertificateBody(final DERApplicationSpecific certificateProfileIdentifier, final CertificationAuthorityReference certificationAuthorityReference, final PublicKeyDataObject publicKey, final CertificateHolderReference certificateHolderReference, final CertificateHolderAuthorization certificateHolderAuthorization, final PackedDate packedDate, final PackedDate packedDate2) {
        this.certificateType = 0;
        this.setCertificateProfileIdentifier(certificateProfileIdentifier);
        this.setCertificationAuthorityReference(new DERApplicationSpecific(2, certificationAuthorityReference.getEncoded()));
        this.setPublicKey(publicKey);
        this.setCertificateHolderReference(new DERApplicationSpecific(32, certificateHolderReference.getEncoded()));
        this.setCertificateHolderAuthorization(certificateHolderAuthorization);
        try {
            this.setCertificateEffectiveDate(new DERApplicationSpecific(false, 37, new DEROctetString(packedDate.getEncoding())));
            this.setCertificateExpirationDate(new DERApplicationSpecific(false, 36, new DEROctetString(packedDate2.getEncoding())));
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("unable to encode dates: " + ex.getMessage());
        }
    }
    
    private CertificateBody(final ASN1ApplicationSpecific iso7816CertificateBody) throws IOException {
        this.certificateType = 0;
        this.setIso7816CertificateBody(iso7816CertificateBody);
    }
    
    private ASN1Primitive profileToASN1Object() throws IOException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.certificateProfileIdentifier);
        asn1EncodableVector.add(this.certificationAuthorityReference);
        asn1EncodableVector.add(new DERApplicationSpecific(false, 73, this.publicKey));
        asn1EncodableVector.add(this.certificateHolderReference);
        asn1EncodableVector.add(this.certificateHolderAuthorization);
        asn1EncodableVector.add(this.certificateEffectiveDate);
        asn1EncodableVector.add(this.certificateExpirationDate);
        return new DERApplicationSpecific(78, asn1EncodableVector);
    }
    
    private void setCertificateProfileIdentifier(final DERApplicationSpecific certificateProfileIdentifier) throws IllegalArgumentException {
        if (certificateProfileIdentifier.getApplicationTag() == 41) {
            this.certificateProfileIdentifier = certificateProfileIdentifier;
            this.certificateType |= 0x1;
            return;
        }
        throw new IllegalArgumentException("Not an Iso7816Tags.INTERCHANGE_PROFILE tag :" + EACTags.encodeTag(certificateProfileIdentifier));
    }
    
    private void setCertificateHolderReference(final DERApplicationSpecific certificateHolderReference) throws IllegalArgumentException {
        if (certificateHolderReference.getApplicationTag() == 32) {
            this.certificateHolderReference = certificateHolderReference;
            this.certificateType |= 0x8;
            return;
        }
        throw new IllegalArgumentException("Not an Iso7816Tags.CARDHOLDER_NAME tag");
    }
    
    private void setCertificationAuthorityReference(final DERApplicationSpecific certificationAuthorityReference) throws IllegalArgumentException {
        if (certificationAuthorityReference.getApplicationTag() == 2) {
            this.certificationAuthorityReference = certificationAuthorityReference;
            this.certificateType |= 0x2;
            return;
        }
        throw new IllegalArgumentException("Not an Iso7816Tags.ISSUER_IDENTIFICATION_NUMBER tag");
    }
    
    private void setPublicKey(final PublicKeyDataObject publicKeyDataObject) {
        this.publicKey = PublicKeyDataObject.getInstance(publicKeyDataObject);
        this.certificateType |= 0x4;
    }
    
    private ASN1Primitive requestToASN1Object() throws IOException {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.certificateProfileIdentifier);
        asn1EncodableVector.add(new DERApplicationSpecific(false, 73, this.publicKey));
        asn1EncodableVector.add(this.certificateHolderReference);
        return new DERApplicationSpecific(78, asn1EncodableVector);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        try {
            if (this.certificateType == 127) {
                return this.profileToASN1Object();
            }
            if (this.certificateType == 13) {
                return this.requestToASN1Object();
            }
        }
        catch (final IOException ex) {
            return null;
        }
        return null;
    }
    
    public int getCertificateType() {
        return this.certificateType;
    }
    
    public static CertificateBody getInstance(final Object o) throws IOException {
        if (o instanceof CertificateBody) {
            return (CertificateBody)o;
        }
        if (o != null) {
            return new CertificateBody(ASN1ApplicationSpecific.getInstance(o));
        }
        return null;
    }
    
    public PackedDate getCertificateEffectiveDate() {
        if ((this.certificateType & 0x20) == 0x20) {
            return new PackedDate(this.certificateEffectiveDate.getContents());
        }
        return null;
    }
    
    private void setCertificateEffectiveDate(final DERApplicationSpecific certificateEffectiveDate) throws IllegalArgumentException {
        if (certificateEffectiveDate.getApplicationTag() == 37) {
            this.certificateEffectiveDate = certificateEffectiveDate;
            this.certificateType |= 0x20;
            return;
        }
        throw new IllegalArgumentException("Not an Iso7816Tags.APPLICATION_EFFECTIVE_DATE tag :" + EACTags.encodeTag(certificateEffectiveDate));
    }
    
    public PackedDate getCertificateExpirationDate() throws IOException {
        if ((this.certificateType & 0x40) == 0x40) {
            return new PackedDate(this.certificateExpirationDate.getContents());
        }
        throw new IOException("certificate Expiration Date not set");
    }
    
    private void setCertificateExpirationDate(final DERApplicationSpecific certificateExpirationDate) throws IllegalArgumentException {
        if (certificateExpirationDate.getApplicationTag() == 36) {
            this.certificateExpirationDate = certificateExpirationDate;
            this.certificateType |= 0x40;
            return;
        }
        throw new IllegalArgumentException("Not an Iso7816Tags.APPLICATION_EXPIRATION_DATE tag");
    }
    
    public CertificateHolderAuthorization getCertificateHolderAuthorization() throws IOException {
        if ((this.certificateType & 0x10) == 0x10) {
            return this.certificateHolderAuthorization;
        }
        throw new IOException("Certificate Holder Authorisation not set");
    }
    
    private void setCertificateHolderAuthorization(final CertificateHolderAuthorization certificateHolderAuthorization) {
        this.certificateHolderAuthorization = certificateHolderAuthorization;
        this.certificateType |= 0x10;
    }
    
    public CertificateHolderReference getCertificateHolderReference() {
        return new CertificateHolderReference(this.certificateHolderReference.getContents());
    }
    
    public DERApplicationSpecific getCertificateProfileIdentifier() {
        return this.certificateProfileIdentifier;
    }
    
    public CertificationAuthorityReference getCertificationAuthorityReference() throws IOException {
        if ((this.certificateType & 0x2) == 0x2) {
            return new CertificationAuthorityReference(this.certificationAuthorityReference.getContents());
        }
        throw new IOException("Certification authority reference not set");
    }
    
    public PublicKeyDataObject getPublicKey() {
        return this.publicKey;
    }
}
