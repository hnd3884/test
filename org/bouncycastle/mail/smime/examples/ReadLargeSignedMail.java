package org.bouncycastle.mail.smime.examples;

import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import java.io.InputStream;
import javax.mail.internet.MimeMessage;
import org.bouncycastle.mail.smime.util.SharedFileInputStream;
import javax.mail.Authenticator;
import javax.mail.Session;
import java.util.Iterator;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.Selector;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.mail.smime.SMIMESignedParser;

public class ReadLargeSignedMail
{
    private static final String BC = "BC";
    
    private static void verify(final SMIMESignedParser smimeSignedParser) throws Exception {
        final Store certificates = smimeSignedParser.getCertificates();
        for (final SignerInformation signerInformation : smimeSignedParser.getSignerInfos().getSigners()) {
            if (signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(new JcaX509CertificateConverter().setProvider("BC").getCertificate((X509CertificateHolder)certificates.getMatches((Selector)signerInformation.getSID()).iterator().next())))) {
                System.out.println("signature verified");
            }
            else {
                System.out.println("signature failed!");
            }
        }
    }
    
    public static void main(final String[] array) throws Exception {
        final MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(System.getProperties(), (Authenticator)null), (InputStream)new SharedFileInputStream("signed.message"));
        if (mimeMessage.isMimeType("multipart/signed")) {
            final SMIMESignedParser smimeSignedParser = new SMIMESignedParser(new JcaDigestCalculatorProviderBuilder().build(), (MimeMultipart)mimeMessage.getContent());
            System.out.println("Status:");
            verify(smimeSignedParser);
        }
        else if (mimeMessage.isMimeType("application/pkcs7-mime")) {
            final SMIMESignedParser smimeSignedParser2 = new SMIMESignedParser(new JcaDigestCalculatorProviderBuilder().build(), (Part)mimeMessage);
            System.out.println("Status:");
            verify(smimeSignedParser2);
        }
        else {
            System.err.println("Not a signed message!");
        }
    }
}
