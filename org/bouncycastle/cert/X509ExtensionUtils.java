package org.bouncycastle.cert;

import java.io.OutputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import java.math.BigInteger;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.operator.DigestCalculator;

public class X509ExtensionUtils
{
    private DigestCalculator calculator;
    
    public X509ExtensionUtils(final DigestCalculator calculator) {
        this.calculator = calculator;
    }
    
    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(final X509CertificateHolder x509CertificateHolder) {
        return new AuthorityKeyIdentifier(this.getSubjectKeyIdentifier(x509CertificateHolder), new GeneralNames(new GeneralName(x509CertificateHolder.getIssuer())), x509CertificateHolder.getSerialNumber());
    }
    
    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        return new AuthorityKeyIdentifier(this.calculateIdentifier(subjectPublicKeyInfo));
    }
    
    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(final SubjectPublicKeyInfo subjectPublicKeyInfo, final GeneralNames generalNames, final BigInteger bigInteger) {
        return new AuthorityKeyIdentifier(this.calculateIdentifier(subjectPublicKeyInfo), generalNames, bigInteger);
    }
    
    public SubjectKeyIdentifier createSubjectKeyIdentifier(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        return new SubjectKeyIdentifier(this.calculateIdentifier(subjectPublicKeyInfo));
    }
    
    public SubjectKeyIdentifier createTruncatedSubjectKeyIdentifier(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        final byte[] calculateIdentifier = this.calculateIdentifier(subjectPublicKeyInfo);
        final byte[] array = new byte[8];
        System.arraycopy(calculateIdentifier, calculateIdentifier.length - 8, array, 0, array.length);
        final byte[] array2 = array;
        final int n = 0;
        array2[n] &= 0xF;
        final byte[] array3 = array;
        final int n2 = 0;
        array3[n2] |= 0x40;
        return new SubjectKeyIdentifier(array);
    }
    
    private byte[] getSubjectKeyIdentifier(final X509CertificateHolder x509CertificateHolder) {
        if (x509CertificateHolder.getVersionNumber() != 3) {
            return this.calculateIdentifier(x509CertificateHolder.getSubjectPublicKeyInfo());
        }
        final Extension extension = x509CertificateHolder.getExtension(Extension.subjectKeyIdentifier);
        if (extension != null) {
            return ASN1OctetString.getInstance((Object)extension.getParsedValue()).getOctets();
        }
        return this.calculateIdentifier(x509CertificateHolder.getSubjectPublicKeyInfo());
    }
    
    private byte[] calculateIdentifier(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        final byte[] bytes = subjectPublicKeyInfo.getPublicKeyData().getBytes();
        final OutputStream outputStream = this.calculator.getOutputStream();
        try {
            outputStream.write(bytes);
            outputStream.close();
        }
        catch (final IOException ex) {
            throw new CertRuntimeException("unable to calculate identifier: " + ex.getMessage(), ex);
        }
        return this.calculator.getDigest();
    }
}
