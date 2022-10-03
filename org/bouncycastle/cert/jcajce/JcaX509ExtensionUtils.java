package org.bouncycastle.cert.jcajce;

import java.io.OutputStream;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x500.X500Name;
import java.math.BigInteger;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import java.security.cert.X509Certificate;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.operator.DigestCalculator;
import java.security.MessageDigest;
import org.bouncycastle.cert.X509ExtensionUtils;

public class JcaX509ExtensionUtils extends X509ExtensionUtils
{
    public JcaX509ExtensionUtils() throws NoSuchAlgorithmException {
        super(new SHA1DigestCalculator(MessageDigest.getInstance("SHA1")));
    }
    
    public JcaX509ExtensionUtils(final DigestCalculator digestCalculator) {
        super(digestCalculator);
    }
    
    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(final X509Certificate x509Certificate) throws CertificateEncodingException {
        return super.createAuthorityKeyIdentifier(new JcaX509CertificateHolder(x509Certificate));
    }
    
    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(final PublicKey publicKey) {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }
    
    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(final PublicKey publicKey, final X500Principal x500Principal, final BigInteger bigInteger) {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()), new GeneralNames(new GeneralName(X500Name.getInstance((Object)x500Principal.getEncoded()))), bigInteger);
    }
    
    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(final PublicKey publicKey, final GeneralNames generalNames, final BigInteger bigInteger) {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()), generalNames, bigInteger);
    }
    
    public SubjectKeyIdentifier createSubjectKeyIdentifier(final PublicKey publicKey) {
        return super.createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }
    
    public SubjectKeyIdentifier createTruncatedSubjectKeyIdentifier(final PublicKey publicKey) {
        return super.createTruncatedSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()));
    }
    
    public static ASN1Primitive parseExtensionValue(final byte[] array) throws IOException {
        return ASN1Primitive.fromByteArray(ASN1OctetString.getInstance((Object)array).getOctets());
    }
    
    private static class SHA1DigestCalculator implements DigestCalculator
    {
        private ByteArrayOutputStream bOut;
        private MessageDigest digest;
        
        public SHA1DigestCalculator(final MessageDigest digest) {
            this.bOut = new ByteArrayOutputStream();
            this.digest = digest;
        }
        
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
        }
        
        public OutputStream getOutputStream() {
            return this.bOut;
        }
        
        public byte[] getDigest() {
            final byte[] digest = this.digest.digest(this.bOut.toByteArray());
            this.bOut.reset();
            return digest;
        }
    }
}
