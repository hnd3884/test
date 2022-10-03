package org.bouncycastle.mail.smime.examples;

import javax.mail.internet.MimeBodyPart;
import javax.mail.Part;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import java.io.InputStream;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import javax.mail.Authenticator;
import javax.mail.Session;
import java.util.Iterator;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.Selector;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.mail.smime.SMIMESigned;

public class ReadSignedMail
{
    private static final String BC = "BC";
    
    private static void verify(final SMIMESigned smimeSigned) throws Exception {
        final Store certificates = smimeSigned.getCertificates();
        for (final SignerInformation signerInformation : smimeSigned.getSignerInfos().getSigners()) {
            if (signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(new JcaX509CertificateConverter().setProvider("BC").getCertificate((X509CertificateHolder)certificates.getMatches((Selector)signerInformation.getSID()).iterator().next())))) {
                System.out.println("signature verified");
            }
            else {
                System.out.println("signature failed!");
            }
        }
    }
    
    public static void main(final String[] array) throws Exception {
        final MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(System.getProperties(), (Authenticator)null), (InputStream)new FileInputStream("signed.message"));
        if (mimeMessage.isMimeType("multipart/signed")) {
            final SMIMESigned smimeSigned = new SMIMESigned((MimeMultipart)mimeMessage.getContent());
            final MimeBodyPart content = smimeSigned.getContent();
            System.out.println("Content:");
            final Object content2 = content.getContent();
            if (content2 instanceof String) {
                System.out.println((String)content2);
            }
            else if (content2 instanceof Multipart) {
                final Multipart multipart = (Multipart)content2;
                for (int count = multipart.getCount(), i = 0; i < count; ++i) {
                    final Object content3 = multipart.getBodyPart(i).getContent();
                    System.out.println("Part " + i);
                    System.out.println("---------------------------");
                    if (content3 instanceof String) {
                        System.out.println((String)content3);
                    }
                    else {
                        System.out.println("can't print...");
                    }
                }
            }
            System.out.println("Status:");
            verify(smimeSigned);
        }
        else if (mimeMessage.isMimeType("application/pkcs7-mime") || mimeMessage.isMimeType("application/x-pkcs7-mime")) {
            final SMIMESigned smimeSigned2 = new SMIMESigned((Part)mimeMessage);
            final MimeBodyPart content4 = smimeSigned2.getContent();
            System.out.println("Content:");
            final Object content5 = content4.getContent();
            if (content5 instanceof String) {
                System.out.println((String)content5);
            }
            System.out.println("Status:");
            verify(smimeSigned2);
        }
        else {
            System.err.println("Not a signed message!");
        }
    }
}
