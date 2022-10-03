package org.bouncycastle.x509.extension;

import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.cert.CertificateParsingException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.X509Extension;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;

public class AuthorityKeyIdentifierStructure extends AuthorityKeyIdentifier
{
    public AuthorityKeyIdentifierStructure(final byte[] array) throws IOException {
        super((ASN1Sequence)X509ExtensionUtil.fromExtensionValue(array));
    }
    
    @Deprecated
    public AuthorityKeyIdentifierStructure(final X509Extension x509Extension) {
        super((ASN1Sequence)x509Extension.getParsedValue());
    }
    
    public AuthorityKeyIdentifierStructure(final Extension extension) {
        super((ASN1Sequence)extension.getParsedValue());
    }
    
    private static ASN1Sequence fromCertificate(final X509Certificate x509Certificate) throws CertificateParsingException {
        try {
            if (x509Certificate.getVersion() != 3) {
                return (ASN1Sequence)new AuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(x509Certificate.getPublicKey().getEncoded()), new GeneralNames(new GeneralName(PrincipalUtil.getIssuerX509Principal(x509Certificate))), x509Certificate.getSerialNumber()).toASN1Primitive();
            }
            final GeneralName generalName = new GeneralName(PrincipalUtil.getIssuerX509Principal(x509Certificate));
            final byte[] extensionValue = x509Certificate.getExtensionValue(Extension.subjectKeyIdentifier.getId());
            if (extensionValue != null) {
                return (ASN1Sequence)new AuthorityKeyIdentifier(((ASN1OctetString)X509ExtensionUtil.fromExtensionValue(extensionValue)).getOctets(), new GeneralNames(generalName), x509Certificate.getSerialNumber()).toASN1Primitive();
            }
            return (ASN1Sequence)new AuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(x509Certificate.getPublicKey().getEncoded()), new GeneralNames(generalName), x509Certificate.getSerialNumber()).toASN1Primitive();
        }
        catch (final Exception ex) {
            throw new CertificateParsingException("Exception extracting certificate details: " + ex.toString());
        }
    }
    
    private static ASN1Sequence fromKey(final PublicKey publicKey) throws InvalidKeyException {
        try {
            return (ASN1Sequence)new AuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded())).toASN1Primitive();
        }
        catch (final Exception ex) {
            throw new InvalidKeyException("can't process key: " + ex);
        }
    }
    
    public AuthorityKeyIdentifierStructure(final X509Certificate x509Certificate) throws CertificateParsingException {
        super(fromCertificate(x509Certificate));
    }
    
    public AuthorityKeyIdentifierStructure(final PublicKey publicKey) throws InvalidKeyException {
        super(fromKey(publicKey));
    }
}
