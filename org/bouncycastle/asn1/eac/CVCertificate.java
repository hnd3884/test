package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1ApplicationSpecific;
import org.bouncycastle.asn1.ASN1Object;

public class CVCertificate extends ASN1Object
{
    private CertificateBody certificateBody;
    private byte[] signature;
    private int valid;
    private static int bodyValid;
    private static int signValid;
    
    private void setPrivateData(final ASN1ApplicationSpecific asn1ApplicationSpecific) throws IOException {
        this.valid = 0;
        if (asn1ApplicationSpecific.getApplicationTag() != 33) {
            throw new IOException("not a CARDHOLDER_CERTIFICATE :" + asn1ApplicationSpecific.getApplicationTag());
        }
        final ASN1InputStream asn1InputStream = new ASN1InputStream(asn1ApplicationSpecific.getContents());
        ASN1Primitive object;
        while ((object = asn1InputStream.readObject()) != null) {
            if (!(object instanceof DERApplicationSpecific)) {
                throw new IOException("Invalid Object, not an Iso7816CertificateStructure");
            }
            final DERApplicationSpecific derApplicationSpecific = (DERApplicationSpecific)object;
            switch (derApplicationSpecific.getApplicationTag()) {
                case 78: {
                    this.certificateBody = CertificateBody.getInstance(derApplicationSpecific);
                    this.valid |= CVCertificate.bodyValid;
                    continue;
                }
                case 55: {
                    this.signature = derApplicationSpecific.getContents();
                    this.valid |= CVCertificate.signValid;
                    continue;
                }
                default: {
                    throw new IOException("Invalid tag, not an Iso7816CertificateStructure :" + derApplicationSpecific.getApplicationTag());
                }
            }
        }
        asn1InputStream.close();
        if (this.valid != (CVCertificate.signValid | CVCertificate.bodyValid)) {
            throw new IOException("invalid CARDHOLDER_CERTIFICATE :" + asn1ApplicationSpecific.getApplicationTag());
        }
    }
    
    public CVCertificate(final ASN1InputStream asn1InputStream) throws IOException {
        this.initFrom(asn1InputStream);
    }
    
    private void initFrom(final ASN1InputStream asn1InputStream) throws IOException {
        ASN1Primitive object;
        while ((object = asn1InputStream.readObject()) != null) {
            if (!(object instanceof DERApplicationSpecific)) {
                throw new IOException("Invalid Input Stream for creating an Iso7816CertificateStructure");
            }
            this.setPrivateData((ASN1ApplicationSpecific)object);
        }
    }
    
    private CVCertificate(final ASN1ApplicationSpecific privateData) throws IOException {
        this.setPrivateData(privateData);
    }
    
    public CVCertificate(final CertificateBody certificateBody, final byte[] array) throws IOException {
        this.certificateBody = certificateBody;
        this.signature = Arrays.clone(array);
        this.valid |= CVCertificate.bodyValid;
        this.valid |= CVCertificate.signValid;
    }
    
    public static CVCertificate getInstance(final Object o) {
        if (o instanceof CVCertificate) {
            return (CVCertificate)o;
        }
        if (o != null) {
            try {
                return new CVCertificate(ASN1ApplicationSpecific.getInstance(o));
            }
            catch (final IOException ex) {
                throw new ASN1ParsingException("unable to parse data: " + ex.getMessage(), ex);
            }
        }
        return null;
    }
    
    public byte[] getSignature() {
        return Arrays.clone(this.signature);
    }
    
    public CertificateBody getBody() {
        return this.certificateBody;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.certificateBody);
        try {
            asn1EncodableVector.add(new DERApplicationSpecific(false, 55, new DEROctetString(this.signature)));
        }
        catch (final IOException ex) {
            throw new IllegalStateException("unable to convert signature!");
        }
        return new DERApplicationSpecific(33, asn1EncodableVector);
    }
    
    public ASN1ObjectIdentifier getHolderAuthorization() throws IOException {
        return this.certificateBody.getCertificateHolderAuthorization().getOid();
    }
    
    public PackedDate getEffectiveDate() throws IOException {
        return this.certificateBody.getCertificateEffectiveDate();
    }
    
    public int getCertificateType() {
        return this.certificateBody.getCertificateType();
    }
    
    public PackedDate getExpirationDate() throws IOException {
        return this.certificateBody.getCertificateExpirationDate();
    }
    
    public int getRole() throws IOException {
        return this.certificateBody.getCertificateHolderAuthorization().getAccessRights();
    }
    
    public CertificationAuthorityReference getAuthorityReference() throws IOException {
        return this.certificateBody.getCertificationAuthorityReference();
    }
    
    public CertificateHolderReference getHolderReference() throws IOException {
        return this.certificateBody.getCertificateHolderReference();
    }
    
    public int getHolderAuthorizationRole() throws IOException {
        return this.certificateBody.getCertificateHolderAuthorization().getAccessRights() & 0xC0;
    }
    
    public Flags getHolderAuthorizationRights() throws IOException {
        return new Flags(this.certificateBody.getCertificateHolderAuthorization().getAccessRights() & 0x1F);
    }
    
    static {
        CVCertificate.bodyValid = 1;
        CVCertificate.signValid = 2;
    }
}
