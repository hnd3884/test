package org.bouncycastle.eac;

import java.io.OutputStream;
import org.bouncycastle.asn1.eac.CVCertificate;
import org.bouncycastle.eac.operator.EACSigner;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.eac.CertificateBody;
import org.bouncycastle.asn1.eac.CertificationAuthorityReference;
import org.bouncycastle.asn1.eac.CertificateHolderReference;
import org.bouncycastle.asn1.eac.PackedDate;
import org.bouncycastle.asn1.eac.CertificateHolderAuthorization;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;

public class EACCertificateBuilder
{
    private static final byte[] ZeroArray;
    private PublicKeyDataObject publicKey;
    private CertificateHolderAuthorization certificateHolderAuthorization;
    private PackedDate certificateEffectiveDate;
    private PackedDate certificateExpirationDate;
    private CertificateHolderReference certificateHolderReference;
    private CertificationAuthorityReference certificationAuthorityReference;
    
    public EACCertificateBuilder(final CertificationAuthorityReference certificationAuthorityReference, final PublicKeyDataObject publicKey, final CertificateHolderReference certificateHolderReference, final CertificateHolderAuthorization certificateHolderAuthorization, final PackedDate certificateEffectiveDate, final PackedDate certificateExpirationDate) {
        this.certificationAuthorityReference = certificationAuthorityReference;
        this.publicKey = publicKey;
        this.certificateHolderReference = certificateHolderReference;
        this.certificateHolderAuthorization = certificateHolderAuthorization;
        this.certificateEffectiveDate = certificateEffectiveDate;
        this.certificateExpirationDate = certificateExpirationDate;
    }
    
    private CertificateBody buildBody() {
        return new CertificateBody(new DERApplicationSpecific(41, EACCertificateBuilder.ZeroArray), this.certificationAuthorityReference, this.publicKey, this.certificateHolderReference, this.certificateHolderAuthorization, this.certificateEffectiveDate, this.certificateExpirationDate);
    }
    
    public EACCertificateHolder build(final EACSigner eacSigner) throws EACException {
        try {
            final CertificateBody buildBody = this.buildBody();
            final OutputStream outputStream = eacSigner.getOutputStream();
            outputStream.write(buildBody.getEncoded("DER"));
            outputStream.close();
            return new EACCertificateHolder(new CVCertificate(buildBody, eacSigner.getSignature()));
        }
        catch (final Exception ex) {
            throw new EACException("unable to process signature: " + ex.getMessage(), ex);
        }
    }
    
    static {
        ZeroArray = new byte[] { 0 };
    }
}
