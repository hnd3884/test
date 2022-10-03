package com.adventnet.sym.server.mdm.certificates.scep;

import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.cert.X509CertificateHolder;
import java.security.PublicKey;
import java.util.Date;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.jscep.util.CertificationRequestUtils;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import java.util.Calendar;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

public class SigningCertificateAuthority
{
    private final Logger logger;
    private final X509Certificate caCertificate;
    private final PrivateKey privateKey;
    
    public SigningCertificateAuthority(final X509Certificate rootCACertificate, final PrivateKey privateKey) {
        this.logger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
        if (rootCACertificate != null && privateKey != null) {
            this.logger.log(Level.INFO, "SigningCertificateAuthority: Creating signing certificate authority : {0}", new Object[] { rootCACertificate.getSubjectDN() });
            this.caCertificate = rootCACertificate;
            this.privateKey = privateKey;
            return;
        }
        this.logger.log(Level.SEVERE, "SigningCertificateAuthority: Invalid caCertificate or private key");
        throw new IllegalArgumentException("SigningCertificateAuthority: Root CA certificate or private key is null");
    }
    
    public List<X509Certificate> getCertificates() {
        return Collections.singletonList(this.caCertificate);
    }
    
    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
    
    public X509Certificate issueCertificate(final PKCS10CertificationRequest certificateSigningRequest, final int validityInYears) throws Exception {
        final X500Name subject = X500Name.getInstance((Object)certificateSigningRequest.getSubject());
        this.logger.log(Level.INFO, "======================= CREATING CLIENT CERTIFICATE FOR SUBJECT: {0} =====================", new Object[] { subject });
        final X500Name issuer = new JcaX509CertificateHolder(this.caCertificate).getSubject();
        this.logger.log(Level.INFO, "SigningCertificateAuthority: Issuer : {0}", new Object[] { issuer });
        final Date notBefore = Calendar.getInstance().getTime();
        final Date notAfter = CertificateUtil.getCertificateValidityEndDate(validityInYears);
        this.logger.log(Level.INFO, "SigningCertificateAuthority: Issuing certificate for : {0} validity: {1} from {2} to {3}", new Object[] { subject, validityInYears, notBefore, notAfter });
        final PublicKey clientPublicKey = CertificationRequestUtils.getPublicKey(certificateSigningRequest);
        final X509CertificateHolder holder = this.constructX509CertificateHolder(notBefore, notAfter, subject, issuer, clientPublicKey);
        final X509Certificate issuedCertificate = new JcaX509CertificateConverter().getCertificate(holder);
        this.logger.log(Level.INFO, "======================= SUCCESSFULLY CREATED CLIENT CERTIFICATE FOR SUBJECT: {0} =====================", new Object[] { issuedCertificate.getSubjectDN() });
        return issuedCertificate;
    }
    
    private X509CertificateHolder constructX509CertificateHolder(final Date notBefore, final Date notAfter, final X500Name subject, final X500Name issuer, final PublicKey clientPublicKey) throws Exception {
        this.logger.log(Level.INFO, "SigningCertificateAuthority: Constructing client certificate for: {0}", new Object[] { subject });
        final JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuer, CertificateUtil.getRandomSerialNumber(), notBefore, notAfter, subject, clientPublicKey);
        CertificateUtil.addTypicalClientKeyUsages(builder);
        CertificateUtil.addClientAuthExtendedKeyUsage(builder);
        CertificateUtil.addSubjectKeyIdentifier(builder, clientPublicKey);
        CertificateUtil.addAuthorityKeyIdentifier(builder, this.caCertificate.getPublicKey());
        final ContentSigner signer = new JcaContentSignerBuilder("SHA512withRSA").build(this.privateKey);
        final X509CertificateHolder holder = builder.build(signer);
        this.logger.log(Level.INFO, "SigningCertificateAuthority: Client certificate successfully constructed for : {0}", new Object[] { subject });
        return holder;
    }
}
