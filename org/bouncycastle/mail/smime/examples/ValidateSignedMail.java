package org.bouncycastle.mail.smime.examples;

import org.bouncycastle.asn1.ASN1Encodable;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import javax.security.auth.x500.X500Principal;
import java.security.cert.CertificateFactory;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.bouncycastle.asn1.x509.Extension;
import java.security.cert.Certificate;
import org.bouncycastle.x509.PKIXCertPathReviewer;
import java.util.Iterator;
import java.security.cert.X509Certificate;
import org.bouncycastle.i18n.ErrorBundle;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.mail.smime.validator.SignedMailValidator;
import java.util.Locale;
import java.security.cert.X509CRL;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStore;
import java.util.Collection;
import java.security.cert.CollectionCertStoreParameters;
import java.util.ArrayList;
import java.security.cert.TrustAnchor;
import java.util.Set;
import java.security.cert.PKIXParameters;
import java.util.HashSet;
import java.io.InputStream;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import javax.mail.Authenticator;
import javax.mail.Session;
import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ValidateSignedMail
{
    public static final boolean useCaCerts = false;
    public static final int TITLE = 0;
    public static final int TEXT = 1;
    public static final int SUMMARY = 2;
    public static final int DETAIL = 3;
    static int dbgLvl;
    private static final String RESOURCE_NAME = "org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages";
    
    public static void main(final String[] array) throws Exception {
        Security.addProvider((Provider)new BouncyCastleProvider());
        final MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(System.getProperties(), (Authenticator)null), (InputStream)new FileInputStream("signed.message"));
        final HashSet set = new HashSet();
        TrustAnchor trustAnchor = getTrustAnchor("trustanchor");
        if (trustAnchor == null) {
            System.out.println("no trustanchor file found, using a dummy trustanchor");
            trustAnchor = getDummyTrustAnchor();
        }
        set.add(trustAnchor);
        final PKIXParameters pkixParameters = new PKIXParameters(set);
        final ArrayList list = new ArrayList();
        final X509CRL loadCRL = loadCRL("crl.file");
        if (loadCRL != null) {
            list.add(loadCRL);
        }
        pkixParameters.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(list), "BC"));
        pkixParameters.setRevocationEnabled(true);
        verifySignedMail(mimeMessage, pkixParameters);
    }
    
    public static void verifySignedMail(final MimeMessage mimeMessage, final PKIXParameters pkixParameters) throws Exception {
        final Locale english = Locale.ENGLISH;
        final SignedMailValidator signedMailValidator = new SignedMailValidator(mimeMessage, pkixParameters);
        final Iterator iterator = signedMailValidator.getSignerInformationStore().getSigners().iterator();
        while (iterator.hasNext()) {
            final SignedMailValidator.ValidationResult validationResult = signedMailValidator.getValidationResult((SignerInformation)iterator.next());
            if (validationResult.isValidSignature()) {
                System.out.println(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.sigValid").getText(english));
            }
            else {
                System.out.println(new ErrorBundle("org.bouncycastle.mail.smime.validator.SignedMailValidatorMessages", "SignedMailValidator.sigInvalid").getText(english));
                System.out.println("Errors:");
                for (final ErrorBundle errorBundle : validationResult.getErrors()) {
                    if (ValidateSignedMail.dbgLvl == 3) {
                        System.out.println("\t\t" + errorBundle.getDetail(english));
                    }
                    else {
                        System.out.println("\t\t" + errorBundle.getText(english));
                    }
                }
            }
            if (!validationResult.getNotifications().isEmpty()) {
                System.out.println("Notifications:");
                for (final ErrorBundle errorBundle2 : validationResult.getNotifications()) {
                    if (ValidateSignedMail.dbgLvl == 3) {
                        System.out.println("\t\t" + errorBundle2.getDetail(english));
                    }
                    else {
                        System.out.println("\t\t" + errorBundle2.getText(english));
                    }
                }
            }
            final PKIXCertPathReviewer certPathReview = validationResult.getCertPathReview();
            if (certPathReview != null) {
                if (certPathReview.isValidCertPath()) {
                    System.out.println("Certificate path valid");
                }
                else {
                    System.out.println("Certificate path invalid");
                }
                System.out.println("\nCertificate path validation results:");
                System.out.println("Errors:");
                for (final ErrorBundle errorBundle3 : certPathReview.getErrors(-1)) {
                    if (ValidateSignedMail.dbgLvl == 3) {
                        System.out.println("\t\t" + errorBundle3.getDetail(english));
                    }
                    else {
                        System.out.println("\t\t" + errorBundle3.getText(english));
                    }
                }
                System.out.println("Notifications:");
                final Iterator iterator5 = certPathReview.getNotifications(-1).iterator();
                while (iterator5.hasNext()) {
                    System.out.println("\t" + ((ErrorBundle)iterator5.next()).getText(english));
                }
                final Iterator<? extends Certificate> iterator6 = certPathReview.getCertPath().getCertificates().iterator();
                int n = 0;
                while (iterator6.hasNext()) {
                    final X509Certificate x509Certificate = (X509Certificate)iterator6.next();
                    System.out.println("\nCertificate " + n + "\n========");
                    System.out.println("Issuer: " + x509Certificate.getIssuerDN().getName());
                    System.out.println("Subject: " + x509Certificate.getSubjectDN().getName());
                    System.out.println("\tErrors:");
                    for (final ErrorBundle errorBundle4 : certPathReview.getErrors(n)) {
                        if (ValidateSignedMail.dbgLvl == 3) {
                            System.out.println("\t\t" + errorBundle4.getDetail(english));
                        }
                        else {
                            System.out.println("\t\t" + errorBundle4.getText(english));
                        }
                    }
                    System.out.println("\tNotifications:");
                    for (final ErrorBundle errorBundle5 : certPathReview.getNotifications(n)) {
                        if (ValidateSignedMail.dbgLvl == 3) {
                            System.out.println("\t\t" + errorBundle5.getDetail(english));
                        }
                        else {
                            System.out.println("\t\t" + errorBundle5.getText(english));
                        }
                    }
                    ++n;
                }
            }
        }
    }
    
    protected static TrustAnchor getTrustAnchor(final String s) throws Exception {
        final X509Certificate loadCert = loadCert(s);
        if (loadCert == null) {
            return null;
        }
        final byte[] extensionValue = loadCert.getExtensionValue(Extension.nameConstraints.getId());
        if (extensionValue != null) {
            return new TrustAnchor(loadCert, ((ASN1Encodable)X509ExtensionUtil.fromExtensionValue(extensionValue)).toASN1Primitive().getEncoded("DER"));
        }
        return new TrustAnchor(loadCert, null);
    }
    
    protected static X509Certificate loadCert(final String s) {
        X509Certificate x509Certificate = null;
        try {
            x509Certificate = (X509Certificate)CertificateFactory.getInstance("X.509", "BC").generateCertificate(new FileInputStream(s));
        }
        catch (final Exception ex) {
            System.out.println("certfile \"" + s + "\" not found - classpath is " + System.getProperty("java.class.path"));
        }
        return x509Certificate;
    }
    
    protected static X509CRL loadCRL(final String s) {
        X509CRL x509CRL = null;
        try {
            x509CRL = (X509CRL)CertificateFactory.getInstance("X.509", "BC").generateCRL(new FileInputStream(s));
        }
        catch (final Exception ex) {
            System.out.println("crlfile \"" + s + "\" not found - classpath is " + System.getProperty("java.class.path"));
        }
        return x509CRL;
    }
    
    private static TrustAnchor getDummyTrustAnchor() throws Exception {
        final X500Principal x500Principal = new X500Principal("CN=Dummy Trust Anchor");
        final KeyPairGenerator instance = KeyPairGenerator.getInstance("RSA", "BC");
        instance.initialize(1024, new SecureRandom());
        return new TrustAnchor(x500Principal, instance.generateKeyPair().getPublic(), null);
    }
    
    static {
        ValidateSignedMail.dbgLvl = 3;
    }
}
