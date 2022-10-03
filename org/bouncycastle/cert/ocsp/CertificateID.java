package org.bouncycastle.cert.ocsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import java.math.BigInteger;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CertificateID
{
    public static final AlgorithmIdentifier HASH_SHA1;
    private final CertID id;
    
    public CertificateID(final CertID id) {
        if (id == null) {
            throw new IllegalArgumentException("'id' cannot be null");
        }
        this.id = id;
    }
    
    public CertificateID(final DigestCalculator digestCalculator, final X509CertificateHolder x509CertificateHolder, final BigInteger bigInteger) throws OCSPException {
        this.id = createCertID(digestCalculator, x509CertificateHolder, new ASN1Integer(bigInteger));
    }
    
    public ASN1ObjectIdentifier getHashAlgOID() {
        return this.id.getHashAlgorithm().getAlgorithm();
    }
    
    public byte[] getIssuerNameHash() {
        return this.id.getIssuerNameHash().getOctets();
    }
    
    public byte[] getIssuerKeyHash() {
        return this.id.getIssuerKeyHash().getOctets();
    }
    
    public BigInteger getSerialNumber() {
        return this.id.getSerialNumber().getValue();
    }
    
    public boolean matchesIssuer(final X509CertificateHolder x509CertificateHolder, final DigestCalculatorProvider digestCalculatorProvider) throws OCSPException {
        try {
            return createCertID(digestCalculatorProvider.get(this.id.getHashAlgorithm()), x509CertificateHolder, this.id.getSerialNumber()).equals((Object)this.id);
        }
        catch (final OperatorCreationException ex) {
            throw new OCSPException("unable to create digest calculator: " + ex.getMessage(), ex);
        }
    }
    
    public CertID toASN1Primitive() {
        return this.id;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof CertificateID && this.id.toASN1Primitive().equals((Object)((CertificateID)o).id.toASN1Primitive());
    }
    
    @Override
    public int hashCode() {
        return this.id.toASN1Primitive().hashCode();
    }
    
    public static CertificateID deriveCertificateID(final CertificateID certificateID, final BigInteger bigInteger) {
        return new CertificateID(new CertID(certificateID.id.getHashAlgorithm(), certificateID.id.getIssuerNameHash(), certificateID.id.getIssuerKeyHash(), new ASN1Integer(bigInteger)));
    }
    
    private static CertID createCertID(final DigestCalculator digestCalculator, final X509CertificateHolder x509CertificateHolder, final ASN1Integer asn1Integer) throws OCSPException {
        try {
            final OutputStream outputStream = digestCalculator.getOutputStream();
            outputStream.write(x509CertificateHolder.toASN1Structure().getSubject().getEncoded("DER"));
            outputStream.close();
            final DEROctetString derOctetString = new DEROctetString(digestCalculator.getDigest());
            final SubjectPublicKeyInfo subjectPublicKeyInfo = x509CertificateHolder.getSubjectPublicKeyInfo();
            final OutputStream outputStream2 = digestCalculator.getOutputStream();
            outputStream2.write(subjectPublicKeyInfo.getPublicKeyData().getBytes());
            outputStream2.close();
            return new CertID(digestCalculator.getAlgorithmIdentifier(), (ASN1OctetString)derOctetString, (ASN1OctetString)new DEROctetString(digestCalculator.getDigest()), asn1Integer);
        }
        catch (final Exception ex) {
            throw new OCSPException("problem creating ID: " + ex, ex);
        }
    }
    
    static {
        HASH_SHA1 = new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE);
    }
}
